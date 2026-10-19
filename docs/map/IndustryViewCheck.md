# IndustryViewCheck.java - 1,218 lines · 32 methods · 6 constants · harnesses

`ham/citybuildersim/IndustryViewCheck.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The materials pictograms' model, checked (0.7.141, batch I11;
> docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's
> I11 row): IndustryView's figures held to the model's own reads in three
> played towns built to cause each part of the picture, and three at their
> founding where the plans order; and since 0.7.143 (batch I12)
> IndustryPicture's layout of those figures, to scale and on the frame.
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
>   9. PURE: read twice, the same view, picture and strip; reading it makes
>      no production row; the save before a read and after it is the same,
>      byte for byte, but for its time stamp.
>  10. AFTER A LOAD: a saved town's twin reads its month as not counted,
>      its picture empty, until one runs.
>  11. THE PLAN'S ORDER: in towns at their founding where kinds pass every
>      gate - a longwall's coal unwashed, a forest with no crew, ore with no
>      mine - each sector's plan orders the kind the view would.
>  12. THE PICTURE (0.7.143, batch I12; IndustryPicture): every industry the
>      towns draw, at the 1,389 x 868 window's width - its ribbons to scale,
>      each pair's its flows' quantity times one scale and each node's the
> ... (4 more lines in the source)

**Uses:** [IndustryView](IndustryView.md) (245), [Good](Good.md) (68), [IndustryPicture](IndustryPicture.md) (60), [Game](Game.md) (31), [Resource](Resource.md) (18), [RefineryView](RefineryView.md) (16), [Sectors](Sectors.md) (11), [Sector](Sector.md) (9), [SpreadPlanner](SpreadPlanner.md) (8), [CoalMining](CoalMining.md) (7), [HeavyIndustry](HeavyIndustry.md) (7), [Forestry](Forestry.md) (7), [Deposit](Deposit.md) (7), [BusinessInvestment](BusinessInvestment.md) (6), [LandManager](LandManager.md) (5), [BuildingManager](BuildingManager.md) (5), [OldMetal](OldMetal.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [CopperRefining](CopperRefining.md) (4), [WoodProducts](WoodProducts.md) (4), [SectorFlow](SectorFlow.md) (4), [Quarrying](Quarrying.md) (3), [MaterialsCheck](MaterialsCheck.md) (3), [GameFiles](GameFiles.md) (2), [CoalMiningCheck](CoalMiningCheck.md) (2), [CopperMining](CopperMining.md) (2), [ForestryCheck](ForestryCheck.md) (2), [ExtractionSector](ExtractionSector.md) (2), [ProcessingSector](ProcessingSector.md) (2), [Trade](Trade.md) (2)... and 4 more

## Sections

| line | section |
|---:|---|
| 165 | the towns |
| 262 | 1. THE NINE |
| 304 | 2. THE FIELDS |
| 377 | 3. EVERY GOOD BALANCES |
| 430 | 4. EVERY FIGURE IS THE MODEL'S |
| 592 | 5. FROM WHERE |
| 698 | 6. THE POWER |
| 726 | 7. THE FOREST |
| 771 | 8. THE WORDS AND COLOURS |
| 844 | 9. PURE |
| 888 | 11. THE PLAN'S ORDER |
| 931 | 10. AFTER A LOAD |
| 953 | 12. THE PICTURE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 123 | `IndustryViewCheck.SETTLE_MONTHS` | `2` | Months a town plays before the month it is read on. |
| 126 | `IndustryViewCheck.OLD_STEEL` | `14_000` | Old metal arisen before the month read, tonnes before recovery: enough for one Scrap Yard's 10,500 t and some to landfill. |
| 128 | `IndustryViewCheck.OLD_COPPER` | `4_000` | ...and old copper, for one Scrap Copper Refinery's 4,400 t. |
| 133 | `IndustryViewCheck.FILES` | `new ArrayList<>()` |  |
| 956 | `IndustryViewCheck.FRAME_WIDTH` | `1_200` | The picture's width at the 1,389 x 868 window: the Operations card's inside (RefineryViewCheck's 1,200). |
| 959 | `IndustryViewCheck.PX` | `1e-6` | The slack a laid-out pixel is held to. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 97 | `static int fails` |  |
| 98 | `static PrintStream out` |  |
| 99 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 95 | 1124 | **type** `public class IndustryViewCheck` | The materials pictograms' model, checked (0.7.141, batch I11; docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's I11 row): IndustryView's figures held to the model's own reads in three played town... |
| 101 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 106 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 111 | 5 | `static void quietly(Runnable r)` |  |
| 118 | 3 | `static boolean near(double a, double b)` | Within a millionth of the larger, or of one: the rows are sums of many fills. |
| 131 | 1 | **type** `record Town(String name, Game game, Map<String, Map<Good, Double>> stockBefore)` | A town, what its industries' stock was before the month read, and its files. |
| 135 | 29 | `public static void main(String[] args) throws Exception` |  |

### the towns (lines 165-261)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 18 | `static Town works()` | The works town: CoalMiningCheck's coal town (its founding coking field and a thermal site by fiat), a hematite and a limestone site by fiat on it, and a chain on them - an Iron Mine, a Surface and an Underground Coal ... |
| 202 | 13 | `static Town copper()` | The copper town: CopperMiningCheck's (its founding oxide field and a sulphide site by fiat) with a Copper Mine & Mill and a Leach & SX-EW Plant on them, and every works of Copper Refining - a smelter far bigger than t... |
| 217 | 11 | `static Town wood()` | The wood town: ForestryCheck's forest town with a Logging Camp on its forest, and a Sawmill, a Pulp Mill and a Wood Pellet Mill. |
| 230 | 17 | `static Town play(String name, Game g, Runnable each)` | Hands a town its workers, plays SETTLE_MONTHS, notes every industry's stock, and plays the month it is read on - `each` before every month. |
| 248 | 13 | `static void printTown(Town t)` |  |

### 1. THE NINE (lines 262-303)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 39 | `static void theNine(List<Town> towns)` |  |

### 2. THE FIELDS (lines 304-376)

| line | len | member | says |
|---:|---:|---|---|
| 306 | 70 | `static void theFields(Town works, Town copper, Town wood)` |  |

### 3. EVERY GOOD BALANCES (lines 377-429)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 50 | `static void everyGoodBalances(List<Town> towns)` |  |

### 4. EVERY FIGURE IS THE MODEL'S (lines 430-591)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 81 | `static void everyFigure(List<Town> towns)` |  |
| 515 | 7 | `static boolean sold(Game g, Good good, String key, List<IndustryView.Part> parts)` | Whether `parts` are exactly the month's fills of `g` that `key` sold in the city, by buyer. |
| 524 | 7 | `static boolean bought(Game g, Good good, String key, List<IndustryView.Part> parts)` | ...and the fills `key` bought in the city, by seller. |
| 532 | 9 | `static boolean matches(Map<String, Double> by, List<IndustryView.Part> parts)` |  |
| 543 | 5 | `static boolean same(SpreadPlanner.Candidate a, SpreadPlanner.Candidate b)` | Whether two appraisals of a building are the same: its template, earnings, cost, first gate and every refusal. |
| 549 | 3 | `static boolean same(double a, double b)` |  |
| 560 | 31 | `static String planAgrees(Game g, IndustryView.View v, int[] tally)` | Whether the sector's plan orders what the view says it would (null) or why not, `tally` counting {ordered, none, waiting}: with an order in flight the plan waits; Coal Mining washes first (planWash()), so a passing wa... |

### 5. FROM WHERE (lines 592-697)

| line | len | member | says |
|---:|---:|---|---|
| 594 | 103 | `static void fromWhere(Town works, Town copper, Town wood)` |  |

### 6. THE POWER (lines 698-725)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 25 | `static void thePower(List<Town> towns, Town works, Town wood)` |  |

### 7. THE FOREST (lines 726-770)

| line | len | member | says |
|---:|---:|---|---|
| 728 | 42 | `static void theForest(Town wood)` |  |

### 8. THE WORDS AND COLOURS (lines 771-843)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 70 | `static void wordsAndColours(List<Town> towns)` |  |

### 9. PURE (lines 844-887)

| line | len | member | says |
|---:|---:|---|---|
| 846 | 16 | `static void pure(Town town) throws Exception` |  |
| 864 | 14 | `static String words(Game g)` | Every view of the town and its words, as text - and since batch I12 each picture's words and its strip. |
| 880 | 7 | `static String saved(Game g, int slot) throws Exception` | The town saved to a slot of its scratch folder, as text without the save's time stamp. |

### 11. THE PLAN'S ORDER (lines 888-930)

| line | len | member | says |
|---:|---:|---|---|
| 897 | 33 | `static void thePlansOrder()` | Towns at their founding, where kinds pass every gate - a sector with nothing running weighs one more at nameplate - and each plan orders: CoalMiningCheck's longwall with its raw coal unwashed, ForestryCheck's forest w... |

### 10. AFTER A LOAD (lines 931-952)

| line | len | member | says |
|---:|---:|---|---|
| 933 | 19 | `static void afterALoad(Town town)` |  |

### 12. THE PICTURE (lines 953-1218)

| line | len | member | says |
|---:|---:|---|---|
| 970 | 239 | `static void thePicture(List<Town> towns)` | The picture of every industry drawn in the three towns, at the 1,389 x 868 window's width: every ribbon from one node's right edge to another's left, its height its flow's quantity times the picture's one scale; each ... |
| 1211 | 3 | `static double drawnPart(double units)` | A part as the picture draws it: nothing under IndustryPicture.LEAST_PART. |
| 1215 | 3 | `static boolean inside(double x0, double y0, double x1, double y1, double w, double h)` |  |

