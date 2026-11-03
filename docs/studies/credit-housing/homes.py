"""Owning a home on the cells (spec-credit-housing.md section 2.3).

Each cell holds, per household: own the share owning, M the mortgage per
owner, rate the owners' average contract rate, left their average months of
amortization left, arrears the share of owners behind. The city holds one
price per person of capacity, P, as the rents are held per person of capacity.

A month:
  1. the mortgage rate is the policy rate plus the bank's spread; a fifth of
     the owners' book renews each year at it (TERM_MONTHS);
  2. buyers: BUY_PACE of the gap between a cell's wanted share (by life stage)
     and its share owning, times the share that passes the bank's tests at P:
     savings over the down payment and closing, income over what the payment
     at the stress rate, the property tax and the heat need at GDS (39%);
  3. sellers: owners who leave (EXIT by stage), and forced sales from arrears;
  4. the landlord sells let homes when P is over what a let home is worth to
     it, net rent over its cap rate, and buys when P is under (SELL_PACE);
  5. builders start homes for sale when P clears the cost by BUILD_MARGIN;
  6. P* clears buyers against sellers, the landlord and the builders' finished
     homes (bisection); P walks a PRICE_LAG_MONTHS-th of the way to it;
  7. owners pay the annuity, the tax and the upkeep; one whose take-home cannot
     is at risk (a year's chance, as cards.py), and ARREARS_MONTHS behind the
     home is sold, the loss over the price the insurer's or the bank's.

    python homes.py > out/homes.txt
"""
import math
from spread import share_below, partial_mean_above

P0 = dict(
    POLICY=0.0275,           # est.: a neutral policy rate
    MORTGAGE_SPREAD=0.018,   # est.: a five-year fixed over the policy rate
    STRESS_ADD=0.02,         # rule: qualify at the contract rate + 2 points (OSFI B-20, S18)
    GDS=0.39, TDS=0.44,      # rule: CMHC's limits (S18)
    MIN_DOWN=0.05,           # rule: 5% of the first tier (S18); one share in the game, section 2.3
    INSURED_BELOW=0.20,      # rule: under 20% down the loan must be insured (S18)
    PREMIUM_95=0.04,         # rule: CMHC's premium at 95% loan-to-value (S19)
    AMORT=300, TERM_MONTHS=60,   # rule: 25 years insured (S18); est.: the five-year term
    CLOSING=0.015,           # est.: closing costs, a share of the price
    PROPERTY_TAX=0.015,      # TaxPolicy.DEFAULT_PROPERTY_TAX
    UPKEEP=0.01,             # RealEstate.MAINTENANCE_PER_YEAR
    HEAT_SHARE=0.01,         # est.: heat a month, a share of gross income, in GDS
    BUY_PACE=1/36,           # est.: a third of the wanting gap a year looks to buy
    SELL_PACE=0.004,         # est.: the most of its let homes the landlord sells or buys in a month
    BAND=0.10,               # est.: the price gap over its value at which it sells at full pace
    LANDLORD_PREMIUM=0.015,  # est.: the landlord's cap rate over the insured mortgage rate
    PASS=0.4,                # how much of the mortgage rate's move reaches the cap rate (call C4): 0.4 gives
                             # -6.6% a point, against the Fed paper's -6.9 (S16)
    YIELD=0.058,             # RealEstate.LANDLORD_YIELD: rent's own target, unchanged
    BUILD_MARGIN=0.33,       # RealEstate.BUILD_MARGIN
    BUILD_PACE=0.0015,       # est.: homes started a month at full margin, a share of households
    BUILD_MONTHS=18,         # est.
    PRICE_LAG_MONTHS=12,     # est.: as the lease (RealEstate.LEASE_MONTHS)
    SIGMA_SAVINGS=1.5,       # est., until the households and wealth studies give the spread
    SIGMA_INCOME=0.35,       # est.: pay inside one tier and stage
    ARREARS_MONTHS=9,
    SAVINGS_MONTHS=6,        # est.: months of the bills an owner's savings must cover before it falls behind        # est.: power of sale after about nine months
    CURE=0.08,               # est.
    SELL_COST=0.05,          # est.: a forced sale's costs
)

# a year's share of each stage that moves on to the next (est., until the households study's age bands)
MOVE_ON = {"young": 1/10, "family": 1/15, "middle": 1/25}
NEXT = {"young": "family", "family": "middle", "middle": "retired"}

