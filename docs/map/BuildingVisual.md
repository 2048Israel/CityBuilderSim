# BuildingVisual.java - 541 lines · 24 methods · 60 constants · model

`ham/citybuildersim/BuildingVisual.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which kind, whether it stands on a resource's sites, and whether the map places it from the outside of the city in.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5 and 2.6,
> star 10). Jerus's map mockup (city-map.html) drew ten kinds of building -
> homes, shops, offices, industry, farms, utilities, schools, health, safety,
> and mines and wells - each with its fill, its edge and its footprint (the
> mockup's star 8), and the game has its building types in eighteen
> categories. This is the one table between them, so the tile painter, the
> raster, the districts' pyramid and the legend J4 draws all read one answer.
> It is the picture only: nothing in the model reads it.
> 
> WHAT IT DECIDES, AND FROM WHAT. A type's category gives its class; three
> things are read off the template rather than its name, as the house rule
> asks (BuildingsTemplate's care field): a road is an INFRASTRUCTURE type with
> a road capacity and no transit, its kind by its freight grade (none gravel,
> some paved, all a highway); flats are the homes with more than one dwelling
> (the apartment types, the mockup's star 9 by type, not by people); a mine
> or well stands on the sites of the resource whose good it makes (since
> 0.8.0 the sites its template declares: THE MATERIALS DRAWN AS THEMSELVES,
> below). One type
> is named by its permanent id, home care (22), drawn as a home because it
> is run from homes (spec-land star 10); id 15 was the home daycare until
> 0.7.71 made it the Small Childcare Centre, a centre, drawn as care.
> 
> WHAT A TILE DRAWS (0.7.64, batch L2; J3b's visual counts gone): one
> building for every model building, of its type, on the type's own land
> (FOOTPRINTS below) - a Low-Rise Apartments block one block of 2 x 3 plots,
> not a street of houses; a kind the city has none of is not drawn.
> 
> RAIL AS TRACK (0.7.72, batch N3). Jerus: "rail you add must connect to
> the rail network, and rail terminals prefer to sit near mines." The
> railway's lines - the Rail Spur and the Freight Line, a RAIL type that
> is not a terminal - are drawn as track, their own land in plots laid
> along a line (track()), as a road's are; a Rail Terminal (by its
> permanent id, RAIL_TERMINALS) is a building, a yard beside its track.
> 
> THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; spec-materials 5's
> I14 row). The nine materials industries drew as two kinds: a mine on the
> first site of its resource or, for every other mine, quarry, camp and
> plantation, a plain grey "Mines and wells" box, and every works a plain
> violet "Industry" box. Now a type standing on a resource's sites says so
> from the template's own site (BuildingsTemplate.site(): its resource,
> the type of field and how many sites it takes - a Large Open-Pit Mine
> twenty), and each kind of building has a look of its own (LOOKS): an open
> pit, an underground mine, a quarry, a leach plant, a coal wash plant, a
> logging camp, planted forest - and each processing industry's works, of
> one colour and placed as one campus (GROUPS: the refinery's units since
> 0.7.97, then steel's, cement's, copper's and wood's). Still the picture
> only: nothing in the model reads it.
> 
> TRANSPORT (0.8.1, batch FR0; spec-freight 4.2, D15). An eleventh class
> takes the railway's yards, the sea terminals and the transit networks -
> the RAIL, PORTS and INFRASTRUCTURE categories - out of Industry and
> Utilities, in a slate between offices' blue and utilities' teal
> (#5e7d8c, star FR0-6); a sea terminal keeps its own fill.

**Uses:** [BuildingType](BuildingType.md) (15), [Resource](Resource.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (6), [Sectors](Sectors.md) (5), [Deposit](Deposit.md) (5), [Refining](Refining.md) (3), [TileRaster](TileRaster.md) (3), [DistrictPlan](DistrictPlan.md) (2), [World](World.md) (1), [LandManager](LandManager.md) (1), [StrategicReserve](StrategicReserve.md) (1), [Freight](Freight.md) (1)

**Used by (22):** [ChildcareCheck](ChildcareCheck.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [CityShore](CityShore.md), [DistrictPicture](DistrictPicture.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [LongPlaytest](LongPlaytest.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 67 | THE ELEVEN CLASSES (the mockup's TYPES, in its order; TRANSPORT, 0.8.1's, last) |
| 133 | · the materials' looks (0.8.0, batch I14) |
| 196 | · road kinds |
| 237 | ONE TYPE |
| 436 | FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `BuildingVisual.HOME` | `0` | Homes: houses, and the flats drawn darker. |
| 74 | `BuildingVisual.SHOP` | `1` | Shops: convenience and grocery stores, the bank's branches, boutiques, diners. |
| 76 | `BuildingVisual.OFFICE` | `2` | Offices: business services. |
| 78 | `BuildingVisual.INDUSTRY` | `3` | Industry: bakeries to steel, construction and the car plants. |
| 80 | `BuildingVisual.FARM` | `4` | Farms: on open grass only. |
| 82 | `BuildingVisual.UTILITY` | `5` | Utilities: power and water. |
| 84 | `BuildingVisual.SCHOOL` | `6` | Schools, colleges and universities. |
| 86 | `BuildingVisual.HEALTH` | `7` | Health: clinics, hospitals, care and the cemeteries. |
| 88 | `BuildingVisual.SAFETY` | `8` | Safety: police and the jails. |
| 90 | `BuildingVisual.MINE` | `9` | Mines and wells: on their resource's sites. |
| 92 | `BuildingVisual.TRANSPORT` | `10` | Transport: the railway's yards, the sea terminals and the transit networks (0.8.1, batch FR0; spec-freight 4.2, D15). |
| 95 | `BuildingVisual.CLASSES` | `11` | How many classes: eleven - what the pyramid sums a node's buildings by (spec-land 2.5). |
| 98 | `BuildingVisual.CLASS_NAMES` | `{ "Homes", "Shops", "Offices", "Industry", "Farms", "Utilities", "Schools", "...` | The classes' names, as the legend writes them (the mockup's). |
| 102 | `BuildingVisual.FILL` | `{ 0xffeaa572, 0xfff2c94c, 0xff5b8fd6, 0xff9076ba, 0xffe0cb84, 0xff2ea89e, 0xf...` | Each class's fill, 0xAARRGGBB - the mockup's TYPES. |
| 106 | `BuildingVisual.EDGE` | `{ 0xff9a5a2c, 0xff987718, 0xff2c5590, 0xff55407a, 0xffa99550, 0xff17635d, 0xf...` | ...and its edge, drawn from 6 px a plot (the mockup's). |
| 110 | `BuildingVisual.FLATS_FILL` | `0xffd07a44` | Flats' fill: the mockup's FLATS, a darker orange than a house. |
| 113 | `BuildingVisual.FLATS_EDGE` | `0xff7e4119` | ...and their edge. |
| 116 | `BuildingVisual.CAMPUS_FILL` | `0xff4a3f5c` | The refinery's units (0.7.97, campus()): mockup 3's refinery, #4a3f5c, a darker violet than industry's, so the campus reads as one... |
| 119 | `BuildingVisual.CAMPUS_EDGE` | `0xff7a6a90` | ...edged in its #7a6a90. |
| 122 | `BuildingVisual.TERMINAL_FILL` | `0xff2e3640` | A sea terminal's apron (0.7.97, berth()): mockup 3's quays' grey, #2e3640... |
| 125 | `BuildingVisual.TERMINAL_EDGE` | `0xff4a5866` | ...edged in its #4a5866. |
| 128 | `BuildingVisual.TANKS_FILL` | `0xff2a3540` | A tank farm's tanks (0.7.97, the refiners' Tank Farm and the city's Strategic Reserve): mockup 3's tanks, #2a3540... |
| 131 | `BuildingVisual.TANKS_EDGE` | `0xff7f8ca6` | ...ringed in its #7f8ca6. |
| 136 | `BuildingVisual.PLAIN` | `0` | A type's look: as its class draws it, a box of its class's colours (every type but the materials'). |
| 138 | `BuildingVisual.PIT` | `1` | ...an open pit on its sites: benches stepping down to its floor, in its resource's colour (the iron mines, the surface coal mines, the sulphide copper mines). |
| 140 | `BuildingVisual.SHAFT` | `2` | ...an underground mine on its site: the site's yard and spoil, its headframe in the middle (the coking coal's mine). |
| 142 | `BuildingVisual.QUARRY` | `3` | ...a quarry on its sites: straight benches cut in the stone (the three quarries). |
| 144 | `BuildingVisual.LEACH` | `4` | ...a leach plant on its site: its heaps in rows and its pond (the oxide copper's Leach & SX-EW Plant). |
| 146 | `BuildingVisual.WASH` | `5` | ...a coal wash plant: a dark works with its conveyor (the Coal Preparation Plant). |
| 148 | `BuildingVisual.CAMP` | `6` | ...a logging camp: a clearing and its log stacks (Forestry's crews). |
| 150 | `BuildingVisual.STAND` | `7` | ...planted forest: trees in rows on its ground (the Tree Plantation and the Woodlot). |
| 152 | `BuildingVisual.WORKS` | `8` | ...a processing works: its industry's own colours (GROUP_FILL), placed with the rest of its industry's as one campus. |
| 155 | `BuildingVisual.LOOKS` | `9` | How many looks. |
| 158 | `BuildingVisual.LOOK_NAMES` | `{ "", "Open pit", "Underground mine", "Quarry", "Leach plant", "Coal wash pla...` | Each look as the legend names it (PLAIN and WORKS by their class and their industry instead). |
| 162 | `BuildingVisual.LOOK_FILL` | `{ 0, 0xff6e5a4a, 0xffa39783, 0xffcfc8b4, 0xff5fae93, 0xff3a3a42, 0xffb58c5c, ...` | Each look's fill, 0xAARRGGBB: a pit's and a quarry's from their resource's colour (pitColours()), the rest their own - a shaft's yard, the heaps' green, the wash plant's coal, the camp's sawdust, the plantation's dark... |
| 165 | `BuildingVisual.LOOK_EDGE` | `{ 0, 0xff3d3129, 0xff5c5347, 0xff8d8673, 0xff2f6f5b, 0xff17171b, 0xff6d4c2a, ...` | ...and its edge from 6 px a plot. |
| 168 | `BuildingVisual.NO_GROUP` | `0` | A works' campus (GROUPS): none... |
| 170 | `BuildingVisual.REFINERY` | `1` | ...the refinery's units (0.7.97, campus())... |
| 172 | `BuildingVisual.STEEL` | `2` | ...Iron & Steel's works (sectors.HeavyIndustry: the arc furnaces and mini-mills, the Scrap Yard, the integrated route)... |
| 174 | `BuildingVisual.CEMENT` | `3` | ...Cement & Concrete's kilns and cement plants... |
| 176 | `BuildingVisual.COPPER_WORKS` | `4` | ...Copper Refining's smelters, refineries and rod mills... |
| 178 | `BuildingVisual.WOOD` | `5` | ...and Wood Products' sawmills and mills. |
| 181 | `BuildingVisual.GROUPS` | `6` | How many groups, NO_GROUP among them. |
| 184 | `BuildingVisual.GROUP_SECTORS` | `{ null, null, Sectors.HEAVY_INDUSTRY, Sectors.CEMENT_CONCRETE, Sectors.COPPER...` | The sector each processing works' group is (Sectors' keys), by group; none for NO_GROUP and the refinery's, which campus() reads. |
| 188 | `BuildingVisual.GROUP_NAMES` | `{ "", "Refinery", "Iron & steel works", "Cement works", "Copper works", "Wood...` | Each group's works as the legend names them. |
| 191 | `BuildingVisual.GROUP_FILL` | `{ 0, CAMPUS_FILL, 0xff6b4a42, 0xffb4b0a6, 0xffb0663a, 0xff8a6a44 }` | Each group's fill: the refinery's CAMPUS_FILL, steel's rust, cement's grey, copper's own and the mills' timber. |
| 194 | `BuildingVisual.GROUP_EDGE` | `{ 0, CAMPUS_EDGE, 0xffa0705c, 0xff76726a, 0xff6e3b1d, 0xff5a4128 }` | ...and its edge from 6 px a plot. |
| 199 | `BuildingVisual.NOT_A_ROAD` | `0` | Not a road. |
| 201 | `BuildingVisual.GRAVEL` | `1` | A gravel road: no freight grade. |
| 203 | `BuildingVisual.PAVED` | `2` | A paved road: a freight grade under one. |
| 205 | `BuildingVisual.HIGHWAY` | `3` | An elevated highway: a freight grade of one. |
| 208 | `BuildingVisual.DRAWN_AS_HOMES` | `{ 22 }` | The permanent ids of the care types drawn in the homes' colour, because they are run from homes (spec-land star 10): Home Care Service (22) - still drawn, one for one, on its own land (0.7.64). |
| 211 | `BuildingVisual.RAIL_TERMINALS` | `{ 64, 141 }` | The permanent ids of the rail types drawn as a yard beside the track, not as track (0.7.72): the Rail Terminal (64), and since FR7 the Rail Depot (141), the small goods yard a railway starts with. |
| 214 | `BuildingVisual.RAIL_DEPOT` | `141` | The permanent id of the Rail Depot (FR7 slice 3): the small yard of RAIL_TERMINALS, drawn at its own footprint with a few sidings, not a Rail Terminal's fan of eight. |
| 222 | `BuildingVisual.FREIGHT_LINES` | `{ 63 }` | The permanent ids of the railway lines whose track makes the main line double (0.8.1, batch FR0; spec-freight 4.3): the Freight Line (63). |
| 232 | `BuildingVisual.NOT_GATHERED` | `{ 11, 12 }` | The permanent ids of the works drawn in their group's colours but placed as any industry, not gathered into its campus (0.8.0, batch I14): Iron & Steel's Electric Arc Furnace (11) and Steel Mini-Mill (12), the scrap r... |
| 235 | `BuildingVisual.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | Square feet in a plot: 30 m squared in square feet, 9,687.5 - what a template's land is turned into plots by. |
| 395 | `BuildingVisual.PIT_DUST` | `0xffb8a58a` | The dust a pit's benches are blended toward: dry spoil, #b8a58a. |
| 398 | `BuildingVisual.PIT_SHADE` | `0xff2a2620` | ...and the shade its floor is: #2a2620. |
| 499 | `BuildingVisual.ORDERS` | `java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>())` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 479 | **type** `public final class BuildingVisual` | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which kind, whether it stands on a resource's sites, and whether the map pla... |
| 65 | 1 | `private BuildingVisual()` |  |

### THE ELEVEN CLASSES (the mockup's TYPES, in its order; TRANSPORT, 0.8.1's, last) (lines 67-132)

### the materials' looks (0.8.0, batch I14) (lines 133-195)

### road kinds (lines 196-236)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 3 | `static boolean depot(Type t)` | Whether the type is the Rail Depot, the railway's small first yard (FR7 slice 3). |
| 225 | 5 | `static boolean freightLine(Type t)` | Whether the type is one of FREIGHT_LINES: a railway line whose presence doubles the main line. |

### ONE TYPE (lines 237-435)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 27 | **type** `public record Type(int id, BuildingType category, int cls, boolean flats, int road, Resource site, boolean ...` | One building type as the map draws it. |
| 294 | 1 | `public int campusGroup()` _(in BuildingVisual.Type)_ | The campus it is placed in (0.8.0): its group where it is gathered, else NO_GROUP - what the city map and the district plan place by. |
| 297 | 1 | `public boolean drawn()` _(in BuildingVisual.Type)_ | Whether it is drawn as a building: not a road, nor a railway line (0.7.72), nor at sea (0.7.91). |
| 300 | 4 | `public int fill()` _(in BuildingVisual.Type)_ | Its fill: its class's, the flats darker, and since 0.7.97 the refinery's units, a terminal and a tank farm their own (still of their class's in the blocks and the legend's eleven); since 0.8.0 a works its group's and ... |
| 306 | 4 | `public int edge()` _(in BuildingVisual.Type)_ | Its edge. |
| 312 | 1 | `public boolean materials()` _(in BuildingVisual.Type)_ | Whether it is one of the materials industries' own (0.8.0): drawn by a look of its own, not its class's box. |
| 316 | 44 | `public static Type of(BuildingsTemplate t)` | A template as the map draws it. |
| 371 | 10 | `static int lookOf(BuildingsTemplate t, Resource site, Deposit.FieldType field, int group)` | A type's look (0.8.0, batch I14), off the template as the house rule asks: on a resource's sites, iron's, coal's and copper's mines an open pit - but coking coal's, which is mined underground, a shaft, and copper's ox... |
| 383 | 4 | `static int lookFill(Type t)` | A look's fill for a type: a pit's floor and a quarry's stone from its resource's colour, the rest the look's own. |
| 389 | 4 | `static int lookEdge(Type t)` | ...and its edge: a pit's rim. |
| 401 | 4 | `public static int[] pitColours(Resource r)` | A pit's colours from its resource's: {its floor, its benches, its rim} - the ore's colour darkened toward PIT_SHADE, blended halfway with PIT_DUST, and the dust - so an iron pit reads red, a coal pit black and a coppe... |
| 407 | 16 | `public static int classOf(BuildingType c)` | A category's class (the mockup's ten, and since 0.8.1 TRANSPORT). |
| 428 | 7 | `public static Type[] table(List<BuildingsTemplate> templates)` | Every type of a list of templates, indexed by id (null where no template has that id): the table the map is drawn from. |

### FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) (lines 436-541)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 6 | `public static int[] footprint(Type t)` | The whole plots a type is drawn on, {across, down}: its land in plots, round(sqrt(plots)) across and round(plots / across) down, at least one each way and never past a cell's interior (DistrictPlan.INTERIOR, 31; a til... |
| 470 | 6 | `public static int cells(Type t)` | The whole plots a type is drawn on: its footprint's, or a road's or a railway line's own plots rounded (a Gravel Road 46, a Paved Road 26, an Elevated Highway 7; a Rail Spur 72, a Freight Line 248) - what a district's... |
| 484 | 3 | `public static int[] rankedTypes(Type[] types)` | The order buildings are dealt to a district's tiles and placed on a tile: the largest footprint first (cells()), then by type id - every drawn type of the table by rank. |
| 489 | 3 | `public static int[] cellsById(Type[] types)` | ...and each type's whole plots, by id (cells()): the table's order and plots worked out once a table, the painter's every tile. |
| 494 | 4 | `public static int[][] footprintsById(Type[] types)` | ...and each type's footprint by id, {across[], down[]} (footprint()). |
| 502 | 18 | `private static int[][] orderOf(Type[] types)` | A table's {order, ranks, cells, across, down}, kept by the table itself. |
| 521 | 10 | `private static int[] rank(Type[] types)` |  |
| 533 | 3 | `public static int[] placeRanks(Type[] types)` | ...and each type's rank in it, by id (-1 for a road or no type). |
| 538 | 3 | `public static double plotSide(Type t)` | The side a one-plot building is drawn at, as a share of its plot: the square root of its land in plots, a House's 0.91 and a Convenience Store's 0.72, so its drawn area is its own; a whole plot at a plot or more. |

