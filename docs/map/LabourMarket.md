# LabourMarket.java - 915 lines · 53 methods · 13 constants · model

`ham/citybuildersim/LabourMarket.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> What labour costs, and why it costs that.
> 
> WHY THIS EXISTS
> 
> Labour was the last thing in the game that was not a market. Iron prices off
> scarcity, land prices off scarcity, the city's borrowing rate prices off its
> own books - and a wage was one of six constants in PayTier, copied into
> PopulationManager once at startup and never moved again. A city that could
> not staff a hospital paid its doctors exactly what a city with a queue of
> them paid.
> 
> That is the same complaint the backlog already makes about rent, one system
> over and worse, because labour is an input to everything.
> 
> THE MODEL
> 
>     base   = minimumWage x ratio[type] x costOfLiving
>     tight  = posts / qualified workers
>     target = base x clamp(tight ^ ELASTICITY, MIN_MULTIPLE, MAX_MULTIPLE)
>     wage  += (target - wage) x ADJUST_RATE
>     wage   = max(wage, cashMinimumWage())     // the floor in today's money, which isPinned() reads too (0.7.47)
> 
> Four things in there are load-bearing and none of them is the exponent.
> 
> THE MINIMUM WAGE IS THE BASE. Jerus: "make the base a modifiable min wage".
> It is not merely a floor under the lowest band - the whole ladder is a
> multiple of it, so raising it lifts every wage in the city and the player has
> one dial that moves the entire labour cost of the economy. The multiples are
> READ OFF PayTier rather than invented (see ratioOf), which gives the property
> that matters: at the default minimum wage this market pays exactly what the
> game paid before it existed. Every balance measurement already taken survives.
> 
> IT IS CLAMPED. A purely relative price has no absolute level, and one
> unfilled post in a city with no doctors is infinite scarcity. Same reasoning
> as the credit spread stopping at eight points.
> 
> IT IS DAMPED. People take months to move, so the supply response lags - and a
> lagged negative feedback loop oscillates rather than settles. That is the
> cobweb cycle, and undamped it produces wage swings that read as a bug rather
> than as economics. ADJUST_RATE is what stops the city hunting.
> 
> AND THE FLOOR IS A POLICY, WHICH IS THE INTERESTING PART. Without one:
> unemployment drives wages down, low wages drive people out, a smaller city
> needs fewer workers, and it spirals. The minimum wage stops that - and
> because the unskilled base IS the minimum wage, an unskilled surplus cannot
> be priced away at all. The adjustment has to happen in QUANTITY instead, which
> is exactly what a binding minimum wage does in the real world and exactly
> what Migration's surplus departures now model. The player sets the number and
> lives with which of the two costs they would rather pay.

**Uses:** [JobType](JobType.md) (27), [WageBand](WageBand.md) (20), [WageLadder](WageLadder.md) (7), [DecisionLog](DecisionLog.md) (5), [PayTier](PayTier.md) (3), [EducationType](EducationType.md) (2)

**Used by (22):** [ArrivalMixCheck](ArrivalMixCheck.md), [CityNeeds](CityNeeds.md), [DiscoveryCheck](DiscoveryCheck.md), [Education](Education.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [PeopleScreen](PeopleScreen.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationManager](PopulationManager.md), [PriceLevelCheck](PriceLevelCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [WageLadderCheck](WageLadderCheck.md), [WantedPosts](WantedPosts.md), [WantedPostsCheck](WantedPostsCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | THE DIAL |
| 79 | THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. |
| 185 | THE CURVE |
| 303 | THE LADDER |
| 330 | THE COST OF LIVING |
| 429 | · HALF WHAT PEOPLE EXPECT, HALF THE CHASE (0.7.42) |
| 511 | THE MONTH |
| 630 | READING AND SETTING |
| 682 | THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. |
| 771 | · WHAT THE POLICY TAB ASKS (0.7.36) |
| 817 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `LabourMarket.DEFAULT_MINIMUM_WAGE` | `PayTier.UNSKILLED.getMonthlyWage()` | Where the minimum wage starts, and therefore what the whole ladder is anchored to on month one. |
| 106 | `LabourMarket.MIN_SETTABLE_SHARE` | `.25` | The lowest the dial goes, as a share of the unskilled wage. |
| 109 | `LabourMarket.MAX_SETTABLE_MULTIPLE` | `5.0` | The highest the dial goes, as a multiple of the unskilled wage. |
| 112 | `LabourMarket.MIN_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MIN_SETTABLE_SHARE` | Bounds on the dial, so the screen cannot ask for a negative wage. |
| 114 | `LabourMarket.MAX_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MAX_SETTABLE_MULTIPLE` |  |
| 197 | `LabourMarket.ELASTICITY` | `.5` | How hard a shortage pushes the wage. |
| 208 | `LabourMarket.MAX_MULTIPLE` | `4.0` | How far above base a wage can climb. |
| 211 | `LabourMarket.MIN_MULTIPLE` | `.70` | ...and how far below, before the minimum wage catches it anyway. |
| 242 | `LabourMarket.ADJUST_RATE` | `.12` | How much of the gap to its target a wage closes each month. |
| 252 | `LabourMarket.PINNED_TOLERANCE` | `.02` | How far from its floor a wage counts as PINNED. |
| 388 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 409 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |
| 454 | `LabourMarket.EXPECTED_SHARE` | `.5` | The share of a wage's monthly indexing taken from expected inflation; the rest chases the index at the old speed's share. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private transient DecisionLog decisions` | Where the player's change to the wage floor is written (DecisionLog, 0.7.23); null for one no city holds. |
| 127 | `private double minSettable` | The same bounds, in TODAY's money. |
| 128 | `private double maxSettable` |  |
| 138 | `private transient WageLadder ladder` | The wage ladder this market prices on (R0d): today's, PayTier's, in every city but one founded the 1900 way, whose market pays 1901's (WageLadder). |
| 167 | `private double minimumWage` |  |
| 177 | `private boolean floorBinding` | Whether the minimum wage is law yet (R0): false in a city before the date Canada first had one (PolicyDates, MINIMUM_WAGE, 1918), and then the floor binds no wage - the max() in advanceMonth() and isPinned()'s floor a... |
| 254 | `private final double[] wage` |  |
| 266 | `private final double[] tightness` | Scarcity per BAND, not per job type. |
| 287 | `private final double[] licenceTightness` | THE LICENCE PREMIUM (2026-09-06). |
| 288 | `private final double[] licenceMultiple` |  |
| 289 | `private final double[] bandMultiple` |  |
| 411 | `private double costOfLiving` |  |
| 412 | `private double livingTarget` |  |
| 708 | `private double minimumWageAdjustment` | What the player has added to or taken off the floor, as a share. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 862 | **type** `public class LabourMarket` | What labour costs, and why it costs that. |
| 60 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 62 | 3 | `private void decided(String kind, String label)` |  |

