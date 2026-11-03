"""The wages study's table 2 (docs/studies/spec-wages.md, section 5): one row a
variant, its out-of-work rate, workers a post, leaving and inflation (the
median over seeds, from month 240), and its people at months 480 and 2,400
against `today` on the same seed, the median and each seed's own, since a
change sends a seed down another path and only a sign every seed agrees on
counts.

    python -I people.py OUT today,sen13,...
"""
import csv
import glob
import os
import statistics as st
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
_argv = sys.argv
sys.argv = [_argv[0], _argv[1], "240"]
import summarize as S  # noqa: E402
sys.argv = _argv

OUT = sys.argv[1]
names = sys.argv[2].split(",")
runs = {}
for p in sorted(glob.glob(os.path.join(OUT, "*-s*.csv"))):
    name, seed = os.path.basename(p)[:-4].rsplit("-s", 1)
    if name in names or name == "today":
        runs.setdefault(name, {})[int(seed)] = S.load(p)


def pop_at(rows, m):
    at = [r for r in rows if r["m"] == m]
    return (at or rows)[-1]["pop"]


print("| variant | out of work | from m1,200 | workers a post | people at m480 | people at m2,400 | leaving a year | inflation | each seed, m480 / m2,400 |")
print("|---|---|---|---|---|---|---|---|---|")
for n in names:
    per = [S.measures(r) for r in runs[n].values()]
    m = lambda k: st.median(x[k] for x in per)
    d480, d2400, each = [], [], []
    for s, rows in sorted(runs[n].items()):
        t = runs["today"][s]
        a = 100 * (pop_at(rows, 480) / pop_at(t, 480) - 1)
        b = 100 * (rows[-1]["pop"] / t[-1]["pop"] - 1)
        d480.append(a)
        d2400.append(b)
        each.append(f"{a:+.0f}/{b:+.0f}")
    print(f"| {n} | {m('unemp %'):.1f}% | {m('unemp m1200+ %'):.1f}% | {m('workers/post'):.2f} | {st.median(d480):+.0f}% |"
          f" {st.median(d2400):+.0f}% | {m('departures %/yr'):.1f}% | {m('infl %/yr'):.2f}% | {' '.join(each)} |")
