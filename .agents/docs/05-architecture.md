# 05 — 系统架构设计

> 所属：Neo Concept 结构化需求文档 | Vibe-Flow Stage 5

---

## 技术栈总览

| 层次 | 选型 | 版本 | 用途 |
|------|------|------|------|
| 框架 | React Native | 0.76+ | 跨平台 App（Android 优先） |
| 语言 | TypeScript | 5.x | 类型安全 |
| 导航 | @react-navigation/native | 6.x | Tab + Stack 路由 |
| 状态管理 | Zustand | 5.x | 轻量全局状态 |
| KV 存储 | react-native-mmkv | 3.x | 进度/设置持久化 |
| SQLite | expo-sqlite | — | ECDICT 词典查询 |
| TTS | Piper (kathleen-low) | — | 离线语音合成 |
| ASR | Whisper.cpp (tiny.en) | — | 离线语音识别 |
| 包管理 | bun | — | 依赖管理 |

---

## 项目目录结构

```
neo-concept/
├── android/                    # Android 原生代码
│   └── app/src/main/
│       ├── assets/
│       │   ├── ecdict.db       # ECDICT SQLite (~10 MB)
│       │   ├── piper/          # Piper TTS 模型 (~20 MB)
│       │   └── whisper/        # Whisper 模型 (~75 MB)
│       └── java/.../
│           ├── PiperModule.kt      # TTS 原生模块
│           ├── WhisperModule.kt    # ASR 原生模块
│           └── EcdictModule.kt     # 词典原生模块
├── src/
│   ├── app/
│   │   ├── App.tsx             # 根组件
│   │   ├── navigation.tsx      # 路由配置
│   │   └── providers.tsx       # Context Provider 组合
│   ├── screens/
│   │   ├── CourseListScreen.tsx
│   │   ├── DownloadScreen.tsx
│   │   ├── LessonScreen.tsx    # Step 容器
│   │   ├── StatsScreen.tsx
│   │   └── SettingsScreen.tsx
│   ├── features/
│   │   ├── course/             # 课程管理
│   │   │   ├── components/     # CourseTree, LessonRow, UnitHeader
│   │   │   ├── hooks/          # useManifest, useDownload
│   │   │   └── store.ts        # manifest, download queue
│   │   ├── learning/           # 学习流程
│   │   │   ├── components/     # StepProgressBar, ResumeOverlay
│   │   │   ├── hooks/          # useLessonProgress, useStepFlow
│   │   │   └── store.ts        # current lesson progress
│   │   ├── steps/              # 6 个 Step
│   │   │   ├── Step1Passage/
│   │   │   │   ├── index.tsx
│   │   │   │   └── components/ # PassageView, GrammarCard
│   │   │   ├── Step2FillBlanks/
│   │   │   │   ├── index.tsx
│   │   │   │   └── components/ # BlankInput, HintBar
│   │   │   ├── Step3Vocabulary/
│   │   │   │   ├── index.tsx
│   │   │   │   ├── FlashcardMode.tsx
│   │   │   │   ├── SpellingMode.tsx
│   │   │   │   └── MatchingMode.tsx
│   │   │   ├── Step4Listening/
│   │   │   │   ├── index.tsx
│   │   │   │   └── components/ # AudioPlayer, QuestionCard
│   │   │   ├── Step5Reading/
│   │   │   │   ├── index.tsx
│   │   │   │   └── components/ # EvidenceHighlight
│   │   │   └── Step6Speaking/
│   │   │       ├── index.tsx
│   │   │       └── components/ # RecordButton, AccuracyBar
│   │   ├── tooltip/            # 单词释义
│   │   │   ├── WordTooltip.tsx
│   │   │   └── useWordLookup.ts
│   │   ├── tts/                # TTS 管理
│   │   │   ├── useTTS.ts
│   │   │   └── ttsManager.ts   # 单例，管理播放队列
│   │   ├── asr/                # ASR 管理
│   │   │   ├── useASR.ts
│   │   │   └── scoring.ts      # 编辑距离评分
│   │   └── settings/
│   │       └── store.ts        # 设置项持久化
│   ├── shared/
│   │   ├── components/         # Button, Card, Modal, Loading
│   │   ├── hooks/              # useMMKV, useNetworkStatus
│   │   ├── types/              # Lesson, Manifest, Progress 类型
│   │   └── utils/              # hash, fileIO, scoring
│   └── native/                 # 原生模块 TS 桥接层
│       ├── piper.ts            # PiperTTS.speak(text, speed)
│       ├── whisper.ts          # Whisper.recognize(audioPath)
│       └── ecdict.ts           # Ecdict.lookup(word)
├── __tests__/                  # 测试
└── package.json
```

