# Forms1900Check.java - 994 lines · 38 methods · 5 constants · harnesses

`ham/citybuildersim/Forms1900Check.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The farm's horses, the factory's steam, road freight's horse and wagon and the homes' lamp oil and coal, as forms a share of a sector or of the homes runs.
> 
> WHAT THIS HAS TO PROVE (R0c, 2026-11-05; research-1900 section 4). The mechanics are SHARES, 0 to 1, of a sector that runs the 1900 way
> (Agriculture.setHorseShare(), Sector.setSteamShare(), Haulage.setHorseShare()) or of the homes (HomeFuel.setLampShare(), setCoalShare()); R1 and
> R0e will set them and nothing sets them in a played city yet, so what is checkable is that off the month is today's and that on each does what it says:
> 
>   1. AT 0 THE MONTH IS TODAY'S, to the bit: two twin cities, one with every share set to 0 on every sector every month (Haulage's
>      included), agree on the treasury, every sector's books, Haulage's fleet and quote and the city's power demand for twelve months, and
>      the shares never put a good on a sector's list
>   2. THE HORSES EAT THE FARM'S OWN CROP: captive, the horse share of HORSE_FEED_SHARE of what the farms made at the rate; no crop is bought
>      or sold between the farm and itself, and the market saw the harvest less the feed
>   3. THE HORSE FARM NEEDS MORE HANDS: its posts per tier, the payroll at full staffing and the posts the labour market is offered all
>      read the horse posts alike
>   4. STEAM TAKES THE FACTORY OFF THE GRID: its draw, its bill and the city's demand fall by the share, its coal need rises by the
>      coal the engine burns for it, and the coal is bought at the rate
>   5. A WORKS THAT ALREADY BURNS COAL ADDS THE STEAM COAL TO IT
>   6. THE MONEY ADDS UP, with both forms on every sector, every month
>   7. THE CARTS BURN NO DIESEL AND EAT CROPS: in a city whose bulk Haulage carries, at a horse share of 1 the trucks draw no diesel and the
>      teams' feed is carried / TONNES_A_TEAM_MONTH teams x HORSES_A_TEAM x HORSE_FEED_TONNES_A_MONTH tonnes of crops, drawn on Freight's books
>      from the farms or the world; at one half the diesel and the feed are each half a tonne's
>   8. THE CARTERS ARE POSTS AND ARE PAID: the NO_DIPLOMA posts Freight offers, the city counts and the labour market fills rise by
>      round(need / TONNES_A_TEAM_MONTH x CARTERS_A_TEAM), Haulage's payroll is the larger by their wages and the Rail division's is not
>   9. A HORSE CITY BUYS FEWER TRUCKS: at 1 none are bought or held, at one half about half the twin's
>  10. THE QUOTE PRICES THE HORSE: estimate() is the blend of today's and the teams' (feed and carters over a team's tonnes), and the
>      reprice with a history is finite, positive and covers the feed and the carters
>  11. THE MONEY ADDS UP, with the carts carrying every bulk tonne, every month
>  12. A LAMP-LIT HOME BUYS LAMP OIL: with the lamp share at 1 the JET Retail sold to the households is the homes x LAMP_OIL_LITRES_A_HOME_MONTH,
>      drawn on its books and sold at the pump's rule on its own wholesale with no fuel duty, and the bill is the sale's value exactly; at one
>      half the litres a home are half
>  13. A STOVE-HEATED HOME BUYS COAL: the same with the coal share, THERMAL_COAL the homes x COAL_TONNES_A_HOME_MONTH, the world's part the
>      bill's imported part, and the city's own stock drawn before the world's
>  14. THE HOUSEHOLDS PAY IT: with both shares at 1 their expenses rise by the bill over a twin with both at 0, the rows pay it by their
>      doors (the orphans and prisoners none), it is in the waterfall the cells are settled by and the cells' fixed bills rise by it; with
>      the shares back to 0 it is gone
>  15. THE MONEY ADDS UP, with the homes buying lamp oil and coal every month
> 
> Every fixture CAUSES its condition: the shares are set, and the sector asked is one with buildings standing that draw power (or, for 7 to
> 11, a city with a bulk chain at home and trade both ways: HaulageCheck.base(); for 12 to 15, a town with homes and a grocery, the lamp and
> coal shares set before the month). The figures are asserted against the model's own constants (HORSE_FEED_SHARE, HORSE_POSTS_FACTOR,
> STEAM_COAL_KG_A_KWH, HydroSites.KWH_PER_UNIT, Haulage.HORSES_A_TEAM, HORSE_FEED_TONNES_A_MONTH, TONNES_A_TEAM_MONTH, CARTERS_A_TEAM,
> HomeFuel.LAMP_OIL_LITRES_A_HOME_MONTH, COAL_TONNES_A_HOME_MONTH, Retail.pumpPrice()) and its own getters, never numbers copied from the brief.

**Uses:** [Good](Good.md) (81), [Game](Game.md) (38), [Sector](Sector.md) (26), [Haulage](Haulage.md) (17), [Agriculture](Agriculture.md) (10), [Retail](Retail.md) (9), [HouseholdAccounts](HouseholdAccounts.md) (7), [MoneyAudit](MoneyAudit.md) (6), [HomeFuel](HomeFuel.md) (6), [Household](Household.md) (5), [Freight](Freight.md) (4), [HydroSites](HydroSites.md) (4), [GameFiles](GameFiles.md) (3), [BuildingManager](BuildingManager.md) (3), [CementConcrete](CementConcrete.md) (3), [HaulageCheck](HaulageCheck.md) (3), [Founding](Founding.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [JobType](JobType.md) (2), [GoodsMarket](GoodsMarket.md) (2), [TaxPolicy](TaxPolicy.md) (2), [MaterialsCheck](MaterialsCheck.md) (1), [Statement](Statement.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (4):** [BasketYearCheck](BasketYearCheck.md), [EquipmentCheck](EquipmentCheck.md), [FittingCheck](FittingCheck.md), [RecipeCheck](RecipeCheck.md)

## Sections

| line | section |
|---:|---|
| 221 | 1. at 0 the month is today's |
| 261 | 2 and 3. the horses |
| 357 | 4. steam takes the factory off the grid |
| 408 | 5. a works that already burns coal |
| 436 | 6. the money adds up |
| 461 | 7 to 11. road freight's horse and wagon |
| 740 | 12 to 15. the homes' lamp oil and coal |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `Forms1900Check.SETTLE` | `6` | The months a fixture city is played before anything is asked of it, so its farms and bakeries are running. |
| 90 | `Forms1900Check.MONTHS` | `12` | The months the twin cities are compared over, and the money audit is held over. |
| 464 | `Forms1900Check.CART_MONTHS` | `12` | The months the cart cities are played: the fleet and the money audit are held over them. |
| 541 | `Forms1900Check.READ` | `6` | The month the per-tonne comparisons are read in: the trucks are seeded, the plant has run, and none of the three cities is short of trucks. |
| 544 | `Forms1900Check.FARM_MONTHS` | `4` | The months the farm city is played before its books are read: the farms have a shelf by the third. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 59 | `static int fails` |  |
| 60 | `static PrintStream out` |  |
| 61 | `static PrintStream quiet` |  |
| 468 | `double need, carried, truckTonnes, teamTonnes, litres, diesel, teams, feedTonnes, feedTonnesImported, feed,...` |  |
| 469 | `double trucks, share, quote, allowed, haulagePay` |  |
| 470 | `boolean quoteKnown, auditCloses` |  |
| 471 | `String audit` |  |
| 473 | `double cropsHome` | What Freight's statement booked of crops at home and abroad (-1: no line), and of trucks. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 938 | **type** `public class Forms1900Check` | The farm's horses, the factory's steam, road freight's horse and wagon and the homes' lamp oil and coal, as forms a share of a sector or of the homes runs. |
| 63 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 68 | 1 | `static void check(String label, boolean ok)` |  |
| 71 | 4 | `static void near(String label, double actual, double expected, double rel)` | A figure against the model's, to a relative tolerance (and an absolute floor for a figure that is nothing). |
| 76 | 5 | `static void quietly(Runnable r)` |  |
| 82 | 3 | `static void play(Game g, int months)` |  |
| 93 | 21 | `static Game town(String label)` | A town with farms for the horses and bakeries for the steam: roads, homes, groceries, power and water, the farms and the ovens standing at once. |
| 116 | 64 | `static void snapshot(Game g, List<String> names, List<Double> values)` | Every figure the twins are compared on, with its name: the treasury, the city's power demand, and each sector's books and rate. |
| 182 | 13 | `static String firstDifference(Game a, Game b)` | The first figure two cities do not agree on, to the bit, or null for none. |
| 197 | 1 | `static long[] cityPosts(Game g)` | What a city's posts are, per job type, as the labour market is offered them. |
| 199 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. at 0 the month is today's (lines 221-260)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 37 | `static void sectionOne(Game a, Game b)` |  |

### 2 and 3. the horses (lines 261-356)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 86 | `static void sectionTwoAndThree(Game a, Game b)` | Turns the twins into a horse farm at share 1 and one at half, and plays the month. |
| 351 | 5 | `static long total(long[] posts)` |  |

### 4. steam takes the factory off the grid (lines 357-407)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 48 | `static void sectionFour(Game c)` |  |

### 5. a works that already burns coal (lines 408-435)

| line | len | member | says |
|---:|---:|---|---|
| 410 | 25 | `static void sectionFive()` |  |

### 6. the money adds up (lines 436-460)

| line | len | member | says |
|---:|---:|---|---|
| 438 | 22 | `static void sectionSix(Game c, Game untouched)` |  |

### 7 to 11. road freight's horse and wagon (lines 461-739)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 8 | **type** `static final class Month` | One month of a city's Haulage, as the sections read it, taken the moment the month was played. |
| 477 | 5 | `static Game cartTown(String label, double horses)` | HaulageCheck's city (mines and a pelletising plant that lands its coal and ships its pellets, trade both ways, the goods' literals) with Haulage's horse share set before the first month. |
| 483 | 3 | `static void step(Game g, int months)` |  |
| 488 | 38 | `static Month[] run(Game g, int months)` | Plays `months` months one at a time, and takes Haulage's figures after each (index 1 to months). |
| 527 | 12 | `static void sectionsSevenToEleven()` |  |
| 546 | 59 | `static void sectionSeven(Month[] z, Month[] hh, Month[] f)` |  |
| 606 | 1 | **type** `interface Pick` |  |
| 606 | 1 | `boolean of(Month m)` _(in Forms1900Check.Pick)_ |  |
| 609 | 4 | `static boolean allMonths(Month[] rows, Pick pick)` | Whether every month of a run (1 to its last) satisfies the test. |
| 614 | 58 | `static void sectionEight()` |  |
| 673 | 14 | `static void sectionNine(double founding, Month[] z, Month[] hh, Month[] f)` |  |
| 688 | 38 | `static void sectionTen(Game full, Month[] f)` |  |
| 727 | 12 | `static void sectionEleven(Month[] f)` |  |

### 12 to 15. the homes' lamp oil and coal (lines 740-994)

| line | len | member | says |
|---:|---:|---|---|
| 743 | 4 | `static double unitsSold(Sector s, Good g)` | The units of a good a sector sold to local buyers in its last month, as its output line has them (0 for no line). |
| 749 | 4 | `static double soldValue(Sector s, Good g)` | The money a sector's statement books for the sale of a good (0 for no line). |
| 755 | 4 | `static double boughtValue(Sector s, Good g)` | ...and for its purchase of a good, in all and from the world alone. |
| 760 | 4 | `static double boughtAbroad(Sector s, Good g)` |  |
| 766 | 3 | `static void same(String label, double actual, double expected)` | A figure against the model's, to the bit. |
| 771 | 7 | `static double fixedBills(Game g)` | What the cells of a city owe before they spend: the take-home less what is left after the fixed bills (rent, fees, interest, loans), summed over their households. |
| 780 | 12 | `static double homeFuelSettled(Game g)` | The home fuel bill the settled cells were handed: each row's part, by the people in the cells that settle (a cell under HouseholdBalance.EMPTY_CELL households settles nothing), as the balance splits a row's fees. |
| 793 | 57 | `static void sectionTwelve()` |  |
| 851 | 49 | `static void sectionThirteen()` |  |
| 901 | 72 | `static void sectionFourteen()` |  |
| 974 | 20 | `static void sectionFifteen()` |  |

