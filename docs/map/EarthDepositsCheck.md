# EarthDepositsCheck.java - 789 lines · 36 methods · 16 constants · harnesses

`ham/citybuildersim/EarthDepositsCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> Deposits where the geology says (0.8.12, batch EG1; docs/studies/spec-earth2-terrain.md 8.5): oil by a province's known petroleum, iron on shields and in recorded districts, the Sydney coalfield in its own basin, fields a third as many and three times wider, and every cluster on its own province's ground - and, counted in a real run, how often each rule fires.
> 
> WHY. Jerus's Nova Scotia cities found oil and iron all round them (the
> study, 8.1 and 8.3): within 150 km of Halifax the world drew 417 iron
> fields and 155 oil fields, 102 of them at sea off a fold belt that holds
> neither (the shelf's sea weight carried into its nearshore water). The study found the code had not changed and
> the table was applied as designed: the weights only rescaled the generated
> world's densities within a narrow range (a fold belt's oil weight was one,
> iron was nought only on a shelf), known petroleum could only raise oil, and
> a cluster's centre fell anywhere in its 61 km cell (8.4). EG1 changed the
> weights, the placement and the size of a field; this is the proof that it
> did what the study said it would, with Nova Scotia as its natural fixture,
> and the count, in a real run, of how often each rule fires - a rule that
> never fires proves nothing about the model that has it.
> 
> THE DATA. EarthCheck's fixture holds the whole geology grid and the whole
> province table, so 1 to 4 and 7's first half run anywhere; the field counts
> of 5 and 6 need the whole planet's far layers and the full file (-Dcbs.earth=
> <path>, EarthData.locate()), and 7's second half on it too; where it is not
> named each says so in one line and 8 (the save) runs without any file.
> 
> What this has to prove (spec-earth2-terrain 8.5):
>   1. THE HAND TABLES STAND ON THE DATA: every KNOWN row's code and name are
>      the data's, its file figure nought and Geology's known petroleum for
>      the code the row's; the patch's code is the data's; every district is
>      20 km wide at the least and on the planet. Row counts printed.
>   2. OIL FOLLOWS KNOWN PETROLEUM: each USGS code's area on the sphere and
>      the Earth's known petroleum a km2 recomputed by brute force over the
>      geology grid are Geology's; each province's oil multiplier is
>      (OIL_ALLOWANCE x W_OIL[class] + its code's known a km2 over the mean)
>      x Z_OIL, and a province with none exactly the allowance's.
>   3. IRON ON SHIELDS AND IN RECORDED DISTRICTS: a shield's multiplier is
>      W_IRON[SHIELD] x Z_IRON, every other class's IRON_ALLOWANCE x its
>      weight x Z_IRON; at a district's centre and 1 km inside its radius the
>      point's is the district's IRON_DISTRICT_WEIGHT x Z_IRON, and 1 km
>      outside it the province's.
>   4. THE SYDNEY COALFIELD: Glace Bay, New Waterford and Sydney Mines read
>      the Sidney Basin (5220), a basin, twice the Appalachian Foldbelt's
>      coal; Halifax and Antigonish still read the fold belt (5219).
>   5. NOVA SCOTIA, AS SPEC 8 FOUND IT (full file): the worlds of Jerus's two
>      saves, their iron within 150 and 300 km at most a tenth of the study's,
>      Halifax's oil on land likewise, every oil field at sea in a province
>      whose sea holds oil, the Scotian Shelf's oil above its allowance; and
>      controls - Mesabi holds more iron than a shield spot outside every
>      district, Tom Price a tenth of Mesabi's at the least, Calgary oil.
>   6. FEWER AND BIGGER (full file): every Earth field holds FIELD_SCALE x
>      EARTH_FIELD_SCALE sites at the least and MAX_SITES at the most, a
>      pool's field count is its old count over that, and the mean sites of
>      the fields drawn is that x MEAN_SITES within SIGMAS standard errors -
>      a generated world's FIELD_SCALE x MEAN_SITES the same way.
>   7. A CLUSTER STAYS ON ITS PROVINCE'S GROUND: of the fields within 300 km
>      of the playtest's Rotterdam (the fixture's, and the full file's, the
>      world LongPlaytest --earth rotterdam plays) and of Halifax's (the full
>      file's), those on ground that holds none of their resource are at most
>      what World counted (fieldsOnNone) and under half a percent of the
>      fields; World's five counters are printed as how often the rule fires,
>      and the harness's own detector is tried on a sea plot and an oil sea's,
>      so that a count of nought is a detector that can see.
>   8. THE SAVE: an Earth save of the format before DEPOSITS_FORMAT is
>      refused in BEFORE_DEPOSITS_WORDS, one of this format is not, and a
> ... (6 more lines in the source)

**Uses:** [Geology](Geology.md) (82), [Resource](Resource.md) (69), [World](World.md) (57), [EarthData](EarthData.md) (45), [EarthSite](EarthSite.md) (11), [Deposit](Deposit.md) (8), [EarthTerrain](EarthTerrain.md) (7), [GameVersion](GameVersion.md) (5), [EarthProj](EarthProj.md) (4), [EarthCheck](EarthCheck.md) (3), [Founding](Founding.md) (3)

## Sections

| line | section |
|---:|---|
| 221 | 1. THE HAND TABLES STAND ON THE DATA |
| 263 | 2. OIL FOLLOWS KNOWN PETROLEUM |
| 354 | 3. IRON ON SHIELDS AND IN RECORDED DISTRICTS |
| 433 | 4. THE SYDNEY COALFIELD |
| 472 | THE FIELDS ROUND A SITE |
| 521 | 5. NOVA SCOTIA, AS SPEC 8 FOUND IT |
| 616 | 6. FEWER AND BIGGER |
| 688 | 7. A CLUSTER STAYS ON ITS PROVINCE'S GROUND |
| 775 | 8. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `EarthDepositsCheck.out` | `System.out` | Where the harness prints: the console. |
| 102 | `EarthDepositsCheck.SIGMAS` | `3` | A sample's mean is the model's within this many of its own standard errors (the sample's, sd over the root of the fields): the same three standard deviations EarthTerrainCheck holds its draws to. |
| 105 | `EarthDepositsCheck.AREA_REL` | `1e-9` | A province or code area is the brute force's within this share, relative: a billionth (both sum the same cells, only in another order). |
| 108 | `EarthDepositsCheck.MULT_REL` | `1e-12` | A multiplier is the formula's within this share, relative: a million millionth (the same sums of a few terms). |
| 111 | `EarthDepositsCheck.MIN_POOLS` | `200` | The pools whose field counts and lists 6 compares, at least: 200 (spec 8.5's "fewer and bigger" holds on a world, not on a pool). |
| 114 | `EarthDepositsCheck.REACH_CELLS` | `6` | The cells round the founding site that 5 and 7 draw the fields of, each way: 6, 368 km, past 300 km and the cell the site stands in. |
| 117 | `EarthDepositsCheck.CONTROL_CELLS` | `2` | ...and the controls', each way: 2, 123 km, past their 50 km. |
| 120 | `EarthDepositsCheck.CONTROL_SEED` | `1` | The seed the controls' worlds are drawn with (spec-earth2-terrain 8.3's controls ran on several; one is enough to count). |
| 123 | `EarthDepositsCheck.TENTH` | `0.1` | A tenth: how much of what the study found a finding of 5 may leave (spec 8.5: "oil and iron all round them" to a handful). |
| 126 | `EarthDepositsCheck.KM` | `{ 50, 150, 300 }` | The distances 5 counts the fields within, in km (the study's: spec 8.3). |
| 129 | `EarthDepositsCheck.FIELD_RESOURCES` | `Arrays.stream(Resource.values()).filter(Resource : : inFields).toArray(Resour...` | Every resource drawn in fields. |
| 135 | `EarthDepositsCheck.HALIFAX` | `new Spot("Halifax", 44.65, - 63.60, 758_809_812L)` | Jerus's two saves (spec-earth2-terrain 8.1): slot 1 and slot 2 as the study read them, by their seeds. |
| 136 | `EarthDepositsCheck.EASTERN_SHORE` | `new Spot("Eastern Shore", 45.17, - 61.72, 284_719_415L)` |  |
| 139 | `EarthDepositsCheck.HALIFAX_IRON` | `{ 45, 417, 941 }, HALIFAX_OIL = { 14, 155, 661 }, HALIFAX_COAL = { 2, 11, 20 }` | The study's counts within 50, 150 and 300 km (spec-earth2-terrain 8.3), by resource: iron, oil (land and sea together), coal. |
| 140 | `EarthDepositsCheck.SHORE_IRON` | `{ 72, 320, 699 }, SHORE_OIL = { 8, 182, 646 }, SHORE_COAL = { 0, 3, 15 }` |  |
| 143 | `EarthDepositsCheck.HALIFAX_OIL_LAND_150` | `53` | ...and, of Halifax's 155 oil fields within 150 km, the 53 on land (spec 8.3: "53 + 102"). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `static int fails` |  |
| 481 | `final World w` |  |
| 482 | `final Map<Resource, List<Field>> by` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 705 | **type** `public class EarthDepositsCheck` | Deposits where the geology says (0.8.12, batch EG1; docs/studies/spec-earth2-terrain.md 8.5): oil by a province's known petroleum, iron on shields and in recorded districts, the Sydney coalfield in its own basin, fiel... |
| 91 | 4 | `static void check(String label, boolean ok)` |  |
| 96 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 132 | 1 | **type** `record Spot(String name, double lat, double lon, long seed)` | A spot of the study: its name, where, and the seed of the world there. |
| 145 | 42 | `public static void main(String[] args) throws Exception` |  |
| 189 | 13 | `static EarthData openFull() throws Exception` | The full file's reader when it is named or lies at data/earth-2.bin, else null (and a named file that is not there fails). |
| 203 | 5 | `static Set<String> codesOf(EarthData d)` |  |
| 209 | 5 | `static Set<Geology.Kind> classesOf(EarthData d, Geology geo)` |  |
| 216 | 4 | `static int rowOf(EarthData d, String code)` | The row of a USGS code's first province in the data's table, 0 for none. |

### 1. THE HAND TABLES STAND ON THE DATA (lines 221-262)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 38 | `static void tables(EarthData data, Geology geo)` |  |

### 2. OIL FOLLOWS KNOWN PETROLEUM (lines 263-353)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 3 | `static boolean near(double a, double b, double rel)` | A relative closeness: both nought is equal. |
| 273 | 8 | `static String codeAt(EarthData data, double lat, double lon)` | The USGS code under a point as the data and the patches give it, by the harness's own reading: a patch's box first, else the raw grid's province; null for none. |
| 282 | 71 | `static void oil(EarthData data, Geology geo)` |  |

### 3. IRON ON SHIELDS AND IN RECORDED DISTRICTS (lines 354-432)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 8 | `static double[] east(double lat, double lon, double km)` | The point `km` due east of (lat, lon) on the sphere of EarthProj.RADIUS_M: {lat, lon}, the longitude in -180 to 180. |
| 368 | 58 | `static void iron(EarthData data, Geology geo, EarthData full)` |  |
| 428 | 4 | `static int firstOf(EarthData data, Geology geo, Geology.Kind k)` | The first province row of a class. |

### 4. THE SYDNEY COALFIELD (lines 433-471)

| line | len | member | says |
|---:|---:|---|---|
| 436 | 35 | `static void sydney(EarthData data, Geology geo)` |  |

### THE FIELDS ROUND A SITE (lines 472-520)

| line | len | member | says |
|---:|---:|---|---|
| 477 | 1 | **type** `record Field(Deposit d, double km, boolean sea, int row)` | A field near the site: the deposit, its centre plot's distance from the founding site in km, whether that plot is sea, and the province (row) under it. |
| 480 | 38 | **type** `static final class Near` | The fields of some resources in the cells round a world's founding site, counted by their centre plots' distance (the plots' 30 m, the projection's near-site km). |
| 484 | 18 | `Near(World w, int reach, Resource...resources)` _(in EarthDepositsCheck.Near)_ |  |
| 504 | 5 | `List<Field> within(Resource r, double km, Boolean sea)` _(in EarthDepositsCheck.Near)_ | The fields of a resource within km of the site; `sea` null for all, true for those at sea, false for those on land. |
| 510 | 1 | `int count(Resource r, double km)` _(in EarthDepositsCheck.Near)_ |  |
| 512 | 1 | `int land(Resource r, double km)` _(in EarthDepositsCheck.Near)_ |  |
| 514 | 1 | `int sea(Resource r, double km)` _(in EarthDepositsCheck.Near)_ |  |
| 516 | 1 | `int[] counts(Resource r)` _(in EarthDepositsCheck.Near)_ |  |
| 519 | 1 | `static String triple(int[] a)` |  |

### 5. NOVA SCOTIA, AS SPEC 8 FOUND IT (lines 521-615)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 35 | `static void novaScotia(EarthData full, String id, Geology geo, World halifaxWorld, Near halifax)` |  |
| 561 | 14 | `static void controls(EarthData full, String id, Geology geo)` | The controls, each its own world on CONTROL_SEED: Mesabi, a shield spot outside every district, Tom Price and Calgary. |
| 576 | 7 | `static int ironIn50(String id, Spot s)` |  |
| 585 | 24 | `static double[] shieldSpot(EarthData data, Geology geo, double lat, double lon)` | A shield spot outside every district, nearest the one given: the spot itself if it is one, else the nearest on a 0.5 degree lattice out to 4 degrees; {lat, lon, km to the nearest district's rim}. |
| 610 | 5 | `static double greatCircleKm(double lat1, double lon1, double lat2, double lon2)` |  |

### 6. FEWER AND BIGGER (lines 616-687)

| line | len | member | says |
|---:|---:|---|---|
| 621 | 1 | **type** `record PoolRef(Resource r, int px, int py)` | A pool: a resource and its place among that resource's pools. |
| 624 | 9 | `static List<PoolRef> poolsWithFields(World w)` | Every pool of a world that holds at least one field, resource by resource, from the pools' counts alone (nothing is drawn). |
| 635 | 1 | **type** `record Sample(int pools, int counted, int fields, int least, int most, double mean, double se, int outOfRange)` | What a world's pools say of its fields' size. |
| 642 | 23 | `static Sample sample(World w, int scale, int want)` | Draws evenly spread pools of a world (at least `want`, every resource's) and compares each pool's field count with its old count over `scale` (atLeastOne() at its two ends, u = 1 and u = 0, bound what the pool's draw ... |
| 666 | 21 | `static void fewerAndBigger(String id)` |  |

### 7. A CLUSTER STAYS ON ITS PROVINCE'S GROUND (lines 688-774)

| line | len | member | says |
|---:|---:|---|---|
| 693 | 10 | `static boolean holdsNone(World w, Resource r, Deposit d)` | Whether a field lies on ground that holds none of its resource, by the harness's own reading: the province's multiplier there is nought, or the plot is sea and it is not oil in a province whose sea holds oil. |
| 704 | 22 | `static void ground(String where, World w, Near near)` |  |
| 731 | 13 | `static long[] seaPlot(World w, boolean oil)` | The first plot, stepping a km at a time out from the founding site along eight bearings to 400 km, that is sea (and, for oil, in a province whose sea holds oil): {x, y}, or null for none. |
| 746 | 14 | `static void detector(String where, World w)` | The detector sees: iron on a sea plot is on ground that holds none, oil on a plot of a sea that holds oil is not, and the model's fieldGround() says the same of both. |
| 762 | 12 | `static void playtestsRotterdam(EarthData data, String id, String which)` | The world `LongPlaytest --earth rotterdam` founds on a data file: Rotterdam as the place search finds it, the default world's seed. |

### 8. THE SAVE (lines 775-789)

| line | len | member | says |
|---:|---:|---|---|
| 778 | 11 | `static void theSave()` |  |

