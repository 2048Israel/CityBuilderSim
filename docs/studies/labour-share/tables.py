"""The labour-share study's summary tables (LS1, docs/studies/spec-labour-share.md), from the runs' CSVs.

    python -I docs/studies/labour-share/tables.py decompose BEA.csv RUN.csv [RUN.csv ...]
    python -I docs/studies/labour-share/tables.py costs BEA.csv RUN.csv [RUN.csv ...]
    python -I docs/studies/labour-share/tables.py size BEA.csv RUN.csv [RUN.csv ...]
    python -I docs/studies/labour-share/tables.py city BEA.csv RUN.csv [RUN.csv ...]

BEA.csv is bea-compensation-share.csv; bea-va-gross-output.csv is read beside it. Each run is read
at m600, 1,200, 2,400 and 4,000, the last year row at or before each, as report.py reads it.

decompose: each sector's payroll over its value added, the median over every run and month, beside
BEA's industry, and the gap's two halves: payroll over revenue against BEA's compensation over gross
output (the posts and the wage), and value added over revenue against BEA's value added over gross
output (the markup over inputs); the share over BEA's is the first half over the second. Then value
added per worker in the city's mean wage, beside what BEA's share would make it at the game's pay
per worker (pay per worker over BEA's share).
costs: each sector's cost lines over its revenue (inputs, utilities, payroll, taxes, the net left),
beside BEA's intermediate inputs over gross output.
size: each sector's share of the city's value added (a loss counted as none), payroll and workers,
beside BEA's value added and compensation shares of the industries the game's sectors map to.
city: the city's payroll over its value added, and without Copper Refining where it stands; its
three largest sectors by value added; household wages over GDP; BEA's GDP and private industries.

LS1's own study code, not the game's; report.py's functions are loaded from beside this file.
"""
import csv
import importlib.util
import statistics as st
import sys
from pathlib import Path

_spec = importlib.util.spec_from_file_location("report", Path(__file__).parent / "report.py")
R = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(R)
MONTHS = (600, 1200, 2400, 4000)


def years(path):
    """(run, month, that year's rows) at each of MONTHS the run reached."""
    rows = list(csv.DictReader(open(path, newline="")))
    have = sorted({int(r["month"]) for r in rows})
    for asked in MONTHS:
        m = R.at(have, asked)
        if m is not None and asked - m < 12:
            yield Path(path).stem, m, [r for r in rows if int(r["month"]) == m]


def z(r, key):
    x = R.num(r, key)
    return 0.0 if x != x else x


def va(r):
    return z(r, "revenue") - sum(z(r, k) for k in ("inputs", "electricity", "water", "maintenance"))


def decompose(shares, go, runs):
    seen = {}
    for path in runs:
        for _, _, year in years(path):
            for r in year:
                s, code = R.shares_of(r), R.BEA_OF.get(r["sector"])
                if not s or s[0] is None or code not in go or code not in shares:
                    continue
                gva, gpay = go[code]
                b = shares[code] / 100
                seen.setdefault(r["label"], []).append((s[0], s[2] / gpay, s[3] / gva, s[4] * s[3], s[5] / b, b))
    print("share: payroll over value added, %, the median over every run and month (n); BEA: compensation over")
    print("value added, 2023-2025; gap = share / BEA = (pay/rev / BEA's comp/GO) / (va/rev / BEA's va/GO);")
    print("va/wkr: value added per worker in the city's mean wage; BEA va/wkr: what BEA's share makes it.")
    print("For Retail and Luxury Retail, rev is the margin (sales less the goods bought), as BEA's GO is for trade.")
    print(f"{'sector':18} {'n':>2} {'share':>6} {'BEA':>5} {'gap':>5} {'pay/rev/BEA':>11} {'va/rev/BEA':>10} "
          f"{'va/wkr':>7} {'BEA va/wkr':>10}")
    for label, xs in sorted(seen.items(), key=lambda kv: st.median(x[0] for x in kv[1]) / kv[1][0][5]):
        med = lambda i: st.median(x[i] for x in xs)
        b = xs[0][5]
        print(f"{label[:18]:18} {len(xs):2} {100 * med(0):6.1f} {100 * b:5.1f} {med(0) / b:5.2f} {med(1):11.2f} "
              f"{med(2):10.2f} {med(3):7.1f} {med(4):10.1f}")


