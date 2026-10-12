# The Earth 2 study's landmass cases: for two points, the landmass each stands on (earth-2's rule, landmass.py) and the
# shortest water crossing between their land - through crossable water only (fresh, or sea no deeper than 30 m), and
# through any water - by Dijkstra over the 15" cells of a window round them (8 neighbours, metres on the ground; the
# half cell from each shore to the first water cell's centre counted). The land on each side is the 4-connected piece
# of land cells holding the point, inside the window; a point on water takes the nearest land cell.
# Also: a list of islands and straits, each point's landmass under the old rule (420 m rivers, sea always cuts) and the
# new one.   python3 -I landmass_gaps.py
import sys, os, math, zlib, json
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
import numpy as np
from scipy import ndimage
from scipy.sparse import coo_matrix
from scipy.sparse.csgraph import dijkstra
import terrain as T, landmass as LM
from earthread import Earth

E = Earth(T.EARTH1); G = T.gebco()


class Layer:
    """A landmass layer from landmass.py's blobs.npz and table.tsv."""
    def __init__(self, folder):
        z = np.load(os.path.join(folder, 'blobs.npz'))
        self.ent = z['entries']; self.data = z['data'].tobytes()
        self.off = dict(zip(z['tiles'].tolist(), np.concatenate([[0], np.cumsum(z['lens'])[:-1]]).tolist()))
        self.len = dict(zip(z['tiles'].tolist(), z['lens'].tolist()))
        self.tab = open(os.path.join(folder, 'table.tsv'), encoding='utf-8').read().splitlines()

    def tile(self, t):
        e = int(self.ent[t])
        if e <= 0:
            return np.full((240, 240), -e, np.uint32)
        b = zlib.decompress(self.data[self.off[t]:self.off[t] + self.len[t]]); p = [0]
        def uv():
            n = 0; s = 0
            while True:
                x = b[p[0]]; p[0] += 1; n |= (x & 0x7f) << s; s += 7
                if not x & 0x80: return n
        n = uv(); vals = []; prev = 0
        for _ in range(n):
            prev += uv(); vals.append(prev)
        idx = np.frombuffer(b[p[0]:], np.uint8 if n <= 256 else '>u2').reshape(240, 240)
        return np.array(vals, np.uint32)[idx]

    def at(self, lat, lon):
        t = (math.floor(lat) + 90) * 360 + math.floor(lon) + 180
        return int(self.tile(t)[min(239, int((math.floor(lat) + 1 - lat) * 240)), min(239, int((lon - math.floor(lon)) * 240))])

    def area(self, k):
        return float(self.tab[k - 1].split('\t')[0]) if k > 0 else 0.0


NEW = Layer(os.path.join(T.WORK2, 'landmass'))
OLD = Layer(os.path.join(T.WORK2, 'landmass-420')) if os.path.exists(os.path.join(T.WORK2, 'landmass-420')) else None


def window(s, n, w, e):
    la0, la1 = math.floor(s), math.floor(n - 1e-9); lo0, lo1 = math.floor(w), math.floor(e - 1e-9)
    ids = []; cr = []; sea = []
    for la in range(la1, la0 - 1, -1):
        ri, rc, rs = [], [], []
        for lo in range(lo0, lo1 + 1):
            t = (la + 90) * 360 + lo + 180
            g = NEW.tile(t); ri.append(g)
            rc.append(LM.crossable(E, G, t, g > 0)); rs.append(E.grid('sea', t).astype(np.int16) + E.grid('lake', t))
        ids.append(np.hstack(ri)); cr.append(np.hstack(rc)); sea.append(np.hstack(rs))
    return np.vstack(ids), np.vstack(cr), np.vstack(sea), la1 + 1, lo0


def gap(lat_a, lon_a, lat_b, lon_b, pad=0.25):
    s, n = min(lat_a, lat_b) - pad, max(lat_a, lat_b) + pad
    cl = math.cos(math.radians((lat_a + lat_b) / 2)); pw = pad / cl
    w, e = min(lon_a, lon_b) - pw, max(lon_a, lon_b) + pw
    ids, cr, wat, top, left = window(s, n, w, e)
    land = ids > 0
    loc, _ = ndimage.label(land)
    def piece(lat, lon):
        r = int((top - lat) * 240); c = int((lon - left) * 240)
        if loc[r, c] == 0:
            rr, cc = np.nonzero(land); j = np.argmin((rr - r) ** 2 + ((cc - c) * cl) ** 2); r, c = rr[j], cc[j]
        return loc[r, c], int(ids[r, c])
    pa, ia = piece(lat_a, lon_a); pb, ib = piece(lat_b, lon_b)
    H, W = ids.shape; dy = LM.CELL_M; dx = LM.CELL_M * cl
    res = dict(id_a=ia, id_b=ib, same=ia == ib, area_a_km2=NEW.area(ia), area_b_km2=NEW.area(ib))
    if pa == pb:
        res.update(crossable_m=0, any_water_m=0, by_land=True)
        return res
    def pairs(H, W, ddr, ddc):
        """Index arrays (a, b) of every cell a and its neighbour b = a + (ddr, ddc) inside the window."""
        r0, r1 = max(0, -ddr), H - max(0, ddr); c0, c1 = max(0, -ddc), W - max(0, ddc)
        rr, cc = np.mgrid[r0:r1, c0:c1]
        return rr.ravel(), cc.ravel(), (rr + ddr).ravel(), (cc + ddc).ravel()

    for name, ok in (('crossable_m', cr & ~land), ('any_water_m', ~land)):
        N = H * W; src = N; dst = N + 1
        idx = np.arange(N).reshape(H, W)
        rows, cols, wts = [], [], []
        for ddr, ddc in ((0, 1), (1, 0), (1, 1), (1, -1)):
            L = math.hypot(ddr * dy, ddc * dx)
            ra, ca, rb, cb = pairs(H, W, ddr, ddc)
            m = ok[ra, ca] & ok[rb, cb]
            rows.append(idx[ra[m], ca[m]]); cols.append(idx[rb[m], cb[m]]); wts.append(np.full(m.sum(), L))
        for p, node in ((pa, src), (pb, dst)):             # the shores: water next to each piece, half a step away
            for ddr, ddc, L in ((0, 1, dx), (0, -1, dx), (1, 0, dy), (-1, 0, dy)):
                ra, ca, rb, cb = pairs(H, W, ddr, ddc)
                m = (loc[ra, ca] == p) & ok[rb, cb]
                rows.append(np.full(m.sum(), node)); cols.append(idx[rb[m], cb[m]]); wts.append(np.full(m.sum(), L / 2))
        g = coo_matrix((np.concatenate(wts), (np.concatenate(rows), np.concatenate(cols))), shape=(N + 2, N + 2)).tocsr()
        d = dijkstra(g, directed=False, indices=src)
        res[name] = None if not np.isfinite(d[dst]) else round(float(d[dst]))
    return res