### THE DIAL (lines 66-78)

### THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. (lines 79-184)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 1 | `public double getMinSettable()` |  |
| 131 | 1 | `public double getMaxSettable()` |  |
| 141 | 1 | `public WageLadder getLadder()` | The ladder this market prices on (R0d). |
| 147 | 5 | `public void setLadder(WageLadder l)` | Puts this market on a ladder (R0d): the ladder and the dial's bounds, which are multiples of its anchor (today's are exactly MIN_SETTABLE and MAX_SETTABLE). |
| 154 | 1 | `private double lowestSettable()` | The dial's bounds on this market's ladder at the founding unit: the constants on today's, multiples of the 1901 anchor on 1901's. |
| 155 | 1 | `private double highestSettable()` |  |
| 161 | 5 | `public void foundOn(WageLadder l)` | A city founded on a ladder (R0d): its bounds, its minimum wage at the ladder's anchor - the field, not setMinimumWage(), which would log a decision nobody made - and every wage on its base. |
| 180 | 1 | `public boolean isFloorBinding()` | Whether the floor binds (see floorBinding). |
| 183 | 1 | `public void setFloorBinding(boolean binding)` | Game's push, with the policy's month: whether the minimum wage is law in this city yet. |

### THE CURVE (lines 185-302)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 3 | `public static double multipleAt(double tightness)` | THE CURVE ITSELF, in one place (0.7.18): the multiple a band's wage aims at when its posts stand at this tightness against its supply. |
| 230 | 3 | `public static double tightnessAt(double multiple)` | ...and the curve read backwards: the tightness at which a band's wage aims at this multiple of its base, clamps aside. |
| 292 | 6 | `public static boolean isGated(JobType job)` | True for a job that only a licence holder can fill - see EducationType. |
| 299 | 3 | `public LabourMarket()` |  |

### THE LADDER (lines 303-329)

| line | len | member | says |
|---:|---:|---|---|
| 321 | 3 | `public static double ratioOf(JobType job)` | What this job pays relative to the unskilled floor. |
| 326 | 3 | `public double ratio(JobType job)` | What this job pays relative to the unskilled floor on this market's ladder (R0d): ratioOf() on today's, the census's ratios on 1901's. |

### THE COST OF LIVING (lines 330-428)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 5 | `public void updateCostOfLiving(double priceIndex)` | A REAL index since phase 5 - see PriceIndex. |

