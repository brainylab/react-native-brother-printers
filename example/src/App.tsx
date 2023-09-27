import React, { useCallback, useState } from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { searchPrintersInNetwork } from '@brainylab/react-native-brother-printers';
import type { SearchPrintersInNetwork } from '@brainylab/react-native-brother-printers';

export default function App() {
  const [loading, setLoading] = useState(false);
  const [printers, setPrinters] = useState<SearchPrintersInNetwork[]>(
    [] as SearchPrintersInNetwork[]
  );

  const handleSearchPrintersNetwork = useCallback(async () => {
    setLoading(true);
    const printersInNetwork = await searchPrintersInNetwork();

    setPrinters(printersInNetwork);
    setLoading(false);
  }, []);

  return (
    <View style={styles.container}>
      {loading ? (
        <Text>Loading Printers in Network</Text>
      ) : (
        printers.map((printer) => (
          <Text key={printer.modelName}>{printer.modelName}</Text>
        ))
      )}

      <TouchableOpacity
        style={styles.button}
        onPress={handleSearchPrintersNetwork}
      >
        <Text>Request Permission</Text>
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
