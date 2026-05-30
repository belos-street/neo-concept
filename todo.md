# Neo Concept — 迁移计划（RN → 纯原生）

> React Native + TypeScript → Android (Kotlin + Jetpack Compose) / iOS (Swift + SwiftUI)
>
> 状态：`⬜` 待开始 | `🔄` 进行中 | `✅` 已完成

---

## Phase 0：清理 RN 相关文件

### 删除根目录配置文件

| # | 文件 | 说明 | 状态 |
|---|------|------|:--:|
| 0.1 | `App.tsx` | RN 入口组件 | ✅ |
| 0.2 | `app.json` | RN 应用配置 | ✅ |
| 0.3 | `index.js` | RN 注册入口 | ✅ |
| 0.4 | `package.json` | bun/npm 依赖 | ✅ |
| 0.5 | `bun.lockb` | bun 锁文件 | ✅ |
| 0.6 | `bunfig.toml` | bun 配置 | ✅ |
| 0.7 | `tsconfig.json` | TypeScript 配置 | ✅ |
| 0.8 | `babel.config.js` | Babel 转译配置 | ✅ |
| 0.9 | `metro.config.js` | Metro bundler 配置 | ✅ |
| 0.10 | `.prettierrc` | Prettier 格式化配置 | ✅ |
| 0.11 | `Gemfile` | Ruby CocoaPods 依赖 | ✅ |
| 0.12 | `test-setup.ts` | Jest/bun 测试初始化 | ✅ |

### 删除目录

| # | 目录 | 说明 | 状态 |
|---|------|------|:--:|
| 0.13 | `__tests__/` | RN 测试文件 | ✅ |
| 0.14 | `scripts/build-android.ts` | RN 构建脚本 | ✅ |
| 0.15 | `scripts/run-android.ts` | RN 启动脚本 | ✅ |
| 0.16 | `android/` | 整个 RN Android 壳（先备份 ecdict.db） | ✅ |
| 0.17 | `ios/` | 整个 RN iOS 壳 | ✅ |

### 删除不再适用的 Skills

| # | 目录 | 说明 | 状态 |
|---|------|------|:--:|
| 0.18 | `.agents/skills/bun/` | Bun 运行时参考 | ✅ |
| 0.19 | `.agents/skills/react-best-practices/` | React 最佳实践（62 文件） | ✅ |
| 0.20 | `.agents/skills/react-native-skills/` | RN 专项参考 | ✅ |
| 0.21 | `.agents/skills/zustand/` | Zustand 状态管理 | ✅ |

### 更新配置文件

| # | 文件 | 操作 | 状态 |
|---|------|------|:--:|
| 0.22 | `agents.md` | 技术栈改为 Kotlin/Swift，删除 bun/RN 描述 | ✅ |
| 0.23 | `.gitignore` | 添加 `.idea/`、`*.iml`、`local.properties`、`.gradle/`、`build/` | ✅ |
| 0.24 | `README.md` | 更新项目说明 | ✅ |

### 保留不删除

- `src/` — Vibecoding 参考（TS 业务逻辑）
- `scripts/trim_ecdict.py` — Python 独立工具
- `.agents/docs/` — 已更新为原生方案的设计文档
- `.agents/skills/belos-street/` — 个人编码规范
- `.agents/skills/brainstorming/` — 流程工具
- `.agents/skills/writing-plans/` — 流程工具
- `.agents/skills/frontend-design/` — 设计理论
- `.agents/skills/vibe-flow/` — Vibe Coding 流程
- `LICENSE` — GPL-3.0 不变

---

## Phase 1：Android 项目初始化

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 1.1 | 备份 ecdict.db 到 `assets/ecdict.db` | 0.16 | ✅ |
| 1.2 | Android Studio 创建新项目（Kotlin + Compose，minSdk 26） | 0.16 | ✅ |
| 1.3 | Gradle KTS 配置（Compose、Navigation、DataStore、Coroutines） | 1.2 | ✅ |
| 1.4 | 包结构：`ui/`、`data/`、`domain/`、`native/` | 1.2 | ✅ |
| 1.5 | 移入 ecdict.db → `app/src/main/assets/` | 1.1, 1.2 | ✅ |
| 1.6 | Theme 定义 — Swiss International（黑白+红、无圆角、无阴影） | 1.2 | ✅ |
| 1.7 | Navigation Compose — 底部 Tab + Stack | 1.3 | ✅ |
| 1.8 | DataStore 封装 — PreferencesDataStore | 1.3 | ✅ |
| 1.9 | 基础 UI 组件 — Button/Card/Divider/Modal/ScreenHeader/Loading | 1.6 | ✅ |

---

## Phase 2：原生模块

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 2.1 | ECDICT — Asset 复制 + SQLite 查询 + FTS 搜索 | 1.5 | ⬜ |
| 2.2 | Piper TTS — 模型初始化 + 合成 + 播放 | 1.4 | ⬜ |
| 2.3 | Whisper ASR — 模型加载 + 录音 + 识别 | 1.4 | ⬜ |
| 2.4 | 集成测试 — 真机验证三个模块 | 2.1, 2.2, 2.3 | ⬜ |

