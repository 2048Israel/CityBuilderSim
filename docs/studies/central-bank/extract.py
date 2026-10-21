r"""Reads a CityBuilderSim history save (read-only) and writes the columns the
central-bank study's model reads, one row a month. Run it on a COPY or on the
real file: it only reads. Slot 3 ("for claude", Halifax, 0.8.10) is the one the
study uses; its output is halifax.csv beside this file.

    python -I extract.py "%APPDATA%\CityBuilderSim\saves\slot-03-history.json" halifax.csv
"""
import csv, json, sys

COLUMNS = ["priceIndex", "expectedInflation", "credibility", "policyRate", "revenue", "remittance", "advancesToTreasury",
           "m0", "cash", "bankDeposits", "bankLent", "fundValue"]

def main(src, out):
    h = json.load(open(src, encoding="utf-8"))
    months = h["month"]
    n = len(months)
    def col(k):
        a = h.get(k) or []
        return [None] * (n - len(a)) + a if len(a) < n else a[len(a) - n:]
    cols = {k: col(k) for k in COLUMNS}
    with open(out, "w", newline="") as f:
        w = csv.writer(f, lineterminator="\n")
        w.writerow(["month"] + COLUMNS)
        for i in range(n):
            w.writerow([months[i]] + ["" if cols[k][i] is None else cols[k][i] for k in COLUMNS])
    print("wrote", n, "months to", out)

if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
