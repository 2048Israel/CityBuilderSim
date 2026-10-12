# The Earth 2 study's UI mockup (task B): the map's crops at the zooms the map uses, drawn from earth-2.bin itself,
# and the figures the mocked screens quote (the founding cards' ground, the bridges' profiles).
#
# The rule is figures.py's "game" rule (its noise, its thresholds, its colours), read from earth-2.bin's own layers
# rather than the site study's histograms: slope at 30" (FORMAT-2.txt: F, S, V shares of the cell's land), the
# shallow share at 15", sea and lake at 15", land cover at 2', rivers from GRWL. A plot's slope class is the count of
# the three shares its rank in a fixed noise field passes (spec-earth 2.3's forest rule applied to slope; est.).
#
# Two ways to draw a slope on forest (the study's D5):
#   A  the slope's grey drawn over every cover, forest included (figures.py as it was);
#   B  greys on open ground (grass, desert); forest on a slope stays forest, mixed toward that slope's grey
#      (FOREST_MIX of the way), so the forest and the slope both read. Ice wins over both.
# The far view (the GROUND tier) and the globe's picture (the PICTURE tier) are the far rule: each pixel's classes from
# the shares at its centre, a hash dither against them. Bridges are drawn as decks (TileRaster.DECK) under the road's
# colour (TileRaster.ROAD), a simple overlay.
#   python3 -I mockups.py            writes mock-*.png beside this script and prints the figures the page quotes
import sys, os, math, json
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, '..', '..', '..', 'tools', 'earth'))
sys.path.insert(0, HERE)
import numpy as np
from scipy import ndimage
from PIL import Image, ImageDraw, ImageFont
import figures as F            # its noise, at(), mosaic(), tid(), dest() and colours
import terrain as T
from earthread import Earth

E2 = Earth(os.environ.get('CBS_EARTH2', os.path.join(T.OUT2, 'earth-2.bin')))
TABLE = E2.raw('landmasses').decode('utf-8').split('\n')
PLOT_M = 30.0                  # World.PLOT_M


def rgb(h):
    return np.array(((h >> 16) & 255, (h >> 8) & 255, h & 255), np.float32)


# TileRaster.GROUND (L69): GRASS, FOREST, FRESH, SALT, SAND, ICE, DESERT; the study's proposals (figures.py's) after
GROUND = dict(grass=0xa8c47e, forest=0x527c45, fresh=0x78b4dc, deep=0x2f5d88, sand=0xe6daaa, ice=0xdfe7ee, desert=0xd2b48c,
              foothill=0xc8c3b8, steep=0x9b968c, vsteep=0x6c6862, shallow=0x4f87b5)
DECK = 0x5d4c3c                # TileRaster.DECK (L81)
HIGHWAY = 0x3b4048             # TileRaster.ROAD[HIGHWAY]
RAIL = 0x707070                # TileRaster.RAIL_LINE
VOID = 0x16222c                # TileRaster.VOID
UNOWNED_DIM = 0.4              # TileRaster.UNOWNED_DIM
OFFER = 0xf6a6c9               # LandMap.OFFER_EDGE
GREYS = ('foothill', 'steep', 'vsteep')
FOREST_MIX = (0.50, 0.50, 0.60)   # B: forest on foothill, steep, very steep, this much of the way to the slope's grey (est.)

# cover codes
GRASS, FOREST, DESERT, ICE, FRESH, SHALLOW, DEEP = range(7)


def tid(la, lo):
    return (la + 90) * 360 + lo + 180


def slope_tile(la, lo):
    """earth-2's slope layer for a tile, decoded: (3, 120, 120) shares of land at or above foothill, steep, very steep."""
    b = E2.grid('slope', tid(la, lo)).astype(np.int32)
    f = (b & 7) / 7.0; s = ((b >> 3) & 7) / 7.0; v = s * (b >> 6) / 3.0
    return np.stack([f, s, v]).astype(np.float32)


def box(clat, clon, wkm, hkm):
    hl = hkm / 2 / 111.195; hw = wkm / 2 / 111.195 / math.cos(math.radians(clat))
    return clat - hl, clat + hl, clon - hw, clon + hw


