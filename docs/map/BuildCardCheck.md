# BuildCardCheck.java - 998 lines · 21 methods · 1 constants · harnesses

`ham/citybuildersim/BuildCardCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The build card (0.7.25): BuildCard's figures for all 76 buildings held to
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
> 
> Every fixture causes its condition.

**Uses:** [BuildCard](BuildCard.md) (177), [CityNeeds](CityNeeds.md) (89), [BuildAdvice](BuildAdvice.md) (58), [Game](Game.md) (37), [BuildingsTemplate](BuildingsTemplate.md) (29), [CareType](CareType.md) (16), [Sector](Sector.md) (5), [Founding](Founding.md) (4), [Good](Good.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [GameFiles](GameFiles.md) (3), [Formats](Formats.md) (3), [BuildingManager](BuildingManager.md) (3), [LandManager](LandManager.md) (3), [BuildingsStacks](BuildingsStacks.md) (2), [JobType](JobType.md) (2), [EducationType](EducationType.md) (2), [Bank](Bank.md) (1), [Sectors](Sectors.md) (1), [Rail](Rail.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1)

## Sections

| line | section |
|---:|---|
| 199 | 1. EVERY BUILDING HAS A CARD |
| 243 | 2. THE HERO AND THE BARS |
| 369 | 3. THE TAGS |
| 405 | 4. THE NOTES |
| 435 | 5. THE INVESTORS' LINE |
| 509 | 6. THE GATES |
| 580 | 7. THE VERDICT |
| 633 | 8. THE WORD'S KIND |
| 767 | 9. A RUN OF ORDERS |
| 858 | · 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 636 | `BuildCardCheck.KIND_EXAMPLES` | `{ { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind....` | The examples: a word shaped as the model files it, and the kind it must read as. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |
| 80 | `static PrintStream out` |  |
| 81 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 922 | **type** `public class BuildCardCheck` | The build card (0.7.25): BuildCard's figures for all 76 buildings held to the model's own reads - the quote, the land, the staffing tests, the markets, the investors' words and their gates - in a played city. |
| 83 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 88 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 94 | 5 | `static void quietly(Runnable r)` |  |
| 100 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 120 | 27 | `static Game town(Path root, String name)` | A played town with workers to spare: the founding, then a bank, shops, two bakeries, six hundred houses, the three basic schools and a college, builders, roads, a clinic, police, power, water and one rail spur standin... |
| 148 | 22 | `public static void main(String[] args) throws Exception` |  |
| 172 | 11 | `static List<BuildCard.Group> allGroups(Game g)` | Every page's groups: the market's nine, and the city's five ring by ring. |
| 185 | 13 | `static void printTown(Game g)` | The town as the cards read it, for the record. |

### 1. EVERY BUILDING HAS A CARD (lines 199-242)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 41 | `static void everyBuilding(Game g)` |  |

### 2. THE HERO AND THE BARS (lines 243-368)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 110 | `static void heroAndBars(Game g)` |  |
| 356 | 4 | `static int onSite(BuildingManager bm, BuildingsTemplate t)` |  |
| 362 | 6 | `static BuildAdvice.Measure measureOf(BuildCard.Group gr)` | The measure a city group was drawn for: the ring whose label is its title. |

### 3. THE TAGS (lines 369-404)

| line | len | member | says |
|---:|---:|---|---|
| 371 | 33 | `static void theTags(Game g)` |  |

### 4. THE NOTES (lines 405-434)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 27 | `static void theNotes(Game g)` |  |

### 5. THE INVESTORS' LINE (lines 435-508)

| line | len | member | says |
|---:|---:|---|---|
| 437 | 71 | `static void theInvestors(Path root, Game g)` |  |

### 6. THE GATES (lines 509-579)

| line | len | member | says |
|---:|---:|---|---|
| 511 | 68 | `static void theGates(Game g)` |  |

### 7. THE VERDICT (lines 580-632)

| line | len | member | says |
|---:|---:|---|---|
| 582 | 50 | `static void theVerdict(Game g)` |  |

### 8. THE WORD'S KIND (lines 633-766)

| line | len | member | says |
|---:|---:|---|---|
| 694 | 73 | `static void theKinds(Game g)` |  |

### 9. A RUN OF ORDERS (lines 767-857)

| line | len | member | says |
|---:|---:|---|---|
| 769 | 5 | `static Map<BuildingsTemplate, Integer> run(Object...pairs)` |  |
| 775 | 82 | `static void theRun(Game g)` |  |

### 10. SERVED (0.7.41). Jerus, playing 0.7.39: the roads read "180%" and (lines 858-998)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 130 | `static void theServed(Game g)` |  |

