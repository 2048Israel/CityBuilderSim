# The Earth 2 study's site measurements (sites.py's per-cell histograms): each site's share of land in each slope
# class from each source, at a set of thresholds; and the coarse estimate from GEBCO's 15" slope, calibrated on the
# other sites' tiles (leave one site out), against the 30 m truth.
#   python3 -I analyse_sites.py [foothill steep very_steep]      (percent grade; default terrain.THRESHOLDS)
import sys, os, math, json
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
import terrain as T
from earthread import Earth
from sites import SITES, box, tiles

E = Earth(T.EARTH1)
SRC = ['nas30', 'nas90', 'cop30', 'cop90']
TRUTH = os.environ.get('TRUTH', 'nas90')       # what the coarse estimate is calibrated on and judged against


def load(lat0, lon0):
    return np.load(os.path.join(T.WORK2, 'sites', T.tile_name(lat0, lon0, 'srtm') + '.npz'))


def land_share(lat0, lon0):
    t = (lat0 + 90) * 360 + lon0 + 180
    return 1 - (E.grid('sea', t).astype(np.float32) + E.grid('lake', t)) / 15


def ge(h, th):
    """Per cell share of pixels at or above th (percent grade); th must be a bin edge."""
    k = int(np.searchsorted(T.BINS, th))
    assert abs(T.BINS[k] - th) < 1e-6, th
    n = h.sum(axis=2).astype(np.float64)
    return np.where(n > 0, h[:, :, k:].sum(axis=2) / np.maximum(n, 1), 0)


def cell_classes(h, L, th):
    """Per cell the share of LAND at or above each threshold: the pixels' counts over the cell's land
    (water, flattened in every source, counts as flat), capped at 1."""
    out = []
    for x in th:
        s = ge(h, x)
        out.append(np.where(L > 0.02, np.minimum(1, s / np.maximum(L, 0.02)), 0))
    return np.stack(out)                                   # (3, 240, 240): >= foothill, >= steep, >= very steep


def in_box(site, lat0, lon0):
    s, n, w, e = box(site)
    la = lat0 + 1 - (np.arange(240) + 0.5) / 240
    lo = lon0 + (np.arange(240) + 0.5) / 240
    return ((la >= s) & (la < n))[:, None] & ((lo >= w) & (lo < e))[None, :]


def site_shares(site, th, src):
    """The site box's land shares (flat, foothill, steep, very steep) from one source."""
    acc = np.zeros(3); W = 0.0
    for la, lo in tiles(site):
        z = load(la, lo); L = land_share(la, lo); m = in_box(site, la, lo)
        c = cell_classes(z[src], L, th)
        w = np.where(m, L * np.cos(np.radians(la + 0.5)), 0)
        acc += (c * w).sum(axis=(1, 2)); W += w.sum()
    g = acc / W
    return np.array([1 - g[0], g[0] - g[1], g[1] - g[2], g[2]])


# ---------------------------------------------------------------- the coarse estimate from GEBCO 15"
GB = np.array([0, 1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 18, 22, 26, 30, 35, 40, 50, 60, 80, 1e9])   # GEBCO 15" slope bins, percent


def gebco_features(z):
    return np.clip(np.searchsorted(GB, z['gslope'], side='right') - 1, 0, len(GB) - 2)


def calibrate(train, th, truth='nas30'):
    """Mean land share at or above each threshold, per GEBCO slope bin, over the training tiles' land cells."""
    S = np.zeros((len(GB) - 1, 3)); N = np.zeros(len(GB) - 1)
    for la, lo in train:
        z = load(la, lo); L = land_share(la, lo)
        c = cell_classes(z[truth], L, th)
        b = gebco_features(z)
        w = (L > 0.5).astype(np.float64)
        for k in range(3):
            S[:, k] += np.bincount(b.ravel(), (c[k] * w).ravel(), minlength=len(GB) - 1)
        N += np.bincount(b.ravel(), w.ravel(), minlength=len(GB) - 1)
    return S / np.maximum(N, 1)[:, None], N


def coarse_shares(site, th, table):
    acc = np.zeros(3); W = 0.0
    for la, lo in tiles(site):
        z = load(la, lo); L = land_share(la, lo); m = in_box(site, la, lo)
        b = gebco_features(z)
        c = table[b].transpose(2, 0, 1)
        w = np.where(m, L * np.cos(np.radians(la + 0.5)), 0)
        acc += (c * w).sum(axis=(1, 2)); W += w.sum()
    g = acc / W
    return np.array([1 - g[0], g[0] - g[1], g[1] - g[2], g[2]])


