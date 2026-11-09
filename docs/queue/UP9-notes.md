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
