# BuildAdvice.java - 3,292 lines · 175 methods · 16 constants · model

`ham/citybuildersim/BuildAdvice.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
> take the automatic remit in its order (AutoBuilder.remit()) - a school above
> the ladder listed only for the students the city would both get and hire
> (CityNeeds.wanted(), listsSchool()) - and keep those a city-built building
> answers and what is on site does not already keep ahead; for each, of the
> buildings that serve its measure (for the roads, any road or line, and
> the city's gravel roads paved beside them), the plan with the lowest
> whole-life net cost (HW1, WorksValue: order, replacement land and running
> for BUILD_BOND_YEARS, less physical output loss prevented and payroll
> taxes returned). A plan that closes the need ranks before one that cannot.
> Reserve headroom sizes the plan but cannot claim prevented output loss;
> the same physical valuation appears on the Build card. The
> count that keeps the need ahead at the demand it opens to, projected
> the way the businesses project theirs (above the ladder, the students it
> would get and hire by then, its feeder's graduates counted) and the player's reserve
> past it - off NEEDS YOU's list there, and a served gauge at 100% of it -
> after what is on site at its staffed capacity; on credit when its quote
> is more than the cash the ones before leave, never cut to the cash; at
> most three, one per need. Every judgement in it is named where it is made
> below, and in the project's design notes for 0.7.24 and 0.7.51. A first
> building where none stands is suggested however far past the need one
> goes (Jerus's school at 0% that one would take to 20,000%): the overshoot
> enters the plan's cost, it never stops one (0.7.101, HW1).

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (238), [Game](Game.md) (125), [CityNeeds](CityNeeds.md) (64), [BuildingType](BuildingType.md) (48), [NewUnit](NewUnit.md) (36), [CareType](CareType.md) (33), [Landmasses](Landmasses.md) (24), [PowerDispatch](PowerDispatch.md) (21), [Ports](Ports.md) (20), [WorksValue](WorksValue.md) (20), [JobType](JobType.md) (17), [InfrastructureManager](InfrastructureManager.md) (17), [EducationType](EducationType.md) (13), [ConstructionControl](ConstructionControl.md) (13), [Good](Good.md) (10), [UtilitiesHandler](UtilitiesHandler.md) (9), [BusinessInvestment](BusinessInvestment.md) (9), [BuildingManager](BuildingManager.md) (7), [Substations](Substations.md) (7), [Healthcare](Healthcare.md) (7), [SafetyType](SafetyType.md) (6), [Crime](Crime.md) (6), [Traffic](Traffic.md) (6), [AutoBuilder](AutoBuilder.md) (6), [Sectors](Sectors.md) (6), [BuildingsStacks](BuildingsStacks.md) (4), [LandManager](LandManager.md) (4), [LicenceFees](LicenceFees.md) (4), [LandParcel](LandParcel.md) (4), [Bridges](Bridges.md) (4)... and 20 more

**Used by (60):** [AdviceValueOracle](AdviceValueOracle.md), [AskProceedsCheck](AskProceedsCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [BridgeCheck](BridgeCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ER1Check](ER1Check.md), [EquipmentCheck](EquipmentCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FB13Check](FB13Check.md), [FarmlandReserveCheck](FarmlandReserveCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [Game](Game.md), [GroundSkipCheck](GroundSkipCheck.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HydroCheck](HydroCheck.md), [Inbox](Inbox.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [KeptTimingCheck](KeptTimingCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LandmassCheck](LandmassCheck.md), [LandmassView](LandmassView.md), [LicenceFees](LicenceFees.md), [LongPlaytest](LongPlaytest.md), [MapView](MapView.md), [NewUnitCheck](NewUnitCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerViewCheck](PowerViewCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [ReadPathCheck](ReadPathCheck.md), [RecipeChanges](RecipeChanges.md), [ResearchCheck](ResearchCheck.md), [RoadCheck](RoadCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [StepsCheck](StepsCheck.md), [SubstationCheck](SubstationCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [UtilitiesHandler](UtilitiesHandler.md), [WaterCheck](WaterCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md)

## Sections

| line | section |
|---:|---|
| 53 | THE CATEGORIES (0.7.24) |
| 190 | THE MEASURES |
| 367 | STAFFED CAPACITY |
| 511 | THE FIGURE, BEFORE AND AFTER |
| 692 | · THE FIRM SUPPLY (PW3, 2026-11-23; spec-electricity 2.8) |
| 1143 | THE SCARCE STAFF, AND WHAT AN ORDER NEEDS |
| 1206 | THE SUGGESTIONS |
| 1728 | THE POWER PLANNER (PW3, 2026-11-23; spec-electricity 2.8) |
| 2103 | THE SIZE THAT FITS THE NEED (0.7.71) |
| 2126 | A ROAD OVER ITS LIFE (0.7.70) |
| 2451 | A BUILDING OVER ITS LIFE, FOR WHAT IT WILL SERVE (0.7.101, batch P2) |
| 2636 | AN IRON FIELD THAT WILL NOT PAY BACK (0.7.101, batch P2) |
| 2831 | A BERTH THAT PAYS BACK (FR3, 2026-10-19; docs/queue/FR3.md 5, the |
| 2927 | · A BERTH'S PAYBACK, FOR ITS CARD (FR4, 2026-10-20) |
| 3003 | THE ISLAND LINK (AL1, 2026-11-09) |
| 3217 | THE ISLAND LINK, AS THE SCREENS SAY IT (FB13, AL1-W) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 229 | `BuildAdvice.Kind.POWER` |  |
| 229 | `BuildAdvice.Kind.WATER` |  |
| 229 | `BuildAdvice.Kind.ROADS` |  |
| 229 | `BuildAdvice.Kind.TRANSIT` |  |
| 229 | `BuildAdvice.Kind.CARE` |  |
| 229 | `BuildAdvice.Kind.DEATH` |  |
| 229 | `BuildAdvice.Kind.PLOTS` |  |
| 229 | `BuildAdvice.Kind.SCHOOL` |  |
| 229 | `BuildAdvice.Kind.POLICE` |  |
| 229 | `BuildAdvice.Kind.CELLS` |  |
| 229 | `BuildAdvice.Kind.HIGHWAYS` |  |
| 229 | `BuildAdvice.Kind.PORT` |  |
| 229 | `BuildAdvice.Kind.GRID` |  |
| 1388 | `BuildAdvice.Lost.DEARER` |  |
| 1388 | `BuildAdvice.Lost.CANNOT_CLOSE` |  |
| 1388 | `BuildAdvice.Lost.NO_ROOM` |  |
| 2939 | `BuildAdvice.BerthState.PAYS` |  |
| 2939 | `BuildAdvice.BerthState.SHORT` |  |
| 2939 | `BuildAdvice.BerthState.COVERED` |  |
| 2939 | `BuildAdvice.BerthState.NO_DUES` |  |
| 2939 | `BuildAdvice.BerthState.UPKEEP` |  |
| 2939 | `BuildAdvice.BerthState.NO_CARGO` |  |
| 3029 | `BuildAdvice.LinkKind.BRIDGE` |  |
| 3029 | `BuildAdvice.LinkKind.PORT` |  |
| 3029 | `BuildAdvice.LinkKind.NONE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `BuildAdvice.OVERVIEW` | `"Overview"` | The page Build opens on: the city's job, what would help most, and what the market builds. |
| 116 | `BuildAdvice.UTILITIES` | `"Utilities", ROADS = "Infrastructure", HEALTHCARE = "Healthcare", EDUCATION =...` | The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. |
| 1235 | `BuildAdvice.MAX_SUGGESTIONS` | `3` | At most this many suggestions, one per need. |
| 1238 | `BuildAdvice.MOST` | `1<<22` | The most of one building a search will count to. |
| 1241 | `BuildAdvice.SLACK` | `BusinessInvestment.TARGET_HEADROOM` | The businesses' headroom, retained for explicit callers. |
| 1244 | `BuildAdvice.HORIZON` | `BusinessInvestment.PLANNING_HORIZON` | The least months past an order's opening it is sized for: the businesses' (BusinessInvestment.PLANNING_HORIZON); an order that opens later than that is sized further past it, as the businesses size theirs (BusinessInv... |
| 1257 | `BuildAdvice.NOW` | `new Ahead(0, 1, 0)` | Today's demand, no slack: what NEEDS YOU reads. |
| 1323 | `BuildAdvice.FIRST_WORK_SHARE` | `.30` | Jerus's HW1 first-work threshold: 150 students for a 500-seat college, and the same share for other oversized works. |
| 1797 | `BuildAdvice.RESERVE_ORDERS` | `new java.util.concurrent.atomic.AtomicLong()` | Power's reserve orders suggested (the reserve test fired): PowerPlanCheck's count, never a decision. |
| 1800 | `BuildAdvice.SPREAD_ORDERS` | `new java.util.concurrent.atomic.AtomicLong()` | Power's spread orders suggested (the spread fired): PowerPlanCheck's count, never a decision. |
| 1803 | `BuildAdvice.YEAR_HOURS` | `12 * PowerDispatch.MONTH_HOURS` | The game's hours a year: twelve months of PowerDispatch.MONTH_HOURS. |
| 2184 | `BuildAdvice.LIFE_MONTHS` | `Game.BUILD_BOND_YEARS * 12` | Months a road is weighed over (0.7.70): the funding page's bond term, Game.BUILD_BOND_YEARS - a long-lived asset "paid for over the years the city uses it". |
| 2495 | `BuildAdvice.LIFE_YEARS` | `LIFE_MONTHS / 12` | The years of a building's life the served figure reads: LIFE_MONTHS in whole years. |
| 3056 | `BuildAdvice.NO_CROSSING_MONTHS` | `12` | The months a survey that found no crossing to an island stands before the rule surveys it again: such a survey searches out to 45 km, up to 1.7 s (ET6), and an island held for money or with no link at all is asked eve... |
| 3062 | `BuildAdvice.NO_CROSSING` | `new java.util.WeakHashMap<>()` | The surveys that found no crossing, by island, kept for each city's landmasses (a loaded or new city has its own; never saved, so a loaded city surveys once). |
| 3233 | `BuildAdvice.SCREEN_NO_CROSSING` | `new java.util.WeakHashMap<>()` | The surveys that found no crossing as the screens remember them, apart from NO_CROSSING: what a screen has read never moves the rule's own memory, and the rule's own fresh memory is read before it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 3065 | `static int linkSurveys` | How many crossings the rule has surveyed in this run (AL1): a harness reads that an island with none is not surveyed every month. |
| 3236 | `static int screenSurveys` | How many crossings the screens have surveyed in this run (FB13): a harness reads that a no-crossing island is surveyed once for a ground, not each month. |
| 3242 | `private static volatile Shown shownKept` |  |
| 3245 | `static int shownReads` | How many times the screens' reading was made afresh in this run (FB13): a harness reads that a redraw with nothing new reads nothing. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 3244 | **type** `public final class BuildAdvice` | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure before and after an order by the model's own arithmetic, and the rule behin... |
| 51 | 1 | `private BuildAdvice()` |  |

### THE CATEGORIES (0.7.24) (lines 53-189)

| line | len | member | says |
|---:|---:|---|---|
| 99 | 15 | **type** `public record Category(String name, String was, EnumSet<BuildingType> types, boolean cityBuilds)` | One category of the strip. |
| 101 | 1 | `public EnumSet<BuildingType> types()` _(in BuildAdvice.Category)_ | A fresh set every call: EnumSet is mutable, and screens should not share one. |
| 108 | 5 | `public boolean holds(BuildingsTemplate t)` _(in BuildAdvice.Category)_ | Whether this building is on the category's page: its type is among the category's - but for the Strategic Reserve, a HEAVY_INDUSTRY building the city alone builds, which is Infrastructure's and not Industry's (FR4). |
| 125 | 22 | `public static List<Category> categories()` | The strip, in its order: the city's five, then the market's nine. |
| 149 | 7 | `public static Category category(String name)` | A category by its name - or by its label before 0.7.24. |
| 158 | 4 | `public static Category categoryOf(BuildingType type)` | The category a kind of building is in, by its type: the Strategic Reserve's is Industry's, whose it is not (categoryOf(BuildingsTemplate) knows). |
| 164 | 5 | `public static Category categoryOf(BuildingsTemplate t)` | The category a building is on the page of (FR4): its type's, but for the Strategic Reserve, which is Infrastructure's (Category.holds()). |
| 171 | 5 | `public static List<BuildingsTemplate> templatesOf(Category c, BuildingManager buildings)` | A category's buildings in catalogue order: those of its types it holds, and the reserve in Infrastructure's. |
| 178 | 11 | `public static Category categoryOf(CityNeeds.Go go)` | The category a NEEDS YOU row's fix is in, for the five the city builds and Homes; null for the rest. |

### THE MEASURES (lines 190-366)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 1 | **type** `public enum Kind` | The measures. |
| 232 | 65 | **type** `public record Measure(Kind kind, CareType care, EducationType school, Ports.Cargo cargo)` | One measure: a kind, and the care type or the school it is about - or, for PORT, the cargo (null for every other kind). |
| 234 | 1 | `public static Measure of(Kind kind)` _(in BuildAdvice.Measure)_ |  |
| 235 | 1 | `public static Measure care(CareType care)` _(in BuildAdvice.Measure)_ |  |
| 236 | 1 | `public static Measure school(EducationType school)` _(in BuildAdvice.Measure)_ |  |
| 238 | 1 | `public static Measure port(Ports.Cargo cargo)` _(in BuildAdvice.Measure)_ | The berths of a kind of cargo (FR3): the sea terminals that handle it. |
| 241 | 20 | `public boolean serves(BuildingsTemplate t)` _(in BuildAdvice.Measure)_ | Whether this building serves the measure - the buildings its ring shows. |
| 263 | 10 | `public String category()` _(in BuildAdvice.Measure)_ | The category its buildings are in. |
| 275 | 18 | `public String label()` _(in BuildAdvice.Measure)_ | Its name, as a ring and a heading say it. |
| 295 | 1 | `public boolean isLoad()` _(in BuildAdvice.Measure)_ | True for a figure that is a load - the share of a network's capacity in use. |
| 299 | 30 | `public static List<Measure> measuresOf(String category)` | The measures a city category's page opens on, in its ring order; empty for a market category. |
| 331 | 17 | `public static Measure measureOf(CityNeeds.Need need)` | The measure a NEEDS YOU row is about, for the rows a city-built building answers; null for the rest. |
| 356 | 10 | `public static CityNeeds.Need needFor(List<CityNeeds.Need> all, Measure m)` | The NEEDS YOU row that judges a measure - the worst of them, as the panel would list them first, for death care whose two rows are the dead and the plots - or null for a measure no row watches (transit, a basic stage ... |

### STAFFED CAPACITY (lines 367-510)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 3 | `public static double staffing(BuildingsTemplate t, double[] fill)` | Σ posts x fill / Σ posts over a building's own posts; 1 with none - BuildingManager's per-building weighting. |
| 384 | 10 | `public static double staffing(NewUnit u, double[] fill)` | ...for one new building already resolved (QN1): its own posts. |
| 403 | 3 | `public static double unfilledPosts(BuildingsTemplate t, double[] fill)` | The posts of a building the city likely cannot fill: Σ posts x (1 - the city's fill rate for that job) over its own posts, 0 with none. |
| 408 | 10 | `public static double unfilledPosts(NewUnit u, double[] fill)` | ...for one new building already resolved (QN1): its own posts. |
| 420 | 3 | `public static boolean hasPosts(BuildingsTemplate t)` | Whether a building has any posts at all - a road has none, and its card says so instead of drawing a staff bar. |
| 425 | 4 | `public static boolean hasPosts(NewUnit u)` | ...for one new building already resolved (QN1): its own posts. |
| 439 | 3 | `public static double unit(Game game, Measure m, BuildingsTemplate t)` | What one building of this kind adds to a measure, in the measure's own unit, at today's staffing: generation or treatment a month, people cared for, places, officers, cells; for road capacity the trips it takes off wh... |
| 444 | 47 | `public static double unit(Game game, Measure m, NewUnit u)` | ...of one new building already resolved (QN1): its own output and posts; the template it is of is what the measure serves. |
| 493 | 3 | `public static double built(Game game, Measure m, BuildingsTemplate t)` | The same, fully staffed: what the building is built for. |
| 498 | 12 | `public static double built(Game game, Measure m, NewUnit u)` | ...of one new building already resolved (QN1): its own output. |

### THE FIGURE, BEFORE AND AFTER (lines 511-691)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 1 | `public static boolean roadKind(Measure m)` | Whether a measure is one of the two road needs (FR7): the streets or the highways, which read the same network, so each counts every road on site. |
| 532 | 17 | `public static Map<BuildingsTemplate, Long> onSite(Game game, Measure m)` | Every unit on site of the buildings that serve a measure, for anybody's order. |
| 551 | 12 | `public static double firstCompletion(Game game, Measure m, Map<BuildingsTemplate, Long> site)` | First pending unit's quoted opening at the current shares, including preparation owed before it (HW1). |
| 565 | 5 | `public static long units(Map<BuildingsTemplate, Long> added)` | Units on site, all told: the buildings going up (a gravel road a paving takes away is not one). |
| 572 | 5 | `public static Map<BuildingsTemplate, Long> plus(Map<BuildingsTemplate, Long> base, BuildingsTemplate t, long n)` | A copy with n more of t. |
| 579 | 5 | `public static Map<BuildingsTemplate, Long> plus(Map<BuildingsTemplate, Long> base, Map<BuildingsTemplate, Long> more)` | ...and with every quantity of another added. |
| 589 | 3 | `private static NewUnit adding(Game game, BuildingsTemplate t, long n)` | One entry of an `added` map as the figures below read it (QN1): a building to add (n > 0) as one new building of its template would open now, a removal (n <= 0: the paving's gravel) as the template stands. |
| 594 | 3 | `public static double figure(Game game, Measure m, Map<BuildingsTemplate, Long> added)` | The measure's figure with these buildings standing as well. |
| 599 | 92 | `public static double figure(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p)` | ...against the demand `p` says (0.7.51): today's times p.scale() - projected and padded - on the demand side only. |

### THE FIRM SUPPLY (PW3, 2026-11-23; spec-electricity 2.8) (lines 692-1142)

| line | len | member | says |
|---:|---:|---|---|
| 703 | 17 | `public static double variableShare(Game game, Map<BuildingsTemplate, Long> added)` | The variable rows' share of the city's energy with these added: the wind and solar farms' average kW (the sun's at the city's place) over the city's draw; 0 drawing nothing. |
| 722 | 8 | `public static double credit(Game game, BuildingsTemplate t, double share)` | A plant's firm kW over its production1 at this share of the city's energy from the variable rows: a wind farm's and a solar farm's credit at the city's place and rack (PowerDispatch), every other row's 1. |
| 732 | 3 | `static double firmFill(BuildingsTemplate t, double staffed)` | The share of a plant's production1 its staffing gives: its posts' fill, a hydro station's all of it with anybody on shift (its water, as UtilitiesHandler counts a station's month). |
| 737 | 39 | `private static double[] powerSupplyDemand(Game game, Map<BuildingsTemplate, Long> added, Ahead p)` | Power's {firm supply, the peak hour's draw}, kW, with these buildings standing as well, at `p`'s demand: THE FIRM SUPPLY above. |
| 789 | 3 | `public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Long> added)` | What the measure has against what it needs, with these buildings standing as well, in the measure's own unit: generation or treatment against the draw; the road's capacity against the trips on it, and the room on tran... |
| 794 | 79 | `public static double[] supplyDemand(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p)` | ...against the demand `p` says (0.7.51), as figure() reads it: the supply side unchanged. |
| 881 | 10 | `static double schoolNeed(Game game, EducationType type, Ahead p)` | The people a school serves at `p`'s demand (0.7.51): a basic stage's children or teens times p.scale(); above the ladder the students the city would get and hire by then (CityNeeds.wanted(), its posts grown by p.k()) ... |
| 901 | 7 | `static double carePlaces(Game game, CareType care, Map<BuildingsTemplate, Long> added, double[] fill)` | A kind of care's places with these buildings standing as well: the staffed beds and the order's at the fill, less the people the fee turned away last month (Healthcare.getPricedOut()) - the beds less the priced out, a... |
| 910 | 5 | `public static double cover(Game game, Measure m, Map<BuildingsTemplate, Long> added)` | The figure as a share of what is needed, 0 to 1 - supply over demand, held to it - for a ring and the order bar's stacked bar. |
| 926 | 6 | `public static boolean isServed(Measure m)` | Whether a measure's gauge reads as served (0.7.41): power, water, the road, transit, care and the schools. |
| 934 | 5 | `public static double served(Game game, Measure m, Map<BuildingsTemplate, Long> added)` | What the measure serves with these buildings standing as well (0.7.41): supply over demand, unclamped; NaN for a measure that is not served (isServed()). |
| 948 | 17 | `public static CityNeeds.Served verdict(Game game, Measure m, Map<BuildingsTemplate, Long> added)` | ...and CityNeeds' one verdict on it (0.7.41): the lines of the NEEDS YOU row that watches the measure. |
| 967 | 11 | `public static double[] servedLines(Measure m)` | A served measure's two lines in served terms (0.7.41), {off NEEDS YOU's list past this, red at or under this}; null for transit, for the highways (their verdict is the row's own level: CityNeeds.highwayVerdict()) and ... |
| 980 | 3 | `public static CityNeeds.Served verdictAfter(Game game, Suggestion s)` | What a suggestion would leave its measure serving, and the verdict on it (0.7.41): what is on site and the order, standing - the served side of its after(); null for a measure not served. |
| 985 | 4 | `public static Map<BuildingsTemplate, Long> added(Game game, Suggestion s)` | What a suggestion changes on the city: its count of its building - and for a paving (0.7.70), as many gravel roads gone. |
| 991 | 3 | `static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Long> added)` | The road network with these buildings standing: InfrastructureManager.with(), the model's own curve. |
| 996 | 25 | `static InfrastructureManager roads(Game game, Map<BuildingsTemplate, Long> added, double k)` | ...with the city's own traffic times k (0.7.51): the load and every stream grown alike, then the buildings' own as before. |
| 1028 | 8 | `public static double highwayFlow(Game game)` | The trips a month one more highway carries at free flow (FR7): the Elevated Highway's capacity at FREE_FLOW - the unit the through need is read in, so a share of one is a share of a highway worth building. |
| 1038 | 4 | `static double highwayFill(Game game, InfrastructureManager r)` | The through need's figure (FR7): the through trips with no highway to take them, over what one more highway carries (CityNeeds.HIGHWAY_MIN_FILL of one bites); 0 with no highway to build. |
| 1051 | 5 | `static double roadsOver(Game game, Map<BuildingsTemplate, Long> added)` | Trips over the line: what the road carries past STRAINED of its capacity, the line NEEDS YOU lists the roads at. |
| 1058 | 3 | `static double unburiedNext(Game game, Map<BuildingsTemplate, Long> added)` | The dead next month at today's deaths: those waiting and those dying, less what the free plots and the ovens take (Healthcare.settleDeaths()'s arithmetic). |
| 1063 | 16 | `static double unburiedNext(Game game, Map<BuildingsTemplate, Long> added, double k)` | ...with k times today's deaths (0.7.51): the unburied are a stock, and stay as they are. |
| 1081 | 3 | `static double school(Game game, EducationType type, Map<BuildingsTemplate, Long> added)` | A school's figure: a basic stage's coverage (Education's own cover, places over the children it serves), or above the ladder who would come and be hired over the seats (0.7.51; who would come until then). |
| 1086 | 14 | `static double school(Game game, EducationType type, Map<BuildingsTemplate, Long> added, Ahead p)` | ...against the people `p` says it serves (schoolNeed()). |
| 1101 | 8 | `static double officers(Game game, Map<BuildingsTemplate, Long> added)` |  |
| 1110 | 8 | `static double cells(Game game, Map<BuildingsTemplate, Long> added)` |  |
| 1125 | 17 | `public static boolean clear(Measure m, double figure)` | Whether a figure is off NEEDS YOU's list: past the line it is listed at (the yellow line, strictly), which way past depending on the row. |

### THE SCARCE STAFF, AND WHAT AN ORDER NEEDS (lines 1143-1205)

| line | len | member | says |
|---:|---:|---|---|
| 1148 | 10 | `public static JobType scarceJob(BuildingsTemplate t, double[] fill)` | The job type among a building's posts with the city's lowest fill rate, or null for a building with no posts. |
| 1160 | 3 | `public static double[] needs(Map<BuildingsTemplate, Long> order)` | Posts and land an order needs: posts by job type (index JobType.ordinal()), then the land in the last slot. |
| 1165 | 3 | `public static double[] needs(Game game, Map<BuildingsTemplate, Long> order)` | ...of an order as it would open now (QN1): each building to add (a count above nothing) as one new building of its template, a removal as it stands. |
| 1169 | 10 | `private static double[] needs(Map<BuildingsTemplate, Long> order, java.util.function.BiFunction<BuildingsTemplate, Long, NewUni...` |  |
| 1186 | 5 | `public static double quoteTotal(Game game, Map<BuildingsTemplate, Long> order)` | What an order of several buildings costs all in (0.7.34): each one's Game.quoteBuild() total, as its own card quotes it and its own Build charges it - the order bar's "$X all in" and its Build button's label, which th... |
| 1200 | 5 | `public static double quoteTotal(List<Suggestion> advice)` | ...and of the overview's suggestions (0.7.38): each one's own quote, its price(), which is Game.quoteBuild() as its card shows it and its Build charges it, added in their order. |

### THE SUGGESTIONS (lines 1206-1727)

| line | len | member | says |
|---:|---:|---|---|
| 1251 | 4 | **type** `public record Ahead(double months, double k, double slack)` | The demand an order is sized against (0.7.51): today's, `months` ahead grown by k (BusinessInvestment.growthFactor()), and padded by `slack`. |
| 1253 | 1 | `public double scale()` _(in BuildAdvice.Ahead)_ | What today's demand is multiplied by. |
| 1267 | 3 | `public static Ahead opening(Game game, double lead)` | The demand an order that opens after `lead` months is sized to: the wait (a stalled one, NaN, counts as none), plus the businesses' horizon for a building that takes so long (BusinessInvestment.horizonFor(): HORIZON f... |
| 1272 | 5 | `public static Ahead opening(Game game, double lead, double slack)` | ...with another slack past it (0.7.73): automatic building's, the player's slider (AutoBuilder). |
| 1292 | 8 | `public static boolean ahead(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p)` | Whether these buildings keep the measure ahead at `p`'s demand: off NEEDS YOU's list there (clear()), and a served measure - not transit, which has no line - at 100% of it or more. |
| 1302 | 6 | `public static boolean maintained(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p)` | Automatic works (HW1): every essential service meets the whole demand, including highways, police and professional schools. |
| 1315 | 6 | `public static boolean lacksNow(Game game, Measure m)` | What the city lacks this month (PW3, 2026-11-24): every measure but power is lacking when it is not maintained now; power is short when the firm supply is below the draw of the peak block in the year's highest month (... |
| 1326 | 14 | `public static boolean warranted(Game game, Measure m)` | Below a useful share of the smallest work, no shortage is declared or forced (HW1). |
| 1348 | 6 | `public static double landValue(Game game, double sqFt, double landLeft)` | What `sqFt` of ground is worth to the city with `landLeft` free: the free part at the cheaper of what a business pays the city for it and the land office's price, the rest at the land office's (LandManager.getOfficePr... |
| 1365 | 7 | `public static double landValue(Game game, BuildingsTemplate t, double sqFt, double landLeft)` | ...a template's ground (RG1), `landLeft` the ground it may take as availableGround() reads it: a Wind or Solar Farm in the farmland reserve while one stands - as much as the reserve holds at the reserve's price (LandM... |
| 1374 | 5 | `static double reserveHeldFor(Game game, BuildingsTemplate t)` | The farmland reserve's held ground a template may take besides the ground free for anyone (RG1): a Wind or Solar Farm's while a reserve stands, else none. |
| 1381 | 5 | `static double availableGround(Game game, BuildingsTemplate t, double buildingGroundLeft)` | Ground remaining for this order's actual land use after earlier cards' footprints - and for a Wind or Solar Farm the farmland reserve's held ground while one stands (RG1, reserveHeldFor()). |
| 1388 | 1 | **type** `public enum Lost` | Why the next plan lost: greater whole-life net cost, or it cannot close the need (HW1). |
| 1430 | 10 | **type** `public record Suggestion(CityNeeds.Need need, Measure measure, BuildingsTemplate template, long count, doub...` | One suggested order (0.7.51: one count, the one that keeps the need ahead - the cash no longer caps it). |
| 1438 | 1 | `public double planValue()` _(in BuildAdvice.Suggestion)_ | The complete plan's comparable net present value, separate from its informational unit cost. |
| 1442 | 3 | `public static List<Suggestion> suggest(Game game)` | The rule, on this city now. |
| 1451 | 16 | `public static List<Suggestion> suggest(Game game, List<CityNeeds.Need> all)` | The rule, on NEEDS YOU as already measured (the screen measures it once a redraw): each card on the cash and the ground the ones before it leave. |
| 1469 | 5 | `public static LinkedHashMap<BuildingsTemplate, Long> run(List<Suggestion> advice)` | The suggestions as one run, as "Build all three" places it: each card's building and count in their order, two of one building added together - and no paving (0.7.70), which is not a build order and has its card's own... |
| 1476 | 5 | `public static List<Suggestion> builds(List<Suggestion> advice)` | The suggestions "Build all three" places (0.7.70): every one but a paving. |
| 1483 | 3 | **type** `private record Weighed(BuildingsTemplate t, int tier, double per, double unit, long count, boolean closes, ...` | One building weighed for a need: its count at its projection, and where it ranks - or, paving, gravel roads paved (0.7.70). |
| 1488 | 3 | `static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft)` | One need's suggestion, or null when what is on site already keeps it ahead or no building can help. |
| 1500 | 4 | `public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double slack)` | ...sized with `slack` past its projection in place of SLACK (0.7.73): automatic building's spare margin, the player's slider (AutoBuilder) - the same ranking, the same projection, the same count at another slack. |
| 1506 | 4 | `public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double slack, ...` | ...with the buildings in `skip` left out of the ranking (0.7.73): automatic building's, for one that would not pay back (since 0.7.104; one its budget could not run from 0.7.73), and since 0.7.101 one the money or the... |
| 1512 | 4 | `public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double slack, ...` | ...and with the city's gravel roads paved left out too when `noPaving` (0.7.101): a paving the money cannot pay for, passed over the same way (and the test player's paving once offered, since 0.7.103). |
| 1521 | 4 | `public static Suggestion suggestFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double slack, ...` | The shared Build-card and automatic full-service target, with the player's reserve setting (HW1). |
| 1527 | 164 | `private static Suggestion suggestionFor(Game game, CityNeeds.Need need, Measure m, double cashLeft, double landLeft, double sla...` | suggestFor()'s reading itself. |
| 1693 | 34 | `private static Suggestion suggestion(Game game, CityNeeds.Need need, Measure m, Map<BuildingsTemplate, Long> site, double befor...` | A need's suggestion from the building ranked first, and the one after it (null with none): its quote, its figures and its ground. |

### THE POWER PLANNER (PW3, 2026-11-23; spec-electricity 2.8) (lines 1728-2102)

| line | len | member | says |
|---:|---:|---|---|
| 1806 | 6 | `public static int slice(double firmKw, double meanDrawKw)` | The slices of the load, the screening curve's (spec 2.8): 0 the base, 1 the shoulder and the peak, 2 the peak alone. |
| 1814 | 6 | `public static double sliceHours(int slice)` | The hours a year a slice's kW is needed: all of them for the base, the shoulder's and the peak's blocks for the shoulder, the peak's alone above (PowerDispatch.BLOCK_HOURS). |
| 1827 | 8 | `static long reserveMost(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Long> at)` | The most of a power plant the reserve test counts with `at` open (PW3, REV-PW3 #3): a hydro station's the slots its river has free (HY1); a wind or solar farm's the farms that keep the variable rows' share of the ener... |
| 1837 | 3 | `public static double runningPerKwh(Game game, BuildingsTemplate t)` | What a kWh a plant runs costs the city: a fuelled row's fuel and variable upkeep (UtilitiesHandler.marginalOf()), a station's variable upkeep, a wind or solar farm's nothing. |
| 1842 | 3 | `static double capitalOf(Game game, BuildingsTemplate t, long n, double landLeft)` | n of a plant's capital and ground: the quote of the order, its ground at landValue() on the land left - a farm's in the farmland reserve first, at its price (RG1). |
| 1847 | 3 | `static double yearCost(Game game, BuildingsTemplate t, long n, double capital, double life)` | What n of a plant cost a year: twelve months of its `capital` over lifeFactor() `life` and of its fixed running (the spread's cost, and the reserve rank's). |
| 1856 | 9 | `static double screening(Game game, BuildingsTemplate t, long n, Map<BuildingsTemplate, Long> at, Ahead p, double landLeft)` | n of a plant's price a firm kW-year for the slice the gap at `p`'s demand lies in, with `at` open (the screening curve, rule 1): twelve months of its capital and ground over lifeFactor() and of its fixed running (year... |
| 1873 | 11 | `static double reserveRank(Game game, BuildingsTemplate t, long n, boolean closes, double lead, Map<BuildingsTemplate, Long> at,...` | A plant's net cost a year (PW3, REV-PW3 #3), lower first: n of it, its yearCost() less the year's running it saves (UtilitiesHandler.yearRunning() on what is on site by `lead`, the plant's own opening, as spreads() re... |
| 1901 | 19 | `private static boolean timely(Game game, Measure m, Weighed d, Map<BuildingsTemplate, Long> at, boolean late, Map<BuildingsTemp...` | Whether a power row that passed its own test, so is due (the need date falls at or before its completion), orders now: THE NEED DATE (PW3, REV-PW3f). |
| 1926 | 4 | `static boolean seasonalShort(Game game, Measure m, Map<BuildingsTemplate, Long> at, double months)` | Whether the seasonal peak (PowerDispatch.PLAN_PEAK_SEASON, the peak block of the year's highest month) is short `months` on, with `at` open: the town's growth to then (BusinessInvestment.growthFactor()), no horizon pa... |
| 1932 | 1 | `private static boolean isFarm(BuildingsTemplate t)` | A wind or solar farm: the rows whose credit falls with their share (reserveMost(), placeFarms()). |
| 1941 | 18 | `private static void placeFarms(Game game, List<Weighed> weighed, Map<BuildingsTemplate, Map<BuildingsTemplate, Long>> opened, M...` | Where the farms that close the gap stand among the sorted candidates (PW3, REV-PW3 #3; the reviewer's: a farm's credit avoids a thermal plant's capital). |
| 1961 | 6 | **type** `public record Spread(BuildingsTemplate template, double saved, double cost, double capital)` | One more of a plant weighed by the spread (rule 2): the year's running it saves, what it costs a year, and the net over its capital. |
| 1963 | 1 | `public double net()` _(in BuildAdvice.Spread)_ | What it saves a year less what it costs a year, city money. |
| 1965 | 1 | `public double ratio()` _(in BuildAdvice.Spread)_ | ...over its capital and ground: the saving a dollar, what the spread ranks by. |
| 1972 | 18 | `public static List<Spread> spreads(Game game, double landLeft, java.util.Set<BuildingsTemplate> skip)` | Every power plant the city could order one more of, weighed by the spread with what is on site standing at that plant's own opening (WorksValue.openBy() at its quoted lead, as the reserve test reads a site; PW3, REV-P... |
| 1992 | 23 | `private static Suggestion spread(Game game, CityNeeds.Need need, Measure m, Map<BuildingsTemplate, Long> site, double before, d...` | Power with the reserve test passed (rule 2): one of the plant whose next unit saves the most a dollar of capital, when it saves more than it costs; null when none does. |
| 2021 | 3 | `static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t)` | The count before 0.7.51, kept for the harness's comparison: the least n whose figure today is off the list (count() at no projection and no slack, judged by clear() alone). |
| 2031 | 5 | `static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t, Ahead p)` | The count of t that keeps the need ahead at p's demand, after what is on site (p null: the old rule, off the list today). |
| 2043 | 4 | `static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> step, Ahead p, long m...` | ...of a step that may be more than one building (0.7.70): a paving is a Paved Road more and a Gravel Road less (pavingStep()); and at most `most` of it, the gravel roads there are to pave. |
| 2049 | 46 | `static long[] count(Game game, Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> step, Ahead p, long m...` | Count full staffed service for automatic essential works; other advice retains its own figure (HW1). |
| 2097 | 5 | `static Map<BuildingsTemplate, Long> times(Map<BuildingsTemplate, Long> base, Map<BuildingsTemplate, Long> step, long n)` | A copy of base with n times every quantity of step added. |

### THE SIZE THAT FITS THE NEED (0.7.71) (lines 2103-2125)

### A ROAD OVER ITS LIFE (0.7.70) (lines 2126-2450)

| line | len | member | says |
|---:|---:|---|---|
| 2200 | 3 | `public static double running(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Long> site)` | What one costs the city a month, standing (0.7.70): its repairs - its share a year of what one costs to put up today (since 0.7.103 a road kind's own, BuildingsTemplate.repairsPerYear(); the house's one percent for ev... |
| 2205 | 3 | `public static double running(Game game, NewUnit u, Map<BuildingsTemplate, Long> site)` | ...of one new building already resolved (QN1): its own posts, power and water. |
| 2218 | 4 | `public static double running(Game game, BuildingsTemplate t, Map<BuildingsTemplate, Long> site, boolean added)` | ...and, for a Gravel Road, at the traffic on the streets it is repaired at (FR7 2.2, BuildingsTemplate.gravelRepairsAt()): the streets' utilisation of the city with what is on site and, when `added`, the road itself t... |
| 2224 | 6 | `public static double running(Game game, NewUnit u, Map<BuildingsTemplate, Long> site, boolean added)` | ...of one new building already resolved (QN1). |
| 2235 | 3 | `private static double runningCostAt(Game game, BuildingsTemplate t, double streetUtilisation)` | The running cost the planners read: the building's own (rawRunningCostAt()) with a patented public work's licence added (R2c-P, LicenceFees.forNewWork()), so WorksValue and the auto-builder net the rent a new utility ... |
| 2240 | 4 | `private static double runningCostAt(Game game, NewUnit u, double streetUtilisation)` | ...of one new building already resolved (QN1); the licence is the template's. |
| 2249 | 3 | `static double licenceOf(Game game, BuildingsTemplate t)` | The licence running() adds for one more of this building (R2c-P, REV-R2c-P-1): LicenceFees.forNewWork() on its raw running cost, over a life; 0 for any work that is not one of a 1900 city's own utilities, and in any o... |
| 2254 | 5 | `static double licenceOf(Game game, NewUnit u)` | ...of one new building already resolved (QN1): the licence on its own running cost, as running(game, u, ...) adds it. |
| 2264 | 3 | `static double rawRunningCostAt(Game game, BuildingsTemplate t, double streetUtilisation)` | The running cost of one building before any licence (R2c-P: runningCostAt() adds a public work's), at this street utilisation: the template as it stands (LicenceFees and RecipeChanges.feesAt() price the buildings they... |
| 2269 | 12 | `static double rawRunningCostAt(Game game, NewUnit u, double streetUtilisation)` | ...of one new building already resolved (QN1): its own power, water, posts; its repairs and upkeep are the template's. |
| 2283 | 27 | `static double runningChange(Game game, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate, Long> after, Ahead at)` | The complete stock change's bill, with its actual traffic wear and one shared pool of fare-paying riders. |
| 2320 | 4 | `public static double lifeFactor(Game game)` | Months of a month's running cost a life is worth today (0.7.70): the level annuity's factor over LIFE_MONTHS at the real rate - the debt market's rate for that term at the city's debt now (DebtManager.quoteRate()), le... |
| 2326 | 4 | `public static double lifeRate(Game game)` | ...the real rate it is struck at, a year: the debt market's for LIFE_MONTHS less expected inflation (BusinessInvestment.realTestRate()). |
| 2332 | 3 | `public static double lifetime(Game game, BuildingsTemplate t, double landLeft, Map<BuildingsTemplate, Long> site)` | A road's or a line's cost over its life (0.7.70): its quote for one, its ground at landValue() on the land left, and lifeFactor() months of running(). |
| 2337 | 5 | `public static double lifetime(Game game, NewUnit u, double landLeft, Map<BuildingsTemplate, Long> site)` | ...of one new building already resolved (QN1): its own running cost; its quote and ground are the template's. |
| 2344 | 9 | `static Map<BuildingsTemplate, Long> pavingStep(Game game)` | One paving (0.7.70): a Paved Road more and a Gravel Road less; empty with either missing from the catalogue. |
| 2355 | 5 | `public static double pavingFrees(Game game)` | The ground one paving frees (0.7.70): a gravel road's less a paved road's; 0 with either missing. |
| 2362 | 5 | `public static double pavingUnit(Game game, Map<BuildingsTemplate, Long> site)` | The trips one paving takes off the road (0.7.70): over the line with what is on site, less with one more gravel road paved. |
| 2375 | 9 | `public static double pavingLifetime(Game game, double landLeft, Map<BuildingsTemplate, Long> site)` | A paving's cost over its life (0.7.70), as lifetime() prices a road: its quote for one (Game.quotePave()), less the ground it frees at landValue() on the land left - what the next road would pay for it - and lifeFacto... |
| 2391 | 6 | `public static boolean pavingBeatsPaved(Game game, double landLeft, Map<BuildingsTemplate, Long> site)` | Whether paving beats a new Paved Road (0.7.70), each over its life a trip it takes off: pavingLifetime() over pavingUnit() under a Paved Road's lifetime() over unit() - Jerus's "recommends this if better". |
| 2412 | 3 | `static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Long> site, double landLeft)` | The city's gravel roads, paved, weighed for the road (0.7.70) as a building is in suggestFor(): the count that keeps the road ahead at its projection, of the gravel roads the city could pave (Game.paveable()); its wai... |
| 2417 | 3 | `static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Long> site, double landLeft, double slack)` | ...at another slack past its projection (0.7.73, AutoBuilder). |
| 2421 | 29 | `private static Weighed weighPaving(Game game, Measure m, Map<BuildingsTemplate, Long> site, double landLeft, double slack, bool...` |  |

### A BUILDING OVER ITS LIFE, FOR WHAT IT WILL SERVE (0.7.101, batch P2) (lines 2451-2635)

| line | len | member | says |
|---:|---:|---|---|
| 2509 | 14 | `public static double lifeTrend(Game game, double months)` | The city's demand `months` from now over today's, for a building's life (0.7.101): the city's own population trend over the last LIFE_MONTHS of its record - or since its first month recorded, a younger city - linear i... |
| 2525 | 4 | `public static double orderLife(Game game, BuildingsTemplate t, int count, double landLeft, Map<BuildingsTemplate, Long> site)` | An order of `count` of t over its life (0.7.101): its quote for the count, its ground at landValue() on the land left, and lifeFactor() months of running() for each. |
| 2531 | 6 | `public static double orderLife(Game game, NewUnit u, int count, double landLeft, Map<BuildingsTemplate, Long> site)` | ...of one new building already resolved (QN1): its own running cost; its quote and ground are the template's. |
| 2539 | 8 | `public static double pavingOrderLife(Game game, int count, double landLeft, Map<BuildingsTemplate, Long> site)` | ...`count` pavings' (0.7.101): their quote (Game.quotePave()), less the ground they free at landValue() on the land left, and lifeFactor() months of a paved road's running over a gravel road's for each; NaN with nothi... |
| 2557 | 25 | `public static double shortfall(Game game, Measure m, Map<BuildingsTemplate, Long> added, Ahead p)` | What a measure is short of, in its own unit, with these buildings standing as well, at `p`'s demand (0.7.101): power and water the supply NEEDS YOU's quarter in hand asks (the demand over NETWORK_YELLOW) less the supp... |
| 2584 | 6 | `public static boolean weighsServed(Measure m)` | Whether a measure's candidates are weighed for what they will serve over their lives (shortfall()): power, water, the roads (streets and highways) and care. |
| 2601 | 19 | `public static double servedOverLife(Game game, Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> order...` | What an order serves over its life, a month on average, in the measure's unit (0.7.101): each of its LIFE_YEARS years read at its middle month - from its opening, `p`'s months less the horizon (BusinessInvestment.lead... |
| 2622 | 3 | `public static double perServed(double life, double served, int count, double unit)` | The figure from an order's life and what it serves (0.7.101): the life over what it will serve; with that not measured, or nothing, over what its count adds (count x unit). |
| 2631 | 4 | `public static double lifePerServed(Game game, Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t, int count, dou...` | A candidate's informational cost per physical unit relieved (HW1). |

### AN IRON FIELD THAT WILL NOT PAY BACK (0.7.101, batch P2) (lines 2636-2830)

| line | len | member | says |
|---:|---:|---|---|
| 2665 | 4 | **type** `public record Payback(int mines, double earns, double payment)` | An iron field weighed (0.7.101): the mines the city could staff and work on it, their profit a month, and the month's payment that repays its price - it pays back when the profit is at least the payment. |
| 2667 | 1 | `public boolean pays()` _(in BuildAdvice.Payback)_ | Whether the mines pay the field back. |
| 2671 | 4 | `public static Payback ironPayback(Game game, LandParcel field)` | Whether an offer holding iron would pay itself back in this city (fieldEarnings() against fieldPayment()) - since 0.7.142 by the best mine that fits each of its fields. |
| 2701 | 1 | **type** `public record FieldMines(Deposit.FieldType type, int sites, BuildingsTemplate mine, long count)` | A field of an offer and the mines that would fill it (0.7.142): its type, its sites, a kind of mine and how many of it. |
| 2704 | 32 | `public static List<FieldMines> fieldMines(Game game, LandParcel field)` | Each iron field an offer holds, filled with the iron mines that fit it by type and size in the plan's order (above); a field no mine fits has a row with none. |
| 2738 | 29 | `public static double[] fieldEarnings(Game game, LandParcel field)` | What an offer's iron fields would earn the city's mines a month (0.7.142; BY THE FIELDS IT HOLDS, above): {mines, their monthly profit}, fieldEarnings()'s count on fieldMines()' mines. |
| 2781 | 25 | `public static double[] fieldEarnings(Game game, int sites)` | What a field of `sites` iron sites would earn the city's mines a month (0.7.67, M3b; moved from LongPlaytest in 0.7.101): {mines, their monthly profit}. |
| 2815 | 6 | `public static double fieldPayment(Game game, LandParcel field)` | The month's payment that repays a field's price here over Game.BUILD_BOND_YEARS (0.7.67, M3b; moved from LongPlaytest in 0.7.101): the level payment at the rate the market quotes for that much money at that term (Debt... |
| 2823 | 7 | `public static boolean higherWorse(Measure m)` | Which way is worse for a measure's figure. |

### A BERTH THAT PAYS BACK (FR3, 2026-10-19; docs/queue/FR3.md 5, the (lines 2831-2926)

| line | len | member | says |
|---:|---:|---|---|
| 2869 | 5 | `public static List<Measure> portMeasures()` | The port measures, one a kind of cargo, in Cargo order (FR3). |
| 2876 | 8 | `static double portUncovered(Game game, Measure m, Map<BuildingsTemplate, Long> added)` | A port measure's uncovered tonnes a month with these buildings standing as well: the kind's carriable() less its berths standing and the berths in `added` (Ports.uncovered()). |
| 2894 | 7 | `public static BuildingsTemplate portTerminal(Game game, Measure m, Map<BuildingsTemplate, Long> added, java.util.Set<BuildingsT...` | The terminal to order for a port measure, or null (FR3): with the kind's uncovered tonnes (`added` standing too) paying back some terminal of it but those in `skip` at the port dues dial, the largest of them whose bre... |
| 2908 | 18 | `static Suggestion portSuggestion(Game game, Measure m, double cashLeft, double landLeft, java.util.Set<BuildingsTemplate> skip)` | A port measure's card: as many of the terminal portTerminal() chose as the uncovered tonnes pay back, each counted after the ones before it, and one at least (SL1, Ports.orderCount(); it was one), or null for none (po... |

### A BERTH'S PAYBACK, FOR ITS CARD (FR4, 2026-10-20) (lines 2927-3002)

| line | len | member | says |
|---:|---:|---|---|
| 2939 | 1 | **type** `public enum BerthState` | Where a berth stands on its payback (FR4): PAYS - the dues at the dial cover its cost and more than its upkeep; SHORT - cargo of its kind crosses and no berth takes it, but not enough of it (Ports.berthPaysBack()); CO... |
| 2953 | 10 | **type** `public record BerthPayback(BuildingsTemplate terminal, BerthState state, double carriable, double uncovered...` | One more of a terminal, weighed as the advice weighs one (FR4): the kind's carriable and uncovered tonnes a month, the tonnes a month the advice needs uncovered before the terminal pays back (needed; NaN with no dues)... |
| 2958 | 1 | `public boolean pays()` _(in BuildAdvice.BerthPayback)_ | Whether it pays back (state PAYS). |
| 2961 | 1 | `public double carriedAYear()` _(in BuildAdvice.BerthPayback)_ | The tonnes a year it would carry. |
| 2976 | 26 | `public static BerthPayback berthPayback(Game game, BuildingsTemplate t)` | A sea terminal's payback, as its card says it (FR4; null for any other building). |

### THE ISLAND LINK (AL1, 2026-11-09) (lines 3003-3216)

| line | len | member | says |
|---:|---:|---|---|
| 3029 | 1 | **type** `public enum LinkKind` | How a link is made: a long bridge, sea terminals, or not at all (NONE: the Link says why). |
| 3032 | 1 | **type** `public record LinkOrder(BuildingsTemplate template, int asked, double price)` | One order of a link: the template, the landmass its order is for (a bridge's far landmass, or a terminal's `asked`: Landmasses.RULE for home, an id for an island's own shore), and its quote's total, thousands of US do... |
| 3039 | 12 | **type** `public record Link(int landmass, String name, double sqFt, LinkKind kind, List<LinkOrder> orders, BridgeSur...` | What the city should order to link an island: the island's id, name and buildable ground (square feet), the kind, the orders (none for NONE), the crossing a bridge is surveyed over (null otherwise), the orders' total ... |
| 3043 | 1 | `public double perSqFt()` _(in BuildAdvice.Link)_ | Its price a square foot of ground it links: the candidate's unit is the ground, not trips. |
| 3046 | 1 | `public boolean underWay()` _(in BuildAdvice.Link)_ | Whether a bridge is already on site to the island (FB13): the screens' link for it, a BRIDGE with no orders; linkFor() never returns one. |
| 3049 | 1 | `public BuildingsTemplate bridge()` _(in BuildAdvice.Link)_ | The bridge a BRIDGE link orders (the Road Bridge, or the Highway Bridge where a highway stands), or null for any other link and for one under way. |
| 3053 | 1 | **type** `private record Found(LinkOrder order, double months)` | One terminal order found, and its months. |
| 3059 | 1 | **type** `private record NoCrossing(int month, String why)` | A survey that found no crossing: the month it was made and its refusal in BridgeView's words. |
| 3072 | 13 | `public static Link linkFor(Game game)` | The link the city should order for the island it most needs, or null when nothing is unlinked (checked first and alone: every generated world, and every city on one landmass, reads nothing else). |
| 3087 | 15 | `private static List<Landmasses.Row> islandsToLink(Game game, Landmasses lms)` | The islands the rule would link, the one with the most buildable ground first (the lower id on a tie): unlinked, with no bridge on site to them and no terminal on them while linked ground has one. |
| 3104 | 5 | `private static long linkedTerminals(Landmasses lms)` | The sea terminals standing or on site on the landmasses the city has linked. |
| 3114 | 62 | `private static Link linkOf(Game game, Landmasses lms, Landmasses.Row island, boolean screens)` | The rule for one island. |
| 3177 | 10 | `private static Link portLink(Landmasses.Row island, List<Found> found)` |  |
| 3189 | 10 | `private static BuildingsTemplate bridgeTemplate(Game game)` | The Highway Bridge where the city has an Elevated Highway standing, else the Road Bridge; null when the one wanted is not yet known (never the Rail Bridge). |
| 3205 | 11 | `private static Found cheapestTerminal(Game game, Landmasses lms, int asked, int island)` | The cheapest sea terminal the city knows for a landmass: by the quote of one asked for `asked`, among those whose order would stand where it is asked. |

### THE ISLAND LINK, AS THE SCREENS SAY IT (FB13, AL1-W) (lines 3217-3292)

| line | len | member | says |
|---:|---:|---|---|
| 3230 | 1 | **type** `private record ScreenNone(int version, long stamp, String why)` | A survey that found no crossing as a screen's read of it remembers it: the refusal, and the landmasses' version and the land's stamp it was made on (it stands for that ground and no other, where the rule's stands NO_C... |
| 3239 | 2 | **type** `private record Shown(java.lang.ref.WeakReference<Landmasses> landmasses, int month, int version, long stamp...` | What the screens last read, and what it was read against. |
| 3252 | 4 | `public static Link islandLink(Game game, int island)` | What the rule says links one island, as the screens word it: the link linkOf() finds for it (BRIDGE, PORT, or NONE with the survey's refusal and why no port), or - for an island a bridge is on site to - a BRIDGE with ... |
| 3258 | 4 | `public static Link linkShown(Game game)` | The link the Build tab's card shows: the one linkFor() would answer, the island it most needs; null when there is none. |
| 3263 | 29 | `private static Shown shown(Game game)` |  |

