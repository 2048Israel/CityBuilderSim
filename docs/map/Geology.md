# Geology.java - 751 lines · 33 methods · 21 constants · model

`ham/citybuildersim/Geology.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's name rules and the hand-checked table, its known petroleum, and the weights, the Z that keeps the land's count of fields today's and the type odds an Earth world's fields are drawn with (spec-earth 4).
> 
> WHY THIS EXISTS (0.7.117, batch E3; the project's spec-earth.md 4 and 8,
> runs/spec-materials.md 2.4; Jerus's answers of 2026-10-10, "all rec").
> Until E3 an Earth world drew its fields as a generated one does, at
> today's densities on its cells' land (E2): iron as thick on the Paris
> basin as on the Yilgarn shield, oil as thick in the Alps as under the
> North Sea. The study rasterised the USGS World Energy provinces at 0.25
> degree (earth-1's geology layer), classed each by its name with a rule
> list (tools/earth/geology.py) and weighted each resource's density by the
> class. This holds those classes, the weights and the constants derived
> from them: World draws an Earth world's fields on real geology with them,
> and Deposit types a field by them. A generated world reads none of it
> (w = 1, Z = 1, its types flat) and draws to the bit what it drew.
> 
> THE CLASSES. The data's provinces layer carries each province's class by
> geology.py's rules, the first match winning. The rules leave 219 of the
> 1,023 "other" and misfire on some: every "OCS" planning area is ocean,
> Cook Inlet and the Gulf of Mexico's shelf with it; "Foredeep" matches
> "deep", so Mesopotamia's foreland basin is ocean; "Uplift" makes Ghawar a
> fold belt; "platform" puts Iowa on a shelf. HAND is the hand-checked
> table: each of the 219 placed by its name and where it lies (8 stay
> other: flood-basalt plateaus, oceanic islands and basement highs under a
> shelf sea), and 68 misfires corrected (★E3-1), every row with the class
> the rules gave it and why it moves. A data file whose province under a
> row is not the table's is refused (of()): a re-baked file is checked
> again before it is read.
> 
> THE DENSITY ON EARTH (World.countOf()). A cell's mean count of a
> resource's fields is today's density x its land km2 x w_r(class) x Z_r,
> the class read at each of the cells pass's 16 points
> (EarthTerrain.cellsPass()), so a cell across two provinces takes each
> its share. Oil's is also x (1 + log10(1 + known petroleum /
> OIL_SCALE_MMBOE)), and its area takes in the sea of shelf, basin and
> rift provinces: offshore oil, as the generated world's first place
> allows.
> 
> Z (measure(), the constants Z_IRON to Z_STONE). Z_r = the land / the land
> x w_r over the planet north of ANTARCTIC_LAT, every 4' cell of the far
> layers at its area on the sphere and its class where its centre lies, so
> the land's expected count of each resource's fields is today's (oil's
> weights with its petroleum, its offshore sea on top, as the study's 0.701
> has it). The study's figures were measured on the rules' classes before
> the hand pass, and three of them do not follow from its own table (★E3-2).
> 
> THE TYPES (runs/spec-materials.md 2.4, "On Earth (E3)"). A field's type
> leans on the class at its centre by an odds ratio: coking coal x3 in fold
> belts and x0.5 on shields, limestone x2 in basins and on shelves, x0.5 in
> fold belts and x0.25 on shields, iron and copper flat; one constant a
> resource (COKING_ODDS, LIMESTONE_ODDS) holds the Earth-wide share - over
> the fields expected in each class - at the generated world's TYPE_SHARES.
> 
> EarthTerrainCheck 10 to 14 hold it, on the fixture and on the full file.

**Uses:** [EarthData](EarthData.md) (13), [Resource](Resource.md) (12), [Deposit](Deposit.md) (2), [EarthProj](EarthProj.md) (2)

**Used by (7):** [Deposit](Deposit.md), [EarthSite](EarthSite.md), [EarthTerrain](EarthTerrain.md), [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [LandMap](LandMap.md), [World](World.md)

## Sections

| line | section |
|---:|---|
| 83 | THE WEIGHTS (spec-earth 4's table), in Kind's order - ocean, shield, |
| 108 | Z, measured on earth-1 with the hand table (measure(); EarthTerrainCheck |
| 139 | THE TYPES' ODDS (runs/spec-materials.md 2.4, "On Earth (E3)"): the |
| 158 | THE HAND-CHECKED TABLE (★E3-1). A row: the province's row in the data, |
| 476 | THE WEIGHTS AND ODDS, read |
| 539 | ONE DATA FILE'S GEOLOGY: its provinces' classes and the grid |
| 632 | MEASURING THE PLANET (what Z and the odds are derived from; the check's) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 67 | `Geology.Kind.OCEAN` |  |
| 67 | `Geology.Kind.SHIELD` |  |
| 67 | `Geology.Kind.FOLD_BELT` |  |
| 67 | `Geology.Kind.RIFT` |  |
| 67 | `Geology.Kind.SHELF` |  |
| 67 | `Geology.Kind.BASIN` |  |
| 67 | `Geology.Kind.OTHER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 91 | `Geology.W_IRON` | `{ 1, 4, 1, 0.5, 0, 0.5, 1 }` | Iron's weight by class: four on a shield, nothing on a shelf, half in a rift or basin (spec-earth 4). |
| 94 | `Geology.W_OIL` | `{ 0.5, 0.05, 1, 2, 3, 2, 0.5 }` | Oil's weight by class: three on a shelf, two in a rift or basin, a twentieth on a shield (spec-earth 4). |
| 97 | `Geology.W_COAL` | `{ 0.5, 0.1, 1, 1, 0, 2, 0.5 }` | Coal's weight by class: two in a basin, a tenth on a shield, nothing on a shelf (spec-earth 4). |
| 100 | `Geology.W_COPPER` | `{ 1, 1, 4, 1, 0, 0.3, 1 }` | Copper's weight by class: four in a fold belt, three tenths in a basin, nothing on a shelf (spec-earth 4). |
| 103 | `Geology.W_URANIUM` | `{ 1, 2, 0.5, 1, 0, 1.5, 1 }` | Uranium's weight by class: two on a shield, one and a half in a basin, half in a fold belt (spec-earth 4). |
| 106 | `Geology.W_STONE` | `{ 1, 2, 2, 1, 0, 1, 1 }` | Stone's weight by class: two on a shield or in a fold belt, nothing on a shelf (spec-earth 4). |
| 116 | `Geology.Z_IRON` | `0.7219` | Iron's Z: the land over the land x W_IRON north of ANTARCTIC_LAT, 0.7219 on earth-1 with the hand table. |
| 119 | `Geology.Z_OIL` | `0.6033` | Oil's Z: the land over the land x W_OIL x its petroleum factor north of ANTARCTIC_LAT, 0.6033 on earth-1 with the hand table (the offshore sea on top). |
| 122 | `Geology.Z_COAL` | `0.7884` | Coal's Z: the land over the land x W_COAL north of ANTARCTIC_LAT, 0.7884 on earth-1 with the hand table. |
| 125 | `Geology.Z_COPPER` | `0.7322` | Copper's Z: the land over the land x W_COPPER north of ANTARCTIC_LAT, 0.7322 on earth-1 with the hand table. |
| 128 | `Geology.Z_URANIUM` | `0.7518` | Uranium's Z: the land over the land x W_URANIUM north of ANTARCTIC_LAT, 0.7518 on earth-1 with the hand table. |
| 131 | `Geology.Z_STONE` | `0.6915` | Stone's Z: the land over the land x W_STONE north of ANTARCTIC_LAT, 0.6915 on earth-1 with the hand table. |
| 134 | `Geology.ANTARCTIC_LAT` | `- 60` | The latitude south of which Z's sums leave the ground out: 60 S, the study's "Antarctica excluded" (★E3-4: its ice holds what no city founded on the ice can reach, and E4 refuses ice). |
| 137 | `Geology.OIL_SCALE_MMBOE` | `1000` | Oil's petroleum factor's scale, in million barrels of oil equivalent: 1,000 - oil is x (1 + log10(1 + known / 1,000)) (spec-earth 4): x2.13 under the Anglo-Dutch Basin's 12,640. |
| 147 | `Geology.COKING_RATIO` | `{ 1, 0.5, 3, 1, 1, 1, 1 }` | Coking coal's odds ratio by class: three in a fold belt, a half on a shield, one elsewhere (spec-materials 2.4). |
| 150 | `Geology.LIMESTONE_RATIO` | `{ 1, 0.25, 0.5, 1, 2, 2, 1 }` | Limestone's odds ratio by class: two in a basin or on a shelf, a half in a fold belt, a quarter on a shield (spec-materials 2.4). |
| 153 | `Geology.COKING_ODDS` | `0.135295` | Coking coal's odds where the ratio is one: 0.135295 on earth-1 with the hand table (six figures), so its Earth-wide share is THERMAL's complement, 15%. |
| 156 | `Geology.LIMESTONE_ODDS` | `4.10888` | Limestone's odds where the ratio is one: 4.10888 on earth-1 with the hand table (six figures), so its Earth-wide share is TYPE_SHARES' 70%: .892 in a basin, .507 on a shield. |
| 167 | `Geology.HAND` | `""" 9 \| 5080 \| Chukchi Shelf OCS \| ocean \| shelf \| a shelf: "OCS" matched the...` | The hand-checked classes, a row a province: row \| code \| name \| the rules' class \| the class here \| why. |
| 461 | `Geology.TABLE` | `parse()` | The hand-checked table, parsed, in its rows' order. |
| 544 | `Geology.OF` | `new IdentityHashMap<>()` | Each reader's geology, made once (of()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `private final String label` |  |
| 546 | `private final EarthData data` |  |
| 547 | `private final byte[] grid` |  |
| 548 | `private final int cpd` |  |
| 550 | `private final Kind[] kinds, rules` | Each province's class (row 0, no province, other), and its class by the rules alone. |
| 552 | `private final double[] petroleum` | Each province's petroleum factor. |
| 554 | `private final double[][] multipliers` | Each resource's multiplier for each province, w x Z (x the petroleum factor for oil), by Resource's ordinal. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 689 | **type** `public final class Geology` | The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's name rules and the hand-checked table, its known petroleum, and the weigh... |
| 66 | 16 | **type** `public enum Kind` | A province's class, in the order of the data's names: what its rocks hold. |
| 71 | 1 | `Kind(String label)` _(in Geology.Kind)_ |  |
| 74 | 1 | `public String label()` _(in Geology.Kind)_ | Its name as the data and the table write it. |
| 77 | 4 | `public static Kind of(String label)` _(in Geology.Kind)_ | The kind a name names; null for none. |

