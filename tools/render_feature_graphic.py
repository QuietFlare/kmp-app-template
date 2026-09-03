#!/usr/bin/env python3
"""Render the Google Play feature graphic: exactly 1024x500, PNG, opaque.

    python3 tools/render_feature_graphic.py store/play/feature-graphic-1024x500.png

Play crops the banner differently across its surfaces and may overlay a play button in the
middle, so the mark and wordmark sit left of centre and the right side stays quiet. Colours
come from design/tokens.json and the mark from tools/render_icon.py, so banner, icon and app match.
"""
import json
import os
import sys

from PIL import Image, ImageDraw, ImageFont

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from render_icon import mark_layer, rgb, T  # noqa: E402

W, H = 1024, 500
SERIF = "/System/Library/Fonts/Supplemental/Georgia.ttf"
SS = 4

APP_NAME = "Starter"
TAGLINE = "One line that says what it does."   # Product copy; edit for the app.


def radial_cream(size):
    w, h = size
    inner, outer = rgb(T["icon"]["backgroundIn"]), rgb(T["icon"]["backgroundOut"])
    sw, sh = 128, round(128 * h / w)
    small = Image.new("RGB", (sw, sh), outer)
    px = small.load()
    cx, cy = sw * 0.31, sh * 0.60
    far = max((cx * cx + cy * cy) ** 0.5, ((sw - cx) ** 2 + (sh - cy) ** 2) ** 0.5)
    for y in range(sh):
        for x in range(sw):
            t = min((((x - cx) ** 2 + (y - cy) ** 2) ** 0.5) / far, 1.0)
            px[x, y] = tuple(round(a + (b - a) * t) for a, b in zip(inner, outer))
    return small.resize((w, h), Image.BICUBIC)


def main(out_path):
    big = (W * SS, H * SS)
    canvas = radial_cream(big)

    mark_size = 276 * SS
    mark = mark_layer(mark_size)
    canvas.paste(mark, (72 * SS, (H * SS - mark_size) // 2), mark)

    img = canvas.resize((W, H), Image.LANCZOS)
    draw = ImageDraw.Draw(img)
    ink, ink_soft = rgb(T["colors"]["ink"]), rgb(T["colors"]["inkSoft"])
    try:
        title_font = ImageFont.truetype(SERIF, 88)
        tag_font = ImageFont.truetype(SERIF, 30)
    except OSError:
        title_font = tag_font = ImageFont.load_default()
    draw.text((392, 178), APP_NAME, font=title_font, fill=ink)
    draw.text((396, 290), TAGLINE, font=tag_font, fill=ink_soft)

    img.save(out_path, "PNG")
    print("wrote", out_path, img.size, img.mode)


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
        "store", "play", "feature-graphic-1024x500.png"))
