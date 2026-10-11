# The E0 checks (spec-earth 8): each layer's size against spec-earth 1's figures, and the three windows
# (Rotterdam, Perth, Paris) of the shipped file against the prototype's baked files in scratch-earth/baked/.
# Reads earth-1.bin through earthread.py (so the encoding is checked too). Writes out/check-report.txt.
import sys, os, struct, json, math
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *
import numpy as np
from earthread import Earth
import rivers as RV

PROTO = SCRATCH + '/baked/'
WIN = dict(rot=('Rotterdam', 'N51E003', 'NM31'), per=('Perth', 'S33E114', 'SH50'), par=('Paris', 'N48E000', 'NM31'))
# spec-earth 1, compressed MB: (low, high) or a single measured figure
SPEC = {'sea': (4.8, 12.2), 'lake': (None, 10.7), 'lakes.small': (5.33, 5.33), 'rivers': (23, 35), 'landcover': (6.5, 9.1),
        'far.land+far.lake': (0.36, 0.36), 'geology+provinces': (0.05, 0.05), 'places': (0.10, 0.10)}
TOTAL = (45, 73)
out = []


def say(*a):
    s = ' '.join(str(x) for x in a); out.append(s); print(s, flush=True)


def q4(b):
    return (b.astype(np.int32) * 15 + 127) // 255


def mosaic(E, layer, lat_s, lat_n, lon_w, lon_e):
    """15" layer over whole tiles covering the box, north row first; returns (array, north lat, west lon)."""
    la0, la1 = math.floor(lat_s), math.floor(lat_n - 1e-9)
    lo0, lo1 = math.floor(lon_w), math.floor(lon_e - 1e-9)
    rows = []
    for la in range(la1, la0 - 1, -1):
        rows.append(np.hstack([E.grid(layer, tile_id(la, lo)) for lo in range(lo0, lo1 + 1)]))
    return np.vstack(rows), la1 + 1, lo0


def seg_dist(P, A, B):
    """Distance (m) from each point of P (n x 2: lat, lon) to the nearest segment A[k]-B[k], in local metres."""
    c = math.cos(math.radians(float(P[:, 0].mean())))
    sc = np.array([111320.0, 111320.0 * c])
    p = P * sc; a = A * sc; b = B * sc
    best = np.full(len(p), np.inf)
    for i in range(0, len(a), 2000):
        aa = a[i:i + 2000]; bb = b[i:i + 2000]; v = bb - aa; l2 = (v ** 2).sum(1); l2[l2 == 0] = 1e-12
        t = np.clip(((p[:, None, :] - aa[None]) * v[None]).sum(2) / l2[None], 0, 1)
        d = np.hypot(*(p[:, None, :] - aa[None] - t[..., None] * v[None]).transpose(2, 0, 1))
        best = np.minimum(best, d.min(1))
    return best


def proto_rivers(k):
    b = open(PROTO + k + '.rivers', 'rb').read(); o = 4; n, = struct.unpack('>i', b[:4]); res = []
    for _ in range(n):
        m, = struct.unpack('>i', b[o:o + 4]); o += 4
        a = np.frombuffer(b[o:o + m * 28], dtype=np.dtype([('la', '>f8'), ('lo', '>f8'), ('w', '>f8'), ('f', '>i4')]), count=m); o += m * 28
        res.append(np.column_stack([a['la'], a['lo'], a['w'], a['f'].astype(float)]))
    return res


def sizes(E, stats):
    say('== Layer sizes (compressed, as stored: index + blobs) against spec-earth 1 ==')
    L = E.layers
    by = {k: (L[k]['index_len'] + L[k]['data_len']) / 1e6 for k in L}
    groups = {'sea': ['sea'], 'lake': ['lake'], 'lakes.small': ['lakes.small'], 'rivers': ['rivers'], 'landcover': ['landcover'],
              'far.land+far.lake': ['far.land', 'far.lake'], 'geology+provinces': ['geology', 'provinces'], 'places': ['places']}
    for g, ks in groups.items():
        v = sum(by[k] for k in ks); lo, hi = SPEC[g]
        if lo is None: verdict = 'within (at most %.1f)' % hi if v <= hi else 'OVER %.1f' % hi
        elif lo == hi: verdict = 'spec measured %.2f; %+.0f%%' % (lo, 100 * (v / lo - 1))
        else: verdict = 'within %.1f-%.1f' % (lo, hi) if lo <= v <= hi else ('BELOW %.1f-%.1f' % (lo, hi) if v < lo else 'ABOVE %.1f-%.1f' % (lo, hi))
        say('  %-18s %7.2f MB   %s' % (g, v, verdict))
    tot = os.path.getsize(OUT + '/earth-1.bin') / 1e6
    say('  %-18s %7.2f MB   %s (header and meta %.3f MB)' % ('total file', tot, 'within %d-%d' % TOTAL if TOTAL[0] <= tot <= TOTAL[1] else 'OUTSIDE %d-%d' % TOTAL,
        by['meta'] + 0))


