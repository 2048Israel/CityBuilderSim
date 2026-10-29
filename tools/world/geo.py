"""The world pipeline's geography (batch WN0, slice G, 2026-10-28): from Natural Earth and CERDI's sea distances it
makes the owners table, the nations' nodes and label points, the freight table, the owner raster and the border
lines, which bake.py puts in world-1.bin (FORMAT.txt).

WHY: the nations' world needs to know, for every one-degree tile of the Earth, whose land it is, and what a tonne
costs to move from any nation to any other; no one source says both. Natural Earth draws the countries (with their
territories and the disputed ground a nation of the 196 does not hold) and CERDI measures the sea routes between
ports. Run every stage with python -I (the sources are downloads); stages run in the order below, or alone:
  owners   -> WORK/geo/owners.tsv      whose land every Natural Earth country feature is (the 196, a territory's
                                       sovereign, or an area of its own)
  nations  -> WORK/geo/nations-geo.tsv each nation's node (its largest port city), label point, landlocked
  freight  -> WORK/geo/freight.bin     CERDI's routes as edges, then their closure (the layer's payload)
  raster   -> WORK/geo/owner.index, owner.blobs   the owner of every 15" cell, tile by tile
  lines    -> WORK/geo/lines.bin       the admin-0 land boundary lines with their two sides' owners
  meta     -> WORK/geo/meta-geo.tsv    this slice's dials and sources, for the file's meta layer
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import json
import math
import struct
import time
import zlib
import multiprocessing
from collections import deque

import common
import xlsx

# ----- the dials: each goes to meta as a dial row (see DIALS at the foot of this block) -----
# A populated place is a port city when a Natural Earth port lies within this many km of it.
PORT_KM = 25.0
# Nodes named by hand where Natural Earth's POP_MAX ranks the wrong place first: South Sudan's capital and largest
# city is Juba (about 525,000 people, the 2023 estimates), but POP_MAX gives Yei 185,000 against Juba's 111,975.
NODE_OVERRIDES = {'SSD': 'Juba'}
# The Earth's mean radius, km: the great circle between two points.
EARTH_RADIUS_KM = 6371.0
# The Natural Earth feature types that are territories (their land goes to the sovereign when it is one of the
# 196); an Indeterminate feature is an area of its own even when Natural Earth names a sovereign (Palestine).
TERRITORY_TYPES = ('Dependency', 'Lease', 'Country', 'Sovereignty', 'Disputed')
# The Natural Earth feature types a nation's own land is made of (a feature coded as the nation but of another
# type, Baikonur or Clipperton, is a territory the nation holds, not the place its node is chosen from).
OWN_TYPES = ('Sovereign country', 'Country', 'Sovereignty', 'Disputed')
# The areas without a coast (the rest of the areas are taken as coastal).
AREA_LANDLOCKED = ('BRT', 'CNM', 'KAS', 'KOS', 'VAT')

NE = '/naturalearth/'
NE_COUNTRIES = 'ne_10m_admin_0_countries.geojson'
NE_LINES = 'ne_10m_admin_0_boundary_lines_land.geojson'
NE_PLACES = 'ne_10m_populated_places_simple.geojson'
NE_PORTS = 'ne_10m_ports.geojson'
CERDI_FILE = '/cerdi/CERDI-seadistance_V1.1.xlsx'

DIALS = [
    ('PORT_KM', PORT_KM, 'A populated place is a port city when a Natural Earth port lies within this many km of it.'),
    ('EARTH_RADIUS_KM', EARTH_RADIUS_KM, "The Earth's mean radius, km: the great circle between two points."),
    ('OWNER_CPD', common.OWNER_CPD, "The owner layer's cells a degree (15 arc-seconds, the earth file's finest grid)."),
    ('LINE_SCALE', common.LINE_SCALE,
     "A boundary line's coordinates are whole multiples of 1/LINE_SCALE of a degree (about a metre)."),
]
SOURCES = [
    ('naturalearth', 'Natural Earth 5.1.2 (admin-0 countries, land boundary lines, populated places, ports; 1:10m)',
     'https://github.com/nvkelso/natural-earth-vector (tag v5.1.2)', 'public domain', '2026-10-28'),
    ('cerdi', 'CERDI-seadistance 1.1 (Bertoli, Goujon and Santoni)', 'https://zenodo.org/record/240493', 'CC BY 4.0',
     '2026-10-28'),
]


# ===================================================================== reading Natural Earth
def load_features(path):
    """A geojson file's features."""
    with open(path, encoding='utf-8') as f:
        return json.load(f)['features']


