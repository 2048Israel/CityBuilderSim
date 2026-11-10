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
- **The preview is the floor's own move** (the brief's item): the ladder's ghost and the pair on each row are the
  target the wage walks to now against the target with the staged floor, so the market's own walk is not counted as
  the floor's doing; a wage still walking shows the target as a thin mark (`PolicyScreen.WALK_SHOWN`, 1% of the
  wage, nearer is the bar's own end). The payroll row reads `floorMoveAt()`'s two bills, the figure the effect
  always meant, where the slice had summed the lifted workers alone.
- **Choosing the average-wage tie** holds the floor at its share of the average the day it is chosen, held to
  .15-.70 (`FloorView.enteringShare()`); a floor outside that range is moved by the choice, and the staged words say
  so ("so this lowers it").
- **The average-wage gauge** marks three of the 33 nations by name (the lowest, Canada, the highest) and stripes
  the OECD's .38-.55 band; names that would run into each other drop off the line under the gauge
  (`PolicyScreen.apartNames`, the tick and its tooltip kept), so the caption names the marked ones too.
- **Jobs with no posts** draw a grey bar and "no posts here" (`FloorView.NO_POSTS_WORDS`) in place of a wage.
- **The joined market:** when the fill joins the lowest band to the one above (`BandFill.joinedAbove`), the page
  names the diploma holders in the unskilled posts and the one wage both are paid.

## The slices

- **WS1-A** (Sonnet 5.5, xhigh): the two findings, `FloorView` and `PolicyPreview.floorMoves()`, `LabourCheck`'s two
  sections (the save-and-load one shown red with the fix taken out), `PolicyPreviewCheck`'s page probe. Committed as
  is.
- **WS1-B** (Sonnet 5.5, xhigh): the page in `PolicyScreen` and the render tools. Mended here: the payroll row (above),
  the marked nations in the caption, two `PolicyPreviewCheck` sums (the payroll pair against `floorMoveAt()`, the
  near shares against their counts), and the joined-market fixture, which imprisoned only the unskilled: the
  diploma band's spare then filled their posts and nothing joined. It now takes away, in steps of 2% of the
  workforce, as many as it takes for the lowest band to be hiring and joined (339 of 3,616 in the harness city).

## The renders

`docs/queue/WS1/`: `RenderWS1.java` draws the page from a scratch city in six states (the default, a floor staged
over the going rate, the average-wage tie staged, 900 px wide, a floor that binds, the tie in force). Build and run
from the worktree root:

    javac -cp "target/classes;$(cat target/classpath.txt)" -d target/ws1cls docs/queue/WS1/RenderWS1Page.java docs/queue/WS1/RenderWS1.java
    java -cp "target/ws1cls;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.RenderWS1 docs/queue/WS1

## The checks

- `./batch-check.sh --pick` with the data named, on the final tree with main 0895f70e (GD2) merged: exit 3, 16 of
  194 harnesses, the only reds main's recorded ones (PriceLevelCheck, KeptCheck, LongPlaytest, by
  `docs/notes/gate.txt`); KeptCheck's 3 timing assertions soft. `--timing` left to the gate, as the brief says.
- **The trace:** LongPlaytest on the branch against main 0895f70e's, both with the data named: byte-identical but
  for its two run-time lines (the run's seconds, and the CL3 windows' share of them). The model change only appends
  a saved value and moves the average-wage tie's paths, which the default (prices) never takes: no ensemble.
- No format bump: an older save reads the tie's last average as 0, which is what it was after every load before.
