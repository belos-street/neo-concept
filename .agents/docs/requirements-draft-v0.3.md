# Neo Concept — 需求文档草稿 v0.3

> 状态：草稿，待多轮迭代优化

***

## 零、项目缘起与核心动机

### 0.1 为什么做这个项目

1. **系统化学习英语的诉求** — 作者（也是目标用户）希望有一款能像「新概念英语」那样循序渐进、体系化提升英语能力的工具。现有市面上的产品（如多领国）偏重游戏化打卡，900 天坚持下来却没有获得系统性的提升。这不是毅力的问题，是方法的问题。
2. **帮助英语底子差的朋友** — 目标用户不是「已经不错想再提升」的人，而是基础薄弱、需要从零开始搭建语法和词汇体系的学习者。课程内容需符合这个定位。

### 0.2 核心设计原则（反多领国宣言）

多领国的问题不是它做得不好，而是它的设计目标与「系统化提升英语」不匹配：

| 多领国的做法       | Neo Concept 的做法     |
| ------------ | ------------------- |
| 碎片化句子翻译，缺乏语境 | 每课围绕一篇完整课文，在语境中学习   |
| 重打卡激励，轻知识内化  | 重练习质量，进度基于掌握度而非打卡   |
| 语法隐式教学，零散出现  | 语法显式编排，螺旋式递进        |
| 词汇在孤立的卡片中记忆  | 词汇在课文语境 + 循环复现中记忆   |
| 口语练习极其有限     | 听说读写 + 口语跟读评分，全维度覆盖 |

### 0.3 版权声明

本项目的课程内容由 LLM **原创生成**，参考新概念英语的教学**方法**和**递进体系**（教学方法不受版权保护），但**不复制、不翻译、不改编**其课文原文。所有课文、练习、例句均为 AI 原创内容。

***

## 一、项目概述

| 维度           | 说明                                              |
| ------------ | ----------------------------------------------- |
| **项目名**      | neo-concept                                     |
| **产品形态** | 手机 App（React Native） |
| **App 定位** | 离线优先、系统化英语学习工具 |
| **Agent CLI** | **不在本项目开发范围内**。Agent 是一个独立的外部工具（可以是 LangChain CLI 脚本），负责利用 LLM 生成课程内容，导出 JSON 供 App 消费。本文档仅描述 Agent 与 App 之间的**数据协议契约（JSON Schema）**。 |
| **目标用户**     | 英语基础薄弱、希望体系化提升的学习者                              |
| **商业模式**     | 个人项目，非商业                                        |

***

## 二、技术选型

| 层次 | 选型 | 原因 |
|------|------|------|
| App 框架 | React Native 0.76+ + TypeScript | 练手 RN 开发；Android 优先 |
| 语音识别 (ASR) | Whisper.cpp (tiny.en) | 离线识别，~75 MB，英语专用，39M 参数 |
| 语音合成 (TTS) | Piper (kathleen-low) | 离线朗读，~20 MB，轻量英文女声 |
| 离线词典 | ECDICT (mini) | 英汉离线词典，~10 MB，150万+ 词汇，SQLite |
| 状态管理 | Zustand | 轻量、现代、TypeScript 友好 |
| 本地持久化 | react-native-mmkv + expo-sqlite | KV 存储(进度) + SQLite(词典查询) |
| 内容分发 | Git 仓库 HTTP 拉取 + SHA256 Hash 校验 | 版本化课程内容管理 |
| **App 模型总负载** | | **~105 MB**（ASR 75 + TTS 20 + 词典 10） |

***

## 三、内容层级架构

```
Book (册)  →  Unit (单元)  →  Lesson (课)
  例: 4册         每册~25单元      每单元1课
```

**递进逻辑**：

- Book 1-2：基础词汇 800-1500，简单句，现在/过去/将来时态
- Book 3-4：词汇 1500-4000，复合句、被动语态、虚拟语气
- 每课引入 1-2 个新语法点 + 10-20 个新词汇
- 旧词汇和语法点在后续课程中**循环复现**

### 3.1 四册递进与能力对标

| 册 | 累计词汇 | 语法层次 | 对标难度 |
|---|---------|---------|---------|
| Book 1 | 800-1200 | 简单句、基本时态（现在/过去/将来） | 初中-高一 |
| Book 2 | 1200-2500 | 复合句、完成时态、被动语态、情态动词 | 高二 ~ CET-4 |
| Book 3 | 2500-4000 | 从句体系（名/定/状）、虚拟语气、倒装 | CET-4 ~ CET-6 |
| Book 4 | 4000-6000 | 长难句、学术议论文、报刊评论风格 | CET-6 / 考研英语 |

