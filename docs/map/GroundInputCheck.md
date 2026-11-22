# GroundInputCheck.java - 371 lines · 17 methods · 2 constants · harnesses

`ham/citybuildersim/GroundInputCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> A works the ground limits buys the inputs of what its ground gives it, not of its plant (NG1).
> 
> WHY. HL1's row asked why the works imported 2.6 times a month's inputs the
> month their exports went, and for imported inputs not used to be booked as
> stock. Its m902 was GDP1's January stale price (0.9.97), and seed 12 no
> longer reaches it on 0.9.170; the class still live was the ground: an Iron
> Mine whose field was worked out bid the diesel of a whole month's ore after
> groundLimit() had dug it none, and a surface coal mine's diesel, a leach
> plant's acid and a gold mill's acid and diesel went the same way, their
> empty lift reading order share 1. What nothing made was bought abroad and
> became nothing - no output, no stock - and GDP fell by its value. Since
> NG1 the order follows the ground (Sector.groundShare()), so no input is
> bought that the month's run does not use; what a works does hold over the
> month's end, its pantry and its stock, is the fifth term's.
> 
> What this has to prove:
>   1. an Iron Mine on a field shorter than its month clears the diesel of
>      the ore it dug, and on a worked-out field none but its fleet's; the
>      accounts add up each month;
>   2. a surface coal mine on a short thermal seam bids the diesel of its
>      lift, and once the seam is out clears none for its run;
>   3. a leach plant on short oxide ground bids the acid of its cathode, and
>      once the oxide is out clears none; a Copper Mine & Mill on short
>      sulphide reads the share its concentrate's dig got;
>   4. a gold mill on a worked-out field bids and clears no acid or diesel,
>      and on a falling grade still bids its whole throughput's;
>   5. kept: on ample ground each one's bid is its recipe at its order share
>      and rate, to the bit, as before NG1;
>   6. an input bought and held over the month's end is stock: the vans a
>      works buys for a new mine and has not worn are in HELD, the fifth
>      term's goods, as the accounts count them.

**Uses:** [Good](Good.md) (73), [Game](Game.md) (15), [Resource](Resource.md) (12), [Sector](Sector.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (9), [Mining](Mining.md) (7), [LandManager](LandManager.md) (6), [Deposit](Deposit.md) (6), [CopperMining](CopperMining.md) (6), [NationalAccounts](NationalAccounts.md) (5), [BuildingManager](BuildingManager.md) (4), [CoalMining](CoalMining.md) (4), [GoldMining](GoldMining.md) (4), [CopperMiningCheck](CopperMiningCheck.md) (3), [CoalMiningCheck](CoalMiningCheck.md) (2), [GoldMiningCheck](GoldMiningCheck.md) (2), [EconomyManager](EconomyManager.md) (1), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [MaterialsCheck](MaterialsCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 103 | · 1: the Iron Mine |
| 160 | · 2: the surface coal mine |
| 195 | · 3: the leach plant |
| 253 | · 4: the gold mill |
| 287 | · 5: ample ground |
| 331 | · 6: what is held over |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `GroundInputCheck.out` | `System.out` |  |
| 46 | `GroundInputCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 329 | **type** `public class GroundInputCheck` | A works the ground limits buys the inputs of what its ground gives it, not of its plant (NG1). |
| 48 | 4 | `static void check(String label, boolean ok)` |  |
| 53 | 3 | `static boolean near(double a, double b)` |  |
| 57 | 5 | `static void quietly(Runnable work)` |  |
| 63 | 3 | `static void clear(Game g)` |  |
| 67 | 8 | `static NationalAccounts accounts(Game g)` |  |
| 76 | 3 | `static boolean identity(NationalAccounts na)` |  |
| 81 | 4 | `static double bought(Sector s, Good g)` | Units of an input a sector's row took in this pass, at home and abroad. |
| 87 | 3 | `static double runDiesel(Sector s)` | ...the diesel its run took: the row's, less its fleet's (Sector.runFleet() books through the same row). |
| 92 | 10 | `static double[] order(Sector s, Good made, Good input)` | A stockable good's order share and an input's bid, as the market pass sees them before it clears (THE ORDER). |

### 1: the Iron Mine (lines 103-159)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 16 | `static Game ironTown(String label, double tonnes)` | A scratch town with one Iron Mine on a hematite field of `tonnes` by fiat, its power, water and hands. |
| 123 | 36 | `static void iron()` |  |

### 2: the surface coal mine (lines 160-194)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 32 | `static void coal()` |  |

### 3: the leach plant (lines 195-252)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 55 | `static void leach()` |  |

### 4: the gold mill (lines 253-286)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 31 | `static void gold()` |  |

### 5: ample ground (lines 287-330)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 41 | `static void ample()` |  |

### 6: what is held over (lines 331-371)

| line | len | member | says |
|---:|---:|---|---|
| 333 | 25 | `static void heldOver()` |  |
| 359 | 12 | `public static void main(String[] args)` |  |

