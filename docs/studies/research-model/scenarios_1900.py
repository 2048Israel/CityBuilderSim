"""The 1900 start: the same city, founded in 1900 or in 1950.
Run: python3 scenarios_1900.py        (a few minutes; Monte Carlo over seeds)

Two paces, both from the project's own runs (illustrative, not measured on this build):
  brisk - the hand-played run of 2026-09-04 (playtest-hand-played-116k.md): 37,730 people
          by about month 726, 116,480 at month 1,026. The University comes at month 726,
          an Institute of technology and a Medical school at month 1,000.
  slow  - the scripted playtest (0.7.102-0.7.103 road rules): 17,000-30,000 people at
          month 1,070. The University comes at month 1,100.
  early - a player who builds schools for research before the city needs them: a University
          at month 300, a Law school at 360, a Medical school at 480, an Institute of
          technology at 600 (illustrative).
Every school's field is funded to its room from the month it opens (the most a city can buy).
The question: how many discoveries does the city make here before history runs out (Jan 2026)?
"""
import statistics as st, sys
import research_model as rm
from research_model import post_room, post_cost, faculty_fte, month_of, effort, POST
from catalogue import CATALOGUE as C1950
try:
    from catalogue_1900 import C1900
except ImportError:
    C1900 = []

FULL = 10 ** 9      # a grant no room can spend: every field funded to its room

def timeline(pace, founded):
    m0 = month_of(founded, 1) - 1          # month 1 after founding = January of the founding year
    if pace == 'brisk':
        steps = [(726, {'University': 1}), (1000, {'University': 1, 'Institute of technology': 1, 'Medical school': 1})]
    elif pace == 'early':
        steps = [(300, {'University': 1}), (360, {'University': 1, 'Law school': 1}),
                 (480, {'University': 1, 'Law school': 1, 'Medical school': 1}),
                 (600, {'University': 1, 'Law school': 1, 'Medical school': 1, 'Institute of technology': 1})]
    else:
        steps = [(1100, {'University': 1})]
    return [(m0 + k, (schools, {f: FULL for f in POST})) for k, schools in steps]

def researchers_fn(tl):
    def f(field, m):
        cur = None
        for start, cfg in tl:
            if m >= start: cur = cfg
        if not cur: return 0.0
        schools, grants = cur
        return faculty_fte(schools, field) + min(grants.get(field, 0) / post_cost(field), post_room(schools, field))
    return f

def finds(pace, founded, catalogue, seeds=200):
    rm.CATALOGUE = catalogue
    tl = timeline(pace, founded)
    f = researchers_fn(tl)
    start = month_of(founded, 1)
    counts, ahead, which, before50 = [], [], {}, []
    for s in range(seeds):
        known, log, *_ = rm.run(f, seed=1000 + s, start=start, end=month_of(2026, 1))
        counts.append(len(log))
        before50.append(sum(1 for m, i in log if m < month_of(1950, 1)))
        for m, i in log:
            it = next(c for c in catalogue if c['id'] == i)
            ahead.append((month_of(it['year'], it.get('month', 6)) - m) / 12)
            which[it['code']] = which.get(it['code'], 0) + 1
    spend = sum(post_room(tl[-1][1][0], fl) * post_cost(fl) for fl in POST) / 1000
    return st.mean(counts), (st.mean(ahead) if ahead else 0), which, tl, spend, st.mean(before50)

if __name__ == '__main__':
    # streptomycin (PM6) comes before the 1950 catalogue's TB drugs (M1): research-1900.md section 8.6
    C1950 = [dict(c, needs=c['needs'] + [206]) if c['code'] == 'M1' else c for c in C1950]
    cat = C1900 + C1950
    print(f'catalogue: {len(C1900)} entries 1900-1950 + {len(C1950)} entries 1951-1995')
    for pace in ('early', 'brisk', 'slow'):
        for founded in (1950, 1900):
            mean, ah, which, tl, spend, b50 = finds(pace, founded, cat, seeds=int(sys.argv[1]) if len(sys.argv) > 1 else 200)
            opens = ', '.join(f"{rm.ym(m)}: {'+'.join(k for k in s)}" for m, (s, _) in tl)
            top = sorted(which.items(), key=lambda kv: -kv[1])[:8]
            print(f'{pace:5} founded {founded}: schools {opens}; full room D$ {spend:.1f}M a month')
            print(f'      finds here before 2026: {mean:.2f}, of them before 1950: {b50:.2f}  (ahead of the world {ah:.1f} yrs on average)  most often: {top}')
    # the work a discovery takes at its date (researcher-years), the first study's section 1.8 rule run backward
    print('\nwork at the date (researcher-years):')
    for y in (1900, 1913, 1925, 1937, 1950):
        print(f"  {y}: building/equipment {400 * 1.053 ** (y - 1950):6.0f}  recipe {200 * 1.053 ** (y - 1950):5.0f}"
              f"  drug {500 * 1.035 ** (y - 1950):5.0f}")

    # what arrives from abroad, decade by decade (history as the tutorial)
    print('\narrivals by decade (world dates):')
    for d in range(1900, 1950, 10):
        row = [c for c in C1900 if d <= c['year'] < d + 10]
        print(f"  {d}s: " + ', '.join(f"{c['code']} {c['name']}" for c in sorted(row, key=lambda c: (c['year'], c.get('month', 6)))))
    print('\nwork at the world date, each 1900-1950 entry (researcher-years):')
    for c in C1900:
        print(f"  {c['code']:5} {c['name']:40} {c['year']}  {effort(c):6.0f}")
