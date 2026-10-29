# LandClassCheck.java - 1,349 lines · 51 methods · 8 constants · harnesses

`ham/citybuildersim/LandClassCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> A price for each class of ground, checked (ET3, slice A; docs/studies/spec-earth2-terrain.md 5.1 and 5.2): the going
> price stays the price of flat ground, foothill is that price less its preparation and never under the base price of
> unwanted ground, steep and very steep ground is the base price - no class dearer than flat. The land office prices
> its offers by the ground they hold, the pickers rank them by the ground a building can stand on with its preparation
> counted, and a business pays the city for its plot class by class.
> 
> WHAT THIS HAS TO PROVE, every fixture causing the condition it tests and every figure worked out from the model's own
> constants and getters (LandMarket.openingUsdPerSqFt(), SitePreparation, LandManager's square feet) and never typed:
> 
>   1. THE RULE. LandMarket.reliefPrice() for each class with the going price above the base plus the preparation
>      (foothill = going less the preparation), between the base and base plus the preparation (foothill = base) and
>      below the base (every class = going), at a price level other than 1; never above flat.
>   2. THE OFFICE BUYS EACH CLASS. An offer's price by class: all flat, all foothill, all steep, all very steep, a mix,
>      water at its shares of the flat price, resources as before, rounded to US$5k; and the city buys an offer of each
>      class through LandManager.buyParcel(), paying that price at the rate, the class added to its ground.
>   3. THE PICKERS. Two offers at one dry area, one all flat and one mostly steep and cheaper a dry km2: bestValue()
>      takes the flat one; an all-foothill offer at the flat one's price loses by its preparation; listings of flat
>      ground pick exactly as getDryKm2PerUsd() would; listings with no buildable ground still have a best; and Game's
>      bestOffer() and the auto-builder's shortcut rank by buildable ground and price with the foothill prepared.
>   4. A BUSINESS BUYS EACH CLASS. On a city that owns flat, foothill and steep ground: a building allocated while flat
>      is free pays the flat price, once flat is used it pays the foothill price, a works pays the steep price on the
>      steep, a plot straddling two classes pays the sum by plotByRelief(); the zone's share applies to each class; a
>      plot given back is bought back by the reverse of the split; and the foothill's preparation the price reads is
>      the quote's.
>   5. FLAT WORLDS UNCHANGED. On a city that owns neither foothill nor steep ground every new figure is the old one to
>      the bit: the price by class against the four-argument price(), buildableKm2PerUsd() against getDryKm2PerUsd(),
>      priceFor() and buybackPriceFor() with and without a works and a zone against the old expression, the pickers
>      against the old loops.
>   6. THE BOOKS BY CLASS (slice B, spec 5.3). On a city of buildings on flat and foothill and mines on the steep: a
>      sector's land by class is its buildings' ground at the city's buildings' mix and its works' at the works' mix,
>      adding to what it holds and, over every sector and category, to the split's own; the books' class prices are the
>      land office's, so a sector's land is worth its areas at them; the sheet's land is that, note 11's rows add up to
>      it and only the mines have a steep row; a zone's share applies to every class alike in the roll's land; the equity
>      statement's land revaluation is last month's area on each class times that class's price change; a save keeps
>      the land by class and one without it reads the one price; redenominating rescales its prices. On a flat city
>      (6b) every one of them is the old one-price figure, to the bit, and there is no note.
>   7. THE SCREENS' WORDS ARE THE MODEL'S (slice C, spec 5.3). LandClassView's money words are ui.Money's to the digit; the land
>      office's lines are the getters' prices in the screens' money, a line for each class the city owns or the shelf offers
>      and none for a flat city and a flat shelf, the foothill's line saying how it comes to its price; an offer's line
>      is its relief's figures in its dry ground's unit with the buildable ground and the price with the foothill prepared,
>      empty for an offer all flat; note 11's lines are the books' areas, prices and values, one for each class held.
> 
> WHY. ET1 gave every plot a relief and ET2 made buildings stand by it, so a city on hills owns ground that is worth
> less than the valley floor and was still priced as if it were not: its offers cost as much a square foot on a
> mountainside as on a plain, the best-value pick bought mountains, and a business paid the same for a pad that had to
> be cut as for a pad that did not. The arithmetic of each part (the rule, the offer, the picker, the plot) is its own,
> so each is worked out here from the rule's constants, not from the code under test.

**Uses:** [LandMarket](LandMarket.md) (59), [LandManager](LandManager.md) (46), [LandParcel](LandParcel.md) (45), [SectorStatements](SectorStatements.md) (45), [Sectors](Sectors.md) (44), [Game](Game.md) (36), [LandClassView](LandClassView.md) (33), [World](World.md) (29), [SectorBooks](SectorBooks.md) (15), [Money](Money.md) (15), [CityLand](CityLand.md) (14), [Resource](Resource.md) (10), [Sector](Sector.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [BuildingType](BuildingType.md) (8), [Zoning](Zoning.md) (5), [EconomyManager](EconomyManager.md) (5), [BuildingManager](BuildingManager.md) (4), [AutoBuilder](AutoBuilder.md) (3), [Mining](Mining.md) (3), [GameFiles](GameFiles.md) (1), [SitePreparation](SitePreparation.md) (1), [Currency](Currency.md) (1)

**Used by (3):** [HydroCheck](HydroCheck.md), [LandClassRenders](LandClassRenders.md), [RunWorksCheck](RunWorksCheck.md)

## Sections

| line | section |
|---:|---|
| 133 | fixtures |
| 217 | 1. THE RULE |
| 267 | 2. THE OFFICE BUYS EACH CLASS |
| 385 | 3. THE PICKERS |
| 499 | 4. A BUSINESS BUYS EACH CLASS |
| 691 | 5. FLAT WORLDS UNCHANGED |
| 777 | 6. THE BOOKS VALUE A SECTOR'S LAND BY THE GROUND IT STANDS ON |
| 1134 | 7. THE SCREENS' WORDS ARE THE MODEL'S (slice C, spec 5.3) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 90 | `LandClassCheck.TOL` | `1e-9` | What a fixture of figures near one another can be told apart by: a rounding, not ground. |
| 103 | `LandClassCheck.CASH` | `1e8` | A fixture city's treasury: more than any order here costs, so a refusal is never for cash. |
| 106 | `LandClassCheck.BUSINESS_CASH` | `1e6` | ...and the cash a sector is given to pay a business's order from by fiat. |
| 109 | `LandClassCheck.GROUND` | `{ "flat", "foothill", "steep", "very steep" }` | The relief classes in World's order (FLAT to VERY_STEEP). |
| 111 | `LandClassCheck.HOUSE` | `"House"` |  |
| 114 | `LandClassCheck.ROUNDING` | `2.5 + 1e-6` | An offer's price is rounded to US$5k; a figure worked out from the ground's price can be that far off. |
| 272 | `LandClassCheck.DRY` | `0.5` | The dry ground the offers of 2 are of, square kilometres. |
| 782 | `LandClassCheck.HOUSES` | `100, DINERS = 30, MINES = 2` | The orders the hilly city of 6 is built from: houses and diners on the buildings' ground, iron mines on the works'. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `static int fails` |  |
| 64 | `static PrintStream out` |  |
| 65 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 61 | 1289 | **type** `public class LandClassCheck` | A price for each class of ground, checked (ET3, slice A; docs/studies/spec-earth2-terrain.md 5.1 and 5.2): the going price stays the price of flat ground, foothill is that price less its preparation and never under th... |
| 67 | 4 | `static void check(String label, boolean ok)` |  |
| 72 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 77 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 83 | 5 | `static void quietly(Runnable r)` |  |
| 93 | 3 | `static boolean zero(double part, double plot)` | A part of a plot that should be none is none: a rounding of the split's differences at most, against the plot's size. |
| 98 | 3 | `static boolean same(double a, double b)` | The two figures are the same double, to the bit. |
| 116 | 16 | `public static void main(String[] args) throws Exception` |  |

### fixtures (lines 133-216)

| line | len | member | says |
|---:|---:|---|---|
| 137 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 148 | 13 | `static Game city(String label, double flat, double foothill, double steep, double veryStep)` | A city founded as a harness founds one, its dry ground of the shares given by relief (flat, foothill, steep, very steep, adding up to 1; CityLand.setCentreRelief()), every sector held so no business builds or scraps a... |
| 163 | 3 | `static void ironSites(Game g, int sites)` | Gives the city iron sites by fiat, for the Iron Mine: hematite, the first type. |
| 168 | 8 | `static double crowdingFor(double premium)` | The people per square kilometre at which crowding multiplies the ground's price by `premium` (1 to the ceiling): found by halving, on the model's own curve. |
| 178 | 6 | `static LandMarket market(double premium, double level)` | A bare land office priced at a crowding premium and a world price level: update() with the people that crowd 100 million square feet to it. |
| 186 | 3 | `static double[] relief(double dry, double flat, double foothill, double steep, double veryStep)` | A relief row from shares of the dry ground (flat, foothill, steep, very steep), square kilometres, no shallow sea. |
| 191 | 3 | `static double[] areas(double dry, double fresh, double sea)` | The five areas of a piece of dry ground with its water: total, dry, fresh, sea, forest. |
| 196 | 4 | `static LandParcel offer(int id, double dry, double[] reliefKm2, double priceUsd)` | An offer by hand on a bare office: dry ground by relief, no water and no ore, in its own place, at a price. |
| 202 | 3 | `static double priced(LandMarket m, double dry, double[] reliefKm2)` | An offer's price by class at a market's prices, no ore. |
| 207 | 9 | `static LandParcel oldBest(LandMarket m, List<LandParcel> list)` | The old picker, copied: the most dry square kilometres a dollar among the offers not mostly sea, the nearer on a tie. |

### 1. THE RULE (lines 217-266)

| line | len | member | says |
|---:|---:|---|---|
| 221 | 45 | `static void theRule()` |  |

### 2. THE OFFICE BUYS EACH CLASS (lines 267-384)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 51 | `static void theOfficeBuysEachClass()` |  |
| 327 | 5 | `static LandParcel reliefOf(LandParcel p, double[] shares, double priceUsd)` | An offer of the real shelf with its ground given by relief and a price (its record, one slot changed), the rest as listed. |
| 333 | 51 | `static void buyingEachClass()` |  |

### 3. THE PICKERS (lines 385-498)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 67 | `static void thePickers()` |  |
| 457 | 35 | `static void pickersInTheGame()` |  |
| 494 | 4 | `static LandParcel place(LandParcel at, double dry, double[] reliefKm2, double priceUsd)` | An offer on the real shelf's place and rectangle with ground by relief and no ore, at a price. |

### 4. A BUSINESS BUYS EACH CLASS (lines 499-690)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 1 | `static void setGoing(LandManager lm, double going)` |  |
| 507 | 3 | `static double sum(LandManager lm, double[] parts)` | The plot's parts times the class prices: what a plot of these square feet on {flat, foothill, slope} costs. |
| 511 | 103 | `static void aBusinessBuysEachClass()` |  |
| 615 | 32 | `static void zoneShare(LandManager lm, double going, double prep)` |  |
| 648 | 42 | `static void buyback(LandManager lm, String houses, BuildingsTemplate mine, double going, double prep, double base)` |  |

### 5. FLAT WORLDS UNCHANGED (lines 691-776)

| line | len | member | says |
|---:|---:|---|---|
| 695 | 81 | `static void flatWorldsUnchanged()` |  |

### 6. THE BOOKS VALUE A SECTOR'S LAND BY THE GROUND IT STANDS ON (lines 777-1133)

| line | len | member | says |
|---:|---:|---|---|
| 785 | 3 | `static boolean near(double a, double b)` | The two figures agree to a rounding of the split's arithmetic, relative to the larger. |
| 789 | 5 | `static boolean near(double[] a, double[] b)` |  |
| 796 | 6 | `static boolean order(Game g, String sector, BuildingsTemplate t, int n)` | A business's order, built by hand from cash it is given, as section 4's are. |
| 804 | 7 | `static void closeBooks(Game g)` | The books as a month's close leaves them (Game.recordMonth(): the land's price and its classes pushed, the sheets pushed, the books taken), without playing the month. |
| 813 | 3 | `static double[] officePrices(LandManager lm)` | The classes' prices the land office asks now, {flat, foothill, steep}. |
| 817 | 3 | `static double dot(double[] areas, double[] prices)` |  |
| 821 | 3 | `static double total(double[] a)` |  |
| 825 | 181 | `static void booksByClass()` |  |
| 1008 | 19 | `static void zoneOnEachClass(Game g, EconomyManager em, BuildingManager bm, double[] houseAreas, double[] p)` | A zone's share applies to every class alike in the roll's land. |
| 1029 | 44 | `static void savedAndLoaded(Game g, String[] keys, SectorBooks books)` | A save keeps the land by class; a save without it reads as the one price. |
| 1075 | 16 | `static void redenominated(Game g, EconomyManager em, SectorBooks books, String[] keys, double[] p)` | Redenominating the money rescales the three prices and leaves the areas. |
| 1093 | 40 | `static void flatCityKeepsTheOnePrice()` | A city with only flat ground keeps the one price: the old expression, to the bit, and no land by class. |

### 7. THE SCREENS' WORDS ARE THE MODEL'S (slice C, spec 5.3) (lines 1134-1349)

| line | len | member | says |
|---:|---:|---|---|
| 1139 | 3 | `static String localWords(Game g, double thousandsPerSqFt)` | The city's money's mark, then a ground price a square metre as the screens write it (LandScreen.local()): "D$41.20". |
| 1144 | 3 | `static String usdWords(double thousandsPerSqFt)` | ...in the world's (LandScreen.dollars()): "US$3.10". |
| 1149 | 9 | `static List<String> expectedOffice(Game g, boolean foothill, boolean steep, String foothillHow)` | The office's lines for a city as the screens' own money words write them, from the getters alone: flat first, then the foothill's and the steep's where asked. |
| 1159 | 7 | `static void screensWords()` |  |
| 1168 | 18 | `static void moneyWordsAreTheScreens()` | The view's money words are ui.Money's, over figures from a ten-thousandth of a cent a square metre to a quadrillion dollars. |
| 1188 | 52 | `static void officeWords()` | The land office's lines: every price the getter's, a line for each class owned or on the shelf, none for a flat city. |
| 1242 | 34 | `static void offerWords()` | An offer's ground by class: the figures its relief's, the buildable the flat and foothill, the price with the foothill prepared the market's. |
| 1282 | 23 | `public static Game hillyCity(String label)` | A city on hills with its books closed (slice C's fixture, and tools.LandClassRenders' second block): HOUSES houses and a Real Estate sector on flat and foothill ground, MINES iron mines on the steep ground with their ... |
| 1307 | 42 | `static void noteWords()` | Note 11 as words: its rows' figures the statement's, for the sector that builds on flat and foothill and the sector that works the steep. |

