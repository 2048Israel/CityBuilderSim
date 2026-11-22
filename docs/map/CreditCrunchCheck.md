# CreditCrunchCheck.java - 527 lines · 16 methods · 11 constants · harnesses

`ham/citybuildersim/CreditCrunchCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
>      (FamilyModel.getStillUnplaced(), LongPlaytest's watch, unmoved), and no
>      month the door trimmed is left marked as held for the bank's capital;
>   4. an order of six caused at the crunch's end (REV-FB15), since the
>      played crunch asks for one block at a time once the door prices it:
>      the lender reads it, and the owners are offered what the till lacks,
>      at the door's own price (FB15, HN1-A; red on main's classes); where
>      their issue falls short, the door trims for the bank's capital what
>      equity did not cover, builds what the till buys, and leaves it
>      unmarked (HN1, REV-HN1-1).

**Uses:** [LongPlaytest](LongPlaytest.md) (30), [BusinessDebtManager](BusinessDebtManager.md) (19), [Bank](Bank.md) (14), [Mortgage](Mortgage.md) (14), [Game](Game.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (5), [BusinessInvestment](BusinessInvestment.md) (3), [FamilyModel](FamilyModel.md) (2), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [BuildingManager](BuildingManager.md) (1), [EconomyManager](EconomyManager.md) (1), [Equity](Equity.md) (1), [Investor](Investor.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 77 | 1. THE DOOR'S CAPITAL TEST, ASKED FIRST - a desk and a bank by hand |
| 202 | 2-3. A PLAYED CITY, ITS BANK HELD IN ITS BAND |
| 366 | 4. AN ORDER THE OWNERS' ISSUE FALLS SHORT OF - caused at TO |
| 437 | THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), as |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `CreditCrunchCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 82 | `CreditCrunchCheck.OWED` | `1_000_000, OWNED = 100_000_000` | What the landlords owe before the month, and what they own: room to spare under the default point. |
| 207 | `CreditCrunchCheck.SEED` | `13` | The ensemble's seed CU2 found the crunch on (SHOCK seed 13, the AUTO2 path before its shock). |
| 209 | `CreditCrunchCheck.FROM` | `150, TO = 270` | The crunch, months already run: from the city's first years of growth, before its bank's leverage first binds on its own. |
| 211 | `CreditCrunchCheck.HOLD` | `.5` | Where the bank is held: halfway from its leverage minimum to its target. |
| 213 | `CreditCrunchCheck.MARGIN` | `12` | ★ The households the placement may still take when the crunch starts: the homes cut to the doubling valve's last few. |
| 215 | `CreditCrunchCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 222 | `CreditCrunchCheck.months` | `new ArrayList<>()` |  |
| 371 | `CreditCrunchCheck.ORDER` | `6` | ★ The order caused (REV-FB15): main's seed 13 asked for up to six blocks while its bank was short (m190-194). |
| 373 | `CreditCrunchCheck.TILL` | `1.5` | ★ The till the order meets, in the door's price of one block: one block outright, whatever the issue brings. |
| 375 | `CreditCrunchCheck.MAX_PLOTS` | `60` | The plots the fixture may buy for the order's ground. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static PrintStream out` |  |
| 59 | `static int fails` |  |
| 223 | `static long askedThisMonth` |  |
| 224 | `static boolean shortThisMonth` |  |
| 226 | `static double lenderCost` | What the lender's watch read the last order at, its whole cost (Game.lenderReads()' costOf). |
| 227 | `static double roomAtStart` |  |
| 228 | `static long cutHomes` |  |
| 443 | `static boolean stuck` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 472 | **type** `public final class CreditCrunchCheck` | A credit crunch and the city's homes (MC1, 2026-11-20): a bank whose leverage ratio binds, standing in its band between its leverage minimum and its target, the landlords' insured mortgages refused at its door for its... |
| 63 | 4 | `static void check(String label, boolean ok)` |  |
| 68 | 8 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DOOR'S CAPITAL TEST, ASKED FIRST - a desk and a bank by hand (lines 77-201)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 10 | `static Bank bankAt(double x, double exposure)` | A bank of one branch whose whole exposure is its cash, funded by deposits from abroad: nothing weighs, so its leverage requirement is the larger, and its leverage ratio stands where its liabilities put it - x of the w... |
| 102 | 3 | `static double shortfallFor(double principal)` | The landlords' shortfall whose mortgage's principal is this (Mortgage.loanFor(), the premium added). |
| 107 | 10 | `static BusinessDebtManager deskUnder(Bank bank)` | The landlords' desk, owing OWED on an insured mortgage, under that bank's rule for the month. |
| 119 | 4 | `static boolean door(BusinessDebtManager desk, double principal)` | The door's answer on a mortgage of this principal, on a building costing plenty more. |
| 124 | 77 | `static void theTestAskedFirst()` |  |

### 2-3. A PLAYED CITY, ITS BANK HELD IN ITS BAND (lines 202-365)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 3 | **type** `record Month(int month, double unplaced, double doubled, double households, long homes, int written, boolea...` | One crunch month as it ended. |
| 231 | 6 | `static void holdInBand(Bank bank)` | The restore setters: the bank's cash moved so its leverage ratio stands HOLD of its band up, its book as it is. |
| 239 | 4 | `static double room(Game g)` | The households the placement could still take against the homes standing. |
| 245 | 13 | `static void cutToTheValve(Game g)` | The landlords' homes retired, blocks then houses, while what is left still leaves MARGIN households of room. |
| 259 | 106 | `static void thePlayedCrunch() throws Exception` |  |

### 4. AN ORDER THE OWNERS' ISSUE FALLS SHORT OF - caused at TO (lines 366-436)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 41 | `static void theShortIssue(Game g) throws Exception` | Since FB15 the played crunch asks for one block at a time while the bank is short, and an order of one has no half to trim to: the door's trim for the capital where the owners' issue fell short (MC1, HN1) would be wat... |
| 429 | 3 | `static double door(Game g, BuildingsTemplate t, long n)` | What buildFor() charges for n: the builders' quote and the ground at its zone's price (Game.doorPriceOf() since FB15). |
| 433 | 3 | `static boolean ground(Game g, BuildingsTemplate t, long n)` |  |

### THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), as (lines 437-527)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 12 | `static void run(Game g, int months, Runnable after, Runnable before)` |  |
| 458 | 69 | `static void foundAndPlay(Game g, Runnable after, Runnable before)` |  |

