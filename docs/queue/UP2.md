# UP2 - the clock keeps its frame: redraws paced by their cost

Jerus, 2026-10-22, on the slowdown: "only loads the screen you're at ... if
screens [are] better to have preloaded then that's fine". This batch answers
that ask with numbers, and comes after UP1, whose probe (`ui/UiProbe`)
measures it.

## What was measured (plan/ui-perf, 2026-10-22; UP1's notes have the method)

**Only the screen on show is built today.** `UserInterface.clearMenu()`
(line 1145) empties the one page (`rootMenu`) and the screen's show method
builds it again. Hidden tabs cost nothing: there is no other page. The clock
(`startClock()`, line 5941) runs the month on the FX thread
(`game.toggleNextMonth()`), then at most every `REDRAW_EVERY` (120 ms)
re-runs the visible screen's show method (`redrawScreen`). That rebuilds the
page from scratch, and the chrome on every screen with it:
`refreshHeader()` (`header.getChildren().clear()`, line 1903),
`refreshTabRail()`, `refreshConstructionPanel()`, `refreshInbox()` and
`SummaryScreen.refreshCityPanel()`.

Per redraw, by screen (median of 8), at month 4,000 of the playtest's city:

| screen | build | CSS and layout | first visit |
|---|---:|---:|---:|
| People | 21.3 ms | 33.5 ms | 52 ms |
| Sectors | 14.5 | 25.2 | 45 |
| History | 12.7 | 27.4 | 44 |
| Land office | 11.7 | 19.0 | 36 |
| Build | 10.8 | 20.1 | 36 |
| Bank | 10.0 | 25.7 | 46 |
| Services, Infrastructure, Government, Finances, Trade, Policy | 6.2-9.1 | 17.1-21.7 | 23-35 |

- **The redraw does not grow with the months played.** At month 1,000 the same
  screens took 7.5-29.7 ms to build and 14-44 ms in CSS and layout (the JIT
  was still warming). What grows is the autosave (UP1).
- **At 50x a redraw lands every 120 ms**, so redraws take 25-45% of the FX
  thread. At month 4,000, frames on every screen were p50 6-11 ms and p95
  32-59 ms. The JavaFX pulse (CSS, layout, sync) was 27% of FX-thread samples,
  and the chrome's build (header, rail, panel, inbox) about 4%, before its
  share of the pulse.
- **The month itself was not the problem** in the fixtures: the clock's month
  was median 6.8 ms at month 4,000, and the clock held 50x (9.3-10 months a
  second) on every screen. But nothing stops a heavy month, or a heavy
  redraw, from stacking. The loop runs every month due in one frame (up to
  2.5 at 50x after a stalled frame), and the redraw spacing never looks at
  what the redraw cost.
- **The header reads five whole histories a redraw.** `headlines()` takes
  `historyValues(h, ...)` for each tile (lines 1981-2096), and each is a
  `HistorySave.aligned()`, which rebuilds `seriesByName()` and a full-length
  array, plus `YearBook.realGrowth(h)`. The sparkline draws only
  `SPARK_MONTHS` (120). That is about 1% of the FX thread at month 4,000, and
  linear in the months.

**The call on preloading.** Building all twelve screens once and keeping them
would not make a redraw cheaper: the screen on show is still torn down and
rebuilt each month. It would buy a faster first switch, 23-52 ms at month
4,000, about one to three frames, for twelve pages of nodes held in memory.
Not done. What would cut a redraw is updating the page in place instead of
rebuilding it, since CSS and layout are about 60% of a redraw. That is a
rewrite of the twelve screens' builders, so it is the study UP4's to plan
(queued just ahead of this batch), not this batch's. This batch does it only
for the chrome, which is on every screen. If UP4 has shipped and its note
plans the chrome too, this batch follows UP4's plan for it.

## What changes

