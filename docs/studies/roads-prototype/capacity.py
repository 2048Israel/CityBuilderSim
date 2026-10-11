"""How full a district the plan can draw: Jerus's mix with his roads, and the same city built paved."""
import sys, json
import numpy as np
from roads_proto import load_types, scaled_buildings, JERUS_COUNTS, GRASS, FOREST, SAND
from proto2 import plan_ladder, ground_for, OFF, ART
types = load_types(sys.argv[1])
n = OFF + ART * 8 + 4
T = ground_for(n)
O = np.ones(T.shape, bool); O[:44, 200:] = False
dry = int((O & ((T == GRASS) | (T == FOREST) | (T == SAND))).sum())
# the same city paved: his gravel and paved roads' trips on Paved Roads (capacity kept)
paved_counts = list(JERUS_COUNTS)
trips = JERUS_COUNTS[29] * 900 + JERUS_COUNTS[13] * 1200
paved_counts[29] = 0
paved_counts[13] = round(trips / 1200)
out = {}
for name, counts in (('jerus', JERUS_COUNTS), ('paved-city', paved_counts)):
    for fill in (0.75, 0.85, 0.92):
        blds, roads, k = scaled_buildings(types, dry, fill=fill, scale_counts=counts)
        p, sq, bl = plan_ladder(T, O, (70, 90), blds, {'gravel': roads['gravel'], 'paved': roads['paved']})
        c = p.checks()
        lost = sum(b['w'] * b['h'] for b in p.unplaced)
        need = sum(b['w'] * b['h'] for b in blds)
        out[f'{name}@{fill}'] = dict(buildings=len(blds), unplaced=len(p.unplaced), unplaced_share_of_ground=round(lost / need, 4),
                                     road_share_model=round((roads['gravel'] + roads['paved']) / (need + roads['gravel'] + roads['paved']), 3),
                                     street_share_map=c['street_share'], tracks=c['tracks'], square=sq, blvd=bl, cells=c['cells'],
                                     drawn_share_of_dry=round((c['building_plots'] + c['street_plots']) / dry, 3))
        print(name, fill, json.dumps(out[f'{name}@{fill}']), flush=True)
json.dump(out, open('capacity.json', 'w'), indent=1)
