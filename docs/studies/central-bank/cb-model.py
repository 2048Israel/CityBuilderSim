r"""The central bank study's model (docs/studies/spec-central-bank.md).

Everything the study's tables say is printed by this file. It copies the
game's constants by name (main at fd36e1a, 0.8.11) and marks the study's own
as PROPOSED. It reads three data files beside it, all taken read-only from
Jerus's saves:

    halifax.csv          slot 3 ("for claude", Halifax, 0.8.10), its history, month by month (extract.py)
    halifax-replay.csv   a COPY of slot 3 played 120 months on in a scratch folder by probe/CbProbe.java.txt
    halifax-240-*.csv    COPIES of slot 3 played 240 months on, the dial on the autopilot or in steps (probe/CbSteps.java.txt)
    bergen-replay.csv    a COPY of slot 1 (Bergen) played 3 months the same way

    python -I cb-model.py            the report
    python -I cb-model.py --json     ...and boards.json, the figures the boards draw

Stdlib only.
"""
import csv, json, math, os, sys

HERE = os.path.dirname(os.path.abspath(__file__))

# ------------------------------------------------------------------ the game's constants, by name
NEUTRAL_RATE = .03                 # DebtManager.NEUTRAL_RATE
DEFAULT_INFLATION_TARGET = .02     # DebtManager.DEFAULT_INFLATION_TARGET
TAYLOR_WEIGHT = 1.5                # DebtManager.TAYLOR_WEIGHT (Standard)
MIN_POLICY_RATE = 0.0              # DebtManager.MIN_POLICY_RATE
MAX_POLICY_RATE = 1.00             # DebtManager.MAX_POLICY_RATE
WINDOW_PENALTY = .0025             # CentralBank.WINDOW_PENALTY
INFLATION_LAG = 12                 # PriceIndex.WINDOW (13) less one: inflation is the index over the index twelve months back
ADAPT_MONTHS = 12                  # Expectations.ADAPT_MONTHS
DRIFT_MONTHS = 6                   # spec-city-debt D15 (CD1; taken, not yet in the code)

# ------------------------------------------------------------------ PROPOSED by this study
STEP = .0025                       # PROPOSED: the dial moves in quarter points
RANGE_WIDTH = .0025                # PROPOSED: a target range a quarter point wide (3.50-3.75%)
IORB_UNDER_TOP = .0010             # PROPOSED: interest on reserves ten points under the top, where the Fed sets it
DECISION_MONTHS = 2                # PROPOSED: a decision every two months, six a year (Jerus said three; decision 1)
MAX_STEP = .0075                   # PROPOSED: the most the autopilot moves at a scheduled decision
EMERGENCY_GAP = .03                # PROPOSED: the rule this far from the overnight calls an unscheduled decision
EMERGENCY_STEP = .0100             # PROPOSED: ...which moves at most this far
OPERATING_COST = .0010             # PROPOSED: a year, of total assets (the Fed's run near a tenth of a percent)


def rule(inflation, target=DEFAULT_INFLATION_TARGET):
    """DebtManager.ruleRate() at Standard, held inside the dial's bounds (advisedPolicyRate())."""
    r = NEUTRAL_RATE + (target - DEFAULT_INFLATION_TARGET) + TAYLOR_WEIGHT * (inflation - target)
    return max(MIN_POLICY_RATE, min(MAX_POLICY_RATE, r))


def range_for(rate):
    """The range whose IORB - where the overnight sits in a flush city - is nearest this rate: its bottom (PROPOSED, decision 3)."""
    return max(0.0, round((rate - (RANGE_WIDTH - IORB_UNDER_TOP)) / STEP) * STEP)


def iorb_of(bottom):
    return bottom + RANGE_WIDTH - IORB_UNDER_TOP


def corridor(bottom, window_share=0.0):
    """The rates of a range, and the overnight at a funding position (Bank.windowShare(), 0 flush to 1 at the window)."""
    top = bottom + RANGE_WIDTH
    iorb = top - IORB_UNDER_TOP
    overnight = iorb + (top - iorb) * max(0.0, min(1.0, window_share))
    return {"bottom": bottom, "top": top, "rrp": bottom, "iorb": iorb, "window": top, "overnight": overnight}


def load(name, skip=0):
    with open(os.path.join(HERE, name)) as f:
        lines = f.readlines()[skip:]
    return list(csv.DictReader(lines))


