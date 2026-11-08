"""RE1's till trap, looked for in an ensemble's runs (RE2, 2026-11-08).

    python -I docs/queue/RE2/trap.py <folder of the ensemble's <policy>-<seed>.csv>

Each run needs the landlords' columns, which the ensemble adds with
JAVA_OPTS="-Densemble.extra=re_net=getSectors.realEstate.getNetIncome;..."
(docs/queue/RE2-notes.md has the line). RE1's signature on HD1's seed 4 was the
landlords at a loss in 275 of 301 months, the town flat at its migration target
for 900. A run is flagged when some WINDOW-month stretch has the landlords at a
loss in at least LOSS_SHARE of its months while the population moved less than
FLAT of itself; the flagged runs are the ones to replay with RE1's probe.
"""
import csv, glob, os, sys

WINDOW, LOSS_SHARE, FLAT = 120, 0.5, 0.05

def rows(path):
    with open(path, newline="") as f:
        for r in csv.DictReader(f):
            try:
                yield int(float(r["month"])), float(r["pop"]), float(r.get("re_net", "nan"))
            except (KeyError, ValueError, TypeError):
                continue

def scan(path):
    rs = [r for r in rows(path) if r[1] == r[1]]
    if not rs:
        return None
    worst, hits, first = (0, None, None), 0, None     # most loss months in a window; windows with both tests; the first
    for i in range(0, max(1, len(rs) - WINDOW + 1)):
        w = rs[i:i + WINDOW]
        if len(w) < WINDOW:
            break
        loss = sum(1 for _, _, n in w if n < 0)
        growth = w[-1][1] / w[0][1] - 1 if w[0][1] > 0 else 0
        if loss > worst[0]:
            worst = (loss, w[0][0], growth)
        if loss >= LOSS_SHARE * WINDOW and abs(growth) < FLAT:
            hits += 1
            first = w[0][0] if first is None else first
    loss, start, growth = worst
    flagged = hits > 0
    losses = sum(1 for _, _, n in rs if n < 0)
    return flagged, loss, start, growth, losses, len(rs), rs[-1][1], hits, first

def main(folder):
    out = []
    for p in sorted(glob.glob(os.path.join(folder, "*.csv"))):
        s = scan(p)
        if s:
            out.append((os.path.basename(p)[:-4],) + s)
    flagged = [o for o in out if o[1]]
    print(f"{len(out)} runs read; {len(flagged)} flagged (a {WINDOW}-month stretch with the landlords at a loss"
          f" in {LOSS_SHARE:.0%} of it and the population within {FLAT:.0%})")
    print("run | flagged (windows, first from) | most loss months in a window | from | growth over it | loss months in all | final pop")
    for name, f, loss, start, growth, losses, months, final, hits, first in out:
        g = "-" if growth is None else f"{growth:+.1%}"
        flag = f"YES ({hits}, m{first})" if f else "no"
        print(f"{name} | {flag} | {loss} | {start} | {g} | {losses} of {months} | {final:,.0f}")

if __name__ == "__main__":
    main(sys.argv[1])
