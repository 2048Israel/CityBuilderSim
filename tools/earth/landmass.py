# earth-2's landmass layer (the roads study's I7, docs/studies/spec-roads-and-ports.md 3.1): every 15" land cell's
# connected component, after taking out the sea and HydroLAKES lakes (earth-1's sea and lake shares, a cell land when
# they come to under half: sea + lake <= 7 of 15, EarthTerrain.COAST_BELOW's side) and GRWL rivers 420 m wide or more
# (earth-1's rivers layer, each such segment cutting the cells within half its width of its line, and every cell its
# line passes through). Cells join their four edge neighbours (not corners), across tile edges and the 180th meridian.
# Landmasses are numbered by area, largest first (1 = Afro-Eurasia); 0 is no land.
#   pass 1: label each tile, keep its border labels and each label's area; join the labels across tile edges.
#   pass 2: label each tile again (the same labels), write its blob: uniform tiles as -id in the index; else a
#           palette (uvarint count, uvarint ids ascending) and the cells' palette index, a byte each (a short each,
#           big-endian, when the palette passes 255), rows from the north; zlib.
# Writes <E2>/work/landmass/blobs.npz (index entries and blobs) and table.tsv; bake2.py puts them in earth-2.bin.
#   python3 -I landmass.py
import sys, os, time, json, math, zlib
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
from scipy import ndimage
from scipy.sparse import coo_matrix
from scipy.sparse.csgraph import connected_components
import terrain as T
from earthread import Earth

RIVER_CUT_M = 420.0          # the highway bridge's reach over fresh water (14 plots; the roads study, 3.1)
LAND_MAX_WATER = 7           # sea + lake levels (of 15) a land cell may hold
CELL_M = 2 * math.pi * T.R_M / 360 / 240     # a 15" cell's height, 463.3 m
OUT = os.path.join(T.WORK2, 'landmass')
TILES = 64800


def river_cut(E, t, lat0, lon0):
    """The tile's cells (240 x 240, bool) GRWL rivers RIVER_CUT_M wide or more cover."""
    cut = np.zeros((240, 240), bool)
    runs = E.rivers(t)
    if not runs:
        return cut
    cosl = math.cos(math.radians(lat0 + 0.5))
    for a in runs:
        w = a[:, 2]
        for k in range(len(a) - 1):
            wk = 0.5 * (w[k] + w[k + 1])
            if wk < RIVER_CUT_M:
                continue
            p0, p1 = a[k, :2], a[k + 1, :2]
            seg_m = math.hypot((p1[0] - p0[0]) * 111195, (p1[1] - p0[1]) * 111195 * cosl)
            n = max(2, int(seg_m / 100) + 2)
            s = np.linspace(0, 1, n)
            la = p0[0] + (p1[0] - p0[0]) * s; lo = p0[1] + (p1[1] - p0[1]) * s
            r = (lat0 + 1 - la) * 240; c = (lo - lon0) * 240         # in cells, fractional
            hr = wk / 2 / CELL_M; hc = wk / 2 / (CELL_M * cosl)          # half-width in cells
            for dr in range(-int(hr) - 1, int(hr) + 2):
                for dc in range(-int(hc) - 1, int(hc) + 2):
                    rr = np.floor(r).astype(int) + dr; cc = np.floor(c).astype(int) + dc
                    # the cell centre's distance to the sample, in metres; the cell the line passes through always
                    dy = (rr + 0.5 - r) * CELL_M; dx = (cc + 0.5 - c) * CELL_M * cosl
                    hit = ((dr == 0) & (dc == 0)) | (dx * dx + dy * dy <= (wk / 2) ** 2)
                    ok = hit & (rr >= 0) & (rr < 240) & (cc >= 0) & (cc < 240)
                    cut[rr[ok], cc[ok]] = True
    return cut


def land_of(E, t):
    """The tile's land cells (bool 240 x 240) and dry share (float), or None when it has none."""
    se = E.layers['sea']['entries'][t]; le = E.layers['lake']['entries'][t]
    if se <= 0 and le <= 0 and (-se) + (-le) > LAND_MAX_WATER:
        return None, None
    s = E.grid('sea', t).astype(np.int16); l = E.grid('lake', t).astype(np.int16)
    land = (s + l) <= LAND_MAX_WATER
    if not land.any():
        return None, None
    return land, 1 - (s + l) / 15.0


_cuts = {}


def labels(E, t):
    """The tile's land labelled (4-connected), and its dry share; the river cut is worked out once and kept."""
    lat0, lon0 = t // 360 - 90, t % 360 - 180
    land, dry = land_of(E, t)
    if land is None:
        return None, None
    if t not in _cuts:
        c = river_cut(E, t, lat0, lon0)
        _cuts[t] = np.packbits(c) if c.any() else None
    if _cuts[t] is not None:
        land &= ~np.unpackbits(_cuts[t])[:57600].reshape(240, 240).astype(bool)
    lab, n = ndimage.label(land)                      # 4-connected (the default structure)
    return lab, dry


