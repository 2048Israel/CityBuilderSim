# CD1 - notes

*0.8.15, 2026-10-20 and 21, overnight (Jerus's chain: CL2, then CD1 after EG1,
then CL3, CD2, FX5). The spec is `docs/studies/spec-city-debt.md` §3 (the
curve; Jerus on §3.2, 2026-10-18: "if it is realistic then yes"), §8's CD1
row, §8.1 (the current-code handoff) and §9 (D9, D15, D31). Planned while EG1
shipped, built from main 0f6e1b6 (0.8.12) and merged with CL2's 0.8.13 (9f1b328)
and CL3's 0.8.14 (ef3677c).*

**Jerus, 2026-10-21 morning:** "Keep D9 as decided: H1's premise is retired, A2 ships as noise at its threshold. Ship CD1." H1 is retired in
the scorer and A2 ships red at its threshold (§3).

## 1. What it does

The curve stops sitting on the dial in parallel. The market expects the dial
to drift to what the rule would set at the inflation it expects (§3.1, D9);
each month's forward is that expected dial plus the city's credit spreads;
the zero rate compounds the forwards and carries the term premium less QE's
compression (§3.2); and every payment of every piece is discounted at its own
month's factor, so a piece's yield is solved from its price and is a result,
not the rate it was handed (§1.2).

    pi(t) = pi_inf + (pi_now - pi_inf) e^(-t / Expectations.ADAPT_MONTHS)    pi_inf = Expectations' expected
    R(t)  = advisedPolicyRate(pi(t))                                        the city's rule, inside the dial
    p(t)  = R(t) + (floorRate() - R(0)) e^(-t / YieldCurve.DRIFT_MONTHS)    the dial the market expects
    f(t)  = p(t) + the two spreads, clamped as the short rate is             the forward; f(0) is today's quote
    z(m)  = the compounded forwards + termPremium(m) - compression(m)       the zero rate, curveRate(m)
    d(m)  = (1 + z(m)/12)^(-m)                                               a payment m months away

- **`YieldCurve`** (new, pure): the path, the forwards, the zeros and the
  factors to 600 months; `DRIFT_MONTHS` = 6 (D15) is its dial. A flat path
  prices the old parallel curve to the bit.
- **`DebtManager`**: `curveRate(m)` is the zero rate; `marketValue()` each
  remaining payment at its month's factor (dollar paper on the world's
  curve, `worldCurveAt()`, whose dial does not move); `yieldOf()` and
  `priceOfPar()` solved from that price; the rate taken apart
  (`expectedDialOver`, `creditSpread`, `expectedDialIn`), which add to
  `curveRate(m)` with the premium and the compression. The curve is cached
  on its inputs. The short end, `getRate()`, does not move.
