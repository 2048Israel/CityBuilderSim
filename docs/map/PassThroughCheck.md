# PassThroughCheck.java - 840 lines · 21 methods · 17 constants · harnesses

`ham/citybuildersim/PassThroughCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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
> checks, that the mechanic fires in a real run and not only on a bench; the
> seventh, AN2, the reading the player is shown.
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
>      is the struck rate times the world's price level, to the bit. The city
>      ends with its goods' D$ set GAP_MADE from the D$ (setStruckRate) and is
>      saved so, the gap sections 3, 5 and 6 need; and the wiring is read again
>      for PUSH_MONTHS on a copy of it whose D$ is pinned by turns PUSH_OFF
>      either side of where it stood, so the strikes close real gaps (caused).
>   3. A STEP IN THE D$ REACHES THE GOODS AT PASS_THROUGH A MONTH. Twins of
>      that city, both pinned at the struck rate so no gap is pending, one's
>      D$ then pinned 10% weaker: for 36 months the weak twin's struck rate
>      over the other's is 1.1^(1-(1-k)^n), and so are the markets'. The twins'
>      price indices part by a share of the step - the city's own consumer
>      pass-through, and its five components at 12 months, printed and not
>      asserted, for the index holds rent and land, which the goods' rate does
>      not set. The groceries component's share at 12 months is positive and
>      under the share the goods' rate itself has reached - on a shelf the
>      imported cost sets, caused: both twins' goods' D$ and D$ set at DEAR
>      times the saved city's, so the shop's cost-plus floor stands over its
>      opening price and the clearing price under the floor (see the section
>      for the shelf's rule, traced).
>   4. THE RING SETTLES. Twins of that city on the float, each first given
>      the cash for the whole of what its treasury buys and a vault of twice
>      THIN_COVER months of imports so the vault's cover is flat (a thin vault
>      parts the twins by that road, not by the ring), and neither leaning on
>      the D$ (the FLOAT regime: the defence sells none, so what the treasury
>      sells back is what it bought and the vault earned): the treasury of one
>      buys dollars worth 5% of a year's GDP and sells the same dollars back
>      twelve months later, a swing of the D$ that moves the
>      city's prices and so its parity. Played ten years at PASS_THROUGH and
> ... (28 more lines in the source)

**Uses:** [Game](Game.md) (20), [PriceIndex](PriceIndex.md) (16), [EconomyManager](EconomyManager.md) (13), [ForeignAccounts](ForeignAccounts.md) (10), [ForeignCheck](ForeignCheck.md) (8), [GameFiles](GameFiles.md) (2), [Founding](Founding.md) (2), [Retail](Retail.md) (2), [TempDirs](TempDirs.md) (1), [Sector](Sector.md) (1)

## Sections

| line | section |
|---:|---|
| 196 | the fixtures every section borrows |
| 265 | 1. the rule on the bench |
| 302 | 2. the wiring, every month in a real city |
| 391 | 3. a step in the D$ reaches the goods at PASS_THROUGH a month |
| 495 | 4. the ring settles |
| 690 | 5. a save carries it |
| 740 | 6. how often it matters in a real city |
| 783 | 7. the screen's reading |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 145 | `PassThroughCheck.K` | `EconomyManager.PASS_THROUGH` | The speed the goods follow the D$ at, as the model has it. |
| 151 | `PassThroughCheck.MONTHS` | `240` | The months section 2 plays the real city, and the series it records (sections 6 and 7 read it). |
| 154 | `PassThroughCheck.APART` | `.01` | The gap, in logs, between the D$ and the goods' D$ that sections 3, 5 and 6 need a case to stand at (they assert more than this): a pin that removes it removes a real gap. |
| 157 | `PassThroughCheck.PUSH_MONTHS` | `12` | The months section 2 reads the wiring again on a copy of the saved city whose D$ is pinned off by turns, PUSH_OFF either side of where it stood. |
| 160 | `PassThroughCheck.PUSH_OFF` | `.08` | How far, in logs, either side of where it stood section 2 pins that copy's D$, month by month: a pin 16% from the last month's, more than BIG_GAP. |
| 163 | `PassThroughCheck.BIG_GAP` | `.05` | The gap, in logs, section 2's D$ must move between two strikes in the pinned months for its wiring to be read against a D$ that has moved (the old fixture's 5%). |
| 166 | `PassThroughCheck.GAP_MADE` | `.05` | How far, in logs, section 2 sets the saved city's goods' D$ from its D$ at the end of the run: the gap sections 3, 5 and 6 need (more than APART), made and not found. |
| 169 | `PassThroughCheck.DEAR` | `2` | The multiple of the saved city's goods' D$ at which section 3's twins are struck and pinned: dear enough that the shop's cost-plus floor stands over its opening price and the clearing price under the floor (the sectio... |
| 172 | `PassThroughCheck.MARK_OFF` | `.05` | How far, in logs, section 6 pins the D$ from the goods' D$ (5%) to read the gap the mechanic closes. |
| 175 | `PassThroughCheck.STEP` | `1.10` | The size of the step in sections 1 and 3: the D$ 10% weaker. |
| 260 | `PassThroughCheck.BASE_SLOT` | `1` | The slot, in this harness's own folder (TempDirs), the base city is saved to and its twins are loaded from. |
| 305 | `PassThroughCheck.seen` | `new double [ MONTHS ], struck = new double [ MONTHS ]` | The D$ the strike saw, and the goods' D$ it struck, for each of section 2's months: section 6 reads them. |
| 308 | `PassThroughCheck.gapEnd` | `new double [ MONTHS ]` | Game.struckFromRate() at the end of each of section 2's months (AN2), and whether it read NaN in the city before its first month: section 7 reads them. |
| 498 | `PassThroughCheck.SWING` | `.05` | The size of the treasury's swing: dollars worth this share of a year's GDP. |
| 501 | `PassThroughCheck.HELD` | `12, FOLLOWED = 120` | The months the swing is bought, held and sold in, and the months it is followed for after: a year held, ten years in all. |
| 504 | `PassThroughCheck.HORIZON` | `HELD +(int) Math.ceil(Math.log(.5) / Math.log(1 - K))` | The swing's own horizon, from the model's constants: the round trip (HELD) and the months the goods take to feel half of it (their half-life, rounded up). |
| 789 | `PassThroughCheck.LOG_NOISE` | `1e-14` | The least a month's two figures in logs may differ by before section 7 calls them different: doubles' own noise (a log of a ratio and an exp-log round trip each carry a few 1e-16), so a month whose gap is tiny is not ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 104 | `static int fails` |  |
| 105 | `static PrintStream out` |  |
| 106 | `static PrintStream quiet` |  |
| 109 | `static Path root` | This run's folder: every city here saves under it, none under the real save folder. |
| 263 | `static Game city` | Section 2's city, and the save sections 3 to 5 load from; set by section 2. |
| 309 | `static boolean nanAtFounding` |  |
| 508 | `double dollarWidest, goodsWidest, indexWidest, dollarEnd, goodsEnd, indexEnd, boughtLocal, boughtUsd, soldL...` |  |
| 509 | `int widestAt, excessMonths` |  |
| 510 | `boolean purchaseMoved` |  |
| 512 | `double dollarWidestSeen` | The D$'s widest gap over months 0 to FOLLOWED-1 (the gap of month 0 is none): the most the goods, a month behind it one for one, can have seen by month FOLLOWED. |
| 514 | `final double[] dollarSeries` | The gaps, in logs, month by month after the purchase (index 0 is the day of it: none): the D$, the goods' D$ and the price index. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 102 | 739 | **type** `public class PassThroughCheck` | Proves the city's goods are struck at a D$ that follows the D$ slowly (AN1, 2026-10-22): each month the goods' D$ closes EconomyManager.PASS_THROUGH of its gap to the D$, in logs, and every world price is struck at it... |
| 111 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 116 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 127 | 9 | `static void closeRelative(String label, double actual, double expected, double tol)` | ...to a relative tolerance: the two figures' gap over the expected one's size. |
| 138 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 148 | 1 | `static double reached(double speed, int n)` | The share of a step in the D$ that has reached the goods after n months. |
| 177 | 18 | `public static void main(String[] args) throws Exception` |  |

### the fixtures every section borrows (lines 196-264)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 3 | `static GameFiles files(String label)` | A save folder of this run's own, one per city that saves. |
| 214 | 31 | `static Game town(String label)` | A trading town on the default founding, as ExchangeRateCheck builds it; its D$ is what the founding's seed makes it. |
| 247 | 3 | `static void month(Game g)` | One month, with nothing built or decided by anyone: the twins differ only in what the harness does to them. |
| 252 | 6 | `static Game load(Game base)` | A city loaded from the base city's slot 1 (BASE_SLOT), into a city of its own. |

### 1. the rule on the bench (lines 265-301)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 34 | `static void theRuleOnTheBench()` |  |

### 2. the wiring, every month in a real city (lines 302-390)

| line | len | member | says |
|---:|---:|---|---|
| 311 | 79 | `static void theWiring()` |  |

### 3. a step in the D$ reaches the goods at PASS_THROUGH a month (lines 391-494)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 96 | `static void aStepReachesTheGoods()` |  |
| 491 | 3 | `static boolean costSets(Retail shop)` | Whether the imported cost sets the shop's shelf this month: its cost-plus floor stands over the opening price, and the clearing price is not over the floor (so the target is the floor). |

### 4. the ring settles (lines 495-689)

| line | len | member | says |
|---:|---:|---|---|
| 507 | 9 | **type** `static final class Swing` | One pair of twins' swing: the figures the section prints and asserts. |
| 518 | 103 | `static Swing swing(double speed, String name)` | The twins of the base city, one buying dollars worth SWING of a year's GDP and selling them back HELD months later, played FOLLOWED months, goods following the D$ at `speed`. |
| 622 | 13 | `static void printSwing(String name, Swing s)` |  |
| 637 | 5 | `static double meanGap(double[] series, int from, int to)` | The mean of the gaps' sizes over months `from` to `to`. |
| 643 | 46 | `static void theRingSettles()` |  |

### 5. a save carries it (lines 690-739)

| line | len | member | says |
|---:|---:|---|---|
| 692 | 47 | `static void aSaveCarriesIt()` |  |

### 6. how often it matters in a real city (lines 740-782)

| line | len | member | says |
|---:|---:|---|---|
| 742 | 40 | `static void howOftenItMatters()` |  |

### 7. the screen's reading (lines 783-840)

| line | len | member | says |
|---:|---:|---|---|
| 791 | 49 | `static void theScreensReading()` |  |

