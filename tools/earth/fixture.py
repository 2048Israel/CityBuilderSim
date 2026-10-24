# EarthCheck's fixture, cut from earth-2.bin (batch E1, 2026-10-10, from earth-1.bin; batch ET1, 2026-10-24, from
# earth-2.bin). Two files, named for the file they are cut from (its meta layer's name):
#   - earth-2-fixture.bin: the file's own layout (FORMAT.txt, FORMAT-2.txt) holding only the tiles EarthCheck reads -
#     the three windows the study drew (Rotterdam, Perth, Paris), Fiji's four on the 180th meridian, Halifax's
#     Narrows and Medellin's valley, every layer's blobs copied byte for byte, every other tile's index entry 0
#     (so it reads as uniform 0 or empty: it is not in the fixture), the whole-file layers copied, and the meta
#     given a "fixture" key;
#   - earth-2-fixture.expect: what earthread.py, this folder's reference reader, reads from those tiles, from the
#     fixture and from the full file - for EarthCheck to hold the game's reader (EarthData) to, figure for figure.
#     A line is tab-separated: scope (both = true of the fixture and the full file alike, fixture, full), kind,
#     fields. EarthCheck reads every line; the full file's lines run when the file is named (-Dcbs.earth=<path>).
#     The Narrows and Medellin's valley are "narrows" lines, not "window"s: the other harnesses count the
#     study's four windows.
#   - given earth-1.bin the same script cuts earth-1-fixture.* as it did (no Narrows, none of the new layers).
#   PYTHONPATH="$(cygpath -w ~/.cache/cbs-ensemble/py)" python -B tools/earth/fixture.py <earth-2.bin> src/main/resources/earth
#   (-B: no __pycache__ in the tree; numpy is where PYTHONPATH says, so not -I.)
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
# Halifax's Narrows, 44.60-44.72 N and 63.50-63.70 W: a strait a few hundred metres wide between Halifax and Dartmouth
# (FORMAT-2.txt: one landmass, the sea there shallow). Only an earth-2 file has it.
NARROWS = ('Halifax Narrows', 44.72, -63.70, 29, 48)
# Medellin's valley, 6.10-6.40 N and 75.73-75.43 W (one 1-degree tile): the valley floor flat and its hillsides steep,
# for the relief plane (batch ET1, slice B2: EarthTerrainCheck 17). Only an earth-2 file has it.
MEDELLIN = ('Medellin valley', 6.40, -75.73, 72, 72)
# ...with its 15" tiles alone, not the 4' band it lies in: a band added would move every far node the other harnesses
# hold to the bytes of 0.7.131 (GlobeCheck 4's whole-world node reads every band the fixture has).
BANDLESS = {MEDELLIN[0]}
FIJI = [(-17, 179), (-17, -180), (-18, 179), (-18, -180)]   # the 180th meridian (spec-earth 2: no seam)
FIJI_SPOT = (-17.0, 180.0)
RING = 2                                    # the full file's wider areas: 2 degrees round each window
SPECIAL = [(89, 0), (89, 179), (-90, 0), (-80, 0), (0, -150), (-1, -50), (41, 50), (30, 31), (45, -84)]
SHARES = ['sea', 'lake', 'landcover']       # earth-1's 1-degree share layers
NEW_SHARES = ['slope', 'shallow', 'depth']  # earth-2's byte grids (the cells a degree are each layer's own)
FAR = ['far.land', 'far.lake']              # the latitude bands
VALUES_A_TILE = 16                          # cells a tile and layer: the first cell of each value, up to this many
# named points the full file's lines read (slope byte, shallow, depth code, landmass id, whether the slope is estimated)
POINTS = [('Everest', 27.9881, 86.9250), ('Prince Edward Island', 46.40, -63.25), ('Mariana Trench', 11.35, 142.20),
          ('Medellin', 6.2442, -75.5812), ('Antarctica', -80.0, 0.5), ('Tokyo', 35.6762, 139.6503),
          ('North Greenland', 75.0, -40.0), ('Sahara', 23.0, 10.0), ('Open Pacific', 0.5, -150.5),
          ('Confederation Strait', 46.2, -63.7)]


def tile_id(lat0, lon0):
    return (lat0 + 90) * 360 + (lon0 + 180)


def sha(b):
    return hashlib.sha256(b).hexdigest()


def sha_file(path):
    h = hashlib.sha256()
    with open(path, 'rb') as f:
        for chunk in iter(lambda: f.read(1 << 20), b''):
            h.update(chunk)
    return h.hexdigest()


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


