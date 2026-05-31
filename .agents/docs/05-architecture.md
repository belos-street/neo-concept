# 05 — 系统架构设计

> 所属：Neo Concept 结构化需求文档 | Vibe-Flow Stage 5

---

## 技术栈总览

| 层次 | 技术选型 | 用途 |
|------|---------|------|
| 语言 | Kotlin | Android 原生开发 |
| UI 框架 | Jetpack Compose (Material 3) | 声明式 UI |
| 导航 | Navigation 3 | Tab + Stack 页面路由 |
| 状态管理 | ViewModel + StateFlow | 响应式状态 |
| KV 存储 | DataStore Preferences | 进度/设置持久化 |
| SQLite | SQLiteOpenHelper (原生) | ECDICT 词典查询 |
| YAML 解析 | SnakeYAML | 课程数据解析 |
| TTS | Piper (kathleen-low) | 离线语音合成 |
| ASR | Whisper.cpp (tiny.en) | 离线语音识别 |
| 构建工具 | Gradle (Kotlin DSL) | 项目构建 |

> **Android 优先开发**，iOS 版本后续通过 Vibe Coding 复刻。

### Android Gradle 依赖

| 依赖 | artifact | 用途 |
|------|----------|------|
| Compose BOM | `androidx.compose:compose-bom:2025.05.00` | 统一 Compose 库版本 |
| Material 3 | `androidx.compose.material3:material3` | UI 组件库 |
| Navigation 3 | `androidx.navigation3:navigation3-runtime` | Tab + Stack 导航 |
| ViewModel | `androidx.lifecycle:lifecycle-viewmodel-compose` | 状态管理 |
| DataStore | `androidx.datastore:datastore-preferences` | KV 持久化 |
| Coroutines | `org.jetbrains.kotlinx:kotlinx-coroutines-android` | 异步操作 |
| OkHttp | `com.squareup.okhttp3:okhttp:4.12.0` | HTTP 下载 |
| SnakeYAML | `org.yaml:snakeyaml:2.2` | YAML 解析 |
| SQLite | Android 原生 (`android.database.sqlite`) | ECDICT 词典查询 |
| Piper TTS | JNI (ONNX Runtime) | 离线语音合成 |
| Whisper ASR | JNI (GGML) | 离线语音识别 |

> compileSdk = 37, minSdk = 26, targetSdk = 37, Kotlin = 2.1.x
>
> **版本兼容性说明**：minSdk = 26（Android 8.0 Oreo）确保覆盖 95%+ 的活跃设备，同时支持 DataStore、Navigation 3 等现代 Jetpack 库。

---

## 项目目录结构

### Android (Kotlin)

```
neo-concept/
├── android/                        # Android 原生项目
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── assets/
│   │   │   │   ├── ecdict.db       # ECDICT SQLite (~5 MB)
│   │   │   │   ├── piper/          # Piper TTS 模型 (~20 MB)
│   │   │   │   └── whisper/        # Whisper 模型 (~75 MB)
│   │   │   ├── java/com/neoconcept/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   └── AppNavigation.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── CourseListScreen.kt
│   │   │   │   │   ├── DownloadScreen.kt
│   │   │   │   │   ├── LessonScreen.kt
│   │   │   │   │   ├── StatsScreen.kt
│   │   │   │   │   └── SettingsScreen.kt
│   │   │   │   ├── features/
│   │   │   │   │   ├── lesson/
│   │   │   │   │   │   ├── PassageStep.kt
│   │   │   │   │   │   ├── FillBlanksStep.kt
│   │   │   │   │   │   ├── VocabExerciseStep.kt
│   │   │   │   │   │   ├── ListeningStep.kt
│   │   │   │   │   │   ├── ReadingStep.kt
│   │   │   │   │   │   ├── SpeakingStep.kt
│   │   │   │   │   │   ├── WordTooltip.kt
│   │   │   │   │   │   └── StepProgressBar.kt
│   │   │   │   │   ├── course/
│   │   │   │   │   │   ├── CourseViewModel.kt
│   │   │   │   │   │   └── DownloadManager.kt
│   │   │   │   │   └── settings/
│   │   │   │   │       └── SettingsViewModel.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── db/
│   │   │   │   │   │   └── EcdictDatabase.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── EcdictRepository.kt
│   │   │   │   │   │   ├── ProgressRepository.kt
│   │   │   │   │   │   └── SettingsRepository.kt
│   │   │   │   │   └── model/
│   │   │   │   │       ├── Lesson.kt
│   │   │   │   │       ├── VocabularyItem.kt
│   │   │   │   │       └── Manifest.kt
│   │   │   │   ├── audio/
│   │   │   │   │   ├── PiperTTS.kt
│   │   │   │   │   ├── TTSManager.kt
│   │   │   │   │   └── WhisperASR.kt
│   │   │   │   └── theme/
│   │   │   │       └── Theme.kt
│   │   │   └── res/
│   │   └── build.gradle.kts
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── scripts/                        # 工具脚本
│   └── trim_ecdict.py              # ECDICT 数据库裁剪
└── assets/                         # 源资源（不打包）
    └── stardict.db                 # ECDICT 完整数据库
```

---

## 核心模块

### Piper TTS

**接口（Android Kotlin）：**

```kotlin
class PiperTTS(private val context: Context) {
    suspend fun init()             // 从 assets 复制模型到 filesDir（仅首次）
    suspend fun speak(text: String, speed: Float = 1.0f)  // 合成并播放
    fun stop()                     // 停止当前播放
    fun isReady(): Boolean         // 模型是否就绪
}
```

