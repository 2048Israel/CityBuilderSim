"""The blow-up study's tables (spec-blowup-7200.md) from the drivers' CSVs.

    python -I report.py DIR

DIR holds, from BlowupRun:     seed0.csv seed1.csv seed0-lines.csv seed1-lines.csv (months 7,200-7,600)
               from HaulageProbe: haul0.csv haul1.csv (monthly from 7,200),
                                  year0-2.csv and their .wealth.csv (yearly, the game as it is),
                                  fix0-2.csv, fixB0-2.csv and their .wealth.csv (yearly, prototypes A and B).
"""
import csv
import os
import sys

D = sys.argv[1]


def rows(name):
    p = os.path.join(D, name)
    return list(csv.DictReader(open(p))) if os.path.exists(p) else []


def f(x, k):
    return float(x[k])


print("1. THE FIRST MONTH: the month's overdraft forgiven, against a month's GDP")
for seed in (1, 0):
    r = {int(f(x, "month")): x for x in rows(f"seed{seed}.csv")}
    lines = {}
    for line in open(os.path.join(D, f"seed{seed}-lines.csv")):
        p = line.rstrip("\n").rsplit(",", 1)
        m, lab = p[0].split(",", 1)
        try:
            lines.setdefault(int(m), {})[lab] = float(p[1])
        except ValueError:
            pass
    for m in sorted(lines):
        fg = lines[m].get("+ sectors OverdraftForgiven", 0)
        g = f(r[m], "gdpMonth")
        if fg > g:
            print(f"   seed {seed}: month {m}, forgiven {fg:.3g} = {fg / g:,.0f} months of GDP")
            for k in (m - 1, m, m + 1, m + 2, m + 3):
                x = r[k]
                print(f"      m{k}  bank cash {f(x, 'bankCash'):10.3g}  desk {f(x, 'bankSecurities'):9.3g}  "
                      f"window {f(x, 'cbAdvBank'):9.3g}  deposits {f(x, 'bankDeposits'):9.3g}  "
                      f"valuation {f(x, 'valuation'):9.3g}  profit tax {f(x, 'taxBusiness'):9.3g}")
            break

print("\n2. HAULAGE, MONTH BY MONTH, AROUND IT (the price rule's terms)")
for seed, lo, hi in ((1, 7382, 7389), (0, 7511, 7518)):
    print(f"   seed {seed}:  month   trucks   capacity t   carried t   quote D$/t   allowed     bill")
    for x in rows(f"haul{seed}.csv"):
        m = int(f(x, "month"))
        if lo <= m <= hi:
            print(f"            {m}  {f(x, 'trucks'):8.3g}  {f(x, 'capacityInForce'):10.3g}  {f(x, 'carried'):10.3g}  "
                  f"{f(x, 'quote'):11.3g}  {f(x, 'allowed'):9.3g}  {f(x, 'bill'):9.3g}")

print("\n3. THE FLEET OVER THE RUN, A ROW A CENTURY (yearly rows; the game as it is | the prototype)")
for seed in (0, 1, 2):
    base = {int(f(x, "month")): x for x in rows(f"year{seed}.csv")}
    fix = {int(f(x, "month")): x for x in rows(f"fix{seed}.csv")}
    print(f"   seed {seed}:  month |  trucks  need tr  quote D$/t  Freight preTax |  trucks  quote D$/t")
    for m in range(1200, 8001, 1200):
        b, x = base.get(m), fix.get(m)
        if not b:
            continue
        s = (f"            {m:5d} | {f(b, 'trucks'):8.3g} {f(b, 'need') / 3000:8.3g} {f(b, 'quote'):11.3g} {f(b, 'freightPreTax'):14.3g} |")
        if x:
            s += f" {f(x, 'trucks'):8.3g} {f(x, 'quote'):11.3g}"
        print(s)
    under = [int(f(x, "month")) for x in base.values() if f(x, "trucks") < 1]
    print(f"            under one truck at {len(under)} year marks, first {min(under) if under else '-'}")

def ratio(x, k):
    return f(x, k) / max(1e-9, f(x, "y_gdp"))


print("\n4. THE WORST YEAR AND THE WINDOW (yearly sums from WealthRun's CSV): the game as it is,")
print("   prototype A (re-strike and a one-truck floor), prototype B (the re-strike alone, the batch's)")
print("   seed  version     worst year's valuation, yrs GDP (month)  worst after m1200 (month)  window peak, yrs GDP (month)")
for seed in (0, 1, 2):
    for tag, name in (("as is", "year"), ("A floor", "fix"), ("B", "fixB")):
        w = rows(f"{name}{seed}.csv.wealth.csv")
        if not w:
            continue
        worst = max(w, key=lambda x: ratio(x, "y_valuation"))
        late = max([x for x in w if f(x, "month") > 1200], key=lambda x: ratio(x, "y_valuation"))
        win = max(w, key=lambda x: ratio(x, "cbAdvBank"))
        print(f"   {seed:4d}  {tag:10} {ratio(worst, 'y_valuation'):18.3g} ({int(f(worst, 'month')):4d})"
              f"   {ratio(late, 'y_valuation'):16.3g} ({int(f(late, 'month')):4d})"
              f"   {ratio(win, 'cbAdvBank'):18.3g} ({int(f(win, 'month')):4d})")

print("\n5. A WITHERED FLEET BOUGHT BACK (a yearly row under one truck, the next at ten or more, past month 600)")
for tag, name in (("as is", "year"), ("A floor", "fix"), ("B", "fixB")):
    for seed in (0, 1, 2):
        h = sorted(rows(f"{name}{seed}.csv"), key=lambda x: f(x, "month"))
        w = {int(f(x, "month")): x for x in rows(f"{name}{seed}.csv.wealth.csv")}
        for a, b in zip(h, h[1:]):
            if f(a, "trucks") < 1 and f(b, "trucks") >= 10 and f(a, "month") > 600:
                m = int(f(b, "month"))
                g = f(w[m], "y_gdp") / 12
                print(f"   {tag:8} seed {seed} m{m}: {f(a, 'trucks'):.2g} -> {f(b, 'trucks'):.3g} trucks, quote {f(a, 'quote'):.3g} -> "
                      f"{f(b, 'quote'):.3g} a tonne, the month's bill {f(b, 'bill'):.3g} = {f(b, 'bill') / g:.3g} months of GDP")
