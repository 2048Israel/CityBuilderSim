# TR2 - The ghost cursor and Follow, in replay

Queued 2026-10-31 by TR1, which shipped the recorder, replay mode, cinematic
mode, the camera and the graphs behind the city, and left section 6 of its
brief (`docs/queue/TR1.md`, "The ghost and follow") whole for this batch.
Jerus's words are there: "perhaps a ghost mouse if you go to a screen where
the player was.. along with their actions? with a follow mouse button, so if
they click on something else the navigation follows?"; on the ghost's clicks,
"Re-enact it (Recommended)".

## What is there to build on

- **The input log** is written from TR1 on while "Record this city" is on:
  `recordings/<id>/input-<epoch ms>.bin.gz`, one per sitting (`InputLog`:
  the format, its writer and `read()`; `ui/InputRecorder` writes it). It
  holds the mouse (moves at most 60 a second, drags, presses with button,
  clicks and modifiers, releases, the wheel), the keys, and the markers: the
  window's scene size, the screen open (`UserInterface.currentScreen`), the
  expanded map's view, the clock's speed and pauses, and each month turning,
  every event at its millisecond of the sitting. ReplayCheck 4 round-trips it.
- **Replay mode** (`ui/ReplayMode`, `ReplayCity`): the copy, the time bar,
  whole loads at rest and quick steps while moving, cinematic mode.

## What to build (TR1's brief, section 6)

1. **The ghost cursor:** a see-through pointer in a colour of its own, with a
   ripple on each click, drawn at the player's logged position for the moment
   on view, whenever the screen on view is the one the player had open then.
2. **Follow:** plays the recording in real time of play, at a chosen speed,
   idle stretches skippable; feeds the logged input into the copy's scene
   (tabs switch, pages scroll, the map pans and zooms, hover cards appear,
   clicks land); months turn when the log says they turned, by loading that
   month whole; joining mid-month loads the month and plays its input from
   its start without drawing up to the moment; any input of the viewer's
   turns Follow off; Save, Load, New city, Quit, the Menu's ways out, the
   clock's keys and replay's own keys are dropped from what is re-enacted.
3. **The same layout:** Follow sets the window to the logged size and says so
   when the screen is too small for it (TR1's 1920 x 1080 button shows how).
4. **Which sitting:** a month may be covered by more than one sitting after a
   rewind; the latest sitting whose month markers reach it is the timeline
   the recording holds.
5. **If TR1's screen recordings show it:** carry the district plans across
   replay's quick steps (TR1 notes, call 3: a new `CityMap` each step starts
   with no pictures, so a big city's map lags a few months while the bar
   plays; about 150-250 lines inside `CityMap`'s plan chain and a harness).

## The checks

ReplayCheck gains: a logged sitting replayed into a headless copy reaches the
logged screens in order; a re-enacted Save, Load or Quit writes and closes
nothing (TR1's section 4 holds the model's half); joining mid-month lands on
the same screen as playing from the month's start. Eye checks for Jerus: the
ghost follows and re-enacts a decision on the Policy screen.

## TR1's review, left for this batch

TR1's stranger's review (2026-11-01) passed it with four follow-ups, none of
them able to touch a save slot or a city that does not record:

1. **The input log after a failed write:** `InputLog.Writer.add()` keeps
   queueing once a write has failed (line 397 sets `finished` but `add()`
   does not read it), and the failing path leaves `out` open. Check
   `finished` in `add()`; close `out` in a `finally`.
2. **A recording id read from a save:** `Game.recordingDirectory()` (line
   15735) resolves the saved `recordingId` under `recordings/` as it stands.
   Accept only the shape the recorder writes, so a hand-edited save cannot
   point the recorder outside that folder.
3. **The first write after a load:** a recording loaded and played on whose
   first month's write fails goes on with the old timeline's later months
   still on disk; make that write's failure end the recording (or drop the
   later months first), so one recording never holds two timelines.
4. **Autosave months:** a recorded city snapshots twice in a month that also
   autosaves; take the month's snapshot once and hand it to both.

ReplayCheck gains one assertion for each of 1-3.
