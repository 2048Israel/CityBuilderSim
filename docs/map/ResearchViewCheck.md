# ResearchViewCheck.java - 1,713 lines · 48 methods · 4 constants · harnesses

`ham/citybuildersim/ResearchViewCheck.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

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
> 
> The screens' own reads (R3b, slice B: what the Overview's four figures, the Funding dials, a field card's rows and a school's research line are drawn from, so that
> the screens add up nothing):
> 
>  16. THE FIGURES AND THE DIALS. The researchers at work and the problems in reach added up; the words of the problems by field; the room in whole posts; a grant
>      dial in steps of a post, past the room and the grant in force; a prize dial to a round number past 1,000 researcher-years, twice the least prize that
>      brings a lab and the prize in force; a lab's posts.
>  17. THE CARD. Nothing staged is one figure on both sides; a staged grant is atGrant's posts, researchers, bill and unspent, the preview the staged one's; a
>      grant past the room buys the room and says it is full; a staged prize brings the labs the staged preview wants, at a lab's posts each.
>  18. THE SCHOOLS AND THE BUDGET. The kinds of school stand up the game's own staff and room; a kind has its share of each field's posts; a kind that hosts none, or of
>      which none stands, carries nothing; THE BUDGET moves by the staged grants' posts and not by a prize.
>  19. A LAB STANDING. The card counts the lab and its researchers with the field's, and the same lab is on both sides of a card that moves a grant.
>  20. A DEFAULT CITY'S FACULTY (R3b's gate review). A city founded the default way with a University and no grant: its researchers are the faculty's
>      own and no post is held (the Overview's posts), and the month's counts survive a save and a load though the research has nothing of its own.

**Uses:** [ResearchView](ResearchView.md) (522), [Research](Research.md) (59), [HistorySave](HistorySave.md) (48), [ResearchPreview](ResearchPreview.md) (45), [Game](Game.md) (40), [DecisionLog](DecisionLog.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (18), [EducationType](EducationType.md) (18), [FittingCheck](FittingCheck.md) (13), [ChartModel](ChartModel.md) (11), [YearBook](YearBook.md) (10), [BuildingsStacks](BuildingsStacks.md) (8), [ResearchCatalog](ResearchCatalog.md) (6), [EquipmentFit](EquipmentFit.md) (6), [EquipmentCheck](EquipmentCheck.md) (5), [DiscoveryCheck](DiscoveryCheck.md) (5), [Founding](Founding.md) (5), [PrizeCheck](PrizeCheck.md) (4), [MaterialsCheck](MaterialsCheck.md) (4), [Pieces](Pieces.md) (3), [Equipment](Equipment.md) (2), [GameFiles](GameFiles.md) (2), [ResearchPreviewCheck](ResearchPreviewCheck.md) (2), [Recipe](Recipe.md) (1), [BuildingType](BuildingType.md) (1), [PolicyDates](PolicyDates.md) (1), [EconomyManager](EconomyManager.md) (1), [BuildingManager](BuildingManager.md) (1), [KeptCheck](KeptCheck.md) (1)

**Used by (1):** [KeptCheck](KeptCheck.md)

## Sections

| line | section |
|---:|---|
| 150 | researchTown(): the 1900 city the research screens' checks share |
| 192 | main |
| 226 | 1. a city founded the default way |
| 274 | 2. a 1900 city at its founding |
| 332 | 3. the timeline |
| 422 | 4. the cumulative counts |
| 460 | 5. the flags |
| 527 | 6. the equipment |
| 606 | 7. the recipes in use |
| 671 | 8. where a building came from |
| 744 | 9. the series |
| 943 | 10. researchTown() |
| 989 | R3b: Policy > Research's Overview and Funding pages (sections 11 to 15) |
| 1034 | 11. the Overview |
| 1123 | 12. the Funding card |
| 1237 | 13. the preview through the game |
| 1307 | 14. R5's gate review |
| 1366 | 15. a city founded the default way |
| 1398 | R3b, slice B: the reads the Overview and Funding screens draw their figures, dials, rows and school lines from (secti... |
| 1402 | 16. the Overview's figures and the dials' ranges |
| 1505 | 17. the card's rows |
| 1586 | 18. the schools' lines, and THE BUDGET |
| 1648 | 20. a default city's faculty, through a save |
| 1689 | 19. a lab standing |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 123 | `ResearchViewCheck.CAT` | `ResearchCatalog.get()` |  |
| 288 | `ResearchViewCheck.NOW_SECTION_TWO` | `50` | A month well past the first discoveries, for the cores of section 2. |
| 335 | `ResearchViewCheck.FINDS` | `Map.of("PE1", 4, "PE14", 150, "PE2", 20, "PE3", ResearchView.FIRST_MONTH, "PL...` | The finds of section 3's research: PE1 and PE14 before history's date, PE2 after it, PE3 at the founding month and PL5 the month after it. |
| 343 | `ResearchViewCheck.NOW` | `400` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `static int fails` |  |
| 85 | `static int passes` |  |
| 102 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 1632 | **type** `public class ResearchViewCheck` | What the research screens read (R5, slice A: ResearchView, three series in HistorySave, ChartModel.flags(log, more), Recipes.chosen()). |
| 87 | 4 | `static void check(String label, boolean ok)` |  |
| 93 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 98 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 104 | 5 | `static void quietly(Runnable r)` |  |
| 111 | 6 | `static void ensureQuiet()` | The streams the borrowed fixtures (FittingCheck.village(), DiscoveryCheck.cityWithSchools()) are quiet through, set once and never over another harness's. |
| 119 | 3 | `static void play(Game g, int months)` | One press of the clock, quietly (the player's button: the auto-builder's pass first). |
| 125 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 130 | 4 | `static void setFinds(Game g, Map<String, Integer> finds)` | Puts a city's finds back as given (and keeps its grants, posts and piles): the cheapest honest way to have a find, as DiscoveryCheck reaches them. |
| 136 | 3 | `static int index(Entry e)` | The index of an entry in the catalogue's order. |
| 144 | 5 | `static Game foundedVillage(String label)` | A village of FittingCheck.village() founded the 1900 way. |

### researchTown(): the 1900 city the research screens' checks share (lines 150-191)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 14 | `static Game researchTown() throws Exception` | A city founded the 1900 way, played 13 months (NEW_MONTHS + 1) from the founding to month 14, and ready to be read by the research screens. |

### main (lines 192-225)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 29 | `public static void main(String[] args) throws Exception` |  |

### 1. a city founded the default way (lines 226-273)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 45 | `static void sectionOne()` |  |

### 2. a 1900 city at its founding (lines 274-331)

| line | len | member | says |
|---:|---:|---|---|
| 277 | 4 | `static String entryText(String code, int month)` | One entry of a catalogue text for the harness's own catalogue: January or the given month of 1900. |
| 283 | 3 | `static ResearchCatalog threeMonths()` | A catalogue of the harness's own: T1, T2, T3 dated 1900's first three months (history's months 1, 2, 3). |
| 290 | 41 | `static void sectionTwo()` |  |

### 3. the timeline (lines 332-421)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 5 | `static Research someFinds()` |  |
| 345 | 76 | `static void sectionThree()` |  |

### 4. the cumulative counts (lines 422-459)

| line | len | member | says |
|---:|---:|---|---|
| 424 | 35 | `static void sectionFour()` |  |

### 5. the flags (lines 460-526)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 58 | `static void sectionFive(Game town)` |  |
| 521 | 5 | `static List<String> labels(ChartModel.Flag f)` |  |

### 6. the equipment (lines 527-605)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 76 | `static void sectionSix(Game town)` |  |

### 7. the recipes in use (lines 606-670)

| line | len | member | says |
|---:|---:|---|---|
| 608 | 62 | `static void sectionSeven(Game town)` |  |

### 8. where a building came from (lines 671-743)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 70 | `static void sectionEight(Game town)` |  |

### 9. the series (lines 744-942)

| line | len | member | says |
|---:|---:|---|---|
| 747 | 6 | `static double[] tail(HistorySave h, String name, int n)` | The history's last `n` figures of a series. |
| 754 | 1 | `static double round2(double v)` |  |
| 756 | 1 | `static double round4(double v)` |  |
| 759 | 12 | `static double shareFromStacks(Game g)` | The share fitted as the harness works it out from the stacks (not from ResearchView's rows), rounded as the history rounds it; NaN with no piece known on a stack that stands. |
| 772 | 94 | `static void sectionNine() throws Exception` |  |
| 868 | 5 | `static boolean levelsOf(Game g)` | Whether the three researchers' kinds are levels, with notes. |
| 875 | 12 | `static boolean sameSeries(HistorySave a, HistorySave b)` | Whether two histories hold the same figures in the research's series. |
| 889 | 53 | `static void lateSeries() throws Exception` | A 1900 city of roads and houses only: no equipment stands until a bakery is built, then it does, then it is pulled down. |

### 10. researchTown() (lines 943-988)

| line | len | member | says |
|---:|---:|---|---|
| 945 | 43 | `static void sectionTen(Game town, long builtMillis)` |  |

### R3b: Policy > Research's Overview and Funding pages (sections 11 to 15) (lines 989-1033)

| line | len | member | says |
|---:|---:|---|---|
| 998 | 19 | `static Game fundedTown() throws Exception` | researchTown() with schools and grants: two Universities, an Institute of Technology and a Medical School stand, the hands are handed, and grants in engineering (30.5 posts' worth), medicine (12.5) and business (4.5) ... |
| 1019 | 5 | `static List<String> touchedBy(List<Entry> open)` | The distinct things a list of open problems touch, in order, as the harness counts them (a set of the entries' own texts). |
| 1026 | 7 | `static boolean sameResult(ResearchPreview.Result a, ResearchPreview.Result b)` | Whether two previews are exactly the same answer. |

### 11. the Overview (lines 1034-1122)

| line | len | member | says |
|---:|---:|---|---|
| 1036 | 86 | `static void sectionEleven(Game g)` |  |

### 12. the Funding card (lines 1123-1236)

| line | len | member | says |
|---:|---:|---|---|
| 1125 | 111 | `static void sectionTwelve(Game g)` |  |

### 13. the preview through the game (lines 1237-1306)

| line | len | member | says |
|---:|---:|---|---|
| 1240 | 11 | `static ResearchPreview.Result modelsPreview(Game g, Map<Field, Double> researchers, Map<Field, Double> prize)` | The preview the way the model is asked it for the city as it stands, with some dials replaced: the maps the harness builds from the model's own reads. |
| 1252 | 54 | `static void sectionThirteen(Game g)` |  |

### 14. R5's gate review (lines 1307-1365)

| line | len | member | says |
|---:|---:|---|---|
| 1309 | 56 | `static void sectionFourteen(Game g)` |  |

### 15. a city founded the default way (lines 1366-1397)

| line | len | member | says |
|---:|---:|---|---|
| 1368 | 29 | `static void sectionFifteen() throws Exception` |  |

### R3b, slice B: the reads the Overview and Funding screens draw their figures, dials, rows and school lines from (secti... (lines 1398-1401)

### 16. the Overview's figures and the dials' ranges (lines 1402-1504)

| line | len | member | says |
|---:|---:|---|---|
| 1404 | 100 | `static void sectionSixteen(Game g)` |  |

### 17. the card's rows (lines 1505-1585)

| line | len | member | says |
|---:|---:|---|---|
| 1507 | 78 | `static void sectionSeventeen(Game g)` |  |

### 18. the schools' lines, and THE BUDGET (lines 1586-1647)

| line | len | member | says |
|---:|---:|---|---|
| 1588 | 59 | `static void sectionEighteen(Game g)` |  |

### 20. a default city's faculty, through a save (lines 1648-1688)

| line | len | member | says |
|---:|---:|---|---|
| 1651 | 8 | `static String monthCounts(Research r)` | The month's five counts of a research, field by field, in words a harness can compare. |
| 1660 | 28 | `static void sectionTwenty() throws Exception` |  |

### 19. a lab standing (lines 1689-1713)

| line | len | member | says |
|---:|---:|---|---|
| 1691 | 22 | `static void sectionNineteen(Game g)` |  |