**结论**：学完 4 册后，**阅读和写作可覆盖 CET-6/考研英语难度**（词汇 5500-6000，长难句无障碍）。口语方面，课程能提供准确的发音、充足的句型储备和开口的信心，但要达到「即兴流畅对话」需要数百小时真实对话练习——这是任何结构化课程无法替代的。课程定位为「说得出、说得对」，而非「说得溜」。

### 3.2 每节 Lesson 结构（6 个环节）

学习顺序遵循「输入 → 加工 → 输出」的认知规律：

| 环节 | 名称 | 题量 | 预计用时 | 说明 |
|------|------|------|---------|------|
| Step 1 | **课文预习** (Passage) | — | 2-3 min | 100-200 词短文 + AI 配图 1-2 张 + TTS 全文/逐句朗读 |
| Step 2 | **填词练习** (Fill-blanks) | 6-8 题 | 2-3 min | 课文句子挖空，补全词汇/短语，首字母 + 词性 + 中文提示 |
| Step 3 | **词汇记忆** (Vocabulary) | 10-15 词 | 3-5 min | 闪卡浏览(全部) → 拼写(6-8词) → 英汉匹配(6-8对) |
| Step 4 | **听力练习** (Listening) | 4-5 题 | 2-3 min | TTS 朗读课文片段，全部选择题：主旨1+细节2-3+推理1 |
| Step 5 | **阅读理解** (Reading) | 5 题 | 3-5 min | 全部选择题：细节2+词义推断1+推理1+主旨1，错题定位原文 |
| Step 6 | **口语练习** (Speaking) | 3-5 句 | 3-5 min | 跟读课文句子，ASR 识别 + 编辑距离评分，标红偏差词 |
| **合计** | | | **15-25 min** | 适合碎片化学习，分步骤完成 |

> **AI 配图方案**：图片由 Agent 在生成课程时一并产出（DALL-E 等），存储在 Git 仓库中。App 通过 JSON 中的 `image_url` 字段加载显示，首次加载后本地缓存。每课 1-2 张图，单张 ~50KB，几乎不影响下载速度。

***

## 四、功能需求

### 4.1 课程管理

- 配置课程仓库 URL（GitHub raw）
- 首次启动拉取 `manifest.json` 作为课程索引
- 按需下载单课 JSON（点击某课时触发下载）
- 本地 Hash 对比，检测内容更新（新增/修改的课程）
- 手动「检查更新」按钮

### 4.2 学习流程

- **两种学习模式**：
  - 线性模式：完成前一课解锁下一课
  - 自由模式：所有已下载课程可自由访问
- 一课的学习顺序（即 Step 1-6）：

  ```
  课文预习 → 填词练习 → 词汇记忆 → 听力练习 → 阅读理解 → 口语练习
  Step 1    Step 2    Step 3    Step 4    Step 5    Step 6
  ```

- 每个 Step 完成后自动保存进度（见 4.7 进度系统）
- 每个 Step 记录正确率、用时
- 一课全部 Step 完成后标记为「已完成」
- 支持中途退出，下次进入自动恢复到上次所在 Step

### 4.3 练习详情

**Step 1 — 课文预习**：

- 展示 AI 配图（1-2 张，JSON 中的 `images` 字段，本地缓存）
- 课文全文显示，关键字词高亮
- **所有单词带下划线，可点击弹出释义 tooltip**（见 4.8 内联词汇查阅）
- TTS 全文朗读（可调速 0.5x-1.5x）
- 逐句播放 + 逐句高亮
- 语法点卡片（本课涉及的语法点简要说明）

**Step 2 — 填词练习（6-8 题）**：

- 显示含空格的课文句子（每句 1 个空，从课文不同位置抽取）
- 提示：首字母 + 词性（n./v./adj.）+ 中文释义
- 用户输入补全，实时比对
- 正确→绿色✅ / 错误→显示正确答案（红色）+ 发音
- 全部完成后显示得分 + 错误词列表

**Step 3 — 词汇记忆（10-15 词，含旧词复现）**：

三种子模式按顺序进行：

| 子模式 | 范围 | 形式 |
|--------|------|------|
| 闪卡 | 10-15 词（全部新词 + 部分复现词） | 正面英文+发音按钮，反面中文+例句，左滑不认识/右滑认识 |
| 拼写 | 6-8 词（核心新词） | TTS 发音 → 输入拼写 → 即时反馈，可重听 |
| 英汉匹配 | 6-8 对（混合新旧词） | 拖拽或点击配对，英文→中文，计时完成 |

