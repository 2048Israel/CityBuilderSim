"""
The city's debt, priced as a treasury desk would price it - the study's model.

docs/studies/spec-city-debt.md is the note; this is the arithmetic behind
every figure in it and on the boards. Run it with no arguments:

    python3 debt-model.py            # prints every table the study quotes
    python3 debt-model.py --json     # ...and writes figures.json beside it

It is a design aid, not game code (docs/studies/README.md). Nothing in the
game reads it. Every constant below that the game already has is copied from
the code with its name; every constant the study proposes is marked PROPOSED
and is one of Jerus's numbers to settle (the study's section 9).

Conventions, all of them the game's:
  - a month is the step; rates are nominal a year, compounded monthly;
  - coupons are paid every month (Jerus, 2026-10-12: "monthly, i know
    unrealistic but makes the game alot more coherent"), so there is no
    accrued interest and one price - the clean price IS the dirty price;
  - prices are per 100 of face.
"""

import json
import math
import sys
from dataclasses import dataclass, field, replace

import numpy as np

# --------------------------------------------------------------------------
# constants the game already has (copied, with their names)
# --------------------------------------------------------------------------
NEUTRAL_RATE = .03                    # DebtManager.NEUTRAL_RATE
DEFAULT_INFLATION_TARGET = .02        # DebtManager.DEFAULT_INFLATION_TARGET
TAYLOR_WEIGHT = 1.5                   # DebtManager.TAYLOR_WEIGHT (Standard)
TERM_PREMIUM = {120: .0050, 240: .0090, 360: .0115, 480: .0135, 600: .0150}
MAX_SPREAD_PER_MEASURE = .05          # DebtManager
FULL_STRESS_MULTIPLE = 150            # DebtManager (years of the thing)
MIN_RATE = .005                       # DebtManager
WORLD_BASE_RATE = .02                 # DebtManager
FULL_COMPRESSION_SHARE = .5           # CentralBank
QE_COMPRESSION = 1.0                  # CentralBank
WINDOW_PENALTY = .0025                # CentralBank
ADAPT_MONTHS = 12                     # Expectations
HOUSEHOLD_PAPER_APPETITE = 20         # HouseholdBalance
MAX_HOUSEHOLD_PAPER_SHARE = .5        # HouseholdBalance
CF_APPETITE = 3.0                     # CapitalFlows.APPETITE
CF_MAX_SPREAD = .25                   # CapitalFlows.MAX_SPREAD
CF_ARRIVAL_SPEED = .08                # CapitalFlows.ARRIVAL_SPEED
RISK_CITY = .20                       # Bank.RISK_CITY
MIN_MARGIN = .01                      # Bank.MIN_MARGIN
DESK_SPREAD = .02                     # Exchange.SPREAD: the desk's ask over its bid, a share of fair value

# --------------------------------------------------------------------------
# constants the study PROPOSES (Jerus's numbers to settle, section 9)
# --------------------------------------------------------------------------
DRIFT_MONTHS = 6          # PROPOSED: the dial is expected to close its gap to the rule with this time constant
COUPON_STEP = .00125      # PROPOSED: a new line's coupon is the clearing yield rounded DOWN to 1/8 of a point
MAX_TAIL = .01            # PROPOSED: the default highest yield the city takes over fair at its own auction
RATE_VOL_FLOOR = .0075    # PROPOSED: the least short-rate volatility a call is priced at, a year (normal)
NO_CALL_YEARS = {10: 5, 20: 10, 30: 10, 50: 10}   # PROPOSED: when a callable line may first be called
AUCTION_FEE = 12_000      # PROPOSED: the fixed cost of a sale (Game's fixed fee); the 0.75% spread goes


def term_premium(months):
    """DebtManager.termPremium(), line for line."""
    if months <= 12:
        return 0.0
    if months <= 120:
        return TERM_PREMIUM[120] * (months - 12) / (120 - 12)
    if months >= 600:
        return TERM_PREMIUM[600]
    keys = sorted(TERM_PREMIUM)
    below = months // 120 - 1
    at = (months - (below + 1) * 120) / 120
    return TERM_PREMIUM[keys[below]] + (TERM_PREMIUM[keys[below + 1]] - TERM_PREMIUM[keys[below]]) * at


