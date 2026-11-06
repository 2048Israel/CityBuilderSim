# Research.java - 681 lines · 52 methods · 20 constants · model

`ham/citybuildersim/Research.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> The city's research (R3): the grants the player sets, the posts they buy, the researchers who fill them, the work piled on each open problem, the
> discoveries found here and the month's pure step that moves the work on (docs/studies/research-design.md 1.8, 5.1, 5.2, 6.3).
> 
> WHY. Before R3 a discovery came only on history's date (ResearchCatalog, R2): the tractor in June 1917 whatever a city did, and nothing a school
> or a grant could change. Jerus's brief (2026-10-18, "rec all"): money buys research POSTS at the schools, posts are jobs the labour market
> fills, and the researchers, the schools' own faculty at 40% of their time and whoever fills a post, spread evenly over the field's OPEN
> problems, each with a fixed amount of work in it. Nothing multiplies a chance. This class is that mechanism with no screen and no Game in
> it, as research_model.py (the study's model) is, so a harness can feed it figures: every rule is a static function of its arguments, and the
> instance holds only what a city adds to the catalogue. The seam to the rest of the game is Recipe, Equipment and BuildingsTemplate
> knownAt(month, Research): a discovery found here is known in this city from the month it was found, and the shelf of history's dates is
> otherwise unchanged. In a city with no researcher and no grant nothing is ever found here, so every reading equals history's.
> 
> The month (month()) mirrors the inner loop of research_model.py run(): the world's own dates first (a discovery history has dated this month,
> or earlier, that the city has not found is the city's from abroad, and the work piled on it stops); then, field by field, the problems that
> are OPEN (not known; its science exists, which is history's month less the field's lag; every code it needs known, here or abroad), the
> field's researchers spread over them evenly with none past its team cap, each pile gaining its researchers over twelve researcher-years a month,
> and a pile that reaches the problem's HIDDEN SIZE is found here that month. The size is drawn once from the city's seed and the code
> (hiddenSize()), is a fact about the city and is never saved. Not here, and in Game: striking the posts at the top of the month (nextMonth()),
> counting them among the city's jobs (getJobs()), paying for them and calling the step (advanceDemographics(), beside the schools), saving them
> (DataSave) and the inbox's notices (Inbox.takeResearch()); this class gives them the getters, idleMonth() and restore().

**Uses:** [JobType](JobType.md) (17), [ResearchCatalog](ResearchCatalog.md) (5), [World](World.md) (2), [EducationType](EducationType.md) (2)

**Used by (10):** [BuildingsTemplate](BuildingsTemplate.md), [DataSave](DataSave.md), [DiscoveryCheck](DiscoveryCheck.md), [Equipment](Equipment.md), [EquipmentFit](EquipmentFit.md), [Game](Game.md), [Inbox](Inbox.md), [LandCheck](LandCheck.md), [Recipe](Recipe.md), [Recipes](Recipes.md)

## Sections

| line | section |
|---:|---|
| 39 | THE DIALS (research-design 1.8). Each is the study's, and (est.) where the study says so. |
| 103 | THE FIELD: who does it, what a post is, how far ahead of the world (pure) |
| 195 | THE POST: its cost, its jobs and its bill (pure) |
| 258 | THE SCHOOLS: whose staff research, and the room for posts (pure) |
| 317 | THE WATER FILL: researchers spread over the open problems (pure) |
| 366 | THE CITY'S RESEARCH: what a city adds to the catalogue |
| 393 | · the grants |
| 407 | · the posts |
| 435 | · what is known |
| 475 | · the month |
| 584 | · what is kept |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `Research.FACULTY_SHARE` | `0.40` | The share of their time a school's university-band staff spend on research that no grant pays for: 40 of the 40/40/20 teaching, research and service split of a collective agreement [UVIC]. |
| 47 | `Research.POSTS_PER_STAFF` | `2.7` | The posts a school has room for, per university-band staff member: NSERC's 2.7 trainees to each funded researcher [NSERC-DRR] (est.). |
| 50 | `Research.SIZE_SIGMA` | `0.6` | The spread of the hidden size, the sigma of its lognormal (mean 1): a problem's size is its work times a draw, and 0.6 makes the draw run from about a third to about three times it at two standard deviations (est.). |
| 53 | `Research.SUPPORT_LAB` | `0.43` | The support staff a laboratory post carries, college-band, for each researcher: 0.43, Canada's researchers being 0.70 of its R&D personnel [TOE], [MSTI]. |
| 56 | `Research.SUPPORT_NO_LABS` | `0.20` | The support staff a post with no laboratory carries, diploma-band, for each researcher (est.). |
| 59 | `Research.LABOUR_SHARE_ENGINEERING` | `0.62` | The share of an engineering lab's running cost that is pay, so a post costs its pay over this: 62%, labour being about 60 to 67% of R&D cost [TOE] (est.). |
| 62 | `Research.LABOUR_SHARE_MEDICINE` | `0.60` | The share of a medical lab's running cost that is pay: 60% (est.). |
| 65 | `Research.LABOUR_SHARE_NO_LABS` | `0.80` | The share of business and law research that is pay, there being no labs: 80% (est.). |
| 68 | `Research.LAG_YEARS_ENGINEERING` | `7` | How many years before history's date engineering science exists: 7, academic finding to product [MANS91]. |
| 71 | `Research.LAG_YEARS_MEDICINE` | `9` | How many years before history's date medical science exists: 9, drugs 8.8 [MANS91]. |
| 74 | `Research.LAG_YEARS_BUSINESS` | `4` | How many years before history's date business science exists: 4 (est.; [MANS91] has none for it). |
| 77 | `Research.LAG_YEARS_LAW` | `4` | How many years before history's date legal science exists: 4, and so many months as a law lever may be brought forward (PolicyDates.MAX_EARLY_MONTHS), which DiscoveryCheck holds equal. |
| 80 | `Research.MIN_YEARS_BUILDING` | `3` | The fewest years of work a building takes, however many work on it: 3 [DIMASI] (est.). |
| 83 | `Research.MIN_YEARS_EQUIPMENT` | `3` | The fewest years of work a piece of equipment takes: 3, as a building (est.). |
| 86 | `Research.MIN_YEARS_RECIPE` | `2` | The fewest years of work a recipe takes: 2 (est.). |
| 89 | `Research.MIN_YEARS_DRUG` | `8` | The fewest years of work a drug takes: 8, its clinical phases being sequential [DIMASI], from the year of the rule. |
| 92 | `Research.DRUG_RULE_YEAR` | `1962` | The year a drug first had to pass the efficacy trials, the Kefauver-Harris amendments: 1962 (research-1900 Q6). |
| 95 | `Research.MIN_YEARS_DRUG_BEFORE_RULE` | `2` | The fewest years of work a drug dated before DRUG_RULE_YEAR takes: 2, there being no phased trials to wait on (research-1900 Q6, decided). |
| 98 | `Research.MONTHS_A_YEAR` | `12` | Months in a year: a pile gains its researchers over this many each month. |
| 101 | `Research.IDLE_DUST` | `1e-9` | The smallest number of researchers left over that the water fill counts as idle (the study's 1e-9). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 371 | `private final EnumMap<Field, Double> grants` | The grant dials, a month's money for each field's posts, in the game's money unit; a field with none is 0. |
| 374 | `private final EnumMap<Field, Integer> posts` | The posts struck this month for each field (strikePosts()): what the grants bought within the room. |
| 377 | `private final Map<String, Double> piles` | The work piled on each problem worked on and not yet known, in researcher-years, by code. |
| 380 | `private final Map<String, Integer> found` | The discoveries found here and the month each was found, in the order found. |
| 383 | `private final List<String> foundThisMonth` | This month's events (month()): the codes found here, the codes that reached the city from abroad, and each field's researchers, idle researchers and open problems. |
| 384 | `private final List<String> arrivedThisMonth` |  |
| 385 | `private final EnumMap<Field, Double> researchersNow` |  |
| 386 | `private final EnumMap<Field, Double> idleNow` |  |
| 387 | `private final EnumMap<Field, Integer> openNow` |  |
| 390 | `private final Map<String, Double> sizes` | The hidden sizes drawn so far for one seed, so that a month draws each once. |
| 391 | `private long sizesSeed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 645 | **type** `public final class Research` | The city's research (R3): the grants the player sets, the posts they buy, the researchers who fill them, the work piled on each open problem, the discoveries found here and the month's pure step that moves the work on... |

