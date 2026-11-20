# FitoutCheck.java - 754 lines · 32 methods · 3 constants · harnesses

`ham/citybuildersim/FitoutCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> A new building's equipment is chosen and priced at its order, kept on its site, saved, and delivered at its finish (R1e):
> Fitout, BuildingsStacks' lots, Game.fitWhatFinished().
> 
> WHY (R1e, 2026-11-19; docs/queue/R1e.md, R1b-R's call 3 left open by R1d). A new building paid its template's whole cost at
> the order and what it came with was decided at its finish, so a building that came without a piece had paid for it, and
> no discount could be given while the finish might deliver another set than the one priced. Now the order's quote chooses
> the set (the pieces EquipmentFit.fittedForNew() keeps then), prices the shell and those pieces, the site keeps the set, the
> save carries it, and the finish delivers it; a discovery or a price change after the order is a new fitting decision.
> 
> No expected value here comes from Fitout. The split is the harness's own reading of the catalogue (R1c call 10: a piece
> with work buys its goods at the table's import price as a share of the template's founding cash, and its work is its share
> of the points; a piece with no work is carried in and in no price), the price is BuildingManager.nonMaterialCost()'s
> arithmetic written out here on that shell, the set is EquipmentFit.fittedForNew() asked at the order, and what a building
> came with is the stack's unfitted counts after the month's construction statements ran (SimulationEngine's order: the
> sites, the completions, the finish, the material, the work recognised, the contracts settled, the hand's events).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. The split, sourced: every piece with work in the catalogue is its goods at the import price over its template's
>      founding cash and its work, the 25% pieces a quarter (the Vehicle Works's carbide tools, the Grain Farm's combine), a
>      carried-in piece no part of the price, no shell below nothing; the whole template is its own cash and points to the
>      bit, and the split does not move with the machinery market, which a fitting quote does.
>   2. A city founded the default way: every template's fitout is the whole template and chosen by no one; an order there
>      is priced as before (nonMaterialCost(t) on the template's points), leaves no lot on site and saves no key.
>   3. Two orders for the same template, with and without a piece: the quote, the investors' estimate and the money test
>      price each order's own set (the planners and the invoice agree), the site keeps each order's set front first, and
>      it owes each building the points its set takes.
>   4. A discovery and a price change while both are on site: they move a new order's set, not the orders on site; the
>      site finishes first placed first, and each order's buildings come with the set it froze; the loose (no lot) are
>      decided at the finish as before.
>   5. A save and load mid-build: the lots come back exactly, the twins finish alike; a save without the key (every save
>      from before R1e) reads its sites as the whole template, decided at the finish, as main runs it: no format bump.
>   6. The builders' actual receipts, investment and VAT entries: what the payer paid, the builders' order book and points,
>      the contract, the work recognised as investment and the owner's capital purchases for its tax credit are each the
>      order's own set's price.
>   7. A stopped site's shell keeps its orders' sets, its restart is priced on them, and the restarted site delivers them.
>   8. R1d's rules kept: the builders are credited what the treasury paid for an order, and a building delivered without a
>      piece, given a fitting job and then scrapped, refunds nothing while an unfitted building is left for the job.

**Uses:** [Game](Game.md) (37), [Fitout](Fitout.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (16), [BuildingsStacks](BuildingsStacks.md) (16), [BuildingManager](BuildingManager.md) (12), [Equipment](Equipment.md) (9), [Good](Good.md) (9), [RecipeCheck](RecipeCheck.md) (6), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (4), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (4), [Founding](Founding.md) (2), [Sectors](Sectors.md) (2), [GameFiles](GameFiles.md) (2), [Construction](Construction.md) (2), [ConstructionControl](ConstructionControl.md) (2), [BuildersShort](BuildersShort.md) (2), [FittingCheck](FittingCheck.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [Forms1900Check](Forms1900Check.md) (2), [EquipmentFit](EquipmentFit.md) (1), [ResearchCatalog](ResearchCatalog.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [Sector](Sector.md) (1)

## Sections

| line | section |
|---:|---|
| 84 | · the harness's own split and price |
| 185 | · the 1900 farm town |
| 270 | · 1 |
| 359 | · 2 |
| 393 | · 3 |
| 466 | · 4 |
| 527 | · 5 |
| 585 | · 6 |
| 627 | · 7 |
| 669 | · 8 |
| 697 | · 9 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `FitoutCheck.out` | `System.out` |  |
| 69 | `FitoutCheck.COMBINE` | `"combine", KIT = "autonomy-kit"` |  |
| 71 | `FitoutCheck.KIT_ENTRY` | `"FE8"` | The autonomy kit's discovery: until it is found a new farm's kit is NOT_KNOWN (a near-future entry is known when found, not by a date). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 65 | `static int fails, checks` |  |
| 66 | `static boolean quietRun` |  |
| 189 | `Game game` |  |
| 190 | `BuildingsTemplate farm` |  |
| 191 | `BuildingsStacks stack` |  |
| 192 | `double cheap` |  |
| 245 | `Set<String> offA, offB` |  |
| 246 | `Game.BuildQuote quoteA, quoteB` |  |
| 247 | `boolean placed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 691 | **type** `public final class FitoutCheck` | A new building's equipment is chosen and priced at its order, kept on its site, saved, and delivered at its finish (R1e): Fitout, BuildingsStacks' lots, Game.fitWhatFinished(). |
| 73 | 6 | `static void check(String label, boolean ok, String detail)` |  |
| 80 | 1 | `static void section(String title)` |  |
| 82 | 1 | `static boolean near(double a, double b)` |  |

