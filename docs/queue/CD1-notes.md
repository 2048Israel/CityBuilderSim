# CD1 - notes

*DRAFT, the plan, 2026-10-20, written before the build while EG1 ships. The
spec is `docs/studies/spec-city-debt.md` §3 (the curve; Jerus on §3.2,
2026-10-18: "if it is realistic then yes"), §8's CD1 row, §8.1 (the
current-code handoff) and §9 (D9, D15). Read at main 7089ce5. Nothing is
built yet; the sections below become the record when it is.*

## What CD1 does

The curve stops sitting on the dial in parallel. The market expects the dial
to drift to what the rule would set at the inflation it expects (§3.1, D9),
every month's forward is that expected dial plus the city's credit spread,
the zero rate compounds them and carries the term premium less QE (§3.2),
and every payment of every piece is discounted at its own month's factor.
A piece's yield is solved from that price, so it is a result, not the rate
it was handed (§1.2).

The short end does not move: `getRate()` is the forward at month 0, which is
today's quote to the cent (the floor plus the two spreads, clamped as
today). The policy dial and the central bank's facilities keep it (§8.1).

**No screen of its own** (D31): Borrow as an auction is CD2's, Close it now
CD4's. CD1's screen piece is §8's "the rate taken apart in its new parts", on
Your rate's curve card, and The book's yield, now solved from the price.

## The arithmetic (the spec's, with the game's names)