### THE WEIGHTS (spec-earth 4's table), in Kind's order - ocean, shield, (lines 83-107)

### Z, measured on earth-1 with the hand table (measure(); EarthTerrainCheck (lines 108-138)

### THE TYPES' ODDS (runs/spec-materials.md 2.4, "On Earth (E3)"): the (lines 139-157)

### THE HAND-CHECKED TABLE (★E3-1). A row: the province's row in the data, (lines 158-475)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 1 | **type** `public record Hand(int row, String code, String name, Kind rules, Kind kind, String why)` | A row of the hand-checked table. |
| 463 | 12 | `private static List<Hand> parse()` |  |

### THE WEIGHTS AND ODDS, read (lines 476-538)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 11 | `public static double weight(Resource r, Kind k)` | A resource's weight in a class: its row of the table; one for forest, which is not in fields. |
| 494 | 11 | `public static double z(Resource r)` | A resource's Z; one for forest. |
| 507 | 3 | `public static double petroleumFactor(double knownMmboe)` | Oil's petroleum factor for a province's known petroleum, MMBOE: 1 + log10(1 + known / OIL_SCALE_MMBOE). |
| 512 | 3 | `public static boolean seaHoldsOil(Kind k)` | Whether a class's sea holds oil: a shelf's, a basin's and a rift's (spec-earth 4). |
| 517 | 4 | `static double lean(double odds, double ratio)` | The leaning type's share at odds c and a ratio: c x ratio / (1 + c x ratio). |
| 529 | 9 | `public static double firstShare(Resource r, Kind k)` | The share of a resource's fields of its first type in a class, on Earth: coal's thermal one less coking's lean (COKING_ODDS), stone's limestone its lean (LIMESTONE_ODDS), iron's and copper's Deposit.TYPE_SHARES as on ... |

