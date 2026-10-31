# PriceLevel.java - 81 lines · 5 methods · 5 constants · model

`ham/citybuildersim/PriceLevel.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The price level a city is founded at, 1900's: the US consumer price index of the calendar's first year over its 2025, and the series it is
> read from, the US CPI-U a year from 1900 to 2025 (EP1; docs/studies/spec-1900-start.md 1 and its calls 1 and 3).
> 
> WHY THIS EXISTS. The game opened in January 2000 at today's prices. From 1900 (Jerus, 2026-10-30: "1900 price level") every nominal anchor -
> the world's prices, the wage rungs, the building costs, the opening money - is today's real economy priced at 1900's level, so the game's
> own inflation compounds from there and reaches today's prices about when history did. One factor reaches them all (FACTOR, read where each
> anchor is struck: PayTier, Good, BuildingCatalog and the named literals), and it comes from the same series the world's US dollar follows
> before the world file's own years (WorldData's stand-in century), so the dollar the world inflates on and the factor that set the opening
> prices are one dollar.
> 
> THE SOURCES. 1913-2025: the US Bureau of Labor Statistics, CPI-U, all items, US city average, series CUUR0000SA0, 1982-84 = 100, the
> annual average (US public domain); 2025 is the mean of the 11 months published (October 2025 was not collected). 1900-1912 (est.): Albert
> Rees's cost-of-living index as the BLS compiled it, Historical Statistics of the United States, Colonial Times to 1970 (US Census Bureau,
> 1975), series E 135, 1967 = 100 in whole numbers (US public domain; licences.md, LC1), joined at 1913 (BLS 9.9 over E 135's 29.7: divided
> by 3). The table is docs/studies/1900-start/price-level-us.csv.

**Uses:** [CityCalendar](CityCalendar.md) (1)

**Used by (4):** [InterestAbroadCheck](InterestAbroadCheck.md), [NationsCheck](NationsCheck.md), [WorldData](WorldData.md), [WorldDataCheck](WorldDataCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 23 | `PriceLevel.FIRST_YEAR` | `1900` | The series' first year, the calendar's (CityCalendar.EPOCH_YEAR). |
| 26 | `PriceLevel.LAST_YEAR` | `2025` | The series' last year, the year the game's literals are priced in (WorldMarket.REFERENCE_YEAR). |
| 29 | `PriceLevel.FIRST_PUBLISHED_YEAR` | `1913` | The first year the BLS published the index; the years before it are Rees's index joined at it (est.). |
| 32 | `PriceLevel.US_CPI` | `{ 8.333, 8.333, 8.667, 9.000, 9.000, 9.000, 9.000, 9.333, 9.000, 9.000, 9.333...` | The US CPI-U a year, 1900 to 2025, 1982-84 = 100 (the class's sources). |
| 52 | `PriceLevel.FACTOR` | `factor()` | Every nominal anchor's multiplier, the US CPI of the calendar's first year over its 2025: 8.333 / 321.943 = 0.02588, 1/38.6 (call 1). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 62 | **type** `public final class PriceLevel` | The price level a city is founded at, 1900's: the US consumer price index of the calendar's first year over its 2025, and the series it is read from, the US CPI-U a year from 1900 to 2025 (EP1; docs/studies/spec-1900-... |
| 54 | 1 | `private PriceLevel()` |  |
| 56 | 9 | `private static double factor()` |  |
| 67 | 4 | `public static double usCpi(int year)` | The US CPI-U in a year, FIRST_YEAR to LAST_YEAR, 1982-84 = 100; refused outside them. |
| 73 | 3 | `public static boolean isPublished(int year)` | Whether that year's figure is the BLS's own (true) or Rees's joined at 1913 (false, est.). |
| 78 | 3 | `public static double atFounding(double today)` | A nominal amount priced at today's (2025's) level, priced at the founding's: amount times FACTOR. |

