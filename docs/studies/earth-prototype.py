# earth-prototype.py - the Earth & Map study's prototypes (2026-10-08), five scripts in one file.
# Run each part as its own script; they read GLOBE's 30" mask (pip: global-land-mask) and Natural Earth GeoJSON.
# Results quoted in spec-earth.md: mask_budget (the coast's size), projection (distortion, coverage),
# terrain (the ground rule), city (the mockup city's offers checked against the grid's rules), site (the founding tests from a click).


# ============================== mask_budget.py ==============================
# Measure what a global land/sea mask costs to ship, from GLOBE's 30" mask (global_land_mask).
import numpy as np, zlib, lzma, os, sys, json
import global_land_mask as glm
from global_land_mask import globe
m = globe._mask            # True = land? check
print('shape', m.shape, m.dtype, 'land share', m.mean())
lat = globe._lat; lon = globe._lon
print('lat', lat[0], lat[-1], 'lon', lon[0], lon[-1])

def runs_per_row(mask):
    d = np.diff(mask.astype(np.int8), axis=1) != 0
    return d.sum(axis=1)

def rle_bytes(mask):
    # each row: runs as varints (LEB128) of run lengths, row starts with land flag
    out = bytearray()
    for row in mask:
        idx = np.flatnonzero(np.diff(row.astype(np.int8))) + 1
        bounds = np.concatenate(([0], idx, [row.size]))
        lens = np.diff(bounds)
        out.append(1 if row[0] else 0)
        for L in lens:
            L = int(L)
            while True:
                b = L & 0x7f; L >>= 7
                if L: out.append(b | 0x80)
                else: out.append(b); break
    return bytes(out)

