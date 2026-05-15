module.exports = {
  presets: ['module:@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        root: ['./src'],
        alias: {
          '@': './src',
          '@shared': './src/shared',
          '@features': './src/features',
          '@screens': './src/screens',
          '@native': './src/native'
        }
      }
    ]
  ]
}
