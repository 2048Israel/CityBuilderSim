# Notice.java - 98 lines · 15 methods · 0 constants · model

`ham/citybuildersim/Notice.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> One thing the city needs told about, and whether anybody has looked at it.
> 
> WHY THE TEXT IS STORED RATHER THAN REBUILT
> 
> Every other panel in this game derives its words from the city as it is now,
> and that is right for a panel, because a panel describes the present. A
> notice does not. A notice says "on month 340 your builders started being laid
> off, and here is what it was costing", and by the time the player reads it
> the city may have moved on - so rebuilding the sentence from today's figures
> would rewrite history, and a RESOLVED notice could not be rebuilt at all: the
> condition it describes is gone, and the numbers behind it with it.
> 
> So the body is written when the notice is raised and refreshed every month it
> is still true, which keeps a live warning current and freezes a resolved one
> at the last thing it actually said.
> 
> THREE MONTHS, AND THEY MEAN DIFFERENT THINGS
> 
> raised   - when the city first said it. Never changes.
> read     - when the player opened it. 0 while unread, and unread is what
>            makes a notice force the inbox open.
> resolved - when the condition stopped being true. 0 while it still is. A
>            resolved notice stays in the list, greyed, for two years.

**Used by (6):** [BankCheck](BankCheck.md), [DataSave](DataSave.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [LongPlaytest](LongPlaytest.md), [UserInterface](UserInterface.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `private String key` | Which condition this is. |
| 40 | `private String title` |  |
| 41 | `private List<String> body` |  |
| 43 | `private int raised` |  |
| 44 | `private int read` |  |
| 45 | `private int resolved` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 33 | 66 | **type** `public class Notice` | One thing the city needs told about, and whether anybody has looked at it. |
| 48 | 1 | `Notice()` | Gson needs it. |
| 50 | 6 | `Notice(String key, String title, List<String> body, int month)` |  |
| 57 | 1 | `public String getKey()` |  |
| 58 | 1 | `public String getTitle()` |  |
| 61 | 4 | `public List<String> getBody()` | Never null, even on a notice from a save written before the field existed. |
| 66 | 1 | `public int getRaised()` |  |
| 67 | 1 | `public int getRead()` |  |
| 68 | 1 | `public int getResolved()` |  |
| 70 | 1 | `public boolean isRead()` |  |
| 71 | 1 | `public boolean isResolved()` |  |
| 74 | 1 | `public boolean isUrgent()` | Unread AND still true: the only state that interrupts the player. |
| 76 | 4 | `void refresh(String title, List<String> body)` |  |
| 81 | 3 | `void markRead(int month)` |  |
| 85 | 3 | `void markResolved(int month)` |  |
| 95 | 3 | `int monthsSinceResolved(int month)` | How old the resolution is, for the two-year cull. |

