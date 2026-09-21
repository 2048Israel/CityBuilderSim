# CityCalendar.java - 145 lines · 13 methods · 4 constants · model

`ham/citybuildersim/CityCalendar.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Turns the month counter into a date a person can hold in their head.
> 
> WHY THE COUNTER STAYS
> 
> The city runs on an integer month and everything is keyed to it: saves, the
> demolition and build logs, every bond's maturity, every report. None of that
> changes. This is a presentation layer and nothing else - no state, no
> instances, and deliberately no reverse function, because nothing in the game
> should ever be deriving a month from a date string.
> 
> THE EPOCH
> 
> Month 1 is January 2000, which is where a new game starts (Game sets
> this.month = 1). That makes the arithmetic land where a player expects:
> month 121 is exactly January 2010, ten years on.
> 
> Month 0 and negatives are not real game states, but they are reachable from a
> corrupt save and from any month-difference someone hands in by mistake, so
> they floor at the epoch rather than counting backwards into 1999. A date is
> cosmetic; crashing the status bar over one is not.

**Used by (10):** [CalendarCheck](CalendarCheck.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [PeopleScreen](PeopleScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 45 | DAYS, WHICH THE SIMULATION DOES NOT HAVE AND THE CLOCK NEEDS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 28 | `CityCalendar.EPOCH_YEAR` | `2000` | The year month 1 falls in. |
| 30 | `CityCalendar.MONTHS` | `{ "January", "February", "March", "April", "May", "June", "July", "August", "...` |  |
| 35 | `CityCalendar.SHORT_MONTHS` | `{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov"...` |  |
| 41 | `CityCalendar.DAYS` | `{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }` | Days in each month of a common year; February is corrected below. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 25 | 121 | **type** `public final class CityCalendar` | Turns the month counter into a date a person can hold in their head. |
| 43 | 1 | `private CityCalendar()` |  |

### DAYS, WHICH THE SIMULATION DOES NOT HAVE AND THE CLOCK NEEDS (lines 45-145)

| line | len | member | says |
|---:|---:|---|---|
| 66 | 3 | `public static boolean isLeapYear(int year)` | Whether a calendar year is a leap year, by the Gregorian rule. |
| 71 | 5 | `public static int daysIn(int gameMonth)` | How many days this game month actually has. |
| 83 | 5 | `public static int dayOf(int gameMonth, double progress)` | The day of the month a given share of the way through it. |
| 90 | 3 | `public static String formatDay(int gameMonth, double progress)` | "14 March 2031" - the date bar's line while the clock is running. |
| 95 | 3 | `private static int elapsed(int gameMonth)` | Months since the epoch, floored at zero. |
| 99 | 3 | `public static int yearOf(int gameMonth)` |  |
| 104 | 3 | `public static int monthOfYear(int gameMonth)` | 1-12, the way a person counts months rather than the way an array does. |
| 108 | 3 | `public static String monthName(int gameMonth)` |  |
| 112 | 3 | `public static String shortMonthName(int gameMonth)` |  |
| 117 | 3 | `public static String format(int gameMonth)` | "March 2014" - the status bar. |
| 122 | 3 | `public static String formatShort(int gameMonth)` | "Mar 2014" - tables and strips, where the long form does not fit. |
| 133 | 12 | `public static String until(int fromMonth, int targetMonth)` | "in 3 months", "next month", "this month", "overdue". |

