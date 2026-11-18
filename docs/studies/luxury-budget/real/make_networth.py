"""Builds networth.csv from raw/z1zip/z1_csv_files.zip (Fed Z.1 2026:Q2 release, B.101 = S1M_b.csv) and the BEA income.csv.
Run: python -I make_networth.py <real dir>"""
import csv, io, os, sys, zipfile

base = sys.argv[1]
z = zipfile.ZipFile(os.path.join(base, "raw", "z1zip", "z1_csv_files.zip"))
rows = list(csv.reader(io.StringIO(z.read("csv/S1M_b.csv").decode("utf-8-sig"))))
hdr = rows[0]
data = {r[0]: r for r in rows[1:]}
dic = {}
for line in z.read("data_dictionary/S1M_b.txt").decode("utf-8", "replace").splitlines():
    p = line.split("\t")
    if len(p) >= 3:
        dic[p[0]] = (p[1], p[2])
LINES = ("Line 1", "Line 2", "Line 3", "Line 8", "Line 9", "Line 22", "Line 25", "Line 29", "Line 32", "Line 42", "Line 51", "Line 52")
want = [c for c, (lab, ln) in dic.items() if ln in LINES]
PERIODS = ["2023:Q4", "2024:Q4", "2025:Q4", "2025:Q2", "2026:Q2"]
with open(os.path.join(base, "networth.csv"), "w", newline="") as f:
    w = csv.writer(f)
    w.writerow(["series", "label", "line", "2023Q4_musd", "2024Q4_musd", "2025Q4_musd", "2025Q2_musd", "2026Q2_musd"])
    for c in want:
        if c not in hdr:
            continue
        i = hdr.index(c)
        w.writerow([c, dic[c][0], dic[c][1]] + [data[p][i] if p in data else "" for p in PERIODS])
    # derived: year-end net worth over the BEA annual GDP and DPI of the same calendar year (income.csv)
    inc = {}
    for r in csv.reader(open(os.path.join(base, "income.csv"))):
        if r[0] in ("1.1.5", "2.1") and r[1] in ("1", "27"):
            inc[(r[0], r[1])] = [float(x) for x in r[3:6]]
    nw = [float(data[p][hdr.index("FL152090005.Q")]) for p in PERIODS[:3]]
    gdp, dpi = inc[("1.1.5", "1")], inc[("2.1", "27")]
    w.writerow(["derived", "net worth (year-end) / GDP (calendar year), ratio", "", *["%.3f" % (a / b) for a, b in zip(nw, gdp)], "", ""])
    w.writerow(["derived", "net worth (year-end) / DPI (calendar year), ratio", "", *["%.3f" % (a / b) for a, b in zip(nw, dpi)], "", ""])
    w.writerow(["derived", "net worth (year-end) in billions of dollars", "", *["%.1f" % (a / 1000.0) for a in nw], "", ""])
print(len(want))
