# ConstructionControl.java - 1,055 lines · 77 methods · 11 constants · model

`ham/citybuildersim/ConstructionControl.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [SaveCopy](SaveCopy.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (5), [BuildingsStacks](BuildingsStacks.md) (4), [BuildersShort](BuildersShort.md) (1)

**Used by (37):** [AdviceValueOracle](AdviceValueOracle.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankCheck](BankCheck.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingManager](BuildingManager.md), [BuildingStackCheck](BuildingStackCheck.md), [BuildingsTemplate](BuildingsTemplate.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FarmlandReserveCheck](FarmlandReserveCheck.md), [FitoutCheck](FitoutCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [Game](Game.md), [HousingMarketCheck](HousingMarketCheck.md), [Inbox](Inbox.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [NewUnitCheck](NewUnitCheck.md), [PowerWiresCheck](PowerWiresCheck.md), [RoadCheck](RoadCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [TownGridCheck](TownGridCheck.md), [UserInterface](UserInterface.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md)

## Sections

| line | section |
|---:|---|
| 42 | A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS |
| 72 | B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES |
| 164 | C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT |
| 196 | D. DEMOLISH: THE CITY'S OWN BUILDINGS |
| 262 | E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY |
| 301 | F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70) |
| 369 | G. ABROAD: A SITE HANDED TO THE FOREIGN CREWS (FC2b, 2026-11-24) |
| 396 | · THE STATE, as the save carries it |
| 687 | · the key of a site |
| 698 | · A. the order |
| 730 | · B. the rushes |
| 790 | · C. the cancels and the shells |
| 839 | · D. the demolitions |
| 876 | · E. the buy-outs |
| 882 | · F. the pavings |
| 923 | · G. the sites handed abroad |
| 965 | · each stack's run |
| 987 | · a reform |
| 1002 | · THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. |

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
| 402 | `public String key` |  |
| 404 | `public boolean on` | Whether the player has it on; off until the next month passes, so a stop and a restart between two presses keep the count. |
| 406 | `public int months` | Consecutive months worked on overtime so far. |
| 422 | `public int templateId` |  |
| 423 | `public String building` |  |
| 424 | `public long buildings` |  |
| 426 | `public double progress` | The points of work it holds. |
| 428 | `public double materialsOwed` | Units of material it has still to draw - what a restart quotes for. |
| 430 | `public double refunded` | What came back to the city when it stopped. |
| 432 | `public int month` | The month it stopped. |
| 438 | `public java.util.List<BuildingsStacks.Lot> lots` | The equipment its buildings' orders froze (R1e; BuildingsStacks' lots): the buildings that come without a piece, by the pieces; the rest are the whole template, as every building stopped before R1e is. |
| 458 | `public int id` |  |
| 459 | `public int templateId` |  |
| 460 | `public String building` |  |
| 461 | `public long buildings` |  |
| 463 | `public double points` | The points of work it is, all told: DEMOLITION_SHARE of the work it holds. |
| 464 | `public double progress` |  |
| 466 | `public double salvageUnits` | Units of construction material salvaged when it completes, as quoted: SALVAGE_SHARE of what the buildings hold since 0.7.106 (all of it until then). |
| 468 | `public double landSqFt` | The ground it holds until it completes, in square feet. |
| 470 | `public double paid` | What the city paid the builders for it. |
| 472 | `public double value` | ...and what of that they have still to earn. |
| 474 | `public String from` | "City" for the city's own, a sector's key for a building it bought, "Shell" for a stopped site. |
| 476 | `public int month` | The month it was ordered. |
| 478 | `public boolean closing` | True from the order until the month starts and the buildings close (Game.closeDemolished()): they still stand, and hold their own ground. |
| 480 | `public double groundPaid` | What the city paid a bought building's owner for its ground, for the demolition log; nothing for the city's own. |
| 504 | `public int month` |  |
| 505 | `public int templateId` |  |
| 506 | `public String building` |  |
| 507 | `public long buildings` |  |
| 508 | `public String sector` |  |
| 509 | `public double buildingValue` |  |
| 510 | `public double ground` |  |
| 511 | `public double businessLoss` |  |
| 513 | `public double months` | The months a replacement would have taken, which the business loss was paid for. |
| 530 | `public int templateId` |  |
| 531 | `public long built` |  |
| 532 | `public double billed` |  |
| 552 | `public long roads` |  |
| 554 | `public long ahead` | Paved Roads on the site ahead of this paving, which open first. |
| 556 | `public int month` | The month it was ordered. |
| 575 | `public String key` | The site's key: "B" and its template's id (keyOf()). |
| 576 | `public int templateId` |  |
| 577 | `public String building` |  |
| 579 | `public String payer` | Who handed it over: "City", or a sector's key (BuildingsStacks.Contract). |
| 581 | `public int month` | The month it was handed over. |
| 583 | `public double points` | The points of work handed over. |
| 585 | `public double owed` | ...and the points still owed abroad. |
| 587 | `public double materials` | The units of material the part still draws. |
| 589 | `public double pace` | The foreign crews' month on it, in points a month, struck at the handover (BuildersShort.sitePace()). |
| 591 | `public double pricePerPoint` | What the crews bill a point, in the city's money. |
| 593 | `public double paid` | The extra the payer paid to hand it over, with the builders' tax in it. |
| 595 | `public double monthsHome` | The months the part would have taken at home, and the months it takes abroad, at the handover: Double.MAX_VALUE for a wait that never ends (siteMonths()' never; the save's Gson writes no infinity, REV-FC2b-1). |
| 596 | `public double monthsAbroad` |  |
| 598 | `public double interestSaved` | What the months saved are worth the payer in interest (BuildersShort.interestSaved()), and in the earnings over it (broughtForward() less that). |
| 599 | `public double earningsForward` |  |
| 617 | `public boolean prioritySet` |  |
| 618 | `public java.util.List<String> order` |  |
| 619 | `public java.util.List<Rush> rushes` |  |
| 620 | `public java.util.List<String> cancelling` |  |
| 621 | `public java.util.List<Shell> shells` |  |
| 622 | `public java.util.List<Demolition> demolitions` |  |
| 623 | `public int nextDemolition` |  |
| 624 | `public java.util.List<Expropriation> expropriations` |  |
| 625 | `public java.util.List<Run> runs` |  |
| 627 | `public java.util.List<Paving> paving` | F. |
| 629 | `public java.util.List<Abroad> abroad` | G. |
| 656 | `private State state` |  |
| 1032 | `private final Demolition site` |  |
| 1033 | `private double unitsSold, paid` |  |
| 1048 | `public final java.util.List<Overtime> overtime` |  |
| 1049 | `public final java.util.List<Refund> refunds` |  |
| 1050 | `public final java.util.List<Completed> completed` |  |
| 1051 | `public final java.util.List<Paved> paved` |  |
| 1052 | `public double overtimePoints` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 1016 | **type** `public final class ConstructionControl` | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it has stopped and the shells they left, the buildings it is pulling dow... |

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

### G. ABROAD: A SITE HANDED TO THE FOREIGN CREWS (FC2b, 2026-11-24) (lines 369-395)

### THE STATE, as the save carries it (lines 396-686)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 18 | **type** `public static final class Rush implements Cloneable` | A site put on overtime, and how many months in a row it has been worked on it. |
| 407 | 1 | `public Rush()` _(in ConstructionControl.Rush)_ |  |
| 408 | 1 | `Rush(String key)` _(in ConstructionControl.Rush)_ |  |
| 411 | 7 | `Rush copy()` _(in ConstructionControl.Rush)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 421 | 34 | **type** `public static final class Shell implements Cloneable` | A stopped site: what an order cancelled left standing. |
| 439 | 1 | `public Shell()` _(in ConstructionControl.Shell)_ |  |
| 442 | 12 | `Shell copy()` _(in ConstructionControl.Shell)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 457 | 39 | **type** `public static final class Demolition implements Cloneable` | A demolition on site: the city's own building, a shell, or a building it bought. |
| 481 | 1 | `public Demolition()` _(in ConstructionControl.Demolition)_ |  |
| 484 | 7 | `Demolition copy()` _(in ConstructionControl.Demolition)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 492 | 1 | `public String key()` _(in ConstructionControl.Demolition)_ |  |
| 494 | 1 | `public double owed()` _(in ConstructionControl.Demolition)_ | Points still to do. |
| 503 | 24 | **type** `public static final class Expropriation implements Cloneable` | A compulsory purchase, as the city made it, kept and saved as its record. |
| 514 | 1 | `public Expropriation()` _(in ConstructionControl.Expropriation)_ |  |
| 517 | 7 | `Expropriation copy()` _(in ConstructionControl.Expropriation)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 525 | 1 | `public double total()` _(in ConstructionControl.Expropriation)_ |  |
| 529 | 15 | **type** `public static final class Run implements Cloneable` | One stack's run since it last stood empty: how many of it have opened, and what its orders cost. |
| 533 | 1 | `public Run()` _(in ConstructionControl.Run)_ |  |
| 536 | 7 | `Run copy()` _(in ConstructionControl.Run)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 551 | 17 | **type** `public static final class Paving implements Cloneable` | F. |
| 557 | 1 | `public Paving()` _(in ConstructionControl.Paving)_ |  |
| 560 | 7 | `Paving copy()` _(in ConstructionControl.Paving)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 573 | 41 | **type** `public static final class Abroad implements Cloneable` | G. |
| 600 | 1 | `public Abroad()` _(in ConstructionControl.Abroad)_ |  |
| 603 | 1 | `public double monthsLeft()` _(in ConstructionControl.Abroad)_ | The months the crews take on what is left, at their pace: nothing for nothing owed, endless for no pace (BuildersShort.monthsAbroad(); FC2b). |
| 606 | 7 | `Abroad copy()` _(in ConstructionControl.Abroad)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 616 | 39 | **type** `public static final class State implements Cloneable` | Everything above, under one key in the save. |
| 637 | 17 | `State copy()` _(in ConstructionControl.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() hands out the live one, which the month and the player go on changing while the autosave is written. |
| 659 | 1 | `public State toState()` | The state as the save writes it. |
| 662 | 13 | `public void restore(State saved)` | ...and back, from a save; null - a format-27 save, or none - is a city nobody has touched. |
| 682 | 4 | `public boolean engaged()` | Whether the month's advance has anything of this class to apply: an order set, a rush, a cancel waiting for the month's end, or a demolition on site. |

### the key of a site (lines 687-697)

| line | len | member | says |
|---:|---:|---|---|
| 690 | 1 | `public static String keyOf(BuildingsTemplate t)` | A stack's key: "B" and its template's id. |
| 693 | 4 | `public static int templateIdOf(String key)` | The template id a stack's key names, or -1 for a key that is not a stack's. |

### A. the order (lines 698-729)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 1 | `public boolean isPrioritySet()` |  |
| 703 | 1 | `public java.util.List<String> savedOrder()` | The order as the player left it, keys of sites that may since have gone included. |
| 710 | 7 | `public java.util.List<String> effectiveOrder(java.util.List<String> present)` | The order the city's sites are served in: the player's, with any of them it does not name - ordered since - after it in the order given, and none that has gone. |
| 719 | 4 | `public void setOrder(java.util.List<String> keys)` | Sets the order: the player's hand, from the panel's arrows. |
| 725 | 4 | `public void clearOrder()` | Back to the crews' rule, with no order of the city's own. |

### B. the rushes (lines 730-789)

| line | len | member | says |
|---:|---:|---|---|
| 732 | 4 | `public Rush rushOf(String key)` |  |
| 738 | 4 | `public boolean isRushed(String key)` | Whether the player has this site on overtime. |
| 744 | 4 | `public int monthsOnOvertime(String key)` | Months in a row this site has been worked on overtime so far. |
| 750 | 9 | `public void setRush(String key, boolean on)` | On or off, by the player's hand. |
| 761 | 1 | `public java.util.List<Rush> rushes()` | Every rush on record, on or waiting out its month off. |
| 769 | 20 | `void afterMonth(java.util.Set<String> workedOnOvertime, java.util.Set<String> present, java.util.List<String> cityPresent)` | The month's rush bookkeeping, after the advance: a site worked on overtime counts a month more; one that was not - stopped, finished, or given no crews - starts again from nothing, and a stopped one is forgotten. |

### C. the cancels and the shells (lines 790-838)

| line | len | member | says |
|---:|---:|---|---|
| 792 | 1 | `public boolean isCancelling(String key)` |  |
| 794 | 4 | `public void setCancelling(String key, boolean cancel)` |  |
| 799 | 1 | `java.util.List<String> cancelling()` |  |
| 802 | 1 | `void clearCancelling()` | Every cancel settled at the month's end, stopped or moot. |
| 804 | 1 | `public java.util.List<Shell> shells()` |  |
| 806 | 4 | `public Shell shellOf(int templateId)` |  |
| 812 | 3 | `Shell addShell(BuildingsTemplate t, long buildings, double progress, double materialsOwed, int month)` | A stopped order's buildings, onto the shell of that building - a second stop joins the first. |
| 817 | 19 | `Shell addShell(BuildingsTemplate t, long buildings, double progress, double materialsOwed, int month, java.util.List<BuildingsS...` | ...with the equipment their orders froze (R1e, BuildingsStacks.getLots()), joining the shell's; none for null or empty. |
| 837 | 1 | `void removeShell(Shell s)` |  |

### D. the demolitions (lines 839-875)

| line | len | member | says |
|---:|---:|---|---|
| 841 | 1 | `public java.util.List<Demolition> demolitions()` |  |
| 843 | 4 | `public Demolition demolitionOf(String key)` |  |
| 848 | 18 | `Demolition addDemolition(BuildingsTemplate t, long buildings, double points, double salvageUnits, double landSqFt, double paid,...` |  |
| 868 | 5 | `public long closingOf(int templateId)` | Buildings of this kind ordered demolished that still stand until the month starts. |
| 874 | 1 | `void removeDemolition(Demolition d)` |  |

### E. the buy-outs (lines 876-881)

| line | len | member | says |
|---:|---:|---|---|
| 878 | 1 | `public java.util.List<Expropriation> expropriations()` |  |
| 880 | 1 | `void recordExpropriation(Expropriation e)` |  |

### F. the pavings (lines 882-922)

| line | len | member | says |
|---:|---:|---|---|
| 884 | 1 | `public java.util.List<Paving> pavings()` |  |
| 887 | 5 | `public long paving()` | Gravel roads being paved: on the Paved Road site, standing until their paving opens. |
| 894 | 8 | `Paving addPaving(long roads, long ahead, int month)` | A paving of n roads, behind the `ahead` Paved Roads already on the site. |
| 908 | 14 | `long pavedOf(long finished)` | Of `finished` Paved Roads opening this month off the front of the site, the ones that are pavings', each paving moved up the site by them: the gravel roads to retire. |

### G. the sites handed abroad (lines 923-964)

| line | len | member | says |
|---:|---:|---|---|
| 926 | 4 | `public Abroad abroadOf(String key)` | A site's record of work handed to the foreign crews, null if none (FC2b). |
| 932 | 1 | `public java.util.List<Abroad> abroadSites()` | Every site handed to the foreign crews, in the order they went: an unmodifiable view. |
| 935 | 4 | `void addAbroad(Abroad a)` | Records a handover, replacing the site's own if it has one: a site has one while it stands. |
| 941 | 3 | `void endAbroad(String key)` | The part is built, or the site is empty: its record ends. |
| 946 | 4 | `public double abroadOwedOf(String key)` | The points still owed abroad on a site: nothing for a site with no record. |
| 952 | 5 | `public double abroadOwedTotal()` | ...and on every site. |
| 959 | 5 | `public double abroadMonth()` | The foreign crews' month on the sites handed them: the least of what each owes and its pace, summed. |

### each stack's run (lines 965-986)

| line | len | member | says |
|---:|---:|---|---|
| 968 | 4 | `public Run runOf(int templateId)` | A stack's run, null if none is on record. |
| 973 | 9 | `Run runFor(int templateId)` |  |
| 983 | 3 | `void endRun(int templateId)` |  |

### a reform (lines 987-1001)

| line | len | member | says |
|---:|---:|---|---|
| 990 | 11 | `public void redenominate(double scale)` | A currency reform: the money is money; the points, the units and the ground are not. |

### THE MONTH'S EVENTS, for Game to settle. A month's flows, read once. (lines 1002-1055)

| line | len | member | says |
|---:|---:|---|---|
| 1012 | 2 | **type** `public record Overtime(String key, String building, int month, double share, double work, double perPoint, ...` | A rushed site worked on overtime this month: its share, its work, the builders' wage bill a point it was struck at, the premium on its crews, before the tax, and the part of its share the city's own crews worked (FC1b... |
| 1022 | 2 | **type** `public record Refund(int templateId, String building, long buildings, double value, double allowance, doubl...` | An order stopped at the month's end: what comes back to the city, the material allowance in it, the points of work it was for, the shell it left, and whether it was a depot of the builders' served first (BuildingManag... |
| 1031 | 11 | **type** `public static final class Completed` | A demolition finished this month - and, once Game has sold it, its own sale: the units the builders bought and what they paid, which the inbox reads off the site rather than by its name (after the docs pass: two of on... |
| 1034 | 1 | `public Completed(Demolition site)` _(in ConstructionControl.Completed)_ |  |
| 1035 | 1 | `public Demolition site()` _(in ConstructionControl.Completed)_ |  |
| 1037 | 1 | `public double unitsSold()` _(in ConstructionControl.Completed)_ | Units of its material the builders bought. |
| 1039 | 1 | `public double paid()` _(in ConstructionControl.Completed)_ | ...and what they paid for them. |
| 1040 | 1 | `void sold(double units, double money)` _(in ConstructionControl.Completed)_ |  |
| 1044 | 1 | **type** `public record Paved(long roads, double landFreedSqFt)` | F. |
| 1047 | 8 | **type** `public static final class Events` | The month's events, as the advance left them. |
| 1053 | 1 | `public boolean isEmpty()` _(in ConstructionControl.Events)_ |  |

