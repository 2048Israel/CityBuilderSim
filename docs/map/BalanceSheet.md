# BalanceSheet.java - 175 lines · 26 methods · 0 constants · model

`ham/citybuildersim/BalanceSheet.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Used by (4):** [BooksCheck](BooksCheck.md), [CreditCheck](CreditCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md)

## Sections

| line | section |
|---:|---|
| 31 | · ASSETS |
| 58 | · LIABILITIES |
| 148 | · RATIOS |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `private final String owner` |  |
| 34 | `private double cash` | Cash reserve held by this business. |
| 43 | `private double inventory` | Stock on hand, valued at the current market price. |
| 44 | `private int inventoryUnits` |  |
| 45 | `private double inventoryUnitPrice` |  |
| 48 | `private double land` | Not modelled yet - the game has no concept of land ownership. |
| 56 | `private double buildings` | Buildings at construction cost: cash paid plus materials at market. |
| 61 | `private double bondsPayable` | Not modelled yet - city debt is not attributed to individual businesses. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 149 | **type** `public class BalanceSheet` | A simple balance sheet for one business in the city. |

### ASSETS (lines 31-57)

### LIABILITIES (lines 58-147)

| line | len | member | says |
|---:|---:|---|---|
| 63 | 3 | `public BalanceSheet(String owner)` |  |
| 68 | 4 | `public BalanceSheet setCash(double cash)` | setters (chained) |
| 74 | 6 | `public BalanceSheet setInventory(int units, double unitPrice)` | Stock valued at market: units on hand times the current price per unit. |
| 85 | 6 | `public BalanceSheet setInventoryValue(double value)` | Stock already valued - a sector holding several goods sums them itself. |
| 92 | 4 | `public BalanceSheet setLand(double land)` |  |
| 97 | 4 | `public BalanceSheet setBuildings(double buildings)` |  |
| 102 | 4 | `public BalanceSheet setBondsPayable(double bondsPayable)` |  |
| 108 | 1 | `public String getOwner()` | getters |
| 109 | 1 | `public double getCash()` |  |
| 110 | 1 | `public double getInventory()` |  |
| 111 | 1 | `public int getInventoryUnits()` |  |
| 112 | 1 | `public double getInventoryUnitPrice()` |  |
| 113 | 1 | `public double getLand()` |  |
| 114 | 1 | `public double getBuildings()` |  |
| 115 | 1 | `public double getBondsPayable()` |  |
| 118 | 3 | `public double getCurrentAssets()` | derived |
| 122 | 3 | `public double getNonCurrentAssets()` |  |
| 126 | 3 | `public double getTotalAssets()` |  |
| 131 | 3 | `public double getCurrentLiabilities()` | Nothing is due within the year yet - no payables, no short-term debt. |
| 135 | 3 | `public double getTotalLiabilities()` |  |
| 140 | 3 | `public double getEquity()` | The balancing figure. |
| 144 | 3 | `public double getTotalLiabilitiesAndEquity()` |  |

### RATIOS (lines 148-175)

| line | len | member | says |
|---:|---:|---|---|
| 155 | 4 | `public double getCurrentRatio()` | Current ratio. |
| 160 | 4 | `public double getDebtToAssets()` |  |
| 165 | 4 | `public double getInventoryShareOfAssets()` |  |
| 171 | 4 | `public double getReturnOnAssets(double netIncome)` | Monthly return on assets. |

