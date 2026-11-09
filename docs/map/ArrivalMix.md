# ArrivalMix.java - 169 lines · 8 methods · 3 constants · model

`ham/citybuildersim/ArrivalMix.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> What the world's movers hold by year: the skill mix a month's arrivals are drawn from, in a city founded the 1900 way.
> 
> WHY A MIX THAT FOLLOWS THE YEAR (R0d slice 2, 2026-11-06; docs/studies/research-1900.md 5.4 item 6, "The world's mix of skills follows
> the year ... arrivals' diplomas come from that mix", and 12.1, "Skill mix by year": keep how many arrive apart from what they hold).
> Migration.composeArrivals() makes the diploma the ordinary arrival ("universal high school", WageBand.arrivalCeiling()): the diploma band's
> weight is 1 whatever the city pays, no-diploma arrivals are bought on a premium up to .12 of it, and the graduate bands are bought up to .35
> and .12. That is a developed economy's mix. A 1901 mover was a labourer or a farm hand far more often than a diploma-holder (the 1913
> immigrant tables: 72.8 in a hundred farmers, labourers and miners to 23.1 in a trade), and a university graduate was rarer still (1.2
> students per 1,000 in 1901 against 4.7 in 1950). So a city founded the 1900 way draws its arrivals from the year's mix, and one founded the
> default way (Game.foundedUnfitted() false) never reads this class: its Migration holds no mix and runs the code it always ran.
> 
> WHAT IS READ AND WHAT IS INTERPOLATED. A Knot is the shares of a year's working arrivals by skill, at January of its year, summing to one;
> the shares are what is interpolated, linearly by month from one January to the next. A Point is what composeArrivals() reads, derived from
> shares and stating them relative to the diploma band's 1, the way WageBand's ceilings do: ordinaryNone, the arrivals with no diploma who come
> at the going rate, unconditionally, per diploma arrival (today's model has none); college and university, those bands' ceilings, bought
> exactly as today, on the premium (Migration.reach()). The no-diploma band keeps today's bought part on top of ordinaryNone. Before the first
> knot's January the first knot's mix applies; at and after the last knot's January the mix is TODAY, the constants themselves, so a 1900 city
> grows into the model every other city is on. The licensed professions keep today's licence shares (EducationType.worldLicenceShare()) in every
> year, on the year's university figure: the study gives no earlier share of graduates holding a licence (a call the batch session recorded).
> 
> HOW MANY ARRIVE is not here: Migration's volume (the jobs, the homes, the pulls) is the same in every city. This class only says who comes.
> 
> Sources (Statistics Canada and the Crown, all commercial use allowed):
>   [CYB1913]   Canada Year Book 1913, Table 47 (immigrant arrivals by occupation, fiscal year 1912-13),
>               https://publications.gc.ca/collections/collection_2017/statcan/11-202/CS11-202-1913-eng.pdf
>               (Crown copyright expired; Statistics Canada Open Licence)
>   [HSC-A351]  Historical Statistics of Canada, 11-516-X, series A351-368 (immigrant workers by intended occupation),
>               https://www150.statcan.gc.ca/n1/pub/11-516-x/sectiona/A351_368-eng.csv (Statistics Canada Open Licence)
>   [V1951]     Ninth Census of Canada 1951, Vol. I, Table 27 (years of schooling),
>               https://publications.gc.ca/collections/collection_2017/statcan/CS98-1951-2.pdf (Crown copyright expired)
>   [B1]        Census and Statistics Office, Bulletin I, "Wage-earners by occupations" (the 1901 census, 1907),
>               https://publications.gc.ca/collections/collection_2017/statcan/CS98-1901P-1.pdf (Crown copyright expired; WageLadder's [B1])
>   [V1911]     Census of Canada 1911, Vol. VI, occupations (1901 re-classified),
>               https://publications.gc.ca/collections/collection_2016/statcan/CS98-1911-6.pdf (Crown copyright expired)

**Uses:** [WageBand](WageBand.md) (11), [CityCalendar](CityCalendar.md) (1)

**Used by (5):** [ArrivalMixCheck](ArrivalMixCheck.md), [BasketYearCheck](BasketYearCheck.md), [Consumption](Consumption.md), [Game](Game.md), [Migration](Migration.md)

## Sections

| line | section |
|---:|---|
| 72 | TODAY'S MIX, FROM WageBand'S OWN CEILINGS |
| 83 | THE KNOTS: each figure is one line to change |
| 115 | THE LOOK-UP |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `ArrivalMix.TODAY_SUM` | `WageBand.DIPLOMA.arrivalCeiling() + WageBand.COLLEGE.arrivalCeiling() + WageB...` | The sum of the ceilings the diploma and graduate bands have today: 1 + .35 + .12 = 1.47. |
| 81 | `ArrivalMix.TODAY` | `new Point(0, WageBand.COLLEGE.arrivalCeiling(), WageBand.UNIVERSITY.arrivalCe...` | Today's mix: no ordinary arrivals without a diploma, the graduate bands at WageBand's ceilings. |
| 89 | `ArrivalMix.KNOTS` | `List.of(new Knot(1901,.73066,.23184,.03,.0075, "CYB1913", true, "1913's: the ...` | The year's mix, in year order. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 129 | **type** `public final class ArrivalMix` | What the world's movers hold by year: the skill mix a month's arrivals are drawn from, in a city founded the 1900 way. |
| 43 | 1 | `private ArrivalMix()` |  |
| 49 | 1 | **type** `public record Knot(int year, double none, double diploma, double college, double university, String source,...` | One year's mix of the working arrivals, at January of that year: each skill's share of them (none, diploma, college, university), summing to one, with the key of where it was read, whether it is an estimate, and a not... |
| 55 | 16 | **type** `public record Point(double ordinaryNone, double college, double university)` | The three figures composeArrivals() reads, relative to the diploma band's 1: the no-diploma arrivals who come at the going rate per diploma arrival, and the college and university bands' ceilings, bought on the premium. |
| 58 | 3 | `public double ordinary(WageBand band)` _(in ArrivalMix.Point)_ | The no-diploma arrivals who come at the going rate for this band, per diploma arrival: ordinaryNone for the unskilled band, nothing for any other. |
| 63 | 7 | `public double ceiling(WageBand band)` _(in ArrivalMix.Point)_ | The ceiling of this band's bought arrivals, relative to the diploma band's 1: the mix's own for the graduate bands, WageBand's for the others. |

### TODAY'S MIX, FROM WageBand'S OWN CEILINGS (lines 72-82)

### THE KNOTS: each figure is one line to change (lines 83-114)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 8 | `static { ... }` |  |

### THE LOOK-UP (lines 115-169)

| line | len | member | says |
|---:|---:|---|---|
| 121 | 3 | `public static int januaryOf(int year)` | The game month of January of a year (month 1 is January 1900, CityCalendar): CityCalendar has no reverse function, deliberately, and this is the one place a year is turned back into a month; ArrivalMixCheck asserts it... |
| 130 | 4 | `public static Point derive(double none, double diploma, double college, double university)` | The three figures a year's shares give, relative to the diploma band's 1. |
| 136 | 3 | `public static Point of(Knot knot)` | The point a knot's own shares give. |
| 145 | 19 | `public static Point at(int month)` | The mix of a game month's arrivals. |
| 166 | 3 | `private static double lerp(double a, double b, double t)` | a at t = 0, b at t = 1, a straight line between. |

