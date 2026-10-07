# BuildAdvice.java - 1,146 lines · 61 methods · 7 constants · model

`ham/citybuildersim/BuildAdvice.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> What the city should build next, and why: the Build tab's categories and
> the measures its five city categories open on, each measure's figure
> before and after an order by the model's own arithmetic, and the rule
> behind the overview's WHAT WOULD HELP MOST (0.7.24).
> 
> WHY. Jerus, starting the interface over one screen at a time with Build:
> "when you start the game you start in residential so the player without
> reading thinks he needs to building houses". Build opens on an overview
> now - the city's job, what would help most, and what the market builds by
> itself - and its first two rows are this class. It is ADVICE, NOT A MODEL
> CHANGE: it reads the city, quotes through Game.quoteBuild() (the quote the
> Build button charges) and places nothing; an order is placed only when the
> player clicks, through the Build button's own path. Pure, so the harness
> (BuildAdviceCheck) can hold it without the toolkit.
> 
> THE RULE (0.7.51; the brief's section 4 at 0.7.24), in one paragraph:
> take NEEDS YOU's needs in its order (CityNeeds.biting()) - a school above
> the ladder listed only for the students the city would both get and hire
> (CityNeeds.wanted(), listsSchool()) - and keep those a city-built building
> answers and what is on site does not already keep ahead; for each, of the
> buildings that serve its measure (for the roads, any road or line), the
> one with the lowest price per unit of staffed capacity with its ground
> at what the city would pay to replace it (landValue(), the land office's
> price) - preferring one that can keep the need ahead at all, and then one
> whose whole count fits the ground the suggestions before it leave; the
> count that keeps the need ahead at the demand it opens to, projected
> the way the businesses project theirs (above the ladder, the students it
> would get and hire by then, its feeder's graduates counted) and SLACK
> past it - off NEEDS YOU's list there, and a served gauge at 100% of it -
> after what is on site at its staffed capacity; on credit when its quote
> is more than the cash the ones before leave, never cut to the cash; at
> most three, one per need. Every judgement in it is named where it is made
> below, and in the project's design notes for 0.7.24 and 0.7.51.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (70), [CityNeeds](CityNeeds.md) (53), [BuildingType](BuildingType.md) (35), [CareType](CareType.md) (31), [Game](Game.md) (31), [JobType](JobType.md) (15), [EducationType](EducationType.md) (12), [InfrastructureManager](InfrastructureManager.md) (8), [Healthcare](Healthcare.md) (7), [SafetyType](SafetyType.md) (6), [Crime](Crime.md) (6), [Traffic](Traffic.md) (6), [BuildingManager](BuildingManager.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [AgeBand](AgeBand.md) (2), [Education](Education.md) (2), [BuildingsStacks](BuildingsStacks.md) (1), [Health](Health.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [LandManager](LandManager.md) (1)

**Used by (19):** [BankScreen](BankScreen.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 48 | THE CATEGORIES (0.7.24) |
| 130 | THE MEASURES |
| 266 | STAFFED CAPACITY |
| 364 | THE FIGURE, BEFORE AND AFTER |
| 798 | THE SCARCE STAFF, AND WHAT AN ORDER NEEDS |
| 850 | THE SUGGESTIONS |

## Enum constants

| line | constant | says |
|---:|---|---|
| 141 | `BuildAdvice.Kind.POWER` |  |
| 141 | `BuildAdvice.Kind.WATER` |  |
| 141 | `BuildAdvice.Kind.ROADS` |  |
| 141 | `BuildAdvice.Kind.TRANSIT` |  |
| 141 | `BuildAdvice.Kind.CARE` |  |
| 141 | `BuildAdvice.Kind.DEATH` |  |
| 141 | `BuildAdvice.Kind.PLOTS` |  |
| 141 | `BuildAdvice.Kind.SCHOOL` |  |
| 141 | `BuildAdvice.Kind.POLICE` |  |
| 141 | `BuildAdvice.Kind.CELLS` |  |
| 943 | `BuildAdvice.Lost.DEARER` |  |
| 943 | `BuildAdvice.Lost.CANNOT_CLOSE` |  |
| 943 | `BuildAdvice.Lost.NO_ROOM` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `BuildAdvice.OVERVIEW` | `"Overview"` | The page Build opens on: the city's job, what would help most, and what the market builds. |
| 76 | `BuildAdvice.UTILITIES` | `"Utilities", ROADS = "Roads & transit", HEALTHCARE = "Healthcare", EDUCATION ...` | The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. |
| 879 | `BuildAdvice.MAX_SUGGESTIONS` | `3` | At most this many suggestions, one per need. |
| 882 | `BuildAdvice.MOST` | `1<<22` | The most of one building a search will count to. |
| 885 | `BuildAdvice.SLACK` | `BusinessInvestment.TARGET_HEADROOM` | The slack an order is sized with past its projection: the businesses' own headroom (BusinessInvestment.TARGET_HEADROOM). |
| 888 | `BuildAdvice.HORIZON` | `BusinessInvestment.PLANNING_HORIZON` | Months past an order's opening it is sized for: the businesses' (BusinessInvestment.PLANNING_HORIZON); the opening's wait is held to their BusinessInvestment.MAX_ORDER_MONTHS. |
| 901 | `BuildAdvice.NOW` | `new Ahead(0, 1, 0)` | Today's demand, no slack: what NEEDS YOU reads. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 1103 | **type** `public final class BuildAdvice` | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure before and after an order by the model's own arithmetic, and the rule behin... |
| 46 | 1 | `private BuildAdvice()` |  |

### THE CATEGORIES (0.7.24) (lines 48-129)

| line | len | member | says |
|---:|---:|---|---|
| 70 | 4 | **type** `public record Category(String name, String was, EnumSet<BuildingType> types, boolean cityBuilds)` | One category of the strip. |
| 72 | 1 | `public EnumSet<BuildingType> types()` _(in BuildAdvice.Category)_ | A fresh set every call: EnumSet is mutable, and screens should not share one. |
| 82 | 19 | `public static List<Category> categories()` | The strip, in its order: the city's five, then the market's nine. |
| 103 | 7 | `public static Category category(String name)` | A category by its name - or by its label before 0.7.24. |
| 112 | 4 | `public static Category categoryOf(BuildingType type)` | The category a kind of building is in. |
| 118 | 11 | `public static Category categoryOf(CityNeeds.Go go)` | The category a NEEDS YOU row's fix is in, for the five the city builds and Homes; null for the rest. |

### THE MEASURES (lines 130-265)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 1 | **type** `public enum Kind` |  |
| 144 | 55 | **type** `public record Measure(Kind kind, CareType care, EducationType school)` | One measure: a kind, and the care type or the school it is about. |
| 146 | 1 | `public static Measure of(Kind kind)` _(in BuildAdvice.Measure)_ |  |
| 147 | 1 | `public static Measure care(CareType care)` _(in BuildAdvice.Measure)_ |  |
| 148 | 1 | `public static Measure school(EducationType school)` _(in BuildAdvice.Measure)_ |  |
| 151 | 16 | `public boolean serves(BuildingsTemplate t)` _(in BuildAdvice.Measure)_ | Whether this building serves the measure - the buildings its ring shows. |
| 169 | 9 | `public String category()` _(in BuildAdvice.Measure)_ | The category its buildings are in. |
| 180 | 15 | `public String label()` _(in BuildAdvice.Measure)_ | Its name, as a ring and a heading say it. |
| 197 | 1 | `public boolean isLoad()` _(in BuildAdvice.Measure)_ | True for a figure that is a load - the share of a network's capacity in use. |
| 201 | 28 | `public static List<Measure> measuresOf(String category)` | The measures a city category's page opens on, in its ring order; empty for a market category. |
| 231 | 16 | `public static Measure measureOf(CityNeeds.Need need)` | The measure a NEEDS YOU row is about, for the rows a city-built building answers; null for the rest. |
| 255 | 10 | `public static CityNeeds.Need needFor(List<CityNeeds.Need> all, Measure m)` | The NEEDS YOU row that judges a measure - the worst of them, as the panel would list them first, for death care whose two rows are the dead and the plots - or null for a measure no row watches (transit, a basic stage ... |

### STAFFED CAPACITY (lines 266-363)

| line | len | member | says |
|---:|---:|---|---|
| 278 | 10 | `public static double staffing(BuildingsTemplate t, double[] fill)` | Σ posts x fill / Σ posts over a building's own posts; 1 with none - BuildingManager's per-building weighting. |
| 297 | 10 | `public static double unfilledPosts(BuildingsTemplate t, double[] fill)` | The posts of a building the city likely cannot fill: Σ posts x (1 - the city's fill rate for that job) over its own posts, 0 with none. |
| 309 | 4 | `public static boolean hasPosts(BuildingsTemplate t)` | Whether a building has any posts at all - a road has none, and its card says so instead of drawing a staff bar. |
| 323 | 30 | `public static double unit(Game game, Measure m, BuildingsTemplate t)` | What one building of this kind adds to a measure, in the measure's own unit, at today's staffing: generation or treatment a month, people cared for, places, officers, cells; for road capacity the trips it takes off wh... |
| 355 | 8 | `public static double built(Game game, Measure m, BuildingsTemplate t)` | The same, fully staffed: what the building is built for. |

### THE FIGURE, BEFORE AND AFTER (lines 364-797)

| line | len | member | says |
|---:|---:|---|---|
| 382 | 10 | `public static Map<BuildingsTemplate, Integer> onSite(Game game, Measure m)` | Every unit on site of the buildings that serve a measure, for anybody's order. |
| 394 | 5 | `public static int units(Map<BuildingsTemplate, Integer> added)` | Units on site, all told. |
| 401 | 5 | `public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, BuildingsTemplate t, int n)` | A copy with n more of t. |
| 408 | 5 | `public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, Map<BuildingsTemplate, Integer> more)` | ...and with every quantity of another added. |
| 415 | 3 | `public static double figure(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | The measure's figure with these buildings standing as well. |
| 420 | 86 | `public static double figure(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p)` | ...against the demand `p` says (0.7.51): today's times p.scale() - projected and padded - on the demand side only. |
| 519 | 3 | `public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | What the measure has against what it needs, with these buildings standing as well, in the measure's own unit: generation or treatment against the draw; the road's capacity against the trips on it, and the room on tran... |
| 524 | 61 | `public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p)` | ...against the demand `p` says (0.7.51), as figure() reads it: the supply side unchanged. |
| 593 | 10 | `static double schoolNeed(Game game, EducationType type, Ahead p)` | The people a school serves at `p`'s demand (0.7.51): a basic stage's children or teens times p.scale(); above the ladder the students the city would get and hire by then (CityNeeds.wanted(), its posts grown by p.k()) ... |
| 605 | 5 | `public static double cover(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | The figure as a share of what is needed, 0 to 1 - supply over demand, held to it - for a ring and the order bar's stacked bar. |
| 621 | 6 | `public static boolean isServed(Measure m)` | Whether a measure's gauge reads as served (0.7.41): power, water, the road, transit, care and the schools. |
| 629 | 5 | `public static double served(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | What the measure serves with these buildings standing as well (0.7.41): supply over demand, unclamped; NaN for a measure that is not served (isServed()). |
| 643 | 16 | `public static CityNeeds.Served verdict(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | ...and CityNeeds' one verdict on it (0.7.41): the lines of the NEEDS YOU row that watches the measure. |
| 661 | 11 | `public static double[] servedLines(Measure m)` | A served measure's two lines in served terms (0.7.41), {off NEEDS YOU's list past this, red at or under this}; null for transit and a measure not served. |
| 674 | 3 | `public static CityNeeds.Served verdictAfter(Game game, Suggestion s)` | What a suggestion would leave its measure serving, and the verdict on it (0.7.41): what is on site and the order, standing - the served side of its after(); null for a measure not served. |
| 679 | 3 | `static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added)` | The road network with these buildings standing: InfrastructureManager.with(), the model's own curve. |
| 684 | 18 | `static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added, double k)` | ...with the city's own traffic times k (0.7.51): the load and every stream grown alike, then the buildings' own as before. |
| 711 | 4 | `static double roadsOver(Game game, Map<BuildingsTemplate, Integer> added)` | Trips over the line: what the road carries past STRAINED of its capacity, the line NEEDS YOU lists the roads at. |
| 717 | 3 | `static double unburiedNext(Game game, Map<BuildingsTemplate, Integer> added)` | The dead next month at today's deaths: those waiting and those dying, less what the free plots and the ovens take (Healthcare.settleDeaths()'s arithmetic). |
| 722 | 16 | `static double unburiedNext(Game game, Map<BuildingsTemplate, Integer> added, double k)` | ...with k times today's deaths (0.7.51): the unburied are a stock, and stay as they are. |
| 740 | 3 | `static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added)` | A school's figure: a basic stage's coverage (Education's own cover, places over the children it serves), or above the ladder who would come and be hired over the seats (0.7.51; who would come until then). |
| 745 | 14 | `static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added, Ahead p)` | ...against the people `p` says it serves (schoolNeed()). |
| 760 | 8 | `static double officers(Game game, Map<BuildingsTemplate, Integer> added)` |  |
| 769 | 8 | `static double cells(Game game, Map<BuildingsTemplate, Integer> added)` |  |
| 784 | 13 | `public static boolean clear(Measure m, double figure)` | Whether a figure is off NEEDS YOU's list: past the line it is listed at (the yellow line, strictly), which way past depending on the row. |

### THE SCARCE STAFF, AND WHAT AN ORDER NEEDS (lines 798-849)

| line | len | member | says |
|---:|---:|---|---|
| 803 | 10 | `public static JobType scarceJob(BuildingsTemplate t, double[] fill)` | The job type among a building's posts with the city's lowest fill rate, or null for a building with no posts. |
| 815 | 8 | `public static double[] needs(Map<BuildingsTemplate, Integer> order)` | Posts and land an order needs: posts by job type (index JobType.ordinal()), then the land in the last slot. |
| 830 | 5 | `public static double quoteTotal(Game game, Map<BuildingsTemplate, Integer> order)` | What an order of several buildings costs all in (0.7.34): each one's Game.quoteBuild() total, as its own card quotes it and its own Build charges it - the order bar's "$X all in" and its Build button's label, which th... |
| 844 | 5 | `public static double quoteTotal(List<Suggestion> advice)` | ...and of the overview's suggestions (0.7.38): each one's own quote, its price(), which is Game.quoteBuild() as its card shows it and its Build charges it, added in their order. |

### THE SUGGESTIONS (lines 850-1146)

| line | len | member | says |
|---:|---:|---|---|
| 895 | 4 | **type** `public record Ahead(double months, double k, double slack)` | The demand an order is sized against (0.7.51): today's, `months` ahead grown by k (BusinessInvestment.growthFactor()), and padded by `slack`. |
| 897 | 1 | `public double scale()` _(in BuildAdvice.Ahead)_ | What today's demand is multiplied by. |
| 909 | 5 | `public static Ahead opening(Game game, double lead)` | The demand an order that opens after `lead` months is sized to: the wait, held to the businesses' MAX_ORDER_MONTHS (a stalled one, NaN, counts as none), plus HORIZON - the businesses' growthFactor() over those months ... |
| 921 | 6 | `public static boolean ahead(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p)` | Whether these buildings keep the measure ahead at `p`'s demand: off NEEDS YOU's list there (clear()), and a served measure - not transit, which has no line - at 100% of it or more. |
| 935 | 6 | `public static double landValue(Game game, double sqFt, double landLeft)` | What `sqFt` of ground is worth to the city with `landLeft` free: the free part at the cheaper of what a business pays the city for it and the land office's price, the rest at the land office's (LandManager.getOfficePr... |
| 943 | 1 | **type** `public enum Lost` | Why the next building in the ranking lost to the one suggested: dearer a unit with its land, it cannot keep the need ahead, or its count needs more land than is left. |
| 980 | 6 | **type** `public record Suggestion(CityNeeds.Need need, Measure measure, BuildingsTemplate template, int count, doubl...` | One suggested order (0.7.51: one count, the one that keeps the need ahead - the cash no longer caps it). |
| 988 | 3 | `public static List<Suggestion> suggest(Game game)` | The rule, on this city now. |
| 997 | 17 | `public static List<Suggestion> suggest(Game game, List<CityNeeds.Need> all)` | The rule, on NEEDS YOU as already measured (the screen measures it once a redraw): each card on the cash and the ground the ones before it leave. |
| 1016 | 5 | `public static LinkedHashMap<BuildingsTemplate, Integer> run(List<Suggestion> advice)` | The suggestions as one run, as "Build all three" places it: each card's building and count in their order, two of one building added together. |
| 1023 | 2 | **type** `private record Weighed(BuildingsTemplate t, int tier, double per, double unit, int count, boolean closes, b...` | One building weighed for a need: its count at its projection, and where it ranks. |
| 1027 | 57 | `static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft)` | One need's suggestion, or null when what is on site already keeps it ahead or no building can help. |
| 1090 | 3 | `static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t)` | The count before 0.7.51, kept for the harness's comparison: the least n whose figure today is off the list (count() at no projection and no slack, judged by clear() alone). |
| 1100 | 37 | `static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, Ahead p)` | The count of t that keeps the need ahead at p's demand, after what is on site (p null: the old rule, off the list today). |
| 1139 | 7 | `public static boolean higherWorse(Measure m)` | Which way is worse for a measure's figure. |

