import { create } from 'zustand'
import { mmkvStorage } from '@shared/hooks/useMMKV'

const PROGRESS_KEY = 'lesson_progress'

export interface LessonProgress {
  lessonId: string
  currentStep: number
  totalSteps: number
  completedSteps: number[]
  lastAccessedAt: string
  finished: boolean
}

export interface ProgressSnapshot {
  [lessonId: string]: LessonProgress
}

function loadProgress(): ProgressSnapshot {
  const raw = mmkvStorage.getString(PROGRESS_KEY)
  if (!raw) return {}
  try {
    return JSON.parse(raw)
  } catch {
    return {}
  }
}

function saveProgress(snapshot: ProgressSnapshot) {
  mmkvStorage.set(PROGRESS_KEY, JSON.stringify(snapshot))
}

export const useProgressStore = create<{
  snapshot: ProgressSnapshot
  getProgress: (lessonId: string) => LessonProgress | null
  startLesson: (lessonId: string, totalSteps: number) => void
  completeStep: (lessonId: string, stepIndex: number) => void
  undoCompleteStep: (lessonId: string, stepIndex: number) => void
  goToStep: (lessonId: string, stepIndex: number) => void
  finishLesson: (lessonId: string) => void
  resetLesson: (lessonId: string) => void
}>((set, get) => ({
  snapshot: loadProgress(),

  getProgress: (lessonId) => get().snapshot[lessonId] ?? null,

  startLesson: (lessonId, totalSteps) =>
    set((state) => {
      const next = {
        ...state.snapshot,
        [lessonId]: {
          lessonId,
          currentStep: 0,
          totalSteps,
          completedSteps: [],
          lastAccessedAt: new Date().toISOString(),
          finished: false
        }
      }
      saveProgress(next)
      return { snapshot: next }
    }),

  completeStep: (lessonId, stepIndex) =>
    set((state) => {
      const prev = state.snapshot[lessonId]
      if (!prev) return state
      const completedSteps = prev.completedSteps.includes(stepIndex)
        ? prev.completedSteps
        : [...prev.completedSteps, stepIndex]
      const next = {
        ...state.snapshot,
        [lessonId]: {
          ...prev,
          completedSteps,
          currentStep: stepIndex,
          lastAccessedAt: new Date().toISOString()
        }
      }
      saveProgress(next)
      return { snapshot: next }
    }),

  undoCompleteStep: (lessonId, stepIndex) =>
    set((state) => {
      const prev = state.snapshot[lessonId]
      if (!prev) return state
      const completedSteps = prev.completedSteps.filter((s) => s !== stepIndex)
      const next = {
        ...state.snapshot,
        [lessonId]: {
          ...prev,
          completedSteps,
          currentStep: stepIndex,
          finished: false,
          lastAccessedAt: new Date().toISOString()
        }
      }
      saveProgress(next)
      return { snapshot: next }
    }),

  goToStep: (lessonId, stepIndex) =>
    set((state) => {
      const prev = state.snapshot[lessonId]
      if (!prev) return state
      const next = {
        ...state.snapshot,
        [lessonId]: {
          ...prev,
          currentStep: stepIndex,
          lastAccessedAt: new Date().toISOString()
        }
      }
      saveProgress(next)
      return { snapshot: next }
    }),

  finishLesson: (lessonId) =>
    set((state) => {
      const prev = state.snapshot[lessonId]
      if (!prev) return state
      const next = {
        ...state.snapshot,
        [lessonId]: {
          ...prev,
          finished: true,
          lastAccessedAt: new Date().toISOString()
        }
      }
      saveProgress(next)
      return { snapshot: next }
    }),

  resetLesson: (lessonId) =>
    set((state) => {
      const { [lessonId]: _, ...rest } = state.snapshot
      saveProgress(rest)
      return { snapshot: rest }
    })
}))
