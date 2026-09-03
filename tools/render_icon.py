#!/usr/bin/env python3
"""Render the app icon and launch mark from design/tokens.json.

    python3 tools/render_icon.py

Writes:
    iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/icon-1024.png   1024², NO alpha
    iosApp/iosApp/LaunchAssets/mark{,@2x,@3x}.png                    96/192/288, transparent
    store/play/play-icon-512.png                                     512², no alpha

The icon is opaque and square with no pre-rounded corners: App Store Connect rejects icons
with an alpha channel, and iOS applies its own mask. The launch mark is the same geometry on
a transparent ground. PLACEHOLDER: replace `draw_mark()` and keep its signature; the rest of
the pipeline (sizes, alpha rules, radial wash) keeps working. Match the shape in
androidApp/src/main/res/drawable/ic_launcher_foreground.xml (the iOS 100×100 design space
scaled by 0.72 onto the 108dp adaptive canvas).
"""
import json
import os
import sys

from PIL import Image, ImageDraw

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
T = json.load(open(os.path.join(ROOT, "design", "tokens.json")))

SS = 4  # supersample, then downscale, for clean curved edges


def rgb(h):
    h = h.lstrip("#")
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4))


def radial_wash(size, inner, outer, cx=0.5, cy=0.42, radius=0.78):
    """The icon ground: warm centre fading outward, matching ic_launcher_background.xml."""
    w = h = size
    sw = 128
    small = Image.new("RGB", (sw, sw), outer)
    px = small.load()
    ccx, ccy, far = sw * cx, sw * cy, sw * radius
    for y in range(sw):
        for x in range(sw):
            t = min((((x - ccx) ** 2 + (y - ccy) ** 2) ** 0.5) / far, 1.0)
            px[x, y] = tuple(round(a + (b - a) * t) for a, b in zip(inner, outer))
    return small.resize((w, h), Image.BICUBIC)


def diagonal_gradient(size, stops):
    """Top-left to bottom-right gradient between the given colour stops."""
    w = h = size
    img = Image.new("RGB", (w, h))
    px = img.load()
    n = len(stops) - 1
    for y in range(h):
        for x in range(w):
            t = (x / max(w - 1, 1) + y / max(h - 1, 1)) / 2
            i = min(int(t * n), n - 1)
            u = t * n - i
            a, b = stops[i], stops[i + 1]
            px[x, y] = tuple(round(p + (q - p) * u) for p, q in zip(a, b))
    return img


def draw_mark(size):
    """The mark as an "L" alpha mask on a `size`×`size` canvas in the 100×100 design space.

    Placeholder: a thick ring (outer r30, inner r14, centred). Keep it bold; detail below ~4 design units turns to grain at 40pt.
    """
    s = size / 100.0
    mask = Image.new("L", (size, size), 0)
    d = ImageDraw.Draw(mask)
    cx, cy, r_out, r_in = 50 * s, 50 * s, 30 * s, 14 * s
    d.ellipse([cx - r_out, cy - r_out, cx + r_out, cy + r_out], fill=255)
    d.ellipse([cx - r_in, cy - r_in, cx + r_in, cy + r_in], fill=0)
    return mask


def mark_layer(size):
    """The mark filled with the primary-to-secondary gradient, on a transparent ground."""
    fill = diagonal_gradient(size, [rgb(T["colors"]["primary"]), rgb(T["colors"]["secondary"])])
    layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    layer.paste(fill, (0, 0), draw_mark(size))
    return layer


def icon(size):
    big = size * SS
    canvas = radial_wash(big, rgb(T["icon"]["backgroundIn"]), rgb(T["icon"]["backgroundOut"]))
    canvas.paste(mark_layer(big), (0, 0), mark_layer(big))
    return canvas.resize((size, size), Image.LANCZOS).convert("RGB")  # RGB: no alpha channel


def mark(size):
    big = size * SS
    return mark_layer(big).resize((size, size), Image.LANCZOS)


def write(img, rel):
    path = os.path.join(ROOT, rel)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path, "PNG")
    print("wrote", rel, img.size, img.mode)


def main():
    write(icon(1024), "iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/icon-1024.png")
    write(icon(512), "store/play/play-icon-512.png")
    write(mark(96), "iosApp/iosApp/LaunchAssets/mark.png")
    write(mark(192), "iosApp/iosApp/LaunchAssets/mark@2x.png")
    write(mark(288), "iosApp/iosApp/LaunchAssets/mark@3x.png")
    return 0


if __name__ == "__main__":
    sys.exit(main())
