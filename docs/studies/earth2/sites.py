# The Earth 2 study's sample sites: for each 1-degree tile under a site's box, the per-15"-cell histogram of slope
# (percent grade, terrain.BINS) from four 30-90 m heights, and GEBCO 2026's own 15" slope and height:
#   nas30  NASADEM 1" (NASA, public domain), Horn's method over 1"
#   nas90  the same heights averaged over 3 x 3 (a 3" grid), Horn's method over 3"
#   cop30  Copernicus GLO-30 (for comparison only: its licence fails the credits-only rule; see the study)
#   cop90  Copernicus GLO-90 (the same)
#   gslope GEBCO 2026's slope at 15" (Horn over 15"), gelev its height, nmean NASADEM's mean height in the cell
# Writes <E2>/work/sites/<TILE>.npz.   python3 -I sites.py [site ...]
import sys, os, math, time
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'tools', 'earth'))
import numpy as np
import terrain as T

# name: (label, centre lat, centre lon, half-height of the box in degrees of latitude); the box is square on the ground
SITES = {
    'medellin':   ('Medellin (Aburra valley)', 6.245, -75.575, 0.135),
    'bogota':     ('Bogota', 4.650, -74.100, 0.135),
    'kathmandu':  ('Kathmandu valley', 27.700, 85.330, 0.135),
    'everest':    ('High Himalaya round Everest', 27.988, 86.925, 0.135),
    'lapaz':      ('La Paz and El Alto', -16.500, -68.150, 0.135),
    'innsbruck':  ('Innsbruck (Inn valley)', 47.265, 11.390, 0.135),
    'engadin':    ('Upper Engadin (St. Moritz, 1,800 m)', 46.490, 9.840, 0.135),
    'halifax':    ('Halifax NS', 44.660, -63.600, 0.135),
    'rotterdam':  ('Rotterdam', 51.920, 4.480, 0.135),
    'rio':        ('Rio de Janeiro', -22.920, -43.250, 0.135),
    'hongkong':   ('Hong Kong', 22.320, 114.170, 0.135),
    'denver':     ('Denver and the Front Range', 39.700, -105.100, 0.135),
    'wellington': ('Wellington', -41.250, 174.800, 0.135),
    'amazon':     ('Amazon forest near Manaus (flat, under canopy)', -3.450, -60.450, 0.135),
}


def box(site):
    _, la, lo, h = SITES[site]
    w = h / math.cos(math.radians(la))
    return la - h, la + h, lo - w, lo + w


def tiles(site):
    s, n, w, e = box(site)
    return [(a, b) for a in range(math.floor(s), math.floor(n - 1e-9) + 1) for b in range(math.floor(w), math.floor(e - 1e-9) + 1)]


def cop_tile(lat0, lon0, res):
    ns = 'N' if lat0 >= 0 else 'S'; ew = 'E' if lon0 >= 0 else 'W'
    name = 'Copernicus_DSM_COG_%d_%s%02d_00_%s%03d_00_DEM' % (res, ns, abs(lat0), ew, abs(lon0))
    url = 'https://copernicus-dem-%dm.s3.amazonaws.com/%s/%s.tif' % (30 if res == 10 else 90, name, name)
    p = os.path.join(T.DL2, 'cop%d' % (30 if res == 10 else 90), name + '.tif')
    if os.path.exists(p) or T.fetch(url, p):
        return T.read_tif(p)
    return None


def tile_hist(z, lat0, rows_per_deg, keep_last):
    """Slope histogram per 15" cell of a tile's heights z (rows from lat0 + 1; keep_last: z has the shared last row and
    column, as NASADEM's 3601 do)."""
    nr = rows_per_deg
    W = z.shape[1] - (1 if keep_last else 0)
    pct = T.slope_pct(z, lat0 + 1, 1.0 / nr, 1.0 / W)
    pct = pct[:nr, :W]
    k = 1
    while (W * k) % 240: k += 1
    if k > 1:
        pct = np.repeat(pct, k, axis=1)
    return T.cell_hist(pct, nr // 240, W * k // 240)


def do_tile(lat0, lon0):
    name = T.tile_name(lat0, lon0, 'srtm')
    out = os.path.join(T.WORK2, 'sites', name + '.npz')
    if os.path.exists(out):
        return
    t0 = time.time()
    r = {}
    z, src = T.nasadem_tile(lat0, lon0, keep=True)
    if z is not None:
        z = T.fill_voids(z)
        r['nas30'] = tile_hist(z, lat0, 3600, True)
        z3 = T.block_mean(z[:3600, :3600], 3)
        # the 3 x 3 means' centres are a second inside the tile's north and west edges: the slope's latitude is
        # taken at the tile's top edge, which moves cos(lat) by under 1e-5
        r['nas90'] = tile_hist(z3, lat0, 1200, False)
        r['nmean'] = T.block_mean(z[:3600, :3600], 15)
        r['nas_src'] = np.array(src)
    c = cop_tile(lat0, lon0, 10)
    if c is not None:
        r['cop30'] = tile_hist(c, lat0, 3600, False)
    c = cop_tile(lat0, lon0, 30)
    if c is not None:
        r['cop90'] = tile_hist(c, lat0, 1200, False)
    G = T.gebco()
    g = T.gebco_tile(G, lat0, lon0, pad=1).astype(np.float32)
    gs = T.slope_pct(g, lat0 + 1 + 1 / 240.0 - 0.5 / 240, 1 / 240.0)[1:-1, 1:-1]
    r['gslope'] = gs.astype(np.float32)
    r['gelev'] = g[1:-1, 1:-1].astype(np.int16)
    os.makedirs(os.path.dirname(out), exist_ok=True)
    np.savez_compressed(out + '.tmp.npz', **r)
    os.replace(out + '.tmp.npz', out)
    T.log('sites', '%s done (%s) in %.0f s' % (name, ', '.join(k for k in r), time.time() - t0))


def main():
    sel = sys.argv[1:] or list(SITES)
    todo = sorted({t for s in sel for t in tiles(s)})
    T.log('sites', '%d tiles for %d sites' % (len(todo), len(sel)))
    for la, lo in todo:
        do_tile(la, lo)


if __name__ == '__main__':
    main()
