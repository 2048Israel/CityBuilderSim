# earth-2's landmass layer (the roads study's I7, docs/studies/spec-roads-and-ports.md 3.1, as the Earth 2 study
# widened it on 2026-10-12): a landmass is ground cut off by more water than a SHORT CROSSING spans - a highway or
# railway bridge of up to CROSS_M (34 plots, 1,020 m) over fresh water or shallow sea. Deep sea is never crossed.
#   LAND       a 15" cell whose earth-1 sea + lake levels come to 7 of 15 or less (EarthTerrain.COAST_BELOW's side),
#              unless a GRWL river wider than RIVER_CUT_M (1,020 m) covers it (each such segment cuts the cells within
#              half its width of its line, and every cell its line passes through).
#   CROSSABLE  a water cell that is fresh (its lake share at least its sea share, or a river's cut) or shallow sea (its
#              earth-2 shallow share, depth.shallow_share() at depth.SHALLOW_M, 8 of 15 or more). Every other water
#              cell is deep and joins nothing.
#   JOINED     land cells that touch along an edge; and two pieces of land when a crossable cell next to the one and a
#              crossable cell next to the other (or the other's own shore) are close enough: each crossable cell takes
#              the nearest land's label and its distance from that shore (a distance transform, the half cell to the
#              shore taken off), and two neighbouring cells of different labels join them when shore-to-cell +
#              cell-to-cell + cell-to-shore comes to CROSS_M or less. Done in a window round every tile (wide enough for
#              CROSS_M at its latitude), so across tile edges and the 180th meridian.
# Landmasses are numbered by area, largest first (1 = Afro-Eurasia); 0 is no land.
#   pass 1: label each tile's land (4-connected), keep its border labels and each label's area; join across edges.
#   pass 2: the crossings, tile by tile in a window (above).
#   pass 3: label each tile again (the same labels), write its blob: uniform tiles as -id in the index; else a
#           palette (uvarint count, uvarint ids ascending as deltas) and the cells' palette index, a byte each (a short
#           each, big-endian, when the palette passes 256), rows from the north; zlib.
# Writes <E2>/work/landmass/blobs.npz (index entries and blobs), table.tsv and stats.json; bake2.py puts them in
# earth-2.bin. Needs GEBCO 2026 (terrain.gebco(), for the shallow sea).
#   python3 -I landmass.py
import sys, os, time, json, math, zlib
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
from scipy import ndimage
from scipy.sparse import coo_matrix
from scipy.sparse.csgraph import connected_components
from scipy.ndimage import distance_transform_edt
from collections import OrderedDict
import terrain as T, depth as D
from earthread import Earth

CROSS_M = 1020.0             # the short crossing: a highway or rail bridge of up to 34 plots over fresh water or shallow sea
RIVER_CUT_M = CROSS_M        # a river wider than that cuts the land it runs through (it is crossable water like a lake)
SHALLOW_Q = 8                # a sea cell is crossable when this much of its sea, of 15, is shallow (depth.SHALLOW_M)
LAND_MAX_WATER = 7           # sea + lake levels (of 15) a land cell may hold
CELL_M = 2 * math.pi * T.R_M / 360 / 240     # a 15" cell's height, 463.3 m
OUT = os.path.join(T.WORK2, 'landmass')
TILES = 64800


def river_cut(E, t, lat0, lon0):
    """The tile's cells (240 x 240, bool) GRWL rivers wider than RIVER_CUT_M cover."""
    cut = np.zeros((240, 240), bool)
    runs = E.rivers(t)
    if not runs:
        return cut
    cosl = math.cos(math.radians(lat0 + 0.5))
    for a in runs:
        w = a[:, 2]
        for k in range(len(a) - 1):
            wk = 0.5 * (w[k] + w[k + 1])
            if wk <= RIVER_CUT_M:
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


