# HL1: the households' books closed ({next}, 2026-11-11)

Brief: `docs/studies/spec-wealth.md` 2.4 and 3.4. Four slices (A the instrument, B the upkeep, C the
books closed and asserted, D what the ensemble then found), each coded by a Sonnet slice and reviewed;
E is the fixtures the new path moved; F and G are two findings of the stranger's review, sent back
before the gate ("The review's three findings", below).

## What it was, measured on main before any fix (e6bb1787, 0.9.92)

The study's driver (`docs/studies/wealth/WealthRun.java`, seed 0, months 240-900): the households' gap
is -210,230 over 661 months, -0.163% of mean savings a month, about -4.4% of the period's GDP,
negative in 605 months of 661. The study had -0.1% a month on 0.9.2: the leak was still there.

## What the leak was

The study named the services' upkeep (56%) and "transit and the bank" (44%). Struck month by month
(`MoneyAudit.Households`, slice A, and a hunt that explains every month to 6e-10 on two seeds) it was
four kinds of thing:

| kind | what | with the instrument alone (slice A), seed 0 / seed 1, months 2-900 |
|---|---|---|
| paid to nobody | the services' upkeep: care, schools, research, safety (transit's non-wage bill is 0 in every real city: no road or line in `buildings.json` has an upkeep; the bank's operating cost is usually 0, its branch rule closes what the fees do not cover) | 61,478 / 27,824 |
| destroyed | a bankrupt share's savings, `Household.discharge()`: `savings *= 1 - BANKRUPT_RATE` with nobody on the other end. It follows how many months a cell is bankrupt, which the path decides | 783 / 8,002; on slice B's path 30,218 / 117,105 (3.3% of seed 1's GDP) |
| out of step | money a payer has paid and the households are handed a press later, or the reverse: the public payrolls, pensions, the wage tax, the pension contributions and the EI and health premiums off the same payslips, fares, care and school fees, the three counters paid at the till after the strike (cars, boutiques, meals), and the yards' old metal | nets to nothing over a run, up to 940 in a month |
| a household-side fault | bills the sellers book in full that the household could not fund; income, rent, fees and shopping split over cells too small to settle; public wages paid at one fill and received at another; the census's orphan cells, of savings and of a bond's face | -4,450, +6,411, -675, 0.14 / -5,891, +2,759, -376, 0.16 |

So the study's "44% transit and the bank" was the lags and the faults, not a bill.

## What shipped

- **The identity** (`MoneyAudit.households()`, `Game.getLastHouseholdsAudit()`):
  `(held after - held before) - (to households - from households) - own = 0`, struck beside the pools'
  every press and asserted by `LongPlaytest.audit()` at the pools' tolerance (over a cent and over 1e-7
  of what moved). `householdsHeld()` is the savings less an unsettled buyback plus what is in transit
  (`householdsInTransit()`, the "out of step" row). The households' share trades with the world and
  what migrants bring and take are pairs of lines, so the balance of payments sees them; the two
  no-cash write-off halves are `Scope.BOOK`.
- **The upkeep is bought of Business Services** (★5 of the study, as recommended): its other revenue
  (`Sector.UPKEEP_SOLD`, "Upkeep work"), three quarters of it an imported service on its own books
  (`BusinessServices.UPKEEP_IMPORTED = 0.75`, measured: the grocers import 0.75-0.78 of their inputs,
  the restaurants 0.73-0.77, the builders 0.62-0.80), all of it where the sector has no seats. The
  buyers pay what and when they paid. In transit in the seller's pool until its strike
  (`Ledger.upkeepSold`, saved; a missing key reads 0, no format bump). The bank's operating cost is
  bought the same way on an input line of its own (`BANK_UPKEEP_SUPPLIES`, `sellBankUpkeep()`),
  which the national accounts leave out of their imports (slice F, below).
- **The bankrupt's savings repay the bank first** (my call, below): `seized = min(savings, debt)` per
  bankrupt household is a repayment through the channel that exists, the write-off is the rest, and
  what is left of the savings stays theirs.
- **The household-side faults are named, not mended**: nine "own" lines (`+ households Unfunded`,
  `- households UnsettledIncome`, `+ households UnsettledRent`, `UnsettledFees`, `UnsettledShopping`,
  `- households CensusStranded`, `+ households CensusCreated`, `+ households PublicWagesRestruck`,
  `- households BondFaceOrphaned`). Each needs a rule; HL2 (`docs/queue/HL2.md`) mends them and
  asserts the lines at nought.

## The final model, measured (seeds 0 and 1, months 240-900)

