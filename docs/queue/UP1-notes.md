# UP1 - notes: the autosave off the clock

*0.8.25, 2026-10-23. [The brief](UP1.md), from the plan that measured the slowdown after long play (merge "plan/ui-perf"; Jerus, 2026-10-22: "whatever you think, idk what's good"; the 30 s floor his call the same day). Built from main 015a4ed4 (0.8.24). The model's month is untouched; the save format is unchanged. The slices went to Sonnet subagents (section 7).*

## 1. What was built

**One writer, a snapshot, the model read only on the FX thread.** `Game.autosave()` still fills the save where the model is safe to read: `prepareSave()` is the old `save()` up to the stamp, unchanged (about 150 setters, the map's sidecar bytes). It then hands `SaveWriter.shared()` a `Job` holding `DataSave.snapshot()` (a shallow clone plus its `buildings[]`, the one array DataSave writes in place) and `HistorySave.snapshot()` (`restoreFrom()`, the one place every series is listed: every list copied), and returns. The writer is one thread for the JVM, started when there is something to write and gone when there is not, not a daemon (a JVM that ends by itself, the window closed, waits for a write in flight). It writes the save, the map and the history in the order `save()` always did. A job for a file that has one waiting replaces it, and two writes never run at once. Its result comes back to the FX thread through a queue that `reportSaves()` drains at each month's end: a failure becomes `getLastSaveResult()` and prints today's `Autosave failed:` line.

**The save holds no live object.** A shallow clone is only safe if no setter keeps a reference the model still changes. Slice B made that mechanical (section 3) and found 531 shared objects at month 124: the bond market's bonds and books (358), the exchange's books (121), the build log's entries (40, whose quantity grows), the construction control's live state (9), the licensed and skilled head counts, and the inbox's notices (which `markRead()` changes). Those setters now copy: `SaveCopy` for a list or a map (into a LinkedHashMap in the source's order, so the JSON text is unchanged) and a `copy()` beside the fields of `Notice`, `BuildLog.Entry`, `OrderBook`/`Order`, `CorporateBond`, `BondMarket.State`, `Exchange.State`, `ConstructionControl.State` and its six records, `TreasuryFund.State` with `Resolution` and `HandOrder`, `Bank.Preferred`, and `AutoBuilder.Entry` in `AutoBuilder.toState()`. The last four a grown city never reaches; the static audit of every setter found them.

