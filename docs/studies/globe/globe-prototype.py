"""
The globe study's prototype (docs/studies/spec-globe.md): a design aid, not game code.

It draws the land office's expanded map of an Earth city two ways, on real
coasts, and measures what the study rests on:

  - TODAY: the city's plane (Lambert azimuthal equal-area round the city,
    EarthProj's sphere), clipped to the world's square (World.SIDE plots of
    30 m) and to the planet's disc (EarthProj.REACH_M) - what 0.7.123 draws;
  - THE GLOBE: orthographic round a view centre, as FoundingScreen's Globe
    draws it (orthoInverse());
  - the handoff: how far apart the two pictures are, in pixels, across a
    canvas, at a scale and a distance from the city.

Land and sea only, from GLOBE's 30" land mask as packaged by
`global-land-mask` (the Earth study's prototype data, spec-earth.md); the
game's forest, rivers, lakes, ice and desert are not drawn here.

    pip install --break-system-packages global-land-mask numpy pillow
    python3 globe-prototype.py OUTDIR
"""
import math
import sys
import time

import numpy as np
from PIL import Image
from global_land_mask import globe as glm

R = 6_371_007.0                      # EarthProj.RADIUS_M
REACH = 2 * R                        # EarthProj.REACH_M: the antipode's circle
HALF_SIDE = 753_664 * 30.0 / 2       # World.SIDE plots x World.PLOT_M, halved: 11,304,960 m

# The map's colours (TileRaster.GROUND, VOID; Palette.STAGE), the world off the
# city's own ground dimmed about as the expanded map draws it.
GRASS, SALT = (0xA8, 0xC4, 0x7E), (0x2F, 0x5D, 0x88)
DIM = 0.62
VOID, STAGE, SPACE = (0x16, 0x22, 0x2C), (0x0B, 0x11, 0x18), (0x0B, 0x11, 0x18)

W, H = 1345, 806                     # MapTiles' budgeted view: the expanded map at 1,389 x 868

CITIES = {
    'halifax': (44.65, -63.57),
    'manila': (14.60, 120.98),
}


# ---------------------------------------------------------------- projections

def lambert_inverse(lat0, lon0, x, y):
    """EarthProj's inverse: plane metres (x east, y north) round (lat0, lon0) to degrees; NaN off the disc."""
    f0 = math.radians(lat0)
    rho = np.hypot(x, y)
    s = rho / REACH                                   # sin of half the angle from the centre
    ok = s <= 1
    c = 2 * np.arcsin(np.clip(s, 0, 1))
    sc, cc = np.sin(c), np.cos(c)
    with np.errstate(invalid='ignore', divide='ignore'):
        lat = np.degrees(np.arcsin(np.clip(cc * math.sin(f0) + np.where(rho > 0, y * sc * math.cos(f0) / rho, 0), -1, 1)))
        lon = lon0 + np.degrees(np.arctan2(x * sc, rho * cc * math.cos(f0) - y * sc * math.sin(f0)))
    lat = np.where(rho == 0, lat0, lat)
    lon = np.where(rho == 0, lon0, lon)
    return np.where(ok, lat, np.nan), np.where(ok, (lon + 180) % 360 - 180, np.nan)


def lambert_forward(lat0, lon0, lat, lon):
    f0, f, dl = math.radians(lat0), np.radians(lat), np.radians(lon - lon0)
    cosc = math.sin(f0) * np.sin(f) + math.cos(f0) * np.cos(f) * np.cos(dl)
    k = np.sqrt(2 / np.maximum(1e-15, 1 + cosc))
    return R * k * np.cos(f) * np.sin(dl), R * k * (math.cos(f0) * np.sin(f) - math.sin(f0) * np.cos(f) * np.cos(dl))


def ortho_inverse(lat0, lon0, x, y):
    """FoundingScreen.orthoInverse(): NaN off the planet's disc."""
    f0 = math.radians(lat0)
    rho = np.hypot(x, y)
    ok = rho <= R
    c = np.arcsin(np.clip(rho / R, 0, 1))
    sc, cc = np.sin(c), np.cos(c)
    with np.errstate(invalid='ignore', divide='ignore'):
        lat = np.degrees(np.arcsin(np.clip(cc * math.sin(f0) + np.where(rho > 0, y * sc * math.cos(f0) / rho, 0), -1, 1)))
        lon = lon0 + np.degrees(np.arctan2(x * sc, rho * cc * math.cos(f0) - y * sc * math.sin(f0)))
    lat = np.where(rho == 0, lat0, lat)
    lon = np.where(rho == 0, lon0, lon)
    return np.where(ok, lat, np.nan), np.where(ok, (lon + 180) % 360 - 180, np.nan)