1. **The redraw is paced by its cost.** The clock measures each redraw from
   `redrawScreen.run()` to the end of the layout pulse that follows it (a
   post-layout pulse listener on the scene). The next redraw waits
   `max(REDRAW_EVERY, cost x REDRAW_SPACING)`, a new dial: redraws take at most
   a third of the thread. At 50x a 55 ms People page redraws every 165 ms; a
   20 ms page keeps the 120 ms floor.
2. **A frame has a budget for months.** The loop stops when the months run in
   this frame pass `MONTH_FRAME_MS`, a dial of about 40 ms; at least one month
   runs whenever one is due. Time owed past one month is dropped, not banked,
   so a slow stretch slows the city instead of bursting afterwards. The
   frame clamp (0.25 s) stays.
3. **The speed says when it falls short.** When the months landed over the
   last two seconds fall more than a tenth under the rung, the reading
   between the arrows shows both ("50x · 31x"). It is in the label already
   kept (`speedReading`), so the header is not rebuilt for it.
4. **The chrome is kept and updated in place**, but only where the probe shows
   it still matters after 1-3: the header's tiles and money block, and the
   tab rail. Their nodes are built once, and a redraw sets their text, tone
   and sparkline. Rebuild only when their structure changes: a tile added, a
   city opened, a rail tab shown or hidden. If after 1-3 the chrome's build
   and layout together are under 5% of the FX thread at 50x, this item is
   left undone and said so in the notes.
5. **The header reads the window it draws.** Each tile reads the last
   `SPARK_MONTHS` + 13 months, through a `HistorySave` call that copies only a
   tail. The series is computed once per redraw, not once per tile. Same
   figures, asserted.

The clock's arithmetic (1-3) lives in a small model class, say `ClockPace`:
pure, no JavaFX. The ui's timer calls it, and the harness drives it.

## The harness

New `ClockCheck`, registered in `AllChecks`, with fixtures that cause each
condition:

- A redraw costing 10, 60 and 200 ms is followed by the next no sooner than
  `max(ClockPace.REDRAW_EVERY, cost x ClockPace.REDRAW_SPACING)`.
- Months costing 5 ms run as many in a frame as are due. Months costing 50
  ms run one a frame. Across a 600-frame run no month is skipped or run twice:
  the count of months equals the count of `toggleNextMonth()` calls.
- A 2 s stall at 50x adds at most one month owed (no catch-up burst).
- The reading is the rung while the city keeps up, and both figures past the
  tenth.
- The header's five series, read the windowed way, equal the last
  `SPARK_MONTHS` of the whole-history read, value for value, on a city with
  more than `SPARK_MONTHS` months.

On the PC, `UiProbe` at month 4,000 is the timing. Frame p95 at 50x on People
should be under `UiProbe.FRAME_P95_MS` (a dial; about 34 ms, against 49 today)
and on every screen under its own figure today, through `Timing.within`. The
numbers before and after go in the notes. It is not in the suite (it needs a
display; UP1).

## Files

- `ui/UserInterface.java`:
  - the clock, `startClock()` (line 5941);
  - `REDRAW_EVERY` moves to `ClockPace`;
  - `clearMenu()`, `refreshHeader()` (line 1902), `headlines()` (line 1937),
    `refreshTabRail()` (line 5313), `clockBlock()`.
- `ui/HistoryScreen.historyValues()` (line 2450) and `HistorySave` for the
  tail read.
- New `ClockPace.java` and `ClockCheck.java`; its line in `AllChecks`.
- `ui/UiProbe.java`: the frame dial.

## Risk

- **A press under a rebuild.** 0.7.40's rule (a press holds the redraw)
  and 0.7.50's (a page torn down hears nothing) must hold with the new
  spacing. Checked by eye on the PC: a click on a dial at 50x lands.
- **Kept chrome holding stale state.** A tile kept across a load shows the
  last city. The kept nodes are dropped wherever `anotherCity()` drops the
  screens' state.
- **The speed shown is lower than the rung asked for.** That is the point.
  It is checked by eye.

## Decided

- **The screens updated in place get a study of their own, now** (Jerus,
  2026-10-22: "Study it now"). That is UP4, queued after UP1 and ahead of
  this batch. This batch keeps to the chrome.
