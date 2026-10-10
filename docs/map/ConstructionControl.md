# ConstructionControl.java - 820 lines · 59 methods · 11 constants · model

`ham/citybuildersim/ConstructionControl.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (4)

**Used by (17):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankCheck](BankCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [DataSave](DataSave.md), [Game](Game.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [RoadCheck](RoadCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 42 | A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS |
| 69 | B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES |
| 161 | C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT |
| 193 | D. DEMOLISH: THE CITY'S OWN BUILDINGS |
| 259 | E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY |
| 298 | F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70) |
| 366 | · THE STATE, as the save carries it |
| 518 | · the key of a site |
| 529 | · A. the order |
| 561 | · B. the rushes |
| 621 | · C. the cancels and the shells |
| 660 | · D. the demolitions |
| 697 | · E. the buy-outs |
| 703 | · F. the pavings |
| 744 | · each stack's run |
| 766 | · a reform |
| 778 | · THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `ConstructionControl.STANDARD_HOURS` | `40` | The normal working week the report measures against, in hours. |
| 115 | `ConstructionControl.OVERTIME_HOURS` | `50` | The week on overtime: the Business Roundtable's five tens (Report C-2, Figure 4). |
| 118 | `ConstructionControl.OVERTIME_RATE` | `1.5` | What an hour over the standard week is paid at: time and a half (Canada Labour Code, section 174). |
| 121 | `ConstructionControl.WEEKS_A_MONTH` | `52.0 / 12.0` | Weeks in a month for averaging the report's table: 52 / 12, the 4.33 the brief reads it at. |
| 124 | `ConstructionControl.OVERTIME_WEEKS_ENDING` | `{ 2, 4, 6, 8, 10 }` | Where each step of the report's 50-hour curve ends, in weeks on the schedule (Report C-2, Figure 4); the last step runs on. |
| 127 | `ConstructionControl.OVERTIME_PRODUCTIVITY` | `{ 0.926, 0.90, 0.87, 0.80, 0.752, 0.750 }` | Productivity on a 50-hour week against a 40-hour one, for each step above and beyond the last (Report C-2, Figure 4). |
| 130 | `ConstructionControl.OVERTIME_WAGE_BILL` | `(STANDARD_HOURS +(OVERTIME_HOURS - STANDARD_HOURS) * OVERTIME_RATE) / STANDAR...` | The crews' wage bill on overtime over their normal bill: (40 + 10 x 1.5) / 40 = 1.375. |
| 246 | `ConstructionControl.DEMOLITION_SHARE` | `0.05` | The share of a building's construction points its demolition is: Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April 2017), over the average new single-family home of 2015, $289,415 (NAHB) - 5.1%, ta... |
| 257 | `ConstructionControl.SALVAGE_SHARE` | `0.25` | The share of a building's construction material its demolition recovers as material: 20 to 30 percent of building-related construction and demolition debris was recovered for processing and recycling in the United Sta... |
| 356 | `ConstructionControl.PAVE_FROM` | `"Gravel Road"` | The road that can be paved (0.7.70): a Gravel Road, and nothing else. |
| 359 | `ConstructionControl.PAVE_TO` | `"Paved Road"` | ...and what it is paved to: a Paved Road. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 372 | `public String key` |  |
| 374 | `public boolean on` | Whether the player has it on; off until the next month passes, so a stop and a restart between two presses keep the count. |
| 376 | `public int months` | Consecutive months worked on overtime so far. |
| 383 | `public int templateId` |  |
| 384 | `public String building` |  |
| 385 | `public int buildings` |  |
| 387 | `public double progress` | The points of work it holds. |
| 389 | `public double materialsOwed` | Units of material it has still to draw - what a restart quotes for. |
| 391 | `public double refunded` | What came back to the city when it stopped. |
| 393 | `public int month` | The month it stopped. |
| 399 | `public int id` |  |
| 400 | `public int templateId` |  |
| 401 | `public String building` |  |
| 402 | `public int buildings` |  |
| 404 | `public double points` | The points of work it is, all told: DEMOLITION_SHARE of the work it holds. |
| 405 | `public double progress` |  |
| 407 | `public double salvageUnits` | Units of construction material salvaged when it completes, as quoted: SALVAGE_SHARE of what the buildings hold since 0.7.106 (all of it until then). |
| 409 | `public double landSqFt` | The ground it holds until it completes, in square feet. |
| 411 | `public double paid` | What the city paid the builders for it. |
| 413 | `public double value` | ...and what of that they have still to earn. |
| 415 | `public String from` | "City" for the city's own, a sector's key for a building it bought, "Shell" for a stopped site. |
| 417 | `public int month` | The month it was ordered. |
| 419 | `public boolean closing` | True from the order until the month starts and the buildings close (Game.closeDemolished()): they still stand, and hold their own ground. |
| 421 | `public double groundPaid` | What the city paid a bought building's owner for its ground, for the demolition log; nothing for the city's own. |
| 436 | `public int month` |  |
| 437 | `public int templateId` |  |
| 438 | `public String building` |  |
| 439 | `public int buildings` |  |
| 440 | `public String sector` |  |
| 441 | `public double buildingValue` |  |
| 442 | `public double ground` |  |
| 443 | `public double businessLoss` |  |
| 445 | `public double months` | The months a replacement would have taken, which the business loss was paid for. |
| 452 | `public int templateId` |  |
| 453 | `public int built` |  |
| 454 | `public double billed` |  |
| 465 | `public int roads` |  |
| 467 | `public int ahead` | Paved Roads on the site ahead of this paving, which open first. |
| 469 | `public int month` | The month it was ordered. |
| 475 | `public boolean prioritySet` |  |
| 476 | `public java.util.List<String> order` |  |
| 477 | `public java.util.List<Rush> rushes` |  |
| 478 | `public java.util.List<String> cancelling` |  |
| 479 | `public java.util.List<Shell> shells` |  |
| 480 | `public java.util.List<Demolition> demolitions` |  |
| 481 | `public int nextDemolition` |  |
| 482 | `public java.util.List<Expropriation> expropriations` |  |
| 483 | `public java.util.List<Run> runs` |  |
| 485 | `public java.util.List<Paving> paving` | F. |
| 488 | `private State state` |  |
| 797 | `private final Demolition site` |  |
| 798 | `private double unitsSold, paid` |  |
| 813 | `public final java.util.List<Overtime> overtime` |  |
| 814 | `public final java.util.List<Refund> refunds` |  |
| 815 | `public final java.util.List<Completed> completed` |  |
| 816 | `public final java.util.List<Paved> paved` |  |
| 817 | `public double overtimePoints` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 781 | **type** `public final class ConstructionControl` | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it has stopped and the shells they left, the buildings it is pulling dow... |

### A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS (lines 42-68)

### B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES (lines 69-160)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 12 | `public static double overtimeProductivity(int month)` | The report's productivity factor for the n-th consecutive month on overtime: its table averaged over that month's weeks, each week weighted by the part of it the month covers. |
| 152 | 3 | `public static double overtimeOutput(int month)` | A rushed site's month of work over a normal month's, in its n-th consecutive month on overtime: (50 / 40) x the month's factor. |
| 157 | 3 | `public static double premiumShare()` | What the city pays on a rushed site's crews, over their normal wage bill: 0.375. |

### C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT (lines 161-192)

### D. DEMOLISH: THE CITY'S OWN BUILDINGS (lines 193-258)

### E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY (lines 259-297)

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70) (lines 298-365)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 3 | `public static boolean paves(BuildingsTemplate t)` | Whether a building is one the city can pave: a Gravel Road. |

### THE STATE, as the save carries it (lines 366-517)

| line | len | member | says |
|---:|---:|---|---|
| 371 | 9 | **type** `public static final class Rush` | A site put on overtime, and how many months in a row it has been worked on it. |
| 377 | 1 | `public Rush()` _(in ConstructionControl.Rush)_ |  |
| 378 | 1 | `Rush(String key)` _(in ConstructionControl.Rush)_ |  |
| 382 | 14 | **type** `public static final class Shell` | A stopped site: what an order cancelled left standing. |
| 394 | 1 | `public Shell()` _(in ConstructionControl.Shell)_ |  |
| 398 | 30 | **type** `public static final class Demolition` | A demolition on site: the city's own building, a shell, or a building it bought. |
| 422 | 1 | `public Demolition()` _(in ConstructionControl.Demolition)_ |  |
| 424 | 1 | `public String key()` _(in ConstructionControl.Demolition)_ |  |
| 426 | 1 | `public double owed()` _(in ConstructionControl.Demolition)_ | Points still to do. |
| 435 | 14 | **type** `public static final class Expropriation` | A compulsory purchase, as the city made it, kept and saved as its record. |
| 446 | 1 | `public Expropriation()` _(in ConstructionControl.Expropriation)_ |  |
| 447 | 1 | `public double total()` _(in ConstructionControl.Expropriation)_ |  |
| 451 | 6 | **type** `public static final class Run` | One stack's run since it last stood empty: how many of it have opened, and what its orders cost. |
| 455 | 1 | `public Run()` _(in ConstructionControl.Run)_ |  |
| 464 | 8 | **type** `public static final class Paving` | F. |
| 470 | 1 | `public Paving()` _(in ConstructionControl.Paving)_ |  |
| 474 | 13 | **type** `public static final class State` | Everything above, under one key in the save. |
| 491 | 1 | `public State toState()` | The state as the save writes it. |
| 494 | 12 | `public void restore(State saved)` | ...and back, from a save; null - a format-27 save, or none - is a city nobody has touched. |
| 513 | 4 | `public boolean engaged()` | Whether the month's advance has anything of this class to apply: an order set, a rush, a cancel waiting for the month's end, or a demolition on site. |

### the key of a site (lines 518-528)

| line | len | member | says |
|---:|---:|---|---|
| 521 | 1 | `public static String keyOf(BuildingsTemplate t)` | A stack's key: "B" and its template's id. |
| 524 | 4 | `public static int templateIdOf(String key)` | The template id a stack's key names, or -1 for a key that is not a stack's. |

### A. the order (lines 529-560)

| line | len | member | says |
|---:|---:|---|---|
| 531 | 1 | `public boolean isPrioritySet()` |  |
| 534 | 1 | `public java.util.List<String> savedOrder()` | The order as the player left it, keys of sites that may since have gone included. |
| 541 | 7 | `public java.util.List<String> effectiveOrder(java.util.List<String> present)` | The order the city's sites are served in: the player's, with any of them it does not name - ordered since - after it in the order given, and none that has gone. |
| 550 | 4 | `public void setOrder(java.util.List<String> keys)` | Sets the order: the player's hand, from the panel's arrows. |
| 556 | 4 | `public void clearOrder()` | Back to the crews' rule, with no order of the city's own. |

### B. the rushes (lines 561-620)

| line | len | member | says |
|---:|---:|---|---|
| 563 | 4 | `public Rush rushOf(String key)` |  |
| 569 | 4 | `public boolean isRushed(String key)` | Whether the player has this site on overtime. |
| 575 | 4 | `public int monthsOnOvertime(String key)` | Months in a row this site has been worked on overtime so far. |
| 581 | 9 | `public void setRush(String key, boolean on)` | On or off, by the player's hand. |
| 592 | 1 | `public java.util.List<Rush> rushes()` | Every rush on record, on or waiting out its month off. |
| 600 | 20 | `void afterMonth(java.util.Set<String> workedOnOvertime, java.util.Set<String> present, java.util.List<String> cityPresent)` | The month's rush bookkeeping, after the advance: a site worked on overtime counts a month more; one that was not - stopped, finished, or given no crews - starts again from nothing, and a stopped one is forgotten. |

### C. the cancels and the shells (lines 621-659)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 1 | `public boolean isCancelling(String key)` |  |
| 625 | 4 | `public void setCancelling(String key, boolean cancel)` |  |
| 630 | 1 | `java.util.List<String> cancelling()` |  |
| 633 | 1 | `void clearCancelling()` | Every cancel settled at the month's end, stopped or moot. |
| 635 | 1 | `public java.util.List<Shell> shells()` |  |
| 637 | 4 | `public Shell shellOf(int templateId)` |  |
| 643 | 14 | `Shell addShell(BuildingsTemplate t, int buildings, double progress, double materialsOwed, int month)` | A stopped order's buildings, onto the shell of that building - a second stop joins the first. |
| 658 | 1 | `void removeShell(Shell s)` |  |

### D. the demolitions (lines 660-696)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 1 | `public java.util.List<Demolition> demolitions()` |  |
| 664 | 4 | `public Demolition demolitionOf(String key)` |  |
| 669 | 18 | `Demolition addDemolition(BuildingsTemplate t, int buildings, double points, double salvageUnits, double landSqFt, double paid, ...` |  |
| 689 | 5 | `public int closingOf(int templateId)` | Buildings of this kind ordered demolished that still stand until the month starts. |
| 695 | 1 | `void removeDemolition(Demolition d)` |  |

### E. the buy-outs (lines 697-702)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 1 | `public java.util.List<Expropriation> expropriations()` |  |
| 701 | 1 | `void recordExpropriation(Expropriation e)` |  |

### F. the pavings (lines 703-743)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 1 | `public java.util.List<Paving> pavings()` |  |
| 708 | 5 | `public int paving()` | Gravel roads being paved: on the Paved Road site, standing until their paving opens. |
| 715 | 8 | `Paving addPaving(int roads, int ahead, int month)` | A paving of n roads, behind the `ahead` Paved Roads already on the site. |
| 729 | 14 | `int pavedOf(int finished)` | Of `finished` Paved Roads opening this month off the front of the site, the ones that are pavings', each paving moved up the site by them: the gravel roads to retire. |

### each stack's run (lines 744-765)

| line | len | member | says |
|---:|---:|---|---|
| 747 | 4 | `public Run runOf(int templateId)` | A stack's run, null if none is on record. |
| 752 | 9 | `Run runFor(int templateId)` |  |
| 762 | 3 | `void endRun(int templateId)` |  |

### a reform (lines 766-777)

| line | len | member | says |
|---:|---:|---|---|
| 769 | 8 | `public void redenominate(double scale)` | A currency reform: the money is money; the points, the units and the ground are not. |

### THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. (lines 778-820)

| line | len | member | says |
|---:|---:|---|---|
| 783 | 2 | **type** `public record Overtime(String key, String building, int month, double share, double work, double perPoint, ...` | A rushed site worked on overtime this month: its share, its work, the builders' wage bill a point it was struck at, and the premium on its crews, before the tax. |
| 787 | 2 | **type** `public record Refund(int templateId, String building, int buildings, double value, double allowance, double...` | An order stopped at the month's end: what comes back to the city, the material allowance in it, the points of work it was for, and the shell it left. |
| 796 | 11 | **type** `public static final class Completed` | A demolition finished this month - and, once Game has sold it, its own sale: the units the builders bought and what they paid, which the inbox reads off the site rather than by its name (after the docs pass: two of on... |
| 799 | 1 | `public Completed(Demolition site)` _(in ConstructionControl.Completed)_ |  |
| 800 | 1 | `public Demolition site()` _(in ConstructionControl.Completed)_ |  |
| 802 | 1 | `public double unitsSold()` _(in ConstructionControl.Completed)_ | Units of its material the builders bought. |
| 804 | 1 | `public double paid()` _(in ConstructionControl.Completed)_ | ...and what they paid for them. |
| 805 | 1 | `void sold(double units, double money)` _(in ConstructionControl.Completed)_ |  |
| 809 | 1 | **type** `public record Paved(int roads, double landFreedSqFt)` | F. |
| 812 | 8 | **type** `public static final class Events` | The month's events, as the advance left them. |
| 818 | 1 | `public boolean isEmpty()` _(in ConstructionControl.Events)_ |  |