CASES = [
    ('Halifax peninsula / Dartmouth', 44.648, -63.580, 44.672, -63.565),
    ('Halifax peninsula / Bedford', 44.648, -63.580, 44.73, -63.66),
    ('McNabs Island / Dartmouth (Eastern Passage)', 44.612, -63.525, 44.600, -63.490),
    ('McNabs Island / Halifax peninsula (Point Pleasant)', 44.612, -63.525, 44.625, -63.570),
    ('Prince Edward Island / New Brunswick', 46.24, -63.70, 46.17, -63.80),
    ('Manhattan / the Bronx', 40.78, -73.97, 40.85, -73.87),
    ('Long Island (Brooklyn) / Manhattan', 40.65, -73.95, 40.75, -73.99),
    ('Goeree-Overflakkee / Rotterdam mainland (Voorne-Putten side)', 51.78, 4.17, 51.85, 4.25),
    ('Goeree-Overflakkee / North Brabant (Hellegatsplein side)', 51.70, 4.40, 51.68, 4.50),
    ('Great Britain / France (Dover / Calais)', 51.13, 1.30, 50.95, 1.85),
    ('Sicily / Italy (Messina / Villa San Giovanni)', 38.19, 15.55, 38.22, 15.64),
    ('Zealand / Funen (Korsor / Nyborg)', 55.33, 11.14, 55.31, 10.79),
]
ISLANDS = [('Sri Lanka', 7.0, 80.7), ('Singapore', 1.35, 103.82), ('Hong Kong Island', 22.26, 114.18), ('Kyushu', 32.8, 130.7),
           ('Shikoku', 33.7, 133.5), ('Hokkaido', 43.2, 142.8), ('Sakhalin', 50.0, 143.0), ('Zealand', 55.5, 12.0), ('Funen', 55.3, 10.4),
           ('Sicily', 37.5, 14.2), ('Sardinia', 40.0, 9.0), ('Corsica', 42.2, 9.1), ('Anglesey', 53.28, -4.35), ('Isle of Wight', 50.67, -1.3),
           ('Cape Breton Island', 46.0, -60.8), ('Newfoundland', 48.5, -56.0), ('Vancouver Island', 49.5, -125.5), ('Montreal Island', 45.5, -73.65),
           ('Manhattan', 40.78, -73.97), ('Staten Island', 40.58, -74.15), ('Penang', 5.4, 100.25), ('Bali', -8.4, 115.2), ('Java', -7.5, 110.0),
           ('Sumatra', 0.0, 101.5), ('Tierra del Fuego', -54.0, -68.5), ('Chiloe', -42.6, -73.9), ('Bahrain', 26.05, 50.55), ('Rugen', 54.42, 13.4),
           ('Great Britain', 52.5, -1.5), ('Ireland', 53.3, -8.0), ('New Guinea', -5.0, 141.0), ('Honshu', 36.0, 138.5),
           ('Prince Edward Island', 46.3, -63.3), ('Goeree-Overflakkee', 51.77, 4.15), ('Marajo', -1.0, -49.5), ('Hainan', 19.2, 109.7)]


def main():
    out = dict(cases=[], islands=[])
    for name, a, b, c, d in CASES:
        r = gap(a, b, c, d); r['case'] = name; out['cases'].append(r)
        print('%-62s ids %s/%s %-9s crossable %s m, any water %s m' % (name, r['id_a'], r['id_b'], 'JOINED' if r['same'] else 'separate',
              r['crossable_m'], r['any_water_m']))
    print()
    for name, la, lo in ISLANDS:
        k = NEW.at(la, lo); o = OLD.at(la, lo) if OLD else None
        r = dict(name=name, new_id=k, new_km2=NEW.area(k), old_id=o, old_km2=OLD.area(o) if OLD else None); out['islands'].append(r)
        print('%-22s new %7d %14.1f km2   old %7s %14s km2' % (name, k, NEW.area(k), o, '%.1f' % OLD.area(o) if OLD else ''))
    json.dump(out, open(os.path.join(T.WORK2, 'landmass_gaps.json'), 'w'), indent=1)


if __name__ == '__main__':
    main()
