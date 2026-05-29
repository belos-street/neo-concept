# 12 — 原生模块桥接手動测试用例

> Sprint 2 测试文档。先测，再 Merge 代码。
> 每完成一条在 `[ ]` 内打 `x`。

---

## A — 前置条件

- [ ] Android 模拟器或真机已连接 `adb devices`
- [ ] Android Studio 项目已打开，Gradle 同步完成
- [ ] App 已安装到设备（Run from Android Studio）
- [ ] Logcat 打开，过滤 `com.neoconcept`，确认无崩溃

---

## B — ECDICT 词典模块（2.1 + 2.2）

### B1 模块加载

- [ ] **B1.1** 应用启动后无 ECDICT 相关崩溃
- [ ] **B1.2** Logcat 无 `EcdictDatabase` 相关报错

### B2 初始化

- [ ] **B2.1** 首次启动：`ecdict.db` 从 assets 复制到 `filesDir/ecdict/`（通过 `adb shell ls /data/data/com.neoconcept/files/ecdict/` 确认）
- [ ] **B2.2** 二次启动：不重复复制（文件已存在，跳过）
- [ ] **B2.3** `isReady()` 初始化前返回 `false`，初始化后返回 `true`

### B3 查询功能

- [ ] **B3.1** 查找单词 `hello` → 返回 `{ word: "hello", phonetic: "...", meaning: "int. 你好", ... }`
- [ ] **B3.2** 查找单词 `apple` → 返回含 `n. 苹果` 释义的记录
- [ ] **B3.3** 查找单词 `BEAUTIFUL`（全大写）→ 正常返回结果（大小写不敏感）
- [ ] **B3.4** 查找单词 `don't`（含撇号）→ 正常返回
- [ ] **B3.5** 查找不存在的词 `xyzzy123` → 返回 `null`
- [ ] **B3.6** 查找空字符串 `""` → 返回 `null`，不崩溃

### B4 性能

- [ ] **B4.1** 连续查询 10 个单词，单次耗时 < 10ms（通过 adb logcat 观察）

### B5 错误处理

- [ ] **B5.1** 数据库文件损坏 → 重新复制 assets 中的原始 db（或优雅提示）
- [ ] **B5.2** 初始化前调用 `lookup()` → 返回 `null`，不崩溃

---

## C — Piper TTS 语音合成（2.3 + 2.4）

### C1 模块加载

- [ ] **C1.1** 应用启动后无 TTS 相关崩溃
- [ ] **C1.2** Logcat 无 `PiperTTS` 相关报错

### C2 初始化

- [ ] **C2.1** 首次启动：模型文件从 assets 复制到 `filesDir/piper/`（`adb shell ls /data/data/com.neoconcept/files/piper/`）
- [ ] **C2.2** 二次启动：不重复复制
- [ ] **C2.3** `isReady()` 初始化前 `false` → 初始化后 `true`

### C3 播放功能

- [ ] **C3.1** `speak("Hello world")` → 扬声器正常播放英文语音，音质可辨识
- [ ] **C3.2** `speak("The quick brown fox jumps over the lazy dog")` → 长句完整播完
- [ ] **C3.3** `speak("")`（空字符串）→ 不播放，不崩溃，Promise resolve

### C4 速度控制

- [ ] **C4.1** `speak("Hello", 0.5)` → 明显慢速播放
- [ ] **C4.2** `speak("Hello", 1.0)` → 正常速度
- [ ] **C4.3** `speak("Hello", 1.5)` → 明显快速播放
- [ ] **C4.4** `speak("Hello", 2.0)`（超范围）→ 不崩溃，用 1.5x 上限播放

### C5 并发控制

- [ ] **C5.1** `speak()` 播放中调用第二次 `speak()` → 停止前一个，播放新内容（无缝切换）
- [ ] **C5.2** 连续快速调用 `speak()` 3 次 → 只播放最后一个，不产生叠加噪音

### C6 停止功能

- [ ] **C6.1** 播放中 `stop()` → 立即静音
- [ ] **C6.2** 未播放时 `stop()` → 不崩溃
- [ ] **C6.3** `stop()` 后调用 `speak()` → 正常播放新内容

### C7 生命周期

