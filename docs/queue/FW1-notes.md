# FW1 - a refused load leaves the city in play alone (0.8.37)

- **The cause**: `Game.loadGameSave()` called `buildWorld()` before `loadGame()` read the file, and the refusals
  (format below `OLDEST_LOADABLE_FORMAT`, from a newer build, a missing Earth, an empty or damaged file) are in
  `loadGame()`. A refused load from a running city left a blank one in its place (P7's leftover, todo-archive's
  FOUND ON THE WAY IN 0.7.107 TO 0.7.109).
- **The fix**: the three refusals are one method, `Game.refusal(slot, loaded)`; `refusalBeforeRebuild(slot)` reads
  the file and asks it (and the empty, unreadable and damaged cases) before the rebuild, and `loadGameSave()`
  returns with `loadFailure` set. `loadGame()` still asks it for its direct callers. `GameVersion.fromNewerBuild()`
  names the newer build, as `noLongerReadable()` names the older; the old "newer version" text is kept in it.
- **The check**: SaveFileCheck, "a refused save loaded into a running city leaves that city as it was": a city
  loaded at the floor is handed a save below the floor and one stamped by a newer build; its managers are the same
  objects, month and cash the same, the refusals the expected words, its save before and after equal but `savedAt`.
  5 FAIL with main's `Game.java`, all OK on the fix.
- **The trace**: the default playtest's log is byte-identical to main's; no ensemble (EN1).
