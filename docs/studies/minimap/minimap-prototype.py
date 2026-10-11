"""
The minimap study's prototype (docs/studies/spec-globe.md §8): a design aid, not game code.

An ILLUSTRATIVE big city - not a save: a coast, a port, a core, rings of
homes, industry along the port and the railway, farms and mines outside -
laid on a raster of 2 plots a cell (60 m), a district 128 x 128 cells (the
game's 256-plot district), 4.5 x 3 districts - a city of about a million. It is drawn at the minimap's size three ways:

  1. TODAY: MapTiles.nodePixels()'s rule at far zoom - each district one
     flat tint, the class whose buildings take the most ground in it, at
     TileRaster.BLOCK_ALPHA by how much of its ground is used;
  2. SHARES: each district's pixels dithered by its classes' shares of its
     ground, stable from month to month (a hash a pixel against the
     cumulative shares), with the skeleton drawn as lines;
  3. THUMBNAILS: each district's own picture at 8 plots a pixel, from where
     its buildings stand (here the synthetic raster; in the game its street
     plan), each minimap pixel dithered by its own cells, the skeleton over it.

    python3 minimap-prototype.py OUTDIR
"""
import math
import sys

import numpy as np
from PIL import Image

# BuildingVisual's classes and fills, FLATS_FILL; TileRaster's ground, roads, rail, dims.
HOME, SHOP, OFFICE, INDUSTRY, FARM, UTILITY, SCHOOL, HEALTH, SAFETY, MINE = range(10)
FLATS = 10                                              # homes as flats: a fill of its own
EMPTY = 11                                              # owned, nothing built
FILL = [0xEAA572, 0xF2C94C, 0x5B8FD6, 0x9076BA, 0xE0CB84, 0x2EA89E, 0xD0587A, 0xF7F4EE, 0xD8442F, 0x8E9093, 0xD07A44]
GRASS, FOREST, FRESH, SALT = 0xA8C47E, 0x527C45, 0x78B4DC, 0x2F5D88
HIGHWAY, RAIL, EDGE = 0x3B4048, 0x707070, 0xFFFFFF
VOID = 0x16222C
UNOWNED_DIM = 0.4
BLOCK_FILLS = (0.12, 0.28, 0.5)
BLOCK_ALPHA = (0.4, 0.6, 0.8, 0.96)

CELL_PLOTS = 2                                          # plots a cell of the illustration: 60 m
CELL_KM = CELL_PLOTS * 0.03
D = 256 // CELL_PLOTS                                   # cells a district side (the game's 256-plot district)
DX, DY = 4.5, 3                                         # districts across the illustration: 34.6 x 23 km
W, H = int(DX * D), int(DY * D)                         # cells
rng = np.random.default_rng(7)


def rgb(c):
    return np.array([(c >> 16) & 255, (c >> 8) & 255, c & 255], dtype=float)


def blend(a, b, t):
    return a * (1 - t) + b * t


def value_noise(w, h, scale, seed):
    r = np.random.default_rng(seed)
    gw, gh = int(w / scale) + 2, int(h / scale) + 2
    g = r.random((gh, gw))
    y, x = np.mgrid[0:h, 0:w] / scale
    x0, y0 = x.astype(int), y.astype(int)
    fx, fy = x - x0, y - y0
    fx, fy = fx * fx * (3 - 2 * fx), fy * fy * (3 - 2 * fy)
    a = g[y0, x0] * (1 - fx) + g[y0, x0 + 1] * fx
    b = g[y0 + 1, x0] * (1 - fx) + g[y0 + 1, x0 + 1] * fx
    return a * (1 - fy) + b * fy