def polygons(geom):
    """A country feature's polygons, each a list of rings (the outer ring first), a ring a list of (lon, lat)."""
    if geom['type'] == 'Polygon':
        return [geom['coordinates']]
    return geom['coordinates']


def feature_code(p):
    """A country feature's code: ISO_A3_EH, or ADM0_A3 where that is -99."""
    c = p['ISO_A3_EH']
    return p['ADM0_A3'] if c in (None, '-99', '') else c


def gc_km(lat1, lon1, lat2, lon2):
    """The great circle between two points, km."""
    p1, p2 = math.radians(lat1), math.radians(lat2)
    dp, dl = p2 - p1, math.radians(lon2 - lon1)
    a = math.sin(dp / 2) ** 2 + math.cos(p1) * math.cos(p2) * math.sin(dl / 2) ** 2
    return 2 * EARTH_RADIUS_KM * math.asin(min(1.0, math.sqrt(a)))


# ===================================================================== 1. OWNERS
def stage_owners(src, work):
    """Each country feature's owner (see the module's rules): rule a, its code is one of the 196; rule b, a
    territory of one; rule c, an area of its own, numbered after the 196 in the order of the codes."""
    feats = load_features(src + NE + NE_COUNTRIES)
    N = set(common.NATIONS)
    codes = [feature_code(f['properties']) for f in feats]
    held = {}   # ADMIN -> nation code, by rule a: how a territory finds its sovereign
    for f, c in zip(feats, codes):
        if c in N:
            held.setdefault(f['properties']['ADMIN'], c)
    area_codes = sorted(set(c for f, c in zip(feats, codes) if c not in N and not
                            (f['properties']['TYPE'] in TERRITORY_TYPES and
                             held.get(f['properties']['SOVEREIGNT']) is not None)))
    area_number = {c: len(N) + i + 1 for i, c in enumerate(area_codes)}
    rows, terr, areas = [], [], []
    for f, c in zip(feats, codes):
        p = f['properties']
        if c in N:
            owner, rule = c, 'a'
        elif p['TYPE'] in TERRITORY_TYPES and held.get(p['SOVEREIGNT']) is not None:
            owner, rule = held[p['SOVEREIGNT']], 'b'
            terr.append((c, p['NAME'], p['TYPE'], owner))
        else:
            owner, rule = 'area:' + c, 'c'
            areas.append((c, p['NAME'], p['TYPE'], p['SOVEREIGNT']))
        num = common.number(owner) if rule != 'c' else area_number[c]
        rows.append([c, p['ADMIN'], p['TYPE'], p['SOVEREIGNT'], owner, num, p['ADM0_A3'], rule])
    assert len(set(r[6] for r in rows)) == len(rows), 'ADM0_A3 is not unique'
    common.write_tsv(work + '/geo/owners.tsv',
                     ['code', 'admin', 'type', 'sovereignt', 'owner', 'number', 'adm0_a3', 'rule'], rows)
    common.log('owners: %d features: %d rule a (a nation), %d territories (b), %d areas (c)'
               % (len(rows), sum(1 for r in rows if r[7] == 'a'), len(terr), len(areas)))
    print('  territories (code name type -> owner):')
    for t in terr:
        print('    %s %s (%s) -> %s' % t)
    print('  areas (number code name type sovereignt):')
    for a in areas:
        print('    %d %s %s (%s; NE sovereignt %s)' % ((area_number[a[0]],) + a))
    return rows


