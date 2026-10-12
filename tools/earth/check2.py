# The earth-2 checks: earth-1's layers byte for byte; the new layers' rules (FORMAT-2.txt) on every tile; and named
# places against the study's measurements (docs/studies/spec-earth2-terrain.md). Reads earth-2.bin through
# earthread.py, so the encoding is checked too. Writes <E2>/out/check2-report.txt; exit code 1 on a failure.
#   python3 -I check2.py [path to earth-2.bin]
import sys, os, json, math, hashlib
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
import terrain as T, depth as D
from bake2 import unpack_slope
from earthread import Earth

out = []; fails = [0]


def say(*a):
    s = ' '.join(str(x) for x in a); out.append(s); print(s, flush=True)


def check(label, ok, detail=''):
    say('%s  %s%s' % ('ok  ' if ok else 'FAIL', label, (' - ' + detail) if detail else ''))
    if not ok: fails[0] += 1


def cell(lat, lon):
    la0, lo0 = math.floor(lat), math.floor(lon)
    return (la0 + 90) * 360 + lo0 + 180, min(239, int((la0 + 1 - lat) * 240)), min(239, int((lon - lo0) * 240))


def shares(E, lat, lon, h=0):
    """(flat, foothill, steep, very steep) land shares round a point (a (2h+1) square of 15" cells, land-weighted)."""
    t, r, c = cell(lat, lon)
    sg = E.grid('slope', t); k = 240 // sg.shape[0]
    s = np.repeat(np.repeat(sg, k, axis=0), k, axis=1)
    L = 1 - (E.grid('sea', t).astype(float) + E.grid('lake', t)) / 15
    sl = (slice(max(0, r - h), r + h + 1), slice(max(0, c - h), c + h + 1))
    F, S, V = unpack_slope(s[sl]); w = L[sl]
    W = max(w.sum(), 1e-9); f, st, vs = (F * w).sum() / W, (S * w).sum() / W, (V * w).sum() / W
    return np.round([1 - f, f - st, st - vs, vs], 3)


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else os.path.join(T.OUT2, 'earth-2.bin')
    E = Earth(path); E1 = Earth(T.EARTH1)
    m = E.meta()
    say('earth-2 check of', path, os.path.getsize(path), 'bytes')
    check('the meta layer names the file earth-2', m.get('name') == 'earth-2')
    want = open(os.path.join(T.HERE, 'earth-1.sha256')).read().split()[0]
    check('it was made from the shipped earth-1', m.get('from_sha256') == want)
    # 1. earth-1's layers, blob by blob
    for name, L1 in E1.layers.items():
        if name == 'meta': continue
        L2 = E.layers.get(name)
        if L2 is None:
            check('layer %s is there' % name, False); continue
        same = L1['kind'] == L2['kind'] and L1['cpd'] == L2['cpd'] and L1['data_len'] == L2['data_len']
        if same and L1['kind'] != 2:
            same = np.array_equal(L1['entries'], L2['entries'])
        if same:
            E1.f.seek(L1['data_off']); a = E1.f.read(L1['data_len']); E.f.seek(L2['data_off']); b = E.f.read(L2['data_len'])
            same = hashlib.sha256(a).digest() == hashlib.sha256(b).digest()
        check('earth-1\'s %s is copied byte for byte' % name, same)
    # 2. the new layers' rules on every tile
    bad_order = bad_v = bad_shsea = bad_dsea = bad_lm = bad_slope_water = 0
    n_land = n_sea = 0
    for t in range(64800):
        sea = E.grid('sea', t); lake = E.grid('lake', t)
        s = E.grid('slope', t); F, S, V = unpack_slope(s)
        bad_order += int((S > F + 1e-9).sum()); bad_v += int((V > S + 1e-9).sum())
        k = 240 // s.shape[0]
        water = ((sea.astype(int) + lake) >= 15).reshape(s.shape[0], k, s.shape[1], k).all(axis=(1, 3))
        bad_slope_water += int((s > 0)[water].sum())
        sh = E.grid('shallow', t)
        bad_shsea += int(((sh > 0) & (sea == 0)).sum())
        d = E.grid('depth', t)
        k = 240 // d.shape[0]
        sea_any = sea.reshape(d.shape[0], k, d.shape[1], k).max(axis=(1, 3)) > 0
        bad_dsea += int(((d > 0) & ~sea_any).sum())
        if 'landmass' in E.layers:
            lm = E.landmass(t)
            land = (sea.astype(int) + lake) <= 7
            bad_lm += int(((lm > 0) & ~land).sum())
        n_land += int((~water).sum()); n_sea += int((sea > 0).sum())
    check('slope: the share at or above steep is never more than at or above foothill', bad_order == 0, '%d cells' % bad_order)
    check('slope: the share at or above very steep is never more than at or above steep', bad_v == 0, '%d cells' % bad_v)
    check('slope: no slope on a cell that is all water', bad_slope_water == 0, '%d cells' % bad_slope_water)
    check('shallow: no shallow share where the cell has no sea', bad_shsea == 0, '%d cells' % bad_shsea)
    check('depth: no depth where the 1\' cell has no sea', bad_dsea == 0, '%d cells' % bad_dsea)
    if 'landmass' in E.layers:
        check('landmass: no id on a cell that is not land', bad_lm == 0, '%d cells' % bad_lm)
    # 3. places
    say('--- places ---')
    med = shares(E, 6.245, -75.575, 30)
    say('Medellin, 15 km round the centre: flat, foothill, steep, very steep', med)
    floor = shares(E, 6.25, -75.575, 1)
    check('Medellin\'s valley floor (Parque Berrio, 6.25 N 75.575 W, 3 x 3 cells) is mostly flat', floor[0] >= 0.5, str(floor))
    ev = shares(E, 27.95, 86.90, 20)
    check('the Himalaya round Everest is mostly steep or very steep', ev[2] + ev[3] >= 0.5, str(ev))
    rot = shares(E, 51.92, 4.48, 30)
    check('Rotterdam is flat', rot[0] >= 0.97, str(rot))
    hel = shares(E, 60.17, 24.94, 30)
    say('Helsinki (60.17 N, in NASADEM\'s row at 60-61 N):', hel, 'estimated tile' if E.slope_est(cell(60.17, 24.94)[0]) else 'measured')
    tr = shares(E, 63.43, 10.39, 30)
    say('Trondheim (63.43 N, an estimate):', tr)
    check('north of 61 N is marked estimated (Trondheim), south of it measured (Helsinki, Oslo)', E.slope_est(cell(63.43, 10.39)[0]) and not E.slope_est(cell(60.17, 24.94)[0]) and not E.slope_est(cell(59.9, 10.75)[0]))
    # depth and shallow at Halifax
    for name, lat, lon, lo_, hi_ in [('the Narrows under the Macdonald Bridge', 44.6637, -63.5846, 1, 30),
                                     ('Bedford Basin', 44.6935, -63.640, 35, 80),
                                     ('the North Sea 40 km west of Hoek van Holland', 51.98, 3.54, 15, 40)]:
        t, r, c = cell(lat, lon)
        d = float(D.decode_depth(E.grid('depth', t)[r // 4, c // 4]))
        say('%s: depth %.0f m (1\' cell), shallow share %d/15, sea %d/15' % (name, d, E.grid('shallow', t)[r, c], E.grid('sea', t)[r, c]))
        check('%s is %d to %d m deep' % (name, lo_, hi_), lo_ <= d <= hi_)
    t, r, c = cell(44.6637, -63.5846)
    nar = E.grid('shallow', t)[r - 2:r + 3, c - 2:c + 3]; seaw = E.grid('sea', t)[r - 2:r + 3, c - 2:c + 3]
    check('the Narrows\' sea cells at the Macdonald Bridge are mostly shallow', (nar[seaw >= 8] >= 8).mean() >= 0.5 if (seaw >= 8).any() else False)
    if 'landmass' in E.layers:
        say('--- landmasses ---')
        tab = E.raw('landmasses').decode('utf-8').splitlines()
        def lm(lat, lon):
            t, r, c = cell(lat, lon); return int(E.landmass(t)[r, c])
        for nm, la, lo in [('Paris', 48.86, 2.35), ('Beijing', 39.9, 116.4), ('Denver', 39.74, -104.99), ('Sydney', -33.87, 151.0),
                           ('Halifax peninsula', 44.648, -63.580), ('Dartmouth', 44.672, -63.565), ('Bedford', 44.73, -63.66),
                           ('McNabs Island', 44.612, -63.525), ('Buenos Aires', -34.6, -58.45), ('Cairo', 30.05, 31.25)]:
            k = lm(la, lo); row = tab[k - 1].split('\t') if k > 0 else ['', '', '']
            say('  %-18s landmass %d: %s km2, %s cells, named %r' % (nm, k, row[0], row[1], row[2]))
        check('Paris and Beijing are on landmass 1', lm(48.86, 2.35) == 1 and lm(39.9, 116.4) == 1)
        check('Cairo is on landmass 1 and Buenos Aires on Denver\'s (Suez and Panama do not cut)', lm(30.05, 31.25) == 1 and lm(-34.6, -58.45) == lm(39.74, -104.99) == 2)
        check('Halifax peninsula and Dartmouth are one landmass (round the head of Bedford Basin)', lm(44.648, -63.58) == lm(44.672, -63.565) != 0)
        check('McNabs Island is a landmass of its own', lm(44.612, -63.525) not in (0, lm(44.648, -63.58)))
    for name, L in E.layers.items():
        say('  layer %-14s %10d bytes' % (name, L['index_len'] + L['data_len']))
    say('%d failure(s)' % fails[0])
    os.makedirs(T.OUT2, exist_ok=True)
    with open(os.path.join(T.OUT2, 'check2-report.txt'), 'w') as f:
        f.write('\n'.join(out) + '\n')
    sys.exit(1 if fails[0] else 0)


if __name__ == '__main__':
    main()
