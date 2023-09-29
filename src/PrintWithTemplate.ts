import type {PrintersModels} from './types/models';

const ReactNativeBrotherPrinters =
  require('./NativeReactNativeBrotherPrinters').default;

type PrintWithTemplate = {
  template: string;
  model: PrintersModels;
  ip_address: string;
  replaces: Record<string, string>;
};

export async function printWithTemplate(
  params: PrintWithTemplate,
): Promise<void> {
  try {
    const value = await ReactNativeBrotherPrinters.printWithTemplate(params);

    console.log(value);
  } catch (err) {
    console.error(err);
  }
}
