"""R3b's reference for ResearchPreviewCheck (research-design 5.4 and 9): the study's two cities (figures.py), each preview's
mean finds here per field over ten years with its Monte Carlo standard error and the chance of at least one, and the prizes
both as figures.py ran them (labs on one problem) and by the game's own chase (Research.month(), labsFor(): whole labs of 100,
poured cheapest first on every problem the purse covers that a team at its limit could finish before the world).
Run: python3 previews.py     (a few minutes; 1,000 draws a preview). Not game code.
"""
import sys, math, random, statistics as st
import research_model as rm
from research_model import *
from scenarios import researchers_fn, B

code = {c['id']: c['code'] for c in CATALOGUE}
byid = {c['id']: c for c in CATALOGUE}
FIELDS = ('eng', 'med', 'bus', 'law')

def forward2(known0, prog0, now, plan, labs=None, years=10, draws=1000, rng_seed=7):
    """forward() with per-draw counts by field; labs = (item_id, n, field): n lab researchers added to that one problem, as figures.py does."""
    rng = random.Random(rng_seed)
    end = now + years * 12
    counts = {f: [] for f in FIELDS}
    for d in range(draws):
        known = dict(known0); prog = dict(prog0); size = {}
        for it in CATALOGUE:
            if it['id'] in known: continue
            while True:
                z = rng.gauss(0, 1); s = effort(it) * math.exp(0.6 * z - 0.18)
                if s > prog[it['id']]: break
            size[it['id']] = s
        c = {f: 0 for f in FIELDS}
        for m in range(now + 1, end + 1):
            for it in CATALOGUE:
                if it['id'] not in known and month_of(it['year'], it.get('month', 6)) <= m:
                    known[it['id']] = (m, 'world')
            for f in plan:
                open_ = [it for it in CATALOGUE if it['field'] == f and it['id'] not in known
                         and month_of(it['year'] - WINDOW[f], it.get('month', 6)) <= m
                         and all(p in known for p in it.get('needs', []))]
                if not open_: continue
                caps = {it['id']: team_cap(it) for it in open_}
                alloc, _ = water_fill(plan[f], caps)
                if labs and labs[2] == f and labs[0] in caps:
                    alloc[labs[0]] = min(caps[labs[0]], alloc[labs[0]] + labs[1])
                for k, a in alloc.items():
                    prog[k] += a / 12
                    if prog[k] >= size[k]:
                        known[k] = (m, 'city'); c[f] += 1
        for f in FIELDS: counts[f].append(c[f])
    return counts

def show(name, plan, known, prog, now, labs=None):
    counts = forward2(known, prog, now, plan, labs=labs)
    tot = [sum(counts[f][i] for f in FIELDS) for i in range(len(counts['eng']))]
    parts = []
    for f in FIELDS:
        xs = counts[f]; m = st.mean(xs); se = st.pstdev(xs) / math.sqrt(len(xs)); p = sum(1 for x in xs if x > 0) / len(xs)
        parts.append(f"{f} mean {m:.4f} se {se:.4f} P1 {p:.4f}")
    print(f"  {name:36} all {st.mean(tot):.3f} (se {st.pstdev(tot)/math.sqrt(len(tot)):.3f}) | " + " | ".join(parts))

def state(known, prog, now):
    print("  known (code month source):", sorted((code[i], m, s) for i, (m, s) in known.items()))
    print("  piles of the not-known:", {code[i]: round(p, 6) for i, p in prog.items() if i not in known and p > 0})

UNI = {'University': 1}
fa = researchers_fn([(month_of(1953, 9), (UNI, {}))]); nowA = month_of(1955, 6)
kA, lA, pA, sA, _ = run(fa, seed=1022, end=nowA)
base = {f: faculty_fte(UNI, f) for f in FIELDS}
print('YOUNG CITY, June 1955, now month (1950 epoch)', nowA, 'researchers', base, 'post cost', {f: round(post_cost(f), 4) for f in FIELDS})
state(kA, pA, nowA)
show('as is', base, kA, pA, nowA)
show('engineering grant D$ 0.55M', dict(base, eng=base['eng'] + 550 / post_cost('eng')), kA, pA, nowA)
show('medical grant D$ 0.70M', dict(base, med=base['med'] + 700 / post_cost('med')), kA, pA, nowA)
show('engineering prize, 200 labs on 7', base, kA, pA, nowA, labs=(7, 200, 'eng'))
print('  item 7 is', code[7], byid[7]['name'], 'effort', effort(byid[7]))

