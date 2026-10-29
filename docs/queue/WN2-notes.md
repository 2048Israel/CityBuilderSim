# WN2 - the nations on the map: borders, dots, labels and the card ({next}, 2026-10-28)

The batch's record. Jerus took the nations study on 2026-10-20 ("do rec"), and the night's line on 2026-10-28
("you are to continue all night until 7am"). The brief is `docs/studies/spec-nations.md` §9's WN2 row, §6.1 (the
borders) and §8's board 1 (`docs/studies/nations/1-the-world-map.html`), on WN0's world file and WN1's `Nations`.
It is the nations lane's third batch, built on WN1's branch (RL1) and on main once WN1 landed (27717959, 0.8.67).
Opus wrote two briefs; two Sonnet 5.5 slices at xhigh coded them one after the other (A: `NationsMap` and its
checks; B: the screen), each reviewed as a stranger's diff and committed apart.

## 1. What it is

- **`NationsMap`** (new, the model's half; nothing in the month reads it): the 7,980 border lines kept once at five
  levels of detail (`LOD_KM` 0, 1, 4, 16, 64 km: Douglas-Peucker measured as a chord's miss on the sphere, each level
  a subset of the one before), each line with the cap that holds it. A globe frame takes only the lines whose cap
  meets the view's, at the coarsest level under half a pixel (`LOD_PX`), projected through the view's ortho and cut
  to the canvas; the last view's runs are kept. On the city's plane, only the lines with a segment crossing the box
  of the city's land and offers (`crossing()`, kept until the box moves). Each nation's dot at its node, its radius by
  output (the board's: 1.6 + 1.9 px a root of a trillion, 2.5 to 11 px); its name and "US$2.24T · 41.5M" on its land
  where its ground in view holds them, tried on a coarse owner grid (8 cells a degree, built once from the owner
  layer), largest output first, never overlapping. The hit test (a dot, else the exact 15" owner) and the card.
- **The card**: GDP (real US$ of 2015), people and GDP a head, each "Nth of 196"; this year's real growth and
  prices; its money, its rate a US$ and its worth in D$; the region and the km from the city. Estimates carry ○ and
  projections ◐, as the leaderboard marks them.
- **The screen** (`ui/MapView`, `GamePrefs`): on an Earth world whose world file is here, the expanded map's head
  has a group of two chips, **Nations** and **Borders** (the board's lit and unlit look), both on by default and kept
  in `settings.json` beside the saves (`nationsOnMap`, `bordersOnMap`). On the globe: the borders dashed white as one
  stroke, then the dots and labels, under the city's offers and ring. On the plane (the near view and the land
  office's small map): only the borders crossing the city's land and its offers. Hovering a dot gives its figures
  and "Click for its card"; hovering other land names its nation or area first. A click on a dot or a nation's land
  opens its card beside the point: the name, its region as a tag, a ×, the distance, then the rows. Esc closes it
  first, and only one card is open at a time (it and the buying card close each other). Over the city's plane the
  land office's hover and click come first, even under a dot (review: a city founded at its nation's largest place
  would otherwise lose its offers' clicks there). The legend has a head, "▾ Legend · click to fold", the only part
  that takes the click (the body stays mouse-transparent); folded is kept too (`mapLegendFolded`), and the legend
  lists "A border" and "A nation, by its economy" while their chips are on. The world file opens once on the map's
  worker and stays open (the exact owner under a click reads it); `Game.getNations()` is asked there too. A generated
  world, or no world file, shows no chips and draws nothing.
- **What it costs** (the PC, wall clock: its CPU clock steps 15.6 ms): the build 147-189 ms once on the worker
  (`BUILD_MS` 600); a globe view's borders 0.96 ms at worst (`BORDERS_MS` 3), the whole planet 14,814 vertices at the
  4 km level where the file's own would be 56,914 (`VERTICES_MOST` 45,000); a view's dots and labels 0.10 ms
  (`MARKS_MS` 0.4). Each budget is about three times its measure, new; no existing budget moved.

## 2. The calls (each ★ took the recommended way, BP1)

1. ★ **Trade routes: no chip and no lanes here.** Its lanes need WN3's trade by partner, and a chip that draws
   nothing reads as broken. SL1 (0.8.6x) makes the city's sea lanes shared and branching, and Jerus worries that many
   lanes slow a late city. **Recommended:** WN3 adds the chip with its lanes, drawn from the city's trade by partner,
   with the board's floor (none under D$0.3M a month) so they stay few.
2. ★ **A generated world's partners as arrows at the edge** (§6.4) wait for WN3 too: the arrows are its partners, and
   D23's stand-in place is WN3's market. Here a generated world draws no borders and no nations, and shows no chips.
   **Recommended:** WN3.
3. ★ **Dots and labels at the far views alone** (the globe's tiers); the near view, the city's own, draws only the
   borders that cross its ground. **Recommended:** yes.
4. ★ **The card's GDP is real US$ of 2015**, WN1's figure, its row labelled so; its land's price (WN5) and its trade
   with us (WN3) are left out until they exist, with no "coming" words, and the "All its measures" door waits for the
   Rankings tab. **Recommended:** yes; WN4 or WN6 can add the D$ figure.
5. ★ **Labels are tried on the coarse owner grid**, where a coastal tile wholly one nation's counts its sea as that
   nation's (the owner layer does not tell land from sea); Natural Earth's label point keeps the names on land.
   **Recommended:** yes.
6. ★ **On the globe every border in view is drawn**, over the plane's ground too at the GROUND tier; on the plane's
   own tier only those crossing the city's ground. **Recommended:** yes.

## 3. For WN3 and the later batches

- The card has room for WN3's rows (what it buys from us and sells us this year, the haul) and WN5's (its land's
  price): `NationsMap.card()` builds the rows; add them there, not in the screen.
- The Trade routes chip goes in the head's nations group beside Nations and Borders (`MapView.buildPane()`), its
  setting in `GamePrefs` beside theirs.
- Found on the way: WN0's rate for Venezuela in 2000 is 6.8e-12 bolívares a US$ (the money of 2021 carried back over
  three redenominations), so its card's money row reads "VES 0.0000000000068 a US$". The file's data, left as it is.

## 4. The checks, the trace

- `./batch-check.sh --pick`: exit 0, five harnesses in 291 s (GlobeCheck 150, MapCheck 612, TimingCheck, TruckCheck,
  BridgeMapCheck; no SOFT line), Stale no firm finding. `MapView` is read by no harness: the screen is WN2-C's. Its
  review line, threading: the world file opened and `NationsMap` built once on the map's `WORKER` behind a static
  `CompletableFuture` (a `synchronized` getter asks it once, a `volatile` field holds it), read on the FX thread only
  once done; `Game.getNations()` (itself `synchronized`) asked on `WORKER`, its result handed back through
  `Platform.runLater`; `NationsMap`'s three caches touched from the FX thread alone, and `WorldData.ownerAt()` is
  `synchronized` on its tile cache.
- **GlobeCheck 9-12** (new): on the world fixture always and the full file under `-Dcbs.world`: the levels keep each
  line's ends and every dropped vertex within its tolerance; the views' culling (over Europe fewer than a quarter of
  the lines considered), the margin, the visible side and the cache; the borders, the marks and the build timed; the
  dots' radii, labels never overlapping and each on its own ground, Canada and the US labelled over the North
  Atlantic, Luxembourg and Belgium keeping only their dots; the hit test against the fixture's expected cells, the
  open Atlantic and Canada's interior; the card's rows, ranks, ordinals and words at every threshold.
- **MapCheck 19** (new): a box of a city's ground round Baarle-Nassau is crossed by the Belgium-Netherlands line and
  by no line `crossing()` misses (each segment sampled a thousand times); the same box round Rotterdam by none; the
  plane's runs cut to the canvas; a generated world draws none.
- **The trace**: unchanged by construction: the diff touches no class the month runs (`NationsMap` is read by
  `ui/MapView` alone, `GamePrefs` is `settings.json`). So no ensemble (EN1's rule).
- **Not checked by eye**: the screen (WN2-C).
