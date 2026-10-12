# How good the slope estimate north of 60 N is likely to be: the GEBCO-15"-slope table calibrated on NASADEM's
# tiles at 45-55 degrees north and south, tested against NASADEM's own measure on the tiles at 55-60 N (the land
# nearest in kind to what lies past 60 N). Per tile and overall: the land shares flat / foothill / steep / very steep.
#   python3 -I estimate_error.py       (after slope_tiles.py)
import sys, os, json
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
import numpy as np
import terrain as T
from earthread import Earth
import bake2 as B

E = Earth(T.EARTH1); G = T.gebco()
SL = os.path.join(T.WORK2, 'slope')


def tiles():
    out = []
    for n in os.listdir(SL):
        if n.endswith('.npz'):
            s = n[:-4]; la = int(s[1:3]) * (1 if s[0] == 'N' else -1); lo = int(s[4:7]) * (1 if s[3] == 'E' else -1)
            out.append((la, lo))
    return out


def measure(la, lo):
    t = (la + 90) * 360 + lo + 180
    z = np.load(os.path.join(SL, T.tile_name(la, lo, 'srtm') + '.npz'))
    _, L = B.land_of(E, t)
    cs = B.ge_counts(z['h'], T.THRESHOLDS)
    return np.stack([np.where(L > 0, np.minimum(1, c / (B.PER_CELL * np.maximum(L, 1e-6))), 0) for c in cs]), L


def classes(c, w):
    W = max(w.sum(), 1e-9); g = [(c[k] * w).sum() / W for k in range(3)]
    return np.array([1 - g[0], g[0] - g[1], g[1] - g[2], g[2]])


def main():
    tl = tiles()
    train = [t for t in tl if 45 <= t[0] < 55 or -56 <= t[0] < -45]
    test = [t for t in tl if 55 <= t[0] < 60]
    S = np.zeros((len(B.GB) - 1, 3)); N = np.zeros(len(B.GB) - 1)
    for la, lo in train:
        c, L = measure(la, lo); b = B.gebco_bins(G, la, lo).ravel(); w = (L > 0.5).ravel().astype(float)
        for k in range(3):
            S[:, k] += np.bincount(b, c[k].ravel() * w, minlength=len(B.GB) - 1)
        N += np.bincount(b, w, minlength=len(B.GB) - 1)
    table = S / np.maximum(N, 1)[:, None]
    errs = []; tm = np.zeros(4); te = np.zeros(4); Wt = 0
    for la, lo in test:
        c, L = measure(la, lo); b = B.gebco_bins(G, la, lo)
        est = np.stack([np.where(L > 0, table[b, k], 0) for k in range(3)])
        w = L * np.cos(np.radians(la + 0.5))
        if w.sum() < 100: continue
        m, e = classes(c, w), classes(est, w)
        errs.append(np.abs(m - e)); tm += m * w.sum(); te += e * w.sum(); Wt += w.sum()
    errs = np.array(errs) * 100
    res = dict(train_tiles=len(train), test_tiles=len(errs), table=table.tolist(),
               per_tile_mean_abs_points=errs.mean(0).round(2).tolist(), per_tile_p90_points=np.percentile(errs, 90, axis=0).round(2).tolist(),
               all_test_measured=(tm / Wt).round(4).tolist(), all_test_estimated=(te / Wt).round(4).tolist())
    json.dump(res, open(os.path.join(T.WORK2, 'estimate_error.json'), 'w'), indent=1)
    print(json.dumps({k: v for k, v in res.items() if k != 'table'}, indent=1))


if __name__ == '__main__':
    main()
