"""The game's title art for the store pages: lays out "the ledger's total" in the game's own font, and
writes branding/title.svg (the font inside it) and transparent PNGs at TITLE_WIDTHS.

WHY: Jerus chose it of four concepts (BR1, 2026-10-27): "Civic Ledger" in IBM Plex Sans Bold, the
title screen's face, over the double rule an account closes on, its top line climbing past the
name's end to a ringed dot like a chart. The start screen draws the same art itself, in JavaFX
(ui/Branding.title()), from the same proportions as below; this is for everything that is not the
game: the itch.io cover, the Steam logo and capsules, a press kit.

The rules end where the name ends, so the name's width must be known in its font: no font tools are
installed, so Edge lays the text out (pngio.dump_dom), the finished SVG is taken from its DOM, and
the font file is put inside it (IBM Plex is under the OFL, which allows embedding; the licence is
src/main/resources/fonts/OFL.txt).

    python tools/branding/make_title.py            # writes branding/
"""
import argparse, base64, os, re, sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from pngio import browser, screenshot, dump_dom, png_bytes, crop  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", ".."))
OUT = os.path.join(ROOT, "branding")
FONT = os.path.join(ROOT, "src", "main", "resources", "fonts", "IBMPlexSans-Bold.ttf")

NAME = "Civic Ledger"          # GameVersion.NAME
TITLE_WIDTHS = [600, 1200, 2400]
SIZE = 150                     # the name's font size, in the SVG's own units
PAD = 24                       # room around the art, past the rules' and the dot's strokes
TEXT = "#e6edf3"               # ui/Palette.TEXT
MONEY = "#5aa9ff"              # ui/Palette.MONEY
STAGE = "#0b1118"              # ui/Palette.STAGE, the dot's middle

# ui/Branding's proportions, in ems of SIZE: keep the two equal.
RULE, TOP_DROP, LOW_DROP, BEND_BACK = .0467, .413, .56, .2
CURVE = [.0667, 0, .133, -.08, .227, -.24]
CLIMB_X, CLIMB_Y, DOT, RING = .467, -.747, .06, .0333


def layout_page():
    font = "file:///" + FONT.replace(os.sep, "/")
    return f'''<!doctype html><html><head><meta charset="utf-8"><style>
@font-face {{ font-family: Plex; font-weight: 700; src: url("{font}"); }}
</style></head><body>
<svg id="art" viewBox="0 0 2000 600" width="2000" height="600"></svg>
<script>
const NS = 'http://www.w3.org/2000/svg', S = {SIZE}, svg = document.getElementById('art');
function el(tag, a) {{ const e = document.createElementNS(NS, tag); for (const k in a) e.setAttribute(k, a[k]); svg.appendChild(e); return e; }}
document.fonts.load('700 {SIZE}px Plex').then(() => {{
  const base = S;
  const t = el('text', {{x: 0, y: base, 'font-family': 'Plex', 'font-weight': 700, 'font-size': S, fill: '{TEXT}'}});
  t.textContent = {NAME!r};
  const x0 = .03 * S, x1 = t.getComputedTextLength();
  const yTop = base + {TOP_DROP} * S, yLow = base + {LOW_DROP} * S, w = {RULE} * S;
  const line = {{fill: 'none', stroke: '{MONEY}', 'stroke-width': w, 'stroke-linecap': 'round', 'stroke-linejoin': 'round'}};
  el('line', Object.assign({{x1: x0, y1: yLow, x2: x1, y2: yLow}}, line));
  const c = {CURVE};
  const ex = x1 + {CLIMB_X} * S, ey = yTop + {CLIMB_Y} * S;
  el('path', Object.assign({{d: `M${{x0}},${{yTop}} L${{x1 - {BEND_BACK} * S}},${{yTop}} C${{x1 + c[0] * S}},${{yTop + c[1] * S}} ${{x1 + c[2] * S}},${{yTop + c[3] * S}} ${{x1 + c[4] * S}},${{yTop + c[5] * S}} L${{ex}},${{ey}}`}}, line));
  el('circle', {{cx: ex, cy: ey, r: {DOT} * S, fill: '{STAGE}', stroke: '{MONEY}', 'stroke-width': {RING} * S}});
  const b = svg.getBBox(), P = {PAD};
  const vb = [b.x - P, b.y - P, b.width + 2 * P, b.height + 2 * P].map(v => Math.round(v * 10) / 10);
  svg.setAttribute('viewBox', vb.join(' '));
  svg.setAttribute('width', vb[2]); svg.setAttribute('height', vb[3]);
  document.body.setAttribute('data-done', '1');
}});
</script></body></html>
'''


def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--browser", help="Edge or Chrome to lay out and render with")
    args = ap.parse_args()
    exe = browser(args.browser)
    os.makedirs(OUT, exist_ok=True)

    page = os.path.join(OUT, "_layout.html")
    with open(page, "w", encoding="utf-8") as f:
        f.write(layout_page())
    try:
        dom = dump_dom(exe, page)
    finally:
        os.remove(page)
    if 'data-done="1"' not in dom:
        sys.exit("the layout page did not finish (the font did not load?)")
    m = re.search(r'<svg id="art"(.*?)>(.*?)</svg>', dom, re.S)
    attrs, body = m.group(1), m.group(2)
    vb = [float(v) for v in re.search(r'viewBox="([^"]+)"', attrs).group(1).split()]
    font64 = base64.b64encode(open(FONT, "rb").read()).decode("ascii")
    svg = (f'<svg xmlns="http://www.w3.org/2000/svg"{attrs}>\n'
           f'<defs><style>@font-face {{ font-family: Plex; font-weight: 700; '
           f'src: url(data:font/ttf;base64,{font64}) format("truetype"); }}</style></defs>\n'
           f'{body}\n</svg>\n')
    with open(os.path.join(OUT, "title.svg"), "w", encoding="utf-8", newline="\n") as f:
        f.write(svg)

    gap, y, places = 8, 0, []
    for wd in TITLE_WIDTHS:
        ht = round(wd * vb[3] / vb[2])
        places.append((wd, ht, y))
        y += ht + gap
    width, height = max(TITLE_WIDTHS), y
    imgs = "".join(f'<img src="title.svg" width="{wd}" height="{ht}" style="position:absolute;left:0;top:{py}px">'
                   for (wd, ht, py) in places)
    page = os.path.join(OUT, "_render.html")
    with open(page, "w", encoding="utf-8") as f:
        f.write(f'<!doctype html><html style="background:transparent"><body style="margin:0;background:transparent">{imgs}</body></html>')
    try:
        w, h, rgba = screenshot(exe, page, width, height)
    finally:
        os.remove(page)
    for (wd, ht, py) in places:
        with open(os.path.join(OUT, f"title-{wd}.png"), "wb") as f:
            f.write(png_bytes(wd, ht, crop(w, rgba, 0, py, wd, ht)))
    print(f"wrote {OUT}: title.svg ({vb[2]:.0f} x {vb[3]:.0f}, the font inside), "
          f"PNGs {', '.join(f'{wd}x{ht}' for (wd, ht, _) in places)}")


if __name__ == "__main__":
    main()
