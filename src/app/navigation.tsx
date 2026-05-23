import { View, Text, StyleSheet, Platform } from 'react-native'
import { NavigationContainer } from '@react-navigation/native'
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { CourseListScreen } from '@screens/CourseListScreen'
import { DownloadScreen } from '@screens/DownloadScreen'
import { LessonScreen } from '@screens/LessonScreen'
import { StatsScreen } from '@screens/StatsScreen'
import { SettingsScreen } from '@screens/SettingsScreen'
import { color, border, typography } from '@shared/theme'

export type CourseStackParamList = {
  CourseList: undefined
  Download: { lessonId: string; lessonTitle: string }
  Lesson: { lessonId: string }
}

export type TabParamList = {
  CourseTab: undefined
  StatsTab: undefined
  SettingsTab: undefined
}

const CourseStack = createNativeStackNavigator<CourseStackParamList>()

function CourseStackNavigator() {
  return (
    <CourseStack.Navigator screenOptions={{ headerShown: false }}>
      <CourseStack.Screen name="CourseList" component={CourseListScreen} />
      <CourseStack.Screen name="Download" component={DownloadScreen} />
      <CourseStack.Screen name="Lesson" component={LessonScreen} />
    </CourseStack.Navigator>
  )
}

const Tab = createBottomTabNavigator<TabParamList>()

function TabBarLabel({ label, focused }: { label: string; focused: boolean }) {
  return (
    <Text style={[styles.tabLabel, focused && styles.tabLabelActive]}>
      {label.toUpperCase()}
    </Text>
  )
}

function TabIcon({ icon, focused }: { icon: string; focused: boolean }) {
  return (
    <Text style={[styles.tabIcon, focused && styles.tabIconActive]}>
      {icon}
    </Text>
  )
}

export function AppNavigator() {
  const insets = useSafeAreaInsets()

  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={{
          headerShown: false,
          tabBarStyle: [
            styles.tabBar,
            { height: 60 + insets.bottom, paddingBottom: insets.bottom },
          ],
          tabBarShowLabel: true,
        }}
      >
        <Tab.Screen
          name="CourseTab"
          component={CourseStackNavigator}
          options={{
            tabBarLabel: ({ focused }) => (
              <TabBarLabel label="课程" focused={focused} />
            ),
            tabBarIcon: ({ focused }) => (
              <TabIcon icon="≡" focused={focused} />
            ),
          }}
        />
        <Tab.Screen
          name="StatsTab"
          component={StatsScreen}
          options={{
            tabBarLabel: ({ focused }) => (
              <TabBarLabel label="统计" focused={focused} />
            ),
            tabBarIcon: ({ focused }) => (
              <TabIcon icon="⬡" focused={focused} />
            ),
          }}
        />
        <Tab.Screen
          name="SettingsTab"
          component={SettingsScreen}
          options={{
            tabBarLabel: ({ focused }) => (
              <TabBarLabel label="设置" focused={focused} />
            ),
            tabBarIcon: ({ focused }) => (
              <TabIcon icon="⚙" focused={focused} />
            ),
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  )
}

const styles = StyleSheet.create({
  tabBar: {
    backgroundColor: color.bg,
    borderTopWidth: border.widthThick,
    borderTopColor: color.border,
    borderTopLeftRadius: 0,
    borderTopRightRadius: 0,
    elevation: 0,
    shadowOpacity: 0,
    ...Platform.select({
      android: { elevation: 0 },
    }),
  },
  tabLabel: {
    ...typography.label,
    fontSize: 11,
    color: color.fg,
    opacity: 0.4,
    marginTop: -4,
  },
  tabLabelActive: {
    opacity: 1,
    color: color.fg,
  },
  tabIcon: {
    fontSize: 22,
    color: color.fg,
    opacity: 0.4,
    marginBottom: -2,
  },
  tabIconActive: {
    opacity: 1,
  },
})