def spread_for(debt, annual_capacity):
    """DebtManager.spreadFor(): a linear ramp to MAX_SPREAD_PER_MEASURE at FULL_STRESS_MULTIPLE years."""
    if annual_capacity <= 0:
        return MAX_SPREAD_PER_MEASURE
    return MAX_SPREAD_PER_MEASURE * min(1.0, debt / (annual_capacity * FULL_STRESS_MULTIPLE))


# --------------------------------------------------------------------------
# the city, as the market reads it
# --------------------------------------------------------------------------
@dataclass
class City:
    name: str
    policy: float                 # the dial
    target: float = DEFAULT_INFLATION_TARGET
    inflation: float = .02        # Expectations.smoothed - what is happening now
    credibility: float = .80      # Expectations.credibility (KSEED)
    cost_of_funds: float = .015   # what the bank's money costs it
    deposit_rate: float = .015    # what the bank pays savers
    cb_share: float = 0.0         # the central bank's share of the term paper
    gdp: float = 5.4e9            # a year of output
    revenue: float = 1.1e9        # a year of tax
    debt: float = 3.4e9           # all the city owes (priced debt)
    hh_spare: float = 2.0e9       # households' savings past the cushion
    bank_room: float = 1.0e9      # what the bank may still hold, risk-weighted
    world_stock: float = 1.5e8    # hot money in the city now
    depreciation: float = 0.0     # what the world expects the currency to lose, a year

    # ---- expectations -------------------------------------------------------
    def long_run_inflation(self):
        """Expectations' rule for what is expected: the target weighted by credibility, the rest recent experience."""
        return self.credibility * self.target + (1 - self.credibility) * self.inflation

    def inflation_path(self, t):
        """What the market expects inflation to be t months out: from now to the long-run expectation over ADAPT_MONTHS."""
        lr = self.long_run_inflation()
        return lr + (self.inflation - lr) * math.exp(-t / ADAPT_MONTHS)

    def rule(self, inflation):
        """DebtManager.ruleRate() at Standard, held inside the dial's bounds."""
        r = NEUTRAL_RATE + (self.target - DEFAULT_INFLATION_TARGET) + TAYLOR_WEIGHT * (inflation - self.target)
        return min(1.0, max(0.0, r))

    def dial_path(self, t):
        """THE EXPECTED PATH (section 3.1): the rule at the inflation expected then, plus today's gap to it, closing over DRIFT_MONTHS."""
        gap = self.floor() - self.rule(self.inflation_path(0))
        return self.rule(self.inflation_path(t)) + gap * math.exp(-t / DRIFT_MONTHS)

    # ---- credit -------------------------------------------------------------
    def credit(self, extra=0.0):
        """The credit spread, the code's two measures, with an issue priced in."""
        d = self.debt + extra
        return spread_for(d, self.gdp) + spread_for(d, self.revenue)

    def bank_floor(self):
        """DebtManager.bankFloorRate(): the policy rate, or the bank's cost of funds plus its margin."""
        return max(self.policy, self.cost_of_funds + MIN_MARGIN)

    def floor(self):
        """DebtManager.floorRate(): the policy rate, or the bank's cost of funds if that is higher - where the curve starts."""
        return max(self.policy, self.cost_of_funds)

    def funding_strain(self, months):
        """
        What the bank needs over fair for paper of this many months: the part
        of its floor the curve does not already carry (its cost of funds plus
        its margin past the policy rate), expected to fade as the dial's gap
        does. Nothing when the bank's money costs it less than the policy
        rate, which is the usual case: then it bids at fair.
        """
        extra = max(0.0, self.bank_floor() - self.floor())
        if extra <= 0:
            return 0.0
        decay = np.mean([math.exp(-t / DRIFT_MONTHS) for t in range(months)])
        return extra * float(decay)

    def compression(self, months):
        """DebtManager.compression(): QE takes the term premium off in proportion to the share held."""
        return term_premium(months) * min(1, self.cb_share / FULL_COMPRESSION_SHARE) * QE_COMPRESSION


