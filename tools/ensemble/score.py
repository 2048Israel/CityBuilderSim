#!/usr/bin/env python3
"""score.py - scores one ensemble (16 seeds x 5 policies) against the roadmap's criteria.

usage: python -I score.py <outdir> [--baseline <summary.json | docs/ensemble/<version>.md>] [--summary <summary.json to write>]

A baseline .md is a docs/ensemble/<version>.md: what is between its first line that is exactly ```json and the next line
that is exactly ``` is read as the summary.json. pandas is looked for first in ENSEMBLE_PYLIB (default
~/.cache/cbs-ensemble/py, where tools/ensemble/ensemble.sh's README says to install it); exit 2 where it is missing.

<outdir> holds <POLICY>-<seed>.csv as Ensemble.java writes them (one row a month) and,
optionally, times.txt from ensemble.sh. Every criterion below is paired where it can be: the
AUTO2 run of the same seed is the same city to the bit until the policy changes (same
founding, same autopilot, same advisor rhythm), so "the policy's effect" is the run minus
its AUTO2 twin over the same months, and the weather cancels.

Inflation over a window is the annualised change in the price index over that window:
(index[m1] / index[m0]) ** (12 / (m1 - m0)) - 1. "yoy" is PriceIndex.inflation(), the
twelve-month rate the autopilot itself reads. Medians and [min-max] are across seeds.

==================================  THE CRITERIA  ==================================
roadmap-inflation.md, five policies and "across all five", made precise (thresholds are
the harness's judgement calls, marked * in the report):

AUTO2  the autopilot at 2%, 2,400 months
  A1 mean      inflation from month AUTO2_FROM to the end: the median seed within
               TARGET +- BAND, and at least AUTO2_SEEDS_IN_BAND seeds individually within it
  A2 runaway   (restated EN1) from month AUTO2_FROM, no seed has yoy above RUNAWAY_YOY for
               RUN_MONTHS consecutive months. Until EN1 from month SPELL_FROM (120); the founding,
               month 120 to AUTO2_FROM, is printed as info
  A3 collapse  (restated EN1) from month AUTO2_FROM, no seed has yoy below COLLAPSE_YOY for
               RUN_MONTHS consecutive months, every
               seed finishes its months, and none ends below COLLAPSE_POP of its peak population
ZERO_SLACK  the autopilot to month 1,800 (a mature city; Ensemble.SLACK_START), then 0% held 240 months
  S1 little    (restated 0.7.100, A27) the zero window against its AUTO2 twin's same months: the
               window's inflation over the twin's, median at most SLACK_EXCESS_MEDIAN and every
               seed at most SLACK_EXCESS_SEED; and no seed runs away in the window (yoy above
               RUNAWAY_YOY for RUN_MONTHS consecutive months, A2's runaway). Until 0.7.100: from
               month 600, the window's own inflation, median at most SLACK_MEDIAN_MAX, every seed
               at most SLACK_SEED_MAX, no yoy above RUNAWAY_YOY (see THE PREMISES RESTATED below)
ZERO_HOT  0% held 240 months from HOT_START (young city), then the autopilot 120 months
  H1 persists  (restated 0.7.100, A27) the zero window's two halves against the AUTO2 twin's same
               months: the first half's inflation over the twin's (the burst) and the second
               half's (the burst persisting while the dial is held), median at least
               HOT_PERSIST_MIN each, and the second half no faster than the first by
               HOT_ACCEL_MIN or more (median) - a burst, not a spiral. Until 0.7.100 "H1 builds":
               the second half at least HOT_SPIRAL_MIN, above the first half by at least
               HOT_ACCEL_MIN and above the twin by at least HOT_EXCESS_MIN (medians)
  H2 stops     (restated EN1) the last STOP_TAIL months of the autopilot phase against the AUTO2
               twin's same months: their inflation over the twin's, median at most HOT_STOP_EXCESS,
               and below the zero window's second half's over the twin's in at least SEEDS_MOST
               seeds (vacuous - marked so - when H1 failed: there was no burst to stop). Until EN1
               the tail's own inflation, median at most HOT_STOP_MAX (4%) and below the second
               half's own in SEEDS_MOST seeds
SHOCK  the autopilot; at month 600 the world's price level x 1.3, once; 240 months on
  K1 step      the index above its AUTO2 twin's, at its highest within SHOCK_STEP_WINDOW months
               of the shock: median at least SHOCK_STEP_MIN
  K2 settles   the last SETTLE_TAIL months: median inflation at most TARGET + BAND, and their
               inflation over the twin's at most SHOCK_SPIRAL_EXCESS a year (median) - a step,
               not a spiral (judged whether or not K1 passed). First written as the gap's growth
               over the tail (1% in ten years), which the weather between twins alone exceeds;
               restated per year on 2026-10-04 before any later version was scored.
HIGH  the autopilot to month 600, then 15% held 120 months
  R1 falls     window inflation minus the twin's: median at most HIGH_FALL
  R2 cost      window unemployment minus the twin's: median at least HIGH_COST
  R3 no trap   the window's last TRAP_TAIL months, inflation minus the twin's: median at most 0,
               and at most TRAP_SEEDS seeds above +1 point
ALL
  T1 takeoffs  AUTO2 seeds whose population reached TAKEOFF_POP: at least the baseline's
               count less TAKEOFF_SLACK (no baseline: reported only). LongPlaytest has no takeoff
               test of its own; 40,000 is past the old ~17k plateau, and every 0.7.41 seed passes
               it between months 1,220 and 1,503. The other policies end by month 840, before any
               city is that big, so they are judged by T2 and by their population against the twin.
  T2 size      each policy's median final population at least POP_KEEP of the baseline's
  M1 audit     the money audit clean in every month of every run
  F1 findings  LongPlaytest's audit: runs with any finding (reported against the baseline)
===================================================================================
"""
import json
import math
import os
import re
import sys