def the_city():
    """Ground, ownership and land use on the cell raster; the skeleton as polylines in cells. Distances in km."""
    y, x = np.mgrid[0:H, 0:W].astype(float)
    X, Y = x * CELL_KM, y * CELL_KM
    k = 1 / CELL_KM                                     # cells a km
    n1 = value_noise(W, H, 3.0 * k, 1) + 0.5 * value_noise(W, H, 1.0 * k, 2)
    coast = (X - 0.62 * W * CELL_KM) * 0.55 + (Y - 0.72 * H * CELL_KM) * 0.85 + 2.2 * (n1 - 0.75)
    port = (0.66 * W * CELL_KM, 0.60 * H * CELL_KM)
    bay = np.hypot(X - port[0] - 0.8, Y - port[1] - 0.6) < 1.2
    ground = np.full((H, W), GRASS)
    sea = (coast > 0) | bay
    ground[value_noise(W, H, 2.0 * k, 3) > 0.62] = FOREST
    river = np.abs((Y - 0.30 * H * CELL_KM) - 0.18 * (X - 0.15 * W * CELL_KM) - 0.8 * np.sin(X / 2.4)) < 0.09
    ground[river & (X < 0.64 * W * CELL_KM)] = FRESH
    ground[sea] = SALT
    core = (0.50 * W * CELL_KM, 0.50 * H * CELL_KM)
    r = np.hypot(X - core[0], Y - core[1])
    rp = np.hypot(X - port[0], Y - port[1])
    edge_noise = 2.0 * (value_noise(W, H, 2.5 * k, 4) - 0.5)
    owned = (r < 11.5 + edge_noise) & ~sea | ((rp < 1.6) & sea)
    use = np.full((H, W), EMPTY)
    dry = owned & (ground != SALT) & (ground != FRESH)
    p = rng.random((H, W))
    patch = value_noise(W, H, 0.5 * k, 5)
    use[dry & (r < 1.2) & (p < 0.92)] = OFFICE
    use[dry & (r < 3.2) & (r >= 1.2) & (p < 0.78)] = FLATS
    use[dry & (r < 2.5) & (p > 0.9)] = SHOP
    use[dry & (use == EMPTY) & (r < 9.5) & (patch < 0.95 - r / 15)] = HOME
    pts = [(port[0], port[1]), (0.57 * W * CELL_KM, 0.47 * H * CELL_KM), (0.40 * W * CELL_KM, 0.36 * H * CELL_KM), (0.10 * W * CELL_KM, 0.18 * H * CELL_KM)]
    def near_line(pts, w):
        m = np.zeros((H, W), dtype=bool)
        for (ax, ay), (bx, by) in zip(pts, pts[1:]):
            vx, vy = bx - ax, by - ay
            t = np.clip(((X - ax) * vx + (Y - ay) * vy) / (vx * vx + vy * vy), 0, 1)
            m |= np.hypot(X - (ax + t * vx), Y - (ay + t * vy)) < w
        return m
    use[dry & ((rp < 3.4) | near_line(pts, 0.55)) & (patch > 0.25) & (r > 1.2)] = INDUSTRY
    use[dry & (use == EMPTY) & (r > 8) & (patch > 0.6)] = FARM
    for mx, my in ((0.13 * W * CELL_KM, 0.22 * H * CELL_KM), (0.30 * W * CELL_KM, 0.82 * H * CELL_KM)):
        use[dry & (np.hypot(X - mx, Y - my) < 0.6)] = MINE
    for cls, share in ((UTILITY, 0.012), (SCHOOL, 0.012), (HEALTH, 0.006), (SAFETY, 0.004), (SHOP, 0.02)):
        use[dry & (use == HOME) & (rng.random((H, W)) < share)] = cls
    c = (core[0] * k, core[1] * k)
    cell = lambda q: (q[0] * k, q[1] * k)
    highways = [
        [c, (0.20 * W, 0.40 * H), (0.0, 0.42 * H)],
        [c, (0.50 * W, 0.12 * H), (0.48 * W, 0.0)],
        [c, (0.30 * W, 0.72 * H), (0.18 * W, 1.0 * H)],
        [c, cell(port)],
    ]
    rail = [cell(q) for q in pts]
    return ground, owned, use, highways, rail


def colour_of(use, ground, owned):
    """A cell's colour: the ground, dimmed off the city's land, a building's fill over its own."""
    out = np.zeros(use.shape + (3,))
    for g in (GRASS, FOREST, FRESH, SALT):
        out[ground == g] = rgb(g)
    out[~owned] = blend(out[~owned], rgb(VOID), UNOWNED_DIM)
    for k, f in enumerate(FILL):
        out[use == k] = rgb(f)
    return out


def district_shares(use, owned, ground):
    """Each district's ground by class over its owned dry ground: {district: (shares, fill)}."""
    out = {}
    for j in range(math.ceil(DY)):
        for i in range(math.ceil(DX)):
            u = use[j * D:(j + 1) * D, i * D:(i + 1) * D]
            o = owned[j * D:(j + 1) * D, i * D:(i + 1) * D]
            g = ground[j * D:(j + 1) * D, i * D:(i + 1) * D]
            dry = o & (g != SALT) & (g != FRESH)
            n = dry.sum()
            if n == 0:
                continue
            counts = np.array([((u == k) & dry).sum() for k in range(11)], dtype=float)
            out[(i, j)] = (counts / n, counts.sum() / n)
    return out


def today(use, owned, ground, scale):
    """MapTiles.nodePixels(): a district one tint, the class with the most ground, at BLOCK_ALPHA by its fill."""
    shares = district_shares(use, owned, ground)
    base = colour_of(np.full(use.shape, EMPTY), ground, owned)
    out = base.copy()
    for (i, j), (s, f) in shares.items():
        homes = s[HOME] + s[FLATS]
        ground_by = s.copy()
        ground_by[HOME] = homes
        ground_by[FLATS] = 0
        best = int(np.argmax(ground_by[:10]))
        if ground_by[best] <= 0:
            continue
        lvl = 0 if f < BLOCK_FILLS[0] else 1 if f < BLOCK_FILLS[1] else 2 if f < BLOCK_FILLS[2] else 3
        c = FILL[best]
        if best == HOME and (lvl == 3 or s[FLATS] * 2 >= homes):
            c = FILL[FLATS]
        sl = (slice(j * D, (j + 1) * D), slice(i * D, (i + 1) * D))
        o = owned[sl] & (ground[sl] != SALT) & (ground[sl] != FRESH)
        blk = out[sl]
        blk[o] = blend(blk[o], rgb(c), BLOCK_ALPHA[lvl])
    return shrink(out, scale, majority=False)


