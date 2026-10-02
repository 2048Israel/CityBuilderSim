# FundCheck.java - 1,087 lines · 22 methods · 0 constants · harnesses

`ham/citybuildersim/FundCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
>      limit and no further.
>   9. The 3% transfer, from cash only.
>  10. The hand: its orders at fair value, or at a price the player names,
>      and one cancelled before the step (0.7.39); pay-in and draw-out off
>      the surplus.
>  11. Every piece round-trips through a save; an older save loads empty.
>  12. Insane: nothing in the treasury or the vault; one dollar bond abroad at
>      3% for twenty years at the land's value; a day-0 city is quoted both
>      of the build screen's offers, and the build screen and the land office
>      ask for funding at D$0; borrowed, it runs and its audit closes.
>  13. Insane, never borrowing (0.7.15, Jerus: "Play runs on advances"): at
>      day 0 its ceiling is nothing, so a purchase and a discretionary line
>      are refused and a promise is paid past it; the time skip runs it a
> ... (4 more lines in the source)

**Uses:** [Game](Game.md) (53), [Equity](Equity.md) (51), [Bank](Bank.md) (44), [TreasuryFund](TreasuryFund.md) (39), [Founding](Founding.md) (16), [Exchange](Exchange.md) (13), [OrderBook](OrderBook.md) (12), [Household](Household.md) (6), [GameFiles](GameFiles.md) (5), [LongTermBond](LongTermBond.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [MoneyAudit](MoneyAudit.md) (3), [TreasuryLine](TreasuryLine.md) (3), [CentralBank](CentralBank.md) (2), [JobType](JobType.md) (2), [WorldEconomy](WorldEconomy.md) (2), [Debt](Debt.md) (2), [DebtQuote](DebtQuote.md) (2), [TreasuryJournal](TreasuryJournal.md) (1), [CityCalendar](CityCalendar.md) (1), [Sectors](Sectors.md) (1), [LandManager](LandManager.md) (1), [LandMarket](LandMarket.md) (1), [NationalAccounts](NationalAccounts.md) (1)

**Used by (1):** [FundLedgerCheck](FundLedgerCheck.md)

## Sections

| line | section |
|---:|---|
| 158 | · the city |
| 202 | 1. automatic |
| 277 | 2. the button, and 3. dilution |
| 381 | 4. the preferred |
| 474 | 5. the repayment |
| 650 | 6. the offer |
| 713 | 7. the dial |
| 755 | 8. the rule |
| 843 | 9. the transfer |
| 867 | 10. the hand |
| 907 | 11. the save |
| 961 | 12. Insane |
| 1015 | 13. Insane, never borrowing (0.7.15) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `static int fails` |  |
| 77 | `static PrintStream out` |  |
| 78 | `static PrintStream quiet` |  |
| 114 | `static GameFiles files` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1014 | **type** `public class FundCheck` | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). |
| 80 | 4 | `static void check(String label, boolean ok)` |  |
| 85 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 95 | 4 | `static void quietly(Runnable r)` |  |
| 100 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 108 | 5 | `static boolean play(Game g)` | One month, pressed, and whether its audit closed: the playtest's own test (LongPlaytest.audit()). |
| 117 | 5 | `static Game copy()` | A copy of the fixture city, loaded from its save: every scenario starts from the same city. |
| 124 | 3 | `static void takeEquityTo(Bank bank, double equity)` | Takes the bank to `equity` by losing money between two presses - a loss nobody is paid for, which the next strike does not see. |
| 145 | 8 | `static void takeEquityToOwn(Bank bank, java.util.function.ToDoubleFunction<Bank> level)` | ...TO A LEVEL OF ITS OWN MEASURE, AS THE MOVE LEAVES IT - RE-CAUSED (0.7.19). |
| 154 | 47 | `public static void main(String[] args)` |  |

### 1. automatic (lines 202-276)

| line | len | member | says |
|---:|---:|---|---|
| 204 | 65 | `static void resolvedAutomatically()` |  |
| 270 | 6 | `static boolean journalHas(Game g, String label, double amount)` |  |

### 2. the button, and 3. dilution (lines 277-380)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 101 | `static void resolvedOnTheButton()` |  |

### 4. the preferred (lines 381-473)

| line | len | member | says |
|---:|---:|---|---|
| 383 | 90 | `static void thePreferred()` |  |

### 5. the repayment (lines 474-649)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 167 | `static void theRepayment()` | THE PREFERRED REPAID AT ITS THIRD ANNIVERSARY (Jerus: "Sell new shares to repay"): whole, at par with its unpaid dividends, from the bank's capital over its target and then an offering of new common to the public; the... |

### 6. the offer (lines 650-712)

| line | len | member | says |
|---:|---:|---|---|
| 652 | 60 | `static void theOffer()` |  |

### 7. the dial (lines 713-754)

| line | len | member | says |
|---:|---:|---|---|
| 715 | 39 | `static void theDial()` |  |

### 8. the rule (lines 755-842)

| line | len | member | says |
|---:|---:|---|---|
| 757 | 85 | `static void theRule()` |  |

### 9. the transfer (lines 843-866)

| line | len | member | says |
|---:|---:|---|---|
| 845 | 21 | `static void theTransfer()` |  |

### 10. the hand (lines 867-906)

| line | len | member | says |
|---:|---:|---|---|
| 869 | 37 | `static void theHand()` |  |

### 11. the save (lines 907-960)

| line | len | member | says |
|---:|---:|---|---|
| 909 | 51 | `static void theSave()` |  |

### 12. Insane (lines 961-1014)

| line | len | member | says |
|---:|---:|---|---|
| 963 | 51 | `static void insane()` |  |

### 13. Insane, never borrowing (0.7.15) (lines 1015-1087)

| line | len | member | says |
|---:|---:|---|---|
| 1025 | 62 | `static void insaneOnAdvances()` | Jerus, asked whether play should work before the city borrows: "Play works from day one. |

