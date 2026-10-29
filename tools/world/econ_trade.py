"""The base year's trade of the nations, by good: BACI's flows through the concordance, the partner rule, the domestic
estimate, the kernel's costs and preferences, and the proof that a solve from 1 gives the flows back (batch WN0, E2).

WHY: spec-nations 4.2-4.3. The game's world market is a smooth spatial equilibrium that must reproduce the real base year;
BACI has the flows (HS6, 196-ish countries) but not what each nation makes and uses, nor a price. This stage keeps the pairs
the partner rule says matter (the 20 largest economies trade with all; the rest with the top exporters and their nearest
makers: D5, D11), estimates the domestic flow of each nation by its share of the world's output (spec 3.3's fallback, mark
1), and sets each kept pair's fixed preference so that freight and the elasticity (D18) give back BACI's flows. It then
solves the kernel from a = b = 1 and asserts the flows return, so the Java side can rely on the same arithmetic.

The proof is not plain Sinkhorn: a small nation whose own pair is nearly all its make and its use (San Marino imports a
ten-thousandth of what it uses) keeps an error that shrinks by that ten-thousandth a sweep, so plain sweeps need up to
tens of thousands to reach 0.01% and millions to reach 1e-12. solve_exact solves each nation's a and b together, exactly,
the others held; the solution is unique, so it is the one plain sweeps approach. The plain count from 1 is still made
and printed (cold_sweeps), for the game's January solve.

Run: python -I econ_trade.py [--sources DIR] [--work DIR] [--stand-in] [--no-samples] [--cache] [--only KEY,KEY]
(about 90 s). Reads WORK/geo/owners.tsv and freight.bin (geo.py's) and WORK/econ/gdp-base.tsv (econ_paths.py's);
--stand-in uses WDI and CERDI in place of any of the three that is missing, for testing only, and writes under
WORK/econ/stand-in; --only (development) writes under WORK/econ/only.
Writes WORK/econ/goods.tsv, trade.bin (the trade layer's payload), meta-trade.tsv and baci-samples.tsv.
"""
import argparse
import array
import csv
import json
import math
import operator
import os
import pickle
import re
import struct
import sys
import time
import zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import common
import xlsx
import econ_trade_countries
import econ_trade_samples

N = len(common.NATIONS)
NAN = float('nan')

# A nation is a maker of a good when its exports of it are at least this share of the world's.
MAKER_FLOOR = 0.0001
# A nation's domestic flow is at least this share of the largest of its use, imports and exports (spec 3.3's fallback).
DOMESTIC_FLOOR = 0.05
# The trade elasticity of a bulk good, nearly identical from any seller (D18, est.).
ELASTICITY_BULK = 8.0
# The trade elasticity of a processed good (D18: Head and Mayer's preferred estimate, about 5).
ELASTICITY_PROCESSED = 5.0
# The trade elasticity of a made good (D18: the Armington literature's median, 3.8).
ELASTICITY_MADE = 3.8
ELASTICITY = {'bulk': ELASTICITY_BULK, 'processed': ELASTICITY_PROCESSED, 'made': ELASTICITY_MADE}
# The largest economies, by output in the base year, that trade with every nation (D5).
TOP_ECONOMIES = 20
# The largest net exporters of a good that sell to every nation (D5).
TOP_EXPORTERS = 10
# How many of a nation's nearest makers of a good it trades with (D11).
NEAREST_MAKERS = 10

DIALS = [('MAKER_FLOOR', MAKER_FLOOR, 'A nation is a maker of a good when its exports of it are at least this share of the world\'s.'),
         ('DOMESTIC_FLOOR', DOMESTIC_FLOOR, 'A nation\'s domestic flow is at least this share of the largest of its use, imports and exports.'),
         ('ELASTICITY_BULK', ELASTICITY_BULK, 'The trade elasticity of a bulk good, nearly identical from any seller (D18, est.).'),
         ('ELASTICITY_PROCESSED', ELASTICITY_PROCESSED, 'The trade elasticity of a processed good (D18: Head and Mayer\'s preferred estimate).'),
         ('ELASTICITY_MADE', ELASTICITY_MADE, 'The trade elasticity of a made good (D18: the Armington literature\'s median).'),
         ('TOP_ECONOMIES', TOP_ECONOMIES, 'The largest economies of the base year, which trade with every nation (D5).'),
         ('TOP_EXPORTERS', TOP_EXPORTERS, 'The largest net exporters of a good, which sell to every nation (D5).'),
         ('NEAREST_MAKERS', NEAREST_MAKERS, 'How many of a nation\'s nearest makers of a good it trades with (D11).')]

