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

LM1-D's gate was in its suite phase at startup; only `--fast` is allowed
while that heavy run holds the machine. Named harnesses wait for the slot.

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
cities use GameFiles.scratch. Its mutation check is still to run once the
heavy slot is free. No eye check is needed to establish the rule; visually,
check that four compact count buttons and the count fit on a building card.

`--fast` has passed twice during implementation (52s and 48s compilation),
with zero firm Stale findings. These are compilation/map checks, not claims
that the new harness or the ensemble has run.
