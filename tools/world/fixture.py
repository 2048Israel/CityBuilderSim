"""Cuts world-1's fixture and its expect file (batch WN0, slice G, 2026-10-28) from the baked world-1.bin:
src/main/resources/world/world-1-fixture.bin, the file WorldDataCheck reads, and world-1-fixture.expect, what
worldread.py read from the fixture and from the full file, for the Java reader to be held to.

WHY: the full file is too large to commit and takes minutes to load, but the reader must be proved against real
data: twelve nations carry every kind of row (an island, a landlocked state, a union, two pegs, a currency that
changed), six owner tiles carry a real three-nation border, and the cut is arithmetic, so a mistake in the Java
shows as a number that differs. FORMAT.txt, THE FIXTURE and THE EXPECT FILE, is the contract.
Usage: python -I fixture.py [--full path] [--out-dir dir] [--econ dir]
A layer the full file lacks (a trial bake with --partial) is left out of the fixture, with a warning.
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import collections
import math
import random
import zlib

import common
import worldread as wr

# The fixture's owner window: the tiles with south-west corners 51..52 N and 3..5 E (51-53 N, 3-6 E).
WINDOW = [(lat0, lon0) for lat0 in (51, 52) for lon0 in (3, 4, 5)]
# Tiles the expect file lists besides the window's, all uniform in the fixture: (lat0, lon0).
UNIFORM_TILES = [(0, 0), (-33, 150), (60, -100), (35, 105), (48, 2), (36, 138), (4, -75), (46, 8), (38, -100),
                 (-90, 0)]
# The expect file's four nations for the paths, and the years it checks them at.
PATH_NATIONS = ['CHE', 'CHN', 'NLD', 'USA']
PATH_YEARS = [2000, 2015, 2025, 2100]
# The years the expect file checks each good's price at.
PRICE_YEARS = [2000, 2010, 2025]
# Months the expect file checks each money's rate at.
RATE_MONTHS = [0, 100, 311]
# How many of each the expect file lists: cells of the window, freight pairs, flows and nations of a good.
CELLS = 40
FREIGHT_PAIRS = 20
FLOWS_A_GOOD = 3
MAKES_A_GOOD = 3
FIRST_LINES = 5
# The seed for the cells' draw: the same file always gives the same expect lines.
SEED = 20261028


def window_tiles():
    return [common.tile_id(a, b) for a, b in WINDOW]


def money_codes(rows):
    """The money codes a set of nations' rows name: the money, its history and its regime's money."""
    codes = []
    for r in rows:
        for part in [r['money']] + r['money_history'].split(',') + [r['regime']]:
            if part in ('-', ''):
                continue
            c = part.split(':')[1] if part.startswith(('peg:', 'uses:')) else part.split(':')[0]
            if len(c) == 3 and c.isalpha() and c not in codes:
                codes.append(c)
    return codes


def build_fixture(full, sha):
    """The fixture's layers cut from a WorldFile; returns (layers, notes)."""
    twelve = common.FIXTURE_NATIONS
    num = {c: common.number(c) for c in twelve}
    fmap = {num[c]: i + 1 for i, c in enumerate(twelve)}   # full number -> fixture number
    layers, notes = [], []
    h, rows = full.nations()
    byiso = {r[1]: dict(zip(h, r)) for r in rows}
    nrows = []
    for i, c in enumerate(twelve):
        r = byiso[c]
        r = dict(r, number=str(i + 1))
        nrows.append(r)
    ncols = h
    keep_money = money_codes(nrows) if 'money' in ncols else []
    mt = None
    if full.has('moneys'):
        m = full.moneys()
        ix = [i for i, c in enumerate(m['codes']) if c in keep_money]
        missing = [c for c in keep_money if c not in m['codes']]
        assert not missing, 'moneys the twelve name but the file lacks: %s' % missing
        mt = dict(codes=[m['codes'][i] for i in ix], names=[m['names'][i] for i in ix], first_month=m['first_month'],
                  months=m['months'], rate=[m['rate'][i] for i in ix], mark=[m['mark'][i] for i in ix])
    meta = full.meta()
    ngoods = 0
    if full.has('goods'):
        gh, grows = full.goods()
        ngoods = len(grows)
    mrows = [['key', 'fields'], ['name', 'world-1-fixture'], ['made', wr.meta_value(meta, 'made') or '-'],
             ['of', 'world-1:' + sha], ['base_year', wr.meta_value(meta, 'base_year') or str(common.BASE_YEAR)],
             ['nations', str(len(twelve))], ['areas', '0'], ['moneys', str(len(mt['codes']) if mt else 0)],
             ['goods', str(ngoods)]]
    mrows += [r for r in meta if r[0] in ('dial', 'source')]
    layers.append(common.text_layer('meta', '\n'.join('\t'.join(r) for r in mrows) + '\n'))
    layers.append(common.Layer('nations', 2, payload=wr.encode_table(ncols, [[r[c] for c in ncols] for r in nrows])))
    if mt:
        layers.append(common.bytes_layer('moneys', wr.encode_moneys(mt)))
    else:
        notes.append('moneys')
    if full.has('paths'):
        p = full.paths()
        y = p['years']
        value, mark = [], []
        for s in range(len(p['series'])):
            v, k = [], []
            for c in twelve:
                a = (num[c] - 1) * y
                v += p['value'][s][a:a + y]
                k += p['mark'][s][a:a + y]
            value.append(v)
            mark.append(k)
        layers.append(common.bytes_layer('paths', wr.encode_paths(dict(
            n=len(twelve), first_year=p['first_year'], years=y, series=p['series'], value=value, mark=mark))))
    else:
        notes.append('paths')
    if full.has('goods'):
        layers.append(common.Layer('goods', 2, payload=full.payload('goods')))
    else:
        notes.append('goods')
    if full.has('trade'):
        t = full.trade()
        pairs, make, use = [], [], []
        for gi in range(t['g']):
            kept = [(fmap[s], fmap[b], f, tn, c, pf, mk) for s, b, f, tn, c, pf, mk in t['pairs'][gi]
                    if s in fmap and b in fmap]
            mk_, us_ = wr.sums_of(len(twelve), kept)
            pairs.append(kept)
            make.append(mk_)
            use.append(us_)
        layers.append(common.bytes_layer('trade', wr.encode_trade(dict(
            n=len(twelve), g=t['g'], base_year=t['base_year'], pairs=pairs, make=make, use=use))))
    else:
        notes.append('trade')
    if full.has('freight'):
        f = full.freight()
        n = f['n']
        idx = [num[c] - 1 for c in twelve]
        cut = {k: [f[k][i * n + j] for i in idx for j in idx] for k in ('sea', 'land', 'cost')}
        edges = [(i + 1, j + 1, cut['sea'][i * 12 + j], cut['land'][i * 12 + j], 4)
                 for i in range(12) for j in range(i + 1, 12)]
        layers.append(common.bytes_layer('freight', wr.encode_freight(dict(n=12, edges=edges, **cut))))
    else:
        notes.append('freight')
    # the owner raster: the window's six tiles as blobs, every other tile uniform
    table = bytes([fmap.get(i, 0) for i in range(256)])
    entries, blobs = [0] * common.TILES, []
    win = set(window_tiles())
    fe = full.entries('owner')
    for tid in range(common.TILES):
        e = fe[tid]
        if tid in win:
            grid = full.owner_grid(tid)
            stray = sorted(set(grid) - {0} - set(fmap))
            assert not stray, 'owners outside the twelve in the window tile %d: %s' % (tid, stray)
            blob = zlib.compress(grid.translate(table), common.ZLIB_LEVEL)
            entries[tid] = len(blob)
            blobs.append(blob)
        elif e <= 0:
            entries[tid] = -fmap.get(-e, 0)
        else:
            cnt = collections.Counter(full.tile('owner', tid))
            top = max(cnt.items(), key=lambda kv: (kv[1], -kv[0]))[0]
            entries[tid] = -fmap.get(top, 0)
    layers.append(common.tile_layer('owner', full.layers['owner']['cpd'], entries, blobs))
    if full.has('lines'):
        ls = full.lines()
        lat_lo, lat_hi = 51 * common.LINE_SCALE, 53 * common.LINE_SCALE
        lon_lo, lon_hi = 3 * common.LINE_SCALE, 6 * common.LINE_SCALE
        out = []
        for left, right, pts in ls:
            lats = [p[0] for p in pts]
            lons = [p[1] for p in pts]
            if min(lats) <= lat_hi and max(lats) >= lat_lo and min(lons) <= lon_hi and max(lons) >= lon_lo:
                out.append((fmap.get(left, 0), fmap.get(right, 0), pts))
        layers.append(common.bytes_layer('lines', wr.encode_lines(out)))
    else:
        notes.append('lines')
    if full.has('prices'):
        layers.append(common.bytes_layer('prices', full.payload('prices')))   # whole: it is the goods', not a nation's
    else:
        notes.append('prices')
    order = ['meta', 'nations', 'moneys', 'paths', 'goods', 'trade', 'freight', 'owner', 'lines', 'prices']
    layers.sort(key=lambda L: order.index(L.name))
    return layers, notes