# The solve from a = b = 1 must meet every row and column to this relative error.
SINKHORN_TOL = 1e-12
# ...and then give every flow back to this relative error.
FLOW_TOL = 1e-9
# Four ulps: the error a margin of double precision leaves in a flow, over the margin.
MARGIN_ULPS = 1e-15
# The world's makes and uses must close to this relative error.
CLOSE_TOL = 1e-9
# The most passes or sweeps any solve may take.
SWEEP_CAP = 100000
# The cold plain-Sinkhorn count (a report for the game's January solve): to these row errors, within this many sweeps.
COLD_TARGETS = (1e-2, 1e-4)
# The most plain sweeps the cold count runs.
COLD_CAP = 20000
# Whether to run the cold plain-Sinkhorn count (a report; about half the run).
COLD = True
# Stand-in only: the km given to a pair CERDI lacks.
STAND_IN_KM = 30000.0

SOURCE_ROW = ['source', 'baci', 'BACI HS22 V202601 (CEPII)', 'https://www.cepii.fr/DATA_DOWNLOAD/baci/doc/baci_webpage.html',
              'Etalab 2.0', '2026-10-28']

T0 = time.time()


def log(msg):
    print('[%4.0f s] %s' % (time.time() - T0, msg), flush=True)


# ------------------------------------------------------------------------------------------------------------ the goods

def read_tsv(path):
    """The rows of an authored TSV (comment lines start with #; the first other line names the columns) as dicts."""
    with open(path, encoding='utf-8') as f:
        lines = [l.rstrip('\n') for l in f if l.strip() and not l.startswith('#')]
    head = lines[0].split('\t')
    rows = []
    for l in lines[1:]:
        cells = l.split('\t')
        assert len(cells) == len(head), '%s: %d cells for %d columns in "%s"' % (path, len(cells), len(head), l[:60])
        rows.append(dict(zip(head, cells)))
    return rows


def load_goods(path):
    """goods.tsv as a list of dicts in number order, checked: numbers 1..G, class and elasticity agree (D18)."""
    goods = read_tsv(path)
    for n, g in enumerate(goods, 1):
        assert int(g['number']) == n, 'goods.tsv: number %s at row %d' % (g['number'], n)
        assert g['class'] in ELASTICITY, g['key'] + ': class ' + g['class']
        assert float(g['elasticity']) == ELASTICITY[g['class']], g['key'] + ': elasticity does not match its class'
        g['units_per_tonne'] = float(g['units_per_tonne'])
        g['traded_share'] = float(g['traded_share'])
        g['elasticity'] = float(g['elasticity'])
        assert g['units_per_tonne'] > 0 and 0 < g['traded_share'] < 1, g['key']
    return goods


def check_against_java(goods):
    """Warns when goods.tsv differs from Good.java's BAND goods less the services (keys, order, units)."""
    path = os.path.join(HERE, '..', '..', 'src', 'main', 'java', 'ham', 'citybuildersim', 'Good.java')
    if not os.path.exists(path):
        log('Good.java not found beside the tools: goods not cross-checked')
        return
    pat = re.compile(r'^    ([A-Z_0-9]+)\("([^"]*)", "([^"]*)",.*Pricing\.BAND')
    java = []
    with open(path, encoding='utf-8') as f:
        for l in f:
            m = pat.match(l)
            if m and not m.group(1).endswith('_WORK'):
                java.append((m.group(1), m.group(3)))
    mine = [(g['key'], g['unit']) for g in goods]
    if java == mine:
        log('goods.tsv agrees with Good.java: %d BAND goods less the three services, keys, order and units' % len(mine))
    else:
        log('WARNING goods.tsv and Good.java differ: only in tsv %s, only in java %s'
            % ([x for x in mine if x not in java][:5], [x for x in java if x not in mine][:5]))


def load_concordance(path, goods):
    """concordance.tsv as {prefix: [(good index or -1, share, vshare)]}; each prefix's shares and vshares sum to 1."""
    index = {g['key']: n for n, g in enumerate(goods)}
    conc = {}
    for r in read_tsv(path):
        hs = r['hs']
        assert hs.isdigit() and len(hs) in (2, 4, 6), 'concordance: hs ' + hs
        gi = -1 if r['good'] == '-' else index[r['good']]
        share = float(r['share'])
        vshare = share if r['vshare'] == '-' else float(r['vshare'])
        conc.setdefault(hs, []).append((gi, share, vshare))
    for hs, m in conc.items():
        assert abs(sum(x[1] for x in m) - 1) < 1e-9 and abs(sum(x[2] for x in m) - 1) < 1e-9, 'concordance: shares of ' + hs
        assert len({x[0] for x in m}) == len(m), 'concordance: a good twice under ' + hs
    return conc


