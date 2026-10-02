# BuildAdviceCheck.java - 664 lines · 28 methods · 0 constants · harnesses

`ham/citybuildersim/BuildAdviceCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
>   3. THE COUNT AND THE PRICE: the count is the least that takes the need
>      off the list after what is on site, at the staffed capacity the model
>      counts; the price is Game.quoteBuild() for it, to the bit; and with
>      the cash short the count is the most it affords, the next suggestion
>      capped by what the one before left, and with no cash at all the full
>      count marked as needing credit; and the building fits the land free,
>      or none that closes the need does.
>   4. BEFORE AND AFTER, BY THE MODEL: in a twin of the city, the order and
>      what is on site built standing and the month's own services pass run
>      on it, every suggestion's figure is the one the model reads - and the
>      same for an order of every measure a city category opens on.
>   5. THE STAFFING WEIGHTING: in a city short of one job type, of two
>      buildings alike in everything but how many of those posts they have,
>      the advice takes the one with fewer.
>   6. NOTHING NEEDED, NOTHING SUGGESTED: a city NEEDS YOU lists no
>      city-built need for gets no suggestion.
> 
> Every fixture causes its condition.

**Uses:** [BuildAdvice](BuildAdvice.md) (99), [Game](Game.md) (31), [BuildingsTemplate](BuildingsTemplate.md) (25), [CityNeeds](CityNeeds.md) (24), [BuildingType](BuildingType.md) (17), [Crime](Crime.md) (13), [SafetyType](SafetyType.md) (9), [JobType](JobType.md) (9), [GameFiles](GameFiles.md) (5), [CareType](CareType.md) (5), [BuildingManager](BuildingManager.md) (4), [Founding](Founding.md) (3), [Sector](Sector.md) (2), [Formats](Formats.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [Healthcare](Healthcare.md) (2), [ServicesManager](ServicesManager.md) (1)

## Sections

| line | section |
|---:|---|
| 201 | 1. THE CATEGORIES |
| 256 | 2. NEEDS YOU'S NEEDS, IN ITS ORDER |
| 308 | 3. THE COUNT AND THE PRICE |
| 431 | 4. BEFORE AND AFTER, BY THE MODEL |
| 577 | 5. THE STAFFING WEIGHTING |
| 647 | 6. NOTHING NEEDED |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static int fails` |  |
| 59 | `static PrintStream out` |  |
| 60 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 609 | **type** `public class BuildAdviceCheck` | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city - and the categories it is filed under. |
| 62 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 67 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 73 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 79 | 5 | `static void quietly(Runnable r)` |  |
| 85 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 107 | 25 | `static Game shortCity(Path root, String name, boolean onSiteClinic)` | A played city short of the city's works: about four thousand people in nine hundred houses on one paved road, no clinic or daycare (the founding's own care only), a school of each basic stage, a college, a university ... |
| 140 | 22 | `static Game servedCity(Path root, String name)` | A smaller city well served: every measure NEEDS YOU watches for a city-built building past its line - care of every kind, a school of every basic stage, a college, a university and a medical school with seats for who ... |
| 163 | 23 | `public static void main(String[] args) throws Exception` |  |
| 188 | 12 | `static void printCity(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice)` | The city as the advice read it, for the record. |

### 1. THE CATEGORIES (lines 201-255)

| line | len | member | says |
|---:|---:|---|---|
| 203 | 40 | `static void theCategories(Path root)` |  |
| 244 | 11 | `static boolean checkInvestors(List<BuildAdvice.Category> cats)` |  |

### 2. NEEDS YOU'S NEEDS, IN ITS ORDER (lines 256-307)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 40 | `static void needsYousOrder(Game g, List<CityNeeds.Need> all, List<BuildAdvice.Suggestion> advice)` |  |
| 300 | 7 | `static boolean onSiteAnswers(Game g, List<CityNeeds.Need> biting)` | Whether any biting city-built need has something on site that serves it. |

### 3. THE COUNT AND THE PRICE (lines 308-430)

| line | len | member | says |
|---:|---:|---|---|
| 310 | 32 | `static void theCountAndThePrice(Game g, List<BuildAdvice.Suggestion> advice)` |  |
| 344 | 13 | `static boolean fitsOrNone(Game g, BuildAdvice.Suggestion s)` | The land rule: the chosen building's whole count fits the free ground, unless none that closes it would. |
| 362 | 29 | `static double unitByTheModel(Game twin, BuildAdvice.Measure m, BuildingsTemplate t)` | One building's staffed capacity as the model counts it: the model's getter with one more of it standing in a twin, less the getter before. |
| 398 | 32 | `static void theCashCap(Path root)` | The cash cap: with cash for the suggestions before the largest one and half of it, the largest is cut to the most the cash left affords and the ones before it are whole; with none, every one is the full count, marked ... |

### 4. BEFORE AND AFTER, BY THE MODEL (lines 431-576)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 5 | `static Game twin(Game g)` | A twin of the city: the same save, loaded. |
| 445 | 11 | `static double builtAndRead(Game twin, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> add)` | The order and what is on site built standing in a twin, the month's own services pass run on it (SimulationEngine's last steps), and the figure read the way the model reads it. |
| 458 | 60 | `static double readByTheModel(Game twin, BuildAdvice.Measure m)` | A measure's figure, read off the model: the handlers' own loads, the education month, the crime month. |
| 520 | 6 | `static Crime crimeMonth(Game twin, double officers, double cells, int months)` | The city's crime, restored into a fresh Crime and played `months` months on these officers and cells. |
| 528 | 8 | `static void advance(Game twin, Crime month, double officers, double cells)` | One month of crime on the city's own causes - the adults in each, from the pressure the city's month left. |
| 537 | 12 | `static void beforeAndAfter(Path root, Game g, List<BuildAdvice.Suggestion> advice)` |  |
| 551 | 25 | `static void everyMeasure(Path root, Game g)` | Every measure a city category opens on: an order of the first building that serves it, built in a twin. |

### 5. THE STAFFING WEIGHTING (lines 577-646)

| line | len | member | says |
|---:|---:|---|---|
| 584 | 43 | `static void theStaffingWeighting(Path root)` | Two clinics alike in everything - price, capacity, land, posts in all - but how many of the scarcest of the clinic's own posts they need: the advice takes the one with fewer. |
| 628 | 12 | `static BuildingsTemplate clone(BuildingsTemplate t, String name, JobType a, int na, JobType b, int nb)` |  |
| 641 | 5 | `static int posts(BuildingsTemplate t)` |  |

### 6. NOTHING NEEDED (lines 647-664)

| line | len | member | says |
|---:|---:|---|---|
| 649 | 15 | `static void nothingNeeded(Path root)` |  |

