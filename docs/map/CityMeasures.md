# CityMeasures.java - 389 lines · 19 methods · 6 constants · model

`ham/citybuildersim/CityMeasures.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The city's figure for each of the Rankings' 70 measures (spec-leaderboards 6,
> "Where the game's side comes from"), worked out of its HistorySave and the
> few figures a history does not keep.
> 
> WHY. The board ranks the active city beside the real world on measures the
> real world publishes in its own units (a rate per 1,000 people, a share of
> GDP, a year's wage in whole dollars), and the game keeps its figures as the
> model counts them: money in thousands, a month at a time, a rate as a
> fraction, a share of GDP not at all. Left to the screen, each of those
> conversions would be written where it is drawn and drift from the next. Here
> it is one pure function of a history, so the board reads the live game
> (Rankings.City.of(Game)) and LB3 reads a saved city's history file with the
> same code, and nothing in here reads Game.
> 
> UNITS (the model counts money in THOUSANDS, ui/Money). Every
> money figure this makes is in WHOLE units of the city's money, and a flow is a
> year of it. The series that are thousands, each multiplied by THOUSANDS where it
> becomes a figure: gdp, consumption, totalWage and minimumWage (and so the average
> wage and the minimum wage), and the ones that are only ever a share of gdp
> (investment, government, netExports, exportsAbroad, importsAbroad,
> currentAccount, m2, debt, surplus, the five taxes, fundValue, pensionBill,
> healthBill, bankLent), where numerator and denominator are both thousands of
> the city's money and need no factor; rentPrice, thousands against the average
> wage's thousands, likewise. reservesUsd is thousands of US dollars: it meets
> importsAbroad (thousands of the city's money, which a reform scales and the
> rate is the city's money per US dollar) after dividing each month's imports by
> that month's fxRate. None of the "abroad" series is a US dollar series but
> reservesUsd and foreignDebtUsd (HistorySave, THE EDGE).
> 
> THE RULES (one year, F(s) and friends). F(s) is series s summed over the last 12
> months, scaled by 12 over n when the city has lived n < 12 months (n at least 1);
> L(s) its last month; back(s, k) its value k months before. A series is read through
> HistorySave.alignedTail, a month at a time; the months a history no longer holds
> monthly (HistorySave.foldOldYears: the years past MONTHLY_KEPT) are never read as
> months, so a measure that needs more months than the history holds as months is
> absent, and so is one whose series has a month "not counting" (NaN) inside its
> window. A measure the city has no figure for is absent from the map, never 0.
> 
> ABSENT IN LB2, each for its reason: participation (the workforce is every adult
> of working age, the students and the prisoners included, so there is no inactivity
> to rate); tertiary (students counts the people studying, and the bands carry no
> age group to put it over, which is what a gross enrolment is); yield_10y (the city
> has one borrowing rate and no ten-year yield); debt_foreign (lane A is rebuilding
> the city's foreign-money debt: stay off it); interest_revenue (the interest paid is
> not a series of its own; the debt's rate times its principal would only estimate
> it); credit_rating (no agency); bad_loans (the bank keeps write-offs and
> provisions, flows, and no share of its loans past due); stock_market (a share is
> priced per founding share, and no market capitalisation is kept); hh_debt (only the
> student loans are kept, with no household income to put them over); aged_65 (the
> bands break at 70, not 65); sharing (no series says how many households share a
> home); price_income (no price of a home is kept, only the month's rent);
> electricity_pp, water_pp (energyRatio and waterRatio are delivered over drawn, a
> ratio, not an amount); renewable_share, cars_per_1000, transit, oil (not kept).
> ADDED, a history series after all: police, the coverage (staffed officers over
> Crime.FULL_OFFICERS_PER_100K) times that full figure, officers per 100,000; the
> coverage is capped at 1, so a city above full coverage reads the full figure.
> 
> WHERE A RULE OF THE BRIEF DID NOT FIT WHAT THE SERIES IS, and the book's own
> definition was taken: unemployment is YearBook.unemploymentTail, the people out
> of work over the LABOUR force (workforce less students and prisoners), not over
> ... (15 more lines in the source)

**Uses:** [AgeBand](AgeBand.md) (12), [HistorySave](HistorySave.md) (3), [YearBook](YearBook.md) (2), [Crime](Crime.md) (1)

**Used by (3):** [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `CityMeasures.THOUSANDS` | `1_000` | The model counts money in thousands (ui/Money): this turns a money series' figure into whole units of the city's money. |
| 91 | `CityMeasures.YEAR` | `12` | The months in a year: the window F(s) sums, and the distance inflation and population growth look back. |
| 94 | `CityMeasures.GROWTH_MONTHS` | `2 * YEAR` | Months the real growth of a year over the year before needs: two years. |
| 97 | `CityMeasures.DECADE` | `10, DECADE_MONTHS = DECADE * YEAR + YEAR` | Years the long growth compounds over, and the months it needs monthly: that decade, and the year after it (growth_10y's 12 months ending 120 back, and the last 12). |
| 100 | `CityMeasures.LEVERAGE_CEILING` | `10` | The ceiling the bank's leverage ratio is recorded at (HistorySave: clamped at ten, which is also what a city with no book reads); a city at it has no ratio to rank. |
| 264 | `CityMeasures.BAND_DEATHS` | `{ "deathsBabies", "deathsChildren", "deathsTeens", "deathsAdults", "deathsSen...` | The six bands' deaths, in AgeBand order, by the series that carry them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 318 | `final HistorySave h` |  |
| 319 | `final int monthly, window` |  |
| 320 | `final double scale` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 83 | 307 | **type** `public final class CityMeasures` | The city's figure for each of the Rankings' 70 measures (spec-leaderboards 6, "Where the game's side comes from"), worked out of its HistorySave and the few figures a history does not keep. |
| 85 | 1 | `private CityMeasures()` |  |
| 103 | 9 | **type** `public record Extras(double areaKm2, double[] bandPeople, double cashMinimumWage)` | What the history does not keep: read live by Rankings.City.of(Game), from a save's header in LB3. |
| 105 | 1 | `public Extras(double areaKm2, double[] bandPeople)` _(in CityMeasures.Extras)_ | Extras without the wage floor in today's money: the minimum wage is then the history's floor lifted by the price index. |
| 108 | 3 | `double people(AgeBand band)` _(in CityMeasures.Extras)_ | The people of a band, NaN where the extras do not know. |
| 121 | 1 | **type** `public record Figure(double value, double real, String how)` | A figure in the measure's own unit, its value at founding's prices where the measure has a constant series (gdp, gdp_pp, hh_spend_pp; NaN otherwise), and a few words for the drawer on how it is made. |
| 124 | 108 | `public static Map<String, Figure> of(HistorySave h, Extras x)` | Every measure the city has a figure for at the history's last month, in the file's order; a measure it has none for is absent, never 0. |
| 234 | 4 | `private static void put(Map<String, Figure> out, String key, double value, double real, String how)` | Adds a figure when it is a number a city can have: finite, and a real value finite or NaN. |
| 240 | 4 | `private static boolean positive(double...v)` | Whether every number is a finite one over nothing: a growth's two sums need both over 0. |
| 246 | 3 | `private static double share(double part, double gdp)` | 100 x part over the year of output; NaN where output is not over 0, so the measure is absent. |
| 251 | 11 | `private static double reservesMonths(Reading r)` | The vault in months of imports: the last month's reservesUsd over a month's imports in US dollars, the window's months each at that month's rate. |
| 271 | 5 | `private static double bandDeaths(Reading r)` | F of the six bands' deaths added: a year of the city's dead. |
| 278 | 13 | `private static double lifeExpectancy(Reading r, Extras x)` | Life expectancy at birth from the six bands' last year of deaths over the people now in them; NaN under a year lived, or with a band empty. |
| 298 | 13 | `static double lifeTable(double[] hazard)` | A period life table over the six bands (spec-leaderboards 6): hazard[b] is a year's chance of dying, as a rate, in band b. |
| 317 | 72 | **type** `private static final class Reading` | A history read the way the rules above say: the series a month at a time, in the entries that are months. |
| 322 | 6 | `Reading(HistorySave h)` _(in CityMeasures.Reading)_ |  |
| 330 | 4 | `double[] tail(String series, int n)` _(in CityMeasures.Reading)_ | The last n months of a series, or null when the history holds fewer than n as months. |
| 336 | 7 | `double flow(String series)` _(in CityMeasures.Reading)_ | F(s): the series summed over the window and scaled to a year; NaN where a month in it was not counted. |
| 345 | 10 | `double flowDeflated(String series)` _(in CityMeasures.Reading)_ | The window's series each month over that month's price index, summed and scaled to a year: output at founding's prices. |
| 357 | 10 | `double sumReal(String series, int months, int from, int to)` _(in CityMeasures.Reading)_ | The last `months` months of a series each over that month's price index, summed over entries from (inclusive) to to (exclusive), counted from the oldest of them. |
| 369 | 4 | `double last(String series)` _(in CityMeasures.Reading)_ | L(s): the series' last month. |
| 375 | 4 | `double ago(String series, int k)` _(in CityMeasures.Reading)_ | back(s, k): the series k months before the last. |
| 381 | 7 | `double mean(String series)` _(in CityMeasures.Reading)_ | The mean of the window, for a series that is already a rate a year. |

