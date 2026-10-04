# BuildAdvice.java - 952 lines · 50 methods · 4 constants · model

`ham/citybuildersim/BuildAdvice.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

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
> THE RULE (the brief's section 4), in one paragraph: take NEEDS YOU's needs
> in its order (CityNeeds.biting()) and keep those a city-built building
> answers; for each, of the buildings that serve its measure (for the roads,
> any road or line), the one with the lowest all-in price per unit of staffed
> capacity - preferring one that can close the gap at all, and then one whose
> whole count fits the land free; the count that closes the need's gap to
> the line NEEDS YOU lists it at, after what is on site at its staffed
> capacity, capped at what the cash left after the suggestions before it
> affords; at most three, one per need. Every judgement in it is named
> where it is made below, and in the project's design note for 0.7.24.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (58), [CityNeeds](CityNeeds.md) (54), [BuildingType](BuildingType.md) (35), [CareType](CareType.md) (31), [Game](Game.md) (22), [JobType](JobType.md) (15), [EducationType](EducationType.md) (12), [Healthcare](Healthcare.md) (7), [SafetyType](SafetyType.md) (6), [Crime](Crime.md) (6), [InfrastructureManager](InfrastructureManager.md) (6), [Traffic](Traffic.md) (4), [AgeBand](AgeBand.md) (4), [Education](Education.md) (4), [BuildingManager](BuildingManager.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [BuildingsStacks](BuildingsStacks.md) (1), [Health](Health.md) (1)

**Used by (16):** [BankScreen](BankScreen.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 40 | THE CATEGORIES (0.7.24) |
| 122 | THE MEASURES |
| 258 | STAFFED CAPACITY |
| 344 | THE FIGURE, BEFORE AND AFTER |
| 727 | THE SCARCE STAFF, AND WHAT AN ORDER NEEDS |
| 779 | THE SUGGESTIONS |

## Enum constants

| line | constant | says |
|---:|---|---|
| 133 | `BuildAdvice.Kind.POWER` |  |
| 133 | `BuildAdvice.Kind.WATER` |  |
| 133 | `BuildAdvice.Kind.ROADS` |  |
| 133 | `BuildAdvice.Kind.TRANSIT` |  |
| 133 | `BuildAdvice.Kind.CARE` |  |
| 133 | `BuildAdvice.Kind.DEATH` |  |
| 133 | `BuildAdvice.Kind.PLOTS` |  |
| 133 | `BuildAdvice.Kind.SCHOOL` |  |
| 133 | `BuildAdvice.Kind.POLICE` |  |
| 133 | `BuildAdvice.Kind.CELLS` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `BuildAdvice.OVERVIEW` | `"Overview"` | The page Build opens on: the city's job, what would help most, and what the market builds. |
| 68 | `BuildAdvice.UTILITIES` | `"Utilities", ROADS = "Roads & transit", HEALTHCARE = "Healthcare", EDUCATION ...` | The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. |
| 784 | `BuildAdvice.MAX_SUGGESTIONS` | `3` | At most this many suggestions, one per need. |
| 787 | `BuildAdvice.MOST` | `1<<22` | The most of one building a search will count to. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 917 | **type** `public final class BuildAdvice` | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure before and after an order by the model's own arithmetic, and the rule behin... |
| 38 | 1 | `private BuildAdvice()` |  |

### THE CATEGORIES (0.7.24) (lines 40-121)

| line | len | member | says |
|---:|---:|---|---|
| 62 | 4 | **type** `public record Category(String name, String was, EnumSet<BuildingType> types, boolean cityBuilds)` | One category of the strip. |
| 64 | 1 | `public EnumSet<BuildingType> types()` _(in BuildAdvice.Category)_ | A fresh set every call: EnumSet is mutable, and screens should not share one. |
| 74 | 19 | `public static List<Category> categories()` | The strip, in its order: the city's five, then the market's nine. |
| 95 | 7 | `public static Category category(String name)` | A category by its name - or by its label before 0.7.24. |
| 104 | 4 | `public static Category categoryOf(BuildingType type)` | The category a kind of building is in. |
| 110 | 11 | `public static Category categoryOf(CityNeeds.Go go)` | The category a NEEDS YOU row's fix is in, for the five the city builds and Homes; null for the rest. |

### THE MEASURES (lines 122-257)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 1 | **type** `public enum Kind` |  |
| 136 | 55 | **type** `public record Measure(Kind kind, CareType care, EducationType school)` | One measure: a kind, and the care type or the school it is about. |
| 138 | 1 | `public static Measure of(Kind kind)` _(in BuildAdvice.Measure)_ |  |
| 139 | 1 | `public static Measure care(CareType care)` _(in BuildAdvice.Measure)_ |  |
| 140 | 1 | `public static Measure school(EducationType school)` _(in BuildAdvice.Measure)_ |  |
| 143 | 16 | `public boolean serves(BuildingsTemplate t)` _(in BuildAdvice.Measure)_ | Whether this building serves the measure - the buildings its ring shows. |
| 161 | 9 | `public String category()` _(in BuildAdvice.Measure)_ | The category its buildings are in. |
| 172 | 15 | `public String label()` _(in BuildAdvice.Measure)_ | Its name, as a ring and a heading say it. |
| 189 | 1 | `public boolean isLoad()` _(in BuildAdvice.Measure)_ | True for a figure that is a load - the share of a network's capacity in use. |
| 193 | 28 | `public static List<Measure> measuresOf(String category)` | The measures a city category's page opens on, in its ring order; empty for a market category. |
| 223 | 16 | `public static Measure measureOf(CityNeeds.Need need)` | The measure a NEEDS YOU row is about, for the rows a city-built building answers; null for the rest. |
| 247 | 10 | `public static CityNeeds.Need needFor(List<CityNeeds.Need> all, Measure m)` | The NEEDS YOU row that judges a measure - the worst of them, as the panel would list them first, for death care whose two rows are the dead and the plots - or null for a measure no row watches (transit, a basic stage ... |

### STAFFED CAPACITY (lines 258-343)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 10 | `public static double staffing(BuildingsTemplate t, double[] fill)` | Σ posts x fill / Σ posts over a building's own posts; 1 with none - BuildingManager's per-building weighting. |
| 289 | 10 | `public static double unfilledPosts(BuildingsTemplate t, double[] fill)` | The posts of a building the city likely cannot fill: Σ posts x (1 - the city's fill rate for that job) over its own posts, 0 with none. |
| 301 | 4 | `public static boolean hasPosts(BuildingsTemplate t)` | Whether a building has any posts at all - a road has none, and its card says so instead of drawing a staff bar. |
| 315 | 18 | `public static double unit(Game game, Measure m, BuildingsTemplate t)` | What one building of this kind adds to a measure, in the measure's own unit, at today's staffing: generation or treatment a month, people cared for, places, officers, cells; for road capacity the trips it takes off wh... |
| 335 | 8 | `public static double built(Game game, Measure m, BuildingsTemplate t)` | The same, fully staffed: what the building is built for. |

### THE FIGURE, BEFORE AND AFTER (lines 344-726)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 10 | `public static Map<BuildingsTemplate, Integer> onSite(Game game, Measure m)` | Every unit on site of the buildings that serve a measure, for anybody's order. |
| 371 | 5 | `public static int units(Map<BuildingsTemplate, Integer> added)` | Units on site, all told. |
| 378 | 5 | `public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, BuildingsTemplate t, int n)` | A copy with n more of t. |
| 385 | 5 | `public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, Map<BuildingsTemplate, Integer> more)` | ...and with every quantity of another added. |
| 392 | 75 | `public static double figure(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | The measure's figure with these buildings standing as well. |
| 480 | 70 | `public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | What the measure has against what it needs, with these buildings standing as well, in the measure's own unit: generation or treatment against the draw; the road's capacity against the trips on it, and the room on tran... |
| 552 | 5 | `public static double cover(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | The figure as a share of what is needed, 0 to 1 - supply over demand, held to it - for a ring and the order bar's stacked bar. |
| 568 | 6 | `public static boolean isServed(Measure m)` | Whether a measure's gauge reads as served (0.7.41): power, water, the road, transit, care and the schools. |
| 576 | 5 | `public static double served(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | What the measure serves with these buildings standing as well (0.7.41): supply over demand, unclamped; NaN for a measure that is not served (isServed()). |
| 588 | 15 | `public static CityNeeds.Served verdict(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | ...and CityNeeds' one verdict on it (0.7.41): the lines of the NEEDS YOU row that watches the measure. |
| 605 | 11 | `public static double[] servedLines(Measure m)` | A served measure's two lines in served terms (0.7.41), {off NEEDS YOU's list past this, red at or under this}; null for transit and a measure not served. |
| 618 | 3 | `public static CityNeeds.Served verdictAfter(Game game, Suggestion s)` | What a suggestion would leave its measure serving, and the verdict on it (0.7.41): what is on site and the order, standing - the served side of its after(); null for a measure not served. |
| 623 | 16 | `static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added)` | The road network with these buildings standing: InfrastructureManager.with(), the model's own curve. |
| 648 | 4 | `static double roadsOver(Game game, Map<BuildingsTemplate, Integer> added)` | Trips over the line: what the road carries past STRAINED of its capacity, the line NEEDS YOU lists the roads at. |
| 654 | 15 | `static double unburiedNext(Game game, Map<BuildingsTemplate, Integer> added)` | The dead next month at today's deaths: those waiting and those dying, less what the free plots and the ovens take (Healthcare.settleDeaths()'s arithmetic). |
| 671 | 17 | `static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added)` | A school's figure: a basic stage's coverage (Education's own cover, places over the children it serves), or who would come over the seats above the ladder. |
| 689 | 8 | `static double officers(Game game, Map<BuildingsTemplate, Integer> added)` |  |
| 698 | 8 | `static double cells(Game game, Map<BuildingsTemplate, Integer> added)` |  |
| 713 | 13 | `public static boolean clear(Measure m, double figure)` | Whether a figure is off NEEDS YOU's list: past the line it is listed at (the yellow line, strictly), which way past depending on the row. |

### THE SCARCE STAFF, AND WHAT AN ORDER NEEDS (lines 727-778)

| line | len | member | says |
|---:|---:|---|---|
| 732 | 10 | `public static JobType scarceJob(BuildingsTemplate t, double[] fill)` | The job type among a building's posts with the city's lowest fill rate, or null for a building with no posts. |
| 744 | 8 | `public static double[] needs(Map<BuildingsTemplate, Integer> order)` | Posts and land an order needs: posts by job type (index JobType.ordinal()), then the land in the last slot. |
| 759 | 5 | `public static double quoteTotal(Game game, Map<BuildingsTemplate, Integer> order)` | What an order of several buildings costs all in (0.7.34): each one's Game.quoteBuild() total, as its own card quotes it and its own Build charges it - the order bar's "$X all in" and its Build button's label, which th... |
| 773 | 5 | `public static double quoteTotal(List<Suggestion> advice)` | ...and of the overview's suggestions (0.7.38): each one's own quote, its price(), which is Game.quoteBuild() as its card shows it and its Build charges it, added in their order - WHAT WOULD HELP MOST's "all three ≈ $X... |

### THE SUGGESTIONS (lines 779-952)

| line | len | member | says |
|---:|---:|---|---|
| 808 | 5 | **type** `public record Suggestion(CityNeeds.Need need, Measure measure, BuildingsTemplate template, int count, int f...` | One suggested order. |
| 815 | 3 | `public static List<Suggestion> suggest(Game game)` | The rule, on this city now. |
| 820 | 15 | `public static List<Suggestion> suggest(Game game, List<CityNeeds.Need> all)` | The rule, on NEEDS YOU as already measured (the screen measures it once a redraw). |
| 837 | 55 | `static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft)` | One need's suggestion, or null when what is on site already takes it off the list or no building can help. |
| 899 | 33 | `static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t)` | The count of t that closes the need, after what is on site: the least n whose figure is off the list. |
| 934 | 7 | `public static boolean higherWorse(Measure m)` | Which way is worse for a measure's figure. |
| 943 | 9 | `static int affordable(Game game, BuildingsTemplate t, int full, double cash)` | The most of t up to `full` whose quote the cash covers; 0 for none. |