class Ground:
    """A box's ground at plots of `step` metres (the game's rule, est.): cover and slope class per plot."""

    def __init__(self, clat, clon, wkm, hkm, step=PLOT_M, seed=0):
        self.clat, self.clon, self.step = clat, clon, step
        s, n, w, e = box(clat, clon, wkm, hkm)
        self.s, self.n, self.w, self.e = s, n, w, e
        ny = int(round(hkm * 1000 / step)); nx = int(round(wkm * 1000 / step))
        lat = n - (np.arange(ny) + 0.5) * (n - s) / ny; lon = w + (np.arange(nx) + 0.5) * (e - w) / nx
        LA, LO = np.meshgrid(lat, lon, indexing='ij')
        self.LA, self.LO = LA, LO
        sea, top, left = F.mosaic(lambda a, b: E2.grid('sea', tid(a, b)), s, n, w, e, 240)
        lake, _, _ = F.mosaic(lambda a, b: E2.grid('lake', tid(a, b)), s, n, w, e, 240)
        shal, _, _ = F.mosaic(lambda a, b: E2.grid('shallow', tid(a, b)), s, n, w, e, 240)
        lc, top2, left2 = F.mosaic(lambda a, b: E2.grid('landcover', tid(a, b)), s, n, w, e, 30)
        sl, top3, left3 = F.mosaic(slope_tile, s, n, w, e, 120)
        shape = LA.shape
        u = F.noise(shape, 11 + seed); u2 = F.noise(shape, 21 + seed); u3 = F.noise(shape, 31 + seed); u4 = F.noise(shape, 41 + seed)
        wsea = F.at(sea.astype(np.float32) / 15, top, left, 240, LA, LO); wlake = F.at(lake.astype(np.float32) / 15, top, left, 240, LA, LO)
        water = (1 - wsea - wlake) + (u4 - 0.5) * 0.6 < 0.5
        sh = [F.at(sl[k], top3, left3, 120, LA, LO) for k in range(3)]
        c = sum((u > 1 - sh[k]).astype(np.int8) for k in range(3))
        # the shallow share is of a cell's SEA (0 where it has none), so it is interpolated weighted by the sea share:
        # read plain, the land cells' 0 pulls it down along every coast and draws a fringe of deep sea at the shore
        sw = sea.astype(np.float32) / 15
        shallow = F.at(shal.astype(np.float32) / 15 * sw, top, left, 240, LA, LO) / np.maximum(F.at(sw, top, left, 240, LA, LO), 1e-6) > u2
        forest_share = (lc & 0x0F).astype(np.float32) / 15; ice = ((lc & 0x40) > 0).astype(np.float32); desert = ((lc & 0x20) > 0).astype(np.float32)
        icem = F.at(ice, top2, left2, 30, LA, LO) + 0.5 * (u3 - 0.5) > 0.5
        fr = F.at(forest_share, top2, left2, 30, LA, LO); de = F.at(desert, top2, left2, 30, LA, LO) > 0.5
        cov = np.full(shape, GRASS, np.int8)
        cov[fr > u2] = FOREST
        cov[de] = DESERT
        cov[icem] = ICE
        seaw = water & (wsea >= wlake)
        cov[seaw] = np.where(shallow, SHALLOW, DEEP)[seaw]
        cov[water & (wlake > wsea)] = FRESH
        rivers = [r for a, b in F.tiles_for(s, n, w, e) for r in E2.rivers(tid(a, b))]
        cov[self.river_mask(rivers) & ~seaw] = FRESH
        # the small lakes (earth-1's points with an area), each a disc of its area - the game frays its blob with noise
        ny, nx = shape
        for a, b in F.tiles_for(s, n, w, e):
            for la, lo, km2 in E2.small_lakes(tid(a, b)):
                r, cc = self.rc(la, lo); rad = math.sqrt(km2 / math.pi) * 1000 / step
                if r + rad < 0 or cc + rad < 0 or r - rad >= ny or cc - rad >= nx: continue
                r0, r1 = max(0, int(r - rad)), min(ny, int(r + rad) + 1); c0, c1 = max(0, int(cc - rad)), min(nx, int(cc + rad) + 1)
                rr, ccs = np.mgrid[r0:r1, c0:c1]
                fray = rad * (1 + 0.5 * (u4[r0:r1, c0:c1] - 0.5))        # the blob's edge frayed by the water's noise
                m = ((rr + 0.5 - r) ** 2 + (ccs + 0.5 - cc) ** 2 <= fray * fray) & ~seaw[r0:r1, c0:c1]
                cov[r0:r1, c0:c1][m] = FRESH
        c[(cov == ICE) | (cov >= FRESH)] = 0
        self.cov, self.cls = cov, c

    def river_mask(self, rivers, least_px=0.5):
        """figures.py's river stroke: every plot whose centre a GRWL segment's width covers (at least least_px)."""
        m = np.zeros(self.LA.shape, bool); ny, nx = self.LA.shape
        s, n, w, e, step = self.s, self.n, self.w, self.e, self.step
        for a in rivers:
            for k in range(len(a) - 1):
                p0, p1 = a[k], a[k + 1]
                L = math.hypot((p1[0] - p0[0]) * 111195, (p1[1] - p0[1]) * 111195 * math.cos(math.radians(self.clat)))
                for t in np.linspace(0, 1, max(2, int(L / (step / 2)) + 1)):
                    la = p0[0] + (p1[0] - p0[0]) * t; lo = p0[1] + (p1[1] - p0[1]) * t
                    r = (n - la) / (n - s) * ny; cc = (lo - w) / (e - w) * nx
                    rad = max(least_px, (p0[2] + (p1[2] - p0[2]) * t) / 2 / step)
                    r0, r1 = int(r - rad), int(r + rad) + 1; c0, c1 = int(cc - rad), int(cc + rad) + 1
                    if r1 < 0 or c1 < 0 or r0 >= ny or c0 >= nx: continue
                    rr, ccs = np.mgrid[max(0, r0):min(ny, r1), max(0, c0):min(nx, c1)]
                    m[rr, ccs] |= (rr + 0.5 - r) ** 2 + (ccs + 0.5 - cc) ** 2 <= rad ** 2 + 0.25
        return m

    def rc(self, lat, lon):
        ny, nx = self.LA.shape
        return (self.n - lat) / (self.n - self.s) * ny, (lon - self.w) / (self.e - self.w) * nx

    def latlon(self, r, c):
        ny, nx = self.LA.shape
        return self.n - (r + 0.5) / ny * (self.n - self.s), self.w + (c + 0.5) / nx * (self.e - self.w)


def paint(cov, cls, way='B'):
    """The plots' colours: cover, then slope by way A or B, ice over both."""
    img = np.zeros(cov.shape + (3,), np.float32)
    for code, name in ((GRASS, 'grass'), (FOREST, 'forest'), (DESERT, 'desert'), (ICE, 'ice'), (FRESH, 'fresh'),
                       (SHALLOW, 'shallow'), (DEEP, 'deep')):
        img[cov == code] = rgb(GROUND[name])
    for k, g in enumerate(GREYS, start=1):
        on = cls == k
        if way == 'A':
            img[on & (cov <= DESERT)] = rgb(GROUND[g])
        else:
            img[on & ((cov == GRASS) | (cov == DESERT))] = rgb(GROUND[g])
            m = FOREST_MIX[k - 1]
            img[on & (cov == FOREST)] = rgb(GROUND['forest']) * (1 - m) + rgb(GROUND[g]) * m
    return img


def to_png(img, path, scale=1):
    a = np.clip(np.round(img), 0, 255).astype(np.uint8)
    if scale > 1:
        a = np.repeat(np.repeat(a, scale, axis=0), scale, axis=1)
    Image.fromarray(a).save(path, optimize=True)


def dim_outside(img, owned):
    out = img.copy()
    out[~owned] = img[~owned] * (1 - UNOWNED_DIM) + rgb(VOID) * UNOWNED_DIM
    return out


def shares(g, mask):
    """Shares of the land under a mask by class (flat, foothill, steep, very steep, ice) and of the whole by water."""
    land = mask & (g.cov <= ICE); n = max(1, int(land.sum())); tot = max(1, int(mask.sum()))
    out = dict(flat=float(((g.cls == 0) & land & (g.cov != ICE)).sum() / n),
               foothill=float(((g.cls == 1) & land).sum() / n), steep=float(((g.cls == 2) & land).sum() / n),
               vsteep=float(((g.cls == 3) & land).sum() / n), ice=float(((g.cov == ICE) & mask).sum() / n),
               forest_on_slope=float(((g.cov == FOREST) & (g.cls > 0) & land).sum() / n),
               forest=float(((g.cov == FOREST) & land).sum() / n))
    out.update(dry=float((land & (g.cov != ICE)).sum() / tot), fresh=float(((g.cov == FRESH) & mask).sum() / tot),
               sea=float((((g.cov == SHALLOW) | (g.cov == DEEP)) & mask).sum() / tot),
               shallow_of_sea=float(((g.cov == SHALLOW) & mask).sum() / max(1, (((g.cov == SHALLOW) | (g.cov == DEEP)) & mask).sum())))
    return {k: round(v, 4) for k, v in out.items()}


