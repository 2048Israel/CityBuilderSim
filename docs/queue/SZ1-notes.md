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

★ The model and player share the instrument dials, price level and cap.
After the EP1 finding below, model funding uses the exact priced-and-capped
lot; the player's pages round it down to 1-2-5. Rollover and automatic
construction and day-zero model quotes use fundingLot; Build, the land
office/map and the bank rescue offer use issueLot. Game's two old
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

The model-funding comparisons now read fundingLot; the UI comparisons read
issueLot. This distinction follows the EP1 finding below.

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

The orchestrator approved the two save/load hooks at 20:36. A save without
healthcareAccess loads offered=0 and affordable=1, the current constructor
defaults, exactly the incomplete readings it has today; no existing saved
number is reinterpreted. This is the standing old-saves rule's missing-key
case, so no SAVE_FORMAT change is warranted. Main's fixture did not reach
this care-order path; the new lots exposed it. AutoBuildCheck is now green
with no diagnostic flags: its access assertions and unchanged 36-month
comparison pass, worst 0.0, and all later cases pass too
(`target/SZ1-AutoBuildCheck.txt`, `target/SZ1-AutoBuildCheck.log`).

A final source sweep found four remaining explicit fixture quotes in
KeptBench's fundingRunShort/fundingRunCovered. These describe the production
funding page's offers, so they now use the same live Term/Note lots as that
page. Their shortBy/coverBy conditions and quantity search stay intact;
KeptCheck will be rerun after this correction. The fixed population figures
in PeopleScreen are milestone labels, not magnitude controls.

## EP1: exact model scaling and round player lots

PriceLevelCheck on 5eef1297a exposed a real design error in SZ1's initial
choice to round every caller's lot in nominal money. It finished with 710
failures, 31 NEW judge lines; most others were the existing HP1 class.
The played twin's nudge horizon was 120/120 (main's recorded HP1 horizon
was 105), but its factor/today cities first parted at **month 60**, before
that horizon. Cash was -511.862154616 normalized at the factor against
-354.064381968 today. Debt first appeared that month: 15,840.2292092
normalized against 16,000 today. Both cities had zero debt and equal cash
through month 59. The lot was 10 at the factor, or 386.347053882 normalized,
against 500 today. The first actual borrowing therefore took different
scaled faces: round125(factor * L) is not factor * round125(L).

The diagnostic retained the two children and printed their post-month
cash, debt, CPI and lots using pure reads. It reproduced the normal run's
710 failures. Logs: `target/SZ1-PriceLevelCheck-normal.log`,
`target/SZ1-PriceLevelCheck-probe.txt`, and the two
`target/SZ1-price-{factor,today}-twin-played.txt` children;
`target/SZ1-price-first.py` locates the first parting. The diagnostic
PriceLevelCheck changes were restored byte for byte, checked with an empty
diff. No tolerance, horizon or comparison in that harness changed.

★ Adopt the orchestrator's 20:49 recommendation and preserve EP1 in the
model: BondMarket.fundingLot is the exact dial times the current price
level, capped at the exact minimum issue. Rollover, the auto-builder and
LongPlaytest's funding driver use it, including foreign funding. The
player's BondMarket.issueLot rounds that same priced-and-capped amount down
to 1-2-5; Game.issueLot and every actual UI funding page retain it. REV-SZ1
P1 below corrects my initial classification of the opening-city scratch
quotes: their model API must also preserve exact scaling.
CD2's generic cash solver continues to use whichever lot its caller passes.
This keeps Jerus's visible round lots and ★1's cap while preserving the
standing price-level law in model sales. The model still changes the trace
from main's old granules, so its ensemble is still required.

StepsCheck now asserts both exact model pricing and the round player lot
against each instrument dial, the model cap and EP1 scaling, plus the
foreign conversion and the actual rollover booking. Model-funding
expectations in the existing harnesses use fundingLot; player-page
expectations continue to use issueLot. The causal care-access save fix
remains: it records real flows the builder and screens read after a load.
SaveFileCheck passed, including offered/priced-out/affordable readings for
all three living care kinds (`target/SZ1-SaveFileCheck.txt` and `.log`).

