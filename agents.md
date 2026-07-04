# Neo Concept — Agent Configuration

## 1. 项目基本信息

| 维度 | 说明 |
|------|------|
| **项目名** | neo-concept |
| **产品形态** | Android 原生 App（Kotlin + Jetpack Compose）；iOS 版本后续通过 Vibe Coding 实现 |
| **App 定位** | 离线优先、系统化英语学习工具 |
| **目标用户** | 英语基础薄弱、希望体系化提升的学习者 |
| **开源协议** | GPL-3.0 |
| **包管理器** | Gradle KTS |
| **开发设备** | macOS / Windows |
| **开发工具** | Android Studio |

### 核心原则

- **离线优先**：ASR/TTS/词典/课程 JSON 全部打包进 APK，不依赖线上拉取
- **纯本地**：无账号、无云端同步、无社交功能
- **非商业**：个人开源项目，课程内容由 LLM 原创生成，仅供学习交流
- **输入→加工→输出**：每课 6 个 Step 按认知规律递进，15-25 min/课
- **Android 优先**：当前专注于 Android 原生开发，iOS 版本后续通过 Vibe Coding 复刻
- **统一 UI 风格**：所有 UI 组件必须严格遵循 `SwissTheme` 设计规范（详见 [`docs/ui/swiss-minimalist.md`](docs/ui/swiss-minimalist.md)），禁止使用 Material3 默认样式

---

## 2. Skills 目录

`.agents/skills/` 下是项目配置的技能模块，为 AI Agent 提供领域知识、编码规范和参考文档：

### 核心开发技能

| 技能 | 路径 | 作用 |
|------|------|------|
| **belos-street** | `skills/belos-street/` | 个人编码规范：命名约定、代码组织、代码风格、测试理念、LLM 编码指南 |
| **android-navigation-3** | `skills/android-navigation-3/` | Jetpack Navigation 3：Tab/Stack 导航、深链、多回退栈、BottomSheet、Dialog |
| **android-compose-theming** | `skills/android-compose-theming/` | Compose Styles API：组件主题、自定义样式、Modifier.styleable |
| **android-testing** | `skills/android-testing/` | Android 测试策略：单元测试、UI 测试、截图测试、覆盖率 |
| **android-edge-to-edge** | `skills/android-edge-to-edge/` | Edge-to-edge 全屏显示：系统栏、IME 处理、导航栏适配 |

### 设计与流程技能

| 技能 | 路径 | 作用 |
|------|------|------|
| **frontend-design** | `skills/frontend-design/` | 前端设计：色彩理论、排版、布局、动效、无障碍 |
| **vibe-flow** | `skills/vibe-flow/` | Vibe Coding 全流程：从 idea 到 launch 的 14 步流水线 |
| **superpowers** | `skills/superpowers/` | AI 辅助开发工作流：规划、实现、调试、代码审查、TDD 等全流程指导 |

### 辅助技能

（暂无）

---

## 3. 项目文档入口

所有项目文档统一收纳在 `docs/` 目录，并通过 [`docs/README.md`](docs/README.md) 作为总索引。该索引按阅读顺序列出需求、交互、数据、系统、视觉、工程、任务拆解等全部文档，开发前应先通读索引确定上下文。

主要文档分类：

| 分类 | 路径 | 说明 |
|------|------|------|
| 需求与设计规范 | `docs/superpowers/specs/` | 信息架构、课程交互、内容导入、进度模型、启动引导、设置、错误处理等 |
| 视觉设计系统 | `docs/ui/swiss-minimalist.md` | Swiss Theme 颜色、字体、间距、组件、布局架构 |
| 交互 Demo | `docs/demo/index.html` | 低保真可交互 Demo |
| 工程与开发计划 | `docs/vibe-flow/` | 系统架构、数据模型、工程化基建、任务拆解 |
| 设计任务清单 | `docs/DESIGN-TODO.md` | 已完成与待设计项跟踪 |

---

## 4. 文档与图表规范

- **图表统一使用 Mermaid**：所有结构图、流程图、类图、状态图等统一使用 Markdown 原生的 Mermaid 语法绘制，确保在 GitHub、GitLab 及支持 Mermaid 的渲染器中可直接查看。
- **避免 ASCII 流程图与外部图片**：不要使用纯 ASCII 流程图、截图或外部绘图工具代替 Mermaid；只有纯粹的 UI 布局线框图可保留简化 ASCII。
