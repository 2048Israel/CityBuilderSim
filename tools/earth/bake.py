# Bakes earth-1.bin from the passes' results (wc_tiles.py, gebco_antarctic.py, lakes.py, rivers.py, geology.py)
# and the study's copies of GEBCO's TID grid and Natural Earth's places. The format is in FORMAT.txt.
#   python3 -I bake.py
import sys, os, io, time, zlib, struct, json, glob, hashlib, datetime
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *
import numpy as np, shapefile, warnings
from scipy.io import netcdf_file
warnings.filterwarnings('ignore', message='Cannot close a netcdf_file')

TID = src('gebco/GEBCO_2026_TID.nc')
PLACES = src('ne/pp/ne_10m_populated_places_simple.shp')
OUTF = OUT + '/earth-1.bin'
LEVEL = 9                                  # zlib level (java.util.zip.Inflater reads any)
Q = 15                                     # 4-bit shares: 0..15
SMALL_KM2 = 10.0

# land-cover byte, a 2' cell (shares of the cell's land, i.e. of its pixels that are not water)
LC_FOREST = 0x0F                           # forest level 0..15 = round(forest / land x 15)
LC_BARE2 = 0x10                            # bare >= 2% of land (the beach rule's threshold, spec-earth 3)
LC_BARE50 = 0x20                           # bare >= 50% of land (the desert warning, spec-earth 7)
LC_ICE = 0x40                              # snow and ice >= 50% of land (the refusal, spec-earth 2)
LC_NODATA = 0x80                           # land without WorldCover (south of 60 S, and GEBCO-only land): fallback


