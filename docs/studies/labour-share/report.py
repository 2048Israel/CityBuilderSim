"""The labour-share study's report (LS1, docs/studies/spec-labour-share.md).

    python -I docs/studies/labour-share/report.py BEA.csv RUN.csv [RUN.csv ...] [--months 600,1200,2400,4000]
        [--series "Luxury Retail,Construction"]

Reads LabourProbe's year rows and BEA's compensation share of value added
(bea-compensation-share.csv, beside this file) and prints, for each run at
each month asked, every sector's payroll over its value added beside its BEA
industry, and the two halves of the share: payroll over revenue (the posts
and the wage) and value added over revenue (the markup over inputs). Then
the city's wage against its prices. A month asked takes the last year row at
or before it (a 4,000-month run's last is m3996). --series prints the named
sectors every 240 months instead, for the share's path through a run.

Value added here is BEA's: revenue less inputs, electricity, water and
maintenance, so it holds the taxes on production as BEA's does ("va");
"basic" takes the sales and property tax out of it as well. For Retail and
Luxury Retail "revenue" in the halves is the margin, sales less the goods
bought, because BEA's gross output for trade is the margin; the share itself
does not depend on it.
"""
import csv
import sys

# The game's sector, BEA's industry code beside it (BEA's table, section 2 of the note).
BEA_OF = {
    "Agriculture": "111CA", "Forestry": "113FF", "Oil": "211", "Coal Mining": "212",
    "Copper Mining": "212", "Mining": "212", "Quarrying": "212", "Construction": "23",
    "Food Processing": "311FT", "Industry": "311FT", "Wood Products": "321", "Refining": "324",
    "Cement & Concrete": "327", "Materials": "327", "Heavy Industry": "331", "Copper Refining": "331",
    "Manufacturing": "332", "Automotive": "3361MV", "Freight": "48TW", "Retail": "44RT",
    "Luxury Retail": "4A0", "Restaurants": "722", "Business Services": "5412OP", "Real Estate": "531",
}
YEARS = ("2023", "2024", "2025")
# BEA's industries whose gross output is the margin (sales less the goods resold), not sales.
TRADE = {"44RT", "4A0"}


def bea(path):
    """BEA's compensation share, the mean of the last three years, by industry code."""
    out = {}
    for row in csv.DictReader(open(path, newline="")):
        if row["component"] == "compensation":
            out[row["code"]] = sum(float(row[y]) for y in YEARS) / len(YEARS)
    return out


def bea_go(path):
    """BEA's value added and compensation over gross output, 2023-2025, by industry code
    (bea-va-gross-output.csv beside the compensation table): the two halves of BEA's share."""
    out = {}
    try:
        for row in csv.DictReader(open(path, newline="")):
            out[row["code"]] = (float(row["va_over_go"]), float(row["comp_over_go"]))
    except FileNotFoundError:
        pass
    return out


def num(row, key):
    try:
        return float(row[key])
    except (KeyError, ValueError):
        return float("nan")


def pct(x):
    return "   -" if x != x else f"{100 * x:5.1f}"


def at(have, m):
    """The last year row at or before month m, or None."""
    before = [h for h in have if h <= m]
    return before[-1] if before else None


def shares_of(r):
    """va, basic, pay/rev, va/rev, rev/worker and pay/worker in the mean wage, or None. For the shops
    (TRADE) "rev" is the margin, sales less the goods bought, as BEA's gross output is for trade."""
    sales, pay, workers, wage = num(r, "revenue"), num(r, "payroll"), num(r, "workers"), num(r, "meanWage")
    va = sales - sum(num(r, k) for k in ("inputs", "electricity", "water", "maintenance"))
    rev = sales - num(r, "inputs") if BEA_OF.get(r["sector"]) in TRADE else sales
    if rev <= 0 or pay <= 0 or workers <= 0:
        return None
    basic = va - num(r, "salesTax") - num(r, "propertyTax")
    return (pay / va if va > 0 else None, pay / basic if basic > 0 else None, pay / rev, va / rev,
            rev / workers / wage, pay / workers / wage)


