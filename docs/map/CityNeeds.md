# CityNeeds.java - 984 lines · 46 methods · 20 constants · model

`ham/citybuildersim/CityNeeds.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> NEEDS YOU, measured: everything with a lever, each against its own line, in
> the order a city is built - the list the left panel's Summary prints, the
> header's "Needs you" chip counts, and the Build tab's overview and its
> suggestions read (0.7.24).
> 
> WHY. The list was measured inside the interface (SummaryScreen.watchAll(),
> since 2026-09-14), and 0.7.24's Build overview needed the same verdicts -
> which need in a category is worst, and how bad - for its rings and for
> the orders it suggests (BuildAdvice). A second copy of the thresholds in
> the Build tab would have been a second scoring, and the two would have
> disagreed the first time either was tuned; a scoring the interface alone
> can run is one no harness can hold. So the measuring moved here, verbatim
> - every line, every threshold, every reading and the order they are read
> in - and the panel maps each need to the screen that answers it (its Go).
> The interface's words for a figure (people, money, a wait) are passed in
> as Words, so the readings on the panel are the ones it printed before;
> PLAIN is the same shapes without the toolkit, for a harness.
> 
> One row is new: FALLS DUE, the bottom strip's red maturity, which left the
> frame in 0.7.24 (see fallsDue()). Since 0.7.45 another: PRICES, whether the
> city still believes the central bank (prices()).
> 
> Since 0.7.41 the rows that are a supply against a demand - power, water,
> the road, care and the basic ladder - read what they serve (a school above
> the ladder still reads its seats and, since 0.7.51, who would come and be
> hired), and the one verdict every screen colours such a gauge by is here
> too (SERVED, below). What is listed, and in what order, is still read on
> the lines as they stood.
> 
> Reads the city; changes nothing. Every figure is a getter the screens
> already read.

**Uses:** [CareType](CareType.md) (47), [EducationType](EducationType.md) (35), [Game](Game.md) (20), [Education](Education.md) (6), [JobType](JobType.md) (6), [InfrastructureManager](InfrastructureManager.md) (5), [Formats](Formats.md) (4), [BuildingType](BuildingType.md) (4), [PopulationCohorts](PopulationCohorts.md) (3), [DebtManager](DebtManager.md) (3), [PopulationManager](PopulationManager.md) (3), [WageBand](WageBand.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (2), [SafetyType](SafetyType.md) (2), [LandManager](LandManager.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [EconomyManager](EconomyManager.md) (1), [Healthcare](Healthcare.md) (1), [Crime](Crime.md) (1), [FamilyModel](FamilyModel.md) (1), [Bank](Bank.md) (1), [Expectations](Expectations.md) (1), [PriceIndex](PriceIndex.md) (1), [Health](Health.md) (1), [NationalAccounts](NationalAccounts.md) (1), [LabourMarket](LabourMarket.md) (1), [BuildingManager](BuildingManager.md) (1), [AgeBand](AgeBand.md) (1), [Debt](Debt.md) (1)... and 1 more

**Used by (25):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureManager](InfrastructureManager.md), [InfrastructureScreen](InfrastructureScreen.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [UtilitiesHandler](UtilitiesHandler.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 161 | THE LINES. Per condition, not one rule, because the same percentage |
| 247 | SERVED (0.7.41): ONE RULE FOR EVERY SERVICE GAUGE. Jerus, playing |
| 366 | THE LIST |
| 391 | · · the networks |
| 412 | · · the care |
| 459 | · · the schools |
| 476 | · · and the schools above them |
| 479 | · · the police |
| 492 | · · the housing |
| 498 | · · the ground |
| 504 | · · the money |
| 539 | · · the promises |
| 681 | THE PIECES |
| 855 | · the student body the city would get and hire (0.7.51) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 45 | `CityNeeds.Kind.POWER` |  |
| 45 | `CityNeeds.Kind.WATER` |  |
| 45 | `CityNeeds.Kind.ROADS` |  |
| 45 | `CityNeeds.Kind.CARE` |  |
| 45 | `CityNeeds.Kind.DEAD` |  |
| 45 | `CityNeeds.Kind.PLOTS` |  |
| 45 | `CityNeeds.Kind.BASIC_SCHOOLS` |  |
| 45 | `CityNeeds.Kind.HIGHER_SCHOOL` |  |
| 45 | `CityNeeds.Kind.CRIME` |  |
| 45 | `CityNeeds.Kind.CELLS` |  |
| 46 | `CityNeeds.Kind.HOMES` |  |
| 46 | `CityNeeds.Kind.GROUND` |  |
| 46 | `CityNeeds.Kind.BUILDERS` |  |
| 46 | `CityNeeds.Kind.TREASURY` |  |
| 46 | `CityNeeds.Kind.BANK` |  |
| 46 | `CityNeeds.Kind.BORROWING` |  |
| 46 | `CityNeeds.Kind.FALLS_DUE` |  |
| 46 | `CityNeeds.Kind.PENSIONS` |  |
| 46 | `CityNeeds.Kind.WAGES` |  |
| 46 | `CityNeeds.Kind.BUDGET` |  |
| 48 | `CityNeeds.Kind.PRICES` | Whether the city still believes the central bank (0.7.45): the anchor's row. |
| 53 | `CityNeeds.Go.UTILITIES` |  |
| 53 | `CityNeeds.Go.ROADS` |  |
| 53 | `CityNeeds.Go.HEALTHCARE` |  |
| 53 | `CityNeeds.Go.EDUCATION` |  |
| 53 | `CityNeeds.Go.SAFETY` |  |
| 53 | `CityNeeds.Go.HOMES` |  |
| 54 | `CityNeeds.Go.LAND` |  |
| 54 | `CityNeeds.Go.BUILDERS` |  |
| 54 | `CityNeeds.Go.FINANCES` |  |
| 54 | `CityNeeds.Go.BANK` |  |
| 54 | `CityNeeds.Go.PENSIONS` |  |
| 54 | `CityNeeds.Go.WAGES` |  |
| 54 | `CityNeeds.Go.TAXES` |  |
| 56 | `CityNeeds.Go.MONEY` | Policy › Money › The policy rate (0.7.45). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 146 | `CityNeeds.PLAIN` | `new Words() { @ Override public String people(double count) { return Formats....` | The same four shapes without the toolkit (Formats), for a harness and a log. |
| 168 | `CityNeeds.NETWORK_YELLOW` | `.75, NETWORK_RED = 1` | A network is listed from three-quarters of its capacity, red once it is over. |
| 171 | `CityNeeds.GENERAL_YELLOW` | `.80, GENERAL_RED =.50` | General care is watched hardest: it moves the sick rate. |
| 174 | `CityNeeds.OTHER_CARE_YELLOW` | `.70, OTHER_CARE_RED =.40` | Childcare and senior care kill at the ends of life; a young city legitimately has neither for a while. |
| 177 | `CityNeeds.PLOTS_WATCHED` | `120` | Burial plots are watched once fewer than this many months are left. |
| 179 | `CityNeeds.PLOTS_YELLOW` | `24, PLOTS_RED = 6` | ...listed under two years of plots, red under six months. |
| 182 | `CityNeeds.SCHOOLS_YELLOW` | `.90, SCHOOLS_RED =.60` | The basic ladder's bottleneck, its coverage (read as served since 0.7.41; "taught" until then). |
| 185 | `CityNeeds.SEATS_YELLOW` | `1.05, SEATS_RED = 2` | A school above the ladder: who would come and be hired (wanted(), 0.7.51; who would come until then) over its seats. |
| 188 | `CityNeeds.SEATS_FLOOR` | `25` | A class's worth: fewer students than this the city would get and hire (would-be students until 0.7.51) and a school is not a row (measured: 3 would-be law students in a city of 1,650). |
| 191 | `CityNeeds.CRIME_YELLOW` | `1.2, CRIME_RED = 1.5` | Crime against Canada's rate. |
| 194 | `CityNeeds.CELLS_YELLOW` | `1, CELLS_RED = 25` | The caught, not held. |
| 202 | `CityNeeds.SICK_YELLOW` | `.06, SICK_RED =.12` | The sick rate, the share of the workforce off sick (0.7.28): the left panel's OFF SICK lines (SummaryScreen's literals until then), here so the Services screen colours the same figure by the same lines. |
| 211 | `CityNeeds.FALLS_DUE_MONTHS` | `3` | FALLS DUE (0.7.24): paper due within this many months is red, as the bottom strip drew its maturity chip until it left the frame (its maturity chip, FinancesScreen's until 0.7.32 and its urgency() since: gap <= 3 red,... |
| 219 | `CityNeeds.FALLS_DUE_SOON_MONTHS` | `12` | ...and paper due within this many months is amber: the strip's other rule (gap <= 12), named in 0.7.32 so the Finances tab's NEXT DUE, its book and its strip colour a maturity by one line. |
| 229 | `CityNeeds.SERVICE_FELT` | `.12, SERVICE_CONSTRAINED =.25` | DEBT SERVICE, A SHARE OF THE TAKE (0.7.32): what the city pays its lenders against what it collects is comfortable under SERVICE_FELT, felt from there, and constrained past SERVICE_CONSTRAINED - the bands the Finances... |
| 239 | `CityNeeds.YEAR_WALL` | `.5` | A WALL ON THE LADDER (0.7.32): a calendar year whose payments - coupons and principal - pass this share of a year of revenue is one a city meets by refinancing before it arrives. |
| 273 | `CityNeeds.SERVED` | `"served", SHORT = "short", TIGHT = "tight", ENOUGH = "enough"` | The gauges' words (0.7.41), the same on every screen: what the figure is, and its verdict's three words. |
| 569 | `CityNeeds.GROUND_YELLOW` | `LandManager.BLOCK_SQ_FT` | Free ground under which NEEDS YOU lists the GROUND row: a block, 100,000 sq ft. |
| 572 | `CityNeeds.TRUST_RED` | `.5` | Trust in the central bank under which a fall is red in the PRICES row: half - under it, what people expect is more recent prices than the bank's target. |
| 858 | `CityNeeds.FIRST_SCHOOL_SHARE` | `.5` | A first school above the ladder is listed once the students it would get and hire fill this share of the smallest that teaches it - the firms' first-plant share (Materials.FIRST_PLANT_UTILISATION, Agriculture.FIRST_FA... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 946 | **type** `public final class CityNeeds` | NEEDS YOU, measured: everything with a lever, each against its own line, in the order a city is built - the list the left panel's Summary prints, the header's "Needs you" chip counts, and the Build tab's overview and ... |
| 41 | 1 | `private CityNeeds()` |  |
| 44 | 6 | **type** `public enum Kind` | What a need is about - the measure the Build tab's advice and rings read. |
| 52 | 6 | **type** `public enum Go` | Where a need's fix is: the screen its row opens. |
| 84 | 48 | **type** `public record Need(String label, String reading, int level, double near, Kind kind, Go go, CareType care, E...` | One thing being watched. |
| 90 | 9 | `public boolean cityBuilds()` _(in CityNeeds.Need)_ | A need the city answers with a building of its own (BuildAdvice): roads, care, schools, police, cells, power and water. |
| 106 | 10 | `public Served served()` _(in CityNeeds.Need)_ | What the row's figure serves, read the one way (0.7.41), for the rows that are a supply against a demand - power, water, the road, care and the schools - or null for the rest. |
| 122 | 4 | `public int verdictLevel()` _(in CityNeeds.Need)_ | The row's colour on a screen (0.7.41): a served row's verdict, so a gauge and its row read one colour everywhere; any other row's level. |
| 127 | 4 | `Need withOnSite(String moreReading, int units, double months, int newLevel)` _(in CityNeeds.Need)_ |  |
| 138 | 6 | **type** `public interface Words` | How the interface writes a figure, passed in so the readings are its own: a headcount, an amount of money in the model's thousands, a short number for a narrow cell, and a build time. |
| 139 | 1 | `String people(double count)` _(in CityNeeds.Words)_ |  |
| 140 | 1 | `String money(double thousands)` _(in CityNeeds.Words)_ |  |
| 141 | 1 | `String shortNumber(double value)` _(in CityNeeds.Words)_ |  |
| 142 | 1 | `String monthsWait(double months)` _(in CityNeeds.Words)_ |  |

### THE LINES. Per condition, not one rule, because the same percentage (lines 161-246)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 4 | `public static int serviceLevel(double share)` | A debt-service share's band (0.7.32): 0 comfortable, 1 felt, 2 constrained; a share that is not a number is comfortable. |

### SERVED (0.7.41): ONE RULE FOR EVERY SERVICE GAUGE. Jerus, playing (lines 247-365)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 4 | **type** `public record Served(double share, int level, String word)` | One gauge, read the one way (0.7.41): its share served - supply over demand, unclamped, +∞ when nothing is asked of it - its verdict's level (0 green, 1 amber, 2 red; -1 none, for a gauge no line judges) and its word ... |
| 283 | 1 | `public boolean enough()` _(in CityNeeds.Served)_ | Green: 100% or more and off NEEDS YOU's list. |
| 287 | 4 | `public static double servedShare(double supply, double demand)` | Supply over demand, unclamped (0.7.41): nothing asked of it is all of it met, +∞; nothing supplying a demand is nothing, 0. |
| 297 | 5 | `public static String servedPct(double share)` | A served share as a whole per cent, "62%", or a dash for one that is not a finite number (nothing asked of it). |
| 309 | 5 | `public static Served served(double share, double offList, double red)` | The verdict on a served share against two lines in served terms (0.7.41): green past `offList` (strictly, as NEEDS YOU lists a row at its line) and at 100% or more; red at or under `red`; amber between, "short" under ... |
| 316 | 3 | `public static Served unjudged(double share)` | ...and a share no line judges (transit; a school above the ladder NEEDS YOU does not list, listsSchool()): no colour, no word. |
| 327 | 17 | `public static double[] servedLines(Kind kind, CareType care)` | A served kind's two lines in served terms (0.7.41), {off the list past this, red at or under this} - NEEDS YOU's own lines turned over where its figure was a load (a network, the road) or a crowd (who would come and b... |
| 346 | 4 | `public static Served verdict(Kind kind, CareType care, double share)` | The one verdict on a served share of one of NEEDS YOU's served kinds (0.7.41); null for a kind that is not a supply against a demand. |
| 360 | 5 | `public static String freshLimitLine(UtilitiesHandler utilities)` | THE FRESH WATER LIMIT'S LINE (0.7.59, batch J2): what the Services page says under the water when the city's lakes and river hold its plants back - "fresh water limit: 62% of the plants' nameplate idle · buy lake or r... |

### THE LIST (lines 366-680)

| line | len | member | says |
|---:|---:|---|---|
| 375 | 192 | `public static List<Need> measure(Game game, Words w)` | Everything with a lever, measured against its own line, in the order it is read - the fine ones too, because "next to watch" on a healthy city is the nearest of them. |
| 588 | 25 | `public static Need prices(Game game)` | PRICES (0.7.45; the UI spec's D2): whether the city still believes the central bank. |
| 628 | 8 | `public static Need ground(Game game, Words w)` | The GROUND row on its own (0.7.26), as measure() lists it: what the land office's GROUND FREE, Build's LAND FREE and the left panel's land colour themselves by, so the three and NEEDS YOU agree - none of them reads th... |
| 643 | 6 | `public static List<Need> biting(List<Need> all)` | NEEDS YOU: what is near a line or past one, red above yellow - and inside a tier the order they were measured in, which is the order a city is built. |
| 651 | 3 | `public static List<Need> needsYou(Game game)` | NEEDS YOU for this city, in the panel's order. |
| 660 | 4 | `public static Need worst(List<Need> all, Go go)` | The worst need of those a screen answers: the highest level, and of equals the first in NEEDS YOU's order - the row the panel would list first. |
| 672 | 8 | `public static Need worstUnlisted(List<Need> all, Go go)` | ...and of the served rows NEEDS YOU does NOT list, the one that is still not enough (0.7.41): general care at 90% is past its line, so not listed, and short of 100% - the worst verdict, of equals the first in order. |

### THE PIECES (lines 681-854)

| line | len | member | says |
|---:|---:|---|---|
| 686 | 5 | `public static double careCover(Game game, CareType care, PopulationCohorts cohorts, double[] staffing)` | How much of the people who need a kind of care the staffed beds could take: the panel's coverage, Game.careCoverage()'s. |
| 693 | 3 | `public static double careServed(Game game, CareType care, PopulationCohorts cohorts, double[] staffing)` | ...the same places over the same people, unclamped: what the care rows read as served (0.7.41), and Build's care rings. |
| 698 | 3 | `static double careHave(Game game, CareType care, double[] staffing)` | The staffed places of a kind of care: what its row carries as its supply (0.7.41). |
| 703 | 5 | `public static double taxRaised(Game game)` | What the city raised in tax last month: profit, sales, wages and property (the Policy tab's TAX A MONTH). |
| 710 | 9 | `public static int pinnedBands(Game game)` | Wage bands pinned to the minimum wage with people spare in them. |
| 726 | 4 | `public static int level(double value, double yellow, double red, boolean higherWorse)` | A figure's level against two lines, as a row is struck - over() when higher is worse, under() when lower is (0.7.28): for a screen that colours a figure no row lists (the Services screen's OFF SICK) by the same rule, ... |
| 732 | 7 | `static void over(List<Need> out, String label, String reading, double value, double yellow, double red, Kind kind, Go go, CareT...` | Higher is worse. |
| 741 | 7 | `static void under(List<Need> out, String label, String reading, double value, double yellow, double red, Kind kind, Go go, Care...` | Lower is worse. |
| 750 | 5 | `static void flag(List<Need> out, String label, String reading, boolean bad, boolean severe, Kind kind, Go go, double value)` | A thing that is simply true or not. |
| 764 | 16 | `static void onTheWay(Game game, Words w, List<Need> out, int at, java.util.function.Predicate<BuildingsTemplate> serves)` | ...AND WHAT IS ALREADY ON THE WAY (0.7.20). |
| 782 | 4 | `static void onTheWay(Game game, Words w, List<Need> out, java.util.function.Predicate<BuildingsTemplate> serves)` | The same, for the need just measured. |
| 798 | 19 | `static void network(List<Need> out, String label, Kind kind, double demand, double supply, double ratio)` | One network: how much of its capacity is spoken for, and whether it is still meeting demand. |
| 838 | 16 | `static void seatsWanted(Game game, Words w, List<Need> out)` | SEATS AGAINST WHO WOULD COME, for the schools above the basic ladder: what the schools hold, and the student body this city would sustain - so a bigger second number means another building fills. |

### the student body the city would get and hire (0.7.51) (lines 855-984)

| line | len | member | says |
|---:|---:|---|---|
| 861 | 4 | `public static boolean listsSchool(Game game, EducationType type, double want, double seats)` | Whether a school above the ladder is a row: a class's worth wanted (SEATS_FLOOR), and with no seats of its kind yet, FIRST_SCHOOL_SHARE of the smallest school's. |
| 867 | 7 | `public static double smallestSchool(Game game, EducationType type)` | The seats of the smallest building that teaches this; 0 when none does. |
| 881 | 3 | `public static double wanted(Game game, EducationType type, double months, double k)` | The student body the city would get and hire, `months` from now with its posts grown by k: the smaller of who would come by then (wouldComeAt()) and the posts their degree would fill (hires()). |
| 886 | 6 | `public static double wouldComeAt(Game game, EducationType type, double months)` | Who would come `months` from now: wouldCome()'s arithmetic with the feeder's graduates by then added to the band (feederOver()); at 0 it is wouldCome(). |
| 900 | 11 | `public static double feederOver(Game game, EducationType type, double months)` | The feeder's graduates over the next `months`: for a college or a university the high schools' diplomas a month at the ladder's saved coverage (Education.schoolLeavers(), advanceMonth()'s flow) times the months; for a... |
| 920 | 27 | `public static double hires(Game game, EducationType type, double k)` | The posts this school's graduates would take, as a student body: the posts of the band it qualifies for (a professional school: its licence's posts), standing and on site, grown by k, less the people already qualified... |
| 949 | 7 | `public static double wouldCome(Game game, EducationType type)` | The student body a school above the ladder would hold if seats were free: eligible x willing x the enrolment rate, times the course. |
| 965 | 19 | `static void fallsDue(Game game, Words w, List<Need> out)` | FALLS DUE (0.7.24). |

