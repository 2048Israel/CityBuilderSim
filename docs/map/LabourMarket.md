# LabourMarket.java - 1,115 lines · 65 methods · 19 constants · model

`ham/citybuildersim/LabourMarket.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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
>     base   = goingRate x ratio[type] x costOfLiving
>     tight  = posts / qualified workers
>     target = base x clamp(tight ^ ELASTICITY, MIN_MULTIPLE, MAX_MULTIPLE)
>     wage  += (target - wage) x ADJUST_RATE
>     wage   = max(wage, cashMinimumWage())     // the floor in today's money, which isPinned() reads too (0.7.47)
> 
> Four things in there are load-bearing and none of them is the exponent.
> 
> THE GOING RATE IS THE BASE (WG1, 2026-11-10). It was "THE MINIMUM WAGE IS THE
> BASE" from the day this class was written - Jerus: "make the base a modifiable
> min wage" - and the whole ladder was a multiple of the floor, so raising the
> floor lifted every wage in the city and 94% of workers were paid exactly the
> floor (spec-wages 2.2). Jerus, 2026-11-02: "yes a real minimum wage". The
> ladder is now anchored on the GOING RATE: what an unskilled post pays where
> there are exactly enough workers, ladder.anchor(), in founding money and
> lifted by the cost of living as every base is. The multiples are still READ
> OFF PayTier rather than invented (see ratioOf), which still gives the
> property that matters: at the going rate this market pays exactly what the
> game paid before it existed, so every balance measurement already taken
> survives. The floor is not the anchor; it starts at DEFAULT_FLOOR_SHARE of it.
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
> needs fewer workers, and it spirals. The minimum wage stops that. Until WG1
> (2026-11-10) the unskilled base WAS the minimum wage, so an unskilled surplus
> could not be priced away at all; the floor is now a real minimum, a figure of
> its own that only lifts the wages UNDER it (wage = max(wage, cash floor)), and
> the unskilled wage can fall to the floor and no further. A floor over what the
> market would pay still turns a price adjustment into a QUANTITY one: the
> surplus cannot be priced away, so it is unemployed or it leaves, which is
> exactly what a binding minimum wage does in the real world and exactly what
> Migration's surplus departures model. The player sets the number and lives
> with which of the two costs they would rather pay.
> 
> THE FLOOR'S TIE (WG1). The floor is set in founding money and lifted by the
> ... (4 more lines in the source)

**Uses:** [JobType](JobType.md) (31), [WageBand](WageBand.md) (22), [DecisionLog](DecisionLog.md) (10), [WageLadder](WageLadder.md) (7), [PayTier](PayTier.md) (3), [EducationType](EducationType.md) (2)

**Used by (23):** [ArrivalMixCheck](ArrivalMixCheck.md), [CityNeeds](CityNeeds.md), [DiscoveryCheck](DiscoveryCheck.md), [Education](Education.md), [ExpectationsCheck](ExpectationsCheck.md), [FloorView](FloorView.md), [Game](Game.md), [HistorySave](HistorySave.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [PeopleScreen](PeopleScreen.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationManager](PopulationManager.md), [PriceLevelCheck](PriceLevelCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [WageLadderCheck](WageLadderCheck.md), [WantedPosts](WantedPosts.md), [WantedPostsCheck](WantedPostsCheck.md)

## Sections

| line | section |
|---:|---|
| 81 | THE DIAL |
| 119 | THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. |
| 240 | THE CURVE |
| 358 | THE LADDER |
| 385 | THE COST OF LIVING |
| 491 | · HALF WHAT PEOPLE EXPECT, HALF THE CHASE (0.7.42) |
| 578 | THE MONTH |
| 723 | READING AND SETTING |
| 775 | THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. |
| 871 | · WHAT THE POLICY TAB ASKS (0.7.36) |
| 921 | THE FLOOR'S TIE (WG1, 2026-11-10; spec-wages 3.2) |
| 992 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 935 | `LabourMarket.FloorTie.PRICES` |  |
| 935 | `LabourMarket.FloorTie.AVERAGE_WAGE` |  |
| 935 | `LabourMarket.FloorTie.NOTHING` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `LabourMarket.GOING_RATE` | `PayTier.UNSKILLED.getMonthlyWage()` | The going rate on today's ladder, in founding money, for a reader with no city: what an unskilled post pays where there are exactly enough workers, and what the ladder is anchored on (a city's market reads getGoingRat... |
| 95 | `LabourMarket.DEFAULT_FLOOR_SHARE` | `.85` | Where a new city's floor starts, as a share of the going rate: Nova Scotia's legal minimum, $17.00 an hour since 1 October 2026, over the material-handling labourer's Job Bank median, $20.00, which the unskilled rung ... |
| 101 | `LabourMarket.DEFAULT_MINIMUM_WAGE` | `GOING_RATE * DEFAULT_FLOOR_SHARE` | Where the minimum wage starts on today's ladder. |
| 104 | `LabourMarket.NEAR_FLOOR` | `.10` | How far above the floor a wage counts as near it: the reach the spillover evidence gives the next few dollars, an estimate (spec-wages 3.6). |
| 107 | `LabourMarket.DEFAULT_TIE_SHARE` | `.45` | The floor's share of the average wage when it is tied to it: the middle of the OECD's 37-55% (28 of the 33 nations in the world file), an estimate (spec-wages 3.2). |
| 110 | `LabourMarket.MIN_TIE_SHARE` | `.15` | The lowest share of the average wage a tie to it can ask: under the world file's lowest minimum-to-average ratio (.17, the United States), so even that nation can be copied. |
| 117 | `LabourMarket.MAX_TIE_SHARE` | `.70` | The highest share of the average wage a tie to it can ask: over the world file's highest minimum-to-average ratio (.69, Costa Rica), so every nation can be copied and none exceeded, and under 1, where the floor is las... |
| 146 | `LabourMarket.MIN_SETTABLE_SHARE` | `.25` | The lowest the dial goes, as a share of the unskilled wage. |
| 149 | `LabourMarket.MAX_SETTABLE_MULTIPLE` | `5.0` | The highest the dial goes, as a multiple of the unskilled wage. |
| 152 | `LabourMarket.MIN_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MIN_SETTABLE_SHARE` | Bounds on the dial, so the screen cannot ask for a negative wage. |
| 154 | `LabourMarket.MAX_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MAX_SETTABLE_MULTIPLE` |  |
| 252 | `LabourMarket.ELASTICITY` | `.5` | How hard a shortage pushes the wage. |
| 263 | `LabourMarket.MAX_MULTIPLE` | `4.0` | How far above base a wage can climb. |
| 266 | `LabourMarket.MIN_MULTIPLE` | `.70` | ...and how far below, before the minimum wage catches it anyway. |
| 297 | `LabourMarket.ADJUST_RATE` | `.12` | How much of the gap to its target a wage closes each month. |
| 307 | `LabourMarket.PINNED_TOLERANCE` | `.02` | How far from its floor a wage counts as PINNED. |
| 443 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 464 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |
| 516 | `LabourMarket.EXPECTED_SHARE` | `.5` | The share of a wage's monthly indexing taken from expected inflation; the rest chases the index at the old speed's share. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private transient DecisionLog decisions` | Where the player's change to the wage floor is written (DecisionLog, 0.7.23); null for one no city holds. |
| 167 | `private double minSettable` | The same bounds, in TODAY's money. |
| 168 | `private double maxSettable` |  |
| 175 | `private double goingRate` | The going rate in the market's current unit (founding money): the ladder's anchor, what every base is a multiple of. |
| 191 | `private transient WageLadder ladder` | The wage ladder this market prices on (R0d): today's, PayTier's, in every city but one founded the 1900 way, whose market pays 1901's (WageLadder). |
| 222 | `private double minimumWage` |  |
| 232 | `private boolean floorBinding` | Whether the minimum wage is law yet (R0): false in a city before the date Canada first had one (PolicyDates, MINIMUM_WAGE, 1918), and then the floor binds no wage - the max() in advanceMonth() and isPinned()'s floor a... |
| 309 | `private final double[] wage` |  |
| 321 | `private final double[] tightness` | Scarcity per BAND, not per job type. |
| 342 | `private final double[] licenceTightness` | THE LICENCE PREMIUM (2026-09-06). |
| 343 | `private final double[] licenceMultiple` |  |
| 344 | `private final double[] bandMultiple` |  |
| 466 | `private double costOfLiving` |  |
| 467 | `private double livingTarget` |  |
| 801 | `private double minimumWageAdjustment` | What the player has added to or taken off the floor, as a share. |
| 937 | `private final String label` |  |
| 944 | `private FloorTie floorTie` |  |
| 947 | `private double tieShare` | The floor's share of the average wage when it is tied to it (AVERAGE_WAGE); kept, unused, under another tie. |
| 953 | `private double lastAverageWage` | The average wage the tie last read, in today's money (0 before any month has said one, and for a nobody-employed month). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 69 | 1047 | **type** `public class LabourMarket` | What labour costs, and why it costs that. |
| 75 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 77 | 3 | `private void decided(String kind, String label)` |  |

