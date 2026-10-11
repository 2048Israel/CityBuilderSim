"""
hw.py - the city's highways on corridors (spec-roads-and-ports.md, part A5). NOT GAME CODE.

The model's highway plots (an Elevated Highway is 6.5 plots of land: 195 m of highway) are laid,
month by month, as RUNS that never move:
  - THE SPINE: the street line through the founding site's row, and its column; four arms out from
    the one interchange at the site.
  - Each new plot goes to the arm that is shortest and can still grow: over owned ground, across
    fresh water up to 14 plots as a bridge, never across the sea or onto ground the city does not own.
  - AT THE SEA an arm turns 45 degrees to the side with more of the city's ground ahead and runs on
    straight along the shore, turning back to its heading where the sea lets it.
  - When every arm stands at the city's edge, the next CORRIDOR opens: the parallel line 256 plots
    (one district) out that crosses a highway already laid on the city's ground - an interchange - and
    grows its two arms the same way.
"""
import math, sys, json
import numpy as np
from PIL import Image, ImageDraw
from roads_proto import noise, GROUND, GRASS, FOREST, FRESH, SALT, SAND, HIGHWAY_C, CENTRE

LINE_OFF = 20          # a highway rides the street line between two arterials: 20 mod 32
APART = 256            # corridors one district apart (7.68 km)
BRIDGE = 14            # the longest crossing of fresh water, in plots (TilePainter.MAX_BRIDGE, a highway's)
DIRS = [(1, 0), (1, 1), (0, 1), (-1, 1), (-1, 0), (-1, -1), (0, -1), (1, -1)]


def world(W=2048, H=1536, seed=11):
    T = np.zeros((H, W), np.int8)
    f = noise((H, W), 160, seed)
    T[f > 0.64] = FOREST
    yy, xx = np.mgrid[0:H, 0:W]
    n = noise((H, W), 220, seed + 3)
    coast = W * 0.70 + (n - 0.5) * 520
    # a bay cutting west into the land just south of the site's row
    bay = np.exp(-((yy - H * 0.56) / 110.0) ** 2) * 420
    T[xx > coast - bay] = SALT
    near = (xx > coast - bay - 4) & (T != SALT)
    T[near] = SAND
    ry = (H * 0.32 + 60 * np.sin(xx / 170.0) + 25 * np.sin(xx / 53.0)).astype(int)
    T[(np.abs(yy - ry) <= 3) & (T != SALT)] = FRESH
    return T


def owned_blob(T, S, r_km, seed=5):
    """The city's ground: a blob round the site, r_km in radius, ragged, on dry ground and its shore."""
    H, W = T.shape
    yy, xx = np.mgrid[0:H, 0:W]
    d = np.hypot(xx - S[0], (yy - S[1]) * 1.15) * 0.03
    n = noise((H, W), 120, seed)
    return d < r_km * (0.75 + 0.5 * n)


