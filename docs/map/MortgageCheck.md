# MortgageCheck.java - 1,027 lines · 24 methods · 2 constants · harnesses

`ham/citybuildersim/MortgageCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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
>  11. A BRANCH THAT DOES NOT PAY IS CLOSED: after Bank.BRANCH_CLOSE_MONTHS
>      of a book that does not keep its branches' staff, and not before; a
>      covered bank closes nothing; the last branch stays; in a played city
>      the closure is a retired building, and the bank's equity and the
>      money audit close through it.
>  12. PAYOUTS AFTER PRINCIPAL: a landlord with a mortgage pays Equity.PAYOUT
>      of its income less the principal it repaid, and nothing when the
>      principal is the larger (the cushion that counts the payments is
>      ExchangeCheck's).
> 
> Every fixture causes its condition.

**Uses:** [Mortgage](Mortgage.md) (83), [Bank](Bank.md) (68), [BusinessDebtManager](BusinessDebtManager.md) (26), [Game](Game.md) (19), [GameFiles](GameFiles.md) (6), [LongPlaytest](LongPlaytest.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Equity](Equity.md) (4), [BusinessDebt](BusinessDebt.md) (3), [Sectors](Sectors.md) (2), [SectorBooks](SectorBooks.md) (2), [Founding](Founding.md) (1), [Formats](Formats.md) (1), [BusinessLoan](BusinessLoan.md) (1), [RealEstate](RealEstate.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Ladder](Ladder.md) (1), [CentralBank](CentralBank.md) (1), [DebtManager](DebtManager.md) (1), [EconomyManager](EconomyManager.md) (1), [DemolitionLog](DemolitionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 171 | 1. THE ANNUITY |
| 236 | 2. ORIGINATION |
| 312 | 3. THE DOWN PAYMENT |
| 359 | 4. THE LENDER'S TEST |
| 386 | 5. FIXED FOR THE TERM |
| 454 | 6. INSURANCE |
| 520 | 7. THE BANK |
| 638 | 8. SAVE AND LOAD |
| 737 | 9. WHERE THE OLD RULE SAID NO |
| 806 | 10. THE LEVERAGE RATIO (round 2) |
| 892 | 11. A BRANCH THAT DOES NOT PAY IS CLOSED (round 2) |
| 986 | 12. PAYOUTS AFTER PRINCIPAL (round 2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 109 | `MortgageCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 110 | `MortgageCheck.FEE` | `Bank.LOAN_FEE` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `static int fails` |  |
| 75 | `static PrintStream out` |  |
| 76 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 956 | **type** `public class MortgageCheck` | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. |
| 78 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 83 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 89 | 6 | `static void same(String label, String actual, String expected)` |  |
| 96 | 5 | `static void quietly(Runnable r)` |  |
| 102 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 113 | 5 | `static double scheduleRate()` | The premium's rate from the schedule's own constants: the base and a surcharge for each step past the base years. |
| 120 | 4 | `static double level(double balance, double annual, int months)` | The level payment, written out here rather than asked of the class under test. |
| 130 | 18 | `static Game landlordCity(Path root, String name)` | A city whose landlords buy on mortgages: founded, funded, given ground and jobs, with a bank, and its housing left to the landlords - who are short of doors from the first month, so they order at once. |
| 149 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ANNUITY (lines 171-235)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 62 | `static void annuity()` |  |

### 2. ORIGINATION (lines 236-311)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 73 | `static void origination(Path root) throws Exception` |  |

### 3. THE DOWN PAYMENT (lines 312-358)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 44 | `static void downPayment(Path root)` |  |

### 4. THE LENDER'S TEST (lines 359-385)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 24 | `static void lendersTest()` |  |

### 5. FIXED FOR THE TERM (lines 386-453)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 65 | `static void fixedForTheTerm(Path root)` |  |

### 6. INSURANCE (lines 454-519)

| line | len | member | says |
|---:|---:|---|---|
| 456 | 63 | `static void insurance(Path root)` |  |

### 7. THE BANK (lines 520-637)

| line | len | member | says |
|---:|---:|---|---|
| 522 | 115 | `static void theBank(Path root)` |  |

### 8. SAVE AND LOAD (lines 638-736)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 87 | `static void saveAndLoad(Path root) throws Exception` |  |
| 729 | 7 | `static String describe(BusinessDebt loan)` | A loan's fields, to the bit, as one string. |

### 9. WHERE THE OLD RULE SAID NO (lines 737-805)

| line | len | member | says |
|---:|---:|---|---|
| 739 | 66 | `static void whereTheOldRuleSaidNo(Path root)` |  |

### 10. THE LEVERAGE RATIO (round 2) (lines 806-891)

| line | len | member | says |
|---:|---:|---|---|
| 813 | 9 | `static Bank mortgageBank(double equity)` | A bank by hand whose business book is $1M, $900k of it insured mortgages, and whose equity is `equity`: net borrowed, so the book is everything on its sheet and the exposure is the book. |
| 823 | 68 | `static void leverageRatio()` |  |

### 11. A BRANCH THAT DOES NOT PAY IS CLOSED (round 2) (lines 892-985)

| line | len | member | says |
|---:|---:|---|---|
| 895 | 6 | `static void closeAMonth(Bank b, double interest, double payroll)` | One closed month of a hand-built bank: this interest, these costs. |
| 902 | 83 | `static void branchesClose(Path root)` |  |

### 12. PAYOUTS AFTER PRINCIPAL (round 2) (lines 986-1027)

| line | len | member | says |
|---:|---:|---|---|
| 988 | 39 | `static void payoutsAfterPrincipal(Path root)` |  |

