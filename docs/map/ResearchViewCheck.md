# ResearchViewCheck.java - 1,378 lines · 42 methods · 4 constants · harnesses

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
> 
> Policy > Research's Overview and Funding pages (R3b, slice A: the reads their screen draws, which sections 11 to 15 hold to the model's own, in a played
> 1900 town with two Universities, an Institute of Technology and a Medical School, and grants that bought posts):
> 
>  11. THE OVERVIEW. Each field's row is Research's reads: its researchers and the labs', the idle among them, the problems in reach and what they touch, what it
>      found here and had come from abroad, the schools that host it; the month's bill and the prizes paid; the researchers to each 1,000 at work; the words.
>  12. THE FUNDING CARD. Each field's grant, prize, posts (the grant's and the pot's), room, the grant that fills it, the researchers, the bill and the part
>      of the grant unspent are the model's; the labs standing and on site; the least prize that brings a lab, from both sides; what a staged grant buys.
>  13. THE PREVIEW THROUGH THE GAME. The as-is and staged previews are ResearchPreview's over the model's own researchers, cached, and a staged dial equal
>      to the one in force is the answer already kept.
>  14. R5'S GATE REVIEW. The found-here and abroad counts laid on History's months, what is still to come, and the bars' shares.
>  15. A CITY FOUNDED THE DEFAULT WAY. Nothing in reach, nothing to find, no lab to come, whatever the catalogue's dates say of the month.

