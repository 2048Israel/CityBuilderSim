# ConstructionControl.java - 665 lines · 53 methods · 8 constants · model

`ham/citybuildersim/ConstructionControl.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The player's hand on the construction queue (0.7.22): the order the
> city's own sites are served in, the sites it has put on overtime, the
> orders it has stopped and the shells they left, the buildings it is
> pulling down, and what it paid to buy a business's buildings to do so.
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
> the rest; it is bookkeeping and moves nothing.)
> 
> A SITE is what the model keeps on site: one stack per building type
> ("B" + its template id) - however many orders are on it - and, since
> this class, each demolition the city has ordered ("D" + its number). The
> CITY'S sites are the stacks whose every order on site is the city's, and
> the demolitions. A stack the city shares with an investor's order is not
> the city's to reorder, rush or stop: its progress is one pool, and the
> model cannot divide it by who paid for which building.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (3)

**Used by (9):** [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [DataSave](DataSave.md), [Game](Game.md), [Inbox](Inbox.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 40 | A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS |
| 67 | B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES |
| 159 | C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT |
| 191 | D. DEMOLISH: THE CITY'S OWN BUILDINGS |
| 235 | E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY |
| 274 | · THE STATE, as the save carries it |
| 408 | · the key of a site |
| 419 | · A. the order |
| 451 | · B. the rushes |
| 511 | · C. the cancels and the shells |
| 550 | · D. the demolitions |
| 587 | · E. the buy-outs |
| 593 | · each stack's run |
| 615 | · a reform |
| 627 | · THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `ConstructionControl.STANDARD_HOURS` | `40` | The normal working week the report measures against, in hours. |
| 113 | `ConstructionControl.OVERTIME_HOURS` | `50` | The week on overtime: the Business Roundtable's five tens (Report C-2, Figure 4). |
| 116 | `ConstructionControl.OVERTIME_RATE` | `1.5` | What an hour over the standard week is paid at: time and a half (Canada Labour Code, section 174). |
| 119 | `ConstructionControl.WEEKS_A_MONTH` | `52.0 / 12.0` | Weeks in a month for averaging the report's table: 52 / 12, the 4.33 the brief reads it at. |
| 122 | `ConstructionControl.OVERTIME_WEEKS_ENDING` | `{ 2, 4, 6, 8, 10 }` | Where each step of the report's 50-hour curve ends, in weeks on the schedule (Report C-2, Figure 4); the last step runs on. |
| 125 | `ConstructionControl.OVERTIME_PRODUCTIVITY` | `{ 0.926, 0.90, 0.87, 0.80, 0.752, 0.750 }` | Productivity on a 50-hour week against a 40-hour one, for each step above and beyond the last (Report C-2, Figure 4). |
| 128 | `ConstructionControl.OVERTIME_WAGE_BILL` | `(STANDARD_HOURS +(OVERTIME_HOURS - STANDARD_HOURS) * OVERTIME_RATE) / STANDAR...` | The crews' wage bill on overtime over their normal bill: (40 + 10 x 1.5) / 40 = 1.375. |
| 233 | `ConstructionControl.DEMOLITION_SHARE` | `0.05` | The share of a building's construction points its demolition is: Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April 2017), over the average new single-family home of 2015, $289,415 (NAHB) - 5.1%, ta... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 280 | `public String key` |  |
| 282 | `public boolean on` | Whether the player has it on; off until the next month passes, so a stop and a restart between two presses keep the count. |
| 284 | `public int months` | Consecutive months worked on overtime so far. |
| 291 | `public int templateId` |  |
| 292 | `public String building` |  |
| 293 | `public int buildings` |  |
| 295 | `public double progress` | The points of work it holds. |
| 297 | `public double materialsOwed` | Units of material it has still to draw - what a restart quotes for. |
| 299 | `public double refunded` | What came back to the city when it stopped. |
| 301 | `public int month` | The month it stopped. |
| 307 | `public int id` |  |
| 308 | `public int templateId` |  |
| 309 | `public String building` |  |
| 310 | `public int buildings` |  |
| 312 | `public double points` | The points of work it is, all told: DEMOLITION_SHARE of the work it holds. |
| 313 | `public double progress` |  |
| 315 | `public double salvageUnits` | Units of construction material the buildings hold, salvaged when it completes. |
| 317 | `public double landSqFt` | The ground it holds until it completes, in square feet. |
| 319 | `public double paid` | What the city paid the builders for it. |
| 321 | `public double value` | ...and what of that they have still to earn. |
| 323 | `public String from` | "City" for the city's own, a sector's key for a building it bought, "Shell" for a stopped site. |
| 325 | `public int month` | The month it was ordered. |
| 327 | `public boolean closing` | True from the order until the month starts and the buildings close (Game.closeDemolished()): they still stand, and hold their own ground. |
| 329 | `public double groundPaid` | What the city paid a bought building's owner for its ground, for the demolition log; nothing for the city's own. |
| 344 | `public int month` |  |
| 345 | `public int templateId` |  |
| 346 | `public String building` |  |
| 347 | `public int buildings` |  |
| 348 | `public String sector` |  |
| 349 | `public double buildingValue` |  |
| 350 | `public double ground` |  |
| 351 | `public double businessLoss` |  |
| 353 | `public double months` | The months a replacement would have taken, which the business loss was paid for. |
| 360 | `public int templateId` |  |
| 361 | `public int built` |  |
| 362 | `public double billed` |  |
| 368 | `public boolean prioritySet` |  |
| 369 | `public java.util.List<String> order` |  |
| 370 | `public java.util.List<Rush> rushes` |  |
| 371 | `public java.util.List<String> cancelling` |  |
| 372 | `public java.util.List<Shell> shells` |  |
| 373 | `public java.util.List<Demolition> demolitions` |  |
| 374 | `public int nextDemolition` |  |
| 375 | `public java.util.List<Expropriation> expropriations` |  |
| 376 | `public java.util.List<Run> runs` |  |
| 379 | `private State state` |  |
| 646 | `private final Demolition site` |  |
| 647 | `private double unitsSold, paid` |  |
| 659 | `public final java.util.List<Overtime> overtime` |  |
| 660 | `public final java.util.List<Refund> refunds` |  |
| 661 | `public final java.util.List<Completed> completed` |  |
| 662 | `public double overtimePoints` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 628 | **type** `public final class ConstructionControl` | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it has stopped and the shells they left, the buildings it is pulling dow... |

### A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS (lines 40-66)

### B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES (lines 67-158)

| line | len | member | says |
|---:|---:|---|---|
| 136 | 12 | `public static double overtimeProductivity(int month)` | The report's productivity factor for the n-th consecutive month on overtime: its table averaged over that month's weeks, each week weighted by the part of it the month covers. |
| 150 | 3 | `public static double overtimeOutput(int month)` | A rushed site's month of work over a normal month's, in its n-th consecutive month on overtime: (50 / 40) x the month's factor. |
| 155 | 3 | `public static double premiumShare()` | What the city pays on a rushed site's crews, over their normal wage bill: 0.375. |

### C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT (lines 159-190)

### D. DEMOLISH: THE CITY'S OWN BUILDINGS (lines 191-234)

### E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY (lines 235-273)

### THE STATE, as the save carries it (lines 274-407)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 9 | **type** `public static final class Rush` | A site put on overtime, and how many months in a row it has been worked on it. |
| 285 | 1 | `public Rush()` _(in ConstructionControl.Rush)_ |  |
| 286 | 1 | `Rush(String key)` _(in ConstructionControl.Rush)_ |  |
| 290 | 14 | **type** `public static final class Shell` | A stopped site: what an order cancelled left standing. |
| 302 | 1 | `public Shell()` _(in ConstructionControl.Shell)_ |  |
| 306 | 30 | **type** `public static final class Demolition` | A demolition on site: the city's own building, a shell, or a building it bought. |
| 330 | 1 | `public Demolition()` _(in ConstructionControl.Demolition)_ |  |
| 332 | 1 | `public String key()` _(in ConstructionControl.Demolition)_ |  |
| 334 | 1 | `public double owed()` _(in ConstructionControl.Demolition)_ | Points still to do. |
| 343 | 14 | **type** `public static final class Expropriation` | A compulsory purchase, as the city made it, kept and saved as its record. |
| 354 | 1 | `public Expropriation()` _(in ConstructionControl.Expropriation)_ |  |
| 355 | 1 | `public double total()` _(in ConstructionControl.Expropriation)_ |  |
| 359 | 6 | **type** `public static final class Run` | One stack's run since it last stood empty: how many of it have opened, and what its orders cost. |
| 363 | 1 | `public Run()` _(in ConstructionControl.Run)_ |  |
| 367 | 11 | **type** `public static final class State` | Everything above, under one key in the save. |
| 382 | 1 | `public State toState()` | The state as the save writes it. |
| 385 | 11 | `public void restore(State saved)` | ...and back, from a save; null - a format-27 save, or none - is a city nobody has touched. |
| 403 | 4 | `public boolean engaged()` | Whether the month's advance has anything of this class to apply: an order set, a rush, a cancel waiting for the month's end, or a demolition on site. |

### the key of a site (lines 408-418)

| line | len | member | says |
|---:|---:|---|---|
| 411 | 1 | `public static String keyOf(BuildingsTemplate t)` | A stack's key: "B" and its template's id. |
| 414 | 4 | `public static int templateIdOf(String key)` | The template id a stack's key names, or -1 for a key that is not a stack's. |

### A. the order (lines 419-450)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 1 | `public boolean isPrioritySet()` |  |
| 424 | 1 | `public java.util.List<String> savedOrder()` | The order as the player left it, keys of sites that may since have gone included. |
| 431 | 7 | `public java.util.List<String> effectiveOrder(java.util.List<String> present)` | The order the city's sites are served in: the player's, with any of them it does not name - ordered since - after it in the order given, and none that has gone. |
| 440 | 4 | `public void setOrder(java.util.List<String> keys)` | Sets the order: the player's hand, from the panel's arrows. |
| 446 | 4 | `public void clearOrder()` | Back to the crews' rule, with no order of the city's own. |

### B. the rushes (lines 451-510)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 4 | `public Rush rushOf(String key)` |  |
| 459 | 4 | `public boolean isRushed(String key)` | Whether the player has this site on overtime. |
| 465 | 4 | `public int monthsOnOvertime(String key)` | Months in a row this site has been worked on overtime so far. |
| 471 | 9 | `public void setRush(String key, boolean on)` | On or off, by the player's hand. |
| 482 | 1 | `public java.util.List<Rush> rushes()` | Every rush on record, on or waiting out its month off. |
| 490 | 20 | `void afterMonth(java.util.Set<String> workedOnOvertime, java.util.Set<String> present, java.util.List<String> cityPresent)` | The month's rush bookkeeping, after the advance: a site worked on overtime counts a month more; one that was not - stopped, finished, or given no crews - starts again from nothing, and a stopped one is forgotten. |

### C. the cancels and the shells (lines 511-549)

| line | len | member | says |
|---:|---:|---|---|
| 513 | 1 | `public boolean isCancelling(String key)` |  |
| 515 | 4 | `public void setCancelling(String key, boolean cancel)` |  |
| 520 | 1 | `java.util.List<String> cancelling()` |  |
| 523 | 1 | `void clearCancelling()` | Every cancel settled at the month's end, stopped or moot. |
| 525 | 1 | `public java.util.List<Shell> shells()` |  |
| 527 | 4 | `public Shell shellOf(int templateId)` |  |
| 533 | 14 | `Shell addShell(BuildingsTemplate t, int buildings, double progress, double materialsOwed, int month)` | A stopped order's buildings, onto the shell of that building - a second stop joins the first. |
| 548 | 1 | `void removeShell(Shell s)` |  |

### D. the demolitions (lines 550-586)

| line | len | member | says |
|---:|---:|---|---|
| 552 | 1 | `public java.util.List<Demolition> demolitions()` |  |
| 554 | 4 | `public Demolition demolitionOf(String key)` |  |
| 559 | 18 | `Demolition addDemolition(BuildingsTemplate t, int buildings, double points, double salvageUnits, double landSqFt, double paid, ...` |  |
| 579 | 5 | `public int closingOf(int templateId)` | Buildings of this kind ordered demolished that still stand until the month starts. |
| 585 | 1 | `void removeDemolition(Demolition d)` |  |

### E. the buy-outs (lines 587-592)

| line | len | member | says |
|---:|---:|---|---|
| 589 | 1 | `public java.util.List<Expropriation> expropriations()` |  |
| 591 | 1 | `void recordExpropriation(Expropriation e)` |  |

### each stack's run (lines 593-614)

| line | len | member | says |
|---:|---:|---|---|
| 596 | 4 | `public Run runOf(int templateId)` | A stack's run, null if none is on record. |
| 601 | 9 | `Run runFor(int templateId)` |  |
| 611 | 3 | `void endRun(int templateId)` |  |

### a reform (lines 615-626)

| line | len | member | says |
|---:|---:|---|---|
| 618 | 8 | `public void redenominate(double scale)` | A currency reform: the money is money; the points, the units and the ground are not. |

### THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. (lines 627-665)

| line | len | member | says |
|---:|---:|---|---|
| 632 | 2 | **type** `public record Overtime(String key, String building, int month, double share, double work, double perPoint, ...` | A rushed site worked on overtime this month: its share, its work, the builders' wage bill a point it was struck at, and the premium on its crews, before the tax. |
| 636 | 2 | **type** `public record Refund(int templateId, String building, int buildings, double value, double allowance, double...` | An order stopped at the month's end: what comes back to the city, the material allowance in it, the points of work it was for, and the shell it left. |
| 645 | 11 | **type** `public static final class Completed` | A demolition finished this month - and, once Game has sold it, its own sale: the units the builders bought and what they paid, which the inbox reads off the site rather than by its name (after the docs pass: two of on... |
| 648 | 1 | `public Completed(Demolition site)` _(in ConstructionControl.Completed)_ |  |
| 649 | 1 | `public Demolition site()` _(in ConstructionControl.Completed)_ |  |
| 651 | 1 | `public double unitsSold()` _(in ConstructionControl.Completed)_ | Units of its material the builders bought. |
| 653 | 1 | `public double paid()` _(in ConstructionControl.Completed)_ | ...and what they paid for them. |
| 654 | 1 | `void sold(double units, double money)` _(in ConstructionControl.Completed)_ |  |
| 658 | 7 | **type** `public static final class Events` | The month's events, as the advance left them. |
| 663 | 1 | `public boolean isEmpty()` _(in ConstructionControl.Events)_ |  |

