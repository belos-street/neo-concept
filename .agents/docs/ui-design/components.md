# UI Components — Swiss Mobile Spec

> Design token reference: [theme.md](theme.md)
> Tech stack: Kotlin + Jetpack Compose (Android) / Swift + SwiftUI (iOS)

---

## 1. Primitives

### 1.1 ScreenHeader

The top bar of every screen. Contains back navigation, title, and optional right action.

```
┌──────────────────────────────────┐
│ ←  COURSE LIST          [UPDATE] │
└──────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Height | 48px |
| Background | `colorBg` white |
| Bottom border | 2px solid `colorBorder` black |
| Left spacing | 16px (back arrow + gap + title) |
| Back arrow | Icon 24px, `colorFg` black |
| Title | `titleSm` (16px / Bold / 0.5 tracking / UPPERCASE) |
| Right action | `label` (12px / Bold / 1.5 tracking / UPPERCASE) |
| Right action touch | >= 44x44px |

**States:**
- Title always left-aligned
- Right action text on press: pressed → `colorAccent` red
- Right action hidden when not provided

### 1.2 Divider

A simple horizontal line used to separate sections.

| Property | Spec |
|----------|------|
| Height | 2px |
| Color | `colorBorder` black |
| Margin vertical | `space-6` (24px) |
| Horizontal | full width (no margin on edges) |

### 1.3 StatusDot

Small circle indicating lesson status.

```
⬤ = completed (colorFg black)
◯ = current / in-progress (colorAccent red outline)
○ = locked / not-started (colorMuted light gray)
```

| Property | Spec |
|----------|------|
| Diameter | 12px |
| Completed | `colorFg` black fill |
| Current | 2px `colorAccent` red stroke, no fill |
| Locked | `colorMuted` #F2F2F2 fill |
| In-progress | same as current (red outline) |

### 1.4 Icon (Platform Native)

Icons are functional elements, never decorative.

| Size | Usage |
|------|-------|
| 24px | Tab bar, back arrow, standalone action |
| 20px | Inline with text (lesson status, hint) |
| 16px | Dense UI (step progress label) |

- **Style**: Material Icons (Android) / SF Symbols (iOS)
- **Color**: `colorFg` black by default; `colorAccent` red for active/signal states

---

## 2. Composite Components

### 2.1 Button

Strictly rectangular, no border radius, no shadow.

**Primary Button** (main action):

```
┌──────────────────────────────────────┐
│            NEXT STEP                  │  ← height 52px
└──────────────────────────────────────┘
```

| Property | Primary | Secondary |
|----------|---------|-----------|
| Background | `colorFg` black | `colorBg` white |
| Border | 2px `colorFg` black | 2px `colorFg` black |
| Text color | `colorBg` white | `colorFg` black |
| Label | `labelLg` (14px / Bold / 1px tracking / UPPERCASE) | same |
| Height | 52px | 52px |
| Padding H | 24px | 24px |
| Min touch | 44x44px | 44x44px |

**States (press):**

| State | Primary | Secondary |
|-------|---------|-----------|
| Default | Black bg, white text | White bg, black border + text |
| Pressed | Red bg (`colorAccent`), white text | Red border + red text |
| Disabled | `colorMuted` bg, 50% opacity text | `colorMuted` border, 50% opacity text |

**Text-only Button** (inline action, e.g. "Check Update"):

| Property | Spec |
|----------|------|
| Label | `label` (12px / Bold / 1.5 tracking / UPPERCASE) |
| Underline | 1px bottom border on `colorAccent` red |
| Pressed | Text turns `colorAccent` red |
| Height | auto (min 44px touch) |

### 2.2 Card

| Property | Spec |
|----------|------|
| Background | `colorBg` white |
| Border | 2px `colorBorder` black |
| Border radius | 0px |
| Padding | `space-5` (20px) |
| Margin bottom | `space-3` (12px) — if in a list |

**Pressable Card** (e.g. lesson row):

| State | Behavior |
|-------|----------|
| Idle | White bg, black border |
| Pressed | `colorFg` black bg, white text + white border |
| Disabled/Locked | `colorMuted` bg, 50% opacity text, gray border |

### 2.3 TextInput

```
┌──────────────────────────────────────┐
│ User input text here                  │  ← 2px black border, 0 radius
└──────────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Border | 2px `colorBorder` black |
| Border radius | 0px |
| Background | `colorBg` white |
| Height | 48px (single line) |
| Padding H | 16px |
| Text | `body` (16px / Regular) |

