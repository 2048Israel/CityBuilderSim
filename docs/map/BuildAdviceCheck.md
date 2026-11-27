# BuildAdviceCheck.java - 2,155 lines · 66 methods · 2 constants · harnesses

`ham/citybuildersim/BuildAdviceCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS
> YOU's list, to the quote, and to the model's own figures, in a played
> city - and the categories it is filed under.
> 
> WHY. WHAT WOULD HELP MOST tells a player what to build and how many, and
> each card's Build button spends the treasury on that. A rule that read a
> need NEEDS YOU does not list, counted past what closes it, quoted a price
> the Build button would not charge, or promised a figure the month would
> not reach would be a confident wrong answer on the front page of the game.
> It is advice, not a model change, so nothing in the default playtest
> reaches it: this is the only place it is played.
> 
> What this has to prove:
>   1. THE CATEGORIES: the strip's fourteen, each once, the five renamed by
>      the old label still landing on the new; every building in exactly one.
>   2. NEEDS YOU'S NEEDS, IN ITS ORDER: every suggestion is a need NEEDS YOU
>      lists, one a city-built building answers, in the list's order, at
>      most three and one per need - and a need it passed over before the
>      last one it took is one what is on site already answers, or one no
>      building can help.
>   3. THE COUNT AND THE PRICE (0.7.51): the count is the least that keeps
>      the need ahead at its projection after what is on site, at the
>      staffed capacity the model counts - one fewer does not; the price is
>      Game.quoteBuild() for it, to the bit; and the building fits the land
>      the cards before it leave, or none that closes the need does.
>      3b. THE CASH, NO CAP: every count is what keeps its need ahead
>      whatever the cash, and a card is on credit exactly when the cash the
>      ones before left is short of its quote - by that much; with no cash,
>      every one, its count unchanged. (Until 0.7.51 the count was cut to
>      what the cash left afforded.)
>   4. BEFORE AND AFTER, BY THE MODEL: in a twin of the city, the order and
>      what is on site built standing and the month's own services pass run
>      on it, every suggestion's figure is the one the model reads - and the
>      same for an order of every measure a city category opens on.
>   5. THE STAFFING WEIGHTING: in a city short of one job type, of two
>      buildings alike in everything but how many of those posts they have,
>      the advice takes the one with fewer.
>   6. NOTHING NEEDED, NOTHING SUGGESTED: a city NEEDS YOU lists no
>      city-built need for gets no suggestion.
>   7. LAND COUNTED (0.7.51): a card's figure is, since 0.7.101, its order
>      over its life - its quote for the count, its ground at landValue()
>      and its running - over what it will serve over that life, to the bit,
>      on the land the cards before leave (its quote for one and its ground
>      over what one serves until then, a road's with its running over its
>      life since 0.7.70; RoadCheck holds the rest);
>      with no ground free, the land office's prices at nothing pick the
>      road that is cheapest to build and keep, and at ten times the price
>      past which the road that needs the least ground a trip is the
>      cheapest with it, that road.
>   8. SLACK BUILT: a served card is at 100% of its demand projected and
>      SLACK past it, and one fewer does not cover that projected demand.
>   9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE: a college or university row
>      wants no more than would come and no more than would be hired, and a
>      first school half the smallest; a city with students and no posts
>      for them gets no row and no card until the posts come; and the
>      playtest's month-9 university is gone.
>  10. SIZED TO THE PROJECTION: a card's growth is the businesses'
>      growthFactor() over its wait and their horizon, to the bit, and a
>      rising population never orders fewer.
>  11. THE RUN: "Build all three" places the cards' orders in their order,
> ... (30 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (341), [CityNeeds](CityNeeds.md) (113), [Game](Game.md) (82), [BuildingsTemplate](BuildingsTemplate.md) (76), [BuildingType](BuildingType.md) (34), [Ports](Ports.md) (23), [CareType](CareType.md) (18), [JobType](JobType.md) (17), [Crime](Crime.md) (13), [PowerDispatch](PowerDispatch.md) (12), [BuildingManager](BuildingManager.md) (11), [LongPlaytest](LongPlaytest.md) (11), [EducationType](EducationType.md) (10), [GameFiles](GameFiles.md) (9), [SafetyType](SafetyType.md) (9), [UtilitiesHandler](UtilitiesHandler.md) (7), [WageBand](WageBand.md) (7), [AdultStage](AdultStage.md) (7), [Founding](Founding.md) (6), [Observation](Observation.md) (6), [BusinessInvestment](BusinessInvestment.md) (5), [AutoBuilder](AutoBuilder.md) (4), [Formats](Formats.md) (3), [StrategicReserve](StrategicReserve.md) (3), [Healthcare](Healthcare.md) (3), [Education](Education.md) (3), [TaxPolicy](TaxPolicy.md) (3), [ResearchCatalog](ResearchCatalog.md) (3), [WorksValue](WorksValue.md) (3), [Sector](Sector.md) (2)... and 18 more

**Used by (3):** [BuildCardCheck](BuildCardCheck.md), [RoadCheck](RoadCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 289 | 1. THE CATEGORIES |
| 426 | 2. NEEDS YOU'S NEEDS, IN ITS ORDER |
| 479 | 3. THE COUNT AND THE PRICE |
| 623 | 4. BEFORE AND AFTER, BY THE MODEL |
| 809 | 5. THE STAFFING WEIGHTING |
| 906 | 6. NOTHING NEEDED |
| 928 | 7. LAND COUNTED |
| 1105 | 8. SLACK BUILT |
| 1158 | 9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE |
| 1348 | 10. SIZED TO THE PROJECTION |
| 1408 | 11. THE RUN |
| 1462 | 12. THE BEDS LESS THE PRICED OUT |
| 1527 | 13. A BERTH PAYS BACK (FR3) |
| 1813 | 16. WARRANTED() READ ONCE IN A SEARCH (AP7) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1359 | `BuildAdviceCheck.RISE_A_MONTH` | `.03` | The share of its people the short city's rising history gains a month in section 10, over the businesses' window. |
| 1718 | `BuildAdviceCheck.SHORT_ON_THE_AVERAGE` | `1.3` | How many times its production the powered city draws before the Wind Farms are added (section 15): short on the average, so the wind is what clears it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 109 | `static int fails` |  |
| 110 | `static PrintStream out` |  |
| 111 | `static PrintStream quiet` |  |
| 676 | `static double wantBefore` | The students a school above the ladder would get and hire, read before the order stood (builtAndRead()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 107 | 2049 | **type** `public class BuildAdviceCheck` | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city - and the categories it is filed under. |
| 113 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 118 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 124 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 130 | 5 | `static void quietly(Runnable r)` |  |
| 136 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 169 | 3 | `static Game shortCity(Path root, String name, boolean onSiteClinic)` | A played city short of the city's works: about four thousand people in nine hundred houses on one paved road, no clinic or daycare (the founding's own care only), a school of each basic stage, a college, a university ... |
| 174 | 32 | `static Game shortCity(Path root, String name, boolean onSiteClinic, Set<String> without)` | ...without the buildings named in `without` (section 9: the short city with no university). |
| 214 | 24 | `static Game servedCity(Path root, String name)` | A smaller city well served: every measure NEEDS YOU watches for a city-built building past its line - care of every kind, a school of every basic stage, a college, a university and a medical school with seats for who ... |
| 239 | 34 | `public static void main(String[] args) throws Exception` |  |
| 275 | 13 | `static void printCity(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice)` | The city as the advice read it, for the record. |

### 1. THE CATEGORIES (lines 289-425)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 45 | `static void theCategories(Path root)` |  |
| 342 | 67 | `static void infrastructureAndFreight(BuildingManager bm, List<BuildAdvice.Category> cats)` | FR4 (2026-10-19; spec-freight-and-infrastructure 5, board 4): the city's things on one strip - Infrastructure holds the sea terminals and the Strategic Reserve - and the businesses' on theirs: Freight the rail rungs a... |
| 410 | 15 | `static boolean checkInvestors(List<BuildAdvice.Category> cats)` |  |

### 2. NEEDS YOU'S NEEDS, IN ITS ORDER (lines 426-478)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 41 | `static void needsYousOrder(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice)` |  |
| 471 | 7 | `static boolean onSiteAnswers(Game g, List<CityNeeds.Need> biting)` | Whether any biting city-built need has something on site that serves it. |

### 3. THE COUNT AND THE PRICE (lines 479-622)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 34 | `static void theCountAndThePrice(Game g, List<BuildAdvice.Suggestion> advice)` |  |
| 522 | 14 | `static boolean fitsOrNone(Game g, BuildAdvice.Suggestion s, double landLeft)` | The land rule: the chosen building's whole count fits the ground the cards before it leave, unless none that keeps the need ahead would - each counted as the advice counts it, at today's demand with the slack for its ... |
| 541 | 31 | `static double unitByTheModel(Game twin, BuildAdvice.Measure m, BuildingsTemplate t)` | One building's staffed capacity as the model counts it: the model's getter with one more of it standing in a twin, less the getter before. |
| 581 | 32 | `static void theCash(Path root)` | THE CASH, NO CAP (0.7.51): with the cash for the first card and half the second, every card is the count that keeps its need ahead, as with the Wealthy cash; the second and after are on credit for exactly what the cas... |
| 615 | 7 | `static boolean sameOrders(List<BuildAdvice.Suggestion> a, List<BuildAdvice.Suggestion> b)` | The same buildings, in the same counts, in the same order. |

### 4. BEFORE AND AFTER, BY THE MODEL (lines 623-808)

| line | len | member | says |
|---:|---:|---|---|
| 626 | 5 | `static Game twin(Game g)` | A twin of the city: the same save, loaded. |
| 644 | 13 | `static double builtAndRead(Game twin, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> add)` | The order and what is on site built standing in a twin, the month's own services pass run on it (SimulationEngine's last steps), and the figure read the way the model reads it. |
| 662 | 12 | `static double modelFirm(Game twin)` | The model's firm supply (PW3, PW9): the handler's production - the fixed jobless founding grid, the works' power, each plant at its own posts' fill and a station at its water - less each wind and solar farm's average ... |
| 679 | 71 | `static double readByTheModel(Game twin, BuildAdvice.Measure m)` | A measure's figure, read off the model: the handlers' own loads, the education month, the crime month. |
| 752 | 6 | `static Crime crimeMonth(Game twin, double officers, double cells, int months)` | The city's crime, restored into a fresh Crime and played `months` months on these officers and cells. |
| 760 | 8 | `static void advance(Game twin, Crime month, double officers, double cells)` | One month of crime on the city's own causes - the adults in each, from the pressure the city's month left. |
| 769 | 12 | `static void beforeAndAfter(Path root, Game g, List<BuildAdvice.Suggestion> advice)` |  |
| 783 | 25 | `static void everyMeasure(Path root, Game g)` | Every measure a city category opens on: an order of the first building that serves it, built in a twin. |

### 5. THE STAFFING WEIGHTING (lines 809-905)

| line | len | member | says |
|---:|---:|---|---|
| 816 | 70 | `static void theStaffingWeighting(Path root)` | Two clinics alike in everything - price, capacity, land, posts in all - but how many of the scarcest of the clinic's own posts they need: the fewer serve more. |
| 887 | 12 | `static BuildingsTemplate clone(BuildingsTemplate t, String name, JobType a, int na, JobType b, int nb)` |  |
| 900 | 5 | `static int posts(BuildingsTemplate t)` |  |

### 6. NOTHING NEEDED (lines 906-927)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 15 | `static void nothingNeeded(Path root)` |  |
| 924 | 3 | `static boolean bitsEqual(double a, double b)` |  |

### 7. LAND COUNTED (lines 928-1104)

| line | len | member | says |
|---:|---:|---|---|
| 954 | 111 | `static void landCounted(Path root)` | LAND COUNTED (0.7.51): the short city's cards with the land office's listing at nothing, as it stands, and at ten times its prices (each parcel's dollar price in LandMarket's listing state, scaled): every card's figur... |
| 1079 | 4 | `static double lifeFigure(Game g, BuildAdvice.Suggestion s, double landLeft)` | A card's figure, recomputed here from the model's own pieces (0.7.101, BuildAdvice, A BUILDING OVER ITS LIFE): its order over its life - the quote for its count (Game.quoteBuild(), or Game.quotePave() and the ground a... |
| 1084 | 1 | `static double planFigure(Game g, BuildAdvice.Suggestion s, double landLeft)` |  |
| 1087 | 3 | `static double[] valueFigure(Game g, BuildAdvice.Suggestion s, double landLeft)` | Recompute the invoice, delayed running, actual payroll return and physical relief; no reserve is lost work. |
| 1091 | 3 | `static double[] valueFigure(Game g, BuildAdvice.Suggestion s, double landLeft, double cashLeft)` |  |
| 1096 | 8 | `static double[][] scaledOffers(double[][] base, double f)` | The offers' records with each one's dollar price times f (LandParcel.offerRow(): the price second from the end). |

### 8. SLACK BUILT (lines 1105-1157)

| line | len | member | says |
|---:|---:|---|---|
| 1114 | 43 | `static void slackBuilt(Game g, List<BuildAdvice.Suggestion> advice)` | SLACK BUILT (0.7.51): a served card - power, water, the road, care, the schools - at 100% or more of its demand at its projection with SLACK on top, not just off NEEDS YOU's list; care's and a basic school's demand th... |

### 9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE (lines 1158-1347)

| line | len | member | says |
|---:|---:|---|---|
| 1179 | 88 | `static void thePipeline(Path root, Game g, List<CityNeeds.Need> all)` | NO HIGHER EDUCATION WITHOUT ITS PIPELINE (0.7.51). |
| 1269 | 18 | `static void higherRows(Game g, List<CityNeeds.Need> all, String where, int[] rows)` | Every school row above the ladder: no more wanted than would come or be hired, to the bit; with no seats, a first school's. |
| 1289 | 22 | `static void historicalPipeline()` | R1f: the young historical founding has no university class; its basic-school pipeline still gates advice. |
| 1312 | 4 | `static CityNeeds.Need schoolRow(List<CityNeeds.Need> all, EducationType t)` |  |
| 1317 | 6 | `static boolean suggests(Game g, List<CityNeeds.Need> all, EducationType t)` |  |
| 1325 | 22 | `static Game founded(Path root, String name, int month)` | The default playtest's founding (LongPlaytest's own: Standard, its first houses, shops and fields), played to `month`. |

### 10. SIZED TO THE PROJECTION (lines 1348-1407)

| line | len | member | says |
|---:|---:|---|---|
| 1372 | 35 | `static void sizedToTheProjection(Path root)` | SIZED TO THE PROJECTION (0.7.51): the short city's population history rising - RISE_A_MONTH a month over the businesses' window to the people it has - against the same history flat. |

### 11. THE RUN (lines 1408-1461)

| line | len | member | says |
|---:|---:|---|---|
| 1419 | 42 | `static void theRun(Path root, Game g, List<BuildAdvice.Suggestion> advice)` | THE RUN (0.7.51): "Build all three" places BuildAdvice.run(), the cards' orders in their order. |

### 12. THE BEDS LESS THE PRICED OUT (lines 1462-1526)

| line | len | member | says |
|---:|---:|---|---|
| 1475 | 51 | `static void thePricedOut(Game g)` | THE BEDS LESS THE PRICED OUT (0.7.106, batch P5; Jerus's A20 - 0.7.27's D16, "switch"). |

### 13. A BERTH PAYS BACK (FR3) (lines 1527-1812)

| line | len | member | says |
|---:|---:|---|---|
| 1539 | 103 | `static void aBerthPaysBack(Path root)` | A BERTH PAYS BACK (FR3, docs/queue/FR3.md 6 item 9): the advice offers a berth on its payback, Ports.berthPaysBack() - the kind's uncovered tonnes a month reaching WORTH_A_BERTH / the port dues dial of that berth's mo... |
| 1643 | 73 | `static void powerForTheCalmPeak(Game fixture)` |  |
| 1725 | 69 | `static void powerRowOnTheCalmPeak(Game fixture)` | 15. |
| 1796 | 4 | `static CityNeeds.Need powerRow(List<CityNeeds.Need> all)` | The NEEDS YOU POWER row of a measure, or null when the city draws nothing. |
| 1802 | 4 | `static BuildAdvice.Suggestion powerCard(List<BuildAdvice.Suggestion> advice)` | The card among the suggestions that is for power, or null. |
| 1808 | 4 | `static void causeDryBulk(Game g, double tonnes)` | Begins the city's Ports month and tells it `tonnes` of iron ore (dry bulk) crossed the boundary inbound, none by sea. |

### 16. WARRANTED() READ ONCE IN A SEARCH (AP7) (lines 1813-2155)

| line | len | member | says |
|---:|---:|---|---|
| 1819 | 1 | **type** `private record Town(Path root, Game game, BuildAdvice.Measure measure, String schoolName, String clinicName...` | The fixture town of section 16, saved to slot 10 of its scratch folder: the city every writer's own city is loaded from (cityOf()), with what it stands on - the measure, the schools' and the clinics' names, its clinic... |
| 1834 | 90 | `static void warrantHeld(Path root)` | AP7: BuildAdvice.Warrant reads warranted() at its first ask and holds it for one search or valuation, never between calls. |
| 1929 | 15 | `static Game cityOf(Town town, String what)` | A city for one writer: the fixture town's save (slot 10) loaded into a Game made for it, so that the first held reading of the measure made in it is the block's warm one. |
| 1946 | 3 | `static boolean underScratch(Path root, Game g)` | Whether the city's saves are under the harness's own scratch root (never the player's folder). |
| 1956 | 47 | `static void warrantResearch(Town town)` | 5. |
| 2008 | 18 | `static void warrantLoad(Town town, long fewer, long more)` | 6. |
| 2032 | 33 | `static void warrantCounts(Town town)` | The counts, and the run, in a city of its own. |
| 2071 | 26 | `static void warrantWriter(String what, Game g, BuildAdvice.Measure m, String plantName, Runnable writer)` | One writer: the readings made warm before it, the writer, the fixture's premise (it moved the bare warranted() across the threshold), and after it the readings held and bare, which must be the same to the bit, the hel... |
| 2104 | 18 | `static List<Long> readings(Game g, BuildAdvice.Measure m, BuildingsTemplate plant, boolean bare)` | The three readings a held warranted() reaches: the Build card the automatic builder reads (one marker for none, else the template, the count and every figure of the card), the count of the plant that keeps the measure... |
| 2124 | 15 | `static void figures(List<Long> into, Record record)` | Every number a record holds, doubles as their raw bits, the records inside it too. |
| 2141 | 9 | `static double leastServing(Game g, BuildAdvice.Measure m)` | The smallest unit any known template of the measure builds (BuildAdvice.warranted()'s own reading), infinite when none. |
| 2152 | 3 | `static BuildAdvice.Suggestion ask(Game g, BuildAdvice.Measure m)` | What the advice offers for a port measure, cash and land to spare. |

