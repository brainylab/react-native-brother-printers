import type {PrintersModels} from './types/models';

const ReactNativeBrotherPrinters =
  require('./NativeReactNativeBrotherPrinters').default;

export type SearchPrintersInNetwork = {
  ip_address: string;
  model_name: PrintersModels;
  mac_address: string;
  serial_number: string;
  node_name: string;
};

type SearchPrintersInNetworkError = {
  message: string;
};

const printerModels = {
  'QL-810W': 'QL_810W',
};

export async function searchPrintersInNetwork(): Promise<
  Array<SearchPrintersInNetwork>
> {
  try {
    const result = await ReactNativeBrotherPrinters.searchPrintersInNetwork();

    const value = JSON.parse(result) as Array<any>;

    const newValue = value.map(item => ({
      ...item,
      model_name: printerModels[item.model_name as keyof typeof printerModels],
    }));

    return newValue;
  } catch (err) {
    const error = err as SearchPrintersInNetworkError;
    if (error && error.message === 'device is not connected to a network') {
      throw new Error('device is not connected to a network');
    } else {
      throw new Error('unknown error');
    }
  }
}
