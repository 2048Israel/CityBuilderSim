# BusinessServicesCheck.java - 386 lines · 5 methods · 3 constants · harnesses

`ham/citybuildersim/BusinessServicesCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> The sector whose customer is not in the city.
> 
> WHAT THIS HAS TO PROVE, and why it is mechanism rather than size: the crime
> batch established that the long-run population of a city moves ten percent on
> perturbations of one part in a million, so an eight-seed median cannot resolve
> a ten percent effect and "the city got bigger" is not evidence of anything.
> What is checkable is whether the thing WORKS:
> 
>   1. the three goods are export-only and clear at the world's floor
>   2. the templates are the arithmetic the design doc claims
>   3. the licence gate refuses, and stops refusing when the engineers exist
>   4. the wage bill is the brake - a city that pays more stops expanding
>   5. a strong currency closes the sector and a weak one reopens it
>   6. it books like every other sector, and survives a reload
> 
> Every fixture here CAUSES its condition. The licence test grants licences
> rather than waiting for a school; the currency test moves the rate rather
> than hoping a run drifts.

**Uses:** [Good](Good.md) (20), [JobType](JobType.md) (13), [Game](Game.md) (12), [Sectors](Sectors.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [Equity](Equity.md) (5), [BuildingManager](BuildingManager.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [GoodsMarket](GoodsMarket.md) (2), [GameFiles](GameFiles.md) (2), [BusinessServices](BusinessServices.md) (2), [TempDirs](TempDirs.md) (1), [PopulationManager](PopulationManager.md) (1), [PayTier](PayTier.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [SectorBooks](SectorBooks.md) (1), [Household](Household.md) (1)

## Sections

| line | section |
|---:|---|
| 66 | · 1. the goods: export only, and the floor is the price |
| 99 | · 2. the templates are the design's arithmetic |
| 141 | · · the trap this batch actually fell into, now a standing check |
| 172 | · 3. the licence gate |
| 212 | · 4/5. the wage bill and the currency are the brake |
| 283 | · 6. the books, and a reload |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BusinessServicesCheck.CONTACT` | `"Contact Centre"` | The three rungs, and the band each is meant to employ. |
| 58 | `BusinessServicesCheck.SHARED` | `"Shared Services Centre"` |  |
| 59 | `BusinessServicesCheck.OFFICE` | `"Engineering Services Office"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 31 | `static int fails` |  |
| 32 | `static PrintStream out` |  |
| 33 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 358 | **type** `public class BusinessServicesCheck` | The sector whose customer is not in the city. |
| 35 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 40 | 9 | `static void check(String label, double actual, double expected, double tol)` |  |
| 50 | 5 | `static void quietly(Runnable r)` |  |
| 61 | 319 | `public static void main(String[] args) throws Exception` |  |
| 381 | 5 | `static double jobsOf(BuildingsTemplate t)` |  |

