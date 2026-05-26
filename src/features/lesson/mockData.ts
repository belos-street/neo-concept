import type { Lesson } from '@shared/types'

export const mockLesson: Lesson = {
  id: 'lesson-01',
  title: 'A Day at the Park',
  book_id: 'book-01',
  unit_id: 'unit-01',
  grammar_points: [
    {
      name: 'Present Continuous',
      explanation:
        'Used to describe actions happening right now. Form: subject + am/is/are + verb-ing.',
      examples: ['I am walking in the park.', 'She is reading a book.']
    },
    {
      name: 'Prepositions of Place',
      explanation: 'Words that show location: on, in, under, next to, between.',
      examples: [
        'The dog is under the tree.',
        'The bench is next to the fountain.'
      ]
    }
  ],
  new_vocabulary: [
    {
      word: 'bench',
      phonetic: '/bentʃ/',
      definition_cn: '长椅',
      part_of_speech: 'n.',
      example: 'They sat on the bench.'
    },
    {
      word: 'fountain',
      phonetic: '/ˈfaʊntən/',
      definition_cn: '喷泉',
      part_of_speech: 'n.',
      example: 'The fountain is beautiful.'
    },
    {
      word: 'squirrel',
      phonetic: '/ˈskwɜːrəl/',
      definition_cn: '松鼠',
      part_of_speech: 'n.',
      example: 'A squirrel ran up the tree.'
    },
    {
      word: 'path',
      phonetic: '/pæθ/',
      definition_cn: '小路',
      part_of_speech: 'n.',
      example: 'We walked along the path.'
    },
    {
      word: 'shade',
      phonetic: '/ʃeɪd/',
      definition_cn: '阴凉处',
      part_of_speech: 'n.',
      example: 'Let us sit in the shade.'
    }
  ],
  review_vocabulary: ['park', 'tree', 'walk', 'book', 'sunny'],
  passage: {
    title: 'A Day at the Park',
    text: 'It is a sunny afternoon. Tom and his sister Lily are walking in the park. They see a big fountain in the middle of the park. Some children are playing near the fountain.\n\nTom sits on a bench under a tall tree. Lily is reading a book next to him. A small squirrel is running up the tree. The squirrel is looking for food.\n\n"Look at the squirrel!" says Lily. Tom smiles and watches the squirrel. They are enjoying the cool shade of the tree.\n\nAfter a while, they walk along the path to the lake. Some ducks are swimming on the water. It is a beautiful day at the park.',
    word_count: 124,
    images: [
      {
        url: 'https://picsum.photos/seed/park1/400/200',
        alt: 'People walking in a sunny park with a fountain'
      },
      {
        url: 'https://picsum.photos/seed/squirrel/400/200',
        alt: 'A squirrel on a tree'
      }
    ]
  },
  listening: {
    questions: [
      {
        type: 'choice',
        audio_segment: 'Tom and his sister Lily are walking in the park.',
        question: 'What are Tom and Lily doing?',
        options: [
          'Running in the park',
          'Walking in the park',
          'Sitting on a bench',
          'Swimming in the lake'
        ],
        answer: 1
      },
      {
        type: 'choice',
        audio_segment: 'A small squirrel is running up the tree.',
        question: 'What is the squirrel doing?',
        options: [
          'Looking for food',
          'Running up the tree',
          'Sitting on the bench',
          'Swimming in the lake'
        ],
        answer: 1
      },
      {
        type: 'choice',
        audio_segment: 'Some ducks are swimming on the water.',
        question: 'What are the ducks doing?',
        options: [
          'Walking on the path',
          'Running up the tree',
          'Swimming on the water',
          'Playing near the fountain'
        ],
        answer: 2
      }
    ]
  },
  fill_blanks: {
    template:
      'It is a ___ afternoon. Tom and his sister Lily are ___ in the park. They see a big ___ in the middle of the park. Some children are ___ near the fountain. Tom sits on a ___ under a tall tree. Lily is ___ a book next to him.',
    gaps: [
      {
        gap_index: 0,
        word: 'sunny',
        initial: 's',
        pos: 'adj.',
        meaning_cn: '晴朗的'
      },
      {
        gap_index: 1,
        word: 'walking',
        initial: 'w',
        pos: 'v.',
        meaning_cn: '散步'
      },
      {
        gap_index: 2,
        word: 'fountain',
        initial: 'f',
        pos: 'n.',
        meaning_cn: '喷泉'
      },
      {
        gap_index: 3,
        word: 'playing',
        initial: 'p',
        pos: 'v.',
        meaning_cn: '玩耍'
      },
      {
        gap_index: 4,
        word: 'bench',
        initial: 'b',
        pos: 'n.',
        meaning_cn: '长椅'
      },
      {
        gap_index: 5,
        word: 'reading',
        initial: 'r',
        pos: 'v.',
        meaning_cn: '阅读'
      }
    ]
  },
  vocabulary_exercises: {
    flashcards: [0, 1, 2, 3, 4],
    spelling: [0, 1, 2, 3, 4],
    matching: [
      { en_index: 0, cn_options: ['长椅', '松鼠', '喷泉', '小路'], answer: 0 },
      {
        en_index: 1,
        cn_options: ['长椅', '喷泉', '小路', '阴凉处'],
        answer: 1
      },
      {
        en_index: 2,
        cn_options: ['松鼠', '喷泉', '小路', '阴凉处'],
        answer: 0
      },
      { en_index: 3, cn_options: ['小路', '松鼠', '喷泉', '长椅'], answer: 0 },
      { en_index: 4, cn_options: ['松鼠', '阴凉处', '喷泉', '小路'], answer: 1 }
    ]
  },
  reading: {
    questions: [
      {
        type: 'choice',
        question: 'Where is the big fountain?',
        options: [
          'Next to the bench',
          'In the middle of the park',
          'Near the lake',
          'Under the tree'
        ],
        answer: 1,
        evidence: 'They see a big fountain in the middle of the park.'
      },
      {
        type: 'choice',
        question: 'What is Lily doing on the bench?',
        options: [
          'Watching the squirrel',
          'Walking on the path',
          'Reading a book',
          'Playing with children'
        ],
        answer: 2,
        evidence: 'Lily is reading a book next to him.'
      },
      {
        type: 'choice',
        question: 'What is the squirrel looking for?',
        options: ['Water', 'Food', 'Shade', 'The lake'],
        answer: 1,
        evidence: 'The squirrel is looking for food.'
      }
    ]
  },
  speaking: {
    sentences: [
      {
        text: 'Tom and his sister Lily are walking in the park.',
        difficulty: 1
      },
      { text: 'A small squirrel is running up the tree.', difficulty: 1 },
      { text: 'They are enjoying the cool shade of the tree.', difficulty: 2 },
      { text: 'Some ducks are swimming on the water.', difficulty: 1 }
    ]
  }
}
