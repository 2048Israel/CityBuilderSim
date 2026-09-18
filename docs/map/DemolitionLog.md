# DemolitionLog.java - 136 lines · 10 methods · 2 constants · model

`ham/citybuildersim/DemolitionLog.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> What the city has lost, and when.
> 
> Businesses can scrap capacity they cannot afford to hold, and until now that
> happened in complete silence: the building count on the overview simply went
> down between one month and the next, with nothing anywhere saying which
> building went or why. A city that shrinks without telling you is a city you
> cannot govern.
> 
> Entries hang around for KEEP_MONTHS rather than one turn, because the whole
> point is that the player may have been fast-forwarding and needs to see what
> happened while they were not watching. Each one carries the month it happened
> so the panel can say how long ago it was.

**Used by (6):** [BuildLog](BuildLog.md), [CalendarCheck](CalendarCheck.md), [DataSave](DataSave.md), [Game](Game.md), [HouseholdCheck](HouseholdCheck.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 24 | `DemolitionLog.KEEP_MONTHS` | `24` | How long a demolition stays on the panel. |
| 27 | `DemolitionLog.MAX_ENTRIES` | `40` | Hard cap, so a city demolishing constantly cannot grow this without limit. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 31 | `public final String building` |  |
| 32 | `public final int quantity` |  |
| 33 | `public final String sector` |  |
| 34 | `public final int month` |  |
| 37 | `public final double proceeds` | What the city paid for the plot, or 0 if it was abandoned. |
| 65 | `private final List<Entry> entries` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 116 | **type** `public class DemolitionLog` | What the city has lost, and when. |
| 29 | 35 | **type** `public static class Entry` |  |
| 39 | 7 | `Entry(String building, int quantity, String sector, int month, double proceeds)` _(in DemolitionLog.Entry)_ |  |
| 47 | 3 | `public boolean wasPaidFor()` _(in DemolitionLog.Entry)_ |  |
| 52 | 3 | `public int monthsAgo(int currentMonth)` _(in DemolitionLog.Entry)_ | How long ago, in months. |
| 57 | 6 | `public String when(int currentMonth)` _(in DemolitionLog.Entry)_ | "this month", "last month", "3 months ago". |
| 67 | 13 | `public void record(String building, int quantity, String sector, int month, double proceeds)` |  |
| 88 | 13 | `public List<Entry> recent(int currentMonth)` | What to show right now: everything within KEEP_MONTHS, newest first. |
| 103 | 5 | `public List<Entry> all()` | Every entry ever kept, newest first. |
| 118 | 10 | `public void restore(List<Entry> saved)` | Puts a saved log back, in the order it was written. |
| 129 | 3 | `public int size()` |  |
| 133 | 3 | `public void clear()` |  |

