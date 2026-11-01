# TR1 - Replay a recorded city, to film the trailer

Queued 2026-10-31 at the top on Jerus's word, to run on the side of the
lanes now: "since its camera tools... do you think it can be done right now
on the side? ... if so top of queue and message the orchestrator". One batch,
his call ("One big batch" over two).

He is about to make the game's trailer and needs camera tools in the game.
His plan for it, a 3:15 track:

    0:00-0:54 The city's first streets and buildings while the track builds
    0:54-1:35 The city expands fast, with GDP and population graphs rising faintly behind it
    1:35-1:55 Growth stalls
    1:55-2:10 The economy collapses and the player makes emergency decisions
    2:10-2:25 A false calm in the quiet stretch
    2:26-2:40 The currency peg breaks, peaking on the big hit at 2:40
    2:40-3:12 The camera pulls out from the city to the whole Earth
    3:12-3:15 Title card as the track ends

## What he asked for, in his words

- "you record, then just open that recording and click whatever you want
  inside the city and record whatever you want, just no actual clicking that
  changes the game, just navigation ... with a button to hide the cursor"
- "you just replay the city in navigation mode however times you want, been
  able to reverse time, you just can navigate, and then record the screen or
  specific areas"
- "allow cinematic mode button when on navigation mode"; for the 1:55 beat,
  the real screens (not cards drawn over the map).
- "nothing sticks... perhaps a ghost mouse if you go to a screen where the
  player was.. along with their actions? with a follow mouse button, so if
  they click on something else the navigation follows?"; on the ghost's
  clicks, "Re-enact it (Recommended)".
- Camera tools, all four offered: smooth zoom and pan, A->B glide, fly out
  to Earth, graphs behind the city.

He films with his own screen recorder (OBS, or Snipping Tool's region
recording) and edits to the music in his video editor; the game does not
make video, and nothing here is timed to the track.

Why a recording and not a live take: a peg breaks by chance
(`ForeignAccounts.BREAK_BASE`, `BREAK_SLOPE`), a collapse comes when it
comes, and a big city's month takes time. He plays a city once, reloading
earlier saves until the story happens, and films the recording as often as
he likes.

## 1. The recorder

- **A switch per city**, "Record this city" in the Menu, off by default. It
  is kept in the city's save: a recording id and the switch, saved through
  `DataSave`, the load path and `SaveFileCheck`. No format bump: a save
  without the key loads with recording off.
- **Every month** while it is on, beside the autosave at the month's end
  (`docs/month-order.md` 164, `Game.autosave`), write that month's city: the
  state as a save writes it, and the map sidecar, compressed. Write them on
  `SaveWriter`'s thread from the snapshot a save already makes, so the month
  pays nothing. Measure and report this.
- **The history** (6 to 27 MB in his saves) is written at intervals and when
  he quits or stops recording, not every month. The replay cuts it at the
  month on screen.
