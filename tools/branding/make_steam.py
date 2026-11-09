"""Steam's store art: the capsules at Steamworks' sizes, the library's header, hero and transparent
logo, and the page background, drawn from the title art and the icon's own towers. Writes branding/steam/.

WHY: BR1 (2026-10-27) left Steam's art to Jerus: an AI drawing from branding/, or a human artist.
His call, 2026-10-31, was "From our art (AI)" (BR2). Steamworks asks for nine pictures at fixed
sizes (partner.steamgames.com/doc/store/assets/standard and .../libraryassets, read 2026-11-09:
sizes as below, "game title only" on a capsule, "no text" on the hero, a transparent PNG for the
logo), none of which the itch.io cover (630 x 500, by hand from a played run's shot) can be cut to.

Each picture is a skyline of the icon's towers, lit in the four palette colours, standing under
a rising chart line with ringed dots: the icon's "towers as a chart", drawn wide, the towers' tops
following the line. The title art (branding/title.svg, the name in its own font) sits in the sky
above it on a scrim, so the name reads at every size, and it is the only text anywhere: no tagline,
no review or award line, which Steam does not allow on a capsule. The hero carries no text at all
(Steam lays the logo over it) and keeps its quiet corner at the bottom left, where the logo sits by
default. The numbers are not random but seeded, so a rerun draws the same pictures.

No game's shot is behind it: a played run's screenshot, as the itch.io cover has, would be a
picture of a city the player has not built yet, and a script cannot take one. The picture is the
brand's, not the map's.

Like the other tools here it needs only Python and Edge or Chrome (pngio.py), and is remade when
the title art or the palette is (make_title.py first, then this):

    python tools/branding/make_steam.py                    # writes branding/steam/
    python tools/branding/make_steam.py --only header-capsule     # one picture, to look at
"""
import argparse, os, random, re, sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from pngio import browser, screenshot, png_bytes, png_rgb_bytes, crop, url  # noqa: E402
from make_icons import smooth, STAGE, BLOCK, EDGE, DARK_WIN, MONEY, PEOPLE, BUSINESS, BUILDING  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", ".."))
BRANDING = os.path.join(ROOT, "branding")
OUT = os.path.join(BRANDING, "steam")
TITLE_SVG = os.path.join(BRANDING, "title.svg")

TEXT = "#e6edf3"          # ui/Palette.TEXT, the name's colour in title.svg
TOWER_COLOURS = [PEOPLE, BUSINESS, BUILDING, MONEY]
BACK_FILL, BACK_EDGE = "#101a27", "#182638"

# What Steamworks asks for, read 2026-11-09; BrandingCheck holds branding/steam/ to this list.
# name: (width, height, kind) - "capsule" carries the name, "hero" and "background" no text at all,
# "logo" is the name alone on a transparent PNG.
ASSETS = {
    "header-capsule":     (920, 430, "capsule"),
    "small-capsule":      (462, 174, "capsule"),
    "main-capsule":       (1232, 706, "capsule"),
    "vertical-capsule":   (748, 896, "capsule"),
    "library-capsule":    (600, 900, "capsule"),
    "library-header":     (920, 430, "capsule"),
    "library-hero":       (3840, 1240, "hero"),
    "library-logo":       (1280, 720, "logo"),
    "page-background":    (1438, 810, "background"),
}

# How each picture is composed, in its own pixels. u is the window grid's unit (a tower's windows
# step by u); logo is (width of the name, its centre's y as a share of the height); line is the
# chart line's height at the left edge and at the right, as shares of the height; gap is how far, in u,
# a tower's top stands under the line; back is how far the
# dim towers behind the front row may stand above the line, in u; seed fixes the drawing.
LAYOUT = {
    "header-capsule":   dict(u=13, logo=(.80, .33), line=(.86, .68), gap=2.4, back=4.5, seed=1),
    "small-capsule":    dict(u=6,  logo=(.88, .42), line=(.90, .80), gap=1.6, back=0, seed=2),
    "main-capsule":     dict(u=19, logo=(.78, .30), line=(.80, .60), gap=2.4, back=5, seed=3),
    "vertical-capsule": dict(u=16, logo=(.86, .22), line=(.80, .52), gap=2.6, back=6, seed=4),
    "library-capsule":  dict(u=14, logo=(.90, .21), line=(.80, .52), gap=2.6, back=6, seed=5),
    "library-header":   dict(u=13, logo=(.80, .33), line=(.86, .68), gap=2.4, back=4.5, seed=1),
    "library-hero":     dict(u=30, logo=None,        line=(.88, .34), gap=2.8, back=7, seed=6, quiet_left=.30),
    "library-logo":     dict(u=0,  logo=(.97, .50)),
    "page-background":  dict(u=20, logo=None,        line=(.90, .62), gap=2.6, back=5, seed=7, dim=.55),
}