# Plumbing (EN1): the table has a "≥", which a Windows console's code page cannot write, and Windows
# would end its lines with CR LF in score.md; and pandas lives in a folder of its own (pip --target),
# put first on the path, because "python -I" ignores PYTHONPATH.
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8", newline="\n")
_PYLIB = os.environ.get("ENSEMBLE_PYLIB") or os.path.expanduser("~/.cache/cbs-ensemble/py")
if os.path.isdir(_PYLIB):
    sys.path.insert(0, _PYLIB)
try:
    import pandas as pd
except ImportError:
    print('pandas is not installed: python -m pip install --target "$HOME/.cache/cbs-ensemble/py" pandas',
          file=sys.stderr)
    sys.exit(2)

# ---- thresholds (* in the report) -------------------------------------------------
TARGET = 0.02
BAND = 0.01
AUTO2_FROM = 240           # the young city's first twenty years are left out of A1, and since EN1 of A2/A3
AUTO2_SEEDS_IN_BAND = 12
SPELL_FROM = 120           # until EN1 where A2/A3 began; now where the founding's info row begins (THE PREMISES RESTATED, EN1)
YOUNG_TO = 600             # the young city's window, AUTO2_FROM to here, printed as info (the spec's section 8: "A1/A2 per window")
RUNAWAY_YOY = 0.10
COLLAPSE_YOY = -0.10
RUN_MONTHS = 12
COLLAPSE_POP = 0.5
SLACK_MEDIAN_MAX = 0.03
SLACK_SEED_MAX = 0.05
HOT_SPIRAL_MIN = 0.05
HOT_ACCEL_MIN = 0.01
HOT_EXCESS_MIN = 0.03
HOT_STOP_MAX = 0.04
STOP_TAIL = 60
SEEDS_MOST = 12
SHOCK_STEP_WINDOW = 36
SHOCK_STEP_MIN = 0.02
SETTLE_TAIL = 120
SHOCK_SPIRAL_EXCESS = 0.01
HIGH_FALL = -0.005
HIGH_COST = 0.005
TRAP_TAIL = 60
TRAP_SEEDS = 2
TAKEOFF_POP = 40000
TAKEOFF_SLACK = 2
POP_KEEP = 0.75

