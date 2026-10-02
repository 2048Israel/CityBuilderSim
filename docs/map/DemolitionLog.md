# DemolitionLog.java - 142 lines · 10 methods · 2 constants · model

`ham/citybuildersim/DemolitionLog.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
> 
> SINCE 0.7.22 THE CITY'S OWN TOO. The buildings the player orders
> demolished, and the ones it buys out to pull down, are logged the month
> they close (Game.closeDemolished()), as "City" or "City, bought from" the
> sector, with what the city paid for a bought building's ground as the
> proceeds.

**Used by (7):** [BuildLog](BuildLog.md), [CalendarCheck](CalendarCheck.md), [DataSave](DataSave.md), [Game](Game.md), [HouseholdCheck](HouseholdCheck.md), [MortgageCheck](MortgageCheck.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `DemolitionLog.KEEP_MONTHS` | `24` | How long a demolition stays on the panel. |
| 33 | `DemolitionLog.MAX_ENTRIES` | `40` | Hard cap, so a city demolishing constantly cannot grow this without limit. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `public final String building` |  |
| 38 | `public final int quantity` |  |
| 39 | `public final String sector` |  |
| 40 | `public final int month` |  |
| 43 | `public final double proceeds` | What the city paid for the plot - a business's bought back, or since 0.7.22 a bought-out building's ground - or 0: abandoned, or the city's own. |
| 71 | `private final List<Entry> entries` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 27 | 116 | **type** `public class DemolitionLog` | What the city has lost, and when. |
| 35 | 35 | **type** `public static class Entry` |  |
| 45 | 7 | `Entry(String building, int quantity, String sector, int month, double proceeds)` _(in DemolitionLog.Entry)_ |  |
| 53 | 3 | `public boolean wasPaidFor()` _(in DemolitionLog.Entry)_ |  |
| 58 | 3 | `public int monthsAgo(int currentMonth)` _(in DemolitionLog.Entry)_ | How long ago, in months. |
| 63 | 6 | `public String when(int currentMonth)` _(in DemolitionLog.Entry)_ | "this month", "last month", "3 months ago". |
| 73 | 13 | `public void record(String building, int quantity, String sector, int month, double proceeds)` |  |
| 94 | 13 | `public List<Entry> recent(int currentMonth)` | What to show right now: everything within KEEP_MONTHS, newest first. |
| 109 | 5 | `public List<Entry> all()` | Every entry ever kept, newest first. |
| 124 | 10 | `public void restore(List<Entry> saved)` | Puts a saved log back, in the order it was written. |
| 135 | 3 | `public int size()` |  |
| 139 | 3 | `public void clear()` |  |