def dither_pick(cum, h):
    """The class a hash h (0 to 1) falls in, against cumulative shares."""
    return int(np.searchsorted(cum, h, side='right'))


def shares_rule(use, owned, ground, scale):
    """Each district's pixels dithered by its classes' shares; stable: one hash a pixel."""
    shares = district_shares(use, owned, ground)
    w, h = W // scale, H // scale
    base = shrink(colour_of(np.full(use.shape, EMPTY), ground, owned), scale, majority=False)
    out = base.copy()
    hsh = np.random.default_rng(99).random((h, w))
    for (i, j), (s, f) in shares.items():
        cum = np.cumsum(s[:11])
        for py in range(j * D // scale, min(h, (j + 1) * D // scale)):
            for px in range(i * D // scale, min(w, (i + 1) * D // scale)):
                cy, cx = py * scale + scale // 2, px * scale + scale // 2
                if not owned[cy, cx] or ground[cy, cx] in (SALT, FRESH):
                    continue
                k = dither_pick(cum, hsh[py, px])
                if k < 11:
                    out[py, px] = rgb(FILL[k])
    return out


def thumbnails_rule(use, owned, ground, scale):
    """Each pixel dithered by its own cells' classes: where the buildings stand."""
    w, h = W // scale, H // scale
    col = colour_of(use, ground, owned)
    out = np.zeros((h, w, 3))
    hsh = np.random.default_rng(99).random((h, w))
    for py in range(h):
        for px in range(w):
            blk = use[py * scale:(py + 1) * scale, px * scale:(px + 1) * scale].ravel()
            cblk = col[py * scale:(py + 1) * scale, px * scale:(px + 1) * scale].reshape(-1, 3)
            k = min(len(blk) - 1, int(hsh[py, px] * len(blk)))
            order = np.argsort(blk, kind='stable')
            out[py, px] = cblk[order[k]]
    return out


def shrink(img, scale, majority):
    h, w = img.shape[0] // scale, img.shape[1] // scale
    return img[:h * scale, :w * scale].reshape(h, scale, w, scale, 3).mean(axis=(1, 3))


def skeleton(img, scale, highways, rail, owned):
    """Highways and the railway as 1-px lines, the city's edge in white at 55%: drawn whatever the scale."""
    h, w = img.shape[:2]
    def line(pts, c, alpha=1.0):
        for (ax, ay), (bx, by) in zip(pts, pts[1:]):
            n = int(max(abs(bx - ax), abs(by - ay)) / scale * 2) + 1
            for t in np.linspace(0, 1, n):
                px, py = int((ax + t * (bx - ax)) / scale), int((ay + t * (by - ay)) / scale)
                if 0 <= px < w and 0 <= py < h:
                    img[py, px] = blend(img[py, px], rgb(c), alpha)
    for hw in highways:
        line(hw, HIGHWAY)
    line(rail, RAIL)
    o = shrink(np.repeat(owned[:, :, None].astype(float), 3, axis=2), scale, False)[:, :, 0] > 0.5
    e = o & ~(np.roll(o, 1, 0) & np.roll(o, -1, 0) & np.roll(o, 1, 1) & np.roll(o, -1, 1))
    img[e] = blend(img[e], rgb(EDGE), 0.55)
    return img


def save(img, path, zoom=2):
    a = np.clip(img, 0, 255).astype(np.uint8)
    Image.fromarray(a).resize((a.shape[1] * zoom, a.shape[0] * zoom), Image.NEAREST).save(path)


def main(out):
    ground, owned, use, highways, rail = the_city()
    for scale, tag in ((1, 'full'), (2, 'panel'), (8, 'stamp')):
        save(today(use, owned, ground, scale), f'{out}/mm-today-{tag}.png', zoom=1)
        save(skeleton(shares_rule(use, owned, ground, scale), scale, highways, rail, owned), f'{out}/mm-shares-{tag}.png', zoom=1)
        save(skeleton(thumbnails_rule(use, owned, ground, scale), scale, highways, rail, owned), f'{out}/mm-thumbs-{tag}.png', zoom=1)
        print(f'{tag}: {W // scale} x {H // scale} px, {scale * CELL_PLOTS * 30} m a pixel')
    print(f'the illustration: {W} x {H} cells of {CELL_PLOTS * 30} m, {W * CELL_KM:.1f} x {H * CELL_KM:.1f} km, {DX * DY:g} districts')


if __name__ == '__main__':
    main(sys.argv[1] if len(sys.argv) > 1 else '.')
