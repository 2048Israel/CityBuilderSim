# Geology.java - 1,298 lines · 49 methods · 35 constants · model

`ham/citybuildersim/Geology.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's name rules and the hand-checked table, its known petroleum, and the weights, the Z that keeps the land's count of fields today's and the type odds an Earth world's fields are drawn with (spec-earth 4).
> 
> WHY THIS EXISTS (0.7.117, batch E3; the project's spec-earth.md 4 and 8,
> docs/studies/spec-materials.md 2.4; Jerus's answers of 2026-10-10, "all
> rec"). Until E3 an Earth world drew its fields as a generated one does, at
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
> its share. Oil's area takes in the sea of shelf, basin and rift
> provinces: offshore oil, as the generated world's first place allows. (To
> 0.8.11 oil's was also x (1 + log10(1 + known petroleum /
> OIL_SCALE_MMBOE)); since EG1 oil's and iron's weights are the province's
> own, below, and iron's is read at the point, for a district.)
> 
> Z (measure(), the constants Z_IRON to Z_STONE). Z_r = the land / the land
> x w_r over the planet north of ANTARCTIC_LAT, every 4' cell of the far
> layers at its area on the sphere and its class where its centre lies (and,
> for iron, its district), so the land's expected count of each resource's
> fields is today's (oil's weights with its offshore sea on top, as the
> study's 0.701 has it). The study's figures were measured on the rules' classes before
> the hand pass, and three of them do not follow from its own table (★E3-2).
> 
> THE TYPES (docs/studies/spec-materials.md 2.4, "On Earth (E3)"). A field's
> type leans on the class at its centre by an odds ratio: coking coal x3 in
> fold belts and x0.5 on shields, limestone x2 in basins and on shelves, x0.5
> in fold belts and x0.25 on shields, iron and copper flat; one constant a
> resource (COKING_ODDS, LIMESTONE_ODDS) holds the Earth-wide share - over
> the fields expected in each class - at the generated world's TYPE_SHARES.
> 
> EG1, OIL AND IRON WHERE THE GEOLOGY SAYS (0.8.12, batch EG1;
> docs/studies/spec-earth2-terrain.md 8 and 8.5; Jerus, 2026-10-12, "all
> rec"). E3's weights only rescaled the generated world's densities within a
> narrow range: a fold belt's oil weight was 1, so the Appalachians counted
> like the Zagros; iron was nought only on a shelf; and known petroleum could
> raise a province's oil, never lower it - a province with none took its
> class's full weight. Jerus's Nova Scotia cities found oil and iron all
> ... (16 more lines in the source)

**Uses:** [Resource](Resource.md) (19), [EarthData](EarthData.md) (17), [EarthProj](EarthProj.md) (5), [Deposit](Deposit.md) (2)

**Used by (9):** [CoalMiningCheck](CoalMiningCheck.md), [Deposit](Deposit.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthSite](EarthSite.md), [EarthTerrain](EarthTerrain.md), [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [LandMap](LandMap.md), [World](World.md)

## Sections

| line | section |
|---:|---|
| 106 | THE WEIGHTS (spec-earth 4's table), in Kind's order - ocean, shield, |
| 131 | Z, measured on earth-1 with the hand tables (measure(), zOf(r, g); |
| 166 | OIL, IRON AND THE DISTRICTS WHERE THE GEOLOGY SAYS (0.8.12, batch EG1; |
| 186 | THE TYPES' ODDS (docs/studies/spec-materials.md 2.4, "On Earth (E3)"): |
| 205 | THE HAND-CHECKED TABLE (★E3-1). A row: the province's row in the data, |
| 523 | THE KNOWN HAND TABLE (0.8.12, batch EG1; spec-earth2-terrain 8.2, 8.5) |
| 597 | THE RECORDED IRON DISTRICTS (0.8.12, batch EG1; spec-earth2-terrain 8.5) |
| 727 | THE RASTER PATCHES (0.8.12, batch EG1; spec-earth2-terrain 8.2) |
| 761 | THE WEIGHTS AND ODDS, read |
| 824 | THE DISTRICTS, LOOKED UP (0.8.12, batch EG1) |
| 894 | THE PATCHES, LOOKED UP (0.8.12, batch EG1) |
| 924 | ONE DATA FILE'S GEOLOGY: its provinces' classes and the grid |
| 1087 | · EG1: the weights of a row |
| 1141 | MEASURING THE PLANET (what Z and the odds are derived from; the check's) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 90 | `Geology.Kind.OCEAN` |  |
| 90 | `Geology.Kind.SHIELD` |  |
| 90 | `Geology.Kind.FOLD_BELT` |  |
| 90 | `Geology.Kind.RIFT` |  |
| 90 | `Geology.Kind.SHELF` |  |
| 90 | `Geology.Kind.BASIN` |  |
| 90 | `Geology.Kind.OTHER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 114 | `Geology.W_IRON` | `{ 1, 4, 1, 0.5, 0, 0.5, 1 }` | Iron's weight by class: four on a shield, nothing on a shelf, half in a rift or basin (spec-earth 4). |
| 117 | `Geology.W_OIL` | `{ 0.5, 0.05, 1, 2, 3, 2, 0.5 }` | Oil's weight by class: three on a shelf, two in a rift or basin, a twentieth on a shield (spec-earth 4). |
| 120 | `Geology.W_COAL` | `{ 0.5, 0.1, 1, 1, 0, 2, 0.5 }` | Coal's weight by class: two in a basin, a tenth on a shield, nothing on a shelf (spec-earth 4). |
| 123 | `Geology.W_COPPER` | `{ 1, 1, 4, 1, 0, 0.3, 1 }` | Copper's weight by class: four in a fold belt, three tenths in a basin, nothing on a shelf (spec-earth 4). |
| 126 | `Geology.W_URANIUM` | `{ 1, 2, 0.5, 1, 0, 1.5, 1 }` | Uranium's weight by class: two on a shield, one and a half in a basin, half in a fold belt (spec-earth 4). |
| 129 | `Geology.W_STONE` | `{ 1, 2, 2, 1, 0, 1, 1 }` | Stone's weight by class: two on a shield or in a fold belt, nothing on a shelf (spec-earth 4). |
| 143 | `Geology.Z_IRON` | `0.9706` | Iron's Z: the land over the land x iron's weight (a shield's four, an allowance elsewhere, a district's twenty) north of ANTARCTIC_LAT, 0.9706 on earth-1 with the hand tables, EG1 (E3's 0.7219). |
| 146 | `Geology.Z_OIL` | `0.3616` | Oil's Z: the land over the land x oil's weight (the allowance and the known petroleum a km2) north of ANTARCTIC_LAT, 0.3616 on earth-1 with the hand tables, EG1 (E3's 0.6033, with its petroleum factor); the offshore s... |
| 149 | `Geology.Z_COAL` | `0.7884` | Coal's Z: the land over the land x W_COAL north of ANTARCTIC_LAT, 0.7884 on earth-1 with the hand tables, EG1 (the Sydney patch moves a little land). |
| 152 | `Geology.Z_COPPER` | `0.7323` | Copper's Z: the land over the land x W_COPPER north of ANTARCTIC_LAT, 0.7323 on earth-1 with the hand tables, EG1 (E3's 0.7322). |
| 155 | `Geology.Z_URANIUM` | `0.7518` | Uranium's Z: the land over the land x W_URANIUM north of ANTARCTIC_LAT, 0.7518 on earth-1 with the hand tables, EG1. |
| 158 | `Geology.Z_STONE` | `0.6915` | Stone's Z: the land over the land x W_STONE north of ANTARCTIC_LAT, 0.6915 on earth-1 with the hand tables, EG1. |
| 161 | `Geology.ANTARCTIC_LAT` | `- 60` | The latitude south of which Z's sums leave the ground out: 60 S, the study's "Antarctica excluded" (★E3-4: its ice holds what no city founded on the ice can reach, and E4 refuses ice). |
| 164 | `Geology.OIL_SCALE_MMBOE` | `1000` | E3's petroleum factor's scale, in million barrels of oil equivalent: 1,000 - oil was x (1 + log10(1 + known / 1,000)) (spec-earth 4): x2.13 under the Anglo-Dutch Basin's 12,640. |
| 178 | `Geology.OIL_ALLOWANCE` | `0.1` | A province's oil without known petroleum: a tenth of its class's weight (EG1; spec-earth2-terrain 8.5, est.). |
| 181 | `Geology.IRON_ALLOWANCE` | `0.1` | Iron off a shield and outside a recorded district: a tenth of its class's weight (EG1, est.). |
| 184 | `Geology.IRON_DISTRICT_WEIGHT` | `20` | Iron's weight in a recorded iron district: twenty, five times a shield's (EG1, est.). |
| 194 | `Geology.COKING_RATIO` | `{ 1, 0.5, 3, 1, 1, 1, 1 }` | Coking coal's odds ratio by class: three in a fold belt, a half on a shield, one elsewhere (spec-materials 2.4). |
| 197 | `Geology.LIMESTONE_RATIO` | `{ 1, 0.25, 0.5, 1, 2, 2, 1 }` | Limestone's odds ratio by class: two in a basin or on a shelf, a half in a fold belt, a quarter on a shield (spec-materials 2.4). |
| 200 | `Geology.COKING_ODDS` | `0.135296` | Coking coal's odds where the ratio is one: 0.135296 on earth-1 with the hand tables, EG1 (six figures), so its Earth-wide share is THERMAL's complement, 15%. |
| 203 | `Geology.LIMESTONE_ODDS` | `4.10885` | Limestone's odds where the ratio is one: 4.10885 on earth-1 with the hand tables, EG1 (six figures), so its Earth-wide share is TYPE_SHARES' 70%: .892 in a basin, .507 on a shield. |
| 214 | `Geology.HAND` | `""" 9 \| 5080 \| Chukchi Shelf OCS \| ocean \| shelf \| a shelf: "OCS" matched the...` | The hand-checked classes, a row a province: row \| code \| name \| the rules' class \| the class here \| why. |
| 508 | `Geology.TABLE` | `parse()` | The hand-checked table, parsed, in its rows' order. |
| 537 | `Geology.KNOWN_HAND` | `""" 5001 \| Northern Alaska \| 24000 \| Prudhoe Bay, Kuparuk and the North Slope...` | The known petroleum the file holds none of, a province a row: code \| name \| known MMBOE \| why. |
| 584 | `Geology.KNOWN` | `parseKnown()` | The known hand table, parsed, in its rows' order. |
| 609 | `Geology.DISTRICT_HAND` | `""" Hamersley Province(Pilbara) \| -22.7 \| 118.6 \| 180 \| Tom Price, Paraburdoo...` | The recorded iron districts, a row each: name \| latitude \| longitude \| radius km \| what. |
| 712 | `Geology.DISTRICTS` | `parseDistricts()` | The recorded iron districts, parsed, in their rows' order (districtAt() returns an index into it). |
| 739 | `Geology.PATCH_HAND` | `""" Sydney coalfield \| 46.05 \| 46.35 \| -60.45 \| -59.75 \| 5220 \| the onshore S...` | The patches over the province grid, a row each: name \| south \| north \| west \| east \| code \| why. |
| 747 | `Geology.PATCHES` | `parsePatches()` | The patches, parsed, in their rows' order. |
| 839 | `Geology.DISTRICT_BIN_MARGIN` | `0.01` | The margin a district's bins are widened by, in degrees: a hundredth, so a point on a disc's edge is never missed for rounding. |
| 842 | `Geology.DIST_COS` |  | Each district's centre as a unit vector, and the cosine of its angular radius. |
| 845 | `Geology.DISTRICT_BINS` |  | The districts each 1-degree bin holds (row 90 + latitude's floor, column 180 + longitude's floor, row x 360 + column), null for a bin that holds none. |
| 899 | `Geology.PATCH_BOX` | `patchBoxes()` | Each patch's box as {south, north, west, east}, in PATCHES' order. |
| 902 | `Geology.PATCH_ALL` | `patchAll()` | ...and the box that holds them all, {south, north, west, east}: the cheap test provinceAt() makes first. |
| 929 | `Geology.OF` | `new IdentityHashMap<>()` | Each reader's geology, made once (of()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 92 | `private final String label` |  |
| 931 | `private final EarthData data` |  |
| 932 | `private final byte[] grid` |  |
| 933 | `private final int cpd` |  |
| 935 | `private final Kind[] kinds, rules` | Each province's class (row 0, no province, other), and its class by the rules alone. |
| 937 | `private final double[] petroleum` | Each province's E3 petroleum factor, of the file's own known petroleum: the study's figure, kept for EarthTerrainCheck's replay - the model's oil no longer reads it (EG1). |
| 939 | `private final double[][] weights` | Each resource's weight for each province, before Z, by Resource's ordinal: the class's, except oil's and iron's by EG1's rules (weightOf()). |
| 941 | `private final double[][] multipliers` | Each resource's multiplier for each province, weight x Z, by Resource's ordinal. |
| 943 | `private final String[] codes` | The USGS codes of the data's provinces, in the order of their first rows; each row's code's index in them (-1 for row 0); each code's area on the sphere in km2 (the geology grid's cells, land and sea, all latitudes) a... |
| 944 | `private final int[] codeOf` |  |
| 945 | `private final double[] codeArea, codeKnown` |  |
| 947 | `private final double knownMean` | The Earth's known petroleum a km2 of its provinces: every code's known added over every code's area added, north of ANTARCTIC_LAT (the cells whose centre is). |
| 949 | `private final int[] patchRow` | Each patch's province: the lowest row with its code. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 1213 | **type** `public final class Geology` | The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's name rules and the hand-checked table, its known petroleum, and the weigh... |
| 89 | 16 | **type** `public enum Kind` | A province's class, in the order of the data's names: what its rocks hold. |
| 94 | 1 | `Kind(String label)` _(in Geology.Kind)_ |  |
| 97 | 1 | `public String label()` _(in Geology.Kind)_ | Its name as the data and the table write it. |
| 100 | 4 | `public static Kind of(String label)` _(in Geology.Kind)_ | The kind a name names; null for none. |

### THE WEIGHTS (spec-earth 4's table), in Kind's order - ocean, shield, (lines 106-130)

### Z, measured on earth-1 with the hand tables (measure(), zOf(r, g); (lines 131-165)

### OIL, IRON AND THE DISTRICTS WHERE THE GEOLOGY SAYS (0.8.12, batch EG1; (lines 166-185)

### THE TYPES' ODDS (docs/studies/spec-materials.md 2.4, "On Earth (E3)"): (lines 186-204)

### THE HAND-CHECKED TABLE (★E3-1). A row: the province's row in the data, (lines 205-522)

| line | len | member | says |
|---:|---:|---|---|
| 505 | 1 | **type** `public record Hand(int row, String code, String name, Kind rules, Kind kind, String why)` | A row of the hand-checked table. |
| 510 | 12 | `private static List<Hand> parse()` |  |

### THE KNOWN HAND TABLE (0.8.12, batch EG1; spec-earth2-terrain 8.2, 8.5) (lines 523-596)

| line | len | member | says |
|---:|---:|---|---|
| 581 | 1 | **type** `public record Known(String code, String name, double mmboe, String why)` | A row of the known hand table. |
| 586 | 10 | `private static List<Known> parseKnown()` |  |

### THE RECORDED IRON DISTRICTS (0.8.12, batch EG1; spec-earth2-terrain 8.5) (lines 597-726)

| line | len | member | says |
|---:|---:|---|---|
| 709 | 1 | **type** `public record District(String name, double lat, double lon, double radiusKm, String what)` | A recorded iron district: its name, its centre, its radius in km and what it holds. |
| 714 | 12 | `private static List<District> parseDistricts()` |  |

### THE RASTER PATCHES (0.8.12, batch EG1; spec-earth2-terrain 8.2) (lines 727-760)

| line | len | member | says |
|---:|---:|---|---|
| 744 | 1 | **type** `public record Patch(String name, double lat0, double lat1, double lon0, double lon1, String code, String why)` | A patch: the box in which the province is the code's. |
| 749 | 11 | `private static List<Patch> parsePatches()` |  |

### THE WEIGHTS AND ODDS, read (lines 761-823)

| line | len | member | says |
|---:|---:|---|---|
| 766 | 11 | `public static double weight(Resource r, Kind k)` | A resource's weight in a class: its row of the table; one for forest, which is not in fields. |
| 779 | 11 | `public static double z(Resource r)` | A resource's Z; one for forest. |
| 792 | 3 | `public static double petroleumFactor(double knownMmboe)` | E3's oil petroleum factor for a province's known petroleum, MMBOE: 1 + log10(1 + known / OIL_SCALE_MMBOE). |
| 797 | 3 | `public static boolean seaHoldsOil(Kind k)` | Whether a class's sea holds oil: a shelf's, a basin's and a rift's (spec-earth 4). |
| 802 | 4 | `static double lean(double odds, double ratio)` | The leaning type's share at odds c and a ratio: c x ratio / (1 + c x ratio). |
| 814 | 9 | `public static double firstShare(Resource r, Kind k)` | The share of a resource's fields of its first type in a class, on Earth: coal's thermal one less coking's lean (COKING_ODDS), stone's limestone its lean (LIMESTONE_ODDS), iron's and copper's Deposit.TYPE_SHARES as on ... |

### THE DISTRICTS, LOOKED UP (0.8.12, batch EG1) (lines 824-893)

| line | len | member | says |
|---:|---:|---|---|
| 847 | 33 | `static { ... }` |  |
| 882 | 11 | `public int districtAt(double lat, double lon)` | The first district the point is in, its index in DISTRICTS, or -1 for none (EG1): a bin read, and a test of the few districts the bin holds. |

### THE PATCHES, LOOKED UP (0.8.12, batch EG1) (lines 894-923)

| line | len | member | says |
|---:|---:|---|---|
| 904 | 8 | `private static double[][] patchBoxes()` |  |
| 913 | 10 | `private static double[] patchAll()` |  |

### ONE DATA FILE'S GEOLOGY: its provinces' classes and the grid (lines 924-1086)

| line | len | member | says |
|---:|---:|---|---|
| 952 | 5 | `public static Geology of(EarthData d)` | The geology of a data file, made once a reader: its classes the rules' with the hand table over them. |
| 958 | 91 | `private Geology(EarthData d)` |  |
| 1051 | 1 | `public EarthData data()` | The data it was read from. |
| 1054 | 1 | `public int rows()` | The rows a province can have: one past the highest. |
| 1062 | 15 | `public int provinceAt(double lat, double lon)` | The province at a point, its row in the data's table; 0 for none: EarthData.provinceAt()'s rule on the grid in hand, except inside a patch's box (PATCHES), where it is the patch's province (EG1). |
| 1079 | 1 | `public Kind kindOf(int row)` | A province's class, the hand table's over the rules'; OTHER for row 0 (no province) or a row past the table. |
| 1082 | 1 | `public Kind rulesKindOf(int row)` | A province's class by geology.py's rules alone, as the data carries it. |
| 1085 | 1 | `public double petroleumOf(int row)` | A province's E3 petroleum factor (petroleumFactor() of the file's known petroleum); 1 for none. |

### EG1: the weights of a row (lines 1087-1140)

| line | len | member | says |
|---:|---:|---|---|
| 1090 | 3 | `static double ironWeight(Kind k)` | Iron's weight in a class (EG1): a shield's W_IRON, four, and IRON_ALLOWANCE of the class's elsewhere - so a fold belt's tenth, a basin's twentieth, a shelf's none. |
| 1095 | 5 | `private double oilWeight(int row)` | A province's oil weight (EG1): OIL_ALLOWANCE x its class's W_OIL plus its known petroleum a km2 over the Earth's (KNOWN_MEAN), the density of its whole USGS code, land and sea. |
| 1102 | 8 | `public double weightOf(Resource r, int row)` | A resource's weight for a province before Z (EG1): oil's by its known petroleum, iron's a shield's four or an allowance, the other fields' their class's (weight()); one for forest. |
| 1112 | 3 | `public double multiplier(Resource r, int row)` | What a province does to a resource's density: its weight x Z (weightOf() x Z) - the multiplier the founding card names. |
| 1117 | 5 | `public double multiplierAt(Resource r, int row, double lat, double lon)` | What the ground at a point does to a resource's density (EG1): its province's multiplier, and for iron inside a recorded district (districtAt()) IRON_DISTRICT_WEIGHT x Z_IRON where that is the greater. |
| 1124 | 4 | `public double area(String code)` | A USGS code's area on the sphere, km2: the geology grid's cells under provinceAt() (the patches in), land and sea, all latitudes, every row of the code added; 0 for a code the data lacks (EG1). |
| 1130 | 4 | `public double known(String code)` | A USGS code's known petroleum, MMBOE: the file's figure, or the KNOWN table's where the file holds none (the rows of a code repeat the file's figure, so it is taken once); 0 for a code the data lacks (EG1). |
| 1136 | 1 | `public double knownMean()` | The Earth's known petroleum a km2 of its provinces, MMBOE: every code's known over every code's area, north of ANTARCTIC_LAT (EG1). |
| 1139 | 1 | `public boolean seaHoldsOil(int row)` | Whether a province's sea holds oil (seaHoldsOil() of its class). |

