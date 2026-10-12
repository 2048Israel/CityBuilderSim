# Bakes earth-2.bin: every layer of earth-1.bin copied byte for byte, and the Earth 2 study's new layers
# (docs/studies/spec-earth2-terrain.md; FORMAT-2.txt has the layout in words):
#   slope                SLOPE_CPD a degree, a byte a cell: the shares of each cell's land at or above each slope
#                        threshold (terrain.THRESHOLDS) in 8, 8 and 4 levels (pack_slope()), from slope_tiles.py's
#                        histograms (NASADEM over 90 m) where NASADEM reaches (61 N to 56 S), else GEBCO 2026's 15"
#                        slope through a table calibrated on NASADEM's tiles at 45-60 degrees
#   slope.est            which 1-degree tiles hold the estimate (a bit a tile)
#   shallow              15": the share of each cell's sea no deeper than depth.SHALLOW_M (GEBCO 2026)
#   depth                1': the depth of each cell's sea (GEBCO 2026), depth.code_depth()
#   landmass, landmasses 15": each land cell's landmass id, and the table of landmasses (landmass.py: ground cut off
#                        by more than a short crossing, 1,020 m of fresh water or shallow sea; deep sea never crossed)
# The container is earth-1's (the same magic and layout version), so EarthData reads it as it is; the file's name in
# its meta layer is earth-2, and its identity its SHA-256.
#   python3 -I bake2.py
import sys, os, io, time, zlib, struct, json, math, hashlib, datetime
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
import terrain as T, depth as D
from earthread import Earth
import sources2

OUTF = os.path.join(T.OUT2, os.environ.get('CBS_E2_NAME', 'earth-2.bin'))
# a trial run over some bands of latitude only (CBS_BANDS=134,135 - band = lat0 + 90); the others' new layers left empty
BANDS = set(int(x) for x in os.environ['CBS_BANDS'].split(',')) if os.environ.get('CBS_BANDS') else None
LEVEL = 9
Q = 15
TILES = 64800
SLOPE = os.path.join(T.WORK2, 'slope')
CALIB = os.path.join(T.WORK2, 'slope_calib.json')
GB = [0, 1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 18, 22, 26, 30, 35, 40, 50, 60, 80, 1e9]    # GEBCO 15" slope bins, percent
PER_CELL = 25                                     # 3" slopes in a 15" cell
SLOPE_CPD = int(os.environ.get('CBS_SLOPE_CPD', '120'))   # the slope layer's cells a degree: 120 (30", 57 MB projected) or 240 (15", 204 MB)


def pack_slope(F, S, V):
    """Shares (0..1) of land at or above foothill, steep and very steep (F >= S >= V) to a byte: bits 0-2 F in
    sevenths, bits 3-5 S in sevenths, bits 6-7 the very steep part OF THE STEEP (V / S) in thirds - so V decodes
    to no more than S."""
    f = np.floor(F * 7 + 0.5).astype(np.uint8); s = np.floor(S * 7 + 0.5).astype(np.uint8)
    with np.errstate(divide='ignore', invalid='ignore'):
        r = np.where(S > 0, V / np.maximum(S, 1e-9), 0)
    v = np.where(s > 0, np.floor(np.clip(r, 0, 1) * 3 + 0.5), 0).astype(np.uint8)
    return (f | (s << 3) | (v << 6)).astype(np.uint8)


def unpack_slope(b):
    """A slope byte to (F, S, V), the shares of land at or above foothill, steep and very steep."""
    b = np.asarray(b)
    S = ((b >> 3) & 7) / 7.0
    return (b & 7) / 7.0, S, S * (b >> 6) / 3.0


def to_cpd(counts, L, cpd=None):
    """Per-15"-cell counts of 3" slopes at or above each threshold, and the land share, to the shares of land on a
    cpd-a-degree grid (counts and land summed over the coarser cell)."""
    cpd = SLOPE_CPD if cpd is None else cpd
    k = 240 // cpd
    if k > 1:
        counts = [c.reshape(cpd, k, cpd, k).sum(axis=(1, 3)) for c in counts]; L = L.reshape(cpd, k, cpd, k).sum(axis=(1, 3))
    return [np.where(L > 0, np.minimum(1, c / (PER_CELL * np.maximum(L, 1e-6))), 0) for c in counts]

