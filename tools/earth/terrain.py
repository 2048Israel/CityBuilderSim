# earth-2's ground and sea (the Earth 2 study, docs/studies/spec-earth2-terrain.md): the shared pieces of its pipeline.
#   - where things go (E2, overridable by the CBS_E2 environment variable, so the same scripts run on the PC);
#   - the slope classes and their thresholds (percent grade), and the slope of a geographic grid (Horn's method);
#   - the 1" heights: NASADEM (NASA, public domain / CC0 by NASA's EOSDIS policy) read from Microsoft's Planetary Computer
#     mirror (anonymous, a SAS token from its token API), or USGS SRTMGL1 v3 from ESA STEP's mirror as the fallback;
#   - GEBCO 2026 (public domain, credit): the global 15" grid, memory-mapped from the whole netCDF file (rows from the south).
# Nothing here needs a login. Every download is written to a temporary name and renamed when whole.
import os, sys, time, json, math, zipfile, io, subprocess, urllib.request, ssl
import numpy as np

HERE = os.path.dirname(os.path.abspath(__file__))
E2 = os.environ.get('CBS_E2', '/home/claude/e2data')
DL2 = os.path.join(E2, 'dl')            # downloads (the DEM tiles are deleted after use unless KEEP_TILES)
WORK2 = os.path.join(E2, 'work')        # per-tile results, a file per tile, a .done marker last
OUT2 = os.path.join(E2, 'out')          # earth-2.bin, its sha256, the reports
LOG2 = os.path.join(E2, 'log')
EARTH1 = os.environ.get('CBS_EARTH1', os.path.join(E2, 'earth-1.bin'))
GEBCO_NC = os.environ.get('CBS_GEBCO', os.path.join(DL2, 'gebco', 'GEBCO_2026.nc'))
GEBCO_URL = 'https://dap.ceda.ac.uk/bodc/gebco/global/gebco_2026/ice_surface_elevation/netcdf/GEBCO_2026.nc?download=1'
GEBCO_BYTES = 7466018396
GEBCO_OFFSET = 1058396                  # the elevation variable's first byte (contiguous, int16, 43,200 x 86,400, rows from 90 S)
KEEP_TILES = os.environ.get('CBS_KEEP_TILES', '') == '1'

R_M = 6371007.0                         # EarthProj.RADIUS_M
C15 = 240                               # 15" cells a degree (earth-1's sea and lake grid; GEBCO's grid)

# ------------------------------------------------------------------ the slope classes
# Percent grade (rise over run x 100) of the ground, measured over 90 m (slope_tiles.py: heights averaged to 3", Horn's
# method over 3"). A class is the share of a cell's land at or above each threshold: FOOTHILL_PCT (dearer to prepare),
# STEEP_PCT (refused for buildings) and VERY_STEEP_PCT (the darkest grey) - FAO's slope-class edges (Guidelines for
# soil description, 2006, Table 7); the Earth 2 study gives the evidence for each.
FOOTHILL_PCT = 10.0       # 5.7 degrees
STEEP_PCT = 30.0          # 16.7 degrees
VERY_STEEP_PCT = 60.0     # 31.0 degrees
THRESHOLDS = (FOOTHILL_PCT, STEEP_PCT, VERY_STEEP_PCT)

# Histogram bins (percent grade) the study's per-cell measurements keep, so any thresholds can be tried afterwards.
BINS = np.array([0, 2, 4, 6, 8, 10, 12, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 100, 150, 1e9], np.float32)
NB = len(BINS) - 1


def log(name, *msg):
    os.makedirs(LOG2, exist_ok=True)
    line = time.strftime('%H:%M:%S ') + ' '.join(str(m) for m in msg)
    with open(os.path.join(LOG2, name + '.log'), 'a') as f:
        f.write(line + '\n')
    print(line, flush=True)


def tile_name(lat0, lon0, style='nasadem'):
    ns = 'n' if lat0 >= 0 else 's'; ew = 'e' if lon0 >= 0 else 'w'
    s = '%s%02d%s%03d' % (ns, abs(lat0), ew, abs(lon0))
    return s if style == 'nasadem' else s.upper()


