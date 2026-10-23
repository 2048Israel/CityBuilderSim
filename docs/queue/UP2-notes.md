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

FIGURES_TBD

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

CHECKS_TBD

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