def resolve(codes, conc, goods):
    """Each HS6 code's goods by the longest prefix. Returns ({code as bytes: [(gi, share, vshare)]}, subheadings per
    good, the prefixes that matched no subheading)."""
    hs6 = {}
    count = [0] * len(goods)
    used = set()
    for code in codes:
        for n in (6, 4, 2):
            m = conc.get(code[:n])
            if m is not None:
                used.add(code[:n])
                real = [x for x in m if x[0] >= 0]
                if real:
                    hs6[code.encode()] = real
                    for gi, _, _ in real:
                        count[gi] += 1
                break
    return hs6, count, sorted(set(conc) - used)


# ------------------------------------------------------------------------------------------------------------- BACI

def stream_baci(z, member, hs6, by_code, G):
    """One pass over the year's CSV. Returns (agg, stats): agg {(good * 256 + seller) * 256 + buyer: [value, tonnes,
    every row had q]} for different mapped nations; stats the BACI value of the mapped subheadings by good: total, dropped
    for a country that is no nation, dropped for the same nation at both ends."""
    agg = {}
    total = [0.0] * G
    nat = [0.0] * G
    same = [0.0] * G
    rows = mapped = 0
    with z.open(member) as f:
        f.readline()
        for line in f:
            rows += 1
            p = line.split(b',')
            m = hs6.get(p[3])
            if m is None:
                continue
            mapped += 1
            v = float(p[4])
            i = by_code.get(p[1], 0)
            j = by_code.get(p[2], 0)
            if i == 0 or j == 0:
                for gi, sh, vs in m:
                    total[gi] += v * vs
                    nat[gi] += v * vs
                continue
            if i == j:
                for gi, sh, vs in m:
                    total[gi] += v * vs
                    same[gi] += v * vs
                continue
            qs = p[5].strip()
            q = float(qs) if qs and qs != b'NA' else None
            for gi, sh, vs in m:
                total[gi] += v * vs
                key = (gi * 256 + i) * 256 + j
                e = agg.get(key)
                if e is None:
                    agg[key] = [v * vs, q * sh if q is not None else 0.0, q is not None]
                else:
                    e[0] += v * vs
                    if q is None:
                        e[2] = False
                    else:
                        e[1] += q * sh
    return agg, dict(rows=rows, mapped=mapped, total=total, nat=nat, same=same)


# ------------------------------------------------------------------------------------------------ the other inputs

def read_gdp(path):
    """WORK/econ/gdp-base.tsv (iso3, gdp_usd, gdp_2015usd, mark) as a list a nation of base-year gdp_usd; 0 where none."""
    gdp = [0.0] * (N + 1)
    with open(path, encoding='utf-8') as f:
        rd = csv.reader(f, delimiter='\t')
        for r in rd:
            if not r or r[0] not in common.NATIONS:
                continue
            try:
                v = float(r[1])
            except ValueError:
                continue
            if v > 0 and v == v:
                gdp[common.number(r[0])] = v
    return gdp


def stand_in_gdp(sources):
    """Testing only: WDI's NY.GDP.MKTP.CD of the base year."""
    gdp = [0.0] * (N + 1)
    with open(sources + '/wdi/NY.GDP.MKTP.CD.json', encoding='utf-8') as f:
        data = json.load(f)
    for r in data[1]:
        iso = r.get('countryiso3code')
        if iso in common.NATIONS and r.get('date') == str(common.BASE_YEAR) and r.get('value'):
            gdp[common.number(iso)] = float(r['value'])
    return gdp


def read_freight(path):
    """freight.bin (geo.py's, the freight layer's payload): (sea, land, cost) as N*N arrays of doubles, seller-major."""
    with open(path, 'rb') as f:
        data = f.read()
    n = struct.unpack('>i', data[:4])[0]
    if n != N:
        import zlib
        data = zlib.decompress(data)
        n = struct.unpack('>i', data[:4])[0]
    assert n == N, 'freight.bin: N = %d' % n
    out = []
    o = 4
    for _ in range(3):
        a = array.array('d')
        a.frombytes(data[o:o + N * N * 8])
        if sys.byteorder == 'little':
            a.byteswap()
        out.append(a)
        o += N * N * 8
    return out


def stand_in_freight(sources):
    """Testing only: CERDI's sea distances alone (km), STAND_IN_KM for a pair it lacks, no land, cost = sea."""
    sea = array.array('d', [STAND_IN_KM]) * (N * N)
    for k in range(N):
        sea[k * N + k] = 0.0
    head = None
    for r in xlsx.rows(sources + '/cerdi/CERDI-seadistance_V1.1.xlsx', 'CERDIseadistance'):
        if head is None:
            head = {c: n for n, c in enumerate(r)}
            continue
        a, b = r[head['iso1']], r[head['iso2']]
        if a in common.NATIONS and b in common.NATIONS and a != b and r[head['seadistance']]:
            d = float(r[head['seadistance']])
            x, y = common.number(a) - 1, common.number(b) - 1
            sea[x * N + y] = d
            sea[y * N + x] = d
    land = array.array('d', [0.0]) * (N * N)
    return sea, land, array.array('d', sea)