f = researchers_fn(B); now = month_of(1979, 6)
known, log, prog, size, idle = run(f, seed=1042, end=now)
bb = {fl: f(fl, now) for fl in FIELDS}
print('\nBIG CITY, June 1979, now', now, 'found here', [(code[i], m) for m, i in log])
print('  researchers', bb, 'room', {fl: post_room(B[-1][1][0], fl) for fl in FIELDS}, 'faculty', {fl: faculty_fte(B[-1][1][0], fl) for fl in FIELDS})
state(known, prog, now)
show('as is', bb, known, prog, now)
show('med +63, bus to room', dict(bb, med=bb['med'] + 63, bus=faculty_fte(B[-1][1][0], 'bus') + post_room(B[-1][1][0], 'bus')), known, prog, now)
show('law grant D$ 1.0M', dict(bb, law=bb['law'] + 1000 / post_cost('law')), known, prog, now)
show('engineering grant cut to 0', dict(bb, eng=faculty_fte(B[-1][1][0], 'eng')), known, prog, now)
show('medical prize, 130 labs on 29', bb, known, prog, now, labs=(29, 130, 'med'))
show('medical prize, 100 labs on 29', bb, known, prog, now, labs=(29, 100, 'med'))
print('  item 29 is', code[29], byid[29]['name'], ' item 17 is', code[17], byid[17]['name'])

# ------------------------------------------------ the prizes by the game's chase
def labs_for(room):
    return max(0, math.floor(room / 100 + 1 - 0.5)) if room > 0 else 0

def forward_labs(known0, prog0, now, plan, prize, years=10, draws=1000, rng_seed=7):
    rng = random.Random(rng_seed)
    end = now + years * 12
    counts = {f: [] for f in FIELDS}; purse = []; labs_first = None
    for d in range(draws):
        known = dict(known0); prog = dict(prog0); size = {}
        for it in CATALOGUE:
            if it['id'] in known: continue
            while True:
                z = rng.gauss(0, 1); s = effort(it) * math.exp(0.6 * z - 0.18)
                if s > prog[it['id']]: break
            size[it['id']] = s
        c = {f: 0 for f in FIELDS}; paid = 0
        for m in range(now + 1, end + 1):
            for it in CATALOGUE:
                if it['id'] not in known and month_of(it['year'], it.get('month', 6)) <= m:
                    known[it['id']] = (m, 'world')
            for f in plan:
                open_ = [it for it in CATALOGUE if it['field'] == f and it['id'] not in known
                         and month_of(it['year'] - WINDOW[f], it.get('month', 6)) <= m
                         and all(p in known for p in it.get('needs', []))]
                if not open_: continue
                caps = {it['id']: team_cap(it) for it in open_}
                alloc, _ = water_fill(plan[f], caps)
                p = prize.get(f, 0)
                if p > 0:
                    cost = post_cost(f)
                    chase = []
                    for it in open_:
                        rem = max(effort(it) - prog[it['id']], 1e-6)
                        if p >= rem * 12 * cost and m + 12 * rem / caps[it['id']] < month_of(it['year'], it.get('month', 6)):
                            chase.append((rem, it['id']))
                    chase.sort(key=lambda t: t[0])
                    room = sum(max(0, caps[i] - alloc[i]) for _, i in chase)
                    left = 100 * labs_for(room)
                    if d == 0 and m == now + 1: labs_first = (labs_for(room), round(room, 2))
                    for _, i in chase:
                        take = min(left, max(0, caps[i] - alloc[i]))
                        alloc[i] += take; left -= take
                for k, a in alloc.items():
                    prog[k] += a / 12
                    if prog[k] >= size[k]:
                        known[k] = (m, 'city'); c[f] += 1
                        if p > 0: paid += p
        for f in FIELDS: counts[f].append(c[f])
        purse.append(paid)
    return counts, purse, labs_first

print('\nBY THE GAME\'S CHASE')
show_labs = lambda name, plan, k, p, n, prize: print_labs(name, forward_labs(k, p, n, plan, prize))
def print_labs(name, res):
    counts, purse, lf = res
    parts = [f"{f} mean {st.mean(counts[f]):.4f} se {st.pstdev(counts[f])/math.sqrt(len(counts[f])):.4f} P1 {sum(1 for x in counts[f] if x > 0)/len(counts[f]):.4f}" for f in FIELDS]
    print(f"  {name:30} labs at the first month {lf} purse mean {st.mean(purse):.1f}k | " + " | ".join(parts))
show_labs('young: eng prize D$ 140M', base, kA, pA, nowA, {'eng': 140000})
show_labs('big: med prize D$ 350M', bb, known, prog, now, {'med': 350000})
