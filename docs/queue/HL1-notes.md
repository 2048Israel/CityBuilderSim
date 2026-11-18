# HL1: the households' books closed ({next}, resumed 2026-11-18)

**Where it stands (2026-11-18).** Jerus lifted the stop. The existing batch/HL1 was merged with
origin/main 477bdb1c3 (0.9.150), never rebased. The old measurements below are historical; the resumed
checks and final ensemble are recorded under "Resumed on current main". The ship file stays off the
tip during building. HN1 and LM1-D must land before the final model is measured; the orchestrator
places the ensemble and dry run. The 2026-11-11 stopped state follows for provenance.

Brief: `docs/studies/spec-wealth.md` 2.4 and 3.4. Four slices (A the instrument, B the upkeep, C the
books closed and asserted, D what the ensemble then found), each coded by a Sonnet slice and reviewed;
E is the fixtures the new path moved; F and G are two findings of the stranger's review, sent back
before the gate ("The review's three findings", below).

**Where it stands (2026-11-11, the queue stopped by Jerus: "Stop now", main's 72917eb4).** The
review's three findings are done, the checks and the ensemble are below, and main is merged in
(72917eb4). Only the ship file is not on the tip: it is the batch's "ready", and the queue was stopped
before it went on. It is written, in 66f565b4 (`git show 66f565b4:docs/queue/HL1-ship.md >
docs/queue/HL1-ship.md`, commit, push last). To take it up again: merge main (LM1-D is ahead of HL1
and widens the founding's water: if the default city founds differently, play AUTO2's 16 runs again
and the 80 only if they moved), `--pick`, the ship file last.

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

## Resumed on current main (2026-11-18)

### The merge: 477bdb1c3

Generated maps, dials, harness index and month order were taken from main as temporary inputs and
regenerated by `./batch-check.sh --fast`; no generated conflict was hand-merged. Compile passed,
Stale had 0 firm findings (187 soft). The first compile found six references from HL1's GdpCheck
to HA1's deleted MoneyCheck; these now call LongPlaytest.MoneyFixtures.

| file | main's work met by HL1 |
|---|---|
| Game | R1's equipment/recipe application and the 1900 founding, R2's research/fitting and public patent bill, FB3's draw closure and saved load seams, AQ1's operating-loss streak and FB4's fund transfer are retained. The household opening and closing stay beside the pool window; upkeep is sold only after treasury payment and the bank's operating payment. Public patent licences remain their own treasury payment, not a household or upkeep receipt. |
| MoneyAudit | LF1b independently added the household/world share pairs: main's `shares` labels are kept once, replacing HL1's duplicate `equity` pairs. LF1c's world's shares wiped and short closed for nothing retain their valuation legs, with their noncash cash-offset legs Scope.BOOK, as HL1 already does for the bank's ownership wipe and bond write-offs. GD4's GoldMinted remains MONEY. R2c's private IP charges and R2c-P's `- city IP charges` remain TRADE, beside the household identity, never classified as household spending. |
| EconomyManager | Main's long branch counts, BF1's debt rounding and retirement recovery, public IP cost and national-account/load lines, and GD4's saved valuables stay. HL1 excludes only the bank's upkeep input from GDP imports; the five services' upkeep and public IP imports remain in the accounts. |
| Bank | BF1's project/leverage capital headroom deducts unsettled losses. HL1's seized savings still pass through the existing household repayment channel, and only the uncovered debt is written off. |
| BondMarket | BS1's issue fixed point and price ticks, BF1's founding-unit face dust and capital-constrained bank bids stay. HL1's orphaned household face measurement observes those payments without changing bidding or holdings. |
| Household | Main had no direct changes since the merge base. HL1 keeps savings above the debt at discharge; the seizure remains a repayment and scales with money. |
| HouseholdBalance | BF1's founding-money MIN_MOVE and Debt.moved paper retirement remain. HL1's arrival, census and unsettled-flow counters measure the existing settlement path and scale with it. |
| Sector | Main's long building counts, tied staffing refusals, IP purchases, CPB1 captive/feed and buffer costing, BF1 warehouse losses/recovery, GD4 mint sales and AQ1 retirement hooks stay. HL1's upkeep is a distinct pending revenue and imported-service input, not a second goods sale. |
| SectorState | Main had no direct semantic change here. The pending ledger's upkeepSold remains keyed by name, written and restored alongside all main ledger fields; absent keys read zero. |
| sectors/BusinessServices | TQ1's tied staffing holds and AQ1's `bleedsWhileLent()` stay. Upkeep revenue and its two imported-input lines remain separate from its export goods and its operating-loss rule. |
| ui/GovernmentScreen | Main's parked scopes, sizing calls, wealth/paper distinction, public Patent licences expense, formatted growth rates and GD4 investment-valuables row stay. HL1 changes only the explanation of the bank import exclusion. |

HA1 moved MoneyCheck into LongPlaytest.MoneyFixtures; its complete main fixture body was three-way
merged with HL1's MoneyCheck changes, retaining R1f's current town and every HL1 assertion (old
metal, upkeep/save/load, identity, stolen cent, orphaned bond face, payslip carriers). MoneyCheck
stays deleted and no registry is reverted. Main's KS1 ensureWorks already holds the sawmill through
a counted month with try/finally, superseding HL1's old fixture; FB12's independent kept section is
left alone. Main's QB1 ScaleCheck fixture month 225 replaces HL1's obsolete month 165 and its old
scan narrative. These two decisions keep main's repaired fixtures; fresh checks decide whether the
new path still causes their premises. Orchestrator notified before resolving KeptCheck.

? Keep LF1b's established share labels and BOOK-classify LF1c's noncash offset legs: a noncash
valuation is not income from or spending by a household. All real new money flows remain in the
pool audit, either household DOMESTIC flows or the named TRADE/MONEY/FINANCIAL lanes beside them.
No save-format bump: the upkeep pending field is additive and missing means no previously booked
upkeep. DataSave and HistorySave still carry SectorState through the ordinary load path; save and
read checks are required again below.

### Checks on f56f6f5ac (before HN1/LM1-D)

`./batch-check.sh --fast`: exit 0, compile 49 s, regenerated indexes, 0 firm Stale findings
(187 soft). `sh tools/pick.sh origin/main HEAD`: ALL, 221 harnesses; review needed for money
(Bank, BondMarket). No full pick was run: the scheduled dry run is that verdict.

`./batch-check.sh GdpCheck`: exit 0, 14 s suite. The bank paid 14.75 in both fixtures: with no
Business Services seats its import is 14.75 (whole-sector imports 208.25, national accounts 193.49,
GDP 185.05 versus 170.30 counting the bank import); with 600 seats the import is 11.07 (whole
265.87, accounts 254.80, GDP 259.93 versus 248.87). The five services' line is 17.70 without seats,
13.28 with the office. These replace the old comparison where figures changed, keeping the same
claim and tolerances. Logs: target/HL1-GdpCheck.txt and target/checks/GdpCheck.log.

Named calls use one name per batch-check invocation and `-Dcbs.parallel=1` explicitly: AllChecks
matches substrings, so BankCheck also selects CentralBankCheck and YoungBankCheck. The first Bank
invocation exposed that matcher; subsequent calls serialize its selected harnesses as well.

The integration now has two additional caused assertions: PublicLicenceCheck.payment() requires
the household identity to close in its month of a positive public patent payment; ShortSellingCheck
theWipe() requires it to close through LF1c's positive share wipe and short closure. They use the
same closure predicate as LongPlaytest's money fixtures, alongside the existing pool/BOP assertions.
These cover new main flows at the seam rather than relying on a default city to exercise them.

`./batch-check.sh BankCheck`: exit 3; 3 substring-matched harnesses, 785 s. BankCheck and
CentralBankCheck passed. YoungBankCheck's only red is main's recorded `the bank is never in
resolution`; no new red. The two historical CentralBankCheck reds do not recur: the current
harness compares 24 shapes (its perturbation fixture holds 23/24 against 18 required), and the
probe's Very Loose trust is .680 against Standard .879. Mean inflation is 3.529% Very Strict,
3.895% Standard, 4.737% Very Loose. The discharge fixtures end 192.42k of debt: with half-debt
savings, 32.70k repaid and 159.72k written off; with twice-debt savings, 192.42k repaid and nothing
written off, with 7,783k savings still theirs. All repayment/bank channel assertions passed.

`./batch-check.sh ScaleCheck`: suite passed with main's current QB1 fixture at month 225; the
obsolete HL1 month-165 fixture and its narrow 7/36 scan are not restored. No dial or premise moved.
Full wrapper exits and remaining named results follow below.

ScaleCheck's current six-month K=1/K=1000 tracking maxima are: people 6.64e-5 (dial .015),
cash .00370 (.0125), savings .00678 (.016), GDP .0564 (.14), deposits .00544 (.026), prices
6.44e-5 (.004), unemployment .000464 (.033), policy zero (.0005). The registers close to 2.5e-13
of their issue. Its new 1900 section passes too. Wrapper exit 0.

`./batch-check.sh AuctionCheck`: exit 0. `./batch-check.sh DebtExchangeCheck`: exit 0.
DebtExchangeCheck now tops the dollar paper up by US$3,539.69k; the caused 10% fall leaves a
209.96k cash surplus beside the -424.74k balance (-634.38k exchange loss). Four one-point market
steps put the term bond at 8.56%, its below-book buyback 2,543.58k for a 35.82k gain; cash surplus
171.91k, balance 207.73k. No tolerance or premise changed from HL1's already reviewed fixtures.

AutoBuildCheck's first pass had one NEW assertion: `the first warranted school is essential and
ordered without a payback veto`. Main's HW1 changed this case from a payback refusal to an
essential-school order; unlike its already-repaid positive twin, the first-school town still kept
its central-bank advances. HL1's imported upkeep leaves that town over the debt/GDP limit. The
mend uses the same existing repaid() fixture operation (cash repays advances, within-limit asserted)
for the newly positive first-school case, prints its original ratio, and leaves the explicit
zero/over-limit cases and the model untouched. Budget, children, staffing and all claim/tolerances
stay; the rerun must prove the order, not just the within-limit precondition.

`./batch-check.sh SaveFileCheck` and `./batch-check.sh ReadPathCheck`: exit 0 each on the merged
model (the latter compiled the first-school fixture mend). `./batch-check.sh PublicLicenceCheck`:
exit 0, including the new household closure assertion. Its caused water-works licence is
3.338775576, pending then paid at the next strike, alongside the named `- city IP charges` audit
line, the accounts' import and expense, history, save/load and load-path readers. The current
standing stack's next pending bill is 3.040792804; the hydrogen works' test bill is 1.013918045.

`./batch-check.sh ShortSellingCheck`: exit 0. The caused LF1c month is m53: valuation in 17.36,
valuation out 34.73; household held 113.87 -> 114.83, to households 6.89, from households 4.03,
own -1.89, residual zero at display precision. Both valuations exceed the audit cent, the net is
against the city, and the new household assertion passes beside the pool and BOP assertions.

The temporary named-check loop was edited while active (adding serialization and later names),
which caused shell parse errors after the completed bank call and at the end of the original
nine-name loop. Each completed harness verdict above is from its saved batch-check output; these
errors did not change a harness. Subsequent calls use unchanged one-name wrappers. The two late
names HealthCheck/KeptCheck did not run in that loop and are still required, as is the mended
AutoBuildCheck rerun and LongPlaytest. No such wrapper is tracked or shipped.

AutoBuildCheck rerun after the school fixture mend: **exit 0**, all assertions including the
first essential school order and both debt-limit refusal cases pass. No model or tolerance changed.

### HN1 merged: f17ff2141 (0.9.151)

HN1's Game.considerOnMortgage() retries the door's refused quantity at halves down to one;
BusinessDebtManager.takeBackCapitalRefusal() restores only the reporting marks that this try set.
The source merge is clean; generated conflicts were main's temporarily, then regenerated.

The money still follows Game.mortgageInvestor() -> sectorInvestor()/buildFor(): the down payment
is the landlord's own sector till, the mortgage is its liability and the bank's loan asset. The
borrower receives principal less the existing fee; the insurance premium reaches the treasury;
the build pays the construction order book and land reaches the treasury. A failed door test
changes none of that money; only the accepted smaller order borrows and spends. An outright
trimmed purchase uses its till and creates no mortgage. Funding, repayments, fees, premiums and
insured claims are pool-to-pool, as MoneyAudit's existing "SO DO THE LANDLORDS' INSURED MORTGAGES"
paragraph specifies; they must not be labelled household savings income or spending merely
because households own shares in the landlord. Households' own rent, wages, share purchases,
dividends and consumer loans retain their existing DOMESTIC legs and transit carriers. Thus HN1
adds no new money lane or own plug: it changes amounts and timing, which the unchanged pool and
household identities must close on the new path. No saved field changed in this retry/reporting
mend. HousingDoorCheck and the final default/ensemble will verify the combined model.

The essential-school diagnostic before repayment is 285.3% debt/GDP against 240%; after repayment
it is within the unchanged limit and the school is ordered. This confirms the fixture diagnosis.

`--fast` after HN1 merge: exit 0, compile 41 s, indexes regenerated, 0 firm Stale findings
(187 soft). HN1 supplies the new current ensemble baseline docs/ensemble/0.9.151.md; the final
HL1 ensemble must compare with the newest baseline after LM1-D, not the old 0.9.93 record.

HousingDoorCheck on the HN1 merge, watched m431-500: the added household assertion passes in all
499 played months (worst relative month 143). It has four real trims, all down-payment refusals
(m435, 436, 439, 493), two whole-order holds (m440, 443), no stranded landlord that could buy one,
no household with no door, and 160 studio/819 family homes finished. Its one NEW red is the
required fixture `the door trimmed an order it first refused for the bank's capital`: that event
no longer fires in the old 70-month window on HL1's path. The next run retains all those months
and extends the watched period to m900, retaining the capital-trim requirement, halving rule,
refusal-mark assertion, no-door watch and every monthly household closure check. No model dial
or tolerance moved. The old result is target/HL1-HousingDoorCheck-500.log.

HousingDoorCheck through m900: **exit 0**. 151 watched months with an order at the door, 22 trims
(all valid halvings), 18 whole-order holds, eight trims first refused for capital, none left marked
held. The first capital trim is m502, only two months past the old endpoint: the till 2,421.1 buys
one at 2,205.7 outright after the plan passed two, from seven asked. So the missing fixture was a
window, not a broken retry. No month strands a landlord whose till buys one. Homes finished:
880 studio, 6,426 size-4 family, four size-6 family; no household past the housing valves. Every
one of the 899 played months closes the household identity; worst relative month 614, held
315,105.93 -> 316,642.36, to 9,184.96, from 7,649.15, own .63, residual zero at displayed precision.
The window keeps all former months and eight actual capital events for margin, not one lucky
month; no requirement or tolerance is dropped.

`./batch-check.sh HealthCheck` on the HN1 merge: exit 3, 20 s suite, only the recorded main red
`...and the households who skipped a bill ate with it: over the last year the dear city is no
hungrier than the free one by more than the price of care`. The old HL1 burial red does not
recur: deaths/year/1,000 are 10.27 at fee x1 and 14.33 at x15, old deaths 54.75 against 88.00;
both "buries more" and "its old first" pass. No new red.
