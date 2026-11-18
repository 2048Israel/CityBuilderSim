# FB8 notes - DM2's three follow-ups, the map's sidecar

Built on origin/main c471e961f (0.9.139), on batch/FB8, by a Claude session (Codex out of plan until 2026-11-17 19:35).

## What was read

CLAUDE.md, docs/batch.md, QUEUE.md's FB8 row, docs/queue/FB8.md, DM2-notes.md (the three follow-ups and the review
history behind them), docs/map/CityMap.md, docs/map/MapCheck.md, MapSiteHistory.java whole, CityMap's readSidecar,
siteMask, siteList, placeSited/relay and reconcile's site-history interval, MapCheck 2h-f and section 4,
docs/notes/known-reds.txt (LX1's two LongPlaytest entries under FB8) and LX1-notes.md, "The longer run".

## DM2-C: the rectangle codec's 256-plot assumption

- Found: `MapSiteHistory.rectangles()` matched a row's spans by `(column << 12) | ((width - 1) << 4) | kinds`, 8 bits
  for the width less one. Right for DISTRICT 256 (width - 1 at most 255); at a DISTRICT past 256 a span 257 wide takes
  the next column's key and two spans are written as one rectangle. Nothing refuses that: the reader expands it into a
  consistent but wrong history.
- ★ Decided: the key is laid out from DISTRICT itself (`(column * DISTRICT + width - 1) << 4 | kinds`, a long), not
  guarded at 256: it removes the assumption rather than fencing it, and the bytes written at 256 are the same (the key
  only groups; it is not written). The remaining limit is the journals' int records (`plot << 8` in jRec, the tightest;
  `plot << 4` in the site records): `CityMap.DISTRICT_MOST` = 2,896 beside DISTRICT, with the WHY, and a static guard
  that fails CityMap's loading past it. MapSiteHistory's comments say DISTRICT and FRAME, not 256 and 257.
- MapCheck 4b (`siteCodecEdges()`), through the history's own begin/changed/end: month 1 a whole district to field
  (one DISTRICT x DISTRICT rectangle, the widest and tallest), month 2 a whole row, the half row under it from the same
  column and the last column down the rest; asserts DISTRICT within DISTRICT_MOST against the constants, exactly 4
  rectangles, and the history read back plot for plot and written again byte for byte.
- Check: green on the branch (4 rectangles, 24 bytes). By hand against a mutant whose width field is 7 bits
  (`(width - 1) & 127`, the same failure a DISTRICT of 512 would give the old key): 3 rectangles and the history read
  back different, two FAILs (target/fb8/mutant-c-src). The old code at 256 is correct, so there is no red before on
  main: DM2-C is a guard, as its row says.

## DM2-A: the actual-map watched/unseen twin

- MapCheck 2h-f's city now has a twin (`Months.twin`): the same ground and counts, reconciled beside the watched map
  every month and never planned, through the wall's birth (m5), its cure by the actual rail and yard (m9), their
  removal at idle expiry (m134), the wall's return (m142) and its second cure (m143), and the arrivals after.
  `unseenWallTwin()` then asserts: the twin holds the watched map's source journals and no wall record
  (`CityMap.sameSources`, new: sameDistricts with the wall journals left out); the watched map's journal has births
  and releases in four months at least (14 kept, 14 released, 4 months); the twin's sidecar saved before its first view
  round trips byte for byte; its first view dates every wall event in the watched map's months, draws the same boxes
  and streets in every district, keeps its saved source journals, and the whole twin is then `same()` as the watched.
