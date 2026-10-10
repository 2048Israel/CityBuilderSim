# ConstructionControl.java - 798 lines · 59 methods · 10 constants · model

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

**Used by (15):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [DataSave](DataSave.md), [Game](Game.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [RoadCheck](RoadCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 42 | A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS |
| 69 | B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES |
| 161 | C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT |
| 193 | D. DEMOLISH: THE CITY'S OWN BUILDINGS |
| 237 | E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY |
| 276 | F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70) |
| 344 | · THE STATE, as the save carries it |
| 496 | · the key of a site |
| 507 | · A. the order |
| 539 | · B. the rushes |
| 599 | · C. the cancels and the shells |
| 638 | · D. the demolitions |
| 675 | · E. the buy-outs |
| 681 | · F. the pavings |
| 722 | · each stack's run |
| 744 | · a reform |
| 756 | · THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. |

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
| 235 | `ConstructionControl.DEMOLITION_SHARE` | `0.05` | The share of a building's construction points its demolition is: Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April 2017), over the average new single-family home of 2015, $289,415 (NAHB) - 5.1%, ta... |
| 334 | `ConstructionControl.PAVE_FROM` | `"Gravel Road"` | The road that can be paved (0.7.70): a Gravel Road, and nothing else. |
| 337 | `ConstructionControl.PAVE_TO` | `"Paved Road"` | ...and what it is paved to: a Paved Road. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 350 | `public String key` |  |
| 352 | `public boolean on` | Whether the player has it on; off until the next month passes, so a stop and a restart between two presses keep the count. |
| 354 | `public int months` | Consecutive months worked on overtime so far. |
| 361 | `public int templateId` |  |
| 362 | `public String building` |  |
| 363 | `public int buildings` |  |
| 365 | `public double progress` | The points of work it holds. |
| 367 | `public double materialsOwed` | Units of material it has still to draw - what a restart quotes for. |
| 369 | `public double refunded` | What came back to the city when it stopped. |
| 371 | `public int month` | The month it stopped. |
| 377 | `public int id` |  |
| 378 | `public int templateId` |  |
| 379 | `public String building` |  |
| 380 | `public int buildings` |  |
| 382 | `public double points` | The points of work it is, all told: DEMOLITION_SHARE of the work it holds. |
| 383 | `public double progress` |  |
| 385 | `public double salvageUnits` | Units of construction material the buildings hold, salvaged when it completes. |
| 387 | `public double landSqFt` | The ground it holds until it completes, in square feet. |
| 389 | `public double paid` | What the city paid the builders for it. |
| 391 | `public double value` | ...and what of that they have still to earn. |
| 393 | `public String from` | "City" for the city's own, a sector's key for a building it bought, "Shell" for a stopped site. |
| 395 | `public int month` | The month it was ordered. |
| 397 | `public boolean closing` | True from the order until the month starts and the buildings close (Game.closeDemolished()): they still stand, and hold their own ground. |
| 399 | `public double groundPaid` | What the city paid a bought building's owner for its ground, for the demolition log; nothing for the city's own. |
| 414 | `public int month` |  |
| 415 | `public int templateId` |  |
| 416 | `public String building` |  |
| 417 | `public int buildings` |  |
| 418 | `public String sector` |  |
| 419 | `public double buildingValue` |  |
| 420 | `public double ground` |  |
| 421 | `public double businessLoss` |  |
| 423 | `public double months` | The months a replacement would have taken, which the business loss was paid for. |
| 430 | `public int templateId` |  |
| 431 | `public int built` |  |
| 432 | `public double billed` |  |
| 443 | `public int roads` |  |
| 445 | `public int ahead` | Paved Roads on the site ahead of this paving, which open first. |
| 447 | `public int month` | The month it was ordered. |
| 453 | `public boolean prioritySet` |  |
| 454 | `public java.util.List<String> order` |  |
| 455 | `public java.util.List<Rush> rushes` |  |
| 456 | `public java.util.List<String> cancelling` |  |
| 457 | `public java.util.List<Shell> shells` |  |
| 458 | `public java.util.List<Demolition> demolitions` |  |
| 459 | `public int nextDemolition` |  |
| 460 | `public java.util.List<Expropriation> expropriations` |  |
| 461 | `public java.util.List<Run> runs` |  |
| 463 | `public java.util.List<Paving> paving` | F. |
| 466 | `private State state` |  |
| 775 | `private final Demolition site` |  |
| 776 | `private double unitsSold, paid` |  |
| 791 | `public final java.util.List<Overtime> overtime` |  |
| 792 | `public final java.util.List<Refund> refunds` |  |
| 793 | `public final java.util.List<Completed> completed` |  |
| 794 | `public final java.util.List<Paved> paved` |  |
| 795 | `public double overtimePoints` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 759 | **type** `public final class ConstructionControl` | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it has stopped and the shells they left, the buildings it is pulling dow... |

### A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS (lines 42-68)

### B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES (lines 69-160)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 12 | `public static double overtimeProductivity(int month)` | The report's productivity factor for the n-th consecutive month on overtime: its table averaged over that month's weeks, each week weighted by the part of it the month covers. |
| 152 | 3 | `public static double overtimeOutput(int month)` | A rushed site's month of work over a normal month's, in its n-th consecutive month on overtime: (50 / 40) x the month's factor. |
| 157 | 3 | `public static double premiumShare()` | What the city pays on a rushed site's crews, over their normal wage bill: 0.375. |

### C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT (lines 161-192)

### D. DEMOLISH: THE CITY'S OWN BUILDINGS (lines 193-236)

### E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY (lines 237-275)

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70) (lines 276-343)

