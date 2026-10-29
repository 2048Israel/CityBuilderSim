"""The reference reader of world-1.bin (batch WN0, slice G, 2026-10-28): every layer of FORMAT.txt parsed to plain
Python values and encoded back to the same bytes; fixture.py cuts the fixture with it, WorldData's Java reader is
held to it. Run as a script it prints a summary of a file.

WHY: the layers are binary and a second reader, written from FORMAT.txt alone, finds the places where the contract
and the writer disagree before the Java does (tools/earth/earthread.py's role for earth-1.bin). Pure standard
library.
Usage: python -I worldread.py [path to world-1.bin]   (default <work>/out/world-1.bin)
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import math
import struct
import zlib

import common

NAN = float('nan')


# ===================================================================== the container
class WorldFile:
    """A world-1.bin (or its fixture) read whole: the header, and each layer's payload, entries and tiles."""

    def __init__(self, path):
        self.path = path
        with open(path, 'rb') as f:
            self.data = f.read()
        d = self.data
        if d[:8] != common.MAGIC:
            raise ValueError('not a world-1 file: ' + repr(d[:8]))
        self.version, n = struct.unpack_from('>ii', d, 8)
        o = 16
        self.layers = {}
        self.order = []
        for _ in range(n):
            name, o = read_utf(d, o)
            kind, cpd, io_, il, do, dl = struct.unpack_from('>biqiqq', d, o)
            o += 1 + 4 + 8 + 4 + 8 + 8
            self.layers[name] = dict(kind=kind, cpd=cpd, index_off=io_, index_len=il, data_off=do, data_len=dl)
            self.order.append(name)
        self._entries = {}
        self._offsets = {}

    def has(self, name):
        return name in self.layers

    def payload(self, name):
        """A kind 2 layer's inflated bytes."""
        L = self.layers[name]
        assert L['kind'] == 2, name
        return zlib.decompress(self.data[L['data_off']:L['data_off'] + L['data_len']])

    def entries(self, name):
        """A kind 0 layer's 64,800 index entries."""
        if name not in self._entries:
            L = self.layers[name]
            assert L['kind'] == 0, name
            raw = zlib.decompress(self.data[L['index_off']:L['index_off'] + L['index_len']])
            e = struct.unpack('>%di' % common.TILES, raw)
            offs, p = {}, L['data_off']
            for i, v in enumerate(e):
                if v > 0:
                    offs[i] = p
                    p += v
            assert p == L['data_off'] + L['data_len'], 'blobs do not fill the data'
            self._entries[name], self._offsets[name] = e, offs
        return self._entries[name]

    def tile(self, name, i):
        """A tile's inflated blob, or None when the tile is uniform."""
        e = self.entries(name)[i]
        if e <= 0:
            return None
        o = self._offsets[name][i]
        return zlib.decompress(self.data[o:o + e])

    def owner_grid(self, i, name='owner'):
        """A tile of the owner layer as 57,600 bytes, a uniform tile filled."""
        b = self.tile(name, i)
        if b is None:
            return bytes([-self.entries(name)[i]]) * (self.layers[name]['cpd'] ** 2)
        return b

    def owner_at(self, i, r, c, name='owner'):
        e = self.entries(name)[i]
        if e <= 0:
            return -e
        return self.tile(name, i)[r * self.layers[name]['cpd'] + c]

    # ----- the layers parsed -----
    def meta(self):
        return parse_meta(self.payload('meta'))

    def nations(self):
        return parse_table(self.payload('nations'))

    def moneys(self):
        return parse_moneys(self.payload('moneys'))

    def paths(self):
        return parse_paths(self.payload('paths'))

    def goods(self):
        return parse_table(self.payload('goods'))

    def trade(self):
        return parse_trade(self.payload('trade'))

    def freight(self):
        return parse_freight(self.payload('freight'))

    def lines(self):
        return parse_lines(self.payload('lines'))

    def prices(self):
        return parse_prices(self.payload('prices'))


def read_utf(b, o):
    """java.io.DataInputStream.readUTF at o: (the text, the next offset). Text the pipeline writes is plain."""
    n, = struct.unpack_from('>H', b, o)
    return b[o + 2:o + 2 + n].decode('utf-8'), o + 2 + n