- Check: all six lines green on the branch. By hand against a mutant that dates a discovered wall at the view's month
  (`wMonth[at] = Math.max(month, in.month)` in DistrictPlan's wallRecord, target/fb8/mutant-a-src): every watched 2h-f
  line still passes and the twin's three after-view lines fail. The monthly-watched fixture alone could not catch that.
- Scope as briefed: MapCheck, plus the one comparison in CityMap; no model line, the trace unmoved.

## DM2-B: the site-mask mismatch, studied before the refusal is touched

- The route, reproduced: main c471e961f runs clean at 4,000 months on seed 0 (reloads at 375 ... 3968, all read back);
  LX1's own tree a3e714c69 gives both reds exactly (m3589 slot 9, m3952 slot 1, readSidecar null). On LX1's tree the
  refusal is `MapSiteHistory.matchesCurrent()`: district (1,0) has 96 plots the journal says MINED that are FIELD,
  (2,0) 398 (last records m3256-3276). The reload is faithful: at m3589 the live and loaded claims are identical. The
  live map's own history went wrong first, at m3551 (the first month `matchesCurrent` fails on the live map).
- The cause, instrumented at m3551 (target/fb8/lx1-tree3, probe-lx1c-err.txt): IRON's holding states moved, so
  `placeSited` relaid it with the same claims; the new walks need nothing of (2,0), whose runs (12 sites of one works
  owed into it from (1,0), no works counted there) `relay()` took off by writing `siteRuns[slot] = null`. `markRun()`
  moves a district's version with every runs write; `relay()`'s clearing did not, so (2,0) stayed at v64 and its kept
  `siteList()` still drew 398 mined plots (cached 398, fresh 0). `MapSiteHistory.end()` read that list: (2,0) "unchanged",
  (1,0) written 4311 -> 4009 where the fresh mask says 3913 (the 96 are (2,0)'s stale boxes over the seam). The lists
  were let go after `end()` (the states moved), so from m3551 the live map disagreed with its own history, and every
  save after it was refused. The same stale version also kept the plans over (2,0) (ownKey) and the reserves' guard.
- ★ Fixed at the writer: `relay()` moves the version (and `changes`) of each district whose slot it empties, as
  markRun() does; ownKey's javadoc already said "every write to its counts or its sites' runs moves" it. No reader
  change, no format change: a sidecar the old writer wrote with a wrong journal is refused, as it should be.
- ★ The refusal kept whole and documented on `matchesCurrent()`: the only mismatch on record is the writer's, and a
  per-district recovery (re-born at the load month) would have loaded wrong dates there and hidden this bug, which only
  the loud refusal found. The relaxing goes to the queue as DM4, to wait for a mismatch no writer caused.
- MapCheck 4c (`relayTakesLastRuns`), on a fresh copy of Jerus's city x 1 where its square holds iron (Squares.ironX1's
  square, 138 iron sites): the 20-site works (type 108) grows a works a month until one is owed into a district that
  counts none (m4, district (1,-1), 5 sites), every district planned; then the works all leave in the month an Iron Mine
  arrives (two claims moved: a relay). Asserts no district without runs draws a mine, the owed district's mined plots
  journalled back to field in that month, and the sidecar written then read back the same map and plans, byte for byte.
- Check: green on the branch (6 lines). By hand against the old relay (the two lines out, target/fb8/mutant-b-src):
  the three outcome lines FAIL (mines still drawn, no field record, sidecar refused); the three fixture lines pass.
- The known reds, red before and green after on the path that had them: LX1's tree a3e714c69 at 4,000 months, seed 0,
  gave "8 of 10 reload(s) read it back the same", "the city map across a save" and "the played city sidecar was refused
  or lost history" twice each (months 3589-3952), exit 1 (target/fb8/probe-lx1-4000.txt); the same tree with only
  relay()'s fix gives "10 of 10", neither flag, "All checks passed", exit 0 (target/fb8/probe-lx1-fixed.txt). Main's
  own 4,000-month path no longer reaches the route (10 of 10 at c471e961f, exit 0): the cover LX1 filed under FB8 is
  FB8's own and goes with its ship; the cause was the sidecar's writer, so nothing is re-filed.

## Checks

- `./batch-check.sh MapCheck LongPlaytest SaveFileCheck` at 8fa07a291: exit 3, known red only. MapCheck's four reds
  are FB9's entries in known-reds.txt (the 30th-month drawing, 2h-c's boxes on LX1's played city, the streets' one
  network and their surface); LongPlaytest (2,404 months, 6 of 6 reloads read back) and SaveFileCheck green; 4b, 4c
  and 2h-f's twin lines all OK; maps regenerated, Stale 0 firm.
- `sh tools/pick.sh origin/main HEAD`: 32 of 218 (BondCheck BridgeCheck BridgeMapCheck CapitalFlowCheck CrossingCheck
  EarthTerrainCheck GatewayCheck GlobeCheck HealthCheck IslandNetCheck IslandRailCheck JunctionCheck LandCheck
  LandmassCheck LongPlaytest MainLineCheck MapCheck MiningCheck PartnerCallsCheck PlanCheck PolicyPreviewCheck PortCheck
  PriceLevelCheck QuayCraneCheck RailDrawCheck ReadPathCheck ReplayCheck RunWorksCheck SaveFileCheck SlopeCheck
  TimingCheck TruckRouteCheck); review not needed (no money, saves or threading).
- No save format change: the reader is as it was; a sidecar the old writer wrote with a wrong journal is refused and
  the map drawn afresh, as before. The model reads no map: the playtest trace is unmoved, no ensemble.
- The branch at 4,000 months, seed 0 (ccdcb9924's classes, the nightly's length): "10 of 10 reload(s) read it back
  the same", "All checks passed", exit 0; its map summary is main's c471e961f's to the figure (48 districts, 78 of 157
  owned iron sites under mines), the trace unmoved (target/fb8/branch-4000.txt).