class Layer:
    def __init__(self, name, kind, cpd, n):
        self.name, self.kind, self.cpd = name, kind, cpd
        self.entries = np.zeros(n, np.int64); self.blobs = {}

    def put(self, i, a, fill_ok=True):
        if isinstance(a, np.ndarray):
            if fill_ok and a.size and a.min() == a.max():
                self.entries[i] = -int(a.flat[0]); return
            raw = np.ascontiguousarray(a, np.uint8).tobytes()
        else:
            raw = a
            if not raw:
                self.entries[i] = 0; return
        b = zlib.compress(raw, LEVEL)
        self.entries[i] = len(b); self.blobs[i] = b

    def put_blob(self, i, entry, blob=None):
        self.entries[i] = entry
        if blob is not None: self.blobs[i] = blob

    def size(self):
        return sum(len(b) for b in self.blobs.values())


def copy_earth1(E, f):
    """earth-1's layers but meta, their blobs as they are in the file."""
    out = {}
    for name, L in E.layers.items():
        if name == 'meta':
            continue
        n = TILES if L['kind'] == 0 else 180 if L['kind'] == 1 else 1
        N = Layer(name, L['kind'], L['cpd'], n)
        if L['kind'] == 2:
            f.seek(L['data_off']); N.put_blob(0, L['data_len'], f.read(L['data_len']))
        else:
            e = L['entries']; N.entries[:] = e
            f.seek(L['data_off']); data = f.read(L['data_len']); pos = 0
            for i in np.nonzero(e > 0)[0]:
                N.blobs[int(i)] = data[pos:pos + int(e[i])]; pos += int(e[i])
            assert pos == len(data), name
        out[name] = N
    return out


def gebco_bins(G, la, lo):
    gp = T.gebco_tile(G, la, lo, pad=1).astype(np.float32)
    s = T.slope_pct(gp, la + 1 + 0.5 / 240, 1 / 240.0)[1:-1, 1:-1]
    return np.clip(np.searchsorted(GB, s, side='right') - 1, 0, len(GB) - 2)


def ge_counts(h, th):
    k = [int(np.searchsorted(T.BINS, x)) for x in th]
    return [h[:, :, j:].sum(axis=2, dtype=np.int32) for j in k]


def land_of(E, t):
    s = E.grid('sea', t).astype(np.int16); l = E.grid('lake', t).astype(np.int16)
    return s, 1 - (s + l) / 15.0


def calibrate(E, G, done):
    """Mean land share at or above each threshold per GEBCO 15" slope bin, over the NASADEM tiles at 45-60 degrees
    north and south (land cells at least half land)."""
    if os.path.exists(CALIB):
        c = json.load(open(CALIB))
        if c['thresholds'] == list(T.THRESHOLDS) and c.get('tiles') == len(done):
            return np.array(c['table'])
    S = np.zeros((len(GB) - 1, 3)); N = np.zeros(len(GB) - 1)
    for t in done:
        la, lo = t // 360 - 90, t % 360 - 180
        if not (45 <= la < 60 or -56 <= la < -45):
            continue
        z = np.load(os.path.join(SLOPE, T.tile_name(la, lo, 'srtm') + '.npz'))
        _, L = land_of(E, t)
        cs = ge_counts(z['h'], T.THRESHOLDS)
        b = gebco_bins(G, la, lo).ravel(); w = (L > 0.5).ravel().astype(np.float64)
        for k in range(3):
            S[:, k] += np.bincount(b, (np.minimum(1, cs[k] / (PER_CELL * np.maximum(L, 1e-6)))).ravel() * w, minlength=len(GB) - 1)
        N += np.bincount(b, w, minlength=len(GB) - 1)
    table = S / np.maximum(N, 1)[:, None]
    json.dump(dict(thresholds=list(T.THRESHOLDS), tiles=len(done), bins=GB, table=table.tolist(), cells=N.tolist()), open(CALIB, 'w'), indent=1)
    return table


