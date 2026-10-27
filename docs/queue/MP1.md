# MP1 - the map fast again: every timing line main now misses, traced and fixed

## Jerus, 2026-10-26 (the requirement)

Asked to ship CC1-S past one speed line (EarthTerrainCheck's far node over
Medellin, 134 ms against 80):

> ship, but its clear, map is slowing the game down lol, probably every
> single batch from now will show same result, cause map ms probably wont
> pass anymore

Then, offered TM3 to the top and this batch after it: "Both, TM3 then MP1".

## Why

On 2026-10-26 main itself missed map timing lines on about half its runs,
on a PC loaded by two batch sessions:

| line | budget | main's figures that evening |
|---|---|---|
| EarthTerrainCheck: a far node over Medellin at 1024 plots a pixel | `MapCheck.SCREEN_MS` 80 ms | 60.3, 107.0 |
| EarthTerrainCheck: the Earth screen, 198 tiles at Rotterdam's site | `SCREEN_MS` | red on one run of two |
| MapCheck: a month's FX-side map work at 5B, the median month | 58 ms | 59.9 at HY1's gate; red on both side-by-side runs |
| MapCheck: Rotterdam's far node at every stride | `SCREEN_MS` | 63.7 at 1024, 24.7 at 512 (passing; MS2's runs 130) |
| KeptCheck: the whole kept chrome's month | `KEPT_ALL_CHROME_MS` 8 ms | red on both side-by-side runs |
| MapCheck: the stamp, the L0 screens, THE SCREEN, the sidecar, a month's change at 10B | their own | red on main's 19:00 run (PC just off battery) |

Two causes are mixed in those figures: the PC's load, and the map really
growing heavier since the budgets were set (MS1, BL1 and MS2 added drawing
in the same weeks). TM3 settles the first at the gate. This batch settles
the second.

## What MP1 does

1. **Measure on a quiet PC:** no other batch JVM, the game closed. Run each
   timing harness `TIMED` times and take the medians. Record which lines
   main misses and by how much.
2. **Find the batch that slowed each one.** Time the same lines at the tags
   back to the last version where each passed with margin (`git worktree`
   per tag, the figures side by side). Name the commit.
3. **Make it fast again, at its cause.** No budget moves and no premise
   moves (README, *The checks*: a harness is the finding). If a line's
   budget proves wrong for the design (measured against a screen it was never
   derived for), stop and send the orchestrator the call in plain words with
   a recommendation.
4. **Say what Jerus feels at 50x.** KeptCheck's month lines are the game's
   own month on the kept screens. Say in plain words whether the map slows
   the clock, and by how much, before the fix and after it.

## Scope and checks

- The map's model and screens: `MapView`, `GlobeFrame`, `MapTiles`,
  `CityMap`, `TilePainter`, `TileRaster`, `MiniMapFrame`, the far nodes. The
  model is untouched: the trace is byte-identical, and there is no ensemble.
- Every timing line main misses on the quiet PC passes there after MP1, three
  hard runs in a row. The notes give the before and after medians.
- After TM3, which judges the gate's timing side by side.
