# Research.java - 1,158 lines · 97 methods · 24 constants · model

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
> 
> R4, the prize and the Research Lab (research-design 5.3, 1.5, 5.4): a PRIZE dial per field is the D$ paid for each discovery made HERE in that field,
> and only on a find; and a Research Lab is a private building of LAB_POSTS posts of one field whose researchers, unlike the city's own, do not spread
> evenly but go for the cheapest open problem the purse pays for (the "chase", chase()). The month() with labs spreads the city's researchers exactly
> as before and then pours the labs' on the chase list, cheapest first, each problem taking what its team cap leaves; a find splits the purse by the
> work piled, the labs' part going (in a later slice) to the labs' owners and the schools' part into the field's POT, which buys posts beyond the grant
> (strikePosts(), drawPots()). The investors' questions (labsWanted(), labTarget(), labStake()) and the labs a field has too many of (labTooMany())
> are answered here too, so that Game only acts on them. Still no screen and no Game: every rule is a static function of its arguments.
> 
> WHY (R4). A grant pays for the work whether it finds anything or not (1.5); a prize pays only for the result, and so draws outsiders and their
> money to the problems near enough to finish before the world does, which is what the evidence says a prize does. Not here, and in Game (slice B):
> the Research Lab as a building (buildings.json "researches"), the investors building and shedding them (Game.considerLabs(), shedIdleLabs()), the
> purse paid to their owners (TreasuryLine.PRIZES) and the save (DataSave.ResearchRecord); in a later slice, the screens.

**Uses:** [JobType](JobType.md) (17), [ResearchCatalog](ResearchCatalog.md) (7), [World](World.md) (2), [EducationType](EducationType.md) (2), [BusinessInvestment](BusinessInvestment.md) (1)

**Used by (19):** [BuildScreen](BuildScreen.md), [BuildingsTemplate](BuildingsTemplate.md), [DataSave](DataSave.md), [DiscoveryCheck](DiscoveryCheck.md), [Equipment](Equipment.md), [EquipmentFit](EquipmentFit.md), [Game](Game.md), [HistorySave](HistorySave.md), [Inbox](Inbox.md), [KeptCheck](KeptCheck.md), [LandCheck](LandCheck.md), [PrizeCheck](PrizeCheck.md), [Recipe](Recipe.md), [Recipes](Recipes.md), [ResearchPreview](ResearchPreview.md), [ResearchPreviewCheck](ResearchPreviewCheck.md), [ResearchScreen](ResearchScreen.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md)

## Sections

