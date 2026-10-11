"""
roads_proto.py - a prototype of the district plan in spec-roads-and-ports.md.

NOT GAME CODE. It checks that the proposed rules hold and draws the boards:
  streets by need (whole blocks of the 8-plot lattice opened from the hub,
  arterials every 32 plots, superblocks for large buildings), the model's
  road plots drawn as the streets' surface (width and kind, tracks for what
  the city has not paid for), buildings only within reach of an open street.

Buildings and roads come from the game's own data file (buildings.json at
0.7.79) and MapCheck's fixture of Jerus's city (JERUS_COUNTS), scaled.
"""
import json, math, sys, hashlib
import numpy as np
from PIL import Image, ImageDraw

# --------------------------------------------------------------- constants
PLOT_SQFT = 9687.5          # BuildingVisual.SQ_FT_PER_PLOT
LAT, OFF = 8, 4             # street lattice: lines at 4 mod 8 (TilePainter.JUNCTION_APART, STREET_AT)
ART = 32                    # arterials: lines at 4 mod 32 (every 4th street line, 960 m)
LONG = True                 # long blocks: two lattice cells merged, 7 x 15, the cross street every 16 plots
REACH = 4                   # TilePainter.REACH, counted across corners
FILL = 0.90                 # a block's lots taken before the next block opens
W_MIN, W_FULL = 0.5, 1.0    # a street's surface, in plots of its right of way: 15 m, 30 m

GRASS, FOREST, FRESH, SALT, SAND = 0, 1, 2, 3, 4
GROUND = [(0xa8, 0xc4, 0x7e), (0x52, 0x7c, 0x45), (0x78, 0xb4, 0xdc), (0x2f, 0x5d, 0x88), (0xe6, 0xda, 0xaa)]
FILLC = [0xeaa572, 0xf2c94c, 0x5b8fd6, 0x9076ba, 0xe0cb84, 0x2ea89e, 0xd0587a, 0xf7f4ee, 0xd8442f, 0x8e9093]
EDGEC = [0x9a5a2c, 0x987718, 0x2c5590, 0x55407a, 0xa99550, 0x17635d, 0x86304a, 0x8f8a82, 0x86251a, 0x3d3f42]
FLATS_FILL, FLATS_EDGE = 0xd07a44, 0x7e4119
GRAVEL_C, PAVED_C, HIGHWAY_C = (0xcd, 0xb0, 0x7c), (0xa4, 0xaa, 0xb0), (0x3b, 0x40, 0x48)
PAVED_CASE, HW_CASE, CENTRE = (0x6a, 0x70, 0x77), (0x1d, 0x21, 0x26), (0xf0, 0xcf, 0x5a)
VERGE = (0xbf, 0xd3, 0x9a)
TRACK_C = (0xa8, 0x8a, 0x5a)

CLASS_OF = {'RESIDENTIAL': 0, 'COMMERCIAL': 1, 'LUXURY': 1, 'HOSPITALITY': 1, 'BUSINESS_SERVICES': 2,
            'AGRICULTURE': 4, 'ELECTRICITY': 5, 'WATER': 5, 'INFRASTRUCTURE': 5, 'EDUCATION': 6,
            'HEALTHCARE': 7, 'SAFETY': 8, 'MINING': 9}
OUTER_CATS = {'AGRICULTURE', 'ELECTRICITY', 'WATER', 'MINING', 'RAIL', 'AUTOMOTIVE'}

JERUS_COUNTS = [1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114,
                1, 2, 0, 27, 12, 0, 8, 1, 1, 5, 385, 6, 47, 47, 40, 7, 6, 4, 2, 1, 2, 7, 37, 0, 2, 0, 2, 0, 0, 0, 6, 113, 106,
                1, 5, 0, 0, 14, 114, 148, 27, 11, 2, 11, 0, 0, 7, 114, 0, 0, 133, 0, 1120, 0]
JERUS_DRY_PLOTS = 89.63e6 / 900.0   # 89.63 km2 of dry ground


def h32(*a):
    """A stable hash in [0, 1)."""
    s = hashlib.blake2b(repr(a).encode(), digest_size=8).digest()
    return int.from_bytes(s, 'little') / 2 ** 64


