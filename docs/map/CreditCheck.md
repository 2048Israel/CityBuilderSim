# CreditCheck.java - 2,020 lines · 18 methods · 1 constants · harnesses

`ham/citybuildersim/CreditCheck.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> Verifies private-sector credit: pricing, origination, rollover, cash conservation.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (133), [Game](Game.md) (19), [Sectors](Sectors.md) (16), [DebtManager](DebtManager.md) (16), [Bank](Bank.md) (13), [GameFiles](GameFiles.md) (7), [DebtQuote](DebtQuote.md) (7), [LongPlaytest](LongPlaytest.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (5), [BusinessLoan](BusinessLoan.md) (4), [InterimLoan](InterimLoan.md) (3), [Founding](Founding.md) (2), [BuildingManager](BuildingManager.md) (2), [Mortgage](Mortgage.md) (2), [MoneyAudit](MoneyAudit.md) (2), [LongTermBond](LongTermBond.md) (2), [Debt](Debt.md) (2), [Sector](Sector.md) (1), [FoodIndustry](FoodIndustry.md) (1), [Trade](Trade.md) (1), [Good](Good.md) (1), [BalanceSheet](BalanceSheet.md) (1), [EconomyManager](EconomyManager.md) (1), [Equity](Equity.md) (1), [BusinessDebt](BusinessDebt.md) (1), [SectorBooks](SectorBooks.md) (1), [Investor](Investor.md) (1)

## Sections

| line | section |
|---:|---|
| 35 | · 1. pricing |
| 156 | · · ...and it pays its fee out of the proceeds (0.7.7) |
| 166 | · 2. rate is fixed at issue |
| 184 | · 3. origination |
| 203 | · · ...and it stops at the borrower's own insolvency line |
| 225 | · · ...and the ceiling is BELOW the line, by a real gap |
| 288 | · · ...and a shut lender lends nothing, from either desk |
| 312 | · · ...and a repeat defaulter is shut out for longer |
| 355 | · · ...and the record is priced, not only banned on |
| 383 | · · ...and a bankruptcy forgives the overdraft, once |
| 425 | · 4. maturity and rollover |
| 459 | · 5. cash conservation |
| 498 | · 6. balance sheet integration |
| 508 | · 7. the spiral guard |
| 540 | · the CITY's debt market, repriced |
| 652 | · 7. the quote IS the deal |
| 748 | · 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR |
| 812 | · 10. THE STORY THAT BROKE, END TO END |
| 900 | · 10. THE BAN IS ONE BAN |
| 916 | · · MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY. |
| 1096 | 13. CAN'T PAY MEANS DEFAULT (0.7.12, round 4) |
| 1321 | 15. A NEW SECTOR'S FIRST READING (0.7.12, round 7) |
| 1443 | 16. THE SHORTFALL DESK'S BOND, GROSSED UP (0.7.12, round 7) |
| 1567 | 12. NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) |
| 1848 | 11. THE CURVE (0.7.1) |

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
| 4 | 2017 | **type** `public class CreditCheck` | Verifies private-sector credit: pricing, origination, rollover, cash conservation. |
| 8 | 6 | `static void check(String label, double actual, double expected)` |  |
| 15 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 23 | 9 | `static double priced(DebtManager m, double cash)` | The standing rate at a given cash position, leaving the market as it found it. |
| 33 | 1062 | `public static void main(String[] args) throws Exception` |  |

### 13. CAN'T PAY MEANS DEFAULT (0.7.12, round 4) (lines 1096-1320)

| line | len | member | says |
|---:|---:|---|---|
| 1111 | 12 | `static BusinessDebtManager.BondBook bondsOf(String s, double[] face)` |  |
| 1124 | 196 | `static void cantPayMeansDefault()` |  |

### 15. A NEW SECTOR'S FIRST READING (0.7.12, round 7) (lines 1321-1442)

| line | len | member | says |
|---:|---:|---|---|
| 1339 | 103 | `static void firstReading() throws Exception` |  |

### 16. THE SHORTFALL DESK'S BOND, GROSSED UP (0.7.12, round 7) (lines 1443-1566)

| line | len | member | says |
|---:|---:|---|---|
| 1452 | 14 | `static BusinessDebtManager.BondDesk deskOf(double bondPart, double cost)` |  |
| 1467 | 20 | `static void shortfallBondGrossedUp()` |  |
| 1489 | 11 | `static double nothingLeft()` | A sector owing $1,000 of loans and a $200 interim loan with nothing left: what of the interim loan the backstop keeps. |
| 1501 | 65 | `static void creditLinesStayOpen()` |  |

### 12. NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) (lines 1567-1847)

| line | len | member | says |
|---:|---:|---|---|
| 1581 | 266 | `static void nothingPastTheDefaultPoint() throws Exception` |  |

### 11. THE CURVE (0.7.1) (lines 1848-2020)

| line | len | member | says |
|---:|---:|---|---|
| 1862 | 76 | `static void theCurve(Game city)` |  |
| 1947 | 3 | `static boolean onCurve(DebtQuote q)` | True if this quote came off the sloped part of the curve. |
| 1951 | 6 | `static boolean onCurve(double rate)` |  |
| 1959 | 5 | `static void quietly(Runnable work)` | Runs a stretch of the game without its per-month console output. |
| 1965 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 1981 | 39 | `static void bookAndCompare(Game g, String type, double amount, int duration, double rounding, boolean mustBeOnCurve)` | Takes a quote, books it, and checks the books say what the quote said. |

