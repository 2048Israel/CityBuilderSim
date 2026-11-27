# AP8 — slot 9 at 50x after AP7: the households' bond orders, the build quote's abroad terms, private investment

Queued by AP7 (2026-11-26), measured, not built. AP7's brief says to profile once more and queue the next cause if 50x is
still below 10 months a second. On the slot-9 copy (dojha, m4866, 21,918,046 people) AP7's two adjacent pairs give
Build 7.4 → 8.7 and 6.1 → 9.0, Land 7.3 → 7.7 and 7.4 → 7.9 months a second; the other screens run 7.2-10.0 on a
laptop under other sessions' load (the second main run was slow on every screen). Every frame p95 still exceeds its
`FRAME_P95`. Headless median 280.1 → 263.2 and 298.1 → 263.4 ms a month. Figures in `docs/queue/AP7-notes.md`.

## The measured next causes

AP7's final 50x JFR: 3,712 FX ExecutionSamples over 66.68 s, none truncated (AP6's reader, `-Xmx256m`, one processor).
Inclusive, overlapping: `AutoBuilder.pass` 28.02%, `WorksValue.relief` 19.91%, `BondMarket.takeMonth` 16.33%
(`postHouseholds` 15.11%, `OrderBook.submit` 11.34% from `BondMarket.submit`, `OrderBook.rest` 4.45%),
`Game.runPrivateInvestment` 11.67% (`ProcessingSector.plan` 4.18%, `EquipmentFit.priced` 4.07%),
`HouseholdBalance.followThePeople` 9.16%, `Game.quoteBuild` 7.57%. In `quoteBuild` the order's abroad terms are read
twice for the same arguments, once inside `orderPriceMultiplier` (129 samples) and once directly (95): `abroadOfOrder`
is 224 samples (6.03%), `foreignTerms` and `pricePerPointWith` below it. Within relief, `quoteBuild` for each horizon
date's extra units is 130 samples (3.50%), `outputRestored` 223 (6.01%). AP7's `warranted` is 90 (2.43%), one read
per search or valuation.

## What to build

Measure first and after as AP6 and AP7 did (their drivers and `measure.sh` in the kept AP7 evidence). Start with the
largest exact cause: the households' order posting into the bond book, then the quote's twice-read abroad terms. Keep
the book's matching order, every money figure and the floating arithmetic exact; any held figure gets AP5/AP6's
warmed writer-by-writer raw-bit proof, each invalidation removed shown red. Expected files: `BondMarket.java`,
`OrderBook.java`, `BuildingManager.java`, `Game.java` (quoteBuild only); proofs through `BondCheck`, `OrderBookCheck`,
`BuildAdviceCheck`. Require the default playtest's eight md5s to match main; no ensemble for an unchanged trace.

Evidence: `C:/Users/Jerus/AppData/Local/Temp/cbs-AP7-evidence/` (`after-profile/`, `measure.sh`, `trace.sh`, the
slot copy in `scratchpad/original`); load through `GameFiles(dir, legacy)`, explicit Earth/world paths, `-Xmx4g`;
never the player's slots. No AP8 implementation is included in AP7.
