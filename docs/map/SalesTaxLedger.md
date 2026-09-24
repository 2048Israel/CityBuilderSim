# SalesTaxLedger.java - 215 lines · 21 methods · 1 constants · model

`ham/citybuildersim/SalesTaxLedger.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The month's sales tax, as tax payable less input tax credits.
> 
> WHY THIS REPLACED A ONE-LINE SALES TAX
> 
> calculateSalesTax() was three lines: food-plant revenue, store revenue, and
> the retail import tax, each times the one city rate. It taxed the same food
> TWICE - once when the plant sold it and again when the store did - and it
> never touched Heavy Industry or Mining at all, so steel and ore moved
> through the economy untaxed while a loaf of bread was charged at every
> step.
> 
> Jerus's fix, in his words: "just like real life... it's taxed, all of it,
> just that there is tax credits - if you bought stuff with 3k tax then what
> you sell has a 3k tax credit, basically the HST receivable and payable
> thing." So every sector charges tax on what it sells and claims back the
> tax embedded in what it bought. The city collects the difference, which is
> the tax on the VALUE THE SECTOR ADDED.
> 
> ONE DELIBERATE DEPARTURE FROM REAL HST: the rate follows the PRODUCER, not
> the product, so every per-sector dial is a real lever. A sector buying at a
> high rate and selling at a low one can show a NEGATIVE net remittance;
> that is a refund, it is correct, and the ledger does not floor it.
> 
> EXPORTS ARE ZERO-RATED, and the credits behind them stay claimable. IMPORTS
> ARE TAXED AND CREDITABLE, so a sector cannot undercut a local supplier by
> buying from outside. THE CITY IS EXEMPT, and so is residential rent (see
> Good.taxExempt()).
> 
> SINCE THE SECTOR TEMPLATE (2026-09-11) the ledger is STRUCK FROM THE
> TRADES rather than written by hand per sector - see
> EconomyManager.settleSalesTax(): a sale to a local buyer at the seller's
> rate, an export zero-rated, a purchase credited at the supplier's rate, an
> import charged at the buyer's. Keyed by the sector's name, so a seventh
> sector is a seventh row and nothing here changes.

**Uses:** [TaxPolicy](TaxPolicy.md) (2)

**Used by (5):** [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 58 | WHAT HAPPENED THIS MONTH |
| 116 | WHAT IT COMES TO |
| 157 | · save and restore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `SalesTaxLedger.TAXABLE_SALES` | `0, IMPORT_TAX = 1, ZERO_RATED = 2, CREDITED_INPUT = 3, PAYABLE = 4, CREDIT = ...` | slots in a row |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `private final Map<String, double[]> rows` |  |
| 52 | `private double totalRemitted` |  |
| 161 | `public String sector` |  |
| 162 | `public double taxableSales, importTax, zeroRated, creditedInput, payable, credit` |  |
| 166 | `public double totalRemitted` |  |
| 167 | `public List<Row> rows` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 172 | **type** `public class SalesTaxLedger` | The month's sales tax, as tax payable less input tax credits. |
| 54 | 3 | `private double[] row(String sector)` |  |

### WHAT HAPPENED THIS MONTH (lines 58-115)

| line | len | member | says |
|---:|---:|---|---|
| 63 | 4 | `public void recordSales(String sector, double revenue)` | Sales to anyone inside the city. |
| 73 | 4 | `public void recordExport(String sector, double revenue)` | Sales out of the city. |
| 84 | 4 | `public void recordInputTax(String sector, double taxPaid)` | Tax the sector actually PAID on its inputs, recoverable in full. |
| 94 | 7 | `public double chargeImport(String sector, double landedCost, TaxPolicy policy)` | Tax on goods bought from outside the city, at the BUYER's rate: charged on the way in and credited, which nets to zero for a sector that resells locally - the tax lands on the final sale either way. |
| 103 | 12 | `public String deepestRefund()` | The sector in the biggest refund position this month, or null if none is. |

### WHAT IT COMES TO (lines 116-156)

| line | len | member | says |
|---:|---:|---|---|
| 121 | 12 | `public double settle(TaxPolicy policy)` | Strikes the month's tax. |
| 134 | 1 | `public double getTotalRemitted()` |  |
| 135 | 1 | `public double getPayable(String s)` |  |
| 136 | 1 | `public double getCredit(String s)` |  |
| 137 | 1 | `public double getNet(String s)` |  |
| 138 | 1 | `public double getTaxableSales(String s)` |  |
| 139 | 1 | `public double getZeroRated(String s)` |  |
| 140 | 1 | `public double getImportTax(String s)` |  |
| 142 | 1 | `private boolean has(String s)` |  |
| 145 | 1 | `public boolean isInRefund(String s)` | True when the city owes this sector rather than the other way round. |
| 152 | 4 | `public void startMonth()` | Clears the month. |

### save and restore (lines 157-215)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 4 | **type** `public static final class Row` | One sector's row, as the save carries it. |
| 165 | 4 | **type** `public static final class State` |  |
| 170 | 17 | `public State toState()` |  |
| 189 | 16 | `public boolean restore(State s)` |  |
| 206 | 1 | `public void reset()` |  |
| 209 | 6 | `public void redenominate(double scale)` | The month's VAT working, in the new unit. |

