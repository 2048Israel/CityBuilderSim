# UtilitiesHandler.java - 731 lines · 70 methods · 5 constants · model

`ham/citybuildersim/UtilitiesHandler.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [CityNeeds](CityNeeds.md) (2)

**Used by (24):** [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CementConcreteCheck](CementConcreteCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [HydroCheck](HydroCheck.md), [LandManager](LandManager.md), [LongPlaytest](LongPlaytest.md), [MaterialsCheck](MaterialsCheck.md), [MoneyAudit](MoneyAudit.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [WaterCheck](WaterCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 22 | WATER SUPPLY |
| 79 | THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) |
| 171 | · READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is |
| 256 | · Split books. The two utilities share one workforce and one fill rate, but |
| 316 | THE POWER THE WORKS MAKE (0.7.112, batch I2; |
| 613 | · · ELECTRIC POWER |
| 644 | · · WATER |
| 684 | · · CONSOLIDATED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `UtilitiesHandler.BASE_WATER_SUPPLY` | `8000` | What the city can draw before it builds anything: the legacy wells and the old municipal intake. |
| 42 | `UtilitiesHandler.BASE_GRID_KW` | `10000` | The kilowatts the grid starts with, before a power plant is built (setWattsProduction()). |
| 69 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 112 | `UtilitiesHandler.FRESH_UNITS_PER_KM2` | `121_600` | Units of fresh water a month one square kilometre of owned lake or river yields: the world's renewable river runoff (about 42,700 km3 a year, Shiklomanov) over the world's river and stream area (773,000 km2, Allen & P... |
| 711 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `public double production` |  |
| 18 | `public double baseProduction` |  |
| 19 | `public double consumption` |  |
| 20 | `public double energyRatio` |  |
| 54 | `private long foundingEndowments` | How many founding endowments the city holds (FW9, 2026-10-25; BuildingManager.getFoundingEndowments()): 1 for a founded city. |
| 77 | `private double pricePerWaterUnit` | $50 per unit, i.e. $5 per 1,000 gallons. |
| 114 | `public double waterProduction` |  |
| 115 | `public double baseWaterProduction` |  |
| 118 | `private double freshNameplate, desalNameplate` | The fresh plants' nameplate and the desalination plants', standing - the two parts of baseWaterProduction past the wells (0.7.59). |
| 121 | `private double freshCap` | What the city's fresh water yields a month, the rights included; infinite until the city's land says otherwise (setFreshCap()). |
| 124 | `private double freshDrawn, desalOutput` | This month's fresh water treated - the fresh plants at their staffing, held to the cap - and the sea desalinated. |
| 125 | `public double buildingWaterDraw` |  |
| 126 | `public double residentWaterDraw` |  |
| 133 | `public double billedWaterDraw` | The slice of demand that is actually invoiced: commercial and industrial buildings. |
| 134 | `public double waterConsumption` |  |
| 135 | `public double waterRatio` |  |
| 138 | `private double[] utilityWages` | jobs |
| 139 | `private long[] utilityJobs` |  |
| 143 | `private double[] electricityWages` | Same payroll, split by which utility the job belongs to, so the report can show two businesses rather than one lump. |
| 144 | `private double[] waterWages` |  |
| 145 | `private double[] fillRate` |  |
| 147 | `private double averageUtilityFill` |  |
| 150 | `private double pricePerWatt` | temporary |
| 300 | `private boolean billedByStatement` | WHAT THE CUSTOMERS WERE ACTUALLY CHARGED, since 2026-09-06. |
| 301 | `private double billedElectricityRevenue` |  |
| 302 | `private double billedWaterRevenue` |  |
| 336 | `private double worksPower` | kW the works make past their own draws, standing: added to the grid's generation. |
| 339 | `private double billedPowerBought` | What the works' statements booked as power sold to the grid this month: what the utility pays them. |
| 356 | `public double billedElectricityDraw` | What the four charged categories draw. |
| 375 | `private double homesElectricityDraw, homesWaterDraw` | What the homes draw, power and water (0.7.28): the residential buildings' own draw, set beside the billed draws by ServicesManager, so the Services screen can say who the unbilled draw is - the homes, or the city's ow... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 15 | 717 | **type** `public class UtilitiesHandler` |  |

### WATER SUPPLY (lines 22-78)

| line | len | member | says |
|---:|---:|---|---|
| 56 | 1 | `public void setFoundingEndowments(long endowments)` |  |
| 58 | 1 | `private double baseGrid()` |  |
| 59 | 1 | `private double baseWater()` |  |

### THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) (lines 79-170)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 3 | `public UtilitiesHandler()` |  |
| 157 | 4 | `public void updateUtilitiesHandler()` | updaters |
| 163 | 3 | `public double getEnergyRatio()` | getters |
| 167 | 3 | `public double getPricerPerWatt()` |  |

### READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is (lines 171-255)

| line | len | member | says |
|---:|---:|---|---|
| 176 | 1 | `public double getProduction()` |  |
| 177 | 1 | `public double getBaseProduction()` |  |
| 178 | 1 | `public double getConsumption()` |  |
| 179 | 1 | `public double getAverageUtilityFill()` |  |
| 181 | 1 | `public double getWaterProduction()` |  |
| 182 | 1 | `public double getBaseWaterProduction()` |  |
| 183 | 1 | `public double getWaterConsumption()` |  |
| 184 | 1 | `public double getBuildingWaterDraw()` |  |
| 185 | 1 | `public double getResidentWaterDraw()` |  |
| 186 | 1 | `public double getBilledWaterDraw()` |  |
| 187 | 1 | `public double getUnbilledWaterDraw()` |  |
| 188 | 1 | `public double getWaterRatio()` |  |
| 191 | 1 | `public double getFreshCap()` | The fresh water limit this month: FRESH_UNITS_PER_KM2 x the owned lakes and river, plus the rights (0.7.59); infinite when no land was given. |
| 193 | 1 | `public double getFreshNameplate()` | The Water Treatment Plants' nameplate, standing. |
| 195 | 1 | `public double getFreshDrawn()` | The fresh water treated this month: the fresh plants at their staffing, held to the cap. |
| 197 | 1 | `public double getDesalNameplate()` | The Desalination Plants' nameplate, standing. |
| 199 | 1 | `public double getDesalOutput()` | The sea desalinated this month: their nameplate at the staffing. |
| 202 | 3 | `public boolean isFreshCapped()` | Whether the fresh water limit holds the plants back this month: what they would treat at their staffing is more than the cap. |
| 207 | 4 | `public double getFreshIdleShare()` | The share of what the fresh plants would treat at their staffing that the limit idles, 0 to 1: the Services page's "62% of the plants' nameplate idle". |
| 213 | 3 | `public double getFreshHeadroom()` | The fresh water a plant more would add at the staffing: what is left under the cap, never below nothing. |
| 218 | 3 | `public double getWaterAtFullStaff()` | What the works would produce fully staffed, held to the fresh water limit: the Services page's "at full staff" (0.7.59; baseWaterProduction until then). |
| 232 | 3 | `public static double waterOutput(double fresh, double desal, double fill, double cap)` | The water the works produce at a staffing, by the rule above, from its parts: the nameplate and the wells times the fill, unless the fresh plants' share would pass the cap, when the wells and the sea are times the fil... |
| 237 | 6 | `public static double waterOutput(double fresh, double desal, double fill, double cap, long endowments)` | ...for a city holding that many founding endowments (FW9): the wells are one endowment's, times them. |
| 243 | 1 | `public double getPricePerWaterUnit()` |  |
| 252 | 1 | `public double getPowerServed()` | SERVED (0.7.41): what the grid generates over what the city asks of it, unclamped - energyRatio's own fraction without its cap at 1, so past 100% it keeps counting the headroom - CityNeeds.servedShare(): +∞ with nothi... |
| 254 | 1 | `public double getWaterServed()` | ...and what the water works treat over what the city asks of them, waterRatio's fraction unclamped. |

### Split books. The two utilities share one workforce and one fill rate, but (lines 256-315)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 4 | `public double getElectricityRevenue()` | The month's electricity bill - only the draw somebody is actually invoiced for, and only the fraction delivered. |
| 304 | 5 | `public void setBilledRevenue(double electricity, double water)` |  |
| 311 | 4 | `public void setBilledRevenue(double electricity, double water, double powerBought)` | ...and what the works sold it of their own power, as their statements booked it (0.7.112; THE POWER THE WORKS MAKE, below). |

### THE POWER THE WORKS MAKE (0.7.112, batch I2; (lines 316-731)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 3 | `public void setWorksPower(double kw)` | The surplus the works make past their own draws, kW (ServicesManager, from the buildings standing). |
| 347 | 1 | `public double getWorksPower()` | kW the works sell the grid: added to what it generates. |
| 350 | 4 | `public double getPowerBought()` | What the utility pays the works for their power this month: the statements' figure, or with none handed in the surplus at the tariff. |
| 358 | 3 | `public void setBilledElectricityDraw(double draw)` |  |
| 361 | 1 | `public double getBilledElectricityDraw()` |  |
| 362 | 3 | `public double getUnbilledElectricityDraw()` |  |
| 377 | 4 | `public void setHomesDraw(double electricity, double water)` |  |
| 381 | 1 | `public double getHomesElectricityDraw()` |  |
| 382 | 1 | `public double getHomesWaterDraw()` |  |
| 390 | 4 | `public double getWaterRevenue()` | Only the billed slice, and only the fraction actually delivered - during rationing customers receive waterRatio of what they asked for and are charged for that, which is also exactly what the commercial and industrial... |
| 403 | 3 | `public double getElectricityPayroll()` | PER JOB TYPE since 0.7.17, as every employer's payroll is: each type's wage for the posts of that type filled, which is what the households in them are paid. |
| 407 | 3 | `public double getWaterPayroll()` |  |
| 412 | 6 | `private double staffed(double[] bill)` | A wage bill by job type, at each type's own fill. |
| 419 | 6 | `public double getElectricityIncome()` |  |
| 426 | 3 | `public double getWaterIncome()` |  |
| 430 | 5 | `private static double sum(double[] a)` |  |
| 436 | 3 | `public double getUtilityPayroll()` |  |
| 440 | 3 | `public double getUtilityRevenue()` |  |
| 445 | 3 | `public void setWattsProduction(double watts)` | setters |
| 449 | 4 | `public void setWattsConsumption(double watts)` |  |
| 454 | 3 | `public void setWaterProduction(double water)` |  |
| 459 | 4 | `public void setWaterSources(double fresh, double desal)` | The two parts of that nameplate (0.7.59): the fresh plants' and the desalination plants'. |
| 465 | 3 | `public void setFreshCap(double cap)` | The fresh water limit, from the city's land and rights (Game.getFreshCap()); infinite for none. |
| 470 | 3 | `public void setBuildingWaterDraw(double water)` | Summed draw of every building standing, from the templates. |
| 475 | 3 | `public void setBilledWaterDraw(double water)` | The commercial + industrial slice, i.e. the part with a paying customer. |
| 484 | 3 | `public void setPopulation(long population)` | The people. |
| 488 | 3 | `public void setPricePerWaterUnit(double price)` |  |
| 494 | 8 | `public void updateEnergyRatio()` | passers calculators |
| 503 | 31 | `public void updateWaterRatio()` |  |
| 539 | 3 | `public double getUtilityIncome()` | Both utilities consolidated. |
| 550 | 52 | `public void updateUtilitiyWages(double[] wages, long[] electricityJobs, long[] waterJobs)` | NOTE: this now takes the electricity and water job arrays separately rather than one combined array. |
| 603 | 4 | `public void updateJobFillRate(double[] fillRate)` |  |
| 609 | 100 | `public void printUtilitiesInfo()` | printers |
| 713 | 4 | `static { ... }` |  |
| 720 | 10 | `public void redenominate(double scale)` | The utilities' prices and this month's bills, in the new unit. |

