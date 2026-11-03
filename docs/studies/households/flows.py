"""Households as flows: today's net-change pool against flows that carry their money.

A toy of the household ledger, not the game. Six pay tiers, each a working row,
and the retired; people move between them as the game's pyramid moves them
(AgeBand's rates), and every row's money is held per adult, as
HouseholdBalance holds it per grown-up.

Rule A (today, HouseholdBalance.moveStock): each month only each row's NET
change of adults is seen. Rows that shrank release their average into a pool;
rows that grew draw on it (a toy row is one cell, so "within the row first" is
the row itself); a gain nobody released arrives with OPENING_BUFFER_MONTHS of
take-home; a release nobody claimed leaves the city.

Rule B (the study, HL1): the pyramid's own transfers move their money with
them, each at the per-adult average of the row they left:
  - retiring: an established adult of tier t into retired[t] (the retired keep
    their former tier, so estates know their lineage);
  - dying: a partner's money stays with the survivor (COUPLED_SENIORS of the
    retired are couples); the money of one who dies alone is an estate, paid to
    the established adults of the same tier;
  - leaving: an emigrant takes the row's average out; an arrival brings the
    opening buffer.
With stages (HL2) the working row splits into young (18-34) and established
(35-69), and ageing into established carries its money too.

Run: python -I flows.py > out/flows.txt   (standard library only)
"""

# ---- the game's own figures (each named where it lives) -----------------
WAGES = [3.460, 4.500, 5.500, 8.200, 9.600, 15.600]   # PayTier, $k a month
JOB_MIX = [.30, .25, .20, .12, .08, .05]               # ★est. a tier's share of adults
WAGE_TAX = .15                                          # TaxPolicy.DEFAULT_INCOME_TAX
CPP = .0595                                             # SocialSecurity.DEFAULT_CONTRIBUTION_RATE
PENSION = .45 * WAGES[0]                                # SocialSecurity.DEFAULT_PENSION_REPLACEMENT
MPC = .80                                               # HouseholdBalance.MARGINAL_PROPENSITY
WEALTH_SPENT = .0033                                    # HouseholdBalance.WEALTH_SPENT_A_MONTH
BUFFER_MONTHS = 1.5                                     # HouseholdBalance.OPENING_BUFFER_MONTHS
COUPLED = .55                                           # FamilyModel.COUPLED_SENIORS
# AgeBand: span in years and annual mortality
TEEN_SPAN, ADULT_SPAN, SENIOR_SPAN = 5, 52, 15
MORT = {"adult": .00225, "senior": .0290, "elder": .1277}
YOUNG_SPAN = 17                                         # HL2: 18-34, then 35-69 (the rest of ADULT)

# ---- the toy's own (★est., each with its reason) ------------------------
SUBSISTENCE = 1.20      # ★ $k an adult a month spent whatever the income: about a basket, rent and bills at the unskilled tier
REAL_RETURN = .02 / 12  # ★ the deposit's real return, a month: the bank's ~2% real
EMIGRATION = .001       # ★ gross leavers a month, every row, as a share: Migration's departures, order of magnitude


def take_home(w):
    return w * (1 - WAGE_TAX - CPP)


def saving(income, wealth):
    """A month's change in one adult's money: the game's plan, reduced to one line."""
    spend = SUBSISTENCE + MPC * max(0.0, income - SUBSISTENCE) + WEALTH_SPENT * wealth
    return income - spend + REAL_RETURN * wealth


def mrate(annual):
    return 1 - (1 - annual) ** (1 / 12)