| line | section |
|---:|---|
| 53 | THE DIALS (research-design 1.8). Each is the study's, and (est.) where the study says so. |
| 129 | THE FIELD: who does it, what a post is, how far ahead of the world (pure) |
| 221 | THE POST: its cost, its jobs and its bill (pure) |
| 284 | THE SCHOOLS: whose staff research, and the room for posts (pure) |
| 343 | THE WATER FILL: researchers spread over the open problems (pure) |
| 392 | THE PRIZE: what the purse pays for, and the labs (pure) |
| 446 | THE CITY'S RESEARCH: what a city adds to the catalogue |
| 498 | · the grants |
| 512 | · the prizes |
| 531 | · the posts |
| 585 | · what is known |
| 625 | · the month |
| 830 | · the labs' month |
| 882 | · the investors' questions |
| 906 | · what is kept |
| 975 | · the month's counts, to save (R3b): a flow cannot be reconstructed from the state a month ended in, and a load would r... |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `Research.FACULTY_SHARE` | `0.40` | The share of their time a school's university-band staff spend on research that no grant pays for: 40 of the 40/40/20 teaching, research and service split of a collective agreement [UVIC]. |
| 61 | `Research.POSTS_PER_STAFF` | `2.7` | The posts a school has room for, per university-band staff member: NSERC's 2.7 trainees to each funded researcher [NSERC-DRR] (est.). |
| 64 | `Research.SIZE_SIGMA` | `0.6` | The spread of the hidden size, the sigma of its lognormal (mean 1): a problem's size is its work times a draw, and 0.6 makes the draw run from about a third to about three times it at two standard deviations (est.). |
| 67 | `Research.SUPPORT_LAB` | `0.43` | The support staff a laboratory post carries, college-band, for each researcher: 0.43, Canada's researchers being 0.70 of its R&D personnel [TOE], [MSTI]. |
| 70 | `Research.SUPPORT_NO_LABS` | `0.20` | The support staff a post with no laboratory carries, diploma-band, for each researcher (est.). |
| 73 | `Research.LABOUR_SHARE_ENGINEERING` | `0.62` | The share of an engineering lab's running cost that is pay, so a post costs its pay over this: 62%, labour being about 60 to 67% of R&D cost [TOE] (est.). |
| 76 | `Research.LABOUR_SHARE_MEDICINE` | `0.60` | The share of a medical lab's running cost that is pay: 60% (est.). |
| 79 | `Research.LABOUR_SHARE_NO_LABS` | `0.80` | The share of business and law research that is pay, there being no labs: 80% (est.). |
| 82 | `Research.LAG_YEARS_ENGINEERING` | `7` | How many years before history's date engineering science exists: 7, academic finding to product [MANS91]. |
| 85 | `Research.LAG_YEARS_MEDICINE` | `9` | How many years before history's date medical science exists: 9, drugs 8.8 [MANS91]. |
| 88 | `Research.LAG_YEARS_BUSINESS` | `4` | How many years before history's date business science exists: 4 (est.; [MANS91] has none for it). |
| 91 | `Research.LAG_YEARS_LAW` | `4` | How many years before history's date legal science exists: 4, and so many months as a law lever may be brought forward (PolicyDates.MAX_EARLY_MONTHS), which DiscoveryCheck holds equal. |
| 94 | `Research.MIN_YEARS_BUILDING` | `3` | The fewest years of work a building takes, however many work on it: 3 [DIMASI] (est.). |
| 97 | `Research.MIN_YEARS_EQUIPMENT` | `3` | The fewest years of work a piece of equipment takes: 3, as a building (est.). |
| 100 | `Research.MIN_YEARS_RECIPE` | `2` | The fewest years of work a recipe takes: 2 (est.). |
| 103 | `Research.MIN_YEARS_DRUG` | `8` | The fewest years of work a drug takes: 8, its clinical phases being sequential [DIMASI], from the year of the rule. |
| 106 | `Research.DRUG_RULE_YEAR` | `1962` | The year a drug first had to pass the efficacy trials, the Kefauver-Harris amendments: 1962 (research-1900 Q6). |
| 109 | `Research.MIN_YEARS_DRUG_BEFORE_RULE` | `2` | The fewest years of work a drug dated before DRUG_RULE_YEAR takes: 2, there being no phased trials to wait on (research-1900 Q6, decided). |
| 112 | `Research.LAB_POSTS` | `100` | A Research Lab's research posts, all of one field (research-design 5.3, est.: a building of 100, the Engineering Services Office's 120 staff being the study's yardstick). |
| 115 | `Research.LAB_ROOM_SHARE` | `0.5` | The share of a lab's researchers that must have work for investors to build it, and that a field's idle lab researchers may reach, as one lab's, before the field has a lab too many (est.). |
| 118 | `Research.REMAINING_DUST` | `1e-6` | The least remaining work a problem is judged by, so that a pile past its typical work still costs something (research_model.py: 1e-6). |
| 121 | `Research.LAB_IDLE_MONTHS` | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | The months in a row a field has a lab too many before one is shed: a building's retirement months (BusinessInvestment.RETIREMENT_LOSS_MONTHS), "then is shed, as any building is" (5.3). |
| 124 | `Research.MONTHS_A_YEAR` | `12` | Months in a year: a pile gains its researchers over this many each month. |
| 127 | `Research.IDLE_DUST` | `1e-9` | The smallest number of researchers left over that the water fill counts as idle (the study's 1e-9). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 451 | `private final EnumMap<Field, Double> grants` | The grant dials, a month's money for each field's posts, in the game's money unit; a field with none is 0. |
| 454 | `private final EnumMap<Field, Integer> posts` | The posts struck this month for each field (strikePosts()): what the grants bought within the room. |
| 457 | `private final Map<String, Double> piles` | The work piled on each problem worked on and not yet known, in researcher-years, by code. |
| 460 | `private final Map<String, Integer> found` | The discoveries found here and the month each was found, in the order found. |
| 463 | `private final List<String> foundThisMonth` | This month's events (month()): the codes found here, the codes that reached the city from abroad, and each field's researchers, idle researchers and open problems. |
| 464 | `private final List<String> arrivedThisMonth` |  |
| 465 | `private final EnumMap<Field, Double> researchersNow` |  |
| 466 | `private final EnumMap<Field, Double> idleNow` |  |
| 467 | `private final EnumMap<Field, Integer> openNow` |  |
| 470 | `private final Map<String, Double> sizes` | The hidden sizes drawn so far for one seed, so that a month draws each once. |
| 471 | `private long sizesSeed` |  |
| 474 | `private final EnumMap<Field, Double> prizes` | The prize dials, D$ paid for each discovery made here in a field, in the game's money unit; a field with none is 0. |
| 477 | `private final EnumMap<Field, Double> pots` | Each field's pot: the schools' share of the purses, not yet spent on posts beyond the grant (strikePosts(), drawPots()). |
| 480 | `private final EnumMap<Field, Integer> grantPosts` | The posts the grant alone bought at the last strike, so that drawPots() knows which posts the pot pays for. |
| 483 | `private final Map<String, Double> labPiles` | The part of each pile that the labs' researchers worked, in researcher-years, by code: kept apart so that a find knows who did the work. |
| 486 | `private final Map<String, Double> cityShares` | The city's own researchers on each open problem in the last month() (not the labs'), by code; what labsWanted() reads, so it is saved. |
| 489 | `private final EnumMap<Field, Integer> labIdleMonths` | The consecutive months a field has had a lab too many, by field; none is not in the map. |
| 492 | `private final Map<String, Double> labShares` | This month's labs (month()): the labs' researchers on each problem, each field's lab researchers and idle ones, the purse owed to the labs and each find's purse. |
| 493 | `private final EnumMap<Field, Double> labResearchersNow` |  |
| 494 | `private final EnumMap<Field, Double> labsIdleNow` |  |
| 495 | `private final EnumMap<Field, Double> toLabsNow` |  |
| 496 | `private final Map<String, Purse> purses` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 1108 | **type** `public final class Research` | The city's research (R3): the grants the player sets, the posts they buy, the researchers who fill them, the work piled on each open problem, the discoveries found here and the month's pure step that moves the work on... |

