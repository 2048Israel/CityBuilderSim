# ET2 - Buildable by slope (0.8.43, 2026-10-24)

The brief: `QUEUE.md`'s ET2 row and `docs/studies/spec-earth2-terrain.md` §10's ET2 row, with
§3.2, §3.4, §4.1, §4.2 (the order of use), §4.4; Jerus's decisions of 2026-10-12 ("all rec": D6,
D7, D9, D21) and the CONFIRM item E7 ★10. Five slices, each written by a Sonnet subagent and
reviewed and committed here, and one fix of the land buyers found in the Medellín run.

## The model

- `LandManager`, BUILDABLE BY SLOPE. One pool still: `allocatedSqFt`, with `worksSqFt` (the part
  works stand on) and `preparedSqFt` (the foothill prepared, never falling) beside it, saved as
  `landWorksSqFt` and `landPreparedSqFt`. One helper (`split()`) derives the rest: works on the
  steep first, then foothill, then flat; buildings on flat first, then foothill; the steep no works
  stands on (`idleSlopeSqFt()`) is not free for a building and is free for a works.
- `Game.standsAnywhere(t)`: every template with a site, 16 of them (the mines, quarries, wells and
  the two offshore templates, which take no land). Used by both orders, the quote's free ground,
  every release with a template, `runAhead`, BusinessInvestment, BuildCard and AutoBuilder.
- `getUtilisation()` and the new `getUsedShare()` read the share of the ground a building may use
  (built less the works on the steep, over owned less the steep). The playtest's two land buyers
  (`keepGroundAhead`, `roomToGrow`) read it. Before this, Medellín's buyer saw the city 89% full
  while no building could stand, stopped buying for a thousand months and ended at 122,512 people.
- Works on ice (D9) are not done: the owned figure has never counted ice (`World.dry()`), so a
  mine on a glacier needs an ice area in the books first (ET2-C ★4).

## The preparation (D7, §4.1)

- `SitePreparation`: a pad 30 m wide on the foothill's mean grade, **19%**, moves g x W / 4 =
  1.425 m³ of earth and g = 0.19 m² of wall a m². The 19% is fitted from the study's measured class
  shares by an exponential fall of land with grade between the class edges: 17.9% to 19.7% at nine
  sites. The study's per-tile histograms were not kept in the repository, so they could not be read.
- The rates: **$20 a m³** of balanced cut and fill with compaction, no haul (US$15-30 a cubic yard
  is the commonest 2026 benchmark, snaptrude.com; contractor guides lower), and **$430 a m²** of
  segmental retaining wall face ($15-35 a sq ft in national guides, fixr.com; $70-140 engineered,
  installitdirect.com, San Diego 2025). Together **$110.2 a m²**. These are cost guides, not
  RSMeans (★1).
- Points: on the House's founding price a point, $26.6k (266 for 10), so 0.004143 points a m²: an
  8,000 sq ft House pad is 3.08 points beside the House's 10, about $82k at founding. The money is
  those points at today's builders' price for house work (`nonMaterialCost(House)` / 10), so it
  keeps up with wages (★2).
- In the quote's sticker (taxed with the work), owed on the site as progress below nothing
  (`BuildingsStacks.addPreparation()`) before the first building finishes, billed to Construction
  with the points. No new flow: it reaches the contract, Construction's earnings, GDP and the money
  audit by the paths every order takes (SlopeCheck 6). A shell cancelled before its pad keeps the
  pad owed (`resumeShell`); the materials are drawn in proportion to all the owed points, so some
  arrive while the pad is cut.

## The map and the founding

- `DistrictPlan.Input.relief` (filled by `CityMap.readGround` a tile at a time); `groundRow`'s dry
  and `cvKeepable` read `World.buildable()`; a new `unfit[]` (steep, very steep, ice) refuses
  streets where the sea did. Highways and rail as before (ET4). Mines sit on their sites with no
  ground test, so steep sites stand. Flat first on the map: `growthOrder` adds `FOOTHILL_RANK`
  (one cell) times a cell's foothill share to its rank.
- The snap (`EarthTerrain.siteBuildable`) looks for buildable ground; a spot with none within 5 km is
  refused: "No ground a building may stand on within 5 km: the slopes here are steep. The nearest
  buildable ground is 9.0 km west." (Everest's southern slopes; past 50 km, "There is none within
  50 km"). The world's founding site is recomputed from the key, so an Earth city saved before
  could load on another site: `EarthSite.SLOPE_FORMAT` 46, `SAVE_FORMAT` 46, the Earth floor 46,
  `OLDEST_LOADABLE_FORMAT` 45 (generated cities load). EarthTerrainCheck's MARGIN spot moved 9 km
  so its snap stays out of an ice cell; its premise is unchanged.
- Screens: Build's quote adds "Preparing 743 m² of foothill: 3.1 points of builders' work, $82k";
  the land office "Steep ground only mines, quarries and wells may use: X (Y of it worked)" and
  "Foothill built on: X, prepared: Y". Probed in fixtures, not seen in a played game.
- Renders in `docs/queue/ET2/` (`tools.SlopeRenders`): `et2-a-medellin-city.png`, the city at month
  2,000 with no building or street on its 2,495 owned steep plots in view; `et2-c-refusal.txt`, the
  card's words at Medellín, a steep Andes spot and Everest. The works render was skipped: the
  Medellín run builds no works (no ore in its offers; the nearest iron is 32 km off).

## Checks and traces

- SlopeCheck (new, `slope`): a building, a mine and a street on each class; the order of use; the
  preparation against `SitePreparation`'s constants; prepared once; both orders through the
  builders' books, GDP and the money audit; the plan on a district with steep, very steep and ice
  bands; the founding; a Medellín run counted. Mutations (no `addPreparation`, idle steep free for
  buildings, `allocate` not raising prepared, streets ignoring `unfit`) each turn it red.
- `./batch-check.sh` with earth-2 named: 138 of 139; MapCheck 8 is main's known red (FW11). The
  timing figures over budget are soft (another batch's JVM).
- The default playtest: byte-identical but the run-time lines, so no ensemble.
- `--earth rotterdam`: byte-identical but the run-time lines and the new ground line ("flat 177.695
  km2, foothill 0.000, steep 0.000"): Rotterdam is flat.
- `--earth medellin`, before (main) and after: 278,258 people at month 4,005 and 257,761; the ground
  at the end flat 39.3 km², foothill 27.8, steep 5.4 (none worked); 22.5 km² of foothill prepared,
  93,085 points (0.74% of the points billed), in 724 orders. The audit and every reload clean in
  both. Its trace is a baseline of its own.

## Calls for Jerus (ET2-C, under Pending)

1. **The rates**: cut and fill $20 a m³, wall $430 a m² of face, the mean grade 19% (fitted, not
   measured). From cost guides; keep them or name better sources.
2. **The point's price** read off the House ($26.6k a point at founding), the money at today's
   builders' price for house work.
3. **The land buyer buys steep ground it cannot build on**: Medellín ends with 5.4 of its 72.4 km²
   steep and idle, no mine built. ET3's price by class (steep at the base) is the fix (rec.).
4. **Works on ice (D9)**: a batch of its own with ET3 (rec.), or later.
5. **Earth cities saved before 0.8.43 are refused** (the Earth floor at 46); generated cities load.

## Found on the way

- The founding card's Andes probe (-13.2, -72.5) read "16 km east" in slice C's probe and "5.3 km
  south" in the render tool's; the lattice search's first hit depends on the snap's centre, and the
  two probes may not have used the same one. Not chased.
