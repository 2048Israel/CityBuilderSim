# BuildAdviceCheck.java - 1,124 lines · 41 methods · 0 constants · harnesses

`ham/citybuildersim/BuildAdviceCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
>   7. LAND COUNTED (0.7.51): a building's price a unit is its quote for one
>      and its ground at landValue(), over what it serves, to the bit, on
>      the land the cards before leave - a road's with its running over its
>      life since 0.7.70 (BuildAdvice.lifetime(); RoadCheck holds the rest);
>      with no ground free, the land office's prices at nothing pick the
>      road that is cheapest to build and keep, and at ten times the price
>      past which the road that needs the least ground a trip is the
>      cheapest with it, that road.
>   8. SLACK BUILT: a served card is at 100% of its demand projected and
>      SLACK past it, and its count is never fewer than the old rule's.
>   9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE: a college or university row
>      wants no more than would come and no more than would be hired, and a
>      first school half the smallest; a city with students and no posts
>      for them gets no row and no card until the posts come; and the
>      playtest's month-9 university is gone.
>  10. SIZED TO THE PROJECTION: a card's growth is the businesses'
>      growthFactor() over its wait and their horizon, to the bit, and a
>      rising population never orders fewer.
>  11. THE RUN: "Build all three" places the cards' orders in their order,
>      goes all the way exactly when no card is short of land and otherwise
>      stops at the first that is, for land; and what it charges is the
> ... (3 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (166), [CityNeeds](CityNeeds.md) (58), [Game](Game.md) (49), [BuildingsTemplate](BuildingsTemplate.md) (35), [BuildingType](BuildingType.md) (21), [Crime](Crime.md) (13), [JobType](JobType.md) (13), [LongPlaytest](LongPlaytest.md) (10), [SafetyType](SafetyType.md) (9), [GameFiles](GameFiles.md) (6), [BusinessInvestment](BusinessInvestment.md) (6), [CareType](CareType.md) (5), [EducationType](EducationType.md) (5), [Founding](Founding.md) (4), [Formats](Formats.md) (4), [BuildingManager](BuildingManager.md) (4), [Sector](Sector.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [Healthcare](Healthcare.md) (2), [LandManager](LandManager.md) (2), [WageBand](WageBand.md) (2), [ServicesManager](ServicesManager.md) (1), [LandMarket](LandMarket.md) (1), [LandParcel](LandParcel.md) (1)

**Used by (1):** [RoadCheck](RoadCheck.md)

## Sections

| line | section |
|---:|---|
| 240 | 1. THE CATEGORIES |
| 302 | 2. NEEDS YOU'S NEEDS, IN ITS ORDER |
| 355 | 3. THE COUNT AND THE PRICE |
| 497 | 4. BEFORE AND AFTER, BY THE MODEL |
| 655 | 5. THE STAFFING WEIGHTING |
| 725 | 6. NOTHING NEEDED |
| 747 | 7. LAND COUNTED |
| 873 | 8. SLACK BUILT |
| 908 | 9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE |
| 1026 | 10. SIZED TO THE PROJECTION |
| 1071 | 11. THE RUN |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `static int fails` |  |
| 84 | `static PrintStream out` |  |
| 85 | `static PrintStream quiet` |  |
| 533 | `static double wantBefore` | The students a school above the ladder would get and hire, read before the order stood (builtAndRead()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 1044 | **type** `public class BuildAdviceCheck` | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city - and the categories it is filed under. |
| 87 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 92 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 98 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 104 | 5 | `static void quietly(Runnable r)` |  |
| 110 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 134 | 3 | `static Game shortCity(Path root, String name, boolean onSiteClinic)` | A played city short of the city's works: about four thousand people in nine hundred houses on one paved road, no clinic or daycare (the founding's own care only), a school of each basic stage, a college, a university ... |
| 139 | 26 | `static Game shortCity(Path root, String name, boolean onSiteClinic, Set<String> without)` | ...without the buildings named in `without` (section 9: the short city with no university). |
| 173 | 22 | `static Game servedCity(Path root, String name)` | A smaller city well served: every measure NEEDS YOU watches for a city-built building past its line - care of every kind, a school of every basic stage, a college, a university and a medical school with seats for who ... |
| 196 | 28 | `public static void main(String[] args) throws Exception` |  |
| 226 | 13 | `static void printCity(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice)` | The city as the advice read it, for the record. |

### 1. THE CATEGORIES (lines 240-301)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 41 | `static void theCategories(Path root)` |  |
| 284 | 17 | `static boolean checkInvestors(List<BuildAdvice.Category> cats)` |  |

### 2. NEEDS YOU'S NEEDS, IN ITS ORDER (lines 302-354)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 41 | `static void needsYousOrder(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice)` |  |
| 347 | 7 | `static boolean onSiteAnswers(Game g, List<CityNeeds.Need> biting)` | Whether any biting city-built need has something on site that serves it. |

### 3. THE COUNT AND THE PRICE (lines 355-496)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 34 | `static void theCountAndThePrice(Game g, List<BuildAdvice.Suggestion> advice)` |  |
| 398 | 14 | `static boolean fitsOrNone(Game g, BuildAdvice.Suggestion s, double landLeft)` | The land rule: the chosen building's whole count fits the ground the cards before it leave, unless none that keeps the need ahead would - each counted as the advice counts it, at today's demand with the slack for its ... |
| 417 | 29 | `static double unitByTheModel(Game twin, BuildAdvice.Measure m, BuildingsTemplate t)` | One building's staffed capacity as the model counts it: the model's getter with one more of it standing in a twin, less the getter before. |
| 455 | 32 | `static void theCash(Path root)` | THE CASH, NO CAP (0.7.51): with the cash for the first card and half the second, every card is the count that keeps its need ahead, as with the Wealthy cash; the second and after are on credit for exactly what the cas... |
| 489 | 7 | `static boolean sameOrders(List<BuildAdvice.Suggestion> a, List<BuildAdvice.Suggestion> b)` | The same buildings, in the same counts, in the same order. |

### 4. BEFORE AND AFTER, BY THE MODEL (lines 497-654)

| line | len | member | says |
|---:|---:|---|---|
| 500 | 5 | `static Game twin(Game g)` | A twin of the city: the same save, loaded. |
| 518 | 13 | `static double builtAndRead(Game twin, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> add)` | The order and what is on site built standing in a twin, the month's own services pass run on it (SimulationEngine's last steps), and the figure read the way the model reads it. |
| 536 | 60 | `static double readByTheModel(Game twin, BuildAdvice.Measure m)` | A measure's figure, read off the model: the handlers' own loads, the education month, the crime month. |
| 598 | 6 | `static Crime crimeMonth(Game twin, double officers, double cells, int months)` | The city's crime, restored into a fresh Crime and played `months` months on these officers and cells. |
| 606 | 8 | `static void advance(Game twin, Crime month, double officers, double cells)` | One month of crime on the city's own causes - the adults in each, from the pressure the city's month left. |
| 615 | 12 | `static void beforeAndAfter(Path root, Game g, List<BuildAdvice.Suggestion> advice)` |  |
| 629 | 25 | `static void everyMeasure(Path root, Game g)` | Every measure a city category opens on: an order of the first building that serves it, built in a twin. |

### 5. THE STAFFING WEIGHTING (lines 655-724)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 43 | `static void theStaffingWeighting(Path root)` | Two clinics alike in everything - price, capacity, land, posts in all - but how many of the scarcest of the clinic's own posts they need: the advice takes the one with fewer. |
| 706 | 12 | `static BuildingsTemplate clone(BuildingsTemplate t, String name, JobType a, int na, JobType b, int nb)` |  |
| 719 | 5 | `static int posts(BuildingsTemplate t)` |  |

### 6. NOTHING NEEDED (lines 725-746)

| line | len | member | says |
|---:|---:|---|---|
| 727 | 15 | `static void nothingNeeded(Path root)` |  |
| 743 | 3 | `static boolean bitsEqual(double a, double b)` |  |

### 7. LAND COUNTED (lines 747-872)

| line | len | member | says |
|---:|---:|---|---|
| 769 | 93 | `static void landCounted(Path root)` | LAND COUNTED (0.7.51): the short city's cards with the land office's listing at nothing, as it stands, and at ten times its prices (each parcel's dollar price in LandMarket's listing state, scaled): every card's price... |
| 864 | 8 | `static double[][] scaledOffers(double[][] base, double f)` | The offers' records with each one's dollar price times f (LandParcel.offerRow(): the price second from the end). |

### 8. SLACK BUILT (lines 873-907)

| line | len | member | says |
|---:|---:|---|---|
| 882 | 25 | `static void slackBuilt(Game g, List<BuildAdvice.Suggestion> advice)` | SLACK BUILT (0.7.51): a served card - power, water, the road, care, the schools - at 100% or more of its demand at its projection with SLACK on top, not just off NEEDS YOU's list; care's and a basic school's demand th... |

### 9. NO HIGHER EDUCATION WITHOUT ITS PIPELINE (lines 908-1025)

| line | len | member | says |
|---:|---:|---|---|
| 920 | 49 | `static void thePipeline(Path root, Game g, List<CityNeeds.Need> all)` | NO HIGHER EDUCATION WITHOUT ITS PIPELINE (0.7.51). |
| 971 | 18 | `static void higherRows(Game g, List<CityNeeds.Need> all, String where, int[] rows)` | Every school row above the ladder: no more wanted than would come or be hired, to the bit; with no seats, a first school's. |
| 990 | 4 | `static CityNeeds.Need schoolRow(List<CityNeeds.Need> all, EducationType t)` |  |
| 995 | 6 | `static boolean suggests(Game g, List<CityNeeds.Need> all, EducationType t)` |  |
| 1003 | 22 | `static Game founded(Path root, String name, int month)` | The default playtest's founding (LongPlaytest's own: Standard, its first houses, shops and fields), played to `month`. |

### 10. SIZED TO THE PROJECTION (lines 1026-1070)

| line | len | member | says |
|---:|---:|---|---|
| 1036 | 34 | `static void sizedToTheProjection(Path root)` | SIZED TO THE PROJECTION (0.7.51): the short city's population history rising - two per cent a month over the businesses' window to the people it has - against the same history flat. |

### 11. THE RUN (lines 1071-1124)

| line | len | member | says |
|---:|---:|---|---|
| 1082 | 42 | `static void theRun(Path root, Game g, List<BuildAdvice.Suggestion> advice)` | THE RUN (0.7.51): "Build all three" places BuildAdvice.run(), the cards' orders in their order. |

