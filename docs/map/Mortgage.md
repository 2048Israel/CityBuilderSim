# Mortgage.java - 458 lines · 35 methods · 10 constants · model

`ham/citybuildersim/Mortgage.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> An insured mortgage on a new residential building: a level payment every
> month over a forty-year amortization, at a rate fixed for a ten-year term
> and renewed at the day's rate when the term ends, insured by the city.
> 
> WHY (0.7.11). The landlords built on BusinessLoan, the same instrument as
> a mill: interest only for LOAN_TERM_MONTHS, three years, then the whole
> principal back at once. That is a bridge loan, and nobody finances
> housing on one. A trace of 0.7.10 on autopilot (seed 0, months 2,280 to
> 2,490) found what it did: the landlord's maturing loans were not rolled,
> so its principal fell from $3.4B to $0.5B while its till went from
> -$3.9B to -$6.8B; every new building then had to borrow its cost plus
> that hole, so even one unit failed the interest test at a 2.9% prime;
> and nothing was built for about two hundred months while the city was
> 37% short of homes and nobody was out of work. Held at a 10% dial the
> same test lent against 100% of the cost at 11.3-11.7%, a new home earned
> 8-9% gross at market rent, and nothing was built for 333 years. A high
> rate cut the supply of homes much harder than it cut the demand for them
> (the project's the-rate-that-stops-the-cranes.md).
> 
> Jerus chose the instrument a landlord actually borrows on, and whose
> terms: "Mortgages", "CMHC (Canada)", "Keep it" (the rent floor), and
> "Insured by the city".
> 
> THE PRODUCT. CMHC's Mortgage Loan Insurance for Standard Rental Housing
> (five units or more): a loan of up to 85% of the lending value, amortized
> over up to 40 years - which "must not exceed the remaining economic life
> of the property" - with a debt coverage ratio of at least 1.20 on a term
> of ten years or more (1.30 on a shorter one). CMHC, Standard Rental
> Housing, https://www.cmhc-schl.gc.ca/professionals/project-funding-and-
> mortgage-financing/mortgage-loan-insurance/multi-unit-insurance/standard-
> rental-housing, and its reference guide, https://assets.cmhc-schl.gc.ca/
> sf/project/cmhc/pdfs/content/en/reference-guide.pdf. The constants below
> are those terms; the lender's two tests (MORTGAGE_MAX_LOAN_TO_COST,
> MORTGAGE_DEBT_COVERAGE) are asked in Game.consider() and
> BusinessDebtManager.canFundMortgage().
> 
> WHAT IT DOES EACH MONTH. The payment is the level annuity on what is left
> over what is left of the amortization, at the fixed rate:
> 
>     A = B r / (1 - (1 + r)^-n),   r = rate / 12
> 
> The interest, B r, is an expense on the landlord's income statement, as a
> loan's interest always was (getMonthlyInterestExpense(); the rent floor
> reads it through the sector's interest line). The rest, A - B r, is
> principal: a cash movement that lowers the balance and is not an expense -
> the manager parks it with the principal that fell due this month and the
> month's settle takes it from the landlord's till, exactly as it takes a
> bullet's at maturity (BusinessDebtManager.processMonth()). After
> MORTGAGE_AMORTIZATION_MONTHS payments nothing is owed.
> 
> WHY A BusinessDebt AND NOT A BusinessLoan. The hierarchy is where business
> credit lives: its interest is the borrower's expense and never the city's
> cash, its principal is settled by the manager against the borrower's
> books, the bank's book reads getOutstandingPrincipal(), and a default
> writes every instrument of the borrower down pro rata through writeDown().
> A mortgage needs all of that. What it does not share with BusinessLoan is
> the schedule: a loan charges interest on its face and hands its principal
> back at maturity; this pays both down every month, and renews rather than
> matures. A sibling, not a child.
> 
> ... (13 more lines in the source)

**Uses:** [Bank](Bank.md) (2), [BusinessDebt](BusinessDebt.md) (1), [Formats](Formats.md) (1)

**Used by (9):** [Bank](Bank.md), [BankScreen](BankScreen.md), [BusinessDebtManager](BusinessDebtManager.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [LongPlaytest](LongPlaytest.md), [MortgageCheck](MortgageCheck.md), [ReadPathCheck](ReadPathCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 169 | · the arithmetic |
| 247 | · the landlord's order |
| 323 | · the month |
| 396 | · readings |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `Mortgage.MORTGAGE_TERM_MONTHS` | `120` | The term the rate is fixed for, in months: ten years, the term CMHC's 1.20 debt coverage is asked on (1.30 under ten), and the point of the bank's curve it is priced at (Bank.insuredMortgageRate()). |
| 84 | `Mortgage.MORTGAGE_AMORTIZATION_MONTHS` | `480` | The amortization, in months: forty years, the longest CMHC insures standard rental housing over - and no longer than the economic life of the building, which in this game does not wear out. |
| 87 | `Mortgage.MORTGAGE_MAX_LOAN_TO_COST` | `.85` | The most a mortgage lends against the building's cost: 85%, CMHC's highest loan-to-value for standard rental housing - so the landlord puts at least 15% from its own funds. |
| 90 | `Mortgage.MORTGAGE_DEBT_COVERAGE` | `1.20` | The lender's test: the building's net operating income must cover the mortgage's monthly payment this many times - CMHC's minimum debt coverage ratio, 1.20, on a term of ten years or more. |
| 101 | `Mortgage.PREMIUM_RATE` | `.05` | CMHC's premium on a standard rental loan at an LTV of 85% or less, as a share of the loan: 5.00%. |
| 104 | `Mortgage.PREMIUM_SURCHARGE` | `.0025` | The premium's surcharge for each PREMIUM_SURCHARGE_STEP_YEARS of amortization past PREMIUM_BASE_YEARS: 0.25% of the loan (the same schedule), so 0.75% at forty years. |
| 107 | `Mortgage.PREMIUM_BASE_YEARS` | `25` | The amortization the base premium is struck for, in years: 25; a longer one pays PREMIUM_SURCHARGE a step. |
| 110 | `Mortgage.PREMIUM_SURCHARGE_STEP_YEARS` | `5` | How many years of amortization past PREMIUM_BASE_YEARS each step of the surcharge covers: five, up to forty. |
| 299 | `Mortgage.Decision.DOWN_PAYMENT` | `"down payment"` | Trimmed, or refused, because its own funds could not put the down payment on it. |
| 301 | `Mortgage.Decision.LENDERS_TEST` | `"lender's test"` | ...because its rent would not cover the payment MORTGAGE_DEBT_COVERAGE times. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 113 | `private double loan` | What was advanced toward the building, before the premium was added to it. |
| 123 | `private double premium` | The premium, added to the principal and paid to the treasury. |
| 126 | `private int amortizationMonths` | The whole amortization it was written over, and the months of it still to pay. |
| 127 | `private int amortizationLeft` |  |
| 130 | `private int termMonths` | How long the term that is running was written for; remainingMonths is what is left of it. |
| 133 | `private int renewals` | How many times it has renewed. |
| 136 | `private boolean insured` | Insured by the city: every mortgage this build writes. |
| 139 | `private transient double principalPaid` | The principal this month's payment took off the balance, for the manager to settle. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 78 | 381 | **type** `public class Mortgage extends BusinessDebt` | An insured mortgage on a new residential building: a level payment every month over a forty-year amortization, at a rate fixed for a ten-year term and renewed at the day's rate when the term ends, insured by the city. |
| 142 | 1 | `Mortgage()` | For Gson. |
| 152 | 16 | `public Mortgage(String sector, double loan, int monthStarted, double annualRate, boolean insured)` | Writes a mortgage: the loan, the premium added to it, a term of MORTGAGE_TERM_MONTHS at this rate, and MORTGAGE_AMORTIZATION_MONTHS to pay it off. |

### the arithmetic (lines 169-246)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 5 | `public static double premiumRate(int amortizationMonths)` | The premium as a share of the loan, for an amortization this long: PREMIUM_RATE, plus PREMIUM_SURCHARGE for each PREMIUM_SURCHARGE_STEP_YEARS (or part of one) past PREMIUM_BASE_YEARS. |
| 179 | 3 | `public static double premiumRate()` | The premium as a share of the loan on a mortgage this build writes, over MORTGAGE_AMORTIZATION_MONTHS: 5.75%. |
| 188 | 6 | `public static double payment(double balance, double annualRate, int months)` | The level monthly payment that pays this balance off over this many months at this annual rate: B r / (1 - (1 + r)^-n), r a twelfth of the rate - and B / n at no rate at all. |
| 200 | 9 | `public static double balanceAfter(double balance, double annualRate, int months, int k)` | What is left after k of the payments above, the closed form: B (1 + r)^k less the payments compounded, A ((1 + r)^k - 1) / r. |
| 218 | 4 | `public static double loanFor(double shortfall)` | The loan that covers a shortfall once its fee is paid: the landlord is handed the loan less Bank.LOAN_FEE of the principal the premium is added to, so the loan is the shortfall over 1 - LOAN_FEE x (1 + premiumRate()). |
| 229 | 4 | `public static double ownFundsFor(double cost)` | The least the landlord must hold to buy a building of this cost on a mortgage: its down payment, 1 - MORTGAGE_MAX_LOAN_TO_COST of the cost, and the fee on the largest mortgage the rest could be - what makes loanFor(co... |
| 241 | 5 | `public static double coverage(double netOperatingIncome, double shortfall, double annualRate)` | THE LENDER'S TEST, as a ratio: this net operating income over the monthly payment on the mortgage that covers this shortfall - its principal, the premium added, at this rate over MORTGAGE_AMORTIZATION_MONTHS. |

### the landlord's order (lines 247-322)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 23 | `public static Decision decide(int asked, java.util.function.IntToDoubleFunction costOf, double cash, double noiPerUnit, double ...` | WHAT THE LANDLORD CAN BUY ON A MORTGAGE, and why not more: the largest number of these, scanning down from what was asked for, that its own funds buy outright, or that its own funds can put the down payment on (ownFun... |
| 295 | 27 | **type** `public record Decision(int quantity, String trimmedBy, boolean shortOfDown, double ownFundsForOne, double c...` | The order, decided: how many; what trimmed it from what was asked, if anything did (DOWN_PAYMENT or LENDERS_TEST, the first that failed on the way down); and, when not even one passed, whether one was short of its dow... |
| 304 | 8 | `public String refusal(String building)` _(in Mortgage.Decision)_ | The advisor's line when not even one passes: the down payment it needs, or the lender's figure against its own. |
| 314 | 7 | `public String trimmed(int asked)` _(in Mortgage.Decision)_ | ...and the words for an order cut down from what was asked, or nothing when it was not. |

### the month (lines 323-395)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 19 | `public void processMonth()` | One payment: the interest on what is owed at the fixed rate, which is the income statement's; and the principal, the rest of the level payment, taken off the balance and left for the manager to settle (takePrincipalPa... |
| 356 | 5 | `public double takePrincipalPaid()` | The principal this month's payment took off the balance, handed over once. |
| 363 | 3 | `public boolean isTermEnded()` | True once the term that is running has run, with something still to pay. |
| 368 | 3 | `public boolean isPaidOff()` | True once the amortization is done and nothing is owed. |
| 379 | 7 | `public void renew(double annualRate)` | RENEWS for another term at the day's rate: MORTGAGE_TERM_MONTHS, or what is left of the amortization if that is shorter. |
| 388 | 7 | `public double close()` | Closes it: the whole balance falls due, as a loan's does at maturity (a renewal the bank cannot write). |

### readings (lines 396-458)

| line | len | member | says |
|---:|---:|---|---|
| 400 | 3 | `public double getMonthlyInterestExpense()` | Interest on what is owed: the balance, not the face - it is paid down. |
| 405 | 3 | `public double getOutstandingPrincipal()` |  |
| 411 | 3 | `public int getMaturityMonth()` | The month the term that is running ends - when it renews, not when it is paid off (getPaidOffMonth()). |
| 417 | 3 | `public boolean isMatured()` | Never matures as a loan does: it renews, or it is paid off, and the manager handles both. |
| 422 | 3 | `public String getType()` |  |
| 427 | 3 | `public double getMonthlyPayment()` | The level payment due next month, on what is owed over what is left at the fixed rate. |
| 432 | 3 | `public int getNextRenewalMonth()` | The month the term that is running ends. |
| 437 | 3 | `public int getPaidOffMonth()` | The month the last payment falls. |
| 441 | 1 | `public double getLoan()` |  |
| 442 | 1 | `public double getPremium()` |  |
| 443 | 1 | `public int getAmortizationMonths()` |  |
| 444 | 1 | `public int getAmortizationLeft()` |  |
| 445 | 1 | `public int getTermMonths()` |  |
| 446 | 1 | `public int getRenewals()` |  |
| 447 | 1 | `public boolean isInsured()` |  |
| 448 | 1 | `public int getMonthStarted()` |  |
| 452 | 6 | `public void redenominate(double scale)` | The mortgage in the new unit: its balance, its face, the loan and the premium. |

