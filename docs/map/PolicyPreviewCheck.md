# PolicyPreviewCheck.java - 1,224 lines · 23 methods · 3 constants · harnesses

`ham/citybuildersim/PolicyPreviewCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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
> ... (6 more lines in the source)

**Uses:** [PolicyPreview](PolicyPreview.md) (95), [FloorView](FloorView.md) (55), [Sectors](Sectors.md) (30), [JobType](JobType.md) (28), [Game](Game.md) (27), [LabourMarket](LabourMarket.md) (26), [TaxPolicy](TaxPolicy.md) (23), [TreasuryFund](TreasuryFund.md) (23), [WageBand](WageBand.md) (19), [Formats](Formats.md) (14), [EconomyManager](EconomyManager.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (7), [CentralBank](CentralBank.md) (7), [WorldMeasures](WorldMeasures.md) (7), [Ports](Ports.md) (7), [GameFiles](GameFiles.md) (6), [Zoning](Zoning.md) (5), [PriceLevel](PriceLevel.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [BuildingType](BuildingType.md) (4), [LongPlaytest](LongPlaytest.md) (3), [Sector](Sector.md) (3), [DebtManager](DebtManager.md) (3), [Unemployment](Unemployment.md) (2), [PopulationManager](PopulationManager.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [Research](Research.md) (2), [LandManager](LandManager.md) (2), [HaulageCheck](HaulageCheck.md) (2), [PortDivision](PortDivision.md) (2)... and 9 more

**Used by (1):** [GovernmentViewCheck](GovernmentViewCheck.md)

## Sections

| line | section |
|---:|---|
| 265 | 1. THE COPY |
| 318 | 2. AT THE CITY'S OWN POLICY |
| 396 | 7. A DIAL AT ZERO |
| 438 | 6. APPLIED, THE PREVIEW IS THE MODEL |
| 496 | 3. THE BUDGET |
| 555 | 4. THE OTHER READS |
| 700 | 11. THE MINIMUM-WAGE PAGE (WS1) |
| 1006 | 8. THE FUND'S WITHDRAWAL (0.7.48) |
| 1077 | 5. AFTER A LOAD |
| 1097 | 9. ZONING'S BEFORE AND AFTER (0.7.107) |
| 1153 | 10. THE PORT DUES (FR4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 91 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 186 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "800" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders (the houses 800 since PT1-M4: see city()). |
| 252 | `PolicyPreviewCheck.EI_POOL_ROUNDS` | `6` | The most employers anEiPool() closes before it gives up on a pool (IC1): the city with a post for everyone is closed into one in two or three. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `static int fails` |  |
| 82 | `static PrintStream out` |  |
| 83 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 1146 | **type** `public class PolicyPreviewCheck` | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. |
| 85 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 93 | 1 | `static boolean near(double a, double b)` |  |
| 95 | 5 | `static void quietly(Runnable r)` |  |
| 143 | 41 | `static Game city(Path root)` | A played city with every line of the Policy tab in it: homes, shops, a diner and builders; a mine, a foundry and a fabrication shop; power, water and roads; a commercial bank (its profit tax), clinics (their fees) and... |
| 192 | 24 | `public static void main(String[] args) throws Exception` |  |
| 223 | 27 | `static void anEiPool(Game g)` | An EI pool that stays, caused. |
| 255 | 9 | `static void printCity(Game g)` | The city as the previews read it, for the record. |

### 1. THE COPY (lines 265-317)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 50 | `static void theCopy(Game g)` |  |

### 2. AT THE CITY'S OWN POLICY (lines 318-395)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 69 | `static void atTheCitysOwn(Game g, String section)` |  |
| 390 | 5 | `static double sum(java.util.Map<String, Double> m)` |  |

### 7. A DIAL AT ZERO (lines 396-437)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 39 | `static void aDialAtZero(Game g)` |  |

### 6. APPLIED, THE PREVIEW IS THE MODEL (lines 438-495)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 55 | `static void appliedIsTheModel(Game g)` |  |

### 3. THE BUDGET (lines 496-554)

| line | len | member | says |
|---:|---:|---|---|
| 498 | 56 | `static void theBudget(Game g)` |  |

### 4. THE OTHER READS (lines 555-699)

| line | len | member | says |
|---:|---:|---|---|
| 557 | 32 | `static void theOtherReads(Game g)` |  |
| 597 | 102 | `static void theFloorsMove(Game g)` | THE FLOOR LIFTS ONLY THE WAGES UNDER IT (WG1; it moved every wage by its own ratio until then): a job's target at a floor is its own, or the floor in today's money where that is more (LabourMarket.targetWageAt() has t... |

### 11. THE MINIMUM-WAGE PAGE (WS1) (lines 700-1005)

| line | len | member | says |
|---:|---:|---|---|
| 703 | 3 | `static boolean same(double a, double b)` | Equal to a part in a billion, or to a billionth outright: the shares and sums of a played city. |
| 708 | 9 | `static double filledWhere(Game g, java.util.function.Predicate<JobType> which)` | The posts filled in the jobs that pass `which`, as PopulationManager.getJobsFilled() counts them (posts x fill rate, unrounded): the hand sum. |
| 725 | 280 | `static void theMinimumWagePage(Path root, Game g)` | THE MINIMUM-WAGE PAGE'S FIGURES (WS1): FloorView is what the page draws and the screen recomputes nothing, so every figure is held to a hand sum of the model's own getters in the played city, and each state the page's... |

### 8. THE FUND'S WITHDRAWAL (0.7.48) (lines 1006-1076)

| line | len | member | says |
|---:|---:|---|---|
| 1008 | 68 | `static void theWithdrawal(Game g)` |  |

### 5. AFTER A LOAD (lines 1077-1096)

| line | len | member | says |
|---:|---:|---|---|
| 1079 | 18 | `static void afterALoad(Path root, Game g)` |  |

### 9. ZONING'S BEFORE AND AFTER (0.7.107) (lines 1097-1152)

| line | len | member | says |
|---:|---:|---|---|
| 1099 | 53 | `static void zoning(Game g)` |  |

### 10. THE PORT DUES (FR4) (lines 1153-1224)

| line | len | member | says |
|---:|---:|---|---|
| 1156 | 8 | `static Game portCity(String label)` | A city with a Bulk Terminal handed to it and three months played on, so a berth has worked and Freight has billed its tonnes (LandlordPortCheck.seaBase()'s recipe). |
| 1165 | 59 | `static void portDues()` |  |