### ONE DATA FILE'S GEOLOGY: its provinces' classes and the grid (lines 539-631)

| line | len | member | says |
|---:|---:|---|---|
| 557 | 5 | `public static Geology of(EarthData d)` | The geology of a data file, made once a reader: its classes the rules' with the hand table over them. |
| 563 | 34 | `private Geology(EarthData d)` |  |
| 599 | 1 | `public EarthData data()` | The data it was read from. |
| 602 | 1 | `public int rows()` | The rows a province can have: one past the highest. |
| 605 | 9 | `public int provinceAt(double lat, double lon)` | The province at a point, its row in the data's table; 0 for none (EarthData.provinceAt()'s rule, on the grid in hand). |
| 616 | 1 | `public Kind kindOf(int row)` | A province's class, the hand table's over the rules'; OTHER for row 0 (no province) or a row past the table. |
| 619 | 1 | `public Kind rulesKindOf(int row)` | A province's class by geology.py's rules alone, as the data carries it. |
| 622 | 1 | `public double petroleumOf(int row)` | A province's petroleum factor (petroleumFactor() of its known petroleum); 1 for none. |
| 625 | 3 | `public double multiplier(Resource r, int row)` | What a province does to a resource's density: w x Z, and for oil x its petroleum factor - the spot's multiplier the founding card is to name (E4). |
| 630 | 1 | `public boolean seaHoldsOil(int row)` | Whether a province's sea holds oil (seaHoldsOil() of its class). |

