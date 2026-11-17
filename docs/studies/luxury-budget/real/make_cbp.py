"""Builds cbp-establishments.csv from the CBP 2023 national file (NAICS 2017 codes) in raw/cbp/cbp23us.zip.
Run: python -I make_cbp.py <real dir>. emp = mid-March employees, ap = annual payroll in $1,000."""
import csv, io, os, sys, zipfile
base = sys.argv[1]
z = zipfile.ZipFile(os.path.join(base, "raw", "cbp", "cbp23us.zip"))
rows = list(csv.reader(io.StringIO(z.read("cbp23us.txt").decode("utf-8", "replace"))))
h = rows[0]
ix = {k: h.index(k) for k in ("naics", "lfo", "est", "emp", "ap")}
LABEL = {"------": "Total for all sectors", "44----": "Retail trade (NAICS 44-45 combined)", "448///": "Clothing and clothing accessories stores",
         "4481//": "Clothing stores", "4482//": "Shoe stores", "4483//": "Jewelry, luggage, and leather goods stores", "448310": "Jewelry stores",
         "448320": "Luggage and leather goods stores", "452///": "General merchandise stores", "4522//": "Department stores", "452210": "Department stores",
         "452311": "Warehouse clubs and supercenters", "453///": "Miscellaneous store retailers", "4532//": "Office supplies, stationery, and gift stores"}
with open(os.path.join(base, "cbp-establishments.csv"), "w", newline="") as f:
    w = csv.writer(f)
    w.writerow(["naics_2017", "kind_of_business", "establishments_2023", "employees_mid_march_2023", "annual_payroll_2023_usd_thousands", "employees_per_establishment_derived"])
    for r in rows[1:]:
        n = r[ix["naics"]].strip()
        if r[ix["lfo"]] == "-" and n in LABEL:
            e, m = int(r[ix["est"]]), int(r[ix["emp"]])
            w.writerow([n.replace("/", "").replace("-", ""), LABEL[n], e, m, r[ix["ap"]], "%.1f" % (m / e)])
print("ok")