def q4(b):
    """A byte share (x 255) to 4 bits (x 15), rounded half up."""
    return ((b.astype(np.int32) * Q + 127) // 255).astype(np.uint8)


def wc_index():
    idx = {}
    for f in glob.glob(WORK + '/wc/*.done'):
        name = os.path.basename(f)[:-5]
        idx[name] = f[:-5] + '.npz'
    return idx


def wc_name(lat0, lon0):
    a = (lat0 // 3) * 3; b = (lon0 // 3) * 3
    return '%s%02d%s%03d' % ('N' if a >= 0 else 'S', abs(a), 'E' if b >= 0 else 'W', abs(b)), a, b


class Layer:
    def __init__(self, name, kind, cpd, n):
        self.name, self.kind, self.cpd = name, kind, cpd
        self.entries = np.zeros(n, np.int32); self.blobs = {}

    def put(self, i, arr_or_bytes, fill_ok=True):
        if isinstance(arr_or_bytes, np.ndarray):
            a = arr_or_bytes
            if fill_ok and a.size and a.min() == a.max():
                self.entries[i] = -int(a.flat[0]); return
            raw = a.astype(np.uint8).tobytes()
        else:
            raw = arr_or_bytes
            if not raw:
                self.entries[i] = 0; return
        blob = zlib.compress(raw, LEVEL)
        self.entries[i] = len(blob); self.blobs[i] = blob

    def size(self):
        return sum(len(b) for b in self.blobs.values())


def bake_grids(layers, stats, bands=range(180)):
    wci = wc_index()
    log('bake', '%d WorldCover tiles baked' % len(wci))
    g = netcdf_file(TID, 'r', mmap=True); tid = g.variables['tid']
    ant = np.load(WORK + '/antarctic_land.npy')                 # 7200 x 10800 packed, south row first
    lidx = np.load(WORK + '/lakes_share.idx'); lbin = open(WORK + '/lakes_share.bin', 'rb')
    lakes_at = {int(t): (int(o), int(n)) for t, o, n in lidx}
    sea_L, lake_L, lc_L, fl_L, fk_L = layers['sea'], layers['lake'], layers['landcover'], layers['far.land'], layers['far.lake']
    cache = {}
    st = dict(wc_tiles=0, gebco_tiles=0, antarctic_tiles=0, gebco_only_land_cells=0, antarctic_land_cells=0,
              override_cells=0, override_bytes=0)
    t0 = time.time()
    for b in bands:
        lat0 = b - 90
        r0 = b * 240
        tb = np.flipud(np.array(tid[r0:r0 + 240, :])) != 0             # GEBCO ocean, north row first
        if lat0 < -60:
            antl = np.flipud(np.unpackbits(ant[r0:r0 + 240], axis=1).astype(bool))   # land, north row first
        sea_band = np.zeros((240, 86400), np.uint8); lake_band = np.zeros((240, 86400), np.uint8)
        for lon0 in range(-180, 180):
            t = tile_id(lat0, lon0)
            c0 = (lon0 + 180) * 240
            name, wa, wb = wc_name(lat0, lon0)
            lc = None
            if name in wci:
                if name not in cache:
                    if len(cache) > 400: cache.clear()
                    z = np.load(wci[name]); cache[name] = (z['sea'], z['lc'])
                sea_wc, lc_wc = cache[name]
                rr = (wa + 3 - (lat0 + 1)) * 240; cc = (lon0 - wb) * 240
                sea_b = sea_wc[rr:rr + 240, cc:cc + 240].astype(np.int16)
                lc = lc_wc[:, rr // 8:rr // 8 + 30, cc // 8:cc // 8 + 30].astype(np.int32)
                st['wc_tiles'] += 1; src = 'wc'
            elif lat0 < -60:
                land = antl[:, c0:c0 + 240]
                sea_b = np.where(land, 0, 255).astype(np.int16)
                st['antarctic_tiles'] += 1; st['antarctic_land_cells'] += int(land.sum()); src = 'ant'
            else:
                oc = tb[:, c0:c0 + 240]
                sea_b = np.where(oc, 255, 0).astype(np.int16)
                st['gebco_tiles'] += 1; st['gebco_only_land_cells'] += int((~oc).sum()); src = 'gebco'
            if t in lakes_at:
                o, n = lakes_at[t]; lbin.seek(o)
                lake_b = np.frombuffer(zlib.decompress(lbin.read(n)), np.uint8).reshape(240, 240).astype(np.int16)
                ov = (lake_b > 0) & (sea_b > 0)
                st['override_cells'] += int(ov.sum()); st['override_bytes'] += int(np.minimum(sea_b, lake_b)[ov].sum())
                sea_b = np.maximum(0, sea_b - lake_b)                   # the lake overrides GEBCO's ocean (spec-earth 4)
            else:
                lake_b = np.zeros((240, 240), np.int16)
            sea_q = q4(sea_b); lake_q = q4(lake_b)
            assert int((sea_q.astype(np.int16) + lake_q).max()) <= Q
            sea_L.put(t, sea_q); lake_L.put(t, lake_q)
            sea_band[:, c0:c0 + 240] = sea_b; lake_band[:, c0:c0 + 240] = lake_b
            # land cover, 30 x 30
            if lc is not None:
                F, B, I, Wt = lc
                land = 10000 - Wt
                with np.errstate(divide='ignore', invalid='ignore'):
                    fr = np.where(land > 0, F / np.maximum(land, 1), 0.0)
                    br = np.where(land > 0, B / np.maximum(land, 1), 0.0)
                    ir = np.where(land > 0, I / np.maximum(land, 1), 0.0)
                v = (np.floor(fr * Q + 0.5).astype(np.int32) & LC_FOREST)
                v |= np.where(br >= 0.02, LC_BARE2, 0) | np.where(br >= 0.5, LC_BARE50, 0) | np.where(ir >= 0.5, LC_ICE, 0)
                v = np.where(land > 0, v, 0)
            else:
                dry = (255 - sea_b - lake_b) > 0                          # 15" cells with dry ground
                dry2 = dry.reshape(30, 8, 30, 8).any(axis=(1, 3))
                v = np.where(dry2, LC_NODATA | (LC_ICE if src == 'ant' else 0), 0)
            lc_L.put(t, v.astype(np.uint8))
        # the far view, 4' (16 x 16 cells of 15"): dry share and lake share, bytes
        s = sea_band.reshape(15, 16, 5400, 16).sum(axis=(1, 3), dtype=np.int64)
        k = lake_band.reshape(15, 16, 5400, 16).sum(axis=(1, 3), dtype=np.int64)
        land4 = np.floor((255 * 256 - s - k) / 256.0 + 0.5).astype(np.uint8)
        lake4 = np.floor(k / 256.0 + 0.5).astype(np.uint8)
        fl_L.put(b, land4); fk_L.put(b, lake4)
        if b % 10 == 0:
            log('bake', 'band %d (lat %d), %.0f s; sea %.2f MB, lake %.2f MB, land cover %.2f MB' %
                (b, lat0, time.time() - t0, sea_L.size() / 1e6, lake_L.size() / 1e6, lc_L.size() / 1e6))
    stats.update(st)


def bake_small_lakes(L, stats):
    z = np.load(WORK + '/lakes_small.npz')
    s = z['area'] < SMALL_KM2
    la, lo, ar = z['lat'][s], z['lon'][s], z['area'][s]
    qa = np.round(la * 1e4).astype(np.int64); qo = np.round(lo * 1e4).astype(np.int64)
    code = np.clip(np.round(np.log2(ar / 0.1) * 8), 0, 255).astype(np.int64)     # 0.1 km2 x 2^(code/8)
    tl = np.floor(la).astype(int); tn = np.floor(lo).astype(int)
    tn = np.where(tn == 180, 179, tn)
    t = (tl + 90) * 360 + (tn + 180)
    o = np.lexsort((qo, qa, t))
    t, qa, qo, code = t[o], qa[o], qo[o], code[o]
    starts = np.r_[0, np.nonzero(np.diff(t))[0] + 1]; ends = np.r_[starts[1:], len(t)]
    for a, b in zip(starts, ends):
        tile = int(t[a]); lat0, lon0 = tile_of(tile)
        buf = bytearray(); uvarint(buf, int(b - a))
        pa, po = lat0 * 10000, lon0 * 10000
        for i in range(a, b):
            varint(buf, int(qa[i] - pa)); varint(buf, int(qo[i] - po)); buf.append(int(code[i]))
            pa, po = qa[i], qo[i]
        L.put(tile, bytes(buf), fill_ok=False)
    stats['small_lakes'] = int(s.sum()); stats['small_lake_tiles'] = len(starts)
    stats['small_lakes_area_km2'] = float(ar.sum())


def bake_rivers(L, stats):
    V = []; lens = []
    for f in sorted(glob.glob(WORK + '/rivers/*.npz')):
        z = np.load(f); V.append(z['v']); lens.append(z['lens'])
    V = np.concatenate(V); lens = np.concatenate(lens)
    stats['river_lines'] = int(len(lens)); stats['river_vertices'] = int(len(V))
    stats['grwl_tiles'] = len(glob.glob(WORK + '/rivers/*.done'))
    src = 0
    for f in glob.glob(WORK + '/rivers/*.done'):
        src += json.load(open(f))['source_vertices']
    stats['grwl_source_vertices'] = src
    qa = np.round(V[:, 0] * 1e5).astype(np.int64); qo = np.round(V[:, 1] * 1e5).astype(np.int64)
    w = np.round(V[:, 2]).astype(np.int64); fl = V[:, 3].astype(np.int64)
    # segment k joins vertex k and k + 1 of the same line; it goes to every 1-degree tile its box touches,
    # and a tile's segments, consecutive, make its runs
    last = np.cumsum(lens) - 1
    ok = np.ones(len(V) - 1, bool); ok[last[:-1]] = False
    k = np.nonzero(ok)[0]
    la0, la1, lo0, lo1 = V[k, 0], V[k + 1, 0], V[k, 1], V[k + 1, 1]
    ty0 = np.floor(np.minimum(la0, la1)).astype(np.int64); ty1 = np.floor(np.maximum(la0, la1)).astype(np.int64)
    tx0 = np.floor(np.minimum(lo0, lo1)).astype(np.int64); tx1 = np.floor(np.maximum(lo0, lo1)).astype(np.int64)
    tx0 = np.minimum(tx0, 179); tx1 = np.minimum(tx1, 179)
    one = (ty0 == ty1) & (tx0 == tx1)
    seg_tile = [(ty0[one] + 90) * 360 + (tx0[one] + 180)]; seg_k = [k[one]]
    for j in np.nonzero(~one)[0]:
        for ty in range(ty0[j], ty1[j] + 1):
            for tx in range(tx0[j], tx1[j] + 1):
                seg_tile.append(np.array([(ty + 90) * 360 + tx + 180])); seg_k.append(np.array([k[j]]))
    seg_tile = np.concatenate(seg_tile); seg_k = np.concatenate(seg_k)
    o = np.lexsort((seg_k, seg_tile)); seg_tile = seg_tile[o]; seg_k = seg_k[o]
    tstart = np.r_[0, np.nonzero(np.diff(seg_tile))[0] + 1]; tend = np.r_[tstart[1:], len(seg_tile)]
    tiles = {int(seg_tile[a]): seg_k[a:b] for a, b in zip(tstart, tend)}
    nseg = 0
    for tile, segs in tiles.items():
        lat0, lon0 = tile_of(tile)
        nseg += len(segs)
        brk = np.r_[0, np.nonzero(np.diff(segs) != 1)[0] + 1, len(segs)]
        buf = bytearray(); uvarint(buf, len(brk) - 1)
        for a, b in zip(brk[:-1], brk[1:]):
            i0 = segs[a]; i1 = segs[b - 1] + 1                        # vertices i0..i1
            uvarint(buf, int(i1 - i0 + 1))
            pa, po, pw = lat0 * 100000, lon0 * 100000, 0
            for i in range(i0, i1 + 1):
                varint(buf, int(qa[i] - pa)); varint(buf, int(qo[i] - po)); varint(buf, int(w[i] - pw)); uvarint(buf, int(fl[i]))
                pa, po, pw = qa[i], qo[i], w[i]
        L.put(tile, bytes(buf), fill_ok=False)
    stats['river_tiles'] = len(tiles); stats['river_segments_stored'] = nseg


def bake_whole(layers, stats):
    grid = np.load(WORK + '/provinces025.npy')
    assert grid.max() < 65536
    layers['geology'].put(0, grid.astype('>u2').tobytes(), fill_ok=False)
    layers['provinces'].put(0, open(WORK + '/provinces.tsv', 'rb').read(), fill_ok=False)
    r = shapefile.Reader(PLACES)
    rows = []
    for rec in r.iterRecords(fields=['name', 'nameascii', 'adm0name', 'latitude', 'longitude', 'pop_max']):
        rows.append((-int(rec['pop_max']), rec['name'], rec['nameascii'], rec['adm0name'], rec['latitude'], rec['longitude']))
    rows.sort()
    txt = ''.join('%s\t%s\t%s\t%.4f\t%.4f\t%d\n' % (n, a if a != n else '', c, la, lo, -p) for p, n, a, c, la, lo in rows)
    layers['places'].put(0, txt.encode('utf-8'), fill_ok=False)
    stats['places'] = len(rows)


def write(layers, meta):
    order = list(layers)
    meta_raw = json.dumps(meta, indent=1, sort_keys=True).encode('utf-8')
    layers['meta'] = Layer('meta', 2, 0, 1); layers['meta'].put(0, meta_raw, fill_ok=False); order.append('meta')
    # header size first
    def header(offsets):
        h = io.BytesIO()
        h.write(b'EARTH-1\n'); h.write(struct.pack('>ii', 1, len(order)))
        for name in order:
            L = layers[name]; nb = name.encode('utf-8')
            h.write(struct.pack('>H', len(nb))); h.write(nb)
            io_, il, do, dl = offsets.get(name, (0, 0, 0, 0))
            h.write(struct.pack('>biqiqq', L.kind, L.cpd, io_, il, do, dl))
        return h.getvalue()
    idx = {name: zlib.compress(layers[name].entries.astype('>i4').tobytes(), LEVEL) if layers[name].kind != 2 else b'' for name in order}
    pos = len(header({}))
    offsets = {}
    for name in order:
        L = layers[name]
        io_ = pos; il = len(idx[name]); pos += il
        do = pos; dl = L.size(); pos += dl
        offsets[name] = (io_, il, do, dl)
    hb = header(offsets)
    os.makedirs(OUT, exist_ok=True)
    sha = hashlib.sha256()
    with open(OUTF, 'wb') as f:
        for part in [hb] + [p for name in order for p in [idx[name]] + [layers[name].blobs[i] for i in sorted(layers[name].blobs)]]:
            f.write(part); sha.update(part)
    assert os.path.getsize(OUTF) == pos
    return sha.hexdigest(), {name: (offsets[name][1] + offsets[name][3]) for name in order}, len(hb)


def main():
    t0 = time.time()
    layers = {
        'sea': Layer('sea', 0, 240, TILES), 'lake': Layer('lake', 0, 240, TILES),
        'lakes.small': Layer('lakes.small', 0, 0, TILES), 'rivers': Layer('rivers', 0, 0, TILES),
        'landcover': Layer('landcover', 0, 30, TILES),
        'far.land': Layer('far.land', 1, 15, 180), 'far.lake': Layer('far.lake', 1, 15, 180),
        'geology': Layer('geology', 2, 4, 1), 'provinces': Layer('provinces', 2, 0, 1), 'places': Layer('places', 2, 0, 1),
    }
    stats = {}
    bake_grids(layers, stats); log('bake', 'grids done %.0f s' % (time.time() - t0))
    bake_small_lakes(layers['lakes.small'], stats); log('bake', 'small lakes done %.0f s' % (time.time() - t0))
    bake_rivers(layers['rivers'], stats); log('bake', 'rivers done %.0f s' % (time.time() - t0))
    bake_whole(layers, stats)
    meta = dict(name='earth-1', format=1, built=datetime.datetime.now().strftime('%Y-%m-%d'),
                tiles='1-degree tiles numbered (lat0 + 90) * 360 + (lon0 + 180), south row first; cells rows from the north',
                stats=stats, sources=json.load(open(OUT + '/sources.json')) if os.path.exists(OUT + '/sources.json') else {})
    sha, sizes, hlen = write(layers, meta)
    with open(OUT + '/earth-1.sha256', 'w') as f:
        f.write('%s  earth-1.bin\n' % sha)
    rep = dict(sha256=sha, bytes=os.path.getsize(OUTF), header=hlen, layers=sizes, stats=stats, seconds=round(time.time() - t0))
    write_json(OUT + '/bake-stats.json', rep)
    log('bake', 'wrote %s, %d bytes, sha256 %s, %.0f s' % (OUTF, rep['bytes'], sha, time.time() - t0))
    for k, v in sizes.items():
        log('bake', '  %-12s %10d bytes' % (k, v))


if __name__ == '__main__':
    main()
