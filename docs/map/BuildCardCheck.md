# BuildCardCheck.java - 1,806 lines · 33 methods · 1 constants · harnesses

`ham/citybuildersim/BuildCardCheck.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The build card (0.7.25): BuildCard's figures for every building in
> buildings.json (139 since 0.7.140) held to
> the model's own reads - the quote, the land, the staffing tests, the
> markets, the investors' words and their gates - in a played city.
> 
> WHY. The card is what a player reads before spending the treasury on a
> building, and since 0.7.25 every card carries figures the model never
> printed before: a price per resident or per thousand meals, a maker's
> value added, land per unit, the investors' word and the first gate this
> building fails for them. A bar that divided by the wrong figure, a tag on
> a card that is not the best, an investors' line that claimed an order on
> site nobody placed, or a "this one:" that named a gate the building
> passes would be a confident wrong answer on the screen that spends. The
> screen is checked by eye; this holds what it is drawn from.
> 
> What this has to prove:
>   1. EVERY BUILDING HAS A CARD: each of the catalogue's buildings sits in
>      a group on its category's page - a market building on exactly one -
>      with a unit above zero or the "adds nothing" state.
>   2. THE HERO AND THE BARS ARE THE MODEL'S: the hero's figure is the
>      template's own; bar 1 is the quote at one over the unit, to the bit;
>      bar 2 is land per unit, the posts the city likely cannot fill per
>      10,000 served, or the share of an office's posts it could not staff;
>      value added is the market's prices on what one makes and uses; the
>      running cost is the upkeep and the posts at today's wages.
>   3. THE TAGS: only the strict best on a bar, only in a group of two or
>      more, and never in a group of one.
>   4. THE NOTES: each group's note reads the sector's own figures.
>   5. THE INVESTORS' LINE: "on site" exactly when an investor's order with
>      value is on the site (a fixture order by Luxury Retail), not for the
>      city's own; the word is Game.getLastInvestment() under the slot the
>      month files it in, the bank's under "Bank"; and after a load, the
>      bank's planner sees the bank (the load-path fix of 0.7.25).
>   6. THE GATES: each "this one:" gate caused by a fixture - no deposit,
>      nobody licensed, a building the city could not staff, no land, a
>      loss in the investors' estimate - and none for one that passes them.
>   7. THE VERDICT: the quote line's verdict in buildStack()'s order, and
>      for each refusal the same answer buildStack() gives.
>   8. THE WORD'S KIND (0.7.30): every sector's word in the town has a kind
>      that is not OTHER; an example of every phrase gets its kind, matched
>      as whole words and in WordKind's order; and a sector's investors are
>      its own buildings' lines, its word, and "building" while investors
>      have an order on site.
>   9. A RUN OF ORDERS (0.7.40): Build's funding page is sized to a run -
>      its invoice, each order priced on the yard the ones before it leave,
>      is what placing them in turn charges, to the bit, and more than the
>      orders quoted alone when the yard covers part; the gap is that less
>      the cash, an overdraft in full; and the run is walked as buildStack()
>      checks it - an order short of ground once the ones before it have
>      theirs, of a deposit or of licences stops it, with buildStack()'s own
>      answer there.
>   10. SERVED (0.7.41): every Build ring and NEEDS YOU row that is a
>       supply against a demand reads supply over demand, unclamped - each
>       row its owner's getter, to the bit - and CityNeeds' one verdict on
>       it is NEEDS YOU's own lines turned over: the same colour as the
>       row's level wherever the row is listed and nothing is on the way, a
>       tick only at 100% or more, and Jerus's case - general care at 90%,
>       off the list - amber and "short", shown on Build's tile before its
>       tick.
>   11. THE INTEREST ON ITS WHOLE COST (0.7.122): the gate after the loss
> ... (35 more lines in the source)

**Uses:** [BuildCard](BuildCard.md) (332), [CityNeeds](CityNeeds.md) (89), [BuildAdvice](BuildAdvice.md) (79), [Game](Game.md) (51), [BuildingsTemplate](BuildingsTemplate.md) (43), [Resource](Resource.md) (31), [Good](Good.md) (30), [Deposit](Deposit.md) (27), [CareType](CareType.md) (16), [Formats](Formats.md) (14), [SpreadPlanner](SpreadPlanner.md) (13), [ExtractionSector](ExtractionSector.md) (11), [World](World.md) (11), [Forestry](Forestry.md) (10), [Sector](Sector.md) (9), [Mining](Mining.md) (9), [BusinessInvestment](BusinessInvestment.md) (9), [BuildingManager](BuildingManager.md) (7), [Sectors](Sectors.md) (6), [HeavyIndustry](HeavyIndustry.md) (6), [Refining](Refining.md) (5), [LandManager](LandManager.md) (5), [ProcessingSector](ProcessingSector.md) (5), [GameFiles](GameFiles.md) (4), [Founding](Founding.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [ForestryCheck](ForestryCheck.md) (4), [CityLand](CityLand.md) (4), [LandParcel](LandParcel.md) (4), [WoodProducts](WoodProducts.md) (3)... and 17 more

## Sections

| line | section |
|---:|---|
| 253 | 1. EVERY BUILDING HAS A CARD |
| 392 | 2. THE HERO AND THE BARS |
| 566 | 3. THE TAGS |
| 602 | 4. THE NOTES |
| 634 | 5. THE INVESTORS' LINE |
| 723 | 6. THE GATES |
| 825 | 7. THE VERDICT |
| 878 | 8. THE WORD'S KIND |
| 1029 | 9. A RUN OF ORDERS |
| 1120 | · 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and |
| 1261 | 11. THE INTEREST ON ITS WHOLE COST |
| 1399 | 12. THE MATERIALS' CARDS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 881 | `BuildCardCheck.KIND_EXAMPLES` | `{ { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind....` | The examples: a word shaped as the model files it, and the kind it must read as. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 126 | `static int fails` |  |
| 127 | `static PrintStream out` |  |
| 128 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 124 | 1683 | **type** `public class BuildCardCheck` | The build card (0.7.25): BuildCard's figures for every building in buildings.json (139 since 0.7.140) held to the model's own reads - the quote, the land, the staffing tests, the markets, the investors' words and thei... |
| 130 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 135 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 140 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 146 | 5 | `static void quietly(Runnable r)` |  |
| 152 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 172 | 27 | `static Game town(Path root, String name)` | A played town with workers to spare: the founding, then a bank, shops, two bakeries, six hundred houses, the three basic schools and a college, builders, roads, a clinic, police, power, water and one rail spur standin... |
| 200 | 24 | `public static void main(String[] args) throws Exception` |  |
| 226 | 11 | `static List<BuildCard.Group> allGroups(Game g)` | Every page's groups: the market's nine, and the city's five ring by ring. |
| 239 | 13 | `static void printTown(Game g)` | The town as the cards read it, for the record. |

### 1. EVERY BUILDING HAS A CARD (lines 253-391)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 136 | `static void everyBuilding(Game g)` |  |

### 2. THE HERO AND THE BARS (lines 392-565)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 158 | `static void heroAndBars(Game g)` |  |
| 553 | 4 | `static int onSite(BuildingManager bm, BuildingsTemplate t)` |  |
| 559 | 6 | `static BuildAdvice.Measure measureOf(BuildCard.Group gr)` | The measure a city group was drawn for: the ring whose label is its title. |

### 3. THE TAGS (lines 566-601)

| line | len | member | says |
|---:|---:|---|---|
| 568 | 33 | `static void theTags(Game g)` |  |

### 4. THE NOTES (lines 602-633)

| line | len | member | says |
|---:|---:|---|---|
| 604 | 29 | `static void theNotes(Game g)` |  |

### 5. THE INVESTORS' LINE (lines 634-722)

| line | len | member | says |
|---:|---:|---|---|
| 636 | 86 | `static void theInvestors(Path root, Game g)` |  |

### 6. THE GATES (lines 723-824)

| line | len | member | says |
|---:|---:|---|---|
| 725 | 99 | `static void theGates(Game g)` |  |

### 7. THE VERDICT (lines 825-877)

| line | len | member | says |
|---:|---:|---|---|
| 827 | 50 | `static void theVerdict(Game g)` |  |

### 8. THE WORD'S KIND (lines 878-1028)

| line | len | member | says |
|---:|---:|---|---|
| 955 | 74 | `static void theKinds(Game g)` |  |

### 9. A RUN OF ORDERS (lines 1029-1119)

| line | len | member | says |
|---:|---:|---|---|
| 1031 | 5 | `static Map<BuildingsTemplate, Integer> run(Object...pairs)` |  |
| 1037 | 82 | `static void theRun(Game g)` |  |

### 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and (lines 1120-1260)

| line | len | member | says |
|---:|---:|---|---|
| 1130 | 130 | `static void theServed(Game g)` |  |

### 11. THE INTEREST ON ITS WHOLE COST (lines 1261-1398)

| line | len | member | says |
|---:|---:|---|---|
| 1273 | 120 | `static void theInterest(Game g)` | consider()'s brake on the card (0.7.122, batch C1). |
| 1395 | 3 | `static double tillOf(Game g, String sector)` | What the card's test counts as the sector's till: its cash and what it holds abroad (Game.interestTest()). |

### 12. THE MATERIALS' CARDS (lines 1399-1806)

| line | len | member | says |
|---:|---:|---|---|
| 1415 | 77 | `static void theMaterials(Game g, Path root)` | The materials buildings' cards (0.7.142, batch I13; spec-materials 2.13): each line a fixture causes, each figure against the model's own. |
| 1494 | 70 | `static void typedField(Game g)` | b. |
| 1566 | 30 | `static void moneyGate(Game g)` | c. |
| 1598 | 41 | `static void ownPowerAndOldMetal(Game g)` | d. |
| 1641 | 54 | `static void whoBuys(Game g)` | e. |
| 1697 | 27 | `static void crewRule(Path root)` | f. |
| 1726 | 31 | `static void hoverByType(Game g)` | g. |
| 1759 | 23 | `static void paybackByType(Game g)` | h. |
| 1784 | 22 | `static Deposit unownedNearest(Game g, World w, Resource r, Deposit.FieldType type, int maxSites)` | The field of a type nearest the town's founding site whose centre plot nobody owns, of at most `maxSites` sites. |

