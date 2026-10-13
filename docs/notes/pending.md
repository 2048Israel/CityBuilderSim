# Pending for the project record

Lines the docs pass could not write to the project itself. The next Cowork
session moves them over and empties this file. Newest at the top. It holds
changelog and todo lines for the claude.ai project, which the sessions that
write them cannot reach.

## 0.8.0 (batch I14, 2026-10-13) - for `claude/changelog.md`

**0.8.0 - the release that finishes industry; the materials on the map (I14).** The oil chain (0.7.76-0.7.98) and
the five materials chains (0.7.110-0.7.143) are whole, and now drawn as themselves: every type of iron, oil, coal,
stone and copper stands on its resource's sites by its type of field, on as many as it takes (a Large Open-Pit Mine
on twenty hematite sites, counted once), laid as LandManager.fits() packs them - coal's, stone's and copper's first
types had been placed nowhere. Each kind has a look of its own (open pits in the ore's colour, a shaft, quarries, a
leach plant, the wash plant, camps, planted forest), each processing industry's works one campus in its colours (the
arc furnaces keep their places), and a zone's districts are tinted in its sector's colour with the legend naming it.
Found on the way: a footprint of a tile's 32 plots threw the district's plan; capped at a cell's 31. MapCheck 15
holds it, a screen over the industry 32 ms of thread CPU. Sidecar FORMAT 7; no save change; playtests byte-identical.
Gate: BuildMenuCheck 139, Stale 0 firm, MapCheck and PlanCheck green; EarthTerrainCheck 8's Earth screen bound red, as
on main on the same machine. See docs/queue/I14-notes/fixI14-notes.md.

## 0.8.0 - for `claude/todo.md`

- ~~I14 (UI), 0.8.0: the map and the release~~ - done 2026-10-13 (0.8.0): with it I5's 20-site works drawn as one
  mine, I6's coal mines and preparation plant and I7's Dimension Stone Quarry drawn as plain MINING, and I9's ★10
  (coal's, stone's and copper's first types placed nowhere); W1's note stays open (below).
- CHECK (Jerus, on the PC): the materials on the map - a pan over an industrial city, a zone's tint - beside
  docs/queue/I14-notes/png/ (QUEUE.md Pending, I14-C; fixI14-notes §8).
- CONFIRM: a plantation drawn on a cell's 31 x 31 plots of its 105 x 105 (I14 ★3); the arc furnaces and mini-mills
  not gathered into steel's campus (★4); sited works grey in the far views (★6); no toggle for the zones' tint (★7).
- W1's walled street piece over a dense iron cluster stays open (QUEUE.md Pending, W1-P).
- CONFIRM: the map's zone tint is LandMap.ZONE_COLOURS, a palette apart from ZoningScreen.ZONE_COLOURS, so a zone's
  colour on the map is not its colour on Policy > Zoning (the I14 docs pass) - one palette, or two on purpose?
- FOUND (the cloud orchestrator, 2026-10-13): EarthTerrainCheck 8's Earth screen bound (MapCheck.SCREEN_MS, 80 ms)
  went red on the cloud machine mid-session, on main without I12 too (81 ms at d4e12dc; 105-109 ms on the batches'
  trees), having passed in the session's first two full runs: a timing on a shared VM, not a batch's. On the PC it
  should pass; if it does not, the bound or the Earth screen's raster wants a look. MapCheck 14's title is two joined
  strings, so docs/harnesses.md shows no heading for it (the docs pass, left: outside I14's change).
- Section 0, under the manual's entry: the build is 0.8.0, the release that finishes industry (save format still 39,
  harnesses still 108) - for the manual's next edition (a milestone).

## 0.7.143 (batch I12, 2026-10-13) - for `claude/changelog.md`

**0.7.143 - the materials pictograms drawn (I12).** The nine materials industries' Operations pages open on their
month as a picture, the refinery's pictogram made general (spec-materials 2.13; the study's mockups 1 to 5): a mine's
fields as vessels filled to what is left, or a works' feeds by where they came from, imports hatched; the works in
ranks, each with its run and in a word the gate one more would fail, a flow that skips a rank drawn as a band through
its column; the tank of what left the works under the industry's roof (pit, camp, furnace, kiln, mill); who took it;
the power and the month's note - every ribbon one scale, in tonnes, the forest's in cubic metres - and a strip of cells
under it. New model class IndustryPicture (pure: the month traced end to end, then laid out), painted by
ui/SectorScreen with the refinery's painter. IndustryViewCheck 12 holds it: ribbons to scale, each node the view's
figures, every label's figure the view's, nothing off the frame at 1,389 x 868. A words probe of every string and nine
headless renders are in docs/queue/I12-notes/. No save change. Gate: suite 109 of 109, BuildMenuCheck 139, Stale 0 firm, both playtests 8/8 traces byte-identical.

## 0.7.143 - for `claude/todo.md`

- CHECK (Jerus, on the PC at 1,389 x 868): the nine materials Operations pictures by eye, beside
  docs/queue/I12-notes/png/ and fixI12-words.txt (QUEUE.md Pending, I12-C).
- CONFIRM: copper drawn in tonnes of the good, not of copper contained as the mockups say (I12 ★2: the model holds no
  copper content); the three lines over a works (I12 ★5).
- Section 0, under the manual's entry: the build is 0.7.143 (the manual's header says 0.7.135; save format still 39,
  harnesses still 108); the nine materials industries' Operations pages open on a picture now, for its next edition.