# ------------------------------------------------------------------ decks
def line_plots(g, a, b):
    """The plots a straight line from a to b (lat, lon) passes, in order, as (r, c)."""
    r0, c0 = g.rc(*a); r1, c1 = g.rc(*b)
    n = int(max(abs(r1 - r0), abs(c1 - c0)) * 2) + 2
    seen = []; last = None
    for t in np.linspace(0, 1, n):
        rc = (int(r0 + (r1 - r0) * t), int(c0 + (c1 - c0) * t))
        if rc != last:
            seen.append(rc); last = rc
    return seen


def water_runs(g, path):
    """The runs of water (fresh, shallow, deep) along a path of plots: [(start index, length, kinds)]."""
    runs = []; i = 0
    while i < len(path):
        r, c = path[i]
        if 0 <= r < g.cov.shape[0] and 0 <= c < g.cov.shape[1] and g.cov[r, c] >= FRESH:
            j = i; kinds = set()
            while j < len(path) and g.cov[path[j]] >= FRESH:
                kinds.add(int(g.cov[path[j]])); j += 1
            runs.append((i, j - i, kinds)); i = j
        else:
            i += 1
    return runs


def water_metres(g, a, b, step=5.0):
    """The runs of water along a straight line a-b on a plot grid, in metres (sampled every `step` m)."""
    L = math.hypot((b[0] - a[0]) * 111195, (b[1] - a[1]) * 111195 * math.cos(math.radians(a[0])))
    n = int(L / step); runs = []; cur = 0; H, W = g.cov.shape
    for i in range(n + 1):
        t = i / n
        r, c = (int(x) for x in g.rc(a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t))
        if 0 <= r < H and 0 <= c < W and g.cov[r, c] >= FRESH:
            cur += step
        elif cur:
            runs.append(cur); cur = 0
    if cur: runs.append(cur)
    return dict(line_m=round(L), water_runs_m=[round(x) for x in runs])


class crop:
    """A window of a Ground, `h` x `w` plots centred on a point: its cover, classes and a point's plot in it."""

    def __init__(self, g, lat, lon, w, h):
        r, c = (int(x) for x in g.rc(lat, lon))
        self.r0, self.c0 = r - h // 2, c - w // 2
        self.g = g
        self.cov = g.cov[self.r0:self.r0 + h, self.c0:self.c0 + w]
        self.cls = g.cls[self.r0:self.r0 + h, self.c0:self.c0 + w]
        self.step = g.step

    def rc(self, lat, lon):
        r, c = self.g.rc(lat, lon)
        return r - self.r0, c - self.c0


def draw_deck(img, g, a, b, px, road=HIGHWAY, half_plots=1.5, rail=False):
    """A road (or rail) along a-b at `px` image pixels a plot: the deck's colour wider on the water, the road's over it."""
    """...the deck only over water, as TileRaster edges a road on a bridge; the road the whole way."""
    r0, c0 = g.rc(*a); r1, c1 = g.rc(*b)
    H, W = img.shape[:2]
    xy = [(c0 * px, r0 * px), (c1 * px, r1 * px)]
    deck_w = max(3, int(round(2 * half_plots * px + 2 * max(1, px * 0.5))))
    road_w = max(1, int(round(2 * half_plots * px)))
    dm = Image.new('L', (W, H), 0); ImageDraw.Draw(dm).line(xy, fill=255, width=deck_w)
    rm = Image.new('L', (W, H), 0); ImageDraw.Draw(rm).line(xy, fill=255, width=road_w)
    wet = np.repeat(np.repeat(g.cov >= FRESH, px, axis=0), px, axis=1)[:H, :W] if hasattr(g, 'cov') else np.ones((H, W), bool)
    wet = ndimage.binary_dilation(wet, iterations=max(1, px))
    out = img.copy()
    out[(np.asarray(dm) > 0) & wet] = rgb(DECK)
    out[np.asarray(rm) > 0] = rgb(RAIL if rail else road)
    return out


def font(size, mono=False, bold=False):
    base = os.path.join(HERE, '..', '..', '..', 'src', 'main', 'resources', 'fonts')
    name = ('IBMPlexMono-' if mono else 'IBMPlexSans-') + ('SemiBold' if bold else 'Regular') + '.ttf'
    try:
        return ImageFont.truetype(os.path.join(base, name), size)
    except Exception:
        return ImageFont.load_default()


def label(img, xy, text, size=13, fill=(230, 237, 243), halo=(11, 17, 24)):
    pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
    d.text(xy, text, font=font(size, bold=True), fill=fill, stroke_width=3, stroke_fill=halo)
    return np.asarray(pil).astype(np.float32)


def scale_bar(img, px_per_km, km, xy=None, text=None, size=11):
    h, w = img.shape[:2]
    k = size / 11.0
    x0, y0 = xy if xy else (int(14 * k), int(h - 22 * k))
    pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
    L = int(round(km * px_per_km))
    d.rectangle([x0 - 4 * k, y0 - 18 * k, x0 + L + 4 * k, y0 + 8 * k], fill=(16, 25, 36))
    d.rectangle([x0, y0, x0 + L, y0 + 3 * k], fill=(230, 237, 243))
    d.text((x0, y0 - 16 * k), text or ('%g km' % km), font=font(size, mono=True), fill=(230, 237, 243))
    return np.asarray(pil).astype(np.float32)


# ------------------------------------------------------------------ the far rule (GROUND and PICTURE tiers)
def hash01(shape, seed):
    return np.random.default_rng(seed).random(shape).astype(np.float32)


def sample15(name, LA, LO):
    """A 15" layer's value at points (nearest cell), each tile read once."""
    out = np.zeros(LA.shape, np.float32)
    la0 = np.floor(LA).astype(int); lo0 = np.floor(LO).astype(int)
    for a, b in set(zip(la0.ravel().tolist(), lo0.ravel().tolist())):
        m = (la0 == a) & (lo0 == b)
        g = E2.grid(name, tid(a, b))
        r = np.clip(((a + 1 - LA[m]) * 240).astype(int), 0, 239); c = np.clip(((LO[m] - b) * 240).astype(int), 0, 239)
        out[m] = g[r, c]
    return out


