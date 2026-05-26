export interface WordDiff {
  word: string
  status: 'correct' | 'wrong' | 'missing'
}

function normalize(s: string): string {
  return s
    .toLowerCase()
    .replace(/[^a-z0-9\s']/g, '')
    .replace(/\s+/g, ' ')
    .trim()
}

function tokenize(s: string): string[] {
  return normalize(s).split(' ').filter(Boolean)
}

export function wordDiff(expected: string, recognized: string): {
  diffs: WordDiff[]
  score: number
} {
  const a = tokenize(expected)
  const b = tokenize(recognized)
  const m = a.length
  const n = b.length

  const dp: number[][] = Array.from({ length: m + 1 }, () =>
    Array(n + 1).fill(0)
  )
  for (let i = 0; i <= m; i++) dp[i][0] = i
  for (let j = 0; j <= n; j++) dp[0][j] = j
  for (let i = 1; i <= m; i++) {
    for (let j = 1; j <= n; j++) {
      dp[i][j] =
        a[i - 1] === b[j - 1]
          ? dp[i - 1][j - 1]
          : 1 + Math.min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
    }
  }

  const diffs: WordDiff[] = []
  let i = m
  let j = n
  const ops: Array<{ type: string; i: number; j: number }> = []
  while (i > 0 || j > 0) {
    if (i > 0 && j > 0 && a[i - 1] === b[j - 1]) {
      ops.unshift({ type: 'match', i: i - 1, j: j - 1 })
      i--
      j--
    } else if (i > 0 && j > 0 && dp[i][j] === dp[i - 1][j - 1] + 1) {
      ops.unshift({ type: 'replace', i: i - 1, j: j - 1 })
      i--
      j--
    } else if (j > 0 && dp[i][j] === dp[i][j - 1] + 1) {
      ops.unshift({ type: 'insert', i: -1, j: j - 1 })
      j--
    } else {
      ops.unshift({ type: 'delete', i: i - 1, j: -1 })
      i--
    }
  }

  for (const op of ops) {
    if (op.type === 'match') {
      diffs.push({ word: a[op.i], status: 'correct' })
    } else if (op.type === 'replace') {
      diffs.push({ word: a[op.i], status: 'wrong' })
    } else if (op.type === 'delete') {
      diffs.push({ word: a[op.i], status: 'missing' })
    }
  }

  const correct = diffs.filter((d) => d.status === 'correct').length
  const score = m === 0 ? 100 : Math.round((correct / m) * 100)

  return { diffs, score }
}
