# MP1 - the map fast again ({next}, 2026-10-27)

The brief: [MP1.md](MP1.md), Jerus on 2026-10-26: "map is slowing the game down lol". The model is untouched:
the default playtest's trace is byte-identical to main's but for its two run-time figures (the run's seconds, and
"the windows took"), so no ensemble. Two slices on Sonnet 5.5 at xhigh, Opus on the briefs, the profiles and the
review; a third slice was written and taken out (section 5 below). No budget, dial or premise moved.

## 1. What main misses on a quiet PC

Measured 18:55-19:18 with no other CityBuilderSim JVM running before or after any run (counted at each run's start
and end) and the game closed, in the window the orchestrator held for it (no gate, SP1's suites held; SC1's gate
ended 18:54). Jerus's load in it, as the orchestrator gave it: the game not running, two things open in Microsoft
Edge, and another agent of his doing finance work, nothing of CityBuilderSim's. `./batch-check.sh --timing MapCheck
EarthTerrainCheck KeptCheck`, hard, three rounds, main (0.8.58) and MP1 in turn. In ms, round by round:

| line | budget | main | MP1 |
|---|---|---|---|
| MapCheck 18: a month's FX-side map work at 5B, the median month | 58 | **66.4, 56.9, 59.1** | 20.6, 24.0, 21.3 |
| EarthTerrainCheck: a far node over Medellin at 1,024 plots a pixel | 80 (`SCREEN_MS`) | 55.2, 54.5, 53.8 | 32.7, 31.4, 31.4 |
| MapCheck: every stride's far node over Rotterdam, on the full file | 80 | 59.9, 58.7, 56.8 | 53.8, 54.2, 55.2 |
| EarthTerrainCheck: the Earth screen, 198 tiles at Rotterdam's site | 80 | 36.7, 48.4, 49.9 | 43.4, 37.2, 42.3 |
| MapCheck 5: at 10 billion a month's change | 5 | 4.3, 3.7, 3.7 | 3.9, 3.7, 3.8 |
| MapCheck: the sidecar written and read back, the slowest figure of the three rounds | 500 | 283.5 | 312.3 |
| KeptCheck: the whole kept chrome's month | 8 | 5.6, 5.5, 5.5 | 5.9, 5.4, 6.2 |

**Quiet, main misses one line, the 5B month, in two rounds of three**; MP1 passes every line in all three (exit 3:
only main's recorded red, MapCheck's "its streets one network"). Every other line of the brief's table passes
quietly on main, so what the gates saw on 2026-10-26 was mostly load, which TM3 now judges side by side. Loaded
(main, three `--timing` runs 16:21-16:51 beside two gates and another session's checks, 2 to 5 other JVMs) the
same lines read up to twice their quiet figures: the 5B month 122.6 / 115.9 / 55.4, Rotterdam's far node 121.0 /
118.3 / 60.1, Medellin's 105.3 / 107.3 / 68.6, the 10B month's change 7.5 / 6.7 / 9.9, the sidecar's slowest
573.9 / 427.8 / 331.6, the chrome 9.4 / 7.9 / 5.0.

## 2. Where the time went, and which batch put it there

Each line was profiled (Java Flight Recorder over a probe that repeats the timed work, the samples filtered to the
timed calls) and timed at the tags back to its start, the tags run in turn so they share the PC's load.

**The 5B month (58 ms, set by UP3, 0.8.27, 2026-10-23).** The month is the far screen (about 20 ms) and the L0
screen re-stamped (about 40). Three quarters of the L0 part was `CityMap.reserveBlocked()`, the interchanges'
guard (FR7 slice 4, 7aaf4ff9, 2026-10-19): it walked all 14,661 districts' mined sites on every call, and each
month's change (`changes`, the guard's version) made every L0 tile's `reservesIn()` find its reserves again, every
candidate calling it - dozens of full walks a month. A sixth was the world line's `dryRun()` reading each plot
four times. Half the far part was the district index: `byKey`, a `HashMap<Long, District>` keyed by
`(dx << 32) | dy`, whose `Long.hashCode()` is `dx ^ dy`, so a city round the origin shares a few hundred hashes
and every lookup walked a tree of a bin. **No batch after UP3 slowed it**: all three costs were in UP3's own
figure (its L0 32.5 ms), and the line was set about 5 ms over it. MapCheck whole, in turn: 0.8.27 54.7 / 63.1,
0.8.41 62.3 / 69.2, 0.8.51 71.0 / 55.4, 0.8.57 66.3 / 76.2 ms - two runs of one tag differ more than the tags do;
a probe of the month (37-57 months a tag) puts 0.8.27, 0.8.41, 0.8.51 and 0.8.57 within its noise, and MS3 against
0.8.57 at 63.7 / 57.0 against 54.1 / 63.8 ms: its gate's +14.9% was the load.

**The far nodes (Medellin at 1,024 plots a pixel, since ET1 slice B2, 0.8.41; Rotterdam at every stride, since
E5, 0.7.119).** Both time `EarthTerrain.farTerrain()`. At Medellin 90% of a call was `strokeWater()` (E5, 0c6cad9b,
2026-10-11): for each of the ~600 one-degree tiles in view whose widest river may show, every call parsed the
tile's rivers again (40%) and sorted each run's widths for its median (45%) - E5's from its first day. Over
Rotterdam the node is northern Europe's small lakes and tiles the file's cache (`EarthData.CACHE_BYTES`, 10 MB)
evicted since the last call, inflated again; it passes quietly with more than a quarter to spare, and MP1 leaves it.

**At 10 billion a month's change (5 ms).** The worst of fourteen months on the wall clock (this JVM reads no
thread's CPU finely enough). The median month is about 1-2 ms; the worst is a month that lays a highway
(`layRuns()` -> `openPieces()`, CL5's map pieces), 4-6 ms. It passes quietly; under load one busy moment fails it.

## 3. What changed

Every change gives the answers it gave: the same pixels, lists, runs and order, with fewer reads.

1. **The reserves' guard keeps the mined sites against `changes`** (`CityMap.reserveBlocked()`, slice A): one
   walk a change of the map instead of one a call. `minedSites()` itself and its other callers are as they
   were. `changes` is enough: every write to a district's site runs goes through `add()`, which moves it, as a
   purchase and `rebind()` do, and the guard already reported `changes` as its version.
2. **`dryRun()` reads a plot once** however many quarter steps land on it, and the line's run on to
   `WORLD_REACH` starts at its check's last step (256, not 0): the check passes when step 256's plot is wet as
   well as when all is dry, so the run must read it again (Sonnet's catch; the brief said 257).
3. **`byKey` stores `key()` multiplied by an odd constant** (one-to-one on 64 bits): `districtAt()`,
   `indexDistrict()`, `unindexDistrict()`. It is only got, put and removed, never iterated.
4. **A far node's rivers kept per tile** (`EarthTerrain.FarRivers`, slice B): the tile's runs whose median is at
   least the `narrowest` they were read for, with their medians and the tile's widest, drawn in the same order by
   the same code; a wider view's smaller `narrowest` reads the tile again. The array is made with the terrain (a
   reference a tile, about half a megabyte; four Earth worlds are kept at most). **Threads:** far nodes are drawn
   on the map's workers as well as the screen's thread; an entry is immutable (final fields, its list built before
   it), so a thread sees a whole entry or none, and two building one tile's cost a parse - as with `widestRun`
   beside it. No lock and no `volatile`; `pick.sh` names no review.

Quiet, MapCheck 18's own month figures (median, ms): the 5B month 59.1 -> 21.3 (the L0 screen 38.0 -> 5.1, the
far screen 20.9 -> 15.5); x 1, Jerus's city as it is, 8.2 -> 6.0; x 80 53.7 -> 48.6.

## 4. What Jerus feels at 50x

At 50x a month passes every tenth of a second. The map's share of a month is drawn in the clock's redraw, which
waits three times what it cost before the next (`ClockPace`), so the map never held the clock back: it made the
map move less often and the window catch for a moment.

- **His city now** (MapCheck's x 1): about 8 ms of map work a month before, 6 after. Nothing he could feel either
  way: the map was not slowing his game. What he saw was the gates' timing lines going red on a busy PC.
- **A city of five billion with the map open**: about 60 ms a month before - a catch of nearly four frames each
  time, and the map moving about four times a second, every two or three months. About 21 ms after: the map
  follows the clock almost month by month, with no catch to see.
- **A city of 40 million** (x 80): about 54 ms before, 49 after; the rest is the chain (below).

## 5. Tried and taken out

A tree for `CityMap.roomiest()` (slice C): a first profile put 85% of a month's change in it, but on the x 10,000
copy, not the 10 billion one. MapCheck's new section held the tree to the walk (292 asks at 5B, none wrong), yet at
10B it was never asked, and in turn the line did not move (5B 3.87 / 3.71 / 3.32 ms main against 4.08 / 3.52 / 4.30;
10B 6.30 / 5.59 / 4.72 against 5.50 / 4.30 / 4.50). 150 lines that buy nothing were reverted unshipped.

## 6. The checks

`./batch-check.sh --pick`: exit 3, 21 harnesses, the only red main's recorded one, no timing line soft. The
default playtest's trace: byte-identical to main's but for its two run-time figures. The quiet `--timing` above:
MP1 exit 3 three rounds in a row. The playtest's own time, main and MP1 in turn on a busy PC: 77 / 76 s against
51 / 110 s, the load.

## 7. Left for later, with a recommendation each

- **x 80's chain, 25 ms a month on the screen's thread** (no budget). It is each moved district's plan input
  gathered as its job is handed out: `planInputHere()` -> `runsInto()` (`CityRuns.fill()` over the district's
  frame, 40%) and `reservesIn()` (38%). The reserves could stay kept across months if the guard's version moved only
  when the mines or the shore's works it reads move (a digest of them, not `changes`). Recommend that as a small
  batch with a harness counting how often the reserves are found again.
- **Rotterdam's far node** (54 ms of 80 quiet): its tiles' blobs are inflated again each call. Raising
  `EarthData.CACHE_BYTES` trades memory for speed: Jerus's call, not needed for any line now.
- **The 10B month's change** (3.7 of 5 quiet, 6-10 loaded): its worst month lays a highway. Only if it keeps
  failing at the gates after TM3.

## 8. Sent back from the gate (2026-10-27, 20:25), and what was done

- **KeptCheck's "the kept month is within KEPT_MONTH_MS"** read main 5.31 ms against MP1 10.7 (+101.8%) in the
  gate's paired rounds, with Jerus's game open from about 19:35. The line times the People page's draw, CSS and
  layout, not the game's month, and nothing MP1 changes runs there (the far rivers' array is made once a terrain,
  four Earth worlds kept at most; `GlobePicture` makes its terrain once). Timed again in turn, main then MP1, beside
  SC1's and RL1's gates (3-5 other JVMs): main 11.4 / 6.1 / 6.8 / 6.7, MP1 5.8 / 5.0 / 5.8 / 6.1 ms; in the quiet
  window main 5.4 / 5.3 / 5.9, MP1 6.0 / 5.5 / 5.5. The gate's doubling was the load. Its neighbour, the whole
  kept chrome (8 ms), went over in one of four rounds on each side (main 10.6, MP1 8.7): main's flake under load.
- **EarthTerrainCheck's far node over Medellin** ANDed its relief shares with its time, so a red had no figure the
  paired step could judge. Split as ET6 split BridgeCheck's: the shares on their own line, and "...and at N plots a
  pixel it is drawn in no more than SCREEN_MS" through `Timing.within` alone. No budget moved. Two other lines of the
  harness AND a time the same way (a landmass tile, line 620; the globe's frame at the handoff, line 2311); left as
  they are, named here for whoever meets them red.
- main (ZT1, I13-L) merged into the branch; the maps regenerated.