def far(clat, clon, wkm, hkm, km_px, rivers=True, coarse=False, shelf=False):
    """A far view: a pixel km_px across, its classes from the shares at its centre and a hash dither (est.)."""
    s, n, w, e = box(clat, clon, wkm, hkm)
    ny = int(round(hkm / km_px)); nx = int(round(wkm / km_px))
    lat = n - (np.arange(ny) + 0.5) * (n - s) / ny; lon = w + (np.arange(nx) + 0.5) * (e - w) / nx
    LA, LO = np.meshgrid(lat, lon, indexing='ij')
    if coarse:   # the planet's picture: far.land and far.lake at 4' (bands), as GlobePicture's far rule reads them
        la0, la1 = math.floor(s), math.floor(n - 1e-9)
        land = np.concatenate([E2.grid('far.land', la + 90) for la in range(la1, la0 - 1, -1)], axis=0).astype(np.float32) / 255
        lakef = np.concatenate([E2.grid('far.lake', la + 90) for la in range(la1, la0 - 1, -1)], axis=0).astype(np.float32) / 255
        top = la1 + 1
        r = np.clip(((top - LA) * 15).astype(int), 0, land.shape[0] - 1); c = (((LO + 180) * 15).astype(int)) % land.shape[1]
        wland = land[r, c]; wlake = lakef[r, c]; wsea = np.clip(1 - wland - wlake, 0, 1)
        shal = sample15('shallow', LA, LO) / 15 if shelf else np.zeros_like(wsea)   # the texel centre's shallow share
    else:
        sea, top, left = F.mosaic(lambda a, b: E2.grid('sea', tid(a, b)), s, n, w, e, 240)
        lake, _, _ = F.mosaic(lambda a, b: E2.grid('lake', tid(a, b)), s, n, w, e, 240)
        sh15, _, _ = F.mosaic(lambda a, b: E2.grid('shallow', tid(a, b)), s, n, w, e, 240)
        wsea = F.at(sea.astype(np.float32) / 15, top, left, 240, LA, LO); wlake = F.at(lake.astype(np.float32) / 15, top, left, 240, LA, LO)
        sw = sea.astype(np.float32) / 15     # weighted by the sea share, as Ground's
        shal = F.at(sh15.astype(np.float32) / 15 * sw, top, left, 240, LA, LO) / np.maximum(F.at(sw, top, left, 240, LA, LO), 1e-6)
    lc, top2, left2 = F.mosaic(lambda a, b: E2.grid('landcover', tid(a, b)), s, n, w, e, 30)
    sl, top3, left3 = F.mosaic(slope_tile, s, n, w, e, 120)
    fr = F.at((lc & 0x0F).astype(np.float32) / 15, top2, left2, 30, LA, LO, order=0)
    ice = F.at(((lc & 0x40) > 0).astype(np.float32), top2, left2, 30, LA, LO, order=0) > 0.5
    de = F.at(((lc & 0x20) > 0).astype(np.float32), top2, left2, 30, LA, LO, order=0) > 0.5
    sh = [F.at(sl[k], top3, left3, 120, LA, LO) for k in range(3)]
    d1 = hash01(LA.shape, 5); d2 = hash01(LA.shape, 6); d3 = hash01(LA.shape, 7)
    cov = np.full(LA.shape, GRASS, np.int8)
    cov[d1 < fr] = FOREST; cov[de] = DESERT; cov[ice] = ICE
    c = sum((d2 < sh[k]).astype(np.int8) for k in range(3))
    seaw = wsea > 0.5
    cov[seaw] = np.where(d3 < shal, SHALLOW, DEEP)[seaw]
    cov[(wlake > 0.5) & ~seaw] = FRESH
    g = type('G', (), {})()
    g.s, g.n, g.w, g.e, g.step, g.clat, g.LA, g.LO = s, n, w, e, km_px * 1000, clat, LA, LO
    if rivers and not coarse:
        rv = [r for a, b in F.tiles_for(s, n, w, e) for r in E2.rivers(tid(a, b)) if max(p[2] for p in r) >= 100]
        m = Ground.river_mask(g, rv, least_px=0.75)
        cov[m & ~seaw] = FRESH
    c[(cov == ICE) | (cov >= FRESH)] = 0
    g.cov, g.cls = cov, c
    g.rc = lambda lat, lon: Ground.rc(g, lat, lon)
    return g


# ------------------------------------------------------------------ the founding card's ground
def snap(g, r0, c0, reach=167, test=20):
    """EarthTerrain.siteNear()'s ring search on a plot grid: the nearest plot whose centre and eight points `test`
    plots round it are buildable (Earth 2: dry and flat or foothill), within `reach` plots. None when there is none."""
    ok = (g.cov <= DESERT) & (g.cls <= 1)
    H, W = ok.shape
    def site(r, c):
        if not (0 <= r < H and 0 <= c < W) or not ok[r, c]: return False
        for q in range(8):
            a = q * math.pi / 4
            rr = int(math.floor(r + 0.5 + test * math.sin(a))); cc = int(math.floor(c + 0.5 + test * math.cos(a)))
            if not (0 <= rr < H and 0 <= cc < W) or not ok[rr, cc]: return False
        return True
    offs = [(dy * dy + dx * dx, dy, dx) for dy in range(-reach, reach + 1) for dx in range(-reach, reach + 1)
            if dy * dy + dx * dx <= reach * reach]
    offs.sort(key=lambda p: (int(math.sqrt(p[0])), p[1], p[2]))     # ring by ring of a plot, then north to south, west to east
    for _, dy, dx in offs:
        if site(r0 + dy, c0 + dx): return r0 + dy, c0 + dx
    return None


def disc(g, r, c, plots=167):
    H, W = g.cov.shape
    rr, cc = np.mgrid[0:H, 0:W]
    return (rr - r) ** 2 + (cc - c) ** 2 <= plots * plots


def nearest_km(g, mask, r, c):
    rr, cc = np.nonzero(mask)
    if len(rr) == 0: return None
    return float(np.sqrt(((rr - r) ** 2 + (cc - c) ** 2).min()) * g.step / 1000)


def depth_m(v):
    v = int(v)
    return 0 if v == 0 else v if v <= 200 else 200 + 10 * (v - 200) if v < 255 else 750


