# HouseholdFlowCheck.java - 702 lines · 21 methods · 4 constants · harnesses

`ham/citybuildersim/HouseholdFlowCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Money follows people: the households' balance carries each month's own flows
> - the retiring, the ageing on, the dead and the leavers - with the stocks
> they hold, before the census pool nets what is left (HS1).
> 
> WHY, AND WHAT THE OLD READING GOT WRONG. The pool sees only each cell's net
> change, so a retiring couple's savings stayed in their pay row for whoever
> joined it, a household coming into the retired row was handed whatever the
> strangers leaving other rows released, the survivor of a senior couple kept
> half, and a leaver's money stayed in the city whenever any cell grew. Every
> fixture below CAUSES one flow and puts a competing claimant in the same
> tier, a cell that grows by exactly the households the flow takes out of the
> row, so a pool that read the net change alone would hand the money to the
> wrong cell. Sections 1 to 7 run the month's census alone
> (HouseholdBalance.censusOnly()), with no month settled and nobody arriving
> with a buffer, so every figure is exact; section 8 counts the flows in a
> real city's months.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. A QUIET MONTH MOVES NOTHING. A balance holding stocks, no flow set and
>      a census equal to its counts comes out with every stock's total as it
>      was and every flow record at nought.
> 
>   2. A RETIREMENT AND AN AGEING ON CARRY THE CELL'S AVERAGE, stock by stock,
>      into the retired cell of the SAME FORMER TIER, and the cell they leave
>      keeps its average on fewer households.
> 
>   3. A PARTNER'S DEATH LEAVES THE SURVIVOR EVERYTHING: the household goes
>      whole to the alone cell of its row and tier, a working couple with a
>      child to the single parents; nothing is written off and no estate
>      passes.
> 
>   4. AN ESTATE REACHES THE TIER'S WORKING HOUSEHOLDS, the debt settled
>      from the savings, the shortfall and the student loan written off, the
>      rest passed in kind by grown-ups - and, with no heir of the tier, to
>      every working cell.
> 
>   5. A LEAVER TAKES THE AVERAGE OUT of the city, stock by stock, into the
>      record of each, and the evicted who give up leave the cell they were
>      taken from.
> 
>   6. EVERY STOCK IS CONSERVED, all the flows at once on one mixed fixture,
>      with a reshuffle no flow explains: what is held after, and what left
>      the city, is what was held before.
> 
>   7. THE ORPHANS WEIGH BY THEIR PEOPLE (OH1): a shrinking orphan cell's
>      stocks go to the city and no longer vanish, and a growing one claims
>      from the pool and makes nothing - but never a debt, a student loan or
>      a car (HS1's review).
> 
>   8. COUNTED IN A REAL RUN: the flows move savings in a city's months, and
>      the pool is left what no flow explains.

**Uses:** [FamilyStructure](FamilyStructure.md) (75), [PayTier](PayTier.md) (61), [Household](Household.md) (48), [HouseholdBalance](HouseholdBalance.md) (32), [UnemployedHousehold](UnemployedHousehold.md) (6), [LongPlaytest](LongPlaytest.md) (6), [AgeBand](AgeBand.md) (4), [GameFiles](GameFiles.md) (2), [Game](Game.md) (2), [Equity](Equity.md) (1), [TempDirs](TempDirs.md) (1), [PriceLevel](PriceLevel.md) (1)

## Sections

| line | section |
|---:|---|
| 241 | · 1. a quiet month moves nothing |
| 293 | · 2. a retirement and an ageing on carry the average |
| 377 | · 3. a partner's death leaves the survivor everything |
| 424 | · 4. an estate reaches the tier's working households |
| 484 | · 5. a leaver takes the average out |
| 533 | · 6. every stock is conserved |
| 583 | · 7. the orphans weigh by their people |
| 633 | · 8. counted in a real run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `HouseholdFlowCheck.TOLERANCE` | `1e-9` | How close two figures must be, relative to the larger of the want and one: a stock is carried as an average times a count, so it is exact to the cent far inside this. |
| 111 | `HouseholdFlowCheck.STOCK_NAMES` | `{ "savings", "debt", "shares, every company", "dollars abroad", "student loan...` | The stocks the conservation reads, named: the paper is left out (a bare fixture's leavers' paper has no record). |
| 112 | `HouseholdFlowCheck.STOCKS` | `List.of(c -> c.savings, c -> c.debt, HouseholdFlowCheck : : sharesOf, c -> c....` |  |
| 115 | `HouseholdFlowCheck.KINDS` | `{ "retiring", "ageing on", "partner dies", "dying alone", "leaving" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `static int fails` |  |
| 68 | `static PrintStream out` |  |
| 69 | `static PrintStream quiet` |  |
| 178 | `final HouseholdBalance bal` |  |
| 179 | `private final double[][] census` |  |
| 180 | `private final Map<Household, Double> outside` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 65 | 638 | **type** `public class HouseholdFlowCheck` | Money follows people: the households' balance carries each month's own flows - the retiring, the ageing on, the dead and the leavers - with the stocks they hold, before the census pool nets what is left (HS1). |
| 74 | 5 | `static void check(String label, double got, double want)` |  |
| 80 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 85 | 5 | `static void quietly(Runnable r)` |  |
| 92 | 5 | `static double across(HouseholdBalance b, ToDoubleFunction<Household> perHousehold)` | One stock across every cell: per household times households. |
| 99 | 3 | `static double total(Household c, ToDoubleFunction<Household> stock)` | One cell's total of a stock. |
| 104 | 5 | `static double sharesOf(Household c)` | Every company's shares in one cell, a household's. |
| 118 | 11 | `static Household holds(Household c, double savings, double debt, double shares, double abroad, double loans, double cars, doubl...` | What the cell holds, a household's of each stock. |
| 131 | 3 | `static Household fill(Household c, double k)` | A cell holding some of everything, in proportion to k. |
| 136 | 5 | `static double[] held(HouseholdBalance b)` | What the city holds of each stock in STOCKS. |
| 142 | 5 | `static double sharesTakenAway(HouseholdBalance b)` |  |
| 149 | 9 | `static double[] left(HouseholdBalance b)` | What left the city of each stock in STOCKS, the estates' repayments among the savings and the debt. |
| 160 | 8 | `static boolean nothingElseIn(HouseholdBalance b, FamilyStructure shape, Household...except)` | True when no cell of the shape, but the ones named, holds a household or a stock. |
| 177 | 53 | **type** `static final class Town` | A fixture: a balance, and the census the month ends on. |
| 183 | 7 | `Household cell(FamilyStructure shape, PayTier tier, double households)` _(in HouseholdFlowCheck.Town)_ | A family cell of this many households, expected to be as many at the census. |
| 192 | 4 | `Town expect(FamilyStructure shape, PayTier tier, double households)` _(in HouseholdFlowCheck.Town)_ | The census's count for a working cell. |
| 198 | 4 | `Town expectRetired(FamilyStructure shape, double total)` _(in HouseholdFlowCheck.Town)_ | ...a retired shape's total, every former tier together. |
| 204 | 4 | `Household outsideCell(Household c, double households)` _(in HouseholdFlowCheck.Town)_ | An outside cell of this many households, expected to be as many. |
| 210 | 4 | `Town expectOutside(Household c, double households)` _(in HouseholdFlowCheck.Town)_ | The census's count for an outside cell. |
| 216 | 7 | `Town drift(double factor)` _(in HouseholdFlowCheck.Town)_ | Every count the census holds, scaled: the reshuffles no flow explains. |
| 225 | 4 | `void run()` _(in HouseholdFlowCheck.Town)_ | The month's census alone, as the balance reads it. |
| 232 | 3 | `static boolean isZero(double x)` | True for a figure that is nought to the tolerance. |
| 236 | 466 | `public static void main(String[] args) throws Exception` |  |