def size(go_path, runs):
    go = {r["code"]: r for r in csv.DictReader(open(go_path, newline=""))}
    codes = {c for c in R.BEA_OF.values() if c in go}
    tva = sum(float(go[c]["value_added"]) for c in codes)
    tcp = sum(float(go[c]["compensation"]) for c in codes)
    print("Each sector's share of the city's value added, payroll and workers, %; then BEA's value added and")
    print("compensation shares of the industries the game maps to, 2023-2025 (an industry two sectors share")
    print("is printed beside both).")
    for path in runs:
        for name, m, year in years(path):
            v = sum(max(0, va(r)) for r in year)
            p = sum(z(r, "payroll") for r in year)
            w = sum(z(r, "workers") for r in year)
            print(f"\n== {name}, the year to m{m}\n  {'sector':18} {'va':>5} {'pay':>5} {'wkrs':>5}   {'BEA va':>6} {'comp':>5}")
            for r in sorted(year, key=lambda r: -max(0, va(r))):
                if va(r) <= 0 and z(r, "payroll") <= 0:
                    continue
                g = go.get(R.BEA_OF.get(r["sector"], ""))
                bv = 100 * float(g["value_added"]) / tva if g else float("nan")
                bc = 100 * float(g["compensation"]) / tcp if g else float("nan")
                print(f"  {r['label'][:18]:18} {100 * max(0, va(r)) / v:5.1f} {100 * z(r, 'payroll') / p:5.1f} "
                      f"{100 * z(r, 'workers') / w:5.1f}   {bv:6.1f} {bc:5.1f}")


def city(shares, runs):
    print(f"BEA, compensation over value added, 2023-2025: GDP {shares['GDP']:.1f}%, private industries "
          f"{shares['PVT']:.1f}%.")
    print("The city's payroll over its value added (every sector's, a loss counted as one); without Copper")
    print("Refining where it stands; household wages over GDP; the three largest sectors' share of value added.")
    for path in runs:
        for name, m, year in years(path):
            pay, v = sum(z(r, "payroll") for r in year), sum(va(r) for r in year)
            cr = [r for r in year if r["sector"] == "Copper Refining" and va(r) > 0]
            bare = ""
            if cr:
                bare = f", without Copper Refining {100 * (pay - z(cr[0], 'payroll')) / (v - va(cr[0])):5.1f}%"
            c = year[0]
            top = sorted(year, key=lambda r: -va(r))[:3]
            print(f"{name:16} m{m:<5} pay/va {100 * pay / v:5.1f}%{bare}; household wages/GDP "
                  f"{100 * z(c, 'hhWages') / z(c, 'gdp'):5.1f}%; top: "
                  + ", ".join(f"{r['label']} {100 * va(r) / v:.0f}%" for r in top))


def costs(go, runs):
    seen = {}
    for path in runs:
        for _, _, year in years(path):
            for r in year:
                rev = z(r, "revenue")
                if rev > 0:
                    lines = (z(r, "inputs"), sum(z(r, k) for k in ("electricity", "water", "maintenance")),
                             z(r, "payroll"), sum(z(r, k) for k in ("salesTax", "propertyTax", "tax")), z(r, "net"))
                    seen.setdefault((r["label"], r["sector"]), []).append([x / rev for x in lines])
    print("Each cost line over revenue: the median, then the lowest and highest, over every run and month (n).")
    print("taxes: sales, property and profit tax; net: after interest and tax. BEA in: intermediate inputs over")
    print("gross output, 2023-2025 (one less value added over gross output), beside inputs and utilities summed;")
    print("none for Retail and Luxury Retail, whose BEA gross output is the margin, not sales.")
    print(f"{'sector':18} {'n':>2} {'inputs':>15} {'utilities':>15} {'payroll':>15} {'taxes':>15} {'net':>16} {'BEA in':>6}")
    for (label, key), xs in sorted(seen.items()):
        cell = lambda i: (f"{st.median(x[i] for x in xs):4.2f} {min(x[i] for x in xs):4.2f}-"
                          f"{max(x[i] for x in xs):4.2f}")
        code = R.BEA_OF.get(key, "")
        g = go.get(code) if code not in R.TRADE else None
        print(f"{label[:18]:18} {len(xs):2} " + " ".join(f"{cell(i):>15}" for i in range(4))
              + f" {cell(4):>16} {1 - g[0] if g else float('nan'):6.2f}")


def main(argv):
    what, bea_path, runs = argv[0], argv[1], argv[2:]
    go_path = str(Path(bea_path).with_name("bea-va-gross-output.csv"))
    shares = R.bea(bea_path)
    if what == "decompose":
        decompose(shares, R.bea_go(go_path), runs)
    elif what == "costs":
        costs(R.bea_go(go_path), runs)
    elif what == "size":
        size(go_path, runs)
    elif what == "city":
        city(shares, runs)
    else:
        sys.exit(__doc__)


if __name__ == "__main__":
    main(sys.argv[1:])
