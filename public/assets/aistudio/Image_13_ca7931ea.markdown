---
name: Taraz Precision
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#45464f'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#767680'
  outline-variant: '#c6c5d0'
  surface-tint: '#4f5c8e'
  primary: '#000f3f'
  on-primary: '#ffffff'
  primary-container: '#172554'
  on-primary-container: '#808dc2'
  inverse-primary: '#b7c4fd'
  secondary: '#0051d5'
  on-secondary: '#ffffff'
  secondary-container: '#316bf3'
  on-secondary-container: '#fefcff'
  tertiary: '#00180d'
  on-tertiary: '#ffffff'
  tertiary-container: '#002f1e'
  on-tertiary-container: '#00a371'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b7c4fd'
  on-primary-fixed: '#071747'
  on-primary-fixed-variant: '#374475'
  secondary-fixed: '#dbe1ff'
  secondary-fixed-dim: '#b4c5ff'
  on-secondary-fixed: '#00174b'
  on-secondary-fixed-variant: '#003ea8'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  headline-xl:
    fontFamily: Vazirmatn
    fontSize: 32px
    fontWeight: '800'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Vazirmatn
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Vazirmatn
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 30px
    letterSpacing: 0em
  headline-sm:
    fontFamily: Vazirmatn
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0em
  body-lg:
    fontFamily: Vazirmatn
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 26px
    letterSpacing: 0em
  body-md:
    fontFamily: Vazirmatn
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: 0em
  body-sm:
    fontFamily: Vazirmatn
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: 0em
  label-lg:
    fontFamily: Vazirmatn
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0em
  label-md:
    fontFamily: Vazirmatn
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Vazirmatn
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.02em
  tabular-num:
    fontFamily: Vazirmatn
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1.5rem
  margin: 2rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system embodies precision, financial clarity, and institutional trust tailored specifically for high-frequency Persian desktop accounting and personal wealth management. It balances modern Swiss-inspired FinTech minimalism with native Right-to-Left (RTL) ergonomics. 

### Visual Tenets
- **Mathematical Restraint:** Every border, margin, and visual indicator follows structural necessity over decorative flair. Dense data tables and financial charts maintain breathing room through intentional macro/micro whitespace.
- **RTL-First Flow:** All cognitive paths begin top-right and terminate bottom-left. Action zones, chevron orientations, progress bars, and data entry flows naturally honor Persian visual scanning behavior.
- **Institutional Weight:** Deep Navy anchor tones project fiscal security and enterprise reliability, paired with high-contrast functional hues that communicate financial deltas (cash flow, expenditure spikes, budget thresholds) without ambiguity.

## Colors

The palette is engineered for prolonged operational sessions on high-density desktop displays, prioritizing WCAG 2.2 AA contrast ratios and distinct semantic signaling.

### Roles & Applications
- **Primary (`#172554` - Midnight Navy):** Serves as the cognitive anchor. Used for primary navigation frames, critical headers, structural badges, and assertive interactive targets.
- **Secondary (`#2563EB` - Tech Blue):** Expresses active state dynamics: primary button states, focus indicator outlines, interactive links, active tab lines, and selection radios.
- **Financial Status & Semantics:**
  - **Success / Inflow (`#10B981` - Emerald):** Positive balances, income vectors, completed ledger transactions, and budget safety indicators.
  - **Warning / Alert (`#F59E0B` - Warm Amber):** Nearing budget thresholds, pending reconciliations, fiscal review prompts.
  - **Error / Outflow (`#EF4444` - Coral Crimson):** Overdrawn categories, critical accounting disparities, destructive actions.
- **Surfaces & Linework:**
  - **Canvas Base:** Soft Slate (`#F8FAFC`) to minimize glare during financial audits.
  - **Surface Enclosures:** Pure White (`#FFFFFF`) to elevate analytical cards and tabular data.
  - **Structural Borders:** Subtle Slate (`#E2E8F0`) providing strict boundary clarity without visual vibration.
- **Text & Visual Hierarchy:**
  - **High-Emphasis:** Deep Charcoal (`#0F172A`) achieving a minimum 12:1 contrast against white surfaces.
  - **Muted / Auxiliary:** Slate Gray (`#64748B`) for Shamsi dates, secondary metadata, table headers, and currency units (`ریال / تومان`).

## Typography

The typography is built upon `Vazirmatn` (with fallbacks to `Shabnam` and system RTL grotesques), selected for its open counters, balanced baseline rhythm, and optimized Persian glyph rendering at small sizes.

### Numeric & Currency Formatting Rules
- **Tabular Figures:** All financial balances, account totals, and analytical metrics must enforce tabular numbers (`font-variant-numeric: tabular-nums`) to preserve column integrity in tables and transaction ledgers.
- **Dual Script Integration:** Persian numerals are mandated across standard Persian accounting views. When Latin characters or international IBAN/BIC codes appear, baseline-aligned sans-serif glyphs preserve horizontal optical parity.
- **RTL Baseline Adjustments:** Arabic-script diacritics and tall ascendings require an increased line-height ratio compared to Latin scripts to prevent ascender collision with top-level container borders.

## Layout & Spacing

The layout is optimized for modern desktop productivity displays (1280px to 1920px+).

