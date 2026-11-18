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