def cut_of(E, t):
    """The tile's river cut (bool 240 x 240) or None; worked out once and kept."""
    if t not in _cuts:
        c = river_cut(E, t, t // 360 - 90, t % 360 - 180)
        _cuts[t] = np.packbits(c) if c.any() else None
    return None if _cuts[t] is None else np.unpackbits(_cuts[t])[:57600].reshape(240, 240).astype(bool)


def labels(E, t):
    """The tile's land labelled (4-connected), and its dry share."""
    land, dry = land_of(E, t)
    if land is None:
        return None, None
    cut = cut_of(E, t)
    if cut is not None:
        land &= ~cut
    lab, n = ndimage.label(land)                      # 4-connected (the default structure)
    return lab, dry


def crossable(E, G, t, land):
    """The tile's crossable water cells (bool 240 x 240): fresh water (lake share at least sea share, or a river's
    cut) and shallow sea (shallow share SHALLOW_Q of 15 or more); land is the tile's land (bool) or None."""
    se = E.layers['sea']['entries'][t]; le = E.layers['lake']['entries'][t]
    if se == 0 and le == 0 and land is not None and land.all():
        return np.zeros((240, 240), bool)
    sea = E.grid('sea', t).astype(np.int16); lake = E.grid('lake', t).astype(np.int16)
    water = ~land if land is not None else np.ones((240, 240), bool)
    cut = cut_of(E, t)
    fresh = water & ((lake >= sea) | (cut if cut is not None else False))
    out = fresh
    if sea.max() > 0:
        gp = T.gebco_tile(G, t // 360 - 90, t % 360 - 180, pad=1)
        if gp.max() >= -D.SHALLOW_M - 50:
            q = D.q4(D.shallow_share(gp, sea))
            out = out | (water & (sea > lake) & (q >= SHALLOW_Q))
    return out


def water_joins(E, offset):
    """Pass 2: the pairs of pass-1 labels (global, 1-based) a short crossing joins, from a window round every tile
    holding crossable water (CROSS_M, the module's header)."""
    G = T.gebco()
    cache = OrderedDict()

    def data(t):
        """(global labels int32 or None, crossable bool) of a tile, kept for the windows of its neighbours."""
        if t in cache:
            cache.move_to_end(t); return cache[t]
        lab, _ = labels(E, t)
        land = None if lab is None else lab > 0
        glab = None if lab is None or not land.any() else np.where(land, lab + offset[t], 0).astype(np.int32)
        cr = crossable(E, G, t, land)
        cache[t] = (glab, cr)
        if len(cache) > 1300:
            cache.popitem(last=False)
        return cache[t]

    A, B = [], []; tiles = 0; t0 = time.time()
    for t in range(TILES):
        b, l = divmod(t, 360)
        lat0 = b - 90
        glab, cr = data(t)
        if not cr.any():
            continue
        mr = int(math.ceil(CROSS_M / CELL_M)) + 1
        cpole = max(0.02, math.cos(math.radians(max(abs(lat0), abs(lat0 + 1)))))
        mc = min(240, int(math.ceil(CROSS_M / (CELL_M * cpole))) + 1)
        H, W = 240 + 2 * mr, 240 + 2 * mc
        wl = np.zeros((H, W), np.int32); wc = np.zeros((H, W), bool)
        for ddr, bb in ((-1, b + 1), (0, b), (1, b - 1)):          # north, this band, south (rows from the north)
            if bb < 0 or bb > 179:
                continue
            for ddc in (-1, 0, 1):
                tt = bb * 360 + (l + ddc) % 360
                gl, cc = data(tt)
                r0 = mr + ddr * 240; c0 = mc + ddc * 240
                rs, re_ = max(0, r0), min(H, r0 + 240); cs, ce = max(0, c0), min(W, c0 + 240)
                if rs >= re_ or cs >= ce:
                    continue
                if gl is not None:
                    wl[rs:re_, cs:ce] = gl[rs - r0:re_ - r0, cs - c0:ce - c0]
                wc[rs:re_, cs:ce] = cc[rs - r0:re_ - r0, cs - c0:ce - c0]
        land = wl > 0
        if not land.any():
            continue
        tiles += 1
        dy = CELL_M; dx = CELL_M * math.cos(math.radians(lat0 + 0.5))
        dist, ind = distance_transform_edt(~land, sampling=(dy, dx), return_indices=True)
        rr, cc_ = np.indices((H, W))
        n = np.maximum(np.abs(ind[0] - rr), np.abs(ind[1] - cc_))
        s = dist - 0.5 * dist / np.maximum(n, 1)                 # from the shore, not the land cell's centre
        lab = np.where(land, wl, np.where(wc, wl[ind[0], ind[1]], 0))
        inner = np.zeros((H, W), bool); inner[mr:mr + 240, mc:mc + 240] = True
        for step, ax in ((dx, 1), (dy, 0)):
            if ax == 1:
                la_, lb_ = lab[:, :-1], lab[:, 1:]; sa, sb = s[:, :-1], s[:, 1:]; na, nb = land[:, :-1], land[:, 1:]
                ia, ib = inner[:, :-1], inner[:, 1:]
            else:
                la_, lb_ = lab[:-1], lab[1:]; sa, sb = s[:-1], s[1:]; na, nb = land[:-1], land[1:]
                ia, ib = inner[:-1], inner[1:]
            ga = np.where(na, -0.5 * step, sa); gb = np.where(nb, -0.5 * step, sb)
            ok = (la_ > 0) & (lb_ > 0) & (la_ != lb_) & (ga + step + gb <= CROSS_M) & (ia | ib) & ~(na & nb)
            if ok.any():
                pr = np.unique(np.stack([np.minimum(la_[ok], lb_[ok]), np.maximum(la_[ok], lb_[ok])], 1), axis=0)
                A.append(pr[:, 0]); B.append(pr[:, 1])
        if t % 3600 == 3599:
            T.log('landmass', 'pass 2: band %d, %d tiles with crossings, %.0f s' % (b, tiles, time.time() - t0))
    if A:
        P = np.unique(np.stack([np.concatenate(A), np.concatenate(B)], 1), axis=0)
        return P[:, 0].astype(np.int64), P[:, 1].astype(np.int64), dict(tiles=tiles)
    return np.zeros(0, np.int64), np.zeros(0, np.int64), dict(tiles=tiles)


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
    T.log('landmass', 'pass 1 done: %d labels, %d edge joins, %.0f s' % (base, len(ea), time.time() - t0))
    wa, wb, st2 = water_joins(E, offset)
    T.log('landmass', 'pass 2 done: %d crossings joined over %d tiles, %.0f s' % (len(wa), st2['tiles'], time.time() - t0))
    ea = np.concatenate([ea, wa - 1]); eb = np.concatenate([eb, wb - 1])
    g = coo_matrix((np.ones(len(ea), np.int8), (ea, eb)), shape=(base, base))
    ncomp, comp = connected_components(g, directed=False)
    carea = np.bincount(comp, areas, minlength=ncomp); ccells = np.bincount(comp, cells, minlength=ncomp)
    order = np.argsort(-carea, kind='stable')
    rank = np.empty(ncomp, np.int64); rank[order] = np.arange(1, ncomp + 1)
    gid = rank[comp]                                   # local label (global index) -> landmass id
    T.log('landmass', 'joined: %d labels, %d landmasses, %.0f s' % (base, ncomp, time.time() - t0))
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
    # pass 3: blobs
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
            T.log('landmass', 'pass 3: band %d, %.2f MB, %.0f s' % (t // 360, sum(map(len, blobs.values())) / 1e6, time.time() - t0))
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
    st = dict(rule=dict(cross_m=CROSS_M, river_cut_m=RIVER_CUT_M, shallow_m=D.SHALLOW_M, shallow_q=SHALLOW_Q),
              water_joins=int(len(wa)), water_join_tiles=int(st2['tiles']),
              landmasses=int(ncomp), labels=int(base), blob_bytes=int(sum(map(len, blobs.values()))),
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
