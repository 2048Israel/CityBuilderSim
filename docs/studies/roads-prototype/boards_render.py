"""Draws every picture the study's boards use, from the prototypes, into one folder, with the figures beside them."""
import sys, json, math, os
import numpy as np
from PIL import Image, ImageDraw, ImageFont
from roads_proto import load_types, scaled_buildings, GRASS, FOREST, FRESH, SALT, SAND, GROUND, HIGHWAY_C, CENTRE, h32
from proto2 import Plan2, plan_ladder, ground_for, OFF, ART, CELL

types = load_types(sys.argv[1])
out = sys.argv[2]
os.makedirs(out, exist_ok=True)
facts = {}

# ------------------------------------------------------------------ the district, three road budgets
n = OFF + ART * 8 + 4
T = ground_for(n)
O = np.ones(T.shape, bool)
O[:44, 200:] = False
dry = int((O & ((T == GRASS) | (T == FOREST) | (T == SAND))).sum())
hub = (70, 90)
blds, roads, k = scaled_buildings(types, dry, fill=0.7)
cap = roads['gravel'] / types[29]['plots'] * 900 + roads['paved'] / types[13]['plots'] * 1200
paved_all = cap / 1200 * types[13]['plots']
plans = {}
for name, budget in (('jerus', {'gravel': roads['gravel'], 'paved': roads['paved']}),
                     ('paved', {'paved': paved_all}), ('thin', {'paved': paved_all * 0.5})):
    p, sq, bl = plan_ladder(T, O, hub, blds, budget)
    c = p.checks()
    c.update(square_cells=sq, boulevard_cells=bl, budget_detail={a: round(b) for a, b in budget.items()})
    facts['district_' + name] = c
    plans[name] = p
facts['district'] = dict(dry_plots=dry, dry_km2=round(dry * 0.0009, 2), buildings=len(blds),
                         jerus_scale=round(k, 4), hub=hub)

pj = plans['jerus']
pj.render(2).save(f'{out}/district-jerus.png')
pair = None
for c, v in sorted(pj.cells.items(), key=lambda kv: kv[1]['rank']):
    r = (c[0] + 1, c[1])
    if v['kind'] == 'estate' and r in pj.cells and pj.cells[r]['kind'] == 'homes':
        pair = (c, r); break
    l = (c[0] - 1, c[1])
    if v['kind'] == 'homes' and l in pj.cells and pj.cells[l]['kind'] == 'estate':
        pair = (l, c); break
x0, y0, _, _ = pj.cell_box(*pair[0])
pj.render(19, crop=(x0 + 0, y0 + 2, x0 + 66, y0 + 34)).save(f'{out}/after-tilepair.png')
facts['tilepair'] = pair
def find_pair(p):
    for c, v in sorted(p.cells.items(), key=lambda kv: kv[1]['rank']):
        r = (c[0] + 1, c[1])
        if v['kind'] == 'estate' and r in p.cells and p.cells[r]['kind'] == 'homes':
            return (c, r)
        l = (c[0] - 1, c[1])
        if v['kind'] == 'homes' and l in p.cells and p.cells[l]['kind'] == 'estate':
            return (l, c)
pp_ = plans['paved']
pr = find_pair(pp_)
x0, y0, _, _ = pp_.cell_box(*pr[0])
pp_.render(19, crop=(x0 + 0, y0 + 2, x0 + 66, y0 + 34)).save(f'{out}/after-tilepair-paved.png')
facts['tilepair_paved'] = pr
plans['thin'].render(2).save(f'{out}/district-thin.png')

# a homes cell and an estate cell, close
def cell_crop(p, c, pad=3):
    x0, y0, x1, y1 = p.cell_box(*c)
    return (x0 - 1 - pad, y0 - 1 - pad, x1 + 1 + pad, y1 + 1 + pad)
