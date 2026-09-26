# Equity.java - 940 lines · 80 methods · 17 constants · model

`ham/citybuildersim/Equity.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
> and the rest are held abroad. There was no exchange at first: a share was
> bought at an offering, paid its dividend, and was held. Jerus: "when we
> build the exchange, which we will but not just yet." It came after
> (Exchange), and since 0.7.12 round 2 a share changes hands on its
> company's order book.
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
> ... (40 more lines in the source)

**Uses:** [HouseholdBalance](HouseholdBalance.md) (5), [Sectors](Sectors.md) (1)

**Used by (30):** [AgricultureCheck](AgricultureCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [Household](Household.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [PeopleScreen](PeopleScreen.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 123 | · the dials |
| 205 | · a company |
| 280 | THE RECORD |
| 360 | HOW MUCH TO RAISE |
| 410 | THE OFFERING |
| 545 | THE DIVIDEND |
| 684 | THE HOLDERS |
| 781 | · reading |
| 829 | · saving |

## Enum constants

| line | constant | says |
|---:|---|---|
| 203 | `Equity.Regime.NEW` |  |
| 203 | `Equity.Regime.GOOD` |  |
| 203 | `Equity.Regime.NORMAL` |  |
| 203 | `Equity.Regime.BAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 108 | `Equity.COMPANIES` |  | The companies, in register order: every sector in the registry's order, then the bank. |
| 109 | `Equity.BANK` |  |  |
| 126 | `Equity.PAYOUT` | `.40` | The share of a positive month's net income paid to the owners - of what it leaves after the principal repaid, since round 2 of 0.7.11 (dividendDue()) - every company's but the bank's, which pays by its own capital rul... |
| 129 | `Equity.FOUNDING_PRICE` | `1.0` | A founding share: a thousand dollars, in the game's thousands. |
| 161 | `Equity.RECORD_MONTHS` | `12` | Months on the books before a company has a record to be judged on. |
| 164 | `Equity.GOOD_MONTHS` | `9` | Profitable months of the last twelve that make a good year. |
| 167 | `Equity.BAD_MONTHS` | `6` | ...and the most a bad year has. |
| 170 | `Equity.BASE_EQUITY_SHARE` | `.30` | What a steady business keeps as equity: the rest is leverage. |
| 173 | `Equity.RISK_SLOPE` | `.20` | How much the target rises per unit of income swing (std dev over \|mean\|). |
| 175 | `Equity.MAX_EQUITY_SHARE` | `.70` |  |
| 178 | `Equity.NEW_EQUITY_SHARE` | `.50` | A new company's plans are this much equity, whatever its assets say. |
| 181 | `Equity.HORIZON_YEARS` | `3` | In good times, the years of expansion a company raises for ahead. |
| 184 | `Equity.UNDER_TARGET` | `.10` | Under target by this much before a normal year raises instead of borrows. |
| 187 | `Equity.FOREIGN_PREMIUM` | `.03` | What the world wants over its own rate to buy a share here, annual. |
| 837 | `Equity.SLOTS_BEFORE_DESK` | `RECORD_MONTHS * 2 + 10` | Slots a company before the desk (2026-09-10, night). |
| 840 | `Equity.SLOTS_BEFORE_PAID` | `SLOTS_BEFORE_DESK + 2` | ...and before the dividends actually paid (0.7.12 round 2). |
| 843 | `Equity.SLOTS` | `SLOTS_BEFORE_PAID + RECORD_MONTHS + 1` | The ring of dividends paid and its count, appended. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `private double foundingPrice` | The same price in TODAY's money, after any currency reform. |
| 209 | `final String name` |  |
| 210 | `double shares` | in issue |
| 211 | `double foreignShares` | in issue |
| 212 | `double dealerShares` | of those, held abroad |
| 213 | `double lastPrice` | ...and held by the bank's trading desk (never its own: those are cancelled) |
| 214 | `final double[] income` | net income, a ring |
| 215 | `final double[] spent` | net income, a ring |
| 216 | `int months` | on buildings, a ring |
| 217 | `double lifetimeRaisedHome, lifetimeRaisedAbroad` | recorded so far |
| 218 | `double lifetimeDividendsHome, lifetimeDividendsAbroad` |  |
| 219 | `int offerings` |  |
| 228 | `final double[] paid` | THE DIVIDENDS IT ACTUALLY PAID, a ring of the last twelve months (0.7.12 round 2): the ordinary dividend every holder was paid, the desk's part and the world's included - the special dividends kept out while there wer... |
| 229 | `int paidMonths` |  |
| 230 | `double paidThisMonth` |  |
| 233 | `double offered, raisedHome, raisedAbroad, dividendHome, dividendDesk, dividendAbroad` | this month |
| 234 | `double boughtBackThisMonth, lifetimeBoughtBack` |  |
| 235 | `Regime regime` |  |
| 236 | `double targetShare` |  |
| 274 | `private final Listing[] listings` |  |
| 775 | `private double ordinaryPaidAtClose` | Every company's ordinary dividends at the last close (closeDividendMonth()): the month's, for the playtest. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 105 | 836 | **type** `public class Equity` | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. |
| 110 | 7 | `static { ... }` |  |
| 118 | 4 | `public static int indexOf(String company)` |  |