### THE DIALS (research-design 1.8). Each is the study's, and (est.) where the study says so. (lines 39-102)

### THE FIELD: who does it, what a post is, how far ahead of the world (pure) (lines 103-194)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 8 | `public static JobType researcherA(Field f)` | The first of a field's two researcher job types, the larger half of a post: the licence-holding band (the high-tech engineer, the doctor, the finance officer, the lawyer). |
| 118 | 6 | `public static JobType researcherB(Field f)` | The second researcher job type of a field, the other half of a post: the ungated university band (science for the labs, policy for business and law). |
| 126 | 7 | `public static JobType supportType(Field f)` | The job type of a field's support staff: college engineering and college health for the labs, a diploma for business and law. |
| 135 | 6 | `public static double supportPerPost(Field f)` | Support staff a post carries for each researcher: SUPPORT_LAB in the labs, SUPPORT_NO_LABS in business and law. |
| 143 | 7 | `public static double labourShare(Field f)` | The share of a post's all-in cost that is pay. |
| 152 | 8 | `public static int lagYears(Field f)` | Years before history's date the science of a field's discoveries exists. |
| 162 | 3 | `public static int lagMonths(Field f)` | Months before history's date the science of a field's discoveries exists: lagYears() of twelve. |
| 167 | 8 | `public static double minYears(Entry e)` | The fewest years of work a discovery takes, however many work on it: its size's (a drug dated before DRUG_RULE_YEAR the shorter). |
| 177 | 3 | `public static double teamCap(Entry e)` | The most researchers that can work on a discovery at once: its work over its fewest years (a drug cannot be found in less than 8 years, however many work on it). |
| 186 | 8 | `public static double hiddenSize(long seed, Entry e)` | A discovery's size for THIS city, in researcher-years: its work times a lognormal draw (sigma SIZE_SIGMA, mean 1) from the city's seed and the discovery's code, by World.mix() (the SplitMix64 finaliser the world uses)... |