**States:**

| State | Spec |
|-------|------|
| Default | Black border, white bg |
| Focus | `colorAccent` red border, white bg |
| Error | `colorAccent` red border, red text |
| Disabled | `colorMuted` bg, `colorMuted` border |

**Underline variant** (for fill-in-blank):

```
Every ______, Anna goes to the ______.
     └── 2px dashed black underline ──┘
```

- No border box, just a 2px dashed underline
- Text typed: `body` 16px, `colorFg` black
- Placeholder: `caption` 12px, 30% opacity black
- Correct: underline turns solid black
- Wrong: underline turns `colorAccent` red, correct answer shown below

### 2.4 BottomSheet (Modal)

Used for WordTooltip, ResumeOverlay, ConfirmDialog.

```
┌──────────────────────────────────────┐
│░░░░░░░░░░  40% black overlay  ░░░░░░░░│ ← tap to close
│                                      │
│┌────────────────────────────────────┐│
││                            [X]     ││ ← 2px black top border
││  MARKET                            ││ ← titleLg 26px
││  /ˈmɑːrkɪt/                        ││ ← body 14px
││                                    ││
││  市场; 集市                        ││ ← body 16px
││                                    ││
││  "She buys fresh vegetables at     ││
││   the market."                     ││ ← bodySm 14px
││                                    ││
││  [  ADD TO REVIEW  ]               ││ ← full-width black button
│└────────────────────────────────────┘│
└──────────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Overlay | 40% `#000000` opacity, instant appear |
| Sheet bg | `colorBg` white |
| Sheet top border | 2px `colorBorder` black |
| Sheet padding | `space-5` (20px) |
| Max height | 70% of screen |
| Close | Tap overlay OR X button (24px icon, top-right) |
| Animation | Instant appear (no slide, no fade — Swiss directness) |

### 2.5 ProgressDots

Used for step progress bar (1-6 at top of lesson screens).

```
  ①──②──③──④──⑤──⑥
  │   │   │   │   │   │
  ⬤──◯──○──○──○──○
```

| Element | Spec |
|---------|------|
| Dot diameter | 12px |
| Connecting line | 2px height, `colorMuted` light gray |
| Completed dot | `colorFg` black fill |
| Current dot | 2px `colorAccent` red stroke, no fill |
| Future dot | `colorMuted` #F2F2F2 fill |
| Label below | `caption` (12px / Medium / UPPERCASE), centered under each dot |

---

## 3. Page-Specific Components

### 3.1 TreeRow (Course List: Book / Unit / Lesson)

```
┌──────────────────────────────────────┐
│ ▼  BOOK 1: FOUNDATIONS          [12] │ ← BookRow (48px)
├──────────────────────────────────────┤
│   ▼  UNIT 1: SIMPLE PRESENT          │ ← UnitRow (44px, indented 16px)
│   ┌────────────────────────────────┐ │
│   │ 🟢  1-1  A Day at the Market  │ │ ← LessonCard (52px, indented 32px)
│   ├────────────────────────────────┤ │
│   │ ⬜  1-2  Shopping List         │ │
│   ├────────────────────────────────┤ │
│   │ 🔒  1-3  At the Zoo           │ │
│   └────────────────────────────────┘ │
└──────────────────────────────────────┘
```

**BookRow:**

| Property | Spec |
|----------|------|
| Height | 48px |
| Indent | 0px |
| Bottom border | 2px `colorBorder` black |
| Title | `title` (20px / Bold / UPPERCASE) |
| Right side | `label` (12px) lesson count, or chevron |

**UnitRow:**

| Property | Spec |
|----------|------|
| Height | 44px |
| Left indent | 16px |
| Bottom border | 2px `colorBorder` black |
| Title | `titleSm` (16px / Bold / UPPERCASE) |
| Right side | chevron icon 20px |

