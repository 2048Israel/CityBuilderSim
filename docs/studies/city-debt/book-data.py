"""
The figures the boards draw (city-debt-study.html), made by debt-model.py's
own arithmetic: one city's book in January 2203, two years after a calm
January 2201, eighteen months into a hike - the HIKING city of the model.

    python3 book-data.py        # writes boards-data.json beside it

A design aid, not game code. The lines, their sizes and their holders are
invented for the picture; every price, yield, carrying value, gain and loss
is the model's.
"""

import importlib.util
import json
import math
import os
from dataclasses import replace

import numpy as np

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("dm", os.path.join(HERE, "debt-model.py"))
dm = importlib.util.module_from_spec(spec)
spec.loader.exec_module(dm)

NOW = dm.HIKING                      # January 2203
THEN = dm.CALM                       # January 2201
CAL0 = 2201 * 12                     # month index of January 2201
MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]


def cal(m):
    return f"{MONTHS[m % 12]} {m // 12}"


def city_at(k):
    """The city k months after January 2201: calm for six months, then the hike over twelve, held since."""
    s = min(1.0, max(0.0, (k - 6) / 12))
    lerp = lambda a, b: a + (b - a) * s
    return replace(THEN, name=f"month {k}", policy=lerp(THEN.policy, NOW.policy),
                   inflation=lerp(THEN.inflation, NOW.inflation), credibility=lerp(THEN.credibility, NOW.credibility),
                   cost_of_funds=lerp(THEN.cost_of_funds, NOW.cost_of_funds),
                   deposit_rate=lerp(THEN.deposit_rate, NOW.deposit_rate))


def ytm(flows, price):
    return dm.ytm(np.asarray(flows, float), price)


def issue_bullet(k, years, face, coupon=None):
    """A bullet sold at month k at the curve's fair yield then: its coupon, price, proceeds and effective rate."""
    c = city_at(k)
    cv = dm.Curve(c, extra_debt=face)
    months = years * 12
    fair = cv.par_yield(months)
    cpn = coupon if coupon is not None else math.floor(fair / dm.COUPON_STEP) * dm.COUPON_STEP
    flows = dm.bullet_flows(cpn, months)
    price = dm.flat_price(flows, fair)
    proceeds = price / 100 * face - dm.AUCTION_FEE
    eir = ytm(flows / 100 * face, proceeds)
    return dict(fair=fair, coupon=cpn, price=price, proceeds=proceeds, eir=eir)


def carrying(face, coupon, eir, proceeds, months_run):
    cv = proceeds
    for _ in range(months_run):
        cv = cv + cv * eir / 12 - face * coupon / 12
    return cv


def line_row(**k):
    return k


