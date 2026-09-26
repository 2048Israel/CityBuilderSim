# OrderBookCheck.java - 297 lines · 16 methods · 0 constants · harnesses

`ham/citybuildersim/OrderBookCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
> 
> The bond market's own use of the book - its participants, their orders,
> and a played month's audit closing through the trades - is BondCheck's,
> and the shares' since 0.7.12 round 2 ExchangeCheck's.
> Every fixture causes its condition.

**Uses:** [OrderBook](OrderBook.md) (92)

## Sections

| line | section |
|---:|---|
| 102 | 1. PRICE-TIME |
| 134 | 2. RESTING PRICE |
| 151 | 3. NOBODY OBLIGED |
| 173 | 4. PARTIAL FILLS |
| 194 | 5. NO SELF-TRADE |
| 209 | 6. CLEARING LIMITS |
| 230 | 7. THE RECORD |
| 255 | 8. A CURRENCY REFORM |
| 273 | 9. SAVE AND LOAD |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `static int fails` |  |
| 43 | `static PrintStream out` |  |
| 58 | `final Map<String, Double> cash` |  |
| 59 | `int settled` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 258 | **type** `public class OrderBookCheck` | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. |
| 45 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 50 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 57 | 22 | **type** `static final class Ledger implements OrderBook.Clearing` | A clearing with cash and holdings per participant: each buyer pays out of its cash, each seller delivers out of its holding. |
| 61 | 5 | `Ledger give(String who, double money, double units)` _(in OrderBookCheck.Ledger)_ |  |
| 67 | 3 | `public double capacity(String who, OrderBook.Side side, double price)` _(in OrderBookCheck.Ledger)_ |  |
| 71 | 7 | `public void settle(String buyer, String seller, double quantity, double price)` _(in OrderBookCheck.Ledger)_ |  |
| 81 | 5 | `static Ledger rich(String...who)` | Everybody able to settle anything. |
| 87 | 14 | `public static void main(String[] args)` |  |

### 1. PRICE-TIME (lines 102-133)

| line | len | member | says |
|---:|---:|---|---|
| 104 | 29 | `static void priceTime()` |  |

### 2. RESTING PRICE (lines 134-150)

| line | len | member | says |
|---:|---:|---|---|
| 136 | 14 | `static void restingPrice()` |  |

### 3. NOBODY OBLIGED (lines 151-172)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 19 | `static void nobodyObliged()` |  |

### 4. PARTIAL FILLS (lines 173-193)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 18 | `static void partialFills()` |  |

### 5. NO SELF-TRADE (lines 194-208)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 12 | `static void noSelfTrade()` |  |

### 6. CLEARING LIMITS (lines 209-229)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 18 | `static void clearingLimits()` |  |

### 7. THE RECORD (lines 230-254)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 22 | `static void theRecord()` |  |

### 8. A CURRENCY REFORM (lines 255-272)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 15 | `static void currencyReform()` |  |

### 9. SAVE AND LOAD (lines 273-297)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 22 | `static void saveAndLoad()` |  |