def deepest_within(lat, lon, km):
    """The deepest 1' depth cell (earth-2's depth layer, a cell's mean) whose centre lies within km of a point."""
    best = 0
    dl = km / 111.195; dw = dl / math.cos(math.radians(lat))
    for la in range(math.floor(lat - dl), math.floor(lat + dl) + 1):
        for lo in range(math.floor(lon - dw), math.floor(lon + dw) + 1):
            gd = E2.grid('depth', tid(la, lo))
            cl = la + 1 - (np.arange(60) + 0.5) / 60; cn = lo + (np.arange(60) + 0.5) / 60
            CL, CN = np.meshgrid(cl, cn, indexing='ij')
            dd = np.hypot((CL - lat) * 111.195, (CN - lon) * 111.195 * math.cos(math.radians(lat)))
            sel = gd[dd <= km]
            if sel.size: best = max(best, max(depth_m(x) for x in sel))
    return best


def card(name, g, lat, lon):
    r0, c0 = (int(x) for x in g.rc(lat, lon))
    s = snap(g, r0, c0)
    out = dict(name=name, spot=(lat, lon))
    if s is None:
        m = disc(g, r0, c0)
        out.update(site=None, ground=shares(g, m))
        return out
    r, c = s
    m = disc(g, r, c)
    sl = g.latlon(r, c)
    out.update(site=(round(sl[0], 4), round(sl[1], 4)), snapped_km=round(math.hypot(r - r0, c - c0) * g.step / 1000, 2), ground=shares(g, m))
    land = m & (g.cov <= DESERT)
    km2 = (g.step / 1000) ** 2
    out['km2'] = {k: round(float(((g.cls == i) & land).sum() * km2), 2) for i, k in enumerate(('flat', 'foothill', 'steep', 'vsteep'))}
    out['shallow_km'] = nearest_km(g, m & (g.cov == SHALLOW), r, c)
    out['deep_km'] = nearest_km(g, m & (g.cov == DEEP), r, c)
    out['fresh_km'] = nearest_km(g, m & (g.cov == FRESH), r, c)
    out['sea_km'] = nearest_km(g, m & ((g.cov == SHALLOW) | (g.cov == DEEP)), r, c)
    out['deepest_m'] = deepest_within(sl[0], sl[1], 5)
    return out


def landmass_at(lat, lon):
    la, lo = math.floor(lat), math.floor(lon)
    g = E2.landmass(tid(la, lo))
    i = int(g[min(239, int((la + 1 - lat) * 240)), min(239, int((lon - lo) * 240))])
    return i, (TABLE[i - 1].split('\t') if i > 0 else None)


def profile(a, b, step=25.0):
    """A straight crossing's water from the data: the 15" cells it passes (landmass 0 = water), their shallow share,
    GEBCO's depth and the 1' depth layer, from land on one side to land on the other (est.: a line, not the planner's
    search)."""
    R = T.R_M
    p1, p2 = math.radians(a[0]), math.radians(b[0]); dl = math.radians(b[1] - a[1])
    L = 2 * R * math.asin(math.sqrt(math.sin((p2 - p1) / 2) ** 2 + math.cos(p1) * math.cos(p2) * math.sin(dl / 2) ** 2))
    n = int(L / step) + 1
    rows = []
    for i in range(n + 1):
        t = i / n; lat = a[0] + (b[0] - a[0]) * t; lon = a[1] + (b[1] - a[1]) * t
        la, lo = math.floor(lat), math.floor(lon)
        r, c = min(239, int((la + 1 - lat) * 240)), min(239, int((lon - lo) * 240))
        lm = int(E2.landmass(tid(la, lo))[r, c]); sh = int(E2.grid('shallow', tid(la, lo))[r, c]); sea = int(E2.grid('sea', tid(la, lo))[r, c])
        dp = depth_m(E2.grid('depth', tid(la, lo))[min(59, int((la + 1 - lat) * 60)), min(59, int((lon - lo) * 60))])
        gb = -int(F.G[int((lat + 90) * 240), int((lon + 180) * 240) % 86400])
        rows.append((t * L, lm, sh, sea, dp, gb))
    wet = [i for i, r in enumerate(rows) if r[1] == 0]
    i0, i1 = wet[0], wet[-1]
    lm0 = next((rows[i][1] for i in range(i0 - 1, -1, -1) if rows[i][1]), 0)
    lm1 = next((rows[i][1] for i in range(i1 + 1, len(rows)) if rows[i][1]), 0)
    water = rows[i0:i1 + 1]
    deep = next((r for r in water if r[1] == 0 and r[3] > 0 and r[2] < 8), None)
    return dict(line_km=round(L / 1000, 2), water_km=round((rows[i1][0] - rows[i0][0] + step) / 1000, 2),
                from_landmass=lm0, to_landmass=lm1,
                gebco_m=(min(r[5] for r in water if r[1] == 0), max(r[5] for r in water if r[1] == 0)),
                depth_layer_m=(min(r[4] for r in water if r[1] == 0), max(r[4] for r in water if r[1] == 0)),
                shallow_min=min(r[2] for r in water if r[1] == 0 and r[3] > 0),
                deep_at_km=round((deep[0] - rows[i0][0]) / 1000, 2) if deep else None, deep_gebco_m=deep[5] if deep else None,
                deepest_gebco_m=max(r[5] for r in water))


# ------------------------------------------------------------------ the boards' pictures
def out(name):
    return os.path.join(HERE, name)


def medellin(fig):
    # the Aburra valley, Medellin's centre (Parque Berrio) at the middle
    g = Ground(6.245, -75.575, 18.0, 18.0)
    whole = np.ones(g.cov.shape, bool)
    fig['medellin_box'] = shares(g, whole)
    # the close-up: the valley's eastern rim (picked for its mix: flat, foothill, steep, forest on each), the same
    # plots as the 1 px crop, at 8 px a plot (L0, past BIG_TILES_FROM)
    k = crop(g, 6.2017, -75.5233, 60, 40)
    r0, c0, cc, cl = k.r0, k.c0, k.cov, k.cls
    fig['medellin_close'] = dict(at=g.latlon(r0 + 20, c0 + 30), **shares(k, np.ones(cc.shape, bool)))
    for way in 'AB':
        img = paint(g.cov, g.cls, way)
        pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
        d.rectangle([c0 - 1, r0 - 1, c0 + 60, r0 + 40], outline=(255, 255, 255), width=1)
        img = np.asarray(pil).astype(np.float32)
        img = scale_bar(img, 1000 / PLOT_M, 2)
        to_png(img, out('mock-medellin-%s.png' % way.lower()))
        to_png(paint(cc, cl, way), out('mock-medellin-l0-%s.png' % way.lower()), scale=8)
    fig['card_medellin'] = card('Medellin', g, 6.2518, -75.5683)
    return g


