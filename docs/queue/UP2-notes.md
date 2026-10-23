# UP2 - notes: the clock keeps its frame

*0.8.28, 2026-10-23. [The brief](UP2.md), from the plan "plan/ui-perf" (Jerus, 2026-10-22: "only loads
the screen you're at"); its item 4 as the study UP4 planned it (`docs/studies/spec-screens-in-place.md` §7,
"UP2's chrome"). Built from main 95d02db3 (0.8.26), main 9f6ece08 (UP3, 0.8.27) merged in at the end. The model's month is untouched and the save format is
unchanged. The slices went to Sonnet subagents (section 6).*

## 1. What was built

**The clock's pace, `ClockPace`** (new, pure Java; the window's timer calls it). Three changes to
`UserInterface.startClock()`:

- **A redraw is paced by its cost.** Each redraw the clock makes is timed from `redrawScreen.run()` to the
  end of the layout pass of the same pulse (a post-layout pulse listener on the scene), and the next waits
  `max(REDRAW_EVERY, cost x REDRAW_SPACING)`: 120 ms at least, three times the cost above that, so redraws
  take at most a third of the FX thread (a cost is counted at most `REDRAW_COST_MAX`, half a second, so a
  late pulse cannot hold the screen still). A pending redraw is drawn as soon as its gap has passed,
  whether or not a month lands in that frame. The press rule (0.7.40) is untouched: nothing is redrawn
  while a button is down, and the first frame after the release draws what is pending at once.
- **A frame has a budget for months.** The loop stops once the months run in a frame pass
  `MONTH_FRAME_MS` (40 ms); the first always runs. Time owed past one month is dropped, not banked
  (`droppedMonths()` counts it), so a slow stretch slows the city instead of bursting after it. The
  0.25 s frame clamp stays (`FRAME_CLAMP`, its two comments with it).
- **The speed says when the city falls short.** Over the last `SHORT_WINDOW` (2 s) of running time the
  months advanced give the speed achieved; more than `SHORT_BY` (a tenth) under the rung, the reading
  between the arrows shows both, "50× · 39×". The window starts again when the clock starts, the speed
  changes or another city opens. The reading is set in place on the kept label.

**The header reads the window it draws.** Each tile's sparkline reads its last `SPARK_MONTHS` through
`HistorySave.alignedTail()` (aligned()'s figures, padding and folded years included, without the whole
axis), unemployment through `YearBook.unemploymentTail()`, and the GDP tile's change line through
`YearBook.realGrowthTail(h, 1)`, which reads the 24 months that figure leans on. YearBook's
`labourForce`, `unemployment`, `real` and `rollingYear` each keep one definition, which the whole and the
tail reads share.

**The chrome kept** (the study's §7). The header (the clock block, the money block, the five tiles and
their sparklines, the "Needs you" chip, the rating, the envelope), the tab rail (twelve buttons and Menu;
the minimap node never taken out), the city panel (`SummaryScreen.refreshCityPanel()`: its sections a scope
each, keyed by their key), the construction panel (a site row keyed by its building, the two logs) and the
inbox's list (a notice a scope keyed by its key; the toasts as they were) - each drawn through `ui/Kept` from a pool of its own,
so a month that lands sets their figures on last month's nodes. `HeadlineTile` and `Sparkline` are
pictures told their figures by `set()` (a sparkline whose series is unchanged is not repainted).
What the rebuild did by being a rebuild is kept by hand: a clicked chrome control loses the focus at the
next draw, as it did when its node was thrown away; a node drawn again under a still pointer keeps its
hover look (the draw sets the look `isHover()` calls for: the rail buttons, the speed arrows, the tiles,
the money block, `Pieces.infoButton`); `anotherCity()` forgets every chrome pool (six: the header, the envelope, the rail, the city panel,
the construction panel, the list). The kept scrollers (the city panel's, the inbox list's) keep their place
by themselves; the city panel's restores its remembered place only on a new one, as before. `Kept.draw()` is now
safe re-entered (a draw of another pool inside a draw saves the outer and restores it): the header's draw
reaches the envelope's, which is also drawn alone when a notice is read.