def slope_pct(z, lat_n, dlat, dlon=None):
    """Percent grade of a geographic grid z (rows from the north, the first row's centre at lat_n, spacing dlat and dlon
    degrees), by Horn's 3 x 3 method with the edges replicated. float32, the shape of z."""
    dlon = dlat if dlon is None else dlon
    z = z.astype(np.float32)
    p = np.pad(z, 1, mode='edge')
    a, b, c = p[:-2, :-2], p[:-2, 1:-1], p[:-2, 2:]
    d, f = p[1:-1, :-2], p[1:-1, 2:]
    g, h, i = p[2:, :-2], p[2:, 1:-1], p[2:, 2:]
    lat = lat_n - np.arange(z.shape[0], dtype=np.float64) * dlat
    dx = (R_M * np.radians(dlon) * np.cos(np.radians(lat))).astype(np.float32)[:, None]
    dy = np.float32(R_M * np.radians(dlat))
    gx = ((c + 2 * f + i) - (a + 2 * d + g)) / (8 * np.maximum(dx, 1.0))
    gy = ((g + 2 * h + i) - (a + 2 * b + c)) / (8 * dy)
    return 100.0 * np.sqrt(gx * gx + gy * gy)


def block_mean(z, k):
    """z (n x n, n a multiple of k) averaged over k x k blocks."""
    n0, n1 = z.shape[0] // k * k, z.shape[1] // k * k
    return z[:n0, :n1].reshape(n0 // k, k, n1 // k, k).mean(axis=(1, 3), dtype=np.float64).astype(np.float32)


def class_of(pct, th=THRESHOLDS):
    """0 flat, 1 foothill, 2 steep, 3 very steep."""
    return (pct >= th[0]).astype(np.uint8) + (pct >= th[1]) + (pct >= th[2])


def cell_hist(pct, pr, pc=None):
    """Per 15" cell histogram of percent grade over BINS: pct covers a 1-degree tile, rows from the north, pr rows and
    pc columns of it to a 15" cell (the rest, a shared last row or column, is left out); uint16 (240, 240, NB)."""
    pc = pr if pc is None else pc
    nr, nc = C15 * pr, C15 * pc
    b = np.searchsorted(BINS, pct[:nr, :nc], side='right') - 1
    b = np.clip(b, 0, NB - 1).astype(np.int32)
    idx = ((np.arange(nr) // pr)[:, None] * C15 + (np.arange(nc) // pc)[None, :]) * NB + b
    h = np.bincount(idx.ravel(), minlength=C15 * C15 * NB)
    return h.reshape(C15, C15, NB).astype(np.uint16)


# ------------------------------------------------------------------ HTTP
CA = '/root/.ccr/ca-bundle.crt'
UA = 'curl/8.5.0 (CityBuilderSim earth-2 pipeline)'


def fetch(url, dst, tries=6, resume=False):
    """url to dst through a temporary name; True when written, False on 404 (no such tile). resume: a part left by
    an earlier try is continued (the 7.47 GB GEBCO file)."""
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    part = dst + '.part'
    for k in range(tries):
        cmd = ['curl', '-sS', '-L', '--fail', '-A', UA, '-o', part, '-w', '%{http_code}'] + (['-C', '-'] if resume else []) + [url]
        r = subprocess.run(cmd, capture_output=True, text=True)
        code = r.stdout.strip()[-3:]
        if r.returncode == 0:
            os.replace(part, dst); return True
        if code in ('404', '409') and k >= 1:
            if os.path.exists(part): os.remove(part)
            return False
        time.sleep(min(60, 2 ** k))
    raise IOError('could not fetch %s: %s %s' % (url, code, r.stderr[-300:]))


_token = [None, 0.0]


def mpc_token():
    """An anonymous read token for the Planetary Computer's NASADEM container (it lasts about an hour or more)."""
    if _token[0] is None or time.time() - _token[1] > 1800:
        for k in range(6):
            try:
                ctx = ssl.create_default_context(cafile=CA) if os.path.exists(CA) else None
                with urllib.request.urlopen('https://planetarycomputer.microsoft.com/api/sas/v1/token/nasademeuwest/nasadem-cog',
                                            context=ctx, timeout=60) as r:
                    _token[0] = json.load(r)['token']; _token[1] = time.time(); break
            except Exception:
                time.sleep(2 ** k)
    return _token[0]


def nasadem_url(lat0, lon0):
    return 'https://nasademeuwest.blob.core.windows.net/nasadem-cog/v001/NASADEM_HGT_%s.tif?%s' % (tile_name(lat0, lon0), mpc_token())


def step_url(lat0, lon0):
    return 'https://step.esa.int/auxdata/dem/SRTMGL1/%s.SRTMGL1.hgt.zip' % tile_name(lat0, lon0, 'srtm')


def read_tif(path):
    import tifffile
    with tifffile.TiffFile(path) as tf:
        return tf.pages[0].asarray()


def nasadem_tile(lat0, lon0, keep=None):
    """The 1" heights of a 1-degree tile, int16 (3601 x 3601, rows from lat0 + 1, pixel centres on whole seconds;
    -32768 none), from NASADEM's Planetary Computer mirror, else SRTMGL1 v3 from ESA STEP; None when neither has it
    (sea, or beyond 61 N / 56 S). Returns (array, source name)."""
    keep = KEEP_TILES if keep is None else keep
    p = os.path.join(DL2, 'nasadem', 'NASADEM_HGT_%s.tif' % tile_name(lat0, lon0))
    if os.path.exists(p) or fetch(nasadem_url(lat0, lon0), p):
        z = read_tif(p)
        if not keep: os.remove(p)
        return z, 'nasadem'
    q = os.path.join(DL2, 'srtm', '%s.SRTMGL1.hgt.zip' % tile_name(lat0, lon0, 'srtm'))
    if os.path.exists(q) or fetch(step_url(lat0, lon0), q):
        with zipfile.ZipFile(q) as zf:
            raw = zf.read(zf.namelist()[0])
        if not keep: os.remove(q)
        return np.frombuffer(raw, '>i2').reshape(3601, 3601).astype(np.int16), 'srtmgl1'
    return None, None


def fill_voids(z):
    """-32768 (and below -1000) to the mean of the valid heights around (a few passes of a 3 x 3 mean); a tile with
    no valid height returns None."""
    z = z.astype(np.float32)
    bad = z < -1000
    if not bad.any():
        return z
    if bad.all():
        return None
    for _ in range(64):
        if not bad.any(): break
        v = np.where(bad, 0, z); w = (~bad).astype(np.float32)
        pv = np.pad(v, 1); pw = np.pad(w, 1)
        s = sum(pv[i:i + z.shape[0], j:j + z.shape[1]] for i in range(3) for j in range(3))
        n = sum(pw[i:i + z.shape[0], j:j + z.shape[1]] for i in range(3) for j in range(3))
        fill = bad & (n > 0)
        z[fill] = s[fill] / n[fill]; bad &= ~fill
    z[bad] = 0
    return z


# ------------------------------------------------------------------ GEBCO 2026
def gebco():
    """The GEBCO 2026 grid as a read-only memmap (43,200 x 86,400 int16, row 0 the southernmost, column 0 at 180 W);
    downloads the whole file (7.47 GB) first when it is not there."""
    if not os.path.exists(GEBCO_NC):
        log('gebco', 'downloading GEBCO_2026.nc, 7.47 GB')
        fetch(GEBCO_URL, GEBCO_NC, tries=20, resume=True)
    assert os.path.getsize(GEBCO_NC) == GEBCO_BYTES, os.path.getsize(GEBCO_NC)
    return np.memmap(GEBCO_NC, dtype='<i2', mode='r', offset=GEBCO_OFFSET, shape=(43200, 86400))


def gebco_tile(G, lat0, lon0, pad=0):
    """GEBCO over a 1-degree tile, north row first, (240 + 2 pad) square; columns wrap at 180."""
    r0 = (lat0 + 90) * C15 - pad; r1 = (lat0 + 91) * C15 + pad
    r0c, r1c = max(r0, 0), min(r1, 43200)
    cols = (np.arange((lon0 + 180) * C15 - pad, (lon0 + 181) * C15 + pad)) % 86400
    a = np.asarray(G[r0c:r1c][:, cols])
    if r0c > r0: a = np.vstack([np.repeat(a[:1], r0c - r0, 0), a])
    if r1 > r1c: a = np.vstack([a, np.repeat(a[-1:], r1 - r1c, 0)])
    return np.flipud(a)
