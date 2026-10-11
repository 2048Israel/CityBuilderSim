"""The two cities the mockups show. Run: python3 scenarios.py"""
import statistics as st
from research_model import *

def researchers_fn(timeline):
    """timeline: list of (from_month, {field: (schools dict, grant D$k/mo)})"""
    def f(field, m):
        cur = None
        for start, cfg in timeline:
            if m >= start: cur = cfg
        if not cur: return 0.0
        schools, grants = cur
        fac = faculty_fte(schools, field)
        posts = min(grants.get(field, 0) / post_cost(field), post_room(schools, field))
        return fac + posts
    return f

def expect(timeline, start, end, seeds=400, prize=None):
    """Monte Carlo over cities: finds made here between start and end"""
    counts, firsts, ahead = [], {}, []
    f = researchers_fn(timeline)
    for s in range(seeds):
        known, log, *_ = run(f, seed=1000 + s, start=1, end=end, prize=prize,
                             cost={k: post_cost(k) for k in POST})
        here = [(m, i) for m, i in log if m >= start]
        counts.append(len(here))
        for m, i in here:
            it = next(c for c in CATALOGUE if c['id'] == i)
            ahead.append((month_of(it['year'], it['month']) - m) / 12)
            firsts[i] = firsts.get(i, 0) + 1
    return st.mean(counts), counts, firsts, ahead

# ------------------------------------------------------------------ city A
UNI = {'University': 1}
A_base = [(month_of(1955, 9), (UNI, {}))]
A_grant = [(month_of(1955, 9), (UNI, {})), (month_of(1957, 7), (UNI, {'eng': 550}))]
A_more = [(month_of(1955, 9), (UNI, {})), (month_of(1957, 7), (UNI, {'eng': 1080, 'med': 0}))]
if __name__ == '__main__':
    print('post room, University: eng', post_room(UNI, 'eng'), 'med', post_room(UNI, 'med'))
    for name, tl in [('A none', A_base), ('A eng 0.55M', A_grant), ('A eng 1.08M (room)', A_more)]:
        f = researchers_fn(tl)
        mean, counts, firsts, ahead = expect(tl, month_of(1957, 7), month_of(1967, 6))
        print(f"{name:20} researchers eng {f('eng', month_of(1958,1)):6.1f}  finds 1957-67 mean {mean:.2f}"
              f"  P(>=1) {sum(c>0 for c in counts)/len(counts):.2f}  by item {[(next(c['code'] for c in CATALOGUE if c['id']==k), v) for k,v in sorted(firsts.items())]}")

# ------------------------------------------------------------------ city B
def sch(**k): return k
B = [
 (month_of(1953, 9), ({'University': 1}, {})),
 (month_of(1958, 9), ({'University': 1, 'Institute of technology': 1}, {'eng': 1000})),
 (month_of(1963, 9), ({'University': 1, 'Institute of technology': 1, 'Medical school': 1}, {'eng': 2000, 'med': 1200})),
 (month_of(1968, 9), ({'University': 2, 'Institute of technology': 1, 'Medical school': 1}, {'eng': 3500, 'med': 2500})),
 (month_of(1972, 9), ({'University': 2, 'Institute of technology': 1, 'Medical school': 1, 'Business school': 1}, {'eng': 4500, 'med': 3500, 'bus': 500})),
 (month_of(1975, 9), ({'University': 2, 'Institute of technology': 2, 'Medical school': 1, 'Business school': 1}, {'eng': 6500, 'med': 4500, 'bus': 900})),
 (month_of(1977, 9), ({'University': 2, 'Institute of technology': 2, 'Medical school': 1, 'Business school': 1, 'Law school': 1}, {'eng': 6500, 'med': 4500, 'bus': 900})),
]
if __name__ == '__main__' and 'B' in sys.argv:
    f = researchers_fn(B)
    now = month_of(1979, 6)
    for fl in POST:
        print(fl, 'researchers now', round(f(fl, now), 1), 'room', round(post_room(B[-1][1][0], fl)), 'faculty', round(faculty_fte(B[-1][1][0], fl), 1))
    mean, counts, firsts, ahead = expect(B, 1, now, seeds=300)
    print('finds 1950-79 mean', round(mean, 2), 'dist', sorted(set(counts)), 'ahead mean', round(st.mean(ahead), 1) if ahead else None)
    print({next(c['code'] for c in CATALOGUE if c['id']==k): v for k, v in sorted(firsts.items())})