- 闪卡模式可选「跳过认识词」，仅学习不认识的词
- 错词自动加入复习队列

**Step 4 — 听力练习（4-5 题，全部选择题）**：

- TTS 朗读课文片段（不是完整课文，是截取的段落）
- 每题 4 个选项，可重复播放
- 题型分布：
  - 主旨题 1 道（这段主要在说什么？）
  - 细节题 2-3 道（谁、哪里、什么、何时）
  - 推理题 1 道（可推断出什么？）
- 即时判分，错题可重听

> **为什么不用填空题做听力？** 填空题同时考验「听」+「拼写」，对基础薄弱者负担过重。听力练的是抓取信息，选择题即可。

**Step 5 — 阅读理解（5 题，全部选择题）**：

- 展示课文全文（可滚动）
- 题型分布：
  - 细节理解 2 道（文中明确提及的信息）
  - 词义推断 1 道（根据上下文推断单词或短语意思）
  - 推理判断 1 道（文中暗示但未直接说明）
  - 主旨大意 1 道（文章主要讲什么/最佳标题）
- **关键功能**：选错后自动高亮原文对应句，显示解析

**Step 6 — 口语练习（3-5 句）**：

- 句子来源：课文精选，覆盖本课语法点 + 发音难点
- 难度递进：短句 → 中等 → 长句
- 流程：显示句子 → 播放标准发音（Piper TTS）→ 用户录音 → Whisper 识别
- 评分：编辑距离比对，显示准确率百分比
- 标红偏差词汇（用户说的 vs 原文的差异）
- 最小可接受准确率：60%（可在设置中调整严格模式）

### 4.4 学习统计

- 总学习时长、完成课时数
- 各模块正确率趋势
- 词汇掌握量
- 最近学习记录

### 4.5 数据管理

- **导出**：学习进度 + 统计 + 已下载课程打包为 JSON/ZIP
- **导入**：从文件恢复全部数据
- 用途：换设备迁移、备份

### 4.6 离线能力

- 所有功能无需联网（首次下载课程后）
- ASR/TTS 模型本地推理
- 课程 JSON 本地存储

### 4.7 学习进度系统（Per-Step Caching）

App 面向碎片化学习场景——用户可能在地铁上做 Step 1-2，午休做 Step 3-4，晚上做 Step 5-6。因此进度粒度必须细化到每个 Step。

**进度数据结构**：

```typescript
interface LessonProgress {
  lessonId: string          // "lesson-1-1"
  currentStep: 1 | 2 | 3 | 4 | 5 | 6  // 当前所在环节
  steps: {
    [step: number]: {
      status: 'idle' | 'in_progress' | 'completed'
      startedAt: number | null      // timestamp
      completedAt: number | null    // timestamp
      score: number | null          // 正确率 0-100
      timeSpentSeconds: number | null
      // Step 特定数据
      errors?: string[]             // 填词/拼写错误词列表
      attemptCount?: number         // 口语练习尝试次数
    }
  }
  lastUpdated: number
  lessonCompleted: boolean         // 所有 6 个 step 都完成
}
```

**存储策略**：

- 使用 `react-native-mmkv` 存储，key 格式：`progress:{lessonId}`
- 每个 Step 结束时立即写入（非全课完成后）
- 支持 App 被杀死后恢复

**进度流转规则**：

```
idle → in_progress → completed
                       ↓
                   下一 Step 自动解锁
```

- 用户进入新课时，Step 1 自动设为 `in_progress`
- 完成当前 Step（满足最低正确率）后自动保存，解锁下一 Step
- 用户可回看已完成 Step 的内容和答题记录，但不可修改答案
- 退出 App → 下次打开该课 → 自动跳转到 `currentStep`

**一课时间线示例**：

```
08:15  用户打开 lesson-1-1
       Step 1: 课文预习 → 3 分钟 → completed
       进度写入 MMKV: currentStep=2

12:30  午休，继续学习
       Step 2: 填词 6 题 → 2 分钟 → completed, score=83%
       Step 3: 词汇闪卡+拼写 → 4 分钟 → completed, score=75%
       进度写入: currentStep=4

21:00  晚上，继续
       Step 4: 听力 4 题 → 2 分钟 → completed
       Step 5: 阅读 5 题 → 4 分钟 → completed
       Step 6: 口语 3 句 → 5 分钟 → completed
       进度写入: lessonCompleted=true, currentStep=6
```

### 4.8 内联词汇查阅（Inline Vocabulary Lookup）

App 中所有展示英文文本的地方——课文、题目、选项、例句——其中的每个单词都应该是可点击的。用户点击任意单词，弹出 tooltip 显示该词的中文释义、音标、词性、例句。

