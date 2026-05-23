# Neo Concept — Agent Configuration

## 1. 项目基本信息

| 维度 | 说明 |
|------|------|
| **项目名** | neo-concept |
| **产品形态** | 手机 App（React Native 0.76+ / TypeScript），Android 优先，iOS 接口预留 |
| **App 定位** | 离线优先、系统化英语学习工具 |
| **目标用户** | 英语基础薄弱、希望体系化提升的学习者 |
| **开源协议** | GPL-3.0 |
| **包管理器** | bun（依赖管理 + 脚本运行 + 单元测试） |
| **开发设备** | macOS / Windows |
| **开发工具** | VS Code + Android Studio（模拟器） |

### 核心原则

- **离线优先**：ASR/TTS/词典全部打包进 APK，仅课程 JSON 需联网下载
- **纯本地**：无账号、无云端同步、无社交功能
- **非商业**：个人开源项目，课程内容由 LLM 原创生成，仅供学习交流
- **输入→加工→输出**：每课 6 个 Step 按认知规律递进，15-25 min/课
- **跨平台设计**：业务逻辑与 UI 层保持平台无关（React Native 层），平台相关代码通过 `src/native/` 桥接层隔离；Android 优先实现，iOS 接口预留，后续只需补充 iOS 原生模块即可复用全部上层代码

---

## 2. Skills 目录

`.agents/skills/` 下是项目配置的技能模块，为 AI Agent 提供领域知识、编码规范和参考文档：

### 核心开发技能

| 技能 | 路径 | 作用 |
|------|------|------|
| **belos-street** | `skills/belos-street/` | 个人编码规范：命名约定、代码组织、代码风格、测试理念、LLM 编码指南 |
| **bun** | `skills/bun/` | Bun 运行时专项：基础 API、包管理、CLI 工具、测试、服务端 |
| **react-best-practices** | `skills/react-best-practices/` | React 最佳实践：Hooks、Event、Context、Performance、TypeScript、Suspense 等 62 个参考文档 |
| **react-native-skills** | `skills/react-native-skills/` | React Native 专项：列表性能、动画、导航、UI 组件、原生模块等 |
| **zustand** | `skills/zustand/` | Zustand 状态管理：store 定义、selectors、middleware、TypeScript 支持 |

### 设计与流程技能

| 技能 | 路径 | 作用 |
|------|------|------|
| **brainstorming** | `skills/brainstorming/` | 头脑风暴：在编码前探索需求、对比方案、产出设计文档 |
| **writing-plans** | `skills/writing-plans/` | 实施计划：将设计文档转换为可执行的任务拆解 |
| **frontend-design** | `skills/frontend-design/` | 前端设计：色彩理论、排版、布局、动效、无障碍 |
| **vibe-flow** | `skills/vibe-flow/` | Vibe Coding 全流程：从 idea 到 launch 的 14 步流水线 |

### 辅助技能

| 技能 | 路径 | 作用 |
|------|------|------|
| **langchain** | `skills/langchain/` | LangChain 框架：Agent CLI 生成课程内容时使用（App 本体不依赖） |

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
| [05-architecture.md](.agents/docs/05-architecture.md) | 系统架构 | 技术栈总览、项目目录结构、原生模块桥接架构（Piper/Whisper/ECDICT 的 TS 接口与 Kotlin 实现要点） |

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

### 📝 历史文档

| 文件 | 说明 |
|------|------|
| [requirements-draft-v0.3.md](.agents/docs/requirements-draft-v0.3.md) | 需求草稿 v0.3，已被 02-requirements/ 正式需求文档取代，仅供参考 |
