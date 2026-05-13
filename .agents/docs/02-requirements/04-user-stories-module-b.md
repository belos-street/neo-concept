# 04 — 用户故事：模块 B — 学习流程

> 所属：Neo Concept 结构化需求文档

---

## US-B1 — 线性学习模式

> **作为** 学习者
> **我希望** 必须完成前一课才能进入下一课
> **以便** 按照设计的递进体系系统学习

### 验收标准

- [ ] AC-B1.1：未完成 lesson-{N} 时，lesson-{N+1} 显示为锁定状态（灰色 + 🔒）
- [ ] AC-B1.2：完成 lesson-{N}（全部 6 个 Step 完成）后，lesson-{N+1} 自动解锁
- [ ] AC-B1.3：可在设置中切换为「自由模式」，解锁所有已下载课程

---

## US-B2 — 自由学习模式

> **作为** 学习者
> **我希望** 自由访问所有已下载的课程
> **以便** 复习或跳过某些内容

### 验收标准

- [ ] AC-B2.1：自由模式下，所有已下载课程可点击进入
- [ ] AC-B2.2：未下载的课程仍需要先下载
- [ ] AC-B2.3：在设置中可在线性/自由模式间切换

---

## US-B3 — Step-by-Step 学习闭环

> **作为** 学习者
> **我希望** 按 Step 1→2→3→4→5→6 的顺序完成一课的学习
> **以便** 遵循「输入→加工→输出」的认知规律

### 验收标准

- [ ] AC-B3.1：Step 按顺序解锁，完成 Step N 后自动解锁 Step N+1
- [ ] AC-B3.2：每个 Step 完成后自动保存进度（得分、用时、错误记录）
- [ ] AC-B3.3：6 个 Step 全部完成后，该课标记为「已完成」
- [ ] AC-B3.4：用户可回看已完成 Step 的内容和答题记录（只读）

### 异常流程

- **App 在 Step 中途被杀死**：恢复时回到该 Step 起始状态（未完成状态下的答题不保留）
- **用户主动退出 Step**：弹出确认「确定退出？当前进度不会保存」

### 边界场景

- 用户在某 Step 中反复进出 → 每次进入都是新鲜状态

---

## US-B4 — Per-Step 进度持久化与恢复

> **作为** 学习者
> **我希望** App 记住我每节课学到哪个 Step
> **以便** 我可以在碎片时间分段学习，随时继续

### 验收标准

- [ ] AC-B4.1：每个 Step 完成时立即写入 MMKV（key: `progress:{lessonId}`）
- [ ] AC-B4.2：下次打开该课时，自动跳转到 `currentStep`
- [ ] AC-B4.3：一课全部 Step 完成后 `lessonCompleted: true`
- [ ] AC-B4.4：App 被杀死后重新打开，进度不丢失

### 异常流程

- **MMKV 写入失败**：App 不应崩溃；下次启动时该课进度可能丢失，用户需重新学习
- **数据损坏**：读取进度时做防御性校验，若数据结构不合法则视为无进度，从 Step 1 开始

### 进度数据结构

```typescript
interface LessonProgress {
  lessonId: string
  currentStep: 1 | 2 | 3 | 4 | 5 | 6
  steps: {
    [step: number]: {
      status: 'idle' | 'in_progress' | 'completed'
      startedAt: number | null
      completedAt: number | null
      score: number | null          // 0-100
      timeSpentSeconds: number | null
      errors?: string[]
      attemptCount?: number
    }
  }
  lastUpdated: number
  lessonCompleted: boolean
}
```