def run(rule, stages, growth, months):
    """Plays one city. growth(m) is the net arrival share a month (Migration)."""
    T = len(WAGES)
    teens = 6.0
    nY = [0.0] * T
    nE = [JOB_MIX[t] * 30 for t in range(T)]
    nS = [JOB_MIX[t] * 5 for t in range(T)]
    nL = [JOB_MIX[t] * 1 for t in range(T)]
    if stages:
        for t in range(T):
            nY[t], nE[t] = nE[t] * YOUNG_SPAN / ADULT_SPAN, nE[t] * (1 - YOUNG_SPAN / ADULT_SPAN)
    wY = [BUFFER_MONTHS * take_home(WAGES[t]) for t in range(T)]
    wE = list(wY)
    wR = [BUFFER_MONTHS * PENSION] * T
    out = []
    est_span = (ADULT_SPAN - YOUNG_SPAN) if stages else ADULT_SPAN
    for m in range(1, months + 1):
        g = growth(m)
        # --- a month's income and spending, per adult ---
        for t in range(T):
            y = take_home(WAGES[t])
            wY[t] = max(0.0, wY[t] + saving(y, wY[t]))
            wE[t] = max(0.0, wE[t] + saving(y, wE[t]))
            wR[t] = max(0.0, wR[t] + saving(PENSION, wR[t]))
        born = teens / (TEEN_SPAN * 12)
        teens *= 1 + g
        if rule == "B":
            for t in range(T):
                buf_w, buf_r = BUFFER_MONTHS * take_home(WAGES[t]), BUFFER_MONTHS * PENSION
                into = born * JOB_MIX[t]
                aY, aE, aR = g * nY[t], g * nE[t], g * (nS[t] + nL[t])
                kY = nY[t] / (YOUNG_SPAN * 12) if stages else 0.0
                ret = nE[t] / (est_span * 12)
                kS = nS[t] / (SENIOR_SPAN * 12)
                dY, dE = nY[t] * mrate(MORT["adult"]), nE[t] * mrate(MORT["adult"])
                dS, dL = nS[t] * mrate(MORT["senior"]), nL[t] * mrate(MORT["elder"])
                eY, eE, eS, eL = (EMIGRATION * n for n in (nY[t], nE[t], nS[t], nL[t]))
                alone = 1 - COUPLED
                MY = nY[t] * wY[t] + aY * buf_w - (kY + eY + dY * alone) * wY[t]
                ME = nE[t] * wE[t] + aE * buf_w + kY * wY[t] - (ret + eE + dE * alone) * wE[t]
                MR = (nS[t] + nL[t]) * wR[t] + (aR * buf_r) + ret * wE[t] - (eS + eL + (dS + dL) * alone) * wR[t]
                # estates: a retiree's to the established of the tier, an established adult's
                # to the young (their children) when there are stages, a young adult's to the established
                ME += (dS + dL) * alone * wR[t] + dY * alone * wY[t]
                if stages:
                    MY += dE * alone * wE[t]
                else:
                    ME += dE * alone * wE[t]
                nY2 = nY[t] + (into if stages else 0) + aY - kY - dY - eY
                nE2 = nE[t] + (0 if stages else into) + aE + kY - ret - dE - eE
                nS2 = nS[t] + aR * (nS[t] / max(1e-12, nS[t] + nL[t])) + ret - kS - dS - eS
                nL2 = nL[t] + aR * (nL[t] / max(1e-12, nS[t] + nL[t])) + kS - dL - eL
                nY[t], nE[t], nS[t], nL[t] = nY2, nE2, nS2, nL2
                wY[t] = MY / nY[t] if nY[t] > 1e-12 else 0.0
                wE[t] = ME / nE[t] if nE[t] > 1e-12 else 0.0
                wR[t] = MR / (nS[t] + nL[t]) if nS[t] + nL[t] > 1e-12 else 0.0
        else:
            # Rule A: the six tier rows (every adult) and ONE retired row; only net change is seen
            before = [nE[t] for t in range(T)] + [sum(nS) + sum(nL)]
            per = wE[:] + [wR[0]]
            for t in range(T):
                into = born * JOB_MIX[t]
                aE, aR = g * nE[t], g * (nS[t] + nL[t])
                ret = nE[t] / (est_span * 12)
                kS = nS[t] / (SENIOR_SPAN * 12)
                dE = nE[t] * mrate(MORT["adult"])
                dS, dL = nS[t] * mrate(MORT["senior"]), nL[t] * mrate(MORT["elder"])
                eE, eS, eL = (EMIGRATION * n for n in (nE[t], nS[t], nL[t]))
                nE2 = nE[t] + into + aE - ret - dE - eE
                nS2 = nS[t] + aR * (nS[t] / max(1e-12, nS[t] + nL[t])) + ret - kS - dS - eS
                nL2 = nL[t] + aR * (nL[t] / max(1e-12, nS[t] + nL[t])) + kS - dL - eL
                nE[t], nS[t], nL[t] = nE2, nS2, nL2
            after = [nE[t] for t in range(T)] + [sum(nS) + sum(nL)]
            pool = loss = gain = 0.0
            for i in range(T + 1):
                d = after[i] - before[i]
                if d < 0:
                    pool += -d * per[i]; loss += -d
                else:
                    gain += d
            moved = min(loss, gain)
            pool *= moved / loss if loss > 0 else 0.0
            for i in range(T + 1):
                d = after[i] - before[i]
                held = min(before[i], after[i]) * per[i]
                if d > 0:
                    claim = d * moved / gain if gain > 0 else 0.0
                    buf = BUFFER_MONTHS * (take_home(WAGES[i]) if i < T else PENSION)
                    held += (pool * claim / moved if moved > 0 else 0.0) + (d - claim) * buf
                w = held / after[i] if after[i] > 1e-12 else 0.0
                if i < T:
                    wE[i] = w
                else:
                    wR = [w] * T
        if m % 120 == 0:
            out.append((m, nY[:], nE[:], nS[:], nL[:], wY[:], wE[:], wR[:]))
    return out


