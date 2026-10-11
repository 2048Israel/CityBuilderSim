# EC1 - EarthTerrainCheck's cells pass on the thread's CPU (2026-10-11)

The brief for batch EC1. `QUEUE.md` links here. Written by the orchestrator after the gate of 0.7.126 (I6).

## What is wrong
EarthTerrainCheck asserts "the cells pass in no more than CELLS_PASS_MS, its tiles read from the file the first time"
(`CELLS_PASS_MS` 1,000 ms; the study measured 370 to 450). It times the pass on the wall clock, its first read cold
from `earth-1.bin`.

- In a full suite on the cloud machine (two cores) it fails often: 1,085 ms in the gate of the I6 merge (4978fb6),
  1,125 and 1,212 ms in I6's own suites, and red in I5's development suite. It passed in the gates of I5 and MM1.
- Alone it passes, before and after I6 alike (163ce0c, before I6: 640 and 652 ms; 4978fb6, after: 491 and 551 ms).
  So no batch slowed it: in a suite the file's pages have been pushed out of the cache by the harnesses before it,
  and the first read waits on the disk.

## Do
Time the pass on the thread's CPU, as MapCheck's month bound is since 0.7.100 (Jerus's decision A28 of 2026-10-10:
"Time it as thread CPU (not a loosening)"; `MapCheck.CPU_TIMED` and `THREADS` show the shape). Keep `CELLS_PASS_MS`
and the assertion's premise. Print the wall clock beside the CPU time, so a slow disk still shows. If the pass runs
on more than one thread, time each thread's share or say why the wall clock must stay. ★ any call.

- Files: `EarthTerrainCheck` only (and `GameVersion` if the batch takes a number; it changes no game behaviour, so ★).
- Traces: byte-identical. The full suite run three times; the check green in each.
