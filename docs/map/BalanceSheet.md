# BalanceSheet.java - 201 lines · 30 methods · 0 constants · model

`ham/citybuildersim/BalanceSheet.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> A simple balance sheet for one business in the city.
> 
> Deliberately built as its own class rather than a handful of fields on
> IndustrialHandler: the plan is for every sector to get one - real estate,
> retail, utilities - and the whole point of doing this before adding more
> industries is that the second, third and fourth set of books should be a
> few lines of wiring rather than a copy of this logic.
> 
> Structure follows the accounting identity:
> 
>     ASSETS = LIABILITIES + EQUITY
> 
> Equity is not stored. It is derived as assets minus liabilities, which means
> the sheet balances by construction and can never be shown out of balance.
> That is the "make the formula happy" version. When retained earnings become
> a real quantity - a business that can be undercapitalised, issue shares, or
> go bankrupt - equity becomes a stored figure and this derivation becomes a
> CHECK against it instead.
> 
> Placeholders as of now: land and bonds payable are always zero, because the
> game does not model land ownership or per-business debt yet. They are here so
> the layout does not have to change when it does.

**Used by (6):** [BooksCheck](BooksCheck.md), [CreditCheck](CreditCheck.md), [EconomyManager](EconomyManager.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 31 | · ASSETS |
| 64 | · LIABILITIES |
| 173 | · RATIOS |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `private final String owner` |  |
| 34 | `private double cash` | Cash reserve held by this business. |
| 43 | `private double inventory` | Stock on hand, valued at the current market price. |
| 44 | `private int inventoryUnits` |  |
| 45 | `private double inventoryUnitPrice` |  |
| 48 | `private double land` | Not modelled yet - the game has no concept of land ownership. |
| 54 | `private double tradeReceivables` | What its buyers owe it for stock it let them have on credit, which the next strike collects (0.7.44; SupplierCredit): a current asset. |
| 62 | `private double buildings` | Buildings at construction cost: cash paid plus materials at market. |
| 67 | `private double bondsPayable` | Not modelled yet - city debt is not attributed to individual businesses. |
| 74 | `private double tradePayables` | What it owes its suppliers for stock they let it have on credit, which the next strike pays (0.7.44; SupplierCredit): a current liability, the first this sheet has had. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 175 | **type** `public class BalanceSheet` | A simple balance sheet for one business in the city. |

### ASSETS (lines 31-63)

### LIABILITIES (lines 64-172)

| line | len | member | says |
|---:|---:|---|---|
| 76 | 3 | `public BalanceSheet(String owner)` |  |
| 81 | 4 | `public BalanceSheet setCash(double cash)` | setters (chained) |
| 87 | 6 | `public BalanceSheet setInventory(int units, double unitPrice)` | Stock valued at market: units on hand times the current price per unit. |
| 98 | 6 | `public BalanceSheet setInventoryValue(double value)` | Stock already valued - a sector holding several goods sums them itself. |
| 105 | 4 | `public BalanceSheet setLand(double land)` |  |
| 110 | 4 | `public BalanceSheet setBuildings(double buildings)` |  |
| 115 | 4 | `public BalanceSheet setBondsPayable(double bondsPayable)` |  |
| 120 | 4 | `public BalanceSheet setTradeReceivables(double owedToIt)` |  |
| 125 | 4 | `public BalanceSheet setTradePayables(double owedByIt)` |  |
| 131 | 1 | `public String getOwner()` | getters |
| 132 | 1 | `public double getCash()` |  |
| 133 | 1 | `public double getInventory()` |  |
| 134 | 1 | `public int getInventoryUnits()` |  |
| 135 | 1 | `public double getInventoryUnitPrice()` |  |
| 136 | 1 | `public double getLand()` |  |
| 137 | 1 | `public double getBuildings()` |  |
| 138 | 1 | `public double getBondsPayable()` |  |
| 139 | 1 | `public double getTradeReceivables()` |  |
| 140 | 1 | `public double getTradePayables()` |  |
| 143 | 3 | `public double getCurrentAssets()` | derived |
| 147 | 3 | `public double getNonCurrentAssets()` |  |
| 151 | 3 | `public double getTotalAssets()` |  |
| 156 | 3 | `public double getCurrentLiabilities()` | What is due within the year: what it owes its suppliers, since 0.7.44 (its loans and bonds are not split by term here). |
| 160 | 3 | `public double getTotalLiabilities()` |  |
| 165 | 3 | `public double getEquity()` | The balancing figure. |
| 169 | 3 | `public double getTotalLiabilitiesAndEquity()` |  |

### RATIOS (lines 173-201)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 4 | `public double getCurrentRatio()` | Current ratio. |
| 186 | 4 | `public double getDebtToAssets()` |  |
| 191 | 4 | `public double getInventoryShareOfAssets()` |  |
| 197 | 4 | `public double getReturnOnAssets(double netIncome)` | Monthly return on assets. |

