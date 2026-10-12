# The Earth 2 study's depth checks at named places, from GEBCO 2026 and earth-1:
#   - GEBCO's depth along the Macdonald and MacKay bridges (Halifax) and the Confederation Bridge (PEI);
#   - E4's rule (EarthTerrain.depthAt(): 140 m x the distance to the coast, at least 30 m, / 54,167 m) against GEBCO
#     at points off Rotterdam, Halifax (the Scotian Shelf), Galveston (the Texas shelf) and Valparaiso (a narrow shelf).
# The coast distance is E4's: the great circle to the nearest 15" cell whose earth-1 sea share is under 8 of 15.
#   python3 -I depth_sites.py
import sys, os, math, json
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
import numpy as np
import terrain as T
from earthread import Earth

E = Earth(T.EARTH1); G = T.gebco()
R = T.R_M


def gebco_at(lat, lon):
    r = int((lat + 90) * 240); c = int((lon + 180) * 240) % 86400
    return int(G[r, c])


def sea_at(lat, lon):
    t = (math.floor(lat) + 90) * 360 + math.floor(lon) + 180
    g = E.grid('sea', t)
    return int(g[min(239, int((math.floor(lat) + 1 - lat) * 240)), min(239, int((lon - math.floor(lon)) * 240))])


def dest(lat, lon, bearing, d):
    """The point d metres from (lat, lon) on the given bearing (degrees), on the sphere."""
    p1, l1, b = math.radians(lat), math.radians(lon), math.radians(bearing); a = d / R
    p2 = math.asin(math.sin(p1) * math.cos(a) + math.cos(p1) * math.sin(a) * math.cos(b))
    l2 = l1 + math.atan2(math.sin(b) * math.sin(a) * math.cos(p1), math.cos(a) - math.sin(p1) * math.sin(p2))
    return math.degrees(p2), math.degrees(l2)


def coast_distance(lat, lon, reach_deg=2.5):
    """E4's coast: the nearest 15" cell centre whose sea share is under 8, by brute force over a window."""
    la0, la1 = math.floor(lat - reach_deg), math.floor(lat + reach_deg)
    cl = math.cos(math.radians(lat)); rl = reach_deg / max(cl, 0.2)
    lo0, lo1 = math.floor(lon - rl), math.floor(lon + rl)
    best = float('inf')
    for a in range(la0, la1 + 1):
        for b in range(lo0, lo1 + 1):
            t = (a + 90) * 360 + (b + 180) % 360
            s = E.grid('sea', t)
            rr, cc = np.nonzero(s < 8)
            if len(rr) == 0: continue
            pl = a + 1 - (rr + 0.5) / 240; pn = b + (cc + 0.5) / 240
            p1, p2 = math.radians(lat), np.radians(pl)
            dl = np.radians(pn - lon)
            h = np.sin((p2 - p1) / 2) ** 2 + math.cos(p1) * np.cos(p2) * np.sin(dl / 2) ** 2
            best = min(best, float((2 * R * np.arcsin(np.sqrt(h))).min()))
    return best


def e4(d):
    return 140.0 * max(30.0, d) / 54167.0


def profile(name, a, b, n=200):
    la = np.linspace(a[0], b[0], n); lo = np.linspace(a[1], b[1], n)
    g = np.array([gebco_at(x, y) for x, y in zip(la, lo)]); s = np.array([sea_at(x, y) for x, y in zip(la, lo)])
    L = math.hypot((b[0] - a[0]) * 111195, (b[1] - a[1]) * 111195 * math.cos(math.radians(a[0])))
    wet = s >= 8
    return dict(bridge=name, length_m=round(L), deepest_m=int(-g[wet].min()) if wet.any() else None,
                water_m=round(L * wet.mean()), cells_wet=int(wet.sum()), samples=n,
                depths_on_water=sorted(set(int(-x) for x in g[wet])))


def main():
    out = {}
    # the bridges: the infobox coordinate (Wikipedia) as the middle; the lines' ends est. from the length and a bearing
    # read off the map (Macdonald 55 degrees, MacKay 65 degrees); Confederation's ends Borden-Carleton and Cape Jourimain
    br = []
    for name, (la, lo), bear, L in [('Macdonald', (44.6637, -63.5846), 55, 1300), ('MacKay', (44.6778, -63.6119), 65, 1200)]:
        a = dest(la, lo, bear + 180, L / 2); b = dest(la, lo, bear, L / 2)
        br.append(profile(name, a, b))
    br.append(profile('Confederation', (46.2448, -63.6961), (46.1617, -63.8136)))
    out['bridges'] = br
    pts = []
    # E4 against GEBCO: (place, coast point, bearing out to sea, distances km)
    for place, (la, lo), bear, ds in [
            ('Rotterdam, the North Sea west of Hoek van Holland', (51.98, 4.12), 270, (10, 20, 40, 80)),
            ('Halifax, the Scotian Shelf south-east of the harbour mouth', (44.48, -63.52), 150, (10, 20, 50, 100, 150, 200)),
            ('Galveston, the Texas shelf', (29.30, -94.75), 135, (10, 20, 40, 80, 120)),
            ('Valparaiso, a narrow shelf', (-33.03, -71.64), 290, (5, 10, 20, 40, 80)),
            ('Persian Gulf off Ras Tanura', (26.65, 50.17), 45, (10, 20, 40, 80))]:
        for d in ds:
            p = dest(la, lo, bear, d * 1000)
            g = gebco_at(*p); cd = coast_distance(*p)
            pts.append(dict(place=place, km_out=d, lat=round(p[0], 4), lon=round(p[1], 4), gebco_depth_m=-g,
                            coast_km=round(cd / 1000, 1), e4_depth_m=round(e4(cd), 1), earth1_sea=sea_at(*p)))
    out['e4_vs_gebco'] = pts
    json.dump(out, open(os.path.join(T.WORK2, 'depth_sites.json'), 'w'), indent=1)
    for b in br:
        print(b)
    print('%-58s %6s %8s %8s %8s %4s' % ('place', 'km out', 'coast km', 'GEBCO m', 'E4 m', 'sea'))
    for p in pts:
        print('%-58s %6d %8.1f %8d %8.1f %4d' % (p['place'], p['km_out'], p['coast_km'], p['gebco_depth_m'], p['e4_depth_m'], p['earth1_sea']))


if __name__ == '__main__':
    main()
