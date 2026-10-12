# The Earth 2 study's measures of the sea's depth (GEBCO 2026 against earth-1's sea), over every 1-degree tile:
#   - the world's sea area by depth, and the sea within 20 km of land (a distance transform on earth-1's 15" cells,
#     land = sea share under half), shallow at each candidate threshold;
#   - the compressed size of the candidate layers: the shallow share (4-bit, 15") at 20/30/50 m, and the depth byte
#     (depth.code_depth) at 15", 30", 1' and 2'.
# Writes <E2>/work/depth_study.json.   python3 -I depth_study.py
import sys, os, math, time, json
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
import numpy as np
from scipy.ndimage import distance_transform_edt
import terrain as T, depth as D
from earthread import Earth

TH = [5, 10, 15, 20, 25, 30, 40, 50, 60, 80, 100, 150, 200]
NEAR_M = 20000.0
SH_SIZES = [20, 30, 50]
DEPTH_RES = [240, 120, 60, 30]


def main():
    E = Earth(T.EARTH1)
    G = T.gebco()
    cache = {}

    def sea(t):
        if t not in cache:
            if len(cache) > 1500:
                for k in list(cache)[:500]: cache.pop(k)
            cache[t] = E.grid('sea', t)
        return cache[t]

    area_all = 0.0; area_sh = np.zeros(len(TH)); near_all = 0.0; near_sh = np.zeros(len(TH))
    sizes = {'shallow%d' % m: 0 for m in SH_SIZES}
    sizes.update({'depth%d' % c: 0 for c in DEPTH_RES})
    blobs = {k: 0 for k in sizes}
    t0 = time.time()
    for b in range(180):
        lat0 = b - 90
        latc = lat0 + 1 - (np.arange(240) + 0.5) / 240
        A = (np.cos(np.radians(latc)) * (463.3 ** 2))[:, None]       # m2 a cell, by row
        mr = 44                                                        # 20.4 km of rows
        mc = int(min(240, math.ceil(44 / max(0.05, math.cos(math.radians(abs(lat0) + 1))))))
        for lo in range(-180, 180):
            t = b * 360 + lo + 180
            s = sea(t)
            if s.max() == 0:
                continue
            gp = T.gebco_tile(G, lat0, lo, pad=1)
            d = np.maximum(0, -gp[1:-1, 1:-1].astype(np.float32))
            w = s.astype(np.float32) / 15 * A
            area_all += w.sum()
            q = {}
            if gp.max() > -max(TH) - 50:
                sub = D.subsample(gp)
                for i, m in enumerate(TH):
                    sh = D.shallow_share(gp, s, m, sub=sub)
                    q[m] = sh
                    area_sh[i] += (sh * w).sum()
            else:                                                       # open ocean: nothing shallow
                for m in TH:
                    q[m] = np.zeros((240, 240), np.float32)
            # within 20 km of land: a window of earth-1's sea round the tile
            win = np.zeros((240 + 2 * mr, 240 + 2 * mc), np.uint8)
            for dr, la in ((-1, lat0 + 1), (0, lat0), (1, lat0 - 1)):
                if la < -90 or la > 89:
                    continue
                for dc, ln in ((-1, lo - 1), (0, lo), (1, lo + 1)):
                    ln2 = (ln + 180) % 360 - 180
                    g2 = sea((la + 90) * 360 + ln2 + 180)
                    r0 = mr + dr * 240; c0 = mc + dc * 240
                    rs, re_ = max(0, r0), min(win.shape[0], r0 + 240)
                    cs, ce = max(0, c0), min(win.shape[1], c0 + 240)
                    win[rs:re_, cs:ce] = g2[rs - r0:re_ - r0, cs - c0:ce - c0]
            land = win < 8
            if land.any():
                dist = distance_transform_edt(~land, sampling=(463.3, 463.3 * math.cos(math.radians(lat0 + 0.5))))
                near = dist[mr:mr + 240, mc:mc + 240] <= NEAR_M
                wn = w * near
                near_all += wn.sum()
                for i, m in enumerate(TH):
                    near_sh[i] += (q[m] * wn).sum()
            # layer sizes
            for m in SH_SIZES:
                n = D.blob_size(D.q4(q[m])); sizes['shallow%d' % m] += n; blobs['shallow%d' % m] += n > 0
            for c in DEPTH_RES:
                n = D.blob_size(D.code_depth(D.sea_depth(gp, s, c))); sizes['depth%d' % c] += n; blobs['depth%d' % c] += n > 0
        if b % 10 == 9:
            T.log('depth_study', 'band %d, %.0f s, sea %.3g km2, sizes %s' % (b, time.time() - t0, area_all / 1e6,
                  {k: round(v / 1e6, 2) for k, v in sizes.items()}))
    res = dict(thresholds=TH, sea_km2=area_all / 1e6, shallow_share=list(area_sh / area_all),
               near_km2=near_all / 1e6, near_shallow_share=list(near_sh / near_all),
               layer_bytes=sizes, layer_blobs=blobs, seconds=round(time.time() - t0))
    os.makedirs(T.WORK2, exist_ok=True)
    with open(os.path.join(T.WORK2, 'depth_study.json'), 'w') as f:
        json.dump(res, f, indent=1)
    T.log('depth_study', json.dumps(res))


if __name__ == '__main__':
    main()