**The waits.** The numbered slots go through `save()`, which waits for the writer and then writes the live objects on the calling thread, as before: the player's Save is shown as it was. A load, a skip (before its "before skipping" autosave, and again at its end, so a skip ends with its files on disk), Quit (`saveForQuit()`: the quit's autosave, waited for, then `System.exit`) and the Load menu (it shows the autosave's header) wait too.

**The floor** (Jerus: "At most every 30 s"). At the end of `nextMonth()` a due month writes only if `SaveWriter.FLOOR_SECONDS` of real time have passed since the last autosave; otherwise its counter starts again and the next due month past the floor writes. Nothing is queued for later. The skip's and Quit's autosaves are not floored. The floor's clock lives on Game outside `buildWorld()`, so it spans a load.

**The files.** `GameFiles.write(Path, Body)` streams through a buffered UTF-8 writer into the `.tmp`, then the `.bak` and the atomic move, as the byte version (one shared tail). The save stays pretty and is byte for byte what `gson.toJson(this)` gave; a number JSON cannot hold still fails as "the city's numbers cannot be written down". The history is compact: 7.5 MB at month 4,000 where it was 13.4 MB. Gson reads either, so an older pretty history loads.

**ui/UiProbe** is the plan's three scratch sources made one `main()` in the tree, run by hand on the PC (not in AllChecks): it grows the playtest's city, opens the window, times each rail screen's build and layout, runs the clock at 50x on each, and profiles the FX thread with JFR. Its javadoc says how to run it. LongPlaytest has a small public door for it (`probeCity`, `probePlay`, `probeMonthMs`). Where JavaFX cannot start it says so and exits 0.

## 2. Measured (the probe, the PC's i7-13650HX, JDK 21.0.4)

Before is the plan's run (`UP1/default-m1000-4000.txt`, main before UP1); after is `UiProbe 1000,2000,3000,4000 3` on this branch (`UP1/up1-m1000-4000.txt`). The cities are the playtest's founding grown by the same loop; they differ a little, since the model moved between the two builds (323,246 people at month 4,005 after, 315,140 before).

| month | autosave on the clock, before | after: on the clock (fill, snapshot) | after: the write, off it | autosave's share of the FX thread at 50x, before | after | longest frame at 50x, before | after |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 1,000 | 50-64 ms | 5 ms | 61 ms | 11.6% | 0.0% | 94-332 ms | 47-251 ms |
| 2,000 | 141-149 ms | 4 ms | 86 ms | 20.7% | 0.0% | 129-247 ms | 40-87 ms |
| 3,000 | 138-162 ms | 4 ms | 90 ms | 28.1% | 0.0% | 194-240 ms | 56-125 ms |
| 4,000 | 169-176 ms | 4 ms | 112 ms | 31.4% | 0.0% | 225-297 ms | 45-124 ms |

- The longest frame range is across the twelve screens; month 1,000's 251 ms is the Build screen's first frames, the rest of its screens 47-83 ms. At month 4,000 the GC paused 14 times for 204 ms in the probe's run, against 62 times for 1,320 ms before: the 13 MB String and its bytes are gone.
- The player's own Save (a numbered slot, on the clock by design) is 119 ms at month 4,000, where the autosave was 176 ms: the history is compact and streamed on that path too.
- The `save-writer` thread shows 5-10 JFR samples a run. Each live run opens with one autosave the probe asks for; the floor then holds back the due months for 30 s, so the share at 50x is now the floor's as much as the writer's.
- The FX thread at 50x is now the screens' (the pulse 49-54%, layout and CSS) and the month (`nextMonth` 23-39%): UP2's and UP4's.
- In SaveSlotCheck 7c.2, on a city whose history holds `MONTHLY_KEPT` (6,000) months: the old way wrote it in 151-280 ms across runs; the month that autosaves now costs the clock 7-19 ms (the median of 5, the whole month; a plain month there is 5-9 ms), against `CLOCK_MS` 25.

## 3. How the save was proved exact

- **7c.1, the walk.** Two snapshots taken back to back on a city grown 180 months are walked the way Gson walks them, and every mutable object (an array, a list, a map, a model object with a field that is not final) is collected by identity: 1,627 in the save, 246 in the history. None is in both, and no class in them is one the walk cannot see into (a view such as `Collections.unmodifiableList` fails on its own). A setter that kept a live object would have put the same object in both. The walk is shown to see sharing: a snapshot shares 1,625 objects with the live save, a snapshot of a snapshot all but its own two, and one list planted in two saves is named by its field.
- **7c.1b.** The copying setters on states filled in every field by reflection (the ones a grown city does not reach): each copy shares nothing with its source and writes the same JSON, and every field was filled, so a field added to one of those classes later fails here.
- **7c.3, behind the months.** Slot 3 is saved the old way (the live objects, on the calling thread); the autosave of the same state is asked for and held in the writer while three months change the city; released, its save equals slot 3's in all 183 fields but `slotName` and `savedAt`, its history is identical as text (8.3 MB) and as a tree, its map byte for byte, and it loads at the month it was asked at.
- `SaveFileCheck`, `ReadPathCheck` and `HistoryCheck`, unchanged, are green.

## 4. The checks

- **SaveSlotCheck 7c** (new), in its header's words: 7c.1 and 7c.1b above; 7c.2 the month that autosaves within `CLOCK_MS` through `Timing` (SaveSlotCheck joins `Timing.HARNESSES`); 7c.3 above; 7c.4 two autosaves back to back write once held, once or twice free, never two at once (`mostAtOnce() == 1`), the files whole after each; 7c.5 a load, a numbered save, a skip and Quit each return only after a held autosave is written (the skip's writes are 2, where without the wait its own would have replaced the held one); 7c.6 a write that cannot open its `.tmp` (a folder in its place) is reported (`Autosave failed:`, `getLastSaveResult()`), the earlier autosave untouched, and the next writes; 7c.7 the floor - 48 months on a still clock write nothing, a second short of the floor nothing, a second past it one, and on the real clock 60 months of a young city in 0.4 s write exactly one, the first due month. SaveSlotCheck takes about 20 s.
- **Section 7 and 7b**, the cadence and the whole month: the city gets a save clock that passes the floor at every look (as 30 s of play would), so the twelfth month and the skip's months write as they did. 7b's quit is `saveForQuit()`.
- **The suite** (`batch-check.sh`, the final model): 129 harnesses, 127 passed. MapCheck 5 missed "at 10 billion a month's change takes no more than 5 ms" by 0.08 ms (5.08), the map's own figure (MapCheck steps with `simulateMonths`, which waits for the writer, so no write ran beside it), counted hard because no other batch's JVM ran at the suite's start; alone and hard it passes. StaleCheck: CLAUDE.md line 50 says the interface is "thirty files", and `ui/` holds 31 with UiProbe. CLAUDE.md is the session's instructions, which this batch does not edit: the merge (or Jerus) changes "thirty" to "thirty-one". Stale has no other firm finding.
- **Timing, hard, the timing harnesses alone** (`batch-check.sh --timing`): 9 of 9 green, SaveSlotCheck among them (7c.2's median 6.8 ms against 25; the old way 151 ms on its 8.2 MB history), MapCheck 5 OK.

## 5. The trace

Main (015a4ed4) built apart from its own sources, and the batch, the default playtest traced on each (`-Dplaytest.trace`): all eight CSVs byte-identical, and the report identical but for its run time - 108 s on main, 73 s here (the run's 333 due months each wrote an autosave on main; the floor now holds them to one in 30 s of real time, and the history is written compact). So no ensemble (EN1's rule).

## 6. For Jerus (UP1-C)

Checked by eye on the PC: a long city at 50x on any screen, the stall every 1.2 s gone; the console's `Autosave: Saved to` lines at most one in 30 s; Load's autosave row the latest. His calls:

- **★1 The window's close button.** Closing the window (not Quit) writes no autosave, as before UP1; a write in flight now finishes before the game exits. Should closing the window autosave like Quit does?
- **★2 The player's own Save stays on the clock**, as the brief decided (119 ms at month 4,000, the result shown at once). It could go to the writer with a "Saving..." line instead.
- **★3 The floor spans the session**: it is measured from the last autosave of any kind (a skip's and Quit's included) and survives a load, so the first due month after a load within 30 s of an autosave does not write. Fine as it is?

## 7. The slices

Four slices, each to a Sonnet 5.5 subagent at xhigh, reviewed here and committed one by one.

| slice | what | the review |
|---|---|---|
| A | SaveWriter, the streamed writes, the snapshots, Game's split, waits and floor; 7/7b on a passing clock | It moved the passing clock from 7b to section 7, where the skip's assertion had in fact been met by the skip's own month-24 and -36 autosaves, which the floor now holds back. Kept. |
| B | the walk (7c.1, 7c.1b) and the copying setters | Found the 531 shared objects; the clone-based `copy()`s are held by 7c.1b's filled states. `OrderBook`'s two lists lost their `final` for the copy. |
| C | 7c.2-7c.7 | Nothing changed. |
| D | ui/UiProbe, LongPlaytest's door, the menus | The Save menu's wait dropped (it lists numbered slots only, which the writer never writes); the Load menu keeps its wait. |