def summary(label, rows, stages):
    print(f"\n== {label} ==")
    print(" year | people | young $k/adult | established $k/adult | retired $k/adult | retired / established | retired, years of pension")
    T = len(WAGES)
    for (m, young, est, sen, eld, wy, we, wr) in rows:
        ny, ne, nr = sum(young), sum(est), sum(sen) + sum(eld)
        my = sum(young[t] * wy[t] for t in range(T)) / ny if ny > 0 else 0
        me = sum(est[t] * we[t] for t in range(T)) / ne if ne > 0 else 0
        mr = sum((sen[t] + eld[t]) * wr[t] for t in range(T)) / nr if nr > 0 else 0
        yy = f"{my:14.0f}" if stages else "             -"
        print(f" {m // 12:4d} | {ny + ne + nr:6.0f} | {yy} | {me:20.0f} | {mr:16.0f} | {mr / me if me else 0:21.2f} | {mr / (12 * PENSION):6.1f}")


if __name__ == "__main__":
    grow = lambda m: .01 if m <= 240 else (.002 if m <= 1200 else 0.0)   # ★ a city that grows 20 years, then slows
    print("Households as flows: the toy (docs/studies/households/flows.py). Money per adult, $k (the game's thousands).")
    print(f"Take-home a month by tier: {[round(take_home(w), 2) for w in WAGES]}; pension {PENSION:.3f}; subsistence ★{SUBSISTENCE}")
    for t, w in enumerate(WAGES):
        y = take_home(w)
        eq = .2 * (y - SUBSISTENCE) / (WEALTH_SPENT - REAL_RETURN)
        print(f"  tier {t}: the plan's own resting wealth {eq:7.0f} $k = {eq / (12 * y):4.1f} years of take-home")
    eqr = max(0.0, .2 * (PENSION - SUBSISTENCE)) / (WEALTH_SPENT - REAL_RETURN)
    print(f"  retired on the pension alone: {eqr:7.0f} $k = {eqr / (12 * PENSION):4.1f} years of pension")
    summary("A: today's net-change pool, one adult band", run("A", False, grow, 2400), False)
    summary("B: flows carry their money, one adult band (HL1 alone)", run("B", False, grow, 2400), False)
    summary("B with stages: young 18-34 and established 35-69 (HL1 + HL2)", run("B", True, grow, 2400), True)
