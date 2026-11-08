# UP8 - the 10B month's change: the Remark pause, and the walk once the copies are full ({next}, 2026-11-08)

The brief: `docs/queue/UP8.md`, with RT1's and XI2's gates added to it (the 10B month at twice main's on code nothing
in the map runs). A map batch: nothing in the model reads the map. No budget and no premise moved.

## 1. What doubles the 10B month at the gates

- **Reproduced, the other way round.** XI2's two builds (main f8841a82, the merge 58b3d0fd), MapCheck alone with the
  gate's flags, ABBA, four JVMs a side; the worst bounded 10B month: main 5.22 / 5.27 / 5.27 / 2.78 ms, the merge
  3.10 / 8.53 / 3.11 / 3.11 (medians 5.25 and 3.11). XI2's gate had main 3.92 and the merge 7.77. The months around
  the worst are about 1 ms; the worst is one month in 13, at month 1, 2 or 11, 2-5 ms above them.
- **It is G1's Remark.** Each timed month read the GC beans' counts before and after (a probe in both builds): no
  young collection lands in a timed month; the months that jump hold a concurrent-cycle event. A MapCheck JVM holds
  580-750 MB live (the played city and the four copies, of 1.3-1.4 GB committed under `-Xmx2048m`), so G1 marks again
  and again, about 25 cycles a run, each ending in a Remark of 2.1-3.7 ms (4.8-6.9 for the first two). Where a Remark
  falls decides the line: the heap's state and the minute, not the code. RT1's three doublings, XI2's, and the +10-13%
  the gates of 10-27 show the merge over main, are this.
- **Fixed at the harness, not the budget (★1):** a `System.gc()` before each copy's months, as before the screen
  rounds already: the bound is then the map's month on its own garbage. Section 5's line now counts the collections
  inside its bounded months, so a red that holds one says so.
- **Whether a game's JVM meets it.** The game runs with no JVM flags: G1, its heap a quarter of the RAM (8 GB on
  Jerus's 32). A Remark, when one comes, stops every thread for its 2-5 ms, whatever runs: a frame or the map's month.
  It is the JVM's pause, not the map's work; the map's own month is 1-3 ms of it.

## 2. The month after a big month

Today's main, quiet, phase by phase (a scratch copy of `CityMap` with timers; the 5B copy's year first, as section 5
runs it, then a `System.gc()`): the 10B year's worst 3.26 ms. Month 1: the placing 1.36, `advanceCursors()` 0.31, the
runs 0.98, the log 0.35; month 8 3.12 (the placing 1.32); the other months 1.3-2.2 (the placing 0.22-0.38, the runs
0.65-1.15, the log 0.09-0.21). GT1's queue already logs only what a month moved. What was left of the two spikes is
the walks over full districts and the cursors' walk over the districts the big month filled: THE ROOM TREE (3).

## 3. The walk once the copies are full

A fresh 10B copy grown +0.01% a month (`Up8Full`, a probe): free plots 3,562,377 after a year, 1,472,987 after two,
none from year 3's eighth month (32 months). From then a month cost 12-32 ms (years 4-6: medians 16.6, 23.0, 18.1 ms),
the placing 15-26 of it: with the cursors at the ends, each type's overflow went to `roomiest()`, a pass over all
29,255 districts' objects. THE ROOM TREE (`CityMap`): two max trees beside GT1's `freeOf` and `freeRoadOf`, kept in
`moved()`, so `place()` jumps to the next district that fits, the cursors to the next with room and `roomiest()` reads
the leftmost maximum, each in about 15 steps. The placements are the old code's: a hash of every district's counts
after every month, six years from the fresh copy, is main's year for year (d0fe1c61... to f49e8ccb...).

## 4. Against main, ABBA

Main's classes (39c0948d) and the batch's in turn, each JVM alone with the gate's flags (`-Xmx2048m -Xms64m
-Dcbs.timing=hard`), MapCheck then the probe, four rounds; the night's other lane ran its suite through rounds 1-2
(eight JVMs, 79% CPU).

| round | 10B worst bounded month, main / UP8 (ms) | full copy's years 4-5, median (worst), main | UP8 |
|---|---|---|---|
| 1 (loaded) | 39.48 / 38.65 | 13.85 (15.54), 14.06 (16.41) | 0.92 (1.87), 0.89 (2.06) |
| 2 (UP8's turn loaded) | 1.98 / 117.11 | 16.04 (26.84), 16.66 (17.96) | 0.51 (1.12), 0.46 (0.94) |
| 3 | 4.83 / 1.62 | 15.17 (33.42), 14.38 (16.04) | 0.58 (1.09), 0.43 (0.95) |
| 4 | 2.66 / 2.82 | 13.43 (14.97), 13.48 (14.20) | 0.50 (1.19), 0.43 (0.88) |

No collection fell inside any of UP8's bounded months. The loaded months are the PC's: 117 ms and 39 ms months with
no collection in them are a JVM not run, which no change to the map's work moves; a gate's paired rounds meet them
on either side. MapCheck 5b on UP8: median 0.53-1.01 ms, worst 0.92-1.63 (48.0 in round 1's load).

Four more rounds of MapCheck alone once the other lane's suite had ended (three JVMs, 13% CPU), the 10B worst bounded
month: main 2.25 / 2.65 / 6.33 / 5.50 ms (median 4.08, over RECONCILE_MS twice: month 1 at 6.33), UP8 1.76 / 2.03 /
2.80 / 2.51 (median 2.27), no collection inside UP8's months. Over the six rounds not loaded, main's median 3.74 and
worst 6.33, UP8's 2.27 and 2.82: under 5 ms with room. WN3's gate (main 6.92, the merge 8.97) was not rebuilt: its
+30% is the same coin, where a Remark fell.

## 5. The checks

- MapCheck 5: after every month, outside the timed span, every district's log sums to its counts and the room the
  walks read is each district's own (`freeKept()`); `logsAddUp()` reuses one array (it had made 17 MB a call at 10B,
  garbage that would start the marking it now runs between).
- MapCheck 5b (new, last in `main()`): the 10B copy grown until a month places past every district's room
  (`lastOverflows()`, at most `FULL_MONTHS_MAX` months), then 12 timed months, each placing past it; their figures
  printed, no budget of their own (★2).
- `./batch-check.sh --pick` on the merge with main 39c0948d: 18 harnesses, exit 4 on one line, MapCheck 2d's "a yard
  that goes moves none of the buildings ... (1200 of them, 136 moved)": DM2's class (queued), and main's own classes
  fail it word for word in every one of their twelve runs here (XI2's main in its four): not this batch's, passed by
  name (Jerus, 2026-11-04). Timing soft there; the gate times it hard.
- The ensemble is skipped: nothing in the model reads the map, and the placements are main's month for month (3).

## 6. The calls (each takes its recommended way, BP1)

- **★1 A `System.gc()` before each copy's timed months.** Recommended: yes. The Remark is the JVM's, started by the
  other sections' heap; the month measured on its own garbage is what RECONCILE_MS bounds. The alternative, judging the
  line with Remark months left out, reads the GC beans inside the timed span and keeps the gates' coin toss.
- **★2 The full copy's month has no budget.** Recommended: printed only, as the brief says ("no new budget unless
  Jerus sets one"); RECONCILE_MS is the figure to compare it with.
