"""WN3-R's study of R3 on the world's band: the rent's parts, HIGH against its AUTO2 twin (2026-11-08).

    python -I docs/queue/WN3-R/rent.py <folder of an ensemble's HIGH-<seed>.csv and AUTO2-<seed>.csv>

The runs need the landlords' columns, which the ensemble adds with (both data files named, to month 720):
POLICIES="AUTO2 HIGH" BASELINE=none JAVA_OPTS="-Dcbs.earth=<data>/earth-2.bin -Dcbs.world=<data>/world-1.bin
-Densemble.months=720 -Densemble.extra=scar=getSectors.realEstate.rentScarcityMultiple;be=getSectors.realEstate.rentBreakEven;
req=getSectors.realEstate.rentRequired;tgt=getSectors.realEstate.blendedRentTarget;int=getSectors.realEstate.getInterestExpense;
cap=getSectors.realEstate.getOwnedHousingCapacity" tools/ensemble/ensemble.sh <label>
The rent's target is rentRequired() x the scarcity multiple x the carry lift, so the lift is read as tgt / (req x scar).
"""
import csv, sys, os, statistics as st
d = sys.argv[1]; m0, m1 = 660, 720
def load(p):
    rows = {}
    with open(p) as f:
        for r in csv.DictReader(f):
            try: rows[int(float(r["month"]))] = r
            except (ValueError, KeyError): pass
    return rows
def g(R, m, c):
    try: return float(R[m][c])
    except (ValueError, KeyError): return float("nan")
def ann(R, c):
    x, y = g(R, m0, c), g(R, m1, c)
    return ((y / x) ** (12.0 / (m1 - m0)) - 1) * 100 if x > 0 and y > 0 else float("nan")
def lift(R, m):
    base = g(R, m, "req") * g(R, m, "scar")
    return g(R, m, "tgt") / base if base > 0 else float("nan")
def share(R, m):
    carry = g(R, m, "be") * g(R, m, "cap")
    return g(R, m, "int") / carry if carry > 0 else float("nan")
print(f"seed   R3 gap | rent a year, HIGH twin | D$ a year, HIGH twin | at m{m1}, HIGH over twin: rent  cost  scarcity  lift | interest/carry HIGH twin")
gaps = []
for s in range(16):
    H, T = load(os.path.join(d, f"HIGH-{s}.csv")), load(os.path.join(d, f"AUTO2-{s}.csv"))
    gap = ann(H, "index") - ann(T, "index"); gaps.append(gap)
    r = lambda c: g(H, m1, c) / g(T, m1, c)
    print(f"{s:>4} {gap:+8.2f} | {ann(H, 'rent'):+9.1f} {ann(T, 'rent'):+5.1f} | {ann(H, 'fx'):+9.1f} {ann(T, 'fx'):+5.1f} | "
          f"{r('rent'):>30.2f} {r('req'):5.2f} {r('scar'):9.2f} {lift(H, m1) / lift(T, m1):5.2f} | {share(H, m1):14.2f} {share(T, m1):5.2f}")
print(f"R3: median {st.median(gaps):+.2f} points, {sum(1 for x in gaps if x > 1)} seeds above +1 {[i for i, x in enumerate(gaps) if x > 1]}")
for pol, frm in (("AUTO2", 240), ("HIGH", 600)):
    lifts, binds = [], 0
    for s in range(16):
        R = load(os.path.join(d, f"{pol}-{s}.csv"))
        for m in range(frm, m1 + 1):
            l = lift(R, m)
            if l == l: lifts.append(l); binds += l > 1.001
    print(f"{pol} m{frm}-{m1}: the carry lift binds in {binds / len(lifts):.0%} of months, median {st.median(lifts):.2f}")
