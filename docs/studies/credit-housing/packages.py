"""Banks selling loans (spec-credit-housing.md section 2.5).

A package is a month's loans of one kind, sold as a pass-through: the buyer is
paid the loans' interest less the bank's servicing fee, their principal as it
is paid and prepaid, and bears their defaults less what is recovered; the bank
keeps RETAIN of every package (skin in the game). Part 1 prices one like a
bond. Part 2 is the catch: a bank that keeps less of what it lends screens less,
buyers learn the defaults only as they arrive, and the cycle follows.

    python packages.py > out/packages.txt
"""
import math

def smm(cpr): return 1 - (1 - cpr) ** (1 / 12)

def cash_flows(coupon, servicing, months, cpr, cdr, recovery, balance=100.0, amort=300):
    """The buyer's monthly cash flows on a pool: interest net of servicing,
    scheduled principal, prepayment, and what defaults recover."""
    r = coupon / 12; out = []; t = 0
    s, d = smm(cpr), smm(cdr)
    while balance > 1e-6 and t < months:
        left = amort - t
        sched = balance * r / (1 - (1 + r) ** -left) - balance * r if left > 0 else balance
        defaulted = balance * d
        live = balance - defaulted
        interest = live * (coupon - servicing) / 12
        sched_p = min(live, sched * live / balance)
        prepaid = (live - sched_p) * s
        out.append(interest + sched_p + prepaid + defaulted * recovery)
        balance = live - sched_p - prepaid
        t += 1
    if balance > 1e-6 and out: out[-1] += balance          # the term's end: the balance renews off the pool
    return out

def price(cfs, yld):
    v = 1 / (1 + yld / 12)
    return sum(cf * v ** (t + 1) for t, cf in enumerate(cfs))

def wal(cfs_principal):
    tot = sum(cfs_principal)
    return sum((t + 1) * c for t, c in enumerate(cfs_principal)) / tot / 12

def part1():
    print("--- 1. a package of five-year mortgages priced like a bond: price per 100 of balance ---")
    print("  coupon 4.55%, servicing 0.25%, 25-year amortization, 60-month term, recovery 75% (est.)")
    print(f"  {'buyer yield':>12s} " + " ".join(f"{f'CDR {c:.1%}':>11s}" for c in (0.0, 0.003, 0.01, 0.03)))
    for y in (0.035, 0.0405, 0.045, 0.055):
        row = [price(cash_flows(0.0455, 0.0025, 60, 0.08, c, 0.75), y) for c in (0.0, 0.003, 0.01, 0.03)]
        print(f"  {y:12.2%} " + " ".join(f"{v:11.2f}" for v in row))
    print("  prepayment, at a 4.05% yield and CDR 0.3%:", ", ".join(
        f"CPR {c:.0%} {price(cash_flows(0.0455, 0.0025, 60, c, 0.003, 0.75), 0.0405):.2f}" for c in (0.0, 0.08, 0.20)))
    print("--- 1b. a package of card balances: 21% rate, servicing 2%, paid 20% a month, recovery 5% ---")
    for cdr in (0.04, 0.08, 0.15):
        # a card pool as a 12-month revolving period held flat, then paid out
        cfs = cash_flows(0.21, 0.02, 24, 0.9, cdr, 0.05, amort=6)
        print(f"  CDR {cdr:.0%}: price at 6% {price(cfs, 0.06):6.2f}, at 9% {price(cfs, 0.09):6.2f}")

# ---------------------------------------------------------------------------
LAX_PD = 0.25      # est.: defaults up a quarter on loans whose risk the bank passes on whole (Keys et al. 2010, S20)
LAX_VOLUME = 0.30  # est.: approvals up 30% at the same laxity
LEARN = 24         # est.: months of observed defaults a buyer prices on
BASE_CDR = 0.004   # est.: an uninsured mortgage's yearly default rate in a sound city (S13's arrears)
SEASON = 36        # est.: months to a vintage's peak default

FALL_SENS = 6.0    # est.: a tenth off prices raises a prudent vintage's defaults by 60%
LAX_FALL = 4.0     # est.: and a lax one's by (1 + 4 x its laxity) times that: it was lent on less equity,
                   # so the same fall puts more of it under water

def vintage_cdr(age, quality, prices_fall, lax=0.0):
    shape = (age / SEASON) * math.exp(1 - age / SEASON)            # a hump at SEASON months
    return BASE_CDR * quality * shape * (1 + FALL_SENS * max(0.0, prices_fall) * (1 + LAX_FALL * lax))

