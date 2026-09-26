# ExchangeCheck.java - 982 lines · 22 methods · 3 constants · harnesses

`ham/citybuildersim/ExchangeCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> Verifies the exchange on the order book (0.7.12 round 2): what the desk
> posts and what it is not obliged to take, who trades with whom and at what
> price, what a company does with its surplus, a split, and that a city with
> a market in it still adds up and survives a save.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Order book for both"; the
> round-2 brief: the dealer's quote is replaced by the book, the desk is one
> participant, the price is the last trade):
> 
>   1. THE PRICE IS THE LAST TRADE: before its first a company is priced at
>      fair value; the desk bids and asks half a SPREAD round fair value,
>      within its capital, and never asks what it does not hold; a trade
>      sets the price, fair value stays beside it, and the desk carries its
>      inventory at the lower of the two.
>   2. NOBODY IS OBLIGED: a bank with no capital posts nothing, and a
>      household short of money with no buyer waits - its ask rests, it
>      borrows, and the step counts it; a live desk takes what its capital
>      carries and not a share more, and the rest of the offer waits.
>  2b. THE DESK'S CAPS (round 3, Jerus: "Put them back"): POSITION_LIMIT in
>      one company and BOOK_LIMIT in all, at fair value, beside its capital;
>      the tightest binds its bid.
>  2c. ITS EXCESS (round 4, "Offer the excess at fair value"): what it holds
>      over its caps it asks at fair value, never under, and the rest at its
>      ask.
>   3. THE LEAVERS sell into the desk's bid on the way out; with nobody
>      bidding their shares rest unfilled and stay abroad with them.
>   4. THE WORLD sells when the yield at the best bid is under its hurdle
>      and bids when the yield at the price beats it; a household's sale into
>      its bid is declared abroad; a company with no record it leaves alone.
>   5. THE HOUSEHOLD CELLS each buy the best yield with their own money,
>      taking what is asked best first and resting the rest at the market,
>      not at their reservation; tied names go to the deepest, and a ulp
>      does not decide it.
>   6. CELL TO CELL: a rich cell's resting bid is where a short cell's
>      shares go - a transfer inside the households, no pool line moved.
>  6b. THE CELLS REBALANCE (round 3, "Yes, same rule"): a cell over its
>      target sells HOME_SPEED of the excess to a cell under it, which buys
>      OUT_SPEED of the gap.
>   7. THE BUYBACK: a company over its target takes what is asked up to fair
>      value plus the tolerance, and the rest of the month's money stays in
>      its till while its bid rests at that limit, where a seller who comes
>      later is met (round 4: no special dividend by any path); the cushion
>      counts its debt service; a bad year or a company at target does nothing.
>   8. THE SPLIT: counts, resting orders and the last trade move by the
>      factor and nothing anybody owns changes in value.
>   9. THE SAVE: the books with their resting orders, the last trade, fair
>      value and the split factor round-trip; the dealer's old array loads
>      with its quote as the last price.
>  10. A LIVE CITY: every month of share trading closes the audit, the
>      register and the cells agree, the bank carries the desk at the
>      exchange's mark, the price read is the last trade, and it all comes
>      back from a save.
> 
> Every claim is CAUSED: a register handed shares, a book and a bank; a
> family that leaves; a world shown a yield; cells given money, or left
> short; a company handed a surplus and a market that is cheap, then dear.

**Uses:** [Exchange](Exchange.md) (137), [Equity](Equity.md) (72), [HouseholdBalance](HouseholdBalance.md) (49), [OrderBook](OrderBook.md) (41), [PayTier](PayTier.md) (23), [Bank](Bank.md) (18), [FamilyStructure](FamilyStructure.md) (15), [Household](Household.md) (7), [Game](Game.md) (4), [Sectors](Sectors.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (1)

**Used by (1):** [BankCheck](BankCheck.md)

## Sections

| line | section |
|---:|---|
| 199 | · 1. the price is the last trade |
| 235 | · 2. nobody is obliged |
| 286 | · 2b. the desk's caps (round 3) |
| 333 | · 2c. what is over the caps, offered at fair value |
| 427 | · 3. the leavers |
| 469 | · 4. the world |
| 524 | · 5. the household cells |
| 603 | · 6. cell to cell |
| 649 | · 6b. the cells rebalance by the bond rule (round 3) |
| 680 | · 7. the buyback, and the money that stays |
| 809 | · 8. the split |
| 843 | · 9. the save |
| 880 | · 10. a live city |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `ExchangeCheck.W` | `DebtManager.WORLD_BASE_RATE` |  |
| 88 | `ExchangeCheck.N` | `Equity.COMPANIES.length` |  |
| 89 | `ExchangeCheck.HURDLE` | `W + Equity.FOREIGN_PREMIUM` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 68 | `static int fails` |  |
| 69 | `static PrintStream out` |  |
| 70 | `static PrintStream quiet` |  |
| 155 | `final double[] cash` |  |
| 157 | `final double[] debt` | Interest and principal a month (0.7.11, round 2): nothing unless a fixture says so. |
| 158 | `final double[] boughtBack` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 66 | 917 | **type** `public class ExchangeCheck` | Verifies the exchange on the order book (0.7.12 round 2): what the desk posts and what it is not obliged to take, who trades with whom and at what price, what a company does with its surplus, a split, and that a city ... |
| 72 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 77 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 92 | 5 | `static Equity withRecord(int company, double...monthlyIncome)` | A register with twelve months of this income on one company's record, and no dividend paid. |
| 99 | 5 | `static Equity withPaid(int company, double monthlyIncome, double monthlyPaid)` | A register with twelve months of this income and this ordinary dividend actually paid on one company's record - what the participants value a share on since round 2. |
| 105 | 7 | `static void addPaid(Equity e, int company, double monthlyIncome, double monthlyPaid)` |  |
| 114 | 11 | `static Equity withPaid(int a, double incomeA, double paidA, int b, double incomeB, double paidB)` | ...for two companies over the same twelve months: the register closes every company's month together, so each is noted in it. |
| 126 | 1 | `static double[] months(double v)` |  |
| 129 | 12 | `static HouseholdBalance savers(double savedEach, double takeHome)` | Households: a hundred unskilled couples with this much saved each, struck once so they have a take-home. |
| 142 | 1 | `static Household couple(HouseholdBalance hb)` |  |
| 145 | 7 | `static Bank bankWith(double capital)` | A bank with one branch and this much capital, its month open: a desk posts only for a bank that stands (0.7.12 round 2). |
| 154 | 13 | **type** `static class Firms implements Exchange.Companies` | The companies, as the exchange sees them: a till, a balance sheet, a payroll. |
| 159 | 1 | `public double cashAvailable(int c, double wanted)` _(in ExchangeCheck.Firms)_ |  |
| 160 | 1 | `public double till(int c)` _(in ExchangeCheck.Firms)_ |  |
| 161 | 1 | `public void payBuyback(int c, double x)` _(in ExchangeCheck.Firms)_ |  |
| 162 | 1 | `public double assets(int c)` _(in ExchangeCheck.Firms)_ |  |
| 163 | 1 | `public double equity(int c)` _(in ExchangeCheck.Firms)_ |  |
| 164 | 1 | `public double monthlyOperatingCost(int c)` _(in ExchangeCheck.Firms)_ |  |
| 165 | 1 | `public double monthlyDebtService(int c)` _(in ExchangeCheck.Firms)_ |  |
| 169 | 5 | `static void step(Exchange ex, Equity reg, HouseholdBalance hb, Bank bank, Firms firms, double[] book, double worldRate, double ...` | The exchange's step for month m, its flows opened first, as Game runs it. |
| 176 | 6 | `static double freshDeskLimit(Bank bank, double bid, double fair, double floatShares)` | What a desk holding nothing bids for at this price: the tightest of its capital, its two caps at fair value, and the float (round 3). |
| 183 | 5 | `static double[] bookOf(int company, double equity)` |  |
| 189 | 785 | `public static void main(String[] args) throws Exception` |  |
| 976 | 6 | `static void addPaidOnly(Equity e, int company, double monthlyPaid)` | Twelve months of this ordinary dividend paid, no income recorded: a register whose regime its income sets elsewhere. |

