# Inbox.java - 392 lines · 17 methods · 1 constants · model

`ham/citybuildersim/Inbox.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Notice](Notice.md) (12), [Game](Game.md) (7), [Healthcare](Healthcare.md) (6), [Crime](Crime.md) (5), [CareType](CareType.md) (5), [Health](Health.md) (4), [AgeBand](AgeBand.md) (4), [Sickness](Sickness.md) (2), [Migration](Migration.md) (2), [Bank](Bank.md) (1), [BuildingManager](BuildingManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1)

**Used by (3):** [Game](Game.md), [InboxCheck](InboxCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 47 | THE MONTH |
| 122 | READING |
| 171 | SAVE AND LOAD |
| 192 | WHAT EACH ONE SAYS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `Inbox.KEEP_MONTHS` | `24` | How long a resolved notice stays readable. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `private List<Notice> notices` | Newest LAST, because that is the order they happened in and appending is the honest way to record a sequence. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 357 | **type** `public class Inbox` | Everything the city has had to say for itself, newest first. |

### THE MONTH (lines 47-121)

| line | len | member | says |
|---:|---:|---|---|
| 58 | 33 | `public void takeMonth(Game game)` | Raise what is newly true, refresh what still is, resolve what is not. |
| 100 | 15 | `private void take(Game game, int month, String key, boolean nowTrue, String title, List<String> body)` | One condition, this month. |
| 117 | 4 | `private void cull(int month)` | Drop what has been settled longer than anybody needs to remember. |

### READING (lines 122-170)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 5 | `public List<Notice> newestFirst()` | Newest first, which is the order the screen wants and nothing else does. |
| 134 | 1 | `public List<Notice> all()` | In the order they happened. |
| 136 | 1 | `public int size()` |  |
| 144 | 6 | `public Notice urgent()` | The one that interrupts: newest unread notice whose condition still holds. |
| 152 | 5 | `public int unread()` | What goes on the envelope. |
| 159 | 7 | `public Notice live(String key)` | The live notice for a condition, or null if there is not one. |
| 167 | 3 | `public void markRead(Notice notice, int month)` |  |

### SAVE AND LOAD (lines 171-191)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 4 | `public void restoreFrom(List<Notice> loaded)` | Takes over a loaded inbox wholesale. |
| 190 | 1 | `public void reset()` |  |

### WHAT EACH ONE SAYS (lines 192-392)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 21 | `private static List<String> sheddingBody(Game game)` |  |
| 223 | 22 | `private static List<String> landLockBody(Game game)` |  |
| 246 | 21 | `private static List<String> bankBody(Game game)` |  |
| 274 | 33 | `private static List<String> crimeBody(Game game)` | Crime at one and a half times Canada's rate or worse, or people the police caught with no cell to hold them (2026-09-11). |
| 320 | 72 | `private static List<String> healthcareBody(Game game)` | The three things healthcare is silently costing the city, when they apply. |

