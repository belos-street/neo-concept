import { ecdict } from '@native/ecdict'
import type { VocabularyItem } from '@shared/types'

const POS_MAP: Record<string, string> = {
  n: 'n.',
  v: 'v.',
  j: 'adj.',
  r: 'adv.',
  i: 'prep.',
  c: 'conj.',
  p: 'pron.',
  u: 'num.',
  x: 'int.',
  a: 'art.'
}

function parsePos(posStr: string): string {
  if (!posStr) return ''
  let best = ''
  let bestPct = 0
  for (const part of posStr.split('/')) {
    const [tag, pctStr] = part.split(':')
    const pct = parseInt(pctStr, 10) || 0
    if (pct > bestPct) {
      bestPct = pct
      best = POS_MAP[tag] || tag
    }
  }
  return best
}

function extractExample(defStr: string): string {
  if (!defStr) return ''
  const lines = defStr.split('\n').filter((l) => l.trim())
  for (const line of lines) {
    const cleaned = line.replace(/^[a-z]+\.\s*/i, '').trim()
    if (cleaned.length > 5) {
      return cleaned
    }
  }
  return ''
}

function cleanTranslation(transStr: string): string {
  if (!transStr) return ''
  return transStr
    .split('\n')
    .map((line) => line.replace(/^[a-z]+\.\s*/i, '').trim())
    .filter((line) => line && !line.startsWith('['))
    .join('；')
}

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
        definition_cn: cleanTranslation(entry.translation) || entry.definition,
        part_of_speech: parsePos(entry.pos),
        example: extractExample(entry.definition)
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
          definition_cn:
            cleanTranslation(entry.translation) || entry.definition,
          part_of_speech: parsePos(entry.pos),
          example: extractExample(entry.definition)
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