**实现要点：**
- 模型文件从 APK assets 复制到 `context.filesDir/piper/`（仅首次）
- `speak()` 在后台协程运行，合成 PCM → AudioTrack 播放
- 支持 speed 参数（0.5x-1.5x），通过调整 synthesis length scale 实现
- 同一时间只允许一个 `speak()` 调用，新调用自动取消上一个

### Whisper ASR

**接口（Android Kotlin）：**

```kotlin
class WhisperASR(private val context: Context) {
    suspend fun init()                             // 从 assets 复制模型
    suspend fun recognize(audioPath: String): String  // 识别音频文件，返回文本
    fun isReady(): Boolean
}
```

**实现要点：**
- 模型从 assets 复制到 `context.filesDir/whisper/`
- 音频录制为 WAV 16kHz mono，保存到 cache 目录
- `recognize()` 在后台协程运行，返回识别文本
- 识别完成后删除临时音频文件（隐私要求）

### ECDICT 词典

**接口（Android Kotlin）：**

```kotlin
class EcdictDatabase(private val context: Context) {
    suspend fun init()                                        // 从 assets 复制 db
    suspend fun lookup(word: String): VocabularyItem?         // 查询单词
    fun isReady(): Boolean
}

data class VocabularyItem(
    val word: String,
    val phonetic: String,
    val definitionCn: String,    // 中文释义（translation 字段）
    val partOfSpeech: String,    // 词性（n. / v. / adj. 等）
    val example: String          // 英文例句
)
```

**实现要点：**
- 使用原生 SQLiteOpenHelper，db 文件从 assets 复制到 `databases/`
- 查询语句：`SELECT word, phonetic, translation, definition, pos, exchange FROM stardict WHERE word = ? COLLATE NOCASE LIMIT 1`
- `translation` 字段为中文释义，`definition` 字段为英文释义
- 预计查询 < 10ms（索引命中）

---

## 状态管理

### Android: ViewModel + StateFlow

```kotlin
// 课程进度 ViewModel
class LessonProgressViewModel : ViewModel() {
    private val _progress = MutableStateFlow<LessonProgress?>(null)
    val progress: StateFlow<LessonProgress?> = _progress.asStateFlow()

    fun loadProgress(lessonId: String)    // 从 DataStore 读取
    fun completeStep(step: Int, score: Int, timeSpent: Long)
    fun markLessonCompleted()
    fun resetProgress(lessonId: String)
}

// 设置 ViewModel
class SettingsViewModel : ViewModel() {
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    fun <T> setSetting(key: String, value: T)
}

data class Settings(
    val learningMode: String = "linear",    // "linear" | "free"
    val ttsSpeed: Float = 1.0f,             // 0.5 - 1.5
    val speakingThreshold: Int = 60         // 40-90
)
```

**持久化策略：**
- 写入时机：每个 Step 完成时立即写入 DataStore
- Key 格式：`progress:{lessonId}`
- 写入失败：静默降级，不崩溃

---

## 存储架构

```
┌─────────────────────────────────────────────┐
│                 App 存储层                    │
├──────────────┬──────────────────────────────┤
│  DataStore   │   文件系统                    │
│  (KV)        │   (课程 YAML)                │
├──────────────┼──────────────────────────────┤
│ settings     │ /files/lessons/lesson-1-1.yaml│
│ progress:*   │                               │
│ manifest     │                               │
├──────────────┴──────────────────────────────┤
│   SQLite                                    │
│   /databases/ecdict.db                      │
├─────────────────────────────────────────────┤
│   Assets (APK 内置，首次启动复制)             │
│   ecdict.db / piper/ / whisper/              │
└─────────────────────────────────────────────┘
```

### 文件系统路径规范

| 数据 | 路径 | 大小 |
|------|------|------|
| 课程 YAML | `{filesDir}/lessons/lesson-{id}.yaml` | < 100KB/课 |
| 课程图片 | HTTPS URL（远程仓库） | 按需加载缓存 |
| ECDICT | `{filesDir}/databases/ecdict.db` | ~5 MB |
| Piper 模型 | `{filesDir}/piper/` | ~20 MB |
| Whisper 模型 | `{filesDir}/whisper/` | ~75 MB |
| 临时音频 | `{cacheDir}/recording_*.wav` | 用完即删 |

### 课程图片资源

课程配图使用 HTTPS URL，存储在独立的课程资源仓库：
- 仓库：`https://github.com/neo-concept/assets`
- 格式：`https://raw.githubusercontent.com/neo-concept/assets/main/images/{book}/{unit}/{image}`
- App 端按需下载并缓存到本地

---

## TTS 播放管理

### 单例 TTSManager

```kotlin
object TTSManager {
    private var isPlaying = false
    private var currentJob: Job? = null

    suspend fun speak(text: String, speed: Float = 1.0f) {
        stop()
        currentJob = coroutineScope {
            launch(Dispatchers.IO) {
                isPlaying = true
                PiperTTS.speak(text, speed)
                isPlaying = false
            }
        }
    }

    fun stop() {
        currentJob?.cancel()
        PiperTTS.stop()
        isPlaying = false
    }
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
  │   └─ DataStore 写入 progress:{lessonId}
  │
  ├─ step < 6 → 解锁 Step+1
  └─ step = 6 → markLessonCompleted()
```

### 词典查询数据流

```
用户点击单词
  │
  ▼
EcdictRepository.lookup(word)
  │
  ├─ ECDICT 就绪？
  │   ├─ 是 → db.lookup(word.lowercase())
  │   │       ├─ 找到 → 显示 WordTooltip
  │   │       └─ 未找到 → 显示「未找到」
  │   └─ 否 → 显示「词典加载中...」
```
