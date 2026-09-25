# TreasuryLine.java - 119 lines · 2 methods · 0 constants · model

`ham/citybuildersim/TreasuryLine.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Every kind of payment the treasury makes, and whether it is a promise.
> 
> WHY (0.7.0). The treasury's overdraft used to be an emergency note with no
> limit on it, so a broke city paid every bill it had by borrowing at whatever
> the note cost - 27-36% once its bank really paid for the paper - and
> compounded to $193 quadrillion. Now the overdraft is advances from the
> central bank, and they stop at a ceiling in months of revenue (the player's
> dial since 0.7.2, CentralBank.DEFAULT_ADVANCES_MONTHS until it is moved).
> Past that something has to go unpaid, and Jerus chose what, the night the
> rule was written: "pay promises first, cut the rest."
> 
> A PROMISE is paid whatever the treasury holds, drawing advances past the
> ceiling if it must: pensions, EI, health, the schools, the city's own wages,
> and the coupons and principal on its paper (a default is a different rule).
> Everything else is DISCRETIONARY: once the ceiling has bound, and while any
> arrears are still owed, it is paid only from cash at or above zero
> (Game.discretionaryRoom()). A discretionary line that is not a purchase and
> is refused becomes ARREARS - owed, interest-free, paid down first when cash
> returns (Game.settleTreasury()); a refused PURCHASE is simply not made.
> 
> Every payment goes through Game.treasuryPays() (the two that do not are
> named in TreasuryJournal's list), which reads these two flags and nothing
> else. A constant's sentence says why it sits on its side.

**Used by (4):** [CentralBankCheck](CentralBankCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [LongPlaytest](LongPlaytest.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 31 | `TreasuryLine.INTEREST` | The coupons on the city's own paper, accrued through the month: a coupon is a promise, and not paying one is a default. |
| 34 | `TreasuryLine.PRINCIPAL` | Principal on the city's own paper, as each piece falls due - the same promise as the coupon. |
| 37 | `TreasuryLine.FOREIGN_INTEREST` | A dollar coupon abroad; not paying it is the default rule in Game.checkForeignSolvency(), not this one. |
| 40 | `TreasuryLine.FOREIGN_PRINCIPAL` | Dollar principal abroad, for the coupon's reason. |
| 43 | `TreasuryLine.CENTRAL_BANK_INTEREST` | What the treasury pays on its advances: a coupon to its own central bank, and remitted back to it less what reserves cost. |
| 46 | `TreasuryLine.PENSIONS` | Pensions: a promise made to people who have stopped working. |
| 49 | `TreasuryLine.EI_BENEFITS` | EI benefits: a promise made to the people out of work. |
| 52 | `TreasuryLine.HEALTHCARE` | The health service's bill: its staff and its upkeep. |
| 55 | `TreasuryLine.SCHOOLS` | The schools' bill: their staff and their upkeep. |
| 58 | `TreasuryLine.SAFETY` | The police and the prisons - the city's own wages again. |
| 61 | `TreasuryLine.CITY_SERVICES` | The utilities and transit, when they cost more than they collect: the city's own wages and running costs. |
| 64 | `TreasuryLine.STUDENT_LOANS` | Student loans lent: the ledger lent it at enrolment, so refusing the cash would leave a student owing money never received. |
| 73 | `TreasuryLine.MORTGAGE_INSURANCE_CLAIMS` | What the city's mortgage insurance pays the bank when a landlord's insured mortgage is written down (0.7.11): a guarantee, and the reason the bank weighs the loan at nothing - a claim the insurer might not pay would n... |
| 76 | `TreasuryLine.CONSTRUCTION_SUBSIDY` | The standing policy's top-up of the builders to break-even - the construction subsidy. |
| 79 | `TreasuryLine.BUSINESS_SUBSIDIES` | The standing policy's top-up of any other protected sector to break-even. |
| 82 | `TreasuryLine.STUDENT_GRANTS` | Grants to students: a policy the city chose and can owe. |
| 85 | `TreasuryLine.CITY_REPAIRS` | The city's share of the repair bill on its own buildings: work done, and owed to the builders if it cannot be paid. |
| 88 | `TreasuryLine.LAND` | Land the city buys, from the land office or back from a sector; refused, it stays unbought. |
| 91 | `TreasuryLine.BUILDINGS` | Buildings the city orders; refused, they are not ordered. |
| 94 | `TreasuryLine.BANK_CAPITAL` | Capital put into the commercial bank. |
| 97 | `TreasuryLine.RESERVE_PURCHASES` | Foreign currency bought for the vault. |
| 100 | `TreasuryLine.BUYBACKS` | A bond bought back before it is due. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `public final String label` | The player's words for it, for the Government tab's arrears list and the playtest. |
| 106 | `public final boolean promise` | Paid whatever the treasury holds, past the ceiling if it must be. |
| 109 | `public final boolean purchase` | Refused, it is simply not made; nothing is owed. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 28 | 92 | **type** `public enum TreasuryLine` | Every kind of payment the treasury makes, and whether it is a promise. |
| 111 | 5 | `TreasuryLine(String label, boolean promise, boolean purchase)` |  |
| 118 | 1 | `public boolean accruesArrears()` | True for a discretionary line whose refusal is owed as arrears. |

