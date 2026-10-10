# ManufacturingCheck.java - 550 lines · 7 methods · 3 constants · harnesses

`ham/citybuildersim/ManufacturingCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The ninth sector: what the city makes out of its own steel, and ships.
> 
> WHAT THIS HAS TO PROVE, and why none of it is "the city got bigger". The
> crime batch established that the long-run population of a city moves ten
> percent on perturbations of one part in a million, so no ensemble this
> harness could afford is evidence about a sector. Sixteen seeds of 333 years
> say the effect is real and enormous - 6/16 cities past forty thousand
> becoming 16/16, median 16,834 becoming 89,700 - and that measurement lives
> in claude/manufacturing.md where a number that will drift belongs. What is
> CHECKABLE is whether the thing works:
> 
>   1. the two goods are export-only, flow not stock, and clear at the floor
>   2. STEEL has a ceiling now, and it binds only when somebody here bids
>   3. the templates are the arithmetic the design claims, at every steel price
>   4. the traps: no gated post without a licence, and the staffing floor bites
>   5. the two brakes are different - fabrication dies on the steel price,
>      the machine works on the wage bill, and each is CAUSED here
>   6. it books like everybody else, the audit holds, and it reloads - and
>      since 0.7.83 (batch O6) it buys the oil on its line, lubricants at
>      the spec's rate a tonne made
> 
> Every fixture causes its condition. The steel test moves the steel price
> rather than waiting for a mill; the wage test moves the floor rather than
> hoping a run drifts.

**Uses:** [Good](Good.md) (67), [Game](Game.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (8), [Sectors](Sectors.md) (7), [Sector](Sector.md) (6), [JobType](JobType.md) (6), [Manufacturing](Manufacturing.md) (6), [GoodsMarket](GoodsMarket.md) (5), [BuildingManager](BuildingManager.md) (5), [GameFiles](GameFiles.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [BuildingType](BuildingType.md) (1), [PopulationManager](PopulationManager.md) (1), [Founding](Founding.md) (1), [SectorBooks](SectorBooks.md) (1), [Equity](Equity.md) (1), [PayTier](PayTier.md) (1)

## Sections

| line | section |
|---:|---|
| 67 | WHOSE COST IS IT - Sector.costShareOf(), guarded here because this is |
| 109 | · 1. two goods the city makes |
| 168 | · 2. and steel has a ceiling now |
| 189 | · 3. the templates are the design's arithmetic |
| 321 | · 4. the traps |
| 360 | · 5. the two brakes, each caused |
| 443 | · 6. the books, the audit, and a reload |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `ManufacturingCheck.SHOP` | `"Fabrication Shop"` |  |
| 64 | `ManufacturingCheck.WORKS` | `"Fabrication Works"` |  |
| 65 | `ManufacturingCheck.MACH` | `"Machine Works"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 39 | `static PrintStream out` |  |
| 40 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 515 | **type** `public class ManufacturingCheck` | The ninth sector: what the city makes out of its own steel, and ships. |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 9 | `static void check(String label, double actual, double expected, double tol)` |  |
| 57 | 5 | `static void quietly(Runnable r)` |  |

### WHOSE COST IS IT - Sector.costShareOf(), guarded here because this is (lines 67-550)

| line | len | member | says |
|---:|---:|---|---|
| 80 | 23 | `static void costSharesAddUp(Game g)` |  |
| 104 | 434 | `public static void main(String[] args) throws Exception` |  |
| 539 | 5 | `static double jobsOf(BuildingsTemplate t)` |  |
| 545 | 5 | `static double payrollOf(BuildingsTemplate t)` |  |