- **Where:** `%APPDATA%\CityBuilderSim\recordings\<id>\`, through a
  `GameFiles` method. Never a save slot, and never slots 1-9.
- **Sizes measured on the PC, 2026-10-31:** `autosave.json` 4.7 MB,
  `autosave-history.json` 6.2 MB, `autosave-map.bin` 25 KB. Report a
  recording's size per month after compression for a mid-sized city.
- **Rewind:** loading an earlier save of a recorded city and playing on
  rewrites the recording from that month. The recording is always the
  timeline last played; months past the new one are deleted when the first
  new month is written.
- **The input log**, kept from the start so the ghost has something to play
  back:
  - Through an event filter on the window's scene, log the mouse (moves at
    up to 60 a second, presses, releases, drags, the wheel) and keys, each
    with its moment in milliseconds of real play.
  - Log markers beside them: the window's size; the screen or tab open; the
    expanded map's view (centre, scale, roll) when it moves; the clock's
    speed and pauses; and the moment each month turned.
  - Compressed, appended per sitting.

## 2. Replay mode

- **Opened from the Menu,** "Replay a recording": a list of recordings (the
  city, its months, its size). Leaving replay returns to the title.
- **A throwaway copy:** the month on screen is loaded into a game whose
  folder comes from `TempDirs.create`. Never `new Game()`, which points at
  the real save folder.
  - Nothing sticks: any click may change the copy, and moving in time
    reloads from the recording.
  - The clock never runs, autosave never fires, and Save, Load, New city and
    Quit-with-save are gone or do nothing. The real save folder and the
    recording are never written.
- **The time bar** across the bottom:
  - drag to any month;
  - play forward or backward at a speed chosen from about 1 to 16 months a
    second;
  - step a month either way; keys for each.
  - While playing, only the map sidecar (tens of KB) and the history are
    swapped, so play is fast; when the bar stops, the full month loads, so
    every screen (budget, policy, charts, the land office) shows exactly
    that month. Measure a month's full load. If it is fast enough, load in
    full at every step and say so.
- **Every screen** draws its charts up to the month on screen.

## 3. Cinematic mode

- **One key and a button** in replay hides the window's header, the rail,
  the minimap, the map's legend, hint and scale bar, the hover card, the
  time bar and the mouse cursor. The ghost (6) stays unless switched off.
  The same key, or Esc, brings it all back.
- **A button** sets the window's content to exactly 1920 x 1080 for the
  screen recorder. Follow (6) sets the size the player played at instead.

## 4. Camera tools (in replay only; normal play is unchanged)

- **Smooth zoom and pan:** the wheel's notches and WASD ease in and out,
  gliding to the target with the scale moving in proportion. A drift speed
  gives the slow constant move a still shot needs.
- **A->B glide:** mark view A and view B (centre, scale, roll, and the
  month, so time can move during the glide: the 0:54-1:35 shot). It glides
  between them in N seconds he sets, easing in and out, with the scale
  moving in proportion. Before it starts it paints every tile along the
  path, so nothing appears late on the recording.
- **Fly out to Earth:** one key pulls out continuously from the view to the
  whole planet and another flies back in, at a set speed. `GlobeFrame`
  already crossfades the city's plane to the globe and eases the roll; the
  fly drives its centre and scale.
- **The arithmetic** (the eases, the proportional scale, the path, the
  fly's curve) goes in a model class without the toolkit, as `GlobeFrame`
  and `MapFrame` are, checked by the harness; `MapView` is the toolkit's
  half.
- **Pop-in:** the map paints at most `MapView.FRAME_MS` (8 ms) of tiles a
  frame and shows a coarser picture meanwhile. Replay computes no month, so
  give it a larger budget there, and look at `CityMap.PICTURES_KEPT` (128)
  for long glides.
- **Boats, trains and trucks** keep moving in replay. They draw from
  schedules made on a game month (`BoatSchedule`, `TrainSchedule`,
  `MapView.boatsLive`); make sure they run on a loaded month with the clock
  stopped.

## 5. Graphs behind the city

A key toggles faint GDP and population lines over the map, each drawn up to
the month on screen and scaled to the whole recording, so a line rises as
time plays (the 0:54-1:35 shot). The look is the batch's; put renders to
Jerus.

## 6. The ghost and follow

- **The ghost cursor:** a see-through pointer in a colour of its own, with a
  ripple on each click, drawn at the player's logged position for the
  moment on view. It shows whenever the screen on view is the one the
  player had open then.
- **Follow** plays the recording in real time of play, at a chosen speed,
  with idle stretches skippable:
  - It feeds the logged input back into the copy's scene, so tabs switch,
    pages scroll, the map pans and zooms, hover cards appear and the clicks
    land: the ghost clicks a policy chip and the chip moves. This is how the
    1:55 beat is filmed on the real screens.
  - Months turn when the log says they turned, by loading that month's city
    (which already holds the decisions made in the month before).
  - Joining in the middle of a month loads the month and plays its input
    from its start without drawing, up to the moment.
  - Any input of the viewer's turns follow off.
- **Dropped from the replay:** Save, Load, New city, Quit, the Menu's ways
  out of the city, the clock's keys and speed, and replay's own keys. With
  the copy throwaway, a misplaced click changes nothing that lasts.
- **The same layout:** re-enacted clicks need it, so follow sets the window
  to the logged size and says so if the screen is too small for it.

## 7. The checks

A harness of its own (sections numbered from this list) and its line in
`AllChecks`; fixtures on `GameFiles.scratch` or `TempDirs` only.

1. A scratch city recording for 24 months writes one month each. A reload
   at month 12 and play-on rewrites from 13. Nothing is written in the save
   slots.
2. Replaying month N gives the figures the city had at month N (population,
   GDP, cash, the map's counts), kept while it ran.
3. Nothing sticks: a policy changed in replay is back to its recorded value
   after a step away and back, and the recording's files and the save
   folder are byte for byte unchanged.
4. The input log round-trips. A logged Save, Load or Quit re-enacted writes
   and closes nothing.
5. The camera's arithmetic: the glide's ends are exact, its middle scale is
   the two ends' geometric mean, its ease is symmetric, and the fly reaches
   the globe's whole view.
6. The month's time with recording on against off: the cost stated, inside
   the timing budgets.

Eye checks go to Pending for Jerus: cinematic mode hides everything; a
glide and the fly to Earth look smooth on a screen recording; the ghost
follows and re-enacts a decision; the graphs behind the city.

## Overlaps and what is not here

- **Overlaps with the other lanes:**
  - WN6 (the nations' screens) and WN7 (ships and trains to partners, in
    `MapView`) are queued behind WN5; LD1, IC1 and ST2 are in the model.
  - Keep TR1's hooks thin, each under its own banner, in `UserInterface`,
    `MapView`, `MiniMap`, the month's end and the load path, so a merge with
    WN6 and WN7 is mechanical.
  - The rest goes in new files.
- **The manual** gets a section on recording and replay; it is a player's
  feature as much as the trailer's, in plain words.
- **Not in TR1:** writing a glide out as image files frame by frame. A
  later batch, if the screen recorder shows stutter or pop-in that the
  larger paint budget and the warm-up do not cure.
