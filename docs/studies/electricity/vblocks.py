# Wind and solar output in the three load blocks (10/40/50 of hours by load), US lower 48, EIA-930 2025.
import csv, sys, collections
def num(s):
    s = (s or '').replace(',', '').strip()
    try: return float(s)
    except ValueError: return None
load = collections.defaultdict(float); wind = collections.defaultdict(float); sun = collections.defaultdict(float)
seen = collections.defaultdict(int)
for path in sys.argv[1:]:
    with open(path, newline='', encoding='utf-8-sig') as f:
        r = csv.DictReader(f)
        for row in r:
            t = row['UTC Time at End of Hour']
            d = num(row['Demand (MW) (Adjusted)'])
            if d is not None and d > 0: load[t] += d; seen[t] += 1
            w = num(row.get('Net Generation (MW) from Wind without Integrated Battery Storage (Adjusted)'))
            s = num(row.get('Net Generation (MW) from Solar without Integrated Battery Storage (Adjusted)'))
            if w is not None: wind[t] += max(0.0, w)
            if s is not None: sun[t] += max(0.0, s)
mx = max(seen.values())
hours = [t for t in load if seen[t] >= mx - 3]
hours.sort(key=lambda t: -load[t])
n = len(hours)
def mean(d, hs): return sum(d[t] for t in hs) / len(hs)
L, W, S = mean(load, hours), mean(wind, hours), mean(sun, hours)
cut = [0, int(round(0.1*n)), int(round(0.5*n)), n]
print('hours', n, 'BAs per hour', mx, 'mean load', round(L), 'wind', round(W), 'solar', round(S))
for i, name in enumerate(['peak', 'shoulder', 'base']):
    hs = hours[cut[i]:cut[i+1]]
    print(name, 'load', round(mean(load, hs)/L, 3), 'wind', round(mean(wind, hs)/W, 3), 'solar', round(mean(sun, hs)/S, 3))
top = hours[:100]
print('top100', 'wind', round(mean(wind, top)/W, 3), 'solar', round(mean(sun, top)/S, 3))
