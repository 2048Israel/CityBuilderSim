"""Credit cards on the cells (spec-credit-housing.md section 2.2).

Each cell holds, per household: R the current card balance (all revolvers'
balances over all households), r the share revolving, L the balance behind on
its payments, late the share behind, lock the share shut out after a
charge-off. Within the cell a revolver's balance is lognormal (SIGMA_CARD)
around R / r, a household's buffer lognormal (SIGMA_BUFFER) around the cell's
savings. Nothing is drawn: every step is one of spread.py's closed forms.

A month, per cell:
  1. interest on R at the card rate (never over the policy's cap);
  2. shocks: SHOCK_P of the households not revolving meet an expense of
     SHOCK_MONTHS of take-home; those whose buffer is under it put the
     difference on the card (expected_shortfall), the rest pay from savings;
  3. the minimum: max(MIN_PAY x balance, interest + MIN_PRINCIPAL x balance);
     a revolver whose minimum is over what is left after its fixed bills and
     food is at risk: the share of balances over that line is a year's chance
     of falling behind, a twelfth of it moving to L each month;
     (CHARGE_OFF_MONTHS is the late stock's mean life, not the 180-day rule:
     one stock stands for six monthly buckets)
  4. paydown: a current revolver pays the minimum and PAYDOWN_SHARE of what
     it has spare beyond it; one whose whole balance is under that payment
     clears it and stops revolving;
  5. L cures at CURE or is charged off after CHARGE_OFF_MONTHS on average:
     written off by the bank, the household shut out for LOCKOUT_MONTHS.

    python cards.py [save.json] > out/cards.txt     (a save is read, never written)
"""
import sys, json
from spread import share_below, expected_shortfall, partial_mean_above

P = dict(
    CARD_RATE=0.2135,        # Bank of Canada: chartered banks' average rate on card balances, Aug 2026 (S2)
    RATE_CAP=0.35,           # rule: Canada's criminal interest rate, 35% APR since 2025-01-01; the cap's default
    MIN_PAY=0.03,            # practice: issuers' 3% (FCAC, S5); Quebec's 5% since 2025-08-01 is the lever's other end (S6)
    MIN_PRINCIPAL=0.01,
    SHOCK_P=1/6,             # est., calibrated (section 1): two unexpected expenses a year...
    SHOCK_MONTHS=0.75,       # ...of three quarters of a month's take-home
    PAYDOWN_SHARE=0.12,      # est., calibrated: with the two above, 43% revolving and 0.58 months owed (S1: 45%, 0.6)
    SIGMA_CARD=1.28,         # derived: sqrt(2 ln(6.1 / 2.7)), the SCF 2022's card balances (S1)
    SIGMA_BUFFER=1.5,        # est., until the households and wealth studies give the savings spread
    CURE=0.15,               # est.: a sixth of late balances catch up in a month
    CHARGE_OFF_MONTHS=9,     # derived: one stock's hazard for the Fed's charge-offs over its delinquency, 12 x 2.85 / 3.82 (S3)
    LOCKOUT_MONTHS=12,       # HouseholdBalance.LOCKOUT_MONTHS
)


class Cell:
    def __init__(self, name, n, y, fixed, food, buffer_months, adults=2):
        self.name, self.n, self.y = name, n, y
        self.fixed, self.food = fixed, food              # money a month, per household
        self.savings = buffer_months * y
        self.buffer0 = self.savings
        self.adults = adults
        self.R = self.r = self.L = self.late = self.lock = 0.0
        self.interest = self.written = self.newly_late = 0.0

    def spare(self):
        return self.y - self.fixed - self.food            # after the bills and the food


def month(c, p):
    i_m = min(p['RATE_CAP'], p['CARD_RATE']) / 12
    c.interest = c.R * i_m
    c.R += c.interest
    # 2. shocks, among those neither revolving, behind nor shut out
    free = max(0.0, 1 - c.r - c.late - c.lock)
    shock = p['SHOCK_MONTHS'] * c.y
    if c.savings > 0:
        borrow_per_hit = expected_shortfall(shock, c.savings, p['SIGMA_BUFFER'])
        share_borrowing = share_below(shock, c.savings, p['SIGMA_BUFFER'])
    else:
        borrow_per_hit, share_borrowing = shock, 1.0
    hits = p['SHOCK_P'] * free
    c.R += hits * borrow_per_hit
    c.r += hits * share_borrowing
    c.savings = max(0.0, c.savings - hits * (shock - borrow_per_hit))
    c.newly_late = 0.0
    if c.r <= 1e-12 or c.R <= 1e-12:
        c.R = c.r = 0.0
    else:
        cap = max(1e-9, c.spare())
        minshare = max(p['MIN_PAY'], i_m + p['MIN_PRINCIPAL'])
        # 3. those who cannot meet the minimum: B_i x minshare > cap
        B = c.R / c.r
        line = cap / minshare
        # the share over the line is a YEAR's chance of falling behind, not this month's: the
        # shape is put back every month, so moving the whole tail at once would move it again
        # next month (a conveyor); a twelfth a month leaves the tail to the households in it
        late_bal = partial_mean_above(line, B, p['SIGMA_CARD']) * c.r / 12
        late_share = (1 - share_below(line, B, p['SIGMA_CARD'])) * c.r / 12
        c.newly_late = late_bal
        c.R -= late_bal; c.r -= late_share
        c.L += late_bal; c.late += late_share
        # 4. the current revolvers pay the minimum and a share of their spare
        if c.r > 1e-12 and c.R > 1e-12:
            B = c.R / c.r
            pay = min(B, minshare * B + p['PAYDOWN_SHARE'] * max(0.0, cap - minshare * B))
            clear = share_below(pay, B, p['SIGMA_CARD'])
            below_mean = B - partial_mean_above(pay, B, p['SIGMA_CARD'])   # E[B; B <= pay]
            paid = c.r * (below_mean + pay * (1 - clear))
            c.R = max(0.0, c.R - paid); c.r = max(0.0, c.r * (1 - clear))
    # 5. the late pipeline
    cured, cured_share = c.L * p['CURE'], c.late * p['CURE']
    off, off_share = c.L / p['CHARGE_OFF_MONTHS'], c.late / p['CHARGE_OFF_MONTHS']
    c.L -= cured + off; c.late -= cured_share + off_share
    c.R += cured; c.r += cured_share
    c.written = off
    c.lock += off_share - c.lock / p['LOCKOUT_MONTHS']
    # the buffer is held where the surveys put it: what the households and wealth studies keep
    # it at is theirs to design (section 6); a save's run keeps its own savings the same way
    if p.get('HOLD_BUFFER', True):
        c.savings = c.buffer0
    else:
        c.savings += 0.2 * max(0.0, c.spare())