def num(r, k):
    v = r.get(k, "")
    return float(v) if v not in ("", None) else 0.0


# ================================================================== 1. the dial today, replayed
def halifax_inflation(rows):
    """Inflation as the autopilot read it: last month's index over the index twelve months before it."""
    idx = [num(r, "priceIndex") for r in rows]
    out = [None] * len(rows)
    for i in range(INFLATION_LAG + 1, len(rows)):
        back = idx[i - 1 - INFLATION_LAG]
        out[i] = idx[i - 1] / back - 1 if back > 0 else None
    return out


def stepped(inflation, every, max_step, emergency_gap=None, emergency_step=None, start=0.03):
    """The overnight in a flush city (the range's IORB), month by month, under the step rule: at a decision, toward the rule in quarter points, at most max_step."""
    bottom = range_for(start)
    mids, unscheduled = [], 0
    for t, pi in enumerate(inflation):
        if pi is None:
            mids.append(None)
            continue
        target = rule(pi)
        gap = target - iorb_of(bottom)
        scheduled = (t % every == 0)
        urgent = emergency_gap is not None and abs(gap) >= emergency_gap
        if scheduled or urgent:
            limit = max_step if scheduled else emergency_step
            if urgent and scheduled:
                limit = max(max_step, emergency_step)
            if abs(gap) > STEP / 2:
                move = min(round(abs(gap) / STEP) * STEP, limit)
                bottom = max(0.0, bottom + math.copysign(move, gap))
                if urgent and not scheduled:
                    unscheduled += 1
        mids.append(iorb_of(bottom))
    return mids, unscheduled


def measure(path, inflation):
    pts = [(p, rule(pi)) for p, pi in zip(path, inflation) if p is not None and pi is not None]
    moves = [abs(pts[i][0] - pts[i - 1][0]) for i in range(1, len(pts))]
    gaps = [abs(p - r) for p, r in pts]
    return {
        "changes": sum(1 for m in moves if m > 1e-9),
        "distinct": len({round(p, 6) for p, _ in pts}),
        "biggest_move_pt": 100 * max(moves),
        "mean_move_bp": 10000 * sum(moves) / len(moves),
        "mean_gap_bp": 10000 * sum(gaps) / len(gaps),
        "months_gap_over_1pt": sum(1 for g in gaps if g > .01),
        "max_gap_pt": 100 * max(gaps),
        "months": len(pts),
    }


