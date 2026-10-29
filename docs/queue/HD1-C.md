# HD1-C - the coupon abroad as profit: how it comes home

A call for Jerus, from HD1 (item 1), 2026-10-29. Jerus, 2026-10-28: "interest
abroad as profit sure". Built and taken back the same day (the orchestrator's
call, on the HD1 session's recommendation): it did what it was for, and it
turned the rate dial upside down in a young city.

## What was built (commit 3dea900f, reverted in 40cb44c5)

The coupon on a business's dollars abroad (the dollars held x the world's 2%
over twelve, at the month's rate) is struck with the sectors' statements
(`OutwardInvestment.payCoupons()`, before `strikeSectors()`), a line of
finance income below the operating line ("Interest abroad"), inside pre-tax
income, so the business tax and `Equity.PAYOUT` see it, and `Sector.bank()`
pays it into the till net of that tax - as the households' coupon has been
since 2026-09-17. The dollars no longer grow by it; the rate moving them is a
revaluation on the equity statement, no income. MoneyAudit keeps the income
credit and drops the reinvested debit. CapitalFlowCheck section 9 holds it on
a twin city (the coupon, the tax at the rate, the till, the dividend at
PAYOUT, the revaluation out of income). `git revert 40cb44c5` puts it back.

**What it did** (the default playtest, one run, a year's GDP):

| | dollars abroad m1200 | m2400 | m2852 | m4000 | tills m2852 | D$ per US$ m2852 | price index m2852 |
|---|---|---|---|---|---|---|---|
| main | 2.05 | 8.95 | 15.22 | 35.43 | 3.86 | 232 | 361 |
| the coupon home | 0.85 | 6.15 | 10.59 | 17.95 | 3.32 | 143 | 227 |

The hoard abroad halves by m4,000, and the D$ ends stronger with less
inflation - the appreciation `OutwardInvestment`'s old WHY warned of ("a
coupon from abroad is a current-account credit"). Real GDP at m4,000 is about
13% higher in that one run. Seed 4 of the ensemble with the coupon alone:
241,339 people at the end against main's 249,634 (the full ensemble was run
only with Real Estate's posts as well; see RE1).

## What it broke

**MonetaryCheck, "inflation falls with the rate"**, a founding city of 214
people over months 25-60 at four policy rates:

| policy | 3% | 10% | 20% | 40% |
|---|---|---|---|---|
| inflation, main | 10.43% | 9.68% | 8.99% | 8.82% |
| inflation, the coupon home | 7.92% | 6.25% | 6.76% | 8.17% |
| D$ per US$, main | 1.2745 | 1.0245 | 0.8306 | 0.7632 |
| D$ per US$, the coupon home | 1.2088 | 0.9233 | 0.7832 | 0.7755 |

A higher rate no longer lowers inflation. At a low rate the bank pays savers
next to nothing, so the businesses' idle money is abroad (`APPETITE` x the
spread, up to 90%), and its coupon, now paid home, lifts the D$ and cheapens
imports by more than the dial restrains; at a high rate the money is home,
earning the bank's deposit interest (outside the profit), and there is no
coupon to lift it. ScaleCheck's fixture month moved as well (its town at 807
no longer grows); with the coupon alone its copy tracks as main's does (the
first month's treasury miss 0.6-2.7%), so a rescan should find a window.

## The ways out, and the recommendation

1. **Counted as profit, kept abroad (recommended).** The statement carries the
   coupon, the tax and the dividend are struck on it, and the cash stays
   where it was earned: the dollars grow by it as before, and the till pays
   the tax and the dividend, bringing dollars home (`recall()`) only when it
   is short. A balance of payments books a foreign holding's reinvested
   earnings the same way: income in, the same amount out as investment. Most
   of the coupon then never crosses the exchanges; what does is at most the
   tax and the dividend on it (15% + 40% x 85%, about half), and only from a
   till that cannot cover them. To measure: MonetaryCheck's four rows, the
   hoard above and the ensemble.
2. **A dial**, the share of the coupon paid home, 0 (main) to 1 (as built):
   Jerus picks the city's rentier.
3. **As built, and MonetaryCheck re-premised.** Not recommended: the rate
   dial is the player's main lever, and the check is its proof.

The study's other figures (the businesses' $406.8T abroad, 47x GDP, 57% of it
compounded coupon) are in [HD1](HD1.md).
