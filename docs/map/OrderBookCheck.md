# OrderBookCheck.java - 633 lines · 33 methods · 1 constants · harnesses

`ham/citybuildersim/OrderBookCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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
>  12. THE KNOWN ORDER (AP8): a side in known raw price order is halved to
>      find where a new order rests, and rests it where walking the side from
>      its best order would - warmed, then after each writer of the side (an
>      order, a fill, a trim, a cancel, the close, a roundoff out of raw
>      order, a split, a reform, a load, the autosave's copy), the book to
>      the bit against a twin that always walks.
> 
> The bond market's own use of the book - its participants, their orders,
> and a played month's audit closing through the trades - is BondCheck's,
> and the shares' since 0.7.12 round 2 ExchangeCheck's.
> Every fixture causes its condition.

**Uses:** [OrderBook](OrderBook.md) (146), [MoneyAudit](MoneyAudit.md) (7), [PriceLevel](PriceLevel.md) (1)

**Used by (1):** [BondCheck](BondCheck.md)

## Sections

| line | section |
|---:|---|
| 113 | 1. PRICE-TIME |
| 197 | 2. RESTING PRICE |
| 214 | 3. NOBODY OBLIGED |
| 236 | 4. PARTIAL FILLS |
| 257 | 5. NO SELF-TRADE |
| 272 | 6. CLEARING LIMITS |
| 293 | 7. THE RECORD |
| 318 | 8. A CURRENCY REFORM |
| 336 | 9. SAVE AND LOAD |
| 361 | 12. THE KNOWN ORDER (AP8) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 363 | `OrderBookCheck.BUY` | `OrderBook.Side.BUY, SELL = OrderBook.Side.SELL` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 51 | `static PrintStream out` |  |
| 66 | `final Map<String, Double> cash` |  |
| 67 | `int settled` |  |
| 366 | `static long knownRested, knownHalved, knownCompared, knownDiffered` | Every halving checked against its walk, over every twin of section 12. |
| 376 | `OrderBook held, bare` |  |
| 377 | `final Ledger heldLedger` |  |
| 378 | `final OrderBook.Probe probe` |  |
| 379 | `int fillsDiffered` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 586 | **type** `public class OrderBookCheck` | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. |
| 53 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 58 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 65 | 22 | **type** `static final class Ledger implements OrderBook.Clearing` | A clearing with cash and holdings per participant: each buyer pays out of its cash, each seller delivers out of its holding. |
| 69 | 5 | `Ledger give(String who, double money, double units)` _(in OrderBookCheck.Ledger)_ |  |
| 75 | 3 | `public double capacity(String who, OrderBook.Side side, double price)` _(in OrderBookCheck.Ledger)_ |  |
| 79 | 7 | `public void settle(String buyer, String seller, double quantity, double price)` _(in OrderBookCheck.Ledger)_ |  |
| 89 | 5 | `static Ledger rich(String...who)` | Everybody able to settle anything. |
| 95 | 17 | `public static void main(String[] args)` |  |

### 1. PRICE-TIME (lines 113-196)

| line | len | member | says |
|---:|---:|---|---|
| 115 | 32 | `static void floatingPrices()` |  |
| 148 | 18 | `static void staggeredBand()` |  |
| 167 | 29 | `static void priceTime()` |  |

### 2. RESTING PRICE (lines 197-213)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 14 | `static void restingPrice()` |  |

### 3. NOBODY OBLIGED (lines 214-235)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 19 | `static void nobodyObliged()` |  |

### 4. PARTIAL FILLS (lines 236-256)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 18 | `static void partialFills()` |  |

### 5. NO SELF-TRADE (lines 257-271)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 12 | `static void noSelfTrade()` |  |

### 6. CLEARING LIMITS (lines 272-292)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 18 | `static void clearingLimits()` |  |

### 7. THE RECORD (lines 293-317)

| line | len | member | says |
|---:|---:|---|---|
| 295 | 22 | `static void theRecord()` |  |

### 8. A CURRENCY REFORM (lines 318-335)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 15 | `static void currencyReform()` |  |

### 9. SAVE AND LOAD (lines 336-360)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 22 | `static void saveAndLoad()` |  |

### 12. THE KNOWN ORDER (AP8) (lines 361-633)

| line | len | member | says |
|---:|---:|---|---|
| 375 | 34 | **type** `static final class Twin` | Two books fed the same orders (AP8): `held` halves a side in known order, play's path, under a checked probe that also walks the side from its best order and compares where the two stop; `bare` always walks, as every ... |
| 381 | 13 | `Twin(String name)` _(in OrderBookCheck.Twin)_ |  |
| 395 | 1 | `void give(String who)` _(in OrderBookCheck.Twin)_ |  |
| 397 | 1 | `void attach()` _(in OrderBookCheck.Twin)_ |  |
| 399 | 5 | `void submit(String who, OrderBook.Side side, double price, double quantity)` _(in OrderBookCheck.Twin)_ |  |
| 405 | 1 | `void cash(String who, double money)` _(in OrderBookCheck.Twin)_ |  |
| 407 | 1 | `void withdrawAll()` _(in OrderBookCheck.Twin)_ |  |
| 410 | 7 | `static String fills(List<OrderBook.Fill> fills)` |  |
| 419 | 15 | `static String bits(OrderBook b)` | A book to the bit: every resting order on both sides in its place, and the month's record. |
| 436 | 11 | `static void same(String label, Twin t)` | The held book against the bare one after a writer: to the bit, every fill alike, every halving where its walk stops. |
| 454 | 8 | `static Twin warmed(String name)` | Both sides warmed the way the households post (AP8's count on slot 9: two in three of the orders a new bid walked past stood at its very price): bids in rising runs, in falling runs and many at one price, asks above t... |
| 464 | 1 | `static double cents(int n)` | A price level as one double however it is reached: n hundredths. |
| 467 | 8 | `static void more(Twin t, double scale)` | A few orders after a writer, on both sides, between and past their levels. |
| 477 | 4 | `static int indexOf(List<OrderBook.Order> side, String who)` | Where a participant's first order stands on a side, -1 if it has none. |
| 483 | 4 | `static void roundoff(Twin t, double k, double j)` | The binade's roundoff: a bid just under the edge, then a higher one just over it, within its band (k, j). |
| 488 | 145 | `static void knownOrder()` |  |

