# BB1 - the buyback card shows what the auction would book; offers capped at the call price ({next}, 2026-11-09)

The brief: `docs/queue/BB1.md` (CD6-C ★1, CD4-C ★6). Branch `batch/BB1` from main 06125149.

Compaction window: `$CLAUDE_CODE_AUTO_COMPACT_WINDOW` = 300000 as this side session's shell sees it.

## The plan

- A, the model: `Buyback.Line` gains `callPer100` (NaN when the line may not be called now; a ten-argument
  constructor keeps every old caller), every offer capped at it; `Game.quoteBuyback()` passes
  `CallTree.CALL_PRICE` when `canCall(debt)`. `BuybackCheck` section 9.
- B, the screen: Close it now leads with what the auction books, fair's beside it and why they differ; the face's
  quick picks a quarter, a half, all on offer, the picked one marked. Renders in `docs/queue/BB1/`.

## A: the cap (CD4-C ★6, "Cap offers at 100 (Recommended)"), commit 4e544a04

`Buyback.Line` has a last component `callPer100` (`CallTree.CALL_PRICE` on a line the city may call now, else NaN; the
old ten-argument constructor gives NaN, so no other caller moved). `slices()` takes the least of the cap and the
ascending price, for every holder, the floor at fair included: the city could call the line at 100, so nobody is
paid more. With no call price the cap is infinity, and every other line prices exactly as before.
`Game.quoteBuyback()` passes the call price when `canCall(debt)` (callable, first call come, nothing unsettled).

`BuybackCheck` 9 (44 assertions): by hand, a 4.5% line asks up to 122.6 without the cap and at most the call price
with it, the offers under it unchanged; an 8% line whose fair is itself over 100 is offered at exactly 100 (the cap
wins over fair); in the played city, a callable line before its first call is quoted as before (its dearest offer
319.1 per 100: the households' rule near the deposit rate, CD4's), and at its first call the same ladder with each
offer at the least of it and 100, `buyBack()` paying that quote. A callable line's fair falls at its first call
month (the tree exercises), so 9b compares at that month's fair.

The trace: nothing in the month or the test player buys back (`Buyback.run` is reached only from
`Game.quoteBuyback()`, whose callers are the screen, `buyBack()` and the harnesses), so the default playtest cannot
move and the ensemble is skipped.
