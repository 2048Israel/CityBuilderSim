# UP1 - the autosave off the clock, and the probe that times the window

Jerus, 2026-10-22: the game gets sluggish after long play ("only loads the
screen you're at", "the map doesn't regenerate every single building",
"whatever you think, idk what's good"). The plan's measurements are below;
this batch is the largest cost they found, and the one that grows with every
month played.

## What was measured (plan/ui-perf, 2026-10-22)

A scratch probe grew LongPlaytest's city (its own loop: the playtest's
founding, `advise`, `ensureSchools`) and, at months 1,000 to 4,000, opened the
real window (`new UserInterface(game)`, `start(stage)`, a scratch `GameFiles`)
and timed what the FX thread does: each of the twelve rail screens built and
redrawn, then the real clock at 50x for 3 s a screen with JFR sampling the FX
thread (`profile` settings, stack depth 512). The i7-13650HX, JDK 21.0.4.

| month | people | history file | save file | autosave on the clock | autosave's share of the FX thread at 50x | longest frame at 50x |
|---:|---:|---:|---:|---:|---:|---:|
| 1,000 | 11,859 | 2.9 MB | 1.0 MB | 50-64 ms | 11.6% | 94-332 ms |
| 2,000 | 67,621 | 6.1 MB | 3.7 MB | 141-149 ms | 20.7% | 129-247 ms |
| 3,000 | 161,237 | 9.7 MB | 3.3 MB | 138-162 ms | 28.1% | 194-240 ms |
| 4,000 | 315,140 | 13.4 MB | 3.3 MB | 169-176 ms | 31.4% | 225-297 ms |

- The autosave runs inside the month, on the FX thread, every
  `Game.AUTOSAVE_MONTHS` (12): at 50x that is every 1.2 s, a stall of 170 ms
  at month 4,000 every 1.2 s, on every screen. It is the longest frame on
  every screen at every month measured.
- Of the autosave, the history is two thirds: `HistorySave.saveHistory` alone
  was 22.3% of all FX-thread samples at month 4,000 (`DataSave.saveGame` 6.0%,
  `GameFiles.write` 3.0%). Both are Gson with `setPrettyPrinting()`, built
  into one `String` and then written; `GameFiles.write` also copies the old
  file to `.bak` first.
- The history grows by about 3.3 MB a thousand months (143 named series plus
  one per company and sector, every month kept to `HistorySave.MONTHLY_KEPT`).
  Jerus's older cities' `slot-NN-history.json` reach 25 MB: at that size the
  stall is estimated at 300-350 ms (linear in the bytes; not measured, since
  none of his slots loads in this build).
- A load of the month-4,000 city took 105 ms; loading is not the problem.
- **The Earth playtest agrees.** On Rotterdam, `LongPlaytest.earthFounding`,
  the autosave was 17.7% of the FX thread at month 1,500 and 27.4% at 3,000.
  The history was 9.9 MB there, and the autosave's month cost 142 ms.

The probe as run, and its trimmed output, are beside this brief:

- `docs/queue/UP1/probe/` holds `UiCostProbe`, `UiGrowProbe` and
  `JfrCallers`, the scratch sources. They were never in `src/`.
- `default-m1000-4000.txt` and `earth-rotterdam-m1500-3000.txt` are the
  output.

It was run from the clone's root like this:

    APPDATA=<scratch> java -XX:FlightRecorderOptions:stackdepth=512 -Dprobe.out=<dir>
        -cp "<probe classes>;target/classes;<gson>;<javafx-base, -graphics, -controls 21 -win jars>"
        ham.citybuildersim.ui.UiCostProbe 1000,2000,3000,4000 3

For the Earth run, add `-Dcbs.earth=<earth-1.bin> -Dprobe.earth=rotterdam`.
Setting `APPDATA` to a scratch folder keeps the window's preferences off the
real save folder; the city itself is in a `GameFiles.scratch()` folder.

## What changes

1. **The write leaves the clock.** `Game.save()` keeps filling `DataSave` and
   asks the map for its sidecar bytes on the FX thread, as now (the model is
   only safe to read there). What is slow - turning `DataSave` and
   `HistorySave` into JSON and writing the three files - moves to one
   background writer thread (a small model class, say `SaveWriter`, no
   JavaFX). It owns the thread and the dials.
   - **The history is snapshotted, not shared.** The writer is handed a copy of
     every series list (references to immutable `Double`s; about 600,000 at
     month 4,000, an array copy), never the live `HistorySave`.
   - **`DataSave` is audited the same way.** Any setter that keeps a
     reference to a live model array or list copies it. Where the audit is
     not sure, the setter copies; `SaveFileCheck` holds the round trip.
