import sys, json
import numpy as np
from roads_proto import load_types, scaled_buildings, JERUS_COUNTS, GRASS
from proto2 import plan_ladder, OFF, ART
types = load_types(sys.argv[1])
n = OFF + ART * 8 + 4
T = np.zeros((n, n), np.int8)          # all grass, all owned: the plan's pure geometry
O = np.ones(T.shape, bool)
dry = n * n
paved_counts = list(JERUS_COUNTS)
trips = JERUS_COUNTS[29] * 900 + JERUS_COUNTS[13] * 1200
paved_counts[29] = 0; paved_counts[13] = round(trips / 1200)
for name, counts in (('jerus', JERUS_COUNTS), ('paved-city', paved_counts)):
    for fill in (0.75, 0.80, 0.85, 0.92):
        blds, roads, k = scaled_buildings(types, dry, fill=fill, scale_counts=counts)
        p, sq, bl = plan_ladder(T, O, (n // 2, n // 2), blds, {'gravel': roads['gravel'], 'paved': roads['paved']})
        c = p.checks()
        need = sum(b['w'] * b['h'] for b in blds)
        lost = sum(b['w'] * b['h'] for b in p.unplaced)
        print(name, fill, json.dumps(dict(unplaced=len(p.unplaced), of=len(blds), lost_ground=round(lost / need, 4), cells=c['cells'],
              street_share_map=c['street_share'], drawn=round((c['building_plots'] + c['street_plots']) / dry, 3),
              out_of_reach=c['out_of_reach'], comps=c['components'], plus=c['closest_plus'])), flush=True)
