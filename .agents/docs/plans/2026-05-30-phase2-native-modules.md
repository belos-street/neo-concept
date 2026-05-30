# Phase 2: 原生模块实现计划

> **For agentic workers:** 按照任务顺序逐步实现，每完成一个步骤在 `[ ]` 内打 `x`。

**Goal:** 实现 ECDICT 词典查询、Piper TTS 语音合成、Whisper ASR 语音识别三个原生模块。

**Architecture:** 
- ECDICT: SQLiteOpenHelper + assets 复制 + FTS 搜索
- Piper TTS: ONNX Runtime JNI + AudioTrack 播放
- Whisper ASR: GGML JNI + MediaRecorder 录音
- 所有模块遵循 init() → isReady() → use() → cleanup() 生命周期

**Tech Stack:** Kotlin, SQLite, ONNX Runtime, GGML, AudioTrack, MediaRecorder

---

## 文件结构

```
android/app/src/main/java/com/neoconcept/
├── data/
│   ├── db/
│   │   └── EcdictDatabase.kt          # SQLiteOpenHelper 封装
│   └── repository/
│       └── EcdictRepository.kt         # 词典查询 Repository
├── audio/
│   ├── PiperTTS.kt                     # TTS 引擎封装
│   ├── TTSManager.kt                   # TTS 单例管理器
│   └── WhisperASR.kt                   # ASR 引擎封装
└── util/
    └── AssetUtils.kt                   # Assets 复制工具

android/app/src/main/assets/
├── ecdict.db                           # ECDICT 数据库文件
├── piper/
│   ├── model.onnx                      # Piper ONNX 模型
│   └── model.onnx.json                 # 模型配置
└── whisper/
    └── ggml-tiny.en.bin                # Whisper 模型文件
```

---

## Task 1: Asset 复制工具

**Files:**
- Create: `android/app/src/main/java/com/neoconcept/util/AssetUtils.kt`

- [ ] **Step 1: 创建 AssetUtils 工具类**

```kotlin
package com.neoconcept.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object AssetUtils {

    fun copyAssetToDir(context: Context, assetPath: String, targetDir: File): Boolean {
        try {
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val assetManager = context.assets
            val assets = assetManager.list(assetPath) ?: emptyArray()

            if (assets.isEmpty()) {
                copyAssetFile(context, assetPath, File(targetDir, assetPath))
            } else {
                for (asset in assets) {
                    val subDir = File(targetDir, asset)
                    copyAssetToDir(context, "$assetPath/$asset", subDir)
                }
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun copyAssetFile(context: Context, assetPath: String, targetFile: File) {
        val inputStream = context.assets.open(assetPath)
        val outputStream = FileOutputStream(targetFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    fun isAssetExists(context: Context, assetPath: String): Boolean {
        return try {
            context.assets.open(assetPath).close()
            true
        } catch (e: IOException) {
            false
        }
    }
}
```

- [ ] **Step 2: 验证编译通过**

Run: `cd android && .\gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add android/app/src/main/java/com/neoconcept/util/AssetUtils.kt
git commit -m "feat: add AssetUtils for copying assets to filesystem"
```

---

## Task 2: ECDICT 数据库模块

**Files:**
- Create: `android/app/src/main/java/com/neoconcept/data/db/EcdictDatabase.kt`
- Create: `android/app/src/main/java/com/neoconcept/data/model/VocabularyItem.kt`
- Create: `android/app/src/main/java/com/neoconcept/data/repository/EcdictRepository.kt`

- [ ] **Step 1: 创建 VocabularyItem 数据模型**

```kotlin
package com.neoconcept.data.model

data class VocabularyItem(
    val word: String,
    val phonetic: String,
    val definitionCn: String,
    val partOfSpeech: String,
    val example: String
)
```

- [ ] **Step 2: 创建 EcdictDatabase SQLiteOpenHelper**

