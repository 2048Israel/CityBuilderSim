# FundCheck.java - 1,307 lines · 26 methods · 0 constants · harnesses

`ham/citybuildersim/FundCheck.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

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
>  8b. The city's mark (0.7.48, C4): a holding whose last trade is older
>      than Exchange.STALE_MARK_MONTHS is marked at fair value; trading
>      still reads the last trade.
>   9. The transfer at the default withdrawal, Norway's 3% a year, from cash
>      only.
>  9b. The withdrawal dial (0.7.48, C1; Jerus: "even 0 or 10% a month"): at
>      the default it is the transfer to the bit; whole steps from nothing
>      to MAX_WITHDRAWAL_STEPS; at nothing it pays and sells nothing; over
>      the default what the cash cannot cover is sold from the market book
>      at the step, pro rata, and paid at the next month's top, it buys
>      nothing, and the rescue book is never sold; at or under the default a
> ... (16 more lines in the source)

**Uses:** [TreasuryFund](TreasuryFund.md) (65), [Equity](Equity.md) (64), [Game](Game.md) (62), [Bank](Bank.md) (44), [Exchange](Exchange.md) (31), [OrderBook](OrderBook.md) (25), [Founding](Founding.md) (16), [Household](Household.md) (8), [GameFiles](GameFiles.md) (5), [LongTermBond](LongTermBond.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [MoneyAudit](MoneyAudit.md) (3), [Sectors](Sectors.md) (3), [CorporateBond](CorporateBond.md) (3), [BondMarket](BondMarket.md) (3), [TreasuryLine](TreasuryLine.md) (3), [CentralBank](CentralBank.md) (2), [JobType](JobType.md) (2), [WorldEconomy](WorldEconomy.md) (2), [Debt](Debt.md) (2), [DebtQuote](DebtQuote.md) (2), [TreasuryJournal](TreasuryJournal.md) (1), [CityCalendar](CityCalendar.md) (1), [FundView](FundView.md) (1), [DecisionLog](DecisionLog.md) (1), [LandManager](LandManager.md) (1), [LandMarket](LandMarket.md) (1), [NationalAccounts](NationalAccounts.md) (1)

**Used by (1):** [FundLedgerCheck](FundLedgerCheck.md)

## Sections

| line | section |
|---:|---|
| 170 | · the city |
| 216 | 1. automatic |
| 291 | 2. the button, and 3. dilution |
| 395 | 4. the preferred |
| 488 | 5. the repayment |
| 664 | 6. the offer |
| 727 | 7. the dial |
| 769 | 8. the rule |
| 870 | 8b. the city's mark (0.7.48, C4) |
| 905 | 9. the transfer |
| 929 | 9b. the withdrawal dial (0.7.48, C1) |
| 1087 | 10. the hand |
| 1127 | 11. the save |
| 1181 | 12. Insane |
| 1235 | 13. Insane, never borrowing (0.7.15) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `static int fails` |  |
| 89 | `static PrintStream out` |  |
| 90 | `static PrintStream quiet` |  |
| 126 | `static GameFiles files` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 1222 | **type** `public class FundCheck` | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). |
| 92 | 4 | `static void check(String label, boolean ok)` |  |
| 97 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 107 | 4 | `static void quietly(Runnable r)` |  |
| 112 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 120 | 5 | `static boolean play(Game g)` | One month, pressed, and whether its audit closed: the playtest's own test (LongPlaytest.audit()). |
| 129 | 5 | `static Game copy()` | A copy of the fixture city, loaded from its save: every scenario starts from the same city. |
| 136 | 3 | `static void takeEquityTo(Bank bank, double equity)` | Takes the bank to `equity` by losing money between two presses - a loss nobody is paid for, which the next strike does not see. |
| 157 | 8 | `static void takeEquityToOwn(Bank bank, java.util.function.ToDoubleFunction<Bank> level)` | ...TO A LEVEL OF ITS OWN MEASURE, AS THE MOVE LEAVES IT - RE-CAUSED (0.7.19). |
| 166 | 49 | `public static void main(String[] args)` |  |

### 1. automatic (lines 216-290)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 65 | `static void resolvedAutomatically()` |  |
| 284 | 6 | `static boolean journalHas(Game g, String label, double amount)` |  |

### 2. the button, and 3. dilution (lines 291-394)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 101 | `static void resolvedOnTheButton()` |  |

### 4. the preferred (lines 395-487)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 90 | `static void thePreferred()` |  |

### 5. the repayment (lines 488-663)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 167 | `static void theRepayment()` | THE PREFERRED REPAID AT ITS THIRD ANNIVERSARY (Jerus: "Sell new shares to repay"): whole, at par with its unpaid dividends, from the bank's capital over its target and then an offering of new common to the public; the... |

### 6. the offer (lines 664-726)

| line | len | member | says |
|---:|---:|---|---|
| 666 | 60 | `static void theOffer()` |  |

### 7. the dial (lines 727-768)

| line | len | member | says |
|---:|---:|---|---|
| 729 | 39 | `static void theDial()` |  |

### 8. the rule (lines 769-869)

| line | len | member | says |
|---:|---:|---|---|
| 771 | 98 | `static void theRule()` |  |

### 8b. the city's mark (0.7.48, C4) (lines 870-904)

| line | len | member | says |
|---:|---:|---|---|
| 872 | 32 | `static void theStaleMark()` |  |

### 9. the transfer (lines 905-928)

| line | len | member | says |
|---:|---:|---|---|
| 907 | 21 | `static void theTransfer()` |  |

### 9b. the withdrawal dial (0.7.48, C1) (lines 929-1086)

| line | len | member | says |
|---:|---:|---|---|
| 939 | 19 | `static int[] holdTwoAndARescueBook(Game g)` | A FUND HOLDING TWO COMPANIES ON ITS MARKET BOOK AND A RESCUE BOOK SIX TIMES THAT, WITH NO CASH: each company's households hand it 5% of the company (a fixture of a holding, not a trade, as section 8's band) and the ba... |
| 960 | 3 | `static double soldOrAsked(Game g, int c, double before)` | What the rule has on a company's book or sold off it since `before` shares: the step's sale, filled or resting. |
| 964 | 122 | `static void theWithdrawal()` |  |

### 10. the hand (lines 1087-1126)

| line | len | member | says |
|---:|---:|---|---|
| 1089 | 37 | `static void theHand()` |  |

### 11. the save (lines 1127-1180)

| line | len | member | says |
|---:|---:|---|---|
| 1129 | 51 | `static void theSave()` |  |

### 12. Insane (lines 1181-1234)

| line | len | member | says |
|---:|---:|---|---|
| 1183 | 51 | `static void insane()` |  |

### 13. Insane, never borrowing (0.7.15) (lines 1235-1307)

| line | len | member | says |
|---:|---:|---|---|
| 1245 | 62 | `static void insaneOnAdvances()` | Jerus, asked whether play should work before the city borrows: "Play works from day one. |