def ortho_forward(lat0, lon0, lat, lon):
    f0, f, dl = math.radians(lat0), np.radians(lat), np.radians(lon - lon0)
    return R * np.cos(f) * np.sin(dl), R * (math.cos(f0) * np.sin(f) - math.sin(f0) * np.cos(f) * np.cos(dl))


def great_circle_km(lat1, lon1, lat2, lon2):
    f1, f2, dl = map(math.radians, (lat1, lat2, lon2 - lon1))
    return R / 1000 * math.acos(max(-1, min(1, math.sin(f1) * math.sin(f2) + math.cos(f1) * math.cos(f2) * math.cos(dl))))


def destination(lat, lon, bearing_deg, km):
    f, l, b, d = math.radians(lat), math.radians(lon), math.radians(bearing_deg), km * 1000 / R
    f2 = math.asin(math.sin(f) * math.cos(d) + math.cos(f) * math.sin(d) * math.cos(b))
    l2 = l + math.atan2(math.sin(b) * math.sin(d) * math.cos(f), math.cos(d) - math.sin(f) * math.sin(f2))
    return math.degrees(f2), (math.degrees(l2) + 180) % 360 - 180


# ---------------------------------------------------------------- drawing

def screen_metres(km_px, w=W, h=H):
    i, j = np.meshgrid(np.arange(w), np.arange(h))
    return (i + 0.5 - w / 2) * km_px * 1000, (h / 2 - j - 0.5) * km_px * 1000


def paint(lat, lon, off_colour, off_mask):
    land = np.zeros(lat.shape, dtype=bool)
    good = ~np.isnan(lat)
    land[good] = glm.is_land(lat[good], lon[good])
    img = np.empty(lat.shape + (3,), dtype=np.uint8)
    img[...] = [round(v * DIM) for v in SALT]
    img[land] = [round(v * DIM) for v in GRASS]
    img[~good] = VOID
    img[off_mask] = off_colour
    return img


def today(city, km_px):
    """The plane round the city, as the expanded map draws it: the void off the planet, the stage off the world's square."""
    lat0, lon0 = CITIES[city]
    x, y = screen_metres(km_px)
    lat, lon = lambert_inverse(lat0, lon0, x, y)
    off_square = (np.abs(x) >= HALF_SIDE) | (np.abs(y) >= HALF_SIDE)
    return paint(lat, lon, STAGE, off_square)


def the_globe(lat0, lon0, km_px):
    x, y = screen_metres(km_px)
    lat, lon = ortho_inverse(lat0, lon0, x, y)
    return paint(lat, lon, SPACE, np.isnan(lat))


def mark(img, km_px, centre_lat, centre_lon, lat, lon, forward):
    """A small ring at (lat, lon): the city."""
    x, y = forward(centre_lat, centre_lon, np.array([lat]), np.array([lon]))
    cx, cy = W / 2 + x[0] / 1000 / km_px, H / 2 - y[0] / 1000 / km_px
    for a in np.linspace(0, 2 * math.pi, 64, endpoint=False):
        for r in (4.5, 5.5):
            u, v = int(round(cx + r * math.cos(a))), int(round(cy + r * math.sin(a)))
            if 0 <= u < W and 0 <= v < H:
                img[v, u] = (0xF2, 0xC1, 0x4E)


# ---------------------------------------------------------------- measures

def handoff_px(km_px, w=W, h=H):
    """At a scale, the most two pictures of the ground round the city part on the canvas: the plane against the globe facing the city, in px."""
    lat0, lon0 = 0.0, 0.0            # rotationally symmetric about the centre: any city gives the same
    x, y = screen_metres(km_px, w, h)
    lat, lon = ortho_inverse(lat0, lon0, x, y)
    px, py = lambert_forward(lat0, lon0, lat, lon)
    return float(np.nanmax(np.hypot(px - x, py - y))) / 1000 / km_px


def roll_at(city_lat, city_lon, lat, lon):
    """The angle, degrees, from the plane's up to north at (lat, lon) on the city's plane: what a north-up globe there is turned by against it."""
    d = 1e-4
    x0, y0 = lambert_forward(city_lat, city_lon, np.array([lat]), np.array([lon]))
    x1, y1 = lambert_forward(city_lat, city_lon, np.array([lat + d]), np.array([lon]))
    return math.degrees(math.atan2(x1[0] - x0[0], y1[0] - y0[0]))