### the harness's own split and price (lines 84-184)

| line | len | member | says |
|---:|---:|---|---|
| 87 | 6 | `static double share(BuildingsTemplate t, Equipment piece)` | A piece's part of its template's cash, read off the catalogue (R1c call 10): its goods at the import price over the founding cash, for a piece with work; none for one carried in. |
| 95 | 9 | `static double goodsAt(Equipment piece)` | A built-in piece's goods at the import price, in founding money; 0 for one carried in. |
| 111 | 13 | `static double[] shell(BuildingsTemplate t, Set<String> off)` | One building's cash and points without these pieces, from the catalogue in money (REV-R1e): the founding cash less every built-in piece's goods is the frame (its labour, its own goods, the rest), the same a point in e... |
| 126 | 8 | `static double nonMaterial(Game g, double cash, double points)` | BuildingManager.nonMaterialCost()'s arithmetic written out: the cash, its founding labour (the points at the founding wage a point, never more than the cash over the expected level) taken out at the level and put back... |
| 136 | 3 | `static double buildingsPart(Game.BuildQuote q)` | A quote's work for its buildings alone: its sticker less the pad, the works, the span and the deep water, which no fitout moves but by the multiple. |
| 141 | 4 | `static double expectedPart(Game g, BuildingsTemplate t, Set<String> off, long n)` | What an order of n of t with these pieces off should be quoted for its buildings: the shell's price at the multiple on its points. |
| 147 | 6 | `static Set<String> rejected(Game g, BuildingsTemplate t)` | The pieces a new building of t would not get now, by the rule the finish asked before R1e. |
| 154 | 5 | `static String lots(BuildingsStacks s)` |  |
| 160 | 1 | `static String lot(long n, Set<String> off)` |  |
| 162 | 5 | `static Map<String, Long> unfitted(BuildingsStacks s)` |  |
| 169 | 15 | `static List<BuildingManager.Completion> work(Game g, int points)` | The month's construction statements in SimulationEngine's order on `points` of site output; the completions. |

### the 1900 farm town (lines 185-269)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 6 | **type** `static final class Farms` | A 1900 town where every discovery but the autonomy kit's is found, past the combine's date, with two builders' depots, their yard full (no material is bought, so no escalation). |
| 195 | 25 | `static Farms farmTown(String label) throws Exception` |  |
| 221 | 4 | `static void cheap(Farms f)` |  |
| 226 | 4 | `static void dear(Farms f)` |  |
| 231 | 5 | `static boolean cityOrder(Game g, BuildingsTemplate t, long n)` |  |
| 237 | 5 | `static boolean farmersOrder(Game g, BuildingsTemplate t, long n)` |  |
| 244 | 5 | **type** `static final class Two` | Both orders on site: A, two farms with every piece that pays at cheap machinery; B, three at dear machinery. |
| 250 | 3 | `static Two placeBoth(Farms f)` |  |
| 255 | 14 | `static Two placeBoth(Farms f, boolean farmersB)` | ...B the farmers' order, or the city's (a site the city may stop is the city's alone). |

### 1 (lines 270-358)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 84 | `static void splitSourced() throws Exception` |  |
| 357 | 1 | `static boolean apart(double a, double b)` |  |

### 2 (lines 359-392)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 31 | `static void defaultCity() throws Exception` |  |

### 3 (lines 393-465)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 70 | `static void twoOrders() throws Exception` |  |

### 4 (lines 466-526)

| line | len | member | says |
|---:|---:|---|---|
| 468 | 58 | `static void discoveryOnSite() throws Exception` |  |

### 5 (lines 527-584)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 55 | `static void saveMidBuild() throws Exception` |  |

### 6 (lines 585-626)

| line | len | member | says |
|---:|---:|---|---|
| 587 | 39 | `static void receipts() throws Exception` |  |

### 7 (lines 627-668)

| line | len | member | says |
|---:|---:|---|---|
| 629 | 39 | `static void shell() throws Exception` |  |

### 8 (lines 669-696)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 25 | `static void r1dKept() throws Exception` |  |

### 9 (lines 697-754)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 30 | `static void runInvoice() throws Exception` |  |
| 730 | 24 | `public static void main(String[] args) throws Exception` |  |

