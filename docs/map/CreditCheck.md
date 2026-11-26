# CreditCheck.java - 2,245 lines · 19 methods · 1 constants · harnesses

`ham/citybuildersim/CreditCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Verifies private-sector credit: pricing, origination, rollover, cash conservation.
> 
> FIXTURES (FS9). The ban's town (section 10): a retailer held at a till of nought - below
> what any shop costs - so what the law reads, that a banned retailer builds nothing on credit
> it did not have, is a law about credit and not about a till the path happened to leave; its
> shop's cost is read from the model (BusinessInvestment.getCostOf()) and the orders the advisor
> judges are watched (Game.watchOrders()), and the other side is shown: with the ban re-armed
> and a till that covers two shops it builds from the till and borrows nothing. The past-the-line
> city (section 12): stood at once, no month played before the claims - the builders' plant
> (buildStack), the bank's branch counted and capitalised through its own door (Bank.openBranches
> and injectCapital), the builders' till set (setSectorCash) and a due claim set above the till by
> more than the builders take in in a month. WHY: the played cities moved under the numbers the
> old fixtures were sized on, which a path gave and a path took away.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (134), [PriceLevel](PriceLevel.md) (27), [Game](Game.md) (22), [DebtManager](DebtManager.md) (16), [Sectors](Sectors.md) (15), [Bank](Bank.md) (13), [GameFiles](GameFiles.md) (7), [DebtQuote](DebtQuote.md) (7), [LongPlaytest](LongPlaytest.md) (7), [MoneyAudit](MoneyAudit.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [BusinessLoan](BusinessLoan.md) (4), [BusinessInvestment](BusinessInvestment.md) (3), [InterimLoan](InterimLoan.md) (3), [Debt](Debt.md) (3), [Founding](Founding.md) (2), [BuildingManager](BuildingManager.md) (2), [Mortgage](Mortgage.md) (2), [LongTermBond](LongTermBond.md) (2), [Sector](Sector.md) (1), [FoodIndustry](FoodIndustry.md) (1), [Trade](Trade.md) (1), [Good](Good.md) (1), [BalanceSheet](BalanceSheet.md) (1), [TempDirs](TempDirs.md) (1), [PriceLevelCheck](PriceLevelCheck.md) (1), [EconomyManager](EconomyManager.md) (1), [Equity](Equity.md) (1), [BusinessDebt](BusinessDebt.md) (1), [SectorBooks](SectorBooks.md) (1)... and 2 more

## Sections

| line | section |
|---:|---|
| 49 | · 1. pricing |
| 170 | · · ...and it pays its fee out of the proceeds (0.7.7) |
| 180 | · 2. rate is fixed at issue |
| 198 | · 3. origination |
| 217 | · · ...and it stops at the borrower's own insolvency line |
| 239 | · · ...and the ceiling is BELOW the line, by a real gap |
| 302 | · · ...and a shut lender lends nothing, from either desk |
| 326 | · · ...and a repeat defaulter is shut out for longer |
| 369 | · · ...and the record is priced, not only banned on |
| 397 | · · ...and a bankruptcy forgives the overdraft, once |
| 439 | · 4. maturity and rollover |
| 473 | · 5. cash conservation |
| 512 | · 6. balance sheet integration |
| 522 | · 7. the spiral guard |
| 554 | · the CITY's debt market, repriced |
| 666 | · 7. the quote IS the deal |
| 780 | · 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR |
| 844 | · 10. THE STORY THAT BROKE, END TO END |
| 932 | · 10. THE BAN IS ONE BAN |
| 952 | · · MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY. |
| 1276 | 13. CAN'T PAY MEANS DEFAULT (0.7.12, round 4) |
| 1501 | 15. A NEW SECTOR'S FIRST READING (0.7.12, round 7) |
| 1629 | 16. THE SHORTFALL DESK'S BOND, GROSSED UP (0.7.12, round 7) |
| 1753 | 12. NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) |
| 2056 | 11. THE CURVE (0.7.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `CreditCheck.IND` | `Sectors.INDUSTRY` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 20 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 2228 | **type** `public class CreditCheck` | Verifies private-sector credit: pricing, origination, rollover, cash conservation. |
| 22 | 6 | `static void check(String label, double actual, double expected)` |  |
| 29 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 37 | 9 | `static double priced(DebtManager m, double cash)` | The standing rate at a given cash position, leaving the market as it found it. |
| 47 | 1228 | `public static void main(String[] args) throws Exception` |  |

### 13. CAN'T PAY MEANS DEFAULT (0.7.12, round 4) (lines 1276-1500)

| line | len | member | says |
|---:|---:|---|---|
| 1291 | 12 | `static BusinessDebtManager.BondBook bondsOf(String s, double[] face)` |  |
| 1304 | 196 | `static void cantPayMeansDefault()` |  |

### 15. A NEW SECTOR'S FIRST READING (0.7.12, round 7) (lines 1501-1628)

| line | len | member | says |
|---:|---:|---|---|
| 1519 | 109 | `static void firstReading() throws Exception` |  |

### 16. THE SHORTFALL DESK'S BOND, GROSSED UP (0.7.12, round 7) (lines 1629-1752)

| line | len | member | says |
|---:|---:|---|---|
| 1638 | 14 | `static BusinessDebtManager.BondDesk deskOf(double bondPart, double cost)` |  |
| 1653 | 20 | `static void shortfallBondGrossedUp()` |  |
| 1675 | 11 | `static double nothingLeft()` | A sector owing $1,000 of loans and a $200 interim loan with nothing left: what of the interim loan the backstop keeps. |
| 1687 | 65 | `static void creditLinesStayOpen()` |  |

### 12. NOTHING PAST THE DEFAULT POINT (0.7.12, round 2) (lines 1753-2055)

| line | len | member | says |
|---:|---:|---|---|
| 1767 | 288 | `static void nothingPastTheDefaultPoint() throws Exception` |  |

### 11. THE CURVE (0.7.1) (lines 2056-2245)

| line | len | member | says |
|---:|---:|---|---|
| 2070 | 82 | `static void theCurve(Game city)` |  |
| 2161 | 3 | `static boolean onCurve(DebtQuote q)` | True if this quote came off the sloped part of the curve. |
| 2165 | 6 | `static boolean onCurve(double rate)` |  |
| 2173 | 3 | `static double retailShopsOnSite(Game game, String sector)` | The shops on site, as Retail.plan() counts them for its "already building" (a Filling Station is the forecourts' order). |
| 2178 | 5 | `static void quietly(Runnable work)` | Runs a stretch of the game without its per-month console output. |
| 2184 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 2200 | 45 | `static void bookAndCompare(Game g, String type, double amount, int duration, double rounding, boolean mustBeOnCurve)` | Takes a quote, books it, and checks the books say what the quote said. |

