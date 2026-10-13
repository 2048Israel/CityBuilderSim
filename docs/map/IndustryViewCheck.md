# IndustryViewCheck.java - 941 lines · 29 methods · 4 constants · harnesses

`ham/citybuildersim/IndustryViewCheck.java` - generated 2026-10-13 by CodeMap; line numbers are as of that run.

> The materials pictograms' model, checked (0.7.141, batch I11;
> docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's
> I11 row): IndustryView's figures held to the model's own reads in three
> played towns built to cause each part of the picture, and three at their
> founding where the plans order.
> 
> WHY. The nine materials industries' Operations pages draw IndustryView's
> figures (batch I12): the fields as vessels, the feeds by where they came
> from, each kind of works with its run and its gate, the products and who
> took them, the power, the forest. A feed drawn from a seller that sold
> nothing, a product whose parts do not add to what was made, a gate in
> words the plan never says, or a view that wrote to the city it read would
> be a confident wrong picture. The screen is checked by eye on the PC; this
> holds what it draws.
> 
> What this has to prove:
>   1. THE NINE: the view covers the nine industries and no other, each
>      under its roof, and reads every one of them in every town.
>   2. THE FIELDS: mines on typed fields - the works town's thermal coal and
>      hematite and limestone by fiat beside its founding coking field, the
>      copper town's sulphide by fiat beside its oxide field: each vessel is
>      the ground's own reads by type, its sites worked the buildings'
>      claims; the forest's vessel what stands of it.
>   3. EVERY GOOD BALANCES: in every industry of every town, each product's
>      made, kept, sold at home, shipped, carried and lost foots to what its
>      warehouse moved over the month, measured before and after, in units
>      and in tonnes - and the industry's tonnes with it; each feed's parts
>      add to what its works received, and what they received is what they
>      needed.
>   4. EVERY FIGURE IS THE MODEL'S: a product's and a feed's figures are its
>      rows'; its buyers and sellers the month's fills, adding to the rows;
>      every kind's count, one-more appraisal, last earnings and idle months
>      its sector's own; the kind the view would order is what the plan
>      orders.
>   5. FROM WHERE: a works on imported feed (the smelter's concentrate), a
>      captive chain (coal to coke to the blast furnace, the sawmill's
>      residues to the pellet and pulp mills, anode to cathode to rod), old
>      metal bought (steel at the yard, copper at the scrap refinery), and
>      goods sold at home across industries (ore, coal, limestone, acid,
>      sawlogs), limestone no buyer took carried into aggregate.
>   6. THE POWER: made less drawn is sold less bought, every industry; the
>      coke ovens sell their surplus and the pulp mill its 13,500 kW.
>   7. THE FOREST: a forest with a crew - each figure the forest's and
>      Forestry's own; it settles where Forestry.settlesAt() says with the
>      law and at the regrowth rule's depletion without; with 40% protected,
>      at no less than 40%.
>   8. THE WORDS AND COLOURS: every good the nine make or use has a colour of
>      its own; every kind, vessel, part and line has its words.
>   9. PURE: read twice, the same view; reading it makes no production row;
>      the save before a read and after it is the same, byte for byte, but
>      for its time stamp.
>  10. AFTER A LOAD: a saved town's twin reads its month as not counted
>      until one runs.
>  11. THE PLAN'S ORDER: in towns at their founding where kinds pass every
>      gate - a longwall's coal unwashed, a forest with no crew, ore with no
>      mine - each sector's plan orders the kind the view would.
> 
> Every fixture causes its condition.

**Uses:** [IndustryView](IndustryView.md) (206), [Good](Good.md) (61), [Game](Game.md) (29), [Resource](Resource.md) (17), [Sectors](Sectors.md) (10), [Sector](Sector.md) (9), [SpreadPlanner](SpreadPlanner.md) (8), [CoalMining](CoalMining.md) (7), [Forestry](Forestry.md) (7), [Deposit](Deposit.md) (7), [HeavyIndustry](HeavyIndustry.md) (6), [BusinessInvestment](BusinessInvestment.md) (6), [LandManager](LandManager.md) (5), [BuildingManager](BuildingManager.md) (5), [OldMetal](OldMetal.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [CopperRefining](CopperRefining.md) (4), [WoodProducts](WoodProducts.md) (4), [SectorFlow](SectorFlow.md) (4), [Quarrying](Quarrying.md) (3), [MaterialsCheck](MaterialsCheck.md) (3), [GameFiles](GameFiles.md) (2), [CoalMiningCheck](CoalMiningCheck.md) (2), [CopperMining](CopperMining.md) (2), [ForestryCheck](ForestryCheck.md) (2), [ExtractionSector](ExtractionSector.md) (2), [ProcessingSector](ProcessingSector.md) (2), [Trade](Trade.md) (2), [LongPlaytest](LongPlaytest.md) (1), [Mining](Mining.md) (1)... and 3 more

## Sections

| line | section |
|---:|---|
| 158 | the towns |
| 255 | 1. THE NINE |
| 297 | 2. THE FIELDS |
| 370 | 3. EVERY GOOD BALANCES |
| 423 | 4. EVERY FIGURE IS THE MODEL'S |
| 585 | 5. FROM WHERE |
| 691 | 6. THE POWER |
| 719 | 7. THE FOREST |
| 764 | 8. THE WORDS AND COLOURS |
| 837 | 9. PURE |
| 880 | 11. THE PLAN'S ORDER |
| 923 | 10. AFTER A LOAD |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 117 | `IndustryViewCheck.SETTLE_MONTHS` | `2` | Months a town plays before the month it is read on. |
| 120 | `IndustryViewCheck.OLD_STEEL` | `14_000` | Old metal arisen before the month read, tonnes before recovery: enough for one Scrap Yard's 10,500 t and some to landfill. |
| 122 | `IndustryViewCheck.OLD_COPPER` | `4_000` | ...and old copper, for one Scrap Copper Refinery's 4,400 t. |
| 127 | `IndustryViewCheck.FILES` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `static int fails` |  |
| 92 | `static PrintStream out` |  |
| 93 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 89 | 853 | **type** `public class IndustryViewCheck` | The materials pictograms' model, checked (0.7.141, batch I11; docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's I11 row): IndustryView's figures held to the model's own reads in three played town... |
| 95 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 100 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 105 | 5 | `static void quietly(Runnable r)` |  |
| 112 | 3 | `static boolean near(double a, double b)` | Within a millionth of the larger, or of one: the rows are sums of many fills. |
| 125 | 1 | **type** `record Town(String name, Game game, Map<String, Map<Good, Double>> stockBefore)` | A town, what its industries' stock was before the month read, and its files. |
| 129 | 28 | `public static void main(String[] args) throws Exception` |  |

### the towns (lines 158-254)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 18 | `static Town works()` | The works town: CoalMiningCheck's coal town (its founding coking field and a thermal site by fiat), a hematite and a limestone site by fiat on it, and a chain on them - an Iron Mine, a Surface and an Underground Coal ... |
| 195 | 13 | `static Town copper()` | The copper town: CopperMiningCheck's (its founding oxide field and a sulphide site by fiat) with a Copper Mine & Mill and a Leach & SX-EW Plant on them, and every works of Copper Refining - a smelter far bigger than t... |
| 210 | 11 | `static Town wood()` | The wood town: ForestryCheck's forest town with a Logging Camp on its forest, and a Sawmill, a Pulp Mill and a Wood Pellet Mill. |
| 223 | 17 | `static Town play(String name, Game g, Runnable each)` | Hands a town its workers, plays SETTLE_MONTHS, notes every industry's stock, and plays the month it is read on - `each` before every month. |
| 241 | 13 | `static void printTown(Town t)` |  |

### 1. THE NINE (lines 255-296)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 39 | `static void theNine(List<Town> towns)` |  |

### 2. THE FIELDS (lines 297-369)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 70 | `static void theFields(Town works, Town copper, Town wood)` |  |

### 3. EVERY GOOD BALANCES (lines 370-422)

| line | len | member | says |
|---:|---:|---|---|
| 372 | 50 | `static void everyGoodBalances(List<Town> towns)` |  |

### 4. EVERY FIGURE IS THE MODEL'S (lines 423-584)

| line | len | member | says |
|---:|---:|---|---|
| 425 | 81 | `static void everyFigure(List<Town> towns)` |  |
| 508 | 7 | `static boolean sold(Game g, Good good, String key, List<IndustryView.Part> parts)` | Whether `parts` are exactly the month's fills of `g` that `key` sold in the city, by buyer. |
| 517 | 7 | `static boolean bought(Game g, Good good, String key, List<IndustryView.Part> parts)` | ...and the fills `key` bought in the city, by seller. |
| 525 | 9 | `static boolean matches(Map<String, Double> by, List<IndustryView.Part> parts)` |  |
| 536 | 5 | `static boolean same(SpreadPlanner.Candidate a, SpreadPlanner.Candidate b)` | Whether two appraisals of a building are the same: its template, earnings, cost, first gate and every refusal. |
| 542 | 3 | `static boolean same(double a, double b)` |  |
| 553 | 31 | `static String planAgrees(Game g, IndustryView.View v, int[] tally)` | Whether the sector's plan orders what the view says it would (null) or why not, `tally` counting {ordered, none, waiting}: with an order in flight the plan waits; Coal Mining washes first (planWash()), so a passing wa... |

### 5. FROM WHERE (lines 585-690)

| line | len | member | says |
|---:|---:|---|---|
| 587 | 103 | `static void fromWhere(Town works, Town copper, Town wood)` |  |

### 6. THE POWER (lines 691-718)

| line | len | member | says |
|---:|---:|---|---|
| 693 | 25 | `static void thePower(List<Town> towns, Town works, Town wood)` |  |

### 7. THE FOREST (lines 719-763)

| line | len | member | says |
|---:|---:|---|---|
| 721 | 42 | `static void theForest(Town wood)` |  |

### 8. THE WORDS AND COLOURS (lines 764-836)

| line | len | member | says |
|---:|---:|---|---|
| 766 | 70 | `static void wordsAndColours(List<Town> towns)` |  |

### 9. PURE (lines 837-879)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 16 | `static void pure(Town town) throws Exception` |  |
| 857 | 13 | `static String words(Game g)` | Every view of the town and its words, as text. |
| 872 | 7 | `static String saved(Game g, int slot) throws Exception` | The town saved to a slot of its scratch folder, as text without the save's time stamp. |

### 11. THE PLAN'S ORDER (lines 880-922)

| line | len | member | says |
|---:|---:|---|---|
| 889 | 33 | `static void thePlansOrder()` | Towns at their founding, where kinds pass every gate - a sector with nothing running weighs one more at nameplate - and each plan orders: CoalMiningCheck's longwall with its raw coal unwashed, ForestryCheck's forest w... |

### 10. AFTER A LOAD (lines 923-941)

| line | len | member | says |
|---:|---:|---|---|
| 925 | 16 | `static void afterALoad(Town town)` |  |

