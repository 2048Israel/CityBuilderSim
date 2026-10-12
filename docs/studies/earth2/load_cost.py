# The Earth 2 study's load cost: for a founding at a few places, the 1-degree tiles within a radius of the spot and
# the bytes each layer costs there - as stored (what is read from the file) and inflated (what the tile cache holds,
# EarthData.CACHE_BYTES) - earth-1's layers against earth-2's new ones.
#   python3 -I load_cost.py [path to earth-2.bin] [radius km]
import sys, os, math, json
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
import numpy as np
import terrain as T
from earthread import Earth

PLACES = [('Halifax', 44.65, -63.58), ('Medellin', 6.25, -75.57), ('Rotterdam', 51.92, 4.48), ('Kathmandu', 27.70, 85.32),
          ('Hong Kong', 22.30, 114.17), ('Denver', 39.74, -104.99)]
NEW = ['slope', 'slope.vsteep', 'shallow', 'depth', 'landmass']
OLD = ['sea', 'lake', 'landcover', 'lakes.small', 'rivers']


def tiles_near(lat, lon, km):
    dl = km / 111.2; dn = dl / max(0.05, math.cos(math.radians(lat)))
    return [((a + 90) * 360 + (b + 180) % 360) for a in range(math.floor(lat - dl), math.floor(lat + dl) + 1)
            for b in range(math.floor(lon - dn), math.floor(lon + dn) + 1)]


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else os.path.join(T.OUT2, 'earth-2.bin')
    km = float(sys.argv[2]) if len(sys.argv) > 2 else 50
    E = Earth(path)
    res = {}
    for name, la, lo in PLACES:
        ts = tiles_near(la, lo, km)
        st = {}
        for L in OLD + NEW:
            if L not in E.layers: continue
            e = E.layers[L]['entries'][ts]
            stored = int(np.maximum(e, 0).sum())
            cpd = E.layers[L]['cpd'] or 0
            infl = len(ts) * (cpd * cpd * (4 if L == 'landmass' else 1)) if L in ('sea', 'lake', 'landcover') + tuple(NEW) else 0
            st[L] = dict(stored=stored, inflated=infl)
        o = sum(st[L]['stored'] for L in OLD if L in st); n = sum(st[L]['stored'] for L in NEW if L in st)
        oi = sum(st[L]['inflated'] for L in OLD if L in st); ni = sum(st[L]['inflated'] for L in NEW if L in st)
        res[name] = dict(tiles=len(ts), layers=st, earth1_stored=o, new_stored=n, earth1_inflated=oi, new_inflated=ni)
        print('%-10s %2d tiles within %g km: read %7.0f KB of earth-1 layers + %7.0f KB new; inflated %6.0f KB + %6.0f KB'
              % (name, len(ts), km, o / 1e3, n / 1e3, oi / 1e3, ni / 1e3))
    json.dump(res, open(os.path.join(T.WORK2, 'load_cost.json'), 'w'), indent=1)


if __name__ == '__main__':
    main()
