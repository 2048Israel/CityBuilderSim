# BasketYearCheck.java - 807 lines · 37 methods · 6 constants · harnesses

`ham/citybuildersim/BasketYearCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> The household's basket by year (R0e slice 1): the year's basket in a city founded the 1900 way, today's in every other. Not part of the game.
> 
> WHAT THIS HAS TO PROVE (R0e, 2026-11-07; docs/studies/research-1900.md 5.4 item 5, "1900's has more raw staples, lamp oil and coal, and no
> car", and section 4, "Home butter and bread: data, not a mechanism"). A 1900 household baked its bread from flour, lit its rooms with a lamp
> and heated them with a coal stove, and did not own a car; the basket reaches today's at the last knot, and only a city founded the 1900 way
> (Founding.isUnfitted()) reads any of it. The figures are the data's (consumption.json's years, sourced in R0e's notes), so none is written here: every
> one is read from the loaded model (years(), yearAt(), item(), homeMadeFrom()) or is a constant of the model (HomeFuel's two). What is checkable:
> 
>   1. THE DATA: the packaged file loads years, in ascending order, every share and cap in [0, 1], every bought key and every homeMadeFrom a
>      good of the file, bread and the bakery's goods made from grain, and the last knot today's (so nothing jumps at its January)
>   2. THE INTERPOLATION, on a synthetic file: the first knot before its January, a knot's own figures at its January, the mean half-way, null
>      from the last knot's January on, a good a knot does not name bought whole there; a malformed block leaves no years and the goods loaded
>   3. THE HOME-MADE BASKET, on the packaged model: a calorie-bound household holds today's calories in more grain and less bread, a good with
>      no home-made source done without; a money-bound one pays the same and holds more calories; with no year it is today's basket, to the bit
>   4. A DEFAULT CITY READS NONE OF IT: no year in any month, the homes' shares 0, no lamp oil or coal sold, its shops' basket today's
>   5. A 1900 CITY'S SHOPS STOCK THE YEAR'S BASKET: less bread and more grain than the same city's today's basket, and than a default twin's
>   6. LAMP OIL AND COAL BY YEAR: the homes' shares are the year's each month, the litres and tonnes sold the homes x share x the model's
>      per-home figure, and the households charged the bill the month after
>   7. NO CAR IN 1900: where a default city motorises, the 1900 city's fleet stays under the year's cap, and its ownership ceiling is at most it
>   8. THE HELD SALE CROSSES A SAVE: a 1900 city saved in a month that struck lamp oil and coal loads the same sale and pays the same bill
>   9. TODAY'S FROM THE LAST KNOT: from the last knot's January a 1900 city has no year, no share, no cap and today's basket
> 
> Every fixture CAUSES its condition: the cities are founded through newGame(Founding.withUnfitted(true)) as the player's door founds them,
> a default twin on the same village beside each, the car town is CarCheck's (900 houses, 60 roads) with a control whose cap is lifted by
> editing the packaged file's years and its households handed a car's price over their cushion before every month (FS17: the money for a
> car is the fixture's, not the played town's), and the synthetic files are written here. Figures are asserted against the model's own constants and
> methods; a harness city lives in GameFiles.scratch(), never in a slot of the player's.

**Uses:** [Consumption](Consumption.md) (60), [Good](Good.md) (44), [Game](Game.md) (22), [Founding](Founding.md) (7), [Forms1900Check](Forms1900Check.md) (7), [ArrivalMix](ArrivalMix.md) (7), [HomeFuel](HomeFuel.md) (6), [FittingCheck](FittingCheck.md) (3), [EquipmentCheck](EquipmentCheck.md) (2), [Household](Household.md) (2), [Retail](Retail.md) (2), [GameFiles](GameFiles.md) (2), [CityCalendar](CityCalendar.md) (1), [BuildingManager](BuildingManager.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

## Sections

| line | section |
|---:|---|
| 130 | the packaged file |
| 183 | 1. the data |
| 253 | 2. the interpolation |
| 358 | 3. the home-made basket |
| 476 | the shops' basket, worked here |
| 525 | 4. a default city reads none of it |
| 556 | 5. a 1900 city's shops |
| 586 | 6. lamp oil and coal by year |
| 636 | 7. no car in 1900 |
| 713 | 8. the held sale crosses a save |
| 773 | 9. today's from the last knot |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 104 | `BasketYearCheck.DEFAULT` | `Founding.defaults()` |  |
| 105 | `BasketYearCheck.THE_1900_WAY` | `Founding.defaults().withUnfitted(true)` |  |
| 108 | `BasketYearCheck.SETTLE` | `Forms1900Check.SETTLE` | The months a village is played before anything is asked of its shops and homes (Forms1900Check.SETTLE). |
| 256 | `BasketYearCheck.Y1` | `1910, Y2 = 1920, Y3 = 1930` | The synthetic file's three knots, a decade apart (the harness's own figures, not the packaged file's): lamp share, coal share, cars. |
| 257 | `BasketYearCheck.LAMP` | `{.8,.4, 0 }, COAL = {.6,.2, 0 }, CARS = { 0,.5, 1 }` |  |
| 259 | `BasketYearCheck.LOAF_1` | `.2, LOAF_2 =.6, FLOUR_2 =.4` | LOAF is named by the first two knots, FLOUR by the second alone; a knot that does not name a good buys it whole. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 56 | `static PrintStream out` |  |
| 57 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 755 | **type** `public class BasketYearCheck` | The household's basket by year (R0e slice 1): the year's basket in a city founded the 1900 way, today's in every other. |
| 59 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 64 | 1 | `static void check(String label, boolean ok)` |  |
| 67 | 4 | `static void near(String label, double actual, double expected, double rel)` | A figure against the harness's own, to a relative tolerance. |
| 73 | 3 | `static void same(String label, double actual, double expected)` | A figure against the model's, to the bit. |
| 77 | 5 | `static void quietly(Runnable r)` |  |
| 84 | 9 | `static List<String> printed(Runnable r)` | What a piece of code prints, as lines (the loader's one line in the log is part of what it promises). |
| 95 | 3 | `static void step(Game g, int months)` | One press of the clock, as the player's button makes it, quietly. |
| 100 | 3 | `static Game village(String label, Founding founding)` | A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()). |
| 110 | 19 | `public static void main(String[] args) throws Exception` |  |

### the packaged file (lines 130-182)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 7 | `static String packagedText()` | The packaged consumption.json as text: the file the game loads when no editable copy lies beside it. |
| 142 | 5 | `static Consumption packaged()` | The packaged file loaded into a model of its own. |
| 149 | 5 | `static Consumption model(String text)` | A model from a text; what the loader says is the caller's to catch (printed()) or to hear. |
| 156 | 5 | `static String packagedWithoutTheCap()` | The packaged file with every knot's cap on the cars lifted to 1 (no cap), as text: the control of section 7. |
| 163 | 5 | `static String packagedWithoutYears()` | The packaged file with no years block at all, as text: a city founded the 1900 way on it behaves as today (section 5's twin). |
| 170 | 12 | `static String packagedWithTheYearShort()` | The packaged file with the years cut to its first knot and the last, the last one year on, as text: the year runs out after twelve months (section 9). |

### 1. the data (lines 183-252)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 65 | `static void sectionOne()` |  |
| 251 | 1 | `static boolean inUnit(double v)` |  |

### 2. the interpolation (lines 253-357)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 5 | `static String good(String key, String made, double kcal, double price)` |  |
| 267 | 3 | `static String knot(int year, double lamp, double coal, double cars, String bought)` |  |
| 272 | 5 | `static String file(String years, String loafMadeFrom)` | A file of two goods, FLOUR and LOAF (made from flour, unless it names another), and the years block as given (null: none). |
| 278 | 5 | `static String synthetic()` |  |
| 284 | 73 | `static void sectionTwo()` |  |

### 3. the home-made basket (lines 358-475)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 12 | `static Map<String, Double> afterTheYear(Consumption c, Map<String, Double> today, Consumption.YearPoint y)` | A household's basket before the year's scale, in the null basket's units: what the year leaves bought, plus what the homes make of the raw goods. |
| 374 | 8 | `static double dot(Map<String, Double> basket, Consumption c, boolean calories)` |  |
| 383 | 86 | `static void sectionThree()` |  |
| 470 | 5 | `static boolean sameBasket(Map<String, Double> a, Map<String, Double> b)` |  |

### the shops' basket, worked here (lines 476-524)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 32 | `static Map<Good, Double> shopsBasket(Game g, Consumption.YearPoint year)` | The basket per head the shops stock, worked as CityBasket.perHead() works it, but in the year given (null: today's). |
| 512 | 5 | `static boolean sameShelf(Map<Good, Double> a, Map<Good, Double> b)` |  |
| 519 | 5 | `static double kgShare(Map<Good, Double> basket, Good good)` | A good's share of the basket's kilograms. |

### 4. a default city reads none of it (lines 525-555)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 28 | `static void sectionFour()` |  |

### 5. a 1900 city's shops (lines 556-585)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 27 | `static void sectionFive()` |  |

### 6. lamp oil and coal by year (lines 586-635)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 47 | `static void sectionSix()` |  |

### 7. no car in 1900 (lines 636-712)

| line | len | member | says |
|---:|---:|---|---|
| 642 | 27 | `static Game carTown(String label, Founding founding, String consumption, int months, double[] ceilings)` | CarCheck's town (900 houses, 60 roads, shops, power, water, depots) founded as given, the consumption file replaced if one is given, then played, its households handed a car's price over their cushion before every mon... |
| 679 | 8 | `static void affordable(Game g)` | FS17: THE MONEY FOR A CAR, CAUSED. |
| 688 | 24 | `static void sectionSeven()` |  |

### 8. the held sale crosses a save (lines 713-772)

| line | len | member | says |
|---:|---:|---|---|
| 715 | 57 | `static void sectionEight() throws Exception` |  |

### 9. today's from the last knot (lines 773-807)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 32 | `static void sectionNine()` |  |

