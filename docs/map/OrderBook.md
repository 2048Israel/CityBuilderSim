# OrderBook.java - 433 lines · 50 methods · 1 constants · model

`ham/citybuildersim/OrderBook.java` - generated 2026-09-27 by CodeMap; line numbers are as of that run.

> A limit-order book for one instrument: buy and sell orders from named
> participants, each a price and a quantity, matched by price-time priority.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Jerus, 2026-09-24: "Order book for both" - everyone posts buy and sell
> orders at prices, orders fill only when they meet, and the rest wait; "its
> availability to get filled both buy and sell". Until 0.7.12 every
> secondary market in the city was a dealer: the bank's desk quoted the
> shares (Exchange) and bought the city's paper back at the curve
> (HouseholdBalance.PaperDesk), so a seller always found a buyer at the
> desk's price and nobody ever waited. The corporate bonds of 0.7.12 are the
> first thing that trades here instead; the shares move onto the same book
> in round 2, which is why nothing in this class knows what a bond is.
> 
> ==================== THE RULES ====================
> 
>   PRICE-TIME PRIORITY. The best price first - the highest bid, the lowest
>   ask - and at one price the order that arrived first.
> 
>   A TRADE EXECUTES AT THE RESTING ORDER'S PRICE. An incoming order that
>   crosses the book takes what rests at the resting order's price, as on any
>   continuous exchange, and whatever it cannot fill rests at its own limit.
> 
>   PARTIAL FILLS. An order fills as far as the other side reaches, and the
>   rest of it stays where it was.
> 
>   NOBODY IS OBLIGED TO TRADE. A sell with no buyer rests; a buy with no
>   seller rests. The book never invents the other side.
> 
>   NO TRADE WITH ONESELF. An incoming order passes over the same
>   participant's resting orders (self-trade prevention, as every exchange
>   runs it) and they keep their place - and, since 0.7.14, over any
>   counterparty the clearing refuses (Clearing.mayTrade()), the same way.
> 
>   SETTLEMENT IS THE CALLER'S. The book matches; a Clearing moves the money
>   and the instrument, and says beforehand how much each side can settle -
>   a buyer with less cash than its bid, or a seller with less than its ask,
>   is filled as far as it can pay or deliver and its order trimmed to that,
>   so a trade the book reports is a trade that happened.
> 
> WHAT AN ORDER'S LIFE IS is the owner's decision, not the book's: it rests
> until it fills, is withdrawn (withdraw(), withdrawAll()), or is trimmed by
> the clearing. The bond market withdraws every order at its monthly step and
> each participant posts again from its view of that month - see
> BondMarket, THE ORDERS ARE GOOD FOR A MONTH.
> 
> Every field is plain data, so the book saves with Gson as it stands.