### THE DIAL (lines 81-118)

### THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES. (lines 119-239)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 1 | `public double getGoingRate()` | The going rate, in founding money at the market's current unit: what an unskilled post pays where there are exactly enough workers. |
| 181 | 1 | `public double cashGoingRate()` | The going rate in TODAY's money: the founding figure lifted by the cost of living, as every base is. |
| 183 | 1 | `public double getMinSettable()` |  |
| 184 | 1 | `public double getMaxSettable()` |  |
| 194 | 1 | `public WageLadder getLadder()` | The ladder this market prices on (R0d). |
| 200 | 6 | `public void setLadder(WageLadder l)` | Puts this market on a ladder (R0d): the ladder and the dial's bounds, which are multiples of its anchor (today's are exactly MIN_SETTABLE and MAX_SETTABLE). |
| 208 | 1 | `private double lowestSettable()` | The dial's bounds on this market's ladder at the founding unit: the constants on today's, multiples of the 1901 anchor on 1901's. |
| 209 | 1 | `private double highestSettable()` |  |
| 216 | 5 | `public void foundOn(WageLadder l)` | A city founded on a ladder (R0d): its bounds, its minimum wage at DEFAULT_FLOOR_SHARE of the ladder's anchor (WG1; the anchor itself until then) - the field, not setMinimumWage(), which would log a decision nobody mad... |
| 235 | 1 | `public boolean isFloorBinding()` | Whether the floor binds (see floorBinding). |
| 238 | 1 | `public void setFloorBinding(boolean binding)` | Game's push, with the policy's month: whether the minimum wage is law in this city yet. |

