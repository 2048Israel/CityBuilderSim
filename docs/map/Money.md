# Money.java - 280 lines · 19 methods · 1 constants · interface

`ham/citybuildersim/ui/Money.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> Every figure the interface prints as money, in one place.
> 
> These were instance methods of UserInterface that read nothing but their
> arguments and one number formatter; they are static here so that every
> screen - the shell and the screen classes split out of it - can call
> money(x) as it always did, through an import static, without holding a
> reference to the window. The model counts in THOUSANDS; toDollars() is the
> one conversion and everything else calls it.

**Uses:** [Currency](Currency.md) (2)

**Used by (3):** [BuildScreen](BuildScreen.md), [LandScreen](LandScreen.md), [PolicyScreen](PolicyScreen.md)

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
| 20 | 261 | **type** `public final class Money` | Every figure the interface prints as money, in one place. |

### THE ONE PLACE MODEL MONEY BECOMES A STRING. (lines 22-54)

| line | len | member | says |
|---:|---:|---|---|
| 50 | 4 | `static { ... }` |  |

### NO NEGATIVE ZERO (0.7.20). (lines 55-280)

| line | len | member | says |
|---:|---:|---|---|
| 69 | 11 | `private static NumberFormat withoutNegativeZero(NumberFormat base)` | The same format, except that a negative which rounds to nothing prints with no sign. |
| 85 | 5 | `public static double unsigned0(double value, int decimals)` | A value for String.format at `decimals` places, with a negative that rounds to nothing there - and -0.0 itself - made a plain zero. |
| 91 | 1 | `public static String pct2(double rate)` |  |
| 98 | 4 | `public static String pts(double points)` | TWO DECIMALS SINCE THE LADDER. |
| 113 | 3 | `public static String people(double count)` | A headcount, as a whole number of people. |
| 118 | 3 | `public static String cash(double thousands)` | A wage or a price the model holds in thousands, in the dollars it is. |
| 133 | 1 | `public static double toDollars(double thousands)` | Thousands into dollars, for the one screen that has to talk about a family. |
| 146 | 1 | `public static String tightMoney(double value)` | Money at a width that cannot overflow its column. |
| 159 | 12 | `public static String tightMoney(double value, boolean compact)` | Two thresholds, because the two views of the tier table hold numbers three orders apart. |
| 173 | 3 | `public static String money(double thousands)` | City money. |
| 178 | 3 | `public static String moneyFull(double thousands)` | The same, with every digit rather than an abbreviation. |
| 194 | 5 | `public static String marked(String prefix, String amount)` | Somebody else's money, marked as such: "US$" in place of the "$". |
| 207 | 5 | `public static String signedTight(double thousands, boolean negate)` | signed(), in the k/M column a city-scale statement wants. |
| 219 | 5 | `public static String signed(double thousands, boolean negate)` | A movement, signed - and a zero movement is written without one. |
| 226 | 3 | `public static String usd(double thousands)` | Foreign money, abbreviated. |
| 231 | 3 | `public static String usdFull(double thousands)` | ...and with every digit. |
| 243 | 8 | `public static String unitPrice(double thousands)` | A price small enough that the cents matter - a unit on the shelf, an hourly rate, anything a lopped currency has just made tiny. |
| 263 | 8 | `public static String fxRate(double rate)` | An exchange rate - local dollars per US dollar, a ratio and not money, so it never goes through toDollars(). |
| 273 | 6 | `public static String shortNumber(double value)` | 12.4k rather than 12,400 - the panel is narrow and these are two to a row. |

