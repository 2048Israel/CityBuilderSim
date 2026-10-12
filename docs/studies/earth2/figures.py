# The Earth 2 study's figures: for a place, two maps of the same box side by side, in the game's ground colours
# (TileRaster.GROUND) with the study's three greys, ice, and a shallow sea lighter than the deep:
#   left   the measure, a pixel a 3" (90 m) cell: NASADEM's slope class over 90 m; earth-1's sea and lakes and rivers;
#          GEBCO's depth (bilinear) for the shallow sea; ice, forest and desert from earth-1's 2' land cover;
#   right  what earth-2's shares (slope at 30", water at 15") give a 30 m plot grid with seeded detail below them
#          (the game's rule, est.):
#          each share interpolated between cell centres, a plot's class its rank in a fixed noise field against the
#          shares (the forest rule of spec-earth 2.3 applied to slope, depth and ice).
#   python3 -I figures.py [place ...]          writes <place>.png beside this script
import sys, os, math
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
from scipy import ndimage
from PIL import Image, ImageDraw, ImageFont
import terrain as T, depth as D
from earthread import Earth

HERE = os.path.dirname(os.path.abspath(__file__))
E = Earth(T.EARTH1); G = T.gebco()
GAME_CPD = int(os.environ.get('GAME_CPD', '120'))     # the slope layer's grid in earth-2 (bake2.SLOPE_CPD): 30"


def rgb(h):
    return ((h >> 16) & 255, (h >> 8) & 255, h & 255)


# TileRaster.GROUND: GRASS, FOREST, FRESH, SALT, SAND, ICE, DESERT; then the study's proposals (est.)
COL = dict(grass=rgb(0xa8c47e), forest=rgb(0x527c45), fresh=rgb(0x78b4dc), deep=rgb(0x2f5d88), sand=rgb(0xe6daaa),
           ice=rgb(0xdfe7ee), desert=rgb(0xd2b48c),
           foothill=rgb(0xc8c3b8), steep=rgb(0x9b968c), vsteep=rgb(0x6c6862), shallow=rgb(0x4f87b5),
           bridge=rgb(0xd0453a))

PLACES = {
    'medellin':  ('Medellin and the Aburra valley', 6.245, -75.575, 15.0, []),
    'everest':   ('The Himalaya round Everest', 27.97, 86.90, 15.0, []),
    'halifax':   ('Halifax harbour, the Narrows and Bedford Basin', 44.668, -63.600, 11.0,
                  [('Macdonald', (44.6637, -63.5846), 55, 1300), ('MacKay', (44.6778, -63.6119), 65, 1200)]),
    'innsbruck': ('Innsbruck and the Inn valley', 47.265, 11.39, 15.0, []),
}


def tiles_for(s, n, w, e):
    return [(a, b) for a in range(math.floor(s), math.floor(n - 1e-9) + 1) for b in range(math.floor(w), math.floor(e - 1e-9) + 1)]


def mosaic(fn, s, n, w, e, per_deg):
    """fn(lat0, lon0) -> (per_deg x per_deg) array for a tile, rows from the north; the box's tiles mosaicked;
    returns (array, north lat of row 0's top edge, west lon of column 0's left edge)."""
    la0, la1 = math.floor(s), math.floor(n - 1e-9); lo0, lo1 = math.floor(w), math.floor(e - 1e-9)
    rows = []
    for la in range(la1, la0 - 1, -1):
        rows.append(np.concatenate([fn(la, lo) for lo in range(lo0, lo1 + 1)], axis=-1))
    return np.concatenate(rows, axis=-2), la1 + 1, lo0


def at(grid, top, left, per_deg, lat, lon, order=1):
    """grid sampled at points (cell-centred data), bilinear (order 1) or nearest (order 0)."""
    r = (top - lat) * per_deg - 0.5; c = (lon - left) * per_deg - 0.5
    if order == 0:
        return grid[np.clip(np.round(r).astype(int), 0, grid.shape[0] - 1), np.clip(np.round(c).astype(int), 0, grid.shape[1] - 1)]
    return ndimage.map_coordinates(grid.astype(np.float32), [r, c], order=1, mode='nearest')


