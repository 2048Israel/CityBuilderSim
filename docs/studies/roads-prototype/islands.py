"""
islands.py - landmasses, the two-port rule and sea routes (spec-roads-and-ports.md, parts B and C). NOT GAME CODE.

  LANDMASSES: the city's dry ground cut by the sea into pieces, each labelled; the founding site's is HOME.
  LINKED:     home always; another landmass once it has a port and a linked landmass has one too - so the first
              island takes two ports, each one after it one more.
  ROUTES:     found on a coarse grid of the sea (16 plots, 480 m), one cell off the shore, then pulled straight.
              World trade sails from its berth to THE OFFING - out past the city's ground toward the most open sea -
              and on into the abyss; the city's own ports are joined to each other directly.
  BOATS:      a call is a schedule entry; a boat's place is a pure function of the clock, drawn only where it is
              on screen. A ferry route is a fleet shuttling, not calls.
"""
import math, sys, json, heapq
import numpy as np
from PIL import Image, ImageDraw
from roads_proto import noise, GROUND, GRASS, FOREST, FRESH, SALT, SAND

CG = 16                      # the route grid's cell, in plots (480 m)


def scene(W=1536, H=1024, seed=21):
    T = np.full((H, W), SALT, np.int8)
    yy, xx = np.mgrid[0:H, 0:W]
    n = noise((H, W), 140, seed)
    main = xx < 640 + (n - 0.5) * 300 + 60 * np.sin(yy / 90.0)
    isle_a = np.hypot((xx - 1010) / 1.25, yy - 430) < 120 + (noise((H, W), 50, seed + 1) - 0.5) * 70
    isle_b = np.hypot(xx - 1270, (yy - 790) / 1.1) < 78 + (noise((H, W), 40, seed + 2) - 0.5) * 50
    land = main | isle_a | isle_b
    T[land] = GRASS
    f = noise((H, W), 70, seed + 5)
    T[land & (f > 0.63)] = FOREST
    edge = land & ~(np.roll(land, 3, 0) & np.roll(land, -3, 0) & np.roll(land, 3, 1) & np.roll(land, -3, 1))
    T[edge] = SAND
    return T


def components(land):
    H, W = land.shape
    lab = np.zeros((H, W), np.int32)
    k = 0
    sizes = {}
    for y0, x0 in zip(*np.nonzero(land)):
        if lab[y0, x0]:
            continue
        k += 1
        st = [(y0, x0)]
        lab[y0, x0] = k
        n = 0
        while st:
            y, x = st.pop()
            n += 1
            for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                ny, nx = y + dy, x + dx
                if 0 <= ny < H and 0 <= nx < W and land[ny, nx] and not lab[ny, nx]:
                    lab[ny, nx] = k
                    st.append((ny, nx))
        sizes[k] = n
    return lab, sizes


def linked(landmasses, ports, home):
    """Home is linked; a landmass with a port is linked once some linked landmass has a port."""
    ok = {home}
    if not ports.get(home):
        return ok
    for m in landmasses:
        if m != home and ports.get(m):
            ok.add(m)
    return ok


def sea_grid(T):
    H, W = T.shape
    gh, gw = H // CG, W // CG
    sea = np.zeros((gh, gw), bool)
    for j in range(gh):
        for i in range(gw):
            blk = T[j * CG:(j + 1) * CG, i * CG:(i + 1) * CG]
            sea[j, i] = (blk == SALT).mean() > 0.95
    # one cell off the shore where the sea is wide enough
    shore = sea.copy()
    for dj in (-1, 0, 1):
        for di in (-1, 0, 1):
            shore &= np.roll(np.roll(sea, dj, 0), di, 1)
    return sea, shore


def astar(sea, shore, a, b):
    gh, gw = sea.shape
    def ok(c):
        j, i = c
        return 0 <= j < gh and 0 <= i < gw and sea[j, i]
    def cost(c):
        return 1.0 if shore[c] else 3.0       # hug the open water, not the shore
    openh = [(0, a)]
    g = {a: 0.0}
    prev = {a: None}
    while openh:
        _, c = heapq.heappop(openh)
        if c == b:
            break
        for dj, di in ((1, 0), (-1, 0), (0, 1), (0, -1), (1, 1), (1, -1), (-1, 1), (-1, -1)):
            n = (c[0] + dj, c[1] + di)
            if not ok(n):
                continue
            step = (1.4142 if dj and di else 1.0) * cost(n)
            ng = g[c] + step
            if ng < g.get(n, 1e18):
                g[n] = ng
                prev[n] = c
                heapq.heappush(openh, (ng + math.hypot(n[0] - b[0], n[1] - b[1]), n))
    if b not in prev:
        return None
    path = []
    c = b
    while c is not None:
        path.append(c)
        c = prev[c]
    return path[::-1]


