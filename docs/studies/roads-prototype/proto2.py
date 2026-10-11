"""
proto2.py - the district plan of spec-roads-and-ports.md, second version: ARTERIAL CELLS.

NOT GAME CODE. The plan works in cells: the 31 x 31 plots between arterials (960 m).
Each cell the city opens gets a layout by what it holds:
  HOMES cell  (what follows people): long blocks of 15 x 7 plots - streets every 8 plots one
              way, every 16 the other - two blocks merged across a street for a building wider
              than a block (a hospital, a university).
  ESTATE cell (industry, and the outer kinds): one spine street, two strips of 15 x 31 either
              side, big buildings packed along them side by side, each touching a street.
Cells open in growth order from the hub, so the streets are one network; buildings go only
where a street reaches them; the model's road plots are drawn as the streets' surface.
"""
import sys, math, json, heapq
from collections import deque
import numpy as np
from PIL import Image, ImageDraw
from roads_proto import (load_types, noise, district_ground, scaled_buildings, h32, GROUND, FILLC, EDGEC,
                         FLATS_FILL, FLATS_EDGE, GRAVEL_C, PAVED_C, HIGHWAY_C, CENTRE, VERGE, TRACK_C,
                         GRASS, FOREST, FRESH, SALT, SAND, _rgb, JERUS_COUNTS)

OFF, ART, CELL = 4, 32, 31        # arterial lines at 4 mod 32; a cell's interior is 31 plots
REACH = 4
W_MIN, W_FULL = 0.5, 1.0
BRIDGE_STREET, BRIDGE_ART = 6, 14  # TilePainter.STREET_BRIDGE, MAX_BRIDGE for highways (arterials take it)