**LessonCard:**

| Property | Spec |
|----------|------|
| Height | 52px |
| Left indent | 32px (from screen edge) |
| Border | 2px `colorBorder` black (full card) |
| Padding H | 16px |
| Status icon | StatusDot or custom (12px) |
| Title | `body` (16px / Regular, not uppercase) |
| Right side | chevron or update badge |
| Pressed state | Full black bg inversion |

**Lesson Status Visuals:**

| Icon | Meaning |
|------|---------|
| 🟢 (black dot) | Downloaded, not started |
| ⬜ (light gray dot) | Not downloaded |
| 🔒 (gray dot + dim) | Locked (linear mode, prerequisite not met) |
| 🔄 (red dot) | Has update available |
| ✓ (black check) | Completed |

### 3.2 PassageView (Step 1)

```
┌──────────────────────────────────────┐
│   A Day at the Market                 │ ← titleLg 26px, UPPERCASE
│                                      │
│   Every Sunday, Anna goes to the      │
│   market. She buys fresh vegetables   │ ← body 16px, line-height 24px
│   and fruit. She likes to buy...      │
│                                      │
│ ┌────────────────────────────────────┐│
│ │ SIMPLE PRESENT TENSE               ││ ← GrammarCard
│ │ 表示习惯性动作、一般真理            ││
│ │ "She goes" / "They buy"            ││
│ └────────────────────────────────────┘│
└──────────────────────────────────────┘
```

**GrammarCard:**

| Property | Spec |
|----------|------|
| Background | `colorMuted` #F2F2F2 |
| Border | 2px `colorBorder` black |
| Padding | `space-4` (16px) |
| Title | `label` (12px / Bold / UPPERCASE) |
| Body | `bodySm` (14px / Regular) |

**TTS Controls:**
- 🔊 button: 24px icon, `colorFg` black
- Playing state: icon changes to ⏸
- Speed selector: `caption` 12px, inline buttons (0.75x / 1.0x / 1.5x)
- Sentence highlight: active sentence bg turns `colorMuted` #F2F2F2

### 3.3 FillBlank (Step 2)

```
  1/6                                   ← label 12px, top-left

  Every ______, Anna goes to the ______.
       ↑ 2px dashed black underline      ↑ inline blank
       placeholder: "type here" (caption 12px, 30% opacity)

  Hint: S__________ (n.)                ← bodySm 14px, italic (only italic allowed)

  [ CHECK ]                             ← Secondary button, right-aligned
```

**Blank Input:**
| Property | Idle | Focus | Correct | Wrong |
|----------|------|-------|---------|-------|
| Underline | 2px dashed black | 2px solid `colorAccent` red | 2px solid black | 2px solid `colorAccent` red |
| Text | `body` 16px | same | same | same (strikethrough) |
| Feedback | — | — | ✓ icon right | ✗ icon + show answer below in `caption` |

**Bottom bar:**

| Element | Spec |
|---------|------|
| Score | `label` 12px, left-aligned "CORRECT: 3/6" |
| Action | Primary or Secondary Button, right-aligned |
| Next allowed | score >= 60% |
| Retry max | 3 attempts, after that force allow next |

### 3.4 Flashcard (Step 3)

```
┌──────────────────────────────────────┐
│       market                          │ ← titleLg 26px, center
│       /ˈmɑːrkɪt/                      │ ← body 14px, center
│                                      │
│       [🔊]                            │ ← icon 24px
│                                      │
│                                      │
│ ┌────────────────────────────────────┐│
│ │   ← DON'T KNOW    KNOW →          ││ ← swipe buttons
│ └────────────────────────────────────┘│
│                         3 / 20        │ ← caption 12px, bottom-right
└──────────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Card size | Full width minus margins, aspect ratio ~3:4 |
| Border | 2px `colorBorder` black |
| Front | Word (`titleLg` 26px), phonetics (`body` 14px), 🔊 button |
| Back (flipped) | Definition (`body` 16px), example (`bodySm` 14px) |
| Flip animation | 300ms linear, Y-axis rotation |
| Swipe left | Mark as "don't know", card slides left |
| Swipe right | Mark as "know", card slides right |
| Bottom buttons | Text-only, `label` 12px UPPERCASE |

**Known/Unknown stats:**
- Show after all cards reviewed: "KNOWN: 14 / UNKNOWN: 6"
- Unknown words automatically added to review list

### 3.5 MatchingPair (Step 3 Matching Mode)

```
┌──────────────┐    ┌──────────────┐
│    market    │    │     市场     │
├──────────────┤    ├──────────────┤
│   shopping   │    │    购物      │
├──────────────┤    ├──────────────┤
│   vegetable  │    │    蔬菜      │
└──────────────┘    └──────────────┘
  Left column         Right column
  (English)           (Chinese)
