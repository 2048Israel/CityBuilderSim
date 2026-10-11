"""Design model for claude/research-design.md (Civic Ledger research study).

NOT game code. It checks the study's arithmetic and makes the mockups' figures:
money -> research posts -> researchers -> discoveries, the prize rule, and
equipment adoption across a sector's buildings by the spread rule.

Run: python3 research_model.py            (prints every table the report quotes)
Every constant is cited in the report; (est.) ones are the study's choices.
"""
import math, json, sys
from catalogue import CATALOGUE, FIELD_OF

# ---------------------------------------------------------------- the people
# Game wages, D$k a month (PayTier, NS 2025 figures)
WAGE = {'NONE': 3.46, 'SKILLED': 4.50, 'COLLEGE': 5.50, 'PROF': 8.20, 'SENIOR': 9.60, 'ELITE': 15.60}
# a research post: one researcher (half the field's licence holders, half the
# ungated university band) plus support staff; labour is ~60-67% of R&D cost
# (OECD TOE, Canada business 67%, HE ~48-58%); support 0.43 per researcher
# (Canada: researchers are 0.70 of R&D personnel).
POST = {
    # researcher pay,              support per researcher, support pay,  labour share
    'eng': ((WAGE['SENIOR'] + WAGE['PROF']) / 2, 0.43, WAGE['COLLEGE'], 0.62),   # HIGHTECH_ENG + SCIENCE
    'med': ((WAGE['ELITE'] + WAGE['PROF']) / 2, 0.43, WAGE['COLLEGE'], 0.60),    # DOCTOR + SCIENCE
    'bus': ((WAGE['SENIOR'] + WAGE['PROF']) / 2, 0.20, WAGE['SKILLED'], 0.80),   # FINANCE + POLICY
    'law': ((WAGE['ELITE'] + WAGE['PROF']) / 2, 0.20, WAGE['SKILLED'], 0.80),    # LAW + POLICY
}
def post_cost(f):
    pay, sup, sup_pay, share = POST[f]
    return (pay + sup * sup_pay) / share          # D$k a month, all in

# schools: university-band staff (buildings.json), what they research
SCHOOLS = {
    'University':               {'staff': 44 + 12, 'fields': {'eng': 22, 'med': 22, 'bus': 6, 'law': 6}},
    'Institute of technology':  {'staff': 28 + 18, 'fields': {'eng': 46}},
    'Medical school':           {'staff': 34 + 16, 'fields': {'med': 50}},
    'Business school':          {'staff': 24 + 6,  'fields': {'bus': 30}},
    'Law school':               {'staff': 26 + 8,  'fields': {'law': 34}},
}
FACULTY_RESEARCH = 0.40      # UVic collective agreement: 40/40/20 teaching/research/service
POSTS_PER_FACULTY = 2.7      # NSERC 2019-20: ~2.7 trainees per funded researcher (est. cap)

def faculty_fte(schools, f):
    return sum(n * SCHOOLS[s]['fields'].get(f, 0) * FACULTY_RESEARCH for s, n in schools.items())

def post_room(schools, f):
    """grant posts a field can hold: 2.7 per university-band staff member of its schools.
    The University's room is shared by every field that funds there; this model gives
    each field its own schools' staff share, which is what the mockups need."""
    return sum(n * SCHOOLS[s]['fields'].get(f, 0) * POSTS_PER_FACULTY for s, n in schools.items())

# ---------------------------------------------------------------- the work
GROWTH = {'eng': 0.053, 'med': 0.035, 'bus': 0.053, 'law': 0.053}   # BJVW: US aggregate 5.3%/yr; drugs 3.5%/yr
E1950 = {'building': 400, 'equipment': 400, 'recipe': 200, 'drug': 500}  # researcher-years at 1950 (est.; R1 s9: 300-1,000 eng/bus)
TMIN = {'building': 3, 'equipment': 3, 'recipe': 2, 'drug': 8}         # minimum years (est.; drug: clinical 96.8 months, DiMasi)
WINDOW = {'eng': 7, 'med': 9, 'bus': 4, 'law': 4}                       # years before the world's date the science exists
# Mansfield 1991: academic finding -> product 7.0 yrs (instruments 4.2 ... metals 9.8; drugs 8.8); business est. 2-5

def effort(item):
    kind = item.get('size', item['kind'])
    return E1950[kind] * (1 + GROWTH[item['field']]) ** (item['year'] - 1950)

def team_cap(item):
    kind = item.get('size', item['kind'])
    return effort(item) / TMIN[kind]

def mix(z):
    z = (z + 0x9E3779B97F4A7C15) & 0xFFFFFFFFFFFFFFFF
    z = ((z ^ (z >> 30)) * 0xBF58476D1CE4E5B9) & 0xFFFFFFFFFFFFFFFF
    z = ((z ^ (z >> 27)) * 0x94D049BB133111EB) & 0xFFFFFFFFFFFFFFFF
    return z ^ (z >> 31)

