# Inbox.java - 880 lines · 39 methods · 4 constants · model

`ham/citybuildersim/Inbox.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> Everything the city has had to say for itself, newest first.
> 
> WHAT THIS REPLACED
> 
> Four red boxes that appeared in the middle of the main screen and had to be
> dismissed before you could get on with anything. They worked - the shedding
> banner is the reason a 4,000-month playtest stopped freezing at 660 people -
> but they had two faults a banner cannot fix. A dismissed banner was gone: no
> record it had ever appeared, and nothing to go back to when the player
> changed their mind three months later. And a banner only exists on the screen
> it was written for, so a warning raised during a fifty-month skip was
> announced to an empty room.
> 
> A notice does neither. It is raised by the CITY, once a month, whether
> anybody is watching; it stays in the list after it is read; and it stays
> after it is resolved, greyed, for two years, so "what went wrong in the
> forties" has an answer.
> 
> WHY THE TEXT IS BUILT HERE AND NOT ON THE SCREEN
> 
> Because the screen is not always there. These sentences used to be built
> inside UserInterface, which meant they could only be built while somebody was
> looking at the main menu - so a condition that arose and passed inside a skip
> produced no words at all. Built here, they are written the month the
> condition is true, and the screen's only job is to draw them.

**Uses:** [Notice](Notice.md) (22), [Game](Game.md) (17), [PolicyDates](PolicyDates.md) (10), [Healthcare](Healthcare.md) (6), [LandmassView](LandmassView.md) (5), [ConstructionControl](ConstructionControl.md) (5), [Bank](Bank.md) (5), [Crime](Crime.md) (5), [CareType](CareType.md) (5), [CausesOfDeath](CausesOfDeath.md) (4), [TaxPolicy](TaxPolicy.md) (4), [ResearchCatalog](ResearchCatalog.md) (4), [Health](Health.md) (4), [AgeBand](AgeBand.md) (4), [Research](Research.md) (3), [TreasuryFund](TreasuryFund.md) (2), [BuildingManager](BuildingManager.md) (2), [LandManager](LandManager.md) (2), [Sickness](Sickness.md) (2), [Migration](Migration.md) (2), [Landmasses](Landmasses.md) (1), [GameFiles](GameFiles.md) (1), [Sector](Sector.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1)

**Used by (9):** [DiscoveryCheck](DiscoveryCheck.md), [Game](Game.md), [InboxCheck](InboxCheck.md), [LandmassCheck](LandmassCheck.md), [LandmassView](LandmassView.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PrizeCheck](PrizeCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 50 | THE MONTH |
| 331 | · the autosave (UP6) |
| 444 | READING |
| 532 | SAVE AND LOAD |
| 553 | WHAT EACH ONE SAYS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `Inbox.KEEP_MONTHS` | `24` | How long a resolved notice stays readable. |
| 42 | `Inbox.LANDLOCK_REPEAT_MONTHS` | `12` | How long the "nowhere to build" notice stands before the wait is told again in a new one (PT1-M2): a year. |
| 229 | `Inbox.NOTICED` | `noticed()` | The levers whose opening is told: the ones TaxPolicy holds, the minimum wage (LabourMarket's floor, which TaxPolicy dates but does not hold) and zoning (Game.setZone's gate). |
| 420 | `Inbox.BODY_COLUMNS` | `62` | A body line's most characters (0.7.73): the measure the bodies written by hand keep, inside the list's width in its mono face. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private List<Notice> notices` | Newest LAST, because that is the order they happened in and appending is the honest way to record a sequence. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 845 | **type** `public class Inbox` | Everything the city has had to say for itself, newest first. |

### THE MONTH (lines 50-330)

