# Equity.java - 1,170 lines · 97 methods · 20 constants · model

`ham/citybuildersim/Equity.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [HouseholdBalance](HouseholdBalance.md) (5), [Sectors](Sectors.md) (1), [Exchange](Exchange.md) (1)

**Used by (39):** [AgricultureCheck](AgricultureCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [Household](Household.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [PeopleScreen](PeopleScreen.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [TreasuryFund](TreasuryFund.md)

## Sections

| line | section |
|---:|---|
| 123 | · the dials |
| 205 | · a company |
| 311 | THE RECORD |
| 391 | HOW MUCH TO RAISE |
| 441 | THE OFFERING |
| 605 | THE DIVIDEND |
| 753 | THE HOLDERS |
| 964 | · reading |
| 1012 | · saving |

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
| 1020 | `Equity.SLOTS_BEFORE_DESK` | `RECORD_MONTHS * 2 + 10` | Slots a company before the desk (2026-09-10, night). |
| 1023 | `Equity.SLOTS_BEFORE_PAID` | `SLOTS_BEFORE_DESK + 2` | ...and before the dividends actually paid (0.7.12 round 2). |
| 1026 | `Equity.SLOTS_BEFORE_CITY` | `SLOTS_BEFORE_PAID + RECORD_MONTHS + 1` | ...and before the city's fund (0.7.14): the ring of dividends paid and its count, appended. |
| 1029 | `Equity.SLOTS_BEFORE_PAID_IN` | `SLOTS_BEFORE_CITY + 3` | ...and before its paid-in capital (0.7.75): the city's shares, its rescue book and what it has been paid, appended (0.7.14). |
| 1032 | `Equity.SLOTS` | `SLOTS_BEFORE_PAID_IN + 3` | Its founders' book, what its buybacks paid and whether the two were derived, appended (0.7.75, R3; SAVE_FORMAT 33). |
| 1035 | `Equity.PAID_IN_FORMAT` | `33` | The first save format that keeps a company's paid-in capital (0.7.75, R3): an older save's is derived at the load (restore(), SectorBooks.derivePaidIn()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `private double foundingPrice` | The same price in TODAY's money, after any currency reform. |
| 209 | `final String name` |  |
| 210 | `double shares` | in issue |
| 211 | `double foreignShares` | in issue |
| 212 | `double dealerShares` | of those, held abroad |
| 220 | `double cityShares` | ...AND HELD BY THE CITY'S FUND (0.7.14, TreasuryFund): everything it holds, and of that the part it took in rescuing the bank - its rescue book, which its rule never sells and its 10% limit does not count. |
| 221 | `double cityRescue` |  |
| 222 | `double lastPrice` |  |
| 223 | `final double[] income` | net income, a ring |
| 224 | `final double[] spent` | net income, a ring |
| 225 | `int months` | on buildings, a ring |
| 226 | `double lifetimeRaisedHome, lifetimeRaisedAbroad` | recorded so far |
| 227 | `double lifetimeDividendsHome, lifetimeDividendsAbroad` |  |
| 228 | `int offerings` |  |
| 237 | `final double[] paid` | THE DIVIDENDS IT ACTUALLY PAID, a ring of the last twelve months (0.7.12 round 2): the ordinary dividend every holder was paid, the desk's part and the world's included - the special dividends kept out while there wer... |
| 238 | `int paidMonths` |  |
| 239 | `double paidThisMonth` |  |
| 242 | `double offered, raisedHome, raisedAbroad, dividendHome, dividendDesk, dividendAbroad` | this month |
| 244 | `double dividendCity, lifetimeDividendsCity` | The city's fund's part of this month's dividend (0.7.14), and over its life. |
| 245 | `double boughtBackThisMonth, lifetimeBoughtBack` |  |
| 261 | `double foundersBook, boughtBackPaid` | ITS PAID-IN CAPITAL, IN MONEY (0.7.75, the sector statements' R3; the project's spec-sector-statements.md, D11): the book its founders' shares were issued against, and what its buybacks paid over its life. |
| 262 | `boolean paidInDerived` |  |
| 264 | `double foundedThisMonth` | The founders' shares issued this month, at the book they were issued against: share capital out of the book, no cash moving. |
| 265 | `Regime regime` |  |
| 266 | `double targetShare` |  |
| 305 | `private final Listing[] listings` |  |
| 958 | `private double ordinaryPaidAtClose` | Every company's ordinary dividends at the last close (closeDividendMonth()): the month's, for the playtest. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 105 | 1066 | **type** `public class Equity` | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. |
| 110 | 7 | `static { ... }` |  |
| 118 | 4 | `public static int indexOf(String company)` |  |

### the dials (lines 123-204)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 1 | `public double foundingPrice()` | A founding share in today's money. |
| 156 | 3 | `public void seedConstants(double unit)` | Re-seeds the yardstick at a given unit - since 0.7.42 the unit over the expected price level it is struck at, every month (Game.restrikeMoneyConstants()). |
| 199 | 3 | `public static double requiredYield(double worldRate)` | The earnings yield the market prices a company here on: what a dollar of its yearly income has to earn its owners for them to hold the share at book. |
| 203 | 1 | **type** `public enum Regime` |  |

### a company (lines 205-310)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 96 | **type** `static final class Listing` | One listing. |
| 268 | 1 | `Listing(String name)` _(in Equity.Listing)_ |  |
| 271 | 1 | `double domesticShares()` _(in Equity.Listing)_ | Held by the city's households: what is neither abroad nor on the desk - nor, since 0.7.14, the city's fund's. |
| 272 | 1 | `double raised()` _(in Equity.Listing)_ |  |
| 273 | 1 | `double dividend()` _(in Equity.Listing)_ |  |
| 275 | 6 | `void clearMonth()` _(in Equity.Listing)_ |  |
| 282 | 6 | `double trailingIncome()` _(in Equity.Listing)_ |  |
| 289 | 6 | `double trailingSpent()` _(in Equity.Listing)_ |  |
| 297 | 6 | `double trailingPaid()` _(in Equity.Listing)_ | The ordinary dividends paid over the last twelve months. |
| 307 | 3 | `public Equity()` |  |

### THE RECORD (lines 311-390)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 22 | `public void recordMonth(int company, double netIncome, double spentOnBuildings)` |  |
| 347 | 3 | `public void startMonth()` | Clears the month's flows. |
| 351 | 17 | `private static Regime regimeOf(Listing l)` |  |
| 377 | 13 | `private static double targetEquityShareOf(Listing l)` | The share of the balance sheet a company wants as equity. |

### HOW MUCH TO RAISE (lines 391-440)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 33 | `public double raiseFor(int company, double assets, double equity, double planCost)` | What a company with this plan would raise from its owners first. |

### THE OFFERING (lines 441-604)

| line | len | member | says |
|---:|---:|---|---|
| 455 | 4 | `public double offer(int company, double amount, double bookEquity, HouseholdBalance households, double worldRate)` | Sells shares: to the households first, to the world for what is left. |
| 465 | 54 | `public double offer(int company, double amount, double bookEquity, HouseholdBalance households, double worldRate, double market...` | there is no market: a listed company sells new shares at the market, not at the register's reckoning |
| 541 | 23 | `private double priceOf(Listing l, double bookEquity, double worldRate)` | What one share sells for: the company's value over its shares. |
| 566 | 3 | `private double noValue()` | A share's worth at the market's floor, in today's money: Exchange.MIN_FAIR, seeded by the unit. |
| 582 | 10 | `public boolean listIfUnlisted(int company, double bookEquity, HouseholdBalance households)` | Lists a company that has equity and no owners: the founders' shares are issued against its book, as the first offering would have done. |
| 594 | 4 | `private static void founded(Listing l, double book)` | The founders' shares, issued against this book: its share capital from now on (0.7.75, R3). |
| 600 | 4 | `public double bookPerShare(int company, double bookEquity)` | What one share is worth on the books today. |

### THE DIVIDEND (lines 605-752)

| line | len | member | says |
|---:|---:|---|---|
| 615 | 5 | `public double dividendDue(int company, double netIncome)` | What a company owes its owners on a month's result. |
| 652 | 3 | `public double dividendDue(int company, double netIncome, double principalDue)` | ...PAID AFTER THE PRINCIPAL IT OWED (0.7.11, round 2): PAYOUT of the month's net income less the principal that fell due in it, and nothing when the principal is the larger. |
| 662 | 3 | `public void noteDividendPaid(int company, double paid)` | The month's ordinary dividend, as paid - noted by Game.payDividends() beside payDividend(), which the special dividends went through too until round 4 removed them, so that only the ordinary one is a yield (0.7.12 rou... |
| 667 | 11 | `public void closeDividendMonth()` | Files every company's month of dividends into its ring, paid or not: once a month, after the dividends. |
| 689 | 10 | `public void followEmigrants(HouseholdBalance households)` | Shares that left the city with their holders this month are held abroad from now on. |
| 708 | 36 | `public double payDividend(int company, double paid, HouseholdBalance households)` | Pays a dividend: the households' part into their savings, the rest to the shareholders abroad. |
| 746 | 1 | `public double getDividendDeskThisMonth(int company)` | What the bank's trading desk was paid on its inventory this month. |
| 749 | 1 | `public double getDividendCityThisMonth(int company)` | ...and the city's fund on what it holds (0.7.14), and over its life. |
| 750 | 1 | `public double getLifetimeDividendsCity(int company)` |  |
| 751 | 1 | `public double getDividendDeskThisMonth()` |  |

### THE HOLDERS (lines 753-963)

| line | len | member | says |
|---:|---:|---|---|
| 766 | 1 | `public double getDealerShares(int company)` | Shares the desk holds; negative when it has sold what it did not have. |
| 769 | 4 | `public double getOutstanding(int company)` | Shares in the owners' hands: in issue less what the desk is long. |
| 775 | 4 | `void moveDesk(int company, double n)` | The desk's holding moves by this many shares: bought, positive; sold, negative (0.7.12 round 2). |
| 781 | 5 | `void moveForeign(int company, double n)` | ...and the world's: bought, positive; sold, negative - never under nothing. |
| 790 | 5 | `void moveCity(int company, double n)` | The fund's market book in this company moves by this many shares: bought, positive; sold, negative - never into its rescue book, never under nothing. |
| 803 | 10 | `double sellCityByHand(int company, double n)` | The player's hand sells this many of the fund's shares: its market book first, then its rescue book (TreasuryFund - "the hand may sell anything the fund holds, including the rescue book"). |
| 829 | 10 | `double[] takeAllForCity(int company, double ifNone)` | EVERY COMMON SHARE PASSES TO THE CITY: a failed bank's resolution (0.7.14; Jerus: "City takes the shares"). |
| 841 | 7 | `void issueToCityRescue(int company, double n)` | New shares issued to the city's rescue book - warrants exercised at their expiry (0.7.14): in issue by this many, the city's by the same. |
| 850 | 1 | `public double getCityShares(int company)` | Everything the city's fund holds of this company, both books. |
| 852 | 1 | `public double getCityRescueShares(int company)` | ...of that, its rescue book. |
| 854 | 1 | `public double getCityMarketShares(int company)` | ...and its market book: what its rule bought and may sell. |
| 856 | 4 | `public double cityShare(int company)` | The city's stake in the company, 0-1: both books over what is in issue. |
| 862 | 3 | `void issueOwn(int company, double n)` | The bank issues its own shares through its desk: in issue by this many. |
| 867 | 3 | `void cancelOwn(int company, double n)` | ...and buys them back, cancelled: out of issue by this many. |
| 879 | 8 | `void retire(int company, double n, double paid)` | A company buys back and cancels shares it bought on the book - the seller's holding already moved by the exchange - out of issue, and on its record of buybacks: the shares, and since 0.7.75 what it paid for them, off ... |
| 893 | 12 | `void split(int company, double k)` | A split (k > 1) or a consolidation (k < 1): every count by k, the last price by its inverse. |
| 906 | 1 | `public double getBoughtBackThisMonth(int company)` |  |
| 907 | 1 | `public double getLifetimeBoughtBack(int company)` |  |
| 917 | 5 | `public double getPaidIn(int company)` | A sector's share capital, in money: its founders' book, what it raised at home and abroad, less what its buybacks paid. |
| 923 | 1 | `public double getFoundersBook(int company)` | The book its founders' shares were issued against; nothing on a derived figure (isPaidInDerived()). |
| 925 | 1 | `public double getBoughtBackPaid(int company)` | What its buybacks paid over its life - since the load, on a derived figure. |
| 927 | 1 | `public double getFoundedThisMonth(int company)` | The founders' shares issued this month, at their book. |
| 929 | 1 | `public boolean isPaidInDerived(int company)` | True when its paid-in was derived at the load of a save from before 0.7.75 kept it. |
| 936 | 3 | `public double fairValue(int company, double bookEquity, double worldRate)` | What a share is worth on the register's own reckoning: book or capitalised earnings, whichever is more, over the shares in issue. |
| 941 | 15 | `public double dividendPerShareAnnual(int company)` | The dividend a share paid over the last twelve months - the ordinary dividend actually paid, since 0.7.12 round 2 (it was PAYOUT of the income before the principal, which nobody was paid). |
| 959 | 1 | `public double getOrdinaryDividendsLastClose()` |  |
| 962 | 1 | `public double getDividendsPaidOverYear(int company)` | The ordinary dividends paid over the last twelve months, the company's whole (0.7.12 round 2). |

### reading (lines 964-1011)

| line | len | member | says |
|---:|---:|---|---|
| 966 | 1 | `public double getShares(int company)` |  |
| 967 | 1 | `public double getForeignShares(int company)` |  |
| 968 | 1 | `public double getDomesticShares(int company)` |  |
| 969 | 1 | `public double getLastPrice(int company)` |  |
| 970 | 1 | `public Regime getRegime(int company)` |  |
| 971 | 1 | `public double getTargetEquityShare(int company)` |  |
| 972 | 1 | `public int getMonthsRecorded(int company)` |  |
| 973 | 1 | `public int getOfferings(int company)` |  |
| 976 | 4 | `public double foreignShare(int company)` | Share of the company held abroad, 0-1. |
| 982 | 4 | `public double deskShare(int company)` | Share of the company on the bank's trading desk, 0-1: what the desk is long over what is in issue - nothing while it is short. |
| 987 | 1 | `public double getOfferedThisMonth(int company)` |  |
| 988 | 1 | `public double getRaisedHomeThisMonth(int company)` |  |
| 989 | 1 | `public double getRaisedAbroadThisMonth(int company)` |  |
| 990 | 1 | `public double getDividendHomeThisMonth(int company)` |  |
| 991 | 1 | `public double getDividendAbroadThisMonth(int company)` |  |
| 992 | 1 | `public double getRaisedThisMonth(int company)` |  |
| 993 | 1 | `public double getDividendThisMonth(int company)` |  |
| 995 | 1 | `public double getLifetimeRaisedHome(int company)` |  |
| 996 | 1 | `public double getLifetimeRaisedAbroad(int company)` |  |
| 997 | 1 | `public double getLifetimeDividendsHome(int company)` |  |
| 998 | 1 | `public double getLifetimeDividendsAbroad(int company)` |  |
| 1002 | 1 | `public double getRaisedHomeThisMonth()` | ---- the city, for MoneyAudit and the summary ---- |
| 1003 | 1 | `public double getRaisedAbroadThisMonth()` |  |
| 1004 | 1 | `public double getDividendHomeThisMonth()` |  |
| 1005 | 1 | `public double getDividendAbroadThisMonth()` |  |
| 1006 | 1 | `public double getLifetimeRaisedHome()` |  |
| 1007 | 1 | `public double getLifetimeRaisedAbroad()` |  |
| 1008 | 1 | `public double getLifetimeDividendsHome()` |  |
| 1009 | 1 | `public double getLifetimeDividendsAbroad()` |  |
| 1010 | 1 | `public int getOfferings()` |  |

### saving (lines 1012-1170)

| line | len | member | says |
|---:|---:|---|---|
| 1037 | 1 | `public String[] keys()` |  |
| 1039 | 29 | `public double[] toSaveArray()` |  |
| 1070 | 77 | `public boolean restore(String[] keys, double[] saved)` |  |
| 1148 | 3 | `public void reset()` |  |
| 1156 | 14 | `public void redenominate(double scale)` | Everything in money, in the new unit. |

