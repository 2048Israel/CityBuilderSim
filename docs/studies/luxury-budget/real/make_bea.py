"""Builds pce.csv and income.csv from the DBnomics copies of BEA tables in raw/ (sys.argv[1] = real/ directory)."""
import csv, json, os, re, sys

base = sys.argv[1]
YEARS = ["2023", "2024", "2025"]

def load(name, metric="millions-of-current-dollars"):
    d = json.load(open(os.path.join(base, "raw", name)))
    out = {}
    for x in d["series"]["docs"]:
        if x["dimensions"]["metric"] != metric:
            continue
        m = re.search(r"\(line (\d+)\)", x["series_name"])
        if not m:
            continue
        p = dict(zip(x["period"], x["value"]))
        out[int(m.group(1))] = (re.sub(r"\s*\(line \d+\).*$", "", x["series_name"]), [p.get(y) for y in YEARS])
    return out

# ---- pce.csv : Table 2.4.5U ----
u = load("u20405-annual.json")
WANT = [1, 2, 3, 4, 23, 38, 62, 63, 64, 65, 66, 70, 71, 72, 73, 104, 113, 120, 121, 126, 131, 137, 138, 139, 140, 141, 142,
        150, 190, 209, 234, 235, 280, 307, 308, 311]
with open(os.path.join(base, "pce.csv"), "w", newline="") as f:
    w = csv.writer(f)
    w.writerow(["line", "item", "2023_musd", "2024_musd", "2025_musd", "share_of_PCE_2023_pct_derived",
                "share_of_PCE_2024_pct_derived", "share_of_PCE_2025_pct_derived"])
    tot = u[1][1]
    for ln in WANT:
        name, v = u[ln]
        sh = ["" if (a is None) else "%.3f" % (100.0 * a / t) for a, t in zip(v, tot)]
        w.writerow([ln, name] + ["" if a is None else int(a) for a in v] + sh)

# ---- income.csv : Table 2.1 and 1.1.5 ----
t21 = load("NIPA-T20100-annual.json")
t115 = load("NIPA-T10105-annual.json")
rows = [("2.1", 1), ("2.1", 14), ("2.1", 15), ("2.1", 26), ("2.1", 27), ("2.1", 28), ("2.1", 29), ("2.1", 34), ("1.1.5", 1)]
with open(os.path.join(base, "income.csv"), "w", newline="") as f:
    w = csv.writer(f)
    w.writerow(["table", "line", "item", "2023_musd", "2024_musd", "2025_musd"])
    for t, ln in rows:
        name, v = (t21 if t == "2.1" else t115)[ln]
        w.writerow([t, ln, name] + [int(a) for a in v])
    # derived
    dpi, saving, gdp = t21[27][1], t21[34][1], t115[1][1]
    pce = t21[29][1]
    w.writerow(["derived", "", "personal saving rate = saving / DPI (pct)"] + ["%.2f" % (100.0 * s / d) for s, d in zip(saving, dpi)])
    w.writerow(["derived", "", "PCE / GDP (pct)"] + ["%.2f" % (100.0 * p / g) for p, g in zip(pce, gdp)])
    w.writerow(["derived", "", "DPI / GDP (pct)"] + ["%.2f" % (100.0 * d / g) for d, g in zip(dpi, gdp)])
    w.writerow(["derived", "", "PCE / DPI (pct)"] + ["%.2f" % (100.0 * p / d) for p, d in zip(pce, dpi)])
print("ok")