# --------------------------------------------------------------------------
# the curve
# --------------------------------------------------------------------------
class Curve:
    """
    THE CURVE (section 3): a discount factor for every month to 600.

    The short rate expected in month t is the dial's expected path, plus the
    credit spread; a zero rate at m months compounds those, then carries the
    term premium for m months less what QE compresses. Every price is a sum
    of payments times these discount factors - no bond is discounted at a
    single flat rate any more.
    """

    def __init__(self, city: City, extra_debt=0.0, horizon=601, bump=0.0):
        self.city = city
        cr = city.credit(extra_debt)
        self.short = np.array([city.dial_path(t) for t in range(horizon)])
        self.forward = np.maximum(MIN_RATE, self.short + cr)
        df0 = np.ones(horizon)
        for m in range(1, horizon):
            df0[m] = df0[m - 1] / (1 + self.forward[m - 1] / 12)
        self.zero = np.zeros(horizon)
        self.df = np.ones(horizon)
        for m in range(1, horizon):
            z0 = (df0[m] ** (-1 / m) - 1) * 12
            z = z0 + term_premium(m) - city.compression(m) + bump
            self.zero[m] = z
            self.df[m] = (1 + z / 12) ** (-m)
        self.zero[0] = self.forward[0] + bump
        self.credit_spread = cr

    def pv(self, flows):
        """Flows paid at months 1..n."""
        n = len(flows)
        return float(np.dot(flows, self.df[1:n + 1]))

    def par_yield(self, months):
        """The coupon a bullet of this many months needs to price at 100 on the curve."""
        annuity = self.df[1:months + 1].sum() / 12
        return (1 - self.df[months]) / annuity

    def inflation_avg(self, months):
        c = self.city
        return float(np.mean([c.inflation_path(t) for t in range(months)]))


# --------------------------------------------------------------------------
# the instruments, as schedules
# --------------------------------------------------------------------------
def bullet_flows(coupon, months, face=100.0):
    f = np.full(months, face * coupon / 12)
    f[-1] += face
    return f


def amortizing_flows(coupon, years, face=100.0):
    """Equal annual slices of principal, the coupon monthly on what is left (MediumTermBond's shape)."""
    months = years * 12
    f = np.zeros(months)
    left = face
    slice_ = face / years
    for m in range(months):
        f[m] = left * coupon / 12
        if (m + 1) % 12 == 0:
            f[m] += slice_
            left -= slice_
    return f


def flat_price(flows, y):
    """Price of a schedule at one flat yield (the auction's convention, and a YTM's)."""
    t = np.arange(1, len(flows) + 1)
    return float(np.sum(flows / (1 + y / 12) ** t))


def ytm(flows, price):
    """Debt.getYieldToMaturity(): bisection on the flat yield."""
    lo, hi = -0.05, 1.0
    for _ in range(100):
        mid = (lo + hi) / 2
        if flat_price(flows, mid) > price:
            lo = mid
        else:
            hi = mid
    return (lo + hi) / 2


def mod_duration(flows, y):
    t = np.arange(1, len(flows) + 1) / 12
    d = flows / (1 + y / 12) ** (t * 12)
    mac = float(np.sum(t * d) / np.sum(d))
    return mac / (1 + y / 12)


def amortizing_coupon(curve, years):
    """The ONE coupon that prices the amortizing schedule at 100 on the curve (section 2.3): no more 'the last slice's rate on all of it'."""
    lo, hi = 0.0, .5
    for _ in range(80):
        mid = (lo + hi) / 2
        if curve.pv(amortizing_flows(mid, years)) < 100:
            lo = mid
        else:
            hi = mid
    return (lo + hi) / 2


def frn_margin(curve, months):
    """The fixed margin over the policy rate that prices a floating-rate note at 100 (section 2.4)."""
    p = curve.short[:months]
    df = curve.df[1:months + 1]
    return (1 - curve.df[months] - float(np.dot(p / 12, df))) / (df.sum() / 12)


