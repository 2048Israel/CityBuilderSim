# GovernmentPlayerCheck.java - 241 lines · 15 methods · 1 constants · harnesses

`ham/citybuildersim/GovernmentPlayerCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> The test player's annual wealth policy: both directions, boundaries, caps, the first eligible January and the actual skip/reload path.
> 
> WHY (GV1/GV2). The study's December modulo missed January's newly opened levers (R0's dates; since OP1 every lever is open from the
> founding, and the player's first move is month 13's), and a driver that only reviewed its skip's endpoint
> could cross years without ever reviewing policy. The fixtures cause each balance directly; the last section plays through two actual
> Januaries, saving and loading between them, and counts how often taxes really move. Every city and its slot 10 live in scratch data.

**Uses:** [GovernmentPlayer](GovernmentPlayer.md) (44), [GovernmentView](GovernmentView.md) (26), [Game](Game.md) (18), [TaxPolicy](TaxPolicy.md) (13), [LongPlaytest](LongPlaytest.md) (8), [PolicyDates](PolicyDates.md) (7), [GameFiles](GameFiles.md) (4), [TreasuryFund](TreasuryFund.md) (3), [HistorySave](HistorySave.md) (2), [CityCalendar](CityCalendar.md) (2)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `GovernmentPlayerCheck.out` | `System.out` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 227 | **type** `public final class GovernmentPlayerCheck` | The test player's annual wealth policy: both directions, boundaries, caps, the first eligible January and the actual skip/reload path. |
| 19 | 4 | `private static void check(String label, boolean ok)` |  |
| 24 | 5 | `private static void same(String label, double actual, double expected)` |  |
| 30 | 3 | `private static Game city(String label)` |  |
| 35 | 5 | `private static void at(Game g, int month) throws ReflectiveOperationException` | A fixture's date, set on the city as its month (the policy hears no month since OP1). |
| 41 | 7 | `private static void history(Game g, int count, double revenue) throws ReflectiveOperationException` |  |
| 49 | 5 | `private static void wealth(Game g, double years)` |  |
| 55 | 8 | `private static void rates(Game g, double profit, double sales, double pension, double ei, int withdrawal)` |  |
| 64 | 8 | `private static void levers(Game g, double profit, double sales, double pension, double ei, int withdrawal)` |  |
| 73 | 41 | `private static void directionsAndCaps() throws Exception` |  |
| 115 | 35 | `private static void calendarAndBoundaries() throws Exception` |  |
| 151 | 19 | `private static void openFromFounding() throws Exception` |  |
| 171 | 10 | `private static double rate(Game g, PolicyDates.Lever lever)` |  |
| 182 | 12 | `private static void fixtureRunnerKeepsPolicy()` |  |
| 195 | 29 | `private static void skipsAndReload() throws Exception` |  |
| 225 | 16 | `public static void main(String[] args) throws Exception` |  |

