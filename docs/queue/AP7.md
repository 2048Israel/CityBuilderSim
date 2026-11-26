# AP7 — slot 9 at 50x after AP6: the maintenance valuation's repeated eligibility reads

Queued by AP6 (2026-11-26), measured, not built. AP6's brief says to profile once more and queue
the next cause if 50x is still below 10 months a second. Its adjacent pair on the slot-9 copy
(dojha, m4866, 21,918,046 people) is Build 3.7 → 6.7, Land 3.9 → 6.1, People 3.6 → 8.1,
Services 4.8 → 7.9, Sectors 4.8 → 9.7; the other AP6 screens run 9.8–10.0 months a second.
Every frame p95 still exceeds its `FRAME_P95`. Headless mean 293.4 → 271.3 ms a month.
These are paired, shared-laptop figures, not comparable to AP5's earlier absolute speeds.

## The measured next cause

AP6's final 50x JFR: 3,696 FX ExecutionSamples over 66.56 s; no missing or truncated FX stacks.
`AutoBuilder.pass` 35.50%, `WorksValue.relief` 27.68%, `BuildAdvice.supplyDemand` 13.45%.
Within relief: `BuildAdvice.warranted` 357 samples (9.66% of all FX), the direct `count` call
276 (7.47%), `BuildingManager.getKnownTemplates` 216 (5.84%). Inclusive figures overlap.
The horizon and count searches repeatedly ask warranted/current eligibility, which has no
projected date or added-buildings argument; the known-template scan repeatedly asks research.
The whole thread's `getKnownTemplates` is 258 samples (6.98%), chiefly `ResearchCatalog.knownFrom`.

## What to build

Measure first and after as AP6 did. Trace which eligibility answers are fixed inside one
unchanged valuation/selection scope, starting with `warranted`; read those once, invalidating
at every real input change. Keep projected supply/demand, quote state, count search order and
floating arithmetic exact. Expected model files: `WorksValue.java`, `BuildAdvice.java`;
proofs through `AutoBuildCheck` / `BuildAdviceCheck` and warmed writer-by-writer raw-bit checks,
each notification/input omission shown red. Do not add a broad catalogue cache without evidence.
Require the default playtest's eight MD5s to match main; no ensemble for an unchanged trace.

Evidence/drivers: `C:/Users/Jerus/AppData/Local/Temp/cbs-AP6-evidence/`, especially `after-profile/`
and `reproduce/`; full figures in `docs/queue/AP6-notes.md`. Use a compatible slot copy in an own
scratch folder, `GameFiles(dir, legacy)`, explicit Earth/world paths and `-Xmx4g`; preserve save
format guards and never write the player's slots. No AP7 implementation is included in AP6.
