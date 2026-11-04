# CM1b - the replay's loaded move: the review's follow-ups

Queued 2026-11-04 by the orchestrator, from the stranger's review at CM1's gate (SHIP + FOLLOW-UP; threading, the
normal game and the probe all held). CM2, CM1's own, is the globe's coarse ground; this batch is the load's loose ends
in `ui/MapView` and `ui/ReplayCamera`/`ReplayMode`, each to be caused by a fixture in ReplayCheck before it is fixed:

1. **The load's store kept past its move.** Esc or X while "Ready - click to run" (`ReplayCamera.stop()`) does not wake
   the map, and leaving the replay while Ready (`MapView.forget()`) clears the caches but not `move`: the store's
   copies and the pins stay until the map's next frame. End the move (and unpin) in both, at once.
2. **A load can stall.** The 2 s give-up counts only the worker's idle time and remembers only `nodesAsked` and
   `chartsAsked`: a cell nobody asked for, or a `picturesAsked` entry that never clears, costs 2 s on every later view,
   and a job that fails and is asked again on every paint keeps the worker busy, so the give-up never fires. Remember
   every kind asked, give a failed job up, and put back an overall cap on a load (the old warm-up stopped at 8 s; a
   dial, `static final`, said on the card when it is reached).
3. **The memory check is a fixed 80% of the most heap.** `heapOver()` runs `System.gc()` on the FX thread on every
   finished view once over it, so a city already near 80% before the load fails every move or crawls through full
   collections. Measure against the heap at the load's start, and collect at most once a load.
4. **Other keys act during the load.** Keys other than Esc act on the load's half-way view and month (1 and 2 mark it,
   O and I fly from it, Left and Right step the month; `ReplayMode`'s key handler). Consume them while loading, as the
   click is; and a map lost mid-load returns to the move's start view, not the half-way one.
5. **ReplayCheck's cleanup.** `check()`'s `finally` stops ReplayMode's timer and closes the window but leaves the
   replay on and the map's and minimap's pumps registered, so a pump may keep ticking through section 6, which is
   timed. Turn the replay off and unregister both. 5d's Esc test covers the load, not Ready: add Ready.

(The notes' nit, a drag said to cancel a load, CM1 set right itself; its notes 5b list these five.)