# -------------------------------------------------------------------------------------------------- the partner rule

def nearness(cost):
    """order[x]: the other nations 1..N by reference cost from x, ties by number."""
    order = [None] * (N + 1)
    for x in range(1, N + 1):
        row = cost[(x - 1) * N:x * N]
        order[x] = sorted((m for m in range(1, N + 1) if m != x), key=lambda m: (row[m - 1], m))
    return order


def partner_rule(pairs, top20, order):
    """pairs [(seller, buyer, value, tonnes)] of one good, all with value > 0. Returns the kept pairs."""
    exports = [0.0] * (N + 1)
    imports = [0.0] * (N + 1)
    for s, b, v, t in pairs:
        exports[s] += v
        imports[b] += v
    world = sum(exports)
    makers = {s for s in range(1, N + 1) if exports[s] > 0 and exports[s] >= MAKER_FLOOR * world}
    net = sorted((s for s in range(1, N + 1) if exports[s] > imports[s]), key=lambda s: (imports[s] - exports[s], s))
    top_exp = set(net[:TOP_EXPORTERS])
    nm = [None] * (N + 1)
    for x in range(1, N + 1):
        got = []
        for m in order[x]:
            if m in makers:
                got.append(m)
                if len(got) == NEAREST_MAKERS:
                    break
        nm[x] = set(got)
    kept = [p for p in pairs if p[0] in top20 or p[1] in top20 or p[0] in top_exp or p[0] in nm[p[1]] or p[1] in nm[p[0]]]
    return kept, len(makers), len(top_exp)


# ----------------------------------------------------------------------------------------- make and use, kernel, solve

def solve_good(g, kept, gdp, sea, land):
    """One good: the domestic flows, make and use, unit value, costs, preferences; then the solve from 1 that proves them.

    kept [(seller, buyer, value, tonnes)] with seller != buyer. Returns a dict: pairs [(s, b, flow, tonnes, cost, pref,
    mark)] sorted by seller then buyer, make and use by nation (index 1..N), unit_value, sweeps, world use.
    """
    exports = [0.0] * (N + 1)
    imports = [0.0] * (N + 1)
    for s, b, v, t in kept:
        exports[s] += v
        imports[b] += v
    kept_value = sum(exports)
    world_use = kept_value / g['traded_share']
    gdp_world = sum(gdp)
    dom = [0.0] * (N + 1)
    for j in range(1, N + 1):
        u = world_use * gdp[j] / gdp_world
        dom[j] = max(u - imports[j], DOMESTIC_FLOOR * max(u, imports[j], exports[j]))
    make_f = [dom[j] + exports[j] for j in range(N + 1)]
    use_f = [dom[j] + imports[j] for j in range(N + 1)]
    assert abs(sum(make_f) - sum(use_f)) <= CLOSE_TOL * sum(use_f), g['key'] + ': the world does not close'

    tv = sum(v for s, b, v, t in kept if t == t and t > 0)
    tt = sum(t for s, b, v, t in kept if t == t and t > 0)
    assert tt > 0, g['key'] + ': no kept pair has tonnes'
    unit_value = tv * 1000.0 / tt
    el = g['elasticity']

    rows = []
    for s, b, v, t in kept:
        f = common.SEA_USD_TKM * sea[(s - 1) * N + b - 1] + common.LAND_USD_TKM * land[(s - 1) * N + b - 1] + common.HANDLING_USD_T
        rows.append([s, b, v, t, el * math.log1p(f / unit_value), 3])
    for j in range(1, N + 1):
        if dom[j] > 0:
            rows.append([j, j, dom[j], NAN, 0.0, 1])
    rows.sort(key=lambda r: (r[0], r[1]))
    P = len(rows)

    # pref = ln x + cost, less each row's mean over its kept pairs, then less each column's mean.
    base = [math.log(r[2]) + r[4] for r in rows]
    rs = [0.0] * (N + 1)
    rn = [0] * (N + 1)
    for r, x in zip(rows, base):
        rs[r[0]] += x
        rn[r[0]] += 1
    pref = [x - rs[r[0]] / rn[r[0]] for r, x in zip(rows, base)]
    cs = [0.0] * (N + 1)
    cn = [0] * (N + 1)
    for r, x in zip(rows, pref):
        cs[r[1]] += x
        cn[r[1]] += 1
    pref = [x - cs[r[1]] / cn[r[1]] for r, x in zip(rows, pref)]

    # make and use are the sums of the pair flows, as the file defines them.
    mk = [[] for _ in range(N + 1)]
    us = [[] for _ in range(N + 1)]
    for r in rows:
        mk[r[0]].append(r[2])
        us[r[1]].append(r[2])
    make = [math.fsum(x) for x in mk]
    use = [math.fsum(x) for x in us]
    for j in range(1, N + 1):
        assert abs(make[j] - make_f[j]) <= CLOSE_TOL * max(make[j], 1e-300) and abs(use[j] - use_f[j]) <= CLOSE_TOL * max(use[j], 1e-300)
    assert abs(sum(make) - sum(use)) <= CLOSE_TOL * sum(use)

    passes, worst, limited, reach = solve_exact(g['key'], rows, pref, make, use)
    cold = cold_sweeps(rows, pref, make, use) if COLD else None
    out = [(r[0], r[1], r[2], r[3], r[4], pf, r[5]) for r, pf in zip(rows, pref)]
    return dict(pairs=out, make=make, use=use, unit_value=unit_value, sweeps=passes, reach=reach, cold=cold, worst=worst, limited=limited, world_use=world_use, dom=dom)


