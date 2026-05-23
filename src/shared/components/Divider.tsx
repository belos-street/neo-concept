import { View, StyleSheet } from 'react-native'
import { color, space } from '@shared/theme'

export function Divider() {
  return <View style={styles.divider} />
}

const styles = StyleSheet.create({
  divider: {
    height: 2,
    backgroundColor: color.border,
    marginVertical: space[6],
  },
})