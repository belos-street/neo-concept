# 09 — 任务拆解

> 所属：Neo Concept 结构化需求文档 | Vibe-Flow Stage 9

---

## 拆解原则

- 单个任务 ≤ 4 小时
- 每个任务可独立提交
- 按依赖顺序排列，有依赖的任务标 `blockedBy`
- P0 = MVP 必须，P1 = 重要，P2 = 增强

---

## Sprint 1：Android 项目脚手架 & 基础设施

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 1.1 | Android 项目初始化（Kotlin + Compose + Gradle KTS） | P0 | 2h | — | 可运行的空项目 |
| 1.2 | Navigation Compose 配置（Tab + Stack 路由） | P0 | 2h | 1.1 | 导航骨架 |
| 1.3 | DataStore Preferences 封装（设置 + 进度读写） | P0 | 2h | 1.1 | SettingsRepository + ProgressRepository |
| 1.4 | 共享组件库（Button, Card, Modal, Loading — Compose） | P0 | 3h | 1.1 | 基础 UI 组件 |
| 1.5 | Theme 定义（颜色、字体、间距 — Swiss International 风格） | P0 | 2h | 1.1 | Theme.kt |

---

## Sprint 2：原生模块

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 2.1 | ECDICT 模块 — SQLite 初始化 + assets 复制 + 查询 | P0 | 3h | 1.1 | EcdictDatabase.lookup() 可调用 |
| 2.2 | ECDICT Repository + VocabularyItem 数据模型 | P0 | 2h | 2.1 | EcdictRepository |
| 2.3 | Piper TTS 模块 — 模型初始化 + PCM 合成 + AudioTrack 播放 | P0 | 4h | 1.1 | PiperTTS.speak() 可调用 |
| 2.4 | TTSManager 单例（播放队列 + 速度控制 + 并发管理） | P0 | 2h | 2.3 | TTSManager |
| 2.5 | Whisper ASR 模块 — 模型初始化 + 录音 + 识别 | P0 | 4h | 1.1 | WhisperASR.recognize() 可调用 |
| 2.6 | 原生模块集成测试（手动验证 TTS/ASR/ECDICT） | P0 | 2h | 2.2, 2.4, 2.5 | 三个模块可正常工作 |

---

## Sprint 3：课程管理 (Module A)

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 3.1 | Manifest 拉取 + 解析 + 本地缓存 | P0 | 3h | 1.3 | ManifestRepository |
| 3.2 | 课程列表页 — Book/Unit/Lesson 树形展开（Compose） | P0 | 4h | 3.1, 1.4 | CourseListScreen |
| 3.3 | 课程状态标识（已下载/未下载/锁定/有更新） | P0 | 2h | 3.2 | 状态图标渲染 |
| 3.4 | 下载管理器（串行队列 + Hash 校验 + 取消） | P0 | 4h | 3.1 | DownloadManager |
| 3.5 | 下载进度页 UI | P0 | 2h | 3.4 | DownloadScreen |
| 3.6 | 首次启动引导（URL 输入 + manifest 拉取） | P0 | 2h | 3.1 | 引导弹窗 |
| 3.7 | 错误处理（无网络/解析失败/存储不足） | P0 | 2h | 3.2, 3.4 | 错误提示 UI |
| 3.8 | 检查更新 + 增量下载 | P1 | 3h | 3.1, 3.4 | 更新流程 |

---

## Sprint 4：学习流程框架 (Module B)

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 4.1 | Step 进度条组件（Compose） | P0 | 2h | 1.4 | StepProgressBar |
| 4.2 | LessonScreen — Step 容器 + 顺序解锁（Compose） | P0 | 3h | 4.1 | Step 切换逻辑 |
| 4.3 | 进度持久化 — ProgressRepository + DataStore | P0 | 3h | 1.3 | Step 完成即写入 |
| 4.4 | ResumeOverlay（继续学习/回顾） | P0 | 2h | 4.3 | 恢复弹窗 |
| 4.5 | 线性模式锁定逻辑（Unit 内） | P0 | 2h | 3.2, 4.3 | 🔒 状态渲染 |
| 4.6 | Step 中途退出确认弹窗 | P0 | 1h | 4.2 | ConfirmDialog |

---

## Sprint 5：Step 1-3 实现

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 5.1 | WordTooltip 组件（全局复用 — Compose BottomSheet） | P0 | 3h | 2.1, 2.2 | 点击单词弹出释义 |
| 5.2 | Step 1 — 课文展示 + AI 配图（Compose） | P0 | 2h | 5.1 | PassageView |
| 5.3 | Step 1 — TTS 全文/逐句朗读 | P0 | 3h | 2.4, 5.2 | 播放控制 |
| 5.4 | Step 1 — 语法点卡片 | P0 | 1h | 5.2 | GrammarCard |
| 5.5 | Step 2 — 填词练习交互 | P0 | 4h | 5.1, 2.4 | Step2FillBlanks |
| 5.6 | Step 3 — 闪卡子模式 | P0 | 3h | 5.1, 2.4 | FlashcardMode |
| 5.7 | Step 3 — 拼写子模式 | P0 | 3h | 2.4 | SpellingMode |
| 5.8 | Step 3 — 匹配子模式 | P0 | 3h | — | MatchingMode |

