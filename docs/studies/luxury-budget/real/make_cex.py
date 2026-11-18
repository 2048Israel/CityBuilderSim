"""Builds cex-quintiles.csv from BLS CE 2025 tables in raw/cex/ (quintile Table 1101, decile Table 1110, all-CU detail Table 2500).
Shares published by BLS are shares of average annual EXPENDITURES; 'income' shares here are derived (mean item / mean income before taxes):
the current BLS CE tables carry no income-after-taxes row. Run: python -I make_cex.py <real dir>"""
import csv, os, re, sys
here = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, here)
import xlsxlib
base = sys.argv[1]

def parse(path):
    rows = list(xlsxlib.sheets(path).values())[0]
    out, cur = {}, None
    for r in rows:
        if not r or not r[0].strip():
            continue
        k = r[0].strip()
        if k in ("Mean", "Share", "SE", "RSE", "Percent Reporting"):
            if cur is not None:
                out.setdefault(cur, {}).setdefault(k, r[1:])
        else:
            cur = re.sub(r"\s*\[[DI]\]$", "", k)
    return out

q = parse(os.path.join(base, "raw", "cex", "cu-income-quintiles-before-taxes-2025.xlsx"))
dc = parse(os.path.join(base, "raw", "cex", "cu-income-deciles-before-taxes-2025.xlsx"))
allcu = parse(os.path.join(base, "raw", "cex", "cu-all-detail-2025.xlsx"))

def num(x):
    try:
        return float(x)
    except (ValueError, TypeError):
        return None

QCOLS = ["all_cu", "lowest_20", "second_20", "third_20", "fourth_20", "highest_20"]
ITEMS = ["Income before taxes", "Average annual expenditures", "Food at home", "Food away from home", "Alcoholic beverages", "Housing", "Household furnishings and equipment",
         "Apparel and services", "Footwear", "Transportation", "Vehicle purchases (net outlay)", "Entertainment", "Fees and admissions", "Audio and visual equipment and services",
         "Pets, toys, hobbies, and playground equipment", "Other entertainment supplies, equipment, and services", "Personal care products and services", "Reading",
         "Tobacco products and smoking supplies", "Healthcare", "Cash contributions", "Personal insurance and pensions"]
with open(os.path.join(base, "cex-quintiles.csv"), "w", newline="") as f:
    w = csv.writer(f)
    w.writerow(["section", "item", "measure"] + QCOLS)
    # published means and shares of expenditures (quintiles of income before taxes, 2025)
    for it in ITEMS:
        for meas in ("Mean", "Share"):
            v = q.get(it, {}).get(meas)
            if v is None:
                continue
            w.writerow(["published_quintile_2025", it, "mean_usd" if meas == "Mean" else "share_of_expenditures_pct"] + v[:6])
    inc = [num(x) for x in q["Income before taxes"]["Mean"][:6]]
    exp = [num(x) for x in q["Average annual expenditures"]["Mean"][:6]]
    # derived shares of income before taxes
    def share(it):
        m = [num(x) for x in q[it]["Mean"][:6]]
        return m
    groups = {
        "apparel_and_services": ["Apparel and services"],
        "household_furnishings_and_equipment": ["Household furnishings and equipment"],
        "entertainment": ["Entertainment"],
        "personal_care_products_and_services": ["Personal care products and services"],
        "DISCRETIONARY_BROAD_apparel+furnishings+entertainment+personal_care": ["Apparel and services", "Household furnishings and equipment", "Entertainment", "Personal care products and services"],
        "DISCRETIONARY_GOODSISH_apparel+furnishings+toys_hobbies+AV_equip+personal_care": ["Apparel and services", "Household furnishings and equipment", "Pets, toys, hobbies, and playground equipment", "Audio and visual equipment and services", "Personal care products and services"],
        "DISCRETIONARY_NARROW_apparel+footwear-incl+furnishings+personal_care": ["Apparel and services", "Household furnishings and equipment", "Personal care products and services"],
    }
    w.writerow(["derived_income_before_taxes_pct", "income before taxes (usd, published)", "mean_usd"] + ["%.0f" % x for x in inc])
    w.writerow(["derived_income_before_taxes_pct", "average annual expenditures / income before taxes", "pct"] + ["%.1f" % (100 * e / i) for e, i in zip(exp, inc)])
    for g, its in groups.items():
        tot = [sum(share(it)[k] for it in its) for k in range(6)]
        w.writerow(["derived_income_before_taxes_pct", g, "mean_usd_sum"] + ["%.0f" % t for t in tot])
        w.writerow(["derived_income_before_taxes_pct", g, "pct_of_income_before_taxes"] + ["%.2f" % (100 * t / i) for t, i in zip(tot, inc)])
        w.writerow(["derived_expenditure_pct", g, "pct_of_expenditures"] + ["%.2f" % (100 * t / e) for t, e in zip(tot, exp)])
    # deciles: top and bottom, only the items needed
    DCOLS = ["all_cu", "lowest_10", "second_10", "third_10", "fourth_10", "fifth_10", "sixth_10", "seventh_10", "eighth_10", "ninth_10", "highest_10"]
    w.writerow([])
    w.writerow(["section", "item", "measure"] + DCOLS)
    dinc = [num(x) for x in dc["Income before taxes"]["Mean"][:11]]
    dexp = [num(x) for x in dc["Average annual expenditures"]["Mean"][:11]]
    for it in ["Income before taxes", "Average annual expenditures", "Household furnishings and equipment", "Apparel and services", "Entertainment", "Personal care products and services"]:
        w.writerow(["published_decile_2025", it, "mean_usd"] + dc[it]["Mean"][:11])
    tot = [sum(num(dc[it]["Mean"][k]) for it in groups["DISCRETIONARY_BROAD_apparel+furnishings+entertainment+personal_care"]) for k in range(11)]
    w.writerow(["derived_decile", "DISCRETIONARY_BROAD", "pct_of_income_before_taxes"] + ["%.2f" % (100 * t / i) for t, i in zip(tot, dinc)])
    w.writerow(["derived_decile", "DISCRETIONARY_BROAD", "pct_of_expenditures"] + ["%.2f" % (100 * t / e) for t, e in zip(tot, dexp)])
    # all consumer units, detailed (jewelry, watches, luggage, cosmetics)
    w.writerow([])
    w.writerow(["section", "item", "measure", "all_cu_mean_usd", "percent_reporting", "aggregate_usd_bn_derived_mean_x_137994k_CUs", "BEA_PCE_2025_usd_bn", "CE_as_pct_of_PCE_derived"])
    CU = 137994.0
    pce = {"Jewelry": 87.792, "Watches": 18.426, "Luggage": 51.032, "Cosmetics, perfume, and bath preparations": 75.854, "Apparel and services": 559.768 + 23.784 + 106.218,
           "Footwear": 120.733, "Household furnishings and equipment": None}
    for it in ["Jewelry", "Watches", "Luggage", "Cosmetics, perfume, and bath preparations", "Footwear", "Apparel and services"]:
        m = num(allcu[it]["Mean"][0]) if it in allcu else None
        pr = allcu[it].get("Percent Reporting", [""])[0] if it in allcu else ""
        agg = m * CU / 1e6 if m is not None else None
        p = pce.get(it)
        w.writerow(["published_all_cu_detail_2025", it, "mean_usd", m, pr, "%.2f" % agg if agg else "", p if p else "", "%.1f" % (100 * agg / p) if (agg and p) else ""])
print("ok")