def load_owners(work):
    """owners.tsv as dicts, and {ADM0_A3: owner number} (the lines' sides are named by ADM0_A3)."""
    header, rows = common.read_tsv(work + '/geo/owners.tsv')
    recs = [dict(zip(header, r)) for r in rows]
    by_adm0 = {r['adm0_a3']: int(r['number']) for r in recs}
    return recs, by_adm0


# ===================================================================== 3. NATIONS' GEOGRAPHY
def _bbox(rings):
    xs = [x for r in rings for x, _ in r]
    ys = [y for r in rings for _, y in r]
    return min(xs), min(ys), max(xs), max(ys)


def _inside(x, y, rings):
    """Even-odd: whether (x, y) is inside a polygon's rings."""
    inside = False
    for ring in rings:
        j = len(ring) - 1
        for i in range(len(ring)):
            xi, yi = ring[i]
            xj, yj = ring[j]
            if (yi > y) != (yj > y) and x < (xj - xi) * (y - yi) / (yj - yi) + xi:
                inside = not inside
            j = i
    return inside


def stage_nations(src, work):
    """nations-geo.tsv: per nation its node (the most populous place of its own features within PORT_KM of a port,
    or its most populous place), its label point and whether it is landlocked; then the areas."""
    feats = load_features(src + NE + NE_COUNTRIES)
    recs, by_adm0 = load_owners(work)
    N = common.NATIONS
    own = {c: [] for c in N}   # nation -> [(feature index, bbox, polygons with bboxes)]
    for k, (f, r) in enumerate(zip(feats, recs)):
        p = f['properties']
        if r['rule'] == 'a' and p['TYPE'] in OWN_TYPES:
            polys = [(_bbox(pg), pg) for pg in polygons(f['geometry'])]
            fb = (min(b[0][0] for b in polys), min(b[0][1] for b in polys),
                  max(b[0][2] for b in polys), max(b[0][3] for b in polys))
            own[r['code']].append((k, fb, polys))
    places = []
    for f in load_features(src + NE + NE_PLACES):
        p = f['properties']
        places.append((p['pop_max'] or 0, p['name'], p['latitude'], p['longitude'], p['adm0_a3']))
    places.sort(key=lambda t: (-t[0], t[1]))
    ports = {}
    for f in load_features(src + NE + NE_PORTS):
        lon, lat = f['geometry']['coordinates'][:2]
        ports.setdefault((math.floor(lat), math.floor(lon)), []).append((lat, lon))

    def near_port(lat, lon):
        k = min(12, int(math.ceil(1.0 / max(0.08, math.cos(math.radians(lat))))))
        for a in range(math.floor(lat) - 1, math.floor(lat) + 2):
            for b in range(math.floor(lon) - k, math.floor(lon) + k + 1):
                for plat, plon in ports.get((a, b), ()):
                    if gc_km(lat, lon, plat, plon) <= PORT_KM:
                        return True
        return False

    inside = {c: [] for c in N}   # nation -> its places inside its own features, largest first
    for pl in places:
        pop, name, lat, lon, adm0 = pl
        for c in N:
            hit = False
            for k, fb, polys in own[c]:
                if fb[0] <= lon <= fb[2] and fb[1] <= lat <= fb[3]:
                    for b, pg in polys:
                        if b[0] <= lon <= b[2] and b[1] <= lat <= b[3] and _inside(lon, lat, pg):
                            hit = True
                            break
                if hit:
                    break
            if hit:
                inside[c].append(pl)
                break   # the first nation whose own features hold it (the features do not overlap)
    common.log('nations: %d places placed in %d nations' % (sum(len(v) for v in inside.values()),
                                                           sum(1 for v in inside.values() if v)))
    rows, notes = [], {'port': 0, 'largest': 0, 'adm0': 0, 'label': 0}
    fell = []
    for n, c in enumerate(N, 1):
        mains = [(k, fb, polys) for k, fb, polys in own[c]]
        assert mains, c
        # the label: the feature whose ADMIN is its SOVEREIGNT, else the one with the most vertices
        best = None
        for k, fb, polys in mains:
            p = feats[k]['properties']
            score = (1 if p['ADMIN'] == p['SOVEREIGNT'] else 0, sum(len(r) for _, pg in polys for r in pg))
            if best is None or score > best[0]:
                best = (score, k)
        p = feats[best[1]]['properties']
        landlocked = c in common.LANDLOCKED
        cand = inside[c]
        node, how = None, None
        if c in NODE_OVERRIDES:
            node = next(pl for pl in cand if pl[1] == NODE_OVERRIDES[c])
            how = 'largest'
        if node is None and cand and not landlocked:
            for pl in cand:
                if near_port(pl[2], pl[3]):
                    node, how = pl, 'port'
                    break
        if node is None and cand:
            node, how = cand[0], 'largest'
        if node is None:
            adm = set(feats[k]['properties']['ADM0_A3'] for k, _, _ in mains)
            same = [pl for pl in places if pl[4] in adm]
            if same:
                node, how = same[0], 'adm0'
            else:   # no place of Natural Earth's belongs to it (Nauru): its label point, named for the nation
                node, how = (0, p['NAME'], p['LABEL_Y'], p['LABEL_X'], c), 'label'
        notes[how] += 1
        if how in ('adm0', 'label'):
            fell.append((c, how, node[1]))
        rows.append([n, c, p['NAME'], 'nation', c, node[1], round(node[2], 4), round(node[3], 4),
                     round(p['LABEL_Y'], 4), round(p['LABEL_X'], 4), 1 if landlocked else 0])
    # the areas: NE's label point stands for both the node and the label (an area has no port city to choose)
    seen = set()
    for f, r in zip(feats, recs):
        if r['rule'] == 'c' and r['code'] not in seen:
            seen.add(r['code'])
            p = f['properties']
            lat, lon = round(p['LABEL_Y'], 4), round(p['LABEL_X'], 4)
            rows.append([int(r['number']), r['code'], p['NAME'], 'area', '-', '-', lat, lon, lat, lon,
                         1 if r['code'] in AREA_LANDLOCKED else 0])
    rows.sort(key=lambda r: r[0])
    assert [r[0] for r in rows] == list(range(1, len(rows) + 1))
    common.write_tsv(work + '/geo/nations-geo.tsv',
                     ['number', 'iso3', 'name', 'kind', 'seller', 'node_name', 'node_lat', 'node_lon', 'label_lat',
                      'label_lon', 'landlocked'], rows)
    common.log('nations: %d nations + %d areas; nodes: %d port cities, %d largest places (landlocked or no port '
               'near), %d by ADM0 code, %d at the label point' % (len(N), len(rows) - len(N), notes['port'],
                                                              notes['largest'], notes['adm0'], notes['label']))
    for c, how, name in fell:
        print('  node fallback (%s): %s -> %s' % (how, c, name))
    return rows