class Highways:
    def __init__(self, T, S):
        self.T, self.S = T, S
        H, W = T.shape
        self.H, self.W = H, W
        self.hw = np.zeros((H, W), bool)
        self.order = np.full((H, W), -1, np.int32)     # the plot's place in the order laid: runs never move
        self.n = 0
        y0 = S[1] - ((S[1] - LINE_OFF) % 32)
        x0 = S[0] - ((S[0] - LINE_OFF) % 32)
        self.hub = (x0, y0)
        self.arms = []
        self.corridors = [('row', y0), ('col', x0)]
        for d in (0, 4, 2, 6):
            self.arms.append(dict(x=x0, y=y0, home=d, head=d, len=0, blocked=False, line=('row', y0) if d in (0, 4) else ('col', x0)))
        self._lay(x0, y0)
        self.interchanges = [self.hub]

    def _ok(self, owned, x, y):
        return 0 <= x < self.W and 0 <= y < self.H and owned[y, x] and self.T[y, x] != SALT

    def _lay(self, x, y):
        if not self.hw[y, x]:
            self.hw[y, x] = True
            self.order[y, x] = self.n
            self.n += 1

    def _ahead(self, owned, x, y, d, k):
        """What stops a run k plots ahead: None, 'edge' (ground the city does not own), or 'sea' (the sea, or fresh
        water wider than a highway bridge)."""
        dx, dy = DIRS[d]
        wet = 0
        for s in range(1, k + 1):
            px, py = x + dx * s, y + dy * s
            if not (0 <= px < self.W and 0 <= py < self.H) or not owned[py, px]:
                return 'edge'
            if self.T[py, px] == SALT:
                return 'sea'
            wet = wet + 1 if self.T[py, px] == FRESH else 0
            if wet > BRIDGE:
                return 'sea'
        return None

    def _step(self, a, owned):
        x, y = a['x'], a['y']
        home = a['home']
        # back onto its heading where the way is clear for a while
        if a['head'] != home and self._ahead(owned, x, y, home, 24) is None:
            a['head'] = home
        why = self._ahead(owned, x, y, a['head'], 6)
        if why == 'edge':
            a['blocked'] = True            # it waits for the city to buy the ground ahead
            return False
        if why == 'sea':
            # AT THE SEA: 45 degrees to the side with more of the city's ground ahead - never more
            best = None
            for turn in (1, -1):
                d2 = (home + turn) % 8
                if self._ahead(owned, x, y, d2, 6) is not None:
                    continue
                dx, dy = DIRS[d2]
                score = sum(1 for s in range(1, 160) if self._ahead(owned, x, y, d2, s) is None)
                if best is None or score > best[0]:
                    best = (score, d2)
            if best is None:
                a['blocked'] = True
                return False
            a['head'] = best[1]
        d = a['head']
        dx, dy = DIRS[d]
        nx, ny = x + dx, y + dy
        if self.hw[ny, nx] and a['len'] > 2:
            # it meets a highway: across it (an interchange) if that one runs the other way; joined and stopped if not
            crossing_vertical = self.hw[ny - 1, nx] and self.hw[ny + 1, nx] if 0 < ny < self.H - 1 else False
            crossing_horizontal = self.hw[ny, nx - 1] and self.hw[ny, nx + 1] if 0 < nx < self.W - 1 else False
            if (dy == 0 and crossing_vertical) or (dx == 0 and crossing_horizontal):
                self.interchanges.append((nx, ny))
            else:
                a['blocked'] = True
                a['joined'] = True
                return False
        if a['head'] != home:
            # a run along the shore stops short of another highway beside it: no parallel twin
            hx, hy = DIRS[(d + 2) % 8]
            for s in list(range(2, 40)) + list(range(-39, -1)):
                px, py = nx + hx * s, ny + hy * s
                if 0 <= px < self.W and 0 <= py < self.H and self.hw[py, px] and self.order[py, px] < self.n - 400:
                    a['blocked'] = True
                    a['joined'] = True
                    return False
        if dx and dy:
            self._lay(x + dx, y)           # a diagonal is laid as a staircase, so the run stays joined
        self._lay(nx, ny)
        a['x'], a['y'] = nx, ny
        a['len'] += 1
        return True

    def grow(self, plots, owned):
        """Lays `plots` more plots of highway over the ground the city owns now."""
        for a in self.arms:
            if not a.get('joined'):
                a['blocked'] = False       # new ground may free an arm
        target = self.n + plots
        guard = 0
        while self.n < target and guard < 50 * plots + 1000:
            guard += 1
            live = [a for a in self.arms if not a['blocked']]
            if not live:
                if not self._new_corridor(owned):
                    return False
                continue
            # one corridor at a time: the spine's row both ways, then its column, then the next corridor out -
            # so a small city's highway is one straight line, and junctions come only with more highway
            first = min(self.corridors.index(a['line']) for a in live)
            a = min((a for a in live if self.corridors.index(a['line']) == first), key=lambda a: (a['len'], self.arms.index(a)))
            self._step(a, owned)
        return True

    def _new_corridor(self, owned):
        """The next parallel line, one district out, nearest the site, that crosses a highway on the city's ground."""
        hx, hy = self.hub
        cands = []
        for k in range(1, 12):
            for sgn in (1, -1):
                for kind in ('row', 'col'):
                    v = (hy if kind == 'row' else hx) + sgn * k * APART
                    if (kind, v) in self.corridors:
                        continue
                    # where it crosses a laid highway on owned ground
                    if kind == 'row' and 0 <= v < self.H:
                        xs = np.nonzero(self.hw[v, :] & owned[v, :])[0]
                        pts = [(int(x), v) for x in xs]
                    elif kind == 'col' and 0 <= v < self.W:
                        ys = np.nonzero(self.hw[:, v] & owned[:, v])[0]
                        pts = [(v, int(y)) for y in ys]
                    else:
                        pts = []
                    if pts:
                        p = min(pts, key=lambda p: math.hypot(p[0] - hx, p[1] - hy))
                        cands.append((k, math.hypot(p[0] - hx, p[1] - hy), kind, v, p))
            if cands:
                break
        if not cands:
            return False
        k, _, kind, v, p = min(cands)
        self.corridors.append((kind, v))
        self.interchanges.append(p)
        for d in ((0, 4) if kind == 'row' else (2, 6)):
            self.arms.append(dict(x=p[0], y=p[1], home=d, head=d, len=0, blocked=False, line=(kind, v)))
        return True