def ilb_real_coupon(curve, months):
    """
    The real coupon of an inflation-linked bond priced at 100 (section 2.5):
    the cash flows are real coupons on an indexed principal, so in nominal
    terms they grow with the inflation the market expects; the coupon that
    prices them at 100 is the real par yield. Breakeven = the nominal par
    yield less it.
    """
    c = curve.city
    idx = np.cumprod([1 + c.inflation_path(t) / 12 for t in range(months)])
    df = curve.df[1:months + 1]
    # 100 = sum(r/12 * 100 * idx_t * df_t) + 100 * idx_N * df_N
    return (1 - idx[-1] * df[-1]) / float(np.dot(idx / 12, df))


# --------------------------------------------------------------------------
# a callable bond, on a short-rate tree (section 2.6)
# --------------------------------------------------------------------------
def ho_lee(curve, months, sigma):
    """
    Ho and Lee's tree, monthly, fitted to the curve's discount factors: the
    rate at node (i, j) is theta_i + sigma*sqrt(dt)*(2j - i). Forward
    induction on Arrow-Debreu prices fits each theta to the next discount
    factor exactly, so a bond with no option prices on the tree as it does
    on the curve - the check that the tree is the curve.
    """
    dt = 1 / 12
    step = sigma * math.sqrt(dt)
    rates = []
    q = np.array([1.0])
    for i in range(months):
        j = np.arange(i + 1)
        shape = step * (2 * j - i)
        target = curve.df[i + 1]
        lo, hi = -0.5, 1.5
        for _ in range(80):
            th = (lo + hi) / 2
            val = float(np.sum(q / (1 + (th + shape) / 12)))
            if val > target:
                lo = th
            else:
                hi = th
        r = th + shape
        rates.append(r)
        disc = q / (1 + r / 12)
        nq = np.zeros(i + 2)
        nq[:-1] += .5 * disc
        nq[1:] += .5 * disc
        q = nq
    return rates


def callable_price(rates, coupon, months, first_call, call_price=100.0, callable_=True):
    """Backward through the tree: at every node from first_call on, the city calls when the bond is worth more than the call price."""
    c = 100 * coupon / 12
    v = np.full(months + 1, 100.0 + c)          # at maturity, after the last coupon is added below
    v = np.full(months + 1, 100.0)
    for i in range(months - 1, -1, -1):
        r = rates[i]
        cont = (.5 * v[:-1] + .5 * v[1:] + c) / (1 + r / 12)
        if callable_ and i >= first_call:
            cont = np.minimum(cont, call_price)
        v = cont
    return float(v[0])


def callable_coupon(curve, years, sigma):
    months = years * 12
    rates = ho_lee(curve, months, sigma)
    first = NO_CALL_YEARS[years] * 12
    lo, hi = 0.0, .5
    for _ in range(60):
        mid = (lo + hi) / 2
        if callable_price(rates, mid, months, first) < 100:
            lo = mid
        else:
            hi = mid
    plain = curve.par_yield(months)
    check = callable_price(rates, plain, months, first, callable_=False)
    return (lo + hi) / 2, plain, check


# --------------------------------------------------------------------------
# the auction (section 4)
# --------------------------------------------------------------------------
@dataclass
class Bid:
    who: str
    face: float


def demand(city: City, y, offered, fair=None, months=120):
    """
    What each bidder takes at a yield y, by its own rule in the game:
      households - HouseholdBalance's rule, min(1/2, 20 x spread over the deposit rate) of the issue, out of savings past the cushion;
      the world  - CapitalFlows' rule, its target stock at this excess return less what is here, at ARRIVAL_SPEED;
      the bank   - the rest of its room, at fair plus what its funding needs over the curve (City.funding_strain()).
    """
    hh = min(MAX_HOUSEHOLD_PAPER_SHARE, HOUSEHOLD_PAPER_APPETITE * max(0.0, y - city.deposit_rate)) * offered
    hh = min(hh, city.hh_spare)
    excess = min(CF_MAX_SPREAD, max(0.0, y - WORLD_BASE_RATE - city.depreciation))
    world = max(0.0, CF_APPETITE * excess * city.gdp - city.world_stock) * CF_ARRIVAL_SPEED
    need = (fair if fair is not None else 0.0) + city.funding_strain(months)
    bank = city.bank_room / RISK_CITY if y >= need - 1e-12 else 0.0
    return [Bid("households", hh), Bid("the world", world), Bid("the bank", bank)]


