# Notice.java - 119 lines · 17 methods · 0 constants · model

`ham/citybuildersim/Notice.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

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

**Used by (13):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [DataSave](DataSave.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [KeptCheck](KeptCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [PlaytestFixCheck](PlaytestFixCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [UserInterface](UserInterface.md)

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
| 33 | 87 | **type** `public class Notice implements Cloneable` | One thing the city needs told about, and whether anybody has looked at it. |
| 48 | 1 | `Notice()` | Gson needs it. |
| 50 | 6 | `Notice(String key, String title, List<String> body, int month)` |  |
| 63 | 9 | `Notice copy()` | A copy that shares nothing with this notice (UP1, 2026-10-22): the autosave is written on another thread while markRead(), markResolved() and refresh() go on changing the live ones. |
| 73 | 1 | `public String getKey()` |  |
| 74 | 1 | `public String getTitle()` |  |
| 77 | 4 | `public List<String> getBody()` | Never null, even on a notice from a save written before the field existed. |
| 82 | 1 | `public int getRaised()` |  |
| 83 | 1 | `public int getRead()` |  |
| 84 | 1 | `public int getResolved()` |  |
| 86 | 1 | `public boolean isRead()` |  |
| 87 | 1 | `public boolean isResolved()` |  |
| 90 | 1 | `public boolean isUrgent()` | Unread AND still true: the only state that interrupts the player. |
| 92 | 4 | `void refresh(String title, List<String> body)` |  |
| 97 | 3 | `void markRead(int month)` |  |
| 101 | 3 | `void markResolved(int month)` |  |
| 106 | 3 | `void reopen()` | Standing again, as it was raised (PT1-M2: the wait on ground back within its year, Inbox.takeMonth()). |
| 116 | 3 | `int monthsSinceResolved(int month)` | How old the resolution is, for the two-year cull. |