CAPITAL_ROOM = 1.25  # est.: a bank holds capital for a quarter more than the book it keeps, so one that sells holds less
SHOCK_MONTH = 180  # a rate shock: house prices knocked down a tenth (homes.py's measured fall for 4 points at PASS 0.5)

def part2(sell_share, retain, months=360, label="", shock=True):
    """A bank originating a month's uninsured mortgages, selling sell_share of
    each month's new loans to buyers who price on the defaults they have seen.
    What it keeps must fit its capital: when buyers stop buying, it lends less."""
    vintages = []            # [age, balance kept, balance sold, quality]
    house = 1.0; peak = 1.0
    seen = [BASE_CDR] * LEARN
    selling = sell_share
    defaults_bank = defaults_buyers = 0.0; max_fall = 0.0; vol_path = []; kept = 0.0; sold_out = 0
    history = []; capacity = float('inf'); least_sold = sell_share
    for t in range(months):
        lax = selling * (1 - retain)                 # the share of a new loan's risk it will not bear
        quality = 1 + LAX_PD * lax
        volume = 1 + LAX_VOLUME * lax
        keep_share = 1 - lax
        runoff = sum(v[1] for v in vintages) * 0.006
        if t == 0:   # its capital: a quarter more than the book it keeps once that has settled
            capacity = CAPITAL_ROOM * volume * keep_share / 0.0065
        room = max(0.0, capacity - kept + runoff)
        if volume * keep_share > room:
            volume = room / max(keep_share, 1e-9)    # the capital rule: it lends only what it can keep
        vol_path.append(volume)
        # house prices follow credit: a hundredth of lending's gap a month, back to 1 by a sixtieth (est.)
        house += 0.01 * (volume - 1) - (house - 1) / 60
        if shock and t == SHOCK_MONTH: house *= 0.90
        peak = max(peak, house); fall = 1 - house / peak; max_fall = max(max_fall, fall)
        vintages.append([0, volume * keep_share, volume * lax, quality, lax])
        month_def = 0.0; month_bal = 0.0
        for v in vintages:
            v[0] += 1
            d = smm(vintage_cdr(v[0], v[3], fall, v[4]))
            kb, sb = v[1] * d, v[2] * d
            defaults_bank += kb * 0.25; defaults_buyers += sb * 0.25      # 75% recovered
            v[1] -= kb + v[1] * 0.006; v[2] -= sb + v[2] * 0.006        # amortized and prepaid
            month_def += kb + sb; month_bal += v[1] + v[2]
        vintages = [v for v in vintages if v[0] < 300]
        kept = sum(v[1] for v in vintages)
        observed = 12 * month_def / max(month_bal, 1e-9)
        seen = seen[1:] + [observed]; history.append(observed)
        expect = sum(seen) / LEARN
        ref = sum(history[-120:]) / len(history[-120:])
        # buyers price on the last LEARN months against the last ten years: a quarter over what
        # they are used to and they pay less, half over and they pay under par, where the bank
        # stops selling (est.; part 1: at a 4.05% yield par is near a CDR of 1%)
        selling = sell_share * max(0.0, min(1.0, 1 - (expect / ref - 1.25) / 0.25)) if t > 24 else sell_share
        if sell_share > 0 and selling < 0.5 * sell_share: sold_out += 1
        if t >= SHOCK_MONTH: least_sold = min(least_sold, selling)
    tot = defaults_bank + defaults_buyers
    after = vol_path[SHOCK_MONTH:]
    print(f"  {label:34s} losses {tot:5.2f} (bank {defaults_bank / max(tot, 1e-9):4.0%}), deepest price fall {max_fall:5.1%},"
          f" lending at its least {min(after) / vol_path[0]:4.0%} of before, selling at its least {least_sold:4.0%}, months under half {sold_out:3d}")

if __name__ == "__main__":
    part1()
    print("--- 2. the catch: 30 years of a bank selling its new uninsured mortgages, a rate shock in year 15 ---")
    part2(0.0, 1.0, label="keeps everything")
    part2(0.5, 0.05, label="sells half, keeps 5% of each")
    part2(0.95, 0.05, label="sells 95%, keeps 5% of each")
    part2(0.95, 0.20, label="sells 95%, must keep 20% of each")
    part2(0.95, 0.0, label="sells 95%, keeps nothing")
    print("--- 2b. the same without the shock ---")
    part2(0.0, 1.0, label="keeps everything", shock=False)
    part2(0.95, 0.05, label="sells 95%, keeps 5% of each", shock=False)