# ---- THE PREMISES RESTATED (0.7.100, batch P1; Jerus's A27 of 2026-10-10: "Rewrite both
# premises; watch A2") --------------------------------------------------------------------
# The anchor's tolerance: Expectations.TOLERANCE, 1 point - a miss within it rebuilds the
# central bank's credibility, past it credibility falls toward KMIN (runs/spec-inflation.md 2.1,
# star 14: "TOL 1 point"). What the anchor itself counts as inflation on target.
ANCHOR_TOL = 0.01
# S1, LITTLE INFLATION IN A SLACK CITY. Its window was months 600-840, a city at takeoff, not a
# slack one (on 0.7.43 the twin's unemployment 14.9% at the switch and about 5% within it, the
# population x2.44; the spec's section 8 had said so: "ZERO_SLACK at month 600 is not slack ...
# use city2400-like mature seeds or month 1,800 for 'slack'"). The hold is at 1,800 now
# (Ensemble.SLACK_START). There the autopilot's own weather is in the window too (A2: spells of
# yoy over 10% in the autopilot alone), so S1 reads the run against its twin, which is the
# autopilot as it actually runs, and the old window thresholds - set against the 2% target -
# become their excess over TARGET: 3% -> +1 point, 5% -> +3. The median's +1 is the anchor's
# tolerance: a held 0% that keeps the city within the tolerance of what the autopilot gives
# leaves the anchor where it was, which is "little" in the model's own terms. The old guard, no
# month's yoy above RUNAWAY_YOY, becomes A2's runaway (RUN_MONTHS on end above it): one month's
# yoy rides the grocers' shelf cycle (the shelf cap alone is about +11% on the index, the
# spec's star 17), which a run and its twin ride out of phase once their policies part, so
# neither a month's yoy nor its gap to the twin's same month reads the policy. (First written as
# that gap, at most RUNAWAY_YOY - TARGET; on 0.7.100's runs it read the cycle's phase - seed 0
# at m1890, 16.3% against its twin's -4.1% - and was replaced before the score was taken. The
# peaks and that gap are printed as info.)
SLACK_EXCESS_MEDIAN = ANCHOR_TOL                          # = SLACK_MEDIAN_MAX - TARGET
SLACK_EXCESS_SEED = SLACK_SEED_MAX - TARGET               # +3 points
# H1, A BURST THAT PERSISTS. The model's Phillips curve is the existing level wage curve plus
# expectations, with no rate term (the spec's star 1), and KMIN .25 keeps a quarter of expected
# inflation on the target however long the miss (star 14; KMIN .1 was measured to lock the
# founding at 10%). So a held 0% gives a burst that persists while the dial is held - credibility
# falls toward KMIN and expected inflation follows smoothed inflation three parts in four - and
# not a rising rate, which "H1 builds" asked for. H1 asks for that: each half of the zero window
# above its twin's by at least the anchor's tolerance (beyond it the anchor keeps losing
# credibility: the burst has not faded), and the second half no faster than the first by
# HOT_ACCEL_MIN, the acceleration "H1 builds" required, now its ceiling.
HOT_PERSIST_MIN = ANCHOR_TOL
# H2 then means something: whether the autopilot, back on for 120 months, stops that burst (its
# thresholds as written; vacuous only when H1 finds no burst).

# ---- THE PREMISES RESTATED, AGAIN (EN1, 2026-10-20; Jerus: "yes, do that") ----------------
# On 0.8.9's ensemble (FR6's fr6s1, main's model through 0.8.11) four criteria failed on main
# itself, run after run; docs/queue/EN1-notes.md has each one's figures. Two were premises out of
# date, restated here; S1 waits on Jerus (EN1-C) and R3 on a fix of the model, both as written.
# A2, THE FOUNDING IS NOT A RUNAWAY. SPELL_FROM left out the founding's own blip as it was
# measured on 0.7.4x, over by month 104. The founding has since moved later: on 0.8.9 the 13
# seeds that fail A2 from SPELL_FROM all fail on the founding's rent boom, a spell of 12-19
# months starting between months 128 and 141, in a city of 995-1,712 people whose rents rose
# 32-82% in the year, while the autopilot already held 17-23%. A housing shortage in a village is
# not the autopilot losing hold of the money. A2 and A3 now leave out the young city's first twenty years, the AUTO2_FROM
# that A1 leaves out, and the founding's spells (month SPELL_FROM to AUTO2_FROM) are printed as
# info. From AUTO2_FROM the longest spell on 0.8.9 is 11 months (seed 9, twice): one under
# RUN_MONTHS, so A2 is still the criterion to watch.
# H2, AGAINST THE TWIN, AS H1 IS. The autopilot alone runs a young city hot: on 0.8.9 the AUTO2
# twins' own inflation over ZERO_HOT's tail months (342-402) is 4.99% at the median, above
# HOT_STOP_MAX, so the 4% ceiling judged the young city's autopilot, not whether it stopped the
# burst; the runs themselves end 0.40 points below their twins at the median. H2 reads the tail against the
# twin, as A27 made H1 read the burst: the tail's excess at most the anchor's tolerance (back on
# target in the anchor's own terms), and below the second half's excess, the burst it stops. The
# young city's own inflation (AUTO2_FROM to YOUNG_TO) is printed as info beside A1.
HOT_STOP_EXCESS = ANCHOR_TOL

POLICIES = ["AUTO2", "ZERO_SLACK", "ZERO_HOT", "SHOCK", "HIGH"]
# months each policy runs to, as Ensemble.end() sets them (ZERO_HOT's and, since 0.7.100, ZERO_SLACK's are read from their phases)
EXPECTED_END = {"AUTO2": 2400, "ZERO_SLACK": 2040, "SHOCK": 840, "HIGH": 720}


