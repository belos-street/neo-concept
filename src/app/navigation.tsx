import { NavigationContainer } from '@react-navigation/native'
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { CourseListScreen } from '@screens/CourseListScreen'
import { DownloadScreen } from '@screens/DownloadScreen'
import { LessonScreen } from '@screens/LessonScreen'
import { StatsScreen } from '@screens/StatsScreen'
import { SettingsScreen } from '@screens/SettingsScreen'

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
    <CourseStack.Navigator>
      <CourseStack.Screen
        name="CourseList"
        component={CourseListScreen}
        options={{ title: '课程' }}
      />
      <CourseStack.Screen
        name="Download"
        component={DownloadScreen}
        options={{ title: '下载' }}
      />
      <CourseStack.Screen
        name="Lesson"
        component={LessonScreen}
        options={{ headerShown: false }}
      />
    </CourseStack.Navigator>
  )
}

const Tab = createBottomTabNavigator<TabParamList>()

export function AppNavigator() {
  return (
    <NavigationContainer>
      <Tab.Navigator screenOptions={{ headerShown: false }}>
        <Tab.Screen
          name="CourseTab"
          component={CourseStackNavigator}
          options={{ tabBarLabel: '课程' }}
        />
        <Tab.Screen
          name="StatsTab"
          component={StatsScreen}
          options={{ tabBarLabel: '统计' }}
        />
        <Tab.Screen
          name="SettingsTab"
          component={SettingsScreen}
          options={{ tabBarLabel: '设置' }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  )
}