def share(x):
    return "  neg" if x is None else pct(x)


def series(name, path, sectors, shares):
    rows = list(csv.DictReader(open(path, newline="")))
    have = sorted({int(r["month"]) for r in rows})
    for label in sectors:
        print(f"\n== {name}, {label} (BEA {shares.get(BEA_OF.get(label, ''), float('nan')):.1f}): "
              f"month, va, pay/rev, va/rev, rev/worker")
        for m in range(240, have[-1] + 1, 240):
            r = next((r for r in rows if int(r["month"]) == at(have, m) and label in (r["sector"], r["label"])), None)
            s = r and shares_of(r)
            if s:
                print(f"  m{m:<5} {share(s[0])} {pct(s[2])} {pct(s[3])} {s[4]:8.2f}")


def report(name, path, months, shares, go):
    rows = list(csv.DictReader(open(path, newline="")))
    have = sorted({int(r["month"]) for r in rows})
    for asked in months:
        m = at(have, asked)
        if m is None or (asked != m and asked - m >= 12):
            continue
        year = [r for r in rows if int(r["month"]) == m]
        c = year[0]
        wage = num(c, "meanWage")
        print(f"\n== {name}, the year to m{m}: wage/price index {wage / num(c, 'priceIndex'):.4f}, "
              f"wage/cost of living {wage / num(c, 'costOfLiving'):.3f}, household wages/GDP "
              f"{pct(num(c, 'hhWages') / num(c, 'gdp'))}%, population {c['population']}")
        print(f"{'sector':18} {'va':>6} {'basic':>6} {'BEA':>5} {'pay/rev':>7} {'BEA':>5} {'va/rev':>6} {'BEA':>5} "
              f"{'rev/worker':>10} {'pay/worker':>10} {'workers':>8} {'units/wkr':>9} {'price/wage':>10}")
        for r in sorted(year, key=lambda r: -num(r, "payroll")):
            s = shares_of(r)
            if not s:
                continue
            code = BEA_OF.get(r["sector"], "")
            gva, gpay = go.get(code, (float("nan"), float("nan")))
            units, price = num(r, "mainUnits") / num(r, "workers"), num(r, "mainPrice") / wage
            print(f"{r['label'][:18]:18} {share(s[0]):>6} {share(s[1]):>6} {shares.get(code, float('nan')):5.1f} "
                  f"{pct(s[2]):>7} {pct(gpay):>5} {pct(s[3]):>6} {pct(gva):>5} {s[4]:10.2f} {s[5]:10.2f} {num(r, 'workers') / 12:8.0f} "
                  f"{units:9.3g} {price:10.3g}")


def main(argv):
    months, sectors = [600, 1200, 2400, 4000], None
    if "--months" in argv:
        i = argv.index("--months")
        months = [int(x) for x in argv[i + 1].split(",")]
        argv = argv[:i] + argv[i + 2:]
    if "--series" in argv:
        i = argv.index("--series")
        sectors = argv[i + 1].split(",")
        argv = argv[:i] + argv[i + 2:]
    shares = bea(argv[0])
    go = bea_go(argv[0].replace("\\", "/").rsplit("/", 1)[0] + "/bea-va-gross-output.csv")
    if sectors:
        for path in argv[1:]:
            series(path.replace("\\", "/").rsplit("/", 1)[-1].removesuffix(".csv"), path, sectors, shares)
        return
    print("va, basic: payroll over value added (with, then without, the taxes on production), %;")
    print("BEA: compensation over value added, then over gross output, then value added over")
    print("gross output, 2023-2025 (pay/rev and va/rev's BEA halves); rev/worker, pay/worker: a worker's")
    print("month of revenue and of pay, in the city's mean monthly wage; workers: the year's mean. For Retail and")
    print("Luxury Retail, revenue is the margin (sales less the goods bought), as BEA's trade gross output is.")
    for path in argv[1:]:
        report(path.replace("\\", "/").rsplit("/", 1)[-1].removesuffix(".csv"), path, months, shares, go)


if __name__ == "__main__":
    main(sys.argv[1:])