**UiProbe** times the redraw through the pulse's own passes (`Scene.doCSSPass`/`doLayoutPass`, the
study's §9: `applyCss()` restyled every node, which no pulse does), prints the chrome alone beside each
screen's redraw, and at month 4,000 holds each screen's frame p95 at 50x to its figure before this batch
(`FRAME_P95`; People's is `FRAME_P95_MS`, 34) through `Timing`.

## 2. Measured

`UiProbe 4000` on the PC (i7-13650HX, JDK 21.0.4), the playtest's city grown to month 4,005 (560,230
people, 24,592 buildings). **The PC's speed is not steady**: after a few minutes of work it runs at about
half speed (the model's own month, which this batch does not touch, went from 13.4 ms to 31-32 ms between
runs of the same build), so a figure is compared only with one taken in the same state. Two ways round
it: the builds alternated, before and after (main f29b9938, 0.8.27, built with this batch's UiProbe, and
the branch), and pairs taken where the model's month agrees; and the chrome timed kept and built new
turn and turn about in one JVM (`UiProbe` does both now).

**A redraw** (median of 8, ms: the draw, then the pulse's own CSS and layout), the quiet pair (the model's
month 13.4 and 13.5 ms); "built new" is the same chrome with its pools forgotten, in the same run:

| screen | redraw before | after | the chrome alone before | after, kept | after, built new |
|---|---:|---:|---:|---:|---:|
| Build | 85.5 | 51.7 | 39.9 | 11.2 | 36.0 |
| Land | 54.9 | 39.0 | 26.5 | 8.3 | 29.5 |
| People | 39.4 | 18.9 | 25.9 | 7.7 | 22.7 |
| Services | 33.2 | 21.4 | 20.3 | 6.0 | 18.2 |
| Infrastructure | 31.1 | 19.2 | 17.7 | 6.0 | 16.3 |
| Sectors | 53.0 | 41.0 | 19.9 | 6.2 | 16.3 |
| Government | 30.1 | 18.3 | 16.2 | 3.9 | 14.2 |
| Finances | 24.6 | 13.0 | 14.9 | 2.3 | 11.7 |
| Bank | 35.3 | 23.7 | 15.3 | 3.0 | 10.8 |
| Trade | 26.5 | 16.6 | 14.5 | 2.5 | 11.5 |
| Policy | 22.0 | 14.3 | 11.9 | 2.4 | 10.5 |
| History | 40.9 | 36.5 | 12.5 | 2.9 | 13.2 |