### THE CURVE (lines 240-357)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 3 | `public static double multipleAt(double tightness)` | THE CURVE ITSELF, in one place (0.7.18): the multiple a band's wage aims at when its posts stand at this tightness against its supply. |
| 285 | 3 | `public static double tightnessAt(double multiple)` | ...and the curve read backwards: the tightness at which a band's wage aims at this multiple of its base, clamps aside. |
| 347 | 6 | `public static boolean isGated(JobType job)` | True for a job that only a licence holder can fill - see EducationType. |
| 354 | 3 | `public LabourMarket()` |  |

### THE LADDER (lines 358-384)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 3 | `public static double ratioOf(JobType job)` | What this job pays relative to the unskilled floor. |
| 381 | 3 | `public double ratio(JobType job)` | What this job pays relative to the unskilled floor on this market's ladder (R0d): ratioOf() on today's, the census's ratios on 1901's. |

### THE COST OF LIVING (lines 385-490)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 7 | `public void updateCostOfLiving(double priceIndex)` | A REAL index since phase 5 - see PriceIndex. |
| 487 | 3 | `private void holdTheCashFloor(double cash)` | A floor tied to nothing (WG1) is a cash figure: after the cost of living moves, the real floor is whatever keeps `cash` its cash. |

### HALF WHAT PEOPLE EXPECT, HALF THE CHASE (0.7.42) (lines 491-577)

| line | len | member | says |
|---:|---:|---|---|
| 526 | 8 | `public void updateCostOfLiving(double priceIndex, double expectedMonthly)` | The cost of living, once the basket is based: half from what people expect, half the chase (see HALF WHAT PEOPLE EXPECT, HALF THE CHASE). |
| 536 | 1 | `public double getCostOfLiving()` | What wages have been lifted by, chasing the cost of living. |
| 539 | 1 | `public double getLivingTarget()` | Where they are heading. |
| 541 | 3 | `public void setCostOfLiving(double value)` |  |
| 546 | 31 | `public double baseWage(JobType job)` | What this job would pay in a city with exactly enough people for it. |

### THE MONTH (lines 578-722)

| line | len | member | says |
|---:|---:|---|---|
| 590 | 3 | `public void advanceMonth(double[] bandPosts, double[] bandSupply)` | Re-prices every job type against how hard it is to staff. |
| 598 | 63 | `public void advanceMonth(double[] bandPosts, double[] bandSupply, long[] jobPosts, double[] licensedHeads)` |  |
| 675 | 11 | `public boolean isPinned(WageBand band)` | True when this job's wage has fallen as far as the market will let it and there is still nowhere for those workers to go. |
| 691 | 3 | `public boolean isPaidTheFloor(JobType job)` | Whether the floor is what this job is paid (WG1): the law exists and the wage is within PINNED_TOLERANCE of the cash floor, at or under it times (1 + PINNED_TOLERANCE). |
| 696 | 6 | `public boolean isPaidTheFloor(WageBand band)` | Whether any job of the band is paid the floor (see isPaidTheFloor(JobType)). |
| 707 | 3 | `public boolean isNearTheFloor(JobType job)` | Whether this job is paid a little over the floor (WG1): the law exists, the job is not paid the floor, and its wage is at or under the cash floor times (1 + NEAR_FLOOR) - the wages a rise in the floor is likely to rea... |
| 712 | 6 | `public final void resetToBase()` | Puts every wage back on its base. |
| 719 | 3 | `private static double clamp(double v, double lo, double hi)` |  |