### MEASURING THE PLANET (what Z and the odds are derived from; the check's) (lines 632-751)

| line | len | member | says |
|---:|---:|---|---|
| 643 | 73 | **type** `public record Measure(double[] land, double[] sea, double[] landAll)` | Every 4' cell of the far layers, its area on the sphere: the land (dry and lake) and the sea in each province, km2, north of ANTARCTIC_LAT and in all; by row. |
| 646 | 5 | `public double totalLand()` _(in Geology.Measure)_ | The land north of ANTARCTIC_LAT, km2. |
| 653 | 5 | `public double[] landByKind(Kind[] kinds)` _(in Geology.Measure)_ | The land in each class north of ANTARCTIC_LAT, km2, by Kind's order, each province by the class `kinds` gives its row. |
| 660 | 5 | `public double[] landAllByKind(Kind[] kinds)` _(in Geology.Measure)_ | ...and the land in all, Antarctica in. |
| 667 | 8 | `public double zOf(Resource r, Kind[] kinds, double[] petroleum)` _(in Geology.Measure)_ | A resource's Z with the classes `kinds` gives each row and the petroleum factors `petroleum` (oil's): the land over the land x w (x the factor), north of ANTARCTIC_LAT. |
| 677 | 10 | `public double offshoreOil(Kind[] kinds, double[] petroleum)` _(in Geology.Measure)_ | Oil's offshore area against its land's, both x w x the petroleum factor: what the sea of shelves, basins and rifts adds to the land's count. |
| 689 | 7 | `public double[] fieldShares(Resource r, Kind[] kinds)` _(in Geology.Measure)_ | The fields expected of a resource in each class, as shares: its land x w there over the sum, north of ANTARCTIC_LAT - what an Earth-wide type share is weighted by. |
| 698 | 5 | `public static double shareAt(double[] fieldShares, double[] ratio, double odds)` _(in Geology.Measure)_ | The Earth-wide share of the leaning type at odds c: each class's lean weighted by fieldShares(). |
| 705 | 8 | `public static double oddsFor(double[] fieldShares, double[] ratio, double target)` _(in Geology.Measure)_ | The odds whose Earth-wide share is `target`, by bisection on its logarithm to the last bit. |
| 714 | 1 | `private static Kind kindAt(Kind[] kinds, int row)` _(in Geology.Measure)_ |  |
| 718 | 1 | `public Kind[] kinds()` | Its classes by row, the hand table's over the rules' (a copy). |
| 721 | 1 | `public Kind[] rulesKinds()` | Its classes by row by the rules alone (a copy). |
| 724 | 1 | `public double[] petroleum()` | Its petroleum factors by row (a copy). |
| 727 | 24 | `public Measure measure()` | Measures the planet on its data (Measure): every 4' cell of far.land and far.lake, its centre's province, its area on the sphere at EarthProj.RADIUS_M. |