**Used by (13):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [LongPlaytest](LongPlaytest.md), [OrderBookCheck](OrderBookCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 169 | submitting |
| 279 | withdrawing |
| 320 | reading |

## Enum constants

| line | constant | says |
|---:|---|---|
| 60 | `OrderBook.Side.BUY` |  |
| 60 | `OrderBook.Side.SELL` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `OrderBook.DUST` | `1e-9` | Anything smaller than this is the arithmetic's dust, not a quantity: an order this small is not rested and a fill this small is not a trade. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `private transient double dust` | ...THIS BOOK'S DUST, IN ITS OWN UNITS (0.7.12 round 6). |
| 86 | `long seq` |  |
| 87 | `String who` |  |
| 88 | `Side side` |  |
| 89 | `double price` |  |
| 90 | `double quantity` |  |
| 91 | `double submitted` |  |
| 92 | `int month` |  |
| 146 | `private String instrument` |  |
| 148 | `private final List<Order> bids` | Resting bids, best first: highest price, then earliest. |
| 150 | `private final List<Order> asks` | Resting asks, best first: lowest price, then earliest. |
| 151 | `private long nextSeq` |  |
| 153 | `private double lastPrice` | The last trade's price, 0 before the first (a save carries no NaN). |
| 154 | `private int lastTradeMonth` |  |
| 157 | `private double postedBuy, postedSell, filledBuy, filledSell` | ---- the month's record: flows, cleared by startMonth() ---- |
| 158 | `private double volume, turnover` |  |
| 159 | `private int trades, sellsWaited, sellsPosted, buysWaited, buysPosted` |  |
| 160 | `private double sellQuantityWaited` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 377 | **type** `public class OrderBook` | A limit-order book for one instrument: buy and sell orders from named participants, each a price and a quantity, matched by price-time priority. |
| 60 | 1 | **type** `public enum Side` | Which side of the book an order is on. |
| 79 | 1 | `public void setDust(double dust)` | Sets what this book reads as dust: see above. |
| 82 | 1 | `public double getDust()` | What this book reads as dust. |
| 85 | 35 | **type** `public static final class Order` | One order, resting or just submitted. |
| 94 | 1 | `Order()` _(in OrderBook.Order)_ |  |
| 96 | 9 | `Order(long seq, String who, Side side, double price, double quantity, int month)` _(in OrderBook.Order)_ |  |
| 107 | 1 | `public long seq()` _(in OrderBook.Order)_ | Arrival order: earlier is smaller. |
| 109 | 1 | `public String who()` _(in OrderBook.Order)_ | The participant. |
| 110 | 1 | `public Side side()` _(in OrderBook.Order)_ |  |
| 112 | 1 | `public double price()` _(in OrderBook.Order)_ | The limit: the most a buyer pays, the least a seller takes, per unit. |
| 114 | 1 | `public double quantity()` _(in OrderBook.Order)_ | What is still unfilled. |
| 116 | 1 | `public double submitted()` _(in OrderBook.Order)_ | What was asked for when it was posted. |
| 118 | 1 | `public int month()` _(in OrderBook.Order)_ | The month it was posted in. |
| 125 | 17 | **type** `public interface Clearing` | What settles a trade. |
| 127 | 1 | `double capacity(String who, Side side, double price)` _(in OrderBook.Clearing)_ | The most of the instrument this participant can settle now on this side at this price: a buyer's cash over the price, a seller's holding. |
| 130 | 1 | `void settle(String buyer, String seller, double quantity, double price)` _(in OrderBook.Clearing)_ | Moves the money and the instrument: `quantity` from the seller to the buyer at `price` a unit. |
| 140 | 1 | `default boolean mayTrade(String buyer, String seller)` _(in OrderBook.Clearing)_ | Whether these two may trade with each other at all (0.7.14): true unless the market that owns the book says otherwise. |
| 144 | 1 | **type** `public record Fill(String buyer, String seller, double quantity, double price)` | One trade, as the book reports it back to whoever submitted the order. |
| 162 | 1 | `OrderBook()` |  |
| 164 | 1 | `public OrderBook(String instrument)` |  |
| 167 | 1 | `public String instrument()` | What this book trades. |

### submitting (lines 169-278)

| line | len | member | says |
|---:|---:|---|---|
| 179 | 3 | `public List<Fill> submit(String who, Side side, double price, double quantity, int month, Clearing clearing)` | Submits an order: it takes what crosses it on the other side, best first, at each resting order's price, and whatever is left rests at its own limit. |
| 190 | 65 | `public List<Fill> submit(String who, Side side, double price, double quantity, int month, Clearing clearing, boolean rest)` | ...or, with `rest` false, TAKES what crosses it and rests nothing (immediate-or-cancel): a buyer walking down its list of preferences takes what is on offer at one before trying the next (0.7.12 round 2, Exchange). |
| 256 | 9 | `private void recordTrade(double q, double price, int month)` |  |
| 267 | 11 | `private void rest(Order order)` | Puts an order where its price and arrival say it belongs. |

### withdrawing (lines 279-319)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 11 | `public int withdraw(String who)` | Withdraws every order this participant has resting, on both sides. |
| 295 | 6 | `public void withdrawAll()` | Withdraws every order on the book: the month's close. |
| 302 | 4 | `private void noteWithdrawn(Order o)` |  |
| 308 | 4 | `public void clear()` | Drops every order, uncounted: an instrument that has gone. |
| 314 | 5 | `public void startMonth()` | Opens the month's record. |

### reading (lines 320-433)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 1 | `public List<Order> bids()` | The resting bids, best first. |
| 325 | 1 | `public List<Order> asks()` | The resting asks, best first. |
| 328 | 1 | `public double bestBid()` | The highest bid, or NaN with none. |
| 330 | 1 | `public double bestAsk()` | The lowest ask, or NaN with none. |
| 333 | 9 | `public double depth(Side side, double price)` | What is resting at or better than this price on one side: the book's depth there. |
| 344 | 1 | **type** `public record Level(double price, double quantity, int orders)` | One price level of the depth: a price and everything resting there. |
| 347 | 12 | `public List<Level> levels(Side side)` | The depth of one side, one row per price, best first. |
| 361 | 5 | `public double resting(String who, Side side)` | What this participant has resting on one side. |
| 368 | 1 | `public double lastPrice()` | The price of the last trade, or NaN before the first. |
| 370 | 1 | `public int lastTradeMonth()` | The month of the last trade, or -1 before the first. |
| 373 | 1 | `public double postedBuy()` | This month: quantity posted to buy and to sell, and what of it filled. |
| 374 | 1 | `public double postedSell()` |  |
| 375 | 1 | `public double filledBuy()` |  |
| 376 | 1 | `public double filledSell()` |  |
| 378 | 1 | `public double volume()` | This month's volume in units and its value. |
| 379 | 1 | `public double turnover()` |  |
| 380 | 1 | `public int trades()` |  |
| 382 | 1 | `public int sellsPosted()` | Sell orders posted this month, and those withdrawn at the close still unfilled in whole or part - the sellers who waited - with what they still had to sell. |
| 383 | 1 | `public int sellsWaited()` |  |
| 384 | 1 | `public double sellQuantityWaited()` |  |
| 385 | 1 | `public int buysPosted()` |  |
| 386 | 1 | `public int buysWaited()` |  |
| 394 | 8 | `public void split(double k)` | A split (k > 1) or a consolidation (k < 1) of the instrument (0.7.12 round 2, the shares): every quantity resting and the month's in units times k, every price and the last one over it. |
| 404 | 3 | `public void seedLastPrice(double price)` | Opens a book with a price already on it: a save from before the book, whose dealer's last quote is where the market opens (Exchange.restore()). |
| 414 | 19 | `public void redenominate(double scale, boolean pricePerUnitOfMoney)` | The book in a reformed currency: for an instrument whose unit is money (a bond's face, pricePerUnitOfMoney) every quantity and the month's volumes times the scale and no price; for one whose unit is not (a share) ever... |