def auction(city: City, kind, years, offered, max_tail=MAX_TAIL, line_coupon=None, months=None):
    """
    Sell `offered` of face. Fair is the curve's par yield for the paper with
    the issue on the books. The households bid NON-COMPETITIVELY, as retail
    does at a real treasury auction: what their rule takes at fair, filled
    first and in full at whatever yield the auction stops at - which is
    "the households first" the game has now. Everybody else bids
    competitively, at fair or over it (nobody bids under what the paper
    trades at - it could buy it there). The stop-out is the lowest yield at
    which the bids cover the issue, never under fair and never over fair +
    max_tail; everyone is filled at it (single price), the competitive bids
    at the stop pro rata.
    """
    months = months or years * 12
    curve = Curve(city, extra_debt=offered)
    fair = curve.par_yield(months) if kind != "amortizing" else amortizing_coupon(curve, years)
    first = {b.who: b.face for b in demand(city, fair, offered, fair, months)}
    retail = min(first["households"], offered)

    def competitive(y):
        return [b for b in demand(city, y, offered, fair, months) if b.who != "households"]

    stop = None
    for y in np.arange(fair, fair + max_tail + 1e-9, .00005):
        if retail + sum(b.face for b in competitive(y)) >= offered:
            stop = float(y)
            break
    top = competitive(fair + max_tail)
    # the cover counts what was bid up to the highest yield taken, each bidder at most the whole issue
    cover = (retail + sum(min(b.face, offered) for b in top)) / offered
    if stop is None:
        stop = fair + max_tail
        sold = retail + sum(b.face for b in top)
    else:
        sold = offered
    comp = competitive(stop)
    total = sum(b.face for b in comp)
    fills = {"households": retail}
    for b in comp:
        fills[b.who] = (b.face / total * (sold - retail)) if total > 0 else 0.0
    coupon = line_coupon if line_coupon is not None else math.floor(stop / COUPON_STEP) * COUPON_STEP
    flows = bullet_flows(coupon, months) if kind != "amortizing" else amortizing_flows(coupon, years)
    price = flat_price(flows, stop)
    proceeds = price / 100 * sold - AUCTION_FEE
    net_flows = flows / 100 * sold
    eir = ytm(net_flows, proceeds)
    return dict(kind=kind, years=years, offered=offered, sold=sold, fair=fair, stop=stop, tail=stop - fair,
                cover=cover, coupon=coupon, price=price, proceeds=proceeds, eir=eir, fills=fills,
                credit=curve.credit_spread)


# --------------------------------------------------------------------------
# effective interest (section 5)
# --------------------------------------------------------------------------
def amortized_cost(face, coupon, months, proceeds, eir):
    """The carrying value month by month: last month's, plus a month of it at the effective rate, less the coupon paid."""
    cv = proceeds
    rows = []
    for m in range(1, months + 1):
        expense = cv * eir / 12
        paid = face * coupon / 12
        cv = cv + expense - paid
        if m == months:
            cv -= face
        rows.append((m, expense, paid, expense - paid, cv))
    return rows


# --------------------------------------------------------------------------
# the reverse auction (section 6)
# --------------------------------------------------------------------------
def offers(city: City, curve: Curve, flows_per_100, holdings, line_face):
    """
    Every holder offers what it holds of the line in slices, each at the
    price where ITS OWN BUYING RULE would stop wanting that slice, and never
    under fair (it could sell on the desk at fair):
      households - required spread over the deposit rate = the share of the line they would still hold / 20;
      the world  - required excess over the world's rate = the stock it would still hold / (APPETITE x output);
      the bank   - the desk's ask: fair plus half Exchange.SPREAD, as it quotes a share (it bought at fair, so it is
                   indifferent at fair, and is paid its spread for the trouble).
    The central bank does not offer: its holdings follow its own dial (section 6.3).
    """
    fair = curve.pv(flows_per_100)
    out = []
    slices = 20
    for who, held in holdings.items():
        if held <= 0 or who == "the central bank":
            continue
        for k in range(slices):
            q = held / slices
            left = held - (k + .5) * q          # what it would still hold after this slice
            if who == "households":
                req = city.deposit_rate + (left / line_face) / HOUSEHOLD_PAPER_APPETITE
            elif who == "the world":
                req = WORLD_BASE_RATE + city.depreciation + max(0.0, city.world_stock - (held - left)) / (CF_APPETITE * city.gdp)
            else:
                req = None
            p = fair * (1 + DESK_SPREAD / 2) if req is None else max(fair, flat_price(flows_per_100, req))
            out.append((p, q, who))
    out.sort()
    return fair, out


