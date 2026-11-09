# UP9 - x 80's chain: the lazy try backs off, the reserves kept ({next}, 2026-11-09)

Session log (kept current at each decision; the orchestrator's window: W=150000).

- 2026-11-09: branched `batch/UP9` from origin/main 092130f4 (LS1 merged). Lane C: LS1 -> UP9 -> MB1.
- Main's classes (092130f4) kept in `target/main-classes` for section 18's paired runs; runs name the main clone's
  data (`target/checks/dataopts.txt`: -Dcbs.earth / -Dcbs.world), gate flags -Xmx2048m -Xms64m, timing soft.
- MapCheck is a known red on main (`docs/notes/gate.txt`: the yard lines); exit 3 expected.

## Main's x 80 figures (092130f4, MapCheck 18, alone, gate flags, `target/checks/map-main-1.txt`)

- The chain's FX side: median 33.62 ms, worst 69.97. The month's sum 73.17 (109.06) now's / 89.95 (131.45)
  today's; the lazy checks 48.28 ms.
- A month's median: 250 plan jobs handed out, 21 plans made, 106 links taken, 124 could do neither; the
  worker 577.9 ms; own stamps asked 501, worked out 18. Over 25 months: 578 made, 2474 taken, 2722 could
  neither, 0 not needed; the carry moved in 24 of 25 months - so the lazy try fails almost every month.
- The early district moved: 249 jobs in 3 rounds (11 made, 114 taken, 124 could not), FX 29.20 ms.
  District 31's link let go alone: 94 handed (93 lazy), 1 made, 93 taken, FX 0.90 ms.
- 5B: the month's sum 30.71 ms, the chain 0.08 ms. Settled within `CHAIN_ROUNDS` = 5 in every month.

## The design (decided 2026-11-09)

**A. The lazy try backs off (UP5-B).** One `Backoff` per band (`eager`, `moved`, `took`, `seen` epoch,
`out` jobs outstanding), grown with the bands in `sizeBands` and never reset (x 80's districts grow most
months; `band(i)` is stable). A lazy failure (the redo in `chainTo`, `adopt`'s failure) makes the band
eager and moved. When `chainTo` meets a band in a new epoch, an eager band whose last epoch was quiet
(nothing moved, no job outstanding, at least one eager job took its link) is lazy again. Only the eager
keeps-jobs are watched; one that planned or failed marks the band moved; a late job from an older epoch
changes nothing. Lazy only when `!eager && bandFull[b] != e`. Eager and lazy give the same chain
(`chainEqualsWhole`), so a stale state is harmless. MapCheck 18: fixture 6b warms up, moves one
district's carry (`carryMoves`, a copied link) and asserts the lazy failure, then the eager epoch that
plans both, then a quiet eager one, then the old lazy asserts unchanged; the month series logs each
band's hand-outs and asserts none lazy after a failed one and none eager first after a quiet one.

**B. The reserves kept (MP1-C), revised 2026-11-09.** Found: the guard's `version()` is the map's
`changes`, which moves with every district's count (`syncLand`, `reconcile`, `add`), with `dropLink` in
the chain and with the screen's picture setters, so the reserve cache (`CityRuns.reserveKept`) clears
every month at x 80, maybe mid-chain - MP1-C's premise holds. Decided:
1. The guard's `version()` becomes a content digest of exactly what `reserveBlocked` reads, through the
   same calls: the shore works' boxes, and for each district with any `siteRuns`, each `siteList` site
   with `mine() >= 0`, each box's `World.mix` summed (order-free), mixed with baseDX/baseDY. Preferred
   over a hand-kept `minedStamp`: correct by construction, no writer to miss.
2. It calls `sitedNow()` first, and is cached with the `changes` it was worked out at: recomputed only
   when `changes` moved (one sweep of the districts; the slice reports its cost).
3. `layReserves(g)` keeps the cache when `g` is the ground already bound; `setReserveGuard` when the same
   guard is bound again. A load binds a new guard (`guarded()`), with new runs anyway.
4. CityRuns counts reserves asked, found again, the clears by cause (runs' version, ground's version,
   guard's version, size cap) and the miss path's ms. Section 18 prints them per x 80 month and asserts
   the kept sets equal a fresh `InterchangeGround.sitesIn` (every x 80 month and fixture 2h's), a quiet
   month finds them again, and a month that lays a mine clears them by the guard's cause. 2h unchanged.
5. B2 only if the runs' or the ground's version still clears the cache in more than half of x 80's
   months: then a regional key (slice B2), or a Pending row with the figures if the ground's dryness
   would cost about what `fits` costs.
6. Details (brief `target/deepseek/brief-UP9B.md`): the digest mixes each mined site's district with its
   box, fields folded apart so a swap moves it; its `changes` is read after `sitedNow()`. A clear counts
   every cause whose version moved (one clear may have several), the size cap and the rebinds apart.
   Hooks `reserveCounts()`, `reservesStale()` (kept vs a fresh `sitesIn`, every key) and
   `guardDigestFresh()` (catches a missed writer of `changes`). No new locks: main's cache has none.
   Slice B starts once slice A is reviewed and committed (both edit CityMap and MapCheck).

## Slice A (drafted by Sonnet 5.5, reviewed 2026-11-09)

- Review: matches the brief; `carryMoves` numbers its copy from the map's own `++jobsHanded`; a band
  whose epoch takes the early path hands no watched job, so `took` stays false and it stays eager
  (conservative; same chain). Fixed one spacing nit. MapCheck: exit 3, the four yard lines only.
- x 80 median (one unpaired run, beside other sessions' JVMs): 126 jobs handed (0 lazy; main 250),
  21 made, 106 taken, 0 could do neither (main 124); own stamps asked 376 (main 501). The worker and
  FX times rose ~1.7x in x 1 and 5B too, which this batch does not touch: load, so the paired runs decide.
- x 80 is ONE band (125 districts, `CHAIN_BAND` 128). Band log: x 80 N=1 (the first month's failed
  lazy try; every later month plans 21, so the band stays eager), M=0 over 26 months; x 1 N=0, M=0.
  Rule (ii) is caused by fixture 6b alone: (b) carry of district 39 moved: 94 jobs (93 lazy), 1 made,
  86 could not take; (c) 94 jobs (0 lazy), 2 made, 92 taken; (d) quiet: 94 (0 lazy), 1 made, 93 taken;
  then `quiet` lazy again and `!backingOff(dq)`. `lazyJobs` equals the harness's own count every month.

## Slice B (drafted by Sonnet 5.5, reviewed 2026-11-09)

- Review: matches the brief. One deviation, kept: item 5 (`layReserves` clears only for a new ground) turned
  four JunctionCheck trumpet lines red, since `addArm` lays an arm by hand and the runs' version does not
  move with it. So the public `layReserves` still clears whatever was bound, and `layTo` binds through a
  private `bindReserves` that clears only for another ground. The digest reads what `reserveBlocked` reads
  through the same calls; `reservesStale()` touches no counter. MapCheck: exit 3, the four yard lines only;
  JunctionCheck green.
- x 80 over 25 months (one unpaired run): a median of 401 reserves asked a month, 185 found again, 238
  missed (6.2 ms). The guard's version moved in G = 24 of 25 months (a mine grows most months at x 80); the
  runs' or the ground's version cleared the cache in R = 11. A fresh digest takes about 0.1 ms. The kept
  digest and the kept sites equal fresh ones after every month; fixture 6b's quiet round finds its one ask
  again; 2h's two runs keep 7 of 13 and 8 of 17.
- B2, decided: design rule 5 (R over half the months) is not met, so no B2 slice. The finding it did not
  foresee is G: the guard's stamp is one for the whole map, so a mine laid anywhere clears every region's
  sites. The hits left are those within a month (main cleared mid-chain too, with every `changes`). A
  regional key (the mines and works by region, and the runs' version by region for R) would save at most the
  6.2 ms of misses a month at x 80: a Pending row with these figures, recommended left until x 80's chain is
  measured as the screen's bottleneck again.

## The paired figures (MapCheck 18, x 80, `target/checks/pair.sh`)

Main's classes (092130f4) against the batch's final ones (UP9-C), ABBA over four rounds, each JVM alone, the
gate's heap flags, timing soft; the medians are over the four rounds (main's rounds 2 and 3 ran loaded). The
run after slice B gave the same picture (far screen now's 26.9 -> 18.0 ms, today's 31.7 -> 22.4).

| a month's median, x 80 | main | UP9 |
|---|---|---|
| plan jobs handed (lazy) | 250 | 126 (0) |
| jobs that could do neither | 124 | 0 |
| plans made / links taken | 21 / 106 | 21 / 106 |
| far nodes drawn | 48 | 32 |
| far screen, now's ms (rounds 1-4 -> median) | 26.07 36.33 33.29 24.84 -> 29.68 | 17.54 21.32 18.73 16.97 -> 18.14 |
| far screen, today's ms | 29.97 39.36 37.95 28.72 -> 33.96 | 21.61 27.06 25.01 21.49 -> 23.31 |
| stamp, today's ms | 8.10 | 5.66 |
| the chain's FX side, median ms | 22.44 33.94 31.26 22.73 -> 27.0 | 23.86 28.11 28.25 22.43 -> 25.99 |
| the chain's FX side, worst ms | 56.50 50.57 44.93 26.16 | 39.62 55.49 41.51 33.91 |
| the worker ms | 460.2 | 481.1 |
| months not settled within `CHAIN_ROUNDS` = 5 | 0 | 0 |

The reserves (UP9 only; main counts none): 401 asked a month, 185 found again, 238 missed in 6.3, 6.9, 6.4
and 5.8 ms; G = 24 of 25 months, R = 11; the fresh digest's cost rounds to 0.0 ms.

Reading: the gain is the far screen, coloured once a month instead of twice (48 -> 32 nodes): now's -39%,
today's -31%, the stamp's today's -30%. The FX side falls only ~4%: the inputs gathered are about as many
(main redid 124 jobs eagerly, UP9 hands 126 eager), so the worker is unchanged within noise.

**`CHAIN_ROUNDS` stays 5 (decided).** The harness prints no rounds a month; x 80's first month still fails
its lazy try and is handed again (N = 1), so that month needs main's rounds, and the brief moves no budget
the figures do not earn.

## Section 5's regression, and slice C (2026-11-09)

- Found in the paired runs: MapCheck 5's "a month's change takes no more than 5 ms" was OK in all four
  main rounds and SOFT in all four batch rounds (5B 33.3 30.3 32.4 52.0 ms; 10B 61.1 59.5 59.0 86.4) - a
  hard red at the gate. Cause, confirmed by a probe (removed in slice C): every reconcile moves `changes`
  and the touched districts' `version`, so the guard's digest was worked out again about 15 times a
  section, each rebuilding every site-run district's `siteList` (cached by `d.version`): ~2,028 lists at
  5B (14,661 districts, 30-53 ms), ~3,590 at 10B (29,255 districts, 64-101 ms). Main's `reserveBlocked`
  rebuilt only the queried box's. With `changes` as the version again, both lines were OK.
- Decided: each district keeps its part of the digest against a key of exactly what its mined boxes read:
  its `siteRuns` element by element, its dx/dy, and one land key per recompute - the map's own
  `landVersion`, purchase count and centre stamp (O(1), as `groundKey()` folds them; `land.stamp()` walks
  every purchase), the legacy field and the base. Not `siteGen`: a site's state is not in its box. The
  digest's value is unchanged; `guardDigestFresh()` rebuilds every part, so the fresh == kept asserts
  (2h, 18, and now 5) test that the key is complete. Section 5 prints the parts built and kept and
  asserts some kept. Brief `target/deepseek/brief-UP9C.md`.
- Slice C (drafted by Sonnet 5.5, reviewed): matches the brief. 5B's line OK (worst 3.70 ms); 10B's still
  SOFT (12.98 ms, 7.5 on a rerun): a 10B recompute took 2.5-6.7 ms, of which the keys ~0.6 ms, the rest the
  walk over all 29,255 districts testing each one's `siteRuns`. Section 18 unchanged (401 asked, 185 found
  again, 238 missed in 6.2 ms, G = 24); JunctionCheck green.
- C2, decided: the kept digest walks only a list of the districts with runs (added at `markRun`'s writer,
  dropped in the walk once all its slots are null; ~3,590 at 10B); the fresh digest still walks every
  district, so the fresh == kept asserts test the list as well as the key.
- Slice C2 (drafted by Sonnet 5.5, reviewed): both of section 5's lines OK, 5B's worst month 1.74 ms and
  10B's 2.83; its parts built 1 and kept 26,361 at 5B, built 1 and kept 46,665 at 10B, the kept digest
  the fresh one. Section 18 unchanged: 401 asked a month, 185 found again, 238 missed (5.3 ms), G = 24,
  R = 11; the fresh == kept digest 0 months that differ. Committed as UP9-C.

## The FX side at 5B: main's borderline line (MapCheck 18, `MAP_MONTH_MS` = 19 ms, the median month)

- Main's own line sits at the edge: alone at the gate's flags it read 17.3-18.2 ms; beside the other
  lanes' runs it was SOFT in 7 of 12 runs of main's classes (`pair.sh`, `hyb.sh`, 2026-11-09).
- Pair after C2 (each JVM alone, ABBA): main 18.22, 24.4, 20.3, 17.34; UP9 19.4, 23.8, 23.2, 18.07; UP9
  minus main by round +1.18, -0.62, +2.90, +0.73 ms. Round 1's breakdown: both sides move 193 districts a
  month, repaint 16 of 1,296 stamps, draw the runs' layer in 3 of 3 months, work out no own stamp; the chain
  is 0.06 against 0.09 ms of it; the rest is the far screen's own sum, which UP9 does not change.
- Hybrid (`target/checks/hyb.sh`: main's 25 MapCheck classes over UP9's game classes, loaded): hyb 23.1,
  19.8, 33.2, 25.5; main OK, 23.5, 26.3, 27.1; the far screen's now's medians hyb ~19.0, main ~21.4 ms. No
  systematic cost at the ~1 ms scale, against a run-to-run spread of 3-5 ms.
- What UP9 could add at 5B: four fields on `District`, one `Backoff` per band (~115), one list add in
  `markRun`. Reading: within noise; if the gate's hard run reads the line red, it is main's borderline line
  (memory "Reds not the batch's pass"), passed by name, the fix the ship file's UP9-T row.
- The hybrid's one extra FAIL is expected: main's "a district's link gone and nothing else moved" asserts
  all but one job lazy, but without UP9's fixture 6b (the eager and quiet epochs before it) the band is
  still backing off after the early district's failed lazy try: 94 jobs, 0 lazy (UP9's own harness: 93
  lazy). That is slice A's design, and UP9's harness asserts both halves.

## The trace

The default playtest (`-Dplaytest.trace`) on main's classes (092130f4) and on UP9's final model: the eight
files byte-identical. The report differs only in its run time (46 s against 47) and the windows' (1.2 s
against 1.4); the layout watch compares the same 90,766 boxes, 0 moved. No ensemble.
Slice C changes how the guard's digest is worked out, never its value (sections 2h, 5 and 18 assert the
kept digest equal to the fresh one), and nothing in the model reads the map, so the trace stands for C.

## The checks

`./batch-check.sh --pick`, the data named (`JDK_JAVA_OPTIONS`): 19 harnesses, exit 3, the only reds main's
recorded ones (MapCheck's four yard lines, `docs/notes/gate.txt`). One timing line SOFT, not counted: the FX
side at 5B, 23.8 ms against 19 beside the other 18 harnesses (the section above). Section 5's two 5 ms lines
OK, JunctionCheck green, Stale 0 firm, the maps regenerated. `--timing` left to the gate.