def section_dial(report, boards):
    rows = load("halifax.csv")
    inflation = halifax_inflation(rows)
    dial = [num(r, "policyRate") for r in rows]
    on_rule = sum(1 for d, pi in zip(dial, inflation) if pi is not None and abs(d - rule(pi)) < 1.01e-4)
    counted = sum(1 for pi in inflation if pi is not None)
    report.append("1. THE DIAL TODAY, AND IN STEPS, ON HALIFAX'S OWN INFLATION (slot 3, months %s-%s)"
                  % (rows[INFLATION_LAG + 1]["month"], rows[-1]["month"]))
    report.append("   the saved dial equals the rule on last month's year-on-year inflation in %d of %d months "
                  "(the rest are months with the dial in the player's hand)" % (on_rule, counted))
    today = [d if pi is not None else None for d, pi in zip(dial, inflation)]
    variants = [
        ("today: the dial as saved (monthly, any value)", today, 0),
    ]
    rule_path = [rule(pi) if pi is not None else None for pi in inflation]
    variants.append(("the rule itself, every month (what autopilot would set)", rule_path, 0))
    for every, step, gap, estep, label in [
        (1, .0025, None, None, "every month, a quarter point at most"),
        (2, .0050, None, None, "every 2 months, half a point at most"),
        (3, .0050, None, None, "every 3 months, half a point at most"),
        (3, .0075, None, None, "every 3 months, three quarters at most"),
        (3, .0100, None, None, "every 3 months, a point at most"),
        (3, MAX_STEP, EMERGENCY_GAP, EMERGENCY_STEP, "every 3 months, 3/4 at most, unscheduled past 3 points"),
        (2, MAX_STEP, EMERGENCY_GAP, EMERGENCY_STEP, "every 2 months, 3/4 at most, unscheduled past 3 points (PROPOSED)"),
    ]:
        path, uns = stepped(inflation, every, step, gap, estep)
        variants.append((label, path, uns))
    report.append("   %-66s %7s %8s %9s %9s %8s %8s %6s" % ("rule", "changes", "values", "big move", "mean |rule|", ">1pt", "max gap", "unsch."))
    table = []
    for label, path, uns in variants:
        m = measure(path, inflation)
        table.append(dict(label=label, unscheduled=uns, **m))
        report.append("   %-66s %7d %8d %8.2fpt %8.0fbp %8d %7.2fpt %6d"
                      % (label, m["changes"], m["distinct"], m["biggest_move_pt"], m["mean_gap_bp"],
                         m["months_gap_over_1pt"], m["max_gap_pt"], uns))
    report.append("   (open loop: inflation is the city's as it happened, so it does not answer to the stepped dial;"
                  " 'mean |rule|' is how far the rate sat from what the rule wanted)")
    proposed = variants[-1][1]
    boards["halifax_dial"] = {
        "month": [int(r["month"]) for r in rows],
        "today": [round(100 * d, 3) if d is not None else None for d in today],
        "rule": [round(100 * d, 3) if d is not None else None for d in rule_path],
        "stepped": [round(100 * d, 3) if d is not None else None for d in proposed],
        "table": table,
    }
    # the copy of slot 3 played on, in the current build
    rep = load("halifax-replay.csv", skip=1)
    d = [num(r, "dial") for r in rep]
    jumps = [abs(d[i] - d[i - 1]) for i in range(1, len(d))]
    report.append("   the copy of slot 3 played %d months on, in 0.8.11: the dial ran %.2f%% to %.2f%%, moved a quarter point"
                  " or more in %d of %d months, biggest move %.2f points, %d different values"
                  % (len(d), 100 * min(d), 100 * max(d), sum(j >= .0025 - 1e-9 for j in jumps), len(jumps),
                     100 * max(jumps), len({round(x, 6) for x in d})))
    boards["halifax_replay_dial"] = {"month": [int(r["month"]) for r in rep], "dial": [round(100 * x, 3) for x in d]}
    report.append("")


# ================================================================== 1b. the same, in the game
RUNS = [("today: the autopilot as it is", "halifax-240-today.csv"),
        ("every 3 months, 3/4 at most, unscheduled past 3 points", "halifax-240-every3.csv"),
        ("every 3 months, a point at most, never unscheduled", "halifax-240-every3-cap1.csv"),
        ("every 2 months, 3/4 at most, unscheduled past 3 points", "halifax-240-every2.csv")]


def section_runs(report, boards):
    report.append("1b. THE SAME CITY PLAYED 240 MONTHS ON IN THE GAME (copies of slot 3, 0.8.11, probe/CbSteps.java.txt; one run each)")
    report.append("   %-56s %5s %5s %8s %13s %7s %6s %7s %7s %6s %10s %8s %4s"
                  % ("rule", "moves", "vals", "biggest", "dial range", "infl.", "sd", "|-2%|", ">3pt off", "unemp", "GDP/month", "people", "uns."))
    out = []
    for label, name in RUNS:
        rows = load(name)
        d = [num(r, "dial") for r in rows]
        pi = [num(r, "inflation") for r in rows]
        u = [num(r, "unemployment") for r in rows]
        g = [num(r, "gdp") for r in rows]
        jumps = [abs(d[i] - d[i - 1]) for i in range(1, len(d))]
        mean = sum(pi) / len(pi)
        s = {"label": label, "moves": sum(j > 1e-9 for j in jumps), "values": len({round(x, 6) for x in d}),
             "biggest": 100 * max(jumps), "dial_lo": 100 * min(d), "dial_hi": 100 * max(d), "infl": 100 * mean,
             "sd": 100 * (sum((x - mean) ** 2 for x in pi) / len(pi)) ** .5,
             "miss": 100 * sum(abs(x - DEFAULT_INFLATION_TARGET) for x in pi) / len(pi),
             "off3": sum(abs(x - DEFAULT_INFLATION_TARGET) > .03 for x in pi), "unemp": 100 * sum(u) / len(u),
             "gdp": sum(g) / len(g), "people": int(num(rows[-1], "population")),
             "unscheduled": sum(1 for r in rows if r.get("decision") == "unscheduled"),
             "month": [int(r["month"]) for r in rows], "dial": [round(100 * x, 3) for x in d],
             "inflation": [round(100 * x, 2) for x in pi]}
        out.append(s)
        report.append("   %-56s %5d %5d %7.2fpt %5.2f-%5.2f%% %6.2f%% %5.2f %6.2fpt %7d %5.1f%% %10.0f %8d %4d"
                      % (label, s["moves"], s["values"], s["biggest"], s["dial_lo"], s["dial_hi"], s["infl"], s["sd"],
                         s["miss"], s["off3"], s["unemp"], s["gdp"], s["people"], s["unscheduled"]))
    report.append("   (the stepped runs set the dial to the range's IORB, ten points under the top, and hand it to nothing else;"
                  " the game is deterministic, so each line is one path, not a distribution)")
    boards["runs"] = out
    report.append("")