def title_aspect():
    """The title art's width over its height, from its own viewBox."""
    with open(TITLE_SVG, encoding="utf-8") as f:
        head = f.read(2000)
    vb = [float(v) for v in re.search(r'viewBox="([^"]+)"', head).group(1).split()]
    return vb[2] / vb[3]


def chart(rng, w, h, line, u):
    """Points of the chart line, left to right, rising with dips: (x, y) in pixels."""
    n = max(5, round(w / (u * 12)))
    xs = [-u * 2 + i * (w + u * 4) / (n - 1) for i in range(n)]
    y0, y1 = line[0] * h, line[1] * h
    pts = []
    for i, x in enumerate(xs):
        t = i / (n - 1)
        y = y0 + (y1 - y0) * t
        if 0 < i < n - 1:
            y += rng.uniform(-1, 1) * abs(y0 - y1) * .14 + (u * 1.2 if i % 3 == 2 else 0)
        pts.append((round(x, 1), round(y, 1)))
    return pts


def line_at(pts, x):
    """The chart's height at x, by straight pieces between its points."""
    for (xa, ya), (xb, yb) in zip(pts, pts[1:]):
        if x <= xb:
            return ya + (yb - ya) * max(0, min(1, (x - xa) / (xb - xa)))
    return pts[-1][1]


def towers(rng, w, h, pts, u, gap, lift, fill, edge, lit, dark, alpha, hide_left=0):
    """One row of towers standing on the bottom edge, each top following the chart line."""
    out, x, k = [], -u * 2, rng.randrange(4)
    step_x, step_y = u, u * 1.25
    ww, wh, pad = u * .62, u * .75, u * .5
    while x < w + u:
        cols = rng.choice([2, 2, 3, 3, 4])
        tw = cols * step_x + 2 * pad - (step_x - ww)
        cx = x + tw / 2
        top = line_at(pts, cx) + u * (gap - lift * rng.random())
        if hide_left and cx < hide_left:
            top += u * 6 * (1 - cx / hide_left)
        if top < h - u * 1.5:
            colour = TOWER_COLOURS[k % 4]
            body = f'<rect x="{x:.1f}" y="{top:.1f}" width="{tw:.1f}" height="{h - top + 12:.1f}" rx="{u * .2:.1f}" fill="{fill}" stroke="{edge}" stroke-width="{max(1, u * .17):.1f}"/>'
            wins, y = [], top + pad + u * .25
            while y + wh < h:
                for c in range(cols):
                    r = rng.random()
                    f = colour if r < lit else DARK_WIN if r < lit + dark else None
                    if f:
                        wins.append(f'<rect x="{x + pad + c * step_x:.1f}" y="{y:.1f}" width="{ww:.1f}" height="{wh:.1f}" rx="{max(1, u * .12):.1f}" fill="{f}"/>')
                y += step_y
            out.append(body + (f'<g opacity="{alpha}">' + "".join(wins) + "</g>" if wins else ""))
        x += tw + rng.choice([0, 0, u * .2, u * .45])
        k += 1
    return "\n".join(out)


