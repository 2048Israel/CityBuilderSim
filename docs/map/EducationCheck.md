# EducationCheck.java - 1,238 lines · 11 methods · 2 constants · harnesses

`ham/citybuildersim/EducationCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
>    bases share one rule, and the founding one has to be the founding bill
>    to the bit, a share of the surplus has to read the surplus the bridge
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

**Uses:** [EducationType](EducationType.md) (68), [TaxPolicy](TaxPolicy.md) (63), [Game](Game.md) (32), [WageBand](WageBand.md) (26), [Education](Education.md) (21), [JobType](JobType.md) (11), [GameFiles](GameFiles.md) (10), [Migration](Migration.md) (7), [HouseholdBalance](HouseholdBalance.md) (7), [PayTier](PayTier.md) (6), [FamilyStructure](FamilyStructure.md) (4), [FamilyModel](FamilyModel.md) (3), [Household](Household.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [PopulationManager](PopulationManager.md) (2), [EconomyManager](EconomyManager.md) (2), [StudentHousehold](StudentHousehold.md) (2), [PrisonerHousehold](PrisonerHousehold.md) (2), [NationalAccounts](NationalAccounts.md) (1), [TreasuryJournal](TreasuryJournal.md) (1)

## Sections

| line | section |
|---:|---|
| 138 | · 1. THE PROFESSION IS GATED |
| 191 | · 2. AND WITH ONE, IT CAN |
| 210 | · 3. NO SCHOOL TEACHES NOBODY |
| 229 | · 4. AND DOES NOT EMPTY THE BAND IT DRAWS FROM |
| 249 | · 5. A DEGREE IS A MOVE, NOT AN APPEARANCE |
| 277 | · 6. LICENCES CANNOT OUTNUMBER GRADUATES |
| 298 | · 7. THE PIPELINE IS ITS NARROWEST STAGE |
| 335 | · 8. THE SUBSIDY IS A REAL DIAL |
| 370 | · 9. IT SURVIVES A SAVE |
| 437 | · 9b. THE WAIT IS REAL |
| 479 | · 10. AND THE TREASURY PAYS FOR IT |
| 532 | · 11. THE UNSKILLED BAND IS A REPORT CARD |
| 607 | · 12. THE QUEUE FOR A JOB INCLUDES THE OVERQUALIFIED |
| 636 | · · THIS PREMISE USED TO READ `open[dip] > ownHeads[dip]` - "on its own |
| 682 | · 13. THE GRANT IS A MENU |
| 804 | · 14. THE LOAN'S RATE |
| 941 | · 15. THE PRICE OF A PLACE |
| 1035 | · 16. ALL THREE SURVIVE A SAVE |
| 1086 | · 17. A PRICE PER SCHOOL (0.7.6) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `EducationCheck.OUT` | `System.out` |  |
| 62 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1181 | **type** `public class EducationCheck` | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. |
| 66 | 4 | `static void quietly(Runnable work)` |  |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 83 | 1 | `static void build(Game g, String name, int n)` |  |
| 86 | 41 | `static Game city(GameFiles files)` | A funded city with room, so the thing under test is never money or land. |
| 128 | 7 | `static void schools(Game g)` |  |
| 136 | 1070 | `public static void main(String[] args) throws Exception` |  |
| 1211 | 3 | `static double heads(Game g, WageBand band)` | A band's headcount, which - unlike its share - no amount of immigration into the OTHER bands can move. |
| 1216 | 6 | `static double journalLine(Game g, String label)` | Last month's journal line by its label, or NaN for none. |
| 1224 | 6 | `static double share(Game g, WageBand band)` | A band's share of the workforce. |
| 1231 | 7 | `static void cleanUp(Path root)` |  |

