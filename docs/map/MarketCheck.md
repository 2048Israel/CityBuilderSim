# MarketCheck.java - 673 lines · 22 methods · 3 constants · harnesses

`ham/citybuildersim/MarketCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> Verifies the efficient market (EM1, 2026-10-29): one value for a share,
> every holder reading it, and a price that follows it - each claim on a
> fixture that causes it, not on a city that happens to show it.
> 
> WHY. Households held about 100% of every company from founding and sold
> only short of money; a company's buyback, bid 10% over value, filled
> $0.58B of $4.4T a month. A share was worth the more of its book and its
> dividends, and a book is 95% cash and dollars abroad: boxes of cash priced
> as boxes of cash, earning less than the dollars abroad they could be
> (Jerus, 2026-10-28: "why hold something that is 1.5 book value, when it
> can get something else that is 1.2 book value and has same P/E ... perhaps
> make it an actual efficient market"). ExchangeCheck holds what the book
> does with an order; this holds that the orders are the right ones.
> 
> WHAT THIS HAS TO PROVE (docs/queue/EM1.md, "Checks"):
> 
>   1. ONE VALUE: a share is worth its spare money at face and its
>      earnings capitalised at the required return. A company with real
>      earnings and little money is valued on them, not its book; a cash box
>      on its money; a company with under RECORD_MONTHS months on its
>      prospects; and the parts add up.
>   2. ONE YARDSTICK: the expected return at fair value is the required
>      return for a company with no money, what its money earns for a cash
>      box, and falls as the price rises.
>   3. TWO ALIKE BUT FOR PRICE: with one value the two prices close; with the
>      desk closed the money goes to the cheaper one and nobody pays the
>      dearer one's price; a holder switches from a share to a cheaper one
>      that returns more than a band over it, and not to one within the
>      band.
>   4. THE BUYBACK FINDS ITS SELLERS: a bid over value by more than BAND is
>      sold into by the holders; at the equity target there is no bid and no
>      sale.
>   5. A SHARE EARNING LESS THAN DOLLARS ABROAD IS SOLD - by the cells and by
>      the world; one earning well above it is not.
>   6. A QUIET MARKET does not churn: every price at fair, no trade, no
>      resting sell from a holder, and the same market with one share
>      returning a band less does trade.
>   7. THE SAVE: the figures fair value was struck on round-trip through the
>      exchange's state, an old save strikes them at the next step, and a
>      redenomination scales them.
> 
> Every claim is CAUSED: a register handed a record and a company handed its
> money; two shares that differ in nothing but the price of their last
> trade; a bid placed over value; a cash box whose money earns less than
> dollars abroad; a market of shares inside each other's band.

**Uses:** [Exchange](Exchange.md) (57), [Equity](Equity.md) (22), [ExchangeCheck](ExchangeCheck.md) (20), [HouseholdBalance](HouseholdBalance.md) (20), [Household](Household.md) (12), [OrderBook](OrderBook.md) (11), [PayTier](PayTier.md) (7), [FamilyStructure](FamilyStructure.md) (5), [Sectors](Sectors.md) (4), [OutwardInvestment](OutwardInvestment.md) (4), [Bank](Bank.md) (2)

## Sections

| line | section |
|---:|---|
| 201 | · 1. one value |
| 268 | · 2. one yardstick |
| 292 | · 3. two alike but for price |
| 424 | · 4. the buyback finds its sellers |
| 466 | · 5. a share earning less than dollars abroad is sold |
| 506 | · 6. a quiet market |
| 558 | · 7. the save |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `MarketCheck.W` | `ExchangeCheck.W` |  |
| 87 | `MarketCheck.N` | `Equity.COMPANIES.length` |  |
| 89 | `MarketCheck.REQUIRED` | `Equity.requiredReturn(W)` | What a share is asked to earn: the world's rate plus the premium (Equity.requiredReturn()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `static int fails` |  |
| 55 | `static int checks` |  |
| 56 | `static PrintStream out` |  |
| 75 | `static int sectionStart` |  |
| 97 | `final Equity reg` |  |
| 98 | `final HouseholdBalance hb` |  |
| 99 | `final Bank bank` |  |
| 100 | `final ExchangeCheck.Firms firms` |  |
| 101 | `final double[] book` |  |
| 102 | `final Exchange ex` |  |
| 103 | `int month` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 52 | 622 | **type** `public class MarketCheck` | Verifies the efficient market (EM1, 2026-10-29): one value for a share, every holder reading it, and a price that follows it - each claim on a fixture that causes it, not on a city that happens to show it. |
| 58 | 5 | `static void assertTrue(String label, boolean ok)` |  |
| 64 | 10 | `static void close(String label, double actual, double expected, double tol)` |  |
| 77 | 4 | `static void section(String title)` |  |
| 82 | 3 | `static void sectionEnd()` |  |
| 96 | 37 | **type** `static final class City` | A market on a table: a register, the households that hold it, a bank for the desk, the companies' tills and one exchange - listed company by company, each given a year of earnings and the money it holds. |
| 105 | 4 | `City(HouseholdBalance hb, Bank bank)` _(in MarketCheck.City)_ |  |
| 111 | 6 | `City list(int c, double shares, double yearlyEarnings)` _(in MarketCheck.City)_ | Lists the company, `shares` founders' shares on a book of as many founding prices, with a year of this net income on its record. |
| 119 | 7 | `City firm(int c, double opexMonthly, double money, double moneyEarnsYearly)` _(in MarketCheck.City)_ | The company's money (its till, and the same as the money Exchange.Companies.money() says), what that earns it a year, and its monthly operating cost, which sets the cushion. |
| 128 | 4 | `void step(double depositRate)` _(in MarketCheck.City)_ | The exchange's step for the next month. |
| 135 | 3 | `static double cushion(double opexMonthly)` | Six months of a company's costs, the cushion a company keeps before its money counts as spare (Exchange.BUYBACK_CUSHION_MONTHS). |
| 140 | 12 | `static HouseholdBalance twoCells(double poorTakeHome, double richTakeHome)` | Two cells of a hundred couples each: the unskilled with this take-home, the skilled with theirs (both struck once, savings left to the caller). |
| 153 | 1 | `static Household poorOf(HouseholdBalance hb)` |  |
| 155 | 1 | `static Household richOf(HouseholdBalance hb)` |  |
| 157 | 1 | `static String nameOf(Household cell)` |  |
| 160 | 5 | `static void holdOnly(HouseholdBalance hb, int company, Household holder)` | Every share of the company the households hold goes to this one cell (the register's total is unchanged). |
| 167 | 7 | `static void takeFromHouseholds(Equity reg, HouseholdBalance hb, int company, double n, boolean toDesk)` | `n` of the households' shares of a company go to the desk, or to the world: the register and the cells both move. |
| 176 | 3 | `static double heldBy(Household cell, int company)` | What a cell holds of a company, all its households together. |
| 181 | 5 | `static int tradesThisStep(Exchange ex)` | Trades made on every book since the step opened them (the books clear their month at the step; Exchange.getLastTrades() reads the month before). |
| 188 | 3 | `static double alternative(double depositRate)` | The best the alternatives to a share earn a year: the deposit, the dollars abroad and the city's paper (none in these fixtures), plus the premium. |
| 192 | 417 | `public static void main(String[] args) throws Exception` |  |
| 618 | 18 | `static City switchCity(int a, int b, double lastA, double lastB, double deskCapital)` | Section 3c's market: two companies alike in everything but where they last traded. |
| 643 | 10 | `static City buybackCity(int company, boolean atTarget)` | Section 4's company: a cash box - a little earned, a great deal of money past its cushion, the money earning what the world pays - all equity, held by one cell that is neither short of anything nor has anything to spend. |
| 661 | 12 | `static City quietCity(int third, int first, int second, double secondMoney)` | Section 6's market: three companies earning $1,200 a year on a book of $1,000, the third also holding this much money past its cushion, earning 2% - a spread of returns inside the band. |