SHORT_BRIDGE_M = 14 * PLOT_M      # TilePainter.MAX_BRIDGE / RAIL_BRIDGE: 14 plots, 420 m


def dashed(img, g, a, b, px, colour=0xe3b341, dash=6):
    """A crossing the runs would not lay: a dashed line in Palette.WARN over the water."""
    r0, c0 = g.rc(*a); r1, c1 = g.rc(*b)
    L = math.hypot(r1 - r0, c1 - c0) * px; n = max(2, int(L / dash))
    pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
    for i in range(0, n, 2):
        t0, t1 = i / n, min(1, (i + 1) / n)
        d.line([((c0 + (c1 - c0) * t0) * px, (r0 + (r1 - r0) * t0) * px), ((c0 + (c1 - c0) * t1) * px, (r0 + (r1 - r0) * t1) * px)],
               fill=tuple(int(x) for x in rgb(colour)), width=max(2, px))
    return np.asarray(pil).astype(np.float32)


def halifax(fig):
    g = Ground(44.648, -63.580, 16.0, 14.0)
    # the city's ground: both shores of the Narrows, downtown, the North End and Dartmouth (illustrative)
    H, W = g.cov.shape
    owned = np.zeros((H, W), bool)
    r0, c0 = g.rc(44.700, -63.640); r1, c1 = g.rc(44.630, -63.540)
    owned[int(r0):int(r1), int(c0):int(c1)] = True
    bridges = {'Macdonald': ((44.6604, -63.5925), (44.6670, -63.5767)), 'MacKay': ((44.6735, -63.6200), (44.6820, -63.6038))}
    # the water under each line, in metres (sampled every 5 m on the plots): what a run would have to bridge
    fig['halifax_water_m'] = {name: water_metres(g, a, b) for name, (a, b) in bridges.items()}
    laid = {name: max(fig['halifax_water_m'][name]['water_runs_m'] or [0]) <= SHORT_BRIDGE_M for name in bridges}
    fig['halifax_laid'] = laid
    img = paint(g.cov, g.cls, 'B')
    img = dim_outside(img, owned)
    img = np.repeat(np.repeat(img, 2, axis=0), 2, axis=1)                  # 2 px a plot, so the window shows it near 1:1
    for name, (a, b) in bridges.items():
        img = draw_deck(img, g, a, b, 2, half_plots=0.5) if laid[name] else dashed(img, g, a, b, 2, dash=5)
    pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
    d.rectangle([int(c0) * 2, int(r0) * 2, int(c1) * 2, int(r1) * 2], outline=(255, 255, 255), width=1)
    k = crop(g, 44.6707, -63.5982, 120, 88)
    d.rectangle([k.c0 * 2, k.r0 * 2, (k.c0 + 120) * 2, (k.r0 + 88) * 2], outline=(140, 196, 255), width=1)
    img = np.asarray(pil).astype(np.float32)
    for nm, (la, lo), dx, dy in (('Macdonald', (44.6705, -63.574), 12, -16), ('MacKay', (44.684, -63.602), 12, -20), ('Bedford Basin', (44.703, -63.672), 0, 0)):
        r, c = g.rc(la, lo); img = label(img, (c * 2 + dx, r * 2 + dy), nm, 15)
    img = scale_bar(img, 2 * 1000 / PLOT_M, 2)
    to_png(img, out('mock-halifax.png'))
    fig['halifax_box'] = shares(g, owned)
    # the Narrows at 5 px a plot, the same plots: the decks with their edges, shallow sea in the channel
    n = crop(g, 44.6707, -63.5982, 120, 88)
    img = paint(n.cov, n.cls, 'B')
    img = np.repeat(np.repeat(img, 5, axis=0), 5, axis=1)
    for name, (a, b) in bridges.items():
        img = draw_deck(img, n, a, b, 5, half_plots=0.5) if laid[name] else dashed(img, n, a, b, 5, dash=10)
    wm = fig['halifax_water_m']
    r, c = n.rc(44.6637, -63.5846)
    img = label(img, (c * 5 - 250, r * 5 + 30), 'Macdonald: %d m of water, %d plots' % (max(wm['Macdonald']['water_runs_m']), round(max(wm['Macdonald']['water_runs_m']) / PLOT_M)), 13)
    r, c = n.rc(44.6778, -63.6119)
    img = label(img, (c * 5 - 20, r * 5 + 18), 'MacKay: %d m, %d plots' % (max(wm['MacKay']['water_runs_m']), round(max(wm['MacKay']['water_runs_m']) / PLOT_M)), 13)
    img = scale_bar(img, 5 * 1000 / PLOT_M, 0.5, text='500 m')
    to_png(img, out('mock-halifax-narrows.png'))
    # the same lines over eight seeds' fray, and the share's own half-contour (no fray)
    fig['halifax_seeds_m'] = {name: [] for name in bridges}
    for seed in range(0, 40, 5):
        gs = Ground(44.668, -63.598, 5.0, 4.0, seed=seed)
        for name, (a, b) in bridges.items():
            fig['halifax_seeds_m'][name].append(max(water_metres(gs, a, b)['water_runs_m'] or [0]))
    fig['card_halifax'] = card('Halifax', g, 44.6488, -63.5752)
    return g


def everest(fig):
    # the Khumbu: Namche Bazaar and Thame's valleys up to Everest and Lhotse (earth-1's ice above, the greys below)
    g = Ground(27.905, 86.79, 30.0, 30.0)
    fig['everest_box'] = shares(g, np.ones(g.cov.shape, bool))
    img = paint(g.cov, g.cls, 'B')
    r, c = (int(x) for x in g.rc(27.8362, 86.7646))
    pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
    d.ellipse([c - 167, r - 167, c + 167, r + 167], outline=(255, 158, 158), width=2)      # the card's 5 km round Tengboche
    img = np.asarray(pil).astype(np.float32)
    for nm, (la, lo), dx in (('Everest', (27.9881, 86.9250), -66), ('Lhotse', (27.9617, 86.9333), -58), ('Namche Bazaar', (27.8069, 86.7140), 8),
                         ('Tengboche', (27.8362, 86.7646), 8), ('Dingboche', (27.8923, 86.8314), 8), ('Thame', (27.8333, 86.6500), 8)):
        r, c = g.rc(la, lo)
        if 0 <= r < img.shape[0] and 0 <= c < img.shape[1]:
            pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
            d.ellipse([c - 6, r - 6, c + 6, r + 6], outline=(11, 17, 24), width=3)
            img = np.asarray(pil).astype(np.float32)
            img = label(img, (c + dx * 1.6, r - 14), nm, 20)
    img = scale_bar(img, 1000 / PLOT_M, 2, size=19)
    to_png(img, out('mock-everest.png'))
    fig['card_tengboche'] = card('Tengboche', g, 27.8362, 86.7646)
    fig['card_everest'] = card('Everest', g, 27.9881, 86.9250)
    return g


