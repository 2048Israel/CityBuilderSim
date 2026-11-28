# PolicyPreviewCheck.java - 1,406 lines · 28 methods · 11 constants · harnesses

`ham/citybuildersim/PolicyPreviewCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made
> of, held to the month's own books in a played city.
> 
> WHY. Every dial on the Policy tab shows what it would do before it is
> applied, and since 0.7.36 every "after" is a model read on the class that
> owns the arithmetic, asked of a detached copy of the policy the staged set
> was put through (the project's spec-policy-0735.md, D2). A read that did
> not reproduce the month's own figure at the city's own dials, a copy that
> wrote into the city's decision log, or a preview that differed from what
> the model computes once the same dials are applied would be a confident
> wrong answer on the screen where the player decides the budget. It is
> advice, not a model change, so nothing in the default playtest reaches it:
> this is the only place it is played.
> 
> What this has to prove:
>   1. THE COPY is every dial, detached: its own setters clamp as the city's
>      do, a move on it moves nothing in the city, and it records no decision.
>   2. AT THE CITY'S OWN POLICY every line is the month's: each sector's
>      profit tax its booked tax, its sales tax its net, the wage tax by
>      band summed to getWageTax(), property each sector's charge at the roll
>      as it stands, the contribution and both premiums off the wage bill,
>      the pensions, the EI bill on the pool, the pensioner household, the
>      central bank's compression and ceiling, the floor in today's money -
>      and THE BUDGET moves by nothing.
>   3. THE BUDGET is last month's balance plus each line's move: a tax-only
>      set moves it by the take's move, the pension by the pensions', the
>      clinic's fee by the fees at one - and since 0.7.103 (Jerus's A7) the
>      fuel duty by the month's litres at the pump at the duty charged, at
>      the city's own dial the month's duty.
>   4. THE OTHER READS: the savers' rate the bank would choose, between where
>      it is and where it heads; the compression and the ceiling at another
>      setting; the floor's cash and founding figures one way and back; the
>      floor lifting only the wages under it (WG1: the posts filled in the jobs
>      it reaches, none when it stays under every target, the city's own payroll
>      job by job, the job cover).
>   5. AFTER A LOAD the take at the city's own policy is the saved month's.
>   6. APPLIED, THE PREVIEW IS THE MODEL: the same dials set on the city
>      read as the copy did; the take struck on the copy is the take struck
>      on the city after, on the same books; a month on, the EI bill the
>      treasury paid at the top of it and the bank's tax charged there are
>      the previews struck before it, to the cent, and each payroll line is
>      the month's own at the new dials (6b: section 2 again, there).
>   7. A DIAL AT ZERO previews something (the spec's B9): the old screen
>      scaled today's figure by the ratio of two rates and read nothing from
>      a contribution, a pension, a premium or a benefit at zero.
>   8. THE FUND'S WITHDRAWAL (0.7.48, C2): at the dial in force the
>      preview's due is next month's, struck when it comes; and a year on,
>      earning nothing, the fund is its value times (1 - the rate) to the
>      twelfth, which is what twelve of the model's own withdrawals leave. With
>      cash paid in past what the rule can place (RB1), the same two on the
>      idle cash's withdrawal, the larger of the dial and a 24th.
>   9. ZONING'S BEFORE AND AFTER (0.7.107, batch Z1): a zone and a forest
>      law staged on a copy move nothing in the city, and applied read as
>      the copy did - what the zone holds, what that ground would fetch, the
>      price given up, the forest's three kinds and the ground free for
>      anyone; and a zone's tax relief is the property tax un-zoning it
>      would raise.
>  10. THE PORT DUES (FR4; the dial is FR3's): in a city whose berth has
>      worked, the dues previewed at the city's own dial are the month's,
>      Freight's and the treasury's, to the bit; at 2 twice that, which is
> ... (14 more lines in the source)

**Uses:** [PolicyPreview](PolicyPreview.md) (95), [FloorView](FloorView.md) (56), [Game](Game.md) (40), [JobType](JobType.md) (31), [LabourMarket](LabourMarket.md) (30), [Sectors](Sectors.md) (28), [TaxPolicy](TaxPolicy.md) (27), [TreasuryFund](TreasuryFund.md) (23), [WageBand](WageBand.md) (19), [Formats](Formats.md) (14), [Ports](Ports.md) (8), [GameFiles](GameFiles.md) (7), [EconomyManager](EconomyManager.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (7), [CentralBank](CentralBank.md) (7), [WorldMeasures](WorldMeasures.md) (7), [PriceLevel](PriceLevel.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [Zoning](Zoning.md) (5), [PopulationManager](PopulationManager.md) (4), [Unemployment](Unemployment.md) (4), [BuildingType](BuildingType.md) (4), [LandlordPortCheck](LandlordPortCheck.md) (4), [LongPlaytest](LongPlaytest.md) (3), [Sector](Sector.md) (3), [DebtManager](DebtManager.md) (3), [PortDivision](PortDivision.md) (3), [ResearchCatalog](ResearchCatalog.md) (2), [Research](Research.md) (2), [LandManager](LandManager.md) (2)... and 9 more

**Used by (1):** [GovernmentViewCheck](GovernmentViewCheck.md)

## Sections

| line | section |
|---:|---|
| 294 | 1. THE COPY |
| 347 | 2. AT THE CITY'S OWN POLICY |
| 425 | 7. A DIAL AT ZERO |
| 478 | 6. APPLIED, THE PREVIEW IS THE MODEL |
| 543 | 3. THE BUDGET |
| 635 | 4. THE OTHER READS |
| 840 | 11. THE MINIMUM-WAGE PAGE (WS1) |
| 1162 | 8. THE FUND'S WITHDRAWAL (0.7.48) |
| 1241 | 5. AFTER A LOAD |
| 1261 | 9. ZONING'S BEFORE AND AFTER (0.7.107) |
| 1317 | 10. THE PORT DUES (FR4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 185 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "800" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders (the houses 800 since PT1-M4: see city()). |
| 281 | `PolicyPreviewCheck.EI_POOL_ROUNDS` | `6` | The most employers anEiPool() closes before it gives up on a pool (IC1): the city with a post for everyone is closed into one in two or three. |
| 481 | `PolicyPreviewCheck.BANK_PROFIT` | `10_000` | FS17: the profit the bank is given to be taxed on in section 6 (D$ thousands, founding money): the month just closed's, as Bank.chargeTax() reads it at the top of the next. |
| 546 | `PolicyPreviewCheck.BUDGET_FUEL_DUTY` | `.0000215 * PriceLevel.FACTOR` | FS17: the fuel duty the budget twin's city charges, a dial set by hand (a tenth of the 21.5 cents the section stages over it), so the month's duty is a line with something in it. |
| 549 | `PolicyPreviewCheck.BUDGET_SLOT` | `12, FLOORS_SLOT = 13, WAGES_SLOT = 11` | FS17: the slot of the scratch folder a twin is saved to (slots 1-9 are never written; these are the scratch folder's own). |
| 638 | `PolicyPreviewCheck.HAND_DEPOSITS` | `1_000` | FS17: the deposits the bank is given by hand where a section needs depositors, each book's share (D$ thousands, founding money). |
| 681 | `PolicyPreviewCheck.LADDER_POSTS` | `100` | FS17: the posts every job has on the hand-made ladder (the city's workforce is thousands, so every post is filled), and the one job the ladder gives none. |
| 682 | `PolicyPreviewCheck.LADDER_NO_POSTS` | `JobType.UNIV_POLICY` |  |
| 1320 | `PolicyPreviewCheck.PORT_SEA_SHARE` | `.5` | FS17: the share of the chain's dry bulk the berths are handed (LandlordPortCheck.handShares()), and the most months the berth is worked for a crew to be hired and a quote struck. |
| 1321 | `PolicyPreviewCheck.PORT_MONTHS` | `6` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 89 | `static int fails` |  |
| 90 | `static PrintStream out` |  |
| 91 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 1320 | **type** `public class PolicyPreviewCheck` | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. |
| 93 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 101 | 1 | `static boolean near(double a, double b)` |  |
| 103 | 5 | `static void quietly(Runnable r)` |  |
| 151 | 32 | `static Game city(Path root)` | A played city with every line of the Policy tab in it: homes, shops, a diner and builders; a mine, a foundry and a fabrication shop; power, water and roads; a commercial bank (its profit tax), clinics (their fees) and... |
| 191 | 24 | `public static void main(String[] args) throws Exception` |  |
| 222 | 21 | `static void anEiPool(Game g)` | An EI pool that stays, caused. |
| 245 | 10 | `static BuildingsTemplate largestEmployer(Game g)` | The city's largest employer by its posts, or null when it has none. |
| 261 | 18 | `static boolean layOff(Game g)` | FS17: retires half of the largest employer, and of the next largest after it, until the posts retired are more than twice the city's vacancies (the posts it has, PopulationManager.getJobs(), less the posts filled, get... |
| 284 | 9 | `static void printCity(Game g)` | The city as the previews read it, for the record. |

### 1. THE COPY (lines 294-346)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 50 | `static void theCopy(Game g)` |  |

### 2. AT THE CITY'S OWN POLICY (lines 347-424)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 69 | `static void atTheCitysOwn(Game g, String section)` |  |
| 419 | 5 | `static double sum(java.util.Map<String, Double> m)` |  |

### 7. A DIAL AT ZERO (lines 425-477)

| line | len | member | says |
|---:|---:|---|---|
| 427 | 50 | `static void aDialAtZero(Game g)` |  |

### 6. APPLIED, THE PREVIEW IS THE MODEL (lines 478-542)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 59 | `static void appliedIsTheModel(Game g)` |  |

### 3. THE BUDGET (lines 543-634)

| line | len | member | says |
|---:|---:|---|---|
| 556 | 12 | `static Game budgetTwin(Path root, Game city)` | FS17: a twin of the played city whose every policy line has its base set by hand and is struck by a month at it: the dials put back to the model's defaults (TaxPolicy.reset(), as city() does) and the fuel duty, which ... |
| 569 | 65 | `static void theBudget(Path root, Game city)` |  |

### 4. THE OTHER READS (lines 635-839)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 39 | `static void theOtherReads(Path root, Game g)` |  |
| 692 | 22 | `static Game laddered(Path root, Game city, int slot)` | FS17: a twin of the played city (saved to a slot of the scratch folder and loaded) whose wage ladder is made by hand, so the sections that read the floor against the ladder cause what they read: every job has LADDER_P... |
| 716 | 6 | `static void ladderMonth(Game twin)` | FS17: the labour market's own month on the ladder's figures, every band's posts at par with its supply: the wages walk to their bases and none stays under the floor in force. |
| 733 | 106 | `static void theFloorsMove(Path root, Game city)` | THE FLOOR LIFTS ONLY THE WAGES UNDER IT (WG1; it moved every wage by its own ratio until then): a job's target at a floor is its own, or the floor in today's money where that is more (LabourMarket.targetWageAt() has t... |

### 11. THE MINIMUM-WAGE PAGE (WS1) (lines 840-1161)

| line | len | member | says |
|---:|---:|---|---|
| 843 | 3 | `static boolean same(double a, double b)` | Equal to a part in a billion, or to a billionth outright: the shares and sums of a played city. |
| 848 | 9 | `static double filledWhere(Game g, java.util.function.Predicate<JobType> which)` | The posts filled in the jobs that pass `which`, as PopulationManager.getJobsFilled() counts them (posts x fill rate, unrounded): the hand sum. |
| 865 | 296 | `static void theMinimumWagePage(Path root, Game city)` | THE MINIMUM-WAGE PAGE'S FIGURES (WS1): FloorView is what the page draws and the screen recomputes nothing, so every figure is held to a hand sum of the model's own getters in the played city, and each state the page's... |

### 8. THE FUND'S WITHDRAWAL (0.7.48) (lines 1162-1240)

| line | len | member | says |
|---:|---:|---|---|
| 1164 | 76 | `static void theWithdrawal(Game g)` |  |

### 5. AFTER A LOAD (lines 1241-1260)

| line | len | member | says |
|---:|---:|---|---|
| 1243 | 18 | `static void afterALoad(Path root, Game g)` |  |

### 9. ZONING'S BEFORE AND AFTER (0.7.107) (lines 1261-1316)

| line | len | member | says |
|---:|---:|---|---|
| 1263 | 53 | `static void zoning(Game g)` |  |

### 10. THE PORT DUES (FR4) (lines 1317-1406)

| line | len | member | says |
|---:|---:|---|---|
| 1328 | 9 | `static Game portCity(String label)` | A city with a Bulk Terminal handed to it, the berths' share in force by hand (LandlordPortCheck.seaHanded()), and months played until its berth has a crew and a bulk quote is struck, the share handed again before each... |
| 1338 | 68 | `static void portDues()` |  |

