# Equity.java - 828 lines · 74 methods · 16 constants · model

`ham/citybuildersim/Equity.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The share register: who owns the city's companies, what they paid for them,
> and what the companies pay them back.
> 
> ==================== WHY ====================
> 
> Until this evening nobody owned anything. Six sectors and a bank earned,
> banked, borrowed and defaulted, and every dollar a company ever cleared was
> still on its balance sheet - at month 4,000 the sectors held US$1.7-2.4
> trillion abroad and $150-190bn at home in cities of 20,000 people, because
> no company paid a dividend, bought back a share, or had an owner to pay.
> And the other way round: every expansion was cash or a loan, so a city with
> no bank borrowed at nineteen percent to open its first shop, and the bank's
> own capital was declared to arrive from "savers down the road" without any
> saver paying for it.
> 
> Jerus, 2026-09-10: "businesses first do offerings domestically and if not
> enough is raised they go foreign... each household type gets the offer and
> based on their situation and their cash available they accept or decline...
> each household needs a number of shares owned per company."
> 
> ==================== THE SEVEN ====================
> 
> The six sectors and the bank - "specially the bank, they need equity to
> avoid rough start". Each is one company with one class of share, listed
> here by name; the households hold their shares per cell (Household.shares),
> and the rest are held abroad. No exchange yet: a share is bought at an
> offering, pays its dividend, and is held. Jerus: "when we build the
> exchange, which we will but not just yet."
> 
> ==================== WHEN A COMPANY GOES TO THE MARKET ====================
> 
> Every company lists on day one, and the bank's founding capital is the
> first thing sold. After that, Jerus's rule:
> 
>   "at the beginning, and equity before debt, specially in good times...
>    it will check if it thinks it might expand in the next 2-5 years, and
>    raise equity for it; they will try not to raise equity in bad times; if
>    the situation is normal, they'll use debt, or if they have too much
>    equity relative to assets."
> 
> So each company reads its own last twelve months and is in one of four
> states (see regime()):
> 
>   NEW      fewer than twelve months on the books: every plan is part
>            equity, at the founding share, because there is no record to
>            borrow against and the founders are still putting money in
>   GOOD     profitable in nine months of twelve and not declining: it raises
>            AHEAD - the equity share of three years of what it has been
>            building, so the next expansions are funded before they are
>            wanted, and the till carries the war chest
>   NORMAL   it borrows, unless it has slipped well under its equity target,
>            in which case it raises back up to it
>   BAD      losses: it does not go to the market at all
> 
> ==================== HOW MUCH EQUITY ====================
> 
> "They adjust based on their profitability: if the business is stable
> they'll go for less equity compared to debt, if more risky, then more
> equity... debt is the leverage aspect."
> 
> ... (28 more lines in the source)

**Uses:** [HouseholdBalance](HouseholdBalance.md) (5), [Sectors](Sectors.md) (1)

**Used by (28):** [AgricultureCheck](AgricultureCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [Household](Household.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MoneyAudit](MoneyAudit.md), [PeopleScreen](PeopleScreen.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [SectorScreen](SectorScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 111 | · the dials |
| 193 | · a company |
| 249 | THE RECORD |
| 329 | HOW MUCH TO RAISE |
| 379 | THE OFFERING |
| 507 | THE DIVIDEND |
| 588 | THE DESK |
| 692 | · reading |
| 740 | · saving |

## Enum constants

| line | constant | says |
|---:|---|---|
| 191 | `Equity.Regime.NEW` |  |
| 191 | `Equity.Regime.GOOD` |  |
| 191 | `Equity.Regime.NORMAL` |  |
| 191 | `Equity.Regime.BAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 96 | `Equity.COMPANIES` |  | The companies, in register order: every sector in the registry's order, then the bank. |
| 97 | `Equity.BANK` |  |  |
| 114 | `Equity.PAYOUT` | `.40` | The share of a positive month's net income paid to the owners - every company's but the bank's, which pays by its own capital rule since 0.7.8 (Bank.dividendDue()). |
| 117 | `Equity.FOUNDING_PRICE` | `1.0` | A founding share: a thousand dollars, in the game's thousands. |
| 149 | `Equity.RECORD_MONTHS` | `12` | Months on the books before a company has a record to be judged on. |
| 152 | `Equity.GOOD_MONTHS` | `9` | Profitable months of the last twelve that make a good year. |
| 155 | `Equity.BAD_MONTHS` | `6` | ...and the most a bad year has. |
| 158 | `Equity.BASE_EQUITY_SHARE` | `.30` | What a steady business keeps as equity: the rest is leverage. |
| 161 | `Equity.RISK_SLOPE` | `.20` | How much the target rises per unit of income swing (std dev over \|mean\|). |
| 163 | `Equity.MAX_EQUITY_SHARE` | `.70` |  |
| 166 | `Equity.NEW_EQUITY_SHARE` | `.50` | A new company's plans are this much equity, whatever its assets say. |
| 169 | `Equity.HORIZON_YEARS` | `3` | In good times, the years of expansion a company raises for ahead. |
| 172 | `Equity.UNDER_TARGET` | `.10` | Under target by this much before a normal year raises instead of borrows. |
| 175 | `Equity.FOREIGN_PREMIUM` | `.03` | What the world wants over its own rate to buy a share here, annual. |
| 748 | `Equity.SLOTS_BEFORE_DESK` | `RECORD_MONTHS * 2 + 10` | Slots a company before the desk (2026-09-10, night). |
| 750 | `Equity.SLOTS` | `SLOTS_BEFORE_DESK + 2` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 138 | `private double foundingPrice` | The same price in TODAY's money, after any currency reform. |
| 197 | `final String name` |  |
| 198 | `double shares` | in issue |
| 199 | `double foreignShares` | in issue |
| 200 | `double dealerShares` | of those, held abroad |
| 201 | `double lastPrice` | ...and held by the bank's trading desk (never its own: those are cancelled) |
| 202 | `final double[] income` | net income, a ring |
| 203 | `final double[] spent` | net income, a ring |
| 204 | `int months` | on buildings, a ring |
| 205 | `double lifetimeRaisedHome, lifetimeRaisedAbroad` | recorded so far |
| 206 | `double lifetimeDividendsHome, lifetimeDividendsAbroad` |  |
| 207 | `int offerings` |  |
| 210 | `double offered, raisedHome, raisedAbroad, dividendHome, dividendDesk, dividendAbroad` | this month |
| 211 | `double boughtBackThisMonth, lifetimeBoughtBack` |  |
| 212 | `Regime regime` |  |
| 213 | `double targetShare` |  |
| 243 | `private final Listing[] listings` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 736 | **type** `public class Equity` | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. |
| 98 | 7 | `static { ... }` |  |
| 106 | 4 | `public static int indexOf(String company)` |  |