def confederation(fig):
    a, b = (46.2448, -63.6961), (46.1617, -63.8136)        # Borden-Carleton, Cape Jourimain
    g = Ground(46.205, -63.755, 26.0, 18.0)
    H, W = g.cov.shape
    owned = np.zeros((H, W), bool)
    # the city's ground (illustrative): its home round Cape Jourimain, and an offer bought at Borden-Carleton
    for (la0, lo0, la1, lo1) in ((46.175, -63.900, 46.120, -63.790), (46.262, -63.712, 46.236, -63.666)):
        r0, c0 = g.rc(la0, lo0); r1, c1 = g.rc(la1, lo1)
        owned[max(0, int(r0)):int(r1), max(0, int(c0)):int(c1)] = True
    img = paint(g.cov, g.cls, 'B')          # not dimmed: the sea's two blues are the point here
    # the order: one deck, Borden-Carleton to Cape Jourimain (est.: the line between the two ends)
    a2 = (46.2520, -63.6860); b2 = (46.1560, -63.8210)
    img = draw_deck(img, g, a2, b2, 1, half_plots=1.5)
    fig['confed_runs'] = [(n, sorted(k)) for _, n, k in water_runs(g, line_plots(g, a2, b2))]
    for (la0, lo0, la1, lo1) in ((46.175, -63.900, 46.120, -63.790), (46.262, -63.712, 46.236, -63.666)):
        r0, c0 = g.rc(la0, lo0); r1, c1 = g.rc(la1, lo1)
        pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
        d.rectangle([int(c0), int(r0), int(c1), int(r1)], outline=(255, 255, 255), width=1)
        img = np.asarray(pil).astype(np.float32)
    img = label(img, (g.rc(46.262, -63.66)[1] + 6, g.rc(46.262, -63.66)[0] - 4), 'Prince Edward Island', 18)
    img = label(img, (g.rc(46.172, -63.898)[1] + 4, g.rc(46.172, -63.898)[0] - 22), 'the city: Cape Jourimain', 18)
    img = label(img, (g.rc(46.205, -63.745)[1] + 10, g.rc(46.205, -63.745)[0]), 'Highway Bridge, one order: 12.9 km', 18)
    img = scale_bar(img, 1000 / PLOT_M, 5, size=16)
    to_png(img, out('mock-confederation.png'))
    fig['confed_owned'] = shares(g, owned)
    return g


def far_views(fig):
    # the GROUND tier: a pixel 0.5 km (Medellin, the Northumberland Strait) and 1 km (the Himalaya)
    g = far(6.40, -75.55, 240, 160, 0.5)
    img = paint(g.cov, g.cls, 'B')
    r, c = g.rc(6.2518, -75.5683)
    img = label(img, (c + 8, r - 10), 'Medellin', 15)
    img = scale_bar(img, 2, 20)
    to_png(img, out('mock-far-andes.png'))
    fig['far_andes'] = shares(g, np.ones(g.cov.shape, bool))
    g = far(28.10, 86.70, 400, 260, 1.0)
    img = paint(g.cov, g.cls, 'B')
    for nm, (la, lo) in (('Everest', (27.9881, 86.9250)), ('Kathmandu', (27.7172, 85.3240))):
        r, c = g.rc(la, lo); img = label(img, (c + 6, r - 10), nm, 14)
    img = scale_bar(img, 1, 50)
    to_png(img, out('mock-far-himalaya.png'))
    fig['far_himalaya'] = shares(g, np.ones(g.cov.shape, bool))
    g = far(46.05, -63.20, 240, 150, 0.5)
    img = paint(g.cov, g.cls, 'B')
    img = draw_deck(img, g, (46.2520, -63.6860), (46.1560, -63.8210), 1, half_plots=1.0)
    img = draw_deck(img, g, (45.765, -62.685), (45.951, -62.744), 1, half_plots=0.5, road=0xf85149)   # the refused crossing, red
    r, c = g.rc(46.2448, -63.6961); img = label(img, (c + 8, r - 20), 'Confederation, 12.9 km', 14)
    r, c = g.rc(45.86, -62.70); img = label(img, (c - 300, r + 30), 'Caribou - Wood Islands: deep water 11.1 km out', 14)
    img = scale_bar(img, 2, 20)
    to_png(img, out('mock-far-strait.png'))
    fig['far_strait'] = shares(g, np.ones(g.cov.shape, bool))


def globe_swatch(fig):
    # the PICTURE tier: a 4' texel (7.4 km) - the Himalaya and the Tibetan plateau, high and flat; drawn 2 px a texel
    g = far(31.0, 86.0, 3000, 1500, 7.41, rivers=False, coarse=True)
    img = paint(g.cov, g.cls, 'B')
    img = np.repeat(np.repeat(img, 2, axis=0), 2, axis=1)
    for nm, (la, lo), dx, dy in (('Everest', (27.9881, 86.9250), 8, -22), ('Lhasa', (29.65, 91.10), 8, -8), ('Kathmandu', (27.7172, 85.3240), -70, 6)):
        r, c = g.rc(la, lo)
        pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
        d.ellipse([c * 2 - 3, r * 2 - 3, c * 2 + 3, r * 2 + 3], outline=(11, 17, 24), width=2)
        img = np.asarray(pil).astype(np.float32)
        img = label(img, (c * 2 + dx, r * 2 + dy), nm, 16)
    img = scale_bar(img, 2 / 7.41, 500, size=15)
    to_png(img, out('mock-globe-tibet.png'))
    fig['globe_tibet'] = shares(g, np.ones(g.cov.shape, bool))
    # ...and the Maritimes at the same texel: the shallow sea a bridge may cross reads at 7.4 km only where it is wide
    g = far(45.6, -62.5, 1300, 800, 7.41, rivers=False, coarse=True, shelf=True)
    img = paint(g.cov, g.cls, 'B')
    img = np.repeat(np.repeat(img, 3, axis=0), 3, axis=1)
    for nm, (la, lo), dx, dy in (('Halifax', (44.65, -63.60), 8, -8), ('Northumberland Strait', (46.05, -63.30), 10, 6), ('Sable Island Bank', (43.95, -60.0), 8, 8)):
        r, c = g.rc(la, lo)
        pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
        d.ellipse([c * 3 - 3, r * 3 - 3, c * 3 + 3, r * 3 + 3], outline=(11, 17, 24), width=2)
        img = np.asarray(pil).astype(np.float32)
        img = label(img, (c * 3 + dx, r * 3 + dy), nm, 12)
    img = scale_bar(img, 3 / 7.41, 100)
    to_png(img, out('mock-globe-maritimes.png'))
    fig['globe_maritimes'] = shares(g, np.ones(g.cov.shape, bool))