# ===================================================================== the expect file
class View:
    """A world file seen by iso3: its numbering, for the expect lines of a scope."""

    def __init__(self, wf):
        self.wf = wf
        h, rows = wf.nations()
        self.nrows = [dict(zip(h, r)) for r in rows]
        self.number = {r['iso3']: int(r['number']) for r in self.nrows}
        self.kind = {r['iso3']: r['kind'] for r in self.nrows}
        self.cache = {}

    def get(self, what):
        if what not in self.cache:
            self.cache[what] = getattr(self.wf, what)()
        return self.cache[what]


def expect_lines(scope, v, sel, baci):
    """The expect lines of a scope (fixture or full) from a view and the selections drawn from the fixture."""
    L = []

    def add(*fields):
        L.append('\t'.join([scope] + [str(x) for x in fields]))

    def num(x):
        return repr(float(x)) if not isinstance(x, str) else x

    wf = v.wf
    add('file', common.sha256_file(wf.path), len(wf.data))
    if scope == 'fixture':
        add('of', wr.meta_value(wf.meta(), 'of'))
    for r in v.nrows:
        add('nation', r['number'], r['iso3'], r['kind'], r['money'], num(float(r['node_lat'])),
            num(float(r['node_lon'])), r['landlocked'])
    if wf.has('paths'):
        p = v.get('paths')
        for si, (name, _) in enumerate(p['series']):
            for c in sel['path_nations']:
                for y in PATH_YEARS:
                    k = (v.number[c] - 1) * p['years'] + (y - p['first_year'])
                    add('path', name, c, y, num(p['value'][si][k]), p['mark'][si][k])
    if wf.has('moneys'):
        m = v.get('moneys')
        for c in sel['moneys']:
            mi = m['codes'].index(c)
            for mo in RATE_MONTHS:
                add('rate', c, mo, num(m['rate'][mi][mo - m['first_month']]), m['mark'][mi][mo - m['first_month']])
    e = wf.entries('owner')
    for t in sel['tiles']:
        add('tile', t, e[t])
    for t, r, c in sel['cells']:
        add('cell', t, r, c, wf.owner_at(t, r, c))
    if wf.has('freight'):
        f = v.get('freight')
        n = f['n']
        for a, b in sel['freight']:
            k = (v.number[a] - 1) * n + (v.number[b] - 1)
            add('freight', a, b, num(f['sea'][k]), num(f['land'][k]), num(f['cost'][k]))
    if wf.has('trade') and wf.has('goods'):
        t = v.get('trade')
        goods = sel['goods']
        for gi, key in enumerate(goods):
            look = {(s, b): (fl, tn, co, pf) for s, b, fl, tn, co, pf, _ in t['pairs'][gi]}
            for s, b in sel['flows'][key]:
                fl, tn, co, pf = look[(v.number[s], v.number[b])]
                add('flow', key, s, b, num(fl), num(tn), num(co), num(pf))
            for c in sel['makes'][key]:
                add('make', key, c, num(t['make'][gi][v.number[c] - 1]), num(t['use'][gi][v.number[c] - 1]))
    if wf.has('lines'):
        ls = v.get('lines')
        add('lines', len(ls))
        for i, (left, right, pts) in enumerate(ls[:FIRST_LINES]):
            add('line', i, left, right, len(pts), pts[0][0], pts[0][1])
    if wf.has('prices') and wf.has('goods'):
        pr = v.get('prices')
        for gi, key in enumerate(sel['goods']):
            for y in PRICE_YEARS:
                k = y - pr['first_year']
                add('price', key, y, num(pr['value'][gi][k]), pr['mark'][gi][k])
    if scope == 'fixture':
        for key, s, b, val in baci:
            add('baci', key, s, b, num(float(val)))
    return L


