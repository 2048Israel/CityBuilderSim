# BuildCardCheck.java - 2,270 lines · 41 methods · 1 constants · harnesses

`ham/citybuildersim/BuildCardCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The build card (0.7.25): BuildCard's figures for every building in
> buildings.json (143 since FR3, the Bulk Berth) held to
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
> ... (60 more lines in the source)

**Uses:** [BuildCard](BuildCard.md) (418), [BuildAdvice](BuildAdvice.md) (113), [CityNeeds](CityNeeds.md) (91), [Game](Game.md) (64), [BuildingsTemplate](BuildingsTemplate.md) (56), [Good](Good.md) (33), [Resource](Resource.md) (31), [Deposit](Deposit.md) (27), [Formats](Formats.md) (21), [CareType](CareType.md) (16), [BusinessInvestment](BusinessInvestment.md) (13), [SpreadPlanner](SpreadPlanner.md) (13), [Sectors](Sectors.md) (12), [Ports](Ports.md) (12), [ExtractionSector](ExtractionSector.md) (11), [World](World.md) (11), [Forestry](Forestry.md) (10), [Sector](Sector.md) (9), [BuildingManager](BuildingManager.md) (9), [Mining](Mining.md) (9), [BuildAdviceCheck](BuildAdviceCheck.md) (7), [GameFiles](GameFiles.md) (6), [Founding](Founding.md) (6), [HeavyIndustry](HeavyIndustry.md) (6), [Refining](Refining.md) (5), [LandManager](LandManager.md) (5), [ProcessingSector](ProcessingSector.md) (5), [InfrastructureManager](InfrastructureManager.md) (4), [ForestryCheck](ForestryCheck.md) (4), [CityLand](CityLand.md) (4)... and 28 more

## Sections

| line | section |
|---:|---|
| 280 | 1. EVERY BUILDING HAS A CARD |
| 470 | 2. THE HERO AND THE BARS |
| 646 | 3. THE TAGS |
| 682 | 4. THE NOTES |
| 714 | 5. THE INVESTORS' LINE |
| 803 | 6. THE GATES |
| 905 | 7. THE VERDICT |
| 958 | 8. THE WORD'S KIND |
| 1109 | 9. A RUN OF ORDERS |
| 1200 | · 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and |
| 1344 | 11. THE INTEREST ON ITS WHOLE COST |
| 1482 | 12. THE MATERIALS' CARDS |
| 1890 | 13. A BERTH'S PAYBACK, THE OWNER AND THE DEPOT'S LINE (FR4, 2026-10-20) |
| 2101 | 14. THE LANDLORDS' CARD: THE LENDER'S TEST |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 961 | `BuildCardCheck.KIND_EXAMPLES` | `{ { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind....` | The examples: a word shaped as the model files it, and the kind it must read as. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 151 | `static int fails` |  |
| 152 | `static PrintStream out` |  |
| 153 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 149 | 2122 | **type** `public class BuildCardCheck` | The build card (0.7.25): BuildCard's figures for every building in buildings.json (143 since FR3, the Bulk Berth) held to the model's own reads - the quote, the land, the staffing tests, the markets, the investors' wo... |
| 155 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 160 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 165 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 171 | 5 | `static void quietly(Runnable r)` |  |
| 177 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 197 | 27 | `static Game town(Path root, String name)` | A played town with workers to spare: the founding, then a bank, shops, two bakeries, six hundred houses, the three basic schools and a college, builders, roads, a clinic, police, power, water and one rail spur standin... |
| 225 | 26 | `public static void main(String[] args) throws Exception` |  |
| 253 | 11 | `static List<BuildCard.Group> allGroups(Game g)` | Every page's groups: the market's nine, and the city's five ring by ring. |
| 266 | 13 | `static void printTown(Game g)` | The town as the cards read it, for the record. |

### 1. EVERY BUILDING HAS A CARD (lines 280-469)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 187 | `static void everyBuilding(Game g)` |  |

### 2. THE HERO AND THE BARS (lines 470-645)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 160 | `static void heroAndBars(Game g)` |  |
| 633 | 4 | `static int onSite(BuildingManager bm, BuildingsTemplate t)` |  |
| 639 | 6 | `static BuildAdvice.Measure measureOf(BuildCard.Group gr)` | The measure a city group was drawn for: the ring whose label is its title. |

### 3. THE TAGS (lines 646-681)

| line | len | member | says |
|---:|---:|---|---|
| 648 | 33 | `static void theTags(Game g)` |  |

### 4. THE NOTES (lines 682-713)

| line | len | member | says |
|---:|---:|---|---|
| 684 | 29 | `static void theNotes(Game g)` |  |

### 5. THE INVESTORS' LINE (lines 714-802)

| line | len | member | says |
|---:|---:|---|---|
| 716 | 86 | `static void theInvestors(Path root, Game g)` |  |

### 6. THE GATES (lines 803-904)

| line | len | member | says |
|---:|---:|---|---|
| 805 | 99 | `static void theGates(Game g)` |  |

### 7. THE VERDICT (lines 905-957)

| line | len | member | says |
|---:|---:|---|---|
| 907 | 50 | `static void theVerdict(Game g)` |  |

### 8. THE WORD'S KIND (lines 958-1108)

| line | len | member | says |
|---:|---:|---|---|
| 1035 | 74 | `static void theKinds(Game g)` |  |

### 9. A RUN OF ORDERS (lines 1109-1199)

| line | len | member | says |
|---:|---:|---|---|
| 1111 | 5 | `static Map<BuildingsTemplate, Integer> run(Object...pairs)` |  |
| 1117 | 82 | `static void theRun(Game g)` |  |

### 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and (lines 1200-1343)

| line | len | member | says |
|---:|---:|---|---|
| 1210 | 133 | `static void theServed(Game g)` |  |

### 11. THE INTEREST ON ITS WHOLE COST (lines 1344-1481)

| line | len | member | says |
|---:|---:|---|---|
| 1356 | 120 | `static void theInterest(Game g)` | consider()'s brake on the card (0.7.122, batch C1). |
| 1478 | 3 | `static double tillOf(Game g, String sector)` | What the card's test counts as the sector's till: its cash and what it holds abroad (Game.interestTest()). |

### 12. THE MATERIALS' CARDS (lines 1482-1889)

| line | len | member | says |
|---:|---:|---|---|
| 1498 | 77 | `static void theMaterials(Game g, Path root)` | The materials buildings' cards (0.7.142, batch I13; spec-materials 2.13): each line a fixture causes, each figure against the model's own. |
| 1577 | 70 | `static void typedField(Game g)` | b. |
| 1649 | 30 | `static void moneyGate(Game g)` | c. |
| 1681 | 41 | `static void ownPowerAndOldMetal(Game g)` | d. |
| 1724 | 54 | `static void whoBuys(Game g)` | e. |
| 1780 | 27 | `static void crewRule(Path root)` | f. |
| 1809 | 31 | `static void hoverByType(Game g)` | g. |
| 1842 | 23 | `static void paybackByType(Game g)` | h. |
| 1867 | 22 | `static Deposit unownedNearest(Game g, World w, Resource r, Deposit.FieldType type, int maxSites)` | The field of a type nearest the town's founding site whose centre plot nobody owns, of at most `maxSites` sites. |

### 13. A BERTH'S PAYBACK, THE OWNER AND THE DEPOT'S LINE (FR4, 2026-10-20) (lines 1890-2100)

| line | len | member | says |
|---:|---:|---|---|
| 1894 | 5 | `static void theBerthAndTheDepot(Path root, Game town)` |  |
| 1901 | 15 | `static boolean agreesWithAdvice(Game g, List<BuildingsTemplate> ports, BuildAdvice.Measure m)` | Whether the card's payback for every terminal of the measure's kind says what the advice would do: it pays back (PAYS, or UPKEEP, which the break-even does not see) exactly when portTerminal() orders that terminal, wi... |
| 1917 | 4 | `static boolean finite(double...v)` |  |
| 1922 | 116 | `static void berthPayback(Path root)` |  |
| 2040 | 60 | `static void freightsCards(Game g)` | Freight's cards: owned by Freight (the Rail cards no longer say "the rail sector"), and the Truck Depot's line is Haulage's own. |

### 14. THE LANDLORDS' CARD: THE LENDER'S TEST (lines 2101-2270)

| line | len | member | says |
|---:|---:|---|---|
| 2104 | 17 | `static void landlordsOrder(Game g, BuildingsTemplate home, double cash, double rent)` | Game.considerOnMortgage(), the landlords' order, run for one home with the funds and the rent given: the order the card is held to. |
| 2123 | 10 | `static String lenderWords(BuildCard.Gate gate)` | BuildScreen.lenderWords(), a package-private static of the build screen, read through reflection: it needs no toolkit (RunWorksCheck's way). |
| 2134 | 136 | `static void theLender(Game g)` |  |