def cut(E, name, full_sha, full_bytes, tiles, bands, path):
    """The fixture file: the same header and layers, only these tiles' and bands' blobs."""
    order = list(E.layers)
    meta = E.meta()
    meta['fixture'] = dict(of=name + ':' + full_sha, bytes_of=full_bytes, tiles=sorted(tiles), bands=sorted(bands),
                           made='tools/earth/fixture.py (batch E1, 2026-10-10; from earth-2 by batch ET1, 2026-10-24)',
                           note='EarthCheck\'s fixture: only these tiles and bands hold their blobs; every other '
                                'index entry is 0, so it reads as uniform 0 or empty')
    parts = {}
    for lname in order:
        L = E.layers[lname]
        if L['kind'] == 2:
            blob = zlib.compress(json.dumps(meta, indent=1, sort_keys=True).encode('utf-8'), LEVEL) if lname == 'meta' \
                else whole(E, lname)
            parts[lname] = (b'', [blob])
            continue
        n = TILES if L['kind'] == 0 else 180
        keep = tiles if L['kind'] == 0 else bands
        e = np.zeros(n, '>i4'); blobs = []
        for i in sorted(keep):
            e[i] = int(L['entries'][i])
            b = compressed(E, lname, i)
            if b is not None:
                blobs.append(b)
        parts[lname] = (zlib.compress(e.tobytes(), LEVEL), blobs)

    def header(offsets):
        h = io.BytesIO()
        h.write(b'EARTH-1\n'); h.write(struct.pack('>ii', E.version, len(order)))
        for lname in order:
            L = E.layers[lname]; nb = lname.encode('utf-8')
            h.write(struct.pack('>H', len(nb))); h.write(nb)
            h.write(struct.pack('>biqiqq', L['kind'], L['cpd'], *offsets.get(lname, (0, 0, 0, 0))))
        return h.getvalue()
    pos = len(header({})); offsets = {}
    for lname in order:
        idx, blobs = parts[lname]
        io_ = pos; pos += len(idx); do = pos; dl = sum(len(b) for b in blobs); pos += dl
        offsets[lname] = (io_, len(idx), do, dl)
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


def grids(E):
    """The byte-grid share layers this file has: earth-1's, and earth-2's slope, shallow and depth."""
    return [n for n in SHARES + NEW_SHARES if n in E.layers]


def slope_shares(b):
    """FORMAT-2.txt's slope byte: the shares at or above foothill (F), steep (S) and very steep (V) of the cell's land."""
    f = (b & 7) / 7; s = ((b >> 3) & 7) / 7
    return f, s, s * (b >> 6) / 3


def depth_m(code):
    """FORMAT-2.txt's depth code in metres: 0 none, 1..200 metres, 201..254 200 + 10 x (code - 200), 255 750 or deeper."""
    if code == 0:
        return 0.0
    if code <= 200:
        return float(code)
    if code <= 254:
        return float(200 + 10 * (code - 200))
    return 750.0


def tile_lines(E, scope, t, out):
    for name in grids(E):
        out.append((scope, 'tile', name, t, int(E.layers[name]['entries'][t]), sha(E.grid(name, t).tobytes())))
    for name in ('lakes.small', 'rivers') + (('landmass',) if 'landmass' in E.layers else ()):
        b = E.raw(name, t)
        out.append((scope, 'tile', name, t, int(E.layers[name]['entries'][t]), '-' if b is None else sha(b)))
    runs = E.rivers(t)
    out.append((scope, 'rivers', t, len(runs), sum(len(a) for a in runs), sha(river_ints(runs).encode())))
    lk = E.small_lakes(t)
    out.append((scope, 'lakes', t, len(lk), sha(lake_ints(lk).encode())))
    if 'landmass' in E.layers:
        out.append((scope, 'slopeest', t, int(E.slope_est(t))))
        ids = E.landmass(t)
        uniq = np.unique(ids)
        out.append((scope, 'landmasstile', t, len(uniq), sha(uniq.astype('>u4').tobytes()), sha(ids.astype('>u4').tobytes())))


def cell_at(cpd, lat, lon):
    """The cell holding a point, as EarthData finds it: rows counted from 90 N and columns from 180 W, a cell holding
    its north and west edges (so a point on a whole degree of latitude is in the tile south of it). Returns the tile's
    south-west corner (lat0, lon0) and the cell's row and column within it."""
    g = min(int(math.floor((90 - lat) * cpd)), 180 * cpd - 1)
    c = int(math.floor(((lon + 180) % 360) * cpd)) % (360 * cpd)
    return 89 - g // cpd, c // cpd - 180, g % cpd, c % cpd


def pick(vals, spread):
    """The values a tile's cells are read at: the smallest VALUES_A_TILE of them, or that many spread over the lot."""
    if len(vals) <= VALUES_A_TILE:
        return vals
    if not spread:
        return vals[:VALUES_A_TILE]
    return vals[np.unique(np.linspace(0, len(vals) - 1, VALUES_A_TILE).round().astype(int))]


