"""The wages study's read of Jerus's saves (docs/studies/spec-wages.md, 1.4):
out of work by city size, from each slot's history file, and the migration
target's parts from each slot's save. Reads only; never writes a slot.

    python -I saves.py SAVES_FOLDER

Out of work is outOfWork / workforce, the People screen's figure. The
migration array's layout is Migration.restore()'s: the decline history
(6 tiers x DECLINE_MONTHS), the 6 streaks and the months recorded, then
lastTarget, lastArrivals, ... in the order named below.
"""
import glob
import json
import os
import statistics as st
import sys

DECLINE_MONTHS = 12  # Migration.DECLINE_MONTHS
TIERS = 6            # PayTier
LAST = ["target", "arrivals", "departures", "crowding", "decliningShare", "seniorPull",
        "residentsPerJob", "affordabilityPull", "crimePull", "crimeDepartures", "bankruptcyPush",
        "workDepartures", "jobs", "homeCapacity", "jobDraw", "homeDraw"]

folder = sys.argv[1]
bins = [(0, 1e4), (1e4, 1e5), (1e5, 1e6), (1e6, 1e12)]
pooled = {b: [] for b in bins}
print("slot     last month   population   out of work, last 10 years   workforce per post   senior pull   home share of the draw")
for slot in range(1, 11):
    stem = os.path.join(folder, f"slot-{slot:02d}")
    if not os.path.exists(stem + "-history.json"):
        continue
    h = json.load(open(stem + "-history.json", encoding="utf-8"))
    m, pop, wf, out, jobs = h["month"], h["population"], h["workforce"], h["outOfWork"], h["jobs"]
    n = len(m)
    u = [out[i] / wf[i] if wf[i] else 0 for i in range(n)]
    last = [i for i in range(n) if m[i] >= m[-1] - 120]
    wpp = st.mean(wf[i] / jobs[i] for i in last if jobs[i])
    for i in range(n):
        if m[i] >= 120:
            for b in bins:
                if b[0] <= pop[i] < b[1]:
                    pooled[b].append(u[i])
    pull = home = float("nan")
    try:
        d = json.load(open(stem + ".json", encoding="utf-8"))
        a = d["migration"]
        start = TIERS * DECLINE_MONTHS + TIERS + 1
        v = dict(zip(LAST, a[start:start + len(LAST)]))
        pull = v["seniorPull"]
        home = v["homeDraw"] / (v["jobDraw"] + v["homeDraw"])
    except (OSError, KeyError, ValueError, ZeroDivisionError):
        pass
    print(f"{slot:>4} {m[-1]:>12,} {pop[-1]:>12,} {100 * st.mean(u[i] for i in last):>18.1f}% {wpp:>22.3f}"
          f" {pull:>13.3f} {100 * home:>17.1f}%")
print("\npooled, every slot's months from 120, by the city's size in that month:")
for b, v in pooled.items():
    if v:
        v = sorted(v)
        print(f"  {int(b[0]):>9,} to {int(min(b[1], 1e9)):>13,} people: {len(v):6} months, median {100 * v[len(v) // 2]:4.1f}%,"
              f" 10th percentile {100 * v[len(v) // 10]:4.1f}%, 90th {100 * v[9 * len(v) // 10]:4.1f}%")