def lc_density(E, tiles):
    """Stored landcover bytes, and 2' cells with land (WorldCover pixels not water, from the pass), over 1-degree tiles."""
    byt = 0; land = 0
    for t in tiles:
        e = int(E.layers['landcover']['entries'][t]); byt += max(e, 0)
        lat0, lon0 = tile_of(t)
        a = (lat0 // 3) * 3; b = (lon0 // 3) * 3
        name = '%s%02d%s%03d' % ('N' if a >= 0 else 'S', abs(a), 'E' if b >= 0 else 'W', abs(b))
        f = WORK + '/wc/%s.npz' % name
        if os.path.exists(f):
            lc = np.load(f)['lc']; rr = (a + 3 - (lat0 + 1)) * 30; cc = (lon0 - b) * 30
            land += int((lc[3, rr:rr + 30, cc:cc + 30] < 10000).sum())
    return byt, land


def window(E, key):
    name, wc, grwl = WIN[key]
    h = dict(l.split() for l in open(PROTO + key + '.hdr'))
    lat_n, lon_w, R, C = float(h['lat_n']), float(h['lon_w']), int(h['rows']), int(h['cols'])
    lat_s = lat_n - R / 240; lon_e = lon_w + C / 240
    say('== %s: window %d x %d cells of 15" (%.4f..%.4f N, %.4f..%.4f E) ==' % (name, R, C, lat_s, lat_n, lon_w, lon_e))
    P = {k: np.fromfile(PROTO + key + '.' + k, np.uint8).reshape(R, C) for k in ('sea', 'fresh', 'forest', 'bare', 'ice')}
    z = np.load(WORK + '/wc/%s.npz' % wc)
    top = (1 if wc[0] == 'N' else -1) * int(wc[1:3]) + 3; left = (1 if wc[3] == 'E' else -1) * int(wc[4:7])
    r0 = int(round((top - lat_n) * 240)); c0 = int(round((lon_w - left) * 240))
    inner = np.zeros((R, C), bool); inner[1:-1, 1:-1] = True
    # -- sea, before the lake override: the pipeline's bytes against bake.py's
    ours = z['sea'][r0:r0 + R, c0:c0 + C]
    eq = ours == P['sea']
    say('  sea bytes (x255, before the lake override): equal in %d of %d cells (%.4f%%); inside the border ring %d of %d'
        % (eq.sum(), eq.size, 100 * eq.mean(), eq[inner].sum(), inner.sum()))
    bad = ~eq & inner
    if bad.any():
        full = z['full'][r0:r0 + R, c0:c0 + C]
        say('    inner cells that differ: %d, of which read at 10 m %d; largest difference %d/255; at 4 bits they differ in %d'
            % (bad.sum(), (bad & full).sum(), int(np.abs(ours.astype(int) - P['sea'])[bad].max()), int((q4(ours) != q4(P['sea']))[bad].sum())))
    ring = ~eq & ~inner
    if ring.any():
        say('    border-ring cells that differ: %d (bake.py grew GEBCO\'s ocean inside its window only; the pipeline sees the neighbours outside it)' % ring.sum())
    # -- the shipped 4-bit sea and lake
    sea, la_n, lo_w = mosaic(E, 'sea', lat_s, lat_n, lon_w, lon_e)
    lake, _, _ = mosaic(E, 'lake', lat_s, lat_n, lon_w, lon_e)
    i0 = int(round((la_n - lat_n) * 240)); j0 = int(round((lon_w - lo_w) * 240))
    sea = sea[i0:i0 + R, j0:j0 + C].astype(int); lake = lake[i0:i0 + R, j0:j0 + C].astype(int)
    pq = q4(P['sea'])
    eq4 = sea == pq
    say('  sea, shipped 4 bits against bake.py quantised: equal in %d of %d cells (%.4f%%)' % (eq4.sum(), eq4.size, 100 * eq4.mean()))
    d = ~eq4
    if d.any():
        pre = q4(ours)
        why_lake = d & (sea < pre)                     # the shipped share fell below the pipeline's own: the lake override
        why_ring = d & ~inner & ~why_lake; other = d & ~why_lake & ~why_ring
        say('    differ: %d where a HydroLAKES lake of 10 km2 or more overrides GEBCO\'s ocean (sea becomes lake, spec-earth 4), '
            '%d on the border ring, %d other' % (why_lake.sum(), why_ring.sum(), other.sum()))
        say('    the pipeline\'s sea before the override, at 4 bits: equal in %d of %d cells; %d of %d off the border ring'
            % ((pre == pq).sum(), pre.size, ((pre == pq) & inner).sum(), inner.sum()))
    say('  sea share of the window: shipped %.3f%%, bake.py %.3f%%' % (100 * sea.mean() / 15, 100 * P['sea'].mean() / 255))
    say('  lakes of 10 km2 or more (HydroLAKES): %.3f%% of the window; bake.py\'s fresh (WorldCover inland water in bodies >= 10 ha, '
        'not shipped) %.3f%%' % (100 * lake.mean() / 15, 100 * P['fresh'].mean() / 255))
    # -- land cover at 2'
    rr0, rr1 = -(-r0 // 8), (r0 + R) // 8; cc0, cc1 = -(-c0 // 8), (c0 + C) // 8
    F, B, I, Wt = [a[rr0:rr1, cc0:cc1].astype(float) for a in z['lc']]
    pf = P['forest'][rr0 * 8 - r0:rr1 * 8 - r0, cc0 * 8 - c0:cc1 * 8 - c0].reshape(rr1 - rr0, 8, cc1 - cc0, 8).mean(axis=(1, 3)) / 255
    pb = P['bare'][rr0 * 8 - r0:rr1 * 8 - r0, cc0 * 8 - c0:cc1 * 8 - c0].reshape(rr1 - rr0, 8, cc1 - cc0, 8).mean(axis=(1, 3)) / 255
    pi = P['ice'][rr0 * 8 - r0:rr1 * 8 - r0, cc0 * 8 - c0:cc1 * 8 - c0].reshape(rr1 - rr0, 8, cc1 - cc0, 8).mean(axis=(1, 3)) / 255
    of, ob = F / 10000, B / 10000
    n2 = of.size
    say('  land cover, %d whole 2\' cells: forest share of the cell, pipeline (40 m overview) against bake.py (10 m), mean |difference| %.2f points, '
        'largest %.2f; window total %.2f%% against %.2f%%' % (n2, 100 * np.abs(of - pf).mean(), 100 * np.abs(of - pf).max(), 100 * of.mean(), 100 * pf.mean()))
    landf = 1 - Wt / 10000
    lv_ship = np.zeros_like(of, int); lc_tiles = {}
    for a in range(rr1 - rr0):
        for b in range(cc1 - cc0):
            la = top - (rr0 + a + 0.5) / 30; lo = left + (cc0 + b + 0.5) / 30
            t = tile_id(math.floor(la), math.floor(lo))
            if t not in lc_tiles: lc_tiles[t] = E.grid('landcover', t)
            g = lc_tiles[t]
            lv_ship[a, b] = g[int((math.floor(la) + 1 - la) * 30), int((lo - math.floor(lo)) * 30)]
    m = landf > 0
    lv_proto = np.floor(np.where(m, pf / np.maximum(landf, 1e-9), 0) * 15 + 0.5).clip(0, 15)
    lv_pipe = np.floor(np.where(m, F / np.maximum(10000 - Wt, 1), 0) * 15 + 0.5)
    fl = lv_ship & 15
    say('  forest level shipped (16 levels of the land): equals the pipeline\'s own %s; equals bake.py\'s forest (over the same land) '
        'in %.1f%% of cells with land, off by one level in %.1f%%, more in %.1f%%'
        % ('in every cell' if np.array_equal(fl[m], lv_pipe[m]) else 'NOT in every cell',
           100 * (fl[m] == lv_proto[m]).mean(), 100 * (np.abs(fl[m] - lv_proto[m]) == 1).mean(), 100 * (np.abs(fl[m] - lv_proto[m]) > 1).mean()))
    pb_land = np.where(m, pb / np.maximum(landf, 1e-9), 0)
    bit = (lv_ship & 0x10) > 0
    say('  bare share of the cell: mean |difference| %.2f points (window %.3f%% against %.3f%%); beach bit (bare >= 2%% of land) '
        'agrees with bake.py\'s bare in %.1f%% of cells; ice: shipped bit set in %d cells, bake.py ice share %.3f%%'
        % (100 * np.abs(ob - pb).mean(), 100 * ob.mean(), 100 * pb.mean(), 100 * (bit[m] == (pb_land[m] >= 0.02)).mean(),
           int(((lv_ship & 0x40) > 0).sum()), 100 * pi.mean()))
    # -- rivers
    pr = proto_rivers(key)
    d = RV.read_dbf(open(DATA + '/grwlv/%s.dbf' % grwl, 'rb').read())
    port = RV.simplify(RV.lines(d, (lat_s - 0.02, lat_n + 0.02, lon_w - 0.03, lon_e + 0.03)), 15)
    k_ = lambda a: (a[0, 0], a[0, 1], len(a))
    same = len(port) == len(pr) and all(np.array_equal(a, b) for a, b in zip(sorted(port, key=k_), sorted(pr, key=k_)))
    port30 = RV.simplify(RV.lines(d, (lat_s - 0.02, lat_n + 0.02, lon_w - 0.03, lon_e + 0.03)), 30)
    say('  rivers: the port at 15 m reproduces bake.py\'s file %s (%d lines, %d vertices); at the shipped 30 m it keeps %d vertices'
        % ('exactly' if same else 'NOT exactly', len(pr), sum(map(len, pr)), sum(map(len, port30))))
    runs = []
    for la in range(math.floor(lat_s), math.floor(lat_n) + 1):
        for lo in range(math.floor(lon_w), math.floor(lon_e) + 1):
            runs += E.rivers(tile_id(la, lo))
    if runs:
        A = np.concatenate([r[:-1, :2] for r in runs]); Bv = np.concatenate([r[1:, :2] for r in runs])
        allv = np.concatenate([r[:, :2] for r in runs])
        inside = (allv[:, 0] >= lat_s) & (allv[:, 0] <= lat_n) & (allv[:, 1] >= lon_w) & (allv[:, 1] <= lon_e)
        PV = np.concatenate([p[:, :2] for p in pr])
        d1 = seg_dist(PV, A, Bv)
        PA = np.concatenate([p[:-1, :2] for p in pr]); PB = np.concatenate([p[1:, :2] for p in pr])
        import shapefile
        gb = shapefile.Reader(DATA + '/grwlv/%s.shp' % grwl).bbox      # bake.py read this one GRWL tile
        within = (allv[:, 0] >= gb[1]) & (allv[:, 0] <= gb[3]) & (allv[:, 1] >= gb[0]) & (allv[:, 1] <= gb[2])
        sv = allv[inside & within]
        d2 = seg_dist(sv, PA, PB) if len(sv) else np.zeros(1)
        say('    shipped (30 m, quantised to 1e-5 degree): %d vertices inside the window; each of bake.py\'s %d vertices lies within %.1f m '
            '(mean %.1f) of a shipped line; each shipped vertex in %s\'s area within %.1f m (mean %.1f) of bake.py\'s lines'
            % (inside.sum(), len(PV), d1.max(), d1.mean(), grwl, d2.max(), d2.mean()))
        if (inside & ~within).any():
            say('    %d shipped vertices in the window come from the next GRWL tile, which bake.py did not read (%s spans %.0f to %.0f N)'
                % ((inside & ~within).sum(), grwl, gb[1], gb[3]))
    else:
        say('    no shipped rivers in the window')
    sl = []
    for la in range(math.floor(lat_s), math.floor(lat_n) + 1):
        for lo in range(math.floor(lon_w), math.floor(lon_e) + 1):
            s = E.small_lakes(tile_id(la, lo))
            sl.append(s[(s[:, 0] >= lat_s) & (s[:, 0] <= lat_n) & (s[:, 1] >= lon_w) & (s[:, 1] <= lon_e)])
    sl = np.concatenate(sl)
    say('  small lakes (under 10 km2) in the window: %d, %.1f km2' % (len(sl), sl[:, 2].sum() if len(sl) else 0))


def main():
    E = Earth(OUT + '/earth-1.bin')
    stats = json.load(open(OUT + '/bake-stats.json'))
    say('earth-1.bin %d bytes, sha256 %s' % (os.path.getsize(OUT + '/earth-1.bin'), open(OUT + '/earth-1.sha256').read().split()[0]))
    sizes(E, stats)
    tw = [tile_id(a, b) for a, b in ((51, 3), (51, 4), (51, 5), (52, 3), (52, 4), (52, 5), (48, 1), (48, 2), (49, 1), (49, 2),
                                      (-33, 115), (-33, 116), (-32, 115), (-32, 116))]
    b1, l1 = lc_density(E, tw)
    import glob
    wcl = sum(int((np.load(f)['lc'][3] < 10000).sum()) for f in glob.glob(WORK + '/wc/*.npz'))
    say('  landcover bytes a 2\' cell with land: %.2f over the 14 tiles round the three windows (spec-earth 1 derived 0.47-0.66 '
        'from the windows); %.2f over the globe (%d such cells under WorldCover)' % (b1 / max(1, l1), E.layers['landcover']['data_len'] / max(1, wcl), wcl))
    for k in ('rot', 'per', 'par'):
        window(E, k)
    g = np.frombuffer(E.raw('geology'), '>u2').reshape(720, 1440)
    pg = np.load(PROTO + 'provinces025.npy')
    say('== Geology: the shipped 0.25 degree province index %s the prototype\'s provinces025.npy ==' % ('equals' if np.array_equal(g, pg) else 'DIFFERS FROM'))
    with open(OUT + '/check-report.txt', 'w') as f:
        f.write('\n'.join(out) + '\n')


if __name__ == '__main__':
    main()