# ===================================================================== text layers
def parse_table(payload):
    """A text layer: (the column names, the rows as lists of strings)."""
    lines = payload.decode('utf-8').split('\n')
    if lines and lines[-1] == '':
        lines.pop()
    rows = [ln.split('\t') for ln in lines]
    return rows[0], rows[1:]


def encode_table(header, rows):
    """A text layer's bytes: the column names, then the rows (LF)."""
    return ('\n'.join('\t'.join(r) for r in [header] + list(rows)) + '\n').encode('utf-8')


def parse_meta(payload):
    """meta: (rows as lists of strings, with the first line's column names dropped when it is 'key')."""
    rows = [ln.split('\t') for ln in payload.decode('utf-8').split('\n') if ln != '']
    if rows and rows[0][0] == 'key':
        rows = rows[1:]
    return rows


def meta_value(rows, key):
    """The first field of the first meta row with that key, or None."""
    for r in rows:
        if r[0] == key:
            return r[1] if len(r) > 1 else ''
    return None


# ===================================================================== moneys
def parse_moneys(b):
    """moneys: {codes, names, first_month, months, rate[m][month], mark[m][month]} (marks as lists of ints)."""
    m, first, months = struct.unpack_from('>iii', b, 0)
    o = 12
    codes, names = [], []
    for _ in range(m):
        c, o = read_utf(b, o)
        n, o = read_utf(b, o)
        codes.append(c)
        names.append(n)
    rate = []
    for _ in range(m):
        rate.append(list(struct.unpack_from('>%dd' % months, b, o)))
        o += 8 * months
    mark = []
    for _ in range(m):
        mark.append(list(b[o:o + months]))
        o += months
    assert o == len(b), 'moneys: %d bytes left' % (len(b) - o)
    return dict(codes=codes, names=names, first_month=first, months=months, rate=rate, mark=mark)


def encode_moneys(d):
    out = [struct.pack('>iii', len(d['codes']), d['first_month'], d['months'])]
    for c, n in zip(d['codes'], d['names']):
        out.append(common.utf(c) + common.utf(n))
    for r in d['rate']:
        out.append(struct.pack('>%dd' % d['months'], *r))
    for k in d['mark']:
        out.append(bytes(k))
    return b''.join(out)


# ===================================================================== paths
def parse_paths(b):
    """paths: {n, first_year, years, series [(name, unit)], value[s][nation * years + year offset], mark[s]}."""
    n, first, years, s = struct.unpack_from('>iiii', b, 0)
    o = 16
    series = []
    for _ in range(s):
        name, o = read_utf(b, o)
        unit, o = read_utf(b, o)
        series.append((name, unit))
    value, mark = [], []
    for _ in range(s):
        value.append(list(struct.unpack_from('>%dd' % (n * years), b, o)))
        o += 8 * n * years
    for _ in range(s):
        mark.append(list(b[o:o + n * years]))
        o += n * years
    assert o == len(b), 'paths: %d bytes left' % (len(b) - o)
    return dict(n=n, first_year=first, years=years, series=series, value=value, mark=mark)


def encode_paths(d):
    out = [struct.pack('>iiii', d['n'], d['first_year'], d['years'], len(d['series']))]
    for name, unit in d['series']:
        out.append(common.utf(name) + common.utf(unit))
    for v in d['value']:
        out.append(struct.pack('>%dd' % (d['n'] * d['years']), *v))
    for k in d['mark']:
        out.append(bytes(k))
    return b''.join(out)


# ===================================================================== trade
PAIR = struct.Struct('>hhddddb')


def parse_trade(b):
    """trade: {n, g, base_year, pairs[good] = [(seller, buyer, flow, tonnes, cost, pref, mark)], make[good][nation],
    use[good][nation]}; the sellers and buyers are the nations' numbers (1-based)."""
    n, g, base = struct.unpack_from('>iii', b, 0)
    o = 12
    pairs, make, use = [], [], []
    for _ in range(g):
        k, = struct.unpack_from('>i', b, o)
        o += 4
        pairs.append(list(PAIR.iter_unpack(b[o:o + k * PAIR.size])))
        o += k * PAIR.size
        mu = struct.unpack_from('>%dd' % (2 * n), b, o)
        o += 16 * n
        make.append(list(mu[0::2]))
        use.append(list(mu[1::2]))
    assert o == len(b), 'trade: %d bytes left' % (len(b) - o)
    return dict(n=n, g=g, base_year=base, pairs=pairs, make=make, use=use)


