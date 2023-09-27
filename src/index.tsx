const ReactNativeBrotherPrinters =
  require('./NativeReactNativeBrotherPrinters').default;

export type PrintersModels = 'QL-810W';

export type SearchPrintersInNetwork = {
  channelInfo: string;
  modelName: PrintersModels;
  macAddress: string;
  serialNumber: string;
  nodeName: string;
};

type SearchPrintersInNetworkError = {
  message: string;
};

export async function searchPrintersInNetwork(): Promise<
  Array<SearchPrintersInNetwork>
> {
  try {
    const result = await ReactNativeBrotherPrinters.searchPrintersInNetwork();

    return JSON.parse(result);
  } catch (err) {
    const error = err as SearchPrintersInNetworkError;
    if (error && error.message === 'device is not connected to a network') {
      throw new Error('device is not connected to a network');
    } else {
      throw new Error('unknown error');
    }
  }
}