### MEASURING THE PLANET (what Z and the odds are derived from; the check's) (lines 1141-1298)

| line | len | member | says |
|---:|---:|---|---|
| 1153 | 109 | **type** `public record Measure(double[] land, double[] sea, double[] landAll, double[] landIn)` | Every 4' cell of the far layers, its area on the sphere: the land (dry and lake) and the sea in each province, km2, north of ANTARCTIC_LAT and in all; by row, and (EG1) the land north of ANTARCTIC_LAT inside a recorde... |
| 1156 | 5 | `public double totalLand()` _(in Geology.Measure)_ | The land north of ANTARCTIC_LAT, km2. |
| 1163 | 5 | `public double[] landByKind(Kind[] kinds)` _(in Geology.Measure)_ | The land in each class north of ANTARCTIC_LAT, km2, by Kind's order, each province by the class `kinds` gives its row. |
| 1170 | 5 | `public double[] landAllByKind(Kind[] kinds)` _(in Geology.Measure)_ | ...and the land in all, Antarctica in. |
| 1177 | 5 | `public double districtLand()` _(in Geology.Measure)_ | The land north of ANTARCTIC_LAT inside a recorded iron district (EG1), km2. |
| 1190 | 10 | `public double zOf(Resource r, Geology g)` _(in Geology.Measure)_ | A resource's Z with the weights Geology g gives (EG1): the land over the land x its weight, each 4' cell by its centre's province (patches in) and, for iron, its district: IRON_DISTRICT_WEIGHT where that outweighs the... |
| 1202 | 9 | `public double offshoreOil(Geology g)` _(in Geology.Measure)_ | Oil's offshore area against its land's, both x its weight in Geology g (EG1): what the sea of shelves, basins and rifts adds to the land's count. |
| 1213 | 8 | `public double zOf(Resource r, Kind[] kinds, double[] petroleum)` _(in Geology.Measure)_ | E3's Z with the classes `kinds` gives each row and the petroleum factors `petroleum` (oil's): the land over the land x w (x the factor), north of ANTARCTIC_LAT. |
| 1223 | 10 | `public double offshoreOil(Kind[] kinds, double[] petroleum)` _(in Geology.Measure)_ | E3's oil offshore area against its land's, both x w x the petroleum factor: what the sea of shelves, basins and rifts adds to the land's count. |
| 1235 | 7 | `public double[] fieldShares(Resource r, Kind[] kinds)` _(in Geology.Measure)_ | The fields expected of a resource in each class, as shares: its land x w there over the sum, north of ANTARCTIC_LAT - what an Earth-wide type share is weighted by. |
| 1244 | 5 | `public static double shareAt(double[] fieldShares, double[] ratio, double odds)` _(in Geology.Measure)_ | The Earth-wide share of the leaning type at odds c: each class's lean weighted by fieldShares(). |
| 1251 | 8 | `public static double oddsFor(double[] fieldShares, double[] ratio, double target)` _(in Geology.Measure)_ | The odds whose Earth-wide share is `target`, by bisection on its logarithm to the last bit. |
| 1260 | 1 | `private static Kind kindAt(Kind[] kinds, int row)` _(in Geology.Measure)_ |  |
| 1264 | 1 | `public Kind[] kinds()` | Its classes by row, the hand table's over the rules' (a copy). |
| 1267 | 1 | `public Kind[] rulesKinds()` | Its classes by row by the rules alone (a copy). |
| 1270 | 1 | `public double[] petroleum()` | Its petroleum factors by row (a copy). |
| 1273 | 25 | `public Measure measure()` | Measures the planet on its data (Measure): every 4' cell of far.land and far.lake, its centre's province (provinceAt(), the patches in) and, on land, its district, its area on the sphere at EarthProj.RADIUS_M. |

