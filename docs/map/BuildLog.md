# BuildLog.java - 149 lines · 9 methods · 2 constants · model

`ham/citybuildersim/BuildLog.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> What the city has gained, and when. The other half of DemolitionLog.
> 
> The construction panel already showed what is going up and what has come
> down, and had nothing at all for what actually LANDED. A player skipping fifty
> months watched sites appear and disappear from the panel with no record of
> which of them finished - the only evidence was a building count that had gone
> up while they were not looking, which is exactly the complaint the demolition
> log was written to answer, pointed the other way.
> 
> Entries keep for the same KEEP_MONTHS as demolitions, on purpose: the two
> lists sit in the same panel and a player comparing them should not have to
> remember that the halves expire on different clocks.
> 
> MERGED BY BUILDING AND MONTH, WHICH DEMOLITIONS DO NOT NEED
> 
> Demolitions come from planRetirement, one sector at a time, a handful a year
> - and since 0.7.22 from the player's own orders, one row an order.
> Completions come from advanceConstruction, which can finish several stacks in
> a single month in a large city and does it every month forever. Recording
> them one row per stack per month would push anything older than a year or two
> out of a 40-entry cap within a few turns and make the panel unreadable long
> before that. So a second batch of Houses in the same month adds to the row
> already there rather than starting another.

**Uses:** [DemolitionLog](DemolitionLog.md) (1)

**Used by (4):** [CalendarCheck](CalendarCheck.md), [DataSave](DataSave.md), [Game](Game.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 35 | `BuildLog.KEEP_MONTHS` | `DemolitionLog.KEEP_MONTHS` | How long a completion stays on the panel. |
| 38 | `BuildLog.MAX_ENTRIES` | `40` | Hard cap, so a city building constantly cannot grow this without limit. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `public final String building` |  |
| 43 | `public final int month` |  |
| 46 | `public int quantity` | Not final: completions of the same building in the same month merge. |
| 68 | `private final List<Entry> entries` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 32 | 118 | **type** `public class BuildLog` | What the city has gained, and when. |
| 40 | 27 | **type** `public static class Entry` |  |
| 48 | 5 | `Entry(String building, int quantity, int month)` _(in BuildLog.Entry)_ |  |
| 55 | 3 | `public int monthsAgo(int currentMonth)` _(in BuildLog.Entry)_ | How long ago, in months. |
| 60 | 6 | `public String when(int currentMonth)` _(in BuildLog.Entry)_ | "this month", "last month", "3 months ago". |
| 70 | 25 | `public void record(String building, int quantity, int month)` |  |
| 102 | 13 | `public List<Entry> recent(int currentMonth)` | What to show right now: everything within KEEP_MONTHS, newest first. |
| 117 | 5 | `public List<Entry> all()` | Every entry ever kept, newest first. |
| 131 | 10 | `public void restore(List<Entry> saved)` | Puts a saved log back, in the order it was written. |
| 142 | 3 | `public int size()` |  |
| 146 | 3 | `public void clear()` |  |