def build():
    now_k = 24
    curve = dm.Curve(NOW)
    rows = []

    def bullet_line(name, kind, years, face, issued_k, holders, coupon=None, reopen=None, callable_=None):
        iss = issue_bullet(issued_k, years, face, coupon)
        months = years * 12
        if callable_:
            # a callable line's coupon carries its option: the tree's coupon at the month it was sold
            cvi = dm.Curve(city_at(issued_k), extra_debt=face)
            cc, plain, _ = dm.callable_coupon(cvi, years, dm.RATE_VOL_FLOOR)
            cpn0 = math.floor(cc / dm.COUPON_STEP) * dm.COUPON_STEP
            tree = dm.ho_lee(cvi, months, dm.RATE_VOL_FLOOR)
            px0 = dm.callable_price(tree, cpn0, months, callable_ * 12)
            pr0 = px0 / 100 * face - dm.AUCTION_FEE
            iss = dict(fair=plain, coupon=cpn0, price=px0, proceeds=pr0,
                       eir=ytm(dm.bullet_flows(cpn0, months) / 100 * face, pr0), option=cc - plain)
        left = months - (now_k - issued_k)
        cpn = iss["coupon"]
        face_total, proceeds, cv = face, iss["proceeds"], None
        cv = carrying(face, cpn, iss["eir"], iss["proceeds"], now_k - issued_k)
        tranches = [dict(when=cal(CAL0 + issued_k), face=face, price=iss["price"], yld=iss["fair"])]
        eir = iss["eir"]
        if reopen:
            rk, rface = reopen
            # reopened at month rk: same coupon, sold at that month's yield for the months then left
            c2 = city_at(rk)
            cv2 = dm.Curve(c2, extra_debt=rface)
            m_left = months - (rk - issued_k)
            y2 = cv2.par_yield(m_left)
            p2 = dm.flat_price(dm.bullet_flows(cpn, m_left), y2)
            pr2 = p2 / 100 * rface - dm.AUCTION_FEE
            cv_at = carrying(face, cpn, iss["eir"], iss["proceeds"], rk - issued_k) + pr2
            face_total = face + rface
            # the blended effective rate: the line's carrying value against its remaining flows
            eir = ytm(dm.bullet_flows(cpn, m_left) / 100 * face_total, cv_at)
            cv = carrying(face_total, cpn, eir, cv_at, now_k - rk)
            tranches.append(dict(when=cal(CAL0 + rk), face=rface, price=p2, yld=y2))
        flows = dm.bullet_flows(cpn, left)
        if callable_:
            first_k = issued_k + callable_ * 12
            rates = dm.ho_lee(curve, left, dm.RATE_VOL_FLOOR)
            price = dm.callable_price(rates, cpn, left, max(0, first_k - now_k))
            first = cal(CAL0 + first_k)
        else:
            price = curve.pv(flows)
            first = None
        y = ytm(flows, price)
        mv = price / 100 * face_total
        dur = dm.mod_duration(flows, y)
        rows.append(dict(name=name, kind=kind, coupon=cpn, issued=cal(CAL0 + issued_k), due=cal(CAL0 + issued_k + months),
                         left=left, face=face_total, eir=eir, cv=cv, price=price, ytm=y, mv=mv,
                         gl_cv=cv - mv, gl_face=face_total - mv, dur=dur, dv01=mv * dur / 1e4,
                         holders=holders, tranches=tranches, first_call=first, callable=bool(callable_),
                         option=iss.get("option")))

    # the bill: sold last month in the hike
    bill_face = 150e6
    p_then = 100 * dm.Curve(city_at(23)).df[3]
    p_now = 100 * curve.df[2]
    rows.append(dict(name="3-month bill", kind="BILL", coupon=0.0, issued=cal(CAL0 + 23), due=cal(CAL0 + 26), left=2,
                     face=bill_face, eir=(100 / p_then) ** (12 / 3) * 0 + ((100 / p_then) ** (1 / 3) - 1) * 12,
                     cv=bill_face * p_then / 100 * (100 / p_then) ** (1 / 3), price=p_now,
                     ytm=((100 / p_now) ** (1 / 2) - 1) * 12, mv=bill_face * p_now / 100,
                     gl_cv=0, gl_face=0, dur=2 / 12, dv01=bill_face * p_now / 100 * (2 / 12) / 1e4,
                     holders=dict(households=.0, bank=.85, world=.15, cb=0), tranches=[], first_call=None, callable=False))
    r = rows[-1]
    r["gl_cv"] = r["cv"] - r["mv"]
    r["gl_face"] = r["face"] - r["mv"]

    bullet_line("5-year bond", "BOND", 5, 400e6, 0, dict(households=.38, bank=.52, world=.04, cb=.06))
    bullet_line("10-year bond", "BOND", 10, 300e6, 0, dict(households=.30, bank=.55, world=.10, cb=.05))
    bullet_line("30-year bond", "BOND", 30, 500e6, 0, dict(households=.30, bank=.52, world=.06, cb=.12))
    bullet_line("10-year bond", "BOND", 10, 200e6, 18, dict(households=.31, bank=.61, world=.08, cb=.0),
                reopen=(21, 100e6))
    bullet_line("30-year callable", "BOND", 30, 200e6, 18, dict(households=.40, bank=.50, world=.10, cb=.0),
                callable_=10)

    # the amortizing bond: ten annual slices, sold in the calm month
    cv0 = dm.Curve(city_at(0), extra_debt=250e6)
    ac = dm.amortizing_coupon(cv0, 10)
    face0 = 250e6
    flows_all = dm.amortizing_flows(ac, 10, face0)
    proceeds = face0 - dm.AUCTION_FEE
    eir = ytm(flows_all, proceeds)
    cvv = proceeds
    for m in range(24):
        cvv = cvv * (1 + eir / 12) - flows_all[m]
    left_flows = flows_all[24:]
    price_total = curve.pv(left_flows)
    face_left = face0 * 8 / 10
    y = ytm(left_flows, price_total)
    dur = dm.mod_duration(left_flows, y)
    rows.append(dict(name="10-year amortizing", kind="AMORT", coupon=ac, issued=cal(CAL0), due=cal(CAL0 + 120), left=96,
                     face=face_left, eir=eir, cv=cvv, price=price_total / face_left * 100, ytm=y, mv=price_total,
                     gl_cv=cvv - price_total, gl_face=face_left - price_total, dur=dur, dv01=price_total * dur / 1e4,
                     holders=dict(households=.45, bank=.50, world=.05, cb=0), tranches=[], first_call=None,
                     callable=False, old_coupon=cv0.par_yield(120)))

    # the floating note: policy + its margin, sold in the calm month; its price moves only with the credit spread
    cvf = dm.Curve(city_at(0), extra_debt=150e6)
    mg = dm.frn_margin(cvf, 60)
    p = curve.short[:36]
    df = curve.df[1:37]
    fpv = float(np.dot((p + mg) / 12 * 100, df)) + 100 * curve.df[36]
    rows.append(dict(name="5-year floating", kind="FRN", coupon=mg, issued=cal(CAL0), due=cal(CAL0 + 60), left=36,
                     face=150e6, eir=None, cv=150e6 - dm.AUCTION_FEE * 36 / 60, price=fpv, ytm=None, mv=fpv * 1.5e6,
                     gl_cv=150e6 - fpv * 1.5e6, gl_face=150e6 - fpv * 1.5e6, dur=1 / 12, dv01=150e6 / 12 / 1e4 * fpv / 100,
                     holders=dict(households=.10, bank=.80, world=.10, cb=0), tranches=[], first_call=None, callable=False,
                     index=NOW.policy))

    # the linked bond: real coupon set in the calm month; principal indexed by the inflation the city had
    cvi = dm.Curve(city_at(0), extra_debt=100e6)
    rc = dm.ilb_real_coupon(cvi, 120)
    idx = 1.0
    for k in range(24):
        idx *= 1 + city_at(k).inflation / 12
    real_curve_now = curve
    # remaining flows in nominal terms at the inflation now expected
    exp_idx = np.cumprod([1 + NOW.inflation_path(t) / 12 for t in range(96)])
    flows = np.full(96, rc / 12 * 100) * exp_idx * idx
    flows[-1] += 100 * exp_idx[-1] * idx
    ipv = curve.pv(flows)
    face_idx = 100e6 * idx
    rows.append(dict(name="10-year inflation-linked", kind="ILB", coupon=rc, issued=cal(CAL0), due=cal(CAL0 + 120),
                     left=96, face=face_idx, face_original=100e6, index=idx, eir=None, cv=face_idx * .9995,
                     price=ipv / idx, ytm=None, mv=ipv * 1e6, gl_cv=face_idx * .9995 - ipv * 1e6,
                     gl_face=face_idx - ipv * 1e6, dur=7.6, dv01=ipv * 1e6 * 7.6 / 1e4,
                     holders=dict(households=.60, bank=.30, world=.10, cb=0), tranches=[], first_call=None,
                     callable=False, yield_text=f"{ytm(dm.bullet_flows(rc, 96), ipv / idx) * 100:.2f}% real"))

    rows.sort(key=lambda r: (r["due"][-4:], MONTHS.index(r["due"][:3])))

    # a line's history: the 3 1/2% 2211's price and yield month by month
    first10 = issue_bullet(0, 10, 300e6)
    hist = []
    for k in range(0, 25):
        cvk = dm.Curve(city_at(k))
        fl = dm.bullet_flows(first10["coupon"], 120 - k)
        pr = cvk.pv(fl)
        hist.append(dict(month=cal(CAL0 + k), price=pr, ytm=ytm(fl, pr),
                         cv=carrying(100, first10["coupon"], first10["eir"], first10["proceeds"] / 3e6, k)))

    # the curve now, a year ago and two years ago; the expected dial
    curves = {}
    for label, c in [("now", NOW), ("a year ago", city_at(12)), ("two years ago", THEN)]:
        cv = dm.Curve(c)
        curves[label] = dict(zero=[float(cv.zero[m]) for m in range(1, 601)],
                             path=[float(c.dial_path(t)) for t in range(0, 601)])
    # the real curve (from the linked bonds) and the inflation expected
    infl = [float(NOW.inflation_path(t)) for t in range(600)]
    real = []
    for m in range(1, 601):
        real.append(float(curve.zero[m] - np.mean(infl[:m])))
    world = [dm.WORLD_BASE_RATE + .006 + dm.term_premium(m) for m in range(1, 601)]

    # the 10-year's rate taken apart, now
    m10 = 120
    path_avg = float(np.mean(curve.short[:m10]))
    parts = dict(path=path_avg, credit=curve.credit_spread, premium=dm.term_premium(m10), qe=-NOW.compression(m10),
                 total=float(curve.zero[m10]))

    # the auction: the 10-year line reopened today, D$300M
    # the 10-year line now is the 6% Jul 2212 (114 months left): a reopening, at its own coupon
    auc = dm.auction(NOW, "bullet", 10, 3e8, line_coupon=.06, months=114)
    grid = []
    for y in np.arange(auc["fair"] - .002, auc["fair"] + .0125, .0005):
        d = {b.who: b.face for b in dm.demand(NOW, float(y), 3e8, auc["fair"], 114)}
        d["households"] = min(d["households"], 3e8) if y >= auc["fair"] else 0
        if y < auc["fair"]:
            d = {k: 0.0 for k in d}
        grid.append(dict(y=float(y), **{k: float(min(v, 3e8 * 1.2)) for k, v in d.items()}))
    # with the bank's room nearly gone: the same D$300M (the households, the firms and the world cover it, D32
    # and D33), and twice it, D$600M, which is short at the cap and filled at a higher yield - the red card
    tight = dm.auction(replace(NOW, bank_room=2e7), "bullet", 10, 3e8, line_coupon=.06, months=114)
    short = dm.auction(replace(NOW, bank_room=2e7), "bullet", 10, 6e8, line_coupon=.06, months=114)
    filled = dm.auction(replace(NOW, bank_room=2e7), "bullet", 10, 6e8, max_tail=None, line_coupon=.06, months=114)
    # the terms' columns on Borrow: today's fair yield at each
    terms = {n: float(curve.par_yield(m)) for n, m in [("2y", 24), ("5y", 60), ("10y", 120), ("20y", 240), ("30y", 360), ("50y", 600)]}
    bills = {n: float(curve.zero[m]) for n, m in [("1m", 1), ("3m", 3), ("6m", 6), ("12m", 12)]}
    amort = {f"{y}y": float(dm.amortizing_coupon(curve, y)) for y in (5, 10, 20, 30)}
    frn = {n: float(dm.frn_margin(curve, m)) for n, m in [("2y", 24), ("5y", 60)]}
    ilb = {n: float(dm.ilb_real_coupon(curve, m)) for n, m in [("10y", 120), ("30y", 360)]}
    call = {}
    for y in (10, 30):
        cc, plain, _ = dm.callable_coupon(curve, y, dm.RATE_VOL_FLOOR)
        call[f"{y}y"] = float(cc - plain)

    # the reverse auction on the 3 1/2% 2211, D$100M, today
    line = [r for r in rows if r["name"] == "10-year bond" and r["due"] == "Jan 2211"][0]
    flows100 = dm.bullet_flows(line["coupon"], 96)
    hold = {"households": 300e6 * .30, "the bank": 300e6 * .55, "the world": 300e6 * .10, "the central bank": 300e6 * .05}
    fair, book = dm.offers(NOW, curve, flows100, hold, 300e6)
    ra = dm.reverse_auction(NOW, curve, flows100, hold, 300e6, 1e8)
    ladder = [dict(price=p, face=q, who=w) for p, q, w in book]
    cv_ret = line["cv"] * ra["retired"] / line["face"]
    # replacing the cash on today's 10-year line
    y10 = curve.par_yield(120)
    close = dict(fair=fair, ra=ra, ladder=ladder, cv_retired=cv_ret, gain_cv=cv_ret - ra["paid"],
                 gain_face=ra["retired"] - ra["paid"], premium=ra["paid"] - fair / 100 * ra["retired"],
                 coupon_saved=ra["retired"] * line["coupon"], replace_cost=ra["paid"] * y10, y10=y10)

    return dict(rows=rows, hist=hist, curves=curves, real=real, infl=infl, world=world, parts=parts,
                policy=NOW.policy, auction=auc, auction_grid=grid, tight=tight, short=short, filled=filled, close=close,
                terms=terms, bills=bills, amort=amort, frn=frn, ilb=ilb, call=call,
                today="Jan 2203", rule_now=NOW.rule(NOW.inflation_path(0)),
                dial_1y=NOW.dial_path(12), dial_5y=NOW.dial_path(60), neutral=dm.NEUTRAL_RATE,
                expected_inflation=NOW.long_run_inflation(), credibility=NOW.credibility)