def first_of_each(g, cols=None, spread=False):
    """(r, c) of the first cell in raster order of each value the grid holds (within the columns, if given)."""
    sub = g if cols is None else g[:, cols[0]:cols[1]]
    off = 0 if cols is None else cols[0]
    cells = []
    for v in pick(np.unique(sub), spread):
        r, c = np.argwhere(sub == v)[0]
        cells.append((int(r), int(c) + off, int(v)))
    return cells


def point_line(E, scope, name, la, lo, out):
    """What the file holds at a point, a layer at a time, each at its own cell: slope byte, shallow, depth code, landmass
    id, and whether the tile's slope is an estimate."""
    vals = []
    for lname in NEW_SHARES:
        a, o, r, c = cell_at(E.layers[lname]['cpd'], la, lo)
        vals.append(int(E.grid(lname, tile_id(a, o))[r, c]))
    a, o, r, c = cell_at(E.layers['landmass']['cpd'], la, lo)
    lm = int(E.landmass(tile_id(a, o))[r, c])
    a, o, _, _ = cell_at(1, la, lo)
    out.append((scope, 'point', name, repr(la), repr(lo)) + tuple(vals) + (lm, int(E.slope_est(tile_id(a, o)))))


def main():
    src, outdir = sys.argv[1], sys.argv[2]
    E = Earth(src)
    name = E.meta()['name']                 # earth-1 or earth-2: the file's own name, as EarthData's identity() has it
    relief = 'slope' in E.layers
    full_sha = sha_file(src); full_bytes = os.path.getsize(src)
    windows = [(n, 'window', window_tiles(a, o, r, c), (a - r / 480, o + c / 480)) for n, a, o, r, c in WINDOWS]
    windows.append(('Fiji', 'window', [tile_id(a, o) for a, o in FIJI], FIJI_SPOT))
    if relief:
        n, a, o, r, c = NARROWS
        windows.append((n, 'narrows', window_tiles(a, o, r, c), (a - r / 480, o + c / 480)))
        n, a, o, r, c = MEDELLIN
        windows.append((n, 'narrows', window_tiles(a, o, r, c), (a - r / 480, o + c / 480)))
    tiles = sorted({t for _, _, ts, _ in windows for t in ts})
    bands = sorted({t // 360 for n, _, ts, _ in windows if n not in BANDLESS for t in ts})
    os.makedirs(outdir, exist_ok=True)
    fixture_path = os.path.join(outdir, name + '-fixture.bin')
    fixture = cut(E, name, full_sha, full_bytes, tiles, bands, fixture_path)
    F = Earth(fixture_path)

    L = []
    L.append(('fixture', 'file', sha(fixture), len(fixture)))
    L.append(('full', 'file', full_sha, full_bytes))
    L.append(('fixture', 'of', name + ':' + full_sha))
    for scope, X in (('fixture', F), ('full', E)):
        for lname, Y in X.layers.items():
            L.append((scope, 'table', lname, Y['kind'], Y['cpd'], Y['index_off'], Y['index_len'], Y['data_off'], Y['data_len']))
            if Y['kind'] != 2:
                L.append((scope, 'index', lname, sha(Y['entries'].astype('>i4').tobytes())))
        L.append((scope, 'whole', 'meta', sha(X.raw('meta'))))
    for lname in ('geology', 'provinces', 'places') + (('slope.est', 'landmasses') if relief else ()):
        L.append(('both', 'whole', lname, sha(E.raw(lname))))
    lm_ids = set()
    # the windows: their tiles' bytes and decoded lists, cells of every value, the spot's cells
    for wname, wkind, ts, (sla, slo) in windows:
        L.append(('both', wkind, wname, repr(sla), repr(slo)) + tuple(ts))
        for t in ts:
            tile_lines(E, 'both', t, L)
            for lname in grids(E):
                g = E.grid(lname, t); cpd = E.layers[lname]['cpd']
                for r, c, v in first_of_each(g, spread=lname in ('slope', 'depth')):
                    L.append(('both', 'cell', lname, t, r, c, v))
                a, o, r, c = cell_at(cpd, sla, slo)
                if tile_id(a, o) == t:
                    L.append(('both', 'spot', lname, repr(sla), repr(slo), t, r, c, int(g[r, c])))
            for k, a in enumerate(E.rivers(t)[:3]):
                for j in sorted({0, len(a) // 2, len(a) - 1}):
                    L.append(('both', 'vertex', t, k, j) + tuple(repr(float(x)) for x in a[j]))
            lk = E.small_lakes(t)
            for j in sorted({0, len(lk) // 2, len(lk) - 1}) if len(lk) else []:
                L.append(('both', 'lake', t, j) + tuple(repr(float(x)) for x in lk[j]))
            if relief:
                # the new layers through their own readers' decoding: slope shares, shallow share, depth in metres, landmass id
                for r, c, v in first_of_each(E.grid('slope', t), spread=True):
                    f, s, vs = slope_shares(v)
                    L.append(('both', 'slopeb', t, r, c, v, repr(f), repr(s), repr(vs)))
                for r, c, v in first_of_each(E.grid('shallow', t)):
                    L.append(('both', 'shallowb', t, r, c, v, repr(v / 15)))
                for r, c, v in first_of_each(E.grid('depth', t), spread=True):
                    L.append(('both', 'depthb', t, r, c, v, repr(depth_m(v))))
                ids = E.landmass(t)
                lm_ids |= set(int(x) for x in np.unique(ids))
                for r, c, v in first_of_each(ids):
                    L.append(('both', 'landmasscell', t, r, c, v))
        if relief:
            point_line(E, 'both', wname, sla, slo, L)
        st, un = segments(E, ts)
        L.append(('both', 'segments', wname, st, un))
        for b in sorted({t // 360 for t in ts if wname not in BANDLESS}):
            lat0 = b - 90
            lons = sorted({t % 360 - 180 for t in ts})
            for lname in FAR:
                g = E.grid(lname, b); cpd = E.layers[lname]['cpd']
                L.append(('both', 'tile', lname, b, int(E.layers[lname]['entries'][b]), sha(g.tobytes())))
                c0 = (lons[0] + 180) * cpd; c1 = (lons[-1] + 181) * cpd
                for r, c, v in first_of_each(g, (c0, c1)):
                    L.append(('both', 'cell', lname, b, r, c, v))
                a, o, r, c = cell_at(cpd, sla, slo)
                if a + 90 == b:
                    c = (o + 180) * cpd + c
                    L.append(('both', 'spot', lname, repr(sla), repr(slo), b, r, c, int(g[r, c])))
    for c in range(256):
        L.append(('both', 'area', c, repr(0.1 * 2 ** (c / 8))))
    if relief:
        for c in range(256):
            L.append(('both', 'depthcode', c, repr(depth_m(c))))
        rows = [x for x in E.raw('landmasses').decode('utf-8').split('\n') if x]
        L.append(('both', 'landmasses', len(rows)))
        for i in sorted(lm_ids - {0}):
            f = rows[i - 1].split('\t')
            L.append(('both', 'landmass', i, f[0], f[1], f[2]))
        for pname, la, lo in POINTS:
            point_line(E, 'full', pname, la, lo, L)
    # geology and the provinces it names: the spots, the windows' corners, and the Yilgarn shield east of Perth
    geo = np.frombuffer(E.raw('geology'), '>u2').reshape(720, 1440)
    pts = [(n, a, o) for n, _, _, (a, o) in windows]
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
    for _, _, ts, _ in windows[:3]:
        las = [t // 360 - 90 for t in ts]; los = [t % 360 - 180 for t in ts]
        for a in range(min(las) - RING, max(las) + RING + 1):
            for o in range(min(los) - RING, max(los) + RING + 1):
                wide.add(tile_id(a, o))
    wide |= {tile_id(a, o) for a, o in SPECIAL}
    for t in sorted(wide - set(tiles)):
        tile_lines(E, 'full', t, L)
    for b in range(180):
        for lname in FAR:
            L.append(('full', 'tile', lname, b, int(E.layers[lname]['entries'][b]), sha(E.grid(lname, b).tobytes())))
    for n, _, ts, _ in windows[:3]:
        las = [t // 360 - 90 for t in ts]; los = [t % 360 - 180 for t in ts]
        box = [tile_id(a, o) for a in range(min(las) - RING, max(las) + RING + 1) for o in range(min(los) - RING, max(los) + RING + 1)]
        st, un = segments(E, box)
        L.append(('full', 'segments', n + ' and ring', st, un) + tuple(box))
    with open(os.path.join(outdir, name + '-fixture.expect'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('# What tools/earth/earthread.py reads from %s.bin and from EarthCheck\'s fixture cut from it;\n'
                '# written by tools/earth/fixture.py, read by EarthCheck. Tab-separated: scope, kind, fields.\n' % name)
        for row in L:
            f.write('\t'.join(str(x) for x in row) + '\n')
    print('fixture %d bytes, sha256 %s; %d tiles, %d bands; %d expect lines' % (len(fixture), sha(fixture), len(tiles), len(bands), len(L)))


if __name__ == '__main__':
    main()
