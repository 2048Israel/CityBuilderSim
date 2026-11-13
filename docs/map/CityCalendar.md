# CityCalendar.java - 150 lines · 13 methods · 4 constants · model

`ham/citybuildersim/CityCalendar.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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
> Month 1 is January 1900, which is where a new game starts (Game sets
> this.month = 1; EP1, Jerus, 2026-10-30: the game starts in 1900 with today's
> tech; it was January 2000 until then). That makes the arithmetic land where a
> player expects: month 121 is exactly January 1910, ten years on.
> 
> Month 0 and negatives are not real game states, but they are reachable from a
> corrupt save and from any month-difference someone hands in by mistake, so
> they floor at the epoch rather than counting backwards into 1899. A date is
> cosmetic; crashing the status bar over one is not.
> 
> The calendar is Gregorian all the way: 1900 is not a leap year (divisible by
> 100, not by 400), so February of the first year has 28 days and 2000's 29.

**Used by (61):** [ArrivalMix](ArrivalMix.md), [ArrivalMixCheck](ArrivalMixCheck.md), [AuctionCheck](AuctionCheck.md), [BankScreen](BankScreen.md), [BasketYearCheck](BasketYearCheck.md), [BuildScreen](BuildScreen.md), [CalendarCheck](CalendarCheck.md), [CausesOfDeath](CausesOfDeath.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [Equipment](Equipment.md), [EquipmentCheck](EquipmentCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FoundingScreen](FoundingScreen.md), [FundCheck](FundCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentPlayer](GovernmentPlayer.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [KeptCheck](KeptCheck.md), [LandNationsCheck](LandNationsCheck.md), [Nations](Nations.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyDates](PolicyDates.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PriceLevel](PriceLevel.md), [PriceLevelCheck](PriceLevelCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [ReadPathCheck](ReadPathCheck.md), [RealDollar](RealDollar.md), [RecipeCheck](RecipeCheck.md), [ReplayCheck](ReplayCheck.md), [ReplayMode](ReplayMode.md), [ResearchCatalog](ResearchCatalog.md), [ResearchCheck](ResearchCheck.md), [ResearchScreen](ResearchScreen.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TimeChart](TimeChart.md), [TimeSkipReport](TimeSkipReport.md), [UserInterface](UserInterface.md), [WorldData](WorldData.md), [WorldMoneys](WorldMoneys.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 49 | DAYS, WHICH THE SIMULATION DOES NOT HAVE AND THE CLOCK NEEDS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `CityCalendar.EPOCH_YEAR` | `1900` | The year month 1 falls in. |
| 34 | `CityCalendar.MONTHS` | `{ "January", "February", "March", "April", "May", "June", "July", "August", "...` |  |
| 39 | `CityCalendar.SHORT_MONTHS` | `{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov"...` |  |
| 45 | `CityCalendar.DAYS` | `{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }` | Days in each month of a common year; February is corrected below. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 122 | **type** `public final class CityCalendar` | Turns the month counter into a date a person can hold in their head. |
| 47 | 1 | `private CityCalendar()` |  |

### DAYS, WHICH THE SIMULATION DOES NOT HAVE AND THE CLOCK NEEDS (lines 49-150)

| line | len | member | says |
|---:|---:|---|---|
| 70 | 3 | `public static boolean isLeapYear(int year)` | Whether a calendar year is a leap year, by the Gregorian rule. |
| 75 | 5 | `public static int daysIn(int gameMonth)` | How many days this game month actually has. |
| 87 | 5 | `public static int dayOf(int gameMonth, double progress)` | The day of the month a given share of the way through it. |
| 94 | 3 | `public static String formatDay(int gameMonth, double progress)` | "14 March 2031" - the date in the header's clock (the date bar's line until 0.7.21) while the clock is running. |
| 99 | 3 | `private static int elapsed(int gameMonth)` | Months since the epoch, floored at zero. |
| 103 | 3 | `public static int yearOf(int gameMonth)` |  |
| 108 | 3 | `public static int monthOfYear(int gameMonth)` | 1-12, the way a person counts months rather than the way an array does. |
| 112 | 3 | `public static String monthName(int gameMonth)` |  |
| 116 | 3 | `public static String shortMonthName(int gameMonth)` |  |
| 121 | 3 | `public static String format(int gameMonth)` | "March 2014" - the status bar. |
| 126 | 3 | `public static String formatShort(int gameMonth)` | "Mar 2014" - tables and strips, where the long form does not fit. |
| 138 | 12 | `public static String until(int fromMonth, int targetMonth)` | "in 3 months", "next month", "this month", "overdue". |