def load_nations_geo(work):
    """nations-geo.tsv as a list of dicts."""
    header, rows = common.read_tsv(work + '/geo/nations-geo.tsv')
    return [dict(zip(header, r)) for r in rows]


# ===================================================================== 5. LINES
def stage_lines(src, work):
    """lines.bin: the lines layer's payload. A feature's every line string is a line, its sides the owners of
    ADM0_A3_L and ADM0_A3_R."""
    recs, by_adm0 = load_owners(work)
    feats = load_features(src + NE + NE_LINES)
    out = []
    unknown = {}
    same = 0
    nlines = 0
    for f in feats:
        p = f['properties']
        sides = []
        for key in ('ADM0_A3_L', 'ADM0_A3_R'):
            code = p[key]
            num = by_adm0.get(code, 0)
            if num == 0:
                unknown[code] = unknown.get(code, 0) + 1
            sides.append(num)
        g = f['geometry']
        if g is None:
            continue
        strings = [g['coordinates']] if g['type'] == 'LineString' else g['coordinates']
        for ls in strings:
            sc = common.LINE_SCALE
            pts = [(int(round(c[1] * sc)), int(round(c[0] * sc))) for c in ls]   # (lat, lon) as stored
            out.append(struct.pack('>hhi', sides[0], sides[1], len(pts)) +
                       struct.pack('>%di' % (2 * len(pts)), *[v for pt in pts for v in pt]))
            nlines += 1
            if sides[0] == sides[1]:
                same += 1
    payload = struct.pack('>i', nlines) + b''.join(out)
    with open(work + '/geo/lines.bin', 'wb') as fh:
        fh.write(payload)
    common.log('lines: %d line strings from %d features, %d bytes; %d with the same owner on both sides; '
               'side codes not in owners.tsv: %s' % (nlines, len(feats), len(payload), same, unknown or 'none'))
    return payload


