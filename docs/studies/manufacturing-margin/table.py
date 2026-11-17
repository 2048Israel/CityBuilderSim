"""The manufacturing margin probe's tables (MA1, docs/studies/manufacturing-margin.md).

    python -I docs/studies/manufacturing-margin/table.py RUN.csv [MONTHS]
    python -I docs/studies/manufacturing-margin/table.py ratios RUN.csv

RUN.csv is MarginProbe's output (run.sh). MONTHS (default 600,1200,1800,2400) are the years shown,
each the twelve months to it. Prints Markdown: the city and its exporters, each sector's books over
its sales, the metal goods' prices against the literals, and each works as the planner weighs it.
"""
import csv
import sys
from collections import defaultdict

BOOK = ["revenue", "inputs", "payroll", "electricity", "water", "maintenance", "propertyTax", "interest", "tax",
        "net", "workers", "posts", "exportValue", "consumables"]
MAKERS = ["Manufacturing", "Automotive", "Heavy Industry"]
# The world's prices are kept at 1900's level, the literals' (2025's) times PriceLevel.HISTORICAL_FACTOR, the US CPI's 1900
# over 2025 (spec-1900-start.md): a world price over this is in the literals' own dollars.
LEVEL = 0.02588
LIT = {"STEEL": (.650, .470), "FABRICATED_STEEL": (float("nan"), 1.545), "MACHINERY": (13.4, 8.1),
       "CARS": (41.3125, 33.3125), "VANS": (68.135, float("nan")), "ROLLING_STOCK": (2302.2, float("nan")),
       "TRUCKS": (215.0, float("nan")), "LUBRICANTS": (.0009637, .0008209), "WIRE_CABLE": (17.0, 13.0),
       "COPPER_ROD": (10.75, 10.25)}


def load(path):
    y, s, g, n, t = {}, defaultdict(dict), defaultdict(dict), defaultdict(dict), defaultdict(list)
    with open(path, encoding="utf-8") as fh:
        for row in csv.reader(fh):
            tag, m = row[0], int(row[1])
            if tag == "Y":
                k = ["priceIndex", "meanWage", "rate", "gdp", "exports", "imports", "population", "staffed", "months"]
                y[m] = dict(zip(k, map(float, row[2:])))
            elif tag == "S":
                s[m][row[2]] = dict(zip(BOOK, map(float, row[3:])))
            elif tag == "G":
                v = list(map(float, row[3:]))
                d = dict(zip(["local", "import", "export", "wImport", "wExport", "imported", "exported", "localFilled"], v))
                for k, mk in enumerate(MAKERS):
                    d[mk] = v[8 + 4 * k: 12 + 4 * k]
                g[m][row[2]] = d
            elif tag == "N":
                n[m][row[2]] = list(map(float, row[3:]))
            elif tag == "T":
                t[m].append([row[2], row[3]] + [float(x) for x in row[4:]])
    return y, s, g, n, t


def pct(a, b):
    return "%.1f%%" % (100 * a / b) if b else "-"


def ratios(path):
    """Each year: fabricated steel's band with nothing traded over steel's, and a car's over its 3.5 t of fabricated steel and
    0.4 t of machinery (the Assembly Plant's recipe), each over the literals' same ratio at mid-band; 1 is the literals'."""
    y, s, g, n, t = load(path)
    def mid(lo, hi):
        return hi if lo != lo else lo if hi != hi else (lo + hi) / 2
    lit = {k: mid(v[1], v[0]) for k, v in LIT.items()}
    print("| month | steel / literal | fabricated steel / literal | machinery / literal | car / literal | fabricated over steel, of the literals' | car over its metal, of the literals' |")
    print("|---|---|---|---|---|---|---|")
    for m in sorted(n):
        if m % 120:
            continue
        p = {k: mid(v[0], v[1]) / LEVEL for k, v in n[m].items()}
        if not all(k in p for k in ("STEEL", "FABRICATED_STEEL", "MACHINERY", "CARS")):
            continue
        r = {k: p[k] / lit[k] for k in p}
        fab = r["FABRICATED_STEEL"] / r["STEEL"]
        metal = 3.5 * p["FABRICATED_STEEL"] + 0.4 * p["MACHINERY"]
        car = (p["CARS"] / metal) / (lit["CARS"] / (3.5 * lit["FABRICATED_STEEL"] + 0.4 * lit["MACHINERY"]))
        print("| m%d | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f |" % (m, r["STEEL"], r["FABRICATED_STEEL"], r["MACHINERY"], r["CARS"], fab, car))


