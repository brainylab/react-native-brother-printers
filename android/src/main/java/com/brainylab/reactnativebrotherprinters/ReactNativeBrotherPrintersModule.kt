package com.brainylab.reactnativebrotherprinters

import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

import com.brother.sdk.lmprinter.NetworkSearchOption
import com.brother.sdk.lmprinter.Channel
import com.brother.sdk.lmprinter.PrinterModel
import com.brother.sdk.lmprinter.PrinterSearcher
import com.brother.sdk.lmprinter.PrinterSearchError

import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.brother.ptouch.sdk.TemplateInfo;

import org.json.JSONArray
import org.json.JSONObject

@ReactModule(name = ReactNativeBrotherPrintersModule.NAME)
class ReactNativeBrotherPrintersModule(reactContext: ReactApplicationContext) :
  NativeReactNativeBrotherPrintersSpec(reactContext) {
  private val SEARCH_TIME: Double = 15.0 // sec

  companion object {
    const val NAME = "ReactNativeBrotherPrinters"
  }

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  override fun printWithTemplate(params: ReadableMap, promise: Promise) {
    val model = params.getString("model")
    val ipAddress = params.getString("ip_address")
    val template = params.getString("template")?.toInt() ?: 1
    val replacesObject = params.getMap("replaces")

    val modelPrinter = model?.let { PrinterInfo.Model.valueOf(it) }

    if (modelPrinter != null) {
      val printer = Printer()
      val settings = printer.printerInfo

      settings.printerModel = modelPrinter
      settings.ipAddress = ipAddress
      settings.port = PrinterInfo.Port.NET;

      printer.setPrinterInfo(settings);

      if (printer.startCommunication()) {
            printer.startPTTPrint(template, null)

            for ((key, value) in replacesObject?.toHashMap() ?: emptyMap()) {
              printer.replaceTextName(value as String, key)
            }

            val result = printer.flushPTTPrint()
            if (result.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
              promise.reject("ERROR -" + result.errorCode)
            }

            printer.endCommunication()

            promise.resolve("success send print to $model")
      } else {
        promise.reject("$model not connected")
      }
    } else {
      promise.reject("$model does not compatible")
    }
  }

  @ReactMethod
  override fun searchPrintersInNetwork(promise: Promise) {
    val connectivityManager = reactApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)

    if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {

      var mPrinterList: List<Channel> = ArrayList()

      val activity = reactApplicationContext.currentActivity

      val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
      val option = NetworkSearchOption(SEARCH_TIME, java.lang.Boolean.parseBoolean(sharedPreferences
                      .getString("enabledTethering", "false")))

      val result = PrinterSearcher.startNetworkSearch(activity, option, { channel: Channel ->
          mPrinterList = mPrinterList.plus(channel)
      })

      if (result.error.code == PrinterSearchError.ErrorCode.NoError || result.error.code == PrinterSearchError.ErrorCode.Canceled) {
          val printerArray = JSONArray()

          if (mPrinterList.isNotEmpty()) {
              mPrinterList.forEach { channel ->
                val modelName = channel.extraInfo[Channel.ExtraInfoKey.ModelName]
                val macAddress = channel.extraInfo[Channel.ExtraInfoKey.MACAddress]
                val serialNumber = channel.extraInfo[Channel.ExtraInfoKey.SerialNubmer]
                val nodeName = channel.extraInfo[Channel.ExtraInfoKey.NodeName]
                val jsonObject = JSONObject()
                jsonObject.put("ip_address", channel.channelInfo)
                jsonObject.put("model_name", modelName ?: "")
                jsonObject.put("mac_address", macAddress ?: "")
                jsonObject.put("serial_number", serialNumber ?: "")
                jsonObject.put("node_name", nodeName ?: "")
                printerArray.put(jsonObject)
            }

            promise.resolve(printerArray.toString())
        } else {
          promise.resolve(printerArray.toString())
        }
      } else {
        promise.reject("error finding printers")
      }
    } else {
      promise.reject("device is not connected to a network")
    }
  }
}