### Desktop Canvas Architecture
- **Persistent Right Navigation Drawer:** Fixed desktop sidebar docked strictly to the right side (width: `260px` default, `72px` collapsed). Houses application brand, hierarchical ledger categories, bank reconciliations, and platform settings.
- **Horizontal Top Bar:** Spans the main viewport with a fixed height of `64px`. Anchors the Shamsi calendar indicator (`YYYY/MM/DD`), workspace switcher, active notification bell, and primary CTA.
- **Main Working Canvas:** 12-column dynamic grid system anchored inside an offset flex container. Columns separate via `1.5rem` gutters with an outer canvas edge padding of `2rem`.
- **RTL Alignment Logic:** Primary information anchors to the upper-right corner of each card or section. Action triggers, pagination controllers, and aggregate sums position towards the left edge.

## Elevation & Depth

This system avoids heavy, blurred drop shadows, utilizing crisp 1px borders paired with ultra-diffused, ambient tonal elevation to convey structural stack order without compromising desktop analytical density.

- **Level 0 (Base Surface):** Canvas background (`#F8FAFC`). Flat with no elevation.
- **Level 1 (Cards, Modules, Table Containers):** Surface Pure White (`#FFFFFF`) bounded by a subtle `1px solid #E2E8F0` border. Shadow: `0 1px 3px 0 rgba(15, 23, 42, 0.04), 0 1px 2px -1px rgba(15, 23, 42, 0.02)`.
- **Level 2 (Hover States, Flyouts, Popovers):** Elevated contextual tools. Shadow: `0 4px 6px -1px rgba(15, 23, 42, 0.07), 0 2px 4px -2px rgba(15, 23, 42, 0.05)`. Border remains active to retain crisp edges against varied card content.
- **Level 3 (Modal Windows & Core Dialogs):** Centered overlays. Shadow: `0 20px 25px -5px rgba(15, 23, 42, 0.12), 0 8px 10px -6px rgba(15, 23, 42, 0.08)`. Accompanied by a darkened background scrim (`rgba(15, 23, 42, 0.40)`) with backdrop blur (`backdrop-filter: blur(4px)`).

## Shapes

A balanced geometric corner profile (`roundedness: 2` / base `0.5rem` or `8px`) reinforces a modern yet enterprise-ready posture. 

### Radius Tokens
- **Base (0.5rem / 8px):** Primary interactive elements, input text fields, card modules, action buttons, table cell wrappers.
- **Large (1rem / 16px):** System modal dialogs, flyout drawers, comprehensive stat callouts.
- **Pill (Full Round / 9999px):** Status badges, numerical transaction trend indicators, user avatar chips, tag filters.

## Components

### 1. Navigation Sidebar (Desktop RTL)
- Docked to the right margin with a continuous left border (`1px solid #E2E8F0`).
- Navigation links consist of an icon on the right, typography aligned to the right, and optional badge or nested chevron on the left.
- Active state: `#EFF6FF` background fill, text and icon rendered in `#2563EB`, bounded by a `3px` solid indicator bar on the rightmost outer edge.

### 2. Header & Action Bar
- Features the live Shamsi date (`امروز، ۲۴ آبان ۱۴۰۳`) on the right side using `body-md` in `#64748B`.
- Fast Action CTA ("ثبت تراکنش جدید" / New Transaction) positioned on the far left, highlighted in Primary Blue (`#2563EB`) with a plus (`+`) icon prepended to the right of the label text.

### 3. Stat Summary Cards
- Metric containers bounded by `1px solid #E2E8F0` with `space-md` interior padding.
- Title label positioned at top-right; comparative trend badge (e.g., `+۱۲.۵٪` in emerald pill) positioned at top-left.
- Financial total rendered using `tabular-nums` in `headline-lg` (`#0F172A`) accompanied by the currency unit (`تومان` or `ریال`) in muted `label-sm`.

### 4. Financial Data Tables
- Header row styled in `#F8FAFC` background with a lower border of `1px solid #E2E8F0`. Header labels are right-aligned, rendered in `label-md` (`#64748B`).
- Row heights set to `52px` to optimize visual balance between row density and scan speed.
- Monetary credits are formatted in `#10B981` with a leading `+` sign; debits are displayed in `#0F172A` (or `#EF4444` when exceeding budget margins).
- Checkbox selections and contextual action dots align strictly to the left edge of each row.

### 5. Form Inputs & Currency Selectors
- RTL text entry: placeholder and input values align right; numeric input retains right-to-left numeral entry while formatting numbers with thousand-separator Persian commas (`،`).
- Attached currency markers (`تومان`) sit statically affixed on the left edge inside the border frame.
- Focus state: `1.5px solid #2563EB` with an ambient glow of `0 0 0 3px rgba(37, 99, 235, 0.12)`.

### 6. Budget Progress Bars
- Container height is `8px` with a pill-shaped track (`#E2E8F0`).
- The fill bar expands from right to left (RTL direction). 
- Color shifts contextually based on consumption: `#10B981` (0–75%), `#F59E0B` (76–90%), and `#EF4444` (>90% or over budget).

### 7. Status & Categorization Badges
- Pill-shaped (`rounded-full`) inline wrappers with padding `0.25rem 0.75rem`.
- Income/Verified: `#ECFDF5` background with `#047857` text.
- Review/Pending: `#FFFBEB` background with `#B45309` text.
- Danger/Overdue: `#FEF2F2` background with `#B91C1C` text.
- Structural/General: `#F1F5F9` background with `#334155` text.