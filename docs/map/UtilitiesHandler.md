# UtilitiesHandler.java - 654 lines · 62 methods · 4 constants · model

`ham/citybuildersim/UtilitiesHandler.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [CityNeeds](CityNeeds.md) (2)

**Used by (14):** [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [ReadPathCheck](ReadPathCheck.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 22 | WATER SUPPLY |
| 59 | THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) |
| 151 | · READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is |
| 230 | · Split books. The two utilities share one workforce and one fill rate, but |
| 537 | · · ELECTRIC POWER |
| 568 | · · WATER |
| 608 | · · CONSOLIDATED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `UtilitiesHandler.BASE_WATER_SUPPLY` | `8000` | What the city can draw before it builds anything: the legacy wells and the old municipal intake. |
| 49 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 92 | `UtilitiesHandler.FRESH_UNITS_PER_KM2` | `121_600` | Units of fresh water a month one square kilometre of owned lake or river yields: the world's renewable river runoff (about 42,700 km3 a year, Shiklomanov) over the world's river and stream area (773,000 km2, Allen & P... |
| 635 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `public double production` |  |
| 18 | `public double baseProduction` |  |
| 19 | `public double consumption` |  |
| 20 | `public double energyRatio` |  |
| 57 | `private double pricePerWaterUnit` | $50 per unit, i.e. $5 per 1,000 gallons. |
| 94 | `public double waterProduction` |  |
| 95 | `public double baseWaterProduction` |  |
| 98 | `private double freshNameplate, desalNameplate` | The fresh plants' nameplate and the desalination plants', standing - the two parts of baseWaterProduction past the wells (0.7.59). |
| 101 | `private double freshCap` | What the city's fresh water yields a month, the rights included; infinite until the city's land says otherwise (setFreshCap()). |
| 104 | `private double freshDrawn, desalOutput` | This month's fresh water treated - the fresh plants at their staffing, held to the cap - and the sea desalinated. |
| 105 | `public double buildingWaterDraw` |  |
| 106 | `public double residentWaterDraw` |  |
| 113 | `public double billedWaterDraw` | The slice of demand that is actually invoiced: commercial and industrial buildings. |
| 114 | `public double waterConsumption` |  |
| 115 | `public double waterRatio` |  |
| 118 | `private double[] utilityWages` | jobs |
| 119 | `private long[] utilityJobs` |  |
| 123 | `private double[] electricityWages` | Same payroll, split by which utility the job belongs to, so the report can show two businesses rather than one lump. |
| 124 | `private double[] waterWages` |  |
| 125 | `private double[] fillRate` |  |
| 127 | `private double averageUtilityFill` |  |
| 130 | `private double pricePerWatt` | temporary |
| 274 | `private boolean billedByStatement` | WHAT THE CUSTOMERS WERE ACTUALLY CHARGED, since 2026-09-06. |
| 275 | `private double billedElectricityRevenue` |  |
| 276 | `private double billedWaterRevenue` |  |
| 285 | `public double billedElectricityDraw` | What the four charged categories draw. |
| 304 | `private double homesElectricityDraw, homesWaterDraw` | What the homes draw, power and water (0.7.28): the residential buildings' own draw, set beside the billed draws by ServicesManager, so the Services screen can say who the unbilled draw is - the homes, or the city's ow... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 15 | 640 | **type** `public class UtilitiesHandler` |  |

### WATER SUPPLY (lines 22-58)

### THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7) (lines 59-150)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 3 | `public UtilitiesHandler()` |  |
| 137 | 4 | `public void updateUtilitiesHandler()` | updaters |
| 143 | 3 | `public double getEnergyRatio()` | getters |
| 147 | 3 | `public double getPricerPerWatt()` |  |

### READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is (lines 151-229)

| line | len | member | says |
|---:|---:|---|---|
| 156 | 1 | `public double getProduction()` |  |
| 157 | 1 | `public double getBaseProduction()` |  |
| 158 | 1 | `public double getConsumption()` |  |
| 159 | 1 | `public double getAverageUtilityFill()` |  |
| 161 | 1 | `public double getWaterProduction()` |  |
| 162 | 1 | `public double getBaseWaterProduction()` |  |
| 163 | 1 | `public double getWaterConsumption()` |  |
| 164 | 1 | `public double getBuildingWaterDraw()` |  |
| 165 | 1 | `public double getResidentWaterDraw()` |  |
| 166 | 1 | `public double getBilledWaterDraw()` |  |
| 167 | 1 | `public double getUnbilledWaterDraw()` |  |
| 168 | 1 | `public double getWaterRatio()` |  |
| 171 | 1 | `public double getFreshCap()` | The fresh water limit this month: FRESH_UNITS_PER_KM2 x the owned lakes and river, plus the rights (0.7.59); infinite when no land was given. |
| 173 | 1 | `public double getFreshNameplate()` | The Water Treatment Plants' nameplate, standing. |
| 175 | 1 | `public double getFreshDrawn()` | The fresh water treated this month: the fresh plants at their staffing, held to the cap. |
| 177 | 1 | `public double getDesalNameplate()` | The Desalination Plants' nameplate, standing. |
| 179 | 1 | `public double getDesalOutput()` | The sea desalinated this month: their nameplate at the staffing. |
| 182 | 3 | `public boolean isFreshCapped()` | Whether the fresh water limit holds the plants back this month: what they would treat at their staffing is more than the cap. |
| 187 | 4 | `public double getFreshIdleShare()` | The share of what the fresh plants would treat at their staffing that the limit idles, 0 to 1: the Services page's "62% of the plants' nameplate idle". |
| 193 | 3 | `public double getFreshHeadroom()` | The fresh water a plant more would add at the staffing: what is left under the cap, never below nothing. |
| 198 | 3 | `public double getWaterAtFullStaff()` | What the works would produce fully staffed, held to the fresh water limit: the Services page's "at full staff" (0.7.59; baseWaterProduction until then). |
| 212 | 5 | `public static double waterOutput(double fresh, double desal, double fill, double cap)` | The water the works produce at a staffing, by the rule above, from its parts: the nameplate and the wells times the fill, unless the fresh plants' share would pass the cap, when the wells and the sea are times the fil... |
| 217 | 1 | `public double getPricePerWaterUnit()` |  |
| 226 | 1 | `public double getPowerServed()` | SERVED (0.7.41): what the grid generates over what the city asks of it, unclamped - energyRatio's own fraction without its cap at 1, so past 100% it keeps counting the headroom - CityNeeds.servedShare(): +∞ with nothi... |
| 228 | 1 | `public double getWaterServed()` | ...and what the water works treat over what the city asks of them, waterRatio's fraction unclamped. |

### Split books. The two utilities share one workforce and one fill rate, but (lines 230-654)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 4 | `public double getElectricityRevenue()` | The month's electricity bill - only the draw somebody is actually invoiced for, and only the fraction delivered. |
| 278 | 5 | `public void setBilledRevenue(double electricity, double water)` |  |
| 287 | 3 | `public void setBilledElectricityDraw(double draw)` |  |
| 290 | 1 | `public double getBilledElectricityDraw()` |  |
| 291 | 3 | `public double getUnbilledElectricityDraw()` |  |
| 306 | 4 | `public void setHomesDraw(double electricity, double water)` |  |
| 310 | 1 | `public double getHomesElectricityDraw()` |  |
| 311 | 1 | `public double getHomesWaterDraw()` |  |
| 319 | 4 | `public double getWaterRevenue()` | Only the billed slice, and only the fraction actually delivered - during rationing customers receive waterRatio of what they asked for and are charged for that, which is also exactly what the commercial and industrial... |
| 332 | 3 | `public double getElectricityPayroll()` | PER JOB TYPE since 0.7.17, as every employer's payroll is: each type's wage for the posts of that type filled, which is what the households in them are paid. |
| 336 | 3 | `public double getWaterPayroll()` |  |
| 341 | 6 | `private double staffed(double[] bill)` | A wage bill by job type, at each type's own fill. |
| 348 | 3 | `public double getElectricityIncome()` |  |
| 352 | 3 | `public double getWaterIncome()` |  |
| 356 | 5 | `private static double sum(double[] a)` |  |
| 362 | 3 | `public double getUtilityPayroll()` |  |
| 366 | 3 | `public double getUtilityRevenue()` |  |
| 371 | 3 | `public void setWattsProduction(double watts)` | setters |
| 375 | 4 | `public void setWattsConsumption(double watts)` |  |
| 380 | 3 | `public void setWaterProduction(double water)` |  |
| 385 | 4 | `public void setWaterSources(double fresh, double desal)` | The two parts of that nameplate (0.7.59): the fresh plants' and the desalination plants'. |
| 391 | 3 | `public void setFreshCap(double cap)` | The fresh water limit, from the city's land and rights (Game.getFreshCap()); infinite for none. |
| 396 | 3 | `public void setBuildingWaterDraw(double water)` | Summed draw of every building standing, from the templates. |
| 401 | 3 | `public void setBilledWaterDraw(double water)` | The commercial + industrial slice, i.e. the part with a paying customer. |
| 410 | 3 | `public void setPopulation(long population)` | The people. |
| 414 | 3 | `public void setPricePerWaterUnit(double price)` |  |
| 420 | 6 | `public void updateEnergyRatio()` | passers calculators |
| 427 | 31 | `public void updateWaterRatio()` |  |
| 463 | 3 | `public double getUtilityIncome()` | Both utilities consolidated. |
| 474 | 52 | `public void updateUtilitiyWages(double[] wages, long[] electricityJobs, long[] waterJobs)` | NOTE: this now takes the electricity and water job arrays separately rather than one combined array. |
| 527 | 4 | `public void updateJobFillRate(double[] fillRate)` |  |
| 533 | 100 | `public void printUtilitiesInfo()` | printers |
| 637 | 4 | `static { ... }` |  |
| 644 | 9 | `public void redenominate(double scale)` | The utilities' prices and this month's bills, in the new unit. |