def pair_plans(rows, K):
    """For the solves: per nation the off-diagonal row entries (getter of the buyers, K weights), the off-diagonal column
    entries (getter of the sellers, weights) and the own pair's K (0 when it has none). A single entry is padded with a
    zero weight so that itemgetter returns a tuple."""
    kd = [0.0] * (N + 1)
    rowe = {}
    cole = {}
    for r, k in zip(rows, K):
        if r[0] == r[1]:
            kd[r[0]] = k
        else:
            rowe.setdefault(r[0], ([], []))
            rowe[r[0]][0].append(r[1])
            rowe[r[0]][1].append(k)
            cole.setdefault(r[1], ([], []))
            cole[r[1]][0].append(r[0])
            cole[r[1]][1].append(k)

    def plan(d):
        out = {}
        for i, (idx, w) in d.items():
            if len(idx) == 1:
                idx, w = idx * 2, w + [0.0]
            out[i] = (operator.itemgetter(*idx), w)
        return out

    return kd, plan(rowe), plan(cole)


def node_solve(kd, R, C, make, use, a_old):
    """The pair (a_i, b_i) that meets nation i's row and column exactly, the others held: a (kd b + R) = make and
    b (kd a + C) = use, R and C the off-diagonal sums of K b and K a."""
    if kd > 0 and R > 0 and C > 0:
        m = make - use
        alpha = kd * R / C
        beta = R - kd * m / C
        disc = math.sqrt(beta * beta + 4 * alpha * make)
        p = 2 * make / (beta + disc) if beta >= 0 else (disc - beta) / (2 * alpha)
        return p, use / (kd * p + C)
    if kd > 0 and R > 0:        # nobody else sells to it: b (kd a) = use and a R = make - use
        p = (make - use) / R
        return p, use / (kd * p)
    if kd > 0 and C > 0:        # it sells to nobody else: a (kd b) = make and b C = use - make
        q = (use - make) / C
        return make / (kd * q), q
    if kd > 0:                  # its own pair alone: only the product is fixed
        return a_old, make / (kd * a_old)
    return (make / R if R > 0 else a_old), (use / C if C > 0 else 1.0)


