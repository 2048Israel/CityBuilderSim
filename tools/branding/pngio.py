"""PNG in and out, and a headless browser to draw with, for the branding tools - standard library only.

WHY: the PC has Python but no image library (no Pillow, no cairosvg, no fontTools), and a pip
install is a download nobody asked for. Edge or Chrome in headless mode renders the SVGs (and lays
out text in the game's own font), a screenshot on a transparent page carries the pixels out, and
the little PNG reader and writer here cut it into files.
"""
import os, struct, subprocess, sys, tempfile, zlib

BROWSERS = [
    r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
]


def browser(given=None):
    b = given or next((b for b in BROWSERS if os.path.exists(b)), None)
    if not b:
        sys.exit("No Edge or Chrome found; pass --browser")
    return b


def _run(exe, args, tmp, timeout=120):
    return subprocess.run([exe, "--headless=new", "--disable-gpu", "--hide-scrollbars",
                           "--force-device-scale-factor=1", "--allow-file-access-from-files",
                           "--virtual-time-budget=5000", f"--user-data-dir={os.path.join(tmp, 'profile')}"] + args,
                          check=True, capture_output=True, timeout=timeout)


def url(path):
    return "file:///" + os.path.abspath(path).replace(os.sep, "/")


def screenshot(exe, page, width, height):
    """(width, height, RGBA) of the page drawn on a transparent background."""
    with tempfile.TemporaryDirectory(ignore_cleanup_errors=True) as tmp:
        shot = os.path.join(tmp, "shot.png")
        _run(exe, ["--default-background-color=00000000", f"--window-size={width},{height}",
                   f"--screenshot={shot}", url(page)], tmp)
        w, h, rgba = png_read(shot)
    if w < width or h < height:
        sys.exit(f"the render came back {w}x{h}, smaller than the {width}x{height} asked for")
    return w, h, rgba


def dump_dom(exe, page):
    """The page's DOM as text, after its scripts have run."""
    with tempfile.TemporaryDirectory(ignore_cleanup_errors=True) as tmp:
        return _run(exe, ["--dump-dom", url(page)], tmp).stdout.decode("utf-8")


def png_read(path):
    """Returns (width, height, RGBA bytes) of an 8-bit RGBA PNG."""
    data = open(path, "rb").read()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        sys.exit(f"{path} is not a PNG")
    pos, idat = 8, b""
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
    if ctype != 6:
        sys.exit(f"{path} came back without transparency (RGB): the browser ignored the transparent background")
    bpp, raw = 4, zlib.decompress(idat)
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
    return w, h, bytes(out)


def png_bytes(w, h, rgba):
    def chunk(kind, body):
        return struct.pack(">I", len(body)) + kind + body + struct.pack(">I", zlib.crc32(kind + body) & 0xffffffff)
    raw = b"".join(b"\x00" + rgba[y * w * 4:(y + 1) * w * 4] for y in range(h))
    return (b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
            + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b""))


def crop(w, rgba, x, y, cw, ch):
    return b"".join(rgba[((y + r) * w + x) * 4:((y + r) * w + x + cw) * 4] for r in range(ch))
