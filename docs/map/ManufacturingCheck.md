# ManufacturingCheck.java - 743 lines · 10 methods · 5 constants · harnesses

`ham/citybuildersim/ManufacturingCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

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
>   7. after a bleed sale, its own plant must cover running costs for the
>      recovery dial before a profitable new works is ordered (MR1)
> 
> Every fixture causes its condition. The steel test moves the steel price
> rather than waiting for a mill; the wage test moves the floor rather than
> hoping a run drifts.

**Uses:** [Good](Good.md) (70), [Game](Game.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (10), [Sectors](Sectors.md) (10), [Manufacturing](Manufacturing.md) (10), [Sector](Sector.md) (8), [BusinessInvestment](BusinessInvestment.md) (8), [PriceLevel](PriceLevel.md) (7), [JobType](JobType.md) (7), [BuildingManager](BuildingManager.md) (6), [GoodsMarket](GoodsMarket.md) (5), [GameFiles](GameFiles.md) (4), [HeavyIndustry](HeavyIndustry.md) (2), [PopulationManager](PopulationManager.md) (2), [Statement](Statement.md) (2), [TempDirs](TempDirs.md) (1), [BuildingType](BuildingType.md) (1), [Founding](Founding.md) (1), [SectorBooks](SectorBooks.md) (1), [Equity](Equity.md) (1), [SectorState](SectorState.md) (1), [OldMetal](OldMetal.md) (1), [PayTier](PayTier.md) (1)

## Sections

| line | section |
|---:|---|
| 86 | WHOSE COST IS IT - Sector.costShareOf(), guarded here because this is |
| 128 | · 1. two goods the city makes |
| 187 | · 2. and steel has a ceiling now |
| 209 | · 3. the templates are the design's arithmetic |
| 366 | · 4. the traps |
| 405 | · 5. the two brakes, each caused |
| 517 | · 6. the books, the audit, and a reload |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `ManufacturingCheck.SHOP` | `"Fabrication Shop"` |  |
| 71 | `ManufacturingCheck.WORKS` | `"Fabrication Works"` |  |
| 72 | `ManufacturingCheck.MACH` | `"Machine Works"` |  |
| 81 | `ManufacturingCheck.STEEL_WAS` | `1.284 * PriceLevel.FACTOR, FABRICATED_WAS = 2.21 * PriceLevel.FACTOR, MACHINE...` | The prices the three plants were costed on, written down (2026-09-13 to 0.7.119): steel landed at the US band, and the two goods made of it. |
| 84 | `ManufacturingCheck.STEEL_FALL` | `STEEL_WAS -.650 * PriceLevel.FACTOR` | Steel's fall a tonne at the import price on 0.7.120, the figure Q6b moved each good by: 1.284 to .650. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static int fails` |  |
| 41 | `static PrintStream out` |  |
| 42 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 706 | **type** `public class ManufacturingCheck` | The ninth sector: what the city makes out of its own steel, and ships. |
| 44 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 49 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 54 | 9 | `static void check(String label, double actual, double expected, double tol)` |  |
| 64 | 5 | `static void quietly(Runnable r)` |  |

### WHOSE COST IS IT - Sector.costShareOf(), guarded here because this is (lines 86-743)

| line | len | member | says |
|---:|---:|---|---|
| 99 | 23 | `static void costSharesAddUp(Game g)` |  |
| 123 | 512 | `public static void main(String[] args) throws Exception` |  |
| 637 | 82 | `static void expansionAfterBleed() throws Exception` | The fixture causes a lent bleed; new works remain profitable and staffable throughout the recovery. |
| 721 | 10 | `static void runningMonth(Manufacturing maker, BusinessInvestment plans, double running, double scrap)` | Close a controlled trading result through the same banking and history doors as the real month. |
| 732 | 5 | `static double jobsOf(BuildingsTemplate t)` |  |
| 738 | 5 | `static double payrollOf(BuildingsTemplate t)` |  |