```

| Property | Spec |
|----------|------|
| Card size | 44px height, fills column width |
| Border | 2px `colorBorder` black |
| Background | `colorBg` white |
| Selected | `colorMuted` bg #F2F2F2 |
| Correct match | Background snaps to `colorFg` black, white text |
| Wrong match | Border snaps to `colorAccent` red, reset after 500ms |
| Layout | 2 columns, gap 16px |

### 3.6 SpellingInput (Step 3 Spelling Mode)

```
  ┌──────────────────────────────────────┐
  │  Type what you hear:                  │
  │                                      │
  │  [🔊]                                │
  │                                      │
  │  m a r k e t                         │ ← letter squares
  │  ┌─┬─┬─┬─┬─┬─┐                      │
  │  │m│a│r│k│e│t│                      │ ← each 32x32px
  │  └─┴─┴─┴─┴─┴─┘                      │   2px black border
  │                                      │
  │  [CHECK]                             │
  └──────────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Letter square | 32x32px, 2px `colorBorder` black, 0 radius |
| Correct letter | `colorFg` black |
| Wrong letter | `colorAccent` red + strikethrough |
| 🔊 repeat | Press to replay TTS |

### 3.7 ListeningCard (Step 4)

```
  ┌──────────────────────────────────────┐
  │   STEP 04 — LISTENING                │ ← label 12px, top
  │                                      │
  │   [🔊  PLAY AUDIO]                    │ ← Button, 52px height
  │                                      │
  │   ┌────────────────────────────────┐ │
  │   │  What does Anna buy at the     │ │ ← QuestionCard
  │   │  market?                       │ │    body 16px
  │   │                                │ │
  │   │  ○  A. Vegetables and fruit    │ │ ← Option, 44px height
  │   │  ○  B. Books and pens          │ │    2px bottom border
  │   │  ○  C. Clothes and shoes       │ │
  │   └────────────────────────────────┘ │
  │                                      │
  │            [CHECK]                    │
  └──────────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Option height | 44px |
| Option border | 2px bottom border `colorBorder` |
| Selected | `colorFg` black radio dot (◉) |
| Correct | Black bg, white text, ✓ |
| Wrong | Red bg, white text, ✗, show correct answer |

### 3.8 ReadingQA (Step 5)

```
  ┌──────────────────────────────────────┐
  │   Read the passage and answer:        │
  │                                      │
  │   ┌────────────────────────────────┐ │
  │   │ Every Sunday, Anna goes to...   │ │ ← Passage (scrollable)
  │   │ ...                             │ │    body 16px
  │   └────────────────────────────────┘ │
  │                                      │
  │   True or False:                     │
  │                                      │
  │   ┌────────────────────────────────┐ │
  │   │ Anna goes to the market every  │ │ ← StatementCard
  │   │ Saturday.                      │ │    body 16px
  │   │                                │ │
  │   │    [TRUE]    [FALSE]           │ │ ← Inline buttons
  │   └────────────────────────────────┘ │
  │                                      │
  │            [NEXT]                     │
  └──────────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Statement | `body` 16px, bordered card |
| True/False | Secondary style buttons, inline, 44px height |
| Correct | Selected button: black bg, white text |
| Wrong | Selected button: red bg, white text + show correct |

### 3.9 SpeakingCard (Step 6)

