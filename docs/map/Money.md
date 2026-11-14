# Money.java - 411 lines · 36 methods · 2 constants · interface

`ham/citybuildersim/ui/Money.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Every figure the interface prints as money, in one place.
> 
> These were instance methods of UserInterface that read nothing but their
> arguments and one number formatter; they are static here so that every
> screen - the shell and the screen classes split out of it - can call
> money(x) as it always did, through an import static, without holding a
> reference to the window. The model counts in THOUSANDS; toDollars() is the
> one conversion and everything else calls it.

**Uses:** [Currency](Currency.md) (11), [PriceLevel](PriceLevel.md) (1), [LandManager](LandManager.md) (1), [Formats](Formats.md) (1)

**Used by (32):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LandClassCheck](LandClassCheck.md), [LandScreen](LandScreen.md), [MoneySignCheck](MoneySignCheck.md), [PartnersPage](PartnersPage.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [PriceLevelCheck](PriceLevelCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [ReplayGraphLayer](ReplayGraphLayer.md), [RunWorksCheck](RunWorksCheck.md), [ServicesScreen](ServicesScreen.md), [StatementView](StatementView.md), [SummaryScreen](SummaryScreen.md), [TradeBandCheck](TradeBandCheck.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [VaultTable](VaultTable.md), [WorldData](WorldData.md), [WorldDataCheck](WorldDataCheck.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 22 | THE ONE PLACE MODEL MONEY BECOMES A STRING. |
| 55 | NO NEGATIVE ZERO (0.7.20). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Money.formatter` | `withoutNegativeZero(NumberFormat.getNumberInstance(Locale.CANADA))` | Thousands separators, Canadian style; every figure below goes through it. |
| 224 | `Money.NOTHING` | `.5 * PriceLevel.FACTOR` | A money figure under this either way, thousands, is too small for a line or a word of its own on a screen: half a thousand of today's (2025's) dollars, priced at the founding's level (times PriceLevel.FACTOR, as every... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 392 | **type** `public final class Money` | Every figure the interface prints as money, in one place. |

### THE ONE PLACE MODEL MONEY BECOMES A STRING. (lines 22-54)

| line | len | member | says |
|---:|---:|---|---|
| 50 | 4 | `static { ... }` |  |

### NO NEGATIVE ZERO (0.7.20). (lines 55-411)

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
| 192 | 17 | `public static String tightMoney(double value, boolean compact)` | Two thresholds, because the two views of the tier table hold numbers three orders apart. |
| 211 | 1 | `private static double tenths(double v)` | A figure to one place, as "%.1f" prints it: where a unit's figure would read a thousand. |
| 214 | 3 | `public static String money(double thousands)` | City money. |
| 219 | 3 | `public static String moneyFull(double thousands)` | The same, with every digit rather than an abbreviation. |
| 227 | 1 | `public static boolean nothing(double thousands)` | Whether a money figure, thousands, is under NOTHING either way, so a screen says nothing of it (EP2: the literal half-thousand the screens held hid a 1900 city's lines). |
| 230 | 1 | `public static boolean something(double thousands)` | Whether a money figure, thousands, is NOTHING or more either way, so a screen gives it a line or a word: nothing()'s other side, and false for a figure that is not a number, as the literal test was (EP2). |
| 247 | 3 | `public static String marked(String prefix, String amount)` | Somebody else's money, marked as such: "US$" in place of the city's sign. |
| 252 | 1 | `public static boolean isZero(String shown)` | Whether a money figure as the screens show it is the city's zero: what money(0) writes, "$0" with the "$" before (FM1: not a test for the character). |
| 255 | 1 | `public static String write(String number)` | An already formatted number with the city's sign put around it (Currency.write()): the one door a screen's own figure takes to be money. |
| 258 | 1 | `public static String ours()` | The city's qualified sign, for prose that names the city's money where a foreign one is in sight: "D$" for the Danzik dollar, "kr" for a krona. |
| 261 | 1 | `public static String sign()` | The city's bare sign, "$" unless it chose another (FM1): for a legend or a field's caption, never for a figure - write() places the sign. |
| 271 | 5 | `public static String signedTight(double thousands, boolean negate)` | signed(), in the k/M column a city-scale statement wants. |
| 284 | 5 | `public static String signed(double thousands, boolean negate)` | A movement, signed - and a zero movement is written without one. |
| 291 | 3 | `public static String usd(double thousands)` | Foreign money, abbreviated. |
| 296 | 3 | `public static String usdFull(double thousands)` | ...and with every digit. |
| 308 | 10 | `public static String unitPrice(double thousands)` | A price small enough that the cents matter - a unit on the shelf, an hourly rate, anything a lopped currency has just made tiny. |
| 325 | 3 | `public static String groundPrice(double thousandsPerSqFt)` | A ground price as the player reads it since 0.7.68: a square metre (LandManager.perM2()) of what the model keeps a square foot in thousands, as unitPrice() writes it - "$20,882", "$7.53". |
| 344 | 3 | `public static String fxRate(double rate)` | An exchange rate - local dollars per US dollar, a ratio and not money, so it never goes through toDollars(). |
| 349 | 6 | `public static String shortNumber(double value)` | 12.4k rather than 12,400 - the panel is narrow and these are two to a row. |
| 365 | 7 | `public static String power(double kW)` | Power, from the model's kilowatts (0.7.28): "900 kW", "38.9 MW", "199 MW", "1.18 GW" - three figures at most, the unit scaled to fit. |
| 374 | 6 | `private static String scaled(double v)` | A figure of one to three digits before its unit: 8.1, 38.9, 199, 1.18 - its three significant figures, under 10 to two places only when they say something. |
| 390 | 5 | `public static String coverMonths(double months)` | Months of import cover in words a player can act on (Trade's since 0.7.35, every screen's since 0.7.38): "over 10 years" past ten years (a city with D$100M in the vault and D$36k a month of imports has 2,777 months, w... |
| 404 | 3 | `public static String rate1(double rate)` | A yearly rate to one place, with a true minus: "2.2%", "−0.5%". |
| 409 | 1 | `public static String trust(double credibility)` | How far the city believes the bank, a whole per cent (Expectations.getCredibility()): "82%" - floored, so a trust under CityNeeds.TRUST_RED never reads as half. |

