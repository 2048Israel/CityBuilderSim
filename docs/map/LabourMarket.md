# LabourMarket.java - 873 lines · 47 methods · 13 constants · model

`ham/citybuildersim/LabourMarket.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [JobType](JobType.md) (26), [WageBand](WageBand.md) (20), [DecisionLog](DecisionLog.md) (5), [PayTier](PayTier.md) (5), [EducationType](EducationType.md) (2)

**Used by (19):** [CityNeeds](CityNeeds.md), [Education](Education.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [PeopleScreen](PeopleScreen.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationManager](PopulationManager.md), [PriceLevelCheck](PriceLevelCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [WantedPosts](WantedPosts.md), [WantedPostsCheck](WantedPostsCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | THE DIAL |
| 79 | THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. |
| 151 | THE CURVE |
| 269 | THE LADDER |
| 288 | THE COST OF LIVING |
| 387 | · HALF WHAT PEOPLE EXPECT, HALF THE CHASE (0.7.42) |
| 469 | THE MONTH |
| 588 | READING AND SETTING |
| 640 | THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. |
| 729 | · WHAT THE POLICY TAB ASKS (0.7.36) |
| 775 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `LabourMarket.DEFAULT_MINIMUM_WAGE` | `PayTier.UNSKILLED.getMonthlyWage()` | Where the minimum wage starts, and therefore what the whole ladder is anchored to on month one. |
| 106 | `LabourMarket.MIN_SETTABLE_SHARE` | `.25` | The lowest the dial goes, as a share of the unskilled wage. |
| 109 | `LabourMarket.MAX_SETTABLE_MULTIPLE` | `5.0` | The highest the dial goes, as a multiple of the unskilled wage. |
| 112 | `LabourMarket.MIN_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MIN_SETTABLE_SHARE` | Bounds on the dial, so the screen cannot ask for a negative wage. |
| 114 | `LabourMarket.MAX_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MAX_SETTABLE_MULTIPLE` |  |
| 163 | `LabourMarket.ELASTICITY` | `.5` | How hard a shortage pushes the wage. |
| 174 | `LabourMarket.MAX_MULTIPLE` | `4.0` | How far above base a wage can climb. |
| 177 | `LabourMarket.MIN_MULTIPLE` | `.70` | ...and how far below, before the minimum wage catches it anyway. |
| 208 | `LabourMarket.ADJUST_RATE` | `.12` | How much of the gap to its target a wage closes each month. |
| 218 | `LabourMarket.PINNED_TOLERANCE` | `.02` | How far from its floor a wage counts as PINNED. |
| 346 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 367 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |
| 412 | `LabourMarket.EXPECTED_SHARE` | `.5` | The share of a wage's monthly indexing taken from expected inflation; the rest chases the index at the old speed's share. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private transient DecisionLog decisions` | Where the player's change to the wage floor is written (DecisionLog, 0.7.23); null for one no city holds. |
| 127 | `private double minSettable` | The same bounds, in TODAY's money. |
| 128 | `private double maxSettable` |  |
| 133 | `private double minimumWage` |  |
| 143 | `private boolean floorBinding` | Whether the minimum wage is law yet (R0): false in a city before the date Canada first had one (PolicyDates, MINIMUM_WAGE, 1918), and then the floor binds no wage - the max() in advanceMonth() and isPinned()'s floor a... |
| 220 | `private final double[] wage` |  |
| 232 | `private final double[] tightness` | Scarcity per BAND, not per job type. |
| 253 | `private final double[] licenceTightness` | THE LICENCE PREMIUM (2026-09-06). |
| 254 | `private final double[] licenceMultiple` |  |
| 255 | `private final double[] bandMultiple` |  |
| 369 | `private double costOfLiving` |  |
| 370 | `private double livingTarget` |  |
| 666 | `private double minimumWageAdjustment` | What the player has added to or taken off the floor, as a share. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 820 | **type** `public class LabourMarket` | What labour costs, and why it costs that. |
| 60 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 62 | 3 | `private void decided(String kind, String label)` |  |

### THE DIAL (lines 66-78)

### THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. (lines 79-150)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 1 | `public double getMinSettable()` |  |
| 131 | 1 | `public double getMaxSettable()` |  |
| 146 | 1 | `public boolean isFloorBinding()` | Whether the floor binds (see floorBinding). |
| 149 | 1 | `public void setFloorBinding(boolean binding)` | Game's push, with the policy's month: whether the minimum wage is law in this city yet. |

### THE CURVE (lines 151-268)

| line | len | member | says |
|---:|---:|---|---|
| 186 | 3 | `public static double multipleAt(double tightness)` | THE CURVE ITSELF, in one place (0.7.18): the multiple a band's wage aims at when its posts stand at this tightness against its supply. |
| 196 | 3 | `public static double tightnessAt(double multiple)` | ...and the curve read backwards: the tightness at which a band's wage aims at this multiple of its base, clamps aside. |
| 258 | 6 | `public static boolean isGated(JobType job)` | True for a job that only a licence holder can fill - see EducationType. |
| 265 | 3 | `public LabourMarket()` |  |