def main():
    t0 = time.time()
    E = Earth(T.EARTH1); f1 = open(T.EARTH1, 'rb'); G = T.gebco()
    layers = copy_earth1(E, f1)
    sl = Layer('slope', 0, SLOPE_CPD, TILES)
    sh = Layer('shallow', 0, 240, TILES); dp = Layer('depth', 0, D.DEPTH_CPD, TILES)
    done = set()
    for name in os.listdir(SLOPE):
        if name.endswith('.npz'):
            s = name[:-4]; la = int(s[1:3]) * (1 if s[0] == 'N' else -1); lo = int(s[4:7]) * (1 if s[3] == 'E' else -1)
            done.add((la + 90) * 360 + lo + 180)
    T.log('bake2', '%d tiles of NASADEM slopes' % len(done))
    table = calibrate(E, G, done)
    est = np.zeros(TILES, bool)
    st = dict(slope_tiles_measured=0, slope_tiles_estimated=0, land_cells_measured=0, land_cells_estimated=0,
              shallow_cells=0, sea_tiles=0)
    for t in range(TILES):
        la, lo = t // 360 - 90, t % 360 - 180
        if BANDS is not None and t // 360 not in BANDS:
            continue
        se = E.layers['sea']['entries'][t]; le = E.layers['lake']['entries'][t]
        all_water = se <= 0 and le <= 0 and (-se) + (-le) >= 15
        sea, L = land_of(E, t)
        # the slope
        if all_water:
            sl.put(t, np.zeros((1,), np.uint8))
        elif t in done:
            z = np.load(os.path.join(SLOPE, T.tile_name(la, lo, 'srtm') + '.npz'))
            sl.put(t, pack_slope(*to_cpd(ge_counts(z['h'], T.THRESHOLDS), L)))
            st['slope_tiles_measured'] += 1; st['land_cells_measured'] += int((L > 0).sum())
        else:
            b = gebco_bins(G, la, lo)
            # the table's shares stand for counts: share x 25 x land, so a coarser cell weighs its cells by their land
            cs = [np.where(L > 0, table[b, k], 0) * PER_CELL * L for k in range(3)]
            sl.put(t, pack_slope(*to_cpd(cs, L)))
            est[t] = True; st['slope_tiles_estimated'] += 1; st['land_cells_estimated'] += int((L > 0).sum())
        # the sea
        if sea.max() == 0:
            sh.put(t, np.zeros((1,), np.uint8)); dp.put(t, np.zeros((1,), np.uint8))
        else:
            gp = T.gebco_tile(G, la, lo, pad=1)
            if gp.max() < -D.SHALLOW_M - 50:
                sh.put(t, np.zeros((1,), np.uint8))
            else:
                s4 = D.q4(D.shallow_share(gp, sea)); sh.put(t, s4); st['shallow_cells'] += int((s4 > 0).sum())
            dp.put(t, D.code_depth(D.sea_depth(gp, sea, D.DEPTH_CPD)))
            st['sea_tiles'] += 1
        if t % 3600 == 3599:
            T.log('bake2', 'band %d, %.0f s; slope %.1f MB, shallow %.1f MB, depth %.1f MB' %
                  (t // 360, time.time() - t0, sl.size() / 1e6, sh.size() / 1e6, dp.size() / 1e6))
    layers.update({'slope': sl, 'shallow': sh, 'depth': dp})
    se_ = Layer('slope.est', 2, 0, 1); se_.put(0, np.packbits(est).tobytes(), fill_ok=False); layers['slope.est'] = se_
    # the landmasses
    lm = os.path.join(T.WORK2, 'landmass')
    if os.path.exists(os.path.join(lm, 'blobs.npz')):
        z = np.load(os.path.join(lm, 'blobs.npz'))
        L1 = Layer('landmass', 0, 240, TILES); L1.entries[:] = z['entries']
        data = z['data'].tobytes(); pos = 0
        for t, n in zip(z['tiles'], z['lens']):
            L1.blobs[int(t)] = data[pos:pos + int(n)]; pos += int(n)
        L2 = Layer('landmasses', 2, 0, 1); L2.put(0, open(os.path.join(lm, 'table.tsv'), 'rb').read(), fill_ok=False)
        layers['landmass'] = L1; layers['landmasses'] = L2
        st['landmass'] = json.load(open(os.path.join(lm, 'stats.json')))
    else:
        T.log('bake2', 'no landmass layer (run landmass.py first): left out')
    m1 = E.meta()
    srcs = sources2.sources()                         # earth-1's, as earth-2 uses them, and the new (ATTRIBUTION-2.txt's)
    meta = dict(name='earth-2', format=1, built=datetime.datetime.now().strftime('%Y-%m-%d'), from_file='earth-1.bin',
                from_sha256=open(os.path.join(T.HERE, 'earth-1.sha256')).read().split()[0],
                slope=dict(thresholds_percent=list(T.THRESHOLDS), baseline='heights averaged to 3" (90 m), Horn over 3"',
                           cpd=SLOPE_CPD, packing='bits 0-2: share >= foothill x 7; bits 3-5: share >= steep x 7; bits 6-7: (very steep / steep) x 3',
                           estimate='GEBCO 2026 15" slope, binned %s, through the mean shares of NASADEM\'s tiles at 45-60 degrees' % GB),
                landmass=('a landmass is ground cut off by more water than a short crossing spans: land cells (sea + lake '
                          '<= 7 of 15, less GRWL rivers wider than 1,020 m) joined along edges, and joined across fresh water or '
                          'sea whose shallow share (30 m) is 8 of 15 or more where shore to shore is 1,020 m or less; deep sea '
                          'never joins (landmass.py)'),
                shallow_m=D.SHALLOW_M, depth=dict(cpd=D.DEPTH_CPD, code='0 none; 1-200 metres; 201-254 200 m + 10 m a step; 255 750 m or deeper'),
                tiles=m1.get('tiles'), stats=dict(earth1=m1.get('stats'), earth2=st),
                sources=dict(sources=srcs, fallbacks=m1.get('sources', {}).get('fallbacks', []) + [
                    'North of 61 N and south of 56 S, and in any tile NASADEM lacks, the slope shares are GEBCO 2026\'s estimate '
                    '(slope.est marks the tiles); NASADEM\'s voids are filled from the heights round them before the slope is taken.']))
    sha, sizes, hlen = write(layers, meta)
    with open(OUTF[:-4] + '.sha256', 'w') as fo:
        fo.write('%s  %s\n' % (sha, os.path.basename(OUTF)))
    rep = dict(sha256=sha, bytes=os.path.getsize(OUTF), header=hlen, layers=sizes, stats=st, seconds=round(time.time() - t0))
    with open(os.path.join(T.OUT2, 'bake2-stats.json'), 'w') as fo:
        json.dump(rep, fo, indent=1)
    T.log('bake2', 'wrote %s, %d bytes, sha256 %s, %.0f s' % (OUTF, rep['bytes'], sha, time.time() - t0))
    for k, v in sizes.items():
        T.log('bake2', '  %-14s %10d bytes' % (k, v))


def write(layers, meta):
    order = list(layers)
    L = Layer('meta', 2, 0, 1); L.put(0, json.dumps(meta, indent=1, sort_keys=True).encode('utf-8'), fill_ok=False)
    layers['meta'] = L; order.append('meta')

    def header(offsets):
        h = io.BytesIO()
        h.write(b'EARTH-1\n'); h.write(struct.pack('>ii', 1, len(order)))
        for name in order:
            Ly = layers[name]; nb = name.encode('utf-8')
            h.write(struct.pack('>H', len(nb))); h.write(nb)
            io_, il, do, dl = offsets.get(name, (0, 0, 0, 0))
            h.write(struct.pack('>biqiqq', Ly.kind, Ly.cpd, io_, il, do, dl))
        return h.getvalue()
    idx = {n: zlib.compress(layers[n].entries.astype('>i4').tobytes(), LEVEL) if layers[n].kind != 2 else b'' for n in order}
    pos = len(header({})); offsets = {}
    for n in order:
        io_ = pos; il = len(idx[n]); pos += il
        do = pos; dl = layers[n].size(); pos += dl
        offsets[n] = (io_, il, do, dl)
    hb = header(offsets)
    os.makedirs(T.OUT2, exist_ok=True)
    sha = hashlib.sha256()
    part = OUTF + '.part'
    with open(part, 'wb') as fo:
        for p in [hb] + [p for n in order for p in [idx[n]] + [layers[n].blobs[i] for i in sorted(layers[n].blobs)]]:
            fo.write(p); sha.update(p)
    assert os.path.getsize(part) == pos
    os.replace(part, OUTF)
    return sha.hexdigest(), {n: offsets[n][1] + offsets[n][3] for n in order}, len(hb)


if __name__ == '__main__':
    main()
