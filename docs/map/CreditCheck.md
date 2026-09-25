# CreditCheck.java - 1,231 lines · 10 methods · 1 constants · harnesses

`ham/citybuildersim/CreditCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Verifies private-sector credit: pricing, origination, rollover, cash conservation.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (57), [DebtManager](DebtManager.md) (16), [Game](Game.md) (15), [DebtQuote](DebtQuote.md) (7), [GameFiles](GameFiles.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [Bank](Bank.md) (4), [Sectors](Sectors.md) (3), [LongTermBond](LongTermBond.md) (2), [Debt](Debt.md) (2), [BusinessLoan](BusinessLoan.md) (1), [Sector](Sector.md) (1), [FoodIndustry](FoodIndustry.md) (1), [Trade](Trade.md) (1), [Good](Good.md) (1), [BalanceSheet](BalanceSheet.md) (1), [Founding](Founding.md) (1), [BuildingManager](BuildingManager.md) (1), [EconomyManager](EconomyManager.md) (1), [Equity](Equity.md) (1)

## Sections

| line | section |
|---:|---|
| 35 | · 1. pricing |
| 155 | · · ...and it pays its fee out of the proceeds (0.7.7) |
| 165 | · 2. rate is fixed at issue |
| 183 | · 3. origination |
| 202 | · · ...and it stops at the borrower's own insolvency line |
| 224 | · · ...and the ceiling is BELOW the line, by a real gap |
| 287 | · · ...and a shut lender lends nothing, from either desk |
| 311 | · · ...and a repeat defaulter is shut out for longer |
| 354 | · · ...and the record is priced, not only banned on |
| 382 | · · ...and a bankruptcy forgives the overdraft, once |
| 424 | · 4. maturity and rollover |
| 458 | · 5. cash conservation |
| 497 | · 6. balance sheet integration |
| 507 | · 7. the spiral guard |
| 539 | · the CITY's debt market, repriced |
| 651 | · 7. the quote IS the deal |
| 747 | · 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR |
| 811 | · 10. THE STORY THAT BROKE, END TO END |
| 899 | · 10. THE BAN IS ONE BAN |
| 915 | · · MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY. |
| 1059 | 11. THE CURVE (0.7.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `CreditCheck.IND` | `Sectors.INDUSTRY` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 6 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 4 | 1228 | **type** `public class CreditCheck` | Verifies private-sector credit: pricing, origination, rollover, cash conservation. |
| 8 | 6 | `static void check(String label, double actual, double expected)` |  |
| 15 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 23 | 9 | `static double priced(DebtManager m, double cash)` | The standing rate at a given cash position, leaving the market as it found it. |
| 33 | 1025 | `public static void main(String[] args) throws Exception` |  |

### 11. THE CURVE (0.7.1) (lines 1059-1231)

| line | len | member | says |
|---:|---:|---|---|
| 1073 | 76 | `static void theCurve(Game city)` |  |
| 1158 | 3 | `static boolean onCurve(DebtQuote q)` | True if this quote came off the sloped part of the curve. |
| 1162 | 6 | `static boolean onCurve(double rate)` |  |
| 1170 | 5 | `static void quietly(Runnable work)` | Runs a stretch of the game without its per-month console output. |
| 1176 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 1192 | 39 | `static void bookAndCompare(Game g, String type, double amount, int duration, double rounding, boolean mustBeOnCurve)` | Takes a quote, books it, and checks the books say what the quote said. |

