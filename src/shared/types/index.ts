export interface Manifest {
  version: string
  generated_at: string
  books: Book[]
}

export interface Book {
  id: string
  title: string
  description: string
  level: string
  target_vocabulary: number
  units: Unit[]
}

export interface Unit {
  id: string
  title: string
  grammar_points: string[]
  lessons: LessonMeta[]
}

export interface LessonMeta {
  id: string
  title: string
  hash: string
  file: string
}

export interface Lesson {
  id: string
  title: string
  book_id: string
  unit_id: string
  grammar_points: GrammarPoint[]
  new_vocabulary: VocabularyItem[] | string[]
  review_vocabulary: string[]
  passage: Passage
  listening: ListeningSection
  fill_blanks: FillBlanksSection
  vocabulary_exercises: VocabularyExercises
  reading: ReadingSection
  speaking: SpeakingSection
}

export interface GrammarPoint {
  name: string
  explanation: string
  examples: string[]
}

export interface VocabularyItem {
  word: string
  phonetic: string
  definition_cn: string
  part_of_speech: string
  example: string
}

export interface Passage {
  title: string
  text: string
  word_count: number
  images: PassageImage[]
}

export interface PassageImage {
  url: string
  alt: string
}

export interface ListeningSection {
  questions: ListeningQuestion[]
}

export interface ListeningQuestion {
  type: string
  audio_segment: string
  question: string
  options: string[]
  answer: number
}

export interface FillBlanksSection {
  gaps: FillBlankGap[]
  template: string
}

export interface FillBlankGap {
  gap_index: number
  word: string
  initial: string
  pos: string
  meaning_cn: string
}

export interface VocabularyExercises {
  flashcards: number[]
  spelling: number[]
  matching: MatchingItem[]
}

export interface MatchingItem {
  en_index: number
  cn_options: string[]
  answer: number
}

export interface ReadingSection {
  questions: ReadingQuestion[]
}

export interface ReadingQuestion {
  type: string
  question: string
  options: string[]
  answer: number
  evidence: string
}

export interface SpeakingSection {
  sentences: SpeakingSentence[]
}

export interface SpeakingSentence {
  text: string
  difficulty: 1 | 2 | 3
}
