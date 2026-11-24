# MortgageCheck.java - 1,327 lines · 30 methods · 5 constants · harnesses

`ham/citybuildersim/MortgageCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The landlords' insured mortgages (0.7.11): the instrument, the lender's
> tests, the city's insurance and the bank's book.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Mortgages", "CMHC (Canada)",
> "Keep it", "Insured by the city"):
> 
>   1. THE ANNUITY: the payment is the level annuity; the balance after k
>      payments is the closed form; interest and principal add up to the
>      payment every month; nothing is owed after
>      Mortgage.MORTGAGE_AMORTIZATION_MONTHS.
>   2. ORIGINATION: the loan is the cost less what the landlord put in, and it
>      put in at least 1 - MORTGAGE_MAX_LOAN_TO_COST; the premium is CMHC's
>      5.00% and the forty-year surcharge on the loan, added to the principal
>      and received by the treasury; a played month that writes one closes.
>   3. THE DOWN PAYMENT: a landlord short of it on one holds, in words.
>   4. THE LENDER'S TEST AT ITS LINE: MORTGAGE_DEBT_COVERAGE is financed, a
>      hair under is declined, in words.
>   5. FIXED FOR THE TERM: the dial moving does not move the payment; at
>      MORTGAGE_TERM_MONTHS it renews at the day's rate over what is left.
>      And the rent floor reads its interest and not its principal.
>   6. INSURANCE: a landlord written down - a slice, and the backstop - owes
>      less; the bank loses nothing on the insured part; the treasury pays
>      the bank exactly that part, on its claims line; the month closes.
>   7. THE BANK: an insured mortgage weighs nothing and carries no
>      allowance. It is priced with no loss and with the capital its
>      leverage requirement ties up (round 2). It is not rationed by
>      capital while the risk-based requirement binds, and is rationed
>      with the rest when the leverage ratio does.
>   8. SAVE AND LOAD mid-term, every field; an older save's loans unchanged.
>   9. WHERE THE OLD RULE SAID NO: a landlord the 1.25x interest test refused
>      on 100% of the cost, at its own risk's rate, that the lender's test on
>      85% passes - and it builds.
> 
> ROUND 2 (Jerus, 2026-09-24: "Basel leverage ratio", "Close losing
> branches", "Pay out after principal"):
> 
>  10. THE LEVERAGE RATIO: a bank whose book is mostly insured mortgages is
>      held to Bank.LEVERAGE_RATIO_MIN of everything it has lent. Its target
>      scales with its risk-based one. Its payout, its buybacks and its desk
>      stop at the leverage requirement when that is the larger, and its
>      lending tightens on it. A bank under it opens no branch for the
>      capital, and its weight table still foots.
>  11. A BRANCH THAT DOES NOT PAY IS CLOSED (rewritten for 0.7.19): every
>      branch past what the month's fees cover closes that month, with no
>      fuse to wait out; a bank whose fees cover its branches closes
>      nothing; the last branch stays; in a played city the closure is a
>      retired building, and the bank's equity and the money audit close
>      through it; and what the rule reads survives a save. (Until 0.7.19:
>      after Bank.BRANCH_CLOSE_MONTHS of a book that did not keep its
>      branches' staff, and not before.)
>  12. PAYOUTS AFTER PRINCIPAL: a landlord with a mortgage pays Equity.PAYOUT
>      of its income less the principal it repaid, and nothing when the
>      principal is the larger (the cushion that counts the payments is
>      ExchangeCheck's).
> 
> Every fixture causes its condition. FS9: the played landlords' town moved
> with HN1-A (they order at the door's price): section 12's lean month no
> longer came in the sixty months it searched, and the others waited on what
> the played town happened to do. Each is set, not waited for:
>   2 and 3 play one month of the founded town (short of doors from the
> ... (12 more lines in the source)

**Uses:** [Mortgage](Mortgage.md) (87), [Bank](Bank.md) (62), [BusinessDebtManager](BusinessDebtManager.md) (38), [Game](Game.md) (24), [LongPlaytest](LongPlaytest.md) (7), [MoneyAudit](MoneyAudit.md) (7), [Equity](Equity.md) (7), [GameFiles](GameFiles.md) (6), [EconomyManager](EconomyManager.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (4), [BusinessDebt](BusinessDebt.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [Sectors](Sectors.md) (2), [BusinessLoan](BusinessLoan.md) (2), [Founding](Founding.md) (1), [TempDirs](TempDirs.md) (1), [Formats](Formats.md) (1), [RealEstate](RealEstate.md) (1), [NationalAccounts](NationalAccounts.md) (1), [BondMarket](BondMarket.md) (1), [Ladder](Ladder.md) (1), [CentralBank](CentralBank.md) (1), [DebtManager](DebtManager.md) (1), [TaxPolicy](TaxPolicy.md) (1), [DemolitionLog](DemolitionLog.md) (1), [SectorBooks](SectorBooks.md) (1)

**Used by (1):** [BuildersFirstCheck](BuildersFirstCheck.md)

## Sections

| line | section |
|---:|---|
| 217 | 1. THE ANNUITY |
| 282 | 2. ORIGINATION |
| 363 | 3. THE DOWN PAYMENT |
| 421 | 4. THE LENDER'S TEST |
| 448 | 5. FIXED FOR THE TERM |
| 521 | 6. INSURANCE |
| 623 | 7. THE BANK |
| 745 | 8. SAVE AND LOAD |
| 862 | 9. WHERE THE OLD RULE SAID NO |
| 974 | 10. THE LEVERAGE RATIO (round 2) |
| 1067 | 11. A BRANCH THAT DOES NOT PAY IS CLOSED (round 2; 0.7.19) |
| 1192 | 12. PAYOUTS AFTER PRINCIPAL (round 2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 128 | `MortgageCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 129 | `MortgageCheck.FEE` | `Bank.LOAN_FEE` |  |
| 1195 | `MortgageCheck.PAYOUT_FIXTURE_RATE` | `.005` | The insured rate section 12's by-hand mortgages are written at: low enough that a mortgage's interest leaves the landlords' income standing while its principal can still pass it. |
| 1198 | `MortgageCheck.HOMES_STANDING` | `50` | The homes section 12 stands for the landlords to let (buildStack, noConstruction): enough that the month's rent is more than its costs once their tenants have moved in. |
| 1207 | `MortgageCheck.START_UP_MONTHS` | `6` | The most months section 12 plays for those homes to earn, the first month of a founded town being its start-up: a fixture bound, and the fixture fails past it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `static int fails` |  |
| 94 | `static PrintStream out` |  |
| 95 | `static PrintStream quiet` |  |
| 1211 | `double income, principal, paid, shortfall, till` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 91 | 1237 | **type** `public class MortgageCheck` | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. |
| 97 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 102 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 108 | 6 | `static void same(String label, String actual, String expected)` |  |
| 115 | 5 | `static void quietly(Runnable r)` |  |
| 121 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 132 | 5 | `static double scheduleRate()` | The premium's rate from the schedule's own constants: the base and a surcharge for each step past the base years. |
| 139 | 4 | `static double level(double balance, double annual, int months)` | The level payment, written out here rather than asked of the class under test. |
| 149 | 3 | `static Game landlordCity(Path root, String name)` | A city whose landlords buy on mortgages: founded, funded, given ground and jobs, with a bank, and its housing left to the landlords - who are short of doors from the first month, so they order at once. |
| 154 | 3 | `static Game landlordCity(Path root, String name, boolean worksStanding)` | ...with its works standing from the start when asked: see payoutsAfterPrincipal(). |
| 165 | 29 | `static Game landlordCity(Path root, String name, boolean worksStanding, int depotsStanding)` | ...and, when its works stand, with that many more depots standing beside the founding yard (PT1-M4). |
| 195 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ANNUITY (lines 217-281)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 62 | `static void annuity()` |  |

