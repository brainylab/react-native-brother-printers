import React, {useCallback, useState} from 'react';
import {StyleSheet, View, Text, TouchableOpacity} from 'react-native';

import {
  searchPrintersInNetwork,
  printWithTemplate,
} from '@brainylab/react-native-brother-printers';
import type {SearchPrintersInNetwork} from '@brainylab/react-native-brother-printers';

export default function App() {
  const [loading, setLoading] = useState(false);
  const [printers, setPrinters] = useState<SearchPrintersInNetwork[]>(
    [] as SearchPrintersInNetwork[],
  );

  const handleSearchPrintersNetwork = useCallback(async () => {
    setLoading(true);
    const printersInNetwork = await searchPrintersInNetwork();

    console.log(printersInNetwork);

    setPrinters(printersInNetwork);
    setLoading(false);
  }, []);

  const handlePrintWithTemplate = useCallback(async () => {
    if (printers[0]) {
      console.log(printers[0].ip_address);

      const prints = [
        {
          name: '2356 - JOHN DOE LTDA',
          order_date: '28/09/2023',
          order_number: '123456',
          date_time: '28/09/2023 19:04:23',
          box: '1',
          total_boxes: '4',
        },
        {
          name: '2356 - JOHN DOE LTDA',
          order_date: '28/09/2023',
          order_number: '123456',
          date_time: '28/09/2023 19:04:23',
          box: '2',
          total_boxes: '4',
        },
      ];

      await Promise.all(
        prints.map(
          async print =>
            await printWithTemplate({
              template: '1',
              ip_address: printers[0]!.ip_address,
              model: printers[0]!.model_name,
              replaces: {...print},
            }),
        ),
      );
    }
  }, [printers]);

  return (
    <View style={styles.container}>
      {loading ? (
        <Text>Loading Printers in Network</Text>
      ) : (
        printers.map(printer => (
          <Text key={printer.model_name}>
            {printer.model_name} & {printer.ip_address}
          </Text>
        ))
      )}

      <TouchableOpacity
        style={styles.button}
        onPress={handleSearchPrintersNetwork}>
        <Text>Request Printer</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.button}
        onPress={handlePrintWithTemplate}
        // disabled={!printers[0]}
      >
        <Text>Print With Template</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'white',
  },
  button: {
    marginTop: 40,
    backgroundColor: 'green',
    padding: 10,
  },
});