### the dials (lines 123-204)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 1 | `public double foundingPrice()` | A founding share in today's money. |
| 156 | 3 | `public void seedConstants(double unit)` | Re-seeds the yardstick at a given unit. |
| 199 | 3 | `public static double requiredYield(double worldRate)` | The earnings yield the market prices a company here on: what a dollar of its yearly income has to earn its owners for them to hold the share at book. |
| 203 | 1 | **type** `public enum Regime` |  |

### a company (lines 205-279)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 65 | **type** `static final class Listing` | One listing. |
| 238 | 1 | `Listing(String name)` _(in Equity.Listing)_ |  |
| 241 | 1 | `double domesticShares()` _(in Equity.Listing)_ | Held by the city's households: what is neither abroad nor on the desk. |
| 242 | 1 | `double raised()` _(in Equity.Listing)_ |  |
| 243 | 1 | `double dividend()` _(in Equity.Listing)_ |  |
| 245 | 5 | `void clearMonth()` _(in Equity.Listing)_ |  |
| 251 | 6 | `double trailingIncome()` _(in Equity.Listing)_ |  |
| 258 | 6 | `double trailingSpent()` _(in Equity.Listing)_ |  |
| 266 | 6 | `double trailingPaid()` _(in Equity.Listing)_ | The ordinary dividends paid over the last twelve months. |
| 276 | 3 | `public Equity()` |  |

### THE RECORD (lines 280-359)

| line | len | member | says |
|---:|---:|---|---|
| 292 | 22 | `public void recordMonth(int company, double netIncome, double spentOnBuildings)` |  |
| 316 | 3 | `public void startMonth()` | Clears the month's flows. |
| 320 | 17 | `private static Regime regimeOf(Listing l)` |  |
| 346 | 13 | `private static double targetEquityShareOf(Listing l)` | The share of the balance sheet a company wants as equity. |

### HOW MUCH TO RAISE (lines 360-409)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 33 | `public double raiseFor(int company, double assets, double equity, double planCost)` | What a company with this plan would raise from its owners first. |

### THE OFFERING (lines 410-544)

| line | len | member | says |
|---:|---:|---|---|
| 424 | 4 | `public double offer(int company, double amount, double bookEquity, HouseholdBalance households, double worldRate)` | Sells shares: to the households first, to the world for what is left. |
| 434 | 53 | `public double offer(int company, double amount, double bookEquity, HouseholdBalance households, double worldRate, double market...` | there is no market: a listed company sells new shares at the market, not at the register's reckoning |
| 509 | 7 | `private double priceOf(Listing l, double bookEquity, double worldRate)` | What one share sells for: the company's value over its shares. |
| 529 | 9 | `public boolean listIfUnlisted(int company, double bookEquity, HouseholdBalance households)` | Lists a company that has equity and no owners: the founders' shares are issued against its book, as the first offering would have done. |
| 540 | 4 | `public double bookPerShare(int company, double bookEquity)` | What one share is worth on the books today. |

### THE DIVIDEND (lines 545-683)

| line | len | member | says |
|---:|---:|---|---|
| 555 | 5 | `public double dividendDue(int company, double netIncome)` | What a company owes its owners on a month's result. |
| 592 | 3 | `public double dividendDue(int company, double netIncome, double principalDue)` | ...PAID AFTER THE PRINCIPAL IT OWED (0.7.11, round 2): PAYOUT of the month's net income less the principal that fell due in it, and nothing when the principal is the larger. |
| 602 | 3 | `public void noteDividendPaid(int company, double paid)` | The month's ordinary dividend, as paid - noted by Game.payDividends() beside payDividend(), which the special dividends went through too until round 4 removed them, so that only the ordinary one is a yield (0.7.12 rou... |
| 607 | 11 | `public void closeDividendMonth()` | Files every company's month of dividends into its ring, paid or not: once a month, after the dividends. |
| 629 | 10 | `public void followEmigrants(HouseholdBalance households)` | Shares that left the city with their holders this month are held abroad from now on. |
| 648 | 31 | `public double payDividend(int company, double paid, HouseholdBalance households)` | Pays a dividend: the households' part into their savings, the rest to the shareholders abroad. |
| 681 | 1 | `public double getDividendDeskThisMonth(int company)` | What the bank's trading desk was paid on its inventory this month. |
| 682 | 1 | `public double getDividendDeskThisMonth()` |  |

