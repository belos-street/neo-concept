import { AppProviders } from './src/app/providers'
import { AppNavigator } from './src/app/navigation'

export default function App() {
  return (
    <AppProviders>
      <AppNavigator />
    </AppProviders>
  )
}
