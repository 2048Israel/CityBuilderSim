# Deposit.java - 193 lines · 11 methods · 5 constants · model

`ham/citybuildersim/Deposit.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> One field of a resource in the world's ground: which resource, the world cell it is listed in and its place in that cell's list, its centre as a plot, its sites and what it holds - where each of its sites lies, and (0.7.79) an oil field's grade.
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
>               World.MAX_SITES since 0.7.99, 1 to World.LEGACY_MAX_SITES on
>               the old world's
>               pool sum exactly to its cells' World.cellTotal() since 0.7.99,
>               the old world's of a cell to the cell's

**Uses:** [World](World.md) (10), [Resource](Resource.md) (3)

**Used by (22):** [CityLand](CityLand.md), [CityMap](CityMap.md), [ConversionCheck](ConversionCheck.md), [Game](Game.md), [GridConversion](GridConversion.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LegacyLand](LegacyLand.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryFlow](RefineryFlow.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [WellCheck](WellCheck.md), [World](World.md), [WorldCheck](WorldCheck.md)

## Sections

| line | section |
|---:|---|
| 54 | WHERE THE SITES LIE (0.7.58) |
| 155 | THE CRUDE'S GRADE (0.7.79, batch O3; runs/spec-oil.md 2.2) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 170 | `Deposit.Grade.LIGHT` |  |
| 170 | `Deposit.Grade.MEDIUM` |  |
| 170 | `Deposit.Grade.HEAVY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `Deposit.SITE_PLACES` | `places()` | The grid points a field's sites stand on, nearest the centre first, in site widths east and south: every point within PLACES_BOX of the centre each way sorted by its squared distance, then from north to south, then we... |
| 79 | `Deposit.PLACES_BOX` | `(int) Math.ceil(Math.sqrt(World.MAX_SITES / Math.PI)) + 1` | The box the site table is sorted in, in site widths each way: sqrt(World.MAX_SITES / pi) rounded up, and one more - 14 for the old world's 512 sites, 42 for 5,120. |
| 82 | `Deposit.PLACES_REACH` | `reaches()` | How far, in site widths each way (L-infinity), the first k + 1 sites reach from the centre. |
| 173 | `Deposit.GRADE_SHARES` | `{ 1.0 / 3, 1.0 / 3, 1.0 / 3 }` | The share of fields of each grade, in Grade's order: a third each (est., spec-oil 2.2 and 6 - to confirm; the research gives a grade a field, not the world's mix). |
| 176 | `Deposit.GRADE_SALT` | `0x6A7DE5L` | What makes a field's grade a draw of its own, apart from its turn()'s. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 145 | **type** `public record Deposit(Resource kind, int cell, int index, long x, long y, int sites, double amount)` | One field of a resource in the world's ground: which resource, the world cell it is listed in and its place in that cell's list, its centre as a plot, its sites and what it holds - where each of its sites lies, and (0... |
| 52 | 1 | `public double km2()` | The ground it covers, in square kilometres: its sites times its resource's site. |

### WHERE THE SITES LIE (0.7.58) (lines 54-154)

| line | len | member | says |
|---:|---:|---|---|
| 84 | 12 | `private static int[][] places()` |  |
| 97 | 9 | `private static int[] reaches()` |  |
| 108 | 1 | `public static double siteWidth(Resource r)` | A site's width in plots, for a resource: the side of a square of its site area - an Iron Mine's 0.03716 km2 is 6.43 plots (193 m). |
| 111 | 3 | `int turn()` | The quarter-turns its grid is laid at, 0 to 3: from its kind, cell and index, so the same field always lies the same way. |
| 116 | 11 | `public double[] siteAt(int k)` | Where its k-th site's centre lies, from 0 to sites - 1: {plots east, plots south} of its centre plot. |
| 135 | 3 | `public double siteAmount(int k)` | Its k-th site's share of its amount, whole: floor((k + 1) A / S) less floor(k A / S), so each site holds the amount over its sites to the whole unit, the remainders spread one each through the sites, and the shares of... |
| 139 | 5 | `private double cumulative(int k)` |  |
| 146 | 3 | `public double reach()` | How far its sites reach from its centre plot, in plots each way (L-infinity): the outermost site's centre and half a site. |
| 151 | 3 | `public static double mostReach(Resource r)` | ...and the most any field of a resource reaches: World.MAX_SITES of its sites. |

### THE CRUDE'S GRADE (0.7.79, batch O3; runs/spec-oil.md 2.2) (lines 155-193)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 1 | **type** `public enum Grade` | A crude's grade: LIGHT (Brent's cuts), MEDIUM (the research's blend) or HEAVY (Maya's), in that order. |
| 183 | 10 | `public Grade grade()` | Its crude's grade (0.7.79): a uniform draw from World.mix() of its cell, index, kind and GRADE_SALT, against GRADE_SHARES in Grade's order - the same field always the same grade, on any machine. |

