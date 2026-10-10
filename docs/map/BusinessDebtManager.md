# BusinessDebtManager.java - 3,575 lines · 232 methods · 28 constants · model

`ham/citybuildersim/BusinessDebtManager.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Private-sector credit. The counterpart to DebtManager, which handles the
> city's own borrowing.
> 
> One manager holds every business loan in the city, tagged by sector, rather
> than one manager per sector. That way there is a single list to save, a single
> place to see total private credit, and adding a fourth sector is a constant in
> this file instead of another object to wire up. Each sector still gets its own
> rate - the rate is per sector, the bookkeeping is shared.
> 
> PRICING (0.7.7; the borrower's part from the curve since 0.7.8)
> 
>     rate = the bank's prime + this borrower's own expected loss over the
>            book's + its record
> 
> PRIME is the bank's: what money, running the bank, the expected loss and the
> capital a loan ties up cost it, for a sound business (Bank.prime(), WHAT A
> LOAN COSTS). Until 0.7.7 the base was the city's own rate - with the bank's
> strain premium in it - floored on the bank's cost of funds plus a point.
> The spread is THIS borrower's: since 0.7.8 its own expected loss off the
> curve its firms default on, over the through-the-cycle loss prime already
> carries, at the leverage the loan leaves it at - see PRICING FROM THE
> CURVE. A loan keeps the rate it was written at for its LOAN_TERM_MONTHS.
> 
> ORIGINATION
> 
> Loans are underwritten automatically when a sector cannot cover its month
> (the shortfall desk, coverShortfall()), and for a building a sector's
> investor plans and cannot pay for out of its own cash (the investment desk,
> canFundProject() and issueProjectLoan(), the building counted as the
> collateral). Before this existed, a sector's cash simply went negative with
> no lender, no interest and no liability on its balance sheet - the food
> industry was $48,011.82 overdrawn at month 170 and paying nothing for the
> privilege.
> 
> AND THE LANDLORDS' BUILDINGS ON INSURED MORTGAGES (0.7.11): a residential
> order is funded by a Mortgage, not a loan - issueMortgage(), tested by
> canFundMortgage(), priced at the insured rate Game pushes in with prime,
> paid down every month and renewed at each term's end in processMonth().
> See THE LANDLORDS' MORTGAGES below.
> 
> AND BONDS, AND INTERIM FINANCING (0.7.12): where either desk borrows it
> may sell a bond in part of the loan's place when one is cheaper, never
> past what the bank would lend (AND ITS BONDS; the bonds themselves are
> BondMarket's); a sector that cannot pay its month defaults in it (CAN'T
> PAY MEANS DEFAULT), and what is still unpaid is lent as an InterimLoan
> ranked ahead of its other debt (INTERIM FINANCING).

**Uses:** [Mortgage](Mortgage.md) (35), [BusinessDebt](BusinessDebt.md) (27), [InterimLoan](InterimLoan.md) (12), [Bank](Bank.md) (8), [BusinessLoan](BusinessLoan.md) (5), [Sectors](Sectors.md) (1)

**Used by (26):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [CreditCheck](CreditCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [SectorStatements](SectorStatements.md), [SpreadPlanner](SpreadPlanner.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 85 | PRICING FROM THE CURVE (0.7.8) |
| 153 | INSOLVENCY |
| 242 | A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 332 | RECOVERIES BY INSTRUMENT (0.7.12, round 2) |
| 541 | AND ITS BONDS (0.7.12) |
| 1092 | WHAT IT OWES, BY KIND AND BY WHEN (0.7.74, the sector statements' |
| 1180 | WHAT MOVED IT, BY KIND (0.7.75, the sector statements' R7) |
| 1425 | · ...AND NOBODY LENDS PAST THE CEILING |
| 1615 | ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) |
| 1809 | ...AND WHAT THE NEXT SETTLE WOULD DO, ASKED AHEAD (0.7.12, round 6) |
| 1930 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 2318 | ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) |
| 2467 | THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) |
| 2670 | · insolvency |
| 2943 | CAN'T PAY MEANS DEFAULT (0.7.12, round 4) |
| 3041 | INTERIM FINANCING (0.7.12, round 5) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3001 | `BusinessDebtManager.ShortReason.PAST_DEFAULT_POINT` |  |
| 3001 | `BusinessDebtManager.ShortReason.BANNED` |  |
| 3001 | `BusinessDebtManager.ShortReason.BANK_SHUT` |  |
| 3001 | `BusinessDebtManager.ShortReason.CEILING` |  |
| 3001 | `BusinessDebtManager.ShortReason.AFTER_SETTLE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 184 | `BusinessDebtManager.INSOLVENCY_TRIGGER` | `1.5` | The default point: a firm owing more than this multiple of its assets is not getting repaid, and both sides know it. |
| 217 | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | `0.9` | The most a lender will advance against a borrower's assets. |
| 240 | `BusinessDebtManager.RESTRUCTURE_TARGET` | `.6` | What the backstop leaves a restructured sector owing, as a multiple of its assets. |
| 327 | `BusinessDebtManager.ASSET_VOLATILITY` | `.25` | The one-year volatility of a firm's assets: sigma in PD(L), the spread of fortunes among the firms inside a sector. |
| 330 | `BusinessDebtManager.DEFAULT_HORIZON_MONTHS` | `12` | The horizon a default probability is quoted over, in months: one year, the convention of KMV's EDF and of every rating agency's default rate. |
| 377 | `BusinessDebtManager.LOAN_RECOVERY` | `.75` | What a bank loan gives back of each dollar that defaults: 75%, the midpoint of the 70-80% that first-lien senior secured bank loans recovered on average in Moody's Ultimate Recovery Database and S&P's recovery studies... |
| 380 | `BusinessDebtManager.BOND_RECOVERY` | `.45` | What a bond gives back of each dollar that defaults: 45%, the midpoint of the 40-50% that senior unsecured bonds recovered on average in the same Moody's and S&P data, 1987-2024. |
| 383 | `BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | `1 - LOAN_RECOVERY` | What the bank loses on a dollar of a sector's loans that defaults: 1 - LOAN_RECOVERY, derived. |
| 386 | `BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT` | `1 - BOND_RECOVERY` | ...and what a bondholder loses on a dollar of its bonds: 1 - BOND_RECOVERY, derived. |
| 477 | `BusinessDebtManager.BORROWING_BLOCKED_MONTHS` | `12` | Months a sector cannot borrow after being restructured. |
| 522 | `BusinessDebtManager.DEFAULT_SURCHARGE` | `.01` | Extra annual interest per prior write-down, on top of the borrower's expected loss, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 524 | `BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT` | `3` | The most write-downs DEFAULT_SURCHARGE is charged for: a record adds three points at the most. |
| 527 | `BusinessDebtManager.LOAN_TERM_MONTHS` | `36` | How long a business loan runs, interest only, before its principal is due: three years, and it keeps the rate it was written at for all of them. |
| 537 | `BusinessDebtManager.BUFFER_MONTHS` | `3` | Borrow enough to cover the hole plus this many months of the current loss. |
| 1105 | `BusinessDebtManager.DEBT_KINDS` | `{ "Bank loans", "Bonds", "Mortgages", "Interim financing" }` | The four kinds of debt, in the order the statements print them. |
| 1123 | `BusinessDebtManager.DUE_HORIZONS` | `{ 12, 60 }` | The two horizons R2 reads, in settles: a year and five. |
| 1126 | `BusinessDebtManager.OWED` | `0, WITHIN_YEAR = 1, WITHIN_FIVE = 2, RATE = 3, RUNS_TO = 4` | debtByKind()'s rows: what is owed, what falls due within a year and within five, the rate it pays a year weighted by what is owed, and the month the last of it falls due (0 with none) - the last two since 0.7.75. |
| 1197 | `BusinessDebtManager.BORROWED` | `0, REPAID = 1, WRITTEN_OFF = 2` | debtMovedByKind()'s rows: borrowed (the principal written, a bond's face), repaid, written off. |
| 2017 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 2019 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 2021 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 2023 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 2025 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 2027 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 2520 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 3108 | `BusinessDebtManager.INTERIM_PAST_LINE` | `"past the default point after the write-down"` | Why the interim lender would not lend: the sector still past the default point after the write-down. |
| 3110 | `BusinessDebtManager.INTERIM_BANK_SHUT` | `"the bank is shut"` | ...or the bank that would lend it has failed or is frozen in resolution. |
| 3531 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `private String[] SECTORS` | Every set of books that can borrow, by name, in the registry's order. |
| 539 | `private List<BusinessDebt> loans` |  |
| 635 | `private BondBook bondBook` |  |
| 636 | `private BondDesk bondDesk` |  |
| 647 | `private final Map<String, Double> bondProceeds` | The proceeds of the bonds the shortfall desk issued this month, per sector, until the credit settle hands them over (takeBondProceeds()). |
| 656 | `private final Map<String, Plan> shortfallPlans` | The plan the shortfall desk struck for each sector this month, for the advisor and the playtest. |
| 660 | `private double primeRate` | Prime: the bank's rate for a sound business this month, which every sector's own spread sits on (see PRICING). |
| 663 | `private double insuredMortgageRate` | The insured mortgage's rate this month (Bank.insuredMortgageRate()), pushed in by Game beside prime: what a new mortgage is written at and a term renews at. |
| 666 | `private final Map<String, Double> writtenOffThisMonth` | Written off this month, and over the whole game, per sector. |
| 667 | `private final Map<String, Double> writtenOffTotal` |  |
| 668 | `private final Map<String, Integer> restructures` |  |
| 671 | `private final Map<String, Integer> blockedMonths` | Months of borrowing ban remaining, per sector. |
| 688 | `private boolean lendingOpen` | Whether the lender is open for business at all. |
| 690 | `private final Map<String, Double> assets` |  |
| 691 | `private final Map<String, Double> rates` |  |
| 700 | `private final Map<String, Double> cashBalance` | Each sector's cash balance, refreshed with the assets. |
| 703 | `private final Map<String, Double> overdraftForgiven` | What this month's restructures forgave, per sector, until Game collects it. |
| 706 | `private final Map<String, Double> maturedPrincipal` | Principal that fell due this month, per sector, waiting to be settled. |
| 713 | `private double lentThisMonth` | The lender's side of the month, for MoneyAudit: what it advanced and what it took back. |
| 714 | `private double repaidThisMonth` |  |
| 724 | `private double feesThisMonth` | THE LOAN FEE (0.7.7): Bank.LOAN_FEE of every loan written, paid out of its proceeds - the borrower is handed the principal less this, and the bank's cash takes it at the month's settle beside the lending (Game.nextMon... |
| 725 | `private final Map<String, Double> feesBySector` |  |
| 745 | `private final Map<String, Double> lentBySector` | THE SAME TWO FIGURES, PER SECTOR. |
| 746 | `private final Map<String, Double> repaidBySector` |  |
| 821 | `private final Map<String, Double> concentrationCharges` | THE CONCENTRATION CHARGE ON EACH SECTOR'S LOANS (0.7.12): what the capital a new dollar lent to it adds for concentration costs a year, pushed in by Game from the bank beside prime (Bank .concentrationCharge(), THE BA... |
| 965 | `private final Map<String, QuoteParts> quoteParts` | Each sector's quote in its parts, as it was last priced (priceSector()). |
| 1014 | `private final java.util.Set<String> tillReported` | The sectors whose sheet the economy has reported (setCash(), from EconomyManager.refreshCreditAssets(), every month before anything is lent): the default point reads only those (pastDefaultPoint()). |
| 1199 | `private final Map<String, double[]> moved` |  |
| 1586 | `private final Map<String, Double> lineLentRationed` | WHAT THE OPEN LINE LENT WHILE THE BANK WAS SHORT OF CAPITAL (round 5), per sector, the month's: all of it - the loan and any bond in its place - while the capital rule was on, and the part of it past what the 0.7.8 ru... |
| 1587 | `private final Map<String, Double> lineLentPastOldRule` |  |
| 1610 | `private final Map<String, Double> shortfallLent` | WHAT THE SHORTFALL DESK LENT EACH SECTOR THIS MONTH, loans and bonds at face (0.7.12 round 2): the new borrowing that rolled what fell due, which a dividend is paid after (Game.payDividends(): net income less the prin... |
| 1732 | `private final Map<String, Double> refusedAtDefaultPoint` | What the default point refused this month, per sector: what the shortfall desk would otherwise have lent, the mortgages that fell due whole rather than renew, and the projects the investment desk turned down. |
| 1733 | `private final Map<String, Double> notRenewedAtDefaultPoint` |  |
| 1734 | `private final java.util.Set<String> refusedProjectAtDefaultPoint` |  |
| 1978 | `private double premiumsThisMonth` | The premiums added to the mortgages written this month, and by sector: the treasury's revenue line. |
| 1979 | `private final Map<String, Double> premiumsBySector` |  |
| 1987 | `private final Map<String, Double> mortgageRepaidBySector` | THE PRINCIPAL THE MORTGAGES' PAYMENTS TOOK THIS MONTH, by sector - part of getRepaidThisMonth(), and the figure the Bank tab and the landlords' screen show beside the payment. |
| 1990 | `private int mortgagesWrittenThisMonth, renewedThisMonth, fallenDueThisMonth` | The month's mortgages written, renewed, and fallen due because the lender could not renew them - and the same over the run, for the playtest (not saved, a count for the run). |
| 1991 | `private int renewedLifetime, fallenDueLifetime` |  |
| 2007 | `private final Map<String, Double> insuredWrittenOffThisMonth` | WHAT THE INSURANCE PAID THIS MONTH, by sector: what the month's write-downs took off insured mortgages. |
| 2008 | `private final Map<String, Double> insuredWrittenOffTotal` |  |
| 2011 | `private double premiumsTotal` | The premiums written over the city's life. |
| 2014 | `private String mortgageRefusal` | The reason canFundMortgage() last refused, or null. |
| 2390 | `private double capitalGrowth` |  |
| 2391 | `private boolean keepGoingOnly` |  |
| 2392 | `private final Map<String, Double> principalAtRule` |  |
| 2394 | `private final java.util.Set<String> refusedForCapital` | Sectors whose project the capital rule refused this month, so the investor can say so. |
| 2396 | `private final java.util.Set<String> refusedProjectForCapital` | ...by door (round 5): a building's loan, and a new mortgage - the two GROWTH doors. |
| 2397 | `private final java.util.Set<String> refusedMortgageForCapital` |  |
| 2431 | `private boolean insuredRationed` | True this month when the capital rule rations the insured mortgages too - the bank's leverage requirement binding. |
| 2523 | `private final Map<String, double[]> statements` | Each sector's last month-end readings, oldest first: {owed, owned, owed, owned, ...}, at most STATEMENT_MONTHS pairs. |
| 2642 | `private final List<Written> writtenThisMonth` |  |
| 3004 | `private final Map<String, ShortReason> shortReason` | Why the shortfall desk left each sector short this month, and what the month asked each to pay - both from the credit settle, read at the month's defaults; the month's, not saved. |
| 3005 | `private final Map<String, Double> monthObligations` |  |
| 3017 | `private final Map<String, Double> cannotPayShort` | The month's cash-flow defaults, per sector: what was still unpaid, the share of the sector that could not pay, and why nobody lent. |
| 3018 | `private final Map<String, Double> cannotPayShareThisMonth` |  |
| 3019 | `private final Map<String, ShortReason> cannotPayReason` |  |
| 3113 | `private final Map<String, Double> interimLentThisMonth` | The month's interim financing, per sector: lent (at face), handed to the till (the overdraft it closed), refused and why, fallen due, and written off in a backstop. |
| 3114 | `private final Map<String, Double> interimHanded` |  |
| 3115 | `private final Map<String, String> interimRefused` |  |
| 3116 | `private final Map<String, Double> interimMaturedThisMonth` |  |
| 3117 | `private final Map<String, Double> interimWrittenOffThisMonth` |  |
| 3119 | `private int monthNow` | The month an interim loan is written in: the credit settle's (coverShortfall()). |
| 3131 | `private final Map<String, Double> backstopInBanThisMonth` | The overdraft the backstop closed this month in a sector already inside its ban, which nobody would make an interim loan to: forgiven, nothing new written off (restructure(sector, true)). |
| 3345 | `private final Map<String, Double> loansDefaultedThisMonth` | What defaulted this month in each class, per sector (0.7.12): the loans' and the bonds' share of the slice, or all of both in the backstop - before what was recovered, so recovered over defaulted is each class's recov... |
| 3346 | `private final Map<String, Double> bondsDefaultedThisMonth` |  |
| 3353 | `private final Map<String, Double> bondWrittenOffThisMonth` | The bondholders' side of the month's defaults (0.7.12), per sector: the face written off its bonds, every holder together. |
| 3354 | `private final Map<String, Double> bondWrittenOffTotal` |  |
| 3394 | `private final Map<String, Double> defaultedThisMonth` | The month's defaults, per sector, for the notice and the playtest - struck by restructureInsolventSectors() and read in the same month (Inbox.takeMonth()), so not saved: the bank's own record of the month's write-off ... |
| 3395 | `private final Map<String, Double> defaultShareThisMonth` |  |
| 3396 | `private final java.util.Set<String> restructuredThisMonth` |  |
| 3442 | `private final Map<String, Double> principalJudged` | What each sector owed when the month's insolvency check judged it, against the assets it judged it on (getAssets(), struck at the same check). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 3517 | **type** `public class BusinessDebtManager` | Private-sector credit. |
| 73 | 9 | `public void setSectors(String[] keys)` | The registry's names, in its order. |
| 83 | 1 | `public String[] sectors()` |  |

### PRICING FROM THE CURVE (0.7.8) (lines 85-152)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 3 | `public static double expectedLossSpread(double leverage)` | A borrower's own expected loss over the book's, at this leverage: LOAN_LOSS_GIVEN_DEFAULT x PD(L) less Bank.BASE_LOSS_RATE, never below nothing - the part of its rate that is its risk. |
| 139 | 4 | `static double pricingLeverage(double principal, double assets)` | The leverage a price is read at: what is owed over the assets, and the whole curve against no assets at all, owing or not - a business with nothing is not a good credit, it is an empty one. |
| 149 | 3 | `public static double expectedLossSpread(double leverage, double lossGivenDefault)` | ...at a loss given default of its own (0.7.12): the same curve at an instrument's own loss - a loan's is the one-argument form, and the screens quote a bond's beside it (RECOVERIES BY INSTRUMENT). |

### INSOLVENCY (lines 153-241)

### A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) (lines 242-331)

### RECOVERIES BY INSTRUMENT (0.7.12, round 2) (lines 332-540)

| line | len | member | says |
|---:|---:|---|---|
| 399 | 35 | `public static double normalCdf(double x)` | The standard normal cumulative distribution, N(x): Hart's (1968) double-precision rational approximation as Graeme West gives it ("Better approximations to cumulative normal functions", Wilmott, 2005), with a continue... |
| 442 | 5 | `public static double defaultProbability(double leverage)` | PD(L): the share of a sector's firms - weighted by what they owe - that default within a year at this leverage, N(ln(L / INSOLVENCY_TRIGGER) / ASSET_VOLATILITY). |
| 449 | 6 | `public static double defaultProbability(double leverage, double months)` | ...over this many months instead of a year: 1 - (1 - PD)^(months / DEFAULT_HORIZON_MONTHS), for a borrower held at this leverage. |
| 457 | 3 | `public static double monthlyDefaultShare(double leverage)` | h: the share of a sector's debt that defaults in one month at this leverage - the monthly hazard of PD(L). |
| 462 | 4 | `static double leverageOf(double principal, double assets)` | A sector's leverage for the curve: what it owes over its assets, infinite when it owes anything against nothing. |
| 506 | 3 | `static int exclusionFor(int defaultsSoFar)` | How long a sector is shut out, given how many times it has done this. |

### AND ITS BONDS (0.7.12) (lines 541-1091)

| line | len | member | says |
|---:|---:|---|---|
| 563 | 17 | **type** `public interface BondBook` | What the bond market tells the lender about each sector's bonds. |
| 565 | 1 | `double principal(String sector)` _(in BusinessDebtManager.BondBook)_ | The face a sector owes on its bonds. |
| 567 | 1 | `double monthlyCoupon(String sector)` _(in BusinessDebtManager.BondBook)_ | The coupons its bonds ask this month, on the face outstanding. |
| 569 | 1 | `double writeDown(String sector, double scale)` _(in BusinessDebtManager.BondBook)_ | Writes a sector's bonds down to this share of their face, every holder pro rata. |
| 571 | 1 | `default double faceDueWithin(String sector, int month, int months)` _(in BusinessDebtManager.BondBook)_ | The face of a sector's bonds that falls due within `months` settles of `month`'s (0.7.74, the sector statements' R2): nothing from a book that cannot say. |
| 573 | 1 | `default int lastMaturity(String sector)` _(in BusinessDebtManager.BondBook)_ | The month a sector's last bond falls due (0.7.75, R2's "runs to"): 0 from a book that cannot say. |
| 575 | 1 | `default double couponRate(String sector)` _(in BusinessDebtManager.BondBook)_ | The coupon its bonds pay a year, weighted by face (0.7.75, R2's rate): 0 from a book that cannot say. |
| 577 | 1 | `default double faceIssuedSoFar(String sector)` _(in BusinessDebtManager.BondBook)_ | The face a sector has sold, and repaid at maturity, since the counters started (0.7.75, R7): in memory; nothing from a book that does not count. |
| 578 | 1 | `default double faceRepaidSoFar(String sector)` _(in BusinessDebtManager.BondBook)_ |  |
| 582 | 18 | **type** `public interface BondDesk` | ...and where the desks ask whether a bond would be cheaper than the bank (BondMarket, WHO ISSUES, AND WHEN). |
| 595 | 2 | `Plan plan(String sector, double amount, double loanRate, double loanRoom, double bondRoom, double extraAssets, int month)` _(in BusinessDebtManager.BondDesk)_ | How to finance `amount` for this sector: a bond, the bank, or both. |
| 598 | 1 | `double issue(Plan plan, int month)` _(in BusinessDebtManager.BondDesk)_ | Issues a plan's bond. |
| 616 | 18 | **type** `public record Plan(String sector, double amount, double bondFace, double coupon, double allIn, double loan,...` | How one borrowing is financed (0.7.12): of `amount`, `bondFace` in a bond at `coupon` - `allIn` with its issuing costs spread over its life - and `loan` from the bank at `loanRate`, `loanAllIn` with its fee spread ove... |
| 620 | 3 | `public static Plan bankOnly(String sector, double amount, double loanRate, double loanAllIn, double clearing)` _(in BusinessDebtManager.Plan)_ | All of it from the bank. |
| 624 | 1 | `public boolean hasBond()` _(in BusinessDebtManager.Plan)_ | True when some of it is a bond. |
| 626 | 3 | `public double monthlyInterest()` _(in BusinessDebtManager.Plan)_ | The interest a month the plan costs, both parts: what the project's own test is asked to carry, each at its instrument's rate. |
| 630 | 1 | `public double blendedRate()` _(in BusinessDebtManager.Plan)_ | ...as an annual rate on what it raises. |
| 632 | 1 | `public boolean covers()` _(in BusinessDebtManager.Plan)_ | True when it raises the whole amount. |
| 639 | 4 | `public void setBondMarket(BondBook book, BondDesk desk)` | The bond market, wired by Game; null in a harness that builds this class alone. |
| 644 | 1 | `public BondDesk getBondDesk()` |  |
| 650 | 4 | `public double takeBondProceeds(String sector)` | The bonds the shortfall desk issued for a sector this month, net of their costs, handed over once. |
| 657 | 1 | `public Plan getShortfallPlan(String sector)` |  |
| 728 | 3 | `public static double feeOn(double principal)` | What a loan of this principal pays up front: Bank.LOAN_FEE of it. |
| 732 | 1 | `public double getFeesThisMonth()` |  |
| 733 | 1 | `public double getFeesThisMonth(String sector)` |  |
| 748 | 1 | `public double getLentThisMonth()` |  |
| 749 | 1 | `public double getRepaidThisMonth()` |  |
| 751 | 3 | `public double getLentThisMonth(String sector)` |  |
| 755 | 3 | `public double getRepaidThisMonth(String sector)` |  |
| 759 | 18 | `public void startAuditMonth()` |  |
| 778 | 7 | `public BusinessDebtManager()` |  |
| 794 | 3 | `public void setPrimeRate(double rate)` | Prime, pushed in by Game each month from Bank.prime() before anything is priced (0.7.7; the city's rate before it). |
| 799 | 3 | `public void setInsuredMortgageRate(double rate)` | The insured mortgage's rate, pushed in by Game with prime (Bank.insuredMortgageRate()). |
| 804 | 3 | `public double getInsuredMortgageRate()` | What a new insured mortgage is written at this month, and what a term that ends renews at. |
| 809 | 3 | `public void setAssets(String sector, double totalAssets)` | Total assets from that sector's balance sheet - the denominator of leverage. |
| 823 | 4 | `public void setConcentrationCharges(Map<String, Double> charges)` |  |
| 829 | 1 | `public double getConcentrationCharge(String sector)` | What a loan to this sector pays for the book's concentration, a year: part of its rate. |
| 832 | 5 | `public void updateRates()` | pricing |
| 839 | 7 | `private double priceSector(String sector)` | The quote: the curve at the leverage the sector's last quarter of statements reads (getQuarterLeverage()), and the whole curve against no assets (pricingLeverage()). |
| 848 | 3 | `private double priceSector(String sector, double extraPrincipal, double extraAssets)` | A quote, with a deal on top (see quote()): the rate its parts add up to. |
| 872 | 43 | `private QuoteParts quote(String sector, double extraPrincipal, double extraAssets)` | ratio. |
| 918 | 3 | `public double getRate(String sector)` | What NEW borrowing costs this sector today. |
| 923 | 3 | `public double getSpread(String sector)` | What this sector pays over prime: its own expected loss, its record and the book's concentration on it (QuoteParts.spread()). |
| 928 | 3 | `public double getRiskSpread(String sector)` | ...the first part of it: its own expected loss over the book's, at the leverage its last quarter reads (expectedLossSpread(), quarterPrincipal() over quarterAssets()) and a loan's loss given default. |
| 933 | 3 | `public double getRecordSurcharge(String sector)` | ...and the second: DEFAULT_SURCHARGE a write-down on its record, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 937 | 3 | `private double recordSurcharge(String sector)` |  |
| 952 | 11 | **type** `public record QuoteParts(double prime, double risk, double record, double concentration)` | THE QUOTE IN ITS PARTS (0.7.33): the prime a sector's rate was struck on and the three things over it - its own expected loss over the book's, its record and the book's concentration - as quote() added them up the las... |
| 954 | 6 | `public double spread()` _(in BusinessDebtManager.QuoteParts)_ | What the sector pays over prime: the three parts, added in quote()'s order. |
| 961 | 1 | `public double rate()` _(in BusinessDebtManager.QuoteParts)_ | ...and the rate: prime and the spread, which is getRate(). |
| 968 | 1 | `public QuoteParts quoteParts(String sector)` | A sector's rate in its parts as it was last priced; null for a sector not priced yet, whose getRate() is its placeholder. |
| 977 | 3 | `public double projectRate(String sector, double amount)` | WHAT A PROJECT LOAN OF THIS SIZE WOULD BE WRITTEN AT (0.7.8): the curve at the leverage it leaves the sector at, the building counted at the loan's value - the rate issueProjectLoan() writes, and the one Game.consider... |
| 986 | 4 | `public double leverageAfterProject(String sector, double amount)` | ...and the leverage that loan is priced at: the last quarter's statements with the deal on top, the building counted. |
| 992 | 3 | `public double getPrimeRate()` | Prime, as the bank set it this month. |
| 996 | 4 | `public double getLeverage(String sector)` |  |
| 1001 | 4 | `public void setCash(String sector, double cash)` |  |
| 1016 | 3 | `public double getCash(String sector)` |  |
| 1020 | 3 | `private double getOverdraftForgivenPending(String sector)` |  |
| 1025 | 5 | `public double takeOverdraftForgiven(String sector)` | The overdraft a restructure forgave this month, handed over once. |
| 1031 | 3 | `public double getAssets(String sector)` |  |
| 1036 | 5 | `public double getAllPrincipal()` | Everything every sector owes the bank. |
| 1043 | 3 | `public double getPrincipal(String sector)` | What a sector owes: its bank loans and, since 0.7.12, its bonds - see AND ITS BONDS. |
| 1048 | 5 | `public double getEverythingOwed()` | What every business owes, bank loans and bonds together: getPrincipal() over every sector (0.7.32, the Finances tab's bond market, which summed it itself). |
| 1055 | 9 | `public double getLoanPrincipal(String sector)` | What a sector owes the bank: its loans and its mortgages. |
| 1071 | 8 | `public double getTermLoanPrincipal(String sector)` | ...of which its plain bank loans (0.7.30, the Sectors screen's debt mix): its loans that are neither a mortgage nor interim financing, so the four parts - these, its mortgages, its interim financing and its bonds - ar... |
| 1081 | 3 | `public double getBondPrincipal(String sector)` | ...and what it owes on its bonds (0.7.12): nothing without a bond market. |
| 1086 | 5 | `public double getLoanShare(String sector)` | The bank loans' share of what a sector owes: 1 with no bonds, and with no debt at all. |

### WHAT IT OWES, BY KIND AND BY WHEN (0.7.74, the sector statements' (lines 1092-1179)

| line | len | member | says |
|---:|---:|---|---|
| 1108 | 3 | `private static int kindOf(BusinessDebt loan)` | A loan's kind, DEBT_KINDS' index: a mortgage, interim financing, or a plain bank loan. |
| 1113 | 8 | `public double[] interestByKind(String sector)` | This month's interest by kind (R1): each loan's getMonthlyInterestExpense() by what it is, and the bonds' coupons - getMonthlyInterest() in four parts. |
| 1145 | 34 | `public double[][] debtByKind(String sector, int month)` | What a sector owes by kind (R2), and what of each the settles ahead would ask within a year and within five, as the model pays it: a loan whole in its last month (processMonth()'s maturity, principalDueNextMonth()'s r... |

### WHAT MOVED IT, BY KIND (0.7.75, the sector statements' R7) (lines 1180-1614)

| line | len | member | says |
|---:|---:|---|---|
| 1202 | 4 | `private void moved(String sector, int flow, int kind, double amount)` | A move of `amount` in one kind's principal: BORROWED, REPAID or WRITTEN_OFF. |
| 1208 | 11 | `public double[][] debtMovedByKind(String sector)` | One sector's principal moved so far, {borrowed, repaid, written off}, each in DEBT_KINDS' order: running totals, read twice and differenced (R7). |
| 1220 | 7 | `public double getTotalPrincipal()` |  |
| 1229 | 3 | `public double getMonthlyInterest(String sector)` | This month's interest cost for a sector - the income statement's expense line: its loans' interest and, since 0.7.12, its bonds' coupons. |
| 1234 | 9 | `public double getLoanInterest(String sector)` | ...the part the bank is paid on its loans. |
| 1245 | 5 | `public double getTotalMonthlyInterest()` | Every sector's interest bill, loans and bonds. |
| 1256 | 4 | `public double getEffectiveRate(String sector)` | Blended annual rate actually being paid on existing debt, as opposed to getRate() which is what the next loan would cost. |
| 1261 | 9 | `public int getLoanCount(String sector)` |  |
| 1271 | 3 | `public List<BusinessDebt> getLoans()` |  |
| 1275 | 9 | `public List<BusinessDebt> getLoans(String sector)` |  |
| 1293 | 84 | `public void processMonth()` | Advances every loan and retires the ones that mature. |
| 1379 | 7 | `public double takeMaturedPrincipal(String sector)` | Reads and clears the principal that fell due this month for one sector. |
| 1397 | 182 | `public double coverShortfall(String sector, double cash, double monthlyLoss, int month)` | Underwrites a loan if the sector is short, and returns the proceeds - and since 0.7.12 sells a bond in part of its place where one is cheaper, whose proceeds wait for the settle (takeBondProceeds()). |
| 1589 | 5 | `private void countOpenLine(String sector, double lent, double oldRule)` |  |
| 1596 | 1 | `public boolean capitalRuleOn()` | True this month when the bank's capital rule limits new lending: under its target, or under its minimum. |
| 1599 | 1 | `public double getLineLentRationed(String sector)` | What the working-capital line lent this sector this month while the capital rule was on (round 5). |
| 1601 | 1 | `public double getLineLentPastOldRule(String sector)` | ...of it, what the 0.7.8 rule would have refused. |
| 1613 | 1 | `public double getShortfallLentThisMonth(String sector)` | ...one sector's. |

### ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) (lines 1615-1808)

| line | len | member | says |
|---:|---:|---|---|
| 1688 | 3 | `public double assetsNow(String sector, double cash)` | A sector's assets on the sheet as it stands, its till at `cash`: the month's refresh (getAssets()), with the till it read (getCash()) moved to this one. |
| 1701 | 7 | `public boolean pastDefaultPoint(String sector, double cash)` | Past the default point ON ITS QUARTER (round 3): owing more than INSOLVENCY_TRIGGER times what it owned, averaged over its last STATEMENT_MONTHS month-end readings (quarterPrincipal(), quarterAssets()) - or anything, ... |
| 1710 | 8 | `public boolean pastDefaultPoint(String sector, double cash, double amount, double handed)` | ...or past it once lent `amount`, `handed` of it paid into the till: before the loan or after it, on the same reading. |
| 1720 | 4 | `private double[] defaultPointReading(String sector, double cash)` | {owed, owned} as the default point reads them: the quarter's averages, or with no reading the sheet as it stands, its till at `cash` (round 3). |
| 1726 | 4 | `public double defaultPointLeverage(String sector)` | The leverage the default point reads a sector at now: its quarter's, or as it stands with no reading - for the investor's words and the screens. |
| 1737 | 1 | `public double getRefusedAtDefaultPoint(String sector)` | What the shortfall desk would have lent this sector this month and did not, because it is past the default point (0.7.12, round 2). |
| 1740 | 5 | `public double getRefusedAtDefaultPoint()` | ...every sector's. |
| 1747 | 1 | `public double getNotRenewedAtDefaultPoint(String sector)` | The mortgage balances that fell due whole this month rather than renew, because the landlords were past the default point. |
| 1750 | 1 | `public boolean wasRefusedAtDefaultPoint(String sector)` | True when the investment desk turned this sector's project down this month because it is past the default point. |
| 1758 | 5 | `public double bondCeilingRoom(String sector, double multiple)` | THE CEILING A BOND IS HELD TO (0.7.12): the line at this multiple of its assets, and the ban, as for a loan. |
| 1770 | 6 | `public double projectLoanRoom(String sector, double amount)` | WHAT THE BANK WILL LEND OF A PROJECT (0.7.12): the whole of it if canFundProject() says yes, and otherwise what its capital rule leaves under the default point after the deal - the most a bond and a loan may raise of ... |
| 1778 | 6 | `public double projectBondRoom(String sector, double amount)` | ...and the bond's own ceiling: the same default point after the deal, and the ban. |
| 1805 | 3 | `public double borrowingRoom(String sector)` | How much more this sector may borrow today, from either desk. |

### ...AND WHAT THE NEXT SETTLE WOULD DO, ASKED AHEAD (0.7.12, round 6) (lines 1809-1929)

| line | len | member | says |
|---:|---:|---|---|
| 1826 | 20 | `public double principalDueNextMonth(String sector)` | The principal that falls due at the next settle, as processMonth() will park it: each loan that matures, whole; each mortgage's next payment's principal, and its whole balance where the payment ends the term and the l... |
| 1858 | 12 | `public double workingCapitalLine(String sector, double cash, double due)` | What the working-capital line would hand this sector's till at the next settle, net of its fee: coverShortfall()'s room on coverShortfall()'s rules - the ceiling at MAX_LOAN_TO_ASSETS on what it will owe once what fal... |
| 1898 | 23 | `public boolean canFundProject(String sector, double amount)` | Whether the INVESTMENT desk will fund a project of this size. |
| 1923 | 6 | `private double ceilingRoom(String sector, double multiple)` | The ceiling alone: nothing while the lender is shut or the sector barred. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 1930-2317)

| line | len | member | says |
|---:|---:|---|---|
| 2041 | 26 | `public boolean canFundMortgage(String sector, double shortfall, double cost)` | Whether the lender will write a mortgage for this shortfall on a building of this cost: open, the sector not barred, the loan no more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost, and the borrower not past the ... |
| 2069 | 1 | `public String getMortgageRefusal()` | Why canFundMortgage() last said no, or null if it said yes. |
| 2078 | 21 | `public Mortgage issueMortgage(String sector, double shortfall, int month)` | WRITES A MORTGAGE for this shortfall: the loan that covers it once the fee is paid (Mortgage.loanFor()), the premium added, at the insured rate. |
| 2101 | 1 | `public double getPremiumsThisMonth()` | The premiums on the mortgages written this month: the treasury's revenue line. |
| 2103 | 1 | `public double getPremiumsThisMonth(String sector)` | ...by sector, which its cash flow statement takes off what it was handed. |
| 2105 | 1 | `public double getPremiumsTotal()` | The premiums written over the city's life. |
| 2108 | 1 | `public int getMortgagesWrittenThisMonth()` | The mortgages written this month, renewed this month, and fallen due this month because the lender could not renew them. |
| 2109 | 1 | `public int getRenewedThisMonth()` |  |
| 2110 | 1 | `public int getFallenDueThisMonth()` |  |
| 2112 | 1 | `public int getRenewedLifetime()` | ...over the run, for the playtest; not saved. |
| 2113 | 1 | `public int getFallenDueLifetime()` |  |
| 2116 | 7 | `public List<Mortgage> getMortgages(String sector)` | Every mortgage one sector owes, in the order written. |
| 2125 | 5 | `public List<Mortgage> getMortgages()` | Every mortgage in the city. |
| 2132 | 1 | `public int getMortgageCount(String sector)` | How many mortgages one sector owes. |
| 2135 | 5 | `public double getMortgagePrincipal(String sector)` | What one sector owes on its mortgages. |
| 2142 | 5 | `public double getMortgagePrincipal()` | What every sector owes on mortgages: the bank's mortgage book. |
| 2149 | 5 | `public double getInsuredPrincipal(String sector)` | What one sector owes on its insured mortgages - the part of its debt the city insures. |
| 2156 | 5 | `public double getInsuredPrincipal()` | ...every sector's: what the bank's book holds at Bank.RISK_INSURED_MORTGAGE. |
| 2163 | 3 | `public double getUninsuredPrincipal(String sector)` | What one sector owes the bank that nobody insures: its loans less its insured mortgages - what the bank's allowance reads, and its capital rule while the leverage ratio does not bind (rationedPrincipal()). |
| 2168 | 5 | `public double getMortgagePayment(String sector)` | The level payments one sector's mortgages ask next month: interest and principal together. |
| 2175 | 5 | `public double getMortgagePayment()` | ...every sector's. |
| 2182 | 3 | `public double getMortgageRate(String sector)` | The rate one sector's mortgages carry, weighted by what is owed on each; 0 with none. |
| 2187 | 3 | `public double getMortgageRate()` | ...every mortgage's. |
| 2191 | 8 | `private static double weightedRate(List<Mortgage> ms)` |  |
| 2201 | 8 | `public int getNextRenewalMonth(String sector)` | The first month one of this sector's mortgages renews, or -1 with none. |
| 2211 | 5 | `public int getMortgagesRenewingWithin(int months)` | How many mortgages renew within this many months of this one. |
| 2218 | 4 | `public boolean allMortgagesInsured()` | True when every mortgage in the city is insured - which every one this build writes is. |
| 2224 | 1 | `public double getMortgageRepaidThisMonth(String sector)` | The principal one sector's mortgage payments took this month. |
| 2227 | 5 | `public double getMortgageRepaidThisMonth()` | ...every sector's. |
| 2234 | 1 | `public Map<String, Double> getMortgageRepaidToSave()` | The month's principal repaid on mortgages, for the save: a copy, by sector name. |
| 2237 | 7 | `public void restoreMortgageRepaid(Map<String, Double> saved)` | ...and back on load. |
| 2246 | 1 | `public double getInsuredWrittenOffThisMonth(String sector)` | What this month's write-downs took off one sector's insured mortgages: the claim the treasury pays the bank. |
| 2249 | 5 | `public double getInsuredWrittenOffThisMonth()` | ...every sector's: the month's claims. |
| 2256 | 1 | `public double getInsuredWrittenOffTotal(String sector)` | What write-downs have taken off one sector's insured mortgages over the city's life. |
| 2259 | 5 | `public double getInsuredWrittenOffTotal()` | ...every sector's: the claims over the city's life. |
| 2266 | 1 | `public double getPremiumsTotalToSave()` | The insurance book's record, for the save: the premiums over the city's life. |
| 2269 | 1 | `public Map<String, Double> getInsuredWrittenOffTotals()` | The claims over the city's life, by sector, for the save. |
| 2272 | 8 | `public void restoreInsuranceRecord(double premiums, Map<String, Double> claims)` | ...and both back on load. |
| 2286 | 3 | `private double writeDownSector(String sector, double scale)` | Writes every instrument of one sector down to this share, pro rata, and says what came off its insured mortgages - the claim. |
| 2291 | 8 | `private void writeDownInterim(String sector, double scale)` | One sector's interim loans alone, to this share of what they were. |
| 2301 | 16 | `private double writeDownSector(String sector, double scale, boolean interimToo)` | ...its interim loans with the rest, or (false) every loan but them - they rank first (INTERIM FINANCING). |

### ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) (lines 2318-2466)

| line | len | member | says |
|---:|---:|---|---|
| 2405 | 3 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | The bank's capital rule for the month, from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(). |
| 2415 | 14 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly, boolean insuredToo)` | ...and whether it rations the insured mortgages too: true when the bank's leverage requirement is the larger (Bank.leverageBinds(), 0.7.11 round 2), because a mortgage then uses the capital the bank is short of. |
| 2433 | 1 | `public boolean isInsuredRationed()` |  |
| 2436 | 3 | `private double rationedPrincipal(String sector)` | The debt the capital rule reads: what the sector owes the bank, uninsured, or all of it while insuredRationed. |
| 2446 | 6 | `public double capitalRoom(String sector)` | What the bank's capital lets this sector borrow this month, over what it owes now: its debt when the rule was set, grown by the month's limit (none under the minimum), less what it owes - so what matured is room to re... |
| 2454 | 1 | `public double getCapitalGrowth()` | The month's limit on a borrower's growth, a share a month: infinite with none. |
| 2457 | 1 | `public boolean isKeepGoingOnly()` | True when the bank lends only to keep its borrowers going this month. |
| 2460 | 1 | `public boolean wasRefusedForCapital(String sector)` | True when the capital rule refused this sector a project this month. |
| 2463 | 1 | `public boolean wasProjectRefusedForCapital(String sector)` | ...a building's loan, this month (round 5, the growth doors counted apart). |
| 2465 | 1 | `public boolean wasMortgageRefusedForCapital(String sector)` | ...a new mortgage, this month. |

### THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) (lines 2467-2669)

| line | len | member | says |
|---:|---:|---|---|
| 2543 | 11 | `public void recordStatement(String sector, double owed, double owned)` | One month-end reading of a sector: what it owed and what it owned, as the bank read them (Game.sectorPositions()). |
| 2556 | 7 | `public double quarterPrincipal(String sector)` | What the sector owed, averaged over its last quarter of readings - what it owes now, with none. |
| 2565 | 7 | `public double quarterAssets(String sector)` | ...and what it owned. |
| 2574 | 4 | `public double getQuarterLeverage(String sector)` | The leverage the bank reads the sector at: its quarter's average debt over its average assets, 0 with no assets. |
| 2580 | 3 | `public double getQuarterDefaultRate(String sector)` | The sector's default rate a year at the leverage its last quarter reads (getQuarterLeverage()) - the reading its price is struck on (getRiskSpread()), which the screens print beside that price. |
| 2585 | 4 | `public int getStatementCount(String sector)` | How many readings a sector has, up to STATEMENT_MONTHS. |
| 2591 | 5 | `public Map<String, double[]> getStatementsToSave()` | The readings, for the save: a copy, by sector name. |
| 2598 | 10 | `public void restoreStatements(Map<String, double[]> saved)` | ...and back on load. |
| 2610 | 3 | `public void setLendingOpen(boolean open)` | Game tells the lender each month whether the bank behind it is standing. |
| 2614 | 3 | `public boolean isLendingOpen()` |  |
| 2624 | 3 | `public BusinessLoan issueLoan(String sector, double faceValue, int month)` | Writes a loan of this principal - a shortfall loan, priced at what the sector will owe over the assets it has. |
| 2629 | 3 | `public BusinessLoan issueProjectLoan(String sector, double faceValue, int month)` | ...a project's: priced with the building it buys counted in the assets, at the loan's value (projectRate()). |
| 2640 | 1 | **type** `public record Written(String sector, double amount, double leverage, double rate, boolean project)` | One loan written this month: to whom, how much, the leverage it left the borrower at, the rate it was written at, and whether it bought a building (0.7.8, for the playtest's count of loans written past the watch line ... |
| 2645 | 1 | `public List<Written> getWrittenThisMonth()` | Every loan written this month, in the order written. |
| 2647 | 22 | `private BusinessLoan write(String sector, double faceValue, int month, double extraAssets, boolean project)` |  |

### insolvency (lines 2670-2942)

| line | len | member | says |
|---:|---:|---|---|
| 2672 | 3 | `public boolean isBorrowingBlocked(String sector)` |  |
| 2676 | 3 | `public int getBlockedMonths(String sector)` |  |
| 2680 | 3 | `public double getWrittenOffThisMonth(String sector)` |  |
| 2684 | 3 | `public double getWrittenOffTotal(String sector)` |  |
| 2688 | 3 | `public int getRestructureCount(String sector)` |  |
| 2700 | 9 | `public void restoreWriteOffs(java.util.Map<String, Double> totals)` | Puts the write-off history back on load. |
| 2710 | 3 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2737 | 3 | `public java.util.Map<String, Integer> getRestructureCounts()` | THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED. |
| 2741 | 3 | `public java.util.Map<String, Integer> getBlockedMonthsAll()` |  |
| 2745 | 19 | `public void restoreCreditRecord(java.util.Map<String, Integer> counts, java.util.Map<String, Integer> blocked)` |  |
| 2765 | 7 | `public double getTotalWrittenOff()` |  |
| 2789 | 4 | `public boolean isInsolvent(String sector)` | Is every firm in this sector under water at once - the backstop's case? |
| 2809 | 3 | `public double restructure(String sector)` | THE BACKSTOP: writes a sector with nothing left down to what its assets can support - RESTRUCTURE_TARGET of nothing - forgives its overdraft, counts the default on its record and shuts it out for exclusionFor(). |
| 2822 | 120 | `public double restructure(String sector, boolean forced)` | ...or, `forced`, a sector nobody would make an interim loan to - still past the default point after the month's write-down, or its bank shut - whatever its assets read (INTERIM FINANCING, round 5): the whole sector to... |

### CAN'T PAY MEANS DEFAULT (0.7.12, round 4) (lines 2943-3040)

| line | len | member | says |
|---:|---:|---|---|
| 3001 | 1 | **type** `public enum ShortReason` | Why the shortfall desk left a sector short (round 4). |
| 3008 | 1 | `public ShortReason getShortReason(String sector)` | Why the shortfall desk left this sector short this month - the tighter of its limits, or its reason to refuse - or null if it was not asked. |
| 3011 | 3 | `public void setMonthObligations(String sector, double amount)` | What the month asked this sector to pay: its costs, interest and taxes on this month's statement and the principal that fell due (EconomyManager.settleBusinessCredit()). |
| 3014 | 1 | `public double getMonthObligations(String sector)` |  |
| 3022 | 1 | `public double getCannotPayShort(String sector)` | What the month's bills still left unpaid in one sector when the cash-flow test struck (round 4) - lent as interim financing since round 5, or the backstop's; 0 if it did not default for want of cash. |
| 3024 | 1 | `public double getCannotPayShare(String sector)` | ...the share of it that could not pay, h. |
| 3026 | 1 | `public ShortReason getCannotPayReason(String sector)` | ...and why no lender stood behind it, or null. |
| 3034 | 6 | `double cannotPayShare(String sector)` | THE CASH-FLOW TEST'S SHARE: the part of a sector whose till is short that cannot pay - its shortfall over what the month asked it to pay, all of it when that is more than the month's bills. |

### INTERIM FINANCING (0.7.12, round 5) (lines 3041-3575)

| line | len | member | says |
|---:|---:|---|---|
| 3122 | 1 | `public double getInterimLentThisMonth(String sector)` | What the interim lender lent this sector this month, at face (round 5). |
| 3124 | 1 | `public String getInterimRefusal(String sector)` | Why the interim lender would not lend to this sector this month, or null. |
| 3126 | 1 | `public double getInterimMaturedThisMonth(String sector)` | The interim principal that fell due this month in this sector: repaid, or rolled by the shortfall desk. |
| 3128 | 1 | `public double getInterimWrittenOffThisMonth(String sector)` | The interim principal a backstop wrote off this month in this sector. |
| 3133 | 1 | `public double getBackstopInBanThisMonth(String sector)` | ...one sector's. |
| 3136 | 4 | `public double takeInterimHanded(String sector)` | The interim loan's cash this sector's till is owed from the month's defaults, handed over once (EconomyManager.settleInsolvency()). |
| 3142 | 5 | `public double getInterimPrincipal(String sector)` | What one sector owes on interim financing. |
| 3149 | 5 | `public double getInterimPrincipal()` | ...every sector's. |
| 3156 | 5 | `public int getInterimCount(String sector)` | How many interim loans one sector has outstanding. |
| 3163 | 5 | `public int getInterimCount()` | ...every sector's. |
| 3176 | 10 | `String interimRefusal(String sector, double cash, double loan, double handed, double writtenDown)` | THE INTERIM LENDER'S TEST: null to lend, or why not. |
| 3188 | 7 | `double priceInterim(String sector, double faceValue)` | What an interim loan of this face would be written at: prime, the curve at the leverage its rank sees, the record and the concentration (INTERIM FINANCING). |
| 3197 | 16 | `private InterimLoan writeInterim(String sector, double faceValue)` | Writes an interim loan: counted in the month's lending and fees like any loan, the bank paying it out at the settle. |
| 3225 | 57 | `public double restructureInsolventSectors()` | THE MONTH'S DEFAULTS, every sector: the backstop for a sector with nothing left (restructure()), and for every other the slice of its debt whose firms fell through the default point (defaultSlice()) - or, since round ... |
| 3298 | 3 | `public double defaultSlice(String sector)` | THE SLICE: the share of this sector's debt whose firms fell through the default point this month, monthlyDefaultShare() of it at the leverage the month's check reads (principal over getAssets(), struck after the balan... |
| 3303 | 40 | `public double defaultSlice(String sector, double atLeast)` | ...or, if more, this share: the part of it that cannot pay this month (round 4, CAN'T PAY MEANS DEFAULT). |
| 3349 | 1 | `public double getLoansDefaultedThisMonth(String sector)` | The loans and the bonds that defaulted this month in one sector, before recovery (0.7.12). |
| 3350 | 1 | `public double getBondsDefaultedThisMonth(String sector)` |  |
| 3356 | 6 | `private void recordBondWriteOff(String sector, double face)` |  |
| 3364 | 1 | `public double getBondWrittenOffThisMonth(String sector)` | What this month's defaults took off one sector's bonds, every holder together (0.7.12). |
| 3367 | 1 | `public double getBondWrittenOffTotal(String sector)` | ...over the city's life. |
| 3370 | 5 | `public double getBondWrittenOffTotal()` | ...every sector's, over the city's life. |
| 3377 | 1 | `public Map<String, Double> getBondWrittenOffTotals()` | The bondholders' losses over the city's life, by sector, for the save. |
| 3380 | 7 | `public void restoreBondWrittenOff(Map<String, Double> totals)` | ...and back on load; an older save has none. |
| 3399 | 1 | `public double getDefaultedThisMonth(String sector)` | The debt whose firms defaulted this month in the slice, before what its creditors recover - each instrument's write-off is its own loss given default of its part (getLoansDefaultedThisMonth(), getBondsDefaultedThisMon... |
| 3402 | 1 | `public double getDefaultShareThisMonth(String sector)` | The share of the sector's debt that defaulted this month in the slice, h. |
| 3405 | 1 | `public boolean wasRestructuredThisMonth(String sector)` | True when the backstop wrote this sector down whole this month. |
| 3408 | 3 | `public double getDefaultRate(String sector)` | The sector's default rate a year at its leverage now, PD(L) - what the Bank tab shows beside its leverage. |
| 3428 | 4 | `public boolean defaultsAreNews(String sector)` | WHETHER THIS MONTH'S DEFAULTS ARE NEWS: the backstop, or a slice at least the share that defaults a month at the default point itself - monthlyDefaultShare(INSOLVENCY_TRIGGER), 5.6% of its debt, where half the sector'... |
| 3444 | 4 | `public double getPrincipalJudged(String sector)` |  |
| 3450 | 8 | `public void advanceBlocks()` | Counts down the borrowing bans. |
| 3460 | 3 | `public void setLoans(List<BusinessDebt> loans)` | save / load |
| 3464 | 35 | `public void clearLoans()` |  |
| 3501 | 29 | `public void printBusinessDebtInfo(int currentMonth)` | printers |
| 3533 | 4 | `static { ... }` |  |
| 3539 | 35 | `public void redenominate(double scale)` | Every business loan and this month's lending, in the new unit. |