### HALF WHAT PEOPLE EXPECT, HALF THE CHASE (0.7.42) (lines 429-510)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 6 | `public void updateCostOfLiving(double priceIndex, double expectedMonthly)` | The cost of living, once the basket is based: half from what people expect, half the chase (see HALF WHAT PEOPLE EXPECT, HALF THE CHASE). |
| 472 | 1 | `public double getCostOfLiving()` | What wages have been lifted by, chasing the cost of living. |
| 475 | 1 | `public double getLivingTarget()` | Where they are heading. |
| 477 | 3 | `public void setCostOfLiving(double value)` |  |
| 482 | 28 | `public double baseWage(JobType job)` | What this job would pay in a city with exactly enough people for it. |

### THE MONTH (lines 511-629)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 3 | `public void advanceMonth(double[] bandPosts, double[] bandSupply)` | Re-prices every job type against how hard it is to staff. |
| 531 | 61 | `public void advanceMonth(double[] bandPosts, double[] bandSupply, long[] jobPosts, double[] licensedHeads)` |  |
| 606 | 11 | `public boolean isPinned(WageBand band)` | True when this job's wage has fallen as far as the market will let it and there is still nowhere for those workers to go. |
| 619 | 6 | `public final void resetToBase()` | Puts every wage back on its base. |
| 626 | 3 | `private static double clamp(double v, double lo, double hi)` |  |

### READING AND SETTING (lines 630-681)

| line | len | member | says |
|---:|---:|---|---|
| 634 | 1 | `public double[] getWages()` |  |
| 635 | 1 | `public double getWage(JobType job)` |  |
| 636 | 1 | `public double getTightness(WageBand band)` |  |
| 638 | 1 | `public double getLicenceTightness(JobType job)` | Posts over licence holders for a gated job; 0 for an ungated one or one with no posts. |
| 640 | 1 | `public double getLicenceMultiple(JobType job)` | The licence premium's target multiple this month, 1 when there is none. |
| 642 | 1 | `public double getBandMultiple(WageBand band)` | The band's own multiple this month, without any licence premium on top. |
| 660 | 5 | `public double licencePremium(JobType job)` | What a licensed profession is paid OVER ITS OWN BAND, as actually paid. |
| 673 | 9 | `public double bandPremium(WageBand band)` | The premium the BAND is paying, read off an ungated job in it. |

### THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. (lines 682-770)

| line | len | member | says |
|---:|---:|---|---|
| 711 | 1 | `public double getMinimumWage()` | The real floor, in founding money. |
| 714 | 1 | `public double getMinimumWageBase()` | The same figure. |
| 717 | 1 | `public double getMinimumWageAdjustment()` | The player's nudge, as a share. |
| 730 | 3 | `public double cashMinimumWage()` | The floor in TODAY'S money: the real floor, lifted by the cost of living. |
| 735 | 3 | `public void setMinimumWageAdjustment(double share)` | Moves the percentage. |
| 740 | 4 | `public double premium(JobType job)` | How far above its base a job is paying - 1.00 is the going rate. |
| 763 | 7 | `public void setMinimumWage(double value)` | Moves the dial. |

### WHAT THE POLICY TAB ASKS (0.7.36) (lines 771-816)

| line | len | member | says |
|---:|---:|---|---|
| 778 | 3 | `public double cashAt(double floor)` | What a real floor of `floor`, in founding money, comes to in today's money: cashMinimumWage()'s rule at another setting. |
| 783 | 4 | `public double floorForCash(double cash)` | ...and back: the founding figure whose cash floor is `cash` today - what the Policy tab's dial, read in today's money, sets. |
| 796 | 6 | `public double targetWageAt(JobType job, double floor)` | Where a job's wage heads with the real floor at `floor`: advanceMonth()'s target - its base at that floor times this month's band and licence multiples, held to MAX_MULTIPLE - and never under the floor in today's mone... |
| 804 | 7 | `public double bestWageIn(WageBand band)` | The best-paid job somebody in this band can hold (0.7.36; the Policy tab's own until then): what a course's fee is weighed against. |
| 813 | 3 | `public void setMinimumWageBase(double value)` | Sets the real floor, in founding money. |

### SAVE AND RESTORE (lines 817-915)

| line | len | member | says |
|---:|---:|---|---|
| 827 | 26 | `public double[] state()` |  |
| 854 | 3 | `private int diagnosticsLength()` |  |
| 866 | 27 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded - the same rule the health and healthcare arrays follow. |
| 901 | 6 | `public void redenominate(double scale)` | Wages and the floor, in the new unit. |
| 910 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