def noise(shape, seed, scales=(16, 8, 4, 2), weights=(0.45, 0.3, 0.15, 0.1)):
    """Value noise over the plots, octaves from 16 plots (480 m) to 2 (60 m), ranked to uniform on [0, 1)."""
    rng = np.random.default_rng(seed); acc = np.zeros(shape, np.float32)
    for sc, wt in zip(scales, weights):
        g = rng.random((shape[0] // sc + 2, shape[1] // sc + 2)).astype(np.float32)
        acc += wt * ndimage.zoom(g, sc, order=1)[:shape[0], :shape[1]]
    u = np.empty(acc.size, np.float32); u[np.argsort(acc, axis=None)] = np.arange(acc.size) / acc.size
    return u.reshape(shape)


def slope_shares(la, lo):
    """The 15" cells' land shares at or above foothill, steep and very steep (3, 240, 240), from the site study."""
    from analyse_sites import load, land_share, cell_classes
    z = load(la, lo)
    return cell_classes(z['nas90'], land_share(la, lo), T.THRESHOLDS)


def slope90(la, lo):
    p = os.path.join(T.DL2, 'figures', 'NASADEM_HGT_%s.tif' % T.tile_name(la, lo))   # its own copy: slope_tiles deletes its own
    if not os.path.exists(p):
        T.fetch(T.nasadem_url(la, lo), p)
    z = T.read_tif(p)
    z = T.fill_voids(z)
    z3 = T.block_mean(z[:3600, :3600], 3)
    return T.slope_pct(z3, la + 1 - 1 / 3600.0, 3 / 3600.0)


def tid(la, lo):
    return (la + 90) * 360 + lo + 180


def dest(lat, lon, bearing, d):
    p1, l1, b = math.radians(lat), math.radians(lon), math.radians(bearing); a = d / T.R_M
    p2 = math.asin(math.sin(p1) * math.cos(a) + math.cos(p1) * math.sin(a) * math.cos(b))
    l2 = l1 + math.atan2(math.sin(b) * math.sin(a) * math.cos(p1), math.cos(a) - math.sin(p1) * math.sin(p2))
    return math.degrees(p2), math.degrees(l2)


def render(place):
    title, clat, clon, half_km, bridges = PLACES[place]
    hl = half_km / 111.195; hw = hl / math.cos(math.radians(clat))
    s, n, w, e = clat - hl, clat + hl, clon - hw, clon + hw
    # inputs
    sea, top, left = mosaic(lambda a, b: E.grid('sea', tid(a, b)), s, n, w, e, 240)
    lake, _, _ = mosaic(lambda a, b: E.grid('lake', tid(a, b)), s, n, w, e, 240)
    lc, top2, left2 = mosaic(lambda a, b: E.grid('landcover', tid(a, b)), s, n, w, e, 30)
    shares, _, _ = mosaic(slope_shares, s, n, w, e, 240)
    sl90, top3, left3 = mosaic(slope90, s, n, w, e, 1200)
    gp, _, _ = mosaic(lambda a, b: T.gebco_tile(G, a, b, pad=0).astype(np.float32), s, n, w, e, 240)
    shal = np.zeros_like(sea, np.float32)
    for a, b in tiles_for(s, n, w, e):
        g1 = T.gebco_tile(G, a, b, pad=1)
        r0 = (top - (a + 1)) * 240; c0 = (b - left) * 240
        shal[r0:r0 + 240, c0:c0 + 240] = D.shallow_share(g1, E.grid('sea', tid(a, b)))
    forest = (lc & 0x0F).astype(np.float32) / 15; ice = ((lc & 0x40) > 0).astype(np.float32); desert = ((lc & 0x20) > 0).astype(np.float32)
    rivers = [r for a, b in tiles_for(s, n, w, e) for r in E.rivers(tid(a, b))]

    def grid_points(step_m):
        ny = int(2 * half_km * 1000 / step_m); nx = ny
        lat = n - (np.arange(ny) + 0.5) * (n - s) / ny; lon = w + (np.arange(nx) + 0.5) * (e - w) / nx
        LA, LO = np.meshgrid(lat, lon, indexing='ij')
        return LA, LO

    def river_mask(LA, LO, step_m):
        m = np.zeros(LA.shape, bool); ny, nx = LA.shape
        for a in rivers:
            for k in range(len(a) - 1):
                p0, p1 = a[k], a[k + 1]
                L = math.hypot((p1[0] - p0[0]) * 111195, (p1[1] - p0[1]) * 111195 * math.cos(math.radians(clat)))
                for t in np.linspace(0, 1, max(2, int(L / (step_m / 2)) + 1)):
                    la = p0[0] + (p1[0] - p0[0]) * t; lo = p0[1] + (p1[1] - p0[1]) * t
                    r = (n - la) / (n - s) * ny; c = (lo - w) / (e - w) * nx
                    rad = max(0.5, (p0[2] + (p1[2] - p0[2]) * t) / 2 / step_m)
                    r0, r1 = int(r - rad), int(r + rad) + 1; c0, c1 = int(c - rad), int(c + rad) + 1
                    if r1 < 0 or c1 < 0 or r0 >= ny or c0 >= nx: continue
                    rr, cc = np.mgrid[max(0, r0):min(ny, r1), max(0, c0):min(nx, c1)]
                    m[rr, cc] |= (rr + 0.5 - r) ** 2 + (cc + 0.5 - c) ** 2 <= rad ** 2 + 0.25
        return m

    def paint(cls_img):
        img = np.zeros(cls_img.shape + (3,), np.uint8)
        for k, v in COL.items():
            img[cls_img == k] = v
        return img

    out = []
    for mode, step in (('measure', 92.6), ('game', 30.0)):
        LA, LO = grid_points(step)
        shape = LA.shape
        u = noise(shape, 11 if mode == 'game' else 12, *(((16, 8, 4, 2), (0.45, 0.3, 0.15, 0.1)) if mode == 'game' else ((5, 3, 2, 1), (0.45, 0.3, 0.15, 0.1))))
        u2 = noise(shape, 21); u3 = noise(shape, 31); u4 = noise(shape, 41)
        cls = np.full(shape, 'grass', object)
        wsea = at(sea.astype(np.float32) / 15, top, left, 240, LA, LO); wlake = at(lake.astype(np.float32) / 15, top, left, 240, LA, LO)
        water = (1 - wsea - wlake) + 1.0 * (u4 - 0.5) * (0.6 if mode == 'game' else 0.3) < 0.5
        if mode == 'measure':
            p = at(sl90, top3, left3, 1200, LA, LO, order=0)
            c = (p >= T.FOOTHILL_PCT).astype(int) + (p >= T.STEEP_PCT) + (p >= T.VERY_STEEP_PCT)
            depth = -at(gp, top, left, 240, LA, LO)
            shallow_m = depth <= D.SHALLOW_M
        else:
            kk = 240 // GAME_CPD
            if kk > 1:
                Lw = 1 - (sea.astype(np.float32) + lake) / 15
                Hh, Ww = shares.shape[1] // kk * kk, shares.shape[2] // kk * kk
                cw = Lw[:Hh, :Ww].reshape(Hh // kk, kk, Ww // kk, kk)
                agg = (shares[:, :Hh, :Ww].reshape(3, Hh // kk, kk, Ww // kk, kk) * cw[None]).sum(axis=(2, 4)) / np.maximum(cw.sum(axis=(1, 3)), 1e-9)[None]
                sh = [at(agg[k], top, left, GAME_CPD, LA, LO) for k in range(3)]
            else:
                sh = [at(shares[k], top, left, 240, LA, LO) for k in range(3)]
            c = sum((u > 1 - sh[k]).astype(int) for k in range(3))
            shallow_m = at(shal, top, left, 240, LA, LO) > u2
        icem = at(ice, top2, left2, 30, LA, LO) + 0.5 * (u3 - 0.5) > 0.5
        fr = at(forest, top2, left2, 30, LA, LO); de = at(desert, top2, left2, 30, LA, LO) > 0.5
        cls[:] = 'grass'
        cls[fr > u2] = 'forest'
        cls[de] = 'desert'
        cls[c == 1] = 'foothill'; cls[c == 2] = 'steep'; cls[c == 3] = 'vsteep'
        cls[icem] = 'ice'
        cls[water & (wsea >= wlake)] = np.where(shallow_m, 'shallow', 'deep')[water & (wsea >= wlake)]
        cls[water & (wlake > wsea)] = 'fresh'
        cls[river_mask(LA, LO, step) & ~(water & (wsea >= wlake))] = 'fresh'
        img = paint(cls)
        if mode == 'measure':
            img = np.repeat(np.repeat(img, 3, axis=0), 3, axis=1)
        out.append((img, cls))
    a, b = out[0][0], out[1][0]
    H = min(a.shape[0], b.shape[0]); W0 = min(a.shape[1], b.shape[1])
    a, b = a[:H, :W0], b[:H, :W0]
    pad = 16; legend_h = 120; title_h = 44
    canvas = Image.new('RGB', (2 * W0 + 3 * pad, H + title_h + legend_h + pad), (250, 250, 247))
    canvas.paste(Image.fromarray(a), (pad, title_h)); canvas.paste(Image.fromarray(b), (2 * pad + W0, title_h))
    d = ImageDraw.Draw(canvas)
    try:
        f = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 18); fs = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 14)
    except Exception:
        f = fs = ImageFont.load_default()
    d.text((pad, 10), '%s - %.0f km square' % (title, 2 * half_km), fill=(30, 30, 30), font=f)
    d.text((pad, title_h - 18 + H + 22), 'Measured: slope over 90 m (NASADEM), depth (GEBCO); a pixel 90 m', fill=(60, 60, 60), font=fs)
    d.text((2 * pad + W0, title_h - 18 + H + 22), 'earth-2: its %s slope shares (15" water) with seeded detail; a pixel a 30 m plot (est. rule)' % ('30"' if GAME_CPD == 120 else '15"'), fill=(60, 60, 60), font=fs)
    d.text((2 * pad + W0, 14), "Both: ice, forest, desert from earth-1's 2' land cover; sea and lakes earth-1's", fill=(90, 90, 90), font=fs)
    # bridges
    for name, (la, lo), bear, L in bridges:
        p0 = dest(la, lo, bear + 180, L / 2); p1 = dest(la, lo, bear, L / 2)
        for x0 in (pad, 2 * pad + W0):
            xy = [(x0 + (q[1] - w) / (e - w) * W0, title_h + (n - q[0]) / (n - s) * H) for q in (p0, p1)]
            d.line(xy, fill=COL['bridge'], width=5)
            d.text((xy[1][0] + 6, xy[1][1] - 18), name, fill=(20, 20, 20), font=fs)
    # legend
    items = [('grass', 'Flat: grass'), ('forest', 'Flat: forest'), ('desert', 'Flat: desert'),
             ('foothill', 'Foothill 10-30% (buildable, dearer)'), ('steep', 'Steep 30-60% (refused)'),
             ('vsteep', 'Very steep 60%+ (refused)'), ('ice', 'Snow and ice (refused)'),
             ('shallow', 'Shallow sea, %d m or less (bridgeable)' % D.SHALLOW_M), ('deep', 'Deep sea'), ('fresh', 'Lake or river')]
    y0 = title_h + H + 46; x = pad
    for i, (k, lab) in enumerate(items):
        col = i % 4; row = i // 4
        xx = pad + col * (2 * W0 + pad) // 4; yy = y0 + row * 24
        d.rectangle([xx, yy, xx + 18, yy + 18], fill=COL[k], outline=(80, 80, 80))
        d.text((xx + 26, yy), lab, fill=(30, 30, 30), font=fs)
    path = os.path.join(HERE, place + '.png')
    canvas.save(path, optimize=True)
    # the shares in the box, measured and drawn
    stats = {}
    for lab, (img, cls) in zip(('measure', 'game'), out):
        land = ~np.isin(cls, ['deep', 'shallow', 'fresh'])
        stats[lab] = {k: round(float((cls == k).sum() / max(1, land.sum())), 3) for k in ('foothill', 'steep', 'vsteep', 'ice')}
    print(place, path, stats)


if __name__ == '__main__' and not os.environ.get('RESFIG'):
    for p in (sys.argv[1:] or list(PLACES)):
        render(p)


def resolution_panel(place='medellin', ks=(1, 2, 4, 8)):
    """The same place drawn from slope shares stored at 15", 30", 1' and 2' (the coarser cells land-weighted means
    of the 15" ones), each with the same seeded detail at 30 m: what a coarser layer loses."""
    title, clat, clon, half_km, _ = PLACES[place]
    hl = half_km / 111.195; hw = hl / math.cos(math.radians(clat))
    s, n, w, e = clat - hl, clat + hl, clon - hw, clon + hw
    shares, top, left = mosaic(slope_shares, s, n, w, e, 240)
    sea, _, _ = mosaic(lambda a, b: E.grid('sea', tid(a, b)), s, n, w, e, 240)
    lake, _, _ = mosaic(lambda a, b: E.grid('lake', tid(a, b)), s, n, w, e, 240)
    L = 1 - (sea.astype(np.float32) + lake) / 15
    step = 30.0; ny = int(2 * half_km * 1000 / step)
    lat = n - (np.arange(ny) + 0.5) * (n - s) / ny; lon = w + (np.arange(ny) + 0.5) * (e - w) / ny
    LA, LO = np.meshgrid(lat, lon, indexing='ij')
    u = noise(LA.shape, 11)
    panels = []
    for k in ks:
        if k > 1:
            H, W = shares.shape[1] // k * k, shares.shape[2] // k * k
            cw = L[:H, :W].reshape(H // k, k, W // k, k)
            agg = (shares[:, :H, :W].reshape(3, H // k, k, W // k, k) * cw[None]).sum(axis=(2, 4)) / np.maximum(cw.sum(axis=(1, 3)), 1e-9)[None]
            sh = [at(agg[j], top, left, 240 / k, LA, LO) for j in range(3)]
        else:
            sh = [at(shares[j], top, left, 240, LA, LO) for j in range(3)]
        c = sum((u > 1 - sh[j]).astype(int) for j in range(3))
        img = np.zeros(c.shape + (3,), np.uint8)
        for j, kk in enumerate(['grass', 'foothill', 'steep', 'vsteep']):
            img[c == j] = COL[kk]
        panels.append(img[::2, ::2])
    h, wd = panels[0].shape[:2]; pad = 12; th = 40
    canvas = Image.new('RGB', (len(ks) * (wd + pad) + pad, h + th + 30), (250, 250, 247))
    d = ImageDraw.Draw(canvas)
    try:
        f = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 16)
    except Exception:
        f = ImageFont.load_default()
    names = {1: '15" (460 m)', 2: '30" (930 m)', 4: "1' (1.85 km)", 8: "2' (3.7 km)"}
    d.text((pad, 8), '%s: slope shares stored at four resolutions, the same seeded detail (flat drawn as grass)' % title, fill=(30, 30, 30), font=f)
    for i, (k, p) in enumerate(zip(ks, panels)):
        canvas.paste(Image.fromarray(p), (pad + i * (wd + pad), th))
        d.text((pad + i * (wd + pad), th + h + 6), names[k], fill=(50, 50, 50), font=f)
    path = os.path.join(HERE, place + '-resolution.png')
    canvas.save(path, optimize=True)
    print(path)


if __name__ == '__main__' and os.environ.get('RESFIG'):
    resolution_panel(os.environ['RESFIG'])
