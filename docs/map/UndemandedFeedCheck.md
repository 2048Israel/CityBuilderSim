# UndemandedFeedCheck.java - 383 lines · 15 methods · 2 constants · harnesses

`ham/citybuildersim/UndemandedFeedCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> No feed is bought for undemanded output that cannot cover it (CPB2).
> 
> WHY. CPB1 gated an undemanded stock buffer on its recipe's input cost, and
> REV-CPB1-1 found the two ways round the gate: the buffer it refused came
> back as export spare, priced on the joint bill's share by sales value,
> which charged cathode beside a rod mill less than its own anode; and for a
> good the world will not buy the net export price is NaN, and `NaN < cost`
> refused nothing, so a van plant nobody had asked for filled its shed on
> imported machinery. Building it found a third: committed recovered copper
> runs its refinery whatever the order share, and the share was still spread
> over the whole nameplate, so the anode refinery beside a rod mill was
> ordered as if the scrap did not cover any of it. All three are caused here
> in real sectors through Markets.clearMonth and the strike and the accounts.
> 
> What this has to prove:
>   1. cathode beside a rod mill, undemanded, its net export price over its
>      allocated marginal cost and under its anode: no anode is ordered or
>      bought for the spare, none of it ships, the accounts add up;
>   2. kept beside it: a full shed's profitable spare is still ordered whole and
>      made, and recovered copper already paid for still runs and ships at the
>      marginal cost, its feed sunk;
>   3. recovered copper committed beside a demanded rod mill: the anode
>      refinery is ordered only for the rod mill's cathode the scrap does not
>      make, so none ships on imported anode below its cost;
>   4. every real plant of a stockable good the world will not buy, with no
>      demand here: no buffer, no feed, nothing made, the accounts add up;
>   5. kept beside it: demanded vans make their cover and buy their feed,
>      profitable cars run beside an idle locomotive works, and vans already
>      in the shed sell to a real fleet with no new feed.

**Uses:** [Good](Good.md) (109), [Game](Game.md) (19), [Sector](Sector.md) (18), [CopperRefining](CopperRefining.md) (13), [GoodsMarket](GoodsMarket.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [NationalAccounts](NationalAccounts.md) (4), [OldMetal](OldMetal.md) (4), [Sectors](Sectors.md) (4), [BuildingManager](BuildingManager.md) (2), [GameFiles](GameFiles.md) (1), [EconomyManager](EconomyManager.md) (1), [Markets](Markets.md) (1)

## Sections

| line | section |
|---:|---|
| 90 | · 1 to 3: a refinery beside a rod mill |
| 244 | · 4 and 5: the goods the world will not buy |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `UndemandedFeedCheck.out` | `System.out` |  |
| 43 | `UndemandedFeedCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 344 | **type** `public class UndemandedFeedCheck` | No feed is bought for undemanded output that cannot cover it (CPB2). |
| 45 | 4 | `static void check(String label, boolean ok)` |  |
| 50 | 3 | `static boolean near(double a, double b)` |  |
| 54 | 9 | `static Game city(String label)` |  |
| 64 | 5 | `static void build(Game g, BuildingsTemplate t)` |  |
| 70 | 5 | `static void clear(Game g)` |  |
| 76 | 9 | `static NationalAccounts accounts(Game g)` |  |
| 86 | 3 | `static boolean identity(NationalAccounts na)` |  |

### 1 to 3: a refinery beside a rod mill (lines 90-243)

| line | len | member | says |
|---:|---:|---|---|
| 97 | 15 | `static Game copperPair(String label, double sale, double rodAsk, double rodBid, boolean rodWanted)` | A refinery and a rod mill, anode at 11/10, cathode at (sale + 1)/sale and rod at its own band; with `rodWanted` the rod mill's whole nameplate of rod is demanded at home, so its cathode is captive. |
| 113 | 40 | `static void allocatedSpare()` |  |
| 154 | 55 | `static void keptBesideTheRodMill()` |  |
| 210 | 33 | `static void committedBesideTheRodMill()` |  |

### 4 and 5: the goods the world will not buy (lines 244-383)

| line | len | member | says |
|---:|---:|---|---|
| 246 | 62 | `static void noWorldOutlet()` |  |
| 309 | 7 | `static Game motorTown(String label, String...plants)` |  |
| 317 | 56 | `static void keptBesideTheIdlePlants()` |  |
| 374 | 9 | `public static void main(String[] args)` |  |