def load_types(path):
    d = json.load(open(path))
    items = d if isinstance(d, list) else d.get('buildings', d)
    if isinstance(items, dict):
        items = list(items.values())
    types = {}
    for b in items:
        plots = b['land'] / PLOT_SQFT
        cat = b['category']
        road = {29: 'gravel', 13: 'paved', 30: 'highway'}.get(b['id'])
        across = int(max(1, min(32, round(math.sqrt(plots)))))
        down = int(max(1, min(32, round(plots / across))))
        types[b['id']] = dict(id=b['id'], name=b['name'], cat=cat, plots=plots, w=across, h=down,
                              cls=CLASS_OF.get(cat, 3), flats=(cat == 'RESIDENTIAL' and b.get('dwellings', 1) > 1),
                              outer=cat in OUTER_CATS, road=road,
                              line=b['id'] in (62, 63), cells=round(plots) if (road or b['id'] in (62, 63)) else across * down)
    return types


# --------------------------------------------------------------- the plan
class Plan:
    """One district's plan: its streets (by need), their surface (by the model's road), its buildings."""

    def __init__(self, terrain, owned, hub, seed=4127):
        self.T, self.O = terrain, owned
        self.H, self.W = terrain.shape
        self.hub = hub
        self.seed = seed
        self.dry = owned & ((terrain == GRASS) | (terrain == FOREST) | (terrain == SAND))
        self.fixed = np.zeros(terrain.shape, bool)      # highway or rail plots laid before the plan
        self.hw = np.zeros(terrain.shape, bool)
        self.nbx = (self.W - OFF - 1) // LAT            # blocks across: between lines OFF+8i and OFF+8(i+1)
        self.nby = (self.H - OFF - 1) // LAT

    # block (i, j) interior: x in [OFF+8i+1, OFF+8i+7]
    def interior(self, i, j):
        x0, y0 = OFF + LAT * i + 1, OFF + LAT * j + 1
        return x0, y0, x0 + LAT - 1, y0 + LAT - 1   # exclusive ends

    def block_room(self, i, j):
        x0, y0, x1, y1 = self.interior(i, j)
        return int((self.dry[y0:y1, x0:x1] & ~self.fixed[y0:y1, x0:x1]).sum())

    def cell_dir(self, i, j):
        """An arterial cell's long blocks run east-west ('H') or north-south ('V'), by the cell's own hash."""
        ci, cj = (LAT * i) // ART, (LAT * j) // ART
        return 'H' if h32(self.seed, 'dir', ci, cj) < 0.5 else 'V'

    def merged(self, i, j, side):
        """Whether block (i, j)'s side is the line inside a long block (no street): the odd lines of a cell, across its blocks' length."""
        if not LONG:
            return False
        if side in 'WE':
            line = OFF + LAT * (i + (1 if side == 'E' else 0))
            return self.cell_dir(i, j) == 'H' and (line - OFF) % 16 == 8
        line = OFF + LAT * (j + (1 if side == 'S' else 0))
        return self.cell_dir(i, j) == 'V' and (line - OFF) % 16 == 8

    def region(self, i, j):
        """A block's lots: its interior, and the merged line on its east or south side if it has one."""
        x0, y0, x1, y1 = self.interior(i, j)
        if self.merged(i, j, 'E'):
            x1 += 1
        if self.merged(i, j, 'S'):
            y1 += 1
        return x0, y0, x1, y1

    def _join(self, street, art):
        from collections import deque
        H, W = street.shape
        lat = np.zeros((H, W), bool)
        lat[(np.arange(H) - OFF) % LAT == 0, :] = True
        lat[:, (np.arange(W) - OFF) % LAT == 0] = True
        ok = lat & self.O & (self.T != SALT) & ~self.hw
        self.joins = 0
        for _ in range(50):
            lab = -np.ones((H, W), np.int32)
            sizes = []
            for y, x in zip(*np.nonzero(street)):
                if lab[y, x] >= 0:
                    continue
                c = len(sizes); n = 0
                st = [(y, x)]; lab[y, x] = c
                while st:
                    cy, cx = st.pop(); n += 1
                    for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                        ny, nx = cy + dy, cx + dx
                        if 0 <= ny < H and 0 <= nx < W and street[ny, nx] and lab[ny, nx] < 0:
                            lab[ny, nx] = c; st.append((ny, nx))
                sizes.append(n)
            if len(sizes) <= 1:
                break
            main = int(np.argmax(sizes))
            # breadth first from the main piece along lattice lines to the nearest other piece
            prev = {}
            dq = deque()
            for y, x in zip(*np.nonzero(lab == main)):
                dq.append((y, x)); prev[(y, x)] = None
            hit = None
            while dq and hit is None:
                cy, cx = dq.popleft()
                for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    ny, nx = cy + dy, cx + dx
                    if 0 <= ny < H and 0 <= nx < W and (ny, nx) not in prev and ok[ny, nx]:
                        prev[(ny, nx)] = (cy, cx)
                        if street[ny, nx] and lab[ny, nx] != main:
                            hit = (ny, nx); break
                        dq.append((ny, nx))
            if hit is None:
                break
            p = prev[hit]
            while p is not None and not (street[p] and lab[p] == main):
                street[p] = True
                p = prev[p]
            self.joins += 1
        return street

    def line_is_art(self, v):
        return (v - OFF) % ART == 0

    def lay_highway(self, plots):
        for (x, y) in plots:
            self.hw[y, x] = True
            self.fixed[y, x] = True

    # ---------------------------------------------------------- 1. superblocks and the open blocks
    def build(self, buildings, budget, alleys=True):
        """buildings: list of type dicts (one per building); budget: {'paved': plots, 'gravel': plots}."""
        nbx, nby = self.nbx, self.nby
        room = np.zeros((nby, nbx), int)
        for j in range(nby):
            for i in range(nbx):
                room[j, i] = self.block_room(i, j)
        usable = room >= 25
        hx, hy = self.hub
        hi, hj = (hx - OFF) // LAT, (hy - OFF) // LAT

        def dist(i, j):
            cx, cy = OFF + LAT * i + LAT / 2, OFF + LAT * j + LAT / 2
            return math.hypot(cx - hx, cy - hy) / LAT + 0.9 * h32(self.seed, 'blk', i, j)

        # A building a block's 7 x 7 lots cannot hold takes a superblock (2 x 2 blocks and up); the rest go to the lots.
        large = [b for b in buildings if max(b['w'], b['h']) > LAT - 1]
        small = [b for b in buildings if max(b['w'], b['h']) <= LAT - 1]
        large.sort(key=lambda b: (-b['cells'], b['id']))
        small_plots = sum(b['w'] * b['h'] for b in small)

        # The order the city's blocks open in: grown outward from the hub, always beside an open one (one network).
        import heapq
        order, seen = [], np.zeros((nby, nbx), bool)
        heap = []
        start = (min(max(hi, 0), nbx - 1), min(max(hj, 0), nby - 1))
        if not usable[start[1], start[0]]:
            cand = [(dist(i, j), i, j) for j in range(nby) for i in range(nbx) if usable[j, i]]
            start = min(cand)[1:]
        heapq.heappush(heap, (dist(*start), start))
        seen[start[1], start[0]] = True
        while heap:
            _, (i, j) = heapq.heappop(heap)
            order.append((i, j))
            for di, dj in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                a, b = i + di, j + dj
                if 0 <= a < nbx and 0 <= b < nby and not seen[b, a] and usable[b, a]:
                    seen[b, a] = True
                    heapq.heappush(heap, (dist(a, b), (a, b)))
        rank = {blk: k for k, blk in enumerate(order)}

        # BANDS: what follows people nearest the hub, industry next, the outer kinds (farms, utilities, plants,
        # rail) beyond - each band's blocks taken in the growth order, so the open blocks are one piece.
        def band(bd):
            if bd['outer']:
                return 2
            return 1 if bd['cls'] == 3 else 0
        self_unplaced_large = []
        taken = {}                    # block -> superblock id
        supers = []                   # (i, j, kx, ky, building)
        def group_ok(i, j, kx, ky, lo):
            for bb in range(j, j + ky):
                for aa in range(i, i + kx):
                    if not (0 <= aa < nbx and 0 <= bb < nby) or not usable[bb, aa] or (aa, bb) in taken:
                        return False
                    if rank.get((aa, bb), -1) < lo:
                        return False
            for aa in range(i + 1, i + kx):              # no arterial inside a superblock
                if self.line_is_art(OFF + LAT * aa):
                    return False
            for bb in range(j + 1, j + ky):
                if self.line_is_art(OFF + LAT * bb):
                    return False
            return True
        bands = [[], [], []]
        for bd in buildings:
            bands[band(bd)].append(bd)
        lo = 0
        self.band_range = []
        lot_blocks = {}               # block -> band whose small buildings it holds
        for g in range(3):
            lg = sorted([bd for bd in bands[g] if max(bd['w'], bd['h']) > LAT - 1], key=lambda bd: (-bd['cells'], bd['id']))
            sm = [bd for bd in bands[g] if max(bd['w'], bd['h']) <= LAT - 1]
            sm_plots = sum(bd['w'] * bd['h'] for bd in sm)
            need = sm_plots / (FILL * 49.0) + sum(math.ceil((bd['w'] + 1) / LAT) * math.ceil((bd['h'] + 1) / LAT) for bd in lg)
            span = max(1, int(math.ceil(need)))
            hi_rank = lo
            for n, bd in enumerate(lg):
                kx, ky = math.ceil((bd['w'] + 1) / LAT), math.ceil((bd['h'] + 1) / LAT)
                # spread through the band: a hashed place u^1.5 of the way through it (the painter's PREFERENCE)
                u = h32(self.seed, 'large', bd['id'], n)
                want = lo + int((u ** 1.5) * span) if g == 0 else lo
                placed = None
                for k in list(range(want, len(order))) + list(range(lo, want)):
                    i, j = order[k]
                    if group_ok(i, j, kx, ky, lo):
                        placed = (i, j)
                        break
                if placed is None:
                    self_unplaced_large.append(bd)
                    continue
                i, j = placed
                sid = len(supers)
                for bb in range(j, j + ky):
                    for aa in range(i, i + kx):
                        taken[(aa, bb)] = sid
                        hi_rank = max(hi_rank, rank[(aa, bb)])
                supers.append((i, j, kx, ky, bd))
            lots = 0
            k = lo
            while k < len(order) and lots < sm_plots / FILL:
                blk = order[k]
                if blk not in taken and blk not in lot_blocks:
                    lot_blocks[blk] = g
                    lots += room[blk[1], blk[0]]
                    hi_rank = max(hi_rank, k)
                k += 1
            self.band_range.append((lo, hi_rank))
            lo = hi_rank + 1
        open_blocks = set(order[:lo])
        self.order, self.rank, self.open_blocks, self.supers, self.taken = order, rank, open_blocks, supers, taken
        self.lot_blocks = lot_blocks

        # ---------------------------------------------------------- 2. the streets: every open block's sides
        street = np.zeros((self.H, self.W), bool)
        art = np.zeros((self.H, self.W), bool)
        seg_dist = {}
        def side_plots(i, j, side):
            x0, y0 = OFF + LAT * i, OFF + LAT * j
            if side == 'N':
                return [(x, y0) for x in range(x0, x0 + LAT + 1)]
            if side == 'S':
                return [(x, y0 + LAT) for x in range(x0, x0 + LAT + 1)]
            if side == 'W':
                return [(x0, y) for y in range(y0, y0 + LAT + 1)]
            return [(x0 + LAT, y) for y in range(y0, y0 + LAT + 1)]
        for (i, j) in open_blocks:
            sid = taken.get((i, j))
            for side, (di, dj) in (('N', (0, -1)), ('S', (0, 1)), ('W', (-1, 0)), ('E', (1, 0))):
                nb = (i + di, j + dj)
                if sid is not None and taken.get(nb) == sid:
                    continue                                   # a line inside a superblock stays closed
                if self.merged(i, j, side) and nb in open_blocks:
                    continue                                   # the line inside a long block: no street
                for (x, y) in side_plots(i, j, side):
                    if 0 <= x < self.W and 0 <= y < self.H and self.O[y, x] and self.T[y, x] != SALT and not self.hw[y, x]:
                        street[y, x] = True
                        if (side in 'NS' and self.line_is_art(y)) or (side in 'WE' and self.line_is_art(x)):
                            art[y, x] = True
                        r = self.rank[(i, j)]
                        seg_dist[(x, y)] = min(seg_dist.get((x, y), 10 ** 9), r)
        # ONE NETWORK: a piece the open blocks left apart is joined to the biggest along the lattice's lines - the
        # shortest way, over a long block's middle line or across fresh water as a bridge.
        street = self._join(street, art)
        # Fresh water under a street: a bridge where it is no wider than 6 plots (14 on an arterial), else the line stops there.
        wet = street & (self.T == FRESH)
        self.bridge = wet
        street_dry = street & ~self.fixed
        self.street, self.art = street_dry, art & street_dry

        # ---------------------------------------------------------- 3. the surface: the model's road plots as width and kind
        plots = sorted(zip(*np.nonzero(self.street)), key=lambda p: (0 if self.art[p] else 1, seg_dist.get((p[1], p[0]), 0), p))
        R = len(plots)
        B = budget.get('paved', 0) + budget.get('gravel', 0)
        width = {}
        left = B
        # every street plot its least width, arterials and the core first
        for p in plots:
            if left >= W_MIN:
                width[p] = W_MIN
                left -= W_MIN
            else:
                width[p] = 0.0                                  # a track: the city has bought no road here
        # then arterials to their full width, then the core's streets
        for p in plots:
            if width[p] == W_MIN and left >= W_FULL - W_MIN and (self.art[p] or True):
                width[p] = W_FULL
                left -= (W_FULL - W_MIN)
        # then alleys: one a block, across its middle, a T at each end - never a +
        alley = np.zeros((self.H, self.W), bool)
        lot_room = sum(int((self.dry[y0:y1, x0:x1] & ~street[y0:y1, x0:x1] & ~self.fixed[y0:y1, x0:x1]).sum())
                       for (i, j) in open_blocks for (x0, y0, x1, y1) in [self.region(i, j)])
        need = sum(bd['w'] * bd['h'] for bd in buildings)
        slack = max(0.0, lot_room - need / FILL)
        self.lot_room, self.need = lot_room, need
        if alleys:
            for (i, j) in order:
                if left < 3.5 or slack < 7:
                    break
                if (i, j) not in open_blocks or (i, j) in taken:
                    continue
                x0, y0, x1, y1 = self.interior(i, j)
                vertical = ((i + j) % 2 == 0)                   # staggered: no alley meets another across a street
                cells = [(x0 + 3, y) for y in range(y0, y1)] if vertical else [(x, y0 + 3) for x in range(x0, x1)]
                if all(self.dry[y, x] and not self.fixed[y, x] for (x, y) in cells):
                    for (x, y) in cells:
                        alley[y, x] = True
                    left -= W_MIN * len(cells)
                    slack -= len(cells)
        self.alley = alley
        self.surplus = left
        # kind: paved where the plan reaches first, gravel after
        kind = {}
        pv = budget.get('paved', 0)
        for p in plots:
            if width[p] > 0 and pv >= width[p]:
                kind[p] = 'paved'
                pv -= width[p]
            elif width[p] > 0:
                kind[p] = 'gravel'
            else:
                kind[p] = 'track'
        self.width, self.kind = width, kind
        self.R = R

        # ---------------------------------------------------------- 4. the buildings
        occ = np.zeros((self.H, self.W), np.int32)          # 0 free, -1 road/fixed, k+1 building k
        occ[self.street | self.fixed | alley | self.bridge] = -1
        occ[~self.dry] = -1
        roads = self.street | alley | self.hw
        # reach: within REACH plots of a street, across corners (a chessboard dilation)
        d = np.full((self.H, self.W), 99, np.int16)
        d[self.street | alley] = 0
        cur = self.street | alley
        for k in range(1, REACH + 1):
            n = cur.copy()
            n[1:, :] |= cur[:-1, :]; n[:-1, :] |= cur[1:, :]
            n[:, 1:] |= cur[:, :-1]; n[:, :-1] |= cur[:, 1:]
            n[1:, 1:] |= cur[:-1, :-1]; n[:-1, :-1] |= cur[1:, 1:]
            n[1:, :-1] |= cur[:-1, 1:]; n[:-1, 1:] |= cur[1:, :-1]
            d[n & (d == 99)] = k
            cur = n
        self.reach = d
        self.placed = []
        self.unplaced = list(self_unplaced_large)
        # large buildings in their superblocks, centred, front on the nearest open side
        for (i, j, kx, ky, b) in supers:
            x0, y0 = OFF + LAT * i + 1, OFF + LAT * j + 1
            wx, wy = LAT * kx - 1, LAT * ky - 1
            w, h = b['w'], b['h']
            if w > wx or h > wy:
                w, h = h, w
            bx, by = x0 + (wx - w) // 2, y0 + (wy - h) // 2
            ok = (occ[by:by + h, bx:bx + w] == 0).all()
            if not ok:
                # slide inside the superblock to the first free box
                found = None
                for yy in range(y0, y0 + wy - h + 1):
                    for xx in range(x0, x0 + wx - w + 1):
                        if (occ[yy:yy + h, xx:xx + w] == 0).all():
                            found = (xx, yy)
                            break
                    if found:
                        break
                if not found:
                    self.unplaced.append(b)
                    continue
                bx, by = found
            k = len(self.placed)
            occ[by:by + h, bx:bx + w] = k + 1
            self.placed.append((bx, by, w, h, b))
        # small buildings, largest first, in their band's blocks: homes to the quiet streets, shops and offices to the
        # arterials; what a band's blocks cannot hold goes on to the next open blocks.
        def on_art(blk):
            i, j = blk
            x0, y0 = OFF + LAT * i, OFF + LAT * j
            return self.line_is_art(x0) or self.line_is_art(x0 + LAT) or self.line_is_art(y0) or self.line_is_art(y0 + LAT)
        blocks_open = [blk for blk in order if blk in open_blocks]
        lists = {}
        def blocks_for(g, cls):
            key = (g, cls)
            if key not in lists:
                mine = [blk for blk in blocks_open if lot_blocks.get(blk) == g]
                if cls in (1, 2):
                    mine.sort(key=lambda blk: (0 if on_art(blk) else 1, self.rank[blk]))
                elif cls == 0:
                    mine.sort(key=lambda blk: (1 if on_art(blk) else 0, self.rank[blk]))
                rest = [blk for blk in blocks_open if lot_blocks.get(blk) != g]
                lists[key] = [mine + rest, 0]
            return lists[key]
        small.sort(key=lambda bd: (-bd['w'] * bd['h'], bd['id']))
        for bd in small:
            lst_ptr = blocks_for(band(bd), bd['cls'])
            lst, ptr = lst_ptr
            done = False
            k = ptr
            while k < len(lst) and not done:
                i, j = lst[k]
                x0, y0, x1, y1 = self.region(i, j)
                best = None
                for (w, h) in ((bd['w'], bd['h']), (bd['h'], bd['w'])):
                    for yy in range(y0, y1 - h + 1):
                        for xx in range(x0, x1 - w + 1):
                            box = occ[yy:yy + h, xx:xx + w]
                            if box.shape != (h, w) or not (box == 0).all():
                                continue
                            r = int(d[yy:yy + h, xx:xx + w].min())
                            if r > REACH:
                                continue
                            key = (r, yy, xx)
                            if best is None or key < best[0]:
                                best = (key, xx, yy, w, h)
                    if best and best[0][0] == 1:
                        break
                if best:
                    _, xx, yy, w, h = best
                    kk = len(self.placed)
                    occ[yy:yy + h, xx:xx + w] = kk + 1
                    self.placed.append((xx, yy, w, h, bd))
                    done = True
                else:
                    if k == ptr:
                        ptr += 1
                    k += 1
            lst_ptr[1] = ptr
            if not done:
                self.unplaced.append(bd)
        self.occ = occ
        return self

    # ---------------------------------------------------------- checks
    def checks(self):
        roads = self.street | self.alley
        # one network: 4-connected components of road plots (bridges count)
        net = roads | self.bridge
        lab = np.zeros(net.shape, np.int32)
        comps = 0
        for y, x in zip(*np.nonzero(net)):
            if lab[y, x]:
                continue
            comps += 1
            stack = [(y, x)]
            lab[y, x] = comps
            while stack:
                cy, cx = stack.pop()
                for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    ny, nx = cy + dy, cx + dx
                    if 0 <= ny < net.shape[0] and 0 <= nx < net.shape[1] and net[ny, nx] and not lab[ny, nx]:
                        lab[ny, nx] = comps
                        stack.append((ny, nx))
        # + junctions: a road plot with road on all four sides; the nearest other + along each street
        plus = []
        for y, x in zip(*np.nonzero(roads)):
            if 0 < y < net.shape[0] - 1 and 0 < x < net.shape[1] - 1 and roads[y - 1, x] and roads[y + 1, x] and roads[y, x - 1] and roads[y, x + 1]:
                plus.append((x, y))
        ps = set(plus)
        closest = 99
        for (x, y) in plus:
            for dx, dy in ((1, 0), (0, 1)):
                for k in range(1, 9):
                    if (x + dx * k, y + dy * k) in ps:
                        closest = min(closest, k)
                        break
        far = sum(1 for (bx, by, w, h, b) in self.placed if self.reach[by:by + h, bx:bx + w].min() > REACH)
        return dict(components=comps, plus=len(plus), closest_plus=closest, out_of_reach=far,
                    placed=len(self.placed), unplaced=len(self.unplaced))

    # ---------------------------------------------------------- the picture
    def render(self, px, crop=None, owned_tint=True, grid=False):
        x0, y0, x1, y1 = crop if crop else (0, 0, self.W, self.H)
        img = Image.new('RGB', ((x1 - x0) * px, (y1 - y0) * px))
        dr = ImageDraw.Draw(img)
        for y in range(y0, y1):
            for x in range(x0, x1):
                c = GROUND[self.T[y, x]]
                if not self.O[y, x]:
                    c = tuple(int(v * 0.72 + 30) for v in c)
                dr.rectangle([(x - x0) * px, (y - y0) * px, (x - x0 + 1) * px - 1, (y - y0 + 1) * px - 1], fill=c)
        # streets: the right of way as verge, the surface as a band, a track as a thin line
        def band(x, y, frac, colour, case=None, vertical=None):
            X, Y = (x - x0) * px, (y - y0) * px
            if frac <= 0:
                return
            hor = self._horizontal(x, y) if vertical is None else (not vertical)
            m = (1 - frac) * px / 2
            if hor:
                dr.rectangle([X, Y + m, X + px - 1, Y + px - 1 - m], fill=colour)
            else:
                dr.rectangle([X + m, Y, X + px - 1 - m, Y + px - 1], fill=colour)
        roads = self.street | self.alley
        for y in range(y0, y1):
            for x in range(x0, x1):
                if roads[y, x] or self.hw[y, x]:
                    X, Y = (x - x0) * px, (y - y0) * px
                    dr.rectangle([X, Y, X + px - 1, Y + px - 1], fill=VERGE)
        for y in range(y0, y1):
            for x in range(x0, x1):
                p = (y, x)
                if self.alley[y, x]:
                    band(x, y, W_MIN, GRAVEL_C if self.kind.get(p) != 'paved' else PAVED_C)
                    continue
                if self.street[y, x]:
                    k = self.kind.get(p, 'track')
                    if k == 'track':
                        self._track(dr, x, y, x0, y0, px)
                    else:
                        band(x, y, self.width.get(p, W_MIN), PAVED_C if k == 'paved' else GRAVEL_C)
                if self.bridge[y, x]:
                    band(x, y, 0.6, (0x5d, 0x4c, 0x3c))
                if self.hw[y, x]:
                    X, Y = (x - x0) * px, (y - y0) * px
                    dr.rectangle([X, Y, X + px - 1, Y + px - 1], fill=HIGHWAY_C)
        # junction fill: where two bands meet, square the corner
        for y in range(max(y0, 1), min(y1, self.H - 1)):
            for x in range(max(x0, 1), min(x1, self.W - 1)):
                if roads[y, x] and self.kind.get((y, x), 'track') != 'track':
                    hz = roads[y, x - 1] or roads[y, x + 1]
                    vt = roads[y - 1, x] or roads[y + 1, x]
                    if hz and vt:
                        w = self.width.get((y, x), W_MIN)
                        c = PAVED_C if self.kind.get((y, x)) == 'paved' else GRAVEL_C
                        band(x, y, w, c, vertical=True)
                        band(x, y, w, c, vertical=False)
        for (bx, by, w, h, b) in self.placed:
            if bx + w <= x0 or by + h <= y0 or bx >= x1 or by >= y1:
                continue
            fc = FLATS_FILL if b['flats'] else FILLC[b['cls']]
            ec = FLATS_EDGE if b['flats'] else EDGEC[b['cls']]
            X, Y = (bx - x0) * px, (by - y0) * px
            inset = 0 if px < 4 else (max(1, px // 6) if max(w, h) == 1 else max(1, px // 4))
            dr.rectangle([X + inset, Y + inset, X + w * px - 1 - inset, Y + h * px - 1 - inset],
                         fill=_rgb(fc), outline=_rgb(ec) if px >= 6 else None)
        return img

    def _horizontal(self, x, y):
        r = self.street | self.alley | self.hw
        return (x > 0 and r[y, x - 1]) or (x < self.W - 1 and r[y, x + 1])

    def _track(self, dr, x, y, x0, y0, px):
        X, Y = (x - x0) * px, (y - y0) * px
        t = max(1, px // 8)
        if self._horizontal(x, y):
            for k in range(0, px, max(2, px // 3)):
                dr.line([X + k, Y + px // 2, X + min(px - 1, k + px // 5), Y + px // 2], fill=TRACK_C, width=t)
        else:
            for k in range(0, px, max(2, px // 3)):
                dr.line([X + px // 2, Y + k, X + px // 2, Y + min(px - 1, k + px // 5)], fill=TRACK_C, width=t)


def _rgb(v):
    return ((v >> 16) & 255, (v >> 8) & 255, v & 255)


# --------------------------------------------------------------- ground
def noise(shape, scale, seed, octaves=4):
    rng = np.random.default_rng(seed)
    out = np.zeros(shape)
    amp, tot = 1.0, 0.0
    for o in range(octaves):
        s = max(2, int(scale / 2 ** o))
        g = rng.random((shape[0] // s + 3, shape[1] // s + 3))
        yy, xx = np.mgrid[0:shape[0], 0:shape[1]] / s
        y0, x0 = yy.astype(int), xx.astype(int)
        fy, fx = yy - y0, xx - x0
        fy, fx = fy * fy * (3 - 2 * fy), fx * fx * (3 - 2 * fx)
        a = g[y0, x0] * (1 - fx) + g[y0, x0 + 1] * fx
        b = g[y0 + 1, x0] * (1 - fx) + g[y0 + 1, x0 + 1] * fx
        out += amp * (a * (1 - fy) + b * fy)
        tot += amp
        amp *= 0.5
    return out / tot


def district_ground(n=256, seed=7):
    """A district: grass, some forest, a river two to four plots wide, a bay of sea in one corner."""
    T = np.zeros((n, n), np.int8)
    f = noise((n, n), 48, seed)
    T[f > 0.62] = FOREST
    yy, xx = np.mgrid[0:n, 0:n]
    # a river: a sine walk west to east through the south third
    ry = (n * 0.70 + 9 * np.sin(xx / 23.0) + 5 * np.sin(xx / 9.0 + 1)).astype(int)
    T[np.abs(yy - ry) <= 1 + (xx % 40 > 20)] = FRESH
    # the sea in the south-east corner
    coast = (xx - n) ** 2 + (yy - n) ** 2 < (n * 0.30 + 6 * noise((n, n), 16, seed + 1)) ** 2
    T[coast] = SALT
    near = (xx - n) ** 2 + (yy - n) ** 2 < (n * 0.30 + 2.5 + 6 * noise((n, n), 16, seed + 1)) ** 2
    T[near & ~coast] = SAND
    return T


def scaled_buildings(types, plots_room, fill=0.85, scale_counts=None):
    """Jerus's buildings scaled to a district: his mix, to `fill` of its dry ground (roads among the buildings)."""
    counts = JERUS_COUNTS if scale_counts is None else scale_counts
    total = sum(counts[i] * types[i]['cells'] for i in range(len(counts)) if i in types and counts[i])
    k = fill * plots_room / total
    out, roads = [], {'gravel': 0, 'paved': 0, 'highway': 0}
    for i, c in enumerate(counts):
        if i not in types or not c:
            continue
        t = types[i]
        n = c * k
        n = int(n) + (1 if h32('n', i) < n - int(n) else 0)
        if t['road']:
            roads[t['road']] += n * t['plots']
        elif t['line']:
            continue
        elif t['cat'] in ('MINING',):
            continue
        else:
            out.extend([t] * n)
    return out, roads, k


if __name__ == '__main__':
    types = load_types(sys.argv[1])
    T = district_ground()
    O = np.ones(T.shape, bool)
    O[:40, 200:] = False                      # a corner not yet bought
    dryroom = int((O & ((T == GRASS) | (T == FOREST) | (T == SAND))).sum())
    blds, roads, k = scaled_buildings(types, dryroom)
    print('district dry plots', dryroom, 'scale', round(k, 3), 'buildings', len(blds), 'road plots', {a: round(b) for a, b in roads.items()})
