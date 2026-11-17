"""The luxury-budget study's tables (docs/studies/luxury-budget.md) from run.sh's CSVs.

    python -I docs/studies/luxury-budget/report.py DIR [NAME ...]

Reads DIR/NAME-city.csv and DIR/NAME-cells.csv (one row a year, LuxuryProbe) and
prints, for each run, the year to m240, 600, 1200, 2400 and the last: the budget
over the households' income and GDP, its wealth half, what the counter sold, the
shops' margin over GDP and their share of the workers; then the budget by cell
kind (working or retired, by tier) in the same years; then the medians from m240.
"""
import csv, sys, statistics

def rows(path):
    with open(path, newline="") as f:
        return list(csv.DictReader(f))

def num(r, k):
    try:
        return float(r[k])
    except (KeyError, ValueError):
        return float("nan")

def pct(a, b):
    return 100.0 * a / b if b else float("nan")

MARKS = (240, 600, 1200, 2400)

def city_line(r):
    g = lambda k: num(r, k)
    m = g("months") or 1
    hh_income = g("hhDisposable")
    va = g("lrRevenue") - g("lrInputs") - g("lrUtilities")
    return {
        "month": int(g("month")),
        "people": g("people") / m,
        "want/disp": pct(g("luxWant"), hh_income),
        "want/gdp": pct(g("luxWant"), g("gdp")),
        "wealth/want": pct(g("luxWealth"), g("luxWant")),
        "gated/want": pct(g("luxWantGated"), g("luxWant")),
        "spend/gdp": pct(g("luxSpend"), g("gdp")),
        "spend/C": pct(g("luxSpend"), g("consumption")),
        "spend/disp": pct(g("luxSpend"), hh_income),
        "sold/wanted": pct(g("luxSold"), g("luxWantedPieces")),
        "meal/gdp": pct(g("mealSpend"), g("gdp")),
        "C/gdp": pct(g("consumption"), g("gdp")),
        "disp/gdp": pct(hh_income, g("gdp")),
        "nw/gdp": g("netWorth") / m / g("gdp") if g("gdp") else float("nan"),
        "nw/disp": g("netWorth") / m / hh_income if hh_income else float("nan"),
        "lrVA/gdp": pct(va, g("gdp")),
        "lrPay/VA": pct(g("lrPayroll"), va),
        "lrW/staffed": pct(g("lrWorkers"), g("staffed")),
        "lrW/sectors": pct(g("lrWorkers"), g("sectorWorkers")),
        "retailW/sectors": pct(g("retailWorkers"), g("sectorWorkers")),
        "margin": g("lrMargin") / m,
        "boutiques": g("boutiques") / m,
        "dept": g("deptStores") / m,
        "lrW": g("lrWorkers") / m,
        "landed/wage": g("lrLanded") / m / g("meanWage") if g("meanWage") else float("nan"),
        "spend/person/wage": g("luxSpend") / m / (g("people") / m) / g("meanWage") if g("meanWage") else float("nan"),
    }

def pick(lines, marks):
    out = []
    last = lines[-1]["month"]
    for mk in list(marks) + [last]:
        best = min(lines, key=lambda l: abs(l["month"] - mk))
        if best not in out:
            out.append(best)
    return out

def fmt(v):
    if isinstance(v, int):
        return str(v)
    if v != v:
        return "-"
    if abs(v) >= 1000:
        return f"{v:,.0f}"
    if abs(v) >= 100:
        return f"{v:.0f}"
    return f"{v:.1f}"

def main():
    d = sys.argv[1]
    names = sys.argv[2:] or ["seed0", "seed1", "rotterdam"]
    for name in names:
        lines = [city_line(r) for r in rows(f"{d}/{name}-city.csv")]
        lines = [l for l in lines if l["month"] >= 12]
        keys = list(lines[0].keys())
        print(f"== {name}: the city, the year to each month (shares in %, stocks in years) ==")
        for l in pick(lines, MARKS):
            print("  " + "  ".join(f"{k}={fmt(l[k])}" for k in keys))
        late = [l for l in lines if l["month"] >= 240]
        print(f"  median from m240 ({len(late)} years): " + "  ".join(
            f"{k}={fmt(statistics.median([l[k] for l in late if l[k] == l[k]] or [float('nan')]))}"
            for k in keys if k != "month"))
        # The budget by kind of cell.
        cells = rows(f"{d}/{name}-cells.csv")
        months = sorted({int(float(r["month"])) for r in cells})
        marks = [min(months, key=lambda x: abs(x - mk)) for mk in list(MARKS) + [months[-1]]]
        print(f"== {name}: the budget by cell kind (share of the budget; budget over the kind's disposable; wealth half) ==")
        for mk in dict.fromkeys(marks):
            group = {}
            tot = 0.0
            for r in cells:
                if int(float(r["month"])) != mk:
                    continue
                kind = ("retired " if r["retired"] == "true" else "") + r["tier"]
                if r["kind"] not in ("WorkingHousehold", "RetiredHousehold", "Household"):
                    kind = r["kind"].replace("Household", "").lower() or kind
                a = group.setdefault(kind, [0.0, 0.0, 0.0, 0.0, 0.0])
                a[0] += num(r, "luxWant"); a[1] += num(r, "disposable"); a[2] += num(r, "luxWealth")
                a[3] += num(r, "households"); a[4] += num(r, "netWorth")
                tot += num(r, "luxWant")
            parts = []
            for k, a in sorted(group.items(), key=lambda kv: -kv[1][0]):
                if a[0] <= 0:
                    continue
                parts.append(f"{k}: {pct(a[0], tot):.0f}% / {pct(a[0], a[1]):.0f}% / {pct(a[2], a[0]):.0f}%")
            print(f"  m{mk}: " + "; ".join(parts))
        print()

if __name__ == "__main__":
    main()
