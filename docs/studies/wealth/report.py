"""The wealth study's report tables, from WealthRun CSVs (docs/studies/spec-wealth.md section 2).

    python -I report.py OUT.txt RUN.csv [RUN.csv ...]

Every figure is in years of GDP (the year's GDP summed off the national
accounts), so the price level cancels: a stock over a year's output, a flow
over the output of the same months. Plain Python, no packages.
"""
import csv
import sys

MARKS = (1200, 2400, 3600, 4800, 6000, 7200, 7992)


def load(path):
    with open(path, newline="") as f:
        return list(csv.DictReader(f))


def v(r, k):
    try:
        return float(r[k])
    except (KeyError, ValueError, TypeError):
        return float("nan")


def positions(r):
    hh = v(r, "hhSavings") + v(r, "hhPaper") + v(r, "hhBonds") + v(r, "hhAbroad") - v(r, "hhDebt") - v(r, "studentLoans")
    biz = v(r, "bizCash") + v(r, "orderBook") + v(r, "bizAbroad") + v(r, "bizPaper") - v(r, "bizLoans") - v(r, "bizBonds")
    gov = v(r, "treasuryCash") + v(r, "fundCash") + v(r, "vault") + v(r, "studentLoans") - v(r, "cityDebt")
    return hh, biz, gov, v(r, "bankEquity"), v(r, "cbAdvBank") + v(r, "cbAdvTreasury") + v(r, "cbPaper") - v(r, "m0")


def span(rows, lo, hi, key):
    return sum(v(r, key) for r in rows if lo < v(r, "month") <= hi)


def main():
    out = open(sys.argv[1], "w")
    p = lambda s="": print(s, file=out)
    for path in sys.argv[2:]:
        rows = load(path)
        by = {int(v(r, "month")): r for r in rows}
        p("== %s" % path.replace("\\", "/").split("/")[-1])
        p("STOCKS, years of GDP: money position (financial assets less debts, shares left out) and its parts")
        p("month      pop | households businesses government  bank   CB | treasury   fund  vault  debt | hh savings hh abroad | biz tills | city's share of the companies (rescue book)")
        for m in MARKS:
            r = by.get(m)
            if r is None:
                continue
            y = v(r, "y_gdp")
            hh, biz, gov, bank, cb = positions(r)
            eq = v(r, "eqCity") + v(r, "eqWorld") + v(r, "eqHouseholds")
            p("%5d %9.0f | %10.2f %10.2f %10.2f %5.2f %5.2f | %8.2f %6.2f %6.2f %5.2f | %10.2f %9.2f | %9.2f | %5.0f%% (%3.0f%%)" % (
                m, v(r, "pop"), hh / y, biz / y, gov / y, bank / y, cb / y,
                v(r, "treasuryCash") / y, v(r, "fundCash") / y, v(r, "vault") / y, v(r, "cityDebt") / y,
                v(r, "hhSavings") / y, v(r, "hhAbroad") / y, v(r, "bizCash") / y,
                100 * v(r, "eqCity") / eq if eq > 0 else float("nan"),
                100 * v(r, "eqCityRescue") / eq if eq > 0 else float("nan")))
        p()
        p("FLOWS over each period, years of the period's GDP")
        keys = [("trade", "net exports"), ("income", "income bal"), ("financial", "financial"),
                ("moneyMade", "CB made"), ("auditResidual", "audit gap"), ("hhResidual", "hh gap"),
                ("naC", "C"), ("naI", "I"), ("naG", "G"), ("revenue", "revenue"), ("expenses", "spending"),
                ("taxBusiness", "profit tax"), ("fundTransfer", "fund xfer"), ("treasuryDelta", "treasury +"),
                ("payroll", "payroll"), ("hhDisposable", "hh disp")]
        p("period      " + " ".join("%10s" % k[1] for k in keys))
        last = 0
        for m in MARKS:
            if m not in by:
                continue
            g = span(rows, last, m, "y_gdp")
            p("%5d-%-5d " % (last, m) + " ".join("%10.3f" % (span(rows, last, m, "y_" + k[0]) / g) for k in keys))
            last = m
        p()
        p("PRICES AND THE CURRENCY at each mark")
        p("month   index        fx/parity  policy  unemployment  real GDP (2015 US$)")
        for m in MARKS:
            r = by.get(m)
            if r is None:
                continue
            p("%5d %12.4g %8.3f %8.4f %8.3f %14.4g" % (m, v(r, "index"), v(r, "fx") / v(r, "parity"),
                                                    v(r, "policyRate"), v(r, "unemployment"), v(r, "realGdp")))
        p()
    out.close()


if __name__ == "__main__":
    main()