def main():
    th = tuple(float(x) for x in sys.argv[1:4]) if len(sys.argv) >= 4 else T.THRESHOLDS
    alltiles = sorted({t for s in SITES for t in tiles(s)})
    rows = {}
    print('thresholds (percent grade): foothill %g, steep %g, very steep %g' % th)
    print('%-12s %-6s %6s %6s %6s %6s' % ('site', 'source', 'flat', 'foot', 'steep', 'vsteep'))
    for site in SITES:
        r = {}
        for src in SRC:
            r[src] = site_shares(site, th, src)
        own = set(tiles(site))
        table, _ = calibrate([t for t in alltiles if t not in own], th, TRUTH)
        r['gebco15'] = coarse_shares(site, th, table)
        for src in SRC + ['gebco15']:
            print('%-12s %-6s %s' % (site, src, ' '.join('%6.1f' % (100 * x) for x in r[src])))
        rows[site] = {k: [round(float(x), 4) for x in v] for k, v in r.items()}
        print()
    out = os.path.join(T.WORK2, 'sites_%g_%g_%g_%s.json' % (th + (TRUTH,)))
    json.dump(dict(thresholds=th, sites=rows), open(out, 'w'), indent=1)
    # errors of each source against nas30 (the 30 m truth), in points of share, per class
    print('mean absolute error against %s, points, by class (flat, foot, steep, vsteep):' % TRUTH)
    for src in [x for x in SRC + ['gebco15'] if x != TRUTH]:
        e = np.array([np.abs(np.array(rows[s][src]) - rows[s][TRUTH]) for s in SITES]) * 100
        print('  %-7s %s   worst %s' % (src, ' '.join('%5.1f' % x for x in e.mean(0)),
              ' '.join('%5.1f' % x for x in e.max(0))))


if __name__ == '__main__' and not os.environ.get('RES'):
    main()


# ---------------------------------------------------------------- what a coarser slope layer loses
def resolution(src='nas90', th=T.THRESHOLDS, ks=(1, 2, 4, 8)):
    """For each site box: the land-weighted mean absolute difference between each 15" cell's own share at or above
    foothill and steep (the truth at 15") and the share its coarser cell (k x k 15" cells, land-weighted) gives it;
    and Medellin's valley floor (15" cells at least 60% flat) under each."""
    res = {}
    for site in SITES:
        r = {}
        for k in ks:
            err = np.zeros(2); W = 0.0; vf = []; 
            for la, lo in tiles(site):
                z = load(la, lo); L = land_share(la, lo); m = in_box(site, la, lo)
                c = cell_classes(z[src], L, th)[:2]
                w = L * m
                if k > 1:
                    n = 240 // k
                    cw = L.reshape(n, k, n, k)
                    agg = (c.reshape(2, n, k, n, k) * cw[None]).sum(axis=(2, 4)) / np.maximum(cw.sum(axis=(1, 3)), 1e-9)[None]
                    cc = np.repeat(np.repeat(agg, k, axis=1), k, axis=2)
                else:
                    cc = c
                err += (np.abs(cc - c) * w[None]).sum(axis=(1, 2)); W += w.sum()
                floor = m & (L > 0.5) & (c[0] <= 0.4)
                vf.append((1 - cc[0])[floor])
            vf = np.concatenate(vf)
            r[k] = dict(err_foothill=round(float(err[0] / W), 3), err_steep=round(float(err[1] / W), 3),
                        flat_cells=int(len(vf)), flat_share_kept=round(float(vf.mean()) if len(vf) else 0, 3))
        res[site] = r
    return res


if __name__ == '__main__' and os.environ.get('RES'):
    R = resolution()
    names = {1: '15"', 2: '30"', 4: "1'", 8: "2'"}
    print('%-11s' % 'site' + ''.join('%22s' % names[k] for k in (1, 2, 4, 8)))
    print('%-11s' % '' + ''.join('%22s' % 'err>=F err>=S flat' for k in (1, 2, 4, 8)))
    for s, r in R.items():
        print('%-11s' % s + ''.join('   %5.3f %5.3f %6.2f' % (r[k]['err_foothill'], r[k]['err_steep'], r[k]['flat_share_kept']) for k in (1, 2, 4, 8)))
    json.dump(R, open(os.path.join(T.WORK2, 'resolution.json'), 'w'), indent=1)