def stats(cells):
    H = sum(c.n for c in cells)
    debt = sum(c.n * (c.R + c.L) for c in cells)
    holders = sum(c.n * (c.r + c.late) for c in cells)
    late = sum(c.n * c.L for c in cells)
    written = sum(c.n * c.written for c in cells)
    income = sum(c.n * c.y for c in cells)
    return dict(share=holders / H, bal_months=(debt / holders) / (income / H) if holders else 0,
                late=late / debt if debt else 0, writeoff=12 * written / debt if debt else 0,
                debt_income=debt / (12 * income))


def survey_city():
    """Six tiers on the surveys' shape: take-home and buffers in money (est.)."""
    tiers = [("UNSKILLED", 3.0, 0.5), ("SKILLED", 4.2, 1.0), ("COLLEGE", 5.5, 1.6),
             ("PROFESSIONAL", 7.5, 2.6), ("SENIOR_PROFESSIONAL", 10.0, 4.0), ("ELITE", 18.0, 8.0)]
    weights = [0.22, 0.24, 0.22, 0.16, 0.10, 0.06]
    cells = []
    for (name, y, buf), w in zip(tiers, weights):
        rent = min(0.45 * y, 1.4)                 # rent takes more of a poor household's pay
        cells.append(Cell(name, w * 100_000, y, rent + 0.08 * y, 0.9, buf))
    return cells


def save_city(path):
    """A save's own cells, read only: savings, households, afterFixed, subsistence."""
    d = json.load(open(path, encoding='utf-8'))
    keys = d['householdCellKeys']; a = d['householdCells']
    n = len(keys); slots = (len(a) - 3) // n
    cells = []
    for k in range(n):
        v = a[k * slots:(k + 1) * slots]
        sav, hh, sub, af = v[0], v[3], v[7], v[slots - 1]
        if hh < 0.5 or keys[k].startswith(("ORPHAN", "PRISONER")): continue   # no card for a child or a prisoner
        y = max(af, 0.0) + max(sub, 0.0) + 1e-6      # take-home is not saved: afterFixed is what is left of it
        cells.append(Cell(keys[k], hh, y, y - max(af, 0.0), max(sub, 0.0), sav / y))
    return cells


def run(cells, p, months=240):
    for _ in range(months):
        for c in cells:
            month(c, p)
    return stats(cells)


def show(label, s):
    print(f"  {label:44s} revolving {s['share']:6.1%}  balance {s['bal_months']:5.2f} months' take-home"
          f"  late {s['late']:5.1%} of balances  written off {s['writeoff']:5.1%} a year  debt/income {s['debt_income']:5.1%}")


if __name__ == "__main__":
    print("--- 1. the survey-shaped city, 20 years from no debt ---")
    show("base (21.35% rate, 3% minimum)", run(survey_city(), P))
    for pd in (0.15, 0.35, 0.5):
        show(f"paydown share {pd}", run(survey_city(), dict(P, PAYDOWN_SHARE=pd)))
    print("--- 2. the levers ---")
    show("rate cap 12%", run(survey_city(), dict(P, RATE_CAP=0.12)))
    show("minimum payment 5% (Quebec)", run(survey_city(), dict(P, MIN_PAY=0.05)))
    show("minimum payment 2%", run(survey_city(), dict(P, MIN_PAY=0.02)))
    show("a bad decade: every take-home 15% lower",
         run([Cell(c.name, c.n, c.y * 0.85, c.fixed, c.food, c.savings / c.y) for c in survey_city()], P))
    print("--- 3. by tier, the base run ---")
    cells = survey_city(); run(cells, P)
    for c in cells:
        print(f"  {c.name:22s} revolving {c.r + c.late:6.1%}  behind {c.late:6.2%}  shut out {c.lock:6.2%}"
              f"  owed {(c.R + c.L) / c.y:5.2f} months  buffer {c.savings / c.y:6.1f} months")
    if len(sys.argv) > 1:
        print(f"--- 4. a save's own cells ({sys.argv[1].replace(chr(92), '/').split('/')[-1]}), their savings as they are ---")
        cells = save_city(sys.argv[1]); show("base", run(cells, P))
        for c in sorted(cells, key=lambda c: -(c.R + c.L) / c.y)[:6]:
            print(f"  {c.name:34s} households {c.n:9,.0f} revolving {c.r + c.late:6.1%} behind {c.late:6.2%}"
                  f" owed {(c.R + c.L) / c.y:6.2f} months  buffer {c.savings / c.y:8.1f} months")
