<!-- readiness: provisional -->
## changelog
### FB6, KeptCheck's fixtures cause their own conditions — 2026-11-17, ready, see docs/queue/FB6-notes.md
KeptCheck's sections 44, 51, 63, 64, 66, 67 and 89 now cause what they read instead of finding it on the city's path, each with
a `fixture:` line, and their Observation guards are out; no floor or premise moved. 44 takes a run both offers fall four times
short of, so its first draw has the shortfall's lines and the cap's chips (KBF1). 51 swings the plain business's property tax
by three printed steps of its net income each month, and 66 and 67 put sixty months of paper on the books (KS2). 63 holds the
fund's rule still, watches the bonds that run longest and buys one by hand when none it holds lasts. It also sells small bonds
until Search has a third page and gives the record a second older year (KF63, LX1's three). 64 quotes Borrow at the fill cap,
and 89 holds the families across its month (KHT1). 87's trace shows the Rankings worker, not the save, leaving the board empty;
it goes on as KRW1. 55's Bank Profit page, red only in R6b's dry run, goes on as KBP1. The change is harness only; the
playtest's trace is unchanged.
## queued
| KBP1 | KeptCheck 55, the Bank's Profit page, in R6b's dry run on main `ec481b4a`: the first draw made 299 nodes and one month made 93, a block of the page dropped and redrawn. Run alone, main `ec481b4a` keeps it, and so do FB6's three full runs, where the page's first draw is 192 nodes and each month hands back 193. FB6 could not cause the red, so the block that comes and goes under the suite's load is not yet named. Done means: the run that drops it is traced (which block, and what in the bank's month or the clock moves it), the fixture holds that still or the page keeps the block, and 55 is green on R6b's path. | [FB6 notes §6](FB6-notes.md) |
| KRW1 | KeptCheck 87, the saved cities on the Rankings board (KS1-Saved's trace, FB6 notes §4). The city saved into slot 10 and the same folder read on the harness's thread in 0.2 to 0.7 s finds it with nothing left off. The tab's worker (`RankingsScreen.WORKER`, one daemon thread at `Thread.MIN_PRIORITY`) answered after 16.8 s on a light load (0.3 s of CPU). Under the ensemble's ten JVMs and another suite it had not answered after 60 s, nor its tab-entry read before it (RUNNABLE in a native read, 0.0 s of CPU). So the board stays empty while the machine is busy, the gate's pool included. ★ Recommended: the worker at `Thread.NORM_PRIORITY` (a read of the slots is under a second of CPU). Done means 87 green under the suite's load, with its minute unchanged. | [FB6 notes §4](FB6-notes.md) |
## known-reds
KeptCheck | each month hands back nine in ten of the nodes the first draw made, or more (256 of 299) | KBP1 | 2026-11-17 | Section 55, the Bank's Profit page, in R6b's dry run on main ec481b4a only: one month made 93 of 299 nodes, a block of the page dropped and redrawn. FB6's three full runs keep it (193 of 192 handed back, a first draw of 192 nodes), so FB6 could not cause it; the block is not yet named (FB6-notes §6)
KeptCheck | the read of the saved cities landed within a minute | KRW1 | 2026-11-17 | Section 87: the Rankings tab's worker, one daemon thread at Thread.MIN_PRIORITY, starves on a busy machine. FB6's trace: the saved city on disk and found by the same read on the harness's thread in 0.2 to 0.7 s; the worker not landed after 60 s under the ensemble's load (0.0 s of CPU, in a native read) and landed after 16.8 s on a light one (FB6-notes §4)
