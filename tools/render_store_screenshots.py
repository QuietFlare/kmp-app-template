#!/usr/bin/env python3
"""Compose captioned Play-listing screenshots from the raw app captures.

    python3 tools/render_store_screenshots.py

The raw captures under store/play/{phone,tablet}/ are also what F-Droid shows (fastlane's
phoneScreenshots is a copy of the phone set), and F-Droid wants plain screenshots, so captions
go only into store/play/captioned/, the Play upload; never write over the raw captures. Each
frame keeps the source dimensions, so Play's aspect-ratio rules hold by construction. The
status bar is cropped because an emulator clock and battery read as a phone screenshot, not a
store asset. Palette and serif come from design/tokens.json via render_icon.py.
"""
import os
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from render_icon import rgb, T  # noqa: E402

SERIF = "/System/Library/Fonts/Supplemental/Georgia.ttf"
ROOT = Path(__file__).resolve().parent.parent
RAW = ROOT / "store" / "play"
OUT = ROOT / "store" / "play" / "captioned"

# Fraction of the source height taken by the status bar; cropped rather than covered to avoid a seam.
STATUS_BAR = 0.052

# Headline and optional second line, keyed by file stem; a capture with no entry is skipped with a warning.
# Shot 1 carries the differentiator, since the first frame is the only one most people see.
CAPTIONS = {
    "01-home": ("Where you are today", "No account. No cloud."),
    "02-about": ("Everything stays on your phone", None),
}


def cream_wash(size):
    w, h = size
    inner, outer = rgb(T["icon"]["backgroundIn"]), rgb(T["icon"]["backgroundOut"])
    sw, sh = 96, max(1, round(96 * h / w))
    small = Image.new("RGB", (sw, sh), outer)
    px = small.load()
    cx, cy = sw * 0.5, sh * 0.34
    far = max((cx**2 + cy**2) ** 0.5, ((sw - cx) ** 2 + cy**2) ** 0.5, (cx**2 + (sh - cy) ** 2) ** 0.5)
    for y in range(sh):
        for x in range(sw):
            t = min((((x - cx) ** 2 + (y - cy) ** 2) ** 0.5) / far, 1.0)
            px[x, y] = tuple(round(a + (b - a) * t) for a, b in zip(inner, outer))
    return small.resize((w, h), Image.BICUBIC)


def compose(src: Path, headline: str, sub: str | None) -> Image.Image:
    shot = Image.open(src).convert("RGB")
    w, h = shot.size
    shot = shot.crop((0, round(h * STATUS_BAR), w, h))
    band = round(h * 0.16)
    canvas = cream_wash((w, h))
    draw = ImageDraw.Draw(canvas)
    ink, ink_soft = rgb(T["colors"]["ink"]), rgb(T["colors"]["inkSoft"])
    try:
        head_font = ImageFont.truetype(SERIF, round(w * 0.062))
        sub_font = ImageFont.truetype(SERIF, round(w * 0.036))
    except OSError:
        head_font = sub_font = ImageFont.load_default()
    y = round(band * 0.28)
    draw.text((w / 2, y), headline, font=head_font, fill=ink, anchor="ma")
    if sub:
        draw.text((w / 2, y + round(w * 0.085)), sub, font=sub_font, fill=ink_soft, anchor="ma")
    # The capture, scaled to sit under the band with a little breathing room.
    scale = (h - band) / shot.height
    shot = shot.resize((round(shot.width * scale), h - band), Image.LANCZOS)
    canvas.paste(shot, ((w - shot.width) // 2, band))
    return canvas


def main():
    count = 0
    for device in ("phone", "tablet"):
        src_dir = RAW / device
        if not src_dir.is_dir():
            continue
        out_dir = OUT / device
        out_dir.mkdir(parents=True, exist_ok=True)
        for src in sorted(src_dir.glob("*.png")):
            caption = CAPTIONS.get(src.stem)
            if caption is None:
                print(f"warning: no caption for {src.relative_to(ROOT)}, skipped", file=sys.stderr)
                continue
            out = out_dir / src.name
            compose(src, *caption).save(out, "PNG")
            print("wrote", out.relative_to(ROOT))
            count += 1
    if count == 0:
        print("no captures found under store/play/{phone,tablet}/, nothing written")
    return 0


if __name__ == "__main__":
    sys.exit(main())
