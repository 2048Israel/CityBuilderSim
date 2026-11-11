# GovernmentPlayerCheck.java - 249 lines · 15 methods · 1 constants · harnesses

`ham/citybuildersim/GovernmentPlayerCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The test player's annual wealth policy: both directions, boundaries, caps, historical dates and the actual skip/reload path.
> 
> WHY (GV1/GV2). The study's December modulo missed January's newly opened levers, and a driver that only reviewed its skip's endpoint
> could cross years without ever reviewing policy. The fixtures cause each balance directly; the last section plays through two actual
> Januaries, saving and loading between them, and counts how often taxes really move. Every city and its slot 10 live in scratch data.

**Uses:** [GovernmentPlayer](GovernmentPlayer.md) (45), [GovernmentView](GovernmentView.md) (26), [Game](Game.md) (19), [TaxPolicy](TaxPolicy.md) (13), [PolicyDates](PolicyDates.md) (8), [LongPlaytest](LongPlaytest.md) (8), [GameFiles](GameFiles.md) (4), [TreasuryFund](TreasuryFund.md) (3), [HistorySave](HistorySave.md) (2), [CityCalendar](CityCalendar.md) (2)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 15 | `GovernmentPlayerCheck.out` | `System.out` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 16 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 236 | **type** `public final class GovernmentPlayerCheck` | The test player's annual wealth policy: both directions, boundaries, caps, historical dates and the actual skip/reload path. |
| 18 | 4 | `private static void check(String label, boolean ok)` |  |
| 23 | 5 | `private static void same(String label, double actual, double expected)` |  |
| 29 | 5 | `private static Game city(String label, boolean open)` |  |
| 36 | 6 | `private static void at(Game g, int month) throws ReflectiveOperationException` | Fixture dates also tell the policy, exactly as Game does before running a month's flows. |
| 43 | 7 | `private static void history(Game g, int count, double revenue) throws ReflectiveOperationException` |  |
| 51 | 5 | `private static void wealth(Game g, double years)` |  |
| 57 | 8 | `private static void rates(Game g, double profit, double sales, double pension, double ei, int withdrawal)` |  |
| 66 | 8 | `private static void levers(Game g, double profit, double sales, double pension, double ei, int withdrawal)` |  |
| 75 | 41 | `private static void directionsAndCaps() throws Exception` |  |
| 117 | 35 | `private static void calendarAndBoundaries() throws Exception` |  |
| 153 | 23 | `private static void historicalGates() throws Exception` |  |
| 177 | 10 | `private static double rate(Game g, PolicyDates.Lever lever)` |  |
| 188 | 13 | `private static void fixtureRunnerKeepsPolicy()` |  |
| 202 | 30 | `private static void skipsAndReload() throws Exception` |  |
| 233 | 16 | `public static void main(String[] args) throws Exception` |  |