# ===================================================================== 2. FREIGHT
def closure(n, edges):
    """FORMAT.txt's closure: edges are (a, b, sea, land, kind) with a, b 0-based indices; the matrix starts at the
    cheapest edge of each pair (the first on a tie), then Floyd-Warshall with k, i, j ascending, a route replaced
    only when strictly cheaper, sea and land km carried along. Returns (sea, land, cost) as lists of rows."""
    inf = float('inf')
    lps = common.LAND_PER_SEA
    S = [[inf] * n for _ in range(n)]
    L = [[inf] * n for _ in range(n)]
    C = [[inf] * n for _ in range(n)]
    for i in range(n):
        S[i][i] = L[i][i] = C[i][i] = 0.0
    for a, b, sea, land, _ in edges:
        c = sea + lps * land
        if c < C[a][b]:
            S[a][b] = S[b][a] = sea
            L[a][b] = L[b][a] = land
            C[a][b] = C[b][a] = c
    for k in range(n):
        Sk, Lk = S[k], L[k]
        for i in range(n):
            sik = S[i][k]
            if sik == inf:
                continue
            lik = L[i][k]
            Si, Li, Ci = S[i], L[i], C[i]
            for j in range(n):
                s = sik + Sk[j]
                l = lik + Lk[j]
                c = s + lps * l
                if c < Ci[j]:
                    Si[j] = s
                    Li[j] = l
                    Ci[j] = c
    return S, L, C


def build_edges(src, work):
    """The freight layer's edges from CERDI and the boundary lines, as (a, b, sea, land, kind), a < b, a and b the
    nations' numbers (1-based); also the list of nations CERDI lacks and the counts by kind."""
    N = common.NATIONS
    num = {c: i + 1 for i, c in enumerate(N)}
    geo = load_nations_geo(work)
    node = {g['iso3']: (float(g['node_lat']), float(g['node_lon'])) for g in geo if g['kind'] == 'nation'}
    edges = []
    present = set()
    n0 = n1 = 0
    rows = xlsx.rows(src + CERDI_FILE, 'CERDIseadistance')
    header = next(rows)
    assert header[:6] == ['iso1', 'iso2', 'seadistance', 'capitalport1', 'capitalport2', 'roaddistance'], header
    for r in rows:
        r = r + [''] * (7 - len(r))
        a, b = r[0], r[1]
        if a in num:
            present.add(a)
        if a not in num or b not in num or not a < b:
            continue
        sea = float(r[2]) if r[2] != '' else None
        if sea is not None:
            land = 0.0
            if a in common.LANDLOCKED:
                land += float(r[3])
            if b in common.LANDLOCKED:
                land += float(r[4])
            edges.append((num[a], num[b], sea, land, 0))
            n0 += 1
        if r[5] != '':
            edges.append((num[a], num[b], 0.0, float(r[5]), 1))
            n1 += 1
    lacking = [c for c in N if c not in present]
    # neighbours from the boundary lines' two sides (a territory's side is its sovereign's)
    recs, by_adm0 = load_owners(work)
    nat_of = {v: k for k, v in num.items()}
    nbrs = {}
    for f in load_features(src + NE + NE_LINES):
        p = f['properties']
        x, y = by_adm0.get(p['ADM0_A3_L'], 0), by_adm0.get(p['ADM0_A3_R'], 0)
        if x != y and 1 <= x <= len(N) and 1 <= y <= len(N):
            nbrs.setdefault(x, set()).add(y)
            nbrs.setdefault(y, set()).add(x)
    n2 = n3 = 0
    seen = set()
    isle = []
    for c in lacking:
        i = num[c]
        mine = sorted(nbrs.get(i, ()))
        if mine:
            for j in mine:
                key = (min(i, j), max(i, j))
                if key in seen:
                    continue
                seen.add(key)
                d = gc_km(*node[c], *node[nat_of[j]]) * common.LAND_DETOUR
                edges.append((key[0], key[1], 0.0, d, 2))
                n2 += 1
        else:
            near = min((x for x in N if x not in lacking), key=lambda x: gc_km(*node[c], *node[x]))
            d = gc_km(*node[c], *node[near]) * common.SEA_DETOUR
            edges.append((min(i, num[near]), max(i, num[near]), d, 0.0, 3))
            n3 += 1
            isle.append((c, near))
    edges.sort(key=lambda e: (e[0], e[1], e[4]))
    return edges, lacking, (n0, n1, n2, n3), isle


