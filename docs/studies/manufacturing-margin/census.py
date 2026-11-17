"""The Economic Census's books for the manufacturing margin study (MA1, docs/studies/manufacturing-margin.md).

    python -I docs/studies/manufacturing-margin/census.py basic EC2231BASIC.dat OUT.csv
    python -I docs/studies/manufacturing-margin/census.py matfuel EC2231MATFUEL.dat OUT.csv

The files are the 2022 Economic Census's manufacturing tables (sector 31), unzipped from
    https://www2.census.gov/programs-surveys/economic-census/data/2022/sector31/EC2231BASIC.zip
    https://www2.census.gov/programs-surveys/economic-census/data/2022/sector31/EC2231MATFUEL.zip
(the 2017 ones, EC1731BASIC, are read the same way). They are untrusted downloads: keep them in a
folder of their own and run this with -I. "basic" writes, for the United States and each industry in
CODES, the shipments and their costs in US$ millions and each cost over shipments, and the gross value of
its depreciable assets at the year's end (at what they cost when bought) and its year's capital spending,
each over a year's shipments; "matfuel" writes
each industry's materials consumed by kind, US$ millions and their share of its total materials.
Source: U.S. Census Bureau, 2022 Economic Census (US public domain; docs/studies/licences.md).
"""
import csv
import sys

CODES = ["31-33", "331110", "331420", "332", "3323", "332311", "332312", "332313", "3327", "332710",
         "333", "3331", "3335", "3336", "3339", "3361", "336110", "336111", "336112", "336120", "3362",
         "3363", "336510", "335929", "3359"]
FIELDS = ["RCPTOT", "CSTMTOT", "CSTMPRT", "CSTRSL", "CSTCNT", "CSTFU", "CSTELEC", "PAYANN", "BENEFIT",
          "PCHTT", "RPTOT", "DPRTOT", "VALADD", "EMP", "ASTEND", "CEXTOT"]


def rows(path):
    with open(path, encoding="latin-1", newline="") as fh:
        head = fh.readline().lstrip("#").strip().split("|")
        for line in fh:
            yield dict(zip(head, line.rstrip("\n").split("|")))


def num(x):
    try:
        return float(x)
    except ValueError:
        return float("nan")


def basic(path, out):
    seen = {}
    for r in rows(path):
        if r.get("GEOTYPE") != "01":
            continue
        code = r.get("NAICS2022")
        if code in CODES and code not in seen:
            seen[code] = r
    with open(out, "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["naics", "industry", "year"] + [f + "_musd" for f in FIELDS] +
                   ["materials_over_shipments", "payroll_over_shipments", "pay_and_benefits_over_shipments",
                    "other_expenses_over_shipments", "value_added_over_shipments", "cash_margin_over_shipments",
                    "shipments_per_employee_kusd", "assets_over_shipments", "capital_spending_over_shipments"])
        for code in CODES:
            r = seen.get(code)
            if r is None:
                continue
            v = {f: num(r.get(f, "")) / 1000 for f in FIELDS}
            v["EMP"] *= 1000
            s = v["RCPTOT"]
            other = v["PCHTT"] + v["RPTOT"]
            cash = s - v["CSTMTOT"] - v["PAYANN"] - v["BENEFIT"] - other
            w.writerow([code, r.get("NAICS2022_TTL") or r.get("NAICS2022_LABEL"), r.get("YEAR")] +
                       [round(v[f], 1) for f in FIELDS] +
                       [round(v["CSTMTOT"] / s, 4), round(v["PAYANN"] / s, 4),
                        round((v["PAYANN"] + v["BENEFIT"]) / s, 4), round(other / s, 4),
                        round(v["VALADD"] / s, 4), round(cash / s, 4), round(s * 1000 / v["EMP"], 1),
                        round(v["ASTEND"] / s, 4), round(v["CEXTOT"] / s, 4)])


def matfuel(path, out):
    by = {}
    for r in rows(path):
        if r.get("GEOTYPE") != "01" or r.get("NAICS2022") not in CODES:
            continue
        by.setdefault(r["NAICS2022"], []).append(r)
    with open(out, "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["naics", "industry", "code", "material", "cost_musd", "share_of_total_materials"])
        for code in CODES:
            items = by.get(code, [])
            total = next((num(r["MATFUELCOST"]) for r in items if r["PROD_LABEL"].strip() == "Total Materials"), float("nan"))
            for r in items:
                c = num(r["MATFUELCOST"])
                w.writerow([code, r["NAICS2022_LABEL"], r["MATFUEL"], r["PROD_LABEL"], round(c / 1000, 1),
                            round(c / total, 4) if total == total and total > 0 and c == c else ""])


if __name__ == "__main__":
    {"basic": basic, "matfuel": matfuel}[sys.argv[1]](sys.argv[2], sys.argv[3])
