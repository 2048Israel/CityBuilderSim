# Deposit.java - 135 lines · 10 methods · 2 constants · model

`ham/citybuildersim/Deposit.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> One field of a resource in the world's ground: which resource, the world cell it was drawn in and its place in that cell's list, its centre as a plot, its sites and what it holds - and where each of its sites lies.
> 
> WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1). A
> field is the unit the land office sells and the map draws: it belongs
> whole to the piece of ground that holds its centre (spec-land star 12), it
> is mined a site at a time, and it is worked out in the order the city
> bought it. World.fieldsInCell() draws a cell's fields from the cell's own
> stream whenever they are asked for, so a field is never stored - the cell
> and the index say which one it is, from any save, on any machine.
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
> FIELDS IN A PIECE OF GROUND).
> 
>               cell sum exactly to World.cellTotal()

**Uses:** [World](World.md) (6), [Resource](Resource.md) (3)

**Used by (10):** [CityLand](CityLand.md), [CityMap](CityMap.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [OilCheck](OilCheck.md), [World](World.md), [WorldCheck](WorldCheck.md)

## Sections

| line | section |
|---:|---|
| 41 | WHERE THE SITES LIE (0.7.58) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `Deposit.SITE_PLACES` | `places()` | The grid points a field's sites stand on, nearest the centre first, in site widths east and south: every point within 14 of the centre each way sorted by its squared distance, then from north to south, then west to ea... |
| 63 | `Deposit.PLACES_REACH` | `reaches()` | How far, in site widths each way (L-infinity), the first k + 1 sites reach from the centre. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 100 | **type** `public record Deposit(Resource kind, int cell, int index, long x, long y, int sites, double amount)` | One field of a resource in the world's ground: which resource, the world cell it was drawn in and its place in that cell's list, its centre as a plot, its sites and what it holds - and where each of its sites lies. |
| 39 | 1 | `public double km2()` | The ground it covers, in square kilometres: its sites times its resource's site. |

### WHERE THE SITES LIE (0.7.58) (lines 41-135)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 12 | `private static int[][] places()` |  |
| 78 | 9 | `private static int[] reaches()` |  |
| 89 | 1 | `public static double siteWidth(Resource r)` | A site's width in plots, for a resource: the side of a square of its site area - an Iron Mine's 0.03716 km2 is 6.43 plots (193 m). |
| 92 | 3 | `int turn()` | The quarter-turns its grid is laid at, 0 to 3: from its kind, cell and index, so the same field always lies the same way. |
| 97 | 11 | `public double[] siteAt(int k)` | Where its k-th site's centre lies, from 0 to sites - 1: {plots east, plots south} of its centre plot. |
| 116 | 3 | `public double siteAmount(int k)` | Its k-th site's share of its amount, whole: floor((k + 1) A / S) less floor(k A / S), so each site holds the amount over its sites to the whole unit, the remainders spread one each through the sites, and the shares of... |
| 120 | 5 | `private double cumulative(int k)` |  |
| 127 | 3 | `public double reach()` | How far its sites reach from its centre plot, in plots each way (L-infinity): the outermost site's centre and half a site. |
| 132 | 3 | `public static double mostReach(Resource r)` | ...and the most any field of a resource reaches: World.MAX_SITES of its sites. |

