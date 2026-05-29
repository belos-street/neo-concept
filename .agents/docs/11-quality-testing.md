# 11 — 测试策略

> 所属：Neo Concept 结构化需求文档 | Vibe-Flow Stage 11

---

## 测试原则

- 个人项目，**手动测试为主 + 关键逻辑单元测试**
- 不追求高覆盖率，聚焦核心路径
- 测试工具：JUnit 5

---

## 测试分层

```
┌─────────────────────────────┐
│  手动测试（每次功能开发后）    │  ← 主要手段
├─────────────────────────────┤
│  单元测试（关键纯函数）       │  ← 补充手段
├─────────────────────────────┤
│  集成测试（暂不做）           │  ← 后期考虑
└─────────────────────────────┘
```

---

## 单元测试范围

### 必须覆盖（P0）

| 模块 | 测试对象 | 测试内容 |
|------|---------|---------|
| ScoringUtil.kt | `calculateAccuracy()` | 正常句子、完全匹配、完全不匹配、空输入、大小写、标点 |
| ScoringUtil.kt | `highlightDiffs()` | 偏差词标红、全对、全错 |
| progress store | `completeStep()` | Step 完成写入、分数记录、attemptCount 递增 |
| progress store | `loadProgress()` | 正常加载、数据损坏降级、key 不存在 |
| download manager | `verifyHash()` | Hash 匹配、Hash 不匹配、空文件 |
| settings store | `setSetting()` | 写入持久化、读取默认值 |

### 建议覆盖（P1）

| 模块 | 测试对象 | 测试内容 |
|------|---------|---------|
| manifest parser | `parseManifest()` | 正常解析、字段缺失、格式错误 |
| word lookup | `lookupWord()` | 查到词、未找到、数据库未就绪 |
| fill-blanks scorer | `checkAnswers()` | 全对、部分对、全错、大小写不敏感 |

---

## 手动测试检查表

### 课程管理流程

- [ ] 首次启动：URL 未配置 → 引导弹窗 → 输入 URL → 拉取 manifest → 显示课程树
- [ ] 首次启动：无网络 → 错误页 + 重试 → 联网后重试成功
- [ ] 点击未下载课程 → 下载进度 → 完成后自动进入 Step 1
- [ ] 点击已下载课程（无进度）→ 直接进入 Step 1
- [ ] 点击已下载课程（有进度）→ ResumeOverlay → 继续/回顾
- [ ] 下载中取消 → 返回列表，状态恢复 ⬜
- [ ] 下载中断网 → 错误提示 → 重试
- [ ] 检查更新 → 有更新标识 → 更新后进度重置

### 学习流程

- [ ] 线性模式：未完成 lesson-1 → lesson-2 显示 🔒
- [ ] 完成 lesson-1 → lesson-2 自动解锁
- [ ] 自由模式：所有已下载课程可进入
- [ ] 切换线性↔自由 → 进度不丢失
- [ ] Step 1 完成 → Step 2 解锁 → ... → Step 6 完成 → 课程标记完成
- [ ] App 被杀死 → 重新打开 → 进度不丢失
- [ ] Step 中途退出 → 确认弹窗 → 确认后退出（进度不保存）

### Step 交互

**Step 1 课文预习：**
- [ ] 课文显示完整，单词可点击弹出 tooltip
- [ ] 全文朗读播放/暂停/停止
- [ ] 逐句播放 + 高亮
- [ ] TTS 速度切换即时生效
- [ ] 语法点卡片显示正确

**Step 2 填词练习：**
- [ ] 6-8 题显示，每题有提示（首字母+词性+中文）
- [ ] 正确输入 → 绿色 ✅
- [ ] 错误输入 → 红色 + 正确答案 + 发音按钮
- [ ] ≥ 60% → 可进入下一步
- [ ] < 60% → 可重做（最多 3 次）
- [ ] 3 次后仍 < 60% → 标记 completed，可进入下一步

**Step 3 词汇记忆：**
- [ ] 闪卡：左滑不认识、右滑认识、点击翻转
- [ ] 闪卡：跳过认识词开关正常
- [ ] 拼写：TTS 播放、输入比对、差异高亮
- [ ] 拼写：重做逻辑（同 Step 2）
- [ ] 匹配：配对正确变灰、错误抖动
- [ ] 三个子模式按顺序走完 → Step 3 completed

**Step 4 听力练习：**
- [ ] TTS 自动播放当前题 audio_segment
- [ ] 可重复播放
- [ ] 选择后即时判分
- [ ] 播放中切下一题 → 停止当前音频
- [ ] 重做逻辑（同 Step 2）

**Step 5 阅读理解：**
- [ ] 课文全文可滚动
- [ ] 选错 → 原文 evidence 高亮 + 解析
- [ ] 重做逻辑（同 Step 2）

**Step 6 口语练习：**
- [ ] 按住录音 → 松手停止 → Whisper 识别
- [ ] 显示识别结果 + 准确率 + 偏差词
- [ ] ≥ 阈值 → 可进入下一句
- [ ] < 阈值 → 可重录（不限次数）
- [ ] 识别为空 → 提示重试
- [ ] 30s 自动停止录音
- [ ] 全部完成 → 课程标记完成 🎉

### 全局组件

- [ ] WordTooltip：点击英文弹出、点击外部关闭、发音按钮
- [ ] WordTooltip：词不在 ECDICT → 显示「未找到」
- [ ] WordTooltip：ECDICT 未加载 → 显示「加载中」
- [ ] TTS：同一时间只有一个播放源
- [ ] TTS：App 切后台 → 停止播放

### 设置 & 数据

- [ ] 设置项修改即时生效
- [ ] 设置项重启后保留
- [ ] 导出 JSON/ZIP → 文件可分享
- [ ] 导入 → 预览 → 确认 → 数据覆盖
- [ ] 导入格式错误 → 提示

---

## 测试工具配置

### Android 单元测试配置

使用 JUnit 5 + Kotlin Coroutines Test。测试文件放在 `android/app/src/test/java/com/neoconcept/`。
关键模块（scoring, progress, download）使用纯 Kotlin 函数，方便单元测试。

### 测试文件结构

```
android/app/src/test/java/com/neoconcept/
├── ScoringTest.kt              # 评分算法
├── ProgressRepositoryTest.kt   # 进度持久化
├── DownloadManagerTest.kt      # 下载 + Hash 校验
├── SettingsRepositoryTest.kt   # 设置读写
└── ManifestParserTest.kt       # Manifest 解析
```

---

## 性能基线

| 指标 | 目标 | 测试方法 |
|------|------|---------|
| 课程 JSON 解析 | < 500ms | console.time 手动测量 |
| ECDICT 查询 | < 10ms | 单元测试计时 |
| TTS 首次播放延迟 | < 500ms | 手动感知 |
| ASR 识别（3-5s 音频） | < 2s | 手动计时 |
| 课程列表滚动 | 60fps | Android Studio Profiler |
| App 冷启动 | < 3s | 手动计时 |

---

## Bug 修复流程

```
发现 Bug
  │
  ▼
复现步骤记录（截图 + 操作序列）
  │
  ▼
定位原因（哪个模块/函数）
  │
  ▼
修复 + 手动验证
  │
  ├─ 纯函数 Bug → 补单元测试
  └─ UI/交互 Bug → 更新手动测试检查表
```