# ================================================================== 2. the corridor
def section_corridor(report, boards):
    report.append("2. THE RANGE AND THE OVERNIGHT RATE (PROPOSED)")
    report.append("   %-12s %7s %7s %7s %7s | overnight: flush  half  at the window | today at the same dial: reserves  window"
                  % ("range", "RRP", "IORB", "window", ""))
    rows = []
    for bottom in (0.0, .0275, .0350, .0525, .0800):
        c0, c5, c1 = corridor(bottom, 0), corridor(bottom, .5), corridor(bottom, 1)
        mid = bottom + RANGE_WIDTH / 2
        report.append("   %5.2f-%5.2f%% %6.2f%% %6.2f%% %6.2f%% %7s |       %6.2f%% %6.2f%% %6.2f%%      |                %6.2f%%  %6.2f%%"
                      % (100 * bottom, 100 * c0["top"], 100 * c0["rrp"], 100 * c0["iorb"], 100 * c0["window"], "",
                         100 * c0["overnight"], 100 * c5["overnight"], 100 * c1["overnight"], 100 * mid, 100 * (mid + WINDOW_PENALTY)))
        rows.append({"bottom": bottom, "flush": c0["overnight"], "half": c5["overnight"], "short": c1["overnight"],
                     "iorb": c0["iorb"], "top": c0["top"]})
    report.append("   (the Fed, 17 Sept 2026: range 3.75-4.00%, reverse repo 3.75%, reserves 3.90%, window 4.00%)")
    boards["corridor"] = rows
    report.append("")


# ================================================================== 3. the remittance
def statement(lines):
    return sum(v for _, v in lines)


