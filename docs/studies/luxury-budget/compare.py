"""Main against a prototype, run by run (docs/studies/luxury-budget.md, section 5).

    python -I docs/studies/luxury-budget/compare.py MAIN_DIR PROTO_DIR [NAME ...]

A NAME may be MAIN_NAME:PROTO_NAME, for a prototype run under another name
(seed0:seed0long, the old city). For each run the year to m240, 600, 1200 and
2400, and to m3000, 3600 and 3996 where both runs go so far, and every year's
median from m240 (and from m2400, for a run past it): the city's size, the counter's spend and the shops' value added over GDP,
their workers over the staffed posts, and the households' net worth in years of
GDP and of their disposable income; main's figure, then the prototype's.
"""
import os, statistics, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from report import rows, city_line, fmt

KEYS = ["people", "spend/gdp", "spend/C", "spend/disp", "lrVA/gdp", "lrW/staffed", "meal/gdp",
        "nw/gdp", "nw/disp", "disp/gdp", "C/gdp", "boutiques"]

def lines(d, name):
    return [l for l in (city_line(r) for r in rows(f"{d}/{name}-city.csv")) if l["month"] >= 12]

def at(ls, mk):
    return min(ls, key=lambda l: abs(l["month"] - mk))

def main():
    a, b = sys.argv[1], sys.argv[2]
    for name in sys.argv[3:] or ["seed0", "seed1", "rotterdam"]:
        na, _, nb = name.partition(":")
        la, lb = lines(a, na), lines(b, nb or na)
        last = min(la[-1]["month"], lb[-1]["month"])
        print(f"== {name}: main ; prototype ==")
        for mk in (240, 600, 1200, 2400, 3000, 3600, 3996):
            if mk > last + 12:
                continue
            x, y = at(la, mk), at(lb, mk)
            print(f"  m{x['month']}: " + "  ".join(f"{k} {fmt(x[k])} ; {fmt(y[k])}" for k in KEYS))
        med = lambda ls, k: statistics.median([l[k] for l in ls if 240 <= l["month"] <= last and l[k] == l[k]])
        print("  median m240-m%d: " % last + "  ".join(f"{k} {fmt(med(la, k))} ; {fmt(med(lb, k))}" for k in KEYS))
        if last > 2412:
            late = lambda ls, k: statistics.median([l[k] for l in ls if 2400 <= l["month"] <= last and l[k] == l[k]])
            print("  median m2400-m%d: " % last + "  ".join(f"{k} {fmt(late(la, k))} ; {fmt(late(lb, k))}" for k in KEYS))

if __name__ == "__main__":
    main()