**触发位置**：
- Step 1 课文全文中的任意单词
- Step 4 听力题目和选项中的单词
- Step 5 阅读理解和选项中的单词
- Step 6 口语目标句子中的单词
- 语法卡片例句中的单词

**Tooltip 样式**：

```
┌─────────────────────────────────┐
│  beautiful    /ˈbjuːtɪfl/       │
│  adj. 美丽的；漂亮的              │
│                                 │
│  📝 She is a beautiful girl.    │
│  🔊 [发音按钮]                   │
└─────────────────────────────────┘
```

**数据来源**：ECDICT 离线 SQLite 数据库（~10 MB），查询 <10ms。数据结构如下：

```typescript
interface WordEntry {
  word: string           // "beautiful"
  phonetic: string       // "/ˈbjuːtɪfl/"
  pos: string            // "adj."
  meaning: string        // "美丽的；漂亮的"
  sentence: string       // "She is a beautiful girl."
}
```

**视觉表现**：
- 课文中所有词汇默认带下划线（`text-decoration: underline`），表示可点击
- 点击后弹出 tooltip（不跳转页面，就地浮动层）
- 点击 tooltip 外部或再次点击单词关闭
- 发音按钮调用 Piper TTS 朗读该单词

> **为什么用 ECDICT 而不是其他方案？**
> - 150 万+ 词汇量，远超 WordNet/CMUdict 的 15 万
> - `ecdict.mini.csv` 仅 ~10 MB，移动端友好
> - 英汉互译（含中文释义），其他方案多为英英词典
> - MIT 开源协议，自由使用

***

## 五、JSON 数据协议（Agent ↔ App 契约）

### 5.1 manifest.json — 课程索引

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

### 5.2 lesson-{id}.json — 单课内容

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
      {"position": 3, "word": "Sunday", "hint": "a day of the week"},
      {"position": 7, "word": "fresh", "hint": "not old or stale"}
    ],
    "template": "Every ___, Anna goes to the ___. She buys ___ vegetables and fruit..."
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

### 5.3 内容分发流程

```
Agent CLI（外部工具，非本项目范围）
  │
  │  使用 LLM 生成 course JSON（单课/批量）
  │  计算 SHA256 hash
  │  更新 manifest.json
  │
  ▼
Git 仓库 (GitHub)
  │  book-1/
  │    unit-1/
  │      lesson-1-1.json
  │      lesson-1-2.json
  │  manifest.json
  │  images/
  │    lesson-1-1_img1.png
  │
  ▼
App (React Native)  ← 本项目范围
  │  1. 首次启动 → fetch manifest.json
  │  2. 用户选择课程 → fetch lesson-{id}.json
  │  3. 检查更新 → 对比 manifest 中 hash
  │  4. 增量下载变更的课程 + 图片
```

***

## 六、平台与开发环境

| 维度   | 说明                                                 |
| ---- | -------------------------------------------------- |
| 目标平台 | **Android**（初期不涉及 iOS）                             |
| 开发设备 | macOS                                              |
| 开发工具 | VS Code + Android Studio（模拟器）                      |
| 包管理  | bun                                                |
| 测试策略 | 初期以手动测试为主，后期补充 jest + React Native Testing Library |

***

## 七、风险与待解决问题

| 风险 | 等级 | 应对 |
|------|------|------|
| Whisper.cpp 包体积 (~75 MB) | 低 | tiny.en 仅 75 MB，首次启动下载引导即可 |
| 口语评分准确度 | 高 | 初版仅做编辑距离比对；后续考虑发音置信度评分 |
| 口语「流畅对话」目标 | 中 | 明确课程边界：「说得出、说得对」而非「说得溜」；真实对话练习需用户自行补充 |
| RN 原生模块桥接工作量 | 中 | Whisper/Piper 都有 RN 社区封装，优先使用现有库 |
| LLM 生成内容质量不稳定 | 中 | Agent 端增加校验层：语法检查 + 人工审核流程；structured output 保证格式 |
| 版权风险 | 低 | 所有内容 LLM 原创生成，不复制新概念课文文本 |

***

## 八、不做的事情（明确边界）

- ❌ 不做用户账号系统（纯本地）
- ❌ 不做云端同步（用导入/导出替代）
- ❌ 不做社交功能（排行榜、好友、分享）
- ❌ 不做 iOS 适配（初期）
- ❌ 不做多语言（仅中英）
- ❌ 不照搬新概念英语课文内容

***

*文档版本：v0.3 | 更新日期：2026-05-12 | 待多轮迭代*
