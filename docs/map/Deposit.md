# Deposit.java - 263 lines · 13 methods · 7 constants · model

`ham/citybuildersim/Deposit.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> One field of a resource in the world's ground: which resource, the world cell it is listed in and its place in that cell's list, its centre as a plot, its sites and what it holds - where each of its sites lies, (0.7.79) an oil field's grade and (0.7.110) its type, which on Earth (0.7.117) leans on the geology under it.
> 
> WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1). A
> field is the unit the land office sells and the map draws: it belongs
> whole to the piece of ground that holds its centre (spec-land star 12), it
> is mined a site at a time, and it is worked out in the order the city
> bought it. World.fieldsInCell() draws a cell's fields whenever they are
> asked for, so a field is never stored - the cell and the index say which
> one it is, from any save, on any machine.
> 
> FEWER AND BIGGER (0.7.99, batch W1; World's FEWER AND BIGGER DEPOSITS): a
> tenth as many fields, each World.FIELD_SCALE of the old draws' sites, in
> clusters, drawn a pool of cells at a time and listed in the cell their
> centre is in, numbered from World.FIELD_INDEX_FROM. The old world's fields
> (World.legacyFieldsInCell(), numbered from 0, at most
> World.LEGACY_MAX_SITES sites) are what an older save's ground still holds
> (CityLand.fieldsIn()); the two never share a number in a cell.
> 
> ITS SITES LIE ON THE GROUND (0.7.58, batch J1c): each a square of its
> resource's site area (siteWidth()) laid on a grid round the centre,
> nearest first (siteAt()), each holding an equal share of the field's
> amount to the whole unit (siteAmount()) - where the map draws them and the
> mines and wells stand. From 0.7.58 to 0.7.63 a site also belonged to the
> piece of ground holding the site's own centre, so a field was shared, a
> site at a time, among the ground it covers, and a new city bought one site
> of its founding field for about US$5.3M. Since 0.7.64 (batch L; Jerus,
> "whole iron fields as one offer") the whole field goes with its centre
> again, every site and every tonne in one offer: the default world's
> founding field, 35 sites and 449 Mt, for about US$180M (CityLand, THE
> FIELDS IN A PIECE OF GROUND) - to 0.7.98; since 0.7.99 a new city's
> nearest iron field on the default world lies twelve kilometres out.
> 
>               since 0.7.99, from 0 on the old world's
>               5,120 (FIELD_SCALE x World.LEGACY_MAX_SITES) since 0.7.99, and
>               on Earth since 0.8.12 (batch EG1) World.FIELD_SCALE x
>               World.EARTH_FIELD_SCALE to World.MAX_SITES, 15,360; 1 to
>               World.LEGACY_MAX_SITES on the old world's
>               pool sum exactly to its cells' World.cellTotal() since 0.7.99,
>               the old world's of a cell to the cell's
>               (0.7.117, World.geologyAt()), which its type() leans on;
>               null on a generated world, whose types are drawn flat

**Uses:** [World](World.md) (12), [Resource](Resource.md) (4), [Geology](Geology.md) (2)

**Used by (53):** [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingManager](BuildingManager.md), [BuildingVisual](BuildingVisual.md), [BuildingsTemplate](BuildingsTemplate.md), [CementConcreteCheck](CementConcreteCheck.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthSite](EarthSite.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ExtractionSector](ExtractionSector.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [Geology](Geology.md), [GridConversion](GridConversion.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LegacyLand](LegacyLand.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryFlow](RefineryFlow.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Resource](Resource.md), [SaveFileCheck](SaveFileCheck.md), [WellCheck](WellCheck.md), [World](World.md), [WorldCheck](WorldCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | WHERE THE SITES LIE (0.7.58) |
| 174 | THE CRUDE'S GRADE (0.7.79, batch O3; docs/studies/spec-oil.md 2.2) |
| 213 | THE FIELD'S TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 189 | `Deposit.Grade.LIGHT` |  |
| 189 | `Deposit.Grade.MEDIUM` |  |
| 189 | `Deposit.Grade.HEAVY` |  |
| 239 | `Deposit.FieldType.HEMATITE` |  |
| 239 | `Deposit.FieldType.MAGNETITE` |  |
| 239 | `Deposit.FieldType.THERMAL` |  |
| 239 | `Deposit.FieldType.COKING` |  |
| 239 | `Deposit.FieldType.SULPHIDE` |  |
| 239 | `Deposit.FieldType.OXIDE` |  |
| 239 | `Deposit.FieldType.LIMESTONE` |  |
| 239 | `Deposit.FieldType.HARD_ROCK` |  |
| 239 | `Deposit.FieldType.NONE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 95 | `Deposit.SITE_PLACES` | `places()` | The grid points a field's sites stand on, nearest the centre first, in site widths east and south: every point within PLACES_BOX of the centre each way sorted by its squared distance, then from north to south, then we... |
| 98 | `Deposit.PLACES_BOX` | `(int) Math.ceil(Math.sqrt(World.MAX_SITES / Math.PI)) + 1` | The box the site table is sorted in, in site widths each way: sqrt(World.MAX_SITES / pi) rounded up, and one more - 14 for the old world's 512 sites, 42 for 5,120, 71 for an Earth field's 15,360. |
| 101 | `Deposit.PLACES_REACH` | `reaches()` | How far, in site widths each way (L-infinity), the first k + 1 sites reach from the centre. |
| 192 | `Deposit.GRADE_SHARES` | `{ 1.0 / 3, 1.0 / 3, 1.0 / 3 }` | The share of fields of each grade, in Grade's order: a third each (est., spec-oil 2.2 and 6 - to confirm; the research gives a grade a field, not the world's mix). |
| 195 | `Deposit.GRADE_SALT` | `0x6A7DE5L` | What makes a field's grade a draw of its own, apart from its turn()'s. |
| 244 | `Deposit.TYPE_SHARES` | `{.75,.25,.85,.15,.80,.20,.70,.30, 1 }` | The share of its resource's fields of each type, in FieldType's order: the first of each resource's two the study's (docs/studies/spec-materials.md 2.4, the study's 1.4: hematite .75, thermal .85, sulphide .80, limest... |
| 247 | `Deposit.TYPE_SALT` | `0x7E1DA5L` | What makes a field's type a draw of its own, apart from its turn()'s and its grade()'s. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 210 | **type** `public record Deposit(Resource kind, int cell, int index, long x, long y, int sites, double amount, Geology...` | One field of a resource in the world's ground: which resource, the world cell it is listed in and its place in that cell's list, its centre as a plot, its sites and what it holds - where each of its sites lies, (0.7.7... |
| 57 | 3 | `public Deposit(Resource kind, int cell, int index, long x, long y, int sites, double amount)` | A field of a generated world, or any whose type is drawn flat: no geology under it. |
| 62 | 1 | `public double km2()` | The ground it covers, in square kilometres: its sites times its resource's site. |

### WHERE THE SITES LIE (0.7.58) (lines 64-173)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 12 | `private static int[][] places()` |  |
| 116 | 9 | `private static int[] reaches()` |  |
| 127 | 1 | `public static double siteWidth(Resource r)` | A site's width in plots, for a resource: the side of a square of its site area - an Iron Mine's 0.03716 km2 is 6.43 plots (193 m). |
| 130 | 3 | `int turn()` | The quarter-turns its grid is laid at, 0 to 3: from its kind, cell and index, so the same field always lies the same way. |
| 135 | 11 | `public double[] siteAt(int k)` | Where its k-th site's centre lies, from 0 to sites - 1: {plots east, plots south} of its centre plot. |
| 154 | 3 | `public double siteAmount(int k)` | Its k-th site's share of its amount, whole: floor((k + 1) A / S) less floor(k A / S), so each site holds the amount over its sites to the whole unit, the remainders spread one each through the sites, and the shares of... |
| 158 | 5 | `private double cumulative(int k)` |  |
| 165 | 3 | `public double reach()` | How far its sites reach from its centre plot, in plots each way (L-infinity): the outermost site's centre and half a site. |
| 170 | 3 | `public static double mostReach(Resource r)` | ...and the most any field of a resource reaches on any world: World.MAX_SITES of its sites, an Earth field's (a generated world's widest, 5,120 sites, reaches less; the map and the conversion use it only as a pad roun... |

### THE CRUDE'S GRADE (0.7.79, batch O3; docs/studies/spec-oil.md 2.2) (lines 174-212)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 1 | **type** `public enum Grade` | A crude's grade: LIGHT (Brent's cuts), MEDIUM (the research's blend) or HEAVY (Maya's), in that order. |
| 202 | 10 | `public Grade grade()` | Its crude's grade (0.7.79): a uniform draw from World.mix() of its cell, index, kind and GRADE_SALT, against GRADE_SHARES in Grade's order - the same field always the same grade, on any machine. |

### THE FIELD'S TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) (lines 213-263)

| line | len | member | says |
|---:|---:|---|---|
| 239 | 1 | **type** `public enum FieldType` | A field's type: two for each of iron, coal, copper and stone, in Resource's order, the commoner first; NONE for oil, uranium and forest. |
| 256 | 7 | `public FieldType type()` | Its type (0.7.110): NONE for a resource of one type; else a uniform draw from World.mix() of its cell, index, kind and TYPE_SALT - its resource's first type under that type's TYPE_SHARES, the second from it - the same... |