def select(fx, baci_rows):
    """What the expect file lists, drawn from the fixture view, by fixed rules (so it never changes unless the data
    does): the tiles, the 40 cells, the freight pairs, the flows and nations of each good."""
    rnd = random.Random(SEED)
    twelve = common.FIXTURE_NATIONS
    sel = dict(path_nations=PATH_NATIONS)
    sel['tiles'] = window_tiles() + [common.tile_id(a, b) for a, b in UNIFORM_TILES]
    # the cells: round-robin over the owners present in the window, a random cell of each in turn
    cells_of = collections.defaultdict(list)
    for t in window_tiles():
        g = fx.wf.owner_grid(t)
        for i, o in enumerate(g):
            cells_of[o].append((t, i // 240, i % 240))
    owners = sorted(cells_of)
    for o in owners:
        rnd.shuffle(cells_of[o])
    cells = []
    k = 0
    while len(cells) < CELLS:
        o = owners[k % len(owners)]
        if cells_of[o]:
            cells.append(cells_of[o].pop())
        k += 1
    sel['cells'] = cells
    pairs = [(twelve[i], twelve[j]) for i in range(12) for j in range(i + 1, 12)]
    sel['freight'] = pairs[::3][:FREIGHT_PAIRS]
    assert len(sel['freight']) == FREIGHT_PAIRS
    sel['moneys'] = fx.get('moneys')['codes'] if fx.wf.has('moneys') else []
    sel['goods'], sel['flows'], sel['makes'] = [], {}, {}
    if fx.wf.has('trade') and fx.wf.has('goods'):
        t = fx.get('trade')
        gh, grows = fx.get('goods')
        inv = {n: c for c, n in fx.number.items()}
        for gi, row in enumerate(grows):
            key = row[1]
            sel['goods'].append(key)
            ps = sorted(t['pairs'][gi], key=lambda p: (-p[2], p[0], p[1]))
            own = [p for p in ps if p[0] == p[1]][:1]
            foreign = [p for p in ps if p[0] != p[1]][:FLOWS_A_GOOD - len(own)]
            sel['flows'][key] = [(inv[p[0]], inv[p[1]]) for p in own + foreign]
            order = sorted(range(12), key=lambda i: (-t['make'][gi][i], i))[:MAKES_A_GOOD]
            sel['makes'][key] = [inv[i + 1] for i in order]
    return sel


def read_baci(path):
    """E2's baci-samples.tsv: (good key, seller iso3, buyer iso3, value) rows."""
    if not os.path.exists(path):
        return None
    out = []
    with open(path, encoding='utf-8') as f:
        for ln in f.read().split('\n'):
            p = ln.rstrip('\r').split('\t')
            if len(p) < 4:
                continue
            try:
                float(p[3])
            except ValueError:
                continue   # the column names
            out.append((p[0], p[1], p[2], p[3]))
    return out


def main():
    p = common.parser('Cuts the world-1 fixture and its expect file.')
    p.add_argument('--full', default=None, help='the baked file, default <work>/out/world-1.bin')
    p.add_argument('--out-dir', default=None, help='default <repository>/src/main/resources/world')
    p.add_argument('--econ', default=None, help="E2's folder with baci-samples.tsv, default <work>/econ")
    a = p.parse_args()
    work = common.work_dir(a)
    here = os.path.dirname(os.path.abspath(__file__))
    out_dir = a.out_dir or os.path.normpath(os.path.join(here, '..', '..', 'src', 'main', 'resources', 'world'))
    full_path = a.full or work + '/out/world-1.bin'
    econ = a.econ or work + '/econ'
    full = wr.WorldFile(full_path)
    sha = common.sha256_file(full_path)
    common.log('fixture: cutting %s (%d bytes, %s)' % (full_path, len(full.data), sha))
    layers, notes = build_fixture(full, sha)
    os.makedirs(out_dir, exist_ok=True)
    fpath = out_dir + '/world-1-fixture.bin'
    fsha, sizes = common.write_container(fpath, layers)
    for L in layers:
        print('  %-8s index %7d bytes, data %9d bytes' % ((L.name,) + sizes[L.name]))
    common.log('fixture: wrote %s, %d bytes' % (fpath, os.path.getsize(fpath)))
    if notes:
        print('  WARNING: layers the full file lacks, left out of the fixture: ' + ' '.join(notes))
    fx = View(wr.WorldFile(fpath))
    fv = View(full)
    baci = read_baci(econ + '/baci-samples.tsv')
    if baci is None:
        print('  WARNING: no baci-samples.tsv in %s: the expect file has no baci lines' % econ)
        baci = []
    sel = select(fx, baci)
    lines = ['# world-1-fixture.expect: what worldread.py read from the fixture (scope fixture) and from the full file '
             '(scope full); FORMAT.txt, THE EXPECT FILE.',
             '# Columns are TAB-separated: scope kind fields. Doubles are Python repr(); nan is NaN. Owner numbers '
             'are the scope\'s own.',
             '# tile: the index entry (> 0 a blob\'s length in the file, <= 0 uniform at -entry). cell: tile, row, '
             'column, owner.',
             '# line: i, left, right, points, first latitude and first longitude as stored (degrees x 100000).',
             '# make: the nation\'s make and use of the good in thousand US$, summed over the scope\'s own pairs.']
    lines += expect_lines('fixture', fx, sel, baci)
    lines += expect_lines('full', fv, sel, baci)
    with open(out_dir + '/world-1-fixture.expect', 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(lines) + '\n')
    common.log('fixture: wrote world-1-fixture.expect: %d lines (%d fixture, %d full)'
               % (len(lines), sum(1 for x in lines if x.startswith('fixture\t')),
                  sum(1 for x in lines if x.startswith('full\t'))))
    verify(fx, fv)


def verify(fx, fv):
    """Checks the cut against the full file, number by number."""
    ok = True
    twelve = common.FIXTURE_NATIONS
    if fx.wf.has('freight') and fv.wf.has('freight'):
        a, b = fx.get('freight'), fv.get('freight')
        n = b['n']
        for i, ci in enumerate(twelve):
            for j, cj in enumerate(twelve):
                k = (fv.number[ci] - 1) * n + fv.number[cj] - 1
                for key in ('sea', 'land', 'cost'):
                    if a[key][i * 12 + j] != b[key][k]:
                        ok = False
                        print('  FAIL freight %s %s %s' % (ci, cj, key))
    if fx.wf.has('trade'):
        t = fx.get('trade')
        for gi in range(t['g']):
            mk, us = wr.sums_of(12, t['pairs'][gi])
            if mk != t['make'][gi] or us != t['use'][gi]:
                ok = False
                print('  FAIL trade sums of good', gi)
    inw = set(window_tiles())
    for tid in inw:
        g = fx.wf.owner_grid(tid)
        if not set(g) <= set(range(0, 13)):
            ok = False
            print('  FAIL owner outside 0..12 in tile', tid)
    common.log('fixture: cut checked against the full file: ' + ('all equal' if ok else 'FAILED'))
    if not ok:
        raise SystemExit(1)


if __name__ == '__main__':
    main()
