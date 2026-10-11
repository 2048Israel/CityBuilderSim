# EarthCheck's fixture, cut from earth-1.bin (batch E1, 2026-10-10). Two files:
#   - earth-1-fixture.bin: earth-1.bin's own layout (FORMAT.txt) holding only the tiles EarthCheck reads - the
#     three windows the study drew (Rotterdam, Perth, Paris; check-report.txt's), and Fiji's four on the 180th
#     meridian - their blobs copied byte for byte, every other tile's index entry 0 (so it reads as uniform 0 or
#     empty: it is not in the fixture), the four whole-file layers copied, and the meta given a "fixture" key;
#   - earth-1-fixture.expect: what earthread.py, this folder's reference reader, reads from those tiles, from the
#     fixture and from the full file - for EarthCheck to hold the game's reader (EarthData) to, figure for figure.
#     A line is tab-separated: scope (both = true of the fixture and the full file alike, fixture, full), kind,
#     fields. EarthCheck reads every line; the full file's lines run when the file is named (-Dcbs.earth=<path>).
#   python3 -I tools/earth/fixture.py <earth-1.bin> <out dir>
import sys, os, io, struct, zlib, json, hashlib, math
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
from earthread import Earth

LEVEL = 9                                   # zlib level, as bake.py
TILES = 180 * 360
# name, north edge, west edge (degrees, on the 15" grid), rows, columns of 15" cells: check-report.txt's windows
WINDOWS = [('Rotterdam', 52 + 86 / 240, 3 + 187 / 240, 217, 337),
           ('Perth', -32 + 119 / 240, 115 + 50 / 240, 217, 265),
           ('Paris', 49 + 74 / 240, 1 + 172 / 240, 217, 299)]
FIJI = [(-17, 179), (-17, -180), (-18, 179), (-18, -180)]   # the 180th meridian (spec-earth 2: no seam)
FIJI_SPOT = (-17.0, 180.0)
RING = 2                                    # the full file's wider areas: 2 degrees round each window
SPECIAL = [(89, 0), (89, 179), (-90, 0), (-80, 0), (0, -150), (-1, -50), (41, 50), (30, 31), (45, -84)]
SHARES = ['sea', 'lake', 'landcover']       # the 1-degree share layers
FAR = ['far.land', 'far.lake']              # the latitude bands
VALUES_A_TILE = 16                          # cells a tile and layer: the first cell of each value, up to this many


def tile_id(lat0, lon0):
    return (lat0 + 90) * 360 + (lon0 + 180)


def sha(b):
    return hashlib.sha256(b).hexdigest()


def window_tiles(lat_n, lon_w, rows, cols):
    lat_s = lat_n - rows / 240; lon_e = lon_w + cols / 240
    return [tile_id(a, o) for a in range(math.floor(lat_s), math.floor(lat_n - 1e-9) + 1)
            for o in range(math.floor(lon_w), math.floor(lon_e - 1e-9) + 1)]


def compressed(E, name, i):
    L = E.layers[name]; e = int(L['entries'][i])
    if e <= 0:
        return None
    E.f.seek(int(L['offsets'][i])); return E.f.read(e)


def whole(E, name):
    L = E.layers[name]; E.f.seek(L['data_off']); return E.f.read(L['data_len'])


