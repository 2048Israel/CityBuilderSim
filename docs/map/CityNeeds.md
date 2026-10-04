# CityNeeds.java - 811 lines · 38 methods · 18 constants · model

`ham/citybuildersim/CityNeeds.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

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
> frame in 0.7.24 (see fallsDue()).
> 
> Since 0.7.41 the rows that are a supply against a demand - power, water,
> the road, care and the basic ladder - read what they serve (a school above
> the ladder still reads its seats and who would come), and the one verdict
> every screen colours such a gauge by is here too (SERVED, below). What is
> listed, and in what order, is still read on the lines as they stood.
> 
> Reads the city; changes nothing. Every figure is a getter the screens
> already read.

**Uses:** [CareType](CareType.md) (46), [EducationType](EducationType.md) (25), [Game](Game.md) (13), [InfrastructureManager](InfrastructureManager.md) (5), [Formats](Formats.md) (4), [Education](Education.md) (4), [BuildingType](BuildingType.md) (4), [PopulationCohorts](PopulationCohorts.md) (3), [SafetyType](SafetyType.md) (2), [LabourMarket](LabourMarket.md) (2), [PopulationManager](PopulationManager.md) (2), [WageBand](WageBand.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [EconomyManager](EconomyManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Healthcare](Healthcare.md) (1), [Crime](Crime.md) (1), [FamilyModel](FamilyModel.md) (1), [Bank](Bank.md) (1), [LandManager](LandManager.md) (1), [Health](Health.md) (1), [NationalAccounts](NationalAccounts.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [DebtManager](DebtManager.md) (1), [Debt](Debt.md) (1), [Rollover](Rollover.md) (1)

**Used by (19):** [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureManager](InfrastructureManager.md), [InfrastructureScreen](InfrastructureScreen.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [UtilitiesHandler](UtilitiesHandler.md)

## Sections

| line | section |
|---:|---|
| 155 | THE LINES. Per condition, not one rule, because the same percentage |
| 241 | SERVED (0.7.41): ONE RULE FOR EVERY SERVICE GAUGE. Jerus, playing |
| 344 | THE LIST |
| 369 | · · the networks |
| 390 | · · the care |
| 437 | · · the schools |
| 454 | · · and the schools above them |
| 457 | · · the police |
| 470 | · · the housing |
| 476 | · · the ground |
| 482 | · · the money |
| 515 | · · the promises |
| 612 | THE PIECES |

## Enum constants

| line | constant | says |
|---:|---|---|
| 43 | `CityNeeds.Kind.POWER` |  |
| 43 | `CityNeeds.Kind.WATER` |  |
| 43 | `CityNeeds.Kind.ROADS` |  |
| 43 | `CityNeeds.Kind.CARE` |  |
| 43 | `CityNeeds.Kind.DEAD` |  |
| 43 | `CityNeeds.Kind.PLOTS` |  |
| 43 | `CityNeeds.Kind.BASIC_SCHOOLS` |  |
| 43 | `CityNeeds.Kind.HIGHER_SCHOOL` |  |
| 43 | `CityNeeds.Kind.CRIME` |  |
| 43 | `CityNeeds.Kind.CELLS` |  |
| 44 | `CityNeeds.Kind.HOMES` |  |
| 44 | `CityNeeds.Kind.GROUND` |  |
| 44 | `CityNeeds.Kind.BUILDERS` |  |
| 44 | `CityNeeds.Kind.TREASURY` |  |
| 44 | `CityNeeds.Kind.BANK` |  |
| 44 | `CityNeeds.Kind.BORROWING` |  |
| 44 | `CityNeeds.Kind.FALLS_DUE` |  |
| 44 | `CityNeeds.Kind.PENSIONS` |  |
| 44 | `CityNeeds.Kind.WAGES` |  |
| 44 | `CityNeeds.Kind.BUDGET` |  |
| 49 | `CityNeeds.Go.UTILITIES` |  |
| 49 | `CityNeeds.Go.ROADS` |  |
| 49 | `CityNeeds.Go.HEALTHCARE` |  |
| 49 | `CityNeeds.Go.EDUCATION` |  |
| 49 | `CityNeeds.Go.SAFETY` |  |
| 49 | `CityNeeds.Go.HOMES` |  |
| 50 | `CityNeeds.Go.LAND` |  |
| 50 | `CityNeeds.Go.BUILDERS` |  |
| 50 | `CityNeeds.Go.FINANCES` |  |
| 50 | `CityNeeds.Go.BANK` |  |
| 50 | `CityNeeds.Go.PENSIONS` |  |
| 50 | `CityNeeds.Go.WAGES` |  |
| 50 | `CityNeeds.Go.TAXES` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 140 | `CityNeeds.PLAIN` | `new Words() { @ Override public String people(double count) { return Formats....` | The same four shapes without the toolkit (Formats), for a harness and a log. |
| 162 | `CityNeeds.NETWORK_YELLOW` | `.75, NETWORK_RED = 1` | A network is listed from three-quarters of its capacity, red once it is over. |
| 165 | `CityNeeds.GENERAL_YELLOW` | `.80, GENERAL_RED =.50` | General care is watched hardest: it moves the sick rate. |
| 168 | `CityNeeds.OTHER_CARE_YELLOW` | `.70, OTHER_CARE_RED =.40` | Childcare and senior care kill at the ends of life; a young city legitimately has neither for a while. |
| 171 | `CityNeeds.PLOTS_WATCHED` | `120` | Burial plots are watched once fewer than this many months are left. |
| 173 | `CityNeeds.PLOTS_YELLOW` | `24, PLOTS_RED = 6` | ...listed under two years of plots, red under six months. |
| 176 | `CityNeeds.SCHOOLS_YELLOW` | `.90, SCHOOLS_RED =.60` | The basic ladder's bottleneck, its coverage (read as served since 0.7.41; "taught" until then). |
| 179 | `CityNeeds.SEATS_YELLOW` | `1.05, SEATS_RED = 2` | A school above the ladder: who would come over its seats. |
| 182 | `CityNeeds.SEATS_FLOOR` | `25` | A class's worth: fewer would-be students than this and a school is not a row (measured: 3 would-be law students in a city of 1,650). |
| 185 | `CityNeeds.CRIME_YELLOW` | `1.2, CRIME_RED = 1.5` | Crime against Canada's rate. |
| 188 | `CityNeeds.CELLS_YELLOW` | `1, CELLS_RED = 25` | The caught, not held. |
| 196 | `CityNeeds.SICK_YELLOW` | `.06, SICK_RED =.12` | The sick rate, the share of the workforce off sick (0.7.28): the left panel's OFF SICK lines (SummaryScreen's literals until then), here so the Services screen colours the same figure by the same lines. |
| 205 | `CityNeeds.FALLS_DUE_MONTHS` | `3` | FALLS DUE (0.7.24): paper due within this many months is red, as the bottom strip drew its maturity chip until it left the frame (its maturity chip, FinancesScreen's until 0.7.32 and its urgency() since: gap <= 3 red,... |
| 213 | `CityNeeds.FALLS_DUE_SOON_MONTHS` | `12` | ...and paper due within this many months is amber: the strip's other rule (gap <= 12), named in 0.7.32 so the Finances tab's NEXT DUE, its book and its strip colour a maturity by one line. |
| 223 | `CityNeeds.SERVICE_FELT` | `.12, SERVICE_CONSTRAINED =.25` | DEBT SERVICE, A SHARE OF THE TAKE (0.7.32): what the city pays its lenders against what it collects is comfortable under SERVICE_FELT, felt from there, and constrained past SERVICE_CONSTRAINED - the bands the Finances... |
| 233 | `CityNeeds.YEAR_WALL` | `.5` | A WALL ON THE LADDER (0.7.32): a calendar year whose payments - coupons and principal - pass this share of a year of revenue is one a city meets by refinancing before it arrives. |
| 267 | `CityNeeds.SERVED` | `"served", SHORT = "short", TIGHT = "tight", ENOUGH = "enough"` | The gauges' words (0.7.41), the same on every screen: what the figure is, and its verdict's three words. |
| 545 | `CityNeeds.GROUND_YELLOW` | `LandManager.BLOCK_SQ_FT` | Free ground under which NEEDS YOU lists the GROUND row: a block, 100,000 sq ft. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 775 | **type** `public final class CityNeeds` | NEEDS YOU, measured: everything with a lever, each against its own line, in the order a city is built - the list the left panel's Summary prints, the header's "Needs you" chip counts, and the Build tab's overview and ... |
| 39 | 1 | `private CityNeeds()` |  |
| 42 | 4 | **type** `public enum Kind` | What a need is about - the measure the Build tab's advice and rings read. |
| 48 | 4 | **type** `public enum Go` | Where a need's fix is: the screen its row opens. |
| 78 | 48 | **type** `public record Need(String label, String reading, int level, double near, Kind kind, Go go, CareType care, E...` | One thing being watched. |
| 84 | 9 | `public boolean cityBuilds()` _(in CityNeeds.Need)_ | A need the city answers with a building of its own (BuildAdvice): roads, care, schools, police, cells, power and water. |
| 100 | 10 | `public Served served()` _(in CityNeeds.Need)_ | What the row's figure serves, read the one way (0.7.41), for the rows that are a supply against a demand - power, water, the road, care and the schools - or null for the rest. |
| 116 | 4 | `public int verdictLevel()` _(in CityNeeds.Need)_ | The row's colour on a screen (0.7.41): a served row's verdict, so a gauge and its row read one colour everywhere; any other row's level. |
| 121 | 4 | `Need withOnSite(String moreReading, int units, double months, int newLevel)` _(in CityNeeds.Need)_ |  |
| 132 | 6 | **type** `public interface Words` | How the interface writes a figure, passed in so the readings are its own: a headcount, an amount of money in the model's thousands, a short number for a narrow cell, and a build time. |
| 133 | 1 | `String people(double count)` _(in CityNeeds.Words)_ |  |
| 134 | 1 | `String money(double thousands)` _(in CityNeeds.Words)_ |  |
| 135 | 1 | `String shortNumber(double value)` _(in CityNeeds.Words)_ |  |
| 136 | 1 | `String monthsWait(double months)` _(in CityNeeds.Words)_ |  |

### THE LINES. Per condition, not one rule, because the same percentage (lines 155-240)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 4 | `public static int serviceLevel(double share)` | A debt-service share's band (0.7.32): 0 comfortable, 1 felt, 2 constrained; a share that is not a number is comfortable. |

### SERVED (0.7.41): ONE RULE FOR EVERY SERVICE GAUGE. Jerus, playing (lines 241-343)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 4 | **type** `public record Served(double share, int level, String word)` | One gauge, read the one way (0.7.41): its share served - supply over demand, unclamped, +∞ when nothing is asked of it - its verdict's level (0 green, 1 amber, 2 red; -1 none, for a gauge no line judges) and its word ... |
| 277 | 1 | `public boolean enough()` _(in CityNeeds.Served)_ | Green: 100% or more and off NEEDS YOU's list. |
| 281 | 4 | `public static double servedShare(double supply, double demand)` | Supply over demand, unclamped (0.7.41): nothing asked of it is all of it met, +∞; nothing supplying a demand is nothing, 0. |
| 291 | 5 | `public static String servedPct(double share)` | A served share as a whole per cent, "62%", or a dash for one that is not a finite number (nothing asked of it). |
| 303 | 5 | `public static Served served(double share, double offList, double red)` | The verdict on a served share against two lines in served terms (0.7.41): green past `offList` (strictly, as NEEDS YOU lists a row at its line) and at 100% or more; red at or under `red`; amber between, "short" under ... |
| 310 | 3 | `public static Served unjudged(double share)` | ...and a share no line judges (transit; a school fewer than a class would come to): no colour, no word. |
| 320 | 17 | `public static double[] servedLines(Kind kind, CareType care)` | A served kind's two lines in served terms (0.7.41), {off the list past this, red at or under this} - NEEDS YOU's own lines turned over where its figure was a load (a network, the road) or a crowd (who would come over ... |
| 339 | 4 | `public static Served verdict(Kind kind, CareType care, double share)` | The one verdict on a served share of one of NEEDS YOU's served kinds (0.7.41); null for a kind that is not a supply against a demand. |

### THE LIST (lines 344-611)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 190 | `public static List<Need> measure(Game game, Words w)` | Everything with a lever, measured against its own line, in the order it is read - the fine ones too, because "next to watch" on a healthy city is the nearest of them. |
| 559 | 8 | `public static Need ground(Game game, Words w)` | The GROUND row on its own (0.7.26), as measure() lists it: what the land office's GROUND FREE, Build's LAND FREE and the left panel's land colour themselves by, so the three and NEEDS YOU agree - none of them reads th... |
| 574 | 6 | `public static List<Need> biting(List<Need> all)` | NEEDS YOU: what is near a line or past one, red above yellow - and inside a tier the order they were measured in, which is the order a city is built. |
| 582 | 3 | `public static List<Need> needsYou(Game game)` | NEEDS YOU for this city, in the panel's order. |
| 591 | 4 | `public static Need worst(List<Need> all, Go go)` | The worst need of those a screen answers: the highest level, and of equals the first in NEEDS YOU's order - the row the panel would list first. |
| 603 | 8 | `public static Need worstUnlisted(List<Need> all, Go go)` | ...and of the served rows NEEDS YOU does NOT list, the one that is still not enough (0.7.41): general care at 90% is past its line, so not listed, and short of 100% - the worst verdict, of equals the first in order. |

### THE PIECES (lines 612-811)

| line | len | member | says |
|---:|---:|---|---|
| 617 | 5 | `public static double careCover(Game game, CareType care, PopulationCohorts cohorts, double[] staffing)` | How much of the people who need a kind of care the staffed beds could take: the panel's coverage, Game.careCoverage()'s. |
| 624 | 3 | `public static double careServed(Game game, CareType care, PopulationCohorts cohorts, double[] staffing)` | ...the same places over the same people, unclamped: what the care rows read as served (0.7.41), and Build's care rings. |
| 629 | 3 | `static double careHave(Game game, CareType care, double[] staffing)` | The staffed places of a kind of care: what its row carries as its supply (0.7.41). |
| 634 | 5 | `public static double taxRaised(Game game)` | What the city raised in tax last month: profit, sales, wages and property (the Policy tab's TAX A MONTH). |
| 641 | 9 | `public static int pinnedBands(Game game)` | Wage bands pinned to the minimum wage with people spare in them. |
| 657 | 4 | `public static int level(double value, double yellow, double red, boolean higherWorse)` | A figure's level against two lines, as a row is struck - over() when higher is worse, under() when lower is (0.7.28): for a screen that colours a figure no row lists (the Services screen's OFF SICK) by the same rule, ... |
| 663 | 7 | `static void over(List<Need> out, String label, String reading, double value, double yellow, double red, Kind kind, Go go, CareT...` | Higher is worse. |
| 672 | 7 | `static void under(List<Need> out, String label, String reading, double value, double yellow, double red, Kind kind, Go go, Care...` | Lower is worse. |
| 681 | 5 | `static void flag(List<Need> out, String label, String reading, boolean bad, boolean severe, Kind kind, Go go, double value)` | A thing that is simply true or not. |
| 695 | 16 | `static void onTheWay(Game game, Words w, List<Need> out, int at, java.util.function.Predicate<BuildingsTemplate> serves)` | ...AND WHAT IS ALREADY ON THE WAY (0.7.20). |
| 713 | 4 | `static void onTheWay(Game game, Words w, List<Need> out, java.util.function.Predicate<BuildingsTemplate> serves)` | The same, for the need just measured. |
| 729 | 19 | `static void network(List<Need> out, String label, Kind kind, double demand, double supply, double ratio)` | One network: how much of its capacity is spoken for, and whether it is still meeting demand. |
| 756 | 18 | `static void seatsWanted(Game game, Words w, List<Need> out)` | SEATS AGAINST WHO WOULD COME, for the schools above the basic ladder: what the schools hold, and the student body this city would sustain if seats were free - intake demand times the course - so a bigger second number... |
| 776 | 7 | `public static double wouldCome(Game game, EducationType type)` | The student body a school above the ladder would hold if seats were free: eligible x willing x the enrolment rate, times the course. |
| 792 | 19 | `static void fallsDue(Game game, Words w, List<Need> out)` | FALLS DUE (0.7.24). |

