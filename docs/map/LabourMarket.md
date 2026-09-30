# LabourMarket.java - 750 lines · 38 methods · 12 constants · model

`ham/citybuildersim/LabourMarket.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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
>     base   = minimumWage x ratio[type]
>     tight  = posts / qualified workers
>     target = base x clamp(tight ^ ELASTICITY, MIN_MULTIPLE, MAX_MULTIPLE)
>     wage  += (target - wage) x ADJUST_RATE
>     wage   = max(wage, minimumWage)
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

**Uses:** [JobType](JobType.md) (23), [WageBand](WageBand.md) (17), [PayTier](PayTier.md) (5), [EducationType](EducationType.md) (2)

**Used by (11):** [Education](Education.md), [Game](Game.md), [HistorySave](HistorySave.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationManager](PopulationManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 56 | THE DIAL |
| 69 | THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. |
| 125 | THE CURVE |
| 243 | THE LADDER |
| 262 | THE COST OF LIVING |
| 394 | THE MONTH |
| 505 | READING AND SETTING |
| 557 | THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. |
| 652 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `LabourMarket.DEFAULT_MINIMUM_WAGE` | `PayTier.UNSKILLED.getMonthlyWage()` | Where the minimum wage starts, and therefore what the whole ladder is anchored to on month one. |
| 96 | `LabourMarket.MIN_SETTABLE_SHARE` | `.25` | The lowest the dial goes, as a share of the unskilled wage. |
| 99 | `LabourMarket.MAX_SETTABLE_MULTIPLE` | `5.0` | The highest the dial goes, as a multiple of the unskilled wage. |
| 102 | `LabourMarket.MIN_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MIN_SETTABLE_SHARE` | Bounds on the dial, so the screen cannot ask for a negative wage. |
| 104 | `LabourMarket.MAX_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MAX_SETTABLE_MULTIPLE` |  |
| 137 | `LabourMarket.ELASTICITY` | `.5` | How hard a shortage pushes the wage. |
| 148 | `LabourMarket.MAX_MULTIPLE` | `4.0` | How far above base a wage can climb. |
| 151 | `LabourMarket.MIN_MULTIPLE` | `.70` | ...and how far below, before the minimum wage catches it anyway. |
| 182 | `LabourMarket.ADJUST_RATE` | `.12` | How much of the gap to its target a wage closes each month. |
| 192 | `LabourMarket.PINNED_TOLERANCE` | `.02` | How far from its floor a wage counts as PINNED. |
| 318 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 334 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 117 | `private double minSettable` | The same bounds, in TODAY's money. |
| 118 | `private double maxSettable` |  |
| 123 | `private double minimumWage` |  |
| 194 | `private final double[] wage` |  |
| 206 | `private final double[] tightness` | Scarcity per BAND, not per job type. |
| 227 | `private final double[] licenceTightness` | THE LICENCE PREMIUM (2026-09-06). |
| 228 | `private final double[] licenceMultiple` |  |
| 229 | `private final double[] bandMultiple` |  |
| 336 | `private double costOfLiving` |  |
| 337 | `private double livingTarget` |  |
| 583 | `private double minimumWageAdjustment` | What the player has added to or taken off the floor, as a share. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 697 | **type** `public class LabourMarket` | What labour costs, and why it costs that. |

### THE DIAL (lines 56-68)

### THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. (lines 69-124)

| line | len | member | says |
|---:|---:|---|---|
| 120 | 1 | `public double getMinSettable()` |  |
| 121 | 1 | `public double getMaxSettable()` |  |

### THE CURVE (lines 125-242)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 3 | `public static double multipleAt(double tightness)` | THE CURVE ITSELF, in one place (0.7.18): the multiple a band's wage aims at when its posts stand at this tightness against its supply. |
| 170 | 3 | `public static double tightnessAt(double multiple)` | ...and the curve read backwards: the tightness at which a band's wage aims at this multiple of its base, clamps aside. |
| 232 | 6 | `public static boolean isGated(JobType job)` | True for a job that only a licence holder can fill - see EducationType. |
| 239 | 3 | `public LabourMarket()` |  |

### THE LADDER (lines 243-261)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 4 | `public static double ratioOf(JobType job)` | What this job pays relative to the unskilled floor. |

### THE COST OF LIVING (lines 262-393)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 5 | `public void updateCostOfLiving(double priceIndex)` | A REAL index since phase 5 - see PriceIndex. |
| 355 | 1 | `public double getCostOfLiving()` | What wages have been lifted by, chasing the cost of living. |
| 358 | 1 | `public double getLivingTarget()` | Where they are heading. |
| 360 | 3 | `public void setCostOfLiving(double value)` |  |
| 365 | 28 | `public double baseWage(JobType job)` | What this job would pay in a city with exactly enough people for it. |

### THE MONTH (lines 394-504)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 3 | `public void advanceMonth(double[] bandPosts, double[] bandSupply)` | Re-prices every job type against how hard it is to staff. |
| 414 | 60 | `public void advanceMonth(double[] bandPosts, double[] bandSupply, int[] jobPosts, double[] licensedHeads)` |  |
| 482 | 10 | `public boolean isPinned(WageBand band)` | True when this job's wage has fallen as far as the market will let it and there is still nowhere for those workers to go. |
| 494 | 6 | `public final void resetToBase()` | Puts every wage back on its base. |
| 501 | 3 | `private static double clamp(double v, double lo, double hi)` |  |

### READING AND SETTING (lines 505-556)

| line | len | member | says |
|---:|---:|---|---|
| 509 | 1 | `public double[] getWages()` |  |
| 510 | 1 | `public double getWage(JobType job)` |  |
| 511 | 1 | `public double getTightness(WageBand band)` |  |
| 513 | 1 | `public double getLicenceTightness(JobType job)` | Posts over licence holders for a gated job; 0 for an ungated one or one with no posts. |
| 515 | 1 | `public double getLicenceMultiple(JobType job)` | The licence premium's target multiple this month, 1 when there is none. |
| 517 | 1 | `public double getBandMultiple(WageBand band)` | The band's own multiple this month, without any licence premium on top. |
| 535 | 5 | `public double licencePremium(JobType job)` | What a licensed profession is paid OVER ITS OWN BAND, as actually paid. |
| 548 | 9 | `public double bandPremium(WageBand band)` | The premium the BAND is paying, read off an ungated job in it. |

### THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. (lines 557-651)

| line | len | member | says |
|---:|---:|---|---|
| 586 | 1 | `public double getMinimumWage()` | The real floor, in founding money. |
| 589 | 1 | `public double getMinimumWageBase()` | The same figure. |
| 592 | 1 | `public double getMinimumWageAdjustment()` | The player's nudge, as a share. |
| 605 | 3 | `public double cashMinimumWage()` | The floor in TODAY'S money: the real floor, lifted by the cost of living. |
| 610 | 3 | `public void setMinimumWageAdjustment(double share)` | Moves the percentage. |
| 615 | 4 | `public double premium(JobType job)` | How far above its base a job is paying - 1.00 is the going rate. |
| 643 | 3 | `public void setMinimumWage(double value)` | Moves the dial. |
| 648 | 3 | `public void setMinimumWageBase(double value)` | Sets the real floor, in founding money. |

### SAVE AND RESTORE (lines 652-750)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 26 | `public double[] state()` |  |
| 689 | 3 | `private int diagnosticsLength()` |  |
| 701 | 27 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded - the same rule the health and healthcare arrays follow. |
| 736 | 6 | `public void redenominate(double scale)` | Wages and the floor, in the new unit. |
| 745 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

