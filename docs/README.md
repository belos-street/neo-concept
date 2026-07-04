# Neo Concept — 文档索引

> 本文档串联项目全部设计资料，按阅读顺序排列，方便快速定位。

---

## 阅读顺序

### 第一步：了解产品

| 文档 | 说明 | 状态 |
|------|------|------|
| [REQUIREMENTS.md](REQUIREMENTS.md) | 产品定位、目标平台、离线优先原则、4 本书的课程体系 | 已确认 |
| [DESIGN-TODO.md](DESIGN-TODO.md) | 设计任务清单，记录已完成与推迟项 | 已更新 |

### 第二步：交互与流程设计

| 文档 | 说明 | 状态 |
|------|------|------|
| [specs/2026-07-01-information-architecture.md](superpowers/specs/2026-07-01-information-architecture.md) | 无底部 Tab，首页即主页，设置从首页进入 | 已确认 |
| [specs/2026-07-01-bookshelf-course-list.md](superpowers/specs/2026-07-01-bookshelf-course-list.md) | 书架卡片、课程列表分组、解锁规则（无锁定） | 已确认 |
| [specs/2026-06-30-lesson-interaction-design.md](superpowers/specs/2026-06-30-lesson-interaction-design.md) | 课程学习页 6 步交互：课文 → 填词 → 拼写 → 阅读 → 口语 → 完成 | 已确认 |

### 第三步：内容与数据

| 文档 | 说明 | 状态 |
|------|------|------|
| [specs/2026-07-01-content-import-design.md](superpowers/specs/2026-07-01-content-import-design.md) | 课程 JSON 结构、manifest/book/lesson 打包进 assets、banner 远程加载 | 已确认 |
| [specs/2026-07-02-user-progress-model.md](superpowers/specs/2026-07-02-user-progress-model.md) | 以 lessonId 为键的进度模型、继续学习卡片规则、复习模式 | 已确认 |
| [vibe-flow/06-data-model.md](vibe-flow/06-data-model.md) | 内容模型、进度模型、设置模型、运行时 UI 状态的 Kotlin 数据类 | 已确认 |

### 第四步：系统与体验

| 文档 | 说明 | 状态 |
|------|------|------|
| [specs/2026-07-02-first-launch-onboarding.md](superpowers/specs/2026-07-02-first-launch-onboarding.md) | 闪屏 + 初始化、无权限/欢迎页、TTS/ASR 懒加载 | 已确认 |
| [specs/2026-07-02-settings-page.md](superpowers/specs/2026-07-02-settings-page.md) | 设置入口、TTS 语速、字体大小、导入/覆盖、清除缓存 | 已确认 |
| [specs/2026-07-03-error-handling-edge-cases.md](superpowers/specs/2026-07-03-error-handling-edge-cases.md) | 图片/TTS/ASR/JSON 异常、中途退出恢复、麦克风权限降级 | 已确认 |

### 第五步：视觉与工程

| 文档 | 说明 | 状态 |
|------|------|------|
| [ui/swiss-minimalist.md](ui/swiss-minimalist.md) | Swiss Theme 移动端改造版：颜色、字体、间距、组件、布局架构图 | 已确认 |
| [demo/index.html](demo/index.html) | 可交互的低保真 Demo，展示首页、书详情、导读、6 个学习步骤、设置 | 已确认 |
| [vibe-flow/05-architecture.md](vibe-flow/05-architecture.md) | Android 系统架构：模块划分、技术选型、数据流向 | 已确认 |
| [vibe-flow/08-engineering.md](vibe-flow/08-engineering.md) | 工程化基建：依赖版本、目录结构、代码规范、Git 工作流 | 已确认 |
| [vibe-flow/09-task-breakdown.md](vibe-flow/09-task-breakdown.md) | MVP 开发任务拆解，每步含完成标准与手动验证方法 | 已确认 |

---

## 文档地图

```
docs/
├── README.md                          ← 你在这里
├── REQUIREMENTS.md                    产品需求
├── DESIGN-TODO.md                     设计任务清单
├── demo/
│   └── index.html                     交互 Demo
├── superpowers/specs/                 需求与系统设计规范
│   ├── 2026-06-30-lesson-interaction-design.md
│   ├── 2026-07-01-bookshelf-course-list.md
│   ├── 2026-07-01-content-import-design.md
│   ├── 2026-07-01-information-architecture.md
│   ├── 2026-07-02-first-launch-onboarding.md
│   ├── 2026-07-02-settings-page.md
│   ├── 2026-07-02-user-progress-model.md
│   └── 2026-07-03-error-handling-edge-cases.md
├── ui/
│   └── swiss-minimalist.md            视觉设计系统
└── vibe-flow/                         工程与开发计划
    ├── 05-architecture.md             系统架构设计
    ├── 06-data-model.md               数据模型设计
    ├── 08-engineering.md              工程化基建
    └── 09-task-breakdown.md           任务拆解与验证计划
```

---

## 状态说明

- **已确认**：用户已审核并同意，可直接作为开发依据。
- **待确认**：需要用户做出选择。
- **推迟**：当前版本不实现，后续版本再考虑。

当前全部设计文档均为 **已确认** 状态。

---

## 下一步

进入开发阶段，参考 [vibe-flow/09-task-breakdown.md](vibe-flow/09-task-breakdown.md) 从 **Phase 0：项目初始化** 开始。