```
  ┌──────────────────────────────────────┐
  │   Listen and repeat:                  │
  │                                      │
  │   [🔊]  "Every Sunday, Anna goes     │ ← TTS play, whole sentence
  │          to the market."             │
  │                                      │
  │   ┌────────────────────────────────┐ │
  │   │                                │ │ ← Waveform visualization
  │   │    [🔴 RECORDING...]           │ │    body 14px
  │   │                                │ │
  │   └────────────────────────────────┘ │
  │                                      │
  │   [▶  PLAY MY RECORDING]             │
  │   [⟳  TRY AGAIN]                     │
  │                                      │
  │   [💡  SHOW TEXT]                     │ ← reveals passage text
  └──────────────────────────────────────┘
```

| Property | Spec |
|----------|------|
| Record button | `colorAccent` red bg, white text, pulsing red dot |
| Play recording | Secondary button |
| Try again | Text-only button, `label` 12px |
| Show text | Text-only button, `label` 12px |
| Waveform | Skeleton: `colorMuted` bg, 2px black border |

### 3.10 DownloadProgressCard

```
  ┌──────────────────────────────────────┐
  │  DOWNLOADING...                       │ ← label 12px, top
  │                                      │
  │  ████████████░░░░░░  65%             │ ← ProgressBar
  │                                      │
  │  lesson-1-2.json                     │ ← bodySm 14px
  │  Estimated time remaining: 3s        │ ← caption 12px
  │                                      │
  │  [CANCEL]                            │ ← Secondary button
  └──────────────────────────────────────┘
```

**ProgressBar:**

| Property | Spec |
|----------|------|
| Height | 8px |
| Background | `colorMuted` #F2F2F2, 2px black border |
| Fill | `colorFg` black, left-to-right |
| Percentage | `label` 12px, right-aligned |

---

## 4. Component Architecture (File Structure)

All shared components live under `ui/components/`:

```
app/src/main/java/com/neoconcept/
├── ui/
│   ├── components/
│   │   ├── Button.kt
│   │   ├── Card.kt
│   │   ├── ScreenHeader.kt
│   │   ├── TextInput.kt
│   │   ├── BottomSheet.kt
│   │   ├── ProgressDots.kt
│   │   ├── StatusDot.kt
│   │   ├── Divider.kt
│   │   ├── ProgressBar.kt
│   │   ├── GrammarCard.kt
│   │   ├── PassageView.kt           # reusable passage text block
│   │   ├── OptionList.kt            # radio-button style options (Step 4-5)
│   │   └── TTSControlBar.kt        # play + speed selector
│   └── theme/
│       └── Theme.kt                 # Design tokens (color, space, border, typography)
├── features/
│   ├── course/
│   │   ├── BookRow.kt
│   │   ├── UnitRow.kt
│   │   └── LessonCard.kt
│   ├── lesson/
│   │   ├── PassageStep.kt
│   │   ├── FillBlanksStep.kt
│   │   ├── VocabExerciseStep.kt
│   │   │   ├── FlashcardMode.kt
│   │   │   ├── SpellingMode.kt
│   │   │   └── MatchingMode.kt
│   │   ├── ListeningStep.kt
│   │   ├── ReadingStep.kt
│   │   └── SpeakingStep.kt
│   ├── download/
│   │   └── DownloadProgressCard.kt
│   ├── stats/
│   │   └── StatsCard.kt
│   └── settings/
│       └── SettingsRow.kt
└── data/
    ├── repository/
    └── model/
```

---

## 5. Design Consistency Rules

1. **Every interactive component uses explicit press states** — never rely on default opacity fade, enforce explicit press state styling
2. **All borders are 2px** — never 1px (keeps Swiss boldness on mobile)
3. **Zero `borderRadius`** — everywhere. If something needs rounding, reconsider the design
4. **No hardcoded colors** — always reference `Theme.kt` tokens
5. **UPPERCASE** for titles and labels via code (`uppercase()`) — not by typing uppercase in data, so it's transformable per locale
6. **Left alignment** for all body text and headings — only buttons and single-line elements may center
7. **Touch targets always >= 44x44px** — verify in PR review