# Hourly spread of wind and solar output inside each load block, by balancing authority, EIA-930 2025.
import csv, sys, collections, math
def num(s):
    s = (s or '').replace(',', '').strip()
    try: return float(s)
    except ValueError: return None
W = 'Net Generation (MW) from Wind without Integrated Battery Storage (Adjusted)'
S = 'Net Generation (MW) from Solar without Integrated Battery Storage (Adjusted)'
D = 'Demand (MW) (Adjusted)'
data = collections.defaultdict(dict)
for path in sys.argv[1:]:
    with open(path, newline='', encoding='utf-8-sig') as f:
        for row in csv.DictReader(f):
            data[row['Balancing Authority']][row['UTC Time at End of Hour']] = (num(row[D]), num(row[W]), num(row[S]))
def stats(rows, k):
    v = [r[k] for r in rows if r[k] is not None and r[k] >= 0]
    if len(v) < 100: return None
    m = sum(v) / len(v)
    if m <= 0: return None
    sd = math.sqrt(sum((x - m) ** 2 for x in v) / len(v))
    return m, sd / m
for ba in sys.argv[3:] if False else ['ERCO','SWPP','MISO','PJM','CISO','BPAT','PACE','PSCO','NWMT','AZPS','DUK','ISNE']:
    hrs = [r for r in data[ba].values() if r[0] and r[0] > 0]
    if not hrs: continue
    hrs.sort(key=lambda r: -r[0])
    n = len(hrs); cut = [0, n // 10, n // 2, n]
    out = [ba]
    for i, name in enumerate(['peak', 'shoulder', 'base']):
        blk = hrs[cut[i]:cut[i + 1]]
        w = stats(blk, 1); s = stats(blk, 2)
        out.append('%s wind cv %s solar cv %s' % (name, 'n/a' if not w else round(w[1], 2), 'n/a' if not s else round(s[1], 2)))
    print(' | '.join(out))
