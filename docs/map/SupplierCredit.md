# SupplierCredit.java - 255 lines · 21 methods · 0 constants · model

`ham/citybuildersim/SupplierCredit.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> What a buyer owes its suppliers for stock they let it have on credit (0.7.44): the grocers' trade credit, struck and repaid a month at a time.
> 
> WHY. Since 0.7.12 a firm restocks only as far as its cash and the credit it
> can get reach (Sector, BUY ONLY WHAT IT CAN PAY FOR), and since 0.7.43 the
> shops sell the baskets on their shelf and no more (Retail.sellOwnPriced()).
> Together they made a trap the ensemble found in 11.9% of the autopilot's
> months after month 240 (runs/diag-0743.md, section 5): a grocer whose till
> ran dry bought nothing, so its shelf emptied, so it sold nothing, so its till
> stayed dry - delivering under 5% of what was wanted for two or three months
> until a working-capital loan refilled the shelf, and again ten months later.
> In 94% of those months the city had the shops; they did not have the cash.
> The stock bought this month is sold next month (Markets.clearMonth(): the
> sale, then the restock), so a till paid out of last month's sale cannot buy
> the stock for a sale that never happened. A real grocer does not pay for its
> stock on delivery: its suppliers wait for the money until the stock has sold
> (trade credit - in the US and Canada about a month's terms, "net 30").
> 
> THE RULE, EACH MONTH:
> 
>   1. AT THE CLEARING, what the suppliers will wait for - the LIMIT, the
>      buyer's own (Retail.supplierCreditLimit(): the stock for a month of
>      the sales it expects, at what that stock costs to bring in) - is
>      opened beside the till the buyer will have at the settle (open(), from
>      EconomyManager.purchaseBudget(), which adds the limit to what its till
>      and its lender cover). So the restock is still bounded by its cash and
>      its credit: the credit now includes its suppliers'.
>   2. AS IT IS FILLED, every order for stock is noted against the supplier
>      that filled it - the world for an import - and the goods the credit
>      covers (the shelf) apart from the rest (the fleet) (note(), from
>      Sector.bookPurchase()).
>   3. AT THE CLOSE, what it bought on credit is the part of the stock bill
>      its till cannot cover, never more than the limit nor than the covered
>      goods' bill: min(limit, covered, max(0, stock bill - max(0, till)))
>      (close()). A grocer whose till covers its stock takes none. It is
>      shared over the suppliers of the covered goods in proportion to what
>      each filled.
>   4. AT THE NEXT STRIKE (EconomyManager.settleSupplierCredit()), the
>      statement charges the whole bill as it always has; the till pays all of
>      it but the credit, and repays what the strike before left owed - the
>      stock that has now been sold, paid out of the takings of its sale. Each
>      local supplier is paid less by its share now and is paid what it was
>      owed; the world's share crosses the money audit as a financial flow in
>      and, a month later, out (MoneyAudit, "SupplierCredit").
> 
> THE CREDIT IS THE STOCK'S. The limit is added to what the till and the
> lender cover after that is floored at nothing, not netted against the
> bills they could not: a grocer whose wages and interest outrun its till
> still gets its month's stock on its suppliers' credit, and still defaults
> on the wages and the interest, as the 0.7.12 rule says it must. The stock
> pays for itself - its sale brings in its cost at least RETAIL_MARKUP over,
> and repays the suppliers at the next strike - and suppliers in fact lend
> on those terms because the goods are theirs until paid for (retention of
> title; a purchase-money security interest in inventory ranks first on it
> and its proceeds, US UCC 9-324(b)). Measured the other way first: with the
> limit netted against the bills (and then struck at the shelf price, the
> takings, rather than at cost), a grocer that could not pay its staff out
> of its margin - over-built for a city whose roads and power hand over 40%
> of its coverage - still emptied its shelf, 2.4% of the months of the worst
> seed and a hunger rate of .106, against none and .052 this way (seed 1 of
> the ensemble's autopilot, month 240 on; 0.7.43: 35% and .409).
> ... (22 more lines in the source)

**Uses:** [Good](Good.md) (5)

**Used by (8):** [BondCheck](BondCheck.md), [EconomyManager](EconomyManager.md), [Markets](Markets.md), [MoneyAudit](MoneyAudit.md), [Retail](Retail.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 121 | · the clearing |
| 164 | · the strike |
| 179 | · readings |
| 213 | · save, reset, reform |

## Fields (state)

| line | field | says |
|---:|---|---|
| 98 | `private final Set<Good> covers` | The goods the credit covers: an order for anything else the buyer keeps (its fleet) is paid from its till as before. |
| 101 | `private final Map<String, Double> owed` | What it owes each supplier - a sector's key, or Trade.WORLD - for the stock it bought on credit at the clearing the last strike banked; the next strike repays it. |
| 103 | `private final Map<String, Double> repaid` | ...what the last strike repaid each, the strike before's credit. |
| 105 | `private final Map<String, Double> bought` | ...and what it bought on credit at this month's clearing, by supplier: owed from the next strike. |
| 108 | `private boolean open` | The clearing's, opened and closed inside it - never saved. |
| 109 | `private double limit, till, stockBill` |  |
| 110 | `private final Map<String, Double> coveredBill` |  |
| 112 | `private double rLimit` | The last clearing's reading, for the screens: what the suppliers would have waited for. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 95 | 161 | **type** `public final class SupplierCredit` | What a buyer owes its suppliers for stock they let it have on credit (0.7.44): the grocers' trade credit, struck and repaid a month at a time. |
| 114 | 3 | `public SupplierCredit(Set<Good> covers)` |  |
| 119 | 1 | `public boolean covers(Good g)` | Whether the credit covers this good. |

### the clearing (lines 121-163)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 9 | `public double open(double limit, double tillAtSettle)` | Opens the clearing's credit: what the suppliers will wait for, and the till the buyer will have at the settle before it borrows. |
| 140 | 5 | `public void note(String supplier, Good g, double value)` | An order for stock, filled: by whom, of what, for how much. |
| 151 | 12 | `public void close()` | Closes the clearing: what it bought on credit, the part of the stock bill its till cannot cover, at most the limit and the covered goods' bill, shared over their suppliers by what each filled. |

### the strike (lines 164-178)

| line | len | member | says |
|---:|---:|---|---|
| 171 | 7 | `public void strike()` | The strike: what was owed is repaid, and what the last clearing bought on credit is owed. |

### readings (lines 179-212)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 1 | `public double owedTotal()` | What it owes all its suppliers now: the last strike's credit, which the next strike repays. |
| 184 | 1 | `public double owedTo(String supplier)` | ...one of them. |
| 186 | 1 | `public double repaidTotal()` | What the last strike repaid, all its suppliers. |
| 188 | 1 | `public double repaidTo(String supplier)` | ...one of them. |
| 190 | 1 | `public double takenTotal()` | What the last strike took on credit - what it now owes, since the strike moved it there. |
| 192 | 1 | `public double boughtTotal()` | What this month's clearing bought on credit, owed from the next strike. |
| 194 | 1 | `public double boughtFrom(String supplier)` | ...from one supplier. |
| 196 | 6 | `public Set<String> suppliers()` | The suppliers it owes, or was repaid at the last strike, or bought from on credit since. |
| 203 | 1 | `public double getLimit()` | What the last clearing's suppliers would have waited for. |
| 205 | 1 | `public double openLimit()` | ...and what they will wait for in the clearing open now; nothing outside one. |
| 207 | 5 | `private static double sum(Map<String, Double> m)` |  |

### save, reset, reform (lines 213-255)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 6 | `public void save(Map<String, Double> extras, String prefix)` | Into the owner's extras, by name: each map under its own prefix, keyed by supplier. |
| 224 | 13 | `public void restore(Map<String, Double> extras, String prefix)` | ...and back. |
| 239 | 8 | `public void clear()` | Nothing owed, nothing bought. |
| 249 | 6 | `public void redenominate(double scale)` | Every sum in the new unit. |

