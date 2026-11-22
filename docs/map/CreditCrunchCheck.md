# CreditCrunchCheck.java - 637 lines · 18 methods · 13 constants · harnesses

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
> WHY THE HOLD MOVES CASH AND DEPOSITS FROM ABROAD TOGETHER (FS9: the
> played city moved under it with HN1-A, seed 13 standing at 483
> households at m150 where it stood at 930): the desks' capital rule is
> struck inside the month, in Game.startOfMonthUpdate() (strikeBankBonds(),
> lendingGrowthLimit(), lendsOnlyToKeepBorrowersGoing(), leverageBinds()),
> and the bank's equity and exposure hardly move between the hold before
> the month and that strike. What had moved was the book: it weighs nearly
> all of the bank's exposure, so the risk-based requirement (CAPITAL_RATIO
> of the weighted book) was the larger of the two, and a hold by cash alone
> set the leverage ratio to the band while the leverage requirement did not
> bind: the rule was the risk weights', the bank under its risk minimum
> and lending only to keep borrowers going, most months. The hold now
> brings the exposure to BINDS_BY times what the risk weights ask (cash,
> funded by deposits from abroad, which weigh nothing) and sets the equity
> by the deposits, so the leverage requirement is the larger and the ratio
> stands in its band at the strike. Game.nextMonth() sets the deposits
> from abroad back to the hot money's stock after the strike, so the bank
> a month ends on is not the one its rule was struck on: section 4 holds
> and strikes again before its order.
> 
> What this has to prove:
>   1. the door's capital test asked first (mortgageShortOfCapital()) says
>      what the door says, records nothing, and is no while the risk weights
>      bind, the lender is shut, the landlord is banned or past its default
>      point; the owners are asked for the whole order (ownersAskOnMortgage())
>      only while it says short, on the loan the door is asked for once the
>      down payment is in, never on the cost less a till under the down
>      payment (REV-MC1);
>   2. the fixture: a played city whose bank's leverage requirement is the
>      larger and its ratio held in its band before every month of the crunch
>      (so the rule the month strikes is the band's), its landlords asking for homes the
>      bank has no capital to lend on, ground for GROUND_BLOCKS of their blocks
>      stood before every month (FS9: HT1's path left them none for 25 months),
>      its households doubled up to within a few of the valve's ceiling;
>   3. through the crunch the owners put in what the till lacked, homes are
>      built, and no month leaves more than one household with no home
>      (FamilyModel.getStillUnplaced(), LongPlaytest's watch, unmoved), and no
>      month the door trimmed is left marked as held for the bank's capital;
>   4. an order of six caused at the crunch's end (REV-FB15), since the
> ... (10 more lines in the source)

**Uses:** [LongPlaytest](LongPlaytest.md) (31), [Bank](Bank.md) (20), [BusinessDebtManager](BusinessDebtManager.md) (20), [Game](Game.md) (17), [Mortgage](Mortgage.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (6), [BusinessInvestment](BusinessInvestment.md) (3), [FamilyModel](FamilyModel.md) (2), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [BuildingManager](BuildingManager.md) (1), [EconomyManager](EconomyManager.md) (1), [Equity](Equity.md) (1), [Investor](Investor.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 103 | 1. THE DOOR'S CAPITAL TEST, ASKED FIRST - a desk and a bank by hand |
| 228 | 2-3. A PLAYED CITY, ITS BANK HELD IN ITS BAND |
| 436 | 4. AN ORDER THE OWNERS' ISSUE FALLS SHORT OF - caused at TO |
| 547 | THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), as |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `CreditCrunchCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 108 | `CreditCrunchCheck.OWED` | `1_000_000, OWNED = 100_000_000` | What the landlords owe before the month, and what they own: room to spare under the default point. |
| 233 | `CreditCrunchCheck.SEED` | `13` | The ensemble's seed CU2 found the crunch on (SHOCK seed 13, the AUTO2 path before its shock). |
| 235 | `CreditCrunchCheck.FROM` | `150, TO = 270` | The crunch, months already run: from the city's first years of growth, before its bank's leverage first binds on its own. |
| 237 | `CreditCrunchCheck.HOLD` | `.5` | Where the bank is held: halfway from its leverage minimum to its target. |
| 239 | `CreditCrunchCheck.MARGIN` | `12` | ★ The households the placement may still take when the crunch starts: the homes cut to the doubling valve's last few. |
| 241 | `CreditCrunchCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 248 | `CreditCrunchCheck.months` | `new ArrayList<>()` |  |
| 257 | `CreditCrunchCheck.BINDS_BY` | `1.25` | ★ How far over the risk-based requirement the hold puts the leverage requirement: the book may weigh a quarter more by the strike. |
| 279 | `CreditCrunchCheck.GROUND_BLOCKS` | `CreditCrunchCheck.ORDER` | ★ The ground stood before each crunch month, in blocks of Low-Rise Apartments: section 4's order, so the landlords never wait on land. |
| 441 | `CreditCrunchCheck.ORDER` | `6` | ★ The order caused (REV-FB15): main's seed 13 asked for up to six blocks while its bank was short (m190-194). |
| 443 | `CreditCrunchCheck.TILL` | `1.5` | ★ The till the order meets, in the door's price of one block: one block outright, whatever the issue brings. |
| 445 | `CreditCrunchCheck.MAX_PLOTS` | `60` | The plots the fixture may buy for the order's ground. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `static PrintStream out` |  |
| 85 | `static int fails` |  |
| 249 | `static long askedThisMonth` |  |
| 250 | `static boolean shortThisMonth` |  |
| 252 | `static double lenderCost` | What the lender's watch read the last order at, its whole cost (Game.lenderReads()' costOf). |
| 253 | `static double roomAtStart` |  |
| 254 | `static long cutHomes` |  |
| 259 | `static double heldRatio` | The leverage ratio the last hold left, which is what the month's rule is struck on (the month's end reads the hot money's reset). |
| 281 | `static int groundMonths, groundPlots` | The crunch months that began with ground for GROUND_BLOCKS blocks, and the plots bought for it. |
| 553 | `static boolean stuck` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 556 | **type** `public final class CreditCrunchCheck` | A credit crunch and the city's homes (MC1, 2026-11-20): a bank whose leverage ratio binds, standing in its band between its leverage minimum and its target, the landlords' insured mortgages refused at its door for its... |
| 89 | 4 | `static void check(String label, boolean ok)` |  |
| 94 | 8 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DOOR'S CAPITAL TEST, ASKED FIRST - a desk and a bank by hand (lines 103-227)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 10 | `static Bank bankAt(double x, double exposure)` | A bank of one branch whose whole exposure is its cash, funded by deposits from abroad: nothing weighs, so its leverage requirement is the larger, and its leverage ratio stands where its liabilities put it - x of the w... |
| 128 | 3 | `static double shortfallFor(double principal)` | The landlords' shortfall whose mortgage's principal is this (Mortgage.loanFor(), the premium added). |
| 133 | 10 | `static BusinessDebtManager deskUnder(Bank bank)` | The landlords' desk, owing OWED on an insured mortgage, under that bank's rule for the month. |
| 145 | 4 | `static boolean door(BusinessDebtManager desk, double principal)` | The door's answer on a mortgage of this principal, on a building costing plenty more. |
| 150 | 77 | `static void theTestAskedFirst()` |  |

### 2-3. A PLAYED CITY, ITS BANK HELD IN ITS BAND (lines 228-435)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 3 | **type** `record Month(int month, double unplaced, double doubled, double households, long homes, int written, boolea...` | One crunch month as it ended. |
| 268 | 9 | `static void holdInBand(Bank bank)` | The restore setters: the bank's cash and its deposits from abroad moved, its book as it is, so that its leverage requirement is the larger (BINDS_BY over the risk-based one: the cash and the deposits both come in at n... |
| 289 | 8 | `static void standGround(Game g)` | The city buys the cheapest plots (Game.buyLandBlock(), the player's own purchase, MAX_PLOTS at most a month) until the landlords' zone holds GROUND_BLOCKS blocks. |
| 299 | 4 | `static double room(Game g)` | The households the placement could still take against the homes standing. |
| 305 | 13 | `static void cutToTheValve(Game g)` | The landlords' homes retired, blocks then houses, while what is left still leaves MARGIN households of room. |
| 319 | 116 | `static void thePlayedCrunch() throws Exception` |  |

### 4. AN ORDER THE OWNERS' ISSUE FALLS SHORT OF - caused at TO (lines 436-546)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 61 | `static void theShortIssue(Game g) throws Exception` | Since FB15 the played crunch asks for one block at a time while the bank is short, and an order of one has no half to trim to: the door's trim for the capital where the owners' issue fell short (MC1, HN1) would be wat... |
| 525 | 12 | `static void strikeTheRule(Game g) throws Exception` | The desks' capital rule struck from the bank as it stands, by the calls Game.startOfMonthUpdate() makes (strikeBankBonds() is its own). |
| 539 | 3 | `static double door(Game g, BuildingsTemplate t, long n)` | What buildFor() charges for n: the builders' quote and the ground at its zone's price (Game.doorPriceOf() since FB15). |
| 543 | 3 | `static boolean ground(Game g, BuildingsTemplate t, long n)` |  |

### THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), as (lines 547-637)

| line | len | member | says |
|---:|---:|---|---|
| 555 | 12 | `static void run(Game g, int months, Runnable after, Runnable before)` |  |
| 568 | 69 | `static void foundAndPlay(Game g, Runnable after, Runnable before)` |  |

