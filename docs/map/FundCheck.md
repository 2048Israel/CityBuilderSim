# FundCheck.java - 905 lines · 20 methods · 0 constants · harnesses

`ham/citybuildersim/FundCheck.java` - generated 2026-09-27 by CodeMap; line numbers are as of that run.

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
>  10. The hand: its orders at fair value; pay-in and draw-out off the
>      surplus.
>  11. Every piece round-trips through a save; an older save loads empty.
>  12. Insane: nothing in the treasury or the vault; one dollar bond abroad at
>      3% for twenty years at the land's value; the clock and the time skip
>      refuse until the city borrows; a day-0 city is quoted both of the
>      build screen's offers, and the build screen and the land office ask
>      for funding at D$0; borrowed, it runs and its audit closes.

**Uses:** [Equity](Equity.md) (50), [Game](Game.md) (48), [Bank](Bank.md) (42), [TreasuryFund](TreasuryFund.md) (39), [OrderBook](OrderBook.md) (12), [Exchange](Exchange.md) (12), [Founding](Founding.md) (10), [Household](Household.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (3), [LongTermBond](LongTermBond.md) (3), [MoneyAudit](MoneyAudit.md) (2), [Debt](Debt.md) (2), [DebtQuote](DebtQuote.md) (2), [CentralBank](CentralBank.md) (1), [TreasuryJournal](TreasuryJournal.md) (1), [CityCalendar](CityCalendar.md) (1), [Sectors](Sectors.md) (1), [WorldEconomy](WorldEconomy.md) (1), [LandManager](LandManager.md) (1), [LandMarket](LandMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 125 | · the city |
| 168 | 1. automatic |
| 243 | 2. the button, and 3. dilution |
| 322 | 4. the preferred |
| 415 | 5. the repayment |
| 591 | 6. the offer |
| 618 | 7. the dial |
| 660 | 8. the rule |
| 748 | 9. the transfer |
| 772 | 10. the hand |
| 798 | 11. the save |
| 852 | 12. Insane |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |
| 70 | `static PrintStream out` |  |
| 71 | `static PrintStream quiet` |  |
| 107 | `static GameFiles files` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 839 | **type** `public class FundCheck` | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). |
| 73 | 4 | `static void check(String label, boolean ok)` |  |
| 78 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 88 | 4 | `static void quietly(Runnable r)` |  |
| 93 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 101 | 5 | `static boolean play(Game g)` | One month, pressed, and whether its audit closed: the playtest's own test (LongPlaytest.audit()). |
| 110 | 5 | `static Game copy()` | A copy of the fixture city, loaded from its save: every scenario starts from the same city. |
| 117 | 3 | `static void takeEquityTo(Bank bank, double equity)` | Takes the bank to `equity` by losing money between two presses - a loss nobody is paid for, which the next strike does not see. |
| 121 | 46 | `public static void main(String[] args)` |  |

### 1. automatic (lines 168-242)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 65 | `static void resolvedAutomatically()` |  |
| 236 | 6 | `static boolean journalHas(Game g, String label, double amount)` |  |

### 2. the button, and 3. dilution (lines 243-321)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 76 | `static void resolvedOnTheButton()` |  |

### 4. the preferred (lines 322-414)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 90 | `static void thePreferred()` |  |

### 5. the repayment (lines 415-590)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 167 | `static void theRepayment()` | THE PREFERRED REPAID AT ITS THIRD ANNIVERSARY (Jerus: "Sell new shares to repay"): whole, at par with its unpaid dividends, from the bank's capital over its target and then an offering of new common to the public; the... |

### 6. the offer (lines 591-617)

| line | len | member | says |
|---:|---:|---|---|
| 593 | 24 | `static void theOffer()` |  |

### 7. the dial (lines 618-659)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 39 | `static void theDial()` |  |

### 8. the rule (lines 660-747)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 85 | `static void theRule()` |  |

### 9. the transfer (lines 748-771)

| line | len | member | says |
|---:|---:|---|---|
| 750 | 21 | `static void theTransfer()` |  |

### 10. the hand (lines 772-797)

| line | len | member | says |
|---:|---:|---|---|
| 774 | 23 | `static void theHand()` |  |

### 11. the save (lines 798-851)

| line | len | member | says |
|---:|---:|---|---|
| 800 | 51 | `static void theSave()` |  |

### 12. Insane (lines 852-905)

| line | len | member | says |
|---:|---:|---|---|
| 854 | 51 | `static void insane()` |  |