def scene(name, w, h, cfg, logo_box):
    """The picture as SVG: sky, ledger rules, the dim towers behind, the front row, the chart line."""
    u = cfg["u"]
    rng = random.Random(cfg["seed"])
    pts = chart(rng, w, h, cfg["line"], u)
    lw = u * .52
    path = smooth(pts)
    rules = "".join(f'<line x1="0" y1="{y:.1f}" x2="{w}" y2="{y:.1f}"/>'
                    for y in [u * 3.6 * i + u * 1.2 for i in range(int(h / (u * 3.6)) + 1)])
    quiet = cfg.get("quiet_left", 0) * w
    s = f'''<defs>
<linearGradient id="sky" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="{STAGE}"/><stop offset=".62" stop-color="#101c2c"/><stop offset="1" stop-color="#16293f"/></linearGradient>
<radialGradient id="glow" cx=".5" cy=".5" r=".5"><stop offset="0" stop-color="{STAGE}" stop-opacity=".92"/><stop offset=".6" stop-color="{STAGE}" stop-opacity=".7"/><stop offset="1" stop-color="{STAGE}" stop-opacity="0"/></radialGradient>
</defs>
<rect width="{w}" height="{h}" fill="url(#sky)"/>
<g stroke="{EDGE}" stroke-opacity=".28" stroke-width="{max(1, u * .08):.1f}">{rules}</g>
'''
    if cfg["back"]:
        s += towers(random.Random(cfg["seed"] + 100), w, h, pts, u, cfg["gap"], cfg["back"], BACK_FILL, BACK_EDGE, .5, .2, .55, quiet)
    s += towers(rng, w, h, pts, u, cfg["gap"], 1.5, BLOCK, EDGE, .62, .28, 1, quiet)
    s += f'<path d="{path}" fill="none" stroke="{MONEY}" stroke-opacity=".16" stroke-width="{lw * 4:.1f}" stroke-linecap="round"/>\n'
    s += f'<path d="{path}" fill="none" stroke="{STAGE}" stroke-width="{lw + u * .5:.1f}" stroke-linecap="round" stroke-linejoin="round"/>\n'
    s += f'<path d="{path}" fill="none" stroke="{MONEY}" stroke-width="{lw:.1f}" stroke-linecap="round" stroke-linejoin="round"/>\n'
    for (x, y) in pts[1:-1]:
        s += f'<circle cx="{x}" cy="{y}" r="{lw * .95:.1f}" fill="{STAGE}" stroke="{MONEY}" stroke-width="{lw * .6:.1f}"/>\n'
    if logo_box:
        lx, ly, lwid, lh = logo_box
        s += f'<ellipse cx="{lx + lwid / 2:.1f}" cy="{ly + lh / 2:.1f}" rx="{lwid * .66:.1f}" ry="{lh * 1.05:.1f}" fill="url(#glow)"/>\n'
    if cfg.get("dim"):
        s += f'<rect width="{w}" height="{h}" fill="{STAGE}" fill-opacity="{cfg["dim"]}"/>\n'
    return f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}" style="position:absolute;left:0;top:0">\n{s}</svg>'


def page_for(name, w, h, kind, aspect):
    cfg = LAYOUT[name]
    logo_box, img = None, ""
    if cfg["logo"]:
        share, cy = cfg["logo"]
        lwid = round(w * share)
        lh = round(lwid / aspect)
        logo_box = (round((w - lwid) / 2), round(h * cy - lh / 2), lwid, lh)
        img = (f'<img src="{url(TITLE_SVG)}" width="{lwid}" height="{lh}" '
               f'style="position:absolute;left:{logo_box[0]}px;top:{logo_box[1]}px">')
    art = scene(name, w, h, cfg, logo_box) if kind != "logo" else ""
    bg = "transparent" if kind == "logo" else STAGE
    return (f'<!doctype html><html style="background:{bg}"><body style="margin:0;width:{w}px;height:{h}px;'
            f'overflow:hidden;background:{bg};position:relative">{art}{img}</body></html>')


def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--browser", help="Edge or Chrome to render with")
    ap.add_argument("--only", help="one picture by its name, for looking at")
    args = ap.parse_args()
    if args.only and args.only not in ASSETS:
        sys.exit(f"--only wants one of: {', '.join(ASSETS)}")
    exe = browser(args.browser)
    os.makedirs(OUT, exist_ok=True)
    aspect = title_aspect()
    wrote = []
    for name, (w, h, kind) in ASSETS.items():
        if args.only and name != args.only:
            continue
        page = os.path.join(BRANDING, "_steam.html")
        with open(page, "w", encoding="utf-8") as f:
            f.write(page_for(name, w, h, kind, aspect))
        try:
            sw, _, rgba = screenshot(exe, page, w, h, opaque=(kind != "logo"))
        finally:
            os.remove(page)
        pixels = crop(sw, rgba, 0, 0, w, h)
        data = png_bytes(w, h, pixels) if kind == "logo" else png_rgb_bytes(w, h, pixels)
        with open(os.path.join(OUT, f"{name}-{w}x{h}.png"), "wb") as f:
            f.write(data)
        wrote.append(f"{name} {w}x{h}")
    print(f"wrote {OUT}: {', '.join(wrote)}")


if __name__ == "__main__":
    main()
