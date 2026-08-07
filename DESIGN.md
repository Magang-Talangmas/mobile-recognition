---
name: Stellar Attendance
colors:
  surface: '#f8f9ff'
  surface-dim: '#cddbf2'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e6eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d6e3fb'
  on-surface: '#0f1c2d'
  on-surface-variant: '#454652'
  inverse-surface: '#243143'
  inverse-on-surface: '#eaf1ff'
  outline: '#767683'
  outline-variant: '#c6c5d4'
  surface-tint: '#4c56af'
  primary: '#000666'
  on-primary: '#ffffff'
  primary-container: '#1a237e'
  on-primary-container: '#8690ee'
  inverse-primary: '#bdc2ff'
  secondary: '#7e5700'
  on-secondary: '#ffffff'
  secondary-container: '#feb300'
  on-secondary-container: '#6a4800'
  tertiary: '#181b1e'
  on-tertiary: '#ffffff'
  tertiary-container: '#2d3033'
  on-tertiary-container: '#95989b'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e0e0ff'
  primary-fixed-dim: '#bdc2ff'
  on-primary-fixed: '#000767'
  on-primary-fixed-variant: '#343d96'
  secondary-fixed: '#ffdeac'
  secondary-fixed-dim: '#ffba38'
  on-secondary-fixed: '#281900'
  on-secondary-fixed-variant: '#604100'
  tertiary-fixed: '#e0e3e6'
  tertiary-fixed-dim: '#c4c7ca'
  on-tertiary-fixed: '#181c1f'
  on-tertiary-fixed-variant: '#44474a'
  background: '#f8f9ff'
  on-background: '#0f1c2d'
  surface-variant: '#d6e3fb'
typography:
  headline-lg:
    fontFamily: Hanken Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Hanken Grotesk
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Hanken Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Hanken Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  label-sm:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  container-padding: 20px
  stack-gap: 16px
  item-gap-sm: 8px
  section-margin: 32px
  gutter: 16px
---

## Brand & Style

The design system for PT Talangmas Anugerah Semesta is built on the pillars of **precision, reliability, and modern efficiency**. As an attendance application, it must convey a sense of professional accountability while remaining effortless and welcoming for daily use by employees.

The visual style is **Corporate / Modern with a Glassmorphic influence**. It leverages deep, authoritative navy tones contrasted against a high-energy "Anugerah Yellow" to guide action. Taking inspiration from the reference imagery, the interface uses soft background blurs, subtle mesh gradients, and "floating" card structures to create a sense of depth and technological sophistication. The overall atmosphere is clean and high-tech, ensuring that the critical task of time-tracking feels like a premium, frictionless experience.

## Colors

The palette is derived directly from the corporate identity of PT Talangmas Anugerah Semesta.

*   **Primary (Deep Navy):** Used for headers, primary actions, and brand-heavy backgrounds. It represents stability and professional rigor.
*   **Secondary (Vibrant Yellow/Orange):** This is the high-visibility accent color. It is reserved for critical calls to action (CTA) like "Clock In," status indicators, and highlighting current progress.
*   **Surface & Background:** The application primarily uses a light mode with very soft off-white and cool grey surfaces (`#F5F7FB`) to maintain high legibility.
*   **Gradients:** Use soft, diagonal gradients for landing screens and header containers to emulate the depth seen in the reference material.

## Typography

This design system utilizes **Hanken Grotesk** for its contemporary, geometric clarity. The typeface offers a balance between mechanical precision and approachable warmth, making it ideal for a professional utility app.

*   **Headlines:** Use Bold and SemiBold weights with tighter letter-spacing to create a strong visual anchor.
*   **Body:** Regular weight at 16px is the standard for data entry and general information.
*   **Labels:** Medium and SemiBold weights at smaller sizes ensure that metadata and form headings remain legible and organized. 
*   **Color Application:** Headlines should typically appear in Primary Navy on light backgrounds, or White on dark/gradient backgrounds.

## Layout & Spacing

The design system follows a **fluid grid model** specifically optimized for mobile-first interactions.

*   **Safe Areas:** A minimum horizontal margin of 20px is maintained across all screens to ensure content does not hit the edge of the device.
*   **Spacing Rhythm:** An 8px linear scale is used. Most vertical stacks utilize 16px (`stack-gap`) or 24px increments.
*   **Cards:** Dashboard elements are organized into cards that span the full width of the container minus the margins.
*   **Grid:** For tablet and desktop views, a 12-column grid is employed with 24px gutters, though the primary focus remains a centered, single-column utility layout for mobile users.

## Elevation & Depth

Visual hierarchy is achieved through **Tonal Layering** and **Ambient Shadows**.

1.  **Level 0 (Base):** The primary background, often a very light cool grey or a deep navy gradient on the home screen.
2.  **Level 1 (Cards):** White surfaces with a very soft, diffused shadow (15% opacity Primary Color, 20px blur) to create a "floating" effect.
3.  **Level 2 (Active Elements):** Buttons and active chips use a more pronounced shadow or a subtle inner glow to signify interactability.
4.  **Glassmorphism:** Use `backdrop-filter: blur(12px)` on bottom navigation bars and modal overlays to maintain context with the layers beneath, mirroring the reference aesthetic.

## Shapes

The shape language is defined by **Round Eight** (0.5rem base) logic. This creates a soft, modern feel that avoids the "aggressive" nature of sharp corners while remaining more structured than a fully pill-shaped system.

*   **Small Elements (Chips, Inputs):** Use `rounded-md` (8px).
*   **Standard Cards:** Use `rounded-lg` (16px).
*   **Large Containers/Modals:** Use `rounded-xl` (24px) for top corners to create a distinct "sheet" appearance.
*   **Primary Action Buttons:** Can transition to pill-shaped for maximum distinction from other UI elements.

## Components

### Buttons
*   **Primary:** Vibrant Yellow background with Deep Navy text. High contrast for "Clock In/Out."
*   **Secondary:** Ghost style with Deep Navy border and text.
*   **Loading State:** Replace text with a spinning monochromatic icon.

### Input Fields
*   **Default:** White background, 1px light grey border, 8px corner radius.
*   **Focus:** Border shifts to Primary Navy with a 2px soft outer glow.

### Cards (The "Work" Card)
*   The central component of the attendance app. It features a clean white surface, a high-contrast time display (Headline LG), and the Primary CTA button at the bottom.

### Chips & Badges
*   Used for status updates (e.g., "On Time," "Late," "Overtime"). These should use low-saturation background tints of green, red, or yellow with high-saturation text.

### Bottom Navigation
*   A glassmorphic bar that sits at the bottom of the screen, using icons with a 2pt stroke weight. The active state is highlighted by a Secondary (Yellow) dot or icon color shift.