### THE POST: its cost, its jobs and its bill (pure) (lines 195-257)

| line | len | member | says |
|---:|---:|---|---|
| 204 | 7 | `public static double postCost(Field f, double[] wagePerType)` | What one post costs a month, all in: half the pay of each researcher job type and the support staff a post carries at theirs, over the labour share. |
| 213 | 5 | `public static int posts(double grant, double cost, double room)` | Whole posts a grant buys: the grant over a post's cost, never more than the room there is; none for a grant that is not above nothing, a cost that is not a number above nothing, or no room. |
| 223 | 8 | `public static long[] jobs(Field f, int posts)` | The jobs `posts` posts of a field are, by JobType ordinal: the first researcher type takes the larger half (posts less posts / 2), the second the smaller (posts / 2, whole numbers), and the support staff are the suppo... |
| 233 | 3 | `private static double fillOf(double[] fillPerType, int type)` | The fill of a job type: its entry in the array, or 1 where there is none (BuildingManager.getCategoryPayroll() reads a missing fill as full). |
| 238 | 7 | `public static double payroll(Field f, int posts, double[] wagePerType, double[] fillPerType)` | The pay of the posts, as BuildingManager.getCategoryPayroll() sums it: each job type's jobs times its wage times its fill. |
| 247 | 3 | `public static double bill(Field f, int posts, double[] wagePerType, double[] fillPerType)` | What the filled posts cost the city, all in: their pay over the labour share. |
| 252 | 5 | `public static double filledResearchers(Field f, int posts, double[] fillPerType)` | The researchers the posts give: the researcher jobs (not the support staff) times their fill. |

### THE SCHOOLS: whose staff research, and the room for posts (pure) (lines 258-316)

| line | len | member | says |
|---:|---:|---|---|
| 263 | 11 | `public static List<Field> servedBy(JobType type)` | The fields a university-band job type serves (science serves two, policy two); none for any other job type. |
| 281 | 25 | `public static EnumMap<Field, Double> staffByField(EducationType teaches, double[] univStaffByJobType)` | A school's university-band staff by field. |
| 308 | 3 | `public static double faculty(double staff)` | The researcher-FTE the faculty give for nothing: FACULTY_SHARE of the university-band staff a school has in a field. |
| 313 | 3 | `public static double room(double staff)` | The posts a school has room for in a field: POSTS_PER_STAFF of its university-band staff there. |

### THE WATER FILL: researchers spread over the open problems (pure) (lines 317-365)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 1 | **type** `public record Fill(double[] share, double idle)` | How a field's researchers spread: each problem's share of them, in the order of the caps given, and the researchers left idle when every problem is full. |
| 330 | 35 | `public static Fill waterFill(double people, double[] caps)` | Spreads `people` researchers evenly over the problems, none past its cap, the spare spilling to the rest (research_model.py water_fill()): an even share to each problem still live, the problems that cannot take a whol... |

### THE CITY'S RESEARCH: what a city adds to the catalogue (lines 366-392)

### the grants (lines 393-406)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 3 | `public double grant(Field f)` | A field's grant, a month's money: 0 where none is set. |
| 401 | 5 | `public boolean setGrant(Field f, double amount)` | Sets a field's grant. |