def land_office_map(fig, g):
    """The land office's map, 600 x 400 (MapView.SMALL_W x SMALL_H), on Medellin at 2 px a plot - the same plots as
    the 1 px crop: the city's ground lit, East's six offers edged in the offers' pink (East 3 picked), the rest dimmed
    (illustrative holdings: the city on the valley floor, its offers climbing the valley's east side)."""
    sub = crop(g, 6.2470, -75.5640, 300, 200)
    H, W = sub.cov.shape
    owned = np.zeros((H, W), bool)
    r0, c0 = 40, 112; r1, c1 = 160, 192                    # the city: 3.6 km x 2.4 km of the valley floor
    owned[r0:r1, c0:c1] = True
    img = paint(sub.cov, sub.cls, 'B')
    img = dim_outside(img, owned)
    img = np.repeat(np.repeat(img, 2, axis=0), 2, axis=1)
    pil = Image.fromarray(np.clip(np.round(img), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
    offers = []
    depth = (16, 20, 24, 24, 20, 16)                         # blocks of 4 plots (120 m): 4, 5, 6, 6, 5, 4 deep
    for k in range(6):
        ra, rb = r0 + k * 20, r0 + (k + 1) * 20
        ca, cb = c1, c1 + depth[k]
        offers.append((ra, rb, ca, cb))
        # lit as the office lights the picked offer's ground
        if k == 2:
            lit = paint(sub.cov[ra:rb, ca:cb], sub.cls[ra:rb, ca:cb], 'B')
            lit = np.repeat(np.repeat(lit, 2, axis=0), 2, axis=1)
            a = np.asarray(pil).astype(np.float32); a[ra * 2:rb * 2, ca * 2:cb * 2] = lit
            pil = Image.fromarray(np.clip(np.round(a), 0, 255).astype(np.uint8)); d = ImageDraw.Draw(pil)
    for k, (ra, rb, ca, cb) in enumerate(offers):
        d.rectangle([ca * 2, ra * 2, cb * 2 - 1, rb * 2 - 1], outline=(246, 166, 201), width=3 if k == 2 else 1)
        d.text((ca * 2 + 5, ra * 2 + 4), str(k + 1), font=font(11, mono=True, bold=True), fill=(255, 255, 255), stroke_width=2, stroke_fill=(11, 17, 24))
    d.rectangle([c0 * 2, r0 * 2, c1 * 2, r1 * 2], outline=(255, 255, 255), width=1)
    img = np.asarray(pil).astype(np.float32)
    img = scale_bar(img, 2 * 1000 / PLOT_M, 1)
    to_png(img, out('mock-landoffice-map.png'))
    km2 = (PLOT_M / 1000) ** 2
    fig['offers_east'] = []
    for (ra, rb, ca, cb) in offers:
        cv, cl = sub.cov[ra:rb, ca:cb], sub.cls[ra:rb, ca:cb]
        land = cv <= DESERT
        fig['offers_east'].append(dict(blocks=((cb - ca) // 4, (rb - ra) // 4), km2=round(cv.size * km2, 4),
                                       flat=round(float(((cl == 0) & land).sum() * km2), 4),
                                       foothill=round(float(((cl == 1) & land).sum() * km2), 4),
                                       steep=round(float(((cl == 2) & land).sum() * km2), 4),
                                       vsteep=round(float(((cl == 3) & land).sum() * km2), 4),
                                       fresh=round(float((cv == FRESH).sum() * km2), 4), forest=round(float((cv == FOREST).sum() * km2), 4)))
    cv, cl = sub.cov[r0:r1, c0:c1], sub.cls[r0:r1, c0:c1]; land = cv <= DESERT
    fig['city_centre'] = dict(km2=round(cv.size * km2, 3), flat=round(float(((cl == 0) & land).sum() * km2), 3),
                              foothill=round(float(((cl == 1) & land).sum() * km2), 3), steep=round(float(((cl >= 2) & land).sum() * km2), 3),
                              fresh=round(float((cv == FRESH).sum() * km2), 3))


def main():
    fig = {}
    fig['landmass'] = {k: landmass_at(*p) for k, p in (('PEI', (46.24, -63.13)), ('Cape Jourimain', (46.1617, -63.8136)),
                                                       ('Halifax', (44.6488, -63.5752)), ('Dartmouth', (44.67, -63.56)),
                                                       ('Bahrain', (26.05, 50.55)), ('Qatar', (25.98, 51.03)), ('McNabs', (44.615, -63.525)))}
    fig['profiles'] = {
        'Confederation': profile((46.2520, -63.6860), (46.1560, -63.8210)),
        'Caribou-Wood Islands': profile((45.765, -62.685), (45.960, -62.747)),
        'Bahrain-Qatar': profile((26.05, 50.60), (25.98, 51.06)),
        'Macdonald': profile((44.6604, -63.5925), (44.6670, -63.5767)),
        'MacKay': profile((44.6735, -63.6200), (44.6820, -63.6038)),
        'Halifax-McNabs': profile((44.6250, -63.5650), (44.6150, -63.5320)),
    }
    only = sys.argv[1:]
    steps = [('medellin', medellin), ('halifax', halifax), ('everest', everest), ('confederation', confederation),
             ('far', far_views), ('globe', globe_swatch)]
    g_med = None
    for name, fn in steps:
        if only and name not in only: continue
        r = fn(fig)
        if name == 'medellin': g_med = r
    if not only or 'office' in only:
        land_office_map(fig, g_med)
    print(json.dumps(fig, indent=1, default=str))


if __name__ == '__main__':
    main()