---

## 原生模块桥接

### 架构分层

```
┌─────────────────────────────┐
│     React Native (JS/TS)    │
│  ┌───────────────────────┐  │
│  │  src/native/*.ts      │  │  ← TS 封装层（统一 API）
│  └───────────┬───────────┘  │
│              │ NativeModules │
│  ┌───────────▼───────────┐  │
│  │  android/.../Module.kt│  │  ← Kotlin 原生实现
│  └───────────┬───────────┘  │
│              │ JNI           │
│  ┌───────────▼───────────┐  │
│  │  Piper/Whisper/ECDICT │  │  ← C/C++ 库
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### Piper TTS 模块

```typescript
// src/native/piper.ts
export interface PiperTTS {
  /** 初始化模型（首次从 assets 复制到内部存储） */
  init(): Promise<void>
  /** 合成语音并播放，返回 Promise（播放完成 resolve） */
  speak(text: string, speed?: number): Promise<void>
  /** 停止当前播放 */
  stop(): Promise<void>
  /** 模型是否就绪 */
  isReady(): Promise<boolean>
}
```

**Kotlin 实现要点：**
- 模型文件从 APK assets 复制到 `context.filesDir/piper/`（仅首次）
- `speak()` 在后台线程运行，合成 PCM → AudioTrack 播放
- 支持 speed 参数（0.5x-1.5x），通过调整 synthesis length scale 实现
- 同一时间只允许一个 `speak()` 调用，新调用自动取消上一个

### Whisper ASR 模块

```typescript
// src/native/whisper.ts
export interface WhisperASR {
  /** 初始化模型 */
  init(): Promise<void>
  /** 识别音频文件，返回文本 */
  recognize(audioPath: string): Promise<string>
  /** 模型是否就绪 */
  isReady(): Promise<boolean>
}
```

**Kotlin 实现要点：**
- 模型从 assets 复制到 `context.filesDir/whisper/`
- 音频录制为 WAV 16kHz mono，保存到 cache 目录
- `recognize()` 在后台线程运行，返回识别文本
- 识别完成后删除临时音频文件（隐私要求）

### ECDICT 词典模块

```typescript
// src/native/ecdict.ts
export interface EcdictEntry {
  word: string
  phonetic: string
  definition: string   // 中文释义
  pos: string          // 词性
  exchange: string     // 词形变化
}

export interface EcdictDB {
  /** 初始化（从 assets 复制 db 文件） */
  init(): Promise<void>
  /** 查询单词，返回 null 表示未找到 */
  lookup(word: string): Promise<EcdictEntry | null>
  /** 是否就绪 */
  isReady(): Promise<boolean>
}
```

**Kotlin 实现要点：**
- 使用 `expo-sqlite` 或原生 SQLite，db 文件从 assets 复制
- 查询语句：`SELECT * FROM stardict WHERE word = ? LIMIT 1`
- 预计查询 < 10ms（索引命中）

---

## 状态管理 (Zustand)

### Store 划分

```
useManifestStore    # 课程索引（manifest.json 解析结果）
useDownloadStore    # 下载队列和进度
useProgressStore    # 当前课程学习进度（MMKV 持久化）
useSettingsStore    # 应用设置（MMKV 持久化）
useTTSStore         # TTS 播放状态
```

### useProgressStore

```typescript
interface ProgressState {
  // 当前课程进度
  current: LessonProgress | null
  // 加载进度
  loadProgress: (lessonId: string) => void
  // 完成某个 Step
  completeStep: (step: number, score: number, timeSpent: number) => void
  // 标记课程完成
  markLessonCompleted: () => void
  // 重置进度（课程更新时）
  resetProgress: (lessonId: string) => void
}
```

**持久化策略：**
- 写入时机：每个 Step 完成时立即写入
- Key 格式：`progress:{lessonId}`
- 数据结构：`LessonProgress`（见 04-user-stories-module-b.md）
- 写入失败：静默降级，不崩溃

### useSettingsStore

```typescript
interface SettingsState {
  learningMode: 'linear' | 'free'
  ttsSpeed: 0.5 | 0.75 | 1.0 | 1.25 | 1.5
  speakingThreshold: number  // 40-90, default 60
  repoUrl: string
  setSetting: <K extends keyof SettingsState>(key: K, value: SettingsState[K]) => void
}
```

---

## 存储架构

```
┌─────────────────────────────────────────────┐
│                 App 存储层                    │
├──────────────┬──────────────────────────────┤
│   MMKV       │   文件系统                    │
│   (KV)       │   (课程 JSON + 图片)          │
├──────────────┼──────────────────────────────┤
│ settings     │ /files/lessons/lesson-1-1.json│
│ progress:*   │ /files/images/book-1/...png   │
│ manifest     │                               │
├──────────────┴──────────────────────────────┤
│   SQLite (expo-sqlite)                       │
│   /databases/ecdict.db                       │
├─────────────────────────────────────────────┤
│   Assets (APK 内置，首次启动复制)             │
│   ecdict.db / piper/ / whisper/              │
└─────────────────────────────────────────────┘
```

### 文件系统路径规范

| 数据 | 路径 | 大小 |
|------|------|------|
| 课程 JSON | `{filesDir}/lessons/lesson-{id}.json` | < 100KB/课 |
| 课程图片 | `{filesDir}/images/{path}` | 不定 |
| ECDICT | `{filesDir}/databases/ecdict.db` | ~10 MB |
| Piper 模型 | `{filesDir}/piper/` | ~20 MB |
| Whisper 模型 | `{filesDir}/whisper/` | ~75 MB |
| 临时音频 | `{cacheDir}/recording_*.wav` | 用完即删 |

---

## TTS 播放管理

### 单例 ttsManager

```typescript
class TTSManager {
  private currentSource: string | null = null
  private isPlaying = false