def stage_freight(src, work):
    """freight.bin: the layer's payload, int N, three double[N x N] (sea km, land km, cost), then the edges."""
    N = len(common.NATIONS)
    edges, lacking, counts, isle = build_edges(src, work)
    common.log('freight: %d edges (kind 0: %d, kind 1: %d, kind 2: %d, kind 3: %d); CERDI lacks %s'
               % ((len(edges),) + counts + (lacking,)))
    if isle:
        print('  islands joined to the nearest coast (kind 3):', isle)
    S, L, C = closure(N, [(a - 1, b - 1, s, l, k) for a, b, s, l, k in edges])
    for i in range(N):
        for j in range(N):
            assert C[i][j] < float('inf'), 'unreachable pair %s %s' % (common.NATIONS[i], common.NATIONS[j])
            assert S[i][j] == S[j][i] and L[i][j] == L[j][i] and C[i][j] == C[j][i]
    # the cost is the definition on the final sea and land km, so a reader may recompute it exactly
    lps = common.LAND_PER_SEA
    C = [[0.0 if i == j else S[i][j] + lps * L[i][j] for j in range(N)] for i in range(N)]
    parts = [struct.pack('>i', N)]
    for M in (S, L, C):
        parts.append(struct.pack('>%dd' % (N * N), *[v for row in M for v in row]))
    parts.append(struct.pack('>i', len(edges)))
    for a, b, s, l, k in edges:
        parts.append(struct.pack('>hhddb', a, b, s, l, k))
    payload = b''.join(parts)
    with open(work + '/geo/freight.bin', 'wb') as fh:
        fh.write(payload)
    far = max(((C[i][j], i, j) for i in range(N) for j in range(i + 1, N)))
    common.log('freight: closure finite for all %d pairs; dearest %s-%s cost %.0f (sea %.0f km, land %.0f km); '
               '%d bytes' % (N * (N - 1) // 2, common.NATIONS[far[1]], common.NATIONS[far[2]], far[0],
                             S[far[1]][far[2]], L[far[1]][far[2]], len(payload)))
    return payload


# ===================================================================== 4. OWNER RASTER
CPD = common.OWNER_CPD
COLS = 360 * CPD
_W = {}   # a worker's edges, buckets and owners (built once per process)


def _build_worker(src, work):
    """Every ring edge of every country feature as (y1, y2, x at y1, dx/dy, feature index), y1 < y2, bucketed by
    the one-degree bands it touches; and each feature's owner number."""
    feats = load_features(src + NE + NE_COUNTRIES)
    recs, _ = load_owners(work)
    owner = [int(r['number']) for r in recs]
    edges = []
    for fid, f in enumerate(feats):
        for pg in polygons(f['geometry']):
            for ring in pg:
                n = len(ring)
                for i in range(n if ring[0] != ring[-1] else n - 1):
                    x1, y1 = ring[i][0], ring[i][1]
                    x2, y2 = ring[(i + 1) % n][0], ring[(i + 1) % n][1]
                    if y1 == y2:
                        continue
                    if y1 > y2:
                        x1, y1, x2, y2 = x2, y2, x1, y1
                    edges.append((y1, y2, x1, (x2 - x1) / (y2 - y1), fid))
    buckets = [[] for _ in range(180)]
    for e in edges:
        lo = max(0, math.floor(e[0]) + 90)
        hi = min(179, math.floor(e[1]) + 90)
        for b in range(lo, hi + 1):
            buckets[b].append(e)
    _W['buckets'] = buckets
    _W['owner'] = owner


