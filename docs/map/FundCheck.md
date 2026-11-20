# FundCheck.java - 1,730 lines · 30 methods · 2 constants · harnesses

`ham/citybuildersim/FundCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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
> ... (29 more lines in the source)

**Uses:** [TreasuryFund](TreasuryFund.md) (122), [Equity](Equity.md) (74), [Game](Game.md) (70), [Bank](Bank.md) (44), [Exchange](Exchange.md) (40), [OrderBook](OrderBook.md) (28), [Founding](Founding.md) (17), [PriceLevel](PriceLevel.md) (16), [Household](Household.md) (11), [ShortSellingCheck](ShortSellingCheck.md) (9), [MoneyAudit](MoneyAudit.md) (7), [GameFiles](GameFiles.md) (5), [LongTermBond](LongTermBond.md) (5), [CityLand](CityLand.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [LandMarket](LandMarket.md) (4), [Observation](Observation.md) (3), [Sectors](Sectors.md) (3), [FundLedger](FundLedger.md) (3), [CorporateBond](CorporateBond.md) (3), [BondMarket](BondMarket.md) (3), [LandManager](LandManager.md) (3), [TreasuryLine](TreasuryLine.md) (3), [CentralBank](CentralBank.md) (2), [JobType](JobType.md) (2), [FundView](FundView.md) (2), [PolicyPreview](PolicyPreview.md) (2), [WorldEconomy](WorldEconomy.md) (2), [Debt](Debt.md) (2), [DebtQuote](DebtQuote.md) (2)... and 4 more

**Used by (1):** [FundLedgerCheck](FundLedgerCheck.md)

## Sections

| line | section |
|---:|---|
| 184 | · the city |
| 245 | 1. automatic |
| 323 | 2. the button, and 3. dilution |
| 440 | 4. the preferred |
| 533 | 5. the repayment |
| 710 | 6. the offer |
| 774 | 7. the dial |
| 816 | 8. the rule |
| 945 | 8b. the city's mark (0.7.48, C4) |
| 1005 | 9. the transfer |
| 1048 | 9b. the withdrawal dial (0.7.48, C1) |
| 1226 | 9c. the idle cash (RB1) |
| 1336 | 9d. the idle cash after last month's toRaise (RB1-R) |
| 1407 | 9e. a real run at the default dial (RB1-R) |
| 1497 | 10. the hand |
| 1537 | 11. the save |
| 1594 | 12. Insane |
| 1658 | 13. Insane, never borrowing (0.7.15) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1415 | `FundCheck.MONTHS_9E` | `240` | How many months 9e plays: 240 is twenty years, 5 seconds; the counts (the cap sale in 230 months, the idle transfer in 90) would allow 120 with margin, and the longer run gives the fixed point more buybacks to be held... |
| 1418 | `FundCheck.PROBE_MONTHS_9E` | `24` | How many months 9e's probe of a copy plays to see which company buys its own shares back (the one the fund is then placed in). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 101 | `static int fails` |  |
| 102 | `static PrintStream out` |  |
| 103 | `static PrintStream quiet` |  |
| 139 | `static GameFiles files` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 99 | 1632 | **type** `public class FundCheck` | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). |
| 105 | 4 | `static void check(String label, boolean ok)` |  |
| 110 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 120 | 4 | `static void quietly(Runnable r)` |  |
| 125 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 133 | 5 | `static boolean play(Game g)` | One month, pressed, and whether its audit closed: the playtest's own test (LongPlaytest.audit()). |
| 142 | 6 | `static Game copy()` | A copy of the fixture city, loaded from its save: every scenario starts from the same city. |
| 150 | 3 | `static void takeEquityTo(Bank bank, double equity)` | Takes the bank to `equity` by losing money between two presses - a loss nobody is paid for, which the next strike does not see. |
| 171 | 8 | `static void takeEquityToOwn(Bank bank, java.util.function.ToDoubleFunction<Bank> level)` | ...TO A LEVEL OF ITS OWN MEASURE, AS THE MOVE LEAVES IT - RE-CAUSED (0.7.19). |
| 180 | 64 | `public static void main(String[] args)` |  |

### 1. automatic (lines 245-322)

| line | len | member | says |
|---:|---:|---|---|
| 247 | 68 | `static void resolvedAutomatically()` |  |
| 316 | 6 | `static boolean journalHas(Game g, String label, double amount)` |  |

### 2. the button, and 3. dilution (lines 323-439)

| line | len | member | says |
|---:|---:|---|---|
| 325 | 114 | `static void resolvedOnTheButton()` |  |

### 4. the preferred (lines 440-532)

| line | len | member | says |
|---:|---:|---|---|
| 442 | 90 | `static void thePreferred()` |  |

### 5. the repayment (lines 533-709)

| line | len | member | says |
|---:|---:|---|---|
| 541 | 168 | `static void theRepayment()` | THE PREFERRED REPAID AT ITS THIRD ANNIVERSARY (Jerus: "Sell new shares to repay"): whole, at par with its unpaid dividends, from the bank's capital over its target and then an offering of new common to the public; the... |

### 6. the offer (lines 710-773)

| line | len | member | says |
|---:|---:|---|---|
| 712 | 61 | `static void theOffer()` |  |

### 7. the dial (lines 774-815)

| line | len | member | says |
|---:|---:|---|---|
| 776 | 39 | `static void theDial()` |  |

### 8. the rule (lines 816-944)

| line | len | member | says |
|---:|---:|---|---|
| 818 | 126 | `static void theRule()` |  |

### 8b. the city's mark (0.7.48, C4) (lines 945-1004)

| line | len | member | says |
|---:|---:|---|---|
| 947 | 57 | `static void theStaleMark()` |  |

### 9. the transfer (lines 1005-1047)

| line | len | member | says |
|---:|---:|---|---|
| 1012 | 5 | `static double placeablePayIn(Game g, double wanted)` | A pay-in the rule can place (RB1): `wanted` when the fund's cash with it is within the room its share bids and its bond bids have left (Exchange.fundShareRoom() and BondMarket.fundBondRoom(): none of the cash is idle,... |
| 1018 | 29 | `static void theTransfer()` |  |

### 9b. the withdrawal dial (0.7.48, C1) (lines 1048-1225)

| line | len | member | says |
|---:|---:|---|---|
| 1058 | 19 | `static int[] holdTwoAndARescueBook(Game g)` | A FUND HOLDING TWO COMPANIES ON ITS MARKET BOOK AND A RESCUE BOOK SIX TIMES THAT, WITH NO CASH: each company's households hand it 5% of the company (a fixture of a holding, not a trade, as section 8's band) and the ba... |
| 1079 | 3 | `static double soldOrAsked(Game g, int c, double before)` | What the rule has on a company's book or sold off it since `before` shares: the step's sale, filled or resting. |
| 1083 | 142 | `static void theWithdrawal()` |  |

### 9c. the idle cash (RB1) (lines 1226-1335)

| line | len | member | says |
|---:|---:|---|---|
| 1234 | 101 | `static void theIdleCash()` | THE CASH PAST THE ROOM ITS CAPS LEAVE (RB1). |

### 9d. the idle cash after last month's toRaise (RB1-R) (lines 1336-1406)

| line | len | member | says |
|---:|---:|---|---|
| 1345 | 61 | `static void theIdleCashAfterToRaise()` | THE CASH THE TOP OF THE MONTH PAYS LAST MONTH'S toRaise FROM IS NOT IDLE (RB1-R). |

### 9e. a real run at the default dial (RB1-R) (lines 1407-1496)

| line | len | member | says |
|---:|---:|---|---|
| 1431 | 65 | `static void theRealRun()` | A REAL RUN AT THE DEFAULT DIAL: THE CAP SALE AND THE IDLE TRANSFER FIRE, AND NO HOLDING PASSES THE FIXED POINT (RB1-R). |

### 10. the hand (lines 1497-1536)

| line | len | member | says |
|---:|---:|---|---|
| 1499 | 37 | `static void theHand()` |  |

### 11. the save (lines 1537-1593)

| line | len | member | says |
|---:|---:|---|---|
| 1539 | 54 | `static void theSave()` |  |

### 12. Insane (lines 1594-1657)

| line | len | member | says |
|---:|---:|---|---|
| 1596 | 61 | `static void insane()` |  |

### 13. Insane, never borrowing (0.7.15) (lines 1658-1730)

| line | len | member | says |
|---:|---:|---|---|
| 1668 | 62 | `static void insaneOnAdvances()` | Jerus, asked whether play should work before the city borrows: "Play works from day one. |