| | seed 0 (GDP 8.86M) | seed 1 (GDP 4.16M) |
|---|---|---|
| the identity's worst month | 6.4e-10 | 4.6e-10 |
| unfunded bills | 2,232 (0.025% of GDP, 227 months) | 29,770 (0.72%, 613 months) |
| income to cells too small to settle; their rent, fees, shopping | 4,921; 425 | 6,253; 454 |
| public wages struck twice | 393 | 216 |
| upkeep sold (Business Services' other revenue) | 58,579 | 85,544 |
| bankrupt household-months, of which in cells whose savings covered the debt | 3,978, 3,864 | 15,448, 13,619 |
| written off; repaid out of the bankrupt's savings | 127; 0.1 | 2,598; 1.7 |

The last two rows are a finding: with no income any debt is "at the ceiling", so the households
discharged are overwhelmingly ones that could repay (mean savings of a discharging cell 2,845 against
a debt of 1.11; 10,605 against 64.58). The old rule destroyed 4% of such a cell's savings a month:
that was most of the leak. They are still marked bankrupt, locked out a year, and a quarter leave (994
and 3,862 households in 661 months): HL2's ★2.

## What moves for the player

- **GDP falls by the imported share of the five services' upkeep** (care, schools, research, safety,
  transit): that upkeep was counted in the government's spending (G holds the services' gross cost)
  and earned by nobody; it is now an import but for Business Services' quarter. The bank's operating
  cost was never in G, and the bank's output is in no term of GDP, so its import is kept out of the
  accounts' (slice F): GDP does not move by it. On the two seeds the upkeep is 0-0.3% of GDP in the
  first five years, 1.8-3.2% in years 5-20 and 0.7-2.1% after, and no month reads negative.
- **A young town that is mostly services reads a GDP of nothing.** With no office the whole bill is
  imported. `ChildcareCheck.town` fell 39% (383 a month to 238: the schools' 143 and the care's 2)
  and stands at 283% of a year's GDP against the builder's 240% limit, where main's stood at 189%:
  its builder borrows nothing. `DebtExchangeCheck`'s young city, month 3: G is the upkeep alone
  (179.58, the clinic's 54.36 and the schools' 125.22, before a payroll is struck), all imported,
  and GDP reads -12.02, where main read 167.6, which was that upkeep and nothing else. The debt
  market charges a city with no GDP its whole five points: a bond sold that month clears at 8.21%
  where main's cleared at 3.26%; two months on, the payroll struck, GDP is 198. One bank, no
  operating cost: the bank's mend does not move either town. A player who builds a clinic and
  schools before homes, shops and an office will see this in the first months.
- **The ensemble's cities do not grow more slowly; the seeds reshuffle** (measured after slice F,
  "The review's three findings", 3): the median month of reaching 40,000 people is 1,526 against
  main's 1,212 and the median final population 11% lower, but seed by seed 9 reach it earlier, 6
  later and one never, the means are 1,394 against 1,373 months and 268,000 against 271,000 people.
- **Households keep more**: nothing is destroyed at a discharge, the bank writes off less.
- **Business Services earns more** (other revenue, a quarter of it kept), in every city with seats.

## The harness

`MoneyCheck` 13 (the industrial city's 240 months close, each own line counted), 14 (a cent taken from
a household between presses is seen), 15 (a bond's face no cell holds, caused, is named), 16 (the
contributions and premiums off the payslips, the levers opened and the rates moved twice, are in
transit a press), the old steel and old copper fixtures (the households' books close with the yards'
purchase in transit; each asserts the books would not close without its carrier), the upkeep section
(bought, struck, saved and reloaded in transit, no seats; the services' line and the bank's read
apart, and the households' books closing in the months the bank's operating cost is paid and
struck); `GdpCheck`'s last section (a city with three bank branches and no Business Services seats:
GDP is higher by exactly the bank's operating cost, 14.75, than C + I + G + exports less the sectors'
whole imports; with an office by three quarters of it, 11.07); `BankCheck` (the bankrupt's savings
repay the bank first, at half and at twice the debt); `LongPlaytest.audit()` every month, and its
run's report, which prints the nine own lines over the run and holds each to a bound (slice G).