2. **The order is kept.** Save, then map, then history, as now. A
   newer autosave asked for while one is writing replaces the one waiting and
   never runs beside it: one writer, one file at a time. A save to a numbered
   slot, Quit, a load and "before skipping" all wait for the writer to be done
   first. The player's own Save waits too, and its result is shown as now.
3. **Gson streams.** It writes through a `Writer` (`gson.toJson(obj, writer)`
   into a buffered file stream) rather than building a 13 MB `String`. The
   history is written compact (no pretty printing): nobody reads 13 MB of
   numbers by eye, and `tools.SaveDump` reads it either way. The save file
   stays pretty. The `.tmp`, `.bak` and atomic-move rule of
   `GameFiles.write()` is kept for the streamed write.
4. **A failure is not lost.** A write that fails on the writer thread is
   printed (the log) and reported on the FX thread as today's
   `Autosave failed:` line is.
5. **The probe goes in the tree.** It becomes `ui/UiProbe.java`, a `main()`
   run by hand on the PC, not in `AllChecks`: it opens a window for about a
   minute, and the cloud has no display. Where JavaFX cannot start, it says
   so in one line and exits 0. It:
   - grows the playtest's city to the months it is given;
   - times each rail screen's build, and CSS with layout, per redraw;
   - runs the real clock at 50x on each screen, giving months a second and
     frame p50, p95 and max;
   - profiles the FX thread with JFR, giving inclusive shares for the clock,
     `nextMonth`, the autosave, the chrome, the history series, the charts,
     the map and the pulse.

   UP2 and UP3 measure against it. Its figures go in this batch's notes, not
   in the changelog.

No save-format change: the files are the same JSON, and Gson reads the
compact history as it read the pretty one.

## The harness

`SaveSlotCheck`, a new section after 7b, "the autosave off the clock". Its
fixture causes the condition: a city whose history holds
`HistorySave.MONTHLY_KEPT` months, recorded month by month or padded through
the history's own recording path, never a hand-built file.

- The month that autosaves costs the clock at most `SaveWriter.CLOCK_MS` (a
  dial, one sentence above it; about 25 ms on the PC) through
  `Timing.within`. The same month before the batch costs about 170 ms or more.
- The autosave written in the background loads into the same city as a
  `saveGame()` made the old way on a copy: the save's fields, every history
  series and the map, compared exactly.
- Two autosaves asked for back to back write once or twice, never at the
  same time, and the file on disk is always whole, read back after each.
- Quit, a numbered-slot save and a load each wait for the writer:
  - a load straight after an autosave reads that autosave;
  - Quit's autosave is on disk when `toggleQuit()` returns.
- A write that throws (a read-only folder in `TempDirs`) is reported, and
  the next one still writes.

Also `SaveFileCheck` and `ReadPathCheck`, unchanged: they must stay green,
since they catch a field the snapshot forgets.

## Files

- `Game.java`: `save()`, `autosave()`, the load path and `toggleQuit()`.
  Read them with `Where`.
- `DataSave.java`: `saveGame()` and the setters the audit finds.
- `HistorySave.java`: `saveHistory()` and a `snapshot()`.
- `GameFiles.java`: a streamed `write`.
- New `SaveWriter.java`.
- `ui/UserInterface.java`: the Save dialog waits on the writer.
- `SaveSlotCheck.java`.
- New `ui/UiProbe.java`.

## Risk

- **A race.** A setter that keeps a live reference would write a save from
  the middle of the next month. The audit, `SaveFileCheck`'s round trip and
  the fixture's exact comparison are the guard.
- **The waits.** An autosave left writing when the player loads, quits or
  saves to a slot must finish first. Each of those is asserted.
- **The cadence.** At 50x the writer is still asked every 1.2 s, and at
  month 4,000 writes about 17 MB each time on another core. Whether to also
  hold the autosave to a floor of real time is Jerus's call (below). The
  batch leaves the cadence as it is unless he says otherwise.

## Calls for Jerus

- At 50x the autosave comes every 1.2 s of real time (every 12 game
  months). Once it is off the clock it no longer stalls the screen, but it
  still writes about 17 MB at month 4,000, 25 MB or more in his cities, every
  1.2 s. Keep every 12 months? Or every 12 months but at most once every 30 s
  of real time? The floor would lose up to 30 s of play on a crash at high
  speed. At 1x nothing changes, since 12 months take 60 s.