def reverse_auction(city, curve, flows_per_100, holdings, line_face, wanted, max_price=None):
    fair, book = offers(city, curve, flows_per_100, holdings, line_face)
    got, paid, taken = 0.0, 0.0, {}
    stop = fair
    offered = sum(q for _, q, _ in book)
    for p, q, who in book:
        if got >= wanted - 1e-6:
            break
        if max_price is not None and p > max_price:
            break
        take = min(q, wanted - got)
        got += take
        paid += take * p / 100
        taken[who] = taken.get(who, 0.0) + take
        stop = p
    return dict(fair=fair, retired=got, paid=paid, avg=paid / got * 100 if got else 0, stop=stop,
                cover=offered / wanted, taken=taken)


# --------------------------------------------------------------------------
# the scenarios the study and the boards quote
# --------------------------------------------------------------------------
CALM = City("calm: on target, the dial at neutral", policy=.03, inflation=.02)
HIKING = City("hiking: 6% inflation, the dial at 7%", policy=.07, inflation=.06, credibility=.65,
              cost_of_funds=.045, deposit_rate=.045)
BEHIND = City("behind: 5% inflation, the dial held at 1% by hand", policy=.01, inflation=.05, credibility=.6,
              cost_of_funds=.008, deposit_rate=.006)
QE = replace(CALM, name="calm, the central bank holding a quarter of the term paper", cb_share=.25)
SCENARIOS = [CALM, HIKING, BEHIND, QE]
TENORS = [1, 3, 6, 12, 24, 60, 120, 240, 360, 600]
NAMES = ["1m", "3m", "6m", "1y", "2y", "5y", "10y", "20y", "30y", "50y"]


def pct(x, d=2):
    return f"{x * 100:.{d}f}%"


def money(x):
    a = abs(x)
    s = "-" if x < 0 else ""
    if a >= 1e9:
        return f"{s}D${a / 1e9:.2f}B"
    if a >= 1e6:
        return f"{s}D${a / 1e6:.1f}M"
    if a >= 1e3:
        return f"{s}D${a / 1e3:.0f}k"
    return f"{s}D${a:.0f}"


