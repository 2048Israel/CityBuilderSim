# HousingInventory.java - 304 lines · 34 methods · 1 constants · model

`ham/citybuildersim/HousingInventory.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> Aggregate housing titles, occupants and listings over the physical stacks.
> WHY (RES2): listings and titles partition existing homes; neither creates
> structures. One equivalent service cohort per recipe is sufficient until
> HO1 introduces differing obligations. No row depends on represented count.

**Uses:** [HousingRecipe](HousingRecipe.md) (11), [BuildingsStacks](BuildingsStacks.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (13):** [BuildingManager](BuildingManager.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [FamilyModel](FamilyModel.md), [HousingChoice](HousingChoice.md), [HousingChoiceCheck](HousingChoiceCheck.md), [HousingInventoryCheck](HousingInventoryCheck.md), [HousingMarket](HousingMarket.md), [HousingMarketCheck](HousingMarketCheck.md), [InvestCheck](InvestCheck.md), [MonetaryCheck](MonetaryCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 19 | `HousingInventory.Owner.REAL_ESTATE` |  |
| 19 | `HousingInventory.Owner.HOUSEHOLD` |  |
| 19 | `HousingInventory.Owner.ESTATE` |  |
| 19 | `HousingInventory.Owner.OUTBOUND` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 18 | `HousingInventory.FOUNDING_PLACES` | `100` | The founding places are an endowment, without saleable front doors. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `public int recipe` |  |
| 31 | `public String dwelling` |  |
| 32 | `public Owner owner` |  |
| 33 | `public double owned, occupied, listedVacant, listedOccupied` |  |
| 44 | `private final Supplier<List<BuildingsStacks>> stacks` |  |
| 45 | `private final LongSupplier endowments` |  |
| 46 | `private final Map<Key, Map<Owner, Holding>> holdings` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 289 | **type** `public final class HousingInventory` | Aggregate housing titles, occupants and listings over the physical stacks. |
| 19 | 1 | **type** `public enum Owner` |  |
| 20 | 1 | **type** `public record Key(int recipe, String dwelling)` |  |
| 21 | 5 | **type** `public record Stock(Key key, HousingRecipe recipe, HousingRecipe.Dwelling dwelling, long structures, long p...` |  |
| 23 | 1 | `public double homes()` _(in HousingInventory.Stock)_ |  |
| 24 | 1 | `public double pipeline()` _(in HousingInventory.Stock)_ |  |
| 26 | 1 | **type** `public record Allocation(double structure, double land, double collateral)` |  |
| 29 | 14 | **type** `public static final class Holding` | A saved class/owner cohort. |
| 34 | 1 | `public Holding()` _(in HousingInventory.Holding)_ |  |
| 35 | 1 | `Holding(Key key, Owner owner)` _(in HousingInventory.Holding)_ |  |
| 36 | 6 | `Holding copy()` _(in HousingInventory.Holding)_ |  |
| 48 | 3 | `HousingInventory(Supplier<List<BuildingsStacks>> stacks, LongSupplier endowments)` |  |
| 53 | 11 | `public List<Stock> stock()` | Physical recipe/service cohorts, including sites; a read never caches or trades. |
| 65 | 4 | `private Stock stock(Key key)` |  |
| 69 | 4 | `private Holding held(Key key, Owner owner)` |  |
| 73 | 4 | `private Holding writable(Key key, Owner owner)` |  |
| 77 | 3 | `private static void quantity(double n)` |  |
| 80 | 1 | `private static double tolerance(double n)` |  |
| 82 | 13 | `public double owned(Key key, Owner owner)` |  |
| 95 | 4 | `public double occupied(Key key, Owner owner)` |  |
| 99 | 1 | `public double vacant(Key key, Owner owner)` |  |
| 100 | 4 | `public double listed(Key key, Owner owner, boolean occupied)` |  |
| 104 | 1 | `public long foundingCapacity()` |  |
| 105 | 5 | `public long homes()` |  |
| 110 | 5 | `public long pipelineHomes()` |  |
| 115 | 7 | `public long capacity()` |  |
| 123 | 15 | `public double ownedCapacity(Owner owner, boolean includePipeline)` | The RE portfolio includes its sites, but never the founding endowment. |
| 138 | 8 | `public long[] homesBySize()` |  |
| 148 | 9 | `public void occupy(Key key, Owner owner, double amount)` | Explicit occupancy input; listings cannot exceed either occupied or vacant stock. |
| 157 | 8 | `public void list(Key key, Owner owner, double vacant, double occupied)` |  |
| 167 | 16 | `public void transfer(Key key, Owner from, Owner to, double amount, double occupied)` | Atomic title transfer, retaining the stated occupied quantity; no money or physical work. |
| 185 | 9 | `public Allocation allocate(Key key, Owner owner, double amount, double structure, double land, double collateral)` | Fixed title weights partition actual project books/collateral for a later funded sale. |
| 198 | 25 | `public void match(double[] occupiedBySize)` | Legacy match input by size, distributed evenly across equivalent class doors. |
| 225 | 14 | `public void matchClasses(Map<Key, Double> occupied)` | Typed version of the monthly match, preserving conveyed occupants and listing subsets. |
| 241 | 9 | `boolean mayRemove(BuildingsStacks stack, long amount)` | Physical removal cannot destroy a title already conveyed to another owner. |
| 250 | 13 | `void physicalChanged(BuildingsStacks stack)` |  |
| 264 | 5 | `private void coalesce()` |  |
| 270 | 1 | `void clear()` | A new city clears assignments together with the physical stacks. |
| 271 | 5 | `public List<Holding> forSave()` |  |
| 277 | 27 | `public void restore(List<Holding> saved)` | Validates the whole saved matrix before replacing it; no reconstruction of economic flows. |

