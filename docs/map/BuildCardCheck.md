# BuildCardCheck.java - 2,361 lines · 42 methods · 1 constants · harnesses

`ham/citybuildersim/BuildCardCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

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
>      running cost is the upkeep and the filled posts at today's wages.
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

**Uses:** [BuildCard](BuildCard.md) (422), [BuildAdvice](BuildAdvice.md) (113), [CityNeeds](CityNeeds.md) (106), [Game](Game.md) (64), [BuildingsTemplate](BuildingsTemplate.md) (58), [Good](Good.md) (34), [Resource](Resource.md) (31), [Deposit](Deposit.md) (27), [Formats](Formats.md) (21), [CareType](CareType.md) (19), [Sectors](Sectors.md) (13), [BusinessInvestment](BusinessInvestment.md) (13), [SpreadPlanner](SpreadPlanner.md) (13), [Ports](Ports.md) (12), [ExtractionSector](ExtractionSector.md) (11), [World](World.md) (11), [Sector](Sector.md) (10), [Forestry](Forestry.md) (10), [BuildingManager](BuildingManager.md) (9), [Mining](Mining.md) (9), [BuildAdviceCheck](BuildAdviceCheck.md) (7), [GameFiles](GameFiles.md) (6), [Founding](Founding.md) (6), [HeavyIndustry](HeavyIndustry.md) (6), [Refining](Refining.md) (5), [LandManager](LandManager.md) (5), [ProcessingSector](ProcessingSector.md) (5), [JobType](JobType.md) (4), [WageBand](WageBand.md) (4), [InfrastructureManager](InfrastructureManager.md) (4)... and 32 more

**Used by (1):** [BuildingStackCheck](BuildingStackCheck.md)

## Sections

| line | section |
|---:|---|
| 281 | 1. EVERY BUILDING HAS A CARD |
| 481 | 2. THE HERO AND THE BARS |
| 668 | 3. THE TAGS |
| 704 | 4. THE NOTES |
| 736 | 5. THE INVESTORS' LINE |
| 825 | 6. THE GATES |
| 936 | 7. THE VERDICT |
| 989 | 8. THE WORD'S KIND |
| 1155 | 9. A RUN OF ORDERS |
| 1246 | · 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and |
| 1414 | 11. THE INTEREST ON ITS WHOLE COST |
| 1558 | 12. THE MATERIALS' CARDS |
| 1970 | 13. A BERTH'S PAYBACK, THE OWNER AND THE DEPOT'S LINE (FR4, 2026-10-20) |
| 2181 | 14. THE LANDLORDS' CARD: THE LENDER'S TEST |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 992 | `BuildCardCheck.KIND_EXAMPLES` | `{ { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind....` | The examples: a word shaped as the model files it, and the kind it must read as. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 151 | `static int fails` |  |
| 152 | `static PrintStream out` |  |
| 153 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 149 | 2213 | **type** `public class BuildCardCheck` | The build card (0.7.25): BuildCard's figures for every building in buildings.json (143 since FR3, the Bulk Berth) held to the model's own reads - the quote, the land, the staffing tests, the markets, the investors' wo... |
| 155 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 160 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 165 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 171 | 5 | `static void quietly(Runnable r)` |  |
| 177 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 197 | 27 | `static Game town(Path root, String name)` | A played town with workers to spare: the founding, then a bank, shops, two bakeries, six hundred houses, the three basic schools and a college, builders, roads, a clinic, police, power, water and one rail spur standin... |
| 225 | 27 | `public static void main(String[] args) throws Exception` |  |
| 254 | 11 | `static List<BuildCard.Group> allGroups(Game g)` | Every page's groups: the market's nine, and the city's five ring by ring. |
| 267 | 13 | `static void printTown(Game g)` | The town as the cards read it, for the record. |

### 1. EVERY BUILDING HAS A CARD (lines 281-480)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 197 | `static void everyBuilding(Game g)` |  |

### 2. THE HERO AND THE BARS (lines 481-667)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 171 | `static void heroAndBars(Game g)` |  |
| 655 | 4 | `static long onSite(BuildingManager bm, BuildingsTemplate t)` |  |
| 661 | 6 | `static BuildAdvice.Measure measureOf(BuildCard.Group gr)` | The measure a city group was drawn for: the ring whose label is its title. |

### 3. THE TAGS (lines 668-703)

| line | len | member | says |
|---:|---:|---|---|
| 670 | 33 | `static void theTags(Game g)` |  |

### 4. THE NOTES (lines 704-735)

| line | len | member | says |
|---:|---:|---|---|
| 706 | 29 | `static void theNotes(Game g)` |  |

### 5. THE INVESTORS' LINE (lines 736-824)

| line | len | member | says |
|---:|---:|---|---|
| 738 | 86 | `static void theInvestors(Path root, Game g)` |  |

### 6. THE GATES (lines 825-935)

| line | len | member | says |
|---:|---:|---|---|
| 827 | 108 | `static void theGates(Game g)` |  |

### 7. THE VERDICT (lines 936-988)

| line | len | member | says |
|---:|---:|---|---|
| 938 | 50 | `static void theVerdict(Game g)` |  |

### 8. THE WORD'S KIND (lines 989-1154)

| line | len | member | says |
|---:|---:|---|---|
| 1066 | 76 | `static void theKinds(Game g)` |  |
| 1143 | 11 | `static void shortWords() throws ReflectiveOperationException` | The compact card's text-only reader, without constructing a screen or starting the toolkit. |

### 9. A RUN OF ORDERS (lines 1155-1245)

| line | len | member | says |
|---:|---:|---|---|
| 1157 | 5 | `static Map<BuildingsTemplate, Long> run(Object...pairs)` |  |
| 1163 | 82 | `static void theRun(Game g)` |  |

### 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and (lines 1246-1413)

| line | len | member | says |
|---:|---:|---|---|
| 1256 | 157 | `static void theServed(Game g)` |  |

### 11. THE INTEREST ON ITS WHOLE COST (lines 1414-1557)

| line | len | member | says |
|---:|---:|---|---|
| 1426 | 126 | `static void theInterest(Game g)` | consider()'s brake on the card (0.7.122, batch C1). |
| 1554 | 3 | `static double tillOf(Game g, String sector)` | What the card's test counts as the sector's till: its cash and what it holds abroad (Game.interestTest()). |

### 12. THE MATERIALS' CARDS (lines 1558-1969)

| line | len | member | says |
|---:|---:|---|---|
| 1574 | 77 | `static void theMaterials(Game g, Path root)` | The materials buildings' cards (0.7.142, batch I13; spec-materials 2.13): each line a fixture causes, each figure against the model's own. |
| 1653 | 70 | `static void typedField(Game g)` | b. |
| 1725 | 32 | `static void moneyGate(Game g)` | c. |
| 1759 | 41 | `static void ownPowerAndOldMetal(Game g)` | d. |
| 1802 | 56 | `static void whoBuys(Game g)` | e. |
| 1860 | 27 | `static void crewRule(Path root)` | f. |
| 1889 | 31 | `static void hoverByType(Game g)` | g. |
| 1922 | 23 | `static void paybackByType(Game g)` | h. |
| 1947 | 22 | `static Deposit unownedNearest(Game g, World w, Resource r, Deposit.FieldType type, int maxSites)` | The field of a type nearest the town's founding site whose centre plot nobody owns, of at most `maxSites` sites. |

### 13. A BERTH'S PAYBACK, THE OWNER AND THE DEPOT'S LINE (FR4, 2026-10-20) (lines 1970-2180)

| line | len | member | says |
|---:|---:|---|---|
| 1974 | 5 | `static void theBerthAndTheDepot(Path root, Game town)` |  |
| 1981 | 15 | `static boolean agreesWithAdvice(Game g, List<BuildingsTemplate> ports, BuildAdvice.Measure m)` | Whether the card's payback for every terminal of the measure's kind says what the advice would do: it pays back (PAYS, or UPKEEP, which the break-even does not see) exactly when portTerminal() orders that terminal, wi... |
| 1997 | 4 | `static boolean finite(double...v)` |  |
| 2002 | 116 | `static void berthPayback(Path root)` |  |
| 2120 | 60 | `static void freightsCards(Game g)` | Freight's cards: owned by Freight (the Rail cards no longer say "the rail sector"), and the Truck Depot's line is Haulage's own. |

### 14. THE LANDLORDS' CARD: THE LENDER'S TEST (lines 2181-2361)

| line | len | member | says |
|---:|---:|---|---|
| 2184 | 17 | `static void landlordsOrder(Game g, BuildingsTemplate home, double cash, double rent)` | Game.considerOnMortgage(), the landlords' order, run for one home with the funds and the rent given: the order the card is held to. |
| 2203 | 10 | `static String lenderWords(BuildCard.Gate gate)` | BuildScreen.lenderWords(), a package-private static of the build screen, read through reflection: it needs no toolkit (RunWorksCheck's way). |
| 2214 | 147 | `static void theLender(Game g)` |  |

