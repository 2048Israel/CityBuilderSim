# AuctionTally.java - 145 lines · 11 methods · 1 constants · model

`ham/citybuildersim/AuctionTally.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> The city's auctions counted: how many sales the treasury has booked, what
> they sold and what they asked, how often the stop sat more than a point over
> fair, how often the bids fell short, and the sale last booked.
> 
> WHY (CD2, D33): the cap defaults to one point over fair (Auction.MAX_TAIL)
> on a judgement that the bids almost always cover well inside it; whether
> that is so is a count, not an opinion, and the count is only worth anything
> over a long run. So every auction that BOOKS adds itself here, the harness
> reads it at the end of a playtest (AuctionCheck 11, LongPlaytest's line),
> and the Borrow screen shows the last sale's figures from the same record.
> 
> Only a booking counts. A quote is the same arithmetic run for a screen that
> redraws every second and for the solvers that try a dozen faces to find one;
> counted there the tally would measure the screen. Game.bookProceeds() is the
> one place a domestic sale reaches the treasury, so it is the one place that
> calls record(). Dollar paper is not sold at auction and is not counted.
> 
> It is saved (DataSave.getAuctionTally(), toSave() and restore()): a city
> reloaded in the middle of a run keeps its count, or the count at the end of
> a long run would depend on how many times the player saved.

**Uses:** [Auction](Auction.md) (7)

**Used by (4):** [AuctionCheck](AuctionCheck.md), [DebtManager](DebtManager.md), [LongPlaytest](LongPlaytest.md), [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 28 | `AuctionTally.EPSILON` | `Auction.STOP_TOLERANCE` | How far over fair a tail must be to count as over a point: the stop is found to Auction.STOP_TOLERANCE, so a tail that is the cap's own one point, to the arithmetic's last digits, is not over it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `private int sales` |  |
| 45 | `private double faceSold` |  |
| 46 | `private double faceAsked` |  |
| 47 | `private int tailsOverPoint` |  |
| 48 | `private int shortSales` |  |
| 49 | `private double largestTail` |  |
| 50 | `private Last last` |  |
| 53 | `java.util.function.Consumer<Auction.Result> probeForTest` | A harness's look at each sale as it is counted (AuctionCheck 11 checks every booked sale, not only the sums). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 121 | **type** `public final class AuctionTally` | The city's auctions counted: how many sales the treasury has booked, what they sold and what they asked, how often the stop sat more than a point over fair, how often the bids fell short, and the sale last booked. |
| 41 | 2 | **type** `public record Last(Auction.Kind kind, int months, double stop, double tail, double cover, double sold, doub...` | The sale last booked, as the screen shows it. |
| 56 | 11 | `public void record(Auction.Result r)` | Counts a sale that has been booked. |
| 69 | 1 | `public int auctionSales()` | Sales booked. |
| 72 | 1 | `public double faceSold()` | The face those sales sold, money. |
| 75 | 1 | `public double faceAsked()` | The face those sales asked, money: what the city wanted, not what it got. |
| 78 | 1 | `public int auctionTailsOverPoint()` | Sales whose tail was strictly more than Auction.MAX_TAIL. |
| 81 | 1 | `public int auctionShortSales()` | Sales whose bids did not reach the issue at the highest yield the city took. |
| 84 | 1 | `public double largestTail()` | The largest tail any sale cleared at, in yield (.01 is a point); nothing before the first sale. |
| 87 | 1 | `public Last lastAuction()` | The sale last booked, or null for a city that has booked none. |
| 90 | 9 | `public void clear()` | Nothing counted: a new game, and a save from before the tally. |
| 106 | 20 | `public double[] toSave()` | The tally as the save holds it: sales, face sold, face asked, tails over a point, short sales, the largest tail, then the last sale - whether there is one, its kind (the ordinal), months, stop, tail, cover, sold and a... |
| 128 | 17 | `public void restore(double[] saved)` | ...and back. |