STAGES = [  # name, weight, door capacity, wanted share owning (S14, S15: the age bands), exit a year, savings months of gross, earners
    ("young",   0.20, 2, 0.40, 0.010, 6.0, 1.0),
    ("family",  0.28, 4, 0.66, 0.010, 14.0, 1.8),   # rent's own yardstick is a working couple (RealEstate.REFERENCE_EARNERS)
    ("middle",  0.27, 4, 0.76, 0.012, 30.0, 2.0),
    ("retired", 0.25, 2, 0.80, 0.040, 60.0, 0.7),   # a pension, est.
]
TIERS = [("UNSKILLED", 3.6, 0.22), ("SKILLED", 5.0, 0.24), ("COLLEGE", 6.6, 0.22),
         ("PROFESSIONAL", 9.0, 0.16), ("SENIOR_PROFESSIONAL", 12.0, 0.10), ("ELITE", 21.6, 0.06)]


def annuity(principal, annual, months):
    r = annual / 12
    if months <= 0: return principal
    if r <= 0: return principal / months
    return principal * r / (1 - (1 + r) ** -months)


class Cell:
    def __init__(self, tier, gross, stage, w, k, want, exit_, sav_m):
        self.name = f"{stage}:{tier}"; self.stage = stage
        self.n = w * 100_000; self.Y = gross; self.y = 0.8 * gross
        self.k = k; self.want = want; self.exit = exit_ / 12
        self.S = sav_m * gross
        self.own = 0.0; self.M = 0.0; self.rate = 0.0; self.left = 0.0; self.arrears = 0.0
        self.bought = self.sold = self.forced = self.loss = 0.0


