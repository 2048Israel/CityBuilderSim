# UtilitiesHandler.java - 503 lines · 45 methods · 3 constants · model

`ham/citybuildersim/UtilitiesHandler.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Used by (6):** [ConservationCheck](ConservationCheck.md), [MoneyAudit](MoneyAudit.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 22 | WATER SUPPLY |
| 107 | · READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is |
| 127 | · Split books. The two utilities share one workforce and one fill rate, but |
| 390 | · · ELECTRIC POWER |
| 421 | · · WATER |
| 457 | · · CONSOLIDATED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `UtilitiesHandler.BASE_WATER_SUPPLY` | `8000` | What the city can draw before it builds anything: the legacy wells and the old municipal intake. |
| 49 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 484 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `public double production` |  |
| 18 | `public double baseProduction` |  |
| 19 | `public double consumption` |  |
| 20 | `public double energyRatio` |  |
| 57 | `private double pricePerWaterUnit` | $50 per unit, i.e. $5 per 1,000 gallons. |
| 59 | `public double waterProduction` |  |
| 60 | `public double baseWaterProduction` |  |
| 61 | `public double buildingWaterDraw` |  |
| 62 | `public double residentWaterDraw` |  |
| 69 | `public double billedWaterDraw` | The slice of demand that is actually invoiced: commercial and industrial buildings. |
| 70 | `public double waterConsumption` |  |
| 71 | `public double waterRatio` |  |
| 74 | `private double[] utilityWages` | jobs |
| 75 | `private int[] utilityJobs` |  |
| 79 | `private double[] electricityWages` | Same payroll, split by which utility the job belongs to, so the report can show two businesses rather than one lump. |
| 80 | `private double[] waterWages` |  |
| 81 | `private double[] fillRate` |  |
| 83 | `private double averageUtilityFill` |  |
| 86 | `private double pricePerWatt` | temporary |
| 171 | `private boolean billedByStatement` | WHAT THE CUSTOMERS WERE ACTUALLY CHARGED, since 2026-09-06. |
| 172 | `private double billedElectricityRevenue` |  |
| 173 | `private double billedWaterRevenue` |  |
| 182 | `public double billedElectricityDraw` | What the four charged categories draw. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 15 | 489 | **type** `public class UtilitiesHandler` |  |

### WATER SUPPLY (lines 22-106)

| line | len | member | says |
|---:|---:|---|---|
| 88 | 3 | `public UtilitiesHandler()` |  |
| 93 | 4 | `public void updateUtilitiesHandler()` | updaters |
| 99 | 3 | `public double getEnergyRatio()` | getters |
| 103 | 3 | `public double getPricerPerWatt()` |  |

### READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is (lines 107-126)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 1 | `public double getProduction()` |  |
| 113 | 1 | `public double getBaseProduction()` |  |
| 114 | 1 | `public double getConsumption()` |  |
| 115 | 1 | `public double getAverageUtilityFill()` |  |
| 117 | 1 | `public double getWaterProduction()` |  |
| 118 | 1 | `public double getBaseWaterProduction()` |  |
| 119 | 1 | `public double getWaterConsumption()` |  |
| 120 | 1 | `public double getBuildingWaterDraw()` |  |
| 121 | 1 | `public double getResidentWaterDraw()` |  |
| 122 | 1 | `public double getBilledWaterDraw()` |  |
| 123 | 1 | `public double getUnbilledWaterDraw()` |  |
| 124 | 1 | `public double getWaterRatio()` |  |
| 125 | 1 | `public double getPricePerWaterUnit()` |  |

### Split books. The two utilities share one workforce and one fill rate, but (lines 127-503)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 4 | `public double getElectricityRevenue()` | The month's electricity bill - only the draw somebody is actually invoiced for, and only the fraction delivered. |
| 175 | 5 | `public void setBilledRevenue(double electricity, double water)` |  |
| 184 | 3 | `public void setBilledElectricityDraw(double draw)` |  |
| 187 | 1 | `public double getBilledElectricityDraw()` |  |
| 188 | 3 | `public double getUnbilledElectricityDraw()` |  |
| 198 | 4 | `public double getWaterRevenue()` | Only the billed slice, and only the fraction actually delivered - during rationing customers receive waterRatio of what they asked for and are charged for that, which is also exactly what the commercial and industrial... |
| 211 | 3 | `public double getElectricityPayroll()` | PER JOB TYPE since 0.7.17, as every employer's payroll is: each type's wage for the posts of that type filled, which is what the households in them are paid. |
| 215 | 3 | `public double getWaterPayroll()` |  |
| 220 | 6 | `private double staffed(double[] bill)` | A wage bill by job type, at each type's own fill. |
| 227 | 3 | `public double getElectricityIncome()` |  |
| 231 | 3 | `public double getWaterIncome()` |  |
| 235 | 5 | `private static double sum(double[] a)` |  |
| 241 | 3 | `public double getUtilityPayroll()` |  |
| 245 | 3 | `public double getUtilityRevenue()` |  |
| 250 | 3 | `public void setWattsProduction(double watts)` | setters |
| 254 | 4 | `public void setWattsConsumption(double watts)` |  |
| 259 | 3 | `public void setWaterProduction(double water)` |  |
| 264 | 3 | `public void setBuildingWaterDraw(double water)` | Summed draw of every building standing, from the templates. |
| 269 | 3 | `public void setBilledWaterDraw(double water)` | The commercial + industrial slice, i.e. the part with a paying customer. |
| 278 | 3 | `public void setPopulation(int population)` | The people. |
| 282 | 3 | `public void setPricePerWaterUnit(double price)` |  |
| 288 | 6 | `public void updateEnergyRatio()` | passers calculators |
| 295 | 16 | `public void updateWaterRatio()` |  |
| 316 | 3 | `public double getUtilityIncome()` | Both utilities consolidated. |
| 327 | 52 | `public void updateUtilitiyWages(double[] wages, int[] electricityJobs, int[] waterJobs)` | NOTE: this now takes the electricity and water job arrays separately rather than one combined array. |
| 380 | 4 | `public void updateJobFillRate(double[] fillRate)` |  |
| 386 | 96 | `public void printUtilitiesInfo()` | printers |
| 486 | 4 | `static { ... }` |  |
| 493 | 9 | `public void redenominate(double scale)` | The utilities' prices and this month's bills, in the new unit. |

