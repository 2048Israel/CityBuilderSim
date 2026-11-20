# UndemandedFeedCheck.java - 342 lines · 14 methods · 2 constants · harnesses

`ham/citybuildersim/UndemandedFeedCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> No feed is bought for undemanded output that cannot cover it (CPB2).
> 
> WHY. CPB1 gated an undemanded stock buffer on its recipe's input cost, and
> REV-CPB1-1 found the two ways round the gate: the buffer it refused came
> back as export spare, priced on the joint bill's share by sales value,
> which charged cathode beside a rod mill less than its own anode; and for a
> good the world will not buy the net export price is NaN, and `NaN < cost`
> refused nothing, so a van plant nobody had asked for filled its shed on
> imported machinery. Both are caused here in real sectors through
> Markets.clearMonth and the strike and the accounts.
> 
> What this has to prove:
>   1. cathode beside a rod mill, undemanded, its net export price over its
>      allocated marginal cost and under its anode: no anode is ordered or
>      bought for the spare, none of it ships, the accounts add up;
>   2. kept beside it: a full shed's profitable spare is still ordered whole and
>      made, and recovered copper already paid for still runs and ships at the
>      marginal cost, its feed sunk;
>   3. every real plant of a stockable good the world will not buy, with no
>      demand here: no buffer, no feed, nothing made, the accounts add up;
>   4. kept beside it: demanded vans make their cover and buy their feed,
>      profitable cars run beside an idle locomotive works, and vans already
>      in the shed sell to a real fleet with no new feed.

**Uses:** [Good](Good.md) (97), [Game](Game.md) (18), [Sector](Sector.md) (16), [CopperRefining](CopperRefining.md) (10), [GoodsMarket](GoodsMarket.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [NationalAccounts](NationalAccounts.md) (4), [Sectors](Sectors.md) (4), [BuildingManager](BuildingManager.md) (2), [OldMetal](OldMetal.md) (2), [GameFiles](GameFiles.md) (1), [EconomyManager](EconomyManager.md) (1), [Markets](Markets.md) (1)

## Sections

| line | section |
|---:|---|
| 84 | · 1 and 2: a refinery beside a rod mill |
| 204 | · 3 and 4: the goods the world will not buy |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `UndemandedFeedCheck.out` | `System.out` |  |
| 37 | `UndemandedFeedCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 309 | **type** `public class UndemandedFeedCheck` | No feed is bought for undemanded output that cannot cover it (CPB2). |
| 39 | 4 | `static void check(String label, boolean ok)` |  |
| 44 | 3 | `static boolean near(double a, double b)` |  |
| 48 | 9 | `static Game city(String label)` |  |
| 58 | 5 | `static void build(Game g, BuildingsTemplate t)` |  |
| 64 | 5 | `static void clear(Game g)` |  |
| 70 | 9 | `static NationalAccounts accounts(Game g)` |  |
| 80 | 3 | `static boolean identity(NationalAccounts na)` |  |

### 1 and 2: a refinery beside a rod mill (lines 84-203)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 15 | `static Game copperPair(String label, double sale, double rodAsk, double rodBid, boolean rodWanted)` | A refinery and a rod mill, anode at 11/10, cathode at (sale + 1)/sale and rod at its own band; with `rodWanted` the rod mill's whole nameplate of rod is demanded at home, so its cathode is captive. |
| 107 | 40 | `static void allocatedSpare()` |  |
| 148 | 55 | `static void keptBesideTheRodMill()` |  |

### 3 and 4: the goods the world will not buy (lines 204-342)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 62 | `static void noWorldOutlet()` |  |
| 269 | 7 | `static Game motorTown(String label, String...plants)` |  |
| 277 | 56 | `static void keptBesideTheIdlePlants()` |  |
| 334 | 8 | `public static void main(String[] args)` |  |