```kotlin
package com.neoconcept.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.neoconcept.data.model.VocabularyItem
import com.neoconcept.util.AssetUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class EcdictDatabase(private val context: Context) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {

    companion object {
        private const val DB_NAME = "ecdict.db"
        private const val DB_VERSION = 1
        private const val ASSET_PATH = "ecdict.db"
        private const val TABLE_NAME = "stardict"
    }

    private var isInitialized = false

    suspend fun init() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        val dbFile = context.getDatabasePath(DB_NAME)
        if (!dbFile.exists()) {
            copyDatabaseFromAssets(dbFile)
        }

        isInitialized = true
    }

    private fun copyDatabaseFromAssets(dbFile: File) {
        dbFile.parentFile?.mkdirs()
        val inputStream = context.assets.open(ASSET_PATH)
        val outputStream = dbFile.outputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    fun isReady(): Boolean = isInitialized

    suspend fun lookup(word: String): VocabularyItem? = withContext(Dispatchers.IO) {
        if (!isInitialized) return@withContext null
        if (word.isBlank()) return@withContext null

        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf("word", "phonetic", "translation", "definition", "pos", "exchange"),
            "word = ? COLLATE NOCASE",
            arrayOf(word),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            if (it.moveToFirst()) {
                val word = it.getString(it.getColumnIndexOrThrow("word"))
                val phonetic = it.getString(it.getColumnIndexOrThrow("phonetic")) ?: ""
                val translation = it.getString(it.getColumnIndexOrThrow("translation")) ?: ""
                val definition = it.getString(it.getColumnIndexOrThrow("definition")) ?: ""
                val pos = it.getString(it.getColumnIndexOrThrow("pos")) ?: ""
                val exchange = it.getString(it.getColumnIndexOrThrow("exchange")) ?: ""

                VocabularyItem(
                    word = word,
                    phonetic = phonetic,
                    definitionCn = translation,
                    partOfSpeech = pos,
                    example = definition
                )
            } else {
                null
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}
```

- [ ] **Step 3: 创建 EcdictRepository**

```kotlin
package com.neoconcept.data.repository

import android.content.Context
import com.neoconcept.data.db.EcdictDatabase
import com.neoconcept.data.model.VocabularyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EcdictRepository(private val context: Context) {

    private val database = EcdictDatabase(context)

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    suspend fun init() {
        database.init()
        _isReady.value = database.isReady()
    }

    suspend fun lookup(word: String): VocabularyItem? {
        return database.lookup(word)
    }

    fun isReady(): Boolean = database.isReady()
}
```

- [ ] **Step 4: 验证编译通过**

Run: `cd android && .\gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add android/app/src/main/java/com/neoconcept/data/model/VocabularyItem.kt
git add android/app/src/main/java/com/neoconcept/data/db/EcdictDatabase.kt
git add android/app/src/main/java/com/neoconcept/data/repository/EcdictRepository.kt
git commit -m "feat: implement ECDICT database module with SQLite"
```

---

## Task 3: Piper TTS 模块

**Files:**
- Create: `android/app/src/main/java/com/neoconcept/audio/PiperTTS.kt`
- Create: `android/app/src/main/java/com/neoconcept/audio/TTSManager.kt`
- Modify: `android/app/build.gradle.kts` (添加 ONNX Runtime 依赖)

- [ ] **Step 1: 添加 ONNX Runtime 依赖**

在 `android/app/build.gradle.kts` 的 dependencies 块中添加:

```kotlin
implementation("com.microsoft.onnxruntime:onnxruntime-android:1.16.0")
```

- [ ] **Step 2: 创建 PiperTTS 类**

```kotlin
package com.neoconcept.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.neoconcept.util.AssetUtils
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.FloatBuffer

class PiperTTS(private val context: Context) {

    companion object {
        private const val ASSET_PATH = "piper"
        private const val MODEL_FILE = "model.onnx"
        private const val SAMPLE_RATE = 22050
    }

    private var ortEnv: OrtEnvironment? = null
    private var ortSession: OrtSession? = null
    private var isInitialized = false
    private var isSpeaking = false
    private var audioTrack: AudioTrack? = null

    suspend fun init() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        val modelDir = File(context.filesDir, "piper")
        if (!modelDir.exists()) {
            AssetUtils.copyAssetToDir(context, ASSET_PATH, modelDir)
        }

        ortEnv = OrtEnvironment.getEnvironment()
        val modelPath = File(modelDir, MODEL_FILE).absolutePath
        ortSession = ortEnv?.createSession(modelPath)

        isInitialized = true
    }

    fun isReady(): Boolean = isInitialized

    suspend fun speak(text: String, speed: Float = 1.0f) = withContext(Dispatchers.IO) {
        if (!isInitialized || text.isBlank()) return@withContext

        stop()
        isSpeaking = true

        try {
            val pcmData = synthesize(text, speed)
            playAudio(pcmData)
        } finally {
            isSpeaking = false
        }
    }

    private fun synthesize(text: String, speed: Float): FloatArray {
        val session = ortSession ?: throw IllegalStateException("PiperTTS not initialized")
        val env = ortEnv ?: throw IllegalStateException("PiperTTS not initialized")

        val textTensor = OnnxTensor.createTensor(env, arrayOf(text))
        val speedTensor = OnnxTensor.createTensor(env, floatArrayOf(speed))

        val inputs = mapOf("text" to textTensor, "speed" to speedTensor)
        val results = session.run(inputs)

        val audioTensor = results[0].value as Array<FloatArray>
        return audioTensor[0]
    }

    private fun playAudio(pcmData: FloatArray) {
        val bufferSize = pcmData.size * 2
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        val shortBuffer = ShortArray(pcmData.size) { i ->
            (pcmData[i] * 32767).toInt().toShort()
        }

        audioTrack?.write(shortBuffer, 0, shortBuffer.size)
    }

    fun stop() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        isSpeaking = false
    }

    fun isSpeaking(): Boolean = isSpeaking

    fun release() {
        stop()
        ortSession?.close()
        ortEnv?.close()
        isInitialized = false
    }
}
```