def load(outdir):
    runs = {}
    for name in sorted(os.listdir(outdir)):
        m = re.fullmatch(r"([A-Z0-9_]+)-(\d+)\.csv", name)
        if not m or m.group(1) not in POLICIES:
            continue
        df = pd.read_csv(os.path.join(outdir, name), low_memory=False)
        df = df[pd.to_numeric(df["month"], errors="coerce").notna()].copy()
        df["month"] = df["month"].astype(int)
        df = df.drop_duplicates("month", keep="last").set_index("month", drop=False)
        runs[(m.group(1), int(m.group(2)))] = df
    return runs


def at(df, col, month):
    """The column at a month, or NaN when the run never got there."""
    return float(df.at[month, col]) if month in df.index else math.nan


def ann(df, m0, m1):
    """Annualised inflation of the index from month m0 to m1."""
    a, b = at(df, "index", m0), at(df, "index", m1)
    if not (a > 0 and b > 0) or m1 <= m0:
        return math.nan
    return (b / a) ** (12.0 / (m1 - m0)) - 1


def mean_col(df, col, m0, m1):
    s = df.loc[(df["month"] > m0) & (df["month"] <= m1), col]
    return float(s.mean()) if len(s) else math.nan


def longest_run(mask):
    best = cur = 0
    for v in mask:
        cur = cur + 1 if v else 0
        best = max(best, cur)
    return best


def phase_start(df, phase):
    """Months already run when a phase began (its first row's month - 1), or None."""
    rows = df[df["phase"] == phase]
    return int(rows["month"].iloc[0]) - 1 if len(rows) else None


def fmt(v, pct=True):
    if v is None or (isinstance(v, float) and math.isnan(v)):
        return "n/a"
    return f"{100 * v:.2f}%" if pct else f"{v:,.0f}"


def spread(vals, pct=True):
    v = [x for x in vals if not (isinstance(x, float) and math.isnan(x))]
    if not v:
        return "n/a"
    s = pd.Series(v)
    return f"{fmt(s.median(), pct)} [{fmt(s.min(), pct)} to {fmt(s.max(), pct)}]"


def med(vals):
    v = [x for x in vals if not (isinstance(x, float) and math.isnan(x))]
    return float(pd.Series(v).median()) if v else math.nan


def read_baseline(path):
    """A summary.json, or a docs/ensemble/<version>.md whose fenced json block is one (EN1)."""
    with open(path, encoding="utf-8") as f:
        text = f.read()
    if not path.lower().endswith(".md"):
        return json.loads(text)
    block, inside = [], False
    for line in text.splitlines():
        if not inside:
            inside = line == "```json"
        elif line == "```":
            return json.loads("\n".join(block))
        else:
            block.append(line)
    print(f"{path} has no ```json block to read as a baseline", file=sys.stderr)
    sys.exit(2)