res = {}
for name, f in [('30s', 1), ('60s', 2), ('120s', 4)]:
    mm = m if f == 1 else m.reshape(m.shape[0]//f, f, m.shape[1]//f, f).mean(axis=(1,3)) >= 0.5
    r = runs_per_row(mm)
    raw = mm.size / 8
    rle = rle_bytes(mm)
    z = zlib.compress(rle, 9); x = lzma.compress(rle, preset=9)
    bits = np.packbits(mm, axis=1).tobytes()
    zb = lzma.compress(bits, preset=9)
    res[name] = dict(cells=int(mm.size), transitions=int(r.sum()), raw_bits_MB=raw/1e6, rle_MB=len(rle)/1e6,
                     rle_lzma_MB=len(x)/1e6, rle_zlib_MB=len(z)/1e6, bitpack_lzma_MB=len(zb)/1e6)
    print(name, res[name]); sys.stdout.flush()
json.dump(res, open('/home/claude/earth/out/mask_budget.json','w'), indent=1)

# ============================== projection.py ==============================
# The Earth on the game's flat square: Lambert azimuthal equal-area centred on the city.
import math, json, numpy as np
R = 6371.0088            # mean radius, km (IUGG)
EARTH = 4*math.pi*R*R
CELL_KM = 61.44; CELLS = 368; HALF = CELL_KM*CELLS/2
out = {'earth_km2': EARTH, 'disc_radius_km': 2*R, 'square_half_km': HALF, 'square_km2': (2*HALF)**2,
       'disc_km2': math.pi*(2*R)**2}
# scale along and across the radius at great-circle distance s (LAEA): h = cos(c/2), k = 1/cos(c/2); area h*k = 1
dist = []
for s in [1, 10, 100, 250, 500, 1000, 2000, 5000, 10000]:
    c = s/R
    h = math.cos(c/2); k = 1/h
    rho = 2*R*math.sin(c/2)
    dist.append(dict(km=s, radial_scale=h, across_scale=k, shape_error_pct=(k/h-1)*100, map_radius_km=rho, length_error_pct=(rho/s-1)*100))
out['distortion'] = dist
# farthest real distance reached on an axis: 2R sin(c/2) = HALF
c_axis = 2*math.asin(HALF/(2*R)); out['axis_reach_km'] = c_axis*R
# share of Earth inside the square, by Monte Carlo on the sphere (area-uniform), and void in the square
rng = np.random.default_rng(1)
n = 4_000_000
z = rng.uniform(-1, 1, n); phi = rng.uniform(0, 2*math.pi, n)
# c = angle from the centre (north pole of this frame): cos c = z
c = np.arccos(z); rho = 2*R*np.sin(c/2)
x = rho*np.cos(phi); y = rho*np.sin(phi)
inside = (np.abs(x) <= HALF) & (np.abs(y) <= HALF)
out['earth_share_inside_square'] = float(inside.mean())
out['earth_km2_outside'] = float((1-inside.mean())*EARTH)
out['square_void_km2'] = (2*HALF)**2 - float(inside.mean())*EARTH
out['nearest_point_outside_km'] = float(c_axis*R)
json.dump(out, open('/home/claude/earth/out/projection.json','w'), indent=1)
for k,v in out.items():
    if k!='distortion': print(k, round(v,4) if isinstance(v,float) else v)
for d in dist: print({k: round(v,5) for k,v in d.items()})

# ============================== terrain.py ==============================
"""Prototype of the Earth world's ground, a plot at a time.

The rule under test: the data decides every plot except within about one data
cell of a shore, where seeded noise frays the line. Data here is GLOBE's 30"
land mask (global_land_mask) and Natural Earth 10m rivers - what this sandbox
can reach; the game would read its own pack (finer).
"""
import math, json, numpy as np
from pyproj import Transformer
from global_land_mask import globe
from matplotlib.path import Path

PLOT = 30.0
GRASS, FOREST, FRESH, SALT, SAND = 0, 1, 2, 3, 4
GROUND = np.array([0xa8c47e, 0x527c45, 0x78b4dc, 0x2f5d88, 0xe6daaa])
VOID = 0x16222c
WATER = globe._mask            # True = ocean, 30" rows from 90N
NROW, NCOL = WATER.shape
STEP = 1/120.0                 # degrees a cell

def mix(z):
    z = (z + np.uint64(0x9E3779B97F4A7C15)) & np.uint64(0xFFFFFFFFFFFFFFFF)
    z = ((z ^ (z >> np.uint64(30))) * np.uint64(0xBF58476D1CE4E5B9)) & np.uint64(0xFFFFFFFFFFFFFFFF)
    z = ((z ^ (z >> np.uint64(27))) * np.uint64(0x94D049BB133111EB)) & np.uint64(0xFFFFFFFFFFFFFFFF)
    return z ^ (z >> np.uint64(31))

def lattice(seed, ix, iy):
    with np.errstate(over='ignore'):
        h = mix(np.uint64(seed) ^ mix(ix.astype(np.int64).view(np.uint64) * np.uint64(0x9E3779B97F4A7C15)
                                      ^ iy.astype(np.int64).view(np.uint64)))
    return (h >> np.uint64(11)).astype(np.float64) / float(1 << 53)

def vnoise(seed, x, y):
    ix = np.floor(x); iy = np.floor(y); fx = x - ix; fy = y - iy
    sx = fx*fx*(3-2*fx); sy = fy*fy*(3-2*fy)
    ix = ix.astype(np.int64); iy = iy.astype(np.int64)
    a = lattice(seed, ix, iy); b = lattice(seed, ix+1, iy)
    c = lattice(seed, ix, iy+1); d = lattice(seed, ix+1, iy+1)
    return (a + (b-a)*sx) + ((c + (d-c)*sx) - (a + (b-a)*sx))*sy

def fbm(seed, x, y, hi, lo, fall=0.55):
    tot = np.zeros_like(x); norm = 0; amp = 1.0
    for k in range(hi, lo-1, -1):
        w = float(1 << k)
        tot += amp*vnoise(seed*1000003 + k, x/w, y/w); norm += amp; amp *= fall
    return tot/norm

class Earth:
    def __init__(self, lat0, lon0, seed=4127):
        self.lat0, self.lon0, self.seed = lat0, lon0, seed
        self.inv = Transformer.from_crs(
            f"+proj=laea +lat_0={lat0} +lon_0={lon0} +R=6371008.8 +units=m +no_defs",
            "+proj=longlat +R=6371008.8 +no_defs", always_xy=True)

    def latlon(self, X, Y):      # plots east / south of the spot
        lon, lat = self.inv.transform(X*PLOT, -Y*PLOT)
        return lat, lon

    def landness(self, lat, lon):
        r = (90 - lat)/STEP - 0.5; c = (lon + 180)/STEP - 0.5
        r0 = np.clip(np.floor(r).astype(int), 0, NROW-2); c0 = np.floor(c).astype(int) % NCOL
        c1 = (c0 + 1) % NCOL; fr = np.clip(r - r0, 0, 1); fc = c - np.floor(c)
        L = lambda rr, cc: (~WATER[rr, cc]).astype(np.float64)
        top = L(r0, c0)*(1-fc) + L(r0, c1)*fc; bot = L(r0+1, c0)*(1-fc) + L(r0+1, c1)*fc
        return top*(1-fr) + bot*fr

    def datacell(self, lat, lon):
        r = np.clip(((90 - lat)/STEP).astype(int), 0, NROW-1); c = ((lon + 180)/STEP).astype(int) % NCOL
        return np.where(WATER[r, c], SALT, GRASS)

    def classes(self, X, Y, rivers=(), forest_share=0.55, fray=0.9):
        lat, lon = self.latlon(X, Y)
        Lnd = self.landness(lat, lon)
        n = fbm(self.seed ^ 0x5EA, X, Y, 4, 1)
        v = Lnd + fray*(n - 0.5)
        cls = np.full(X.shape, GRASS, dtype=np.int8)
        cls[v < 0.5] = SALT
        f = fbm(self.seed ^ 0xF0E, X, Y, 8, 5)
        theta = np.quantile(f[cls == GRASS], 1 - forest_share) if (cls == GRASS).any() else 1
        cls[(cls == GRASS) & (f >= theta)] = FOREST
        cls[(cls != SALT) & (v < 0.5 + 0.035)] = SAND
        for (xs, ys, half) in rivers:          # polyline in plots, half-width in plots
            pts = np.stack([X.ravel(), Y.ravel()], 1)
            for i in range(len(xs)-1):
                ax, ay, bx, by = xs[i], ys[i], xs[i+1], ys[i+1]
                dx, dy = bx-ax, by-ay; L2 = dx*dx+dy*dy or 1
                t = np.clip(((pts[:,0]-ax)*dx + (pts[:,1]-ay)*dy)/L2, 0, 1)
                d = np.hypot(pts[:,0]-(ax+t*dx), pts[:,1]-(ay+t*dy)).reshape(X.shape)
                hit = (d <= half) & (cls != SALT)
                cls[hit] = FRESH
        return cls, Lnd

    def to_plots(self, lat, lon):
        fwd = Transformer.from_crs("+proj=longlat +R=6371008.8 +no_defs",
            f"+proj=laea +lat_0={self.lat0} +lon_0={self.lon0} +R=6371008.8 +units=m +no_defs", always_xy=True)
        x, y = fwd.transform(lon, lat)
        return np.asarray(x)/PLOT, -np.asarray(y)/PLOT

def rgb(cls, owned=None, dim=0.4):
    c = GROUND[cls]
    r, g, b = (c >> 16) & 255, (c >> 8) & 255, c & 255
    if owned is not None:
        vr, vg, vb = (VOID >> 16) & 255, (VOID >> 8) & 255, VOID & 255
        k = np.where(owned, 0.0, dim)
        r = r*(1-k) + vr*k; g = g*(1-k) + vg*k; b = b*(1-k) + vb*k
    return np.stack([r, g, b], -1).astype(np.uint8)

def river_lines(earth, name_has, box_plots):
    d = json.load(open('/home/claude/earth/data/ne_10m_rivers_lake_centerlines.geojson'))
    out = []
    for f in d['features']:
        n = f['properties'].get('name') or ''
        if name_has not in n: continue
        g = f['geometry']; parts = g['coordinates'] if g['type'] == 'MultiLineString' else [g['coordinates']]
        for p in parts:
            p = np.array(p); x, y = earth.to_plots(p[:,1], p[:,0])
            x0, y0, x1, y1 = box_plots
            if ((x > x0) & (x < x1) & (y > y0) & (y < y1)).any(): out.append((x, y))
    return out

# ============================== city.py ==============================
"""The mockup city on the block grid: who owns which 120 m block, and the offers standing.
Checks the offers keep the grid's rules: no overlap, each touches the city, each 1 x 2 outward,
each block on its own side (larger of the east-west and north-south distance, a tie to North/South)."""
import json
FOUNDING = {(x, y) for x in range(-2, 3) for y in range(-2, 3) if not (abs(x) == 2 and abs(y) == 2)}
BOUGHT = [('North 3', [(0, -3), (0, -4)]), ('East 3', [(3, 1), (4, 1)]), ('South 2', [(1, 3), (1, 4)])]
OFFERS = {
 'North 1': [(-2, -2), (-2, -3)], 'North 2': [(-1, -3), (-1, -4)], 'North 3': [(0, -5), (0, -6)],
 'North 4': [(1, -3), (1, -4)], 'North 5': [(2, -2), (2, -3)],
 'East 1': [(3, -1), (4, -1)], 'East 2': [(3, 0), (4, 0)], 'East 3': [(5, 1), (6, 1)], 'East 4': [(3, 2), (4, 2)],
 'South 1': [(2, 2), (2, 3)], 'South 2': [(1, 5), (1, 6)], 'South 3': [(0, 3), (0, 4)],
 'South 4': [(-1, 3), (-1, 4)], 'South 5': [(-2, 2), (-2, 3)],
 'West 1': [(-3, 1), (-4, 1)], 'West 2': [(-3, 0), (-4, 0)], 'West 3': [(-3, -1), (-4, -1)],
}
def side_of(b):
    x, y = b
    if abs(x) > abs(y): return 'East' if x > 0 else 'West'
    return 'South' if y > 0 else 'North'
def city(after=()):
    c = set(FOUNDING)
    for _, bl in BOUGHT: c |= set(bl)
    for name in after: c |= set(OFFERS[name])
    return c
def check(c, offers):
    seen = set(); bad = []
    for name, bl in offers.items():
        s = set(bl)
        if s & c: bad.append(name + ' overlaps the city')
        if s & seen: bad.append(name + ' overlaps another offer')
        seen |= s
        if not any((x+dx, y+dy) in c for (x, y) in bl for dx, dy in ((1,0),(-1,0),(0,1),(0,-1))): bad.append(name + ' does not touch')
        for b in bl:
            if side_of(b) != name.split()[0]: bad.append(f'{name} block {b} is on {side_of(b)}')
    return bad
if __name__ == '__main__':
    c = city(); print('city blocks', len(c), 'km2', round(len(c)*0.0144, 4), 'offers', len(OFFERS))
    print('rules broken:', check(c, OFFERS) or 'none')
    # after buying North 3: its next offer straight out
    c2 = city(['North 3']); o2 = dict(OFFERS); o2['North 3'] = [(0, -7), (0, -8)]
    print('after North 3:', len(c2), 'blocks;', check(c2, o2) or 'none broken')

# ============================== site.py ==============================
import sys, numpy as np, json
sys.path.insert(0, '/home/claude/earth/scripts')
from terrain import *
lat0, lon0 = float(sys.argv[1]), float(sys.argv[2]); H = int(sys.argv[3])
E = Earth(lat0, lon0)
ys, xs = np.mgrid[-H:H, -H:H].astype(float)
cls, L = E.classes(xs + 0.5, ys + 0.5)
dry = (cls != SALT) & (cls != FRESH); sea = cls == SALT
def at(a, x, y):
    x = int(round(x)) + H; y = int(round(y)) + H
    if 0 <= x < 2*H and 0 <= y < 2*H: return a[y, x]
    return False
def ok(px, py):
    if not at(dry, px, py): return False
    for k in range(8):
        a = k*np.pi/4
        if not at(dry, px + 20*np.cos(a), py + 20*np.sin(a)): return False
    nearsea = False
    for k in range(16):
        a = k*np.pi/8
        for r in range(1, 71):
            if at(sea, px + r*np.cos(a), py + r*np.sin(a)):
                if r <= 45: return False
                nearsea = True; break
    return nearsea
# spiral from the spot, 17-plot step
found = None; tried = 0
for ring in range(0, 60):
    pts = [(0, 0)] if ring == 0 else [(ring*17*np.cos(t), ring*17*np.sin(t)) for t in np.linspace(0, 2*np.pi, 6*ring, endpoint=False)]
    for (x, y) in pts:
        tried += 1
        if ok(x, y): found = (x, y); break
    if found: break
lat, lon = E.latlon(np.array([found[0]]), np.array([found[1]]))
print(json.dumps(dict(site_plots=found, tried=tried, lat=float(lat[0]), lon=float(lon[0]), km_from_click=float(np.hypot(*found)*0.03))))
