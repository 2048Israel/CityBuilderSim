# ConstructionControl.java - 932 lines · 67 methods · 11 constants · model

`ham/citybuildersim/ConstructionControl.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> The player's hand on the construction queue (0.7.22): the order the
> city's own sites are served in, the sites it has put on overtime, the
> orders it has stopped and the shells they left, the buildings it is
> pulling down, what it paid to buy a business's buildings to do so, and
> since 0.7.70 the gravel roads it is paving (F).
> 
> WHY. Until 0.7.22 an order, once placed, was out of the player's hands:
> the builders' crews were shared by the rule (BuildingManager, EVERY
> BUILDING GETS THE CREW IT CAN USE), nothing could be stopped, and nothing
> the city owned could be taken down. Jerus, on the play-through of 0.7.19,
> asked for "not only repirotize and cancel but also destroy buildings, like
> you yourself destroy buildings", and chose the mechanics on 2026-09-30:
> priority "Both" (a free reorder of the city's own sites, and overtime
> paid for), cancel "Keep the half-built shell", demolish "City's, plus
> buy-outs". Each rule below names its source.
> 
> WHAT THIS CLASS HOLDS: the state, saved whole under one key
> (DataSave.constructionControl; a format-27 save has none of it and loads
> with none), and the arithmetic every rule is made of, as statics a
> harness can hold the game to. BuildingManager applies the crew rules in
> the month's advance; Game, THE PLAYER'S HAND ON THE QUEUE, moves the
> money. None of the rules runs unless the player has used it: engaged() is
> false in a city nobody has touched, and the advance then takes the path
> it always took, to the bit. (Each stack's run - the page's "done of
> total" and "paid" - is kept in every city, on every order, and saved with
> the rest; it is bookkeeping and moves nothing. A paving, F, rides on the
> Paved Road stack's own advance in every city, engaged or not.)
> 
> A SITE is what the model keeps on site: one stack per building type
> ("B" + its template id) - however many orders are on it - and, since
> this class, each demolition the city has ordered ("D" + its number). The
> CITY'S sites are the stacks whose every order on site is the city's, and
> the demolitions. A stack the city shares with an investor's order is not
> the city's to reorder, rush or stop: its progress is one pool, and the
> model cannot divide it by who paid for which building.

**Uses:** [SaveCopy](SaveCopy.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (5), [BuildingsStacks](BuildingsStacks.md) (4)

**Used by (28):** [AdviceValueOracle](AdviceValueOracle.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankCheck](BankCheck.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FitoutCheck](FitoutCheck.md), [Game](Game.md), [Inbox](Inbox.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [RoadCheck](RoadCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [UserInterface](UserInterface.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md)

## Sections

| line | section |
|---:|---|
| 42 | A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS |
| 72 | B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES |
| 164 | C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT |
| 196 | D. DEMOLISH: THE CITY'S OWN BUILDINGS |
| 262 | E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY |
| 301 | F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70) |
| 369 | · THE STATE, as the save carries it |
| 610 | · the key of a site |
| 621 | · A. the order |
| 653 | · B. the rushes |
| 713 | · C. the cancels and the shells |
| 762 | · D. the demolitions |
| 799 | · E. the buy-outs |
| 805 | · F. the pavings |
| 846 | · each stack's run |
| 868 | · a reform |
| 880 | · THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 115 | `ConstructionControl.STANDARD_HOURS` | `40` | The normal working week the report measures against, in hours. |
| 118 | `ConstructionControl.OVERTIME_HOURS` | `50` | The week on overtime: the Business Roundtable's five tens (Report C-2, Figure 4). |
| 121 | `ConstructionControl.OVERTIME_RATE` | `1.5` | What an hour over the standard week is paid at: time and a half (Canada Labour Code, section 174). |
| 124 | `ConstructionControl.WEEKS_A_MONTH` | `52.0 / 12.0` | Weeks in a month for averaging the report's table: 52 / 12, the 4.33 the brief reads it at. |
| 127 | `ConstructionControl.OVERTIME_WEEKS_ENDING` | `{ 2, 4, 6, 8, 10 }` | Where each step of the report's 50-hour curve ends, in weeks on the schedule (Report C-2, Figure 4); the last step runs on. |
| 130 | `ConstructionControl.OVERTIME_PRODUCTIVITY` | `{ 0.926, 0.90, 0.87, 0.80, 0.752, 0.750 }` | Productivity on a 50-hour week against a 40-hour one, for each step above and beyond the last (Report C-2, Figure 4). |
| 133 | `ConstructionControl.OVERTIME_WAGE_BILL` | `(STANDARD_HOURS +(OVERTIME_HOURS - STANDARD_HOURS) * OVERTIME_RATE) / STANDAR...` | The crews' wage bill on overtime over their normal bill: (40 + 10 x 1.5) / 40 = 1.375. |
| 249 | `ConstructionControl.DEMOLITION_SHARE` | `0.05` | The share of a building's construction points its demolition is: Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April 2017), over the average new single-family home of 2015, $289,415 (NAHB) - 5.1%, ta... |
| 260 | `ConstructionControl.SALVAGE_SHARE` | `0.25` | The share of a building's construction material its demolition recovers as material: 20 to 30 percent of building-related construction and demolition debris was recovered for processing and recycling in the United Sta... |
| 359 | `ConstructionControl.PAVE_FROM` | `"Gravel Road"` | The road that can be paved (0.7.70): a Gravel Road, and nothing else. |
| 362 | `ConstructionControl.PAVE_TO` | `"Paved Road"` | ...and what it is paved to: a Paved Road. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 375 | `public String key` |  |
| 377 | `public boolean on` | Whether the player has it on; off until the next month passes, so a stop and a restart between two presses keep the count. |
| 379 | `public int months` | Consecutive months worked on overtime so far. |
| 395 | `public int templateId` |  |
| 396 | `public String building` |  |
| 397 | `public long buildings` |  |
| 399 | `public double progress` | The points of work it holds. |
| 401 | `public double materialsOwed` | Units of material it has still to draw - what a restart quotes for. |
| 403 | `public double refunded` | What came back to the city when it stopped. |
| 405 | `public int month` | The month it stopped. |
| 411 | `public java.util.List<BuildingsStacks.Lot> lots` | The equipment its buildings' orders froze (R1e; BuildingsStacks' lots): the buildings that come without a piece, by the pieces; the rest are the whole template, as every building stopped before R1e is. |
| 431 | `public int id` |  |
| 432 | `public int templateId` |  |
| 433 | `public String building` |  |
| 434 | `public long buildings` |  |
| 436 | `public double points` | The points of work it is, all told: DEMOLITION_SHARE of the work it holds. |
| 437 | `public double progress` |  |
| 439 | `public double salvageUnits` | Units of construction material salvaged when it completes, as quoted: SALVAGE_SHARE of what the buildings hold since 0.7.106 (all of it until then). |
| 441 | `public double landSqFt` | The ground it holds until it completes, in square feet. |
| 443 | `public double paid` | What the city paid the builders for it. |
| 445 | `public double value` | ...and what of that they have still to earn. |
| 447 | `public String from` | "City" for the city's own, a sector's key for a building it bought, "Shell" for a stopped site. |
| 449 | `public int month` | The month it was ordered. |
| 451 | `public boolean closing` | True from the order until the month starts and the buildings close (Game.closeDemolished()): they still stand, and hold their own ground. |
| 453 | `public double groundPaid` | What the city paid a bought building's owner for its ground, for the demolition log; nothing for the city's own. |
| 477 | `public int month` |  |
| 478 | `public int templateId` |  |
| 479 | `public String building` |  |
| 480 | `public long buildings` |  |
| 481 | `public String sector` |  |
| 482 | `public double buildingValue` |  |
| 483 | `public double ground` |  |
| 484 | `public double businessLoss` |  |
| 486 | `public double months` | The months a replacement would have taken, which the business loss was paid for. |
| 503 | `public int templateId` |  |
| 504 | `public long built` |  |
| 505 | `public double billed` |  |
| 525 | `public long roads` |  |
| 527 | `public long ahead` | Paved Roads on the site ahead of this paving, which open first. |
| 529 | `public int month` | The month it was ordered. |
| 544 | `public boolean prioritySet` |  |
| 545 | `public java.util.List<String> order` |  |
| 546 | `public java.util.List<Rush> rushes` |  |
| 547 | `public java.util.List<String> cancelling` |  |
| 548 | `public java.util.List<Shell> shells` |  |
| 549 | `public java.util.List<Demolition> demolitions` |  |
| 550 | `public int nextDemolition` |  |
| 551 | `public java.util.List<Expropriation> expropriations` |  |
| 552 | `public java.util.List<Run> runs` |  |
| 554 | `public java.util.List<Paving> paving` | F. |
| 580 | `private State state` |  |
| 909 | `private final Demolition site` |  |
| 910 | `private double unitsSold, paid` |  |
| 925 | `public final java.util.List<Overtime> overtime` |  |
| 926 | `public final java.util.List<Refund> refunds` |  |
| 927 | `public final java.util.List<Completed> completed` |  |
| 928 | `public final java.util.List<Paved> paved` |  |
| 929 | `public double overtimePoints` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 893 | **type** `public final class ConstructionControl` | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it has stopped and the shells they left, the buildings it is pulling dow... |

### A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS (lines 42-71)

### B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES (lines 72-163)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 12 | `public static double overtimeProductivity(int month)` | The report's productivity factor for the n-th consecutive month on overtime: its table averaged over that month's weeks, each week weighted by the part of it the month covers. |
| 155 | 3 | `public static double overtimeOutput(int month)` | A rushed site's month of work over a normal month's, in its n-th consecutive month on overtime: (50 / 40) x the month's factor. |
| 160 | 3 | `public static double premiumShare()` | What the city pays on a rushed site's crews, over their normal wage bill: 0.375. |

### C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT (lines 164-195)

### D. DEMOLISH: THE CITY'S OWN BUILDINGS (lines 196-261)

### E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY (lines 262-300)

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70) (lines 301-368)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 3 | `public static boolean paves(BuildingsTemplate t)` | Whether a building is one the city can pave: a Gravel Road. |

### THE STATE, as the save carries it (lines 369-609)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 18 | **type** `public static final class Rush implements Cloneable` | A site put on overtime, and how many months in a row it has been worked on it. |
| 380 | 1 | `public Rush()` _(in ConstructionControl.Rush)_ |  |
| 381 | 1 | `Rush(String key)` _(in ConstructionControl.Rush)_ |  |
| 384 | 7 | `Rush copy()` _(in ConstructionControl.Rush)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 394 | 34 | **type** `public static final class Shell implements Cloneable` | A stopped site: what an order cancelled left standing. |
| 412 | 1 | `public Shell()` _(in ConstructionControl.Shell)_ |  |
| 415 | 12 | `Shell copy()` _(in ConstructionControl.Shell)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 430 | 39 | **type** `public static final class Demolition implements Cloneable` | A demolition on site: the city's own building, a shell, or a building it bought. |
| 454 | 1 | `public Demolition()` _(in ConstructionControl.Demolition)_ |  |
| 457 | 7 | `Demolition copy()` _(in ConstructionControl.Demolition)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 465 | 1 | `public String key()` _(in ConstructionControl.Demolition)_ |  |
| 467 | 1 | `public double owed()` _(in ConstructionControl.Demolition)_ | Points still to do. |
| 476 | 24 | **type** `public static final class Expropriation implements Cloneable` | A compulsory purchase, as the city made it, kept and saved as its record. |
| 487 | 1 | `public Expropriation()` _(in ConstructionControl.Expropriation)_ |  |
| 490 | 7 | `Expropriation copy()` _(in ConstructionControl.Expropriation)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 498 | 1 | `public double total()` _(in ConstructionControl.Expropriation)_ |  |
| 502 | 15 | **type** `public static final class Run implements Cloneable` | One stack's run since it last stood empty: how many of it have opened, and what its orders cost. |
| 506 | 1 | `public Run()` _(in ConstructionControl.Run)_ |  |
| 509 | 7 | `Run copy()` _(in ConstructionControl.Run)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 524 | 17 | **type** `public static final class Paving implements Cloneable` | F. |
| 530 | 1 | `public Paving()` _(in ConstructionControl.Paving)_ |  |
| 533 | 7 | `Paving copy()` _(in ConstructionControl.Paving)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 543 | 36 | **type** `public static final class State implements Cloneable` | Everything above, under one key in the save. |
| 562 | 16 | `State copy()` _(in ConstructionControl.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() hands out the live one, which the month and the player go on changing while the autosave is written. |
| 583 | 1 | `public State toState()` | The state as the save writes it. |
| 586 | 12 | `public void restore(State saved)` | ...and back, from a save; null - a format-27 save, or none - is a city nobody has touched. |
| 605 | 4 | `public boolean engaged()` | Whether the month's advance has anything of this class to apply: an order set, a rush, a cancel waiting for the month's end, or a demolition on site. |

### the key of a site (lines 610-620)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 1 | `public static String keyOf(BuildingsTemplate t)` | A stack's key: "B" and its template's id. |
| 616 | 4 | `public static int templateIdOf(String key)` | The template id a stack's key names, or -1 for a key that is not a stack's. |

### A. the order (lines 621-652)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 1 | `public boolean isPrioritySet()` |  |
| 626 | 1 | `public java.util.List<String> savedOrder()` | The order as the player left it, keys of sites that may since have gone included. |
| 633 | 7 | `public java.util.List<String> effectiveOrder(java.util.List<String> present)` | The order the city's sites are served in: the player's, with any of them it does not name - ordered since - after it in the order given, and none that has gone. |
| 642 | 4 | `public void setOrder(java.util.List<String> keys)` | Sets the order: the player's hand, from the panel's arrows. |
| 648 | 4 | `public void clearOrder()` | Back to the crews' rule, with no order of the city's own. |

### B. the rushes (lines 653-712)

| line | len | member | says |
|---:|---:|---|---|
| 655 | 4 | `public Rush rushOf(String key)` |  |
| 661 | 4 | `public boolean isRushed(String key)` | Whether the player has this site on overtime. |
| 667 | 4 | `public int monthsOnOvertime(String key)` | Months in a row this site has been worked on overtime so far. |
| 673 | 9 | `public void setRush(String key, boolean on)` | On or off, by the player's hand. |
| 684 | 1 | `public java.util.List<Rush> rushes()` | Every rush on record, on or waiting out its month off. |
| 692 | 20 | `void afterMonth(java.util.Set<String> workedOnOvertime, java.util.Set<String> present, java.util.List<String> cityPresent)` | The month's rush bookkeeping, after the advance: a site worked on overtime counts a month more; one that was not - stopped, finished, or given no crews - starts again from nothing, and a stopped one is forgotten. |

### C. the cancels and the shells (lines 713-761)

| line | len | member | says |
|---:|---:|---|---|
| 715 | 1 | `public boolean isCancelling(String key)` |  |
| 717 | 4 | `public void setCancelling(String key, boolean cancel)` |  |
| 722 | 1 | `java.util.List<String> cancelling()` |  |
| 725 | 1 | `void clearCancelling()` | Every cancel settled at the month's end, stopped or moot. |
| 727 | 1 | `public java.util.List<Shell> shells()` |  |
| 729 | 4 | `public Shell shellOf(int templateId)` |  |
| 735 | 3 | `Shell addShell(BuildingsTemplate t, long buildings, double progress, double materialsOwed, int month)` | A stopped order's buildings, onto the shell of that building - a second stop joins the first. |
| 740 | 19 | `Shell addShell(BuildingsTemplate t, long buildings, double progress, double materialsOwed, int month, java.util.List<BuildingsS...` | ...with the equipment their orders froze (R1e, BuildingsStacks.getLots()), joining the shell's; none for null or empty. |
| 760 | 1 | `void removeShell(Shell s)` |  |

### D. the demolitions (lines 762-798)

| line | len | member | says |
|---:|---:|---|---|
| 764 | 1 | `public java.util.List<Demolition> demolitions()` |  |
| 766 | 4 | `public Demolition demolitionOf(String key)` |  |
| 771 | 18 | `Demolition addDemolition(BuildingsTemplate t, long buildings, double points, double salvageUnits, double landSqFt, double paid,...` |  |
| 791 | 5 | `public long closingOf(int templateId)` | Buildings of this kind ordered demolished that still stand until the month starts. |
| 797 | 1 | `void removeDemolition(Demolition d)` |  |

### E. the buy-outs (lines 799-804)

| line | len | member | says |
|---:|---:|---|---|
| 801 | 1 | `public java.util.List<Expropriation> expropriations()` |  |
| 803 | 1 | `void recordExpropriation(Expropriation e)` |  |

### F. the pavings (lines 805-845)

| line | len | member | says |
|---:|---:|---|---|
| 807 | 1 | `public java.util.List<Paving> pavings()` |  |
| 810 | 5 | `public long paving()` | Gravel roads being paved: on the Paved Road site, standing until their paving opens. |
| 817 | 8 | `Paving addPaving(long roads, long ahead, int month)` | A paving of n roads, behind the `ahead` Paved Roads already on the site. |
| 831 | 14 | `long pavedOf(long finished)` | Of `finished` Paved Roads opening this month off the front of the site, the ones that are pavings', each paving moved up the site by them: the gravel roads to retire. |

### each stack's run (lines 846-867)

| line | len | member | says |
|---:|---:|---|---|
| 849 | 4 | `public Run runOf(int templateId)` | A stack's run, null if none is on record. |
| 854 | 9 | `Run runFor(int templateId)` |  |
| 864 | 3 | `void endRun(int templateId)` |  |

### a reform (lines 868-879)

| line | len | member | says |
|---:|---:|---|---|
| 871 | 8 | `public void redenominate(double scale)` | A currency reform: the money is money; the points, the units and the ground are not. |

### THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. (lines 880-932)

| line | len | member | says |
|---:|---:|---|---|
| 890 | 2 | **type** `public record Overtime(String key, String building, int month, double share, double work, double perPoint, ...` | A rushed site worked on overtime this month: its share, its work, the builders' wage bill a point it was struck at, the premium on its crews, before the tax, and the part of its share the city's own crews worked (FC1b... |
| 899 | 2 | **type** `public record Refund(int templateId, String building, long buildings, double value, double allowance, doubl...` | An order stopped at the month's end: what comes back to the city, the material allowance in it, the points of work it was for, the shell it left, and whether it was a depot of the builders' served first (BuildingManag... |
| 908 | 11 | **type** `public static final class Completed` | A demolition finished this month - and, once Game has sold it, its own sale: the units the builders bought and what they paid, which the inbox reads off the site rather than by its name (after the docs pass: two of on... |
| 911 | 1 | `public Completed(Demolition site)` _(in ConstructionControl.Completed)_ |  |
| 912 | 1 | `public Demolition site()` _(in ConstructionControl.Completed)_ |  |
| 914 | 1 | `public double unitsSold()` _(in ConstructionControl.Completed)_ | Units of its material the builders bought. |
| 916 | 1 | `public double paid()` _(in ConstructionControl.Completed)_ | ...and what they paid for them. |
| 917 | 1 | `void sold(double units, double money)` _(in ConstructionControl.Completed)_ |  |
| 921 | 1 | **type** `public record Paved(long roads, double landFreedSqFt)` | F. |
| 924 | 8 | **type** `public static final class Events` | The month's events, as the advance left them. |
| 930 | 1 | `public boolean isEmpty()` _(in ConstructionControl.Events)_ |  |

