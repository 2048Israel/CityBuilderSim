"""Leaderboard conversion model, worked on illustrative figures.

Pivot unit: R = real US$ at 2025 prices (the "founding dollar": the game's
prices were struck on 2025-26 real figures, so 1 founding game US$ = 1 real 2025 US$).

A game city c at month t, a domestic money figure X:
  game US$ (market) = X / fx_c(t)            fx = local per game US$
  game US$ (PPP)    = X / parity_c(t)        parity = the game's PPP rate (fxParity)
  R                 = game US$ / W_c(t)      W = that world's US$ price level, 1.0 at founding

A real-world figure arrives already in R (market) or R_ppp (constant 2025 int'l $).

Display, active city 0 at year Y0:
  Real US$            : R
  Game US$  static    : R * W0(Y0)                     (base = your city's year)
  Game US$  per year  : saved city i -> its own raw game US$ at its own year (X_i/fx_i);
                        everything else as static
  A$        static    : R * W0(Y0) * rate0(Y0)         rate = fx or parity by the toggle
  A$        per year  : saved city i -> X_i/rate_i * rate0(Y0)  (bridged on game US$, no deflating)
"""
import json, math

# ---- the active city: Arden, July 2050 (month 607) ----
ARDEN = dict(name="Arden", code="ARD", sym="A$", year=2050, month="Jul 2050", pop=61240,
             gdp_dom=6.48e9, fx=1.74, parity=1.49, W=1.137, P=1.62)
# ---- two saved cities, other worlds, other years ----
SAVES = [
    dict(name="Danzik", slot=3, code="DZD", sym="D$", year=2272, month="Mar 2272", pop=1_230_000,
         gdp_dom=71.2e9, fx=0.46, parity=0.52, W=1.181,
         same_year=dict(pop=312_400, gdp_dom=9.86e9, fx=0.91, parity=0.84, W=1.142)),   # its Jul 2050, from history
    dict(name="Varn", slot=5, code="VAR", sym="V$", year=2031, month="Feb 2031", pop=18_400,
         gdp_dom=0.71e9, fx=1.31, parity=1.22, W=1.092, same_year=None),                   # has not reached 2050
]

# ---- real world: GDP per person 2025, R (market) and R_ppp; population 2025 (M);
#      growth of GDP per person a year to 2050 (real); population 2050 (M). tag S/P/G ----
# IMF WEO Oct 2025 checked for USA, Germany, UK, Canada, France, Italy, Russia, Turkiye, China.
REAL = [
 # name, kind, gdp_pc, gdp_pc_ppp, pop25, g, pop50, tag25
 ("Monaco","micro",256000,None,0.039,0.010,0.040,"S"),
 ("Liechtenstein","micro",197000,None,0.040,0.010,0.042,"S"),
 ("San Jose","city",210000,195000,2.0,0.016,2.3,"G"),
 ("Luxembourg","nation",140900,152000,0.68,0.010,0.80,"S"),
 ("Seattle","city",135000,135000,4.1,0.014,4.9,"G"),
 ("Ireland","nation",129100,135000,5.3,0.015,6.0,"S"),
 ("Boston","city",120000,120000,4.9,0.013,5.3,"G"),
 ("New York","city",118000,118000,19.9,0.012,20.6,"G"),
 ("Zurich","city",115000,98000,1.6,0.010,1.9,"G"),
 ("Switzerland","nation",111000,98000,9.0,0.010,10.1,"S"),
 ("Oslo","city",105000,112000,1.1,0.011,1.3,"G"),
 ("London","city",95000,103000,9.8,0.011,10.9,"G"),
 ("Singapore","nation",92900,156000,6.0,0.015,6.4,"S"),
 ("Iceland","nation",90300,76000,0.39,0.012,0.44,"S"),
 ("Norway","nation",89700,99000,5.6,0.011,6.1,"S"),
 ("United States","nation",89599,89599,347.3,0.014,381.0,"S"),
 ("Reykjavik","city",85000,72000,0.24,0.012,0.29,"G"),
 ("Denmark","nation",74000,80000,6.0,0.012,6.3,"S"),
 ("Netherlands","nation",70000,81000,18.0,0.011,18.6,"S"),
 ("Australia","nation",64500,70000,27.4,0.012,34.0,"S"),
 ("Germany","nation",59925,73553,84.0,0.010,79.6,"S"),
 ("Toronto","city",58000,69000,7.1,0.012,9.4,"G"),
 ("United Kingdom","nation",56661,63759,69.5,0.011,74.1,"S"),
 ("Canada","nation",54935,65500,40.1,0.012,47.8,"S"),
 ("France","nation",48982,66061,66.6,0.010,66.1,"S"),
 ("Tokyo","city",47000,58000,37.0,0.008,34.5,"G"),
 ("Halifax","city",44000,52000,0.48,0.011,0.66,"G"),
 ("Italy","nation",43161,63126,58.9,0.007,52.3,"S"),
 ("Japan","nation",33900,54000,123.1,0.007,104.9,"S"),
 ("Turkiye","nation",18198,43786,86.0,0.025,90.4,"S"),
 ("Russia","nation",17446,49049,144.0,0.012,133.1,"S"),
 ("China","nation",13806,29191,1409.0,0.032,1260.0,"S"),
 ("Brazil","nation",10300,22000,212.0,0.018,220.0,"S"),
 ("India","nation",2880,11900,1464.0,0.045,1680.0,"S"),
]

