# Neo Concept — 工程化基建

> 状态：待用户确认
> 目标：定义 Android 项目结构、依赖版本、代码规范与资源目录约定，作为编码前的「跑道和护栏」。

---

## 1. 开发环境

| 项 | 版本/要求 |
|----|-----------|
| Android Studio | Ladybug 或更新 |
| Gradle | 8.7+ |
| Android Gradle Plugin | 8.5+ |
| compileSdk | 35 |
| targetSdk | 35 |
| minSdk | 26（Android 8.0）|
| Kotlin | 2.0+ |
| Java 兼容性 | 17 |

---

## 2. 核心依赖版本

```kotlin
// libs.versions.toml 示例
[versions]
agp = "8.5.0"
kotlin = "2.0.0"
coreKtx = "1.13.1"
lifecycle = "2.8.3"
activityCompose = "1.9.0"
composeBom = "2024.06.00"
navigation = "2.8.0-beta05"  // type-safe navigation
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"
datastore = "1.1.1"
room = "2.6.1"
coil = "2.7.0"
accompanistPermissions = "0.34.0"
timber = "5.0.1"
kotlinxSerialization = "1.7.0"
ktlint = "12.1.0"
detekt = "1.23.6"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
androidx-hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
accompanist-permissions = { group = "com.google.accompanist", name = "accompanist-permissions", version.ref = "accompanistPermissions" }
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref = "ktlint" }
detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

---

## 3. 项目目录结构

```
neo-concept-android/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/
│       │   │   └── content/
│       │   │       ├── manifest.json
│       │   │       └── books/
│       │   │           └── book01/
│       │   │               ├── book.json
│       │   │               └── lessons/
│       │   │                   └── L01/
│       │   │                       └── lesson.json
│       │   ├── java/com/neoconcept/
│       │   │   ├── NeoConceptApplication.kt
│       │   │   ├── di/
│       │   │   │   ├── AppModule.kt
│       │   │   │   └── EngineModule.kt
│       │   │   ├── ui/
│       │   │   │   ├── MainActivity.kt
│       │   │   │   ├── NeoConceptApp.kt          // Compose Navigation 根入口
│       │   │   │   ├── home/
│       │   │   │   ├── bookshelf/
│       │   │   │   ├── intro/
│       │   │   │   ├── lesson/
│       │   │   │   ├── completion/
│       │   │   │   ├── settings/
│       │   │   │   ├── components/              // 复用组件
│       │   │   │   └── theme/
│       │   │   ├── viewmodel/
│       │   │   ├── domain/
│       │   │   ├── data/
│       │   │   │   ├── content/
│       │   │   │   ├── progress/
│       │   │   │   ├── settings/
│       │   │   │   └── dictionary/
│       │   │   ├── engine/
│       │   │   │   ├── tts/
│       │   │   │   └── asr/
│       │   │   └── model/
│       │   └── res/
│       │       ├── values/
│       │       ├── drawable/
│       │       └── mipmap/
│       └── test/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── gradlew
└── README.md
```

---

## 4. 代码规范

### 4.1 命名约定

| 类型 | 规范 | 示例 |
|------|------|------|
| 包名 | 全小写 | `com.neoconcept.ui.home` |
| 类名 | PascalCase | `HomeViewModel` |
| 函数/变量 | camelCase | `loadBooks()` |
| 常量 | SCREAMING_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Compose 函数 | PascalCase | `HomeScreen()` |
| ViewModel 后缀 | `ViewModel` | `LessonViewModel` |
| Screen Composable | `Screen` 后缀 | `LessonScreen()` |

### 4.2 Compose 规范

- UI 状态统一从 `ViewModel` 的 `StateFlow` 收集。
- 事件回调使用 lambda，不在 Composable 中直接调用 Repository。
- 主题色、字号、间距统一从 `MaterialTheme` 读取，避免硬编码。
- 预览函数命名：`XXXScreenPreview` / `XXXComponentPreview`。

### 4.3 依赖注入规范

- Repository / UseCase / Engine 均通过 Hilt 注入。
- 接口优先：定义 `TtsEngine`、`AsrEngine` 接口，实现类注入。
- DataStore / Room Database 在 `AppModule` 中提供单例。

---

## 5. 静态代码检查

### 5.1 ktlint

- 自动格式化 Kotlin 代码。
- 提交前运行 `./gradlew ktlintFormat`。

### 5.2 detekt

- 静态代码分析，检测复杂度、潜在 Bug、代码异味。
- 配置 `detekt.yml`，禁用与 Compose 不友好的规则（如 `LongParameterList` 可适当放宽）。

### 5.3 CI 检查（可选）

```bash
./gradlew ktlintCheck detekt lintDebug testDebugUnitTest
```

---

## 6. Git 工作流

- 主分支：`main`
- 功能分支：`feature/module-name`
- 提交信息：
  - `feat: 添加首页书架 UI`
  - `fix: 修复填词练习状态丢失`
  - `refactor: 拆分 LessonViewModel`
  - `docs: 更新架构设计文档`

---

## 7. 资源目录约定

### 7.1 assets/content/

- 课程 JSON 必须按固定结构存放：
  - `manifest.json`
  - `books/{bookId}/book.json`
  - `books/{bookId}/lessons/{lessonCode}/lesson.json`
- 构建时通过 Gradle 任务校验目录完整性（可选）。

### 7.2 raw/

- Piper 语音模型、Whisper 模型、ECDICT 数据库过大，不放入 `res/raw`。
- v1.0 方案：首次启动时从 `assets` 或下载目录解压到应用私有目录。
- 具体模型文件管理策略在 [05-architecture.md](05-architecture.md) 中确认。

### 7.3 drawable/

- Banner 占位图：`drawable/banner_placeholder.xml`
- 错误/空状态图标使用 Material Icons Extended，减少自定义 drawable。

---

## 8. 日志与调试

- 使用 Timber 替代 `Log`。
- Debug 包开启完整日志；Release 包关闭或仅保留崩溃日志。
- 引擎初始化、JSON 解析、TTS/ASR 失败必须记录日志。

---

## 9. 安全与隐私

- 麦克风权限按需请求，并在隐私协议/设置中说明用途。
- 用户进度、查词历史仅存应用私有目录，不收集不上传。
- 网络请求仅用于 Banner 图片加载，不发送用户数据。

---

## 10. 待确认决策

1. **minSdk**：26（Android 8.0）是否可接受？是否需要支持到 24？
2. **静态检查**：是否同时启用 ktlint + detekt，还是只选其一？
3. **Compose Navigation 版本**：是否使用 2.8+ 的 type-safe navigation？（需要 Kotlin 2.0+）
4. **Room**：v1.0 是否必须使用 Room 管理查词历史，还是直接用 SQLite？
5. **模型文件位置**：Piper / Whisper / ECDICT 模型是打包进 assets，还是首次启动下载？
6. **日志上报**：Release 版是否需要接入 Firebase Crashlytics 等崩溃上报？
