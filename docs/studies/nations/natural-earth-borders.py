"""The map board's borders: Natural Earth's admin-0 countries at 1:10m (public domain; ne_10m_admin_0_countries.geojson from github.com/nvkelso/natural-earth-vector), each edge two countries' polygons share being a land border and an edge one alone uses a coast; chained into lines, projected as RenderWorld.java's view (EarthProj's spherical Lambert azimuthal equal-area on Halifax, its RADIUS_M) and written as pixel polylines, one a line: x,y x,y ...
Run: python -I natural-earth-borders.py <ne_10m_admin_0_countries.geojson> world-borders.txt"""
import sys, json, math
R = 6371007.0; LAT0, LON0 = math.radians(44.65), -63.57
X0, Y0, S, W, H = -3700000.0, 3900000.0, 9375.0, 1120, 720
def fwd(lon, lat):
    p = math.radians(lat); dl = math.radians(((lon - LON0 + 180) % 360) - 180)
    up = math.sin(LAT0) * math.sin(p) + math.cos(LAT0) * math.cos(p) * math.cos(dl)
    if 1 + up <= 1e-9: return None
    k = R * math.sqrt(2 / (1 + up))
    x = k * math.cos(p) * math.sin(dl); y = k * (math.cos(LAT0) * math.sin(p) - math.sin(LAT0) * math.cos(p) * math.cos(dl))
    return ((x - X0) / S, (Y0 - y) / S)
g = json.load(open(sys.argv[1], encoding='utf-8'))
count = {}
owner = {}
def key(a): return (round(a[0], 6), round(a[1], 6))
for f in g['features']:
    iso = f['properties'].get('ADM0_A3') or f['properties'].get('ISO_A3_EH')
    geom = f['geometry']
    polys = geom['coordinates'] if geom['type'] == 'MultiPolygon' else [geom['coordinates']]
    for poly in polys:
        for ring in poly:
            for a, b in zip(ring, ring[1:]):
                ka, kb = key(a), key(b)
                if ka == kb: continue
                e = (ka, kb) if ka < kb else (kb, ka)
                count[e] = count.get(e, 0) + 1
                owner.setdefault(e, set()).add(iso)
shared = [e for e, c in count.items() if c >= 2 and len(owner[e]) >= 2]
# chain the shared edges into polylines
adj = {}
for a, b in shared:
    adj.setdefault(a, []).append(b); adj.setdefault(b, []).append(a)
used = set(); lines = []
def walk(start, nxt):
    line = [start, nxt]; used.add((start, nxt) if start < nxt else (nxt, start))
    cur, prev = nxt, start
    while len(adj[cur]) == 2:
        n2 = adj[cur][0] if adj[cur][1] == prev else adj[cur][1]
        e = (cur, n2) if cur < n2 else (n2, cur)
        if e in used: break
        used.add(e); line.append(n2); prev, cur = cur, n2
    return line
for v, ns in adj.items():
    if len(ns) != 2:
        for n in ns:
            e = (v, n) if v < n else (n, v)
            if e not in used: lines.append(walk(v, n))
for v, ns in adj.items():
    for n in ns:
        e = (v, n) if v < n else (n, v)
        if e not in used: lines.append(walk(v, n))
out = []; kept = 0
for line in lines:
    pts = [fwd(lon, lat) for lon, lat in line]
    seg = []
    for p in pts:
        inside = p is not None and -40 < p[0] < W + 40 and -40 < p[1] < H + 40
        if inside:
            if not seg or math.hypot(p[0] - seg[-1][0], p[1] - seg[-1][1]) >= 1.2: seg.append(p)
        elif seg:
            if len(seg) > 1: out.append(seg)
            seg = []
    if len(seg) > 1: out.append(seg)
with open(sys.argv[2], 'w', encoding='utf-8') as fo:
    for seg in out:
        fo.write(' '.join(f'{x:.1f},{y:.1f}' for x, y in seg) + '\n'); kept += len(seg)
print(len(shared), 'shared edges,', len(lines), 'lines,', len(out), 'in view,', kept, 'points')
