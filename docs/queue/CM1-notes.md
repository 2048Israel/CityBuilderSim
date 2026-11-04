# CM1 - the replay's camera loads its whole move first, then runs on a click ({next}, 2026-11-03)

**Where it stands (2026-11-04, 10:40):**
1. Ready, the ship file pushed last: slices A (the tally, the probe), B (the load), C (ReplayCheck 5d), on `batch/CM1`.
2. origin/main merged (0.9.10, WN5b and CB1; only the generated indexes conflicted, regenerated); `--pick` with the gate's world files green after it (exit 0: ReplayCheck 0 failures, 5d's glide ready in 7.8 s, 52 frames, unpainted 0, asks 0; TimingCheck); Stale 0 firm.
3. The measure: before section 1, after section 2 (no black, no worker asks, nothing let go in any of seven moves; the globe's coarse ground left, CM2).
4. For the gate: the stranger's review (threading, section 4); CM1-C, Jerus's eye check at 1920 x 1080, is the ship file's pending row.
5. Calls ★1-4 below, each taken as recommended.

## 1. Measured (before)

`ReplayMoveProbe` (slice A), Andorra, a scratch recording of months 366-484 (11,008 people), shown at month 484 unless the move says; the map's canvas 1,492 x 814, the window off screen (2026-11-03 run: about 23 frames a second there, against 40-46 full screen on 11-02, so the shares compare, not the counts). Unpainted = a frame drawing black (a tile or node with no picture yet) or coarse ground (the globe's 7.4 km planet picture where the plane should be).

| move | frames | unpainted | black | coarse | worker asks | let go |
|---|---|---|---|---|---|---|
| M1 fly out, street to Earth | 290 | 158 | 4,169 (FAR 80 of 80 frames, L2 6 of 6) | 42,023 (GROUND 65 of 65) | 637 | 0 |
| M2 fly in | 305 | 113 | 2,718 (FAR 57 of 80) | 39,899 (GROUND 54 of 68) | 533 | 0 |
| M3 zoom-out glide 10 s (warm-up 5.2 s) | 235 | 112 | 1,599 (FAR 59 of 62) | 32,897 (GROUND 53 of 55) | 637 | 0 |
| M4 zoom-in glide 10 s (warm-up 4.3 s) | 229 | 97 | 2,082 (FAR 50 of 61) | 31,226 (GROUND 47 of 52) | 637 | 0 |
| M5 glide in a month, 12 km east at street x4 | 218 | 1 | 0 | 21 | 0 | 180 |
| M6 glide across months 366-484 at the handoff /8 to /16 | 295 | **295** | 11,525 (FAR every frame) | 0 | **5,256** | 0 |
| M7 = M3 again | 236 | 116 | 1,573 | 31,985 | 637 | 28 |

What it says: (1) the black is the far nodes (FAR, about 1 plot a pixel to the handoff), in nearly every frame there, warm-up or not: the warm-up's 25 views reach none of them in time, and the worker is asked about 640 things a move while it runs; (2) a glide across months is black in every frame, because each month is a new map whose nodes are all asked again (5,256 asks); (3) the globe's GROUND tier draws part of the planet from its 7.4 km picture in every moving frame, since its pieces are asked only at rest; (4) the tile levels L0-L2 are clean after the warm-up, but M5 let 180 pictures go and M7's L2 drew stale ones: the caches hold one screen.

## 2. Measured (after slice B)

The same probe, the same recording and window (2026-11-03, off screen), the moves now loaded first and run as a click runs them; the tally counts the run only (the map is veiled while it loads).

