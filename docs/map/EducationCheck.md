# EducationCheck.java - 1,533 lines · 13 methods · 2 constants · harnesses

`ham/citybuildersim/EducationCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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

**Uses:** [TaxPolicy](TaxPolicy.md) (73), [EducationType](EducationType.md) (70), [Game](Game.md) (38), [WageBand](WageBand.md) (32), [Education](Education.md) (23), [PriceLevel](PriceLevel.md) (15), [GameFiles](GameFiles.md) (13), [JobType](JobType.md) (11), [Migration](Migration.md) (7), [PayTier](PayTier.md) (7), [HouseholdBalance](HouseholdBalance.md) (7), [FamilyModel](FamilyModel.md) (5), [FamilyStructure](FamilyStructure.md) (4), [Household](Household.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [PopulationManager](PopulationManager.md) (2), [EconomyManager](EconomyManager.md) (2), [StudentHousehold](StudentHousehold.md) (2), [PrisonerHousehold](PrisonerHousehold.md) (2), [Sectors](Sectors.md) (1), [TempDirs](TempDirs.md) (1), [PriceLevelCheck](PriceLevelCheck.md) (1), [NationalAccounts](NationalAccounts.md) (1), [MoneyAudit](MoneyAudit.md) (1), [TreasuryJournal](TreasuryJournal.md) (1)

**Used by (2):** [DebtExchangeCheck](DebtExchangeCheck.md), [HistoryCheck](HistoryCheck.md)

## Sections

| line | section |
|---:|---|
| 158 | · 1. THE PROFESSION IS GATED |
| 212 | · 2. AND WITH ONE, IT CAN |
| 232 | · 3. NO SCHOOL TEACHES NOBODY |
| 251 | · 4. AND DOES NOT EMPTY THE BAND IT DRAWS FROM |
| 271 | · 5. A DEGREE IS A MOVE, NOT AN APPEARANCE |
| 302 | · 6. LICENCES CANNOT OUTNUMBER GRADUATES |
| 323 | · 6b. THE MONTH'S GRADUATES ARE THE GAINS (A6, 0.7.46) |
| 345 | · 7. THE PIPELINE IS ITS NARROWEST STAGE |
| 418 | · 8. THE SUBSIDY IS A REAL DIAL |
| 473 | · 9. IT SURVIVES A SAVE |
| 540 | · 9b. THE WAIT IS REAL |
| 582 | · 10. AND THE TREASURY PAYS FOR IT |
| 635 | · 11. THE UNSKILLED BAND IS A REPORT CARD |
| 758 | · 12. THE QUEUE FOR A JOB INCLUDES THE OVERQUALIFIED |
| 803 | · · THIS PREMISE USED TO READ `open[dip] > ownHeads[dip]` - "on its own |
| 849 | · 13. THE GRANT IS A MENU |
| 1033 | · 14. THE LOAN'S RATE |
| 1187 | · 15. THE PRICE OF A PLACE |
| 1302 | · 16. ALL THREE SURVIVE A SAVE |
| 1372 | · 17. A PRICE PER SCHOOL (0.7.6) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `EducationCheck.OUT` | `System.out` |  |
| 64 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 60 | 1474 | **type** `public class EducationCheck` | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. |
| 68 | 4 | `static void quietly(Runnable work)` |  |
| 73 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 85 | 1 | `static void build(Game g, String name, int n)` |  |
| 88 | 45 | `static Game city(GameFiles files)` | A funded city with room, so the thing under test is never money or land. |
| 144 | 3 | `static void held(Game g)` | GW1: the businesses hold, in sections 1 and 2's cities, whose subject is who can practise medicine and not what the businesses build. |
| 148 | 7 | `static void schools(Game g)` |  |
| 156 | 1336 | `public static void main(String[] args) throws Exception` |  |
| 1497 | 3 | `static double heads(Game g, WageBand band)` | A band's headcount, which - unlike its share - no amount of immigration into the OTHER bands can move. |
| 1505 | 4 | `static double fullness(Game g, EducationType type)` | How full one kind's schools stand at the month's end: its student body over the seats its staff can teach, the places the month hands Education (BuildingManager.getStaffedEducationPlaces at the city's fill rate). |
| 1511 | 6 | `static double journalLine(Game g, String label)` | Last month's journal line by its label, or NaN for none. |
| 1519 | 6 | `static double share(Game g, WageBand band)` | A band's share of the workforce. |
| 1526 | 7 | `static void cleanUp(Path root)` |  |

