# SP1 - the suite three at a time, each harness under a memory cap

Queued 2026-10-27 from a side session with Jerus, on his word ("yes please,
do it as soon as humanly possible"; `docs/queue/GP1.md` has his words and
the figures). After GP1 and RL1, in the same lane, since all three touch
`batch-check.sh` and `ship.sh`.

## Why

`AllChecks` runs the harnesses one after another, each its own JVM, waiting
for each: about 18 min on a quiet PC (1,011-1,116 s), 31-32 min when it is
shared (MS2 1,879 s, CC1-S 1,934 s). The PC has 14 cores, and most of them
sit idle. Every ship runs the suite once.

## What SP1 does

1. **A memory cap first.** The harness JVMs get no `-Xmx`
   (`AllChecks.command`, `AllChecks.java:331-337`), so each may take a
   quarter of the RAM, 8 GB. On 2026-10-21 the commit charge reached 43 of
   54 GB and a suite died with CreateProcess error 1450. Measure the
   heaviest harnesses' peak and set a cap with headroom, as a dial.
2. **Then N at a time**, a dial, 3 to start; the longest first (MapCheck
   about 170 s, LongPlaytest). The summary and `-f`/`-q` output stay in
   `HARNESSES` order, as now. Per-harness logs are already separate and
   scratch folders already unique (`GameFiles.java:102-105`).
3. **The one shared file**, `timing.tsv` (`Timing.java:187`, appended by
   every timing harness), made safe for concurrent writers: one file per
   harness, joined after, or the batch's better way.
4. **The timing harnesses run twice per gate**: soft in the suite, then hard
   in `--timing`, 5-7 min. At the gate the suite step may skip
   `Timing.HARNESSES` if the timing step then judges their other lines
   exactly as the suite does (known or NEW).
5. **`--timing` stays one at a time**, alone.
6. **One in, one out** (Jerus, 2026-10-27): SP1 removes about as many lines
   of script and rules as it adds. Its notes give the count.

## Scope and checks

`AllChecks`, `Timing`, `batch-check.sh`, `ship.sh`, `docs/batch.md`; no game
code; ships `--no-tag`. One full suite at N=3 against N=1 on the same tree:
the same pass and fail lines, and the notes give both times and the peak
commit charge (with the other lane's load said).

## Calls

- Item 4, the timing harnesses once per gate. Recommendation: yes, if the
  timing step can judge their non-timing lines the way the suite does.
- N above 3. Recommendation: only if the measured peak leaves room with two
  lanes running.