def main(path, months):
    y, s, g, n, t = load(path)
    months = [m for m in months if m in y]
    print("## The city\n")
    print("| year to | people | GDP a month (D$M) | real GDP a head | real wage | exports / GDP | Automotive's exports | Manufacturing's | together, of exports | of GDP | next |")
    print("|---|---|---|---|---|---|---|---|---|---|---|")
    for m in months:
        c = y[m]
        ex = {k: v["exportValue"] for k, v in s[m].items()}
        tot = sum(ex.values())
        a, mf = ex.get("Automotive", 0), ex.get("Manufacturing", 0)
        rest = sorted(((v, k) for k, v in ex.items() if k not in ("Automotive", "Manufacturing")), reverse=True)[:3]
        nxt = ", ".join("%s %s" % (k, pct(v, tot)) for v, k in rest if v > 0)
        # real: a month's GDP a head and the mean wage over the price index (100 at the start), the start's dollars
        real = c["gdp"] / c["months"] / c["population"] / (c["priceIndex"] / 100) if c["months"] and c["population"] else 0
        print("| m%d | %s | %.1f | %.2f | %.2f | %s | %s | %s | %s | %s | %s |" % (
            m, "{:,}".format(int(c["population"])), c["gdp"] / c["months"] / 1e6 if c["months"] else 0,
            real, c["meanWage"] / (c["priceIndex"] / 100), pct(c["exports"], c["gdp"]), pct(a, tot), pct(mf, tot), pct(a + mf, tot), pct(a + mf, c["gdp"]), nxt))
    for sec in MAKERS:
        print("\n## %s's books over its sales\n" % sec)
        print("| year to | sales / GDP | goods bought | of which consumables | payroll | power and water | upkeep and property tax | interest | tax | net | workers | sales a worker-month, 2025 US$ |")
        print("|---|---|---|---|---|---|---|---|---|---|---|---|")
        for m in months:
            b = s[m].get(sec)
            if not b or b["revenue"] <= 0:
                print("| m%d | none |" % m)
                continue
            r = b["revenue"]
            per = r / b["workers"] / y[m]["rate"] * 1000 / LEVEL if b["workers"] else 0
            print("| m%d | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s |" % (
                m, pct(r, y[m]["gdp"]), pct(b["inputs"], r), pct(b["consumables"], r), pct(b["payroll"], r),
                pct(b["electricity"] + b["water"], r), pct(b["maintenance"] + b["propertyTax"], r),
                pct(b["interest"], r), pct(b["tax"], r), pct(b["net"], r),
                "{:,}".format(int(b["workers"] / y[m]["months"])), "{:,}".format(int(per))))
    print("\n## The metal goods' prices, 2025 k$ a unit (the year's mean local / export price over the exchange factor and LEVEL), and their literals\n")
    print("| year to | good | local | export | import | literal export-import | band with nothing traded | made here (Mf, Au, HI) | exported | bought here | imported |")
    print("|---|---|---|---|---|---|---|---|---|---|---|")
    for m in months:
        mo = y[m]["months"]
        for good, d in g[m].items():
            if good in ("LUBRICANTS", "COPPER_ROD"):
                continue
            rate = y[m]["rate"] * LEVEL
            lo, hi = LIT[good][1], LIT[good][0]
            nb = n[m].get(good)
            made = ", ".join("%.0f" % (d[mk][0] / mo) for mk in MAKERS)
            exp = sum(d[mk][1] for mk in MAKERS) / mo
            bought = sum(d[mk][2] for mk in MAKERS) / mo
            imp = sum(d[mk][3] for mk in MAKERS) / mo
            print("| m%d | %s | %.3f | %.3f | %.3f | %.3f-%.3f | %s | %s | %.0f | %.0f | %.0f |" % (
                m, good, d["local"] / mo / rate, d["export"] / mo / rate, d["import"] / mo / rate, lo, hi,
                "%.3f-%.3f" % (nb[0] / LEVEL, nb[1] / LEVEL) if nb else "-", made, exp, bought, imp))
    print("\n## Each works as the planner weighs it (January of the year after; sales and bought at the month's prices, wages at the city's)\n")
    print("| month | works | standing | sales a month (D$k) | goods bought / sales | wages / sales | planner's month / cost |")
    print("|---|---|---|---|---|---|---|")
    for m in months:
        for sec, name, count, sales, bought, wages, profit, cost, cons in t.get(m, []):
            if sales <= 0:
                continue
            print("| m%d | %s | %d | %.0f | %s | %s | %s |" % (m, name, count, sales, pct(bought, sales), pct(wages, sales),
                                                       "%.3f" % (profit / cost) if cost == cost and cost > 0 else "-"))


if __name__ == "__main__":
    if sys.argv[1] == "ratios":
        ratios(sys.argv[2])
        sys.exit()
    ms = [int(x) for x in sys.argv[2].split(",")] if len(sys.argv) > 2 else [600, 1200, 1800, 2400]
    main(sys.argv[1], ms)