def cut(E, full_sha, full_bytes, tiles, bands, path):
    """The fixture file: the same header and layers, only these tiles' and bands' blobs."""
    order = list(E.layers)
    meta = E.meta()
    meta['fixture'] = dict(of='earth-1:' + full_sha, bytes_of=full_bytes, tiles=sorted(tiles), bands=sorted(bands),
                           made='tools/earth/fixture.py (batch E1, 2026-10-10)',
                           note='EarthCheck\'s fixture: only these tiles and bands hold their blobs; every other '
                                'index entry is 0, so it reads as uniform 0 or empty')
    parts = {}
    for name in order:
        L = E.layers[name]
        if L['kind'] == 2:
            blob = zlib.compress(json.dumps(meta, indent=1, sort_keys=True).encode('utf-8'), LEVEL) if name == 'meta' \
                else whole(E, name)
            parts[name] = (b'', [blob])
            continue
        n = TILES if L['kind'] == 0 else 180
        keep = tiles if L['kind'] == 0 else bands
        e = np.zeros(n, '>i4'); blobs = []
        for i in sorted(keep):
            e[i] = int(L['entries'][i])
            b = compressed(E, name, i)
            if b is not None:
                blobs.append(b)
        parts[name] = (zlib.compress(e.tobytes(), LEVEL), blobs)

    def header(offsets):
        h = io.BytesIO()
        h.write(b'EARTH-1\n'); h.write(struct.pack('>ii', E.version, len(order)))
        for name in order:
            L = E.layers[name]; nb = name.encode('utf-8')
            h.write(struct.pack('>H', len(nb))); h.write(nb)
            h.write(struct.pack('>biqiqq', L['kind'], L['cpd'], *offsets.get(name, (0, 0, 0, 0))))
        return h.getvalue()
    pos = len(header({})); offsets = {}
    for name in order:
        idx, blobs = parts[name]
        io_ = pos; pos += len(idx); do = pos; dl = sum(len(b) for b in blobs); pos += dl
        offsets[name] = (io_, len(idx), do, dl)
    out = header(offsets) + b''.join(idx + b''.join(blobs) for idx, blobs in (parts[n] for n in order))
    assert len(out) == pos
    with open(path, 'wb') as f:
        f.write(out)
    return out


def river_ints(runs):
    """A tile's runs as the integers they were stored as, one text: n; then lat,lon,width,flag; a vertex."""
    t = []
    for a in runs:
        t.append('%d;' % len(a))
        for la, lo, w, fl in a:
            t.append('%d,%d,%d,%d;' % (round(la * 1e5), round(lo * 1e5), round(w), round(fl)))
        t.append('|')
    return ''.join(t)


def lake_code(area):
    return round(8 * math.log2(area / 0.1))


def lake_ints(rows):
    return ''.join('%d,%d,%d;' % (round(la * 1e4), round(lo * 1e4), lake_code(ar)) for la, lo, ar in rows)


def segments(E, tiles):
    """Every stored segment of the tiles' runs, and those left when a segment already read in an earlier tile (in tile
    order) is dropped: a segment crossing a tile's edge is stored in every tile its box touches (FORMAT.txt)."""
    stored = 0; kept = 0; before = set()
    for t in sorted(tiles):
        here = set()
        for a in E.rivers(t):
            q = [(round(la * 1e5), round(lo * 1e5), round(w), round(fl)) for la, lo, w, fl in a]
            for k in range(len(q) - 1):
                s = (q[k], q[k + 1]); stored += 1; here.add(s)
                kept += s not in before
        before |= here
    return stored, kept


def tile_lines(E, scope, t, out):
    for name in SHARES:
        out.append((scope, 'tile', name, t, int(E.layers[name]['entries'][t]), sha(E.grid(name, t).tobytes())))
    for name in ('lakes.small', 'rivers'):
        b = E.raw(name, t)
        out.append((scope, 'tile', name, t, int(E.layers[name]['entries'][t]), '-' if b is None else sha(b)))
    runs = E.rivers(t)
    out.append((scope, 'rivers', t, len(runs), sum(len(a) for a in runs), sha(river_ints(runs).encode())))
    lk = E.small_lakes(t)
    out.append((scope, 'lakes', t, len(lk), sha(lake_ints(lk).encode())))


def cell_at(cpd, lat, lon):
    """The cell holding a point, as EarthData finds it: rows counted from 90 N and columns from 180 W, a cell holding
    its north and west edges (so a point on a whole degree of latitude is in the tile south of it). Returns the tile's
    south-west corner (lat0, lon0) and the cell's row and column within it."""
    g = min(int(math.floor((90 - lat) * cpd)), 180 * cpd - 1)
    c = int(math.floor(((lon + 180) % 360) * cpd)) % (360 * cpd)
    return 89 - g // cpd, c // cpd - 180, g % cpd, c % cpd


