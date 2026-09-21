# CreditCheck.java - 1,022 lines · 9 methods · 1 constants · harnesses

`ham/citybuildersim/CreditCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Verifies private-sector credit: pricing, origination, rollover, cash conservation.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (36), [Game](Game.md) (14), [DebtManager](DebtManager.md) (7), [DebtQuote](DebtQuote.md) (6), [GameFiles](GameFiles.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [Sectors](Sectors.md) (3), [BusinessLoan](BusinessLoan.md) (1), [Sector](Sector.md) (1), [FoodIndustry](FoodIndustry.md) (1), [Trade](Trade.md) (1), [Good](Good.md) (1), [BalanceSheet](BalanceSheet.md) (1), [BuildingManager](BuildingManager.md) (1), [EconomyManager](EconomyManager.md) (1), [Equity](Equity.md) (1), [Debt](Debt.md) (1)

## Sections

| line | section |
|---:|---|
| 35 | · 1. pricing |
| 97 | · 2. rate is fixed at issue |
| 115 | · 3. origination |
| 132 | · · ...and it stops at the borrower's own insolvency line |
| 154 | · · ...and the ceiling is BELOW the line, by a real gap |
| 205 | · · ...and a shut lender lends nothing, from either desk |
| 229 | · · ...and a repeat defaulter is shut out for longer |
| 269 | · · ...and the record is priced, not only banned on |
| 292 | · · ...and a bankruptcy forgives the overdraft, once |
| 334 | · 4. maturity and rollover |
| 364 | · 5. cash conservation |
| 403 | · 6. balance sheet integration |
| 413 | · 7. the spiral guard |
| 439 | · the CITY's debt market, repriced |
| 548 | · 7. the quote IS the deal |
| 640 | · 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR |
| 698 | · 10. THE STORY THAT BROKE, END TO END |
| 783 | · 10. THE BAN IS ONE BAN |
| 799 | · · MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `CreditCheck.IND` | `Sectors.INDUSTRY` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 6 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 4 | 1019 | **type** `public class CreditCheck` | Verifies private-sector credit: pricing, origination, rollover, cash conservation. |
| 8 | 6 | `static void check(String label, double actual, double expected)` |  |
| 15 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 23 | 9 | `static double priced(DebtManager m, double cash)` | The standing rate at a given cash position, leaving the market as it found it. |
| 33 | 907 | `public static void main(String[] args) throws Exception` |  |
| 949 | 3 | `static boolean onCurve(DebtQuote q)` | True if this quote came off the sloped part of the curve. |
| 953 | 6 | `static boolean onCurve(double rate)` |  |
| 961 | 5 | `static void quietly(Runnable work)` | Runs a stretch of the game without its per-month console output. |
| 967 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 983 | 39 | `static void bookAndCompare(Game g, String type, double amount, int duration, double rounding, boolean mustBeOnCurve)` | Takes a quote, books it, and checks the books say what the quote said. |