### THE DIALS (research-design 1.8). Each is the study's, and (est.) where the study says so. (lines 53-128)

### THE FIELD: who does it, what a post is, how far ahead of the world (pure) (lines 129-220)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 8 | `public static JobType researcherA(Field f)` | The first of a field's two researcher job types, the larger half of a post: the licence-holding band (the high-tech engineer, the doctor, the finance officer, the lawyer). |
| 144 | 6 | `public static JobType researcherB(Field f)` | The second researcher job type of a field, the other half of a post: the ungated university band (science for the labs, policy for business and law). |
| 152 | 7 | `public static JobType supportType(Field f)` | The job type of a field's support staff: college engineering and college health for the labs, a diploma for business and law. |
| 161 | 6 | `public static double supportPerPost(Field f)` | Support staff a post carries for each researcher: SUPPORT_LAB in the labs, SUPPORT_NO_LABS in business and law. |
| 169 | 7 | `public static double labourShare(Field f)` | The share of a post's all-in cost that is pay. |
| 178 | 8 | `public static int lagYears(Field f)` | Years before history's date the science of a field's discoveries exists. |
| 188 | 3 | `public static int lagMonths(Field f)` | Months before history's date the science of a field's discoveries exists: lagYears() of twelve. |
| 193 | 8 | `public static double minYears(Entry e)` | The fewest years of work a discovery takes, however many work on it: its size's (a drug dated before DRUG_RULE_YEAR the shorter). |
| 203 | 3 | `public static double teamCap(Entry e)` | The most researchers that can work on a discovery at once: its work over its fewest years (a drug cannot be found in less than 8 years, however many work on it). |
| 212 | 8 | `public static double hiddenSize(long seed, Entry e)` | A discovery's size for THIS city, in researcher-years: its work times a lognormal draw (sigma SIZE_SIGMA, mean 1) from the city's seed and the discovery's code, by World.mix() (the SplitMix64 finaliser the world uses)... |

