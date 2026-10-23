# UP5 - the map's month, the rest: the chain's early exit, the close-up's month, a runs key per node

Jerus, 2026-10-23, at the review desk, on UP3's calls (`UP3-notes.md` §7):
★1 "Queue it", ★2 "Same batch as ★1", ★4 "In the same batch"; ★3
`MAP_MONTH_MS` stays 58 ("Keep 58"), to be tightened by this batch once ★2's
32 ms is gone. A map batch: nothing in the model reads the map, the trace
does not move.

1. **The chain's early exit** (UP3's brief item 5). At x 80 a month in one
   district re-plans every district after it in its band: about 130 jobs,
   1.5 s on the worker, 23-28 ms of the FX thread, so at 50x the far view's
   pictures never catch up. Stop re-planning when a district's carry out
   equals its old link's and its edges did not move. MapCheck 18's x 80
   one-district check then holds every node current, not "all but the ones
   over a stale picture".
2. **The L0 screen's month.** The band check (`ownStamp()` for 128 links,
   32 ms a month at 5B: `SITE_LISTS_KEPT` 64 thrashes in the walk, and the
   reserves' frame hash) and `input()` for every tile in view every month
   (5 ms even at x 1). A tile key like `nodeKey()`, and `ownStamp()` kept
   against its own inputs.
3. **A runs key per far node** (`CityRuns.frameHash()` over the node), so a
   month that lays a run re-colours only the nodes it crosses (3.5 ms at 5B
   today, every node).
4. **`MAP_MONTH_MS`**: re-measure MapCheck 18's 5B month after 1-3 and set
   the dial to nine tenths of the new median, as UP3 set 58.

Done when MapCheck 18 holds the kept pictures equal to a full redraw with
every node current at x 80, the timing harnesses green hard, and the notes
give the before and after per item.