def hidden_size(seed, item_id):
    """a discovery's size for this city: its typical effort times a lognormal draw
    (sigma 0.6, mean 1), drawn once from the city's seed and the discovery's id."""
    h1, h2 = mix(seed ^ (item_id * 0x9E3779B97F4A7C15)), mix(seed ^ (item_id * 0xC2B2AE3D27D4EB4F) ^ 1)
    u1, u2 = ((h1 >> 11) + 0.5) * 2.0 ** -53, ((h2 >> 11) + 0.5) * 2.0 ** -53
    z = math.sqrt(-2 * math.log(u1)) * math.cos(2 * math.pi * u2)
    s = 0.6
    return math.exp(s * z - s * s / 2)

def month_of(year, month=1):
    return (year - 1950) * 12 + month       # month 1 = January 1950

def water_fill(people, caps):
    """researchers spread evenly over open problems, none past its cap; spare spill over"""
    alloc = {k: 0.0 for k in caps}
    left, live = people, dict(caps)
    while left > 1e-9 and live:
        share = left / len(live)
        done = [k for k, c in live.items() if c - alloc[k] <= share]
        if not done:
            for k in live: alloc[k] += share
            left = 0
        else:
            for k in done:
                left -= live[k] - alloc[k]; alloc[k] = live[k]; del live[k]
    return alloc, left

def run(researchers_by_month, seed=20261010, start=1, end=None, prize=None, cost=None, verbose=False):
    """researchers_by_month(field, month) -> FTE. Returns finds and the month-by-month state.
    prize: {field: D$k per discovery}; cost: {field: D$k per researcher-month}"""
    end = end or month_of(2026, 1)
    known = {}                    # id -> (month, 'city' | 'world')
    progress = {i['id']: 0.0 for i in CATALOGUE}
    size = {i['id']: effort(i) * hidden_size(seed, i['id']) for i in CATALOGUE}
    log, idle_log = [], []
    for m in range(start, end + 1):
        for it in CATALOGUE:                      # the world's own dates
            if it['id'] not in known and month_of(it['year'], it.get('month', 6)) <= m:
                known[it['id']] = (m, 'world')
        for f in ('eng', 'med', 'bus', 'law'):
            open_ = [it for it in CATALOGUE if it['field'] == f and it['id'] not in known
                     and month_of(it['year'] - WINDOW[f], it.get('month', 6)) <= m
                     and all(p in known for p in it.get('needs', []))]
            if not open_:
                idle_log.append((m, f, researchers_by_month(f, m))); continue
            caps = {it['id']: team_cap(it) for it in open_}
            R = researchers_by_month(f, m)
            alloc, idle = water_fill(R, caps)
            if prize and prize.get(f):
                # labs join every open problem whose expected remaining work costs less than the purse
                for it in sorted(open_, key=lambda x: effort(x) - progress[x['id']]):
                    rem = max(effort(it) - progress[it['id']], 1e-6)
                    if prize[f] >= rem * 12 * cost[f]:
                        room = caps[it['id']] - alloc[it['id']]
                        alloc[it['id']] += max(room, 0)
            for k, a in alloc.items():
                progress[k] += a / 12
                if progress[k] >= size[k]:
                    known[k] = (m, 'city'); log.append((m, k))
            idle_log.append((m, f, idle))
    return known, log, progress, size, idle_log

def ym(m):
    y, mo = divmod(m - 1, 12)
    return f"{['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'][mo]} {1950 + y}"

if __name__ == '__main__':
    for f in POST:
        print(f, 'post cost D$k/mo', round(post_cost(f), 1), 'a year', round(post_cost(f) * 12))

import random
def forward(known0, prog0, now, plan, years=10, draws=400, rng_seed=7, prize=None, cost=None):
    """What a player's preview would say: from today's state, with researchers
    per field held at plan[field], the expected finds here over the next years.
    Sizes are unknown to the player, so they are drawn from the same lognormal,
    conditioned on each open problem not having been found yet."""
    rng = random.Random(rng_seed)
    end = now + years * 12
    tot, per = 0, {}
    for d in range(draws):
        known = dict(known0); prog = dict(prog0)
        size = {}
        for it in CATALOGUE:
            if it['id'] in known: continue
            while True:
                z = rng.gauss(0, 1); s = effort(it) * math.exp(0.6 * z - 0.18)
                if s > prog[it['id']]: break
            size[it['id']] = s
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
                if prize and prize.get(f):
                    for it in open_:
                        rem = max(effort(it) - prog[it['id']], 1e-6)
                        if prize[f] >= rem * 12 * cost[f]:
                            alloc[it['id']] = caps[it['id']]
                for k, a in alloc.items():
                    prog[k] += a / 12
                    if prog[k] >= size[k]:
                        known[k] = (m, 'city'); tot += 1; per[k] = per.get(k, 0) + 1
    return tot / draws, {k: v / draws for k, v in per.items()}