| line | len | member | says |
|---:|---:|---|---|
| 340 | 3 | `public static boolean paves(BuildingsTemplate t)` | Whether a building is one the city can pave: a Gravel Road. |

### THE STATE, as the save carries it (lines 344-495)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 9 | **type** `public static final class Rush` | A site put on overtime, and how many months in a row it has been worked on it. |
| 355 | 1 | `public Rush()` _(in ConstructionControl.Rush)_ |  |
| 356 | 1 | `Rush(String key)` _(in ConstructionControl.Rush)_ |  |
| 360 | 14 | **type** `public static final class Shell` | A stopped site: what an order cancelled left standing. |
| 372 | 1 | `public Shell()` _(in ConstructionControl.Shell)_ |  |
| 376 | 30 | **type** `public static final class Demolition` | A demolition on site: the city's own building, a shell, or a building it bought. |
| 400 | 1 | `public Demolition()` _(in ConstructionControl.Demolition)_ |  |
| 402 | 1 | `public String key()` _(in ConstructionControl.Demolition)_ |  |
| 404 | 1 | `public double owed()` _(in ConstructionControl.Demolition)_ | Points still to do. |
| 413 | 14 | **type** `public static final class Expropriation` | A compulsory purchase, as the city made it, kept and saved as its record. |
| 424 | 1 | `public Expropriation()` _(in ConstructionControl.Expropriation)_ |  |
| 425 | 1 | `public double total()` _(in ConstructionControl.Expropriation)_ |  |
| 429 | 6 | **type** `public static final class Run` | One stack's run since it last stood empty: how many of it have opened, and what its orders cost. |
| 433 | 1 | `public Run()` _(in ConstructionControl.Run)_ |  |
| 442 | 8 | **type** `public static final class Paving` | F. |
| 448 | 1 | `public Paving()` _(in ConstructionControl.Paving)_ |  |
| 452 | 13 | **type** `public static final class State` | Everything above, under one key in the save. |
| 469 | 1 | `public State toState()` | The state as the save writes it. |
| 472 | 12 | `public void restore(State saved)` | ...and back, from a save; null - a format-27 save, or none - is a city nobody has touched. |
| 491 | 4 | `public boolean engaged()` | Whether the month's advance has anything of this class to apply: an order set, a rush, a cancel waiting for the month's end, or a demolition on site. |

### the key of a site (lines 496-506)

| line | len | member | says |
|---:|---:|---|---|
| 499 | 1 | `public static String keyOf(BuildingsTemplate t)` | A stack's key: "B" and its template's id. |
| 502 | 4 | `public static int templateIdOf(String key)` | The template id a stack's key names, or -1 for a key that is not a stack's. |

### A. the order (lines 507-538)

| line | len | member | says |
|---:|---:|---|---|
| 509 | 1 | `public boolean isPrioritySet()` |  |
| 512 | 1 | `public java.util.List<String> savedOrder()` | The order as the player left it, keys of sites that may since have gone included. |
| 519 | 7 | `public java.util.List<String> effectiveOrder(java.util.List<String> present)` | The order the city's sites are served in: the player's, with any of them it does not name - ordered since - after it in the order given, and none that has gone. |
| 528 | 4 | `public void setOrder(java.util.List<String> keys)` | Sets the order: the player's hand, from the panel's arrows. |
| 534 | 4 | `public void clearOrder()` | Back to the crews' rule, with no order of the city's own. |

### B. the rushes (lines 539-598)

| line | len | member | says |
|---:|---:|---|---|
| 541 | 4 | `public Rush rushOf(String key)` |  |
| 547 | 4 | `public boolean isRushed(String key)` | Whether the player has this site on overtime. |
| 553 | 4 | `public int monthsOnOvertime(String key)` | Months in a row this site has been worked on overtime so far. |
| 559 | 9 | `public void setRush(String key, boolean on)` | On or off, by the player's hand. |
| 570 | 1 | `public java.util.List<Rush> rushes()` | Every rush on record, on or waiting out its month off. |
| 578 | 20 | `void afterMonth(java.util.Set<String> workedOnOvertime, java.util.Set<String> present, java.util.List<String> cityPresent)` | The month's rush bookkeeping, after the advance: a site worked on overtime counts a month more; one that was not - stopped, finished, or given no crews - starts again from nothing, and a stopped one is forgotten. |