homes = [c for c, v in pj.cells.items() if v['kind'] == 'homes']
estates = [c for c, v in pj.cells.items() if v['kind'] == 'estate']
hc = min(homes, key=lambda c: pj.cells[c]['rank'])
# a long-block homes cell: in the paved plan (no square blocks)
pp = plans['paved']
hl = [c for c, v in pp.cells.items() if v['kind'] == 'homes' and not v['square']]
hl = sorted(hl, key=lambda c: pp.cells[c]['rank'])[1]
ec = sorted(estates, key=lambda c: pj.cells[c]['rank'])[2]
pj.render(12, crop=cell_crop(pj, hc)).save(f'{out}/cell-square.png')
pp.render(12, crop=cell_crop(pp, hl)).save(f'{out}/cell-long.png')
pj.render(12, crop=cell_crop(pj, ec)).save(f'{out}/cell-estate.png')
facts['cells_drawn'] = dict(square=hc, long=hl, estate=ec,
                            estate_dir=pj.cells[ec]['dir'], long_dir=pp.cells[hl]['dir'])

# three budgets, one crop: from the core out to the first cell with tracks in the thin plan
pt = plans['thin']
tracks = [(y, x) for (y, x), kd in pt.kind.items() if kd == 'track']
ty, tx = min(tracks, key=lambda p: math.hypot(p[1] - hub[0], p[0] - hub[1]))
cx0 = max(0, min(tx, hub[0]) - 20)
cy0 = max(0, min(ty, hub[1]) - 14)
crop = (cx0, cy0, cx0 + 72, cy0 + 48)
for name in ('jerus', 'paved', 'thin'):
    plans[name].render(8, crop=crop).save(f'{out}/budget-{name}.png')
facts['budget_crop'] = crop

# ------------------------------------------------------------------ highways
from hw import world, owned_blob, Highways, render as hw_render
Tw = world()
S = (900, 760)
hwy = Highways(Tw, S)
stages = [(4, 39, 'jerus'), (12, 600, 'b'), (22, 2600, 'c'), (34, 9000, 'd')]
done = 0
hwf = []
for r_km, plots, name in stages:
    owned = owned_blob(Tw, S, r_km)
    hwy.grow(plots - done, owned)
    done = plots
    im = hw_render(Tw, owned, hwy, px=0.3)
    if name == 'jerus':
        # the first stage close: a 12 km square round the site
        im2 = hw_render(Tw, owned, hwy, px=1.2)
        cx, cy = int(S[0] * 1.2), int(S[1] * 1.2)
        im2.crop((cx - 240, cy - 160, cx + 240, cy + 160)).save(f'{out}/hw-jerus-close.png')
    im.save(f'{out}/hw-{name}.png')
    hwf.append(dict(stage=name, highways=round(plots / 6.5), plots=int(hwy.hw.sum()), km=round(float(hwy.hw.sum()) * 0.03, 1),
                    corridors=len(hwy.corridors), interchanges=len(hwy.interchanges), radius_km=r_km))
facts['highways'] = hwf

# ------------------------------------------------------------------ islands and routes
import islands as isl
Ti = isl.scene()
landi = Ti != SALT
lab, sizes = isl.components(landi)
H, W = Ti.shape
yy, xx = np.mgrid[0:H, 0:W]
Si = (420, 470)
homeL = int(lab[Si[1], Si[0]])
A, B = int(lab[430, 1010]), int(lab[790, 1270])
nn = isl.noise((H, W), 100, 9)
own = np.hypot(xx - Si[0], yy - Si[1]) < 330 + (nn - 0.5) * 120
own |= (lab == A)
own |= (lab == B) & (xx < 1305)
own |= (Ti == SALT) & (np.hypot(xx - 820, yy - 450) < 260)
sea, shore = isl.sea_grid(Ti)

def nearest_sea(c):
    best = None
    for j in range(max(0, c[0] - 6), min(sea.shape[0], c[0] + 7)):
        for i in range(max(0, c[1] - 6), min(sea.shape[1], c[1] + 7)):
            if sea[j, i]:
                d = (j - c[0]) ** 2 + (i - c[1]) ** 2
                if best is None or d < best[0]:
                    best = (d, (j, i))
    return best[1]
