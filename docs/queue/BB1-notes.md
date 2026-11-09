# BB1 - the buyback card shows what the auction would book; offers capped at the call price ({next}, 2026-11-09)

The brief: `docs/queue/BB1.md` (CD6-C ★1, CD4-C ★6). Branch `batch/BB1` from main 06125149.

Compaction window: `$CLAUDE_CODE_AUTO_COMPACT_WINDOW` = 300000 as this side session's shell sees it.

## The plan

- A, the model: `Buyback.Line` gains `callPer100` (NaN when the line may not be called now; a ten-argument
  constructor keeps every old caller), every offer capped at it; `Game.quoteBuyback()` passes
  `CallTree.CALL_PRICE` when `canCall(debt)`. `BuybackCheck` section 9.
- B, the screen: Close it now leads with what the auction books, fair's beside it and why they differ; the face's
  quick picks a quarter, a half, all on offer, the picked one marked. Renders in `docs/queue/BB1/`.
