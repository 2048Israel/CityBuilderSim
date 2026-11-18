# TR2-G - replay's copy leaves the player's prefs alone (2026-11-18)

Built on main at 0.9.144 (1cb13fffb). The row: in replay's copy a click on the
drawer's pin or the construction tab changed the in-memory `GamePrefs`, which
the next prefs save took to the player's folder (TR2's review, item 6; TR1's
viewer clicks did the same since 0.9.77). Written whole by Opus; no slice.

## What I read and found

`UserInterface.prefs` is one object, read and written by every screen as
`ui.prefs`, and the copy is the same window: `ReplayMode.enter` only swaps
`ui.game`. So a click in the copy set the player's values. The disk was reached
in two steps: a prefs save inside the copy goes to `game.getGameFiles()`, which
is then the copy's scratch folder (harmless), but the changed values stayed in
memory, and the first prefs save after leaving (any click outside) wrote them
to the player's `settings.json`. Every writer, by grep of the setters: the
pin and `constructionTab` (`UserInterface`), the settings menu's two
(`pauseOnEvents`, `trucksOnMap`), F11 (`fullScreen`), the Reports' pins
(`HistoryScreen`), the dashboard toggle (`SummaryScreen`), the map's four
toggles and its legend (`MapView`). All go through `ui.prefs`; nothing keeps
another reference to it. Follow's re-enactment fires the same handlers
(`FollowRules` drops only the menus, the clock and F11); the viewer's own
clicks the same.

## The mend: one seam

- `GamePrefs.copyForReplay()`: a copy with the player's values (a Gson round
  trip) marked `detached` (transient, not in the file); `save` on it does
  nothing and says no, to any folder.
- `UserInterface.replayHoldPrefs()` / `replayReleasePrefs()`: set the player's
  object aside and give the window the copy, and put it back.
- `ReplayMode.enter` calls the first just before `ui.game = rc.game()`, `leave`
  the second just before `ui.game = realGame`: the only two places the window
  changes hands (`go`, `afterWhole` keep the copy's Game, same prefs).

No handler is guarded, so a writer added later is covered too. The player's own
clicks outside a replay see his object, as before.

## Does anything keep its own reference to the player's prefs? (the orchestrator's question)

No. By grep of every `GamePrefs` and `prefs` mention outside the harnesses and
probes: the only holders of the object are `UserInterface.prefs` and the new
`playerPrefs` (the one set aside). `MapView`, `HistoryScreen` and `SummaryScreen`
take `ui.prefs` at each read and write (no local, no field, no argument; two
null checks at `MapView` 4459 and 4761), the pin and tab handlers and the two
settings toggles in `UserInterface` read the field at the click, and the model
classes that name `GamePrefs` (`GameVersion`, `TruckSchedule`, `WorldEconomy`)
do so only in comments. No prefs *value* is cached either: every `isXOnMap()`
and `getPinned...()` is read inside the draw or the click, so after leaving the
next paint reads the player's. `ReplayMoveProbe` and `UiProbe` load a prefs of
their own to set up a window, not a component of the game. The window's
`UserInterface` is built once and `ui.prefs` swapped on it, so the swap covers
every reader; ReplayCheck 11 shows the pin and the tab, and its sweep of every
other setter shows his object untouched, which holds for the rest by the same
field.

## The harness: ReplayCheck 11

The model half (headless): the copy holds his values and is marked, a change to
it leaves his in memory, its save writes nothing to his folder or any other, his
own still saves. The window half (`FollowBench.prefsCheck`, skipped where JavaFX
cannot start; 11 findings, 1.4 s; the prefs folder is the city's scratch one):
the pin and the tab clicked outside the copy save to his file; in the copy they
pin and open the panel, and with every other setter flipped and a prefs save,
his in-memory values and his file are as they were and the copy's folder holds
no `settings.json`; after leaving, his values are the window's, the first prefs
save leaves his file as it was, and his clicks save again. Fails with the mend
removed (each run alone, the source put back after):

- no `replayHoldPrefs()` in `enter`: five window lines red (the copy's changes
  in his memory, leaving, the first save after leaving, and the clicks after);
- no `detached` check in `save`: the model's "save says no" and the window's "no
  prefs file in the copy's folder" red;
- every save suppressed (the other wrong mend): his own save, the clicks outside
  and the clicks after the replay red.

The last run also showed a flaw in my first draft, mended: a missing file threw
out of `main` and the toolkit's threads kept the JVM alive; the reads now go
through `ReplayCheck.bytesOrNull`, so a broken mend reads FAIL.

## Calls (★)

- ★1 **The copy's prefs also never write to the copy's own scratch folder.** The
  scratch folder was harmless, but "writes nothing, anywhere" is one rule a
  later path through the real game's files cannot break. Recommended: keep.
- ★2 **The viewer's F11 in a replay is not remembered**: it toggles the window,
  the copy's `fullScreen` takes the value and leaves with the copy. The window
  is the film's there (Follow fits it to the logged size itself); before, it was
  saved only by accident, with the next unrelated save. Recommended: keep.
- ★3 **The window half fires MOUSE_CLICKED at the pin and the tab nodes**, not
  through the Reenactor's pick: the seam does not depend on how a click arrives
  (ReplayCheck 10 shows the Reenactor reaching handlers). Recommended: keep.

## Checks

`./batch-check.sh --fast` green. Pick (`tools/pick.sh origin/main HEAD`): 8 of
219 - BuildMenuCheck, ClockCheck, LandCheck, MapCheck, ReplayCheck, TimingCheck,
TruckRouteCheck, ZoningViewCheck - exit 3, the only reds MapCheck's four, main's
FB9 rows, unchanged by this batch. The pick is not ALL: no dry run. Review line:
`review needed - threading: ui/UserInterface.java (UI-wide)` - the change there
is two package-private methods and a field, no thread or listener.

**No model change**: the default playtest's trace cannot move (replay only), so
no ensemble. Batches queued: none.
