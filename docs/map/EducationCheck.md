# EducationCheck.java - 1,971 lines · 19 methods · 23 constants · harnesses

`ham/citybuildersim/EducationCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Verifies the schools: who gets taught, who is allowed to practise, and what
> it costs. Not part of the game.
> 
> WHY THIS EXISTS
> 
> Education is the longest feedback loop in the game - a medical school built
> today is doctors in the 2040s - and a loop that long is one nobody can debug
> by playing. Every failure mode here looks like patience:
> 
> 1. A SCHOOL THAT TEACHES NOBODY looks exactly like a school whose graduates
>    have not arrived yet. The first version of this feature had a medical
>    school that enrolled zero students for three hundred and sixty months
>    while costing $78M, because the return on the course was computed against
>    the doctor's own wage and therefore came out at exactly 1.00.
> 
> 2. A SCHOOL THAT TEACHES TOO FAST empties the band it draws from. The first
>    version took ninety per cent of every diploma-holder in the city the month
>    the college opened; the diploma band went from 1,390 people to 28 and
>    never recovered, because a STOCK was being consumed at the rate a FLOW
>    should be.
> 
> 3. A LICENCE THAT LEAKS staffs posts out of nothing, which is the
>    220-doctors bug in a new hat.
> 
> 4. A GRANT STRUCK ON THE WRONG THING looks like a grant (2026-09-21). Four
>    bases share one rule, and the default one - a fixed amount kept up with
>    the price index since 0.7.19, a share of the unskilled wage until then -
>    has to be its own bill to the bit and, at founding prices, the old
>    default's; a share of the surplus has to read the surplus the bridge
>    shows and nothing in a deficit, and a share of tuition has to follow
>    the price - each of them a number that would look plausible wrong.
> 
> 5. INTEREST THAT LANDS NOWHERE, OR TWICE. The loan's rate is money out of
>    a graduate's wages and into the treasury; it has to be a revenue line
>    once, the principal has to stay the journal's, a student and a prisoner
>    have to be charged nothing, and at a zero rate the arithmetic has to be
>    bit for bit the interest-free loan it was.
> 
> 6. A PRICE THAT ONE READER MISSES. The tuition scale reaches the fee, the
>    household's share, the burden, the treasury's revenue and what it
>    forgave; a reader that took the founding fee would charge one price
>    and record another, and the identity between them is the check.
> 
> 7. A PRICE PER SCHOOL THAT A LOAD PUTS BACK TOGETHER (0.7.6). The scale is
>    nine, one per kind; one kind's move has to reach that kind's fee and no
>    other's, the every-school setter has to move all nine, an old array has
>    to read nine equal scales, and the load path must tell the schools the
>    nine and not the one - the income rate did that to the three bases
>    until 0.7.4. And the Schools page's row per kind has to add up to the
>    totals it sits under.
> 
> FS16: the first sections cause their premises. Their towns (townBuilt()) are six months old with their bands set (skillsSet()) and
> their students seeded part way through a course (finishing()); the sections on a lived city (a licence belongs to a person, through a save, the
> city's books) are identities whose breadth is the played state. The price, the wait, the report card, the grant, the loan and the price of a place do the same: one
> town fed the one lever (subsidy, a pool set wide, students seeded with seeded(), the basket based and the level set, a hand-fed poor town)
> and the month or the course stepped by hand, where they were cities played hundreds of months. The one section still reading a condition off a played city is
> "the queue for a job includes the overqualified", which builds the city it reads.

**Uses:** [EducationType](EducationType.md) (132), [TaxPolicy](TaxPolicy.md) (77), [WageBand](WageBand.md) (49), [Education](Education.md) (48), [Game](Game.md) (43), [JobType](JobType.md) (39), [PriceLevel](PriceLevel.md) (15), [SchoolingLadderCheck](SchoolingLadderCheck.md) (13), [AdultStage](AdultStage.md) (12), [Migration](Migration.md) (11), [GameFiles](GameFiles.md) (10), [LabourCheck](LabourCheck.md) (10), [PopulationManager](PopulationManager.md) (9), [PopulationCohorts](PopulationCohorts.md) (9), [AgeBand](AgeBand.md) (7), [PayTier](PayTier.md) (7), [HouseholdBalance](HouseholdBalance.md) (7), [FamilyModel](FamilyModel.md) (6), [LabourMarket](LabourMarket.md) (6), [SchoolingLadder](SchoolingLadder.md) (6), [FamilyStructure](FamilyStructure.md) (4), [Household](Household.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [TempDirs](TempDirs.md) (2), [EconomyManager](EconomyManager.md) (2), [StudentHousehold](StudentHousehold.md) (2), [PrisonerHousehold](PrisonerHousehold.md) (2), [Sectors](Sectors.md) (1), [PriceLevelCheck](PriceLevelCheck.md) (1), [NationalAccounts](NationalAccounts.md) (1)... and 2 more

**Used by (4):** [DebtExchangeCheck](DebtExchangeCheck.md), [HistoryCheck](HistoryCheck.md), [OutsideCheck](OutsideCheck.md), [Village1900Check](Village1900Check.md)

## Sections

| line | section |
|---:|---|
| 312 | · 1. THE PROFESSION IS GATED |
| 352 | · 2. AND WITH ONE, IT CAN |
| 399 | · 3. NO SCHOOL TEACHES NOBODY |
| 436 | · 4. AND DOES NOT EMPTY THE BAND IT DRAWS FROM |
| 466 | · 5. A DEGREE IS A MOVE, NOT AN APPEARANCE |
| 499 | · 6. LICENCES CANNOT OUTNUMBER GRADUATES |
| 532 | · 6b. THE MONTH'S GRADUATES ARE THE GAINS (A6, 0.7.46) |
| 555 | · 7. THE PIPELINE IS ITS NARROWEST STAGE |
| 648 | · 8. THE SUBSIDY IS A REAL DIAL |
| 700 | · 9. IT SURVIVES A SAVE |
| 767 | · 9b. THE WAIT IS REAL |
| 816 | · 10. AND THE TREASURY PAYS FOR IT |
| 869 | · 11. THE UNSKILLED BAND IS A REPORT CARD |
| 981 | · 12. THE QUEUE FOR A JOB INCLUDES THE OVERQUALIFIED |
| 1038 | · · THIS PREMISE USED TO READ `open[dip] > ownHeads[dip]` - "on its own |
| 1084 | · 13. THE GRANT IS A MENU |
| 1288 | · 14. THE LOAN'S RATE |
| 1449 | · 15. THE PRICE OF A PLACE |
| 1611 | · 16. ALL THREE SURVIVE A SAVE |
| 1681 | · 17. A PRICE PER SCHOOL (0.7.6) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `EducationCheck.OUT` | `System.out` |  |
| 71 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 164 | `EducationCheck.TOWN_MONTHS` | `6` | Months a school town is lived before its section reads it (FS16): the first has no workforce and the second no staffed school, so by the sixth the schools teach and every band has people. |
| 167 | `EducationCheck.DIPLOMA_SHARE` | `.15` | The diploma band a school town is set to, as a share of its workforce (FS16): the pool the colleges and the universities draw on, kept small enough that a drained band shows. |
| 170 | `EducationCheck.COLLEGE_SHARE` | `.15` | The college band a school town is set to, as a share of its workforce (FS16): the people the universities' graduates join and the professional schools draw on. |
| 173 | `EducationCheck.UNIVERSITY_SHARE` | `.20` | The university band a school town is set to, as a share of its workforce (FS16): the pool the medical school draws its doctors from, wider than the doctor posts. |
| 176 | `EducationCheck.DOCTORS_OVER_POSTS` | `1.1` | The medical students finishing in the town that has enough doctors, as a multiple of its doctor posts (FS16): just over what the hospitals can seat. |
| 179 | `EducationCheck.DOCTORS_UNDER_POSTS` | `.9` | The medical students finishing in the town that is short of doctors, as a multiple of its doctor posts (FS16): just under what the hospitals can seat. |
| 182 | `EducationCheck.FINISHING_SHARE` | `.01` | The college and university students seeded to finish in each of the town's first two months, as a share of its workforce (FS16): enough to move the bands, few enough to leave the diploma band standing. |
| 185 | `EducationCheck.PLENTY_OF_PLACES` | `.9` | The share of the children's need the primary seats must clear to be plenty (FS16): the pinched town's "plenty of primary places" law is coverage over this. |
| 188 | `EducationCheck.SEATS_MARGIN` | `.05` | How far either side of PLENTY_OF_PLACES the seats of the coverage threshold are put, as a share of the need (FS16): just over and just under. |
| 231 | `EducationCheck.PRICE_POOL_SHARE` | `.15` | The diploma band the price section's towns are set to, as a share of the first town's workforce (FS16): the same pool in all three, small enough that three universities never fill on it. |
| 234 | `EducationCheck.GRANT_STUDENTS` | `400` | The students the grant's towns are put through a university with (FS16): enough that the bill is a sum of people and not a rounding. |
| 237 | `EducationCheck.GRANT_EXPECTED_LEVEL` | `1.25` | The expected price level the grant's town is put at (FS16): off founding by a quarter, so the bill's level is a factor and not a one. |
| 240 | `EducationCheck.LOAN_STAYING_STUDENTS` | `300` | The university students put into the lender town with their whole course before them (FS16): the ones who stay in the students' cell and keep borrowing. |
| 243 | `EducationCheck.LOAN_FINISHING_STUDENTS` | `100` | The university students put into the lender town in their last month (FS16): the ones who graduate in its first month and carry their loans to the working families in its second. |
| 246 | `EducationCheck.SURPLUS_CONTRIBUTION_RATE` | `.3` | The pension contribution rate the grant's town is put at to show a surplus (FS16): with the income tax at .5 and the property tax at its ceiling, enough that a town six months old closes its bridge. |
| 249 | `EducationCheck.SURPLUS_HEALTH_PREMIUM` | `.1` | The health premium rate the grant's town is put at to show a surplus (FS16): with the contributions above, the last of what a town six months old needs to close its bridge. |
| 265 | `EducationCheck.PLENTY_OF_LABOURERS` | `6_000` | The workforce of the hand-fed town whose labourers outnumber its labouring posts (FS16): over the 3,300 posts the section puts up, so the unskilled wage cannot rise over the going rate. |
| 268 | `EducationCheck.SCARCE_OF_LABOURERS` | `2_400` | The workforce of the hand-fed town whose labouring posts outnumber its labourers (FS16): under the 3,300 posts less its skilled heads, so the unskilled wage climbs over the going rate. |
| 271 | `EducationCheck.WAIT_POOL_SHARE` | `.5` | The diploma band the wait's town is set to, as a share of its workforce (FS16): a university's pool wide enough that its first month's intake is well over the ten students the section asks for. |
| 276 | `EducationCheck.PRICE_TOWN_WORKFORCE` | `4_000` | The workforce of the hand-fed poor town the price section reads (FS16): large enough that its pool of diploma-holders is hundreds. |
| 279 | `EducationCheck.PRICE_TOWN_DIPLOMAS` | `1_500` | The diploma-holders of the hand-fed poor town (FS16): three times its 500 diploma posts, so the diploma wage is a surplus's and sits near the table the fees were struck against. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 67 | 1905 | **type** `public class EducationCheck` | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. |
| 75 | 4 | `static void quietly(Runnable work)` |  |
| 80 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 85 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 92 | 1 | `static void build(Game g, String name, int n)` |  |
| 95 | 45 | `static Game city(GameFiles files)` | A funded city with room, so the thing under test is never money or land. |
| 151 | 3 | `static void held(Game g)` | GW1: the businesses hold, in sections 1 and 2's cities, whose subject is who can practise medicine and not what the businesses build. |
| 155 | 7 | `static void schools(Game g)` |  |
| 191 | 10 | `static Game townBuilt(boolean withSchools, boolean withMedical)` | A town of the schools sections (FS16): city(), held, optionally every school and a medical school, lived TOWN_MONTHS - 1 months so the schools are staffed and the arrivals are in. |
| 203 | 8 | `static void skillsSet(Game g)` | The town's skilled bands set to their shares of its workforce (FS16): the applicants each school draws on, caused and not grown. |
| 217 | 12 | `static boolean finishing(Education ed, EducationType type, double students, int monthsLeft)` | Students put part way through a course (FS16), who graduate in monthsLeft months: the course's queue in the education state (Education.getState(): the dial, the totals, then every course's queue in enum order), writte... |
| 256 | 7 | `static boolean seeded(Game g, EducationType type, double students, int monthsLeft)` | Students put into a course and into the town's books (FS16): finishing()'s cohort, and then the population manager told who is out of the supply (Education.getStudying()) and the families who the students are (setSeek... |
| 282 | 7 | `static double[] priceTownHeads()` | The skilled heads of the hand-fed poor town (FS16): PRICE_TOWN_DIPLOMAS diploma-holders and a few above them. |
| 291 | 11 | `static long[] priceTownPosts()` | The posts of the hand-fed poor town (FS16): 500 for the diploma-holders, and a few labouring and graduate ones. |
| 303 | 1498 | `public static void main(String[] args) throws Exception` |  |
| 1803 | 88 | `static void afterCostReturns()` | WG3: fee and stage changes must reach actual intake through the original curve. |
| 1893 | 40 | `static void afterCostBinding() throws Exception` | WG3's actual Game binding, through construction, a month and a load, using scratch saves only. |
| 1934 | 21 | `static void assertStudyBinding(Game game, String path)` |  |
| 1957 | 6 | `static double journalLine(Game g, String label)` | Last month's journal line by its label, or NaN for none. |
| 1964 | 7 | `static void cleanUp(Path root)` |  |