port_home = (690, 466)
port_a = (880, 432)
pa, pb = nearest_sea((port_home[1] // isl.CG, port_home[0] // isl.CG)), nearest_sea((port_a[1] // isl.CG, port_a[0] // isl.CG))
ferry = isl.pull(isl.astar(sea, shore, pa, pb), sea)
off, bearing = isl.offing(sea, pa, radius_cells=int(660 / isl.CG))
worldr = isl.pull(isl.astar(sea, shore, pa, off), sea)
# a coastal cargo route round Isle A to a wharf on its far side
wharf = (1135, 470)
pc = nearest_sea((wharf[1] // isl.CG, wharf[0] // isl.CG))
cargo = isl.pull(isl.astar(sea, shore, pa, pc), sea)

def cells_to_px(path, px):
    return [((i + 0.5) * isl.CG * px, (j + 0.5) * isl.CG * px) for (j, i) in path]

def draw_islands(px, linked_b, fname, boats=True, crop=None, labels=True):
    g = np.array(GROUND, np.uint8)[Ti]
    m = ~own
    g[m] = (g[m] * 0.66 + 30).astype(np.uint8)
    img = Image.fromarray(g, 'RGB').resize((int(W * px), int(H * px)), Image.BILINEAR)
    dr = ImageDraw.Draw(img, 'RGBA')
    # the city's built ground: a warm wash with the arterial grid over the home landmass round the site and Isle A's town
    built = ((np.hypot(xx - Si[0], yy - Si[1]) < 230 + (nn - 0.5) * 80) & (lab == homeL)) | \
            ((np.hypot(xx - 960, yy - 440) < 70) & (lab == A))
    if linked_b:
        built |= (np.hypot(xx - 1250, yy - 790) < 32) & (lab == B)
    ys, xs = np.nonzero(built[::8, ::8])
    for y, x in zip(ys * 8, xs * 8):
        dr.rectangle([x * px, y * px, (x + 8) * px, (y + 8) * px], fill=(0xea, 0xa5, 0x72, 150))
    for v in range(OFF, W, ART):
        for y in range(0, H, 8):
            if built[y, v]:
                dr.rectangle([v * px, y * px, v * px + max(1, px), (y + 8) * px], fill=(0xa4, 0xaa, 0xb0, 255))
    for v in range(OFF, H, ART):
        for x in range(0, W, 8):
            if built[v, x]:
                dr.rectangle([x * px, v * px, (x + 8) * px, v * px + max(1, px)], fill=(0xa4, 0xaa, 0xb0, 255))
    # Isle B not linked: its owned ground hatched
    if not linked_b:
        hb = (lab == B) & own
        ys, xs = np.nonzero(hb[::4, ::4])
        for y, x in zip(ys * 4, xs * 4):
            if (x + y) % 12 == 0:
                dr.line([x * px, (y + 10) * px, (x + 10) * px, y * px], fill=(0xe3, 0xb3, 0x41, 255), width=2)
    # owned edge: a thin outline of the city's ground
    e = own & ~(np.roll(own, 1, 0) & np.roll(own, -1, 0) & np.roll(own, 1, 1) & np.roll(own, -1, 1))
    ys, xs = np.nonzero(e[::2, ::2])
    for y, x in zip(ys * 2, xs * 2):
        dr.point((x * px, y * px), fill=(255, 255, 255, 140))
    # routes
    def poly(path, colour, dash=False, width=2):
        pts = cells_to_px(path, px)
        if dash:
            for k in range(len(pts) - 1):
                (x0, y0), (x1, y1) = pts[k], pts[k + 1]
                L = math.hypot(x1 - x0, y1 - y0)
                n = max(1, int(L / 8))
                for s in range(0, n, 2):
                    t0, t1 = s / n, min(1, (s + 1) / n)
                    dr.line([x0 + (x1 - x0) * t0, y0 + (y1 - y0) * t0, x0 + (x1 - x0) * t1, y0 + (y1 - y0) * t1], fill=colour, width=width)
        else:
            dr.line(pts, fill=colour, width=width)
        return pts
    wp = poly(worldr, (255, 255, 255, 120), dash=True)
    # beyond the offing: on into the abyss
    (xa, ya), (xb, yb) = wp[-2], wp[-1]
    L = math.hypot(xb - xa, yb - ya)
    ux, uy = (xb - xa) / L, (yb - ya) / L
    for s in range(0, 400, 12):
        a = max(0, 120 - s * 0.35)
        dr.line([xb + ux * s, yb + uy * s, xb + ux * (s + 6), yb + uy * (s + 6)], fill=(255, 255, 255, int(a)), width=2)
    fp = poly(ferry, (0xf7, 0xf4, 0xee, 200), width=2)
    cp = poly(cargo, (0x5a, 0xa9, 0xff, 170), dash=True)
    # the offing circle's arc
    ox, oy = (pa[1] + 0.5) * isl.CG * px, (pa[0] + 0.5) * isl.CG * px
    R = 660 * px
    dr.arc([ox - R, oy - R, ox + R, oy + R], start=bearing - 50, end=bearing + 50, fill=(255, 255, 255, 70), width=1)
    # ports
    def port(x, y, name):
        X, Y = x * px, y * px
        dr.rectangle([X - 6, Y - 6, X + 6, Y + 6], fill=(0x0b, 0x11, 0x18, 230), outline=(0x5a, 0xa9, 0xff, 255), width=2)
        dr.line([X - 3, Y, X + 3, Y], fill=(0x5a, 0xa9, 0xff, 255), width=2)
        dr.line([X, Y - 3, X, Y + 3], fill=(0x5a, 0xa9, 0xff, 255), width=2)
    port(*port_home, 'home')
    port(*port_a, 'A')
    if linked_b:
        port(1205, 760, 'B')
    port(*wharf, 'wharf')
    # boats
    def boat_at(pts, t, colour, size=5):
        segs = [(pts[k], pts[k + 1]) for k in range(len(pts) - 1)]
        Ls = [math.hypot(b[0] - a[0], b[1] - a[1]) for a, b in segs]
        tot = sum(Ls)
        d = (t % 1.0) * tot
        for (a, b), L in zip(segs, Ls):
            if d <= L:
                f = d / L if L else 0
                x, y = a[0] + (b[0] - a[0]) * f, a[1] + (b[1] - a[1]) * f
                ang = math.atan2(b[1] - a[1], b[0] - a[0])
                ca, sa = math.cos(ang), math.sin(ang)
                shape = [(size * 1.6, 0), (size * 0.6, size * 0.7), (-size * 1.4, size * 0.7), (-size * 1.4, -size * 0.7), (size * 0.6, -size * 0.7)]
                dr.polygon([(x + u * ca - v * sa, y + u * sa + v * ca) for (u, v) in shape], fill=colour, outline=(20, 25, 30, 255))
                return
            d -= L
    if boats:
        far = wp + [(xb + ux * 380, yb + uy * 380)]
        for t, col in ((0.18, (0xff, 0x9f, 0x43, 255)), (0.52, (0x5a, 0xa9, 0xff, 255)), (0.83, (0x5a, 0xa9, 0xff, 255))):
            boat_at(far, t, col, 5)
        for t in (0.25, 0.75):
            boat_at(fp, t, (0xf7, 0xf4, 0xee, 255), 4)
        boat_at(cp, 0.6, (0xff, 0x9f, 0x43, 255), 4)
    if crop:
        img = img.crop(crop)
    img.save(fname)

draw_islands(0.5, False, f'{out}/islands-unlinked.png')
draw_islands(0.5, True, f'{out}/islands-linked.png')
masses = {}
for mlab, name in ((homeL, 'Home'), (A, 'Isle A'), (B, 'Isle B')):
    masses[name] = dict(owned_dry_km2=round(float(((lab == mlab) & own).sum()) * 0.0009, 1),
                        whole_km2=round(float((lab == mlab).sum()) * 0.0009, 1))
def plen(path):
    return round(sum(math.hypot(path[k + 1][0] - path[k][0], path[k + 1][1] - path[k][1]) for k in range(len(path) - 1)) * isl.CG * 0.03, 1)
facts['islands'] = dict(landmasses=masses, ferry_km=plen(ferry), cargo_km=plen(cargo), world_to_offing_km=plen(worldr),
                        offing_bearing=bearing, ferry_legs=len(ferry) - 1, cargo_legs=len(cargo) - 1, world_legs=len(worldr) - 1)
json.dump(facts, open(f'{out}/facts.json', 'w'), indent=1, default=str)
print(json.dumps(facts, indent=1, default=str)[:4000])