def pull(path, sea):
    """String-pulling: keep a point only where the straight line to the next kept one would cross land."""
    def clear(p, q):
        n = int(max(abs(p[0] - q[0]), abs(p[1] - q[1])) * 3) + 1
        for k in range(n + 1):
            t = k / n
            j, i = p[0] + (q[0] - p[0]) * t, p[1] + (q[1] - p[1]) * t
            if not sea[int(round(j)), int(round(i))]:
                return False
        return True
    out = [path[0]]
    k = 0
    while k < len(path) - 1:
        m = len(path) - 1
        while m > k + 1 and not clear(path[k], path[m]):
            m -= 1
        out.append(path[m])
        k = m
    return out


def offing(sea, port_cell, radius_cells):
    """The point a world route leaves the city by: the bearing with the longest run of open sea from the port."""
    best = None
    for deg in range(0, 360, 5):
        r = math.radians(deg)
        run = 0
        for s in range(1, 400):
            j, i = int(round(port_cell[0] + math.sin(r) * s)), int(round(port_cell[1] + math.cos(r) * s))
            if not (0 <= j < sea.shape[0] and 0 <= i < sea.shape[1]):
                run = s + 1000        # it leaves the map at sea: open water
                break
            if not sea[j, i]:
                break
            run = s
        if best is None or run > best[0]:
            best = (run, deg)
    deg = best[1]
    r = math.radians(deg)
    j = int(round(port_cell[0] + math.sin(r) * radius_cells))
    i = int(round(port_cell[1] + math.cos(r) * radius_cells))
    j = min(max(j, 0), sea.shape[0] - 1)
    i = min(max(i, 0), sea.shape[1] - 1)
    return (j, i), deg


if __name__ == '__main__':
    out = sys.argv[1]
    T = scene()
    H, W = T.shape
    land = T != SALT
    lab, sizes = components(land)
    yy, xx = np.mgrid[0:H, 0:W]
    S = (420, 470)
    home = lab[S[1], S[0]]
    # the city's ground: the mainland round the site, island A and part of island B, and the sea between them
    n = noise((H, W), 100, 9)
    owned = np.hypot(xx - S[0], yy - S[1]) < 330 + (n - 0.5) * 120
    owned |= (lab == lab[430, 1010])
    owned |= (lab == lab[790, 1270]) & (xx < 1290)
    owned |= (T == SALT) & (np.hypot(xx - 820, yy - 450) < 260)
    masses = {}
    for m in np.unique(lab[owned & land]):
        if m == 0:
            continue
        masses[int(m)] = dict(dry_plots=int((owned & (lab == m)).sum()))
    names = {int(home): 'Home', int(lab[430, 1010]): 'Isle A', int(lab[790, 1270]): 'Isle B'}
    sea, shore = sea_grid(T)
    # ports: home's on its east shore by the site's row, Isle A's on its west shore
    ports = {int(home): [('home', (466 // CG, 690 // CG))], int(lab[430, 1010]): [('isleA', (432 // CG, 880 // CG))]}
    L = linked(masses, ports, int(home))
    for m in masses:
        masses[m]['name'] = names.get(m, f'#{m}')
        masses[m]['ports'] = len(ports.get(m, []))
        masses[m]['linked'] = m in L
        masses[m]['km2'] = round(masses[m]['dry_plots'] * 0.0009, 2)
    # nearest sea cell to each port's quay
    def nearest_sea(c):
        best = None
        for j in range(max(0, c[0] - 6), min(sea.shape[0], c[0] + 7)):
            for i in range(max(0, c[1] - 6), min(sea.shape[1], c[1] + 7)):
                if sea[j, i]:
                    d = (j - c[0]) ** 2 + (i - c[1]) ** 2
                    if best is None or d < best[0]:
                        best = (d, (j, i))
        return best[1]
    pa = nearest_sea(ports[int(home)][0][1])
    pb = nearest_sea(ports[int(lab[430, 1010])][0][1])
    ferry = pull(astar(sea, shore, pa, pb), sea)
    off, bearing = offing(sea, pa, radius_cells=int((330 + 330) / CG))
    world = pull(astar(sea, shore, pa, off), sea)
    res = dict(landmasses=masses, ferry_legs=len(ferry) - 1, world_legs=len(world) - 1, offing_bearing_deg=bearing,
               ferry_km=round(sum(math.hypot(ferry[k + 1][0] - ferry[k][0], ferry[k + 1][1] - ferry[k][1]) for k in range(len(ferry) - 1)) * CG * 0.03, 1),
               world_km_to_offing=round(sum(math.hypot(world[k + 1][0] - world[k][0], world[k + 1][1] - world[k][1]) for k in range(len(world) - 1)) * CG * 0.03, 1))
    print(json.dumps(res, indent=1))
    json.dump(dict(res=res, ferry=ferry, world=world, ports={str(k): v for k, v in ports.items()}, S=S), open(f'{out}/islands.json', 'w'))
    np.save(f'{out}/islands-T.npy', T)
    np.save(f'{out}/islands-owned.npy', owned)
    np.save(f'{out}/islands-lab.npy', lab)
