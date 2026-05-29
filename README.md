# Neo Concept

离线优先、系统化英语学习工具。

## 技术栈

- **Android**: Kotlin + Jetpack Compose + Gradle KTS
- **iOS**: Swift + SwiftUI
- **数据库**: ECDICT SQLite 词典（20,000 词）
- **TTS**: Piper (ONNX)
- **ASR**: Whisper (GGML)

## 项目结构

- `android/` — Android 原生项目（待创建）
- `ios/` — iOS 原生项目（待创建）
- `src/` — 旧 RN 代码（Vibecoding 参考，不参与编译）
- `scripts/` — 工具脚本（ECDICT 裁剪）
- `assets/` — 共享资源（ecdict.db）
- `.agents/docs/` — 设计文档

## 构建

### Android

```sh
# 待 android/ 项目创建后：
# 用 Android Studio 打开 android/ 目录
# 或命令行构建
cd android && ./gradlew assembleDebug
```

### iOS

```sh
# 用 Xcode 打开 ios/ 目录
# 待创建
```

## 开源协议

GPL-3.0
