# SZ1 — steps and lots that scale

## Before code: who supplies a lot

The model supplies lots, so this batch moves the default trace and owes the
ensemble. On starting main 2e862ae69, `Game.rolloverQuote()` at 13269 supplies
`BUILD_NOTE_GRANULE` or `BUILD_BOND_GRANULE`; `issueForRollover()` at 13364
supplies the same granule when booking foreign paper. `AutoBuilder` supplies
`Game.BUILD_BOND_GRANULE` when quoting at 738 and when booking at 1594, 1730
and 2329. CD2's cash solver takes its caller's granule (`Game.solveForCash()`,
11849–11850); it does not select one itself. `Game` also quotes its building
note at 6046 using `BUILD_NOTE_GRANULE`. The Finances screen's five fixed lots
are separate literals (212–239), not the model's only lot suppliers.

No saved order keeps an issue lot: `Rollover.Issue` describes type, term,
currency and cash; the sale obtains its granule when quoting/booking.
`BondMarket.State` saves bonds and order-book prices/quantities, not issue
granules. `DataSave`, `HistorySave` and `TreasuryFund` contain no saved issue
lot or rounding field. No SAVE_FORMAT bump is needed.

★ Take the brief's recommended cap: round down to 1–2–5 and never exceed
the minimum issue. Put each instrument's real-price lot dial in BondMarket,
away from HL1's accounting changes. Mail the orchestrator before touching
Game, and merge HL1's main before the final model checks/ensemble.

## Checks and coordination

LM1-D's gate was in its suite phase at startup. Initially I held named
harnesses under the contract's `--fast`-only restriction. ★ The launch
brief's final machine paragraph explicitly allows `--fast` and named
harnesses one at a time alongside gates. That later direct instruction
overrides the earlier restriction: each named check below runs alone,
one name per batch-check call. The orchestrator was mailed this reading
before the first named check. Ensemble/dry-run placement still needs its go.

## What changed and the calls taken

`MagnitudeSteps.of()` owns the pure rule: the leading unit, then its tenth,
neither under the caller's least, with duplicates removed. At zero there is
one least step. Building orders, paving and the land office use shared
`Pieces.CountSteps`; presses refresh their labels and count in place before
repricing. Finances' `askSteps` delegates to the same rule. The fund's cash
transfer and amount/face/share tickets use it too. The UI sweep also found
the reserves exchange's cash increments, which now use this rule; its cover
presets and percentage allocations remain presets and percentages.

★ Keep the fund's existing least money step (1,000 model thousands) and the
count's least of one. The exchange uses one model thousand, bounded by what
it can exchange. These least steps have not become inflation dials: the
brief prices bond lots, and asks magnitude controls to retain their own least.

★ Round lots down, rather than to the nearest 1–2–5 value. Applying the
minimum-issue cap before rounding guarantees the cap, including just below
a decade. Reference-price dials live together in BondMarket; the local lot
uses PriceLevel.FACTOR times the current CPI ratio, divided by the currency
unit. A foreign lot converts that price level at the month's cross rate and
uses that money's minimum issue before rounding. Currency reforms therefore
change the unit once, not the CPI ratio as well.

★ All live funding uses those same instrument lots: rollover quotes and
foreign bookings, automatic construction, opening-city funding previews,
Build, the land office/map and the bank rescue offer. Game's two old
BUILD_*_GRANULE constants remain for explicit-granule quote fixtures; they
no longer choose a live sale's lot. The quoting APIs still accept a caller's
explicit rounding, as the instrument-pricing harnesses require.

★ Count-step labels use compact k/M/B/T/P/E suffixes so a large city's four
buttons fit on a card. Count additions saturate at Long.MAX_VALUE, and
subtraction stops at zero. The land office clamps to its listed offers and
paving to its paveable roads. KeptBench obtains the current small/large
button label; KeptCheck's two affected sections still press real buttons and
test repricing plus kept/new equality.

The new StepsCheck causes 1900, reference and hyperinflated levels with and
without an active cap for all five dials. It also presses the actual large
clinic-card and fund-transfer buttons on an offscreen JavaFX scene; its
cities use GameFiles.scratch. Its mutation check rejects fixed nominal lots
(results below). No eye check is needed to establish the rule; visually,
check that four compact count buttons and the count fit on a building card.

`--fast` has passed twice during implementation (52s and 48s compilation),
with zero firm Stale findings. These are compilation/map checks, not claims
that the new harness or the ensemble has run.

The live-funding expectations in AuctionCheck, WorksFinanceCheck (and its
independent AdviceValueOracle), LandCheck, MapCheck and TreasuryCheck now
read the actual instrument lot. Their fixture cities, requested cash,
thresholds and money assertions stay the same. In particular, AuctionCheck
still creates a bank room strictly between the capped and filled bids;
MapCheck still requires the card's quote to equal the booking path's quote,
and still asserts lot divisibility. TreasuryCheck captures the lot before
the month turns. These are repairs to the source of the asserted dial,
not relaxations of the harnesses' premises. The orchestrator was mailed
before the LandCheck/MapCheck and HL1-owned edits.

The first pick is ALL (AllChecks plus shared game classes), with stranger
review required for BondMarket and the bank/fund screens. No `--pick` suite
has been run; the orchestrator places its dry run after the ensemble.

LongPlaytest's player-side funding quotes and bookings also consume the live
lots now, including its opening-village, paving, land and preferred-offer
funding. Its requested cash, chosen terms, player policy and build orders
are unchanged; its synthetic paper fixtures retain their explicit granules.
This edit was mailed before touching HL1's LongPlaytest file. StepsCheck
also causes a one-month bill to mature and counts the real rollover sales,
comparing both its plan and actual booked face with the live-lot quote.

The latest complete `--fast` passed: compilation 50s, regenerated maps,
zero firm Stale findings. Source/prose diff whitespace checks pass and the
new Java files have LF line endings.

## Named checks (source 93da11821)

StepsCheck passes: the one-month maturity causes one real auction sale;
its plan and the booked face are both 134 at the live lot. Eight actual
large clinic-card presses carry 23,000 to 103,000 and refresh +10k to +100k;
both actual fund presses move the leading unit or its tenth at 3.4e9.

The old-lot mutation replaced only BondMarket.issueLot's repricing/cap with
`return referenceLot(kind)`, reproducing the old nominal lots for all five
kinds. StepsCheck fails **35 assertions**. The original source was restored
byte for byte, its diff checked empty, and StepsCheck passes again; the
generated maps also returned to a clean tree. Logs are
`target/SZ1-StepsCheck-green.log`, `target/SZ1-StepsCheck-fixed-lots.log` and
`target/SZ1-StepsCheck-restored.txt`. No mutation remains in the branch.

AuctionCheck passes, including the 1,200-month played city and all its
rollover-cap and open-funding-ask assertions (`target/SZ1-AuctionCheck.txt`,
`target/checks/AuctionCheck.log`). LM1-D finished and published while this
single named harness ran. The `Where MagnitudeSteps.of -print` navigation
check ran once the gate was done.
