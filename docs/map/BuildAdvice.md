# BuildAdvice.java - 2,217 lines · 110 methods · 9 constants · model

`ham/citybuildersim/BuildAdvice.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
> buildings that serve its measure (for the roads, any road or line, and
> the city's gravel roads paved beside them), the one whose order costs
> least over its life for each unit it will serve (since 0.7.101,
> lifePerServed(): its quote, its ground at what the city would pay to
> replace it - landValue(), the land office's price - and its running for
> BUILD_BOND_YEARS, as a road's has been since 0.7.70; over what it will
> serve of the need as the city grows into it, for power, water, the
> roads and care, so a building far bigger than the need pays for what
> stands idle, and what its count adds for the rest; see A BUILDING OVER
> ITS LIFE) - preferring one that can keep the need ahead at all, and
> then one
> whose whole count fits the ground the suggestions before it leave; the
> count that keeps the need ahead at the demand it opens to, projected
> the way the businesses project theirs (above the ladder, the students it
> would get and hire by then, its feeder's graduates counted) and SLACK
> past it - off NEEDS YOU's list there, and a served gauge at 100% of it -
> after what is on site at its staffed capacity; on credit when its quote
> is more than the cash the ones before leave, never cut to the cash; at
> most three, one per need. Every judgement in it is named where it is made
> below, and in the project's design notes for 0.7.24 and 0.7.51. A first
> building where none stands is suggested however far past the need one
> goes (Jerus's school at 0% that one would take to 20,000%): the overshoot
> ranks the candidates, it never stops one (0.7.101).

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (145), [Game](Game.md) (73), [CityNeeds](CityNeeds.md) (59), [BuildingType](BuildingType.md) (42), [CareType](CareType.md) (33), [Ports](Ports.md) (19), [JobType](JobType.md) (15), [InfrastructureManager](InfrastructureManager.md) (15), [EducationType](EducationType.md) (13), [ConstructionControl](ConstructionControl.md) (12), [Good](Good.md) (10), [BusinessInvestment](BusinessInvestment.md) (8), [Healthcare](Healthcare.md) (7), [SafetyType](SafetyType.md) (6), [Crime](Crime.md) (6), [Traffic](Traffic.md) (6), [Sectors](Sectors.md) (6), [BuildingManager](BuildingManager.md) (5), [LandParcel](LandParcel.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (3), [Deposit](Deposit.md) (3), [Resource](Resource.md) (3), [AgeBand](AgeBand.md) (2), [Education](Education.md) (2), [ExtractionSector](ExtractionSector.md) (2), [Sector](Sector.md) (2), [GoodsMarket](GoodsMarket.md) (2), [Landmasses](Landmasses.md) (2), [StrategicReserve](StrategicReserve.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)... and 8 more

**Used by (27):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [BridgeCheck](BridgeCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [HydroCheck](HydroCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 58 | THE CATEGORIES (0.7.24) |
| 195 | THE MEASURES |
| 369 | STAFFED CAPACITY |
| 479 | THE FIGURE, BEFORE AND AFTER |
| 984 | THE SCARCE STAFF, AND WHAT AN ORDER NEEDS |
| 1036 | THE SUGGESTIONS |
| 1428 | THE SIZE THAT FITS THE NEED (0.7.71) |
| 1451 | A ROAD OVER ITS LIFE (0.7.70) |
| 1675 | A BUILDING OVER ITS LIFE, FOR WHAT IT WILL SERVE (0.7.101, batch P2) |
| 1851 | AN IRON FIELD THAT WILL NOT PAY BACK (0.7.101, batch P2) |
| 2046 | A BERTH THAT PAYS BACK (FR3, 2026-10-19; docs/queue/FR3.md 5, the |
| 2142 | · A BERTH'S PAYBACK, FOR ITS CARD (FR4, 2026-10-20) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 234 | `BuildAdvice.Kind.POWER` |  |
| 234 | `BuildAdvice.Kind.WATER` |  |
| 234 | `BuildAdvice.Kind.ROADS` |  |
| 234 | `BuildAdvice.Kind.TRANSIT` |  |
| 234 | `BuildAdvice.Kind.CARE` |  |
| 234 | `BuildAdvice.Kind.DEATH` |  |
| 234 | `BuildAdvice.Kind.PLOTS` |  |
| 234 | `BuildAdvice.Kind.SCHOOL` |  |
| 234 | `BuildAdvice.Kind.POLICE` |  |
| 234 | `BuildAdvice.Kind.CELLS` |  |
| 234 | `BuildAdvice.Kind.HIGHWAYS` |  |
| 234 | `BuildAdvice.Kind.PORT` |  |
| 1144 | `BuildAdvice.Lost.DEARER` |  |
| 1144 | `BuildAdvice.Lost.CANNOT_CLOSE` |  |
| 1144 | `BuildAdvice.Lost.NO_ROOM` |  |
| 2154 | `BuildAdvice.BerthState.PAYS` |  |
| 2154 | `BuildAdvice.BerthState.SHORT` |  |
| 2154 | `BuildAdvice.BerthState.COVERED` |  |
| 2154 | `BuildAdvice.BerthState.NO_DUES` |  |
| 2154 | `BuildAdvice.BerthState.UPKEEP` |  |
| 2154 | `BuildAdvice.BerthState.NO_CARGO` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 93 | `BuildAdvice.OVERVIEW` | `"Overview"` | The page Build opens on: the city's job, what would help most, and what the market builds. |
| 121 | `BuildAdvice.UTILITIES` | `"Utilities", ROADS = "Infrastructure", HEALTHCARE = "Healthcare", EDUCATION =...` | The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. |
| 1065 | `BuildAdvice.MAX_SUGGESTIONS` | `3` | At most this many suggestions, one per need. |
| 1068 | `BuildAdvice.MOST` | `1<<22` | The most of one building a search will count to. |
| 1071 | `BuildAdvice.SLACK` | `BusinessInvestment.TARGET_HEADROOM` | The slack an order is sized with past its projection: the businesses' own headroom (BusinessInvestment.TARGET_HEADROOM). |
| 1074 | `BuildAdvice.HORIZON` | `BusinessInvestment.PLANNING_HORIZON` | Months past an order's opening it is sized for: the businesses' (BusinessInvestment.PLANNING_HORIZON); the opening's wait is held to their BusinessInvestment.MAX_ORDER_MONTHS. |
| 1087 | `BuildAdvice.NOW` | `new Ahead(0, 1, 0)` | Today's demand, no slack: what NEEDS YOU reads. |
| 1509 | `BuildAdvice.LIFE_MONTHS` | `Game.BUILD_BOND_YEARS * 12` | Months a road is weighed over (0.7.70): the funding page's bond term, Game.BUILD_BOND_YEARS - a long-lived asset "paid for over the years the city uses it". |
| 1719 | `BuildAdvice.LIFE_YEARS` | `LIFE_MONTHS / 12` | The years of a building's life the served figure reads: LIFE_MONTHS in whole years. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 2164 | **type** `public final class BuildAdvice` | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure before and after an order by the model's own arithmetic, and the rule behin... |
| 56 | 1 | `private BuildAdvice()` |  |

### THE CATEGORIES (0.7.24) (lines 58-194)

| line | len | member | says |
|---:|---:|---|---|
| 104 | 15 | **type** `public record Category(String name, String was, EnumSet<BuildingType> types, boolean cityBuilds)` | One category of the strip. |
| 106 | 1 | `public EnumSet<BuildingType> types()` _(in BuildAdvice.Category)_ | A fresh set every call: EnumSet is mutable, and screens should not share one. |
| 113 | 5 | `public boolean holds(BuildingsTemplate t)` _(in BuildAdvice.Category)_ | Whether this building is on the category's page: its type is among the category's - but for the Strategic Reserve, a HEAVY_INDUSTRY building the city alone builds, which is Infrastructure's and not Industry's (FR4). |
| 130 | 22 | `public static List<Category> categories()` | The strip, in its order: the city's five, then the market's nine. |
| 154 | 7 | `public static Category category(String name)` | A category by its name - or by its label before 0.7.24. |
| 163 | 4 | `public static Category categoryOf(BuildingType type)` | The category a kind of building is in, by its type: the Strategic Reserve's is Industry's, whose it is not (categoryOf(BuildingsTemplate) knows). |
| 169 | 5 | `public static Category categoryOf(BuildingsTemplate t)` | The category a building is on the page of (FR4): its type's, but for the Strategic Reserve, which is Infrastructure's (Category.holds()). |
| 176 | 5 | `public static List<BuildingsTemplate> templatesOf(Category c, BuildingManager buildings)` | A category's buildings in catalogue order: those of its types it holds, and the reserve in Infrastructure's. |
| 183 | 11 | `public static Category categoryOf(CityNeeds.Go go)` | The category a NEEDS YOU row's fix is in, for the five the city builds and Homes; null for the rest. |

### THE MEASURES (lines 195-368)

| line | len | member | says |
|---:|---:|---|---|
| 234 | 1 | **type** `public enum Kind` | The measures. |
| 237 | 63 | **type** `public record Measure(Kind kind, CareType care, EducationType school, Ports.Cargo cargo)` | One measure: a kind, and the care type or the school it is about - or, for PORT, the cargo (null for every other kind). |
| 239 | 1 | `public static Measure of(Kind kind)` _(in BuildAdvice.Measure)_ |  |
| 240 | 1 | `public static Measure care(CareType care)` _(in BuildAdvice.Measure)_ |  |
| 241 | 1 | `public static Measure school(EducationType school)` _(in BuildAdvice.Measure)_ |  |
| 243 | 1 | `public static Measure port(Ports.Cargo cargo)` _(in BuildAdvice.Measure)_ | The berths of a kind of cargo (FR3): the sea terminals that handle it. |
| 246 | 19 | `public boolean serves(BuildingsTemplate t)` _(in BuildAdvice.Measure)_ | Whether this building serves the measure - the buildings its ring shows. |
| 267 | 10 | `public String category()` _(in BuildAdvice.Measure)_ | The category its buildings are in. |
| 279 | 17 | `public String label()` _(in BuildAdvice.Measure)_ | Its name, as a ring and a heading say it. |
| 298 | 1 | `public boolean isLoad()` _(in BuildAdvice.Measure)_ | True for a figure that is a load - the share of a network's capacity in use. |
| 302 | 29 | `public static List<Measure> measuresOf(String category)` | The measures a city category's page opens on, in its ring order; empty for a market category. |
| 333 | 17 | `public static Measure measureOf(CityNeeds.Need need)` | The measure a NEEDS YOU row is about, for the rows a city-built building answers; null for the rest. |
| 358 | 10 | `public static CityNeeds.Need needFor(List<CityNeeds.Need> all, Measure m)` | The NEEDS YOU row that judges a measure - the worst of them, as the panel would list them first, for death care whose two rows are the dead and the plots - or null for a measure no row watches (transit, a basic stage ... |

### STAFFED CAPACITY (lines 369-478)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 10 | `public static double staffing(BuildingsTemplate t, double[] fill)` | Σ posts x fill / Σ posts over a building's own posts; 1 with none - BuildingManager's per-building weighting. |
| 400 | 10 | `public static double unfilledPosts(BuildingsTemplate t, double[] fill)` | The posts of a building the city likely cannot fill: Σ posts x (1 - the city's fill rate for that job) over its own posts, 0 with none. |
| 412 | 4 | `public static boolean hasPosts(BuildingsTemplate t)` | Whether a building has any posts at all - a road has none, and its card says so instead of drawing a staff bar. |
| 426 | 41 | `public static double unit(Game game, Measure m, BuildingsTemplate t)` | What one building of this kind adds to a measure, in the measure's own unit, at today's staffing: generation or treatment a month, people cared for, places, officers, cells; for road capacity the trips it takes off wh... |
| 469 | 9 | `public static double built(Game game, Measure m, BuildingsTemplate t)` | The same, fully staffed: what the building is built for. |

### THE FIGURE, BEFORE AND AFTER (lines 479-983)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 1 | `public static boolean roadKind(Measure m)` | Whether a measure is one of the two road needs (FR7): the streets or the highways, which read the same network, so each counts every road on site. |
| 500 | 17 | `public static Map<BuildingsTemplate, Integer> onSite(Game game, Measure m)` | Every unit on site of the buildings that serve a measure, for anybody's order. |
| 519 | 5 | `public static int units(Map<BuildingsTemplate, Integer> added)` | Units on site, all told: the buildings going up (a gravel road a paving takes away is not one). |
| 526 | 5 | `public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, BuildingsTemplate t, int n)` | A copy with n more of t. |
| 533 | 5 | `public static Map<BuildingsTemplate, Integer> plus(Map<BuildingsTemplate, Integer> base, Map<BuildingsTemplate, Integer> more)` | ...and with every quantity of another added. |
| 540 | 3 | `public static double figure(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | The measure's figure with these buildings standing as well. |
| 545 | 86 | `public static double figure(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p)` | ...against the demand `p` says (0.7.51): today's times p.scale() - projected and padded - on the demand side only. |
| 644 | 3 | `public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | What the measure has against what it needs, with these buildings standing as well, in the measure's own unit: generation or treatment against the draw; the road's capacity against the trips on it, and the room on tran... |
| 649 | 67 | `public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p)` | ...against the demand `p` says (0.7.51), as figure() reads it: the supply side unchanged. |
| 724 | 10 | `static double schoolNeed(Game game, EducationType type, Ahead p)` | The people a school serves at `p`'s demand (0.7.51): a basic stage's children or teens times p.scale(); above the ladder the students the city would get and hire by then (CityNeeds.wanted(), its posts grown by p.k()) ... |
| 744 | 7 | `static double carePlaces(Game game, CareType care, Map<BuildingsTemplate, Integer> added, double[] fill)` | A kind of care's places with these buildings standing as well: the staffed beds and the order's at the fill, less the people the fee turned away last month (Healthcare.getPricedOut()) - the beds less the priced out, a... |
| 753 | 5 | `public static double cover(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | The figure as a share of what is needed, 0 to 1 - supply over demand, held to it - for a ring and the order bar's stacked bar. |
| 769 | 6 | `public static boolean isServed(Measure m)` | Whether a measure's gauge reads as served (0.7.41): power, water, the road, transit, care and the schools. |
| 777 | 5 | `public static double served(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | What the measure serves with these buildings standing as well (0.7.41): supply over demand, unclamped; NaN for a measure that is not served (isServed()). |
| 791 | 17 | `public static CityNeeds.Served verdict(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | ...and CityNeeds' one verdict on it (0.7.41): the lines of the NEEDS YOU row that watches the measure. |
| 810 | 11 | `public static double[] servedLines(Measure m)` | A served measure's two lines in served terms (0.7.41), {off NEEDS YOU's list past this, red at or under this}; null for transit, for the highways (their verdict is the row's own level: CityNeeds.highwayVerdict()) and ... |
| 823 | 3 | `public static CityNeeds.Served verdictAfter(Game game, Suggestion s)` | What a suggestion would leave its measure serving, and the verdict on it (0.7.41): what is on site and the order, standing - the served side of its after(); null for a measure not served. |
| 828 | 4 | `public static Map<BuildingsTemplate, Integer> added(Game game, Suggestion s)` | What a suggestion changes on the city: its count of its building - and for a paving (0.7.70), as many gravel roads gone. |
| 834 | 3 | `static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added)` | The road network with these buildings standing: InfrastructureManager.with(), the model's own curve. |
| 839 | 25 | `static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Integer> added, double k)` | ...with the city's own traffic times k (0.7.51): the load and every stream grown alike, then the buildings' own as before. |
| 871 | 8 | `public static double highwayFlow(Game game)` | The trips a month one more highway carries at free flow (FR7): the Elevated Highway's capacity at FREE_FLOW - the unit the through need is read in, so a share of one is a share of a highway worth building. |
| 881 | 4 | `static double highwayFill(Game game, InfrastructureManager r)` | The through need's figure (FR7): the through trips with no highway to take them, over what one more highway carries (CityNeeds.HIGHWAY_MIN_FILL of one bites); 0 with no highway to build. |
| 894 | 5 | `static double roadsOver(Game game, Map<BuildingsTemplate, Integer> added)` | Trips over the line: what the road carries past STRAINED of its capacity, the line NEEDS YOU lists the roads at. |
| 901 | 3 | `static double unburiedNext(Game game, Map<BuildingsTemplate, Integer> added)` | The dead next month at today's deaths: those waiting and those dying, less what the free plots and the ovens take (Healthcare.settleDeaths()'s arithmetic). |
| 906 | 16 | `static double unburiedNext(Game game, Map<BuildingsTemplate, Integer> added, double k)` | ...with k times today's deaths (0.7.51): the unburied are a stock, and stay as they are. |
| 924 | 3 | `static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added)` | A school's figure: a basic stage's coverage (Education's own cover, places over the children it serves), or above the ladder who would come and be hired over the seats (0.7.51; who would come until then). |
| 929 | 14 | `static double school(Game game, EducationType type, Map<BuildingsTemplate, Integer> added, Ahead p)` | ...against the people `p` says it serves (schoolNeed()). |
| 944 | 8 | `static double officers(Game game, Map<BuildingsTemplate, Integer> added)` |  |
| 953 | 8 | `static double cells(Game game, Map<BuildingsTemplate, Integer> added)` |  |
| 968 | 15 | `public static boolean clear(Measure m, double figure)` | Whether a figure is off NEEDS YOU's list: past the line it is listed at (the yellow line, strictly), which way past depending on the row. |

### THE SCARCE STAFF, AND WHAT AN ORDER NEEDS (lines 984-1035)

| line | len | member | says |
|---:|---:|---|---|
| 989 | 10 | `public static JobType scarceJob(BuildingsTemplate t, double[] fill)` | The job type among a building's posts with the city's lowest fill rate, or null for a building with no posts. |
| 1001 | 8 | `public static double[] needs(Map<BuildingsTemplate, Integer> order)` | Posts and land an order needs: posts by job type (index JobType.ordinal()), then the land in the last slot. |
| 1016 | 5 | `public static double quoteTotal(Game game, Map<BuildingsTemplate, Integer> order)` | What an order of several buildings costs all in (0.7.34): each one's Game.quoteBuild() total, as its own card quotes it and its own Build charges it - the order bar's "$X all in" and its Build button's label, which th... |
| 1030 | 5 | `public static double quoteTotal(List<Suggestion> advice)` | ...and of the overview's suggestions (0.7.38): each one's own quote, its price(), which is Game.quoteBuild() as its card shows it and its Build charges it, added in their order. |

### THE SUGGESTIONS (lines 1036-1427)

| line | len | member | says |
|---:|---:|---|---|
| 1081 | 4 | **type** `public record Ahead(double months, double k, double slack)` | The demand an order is sized against (0.7.51): today's, `months` ahead grown by k (BusinessInvestment.growthFactor()), and padded by `slack`. |
| 1083 | 1 | `public double scale()` _(in BuildAdvice.Ahead)_ | What today's demand is multiplied by. |
| 1095 | 3 | `public static Ahead opening(Game game, double lead)` | The demand an order that opens after `lead` months is sized to: the wait, held to the businesses' MAX_ORDER_MONTHS (a stalled one, NaN, counts as none), plus HORIZON - the businesses' growthFactor() over those months ... |
| 1100 | 5 | `public static Ahead opening(Game game, double lead, double slack)` | ...with another slack past it (0.7.73): automatic building's, the player's slider (AutoBuilder). |
| 1120 | 8 | `public static boolean ahead(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p)` | Whether these buildings keep the measure ahead at `p`'s demand: off NEEDS YOU's list there (clear()), and a served measure - not transit, which has no line - at 100% of it or more. |
| 1136 | 6 | `public static double landValue(Game game, double sqFt, double landLeft)` | What `sqFt` of ground is worth to the city with `landLeft` free: the free part at the cheaper of what a business pays the city for it and the land office's price, the rest at the land office's (LandManager.getOfficePr... |
| 1144 | 1 | **type** `public enum Lost` | Why the next building in the ranking lost to the one suggested: dearer over its life for what it serves (a unit with its land until 0.7.101), it cannot keep the need ahead, or its count needs more land than is left. |
| 1188 | 7 | **type** `public record Suggestion(CityNeeds.Need need, Measure measure, BuildingsTemplate template, int count, doubl...` | One suggested order (0.7.51: one count, the one that keeps the need ahead - the cash no longer caps it). |
| 1197 | 3 | `public static List<Suggestion> suggest(Game game)` | The rule, on this city now. |
| 1206 | 17 | `public static List<Suggestion> suggest(Game game, List<CityNeeds.Need> all)` | The rule, on NEEDS YOU as already measured (the screen measures it once a redraw): each card on the cash and the ground the ones before it leave. |
| 1225 | 5 | `public static LinkedHashMap<BuildingsTemplate, Integer> run(List<Suggestion> advice)` | The suggestions as one run, as "Build all three" places it: each card's building and count in their order, two of one building added together - and no paving (0.7.70), which is not a build order and has its card's own... |
| 1232 | 5 | `public static List<Suggestion> builds(List<Suggestion> advice)` | The suggestions "Build all three" places (0.7.70): every one but a paving. |
| 1239 | 2 | **type** `private record Weighed(BuildingsTemplate t, int tier, double per, double unit, int count, boolean closes, b...` | One building weighed for a need: its count at its projection, and where it ranks - or, paving, gravel roads paved (0.7.70). |
| 1243 | 3 | `static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft)` | One need's suggestion, or null when what is on site already keeps it ahead or no building can help. |
| 1255 | 4 | `public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double slack)` | ...sized with `slack` past its projection in place of SLACK (0.7.73): automatic building's spare margin, the player's slider (AutoBuilder) - the same ranking, the same projection, the same count at another slack. |
| 1261 | 4 | `public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double slack, ...` | ...with the buildings in `skip` left out of the ranking (0.7.73): automatic building's, for one that would not pay back (since 0.7.104; one its budget could not run from 0.7.73), and since 0.7.101 one the money or the... |
| 1267 | 85 | `public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double slack, ...` | ...and with the city's gravel roads paved left out too when `noPaving` (0.7.101): a paving the money cannot pay for, passed over the same way (and the test player's paving once offered, since 0.7.103). |
| 1358 | 3 | `static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t)` | The count before 0.7.51, kept for the harness's comparison: the least n whose figure today is off the list (count() at no projection and no slack, judged by clear() alone). |
| 1368 | 5 | `static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, Ahead p)` | The count of t that keeps the need ahead at p's demand, after what is on site (p null: the old rule, off the list today). |
| 1380 | 40 | `static int[] count(Game game, Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Integer> step, Ahead p, i...` | ...of a step that may be more than one building (0.7.70): a paving is a Paved Road more and a Gravel Road less (pavingStep()); and at most `most` of it, the gravel roads there are to pave. |
| 1422 | 5 | `static Map<BuildingsTemplate, Integer> times(Map<BuildingsTemplate, Integer> base, Map<BuildingsTemplate, Integer> step, int n)` | A copy of base with n times every quantity of step added. |

### THE SIZE THAT FITS THE NEED (0.7.71) (lines 1428-1450)

### A ROAD OVER ITS LIFE (0.7.70) (lines 1451-1674)

| line | len | member | says |
|---:|---:|---|---|
| 1525 | 3 | `public static double running(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Integer> site)` | What one costs the city a month, standing (0.7.70): its repairs - its share a year of what one costs to put up today (since 0.7.103 a road kind's own, BuildingsTemplate.repairsPerYear(); the house's one percent for ev... |
| 1538 | 15 | `public static double running(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Integer> site, boolean added)` | ...and, for a Gravel Road, at the traffic on the streets it is repaired at (FR7 2.2, BuildingsTemplate.gravelRepairsAt()): the streets' utilisation of the city with what is on site and, when `added`, the road itself t... |
| 1563 | 4 | `public static double lifeFactor(Game game)` | Months of a month's running cost a life is worth today (0.7.70): the level annuity's factor over LIFE_MONTHS at the real rate - the debt market's rate for that term at the city's debt now (DebtManager.quoteRate()), le... |
| 1569 | 3 | `public static double lifeRate(Game game)` | ...the real rate it is struck at, a year: the debt market's for LIFE_MONTHS less expected inflation (BusinessInvestment.realTestRate()). |
| 1574 | 4 | `public static double lifetime(Game game, BuildingsTemplate t, double landLeft, Map<BuildingsTemplate, Integer> site)` | A road's or a line's cost over its life (0.7.70): its quote for one, its ground at landValue() on the land left, and lifeFactor() months of running(). |
| 1580 | 9 | `static Map<BuildingsTemplate, Integer> pavingStep(Game game)` | One paving (0.7.70): a Paved Road more and a Gravel Road less; empty with either missing from the catalogue. |
| 1591 | 5 | `public static double pavingFrees(Game game)` | The ground one paving frees (0.7.70): a gravel road's less a paved road's; 0 with either missing. |
| 1598 | 5 | `public static double pavingUnit(Game game, Map<BuildingsTemplate, Integer> site)` | The trips one paving takes off the road (0.7.70): over the line with what is on site, less with one more gravel road paved. |
| 1611 | 9 | `public static double pavingLifetime(Game game, double landLeft, Map<BuildingsTemplate, Integer> site)` | A paving's cost over its life (0.7.70), as lifetime() prices a road: its quote for one (Game.quotePave()), less the ground it frees at landValue() on the land left - what the next road would pay for it - and lifeFacto... |
| 1627 | 6 | `public static boolean pavingBeatsPaved(Game game, double landLeft, Map<BuildingsTemplate, Integer> site)` | Whether paving beats a new Paved Road (0.7.70), each over its life a trip it takes off: pavingLifetime() over pavingUnit() under a Paved Road's lifetime() over unit() - Jerus's "recommends this if better". |
| 1648 | 3 | `static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Integer> site, double landLeft)` | The city's gravel roads, paved, weighed for the road (0.7.70) as a building is in suggestFor(): the count that keeps the road ahead at its projection, of the gravel roads the city could pave (Game.paveable()); its wai... |
| 1653 | 21 | `static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Integer> site, double landLeft, double slack)` | ...at another slack past its projection (0.7.73, AutoBuilder). |

### A BUILDING OVER ITS LIFE, FOR WHAT IT WILL SERVE (0.7.101, batch P2) (lines 1675-1850)

| line | len | member | says |
|---:|---:|---|---|
| 1733 | 14 | `public static double lifeTrend(Game game, double months)` | The city's demand `months` from now over today's, for a building's life (0.7.101): the city's own population trend over the last LIFE_MONTHS of its record - or since its first month recorded, a younger city - linear i... |
| 1749 | 5 | `public static double orderLife(Game game, BuildingsTemplate t, int count, double landLeft, Map<BuildingsTemplate, Integer> site)` | An order of `count` of t over its life (0.7.101): its quote for the count, its ground at landValue() on the land left, and lifeFactor() months of running() for each. |
| 1756 | 8 | `public static double pavingOrderLife(Game game, int count, double landLeft, Map<BuildingsTemplate, Integer> site)` | ...`count` pavings' (0.7.101): their quote (Game.quotePave()), less the ground they free at landValue() on the land left, and lifeFactor() months of a paved road's running over a gravel road's for each; NaN with nothi... |
| 1774 | 21 | `public static double shortfall(Game game, Measure m, Map<BuildingsTemplate, Integer> added, Ahead p)` | What a measure is short of, in its own unit, with these buildings standing as well, at `p`'s demand (0.7.101): power and water the supply NEEDS YOU's quarter in hand asks (the demand over NETWORK_YELLOW) less the supp... |
| 1797 | 6 | `public static boolean weighsServed(Measure m)` | Whether a measure's candidates are weighed for what they will serve over their lives (shortfall()): power, water, the roads (streets and highways) and care. |
| 1814 | 19 | `public static double servedOverLife(Game game, Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Integer>...` | What an order serves over its life, a month on average, in the measure's unit (0.7.101): each of its LIFE_YEARS years read at its middle month - from its opening, `p`'s months less HORIZON, the demand today's times li... |
| 1835 | 3 | `public static double perServed(double life, double served, int count, double unit)` | The figure from an order's life and what it serves (0.7.101): the life over what it will serve; with that not measured, or nothing, over what its count adds (count x unit). |
| 1845 | 5 | `public static double lifePerServed(Game game, Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, int count, ...` | A candidate's figure, the one the advice ranks by (0.7.101): an order of `count` of t over its life (orderLife()) for each unit it will serve over that life (servedOverLife() at `p`, the demand it is sized to; what it... |

### AN IRON FIELD THAT WILL NOT PAY BACK (0.7.101, batch P2) (lines 1851-2045)

| line | len | member | says |
|---:|---:|---|---|
| 1880 | 4 | **type** `public record Payback(int mines, double earns, double payment)` | An iron field weighed (0.7.101): the mines the city could staff and work on it, their profit a month, and the month's payment that repays its price - it pays back when the profit is at least the payment. |
| 1882 | 1 | `public boolean pays()` _(in BuildAdvice.Payback)_ | Whether the mines pay the field back. |
| 1886 | 4 | `public static Payback ironPayback(Game game, LandParcel field)` | Whether an offer holding iron would pay itself back in this city (fieldEarnings() against fieldPayment()) - since 0.7.142 by the best mine that fits each of its fields. |
| 1916 | 1 | **type** `public record FieldMines(Deposit.FieldType type, int sites, BuildingsTemplate mine, int count)` | A field of an offer and the mines that would fill it (0.7.142): its type, its sites, a kind of mine and how many of it. |
| 1919 | 32 | `public static List<FieldMines> fieldMines(Game game, LandParcel field)` | Each iron field an offer holds, filled with the iron mines that fit it by type and size in the plan's order (above); a field no mine fits has a row with none. |
| 1953 | 29 | `public static double[] fieldEarnings(Game game, LandParcel field)` | What an offer's iron fields would earn the city's mines a month (0.7.142; BY THE FIELDS IT HOLDS, above): {mines, their monthly profit}, fieldEarnings()'s count on fieldMines()' mines. |
| 1996 | 25 | `public static double[] fieldEarnings(Game game, int sites)` | What a field of `sites` iron sites would earn the city's mines a month (0.7.67, M3b; moved from LongPlaytest in 0.7.101): {mines, their monthly profit}. |
| 2030 | 6 | `public static double fieldPayment(Game game, LandParcel field)` | The month's payment that repays a field's price here over Game.BUILD_BOND_YEARS (0.7.67, M3b; moved from LongPlaytest in 0.7.101): the level payment at the rate the market quotes for that much money at that term (Debt... |
| 2038 | 7 | `public static boolean higherWorse(Measure m)` | Which way is worse for a measure's figure. |

### A BERTH THAT PAYS BACK (FR3, 2026-10-19; docs/queue/FR3.md 5, the (lines 2046-2141)

| line | len | member | says |
|---:|---:|---|---|
| 2084 | 5 | `public static List<Measure> portMeasures()` | The port measures, one a kind of cargo, in Cargo order (FR3). |
| 2091 | 8 | `static double portUncovered(Game game, Measure m, Map<BuildingsTemplate, Integer> added)` | A port measure's uncovered tonnes a month with these buildings standing as well: the kind's carriable() less its berths standing and the berths in `added` (Ports.uncovered()). |
| 2109 | 7 | `public static BuildingsTemplate portTerminal(Game game, Measure m, Map<BuildingsTemplate, Integer> added, java.util.Set<Buildin...` | The terminal to order for a port measure, or null (FR3): with the kind's uncovered tonnes (`added` standing too) paying back some terminal of it but those in `skip` at the port dues dial, the largest of them whose bre... |
| 2123 | 18 | `static Suggestion portSuggestion(Game game, Measure m, double cashLeft, double landLeft, java.util.Set<BuildingsTemplate> skip)` | A port measure's card: as many of the terminal portTerminal() chose as the uncovered tonnes pay back, each counted after the ones before it, and one at least (SL1, Ports.orderCount(); it was one), or null for none (po... |

### A BERTH'S PAYBACK, FOR ITS CARD (FR4, 2026-10-20) (lines 2142-2217)

| line | len | member | says |
|---:|---:|---|---|
| 2154 | 1 | **type** `public enum BerthState` | Where a berth stands on its payback (FR4): PAYS - the dues at the dial cover its cost and more than its upkeep; SHORT - cargo of its kind crosses and no berth takes it, but not enough of it (Ports.berthPaysBack()); CO... |
| 2168 | 10 | **type** `public record BerthPayback(BuildingsTemplate terminal, BerthState state, double carriable, double uncovered...` | One more of a terminal, weighed as the advice weighs one (FR4): the kind's carriable and uncovered tonnes a month, the tonnes a month the advice needs uncovered before the terminal pays back (needed; NaN with no dues)... |
| 2173 | 1 | `public boolean pays()` _(in BuildAdvice.BerthPayback)_ | Whether it pays back (state PAYS). |
| 2176 | 1 | `public double carriedAYear()` _(in BuildAdvice.BerthPayback)_ | The tonnes a year it would carry. |
| 2191 | 26 | `public static BerthPayback berthPayback(Game game, BuildingsTemplate t)` | A sea terminal's payback, as its card says it (FR4; null for any other building). |

