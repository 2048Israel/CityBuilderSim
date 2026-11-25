# FundCheck.java - 1,978 lines · 37 methods · 4 constants · harnesses

`ham/citybuildersim/FundCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). Not part of the game.
> 
> WHY THIS EXISTS. Until 0.7.14 the city rescued its bank with a gift - capital
> for no shares, the owners keeping theirs, the hole "absorbed from outside"
> by creditors who were the central bank's window - and could buy nothing of
> its own city's companies. Jerus: "City takes the shares", "Treasury setting,
> auto", "Preferred shares", "Rule plus your hand", "New issues only",
> "Central bank advances it", a dial "default 0, max 300%" of "the year's
> surplus", "Home only", and an Insane start: "0 cash, 0 vault, and a 20y bond
> 3% for the initial land cost". Each is a rule with a source, and each is a
> fixture here that causes the condition rather than waiting for a run to.
> 
> What it has to prove, a section each:
> 
>   1. A failed bank, automatic: resolved the month it fails - every share
>      the city's, taken from the households, the world and the fund's own
>      market book; the city paid the hole and the capital to reopen, from
>      its cash first and the rest advanced by the central bank past its
>      ceiling; the bank open again the same month; nothing from outside the
>      city, the world's loss a valuation; the audit closes. Twelve banks
>      frozen on holes in the trillions are each resolved once, and open.
>   2. On the button: frozen, carrying its hole, until pressed - then the same.
>   3. Dilution: the bank's new shares go to others, the city's stake falls,
>      and the fund never takes a new issue (the book passes it over).
>   4. The preferred, TARP's terms: sized within 1-3% of risk-weighted
>      assets, 3% and the rest left to its own share issues when that does
>      not reach its target; 5%, then 9% from the fifth anniversary; arrears
>      block the common dividend, and nothing is paid under the target; no
>      buybacks and no rise in the common dividend a share for three years;
>      warrants struck at last month's price, exercised at expiry if still
>      out and in the money; cancelled at a failure.
>   5. Repaid at its third anniversary (Jerus: "Sell new shares to repay"):
>      whole, at par with its unpaid dividends, from the bank's capital over
>      its target and then an offering of new common to the public - none
>      to the city, whose stake falls by exactly the dilution; under its
>      target it is repaid all the same and its equity ends where it was;
>      the warrants bought back at their Black-Scholes value after the last
>      block; the equity's parts move by exactly those causes; an offering
>      half taken up pays half and the rest the next month; through a save;
>      a failed bank repays nothing.
>   6. The offer: raised at the month's end, in the inbox; declined, asked
>      again a quarter later; a pending offer survives a save.
>   7. The dial: 0, 100% and 300% of a year's surplus; the one-month floor; a
>      deficit year saves nothing; the surplus used once with the rollover,
>      the dial's share first.
>   8. The rule: 70/30, the 10% limit, the rebalancing band, cash that
>      cannot be placed waits, and a holding over 10% is asked down to the
>      limit and no further; since 0.7.48 (C3) its bid stands at the desk's
>      ask.
>  8b. The city's mark (0.7.48, C4): a holding whose quote is a last trade
>      older than Exchange.STALE_MARK_MONTHS is marked at fair value; one
>      whose book has a bid and an ask resting is marked at that quote
>      however old the last trade (EM2); trading reads the quote.
>   9. The transfer at the default withdrawal, Norway's 3% a year, from cash
>      only.
>  9b. The withdrawal dial (0.7.48, C1; Jerus: "even 0 or 10% a month"): at
>      the default it is the transfer to the bit; whole steps from nothing
>      to MAX_WITHDRAWAL_STEPS; at nothing it pays and sells nothing; over
>      the default what the cash cannot cover is sold from the market book
>      at the step, pro rata - since 0.7.102 asked at the desk's bid (A21),
> ... (40 more lines in the source)

**Uses:** [TreasuryFund](TreasuryFund.md) (125), [Equity](Equity.md) (85), [Game](Game.md) (84), [Bank](Bank.md) (48), [Exchange](Exchange.md) (41), [OrderBook](OrderBook.md) (28), [Founding](Founding.md) (17), [PriceLevel](PriceLevel.md) (16), [Household](Household.md) (13), [ShortSellingCheck](ShortSellingCheck.md) (10), [MoneyAudit](MoneyAudit.md) (7), [GameFiles](GameFiles.md) (5), [LongTermBond](LongTermBond.md) (5), [CityLand](CityLand.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [Sectors](Sectors.md) (4), [LandMarket](LandMarket.md) (4), [FundLedger](FundLedger.md) (3), [CorporateBond](CorporateBond.md) (3), [BondMarket](BondMarket.md) (3), [LandManager](LandManager.md) (3), [TreasuryLine](TreasuryLine.md) (3), [CentralBank](CentralBank.md) (2), [JobType](JobType.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [FundView](FundView.md) (2), [PolicyPreview](PolicyPreview.md) (2), [NationalAccounts](NationalAccounts.md) (2), [WorldEconomy](WorldEconomy.md) (2), [Debt](Debt.md) (2)... and 4 more

**Used by (1):** [FundLedgerCheck](FundLedgerCheck.md)

## Sections

| line | section |
|---:|---|
| 201 | · the city |
| 275 | 1. automatic |
| 356 | 2. the button, and 3. dilution |
| 480 | 4. the preferred |
| 573 | 5. the repayment |
| 762 | 6. the offer |
| 860 | 7. the dial |
| 902 | 8. the rule |
| 1071 | 8b. the city's mark (0.7.48, C4) |
| 1137 | 9. the transfer |
| 1180 | 9b. the withdrawal dial (0.7.48, C1) |
| 1399 | 9c. the idle cash (RB1) |
| 1509 | 9d. the idle cash after last month's toRaise (RB1-R) |
| 1595 | 9e. a real run at the default dial (RB1-R) |
| 1714 | 10. the hand |
| 1785 | 11. the save |
| 1842 | 12. Insane |
| 1906 | 13. Insane, never borrowing (0.7.15) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 153 | `FundCheck.FOUNDING_MONTHS` | `72` | The months the fixture town is played before it is saved: a stage of the founding, never a month picked because the town's path passes at it (PK1). |
| 1512 | `FundCheck.BOND_ROOM_MARGIN` | `3` | How many times the bond room before the month 9d lifts the fund's first month's due (all of it short) to, so the room a month on, which may be over it by the month's new bonds, is still under the toRaise. |
| 1603 | `FundCheck.MONTHS_9E` | `240` | How many months 9e plays: 240 is twenty years, 5 seconds; the counts (the cap sale in 230 months, the idle transfer in 90) would allow 120 with margin, and the longer run gives the fixed point more buybacks to be held... |
| 1606 | `FundCheck.PROBE_MONTHS_9E` | `24` | How many months 9e's probe of a copy plays to see which company buys its own shares back (the one the fund is then placed in). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 112 | `static int fails` |  |
| 113 | `static PrintStream out` |  |
| 114 | `static PrintStream quiet` |  |
| 150 | `static GameFiles files` |  |
| 156 | `static Game.BuildResult bankBuilt` | What the founding's order for the bank answered: the fixture fails if it was not a built bank. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 110 | 1869 | **type** `public class FundCheck` | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). |
| 116 | 4 | `static void check(String label, boolean ok)` |  |
| 121 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 131 | 4 | `static void quietly(Runnable r)` |  |
| 136 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 144 | 5 | `static boolean play(Game g)` | One month, pressed, and whether its audit closed: the playtest's own test (LongPlaytest.audit()). |
| 159 | 6 | `static Game copy()` | A copy of the fixture city, loaded from its save: every scenario starts from the same city. |
| 167 | 3 | `static void takeEquityTo(Bank bank, double equity)` | Takes the bank to `equity` by losing money between two presses - a loss nobody is paid for, which the next strike does not see. |
| 188 | 8 | `static void takeEquityToOwn(Bank bank, java.util.function.ToDoubleFunction<Bank> level)` | ...TO A LEVEL OF ITS OWN MEASURE, AS THE MOVE LEAVES IT - RE-CAUSED (0.7.19). |
| 197 | 77 | `public static void main(String[] args)` |  |

### 1. automatic (lines 275-355)

| line | len | member | says |
|---:|---:|---|---|
| 277 | 71 | `static void resolvedAutomatically()` |  |
| 349 | 6 | `static boolean journalHas(Game g, String label, double amount)` |  |

### 2. the button, and 3. dilution (lines 356-479)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 121 | `static void resolvedOnTheButton()` |  |

### 4. the preferred (lines 480-572)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 90 | `static void thePreferred()` |  |

### 5. the repayment (lines 573-761)

| line | len | member | says |
|---:|---:|---|---|
| 583 | 7 | `static void handSavingsToTake(Game g, double amount)` | HOUSEHOLDS THAT WOULD TAKE THE WHOLE OFFERING THEMSELVES (FS13, 2026-11-20; PK1). |
| 597 | 164 | `static void theRepayment()` | THE PREFERRED REPAID AT ITS THIRD ANNIVERSARY (Jerus: "Sell new shares to repay"): whole, at par with its unpaid dividends, from the bank's capital over its target and then an offering of new common to the public; the... |

### 6. the offer (lines 762-859)

| line | len | member | says |
|---:|---:|---|---|
| 765 | 3 | `static void takeToHalfItsMinimumPlus(Bank bank, double lower)` | The bank taken to half its own minimum, as the move leaves it, and `lower` more. |
| 774 | 14 | `static double startThatLeavesUnder(Game g, double lower)` | The `lower`, from the guess given, that a month leaves the bank standing and under its minimum (FS13, 2026-11-20): tried on reloads of `g` saved as it stands, each one taken to half its minimum plus the guess and play... |
| 789 | 70 | `static void theOffer()` |  |

### 7. the dial (lines 860-901)

| line | len | member | says |
|---:|---:|---|---|
| 862 | 39 | `static void theDial()` |  |

### 8. the rule (lines 902-1070)

| line | len | member | says |
|---:|---:|---|---|
| 911 | 9 | `static void emptyTheDesk(Game g)` | THE DESK HOLDS NOTHING, CAUSED (FS13, 2026-11-20; PK1). |
| 926 | 15 | `static boolean handTheHouseholds(Game g, int c, double share)` | THE HOUSEHOLDS HOLD AT LEAST `share` OF A COMPANY, CAUSED (FS13, 2026-11-20; PK1): what they lack is taken from the desk's inventory and then from the world's, as holdings handed over whole - the cells' count a househ... |
| 942 | 128 | `static void theRule()` |  |

### 8b. the city's mark (0.7.48, C4) (lines 1071-1136)

| line | len | member | says |
|---:|---:|---|---|
| 1073 | 63 | `static void theStaleMark()` |  |

### 9. the transfer (lines 1137-1179)

| line | len | member | says |
|---:|---:|---|---|
| 1144 | 5 | `static double placeablePayIn(Game g, double wanted)` | A pay-in the rule can place (RB1): `wanted` when the fund's cash with it is within the room its share bids and its bond bids have left (Exchange.fundShareRoom() and BondMarket.fundBondRoom(): none of the cash is idle,... |
| 1150 | 29 | `static void theTransfer()` |  |

### 9b. the withdrawal dial (0.7.48, C1) (lines 1180-1398)

| line | len | member | says |
|---:|---:|---|---|
| 1192 | 12 | `static boolean giveTheDeskRoom(Game g, int[] companies)` | THE DESK'S ROOM TO BUY EVERY SHARE OF A COMPANY THAT IS NOT ITS OWN, CAUSED (FS13, 2026-11-20; PK1). |
| 1213 | 19 | `static int[] holdTwoAndARescueBook(Game g)` | A FUND HOLDING TWO COMPANIES ON ITS MARKET BOOK AND A RESCUE BOOK SIX TIMES THAT, WITH NO CASH: each company's households hand it 5% of the company (a fixture of a holding, not a trade, as section 8's band) and the ba... |
| 1234 | 3 | `static double splitUnit(Game g, int c)` | The factor the step's split or consolidation left on a company's count of shares (Exchange.getSplit()), 1 when it had none: what a count read before the step is multiplied by to be read after it. |
| 1239 | 3 | `static double soldOrAsked(Game g, int c, double before)` | What the rule has on a company's book or sold off it since `before` shares: the step's sale, filled or resting. |
| 1243 | 155 | `static void theWithdrawal()` |  |

### 9c. the idle cash (RB1) (lines 1399-1508)

| line | len | member | says |
|---:|---:|---|---|
| 1407 | 101 | `static void theIdleCash()` | THE CASH PAST THE ROOM ITS CAPS LEAVE (RB1). |

### 9d. the idle cash after last month's toRaise (RB1-R) (lines 1509-1594)

| line | len | member | says |
|---:|---:|---|---|
| 1521 | 73 | `static void theIdleCashAfterToRaise()` | THE CASH THE TOP OF THE MONTH PAYS LAST MONTH'S toRaise FROM IS NOT IDLE (RB1-R). |

### 9e. a real run at the default dial (RB1-R) (lines 1595-1713)

| line | len | member | says |
|---:|---:|---|---|
| 1619 | 94 | `static void theRealRun()` | A REAL RUN AT THE DEFAULT DIAL: THE CAP SALE AND THE IDLE TRANSFER FIRE, AND NO HOLDING PASSES THE FIXED POINT (RB1-R). |

### 10. the hand (lines 1714-1784)

| line | len | member | says |
|---:|---:|---|---|
| 1716 | 68 | `static void theHand()` |  |

### 11. the save (lines 1785-1841)

| line | len | member | says |
|---:|---:|---|---|
| 1787 | 54 | `static void theSave()` |  |

### 12. Insane (lines 1842-1905)

| line | len | member | says |
|---:|---:|---|---|
| 1844 | 61 | `static void insane()` |  |

### 13. Insane, never borrowing (0.7.15) (lines 1906-1978)

| line | len | member | says |
|---:|---:|---|---|
| 1916 | 62 | `static void insaneOnAdvances()` | Jerus, asked whether play should work before the city borrows: "Play works from day one. |

