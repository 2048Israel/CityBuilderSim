# BusinessDebtManager.java - 3,358 lines · 216 methods · 24 constants · model

`ham/citybuildersim/BusinessDebtManager.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Mortgage](Mortgage.md) (30), [BusinessDebt](BusinessDebt.md) (23), [InterimLoan](InterimLoan.md) (10), [Bank](Bank.md) (8), [BusinessLoan](BusinessLoan.md) (5), [Sectors](Sectors.md) (1)

**Used by (20):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [CreditCheck](CreditCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 85 | PRICING FROM THE CURVE (0.7.8) |
| 153 | INSOLVENCY |
| 242 | A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 332 | RECOVERIES BY INSTRUMENT (0.7.12, round 2) |
| 541 | AND ITS BONDS (0.7.12) |
| 1221 | · ...AND NOBODY LENDS PAST THE CEILING |
| 1411 | ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) |
| 1605 | ...AND WHAT THE NEXT SETTLE WOULD DO, ASKED AHEAD (0.7.12, round 6) |
| 1726 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 2109 | ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) |
| 2258 | THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) |
| 2460 | · insolvency |
| 2732 | CAN'T PAY MEANS DEFAULT (0.7.12, round 4) |
| 2830 | INTERIM FINANCING (0.7.12, round 5) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2790 | `BusinessDebtManager.ShortReason.PAST_DEFAULT_POINT` |  |
| 2790 | `BusinessDebtManager.ShortReason.BANNED` |  |
| 2790 | `BusinessDebtManager.ShortReason.BANK_SHUT` |  |
| 2790 | `BusinessDebtManager.ShortReason.CEILING` |  |
| 2790 | `BusinessDebtManager.ShortReason.AFTER_SETTLE` |  |

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
| 1813 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 1815 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 1817 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 1819 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 1821 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 1823 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 2311 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 2897 | `BusinessDebtManager.INTERIM_PAST_LINE` | `"past the default point after the write-down"` | Why the interim lender would not lend: the sector still past the default point after the write-down. |
| 2899 | `BusinessDebtManager.INTERIM_BANK_SHUT` | `"the bank is shut"` | ...or the bank that would lend it has failed or is frozen in resolution. |
| 3316 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `private String[] SECTORS` | Every set of books that can borrow, by name, in the registry's order. |
| 539 | `private List<BusinessDebt> loans` |  |
| 626 | `private BondBook bondBook` |  |
| 627 | `private BondDesk bondDesk` |  |
| 638 | `private final Map<String, Double> bondProceeds` | The proceeds of the bonds the shortfall desk issued this month, per sector, until the credit settle hands them over (takeBondProceeds()). |
| 647 | `private final Map<String, Plan> shortfallPlans` | The plan the shortfall desk struck for each sector this month, for the advisor and the playtest. |
| 651 | `private double primeRate` | Prime: the bank's rate for a sound business this month, which every sector's own spread sits on (see PRICING). |
| 654 | `private double insuredMortgageRate` | The insured mortgage's rate this month (Bank.insuredMortgageRate()), pushed in by Game beside prime: what a new mortgage is written at and a term renews at. |
| 657 | `private final Map<String, Double> writtenOffThisMonth` | Written off this month, and over the whole game, per sector. |
| 658 | `private final Map<String, Double> writtenOffTotal` |  |
| 659 | `private final Map<String, Integer> restructures` |  |
| 662 | `private final Map<String, Integer> blockedMonths` | Months of borrowing ban remaining, per sector. |
| 679 | `private boolean lendingOpen` | Whether the lender is open for business at all. |
| 681 | `private final Map<String, Double> assets` |  |
| 682 | `private final Map<String, Double> rates` |  |
| 691 | `private final Map<String, Double> cashBalance` | Each sector's cash balance, refreshed with the assets. |
| 694 | `private final Map<String, Double> overdraftForgiven` | What this month's restructures forgave, per sector, until Game collects it. |
| 697 | `private final Map<String, Double> maturedPrincipal` | Principal that fell due this month, per sector, waiting to be settled. |
| 704 | `private double lentThisMonth` | The lender's side of the month, for MoneyAudit: what it advanced and what it took back. |
| 705 | `private double repaidThisMonth` |  |
| 715 | `private double feesThisMonth` | THE LOAN FEE (0.7.7): Bank.LOAN_FEE of every loan written, paid out of its proceeds - the borrower is handed the principal less this, and the bank's cash takes it at the month's settle beside the lending (Game.nextMon... |
| 716 | `private final Map<String, Double> feesBySector` |  |
| 736 | `private final Map<String, Double> lentBySector` | THE SAME TWO FIGURES, PER SECTOR. |
| 737 | `private final Map<String, Double> repaidBySector` |  |
| 812 | `private final Map<String, Double> concentrationCharges` | THE CONCENTRATION CHARGE ON EACH SECTOR'S LOANS (0.7.12): what the capital a new dollar lent to it adds for concentration costs a year, pushed in by Game from the bank beside prime (Bank .concentrationCharge(), THE BA... |
| 964 | `private final java.util.Set<String> tillReported` | The sectors whose sheet the economy has reported (setCash(), from EconomyManager.refreshCreditAssets(), every month before anything is lent): the default point reads only those (pastDefaultPoint()). |
| 1382 | `private final Map<String, Double> lineLentRationed` | WHAT THE OPEN LINE LENT WHILE THE BANK WAS SHORT OF CAPITAL (round 5), per sector, the month's: all of it - the loan and any bond in its place - while the capital rule was on, and the part of it past what the 0.7.8 ru... |
| 1383 | `private final Map<String, Double> lineLentPastOldRule` |  |
| 1406 | `private final Map<String, Double> shortfallLent` | WHAT THE SHORTFALL DESK LENT EACH SECTOR THIS MONTH, loans and bonds at face (0.7.12 round 2): the new borrowing that rolled what fell due, which a dividend is paid after (Game.payDividends(): net income less the prin... |
| 1528 | `private final Map<String, Double> refusedAtDefaultPoint` | What the default point refused this month, per sector: what the shortfall desk would otherwise have lent, the mortgages that fell due whole rather than renew, and the projects the investment desk turned down. |
| 1529 | `private final Map<String, Double> notRenewedAtDefaultPoint` |  |
| 1530 | `private final java.util.Set<String> refusedProjectAtDefaultPoint` |  |
| 1774 | `private double premiumsThisMonth` | The premiums added to the mortgages written this month, and by sector: the treasury's revenue line. |
| 1775 | `private final Map<String, Double> premiumsBySector` |  |
| 1783 | `private final Map<String, Double> mortgageRepaidBySector` | THE PRINCIPAL THE MORTGAGES' PAYMENTS TOOK THIS MONTH, by sector - part of getRepaidThisMonth(), and the figure the Bank tab and the landlords' screen show beside the payment. |
| 1786 | `private int mortgagesWrittenThisMonth, renewedThisMonth, fallenDueThisMonth` | The month's mortgages written, renewed, and fallen due because the lender could not renew them - and the same over the run, for the playtest (not saved, a count for the run). |
| 1787 | `private int renewedLifetime, fallenDueLifetime` |  |
| 1803 | `private final Map<String, Double> insuredWrittenOffThisMonth` | WHAT THE INSURANCE PAID THIS MONTH, by sector: what the month's write-downs took off insured mortgages. |
| 1804 | `private final Map<String, Double> insuredWrittenOffTotal` |  |
| 1807 | `private double premiumsTotal` | The premiums written over the city's life. |
| 1810 | `private String mortgageRefusal` | The reason canFundMortgage() last refused, or null. |
| 2181 | `private double capitalGrowth` |  |
| 2182 | `private boolean keepGoingOnly` |  |
| 2183 | `private final Map<String, Double> principalAtRule` |  |
| 2185 | `private final java.util.Set<String> refusedForCapital` | Sectors whose project the capital rule refused this month, so the investor can say so. |
| 2187 | `private final java.util.Set<String> refusedProjectForCapital` | ...by door (round 5): a building's loan, and a new mortgage - the two GROWTH doors. |
| 2188 | `private final java.util.Set<String> refusedMortgageForCapital` |  |
| 2222 | `private boolean insuredRationed` | True this month when the capital rule rations the insured mortgages too - the bank's leverage requirement binding. |
| 2314 | `private final Map<String, double[]> statements` | Each sector's last month-end readings, oldest first: {owed, owned, owed, owned, ...}, at most STATEMENT_MONTHS pairs. |
| 2433 | `private final List<Written> writtenThisMonth` |  |
| 2793 | `private final Map<String, ShortReason> shortReason` | Why the shortfall desk left each sector short this month, and what the month asked each to pay - both from the credit settle, read at the month's defaults; the month's, not saved. |
| 2794 | `private final Map<String, Double> monthObligations` |  |
| 2806 | `private final Map<String, Double> cannotPayShort` | The month's cash-flow defaults, per sector: what was still unpaid, the share of the sector that could not pay, and why nobody lent. |
| 2807 | `private final Map<String, Double> cannotPayShareThisMonth` |  |
| 2808 | `private final Map<String, ShortReason> cannotPayReason` |  |
| 2902 | `private final Map<String, Double> interimLentThisMonth` | The month's interim financing, per sector: lent (at face), handed to the till (the overdraft it closed), refused and why, fallen due, and written off in a backstop. |
| 2903 | `private final Map<String, Double> interimHanded` |  |
| 2904 | `private final Map<String, String> interimRefused` |  |
| 2905 | `private final Map<String, Double> interimMaturedThisMonth` |  |
| 2906 | `private final Map<String, Double> interimWrittenOffThisMonth` |  |
| 2908 | `private int monthNow` | The month an interim loan is written in: the credit settle's (coverShortfall()). |
| 2920 | `private final Map<String, Double> backstopInBanThisMonth` | The overdraft the backstop closed this month in a sector already inside its ban, which nobody would make an interim loan to: forgiven, nothing new written off (restructure(sector, true)). |
| 3133 | `private final Map<String, Double> loansDefaultedThisMonth` | What defaulted this month in each class, per sector (0.7.12): the loans' and the bonds' share of the slice, or all of both in the backstop - before what was recovered, so recovered over defaulted is each class's recov... |
| 3134 | `private final Map<String, Double> bondsDefaultedThisMonth` |  |
| 3141 | `private final Map<String, Double> bondWrittenOffThisMonth` | The bondholders' side of the month's defaults (0.7.12), per sector: the face written off its bonds, every holder together. |
| 3142 | `private final Map<String, Double> bondWrittenOffTotal` |  |
| 3181 | `private final Map<String, Double> defaultedThisMonth` | The month's defaults, per sector, for the notice and the playtest - struck by restructureInsolventSectors() and read in the same month (Inbox.takeMonth()), so not saved: the bank's own record of the month's write-off ... |
| 3182 | `private final Map<String, Double> defaultShareThisMonth` |  |
| 3183 | `private final java.util.Set<String> restructuredThisMonth` |  |
| 3229 | `private final Map<String, Double> principalJudged` | What each sector owed when the month's insolvency check judged it, against the assets it judged it on (getAssets(), struck at the same check). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 3300 | **type** `public class BusinessDebtManager` | Private-sector credit. |
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

### AND ITS BONDS (0.7.12) (lines 541-1410)

| line | len | member | says |
|---:|---:|---|---|
| 563 | 8 | **type** `public interface BondBook` | What the bond market tells the lender about each sector's bonds. |
| 565 | 1 | `double principal(String sector)` _(in BusinessDebtManager.BondBook)_ | The face a sector owes on its bonds. |
| 567 | 1 | `double monthlyCoupon(String sector)` _(in BusinessDebtManager.BondBook)_ | The coupons its bonds ask this month, on the face outstanding. |
| 569 | 1 | `double writeDown(String sector, double scale)` _(in BusinessDebtManager.BondBook)_ | Writes a sector's bonds down to this share of their face, every holder pro rata. |
| 573 | 18 | **type** `public interface BondDesk` | ...and where the desks ask whether a bond would be cheaper than the bank (BondMarket, WHO ISSUES, AND WHEN). |
| 586 | 2 | `Plan plan(String sector, double amount, double loanRate, double loanRoom, double bondRoom, double extraAssets, int month)` _(in BusinessDebtManager.BondDesk)_ | How to finance `amount` for this sector: a bond, the bank, or both. |
| 589 | 1 | `double issue(Plan plan, int month)` _(in BusinessDebtManager.BondDesk)_ | Issues a plan's bond. |
| 607 | 18 | **type** `public record Plan(String sector, double amount, double bondFace, double coupon, double allIn, double loan,...` | How one borrowing is financed (0.7.12): of `amount`, `bondFace` in a bond at `coupon` - `allIn` with its issuing costs spread over its life - and `loan` from the bank at `loanRate`, `loanAllIn` with its fee spread ove... |
| 611 | 3 | `public static Plan bankOnly(String sector, double amount, double loanRate, double loanAllIn, double clearing)` _(in BusinessDebtManager.Plan)_ | All of it from the bank. |
| 615 | 1 | `public boolean hasBond()` _(in BusinessDebtManager.Plan)_ | True when some of it is a bond. |
| 617 | 3 | `public double monthlyInterest()` _(in BusinessDebtManager.Plan)_ | The interest a month the plan costs, both parts: what the project's own test is asked to carry, each at its instrument's rate. |
| 621 | 1 | `public double blendedRate()` _(in BusinessDebtManager.Plan)_ | ...as an annual rate on what it raises. |
| 623 | 1 | `public boolean covers()` _(in BusinessDebtManager.Plan)_ | True when it raises the whole amount. |
| 630 | 4 | `public void setBondMarket(BondBook book, BondDesk desk)` | The bond market, wired by Game; null in a harness that builds this class alone. |
| 635 | 1 | `public BondDesk getBondDesk()` |  |
| 641 | 4 | `public double takeBondProceeds(String sector)` | The bonds the shortfall desk issued for a sector this month, net of their costs, handed over once. |
| 648 | 1 | `public Plan getShortfallPlan(String sector)` |  |
| 719 | 3 | `public static double feeOn(double principal)` | What a loan of this principal pays up front: Bank.LOAN_FEE of it. |
| 723 | 1 | `public double getFeesThisMonth()` |  |
| 724 | 1 | `public double getFeesThisMonth(String sector)` |  |
| 739 | 1 | `public double getLentThisMonth()` |  |
| 740 | 1 | `public double getRepaidThisMonth()` |  |
| 742 | 3 | `public double getLentThisMonth(String sector)` |  |
| 746 | 3 | `public double getRepaidThisMonth(String sector)` |  |
| 750 | 18 | `public void startAuditMonth()` |  |
| 769 | 7 | `public BusinessDebtManager()` |  |
| 785 | 3 | `public void setPrimeRate(double rate)` | Prime, pushed in by Game each month from Bank.prime() before anything is priced (0.7.7; the city's rate before it). |
| 790 | 3 | `public void setInsuredMortgageRate(double rate)` | The insured mortgage's rate, pushed in by Game with prime (Bank.insuredMortgageRate()). |
| 795 | 3 | `public double getInsuredMortgageRate()` | What a new insured mortgage is written at this month, and what a term that ends renews at. |
| 800 | 3 | `public void setAssets(String sector, double totalAssets)` | Total assets from that sector's balance sheet - the denominator of leverage. |
| 814 | 4 | `public void setConcentrationCharges(Map<String, Double> charges)` |  |
| 820 | 1 | `public double getConcentrationCharge(String sector)` | What a loan to this sector pays for the book's concentration, a year: part of its rate. |
| 823 | 5 | `public void updateRates()` | pricing |
| 830 | 3 | `private double priceSector(String sector)` | The quote: the curve at the leverage the sector's last quarter of statements reads (getQuarterLeverage()), and the whole curve against no assets (pricingLeverage()). |
| 854 | 40 | `private double priceSector(String sector, double extraPrincipal, double extraAssets)` | ratio. |
| 897 | 3 | `public double getRate(String sector)` | What NEW borrowing costs this sector today. |
| 902 | 3 | `public double getSpread(String sector)` | What this sector pays over prime: its own expected loss and record. |
| 907 | 3 | `public double getRiskSpread(String sector)` | ...the first part of it: its own expected loss over the book's, at the leverage its last quarter reads (expectedLossSpread(), quarterPrincipal() over quarterAssets()) and a loan's loss given default. |
| 912 | 3 | `public double getRecordSurcharge(String sector)` | ...and the second: DEFAULT_SURCHARGE a write-down on its record, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 916 | 3 | `private double recordSurcharge(String sector)` |  |
| 927 | 3 | `public double projectRate(String sector, double amount)` | WHAT A PROJECT LOAN OF THIS SIZE WOULD BE WRITTEN AT (0.7.8): the curve at the leverage it leaves the sector at, the building counted at the loan's value - the rate issueProjectLoan() writes, and the one Game.consider... |
| 936 | 4 | `public double leverageAfterProject(String sector, double amount)` | ...and the leverage that loan is priced at: the last quarter's statements with the deal on top, the building counted. |
| 942 | 3 | `public double getPrimeRate()` | Prime, as the bank set it this month. |
| 946 | 4 | `public double getLeverage(String sector)` |  |
| 951 | 4 | `public void setCash(String sector, double cash)` |  |
| 966 | 3 | `public double getCash(String sector)` |  |
| 970 | 3 | `private double getOverdraftForgivenPending(String sector)` |  |
| 975 | 5 | `public double takeOverdraftForgiven(String sector)` | The overdraft a restructure forgave this month, handed over once. |
| 981 | 3 | `public double getAssets(String sector)` |  |
| 986 | 5 | `public double getAllPrincipal()` | Everything every sector owes the bank. |
| 993 | 3 | `public double getPrincipal(String sector)` | What a sector owes: its bank loans and, since 0.7.12, its bonds - see AND ITS BONDS. |
| 998 | 9 | `public double getLoanPrincipal(String sector)` | What a sector owes the bank: its loans and its mortgages. |
| 1009 | 3 | `public double getBondPrincipal(String sector)` | ...and what it owes on its bonds (0.7.12): nothing without a bond market. |
| 1014 | 5 | `public double getLoanShare(String sector)` | The bank loans' share of what a sector owes: 1 with no bonds, and with no debt at all. |
| 1020 | 7 | `public double getTotalPrincipal()` |  |
| 1029 | 3 | `public double getMonthlyInterest(String sector)` | This month's interest cost for a sector - the income statement's expense line: its loans' interest and, since 0.7.12, its bonds' coupons. |
| 1034 | 9 | `public double getLoanInterest(String sector)` | ...the part the bank is paid on its loans. |
| 1045 | 5 | `public double getTotalMonthlyInterest()` | Every sector's interest bill, loans and bonds. |
| 1056 | 4 | `public double getEffectiveRate(String sector)` | Blended annual rate actually being paid on existing debt, as opposed to getRate() which is what the next loan would cost. |
| 1061 | 9 | `public int getLoanCount(String sector)` |  |
| 1071 | 3 | `public List<BusinessDebt> getLoans()` |  |
| 1075 | 9 | `public List<BusinessDebt> getLoans(String sector)` |  |
| 1093 | 80 | `public void processMonth()` | Advances every loan and retires the ones that mature. |
| 1175 | 7 | `public double takeMaturedPrincipal(String sector)` | Reads and clears the principal that fell due this month for one sector. |
| 1193 | 182 | `public double coverShortfall(String sector, double cash, double monthlyLoss, int month)` | Underwrites a loan if the sector is short, and returns the proceeds - and since 0.7.12 sells a bond in part of its place where one is cheaper, whose proceeds wait for the settle (takeBondProceeds()). |
| 1385 | 5 | `private void countOpenLine(String sector, double lent, double oldRule)` |  |
| 1392 | 1 | `public boolean capitalRuleOn()` | True this month when the bank's capital rule limits new lending: under its target, or under its minimum. |
| 1395 | 1 | `public double getLineLentRationed(String sector)` | What the working-capital line lent this sector this month while the capital rule was on (round 5). |
| 1397 | 1 | `public double getLineLentPastOldRule(String sector)` | ...of it, what the 0.7.8 rule would have refused. |
| 1409 | 1 | `public double getShortfallLentThisMonth(String sector)` | ...one sector's. |

### ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) (lines 1411-1604)

| line | len | member | says |
|---:|---:|---|---|
| 1484 | 3 | `public double assetsNow(String sector, double cash)` | A sector's assets on the sheet as it stands, its till at `cash`: the month's refresh (getAssets()), with the till it read (getCash()) moved to this one. |
| 1497 | 7 | `public boolean pastDefaultPoint(String sector, double cash)` | Past the default point ON ITS QUARTER (round 3): owing more than INSOLVENCY_TRIGGER times what it owned, averaged over its last STATEMENT_MONTHS month-end readings (quarterPrincipal(), quarterAssets()) - or anything, ... |
| 1506 | 8 | `public boolean pastDefaultPoint(String sector, double cash, double amount, double handed)` | ...or past it once lent `amount`, `handed` of it paid into the till: before the loan or after it, on the same reading. |
| 1516 | 4 | `private double[] defaultPointReading(String sector, double cash)` | {owed, owned} as the default point reads them: the quarter's averages, or with no reading the sheet as it stands, its till at `cash` (round 3). |
| 1522 | 4 | `public double defaultPointLeverage(String sector)` | The leverage the default point reads a sector at now: its quarter's, or as it stands with no reading - for the investor's words and the screens. |
| 1533 | 1 | `public double getRefusedAtDefaultPoint(String sector)` | What the shortfall desk would have lent this sector this month and did not, because it is past the default point (0.7.12, round 2). |
| 1536 | 5 | `public double getRefusedAtDefaultPoint()` | ...every sector's. |
| 1543 | 1 | `public double getNotRenewedAtDefaultPoint(String sector)` | The mortgage balances that fell due whole this month rather than renew, because the landlords were past the default point. |
| 1546 | 1 | `public boolean wasRefusedAtDefaultPoint(String sector)` | True when the investment desk turned this sector's project down this month because it is past the default point. |
| 1554 | 5 | `public double bondCeilingRoom(String sector, double multiple)` | THE CEILING A BOND IS HELD TO (0.7.12): the line at this multiple of its assets, and the ban, as for a loan. |
| 1566 | 6 | `public double projectLoanRoom(String sector, double amount)` | WHAT THE BANK WILL LEND OF A PROJECT (0.7.12): the whole of it if canFundProject() says yes, and otherwise what its capital rule leaves under the default point after the deal - the most a bond and a loan may raise of ... |
| 1574 | 6 | `public double projectBondRoom(String sector, double amount)` | ...and the bond's own ceiling: the same default point after the deal, and the ban. |
| 1601 | 3 | `public double borrowingRoom(String sector)` | How much more this sector may borrow today, from either desk. |

### ...AND WHAT THE NEXT SETTLE WOULD DO, ASKED AHEAD (0.7.12, round 6) (lines 1605-1725)

| line | len | member | says |
|---:|---:|---|---|
| 1622 | 20 | `public double principalDueNextMonth(String sector)` | The principal that falls due at the next settle, as processMonth() will park it: each loan that matures, whole; each mortgage's next payment's principal, and its whole balance where the payment ends the term and the l... |
| 1654 | 12 | `public double workingCapitalLine(String sector, double cash, double due)` | What the working-capital line would hand this sector's till at the next settle, net of its fee: coverShortfall()'s room on coverShortfall()'s rules - the ceiling at MAX_LOAN_TO_ASSETS on what it will owe once what fal... |
| 1694 | 23 | `public boolean canFundProject(String sector, double amount)` | Whether the INVESTMENT desk will fund a project of this size. |
| 1719 | 6 | `private double ceilingRoom(String sector, double multiple)` | The ceiling alone: nothing while the lender is shut or the sector barred. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 1726-2108)

| line | len | member | says |
|---:|---:|---|---|
| 1837 | 26 | `public boolean canFundMortgage(String sector, double shortfall, double cost)` | Whether the lender will write a mortgage for this shortfall on a building of this cost: open, the sector not barred, the loan no more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost, and the borrower not past the ... |
| 1865 | 1 | `public String getMortgageRefusal()` | Why canFundMortgage() last said no, or null if it said yes. |
| 1874 | 20 | `public Mortgage issueMortgage(String sector, double shortfall, int month)` | WRITES A MORTGAGE for this shortfall: the loan that covers it once the fee is paid (Mortgage.loanFor()), the premium added, at the insured rate. |
| 1896 | 1 | `public double getPremiumsThisMonth()` | The premiums on the mortgages written this month: the treasury's revenue line. |
| 1898 | 1 | `public double getPremiumsThisMonth(String sector)` | ...by sector, which its cash flow statement takes off what it was handed. |
| 1900 | 1 | `public double getPremiumsTotal()` | The premiums written over the city's life. |
| 1903 | 1 | `public int getMortgagesWrittenThisMonth()` | The mortgages written this month, renewed this month, and fallen due this month because the lender could not renew them. |
| 1904 | 1 | `public int getRenewedThisMonth()` |  |
| 1905 | 1 | `public int getFallenDueThisMonth()` |  |
| 1907 | 1 | `public int getRenewedLifetime()` | ...over the run, for the playtest; not saved. |
| 1908 | 1 | `public int getFallenDueLifetime()` |  |
| 1911 | 7 | `public List<Mortgage> getMortgages(String sector)` | Every mortgage one sector owes, in the order written. |
| 1920 | 5 | `public List<Mortgage> getMortgages()` | Every mortgage in the city. |
| 1927 | 1 | `public int getMortgageCount(String sector)` | How many mortgages one sector owes. |
| 1930 | 5 | `public double getMortgagePrincipal(String sector)` | What one sector owes on its mortgages. |
| 1937 | 5 | `public double getMortgagePrincipal()` | What every sector owes on mortgages: the bank's mortgage book. |
| 1944 | 5 | `public double getInsuredPrincipal(String sector)` | What one sector owes on its insured mortgages - the part of its debt the city insures. |
| 1951 | 5 | `public double getInsuredPrincipal()` | ...every sector's: what the bank's book holds at Bank.RISK_INSURED_MORTGAGE. |
| 1958 | 3 | `public double getUninsuredPrincipal(String sector)` | What one sector owes the bank that nobody insures: its loans less its insured mortgages - what the bank's allowance reads, and its capital rule while the leverage ratio does not bind (rationedPrincipal()). |
| 1963 | 5 | `public double getMortgagePayment(String sector)` | The level payments one sector's mortgages ask next month: interest and principal together. |
| 1970 | 5 | `public double getMortgagePayment()` | ...every sector's. |
| 1977 | 3 | `public double getMortgageRate(String sector)` | The rate one sector's mortgages carry, weighted by what is owed on each; 0 with none. |
| 1982 | 3 | `public double getMortgageRate()` | ...every mortgage's. |
| 1986 | 8 | `private static double weightedRate(List<Mortgage> ms)` |  |
| 1996 | 8 | `public int getNextRenewalMonth(String sector)` | The first month one of this sector's mortgages renews, or -1 with none. |
| 2006 | 5 | `public int getMortgagesRenewingWithin(int months)` | How many mortgages renew within this many months of this one. |
| 2013 | 4 | `public boolean allMortgagesInsured()` | True when every mortgage in the city is insured - which every one this build writes is. |
| 2019 | 1 | `public double getMortgageRepaidThisMonth(String sector)` | The principal one sector's mortgage payments took this month. |
| 2022 | 5 | `public double getMortgageRepaidThisMonth()` | ...every sector's. |
| 2029 | 1 | `public Map<String, Double> getMortgageRepaidToSave()` | The month's principal repaid on mortgages, for the save: a copy, by sector name. |
| 2032 | 7 | `public void restoreMortgageRepaid(Map<String, Double> saved)` | ...and back on load. |
| 2041 | 1 | `public double getInsuredWrittenOffThisMonth(String sector)` | What this month's write-downs took off one sector's insured mortgages: the claim the treasury pays the bank. |
| 2044 | 5 | `public double getInsuredWrittenOffThisMonth()` | ...every sector's: the month's claims. |
| 2051 | 1 | `public double getInsuredWrittenOffTotal(String sector)` | What write-downs have taken off one sector's insured mortgages over the city's life. |
| 2054 | 5 | `public double getInsuredWrittenOffTotal()` | ...every sector's: the claims over the city's life. |
| 2061 | 1 | `public double getPremiumsTotalToSave()` | The insurance book's record, for the save: the premiums over the city's life. |
| 2064 | 1 | `public Map<String, Double> getInsuredWrittenOffTotals()` | The claims over the city's life, by sector, for the save. |
| 2067 | 8 | `public void restoreInsuranceRecord(double premiums, Map<String, Double> claims)` | ...and both back on load. |
| 2081 | 3 | `private double writeDownSector(String sector, double scale)` | Writes every instrument of one sector down to this share, pro rata, and says what came off its insured mortgages - the claim. |
| 2086 | 5 | `private void writeDownInterim(String sector, double scale)` | One sector's interim loans alone, to this share of what they were. |
| 2093 | 15 | `private double writeDownSector(String sector, double scale, boolean interimToo)` | ...its interim loans with the rest, or (false) every loan but them - they rank first (INTERIM FINANCING). |

### ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) (lines 2109-2257)

| line | len | member | says |
|---:|---:|---|---|
| 2196 | 3 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | The bank's capital rule for the month, from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(). |
| 2206 | 14 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly, boolean insuredToo)` | ...and whether it rations the insured mortgages too: true when the bank's leverage requirement is the larger (Bank.leverageBinds(), 0.7.11 round 2), because a mortgage then uses the capital the bank is short of. |
| 2224 | 1 | `public boolean isInsuredRationed()` |  |
| 2227 | 3 | `private double rationedPrincipal(String sector)` | The debt the capital rule reads: what the sector owes the bank, uninsured, or all of it while insuredRationed. |
| 2237 | 6 | `public double capitalRoom(String sector)` | What the bank's capital lets this sector borrow this month, over what it owes now: its debt when the rule was set, grown by the month's limit (none under the minimum), less what it owes - so what matured is room to re... |
| 2245 | 1 | `public double getCapitalGrowth()` | The month's limit on a borrower's growth, a share a month: infinite with none. |
| 2248 | 1 | `public boolean isKeepGoingOnly()` | True when the bank lends only to keep its borrowers going this month. |
| 2251 | 1 | `public boolean wasRefusedForCapital(String sector)` | True when the capital rule refused this sector a project this month. |
| 2254 | 1 | `public boolean wasProjectRefusedForCapital(String sector)` | ...a building's loan, this month (round 5, the growth doors counted apart). |
| 2256 | 1 | `public boolean wasMortgageRefusedForCapital(String sector)` | ...a new mortgage, this month. |

### THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) (lines 2258-2459)

| line | len | member | says |
|---:|---:|---|---|
| 2334 | 11 | `public void recordStatement(String sector, double owed, double owned)` | One month-end reading of a sector: what it owed and what it owned, as the bank read them (Game.sectorPositions()). |
| 2347 | 7 | `public double quarterPrincipal(String sector)` | What the sector owed, averaged over its last quarter of readings - what it owes now, with none. |
| 2356 | 7 | `public double quarterAssets(String sector)` | ...and what it owned. |
| 2365 | 4 | `public double getQuarterLeverage(String sector)` | The leverage the bank reads the sector at: its quarter's average debt over its average assets, 0 with no assets. |
| 2371 | 3 | `public double getQuarterDefaultRate(String sector)` | The sector's default rate a year at the leverage its last quarter reads (getQuarterLeverage()) - the reading its price is struck on (getRiskSpread()), which the screens print beside that price. |
| 2376 | 4 | `public int getStatementCount(String sector)` | How many readings a sector has, up to STATEMENT_MONTHS. |
| 2382 | 5 | `public Map<String, double[]> getStatementsToSave()` | The readings, for the save: a copy, by sector name. |
| 2389 | 10 | `public void restoreStatements(Map<String, double[]> saved)` | ...and back on load. |
| 2401 | 3 | `public void setLendingOpen(boolean open)` | Game tells the lender each month whether the bank behind it is standing. |
| 2405 | 3 | `public boolean isLendingOpen()` |  |
| 2415 | 3 | `public BusinessLoan issueLoan(String sector, double faceValue, int month)` | Writes a loan of this principal - a shortfall loan, priced at what the sector will owe over the assets it has. |
| 2420 | 3 | `public BusinessLoan issueProjectLoan(String sector, double faceValue, int month)` | ...a project's: priced with the building it buys counted in the assets, at the loan's value (projectRate()). |
| 2431 | 1 | **type** `public record Written(String sector, double amount, double leverage, double rate, boolean project)` | One loan written this month: to whom, how much, the leverage it left the borrower at, the rate it was written at, and whether it bought a building (0.7.8, for the playtest's count of loans written past the watch line ... |
| 2436 | 1 | `public List<Written> getWrittenThisMonth()` | Every loan written this month, in the order written. |
| 2438 | 21 | `private BusinessLoan write(String sector, double faceValue, int month, double extraAssets, boolean project)` |  |

### insolvency (lines 2460-2731)

| line | len | member | says |
|---:|---:|---|---|
| 2462 | 3 | `public boolean isBorrowingBlocked(String sector)` |  |
| 2466 | 3 | `public int getBlockedMonths(String sector)` |  |
| 2470 | 3 | `public double getWrittenOffThisMonth(String sector)` |  |
| 2474 | 3 | `public double getWrittenOffTotal(String sector)` |  |
| 2478 | 3 | `public int getRestructureCount(String sector)` |  |
| 2490 | 9 | `public void restoreWriteOffs(java.util.Map<String, Double> totals)` | Puts the write-off history back on load. |
| 2500 | 3 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2527 | 3 | `public java.util.Map<String, Integer> getRestructureCounts()` | THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED. |
| 2531 | 3 | `public java.util.Map<String, Integer> getBlockedMonthsAll()` |  |
| 2535 | 19 | `public void restoreCreditRecord(java.util.Map<String, Integer> counts, java.util.Map<String, Integer> blocked)` |  |
| 2555 | 7 | `public double getTotalWrittenOff()` |  |
| 2579 | 4 | `public boolean isInsolvent(String sector)` | Is every firm in this sector under water at once - the backstop's case? |
| 2599 | 3 | `public double restructure(String sector)` | THE BACKSTOP: writes a sector with nothing left down to what its assets can support - RESTRUCTURE_TARGET of nothing - forgives its overdraft, counts the default on its record and shuts it out for exclusionFor(). |
| 2612 | 119 | `public double restructure(String sector, boolean forced)` | ...or, `forced`, a sector nobody would make an interim loan to - still past the default point after the month's write-down, or its bank shut - whatever its assets read (INTERIM FINANCING, round 5): the whole sector to... |

### CAN'T PAY MEANS DEFAULT (0.7.12, round 4) (lines 2732-2829)

| line | len | member | says |
|---:|---:|---|---|
| 2790 | 1 | **type** `public enum ShortReason` | Why the shortfall desk left a sector short (round 4). |
| 2797 | 1 | `public ShortReason getShortReason(String sector)` | Why the shortfall desk left this sector short this month - the tighter of its limits, or its reason to refuse - or null if it was not asked. |
| 2800 | 3 | `public void setMonthObligations(String sector, double amount)` | What the month asked this sector to pay: its costs, interest and taxes on this month's statement and the principal that fell due (EconomyManager.settleBusinessCredit()). |
| 2803 | 1 | `public double getMonthObligations(String sector)` |  |
| 2811 | 1 | `public double getCannotPayShort(String sector)` | What the month's bills still left unpaid in one sector when the cash-flow test struck (round 4) - lent as interim financing since round 5, or the backstop's; 0 if it did not default for want of cash. |
| 2813 | 1 | `public double getCannotPayShare(String sector)` | ...the share of it that could not pay, h. |
| 2815 | 1 | `public ShortReason getCannotPayReason(String sector)` | ...and why no lender stood behind it, or null. |
| 2823 | 6 | `double cannotPayShare(String sector)` | THE CASH-FLOW TEST'S SHARE: the part of a sector whose till is short that cannot pay - its shortfall over what the month asked it to pay, all of it when that is more than the month's bills. |

### INTERIM FINANCING (0.7.12, round 5) (lines 2830-3358)

| line | len | member | says |
|---:|---:|---|---|
| 2911 | 1 | `public double getInterimLentThisMonth(String sector)` | What the interim lender lent this sector this month, at face (round 5). |
| 2913 | 1 | `public String getInterimRefusal(String sector)` | Why the interim lender would not lend to this sector this month, or null. |
| 2915 | 1 | `public double getInterimMaturedThisMonth(String sector)` | The interim principal that fell due this month in this sector: repaid, or rolled by the shortfall desk. |
| 2917 | 1 | `public double getInterimWrittenOffThisMonth(String sector)` | The interim principal a backstop wrote off this month in this sector. |
| 2922 | 1 | `public double getBackstopInBanThisMonth(String sector)` | ...one sector's. |
| 2925 | 4 | `public double takeInterimHanded(String sector)` | The interim loan's cash this sector's till is owed from the month's defaults, handed over once (EconomyManager.settleInsolvency()). |
| 2931 | 5 | `public double getInterimPrincipal(String sector)` | What one sector owes on interim financing. |
| 2938 | 5 | `public double getInterimPrincipal()` | ...every sector's. |
| 2945 | 5 | `public int getInterimCount(String sector)` | How many interim loans one sector has outstanding. |
| 2952 | 5 | `public int getInterimCount()` | ...every sector's. |
| 2965 | 10 | `String interimRefusal(String sector, double cash, double loan, double handed, double writtenDown)` | THE INTERIM LENDER'S TEST: null to lend, or why not. |
| 2977 | 7 | `double priceInterim(String sector, double faceValue)` | What an interim loan of this face would be written at: prime, the curve at the leverage its rank sees, the record and the concentration (INTERIM FINANCING). |
| 2986 | 15 | `private InterimLoan writeInterim(String sector, double faceValue)` | Writes an interim loan: counted in the month's lending and fees like any loan, the bank paying it out at the settle. |
| 3013 | 57 | `public double restructureInsolventSectors()` | THE MONTH'S DEFAULTS, every sector: the backstop for a sector with nothing left (restructure()), and for every other the slice of its debt whose firms fell through the default point (defaultSlice()) - or, since round ... |
| 3086 | 3 | `public double defaultSlice(String sector)` | THE SLICE: the share of this sector's debt whose firms fell through the default point this month, monthlyDefaultShare() of it at the leverage the month's check reads (principal over getAssets(), struck after the balan... |
| 3091 | 40 | `public double defaultSlice(String sector, double atLeast)` | ...or, if more, this share: the part of it that cannot pay this month (round 4, CAN'T PAY MEANS DEFAULT). |
| 3137 | 1 | `public double getLoansDefaultedThisMonth(String sector)` | The loans and the bonds that defaulted this month in one sector, before recovery (0.7.12). |
| 3138 | 1 | `public double getBondsDefaultedThisMonth(String sector)` |  |
| 3144 | 5 | `private void recordBondWriteOff(String sector, double face)` |  |
| 3151 | 1 | `public double getBondWrittenOffThisMonth(String sector)` | What this month's defaults took off one sector's bonds, every holder together (0.7.12). |
| 3154 | 1 | `public double getBondWrittenOffTotal(String sector)` | ...over the city's life. |
| 3157 | 5 | `public double getBondWrittenOffTotal()` | ...every sector's, over the city's life. |
| 3164 | 1 | `public Map<String, Double> getBondWrittenOffTotals()` | The bondholders' losses over the city's life, by sector, for the save. |
| 3167 | 7 | `public void restoreBondWrittenOff(Map<String, Double> totals)` | ...and back on load; an older save has none. |
| 3186 | 1 | `public double getDefaultedThisMonth(String sector)` | The debt whose firms defaulted this month in the slice, before what its creditors recover - each instrument's write-off is its own loss given default of its part (getLoansDefaultedThisMonth(), getBondsDefaultedThisMon... |
| 3189 | 1 | `public double getDefaultShareThisMonth(String sector)` | The share of the sector's debt that defaulted this month in the slice, h. |
| 3192 | 1 | `public boolean wasRestructuredThisMonth(String sector)` | True when the backstop wrote this sector down whole this month. |
| 3195 | 3 | `public double getDefaultRate(String sector)` | The sector's default rate a year at its leverage now, PD(L) - what the Bank tab shows beside its leverage. |
| 3215 | 4 | `public boolean defaultsAreNews(String sector)` | WHETHER THIS MONTH'S DEFAULTS ARE NEWS: the backstop, or a slice at least the share that defaults a month at the default point itself - monthlyDefaultShare(INSOLVENCY_TRIGGER), 5.6% of its debt, where half the sector'... |
| 3231 | 4 | `public double getPrincipalJudged(String sector)` |  |
| 3237 | 8 | `public void advanceBlocks()` | Counts down the borrowing bans. |
| 3247 | 3 | `public void setLoans(List<BusinessDebt> loans)` | save / load |
| 3251 | 33 | `public void clearLoans()` |  |
| 3286 | 29 | `public void printBusinessDebtInfo(int currentMonth)` | printers |
| 3318 | 4 | `static { ... }` |  |
| 3324 | 33 | `public void redenominate(double scale)` | Every business loan and this month's lending, in the new unit. |

