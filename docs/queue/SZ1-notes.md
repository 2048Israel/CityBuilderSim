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
overrides the earlier restriction: each named invocation runs alone,
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

The orchestrator's 18:56 mail confirms the narrow Game, KeptCheck and
LongPlaytest edits, frees LandCheck/MapCheck after LM1-D, and permits single
named harnesses while HL1's ensemble runs. Its accounting changes must ship
first; SZ1 must then merge them before its own ensemble. No own `--pick`,
ensemble or dry run is authorized yet.

Merged LM1-D main b3c4b614e (0.9.152) in 87d97d7e1. Source merged without
conflict; the four conflicts were generated indexes, regenerated from the
merged source. `--fast` passed (47s compile, zero firm Stale). Then merged
the current queue-only main 608bf7025. The land office retains its previous
disabled buttons at one offer and at the listed maximum with the shared
control. WorksFinanceCheck passes on that merged model, including rounded
excess proceeds and the independent advice oracle
(`target/SZ1-WorksFinanceCheck.txt`).

NewGameCheck and LandCheck pass on the merged model. The `MapCheck` name
filter also selected BridgeMapCheck; both pass. After noticing that filter
overlap, remaining calls explicitly pin `-Dcbs.parallel=1` as well as using
one CLI name at a time. No source premise was changed for these checks.

KeptCheck finishes with exit 3: exactly the four main-recorded KFP1 reds
in section 63, all concerning the let-go fund rule settling zero new
holdings and the null holding page watched afterwards. The changed building
step presses, in-place bar refresh, reset and kept/new equality pass. These
are holding lots, not issue-size granules. The existing known-red record
describes the uncaused fixture; FS1 now folds in KFP1, so the ship file will
name FS1 for the four assertions. Logs: `target/SZ1-KeptCheck.txt` and
`target/SZ1-KeptCheck.log`.

The live-price binding case was strengthened beyond synthetic lot inputs:
a separate scratch Game's PriceIndex is fed uniform prices and shopping for
PriceIndex.SETTLING_MONTHS, then all its component prices rise tenfold with
the issue-size cap strictly inactive. All five live lots rise tenfold.
During development this caught a local-name collision, then a fixture that
had not based its basket (the index is a placeholder 1 during settling).
The fixture now causes basing through the model's own dial; its tenfold
expectation is unchanged. StepsCheck passes this stronger case, the real
rollover and the actual UI presses with serial execution pinned.

The proceeds (i) now also distinguishes a bill's face rounded up to cover
cash and costs from an auction sold short; it no longer attributes an
underfilled sale to rounding.

## The changed city found a lost carried flow

AutoBuildCheck's first run reported three NEW assertions. Two were its
quote/book comparisons: its predictions used AutoBuilder.bondFor's live
lot, but four explicit fixture bookings still passed BUILD_BOND_GRANULE.
Those bookings now use the same city's live Term lot. Requested borrowing,
debt limits and the predictions' arithmetic remain unchanged.

The third was genuine: the unchanged saved/live 36-month comparison first
differed at m601. Snapshots and before/after-builder observations isolated
General care: the live city ordered a Walk-in Clinic, the loaded city did
not. Before that pass both had identical CPI, denomination, tax income,
lot, cash, debt and saved builder plans. Healthcare carried served counts
but not offered counts; getPricedOut() (Healthcare.java:674 before this
fix) therefore read zero after loading, and the builder credited the loaded
city with capacity the live city's fee had made inaccessible. The live
city had 0x1.224d873b735b8p8 general-care people priced out. Copying only
its offered[] into the loaded twin as a diagnostic made all 36 subsequent
months agree exactly, worst difference 0.0. Diagnostic log:
`target/SZ1-AutoBuildCheck-access-probe.txt`; diagnostic prints, reflective
copy, snapshot and phase probes were removed afterwards.

★ Save the access flow, rather than reconstruct it from the end state.
Healthcare now exports/restores offered and affordable figures for the
three living kinds; DataSave carries a separate optional healthcareAccess
key, copied at capture, and the existing Game healthcare save/load sites
carry it. The established healthcare array has no new shape or meaning,
so there is no conversion and no SAVE_FORMAT bump. An older save lacks
this new flow and stays incomplete. No saved order keeps a lot. These are
last-month readings, not a historical series; HistorySave gains no chart.
The orchestrator was mailed before the two additional reserved Game hooks.

AutoBuildCheck now asserts that its unchanged city actually priced people
out before saving, and checks the offered, priced-out and affordability
figures of all three kinds immediately after loading, before retaining its
original 36-month city comparison. SaveFileCheck also compares those flows
in its existing complete city's save/load case. This fix is SZ1's finding
to mend, not an unrelated FS1 waiver.
