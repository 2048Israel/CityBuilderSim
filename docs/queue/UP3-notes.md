# UP3 - notes: the map's month redraws only what moved

*0.8.27, 2026-10-23. [The brief](UP3.md), from the plan/ui-perf merge
(2026-10-22: "the map doesn't regenerate every single building"). Built from
main d6166c7d (0.8.25); main (UP4's 0.8.26) merged in before the final checks.
The map draws nothing the model reads.*

## 1. Measured first, at his size

MapCheck's copies of Jerus's city were grown a month at a time by the path a
played month takes (`CityMap.reconcile()`): x 1 (4 districts), a new copy at
x 80 (125 districts, 11 a side; his cities have about 115, CL3's notes) and the
5B copy (14,661 districts, 153,533 mines). Each type with any grew +0.1% a
month at x 1 and x 80 (at least one) and +0.01% at 5B (section 5's). The runs'
types grew only every fourth month, since most months lay no run. A month
moved the counts of 2 of x 1's 4 districts, 1-2 of x 80's 125, and 250-590 of
5B's 14,661. The probe, `UP3/probe/Up3Probe.java`, ran on main's classes; MapCheck 18
(below) measures the same four parts, today's way and the new way, in one run.

## 2. What was built

- **A far node kept** (`MapTiles.KeptNode`, `nodePixels(KeptNode, ...)`; slice A).
  It holds three layers, each redrawn only when its own key moves:
  - *the land*: the city's cover a pixel at a time, which district each pixel
    lies in, and the edge. Its key is the plots owned under the node and its
    ring, and the district count; ownership only grows, as `ownStamp()` argues.
  - *the districts*: each pixel's colour before the runs. Only the pixels of a
    district whose version, built plots, owned plots or current picture moved
    are repainted.
  - *the runs*: the highways, railway, yards and quays. Its key is
    `CityRuns.version()`, the highways' reach and the shore's works.

  The composed pixels are `nodePixels()`'s, which stays as it was: it is
  today's full redraw, the reference MapCheck compares against. The kept path
  copies its colour rule, and a comment in both says they move together.
- **The minimap** (`ui/MiniMap`, slice B). Each node of the stamp and of the
  preview is drawn from its KeptNode. A picture is begun at most every
  `STAMP_EVERY_MS` (250 ms): four a second at 50x, against ten. The frame is
  found only when a picture is about to be begun, so a month that is not drawn
  costs nothing.
- **The far view** (`ui/MapView.paintNodes()`). A node whose version moved is
  re-coloured only when its `MapTiles.nodeKey()` moved: the land, the runs, and
  each district over it (its version, plots and picture). A node drawn short
  of a picture is still re-coloured on every move, since that is what asks for
  its pictures again, but only while the view asks for pictures at all
  (`picturesFit()`). Before this, 5B's far view at 64 plots a pixel drew
  pictures it never asked for, and every node was re-coloured every month.
- **A link's stamp kept** (`CityMap.Link`). It is worked out once, in the
  constructor: `own` and `in` are final, and a carry is never written after it
  is made. `planned()` asks for it for every tile a frame draws; `stampOf()`
  folds every {month, code} pair of the carry. `linksAstray()` is the
  harness's check of this.

## 3. Before and after

From MapCheck 18's last run on the final model, on the PC's wall clock (its CPU
clock steps 15.6 ms): the median month of 21 at x 1 and x 80 and of 9 at 5B.
Across the section's runs the figures moved by about 5%. Each cell reads today's /
now's, in ms a month.

| | x 1 | x 80 | 5B |
|---|---|---|---|
| the stamp (frame and node) | 4.47 / **0.15** | 6.24 / **0.23** | 2.32 / **0.78** |
| the far view's screen, fitted on the city | 12.01 / **2.76** (24 nodes, 4 re-coloured) | 19.84 / **19.97** (28 nodes, 32 draws: the chain, ★1) | 29.57 / **18.24** (54 nodes, 14 re-coloured; their keys 3.5) |
| an L0 screen re-stamped (ready, input) | 5.31 | 6.71 | 33.53 |
| the chain's FX side (jobs handed out, adopted) | 0.71 | 23.50 | 0.11 |
| **the month's FX-side map work** | 22.52 / **8.96** | 56.29 / **50.76** | 65.67 / **52.80** |

- **The stamp.** It repaints 4 of x 1's 4 districts, 2 of x 80's 125 and 35 of
  the 1,296 on 5B's stamp. The runs layer was drawn in exactly the months the
  runs moved: 6, 11 and 3. With the pace, the stamp at 50x costs 1-3 ms a
  second, against 23-65 (ten a second).
- **The probe on main** (Up3Probe, step 1) put the stamp at 2.2-2.8 ms. It
  put the far screen at 11, 11 and 30.5 ms, the L0 screen at 6.2, 8.2 and
  13.8 (its 5B had no mines), and `ownStamp()` over every district at 0.08,
  2.6 and 1,583 ms.
- **The far view at x 80** gains little, because of the chain. A month that
  places in district 0 leaves all 121 built districts' pictures not current
  until the chain has re-planned them. The worker then runs about 130 jobs, about
  1.5 s a month. The FX thread spends 23-28 ms a month handing them out and
  adopting them. Meanwhile every node over a stale picture is dithered and
  re-coloured twice. This is the brief's item 5, the chain's early exit: ★1.
- **The L0 screen** is not this batch's, and is now half of 5B's month. The
  first ask after a month walks the band (`checkBand()`): `ownStamp()` for each
  of 128 links, about 110 µs each at 5B. Of that, 5.5 µs per neighbour's site
  list, since `SITE_LISTS_KEPT` (64) thrashes in the walk. A trial at 1,024 made
  it 101 ms (`siteListVersion.retainAll` on every miss), so it was put back.
  The rest is `runs.frameHash()`'s reserves. Separately, `input()` for every tile
  costs about 5 ms a month even at x 1: ★2.

## 4. How the pixels were proved the same

MapCheck 18 draws each month twice on two `MapTiles` of the same map:

- *today's*: `MiniMapFrame.pixels()` with no keeps, which is `nodePixels()` as
  0.8.25 had it;
- *now's*: the kept nodes, kept across the months as ui/MiniMap keeps them.

It compared them in 73 months, over x 1, x 80, 5B and the played city before
and after a land purchase inside its frame. That was 159 stamps and 159
previews, 9,253,800 pixels, all equal, and the districts each asks for were
equal as lists. Each far node whose key had not moved was compared with a
fresh `nodePixels()`: 2,644 nodes, 1,320 of them held through their month, all
equal. Slice A's probe (`UP3/probe/KeptProbe.java`) did the same at 32, 8 and
512 plots a pixel and on the Earth (Rotterdam x 1, 10 nodes past the rim):
0 differences.

## 5. The checks

**MapCheck 18** (new): A MONTH REDRAWS ONLY WHAT MOVED (about 58 s).

- *Pixels.* The kept stamp and preview equal a full redraw, pixel for pixel,
  asking for the same districts. The far nodes held by their key equal a fresh
  draw.
- *One district.* A month that places in one district (fixture: exactly one
  district's version moved) re-colours only the far nodes over it and its
  neighbours: on 5B, 1 of 40 nodes. Where the pictures are drawn (x 80), the
  nodes over districts whose pictures the chain re-plans move as well, and no
  others: 16 of 28, 12 of them for the chain.
- *The static layer.* It is not redrawn by a month that adds no run. A month
  that lays one redraws it once.
- *The land layer.* It is drawn again by the purchase and in no other month.
- *Stamps.* `linksAstray()` is 0 after each of 24 months on x 1 and x 80, with
  links kept.
- *`MAP_MONTH_MS`* (58): the median month at 5B is 51.6 to 54.2 ms in the
  section's runs, against today's 63.4 to 67.2 (★3).

PlanCheck and LongPlaytest's layout watch are unchanged and green.

## 6. The trace

Main (95d02db3, 0.8.26) built apart from its own sources, and the batch (main merged in), the default playtest traced on each (`-Dplaytest.trace`): all eight CSVs byte-identical, and the report identical. So no ensemble (EN1's rule).

## 7. For Jerus's eye (UP3-C)

On the PC at 50x, with a city of his size:

- The stamp follows the city about four times a second.
- A highway laid shows on the stamp within a quarter of a second.
- The preview on hover is current.
- The land office's far view shows no stale node after a month or a
  purchase.

His calls:

- **★1 The chain's early exit** (the brief's item 5, left out of this batch;
  "under Pending if 1 shows pictures arriving late at his size"). Step 1 shows
  exactly that. At x 80, a month in one district re-plans every district after
  it in its band. That is about 130 jobs and 1.5 s on the worker, so at 50x the
  pictures never catch up. It also costs the FX thread 23-28 ms a month. The fix:
  stop re-planning when a district's carry out equals its old link's and its
  edges did not move. Queue it as its own batch?
- **★2 The L0 screen's month.**
  - The band check (`ownStamp()` for 128 links, 32 ms a month at 5B; the site
    lists' 64 thrash, and the reserves' frame hash).
  - `input()` for every tile in view every month (5 ms at x 1).
  - A tile key like `nodeKey()`, and `ownStamp()` kept against its own inputs,
    would take most of it. Neither was in the brief.
- **★3 MAP_MONTH_MS.** The first figure was 24, half the 47 ms the probe
  measured at 5B. But that probe's 5B had no mines, and the section's own step
  1, on the 5B copy with its 153,533 mines, measured 63.4 to 67.2 ms in four
  runs (mean 65). So the dial is 58, nine tenths of that, read on the median
  month: a month that lays a run moves
  every far node's key, as today. The L0 screen's 32 ms is the floor until ★2.
  Setting it otherwise is his call.
- **★4 The far view's runs key is the city's.** A month that lays a run
  re-colours every far node, as today, plus the keys (3.5 ms at 5B). A runs key
  for each node (`CityRuns.frameHash()` over it) would keep that to the nodes
  the run crosses.
- **Minor:** `MapCheck -q` prints no FAIL line on Windows. It filters on
  `"FAIL\n"`, and `printf`'s `%n` writes `"\r\n"`. This was so before UP3.

## 8. The slices

Each went to an Agent subagent on Sonnet 5.5 at xhigh effort, one at a time,
and was reviewed as a stranger's diff.

| slice | what | the review |
|---|---|---|
| A | `MapTiles.KeptNode`, `nodePixels(KeptNode, ...)`, `nodeKey()`; MiniMapFrame's overloads; KeptProbe | Taken as written. `wanted()`'s order, the Earth's void, and the land-ring and reference checks were all checked against `nodePixels()` line by line. |
| B | ui/MiniMap's kept nodes and `STAMP_EVERY_MS`; ui/MapView's far nodes by key | Taken. After C's finding I changed it: a partial node is held where the view asks for no pictures (`picturesFit()`), mirrored in MapCheck 18. |
| C | MapCheck 18 | x 80's one-district check was red for the chain (index 0, every picture stale). It now allows the nodes over a stale picture and no others; 5B's check, the brief's own, is as written. `MAP_MONTH_MS` 24 was red because its base was wrong (★3). |
| - | the link's stamp kept, `linksAstray()` (mine) | A few lines. |

`ui/UserInterface` was not touched (UP2 works there).

## 9. The suite and the timing

- **The suite** (`batch-check.sh`, the final model with main merged, the Earth file named): 130 harnesses, 130 passed (main's baseline before the batch: 129 of 129). MapCheck 18 took 56 s of MapCheck's run. Stale: 0 firm findings. The maps are regenerated.
- **`--timing`, hard** (the timing harnesses alone): 10 of 10. MapCheck 18's month at 5B: the median 53.9 ms against `MAP_MONTH_MS` 58 (today's way 67.0); MapCheck 5's 10B month and 12's stamp within their bounds.