def solve_exact(key, rows, pref, make, use):
    """The proof: solves x_ij = a_i b_j exp(pref - cost) from a = b = 1 and asserts that every row and column meets its
    margin to SINKHORN_TOL and every flow comes back to FLOW_TOL. Returns (passes, worst flow error, flows limited by their margins, passes to a margin error of the last COLD_TARGETS).

    Plain Sinkhorn cannot do this in reach: a nation whose own pair is nearly all of its make and of its use (San Marino:
    imports a ten-thousandth of its use) keeps an error that shrinks by that ten-thousandth a sweep. Each pass here solves
    every nation's a and b together and exactly (a quadratic), the others held, so the loop converges at the rate of the
    nations' coupling, not of the own pairs. The solution is unique, so it is the one plain sweeps approach.
    """
    K = [math.exp(p - r[4]) for p, r in zip(pref, rows)]
    kd, rplan, cplan = pair_plans(rows, K)
    present = sorted({r[0] for r in rows} | {r[1] for r in rows})
    a = [1.0] * (N + 1)
    b = [1.0] * (N + 1)
    sumprod = math.sumprod
    target = SINKHORN_TOL / 100
    passes = 0
    best, stale = 1.0, 0
    reach = None

    def residual():
        worst = 0.0
        for i in present:
            R = sumprod(rplan[i][1], rplan[i][0](b)) if i in rplan else 0.0
            C = sumprod(cplan[i][1], cplan[i][0](a)) if i in cplan else 0.0
            if make[i] > 0:
                worst = max(worst, abs(a[i] * (kd[i] * b[i] + R) - make[i]) / make[i])
            if use[i] > 0:
                worst = max(worst, abs(b[i] * (kd[i] * a[i] + C) - use[i]) / use[i])
        return worst

    while True:
        passes += 1
        assert passes <= SWEEP_CAP, key + ': no convergence in %d passes' % SWEEP_CAP
        change = 0.0
        for i in present:
            R = sumprod(rplan[i][1], rplan[i][0](b)) if i in rplan else 0.0
            C = sumprod(cplan[i][1], cplan[i][0](a)) if i in cplan else 0.0
            p, q = node_solve(kd[i], R, C, make[i], use[i], a[i])
            change = max(change, abs(p / a[i] - 1), abs(q / b[i] - 1))
            a[i], b[i] = p, q
        if reach is None and passes <= 300 and residual() <= COLD_TARGETS[-1]:
            reach = passes
        if change < 1e-9 and residual() <= target:
            # keep going until the passes stop moving a and b (the margins are then at the floor of double precision):
            # a nation whose own pair is nearly everything has a tiny import that is the difference of two big margins
            if change < best:
                best, stale = change, 0
            else:
                stale += 1
            if change < 1e-15 or stale >= 8:
                break
    worst, limited, at = 0.0, 0, None
    rsum = [[] for _ in range(N + 1)]
    csum = [[] for _ in range(N + 1)]
    for r, k in zip(rows, K):
        x = a[r[0]] * b[r[1]] * k
        err = abs(x / r[2] - 1)
        # A flow under 1e-7 of its row's or column's margin is the difference of two margins that double precision holds
        # to 1e-16 of the larger: it cannot come back to 1e-9 relative, only to a few ulps of the margin.
        allow = FLOW_TOL + MARGIN_ULPS * min(make[r[0]], use[r[1]]) / r[2]
        if allow > 2 * FLOW_TOL:
            limited += 1
        elif err > worst:
            worst = err
        if err > allow:
            at = r
            assert False, '%s: a flow comes back %.3g off: %s to %s, flow %g of make %g and use %g (after %d passes)' % (
                key, err, common.NATIONS[at[0] - 1], common.NATIONS[at[1] - 1], at[2], make[at[0]], use[at[1]], passes)
        rsum[r[0]].append(x)
        csum[r[1]].append(x)
    for i in present:
        assert abs(math.fsum(rsum[i]) - make[i]) <= SINKHORN_TOL * make[i], key + ': row margin'
        assert abs(math.fsum(csum[i]) - use[i]) <= SINKHORN_TOL * use[i], key + ': column margin'
    return passes, worst, limited, reach


def cold_sweeps(rows, pref, make, use, targets=COLD_TARGETS, cap=COLD_CAP):
    """Plain Sinkhorn from a = b = 1 (the game's January solve, cold): the sweeps until every row is within each target
    of its margin (the columns are exact after each sweep), None for a target not reached in cap sweeps."""
    K = [math.exp(p - r[4]) for p, r in zip(pref, rows)]
    byrow = {}
    bycol = {}
    for e, r in enumerate(rows):
        byrow.setdefault(r[0], []).append(e)
        bycol.setdefault(r[1], []).append(e)

    def plan(groups, other):
        out = []
        for i, es in sorted(groups.items()):
            idx = [rows[e][other] for e in es]
            w = [K[e] for e in es]
            if len(idx) == 1:
                idx, w = idx * 2, w + [0.0]
            out.append((i, operator.itemgetter(*idx), w))
        return out

    rplan = plan(byrow, 1)
    cplan = plan(bycol, 0)
    a = [1.0] * (N + 1)
    b = [1.0] * (N + 1)
    sumprod = math.sumprod
    got = {}
    sweeps = 0
    while True:
        s = [(i, sumprod(w, g(b))) for i, g, w in rplan]
        dev = max(abs(a[i] * x - make[i]) / make[i] for i, x in s)
        for t in targets:
            if t not in got and dev <= t:
                got[t] = sweeps
        if len(got) == len(targets) or sweeps >= cap:
            break
        sweeps += 1
        for i, x in s:
            a[i] = make[i] / x
        for j, g, w in cplan:
            b[j] = use[j] / sumprod(w, g(a))
    return [got.get(t) for t in targets]