- **The month's strike**: `Game.strikeTheCurve()`, straight after the
  autopilot sets the dial (month-order step 46), hands the debt market the
  year's inflation and Expectations' expected inflation; the pair is held for
  the month and saved (`marketToSave()`, slots 5 and 6). A manager never
  struck (a fixture's bare one) prices the flat path, today's curve.
- **The issue on the curve** (`quoteCurve()`, the quote's fixed point on the
  whole curve): the serial's one coupon that prices its schedule at par on the
  curve; the term loan's face solved on the curve's factors, its coupon off
  the zero rate at its term, its quote's rate the yield solved from its gross
  price; the dollar serial and term loan the same on the world's curve; the
  note at its term's zero rate by the bank-discount convention. The rollover,
  automatic building and the build screen take the same quotes.
- **The screen** (D31 gives CD1 none of its own; §8 gives it "the rate taken
  apart in its new parts"): Your rate's curve card takes the thirty-year point
  apart as the dial expected, the credit spread, the term premium and the
  central bank's compression, and says where the market expects the dial in a
  year and in five; The book's "of par" and "yields" are solved from the price.
- **No save-format bump**: the struck pair rides in an array the save already
  carries; an older save loads unstruck (the flat curve) until its next
  month's top - incomplete, not wrong (§8.1 agrees).

## 2. The checks

**CurveCheck** (new, 12 sections): 219 assertions, all green, 5.5 s. The
study's four cities match §3.3 to the hundredth (calm 3.12 / 3.12 / 3.18 /
3.35 / 3.62 / 4.02 / 4.27 / 4.62); the path closes by e in `DRIFT_MONTHS`
and the inflation expected by e in `ADAPT_MONTHS`; the autopilot's gap is
nothing at Standard and Strict; every payment at its month's factor; the
issue on the curve (the serial exactly at par, the term loan at its gross to
the half-cent its proceeds round to); round trips net the city minus the
fees, home and abroad; BondMarket's benchmark; the strike through a month and
a save to the bit; the parts add up. Its own count over 120 months of the
founding: the dial held at 3% left the rule by over a quarter point in 104
months; with the rule on the dial the gap was nothing and the curve inverted
in 104. Mutation test: flat pricing gives 7 FAILs, a 12-month drift 19.

The suite (`Batch check.bat --maps`, beside CL2's batch, timing soft): 125
harnesses run in 745 s, **125 passed**; on the tree merged with CL2's 0.8.13, 125 in 968 s, **125 passed**; the maps regenerated; Stale 0 firm
findings. Main's one red, ScaleCheck's "...and it builds" (SC1), passes here:
the moved trajectory builds more homes by its end, which is not SC1's fix -
SC1 stays queued. The timing harnesses, hard and alone (`--timing`): 8 run in 133 s, all green; on the merged tree 8 in 140 s, all green.

**On the tree merged with CL3's 0.8.14** (2026-10-21, `--maps`): 125
harnesses in 788 s, **124 passed**; the maps regenerated with nothing to
change; Stale 0 firm findings. ScaleCheck's "...and it builds" stays green.
The one red is **LongPlaytest**, on one finding of CL3's layout watch, which
is the map's: at month 3,998 the city buys land inside district (-2,-2),
already full (its owned plots 28,217 to 33,731), and its plan, replayed from
its log with the larger room, moves 2,069 of its 2,946 boxes (1,727 of them
houses); the watch excuses only a district whose carry-in changed, and this
one's did not. The model's trace is CD1's to the byte (§3), and main's
playtest, on its own path, raises no such finding. It is CL3's pending
re-chaining by another door (CL3-C), and CD1 does not touch the map. The
timing harnesses, hard and alone: 8 in 173 s, all green.

Existing harnesses restated, each premise kept (the design D9 replaced only
how they measured):
- **CentralBankCheck** `shape20`: the term premium less the compression was
  read as the 20-year over the 6-month note, which was the same figure while
  the curve was parallel; it is now the 20-year less the dial expected over
  20 years and the credit spread (the curve's own parts). Its "short end"
  line reads `curveRate(0)`.
- **CentralBankCheck** §11 and **HoldersCheck**: the price the central bank
  pays "at the market value the operation will see at the top of the month"
  is read after `strikeTheCurve()`, because the top now strikes the month's
  expectations before the operation.
- **ForeignDebtCheck**: the dollar 20-year "valued on the world's curve" is
  the hand sum of its payments, each at its month's rate; "issued at the rate
  it is valued at" compares the quote's yield with the booked piece's solved
  yield.
- **CreditCheck** §11's "the whole curve in parallel" stays green and
  unchanged: its bare manager is never struck (★7 not needed).

## 3. What moved in the trace, and why

**Two ensemble checks went red, and Jerus decided both** (2026-10-21 morning):
"Keep D9 as decided: H1's premise is retired, A2 ships as noise at its threshold. Ship CD1."

| check | what it asks | main 0.8.12 | CD1 | his call |
|---|---|---|---|---|
| H1 | a city whose dial is held at 0% by hand gets at least a point more inflation than its autopilot twin in its first ten years | +2.37 points | +0.27 points | **keep D9: H1's premise retired** (`tools/ensemble/score.py`, THE PREMISES RETIRED: measured and printed, neither scored nor counted) |
| A2 | on the autopilot, no stretch of 12 months or more with prices rising over 10% a year, in any of the 16 cities | longest 11 months (middle 8) | longest 12 months, one city (middle 7) | **ships as noise at its threshold**: still scored as written, red on 0.8.15's stored score |

- **H1 is the design doing its job.** You decided (D9) that the market expects
  the dial to go back to what the rule would set, whoever holds it. So holding
  it at 0% by hand now only makes short money cheap: long money stays priced
  on where the rule will take the dial, which is the study's "behind" city
  (§3.3: its 10-year at 5.35%, not 1.62%). The extra inflation comes later
  instead (+3.02 points in the second decade, against +3.63). Keeping D9 means
  retiring H1's first-decade test or rewriting it for a market that does not
  believe a hand on the dial. The other way is a new dial: the six-month drift
  slowing the longer the dial has been held off the rule, so a market learns
  that the hand stays (a batch of its own).
- **A2 looks like chance.** One city out of 16 had one 12-month stretch where
  the limit is under 12; the middle city's longest stretch got shorter (7
  months against 8). CD1 moves every city's path, and a moved path lands
  differently at a threshold. Nothing in CD1 pushes inflation up on the
  autopilot: the inflation from month 240 on is 2.23% in the middle city, as
  on main.

**After CL2 and CL3:** CL2 (0.8.13) and CL3 (0.8.14), merged into this
branch, move only the map, and each one's own trace is byte-identical to the
version before it, so the playtests and the ensemble below, run before the
merges, stand for the merged tree. Checked on the merge with CL3: the default
playtest traced on CD1's tip (7820daf) and on the merged tree is the same to
the byte in all eight CSVs (4,002 rows each; 619,699 people at the end). Its
report differs in the run time, CL3's layout lines and one finding of CL3's
watch (§2): the map's, not the model's.

Both playtests move from before their first checkpoint (month 266 on the
default run, 262 on Rotterdam), as soon as the city's inflation is off where
it is expected to settle: the corporate benchmark (`BondMarket` reads
`curveRate`), the households' paper ratio and the central bank's purchases
(piece prices payment by payment), the serial's coupon and the term loan's
face. Every month passed the audit and every reload matched, on both.

| | main 0.8.12 | CD1 |
|---|---|---|
| default: final population | 677,355 | 619,699 |
| default: the dial, median (max) | 3.71% (12.34%) | 2.81% (13.77%) |
| default: the city's paper at most | $1.05T (m3537) | $1.22T (m3537) |
| default: the rollover | 5 issues, $11.9B raised | 5 issues, $5.6B raised |
| Rotterdam: final population | 711,024 | 834,678 |
| Rotterdam: the city's paper at most | $88.9B (m2387) | $2.23T (m3533), none at home |

The new count (LongPlaytest's line): the default run's curve was inverted
(10-year under 1-year) in 1,658 months and steep (over it by two points) in
441, the widest move the market expected the dial to make 12.14 points;
Rotterdam 1,360, 384 and 17.72. The inversion is the common case on the
autopilot: inflation running over where credibility says it settles makes
the rule expected to fall, by more than the half-point term premium at ten
years. A curve that never changed shape would be a curve nobody tested.

**The ensemble** (label cd1, against `docs/ensemble/0.8.12.md`): 80 of 80 runs, 11 minutes, 22/26
(one vacuous) against 0.8.12's 25/27 as first scored; rescored from the same
runs by the scorer that retires H1, **22/25** (one vacuous, one retired),
stored as `docs/ensemble/0.8.15.md`.
S1 and R3 stay red, both nearer their lines (S1's longest spell 12 months
against 21; R3 4 seeds over a point against 5). **H1 is red by the design**:
a dial held at 0% by hand no longer buys cheap long money, because the market
expects it back at the rule, so the first decade's inflation over the
autopilot twin is 0.27 points against 2.37 (the second 3.02 against 3.63) -
§3.3's "behind" city in the ensemble's words; retired by Jerus's word (★9). H2 goes
vacuous with it (no first-decade burst to stop). **A2** is red on one seed's
12-month spell over 10% against "under 12" (median 7 months against 8):
read as the chaos of a moved trace at the threshold, not a mechanism, and
shipped as noise at its threshold by his word.

## 4. Who wrote what, and what review changed

| slice | drafted by | review changed |
|---|---|---|
| A, the curve in the model | Sonnet 5.5 | the strike as one Game method (`strikeTheCurve()`) the harnesses call too; the three harnesses restated (§2) |
| B, the issue on the curve | Sonnet 5.5 | ForeignDebtCheck's issue line restated; the struck pair's getters |
| C, the screen and the count | Sonnet 5.5 | nothing |
| D, CurveCheck | Sonnet 5.5 | its reflection on two private fields replaced with package-private read-outs (`Game.bondReadings()`, `BondMarket.readings()`) |

## 5. ★ Calls (Claude's, for Jerus)

1. ★1 The market expects the city's own rule at its strictness
   (`advisedPolicyRate`), not Standard's: then the autopilot's gap is nothing
   at every strictness, as §3.1 says it is. At Standard it is the spec's.
2. ★2 "Inflation now" is the year's, the figure the rule reads
   (`PriceIndex.inflation()`), not Expectations' smoothed one.
3. ★3 The pair is struck at the month's top after the autopilot and held for
   the month (and saved): a player borrowing between the presses is priced on
   the path the month was.
4. ★4 The serial's coupon is solved at par on the curve now, not in CD2: at the
   old final-maturity coupon an inverted curve sells a new serial under par.
5. ★5 Dollar paper is priced and sold payment by payment on the world's curve;
   the world's dial does not move, so its path is flat (D26 keeps its pricing).
6. ★6 The term loan's coupon reads the zero rate at its term (today's figure in
   a calm city); its quote shows the yield solved from its price.
7. ★8 The note keeps the bank-discount convention at its term's zero rate:
   bought straight back it costs a little more than it raised, never less.
8. ★9 **The ensemble's H1** (the ensemble line above): a dial held off the rule
   for years is still expected back within about six months, so holding it at
   0% stimulates less than it did. Keep D9 as it is (recommended: it is the
   decision, and the realistic reading of a market that does not believe a
   hand on the dial), or let the expected drift slow the longer a gap has stood
   (a market that learns the hand stays) - a new dial, a batch of its own.
   **Answered, 2026-10-21: keep D9; H1's premise is retired.**

## 6. What Jerus checks by eye (CD1-C)

Finances › Your rate in a city off its rule (a dial held by hand two points
from the advice): the curve inverted or steep on the chart; the thirty-year
point's four parts; "the market expects the dial at ... in a year and ... in
five". The book: a piece's "of par" and "yields", which now differ from the
curve's rate at its months.

## 7. State

**Ready but for one red, which is CL3's**, at the tip of `batch/CD1`: Jerus's
word recorded (2026-10-21: "Keep D9 as decided: H1's premise is retired, A2
ships as noise at its threshold. Ship CD1."), H1 retired in the scorer and A2
shipped as noise; merged with CL2's 0.8.13 and CL3's 0.8.14, version 0.8.15;
the merged playtest's trace CD1's to the byte, so its ensemble stands, stored
rescored as `docs/ensemble/0.8.15.md` (22/25); on the merged tree the suite
124/125, LongPlaytest red on CL3's layout watch (§2), and `--timing` 8/8
hard; the maps regenerated; the row out of `QUEUE.md` and CD1-C under
Pending. `ship.sh` stops at any red, so whether CD1 ships beside this one or
waits for a fix to the plan is the orchestrator's call.
