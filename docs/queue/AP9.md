# AP9 — slot 9 at 50x after AP8: private investment, the households following their people, the bond clearing

Queued by AP8 (2026-11-27), measured, not built. AP8's brief says to profile once more and queue the next cause if 50x
is still below 10 months a second. On the slot-9 copy (dojha, m4866, 21,918,046 people) AP8's two pairs give Build
9.2 → 7.1 and 9.3 → 9.3, Land 9.4 → 5.8 and 8.0 → 9.6 months a second, the middle two runs under another session's
harness and an open game; every frame p95 still exceeds its `FRAME_P95`. Headless mean 270.2 → 263.7
and 248.7 → 234.5 ms a month. Figures in `docs/queue/AP8-notes.md`, sections 5 and 6.

## The measured next causes

AP8's final 50x JFR: 3,307 FX ExecutionSamples over 66.4 s, none truncated (AP6's reader, `-Xmx256m`, one processor).
Inclusive, overlapping: `AutoBuilder.pass` 25.58%, `WorksValue.relief` 19.02%, `BondMarket.takeMonth` 15.00%
(`postHouseholds` 13.85%, `OrderBook.submit` 11.40%), `Game.runPrivateInvestment` 13.82%,
`HouseholdBalance.followThePeople` 10.61%.

- Private investment: `NewUnit.standingWhile` 5.50%, of it `EquipmentFit.priced` → `pricedAsStanding` 5.26% and
  `EquipmentFit.chosenForNew` 5.23%: a standing unit's equipment priced and chosen afresh for each decision.
- `followThePeople`: `carryTheFlows` 6.71%, `moveStock` 2.57%; `Household.bondFace` 4.84% beneath them, of which
  `Integer.valueOf` 4.39%: `bondFace` is a `TreeMap<Integer, Double>` read with a boxed bond id every time.
- The bond clearing under `OrderBook.submit`: `BondMarket$Settle.settle` 3.36%, `Settle.capacity` 2.54%, with
  `BondMarket.cellOf` → `HouseholdBalance.cellByKey` (a `HashMap` on the cell's key string) 1.78%.

## What to build

Measure first and after as AP6-AP8 did (their drivers, `measure.sh` and `trace.sh` in the kept AP8 evidence). Start
with the largest exact cause. Keep every money figure, every walk's order and the floating arithmetic exact (a
`TreeMap` walk is by id: a replacement must walk the same order and fold in the same order); any held figure gets
AP5-AP8's warmed writer-by-writer raw-bit proof, each invalidation removed shown red. Expected files: `Game.java`
(`runPrivateInvestment` only), `EquipmentFit.java`, `NewUnit.java`, `HouseholdBalance.java`, `Household.java`,
`BondMarket.java`; proofs through the harnesses that read them (`tools/pick.sh`), `BondCheck` 9b and 9c and AP7's
warrant probe staying exact. Require the default playtest's eight md5s to match main; no ensemble for an unchanged
trace.

Evidence: `C:/Users/Jerus/AppData/Local/Temp/cbs-AP8-evidence/` (`ap8-profile/`, `measure.sh`, `trace.sh`,
`drivers/`, the slot copy in `scratchpad/original`); load through `GameFiles(dir, legacy)`, explicit Earth/world
paths, `-Xmx4g`; never the player's slots. No AP9 implementation is included in AP8.