def main():
    args = sys.argv[1:]
    if not args:
        print(__doc__)
        sys.exit(2)
    outdir = args[0]
    baseline = summary_out = None
    if "--baseline" in args:
        baseline = args[args.index("--baseline") + 1]
    if "--summary" in args:
        summary_out = args[args.index("--summary") + 1]
    base = None
    if baseline and os.path.exists(baseline):
        base = read_baseline(baseline)

    runs = load(outdir)
    seeds = sorted({s for (_, s) in runs})
    rows = []      # (policy, id, criterion, measure, value, threshold, verdict)
    summary = {"seeds": len(seeds), "policies": {}}

    def add(pol, cid, what, value, threshold, ok, note=""):
        # A verdict worked out on pandas' figures is numpy's bool, which "is True" never is: until
        # EN1, A3's printed as "True" and the count below skipped it (26 criteria, not 27).
        if not isinstance(ok, str):
            ok = bool(ok)
        verdict = "PASS" if ok is True else "FAIL" if ok is False else str(ok)
        rows.append((pol, cid, what, value, threshold, verdict + (f" ({note})" if note else "")))

    twin = {s: runs.get(("AUTO2", s)) for s in seeds}

    def pol_runs(p):
        return [(s, runs[(p, s)]) for s in seeds if (p, s) in runs]

    # ---------------------------------------------------------------- AUTO2
    a = pol_runs("AUTO2")
    if a:
        means = [ann(df, AUTO2_FROM, int(df["month"].max())) for _, df in a]
        in_band = sum(1 for v in means if abs(v - TARGET) <= BAND)
        add("AUTO2", "A1", f"inflation m{AUTO2_FROM}-end, a year", spread(means),
            f"median {fmt(TARGET)}±{fmt(BAND)}, ≥{AUTO2_SEEDS_IN_BAND}/{len(a)} in band*",
            abs(med(means) - TARGET) <= BAND and in_band >= AUTO2_SEEDS_IN_BAND, f"{in_band}/{len(a)} in band")
        add("AUTO2", "info", f"inflation m{AUTO2_FROM}-{YOUNG_TO} (the young city), a year",
            spread([ann(df, AUTO2_FROM, YOUNG_TO) for _, df in a]), "-", "info")
        # A2 and A3 from AUTO2_FROM since EN1 (THE PREMISES RESTATED, AGAIN); the founding as info.
        founding = lambda df: df[(df["month"] > SPELL_FROM) & (df["month"] <= AUTO2_FROM)]
        add("AUTO2", "info", f"the founding, m{SPELL_FROM}-{AUTO2_FROM}: longest spell of yoy > {fmt(RUNAWAY_YOY)}; "
            f"of yoy < {fmt(COLLAPSE_YOY)}, months",
            f"{spread([longest_run((founding(df)['infl_yoy'] > RUNAWAY_YOY).tolist()) for _, df in a], False)}; "
            f"{spread([longest_run((founding(df)['infl_yoy'] < COLLAPSE_YOY).tolist()) for _, df in a], False)}",
            "-", "info")
        late = lambda df: df[df["month"] > AUTO2_FROM]
        runaway = [longest_run((late(df)["infl_yoy"] > RUNAWAY_YOY).tolist()) for _, df in a]
        add("AUTO2", "A2", f"longest spell of yoy > {fmt(RUNAWAY_YOY)} from m{AUTO2_FROM}, months", spread(runaway, False),
            f"< {RUN_MONTHS} in every seed*", max(runaway) < RUN_MONTHS)
        deflate = [longest_run((late(df)["infl_yoy"] < COLLAPSE_YOY).tolist()) for _, df in a]
        short = [s for s, df in a if df["month"].max() < EXPECTED_END["AUTO2"]]
        endshare = [at(df, "pop", int(df["month"].max())) / max(1, df["pop"].max()) for _, df in a]
        ok = max(deflate) < RUN_MONTHS and not short and min(endshare) >= COLLAPSE_POP
        add("AUTO2", "A3", f"deflation spell from m{AUTO2_FROM} (months); final/peak population",
            f"{spread(deflate, False)}; {spread(endshare)}",
            f"< {RUN_MONTHS}; all finish; ≥ {fmt(COLLAPSE_POP)}*", ok,
            f"short runs: {short}" if short else "")
        rate = [mean_col(df, "policy_rate", AUTO2_FROM, 99999) for _, df in a]
        floor = [float((df.loc[df["month"] > AUTO2_FROM, "policy_rate"] <= 1e-9).mean()) for _, df in a]
        add("AUTO2", "info", "policy rate, mean from m240; share of months at 0%",
            f"{spread(rate)}; {spread(floor)}", "-", "info")
        yoy_all = [mean_col(df, "infl_yoy", AUTO2_FROM, 99999) for _, df in a]
        add("AUTO2", "info", "mean yoy reading from m240", spread(yoy_all), "-", "info")

    # ---------------------------------------------------------------- ZERO_SLACK
    z = pol_runs("ZERO_SLACK")
    if z:
        m0 = phase_start(z[0][1], "zero")
        m1 = m0 + 240 if m0 is not None else None
        EXPECTED_END["ZERO_SLACK"] = m1
        zt = [(s, df, twin[s]) for s, df in z if twin.get(s) is not None]
        win = lambda d: d[(d["month"] > m0) & (d["month"] <= m1)]
        # S1 as restated (THE PREMISES RESTATED): the window against its twin's same months.
        excess = [ann(df, m0, m1) - ann(t, m0, m1) for _, df, t in zt]
        spell = [longest_run((win(df)["infl_yoy"] > RUNAWAY_YOY).tolist()) for _, df in z]
        ok = (med(excess) <= SLACK_EXCESS_MEDIAN and max(excess) <= SLACK_EXCESS_SEED
              and max(spell) < RUN_MONTHS and len(zt) == len(z))
        add("ZERO_SLACK", "S1", f"inflation m{m0}-{m1} at 0% minus the AUTO2 twin's, a year", spread(excess),
            f"median ≤ +{fmt(SLACK_EXCESS_MEDIAN)}, all ≤ +{fmt(SLACK_EXCESS_SEED)}, "
            f"no spell of yoy > {fmt(RUNAWAY_YOY)} of {RUN_MONTHS}*", ok,
            f"longest spell {max(spell)} months")
        vals = [ann(df, m0, m1) for _, df in z]
        peak = [win(df)["infl_yoy"].max() for _, df in z]
        tpeak = [win(t)["infl_yoy"].max() for _, _, t in zt]
        gap = []
        for _, df, t in zt:
            w = win(df).set_index("month")["infl_yoy"]
            gap.append(float((w - win(t).set_index("month")["infl_yoy"].reindex(w.index)).max()))
        add("ZERO_SLACK", "info", "the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most",
            f"{spread(vals)}; {spread(peak)}; {spread(tpeak)}; {spread(gap)}", "-", "info")
        add("ZERO_SLACK", "info", "unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it)",
            f"{spread([mean_col(df, 'unemployment', m0, m1) for _, df in z])}; "
            f"{spread([mean_col(t, 'unemployment', m0, m1) for _, _, t in zt])}", "-", "info")
        add("ZERO_SLACK", "info", f"at m{m1} against twin: fx; rent", "; ".join(
            spread([at(df, c, m1) / at(twin[s], c, m1) - 1 for s, df in z]) for c in ("fx", "rent")), "-", "info")

    # ---------------------------------------------------------------- ZERO_HOT
    h = pol_runs("ZERO_HOT")
    hot = None
    if h:
        m0 = phase_start(h[0][1], "zero")
        stop0 = phase_start(h[0][1], "stop")
        hot = m0
        mid, m1 = m0 + (stop0 - m0) // 2, stop0
        end = stop0 + 120
        EXPECTED_END["ZERO_HOT"] = end
        first = [ann(df, m0, mid) for _, df in h]
        second = [ann(df, mid, m1) for _, df in h]
        ht = [(s, df, twin[s]) for s, df in h if twin.get(s) is not None]
        # H1 as restated (THE PREMISES RESTATED): each half against its twin's, and the second no faster.
        over1 = [ann(df, m0, mid) - ann(t, m0, mid) for _, df, t in ht]
        over2 = [ann(df, mid, m1) - ann(t, mid, m1) for _, df, t in ht]
        accel = [b - a_ for a_, b in zip(first, second)]
        burst = (med(over1) >= HOT_PERSIST_MIN and med(over2) >= HOT_PERSIST_MIN
                 and med(accel) < HOT_ACCEL_MIN and len(ht) == len(h))
        add("ZERO_HOT", "info", f"inflation m{m0}-{mid} (1st half); m{mid}-{m1} (2nd half); index over twin at m{mid}, m{m1}, m{end}",
            f"{spread(first)}; {spread(second)}; "
            + "; ".join(spread([at(df, 'index', m) / at(twin[s], 'index', m) - 1 for s, df in h]) for m in (mid, m1, end)),
            "-", "info")
        add("ZERO_HOT", "H1", f"at 0%, minus the AUTO2 twin: m{m0}-{mid} (1st half); m{mid}-{m1} (2nd half); 2nd minus 1st",
            f"{spread(over1)}; {spread(over2)}; {spread(accel)}",
            f"≥ +{fmt(HOT_PERSIST_MIN)}; ≥ +{fmt(HOT_PERSIST_MIN)}; < +{fmt(HOT_ACCEL_MIN)}*", burst)
        # H2 as restated (THE PREMISES RESTATED, AGAIN): the tail against its twin's, below the 2nd half's excess.
        tail = [ann(df, end - STOP_TAIL, end) for _, df in h]
        tail_over = [ann(df, end - STOP_TAIL, end) - ann(t, end - STOP_TAIL, end) for _, df, t in ht]
        below = sum(1 for t, s2 in zip(tail_over, over2) if t < s2)
        ok = med(tail_over) <= HOT_STOP_EXCESS and below >= SEEDS_MOST and len(ht) == len(h)
        add("ZERO_HOT", "H2", f"inflation m{end - STOP_TAIL}-{end} (autopilot back) minus the AUTO2 twin's",
            spread(tail_over),
            f"median ≤ +{fmt(HOT_STOP_EXCESS)}, below the 2nd half's excess in ≥ {SEEDS_MOST}*",
            ok if burst else "VACUOUS", f"no burst to stop; the numbers alone would {'pass' if ok else 'fail'}"
            if not burst else f"{below}/{len(ht)} below")
        add("ZERO_HOT", "info", f"inflation m{end - STOP_TAIL}-{end}: the run's; the twin's (H2 until EN1: the run's at most "
            f"{fmt(HOT_STOP_MAX)})",
            f"{spread(tail)}; {spread([ann(t, end - STOP_TAIL, end) for _, _, t in ht])}", "-", "info")
        rate = [mean_col(df, "unemployment", m0, m1) - mean_col(twin[s], "unemployment", m0, m1)
                for s, df in h if twin.get(s) is not None]
        add("ZERO_HOT", "info", "unemployment at 0% minus twin (points)", spread(rate), "-", "info")
        fxr = [at(df, "fx", m1) / at(twin[s], "fx", m1) - 1 for s, df in h if twin.get(s) is not None]
        add("ZERO_HOT", "info", f"fx at m{m1} against twin (+ = weaker)", spread(fxr), "-", "info")

    # ---------------------------------------------------------------- SHOCK
    k = pol_runs("SHOCK")
    if k:
        m0 = phase_start(k[0][1], "shock")
        end = EXPECTED_END["SHOCK"]
        gaps, excess, tails, parts = [], [], [], []
        for s, df in k:
            t = twin.get(s)
            if t is None:
                continue
            g = [at(df, "index", m) / at(t, "index", m) - 1 for m in range(m0 + 1, m0 + SHOCK_STEP_WINDOW + 1)]
            gaps.append(max(g))
            pk = m0 + 1 + g.index(max(g))
            parts.append((at(df, "shelf", pk) / at(t, "shelf", pk) - 1, at(df, "rent", pk) / at(t, "rent", pk) - 1,
                          at(df, "fx", pk) / at(t, "fx", pk) - 1))
            excess.append(ann(df, end - SETTLE_TAIL, end) - ann(t, end - SETTLE_TAIL, end))
            tails.append(ann(df, end - SETTLE_TAIL, end))
        add("SHOCK", "K1", f"index over its twin, highest within {SHOCK_STEP_WINDOW} months", spread(gaps),
            f"median ≥ {fmt(SHOCK_STEP_MIN)}*", med(gaps) >= SHOCK_STEP_MIN)
        ok = med(tails) <= TARGET + BAND and med(excess) <= SHOCK_SPIRAL_EXCESS
        add("SHOCK", "K2", f"inflation m{end - SETTLE_TAIL}-{end}; minus the twin's, a year",
            f"{spread(tails)}; {spread(excess)}", f"≤ {fmt(TARGET + BAND)}; ≤ {fmt(SHOCK_SPIRAL_EXCESS)}*", ok)
        add("SHOCK", "info", "at the step's peak against twin: shelf; rent; fx",
            "; ".join(spread([x[i] for x in parts]) for i in range(3)), "-", "info")
        lvl = [at(df, "world_level", m0 + 1) / at(twin[s], "world_level", m0 + 1) - 1 for s, df in k if twin.get(s) is not None]
        add("SHOCK", "info", "world level at the shock against twin", spread(lvl), "-", "info")

    # ---------------------------------------------------------------- HIGH
    r = pol_runs("HIGH")
    if r:
        m0 = phase_start(r[0][1], "high")
        m1 = EXPECTED_END["HIGH"]
        fall = [ann(df, m0, m1) - ann(twin[s], m0, m1) for s, df in r if twin.get(s) is not None]
        add("HIGH", "R1", f"inflation m{m0}-{m1} at 15% minus twin", spread(fall),
            f"median ≤ {fmt(HIGH_FALL)}*", med(fall) <= HIGH_FALL)
        cost = [mean_col(df, "unemployment", m0, m1) - mean_col(twin[s], "unemployment", m0, m1)
                for s, df in r if twin.get(s) is not None]
        add("HIGH", "R2", "unemployment minus twin (points)", spread(cost),
            f"median ≥ +{fmt(HIGH_COST)}*", med(cost) >= HIGH_COST)
        trap = [ann(df, m1 - TRAP_TAIL, m1) - ann(twin[s], m1 - TRAP_TAIL, m1) for s, df in r if twin.get(s) is not None]
        up = sum(1 for v in trap if v > 0.01)
        add("HIGH", "R3", f"inflation m{m1 - TRAP_TAIL}-{m1} minus twin", spread(trap),
            f"median ≤ 0, ≤ {TRAP_SEEDS} seeds > +1 pt*", med(trap) <= 0 and up <= TRAP_SEEDS, f"{up} above +1 pt")
        dep = [mean_col(df, "deposit_rate", m0, m1) for _, df in r]
        add("HIGH", "info", "deposit rate in the window", spread(dep), "-", "info")
        add("HIGH", "info", f"at m{m1} against twin: shelf; rent; fx",
            "; ".join(spread([at(df, c, m1) / at(twin[s], c, m1) - 1 for s, df in r]) for c in ("shelf", "rent", "fx")),
            "-", "info")

    # ---------------------------------------------------------------- ALL
    if a:
        took = [s for s, df in a if df["pop"].max() >= TAKEOFF_POP]
        when = [int(df.loc[df["pop"] >= TAKEOFF_POP, "month"].iloc[0]) for s, df in a if s in took]
        b = base["takeoffs"] if base else None
        add("ALL", "T1", f"AUTO2 seeds reaching {TAKEOFF_POP:,} people (month reached)",
            f"{len(took)}/{len(a)} (m{spread(when, False)})",
            f"≥ baseline {b} - {TAKEOFF_SLACK}*" if b is not None else "baseline (this run)",
            (len(took) >= b - TAKEOFF_SLACK) if b is not None else "info")
        summary["takeoffs"] = len(took)
    for p in POLICIES:
        pr = pol_runs(p)
        if not pr:
            continue
        finals = [at(df, "pop", int(df["month"].max())) for _, df in pr]
        mfin = med(finals)
        bp = base["policies"].get(p, {}).get("final_pop_median") if base else None
        add(p, "T2", "final population", spread(finals, False),
            f"≥ {fmt(POP_KEEP)} of baseline {fmt(bp, False)}*" if bp else "baseline (this run)",
            (mfin >= POP_KEEP * bp) if bp else "info")
        if p != "AUTO2":
            kept = [at(df, "pop", int(df["month"].max())) / at(twin[s], "pop", int(df["month"].max()))
                    for s, df in pr if twin.get(s) is not None]
            add(p, "info", "final population over the AUTO2 twin's, same month", spread(kept), "-", "info")
        bad = sum(int((df["audit_ok"] == 0).sum()) for _, df in pr)
        nan = sum(int(df["audit_ok"].isna().sum()) for _, df in pr)
        fnd = [s for s, df in pr if df["findings_new"].fillna(0).sum() > 0]
        names = sorted({n for _, df in pr for n in df["finding"].dropna().astype(str) if n})
        summary["policies"][p] = {"final_pop_median": mfin, "runs_with_findings": len(fnd),
                                  "audit_months_failed": bad}
        add(p, "M1", "months the money audit failed (unread)", f"{bad} ({nan})", "0", bad == 0 and nan == 0)
        bf = base["policies"].get(p, {}).get("runs_with_findings") if base else None
        add(p, "F1", "runs with a LongPlaytest finding", f"{len(fnd)}/{len(pr)} {names[:3]}",
            f"≤ baseline {bf}" if bf is not None else "baseline (this run)",
            (len(fnd) <= bf) if bf is not None else "info")
    # THE PAIRING, CHECKED: before its switch every run must be its AUTO2 twin to the bit.
    same = total = 0
    for (p, s), df in runs.items():
        t = twin.get(s)
        if p == "AUTO2" or t is None:
            continue
        pre = df[df["phase"] == "pre"]
        if not len(pre):
            continue
        total += 1
        cols = ["pop", "index", "fx", "policy_rate"]
        tt = t.loc[t.index.intersection(pre.index), cols]
        same += int(len(tt) == len(pre) and (tt.values == pre[cols].values).all())
    add("ALL", "pairing", "runs identical to their AUTO2 twin before the switch", f"{same}/{total}", "all", same == total)
    missing = [(p, s) for p in POLICIES for s in seeds if (p, s) not in runs]
    short = [(p, s) for (p, s), df in runs.items() if p in EXPECTED_END and df["month"].max() < EXPECTED_END[p]]

    # ---------------------------------------------------------------- print
    print(f"# Ensemble score: {outdir}  ({len(seeds)} seeds; ZERO_HOT from month {hot})")
    print()
    print("| policy | id | measure | median [min to max] | threshold | verdict |")
    print("|---|---|---|---|---|---|")
    for pol, cid, what, value, thr, verdict in rows:
        print(f"| {pol} | {cid} | {what} | {value} | {thr} | {verdict} |")
    crit = [r_ for r_ in rows if r_[1] not in ("info", "pairing") and r_[5].startswith(("PASS", "FAIL"))]
    npass = sum(1 for r_ in crit if r_[5].startswith("PASS"))
    vac = sum(1 for r_ in rows if r_[5].startswith("VACUOUS"))
    print()
    print(f"criteria passed: {npass}/{len(crit)} (vacuous, not counted: {vac}); "
          f"missing runs: {missing or 'none'}; short runs: {short or 'none'}")
    times = os.path.join(outdir, "times.txt")
    if os.path.exists(times):
        t = pd.read_csv(times, sep=r"\s+", names=["policy", "seed", "months", "seconds"])
        per = t.groupby("policy")["seconds"].median()
        print("time per run (median s): " + ", ".join(f"{p} {per[p]:.1f}" for p in POLICIES if p in per)
              + f"; total {t['seconds'].sum() / 60:.1f} min")
    if summary_out:
        with open(summary_out, "w", newline="\n") as f:
            json.dump(summary, f, indent=1)


if __name__ == "__main__":
    main()