# --------------------------------------------------------------------------------------------------------------- main

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--sources', default='C:/Users/Jerus/Documents/world-sources')
    ap.add_argument('--work', default=None)
    ap.add_argument('--stand-in', action='store_true', help='testing only: WDI and CERDI for any input that is missing')
    ap.add_argument('--no-samples', action='store_true')
    ap.add_argument('--cache', action='store_true', help='development: keep the aggregated BACI in WORK/econ/baci-agg.pkl')
    ap.add_argument('--only', default=None, help='development: comma-separated good keys to solve and write')
    args = ap.parse_args()
    sources = args.sources
    work = args.work or sources + '/work'
    out_dir = work + '/econ'
    owners_path = work + '/geo/owners.tsv'
    freight_path = work + '/geo/freight.bin'
    gdp_path = work + '/econ/gdp-base.tsv'
    stand = []
    if not os.path.exists(gdp_path):
        stand.append('gdp')
    if not os.path.exists(freight_path):
        stand.append('freight')
    if not os.path.exists(owners_path):
        stand.append('owners')
    if stand and not args.stand_in:
        sys.exit('missing inputs %s (run geo.py and econ_paths.py first, or --stand-in for a test run)' % stand)
    if stand:
        out_dir = work + '/econ/stand-in'
        log('STAND-IN for %s: a test run, written under %s' % (stand, out_dir))
    if args.only:
        out_dir += '/only'
    os.makedirs(out_dir, exist_ok=True)

    log('goods and concordance')
    goods = load_goods(HERE + '/goods.tsv')
    G = len(goods)
    check_against_java(goods)
    conc = load_concordance(HERE + '/concordance.tsv', goods)

    zpath = sources + '/baci/BACI_HS22_V202601.zip'
    member = 'BACI_HS22_Y%d_V202601.csv' % common.BASE_YEAR
    with zipfile.ZipFile(zpath) as z:
        codes = [r['code'] for r in csv.DictReader(z.read('product_codes_HS22_V202601.csv').decode('utf-8').splitlines())]
        cc = z.read('country_codes_V202601.csv').decode('utf-8').splitlines()
    hs6, hs_count, unmatched = resolve(codes, conc, goods)
    log('%d HS 2022 subheadings, %d map to a good; %d concordance prefixes in %d rows'
        % (len(codes), len(hs6), len(conc), sum(len(m) for m in conc.values())))
    if unmatched:
        log('WARNING concordance prefixes that win no subheading: ' + ' '.join(unmatched))
    log('subheadings per good: ' + ' '.join('%s %d' % (g['key'], c) for g, c in zip(goods, hs_count)))
    assert all(c > 0 for c in hs_count), 'a good with no subheading: ' + ' '.join(g['key'] for g, c in zip(goods, hs_count) if not c)

    owners = {} if 'owners' in stand else econ_trade_countries.read_owners(owners_path)
    by_code, report = econ_trade_countries.build(cc, owners)
    how = {}
    for code, iso3, name, h, nation in report:
        how.setdefault(h, []).append('%s>%s' % (iso3, nation))
    for h, lst in sorted(how.items()):
        log('countries %s: %d%s' % (h, len(lst), '' if h == 'direct' else ' (' + ' '.join(lst) + ')'))

    cache = work + '/econ/baci-agg.pkl'
    if args.cache and os.path.exists(cache):
        with open(cache, 'rb') as f:
            agg, st = pickle.load(f)
        log('BACI aggregate from the cache')
    else:
        log('streaming BACI %s' % member)
        with zipfile.ZipFile(zpath) as z:
            agg, st = stream_baci(z, member, hs6, by_code, G)
        log('%d rows, %d in the subheadings of the goods, %d pair sums' % (st['rows'], st['mapped'], len(agg)))
        if args.cache:
            with open(cache, 'wb') as f:
                pickle.dump((agg, st), f)
    tot, nat, same = sum(st['total']), sum(st['nat']), sum(st['same'])
    log('BACI value of the goods\' subheadings: US$ %.1f billion; dropped for a country that is no nation %.2f%%, for the same '
        'nation at both ends %.2f%%' % (tot / 1e6, 100 * nat / tot, 100 * same / tot))

    # per good
    bygood = [[] for _ in range(G)]
    for key, e in agg.items():
        if e[0] > 0:
            bygood[key >> 16].append(((key >> 8) & 255, key & 255, e[0], e[1] if e[2] else NAN))

    log('inputs: gdp, freight')
    gdp = stand_in_gdp(sources) if 'gdp' in stand else read_gdp(gdp_path)
    sea, land, cost = stand_in_freight(sources) if 'freight' in stand else read_freight(freight_path)
    missing = [common.NATIONS[i - 1] for i in range(1, N + 1) if gdp[i] <= 0]
    log('gdp_usd of %d: %d nations without one (%s)' % (common.BASE_YEAR, len(missing), ' '.join(missing)))
    ranked = sorted((i for i in range(1, N + 1) if gdp[i] > 0), key=lambda i: (-gdp[i], i))
    top20 = set(ranked[:TOP_ECONOMIES])
    log('the %d largest economies: %s' % (TOP_ECONOMIES, ' '.join(common.NATIONS[i - 1] for i in ranked[:TOP_ECONOMIES])))
    order = nearness(cost)

    only = set(args.only.split(',')) if args.only else None
    results = []
    shares = []
    for gi, g in enumerate(goods):
        if only and g['key'] not in only:
            continue
        pairs = bygood[gi]
        total_v = sum(p[2] for p in pairs)
        kept, nmake, ntop = partner_rule(pairs, top20, order)
        kept_v = sum(p[2] for p in kept)
        r = solve_good(g, kept, gdp, sea, land)
        r['kept_share'] = kept_v / total_v
        r['gi'] = gi
        results.append(r)
        shares.append((r['kept_share'], g['key']))
        cold = ' cold sweeps to 1%%/0.01%%: %s/%s' % tuple('>%d' % COLD_CAP if c is None else str(c) for c in r['cold']) if r['cold'] else ''
        log('%-16s %5d pairs, kept %5d (%.1f%% of the value), %3d makers, uv %9.1f US$/t, K %5d, %3d passes (flows %.0e off, %d limited by their margins; 0.01%% at %s)%s'
            % (g['key'], len(pairs), len(kept), 100 * r['kept_share'], nmake, r['unit_value'], len(r['pairs']), r['sweeps'],
               r['worst'], r['limited'], r['reach'] or '>300', cold))
    shares.sort()
    log('kept share of the value: %.1f%% to %.1f%%; the lowest %s' % (
        100 * shares[0][0], 100 * shares[-1][0], ', '.join('%s %.1f%%' % (k, 100 * s) for s, k in shares[:5])))
    sw = [r['sweeps'] for r in results]
    log('exact passes from 1: %d to %d, mean %.0f; every margin to %g and every flow back to %g' % (min(sw), max(sw), sum(sw) / len(sw),
                                                                                                  SINKHORN_TOL, FLOW_TOL))
    rc = [r['reach'] for r in results if r['reach']]
    log('exact passes to a margin error of %g: %d of %d goods within 300, %d to %d, mean %.0f'
        % (COLD_TARGETS[-1], len(rc), len(results), min(rc), max(rc), sum(rc) / len(rc)))
    if COLD:
        for n, t in enumerate(COLD_TARGETS):
            c = [r['cold'][n] for r in results if r['cold'][n] is not None]
            log('plain Sinkhorn from 1 to %g: %d of %d goods within %d sweeps, %d to %d sweeps, mean %.0f'
                % (t, len(c), len(results), COLD_CAP, min(c), max(c), sum(c) / len(c)))

    # write
    with open(out_dir + '/goods.tsv', 'w', encoding='utf-8', newline='\n') as f:
        f.write('number\tkey\tname\tunit\tclass\telasticity\tunit_value\tunits_per_tonne\ttraded_share\ths6\tkept_share\n')
        for r in results:
            g = goods[r['gi']]
            f.write('\t'.join([g['number'], g['key'], g['name'], g['unit'], g['class'], repr(g['elasticity']), repr(r['unit_value']),
                               repr(g['units_per_tonne']), repr(g['traded_share']), str(hs_count[r['gi']]), repr(r['kept_share'])]) + '\n')
    with open(out_dir + '/trade.bin', 'wb') as f:
        f.write(struct.pack('>iii', N, len(results), common.BASE_YEAR))
        for r in results:
            f.write(struct.pack('>i', len(r['pairs'])))
            for s, b, flow, t, c, pf, mark in r['pairs']:
                f.write(struct.pack('>hhddddb', s, b, flow, t, c, pf, mark))
            for j in range(1, N + 1):
                f.write(struct.pack('>dd', r['make'][j], r['use'][j]))
    with open(out_dir + '/meta-trade.tsv', 'w', encoding='utf-8', newline='\n') as f:
        for name, value, sentence in DIALS:
            f.write('dial\t%s\t%s\t%s\n' % (name, repr(value), sentence))
        f.write('\t'.join(SOURCE_ROW) + '\n')
    log('wrote goods.tsv, trade.bin (%d bytes), meta-trade.tsv in %s' % (os.path.getsize(out_dir + '/trade.bin'), out_dir))

    if not args.no_samples:
        econ_trade_samples.run(zpath, member, HERE + '/concordance.tsv', HERE + '/goods.tsv', by_code,
                               out_dir + '/baci-samples.tsv', log)
    log('done')


if __name__ == '__main__':
    main()
