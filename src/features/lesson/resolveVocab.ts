import { ecdict } from '@native/ecdict'
import type { VocabularyItem } from '@shared/types'

const emptyItem = (word: string): VocabularyItem => ({
  word,
  phonetic: '',
  definition_cn: '',
  part_of_speech: '',
  example: ''
})

let initPromise: Promise<void> | null = null

async function ensureInit(): Promise<void> {
  if (!initPromise) {
    initPromise = ecdict.init().catch((e) => {
      console.warn('[ECDICT] init error:', e)
      initPromise = null
    })
  }
  await initPromise
}

export async function lookupWord(word: string): Promise<VocabularyItem> {
  try {
    await ensureInit()
    const entry = await ecdict.lookup(word.toLowerCase())
    if (entry) {
      return {
        word: entry.word,
        phonetic: entry.phonetic,
        definition_cn: entry.definition,
        part_of_speech: entry.pos,
        example: ''
      }
    }
  } catch (e) {
    console.warn('[ECDICT] lookup error:', word, e)
  }
  return emptyItem(word)
}

export async function resolveVocab(words: string[]): Promise<VocabularyItem[]> {
  try {
    await ensureInit()
  } catch (e) {
    console.warn('[resolveVocab] init error:', e)
    return words.map(emptyItem)
  }

  const results: VocabularyItem[] = []
  for (const w of words) {
    try {
      const entry = await ecdict.lookup(w.toLowerCase())
      if (entry) {
        results.push({
          word: entry.word,
          phonetic: entry.phonetic,
          definition_cn: entry.definition,
          part_of_speech: entry.pos,
          example: ''
        })
      } else {
        results.push(emptyItem(w))
      }
    } catch (e) {
      console.warn('[resolveVocab] lookup error:', w, e)
      results.push(emptyItem(w))
    }
  }
  return results
}
