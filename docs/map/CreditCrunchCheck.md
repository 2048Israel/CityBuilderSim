# CreditCrunchCheck.java - 450 lines · 13 methods · 8 constants · harnesses

`ham/citybuildersim/CreditCrunchCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> A credit crunch and the city's homes (MC1, 2026-11-20): a bank whose
> leverage ratio binds, standing in its band between its leverage minimum and
> its target, the landlords' insured mortgages refused at its door for its
> capital, and the owners asked for what the till lacks on the order.
> 
> WHY: CU2's 80-run ensemble found SHOCK seed 13 with up to 174 households
> with neither a home nor one to double up in (m738-776, F1's one finding):
> every month from m705 to m772 the landlords' plan asked for 20-33 Low-Rise
> Apartments, and the door refused each order for the bank's capital
> (BusinessDebtManager.canFundMortgage(), MORTGAGE_CAPITAL) while its leverage
> ratio bound. One block was built in 70 months. The owners were asked only
> for the down payment, which buys nothing when the bank lends nothing; HN1's
> door buys outright only what the till already holds. Now, while the bank's
> capital rule has no room for the order's mortgage, the owners are asked for
> what the till lacks on the order (Game.consider(), ...AND THE WHOLE ORDER
> WHILE THE BANK IS SHORT; docs/queue/MC1-notes.md has the three candidates).
> This harness builds the crunch itself rather than waiting for a played path
> to reach one: a desk and a bank by hand for the test, and a played city
> whose bank its restore setters hold halfway down its leverage band, its
> homes cut at the start to the doubling valve's last few households.
> 
> What this has to prove:
>   1. the door's capital test asked first (mortgageShortOfCapital()) says
>      what the door says, records nothing, and is no while the risk weights
>      bind, the lender is shut, the landlord is banned or past its default
>      point; the owners are asked for the whole order (ownersAskOnMortgage())
>      only while it says short, on the loan the door is asked for once the
>      down payment is in, never on the cost less a till under the down
>      payment (REV-MC1);
>   2. the fixture: a played city whose bank's leverage ratio binds, held in
>      its band every month of the crunch, its landlords asking for homes the
>      bank has no capital to lend on, its households doubled up to within a
>      few of the valve's ceiling;
>   3. through the crunch the owners put in what the till lacked, homes are
>      built, and no month leaves more than one household with no home
>      (FamilyModel.getStillUnplaced(), LongPlaytest's watch, unmoved); where
>      their issue fell short of the order, the door trimmed for the bank's
>      capital what equity did not cover, and left no such month marked as
>      held for it (HN1, REV-HN1-1; HousingDoorCheck's city covers its orders).

**Uses:** [LongPlaytest](LongPlaytest.md) (29), [BusinessDebtManager](BusinessDebtManager.md) (18), [Bank](Bank.md) (14), [Mortgage](Mortgage.md) (14), [Game](Game.md) (6), [FamilyModel](FamilyModel.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [BuildingManager](BuildingManager.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 72 | 1. THE DOOR'S CAPITAL TEST, ASKED FIRST - a desk and a bank by hand |
| 197 | 2-3. A PLAYED CITY, ITS BANK HELD IN ITS BAND |
| 360 | THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), as |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `CreditCrunchCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 77 | `CreditCrunchCheck.OWED` | `1_000_000, OWNED = 100_000_000` | What the landlords owe before the month, and what they own: room to spare under the default point. |
| 202 | `CreditCrunchCheck.SEED` | `13` | The ensemble's seed CU2 found the crunch on (SHOCK seed 13, the AUTO2 path before its shock). |
| 204 | `CreditCrunchCheck.FROM` | `150, TO = 270` | The crunch, months already run: from the city's first years of growth, before its bank's leverage first binds on its own. |
| 206 | `CreditCrunchCheck.HOLD` | `.5` | Where the bank is held: halfway from its leverage minimum to its target. |
| 208 | `CreditCrunchCheck.MARGIN` | `12` | ★ The households the placement may still take when the crunch starts: the homes cut to the doubling valve's last few. |
| 210 | `CreditCrunchCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 217 | `CreditCrunchCheck.months` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `static PrintStream out` |  |
| 54 | `static int fails` |  |
| 218 | `static long askedThisMonth` |  |
| 219 | `static boolean shortThisMonth` |  |
| 220 | `static double roomAtStart` |  |
| 221 | `static long cutHomes` |  |
| 366 | `static boolean stuck` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 400 | **type** `public final class CreditCrunchCheck` | A credit crunch and the city's homes (MC1, 2026-11-20): a bank whose leverage ratio binds, standing in its band between its leverage minimum and its target, the landlords' insured mortgages refused at its door for its... |
| 58 | 4 | `static void check(String label, boolean ok)` |  |
| 63 | 8 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DOOR'S CAPITAL TEST, ASKED FIRST - a desk and a bank by hand (lines 72-196)

| line | len | member | says |
|---:|---:|---|---|
| 85 | 10 | `static Bank bankAt(double x, double exposure)` | A bank of one branch whose whole exposure is its cash, funded by deposits from abroad: nothing weighs, so its leverage requirement is the larger, and its leverage ratio stands where its liabilities put it - x of the w... |
| 97 | 3 | `static double shortfallFor(double principal)` | The landlords' shortfall whose mortgage's principal is this (Mortgage.loanFor(), the premium added). |
| 102 | 10 | `static BusinessDebtManager deskUnder(Bank bank)` | The landlords' desk, owing OWED on an insured mortgage, under that bank's rule for the month. |
| 114 | 4 | `static boolean door(BusinessDebtManager desk, double principal)` | The door's answer on a mortgage of this principal, on a building costing plenty more. |
| 119 | 77 | `static void theTestAskedFirst()` |  |

### 2-3. A PLAYED CITY, ITS BANK HELD IN ITS BAND (lines 197-359)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 3 | **type** `record Month(int month, double unplaced, double doubled, double households, long homes, int written, boolea...` | One crunch month as it ended. |
| 224 | 6 | `static void holdInBand(Bank bank)` | The restore setters: the bank's cash moved so its leverage ratio stands HOLD of its band up, its book as it is. |
| 232 | 4 | `static double room(Game g)` | The households the placement could still take against the homes standing. |
| 238 | 13 | `static void cutToTheValve(Game g)` | The landlords' homes retired, blocks then houses, while what is left still leaves MARGIN households of room. |
| 252 | 107 | `static void thePlayedCrunch() throws Exception` |  |

### THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), as (lines 360-450)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 12 | `static void run(Game g, int months, Runnable after, Runnable before)` |  |
| 381 | 69 | `static void foundAndPlay(Game g, Runnable after, Runnable before)` |  |

