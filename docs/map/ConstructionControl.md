# ConstructionControl.java - 901 lines · 66 methods · 11 constants · model

`ham/citybuildersim/ConstructionControl.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [SaveCopy](SaveCopy.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (4)

**Used by (23):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankCheck](BankCheck.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [Game](Game.md), [Inbox](Inbox.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [RoadCheck](RoadCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [UserInterface](UserInterface.md)

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
| 599 | · the key of a site |
| 610 | · A. the order |
| 642 | · B. the rushes |
| 702 | · C. the cancels and the shells |
| 741 | · D. the demolitions |
| 778 | · E. the buy-outs |
| 784 | · F. the pavings |
| 825 | · each stack's run |
| 847 | · a reform |
| 859 | · THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. |

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
| 397 | `public int buildings` |  |
| 399 | `public double progress` | The points of work it holds. |
| 401 | `public double materialsOwed` | Units of material it has still to draw - what a restart quotes for. |
| 403 | `public double refunded` | What came back to the city when it stopped. |
| 405 | `public int month` | The month it stopped. |
| 420 | `public int id` |  |
| 421 | `public int templateId` |  |
| 422 | `public String building` |  |
| 423 | `public int buildings` |  |
| 425 | `public double points` | The points of work it is, all told: DEMOLITION_SHARE of the work it holds. |
| 426 | `public double progress` |  |
| 428 | `public double salvageUnits` | Units of construction material salvaged when it completes, as quoted: SALVAGE_SHARE of what the buildings hold since 0.7.106 (all of it until then). |
| 430 | `public double landSqFt` | The ground it holds until it completes, in square feet. |
| 432 | `public double paid` | What the city paid the builders for it. |
| 434 | `public double value` | ...and what of that they have still to earn. |
| 436 | `public String from` | "City" for the city's own, a sector's key for a building it bought, "Shell" for a stopped site. |
| 438 | `public int month` | The month it was ordered. |
| 440 | `public boolean closing` | True from the order until the month starts and the buildings close (Game.closeDemolished()): they still stand, and hold their own ground. |
| 442 | `public double groundPaid` | What the city paid a bought building's owner for its ground, for the demolition log; nothing for the city's own. |
| 466 | `public int month` |  |
| 467 | `public int templateId` |  |
| 468 | `public String building` |  |
| 469 | `public int buildings` |  |
| 470 | `public String sector` |  |
| 471 | `public double buildingValue` |  |
| 472 | `public double ground` |  |
| 473 | `public double businessLoss` |  |
| 475 | `public double months` | The months a replacement would have taken, which the business loss was paid for. |
| 492 | `public int templateId` |  |
| 493 | `public int built` |  |
| 494 | `public double billed` |  |
| 514 | `public int roads` |  |
| 516 | `public int ahead` | Paved Roads on the site ahead of this paving, which open first. |
| 518 | `public int month` | The month it was ordered. |
| 533 | `public boolean prioritySet` |  |
| 534 | `public java.util.List<String> order` |  |
| 535 | `public java.util.List<Rush> rushes` |  |
| 536 | `public java.util.List<String> cancelling` |  |
| 537 | `public java.util.List<Shell> shells` |  |
| 538 | `public java.util.List<Demolition> demolitions` |  |
| 539 | `public int nextDemolition` |  |
| 540 | `public java.util.List<Expropriation> expropriations` |  |
| 541 | `public java.util.List<Run> runs` |  |
| 543 | `public java.util.List<Paving> paving` | F. |
| 569 | `private State state` |  |
| 878 | `private final Demolition site` |  |
| 879 | `private double unitsSold, paid` |  |
| 894 | `public final java.util.List<Overtime> overtime` |  |
| 895 | `public final java.util.List<Refund> refunds` |  |
| 896 | `public final java.util.List<Completed> completed` |  |
| 897 | `public final java.util.List<Paved> paved` |  |
| 898 | `public double overtimePoints` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 862 | **type** `public final class ConstructionControl` | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it has stopped and the shells they left, the buildings it is pulling dow... |

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

### THE STATE, as the save carries it (lines 369-598)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 18 | **type** `public static final class Rush implements Cloneable` | A site put on overtime, and how many months in a row it has been worked on it. |
| 380 | 1 | `public Rush()` _(in ConstructionControl.Rush)_ |  |
| 381 | 1 | `Rush(String key)` _(in ConstructionControl.Rush)_ |  |
| 384 | 7 | `Rush copy()` _(in ConstructionControl.Rush)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 394 | 23 | **type** `public static final class Shell implements Cloneable` | A stopped site: what an order cancelled left standing. |
| 406 | 1 | `public Shell()` _(in ConstructionControl.Shell)_ |  |
| 409 | 7 | `Shell copy()` _(in ConstructionControl.Shell)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 419 | 39 | **type** `public static final class Demolition implements Cloneable` | A demolition on site: the city's own building, a shell, or a building it bought. |
| 443 | 1 | `public Demolition()` _(in ConstructionControl.Demolition)_ |  |
| 446 | 7 | `Demolition copy()` _(in ConstructionControl.Demolition)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 454 | 1 | `public String key()` _(in ConstructionControl.Demolition)_ |  |
| 456 | 1 | `public double owed()` _(in ConstructionControl.Demolition)_ | Points still to do. |
| 465 | 24 | **type** `public static final class Expropriation implements Cloneable` | A compulsory purchase, as the city made it, kept and saved as its record. |
| 476 | 1 | `public Expropriation()` _(in ConstructionControl.Expropriation)_ |  |
| 479 | 7 | `Expropriation copy()` _(in ConstructionControl.Expropriation)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 487 | 1 | `public double total()` _(in ConstructionControl.Expropriation)_ |  |
| 491 | 15 | **type** `public static final class Run implements Cloneable` | One stack's run since it last stood empty: how many of it have opened, and what its orders cost. |
| 495 | 1 | `public Run()` _(in ConstructionControl.Run)_ |  |
| 498 | 7 | `Run copy()` _(in ConstructionControl.Run)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 513 | 17 | **type** `public static final class Paving implements Cloneable` | F. |
| 519 | 1 | `public Paving()` _(in ConstructionControl.Paving)_ |  |
| 522 | 7 | `Paving copy()` _(in ConstructionControl.Paving)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 532 | 36 | **type** `public static final class State implements Cloneable` | Everything above, under one key in the save. |
| 551 | 16 | `State copy()` _(in ConstructionControl.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() hands out the live one, which the month and the player go on changing while the autosave is written. |
| 572 | 1 | `public State toState()` | The state as the save writes it. |
| 575 | 12 | `public void restore(State saved)` | ...and back, from a save; null - a format-27 save, or none - is a city nobody has touched. |
| 594 | 4 | `public boolean engaged()` | Whether the month's advance has anything of this class to apply: an order set, a rush, a cancel waiting for the month's end, or a demolition on site. |

### the key of a site (lines 599-609)

| line | len | member | says |
|---:|---:|---|---|
| 602 | 1 | `public static String keyOf(BuildingsTemplate t)` | A stack's key: "B" and its template's id. |
| 605 | 4 | `public static int templateIdOf(String key)` | The template id a stack's key names, or -1 for a key that is not a stack's. |

### A. the order (lines 610-641)

| line | len | member | says |
|---:|---:|---|---|
| 612 | 1 | `public boolean isPrioritySet()` |  |
| 615 | 1 | `public java.util.List<String> savedOrder()` | The order as the player left it, keys of sites that may since have gone included. |
| 622 | 7 | `public java.util.List<String> effectiveOrder(java.util.List<String> present)` | The order the city's sites are served in: the player's, with any of them it does not name - ordered since - after it in the order given, and none that has gone. |
| 631 | 4 | `public void setOrder(java.util.List<String> keys)` | Sets the order: the player's hand, from the panel's arrows. |
| 637 | 4 | `public void clearOrder()` | Back to the crews' rule, with no order of the city's own. |

### B. the rushes (lines 642-701)

| line | len | member | says |
|---:|---:|---|---|
| 644 | 4 | `public Rush rushOf(String key)` |  |
| 650 | 4 | `public boolean isRushed(String key)` | Whether the player has this site on overtime. |
| 656 | 4 | `public int monthsOnOvertime(String key)` | Months in a row this site has been worked on overtime so far. |
| 662 | 9 | `public void setRush(String key, boolean on)` | On or off, by the player's hand. |
| 673 | 1 | `public java.util.List<Rush> rushes()` | Every rush on record, on or waiting out its month off. |
| 681 | 20 | `void afterMonth(java.util.Set<String> workedOnOvertime, java.util.Set<String> present, java.util.List<String> cityPresent)` | The month's rush bookkeeping, after the advance: a site worked on overtime counts a month more; one that was not - stopped, finished, or given no crews - starts again from nothing, and a stopped one is forgotten. |

### C. the cancels and the shells (lines 702-740)

| line | len | member | says |
|---:|---:|---|---|
| 704 | 1 | `public boolean isCancelling(String key)` |  |
| 706 | 4 | `public void setCancelling(String key, boolean cancel)` |  |
| 711 | 1 | `java.util.List<String> cancelling()` |  |
| 714 | 1 | `void clearCancelling()` | Every cancel settled at the month's end, stopped or moot. |
| 716 | 1 | `public java.util.List<Shell> shells()` |  |
| 718 | 4 | `public Shell shellOf(int templateId)` |  |
| 724 | 14 | `Shell addShell(BuildingsTemplate t, int buildings, double progress, double materialsOwed, int month)` | A stopped order's buildings, onto the shell of that building - a second stop joins the first. |
| 739 | 1 | `void removeShell(Shell s)` |  |

### D. the demolitions (lines 741-777)

| line | len | member | says |
|---:|---:|---|---|
| 743 | 1 | `public java.util.List<Demolition> demolitions()` |  |
| 745 | 4 | `public Demolition demolitionOf(String key)` |  |
| 750 | 18 | `Demolition addDemolition(BuildingsTemplate t, int buildings, double points, double salvageUnits, double landSqFt, double paid, ...` |  |
| 770 | 5 | `public int closingOf(int templateId)` | Buildings of this kind ordered demolished that still stand until the month starts. |
| 776 | 1 | `void removeDemolition(Demolition d)` |  |

### E. the buy-outs (lines 778-783)

| line | len | member | says |
|---:|---:|---|---|
| 780 | 1 | `public java.util.List<Expropriation> expropriations()` |  |
| 782 | 1 | `void recordExpropriation(Expropriation e)` |  |

### F. the pavings (lines 784-824)

| line | len | member | says |
|---:|---:|---|---|
| 786 | 1 | `public java.util.List<Paving> pavings()` |  |
| 789 | 5 | `public int paving()` | Gravel roads being paved: on the Paved Road site, standing until their paving opens. |
| 796 | 8 | `Paving addPaving(int roads, int ahead, int month)` | A paving of n roads, behind the `ahead` Paved Roads already on the site. |
| 810 | 14 | `int pavedOf(int finished)` | Of `finished` Paved Roads opening this month off the front of the site, the ones that are pavings', each paving moved up the site by them: the gravel roads to retire. |

### each stack's run (lines 825-846)

| line | len | member | says |
|---:|---:|---|---|
| 828 | 4 | `public Run runOf(int templateId)` | A stack's run, null if none is on record. |
| 833 | 9 | `Run runFor(int templateId)` |  |
| 843 | 3 | `void endRun(int templateId)` |  |

### a reform (lines 847-858)

| line | len | member | says |
|---:|---:|---|---|
| 850 | 8 | `public void redenominate(double scale)` | A currency reform: the money is money; the points, the units and the ground are not. |

### THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. (lines 859-901)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 2 | **type** `public record Overtime(String key, String building, int month, double share, double work, double perPoint, ...` | A rushed site worked on overtime this month: its share, its work, the builders' wage bill a point it was struck at, and the premium on its crews, before the tax. |
| 868 | 2 | **type** `public record Refund(int templateId, String building, int buildings, double value, double allowance, double...` | An order stopped at the month's end: what comes back to the city, the material allowance in it, the points of work it was for, and the shell it left. |
| 877 | 11 | **type** `public static final class Completed` | A demolition finished this month - and, once Game has sold it, its own sale: the units the builders bought and what they paid, which the inbox reads off the site rather than by its name (after the docs pass: two of on... |
| 880 | 1 | `public Completed(Demolition site)` _(in ConstructionControl.Completed)_ |  |
| 881 | 1 | `public Demolition site()` _(in ConstructionControl.Completed)_ |  |
| 883 | 1 | `public double unitsSold()` _(in ConstructionControl.Completed)_ | Units of its material the builders bought. |
| 885 | 1 | `public double paid()` _(in ConstructionControl.Completed)_ | ...and what they paid for them. |
| 886 | 1 | `void sold(double units, double money)` _(in ConstructionControl.Completed)_ |  |
| 890 | 1 | **type** `public record Paved(int roads, double landFreedSqFt)` | F. |
| 893 | 8 | **type** `public static final class Events` | The month's events, as the advance left them. |
| 899 | 1 | `public boolean isEmpty()` _(in ConstructionControl.Events)_ |  |