def first_of_each(g, cols=None):
    """(r, c) of the first cell in raster order of each value the grid holds (within the columns, if given)."""
    sub = g if cols is None else g[:, cols[0]:cols[1]]
    off = 0 if cols is None else cols[0]
    cells = []
    for v in np.unique(sub)[:VALUES_A_TILE]:
        r, c = np.argwhere(sub == v)[0]
        cells.append((int(r), int(c) + off, int(v)))
    return cells


def main():
    src, outdir = sys.argv[1], sys.argv[2]
    E = Earth(src)
    data = open(src, 'rb').read()
    full_sha = sha(data); full_bytes = len(data)
    windows = [(n, window_tiles(a, o, r, c), (a - r / 480, o + c / 480)) for n, a, o, r, c in WINDOWS]
    windows.append(('Fiji', [tile_id(a, o) for a, o in FIJI], FIJI_SPOT))
    tiles = sorted({t for _, ts, _ in windows for t in ts})
    bands = sorted({t // 360 for t in tiles})
    os.makedirs(outdir, exist_ok=True)
    fixture = cut(E, full_sha, full_bytes, tiles, bands, os.path.join(outdir, 'earth-1-fixture.bin'))
    F = Earth(os.path.join(outdir, 'earth-1-fixture.bin'))

    L = []
    L.append(('fixture', 'file', sha(fixture), len(fixture)))
    L.append(('full', 'file', full_sha, full_bytes))
    L.append(('fixture', 'of', 'earth-1:' + full_sha))
    for scope, X in (('fixture', F), ('full', E)):
        for name, Y in X.layers.items():
            L.append((scope, 'table', name, Y['kind'], Y['cpd'], Y['index_off'], Y['index_len'], Y['data_off'], Y['data_len']))
            if Y['kind'] != 2:
                L.append((scope, 'index', name, sha(Y['entries'].astype('>i4').tobytes())))
        L.append((scope, 'whole', 'meta', sha(X.raw('meta'))))
    for name in ('geology', 'provinces', 'places'):
        L.append(('both', 'whole', name, sha(E.raw(name))))
    # the windows: their tiles' bytes and decoded lists, cells of every value, the spot's cells
    for wname, ts, (sla, slo) in windows:
        L.append(('both', 'window', wname, repr(sla), repr(slo)) + tuple(ts))
        for t in ts:
            tile_lines(E, 'both', t, L)
            lat0, lon0 = t // 360 - 90, t % 360 - 180
            for name in SHARES:
                g = E.grid(name, t); cpd = E.layers[name]['cpd']
                for r, c, v in first_of_each(g):
                    L.append(('both', 'cell', name, t, r, c, v))
                a, o, r, c = cell_at(cpd, sla, slo)
                if tile_id(a, o) == t:
                    L.append(('both', 'spot', name, repr(sla), repr(slo), t, r, c, int(g[r, c])))
            for k, a in enumerate(E.rivers(t)[:3]):
                for j in sorted({0, len(a) // 2, len(a) - 1}):
                    L.append(('both', 'vertex', t, k, j) + tuple(repr(float(x)) for x in a[j]))
            lk = E.small_lakes(t)
            for j in sorted({0, len(lk) // 2, len(lk) - 1}) if len(lk) else []:
                L.append(('both', 'lake', t, j) + tuple(repr(float(x)) for x in lk[j]))
        st, un = segments(E, ts)
        L.append(('both', 'segments', wname, st, un))
        for b in sorted({t // 360 for t in ts}):
            lat0 = b - 90
            lons = sorted({t % 360 - 180 for t in ts})
            for name in FAR:
                g = E.grid(name, b); cpd = E.layers[name]['cpd']
                L.append(('both', 'tile', name, b, int(E.layers[name]['entries'][b]), sha(g.tobytes())))
                c0 = (lons[0] + 180) * cpd; c1 = (lons[-1] + 181) * cpd
                for r, c, v in first_of_each(g, (c0, c1)):
                    L.append(('both', 'cell', name, b, r, c, v))
                a, o, r, c = cell_at(cpd, sla, slo)
                if a + 90 == b:
                    c = (o + 180) * cpd + c
                    L.append(('both', 'spot', name, repr(sla), repr(slo), b, r, c, int(g[r, c])))
    for c in range(256):
        L.append(('both', 'area', c, repr(0.1 * 2 ** (c / 8))))
    # geology and the provinces it names: the spots, the windows' corners, and the Yilgarn shield east of Perth
    geo = np.frombuffer(E.raw('geology'), '>u2').reshape(720, 1440)
    pts = [(n, a, o) for n, _, (a, o) in windows]
    for n, a, o, r, c in WINDOWS:
        pts += [(n + ' NW', a, o), (n + ' SE', a - r / 240, o + c / 240)]
    pts.append(('Yilgarn', -31.95, 117.35))
    rows = {}
    for line in E.raw('provinces').decode('utf-8').split('\n'):
        if line:
            f = line.split('\t'); rows[int(f[0])] = f
    named = set()
    for n, a, o in pts:
        lo = (o + 180) % 360 - 180
        v = int(geo[min(int((90 - a) * 4), 719), min(int((lo + 180) * 4), 1439)])
        L.append(('both', 'geology', n, repr(a), repr(o), v)); named.add(v)
    for v in sorted(named - {0}):
        L.append(('both', 'province') + tuple(rows[v]))
    L.append(('both', 'provinces', len(rows)))
    places = [p for p in E.raw('places').decode('utf-8').split('\n') if p]
    L.append(('both', 'places', len(places)))
    for k, p in enumerate(places):
        f = p.split('\t')
        if k < 3 or (f[0], f[2]) in (('Rotterdam', 'Netherlands'), ('Perth', 'Australia'), ('Paris', 'France')):
            L.append(('both', 'place', k) + tuple(f))
    # the full file's wider areas: a ring of tiles round each window, a few far places, every band
    wide = set()
    for _, ts, _ in windows[:3]:
        las = [t // 360 - 90 for t in ts]; los = [t % 360 - 180 for t in ts]
        for a in range(min(las) - RING, max(las) + RING + 1):
            for o in range(min(los) - RING, max(los) + RING + 1):
                wide.add(tile_id(a, o))
    wide |= {tile_id(a, o) for a, o in SPECIAL}
    for t in sorted(wide - set(tiles)):
        tile_lines(E, 'full', t, L)
    for b in range(180):
        for name in FAR:
            L.append(('full', 'tile', name, b, int(E.layers[name]['entries'][b]), sha(E.grid(name, b).tobytes())))
    for n, ts, _ in windows[:3]:
        las = [t // 360 - 90 for t in ts]; los = [t % 360 - 180 for t in ts]
        box = [tile_id(a, o) for a in range(min(las) - RING, max(las) + RING + 1) for o in range(min(los) - RING, max(los) + RING + 1)]
        st, un = segments(E, box)
        L.append(('full', 'segments', n + ' and ring', st, un) + tuple(box))
    with open(os.path.join(outdir, 'earth-1-fixture.expect'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('# What tools/earth/earthread.py reads from earth-1.bin and from EarthCheck\'s fixture cut from it;\n'
                '# written by tools/earth/fixture.py, read by EarthCheck. Tab-separated: scope, kind, fields.\n')
        for row in L:
            f.write('\t'.join(str(x) for x in row) + '\n')
    print('fixture %d bytes, sha256 %s; %d tiles, %d bands; %d expect lines' % (len(fixture), sha(fixture), len(tiles), len(bands), len(L)))


if __name__ == '__main__':
    main()
