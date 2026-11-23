# UtilitiesHandler.java - 1,337 lines · 122 methods · 9 constants · model

`ham/citybuildersim/UtilitiesHandler.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [PowerDispatch](PowerDispatch.md) (55), [Currency](Currency.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (9), [Substations](Substations.md) (7), [Good](Good.md) (5), [PriceLevel](PriceLevel.md) (3), [CityNeeds](CityNeeds.md) (2)

**Used by (46):** [AdviceValueOracle](AdviceValueOracle.md), [AutoBuildCheck](AutoBuildCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CementConcreteCheck](CementConcreteCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [DataSave](DataSave.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HydroCheck](HydroCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [LandManager](LandManager.md), [LicenceFees](LicenceFees.md), [LongPlaytest](LongPlaytest.md), [MaterialsCheck](MaterialsCheck.md), [MoneyAudit](MoneyAudit.md), [NewUnitCheck](NewUnitCheck.md), [OrderDoorCheck](OrderDoorCheck.md), [PowerDispatchCheck](PowerDispatchCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PowerView](PowerView.md), [PowerViewCheck](PowerViewCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [ResearchCheck](ResearchCheck.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SubstationCheck](SubstationCheck.md), [Substations](Substations.md), [SummaryScreen](SummaryScreen.md), [WaterCheck](WaterCheck.md), [WaterPowerCheck](WaterPowerCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 38 | WATER SUPPLY |
| 102 | THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) |
| 192 | · READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is |
| 280 | · Split books. The two utilities share one workforce and one fill rate, but |
| 340 | THE POWER THE WORKS MAKE (0.7.112, batch I2; |
| 476 | THE MERIT ORDER (PW1, 2026-11-11; spec-electricity 2.2, 2.6; Jerus's Q1: "Market price") |
| 534 | · THE WATER'S POWER (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price") |
| 1204 | · · ELECTRIC POWER |
| 1235 | · · WATER |
| 1276 | · · CONSOLIDATED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `UtilitiesHandler.BASE_WATER_SUPPLY` | `8000` | What the city can draw before it builds anything: the legacy wells and the old municipal intake. |
| 58 | `UtilitiesHandler.BASE_GRID_KW` | `10000` | The kilowatts the grid starts with, before a power plant is built (setWattsProduction()). |
| 86 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 135 | `UtilitiesHandler.FRESH_UNITS_PER_KM2` | `121_600` | Units of fresh water a month one square kilometre of owned lake or river yields: the world's renewable river runoff (about 42,700 km3 a year, Shiklomanov) over the world's river and stream area (773,000 km2, Allen & P... |
| 501 | `UtilitiesHandler.FOUNDING_TARIFF` | `.01 * PriceLevel.FACTOR` | The founding tariff a kW a month, D$10 at the founding's level, held by the struck book until a month has cleared (the tariff the game had before PW1). |
| 503 | `UtilitiesHandler.WORKS_ROW` | `"works", GRID_ROW = "grid"` |  |
| 561 | `UtilitiesHandler.SEA_MARGIN_SHARE` | `.5` | The share of the sea works' output at the fill the sea must deliver to be the water's margin alone: at it or past it the sea's water pays their power, under it their power is spread over the water served, over no more... |
| 612 | `UtilitiesHandler.WORKS_ID` | `- 1, GRID_ID = - 2, FIXTURE_ID = - 3` | Reserved aggregate row ids; catalogue rows keep their permanent nonnegative ids. |
| 1303 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `public double production` |  |
| 34 | `public double baseProduction` | The kilowatts on site, plants and the founding grid. |
| 35 | `public double consumption` |  |
| 36 | `public double energyRatio` |  |
| 70 | `private long foundingEndowments` | How many founding endowments the city holds (FW9, 2026-10-25; BuildingManager.getFoundingEndowments()): 1 for a founded city. |
| 100 | `private double pricePerWaterUnit` | $50 per unit, i.e. $5 per 1,000 gallons. |
| 137 | `public double waterProduction` |  |
| 138 | `public double baseWaterProduction` |  |
| 141 | `private double freshNameplate, desalNameplate` | The fresh plants' nameplate and the desalination plants', standing - the two parts of baseWaterProduction past the wells (0.7.59). |
| 144 | `private double freshCap` | What the city's fresh water yields a month, the rights included; infinite until the city's land says otherwise (setFreshCap()). |
| 147 | `private double freshDrawn, desalOutput` | This month's fresh water treated - the fresh plants at their staffing, held to the cap - and the sea desalinated. |
| 148 | `public double buildingWaterDraw` |  |
| 149 | `public double residentWaterDraw` |  |
| 156 | `public double billedWaterDraw` | The slice of demand that is actually invoiced: commercial and industrial buildings. |
| 157 | `public double waterConsumption` |  |
| 158 | `public double waterRatio` |  |
| 161 | `private double[] utilityWages` | jobs |
| 162 | `private long[] utilityJobs` |  |
| 166 | `private double[] electricityWages` | Same payroll, split by which utility the job belongs to, so the report can show two businesses rather than one lump. |
| 167 | `private double[] waterWages` |  |
| 168 | `private double[] fillRate` |  |
| 170 | `private double averageUtilityFill` |  |
| 324 | `private boolean billedByStatement` | WHAT THE CUSTOMERS WERE ACTUALLY CHARGED, since 2026-09-06. |
| 325 | `private double billedElectricityRevenue` |  |
| 326 | `private double billedWaterRevenue` |  |
| 360 | `private double worksPower` | kW the works make past their own draws, standing: added to the grid's generation. |
| 363 | `private double billedPowerBought` | What the works' statements booked as power sold to the grid this month: what the utility pays them. |
| 380 | `public double billedElectricityDraw` | What the four charged categories draw. |
| 400 | `private double homesElectricityDraw, homesWaterDraw` | What unbilled homes draw, power and water: residential buildings not already invoiced to a sector. |
| 506 | `private double voll` | What a short half-block clears at, and what the long and the street wires charge a kWh delivered: city money, struck by seedConstants(). |
| 509 | `private Map<BuildingsTemplate, Long> plants` | The power plants standing and how many of each (ServicesManager); null until handed in. |
| 512 | `private ToDoubleFunction<Good> fuelPriceSource` | Where the month's fuel price, the exchange rate and the calendar month come from; null for a bare fixture (the world's price, at par, no season). |
| 513 | `private DoubleSupplier exchangeRateSource` |  |
| 514 | `private IntSupplier calendarMonth` |  |
| 517 | `private PowerDispatch.Month latest` | The month last dispatched, and the rows it ran. |
| 518 | `private List<PowerDispatch.Row> latestRows` |  |
| 521 | `private IntSupplier observationMonth` | Observation sources only: the absolute dispatch month and the existing build advice's firm supply. |
| 522 | `private DoubleSupplier firmSupply` |  |
| 523 | `private DispatchRecord latestRecord` |  |
| 524 | `private int paidDispatchMonth` |  |
| 525 | `private int fuelPaidMonth` |  |
| 526 | `private PowerDispatch.Month pendingLoadedMonth` |  |
| 527 | `private DispatchRecord pendingLoadedRecord` |  |
| 528 | `private Supplier<Substations.Costs> gridCostSource` |  |
| 529 | `private Substations.Costs latestGridCosts, pendingLoadedGridCosts` |  |
| 564 | `private double freshWorksDraw, seaWorksDraw` | kW the fresh works and the sea's works draw from the grid, standing (ServicesManager, the WATER category's power bought). |
| 579 | `private WaterDraw latestWater, pendingLoadedWater` | The update last made, what the month's water is priced on, and the one a load brings back (restorePendingPower()). |
| 582 | `private double waterPowerPaid` | What the water's books paid the power's for the works' power this month, city money (setWaterPowerPaid()). |
| 634 | `private Book struck` | The month last struck: the founding tariff until one is. |
| 637 | `private double fuelLocal, fuelImported, upkeepPaid` | The fuel the plants burned and the upkeep they paid, in city money: at home, abroad, and the upkeep (set by setFuelPaid()). |
| 640 | `private Map<String, Double> fuelImportedByGood` | ...and the imported part good by good, for the Trade tab (Sectors.tradeByGood(); its sum is fuelImported). |
| 679 | `public double ratio, energyPrice` | The month's ratio served, and its energy price a kWh delivered. |
| 681 | `public double commercePrice, industryPrice, worksPrice` | City money a kW a month: commerce and light industry (the energy and all the wires), Heavy Industry and Mining (the energy and transmission), and the works' surplus. |
| 683 | `public double[] price` | Per half-block: the price a kWh, the load, and what was left unserved, kW. |
| 685 | `public double askedKwh, servedKwh, curtailedKwh, fuelledKwh, windKwh, hydroKwh, worksKwh, gridKwh` | The month's kWh asked, served and curtailed, and made by the fuelled plants, the wind, the hydro, the works and the founding grid. |
| 687 | `public double upkeepWorld` | The variable upkeep the plants ran up, thousands of the WORLD's money. |
| 689 | `public LinkedHashMap<String, Double> fuel` | Fuel burned: good name to units. |
| 691 | `public boolean cleared` | True once a month has been struck into this book; false in the founding book, for the screens to say "still at founding". |
| 693 | `public DispatchRecord dispatch` | Optional observations: absent in older saves, never back-filled from the ending fleet. |
| 694 | `public int struckMonth` |  |
| 695 | `public double transmissionPrice, distributionPrice` |  |
| 697 | `public Substations.Costs gridCosts` | The grid costs captured with this dispatch; paid by existing city flows, recovered in the wires' tariff. |
| 843 | `public Book struck` |  |
| 844 | `public double fuelLocal, fuelImported, upkeepPaid` |  |
| 845 | `public Map<String, Double> fuelImportedByGood` |  |
| 846 | `public int paidDispatchMonth` |  |
| 847 | `public int fuelPaidMonth` |  |
| 849 | `public PowerDispatch.Month pendingMonth` | The completed closing dispatch, still awaiting the next strike; optional in older saves. |
| 850 | `public DispatchRecord pendingDispatch` |  |
| 851 | `public Substations.Costs pendingGridCosts` |  |
| 853 | `public double waterPowerPaid` | What the water's books paid the power's this month for the works' power, and the update the next strike prices it on (PW1-W); absent in older saves. |
| 854 | `public WaterDraw pendingWater` |  |
| 871 | `private boolean seedOnLoad` | True from a load whose save carried no struck book until seedLoadedRatio() has given the founding book the ratio the load computed. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 23 | 1315 | **type** `public class UtilitiesHandler` |  |

### WATER SUPPLY (lines 38-101)

| line | len | member | says |
|---:|---:|---|---|
| 72 | 1 | `public void setFoundingEndowments(long endowments)` |  |
| 74 | 1 | `private double baseGrid()` |  |
| 75 | 1 | `public double getFoundingGridKw()` |  |
| 76 | 1 | `private double baseWater()` |  |

### THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) (lines 102-191)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 3 | `public UtilitiesHandler()` |  |
| 177 | 4 | `public void updateUtilitiesHandler()` | updaters |
| 183 | 3 | `public double getEnergyRatio()` | getters |
| 188 | 3 | `public double getPricerPerWatt()` | What commerce and light industry pay a kW a month: the struck book's commerce price (THE MERIT ORDER, PW1); the founding tariff until a month is struck. |

### READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is (lines 192-279)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 1 | `public double getProduction()` |  |
| 198 | 1 | `public double getBaseProduction()` |  |
| 199 | 1 | `public double getConsumption()` |  |
| 200 | 1 | `public double getAverageUtilityFill()` |  |
| 202 | 1 | `public double getWaterProduction()` |  |
| 203 | 1 | `public double getBaseWaterProduction()` |  |
| 204 | 1 | `public double getWaterConsumption()` |  |
| 205 | 1 | `public double getBuildingWaterDraw()` |  |
| 206 | 1 | `public double getResidentWaterDraw()` |  |
| 207 | 1 | `public double getBilledWaterDraw()` |  |
| 208 | 1 | `public double getUnbilledWaterDraw()` |  |
| 209 | 1 | `public double getWaterRatio()` |  |
| 212 | 1 | `public double getFreshCap()` | The fresh water limit this month: FRESH_UNITS_PER_KM2 x the owned lakes and river, plus the rights (0.7.59); infinite when no land was given. |
| 214 | 1 | `public double getFreshNameplate()` | The Water Treatment Plants' nameplate, standing. |
| 216 | 1 | `public double getFreshDrawn()` | The fresh water treated this month: the fresh plants at their staffing, held to the cap. |
| 218 | 1 | `public double getDesalNameplate()` | The Desalination Plants' nameplate, standing. |
| 220 | 1 | `public double getDesalOutput()` | The sea desalinated this month: their nameplate at the staffing. |
| 223 | 3 | `public boolean isFreshCapped()` | Whether the fresh water limit holds the plants back this month: what they would treat at their staffing is more than the cap. |
| 228 | 4 | `public double getFreshIdleShare()` | The share of what the fresh plants would treat at their staffing that the limit idles, 0 to 1: the Services page's "62% of the plants' nameplate idle". |
| 234 | 3 | `public double getFreshHeadroom()` | The fresh water a plant more would add at the staffing: what is left under the cap, never below nothing. |
| 239 | 3 | `public double getWaterAtFullStaff()` | What the works would produce fully staffed, held to the fresh water limit: the Services page's "at full staff" (0.7.59; baseWaterProduction until then). |
| 253 | 3 | `public static double waterOutput(double fresh, double desal, double fill, double cap)` | The water the works produce at a staffing, by the rule above, from its parts: the nameplate and the wells times the fill, unless the fresh plants' share would pass the cap, when the wells and the sea are times the fil... |
| 258 | 6 | `public static double waterOutput(double fresh, double desal, double fill, double cap, long endowments)` | ...for a city holding that many founding endowments (FW9): the wells are one endowment's, times them. |
| 265 | 1 | `public double getPricePerWaterUnit()` | What a unit of water is billed at: the tariff and the struck book's water power a unit delivered (PW1-W); the tariff alone until a month strikes one. |
| 267 | 1 | `public double getWaterTariff()` | The water's tariff before its works' power: main's constant, $50 a unit at the founding's level. |
| 276 | 1 | `public double getPowerServed()` | SERVED (0.7.41): what the grid generates over what the city asks of it, unclamped - energyRatio's own fraction without its cap at 1, so past 100% it keeps counting the headroom - CityNeeds.servedShare(): +∞ with nothi... |
| 278 | 1 | `public double getWaterServed()` | ...and what the water works treat over what the city asks of them, waterRatio's fraction unclamped. |

### Split books. The two utilities share one workforce and one fill rate, but (lines 280-339)

| line | len | member | says |
|---:|---:|---|---|
| 306 | 4 | `public double getElectricityRevenue()` | The month's electricity bill - only the draw somebody is actually invoiced for, and only the fraction delivered. |
| 328 | 5 | `public void setBilledRevenue(double electricity, double water)` |  |
| 335 | 4 | `public void setBilledRevenue(double electricity, double water, double powerBought)` | ...and what the works sold it of their own power, as their statements booked it (0.7.112; THE POWER THE WORKS MAKE, below). |

### THE POWER THE WORKS MAKE (0.7.112, batch I2; (lines 340-475)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 3 | `public void setWorksPower(double kw)` | The surplus the works make past their own draws, kW (ServicesManager, from the buildings standing). |
| 371 | 1 | `public double getWorksPower()` | kW the works sell the grid: added to what it generates. |
| 374 | 4 | `public double getPowerBought()` | What the utility pays the works for their power this month: the statements' figure, or with none handed in the surplus at the tariff. |
| 382 | 3 | `public void setBilledElectricityDraw(double draw)` |  |
| 385 | 1 | `public double getBilledElectricityDraw()` |  |
| 386 | 3 | `public double getUnbilledElectricityDraw()` |  |
| 402 | 4 | `public void setHomesDraw(double electricity, double water)` |  |
| 406 | 1 | `public double getHomesElectricityDraw()` |  |
| 407 | 1 | `public double getHomesWaterDraw()` |  |
| 415 | 4 | `public double getWaterRevenue()` | Only the billed slice, and only the fraction actually delivered - during rationing customers receive waterRatio of what they asked for and are charged for that, which is also exactly what the commercial and industrial... |
| 428 | 3 | `public double getElectricityPayroll()` | PER JOB TYPE since 0.7.17, as every employer's payroll is: each type's wage for the posts of that type filled, which is what the households in them are paid. |
| 432 | 3 | `public double getWaterPayroll()` |  |
| 437 | 6 | `private double staffed(double[] bill)` | A wage bill by job type, at each type's own fill. |
| 444 | 11 | `public double getElectricityIncome()` |  |
| 456 | 5 | `public double getWaterIncome()` |  |
| 462 | 5 | `private static double sum(double[] a)` |  |
| 468 | 3 | `public double getUtilityPayroll()` |  |
| 472 | 3 | `public double getUtilityRevenue()` |  |

### THE MERIT ORDER (PW1, 2026-11-11; spec-electricity 2.2, 2.6; Jerus's Q1: "Market price") (lines 476-533)

| line | len | member | says |
|---:|---:|---|---|
| 532 | 1 | `public void setGridCostSource(Supplier<Substations.Costs> source)` | Real city grid costs; a bare dispatch fixture without a city keeps its unconstrained wires. |

### THE WATER'S POWER (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price") (lines 534-1337)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 7 | **type** `public record WaterDraw(double freshKw, double seaKw, double landDelivered, double seaDelivered, double sea...` | One update's water works: their draw (kW), the water each kind delivered (units), the land's (the founding wells' and the fresh works') first, then the sea's, and the sea works' output at the fill (units). |
| 572 | 4 | `double marginalKwPerUnit()` _(in UtilitiesHandler.WaterDraw)_ | The sea works' draw a unit while the sea delivers (THE WATER'S POWER); 0 while the land's water serves, its tariff paying its works' power. |
| 584 | 4 | `public void setWaterWorksDraw(double freshKw, double seaKw)` |  |
| 589 | 1 | `public double getWaterWorksDraw()` |  |
| 592 | 1 | `public WaterDraw getLatestWaterDraw()` | The last update's water works; null before the first update. |
| 595 | 1 | `public double getWaterPowerPerUnit()` | The water's power a unit: the sea works' draw a unit while the sea delivers, at the struck commerce price; 0 while the land's water serves and before any update. |
| 598 | 1 | `public double getWaterPowerBill()` | All the water works' power at the struck commerce price, what the water's books owe the power's for the month of the last update. |
| 601 | 1 | `public void setWaterPowerPaid(double paid)` | What the water's books pay the power's this month for the works' power (Game.buyPowerFuel(), the month just ended's bill, before the next strike). |
| 603 | 1 | `public double getWaterPowerPaid()` |  |
| 606 | 4 | `public void setPowerObservationSources(IntSupplier month, DoubleSupplier firm)` |  |
| 615 | 9 | **type** `public record RowRecord(int id, int sourceIndex, PowerDispatch.Kind kind, Good fuel, double offeredKw, doub...` | One catalogue row's actual run and clearing receipts, not its profit or its fuel purchase bill. |
| 618 | 1 | `{ ... }` _(in UtilitiesHandler.RowRecord)_ |  |
| 619 | 4 | `RowRecord scaled(double scale)` _(in UtilitiesHandler.RowRecord)_ |  |
| 626 | 6 | **type** `public record DispatchRecord(int month, double firmKw, List<RowRecord> rows)` | Immutable bounded aggregates, captured at dispatch, before another month can change fleet, fuel or prices. |
| 627 | 1 | `{ ... }` _(in UtilitiesHandler.DispatchRecord)_ |  |
| 628 | 3 | `DispatchRecord scaled(double scale)` _(in UtilitiesHandler.DispatchRecord)_ |  |
| 643 | 3 | `public void setPlants(Map<BuildingsTemplate, Long> standing)` | The power plants standing and how many of each. |
| 648 | 1 | `public void setFuelPrice(ToDoubleFunction<Good> cityPrice)` | The city's price of a unit of a fuel; null: the world's price (which already carries PriceLevel.FACTOR). |
| 651 | 1 | `public void setExchangeRate(DoubleSupplier rate)` | City money per world thousand of the founding's dollars (Markets.getExchangeRate(): city money a dollar times the world's price level, the D$ the goods are struck at); null: 1, at par. |
| 654 | 1 | `public void setCalendarMonth(IntSupplier month)` | The calendar month, 1 to 12, for the season; null: no season. |
| 656 | 3 | `private double fuelPrice(Good g)` |  |
| 660 | 3 | `private double exchangeRate()` |  |
| 665 | 6 | `public void seedConstants(double unit)` | Re-seeds the money constants at a given unit: the dial over the unit, as Bank.seedConstants() does (the later slice calls it from Game.restrikeMoneyConstants()). |
| 672 | 1 | `public PowerDispatch.Month getLatestMonth()` |  |
| 674 | 1 | `public List<PowerDispatch.Row> getLatestRows()` |  |
| 677 | 46 | **type** `public static final class Book` | A month's price and what it was made of: what the bills and the screens read. |
| 700 | 6 | `public static Book founding()` _(in UtilitiesHandler.Book)_ | The book before any month has cleared: the founding tariff, ratio 1, nothing made. |
| 707 | 15 | `Book copy()` _(in UtilitiesHandler.Book)_ |  |
| 724 | 7 | `private static void scale(Book b, double scale)` |  |
| 733 | 36 | `public void strikePower()` | Strikes the month last dispatched into the book: a new object each time, sharing nothing with the dispatch. |
| 770 | 1 | `public Book getStruckBook()` |  |
| 773 | 1 | `public boolean isFoundingBook()` | True while no month has been struck: the prices are the founding tariff (the book's own flag, so a load keeps it). |
| 776 | 1 | `public double getEnergyPricePerMwh()` | What the month's energy cleared at, city money (thousands) a MWh delivered. |
| 779 | 1 | `public double getFuelBurned(Good g)` | Units of a good the plants burned in the month last struck (tonnes, for coal). |
| 782 | 1 | `public double getCurtailedKwh()` | kWh of the wind and the works' surplus turned away in the month last struck, for want of a load. |
| 785 | 5 | `public double getUnservedKwh()` | kWh of the load left unserved in the month last struck, summed over its six half-blocks. |
| 792 | 5 | `public int getHalfBlocksShort()` | How many of the month's six half-blocks left some load unserved. |
| 799 | 1 | `public double getPricePerWatt()` | What commerce and light industry pay a kW a month (the struck commerce price). |
| 802 | 1 | `public double getIndustryPricePerWatt()` | What Heavy Industry and Mining pay a kW a month (the energy and the long wires). |
| 805 | 1 | `public double getWorksPricePerWatt()` | What the utility pays the works a kW of surplus a month. |
| 811 | 3 | `public void setFuelPaid(double local, double imported, double upkeep)` | The fuel the plants burned this month and the upkeep they ran up, in city money: at home, abroad, and the upkeep (a later slice draws them from the market at the top of the month). |
| 816 | 14 | `public void setFuelPaid(double local, double imported, double upkeep, Map<String, Double> importedByGood)` | ...with the imported part good by good (Game.buyPowerFuel()). |
| 832 | 1 | `public Map<String, Double> getFuelImportedByGood()` | The fuel bought from the world this month, good by good (the Good's name), in city money. |
| 834 | 1 | `public double getFuelLocal()` |  |
| 835 | 1 | `public double getFuelImported()` |  |
| 836 | 1 | `public double getUpkeepPaid()` |  |
| 837 | 1 | `public double getFuelBill()` |  |
| 838 | 1 | `public int getPaidDispatchMonth()` |  |
| 839 | 1 | `public int getFuelPaidMonth()` |  |
| 842 | 14 | **type** `public static final class PowerState` | What the save keeps of the merit order: the struck book and the fuel paid (a later slice wires it to DataSave). |
| 857 | 12 | `public PowerState toPowerState()` |  |
| 879 | 5 | `public void seedLoadedRatio()` | A save from before PW1 carries no book (REV-PW1c): it loads the founding book, and here, on the load path after updateServices() has dispatched the loaded city, that book takes the ratio just computed, the one the loa... |
| 886 | 27 | `public void restorePendingPower()` | Restores the completed closing flow after the load's LAST derived-services rebuild, for the next strike. |
| 915 | 13 | `public void restorePowerState(PowerState s)` | Restores a save's book and fuel paid; a save with none (null, or no book) leaves the founding book. |
| 930 | 3 | `public void setWattsProduction(double watts)` | setters |
| 934 | 4 | `public void setWattsConsumption(double watts)` |  |
| 939 | 3 | `public void setWaterProduction(double water)` |  |
| 944 | 4 | `public void setWaterSources(double fresh, double desal)` | The two parts of that nameplate (0.7.59): the fresh plants' and the desalination plants'. |
| 950 | 3 | `public void setFreshCap(double cap)` | The fresh water limit, from the city's land and rights (Game.getFreshCap()); infinite for none. |
| 955 | 3 | `public void setBuildingWaterDraw(double water)` | Summed draw of every building standing, from the templates. |
| 960 | 3 | `public void setBilledWaterDraw(double water)` | The commercial + industrial slice, i.e. the part with a paying customer. |
| 969 | 3 | `public void setPopulation(long population)` | The people. |
| 973 | 3 | `public void setPricePerWaterUnit(double price)` |  |
| 983 | 50 | `public void updateEnergyRatio()` | The month dispatched (PW1, THE MERIT ORDER): the rows the city's plants make, run against the grid's draw by PowerDispatch; energyRatio is the energy served over the energy asked, and production the power offered, as ... |
| 1040 | 14 | `private PowerDispatch.Row rowFor(BuildingsTemplate t, long n, double extraKw, double fill)` | The row n of a template make at this staffing, their output at nameplate moved by extraKw (0: the row the month runs, to the bit). |
| 1062 | 27 | `public double deliveredWith(BuildingsTemplate t, double moveKw)` | The kW the grid delivers its customers with one template's output at nameplate moved by moveKw (PW1-L): the month's dispatch run again on the rows it ran, at the same draw, season, cap price and wires, that template's... |
| 1090 | 35 | `public void updateWaterRatio()` |  |
| 1130 | 3 | `public double getUtilityIncome()` | Both utilities consolidated. |
| 1141 | 52 | `public void updateUtilitiyWages(double[] wages, long[] electricityJobs, long[] waterJobs)` | NOTE: this now takes the electricity and water job arrays separately rather than one combined array. |
| 1194 | 4 | `public void updateJobFillRate(double[] fillRate)` |  |
| 1200 | 101 | `public void printUtilitiesInfo()` | printers |
| 1305 | 4 | `static { ... }` |  |
| 1312 | 24 | `public void redenominate(double scale)` | The utilities' prices and this month's bills, in the new unit. |

