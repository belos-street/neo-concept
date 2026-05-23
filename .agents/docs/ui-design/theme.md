# Design Style: Swiss International — Mobile (React Native)

## Design Philosophy

**The International Typographic Style (Swiss Style)** — a philosophy of objective communication. Design recedes to let content speak. Every decision is justified by the content's needs.

**Core Tenets for Mobile:**

1. **Typography is the Interface** — Type is the primary structural element. Scale, weight, and position create hierarchy. No decorations needed.

2. **The Grid as Law** — Content follows a strict vertical rhythm. Left-aligned, ragged-right text blocks. No center-alignment except for single-line elements (tab bar icons).

3. **Active Negative Space** — White space is structural. It defines boundaries and gives weight to content. On mobile, this translates to generous padding (16-24px) and clear section separation.

4. **Zero Decoration** — No rounded corners, no drop shadows, no gradients. Flat design with high contrast. Depth comes from content layering and border lines, not visual effects.

5. **Functional Color** — Black/White base. Red (#FF3000) is the only signal color, used sparingly for CTAs, errors, and critical emphasis only.

## Design Token System

### Colors

| Token | Hex | Role |
|-------|-----|------|
| `colorBg` | `#FFFFFF` | App background, card surface |
| `colorFg` | `#000000` | Primary text, icons |
| `colorMuted` | `#F2F2F2` | Secondary background (section separators, inactive states) |
| `colorAccent` | `#FF3000` | CTAs, errors, active indicators, section labels |
| `colorBorder` | `#000000` | All borders, dividers |
| `colorSuccess` | `#000000` | Correct answer feedback (solid black check) |
| `colorError` | `#FF3000` | Incorrect answer feedback, retry prompts |

### Typography Scale (Mobile)

All text uses Inter font family (Helvetica/Akzidenz-Grotesk).

| Name | Size | Weight | LetterSpacing | Usage |
|------|------|--------|---------------|-------|
| `titleLg` | 26px | 700 (Bold) | 0.5px | Screen title (uppercase) |
| `title` | 20px | 700 (Bold) | 0.5px | Section heading (uppercase) |
| `titleSm` | 16px | 700 (Bold) | 0.5px | Card title, lesson name (uppercase) |
| `labelLg` | 14px | 700 (Bold) | 1px | Tab label, button text (uppercase, wide) |
| `label` | 12px | 700 (Bold) | 1.5px | Badge, step label (uppercase, wider) |
| `body` | 16px | 400 (Regular) | 0px | Passage text, body content |
| `bodySm` | 14px | 400 (Regular) | 0px | Secondary body, description |
| `caption` | 12px | 500 (Medium) | 0.5px | Timestamp, hint text |

**Heads-up display (HUD) style**: Step progress numbers (1/6) use `titleLg` Bold, 0.5 tracking. Small "STEP 02" labels use `label` weight and tracking.

### Spacing (4px grid)

| Token | Value | Usage |
|-------|-------|-------|
| `space-1` | 4px | Micro spacing (icon gap) |
| `space-2` | 8px | Tight spacing (inline elements) |
| `space-3` | 12px | Default spacing (text gap) |
| `space-4` | 16px | Section padding horizontal |
| `space-5` | 20px | Card internal padding |
| `space-6` | 24px | Section spacing vertical |
| `space-8` | 32px | Large section gap |
| `space-10` | 40px | Screen top offset |

**Edge margins**: Screen content always uses `space-4` (16px) left/right padding. Status bar offset: `space-10` (40px) on supported devices.

### Borders & Radius

| Token | Value | Usage |
|-------|-------|-------|
| `borderWidth` | 2px | Standard element border |
| `borderWidthThick` | 3px | Tab bar top border, section separators |
| `borderRadius` | 0px | **All** elements — strictly rectangular |

### Shadows

**None.** Zero drop shadows. The design is flat. Visual hierarchy is achieved through:
- Border lines (2-3px solid black)
- Background color contrast (white vs `#F2F2F2`)
- Typography scale contrast
- Content spacing

---

## Mobile-Specific Adaptations

### Touch Interactions (Replacing Hover)

Mobile has no hover. Replace Web hover effects with press state (`Pressable` pressed):

| Web (hover) | Mobile (press) |
|-------------|----------------|
| Color inversion on hover | Background color snap on `Pressable` press |
| Scale(1.0 → 1.05) on hover | Scale(1.0 → 0.97) on press (brief) |
| Underline on hover | No underline; use bg color change |
| Shadow elevation on hover | **Not used** — no shadows |

### Touch Target Minimum

All interactive elements: **44x44px** minimum. Buttons with text adapt to content width + 24px horizontal padding, but never below 44px height.

### Safe Area

- Top: offset by status bar height (use `react-native-safe-area-context`)
- Bottom: offset by navigation bar / home indicator (use `SafeAreaView` edge)
- Tab bar sits above the safe bottom area

### Iconography

Use `react-native-vector-icons` (MaterialIcons outline style) or custom SVG icons.
- **Stroke width**: 2px (matching border weight)
- **Icons are functional** — always enclosed in context, never standalone decorative
- **Icon size**: 24px (standard), 20px (inline with text)

---

## Layout Rules

### Screen Shell

```
┌──────────────────────────────────┐
│ [safe area top: ~40px]           │
│ ┌──────────────────────────────┐ │
│ │ ← Screen Title    [action]   │ │ ← ScreenHeader, 48px height
│ ├──────────────────────────────┤ │
│ │                              │ │
│ │     ScrollView content       │ │ ← 16px horizontal padding
│ │                              │ │
│ ├──────────────────────────────┤ │
│ │ Tab1   Tab2   Tab3           │ │ ← TabBar, 60px height
│ └──────────────────────────────┘ │
│ [safe area bottom: ~20px]       │
└──────────────────────────────────┘
```

### Vertical Rhythm

- Sections separated by 24px vertical gap
- Cards within a section: 12px vertical gap
- Content inside a card: 20px internal padding
- Dividers (2px black line) used between distinct sections, not between cards

### Text Alignment

- **All body text**: flush-left, ragged-right
- **Buttons**: centered (single-line content)
- **Tab bar labels**: centered under icon
- **Headings**: left-aligned with screen margin

---

## Animation Principles

- **Feel**: Instant, mechanical, snappy
- **Duration**: 150-200ms (fast feedback)
- **Easing**: Linear or ease-out — no spring, no bounce
- **Allowed animations**:
  - Press feedback: scale 1.0→0.97, 100ms
  - Page transition: slide in from right (React Navigation default is acceptable)
  - Card flip (flashcard): 300ms, linear
  - Progress bar fill: 300ms, ease-out
  - Correct/wrong feedback: instant color snap, no fade
  - Modal overlay: instant appear (no slide-up — keeps Swiss directness)

---

## Accessibility

- **Contrast**: Black on White = 21:1 (exceeds AA). Red `#FF3000` on White = 6.5:1 (passes AA for large text)
- **Touch targets**: minimum 44x44px
- **Focus**: React Native handles focus rings per platform
- **Reduced motion**: `AccessibilityInfo.reduceMotionEnabled` → disable card flip animation
- **Semantics**: `accessibilityLabel` on all icon buttons, `accessibilityRole` on tabs and buttons

---

## Design Examples by Page

### Course List
- Book/Unit rows: 48px height, 2px bottom border, title uppercase Bold 16px on left, chevron icon on right
- Lesson rows: 52px height, indented by 16px per level, status icon on left, title body 16px
- "Check update" button: text-only, right-aligned, uppercase label 12px with underline

### Lesson Screen (Step Container)
- Step progress bar: 6 dots (12px diameter), connected by 2px line, completed=black, current=red outline, future=light gray
- Content area: fills remaining space, ScrollView
- "Next step" button: full-width at bottom, 52px height, black bg, white label uppercase 14px

### Fill Blank Exercise
- Blank input: dashed underline (placeholder), filled text uses body 16px
- Submit button: right-aligned, uppercase label 12px
- Feedback: correct=instant checkmark, wrong=red underline + show correct answer below

### Flashcard
- Card: white bg, 2px black border, fills width minus margins, aspect ratio ~3:4
- Swipe gesture = L/R to mark known/unknown
- Counter: "3/20" in bottom-right, label 12px

### Word Tooltip (Modal)
- Overlay: instant black 40% opacity
- Sheet: bottom-aligned, 2px black top border, white bg
- Content: word (titleLg 26px), phonetic (body 14px), definition (body 16px), example (bodySm 14px)
- Close: tap overlay or X button on top-right of sheet

---

## Summary: What NOT to do

- ❌ No rounded corners anywhere
- ❌ No drop shadows or elevation
- ❌ No gradients
- ❌ No background patterns/textures (too complex for mobile and unnecessary)
- ❌ No decorative illustrations
- ❌ No elastic/bounce animations
- ❌ No center-aligned body text
- ❌ No italic text
- ❌ No colored text (exception: red for accent only)