class Plan2:
    def __init__(self, T, O, hub, seed=4127):
        self.T, self.O, self.hub, self.seed = T, O, hub, seed
        self.H, self.W = T.shape
        self.dry = O & ((T == GRASS) | (T == FOREST) | (T == SAND))
        self.hw = np.zeros(T.shape, bool)
        self.ncx = (self.W - OFF - 1) // ART
        self.ncy = (self.H - OFF - 1) // ART

    def lay_highway(self, plots):
        for (x, y) in plots:
            if 0 <= x < self.W and 0 <= y < self.H:
                self.hw[y, x] = True

    def cell_box(self, ci, cj):
        x0, y0 = OFF + ART * ci + 1, OFF + ART * cj + 1
        return x0, y0, x0 + CELL, y0 + CELL

    def cell_room(self, ci, cj):
        x0, y0, x1, y1 = self.cell_box(ci, cj)
        return int((self.dry[y0:y1, x0:x1] & ~self.hw[y0:y1, x0:x1]).sum())

    # -------------------------------------------------------------------- the plan
    def build(self, buildings, budget, n_square=0, n_blvd=0):
        H, W = self.H, self.W
        hx, hy = self.hub
        usable = {(ci, cj): self.cell_room(ci, cj) >= 120 for cj in range(self.ncy) for ci in range(self.ncx)}

        def dist(c):
            x0, y0, x1, y1 = self.cell_box(*c)
            return math.hypot((x0 + x1) / 2 - hx, (y0 + y1) / 2 - hy) / ART + 0.6 * h32(self.seed, 'cell', c)
        # growth order: always beside a cell already taken
        start = min((c for c in usable if usable[c]), key=dist)
        order, seen, heap = [], {start}, [(dist(start), start)]
        while heap:
            _, c = heapq.heappop(heap)
            order.append(c)
            for d in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (c[0] + d[0], c[1] + d[1])
                if n in usable and usable[n] and n not in seen:
                    seen.add(n)
                    heapq.heappush(heap, (dist(n), n))
        self.order = order

        occ = np.zeros((H, W), np.int32)   # 0 free, -1 street or not ours, -2 kept free (a merged block's old street is ground)
        occ[~self.dry] = -1
        occ[self.hw] = -1
        street = np.zeros((H, W), bool)
        art = np.zeros((H, W), bool)
        self.cells = {}                    # cell -> dict(kind, dir, rank)
        self.placed, self.unplaced = [], []

        def open_cell(c, kind):
            ci, cj = c
            x0, y0, x1, y1 = self.cell_box(ci, cj)
            d = 'H' if h32(self.seed, 'dir', c) < 0.5 else 'V'
            rank = len(self.cells)
            homes_rank = sum(1 for v in self.cells.values() if v['kind'] == 'homes')
            square = kind == 'homes' and homes_rank < n_square
            blvd = rank < n_blvd
            self.cells[c] = dict(kind=kind, dir=d, rank=rank, square=square, blvd=blvd)
            # its arterials: the ring of plots round its interior - two plots deep on a boulevard
            for ring in ((0, 1) if blvd else (0,)):
                for x in range(x0 - 1 + ring, x1 + 1 - ring):
                    for y in (y0 - 1 + ring, y1 - ring):
                        self._street(street, art, x, y, True)
                for y in range(y0 - 1 + ring, y1 + 1 - ring):
                    for x in (x0 - 1 + ring, x1 - ring):
                        self._street(street, art, x, y, True)
            # its streets
            if square:
                for v in (7, 15, 23):
                    for u in range(CELL):
                        self._street(street, art, x0 + u, y0 + v, False)
                        self._street(street, art, x0 + v, y0 + u, False)
            elif kind == 'homes':
                if d == 'H':
                    for v in (7, 15, 23):
                        for u in range(CELL):
                            self._street(street, art, x0 + u, y0 + v, False)
                    for v in range(CELL):
                        self._street(street, art, x0 + 15, y0 + v, False)
                else:
                    for u in (7, 15, 23):
                        for v in range(CELL):
                            self._street(street, art, x0 + u, y0 + v, False)
                    for u in range(CELL):
                        self._street(street, art, x0 + u, y0 + 15, False)
            else:
                if d == 'H':
                    for v in range(CELL):
                        self._street(street, art, x0 + 15, y0 + v, False)
                else:
                    for u in range(CELL):
                        self._street(street, art, x0 + u, y0 + 15, False)
            occ[street] = -1

        def band(bd):
            if bd['outer']:
                return 2
            return 1 if bd['cls'] == 3 else 0

        reach_cache = {}

        def reach_map():
            d = np.full((H, W), 99, np.int16)
            cur = street.copy()
            d[cur] = 0
            for k in range(1, REACH + 1):
                n = cur.copy()
                n[1:, :] |= cur[:-1, :]; n[:-1, :] |= cur[1:, :]
                n[:, 1:] |= cur[:, :-1]; n[:, :-1] |= cur[:, 1:]
                n[1:, 1:] |= cur[:-1, :-1]; n[:-1, :-1] |= cur[1:, 1:]
                n[1:, :-1] |= cur[:-1, 1:]; n[:-1, 1:] |= cur[1:, :-1]
                d[n & (d == 99)] = k
                cur = n
            return d

        def try_place(c, bd, reach):
            """The first spot in cell c where bd fits on free ground within reach of a street - touching one first,
            then in rows from the cell's north-west (vectorised with box sums)."""
            x0, y0, x1, y1 = self.cell_box(*c)
            free = (occ[y0:y1, x0:x1] == 0).astype(np.int32)
            near = (reach[y0:y1, x0:x1] <= REACH).astype(np.int32)
            touch = (reach[y0:y1, x0:x1] == 1).astype(np.int32)
            def boxsum(a, w, h):
                I = np.zeros((a.shape[0] + 1, a.shape[1] + 1), np.int32)
                I[1:, 1:] = a.cumsum(0).cumsum(1)
                return I[h:, w:] - I[:-h, w:] - I[h:, :-w] + I[:-h, :-w]
            best = None
            for (w, h) in ((bd['w'], bd['h']), (bd['h'], bd['w'])):
                if w > CELL or h > CELL:
                    continue
                fits = boxsum(free, w, h) == w * h
                if not fits.any():
                    continue
                t = fits & (boxsum(touch, w, h) > 0)
                cand = t if t.any() else fits & (boxsum(near, w, h) > 0)
                if not cand.any():
                    continue
                ys, xs = np.nonzero(cand)
                k = np.lexsort((xs, ys))[0]
                r = 1 if t.any() else 2
                key = (r, int(ys[k]), int(xs[k]))
                if best is None or key < best[0]:
                    best = (key, x0 + int(xs[k]), y0 + int(ys[k]), w, h)
            return best

        def merge_for(c, bd):
            """In a homes cell: free the street segments inside the smallest group of long blocks that holds bd."""
            x0, y0, x1, y1 = self.cell_box(*c)
            d = self.cells[c]['dir']
            # groups of blocks: (across, along) in plots, aligned to the block lines, in the cell's own axes
            groups = [(15, 15), (15, 23), (15, 31), (31, 15), (31, 31)]
            for (gu, gv) in groups:
                for (w, h) in ((bd['w'], bd['h']), (bd['h'], bd['w'])):
                    if w > (gu if d == 'H' else gv) or h > (gv if d == 'H' else gu):
                        continue
                    us = [0, 16] if gu == 15 else [0]
                    vs = [v for v in (0, 8, 16, 24) if v + gv <= CELL]
                    for u in us:
                        for v in vs:
                            gx, gy, gw, gh = (x0 + u, y0 + v, gu, gv) if d == 'H' else (x0 + v, y0 + u, gv, gu)
                            reg = occ[gy:gy + gh, gx:gx + gw]
                            if (reg > 0).any():
                                continue
                            inner = street[gy:gy + gh, gx:gx + gw]
                            # the group's own ground, its inner streets returned to ground
                            ok = (self.dry[gy:gy + gh, gx:gx + gw] | inner).mean() > 0.95
                            if not ok:
                                continue
                            street[gy:gy + gh, gx:gx + gw] = False
                            sub = occ[gy:gy + gh, gx:gx + gw]
                            sub[(sub == -1) & self.dry[gy:gy + gh, gx:gx + gw] & ~self.hw[gy:gy + gh, gx:gx + gw]] = 0
                            return True
            return False

        bands = [[], [], []]
        for bd in buildings:
            bands[band(bd)].append(bd)
        k_next = 0
        self.band_cells = []
        for g in range(3):
            items = sorted(bands[g], key=lambda bd: (-bd['w'] * bd['h'], bd['id']))
            if g == 0 and items:
                items = [dict(bd) for bd in items]
                # MIXED: each type dealt round the band's cells in turn (a hashed start a type), so a cell holds
                # homes, shops, a school and a clinic, not a cell of each; placed cell by cell, largest first.
                n0 = max(1, int(math.ceil(sum(bd['w'] * bd['h'] for bd in items) / 640.0)))
                slot, start = {}, {}
                for bd in items:
                    t = bd['id']
                    if t not in start:
                        start[t] = int(h32(self.seed, 'deal', t) * n0)
                        slot[t] = 0
                    bd_slot = (start[t] + slot[t]) % n0
                    slot[t] += 1
                    bd['_slot'] = bd_slot
                items = sorted(items, key=lambda bd: (bd['_slot'], -bd['w'] * bd['h'], bd['id']))
            mine = []
            reach = reach_map()
            for bd in items:
                done = False
                for c in mine:
                    best = try_place(c, bd, reach)
                    if best is None and g == 0 and max(bd['w'], bd['h']) > 7:
                        if merge_for(c, bd):
                            reach = reach_map()
                            best = try_place(c, bd, reach)
                    if best:
                        _, xx, yy, w, h = best
                        occ[yy:yy + h, xx:xx + w] = len(self.placed) + 1
                        self.placed.append((xx, yy, w, h, bd))
                        done = True
                        break
                while not done and k_next < len(order):
                    c = order[k_next]
                    k_next += 1
                    if c in self.cells:
                        continue
                    open_cell(c, 'homes' if g == 0 else 'estate')
                    mine.append(c)
                    reach = reach_map()
                    best = try_place(c, bd, reach)
                    if best is None and g == 0 and max(bd['w'], bd['h']) > 7 and merge_for(c, bd):
                        reach = reach_map()
                        best = try_place(c, bd, reach)
                    if best:
                        _, xx, yy, w, h = best
                        occ[yy:yy + h, xx:xx + w] = len(self.placed) + 1
                        self.placed.append((xx, yy, w, h, bd))
                        done = True
                if not done:
                    self.unplaced.append(bd)
            self.band_cells.append(list(mine))

        # one network, and the plots a bridge carries over fresh water
        street = self._join(street)
        self.bridge = street & (self.T == FRESH)
        self.street = street & ~self.hw
        self.art = art & self.street
        self.occ = occ
        self.reach = reach_map()

        # ------------------------------------------------------------ the surface: the model's road plots
        cell_rank = {c: v['rank'] for c, v in self.cells.items()}
        def plot_rank(p):
            y, x = p
            ci, cj = (x - OFF - 1) // ART, (y - OFF - 1) // ART
            return cell_rank.get((ci, cj), 10 ** 6)
        plots = sorted(zip(*np.nonzero(self.street)), key=lambda p: (0 if self.art[p] else 1, plot_rank(p), p))
        B = budget.get('paved', 0) + budget.get('gravel', 0)
        left, width = B, {}
        for p in plots:
            if left >= W_MIN:
                width[p] = W_MIN; left -= W_MIN
            else:
                width[p] = 0.0
        for p in plots:
            if width[p] == W_MIN and left >= W_FULL - W_MIN:
                width[p] = W_FULL; left -= W_FULL - W_MIN
        kind, pv = {}, budget.get('paved', 0)
        for p in plots:
            if width[p] > 0 and pv >= width[p]:
                kind[p] = 'paved'; pv -= width[p]
            elif width[p] > 0:
                kind[p] = 'gravel'
            else:
                kind[p] = 'track'
        self.width, self.kind, self.surplus, self.B = width, kind, left, B
        return self

    def _street(self, street, art, x, y, arterial):
        if 0 <= x < self.W and 0 <= y < self.H and self.O[y, x] and self.T[y, x] != SALT and not self.hw[y, x]:
            street[y, x] = True
            if arterial:
                art[y, x] = True

    def _join(self, street):
        H, W = street.shape
        lat = np.zeros((H, W), bool)
        lat[(np.arange(H) - OFF) % 8 == 0, :] = True
        lat[:, (np.arange(W) - OFF) % 8 == 0] = True
        ok = lat & self.O & (self.T != SALT) & ~self.hw
        self.joins = 0
        for _ in range(40):
            lab = -np.ones((H, W), np.int32); sizes = []
            for y, x in zip(*np.nonzero(street)):
                if lab[y, x] >= 0:
                    continue
                c = len(sizes); n = 0; st = [(y, x)]; lab[y, x] = c
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
            prev, dq, hit = {}, deque(), None
            for y, x in zip(*np.nonzero(lab == main)):
                dq.append((y, x)); prev[(y, x)] = None
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
                street[p] = True; p = prev[p]
            self.joins += 1
        return street

    # -------------------------------------------------------------------- checks
    def checks(self):
        roads = self.street | self.bridge
        H, W = roads.shape
        lab = np.zeros((H, W), np.int32); comps = 0
        for y, x in zip(*np.nonzero(roads)):
            if lab[y, x]:
                continue
            comps += 1; st = [(y, x)]; lab[y, x] = comps
            while st:
                cy, cx = st.pop()
                for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    ny, nx = cy + dy, cx + dx
                    if 0 <= ny < H and 0 <= nx < W and roads[ny, nx] and not lab[ny, nx]:
                        lab[ny, nx] = comps; st.append((ny, nx))
        lat = np.zeros((H, W), bool)
        lat[(np.arange(H) - OFF) % 8 == 0, :] = True
        lat[:, (np.arange(W) - OFF) % 8 == 0] = True
        r = self.street & lat          # the streets' centre lines (a boulevard's second row is not a junction)
        plus = set()
        for y, x in zip(*np.nonzero(r)):
            if 0 < y < H - 1 and 0 < x < W - 1 and r[y - 1, x] and r[y + 1, x] and r[y, x - 1] and r[y, x + 1]:
                plus.add((x, y))
        closest = 99
        for (x, y) in plus:
            for dx, dy in ((1, 0), (0, 1)):
                for k in range(1, 9):
                    if (x + dx * k, y + dy * k) in plus:
                        closest = min(closest, k); break
        far = sum(1 for (bx, by, w, h, b) in self.placed if self.reach[by:by + h, bx:bx + w].min() > REACH)
        touching = sum(1 for (bx, by, w, h, b) in self.placed if self.reach[by:by + h, bx:bx + w].min() == 1)
        used = sum(w * h for (_, _, w, h, _) in self.placed)
        row = int(self.street.sum())
        w = np.array(list(self.width.values())) if self.width else np.zeros(1)
        return dict(components=comps, plus=len(plus), closest_plus=closest, out_of_reach=far,
                    touching=touching, placed=len(self.placed), unplaced=len(self.unplaced),
                    cells=len(self.cells), homes_cells=sum(1 for v in self.cells.values() if v['kind'] == 'homes'),
                    building_plots=used, street_plots=row, budget=round(self.B), surplus=round(self.surplus),
                    tracks=int((w == 0).sum()), narrow=int((w == W_MIN).sum()), full=int((w == W_FULL).sum()),
                    street_share=round(row / max(1, row + used), 3), joins=self.joins)

    # -------------------------------------------------------------------- the picture
    def render(self, px, crop=None, tint_unowned=True, labels=None):
        x0, y0, x1, y1 = crop if crop else (0, 0, self.W, self.H)
        img = Image.new('RGB', ((x1 - x0) * px, (y1 - y0) * px))
        dr = ImageDraw.Draw(img)
        t = self.T[y0:y1, x0:x1].copy()
        t[(t == FOREST) & (self.occ[y0:y1, x0:x1] > 0)] = GRASS
        g = np.array(GROUND, np.uint8)[t]
        if tint_unowned:
            m = ~self.O[y0:y1, x0:x1]
            g[m] = (g[m] * 0.7 + 28).astype(np.uint8)
        ground = Image.fromarray(g, 'RGB').resize(((x1 - x0) * px, (y1 - y0) * px), Image.NEAREST)
        img.paste(ground)
        allr = self.street | self.hw | self.bridge
        def hor(x, y):
            return (x > 0 and allr[y, x - 1]) or (x < self.W - 1 and allr[y, x + 1])
        def ver(x, y):
            return (y > 0 and allr[y - 1, x]) or (y < self.H - 1 and allr[y + 1, x])
        def band(x, y, frac, colour, horizontal):
            X, Y = (x - x0) * px, (y - y0) * px
            m = (1 - frac) * px / 2
            if horizontal:
                dr.rectangle([X, Y + m, X + px - 1, Y + px - 1 - m], fill=colour)
            else:
                dr.rectangle([X + m, Y, X + px - 1 - m, Y + px - 1], fill=colour)
        ys, xs = np.nonzero(allr[y0:y1, x0:x1])
        for yy, xx in zip(ys + y0, xs + x0):
            X, Y = (xx - x0) * px, (yy - y0) * px
            if self.street[yy, xx]:
                dr.rectangle([X, Y, X + px - 1, Y + px - 1], fill=VERGE)
        for yy, xx in zip(ys + y0, xs + x0):
            p = (yy, xx)
            if self.hw[yy, xx]:
                continue
            k = self.kind.get(p, 'track')
            if self.bridge[yy, xx]:
                k = self.kind.get(p, 'paved') if self.kind.get(p) != 'track' else 'paved'
            h, v = hor(xx, yy), ver(xx, yy)
            if k == 'track':
                t = max(2, px // 5)
                X, Y = (xx - x0) * px, (yy - y0) * px
                if h:
                    for s in range(0, px, max(2, px // 2)):
                        dr.line([X + s, Y + px // 2, X + min(px - 1, s + max(2, px // 3)), Y + px // 2], fill=(0x8a, 0x6a, 0x3c), width=t)
                if v:
                    for s in range(0, px, max(2, px // 2)):
                        dr.line([X + px // 2, Y + s, X + px // 2, Y + min(px - 1, s + max(2, px // 3))], fill=(0x8a, 0x6a, 0x3c), width=t)
                continue
            c = PAVED_C if k == 'paved' else GRAVEL_C
            if self.bridge[yy, xx]:
                c = (0x5d, 0x4c, 0x3c)
            wdt = self.width.get(p, W_MIN) or W_MIN
            if h:
                band(xx, yy, wdt, c, True)
            if v:
                band(xx, yy, wdt, c, False)
            if not h and not v:
                band(xx, yy, wdt, c, True)
        # highways: their own dark band, a dashed centre line
        for yy, xx in zip(*np.nonzero(self.hw[y0:y1, x0:x1])):
            yy += y0; xx += x0
            X, Y = (xx - x0) * px, (yy - y0) * px
            dr.rectangle([X, Y, X + px - 1, Y + px - 1], fill=HIGHWAY_C)
            if px >= 6:
                hh = (xx > 0 and self.hw[yy, xx - 1]) or (xx < self.W - 1 and self.hw[yy, xx + 1])
                if hh and xx % 2 == 0:
                    dr.line([X + 1, Y + px // 2, X + px - 2, Y + px // 2], fill=CENTRE, width=max(1, px // 10))
                elif not hh and yy % 2 == 0:
                    dr.line([X + px // 2, Y + 1, X + px // 2, Y + px - 2], fill=CENTRE, width=max(1, px // 10))
        for (bx, by, w, h, b) in self.placed:
            if bx + w <= x0 or by + h <= y0 or bx >= x1 or by >= y1:
                continue
            fc = FLATS_FILL if b['flats'] else FILLC[b['cls']]
            ec = FLATS_EDGE if b['flats'] else EDGEC[b['cls']]
            X, Y = (bx - x0) * px, (by - y0) * px
            inset = 0 if px < 4 else (max(1, px // 6) if max(w, h) == 1 else max(1, px // 5))
            dr.rectangle([X + inset, Y + inset, X + w * px - 1 - inset, Y + h * px - 1 - inset],
                         fill=_rgb(fc), outline=_rgb(ec) if px >= 6 else None)
        return img


def plan_ladder(T, O, hub, blds, budget, seed=4127):
    """The surface ladder: a first plan with long blocks; the road it leaves over squares the homes cells nearest
    the hub (the + grid at 8), then makes boulevards of the arterials, nearest first; a city short of road keeps
    long blocks and draws the outer streets as tracks."""
    p = Plan2(T, O, hub, seed).build(blds, budget)
    S = p.surplus
    if S <= 0:
        return p, 0, 0
    homes = sum(1 for v in p.cells.values() if v['kind'] == 'homes')
    n_sq = min(homes, int(S // 56))
    S2 = S - 56 * n_sq
    n_bl = int(S2 // 100)
    while True:
        p2 = Plan2(T, O, hub, seed).build(blds, budget, n_square=n_sq, n_blvd=n_bl)
        # never at a building's cost: a step of the ladder that leaves a building out is not taken
        if len(p2.unplaced) <= len(p.unplaced) or (n_bl == 0 and n_sq == 0):
            return p2, n_sq, n_bl
        if n_bl > 0:
            n_bl = n_bl // 2 if n_bl > 1 else 0
        else:
            n_sq = n_sq // 2


def ground_for(n, seed=7):
    T = district_ground(n, seed)
    return T


if __name__ == '__main__':
    import os, time
    types = load_types(sys.argv[1])
    out = sys.argv[2]
    os.makedirs(out, exist_ok=True)
    n = OFF + ART * 8 + 4
    T = ground_for(n)
    O = np.ones(T.shape, bool)
    O[:44, 200:] = False
    dry = int((O & ((T == GRASS) | (T == FOREST) | (T == SAND))).sum())
    res = {}
    for fill in [float(f) for f in os.environ.get('FILLS', '0.85').split(',')]:
        blds, roads, k = scaled_buildings(types, dry, fill=fill)
        cap = roads['gravel'] / types[29]['plots'] * 900 + roads['paved'] / types[13]['plots'] * 1200
        paved_all = cap / 1200 * types[13]['plots']
        for name, budget in (('jerus', {'gravel': roads['gravel'], 'paved': roads['paved']}),
                             ('paved', {'paved': paved_all}), ('thin', {'paved': paved_all * 0.5})):
            t0 = time.time()
            p, n_sq, n_bl = plan_ladder(T, O, (70, 90), blds, budget)
            c = p.checks()
            c['square_cells'], c['boulevard_cells'] = n_sq, n_bl
            c['ms'] = round((time.time() - t0) * 1000)
            c['fill'] = fill
            c['buildings'] = len(blds)
            c['drawn_share_of_dry'] = round((c['building_plots'] + c['street_plots']) / dry, 3)
            c['model_share_of_dry'] = round((sum(b['w'] * b['h'] for b in blds) + sum(budget.values())) / dry, 3)
            res[f'{name}@{fill}'] = c
            print(name, fill, json.dumps(c))
            if os.environ.get('DRAW'):
                p.render(2).save(f'{out}/d2-{name}-{fill}.png')
                p.render(12, crop=(36, 36, 36 + 64, 36 + 40)).save(f'{out}/c2-{name}-{fill}.png')
    json.dump(res, open(f'{out}/results.json', 'w'), indent=1)
