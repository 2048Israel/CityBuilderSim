"""The game's icon: draws it as SVG, renders it at every size with Edge, and writes the PNGs and the .ico.

WHY: until BR1 (2026-10-27) the game had no icon at all - the window, the
taskbar and the exe showed Java's and jpackage's defaults. Jerus chose
concept B of four ("towers as a chart"): four towers rising left to right
like the bars of a chart, each lit in one of the palette's four colours,
the money-blue line running along their roofs. It is drawn twice: the full
drawing for 48 px and up, and a bolder one (bigger windows, a thicker line,
a rim) for 40 px and under, because the full one turns to mush at 16.

The colours are the title screen's (ui/Palette.java: STAGE, PEOPLE, MONEY,
BUSINESS, BUILDING; ui/UserInterface.java's BACKDROP_WINDOW), the towers a
step lighter than the backdrop's so they show at small sizes.

No image library is needed: the SVG is rendered by Edge (or Chrome) in
headless mode onto a transparent page, and the PNGs are cut out of that
screenshot and the .ico packed with the standard library alone.

    python tools/icon/make_icons.py            # writes src/main/resources/icons/
    python tools/icon/make_icons.py --browser "C:/path/to/chrome.exe"
"""
import argparse, os, random, struct, subprocess, sys, tempfile, zlib

ROOT = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", ".."))
OUT = os.path.join(ROOT, "src", "main", "resources", "icons")

# Every size written as a PNG; the .ico carries ICO_SIZES of them. Both are ui/AppIcon.java's
# SIZES and ICO_SIZES, which IconCheck holds the files to: change them there too.
SIZES = [16, 20, 24, 32, 40, 48, 64, 128, 256]
ICO_SIZES = [16, 20, 24, 32, 40, 48, 64, 256]
# At or under this size the bold drawing is used.
SMALL_UP_TO = 40

STAGE = "#0b1118"
BLOCK = "#18263a"
EDGE = "#2a3e57"
DARK_WIN = "#22344a"
MONEY = "#5aa9ff"
PEOPLE = "#2ec4b6"
BUSINESS = "#a78bfa"
BUILDING = "#f17cb0"

BROWSERS = [
    r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
]


# ----- the drawing -----

def smooth(pts):
    """Catmull-Rom through the points, as cubic Beziers."""
    d = f"M{pts[0][0]},{pts[0][1]}"
    for i in range(len(pts) - 1):
        p0 = pts[max(i - 1, 0)]; p1 = pts[i]; p2 = pts[i + 1]; p3 = pts[min(i + 2, len(pts) - 1)]
        c1 = (p1[0] + (p2[0] - p0[0]) / 6, p1[1] + (p2[1] - p0[1]) / 6)
        c2 = (p2[0] - (p3[0] - p1[0]) / 6, p2[1] - (p3[1] - p1[1]) / 6)
        d += f" C{c1[0]:.1f},{c1[1]:.1f} {c2[0]:.1f},{c2[1]:.1f} {p2[0]},{p2[1]}"
    return d


def line(pts, width, dots, halo):
    d = smooth(pts)
    s = f'<path d="{d}" fill="none" stroke="{STAGE}" stroke-width="{width + halo}" stroke-linecap="round" stroke-linejoin="round"/>\n'
    s += f'<path d="{d}" fill="none" stroke="{MONEY}" stroke-width="{width}" stroke-linecap="round" stroke-linejoin="round"/>\n'
    for (x, y) in dots:
        s += f'<circle cx="{x}" cy="{y}" r="{width * .95:.1f}" fill="{STAGE}" stroke="{MONEY}" stroke-width="{width * .6:.1f}"/>\n'
    return s


