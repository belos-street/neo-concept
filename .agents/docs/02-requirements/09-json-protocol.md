# 09 — YAML 数据协议（Agent ↔ App 契约）

> 所属：Neo Concept 结构化需求文档
>
> 这是 Agent 与 App 之间的硬契约。Agent 必须按此 Schema 产出 YAML，App 按此 Schema 解析消费。
>
> **为什么选择 YAML？**
> - 中文内容友好，无需转义引号
> - 多行文本（课文）使用 `|` 块语法更清晰
> - 手动编辑更友好，不易出错
> - 可读性更强

---

## manifest.yaml — 课程索引

```yaml
version: "1.0.0"
lastUpdated: "2026-05-30T00:00:00Z"

books:
  - id: book-1
    title: Book 1: Foundations
    description: "..."
    level: beginner
    target_vocabulary: 500
    units:
      - id: unit-1-1
        title: "Unit 1: Simple Present Tense"
        grammar_points:
          - simple present
          - subject-verb agreement
        lessons:
          - id: lesson-1-1
            title: "A Day at the Market"
            hash: "sha256-abc123..."
            file: "book-1/unit-1/lesson-1-1.yaml"
```

---

## lesson-{id}.yaml — 单课内容

```yaml
id: lesson-1-1
title: "A Day at the Market"
book_id: book-1
unit_id: unit-1-1

grammar_points:
  - name: Simple Present Tense
    explanation: "..."
    examples:
      - "..."

new_vocabulary:
  - word: market
    phonetic: "/ˈmɑːrkɪt/"
    definition_cn: 市场
    part_of_speech: n.
    example: "She goes to the market every Sunday."

passage:
  title: "A Day at the Market"
  text: |
    Every Sunday, Anna goes to the market.
    She buys fresh vegetables and fruit...
  word_count: 150
  images:
    - url: "book-1/unit-1/lesson-1-1_img1.png"
      alt: "A busy outdoor market with colorful stalls"

listening:
  questions:
    - type: choice
      audio_segment: "Every Sunday, Anna goes to the market..."
      question: "Where does Anna go every Sunday?"
      options: [School, Market, Park, Library]
      answer: 1

fill_blanks:
  template: |
    Every ___, Anna goes to the ___.
    She buys ___ vegetables and fruit.
  gaps:
    - gap_index: 1
      word: Sunday
      initial: S
      pos: n.
      meaning_cn: 星期日

    - gap_index: 2
      word: market
      initial: M
      pos: n.
      meaning_cn: 市场

    - gap_index: 3
      word: fresh
      initial: F
      pos: adj.
      meaning_cn: 新鲜的

vocabulary_exercises:
  flashcards: [0]
  spelling: [0]
  matching:
    - en_index: 0
      cn_options: [市场, 学校, 公园, 图书馆]
      answer: 0

reading:
  questions:
    - type: detail
      question: "What does Anna buy at the market?"
      options: [Meat and rice, Vegetables and fruit, Bread and milk, Fish and chips]
      answer: 1
      evidence: "She buys fresh vegetables and fruit"

speaking:
  sentences:
    - text: "Every Sunday, Anna goes to the market."
      difficulty: 1

    - text: "She buys fresh vegetables and fruit."
      difficulty: 2

    - text: "Anna enjoys the colorful stalls and fresh produce every weekend morning."
      difficulty: 3
```

---

### fill_blanks 字段说明

> `gap_index` 从 1 开始计数，表示 `template` 中第 N 个 `___` 占位符。
> 例如：`gap_index: 1` → 第一个 `___`，`gap_index: 2` → 第二个 `___`。
>
> 每个 gap 为结构化提示：
> - `initial`：首字母，供用户参考
> - `pos`：词性（n. / v. / adj. / adv. / prep. 等）
> - `meaning_cn`：中文释义
>
> App 端根据 `initial` + `pos` + `meaning_cn` 渲染提示区，无需自行解析。

### listening 字段说明

> 每道 listening 题目自带 `audio_segment` 字段，为课文关键段落的 **文本内容**（非音频文件）。
> App 端通过 TTS 引擎实时合成播放。不同题目可对应不同文本片段，实现「错题重听对应片段」。

### vocabulary_exercises 字段说明

> `flashcards`、`spelling`、`matching` 中的数字为 **`new_vocabulary` 数组的索引**（从 0 开始），不再重复存储词条数据。
>
> - `flashcards: [0]` → 取 `new_vocabulary[0]` 作为闪卡词条
> - `spelling: [0]` → 取 `new_vocabulary[0]` 作为拼写练习词
> - `matching[].en_index` → 取 `new_vocabulary[N]` 作为英文，与 `cn_options` 做匹配

### reading 字段说明

> `reading` 不再包含 `passage` 字段。Step 5 阅读理解共用 Step 1 的 `passage.text`，避免同一段课文在 YAML 中重复存储。

### speaking 字段说明

> `difficulty` 取值 1-3，反映句子长度和复杂度：
> - `1`（简单）：≤ 8 词，基础句型
> - `2`（中等）：9-15 词，含从句或并列结构
> - `3`（困难）：> 15 词，长难句
>
> App 端按 `difficulty` 升序排列句子，并在 UI 上标注难度标签（简单 / 中等 / 困难）。

---

## YAML 语法要点

### 多行文本
使用 `|` 保留换行符：
```yaml
text: |
  First line.
  
  Second line.
```

### 列表
```yaml
items:
  - item1
  - item2
```

### 字符串引号
包含特殊字符时需要引号：
```yaml
title: "Lesson 1: Excuse me!"
example: "She goes to the market every Sunday."
```

---

## 依赖库

Android 端使用 [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml) 解析 YAML：

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.yaml:snakeyaml:2.2")
}
```
