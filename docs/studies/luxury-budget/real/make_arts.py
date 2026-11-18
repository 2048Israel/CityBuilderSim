"""Builds arts-margins.csv from the Census ARTS 2022 (restated, NAICS 2017) tables in raw/arts/ (sales, gm, gmper).
Run: python -I make_arts.py <real dir>. Needs xlsxlib.py beside it."""
import csv, os, re, sys
here = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, here)
import xlsxlib

base = sys.argv[1]

NAMES = {}

def table(name):
    rows = list(xlsxlib.sheets(os.path.join(base, "raw", "arts", name + ".xlsx")).values())[0]
    hdr = None
    out = {}
    order = []
    names = NAMES
    for r in rows:
        if r and r[0].strip() == "NAICS Code":
            hdr = r
            continue
        if hdr is None or len(r) < 3 or not r[1].strip():
            continue
        label = re.sub(r"[^\x20-\x7e]|\.{2,}", "", r[1]).strip().rstrip(" .")
        if label.startswith(("S -", "Note", "Footnotes", "1Est", "2GAFO", "rRev")):
            continue
        code = r[0].strip()
        if not code:
            low = label.lower()
            code = "GAFO" if "gafo" in low else ("TOTAL_EXCL_MV" if "excl" in low else "TOTAL")
            label = {"GAFO": "GAFO (442, 443, 448, 451, 452, 4532)", "TOTAL_EXCL_MV": "Retail total excl. motor vehicle and parts dealers", "TOTAL": "Retail total"}[code]
        key = (code, label)
        names.setdefault(code, label)
        out[code] = {hdr[i].strip(): r[i].strip() for i in range(2, len(hdr)) if i < len(r)}
        order.append(code)
    return order, out

order, sales = table("sales")
_, gm = table("gm")
_, gmp = table("gmper")
YEARS = ["2022", "2021", "2020", "2019"]
with open(os.path.join(base, "arts-margins.csv"), "w", newline="") as f:
    w = csv.writer(f)
    h = ["naics_2017", "kind_of_business"]
    for y in YEARS:
        h += ["sales_%s_musd" % y, "gross_margin_%s_musd" % y, "gm_pct_published_%s" % y, "gm_pct_derived_gm_over_sales_%s" % y, "markup_multiple_derived_%s" % y]
    w.writerow(h)
    keys = list(order)
    for k in gmp:
        if k not in keys:
            keys.append(k)
    for k in keys:
        row = [k, NAMES[k]]
        have = False
        for y in YEARS:
            s = sales.get(k, {}).get(y, "")
            g = gm.get(k, {}).get(y, "")
            p = gmp.get(k, {}).get(y, "")
            if k not in gmp and k not in gm and k[0] == "":
                pass
            der = mk = ""
            try:
                der = "%.1f" % (100.0 * float(g) / float(s))
            except ValueError:
                pass
            pv = ""
            try:
                pv = "%.1f" % float(p)
                mk = "%.2f" % (1.0 / (1.0 - float(p) / 100.0))
            except ValueError:
                pv = p  # 'S' = not published
            row += [s, g, pv, der, mk]
            have = have or bool(s or g or p)
        if have and (k in gmp or k in gm or k in sales):
            w.writerow(row)
print("ok")
