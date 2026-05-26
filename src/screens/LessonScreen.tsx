import { useState, useCallback, useEffect, useRef } from 'react'
import { View, StyleSheet } from 'react-native'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import type { NativeStackScreenProps } from '@react-navigation/native-stack'
import type { CourseStackParamList } from '@app/navigation'
import { ScreenHeader } from '@shared/components'
import { color } from '@shared/theme'
import { StepProgressBar } from '@features/lesson/StepProgressBar'
import { PassageStep } from '@features/lesson/PassageStep'
import { FillBlanksStep } from '@features/lesson/FillBlanksStep'
import { VocabExerciseStep } from '@features/lesson/VocabExerciseStep'
import { ListeningStep } from '@features/lesson/ListeningStep'
import { ReadingStep } from '@features/lesson/ReadingStep'
import { SpeakingStep } from '@features/lesson/SpeakingStep'
import { ResumeOverlay } from '@features/lesson/ResumeOverlay'
import { ExitModal } from '@features/lesson/ExitModal'
import { useProgressStore } from '@features/lesson/store'
import { mockLesson } from '@features/lesson/mockData'
import type { Lesson } from '@shared/types'

type Props = NativeStackScreenProps<CourseStackParamList, 'Lesson'>

const TOTAL_STEPS = 6
const STEP_LABELS = ['课文', '填空', '词汇', '听力', '阅读', '口语']

export function LessonScreen({ navigation, route }: Props) {
  const insets = useSafeAreaInsets()
  const { lessonId } = route.params

  const lesson: Lesson = mockLesson

  const snapshot = useProgressStore((s) => s.snapshot)
  const startLesson = useProgressStore((s) => s.startLesson)
  const completeStep = useProgressStore((s) => s.completeStep)
  const goToStep = useProgressStore((s) => s.goToStep)
  const finishLesson = useProgressStore((s) => s.finishLesson)
  const resetLesson = useProgressStore((s) => s.resetLesson)
  const undoCompleteStep = useProgressStore((s) => s.undoCompleteStep)

  const progress = snapshot[lessonId]
  const [showResume, setShowResume] = useState(false)
  const [showExit, setShowExit] = useState(false)
  const [initialized, setInitialized] = useState(false)
  const leavingRef = useRef(false)

  useEffect(() => {
    if (initialized) return
    const existing = snapshot[lessonId]
    if (existing && !existing.finished && existing.completedSteps.length > 0) {
      setShowResume(true)
    } else {
      startLesson(lessonId, TOTAL_STEPS)
    }
    setInitialized(true)
  }, [initialized, lessonId, snapshot, startLesson])

  useEffect(() => {
    const unsubscribe = navigation.addListener('beforeRemove', (e) => {
      if (leavingRef.current) return
      if (!progress || progress.finished) return
      e.preventDefault()
      setShowExit(true)
    })
    return unsubscribe
  }, [navigation, progress])

  const currentStep = progress?.currentStep ?? 0
  const completedSteps = progress?.completedSteps ?? []

  const isStepAccessible = useCallback(
    (stepIndex: number) => {
      if (stepIndex === currentStep) return false
      if (completedSteps.includes(stepIndex)) return true
      if (
        stepIndex === currentStep + 1 &&
        completedSteps.includes(currentStep)
      ) {
        return true
      }
      return false
    },
    [currentStep, completedSteps]
  )

  const handleStepPress = useCallback(
    (stepIndex: number) => {
      if (!isStepAccessible(stepIndex)) return
      goToStep(lessonId, stepIndex)
    },
    [isStepAccessible, goToStep, lessonId]
  )

  const handleStepComplete = useCallback(
    (stepIndex: number) => {
      completeStep(lessonId, stepIndex)
      if (stepIndex >= TOTAL_STEPS - 1) {
        finishLesson(lessonId)
        leavingRef.current = true
        navigation.goBack()
      } else if (stepIndex + 1 < TOTAL_STEPS) {
        goToStep(lessonId, stepIndex + 1)
      }
    },
    [lessonId, completeStep, goToStep, finishLesson, navigation]
  )

  const handleRetry = useCallback(
    (stepIndex: number) => {
      undoCompleteStep(lessonId, stepIndex)
      goToStep(lessonId, stepIndex)
    },
    [lessonId, undoCompleteStep, goToStep]
  )

  const handleResume = useCallback(() => {
    setShowResume(false)
    if (progress && !progress.finished) {
      goToStep(lessonId, progress.currentStep)
    } else {
      startLesson(lessonId, TOTAL_STEPS)
    }
  }, [progress, lessonId, goToStep, startLesson])

  const handleRestart = useCallback(() => {
    setShowResume(false)
    resetLesson(lessonId)
    startLesson(lessonId, TOTAL_STEPS)
  }, [lessonId, resetLesson, startLesson])

  const handleExit = useCallback(() => {
    setShowExit(false)
    leavingRef.current = true
    navigation.goBack()
  }, [navigation])

  const renderStep = () => {
    switch (currentStep) {
      case 0:
        return (
          <PassageStep
            lesson={lesson}
            onComplete={() => handleStepComplete(0)}
          />
        )
      case 1:
        return (
          <FillBlanksStep
            lesson={lesson}
            onComplete={() => handleStepComplete(1)}
          />
        )
      case 2:
        return (
          <VocabExerciseStep
            lesson={lesson}
            onComplete={() => handleStepComplete(2)}
          />
        )
      case 3:
        return (
          <ListeningStep
            lesson={lesson}
            onComplete={() => handleStepComplete(3)}
            onRetry={() => handleRetry(3)}
          />
        )
      case 4:
        return (
          <ReadingStep
            lesson={lesson}
            onComplete={() => handleStepComplete(4)}
            onRetry={() => handleRetry(4)}
          />
        )
      case 5:
        return (
          <SpeakingStep
            lesson={lesson}
            onComplete={() => handleStepComplete(5)}
            onRetry={() => handleRetry(5)}
          />
        )
      default:
        return null
    }
  }

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <ScreenHeader
        title={lesson.title}
        onBack={() => setShowExit(true)}
      />

      <StepProgressBar
        current={currentStep}
        total={TOTAL_STEPS}
        completed={completedSteps}
        labels={STEP_LABELS}
        onStepPress={handleStepPress}
      />

      <View style={styles.stepContainer}>{renderStep()}</View>

      <ResumeOverlay
        visible={showResume}
        completedSteps={completedSteps.length}
        totalSteps={TOTAL_STEPS}
        lastAccessedAt={progress?.lastAccessedAt ?? ''}
        onResume={handleResume}
        onRestart={handleRestart}
      />

      <ExitModal
        visible={showExit}
        onConfirm={handleExit}
        onCancel={() => setShowExit(false)}
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: color.bg
  },
  stepContainer: {
    flex: 1
  }
})