### THE POST: its cost, its jobs and its bill (pure) (lines 221-283)

| line | len | member | says |
|---:|---:|---|---|
| 230 | 7 | `public static double postCost(Field f, double[] wagePerType)` | What one post costs a month, all in: half the pay of each researcher job type and the support staff a post carries at theirs, over the labour share. |
| 239 | 5 | `public static int posts(double grant, double cost, double room)` | Whole posts a grant buys: the grant over a post's cost, never more than the room there is; none for a grant that is not above nothing, a cost that is not a number above nothing, or no room. |
| 249 | 8 | `public static long[] jobs(Field f, int posts)` | The jobs `posts` posts of a field are, by JobType ordinal: the first researcher type takes the larger half (posts less posts / 2), the second the smaller (posts / 2, whole numbers), and the support staff are the suppo... |
| 259 | 3 | `private static double fillOf(double[] fillPerType, int type)` | The fill of a job type: its entry in the array, or 1 where there is none (BuildingManager.getCategoryPayroll() reads a missing fill as full). |
| 264 | 7 | `public static double payroll(Field f, int posts, double[] wagePerType, double[] fillPerType)` | The pay of the posts, as BuildingManager.getCategoryPayroll() sums it: each job type's jobs times its wage times its fill. |
| 273 | 3 | `public static double bill(Field f, int posts, double[] wagePerType, double[] fillPerType)` | What the filled posts cost the city, all in: their pay over the labour share. |
| 278 | 5 | `public static double filledResearchers(Field f, int posts, double[] fillPerType)` | The researchers the posts give: the researcher jobs (not the support staff) times their fill. |

### THE SCHOOLS: whose staff research, and the room for posts (pure) (lines 284-342)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 11 | `public static List<Field> servedBy(JobType type)` | The fields a university-band job type serves (science serves two, policy two); none for any other job type. |
| 307 | 25 | `public static EnumMap<Field, Double> staffByField(EducationType teaches, double[] univStaffByJobType)` | A school's university-band staff by field. |
| 334 | 3 | `public static double faculty(double staff)` | The researcher-FTE the faculty give for nothing: FACULTY_SHARE of the university-band staff a school has in a field. |
| 339 | 3 | `public static double room(double staff)` | The posts a school has room for in a field: POSTS_PER_STAFF of its university-band staff there. |

### THE WATER FILL: researchers spread over the open problems (pure) (lines 343-391)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 1 | **type** `public record Fill(double[] share, double idle)` | How a field's researchers spread: each problem's share of them, in the order of the caps given, and the researchers left idle when every problem is full. |
| 356 | 35 | `public static Fill waterFill(double people, double[] caps)` | Spreads `people` researchers evenly over the problems, none past its cap, the spare spilling to the rest (research_model.py water_fill()): an even share to each problem still live, the problems that cannot take a whol... |

### THE PRIZE: what the purse pays for, and the labs (pure) (lines 392-445)