  /** 播放文本，自动取消上一个 */
  async speak(text: string, speed: number = 1.0): Promise<void> {
    if (this.isPlaying) await this.stop()
    this.currentSource = text
    this.isPlaying = true
    await PiperTTS.speak(text, speed)
    this.isPlaying = false
    this.currentSource = null
  }

  async stop(): Promise<void> {
    await PiperTTS.stop()
    this.isPlaying = false
  }
}

export const ttsManager = new TTSManager()
```

### useTTS Hook

```typescript
function useTTS() {
  const [isPlaying, setIsPlaying] = useState(false)
  const { ttsSpeed } = useSettingsStore()

  const speak = useCallback(async (text: string) => {
    setIsPlaying(true)
    await ttsManager.speak(text, ttsSpeed)
    setIsPlaying(false)
  }, [ttsSpeed])

  const stop = useCallback(() => ttsManager.stop(), [])

  return { speak, stop, isPlaying }
}
```

---

## 内容下载与缓存

### 下载流程

```
用户点击未下载课程
  │
  ▼
DownloadManager.download(lessonId, url, expectedHash)
  │
  ├─ HTTP GET → 临时文件
  │
  ├─ SHA256 校验
  │   ├─ 通过 → 移动到 lessons/ 目录 → 缓存 manifest 中的 hash
  │   └─ 失败 → 删除临时文件 → 重试（最多 3 次）
  │
  └─ 更新下载状态 → UI 回调
```

### 下载队列

```typescript
class DownloadManager {
  private queue: string[] = []
  private active: string | null = null

  enqueue(lessonId: string): void
  cancel(lessonId: string): void
  getStatus(lessonId: string): 'queued' | 'downloading' | 'completed' | 'failed'
}
```

- 串行下载（不并发）
- 支持取消（取消后从队列移除）
- 下载中断 → 下次从头重下（不做断点续传，课程文件小）

---

## 关键数据流

### 学习流程数据流

```
用户点击课程
  │
  ├─ 本地有缓存？
  │   ├─ 是 → loadProgress(lessonId)
  │   │       ├─ 有进度 → ResumeOverlay
  │   │       └─ 无进度 → Step 1
  │   └─ 否 → DownloadManager → 下载完成 → Step 1
  │
  ▼
Step 完成
  │
  ├─ completeStep(step, score, time)
  │   └─ MMKV 写入 progress:{lessonId}
  │
  ├─ step < 6 → 解锁 Step+1
  └─ step = 6 → markLessonCompleted()
```

### 词典查询数据流

```
用户点击单词
  │
  ▼
useWordLookup(word)
  │
  ├─ ECDICT 就绪？
  │   ├─ 是 → ecdict.lookup(word.toLowerCase())
  │   │       ├─ 找到 → 显示 tooltip
  │   │       └─ 未找到 → 显示「未找到」
  │   └─ 否 → 显示「词典加载中...」
```
