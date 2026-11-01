# TR1 - Replay a recorded city, to film the trailer (0.8.92, 2026-10-31)

The brief is `docs/queue/TR1.md`. One batch, as Jerus chose ("One big batch"),
run on the side of the LD1 / EP1 / WN lanes; ghost and follow (brief section
6) are queued as TR2. Built in three Sonnet slices (the model, the screen, the
camera), each reviewed here and committed on its own.

## 1. What works

- **Record this city** (the Menu, while a city is open; off by default). The
  switch and an id ride the save (`DataSave.recordingId`, `recording`; no
  format bump - a save without the keys loads with it off). While on, every
  month's end (`Game.nextMonth()`, right after the autosave block, before the
  markets close the month) fills the save as the autosave does, snapshots it
  and hands SaveWriter a task: the month's file is written on SaveWriter's
  thread. The history goes at the autosave's pace from the autosave's own
  snapshot (every 12 months, never sooner than 30 s, and Quit's), at the first
  month of a sitting, and when recording is switched off.
- **Where:** `%APPDATA%\CityBuilderSim\recordings\<id>\` (`GameFiles.
  recordingsDirectory()`, beside `saves\`): `month-0000123.bin` per month (the
  map sidecar first, then the save's compact JSON gzipped), `history.json.gz`,
  `recording.json` (the meta), `input-<epoch ms>.bin.gz` per sitting. The id is
  the city's name and the moment, "medellin-20261031-142355". Nothing is ever
  written in `saves\`.
- **Rewind:** loading an earlier save of a recorded city and playing on - the
  first month written deletes every month past it. The recording is the
  timeline last played (ReplayCheck 1).
- **The input log** while recording: the mouse (moves at most 60 a second,
  drags, presses, releases, the wheel) and the keys, each at its millisecond
  of the sitting, and the markers - the window's size, the screen open, the
  expanded map's view, the clock's speed and pauses, each month turning.
  `InputLog` (model) is the format, its writer thread and its reader;
  `ui/InputRecorder` feeds it. Nothing plays it back yet: that is TR2's ghost.
- **Replay a recording** (the Menu): the list (city, months, size, started);
  a click opens its first month on a **throwaway copy** (`ReplayCity`: its own
  `GameFiles.scratch` folder, slot 10 there, marked a replay copy, which never
  records). The real game is put aside and given back untouched on leaving;
  leaving goes to the title, where Continue returns to the real city.
- **The time bar** across the bottom: step back, play back, play/pause, play
  forward, step forward, the speed (1, 2, 4, 8, 16 months a second), a slider
  over every recorded month, the date and month. Moving in time swaps only
  the map's sidecar (`stepQuick`, 2 ms to read); when the month has rested
  300 ms it is loaded whole, so every screen shows exactly that month and its
  charts end there (the history cut at it, `HistorySave.cutAt`). A land
  purchase between two months makes the quick step fail and that month loads
  whole at once (a pause of one load).
- **Cinematic mode** (C, or the bar's button; C or Esc back): the header, the
  rail with the minimap, the construction panel and tab, the toasts, the
  inbox's list, the time bar and the cursor go; on the expanded map its head,
  hint, legend, scale bar, hover card and padding go and the canvas fills the
  window. The page on show stays (the 1:55 beat is filmed on the real Policy
  and Budget screens). **1920 x 1080** sets the window's content to exactly
  that, or says the screen cannot hold it (F11's full screen on a 1080p
  screen is 1920 x 1080).
- **The camera** (replay only, the expanded map; M opens it): the wheel and
  W/A/S/D ease in and out; Shift+W/A/S/D add a constant drift, R/F a slow
  zoom drift, X stops everything; 1 and 2 mark views A and B (centre, scale,
  and the month), 3 glides A to B over the seconds on the bar (default 10),
  time moving with it when the months differ, after a warm-up walk along its
  path; O flies out to the whole Earth and I flies back. The arithmetic is
  `CameraPath` (no toolkit): an eased glide whose scale is geometric (the
  middle is the ends' geometric mean) and whose ground crosses the screen at
  an even pace, the fly at `FLY_DOUBLINGS_A_SECOND`, the smooth hand.
  `MapView` only applies it; in replay a frame paints 24 ms of tiles, not 8.
- **The graphs behind the city** (G): faint GDP and population lines over the
  map, from the recording's first month to the month on screen, scaled to the
  whole recording, so they rise as time plays; they stay in cinematic mode.
- **Boats, trains and trucks** keep sailing in replay: the map's clock is the
  copy's month plus real time.

Keys in replay: Space play/pause, B the direction, Left/Right a month, Up/Down
the speed, Home/End the ends, C cinematic, M the map, 1/2 mark A/B, 3 glide,
O/I fly out/in, Shift+WASD and R/F drift, X stop, G graphs, Esc stops a move,
then leaves cinematic, then is the replay's menu (Back, Leave, Quit - no Save,
Load, New city or Continue).

## 2. Measured

| | small fixture (848 people, ReplayCheck) | Medellin, 3,300 people, month 360 | Medellin, 78,000 people, month 1,200 |
|---|---|---|---|
| a month's file (gzip 6) | 184 KB | 223 KB | 322 KB (1.2 MB of JSON) |
| the history file | 16 KB | 168 KB | 766 KB |
| the recorder on the FX thread, a month | 2.3-3.3 ms mean, 7.7 most | 4.3 ms (the fill) | 3.5-5.2 ms (the fill) |
| the writer's time for a month | 48 ms (gzip 6), 20 ms (gzip 1, 207 KB) | 227 ms cold | 116 ms cold |
| a whole load in replay | 170-320 ms | 330-800 ms | 210-380 ms |
| a quick step (the map read) | 2 ms | | |

Jerus's own city (save 4.7 MB pretty-printed, history 6.2 MB, map 25 KB; a
pretty save is two to four times its compact JSON) should come to roughly
0.3-0.5 MB a month: a 600-month recording is some 200-300 MB, plus the
history once. Gzip 6 was kept: level 1 is 12% larger and saves only the
writer's time, which is off the clock. At the top speed a big city's month
(about 0.1 s of real time at 50x) is near the writer's time for it, so the
recorder can fall behind there and leave gaps (the log says so); 20x or
gzip 1 (`Recording.DEFLATE_LEVEL`) would close them.

**Recording does not change the month.** ReplayCheck 6 plays two identical
fixtures 12 months, one recording: population, cash, GDP and the price level
equal to the last digit. Recording is off by default, so the default
playtest's trace cannot move: no ensemble run.

## 3. Threading and saves (for the gate)

- The FX thread fills the save and snapshots it (`DataSave.snapshot()`, the
  autosave's own) and submits; SaveWriter's one thread writes. `SaveWriter`
  now takes a `Task` (`Job` is one); the recording's months and history ride
  the same thread as the autosave, one file at a time.
- **A recording never blocks the clock's month:** past `RECORD_BACKLOG` (6)
  files waiting, the clock's month is not recorded (a gap, counted, one log
  line a run; replay steps over gaps). Inside a skip (`simulateMonths`, which
  already holds the window until it ends) the month waits for the writer, at
  most `RECORD_WAIT_MS`, so a skip records every month. ★ below.
- The input log has a daemon thread of its own (`input-log`): SaveWriter
  replaces a waiting job for the same file, so an appended chunk would be
  lost. The FX thread only queues events; only that thread touches the
  stream; it sync-flushes every 2 s and is closed before Quit's exit and in
  `Application.stop()`.
- **Replay never writes the player's folder or the recording:** the copy's
  `GameFiles` is a `TempDirs` folder; `markReplayCopy()` keeps it from
  recording and `replayMap`/`replayHistory` refuse a game that is not a copy.
  ReplayCheck 3 and 4 hash every file of the recording and the city's folder
  before and after a replay that changes a policy, reloads, and calls Save,
  Load and Quit's save on the copy: byte for byte the same. In the window,
  the replay's menu has no Save, Load or New city, the clock never runs, the
  time skip is refused, and Quit leaves the replay first so the real city is
  the one autosaved. Prefs written in replay (F11, the legend's fold) land in
  the copy's scratch folder.
- Loads run on the FX thread, never on a worker (a full load takes the
  window, as the Load menu always has).

## 4. Calls (★)

- ★1 **A skip waits for the recorder** rather than leaving gaps (the clock's
  months never wait). Recommended: keep - a skip is one click that already
  holds the window; the alternative is a recording of a long skip with most
  of its months missing. The other way is one line in `recordForReplay()`.
- ★2 **Quick steps swap the map only, the history at rest.** The brief had
  both swap while playing; `cutAt` copies the whole history (Jerus's is 6-27
  MB), too slow at 16 months a second. The graphs behind the city read the
  recording's history directly, so they move while playing; the screens'
  charts catch up when the bar rests. Recommended: keep.
- ★3 **District pictures are drawn again each month while playing** (a quick
  step makes a new `CityMap`, whose pictures start empty). MapView now keeps
  its tile pictures across the step and repaints only tiles whose content
  moved (1-4 of 32-308 in view on the fixture), but the district plans are
  redrawn per step, so on a big city the map lags a few months while the bar
  plays and catches up at rest. Carrying plans across maps is 150-250 lines
  inside `CityMap`'s plan chain: TR2 if the screen recording shows it.
- ★4 The glide's month moves linearly in time while the camera eases, so the
  city grows at a steady pace during the 0:54-1:35 shot. Recommended: keep.

## 5. Not here

- **TR2** (queued): the ghost cursor and Follow (brief section 6) - play the
  input log back in real time, re-enact clicks on the copy, follow the
  logged window size; and carrying district plans across quick steps (★3) if
  the recording shows the lag. The log is written from this build on, so a
  city recorded now will have its ghost.
- **The manual's section** (pending, TR1-M): the text is below, for the docs
  pass to put in the published manual (the page is generated from the
  artifact; other lanes publish it too, so this batch did not).
- Writing a glide out frame by frame: not TR1 (the brief's last line).
- A recording whose history was last written before a currency reform shows
  months before the reform in the new unit; rare, said here.
- The window's close button (X) does not autosave (it never did), so a
  recording's last history write can be up to 12 months behind; Quit writes
  it.

## 6. The manual's section (for the docs pass)

**Recording and replay.** Turn on *Record this city* in the Menu and the game
keeps every month of the city from then on, with what you clicked, in its
recordings folder (`recordings` beside `saves`). It costs a few milliseconds
a month and a third to half a megabyte a month on disk. Load an
earlier save and play on, and the recording follows the new story from there.
*Replay a recording* opens any recording on a throwaway copy of the city:
drag the time bar or play it forwards or backwards at 1 to 16 months a
second, open any screen, click anything - nothing you do there is kept.
Press C for cinematic mode, which hides everything but the picture, and use
1920 x 1080 to size the window for a screen recorder. On the map (M): the
wheel and W A S D glide; 1 and 2 mark two views, 3 glides between them
(through time too); O flies out to the whole Earth and I flies back; Shift
with W A S D, or R and F, set a slow drift; X stops; G draws GDP and
population faintly behind the city.

## 7. Files

New: `Recording`, `ReplayCity`, `InputLog`, `CameraPath`, `ReplayGraphs`,
`ReplayCheck` (sections 1-6), `ui/ReplayMode`, `ui/InputRecorder`,
`ui/ReplayCamera`, `ui/ReplayGraphLayer`. Hooks: `Game` (the banner THE
RECORDING; the month's end, the autosave, `prepareSave` split into
`fillSave`, `buildWorld`, `readTheSave`, a `skipping` flag in
`simulateMonths`), `SaveWriter` (`Task`), `GameFiles`, `DataSave`,
`HistorySave.cutAt`, `MapTiles` (a constructor carrying the ground),
`UserInterface` (a banner at the end and one-line call sites), `MapView`
(two banners at the end and one-line call sites), `LandScreen` (one method),
`SaveFileCheck` (a section), `AllChecks` and `Timing` (ReplayCheck's lines).