def encode_trade(d):
    out = [struct.pack('>iii', d['n'], d['g'], d['base_year'])]
    for gi in range(d['g']):
        out.append(struct.pack('>i', len(d['pairs'][gi])))
        out.append(b''.join(PAIR.pack(*p) for p in d['pairs'][gi]))
        mu = []
        for i in range(d['n']):
            mu += [d['make'][gi][i], d['use'][gi][i]]
        out.append(struct.pack('>%dd' % (2 * d['n']), *mu))
    return b''.join(out)


def sums_of(n, pairs):
    """make and use of a good from its pairs: make[i] the sum of seller i's flows, use[j] of buyer j's, added in
    the pairs' order (seller then buyer)."""
    make, use = [0.0] * n, [0.0] * n
    for s, b, flow, _, _, _, _ in pairs:
        make[s - 1] += flow
        use[b - 1] += flow
    return make, use


# ===================================================================== freight
def parse_freight(b):
    """freight: {n, sea, land, cost (flat, seller * n + buyer), edges [(a, b, sea, land, kind)]} (a, b 1-based)."""
    n, = struct.unpack_from('>i', b, 0)
    o = 4
    m = []
    for _ in range(3):
        m.append(list(struct.unpack_from('>%dd' % (n * n), b, o)))
        o += 8 * n * n
    e, = struct.unpack_from('>i', b, o)
    o += 4
    edges = list(struct.iter_unpack('>hhddb', b[o:o + e * 21]))
    o += e * 21
    assert o == len(b), 'freight: %d bytes left' % (len(b) - o)
    return dict(n=n, sea=m[0], land=m[1], cost=m[2], edges=edges)


def encode_freight(d):
    n = d['n']
    out = [struct.pack('>i', n)]
    for k in ('sea', 'land', 'cost'):
        out.append(struct.pack('>%dd' % (n * n), *d[k]))
    out.append(struct.pack('>i', len(d['edges'])))
    out.append(b''.join(struct.pack('>hhddb', *e) for e in d['edges']))
    return b''.join(out)


# ===================================================================== lines
def parse_lines(b):
    """lines: [(left, right, [(lat x 1e5, lon x 1e5), ...])]."""
    l, = struct.unpack_from('>i', b, 0)
    o = 4
    out = []
    for _ in range(l):
        left, right, n = struct.unpack_from('>hhi', b, o)
        o += 8
        v = struct.unpack_from('>%di' % (2 * n), b, o)
        o += 8 * n
        out.append((left, right, list(zip(v[0::2], v[1::2]))))
    assert o == len(b), 'lines: %d bytes left' % (len(b) - o)
    return out


def encode_lines(lines):
    out = [struct.pack('>i', len(lines))]
    for left, right, pts in lines:
        out.append(struct.pack('>hhi', left, right, len(pts)))
        out.append(struct.pack('>%di' % (2 * len(pts)), *[v for p in pts for v in p]))
    return b''.join(out)


# ===================================================================== prices
def parse_prices(b):
    """prices: {g, first_year, years, series [the Pink Sheet series a good follows], value[good][year],
    mark[good][year]}."""
    g, first, years = struct.unpack_from('>iii', b, 0)
    o = 12
    series, value, mark = [], [], []
    for _ in range(g):
        name, o = read_utf(b, o)
        series.append(name)
        value.append(list(struct.unpack_from('>%dd' % years, b, o)))
        o += 8 * years
    for _ in range(g):
        mark.append(list(b[o:o + years]))
        o += years
    assert o == len(b), 'prices: %d bytes left' % (len(b) - o)
    return dict(g=g, first_year=first, years=years, series=series, value=value, mark=mark)


def encode_prices(d):
    out = [struct.pack('>iii', d['g'], d['first_year'], d['years'])]
    for name, v in zip(d['series'], d['value']):
        out.append(common.utf(name) + struct.pack('>%dd' % d['years'], *v))
    for k in d['mark']:
        out.append(bytes(k))
    return b''.join(out)