- [ ] **C7.1** App 切到后台 → 停止播放（静音）
- [ ] **C7.2** App 回到前台 → 不自动恢复播放，可重新调用 `speak()`

---

## D — Whisper ASR 语音识别（2.5 + 2.6）

### D1 模块加载

- [ ] **D1.1** 应用启动后无 ASR 相关崩溃
- [ ] **D1.2** Logcat 无 `WhisperASR` 相关报错

### D2 初始化

- [ ] **D2.1** 首次启动：模型从 assets 复制到 `filesDir/whisper/`（`adb shell ls /data/data/com.neoconcept/files/whisper/`）
- [ ] **D2.2** 二次启动：不重复复制
- [ ] **D2.3** `isReady()` 初始化前 `false` → 初始化后 `true`

### D3 识别功能

- [ ] **D3.1** 录制 3 秒英文语音 "hello world" → 返回 `{ text: "hello world", confidence: > 0.8 }`
- [ ] **D3.2** 录制 5 秒长句 → 完整识别，无明显截断
- [ ] **D3.3** 静默录音（空环境）→ 返回 `""` 或置信度极低的结果（< 0.3）
- [ ] **D3.4** 短录音（< 1 秒）→ 不崩溃，提示太短

### D4 隐私要求

- [ ] **D4.1** `recognize()` 完成后 → 临时 WAV 文件被删除（`adb shell ls` 确认 cache 目录无残留）
- [ ] **D4.2** 调用 `recognize()` 时传入不存在的文件路径 → 返回错误，不崩溃

### D5 录音交互

- [ ] **D5.1** 按住录音按钮 → 开始录音，按钮状态变化
- [ ] **D5.2** 松手 → 停止录音，开始识别
- [ ] **D5.3** 按住超过 30 秒 → 自动停止录音
- [ ] **D5.4** 录音中松开后立刻再按住 → 正常开始新录音
- [ ] **D5.5** 识别中有新的录音请求 → 取消当前识别，开始新录音

---

## E — 集成测试（2.7）

### E1 Piper + Whisper 回声测试

- [ ] **E1.1** TTS 播报 "The cat sat on the mat" → 录音 3 秒 → ASR 识别 → 识别文本与原文基本匹配（confidence > 0.6）
- [ ] **E1.2** TTS 播报长句（15 词以上）→ 录音 → 识别 → 内容完整，无明显漏词
- [ ] **E1.3** TTS 以 0.5x 慢速播放 → 录音 → 识别准确率高于常速

### E2 ECDICT + WordTooltip 集成

- [ ] **E2.1** Step 1 课文中点击单词 → 弹出 WordTooltip → 显示 ECDICT 查询结果（音标 + 释义）
- [ ] **E2.2** 点击非常见词（如特定术语）→ WordTooltip 显示「未找到」
- [ ] **E2.3** ECDICT 初始化完成前点击单词 → WordTooltip 显示「加载中…」

### E3 全模块并发

- [ ] **E3.1** TTS 播放中同时进行 ECDICT 查询 → 两者互不干扰
- [ ] **E3.2** ASR 识别中同时进行 ECDICT 查询 → 两者互不干扰
- [ ] **E3.3** 三个模块全部初始化完成后 → 依次调用各功能，各自正常工作

---

## F — 性能基线

| 指标 | 目标 | 实测 | 结果 |
|------|------|:----:|:----:|
| ECDICT 单次查询 | < 10ms | ____ms | ⬜ |
| TTS 首次播放延迟 | < 500ms | ____ms | ⬜ |
| TTS 速度切换生效 | 即时 | ____ | ⬜ |
| ASR 识别（3s 音频） | < 2s | ____s | ⬜ |
| 模型首次复制耗时 | 视大小而定 | ____s | ⬜ |

---

## G — 边界 / 异常情况

- [ ] **G1** 存储空间不足时复制模型 → 提示空间不足，不崩溃
- [ ] **G2** assets 中缺少模型文件 → 初始化失败，错误信息明确
- [ ] **G3** 连续高频调用 `lookup()`（100 次/秒）→ 不卡死 UI 线程
- [ ] **G4** TTS `speak()` 传入超长文本（> 500 字符）→ 正常播放不截断
- [ ] **G5** 三个模块依次 `init()` → `deinit()`（无此 API）→ `init()` 第二次，不重复复制