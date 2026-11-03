"""The wages study's table (docs/studies/spec-wages.md, section 5): every
WageProbe CSV in a folder, one line per variant, the median over its seeds of
each measure, and the floor variants against their `today` twin (same seed).

    python -I summarize.py OUT [FROM_MONTH [NAMES]]

FROM_MONTH (default 240) starts the span measured: a city past its founding.
NAMES, a comma list, picks the variants and their order (default: all).
"""
import csv
import glob
import os
import statistics as st
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from ladder import ladder  # noqa: E402

OUT = sys.argv[1]
FROM = int(sys.argv[2]) if len(sys.argv) > 2 else 240
PICK = sys.argv[3].split(",") if len(sys.argv) > 3 else None


def f(r, k):
    v = r.get(k, "")
    return float(v) if v not in ("", None) else float("nan")


def load(path):
    rows = []
    for r in csv.DictReader(open(path)):
        rows.append({k: f(r, k) for k in r})
    return rows


def measures(rows):
    span = [r for r in rows if r["m"] >= FROM]
    if not span:
        return None
    n = len(span)
    m = {}
    m["unemp %"] = 100 * st.mean(r["unemp"] for r in span)
    m["unemp m1200+ %"] = 100 * st.mean(r["unemp"] for r in span if r["m"] >= 1200) if any(r["m"] >= 1200 for r in span) else float("nan")
    m["pop end k"] = rows[-1]["pop"] / 1000
    first, last = span[0], span[-1]
    years = (last["m"] - first["m"]) / 12
    m["infl %/yr"] = 100 * ((last["idx"] / first["idx"]) ** (1 / years) - 1) if years > 0 else float("nan")
    m["floor/median"] = st.median(r["floor"] / r["median"] for r in span if r["median"] > 0)
    m["floor/mean"] = st.median(r["floor"] / r["mean"] for r in span if r["mean"] > 0)
    m["paid floor %"] = 100 * st.mean(r["at_floor"] / r["staffed"] for r in span if r["staffed"] > 0)
    m["none pinned %"] = 100 * st.mean(r["pinned_none"] for r in span)
    m["dip pinned %"] = 100 * st.mean(r["pinned_dip"] for r in span)
    m["dip=none %"] = 100 * sum(1 for r in span if abs(r["w_dip"] / r["w_nodip"] - 1) < .01) / n
    m["no sci/pol posts %"] = 100 * sum(1 for r in span if r["p_sci"] + r["p_pol"] <= 0) / n
    col = lambda r: max(r["w_colh"], r["w_colb"], r["w_cole"])
    uni = lambda r: max(r["w_sci"], r["w_pol"])
    m["uni<col %"] = 100 * sum(1 for r in span if uni(r) < col(r)) / n
    m["uni<col w/ posts %"] = 100 * sum(1 for r in span if uni(r) < col(r) and r["p_sci"] + r["p_pol"] > 0) / n
    m["col<dip %"] = 100 * sum(1 for r in span if col(r) < r["w_dip"]) / n
    cost = lambda r: r["rent_door"] + r["shop_head"] + r["fees_head"]
    m["floor job covers %"] = 100 * st.median(r["floor"] * (1 - r["payslip_none"]) / cost(r) for r in span if cost(r) > 0)
    m["workers/post"] = st.mean(r["workforce"] / r["posts_all"] for r in span if r["posts_all"] > 0)
    m["senior pull"] = st.mean(r["pull_senior"] for r in span)
    m["home share of draw %"] = 100 * st.mean(r["mig_homedraw"] / (r["mig_jobdraw"] + r["mig_homedraw"]) for r in span if r["mig_jobdraw"] + r["mig_homedraw"] > 0)
    m["real median"] = st.median(r["median"] / r["unit"] for r in span if r["unit"] > 0)
    m["departures %/yr"] = 100 * 12 * st.mean(r["departures"] / r["pop"] for r in span if r["pop"] > 0)
    lad = [ladder({k: str(v) for k, v in r.items()}) for r in span[::12]]
    for step in ("Diploma", "College", "University"):
        m[f"{step} after cost x"] = st.median(l[step]["after"] / l["No diploma"]["after"] for l in lad)
    return m


def main():
    runs = {}
    for p in sorted(glob.glob(os.path.join(OUT, "*-s*.csv"))):
        name, seed = os.path.basename(p)[:-4].rsplit("-s", 1)
        if PICK and name not in PICK and name != "today":
            continue
        rows = load(p)
        if rows:
            runs.setdefault(name, {})[int(seed)] = rows
    keys = None
    table = {}
    for name, seeds in runs.items():
        per = [measures(r) for r in seeds.values()]
        per = [x for x in per if x]
        if not per:
            continue
        keys = list(per[0].keys())
        table[name] = {k: st.median(x[k] for x in per) for k in keys}
        table[name]["seeds"] = len(per)
    names = [n for n in PICK if n in table] if PICK else list(table)
    print(f"from month {FROM}; the median over seeds")
    print(f"{'measure':24}" + "".join(f"{n:>11}" for n in names))
    print(f"{'seeds':24}" + "".join(f"{table[n]['seeds']:>11}" for n in names))
    for k in keys:
        print(f"{k:24}" + "".join(f"{table[n][k]:>11.3g}" for n in names))

    # Against the twin: each floor variant minus `today` on the same seed, over windows.
    if "today" in runs:
        print("\nagainst `today`, same seed (median over seeds): unemployment points, real median wage, population, inflation")
        windows = [(240, 480), (480, 1200), (1200, 2400)]
        for name in names:
            seeds = runs[name]
            if name == "today":
                continue
            line = f"{name:10}"
            for a, b in windows:
                du, dw, dp, di = [], [], [], []
                for s, rows in seeds.items():
                    twin = runs["today"].get(s)
                    if not twin:
                        continue
                    w1 = [r for r in rows if a <= r["m"] < b]
                    w0 = [r for r in twin if a <= r["m"] < b]
                    if not w1 or not w0:
                        continue
                    du.append(100 * (st.mean(r["unemp"] for r in w1) - st.mean(r["unemp"] for r in w0)))
                    dw.append(100 * (st.mean(r["median"] / r["unit"] for r in w1) / st.mean(r["median"] / r["unit"] for r in w0) - 1))
                    dp.append(100 * (w1[-1]["pop"] / w0[-1]["pop"] - 1))
                    yrs = (w1[-1]["m"] - w1[0]["m"]) / 12
                    i1 = (w1[-1]["idx"] / w1[0]["idx"]) ** (1 / yrs) - 1
                    i0 = (w0[-1]["idx"] / w0[0]["idx"]) ** (1 / yrs) - 1
                    di.append(100 * (i1 - i0))
                if du:
                    line += (f" | m{a}-{b}: u {st.median(du):+5.1f} pt, real median {st.median(dw):+5.1f}%,"
                             f" pop {st.median(dp):+5.1f}%, infl {st.median(di):+5.2f} pt")
            print(line)


if __name__ == "__main__":
    main()
