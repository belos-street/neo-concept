import { StyleSheet, TextStyle, ViewStyle } from 'react-native'

export const color = {
  bg: '#FFFFFF',
  fg: '#000000',
  muted: '#F2F2F2',
  accent: '#FF3000',
  border: '#000000',
  success: '#000000',
  error: '#FF3000'
} as const

export const space = {
  1: 4,
  2: 8,
  3: 12,
  4: 16,
  5: 20,
  6: 24,
  8: 32,
  10: 40
} as const

export const border = {
  width: 2,
  widthThick: 3,
  radius: 0
} as const

export const typography = StyleSheet.create({
  titleLg: {
    fontSize: 26,
    fontWeight: '700',
    letterSpacing: 0.5,
    color: color.fg,
    textTransform: 'uppercase'
  } as TextStyle,
  title: {
    fontSize: 20,
    fontWeight: '700',
    letterSpacing: 0.5,
    color: color.fg,
    textTransform: 'uppercase'
  } as TextStyle,
  titleSm: {
    fontSize: 16,
    fontWeight: '700',
    letterSpacing: 0.5,
    color: color.fg,
    textTransform: 'uppercase'
  } as TextStyle,
  labelLg: {
    fontSize: 14,
    fontWeight: '700',
    letterSpacing: 1,
    color: color.fg,
    textTransform: 'uppercase'
  } as TextStyle,
  label: {
    fontSize: 12,
    fontWeight: '700',
    letterSpacing: 1.5,
    color: color.fg,
    textTransform: 'uppercase'
  } as TextStyle,
  body: {
    fontSize: 16,
    fontWeight: '400',
    letterSpacing: 0,
    color: color.fg
  } as TextStyle,
  bodySm: {
    fontSize: 14,
    fontWeight: '400',
    letterSpacing: 0,
    color: color.fg
  } as TextStyle,
  caption: {
    fontSize: 12,
    fontWeight: '500',
    letterSpacing: 0.5,
    color: color.fg
  } as TextStyle
})

export const layout = StyleSheet.create({
  screenPadding: {
    paddingHorizontal: space[4]
  } as ViewStyle,
  screenScroll: {
    flex: 1,
    backgroundColor: color.bg
  } as ViewStyle
})

export const touch = {
  minHeight: 44,
  minWidth: 44
} as const
