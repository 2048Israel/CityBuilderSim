"""Builds employment.csv from the BLS public API v1 monthly CES copy in raw/bls-ces-2023-2025.json.
Annual average = mean of the 12 not-seasonally-adjusted monthly values (derived here: API v1 does not return BLS's own M13).
Run: python -I make_employment.py <real dir>"""
import csv, json, os, sys
base = sys.argv[1]
d = json.load(open(os.path.join(base, "raw", "bls-ces-2023-2025.json")))
ser = {}
for s in d["Results"]["series"]:
    by = {}
    for x in s["data"]:
        by.setdefault(x["year"], []).append(float(x["value"]))
    ser[s["seriesID"]] = {y: sum(v) / len(v) for y, v in by.items() if len(v) == 12}
NAMES = [
    ("CES0000000001", "Total nonfarm employment (thousands)", "thousands"),
    ("CES0500000001", "Total private employment (thousands)", "thousands"),
    ("CES4200000001", "Retail trade (NAICS 44-45) (thousands)", "thousands"),
    ("CES4245800001", "Clothing, clothing accessories, shoe, and jewelry retailers (458) (thousands)", "thousands"),
    ("CES4245810001", "Clothing and clothing accessories retailers (4581) (thousands)", "thousands"),
    ("CES4245820001", "Shoe retailers (4582) (thousands)", "thousands"),
    ("CES4245830001", "Jewelry, luggage, and leather goods retailers (4583) (thousands)", "thousands"),
    ("CES4245500001", "General merchandise retailers (455) (thousands)", "thousands"),
    ("CES4245510001", "Department stores (4551) (thousands)", "thousands"),
    ("CES4245520001", "Warehouse clubs, supercenters, other general merchandise retailers (4552) (thousands)", "thousands"),
    ("CES7072200001", "Food services and drinking places (722) (thousands)", "thousands"),
    ("CES0500000011", "Total private: average weekly earnings, all employees ($)", "dollars"),
    ("CES4200000011", "Retail trade: average weekly earnings, all employees ($)", "dollars"),
    ("CES0500000003", "Total private: average hourly earnings, all employees ($)", "dollars"),
    ("CES4200000003", "Retail trade: average hourly earnings, all employees ($)", "dollars"),
    ("CES0500000007", "Total private: average weekly hours, all employees", "hours"),
    ("CES4200000007", "Retail trade: average weekly hours, all employees", "hours"),
]
YEARS = ["2023", "2024", "2025"]
with open(os.path.join(base, "employment.csv"), "w", newline="") as f:
    w = csv.writer(f)
    w.writerow(["series_id", "item", "unit", "avg_2023", "avg_2024", "avg_2025", "note"])
    for sid, name, unit in NAMES:
        w.writerow([sid, name, unit] + ["%.2f" % ser[sid][y] if unit != "thousands" else "%.1f" % ser[sid][y] for y in YEARS] + ["annual average = mean of 12 NSA monthly values (derived)"])
    nf = ser["CES0000000001"]
    for sid, name, _ in NAMES[2:11]:
        if sid == "CES7072200001" or True:
            w.writerow(["derived", name.split(" (thousands)")[0] + " as % of total nonfarm", "percent"] + ["%.3f" % (100.0 * ser[sid][y] / nf[y]) for y in YEARS] + ["derived"])
print("ok")
