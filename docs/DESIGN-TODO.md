# Neo Concept — 需求设计 TODO

> 本文档记录项目需求与系统设计的完成状态。已确认的内容保留链接，未完成的内容按依赖顺序排列，作为后续对话的推进清单。
> 当前阶段：**聚焦需求与系统设计，不写代码实现。**

---

## 已完成设计

- [x] [产品概述与课程体系](REQUIREMENTS.md)
  - 产品定位、目标平台、离线优先原则、4 本书的课程体系。
- [x] [课程学习页 6 步交互设计](docs/superpowers/specs/2026-06-30-lesson-interaction-design.md)
  - 课文学习 → 填词练习 → 拼写练习 → 阅读理解 → 口语练习（可跳过）→ 完成页。

---

## 待设计清单

按依赖顺序排列，建议从上到下依次推进。

### 1. 应用信息架构与导航流程

> **已确认**：无底部 Tab，首页即唯一主页，设置从首页左上角进入。详见 [docs/superpowers/specs/2026-07-01-information-architecture.md](docs/superpowers/specs/2026-07-01-information-architecture.md)。
> 核心：首页「继续学习」卡片 + 4 本书书架，点击课程直接进入学习页。

- [x] 用户确认无底部 Tab、首页结构、返回栈方案。

### 2. 课程目录/书架设计

> **已确认**：详见 [docs/superpowers/specs/2026-07-01-bookshelf-course-list.md](docs/superpowers/specs/2026-07-01-bookshelf-course-list.md)。
> 核心：首页垂直大卡片书架；书详情页为课程列表，每 10 课粘性分组；4 种课程状态（无锁定）；所有书/课自由开放，只保留完成状态标记；口语跳过保留待补标记。

- [x] 用户确认首页书架形式、课程列表分组方式、解锁规则。

### 3. 内容清单与导入机制

> **方案已确认**：详见 [docs/superpowers/specs/2026-07-01-content-import-design.md](docs/superpowers/specs/2026-07-01-content-import-design.md)。
> 核心：每节课独立 JSON；manifest.json + book.json + lesson.json 全部打包进 App assets，不线上拉取；banner 通过远程 HTTPS 加载 + 离线占位图；首次启动打开即用；音频全部由本地 TTS 生成。

- [x] 用户确认更新后的内容导入方案。

### 4. 用户进度与状态模型

> **已确认**：详见 [docs/superpowers/specs/2026-07-02-user-progress-model.md](docs/superpowers/specs/2026-07-02-user-progress-model.md)。
> 核心：以 `lessonId` 为键记录每课完成步骤、口语跳过、最后位置；书级进度实时计算；应用级只保留最后学习位置；启动不自动跳转，通过首页「继续学习」卡片恢复；复习模式可跳到任意步骤；不记录错误历史。

- [x] 用户确认复习模式、继续学习卡片规则、不记录错误历史。
- [x] 用户确认进度数据可导出/导入实现换机同步（当前阶段不实现）。

### 5. 首次启动与引导流程

> **已确认**：详见 [docs/superpowers/specs/2026-07-02-first-launch-onboarding.md](docs/superpowers/specs/2026-07-02-first-launch-onboarding.md)。
> 核心：首次启动只显示闪屏页 + 初始化转圈，后台建立课程索引；不弹欢迎页、不选水平、不请求权限；TTS/词典/ASR 懒加载；麦克风权限在口语练习时按需请求；界面自解释，不加使用提示。

- [x] 用户确认闪屏页、初始化提示、权限策略、无使用提示。

### 6. 设置页面

> **已确认**：详见 [docs/superpowers/specs/2026-07-02-settings-page.md](docs/superpowers/specs/2026-07-02-settings-page.md)。
> 核心：设置入口只在首页；4 组设置（音频/显示/存储/关于）；TTS 语速 0.8x/1.0x/1.2x 三档；字体大小三档；导入学习记录覆盖本地并二次确认；不显示缓存占用大小。

- [x] 用户确认设置项、生效规则、导入覆盖策略。

### 7. 全局 UI 与视觉设计系统

> **方向已确认**：采用 SwissTheme 理念的移动端改造版，详见 [docs/ui/swiss-minimalist.md](docs/ui/swiss-minimalist.md)。
> 已确定：颜色 Token（含语义状态色）、字体、间距、圆角、动效、深色模式、组件概览。
> 已补充：核心页面组件布局详述（首页卡片、课程列表项、查词 Modal、学习页顶部/底部）。

- [x] 输出更细化的组件布局说明。

### 8. 错误处理与边界情况

> **已确认**：详见 [docs/superpowers/specs/2026-07-03-error-handling-edge-cases.md](docs/superpowers/specs/2026-07-03-error-handling-edge-cases.md)。
> 核心：Banner 失败仅显示默认占位图；TTS 失败 Toast 提示并关闭 App；ASR 不可用同时显示跳过/重试；JSON 首次启动校验，损坏课程隐藏并 Toast 提示；每次步骤切换/完成立即保存进度，返回后停留原步骤；口语权限每次进入都请求，永久拒绝后 Toast + 去设置入口。

- [x] 用户确认各异常场景降级策略。

### 9. 统计与个人中心

> **推迟到 v1.1**：当前版本不实现。后续可考虑连续天数、完成课数、学习时长、学习日历、成就徽章等，但一课内不显示用时。

- [ ] v1.1 再考虑。

### 10. 复习与搜索

> **推迟到后续版本**：当前版本不实现单词收藏、错题回顾、课文搜索、复习机制。

- [ ] 后续版本再考虑。

---