- [ ] **Step 3: 创建 TTSManager 单例**

```kotlin
package com.neoconcept.audio

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object TTSManager {

    private var piperTTS: PiperTTS? = null
    private var currentJob: Job? = null

    fun init(context: Context) {
        piperTTS = PiperTTS(context)
    }

    suspend fun initModel() {
        piperTTS?.init()
    }

    fun isReady(): Boolean = piperTTS?.isReady() == true

    fun speak(text: String, speed: Float = 1.0f) {
        stop()
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            piperTTS?.speak(text, speed)
        }
    }

    fun stop() {
        currentJob?.cancel()
        piperTTS?.stop()
    }

    fun isSpeaking(): Boolean = piperTTS?.isSpeaking() == true

    fun release() {
        stop()
        piperTTS?.release()
        piperTTS = null
    }
}
```

- [ ] **Step 4: 验证编译通过**

Run: `cd android && .\gradlew assembleDebug`
Expected: BUILD SUCCESSFUL (注意：ONNX Runtime 依赖可能需要网络下载)

- [ ] **Step 5: Commit**

```bash
git add android/app/build.gradle.kts
git add android/app/src/main/java/com/neoconcept/audio/PiperTTS.kt
git add android/app/src/main/java/com/neoconcept/audio/TTSManager.kt
git commit -m "feat: implement Piper TTS module with ONNX Runtime"
```

---

## Task 4: Whisper ASR 模块

**Files:**
- Create: `android/app/src/main/java/com/neoconcept/audio/WhisperASR.kt`
- Modify: `android/app/build.gradle.kts` (添加 GGML 依赖)

- [ ] **Step 1: 添加 GGML 依赖**

在 `android/app/build.gradle.kts` 的 dependencies 块中添加:

```kotlin
implementation("com.nickkoro02:whisper-android:1.0.0")
```

注意：如果上述库不可用，需要使用其他 Whisper Android 库或自行编译 JNI。

- [ ] **Step 2: 创建 WhisperASR 类**

```kotlin
package com.neoconcept.audio

import android.content.Context
import android.media.MediaRecorder
import com.neoconcept.util.AssetUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class WhisperASR(private val context: Context) {

    companion object {
        private const val ASSET_PATH = "whisper"
        private const val MODEL_FILE = "ggml-tiny.en.bin"
        private const val SAMPLE_RATE = 16000
        private const val MAX_RECORDING_DURATION = 30000L
    }

    private var isInitialized = false
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    suspend fun init() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        val modelDir = File(context.filesDir, "whisper")
        if (!modelDir.exists()) {
            AssetUtils.copyAssetToDir(context, ASSET_PATH, modelDir)
        }

        isInitialized = true
    }

    fun isReady(): Boolean = isInitialized

    suspend fun recognize(audioPath: String): String = withContext(Dispatchers.IO) {
        if (!isInitialized) return@withContext ""

        val audioFile = File(audioPath)
        if (!audioFile.exists()) return@withContext ""

        try {
            val result = processAudio(audioFile)
            audioFile.delete()
            result
        } catch (e: Exception) {
            audioFile.delete()
            ""
        }
    }

    private fun processAudio(audioFile: File): String {
        return "识别结果占位符"
    }

    fun startRecording(): String? {
        val outputFile = File(context.cacheDir, "recording_${System.currentTimeMillis()}.wav")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(SAMPLE_RATE)
            setOutputFile(outputFile.absolutePath)

            try {
                prepare()
                start()
                isRecording = true
                return outputFile.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaRecorder = null
        isRecording = false
    }

    fun isRecording(): Boolean = isRecording

    fun release() {
        if (isRecording) {
            stopRecording()
        }
        isInitialized = false
    }
}
```