With exact model funding, the unchanged PriceLevelCheck finishes with known
reds only (exit 3): the played twins agree through all 120 months. Its 31
NEW lines are gone; 245 existing failures remain: HP1's reload.* and screen
cut-offs past the town's nudge-derived horizon, 73/120 on this run (the main
record names 20/120 on its earlier town), and HP2's unreached partner-lane
fixture. The default horizon is 60/60 and the built
city's 75/120. No new waiver is needed for SZ1's model fix. Log:
`target/SZ1-PriceLevelCheck-exact.txt`.

★ Guard floating-point boundaries in the shared rule. log10 can round a
figure just below a decade up to that decade, so the leading step is checked
against the figure. Likewise dividing a figure just below 5 * 1e-15 by its
decade can round to 5 and skip the valid 2 * 1e-15 lot. The 1-2-5 rule now
compares the actual candidate amounts. StepsCheck has both reproductions;
the fractional one uses exact equality so a generic absolute tolerance
cannot conceal it. These are arithmetic guards, not changed tolerances.

## Checks on the corrected funding model

StepsCheck, AutoBuildCheck, WorksFinanceCheck, AuctionCheck, TreasuryCheck,
BuildAdviceCheck, ReadPathCheck, SaveSlotCheck and SaveFileCheck all pass.
AuctionCheck includes its 1,200-month counted-auction run (355 assertions).
AutoBuildCheck retains its exact 36-month saved/live comparison. Logs are
`target/SZ1-<Check>-final.txt` and `target/SZ1-<Check>-final.log`.
SaveSlotCheck has one soft timing observation under the concurrent machine
load; its functional checks pass. Each call names one harness and pins
`-Dcbs.parallel=1`. No full pick, own ensemble or dry run was started.

HealthCheck finishes with exit 3 and its one main-recorded HP2 assertion:
the dear city is no hungrier than the free one by more than the price of
care. This is the existing food-shops path comparison (known-reds.txt:28),
not a new care-access save failure. Its other assertions pass. PriceLevelCheck
also has only the existing HP1/HP2 classes, as traced above; its complete
exact-model log is retained at `target/SZ1-PriceLevelCheck-exact.log`.
The full-run screen comparison has 4 of 28,798 series-months different past
its nudge horizon, and the partner-lane fixture sees zero partner lanes;
both assertion fragments are already in main's known-red record.

KeptCheck's final run completes with exit 3: exactly the same four KFP1
fund-settlement assertions named above, no NEW assertion. All changed
building presses, in-place repricing/reset and funding-page comparisons
pass. Log: `target/SZ1-KeptCheck-final.txt` and `.log`.

The final old-lot mutation replaces only the priced-and-capped funding lot
with its fixed nominal reference dial, affecting both model funding and the
player's lot. StepsCheck fails **70 assertions**. Restoring BondMarket byte
for byte leaves its source diff empty, and StepsCheck passes with **zero
failures**, including the two new floating-point boundary cases. Logs:
`target/SZ1-StepsCheck-final-fixed.txt`/`.log` and
`target/SZ1-StepsCheck-final-restored.txt`/`.log`. No diagnostic or mutation
remains, and no backup probe is left active.

The final explicit `--fast` passes (4s compile, regenerated maps, zero firm
Stale findings). The source tree has no uncommitted change; the new Java
files and notes are LF. Source/prose whitespace checks pass; generated
Markdown keeps the generator's own line-break spaces. A fresh fetch still
finds main at 608bf7025. No VERSION, SAVE_FORMAT, queue or publication-record
edit was made by SZ1.

This is the provisional review handoff. HL1 has not shipped yet. Its main
must be merged before SZ1's final-model ensemble; the 80-run ensemble and
the ALL-pick dry run require the orchestrator's separate machine-slot go.
Neither has been run by SZ1. The provisional ship marker stays until those
steps and the stranger's money/save review are accepted.

## REV-SZ1 P1 — day-zero quotes

The review found one P1 at Game.java:6045 and 6046 on ac652ab5a:
dayZeroQuotes passed the player's rounded issueLot to its two model offers.
My earlier classification as a player preview was wrong: this API must
preserve EP1 even though it has no current gameplay caller. ★ Both offers
now pass scratch.fundingLot. No other production behavior changes in this
mend. The method's comment now names its price-level regression.

