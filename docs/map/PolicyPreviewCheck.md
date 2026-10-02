# PolicyPreviewCheck.java - 470 lines · 14 methods · 2 constants · harnesses

`ham/citybuildersim/PolicyPreviewCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
>      clinic's fee by the fees at one.
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
> 
> Every fixture causes its condition.

**Uses:** [PolicyPreview](PolicyPreview.md) (28), [TaxPolicy](TaxPolicy.md) (16), [Sectors](Sectors.md) (15), [Game](Game.md) (14), [Formats](Formats.md) (7), [CentralBank](CentralBank.md) (7), [EconomyManager](EconomyManager.md) (6), [HouseholdAccounts](HouseholdAccounts.md) (5), [WageBand](WageBand.md) (4), [GameFiles](GameFiles.md) (3), [DebtManager](DebtManager.md) (3), [Sector](Sector.md) (2), [Unemployment](Unemployment.md) (2), [LabourMarket](LabourMarket.md) (2), [JobType](JobType.md) (2), [Founding](Founding.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LongPlaytest](LongPlaytest.md) (1), [CityNeeds](CityNeeds.md) (1), [SectorBooks](SectorBooks.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [SocialSecurity](SocialSecurity.md) (1), [Bank](Bank.md) (1)

## Sections

| line | section |
|---:|---|
| 139 | 1. THE COPY |
| 192 | 2. AT THE CITY'S OWN POLICY |
| 270 | 7. A DIAL AT ZERO |
| 312 | 6. APPLIED, THE PREVIEW IS THE MODEL |
| 370 | 3. THE BUDGET |
| 410 | 4. THE OTHER READS |
| 450 | 5. AFTER A LOAD |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 102 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "500" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 58 | `static PrintStream out` |  |
| 59 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 416 | **type** `public class PolicyPreviewCheck` | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. |
| 61 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 69 | 1 | `static boolean near(double a, double b)` |  |
| 71 | 5 | `static void quietly(Runnable r)` |  |
| 84 | 16 | `static Game city(Path root)` | A played city with every line of the Policy tab in it: homes, shops, a diner and builders; a mine, a foundry and a fabrication shop; power, water and roads; a commercial bank (its profit tax), clinics (their fees) and... |
| 108 | 19 | `public static void main(String[] args) throws Exception` |  |
| 129 | 9 | `static void printCity(Game g)` | The city as the previews read it, for the record. |

### 1. THE COPY (lines 139-191)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 50 | `static void theCopy(Game g)` |  |

### 2. AT THE CITY'S OWN POLICY (lines 192-269)

| line | len | member | says |
|---:|---:|---|---|
| 194 | 69 | `static void atTheCitysOwn(Game g, String section)` |  |
| 264 | 5 | `static double sum(java.util.Map<String, Double> m)` |  |

### 7. A DIAL AT ZERO (lines 270-311)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 39 | `static void aDialAtZero(Game g)` |  |

### 6. APPLIED, THE PREVIEW IS THE MODEL (lines 312-369)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 55 | `static void appliedIsTheModel(Game g)` |  |

### 3. THE BUDGET (lines 370-409)

| line | len | member | says |
|---:|---:|---|---|
| 372 | 37 | `static void theBudget(Game g)` |  |

### 4. THE OTHER READS (lines 410-449)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 37 | `static void theOtherReads(Game g)` |  |

### 5. AFTER A LOAD (lines 450-470)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 18 | `static void afterALoad(Path root, Game g)` |  |