---

## Sprint 6：Step 4-6 实现

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 6.1 | Step 4 — 听力练习（TTS 播放 + 选择题） | P0 | 4h | 2.4, 5.1 | Step4Listening |
| 6.2 | Step 5 — 阅读理解（课文 + 选择题 + evidence 高亮） | P0 | 4h | 5.1 | Step5Reading |
| 6.3 | Step 6 — 录音交互（按住录音 + 松手停止） | P0 | 3h | 2.5, 2.6 | RecordButton |
| 6.4 | Step 6 — 评分逻辑（编辑距离 + 偏差词标红） | P0 | 3h | 6.3 | scoring.ts |
| 6.5 | Step 6 — 口语练习完整页面 | P0 | 2h | 6.3, 6.4 | Step6Speaking |
| 6.6 | 各 Step 通过/重做逻辑统一处理 | P0 | 2h | 5.5-6.5 | 重做计数 + 放行 |

---

## Sprint 7：设置 & 统计 & 导入导出

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 7.1 | 设置页 UI | P0 | 2h | 1.4 | SettingsScreen |
| 7.2 | 学习统计页 | P1 | 3h | 4.3 | StatsScreen |
| 7.3 | 数据导出（JSON/ZIP） | P1 | 3h | 4.3 | 导出功能 |
| 7.4 | 数据导入 + 预览 + 覆盖 | P1 | 3h | 7.3 | 导入功能 |

---

## Sprint 8：打磨 & 测试

| # | 任务 | 优先级 | 预估 | 依赖 | 产出 |
|---|------|--------|------|------|------|
| 8.1 | 全流程手动测试 + Bug 修复 | P0 | 4h | 全部 | 稳定版本 |
| 8.2 | 关键路径单元测试（scoring, progress, download） | P1 | 4h | 全部 | 测试覆盖 |
| 8.3 | 性能优化（大列表懒加载、TTS 预热） | P2 | 3h | 8.1 | 体验优化 |
| 8.4 | 边界场景补全（空数据、极端输入） | P1 | 3h | 8.1 | 健壮性 |

---

## 任务依赖图

```
Sprint 1 (Android 脚手架)
  ├── 1.1 Android + Compose 初始化
  │   ├── 1.2 Navigation Compose
  │   ├── 1.3 DataStore 封装
  │   ├── 1.4 共享组件
  │   └── 1.5 Theme 定义
  │
Sprint 2 (原生模块) ← 1.1
  ├── 2.1-2.2 ECDICT
  ├── 2.3-2.4 Piper TTS
  ├── 2.5 Whisper ASR
  └── 2.6 集成测试 ← 2.2, 2.4, 2.5
  │
Sprint 3 (课程管理) ← 1.3, 1.4
  ├── 3.1 Manifest → 3.2 列表 → 3.3 状态
  ├── 3.4 下载器 → 3.5 下载 UI
  └── 3.6 引导, 3.7 错误处理
  │
Sprint 4 (学习框架) ← 1.3, 1.4, 3.2
  ├── 4.1 进度条 → 4.2 Step 容器
  ├── 4.3 进度持久化 → 4.4 ResumeOverlay
  └── 4.5 线性锁定, 4.6 退出确认
  │
Sprint 5 (Step 1-3) ← 2.1, 2.4, 5.1
  ├── 5.1 WordTooltip
  ├── 5.2-5.4 Step 1
  ├── 5.5 Step 2
  └── 5.6-5.8 Step 3
  │
Sprint 6 (Step 4-6) ← 2.4, 2.5, 5.1
  ├── 6.1 Step 4
  ├── 6.2 Step 5
  ├── 6.3-6.5 Step 6
  └── 6.6 通过逻辑统一
  │
Sprint 7 (设置/统计) ← 4.3
  ├── 7.1 设置页
  ├── 7.2 统计页
  └── 7.3-7.4 导入导出
  │
Sprint 8 (打磨) ← 全部
  ├── 8.1 手动测试
  ├── 8.2 单元测试
  └── 8.3-8.4 优化
```

---

## 里程碑

| 里程碑 | 包含 Sprint | 交付物 | 验收标准 |
|--------|------------|--------|---------|
| M1: 可运行骨架 | 1-2 | App 启动 + 原生模块可调用 | TTS 能发声、ASR 能识别、词典能查询 |
| M2: 能下课 | 3 | 课程列表 + 下载 | 能拉取 manifest、下载课程 JSON、显示课程树 |
| M3: 能学一课 | 4-5 | Step 1-3 完整流程 | 能完成预习→填词→词汇记忆 |
| M4: MVP | 6 | Step 4-6 完整 | 一课 6 Step 全部可走通 |
| M5: 可用版 | 7-8 | 设置 + 统计 + 测试 | 功能完整、基本稳定 |
