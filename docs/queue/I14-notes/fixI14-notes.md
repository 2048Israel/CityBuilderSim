# fixI14 - the materials on the map, and 0.8.0

Batch I14 (docs/queue/I14.md; docs/studies/spec-materials.md 5's I14 row), on `batch/I14`. Version 0.8.0, the
release that finishes industry. No save format change (39); the map's sidecar is FORMAT 7.

## 1. What was built

- **Every works on its own sites, by type** (CityMap, THE WORKS ON THEIR SITES BY TYPE). A type's site is read off
  the template (`BuildingsTemplate.site()`: resource, type of field, sites taken), no longer off the good it makes.
  SITED is iron, oil, coal, stone and copper, counted by SLOTS (each resource's field types). Each resource's types
  are claims laid in LandManager.fits()' order - typed before ANY, the larger first, ANY onto the first type first -
  each works on as many sites as it takes (a Large Open-Pit Mine on twenty hematite sites), counted once, in the
  district of its first site; a works with no whole room on its slots stands in the founding district on none. A
  month that moves only the last claim on its slots fills or empties it from where it stood; anything else relays
  the resource. District.siteRuns ({holding, type, sites} per slot) is what siteList() paints from.
- **The looks** (BuildingVisual.LOOKS, TileRaster's THE MATERIALS AS THEMSELVES): open pits (benches stepping to
  the floor in the ore's colour, measured from the whole site so a 33-plot coal site is one pit across tiles),
  underground mine (yard and headframe), quarry (straight benches), leach plant (heaps in rows and a pond), coal
  wash plant (conveyor), logging camp (log stacks), planted forest (crowns in rows), and each processing industry's
  works in its colours with roof ridges (GROUP_FILL: steel, cement, copper, wood). In the legend; and the far
  views' pictures have codes for the works groups, the wash plant, camps and stands (DistrictPicture).
- **Each industry's campus** (CityMap's EVERY INDUSTRY'S CAMPUS, DistrictPlan's THE CAMPUS): Iron & Steel's,
  Cement & Concrete's, Copper Refining's and Wood Products' works each into one district and packed in touching
  cells after the refinery's, by the refinery's rule (BuildingVisual.Type.campusGroup()).
- **Zones tinted by district** (CityMap.zoneTint(), LandMap.zoneAreas()/zoneColour()/zoneLegend(), ui/MapView's
  zoneTints()): each zone's districts in its sector's colour at ZONE_ALPHA (22%) under the offers, the legend
  naming each zone ("Zoned for Forestry"); nothing with no zone set.
- **GameVersion 0.8.0**, headed as the release that finishes industry (the oil chain 0.7.76-0.7.98, the materials
  0.7.110-0.7.143), then this batch. `GridConversion.buildNumber("0.8.0")` = 8,000 > 7,143 (0.7.143), and outside
  SHARED_FROM..SHARED_TO; no other reader parses the version as a number (SaveSlotCheck and SaveFileCheck compare
  it as a string; Build EXE.bat's findstr reads the literal). README names no version (its own rule), so unchanged.

## ★ Calls

All confirmed as built (Jerus, 2026-10-18, "rec all"). The map's zone palette and Zoning's become one: ZT1.

- ★1 SITES FROM THE TEMPLATE. `t.site()` decides, not "makes its resource's good": the Dimension Stone Quarry
  (DIMENSION_STONE) and the two copper concentrators (COPPER_CONC) stand on sites now; the Coal Preparation Plant,
  the crews and the stands stand on none.
- ★2 THE MAP PACKS AS fits() DOES, NOT BY FIELD. A works takes the next whole run of its slot's sites in the walk's
  order (holdings not worked out first, a holding's districts nearest first); its twenty sites can straddle two
  fields of its type or two districts. The model's fits() puts it on one typed piece; the map does not know pieces.
- ★3 A PLANTATION ON A CELL. A Tree Plantation (11,111 plots) and a Woodlot (1,111) draw as planted forest on a
  31 x 31 box, every footprint's cap (found on the way, 7); their ground used still counts their whole land. Drawing
  one over several cells or as forest painted over its district is a bigger change to the plan.
- ★4 THE ARC FURNACES KEEP THEIR PLACES. The Electric Arc Furnace (11) and Steel Mini-Mill (12) draw in steel's
  colours but are not gathered (BuildingVisual.NOT_GATHERED): every saved map and PlanCheck 1's port of the
  prototype (box for box) hold them where they are; a campus would move them.
- ★5 A CAMPUS AT CITY SCALE. A works group standing across more than CAMPUS_REACH (64) districts has no campus and
  places as any industry - else every month reads every district for the one holding the most (the 5B and 10B
  copies' month went from ~1 to ~15 ms). Drawn canonically, a works group no district holds whole is shared as any
  industry; the refinery's rule (the roomiest) is unchanged.
- ★6 FAR VIEWS. A works on sites stays "Mines and wells" grey in the district pictures (the plan's SITE_MINED holds
  no type); boxes (works, wash plant, camps, stands) have codes of their own.
- ★7 THE ZONES' TINT. Whole district squares (not clipped to owned ground), districts with no free ground skipped;
  a zone covers what it holds free plus what its sector built in it; the districts holding the most of its sector's
  ground first, then the rest outer-first for a sector with outer buildings (mines, farms, Forestry), inner-first
  otherwise. No toggle: on whenever a zone is set, off when none (the brief's "off when no zone is set").
- ★8 COKING COAL UNDERGROUND, OXIDE COPPER LEACHED. The look is read off the field type (COKING a shaft, OXIDE a
  leach plant), not the name.
- ★9 LOOKS FROM 3 PX A PLOT (LOOK_FROM); below, a box of the look's fill. A Logging Camp is one plot: its small
  square in the camp's colour, no logs.
- ★10 SIDECAR FORMAT 7 (a district's sites by slot); FORMAT 4 to 6 still read, their iron and oil on the first
  slots until the index counts them again. No save change.
- ★11 THE FIXTURES' IRON MINES ON HEMATITE. MapCheck's copies and §5's months and PlanCheck 5 cap Iron Mines by
  hematite sites, the game's own rule since 0.7.124 (the comment's "Game.hasDepositFor()"), not by all iron sites:
  the 5B copy stands 153,533 mines where it stood 206,094. Not a premise moved: the fixture now asks what the game
  asks.

## 4. The traces

None. Both playtests on the batch's classes against main's traces (scratchpad base/main, 0.7.143's): pt 8/8 and
pte 8/8 traces byte-identical; both "Nothing" in their findings - every month's districts summed to the buildings
(pt 10 districts at m4,000, pte 13), every reload read the map back the same.

## 5. Checks

- Build (mvn compile, the interface too) silent. Stale 0 firm.
- MapCheck green, all fifteen sections. 15 (new): 40 kinds of the nine industries, 40 with a look of their own, 13
  on sites as their templates say; fixture at plot (320512, 375808), 22 cells out, sites by type all ample; the
  districts sum to the counts; 7,716 drawn of 7,716, no works on part of its sites; Large Open-Pit Mine 1 on 20
  sites, Large Magnetite Complex 1 on 20, Large Surface Mine 1 on 4, Large Quarry 1 on 3, Large Copper Mine 1 on 8,
  every site of its type; one more Iron Mine takes one site and moves none; steel 14 works in 7 touching cells,
  cement 6 in 5, copper 8 in 5, wood 12 in 1; every look drawn in its own colours (per site: pits 60 of 60, shafts 2, quarries 7, leach 2, wash 2,
  camps 2, stands 4, works 44); the zones' tint; the screen over the industry 32.3 and 52.6 ms of the thread's CPU in
  two runs (0.16-0.27 ms a tile, the slowest tile 0.43 ms), against 80.
- BuildMenuCheck: 139 of 139 described, all checks passed.
- MapCheck 5: the month's change at 5B worst 1.29 ms and at 10B 2.07 ms of thread CPU (bound 5); the unbounded
  first month 9.0 ms (6.1 before). The screens 31.8-32.3 ms as before.
- PlanCheck green; ChildcareCheck, ForestryCheck, ReadPathCheck, SaveFileCheck, LandCheck, GlobeCheck green.
- EarthTerrainCheck: only 8's Earth screen bound red, 104.9 ms on the wall clock against 80 - red on main on this
  machine too (81-109 ms); this batch adds a look test per building and four ints a site to the tile path.

## 7. Found on the way, and not done

- FOUND: a footprint of 32 plots a side (a tile's) threw DistrictPlan.tryPlace() (shapeSlot out of bounds): a Tree
  Plantation, a Woodlot, and a Large Surface Mine, Underground Coal Mine or Large Copper Mine standing on no site
  would have stopped their district's plan. Footprints are capped at DistrictPlan.INTERIOR (31); no type below
  32 moves. Whether this was QUEUE's CC1-M (the map lost once in a childcare run) is not known.
- FIXED: fixI9's ★10 - coal's, stone's and copper's first types were placed nowhere ("districts do not sum").
- NOT DONE, W1's walled street piece: this batch's drawing does not meet it (DistrictPlan.join() is untouched);
  left, under QUEUE.md Pending W1-P.
- NOT DONE: a toggle for the zones' tint (★7); the far views' sited works by look (★6); a plantation over more than
  one cell (★3).
- Docs pass: maps (BuildingVisual, CityMap, DistrictPlan, DistrictPicture, TilePainter, TileRaster, LandMap,
  ui/MapView, MapCheck, PlanCheck, GameVersion), dials (BuildingVisual's LOOK_ and GROUP_ arrays, NOT_GATHERED,
  CityMap.CAMPUS_REACH, FORMAT 7, TileRaster.LOOK_FROM/BENCHES, LandMap.ZONE_ALPHA, MapCheck.MATERIALS_*),
  harnesses.md (MapCheck 15), docs/tree.md's map row (done here).

## 8. For Jerus on the PC (1,389 x 868)

The headless renders are in docs/queue/I14-notes/png/ (MapCheck 15's fixture: Jerus's city x 1 with one or two of
every materials kind, 3 x 3 tiles at 4 px a plot round each kind; zones-by-district.png the far picture with a Coal
Mining and a Forestry zone tinted). On the PC:

1. Load a city with mines, quarries or a forest crew; Land office > Expand; pan (W, A, S, D) to its industry at the
   near view: each pit on its own site, a Large Open-Pit Mine as twenty pits, coal and copper pits in their ore's
   colours, the quarries' benches, the shaft's headframe, the leach plant's pond. The first load re-lays the sites by
   type (the picture shifts once for the site works).
2. The works: a city that builds steel's integrated route, a cement plant, a smelter or a sawmill - each industry's
   works together in one district, in its own colours; the arc furnaces where they were.
3. Policy > Zoning: set a zone (Forestry, Coal Mining) - its districts tinted on the map, "Zoned for ..." in the
   legend; remove it - the tint goes.
4. The legend: three columns of 16, the works and looks after the refinery.
