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

- **离线优先**：ASR/TTS/词典全部打包进 APK，仅课程 JSON 需联网下载
- **纯本地**：无账号、无云端同步、无社交功能
- **非商业**：个人开源项目，课程内容由 LLM 原创生成，仅供学习交流
- **输入→加工→输出**：每课 6 个 Step 按认知规律递进，15-25 min/课
- **Android 优先**：当前专注于 Android 原生开发，iOS 版本后续通过 Vibe Coding 复刻

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
| **brainstorming** | `skills/brainstorming/` | 头脑风暴：在编码前探索需求、对比方案、产出设计文档 |
| **writing-plans** | `skills/writing-plans/` | 实施计划：将设计文档转换为可执行的任务拆解 |
| **frontend-design** | `skills/frontend-design/` | 前端设计：色彩理论、排版、布局、动效、无障碍 |
| **vibe-flow** | `skills/vibe-flow/` | Vibe Coding 全流程：从 idea 到 launch 的 14 步流水线 |

### 辅助技能

（暂无）

---

## 3. 文档索引

项目设计文档位于 `.agents/docs/`，按 Vibe-Flow 阶段编排：

### 📋 需求文档（02-requirements/）— 11 份，已审查

| # | 文件 | 内容 |
|---|------|------|
| 00 | [00-overview.md](.agents/docs/02-requirements/00-overview.md) | 项目概述：商业动机、市场定位、成功指标、角色定义、开发环境 |
| 01 | [01-course-system.md](.agents/docs/02-requirements/01-course-system.md) | 课程内容体系：4 册递进、能力对标、6 Step Lesson 结构 |
| 02 | [02-priority-matrix.md](.agents/docs/02-requirements/02-priority-matrix.md) | 功能模块与优先级：P0/P1/P2 定义，全部功能优先级总览 |
| 03 | [03-user-stories-module-a.md](.agents/docs/02-requirements/03-user-stories-module-a.md) | 模块 A — 课程管理：manifest 拉取、按需下载、检查更新 |
| 04 | [04-user-stories-module-b.md](.agents/docs/02-requirements/04-user-stories-module-b.md) | 模块 B — 学习流程：线性/自由模式、Step 闭环、进度持久化 |
| 05 | [05-user-stories-module-c.md](.agents/docs/02-requirements/05-user-stories-module-c.md) | 模块 C — 练习交互：Step 1-6 详细交互、验收标准、异常流程 |
| 06 | [06-user-stories-module-d.md](.agents/docs/02-requirements/06-user-stories-module-d.md) | 模块 D — 内联词汇查阅：全局单词点击 tooltip、ECDICT 查询 |
| 07 | [07-user-stories-module-ef.md](.agents/docs/02-requirements/07-user-stories-module-ef.md) | 模块 E/F/G — 统计、导入导出、设置 |
| 08 | [08-business-flows.md](.agents/docs/02-requirements/08-business-flows.md) | 业务流程：学习主流程、碎片恢复、内容下载、异常总览 |
| 09 | [09-json-protocol.md](.agents/docs/02-requirements/09-json-protocol.md) | JSON 数据协议：Agent ↔ App 契约、manifest/lesson Schema、字段说明 |
| 10 | [10-nonfunctional.md](.agents/docs/02-requirements/10-nonfunctional.md) | 非功能需求：离线能力、模型大小、技术选型、风险、范围边界 |

### 🎨 设计文档

| 文件 | 阶段 | 内容 |
|------|------|------|
| [03-interaction-design.md](.agents/docs/03-interaction-design.md) | 交互设计 | 底部 Tab 导航、路由树（TabNavigator → Stack → Screen）、核心页面布局与交互规则 |
| [05-architecture.md](.agents/docs/05-architecture.md) | 系统架构 | 技术栈总览、Android 项目目录结构、原生模块接口（Piper/Whisper/ECDICT）、状态管理、存储方案 |

### 🎨 UI 设计

| 文件 | 阶段 | 内容 |
|------|------|------|
| [theme.md](.agents/docs/ui-design/theme.md) | 视觉规范 | Swiss International 风格移动端适配（颜色、字体、间距、动画原则、Accessibility） |
| [components.md](.agents/docs/ui-design/components.md) | 组件规范 | 所有 UI 组件的尺寸、状态、交互方式、文件结构及一致性规则 |

### 🔧 工程文档

| 文件 | 阶段 | 内容 |
|------|------|------|
| [09-task-breakdown.md](.agents/docs/09-task-breakdown.md) | 任务拆解 | 8 个 Sprint，每个任务 ≤ 4h，含依赖关系图：S1 脚手架 → S2 原生模块 → S3 课程管理 → S4 学习框架 → S5 Step 1-3 → S6 Step 4-6 → S7 设置&统计 → S8 测试打磨 |
| [11-quality-testing.md](.agents/docs/11-quality-testing.md) | 测试策略 | 手动测试为主 + 关键逻辑单元测试，P0/P1 测试范围清单，手动测试检查表 |
| [12-native-module-tests.md](.agents/docs/12-native-module-tests.md) | 原生模块测试 | Sprint 2 手动测试用例：ECDICT/TTS/ASR 模块初始化、功能验证、错误处理 |

### 📝 历史文档

| 文件 | 说明 |
|------|------|
| [requirements-draft-v0.3.md](.agents/docs/requirements-draft-v0.3.md) | 需求草稿 v0.3，已被 02-requirements/ 正式需求文档取代，仅供参考 |