So the chrome went from 11-40 ms of every redraw to 2-11 (a quarter; its CSS from 6-18 ms to 0.1-2.8),
and every screen's redraw is 4-34 ms cheaper; People's, the kept page with the chrome kept, is 18.9 ms
where it was 39.4 on main (and 53 before UP4, in the study's own run). Build's chrome is the dearest because its construction panel is open there.

**The clock at 50x** (months a second; frame p95 and max, ms; 3-4 s a screen):

| screen | quiet pair: before | after | slow pair (the model's month 32 and 31 ms): before | after |
|---|---|---|---|---|
| Build | 9.2/s, 99 (360) | 7.9/s, 194 (331) | 3.3/s, 864 | 4.0/s, 408 |
| Land | 10.0, 57 (84) | 9.9, 57 (81) | 6.3, 526 | 8.3, 221 |
| People | 10.0, 42 (94) | 10.0, **26** (43) | 8.7, 309 | 9.9, 110 |
| Services | 9.9, 37 (58) | 10.0, 28 (33) | 9.9, 123 | 9.8, 83 |
| Infrastructure | 9.7, 43 (70) | 10.0, 27 (42) | 9.6, 101 | 9.9, 58 |
| Sectors | 10.0, 65 (81) | 10.0, 41 (68) | 8.7, 318 | 9.3, 135 |
| Government | 10.0, 36 (54) | 10.0, 21 (33) | 9.8, 161 | 10.2, 62 |
| Finances | 10.0, 32 (53) | 10.0, 24 (36) | 9.6, 146 | 9.9, 50 |
| Bank | 10.0, 42 (66) | 9.7, 26 (65) | 9.4, 205 | 9.9, 68 |
| Trade | 10.0, 20 (48) | 10.0, 21 (58) | 9.9, 95 | 9.8, 44 |
| Policy | 10.0, 32 (54) | 10.0, 24 (40) | 9.7, 81 | 9.9, 52 |
| History | 9.9, 60 (74) | 10.0, 36 (66) | 9.3, 176 | 9.9, 101 |

- **People's p95 at 50x is 26 ms, under `FRAME_P95_MS` (34)**; the brief's figure was 49, this probe's
  before 42. Every screen's p95 is at or under its figure before (`FRAME_P95`, the quiet before run)
  but two: Trade, 21 against 20, which is the noise of a 3 s window; and Build, 194 against 99. Build is
  the first screen of the live loop, which also takes the probe's autosave, and its figure moves the
  most between runs (133 and 99 in two runs before, 136 with the header and rail kept alone, 194 after); in
  the slow pair
  it halved (864 to 408). Its page is the dearest still rebuilt (KP3's), and its redraw is 52-116 ms, so
  every frame that redraws it is long; the spacing makes those frames rarer, not shorter. Not proved
  either way here; checked by eye on the PC (UP2-C).
- **Where the PC is slow, the pacing is what holds the clock**: the frames' p95 fell by half or more on
  every screen and the months a second rose on the slow screens (Land 6.3 to 8.3, People 8.7 to 9.9); a
  city that still cannot keep up says so on the speed ("50× · 39×").
- The header's five histories: `HistorySave.aligned` and `raw` were 1.6% each of the FX thread's samples
  at 50x before (UP1's figure was about 1% for the header's reads); the tiles now read 120 months of each.
- In KeptCheck's city (600 months): the kept header and rail's month 1.2-1.9 ms against 7.1-10.3 built
  new; the whole chrome's 3.0-5.3 ms against 13-19.

## 3. How the kept chrome was proved the same

Three ways, each over a run of months:

- **KeptCheck 6-10, the header and the rail** (ui/KeptBench lays them out on scenes of their own, the
  header at 1,600 px and the rail at 900, built by the same `headerBox()`/`railBox()` as `start()`): on its
  city of 600 months, after the first draw (104 nodes: the header 47, the envelope 4, the rail 53) all 25
  months kept the same nodes and made none; the kept chrome's signature - every node's class, words,
  inline style, the values code set, handlers, tooltip and place - equals a chrome built new (every pool
  swapped for an empty one) at all 25 months, line for line, while the states change among them: the
  clock running and stopped, a "stopped because" line shown and gone, the rung, seven screens (so the lit
  tab, and a game menu with no clock), a notice raised, read and put away; the header and the rail are
  the same pixels at 6 of 6 pictures; one figure spoiled by hand parts the signature and the next draw
  sets it right; a focused control loses the focus at the next draw; a tile and a rail button drawn again
  under the pointer keep their hover look; a city opened makes every node new; a draw inside a draw keeps
  each pool's nodes its own. Made to fail on purpose (the play glyph left stale on a paused month),
  section 7 found 13 differences.
- **KeptCheck 11-13, the city panel, the construction panel and the inbox's list**: 671 nodes at the first
  draw (622, 30, 19), 750 handed back a month; 146 made new over 25 months, at most 13 in one, because the
  logs and the inbox gain rows (so section 11 holds "a fiftieth of the nodes a month on average, no month
  over a tenth" rather than the page's "most months none"); the signature equals a build with every node
  new at 25 of 25 months through the drawer open, closed and pinned, a section folded and unfolded,
  buildings put on site, finished and demolished (`addStack` and the model's own calls, since the played
  city has none on site), the list open and closed and a notice unfolded, added and removed; 9 of 9
  pictures the same pixels; a spoiled figure seen and set right.
- **Against 0.8.26 itself, in the real window** (a scratch probe, `ChromeRender`, beside the build, never in
  `src/`): the playtest's city grown to month 1,000 and to 4,000, the window at 1,600 by 900, People, Build,
  History (the drawer open) and Bank (the inbox's list open), six months redrawn on each, then the header,
  the rail, the construction panel, the city panel and the list written as PNGs - on main 95d02db3's build
  and on this branch's. **All 28 pictures are the same pixels.** The first run found the one thing
  KeptCheck's tolerance let through: a kept sparkline resized kept its canvas's texture, so its last
  pixel column showed whole where a new canvas cuts it (4-9 pixels of a header, by up to 21 levels of
  255); a sparkline whose size moves now draws on a new canvas, and the pictures are the same.

## 4. The checks

- **ClockCheck** (new, in the suite; no toolkit): 1, the redraw's gap after redraws of 10, 60 and 200 ms,
  from both sides; 2, a frame's months under `MONTH_FRAME_MS` from both sides, and a 600-frame run at 50x
  with months of mixed cost in which the months run, owed and dropped add up to the time; 3, a 2 s stall
  at 50x with months of 5 and of 50 ms - no burst after it; 4, the reading at the rung, past the tenth and
  just inside it, at 0.1x, after a restart; 5, the header's five series read the windowed way equal the
  whole-history reads' last `SPARK_MONTHS` (and the GDP tile's change line), on a young city, an older
  one, and a hand-grown history past `MONTHLY_KEPT` with folded years.
- **KeptCheck 6-13** (new sections; section 3), with its budgets `KEPT_CHROME_MS` (4) and
  `KEPT_ALL_CHROME_MS` (8) through Timing.
- **The suite** (`batch-check.sh`, the final model): SUITE_TBD
- **Timing, hard, the timing harnesses alone** (`batch-check.sh --timing`): TIMING_TBD
- **The trace.** The default playtest traced (`-Dplaytest.trace`) on main 95d02db3 and on this branch: all
  eight CSVs byte-identical, the report identical but for its run-time lines (70 s and 65 s). UP3 (merged
  in) left the trace as 0.8.26's, so it is 0.8.27's too. **No ensemble** (EN1's rule).

## 5. For Jerus (UP2-C)

Checked by eye on the PC, at 50x on a long city: a click on a dial lands (0.7.40's rule, with the new
spacing); hover and focus on the header, the rail and the panels under a real pointer; the speed's second
figure on Build, where the city falls short; the drawer's pinned bar and the construction tab, which are
still rebuilt (`drawerBar`, `refreshConstructionTab`: small, left). His calls:

- **★1 The speed's reading when the city falls short.** "50× · 39×" between the arrows: the rung asked for,
  then the speed the last two seconds made. The label widens for it, so the tiles to its right move a few
  pixels when it comes and goes. *Default: as built. Other ways: the achieved figure alone in amber, or a
  tooltip.*
- **★2 The redraw spacing on heavy pages.** A redraw now waits three times what the last cost: Build's
  (70-100 ms at month 4,000) every 210-300 ms at 50x instead of 120, so its figures move three or four
  times a second where the city moves ten months. A page that redraws in under 40 ms keeps the 120 ms
  floor. *Default: REDRAW_SPACING 3 (redraws at most a third of the thread).*
- **★3 Owed time dropped, not banked.** When a frame cannot run the months due in 40 ms, what it owed past
  one month is let go: a slow stretch runs the city slower (and the speed says so) rather than paying the
  months back in a burst after it. The months themselves are never skipped - the city plays every month,
  just fewer a second. *Default: as built.*
- **★4 The inbox's list keeps its scroll.** Rebuilt, it went back to the top at every redraw; kept, it
  stays where you put it, as a kept page does. *Default: kept (a fix, as we read it).*

UP4's calls (its ★1, ★2 and ★4: the screens' order, KP1-KP7, the count-up at 50x) still stand; its ★3,
the chrome in UP2 with Kept, is done here.

## 6. The slices

Three slices, each to a Sonnet 5.5 subagent at xhigh, reviewed here and committed one by one; the UiProbe
change, the renders and the measurements were the Opus session's.

| slice | what | the review |
|---|---|---|
| 1 | ClockPace, the clock wired to it, the header's windowed reads (HistorySave.alignedTail, YearBook's tails, `UserInterface.sparkSeries`), ClockCheck | It added `REDRAW_COST_MAX` (a late pulse cannot hold the screen still for minutes) and counts the window in months advanced, not landed, so 0.1x has a speed over two seconds. Kept both. `historyValues` stays for City History; `sparkSeries` reads the five keys as its cases do (`aligned`, and `YearBook.unemployment`), and ClockCheck holds the tails to those whole reads. |
| 2a | the header and the rail kept; Kept.draw re-entrant; KeptBench's chrome; KeptCheck 6-10 | The envelope has a pool of its own (it is drawn alone too); the hover look also in `Pieces.infoButton`; `popPip` through `Kept.play`, which the Land and Bank screens' pops share. The renders found the sparkline's canvas (section 3). |
| 2b | the city panel, the construction panel, the inbox's list; KeptCheck 11-13 | Section 11's "few or none" became "a fiftieth a month on average, no month over a tenth": the logs and the inbox gain rows month by month, which a rebuild makes new as well. A new section's premise, said here. `Pieces.monoLabel` through Kept (new outside a draw, so its other callers are unchanged). |
