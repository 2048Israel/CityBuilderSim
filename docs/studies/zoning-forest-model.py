"""Forest under each rule. The game's regrowth rule: a forest's depletion D
(m3 cut below full) falls by D/240 a month. Crews: Harvesting Operation,
12,500 m3 a month. Investors add at most one crew a month while the sector
ran at >= 90% last month (a crew pays at 2.7% a month, so pay is never the
limit). A crew is shed after 6 months under 50% running, one a month.

Rules:
  open   - Q14 as decided: no gate on crews
  E      - add a crew only while the cuttable standing wood lasts 36 months
           at the cut with that crew (standing / cut >= 36, gross)
  law    - E, and a share of the forest protected (never cut)
"""
import json, sys

KM2 = 13_700          # m3 a km2, full
CREW = 12_500         # m3 a month, Harvesting Operation
TAU = 240

def run(F_km2, standing0, crews0, rule, protect=0.0, months=1200, horizon=36):
    F = F_km2 * KM2
    Fc = F * (1 - protect)                    # cuttable when full
    prot = F * protect                        # held full, never cut
    Sc = max(0.0, standing0 - prot)           # cuttable standing now
    D = Fc - Sc                               # deficit of the cuttable part
    crews, low, last_rate = crews0, 0, 1.0
    out = []
    for m in range(months + 1):
        R = D / TAU
        cap = crews * CREW
        cut = min(cap, Sc + R)                # can cut what stands plus this month's growth
        rate = cut / cap if cap else 1.0
        # month
        D = D - R + cut
        D = min(max(D, 0.0), Fc)
        Sc = Fc - D
        out.append(dict(m=m, standing=(Sc + prot) / F, cut=cut, crews=crews, regrowth=R))
        # investors, after the month
        add = last_rate >= 0.9 and rate >= 0.9
        if rule in ('E', 'law'):
            add = add and Sc >= horizon * (cap + CREW)
        if rule == 'open':
            add = add and Sc >= CREW
        if add:
            crews += 1
        low = low + 1 if rate < 0.5 else 0
        if low >= 6 and crews > 0:
            crews -= 1
            low = 0
        last_rate = rate
    return out

def summary(o, years=(1, 3, 5, 10, 25, 50, 100)):
    s = {}
    for y in years:
        r = o[min(y * 12, len(o) - 1)]
        s[y] = dict(standing=round(r['standing'] * 100, 1), cut=round(r['cut']),
                    crews=r['crews'])
    peak = max(o, key=lambda r: r['crews'])
    lo = min(o, key=lambda r: r['standing'])
    s['peak_crews'] = (peak['crews'], peak['m'])
    s['low_standing'] = (round(lo['standing'] * 100, 1), lo['m'])
    tail = o[-120:]
    s['last10y_cut'] = round(sum(r['cut'] for r in tail) / len(tail))
    s['last10y_standing'] = round(sum(r['standing'] for r in tail) / len(tail) * 100, 1)
    return s

if __name__ == '__main__':
    # the materials mockups' city: natural woods 1,850 km2, 21.9M m3 standing, 6 crews
    big = dict(F_km2=1850, standing0=21.9e6, crews0=6)
    res = {}
    for name, kw in [('open', dict(rule='open')), ('E', dict(rule='E')),
                     ('law40', dict(rule='law', protect=0.40)),
                     ('law60', dict(rule='law', protect=0.60))]:
        o = run(**big, **kw)
        res[name] = summary(o)
        res[name + '_series'] = [round(r['standing'] * 100, 2) for r in o[::12]]
        res[name + '_cut_series'] = [round(r['cut']) for r in o[::12]]
    # a small city: 10 km2 of forest, full, no crews yet
    small = dict(F_km2=10, standing0=10 * KM2, crews0=0)
    for name, kw in [('small_open', dict(rule='open')), ('small_E', dict(rule='E'))]:
        o = run(**small, **kw)
        res[name] = summary(o)
    # the small city with one crew placed at once (the "strips 10 km2 in 11 months" case)
    o = run(F_km2=10, standing0=10 * KM2, crews0=1, rule='open', months=24)
    res['small_one_crew_months_to_10pct'] = next((r['m'] for r in o if r['standing'] <= 0.10), None)
    json.dump(res, open('/home/claude/zoning/forest.json', 'w'), indent=1)
    for k, v in res.items():
        if not k.endswith('series'):
            print(k, v)