| line | len | member | says |
|---:|---:|---|---|
| 400 | 3 | `public static double remaining(Entry e, double pile)` | The work left on a problem, in researcher-years: its typical work (Entry.work(), never the hidden size, which nobody sees) less the pile, but never less than REMAINING_DUST (research_model.py: max(effort - progress, 1... |
| 405 | 3 | `public static double monthsToFinish(Entry e, double pile)` | The months a team at its limit (teamCap()) takes to finish a problem with that pile: its remaining work over the team, twelve researcher-years a year each. |
| 413 | 4 | `public static boolean purseCovers(double prize, double cost, Entry e, double pile)` | Whether a purse pays for a problem ("on the chase"): the prize is at least the remaining typical work times a researcher-year's cost, `prize >= remaining * 12 * cost`, `cost` being a post's cost a month (postCost()). |
| 419 | 3 | `public static boolean beforeTheWorld(Entry e, double pile, int month)` | Whether a team at its limit could finish a problem with that pile before the world does: this month plus monthsToFinish() falls before history's month. |
| 424 | 3 | `public static double stake(double cost, Entry e, double pile)` | What investors put up for a lab on a problem: LAB_POSTS researchers at `cost` a post a month, for the months it takes to finish. |
| 432 | 5 | `public static int labsFor(double room)` | The labs investors want for that much room (researchers the chase list could still take): while the n-th would keep at least LAB_ROOM_SHARE of its researchers at work, n = floor(room / LAB_POSTS + 1 - LAB_ROOM_SHARE). |
| 439 | 3 | `public static long[] labJobs(Field f)` | The jobs one Research Lab is: jobs() of LAB_POSTS posts of the field (the lab templates in buildings.json are written to equal it). |
| 444 | 1 | **type** `public record Purse(double toLabs, double toSchools)` | What a find here pays out: the labs' part, owed to their owners, and the schools' part, which joins the field's pot. |

### THE CITY'S RESEARCH: what a city adds to the catalogue (lines 446-497)

### the grants (lines 498-511)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 3 | `public double grant(Field f)` | A field's grant, a month's money: 0 where none is set. |
| 506 | 5 | `public boolean setGrant(Field f, double amount)` | Sets a field's grant. |

### the prizes (lines 512-530)

| line | len | member | says |
|---:|---:|---|---|
| 515 | 3 | `public double prize(Field f)` | A field's prize, D$ for each discovery made here: 0 where none is set. |
| 520 | 5 | `public boolean setPrize(Field f, double amount)` | Sets a field's prize. |
| 527 | 3 | `public double pot(Field f)` | A field's pot: the schools' share of the purses paid out here, not yet spent on posts. |

### the posts (lines 531-584)

| line | len | member | says |
|---:|---:|---|---|
| 534 | 3 | `public int posts(Field f)` | The posts struck this month for a field (strikePosts()); 0 before any. |
| 543 | 8 | `public void strikePosts(EnumMap<Field, Double> room, double[] wagePerType)` | Strikes the month's posts: for each field the whole posts its grant and its pot buy at the post's cost (postCost() at these wages) within the room it has (posts()); the posts the grant alone buys are remembered (grant... |
| 553 | 3 | `public int grantPosts(Field f)` | The posts the grant alone bought at the last strike (strikePosts()); the posts above them are the pot's. |
| 561 | 13 | `public double drawPots(double[] wagePerType, double[] fillPerType)` | Pays for the pots' posts at the month's bill and lowers the pots by it: each field's draw is the bill of all its posts less the bill of the grant's (bill() at this month's wages and fill, so a post nobody fills draws ... |
| 576 | 8 | `public long[] jobs()` | The jobs the month's posts are, all fields together, by JobType ordinal (jobs() of each field's posts). |

### what is known (lines 585-624)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 4 | `public int foundMonth(String code)` | The month a code was found here, or Integer.MAX_VALUE for one that was not (a year, a blank and a code the city has not found are all that). |
| 594 | 3 | `public boolean foundHere(String code)` | Whether the city found a discovery itself, before history's date. |
| 602 | 3 | `public int knownFrom(String since)` | The game month a "since" is known from in this city: history's (ResearchCatalog.knownFrom(): a year's January, a code's entry's month, never for a blank or a code the catalogue lacks) lowered to the month the city fou... |
| 607 | 3 | `public boolean known(String code, int month)` | Whether a "since" is known in this city at a game month: from history's month, or from the month it was found here, whichever is first. |
| 612 | 5 | `public boolean isOpen(Entry e, int month)` | Whether a discovery is open to its field's researchers in a month: not known, its science exists (history's month less the field's lag) and every code it needs known (here or abroad). |
| 619 | 5 | `public List<Entry> openProblems(Field f, int month)` | A field's open problems in a month, in the catalogue's order. |

### the month (lines 625-829)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 7 | `private double sizeOf(long seed, Entry e)` | The size of a discovery in this city, drawn once for a seed (hiddenSize()). |
| 637 | 3 | `private static double sane(Double people)` | A researcher count made safe to add: what is not a number or is below nothing is none. |
| 645 | 3 | `public void month(int month, long seed, EnumMap<Field, Double> researchers)` | The month's step with no labs: month() with none and no wages, so that nothing is on the chase (the city's own researchers alone; a find with a prize set then pays the whole purse to the schools). |
| 662 | 68 | `public void month(int month, long seed, EnumMap<Field, Double> researchers, EnumMap<Field, Integer> labs, EnumMap<Field, Double...` | The month's step (research_model.py run(), one month). |
| 737 | 3 | `public void idleMonth(EnumMap<Field, Double> researchers)` | The month of a city founded the default way (Game.advanceDemographics()): every discovery is known from the start, so nothing is open and nothing is found or arrives, and the researchers its faculty and posts give hav... |
| 742 | 17 | `public void idleMonth(EnumMap<Field, Double> researchers, EnumMap<Field, Integer> labs, EnumMap<Field, Double> labResearchers)` | The same with labs: every lab researcher is idle too, and a field with labs has a lab too many this month as month() counts it (labIdleMonths()). |
| 761 | 7 | `private void clearLabMonth()` | Forgets the labs' month: the shares, the researchers, the idle, the purses owed. |
| 774 | 7 | `private void recordLabs(Field f, Integer labCount, double labPeople, double labIdle)` | Records a field's labs for the month: the researchers in them and the idle ones, and the count of consecutive months with a lab too many. |
| 787 | 6 | `public List<Entry> chase(Field f, int month, double[] wagePerType)` | A field's chase in a month: its open problems that the field's prize pays for (purseCovers(), at the post's cost at these wages) and that a team at its limit could finish before the world (beforeTheWorld()), with toda... |
| 795 | 9 | `private List<Entry> chaseOf(List<Entry> open, int month, double prize, double cost)` | The chase among problems already open, in the catalogue's order (the sort is stable, so equal remaining work keeps that order). |
| 806 | 3 | `public List<String> foundThisMonth()` | The codes found here this month (month()), in the order found. |
| 811 | 3 | `public List<String> arrivedThisMonth()` | The codes that reached the city from abroad this month: history dated them this month and the city had not found them. |
| 816 | 3 | `public double researchers(Field f)` | A field's researchers this month, as month() was given them. |
| 821 | 3 | `public double idle(Field f)` | A field's researchers this month that had no problem to work on: all of them when none was open, else the remainder of the water fill. |
| 826 | 3 | `public int open(Field f)` | How many problems were open to a field this month. |

### the labs' month (lines 830-881)

| line | len | member | says |
|---:|---:|---|---|
| 833 | 3 | `public double labShare(String code)` | The labs' researchers on a problem this month (month()): 0 where none worked on it. |
| 838 | 3 | `public double cityShare(String code)` | The city's own researchers on a problem in the last month() (not the labs'): 0 where none worked on it. |
| 843 | 3 | `public double labResearchers(Field f)` | A field's lab researchers this month, as month() was given them. |
| 848 | 3 | `public double labsIdle(Field f)` | A field's lab researchers this month that had nothing to chase: all of them when the chase was empty, else what the pour left over. |
| 853 | 3 | `public double toLabs(Field f)` | The purse this month's finds in a field owed to the labs, all together (month()): the sum of the field's purses' labs' parts. |
| 858 | 3 | `public Map<String, Purse> purses()` | This month's purses by the code found (month()), in the order found: only the finds made with a prize set. |
| 863 | 3 | `public double labPile(String code)` | The work the labs' researchers have piled on a problem, in researcher-years: 0 where none is worked on, it is known, or no lab worked on it. |
| 868 | 3 | `public int labIdleMonths(Field f)` | The consecutive months a field has had a lab too many (month(), idleMonth()); 0 for a field that has not, and for one with no lab. |
| 873 | 3 | `public boolean labTooMany(Field f)` | Whether a field has had a lab too many for LAB_IDLE_MONTHS months running, so that one lab is to be shed. |
| 878 | 3 | `public void labShed(Field f)` | A lab of the field has been shed (Game.shedIdleLabs() calls this): the count of months with a lab too many starts again from nothing. |

### the investors' questions (lines 882-905)

| line | len | member | says |
|---:|---:|---|---|
| 888 | 5 | `public int labsWanted(Field f, int month, double[] wagePerType)` | The labs investors want in a field in a month: labsFor() of the room on the chase list, the sum of what each problem on it can still take of its team cap, less the city's share of it in the last month(). |
| 895 | 4 | `public Entry labTarget(Field f, int month, double[] wagePerType)` | The problem a new lab would go for: the chase list's first, or null where there is none. |
| 901 | 4 | `public double labStake(Field f, int month, double[] wagePerType)` | What investors put up for one lab at the target (stake() of it at the field's post cost and its pile now): NaN where there is no target. |

### what is kept (lines 906-974)

| line | len | member | says |
|---:|---:|---|---|
| 909 | 3 | `public double pile(String code)` | The work piled on a problem, in researcher-years: 0 where none is worked on or it is known. |
| 914 | 3 | `public Map<String, Double> piles()` | The piles by code: a fresh map. |
| 919 | 3 | `public Map<String, Integer> foundHere()` | The discoveries found here by code and the month each was found, in the order found: a fresh map. |
| 924 | 5 | `public boolean isEmpty()` | Whether the city has nothing of its own: no grant, no prize, no pot, no post, no work piled (the labs' either), no lab too many and nothing found (a city founded the default way, always). |
| 931 | 5 | `public Map<String, Double> grantsForSave()` | The grants to save, by Field.name(); only those above nothing. |
| 938 | 5 | `public Map<String, Integer> postsForSave()` | The posts to save, by Field.name(); only those above nothing. |
| 945 | 5 | `public Map<String, Double> prizesForSave()` | The prizes to save, by Field.name(); only those above nothing. |
| 952 | 5 | `public Map<String, Double> potsForSave()` | The pots to save, by Field.name(); only those above nothing. |
| 959 | 3 | `public Map<String, Double> labPilesForSave()` | The labs' part of the piles to save, by code. |
| 964 | 3 | `public Map<String, Double> citySharesForSave()` | The city's own researchers on each problem in the last month(), to save, by code (what labsWanted() reads after a reload). |
| 969 | 5 | `public Map<String, Integer> labIdleMonthsForSave()` | The months in a row each field has had a lab too many, to save, by Field.name(); only those above nothing. |

### the month's counts, to save (R3b): a flow cannot be reconstructed from the state a month ended in, and a load would r... (lines 975-1158)

| line | len | member | says |
|---:|---:|---|---|
| 978 | 1 | `public Map<String, Double> researchersForSave()` | The month's researchers by field, to save, by Field.name(); only those above nothing (researchers() reads 0 for the rest). |
| 981 | 1 | `public Map<String, Double> idleForSave()` | The month's idle researchers by field, to save, by Field.name(); only those above nothing. |
| 984 | 5 | `public Map<String, Integer> openForSave()` | The month's open problems by field, to save, by Field.name(); only those above nothing. |
| 991 | 1 | `public Map<String, Double> labResearchersForSave()` | The month's lab researchers by field, to save, by Field.name(); only those above nothing. |
| 994 | 1 | `public Map<String, Double> labsIdleForSave()` | The month's idle lab researchers by field, to save, by Field.name(); only those above nothing. |
| 996 | 8 | `private static Map<String, Double> aboveNothing(EnumMap<Field, Double> counts)` |  |
| 1012 | 42 | `public void restorePrizes(Map<String, Double> prizes, Map<String, Double> pots, Map<String, Double> labPiles, Map<String, Doubl...` | Puts the prizes, the pots, the labs' piles, the city's shares and the counts of months with a lab too many back, replacing what there was (the load path, called AFTER restore(), whose piles the labs' part must sit wit... |
| 1061 | 18 | `public void restoreMonth(Map<String, Double> researchers, Map<String, Double> idle, Map<String, Integer> open, Map<String, Doub...` | Puts the month's counts back (R3b; the load path, called AFTER restore() and restorePrizes(), neither of which may clear them): each field's researchers, idle researchers, open problems, lab researchers and idle lab r... |
| 1080 | 7 | `private static void putAmounts(EnumMap<Field, Double> into, Map<String, Double> saved)` |  |
| 1093 | 28 | `public void restore(Map<String, Double> grants, Map<String, Integer> posts, Map<String, Double> piles, Map<String, Integer> found)` | Puts the city's research back, replacing all (the load path), keyed by Field.name() and by code and never by an ordinal. |
| 1122 | 7 | `private static Field fieldOf(String name)` |  |
| 1131 | 5 | `public void redenominate(double scale)` | A reform of the money (Game.redenominate()): every grant, prize and pot times `scale`. |
| 1138 | 20 | `public void reset()` | A new game: no grant, no prize, no pot, no post, no work piled, nothing found, no lab too many and no event (Game.buildWorld()). |