# ===================================================================== the summary
def summary(path):
    """Reads every layer of a file, checks what FORMAT.txt promises, and prints a screenful."""
    w = WorldFile(path)
    print('%s: %d bytes, format %d, %d layers' % (path, len(w.data), w.version, len(w.order)))
    for name in w.order:
        L = w.layers[name]
        print('  %-8s kind %d, cells/deg %3d, index %8d bytes, data %9d bytes' % (name, L['kind'], L['cpd'],
                                                                              L['index_len'], L['data_len']))
    meta = w.meta() if w.has('meta') else []
    for r in meta:
        if r[0] not in ('dial', 'source'):
            print('  meta %s = %s' % (r[0], ' '.join(r[1:])))
    print('  meta: %d dial rows, %d source rows' % (sum(1 for r in meta if r[0] == 'dial'),
                                                    sum(1 for r in meta if r[0] == 'source')))
    nn = 0
    if w.has('nations'):
        h, rows = w.nations()
        nn = sum(1 for r in rows if r[3] == 'nation')
        print('  nations: %d nations, %d areas; columns %s' % (nn, len(rows) - nn, ' '.join(h)))
    if w.has('moneys'):
        m = w.moneys()
        nan = sum(1 for r in m['rate'] for v in r if math.isnan(v))
        print('  moneys: %d, %d months from %d, %d rates NaN' % (len(m['codes']), m['months'], m['first_month'], nan))
    if w.has('paths'):
        p = w.paths()
        print('  paths: %d nations, %d years from %d, series %s' % (p['n'], p['years'], p['first_year'],
                                                                 ' '.join(s[0] for s in p['series'])))
    if w.has('goods'):
        h, rows = w.goods()
        print('  goods: %d' % len(rows))
    if w.has('trade'):
        t = w.trade()
        bad = 0
        for gi in range(t['g']):
            mk, us = sums_of(t['n'], t['pairs'][gi])
            bad += sum(1 for i in range(t['n']) if abs(mk[i] - t['make'][gi][i]) > 1e-6 * (1 + abs(mk[i]))
                       or abs(us[i] - t['use'][gi][i]) > 1e-6 * (1 + abs(us[i])))
        print('  trade: %d goods, %d pairs, base year %d, %d make/use sums off from their pairs'
              % (t['g'], sum(len(p) for p in t['pairs']), t['base_year'], bad))
    if w.has('freight'):
        f = w.freight()
        n = f['n']
        lps = common.LAND_PER_SEA
        off = sum(1 for i in range(n * n) if abs(f['cost'][i] - (f['sea'][i] + lps * f['land'][i])) > 1e-6)
        tri = 0
        for i in range(0, n, 7):
            for j in range(0, n, 5):
                for k in range(0, n, 3):
                    if f['cost'][i * n + j] > f['cost'][i * n + k] + f['cost'][k * n + j] + 1e-6:
                        tri += 1
        kinds = {}
        for e in f['edges']:
            kinds[e[4]] = kinds.get(e[4], 0) + 1
        print('  freight: %d nations, %d edges %s; %d costs off the definition; %d triangle breaks in a sample'
              % (n, len(f['edges']), dict(sorted(kinds.items())), off, tri))
    if w.has('owner'):
        e = w.entries('owner')
        print('  owner: %d tiles uniform with an owner, %d full, %d with no owner'
              % (sum(1 for v in e if v < 0), sum(1 for v in e if v > 0), sum(1 for v in e if v == 0)))
    if w.has('lines'):
        ls = w.lines()
        print('  lines: %d, %d points' % (len(ls), sum(len(x[2]) for x in ls)))
    if w.has('prices'):
        pr = w.prices()
        print('  prices: %d goods, %d years from %d' % (pr['g'], pr['years'], pr['first_year']))


if __name__ == '__main__':
    args = common.parser('Prints a summary of a world-1.bin.')
    args.add_argument('path', nargs='?', default=None)
    a = args.parse_args()
    summary(a.path or common.work_dir(a) + '/out/world-1.bin')