def main():
    os.makedirs(OUT, exist_ok=True)
    E = Earth(T.EARTH1)
    t0 = time.time()
    offset = np.zeros(TILES + 1, np.int64)
    borders = {}                                      # t -> (north row, south row, west col, east col) global labels
    areas = []; cells = []
    base = 0
    for t in range(TILES):
        offset[t] = base
        lab, dry = labels(E, t)
        if lab is None:
            continue
        n = int(lab.max())
        if n == 0:
            continue
        lat0 = t // 360 - 90
        A = (np.cos(np.radians(lat0 + 1 - (np.arange(240) + 0.5) / 240)) * (CELL_M ** 2) / 1e6)[:, None]
        areas.append(np.bincount(lab.ravel(), (dry * A).ravel(), minlength=n + 1)[1:])
        cells.append(np.bincount(lab.ravel(), minlength=n + 1)[1:])
        g = np.where(lab > 0, lab + base, 0).astype(np.int64)
        borders[t] = (g[0].copy(), g[-1].copy(), g[:, 0].copy(), g[:, -1].copy())
        base += n
        if t % 3600 == 3599:
            T.log('landmass', 'pass 1: band %d, %d labels, %.0f s' % (t // 360, base, time.time() - t0))
    offset[TILES] = base
    areas = np.concatenate(areas); cells = np.concatenate(cells)
    # join across tile edges: east (wrapping at 180) and north
    ea, eb = [], []
    for t, (nr, sr, wc, ec) in borders.items():
        b, l = divmod(t, 360)
        e = b * 360 + (l + 1) % 360
        if e in borders:
            x, y = ec, borders[e][2]; m = (x > 0) & (y > 0); ea.append(x[m]); eb.append(y[m])
        if b < 179:
            nt = t + 360
            if nt in borders:
                x, y = nr, borders[nt][1]; m = (x > 0) & (y > 0); ea.append(x[m]); eb.append(y[m])
    ea = np.concatenate(ea) - 1; eb = np.concatenate(eb) - 1
    g = coo_matrix((np.ones(len(ea), np.int8), (ea, eb)), shape=(base, base))
    ncomp, comp = connected_components(g, directed=False)
    carea = np.bincount(comp, areas, minlength=ncomp); ccells = np.bincount(comp, cells, minlength=ncomp)
    order = np.argsort(-carea, kind='stable')
    rank = np.empty(ncomp, np.int64); rank[order] = np.arange(1, ncomp + 1)
    gid = rank[comp]                                   # local label (global index) -> landmass id
    T.log('landmass', 'pass 1 done: %d labels, %d landmasses, %.0f s' % (base, ncomp, time.time() - t0))
    # places, for names: the most populous place in each landmass
    places = []
    for line in E.raw('places').decode('utf-8').splitlines():
        p = line.split('\t'); places.append((p[0], float(p[3]), float(p[4]), int(p[5])))
    pt = {}
    for name, la, lo, pop in places:
        t = (int(math.floor(la)) + 90) * 360 + int(math.floor(lo)) + 180
        r = min(239, int((math.floor(la) + 1 - la) * 240)); c = min(239, int((lo - math.floor(lo)) * 240))
        pt.setdefault(t, []).append((name, r, c, pop))
    best = {}
    # pass 2: blobs
    entries = np.zeros(TILES, np.int64); blobs = {}
    for t in range(TILES):
        lab, _ = labels(E, t)
        if lab is None or lab.max() == 0:
            entries[t] = 0; continue
        ids = np.where(lab > 0, gid[np.maximum(lab, 1) - 1 + offset[t]], 0)
        for name, r, c, pop in pt.get(t, []):
            k = int(ids[r, c])
            if k == 0:                                 # a coastal place on a sea cell: its nearest land cell
                rr, cc = np.nonzero(ids)
                j = np.argmin((rr - r) ** 2 + (cc - c) ** 2); k = int(ids[rr[j], cc[j]])
            if k not in best or best[k][1] < pop:
                best[k] = (name, pop)
        u = np.unique(ids)
        if len(u) == 1:
            entries[t] = -int(u[0]); continue
        buf = bytearray()
        T_uv(buf, len(u))
        prev = 0
        for v in u:
            T_uv(buf, int(v) - prev); prev = int(v)     # ascending: deltas
        idx = np.searchsorted(u, ids)
        buf += idx.astype(np.uint8 if len(u) <= 256 else '>u2').tobytes()
        z = zlib.compress(bytes(buf), 9)
        entries[t] = len(z); blobs[t] = z
        if t % 3600 == 3599:
            T.log('landmass', 'pass 2: band %d, %.2f MB, %.0f s' % (t // 360, sum(map(len, blobs.values())) / 1e6, time.time() - t0))
    srt_area = carea[order]; srt_cells = ccells[order]
    lines = []
    for i in range(ncomp):
        k = i + 1
        nm = best.get(k, ('', 0))[0]
        lines.append('%.2f\t%d\t%s' % (srt_area[i], srt_cells[i], nm))
    table = ('\n'.join(lines) + '\n').encode('utf-8')
    np.savez(os.path.join(OUT, 'blobs.npz.tmp.npz'), entries=entries, tiles=np.array(sorted(blobs), np.int64),
             data=np.frombuffer(b''.join(blobs[t] for t in sorted(blobs)), np.uint8),
             lens=np.array([len(blobs[t]) for t in sorted(blobs)], np.int64))
    os.replace(os.path.join(OUT, 'blobs.npz.tmp.npz'), os.path.join(OUT, 'blobs.npz'))
    with open(os.path.join(OUT, 'table.tsv'), 'wb') as f:
        f.write(table)
    st = dict(landmasses=int(ncomp), labels=int(base), blob_bytes=int(sum(map(len, blobs.values()))),
              blobs=len(blobs), uniform_land_tiles=int((entries < 0).sum()),
              table_bytes=len(table), table_zlib=len(zlib.compress(table, 9)),
              at_least_km2={str(a): int((srt_area >= a).sum()) for a in (0.01, 0.1, 1, 10, 100, 1000, 10000, 1e6)},
              largest=[(lines[i]) for i in range(12)], seconds=round(time.time() - t0))
    with open(os.path.join(OUT, 'stats.json'), 'w') as f:
        json.dump(st, f, indent=1)
    T.log('landmass', json.dumps(st))


def T_uv(buf, n):
    while True:
        b = n & 0x7f; n >>= 7
        if n: buf.append(b | 0x80)
        else:
            buf.append(b); return


if __name__ == '__main__':
    main()
