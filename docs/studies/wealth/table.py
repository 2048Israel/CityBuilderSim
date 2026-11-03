"""The wealth study's tables, from WealthRun's CSV (docs/studies/spec-wealth.md).

    python -I table.py RUN.csv [every_months]

Prints, every `every_months` (default 1200, a century), each sector's money
position in years of GDP (the year's GDP, summed off the national accounts),
and the flows that fed it over the period, also in years of that period's GDP.
Plain Python, no packages.
"""
import csv
import sys


def rows(path):
    with open(path, newline="") as f:
        out = []
        for r in csv.DictReader(f):
            d = {}
            for k, v in r.items():
                try:
                    d[k] = float(v) if v not in ("", None) else float("nan")
                except ValueError:
                    d[k] = v
            out.append(d)
        return out


def g(r, k):
    v = r.get(k, float("nan"))
    return v if isinstance(v, float) else float("nan")


def positions(r):
    """Each sector's money position (financial assets less debts, shares left out), local money."""
    hh = g(r, "hhSavings") + g(r, "hhPaper") + g(r, "hhBonds") + g(r, "hhAbroad") - g(r, "hhDebt") - g(r, "studentLoans")
    biz = g(r, "bizCash") + g(r, "orderBook") + g(r, "bizAbroad") + g(r, "bizPaper") - g(r, "bizLoans") - g(r, "bizBonds")
    gov = g(r, "treasuryCash") + g(r, "fundCash") + g(r, "vault") + g(r, "studentLoans") - g(r, "cityDebt")
    bank = g(r, "bankEquity")
    cb = g(r, "cbAdvBank") + g(r, "cbAdvTreasury") + g(r, "cbPaper") - g(r, "m0")
    return {"households": hh, "businesses": biz, "government": gov, "bank": bank, "central bank": cb}


def main():
    data = rows(sys.argv[1])
    every = int(sys.argv[2]) if len(sys.argv) > 2 else 1200
    pick = [r for r in data if int(r["month"]) % every == 0]
    print("month   pop        gdp/yr      hh     biz    gov    bank   cb    | treasury cash  hh savings  biz cash   (years of GDP)")
    for r in pick:
        y = g(r, "y_gdp")
        p = positions(r)
        print("%6d %10.0f %12.4g %6.2f %6.2f %6.2f %6.2f %6.2f | %6.2f %6.2f %6.2f" % (
            r["month"], r["pop"], y, p["households"] / y, p["businesses"] / y, p["government"] / y,
            p["bank"] / y, p["central bank"] / y, g(r, "treasuryCash") / y, g(r, "hhSavings") / y,
            g(r, "bizCash") / y))
    print()
    print("flows over each period, in years of the period's GDP (sum of y_* columns)")
    keys = ["trade", "income", "financial", "valuation", "reserveSold", "moneyMade", "auditResidual",
            "hhResidual", "hhNewcomerEst", "hhTakenAway", "hhSent", "hhHome", "revenue", "expenses",
            "treasuryDelta", "remittance"]
    print("period        " + " ".join("%11s" % k[:11] for k in keys))
    last = 0
    for r in pick:
        m = int(r["month"])
        span = [x for x in data if last < int(x["month"]) <= m]
        gdp = sum(g(x, "y_gdp") for x in span)
        vals = [sum(g(x, "y_" + k) for x in span) / gdp if gdp else float("nan") for k in keys]
        print("%5d-%-6d  " % (last, m) + " ".join("%11.4f" % v for v in vals))
        last = m


if __name__ == "__main__":
    main()
