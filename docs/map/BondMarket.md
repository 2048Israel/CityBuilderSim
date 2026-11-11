# BondMarket.java - 2,119 lines · 174 methods · 8 constants · model

`ham/citybuildersim/BondMarket.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The corporate bond market: every bond the city's businesses have issued,
> who holds each, the order book each trades on, and the rule each
> participant trades by.
> 
> ==================== WHY THIS EXISTS ====================
> 
> With the landlords on insured mortgages (0.7.11) the bank failed 115 times
> over the eight default seeds, against 42 before, and the cause was
> concentration: the one bank was every business's only lender, so the
> sector that broke it - Manufacturing, Automotive, Luxury Retail - owed it
> five to six times its equity. Jerus's answer (2026-09-24): business
> borrowing becomes two things, bank loans and bonds sold to investors, and
> a business takes the cheaper. His answers to the design questions, by
> label, and where each lives:
> 
>   "Cheapest, within the bank's limit." - WHO ISSUES, AND WHEN (plan()).
>   "No limit, just price it."            - Bank, THE BANK PRICES CONCENTRATION.
>   Who buys: households; the bank, if it wants; companies with idle cash;
>   the world.                             - THE PARTICIPANTS.
>   "Bank notes rank first."               - round 1's absolute priority,
>                                            superseded in round 2 by
>   "Real averages by type."               - BusinessDebtManager, RECOVERIES
>                                            BY INSTRUMENT.
>   "Order book for both."                 - OrderBook; the bonds, and since
>                                            round 2 the shares (Exchange).
> 
> ==================== WHAT IT IS NOT ====================
> 
> Not the landlords' mortgages, which stay insured bank loans (0.7.11): a
> mortgage is not a bond. Not the city's own paper (DebtManager), which its
> bank still buys at issue and its desk still buys back. And not a place
> where anybody must trade: an order waits until somebody meets it.
> 
> EVERY DIAL HERE IS ANOTHER CLASS'S. The underwriter's charge is the one
> the city paid until CD2 (Game.FIXED_ISSUE_COST, UNDERWRITING_SPREAD; the
> city's auctions pay the fixed cost alone since); each
> participant bids by the rule it already follows for the nearest thing it
> holds today - the households by their rule for the city's paper, the
> companies and the world by the rules that send idle cash and hot money
> where the return is, the bank by its own loan price. The term is
> CorporateBond.TERM_MONTHS.

**Uses:** [CorporateBond](CorporateBond.md) (76), [OrderBook](OrderBook.md) (39), [BusinessDebtManager](BusinessDebtManager.md) (21), [OutwardInvestment](OutwardInvestment.md) (18), [Bank](Bank.md) (12), [Household](Household.md) (12), [CapitalFlows](CapitalFlows.md) (11), [HouseholdBalance](HouseholdBalance.md) (9), [SaveCopy](SaveCopy.md) (7), [TreasuryFund](TreasuryFund.md) (5), [Game](Game.md) (4), [EconomyManager](EconomyManager.md) (2), [Sectors](Sectors.md) (2), [FundLedger](FundLedger.md) (1)

**Used by (22):** [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [Exchange](Exchange.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SectorScreen](SectorScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 53 | THE PARTICIPANTS |
| 153 | what it reads |
| 213 | state |
| 262 | the bonds |
| 304 | THE BOND BOOK'S DUST, AND WHY IT IS SEEDED (0.7.12 round 6) |
| 357 | the lender's view (BondBook) |
| 454 | the valuation |
| 547 | WHAT AN ISSUE COSTS |
| 600 | THE BOOK IS BUILT |
| 772 | WHO ISSUES, AND WHEN |
| 949 | THE COUPONS AND THE PRINCIPAL |
| 1101 | THE ORDERS ARE GOOD FOR A MONTH |
| 1406 | · the city's fund (0.7.14) |
| 1499 | A HOUSEHOLD SHORT OF MONEY SELLS |
| 1588 | one order, settled |
| 1726 | the people who leave |
| 1771 | reading |
| 1861 | · THE FINANCES TAB'S SUMS (0.7.32) |
| 1929 | save and load |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 122 | `BondMarket.BANK` | `"bank"` | The bank's desk, on the book. |
| 124 | `BondMarket.WORLD` | `"world"` | The world, on the book. |
| 126 | `BondMarket.CELL` | `"household:"` | One household cell on the book - bidding, asking, or selling in the waterfall: this, then its key. |
| 128 | `BondMarket.FUND` | `"city"` | The city's fund, by its rule (0.7.14; TreasuryFund). |
| 130 | `BondMarket.FUND_HAND` | `"city:hand"` | ...and by the player's hand. |
| 145 | `BondMarket.DEFAULT_RATE_FLOOR` | `Bank.BASE_LOSS_RATE / BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | THE LEAST A DEFAULT RATE IS READ AT: the through-the-cycle default rate a sound loan is priced for, Bank.BASE_LOSS_RATE over a loan's loss given default, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT - 1.6% a year since... |
| 148 | `BondMarket.TERM_YEARS` | `CorporateBond.TERM_MONTHS / 12.0` | The term in years, for spreading an issue's costs over its life. |
| 151 | `BondMarket.CHEAPER_BY` | `1e-11` | The arithmetic a bond's all-in cost may sit over the loan's and still be "no dearer": a billionth of a point a year - the bisection's own resolution, not a margin (plan()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 178 | `private Readings readings` |  |
| 183 | `private HouseholdBalance households` |  |
| 184 | `private Bank bank` |  |
| 185 | `private EconomyManager economy` |  |
| 186 | `private OutwardInvestment outward` |  |
| 199 | `private TreasuryFund fund` | The city's fund (0.7.14), a holder and a participant; null in a fixture with none. |
| 201 | `private double fundSharesValue` | What the fund's market book's shares are worth at this step, for its 70/30 (Exchange.cityMarketValue()). |
| 203 | `private double dueCity` | The fund's coupons struck at the top of the month, paid at the step (not saved, like the others'). |
| 205 | `private final Map<Integer, Double> dueCityById` | ...bond by bond, by its id, for the fund's ledger (0.7.39): the same working, not saved. |
| 215 | `private final List<CorporateBond> bonds` |  |
| 216 | `private final Map<String, OrderBook> books` |  |
| 217 | `private int nextId` |  |
| 220 | `private double householdsBought, householdsSold` | ---- the month's flows, cleared by startMonth(), saved (the screens read them the month after) ---- |
| 221 | `private double householdsBoughtAbroad, householdsSoldAbroad` | cash, households <-> the pools |
| 222 | `private double worldBought, worldSold` | cash, households <-> the world |
| 223 | `private double couponsToHouseholds, couponsToBank, couponsToCompanies, couponsAbroad` | cash, the world <-> the pools |
| 224 | `private double principalToHouseholds, principalToBank, principalToCompanies, principalAbroad` |  |
| 230 | `private double orphanedToHouseholds` | What the issuers paid this month on the households' face (principal and coupons, by the bond's record) that no cell was credited, the record less the cells' own, signed: the census moves a cell's bond stock with the o... |
| 231 | `private double lossHouseholds, lossBank, lossCompanies, lossWorld` | face written off, by holder |
| 232 | `private double bankLossCost` | face written off, by holder |
| 233 | `private double issuedFace, issuedCosts` | what the bank's holdings cost it, written off |
| 234 | `private int issues` |  |
| 235 | `private double emigrantsFace` | households' bonds taken abroad by leavers |
| 236 | `private final Map<String, Double> issuedBySector` | households' bonds taken abroad by leavers |
| 237 | `private final Map<String, Double> proceedsBySector` |  |
| 238 | `private final Map<String, Double> repaidBySector` |  |
| 239 | `private final Map<String, Double> boughtBySector` | net cash out on bonds it holds |
| 240 | `private final Map<String, Double> couponsBySector` | net cash out on bonds it holds |
| 241 | `private final Map<String, Double> bankLossBySector` | coupons it was paid |
| 243 | `private double lastPostedBuy, lastPostedSell, lastFilled, lastSellQuantityWaited` | The last month's order-book figures, all books together, and the last issue - for the screens. |
| 244 | `private int lastSellsPosted, lastSellsWaited, lastTrades` |  |
| 247 | `private double lifeIssued, lifeCosts, lifeCouponsHouseholds, lifeCouponsBank, lifeCouponsCompanies, lifeCou...` | ---- over the city's life, saved ---- |
| 248 | `private double lifeLossHouseholds, lifeLossBank, lifeLossCompanies, lifeLossWorld` |  |
| 249 | `private double lifeVolume, lifePostedSell, lifeFilledSell, lifeWorldBought, lifeWorldSold` |  |
| 250 | `private int lifeIssues, lifeSellsPosted, lifeSellsWaited` |  |
| 252 | `private double betweenHouseholds, lifeBetweenHouseholds` | Cell to cell, this month and over the city's life: cash and trades (round 2). |
| 253 | `private int betweenHouseholdsTrades, lifeBetweenHouseholdsTrades` |  |
| 256 | `private double dueHouseholds, dueBank, dueWorld` | ---- the coupons struck at the top of the month, paid at the market's step (not saved: a month's working, settled before any save) ---- |
| 258 | `private final Map<String, Double> dueCells` | ...the households' part, cell by cell, by the cell's key: what each held at the record date (round 2). |
| 259 | `private final Map<String, Double> dueCompanies` |  |
| 260 | `private final Map<String, Double> dueByIssuer` |  |
| 326 | `private double dust` | OrderBook.DUST of face, in today's money. |
| 396 | `private final Map<String, Double> issuedSoFar` | THE FACE SOLD AND REPAID SO FAR, BY ISSUER (0.7.75, the sector statements' R7): counted where a bond is issued and where it is repaid at maturity, in memory, from nothing at a founding or a load. |
| 397 | `private final Map<String, Double> repaidSoFar` |  |
| 449 | `private final Map<String, Double> bankLossTaken` | What takeBankLoss() handed Game this month, by sector, for reading after (the Bank tab's write-off by sector is the loans' and this). |
| 627 | `final String issuer` |  |
| 628 | `final double deposit, world, premium, yearOfOutput, faceOutstanding, householdSpare, bankRoom` |  |
| 629 | `final boolean running, bankBuys` |  |
| 630 | `final String[] companies` |  |
| 631 | `final double[] cash, wealth, othersFace` |  |
| 667 | `final Bidders who` |  |
| 668 | `final String issuer` |  |
| 669 | `final double size, offered, el, deposit, world, premium, yearOfOutput, faceOutstanding, householdSpare` |  |
| 670 | `final String[] companies` |  |
| 671 | `final double[] cash, wealth, othersFace` |  |
| 672 | `final double bankYield, bankRoom` |  |
| 673 | `final boolean running` |  |
| 939 | `private String lastIssue` | The last issue, for the Bonds page: who, how much, at what, against the bank's rate, when. |
| 940 | `private double lastIssueFace, lastIssueCoupon, lastIssueLoanRate` |  |
| 941 | `private int lastIssueMonth` |  |
| 1403 | `private Household posting` | The cell posting its bids at the step right now, and what it may still spend this month. |
| 1404 | `private double postingBudget` |  |
| 1548 | `private Household selling` | The cell selling in the waterfall right now, whose proceeds go back to its waterfall rather than into its savings. |
| 1549 | `private double raised` |  |
| 1585 | `private double companiesSoldShort` | What companies short of money raised selling their bonds this month, from the credit settle (round 4); a month's figure, cleared with the market's month. |
| 1602 | `final CorporateBond b` |  |
| 1933 | `List<CorporateBond> bonds` |  |
| 1934 | `List<OrderBook> books` |  |
| 1935 | `int nextId` |  |
| 1936 | `double[] month` |  |
| 1937 | `double[] life` |  |
| 1938 | `String lastIssue` |  |
| 1939 | `double[] lastIssueFigures` |  |
| 1940 | `Map<String, Double> issuedBySector, proceedsBySector, repaidBySector, boughtBySector, couponsBySector` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 2069 | **type** `public class BondMarket implements BusinessDebtManager.BondBook, BusinessDebtManager.BondDesk` | The corporate bond market: every bond the city's businesses have issued, who holds each, the order book each trades on, and the rule each participant trades by. |

### THE PARTICIPANTS (lines 53-152)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 1 | `static boolean isFund(String who)` | True for either of the fund's names. |

### what it reads (lines 153-212)

| line | len | member | says |
|---:|---:|---|---|
| 156 | 21 | **type** `public interface Readings` | The city's readings the market prices on, which Game supplies. |
| 157 | 1 | `int month()` _(in BondMarket.Readings)_ |  |
| 159 | 1 | `double curve(int months)` _(in BondMarket.Readings)_ | The city's curve at this many months (DebtManager.curveRate()). |
| 161 | 1 | `double policyRate()` _(in BondMarket.Readings)_ | The policy rate, for the bank's own prices. |
| 163 | 1 | `double depositRate()` _(in BondMarket.Readings)_ | What the bank pays savers. |
| 165 | 1 | `double worldRate()` _(in BondMarket.Readings)_ | What the world pays (DebtManager.worldRate(): the US's policy rate where the city has the world file, XR4). |
| 167 | 1 | `double countryPremium()` _(in BondMarket.Readings)_ | What the world charges this city for its risk (DebtManager.countryPremium()). |
| 169 | 1 | `double monthlyGdp()` _(in BondMarket.Readings)_ | A month's output. |
| 171 | 1 | `double localPerUsd()` _(in BondMarket.Readings)_ | Local money per dollar. |
| 173 | 1 | `boolean worldRunning()` _(in BondMarket.Readings)_ | True while hot money is running (CapitalFlows.isStopped()). |
| 175 | 1 | `double unit()` _(in BondMarket.Readings)_ | The currency's unit (Denomination.getUnit()), for the underwriter's fixed fee. |
| 181 | 1 | `Readings readings()` | What it was attached to read, for a harness (CurveCheck, CD1). |
| 189 | 8 | `public void attach(Readings readings, HouseholdBalance households, Bank bank, EconomyManager economy, OutwardInvestment outward)` | Wires the market into a city. |
| 208 | 1 | `private int ledgerMonth()` | The month the fund's ledger dates what it writes by: the city's, read live. |
| 211 | 1 | `public void attachFund(TreasuryFund fund)` | Wires the city's fund in (0.7.14). |

### state (lines 213-261)

### the bonds (lines 262-303)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 1 | `public List<CorporateBond> getBonds()` | Every bond outstanding, oldest first. |
| 268 | 5 | `public List<CorporateBond> getBonds(String issuer)` | One sector's bonds. |
| 275 | 5 | `public double maturingFace(String issuer, int month)` | The face of one issuer's bonds that fall due at `month`'s settle (redeemMaturing()): asked ahead by what a buyer can pay for (0.7.12 round 6). |
| 282 | 4 | `public CorporateBond bond(int id)` | The bond with this id, or null. |
| 288 | 4 | `public OrderBook bookOf(CorporateBond b)` | The order book a bond trades on - a reader: an empty one, not kept, for a bond with none. |
| 294 | 9 | `private OrderBook openBook(CorporateBond b)` | ...and the one the market posts to, opened if it has none - at the market's dust (see dust). |

### THE BOND BOOK'S DUST, AND WHY IT IS SEEDED (0.7.12 round 6) (lines 304-356)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 4 | `public void seedConstants(double unit)` | Re-seeds the dust at a given unit, and every open book's with it. |
| 335 | 5 | `public double totalFace()` | Every bond's face outstanding. |
| 342 | 1 | `public double faceHeldByHouseholds()` | ...held by each class. |
| 343 | 1 | `public double faceHeldByBank()` |  |
| 344 | 1 | `public double faceHeldByWorld()` |  |
| 345 | 1 | `public double faceHeldByCompanies()` |  |
| 347 | 1 | `public double faceHeldBy(String sector)` | ...by one company. |
| 349 | 1 | `public double bankCost()` | What the bank's bonds cost it: its carrying value. |
| 351 | 5 | `public double bankCost(String issuer)` | ...of one issuer's bonds. |

### the lender's view (BondBook) (lines 357-453)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 5 | `public double principal(String sector)` |  |
| 365 | 5 | `public double monthlyCoupon(String sector)` |  |
| 372 | 5 | `public double faceDueWithin(String sector, int month, int months)` | The face of one issuer's bonds maturing within `months` settles of `month`'s (0.7.74, the sector statements' R2): a read, as maturingFace() is. |
| 379 | 5 | `public int lastMaturity(String sector)` | The month one issuer's last bond falls due (0.7.75, R2's "runs to"); 0 with none. |
| 386 | 1 | `public double couponRate(String sector)` | ...and the coupon its bonds pay, weighted by face (R2's rate): averageCoupon(). |
| 399 | 1 | `public double faceIssuedSoFar(String sector)` |  |
| 400 | 1 | `public double faceRepaidSoFar(String sector)` |  |
| 411 | 29 | `public double writeDown(String sector, double scale)` | A DEFAULT, ON THE BONDHOLDERS (0.7.12): every bond of the sector written down to this share of its face, every holder by the same share - the households' face and every cell's own face of it (round 2), the bank's face... |
| 442 | 5 | `public double takeBankLoss(String sector)` | What the month's defaults took off the bank's bonds of this sector, at what it paid - read once by Game, which books it against the bank's allowance with the sector's loans. |
| 452 | 1 | `public double getBankLossThisMonth(String sector)` | ...one sector's: the bank's loss on its bonds this month, at what it paid (0.7.12, round 2). |

### the valuation (lines 454-546)

| line | len | member | says |
|---:|---:|---|---|
| 457 | 1 | `private BusinessDebtManager credit()` | The lender, for the issuer's readings. |
| 465 | 8 | `public double defaultRate(String issuer, double extraDebt, double extraAssets)` | THE ISSUER'S DEFAULT RATE, a year, for a bondholder: 0.7.8's curve at the leverage its last quarter reads, with a deal on top - `extraDebt` owed and `extraAssets` owned - never under DEFAULT_RATE_FLOOR. |
| 482 | 3 | `public double expectedLoss(String issuer)` | WHAT A BONDHOLDER EXPECTS TO LOSE a year on this issuer's bonds: its default rate times a bond's loss given default, BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT - whatever else the issuer owes, since 0.7.12 round 2 (R... |
| 487 | 4 | `public double modelYield(CorporateBond b, int month)` | WHAT A BOND IS WORTH TO THE MARKET: the city's curve for the months it has left plus what a holder expects to lose on it - the yield the participants bid and ask around. |
| 493 | 3 | `public double modelPrice(CorporateBond b, int month)` | ...as a price a unit of face. |
| 498 | 4 | `public double lastPrice(CorporateBond b)` | The last price it traded at on its book, or NaN before its first trade. |
| 504 | 5 | `public double lastYield(CorporateBond b, int month)` | The yield at its last traded price, or at its value before its first trade. |
| 527 | 13 | `public double bankYield(String issuer, int months, double extraBonds, double dealDebt, double extraAssets)` | WHAT THE BANK WOULD EARN LENDING THE ISSUER THE SAME MONEY: an equal loan's four parts at the bond's term (Bank.loanRate() at RISK_BUSINESS), the book's concentration on the issuer (Bank.concentrationCharge()), the is... |
| 542 | 4 | `private boolean bankBuys()` | True while the bank stands, has a branch, and is over its capital target: the only bank that buys. |

### WHAT AN ISSUE COSTS (lines 547-599)

| line | len | member | says |
|---:|---:|---|---|
| 565 | 5 | `public double issueCost(double face)` | What an issue of this face costs to bring: Game.FIXED_ISSUE_COST, in today's money, and Game.UNDERWRITING_SPREAD of the face. |
| 572 | 3 | `public double allIn(double face, double coupon)` | A bond's all-in cost a year: its coupon and its issuing costs spread over its life. |
| 577 | 3 | `public static double loanAllIn(double loanRate)` | A bank loan's all-in cost a year: its rate and its fee (Bank.LOAN_FEE) spread over its term, BusinessDebtManager.LOAN_TERM_MONTHS - the loan's own issuing cost, counted as the bond's are. |
| 593 | 6 | `public double crossover(double loanRate, double coupon)` | THE SIZE AT WHICH A BOND BEGINS TO BEAT THE BANK: the face at which the coupon and the costs spread over the life equal the loan's all-in cost - |

### THE BOOK IS BUILT (lines 600-771)

| line | len | member | says |
|---:|---:|---|---|
| 626 | 38 | **type** `final class Bidders` | What every bidder brings to a new issue by this issuer, read once: the rates, the households' spare savings, each company's till and wealth, the bank's room. |
| 633 | 30 | `Bidders(String issuer)` _(in BondMarket.Bidders)_ |  |
| 666 | 88 | **type** `final class Demand` | Every participant's bid for one new issue of this size, offered at this size, as a function of its coupon. |
| 675 | 3 | `Demand(String issuer, double size, double offered, double dealDebt, double extraAssets)` _(in BondMarket.Demand)_ |  |
| 687 | 24 | `Demand(Bidders who, double size, double offered, double dealDebt, double extraAssets)` _(in BondMarket.Demand)_ | bid is an amount of (THE BOOK IS BUILT) |
| 713 | 5 | `double households(double y)` _(in BondMarket.Demand)_ | The households: the city's paper's share of the issue offered at this expected return, out of savings past the cushion. |
| 720 | 6 | `double world(double y)` _(in BondMarket.Demand)_ | The world: a month of closing the gap to hot money's target, the offered issue's share of it. |
| 728 | 7 | `double company(int i, double y)` _(in BondMarket.Demand)_ | One company: a month of closing the gap to OutwardInvestment's share of its wealth, the offered issue's part of it. |
| 737 | 1 | `double bank(double y)` _(in BondMarket.Demand)_ | The bank: all its room at its loan-equivalent yield or over, nothing under. |
| 739 | 5 | `double total(double y)` _(in BondMarket.Demand)_ |  |
| 746 | 7 | `double ceiling()` _(in BondMarket.Demand)_ | The yield past which nobody's bid grows: the book's ceiling. |
| 756 | 10 | `double clearingYield(Demand dm, double face)` | The lowest coupon at which the bids fill `face`, or NaN when nothing up to the book's ceiling does. |
| 768 | 3 | `public double quoteCoupon(String issuer, double face, double extraAssets)` | The coupon a new issue of this face would clear at today, or NaN - for the screens and the harness. |

### WHO ISSUES, AND WHEN (lines 772-948)

| line | len | member | says |
|---:|---:|---|---|
| 804 | 59 | `public BusinessDebtManager.Plan plan(String sector, double amount, double loanRate, double loanRoom, double bondRoom, double ex...` |  |
| 873 | 64 | `public double issue(BusinessDebtManager.Plan plan, int month)` | SELLS A PLAN'S BOND, at its coupon, at par: every participant takes its bid at that coupon, pro rata if the bids more than fill it, and pays; the bank, as underwriter, is paid the costs out of the proceeds. |
| 943 | 1 | `public String getLastIssuer()` |  |
| 944 | 1 | `public double getLastIssueFace()` |  |
| 945 | 1 | `public double getLastIssueCoupon()` |  |
| 946 | 1 | `public double getLastIssueLoanRate()` |  |
| 947 | 1 | `public int getLastIssueMonth()` |  |

### THE COUPONS AND THE PRINCIPAL (lines 949-1100)

| line | len | member | says |
|---:|---:|---|---|
| 971 | 36 | `public void strikeCoupons()` | Strikes this month's coupons by who holds each bond now: the record date. |
| 1009 | 5 | `public double getCouponsStruck()` | Every coupon struck this month, all holders: what the issuers' statements paid. |
| 1016 | 1 | `public double getCouponsDueToBank()` | The bank's part of this month's coupons, paid it at the settle with its loans' interest. |
| 1026 | 37 | `public void redeemMaturing(int month)` | PAYS EVERY BOND THAT FALLS DUE THIS MONTH: its face out of the issuer's till - which may go short, for the credit settle to cover (see THE COUPONS AND THE PRINCIPAL) - to each holder: every cell its own face, into its... |
| 1065 | 35 | `private void payCoupons()` | Pays the month's coupons struck at the top to everybody but the bank. |

### THE ORDERS ARE GOOD FOR A MONTH (lines 1101-1405)

| line | len | member | says |
|---:|---:|---|---|
| 1121 | 26 | `public void startMonth()` | Opens the month's flows. |
| 1153 | 3 | `public void takeMonth(int month)` | THE MARKET'S MONTH, after the shares have traded and before the sectors move their money abroad: the coupons paid, last month's orders withdrawn, every bond valued, and every participant's orders posted. |
| 1158 | 42 | `public void takeMonth(int month, double fundSharesValue)` | ...with what the city's fund's shares are worth this month (0.7.14), for its mix; its orders are posted last (postFund()). |
| 1202 | 14 | `private void noteBook(OrderBook book)` | Adds a book's month, as it closed, to the market's record. |
| 1218 | 29 | `private void postBank(int month, Map<String, Double> el, double[] value)` | The bank: over its target, a bid at its loan-equivalent price on its spare capital; under it, an ask at the bond's value for what takes it back. |
| 1249 | 26 | `private void postWorld(int month, Map<String, Double> el)` | The world: hot money's target in each bond, its share of the stock the spread calls for; a bid for a month of the gap, an ask for the excess. |
| 1277 | 43 | `private void postCompanies(int month, Map<String, Double> el)` | The companies: OutwardInvestment's share of each owing-nothing company's wealth, spread over the other sectors' bonds by face; a bid for OUT_SPEED of the gap, an ask for HOME_SPEED of the excess. |
| 1335 | 66 | `private void postHouseholds(int month, Map<String, Double> el)` | THE HOUSEHOLDS, CELL BY CELL (0.7.12 round 2): round 1's rule for the pool, applied to each cell's own savings and holdings. |

### the city's fund (0.7.14) (lines 1406-1498)

| line | len | member | says |
|---:|---:|---|---|
| 1424 | 53 | `private void postFund(int month, double[] value)` | THE CITY'S FUND (TreasuryFund), BY ITS RULE AND THEN BY THE PLAYER'S HAND, last: the rest of the fund's market book and cash in bonds - the share Exchange's rule leaves, 1 - TreasuryFund.EQUITY_WEIGHT - spread over th... |
| 1479 | 4 | `private int indexOf(int id)` | Where a bond is in the list, by its id, or -1. |
| 1485 | 1 | `public double faceHeldByCity()` | The city's fund's face in every bond, together (0.7.14). |
| 1488 | 10 | `public double fundBondRoom(int month)` | The money that would buy every live bond's face the city's fund does not hold, at the bonds' value this month (RB1): the room its rule's bond bids could still take, for TreasuryFund.idleCash(). |

### A HOUSEHOLD SHORT OF MONEY SELLS (lines 1499-1587)

| line | len | member | says |
|---:|---:|---|---|
| 1513 | 33 | `double sellForCell(Household cell, double needPer, double borrowingRate)` |  |
| 1565 | 18 | `public double sellForCompany(String sector, double need, double borrowingRate)` | A COMPANY SHORT OF MONEY SELLS (0.7.12 round 4, CAN'T PAY MEANS DEFAULT - see BusinessDebtManager), by the households' rule above: in each other sector's bond it holds, it asks the price at which the buyer's yield wou... |
| 1586 | 1 | `public double getCompaniesSoldShort()` |  |

### one order, settled (lines 1588-1725)

| line | len | member | says |
|---:|---:|---|---|
| 1591 | 3 | `List<OrderBook.Fill> tradeForCheck(CorporateBond b, String who, OrderBook.Side side, double price, double quantity)` | One order on a bond's book, settled, between two steps: for FundLedgerCheck (0.7.39), as Exchange.tradeForCheck(). |
| 1595 | 4 | `private void submit(CorporateBond b, String who, OrderBook.Side side, double price, double quantity, int month)` |  |
| 1601 | 119 | **type** `private final class Settle implements OrderBook.Clearing` | Where each participant's money and holding is, for one bond's book. |
| 1603 | 1 | `Settle(CorporateBond b)` _(in BondMarket.Settle)_ |  |
| 1605 | 26 | `public double capacity(String who, OrderBook.Side side, double price)` _(in BondMarket.Settle)_ |  |
| 1633 | 3 | `public boolean mayTrade(String buyer, String seller)` _(in BondMarket.Settle)_ | The fund's rule and its hand never trade with each other (0.7.14). |
| 1637 | 82 | `public void settle(String buyer, String seller, double q, double price)` _(in BondMarket.Settle)_ |  |
| 1722 | 3 | `private Household cellOf(String who)` | The cell a participant's name is, or null. |

### the people who leave (lines 1726-1770)

| line | len | member | says |
|---:|---:|---|---|
| 1735 | 8 | `public void householdLeft(int id, double face)` | WHAT A HOUSEHOLD THAT LEAVES TAKES WITH IT: its bonds, which it holds from abroad from then on - as a leaver's shares are held abroad (Equity.followEmigrants()) - taken off the households' face of the bond and put on ... |
| 1754 | 9 | `public void recountHouseholds()` | Every bond's households' face, checked against the cells that hold it after a load, which restores the two separately (round 2). |
| 1765 | 5 | `public Map<Integer, Double> householdsFaceById()` | The households' face in each bond, by its id: what an old pooled save hands the cells (HouseholdBalance.claimPooledBonds()). |

### reading (lines 1771-1860)

| line | len | member | says |
|---:|---:|---|---|
| 1774 | 1 | `public double getHouseholdsBought()` | The households' cash into the pools for bonds this month: at issue and from the bank and the companies. |
| 1776 | 1 | `public double getHouseholdsSold()` | ...and out of the pools to them, for bonds they sold to the bank and the companies. |
| 1778 | 1 | `public double getHouseholdsBoughtAbroad()` | What the households paid the world for bonds, and the world paid them: both outside the pools, declared as pairs. |
| 1779 | 1 | `public double getHouseholdsSoldAbroad()` |  |
| 1781 | 1 | `public double getWorldBought()` | The world's money into the pools for bonds - at issue and from the bank and the companies - and out of them for bonds it sold them. |
| 1782 | 1 | `public double getWorldSold()` |  |
| 1784 | 1 | `public double getWorldPurchases()` | Every bond purchase the world made this month, whoever sold, and every sale. |
| 1785 | 1 | `public double getWorldSales()` |  |
| 1786 | 1 | `public double getCouponsToHouseholds()` |  |
| 1787 | 1 | `public double getCouponsToBank()` |  |
| 1788 | 1 | `public double getCouponsToCompanies()` |  |
| 1789 | 1 | `public double getCouponsAbroad()` |  |
| 1790 | 1 | `public double getPrincipalToHouseholds()` |  |
| 1791 | 1 | `public double getPrincipalToBank()` |  |
| 1792 | 1 | `public double getPrincipalToCompanies()` |  |
| 1793 | 1 | `public double getPrincipalAbroad()` |  |
| 1795 | 1 | `public double getOrphanedToHouseholds()` | The principal and coupons the issuers paid this month on the households' face that no cell was credited (HL1): the bond's record less the cells' own. |
| 1797 | 1 | `public double getLossHouseholds()` | Face written off this month's defaults, by holder. |
| 1798 | 1 | `public double getLossBank()` |  |
| 1799 | 1 | `public double getLossCompanies()` |  |
| 1800 | 1 | `public double getWorldWrittenOff()` |  |
| 1801 | 1 | `public double getIssuedFace()` |  |
| 1802 | 1 | `public double getIssuedCosts()` |  |
| 1803 | 1 | `public int getIssues()` |  |
| 1804 | 1 | `public double getEmigrantsFace()` |  |
| 1807 | 1 | `public double getIssued(String sector)` | One sector's month: the face it issued, what that handed it, the principal it repaid, what it spent on other sectors' bonds (net of sales and principal back), and the coupons it was paid. |
| 1808 | 1 | `public double getProceeds(String sector)` |  |
| 1809 | 1 | `public double getRepaid(String sector)` |  |
| 1810 | 1 | `public double getBoughtNet(String sector)` |  |
| 1811 | 1 | `public double getCouponsTo(String sector)` |  |
| 1814 | 1 | `public double getLastPostedBuy()` | Last month's order book, every bond together: posted to buy and to sell, filled, sell orders posted and those that waited unfilled, and trades. |
| 1815 | 1 | `public double getLastPostedSell()` |  |
| 1816 | 1 | `public double getLastFilled()` |  |
| 1817 | 1 | `public int getLastSellsPosted()` |  |
| 1818 | 1 | `public int getLastSellsWaited()` |  |
| 1819 | 1 | `public double getLastSellQuantityWaited()` |  |
| 1820 | 1 | `public int getLastTrades()` |  |
| 1823 | 1 | `public double getLifeIssued()` | Over the city's life. |
| 1824 | 1 | `public double getLifeCosts()` |  |
| 1825 | 1 | `public int getLifeIssues()` |  |
| 1826 | 1 | `public double getLifeCouponsHouseholds()` |  |
| 1827 | 1 | `public double getLifeCouponsBank()` |  |
| 1828 | 1 | `public double getLifeCouponsCompanies()` |  |
| 1829 | 1 | `public double getLifeCouponsAbroad()` |  |
| 1830 | 1 | `public double getLifeLossHouseholds()` |  |
| 1831 | 1 | `public double getLifeLossBank()` |  |
| 1832 | 1 | `public double getLifeLossCompanies()` |  |
| 1833 | 1 | `public double getLifeLossWorld()` |  |
| 1834 | 1 | `public double getLifeVolume()` |  |
| 1835 | 1 | `public double getLifePostedSell()` |  |
| 1836 | 1 | `public double getLifeFilledSell()` |  |
| 1837 | 1 | `public int getLifeSellsPosted()` |  |
| 1838 | 1 | `public int getLifeSellsWaited()` |  |
| 1839 | 1 | `public double getLifeWorldBought()` |  |
| 1840 | 1 | `public double getLifeWorldSold()` |  |
| 1842 | 1 | `public double getBetweenHouseholds()` | One cell buying from another (round 2): this month's cash and trades, and the city's life's - a transfer inside the households. |
| 1843 | 1 | `public int getBetweenHouseholdsTrades()` |  |
| 1844 | 1 | `public double getLifeBetweenHouseholds()` |  |
| 1845 | 1 | `public int getLifeBetweenHouseholdsTrades()` |  |
| 1848 | 5 | `public double averageCoupon(String sector)` | One sector's bonds' coupon, weighted by face: what its bond debt costs a year. |
| 1855 | 5 | `public double averageCoupon()` | ...and every bond's. |

### THE FINANCES TAB'S SUMS (0.7.32) (lines 1861-1928)

| line | len | member | says |
|---:|---:|---|---|
| 1867 | 3 | `public double getCouponsPaid()` | The month's coupons, every holder's: the households', the bank's, the companies' and the world's. |
| 1872 | 3 | `public double getPrincipalRepaid()` | ...the principal repaid at maturity, to every holder. |
| 1877 | 3 | `public double getWrittenOffThisMonth()` | ...and the face written off in defaults, every holder's. |
| 1887 | 3 | **type** `public record Issuer(String issuer, int bonds, double face, double lowCoupon, double highCoupon, int neares...` | One issuer's bonds outstanding together: how many and their face, the lowest and highest coupon, the nearest maturity, its largest bond's number (the one its book opens on), and the face each holder class holds - the ... |
| 1892 | 26 | `public List<Issuer> byIssuer()` | Every issuer with a bond outstanding, biggest face first. |
| 1920 | 8 | `public double valueHeld(java.util.function.ToDoubleFunction<CorporateBond> holding, int month)` | What a holder class's bonds are worth at the bonds' value this month. |

### save and load (lines 1929-2119)

| line | len | member | says |
|---:|---:|---|---|
| 1932 | 36 | **type** `public static final class State implements Cloneable` | Everything the market carries from one month to the next, as the save writes it. |
| 1949 | 18 | `State copy()` _(in BondMarket.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() lists the market's own bonds and books, which the months go on changing while the autosave is written. |
| 1969 | 30 | `public State toState()` |  |
| 2001 | 51 | `public void restore(State s)` | Puts a saved market back. |
| 2054 | 1 | `private static double finite(double v)` | A save carries no NaN: nothing where there was none. |
| 2056 | 5 | `private static void putAll(Map<String, Double> into, Map<String, Double> from)` |  |
| 2063 | 25 | `public void reset()` | No bonds, no books, nothing over the city's life: a new city. |
| 2090 | 29 | `public void redenominate(double scale)` | Every money figure in the new unit: the bonds' faces and holdings, the orders' quantities, the month's flows. |