def section_remittance(report, boards):
    rows = {r["month"]: r for r in load("halifax.csv")}
    report.append("3. THE REMITTANCE PUZZLE AND THE PROPOSED STATEMENT")
    # 3a. the identity, month by month, in the saved history
    window_hits, overdraft_hits = [], []
    ms = sorted(rows, key=int)
    for a, b in zip(ms, ms[1:]):
        ra, rb = rows[a], rows[b]
        rem, rev = num(rb, "remittance"), num(rb, "revenue")
        if rem <= 0 or rev <= 0:
            continue
        adv, m0, dial = num(ra, "advancesToTreasury"), num(ra, "m0"), num(ra, "policyRate")
        if adv < 1 and m0 > 0:
            window_hits.append((int(b), rem, m0 * (dial + WINDOW_PENALTY) / 12, rem / rev))
        elif adv > 0:
            overdraft_hits.append((int(b), rem, adv * dial / 12, rem / rev))
    def fit(hits):
        good = [h for h in hits if h[2] > 0 and abs(h[1] / h[2] - 1) < .05]
        return len(good), len(hits), max((h[3] for h in hits), default=0)
    g, n, top = fit(window_hits)
    report.append("   cash, no overdraft, no paper: remittance = last month's window loans x (dial + %.2f%%) / 12 within 5%% in %d of %d months;"
                  " the most it reached: %.0f%% of revenue" % (100 * WINDOW_PENALTY, g, n, 100 * top))
    g2, n2, top2 = fit(overdraft_hits)
    report.append("   treasury overdrawn: remittance = last month's advances x dial / 12 within 5%% in %d of %d months;"
                  " the most it reached: %.0f%% of revenue" % (g2, n2, 100 * top2))
    rep = load("halifax-replay.csv", skip=1)
    tot = {k: sum(num(r, k) for r in rep) for k in ("window", "advances", "coupons", "gains", "ior", "remitted", "revenue")}
    report.append("   the copy played 120 months on (cash throughout, no overdraft, no paper): remitted D$%.0f in all, of which the window"
                  " D$%.0f, on D$%.0f of revenue" % (tot["remitted"], tot["window"], tot["revenue"]))
    berg = load("bergen-replay.csv", skip=1)
    report.append("   Bergen (slot 1) played 3 months on: interest on reserves D$%.1fM a month on income of nothing; loss carried D$%.2fB"
                  " and growing; reserves D$%.2fB, all of it money made to pay that interest"
                  % (num(berg[0], "ior") / 1e6, num(berg[-1], "lossCarried") / 1e9, num(berg[-1], "reserves") / 1e9))

    # 3b. the four months, today and proposed
    cases = []
    def case(name, month, today_lines, l1_lines, l2_lines, note):
        cases.append({"name": name, "month": month, "today": today_lines, "l1": l1_lines, "l2": l2_lines, "note": note})

    # A. Halifax m440 -> m441: the window
    r = rows["440"]
    W, dial = num(r, "m0"), num(r, "policyRate")
    c = corridor(range_for(dial), 1.0)
    case("Halifax, month 441: the bank at the window", 441,
         [("window interest, at the dial + 0.25", W * (dial + WINDOW_PENALTY) / 12)],
         [("window interest, at the top of the range", W * c["window"] / 12),
          ("operating costs", -W * OPERATING_COST / 12)],
         [("window interest, at the top of the range", W * c["window"] / 12),
          ("interest on reserves (none: the bank is overdrawn)", 0.0),
          ("operating costs", -W * OPERATING_COST / 12)],
         "remitted in the game: D$%.0f; revenue that month D$%.0f" % (num(rows["441"], "remittance"), num(rows["441"], "revenue")))
    # B. Halifax m268 -> m269: the overdraft
    r = rows["268"]
    A, dial = num(r, "advancesToTreasury"), num(r, "policyRate")
    c = corridor(range_for(dial), 1.0)
    case("Halifax, month 269: the treasury overdrawn", 269,
         [("the treasury's interest on its advances, at the dial", A * dial / 12)],
         [("the treasury's interest on its advances, at the top of the range", A * c["window"] / 12),
          ("operating costs", -A * OPERATING_COST / 12)],
         [("the treasury's interest on its advances, at the top of the range", A * c["window"] / 12),
          ("interest on the reserves the advance became, at IORB", -A * c["iorb"] / 12),
          ("operating costs", -A * OPERATING_COST / 12)],
         "remitted in the game: D$%.0f, %.0f%% of revenue, all of it the treasury's own interest coming back"
         % (num(rows["269"], "remittance"), 100 * num(rows["269"], "remittance") / num(rows["269"], "revenue")))
    # C. slot 6, month 267: paper bought, nothing paid on the money that bought it (0.7.49 save; the same rule in 0.8.11)
    P, coupons, dial6 = 3867921.0, 8180.0, .0417
    c = corridor(range_for(dial6), 0.0)
    case("Slot 6, month 267: the city's paper held", 267,
         [("coupons on its paper", coupons), ("interest on reserves (none: the bank's cash is negative)", 0.0)],
         [("coupons on its paper (effective interest; no gain on the day it buys)", coupons),
          ("interest on reserves (none: the bank's cash is negative)", 0.0),
          ("operating costs", -P * OPERATING_COST / 12)],
         [("coupons on its paper", coupons),
          ("interest on the reserves its purchases made, at IORB", -P * c["iorb"] / 12),
          ("operating costs", -P * OPERATING_COST / 12)],
         "paper D$3.87M at face, dial 4.17%, the bank's cash -D$1.75M; remitted D$8,180 a month, 19-20% of revenue")
    # D. Bergen m3003: reserves it never made
    ior = num(berg[0], "ior")
    dialB = num(berg[0], "dial")
    c = corridor(range_for(dialB), 0.0)
    cashB = num(berg[0], "bankCash")
    case("Bergen, month 3003: the bank flush with money from outside", 3003,
         [("interest on reserves, at the dial, on the bank's spare cash", -ior)],
         [("interest on reserves, at IORB, on the bank's spare cash", -cashB * c["iorb"] / 12)],
         [("interest on reserves, at IORB, on the bank's spare cash and its unlent deposits (more; not in the history)", -cashB * c["iorb"] / 12)],
         "no assets earning; loss carried D$35.2B, all of it interest paid; the Fed's deferred asset was $243.5B at the end of 2025")
    for k in cases:
        report.append("   %s" % k["name"])
        for key, title in (("today", "today"), ("l1", "proposed (CB3)"), ("l2", "reserves as a real central bank's (decision 12, not a CB batch)")):
            total = statement(k[key])
            report.append("      %-56s profit D$%14s a month%s" % (title, "{:,.0f}".format(total), "" if total >= 0 else "  -> carried, nothing remitted"))
            for lab, v in k[key]:
                report.append("         %-66s %14s" % (lab, "{:,.0f}".format(v)))
        report.append("      %s" % k["note"])
    boards["cases"] = [{"name": k["name"], "note": k["note"],
                        "today": [[l, round(v)] for l, v in k["today"]],
                        "l1": [[l, round(v)] for l, v in k["l1"]],
                        "l2": [[l, round(v)] for l, v in k["l2"]]} for k in cases]
    hal = load("halifax.csv")
    boards["halifax_remittance"] = {
        "month": [int(r["month"]) for r in hal],
        "share": [round(100 * num(r, "remittance") / num(r, "revenue"), 1) if num(r, "revenue") > 0 else 0 for r in hal],
        "advances": [round(num(r, "advancesToTreasury")) for r in hal],
        "m0": [round(num(r, "m0")) for r in hal],
    }
    report.append("")


