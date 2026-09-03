# design/

| File | Purpose |
|---|---|
| `PRINCIPLES.md` | The house design rules. Read this first; it is what every app shares. |
| `tokens.json` | This app's tokens: palette, type scale, spacing, card, selection, control metrics. |
| `tokens.py` | Generator. Emits `Theme.swift`, `Theme.kt`, `colors.xml`, the launch colorset and storyboard, and the adaptive-icon background. `--check` exits non-zero when any is stale. |

```bash
python3 design/tokens.py          # after any token change
python3 design/tokens.py --check  # what CI's DesignTokensTest also enforces
```

Design canvases (Claude Design `.dc.html` artboards, mockups, contrast audits) live here too
when an app has them. Generated files stay out of `design/`; they are written straight into
the app modules so there is no copy step to forget.
