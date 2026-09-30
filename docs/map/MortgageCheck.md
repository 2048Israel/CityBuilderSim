# MortgageCheck.java - 1,194 lines · 25 methods · 2 constants · harnesses

`ham/citybuildersim/MortgageCheck.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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
> Every fixture causes its condition.

**Uses:** [Mortgage](Mortgage.md) (83), [Bank](Bank.md) (62), [BusinessDebtManager](BusinessDebtManager.md) (34), [Game](Game.md) (20), [LongPlaytest](LongPlaytest.md) (7), [GameFiles](GameFiles.md) (6), [Equity](Equity.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [BusinessDebt](BusinessDebt.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [Sectors](Sectors.md) (2), [EconomyManager](EconomyManager.md) (2), [SectorBooks](SectorBooks.md) (2), [Founding](Founding.md) (1), [Formats](Formats.md) (1), [BusinessLoan](BusinessLoan.md) (1), [RealEstate](RealEstate.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Ladder](Ladder.md) (1), [CentralBank](CentralBank.md) (1), [DebtManager](DebtManager.md) (1), [TaxPolicy](TaxPolicy.md) (1), [DemolitionLog](DemolitionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 189 | 1. THE ANNUITY |
| 254 | 2. ORIGINATION |
| 330 | 3. THE DOWN PAYMENT |
| 392 | 4. THE LENDER'S TEST |
| 419 | 5. FIXED FOR THE TERM |
| 487 | 6. INSURANCE |
| 565 | 7. THE BANK |
| 687 | 8. SAVE AND LOAD |
| 786 | 9. WHERE THE OLD RULE SAID NO |
| 891 | 10. THE LEVERAGE RATIO (round 2) |
| 984 | 11. A BRANCH THAT DOES NOT PAY IS CLOSED (round 2; 0.7.19) |
| 1102 | 12. PAYOUTS AFTER PRINCIPAL (round 2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `MortgageCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 113 | `MortgageCheck.FEE` | `Bank.LOAN_FEE` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static PrintStream out` |  |
| 79 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1120 | **type** `public class MortgageCheck` | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. |
| 81 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 86 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 92 | 6 | `static void same(String label, String actual, String expected)` |  |
| 99 | 5 | `static void quietly(Runnable r)` |  |
| 105 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 116 | 5 | `static double scheduleRate()` | The premium's rate from the schedule's own constants: the base and a surcharge for each step past the base years. |
| 123 | 4 | `static double level(double balance, double annual, int months)` | The level payment, written out here rather than asked of the class under test. |
| 133 | 3 | `static Game landlordCity(Path root, String name)` | A city whose landlords buy on mortgages: founded, funded, given ground and jobs, with a bank, and its housing left to the landlords - who are short of doors from the first month, so they order at once. |
| 138 | 28 | `static Game landlordCity(Path root, String name, boolean worksStanding)` | ...with its works standing from the start when asked: see payoutsAfterPrincipal(). |
| 167 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ANNUITY (lines 189-253)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 62 | `static void annuity()` |  |

### 2. ORIGINATION (lines 254-329)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 73 | `static void origination(Path root) throws Exception` |  |

### 3. THE DOWN PAYMENT (lines 330-391)

| line | len | member | says |
|---:|---:|---|---|
| 332 | 59 | `static void downPayment(Path root)` |  |

### 4. THE LENDER'S TEST (lines 392-418)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 24 | `static void lendersTest()` |  |

### 5. FIXED FOR THE TERM (lines 419-486)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 65 | `static void fixedForTheTerm(Path root)` |  |

### 6. INSURANCE (lines 487-564)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 75 | `static void insurance(Path root)` |  |

### 7. THE BANK (lines 565-686)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 119 | `static void theBank(Path root)` |  |

### 8. SAVE AND LOAD (lines 687-785)

| line | len | member | says |
|---:|---:|---|---|
| 689 | 87 | `static void saveAndLoad(Path root) throws Exception` |  |
| 778 | 7 | `static String describe(BusinessDebt loan)` | A loan's fields, to the bit, as one string. |

### 9. WHERE THE OLD RULE SAID NO (lines 786-890)

| line | len | member | says |
|---:|---:|---|---|
| 788 | 102 | `static void whereTheOldRuleSaidNo(Path root)` |  |

### 10. THE LEVERAGE RATIO (round 2) (lines 891-983)

| line | len | member | says |
|---:|---:|---|---|
| 898 | 9 | `static Bank mortgageBank(double equity)` | A bank by hand whose business book is $1M, $900k of it insured mortgages, and whose equity is `equity`: net borrowed, so the book is everything on its sheet and the exposure is the book. |
| 908 | 75 | `static void leverageRatio()` |  |

### 11. A BRANCH THAT DOES NOT PAY IS CLOSED (round 2; 0.7.19) (lines 984-1101)

| line | len | member | says |
|---:|---:|---|---|
| 1002 | 6 | `static void closeAMonth(Bank b, double interest, double payroll)` | One closed month of a hand-built bank: this interest, these costs. |
| 1009 | 92 | `static void branchesClose(Path root)` |  |

### 12. PAYOUTS AFTER PRINCIPAL (round 2) (lines 1102-1194)

| line | len | member | says |
|---:|---:|---|---|
| 1104 | 90 | `static void payoutsAfterPrincipal(Path root)` |  |

