# ResearchViewCheck.java - 947 lines · 32 methods · 4 constants · harnesses

`ham/citybuildersim/ResearchViewCheck.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> What the research screens read (R5, slice A: ResearchView, three series in HistorySave, ChartModel.flags(log, more), Recipes.chosen()).
> 
> WHY. The screens draw only what the model hands them, and a wrong month in the timeline, a piece shown before it is known, or a NEW mark one month
> too long would sit in a screen where no assertion in the model would see it. So every read is held to the model's own constants and functions
> (FIRST_MONTH, NEW_MONTHS, Entry.worldMonth(), EquipmentFit.verdict(), Recipes.chosen()), each threshold is caused from both sides, and a city
> founded the default way is shown to have nothing new on any of them (research runs only in a city founded the 1900 way, so every played city keeps
> its screens). The cores that take a catalogue, a research and a month are driven at any month with a research of their own, and the Game-level reads
> on cities the harness plays; researchTown() is the one fixture the next slices' screens share.
> 
> What this has to prove:
> 
>   1. A CITY FOUNDED THE DEFAULT WAY. The timeline, the equipment, the recipes in use, the new templates and the flags are all empty, every origin is null
>      and no template is new even where a find sits in its research, knownAtFounding() is the catalogue's size, and the history has none of the three
>      series (the share of the equipment fitted reads NaN in every month).
>   2. A 1900 CITY AT ITS FOUNDING. An empty timeline; knownAtFounding() counts exactly the entries history dates at FIRST_MONTH or before, and an entry
>      of the month after is a discovery, from both sides of FIRST_MONTH; its equipment stands, every piece unfitted.
>   3. THE TIMELINE. A find here is HERE at the month it was found and years ahead of the world by (worldMonth - found) / 12; every other entry
>      history dates after FIRST_MONTH and by now is ABROAD at its date; none after now; a find after history's date is the world's; a find at the founding
>      month is no discovery; ordered by month, then the catalogue's order; byField partitions it.
>   4. THE CUMULATIVE COUNTS. Monotone; the last entries are count(HERE) and count(ABROAD); entry 0 counts the first month itself and every month before it; the
>      length rule, the empty case included.
>   5. THE FLAGS. One entry a discovery, of its kind, at its month, in the words of its source; not in the decision log; and ChartModel.flags(log, more) puts a
>      month's decisions and discoveries in one flag, decisions first, and keeps other months apart.
>   6. THE EQUIPMENT. fitted + unfitted = buildings; the verdict is the Fit pill's; a piece not known at the month is absent and appears once it is; what is
>      on site is counted; the sector filter keeps only its sector's templates; the discovery that made a piece known is on its row.
>   7. THE RECIPES IN USE. Each listed recipe is non-default and known; running is Recipes.chosen() over the parts' sizes (a template with no piece is all
>      one part); a recipe known and not chosen is listed with nothing running.
>   8. WHERE A BUILDING CAME FROM. A template opened by a code found here is HERE at the month found, new for NEW_MONTHS - 1 months after it and not at
>      NEW_MONTHS; from abroad the same; one known at the founding, a code not in the catalogue and a year not yet come have none; a year is ABROAD at its
>      January; the four labs are labs and a school is not.
>   9. THE SERIES. A 1900 city with grants and a prize records each field's researchers, the month's bill and prize and the share of the equipment fitted,
>      every month and as the model says it, the same length as the axis; a late-beginning series begins at its first month and reads NaN before; all
>      survive a save and load; the year book rules each; a reform scales the spend and nothing else; a cut drops a series that began after it.
>  10. researchTown(). The fixture's own guarantees, one at a time, and what it cost to build.

**Uses:** [ResearchView](ResearchView.md) (199), [HistorySave](HistorySave.md) (47), [Game](Game.md) (24), [DecisionLog](DecisionLog.md) (24), [Research](Research.md) (18), [BuildingsTemplate](BuildingsTemplate.md) (17), [FittingCheck](FittingCheck.md) (12), [ChartModel](ChartModel.md) (11), [YearBook](YearBook.md) (10), [BuildingsStacks](BuildingsStacks.md) (7), [ResearchCatalog](ResearchCatalog.md) (6), [EquipmentFit](EquipmentFit.md) (6), [EquipmentCheck](EquipmentCheck.md) (4), [Founding](Founding.md) (4), [PrizeCheck](PrizeCheck.md) (4), [DiscoveryCheck](DiscoveryCheck.md) (3), [Equipment](Equipment.md) (2), [Recipe](Recipe.md) (1), [BuildingType](BuildingType.md) (1), [PolicyDates](PolicyDates.md) (1), [EconomyManager](EconomyManager.md) (1), [GameFiles](GameFiles.md) (1), [BuildingManager](BuildingManager.md) (1)

## Sections

| line | section |
|---:|---|
| 120 | researchTown(): the 1900 city the research screens' checks share |
| 162 | main |
| 185 | 1. a city founded the default way |
| 233 | 2. a 1900 city at its founding |
| 291 | 3. the timeline |
| 381 | 4. the cumulative counts |
| 419 | 5. the flags |
| 486 | 6. the equipment |
| 565 | 7. the recipes in use |
| 630 | 8. where a building came from |
| 703 | 9. the series |
| 902 | 10. researchTown() |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 93 | `ResearchViewCheck.CAT` | `ResearchCatalog.get()` |  |
| 247 | `ResearchViewCheck.NOW_SECTION_TWO` | `50` | A month well past the first discoveries, for the cores of section 2. |
| 294 | `ResearchViewCheck.FINDS` | `Map.of("PE1", 4, "PE14", 150, "PE2", 20, "PE3", ResearchView.FIRST_MONTH, "PL...` | The finds of section 3's research: PE1 and PE14 before history's date, PE2 after it, PE3 at the founding month and PL5 the month after it. |
| 302 | `ResearchViewCheck.NOW` | `400` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static int fails` |  |
| 59 | `static int passes` |  |
| 72 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 892 | **type** `public class ResearchViewCheck` | What the research screens read (R5, slice A: ResearchView, three series in HistorySave, ChartModel.flags(log, more), Recipes.chosen()). |
| 61 | 4 | `static void check(String label, boolean ok)` |  |
| 67 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 74 | 5 | `static void quietly(Runnable r)` |  |
| 81 | 6 | `static void ensureQuiet()` | The streams the borrowed fixtures (FittingCheck.village(), DiscoveryCheck.cityWithSchools()) are quiet through, set once and never over another harness's. |
| 89 | 3 | `static void play(Game g, int months)` | One press of the clock, quietly (the player's button: the auto-builder's pass first). |
| 95 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 100 | 4 | `static void setFinds(Game g, Map<String, Integer> finds)` | Puts a city's finds back as given (and keeps its grants, posts and piles): the cheapest honest way to have a find, as DiscoveryCheck reaches them. |
| 106 | 3 | `static int index(Entry e)` | The index of an entry in the catalogue's order. |
| 114 | 5 | `static Game foundedVillage(String label)` | A village of FittingCheck.village() founded the 1900 way. |

### researchTown(): the 1900 city the research screens' checks share (lines 120-161)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 14 | `static Game researchTown() throws Exception` | A city founded the 1900 way, played 13 months (NEW_MONTHS + 1) from the founding to month 14, and ready to be read by the research screens. |

### main (lines 162-184)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. a city founded the default way (lines 185-232)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 45 | `static void sectionOne()` |  |

### 2. a 1900 city at its founding (lines 233-290)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 4 | `static String entryText(String code, int month)` | One entry of a catalogue text for the harness's own catalogue: January or the given month of 1900. |
| 242 | 3 | `static ResearchCatalog threeMonths()` | A catalogue of the harness's own: T1, T2, T3 dated 1900's first three months (history's months 1, 2, 3). |
| 249 | 41 | `static void sectionTwo()` |  |

### 3. the timeline (lines 291-380)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 5 | `static Research someFinds()` |  |
| 304 | 76 | `static void sectionThree()` |  |

### 4. the cumulative counts (lines 381-418)

| line | len | member | says |
|---:|---:|---|---|
| 383 | 35 | `static void sectionFour()` |  |

### 5. the flags (lines 419-485)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 58 | `static void sectionFive(Game town)` |  |
| 480 | 5 | `static List<String> labels(ChartModel.Flag f)` |  |

### 6. the equipment (lines 486-564)

| line | len | member | says |
|---:|---:|---|---|
| 488 | 76 | `static void sectionSix(Game town)` |  |

### 7. the recipes in use (lines 565-629)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 62 | `static void sectionSeven(Game town)` |  |

### 8. where a building came from (lines 630-702)

| line | len | member | says |
|---:|---:|---|---|
| 632 | 70 | `static void sectionEight(Game town)` |  |

### 9. the series (lines 703-901)

| line | len | member | says |
|---:|---:|---|---|
| 706 | 6 | `static double[] tail(HistorySave h, String name, int n)` | The history's last `n` figures of a series. |
| 713 | 1 | `static double round2(double v)` |  |
| 715 | 1 | `static double round4(double v)` |  |
| 718 | 12 | `static double shareFromStacks(Game g)` | The share fitted as the harness works it out from the stacks (not from ResearchView's rows), rounded as the history rounds it; NaN with no piece known on a stack that stands. |
| 731 | 94 | `static void sectionNine() throws Exception` |  |
| 827 | 5 | `static boolean levelsOf(Game g)` | Whether the three researchers' kinds are levels, with notes. |
| 834 | 12 | `static boolean sameSeries(HistorySave a, HistorySave b)` | Whether two histories hold the same figures in the research's series. |
| 848 | 53 | `static void lateSeries() throws Exception` | A 1900 city of roads and houses only: no equipment stands until a bakery is built, then it does, then it is pulled down. |

### 10. researchTown() (lines 902-947)

| line | len | member | says |
|---:|---:|---|---|
| 904 | 43 | `static void sectionTen(Game town, long builtMillis)` |  |