PriceLevelCheck's existing default factor/today children exercise the
actual Game.dayZeroQuotes API at 3.4 times the minimum-issue floor dial.
They carry the opening city's minimum and floor, and the two actual
quotes' requested cash, face, cash received, monthly interest and total
cost. The parent first asserts that the minimum is the positive floor in
both cities and that both offers sell positive face and cash; then it
compares all ten money readings with today's quote times FACTOR, at the
existing CONSTANT_TOLERANCE, requiring finite values. The separate scratch
quotes run after the child's monthly readings are recorded, so they cannot
perturb the existing twin comparison. No old horizon, tolerance or premise
changes. The method's own GameFiles.scratch fixtures write no city slot.

The first exact-lot run passes all new assertions and finishes with only
the same 245 known HP1/HP2 failures. For example, both exact faces are
5.176692768595684 at the factor, against 200 today times FACTOR. Its log is
`target/SZ1-P1-PriceLevelCheck-first.log` (wrapper:
`target/SZ1-P1-PriceLevelCheck.txt`).

The recursive source audit of every issueLot call and method reference is
kept in `target/SZ1-P1-all-issueLot-calls.txt`. The model audit is:

- Game.dayZeroQuotes: the two reviewed calls above, now fundingLot.
- Game.rolloverQuote and issueForRollover: both already use
  BondMarket.fundingLot, including foreign paper (13271 and 13366 on
  ac652ab5a). Game.fundingLot delegates to that exact helper.
- AutoBuilder: its quote at 738 and bookings at 1594, 1730 and 2329 already
  use fundingLot. No issueLot call remains there.
- LongPlaytest's funding driver: all twelve calls already use fundingLot,
  local or foreign (3970, 5210, 5419, 5423, 5486, 6195, 6342, 6343, 6345,
  6365, 6593 and 6597). No issueLot call remains there.
- LandMap.card: its four calls at 2713, 2714, 2716 and 2717 are the player's
  map/land-office card offers, not automatic model sales. LandScreen's
  offers page consumes them (1983 and 2274); its actual button bookings use
  the same rounded player lot. These stay issueLot.
- Game.issueLot is the documented player facade and delegates to
  BondMarket.issueLot. BondMarket's two issueLot overloads are the player's
  rounding API definitions. Neither is an automatic sale caller.
- The other occurrences are the Bank/Build/Finances/Land screens,
  KeptBench's player presses and quotes, and LandCheck/MapCheck/StepsCheck
  fixtures for those player paths. StepsCheck's issueLot method reference
  tests the live player's price binding. They retain rounded lots.

No additional model caller was found. REV-SZ1's 21:50 mail reports no other
finding in money/EP1, screens, care-access saves or harness changes; no
second review is needed for this isolated dayZeroQuotes mend.

The P1 mutation put scratch.issueLot back in only those two offers. The new
case fails seven quote-money assertions: Term face/cash/interest/cost and
Note face/cash/cost. PriceLevelCheck rises from its 245 existing failures
to 252; the judge reports nine NEW lines including exit and tally. This
isolates the review finding, without changing the rest of model funding.
Logs: `target/SZ1-P1-rounded.txt` and `.log`.

The exact source was restored byte for byte and the backup removed. The
final PriceLevelCheck run passes all 16 new floor, positive-sale and
quote-money assertions and returns to the same 245 known HP1/HP2 failures,
exit 3, with no NEW line (`target/SZ1-P1-restored.txt` and `.log`).
The final explicit `--fast` passes with zero firm Stale findings
(`target/SZ1-P1-fast.txt`). Source/prose whitespace checks pass. No new
known-red waiver, saved state or SAVE_FORMAT change is part of P1.

Only dayZeroQuotes changes production behavior; the recursive audit finds
no current gameplay caller of it. P1 therefore adds no further default-trace
move. SZ1's existing model funding changes still require the final ensemble
after merging HL1, on the orchestrator's go. The provisional marker stays.

## After HL1 shipped — ensemble go

The orchestrator's 23:47 go gives SZ1 the exclusive heavy slot. Merged
origin/main 6b0aa202e (HL1, 0.9.154), including R6d. All Java source merges
cleanly: HL1's accounting and SZ1's exact funding lots, first offers and
care-access save hooks are retained. Nine conflicts were generated indexes
only; they were regenerated from the merged source. The first merged-model
`--fast` passes with zero firm Stale findings (`target/SZ1-HL1-fast.txt`).
The requested serial StepsCheck, AutoBuildCheck, PriceLevelCheck and default
LongPlaytest precede the ensemble. Its comparison is explicitly
`docs/ensemble/0.9.154.md`, HL1's committed 80-run baseline (23/26, hard
10/10 PASS), with the same Earth/world files. No pick or dry run runs beside
this ensemble.

