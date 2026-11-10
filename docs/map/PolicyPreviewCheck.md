# PolicyPreviewCheck.java - 1,182 lines · 23 methods · 3 constants · harnesses

`ham/citybuildersim/PolicyPreviewCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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
>      twelfth, which is what twelve of the model's own withdrawals leave.
>   9. ZONING'S BEFORE AND AFTER (0.7.107, batch Z1): a zone and a forest
>      law staged on a copy move nothing in the city, and applied read as
>      the copy did - what the zone holds, what that ground would fetch, the
>      price given up, the forest's three kinds and the ground free for
>      anyone; and a zone's tax relief is the property tax un-zoning it
>      would raise.
>  10. THE PORT DUES (FR4; the dial is FR3's): in a city whose berth has
>      worked, the dues previewed at the city's own dial are the month's,
>      Freight's and the treasury's, to the bit; at 2 twice that, which is
>      what a twin that ran the month at 2 paid, to the cent; at 0 none, and
>      the budget moves by the dues' move alone; a tonne's handling at a dial
> ... (4 more lines in the source)

**Uses:** [PolicyPreview](PolicyPreview.md) (91), [FloorView](FloorView.md) (55), [Sectors](Sectors.md) (30), [JobType](JobType.md) (28), [Game](Game.md) (27), [LabourMarket](LabourMarket.md) (26), [TaxPolicy](TaxPolicy.md) (23), [WageBand](WageBand.md) (19), [Formats](Formats.md) (14), [TreasuryFund](TreasuryFund.md) (9), [EconomyManager](EconomyManager.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (7), [CentralBank](CentralBank.md) (7), [WorldMeasures](WorldMeasures.md) (7), [Ports](Ports.md) (7), [GameFiles](GameFiles.md) (6), [Zoning](Zoning.md) (5), [PriceLevel](PriceLevel.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [BuildingType](BuildingType.md) (4), [LongPlaytest](LongPlaytest.md) (3), [Sector](Sector.md) (3), [DebtManager](DebtManager.md) (3), [Unemployment](Unemployment.md) (2), [PopulationManager](PopulationManager.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [Research](Research.md) (2), [LandManager](LandManager.md) (2), [HaulageCheck](HaulageCheck.md) (2), [PortDivision](PortDivision.md) (2)... and 9 more

## Sections

| line | section |
|---:|---|
| 263 | 1. THE COPY |
| 316 | 2. AT THE CITY'S OWN POLICY |
| 394 | 7. A DIAL AT ZERO |
| 436 | 6. APPLIED, THE PREVIEW IS THE MODEL |
| 494 | 3. THE BUDGET |
| 553 | 4. THE OTHER READS |
| 698 | 11. THE MINIMUM-WAGE PAGE (WS1) |
| 1004 | 8. THE FUND'S WITHDRAWAL (0.7.48) |
| 1035 | 5. AFTER A LOAD |
| 1055 | 9. ZONING'S BEFORE AND AFTER (0.7.107) |
| 1111 | 10. THE PORT DUES (FR4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 184 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "800" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders (the houses 800 since PT1-M4: see city()). |
| 250 | `PolicyPreviewCheck.EI_POOL_ROUNDS` | `6` | The most employers anEiPool() closes before it gives up on a pool (IC1): the city with a post for everyone is closed into one in two or three. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |
| 80 | `static PrintStream out` |  |
| 81 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 1106 | **type** `public class PolicyPreviewCheck` | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. |
| 83 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 91 | 1 | `static boolean near(double a, double b)` |  |
| 93 | 5 | `static void quietly(Runnable r)` |  |
| 141 | 41 | `static Game city(Path root)` | A played city with every line of the Policy tab in it: homes, shops, a diner and builders; a mine, a foundry and a fabrication shop; power, water and roads; a commercial bank (its profit tax), clinics (their fees) and... |
| 190 | 24 | `public static void main(String[] args) throws Exception` |  |
| 221 | 27 | `static void anEiPool(Game g)` | An EI pool that stays, caused. |
| 253 | 9 | `static void printCity(Game g)` | The city as the previews read it, for the record. |

### 1. THE COPY (lines 263-315)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 50 | `static void theCopy(Game g)` |  |

### 2. AT THE CITY'S OWN POLICY (lines 316-393)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 69 | `static void atTheCitysOwn(Game g, String section)` |  |
| 388 | 5 | `static double sum(java.util.Map<String, Double> m)` |  |

### 7. A DIAL AT ZERO (lines 394-435)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 39 | `static void aDialAtZero(Game g)` |  |

### 6. APPLIED, THE PREVIEW IS THE MODEL (lines 436-493)

| line | len | member | says |
|---:|---:|---|---|
| 438 | 55 | `static void appliedIsTheModel(Game g)` |  |

### 3. THE BUDGET (lines 494-552)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 56 | `static void theBudget(Game g)` |  |

### 4. THE OTHER READS (lines 553-697)

| line | len | member | says |
|---:|---:|---|---|
| 555 | 32 | `static void theOtherReads(Game g)` |  |
| 595 | 102 | `static void theFloorsMove(Game g)` | THE FLOOR LIFTS ONLY THE WAGES UNDER IT (WG1; it moved every wage by its own ratio until then): a job's target at a floor is its own, or the floor in today's money where that is more (LabourMarket.targetWageAt() has t... |

### 11. THE MINIMUM-WAGE PAGE (WS1) (lines 698-1003)

| line | len | member | says |
|---:|---:|---|---|
| 701 | 3 | `static boolean same(double a, double b)` | Equal to a part in a billion, or to a billionth outright: the shares and sums of a played city. |
| 706 | 9 | `static double filledWhere(Game g, java.util.function.Predicate<JobType> which)` | The posts filled in the jobs that pass `which`, as PopulationManager.getJobsFilled() counts them (posts x fill rate, unrounded): the hand sum. |
| 723 | 280 | `static void theMinimumWagePage(Path root, Game g)` | THE MINIMUM-WAGE PAGE'S FIGURES (WS1): FloorView is what the page draws and the screen recomputes nothing, so every figure is held to a hand sum of the model's own getters in the played city, and each state the page's... |

### 8. THE FUND'S WITHDRAWAL (0.7.48) (lines 1004-1034)

| line | len | member | says |
|---:|---:|---|---|
| 1006 | 28 | `static void theWithdrawal(Game g)` |  |

### 5. AFTER A LOAD (lines 1035-1054)

| line | len | member | says |
|---:|---:|---|---|
| 1037 | 18 | `static void afterALoad(Path root, Game g)` |  |

### 9. ZONING'S BEFORE AND AFTER (0.7.107) (lines 1055-1110)

| line | len | member | says |
|---:|---:|---|---|
| 1057 | 53 | `static void zoning(Game g)` |  |

### 10. THE PORT DUES (FR4) (lines 1111-1182)

| line | len | member | says |
|---:|---:|---|---|
| 1114 | 8 | `static Game portCity(String label)` | A city with a Bulk Terminal handed to it and three months played on, so a berth has worked and Freight has billed its tonnes (LandlordPortCheck.seaBase()'s recipe). |
| 1123 | 59 | `static void portDues()` |  |