### THE HOLDERS (lines 684-780)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 1 | `public double getDealerShares(int company)` | Shares the desk holds; negative when it has sold what it did not have. |
| 699 | 4 | `public double getOutstanding(int company)` | Shares in the owners' hands: in issue less what the desk is long. |
| 705 | 4 | `void moveDesk(int company, double n)` | The desk's holding moves by this many shares: bought, positive; sold, negative (0.7.12 round 2). |
| 711 | 5 | `void moveForeign(int company, double n)` | ...and the world's: bought, positive; sold, negative - never under nothing. |
| 718 | 3 | `void issueOwn(int company, double n)` | The bank issues its own shares through its desk: in issue by this many. |
| 723 | 3 | `void cancelOwn(int company, double n)` | ...and buys them back, cancelled: out of issue by this many. |
| 732 | 7 | `void retire(int company, double n)` | A company buys back and cancels shares it bought on the book - the seller's holding already moved by the exchange - out of issue, and on its record of buybacks. |
| 745 | 10 | `void split(int company, double k)` | A split (k > 1) or a consolidation (k < 1): every count by k, the last price by its inverse. |
| 756 | 1 | `public double getBoughtBackThisMonth(int company)` |  |
| 757 | 1 | `public double getLifetimeBoughtBack(int company)` |  |
| 764 | 3 | `public double fairValue(int company, double bookEquity, double worldRate)` | What a share is worth on the register's own reckoning: book or capitalised earnings, whichever is more, over the shares in issue. |
| 769 | 4 | `public double dividendPerShareAnnual(int company)` | The dividend a share paid over the last twelve months - the ordinary dividend actually paid, since 0.7.12 round 2 (it was PAYOUT of the income before the principal, which nobody was paid). |
| 776 | 1 | `public double getOrdinaryDividendsLastClose()` |  |
| 779 | 1 | `public double getDividendsPaidOverYear(int company)` | The ordinary dividends paid over the last twelve months, the company's whole (0.7.12 round 2). |

### reading (lines 781-828)

| line | len | member | says |
|---:|---:|---|---|
| 783 | 1 | `public double getShares(int company)` |  |
| 784 | 1 | `public double getForeignShares(int company)` |  |
| 785 | 1 | `public double getDomesticShares(int company)` |  |
| 786 | 1 | `public double getLastPrice(int company)` |  |
| 787 | 1 | `public Regime getRegime(int company)` |  |
| 788 | 1 | `public double getTargetEquityShare(int company)` |  |
| 789 | 1 | `public int getMonthsRecorded(int company)` |  |
| 790 | 1 | `public int getOfferings(int company)` |  |
| 793 | 4 | `public double foreignShare(int company)` | Share of the company held abroad, 0-1. |
| 799 | 4 | `public double deskShare(int company)` | Share of the company on the bank's trading desk, 0-1: what the desk is long over what is in issue - nothing while it is short. |
| 804 | 1 | `public double getOfferedThisMonth(int company)` |  |
| 805 | 1 | `public double getRaisedHomeThisMonth(int company)` |  |
| 806 | 1 | `public double getRaisedAbroadThisMonth(int company)` |  |
| 807 | 1 | `public double getDividendHomeThisMonth(int company)` |  |
| 808 | 1 | `public double getDividendAbroadThisMonth(int company)` |  |
| 809 | 1 | `public double getRaisedThisMonth(int company)` |  |
| 810 | 1 | `public double getDividendThisMonth(int company)` |  |
| 812 | 1 | `public double getLifetimeRaisedHome(int company)` |  |
| 813 | 1 | `public double getLifetimeRaisedAbroad(int company)` |  |
| 814 | 1 | `public double getLifetimeDividendsHome(int company)` |  |
| 815 | 1 | `public double getLifetimeDividendsAbroad(int company)` |  |
| 819 | 1 | `public double getRaisedHomeThisMonth()` | ---- the city, for MoneyAudit and the summary ---- |
| 820 | 1 | `public double getRaisedAbroadThisMonth()` |  |
| 821 | 1 | `public double getDividendHomeThisMonth()` |  |
| 822 | 1 | `public double getDividendAbroadThisMonth()` |  |
| 823 | 1 | `public double getLifetimeRaisedHome()` |  |
| 824 | 1 | `public double getLifetimeRaisedAbroad()` |  |
| 825 | 1 | `public double getLifetimeDividendsHome()` |  |
| 826 | 1 | `public double getLifetimeDividendsAbroad()` |  |
| 827 | 1 | `public int getOfferings()` |  |

### saving (lines 829-940)

| line | len | member | says |
|---:|---:|---|---|
| 845 | 1 | `public String[] keys()` |  |
| 847 | 23 | `public double[] toSaveArray()` |  |
| 872 | 47 | `public boolean restore(String[] keys, double[] saved)` |  |
| 920 | 3 | `public void reset()` |  |
| 928 | 12 | `public void redenominate(double scale)` | Everything in money, in the new unit. |