Fixtures the new path moved, each re-caused or re-picked with a dated paragraph in its file, no
tolerance or claim changed: `ScaleCheck` (the free copies' month 172 to 165 by a scan of 160-195; the
miss is the K = 1 town's granule, K = 1,000 against 1,000,000 holds to 2e-5; the margin is thin, .86
of the savings' dial), `AuctionCheck` (the builder's debt limit opened, the town asserted within it),
`DebtExchangeCheck` (the dollar paper topped up to twice the surplus; the market pushed up a point at
a time until the bond is under its book, no step on main), `AutoBuildCheck` (the town stood at 189%
of a year's GDP against the 240% limit and stands at 283% with its schools' upkeep imported; the
sections that are about what a pass orders have it repay its central-bank advances out of its cash
first, the sections the limit is the claim of are as they were), `KeptCheck` (`ensureWorks` holds
Wood Products for the month it plays: on HL1's path the distress rule sold the sawmill the fixture
had just put up, "1 Sawmill (Wood Products) month 4821", and the page had no works to draw).

## What slice D was for

The ensemble on slice C flagged the households' identity in 16 of 80 runs, one month each. Two causes,
each replayed to the month, then a third found on the way:

| what | measured | the mend |
|---|---|---|
| the yards pay for the households' old steel and copper at once and strike the purchase a press later | seed 4 m654 0.3513, seed 11 m1382 27.3125, seed 6 m1398 19.01, seed 3 m2098 125.41 | a carrier, `MoneyAudit.oldMetalInTransit()` |
| a bond's record (`CorporateBond.households`) and its cells' own face drift apart at the census; the issuer pays the record, the cells are credited their own | seed 10 m183 -0.000409 (every bond 1e-6 short), seed 14 m207 -0.000588 | a ninth own line, `- households BondFaceOrphaned` (`BondMarket.getOrphanedToHouseholds()`); HL2 makes the record follow the cells |
| the pension contributions and the EI and health premiums are banked at the strike and charged at the next sync | `BondCheck`'s town, the one harness city with every lever open: 0.01 to 0.5 a month unnamed in 40 months of 40 (it was never asserted there) | three more carriers in `householdsInTransit()` |

After it the four seeds replayed close to 1e-14 of what moved in the months that were flagged, and
`BondCheck`'s town to nothing. In section 15's fixture the caused drift shrinks from 3.48 to 0.34 in
59 presses with every month closed: the issuer's bond is written down in its defaults (its face 31.8
to 3.1), the record and the cells by the same factor, and no money moves.

## The review's three findings (sent back before the gate, 2026-11-11)

The stranger's review said ship; the orchestrator sent three of its findings back first.

**1. The bank's upkeep came off GDP without its output ever being in it** (slice F). True: the
bank's operating cost, bought of Business Services since slice B, was booked as an import, and the
bank's output is in no term of GDP (not in G: the bank is not the government; not in C: nobody buys
it over a counter). The accounts' own rule is already written of the drivers' fuel, "the world's
part of their fuel is in neither C nor the imports": so the bank's part is kept out of the import
sum (not counted in G). It is booked on an input line of its own and
`EconomyManager.updateNationalAccounts()` leaves that line out; the sector's statement, the money
audit and the balance of payments keep it, because the cash did leave. Caused in `GdpCheck` (above).
What the review expected of it does not hold, though: the bank's upkeep is not what moved the
fixtures. An operating cost is paid only from the second branch on, and `DebtExchangeCheck`'s young
city and `ChildcareCheck`'s town have one. With main's own versions of the four re-caused fixtures
put back on the mended tree, all four fail as before (`AutoBuildCheck`'s town still at 283% of
240%). What moved them is the five services' upkeep, which is in G and is rightly netted ("What
moves for the player"). And the mend moves no ensemble run: no bank there pays an operating cost.

**2. Two own lines cancel by construction** (slice G). True: `PublicWagesRestruck` is the live public
wages less the struck payrolls while `householdsInTransit()` adds the same live figure, and
`BondFaceOrphaned` likewise for the bonds, so the residual cannot go red through those channels.
`LongPlaytest.audit()` now sums the nine lines over the run, checks every month that the nine named
lines are all of `own` (a tenth added to `households()` and not to `MoneyAudit.OWN_LINES` would
escape), and the run's report prints a row a line and flags one past its bound. The bound is on the
**mean monthly share** of what moved, not the run's total over the run's total: what moved is
nominal, the last years' prices are the whole sum (5.3e14 over the nightly's 4,000 months against
5.7e11 over the gate's 2,400), and a total share reads 1,000 times smaller on the longer run, so it
would bound nothing early. Each bound is three times the larger of the gate's and the nightly's
measured mean, rounded up to one figure:

| the line | fired, of 2,404 months | mean monthly share (gate; nightly) | bound |
|---|---|---|---|
| Unfunded | 1,073 | 2.77e-3; 1.66e-3 | 9e-3 |
| UnsettledIncome | 2,402 | 1.53e-3; 9.19e-4 | 5e-3 |
| UnsettledRent | 2,403 | 1.45e-4; 8.69e-5 | 5e-4 |
| UnsettledFees | 2,402 | 7.77e-6; 4.67e-6 | 3e-5 |
| UnsettledShopping | 161 | 2.06e-5; 1.24e-5 | 7e-5 |
| CensusStranded | 125 | 6.21e-8; 3.73e-8 | 2e-7 |
| CensusCreated | 43 | 2.16e-8; 1.29e-8 | 7e-8 |
| BondFaceOrphaned | 0 | under 1e-16 | 1e-7, the identity's own part in ten million |
| PublicWagesRestruck | 1,817 | 1.32e-4; 7.95e-5 | 4e-4 |

The run itself is not moved (its output is the untouched tree's to the digit). The ensemble's driver
does not print the table (`ownLinesOverTheRun()` is there for it to call); HL2 deletes all of it.
Seen on the way, not HL1's: the nightly's 4,000 months flag "a building on the map moved" three
times (months 3,008-3,989), on the untouched tree as well.

**3. "Cities grow more slowly ... not traced."** Measured again after (1): nothing moved, the 80
runs are the same to the month. So the figures stand (15 of 16 seeds reach 40,000, month 1,526 at
the median against 1,212, the median final population 199,236 against 224,453), and they do not
mean what the notes said. Three measurements on the 16 AUTO2 seeds:

| | main (0.9.93's own runs) | HL1 | HL1 with GDP counted as main counted it (a probe) |
|---|---|---|---|
| seeds reaching 40,000 | 16 | 15 (seed 5 stalls at 12-20,000 from month 600) | 15 (seed 3 stalls at 10,656; seed 5 reaches it) |
| month reached, median; mean of those that reach it | 1,212; 1,373 | 1,526; 1,394 | 1,213; 1,326 |
| final population, median; mean | 224,453; 271,300 | 199,236; 268,016 | 270,526; 256,573 |
| seed by seed against main: earlier, later | | 9, 6 (from 836 months earlier to 910 later) | 9, 6 |
| city-months with the treasury's debt over 240% of twelve times the month's GDP (the builder's limit, read off the runs' columns), of 36,496 | 1,588 | 621 | 1,067 |

The probe is the review's cause tested directly: the same build with the one change that the
services' imported upkeep is not taken off GDP (so the builder's limit, the debt market's spread and
everything else that reads GDP see main's figure; no money moves differently at the start). It moves
each seed by hundreds of months either way against HL1 (from 1,083 earlier to 335 later, 8 earlier
and 6 later), which is the size of the noise: a seed's month of reaching 40,000 is decided by its
path, and any change to the trace reshuffles the sixteen. The medians move with the reshuffle (HL1's
sits in a cluster of nine seeds between 1,446 and 1,761); the means do not, and seed by seed more
cities are earlier on HL1 than later. Two seeds that stalled on main (1 and 10: 44,799 and 68,356
people) grow to 166,000 on HL1; one that grew (5) stalls. And the builder's debt limit is not what
holds HL1's cities: they are over it in fewer months than main's, not more. So: no slower growth
that 16 seeds can show, and the review's mechanism, real in a town that is mostly services (above:
283% of 240%), is not at work in cities whose upkeep is 0-3% of GDP.

## The checks

`./batch-check.sh --pick` on the final tree (9cc2f755: the review's two mends, fe1a0930, with main
merged in, 13549f70: RB1 and LM1 shipped meanwhile, and RB1's fund's hold moves money beside this
identity, so they were checked together): 86 of 201 harnesses, exit 4, four NEW red assertions (nine
lines with their harnesses' exit and count lines), each of a class queued on main and none in HL1's
code; the same four, word for word, as on fe1a0930 before LM1 and as before the review's mends
(43565dc3). Without RB1 (91b976d2) the same three and not the first. `--timing` is the gate's.

| the line, word for word | whose | the figures |
|---|---|---|
| `CentralBankCheck \| ...and in at least STRICT_SHARE of them, 9 of 12 (8 hold) FAIL` | CB2's own line | main sits on the line at 9 of 12 and "a batch that moves the paths tips it about one time in three" (its row; RT1 read 8 too). 9 on HL1 alone, 8 with RB1 merged in |
| `CentralBankCheck \| very loose costs trust, through the lean measured against holding the target FAIL` | CB2's class | very loose's trust is under Standard's in 12 of 12 `STRICT_SHAPES` on main and in 11 of 12 on HL1; only the probe city's path moved, the mean 240-month inflation is the same on both builds. CB2's row should list this line |
| `HealthCheck \| ...and it buries more of its people over the run FAIL` | HX1's | 15.67 deaths per 1,000 against 15.67; the fee twins differ by 0.00-0.02, their noise; main records its sibling "its old first" |
| `KeptCheck \| most months make no node at all (12 of 25) FAIL` | KS1's class | a count on one city's path: 16 quiet months of 25 on main, 12 on HL1, the line needs 13 |

The slice-C tree had 42 NEW red lines; the other 39 were fixtures pinned to main's path (above).
Seen on the way, neither HL1's: the Bank page's integrity cards ("Its lines do not add up", "Its two
parts do not add up to its equity") fire on float dust in KeptCheck's city of 4,820 months, an absolute
$5 against books of 1e13 and more, on main as on HL1 (BI1, queued). In that city's month 4,821 two
sectors could not pay their bills on either build (main: Agriculture, Restaurants; HL1: Retail, and
Business Services written off); the upkeep sale cannot lose its seller money (`sellUpkeep()` books the
bill as revenue and three quarters of it, or all of it with no seats, as the import), so it is read as
that city's month, not traced further.

## The ensemble

`docs/ensemble/HL1.md` (80 runs on fe1a0930, the final tree): 22 of 26, where 0.9.93 scores 23. A2
and A3 fail as they do on main; HIGH's R3, main's third, passes; F1 fails in AUTO2 and ZERO_SLACK on
one finding, the same city-month in a run and its twin (below). No run has a households' or a pools'
finding (M1 0 months). Every one of the 80 runs is the same, month by month (people, GDP, deposits),
as the run on 91b976d2 before the review's mends and before RB1 was merged in: no bank in an
ensemble city pays an operating cost in any month (GDP would differ by it), and RB1's merge moves
none of them either. LM1 shipped (0.9.94) after the run and was merged in (9cc2f755): its notes say
the trace did not move, and AUTO2's 16 runs played again on the merged tree are the same month by
month as the record's, so the record of fe1a0930 stands and the 80 were not run a third time. The
run on slice C (20 of 26) had the households' identity flagged in 16 runs: slice D.

**The negative month** (seed 12, month 902): GDP reads -263.06 between 4,271.91 and 2,104.81.
Manufacturing imports 12,608 that month against 4,876 a month before, builds no stock with it, and
its exports fall to 670 the month after: the city's GDP halves from there. The upkeep's import is
242.52 of it, so the month would read -20.5 without HL1's accounting: the national accounts count a
month's imported inputs against GDP with no stock on the other side. Main's ensemble does not reach
such a month; HL1's path does, once in 192,000 city-months. Queued as NG1.

## Calls

Taken, each overturnable:
- ★5 (the study's): the upkeep bought of Business Services, three quarters imported. As recommended.
- **The bankrupt's savings repay the bank first.** The other way kept the rule and named the loss as
  a line, which leaves up to 3.5% of GDP a year vanishing: the row's own bug.
- **The faults are named and HL2 mends them**, rather than a rule for each inside this batch.

For Jerus (in `docs/queue/HL2.md`, both with a recommendation):
- ★1 who bears an unfunded bill: recommended nobody is sold to (it comes off the sellers' sales).
- ★2 a household whose savings cover its debt is not bankrupt: recommended.

## Left

- HL2, queued: the nine own lines to nought, the window opened before the restrike, a caused month
  for every line no city exercised.
- NG1, queued: a month's imported inputs with no stock built read GDP negative (the ensemble, above).
- BI1, queued: the Bank page's integrity cards on float dust (the checks, above).
- CB2's row should list `CentralBankCheck`'s trust line (the checks, above): the orchestrator's file.
- HL1-R (the orchestrator's, after the tag): the review's two lows, left as told: Business Services'
  kept share is a cliff at one seat, and `ScaleCheck`'s month 165 holds in 7 of 36 months. The young
  town's GDP of nothing ("What moves for the player") is the first one's matter: with no seat the
  whole upkeep is imported, with one a quarter is kept.
- The write-up for the project (`claude/<title>.md`; the batch changes how the game plays and leaves
  calls): this session has no way to write to the project; these notes are its matter.

## One in, one out

No process file changed (no script, no check's machinery, no rule a session reads). `LongPlaytest`
gained 120 lines of harness for the own lines' table and bounds, which HL2 deletes.
