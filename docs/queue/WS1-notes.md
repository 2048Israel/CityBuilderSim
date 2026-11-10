# WS1 - the minimum-wage page ({next}, 2026-11-10)

The brief: `QUEUE.md`'s row and `docs/studies/spec-wages.md` section 3.6 with its `mockup.html` (Jerus, 2026-11-02:
a floor that says whether it is too much or too little, on a Wages screen that is intuitive). Branch `batch/WS1` from
main 3abf4e9b (0.9.83, WG1 on it). Two slices on Sonnet 5.5 at xhigh, reviewed and committed here: WS1-A (the model:
WG1's two review findings and `FloorView`, the page's figures), WS1-B (the page).

## Decided while building (no call left open)

- **WG1's review, finding 1:** `LabourMarket.lastAverageWage` was transient, so 0 after a load (a dial move under the
  AVERAGE_WAGE tie before the first month was undone by the next strike), and `redenominate()` did not scale it (in a
  reform month the share was computed across two units). It is saved now, one value at the end of the labour
  market's state array (an older save reads 0, as before), and scaled with the wages.
- **WG1's review, finding 2:** `MAX_TIE_SHARE` was 1.0, where the floor is last month's average itself and each month
  absorbs the rung over it. Now .70 and `MIN_TIE_SHARE` .15: the world file's 33 nations run .17 (the United States)
  to .69 (Costa Rica), so every nation's ratio can be copied and none past it. Under 1 the tie settles: a floor
  struck at share s of an average that includes it is at most s of the mean of the wages over it, f = s(1-p)w/(1-sp)
  < w. The review suggested .55 (the top of the OECD's middle band); .70 keeps the three nations over .55 reachable
  and is as safe by the same sum.
- **The money in the model's words:** `FloorView`'s words take the screen's money writer as an argument, so no new
  "$" writer is made and FM1's one writer carries over.