class City:
    def __init__(self, p):
        self.p = dict(p)
        self.cells = [Cell(t, g * earners, s, tw * sw, k, want, ex, sav)
                      for (t, g, tw) in TIERS for (s, sw, k, want, ex, sav, earners) in STAGES]
        # rent per person of capacity: TARGET_RENT_BURDEN of a working couple's take-home, in a home for four
        self.rent = 0.30 * 2 * 0.8 * TIERS[0][1] / 4
        self.cost = self.rent * 12 / p['YIELD']            # the cheapest home's cost, per capacity: rent's floor
        self.P = self.cost
        self.capacity = sum(c.n * c.k for c in self.cells) * 1.03
        self.pipeline = [0.0] * p['BUILD_MONTHS']
        self.month = 0
        self.log = []

    def mortgage_rate(self):
        return self.p['POLICY'] + self.p['MORTGAGE_SPREAD']

    def landlord_value(self):
        p = self.p
        net = self.rent * 12 * (1 - 0.18) - self.P * p['PROPERTY_TAX'] * 0   # tax and upkeep: the 18% (est.)
        insured = p['POLICY'] + p['MORTGAGE_SPREAD'] - 0.004               # an insured landlord loan (est.)
        neutral = P0['POLICY'] + P0['MORTGAGE_SPREAD'] - 0.004
        cap = (neutral + p['LANDLORD_PREMIUM']) + p['PASS'] * (insured - neutral)
        return net / max(cap, 0.01)

    def owned_capacity(self):
        return sum(c.n * c.own * c.k for c in self.cells)

    def buyers(self, c, price, rate):
        p = self.p
        gap = max(0.0, c.want - c.own - c.arrears)
        if gap <= 0: return 0.0, 0.0
        V = price * c.k
        need = (p['MIN_DOWN'] + p['CLOSING']) * V
        a_sav = 1 - share_below(need, c.S, p['SIGMA_SAVINGS'])
        if a_sav <= 0: return 0.0, 0.0
        # those who can, put down a fifth; a buyer with less puts down what it has over the closing
        mean_above = partial_mean_above(need, c.S, p['SIGMA_SAVINGS']) / max(a_sav, 1e-12)
        down = min(p['INSURED_BELOW'] * V, max(p['MIN_DOWN'] * V, 0.5 * mean_above - p['CLOSING'] * V))
        loan = V - down
        if down < p['INSURED_BELOW'] * V: loan *= 1 + p['PREMIUM_95']
        stress = rate + p['STRESS_ADD']
        need_y = (annuity(loan, stress, p['AMORT']) + V * p['PROPERTY_TAX'] / 12) / p['GDS'] + p['HEAT_SHARE'] * c.Y / p['GDS']
        a_inc = 1 - share_below(need_y, c.Y, p['SIGMA_INCOME'])
        return p['BUY_PACE'] * gap * c.n * a_sav * a_inc, loan

    def move_on(self, rate):
        p = self.p; up = down = 0.0
        by = {c.name: c for c in self.cells}
        flows = []
        for c in self.cells:
            if c.stage not in NEXT: continue
            d = by[f"{NEXT[c.stage]}:{c.name.split(':')[1]}"]
            m = c.n * MOVE_ON[c.stage] / 12
            flows.append((c, d, m))
        for c, d, m in flows:
            movers = m * c.own
            if movers <= 0: continue
            arrears_movers = m * c.arrears
            extra = (d.k - c.k) * self.P
            M_new = c.M + extra
            if extra > 0:
                # the bank's test on the new loan; those who fail sell and rent (est.)
                need_y = (annuity(M_new, rate + p['STRESS_ADD'], p['AMORT']) + d.k * self.P * p['PROPERTY_TAX'] / 12) / p['GDS']
                passes = 1 - share_below(need_y, d.Y, p['SIGMA_INCOME'])
                up += movers * passes * (d.k - c.k)
                down += movers * (1 - passes) * c.k
                kept = movers * passes
            else:
                # downsizing: the spare capacity is sold, the mortgage paid from it first
                down += movers * (c.k - d.k)
                M_new = max(0.0, c.M + extra)
                kept = movers
            owners_d = d.n * d.own
            tot = owners_d + kept
            if tot > 0:
                d.M = (owners_d * d.M + kept * M_new) / tot
                d.rate = (owners_d * d.rate + kept * (rate if extra > 0 else c.rate)) / tot
                d.left = (owners_d * d.left + kept * (p['AMORT'] if extra > 0 else c.left)) / tot
            d.own = tot / d.n
            d.arrears = min(d.own, d.arrears + arrears_movers / d.n)
            c.own -= movers / c.n
            c.arrears = max(0.0, c.arrears - arrears_movers / c.n)
        return up, down

    def step(self):
        p = self.p; rate = self.mortgage_rate(); self.month += 1
        # 1. renewals: a TERM_MONTHS-th of each owner book moves to today's rate
        for c in self.cells:
            if c.own > 0: c.rate += (rate - c.rate) / p['TERM_MONTHS']
        # 3. sellers who must
        forced_doors = 0.0; leaving = 0.0
        for c in self.cells:
            leave = c.n * c.own * c.exit
            fs = c.n * c.arrears / p['ARREARS_MONTHS']
            c.sold, c.forced = leave, fs
            leaving += leave * c.k; forced_doors += fs * c.k
        # the people move on (the households study's flow): owners carry their book, and trade
        # up or down to the door the next stage needs at today's price
        tradeup, tradedown = self.move_on(rate)
        supply_fixed = leaving + forced_doors + tradedown + self.pipeline.pop(0) - tradeup
        let = self.capacity - self.owned_capacity()
        VL = self.landlord_value()

        def excess(price):
            demand = sum(self.buyers(c, price, rate)[0] * c.k for c in self.cells)
            gapr = max(-1.0, min(1.0, (price - VL) / VL / p['BAND']))
            landlord = p['SELL_PACE'] * (let if gapr > 0 else self.capacity) * gapr
            return demand - supply_fixed - landlord

        lo, hi = self.P / 4, self.P * 4
        for _ in range(40):
            mid = math.sqrt(lo * hi)
            if excess(mid) > 0: lo = mid
            else: hi = mid
        target = math.sqrt(lo * hi)
        self.P += (target - self.P) / p['PRICE_LAG_MONTHS']
        # 5. builders start for sale when the price clears the cost by the margin
        clear = (self.P / (self.cost * (1 + p['BUILD_MARGIN'])) - 1) / 0.10
        starts = p['BUILD_PACE'] * sum(c.n for c in self.cells) * 3 * max(0.0, min(1.0, clear))
        self.pipeline.append(starts); self.capacity += starts
        # 2. the purchases at the walked price, and 7. the owners' month
        for c in self.cells:
            b, loan = self.buyers(c, self.P, rate)
            V = self.P * c.k
            owners = c.n * c.own
            # sellers first: they leave with their share of the book
            out = c.sold + c.forced
            if out > 0 and owners > 0:
                lost = max(0.0, c.M - V * (1 - p['SELL_COST']))
                c.loss = c.forced * lost
                owners_after = max(0.0, owners - out)
                c.arrears = max(0.0, c.arrears - c.forced / c.n)
            else:
                owners_after = owners; c.loss = 0.0
            if b > 0:
                tot = owners_after + b
                c.M = (owners_after * c.M + b * loan) / tot
                c.rate = (owners_after * c.rate + b * rate) / tot
                c.left = (owners_after * c.left + b * p['AMORT']) / tot
                owners_after = tot
            c.own = owners_after / c.n; c.bought = b
            if c.own > 0 and c.M > 0:
                pay = annuity(c.M, c.rate, max(1.0, c.left))
                c.M = max(0.0, c.M - (pay - c.M * c.rate / 12)); c.left = max(0.0, c.left - 1)
                need = pay + V * (p['PROPERTY_TAX'] + p['UPKEEP']) / 12
                # at risk: take-home under the bills plus a basket (est.: 0.9 a household)
                # and its savings cannot carry the gap for SAVINGS_MONTHS (the waterfall draws them first)
                risk = (share_below(need + 0.9, c.y, p['SIGMA_INCOME'])
                        * share_below(p['SAVINGS_MONTHS'] * need, c.S, p['SIGMA_SAVINGS']))
                c.arrears += (c.own - c.arrears) * risk / 12
                c.arrears -= c.arrears * p['CURE']
                c.arrears = min(c.arrears, c.own)

    def run(self, months):
        for _ in range(months):
            self.step()

    def summary(self):
        H = sum(c.n for c in self.cells)
        own = sum(c.n * c.own for c in self.cells) / H
        owners = sum(c.n * c.own for c in self.cells)
        mort = sum(c.n * c.own * c.M for c in self.cells)
        income = sum(c.n * c.y for c in self.cells) * 12
        # price/income: a home for four against the median family's gross a year
        fams = sorted((c for c in self.cells if c.stage in ("family", "middle")), key=lambda c: c.Y)
        half, acc = sum(c.n for c in fams) / 2, 0.0
        for fam in fams:
            acc += fam.n
            if acc >= half: break
        arrears = sum(c.n * c.arrears for c in self.cells) / max(owners, 1e-9)
        return dict(own=own, P=self.P, pti=self.P * 4 / (fam.Y * 12), mort_income=mort / income,
                    arrears=arrears, VL=self.landlord_value())