**Uses:** [ResearchView](ResearchView.md) (319), [Research](Research.md) (48), [HistorySave](HistorySave.md) (48), [ResearchPreview](ResearchPreview.md) (41), [Game](Game.md) (33), [DecisionLog](DecisionLog.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (18), [FittingCheck](FittingCheck.md) (13), [ChartModel](ChartModel.md) (11), [YearBook](YearBook.md) (10), [BuildingsStacks](BuildingsStacks.md) (8), [ResearchCatalog](ResearchCatalog.md) (6), [EquipmentFit](EquipmentFit.md) (6), [DiscoveryCheck](DiscoveryCheck.md) (5), [Founding](Founding.md) (5), [EquipmentCheck](EquipmentCheck.md) (4), [PrizeCheck](PrizeCheck.md) (4), [MaterialsCheck](MaterialsCheck.md) (3), [Equipment](Equipment.md) (2), [ResearchPreviewCheck](ResearchPreviewCheck.md) (2), [Pieces](Pieces.md) (2), [Recipe](Recipe.md) (1), [BuildingType](BuildingType.md) (1), [PolicyDates](PolicyDates.md) (1), [EconomyManager](EconomyManager.md) (1), [GameFiles](GameFiles.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (1):** [KeptCheck](KeptCheck.md)

## Sections

| line | section |
|---:|---|
| 136 | researchTown(): the 1900 city the research screens' checks share |
| 178 | main |
| 207 | 1. a city founded the default way |
| 255 | 2. a 1900 city at its founding |
| 313 | 3. the timeline |
| 403 | 4. the cumulative counts |
| 441 | 5. the flags |
| 508 | 6. the equipment |
| 587 | 7. the recipes in use |
| 652 | 8. where a building came from |
| 725 | 9. the series |
| 924 | 10. researchTown() |
| 970 | R3b: Policy > Research's Overview and Funding pages (sections 11 to 15) |
| 1015 | 11. the Overview |
| 1104 | 12. the Funding card |
| 1218 | 13. the preview through the game |
| 1288 | 14. R5's gate review |
| 1347 | 15. a city founded the default way |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 109 | `ResearchViewCheck.CAT` | `ResearchCatalog.get()` |  |
| 269 | `ResearchViewCheck.NOW_SECTION_TWO` | `50` | A month well past the first discoveries, for the cores of section 2. |
| 316 | `ResearchViewCheck.FINDS` | `Map.of("PE1", 4, "PE14", 150, "PE2", 20, "PE3", ResearchView.FIRST_MONTH, "PL...` | The finds of section 3's research: PE1 and PE14 before history's date, PE2 after it, PE3 at the founding month and PL5 the month after it. |
| 324 | `ResearchViewCheck.NOW` | `400` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `static int fails` |  |
| 71 | `static int passes` |  |
| 88 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1311 | **type** `public class ResearchViewCheck` | What the research screens read (R5, slice A: ResearchView, three series in HistorySave, ChartModel.flags(log, more), Recipes.chosen()). |
| 73 | 4 | `static void check(String label, boolean ok)` |  |
| 79 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 84 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 90 | 5 | `static void quietly(Runnable r)` |  |
| 97 | 6 | `static void ensureQuiet()` | The streams the borrowed fixtures (FittingCheck.village(), DiscoveryCheck.cityWithSchools()) are quiet through, set once and never over another harness's. |
| 105 | 3 | `static void play(Game g, int months)` | One press of the clock, quietly (the player's button: the auto-builder's pass first). |
| 111 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 116 | 4 | `static void setFinds(Game g, Map<String, Integer> finds)` | Puts a city's finds back as given (and keeps its grants, posts and piles): the cheapest honest way to have a find, as DiscoveryCheck reaches them. |
| 122 | 3 | `static int index(Entry e)` | The index of an entry in the catalogue's order. |
| 130 | 5 | `static Game foundedVillage(String label)` | A village of FittingCheck.village() founded the 1900 way. |

### researchTown(): the 1900 city the research screens' checks share (lines 136-177)

| line | len | member | says |
|---:|---:|---|---|
| 163 | 14 | `static Game researchTown() throws Exception` | A city founded the 1900 way, played 13 months (NEW_MONTHS + 1) from the founding to month 14, and ready to be read by the research screens. |

### main (lines 178-206)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 24 | `public static void main(String[] args) throws Exception` |  |

### 1. a city founded the default way (lines 207-254)

| line | len | member | says |
|---:|---:|---|---|
| 209 | 45 | `static void sectionOne()` |  |

### 2. a 1900 city at its founding (lines 255-312)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 4 | `static String entryText(String code, int month)` | One entry of a catalogue text for the harness's own catalogue: January or the given month of 1900. |
| 264 | 3 | `static ResearchCatalog threeMonths()` | A catalogue of the harness's own: T1, T2, T3 dated 1900's first three months (history's months 1, 2, 3). |
| 271 | 41 | `static void sectionTwo()` |  |

### 3. the timeline (lines 313-402)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 5 | `static Research someFinds()` |  |
| 326 | 76 | `static void sectionThree()` |  |

### 4. the cumulative counts (lines 403-440)

| line | len | member | says |
|---:|---:|---|---|
| 405 | 35 | `static void sectionFour()` |  |

### 5. the flags (lines 441-507)

| line | len | member | says |
|---:|---:|---|---|
| 443 | 58 | `static void sectionFive(Game town)` |  |
| 502 | 5 | `static List<String> labels(ChartModel.Flag f)` |  |

### 6. the equipment (lines 508-586)

| line | len | member | says |
|---:|---:|---|---|
| 510 | 76 | `static void sectionSix(Game town)` |  |

### 7. the recipes in use (lines 587-651)

| line | len | member | says |
|---:|---:|---|---|
| 589 | 62 | `static void sectionSeven(Game town)` |  |

### 8. where a building came from (lines 652-724)

| line | len | member | says |
|---:|---:|---|---|
| 654 | 70 | `static void sectionEight(Game town)` |  |

### 9. the series (lines 725-923)

| line | len | member | says |
|---:|---:|---|---|
| 728 | 6 | `static double[] tail(HistorySave h, String name, int n)` | The history's last `n` figures of a series. |
| 735 | 1 | `static double round2(double v)` |  |
| 737 | 1 | `static double round4(double v)` |  |
| 740 | 12 | `static double shareFromStacks(Game g)` | The share fitted as the harness works it out from the stacks (not from ResearchView's rows), rounded as the history rounds it; NaN with no piece known on a stack that stands. |
| 753 | 94 | `static void sectionNine() throws Exception` |  |
| 849 | 5 | `static boolean levelsOf(Game g)` | Whether the three researchers' kinds are levels, with notes. |
| 856 | 12 | `static boolean sameSeries(HistorySave a, HistorySave b)` | Whether two histories hold the same figures in the research's series. |
| 870 | 53 | `static void lateSeries() throws Exception` | A 1900 city of roads and houses only: no equipment stands until a bakery is built, then it does, then it is pulled down. |

### 10. researchTown() (lines 924-969)

| line | len | member | says |
|---:|---:|---|---|
| 926 | 43 | `static void sectionTen(Game town, long builtMillis)` |  |

### R3b: Policy > Research's Overview and Funding pages (sections 11 to 15) (lines 970-1014)

| line | len | member | says |
|---:|---:|---|---|
| 979 | 19 | `static Game fundedTown() throws Exception` | researchTown() with schools and grants: two Universities, an Institute of Technology and a Medical School stand, the hands are handed, and grants in engineering (30.5 posts' worth), medicine (12.5) and business (4.5) ... |
| 1000 | 5 | `static List<String> touchedBy(List<Entry> open)` | The distinct things a list of open problems touch, in order, as the harness counts them (a set of the entries' own texts). |
| 1007 | 7 | `static boolean sameResult(ResearchPreview.Result a, ResearchPreview.Result b)` | Whether two previews are exactly the same answer. |

### 11. the Overview (lines 1015-1103)

| line | len | member | says |
|---:|---:|---|---|
| 1017 | 86 | `static void sectionEleven(Game g)` |  |

### 12. the Funding card (lines 1104-1217)

| line | len | member | says |
|---:|---:|---|---|
| 1106 | 111 | `static void sectionTwelve(Game g)` |  |

### 13. the preview through the game (lines 1218-1287)

| line | len | member | says |
|---:|---:|---|---|
| 1221 | 11 | `static ResearchPreview.Result modelsPreview(Game g, Map<Field, Double> researchers, Map<Field, Double> prize)` | The preview the way the model is asked it for the city as it stands, with some dials replaced: the maps the harness builds from the model's own reads. |
| 1233 | 54 | `static void sectionThirteen(Game g)` |  |

### 14. R5's gate review (lines 1288-1346)

| line | len | member | says |
|---:|---:|---|---|
| 1290 | 56 | `static void sectionFourteen(Game g)` |  |

### 15. a city founded the default way (lines 1347-1378)

| line | len | member | says |
|---:|---:|---|---|
| 1349 | 29 | `static void sectionFifteen() throws Exception` |  |

