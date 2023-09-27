package com.brainylab.reactnativebrotherprinters

import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.bridge.Promise

import com.brother.sdk.lmprinter.NetworkSearchOption
import com.brother.sdk.lmprinter.Channel
import com.brother.sdk.lmprinter.PrinterSearcher
import com.brother.sdk.lmprinter.PrinterSearchError

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

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  // override fun multiply(a: Double, b: Double): Double {
  //   return a * b
  // }

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
                jsonObject.put("channelInfo", channel.channelInfo)
                jsonObject.put("modelName", modelName ?: "")
                jsonObject.put("macAddress", macAddress ?: "")
                jsonObject.put("serialNumber", serialNumber ?: "")
                jsonObject.put("nodeName", nodeName ?: "")
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
