# Consumption.java - 763 lines · 35 methods · 7 constants · model

`ham/citybuildersim/Consumption.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> What a household eats, and what changes it.
> 
> ONE PLACE, AND NOBODY KEEPS A COPY. Jerus: "basically its just where other
> files get there numbers from". Every figure a grocery basket is built out of
> is in consumption.json and every question about one is answered here. The
> rule this is meant to end is the one HistoryScreen.historyValues() broke on
> 2026-09-15, when a second copy of the unemployment formula sat beside the
> model's and rotted for nine days without anybody noticing.
> 
> ==========================================================================
> THE TWO LAWS THESE NUMBERS ARE SHAPED TO OBEY
> ==========================================================================
> 
> ENGEL'S LAW, which is the most robust regularity in economics: as income
> rises the SHARE of it spent on food falls, while the AMOUNT rises. Both
> halves matter. A model with only the first makes rich cities starve and a
> model with only the second makes them spend everything on groceries.
> 
> BENNETT'S LAW, the second most robust: as income rises the share of CALORIES
> from starchy staples falls and the share from meat, dairy and produce rises.
> That is the whole of the "healthy or unhealthy" question - it is not a
> separate dial, it is what income does, and it is why GRAINS carries a
> NEGATIVE elasticity. A staple is an inferior good: a household eats less
> rice as it gets richer, not more.
> 
> AND A THIRD AXIS THAT IS NOT INCOME AT ALL. Convenience food is bought for
> TIME, not for money - a working parent buys it at any income, and an
> out-of-work parent with the same children and the same money does not,
> because what they are short of is money and what they have is afternoons.
> Folding that into income would make a poor household that cooks and a
> time-poor household that does not into the same household, and they are
> different cities with different health.
> 
> ==========================================================================
> WHY INCOME IS MEASURED IN MULTIPLES OF SUBSISTENCE
> ==========================================================================
> 
> Engel's curve is a logarithm of income, and a logarithm of a MONEY figure is
> a money constant wearing a function's clothes: lop two zeroes off the
> currency and every household in the city would appear to fall to a tenth of
> its real income and start eating like the destitute. This project has found
> twenty bugs of that family and seeded four constants against it.
> 
> So income is divided by what bare subsistence COSTS - both are money, the
> ratio is a pure number, and a currency reform moves neither. It needs no
> seeding, it cannot be missed by a redenominate(), and it means something a
> player can say out loud: "this household can afford four times the calories
> it needs to survive".
> 
> ==========================================================================
> NOTHING EATS THIS YET
> ==========================================================================
> 
> Deliberate, and Jerus's call. The model, the data and the harness are real
> and tested; no household consumes any of it. FOOD remains the good that
> drives hunger, subsistence and the shopping plan, exactly as it did. What
> this batch buys is the SHADOW BASKET - what every cell in a played city
> WOULD buy - measured against what it spends today, which is the evidence
> that decides whether turning it on is a realism change or a balance change.
> Hunger feeds sickness feeds mortality, so the cost of guessing that wrong is
> ... (1 more lines in the source)

**Uses:** [ArrivalMix](ArrivalMix.md) (4), [PriceLevel](PriceLevel.md) (2), [FamilyStructure](FamilyStructure.md) (1)

**Used by (8):** [BasketYearCheck](BasketYearCheck.md), [CityBasket](CityBasket.md), [ConsumptionCheck](ConsumptionCheck.md), [ForeignCheck](ForeignCheck.md), [Game](Game.md), [Motoring](Motoring.md), [PriceLevelCheck](PriceLevelCheck.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 87 | ENGEL'S CURVE, AND WHY IT HAS NO FLOOR IN IT |
| 160 | · the goods |
| 248 | · loading |
| 485 | · what it costs |
| 549 | · the basket |
| 643 | · reading a basket |
| 744 | · who a household is |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `Consumption.FILE_NAME` | `"consumption.json"` |  |
| 117 | `Consumption.ENGEL_A` | `1.0` | The share at subsistence: all of it. |
| 120 | `Consumption.ENGEL_B` | `(ENGEL_A -.24) / Math.log(90)` | How fast the share falls with log income. |
| 127 | `Consumption.SATIATION` | `1.15` | How much past need a household eats when money has stopped being the constraint. |
| 144 | `Consumption.ENGEL_MIN_SHARE` | `ENGEL_B` | Where the curve stops being a description of anything, DERIVED. |
| 155 | `Consumption.TIME_WEIGHT` | `.40` | How hard the time axis pushes, per dependant an earner carries. |
| 158 | `Consumption.KCAL_A_MONTH` | `2_000 * 30` | What one person needs in a month, in calories. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 207 | `private final Map<String, Item> byKey` |  |
| 208 | `private final Map<String, Double> targetShares` |  |
| 209 | `private final List<Item> all` |  |
| 211 | `private final Map<String, String> homeMade` | Each good's homeMadeFrom good by key, only for the goods that name one the file holds (see parse()). |
| 213 | `private int[] homeMadeIndex` | For each good in `all` the index of its homeMadeFrom good, or -1: homeMade turned into positions once, so basket() scans nothing. |
| 214 | `private final List<Year> years` |  |
| 215 | `private double referenceIncomeMultiple` |  |
| 216 | `private String source` |  |
| 217 | `private double meanConvenience` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 681 | **type** `public class Consumption` | What a household eats, and what changes it. |

### ENGEL'S CURVE, AND WHY IT HAS NO FLOOR IN IT (lines 87-159)

### the goods (lines 160-247)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 4 | **type** `public record Item(String key, String label, String unit, String category, double worldImportPrice, double ...` | One row of the file. |
| 183 | 6 | **type** `public record Year(int year, double lampShare, double coalShare, double carsPerHousehold, Map<String, Doubl...` | One knot of the household's basket by year, at January of its year: what a city founded the 1900 way (Founding.isUnfitted()) holds then. |
| 185 | 3 | `{ ... }` _(in Consumption.Year)_ |  |
| 195 | 11 | **type** `public record YearPoint(double lampShare, double coalShare, double carCeiling, Map<String, Double> bought)` | The figures of one game month, interpolated between two knots (Consumption.yearAt): the shares of the homes on lamp oil and coal, the ceiling the year puts on the households' cars, and the share of each good a househo... |
| 196 | 3 | `{ ... }` _(in Consumption.YearPoint)_ |  |
| 201 | 4 | `public double bought(String key)` _(in Consumption.YearPoint)_ | The share of this good a household buys ready made: the map's, or 1 (bought whole) for a good it does not name. |
| 219 | 1 | `public String getSource()` |  |
| 220 | 1 | `public List<Item> items()` |  |
| 221 | 1 | `public Item item(String key)` |  |
| 222 | 1 | `public boolean isLoaded()` |  |
| 223 | 1 | `public double referenceMultiple()` |  |
| 229 | 1 | `public List<Year> years()` | The household's basket by year, in year order, empty when the file has no years block (or a malformed one, which parse() drops). |
| 236 | 1 | `public String homeMadeFrom(String key)` | The good a good is made from at home when a household does not buy it ready made, by key; null when it names none, or names a good the file does not hold, or itself. |
| 239 | 1 | `public Map<String, Double> targetShares()` | The calorie share each category should carry in a balanced diet. |
| 242 | 5 | `public List<String> categories()` | Every category in the file, in the order the goods first mention them. |

### loading (lines 248-484)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 28 | `public boolean load()` | An editable copy beside the game first, the packaged one second - the same two steps and the same order as buildings.json, because a modder who can retune the buildings should be able to retune the diet. |
| 289 | 11 | `public boolean loadFrom(Reader reader)` | Loads from anywhere, for a harness that needs to ask what the model does with DIFFERENT numbers - the currency-reform test below all others, which cannot be written at all if the only way in is a file on disk. |
| 301 | 51 | `private boolean parse(Reader reader)` |  |
| 357 | 21 | `private void readHomeMade(Map<String, String> declared)` | Keeps the homeMadeFrom lines that name a good the file holds and not the good itself; any other is ignored with a line in the log (a data file must not stop a game). |
| 385 | 38 | `private void readYears(JsonObject root)` | Reads the optional "years" block into years. |
| 424 | 7 | `private static double yearNumber(JsonObject o, String name)` |  |
| 432 | 5 | `private static double yearShare(JsonObject o, String name, int year)` |  |
| 438 | 4 | `private static String yearText(JsonObject o, String name, String otherwise)` |  |
| 452 | 28 | `public YearPoint yearAt(int gameMonth)` | The household's basket of a game month in a city founded the 1900 way: null when the file has no years, and null from the LAST knot's January on, where the basket is today's model and nothing jumps. |
| 481 | 3 | `private static YearPoint pointOf(Year y)` |  |

### what it costs (lines 485-548)

| line | len | member | says |
|---:|---:|---|---|
| 494 | 8 | `public double cheapestCalorie()` | The cheapest calorie in the file, per kcal. |
| 519 | 3 | `public double subsistenceCostAtWorldPrices()` | What a month of bare survival costs one person, IN THE WORLD'S MONEY. |
| 528 | 3 | `public double subsistenceCost(double exchangeRate)` | What a month of bare survival costs one person, in THIS city's money. |
| 544 | 4 | `public double incomeMultiple(double incomePerHead, double exchangeRate)` | Where a household sits on Engel's curve. |

### the basket (lines 549-642)

| line | len | member | says |
|---:|---:|---|---|
| 556 | 5 | `public static double foodShare(double incomeMultiple)` | How much of its income a household spends on food. |
| 576 | 3 | `public Map<String, Double> basket(double incomeMultiple, double timePressure)` | What one person buys in a month, by key, in the file's units. |
| 594 | 48 | `public Map<String, Double> basket(double incomeMultiple, double timePressure, YearPoint year)` | The same basket in the year of a city founded the 1900 way: what one person buys in a month when part of what today's household buys ready made was made at home. |

### reading a basket (lines 643-743)

| line | len | member | says |
|---:|---:|---|---|
| 645 | 8 | `public double costOf(Map<String, Double> basket)` |  |
| 654 | 8 | `public double caloriesOf(Map<String, Double> basket)` |  |
| 672 | 11 | `public double itemQualityOf(Map<String, Double> basket)` | The average healthiness of what is in a basket, weighted by calories. |
| 695 | 14 | `public double dietQuality(Map<String, Double> basket)` | How close a basket is to a balanced diet, 1 for on target and 0 for nothing in common with it. |
| 711 | 11 | `public double calorieShareOf(Map<String, Double> basket, String category)` | The share of a basket's calories that comes from one category. |
| 733 | 10 | `public Map<String, Double> valueShares(Map<String, Double> basket)` | What a basket is worth to each good, as shares of one. |

### who a household is (lines 744-763)

| line | len | member | says |
|---:|---:|---|---|
| 747 | 1 | `public Item byKey(String key)` | One good by its key, or null if the file has no such line. |
| 759 | 4 | `public static double timePressure(FamilyStructure shape)` | Dependants per adult, for a household that works - and zero for one that does not, which is the whole point of the time axis. |