| line | len | member | says |
|---:|---:|---|---|
| 61 | 151 | `public void takeMonth(Game game)` | Raise what is newly true, refresh what still is, resolve what is not. |
| 214 | 7 | `private static List<String> influenzaBody(Game game)` | The influenza's notice (R0b): its dead last month and in the last twelve, and the share of the workforce off sick with it. |
| 223 | 4 | `private static String people(double count)` | "1 person", "41 people": a count of the dead, rounded to a whole one. |
| 231 | 6 | `private static PolicyDates.Lever[] noticed()` |  |
| 244 | 9 | `private void takeLevers(Game game, int month)` | The Policy levers history opens (R0): in the month a lever opens, one notice of Canada's first terms for it, titled "An income tax: 1917", keyed "lever:" and its name so it is raised once. |
| 261 | 30 | `private void takeResearch(Game game, int month)` | What the city's research found and what reached it from abroad (R3), each a one-off like a lever's: raised in the month it happened, keyed "found:" or "abroad:" and the discovery's code so that it is raised once, sett... |
| 296 | 6 | `static String prizeSentence(Research.Purse purse)` | Where a find's prize went (R4): "The $12.0M prize: $9.1M to the Research Labs, $2.9M to the schools' grants." A part of nothing is left out ("The $12.0M prize: $12.0M to the schools' grants."). |
| 304 | 3 | `private static String capital(String phrase)` | "the Medical school" as a sentence starts it: "The Medical school". |
| 309 | 4 | `static String leverTitle(PolicyDates.Lever lever)` | "An income tax: 1917", "A profit tax: 1916", "Student aid: 1939", "Zoning: 1924": the lever as the player reads it (PolicyDates.named(), the rule the Policy screen's shut card shares), and the date it opened. |
| 319 | 11 | `private void takeIslands(Game game, int month)` | The ground the city owns on landmasses no port links (ET5), a standing notice for each, key "island:" and its id: it stays while the landmass is unlinked and the words in it follow what the city owns there, and it is ... |

### the autosave (UP6) (lines 331-443)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 20 | `public void autosaveWritten(int month, GameFiles.Result result)` | An autosave that could not be written whole (UP6, LB3-S), a condition notice keyed "autosave": the save failed, or it landed beside a history that could not be written (the slot then holds the new save and an older hi... |
| 364 | 16 | `private static List<String> overtimeBody(Game game)` | Every city's site on overtime whose next month is its third or later (ConstructionControl, B. |
| 382 | 12 | `private static List<String> demolishedBody(Game game)` | The demolitions that finished this month: the material sold to the builders, the ground freed. |
| 403 | 15 | `private void take(Game game, int month, String key, boolean nowTrue, String title, List<String> body)` | One condition, this month. |
| 423 | 14 | `static List<String> wrapped(String text, int columns)` | A sentence broken at spaces into lines of at most `columns` characters (a longer word on a line of its own). |
| 439 | 4 | `private void cull(int month)` | Drop what has been settled longer than anybody needs to remember. |

### READING (lines 444-531)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 5 | `public List<Notice> newestFirst()` | Newest first, which is the order the screen wants and nothing else does. |
| 456 | 1 | `public List<Notice> all()` | In the order they happened. |
| 458 | 1 | `public int size()` |  |
| 470 | 6 | `public Notice urgent()` | The one that interrupts: newest unread notice whose condition still holds. |
| 478 | 5 | `public int unread()` | What goes on the envelope. |
| 485 | 3 | `public static String toastKey(Notice n)` | What the window's toasts remember a notice by: its key and the month it was raised (PT1: here, where a harness can hold it). |
| 494 | 7 | `public List<Notice> toToast(java.util.Set<String> toasted)` | The urgent notices not yet toasted, oldest first, each added to `toasted` as it is returned: the window shows these and nothing else, so a notice is toasted once. |
| 507 | 3 | `public void markAllToasted(java.util.Set<String> toasted)` | Every notice now in the inbox counts as toasted (PT1): what a load calls, so a notice raised before the save is not toasted again at once. |
| 512 | 6 | `Notice latest(String key)` | The newest notice for a condition, live or settled, or null if there is not one. |
| 520 | 7 | `public Notice live(String key)` | The live notice for a condition, or null if there is not one. |
| 528 | 3 | `public void markRead(Notice notice, int month)` |  |

### SAVE AND LOAD (lines 532-552)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 4 | `public void restoreFrom(List<Notice> loaded)` | Takes over a loaded inbox wholesale. |
| 551 | 1 | `public void reset()` |  |

### WHAT EACH ONE SAYS (lines 553-880)

| line | len | member | says |
|---:|---:|---|---|
| 562 | 21 | `private static List<String> sheddingBody(Game game)` |  |
| 584 | 35 | `private static List<String> landLockBody(Game game)` |  |
| 621 | 4 | `private static String sectorLabel(Game game, String key)` | A sector's name from its key, as the land office's Waiting card names it (the key when there is no such sector). |
| 626 | 27 | `private static List<String> bankBody(Game game)` |  |
| 655 | 20 | `private static List<String> resolvedBody(Game game, TreasuryFund.Resolution r)` | The month the city resolved the bank (0.7.14): the log's sentence, with its figures. |
| 677 | 22 | `private static List<String> preferredBody(Game game)` | The bank's offer (0.7.14): the popup's sentence, TARP's terms. |
| 708 | 37 | `private static List<String> defaultsBody(Game game)` | FIRMS GOING BUST (0.7.8). |
| 747 | 8 | `private static String money(double thousands)` | Thousands - the unit the model counts in - as a player reads money: $1.3M, $450k. |
| 762 | 33 | `private static List<String> crimeBody(Game game)` | Crime at one and a half times Canada's rate or worse, or people the police caught with no cell to hold them (2026-09-11). |
| 808 | 72 | `private static List<String> healthcareBody(Game game)` | The three things healthcare is silently costing the city, when they apply. |