- [ ] **Step 3: 验证编译通过**

Run: `cd android && .\gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add android/app/build.gradle.kts
git add android/app/src/main/java/com/neoconcept/audio/WhisperASR.kt
git commit -m "feat: implement Whisper ASR module"
```

---

## Task 5: 集成到 MainActivity

**Files:**
- Modify: `android/app/src/main/java/com/neoconcept/MainActivity.kt`

- [ ] **Step 1: 在 MainActivity 中初始化模块**

在 `MainActivity.kt` 的 `onCreate` 方法中添加初始化逻辑:

```kotlin
import com.neoconcept.audio.TTSManager
import com.neoconcept.audio.WhisperASR
import com.neoconcept.data.repository.EcdictRepository

class MainActivity : ComponentActivity() {

    private lateinit var ecdictRepository: EcdictRepository
    private lateinit var whisperASR: WhisperASR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ecdictRepository = EcdictRepository(this)
        whisperASR = WhisperASR(this)
        TTSManager.init(this)

        lifecycleScope.launch {
            ecdictRepository.init()
            TTSManager.initModel()
            whisperASR.init()
        }

        enableEdgeToEdge()
        setContent {
            NeoConceptTheme {
                AppNavigation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TTSManager.release()
        whisperASR.release()
    }
}
```

- [ ] **Step 2: 验证编译通过**

Run: `cd android && .\gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add android/app/src/main/java/com/neoconcept/MainActivity.kt
git commit -m "feat: integrate native modules into MainActivity"
```

---

## Task 6: 集成测试准备

**Files:**
- Create: `android/app/src/main/assets/ecdict.db` (占位文件)
- Create: `android/app/src/main/assets/piper/model.onnx` (占位文件)
- Create: `android/app/src/main/assets/whisper/ggml-tiny.en.bin` (占位文件)

- [ ] **Step 1: 创建 assets 目录结构**

```bash
mkdir -p android/app/src/main/assets/piper
mkdir -p android/app/src/main/assets/whisper
```

- [ ] **Step 2: 创建占位文件**

由于模型文件较大，先创建占位文件用于编译测试:

```bash
echo "placeholder" > android/app/src/main/assets/ecdict.db
echo "placeholder" > android/app/src/main/assets/piper/model.onnx
echo "placeholder" > android/app/src/main/assets/whisper/ggml-tiny.en.bin
```

- [ ] **Step 3: 验证编译通过**

Run: `cd android && .\gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add android/app/src/main/assets/
git commit -m "chore: add placeholder asset files for testing"
```

---

## Task 7: 真机验证

- [ ] **Step 1: 安装到模拟器**

```bash
cd android
.\gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

- [ ] **Step 2: 启动应用并检查 Logcat**

```bash
adb shell am start -n com.neoconcept/.MainActivity
adb logcat -s "com.neoconcept" | grep -E "(Ecdict|PiperTTS|WhisperASR)"
```

Expected: 无崩溃，模块初始化日志正常

- [ ] **Step 3: 验证 ECDICT 查询**

在应用中触发 ECDICT 查询（需要后续 UI 集成），检查 Logcat 输出。

- [ ] **Step 4: Commit 测试结果**

```bash
git add .
git commit -m "test: verify native modules on emulator"
```

---

## 后续任务

完成以上任务后，需要:

1. **替换占位模型文件**: 将真实的 ECDICT 数据库、Piper ONNX 模型、Whisper GGML 模型放入 assets 目录
2. **实现 JNI 绑定**: 对于 Piper TTS 和 Whisper ASR，需要实现真正的 JNI 调用
3. **UI 集成**: 将模块功能集成到各个 Step 页面中
4. **性能优化**: 模型预热、缓存策略等

---

## 依赖关系图

```
Task 1: AssetUtils
    ↓
Task 2: ECDICT Module
    ↓
Task 3: Piper TTS Module
    ↓
Task 4: Whisper ASR Module
    ↓
Task 5: MainActivity Integration
    ↓
Task 6: Asset Files Setup
    ↓
Task 7: Emulator Verification
```

---

## 风险与注意事项

1. **模型文件大小**: ECDICT (~5MB), Piper (~20MB), Whisper (~75MB) 总计约 100MB，会影响 APK 大小
2. **JNI 兼容性**: 需要确保 ONNX Runtime 和 GGML 库支持目标架构 (arm64-v8a, armeabi-v7a)
3. **内存占用**: 模型加载会占用较多内存，需要在 low-memory 设备上测试
4. **权限**: 需要 RECORD_AUDIO 权限用于录音功能
