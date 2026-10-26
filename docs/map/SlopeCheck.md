# SlopeCheck.java - 912 lines · 32 methods · 15 constants · harnesses

`ham/citybuildersim/SlopeCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Buildable by slope, checked (ET2, slice D; docs/studies/spec-earth2-terrain.md 3.2, 3.4, 4.1, 4.2, 4.4): a building
> stands on flat and foothill ground and on no steeper, a mine on any, a foothill pad is prepared and paid for as
> builders' work, and the map and the founding keep to the same ground.
> 
> WHAT THIS HAS TO PROVE, every fixture causing the condition it tests and every figure the model's own constant:
> 
>   1. A BUILDING ON EACH CLASS OF GROUND. Four cities, their dry ground all flat, all foothill, all steep, all
>      very steep: a House's canAllocate(), the city's order (Game.buildStack()) and a business's (buildFor()) go
>      through on flat and foothill and are refused on steep and very steep - NO_LAND, nothing allocated, no money
>      taken.
>   2. A MINE ON EACH CLASS OF GROUND. The same four: a site-bound works (Game.standsAnywhere()) is allowed on all,
>      its ground comes from the class it stands on, and it is charged no preparation, foothill included.
>   3. THE ORDER OF USE. Mixed ground, 40% flat, 30% foothill, 30% steep: buildings take flat before foothill
>      (buildingsOnFoothill stays 0 until the flat is used, then is the excess, to the square foot); works take steep,
>      then foothill, then flat; idle steep ground is not free for a building and is free for a works.
>   4. THE PREPARATION'S PRICE, against the constants: a House onto foothill carries its land as prepSqFt, the
>      points SitePreparation's dollars come to at the House's founding cash a point, the money at the House's
>      non-material cost a point, and the quote's total exceeds the same order on flat by that money grossed up by
>      the builders' sales tax; on flat all three are 0.
>   5. PREPARED ONCE. Built on foothill, demolished, built again on the same ground: no second preparation, and the
>      foothill prepared never falls.
>   6. BUILDERS' WORK IN THE BOOKS. The city's order and a business's onto foothill: Construction's backlog rises by
>      the buildings' points and the pad's, its unearned revenue by the bill; the pad is paid before the first building
>      finishes; over the months the money audit, the GDP identity (the pad's work counted as construction) and the
>      sectors' statements hold, and what was billed is all earned by the time it is built.
>   7. A STREET, A BUILDING AND A MINE ON THE MAP BY RELIEF. On the plan's own predicate and on a district whose
>      ground has bands of steep, very steep and ice: flat and foothill take streets and buildings, the bands take
>      neither - and the same district without the bands would have used them; a mine's site stands on dry ground
>      whatever the slope.
>   8. FOUNDING ON BUILDABLE GROUND, where earth-2.bin is named: Medellin's snapped site and its eight test points
>      are buildable, a spot among Everest's steep slopes is refused in the new words naming the nearest buildable
>      ground (its summit, ice, in the ice's), and a plan over Medellin's real ground puts no street or building on
>      a steep plot of it.
>   9. HOW OFTEN IT FIRES IN A REAL RUN, where earth-2.bin is named: a Medellin city played on the real Earth, the
>      orders charged a preparation, the foothill prepared and its points as a share of every point billed counted;
>      it fires, and no building stands on steep ground.
> 
> WHY. ET1 gave every plot a relief and drew it; ET2's slices A to C made the land model, the price, the map and the
> founding read it. Each has its own arithmetic and none of it is the arithmetic of another, so a figure here is
> worked out from the constants the model states (SitePreparation, World, LandManager) and never typed. The
> sections that need the real Earth say so in one line and skip where the file is not named, as EarthFoundingCheck
> does (-Dcbs.earth=<path>, or data/earth-2.bin).

**Uses:** [World](World.md) (68), [Game](Game.md) (53), [DistrictPlan](DistrictPlan.md) (33), [LongPlaytest](LongPlaytest.md) (26), [LandManager](LandManager.md) (19), [EarthSite](EarthSite.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (11), [EarthData](EarthData.md) (11), [SitePreparation](SitePreparation.md) (11), [SectorStatementCheck](SectorStatementCheck.md) (7), [BuildingVisual](BuildingVisual.md) (7), [BuildingsStacks](BuildingsStacks.md) (4), [Sectors](Sectors.md) (4), [Construction](Construction.md) (4), [PlanCheck](PlanCheck.md) (4), [EarthTerrain](EarthTerrain.md) (4), [CityLand](CityLand.md) (3), [Mining](Mining.md) (3), [GameFiles](GameFiles.md) (2), [Resource](Resource.md) (2), [MoneyAudit](MoneyAudit.md) (2), [Founding](Founding.md) (2), [Sector](Sector.md) (1), [NationalAccounts](NationalAccounts.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [EarthTerrainCheck](EarthTerrainCheck.md) (1), [LandMap](LandMap.md) (1), [CityMap](CityMap.md) (1), [TreasuryFund](TreasuryFund.md) (1)

**Used by (1):** [RunWorksCheck](RunWorksCheck.md)

## Sections

| line | section |
|---:|---|
| 165 | 1. A BUILDING ON EACH CLASS OF GROUND |
| 221 | 2. A MINE ON EACH CLASS OF GROUND |
| 266 | 3. THE ORDER OF USE |
| 364 | 4. THE PREPARATION'S PRICE, AGAINST THE CONSTANTS |
| 412 | 5. PREPARED ONCE |
| 483 | 6. BUILDERS' WORK IN THE BOOKS |
| 619 | 7. A STREET, A BUILDING AND A MINE ON THE MAP, BY RELIEF |
| 732 | 8. FOUNDING ON BUILDABLE GROUND (the real Earth) |
| 829 | 9. HOW OFTEN IT FIRES IN A REAL RUN (Medellin, the real Earth) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `SlopeCheck.CASH` | `1e8` | A fixture city's treasury: more than any order here costs, so a refusal is never for cash. |
| 90 | `SlopeCheck.BUSINESS_CASH` | `1e6` | ...and the cash the Real Estate sector is given to pay a business's order from by fiat: an order's worth and more, in a figure small enough for the sectors' statements to be exact to the cent (a fixture rich beyond it... |
| 93 | `SlopeCheck.TOL` | `1e-9` | What a fixture of figures near one another can be told apart by: a rounding, not ground. |
| 96 | `SlopeCheck.GROUND` | `{ "flat", "foothill", "steep", "very steep" }` | The relief classes' names, in World's order (FLAT to VERY_STEEP). |
| 98 | `SlopeCheck.HOUSE` | `"House"` |  |
| 101 | `SlopeCheck.HOME_CARE` | `"Home Care Service"` | A building of the city's own, which the city may demolish (Game.demolish(): a House is a landlord's) and the builders take a few months to put up. |
| 170 | `SlopeCheck.ORDER` | `5` | How many Houses the fixtures of 1 order at once. |
| 271 | `SlopeCheck.FLAT_SHARE` | `0.4, FOOTHILL_SHARE = 0.3, STEEP_SHARE = 0.3` | The mixed ground's shares: 40% flat, 30% foothill, 30% steep. |
| 417 | `SlopeCheck.PATIENCE` | `120` | The most months a fixture waits for the builders before it says they did not come. |
| 554 | `SlopeCheck.HOUSES` | `100` | Houses ordered in 6, enough that the pad alone (their land's points) is more than a month of the builders' work. |
| 624 | `SlopeCheck.FOOT_COLUMNS` | `DistrictPlan.CELL, STEEP_FROM = 3 * DistrictPlan.CELL + DistrictPlan.CELL / 8...` | Where the bands of 7's district lie, in plots: foothill the first cell's columns; steep, then very steep, then ice rows below. |
| 629 | `SlopeCheck.VERY_FROM` | `STEEP_FROM + STEEP_ROWS, ICE_FROM = VERY_FROM + VERY_ROWS, ICE_TO = ICE_FROM ...` | The bands' rows: steep [STEEP_FROM, VERY_FROM), very steep [VERY_FROM, ICE_FROM), ice [ICE_FROM, ICE_TO). |
| 757 | `SlopeCheck.MEDELLIN` | `{ 6.25, - 75.57 }` |  |
| 760 | `SlopeCheck.SUMMIT` | `EarthTerrainCheck.SUMMIT, SLOPES = { 27.60, 86.70 }` | Everest's summit: ice (EarthTerrainCheck's SUMMIT) - and a spot on its steep southern slopes, no ice about it, no buildable ground within 5 km. |
| 834 | `SlopeCheck.RUN_MONTHS` | `2000` | The months the Medellin run plays: enough for the land office's offers to have brought the city steep ground (at 1,000 it owns none; at 2,000, about a tenth of it) - the section about twenty seconds. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |
| 61 | `static PrintStream out` |  |
| 62 | `static PrintStream quiet` |  |
| 489 | `int played, moneyOff, gdpOff, gdpBooksOff, driftOff, firstFinished` |  |
| 490 | `double applied, appliedAtFirst, worstResidual` |  |
| 492 | `final List<Double> recognised` | Each month's recognised work (the builders') and the construction GDP counted that month, in order. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 855 | **type** `public class SlopeCheck` | Buildable by slope, checked (ET2, slice D; docs/studies/spec-earth2-terrain.md 3.2, 3.4, 4.1, 4.2, 4.4): a building stands on flat and foothill ground and on no steeper, a mine on any, a foothill pad is prepared and p... |
| 64 | 4 | `static void check(String label, boolean ok)` |  |
| 69 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 74 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 80 | 5 | `static void quietly(Runnable r)` |  |
| 103 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 114 | 14 | `static Game city(String label, double flat, double foothill, double steep, double veryStep)` | A city founded as a harness founds one, its dry ground of the shares given by relief (flat, foothill, steep, very steep, adding up to 1; CityLand.setCentreRelief()), every sector held so no business builds or scrapps ... |
| 130 | 3 | `static double ofClass(Game g, int relief)` | The city's dry ground of one relief class, square feet (CityLand's, which the land model reads live). |
| 134 | 5 | `static Game.BuildResult order(Game g, BuildingsTemplate t, int n)` |  |
| 141 | 3 | `static void ironSites(Game g, int sites)` | Gives the city iron sites by fiat, for the Iron Mine (BuildCardCheck's way): hematite, the first type. |
| 145 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1. A BUILDING ON EACH CLASS OF GROUND (lines 165-220)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 48 | `static void buildingOnEachGround()` |  |

### 2. A MINE ON EACH CLASS OF GROUND (lines 221-265)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 40 | `static void mineOnEachGround()` |  |

### 3. THE ORDER OF USE (lines 266-363)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 5 | `static void orderOfUse()` |  |
| 279 | 48 | `static void buildingsFirst()` |  |
| 328 | 35 | `static void worksFirst()` |  |

### 4. THE PREPARATION'S PRICE, AGAINST THE CONSTANTS (lines 364-411)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 43 | `static void preparationPrice()` |  |

### 5. PREPARED ONCE (lines 412-482)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 63 | `static void preparedOnce()` |  |

### 6. BUILDERS' WORK IN THE BOOKS (lines 483-618)

| line | len | member | says |
|---:|---:|---|---|
| 488 | 6 | **type** `static final class Months` | What the months of an order audited: those that did not close or hold, and how many were. |
| 496 | 3 | `static boolean closes(MoneyAudit.Result r)` | The playtest's own test of a month's money audit (LongPlaytest.audit()): no residual over a cent, or none over a ten-millionth of the money moved. |
| 501 | 24 | `static void month(Game g, BuildingsStacks stack, Months m)` | One month, played and audited as the playtest audits one: the money, the GDP identity, GDP's construction against the builders' own books, and every sector's statements (SectorStatementCheck.audit()). |
| 527 | 7 | `static int gdpOffTheBooks(Months m)` | Counts the months in which what GDP counted as construction was not the work the builders recognised the month before. |
| 536 | 8 | `static int[] statements()` | What SectorStatementCheck's tallies hold of the identities it checks: how many it checked, how many missed by more than a cent. |
| 546 | 6 | `static String statementsMissed()` | The first identity SectorStatementCheck's tallies missed, in its words: "none" when none. |
| 556 | 62 | `static void inTheBooks()` |  |

### 7. A STREET, A BUILDING AND A MINE ON THE MAP, BY RELIEF (lines 619-731)

| line | len | member | says |
|---:|---:|---|---|
| 632 | 15 | `static DistrictPlan.Input district(BuildingVisual.Type[] types, boolean banded)` | Jerus's mix on a district of flat ground, fully owned, at the fullest fill the plans are checked at; banded, the relief and ice above laid over it. |
| 649 | 1 | **type** `record Laid(long streetFlat, long streetFoot, long streetUnfit, long buildFlat, long buildFoot, long buildU...` | What a plan laid on each kind of ground of its input: street plots, and building plots, on buildable flat, on foothill, and on ground no street or building may take. |
| 652 | 16 | `static Laid laid(DistrictPlan p, DistrictPlan.Input in)` | Counts what a plan laid by the ground the input has under each plot (World.buildable() of its cover and relief). |
| 670 | 5 | `static BuildingVisual.Type[] planTypes()` | The building types the plans are drawn with (BuildingVisual.table() of the catalogue, which says as it loads). |
| 677 | 7 | `static boolean plotBuilt(DistrictPlan p, int q)` | Whether a plan's building stands on plot q of its frame. |
| 685 | 46 | `static void onTheMap()` |  |

### 8. FOUNDING ON BUILDABLE GROUND (the real Earth) (lines 732-828)

| line | len | member | says |
|---:|---:|---|---|
| 737 | 19 | `static EarthData theEarth()` | The Earth's data where it is named or lies at its place, or null - and the one line saying so. |
| 762 | 3 | `static World.Key key(double[] at)` |  |
| 766 | 62 | `static void foundingOnBuildableGround(EarthData file)` |  |

### 9. HOW OFTEN IT FIRES IN A REAL RUN (Medellin, the real Earth) (lines 829-912)

| line | len | member | says |
|---:|---:|---|---|
| 836 | 76 | `static void inARealRun(EarthData file)` |  |

