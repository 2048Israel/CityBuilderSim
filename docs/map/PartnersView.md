# PartnersView.java - 230 lines · 10 methods · 0 constants · model

`ham/citybuildersim/PartnersView.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

> The Partners board's and a nation's page's figures (WN6; docs/studies/spec-nations.md 8, boards 2 and 3, D29): the table of who the city trades with, by
> nation, for the month or the year so far, and one nation's full page. A pure model of what a screen draws, built from the game's public getters alone
> and read from the screen's thread (the snapshot it starts from is immutable; the rest are the figures the map card already reads).
> <p>
> WHY. CLAUDE.md, "A screen section": every model figure through a public getter, never recomputed in the screen. The Partners board was to rank nations,
> name each one's money, its policy rate, its distance and its land, and foot to the month; a screen that did that itself would have its own idea of the
> year, of the money, of what the rank counts. The page is the map card's: its rows ARE NationsMap.figureRows (the same words, the same ranks by
> NationsMap.YearFigures), and the ranks are asserted to be the card's (PartnersCheck).
> <p>
> UNITS. Money in the trade is thousands of the city's money (the model counts money in thousands); tonnes are tonnes; distances kilometres along the
> great circle from the city's site to the nation's node; a rate is the nation's money a US$ (units a US$) and D$ a unit is the D$ a US$ over it; a policy
> rate is a fraction a year; land is square kilometres and US$ (the tally keeps thousands; the page whole US$). Nations are numbered from 1.

**Uses:** [PartnerTrade](PartnerTrade.md) (9), [Nations](Nations.md) (9), [NationsMap](NationsMap.md) (5), [Game](Game.md) (4), [WorldMarket](WorldMarket.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [WorldData](WorldData.md) (2), [WorldMoneys](WorldMoneys.md) (1), [PolicyRates](PolicyRates.md) (1), [LandFactors](LandFactors.md) (1), [LandSellers](LandSellers.md) (1), [EarthProj](EarthProj.md) (1)

**Used by (1):** [PartnersCheck](PartnersCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 26 | `PartnersView.Span.MONTH` |  |
| 26 | `PartnersView.Span.YEAR` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 95 | `final Game g` |  |
| 96 | `final Nations nations` |  |
| 97 | `final WorldMarket market` |  |
| 98 | `final int year` |  |
| 99 | `final double dollarsAUsd, siteLat, siteLon` |  |
| 100 | `final WorldMoneys moneys` |  |
| 101 | `final PolicyRates policy` |  |
| 102 | `final LandFactors land` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 210 | **type** `public final class PartnersView` | The Partners board's and a nation's page's figures (WN6; docs/studies/spec-nations.md 8, boards 2 and 3, D29): the table of who the city trades with, by nation, for the month or the year so far, and one nation's full ... |
| 23 | 1 | `private PartnersView()` |  |
| 26 | 1 | **type** `public enum Span` | The span of the trade a table is of: the month last tallied, or the calendar year to date. |
| 37 | 6 | **type** `public record Row(int rank, int nation, String name, String iso3, double sold, double bought, double tonnes...` | One nation of the table. |
| 41 | 1 | `public double total()` _(in PartnersView.Row)_ | What the city sold it and bought of it together: what the rows are ranked by. |
| 49 | 2 | **type** `public record Totals(int nationsSoldTo, int nationsBoughtFrom, double sold, double bought, double tonnesOut...` | The table's totals: how many nations the city sold to and how many it bought of, the sums of the rows, and the trade that no partner took (the month's: a good the market has no buyers or sellers of, which the tally ke... |
| 53 | 1 | **type** `public record Table(Span span, int month, int year, List<Row> rows, Totals totals)` | The Partners table for a span: the month's number (the game's) and the calendar year it is in, the rows largest first, and the totals. |
| 59 | 33 | `public static Table table(Game g, Span span)` | The table of who the city trades with, by nation, for a span; null where the city has no market (no world file, or it is not built yet). |
| 94 | 49 | **type** `private static final class Ctx` | What every row of a table (and a page) reads from the game, once. |
| 104 | 23 | `Ctx(Game g, Nations nations, WorldMarket market, PartnerTrade.Snapshot snap)` _(in PartnersView.Ctx)_ |  |
| 129 | 4 | `double unitsPerUsd(Nations.Year y, int nation)` _(in PartnersView.Ctx)_ | The nation's money a US$ this month: the moneys' rate where it is a number above 0, else the file's for the year (the map card's rule). |
| 134 | 3 | `double policyRate(Nations.Year y)` _(in PartnersView.Ctx)_ |  |
| 138 | 4 | `double distanceKm(int nation)` _(in PartnersView.Ctx)_ |  |
| 144 | 4 | `private static String markOf(String money)` |  |
| 149 | 9 | `private static Row row(Ctx c, int nation, int rank, double sold, double bought, double tonnesOut, double tonnesIn)` |  |
| 160 | 1 | **type** `public record YearRow(int year, double valueOut, double valueIn)` | One finished year of the city's trade with a nation: what it sold there and bought of it, thousands of the city's money. |
| 179 | 8 | **type** `public record Page(int nation, String name, String iso3, int year, int count, List<NationsMap.Row> figureRo...` | A nation's full page. |
| 189 | 41 | `public static Page nation(Game g, int nation)` | A nation's full page; null where the city has no market or the number is not a nation's. |

