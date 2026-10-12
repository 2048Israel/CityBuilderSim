# The Earth 2 study's slope-layer size: the compressed bytes of the slope layer under each packing and grid, from the
# tiles slope_tiles.py has done so far (a random order, so a fair sample) and from a random sample of the tiles the
# GEBCO estimate covers; projected to the whole layer.   python3 -I slope_size.py
import sys, os, zlib, random, json
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
import numpy as np
import terrain as T, depth as D, bake2 as B
from earthread import Earth

E = Earth(T.EARTH1); G = T.gebco()
SL = os.path.join(T.WORK2, 'slope')


def z(a):
    a = np.ascontiguousarray(a, np.uint8)
    return 0 if a.min() == a.max() else len(zlib.compress(a.tobytes(), 9))


def variants(cs, L):
    out = {}
    for cpd in (240, 120):
        sh = B.to_cpd(cs, L, cpd)
        out['332@%d' % cpd] = z(B.pack_slope(*sh))
        q = [D.q4(x) for x in sh]
        out['44+4@%d' % cpd] = z(q[0] | (q[1] << 4)) + z(q[2])
    return out


def main():
    names = json.load(open(os.path.join(T.WORK2, 'nasadem_tiles.json')))
    nasa = set()
    for n in names:
        s = n.split('_')[-1]; la = int(s[1:3]) * (1 if s[0] == 'n' else -1); lo = int(s[4:7]) * (1 if s[3] == 'e' else -1)
        nasa.add((la + 90) * 360 + lo + 180)
    done = []
    for n in os.listdir(SL):
        if n.endswith('.npz'):
            s = n[:-4]; la = int(s[1:3]) * (1 if s[0] == 'N' else -1); lo = int(s[4:7]) * (1 if s[3] == 'E' else -1)
            done.append((la + 90) * 360 + lo + 180)
    tot = {}
    for t in done:
        la, lo = t // 360 - 90, t % 360 - 180
        h = np.load(os.path.join(SL, T.tile_name(la, lo, 'srtm') + '.npz'))['h']
        _, L = B.land_of(E, t)
        for k, v in variants(B.ge_counts(h, T.THRESHOLDS), L).items():
            tot[k] = tot.get(k, 0) + v
    # the estimated tiles: land in earth-1, no NASADEM tile
    lm = np.load(os.path.join(T.WORK2, 'landmass', 'blobs.npz'))['entries']
    land_tiles = [t for t in range(64800) if lm[t] != 0]          # uniform land (-id) or a blob (mixed)
    est = [t for t in land_tiles if t not in nasa]
    random.seed(5); smp = random.sample(est, min(600, len(est)))
    # a table from the done tiles at 45-60 degrees (bake2.calibrate's rule, without its cache)
    S = np.zeros((len(B.GB) - 1, 3)); N = np.zeros(len(B.GB) - 1)
    for t in done:
        la, lo = t // 360 - 90, t % 360 - 180
        if not (45 <= la < 60 or -56 <= la < -45): continue
        h = np.load(os.path.join(SL, T.tile_name(la, lo, 'srtm') + '.npz'))['h']; _, L = B.land_of(E, t)
        cs = B.ge_counts(h, T.THRESHOLDS); b = B.gebco_bins(G, la, lo).ravel(); w = (L > 0.5).ravel().astype(float)
        for k in range(3):
            S[:, k] += np.bincount(b, (np.minimum(1, cs[k] / (25 * np.maximum(L, 1e-6)))).ravel() * w, minlength=len(B.GB) - 1)
        N += np.bincount(b, w, minlength=len(B.GB) - 1)
    table = S / np.maximum(N, 1)[:, None]
    etot = {}
    for t in smp:
        la, lo = t // 360 - 90, t % 360 - 180
        _, L = B.land_of(E, t); b = B.gebco_bins(G, la, lo)
        cs = [np.where(L > 0, table[b, k], 0) * 25 * L for k in range(3)]
        for k, v in variants(cs, L).items():
            etot[k] = etot.get(k, 0) + v
    res = {}
    for k in tot:
        m = tot[k] / len(done) * len(nasa); e = etot[k] / len(smp) * len(est)
        res[k] = dict(measured_MB=round(m / 1e6, 1), estimated_MB=round(e / 1e6, 1), total_MB=round((m + e) / 1e6, 1))
    out = dict(done_tiles=len(done), nasadem_tiles=len(nasa), estimated_tiles=len(est), estimated_sample=len(smp), projected=res)
    json.dump(out, open(os.path.join(T.WORK2, 'slope_size.json'), 'w'), indent=1)
    print(json.dumps(out, indent=1))


if __name__ == '__main__':
    main()
