# TradeCostCheck.java - 534 lines · 6 methods · 1 constants · harnesses

`ham/citybuildersim/TradeCostCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The wedge between what the world charges and what it pays, and what it is
> made of.
> 
> WHY THIS HARNESS EXISTS. Until today the two world prices were two constants
> and nothing tested them, because there was nothing to test: a constant is
> either right or it is a balance decision, and neither is a harness's
> business. They are arithmetic now - a world margin the player can never move
> and a freight cost that will one day be a business's price - and arithmetic
> has invariants.
> 
> WHAT IT HAS TO PROVE, and the first one is the whole of step one:
> 
>   1. NOT ONE PRICE MOVED. Every delivered price is the literal it has always
>      been, to the bit. The decomposition is stored the other way round for
>      exactly this reason and this is the assertion that says so.
> 
>   2. THE WORLD BAND IS STILL A BAND. worldBuy above worldSell on every good
>      the world trades both ways - if freight ever exceeded half the wedge the
>      world would be bidding above its own ask, which is not a market.
> 
>   3. FREIGHT IS THREE QUARTERS OF THE WEDGE, because that is the decision
>      that sets how much of it good logistics can win back, and a number that
>      important should fail loudly when somebody edits a price and forgets it.
> 
>   4. NOTHING UNSHIPPABLE IS CHARGED FOR SHIPPING. A seat-month of engineering
>      work goes down a wire.
> 
> It is also where the rest of the transport work will be checked as it lands -
> the traffic split, the modes, and the haulage sector's price. See
> claude/transport-and-the-freight-band.md.

**Uses:** [Traffic](Traffic.md) (46), [Good](Good.md) (44), [TaxPolicy](TaxPolicy.md) (12), [InfrastructureManager](InfrastructureManager.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [Formats](Formats.md) (7), [Game](Game.md) (2), [GameFiles](GameFiles.md) (1), [BuildingManager](BuildingManager.md) (1), [PayTier](PayTier.md) (1)

## Sections

| line | section |
|---:|---|
| 171 | · the traffic split (2026-09-16) |
| 269 | · the modes (2026-09-16) |
| 437 | · the fare (2026-09-16) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `TradeCostCheck.DELIVERED` | `{ { Good.CROPS,.44,.28 }, { Good.GRAINS,.00080,.00050 }, { Good.BREAD,.00250,...` | The delivered prices, as they were on 2026-09-16 before a line of this was written, hard-coded on purpose. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 35 | 500 | **type** `public class TradeCostCheck` | The wedge between what the world charges and what it pays, and what it is made of. |
| 39 | 7 | `static void quietly(Runnable r)` |  |
| 47 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 52 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 104 | 3 | `static boolean same(double a, double b)` | Bit-for-bit, not to a tolerance. |
| 108 | 420 | `public static void main(String[] args)` |  |
| 529 | 5 | `static int countTraded()` |  |