def _fill(g):
    """Gives every unowned cell of a 240 x 240 tile (a bytearray) its nearest owned cell's owner: four-neighbour
    steps, ties to the lower owner number, level by level so each cell takes the lowest of the nearest."""
    W = CPD
    n = len(g)
    zeros = [i for i in range(n) if g[i] == 0]
    if not zeros:
        return
    frontier = set()
    for z in zeros:
        for nb in _nbrs(z, n, W):
            if g[nb]:
                frontier.add(nb)
    while frontier:
        new = {}
        for c in frontier:
            o = g[c]
            for nb in _nbrs(c, n, W):
                if g[nb] == 0:
                    q = new.get(nb)
                    if q is None or o < q:
                        new[nb] = o
        for nb, o in new.items():
            g[nb] = o
        frontier = new.keys()


def _nbrs(c, n, W):
    x = c % W
    if x:
        yield c - 1
    if x < W - 1:
        yield c + 1
    if c >= W:
        yield c - W
    if c + W < n:
        yield c + W


def _band(args):
    """Rasterises one one-degree band (b = lat0 + 90): the even-odd spans of every feature on each of its 240 row
    centres, painted by owner (the lower number wins an overlap); then each tile of the band: uniform when it holds
    one owner or none, else the 57,600-byte grid with every unowned cell filled. Returns (b, [(tile, owner or None,
    blob or None)], seconds, seen) for the tiles that have an owner."""
    b, src, work = args
    t0 = time.time()
    if 'buckets' not in _W:
        _build_worker(src, work)
    owner = _W['owner']
    lat0 = b - 90
    rows = [[] for _ in range(CPD)]   # per local row: crossings (feature, x)
    for y1, y2, x1, dxdy, fid in _W['buckets'][b]:
        # rows l (0 = the band's north row) whose centre yc = lat0 + 1 - (l + 0.5)/CPD lies in [y1, y2)
        lo = max(0, math.floor((lat0 + 1 - y2) * CPD - 0.5) + 1)
        hi = min(CPD - 1, math.floor((lat0 + 1 - y1) * CPD - 0.5))
        for l in range(lo, hi + 1):
            yc = lat0 + 1 - (l + 0.5) / CPD
            rows[l].append((fid, x1 + (yc - y1) * dxdy))
    band = [None] * CPD
    cmin, cmax = COLS, -1
    for l in range(CPD):
        cr = rows[l]
        if not cr:
            continue
        cr.sort()
        spans = []
        i = 0
        m = len(cr)
        while i < m:
            j = i
            fid = cr[i][0]
            while j < m and cr[j][0] == fid:
                j += 1
            xs = [cr[k][1] for k in range(i, j)]
            for k in range(0, len(xs) - 1, 2):
                cs = max(0, math.ceil((xs[k] + 180) * CPD - 0.5))
                ce = min(COLS, math.ceil((xs[k + 1] + 180) * CPD - 0.5))
                if ce > cs:
                    spans.append((owner[fid], cs, ce))
            i = j
        if not spans:
            continue
        spans.sort(reverse=True)   # the highest number first, so the lowest paints last and wins
        row = bytearray(COLS)
        for o, cs, ce in spans:
            row[cs:ce] = bytes([o]) * (ce - cs)
            cmin, cmax = min(cmin, cs), max(cmax, ce - 1)
        band[l] = row
    out = []
    seen = 0
    blank = bytes(CPD)
    if cmax >= 0:
        for t in range(cmin // CPD, cmax // CPD + 1):
            a = t * CPD
            g = bytearray()
            for l in range(CPD):
                row = band[l]
                g += row[a:a + CPD] if row is not None else blank
            owners = set(g)
            owners.discard(0)
            if not owners:
                continue
            tid = b * 360 + t
            seen += 1
            if len(owners) == 1:
                out.append((tid, owners.pop(), None))
            else:
                _fill(g)
                out.append((tid, None, zlib.compress(bytes(g), common.ZLIB_LEVEL)))
    return b, out, time.time() - t0, seen


def stage_raster(src, work, bands=None, procs=None):
    """owner.index (int32 x 64,800 big-endian, the layer's entries) and owner.blobs (the blobs back to back)."""
    todo = list(bands) if bands else list(range(180))
    entries = [0] * common.TILES
    blobs = {}
    procs = procs or max(1, (os.cpu_count() or 2) - 1)
    t0 = time.time()
    args = [(b, src, work) for b in todo]
    done = 0
    with multiprocessing.Pool(procs) as pool:
        for b, out, secs, seen in pool.imap_unordered(_band, args):
            for tid, own, blob in out:
                if blob is None:
                    entries[tid] = -own
                else:
                    entries[tid] = len(blob)
                    blobs[tid] = blob
            done += 1
            if done % 20 == 0 or done == len(todo):
                common.log('raster: %d of %d bands done' % (done, len(todo)))
    uniform = sum(1 for e in entries if e < 0)
    full = sum(1 for e in entries if e > 0)
    nonowned = sum(1 for e in entries if e == 0)
    common.log('raster: %d tiles uniform with an owner, %d full (two owners or more, %d bytes of blobs), %d with no '
               'owner; %.0f s' % (uniform, full, sum(len(v) for v in blobs.values()), nonowned, time.time() - t0))
    if bands:
        return entries, blobs
    with open(work + '/geo/owner.index', 'wb') as fh:
        fh.write(struct.pack('>%di' % common.TILES, *entries))
    with open(work + '/geo/owner.blobs', 'wb') as fh:
        for tid in sorted(blobs):
            fh.write(blobs[tid])
    return entries, blobs


# ===================================================================== 6. META
def stage_meta(src, work):
    """meta-geo.tsv: this slice's dial rows (and the shared common.DIALS) and its source rows."""
    rows = []
    for name, val, sentence in common.DIALS + DIALS:
        rows.append(['dial', name, common.fnum(val), sentence])
    for s in SOURCES:
        rows.append(['source'] + list(s))
    with open(work + '/geo/meta-geo.tsv', 'w', encoding='utf-8', newline='\n') as f:
        for r in rows:
            f.write('\t'.join(r) + '\n')
    common.log('meta: %d dial rows, %d source rows' % (len(common.DIALS) + len(DIALS), len(SOURCES)))


STAGES = ['owners', 'nations', 'freight', 'raster', 'lines', 'meta']


def main():
    p = common.parser('The world pipeline geography: owners, nations, freight, raster, lines, meta.')
    p.add_argument('stages', nargs='*', help='stages to run, in this order (default all): ' + ' '.join(STAGES))
    p.add_argument('--bands', default=None, help='raster only: a list a,b,c of one-degree bands (lat0 + 90) to '
                   'rasterise as a trial; nothing is written')
    p.add_argument('--procs', type=int, default=None, help='raster only: worker processes (default CPUs - 1)')
    a = p.parse_args()
    src, work = a.sources, common.work_dir(a)
    os.makedirs(work + '/geo', exist_ok=True)
    os.makedirs(work + '/out', exist_ok=True)
    run = a.stages or STAGES
    for s in run:
        assert s in STAGES, s
    for s in STAGES:
        if s not in run:
            continue
        if s == 'owners':
            stage_owners(src, work)
        elif s == 'nations':
            stage_nations(src, work)
        elif s == 'freight':
            stage_freight(src, work)
        elif s == 'raster':
            stage_raster(src, work, [int(x) for x in a.bands.split(',')] if a.bands else None, a.procs)
        elif s == 'lines':
            stage_lines(src, work)
        elif s == 'meta':
            stage_meta(src, work)


if __name__ == '__main__':
    main()