def reach_px(city, km, bearing, km_px, w=W, h=H):
    """A view centred km from the city: the plane against a globe there turned to the plane's up and scaled to its mean scale, the most they part, px."""
    clat, clon = CITIES[city]
    vlat, vlon = destination(clat, clon, bearing, km)
    x, y = screen_metres(km_px, w, h)
    lat, lon = ortho_inverse(vlat, vlon, x, y)
    # the plane round the view centre, in the plane's own axes
    px, py = lambert_forward(clat, clon, lat, lon)
    cx, cy = lambert_forward(clat, clon, np.array([vlat]), np.array([vlon]))
    px, py = px - cx[0], py - cy[0]
    # the globe turned by the plane's roll at the centre and scaled to the plane's mean scale there
    g = math.radians(roll_at(clat, clon, vlat, vlon))
    gx, gy = x * math.cos(g) + y * math.sin(g), -x * math.sin(g) + y * math.cos(g)
    s = float(np.nanmean(np.hypot(px, py)) / np.nanmean(np.hypot(gx, gy)))
    return float(np.nanmax(np.hypot(px - s * gx, py - s * gy))) / 1000 / km_px


def main(out):
    t = time.time()
    fit_km = 2 * HALF_SIDE / 1000 / H                    # MapFrame.leastScale(): the world across the shorter side
    globe_km = 2 * R / 1000 / (0.9 * H)                  # FoundingScreen's MOST_KM_PX, on this canvas
    for city in CITIES:
        img = today(city, fit_km)
        mark(img, fit_km, *CITIES[city], *CITIES[city], lambert_forward)
        Image.fromarray(img).save(f'{out}/today-{city}.png')
        img = the_globe(*CITIES[city], globe_km)
        mark(img, globe_km, *CITIES[city], *CITIES[city], ortho_forward)
        Image.fromarray(img).save(f'{out}/globe-{city}.png')
    lat0, lon0 = CITIES['manila']
    for km in (8.0,):
        img = the_globe(lat0, lon0, km)
        mark(img, km, lat0, lon0, lat0, lon0, ortho_forward)
        Image.fromarray(img).save(f'{out}/globe-manila-{int(km)}km.png')
    print(f'boards drawn in {time.time() - t:.1f} s; the fit {fit_km:.2f} km/px, the globe {globe_km:.2f} km/px')

    print('\n--- the square and the disc ---')
    c = 2 * math.asin(HALF_SIDE / REACH)
    print(f'the square cuts along its axes at {math.degrees(c):.2f} deg, {math.degrees(c) * math.pi * R / 180 / 1000:,.0f} km from the city')
    for city, (la, lo) in CITIES.items():
        print(f'  {city}: straight south the land stops at {la - math.degrees(c):.1f} deg; antipode {-la:.1f}, {(lo + 360) % 360 - 180:.1f}')
    a = math.acos(HALF_SIDE / REACH)
    seg = REACH ** 2 * (a - math.sin(a) * math.cos(a))
    print(f'  the four caps the square leaves out: {4 * seg / 1e12:.1f} M km2, {4 * seg / (4 * math.pi * R * R):.1%} of the planet')
    # how squashed the far side is: radial scale cos(c/2) at angle c from the city, tangential its inverse
    for deg in (90, 120, 150, 170):
        k = math.cos(math.radians(deg / 2))
        print(f'  {deg} deg from the city: drawn {k:.3f} as deep and {1 / k:.2f} times as wide; its ring is the outer {1 - math.sin(math.radians(deg / 2)):.1%} of the disc')

    print('\n--- the handoff: the plane against the globe facing the city ---')
    for name, (w, h) in (('the expanded map', (W, H)), ('the founding globe', (560, 270))):
        for km_px in (0.48, 0.96, 1.92, 4.0, 8.0):
            print(f'  {name} {w}x{h} at {km_px:5.2f} km/px: {handoff_px(km_px, w, h):6.2f} px at worst')
        d = math.hypot(w / 2, h / 2)
        print(f'  {name}: half a pixel at the corner from 2R/D^1.5 = {2 * R / 1000 / d ** 1.5:.2f} km/px (D = {d:.0f} px)')

    print('\n--- a view away from the city at 0.96 km/px (32 plots a pixel) on the expanded map ---')
    for km in (100, 300, 500, 1000, 2000):
        rolls = [abs(roll_at(*CITIES['halifax'], *destination(*CITIES['halifax'], b, km))) for b in (90,)]
        worst = max(reach_px('halifax', km, b, 0.96) for b in (0, 45, 90, 135, 180))
        print(f'  {km:5d} km from Halifax: a north-up globe turned {rolls[0]:4.1f} deg (going east); turned to match, {worst:5.2f} px apart at worst')


if __name__ == '__main__':
    main(sys.argv[1] if len(sys.argv) > 1 else '.')