### the dials (lines 111-192)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 1 | `public double foundingPrice()` | A founding share in today's money. |
| 144 | 3 | `public void seedConstants(double unit)` | Re-seeds the yardstick at a given unit. |
| 187 | 3 | `public static double requiredYield(double worldRate)` | The earnings yield the market prices a company here on: what a dollar of its yearly income has to earn its owners for them to hold the share at book. |
| 191 | 1 | **type** `public enum Regime` |  |

### a company (lines 193-248)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 46 | **type** `static final class Listing` | One listing. |
| 215 | 1 | `Listing(String name)` _(in Equity.Listing)_ |  |
| 218 | 1 | `double domesticShares()` _(in Equity.Listing)_ | Held by the city's households: what is neither abroad nor on the desk. |
| 219 | 1 | `double raised()` _(in Equity.Listing)_ |  |
| 220 | 1 | `double dividend()` _(in Equity.Listing)_ |  |
| 222 | 5 | `void clearMonth()` _(in Equity.Listing)_ |  |
| 228 | 6 | `double trailingIncome()` _(in Equity.Listing)_ |  |
| 235 | 6 | `double trailingSpent()` _(in Equity.Listing)_ |  |
| 245 | 3 | `public Equity()` |  |

### THE RECORD (lines 249-328)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 22 | `public void recordMonth(int company, double netIncome, double spentOnBuildings)` |  |
| 285 | 3 | `public void startMonth()` | Clears the month's flows. |
| 289 | 17 | `private static Regime regimeOf(Listing l)` |  |
| 315 | 13 | `private static double targetEquityShareOf(Listing l)` | The share of the balance sheet a company wants as equity. |

### HOW MUCH TO RAISE (lines 329-378)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 33 | `public double raiseFor(int company, double assets, double equity, double planCost)` | What a company with this plan would raise from its owners first. |

### THE OFFERING (lines 379-506)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 4 | `public double offer(int company, double amount, double bookEquity, HouseholdBalance households, double worldRate)` | Sells shares: to the households first, to the world for what is left. |
| 403 | 53 | `public double offer(int company, double amount, double bookEquity, HouseholdBalance households, double worldRate, double market...` | there is no market: a listed company sells new shares at the market, not at the register's reckoning |
| 471 | 7 | `private double priceOf(Listing l, double bookEquity, double worldRate)` | What one share sells for: the company's value over its shares. |
| 491 | 9 | `public boolean listIfUnlisted(int company, double bookEquity, HouseholdBalance households)` | Lists a company that has equity and no owners: the founders' shares are issued against its book, as the first offering would have done. |
| 502 | 4 | `public double bookPerShare(int company, double bookEquity)` | What one share is worth on the books today. |

