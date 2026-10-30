# CreditCheck.java - 2,092 lines · 19 methods · 1 constants · harnesses

`ham/citybuildersim/CreditCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> Verifies private-sector credit: pricing, origination, rollover, cash conservation.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (133), [Game](Game.md) (20), [Sectors](Sectors.md) (16), [DebtManager](DebtManager.md) (16), [Bank](Bank.md) (13), [GameFiles](GameFiles.md) (7), [DebtQuote](DebtQuote.md) (7), [LongPlaytest](LongPlaytest.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (5), [BusinessLoan](BusinessLoan.md) (4), [MoneyAudit](MoneyAudit.md) (4), [InterimLoan](InterimLoan.md) (3), [Debt](Debt.md) (3), [Founding](Founding.md) (2), [BuildingManager](BuildingManager.md) (2), [Mortgage](Mortgage.md) (2), [LongTermBond](LongTermBond.md) (2), [Sector](Sector.md) (1), [FoodIndustry](FoodIndustry.md) (1), [Trade](Trade.md) (1), [Good](Good.md) (1), [BalanceSheet](BalanceSheet.md) (1), [TempDirs](TempDirs.md) (1), [EconomyManager](EconomyManager.md) (1), [Equity](Equity.md) (1), [BusinessDebt](BusinessDebt.md) (1), [SectorBooks](SectorBooks.md) (1), [Investor](Investor.md) (1)

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
| 766 | · 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR |
| 830 | · 10. THE STORY THAT BROKE, END TO END |
| 918 | · 10. THE BAN IS ONE BAN |
| 934 | · · MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY. |
| 1152 | 13. CAN'T PAY MEANS DEFAULT (0.7.12, round 4) |
| 1377 | 15. A NEW SECTOR'S FIRST READING (0.7.12, round 7) |
| 1499 | 16. THE SHORTFALL DESK'S BOND, GROSSED UP (0.7.12, round 7) |
| 1623 | 12. NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) |
| 1906 | 11. THE CURVE (0.7.1) |

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
| 4 | 2089 | **type** `public class CreditCheck` | Verifies private-sector credit: pricing, origination, rollover, cash conservation. |
| 8 | 6 | `static void check(String label, double actual, double expected)` |  |
| 15 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 23 | 9 | `static double priced(DebtManager m, double cash)` | The standing rate at a given cash position, leaving the market as it found it. |
| 33 | 1118 | `public static void main(String[] args) throws Exception` |  |

### 13. CAN'T PAY MEANS DEFAULT (0.7.12, round 4) (lines 1152-1376)

| line | len | member | says |
|---:|---:|---|---|
| 1167 | 12 | `static BusinessDebtManager.BondBook bondsOf(String s, double[] face)` |  |
| 1180 | 196 | `static void cantPayMeansDefault()` |  |

### 15. A NEW SECTOR'S FIRST READING (0.7.12, round 7) (lines 1377-1498)

| line | len | member | says |
|---:|---:|---|---|
| 1395 | 103 | `static void firstReading() throws Exception` |  |

### 16. THE SHORTFALL DESK'S BOND, GROSSED UP (0.7.12, round 7) (lines 1499-1622)

| line | len | member | says |
|---:|---:|---|---|
| 1508 | 14 | `static BusinessDebtManager.BondDesk deskOf(double bondPart, double cost)` |  |
| 1523 | 20 | `static void shortfallBondGrossedUp()` |  |
| 1545 | 11 | `static double nothingLeft()` | A sector owing $1,000 of loans and a $200 interim loan with nothing left: what of the interim loan the backstop keeps. |
| 1557 | 65 | `static void creditLinesStayOpen()` |  |

### 12. NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) (lines 1623-1905)

| line | len | member | says |
|---:|---:|---|---|
| 1637 | 268 | `static void nothingPastTheDefaultPoint() throws Exception` |  |

### 11. THE CURVE (0.7.1) (lines 1906-2092)

| line | len | member | says |
|---:|---:|---|---|
| 1920 | 79 | `static void theCurve(Game city)` |  |
| 2008 | 3 | `static boolean onCurve(DebtQuote q)` | True if this quote came off the sloped part of the curve. |
| 2012 | 6 | `static boolean onCurve(double rate)` |  |
| 2020 | 3 | `static double retailShopsOnSite(Game game, String sector)` | The shops on site, as Retail.plan() counts them for its "already building" (a Filling Station is the forecourts' order). |
| 2025 | 5 | `static void quietly(Runnable work)` | Runs a stretch of the game without its per-month console output. |
| 2031 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 2047 | 45 | `static void bookAndCompare(Game g, String type, double amount, int duration, double rounding, boolean mustBeOnCurve)` | Takes a quote, books it, and checks the books say what the quote said. |