---

## Phase 3：课程管理

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 3.1 | ManifestRepository — 拉取 + 解析 + 本地缓存 | 1.8 | ⬜ |
| 3.2 | CourseListScreen — Book/Unit/Lesson 树形展开 | 3.1, 1.9 | ⬜ |
| 3.3 | 课程状态标识（已下载/未下载/锁定/有更新） | 3.2 | ⬜ |
| 3.4 | DownloadManager — 串行队列 + Hash 校验 + 取消 | 3.1 | ⬜ |
| 3.5 | DownloadScreen — 下载进度 UI | 3.4 | ⬜ |
| 3.6 | 首次启动引导（URL 输入 + manifest 拉取） | 3.1 | ⬜ |
| 3.7 | 错误处理（无网络/解析失败/存储不足） | 3.1 | ⬜ |

---

## Phase 4：学习框架

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 4.1 | StepProgressBar — 进度条组件 | 1.9 | ⬜ |
| 4.2 | LessonScreen — Step 容器 + 顺序解锁 | 4.1, 1.7 | ⬜ |
| 4.3 | ProgressRepository — DataStore 持久化 | 1.8 | ⬜ |
| 4.4 | ResumeOverlay — 继续学习/回顾弹窗 | 4.3 | ⬜ |
| 4.5 | 线性模式锁定逻辑 | 3.2, 4.3 | ⬜ |
| 4.6 | ExitModal — 退出确认弹窗 | 4.2 | ⬜ |

---

## Phase 5：Step 1-3

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 5.1 | WordTooltip — 全局单词点击释义（BottomSheet） | 2.1, 1.9 | ⬜ |
| 5.2 | Step 1 — PassageStep（课文展示 + AI 配图） | 5.1 | ⬜ |
| 5.3 | Step 1 — TTS 全文/逐句朗读 | 2.2, 5.2 | ⬜ |
| 5.4 | Step 1 — GrammarCard 语法点卡片 | 5.2 | ⬜ |
| 5.5 | Step 2 — FillBlanksStep 填词练习 | 5.1 | ⬜ |
| 5.6 | Step 3 — VocabExerciseStep 词汇记忆（闪卡/拼写/匹配） | 2.2, 5.1 | ⬜ |

---

## Phase 6：Step 4-6

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 6.1 | Step 4 — ListeningStep 听力练习 | 2.2, 5.1 | ⬜ |
| 6.2 | Step 5 — ReadingStep 阅读理解 | 5.1 | ⬜ |
| 6.3 | Step 6 — SpeakingStep 口语跟读 | 2.2, 2.3 | ⬜ |
| 6.4 | Step 6 — ASR 识别 + 模糊匹配评分 | 6.3 | ⬜ |
| 6.5 | Step 6 — 通过/未通过 UI | 6.4 | ⬜ |
| 6.6 | 各 Step 通过/重做逻辑统一 | 6.1-6.5 | ⬜ |

---

## Phase 7：设置 & 统计

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 7.1 | SettingsScreen — 仓库 URL、语速、字体大小 | 1.9 | ⬜ |
| 7.2 | StatsScreen — 学习统计展示 | 4.3 | ⬜ |
| 7.3 | 数据导出（JSON/ZIP） | 4.3 | ⬜ |
| 7.4 | 数据导入 + 预览 + 覆盖 | 7.3 | ⬜ |

---

## Phase 8：打磨 & 测试

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 8.1 | 全流程手动测试 + Bug 修复 | 全部 | ⬜ |
| 8.2 | 关键路径单元测试（JUnit 5） | 全部 | ⬜ |
| 8.3 | 性能优化（冷启动/列表/TTS 延迟） | 8.1 | ⬜ |
| 8.4 | 边界场景补全 | 8.1 | ⬜ |

---

## Phase 9：iOS 端（Vibecoding）

| # | 任务 | 依赖 | 状态 |
|---|------|------|:--:|
| 9.1 | Xcode 创建 SwiftUI 项目 | Phase 8 | ⬜ |
| 9.2 | 移植原生模块 — ECDICT / TTS / ASR | 9.1 | ⬜ |
| 9.3 | 复刻 UI — SwiftUI + Swiss International | 9.1 | ⬜ |
| 9.4 | 复刻全部功能（参考 Android + src/ TS 逻辑） | 9.2, 9.3 | ⬜ |

---

## 里程碑

| 里程碑 | Phase | 目标 | 状态 |
|--------|-------|------|:--:|
| M0 | 0 | 清理完毕，仓库干净 | ⬜ |
| M1 | 1-2 | Android 骨架 + 原生模块可运行 | ⬜ |
| M2 | 3 | 能拉取 manifest、下载课程、显示课程树 | ⬜ |
| M3 | 4-5 | 能完成一课 Step 1-3 | ⬜ |
| M4 | 6 | 一课 6 Step 全部走通 | ⬜ |
| M5 | 7-8 | 功能完整、基本稳定 | ⬜ |
| M6 | 9 | iOS 端可用 | ⬜ |
