# BusinessDebtManager.java - 3,852 lines · 255 methods · 29 constants · model

`ham/citybuildersim/BusinessDebtManager.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

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

**Uses:** [Mortgage](Mortgage.md) (39), [BusinessDebt](BusinessDebt.md) (32), [InterimLoan](InterimLoan.md) (14), [Bank](Bank.md) (10), [BankRules](BankRules.md) (9), [BusinessLoan](BusinessLoan.md) (5), [Currency](Currency.md) (5), [Sectors](Sectors.md) (4)

**Used by (46):** [AuctionCheck](AuctionCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BankRules](BankRules.md), [BankRulesCheck](BankRulesCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BooksCheck](BooksCheck.md), [BuildCardCheck](BuildCardCheck.md), [CardCheck](CardCheck.md), [CellSpread](CellSpread.md), [CreditCheck](CreditCheck.md), [CreditCrunchCheck](CreditCrunchCheck.md), [DistressBleedCheck](DistressBleedCheck.md), [EconomyManager](EconomyManager.md), [EquipmentFit](EquipmentFit.md), [FinancesScreen](FinancesScreen.md), [FittingCheck](FittingCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HoldersCheck](HoldersCheck.md), [Inbox](Inbox.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LongPlaytest](LongPlaytest.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OrderDoorCheck](OrderDoorCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [SectorStatements](SectorStatements.md), [SpreadPlanner](SpreadPlanner.md), [SupplierCreditCheck](SupplierCreditCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [YoungBankCheck](YoungBankCheck.md)

## Sections

| line | section |
|---:|---|
| 85 | PRICING FROM THE CURVE (0.7.8) |
| 153 | INSOLVENCY |
| 246 | A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 336 | RECOVERIES BY INSTRUMENT (0.7.12, round 2) |
| 545 | AND ITS BONDS (0.7.12) |
| 1199 | WHAT IT OWES, BY KIND AND BY WHEN (0.7.74, the sector statements' |
| 1287 | WHAT MOVED IT, BY KIND (0.7.75, the sector statements' R7) |
| 1576 | · ...AND NOBODY LENDS PAST THE CEILING |
| 1766 | ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) |
| 1960 | ...AND WHAT THE NEXT SETTLE WOULD DO, ASKED AHEAD (0.7.12, round 6) |
| 2081 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 2511 | ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) |
| 2720 | THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) |
| 2923 | · insolvency |
| 3202 | CAN'T PAY MEANS DEFAULT (0.7.12, round 4) |
| 3300 | INTERIM FINANCING (0.7.12, round 5) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3260 | `BusinessDebtManager.ShortReason.PAST_DEFAULT_POINT` |  |
| 3260 | `BusinessDebtManager.ShortReason.BANNED` |  |
| 3260 | `BusinessDebtManager.ShortReason.BANK_SHUT` |  |
| 3260 | `BusinessDebtManager.ShortReason.CEILING` |  |
| 3260 | `BusinessDebtManager.ShortReason.AFTER_SETTLE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 184 | `BusinessDebtManager.INSOLVENCY_TRIGGER` | `1.5` | The default point: a firm owing more than this multiple of its assets is not getting repaid, and both sides know it. |
| 217 | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | `0.9` | The most a lender will advance against a borrower's assets. |
| 244 | `BusinessDebtManager.RESTRUCTURE_TARGET` | `.6` | What the backstop leaves a restructured sector owing, as a multiple of its assets. |
| 331 | `BusinessDebtManager.ASSET_VOLATILITY` | `.25` | The one-year volatility of a firm's assets: sigma in PD(L), the spread of fortunes among the firms inside a sector. |
| 334 | `BusinessDebtManager.DEFAULT_HORIZON_MONTHS` | `12` | The horizon a default probability is quoted over, in months: one year, the convention of KMV's EDF and of every rating agency's default rate. |
| 381 | `BusinessDebtManager.LOAN_RECOVERY` | `.75` | What a bank loan gives back of each dollar that defaults: 75%, the midpoint of the 70-80% that first-lien senior secured bank loans recovered on average in Moody's Ultimate Recovery Database and S&P's recovery studies... |
| 384 | `BusinessDebtManager.BOND_RECOVERY` | `.45` | What a bond gives back of each dollar that defaults: 45%, the midpoint of the 40-50% that senior unsecured bonds recovered on average in the same Moody's and S&P data, 1987-2024. |
| 387 | `BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | `1 - LOAN_RECOVERY` | What the bank loses on a dollar of a sector's loans that defaults: 1 - LOAN_RECOVERY, derived. |
| 390 | `BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT` | `1 - BOND_RECOVERY` | ...and what a bondholder loses on a dollar of its bonds: 1 - BOND_RECOVERY, derived. |
| 481 | `BusinessDebtManager.BORROWING_BLOCKED_MONTHS` | `12` | Months a sector cannot borrow after being restructured. |
| 526 | `BusinessDebtManager.DEFAULT_SURCHARGE` | `.01` | Extra annual interest per prior write-down, on top of the borrower's expected loss, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 528 | `BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT` | `3` | The most write-downs DEFAULT_SURCHARGE is charged for: a record adds three points at the most. |
| 531 | `BusinessDebtManager.LOAN_TERM_MONTHS` | `36` | How long a business loan runs, interest only, before its principal is due: three years, and it keeps the rate it was written at for all of them. |
| 541 | `BusinessDebtManager.BUFFER_MONTHS` | `3` | Borrow enough to cover the hole plus this many months of the current loss. |
| 1146 | `BusinessDebtManager.KeyAt.AT` |  |  |
| 1212 | `BusinessDebtManager.DEBT_KINDS` | `{ "Bank loans", "Bonds", "Mortgages", "Interim financing" }` | The four kinds of debt, in the order the statements print them. |
| 1230 | `BusinessDebtManager.DUE_HORIZONS` | `{ 12, 60 }` | The two horizons R2 reads, in settles: a year and five. |
| 1233 | `BusinessDebtManager.OWED` | `0, WITHIN_YEAR = 1, WITHIN_FIVE = 2, RATE = 3, RUNS_TO = 4` | debtByKind()'s rows: what is owed, what falls due within a year and within five, the rate it pays a year weighted by what is owed, and the month the last of it falls due (0 with none) - the last two since 0.7.75. |
| 1305 | `BusinessDebtManager.BORROWED` | `0, REPAID = 1, WRITTEN_OFF = 2` | debtMovedByKind()'s rows: borrowed (the principal written, a bond's face), repaid, written off. |
| 2168 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 2170 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 2172 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 2174 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 2176 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 2178 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 2773 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 3367 | `BusinessDebtManager.INTERIM_PAST_LINE` | `"past the default point after the write-down"` | Why the interim lender would not lend: the sector still past the default point after the write-down. |
| 3369 | `BusinessDebtManager.INTERIM_BANK_SHUT` | `"the bank is shut"` | ...or the bank that would lend it has failed or is frozen in resolution. |
| 3802 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `private String[] SECTORS` | Every set of books that can borrow, by name, in the registry's order. |
| 219 | `private BankRules rules` |  |
| 543 | `private List<BusinessDebt> loans` |  |
| 639 | `private BondBook bondBook` |  |
| 640 | `private BondDesk bondDesk` |  |
| 651 | `private final Map<String, Double> bondProceeds` | The proceeds of the bonds the shortfall desk issued this month, per sector, until the credit settle hands them over (takeBondProceeds()). |
| 660 | `private final Map<String, Plan> shortfallPlans` | The plan the shortfall desk struck for each sector this month, for the advisor and the playtest. |
| 664 | `private double primeRate` | Prime: the bank's rate for a sound business this month, which every sector's own spread sits on (see PRICING). |
| 667 | `private double insuredMortgageRate` | The insured mortgage's rate this month (Bank.insuredMortgageRate()), pushed in by Game beside prime: what a new mortgage is written at and a term renews at. |
| 670 | `private final Map<String, Double> writtenOffThisMonth` | Written off this month, and over the whole game, per sector. |
| 671 | `private final Map<String, Double> writtenOffTotal` |  |
| 672 | `private final Map<String, Integer> restructures` |  |
| 675 | `private final Map<String, Integer> blockedMonths` | Months of borrowing ban remaining, per sector. |
| 692 | `private boolean lendingOpen` | Whether the lender is open for business at all. |
| 694 | `private final Map<String, Double> assets` |  |
| 695 | `private final Map<String, Double> rates` |  |
| 704 | `private final Map<String, Double> cashBalance` | Each sector's cash balance, refreshed with the assets. |
| 707 | `private final Map<String, Double> overdraftForgiven` | What this month's restructures forgave, per sector, until Game collects it. |
| 710 | `private final Map<String, Double> maturedPrincipal` | Principal that fell due this month, per sector, waiting to be settled. |
| 717 | `private double lentThisMonth` | The lender's side of the month, for MoneyAudit: what it advanced and what it took back. |
| 718 | `private double repaidThisMonth` |  |
| 728 | `private double feesThisMonth` | THE LOAN FEE (0.7.7): Bank.LOAN_FEE of every loan written, paid out of its proceeds - the borrower is handed the principal less this, and the bank's cash takes it at the month's settle beside the lending (Game.nextMon... |
| 729 | `private final Map<String, Double> feesBySector` |  |
| 746 | `private final Map<String, Double> mortgageFeesBySector` | ...the part of a sector's fees that was on its mortgages (0.7.102, A16): the sector expenses what its borrowing cost it up front over each debt's life (Sector, WHAT ITS BORROWING COST IT UP FRONT) - a mortgage's over ... |
| 761 | `private final Map<String, Double> lentBySector` | THE SAME TWO FIGURES, PER SECTOR. |
| 762 | `private final Map<String, Double> repaidBySector` |  |
| 776 | `private final java.util.Set<String> closedBorrowers` | Borrowers whose last plant closed this turn, resolved after their sheets refresh (BF1). |
| 777 | `private final Map<String, Double> closedRecovery` |  |
| 902 | `private final Map<String, Double> concentrationCharges` | THE CONCENTRATION CHARGE ON EACH SECTOR'S LOANS (0.7.12): what the capital a new dollar lent to it adds for concentration costs a year, pushed in by Game from the bank beside prime (Bank .concentrationCharge(), THE BA... |
| 1046 | `private final Map<String, QuoteParts> quoteParts` | Each sector's quote in its parts, as it was last priced (priceSector()). |
| 1095 | `private final java.util.Set<String> tillReported` | The sectors whose sheet the economy has reported (setCash(), from EconomyManager.refreshCreditAssets(), every month before anything is lent): the default point reads only those (pastDefaultPoint()). |
| 1307 | `private final Map<String, double[]> moved` |  |
| 1737 | `private final Map<String, Double> lineLentRationed` | WHAT THE OPEN LINE LENT WHILE THE BANK WAS SHORT OF CAPITAL (round 5), per sector, the month's: all of it - the loan and any bond in its place - while the capital rule was on, and the part of it past what the 0.7.8 ru... |
| 1738 | `private final Map<String, Double> lineLentPastOldRule` |  |
| 1761 | `private final Map<String, Double> shortfallLent` | WHAT THE SHORTFALL DESK LENT EACH SECTOR THIS MONTH, loans and bonds at face (0.7.12 round 2): the new borrowing that rolled what fell due, which a dividend is paid after (Game.payDividends(): net income less the prin... |
| 1883 | `private final Map<String, Double> refusedAtDefaultPoint` | What the default point refused this month, per sector: what the shortfall desk would otherwise have lent, the mortgages that fell due whole rather than renew, and the projects the investment desk turned down. |
| 1884 | `private final Map<String, Double> notRenewedAtDefaultPoint` |  |
| 1885 | `private final java.util.Set<String> refusedProjectAtDefaultPoint` |  |
| 2129 | `private double premiumsThisMonth` | The premiums added to the mortgages written this month, and by sector: the treasury's revenue line. |
| 2130 | `private final Map<String, Double> premiumsBySector` |  |
| 2138 | `private final Map<String, Double> mortgageRepaidBySector` | THE PRINCIPAL THE MORTGAGES' PAYMENTS TOOK THIS MONTH, by sector - part of getRepaidThisMonth(), and the figure the Bank tab and the landlords' screen show beside the payment. |
| 2141 | `private int mortgagesWrittenThisMonth, renewedThisMonth, fallenDueThisMonth` | The month's mortgages written, renewed, and fallen due because the lender could not renew them - and the same over the run, for the playtest (not saved, a count for the run). |
| 2142 | `private int renewedLifetime, fallenDueLifetime` |  |
| 2158 | `private final Map<String, Double> insuredWrittenOffThisMonth` | WHAT THE INSURANCE PAID THIS MONTH, by sector: what the month's write-downs took off insured mortgages. |
| 2159 | `private final Map<String, Double> insuredWrittenOffTotal` |  |
| 2162 | `private double premiumsTotal` | The premiums written over the city's life. |
| 2165 | `private String mortgageRefusal` | The reason canFundMortgage() last refused, or null. |
| 2583 | `private double capitalGrowth` |  |
| 2584 | `private boolean keepGoingOnly` |  |
| 2586 | `private double projectBudget` | The bank's uninsured project allowance, shared this turn; insured mortgages keep their own rule (BF1). |
| 2587 | `private Bank projectBank` |  |
| 2588 | `private boolean projectBookSettled` |  |
| 2589 | `private double projectSettledLent, projectSettledRepaid` |  |
| 2627 | `private final Map<String, Double> principalAtRule` |  |
| 2629 | `private final java.util.Set<String> refusedForCapital` | Sectors whose project the capital rule refused this month, so the investor can say so. |
| 2631 | `private final java.util.Set<String> refusedProjectForCapital` | ...by door (round 5): a building's loan, and a new mortgage - the two GROWTH doors. |
| 2632 | `private final java.util.Set<String> refusedMortgageForCapital` |  |
| 2666 | `private boolean insuredRationed` | True this month when the capital rule rations the insured mortgages too - the bank's leverage requirement binding. |
| 2776 | `private final Map<String, double[]> statements` | Each sector's last month-end readings, oldest first: {owed, owned, owed, owned, ...}, at most STATEMENT_MONTHS pairs. |
| 2895 | `private final List<Written> writtenThisMonth` |  |
| 3263 | `private final Map<String, ShortReason> shortReason` | Why the shortfall desk left each sector short this month, and what the month asked each to pay - both from the credit settle, read at the month's defaults; the month's, not saved. |
| 3264 | `private final Map<String, Double> monthObligations` |  |
| 3276 | `private final Map<String, Double> cannotPayShort` | The month's cash-flow defaults, per sector: what was still unpaid, the share of the sector that could not pay, and why nobody lent. |
| 3277 | `private final Map<String, Double> cannotPayShareThisMonth` |  |
| 3278 | `private final Map<String, ShortReason> cannotPayReason` |  |
| 3372 | `private final Map<String, Double> interimLentThisMonth` | The month's interim financing, per sector: lent (at face), handed to the till (the overdraft it closed), refused and why, fallen due, and written off in a backstop. |
| 3373 | `private final Map<String, Double> interimHanded` |  |
| 3374 | `private final Map<String, String> interimRefused` |  |
| 3375 | `private final Map<String, Double> interimMaturedThisMonth` |  |
| 3376 | `private final Map<String, Double> interimWrittenOffThisMonth` |  |
| 3378 | `private int monthNow` | The month an interim loan is written in: the credit settle's (coverShortfall()). |
| 3390 | `private final Map<String, Double> backstopInBanThisMonth` | The overdraft the backstop closed this month in a sector already inside its ban, which nobody would make an interim loan to: forgiven, nothing new written off (restructure(sector, true)). |
| 3609 | `private final Map<String, Double> loansDefaultedThisMonth` | What defaulted this month in each class, per sector (0.7.12): the loans' and the bonds' share of the slice, or all of both in the backstop - before what was recovered, so recovered over defaulted is each class's recov... |
| 3610 | `private final Map<String, Double> bondsDefaultedThisMonth` |  |
| 3617 | `private final Map<String, Double> bondWrittenOffThisMonth` | The bondholders' side of the month's defaults (0.7.12), per sector: the face written off its bonds, every holder together. |
| 3618 | `private final Map<String, Double> bondWrittenOffTotal` |  |
| 3658 | `private final Map<String, Double> defaultedThisMonth` | The month's defaults, per sector, for the notice and the playtest - struck by restructureInsolventSectors() and read in the same month (Inbox.takeMonth()), so not saved: the bank's own record of the month's write-off ... |
| 3659 | `private final Map<String, Double> defaultShareThisMonth` |  |
| 3660 | `private final java.util.Set<String> restructuredThisMonth` |  |
| 3706 | `private final Map<String, Double> principalJudged` | What each sector owed when the month's insolvency check judged it, against the assets it judged it on (getAssets(), struck at the same check). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 3794 | **type** `public class BusinessDebtManager` | Private-sector credit. |
| 73 | 9 | `public void setSectors(String[] keys)` | The registry's names, in its order. |
| 83 | 1 | `public String[] sectors()` |  |

### PRICING FROM THE CURVE (0.7.8) (lines 85-152)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 3 | `public static double expectedLossSpread(double leverage)` | A borrower's own expected loss over the book's, at this leverage: LOAN_LOSS_GIVEN_DEFAULT x PD(L) less Bank.BASE_LOSS_RATE, never below nothing - the part of its rate that is its risk. |
| 139 | 4 | `static double pricingLeverage(double principal, double assets)` | The leverage a price is read at: what is owed over the assets, and the whole curve against no assets at all, owing or not - a business with nothing is not a good credit, it is an empty one. |
| 149 | 3 | `public static double expectedLossSpread(double leverage, double lossGivenDefault)` | ...at a loss given default of its own (0.7.12): the same curve at an instrument's own loss - a loan's is the one-argument form, and the screens quote a bond's beside it (RECOVERIES BY INSTRUMENT). |

### INSOLVENCY (lines 153-245)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 1 | `public void setRules(BankRules rules)` |  |
| 221 | 1 | `public BankRules rules()` |  |

### A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) (lines 246-335)

### RECOVERIES BY INSTRUMENT (0.7.12, round 2) (lines 336-544)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 35 | `public static double normalCdf(double x)` | The standard normal cumulative distribution, N(x): Hart's (1968) double-precision rational approximation as Graeme West gives it ("Better approximations to cumulative normal functions", Wilmott, 2005), with a continue... |
| 446 | 5 | `public static double defaultProbability(double leverage)` | PD(L): the share of a sector's firms - weighted by what they owe - that default within a year at this leverage, N(ln(L / INSOLVENCY_TRIGGER) / ASSET_VOLATILITY). |
| 453 | 6 | `public static double defaultProbability(double leverage, double months)` | ...over this many months instead of a year: 1 - (1 - PD)^(months / DEFAULT_HORIZON_MONTHS), for a borrower held at this leverage. |
| 461 | 3 | `public static double monthlyDefaultShare(double leverage)` | h: the share of a sector's debt that defaults in one month at this leverage - the monthly hazard of PD(L). |
| 466 | 4 | `static double leverageOf(double principal, double assets)` | A sector's leverage for the curve: what it owes over its assets, infinite when it owes anything against nothing. |
| 510 | 3 | `static int exclusionFor(int defaultsSoFar)` | How long a sector is shut out, given how many times it has done this. |

### AND ITS BONDS (0.7.12) (lines 545-1198)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 17 | **type** `public interface BondBook` | What the bond market tells the lender about each sector's bonds. |
| 569 | 1 | `double principal(String sector)` _(in BusinessDebtManager.BondBook)_ | The face a sector owes on its bonds. |
| 571 | 1 | `double monthlyCoupon(String sector)` _(in BusinessDebtManager.BondBook)_ | The coupons its bonds ask this month, on the face outstanding. |
| 573 | 1 | `double writeDown(String sector, double scale)` _(in BusinessDebtManager.BondBook)_ | Writes a sector's bonds down to this share of their face, every holder pro rata. |
| 575 | 1 | `default double faceDueWithin(String sector, int month, int months)` _(in BusinessDebtManager.BondBook)_ | The face of a sector's bonds that falls due within `months` settles of `month`'s (0.7.74, the sector statements' R2): nothing from a book that cannot say. |
| 577 | 1 | `default int lastMaturity(String sector)` _(in BusinessDebtManager.BondBook)_ | The month a sector's last bond falls due (0.7.75, R2's "runs to"): 0 from a book that cannot say. |
| 579 | 1 | `default double couponRate(String sector)` _(in BusinessDebtManager.BondBook)_ | The coupon its bonds pay a year, weighted by face (0.7.75, R2's rate): 0 from a book that cannot say. |
| 581 | 1 | `default double faceIssuedSoFar(String sector)` _(in BusinessDebtManager.BondBook)_ | The face a sector has sold, and repaid at maturity, since the counters started (0.7.75, R7): in memory; nothing from a book that does not count. |
| 582 | 1 | `default double faceRepaidSoFar(String sector)` _(in BusinessDebtManager.BondBook)_ |  |
| 586 | 18 | **type** `public interface BondDesk` | ...and where the desks ask whether a bond would be cheaper than the bank (BondMarket, WHO ISSUES, AND WHEN). |
| 599 | 2 | `Plan plan(String sector, double amount, double loanRate, double loanRoom, double bondRoom, double extraAssets, int month)` _(in BusinessDebtManager.BondDesk)_ | How to finance `amount` for this sector: a bond, the bank, or both. |
| 602 | 1 | `double issue(Plan plan, int month)` _(in BusinessDebtManager.BondDesk)_ | Issues a plan's bond. |
| 620 | 18 | **type** `public record Plan(String sector, double amount, double bondFace, double coupon, double allIn, double loan,...` | How one borrowing is financed (0.7.12): of `amount`, `bondFace` in a bond at `coupon` - `allIn` with its issuing costs spread over its life - and `loan` from the bank at `loanRate`, `loanAllIn` with its fee spread ove... |
| 624 | 3 | `public static Plan bankOnly(String sector, double amount, double loanRate, double loanAllIn, double clearing)` _(in BusinessDebtManager.Plan)_ | All of it from the bank. |
| 628 | 1 | `public boolean hasBond()` _(in BusinessDebtManager.Plan)_ | True when some of it is a bond. |
| 630 | 3 | `public double monthlyInterest()` _(in BusinessDebtManager.Plan)_ | The interest a month the plan costs, both parts: what the project's own test is asked to carry, each at its instrument's rate. |
| 634 | 1 | `public double blendedRate()` _(in BusinessDebtManager.Plan)_ | ...as an annual rate on what it raises. |
| 636 | 1 | `public boolean covers()` _(in BusinessDebtManager.Plan)_ | True when it raises the whole amount. |
| 643 | 4 | `public void setBondMarket(BondBook book, BondDesk desk)` | The bond market, wired by Game; null in a harness that builds this class alone. |
| 648 | 1 | `public BondDesk getBondDesk()` |  |
| 654 | 4 | `public double takeBondProceeds(String sector)` | The bonds the shortfall desk issued for a sector this month, net of their costs, handed over once. |
| 661 | 1 | `public Plan getShortfallPlan(String sector)` |  |
| 732 | 3 | `public static double feeOn(double principal)` | What a loan of this principal pays up front: Bank.LOAN_FEE of it. |
| 736 | 1 | `public double getFeesThisMonth()` |  |
| 737 | 1 | `public double getFeesThisMonth(String sector)` |  |
| 749 | 1 | `public double getMortgageFeesThisMonth(String sector)` | The fees on the mortgages written for this sector this month: part of getFeesThisMonth(sector). |
| 764 | 1 | `public double getLentThisMonth()` |  |
| 765 | 1 | `public double getRepaidThisMonth()` |  |
| 767 | 3 | `public double getLentThisMonth(String sector)` |  |
| 771 | 3 | `public double getRepaidThisMonth(String sector)` |  |
| 785 | 3 | `public double settleRetirement(String sector, double available, boolean empty)` | Retirement cash pays the bank's claims in the backstop's rank order: interim first, then its other loans pro rata. |
| 790 | 9 | `public double settleRetirement(String sector, double available, boolean empty, double recoverable)` | Liquid inventory can carry its claim at realizable proceeds until the regular sale settles. |
| 801 | 3 | `public void setRetirementRecovery(String sector, double value)` | Current realizable inventory and unbanked invoices, refreshed alongside the borrower's assets. |
| 806 | 3 | `public double settleRetiringLoans(String sector, double available)` | Cash from the retired inventory repays its marked claims before the next default judgment. |
| 810 | 26 | `private double repayLoanClaims(String sector, double available, boolean retiredOnly)` |  |
| 837 | 21 | `public void startAuditMonth()` |  |
| 859 | 7 | `public BusinessDebtManager()` |  |
| 875 | 3 | `public void setPrimeRate(double rate)` | Prime, pushed in by Game each month from Bank.prime() before anything is priced (0.7.7; the city's rate before it). |
| 880 | 3 | `public void setInsuredMortgageRate(double rate)` | The insured mortgage's rate, pushed in by Game with prime (Bank.insuredMortgageRate()). |
| 885 | 3 | `public double getInsuredMortgageRate()` | What a new insured mortgage is written at this month, and what a term that ends renews at. |
| 890 | 3 | `public void setAssets(String sector, double totalAssets)` | Total assets from that sector's balance sheet - the denominator of leverage. |
| 904 | 4 | `public void setConcentrationCharges(Map<String, Double> charges)` |  |
| 910 | 1 | `public double getConcentrationCharge(String sector)` | What a loan to this sector pays for the book's concentration, a year: part of its rate. |
| 913 | 5 | `public void updateRates()` | pricing |
| 920 | 7 | `private double priceSector(String sector)` | The quote: the curve at the leverage the sector's last quarter of statements reads (getQuarterLeverage()), and the whole curve against no assets (pricingLeverage()). |
| 929 | 3 | `private double priceSector(String sector, double extraPrincipal, double extraAssets)` | A quote, with a deal on top (see quote()): the rate its parts add up to. |
| 953 | 43 | `private QuoteParts quote(String sector, double extraPrincipal, double extraAssets)` | ratio. |
| 999 | 3 | `public double getRate(String sector)` | What NEW borrowing costs this sector today. |
| 1004 | 3 | `public double getSpread(String sector)` | What this sector pays over prime: its own expected loss, its record and the book's concentration on it (QuoteParts.spread()). |
| 1009 | 3 | `public double getRiskSpread(String sector)` | ...the first part of it: its own expected loss over the book's, at the leverage its last quarter reads (expectedLossSpread(), quarterPrincipal() over quarterAssets()) and a loan's loss given default. |
| 1014 | 3 | `public double getRecordSurcharge(String sector)` | ...and the second: DEFAULT_SURCHARGE a write-down on its record, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 1018 | 3 | `private double recordSurcharge(String sector)` |  |
| 1033 | 11 | **type** `public record QuoteParts(double prime, double risk, double record, double concentration)` | THE QUOTE IN ITS PARTS (0.7.33): the prime a sector's rate was struck on and the three things over it - its own expected loss over the book's, its record and the book's concentration - as quote() added them up the las... |
| 1035 | 6 | `public double spread()` _(in BusinessDebtManager.QuoteParts)_ | What the sector pays over prime: the three parts, added in quote()'s order. |
| 1042 | 1 | `public double rate()` _(in BusinessDebtManager.QuoteParts)_ | ...and the rate: prime and the spread, which is getRate(). |
| 1049 | 1 | `public QuoteParts quoteParts(String sector)` | A sector's rate in its parts as it was last priced; null for a sector not priced yet, whose getRate() is its placeholder. |
| 1058 | 3 | `public double projectRate(String sector, double amount)` | WHAT A PROJECT LOAN OF THIS SIZE WOULD BE WRITTEN AT (0.7.8): the curve at the leverage it leaves the sector at, the building counted at the loan's value - the rate issueProjectLoan() writes, and the one Game.consider... |
| 1067 | 4 | `public double leverageAfterProject(String sector, double amount)` | ...and the leverage that loan is priced at: the last quarter's statements with the deal on top, the building counted. |
| 1073 | 3 | `public double getPrimeRate()` | Prime, as the bank set it this month. |
| 1077 | 4 | `public double getLeverage(String sector)` |  |
| 1082 | 4 | `public void setCash(String sector, double cash)` |  |
| 1097 | 3 | `public double getCash(String sector)` |  |
| 1101 | 3 | `private double getOverdraftForgivenPending(String sector)` |  |
| 1106 | 5 | `public double takeOverdraftForgiven(String sector)` | The overdraft a restructure forgave this month, handed over once. |
| 1112 | 3 | `public double getAssets(String sector)` |  |
| 1117 | 5 | `public double getAllPrincipal()` | Everything every sector owes the bank. |
| 1124 | 3 | `public double getPrincipal(String sector)` | What a sector owes: its bank loans and, since 0.7.12, its bonds - see AND ITS BONDS. |
| 1133 | 10 | `public double[] getPrincipalsByKey()` | (AP2) getPrincipal() of every sector of Sectors.KEYS, in that order, from one pass over the loans: each sector's loans are summed in the book's order from nothing, as getLoanPrincipal() sums them, so every figure is t... |
| 1145 | 8 | **type** `private static final class KeyAt` | Each sector key's place in Sectors.KEYS (getPrincipalsByKey()), built the first time it is asked. |
| 1147 | 5 | `static { ... }` _(in BusinessDebtManager.KeyAt)_ |  |
| 1155 | 5 | `public double getEverythingOwed()` | What every business owes, bank loans and bonds together: getPrincipal() over every sector (0.7.32, the Finances tab's bond market, which summed it itself). |
| 1162 | 9 | `public double getLoanPrincipal(String sector)` | What a sector owes the bank: its loans and its mortgages. |
| 1178 | 8 | `public double getTermLoanPrincipal(String sector)` | ...of which its plain bank loans (0.7.30, the Sectors screen's debt mix): its loans that are neither a mortgage nor interim financing, so the four parts - these, its mortgages, its interim financing and its bonds - ar... |
| 1188 | 3 | `public double getBondPrincipal(String sector)` | ...and what it owes on its bonds (0.7.12): nothing without a bond market. |
| 1193 | 5 | `public double getLoanShare(String sector)` | The bank loans' share of what a sector owes: 1 with no bonds, and with no debt at all. |

### WHAT IT OWES, BY KIND AND BY WHEN (0.7.74, the sector statements' (lines 1199-1286)

| line | len | member | says |
|---:|---:|---|---|
| 1215 | 3 | `private static int kindOf(BusinessDebt loan)` | A loan's kind, DEBT_KINDS' index: a mortgage, interim financing, or a plain bank loan. |
| 1220 | 8 | `public double[] interestByKind(String sector)` | This month's interest by kind (R1): each loan's getMonthlyInterestExpense() by what it is, and the bonds' coupons - getMonthlyInterest() in four parts. |
| 1252 | 34 | `public double[][] debtByKind(String sector, int month)` | What a sector owes by kind (R2), and what of each the settles ahead would ask within a year and within five, as the model pays it: a loan whole in its last month (processMonth()'s maturity, principalDueNextMonth()'s r... |

### WHAT MOVED IT, BY KIND (0.7.75, the sector statements' R7) (lines 1287-1765)

| line | len | member | says |
|---:|---:|---|---|
| 1310 | 4 | `private void moved(String sector, int flow, int kind, double amount)` | A move of `amount` in one kind's principal: BORROWED, REPAID or WRITTEN_OFF. |
| 1316 | 11 | `public double[][] debtMovedByKind(String sector)` | One sector's principal moved so far, {borrowed, repaid, written off}, each in DEBT_KINDS' order: running totals, read twice and differenced (R7). |
| 1329 | 5 | `public Map<String, double[][]> movedToSave()` | Every sector's debtMovedByKind() as it stands, for the save (FW2, 2026-10-24). |
| 1343 | 19 | `public void restoreMoved(Map<String, double[][]> saved)` | ...and put back at the end of a load (FW2), so the totals carry on from where the saved city's stood and R7's schedule a month on differences two readings of one series: debtMovedByKind() reads the saved figures again... |
| 1363 | 7 | `public double getTotalPrincipal()` |  |
| 1372 | 3 | `public double getMonthlyInterest(String sector)` | This month's interest cost for a sector - the income statement's expense line: its loans' interest and, since 0.7.12, its bonds' coupons. |
| 1377 | 9 | `public double getLoanInterest(String sector)` | ...the part the bank is paid on its loans. |
| 1388 | 5 | `public double getTotalMonthlyInterest()` | Every sector's interest bill, loans and bonds. |
| 1399 | 4 | `public double getEffectiveRate(String sector)` | Blended annual rate actually being paid on existing debt, as opposed to getRate() which is what the next loan would cost. |
| 1404 | 9 | `public int getLoanCount(String sector)` |  |
| 1414 | 3 | `public List<BusinessDebt> getLoans()` |  |
| 1418 | 9 | `public List<BusinessDebt> getLoans(String sector)` |  |
| 1436 | 92 | `public void processMonth()` | Advances every loan and retires the ones that mature. |
| 1530 | 7 | `public double takeMaturedPrincipal(String sector)` | Reads and clears the principal that fell due this month for one sector. |
| 1548 | 182 | `public double coverShortfall(String sector, double cash, double monthlyLoss, int month)` | Underwrites a loan if the sector is short, and returns the proceeds - and since 0.7.12 sells a bond in part of its place where one is cheaper, whose proceeds wait for the settle (takeBondProceeds()). |
| 1740 | 5 | `private void countOpenLine(String sector, double lent, double oldRule)` |  |
| 1747 | 1 | `public boolean capitalRuleOn()` | True this month when the bank's capital rule limits new lending: under its target, or under its minimum. |
| 1750 | 1 | `public double getLineLentRationed(String sector)` | What the working-capital line lent this sector this month while the capital rule was on (round 5). |
| 1752 | 1 | `public double getLineLentPastOldRule(String sector)` | ...of it, what the 0.7.8 rule would have refused. |
| 1764 | 1 | `public double getShortfallLentThisMonth(String sector)` | ...one sector's. |

### ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) (lines 1766-1959)

| line | len | member | says |
|---:|---:|---|---|
| 1839 | 3 | `public double assetsNow(String sector, double cash)` | A sector's assets on the sheet as it stands, its till at `cash`: the month's refresh (getAssets()), with the till it read (getCash()) moved to this one. |
| 1852 | 7 | `public boolean pastDefaultPoint(String sector, double cash)` | Past the default point ON ITS QUARTER (round 3): owing more than INSOLVENCY_TRIGGER times what it owned, averaged over its last STATEMENT_MONTHS month-end readings (quarterPrincipal(), quarterAssets()) - or anything, ... |
| 1861 | 8 | `public boolean pastDefaultPoint(String sector, double cash, double amount, double handed)` | ...or past it once lent `amount`, `handed` of it paid into the till: before the loan or after it, on the same reading. |
| 1871 | 4 | `private double[] defaultPointReading(String sector, double cash)` | {owed, owned} as the default point reads them: the quarter's averages, or with no reading the sheet as it stands, its till at `cash` (round 3). |
| 1877 | 4 | `public double defaultPointLeverage(String sector)` | The leverage the default point reads a sector at now: its quarter's, or as it stands with no reading - for the investor's words and the screens. |
| 1888 | 1 | `public double getRefusedAtDefaultPoint(String sector)` | What the shortfall desk would have lent this sector this month and did not, because it is past the default point (0.7.12, round 2). |
| 1891 | 5 | `public double getRefusedAtDefaultPoint()` | ...every sector's. |
| 1898 | 1 | `public double getNotRenewedAtDefaultPoint(String sector)` | The mortgage balances that fell due whole this month rather than renew, because the landlords were past the default point. |
| 1901 | 1 | `public boolean wasRefusedAtDefaultPoint(String sector)` | True when the investment desk turned this sector's project down this month because it is past the default point. |
| 1909 | 5 | `public double bondCeilingRoom(String sector, double multiple)` | THE CEILING A BOND IS HELD TO (0.7.12): the line at this multiple of its assets, and the ban, as for a loan. |
| 1921 | 6 | `public double projectLoanRoom(String sector, double amount)` | WHAT THE BANK WILL LEND OF A PROJECT (0.7.12): the whole of it if canFundProject() says yes, and otherwise what its capital rule leaves under the default point after the deal - the most a bond and a loan may raise of ... |
| 1929 | 6 | `public double projectBondRoom(String sector, double amount)` | ...and the bond's own ceiling: the same default point after the deal, and the ban. |
| 1956 | 3 | `public double borrowingRoom(String sector)` | How much more this sector may borrow today, from either desk. |

### ...AND WHAT THE NEXT SETTLE WOULD DO, ASKED AHEAD (0.7.12, round 6) (lines 1960-2080)

| line | len | member | says |
|---:|---:|---|---|
| 1977 | 20 | `public double principalDueNextMonth(String sector)` | The principal that falls due at the next settle, as processMonth() will park it: each loan that matures, whole; each mortgage's next payment's principal, and its whole balance where the payment ends the term and the l... |
| 2009 | 12 | `public double workingCapitalLine(String sector, double cash, double due)` | What the working-capital line would hand this sector's till at the next settle, net of its fee: coverShortfall()'s room on coverShortfall()'s rules - the ceiling at MAX_LOAN_TO_ASSETS on what it will owe once what fal... |
| 2049 | 23 | `public boolean canFundProject(String sector, double amount)` | Whether the INVESTMENT desk will fund a project of this size. |
| 2074 | 6 | `private double ceilingRoom(String sector, double multiple)` | The ceiling alone: nothing while the lender is shut or the sector barred. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 2081-2510)

| line | len | member | says |
|---:|---:|---|---|
| 2192 | 23 | `public boolean canFundMortgage(String sector, double shortfall, double cost)` | Whether the lender will write a mortgage for this shortfall on a building of this cost: open, the sector not barred, the loan no more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost, and the borrower not past the ... |
| 2217 | 5 | `private boolean mortgagePastDefaultPoint(String sector, double principal)` | canFundMortgage()'s default-point test on a mortgage of this principal: past it as the sector stands, or once lent (0.7.12, round 2; see canFundProject()). |
| 2224 | 3 | `private boolean capitalShortFor(String sector, double principal)` | canFundMortgage()'s capital test on a mortgage of this principal: only while the leverage requirement binds (round 2). |
| 2237 | 5 | `public boolean mortgageShortOfCapital(String sector, double shortfall)` | ...the same door asked before the order goes to it, with nothing recorded: whether it would refuse the mortgage this shortfall takes for the bank's capital, and for nothing else (MC1). |
| 2254 | 5 | `public double ownersAskOnMortgage(String sector, double cost, double held)` | What a landlord's owners are asked for on an order of this cost bought on a mortgage, the till and its money abroad holding `held` (Game.consider(), THE OWNERS FIRST): what the till lacks on the down payment - or, whi... |
| 2261 | 1 | `public String getMortgageRefusal()` | Why canFundMortgage() last said no, or null if it said yes. |
| 2270 | 22 | `public Mortgage issueMortgage(String sector, double shortfall, int month)` | WRITES A MORTGAGE for this shortfall: the loan that covers it once the fee is paid (Mortgage.loanFor()), the premium added, at the insured rate. |
| 2294 | 1 | `public double getPremiumsThisMonth()` | The premiums on the mortgages written this month: the treasury's revenue line. |
| 2296 | 1 | `public double getPremiumsThisMonth(String sector)` | ...by sector, which its cash flow statement takes off what it was handed. |
| 2298 | 1 | `public double getPremiumsTotal()` | The premiums written over the city's life. |
| 2301 | 1 | `public int getMortgagesWrittenThisMonth()` | The mortgages written this month, renewed this month, and fallen due this month because the lender could not renew them. |
| 2302 | 1 | `public int getRenewedThisMonth()` |  |
| 2303 | 1 | `public int getFallenDueThisMonth()` |  |
| 2305 | 1 | `public int getRenewedLifetime()` | ...over the run, for the playtest; not saved. |
| 2306 | 1 | `public int getFallenDueLifetime()` |  |
| 2309 | 7 | `public List<Mortgage> getMortgages(String sector)` | Every mortgage one sector owes, in the order written. |
| 2318 | 5 | `public List<Mortgage> getMortgages()` | Every mortgage in the city. |
| 2325 | 1 | `public int getMortgageCount(String sector)` | How many mortgages one sector owes. |
| 2328 | 5 | `public double getMortgagePrincipal(String sector)` | What one sector owes on its mortgages. |
| 2335 | 5 | `public double getMortgagePrincipal()` | What every sector owes on mortgages: the bank's mortgage book. |
| 2342 | 5 | `public double getInsuredPrincipal(String sector)` | What one sector owes on its insured mortgages - the part of its debt the city insures. |
| 2349 | 5 | `public double getInsuredPrincipal()` | ...every sector's: what the bank's book holds at Bank.RISK_INSURED_MORTGAGE. |
| 2356 | 3 | `public double getUninsuredPrincipal(String sector)` | What one sector owes the bank that nobody insures: its loans less its insured mortgages - what the bank's allowance reads, and its capital rule while the leverage ratio does not bind (rationedPrincipal()). |
| 2361 | 5 | `public double getMortgagePayment(String sector)` | The level payments one sector's mortgages ask next month: interest and principal together. |
| 2368 | 5 | `public double getMortgagePayment()` | ...every sector's. |
| 2375 | 3 | `public double getMortgageRate(String sector)` | The rate one sector's mortgages carry, weighted by what is owed on each; 0 with none. |
| 2380 | 3 | `public double getMortgageRate()` | ...every mortgage's. |
| 2384 | 8 | `private static double weightedRate(List<Mortgage> ms)` |  |
| 2394 | 8 | `public int getNextRenewalMonth(String sector)` | The first month one of this sector's mortgages renews, or -1 with none. |
| 2404 | 5 | `public int getMortgagesRenewingWithin(int months)` | How many mortgages renew within this many months of this one. |
| 2411 | 4 | `public boolean allMortgagesInsured()` | True when every mortgage in the city is insured - which every one this build writes is. |
| 2417 | 1 | `public double getMortgageRepaidThisMonth(String sector)` | The principal one sector's mortgage payments took this month. |
| 2420 | 5 | `public double getMortgageRepaidThisMonth()` | ...every sector's. |
| 2427 | 1 | `public Map<String, Double> getMortgageRepaidToSave()` | The month's principal repaid on mortgages, for the save: a copy, by sector name. |
| 2430 | 7 | `public void restoreMortgageRepaid(Map<String, Double> saved)` | ...and back on load. |
| 2439 | 1 | `public double getInsuredWrittenOffThisMonth(String sector)` | What this month's write-downs took off one sector's insured mortgages: the claim the treasury pays the bank. |
| 2442 | 5 | `public double getInsuredWrittenOffThisMonth()` | ...every sector's: the month's claims. |
| 2449 | 1 | `public double getInsuredWrittenOffTotal(String sector)` | What write-downs have taken off one sector's insured mortgages over the city's life. |
| 2452 | 5 | `public double getInsuredWrittenOffTotal()` | ...every sector's: the claims over the city's life. |
| 2459 | 1 | `public double getPremiumsTotalToSave()` | The insurance book's record, for the save: the premiums over the city's life. |
| 2462 | 1 | `public Map<String, Double> getInsuredWrittenOffTotals()` | The claims over the city's life, by sector, for the save. |
| 2465 | 8 | `public void restoreInsuranceRecord(double premiums, Map<String, Double> claims)` | ...and both back on load. |
| 2479 | 3 | `private double writeDownSector(String sector, double scale)` | Writes every instrument of one sector down to this share, pro rata, and says what came off its insured mortgages - the claim. |
| 2484 | 8 | `private void writeDownInterim(String sector, double scale)` | One sector's interim loans alone, to this share of what they were. |
| 2494 | 16 | `private double writeDownSector(String sector, double scale, boolean interimToo)` | ...its interim loans with the rest, or (false) every loan but them - they rank first (INTERIM FINANCING). |

### ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) (lines 2511-2719)

| line | len | member | says |
|---:|---:|---|---|
| 2592 | 4 | `public void setProjectBudget(double room)` | Struck before lending, and again after defaults, net of lending still awaiting the bank's settle. |
| 2598 | 4 | `public void setProjectBudget(Bank bank)` | Read immediate bank claims too: bonds, securities and carry settle before loans do. |
| 2604 | 5 | `public double projectRoom()` | New borrowing and claims already on the bank's books consume one allowance: the room to the target over it, the band's share under it (MC2, Bank.projectHeadroom()). |
| 2610 | 5 | `private double insuredProjectRoom()` | ...and the insured mortgages' own, while the leverage ratio binds: the exposure room to the leverage target over it, the band's share under it, net of pending loans (BF1, MC2); capitalRoom() binds beside it. |
| 2616 | 3 | `private double pendingProjectLoans()` |  |
| 2621 | 5 | `public void projectLoansSettled()` | The bank has refreshed its loan book: these claims are now counted there, not pending too. |
| 2640 | 3 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | The bank's capital rule for the month, from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(). |
| 2650 | 14 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly, boolean insuredToo)` | ...and whether it rations the insured mortgages too: true when the bank's leverage requirement is the larger (Bank.leverageBinds(), 0.7.11 round 2), because a mortgage then uses the capital the bank is short of. |
| 2668 | 1 | `public boolean isInsuredRationed()` |  |
| 2671 | 3 | `private double rationedPrincipal(String sector)` | The debt the capital rule reads: what the sector owes the bank, uninsured, or all of it while insuredRationed. |
| 2681 | 6 | `public double capitalRoom(String sector)` | What the bank's capital lets this sector borrow this month, over what it owes now: its debt when the rule was set, grown by the month's limit (none under the minimum), less what it owes - so what matured is room to re... |
| 2689 | 1 | `public double getCapitalGrowth()` | The month's limit on a borrower's growth, a share a month: infinite with none. |
| 2692 | 1 | `public boolean isKeepGoingOnly()` | True when the bank lends only to keep its borrowers going this month. |
| 2695 | 1 | `public boolean wasRefusedForCapital(String sector)` | True when the capital rule refused this sector a project this month. |
| 2698 | 1 | `public boolean wasProjectRefusedForCapital(String sector)` | ...a building's loan, this month (round 5, the growth doors counted apart). |
| 2700 | 1 | `public boolean wasMortgageRefusedForCapital(String sector)` | ...a new mortgage, this month. |
| 2708 | 4 | `public void takeBackCapitalRefusal(String sector, boolean refusedBefore, boolean mortgageRefusedBefore)` | ...and a capital no taken back (HN1, REV-HN1-1): a smaller order then got past the door, so the sector was trimmed, not held. |
| 2714 | 5 | `public void takeBackCapitalRefusal(String sector, boolean refusedBefore, boolean mortgageRefusedBefore, boolean projectRefusedB...` | ...and a project's order trimmed at the door the same way (FB15, HN1-B): its project mark too, which the playtest counts. |

### THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) (lines 2720-2922)

| line | len | member | says |
|---:|---:|---|---|
| 2796 | 11 | `public void recordStatement(String sector, double owed, double owned)` | One month-end reading of a sector: what it owed and what it owned, as the bank read them (Game.sectorPositions()). |
| 2809 | 7 | `public double quarterPrincipal(String sector)` | What the sector owed, averaged over its last quarter of readings - what it owes now, with none. |
| 2818 | 7 | `public double quarterAssets(String sector)` | ...and what it owned. |
| 2827 | 4 | `public double getQuarterLeverage(String sector)` | The leverage the bank reads the sector at: its quarter's average debt over its average assets, 0 with no assets. |
| 2833 | 3 | `public double getQuarterDefaultRate(String sector)` | The sector's default rate a year at the leverage its last quarter reads (getQuarterLeverage()) - the reading its price is struck on (getRiskSpread()), which the screens print beside that price. |
| 2838 | 4 | `public int getStatementCount(String sector)` | How many readings a sector has, up to STATEMENT_MONTHS. |
| 2844 | 5 | `public Map<String, double[]> getStatementsToSave()` | The readings, for the save: a copy, by sector name. |
| 2851 | 10 | `public void restoreStatements(Map<String, double[]> saved)` | ...and back on load. |
| 2863 | 3 | `public void setLendingOpen(boolean open)` | Game tells the lender each month whether the bank behind it is standing. |
| 2867 | 3 | `public boolean isLendingOpen()` |  |
| 2877 | 3 | `public BusinessLoan issueLoan(String sector, double faceValue, int month)` | Writes a loan of this principal - a shortfall loan, priced at what the sector will owe over the assets it has. |
| 2882 | 3 | `public BusinessLoan issueProjectLoan(String sector, double faceValue, int month)` | ...a project's: priced with the building it buys counted in the assets, at the loan's value (projectRate()). |
| 2893 | 1 | **type** `public record Written(String sector, double amount, double leverage, double rate, boolean project)` | One loan written this month: to whom, how much, the leverage it left the borrower at, the rate it was written at, and whether it bought a building (0.7.8, for the playtest's count of loans written past the watch line ... |
| 2898 | 1 | `public List<Written> getWrittenThisMonth()` | Every loan written this month, in the order written. |
| 2900 | 22 | `private BusinessLoan write(String sector, double faceValue, int month, double extraAssets, boolean project)` |  |

### insolvency (lines 2923-3201)

| line | len | member | says |
|---:|---:|---|---|
| 2925 | 3 | `public boolean isBorrowingBlocked(String sector)` |  |
| 2929 | 3 | `public int getBlockedMonths(String sector)` |  |
| 2933 | 3 | `public double getWrittenOffThisMonth(String sector)` |  |
| 2937 | 3 | `public double getWrittenOffTotal(String sector)` |  |
| 2941 | 3 | `public int getRestructureCount(String sector)` |  |
| 2953 | 9 | `public void restoreWriteOffs(java.util.Map<String, Double> totals)` | Puts the write-off history back on load. |
| 2963 | 3 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2990 | 3 | `public java.util.Map<String, Integer> getRestructureCounts()` | THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED. |
| 2994 | 3 | `public java.util.Map<String, Integer> getBlockedMonthsAll()` |  |
| 2998 | 19 | `public void restoreCreditRecord(java.util.Map<String, Integer> counts, java.util.Map<String, Integer> blocked)` |  |
| 3018 | 7 | `public double getTotalWrittenOff()` |  |
| 3042 | 4 | `public boolean isInsolvent(String sector)` | Is every firm in this sector under water at once - the backstop's case? |
| 3062 | 3 | `public double restructure(String sector)` | THE BACKSTOP: writes a sector with nothing left down to what its assets can support - RESTRUCTURE_TARGET of nothing - forgives its overdraft, counts the default on its record and shuts it out for exclusionFor(). |
| 3075 | 126 | `public double restructure(String sector, boolean forced)` | ...or, `forced`, a sector nobody would make an interim loan to - still past the default point after the month's write-down, or its bank shut - whatever its assets read (INTERIM FINANCING, round 5): the whole sector to... |

### CAN'T PAY MEANS DEFAULT (0.7.12, round 4) (lines 3202-3299)

| line | len | member | says |
|---:|---:|---|---|
| 3260 | 1 | **type** `public enum ShortReason` | Why the shortfall desk left a sector short (round 4). |
| 3267 | 1 | `public ShortReason getShortReason(String sector)` | Why the shortfall desk left this sector short this month - the tighter of its limits, or its reason to refuse - or null if it was not asked. |
| 3270 | 3 | `public void setMonthObligations(String sector, double amount)` | What the month asked this sector to pay: its costs, interest and taxes on this month's statement and the principal that fell due (EconomyManager.settleBusinessCredit()). |
| 3273 | 1 | `public double getMonthObligations(String sector)` |  |
| 3281 | 1 | `public double getCannotPayShort(String sector)` | What the month's bills still left unpaid in one sector when the cash-flow test struck (round 4) - lent as interim financing since round 5, or the backstop's; 0 if it did not default for want of cash. |
| 3283 | 1 | `public double getCannotPayShare(String sector)` | ...the share of it that could not pay, h. |
| 3285 | 1 | `public ShortReason getCannotPayReason(String sector)` | ...and why no lender stood behind it, or null. |
| 3293 | 6 | `double cannotPayShare(String sector)` | THE CASH-FLOW TEST'S SHARE: the part of a sector whose till is short that cannot pay - its shortfall over what the month asked it to pay, all of it when that is more than the month's bills. |

### INTERIM FINANCING (0.7.12, round 5) (lines 3300-3852)

| line | len | member | says |
|---:|---:|---|---|
| 3381 | 1 | `public double getInterimLentThisMonth(String sector)` | What the interim lender lent this sector this month, at face (round 5). |
| 3383 | 1 | `public String getInterimRefusal(String sector)` | Why the interim lender would not lend to this sector this month, or null. |
| 3385 | 1 | `public double getInterimMaturedThisMonth(String sector)` | The interim principal that fell due this month in this sector: repaid, or rolled by the shortfall desk. |
| 3387 | 1 | `public double getInterimWrittenOffThisMonth(String sector)` | The interim principal a backstop wrote off this month in this sector. |
| 3392 | 1 | `public double getBackstopInBanThisMonth(String sector)` | ...one sector's. |
| 3395 | 4 | `public double takeInterimHanded(String sector)` | The interim loan's cash this sector's till is owed from the month's defaults, handed over once (EconomyManager.settleInsolvency()). |
| 3401 | 5 | `public double getInterimPrincipal(String sector)` | What one sector owes on interim financing. |
| 3408 | 5 | `public double getInterimPrincipal()` | ...every sector's. |
| 3415 | 5 | `public int getInterimCount(String sector)` | How many interim loans one sector has outstanding. |
| 3422 | 5 | `public int getInterimCount()` | ...every sector's. |
| 3435 | 10 | `String interimRefusal(String sector, double cash, double loan, double handed, double writtenDown)` | THE INTERIM LENDER'S TEST: null to lend, or why not. |
| 3447 | 7 | `double priceInterim(String sector, double faceValue)` | What an interim loan of this face would be written at: prime, the curve at the leverage its rank sees, the record and the concentration (INTERIM FINANCING). |
| 3456 | 16 | `private InterimLoan writeInterim(String sector, double faceValue)` | Writes an interim loan: counted in the month's lending and fees like any loan, the bank paying it out at the settle. |
| 3484 | 62 | `public double restructureInsolventSectors()` | THE MONTH'S DEFAULTS, every sector: the backstop for a sector with nothing left (restructure()), and for every other the slice of its debt whose firms fell through the default point (defaultSlice()) - or, since round ... |
| 3562 | 3 | `public double defaultSlice(String sector)` | THE SLICE: the share of this sector's debt whose firms fell through the default point this month, monthlyDefaultShare() of it at the leverage the month's check reads (principal over getAssets(), struck after the balan... |
| 3567 | 40 | `public double defaultSlice(String sector, double atLeast)` | ...or, if more, this share: the part of it that cannot pay this month (round 4, CAN'T PAY MEANS DEFAULT). |
| 3613 | 1 | `public double getLoansDefaultedThisMonth(String sector)` | The loans and the bonds that defaulted this month in one sector, before recovery (0.7.12). |
| 3614 | 1 | `public double getBondsDefaultedThisMonth(String sector)` |  |
| 3620 | 6 | `private void recordBondWriteOff(String sector, double face)` |  |
| 3628 | 1 | `public double getBondWrittenOffThisMonth(String sector)` | What this month's defaults took off one sector's bonds, every holder together (0.7.12). |
| 3631 | 1 | `public double getBondWrittenOffTotal(String sector)` | ...over the city's life. |
| 3634 | 5 | `public double getBondWrittenOffTotal()` | ...every sector's, over the city's life. |
| 3641 | 1 | `public Map<String, Double> getBondWrittenOffTotals()` | The bondholders' losses over the city's life, by sector, for the save. |
| 3644 | 7 | `public void restoreBondWrittenOff(Map<String, Double> totals)` | ...and back on load; an older save has none. |
| 3663 | 1 | `public double getDefaultedThisMonth(String sector)` | The debt whose firms defaulted this month in the slice, before what its creditors recover - each instrument's write-off is its own loss given default of its part (getLoansDefaultedThisMonth(), getBondsDefaultedThisMon... |
| 3666 | 1 | `public double getDefaultShareThisMonth(String sector)` | The share of the sector's debt that defaulted this month in the slice, h. |
| 3669 | 1 | `public boolean wasRestructuredThisMonth(String sector)` | True when the backstop wrote this sector down whole this month. |
| 3672 | 3 | `public double getDefaultRate(String sector)` | The sector's default rate a year at its leverage now, PD(L) - what the Bank tab shows beside its leverage. |
| 3692 | 4 | `public boolean defaultsAreNews(String sector)` | WHETHER THIS MONTH'S DEFAULTS ARE NEWS: the backstop, or a slice at least the share that defaults a month at the default point itself - monthlyDefaultShare(INSOLVENCY_TRIGGER), 5.6% of its debt, where half the sector'... |
| 3708 | 4 | `public double getPrincipalJudged(String sector)` |  |
| 3714 | 8 | `public void advanceBlocks()` | Counts down the borrowing bans. |
| 3724 | 3 | `public void setLoans(List<BusinessDebt> loans)` | save / load |
| 3728 | 42 | `public void clearLoans()` |  |
| 3772 | 29 | `public void printBusinessDebtInfo(int currentMonth)` | printers |
| 3804 | 4 | `static { ... }` |  |
| 3810 | 41 | `public void redenominate(double scale)` | Every business loan and this month's lending, in the new unit. |