### 2. ORIGINATION (lines 282-362)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 78 | `static void origination(Path root) throws Exception` |  |

### 3. THE DOWN PAYMENT (lines 363-420)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 55 | `static void downPayment(Path root)` |  |

### 4. THE LENDER'S TEST (lines 421-447)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 24 | `static void lendersTest()` |  |

### 5. FIXED FOR THE TERM (lines 448-520)

| line | len | member | says |
|---:|---:|---|---|
| 450 | 70 | `static void fixedForTheTerm(Path root)` |  |

### 6. INSURANCE (lines 521-622)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 99 | `static void insurance(Path root)` |  |

### 7. THE BANK (lines 623-744)

| line | len | member | says |
|---:|---:|---|---|
| 625 | 119 | `static void theBank(Path root)` |  |

### 8. SAVE AND LOAD (lines 745-861)

| line | len | member | says |
|---:|---:|---|---|
| 747 | 105 | `static void saveAndLoad(Path root) throws Exception` |  |
| 854 | 7 | `static String describe(BusinessDebt loan)` | A loan's fields, to the bit, as one string. |

### 9. WHERE THE OLD RULE SAID NO (lines 862-973)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 109 | `static void whereTheOldRuleSaidNo(Path root)` |  |

### 10. THE LEVERAGE RATIO (round 2) (lines 974-1066)

| line | len | member | says |
|---:|---:|---|---|
| 981 | 9 | `static Bank mortgageBank(double equity)` | A bank by hand whose business book is $1M, $900k of it insured mortgages, and whose equity is `equity`: net borrowed, so the book is everything on its sheet and the exposure is the book. |
| 991 | 75 | `static void leverageRatio()` |  |

### 11. A BRANCH THAT DOES NOT PAY IS CLOSED (round 2; 0.7.19) (lines 1067-1191)

| line | len | member | says |
|---:|---:|---|---|
| 1085 | 6 | `static void closeAMonth(Bank b, double interest, double payroll)` | One closed month of a hand-built bank: this interest, these costs. |
| 1092 | 99 | `static void branchesClose(Path root)` |  |

### 12. PAYOUTS AFTER PRINCIPAL (round 2) (lines 1192-1327)

| line | len | member | says |
|---:|---:|---|---|
| 1201 | 4 | `static void standLandlords(Game g)` | HOMES_STANDING homes stand for the landlords to own (in a town whose works stand: the founding town builds none at once), and they are held from ordering: what a by-hand fixture owes against, and what keeps nothing bu... |
| 1210 | 3 | **type** `static final class LandlordMonth` | One month of the landlords' books as the dividend step reads them: what it earned and repaid, what it paid its owners, what the desk lent it, and the till as the dividend found it. |
| 1215 | 13 | `static LandlordMonth playLandlordMonth(Game g)` | One month played, the till read where the dividend step finds it (the settle probe, after the month's investment and before the owners are paid): nothing in the month moves. |
| 1230 | 3 | `static double rolled(LandlordMonth books, LandlordMonth paying)` | What the shortfall desk rolled of a month's principal, in the month that pays on it (Game.dividendDue(): never more than what fell due). |
| 1240 | 13 | `static Mortgage borrowByHand(Game g, double firstMonthPrincipal)` | A mortgage written by hand on the landlords whose first month repays `firstMonthPrincipal` of principal: the annuity less its interest at PAYOUT_FIXTURE_RATE, per dollar of shortfall, from a bare lender's own mortgage... |
| 1254 | 73 | `static void payoutsAfterPrincipal(Path root)` |  |

