# Money.java - 384 lines · 30 methods · 1 constants · interface

`ham/citybuildersim/ui/Money.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Every figure the interface prints as money, in one place.
> 
> These were instance methods of UserInterface that read nothing but their
> arguments and one number formatter; they are static here so that every
> screen - the shell and the screen classes split out of it - can call
> money(x) as it always did, through an import static, without holding a
> reference to the window. The model counts in THOUSANDS; toDollars() is the
> one conversion and everything else calls it.

**Uses:** [Currency](Currency.md) (2), [LandManager](LandManager.md) (1), [Formats](Formats.md) (1)

**Used by (9):** [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 22 | THE ONE PLACE MODEL MONEY BECOMES A STRING. |
| 55 | NO NEGATIVE ZERO (0.7.20). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Money.formatter` | `withoutNegativeZero(NumberFormat.getNumberInstance(Locale.CANADA))` | Thousands separators, Canadian style; every figure below goes through it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 365 | **type** `public final class Money` | Every figure the interface prints as money, in one place. |

### THE ONE PLACE MODEL MONEY BECOMES A STRING. (lines 22-54)

| line | len | member | says |
|---:|---:|---|---|
| 50 | 4 | `static { ... }` |  |

### NO NEGATIVE ZERO (0.7.20). (lines 55-384)

| line | len | member | says |
|---:|---:|---|---|
| 69 | 11 | `private static NumberFormat withoutNegativeZero(NumberFormat base)` | The same format, except that a negative which rounds to nothing prints with no sign. |
| 85 | 5 | `public static double unsigned0(double value, int decimals)` | A value for String.format at `decimals` places, with a negative that rounds to nothing there - and -0.0 itself - made a plain zero. |
| 91 | 1 | `public static String pct2(double rate)` |  |
| 104 | 1 | `public static String ratePerYear(double rate)` | A yearly rate: "2.37% a year", "0.008% a year". |
| 107 | 5 | `public static String ratePct(double rate)` | ...without its unit, for a column headed "a year": "2.37%". |
| 114 | 3 | `public static String share1(double share)` | A share or a ratio that is not a yearly rate, to one place, grouped: "14.1%", "1,991.3%". |
| 119 | 5 | `public static String points(double spread)` | A spread between two rates, signed, to the rates' own two places: "+2.06 points", "−0.22 points", "0.00 points". |
| 130 | 4 | `public static String pts(double points)` | TWO DECIMALS SINCE THE LADDER. |
| 145 | 3 | `public static String people(double count)` | A headcount, as a whole number of people. |
| 150 | 3 | `public static String cash(double thousands)` | A wage or a price the model holds in thousands, in the dollars it is. |
| 165 | 1 | `public static double toDollars(double thousands)` | Thousands into dollars, for the one screen that has to talk about a family. |
| 179 | 1 | `public static String tightMoney(double value)` | Money at a width that cannot overflow its column. |
| 192 | 16 | `public static String tightMoney(double value, boolean compact)` | Two thresholds, because the two views of the tier table hold numbers three orders apart. |
| 210 | 1 | `private static double tenths(double v)` | A figure to one place, as "%.1f" prints it: where a unit's figure would read a thousand. |
| 213 | 3 | `public static String money(double thousands)` | City money. |
| 218 | 3 | `public static String moneyFull(double thousands)` | The same, with every digit rather than an abbreviation. |
| 234 | 5 | `public static String marked(String prefix, String amount)` | Somebody else's money, marked as such: "US$" in place of the "$". |
| 247 | 5 | `public static String signedTight(double thousands, boolean negate)` | signed(), in the k/M column a city-scale statement wants. |
| 259 | 5 | `public static String signed(double thousands, boolean negate)` | A movement, signed - and a zero movement is written without one. |
| 266 | 3 | `public static String usd(double thousands)` | Foreign money, abbreviated. |
| 271 | 3 | `public static String usdFull(double thousands)` | ...and with every digit. |
| 283 | 8 | `public static String unitPrice(double thousands)` | A price small enough that the cents matter - a unit on the shelf, an hourly rate, anything a lopped currency has just made tiny. |
| 298 | 3 | `public static String groundPrice(double thousandsPerSqFt)` | A ground price as the player reads it since 0.7.68: a square metre (LandManager.perM2()) of what the model keeps a square foot in thousands, as unitPrice() writes it - "$20,882", "$7.53". |
| 317 | 3 | `public static String fxRate(double rate)` | An exchange rate - local dollars per US dollar, a ratio and not money, so it never goes through toDollars(). |
| 322 | 6 | `public static String shortNumber(double value)` | 12.4k rather than 12,400 - the panel is narrow and these are two to a row. |
| 338 | 7 | `public static String power(double kW)` | Power, from the model's kilowatts (0.7.28): "900 kW", "38.9 MW", "199 MW", "1.18 GW" - three figures at most, the unit scaled to fit. |
| 347 | 6 | `private static String scaled(double v)` | A figure of one to three digits before its unit: 8.1, 38.9, 199, 1.18 - its three significant figures, under 10 to two places only when they say something. |
| 363 | 5 | `public static String coverMonths(double months)` | Months of import cover in words a player can act on (Trade's since 0.7.35, every screen's since 0.7.38): "over 10 years" past ten years (a city with D$100M in the vault and D$36k a month of imports has 2,777 months, w... |
| 377 | 3 | `public static String rate1(double rate)` | A yearly rate to one place, with a true minus: "2.2%", "−0.5%". |
| 382 | 1 | `public static String trust(double credibility)` | How far the city believes the bank, a whole per cent (Expectations.getCredibility()): "82%" - floored, so a trust under CityNeeds.TRUST_RED never reads as half. |

