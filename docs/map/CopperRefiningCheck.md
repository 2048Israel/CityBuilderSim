# CopperRefiningCheck.java - 1,114 lines · 27 methods · 16 constants · harnesses

`ham/citybuildersim/CopperRefiningCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md
> 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's -
> materials-chains-research.md - 1.2, 4, 7 and 10's Q11 and Q12): copper's
> processing industry, the twenty-second key, and the Wire & Cable Works it feeds in
> Manufacturing.
> 
> WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I7's
> CementConcreteCheck), and copper's works add three things the other
> chains do not: a co-product another industry buys (the smelter's acid, the
> leach plants'), a chain four works long inside one sector with two
> captive goods, and the city's old copper bought before the market's scrap.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SECTOR ON THE REGISTRY: a ProcessingSector, the twenty-second key,
>      after Copper Mining (Forestry after it since 0.7.138); making cathode, anode, acid and rod of concentrate,
>      anode, cathode and scrap, Iron & Steel's group; anode and cathode
>      captive; cathode its planning good. Manufacturing makes wire and cable
>      of rod, its planning good still fabricated steel.
> 
>   2. THE FIVE ROWS ARE THE STUDY'S (ids 124 to 128, at parity): cost,
>      posts, draw, makes and uses, ground, road load, stock, consumables,
>      the scrap refinery's old copper; and their four cases, the study's 4.4
>      table to its rounding, each row's running cost backed out of its own
>      table (the scrap refinery's from its stated -0.02% in case A); then
>      what the study says of them - the smelter loses on imported
>      concentrate, imported scrap just breaks even, the rod mill's 11% a thin
>      premium on a big tonnage, the wire works built to replace imports and
>      not to export.
> 
>   3. THE CHAIN BY ITS RATIOS: concentrate to anode and acid (0.9 t of acid
>      a tonne of concentrate [K27]), anode to cathode, cathode to rod, rod to
>      wire and cable; and a played chain - a smelter, a refinery and a rod
>      mill standing beside a wire works - its anode and cathode captive at
>      the rate (min of what is made and what is needed), its rod sold at
>      home to the wire works, the rest of the rod and the wire shipped.
> 
>   4. THE SMELTER'S ACID SOLD, THE LEACH PLANT'S BOUGHT: a smelter beside a
>      leach plant makes 29,700 t of acid at the rate, the leach plant buys
>      its 3,500 t of it at home, and the rest ships.
> 
>   5. OLD COPPER: a car scrapped leaves COPPER_A_CAR of copper, a demolished
>      building COPPER_A_MATERIALS_UNIT a unit of its material, each at
>      RECOVERY, in a pool of its own beside the steel's; a Scrap Copper
>      Refinery buys it at PAY_SHARE of COPPER_SCRAP's export price, as much as
>      its intake at the rate and no more than arose, pro rata, the rest to
>      landfill; its bid for scrap the need past it; the households paid into
>      their savings and a selling sector by its other revenue, on the
>      refinery's input line; the audit closes.
> 
>   6. THE PLANNER WEIGHS THE OLD COPPER: one more scrap refinery's scrap is
>      the old copper past what those standing take, at what it pays, and the
>      rest at the market's value; with none arisen, the market's alone, to
>      the bit.
> 
>   7. THE WIRE & CABLE WORKS' ESTIMATE (Manufacturing): the maker's less its
>      insulation at the exchange factor and its freight; the sector's older
>      rows the maker's less their city legs (since FR2, as every maker's), to
>      the bit.
> 
> ... (33 more lines in the source)

**Uses:** [Good](Good.md) (202), [CopperRefining](CopperRefining.md) (46), [Sector](Sector.md) (43), [OldMetal](OldMetal.md) (29), [Game](Game.md) (28), [Sectors](Sectors.md) (23), [BuildingManager](BuildingManager.md) (23), [SpreadPlanner](SpreadPlanner.md) (18), [LongPlaytest](LongPlaytest.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (14), [GoodsMarket](GoodsMarket.md) (11), [CopperMiningCheck](CopperMiningCheck.md) (10), [MaterialsCheck](MaterialsCheck.md) (9), [Manufacturing](Manufacturing.md) (8), [ProcessingSector](ProcessingSector.md) (6), [JobType](JobType.md) (6), [CopperMining](CopperMining.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Trade](Trade.md) (3), [BuildingType](BuildingType.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [MoneyAudit](MoneyAudit.md) (2), [NationalAccounts](NationalAccounts.md) (2), [Markets](Markets.md) (1), [PayTier](PayTier.md) (1), [Statement](Statement.md) (1), [EarthSite](EarthSite.md) (1), [GameFiles](GameFiles.md) (1), [TreasuryFund](TreasuryFund.md) (1)

## Sections

| line | section |
|---:|---|
| 147 | 1. the sector |
| 200 | 2. the five rows against the study |
| 315 | 3. the chain by its ratios |
| 372 | 4. the smelter's acid sold, the leach plant's bought |
| 405 | 5. old copper |
| 494 | 6. the planner weighs the old copper |
| 547 | 7. the wire works' estimate |
| 575 | 8. a works buys inputs only for what it makes |
| 721 | 9. a works making a flow good orders no input for exports that sell under it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `CopperRefiningCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 114 | `CopperRefiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 203 | `CopperRefiningCheck.ROWS` | `{ CopperRefining.SMELTER, CopperRefining.ELECTROLYTIC_REFINERY, CopperRefinin...` | The rows, by their permanent ids, in the study's 4.3 order: Copper Refining's four and Manufacturing's wire works. |
| 207 | `CopperRefiningCheck.STUDY_COSTS` | `{ 500_000, 180_000, 280_000, 45_000, 110_000 }` | The study's 4.3: each one's cost, D$ thousands all in. |
| 210 | `CopperRefiningCheck.STUDY_POSTS` | `{ 700, 200, 200, 80, 400 }` | ...its posts. |
| 213 | `CopperRefiningCheck.STUDY_KW` | `{ 13_400, 6_000, 5_500, 1_100, 2_500 }` | ...its draw, kW. |
| 216 | `CopperRefiningCheck.STUDY_MADE` | `{ { Good.ANODE, Good.ACID }, { Good.COPPER }, { Good.COPPER }, { Good.COPPER_...` | ...what it makes a month, t. |
| 218 | `CopperRefiningCheck.STUDY_MAKES` | `{ { 9_750, 29_700 }, { 9_700 }, { 4_000 }, { 8_000 }, { 2_000 } }` | ...and how many tonnes of each, a month. |
| 221 | `CopperRefiningCheck.STUDY_FEED` | `{ Good.COPPER_CONC, Good.ANODE, Good.COPPER_SCRAP, Good.COPPER, Good.COPPER_R...` | ...and what it takes, t a month. |
| 223 | `CopperRefiningCheck.STUDY_USES` | `{ 33_000, 9_750, 4_400, 8_016, 2_000 }` | ...and how many tonnes of it, a month. |
| 226 | `CopperRefiningCheck.STUDY_TABLE` | `{ { - 0.3, 1.2, 2.7, 1.2 }, { - 1.9, 0.8, 3.5, 0.8 }, { 0.0, 2.2, 2.9, 0.7 },...` | The study's 4.4 table, % of the cost a month, cases A to D. |
| 230 | `CopperRefiningCheck.STUDY_RUNS` | `{ 5_253, 1_460, 1_776, 846, 4_820 }` | Each row's running cost a month, D$ thousands, backed out of its 4.4 table (its 4.3 gives none): the middle of the range on which all four of its returns round to the table - but the scrap refinery's, from the -0.02% ... |
| 233 | `CopperRefiningCheck.STUDY_SCRAP_A` | `- 0.02, STUDY_SCRAP_A_ROUNDING =.005` | The scrap refinery's case A as the study states it (its 4.4: "imported scrap just breaks even (case A, -0.02%)"), and that figure's rounding. |
| 236 | `CopperRefiningCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 318 | `CopperRefiningCheck.ACID_A_TONNE` | `.9` | The acid a smelter makes a tonne of concentrate: 0.9 t (the study's 4.1, [K27]). |
| 1031 | `CopperRefiningCheck.RUN_MONTHS` | `1000` | The months the Rotterdam run plays. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 110 | `static int fails` |  |
| 999 | `int months, cutMonths, firstCut` |  |
| 1000 | `final java.util.EnumSet<Good> goods` |  |
| 1001 | `final java.util.TreeSet<String> sectors` |  |
| 1002 | `double units` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 108 | 1007 | **type** `public class CopperRefiningCheck` | Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's - materials-chains-research.md - 1.2, 4, 7 and 10's Q11 and Q12): copper's processing indu... |
| 116 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 121 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 126 | 5 | `static void quietly(Runnable r)` |  |
| 132 | 14 | `public static void main(String[] args) throws Exception` |  |

### 1. the sector (lines 147-199)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 50 | `static void sector()` |  |

### 2. the five rows against the study (lines 200-314)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 76 | `static void rows()` |  |

### 3. the chain by its ratios (lines 315-371)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 51 | `static void chain()` |  |

### 4. the smelter's acid sold, the leach plant's bought (lines 372-404)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 30 | `static void acid()` |  |

### 5. old copper (lines 405-493)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 86 | `static void oldCopper()` |  |

### 6. the planner weighs the old copper (lines 494-546)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 31 | `static void plannerWeighsOldCopper()` |  |
| 529 | 10 | `static SpreadPlanner.Values oldValues(SpreadPlanner.Values plain, double spare, double price)` | The planner's values with `spare` t of old copper at `price` in front of the market's scrap (CopperRefining, THE OLD COPPER), built by hand. |
| 541 | 5 | `static SpreadPlanner.Candidate weigh(CopperRefining cr, BuildingsTemplate t, SpreadPlanner.Values v, BusinessInvestment plans, ...` | One works of `cr` weighed at `v`, as ProcessingSector weighs it: its freight in its earnings and its vans in its cost. |

### 7. the wire works' estimate (lines 547-574)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 25 | `static void wireWorksEstimate()` |  |

### 8. a works buys inputs only for what it makes (lines 575-720)

| line | len | member | says |
|---:|---:|---|---|
| 581 | 14 | `static Game copperWorks(String label, int smelters, int refineries, int rodMills, double cathodeExport)` | A copper town on the goods' literals (Game.playOnLiterals(): the premise is a fixed world price) with the works standing and, where the cathode's export price is given, that price set on its market (nothing resets it ... |
| 597 | 4 | `static void holdAndRun(Game g, CopperRefining cr)` | One month with the cathode shed put back at the dump line first: the condition caused again each month, not waited for. |
| 603 | 3 | `static boolean near(double a, double b)` | Whether a number is within a relative hair of another (a month's figures are sums of products). |
| 607 | 113 | `static void idleWorks()` |  |

### 9. a works making a flow good orders no input for exports that sell under it (lines 721-1114)

| line | len | member | says |
|---:|---:|---|---|
| 724 | 3 | `static void rodPays(Game g)` | A Rod Mill's rod paying for its cathode at any price the cathode is costed at: rod's world prices doubled, import above export as a band must be (FL1). |
| 729 | 3 | `static void rodUnpaid(Game g)` | ...and rod's export price at half the literal, the import price as it was: what a unit sells for abroad is under what its cathode costs (FL1). |
| 738 | 3 | `static boolean close(double a, double b)` | Whether a figure made at one moment of the month is another's, to what the month's vans add to the rate between them: the flow goods are made at the top of the market pass and the inputs ordered as each good clears, a... |
| 743 | 5 | `static double monthRate(Sector s, Good in)` | The operating rate a month ran at, read off an input's row (needed = the need at the nameplate times the rate the month cleared at): the rate itself moves at the month's end. |
| 750 | 10 | `static void booksLine(String label, Game g, CopperRefining cr)` | The books of the month before, as section 8 (d) reads them: the month's GDP plus the rest of the imports is not below C + G. |
| 761 | 224 | `static void flowGoods()` |  |
| 987 | 9 | `static void netExportAt(GoodsMarket m, Good g, double net)` | Sets a good's world export price, its import price as the literal, so its net export price is `net` (the net price rises with the export price: bisection) (FL1). |
| 998 | 31 | **type** `static final class Cuts` | The Rotterdam run's tally of a flow order's cuts (9 (f)). |
| 1005 | 23 | `void month(Game g)` _(in CopperRefiningCheck.Cuts)_ | Counts the month just played: an exportable traded flow good is cut where its row idled units (the order's cut is all it idles), and the month's GDP. |
| 1034 | 6 | `static void play(Game g, int months, Cuts cuts)` | Plays months one at a time, each counted (LongPlaytest.run(g, 1) is the same month as a run of many). |
| 1042 | 72 | `static void rotterdam()` | (f) how often the rule fires in a real run: a Rotterdam city played as LandmassCheck 13 plays it, each month counted. |

