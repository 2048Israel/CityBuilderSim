# BondMarket.java - 2,342 lines · 194 methods · 16 constants · model

`ham/citybuildersim/BondMarket.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

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
> THE CORPORATE DIALS COME FROM THE EXISTING MARKETS. The underwriter's charge is the one
> the city paid until CD2 (Game.FIXED_ISSUE_COST, UNDERWRITING_SPREAD; the
> city's auctions pay the fixed cost alone since); each
> participant bids by the rule it already follows for the nearest thing it
> holds today - the households by their rule for the city's paper, the
> companies and the world by the rules that send idle cash and hot money
> where the return is, the bank by its own loan price. The term is
> CorporateBond.TERM_MONTHS. SZ1 also keeps the city's reference-price issue
> lot dials here, shared by the borrowing pages and automatic funding.

**Uses:** [CorporateBond](CorporateBond.md) (78), [OrderBook](OrderBook.md) (39), [BusinessDebtManager](BusinessDebtManager.md) (22), [OutwardInvestment](OutwardInvestment.md) (18), [Household](Household.md) (16), [SaveCopy](SaveCopy.md) (13), [Bank](Bank.md) (12), [CapitalFlows](CapitalFlows.md) (11), [HouseholdBalance](HouseholdBalance.md) (9), [Game](Game.md) (6), [TreasuryFund](TreasuryFund.md) (5), [MagnitudeSteps](MagnitudeSteps.md) (2), [PriceLevel](PriceLevel.md) (2), [EconomyManager](EconomyManager.md) (2), [EstateCustody](EstateCustody.md) (2), [Sectors](Sectors.md) (2), [FundLedger](FundLedger.md) (1)

**Used by (32):** [BankFailureCheck](BankFailureCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [EstateCustody](EstateCustody.md), [Exchange](Exchange.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SectorScreen](SectorScreen.md), [StepsCheck](StepsCheck.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 57 | THE PARTICIPANTS |
| 163 | THE CITY'S ISSUE LOTS (SZ1) |
| 217 | what it reads |
| 325 | state |
| 375 | the bonds |
| 417 | THE BOND BOOK'S DUST, AND WHY IT IS SEEDED (0.7.12 round 6) |
| 471 | the lender's view (BondBook) |
| 572 | the valuation |
| 665 | WHAT AN ISSUE COSTS |
| 718 | THE BOOK IS BUILT |
| 897 | WHO ISSUES, AND WHEN |
| 1074 | THE COUPONS AND THE PRINCIPAL |
| 1246 | THE ORDERS ARE GOOD FOR A MONTH |
| 1595 | · the city's fund (0.7.14) |
| 1688 | A HOUSEHOLD SHORT OF MONEY SELLS |
| 1777 | one order, settled |
| 1917 | the people who leave |
| 1962 | reading |
| 2052 | · THE FINANCES TAB'S SUMS (0.7.32) |
| 2120 | save and load |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 126 | `BondMarket.BANK` | `"bank"` | The bank's desk, on the book. |
| 128 | `BondMarket.WORLD` | `"world"` | The world, on the book. |
| 130 | `BondMarket.CELL` | `"household:"` | One household cell on the book - bidding, asking, or selling in the waterfall: this, then its key. |
| 132 | `BondMarket.FUND` | `"city"` | The city's fund, by its rule (0.7.14; TreasuryFund). |
| 134 | `BondMarket.FUND_HAND` | `"city:hand"` | ...and by the player's hand. |
| 149 | `BondMarket.DEFAULT_RATE_FLOOR` | `Bank.BASE_LOSS_RATE / BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | THE LEAST A DEFAULT RATE IS READ AT: the through-the-cycle default rate a sound loan is priced for, Bank.BASE_LOSS_RATE over a loan's loss given default, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT - 1.6% a year since... |
| 152 | `BondMarket.TERM_YEARS` | `CorporateBond.TERM_MONTHS / 12.0` | The term in years, for spreading an issue's costs over its life. |
| 155 | `BondMarket.CHEAPER_BY` | `1e-11` | The arithmetic a bond's all-in cost may sit over the loan's and still be "no dearer": a billionth of a point a year - the bisection's own resolution, not a margin (plan()). |
| 158 | `BondMarket.ISSUE_ROUNDING` | `1e-14` | The relative shortfall at the issue-size fixed point, below the nominal twins' 1e-9 tolerance even after repeated refinancing (BS1). |
| 161 | `BondMarket.PRICE_TICK` | `1e-12` | The price step per unit of bond face, so mathematically equal bids retain arrival priority across currency units (OB1, BS1). |
| 166 | `BondMarket.BILL_LOT` | `1_000` | A bill's lot in thousands at the reference price level, repriced at the month and capped by the minimum issue. |
| 168 | `BondMarket.BOND_LOT` | `100_000` | A fixed-rate bond's lot in thousands at the reference price level, repriced at the month and capped by the minimum issue. |
| 170 | `BondMarket.AMORTIZING_LOT` | `10_000` | An amortizing bond's lot in thousands at the reference price level, repriced at the month and capped by the minimum issue. |
| 172 | `BondMarket.FLOATING_LOT` | `100_000` | A floating-rate note's lot in thousands at the reference price level, repriced at the month and capped by the minimum issue. |
| 174 | `BondMarket.LINKED_LOT` | `100_000` | An inflation-linked bond's lot in thousands at the reference price level, repriced at the month and capped by the minimum issue. |
| 439 | `BondMarket.FACE_DUST` | `PriceLevel.atFounding(OrderBook.DUST)` | OrderBook.DUST of face is money, seeded at founding prices (BF1). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 242 | `private Readings readings` |  |
| 247 | `private HouseholdBalance households` |  |
| 248 | `private Bank bank` |  |
| 249 | `private EconomyManager economy` |  |
| 250 | `private OutwardInvestment outward` |  |
| 263 | `private TreasuryFund fund` | The city's fund (0.7.14), a holder and a participant; null in a fixture with none. |
| 265 | `private double fundSharesValue` | What the fund's market book's shares are worth at this step, for its 70/30 (Exchange.cityMarketValue()). |
| 267 | `private double dueCity, dueCustody` | The fund's and passive custody's record-date coupons, saved until actually paid. |
| 268 | `private EstateCustody custody` |  |
| 269 | `private boolean couponRecordOpen, couponsCharged, bankCouponsBooked` |  |
| 270 | `private double couponCash, couponIssuerDebit, couponBankPaid` |  |
| 271 | `private final Map<String, Double> recordDueCells` |  |
| 272 | `private final Map<Integer, Double> dueCustodyById` |  |
| 317 | `private final Map<Integer, Double> dueCityById` | ...bond by bond, by its id, for the fund's ledger (0.7.39): the same saved record-date working. |
| 327 | `private final List<CorporateBond> bonds` |  |
| 328 | `private final Map<String, OrderBook> books` |  |
| 329 | `private int nextId` |  |
| 332 | `private double householdsBought, householdsSold` | ---- the month's flows, cleared by startMonth(), saved (the screens read them the month after) ---- |
| 333 | `private double householdsBoughtAbroad, householdsSoldAbroad` | cash, households <-> the pools |
| 334 | `private double worldBought, worldSold` | cash, households <-> the world |
| 335 | `private double couponsToHouseholds, couponsToBank, couponsToCompanies, couponsAbroad` | cash, the world <-> the pools |
| 336 | `private double principalToHouseholds, principalToBank, principalToCompanies, principalAbroad` |  |
| 342 | `private double orphanedToHouseholds` | What the issuers paid this month on the households' face (principal and coupons, by the bond's record) that no cell was credited, the record less the cells' own, signed: the census moves a cell's bond stock with the o... |
| 343 | `private double couponsToCustody, principalToCustody, lossCustody` |  |
| 344 | `private double lossHouseholds, lossBank, lossCompanies, lossWorld` | face written off, by holder |
| 345 | `private double bankLossCost` | face written off, by holder |
| 346 | `private double issuedFace, issuedCosts` | what the bank's holdings cost it, written off |
| 347 | `private int issues` |  |
| 348 | `private double emigrantsFace` | households' bonds taken abroad by leavers |
| 349 | `private final Map<String, Double> issuedBySector` | households' bonds taken abroad by leavers |
| 350 | `private final Map<String, Double> proceedsBySector` |  |
| 351 | `private final Map<String, Double> repaidBySector` |  |
| 352 | `private final Map<String, Double> boughtBySector` | net cash out on bonds it holds |
| 353 | `private final Map<String, Double> couponsBySector` | net cash out on bonds it holds |
| 354 | `private final Map<String, Double> bankLossBySector` | coupons it was paid |
| 356 | `private double lastPostedBuy, lastPostedSell, lastFilled, lastSellQuantityWaited` | The last month's order-book figures, all books together, and the last issue - for the screens. |
| 357 | `private int lastSellsPosted, lastSellsWaited, lastTrades` |  |
| 360 | `private double lifeIssued, lifeCosts, lifeCouponsHouseholds, lifeCouponsBank, lifeCouponsCompanies, lifeCou...` | ---- over the city's life, saved ---- |
| 361 | `private double lifeLossHouseholds, lifeLossBank, lifeLossCompanies, lifeLossWorld` |  |
| 362 | `private double lifeVolume, lifePostedSell, lifeFilledSell, lifeWorldBought, lifeWorldSold` |  |
| 363 | `private int lifeIssues, lifeSellsPosted, lifeSellsWaited` |  |
| 365 | `private double betweenHouseholds, lifeBetweenHouseholds` | Cell to cell, this month and over the city's life: cash and trades (round 2). |
| 366 | `private int betweenHouseholdsTrades, lifeBetweenHouseholdsTrades` |  |
| 369 | `private double dueHouseholds, dueBank, dueWorld` | ---- saved record-date entitlements and actual charged/unpaid cash, paid at the market's step ---- |
| 371 | `private final Map<String, Double> dueCells` | ...the households' part, cell by cell, by the cell's key: what each held at the record date (round 2). |
| 372 | `private final Map<String, Double> dueCompanies` |  |
| 373 | `private final Map<String, Double> dueByIssuer` |  |
| 440 | `private double dust` |  |
| 511 | `private final Map<String, Double> issuedSoFar` | THE FACE SOLD AND REPAID SO FAR, BY ISSUER (0.7.75, the sector statements' R7): counted where a bond is issued and where it is repaid at maturity, in memory, from nothing at a founding or a load. |
| 512 | `private final Map<String, Double> repaidSoFar` |  |
| 567 | `private final Map<String, Double> bankLossTaken` | What takeBankLoss() handed Game this month, by sector, for reading after (the Bank tab's write-off by sector is the loans' and this). |
| 745 | `final String issuer` |  |
| 746 | `final double deposit, world, premium, yearOfOutput, faceOutstanding, householdSpare, bankRoom` |  |
| 747 | `final boolean running, bankBuys` |  |
| 748 | `final String[] companies` |  |
| 749 | `final double[] cash, wealth, othersFace` |  |
| 792 | `final Bidders who` |  |
| 793 | `final String issuer` |  |
| 794 | `final double size, offered, el, deposit, world, premium, yearOfOutput, faceOutstanding, householdSpare` |  |
| 795 | `final String[] companies` |  |
| 796 | `final double[] cash, wealth, othersFace` |  |
| 797 | `final double bankYield, bankRoom` |  |
| 798 | `final boolean running` |  |
| 1064 | `private String lastIssue` | The last issue, for the Bonds page: who, how much, at what, against the bank's rate, when. |
| 1065 | `private double lastIssueFace, lastIssueCoupon, lastIssueLoanRate` |  |
| 1066 | `private int lastIssueMonth` |  |
| 1571 | `private Household posting` | The cell posting its bids at the step right now, and what it may still spend this month. |
| 1572 | `private double postingBudget` |  |
| 1575 | `private transient Set<Household> deferredBondRecounts` | Only postHouseholds defers Settle's folds; every other settlement stays eager. |
| 1576 | `private transient boolean eagerBondRecounts` |  |
| 1577 | `private transient long settledBondWrites, settledBondRecounts` |  |
| 1737 | `private Household selling` | The cell selling in the waterfall right now, whose proceeds go back to its waterfall rather than into its savings. |
| 1738 | `private double raised` |  |
| 1774 | `private double companiesSoldShort` | What companies short of money raised selling their bonds this month, from the credit settle (round 4); a month's figure, cleared with the market's month. |
| 1793 | `final CorporateBond b` |  |
| 2124 | `List<CorporateBond> bonds` |  |
| 2125 | `List<OrderBook> books` |  |
| 2126 | `int nextId` |  |
| 2127 | `double[] month` |  |
| 2128 | `double[] life` |  |
| 2129 | `String lastIssue` |  |
| 2130 | `double[] lastIssueFigures` |  |
| 2131 | `Map<String, Double> issuedBySector, proceedsBySector, repaidBySector, boughtBySector, couponsBySector` |  |
| 2132 | `double[] couponDues` |  |
| 2133 | `boolean couponRecordOpen, couponsCharged, bankCouponsBooked` |  |
| 2134 | `double couponCash, couponIssuerDebit, couponBankPaid` |  |
| 2135 | `Map<String, Double> dueCells, recordDueCells, dueCompanies, dueByIssuer` |  |
| 2136 | `Map<Integer, Double> dueCityById, dueCustodyById` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 2288 | **type** `public class BondMarket implements BusinessDebtManager.BondBook, BusinessDebtManager.BondDesk` | The corporate bond market: every bond the city's businesses have issued, who holds each, the order book each trades on, and the rule each participant trades by. |

### THE PARTICIPANTS (lines 57-162)

| line | len | member | says |
|---:|---:|---|---|
| 137 | 1 | `static boolean isFund(String who)` | True for either of the fund's names. |

### THE CITY'S ISSUE LOTS (SZ1) (lines 163-216)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 10 | `public static double referenceLot(String kind)` | An instrument's reference-price lot; the keys are the city's quoting API's. |
| 189 | 3 | `public static double issueLot(String kind, double priceLevel, double minimum)` | The month's price level is relative to reference prices, in the same money as minimum; round down so a lot never exceeds either. |
| 194 | 5 | `public static double fundingLot(String kind, double priceLevel, double minimum)` | Exact price-level funding for the model; rounding in nominal money would break EP1's scaled twins. |
| 201 | 3 | `public static double issueLot(Game game, String kind, String money)` | The player's round issue lot, in local money or the foreign paper's own money at this month's cross rate. |
| 206 | 10 | `public static double fundingLot(Game game, String kind, String money)` | The model's exact priced-and-capped lot, in the paper's own money. |

### what it reads (lines 217-324)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 21 | **type** `public interface Readings` | The city's readings the market prices on, which Game supplies. |
| 221 | 1 | `int month()` _(in BondMarket.Readings)_ |  |
| 223 | 1 | `double curve(int months)` _(in BondMarket.Readings)_ | The city's curve at this many months (DebtManager.curveRate()). |
| 225 | 1 | `double policyRate()` _(in BondMarket.Readings)_ | The policy rate, for the bank's own prices. |
| 227 | 1 | `double depositRate()` _(in BondMarket.Readings)_ | What the bank pays savers. |
| 229 | 1 | `double worldRate()` _(in BondMarket.Readings)_ | What the world pays (DebtManager.worldRate(): the US's policy rate where the city has the world file, XR4). |
| 231 | 1 | `double countryPremium()` _(in BondMarket.Readings)_ | What the world charges this city for its risk (DebtManager.countryPremium()). |
| 233 | 1 | `double monthlyGdp()` _(in BondMarket.Readings)_ | A month's output. |
| 235 | 1 | `double localPerUsd()` _(in BondMarket.Readings)_ | Local money per dollar. |
| 237 | 1 | `boolean worldRunning()` _(in BondMarket.Readings)_ | True while hot money is running (CapitalFlows.isStopped()). |
| 239 | 1 | `double unit()` _(in BondMarket.Readings)_ | The currency's unit (Denomination.getUnit()), for the underwriter's fixed fee. |
| 245 | 1 | `Readings readings()` | What it was attached to read, for a harness (CurveCheck, CD1). |
| 253 | 8 | `public void attach(Readings readings, HouseholdBalance households, Bank bank, EconomyManager economy, OutwardInvestment outward)` | Wires the market into a city. |
| 273 | 1 | `public void attachCustody(EstateCustody owner)` |  |
| 274 | 5 | `public void checkCustodyTransfer(int id, double face)` |  |
| 279 | 6 | `public void transferToCustody(int id, double face)` |  |
| 285 | 8 | `public void transferRecordedCouponToCustody(String cell, double fraction)` |  |
| 294 | 7 | `public void recordCouponIssuerCharge(double paid)` | Books the actual issuer charge after finance has paid it. |
| 302 | 6 | `public void recordCouponBankPayment(double paid)` | The native bank credit leaves the same clearing cash before the other holders are paid. |
| 308 | 1 | `public double couponClearingCash()` |  |
| 309 | 1 | `public double recordedIssuerCouponDebit()` |  |
| 310 | 1 | `public double recordedBankCouponPaid()` |  |
| 311 | 5 | `public double couponUnpaidEntitlement()` |  |
| 320 | 1 | `private int ledgerMonth()` | The month the fund's ledger dates what it writes by: the city's, read live. |
| 323 | 1 | `public void attachFund(TreasuryFund fund)` | Wires the city's fund in (0.7.14). |

### state (lines 325-374)

### the bonds (lines 375-416)

| line | len | member | says |
|---:|---:|---|---|
| 378 | 1 | `public List<CorporateBond> getBonds()` | Every bond outstanding, oldest first. |
| 381 | 5 | `public List<CorporateBond> getBonds(String issuer)` | One sector's bonds. |
| 388 | 5 | `public double maturingFace(String issuer, int month)` | The face of one issuer's bonds that fall due at `month`'s settle (redeemMaturing()): asked ahead by what a buyer can pay for (0.7.12 round 6). |
| 395 | 4 | `public CorporateBond bond(int id)` | The bond with this id, or null. |
| 401 | 4 | `public OrderBook bookOf(CorporateBond b)` | The order book a bond trades on - a reader: an empty one, not kept, for a bond with none. |
| 407 | 9 | `private OrderBook openBook(CorporateBond b)` | ...and the one the market posts to, opened if it has none - at the market's dust (see dust). |

### THE BOND BOOK'S DUST, AND WHY IT IS SEEDED (0.7.12 round 6) (lines 417-470)

| line | len | member | says |
|---:|---:|---|---|
| 443 | 4 | `public void seedConstants(double unit)` | Re-seeds the dust at a given unit, and every open book's with it. |
| 449 | 5 | `public double totalFace()` | Every bond's face outstanding. |
| 456 | 1 | `public double faceHeldByHouseholds()` | ...held by each class. |
| 457 | 1 | `public double faceHeldByBank()` |  |
| 458 | 1 | `public double faceHeldByWorld()` |  |
| 459 | 1 | `public double faceHeldByCompanies()` |  |
| 461 | 1 | `public double faceHeldBy(String sector)` | ...by one company. |
| 463 | 1 | `public double bankCost()` | What the bank's bonds cost it: its carrying value. |
| 465 | 5 | `public double bankCost(String issuer)` | ...of one issuer's bonds. |

### the lender's view (BondBook) (lines 471-571)

| line | len | member | says |
|---:|---:|---|---|
| 473 | 5 | `public double principal(String sector)` |  |
| 479 | 6 | `public double monthlyCoupon(String sector)` |  |
| 487 | 5 | `public double faceDueWithin(String sector, int month, int months)` | The face of one issuer's bonds maturing within `months` settles of `month`'s (0.7.74, the sector statements' R2): a read, as maturingFace() is. |
| 494 | 5 | `public int lastMaturity(String sector)` | The month one issuer's last bond falls due (0.7.75, R2's "runs to"); 0 with none. |
| 501 | 1 | `public double couponRate(String sector)` | ...and the coupon its bonds pay, weighted by face (R2's rate): averageCoupon(). |
| 514 | 1 | `public double faceIssuedSoFar(String sector)` |  |
| 515 | 1 | `public double faceRepaidSoFar(String sector)` |  |
| 526 | 32 | `public double writeDown(String sector, double scale)` | A DEFAULT, ON THE BONDHOLDERS (0.7.12): every bond of the sector written down to this share of its face, every holder by the same share - the households' face and every cell's own face of it (round 2), the bank's face... |
| 560 | 5 | `public double takeBankLoss(String sector)` | What the month's defaults took off the bank's bonds of this sector, at what it paid - read once by Game, which books it against the bank's allowance with the sector's loans. |
| 570 | 1 | `public double getBankLossThisMonth(String sector)` | ...one sector's: the bank's loss on its bonds this month, at what it paid (0.7.12, round 2). |

### the valuation (lines 572-664)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 1 | `private BusinessDebtManager credit()` | The lender, for the issuer's readings. |
| 583 | 8 | `public double defaultRate(String issuer, double extraDebt, double extraAssets)` | THE ISSUER'S DEFAULT RATE, a year, for a bondholder: 0.7.8's curve at the leverage its last quarter reads, with a deal on top - `extraDebt` owed and `extraAssets` owned - never under DEFAULT_RATE_FLOOR. |
| 600 | 3 | `public double expectedLoss(String issuer)` | WHAT A BONDHOLDER EXPECTS TO LOSE a year on this issuer's bonds: its default rate times a bond's loss given default, BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT - whatever else the issuer owes, since 0.7.12 round 2 (R... |
| 605 | 4 | `public double modelYield(CorporateBond b, int month)` | WHAT A BOND IS WORTH TO THE MARKET: the city's curve for the months it has left plus what a holder expects to lose on it - the yield the participants bid and ask around. |
| 611 | 3 | `public double modelPrice(CorporateBond b, int month)` | ...as a price a unit of face. |
| 616 | 4 | `public double lastPrice(CorporateBond b)` | The last price it traded at on its book, or NaN before its first trade. |
| 622 | 5 | `public double lastYield(CorporateBond b, int month)` | The yield at its last traded price, or at its value before its first trade. |
| 645 | 13 | `public double bankYield(String issuer, int months, double extraBonds, double dealDebt, double extraAssets)` | WHAT THE BANK WOULD EARN LENDING THE ISSUER THE SAME MONEY: an equal loan's four parts at the bond's term (Bank.loanRate() at RISK_BUSINESS), the book's concentration on the issuer (Bank.concentrationCharge()), the is... |
| 660 | 4 | `private boolean bankBuys()` | True while the bank stands, has a branch, and is over its capital target: the only bank that buys. |

### WHAT AN ISSUE COSTS (lines 665-717)

| line | len | member | says |
|---:|---:|---|---|
| 683 | 5 | `public double issueCost(double face)` | What an issue of this face costs to bring: Game.FIXED_ISSUE_COST, in today's money, and Game.UNDERWRITING_SPREAD of the face. |
| 690 | 3 | `public double allIn(double face, double coupon)` | A bond's all-in cost a year: its coupon and its issuing costs spread over its life. |
| 695 | 3 | `public static double loanAllIn(double loanRate)` | A bank loan's all-in cost a year: its rate and its fee (Bank.LOAN_FEE) spread over its term, BusinessDebtManager.LOAN_TERM_MONTHS - the loan's own issuing cost, counted as the bond's are. |
| 711 | 6 | `public double crossover(double loanRate, double coupon)` | THE SIZE AT WHICH A BOND BEGINS TO BEAT THE BANK: the face at which the coupon and the costs spread over the life equal the loan's all-in cost - |

### THE BOOK IS BUILT (lines 718-896)

| line | len | member | says |
|---:|---:|---|---|
| 744 | 38 | **type** `final class Bidders` | What every bidder brings to a new issue by this issuer, read once: the rates, the households' spare savings, each company's till and wealth, the bank's room. |
| 751 | 30 | `Bidders(String issuer)` _(in BondMarket.Bidders)_ |  |
| 784 | 5 | `private double bankClaimRoom()` | The same capital allowance as project loans, including loans awaiting settlement. |
| 791 | 88 | **type** `final class Demand` | Every participant's bid for one new issue of this size, offered at this size, as a function of its coupon. |
| 800 | 3 | `Demand(String issuer, double size, double offered, double dealDebt, double extraAssets)` _(in BondMarket.Demand)_ |  |
| 812 | 24 | `Demand(Bidders who, double size, double offered, double dealDebt, double extraAssets)` _(in BondMarket.Demand)_ | bid is an amount of (THE BOOK IS BUILT) |
| 838 | 5 | `double households(double y)` _(in BondMarket.Demand)_ | The households: the city's paper's share of the issue offered at this expected return, out of savings past the cushion. |
| 845 | 6 | `double world(double y)` _(in BondMarket.Demand)_ | The world: a month of closing the gap to hot money's target, the offered issue's share of it. |
| 853 | 7 | `double company(int i, double y)` _(in BondMarket.Demand)_ | One company: a month of closing the gap to OutwardInvestment's share of its wealth, the offered issue's part of it. |
| 862 | 1 | `double bank(double y)` _(in BondMarket.Demand)_ | The bank: all its room at its loan-equivalent yield or over, nothing under. |
| 864 | 5 | `double total(double y)` _(in BondMarket.Demand)_ |  |
| 871 | 7 | `double ceiling()` _(in BondMarket.Demand)_ | The yield past which nobody's bid grows: the book's ceiling. |
| 881 | 10 | `double clearingYield(Demand dm, double face)` | The lowest coupon at which the bids fill `face`, or NaN when nothing up to the book's ceiling does. |
| 893 | 3 | `public double quoteCoupon(String issuer, double face, double extraAssets)` | The coupon a new issue of this face would clear at today, or NaN - for the screens and the harness. |

### WHO ISSUES, AND WHEN (lines 897-1073)

| line | len | member | says |
|---:|---:|---|---|
| 929 | 59 | `public BusinessDebtManager.Plan plan(String sector, double amount, double loanRate, double loanRoom, double bondRoom, double ex...` |  |
| 998 | 64 | `public double issue(BusinessDebtManager.Plan plan, int month)` | SELLS A PLAN'S BOND, at its coupon, at par: every participant takes its bid at that coupon, pro rata if the bids more than fill it, and pays; the bank, as underwriter, is paid the costs out of the proceeds. |
| 1068 | 1 | `public String getLastIssuer()` |  |
| 1069 | 1 | `public double getLastIssueFace()` |  |
| 1070 | 1 | `public double getLastIssueCoupon()` |  |
| 1071 | 1 | `public double getLastIssueLoanRate()` |  |
| 1072 | 1 | `public int getLastIssueMonth()` |  |

### THE COUPONS AND THE PRINCIPAL (lines 1074-1245)

| line | len | member | says |
|---:|---:|---|---|
| 1096 | 45 | `public void strikeCoupons()` | Strikes this month's coupons by who holds each bond now: the record date. |
| 1143 | 6 | `public double getCouponsStruck()` | Coupon bill not yet charged to issuers. |
| 1151 | 1 | `public double getCouponsDueToBank()` | The bank's part of this month's coupons, paid it at the settle with its loans' interest. |
| 1161 | 38 | `public void redeemMaturing(int month)` | PAYS EVERY BOND THAT FALLS DUE THIS MONTH: its face out of the issuer's till - which may go short, for the credit settle to cover (see THE COUPONS AND THE PRINCIPAL) - to each holder: every cell its own face, into its... |
| 1201 | 44 | `private void payCoupons()` | Pays the saved record-date holders after the actual issuer charge and bank booking. |

### THE ORDERS ARE GOOD FOR A MONTH (lines 1246-1594)

| line | len | member | says |
|---:|---:|---|---|
| 1266 | 27 | `public void startMonth()` | Opens the month's flows. |
| 1299 | 3 | `public void takeMonth(int month)` | THE MARKET'S MONTH, after the shares have traded and before the sectors move their money abroad: the coupons paid, last month's orders withdrawn, every bond valued, and every participant's orders posted. |
| 1304 | 47 | `public void takeMonth(int month, double fundSharesValue)` | ...with what the city's fund's shares are worth this month (0.7.14), for its mix; its orders are posted last (postFund()). |
| 1353 | 14 | `private void noteBook(OrderBook book)` | Adds a book's month, as it closed, to the market's record. |
| 1369 | 29 | `private void postBank(int month, Map<String, Double> el, double[] value)` | The bank: over its target, a bid at its loan-equivalent price on its spare capital; under it, an ask at the bond's value for what takes it back. |
| 1400 | 26 | `private void postWorld(int month, Map<String, Double> el)` | The world: hot money's target in each bond, its share of the stock the spread calls for; a bid for a month of the gap, an ask for the excess. |
| 1428 | 43 | `private void postCompanies(int month, Map<String, Double> el)` | The companies: OutwardInvestment's share of each owing-nothing company's wealth, spread over the other sectors' bonds by face; a bid for OUT_SPEED of the gap, an ask for HOME_SPEED of the excess. |
| 1486 | 83 | `private void postHouseholds(int month, Map<String, Double> el)` | THE HOUSEHOLDS, CELL BY CELL (0.7.12 round 2): round 1's rule for the pool, applied to each cell's own savings and holdings. |
| 1580 | 1 | `void eagerBondRecountsForCheck(boolean eager)` | BondCheck's per-market twin and work counts; neither changes settlement arithmetic. |
| 1581 | 1 | `long bondWritesForCheck()` |  |
| 1582 | 1 | `long bondRecountsForCheck()` |  |
| 1584 | 10 | `private void settleBondFace(Household c, int id, double face)` |  |

### the city's fund (0.7.14) (lines 1595-1687)

| line | len | member | says |
|---:|---:|---|---|
| 1613 | 53 | `private void postFund(int month, double[] value)` | THE CITY'S FUND (TreasuryFund), BY ITS RULE AND THEN BY THE PLAYER'S HAND, last: the rest of the fund's market book and cash in bonds - the share Exchange's rule leaves, 1 - TreasuryFund.EQUITY_WEIGHT - spread over th... |
| 1668 | 4 | `private int indexOf(int id)` | Where a bond is in the list, by its id, or -1. |
| 1674 | 1 | `public double faceHeldByCity()` | The city's fund's face in every bond, together (0.7.14). |
| 1677 | 10 | `public double fundBondRoom(int month)` | The money that would buy every live bond's face the city's fund does not hold, at the bonds' value this month (RB1): the room its rule's bond bids could still take, for TreasuryFund.idleCash(). |

### A HOUSEHOLD SHORT OF MONEY SELLS (lines 1688-1776)

| line | len | member | says |
|---:|---:|---|---|
| 1702 | 33 | `double sellForCell(Household cell, double needPer, double borrowingRate)` |  |
| 1754 | 18 | `public double sellForCompany(String sector, double need, double borrowingRate)` | A COMPANY SHORT OF MONEY SELLS (0.7.12 round 4, CAN'T PAY MEANS DEFAULT - see BusinessDebtManager), by the households' rule above: in each other sector's bond it holds, it asks the price at which the buyer's yield wou... |
| 1775 | 1 | `public double getCompaniesSoldShort()` |  |

### one order, settled (lines 1777-1916)

| line | len | member | says |
|---:|---:|---|---|
| 1780 | 3 | `List<OrderBook.Fill> tradeForCheck(CorporateBond b, String who, OrderBook.Side side, double price, double quantity)` | One order on a bond's book, settled, between two steps: for FundLedgerCheck (0.7.39), as Exchange.tradeForCheck(). |
| 1784 | 6 | `private List<OrderBook.Fill> submit(CorporateBond b, String who, OrderBook.Side side, double price, double quantity, int month)` |  |
| 1792 | 119 | **type** `private final class Settle implements OrderBook.Clearing` | Where each participant's money and holding is, for one bond's book. |
| 1794 | 1 | `Settle(CorporateBond b)` _(in BondMarket.Settle)_ |  |
| 1796 | 26 | `public double capacity(String who, OrderBook.Side side, double price)` _(in BondMarket.Settle)_ |  |
| 1824 | 3 | `public boolean mayTrade(String buyer, String seller)` _(in BondMarket.Settle)_ | The fund's rule and its hand never trade with each other (0.7.14). |
| 1828 | 82 | `public void settle(String buyer, String seller, double q, double price)` _(in BondMarket.Settle)_ |  |
| 1913 | 3 | `private Household cellOf(String who)` | The cell a participant's name is, or null. |

### the people who leave (lines 1917-1961)

| line | len | member | says |
|---:|---:|---|---|
| 1926 | 8 | `public void householdLeft(int id, double face)` | WHAT A HOUSEHOLD THAT LEAVES TAKES WITH IT: its bonds, which it holds from abroad from then on - as a leaver's shares are held abroad (Equity.followEmigrants()) - taken off the households' face of the bond and put on ... |
| 1945 | 9 | `public void recountHouseholds()` | Every bond's households' face, checked against the cells that hold it after a load, which restores the two separately (round 2). |
| 1956 | 5 | `public Map<Integer, Double> householdsFaceById()` | The households' face in each bond, by its id: what an old pooled save hands the cells (HouseholdBalance.claimPooledBonds()). |

### reading (lines 1962-2051)

| line | len | member | says |
|---:|---:|---|---|
| 1965 | 1 | `public double getHouseholdsBought()` | The households' cash into the pools for bonds this month: at issue and from the bank and the companies. |
| 1967 | 1 | `public double getHouseholdsSold()` | ...and out of the pools to them, for bonds they sold to the bank and the companies. |
| 1969 | 1 | `public double getHouseholdsBoughtAbroad()` | What the households paid the world for bonds, and the world paid them: both outside the pools, declared as pairs. |
| 1970 | 1 | `public double getHouseholdsSoldAbroad()` |  |
| 1972 | 1 | `public double getWorldBought()` | The world's money into the pools for bonds - at issue and from the bank and the companies - and out of them for bonds it sold them. |
| 1973 | 1 | `public double getWorldSold()` |  |
| 1975 | 1 | `public double getWorldPurchases()` | Every bond purchase the world made this month, whoever sold, and every sale. |
| 1976 | 1 | `public double getWorldSales()` |  |
| 1977 | 1 | `public double getCouponsToHouseholds()` |  |
| 1978 | 1 | `public double getCouponsToBank()` |  |
| 1979 | 1 | `public double getCouponsToCompanies()` |  |
| 1980 | 1 | `public double getCouponsAbroad()` |  |
| 1981 | 1 | `public double getPrincipalToHouseholds()` |  |
| 1982 | 1 | `public double getPrincipalToBank()` |  |
| 1983 | 1 | `public double getPrincipalToCompanies()` |  |
| 1984 | 1 | `public double getPrincipalAbroad()` |  |
| 1986 | 1 | `public double getOrphanedToHouseholds()` | The principal and coupons the issuers paid this month on the households' face that no cell was credited (HL1): the bond's record less the cells' own. |
| 1988 | 1 | `public double getLossHouseholds()` | Face written off this month's defaults, by holder. |
| 1989 | 1 | `public double getLossBank()` |  |
| 1990 | 1 | `public double getLossCompanies()` |  |
| 1991 | 1 | `public double getWorldWrittenOff()` |  |
| 1992 | 1 | `public double getIssuedFace()` |  |
| 1993 | 1 | `public double getIssuedCosts()` |  |
| 1994 | 1 | `public int getIssues()` |  |
| 1995 | 1 | `public double getEmigrantsFace()` |  |
| 1998 | 1 | `public double getIssued(String sector)` | One sector's month: the face it issued, what that handed it, the principal it repaid, what it spent on other sectors' bonds (net of sales and principal back), and the coupons it was paid. |
| 1999 | 1 | `public double getProceeds(String sector)` |  |
| 2000 | 1 | `public double getRepaid(String sector)` |  |
| 2001 | 1 | `public double getBoughtNet(String sector)` |  |
| 2002 | 1 | `public double getCouponsTo(String sector)` |  |
| 2005 | 1 | `public double getLastPostedBuy()` | Last month's order book, every bond together: posted to buy and to sell, filled, sell orders posted and those that waited unfilled, and trades. |
| 2006 | 1 | `public double getLastPostedSell()` |  |
| 2007 | 1 | `public double getLastFilled()` |  |
| 2008 | 1 | `public int getLastSellsPosted()` |  |
| 2009 | 1 | `public int getLastSellsWaited()` |  |
| 2010 | 1 | `public double getLastSellQuantityWaited()` |  |
| 2011 | 1 | `public int getLastTrades()` |  |
| 2014 | 1 | `public double getLifeIssued()` | Over the city's life. |
| 2015 | 1 | `public double getLifeCosts()` |  |
| 2016 | 1 | `public int getLifeIssues()` |  |
| 2017 | 1 | `public double getLifeCouponsHouseholds()` |  |
| 2018 | 1 | `public double getLifeCouponsBank()` |  |
| 2019 | 1 | `public double getLifeCouponsCompanies()` |  |
| 2020 | 1 | `public double getLifeCouponsAbroad()` |  |
| 2021 | 1 | `public double getLifeLossHouseholds()` |  |
| 2022 | 1 | `public double getLifeLossBank()` |  |
| 2023 | 1 | `public double getLifeLossCompanies()` |  |
| 2024 | 1 | `public double getLifeLossWorld()` |  |
| 2025 | 1 | `public double getLifeVolume()` |  |
| 2026 | 1 | `public double getLifePostedSell()` |  |
| 2027 | 1 | `public double getLifeFilledSell()` |  |
| 2028 | 1 | `public int getLifeSellsPosted()` |  |
| 2029 | 1 | `public int getLifeSellsWaited()` |  |
| 2030 | 1 | `public double getLifeWorldBought()` |  |
| 2031 | 1 | `public double getLifeWorldSold()` |  |
| 2033 | 1 | `public double getBetweenHouseholds()` | One cell buying from another (round 2): this month's cash and trades, and the city's life's - a transfer inside the households. |
| 2034 | 1 | `public int getBetweenHouseholdsTrades()` |  |
| 2035 | 1 | `public double getLifeBetweenHouseholds()` |  |
| 2036 | 1 | `public int getLifeBetweenHouseholdsTrades()` |  |
| 2039 | 5 | `public double averageCoupon(String sector)` | One sector's bonds' coupon, weighted by face: what its bond debt costs a year. |
| 2046 | 5 | `public double averageCoupon()` | ...and every bond's. |

### THE FINANCES TAB'S SUMS (0.7.32) (lines 2052-2119)

| line | len | member | says |
|---:|---:|---|---|
| 2058 | 3 | `public double getCouponsPaid()` | The month's coupons, every holder's: the households', the bank's, the companies' and the world's. |
| 2063 | 3 | `public double getPrincipalRepaid()` | ...the principal repaid at maturity, to every holder. |
| 2068 | 3 | `public double getWrittenOffThisMonth()` | ...and the face written off in defaults, every holder's. |
| 2078 | 3 | **type** `public record Issuer(String issuer, int bonds, double face, double lowCoupon, double highCoupon, int neares...` | One issuer's bonds outstanding together: how many and their face, the lowest and highest coupon, the nearest maturity, its largest bond's number (the one its book opens on), and the face each holder class holds - the ... |
| 2083 | 26 | `public List<Issuer> byIssuer()` | Every issuer with a bond outstanding, biggest face first. |
| 2111 | 8 | `public double valueHeld(java.util.function.ToDoubleFunction<CorporateBond> holding, int month)` | What a holder class's bonds are worth at the bonds' value this month. |

### save and load (lines 2120-2342)

| line | len | member | says |
|---:|---:|---|---|
| 2123 | 45 | **type** `public static final class State implements Cloneable` | Everything the market carries from one month to the next, as the save writes it. |
| 2145 | 22 | `State copy()` _(in BondMarket.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() lists the market's own bonds and books, which the months go on changing while the autosave is written. |
| 2169 | 36 | `public State toState()` |  |
| 2207 | 62 | `public void restore(State s)` | Puts a saved market back. |
| 2271 | 1 | `private static double finite(double v)` | A save carries no NaN: nothing where there was none. |
| 2273 | 5 | `private static void putAll(Map<String, Double> into, Map<String, Double> from)` |  |
| 2280 | 27 | `public void reset()` | No bonds, no books, nothing over the city's life: a new city. |
| 2309 | 33 | `public void redenominate(double scale)` | Every money figure in the new unit: the bonds' faces and holdings, the orders' quantities, the month's flows. |