The merged-model StepsCheck and AutoBuildCheck pass. The latter's unchanged
36-month saved/live comparison agrees exactly, worst 0.0, with 42 orders.
PriceLevelCheck has only its known HP1/HP2 classes (exit 3); all day-zero
checks pass. Its built/played/town nudge horizons are 75/118/96 of 120 on
this model. Logs: `target/SZ1-after-HL1-<Check>.txt` and `.log`.

Default LongPlaytest passes, including HL1's household identities. The
trace moves beyond runtime. Compared with HL1's kept gate output
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-HL1-oTsia5uX/checks/LongPlaytest.log`,
both strike 2,404 months, but the amount moved between pools and households
is 829,431,562,211 against HL1's 956,934,678,600. Mean monthly own-line ppm:

| reading | HL1 | SZ1 | unchanged bound |
|---|---:|---:|---:|
| Unfunded | 2,300 | 2,030 | 9,000 |
| UnsettledIncome | 1,560 | 1,570 | 5,000 |
| PublicWagesRestruck | 99.7 | 153 | 400 |

All nine bounds are unchanged. The comparison and all nine readings are in
`target/SZ1-HL1-default-comparison.json`; HL1's output is copied to
`target/SZ1-HL1-main-LongPlaytest.log`. SZ1's log is
`target/SZ1-after-HL1-LongPlaytest.log`, with eight CSV traces under
`target/SZ1-after-HL1-trace/run-*`. No main rerun was needed. The midnight
date change regenerated 602 documentation headers; no source changed
after the merge and these checks.

## Final ensemble on HL1 main — 2026-11-19

The authorized run completed **80/80**, no missing or short runs, in
**110.2 minutes**, exit 0. Its fixed classes snapshot is **3586daad6**,
compiled on merged main **6b0aa202e (0.9.154)**, label `SZ1`, five workers,
all sixteen seeds under AUTO2, ZERO_SLACK, ZERO_HOT, SHOCK and HIGH. The
Earth/world paths are the same as HL1's, explicitly recorded in the snapshot.
Only the ensemble ran after preflight; no pick or dry run overlapped it.
The orchestrator separately authorized RN1-H's one fresh main trace beside
it (00:52 mail), identified after a process-overlap check; no other process
was stopped by SZ1.

**SZ1: 24/26**, against **HL1: 23/26**. Both pass all **10/10 hard M1/F1
criteria**. All 80 runs have zero failed/unread money-audit rows and zero
runs with findings. H1 stays retired; zero vacuous, one retired criterion
is excluded from each score. The only changed verdict is ZERO_HOT H2,
**FAIL → PASS**, with 13/16 below. A1 and A2 remain informational failures.

The scored readings are paired below; criterion definitions, full info
rows, thresholds and the comparison summary are committed together in
`docs/ensemble/SZ1.md`, against `docs/ensemble/0.9.154.md`.

| policy / criterion | HL1 0.9.154 | SZ1 |
|---|---|---|
| AUTO2 A1 | FAIL — 3.09% [2.66% to 3.75%] | FAIL (4/16 in band) — 3.14% [2.70% to 3.51%] |
| AUTO2 A2 | FAIL — 23 [12 to 47] | FAIL — 26 [14 to 47] |
| AUTO2 A3 | PASS — 0 [0 to 3]; 100.00% [97.17% to 100.00%] | PASS — 0 [0 to 4]; 100.00% [98.09% to 100.00%] |
| ZERO_SLACK S1 | PASS — 2.79% [1.52% to 6.28%] | PASS (16/16 seeds within) — 3.16% [1.89% to 5.14%] |
| ZERO_HOT H2 | FAIL — -0.19% [-3.66% to 3.13%] | PASS (13/16 below) — -0.63% [-4.17% to 2.07%] |
| SHOCK K1 | PASS — 16.69% [10.75% to 20.06%] | PASS — 16.75% [11.72% to 23.73%] |
| SHOCK K2 | PASS — 1.97% [-0.45% to 4.24%]; 0.34% [-0.96% to 1.02%] | PASS — 1.97% [-1.35% to 3.49%]; -0.27% [-1.25% to 0.91%] |
| HIGH R1 | PASS — -3.95% [-5.61% to -1.94%] | PASS — -4.04% [-5.65% to -0.47%] |
| HIGH R2 | PASS — 1.52% [-1.00% to 6.54%] | PASS — 0.68% [-4.12% to 4.04%] |
| HIGH R3 | PASS — -4.82% [-6.62% to -1.54%] | PASS (1 above +1 pt) — -4.37% [-6.40% to 1.00%] |
| ALL T1 | PASS — 16/16 (m814 [506 to 1,501]) | PASS — 16/16 (m760 [490 to 1,233]) |
| AUTO2 T2 | PASS — 2,628,909 [1,115,743 to 6,587,878] | PASS — 2,381,553 [1,383,699 to 5,395,142] |
| AUTO2 M1 | PASS — 0 (0) | PASS — 0 (0) |
| AUTO2 F1 | PASS — 0/16 [] | PASS — 0/16 [] |
| ZERO_SLACK T2 | PASS — 1,563,127 [430,325 to 4,312,878] | PASS — 1,621,756 [1,011,424 to 3,539,292] |
| ZERO_SLACK M1 | PASS — 0 (0) | PASS — 0 (0) |
| ZERO_SLACK F1 | PASS — 0/16 [] | PASS — 0/16 [] |
| ZERO_HOT T2 | PASS — 7,622 [1,645 to 19,547] | PASS — 7,366 [1,509 to 14,074] |
| ZERO_HOT M1 | PASS — 0 (0) | PASS — 0 (0) |
| ZERO_HOT F1 | PASS — 0/16 [] | PASS — 0/16 [] |
| SHOCK T2 | PASS — 40,896 [27,485 to 279,392] | PASS — 40,465 [27,612 to 222,697] |
| SHOCK M1 | PASS — 0 (0) | PASS — 0 (0) |
| SHOCK F1 | PASS — 0/16 [] | PASS — 0/16 [] |
| HIGH T2 | PASS — 29,620 [11,584 to 114,818] | PASS — 27,653 [21,862 to 79,166] |
| HIGH M1 | PASS — 0 (0) | PASS — 0 (0) |
| HIGH F1 | PASS — 0/16 [] | PASS — 0/16 [] |

AUTO2's final population median is 9.41% below HL1's (2,381,553 against
2,628,909); its T2 still passes. Takeoff occurs in all 16 seeds, median
month 760 against 814. ★ Retain every informational result under the
contract's M1/F1-only ship rule; no threshold, horizon, seed, policy or
model parameter was changed to improve a score.

The baseline has the required `{next}` in its heading and introductory
version only; the paired score table, complete summary and data provenance
are intact. All raw CSVs/logs/times, the unmodified score and summary,
classes, dependency list and snapshot are retained under
`target/ensemble/SZ1/`; launcher `target/SZ1-ensemble.txt`. No source changed
after the snapshot. This completed ensemble stands for the dry run.

## Current-main records and dry-run handoff — 2026-11-19

Merged main **1041df81b** after the ensemble, in **8a0ca2cb4**. Its FS1
study and queue/known-red records change no source, tools, data or model
parameter; the ensemble's final model is unchanged. `--fast` passes,
zero firm stale findings and zero generated changes; log
`target/SZ1-FS1-records-fast.txt`. The fresh selection is **ALL, 225
harnesses**, against this main. Its money/save review line remains covered
by REV-SZ1 and the isolated P1 mend; no further review finding arrived.

★ Follow FS1's published successors for the same nine known-red rows:
KeptCheck's KFP1 class goes to **FS5**, PriceLevelCheck's HP1/HP2 classes to
**FS2**, HealthCheck's HP2 class to **FS11**. Main's registry already names
these exact prefixes and successor IDs. The ship file adds no red class;
its PriceLevel explanation now names this model's 96-month town horizon.
The provisional marker remains until the authorized dry run and ready go.

## AUTO2 population reading — 2026-11-19

For the orchestrator's report to Jerus: the **−9.41%** is the change in
AUTO2's final-population median, not a measured loss from one identified
event. The model path changed by SZ1 is financing granularity: rollover,
automatic works and the playtest's construction funding now use the exact
monthly-price lot, capped at the minimum issue, instead of fixed nominal
granules. The caller audit above names these paths. Different rounded faces
can leave different cash and securities after an auction, changing the
subsequent construction and investment path. The changed default trace's
pool/household totals establish that financing transactions differ.

I have **not isolated the cause of the negative population-median change**
between these paths; there is no counterfactual run attributing it to one
kind of funding, and I did not rerun or tune the final ensemble. The screen
step changes are not exercised by this headless ensemble. All sixteen AUTO2
cities take off, with median takeoff earlier (m760 against m814), and T2
passes. These readings describe the result; they do not establish a causal
explanation for its sign. ★ Report the observed change and this uncertainty
together, retaining the original informational verdict and thresholds.

## Kept final dry run — 2026-11-19

The authorized launcher completed at **03:07 local**, testing exactly:

- branch **2821bc80211e848b6f8c9374386ae4254a6c8d84**;
- main **1041df81bdc15e9406463294dcef9688e4343631**.

Retained run: `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-SZ1-G5odicQh/`.
Its `ship.log`, `checks/suite.txt`, `checks/*.log`, `checks.sha256` and
`checks.complete` are intact. The local launcher transcript is
`target/SZ1-dry.txt`. The launcher command had a **14,400-second timeout**;
foreground nine-minute waits continued until the explicit `gate exit`.
Nothing was pushed while it ran.

The static pick was ALL, 225 harnesses. The full runner reports **224
harnesses**, **211 passed, 13 with reds**, in **2,866 seconds**; the outer
check took 2,959 seconds. Compile and index generation pass; **zero firm
stale findings** (187 soft). The existing runner policy reports 11 soft
timing assertions and eight observed path assertions. StepsCheck has zero
failures; AutoBuildCheck, SaveFileCheck, WorksFinanceCheck, LandCheck,
ReplayCheck, LongPlaytest, CentralBankCheck and PlanCheck pass.

The initially registered red harnesses were BuildAdviceCheck,
ShortSellingCheck, PriceLevelCheck, PopulationCheck, EducationCheck,
HealthCheck, ScaleCheck, KeptCheck, DeathRecordCheck, DistressBleedCheck,
CopperRefiningCheck and YoungBankCheck. MapCheck also has its existing
HL1-M finding. KeptCheck's watched holding is now B:2732, with **zero months
left**, against the required three; the existing FS5 row's explanation now
records that actual condition rather than the earlier null holding.

The raw dry run stopped with exit 1 (suite exit 4) on **two additional
MapCheck assertions**, plus their exit/tally summaries. ★ Name these moved
played-city fixtures under **FS6**, as the orchestrator's dry-run go
instructed; the two assertion prefixes are recorded separately in the ship
file. `MapCheck.reserveScenario`, lines 3208–3218, copies the played city's
land for this scenario. Lines 3309–3312 demand a reserve absent the month
before, and every final reserve plot first dated in the chosen month.
SZ1 changes only this harness's later land-card quote checks; the reserve
scenario and map/journal machinery are unchanged.

Here the ground is handed in m133, the chosen reserve month is m137, and
**120 reserve plots already exist before it**. The final reserve has 324
plots, 234 dated at m137, so the fixture's none-before/all-new conditions
are not met. Its operational checks all pass: exactly **105** obstructing
boxes move (the undated control moves 197), zero later boxes stand on the
reserve, zero move again, **zero replay differences and zero dropped
records**, exact sidecar round-trip, and fresh/cached reserve equality.
This is the played-land premise moving, with the journal replay preserving
the actual history; FS6 must cause a wholly new reserve for that assertion.

After naming the two conditions, copied only the retained `checks/suite.txt`
into this worktree's `target/checks/` and ran the common saved-log judge
with retagging. **Exit 3: known red only, 13 harnesses; `new-reds.txt` is
empty.** Log: `target/SZ1-dry-rejudge.txt`. The kept run itself was never
changed or executed in. All **840 SHA-256 records** in its manifest verify,
and the local suite copy remains byte-identical. Its completed checks can
be reused with these newly named rows, including the original exit-4
suite, as `tools/gate-reuse.sh` specifies.

Only these notes and the ship file change after the dry run. No model,
harness or other source changed after ensemble snapshot **3586daad6**.
The ship file's retained-run comment is its first line; the final ready
commit removes only the provisional marker.
