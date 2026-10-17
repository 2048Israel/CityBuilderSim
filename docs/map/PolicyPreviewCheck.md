# PolicyPreviewCheck.java - 602 lines · 16 methods · 2 constants · harnesses

`ham/citybuildersim/PolicyPreviewCheck.java` - generated 2026-10-17 by CodeMap; line numbers are as of that run.

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
>      setting; the floor's cash and founding figures one way and back; every
>      wage's target moving by the floor's own ratio.
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
> 
> Every fixture causes its condition.

**Uses:** [PolicyPreview](PolicyPreview.md) (54), [Sectors](Sectors.md) (27), [TaxPolicy](TaxPolicy.md) (17), [Game](Game.md) (16), [Formats](Formats.md) (11), [TreasuryFund](TreasuryFund.md) (9), [CentralBank](CentralBank.md) (7), [EconomyManager](EconomyManager.md) (6), [HouseholdAccounts](HouseholdAccounts.md) (5), [Zoning](Zoning.md) (5), [WageBand](WageBand.md) (4), [GameFiles](GameFiles.md) (3), [LongPlaytest](LongPlaytest.md) (3), [Sector](Sector.md) (3), [DebtManager](DebtManager.md) (3), [Unemployment](Unemployment.md) (2), [LabourMarket](LabourMarket.md) (2), [JobType](JobType.md) (2), [LandManager](LandManager.md) (2), [Founding](Founding.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [CityNeeds](CityNeeds.md) (1), [SectorBooks](SectorBooks.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [SocialSecurity](SocialSecurity.md) (1), [Bank](Bank.md) (1)

## Sections

| line | section |
|---:|---|
| 162 | 1. THE COPY |
| 215 | 2. AT THE CITY'S OWN POLICY |
| 293 | 7. A DIAL AT ZERO |
| 335 | 6. APPLIED, THE PREVIEW IS THE MODEL |
| 393 | 3. THE BUDGET |
| 452 | 4. THE OTHER READS |
| 496 | 8. THE FUND'S WITHDRAWAL (0.7.48) |
| 527 | 5. AFTER A LOAD |
| 547 | 9. ZONING'S BEFORE AND AFTER (0.7.107) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 123 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `static int fails` |  |
| 72 | `static PrintStream out` |  |
| 73 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 69 | 534 | **type** `public class PolicyPreviewCheck` | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. |
| 75 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 83 | 1 | `static boolean near(double a, double b)` |  |
| 85 | 5 | `static void quietly(Runnable r)` |  |
| 105 | 16 | `static Game city(Path root)` | A played city with every line of the Policy tab in it: homes, shops, a diner and builders; a mine, a foundry and a fabrication shop; power, water and roads; a commercial bank (its profit tax), clinics (their fees) and... |
| 129 | 21 | `public static void main(String[] args) throws Exception` |  |
| 152 | 9 | `static void printCity(Game g)` | The city as the previews read it, for the record. |

### 1. THE COPY (lines 162-214)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 50 | `static void theCopy(Game g)` |  |

### 2. AT THE CITY'S OWN POLICY (lines 215-292)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 69 | `static void atTheCitysOwn(Game g, String section)` |  |
| 287 | 5 | `static double sum(java.util.Map<String, Double> m)` |  |

### 7. A DIAL AT ZERO (lines 293-334)

| line | len | member | says |
|---:|---:|---|---|
| 295 | 39 | `static void aDialAtZero(Game g)` |  |

### 6. APPLIED, THE PREVIEW IS THE MODEL (lines 335-392)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 55 | `static void appliedIsTheModel(Game g)` |  |

### 3. THE BUDGET (lines 393-451)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 56 | `static void theBudget(Game g)` |  |

### 4. THE OTHER READS (lines 452-495)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 41 | `static void theOtherReads(Game g)` |  |

### 8. THE FUND'S WITHDRAWAL (0.7.48) (lines 496-526)

| line | len | member | says |
|---:|---:|---|---|
| 498 | 28 | `static void theWithdrawal(Game g)` |  |

### 5. AFTER A LOAD (lines 527-546)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 18 | `static void afterALoad(Path root, Game g)` |  |

### 9. ZONING'S BEFORE AND AFTER (0.7.107) (lines 547-602)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 53 | `static void zoning(Game g)` |  |

