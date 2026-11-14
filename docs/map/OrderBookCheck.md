# OrderBookCheck.java - 333 lines · 17 methods · 0 constants · harnesses

`ham/citybuildersim/OrderBookCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The limit-order book (0.7.12), on its own: the rules any instrument trades
> by, proved on a book that knows nothing about what it trades.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Order book for both" - "its
> availability to get filled both buy and sell"):
> 
>   1. PRICE-TIME PRIORITY: the best price first, and at one price the
>      order that came first.
>   2. A TRADE IS AT THE RESTING ORDER'S PRICE, whichever side arrives.
>   3. NOBODY IS OBLIGED TO TRADE: a sell with no buyer rests unfilled, a
>      buy with no seller rests, and the close counts who waited.
>   4. PARTIAL FILLS: an order fills as far as the other side reaches and
>      the rest keeps its place, or rests at its own limit.
>   5. NO TRADE WITH ONESELF, and the order passed over keeps its place.
>   6. THE CLEARING'S LIMITS: a buyer that cannot pay for all of it, or a
>      seller that cannot deliver, fills as far as it can and is trimmed -
>      so every trade the book reports is one that settled.
>   7. THE RECORD: posted, filled, volume, turnover, the last price, the
>      depth by level.
>   8. A CURRENCY REFORM: quantities move for an instrument counted in
>      money, prices for one that is not.
>   9. SAVE AND LOAD: a book with resting orders round-trips through Gson,
>      and the next order after a reload still arrives after them.
>  10. FLOATING PRICES: a one-step roundoff crosses and keeps time priority,
>      in both money units; a gap past the model's allowance never crosses.
> 
> The bond market's own use of the book - its participants, their orders,
> and a played month's audit closing through the trades - is BondCheck's,
> and the shares' since 0.7.12 round 2 ExchangeCheck's.
> Every fixture causes its condition.

**Uses:** [OrderBook](OrderBook.md) (115), [PriceLevel](PriceLevel.md) (1), [MoneyAudit](MoneyAudit.md) (1)

## Sections

| line | section |
|---:|---|
| 105 | 1. PRICE-TIME |
| 170 | 2. RESTING PRICE |
| 187 | 3. NOBODY OBLIGED |
| 209 | 4. PARTIAL FILLS |
| 230 | 5. NO SELF-TRADE |
| 245 | 6. CLEARING LIMITS |
| 266 | 7. THE RECORD |
| 291 | 8. A CURRENCY REFORM |
| 309 | 9. SAVE AND LOAD |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `static int fails` |  |
| 45 | `static PrintStream out` |  |
| 60 | `final Map<String, Double> cash` |  |
| 61 | `int settled` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 292 | **type** `public class OrderBookCheck` | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. |
| 47 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 52 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 59 | 22 | **type** `static final class Ledger implements OrderBook.Clearing` | A clearing with cash and holdings per participant: each buyer pays out of its cash, each seller delivers out of its holding. |
| 63 | 5 | `Ledger give(String who, double money, double units)` _(in OrderBookCheck.Ledger)_ |  |
| 69 | 3 | `public double capacity(String who, OrderBook.Side side, double price)` _(in OrderBookCheck.Ledger)_ |  |
| 73 | 7 | `public void settle(String buyer, String seller, double quantity, double price)` _(in OrderBookCheck.Ledger)_ |  |
| 83 | 5 | `static Ledger rich(String...who)` | Everybody able to settle anything. |
| 89 | 15 | `public static void main(String[] args)` |  |

### 1. PRICE-TIME (lines 105-169)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 32 | `static void floatingPrices()` |  |
| 140 | 29 | `static void priceTime()` |  |

### 2. RESTING PRICE (lines 170-186)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 14 | `static void restingPrice()` |  |

### 3. NOBODY OBLIGED (lines 187-208)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 19 | `static void nobodyObliged()` |  |

### 4. PARTIAL FILLS (lines 209-229)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 18 | `static void partialFills()` |  |

### 5. NO SELF-TRADE (lines 230-244)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 12 | `static void noSelfTrade()` |  |

### 6. CLEARING LIMITS (lines 245-265)

| line | len | member | says |
|---:|---:|---|---|
| 247 | 18 | `static void clearingLimits()` |  |

### 7. THE RECORD (lines 266-290)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 22 | `static void theRecord()` |  |

### 8. A CURRENCY REFORM (lines 291-308)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 15 | `static void currencyReform()` |  |

### 9. SAVE AND LOAD (lines 309-333)

| line | len | member | says |
|---:|---:|---|---|
| 311 | 22 | `static void saveAndLoad()` |  |

