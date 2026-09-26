# BooksCheck.java - 241 lines · 3 methods · 0 constants · harnesses

`ham/citybuildersim/BooksCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> Verifies a sector's income statement and balance sheet, off the template.
> Not part of the game.
> 
> REWRITTEN FOR THE SECTOR TEMPLATE (2026-09-11). This used to drive an
> IndustrialHandler by hand - setFoodDemand, setFoodPrice, computeMonthlyReport
> - and read its report lines back. There is no handler now: a sector's month
> is a LEDGER of trades struck into a STATEMENT at the top of the next month
> (see Sector.strike and Sector.bank), and the only way revenue gets onto a
> statement is a Trade in the ledger. So the fixture books the trades a month
> of selling would have produced and checks the statement is their sum, the
> bills' sum, and nothing else.

**Uses:** [Good](Good.md) (9), [Sector](Sector.md) (5), [FoodIndustry](FoodIndustry.md) (4), [Trade](Trade.md) (4), [Sectors](Sectors.md) (3), [BalanceSheet](BalanceSheet.md) (3), [Markets](Markets.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [Statement](Statement.md) (1), [BuildingType](BuildingType.md) (1)

## Sections

| line | section |
|---:|---|
| 77 | · income statement |
| 120 | · the sales tax lands on the statement |
| 133 | · balance sheet |
| 154 | · the sheet must move with the market price |
| 168 | · ratios must not blow up on an empty business |
| 179 | · book value comes off the real templates |
| 210 | · the tax is paid once, by the business |

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 226 | **type** `public class BooksCheck` | Verifies a sector's income statement and balance sheet, off the template. |
| 20 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 25 | 6 | `static void check(String label, double actual, double expected)` |  |
| 32 | 209 | `public static void main(String[] args)` |  |