def main():
    out = {}
    print("=" * 78)
    print("1. THE CURVE in four cities - zero rates by maturity")
    print("=" * 78)
    print(f"{'':52}" + " ".join(f"{n:>6}" for n in NAMES))
    out["curves"] = {}
    for c in SCENARIOS:
        cv = Curve(c)
        row = [cv.zero[m] for m in TENORS]
        print(f"{c.name[:52]:52}" + " ".join(f"{z * 100:6.2f}" for z in row))
        out["curves"][c.name] = dict(zero=[float(cv.zero[m]) for m in range(1, 601)],
                                     path=[float(cv.short[m]) for m in range(0, 601)],
                                     tenors=TENORS, names=NAMES, at=row)
    print("\n   today's code, the same four: the floor + credit + the premium table, parallel")
    for c in SCENARIOS:
        floor = max(c.policy, c.cost_of_funds)
        row = [floor + c.credit() + (0 if m <= 12 else term_premium(m) - c.compression(m)) for m in TENORS]
        print(f"   {c.name[:49]:49}" + " ".join(f"{z * 100:6.2f}" for z in row))

    print("\n" + "=" * 78)
    print("2. WHAT EACH INSTRUMENT COSTS at issue, calm city (fair, before any auction)")
    print("=" * 78)
    cv = Curve(CALM)
    inst = []
    for m, n in [(1, "1-month bill"), (3, "3-month bill"), (6, "6-month bill"), (12, "12-month bill")]:
        price = 100 * cv.df[m]
        disc = (100 - price) / 100 * 12 / m
        inst.append((n, f"price {price:.3f}", f"discount {pct(disc, 3)}", f"yield {pct(cv.zero[m], 3)}"))
    for y in [2, 5, 10, 20, 30, 50]:
        py = cv.par_yield(y * 12)
        c = math.floor(py / COUPON_STEP) * COUPON_STEP
        p = cv.pv(bullet_flows(c, y * 12))
        inst.append((f"{y}-year bond", f"coupon {pct(c, 3)}", f"price {p:.3f}", f"yield {pct(py, 3)}"))
    for y in [5, 10, 20, 30]:
        ac = amortizing_coupon(cv, y)
        old = cv.par_yield(y * 12)   # the code's rule: the coupon of the last slice
        inst.append((f"{y}-year amortizing", f"coupon {pct(ac, 3)}", f"(today: {pct(old, 3)})",
                     f"overcharge {(old - ac) * 1e4:.0f} bp"))
    for y in [2, 5]:
        mg = frn_margin(cv, y * 12)
        inst.append((f"{y}-year floating", f"policy + {mg * 1e4:.0f} bp", "price 100", f"index {pct(CALM.policy)}"))
    for y in [10, 30]:
        rc = ilb_real_coupon(cv, y * 12)
        be = cv.inflation_avg(y * 12)
        inst.append((f"{y}-year inflation-linked", f"real coupon {pct(rc, 3)}", f"expects {pct(be, 2)} a year", "price 100"))
    for row in inst:
        print("   " + "  ".join(f"{x:<24}" for x in row))
    out["instruments"] = inst

    print("\n   inflation-linked against nominal when the bank is NOT believed (behind):")
    cvb = Curve(BEHIND)
    for y in [10, 30]:
        rc = ilb_real_coupon(cvb, y * 12)
        py = cvb.par_yield(y * 12)
        print(f"   {y}y: nominal {pct(py)}, real {pct(rc)}, inflation expected {pct(cvb.inflation_avg(y * 12))} - "
              f"against the {pct(BEHIND.target)} target the city would deliver")

    print("\n" + "=" * 78)
    print("3. A CALLABLE BOND costs its option in the coupon (Ho-Lee, monthly)")
    print("=" * 78)
    out["callable"] = []
    for sigma in [.0075, .01]:
        for y in [10, 30]:
            cc, plain, check = callable_coupon(cv, y, sigma)
            print(f"   {y}y callable after {NO_CALL_YEARS[y]}y at par, vol {pct(sigma)}: coupon {pct(cc, 3)} vs "
                  f"{pct(plain, 3)} non-call (+{(cc - plain) * 1e4:.0f} bp); tree prices the plain bond at {check:.3f}")
            out["callable"].append(dict(years=y, sigma=sigma, callable=cc, plain=plain))

    print("\n" + "=" * 78)
    print("4. THE AUCTION - a 10-year line, D$300M, in three cities")
    print("=" * 78)
    out["auctions"] = {}
    for c in [CALM, HIKING, replace(CALM, name="calm, the bank's room nearly gone", bank_room=2e7)]:
        a = auction(c, "bullet", 10, 3e8)
        print(f"   {c.name}")
        print(f"      fair {pct(a['fair'], 3)}  stop-out {pct(a['stop'], 3)}  tail {a['tail'] * 1e4:.1f} bp  "
              f"cover {a['cover']:.2f}x  sold {money(a['sold'])}")
        print(f"      coupon {pct(a['coupon'], 3)}  price {a['price']:.3f}  proceeds {money(a['proceeds'])}  "
              f"effective rate {pct(a['eir'], 3)}")
        print("      filled: " + ", ".join(f"{k} {money(v)}" for k, v in a["fills"].items()))
        out["auctions"][c.name] = a

    print("\n" + "=" * 78)
    print("5. ONE LINE'S LIFE - the 10-year sold in the calm city, then the bank hikes")
    print("=" * 78)
    a = auction(CALM, "bullet", 10, 3e8)
    face, coupon, n = a["sold"], a["coupon"], 120
    sched = amortized_cost(face, coupon, n, a["proceeds"], a["eir"])
    print(f"   sold {money(face)} of {pct(coupon, 3)} 10-year at {a['price']:.3f}; raised {money(a['proceeds'])}; "
          f"effective {pct(a['eir'], 3)}")
    print(f"   {'month':>6} {'expense':>12} {'coupon':>12} {'amortised':>12} {'carrying':>14}")
    for m, e, p, am, cvv in [sched[i] for i in (0, 1, 11, 23, 59, 118)]:
        print(f"   {m:>6} {money(e):>12} {money(p):>12} {money(am):>12} {money(cvv):>14}")
    t = 24
    left = n - t
    carrying = sched[t - 1][4]
    flows100 = bullet_flows(coupon, left)
    out["life"] = dict(face=face, coupon=coupon, eir=a["eir"], proceeds=a["proceeds"], price=a["price"],
                       schedule=[dict(m=m, expense=e, paid=p, amort=am, cv=cvv) for m, e, p, am, cvv in sched])
    out["close"] = {}
    for c in [CALM, HIKING]:
        cvc = Curve(c)
        fair = cvc.pv(flows100)
        y_now = ytm(flows100, fair)
        holdings = {"households": face * .30, "the bank": face * .55, "the world": face * .10,
                    "the central bank": face * .05}
        ra = reverse_auction(c, cvc, flows100, holdings, face, wanted=1e8)
        cv_retired = carrying * ra["retired"] / face
        print(f"\n   two years on, in the {c.name.split(':')[0]} city: fair {fair:.3f} (yield {pct(y_now, 3)}); "
              f"carrying value {carrying / face * 100:.3f} per 100")
        print(f"      market value of the line {money(fair / 100 * face)}  against carrying {money(carrying)}  "
              f"and face {money(face)}")
        print(f"      unrealised: {money(carrying - fair / 100 * face)} against carrying, "
              f"{money(face - fair / 100 * face)} against face")
        print(f"      REVERSE AUCTION for D$100M: offered {ra['cover']:.2f}x, stop {ra['stop']:.3f}, "
              f"average {ra['avg']:.3f}, paid {money(ra['paid'])}")
        print("         taken: " + ", ".join(f"{k} {money(v)}" for k, v in ra["taken"].items()))
        print(f"         gain (loss) against carrying {money(cv_retired - ra['paid'])}; against face "
              f"{money(ra['retired'] - ra['paid'])}; premium over fair {money(ra['paid'] - fair / 100 * ra['retired'])}")
        out["close"][c.name] = dict(fair=fair, yield_now=y_now, carrying=carrying, face=face, ra=ra,
                                    gain_cv=cv_retired - ra["paid"], gain_face=ra["retired"] - ra["paid"],
                                    premium=ra["paid"] - fair / 100 * ra["retired"])

    print("\n" + "=" * 78)
    print("6. THE ROUND TRIP - can the city print money by selling and buying back?")
    print("=" * 78)
    for c in [CALM, HIKING]:
        a = auction(c, "bullet", 10, 3e8)
        cvc = Curve(c, extra_debt=0)
        flows = bullet_flows(a["coupon"], 120)
        holdings = {k: v for k, v in a["fills"].items()}
        ra = reverse_auction(c, cvc, flows, holdings, a["sold"], wanted=a["sold"])
        net = a["proceeds"] - ra["paid"]
        print(f"   {c.name[:40]:40} raised {money(a['proceeds'])}, bought all back for {money(ra['paid'])}: "
              f"{money(net)} ({'a loss' if net < 0 else 'A GAIN - a pump'})")

    if "--json" in sys.argv:
        with open(__file__.replace("debt-model.py", "figures.json"), "w") as f:
            json.dump(out, f, default=lambda o: o.__dict__ if hasattr(o, "__dict__") else str(o), indent=1)
        print("\nwrote figures.json")


if __name__ == "__main__":
    main()
