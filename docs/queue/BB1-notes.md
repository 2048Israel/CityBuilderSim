# BB1 - the buyback card shows what the auction would book; offers capped at the call price (0.9.66, 2026-11-09)

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

## B: Close it now leads with what the auction would book (CD6-C ★1), commit df924cc4

Jerus, 2026-10-31: "show fair, but when click buyback it shows what the actual gain or loss would be cause of
liquidity or whatever, with option to just buyback part of it or something idk." The book's desk and the line's
page stay at fair. The Close it now page has a new first card under the piece, WHAT THIS BUYBACK WOULD BOOK: the
gain or loss at the holders' asks against carrying value, large, green or red; the gain at fair for the same face
under it; and a sentence from the quote's own figures - the holders ask D$X over fair for this face, the holder
asking most named with its amount (or "the offers taken are at fair, so the two agree"), why the households, the
businesses and the world ask more than fair while the bank sells at fair (D34), and on a line callable now that
no holder asks more than the call price. The model's three new getters on `Buyback.Result`: `gainAtFair()`,
`gainAtFairShare()`, `overFair(Holder)`; the screen recomputes nothing. The face's quick picks are now a quarter,
a half and all on offer, beside the box (they wrap under it when narrow), the one the box holds underlined, and a
line under them: the cheapest offers go first, so the less of the line asked for, the less each 100 costs. The
desk's Close it now tip says its figure is at fair and the page shows the auction's.

`BuybackCheck` 10: on a quote taking from more than one holder, the gain at fair less the auction's gain is what
the holders ask over fair, the holders' over-fair adds to it, the bank's is nothing; a bank-only quote agrees with
fair; CD6's case by hand (households hold all of it, carrying value between the fair worth and the auction's
price): fair reads +43.7, the auction -43.7. `KeptCheck`'s face-box test pressed the chip "half", now "a half":
that one token changed, the test's premise unchanged.

**Look at** (renders, `docs/queue/BB1/`, `RenderBB1.java` beside them): `close-all.png`, the household-held
ten-year line all on offer, +D$8.5k (+0.4%) at fair and -D$292k (-14.6%) at the asks, households 75%;
`close-half.png` the same at a half (+D$4.2k, -D$51k: a half costs less over fair); `close-callable.png` a
callable line at its first call, the households' slices at 100.000 and the cap's sentence; `close-narrow.png` at
900 and `close-narrowest.png` at 520, the chips wrapping. RenderCD6's own sequence read every line red or both
green, so the render raises the policy rate in steps to 19.5% until the ten-year line crosses (CD6's +5.1M /
-2.1M was not reproduced). Seen, not this batch's: a holder holding dust (the world here) shows "Taken from the
world D$0 for D$0" and ten D$0 slices on the ladder, as since CD4.

## The checks

`./batch-check.sh --pick` with the Earth and world files named: exit 3, 10 of 188 harnesses (LongPlaytest for
Game.java), the only red KeptCheck's, main's recorded one. Timing left to the gate.

The trace: nothing in the month or the test player buys back (`Buyback.run` is reached only from
`Game.quoteBuyback()`, whose callers are the screen, `buyBack()` and the harnesses), so the default playtest cannot
move and the ensemble is skipped.
