# 06 — 用户故事：模块 D — 内联词汇查阅

> 所属：Neo Concept 结构化需求文档

---

## US-D1 — 全局单词点击查阅

> **作为** 学习者
> **我希望** 在 App 任意英文文本处点击单词就能看到中文释义
> **以便** 随时查阅生词，不中断学习流程

### 验收标准

- [ ] AC-D1.1：所有英文文本区域（课文、题目、选项、例句、语法卡片）中的单词均可点击
- [ ] AC-D1.2：点击弹出 tooltip，显示：单词 / 音标 / 词性 / 中文释义 / 例句 / 发音按钮
- [ ] AC-D1.3：点击 tooltip 外部或再次点击单词 → 关闭 tooltip
- [ ] AC-D1.4：发音按钮调用 Piper TTS 朗读该单词
- [ ] AC-D1.5：所有单词默认带下划线（`text-decoration: underline`），表示可点击

### Tooltip 样式

```
┌─────────────────────────────────┐
│  beautiful    /ˈbjuːtɪfl/       │
│  adj. 美丽的；漂亮的              │
│                                 │
│  📝 She is a beautiful girl.    │
│  🔊 [发音按钮]                   │
└─────────────────────────────────┘
```

### 数据来源

ECDICT 离线 SQLite 数据库（~10 MB），查询 <10ms。数据结构：

```typescript
interface WordEntry {
  word: string           // "beautiful"
  phonetic: string       // "/ˈbjuːtɪfl/"
  pos: string            // "adj."
  meaning: string        // "美丽的；漂亮的"
  sentence: string       // "She is a beautiful girl."
}
```

### 异常流程

- **单词不在 ECDICT 中**：tooltip 显示「未找到该词释义」
- **ECDICT 数据库未加载**：tooltip 显示「词典加载中...」
- **点击标点符号/非单词字符**：不触发 tooltip

### 边界场景

- 同一单词在同一页多次出现 → 每次点击都弹出 tooltip
- tooltip 弹出位置超出屏幕 → 自动翻转到可显示区域（上方/下方）
- 快速连续点击不同单词 → 前一个 tooltip 关闭，新 tooltip 打开
