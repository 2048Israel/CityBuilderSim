"""Reproduces the study's section 5.4 and the mockups' figures.
Run: python3 figures.py     (about a minute; Monte Carlo over 1,000 draws)"""
from scenarios import *
import research_model as rm

code = {c['id']: c['code'] for c in CATALOGUE}
byid = {c['id']: c for c in CATALOGUE}

def labs_on(item_id, n, base, field):
    """a prize's Research Labs: n researchers added to one open problem"""
    orig = rm.water_fill
    def wf(people, caps):
        alloc, left = orig(people, caps)
        if item_id in caps and abs(people - base[field]) < 1e-6:
            alloc[item_id] = min(caps[item_id], alloc[item_id] + n)
        return alloc, left
    return orig, wf

def show(name, plan, known, prog, now, prize=None):
    if prize:
        orig, wf = labs_on(prize[0], prize[1], plan, prize[2]); rm.water_fill = wf
    t, per = rm.forward(known, prog, now, plan, years=10, draws=1000)
    if prize: rm.water_fill = orig
    print(f"  {name:34} expected finds here, 10 yrs: {t:.2f}   {[(code[k], round(v, 2)) for k, v in sorted(per.items())]}")

# ------------------------------------------------ the young city, June 1955
UNI = {'University': 1}
fa = researchers_fn([(month_of(1953, 9), (UNI, {}))]); nowA = month_of(1955, 6)
kA, lA, pA, sA, _ = run(fa, seed=1022, end=nowA)
base = {f: faculty_fte(UNI, f) for f in POST}
print('YOUNG CITY, June 1955 · researchers', {k: round(v, 1) for k, v in base.items()})
show('as is', base, kA, pA, nowA)
show('engineering grant D$ 0.55M', dict(base, eng=base['eng'] + 550 / post_cost('eng')), kA, pA, nowA)
show('medical grant D$ 0.70M', dict(base, med=base['med'] + 700 / post_cost('med')), kA, pA, nowA)
show('engineering prize D$ 140M (2 labs)', base, kA, pA, nowA, prize=(7, 200, 'eng'))
e7 = byid[7]; print(f"  the cheapest problem a lab could finish first: {e7['name']}, {effort(e7):.0f} ry x D$ {post_cost('eng')*12:.1f}k = D$ {effort(e7)*post_cost('eng')*12/1000:.0f}M")

# ------------------------------------------------ the big city, June 1979
f = researchers_fn(B); now = month_of(1979, 6)
known, log, prog, size, idle = run(f, seed=1042, end=now)
print('\nBIG CITY, June 1979 · found here:',
      [(code[i], ym(m), round((month_of(byid[i]['year'], byid[i]['month']) - m) / 12, 1)) for m, i in log])
bb = {fl: f(fl, now) for fl in POST}
print('  researchers', {k: round(v) for k, v in bb.items()}, ' room', {fl: round(post_room(B[-1][1][0], fl)) for fl in POST})
show('as is', bb, known, prog, now)
show('medicine D$ 4.5M -> 6.0M, business -> room', dict(bb, med=bb['med'] + 63, bus=faculty_fte(B[-1][1][0], 'bus') + post_room(B[-1][1][0], 'bus')), known, prog, now)
show('law grant D$ 1.0M', dict(bb, law=bb['law'] + 1000 / post_cost('law')), known, prog, now)
show('engineering grant cut to 0', dict(bb, eng=faculty_fte(B[-1][1][0], 'eng')), known, prog, now)
show('medical prize D$ 350M (130 lab posts)', bb, known, prog, now, prize=(29, 130, 'med'))
for i, fl in ((29, 'med'), (17, 'eng')):
    rem = effort(byid[i]) - prog[i]
    print(f"  labs come for {byid[i]['name']} at D$ {rem * post_cost(fl) * 12 / 1000:.0f}M ({rem:.0f} ry left)")