| move | load | plan views | frames | unpainted | black | coarse | worker asks | let go |
|---|---|---|---|---|---|---|---|---|
| M1 fly out | 11.4 s | 3,165 | 295 | 51 (GROUND only) | 0 | 38,641 | 0 | 0 |
| M2 fly in | 4.6 s | 3,165 | 292 | 48 (GROUND only) | 0 | 36,603 | 0 | 0 |
| M3 zoom-out glide 10 s | 10.8 s | 2,401 | 229 | 40 (GROUND only) | 0 | 30,728 | 0 | 0 |
| M4 zoom-in glide 10 s | 9.1 s | 2,401 | 236 | 41 (GROUND only) | 0 | 31,489 | 0 | 0 |
| M5 glide in a month | 4.6 s | 2,401 | 215 | 0 | 0 | 0 | 0 | 0 |
| M6 glide across months 366-484 | 14.8 s | 2,401 | 288 | **0** | 0 | 0 | **0** | 0 |
| M7 = M3 again | 12.3 s | 2,401 | 232 | 42 (GROUND only) | 0 | 32,278 | 0 | 0 |

No frame of any move draws black, asks the worker anything or lets a picture go; the glide across months, black in every frame before, is clean. Every load reached Ready with no view passed; the heap stood at 510-720 MB after them (Java's most here about 8 GB). What is left is the globe's GROUND tier: past the plane's nodes and the charts, a moving globe draws the planet from its 7.4 km picture (beyond(): pieces are asked only at rest), in 40-51 frames of each move that crosses it - coarse ground, not black (★1). M6's worst frame is 259 ms (247 ms before): the quick step to a new month's map, not the load (★3).

## ★ Calls (taken as recommended unless Jerus says otherwise)

1. **The globe's coarse ground on a move.** Between the handoff and the planet picture's tier, a moving globe draws what lies past the plane's nodes and the charts from the 7.4 km planet picture. **Recommended: ship CM1 without it and queue CM2**: in a loaded move, widen the charts asked to the whole visible disc in the GROUND tier, so the load fetches them like the rest, measured by the same tally (coarse to 0). Alternative: fold it into CM1 now (a day more; the charts' range and their memory are the work).
2. **Half resolution on the move.** The globe is drawn at half resolution while it moves (82-110 frames a move), for the frame rate. **Recommended: keep it in CM1; CM2 measures full resolution in a loaded move at 1920 x 1080** and takes it if the frame rate holds.
3. **The month step's hitch.** A glide across months has one frame of about 250 ms (before and after CM1): a month's quick step. **Recommended: CM2 measures which month and why (it is the quick step, not the load), not CM1.** All three are queued as CM2 (`docs/queue/CM2.md`, in the ship file's queued row).
4. **5d's window takes the keyboard's focus.** ReplayCheck 5d opens the real window off every screen for about 15 s, and JavaFX gives a window the focus when it opens; a key typed on the PC then lands in the replay (a key runs a Ready move, Escape stops one), which could turn 5d red. **Recommended: keep it** (the gate runs unattended; a red 5d with a move stopped is a rerun, and its line says which figure); the other way is a hidden window, which JavaFX does not draw.

## 3. What was built

- **ReplayCamera** (slice B): the warm-up (phase WARM: 25 views, at most 1 s each and 8 s in all, the start month only) is gone; a glide (3), a fly out (O) and a fly in (I) are set up as before and go to phase **LOAD**, then **READY**, then GLIDE or FLY. `planViews()` = `ceil(seconds x PLAN_HZ) + 1` views of the move's own path (`PLAN_HZ` 240 a second, finer than the 165 Hz screen draws); `viewAt(t)` is the one path both the plan and the run take. The bar says "Loading the move..." for the first second, then "Loading the move... N s" (the seconds left, never jumping up by a second or two); then "Ready - click to run" (with how many views could not be loaded whole, if any). A click on the map or any key but a modifier runs it; Escape, a drag, W A S D or the wheel cancels it (a cancelled load puts the view and month back where the move begins). `busy()` holds the month and the clock while it loads or waits.
- **MapView** (slice B, "the move loaded before it runs (CM1)"): `loadStep()` takes the plan's views in order within each frame's 24 ms, paints each with the real `paint()`, and leaves it only when `loadSettled()` finds nothing missing (every tile in view at its level, every far node coloured and not waiting for pictures it would get, on the globe's GROUND tier the plane's nodes and every chart node in range, and nothing asked of the worker and not back). A view the worker sits idle on for `STUCK_S` 2 s is passed and its stuck asks noted, so a later view is not held by the same one. The caches are **pinned** (`Pictures.pinned`: nothing is let go for room) from the load's start to the move's end, then trimmed to their sizes. A move **across months** keeps each month's tile pictures and far nodes in a store (one copy per stamp; a month points at copies), and its run takes them (`takeTile`, `takeNode`) where a quick step left them stale, so it asks the worker nothing. A load over `LOAD_HEAP_SHARE` 0.8 of the heap stops and says so. While it loads the map is veiled by the words; when Ready, the map with the words over it.
- **ReplayMoveProbe** (slice A, run by hand): founds and records a city when its folder is empty, replays it in the real window off every screen (`-Dcbs.probe.onscreen=true` for full screen), runs the seven moves and prints `MapView.MoveTally` for each (slice A: per frame, per level, the cells drawn black, coarse or stale, the worker's tasks, the pictures let go).

## 4. For the gate's review: threading

Everything new runs on the FX thread: the load is a step of `tick()` (through `replayCamera()`), the caches it pins and the store are touched only there, and WORKER's results reach the caches through `Platform.runLater` as before (`nodes.put`, `charts.put`). WORKER became a `ThreadPoolExecutor` (one thread, the same queue and priority) only so the load and the tally can read `getTaskCount()`, `getActiveCount()` and `getQueue()`; what it runs is unchanged. A store copy is never painted over: on a miss `paint()` drops the tile's picture first and `takeNode()` lets the node's arrays go, so the normal paint makes new ones.

## 5. The normal game

Outside a replay `ui.replayCamera()` is null and MapView's `move` is null, so the lines the normal game runs gain only those null tests (`Pictures.removeEldestEntry`'s `pinned`, `paint()`'s and `paintNodes()`'s `moveRunning()`, `draw()`'s `drawLoading()` and `drawReady()`, the press handler's `replayRunsOnClick()`); `tick()`'s idle test reads its own condition where it read a field the warm-up kept. The model is not touched: no trace can move, so the 80-run ensemble is skipped (docs/batch.md 6). No process file changes (one in, one out does not apply).

## 6. The ship file (pushed last, once WN5b is on main and merged in: docs/batch.md 7)

The text of `docs/queue/CM1-ship.md`, between the lines:

---
## changelog
### {next} — CM1, the replay's camera loads its whole move first, then runs on a click — 2026-11-03, ready, see docs/queue/CM1-notes.md
In replay mode a glide (3), a fly out (O) and a fly in (I) now load first: the map says "Loading the move... N s" while
every view the move will show is painted, with no time cap and the map's pictures held, then "Ready - click to run", and a
click or a key runs it (Escape cancels), so a screen recording can start first. Measured on an Andorra recording
(`ui/ReplayMoveProbe`), black frames went from up to all of a move to none: a glide across 119 months, black in all 295
frames and 5,256 asks of the map's worker before, ran with no frame unpainted and nothing asked; loads took 5-15 s. Left
for CM2: the globe near the city still draws part of the planet from its coarse 7.4 km picture while it moves (blurry, not
black), and the half resolution on the move. ReplayCheck 5d runs a move in the real window, off screen. The normal game is
untouched.
## pending
| CM1-C | Eye-check CM1 ({next}) at 1920 x 1080 on a recorded Earth city: in a replay, mark A at the street and B at the whole Earth, press 3: "Loading the move... N s", then "Ready - click to run", a click runs it with no black on the zoom out; the same with O and I, and a glide across months; Escape while it loads stops it and the view stays where the move begins | Not looked at in the running game | [CM1 notes §3](docs/queue/CM1-notes.md#3-what-was-built) |
## queued
| CM2 | The replay's loaded move on the globe (found by CM1, its ★1-3): no coarse ground past the plane in the GROUND tier (the charts asked over the whole visible disc in a loaded move), full resolution on the move if the frame rate holds at 1920 x 1080, and the 250 ms frame of a glide across months measured and taken off if the load can do its work ahead | [CM2](docs/queue/CM2.md) |
---