def show(label, s, base_P=None):
    move = f"  price {s['P'] / base_P - 1:+6.1%}" if base_P else ""
    print(f"  {label:40s} owning {s['own']:6.1%}  price/income {s['pti']:4.1f}  mortgages/take-home {s['mort_income']:6.1%}"
          f"  arrears {s['arrears']:5.2%}{move}")


if __name__ == "__main__":
    print("--- 1. a city of renters (today's game) when owning opens, by year ---")
    city = City(P0)
    for year in (1, 3, 5, 10, 20, 40):
        city.run(year * 12 - city.month)
        show(f"year {year}", city.summary())
    base = city.summary()
    print("--- 2. by life stage and tier at year 40 (wanted share in brackets) ---")
    for c in city.cells:
        if c.name.split(':')[1] in ("UNSKILLED", "COLLEGE", "ELITE"):
            print(f"  {c.name:28s} owning {c.own:6.1%} ({c.want:.0%})  mortgage {c.M / (c.Y * 12):4.1f} years' gross  arrears {c.arrears:6.2%}")
    print("--- 3. the rate: the same city held 10 years at another policy rate ---")
    for pol in (0.01, 0.0375, 0.0475, 0.0675):
        c2 = City(dict(P0, POLICY=pol)); c2.run(480)
        show(f"policy {pol:.2%}", c2.summary(), base['P'])
    for pas in (0.0, 0.5, 1.0):
        b2 = City(dict(P0, PASS=pas)); b2.run(480)
        c2 = City(dict(P0, POLICY=0.0375, PASS=pas)); c2.run(480)
        show(f"policy 3.75%, cap rate pass-through {pas}", c2.summary(), b2.summary()['P'])
    print("--- 4. a shock: the policy rate from 2.75% to 6.75% at year 40, by year after ---")
    city.p['POLICY'] = 0.0675
    for year in (1, 2, 3, 5, 10):
        city.run((40 + year) * 12 - city.month)
        show(f"year {year} after", city.summary(), base['P'])