# ================================================================== 4. CD1's path as a staircase
def expected_paths(pi0, dial0, cred, months=121, every=DECISION_MONTHS, first=1, target=DEFAULT_INFLATION_TARGET):
    """CD1's smooth expected dial (spec-city-debt §3.1) and the same path as decisions in steps."""
    pinf = cred * target + (1 - cred) * pi0
    pi = [pinf + (pi0 - pinf) * math.exp(-t / ADAPT_MONTHS) for t in range(months)]
    R = [rule(p, target) for p in pi]
    smooth = [R[t] + (dial0 - R[0]) * math.exp(-t / DRIFT_MONTHS) for t in range(months)]
    stair, level = [], dial0
    for t in range(months):
        if t >= first and (t - first) % every == 0:
            level = level + max(-MAX_STEP, min(MAX_STEP, smooth[t] - level))
        stair.append(level)
    return smooth, stair


def avg(path, m):
    return sum(path[:m]) / m


def section_cd1(report, boards):
    report.append("4. CD1's EXPECTED DIAL: THE SMOOTH DRIFT AGAINST DECISIONS IN STEPS (next decision in 1 month, then every %d)" % DECISION_MONTHS)
    report.append("   the average expected short rate over the term, before credit spread and term premium (identical in both)")
    terms = (1, 3, 6, 12, 24, 60, 120)
    report.append("   %-52s " % "city" + " ".join("%7s" % ("%dm" % m) for m in terms))
    out = {}
    for name, pi0, dial0, cred in (("calm: 2%, the dial at 3%", .02, .03, .80),
                                   ("hiking: 6%, the dial at 7%, credibility 65%", .06, .07, .65),
                                   ("behind: 5%, the dial at 1% by hand, credibility 60%", .05, .01, .60)):
        smooth, stair = expected_paths(pi0, dial0, cred)
        report.append("   %-52s " % (name + ", smooth") + " ".join("%6.2f%%" % (100 * avg(smooth, m)) for m in terms))
        report.append("   %-52s " % "   in steps" + " ".join("%6.2f%%" % (100 * avg(stair, m)) for m in terms))
        report.append("   %-52s " % "   difference" + " ".join("%5.0fbp" % (10000 * (avg(stair, m) - avg(smooth, m))) for m in terms))
        out[name] = {"smooth": [round(100 * x, 3) for x in smooth[:61]], "stair": [round(100 * x, 3) for x in stair[:61]]}
    boards["cd1"] = out
    report.append("")


def main():
    report, boards = [], {}
    section_dial(report, boards)
    section_runs(report, boards)
    section_corridor(report, boards)
    section_remittance(report, boards)
    section_cd1(report, boards)
    print("\n".join(report))
    if "--json" in sys.argv:
        with open(os.path.join(HERE, "boards.json"), "w") as f:
            json.dump(boards, f, separators=(",", ":"))
        print("wrote boards.json")


if __name__ == "__main__":
    main()