### THE LADDER (lines 269-287)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 4 | `public static double ratioOf(JobType job)` | What this job pays relative to the unskilled floor. |

### THE COST OF LIVING (lines 288-386)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 5 | `public void updateCostOfLiving(double priceIndex)` | A REAL index since phase 5 - see PriceIndex. |

### HALF WHAT PEOPLE EXPECT, HALF THE CHASE (0.7.42) (lines 387-468)

| line | len | member | says |
|---:|---:|---|---|
| 422 | 6 | `public void updateCostOfLiving(double priceIndex, double expectedMonthly)` | The cost of living, once the basket is based: half from what people expect, half the chase (see HALF WHAT PEOPLE EXPECT, HALF THE CHASE). |
| 430 | 1 | `public double getCostOfLiving()` | What wages have been lifted by, chasing the cost of living. |
| 433 | 1 | `public double getLivingTarget()` | Where they are heading. |
| 435 | 3 | `public void setCostOfLiving(double value)` |  |
| 440 | 28 | `public double baseWage(JobType job)` | What this job would pay in a city with exactly enough people for it. |

### THE MONTH (lines 469-587)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 3 | `public void advanceMonth(double[] bandPosts, double[] bandSupply)` | Re-prices every job type against how hard it is to staff. |
| 489 | 61 | `public void advanceMonth(double[] bandPosts, double[] bandSupply, long[] jobPosts, double[] licensedHeads)` |  |
| 564 | 11 | `public boolean isPinned(WageBand band)` | True when this job's wage has fallen as far as the market will let it and there is still nowhere for those workers to go. |
| 577 | 6 | `public final void resetToBase()` | Puts every wage back on its base. |
| 584 | 3 | `private static double clamp(double v, double lo, double hi)` |  |

### READING AND SETTING (lines 588-639)

| line | len | member | says |
|---:|---:|---|---|
| 592 | 1 | `public double[] getWages()` |  |
| 593 | 1 | `public double getWage(JobType job)` |  |
| 594 | 1 | `public double getTightness(WageBand band)` |  |
| 596 | 1 | `public double getLicenceTightness(JobType job)` | Posts over licence holders for a gated job; 0 for an ungated one or one with no posts. |
| 598 | 1 | `public double getLicenceMultiple(JobType job)` | The licence premium's target multiple this month, 1 when there is none. |
| 600 | 1 | `public double getBandMultiple(WageBand band)` | The band's own multiple this month, without any licence premium on top. |
| 618 | 5 | `public double licencePremium(JobType job)` | What a licensed profession is paid OVER ITS OWN BAND, as actually paid. |
| 631 | 9 | `public double bandPremium(WageBand band)` | The premium the BAND is paying, read off an ungated job in it. |

### THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. (lines 640-728)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 1 | `public double getMinimumWage()` | The real floor, in founding money. |
| 672 | 1 | `public double getMinimumWageBase()` | The same figure. |
| 675 | 1 | `public double getMinimumWageAdjustment()` | The player's nudge, as a share. |
| 688 | 3 | `public double cashMinimumWage()` | The floor in TODAY'S money: the real floor, lifted by the cost of living. |
| 693 | 3 | `public void setMinimumWageAdjustment(double share)` | Moves the percentage. |
| 698 | 4 | `public double premium(JobType job)` | How far above its base a job is paying - 1.00 is the going rate. |
| 721 | 7 | `public void setMinimumWage(double value)` | Moves the dial. |

### WHAT THE POLICY TAB ASKS (0.7.36) (lines 729-774)

| line | len | member | says |
|---:|---:|---|---|
| 736 | 3 | `public double cashAt(double floor)` | What a real floor of `floor`, in founding money, comes to in today's money: cashMinimumWage()'s rule at another setting. |
| 741 | 4 | `public double floorForCash(double cash)` | ...and back: the founding figure whose cash floor is `cash` today - what the Policy tab's dial, read in today's money, sets. |
| 754 | 6 | `public double targetWageAt(JobType job, double floor)` | Where a job's wage heads with the real floor at `floor`: advanceMonth()'s target - its base at that floor times this month's band and licence multiples, held to MAX_MULTIPLE - and never under the floor in today's mone... |
| 762 | 7 | `public double bestWageIn(WageBand band)` | The best-paid job somebody in this band can hold (0.7.36; the Policy tab's own until then): what a course's fee is weighed against. |
| 771 | 3 | `public void setMinimumWageBase(double value)` | Sets the real floor, in founding money. |

### SAVE AND RESTORE (lines 775-873)

| line | len | member | says |
|---:|---:|---|---|
| 785 | 26 | `public double[] state()` |  |
| 812 | 3 | `private int diagnosticsLength()` |  |
| 824 | 27 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded - the same rule the health and healthcare arrays follow. |
| 859 | 6 | `public void redenominate(double scale)` | Wages and the floor, in the new unit. |
| 868 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

