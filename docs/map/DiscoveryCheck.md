# DiscoveryCheck.java - 1,420 lines · 44 methods · 4 constants · harnesses

`ham/citybuildersim/DiscoveryCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The city finds discoveries of its own, and a discovery found here is known here from the month it was found (R3, slice A: Research, and the
> seam in Recipe, Equipment, BuildingsTemplate, Recipes and EquipmentFit; docs/studies/research-design.md 1.8, 5.1, 5.2, 6.3).
> 
> WHY. Research is a mechanism and not a chance (5.2: "Nothing multiplies a chance"), so every claim about it is a statement about a month: a post
> costs this, a problem is open in that month and not the one before, a pile reached its size in the first month it could. A wrong dial or a step
> out of order moves a find by a year and no assertion elsewhere would see it, and the seam is the part a played city can get wrong silently
> (a recipe known in a month it should not be). So the figures are held to the study's, the rules to the study's model, the step to its
> invariants over whole runs, and the seam to "equal to history's in every city that found nothing".
> 
> What this has to prove:
> 
>   1. THE POST'S COST. Each field's post at today's wages is the study's D$ 18.2k, 23.8k, 12.2k and 16.0k, and at the game's wages it moves with
>      them; a wage that is not there or not a number gives no cost.
>   2. FACULTY AND ROOM. The five schools' university-band staff, read from buildings.json, split over the fields as the study says (University
>      22, 22, 6, 6; Institute 46; Medical 50; Business 30; Law 34); faculty and room are the dials' share of it; a school that is not one of
>      those hosts none.
>   3. POSTS. Never more than the room or the money buys; a post is the jobs the study says; a post nobody fills costs nothing and has no researcher;
>      the dials refuse what is not an amount.
>   4. THE WATER FILL. An even spread under the caps, a small cap filled and the rest spilt evenly, every cap full leaves the remainder idle, the
>      shares add to the people less the idle, and no problem has less than another unless it is full.
>   5. OPEN PROBLEMS. Not before its science exists, open inside the window, not once known (here or by history's month), not while a need is
>      unknown; the lags are the study's and the law's is the Policy gate's; the minimum years and the team caps are the study's.
>   6. THE HIDDEN SIZE. The same for the same seed and code, equal to the study's model's draw, mean 1 and the dial's spread over 20,000 seeds,
>      different for different codes.
>   7. NO RESEARCHERS, HISTORY'S DATES. Nothing is found and every discovery arrives abroad in history's month, once.
>   8. A FIND. With a great many researchers in a field, discoveries are found here before history's date, never before their science exists or
>      their needs are known, never faster than the team cap allows, in the first month the pile reached the size; and only in that field.
>   9. KNOWN IN THIS CITY. A recipe, a piece and a building whose code was found here is known from that month and history's reading is not; with no
>      research, or an empty one, every reading is history's; the planners' pure cores (canRun, best, judge) and the game layer agree.
>  10. REPRODUCIBLE. The same months, researchers and seed find the same codes in the same months; another seed does not; a run saved, restored
>      and continued finds what the uninterrupted one does; a malformed save entry is skipped.
>  11. A NEW GAME CLEARS IT. Grants, posts, piles and finds are gone after newGame().
> 
> Slice B, the research in the city's month (Game.nextMonth() strikes the posts, getJobs() counts them, advanceDemographics() pays and works them,
> finalUpdateEconomy() settles the treasury's line; DataSave, Inbox, TaxPolicy.bringForward()); each section plays a village with a University:
> 
>  12. POSTS IN THE LABOUR MARKET. A grant buys the posts Research.posts() says at the strike's wages, never above the room; the city's jobs are
>      the buildings' plus Research.jobs() exactly; with every grant at 0 they are the buildings' alone.
>  13. THE BILL. The month's research payroll and costs are the sums of Research.payroll() and bill() at the month's wages and fill; the treasury's
>      line is a promise and the last of its constants; getExpenses() and the national accounts carry it; the month's money audit closes.
>  14. A CITY FOUNDED THE DEFAULT WAY. No grant, no change (no job, no bill, no notice); a grant buys posts that are paid, and every researcher is idle.
>  15. THE INBOX. A find raises one "found:" notice in its month and an arrival one "abroad:" notice, neither twice, none in the first month.
>  16. A LAW FIND OPENS ITS LEVER EARLY. The lever of a found PL entry opens the month after the find, never before its earliest, never later than
>      it was; a code that is no lever's key moves nothing; the openings are saved.
>  17. THE SAVE. A city with research writes it and a load restores grants, posts, piles, finds, jobs and the bill, and plays on as the city saved;
>      a city with none writes no key.
>  18. A REFORM OF THE MONEY scales the grants and the bill and nothing else of the research.
> 
> R3b (the gate reviews of R3 and R4), on the same villages:
> 
>  19. A SCHOOL ORDERED DEMOLISHED gives no posts the month it closes: the strike follows the demolitions (nextMonth()), so the room it strikes
>      from is the standing schools'; a twin with no order strikes as before.
>  20. THE CITY'S OWN PAYROLL at a new floor counts the research posts (PolicyPreview.cityPayrollAt()), and the landlords' credit check counts every
>      post the city offers, the research posts' jobs included (RealEstate.latentHeadShortfall()), as plan() does.

**Uses:** [Research](Research.md) (219), [Recipes](Recipes.md) (34), [JobType](JobType.md) (27), [Game](Game.md) (26), [BuildingsTemplate](BuildingsTemplate.md) (25), [PolicyDates](PolicyDates.md) (21), [EquipmentFit](EquipmentFit.md) (17), [TreasuryLine](TreasuryLine.md) (11), [TaxPolicy](TaxPolicy.md) (11), [Notice](Notice.md) (9), [EducationType](EducationType.md) (8), [PolicyPreview](PolicyPreview.md) (6), [PayTier](PayTier.md) (5), [Recipe](Recipe.md) (5), [Equipment](Equipment.md) (5), [EconomyManager](EconomyManager.md) (5), [MoneyAudit](MoneyAudit.md) (5), [GameFiles](GameFiles.md) (4), [EquipmentCheck](EquipmentCheck.md) (4), [BuildingType](BuildingType.md) (4), [ResearchCatalog](ResearchCatalog.md) (2), [PriceLevel](PriceLevel.md) (2), [BuildingCatalog](BuildingCatalog.md) (2), [FittingCheck](FittingCheck.md) (2), [Founding](Founding.md) (2), [BuildingManager](BuildingManager.md) (2), [MaterialsCheck](MaterialsCheck.md) (2), [Good](Good.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)... and 4 more

**Used by (2):** [PrizeCheck](PrizeCheck.md), [ResearchViewCheck](ResearchViewCheck.md)

## Sections

| line | section |
|---:|---|
| 103 | · the specification, typed from the study |
| 180 | 1. the post's cost |
| 220 | 2. faculty and room |
| 279 | 3. posts |
| 368 | 4. the water fill |
| 424 | 5. open problems |
| 501 | 6. the hidden size |
| 536 | 7. no researchers: history's dates |
| 575 | 8. a find, 9. known in this city |
| 777 | 10. reproducible |
| 830 | 11. a new game clears it |
| 866 | R3 SLICE B: THE RESEARCH IN THE CITY'S MONTH (sections 12 to 18). A city founded the 1900 way, a University (and a La... |
| 944 | 12. the posts are jobs the labour market sees |
| 994 | 13. the bill |
| 1055 | 14. a city founded the default way |
| 1106 | 15. the inbox |
| 1158 | 16. a law find brings its lever forward |
| 1225 | 17. the save |
| 1288 | 18. a reform of the money |
| 1318 | 19. a school ordered demolished (R3b) |
| 1349 | 20. the city's own payroll and the heads it houses (R3b) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 106 | `DiscoveryCheck.STUDY_POST` | `new EnumMap<>(Map.of(Field.ENGINEERING, 18.2, Field.MEDICINE, 23.8, Field.BUS...` | research-design 1.8: a post's cost at today's wages, D$ thousands a month, as the study prints it to a tenth (business's 12.25 is printed 12.2). |
| 109 | `DiscoveryCheck.STUDY_ROUNDING` | `0.06` | How far from the study's figure a post may be: the study's rounding to a tenth. |
| 112 | `DiscoveryCheck.STUDY_LAG_YEARS` | `new EnumMap<>(Map.of(Field.ENGINEERING, 7, Field.MEDICINE, 9, Field.BUSINESS,...` | research-design 1.8: how many years before history's date the science exists. |
| 114 | `DiscoveryCheck.CAT` | `ResearchCatalog.get()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static int passes` |  |
| 95 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1346 | **type** `public class DiscoveryCheck` | The city finds discoveries of its own, and a discovery found here is known here from the month it was found (R3, slice A: Research, and the seam in Recipe, Equipment, BuildingsTemplate, Recipes and EquipmentFit; docs/... |
| 80 | 4 | `static void check(String label, boolean ok)` |  |
| 86 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 91 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 97 | 5 | `static void quietly(Runnable r)` |  |

### the specification, typed from the study (lines 103-179)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1 | `static Entry of(String code)` |  |
| 118 | 5 | `static int lastWorldMonth()` |  |
| 124 | 4 | `static BuildingsTemplate byName(List<BuildingsTemplate> templates, String name)` |  |
| 129 | 4 | `static BuildingsTemplate byId(List<BuildingsTemplate> templates, int id)` |  |
| 135 | 5 | `static double[] wages(boolean today)` | The wages by JobType ordinal: today's (before the founding's price level) or the game's (PayTier.getMonthlyWage()). |
| 142 | 5 | `static double[] staffOf(BuildingsTemplate t)` | A school's university-band staff by JobType ordinal, as buildings.json gives its jobs. |
| 149 | 5 | `static double universityStaff(double[] staff)` | All of a school's university-band staff (the job types Research.servedBy() gives a field). |
| 155 | 24 | `public static void main(String[] args) throws Exception` |  |

### 1. the post's cost (lines 180-219)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 37 | `static void sectionOne()` |  |

### 2. faculty and room (lines 220-278)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 56 | `static void sectionTwo()` |  |

### 3. posts (lines 279-367)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 86 | `static void sectionThree()` |  |

### 4. the water fill (lines 368-423)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 5 | `static boolean sameShares(Research.Fill fill, double...expected)` |  |
| 376 | 47 | `static void sectionFour()` |  |

### 5. open problems (lines 424-500)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 74 | `static void sectionFive()` |  |

### 6. the hidden size (lines 501-535)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 3 | `static void spot(String label, long seed, String code, double expected)` | hiddenSize()'s size over the work, from the study's model: research_model.py hidden_size(seed, id) with id the code's Java hash, computed there. |
| 508 | 27 | `static void sectionSix()` |  |

### 7. no researchers: history's dates (lines 536-574)

| line | len | member | says |
|---:|---:|---|---|
| 538 | 36 | `static void sectionSeven()` |  |

### 8. a find, 9. known in this city (lines 575-776)

| line | len | member | says |
|---:|---:|---|---|
| 578 | 1 | **type** `record Run(Research research, List<String> faults)` | One field staffed with a great many researchers from month 1 to the month after history's last: the Research, and everything that went wrong. |
| 580 | 35 | `static Run run(long seed, Field field, double people)` |  |
| 617 | 9 | `static Set<String> carried(List<BuildingsTemplate> shipped, boolean recipes, boolean equipment, boolean buildings)` | Whether a code is carried as a "since" by a recipe, a piece of equipment or a building of the shipped file. |
| 627 | 149 | `static void sectionEightAndNine()` |  |

### 10. reproducible (lines 777-829)

| line | len | member | says |
|---:|---:|---|---|
| 779 | 50 | `static void sectionTen()` |  |

### 11. a new game clears it (lines 830-865)

| line | len | member | says |
|---:|---:|---|---|
| 832 | 33 | `static void sectionEleven()` |  |

### R3 SLICE B: THE RESEARCH IN THE CITY'S MONTH (sections 12 to 18). A city founded the 1900 way, a University (and a La... (lines 866-943)

| line | len | member | says |
|---:|---:|---|---|
| 872 | 3 | `static void play(Game g, int months)` | One press of the clock, quietly (the player's button: the auto-builder's pass first). |
| 877 | 12 | `static Game cityWithSchools(String label, boolean the1900Way, boolean lawSchool)` | A village (FittingCheck.village) with a University standing, and a Law School if asked, and the staff handed; founded the 1900 way or the default way. |
| 891 | 9 | `static EnumMap<Field, Double> roomOf(Game g)` | The posts the standing schools have room for in each field, written from each stack's template and quantity (Research.staffByField(), Research.room()). |
| 902 | 5 | `static List<Notice> noticesKeyed(Game g, String prefix)` | The notices in a city's inbox whose key starts as given, in the order raised. |
| 909 | 3 | `static boolean same(long[] a, long[] b)` | Whether two long arrays are equal, entry by entry. |
| 914 | 4 | `static boolean noPosts(Research r)` | Whether a research holds no post in any field (it may hold work piled and finds: the faculty of a school research with no grant at all). |
| 920 | 5 | `static long[] plus(long[] buildings, long[] posts)` | The buildings' jobs plus the research posts' of a field, by JobType ordinal: what the labour market is to see. |
| 927 | 8 | `static double[] billOf(Research research, double[] wages, double[] fill)` | The money bill of a research the way the model prices it: the sums of Research.payroll() and Research.bill() over the fields at the wages and fill. |
| 937 | 6 | `static void pileJustUnder(Game g, Entry e)` | Sets a pile just under the size of an entry in a city's research (and keeps the rest), so that one month of researchers in its field finds it. |

### 12. the posts are jobs the labour market sees (lines 944-993)

| line | len | member | says |
|---:|---:|---|---|
| 946 | 47 | `static void sectionTwelve()` |  |

### 13. the bill (lines 994-1054)

| line | len | member | says |
|---:|---:|---|---|
| 996 | 58 | `static void sectionThirteen()` |  |

### 14. a city founded the default way (lines 1055-1105)

| line | len | member | says |
|---:|---:|---|---|
| 1057 | 48 | `static void sectionFourteen()` |  |

### 15. the inbox (lines 1106-1157)

| line | len | member | says |
|---:|---:|---|---|
| 1108 | 49 | `static void sectionFifteen()` |  |

### 16. a law find brings its lever forward (lines 1158-1224)

| line | len | member | says |
|---:|---:|---|---|
| 1160 | 64 | `static void sectionSixteen() throws Exception` |  |

### 17. the save (lines 1225-1287)

| line | len | member | says |
|---:|---:|---|---|
| 1227 | 60 | `static void sectionSeventeen() throws Exception` |  |

### 18. a reform of the money (lines 1288-1317)

| line | len | member | says |
|---:|---:|---|---|
| 1290 | 27 | `static void sectionEighteen()` |  |

### 19. a school ordered demolished (R3b) (lines 1318-1348)

| line | len | member | says |
|---:|---:|---|---|
| 1320 | 28 | `static void sectionNineteen()` |  |

### 20. the city's own payroll and the heads it houses (R3b) (lines 1349-1420)

| line | len | member | says |
|---:|---:|---|---|
| 1351 | 69 | `static void sectionTwenty()` |  |