def tower(x, w, top, rng, colour, lit, dark, win, step, pad, edge):
    s = f'<rect x="{x}" y="{top}" width="{w}" height="{256 - top + 8}" rx="3" fill="{BLOCK}" stroke="{EDGE}" stroke-width="{edge}"/>\n'
    ww, wh = win
    cols = max(1, int((w - 2 * pad + (step[0] - ww)) // step[0]))
    used = cols * step[0] - (step[0] - ww)
    x0 = x + (w - used) / 2
    y = top + pad + 4
    while y + wh < 250:
        for c in range(cols):
            r = rng.random()
            fill = colour if r < lit else DARK_WIN if r < lit + dark else None
            if fill:
                s += f'<rect x="{x0 + c * step[0]:.1f}" y="{y}" width="{ww}" height="{wh}" rx="2" fill="{fill}"/>\n'
        y += step[1]
    return s


def icon_svg(small):
    """Concept B: four towers rising like bars, each lit in one colour, the line along their roofs."""
    rng = random.Random(11)
    body = ""
    if not small:
        towers = [(26, 44, 176, PEOPLE), (82, 44, 138, BUSINESS), (138, 44, 100, BUILDING), (194, 44, 60, MONEY)]
        for (x, w, top, c) in towers:
            body += tower(x, w, top, rng, c, lit=.6, dark=.3, win=(10, 12), step=(16, 20), pad=7, edge=3)
        roofs = [(x + w / 2, top - 22) for (x, w, top, c) in towers]
        body += line([(-4, roofs[0][1] + 22)] + roofs, 12, roofs, halo=12)
        rim = ""
    else:
        towers = [(16, 50, 184, PEOPLE), (76, 50, 144, BUSINESS), (136, 50, 104, BUILDING), (196, 50, 64, MONEY)]
        for (x, w, top, c) in towers:
            body += tower(x, w, top, rng, c, lit=.9, dark=.1, win=(14, 18), step=(20, 24), pad=8, edge=5)
        roofs = [(x + w / 2, top - 26) for (x, w, top, c) in towers]
        body += line([(-10, roofs[0][1] + 30)] + roofs, 20, [roofs[-1]], halo=14)
        rim = f'<rect x="4" y="4" width="248" height="248" rx="52" fill="none" stroke="{EDGE}" stroke-width="8"/>'
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" width="256" height="256">
<defs>
<linearGradient id="bg" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#121e2d"/><stop offset="1" stop-color="{STAGE}"/></linearGradient>
<clipPath id="tile"><rect width="256" height="256" rx="56"/></clipPath>
</defs>
<g clip-path="url(#tile)">
<rect width="256" height="256" fill="url(#bg)"/>
{body}{rim}
</g>
</svg>
'''


# ----- PNG in and out, standard library only -----

def png_read(path):
    """Returns (width, height, RGBA bytes) of an 8-bit RGB or RGBA PNG."""
    data = open(path, "rb").read()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        sys.exit(f"{path} is not a PNG")
    pos, idat, w = 8, b"", None
    while pos < len(data):
        n, kind = struct.unpack(">I4s", data[pos:pos + 8])
        chunk = data[pos + 8:pos + 8 + n]
        if kind == b"IHDR":
            w, h, depth, ctype, _, _, interlace = struct.unpack(">IIBBBBB", chunk)
            if depth != 8 or ctype not in (2, 6) or interlace:
                sys.exit(f"{path}: depth {depth}, colour type {ctype}, interlace {interlace} - not handled")
        elif kind == b"IDAT":
            idat += chunk
        pos += 12 + n
    bpp = 4 if ctype == 6 else 3
    raw = zlib.decompress(idat)
    stride = w * bpp
    out = bytearray(h * stride)
    prev = bytearray(stride)
    for y in range(h):
        f = raw[y * (stride + 1)]
        row = bytearray(raw[y * (stride + 1) + 1:(y + 1) * (stride + 1)])
        for i in range(stride):
            a = row[i - bpp] if i >= bpp else 0
            b = prev[i]
            c = prev[i - bpp] if i >= bpp else 0
            if f == 1: row[i] = (row[i] + a) & 255
            elif f == 2: row[i] = (row[i] + b) & 255
            elif f == 3: row[i] = (row[i] + (a + b) // 2) & 255
            elif f == 4:
                p = a + b - c; pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                row[i] = (row[i] + (a if pa <= pb and pa <= pc else b if pb <= pc else c)) & 255
        out[y * stride:(y + 1) * stride] = row
        prev = row
    if bpp == 3:
        sys.exit(f"{path} came back without transparency (RGB): the browser ignored the transparent background")
    return w, h, bytes(out)


def png_bytes(w, h, rgba):
    def chunk(kind, body):
        return struct.pack(">I", len(body)) + kind + body + struct.pack(">I", zlib.crc32(kind + body) & 0xffffffff)
    raw = b"".join(b"\x00" + rgba[y * w * 4:(y + 1) * w * 4] for y in range(h))
    return (b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
            + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b""))


def crop(w, rgba, x, y, cw, ch):
    return b"".join(rgba[((y + r) * w + x) * 4:((y + r) * w + x + cw) * 4] for r in range(ch))


def ico_bytes(pngs):
    """An .ico of PNG-compressed images (Windows Vista and later), smallest first."""
    head = struct.pack("<HHH", 0, 1, len(pngs))
    offset = 6 + 16 * len(pngs)
    entries, body = b"", b""
    for (n, data) in pngs:
        entries += struct.pack("<BBBBHHII", n % 256, n % 256, 0, 0, 1, 32, len(data), offset)
        body += data
        offset += len(data)
    return head + entries + body


# ----- the run -----

def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--browser", help="Edge or Chrome to render with")
    args = ap.parse_args()
    browser = args.browser or next((b for b in BROWSERS if os.path.exists(b)), None)
    if not browser:
        sys.exit("No Edge or Chrome found; pass --browser")
    os.makedirs(OUT, exist_ok=True)
    for (name, small) in (("icon.svg", False), ("icon-small.svg", True)):
        with open(os.path.join(OUT, name), "w", encoding="utf-8", newline="\n") as f:
            f.write(icon_svg(small))

    gap, x, places = 8, 0, []
    for n in SIZES:
        places.append((n, x))
        x += n + gap
    width, height = x, max(SIZES)
    imgs = "".join(
        f'<img src="{"icon-small.svg" if n <= SMALL_UP_TO else "icon.svg"}" width="{n}" height="{n}" '
        f'style="position:absolute;left:{px}px;top:0">' for (n, px) in places)
    with tempfile.TemporaryDirectory(ignore_cleanup_errors=True) as tmp:
        page = os.path.join(OUT, "_render.html")
        shot = os.path.join(tmp, "render.png")
        with open(page, "w", encoding="utf-8") as f:
            f.write(f'<!doctype html><html style="background:transparent"><body style="margin:0;background:transparent">{imgs}</body></html>')
        try:
            subprocess.run([browser, "--headless=new", "--disable-gpu", "--hide-scrollbars",
                            "--force-device-scale-factor=1", "--default-background-color=00000000",
                            f"--user-data-dir={os.path.join(tmp, 'profile')}", f"--window-size={width},{height}",
                            f"--screenshot={shot}", "file:///" + page.replace(os.sep, "/")],
                           check=True, capture_output=True, timeout=120)
        finally:
            os.remove(page)
        w, h, rgba = png_read(shot)
    if w < width or h < height:
        sys.exit(f"the render came back {w}x{h}, smaller than the {width}x{height} asked for")

    pngs = {}
    for (n, px) in places:
        data = png_bytes(n, n, crop(w, rgba, px, 0, n, n))
        pngs[n] = data
        with open(os.path.join(OUT, f"icon-{n}.png"), "wb") as f:
            f.write(data)
    with open(os.path.join(OUT, "icon.ico"), "wb") as f:
        f.write(ico_bytes([(n, pngs[n]) for n in ICO_SIZES]))
    print(f"wrote {OUT}: icon.svg, icon-small.svg, {len(SIZES)} PNGs ({SIZES[0]}-{SIZES[-1]} px), "
          f"icon.ico ({len(ICO_SIZES)} sizes)")


if __name__ == "__main__":
    main()
