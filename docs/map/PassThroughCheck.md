# PassThroughCheck.java - 589 lines · 19 methods · 8 constants · harnesses

`ham/citybuildersim/PassThroughCheck.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

> Proves the city's goods are struck at a D$ that follows the D$ slowly (AN1,
> 2026-10-22): each month the goods' D$ closes EconomyManager.PASS_THROUGH of
> its gap to the D$, in logs, and every world price is struck at it times the
> world's own price level; a step in the D$ reaches the goods at that speed;
> the ring a one-for-one strike let run echoes less; a save carries the struck
> rate; and in a real city the two are far enough apart for it to matter. Not
> part of the game.
> 
> WHY THIS EXISTS. Jerus, 2026-10-22: "Fix hyperinflation first". Since XR1
> the D$ floats and follows its parity one for one, and until AN1 every traded
> price - the dock's imports and exports, the band the city's own goods trade
> in, the materials in a building's cost - was struck at the D$ of the month:
> a price rise moved parity, the D$ followed it, imports got dearer, prices
> rose again, with no lag anywhere in the ring, and the default playtest ran
> to 1.7e9 times its founding prices (docs/queue/XR1-notes.md, section 3). The
> fix is a lag on the goods and only the goods: EconomyManager, THE D$ THE
> GOODS ARE STRUCK AT. This is its harness. The first four sections prove the
> rule and its wiring; the fifth the save; the sixth, the third rule of the
> checks, that the mechanic fires in a real run and not only on a bench.
> 
> What it has to prove, a section each, every one a fixture that causes the
> condition rather than waiting for it:
> 
>   1. THE RULE ON THE BENCH. passThrough() applied n times to a 10% step is
>      the struck rate times 1.1^(1-(1-k)^n), k = PASS_THROUGH, to 1e-12 for
>      several n; at speed one it is the D$ itself (to the last digit - see
>      the section); a struck rate at the D$ stays there. The half-life and
>      the share reached in a year are printed.
>   2. THE WIRING, EVERY MONTH IN A REAL CITY. A new city on the default
>      founding, 240 months: the first strike is at the D$ itself; each month
>      the struck rate is passThrough() of the last month's, moved toward the
>      D$ the strike saw (the one read between months), and the markets' rate
>      is the struck rate times the world's price level, to the bit.
>   3. A STEP IN THE D$ REACHES THE GOODS AT PASS_THROUGH A MONTH. Twins of
>      that city, both pinned at the struck rate so no gap is pending, one's
>      D$ then pinned 10% weaker: for 36 months the weak twin's struck rate
>      over the other's is 1.1^(1-(1-k)^n), and so are the markets'. The twins'
>      price indices part by a share of the step - the city's own consumer
>      pass-through, and its five components at 12 months, printed and not
>      asserted, for the index holds rent and land, which the goods' rate does
>      not set. The groceries component's share at 12 months, the shelf cost-
>      plus on the goods, is positive and under the share the goods' rate
>      itself has reached.
>   4. THE RING SETTLES. Twins of that city on the float, each first given a
>      vault of twice THIN_COVER months of imports so the vault's cover is
>      flat (a thin vault parts the twins by that road, not by the ring): the
>      treasury of one buys dollars worth 5% of a year's GDP and sells the same
>      dollars back twelve months later, a swing of the D$ that moves the
>      city's prices and so its parity. Played ten years at PASS_THROUGH and
>      again with both twins at speed one (the rule before AN1): the D$'s
>      widest gap between the twins, the goods' widest gap and the price
>      index's gap at the end are printed for each, not asserted: ten years on
>      a gap is the economy amplifying a small difference. The purchase moves
>      the D$ by what INTERVENTION_IMPACT says; at PASS_THROUGH the goods' gap
>      never exceeds the D$'s widest gap so far times 1-(1-k)^n; and over the
>      swing's own horizon - the round trip and the goods' half-life, from
>      the constants - the mean gap of the goods' rate and of the price index
>      is smaller at PASS_THROUGH than one for one (the D$'s is printed).
>   5. A SAVE CARRIES IT. A city saved and loaded keeps the struck rate to
>      the bit and strikes the next month as the live city does; a loaded city
> ... (8 more lines in the source)

**Uses:** [Game](Game.md) (16), [PriceIndex](PriceIndex.md) (16), [EconomyManager](EconomyManager.md) (9), [ForeignCheck](ForeignCheck.md) (7), [ForeignAccounts](ForeignAccounts.md) (5), [GameFiles](GameFiles.md) (2), [Founding](Founding.md) (2), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 152 | the fixtures every section borrows |
| 194 | 1. the rule on the bench |
| 231 | 2. the wiring, every month in a real city |
| 278 | 3. a step in the D$ reaches the goods at PASS_THROUGH a month |
| 341 | 4. the ring settles |
| 514 | 5. a save carries it |
| 564 | 6. how often it matters in a real city |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 123 | `PassThroughCheck.K` | `EconomyManager.PASS_THROUGH` | The speed the goods follow the D$ at, as the model has it. |
| 129 | `PassThroughCheck.MONTHS` | `240` | The months section 2 plays the real city, and the one it is saved at the end of. |
| 132 | `PassThroughCheck.STEP` | `1.10` | The size of the step in sections 1 and 3: the D$ 10% weaker. |
| 189 | `PassThroughCheck.BASE_SLOT` | `1` |  |
| 234 | `PassThroughCheck.seen` | `new double [ MONTHS ], struck = new double [ MONTHS ]` | The D$ the strike saw, and the goods' D$ it struck, for each of section 2's months: section 6 reads them. |
| 344 | `PassThroughCheck.SWING` | `.05` | The size of the treasury's swing: dollars worth this share of a year's GDP. |
| 347 | `PassThroughCheck.HELD` | `12, FOLLOWED = 120` | The months the swing is bought, held and sold in, and the months it is followed for after: a year held, ten years in all. |
| 350 | `PassThroughCheck.HORIZON` | `HELD +(int) Math.ceil(Math.log(.5) / Math.log(1 - K))` | The swing's own horizon, from the model's constants: the round trip (HELD) and the months the goods take to feel half of it (their half-life, rounded up). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 82 | `static int fails` |  |
| 83 | `static PrintStream out` |  |
| 84 | `static PrintStream quiet` |  |
| 87 | `static Path root` | This run's folder: every city here saves under it, none under the real save folder. |
| 192 | `static Game city` | Section 2's city, and the save sections 3 to 5 load from; set by section 2. |
| 354 | `double dollarWidest, goodsWidest, indexWidest, dollarEnd, goodsEnd, indexEnd, boughtLocal, boughtUsd, soldL...` |  |
| 355 | `int widestAt, excessMonths` |  |
| 356 | `boolean purchaseMoved` |  |
| 358 | `final double[] dollarSeries` | The gaps, in logs, month by month after the purchase (index 0 is the day of it: none): the D$, the goods' D$ and the price index. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 510 | **type** `public class PassThroughCheck` | Proves the city's goods are struck at a D$ that follows the D$ slowly (AN1, 2026-10-22): each month the goods' D$ closes EconomyManager.PASS_THROUGH of its gap to the D$, in logs, and every world price is struck at it... |
| 89 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 94 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 105 | 9 | `static void closeRelative(String label, double actual, double expected, double tol)` | ...to a relative tolerance: the two figures' gap over the expected one's size. |
| 116 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 126 | 1 | `static double reached(double speed, int n)` | The share of a step in the D$ that has reached the goods after n months. |
| 134 | 17 | `public static void main(String[] args) throws Exception` |  |

### the fixtures every section borrows (lines 152-193)

| line | len | member | says |
|---:|---:|---|---|
| 155 | 3 | `static GameFiles files(String label)` | A save folder of this run's own, one per city that saves. |
| 160 | 16 | `static Game town(String label)` | A trading town on the default founding, as ExchangeRateCheck builds it; its D$ is what the founding's seed makes it. |
| 178 | 3 | `static void month(Game g)` | One month, with nothing built or decided by anyone: the twins differ only in what the harness does to them. |
| 183 | 5 | `static Game load(Game base)` | A city loaded from the base city's slot 1 (BASE_SLOT), into a city of its own. |

### 1. the rule on the bench (lines 194-230)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 34 | `static void theRuleOnTheBench()` |  |

### 2. the wiring, every month in a real city (lines 231-277)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 41 | `static void theWiring()` |  |

### 3. a step in the D$ reaches the goods at PASS_THROUGH a month (lines 278-340)

| line | len | member | says |
|---:|---:|---|---|
| 280 | 60 | `static void aStepReachesTheGoods()` |  |

### 4. the ring settles (lines 341-513)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 7 | **type** `static final class Swing` | One pair of twins' swing: the figures the section prints and asserts. |
| 362 | 84 | `static Swing swing(double speed, String name)` | The twins of the base city, one buying dollars worth SWING of a year's GDP and selling them back HELD months later, played FOLLOWED months, goods following the D$ at `speed`. |
| 447 | 13 | `static void printSwing(String name, Swing s)` |  |
| 462 | 5 | `static double meanGap(double[] series, int from, int to)` | The mean of the gaps' sizes over months `from` to `to`. |
| 468 | 45 | `static void theRingSettles()` |  |

### 5. a save carries it (lines 514-563)

| line | len | member | says |
|---:|---:|---|---|
| 516 | 47 | `static void aSaveCarriesIt()` |  |

### 6. how often it matters in a real city (lines 564-589)

| line | len | member | says |
|---:|---:|---|---|
| 566 | 23 | `static void howOftenItMatters()` |  |