### the posts (lines 407-434)

| line | len | member | says |
|---:|---:|---|---|
| 410 | 3 | `public int posts(Field f)` | The posts struck this month for a field (strikePosts()); 0 before any. |
| 418 | 6 | `public void strikePosts(EnumMap<Field, Double> room, double[] wagePerType)` | Strikes the month's posts: for each field the whole posts its grant buys at the post's cost (postCost() at these wages) within the room it has (posts()). |
| 426 | 8 | `public long[] jobs()` | The jobs the month's posts are, all fields together, by JobType ordinal (jobs() of each field's posts). |

### what is known (lines 435-474)

| line | len | member | says |
|---:|---:|---|---|
| 438 | 4 | `public int foundMonth(String code)` | The month a code was found here, or Integer.MAX_VALUE for one that was not (a year, a blank and a code the city has not found are all that). |
| 444 | 3 | `public boolean foundHere(String code)` | Whether the city found a discovery itself, before history's date. |
| 452 | 3 | `public int knownFrom(String since)` | The game month a "since" is known from in this city: history's (ResearchCatalog.knownFrom(): a year's January, a code's entry's month, never for a blank or a code the catalogue lacks) lowered to the month the city fou... |
| 457 | 3 | `public boolean known(String code, int month)` | Whether a "since" is known in this city at a game month: from history's month, or from the month it was found here, whichever is first. |
| 462 | 5 | `public boolean isOpen(Entry e, int month)` | Whether a discovery is open to its field's researchers in a month: not known, its science exists (history's month less the field's lag) and every code it needs known (here or abroad). |
| 469 | 5 | `public List<Entry> openProblems(Field f, int month)` | A field's open problems in a month, in the catalogue's order. |

### the month (lines 475-583)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 7 | `private double sizeOf(long seed, Entry e)` | The size of a discovery in this city, drawn once for a seed (hiddenSize()). |
| 487 | 3 | `private static double sane(Double people)` | A researcher count made safe to add: what is not a number or is below nothing is none. |
| 499 | 39 | `public void month(int month, long seed, EnumMap<Field, Double> researchers)` | The month's step (research_model.py run(), one month). |
| 545 | 13 | `public void idleMonth(EnumMap<Field, Double> researchers)` | The month of a city founded the default way (Game.advanceDemographics()): every discovery is known from the start, so nothing is open and nothing is found or arrives, and the researchers its faculty and posts give hav... |
| 560 | 3 | `public List<String> foundThisMonth()` | The codes found here this month (month()), in the order found. |
| 565 | 3 | `public List<String> arrivedThisMonth()` | The codes that reached the city from abroad this month: history dated them this month and the city had not found them. |
| 570 | 3 | `public double researchers(Field f)` | A field's researchers this month, as month() was given them. |
| 575 | 3 | `public double idle(Field f)` | A field's researchers this month that had no problem to work on: all of them when none was open, else the remainder of the water fill. |
| 580 | 3 | `public int open(Field f)` | How many problems were open to a field this month. |

### what is kept (lines 584-681)

| line | len | member | says |
|---:|---:|---|---|
| 587 | 3 | `public double pile(String code)` | The work piled on a problem, in researcher-years: 0 where none is worked on or it is known. |
| 592 | 3 | `public Map<String, Double> piles()` | The piles by code: a fresh map. |
| 597 | 3 | `public Map<String, Integer> foundHere()` | The discoveries found here by code and the month each was found, in the order found: a fresh map. |
| 602 | 3 | `public boolean isEmpty()` | Whether the city has nothing of its own: no grant, no post, no work piled and nothing found (a city founded the default way, always). |
| 607 | 5 | `public Map<String, Double> grantsForSave()` | The grants to save, by Field.name(); only those above nothing. |
| 614 | 5 | `public Map<String, Integer> postsForSave()` | The posts to save, by Field.name(); only those above nothing. |
| 625 | 28 | `public void restore(Map<String, Double> grants, Map<String, Integer> posts, Map<String, Double> piles, Map<String, Integer> found)` | Puts the city's research back, replacing all (the load path), keyed by Field.name() and by code and never by an ordinal. |
| 654 | 7 | `private static Field fieldOf(String name)` |  |
| 663 | 3 | `public void redenominate(double scale)` | A reform of the money (Game.redenominate()): every grant times `scale`. |
| 668 | 13 | `public void reset()` | A new game: no grant, no post, no work piled, nothing found and no event (Game.buildWorld()). |