Month t ahead, t = 0 this month, to `YieldCurve.HORIZON` = 600 (fifty years,
`LongTermBond.MATURITIES`' longest):

    π∞    = Expectations.getExpectedInflation()           (cred x target + (1 - cred) x smoothed, floored)
    π(t)  = π∞ + (π now - π∞) x e^(-t / Expectations.ADAPT_MONTHS)
    R(t)  = DebtManager.advisedPolicyRate(π(t))            (the city's rule, inside the dial's bounds)
    p(t)  = R(t) + (floorRate() - R(0)) x e^(-t / DRIFT_MONTHS)
    f(t)  = max(MIN_RATE, min(p(t) + spreads, p(t) + ceilingRate() - floorRate()))
    z(m)  = ((prod over t < m of (1 + f(t)/12))^(1/m) - 1) x 12 + termPremium(m) - compression(m)
    d(m)  = (1 + z(m)/12)^(-m)

- `DRIFT_MONTHS` = 6 (D15), a new dial on the curve's class.
- `curveRate(m)` becomes z(m). On target with the dial at the rule the path is
  flat, z(m) is exactly today's `priceAt(debt, m)`, and every reader that
  takes a number off the curve (BondMarket's benchmark, PortDivision, the
  Policy tab's thirty-year line) reads what it reads today.
- `marketValue(piece)` becomes the sum of `remainingCashFlows()` times d(m):
  the city's curve for its own paper, the world's for a dollar piece (★5).
  This moves even a calm city: on a rising curve the early coupons are
  discounted at lower rates than the flat rate at maturity did.

## The slices (each a Sonnet brief; Opus reviews and commits)

1. **The curve, pure.** A new class `YieldCurve` (model, no Game): built from
   the floor, the rule as a function, π now, π∞, the spreads, the ceiling's
   gap, the compression by month; holds p, f, z and d to 600 months; `pv()`,
   `zero(m)`, `factor(m)`, `parYield(m)`, and the parts of a zero rate
   (`expectedDial(m)`, the compounded path less the spread). `DRIFT_MONTHS`
   and `HORIZON` live here.
2. **DebtManager on it.** `strikeExpectations(πnow, π∞)`, called at the
   month's top straight after the autopilot (month-order step 46) and held
   for the month (★3); unstruck (a fixture's bare DebtManager), the path is
   flat at the floor, which is today's curve. The curve cached on its inputs
   (floor, the struck pair, target, strictness, spreads, the central bank's
   share), rebuilt only when one moves. `curveRate`, `curveRateAtShare`,
   `foreignCurveRate` as zeros; `marketValue` flow by flow; `yieldOf(piece)`
   and `priceOfPar(piece)` solved from that price; `valuationRate()` and
   `householdBookYield()` read the solved yield. The rate taken apart:
   `expectedDialOver(m)`, `creditSpread()`, `termPremium(m)`,
   `compression(m)`, which add to `curveRate(m)`.
3. **The issue on the curve** (Game's quotes, home and abroad, so the
   rollover, automatic building and the build screen follow): the quote's
   fixed point iterates the curve with the new face in its credit; the note
   discounted at z(m) by the bank-discount convention it has (★8); the
   serial's one coupon solved so its schedule prices at par on the curve
   (closed form: PV is linear in the coupon, ★4); the term loan's coupon off
   z(n) by `longBondCouponYield()` and its face solved on the curve's factors
   (★6). Every quote's rate is the yield solved from its price, and that is
   what `markIssued()` records.
4. **The save.** The struck pair joins `marketToSave()` (slots 5 and 6;
   `restoreMarket()` reads them when the array has them). Everything else
   the path reads is saved already (Expectations, the dial, target,
   strictness, the central bank's book). No `SAVE_FORMAT` bump: an older save
   loads unstruck, priced flat on the floor until its next month's top, which
   is incomplete, not wrong (§8.1 agrees).
5. **The screen.** Your rate › THE CURVE: the thirty-year point taken apart in
   the new parts (the dial the market expects over thirty years, the credit
   spread, the term premium, less the central bank) on the chart's note and
   the card's line; `CURVE_INFO` rewritten. The book's piece card: "Y of par
   · yields Z" from `priceOfPar()` and `yieldOf()`.
6. **The count in a real run.** The trace gets one line: the months the
   curve was inverted (10-year under 1-year), steep (10-year over 1-year by
   more than two points), and the widest gap the market expected to close.

## The harness: `CurveCheck` (sections numbered as §8's list)

1. **Calm prices as today**: on target, autopilot, the dial at neutral; p(t)
   is the floor at every month; z(m) at every curve maturity equals the
   floor + spreads + `termPremium(m)` - `compression(m)`, rebuilt from
   DebtManager's constants; the short end is `getRate()`.
2. **Hiking inverts**: 6% inflation, the dial held at 7%, credibility 65%
   (§3.3's row): p(DRIFT_MONTHS) closes the gap to e^-1 of itself, π(ADAPT_MONTHS)
   to e^-1 of its own; p(600) is R(π∞) within 1e-6; the 10-year under the 1-year.
3. **Behind is steep**: 5%, the dial at 1% by hand, credibility 60%: the
   1-year over the 1-month by more than two points; the 10-year near §3.3's
   5.35% (the fixture's spreads stated).
4. **QE bends the long end**: half of `FULL_COMPRESSION_SHARE` held: z(m) is
   the calm z(m) less half the premium x `QE_COMPRESSION`; nothing at a year.
5. **The autopilot's gap is nothing** (★1, ★2): a Game city on autopilot,
   one month run; the struck pair is the index's inflation and Expectations'
   expected; at Standard and at Strict, R(0) is the dial.
6. **Every payment on its month**: a term loan, a serial, a note and a dollar
   term loan: `marketValue` is the hand sum of flows x d(m); the solved yield
   reprices the flows flat to the price within a cent; two bonds of one
   maturity with different coupons yield differently on a sloped curve.
7. **The issue at the curve**: each quote's schedule on the curve's factors at
   booking is worth what it raised plus the fees (the serial at par, the term
   loan at its gross; the note at or above its proceeds).
8. **No money pump**: issue and buy straight back each kind, home and abroad,
   in all four cities: the city ends no richer (neutral or worse), the
   MoneyAudit closes. RestructureCheck §5 is kept as it is.
9. **BondMarket reads it**: `Readings.curve(m)` is `curveRate(m)` in the
   hiking city, inverted under the corporate yields.
10. **The strike holds**: a city with a manual dial, expectations, QE
    holdings and pieces, saved and loaded: the curve at every maturity and
    every piece's price identical; `Expectations.takeMonth()` at the month's
    end moves nothing until the next top.
11. **The parts add up**: expected dial + spread + premium - compression is
    `curveRate(m)` at 10 and 30 years in all four cities.

## The numbers to meet (§3.3, the model's)

| city | 1m | 1y | 2y | 5y | 10y | 20y | 30y | 50y |
|---|---|---|---|---|---|---|---|---|
| calm | 3.12 | 3.12 | 3.18 | 3.35 | 3.62 | 4.02 | 4.27 | 4.62 |
| hiking | 7.12 | 6.85 | 6.50 | 6.04 | 6.02 | 6.27 | 6.47 | 6.78 |
| behind | 1.12 | 3.65 | 4.46 | 5.00 | 5.35 | 5.79 | 6.05 | 6.41 |
| calm, QE a quarter | 3.12 | 3.12 | 3.15 | 3.24 | 3.37 | 3.57 | 3.70 | 3.87 |

The check asserts the shapes against the constants, and these to the
hundredth only where its fixture is the model's city.

## What the existing checks will say (to confirm at the baseline)

- **CreditCheck §11**, "a hike of two points moves every row by two: the
  whole curve in parallel": the premise D9 replaces, if its city is struck.
  If it goes red it is rewritten to the decided mechanism (the short end by
  two, the long end by less, by `DRIFT_MONTHS`), and said so (★7).
- **ForeignDebtCheck** L679, "a dollar twenty-year is valued on the world's
  curve": true still, flow by flow; its arithmetic follows if it restates
  the flat price.
- **RestructureCheck**, **HoldersCheck**, **CentralBankCheck** §13-14,
  **MoneyCheck**: expected green; `Debt.getMarketValue(rate)` stays the flat
  arithmetic its sections test.

## The trace and the ensemble

Every trace that borrows moves (§8): the pieces' prices (the households'
paper ratio, the central bank's purchases), the serial's coupon (lower on a
rising curve, §1.1's 7-24 bp), the term loan's face, and every long rate in
a month off the rule. LongPlaytest (default and `--earth rotterdam`) is
re-baselined against main's; the ensemble runs once, against the newest
`docs/ensemble/` baseline on main (EN1's runner, `tools/ensemble/`).

## ★ Calls (Claude's, for Jerus)

1. ★1 The market expects the city's own rule, at its strictness
   (`advisedPolicyRate`), not Standard's: then the autopilot's gap is nothing
   at every strictness, as §3.1 says it is. At Standard it is the spec's.
2. ★2 "Inflation now" is the year's, the figure the rule reads
   (`PriceIndex.inflation()`), not Expectations' smoothed: same reason.
3. ★3 The path is struck at the month's top after the autopilot and held
   for the month (and saved): a player borrowing between the presses is
   priced on the path the month was.
4. ★4 The serial's coupon is solved at par on the curve now, not in CD2:
   at the old final-maturity coupon an inverted curve prices a new serial
   under par, which is the wrong direction for a round trip.
5. ★5 Dollar paper is priced flow by flow on the world's curve too; the
   world's dial does not move, so its path is flat (D26 keeps its pricing).
6. ★6 The term loan's coupon formula reads the zero rate at its term (today's
   figure in a calm city); its face is solved on the curve; its quote shows
   the yield solved from its price.
7. ★7 CreditCheck's parallel-curve assertion, if red, rewritten to D9.
8. ★8 The note keeps the bank-discount convention at its term's zero rate:
   buying it straight back costs a little more than it raised, never less.