### READING AND SETTING (lines 723-774)

| line | len | member | says |
|---:|---:|---|---|
| 727 | 1 | `public double[] getWages()` |  |
| 728 | 1 | `public double getWage(JobType job)` |  |
| 729 | 1 | `public double getTightness(WageBand band)` |  |
| 731 | 1 | `public double getLicenceTightness(JobType job)` | Posts over licence holders for a gated job; 0 for an ungated one or one with no posts. |
| 733 | 1 | `public double getLicenceMultiple(JobType job)` | The licence premium's target multiple this month, 1 when there is none. |
| 735 | 1 | `public double getBandMultiple(WageBand band)` | The band's own multiple this month, without any licence premium on top. |
| 753 | 5 | `public double licencePremium(JobType job)` | What a licensed profession is paid OVER ITS OWN BAND, as actually paid. |
| 766 | 9 | `public double bandPremium(WageBand band)` | The premium the BAND is paying, read off an ungated job in it. |

### THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS. (lines 775-870)

| line | len | member | says |
|---:|---:|---|---|
| 804 | 1 | `public double getMinimumWage()` | The real floor, in founding money. |
| 807 | 1 | `public double getMinimumWageBase()` | The same figure. |
| 810 | 1 | `public double getMinimumWageAdjustment()` | The player's nudge, as a share. |
| 823 | 3 | `public double cashMinimumWage()` | The floor in TODAY'S money: the real floor, lifted by the cost of living. |
| 828 | 3 | `public void setMinimumWageAdjustment(double share)` | Moves the percentage. |
| 833 | 4 | `public double premium(JobType job)` | How far above its base a job is paying - 1.00 is the going rate. |
| 860 | 10 | `public void setMinimumWage(double value)` | Moves the dial. |

### WHAT THE POLICY TAB ASKS (0.7.36) (lines 871-920)

| line | len | member | says |
|---:|---:|---|---|
| 878 | 3 | `public double cashAt(double floor)` | What a real floor of `floor`, in founding money, comes to in today's money: cashMinimumWage()'s rule at another setting. |
| 883 | 4 | `public double floorForCash(double cash)` | ...and back: the founding figure whose cash floor is `cash` today - what the Policy tab's dial, read in today's money, sets. |
| 899 | 7 | `public double targetWageAt(JobType job, double floor)` | Where a job's wage heads with the real floor at `floor`: advanceMonth()'s target - its base times this month's band and licence multiples, held to MAX_MULTIPLE - and never under the floor in today's money at that sett... |
| 908 | 7 | `public double bestWageIn(WageBand band)` | The best-paid job somebody in this band can hold (0.7.36; the Policy tab's own until then): what a course's fee is weighed against. |
| 917 | 3 | `public void setMinimumWageBase(double value)` | Sets the real floor, in founding money. |

### THE FLOOR'S TIE (WG1, 2026-11-10; spec-wages 3.2) (lines 921-991)

| line | len | member | says |
|---:|---:|---|---|
| 934 | 9 | **type** `public enum FloorTie` | What the floor keeps pace with. |
| 938 | 1 | `FloorTie(String label)` _(in LabourMarket.FloorTie)_ |  |
| 941 | 1 | `public String label()` _(in LabourMarket.FloorTie)_ | The tie in words, as the Policy tab and the decision log name it. |
| 956 | 1 | `public FloorTie getFloorTie()` | What the floor keeps pace with. |
| 959 | 1 | `public double getTieShare()` | The floor's share of the average wage when it is tied to it. |
| 966 | 13 | `public void setFloorTie(FloorTie tie, double share)` | Ties the floor (WG1): the tie, and its share of the average wage (held to MIN_TIE_SHARE..MAX_TIE_SHARE; the share means something under AVERAGE_WAGE only). |
| 985 | 6 | `public void followAverageWage(double averageWage)` | Game's monthly word on the average wage (WG1), the wage bill over the posts filled, before the wages are walked. |

### SAVE AND RESTORE (lines 992-1115)

| line | len | member | says |
|---:|---:|---|---|
| 1002 | 31 | `public double[] state()` |  |
| 1034 | 3 | `private int diagnosticsLength()` |  |
| 1046 | 41 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded - the same rule the health and healthcare arrays follow. |
| 1098 | 8 | `public void redenominate(double scale)` | Wages and the floor, in the new unit. |
| 1109 | 5 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