def city_R(c, ppp=False):
    rate = c["parity"] if ppp else c["fx"]
    return c["gdp_dom"] / rate / c["W"]          # R, total

def per_head(total, pop): return total / pop

def show(R, money, a=ARDEN, ppp=False):
    """R (real 2025 US$) -> the chosen money at the active city's moment."""
    if money == "real": return R
    if money == "game": return R * a["W"]
    rate = a["parity"] if ppp else a["fx"]
    return R * a["W"] * rate                   # "dom"

def save_per_year(c, money, a=ARDEN, ppp=False):
    """A saved city in its own year's money: no deflating, bridged on game US$."""
    rate = c["parity"] if ppp else c["fx"]
    game = c["gdp_dom"] / rate                 # its own game US$ at its own year
    if money == "game": return game
    if money == "dom":
        return game * (a["parity"] if ppp else a["fx"])
    return game / c["W"]                       # real: always deflated (2025 prices)

out = {}
# ---- Arden, one figure many ways (GDP per person) ----
ways = {}
for ppp in (False, True):
    R = per_head(city_R(ARDEN, ppp), ARDEN["pop"])
    for m in ("dom", "game", "real"):
        ways[f"{m}_{'ppp' if ppp else 'mkt'}"] = round(show(R, m, ppp=ppp))
out["arden_ways"] = ways
# identities
assert abs(ways["dom_mkt"] - ARDEN["gdp_dom"]/ARDEN["pop"]) < 1, "own city keeps its own A$ figure"
assert abs(ways["dom_ppp"] - ARDEN["gdp_dom"]/ARDEN["pop"]) < 1, "own city keeps its own A$ figure under PPP too"

# ---- the Today board and the Projected board (GDP per person, real US$, market) ----
def board(year, money="real", ppp=False, saves_mode="static"):
    rows = []
    for n, kind, pc, pc_ppp, p25, g, p50, tag in REAL:
        base = pc_ppp if ppp else pc
        t = tag
        if base is None:                       # no PPP figure: game fill at market x 1.0
            base, t = pc, "G"
        if year == 2025:
            v, pop = base, p25
        else:
            v, pop = base * (1 + g) ** (year - 2025), p50
            t = "P" if tag == "S" else "G"
        rows.append(dict(name=n, kind=kind, R=v, v=show(v, money, ppp=ppp), pop=pop*1e6, tag=t))
    R = per_head(city_R(ARDEN, ppp), ARDEN["pop"])
    rows.append(dict(name="Arden", kind="you", R=R, v=show(R, money, ppp=ppp), pop=ARDEN["pop"], tag="you",
                     when=ARDEN["month"]))
    for c in SAVES:
        Rc = per_head(city_R(c, ppp), c["pop"])
        v = show(Rc, money, ppp=ppp) if saves_mode == "static" else per_head(save_per_year(c, money, ppp=ppp), c["pop"])
        rows.append(dict(name=c["name"], kind="mine", R=Rc, v=v, pop=c["pop"], tag="mine", when=c["month"], slot=c["slot"]))
    rows.sort(key=lambda r: -r["v"])
    for i, r in enumerate(rows): r["rank"] = i + 1
    return rows

for key, kw in {"today_real": dict(year=2025), "proj_real": dict(year=2050),
                "today_dom": dict(year=2025, money="dom"),
                "today_game_static": dict(year=2025, money="game"),
                "today_game_peryear": dict(year=2025, money="game", saves_mode="peryear"),
                "today_real_ppp": dict(year=2025, ppp=True)}.items():
    b = board(**kw)
    out[key] = [dict(rank=r["rank"], name=r["name"], kind=r["kind"], v=round(r["v"]), pop=round(r["pop"]),
                     tag=r["tag"], when=r.get("when"), slot=r.get("slot")) for r in b]

def rank_of(b, name): return next(r["rank"] for r in out[b] if r["name"] == name)
# a money toggle never reorders the board when every row is converted from the same pivot
assert [r["name"] for r in out["today_real"]] == [r["name"] for r in out["today_dom"]] == \
       [r["name"] for r in out["today_game_static"]], "static boards share one order"
out["ranks"] = {k: rank_of(k, "Arden") for k in ("today_real","proj_real","today_real_ppp","today_game_peryear")}
out["n"] = len(out["today_real"])

# ---- saved cities, per year vs static, game US$ per person ----
sv = {}
for c in SAVES:
    sv[c["name"]] = dict(peryear=round(per_head(save_per_year(c, "game"), c["pop"])),
                         static=round(show(per_head(city_R(c), c["pop"]), "game")),
                         real=round(per_head(city_R(c), c["pop"])))
d = SAVES[0]["same_year"]
sv["Danzik_2050"] = dict(pop=d["pop"], real=round(d["gdp_dom"]/d["fx"]/d["W"]/d["pop"]),
                         game_static=round(d["gdp_dom"]/d["fx"]/d["W"]/d["pop"]*ARDEN["W"]))
out["saves"] = sv
out["arden_R"] = round(per_head(city_R(ARDEN), ARDEN["pop"]))
json.dump(out, open("figures.json", "w"), indent=1)   # written beside wherever it is run
print(json.dumps({k: out[k] for k in ("arden_ways", "ranks", "n", "saves", "arden_R")}, indent=1))
for k in ("today_real", "proj_real"):
    print(k, [(r["rank"], r["name"], r["v"], r["tag"]) for r in out[k]][:40])