def render(T, owned, hwy, px=0.4, crop=None, title=None):
    H, W = T.shape
    g = np.array(GROUND, np.uint8)[T]
    m = ~owned
    g[m] = (g[m] * 0.68 + 30).astype(np.uint8)
    # the city's ground, lightly warmed
    o = owned & (T != SALT)
    g[o] = (g[o] * 0.9 + np.array([22, 14, 4])).clip(0, 255).astype(np.uint8)
    img = Image.fromarray(g, 'RGB')
    w, h = int(W * px), int(H * px)
    img = img.resize((w, h), Image.BILINEAR)
    dr = ImageDraw.Draw(img)
    ys, xs = np.nonzero(hwy.hw)
    r = max(1.6, px * 3)
    for y, x in zip(ys[::1], xs[::1]):
        X, Y = x * px, y * px
        dr.rectangle([X - r / 2, Y - r / 2, X + r / 2, Y + r / 2], fill=HIGHWAY_C)
    for (x, y) in hwy.interchanges:
        X, Y = x * px, y * px
        dr.ellipse([X - 4, Y - 4, X + 4, Y + 4], outline=CENTRE, width=2)
    sx, sy = hwy.S
    dr.ellipse([sx * px - 3, sy * px - 3, sx * px + 3, sy * px + 3], fill=(0xd8, 0x44, 0x2f))
    return img


if __name__ == '__main__':
    out = sys.argv[1]
    T = world()
    S = (900, 760)
    hwy = Highways(T, S)
    stages = [(4, 39, 'jerus'), (12, 600, 'b'), (22, 2600, 'c'), (34, 9000, 'd')]
    res = []
    done = 0
    for r_km, plots, name in stages:
        owned = owned_blob(T, S, r_km)
        hwy.grow(plots - done, owned)
        done = plots
        img = render(T, owned, hwy)
        img.save(f'{out}/hw-{name}.png')
        res.append(dict(stage=name, radius_km=r_km, plots=int(hwy.hw.sum()), km=round(hwy.hw.sum() * 0.03, 1),
                        corridors=len(hwy.corridors), interchanges=len(hwy.interchanges),
                        arms=len(hwy.arms), blocked=sum(a['blocked'] for a in hwy.arms),
                        diagonal_heads=sum(1 for a in hwy.arms if a['head'] != a['home'])))
        print(res[-1])
    json.dump(res, open(f'{out}/hw.json', 'w'), indent=1)