### THE DIVIDEND (lines 507-587)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 5 | `public double dividendDue(int company, double netIncome)` | What a company owes its owners on a month's result. |
| 533 | 10 | `public void followEmigrants(HouseholdBalance households)` | Shares that left the city with their holders this month are held abroad from now on. |
| 552 | 31 | `public double payDividend(int company, double paid, HouseholdBalance households)` | Pays a dividend: the households' part into their savings, the rest to the shareholders abroad. |
| 585 | 1 | `public double getDividendDeskThisMonth(int company)` | What the bank's trading desk was paid on its inventory this month. |
| 586 | 1 | `public double getDividendDeskThisMonth()` |  |

### THE DESK (lines 588-691)

| line | len | member | says |
|---:|---:|---|---|
| 599 | 1 | `public double getDealerShares(int company)` | Shares the desk holds; negative when it has sold what it did not have. |
| 602 | 4 | `public double getOutstanding(int company)` | Shares in the owners' hands: in issue less what the desk is long. |
| 608 | 6 | `void deskBuysFromHouseholds(int company, double n)` | The desk buys from the city's households (whose cells the caller has already debited). |
| 616 | 6 | `void deskSellsToHouseholds(int company, double n)` | ...and sells to them. |
| 623 | 7 | `void deskBuysFromAbroad(int company, double n)` |  |
| 631 | 7 | `void deskSellsAbroad(int company, double n)` |  |
| 647 | 10 | `void cancel(int company, double fromHouseholds, double fromDesk, double fromAbroad)` | A company buys back and cancels shares from every holder pro rata - a tender at one price. |
| 663 | 10 | `void split(int company, double k)` | A split (k > 1) or a consolidation (k < 1): every count by k, the last price by its inverse. |
| 674 | 1 | `public double getBoughtBackThisMonth(int company)` |  |
| 675 | 1 | `public double getLifetimeBoughtBack(int company)` |  |
| 682 | 3 | `public double fairValue(int company, double bookEquity, double worldRate)` | What a share is worth on the register's own reckoning: book or capitalised earnings, whichever is more, over the shares in issue. |
| 687 | 4 | `public double dividendPerShareAnnual(int company)` | Dividend a share would pay over a year on the last twelve months' record. |

### reading (lines 692-739)

| line | len | member | says |
|---:|---:|---|---|
| 694 | 1 | `public double getShares(int company)` |  |
| 695 | 1 | `public double getForeignShares(int company)` |  |
| 696 | 1 | `public double getDomesticShares(int company)` |  |
| 697 | 1 | `public double getLastPrice(int company)` |  |
| 698 | 1 | `public Regime getRegime(int company)` |  |
| 699 | 1 | `public double getTargetEquityShare(int company)` |  |
| 700 | 1 | `public int getMonthsRecorded(int company)` |  |
| 701 | 1 | `public int getOfferings(int company)` |  |
| 704 | 4 | `public double foreignShare(int company)` | Share of the company held abroad, 0-1. |
| 710 | 4 | `public double deskShare(int company)` | Share of the company on the bank's trading desk, 0-1: what the desk is long over what is in issue - nothing while it is short. |
| 715 | 1 | `public double getOfferedThisMonth(int company)` |  |
| 716 | 1 | `public double getRaisedHomeThisMonth(int company)` |  |
| 717 | 1 | `public double getRaisedAbroadThisMonth(int company)` |  |
| 718 | 1 | `public double getDividendHomeThisMonth(int company)` |  |
| 719 | 1 | `public double getDividendAbroadThisMonth(int company)` |  |
| 720 | 1 | `public double getRaisedThisMonth(int company)` |  |
| 721 | 1 | `public double getDividendThisMonth(int company)` |  |
| 723 | 1 | `public double getLifetimeRaisedHome(int company)` |  |
| 724 | 1 | `public double getLifetimeRaisedAbroad(int company)` |  |
| 725 | 1 | `public double getLifetimeDividendsHome(int company)` |  |
| 726 | 1 | `public double getLifetimeDividendsAbroad(int company)` |  |
| 730 | 1 | `public double getRaisedHomeThisMonth()` | ---- the city, for MoneyAudit and the summary ---- |
| 731 | 1 | `public double getRaisedAbroadThisMonth()` |  |
| 732 | 1 | `public double getDividendHomeThisMonth()` |  |
| 733 | 1 | `public double getDividendAbroadThisMonth()` |  |
| 734 | 1 | `public double getLifetimeRaisedHome()` |  |
| 735 | 1 | `public double getLifetimeRaisedAbroad()` |  |
| 736 | 1 | `public double getLifetimeDividendsHome()` |  |
| 737 | 1 | `public double getLifetimeDividendsAbroad()` |  |
| 738 | 1 | `public int getOfferings()` |  |

### saving (lines 740-828)

| line | len | member | says |
|---:|---:|---|---|
| 752 | 1 | `public String[] keys()` |  |
| 754 | 21 | `public double[] toSaveArray()` |  |
| 777 | 31 | `public boolean restore(String[] keys, double[] saved)` |  |
| 809 | 3 | `public void reset()` |  |
| 817 | 11 | `public void redenominate(double scale)` | Everything in money, in the new unit. |

