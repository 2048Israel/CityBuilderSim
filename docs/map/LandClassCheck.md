# LandClassCheck.java - 761 lines · 30 methods · 7 constants · harnesses

`ham/citybuildersim/LandClassCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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
> 
> WHY. ET1 gave every plot a relief and ET2 made buildings stand by it, so a city on hills owns ground that is worth
> less than the valley floor and was still priced as if it were not: its offers cost as much a square foot on a
> mountainside as on a plain, the best-value pick bought mountains, and a business paid the same for a pad that had to
> be cut as for a pad that did not. The arithmetic of each part (the rule, the offer, the picker, the plot) is its own,
> so each is worked out here from the rule's constants, not from the code under test.

**Uses:** [LandMarket](LandMarket.md) (53), [LandParcel](LandParcel.md) (38), [World](World.md) (25), [LandManager](LandManager.md) (18), [Game](Game.md) (14), [CityLand](CityLand.md) (14), [Resource](Resource.md) (10), [Sectors](Sectors.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (4), [AutoBuilder](AutoBuilder.md) (3), [Zoning](Zoning.md) (3), [GameFiles](GameFiles.md) (1), [Sector](Sector.md) (1), [SitePreparation](SitePreparation.md) (1), [Mining](Mining.md) (1)

## Sections

| line | section |
|---:|---|
| 118 | fixtures |
| 202 | 1. THE RULE |
| 252 | 2. THE OFFICE BUYS EACH CLASS |
| 370 | 3. THE PICKERS |
| 484 | 4. A BUSINESS BUYS EACH CLASS |
| 676 | 5. FLAT WORLDS UNCHANGED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `LandClassCheck.TOL` | `1e-9` | What a fixture of figures near one another can be told apart by: a rounding, not ground. |
| 90 | `LandClassCheck.CASH` | `1e8` | A fixture city's treasury: more than any order here costs, so a refusal is never for cash. |
| 93 | `LandClassCheck.BUSINESS_CASH` | `1e6` | ...and the cash a sector is given to pay a business's order from by fiat. |
| 96 | `LandClassCheck.GROUND` | `{ "flat", "foothill", "steep", "very steep" }` | The relief classes in World's order (FLAT to VERY_STEEP). |
| 98 | `LandClassCheck.HOUSE` | `"House"` |  |
| 101 | `LandClassCheck.ROUNDING` | `2.5 + 1e-6` | An offer's price is rounded to US$5k; a figure worked out from the ground's price can be that far off. |
| 257 | `LandClassCheck.DRY` | `0.5` | The dry ground the offers of 2 are of, square kilometres. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 51 | `static PrintStream out` |  |
| 52 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 714 | **type** `public class LandClassCheck` | A price for each class of ground, checked (ET3, slice A; docs/studies/spec-earth2-terrain.md 5.1 and 5.2): the going price stays the price of flat ground, foothill is that price less its preparation and never under th... |
| 54 | 4 | `static void check(String label, boolean ok)` |  |
| 59 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 64 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 70 | 5 | `static void quietly(Runnable r)` |  |
| 80 | 3 | `static boolean zero(double part, double plot)` | A part of a plot that should be none is none: a rounding of the split's differences at most, against the plot's size. |
| 85 | 3 | `static boolean same(double a, double b)` | The two figures are the same double, to the bit. |
| 103 | 14 | `public static void main(String[] args) throws Exception` |  |

### fixtures (lines 118-201)

| line | len | member | says |
|---:|---:|---|---|
| 122 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 133 | 13 | `static Game city(String label, double flat, double foothill, double steep, double veryStep)` | A city founded as a harness founds one, its dry ground of the shares given by relief (flat, foothill, steep, very steep, adding up to 1; CityLand.setCentreRelief()), every sector held so no business builds or scraps a... |
| 148 | 3 | `static void ironSites(Game g, int sites)` | Gives the city iron sites by fiat, for the Iron Mine: hematite, the first type. |
| 153 | 8 | `static double crowdingFor(double premium)` | The people per square kilometre at which crowding multiplies the ground's price by `premium` (1 to the ceiling): found by halving, on the model's own curve. |
| 163 | 6 | `static LandMarket market(double premium, double level)` | A bare land office priced at a crowding premium and a world price level: update() with the people that crowd 100 million square feet to it. |
| 171 | 3 | `static double[] relief(double dry, double flat, double foothill, double steep, double veryStep)` | A relief row from shares of the dry ground (flat, foothill, steep, very steep), square kilometres, no shallow sea. |
| 176 | 3 | `static double[] areas(double dry, double fresh, double sea)` | The five areas of a piece of dry ground with its water: total, dry, fresh, sea, forest. |
| 181 | 4 | `static LandParcel offer(int id, double dry, double[] reliefKm2, double priceUsd)` | An offer by hand on a bare office: dry ground by relief, no water and no ore, in its own place, at a price. |
| 187 | 3 | `static double priced(LandMarket m, double dry, double[] reliefKm2)` | An offer's price by class at a market's prices, no ore. |
| 192 | 9 | `static LandParcel oldBest(LandMarket m, List<LandParcel> list)` | The old picker, copied: the most dry square kilometres a dollar among the offers not mostly sea, the nearer on a tie. |

### 1. THE RULE (lines 202-251)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 45 | `static void theRule()` |  |

### 2. THE OFFICE BUYS EACH CLASS (lines 252-369)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 51 | `static void theOfficeBuysEachClass()` |  |
| 312 | 5 | `static LandParcel reliefOf(LandParcel p, double[] shares, double priceUsd)` | An offer of the real shelf with its ground given by relief and a price (its record, one slot changed), the rest as listed. |
| 318 | 51 | `static void buyingEachClass()` |  |

### 3. THE PICKERS (lines 370-483)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 67 | `static void thePickers()` |  |
| 442 | 35 | `static void pickersInTheGame()` |  |
| 479 | 4 | `static LandParcel place(LandParcel at, double dry, double[] reliefKm2, double priceUsd)` | An offer on the real shelf's place and rectangle with ground by relief and no ore, at a price. |

### 4. A BUSINESS BUYS EACH CLASS (lines 484-675)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 1 | `static void setGoing(LandManager lm, double going)` |  |
| 492 | 3 | `static double sum(LandManager lm, double[] parts)` | The plot's parts times the class prices: what a plot of these square feet on {flat, foothill, slope} costs. |
| 496 | 103 | `static void aBusinessBuysEachClass()` |  |
| 600 | 32 | `static void zoneShare(LandManager lm, double going, double prep)` |  |
| 633 | 42 | `static void buyback(LandManager lm, String houses, BuildingsTemplate mine, double going, double prep, double base)` |  |

### 5. FLAT WORLDS UNCHANGED (lines 676-761)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 81 | `static void flatWorldsUnchanged()` |  |