### C. the cancels and the shells (lines 599-637)

| line | len | member | says |
|---:|---:|---|---|
| 601 | 1 | `public boolean isCancelling(String key)` |  |
| 603 | 4 | `public void setCancelling(String key, boolean cancel)` |  |
| 608 | 1 | `java.util.List<String> cancelling()` |  |
| 611 | 1 | `void clearCancelling()` | Every cancel settled at the month's end, stopped or moot. |
| 613 | 1 | `public java.util.List<Shell> shells()` |  |
| 615 | 4 | `public Shell shellOf(int templateId)` |  |
| 621 | 14 | `Shell addShell(BuildingsTemplate t, int buildings, double progress, double materialsOwed, int month)` | A stopped order's buildings, onto the shell of that building - a second stop joins the first. |
| 636 | 1 | `void removeShell(Shell s)` |  |

### D. the demolitions (lines 638-674)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 1 | `public java.util.List<Demolition> demolitions()` |  |
| 642 | 4 | `public Demolition demolitionOf(String key)` |  |
| 647 | 18 | `Demolition addDemolition(BuildingsTemplate t, int buildings, double points, double salvageUnits, double landSqFt, double paid, ...` |  |
| 667 | 5 | `public int closingOf(int templateId)` | Buildings of this kind ordered demolished that still stand until the month starts. |
| 673 | 1 | `void removeDemolition(Demolition d)` |  |

### E. the buy-outs (lines 675-680)

| line | len | member | says |
|---:|---:|---|---|
| 677 | 1 | `public java.util.List<Expropriation> expropriations()` |  |
| 679 | 1 | `void recordExpropriation(Expropriation e)` |  |

### F. the pavings (lines 681-721)

| line | len | member | says |
|---:|---:|---|---|
| 683 | 1 | `public java.util.List<Paving> pavings()` |  |
| 686 | 5 | `public int paving()` | Gravel roads being paved: on the Paved Road site, standing until their paving opens. |
| 693 | 8 | `Paving addPaving(int roads, int ahead, int month)` | A paving of n roads, behind the `ahead` Paved Roads already on the site. |
| 707 | 14 | `int pavedOf(int finished)` | Of `finished` Paved Roads opening this month off the front of the site, the ones that are pavings', each paving moved up the site by them: the gravel roads to retire. |

### each stack's run (lines 722-743)

| line | len | member | says |
|---:|---:|---|---|
| 725 | 4 | `public Run runOf(int templateId)` | A stack's run, null if none is on record. |
| 730 | 9 | `Run runFor(int templateId)` |  |
| 740 | 3 | `void endRun(int templateId)` |  |

### a reform (lines 744-755)

| line | len | member | says |
|---:|---:|---|---|
| 747 | 8 | `public void redenominate(double scale)` | A currency reform: the money is money; the points, the units and the ground are not. |

### THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. (lines 756-798)

| line | len | member | says |
|---:|---:|---|---|
| 761 | 2 | **type** `public record Overtime(String key, String building, int month, double share, double work, double perPoint, ...` | A rushed site worked on overtime this month: its share, its work, the builders' wage bill a point it was struck at, and the premium on its crews, before the tax. |
| 765 | 2 | **type** `public record Refund(int templateId, String building, int buildings, double value, double allowance, double...` | An order stopped at the month's end: what comes back to the city, the material allowance in it, the points of work it was for, and the shell it left. |
| 774 | 11 | **type** `public static final class Completed` | A demolition finished this month - and, once Game has sold it, its own sale: the units the builders bought and what they paid, which the inbox reads off the site rather than by its name (after the docs pass: two of on... |
| 777 | 1 | `public Completed(Demolition site)` _(in ConstructionControl.Completed)_ |  |
| 778 | 1 | `public Demolition site()` _(in ConstructionControl.Completed)_ |  |
| 780 | 1 | `public double unitsSold()` _(in ConstructionControl.Completed)_ | Units of its material the builders bought. |
| 782 | 1 | `public double paid()` _(in ConstructionControl.Completed)_ | ...and what they paid for them. |
| 783 | 1 | `void sold(double units, double money)` _(in ConstructionControl.Completed)_ |  |
| 787 | 1 | **type** `public record Paved(int roads, double landFreedSqFt)` | F. |
| 790 | 8 | **type** `public static final class Events` | The month's events, as the advance left them. |
| 796 | 1 | `public boolean isEmpty()` _(in ConstructionControl.Events)_ |  |

