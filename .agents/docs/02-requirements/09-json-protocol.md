# 09 — JSON 数据协议（Agent ↔ App 契约）

> 所属：Neo Concept 结构化需求文档
>
> 这是 Agent 与 App 之间的硬契约。Agent 必须按此 Schema 产出 JSON，App 按此 Schema 解析消费。

---

## manifest.json — 课程索引

```json
{
  "version": "1.0.0",
  "generated_at": "2026-05-12T00:00:00Z",
  "books": [
    {
      "id": "book-1",
      "title": "Book 1: Foundations",
      "description": "...",
      "level": "beginner",
      "target_vocabulary": 800,
      "units": [
        {
          "id": "unit-1-1",
          "title": "Unit 1: Simple Present Tense",
          "grammar_points": ["simple present", "subject-verb agreement"],
          "lessons": [
            {
              "id": "lesson-1-1",
              "title": "A Day at the Market",
              "hash": "sha256-abc123...",
              "file": "book-1/unit-1/lesson-1-1.json"
            }
          ]
        }
      ]
    }
  ]
}
```

---

## lesson-{id}.json — 单课内容

```json
{
  "id": "lesson-1-1",
  "title": "A Day at the Market",
  "book_id": "book-1",
  "unit_id": "unit-1-1",
  "hash": "sha256-abc123...",
  "grammar_points": [
    {"name": "Simple Present Tense", "explanation": "...", "examples": ["..."]}
  ],
  "new_vocabulary": [
    {"word": "market", "phonetic": "/ˈmɑːrkɪt/", "definition_cn": "市场", "part_of_speech": "n.", "example": "She goes to the market every Sunday."}
  ],
  "review_vocabulary": ["apple", "go", "every"],
  "passage": {
    "title": "A Day at the Market",
    "text": "Every Sunday, Anna goes to the market. She buys fresh vegetables and fruit...",
    "word_count": 150,
    "images": [
      {"url": "book-1/unit-1/lesson-1-1_img1.png", "alt": "A busy outdoor market with colorful stalls"}
    ]
  },
  "listening": {
    "audio_text": "Every Sunday, Anna goes to the market...",
    "questions": [
      {
        "type": "choice",
        "question": "Where does Anna go every Sunday?",
        "options": ["School", "Market", "Park", "Library"],
        "answer": 1
      }
    ]
  },
  "fill_blanks": {
    "gaps": [
      {"gap_index": 1, "word": "Sunday", "hint": "a day of the week"},
      {"gap_index": 2, "word": "market", "hint": "a place to buy things"},
      {"gap_index": 3, "word": "fresh", "hint": "not old or stale"}
    ],
    "template": "Every ___, Anna goes to the ___. She buys ___ vegetables and fruit."
  },
  "vocabulary_exercises": {
    "flashcards": [{"word": "market", "definition_cn": "市场", "example": "..."}],
    "spelling": [{"word": "market", "audio_hint": true}],
    "matching": [{"en": "market", "cn_options": ["市场", "学校", "公园", "图书馆"], "answer": 0}]
  },
  "reading": {
    "passage": "Every Sunday, Anna goes to the market...",
    "questions": [
      {
        "type": "detail",
        "question": "What does Anna buy at the market?",
        "options": ["Meat and rice", "Vegetables and fruit", "Bread and milk", "Fish and chips"],
        "answer": 1,
        "evidence": "She buys fresh vegetables and fruit"
      }
    ]
  },
  "speaking": {
    "sentences": [
      {"text": "Every Sunday, Anna goes to the market.", "difficulty": 1},
      {"text": "She buys fresh vegetables and fruit.", "difficulty": 1}
    ]
  }
}
```

---

### fill_blanks 字段说明

> `gap_index` 从 1 开始计数，表示 `template` 中第 N 个 `___` 占位符。
> 例如：`gap_index: 1` → 第一个 `___`，`gap_index: 2` → 第二个 `___`。
> `hint` 向用户展示提示信息（如首字母、词性、中文释义）。

---

## 内容分发流程

```
Agent CLI（外部工具，非本项目范围）
  │  使用 LLM 生成 course JSON
  │  计算 SHA256 hash
  │  更新 manifest.json
  ▼
Git 仓库 (GitHub)
  │  book-1/unit-1/lesson-*.json
  │  manifest.json
  │  images/
  ▼
App (React Native)  ← 本项目范围
  │  1. 首次启动 → fetch manifest.json
  │  2. 用户选择课程 → fetch lesson-{id}.json
  │  3. 检查更新 → 对比 manifest 中 hash
  │  4. 增量下载变更的课程 + 图片
```