if __name__ == "__main__":
    data = build()
    with open(os.path.join(HERE, "boards-data.json"), "w") as f:
        json.dump(data, f, indent=1, default=float)
    # the boards: the page with these figures in it (boards.src.html -> city-debt-study.html)
    src = open(os.path.join(HERE, "boards.src.html")).read()
    with open(os.path.join(HERE, "city-debt-study.html"), "w") as f:
        f.write(src.replace("/*DATA*/null", json.dumps(data, default=float, separators=(",", ":"))))
    for r in data["rows"]:
        print(f"{r['name']:26} {r['due']:9} face {r['face'] / 1e6:7.1f}M  cpn {r['coupon'] * 100:6.3f}  "
              f"px {r['price']:8.3f}  ytm {('-' if r['ytm'] is None else f'{r[chr(121)+chr(116)+chr(109)] * 100:.2f}'):>6}  "
              f"eir {('-' if r['eir'] is None else f'{r[chr(101)+chr(105)+chr(114)] * 100:.2f}'):>6}  "
              f"cv {r['cv'] / 1e6:7.1f}  mv {r['mv'] / 1e6:7.1f}  g/l cv {r['gl_cv'] / 1e6:6.1f}  dur {r['dur']:.2f}")
    a = data["auction"]
    print("auction", {k: (round(v, 4) if isinstance(v, float) else v) for k, v in a.items() if k != "fills"}, a["fills"])
    print("tight", data["tight"]["stop"], data["tight"]["cover"], data["tight"]["sold"])
    print("short", data["short"]["stop"], data["short"]["cover"], data["short"]["sold"])
    f = data["filled"]
    print("filled", f["stop"], f["tail"], f["sold"], f["price"], f["proceeds"], f["eir"], f["fills"])
    c = data["close"]
    print("close", c["ra"], c["gain_cv"], c["gain_face"], c["premium"], c["coupon_saved"], c["replace_cost"], c["y10"])
    print("parts", data["parts"], "rule", data["rule_now"], data["dial_1y"], data["dial_5y"])
