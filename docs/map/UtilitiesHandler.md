# UtilitiesHandler.java - 1,171 lines · 109 methods · 8 constants · model

`ham/citybuildersim/UtilitiesHandler.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [PowerDispatch](PowerDispatch.md) (46), [Currency](Currency.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (6), [Good](Good.md) (5), [PriceLevel](PriceLevel.md) (3), [CityNeeds](CityNeeds.md) (2)

**Used by (42):** [AdviceValueOracle](AdviceValueOracle.md), [AutoBuildCheck](AutoBuildCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CementConcreteCheck](CementConcreteCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [DataSave](DataSave.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HydroCheck](HydroCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [LandManager](LandManager.md), [LicenceFees](LicenceFees.md), [LongPlaytest](LongPlaytest.md), [MaterialsCheck](MaterialsCheck.md), [MoneyAudit](MoneyAudit.md), [NewUnitCheck](NewUnitCheck.md), [PowerDispatchCheck](PowerDispatchCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PowerView](PowerView.md), [PowerViewCheck](PowerViewCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [ResearchCheck](ResearchCheck.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [WaterCheck](WaterCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 37 | WATER SUPPLY |
| 96 | THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) |
| 186 | · READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is |
| 271 | · Split books. The two utilities share one workforce and one fill rate, but |
| 331 | THE POWER THE WORKS MAKE (0.7.112, batch I2; |
| 462 | THE MERIT ORDER (PW1, 2026-11-11; spec-electricity 2.2, 2.6; Jerus's Q1: "Market price") |
| 1042 | · · ELECTRIC POWER |
| 1073 | · · WATER |
| 1113 | · · CONSOLIDATED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `UtilitiesHandler.BASE_WATER_SUPPLY` | `8000` | What the city can draw before it builds anything: the legacy wells and the old municipal intake. |
| 57 | `UtilitiesHandler.BASE_GRID_KW` | `10000` | The kilowatts the grid starts with, before a power plant is built (setWattsProduction()). |
| 84 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 129 | `UtilitiesHandler.FRESH_UNITS_PER_KM2` | `121_600` | Units of fresh water a month one square kilometre of owned lake or river yields: the world's renewable river runoff (about 42,700 km3 a year, Shiklomanov) over the world's river and stream area (773,000 km2, Allen & P... |
| 487 | `UtilitiesHandler.FOUNDING_TARIFF` | `.01 * PriceLevel.FACTOR` | The founding tariff a kW a month, D$10 at the founding's level, held by the struck book until a month has cleared (the tariff the game had before PW1). |
| 489 | `UtilitiesHandler.WORKS_ROW` | `"works", GRID_ROW = "grid"` |  |
| 521 | `UtilitiesHandler.WORKS_ID` | `- 1, GRID_ID = - 2, FIXTURE_ID = - 3` | Reserved aggregate row ids; catalogue rows keep their permanent nonnegative ids. |
| 1140 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `public double production` |  |
| 33 | `public double baseProduction` | The kilowatts on site, plants and the founding grid. |
| 34 | `public double consumption` |  |
| 35 | `public double energyRatio` |  |
| 69 | `private long foundingEndowments` | How many founding endowments the city holds (FW9, 2026-10-25; BuildingManager.getFoundingEndowments()): 1 for a founded city. |
| 94 | `private double pricePerWaterUnit` | $50 per unit, i.e. $5 per 1,000 gallons. |
| 131 | `public double waterProduction` |  |
| 132 | `public double baseWaterProduction` |  |
| 135 | `private double freshNameplate, desalNameplate` | The fresh plants' nameplate and the desalination plants', standing - the two parts of baseWaterProduction past the wells (0.7.59). |
| 138 | `private double freshCap` | What the city's fresh water yields a month, the rights included; infinite until the city's land says otherwise (setFreshCap()). |
| 141 | `private double freshDrawn, desalOutput` | This month's fresh water treated - the fresh plants at their staffing, held to the cap - and the sea desalinated. |
| 142 | `public double buildingWaterDraw` |  |
| 143 | `public double residentWaterDraw` |  |
| 150 | `public double billedWaterDraw` | The slice of demand that is actually invoiced: commercial and industrial buildings. |
| 151 | `public double waterConsumption` |  |
| 152 | `public double waterRatio` |  |
| 155 | `private double[] utilityWages` | jobs |
| 156 | `private long[] utilityJobs` |  |
| 160 | `private double[] electricityWages` | Same payroll, split by which utility the job belongs to, so the report can show two businesses rather than one lump. |
| 161 | `private double[] waterWages` |  |
| 162 | `private double[] fillRate` |  |
| 164 | `private double averageUtilityFill` |  |
| 315 | `private boolean billedByStatement` | WHAT THE CUSTOMERS WERE ACTUALLY CHARGED, since 2026-09-06. |
| 316 | `private double billedElectricityRevenue` |  |
| 317 | `private double billedWaterRevenue` |  |
| 351 | `private double worksPower` | kW the works make past their own draws, standing: added to the grid's generation. |
| 354 | `private double billedPowerBought` | What the works' statements booked as power sold to the grid this month: what the utility pays them. |
| 371 | `public double billedElectricityDraw` | What the four charged categories draw. |
| 390 | `private double homesElectricityDraw, homesWaterDraw` | What the homes draw, power and water (0.7.28): the residential buildings' own draw, set beside the billed draws by ServicesManager, so the Services screen can say who the unbilled draw is - the homes, or the city's ow... |
| 492 | `private double voll` | What a short half-block clears at, and what the long and the street wires charge a kWh delivered: city money, struck by seedConstants(). |
| 495 | `private Map<BuildingsTemplate, Long> plants` | The power plants standing and how many of each (ServicesManager); null until handed in. |
| 498 | `private ToDoubleFunction<Good> fuelPriceSource` | Where the month's fuel price, the exchange rate and the calendar month come from; null for a bare fixture (the world's price, at par, no season). |
| 499 | `private DoubleSupplier exchangeRateSource` |  |
| 500 | `private IntSupplier calendarMonth` |  |
| 503 | `private PowerDispatch.Month latest` | The month last dispatched, and the rows it ran. |
| 504 | `private List<PowerDispatch.Row> latestRows` |  |
| 507 | `private IntSupplier observationMonth` | Observation sources only: the absolute dispatch month and the existing build advice's firm supply. |
| 508 | `private DoubleSupplier firmSupply` |  |
| 509 | `private DispatchRecord latestRecord` |  |
| 510 | `private int paidDispatchMonth` |  |
| 511 | `private int fuelPaidMonth` |  |
| 512 | `private PowerDispatch.Month pendingLoadedMonth` |  |
| 513 | `private DispatchRecord pendingLoadedRecord` |  |
| 543 | `private Book struck` | The month last struck: the founding tariff until one is. |
| 546 | `private double fuelLocal, fuelImported, upkeepPaid` | The fuel the plants burned and the upkeep they paid, in city money: at home, abroad, and the upkeep (set by setFuelPaid()). |
| 549 | `private Map<String, Double> fuelImportedByGood` | ...and the imported part good by good, for the Trade tab (Sectors.tradeByGood(); its sum is fuelImported). |
| 588 | `public double ratio, energyPrice` | The month's ratio served, and its energy price a kWh delivered. |
| 590 | `public double commercePrice, industryPrice, worksPrice` | City money a kW a month: commerce and light industry (the energy and all the wires), Heavy Industry and Mining (the energy and transmission), and the works' surplus. |
| 592 | `public double[] price` | Per half-block: the price a kWh, the load, and what was left unserved, kW. |
| 594 | `public double askedKwh, servedKwh, curtailedKwh, fuelledKwh, windKwh, hydroKwh, worksKwh, gridKwh` | The month's kWh asked, served and curtailed, and made by the fuelled plants, the wind, the hydro, the works and the founding grid. |
| 596 | `public double upkeepWorld` | The variable upkeep the plants ran up, thousands of the WORLD's money. |
| 598 | `public LinkedHashMap<String, Double> fuel` | Fuel burned: good name to units. |
| 600 | `public boolean cleared` | True once a month has been struck into this book; false in the founding book, for the screens to say "still at founding". |
| 602 | `public DispatchRecord dispatch` | Optional observations: absent in older saves, never back-filled from the ending fleet. |
| 603 | `public int struckMonth` |  |
| 604 | `public double transmissionPrice, distributionPrice` |  |
| 743 | `public Book struck` |  |
| 744 | `public double fuelLocal, fuelImported, upkeepPaid` |  |
| 745 | `public Map<String, Double> fuelImportedByGood` |  |
| 746 | `public int paidDispatchMonth` |  |
| 747 | `public int fuelPaidMonth` |  |
| 749 | `public PowerDispatch.Month pendingMonth` | The completed closing dispatch, still awaiting the next strike; optional in older saves. |
| 750 | `public DispatchRecord pendingDispatch` |  |
| 765 | `private boolean seedOnLoad` | True from a load whose save carried no struck book until seedLoadedRatio() has given the founding book the ratio the load computed. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 22 | 1150 | **type** `public class UtilitiesHandler` |  |

### WATER SUPPLY (lines 37-95)

| line | len | member | says |
|---:|---:|---|---|
| 71 | 1 | `public void setFoundingEndowments(long endowments)` |  |
| 73 | 1 | `private double baseGrid()` |  |
| 74 | 1 | `private double baseWater()` |  |

### THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) (lines 96-185)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 3 | `public UtilitiesHandler()` |  |
| 171 | 4 | `public void updateUtilitiesHandler()` | updaters |
| 177 | 3 | `public double getEnergyRatio()` | getters |
| 182 | 3 | `public double getPricerPerWatt()` | What commerce and light industry pay a kW a month: the struck book's commerce price (THE MERIT ORDER, PW1); the founding tariff until a month is struck. |

### READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is (lines 186-270)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 1 | `public double getProduction()` |  |
| 192 | 1 | `public double getBaseProduction()` |  |
| 193 | 1 | `public double getConsumption()` |  |
| 194 | 1 | `public double getAverageUtilityFill()` |  |
| 196 | 1 | `public double getWaterProduction()` |  |
| 197 | 1 | `public double getBaseWaterProduction()` |  |
| 198 | 1 | `public double getWaterConsumption()` |  |
| 199 | 1 | `public double getBuildingWaterDraw()` |  |
| 200 | 1 | `public double getResidentWaterDraw()` |  |
| 201 | 1 | `public double getBilledWaterDraw()` |  |
| 202 | 1 | `public double getUnbilledWaterDraw()` |  |
| 203 | 1 | `public double getWaterRatio()` |  |
| 206 | 1 | `public double getFreshCap()` | The fresh water limit this month: FRESH_UNITS_PER_KM2 x the owned lakes and river, plus the rights (0.7.59); infinite when no land was given. |
| 208 | 1 | `public double getFreshNameplate()` | The Water Treatment Plants' nameplate, standing. |
| 210 | 1 | `public double getFreshDrawn()` | The fresh water treated this month: the fresh plants at their staffing, held to the cap. |
| 212 | 1 | `public double getDesalNameplate()` | The Desalination Plants' nameplate, standing. |
| 214 | 1 | `public double getDesalOutput()` | The sea desalinated this month: their nameplate at the staffing. |
| 217 | 3 | `public boolean isFreshCapped()` | Whether the fresh water limit holds the plants back this month: what they would treat at their staffing is more than the cap. |
| 222 | 4 | `public double getFreshIdleShare()` | The share of what the fresh plants would treat at their staffing that the limit idles, 0 to 1: the Services page's "62% of the plants' nameplate idle". |
| 228 | 3 | `public double getFreshHeadroom()` | The fresh water a plant more would add at the staffing: what is left under the cap, never below nothing. |
| 233 | 3 | `public double getWaterAtFullStaff()` | What the works would produce fully staffed, held to the fresh water limit: the Services page's "at full staff" (0.7.59; baseWaterProduction until then). |
| 247 | 3 | `public static double waterOutput(double fresh, double desal, double fill, double cap)` | The water the works produce at a staffing, by the rule above, from its parts: the nameplate and the wells times the fill, unless the fresh plants' share would pass the cap, when the wells and the sea are times the fil... |
| 252 | 6 | `public static double waterOutput(double fresh, double desal, double fill, double cap, long endowments)` | ...for a city holding that many founding endowments (FW9): the wells are one endowment's, times them. |
| 258 | 1 | `public double getPricePerWaterUnit()` |  |
| 267 | 1 | `public double getPowerServed()` | SERVED (0.7.41): what the grid generates over what the city asks of it, unclamped - energyRatio's own fraction without its cap at 1, so past 100% it keeps counting the headroom - CityNeeds.servedShare(): +∞ with nothi... |
| 269 | 1 | `public double getWaterServed()` | ...and what the water works treat over what the city asks of them, waterRatio's fraction unclamped. |

### Split books. The two utilities share one workforce and one fill rate, but (lines 271-330)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 4 | `public double getElectricityRevenue()` | The month's electricity bill - only the draw somebody is actually invoiced for, and only the fraction delivered. |
| 319 | 5 | `public void setBilledRevenue(double electricity, double water)` |  |
| 326 | 4 | `public void setBilledRevenue(double electricity, double water, double powerBought)` | ...and what the works sold it of their own power, as their statements booked it (0.7.112; THE POWER THE WORKS MAKE, below). |

### THE POWER THE WORKS MAKE (0.7.112, batch I2; (lines 331-461)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 3 | `public void setWorksPower(double kw)` | The surplus the works make past their own draws, kW (ServicesManager, from the buildings standing). |
| 362 | 1 | `public double getWorksPower()` | kW the works sell the grid: added to what it generates. |
| 365 | 4 | `public double getPowerBought()` | What the utility pays the works for their power this month: the statements' figure, or with none handed in the surplus at the tariff. |
| 373 | 3 | `public void setBilledElectricityDraw(double draw)` |  |
| 376 | 1 | `public double getBilledElectricityDraw()` |  |
| 377 | 3 | `public double getUnbilledElectricityDraw()` |  |
| 392 | 4 | `public void setHomesDraw(double electricity, double water)` |  |
| 396 | 1 | `public double getHomesElectricityDraw()` |  |
| 397 | 1 | `public double getHomesWaterDraw()` |  |
| 405 | 4 | `public double getWaterRevenue()` | Only the billed slice, and only the fraction actually delivered - during rationing customers receive waterRatio of what they asked for and are charged for that, which is also exactly what the commercial and industrial... |
| 418 | 3 | `public double getElectricityPayroll()` | PER JOB TYPE since 0.7.17, as every employer's payroll is: each type's wage for the posts of that type filled, which is what the households in them are paid. |
| 422 | 3 | `public double getWaterPayroll()` |  |
| 427 | 6 | `private double staffed(double[] bill)` | A wage bill by job type, at each type's own fill. |
| 434 | 9 | `public double getElectricityIncome()` |  |
| 444 | 3 | `public double getWaterIncome()` |  |
| 448 | 5 | `private static double sum(double[] a)` |  |
| 454 | 3 | `public double getUtilityPayroll()` |  |
| 458 | 3 | `public double getUtilityRevenue()` |  |

### THE MERIT ORDER (PW1, 2026-11-11; spec-electricity 2.2, 2.6; Jerus's Q1: "Market price") (lines 462-1171)

| line | len | member | says |
|---:|---:|---|---|
| 515 | 4 | `public void setPowerObservationSources(IntSupplier month, DoubleSupplier firm)` |  |
| 524 | 9 | **type** `public record RowRecord(int id, int sourceIndex, PowerDispatch.Kind kind, Good fuel, double offeredKw, doub...` | One catalogue row's actual run and clearing receipts, not its profit or its fuel purchase bill. |
| 527 | 1 | `{ ... }` _(in UtilitiesHandler.RowRecord)_ |  |
| 528 | 4 | `RowRecord scaled(double scale)` _(in UtilitiesHandler.RowRecord)_ |  |
| 535 | 6 | **type** `public record DispatchRecord(int month, double firmKw, List<RowRecord> rows)` | Immutable bounded aggregates, captured at dispatch, before another month can change fleet, fuel or prices. |
| 536 | 1 | `{ ... }` _(in UtilitiesHandler.DispatchRecord)_ |  |
| 537 | 3 | `DispatchRecord scaled(double scale)` _(in UtilitiesHandler.DispatchRecord)_ |  |
| 552 | 3 | `public void setPlants(Map<BuildingsTemplate, Long> standing)` | The power plants standing and how many of each. |
| 557 | 1 | `public void setFuelPrice(ToDoubleFunction<Good> cityPrice)` | The city's price of a unit of a fuel; null: the world's price (which already carries PriceLevel.FACTOR). |
| 560 | 1 | `public void setExchangeRate(DoubleSupplier rate)` | City money per world thousand of the founding's dollars (Markets.getExchangeRate(): city money a dollar times the world's price level, the D$ the goods are struck at); null: 1, at par. |
| 563 | 1 | `public void setCalendarMonth(IntSupplier month)` | The calendar month, 1 to 12, for the season; null: no season. |
| 565 | 3 | `private double fuelPrice(Good g)` |  |
| 569 | 3 | `private double exchangeRate()` |  |
| 574 | 6 | `public void seedConstants(double unit)` | Re-seeds the money constants at a given unit: the dial over the unit, as Bank.seedConstants() does (the later slice calls it from Game.restrikeMoneyConstants()). |
| 581 | 1 | `public PowerDispatch.Month getLatestMonth()` |  |
| 583 | 1 | `public List<PowerDispatch.Row> getLatestRows()` |  |
| 586 | 43 | **type** `public static final class Book` | A month's price and what it was made of: what the bills and the screens read. |
| 607 | 6 | `public static Book founding()` _(in UtilitiesHandler.Book)_ | The book before any month has cleared: the founding tariff, ratio 1, nothing made. |
| 614 | 14 | `Book copy()` _(in UtilitiesHandler.Book)_ |  |
| 630 | 6 | `private static void scale(Book b, double scale)` |  |
| 638 | 31 | `public void strikePower()` | Strikes the month last dispatched into the book: a new object each time, sharing nothing with the dispatch. |
| 670 | 1 | `public Book getStruckBook()` |  |
| 673 | 1 | `public boolean isFoundingBook()` | True while no month has been struck: the prices are the founding tariff (the book's own flag, so a load keeps it). |
| 676 | 1 | `public double getEnergyPricePerMwh()` | What the month's energy cleared at, city money (thousands) a MWh delivered. |
| 679 | 1 | `public double getFuelBurned(Good g)` | Units of a good the plants burned in the month last struck (tonnes, for coal). |
| 682 | 1 | `public double getCurtailedKwh()` | kWh of the wind and the works' surplus turned away in the month last struck, for want of a load. |
| 685 | 5 | `public double getUnservedKwh()` | kWh of the load left unserved in the month last struck, summed over its six half-blocks. |
| 692 | 5 | `public int getHalfBlocksShort()` | How many of the month's six half-blocks left some load unserved. |
| 699 | 1 | `public double getPricePerWatt()` | What commerce and light industry pay a kW a month (the struck commerce price). |
| 702 | 1 | `public double getIndustryPricePerWatt()` | What Heavy Industry and Mining pay a kW a month (the energy and the long wires). |
| 705 | 1 | `public double getWorksPricePerWatt()` | What the utility pays the works a kW of surplus a month. |
| 711 | 3 | `public void setFuelPaid(double local, double imported, double upkeep)` | The fuel the plants burned this month and the upkeep they ran up, in city money: at home, abroad, and the upkeep (a later slice draws them from the market at the top of the month). |
| 716 | 14 | `public void setFuelPaid(double local, double imported, double upkeep, Map<String, Double> importedByGood)` | ...with the imported part good by good (Game.buyPowerFuel()). |
| 732 | 1 | `public Map<String, Double> getFuelImportedByGood()` | The fuel bought from the world this month, good by good (the Good's name), in city money. |
| 734 | 1 | `public double getFuelLocal()` |  |
| 735 | 1 | `public double getFuelImported()` |  |
| 736 | 1 | `public double getUpkeepPaid()` |  |
| 737 | 1 | `public double getFuelBill()` |  |
| 738 | 1 | `public int getPaidDispatchMonth()` |  |
| 739 | 1 | `public int getFuelPaidMonth()` |  |
| 742 | 10 | **type** `public static final class PowerState` | What the save keeps of the merit order: the struck book and the fuel paid (a later slice wires it to DataSave). |
| 753 | 10 | `public PowerState toPowerState()` |  |
| 773 | 5 | `public void seedLoadedRatio()` | A save from before PW1 carries no book (REV-PW1c): it loads the founding book, and here, on the load path after updateServices() has dispatched the loaded city, that book takes the ratio just computed, the one the loa... |
| 780 | 22 | `public void restorePendingPower()` | Restores the completed closing flow after the load's LAST derived-services rebuild, for the next strike. |
| 804 | 10 | `public void restorePowerState(PowerState s)` | Restores a save's book and fuel paid; a save with none (null, or no book) leaves the founding book. |
| 816 | 3 | `public void setWattsProduction(double watts)` | setters |
| 820 | 4 | `public void setWattsConsumption(double watts)` |  |
| 825 | 3 | `public void setWaterProduction(double water)` |  |
| 830 | 4 | `public void setWaterSources(double fresh, double desal)` | The two parts of that nameplate (0.7.59): the fresh plants' and the desalination plants'. |
| 836 | 3 | `public void setFreshCap(double cap)` | The fresh water limit, from the city's land and rights (Game.getFreshCap()); infinite for none. |
| 841 | 3 | `public void setBuildingWaterDraw(double water)` | Summed draw of every building standing, from the templates. |
| 846 | 3 | `public void setBilledWaterDraw(double water)` | The commercial + industrial slice, i.e. the part with a paying customer. |
| 855 | 3 | `public void setPopulation(long population)` | The people. |
| 859 | 3 | `public void setPricePerWaterUnit(double price)` |  |
| 869 | 62 | `public void updateEnergyRatio()` | The month dispatched (PW1, THE MERIT ORDER): the rows the city's plants make, run against the grid's draw by PowerDispatch; energyRatio is the energy served over the energy asked, and production the power offered, as ... |
| 932 | 31 | `public void updateWaterRatio()` |  |
| 968 | 3 | `public double getUtilityIncome()` | Both utilities consolidated. |
| 979 | 52 | `public void updateUtilitiyWages(double[] wages, long[] electricityJobs, long[] waterJobs)` | NOTE: this now takes the electricity and water job arrays separately rather than one combined array. |
| 1032 | 4 | `public void updateJobFillRate(double[] fillRate)` |  |
| 1038 | 100 | `public void printUtilitiesInfo()` | printers |
| 1142 | 4 | `static { ... }` |  |
| 1149 | 21 | `public void redenominate(double scale)` | The utilities' prices and this month's bills, in the new unit. |

