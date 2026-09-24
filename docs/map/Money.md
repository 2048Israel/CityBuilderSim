# Money.java - 236 lines · 17 methods · 1 constants · interface

`ham/citybuildersim/ui/Money.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Every figure the interface prints as money, in one place.
> 
> These were instance methods of UserInterface that read nothing but their
> arguments and one number formatter; they are static here so that every
> screen - the shell and the screen classes split out of it - can call
> money(x) as it always did, through an import static, without holding a
> reference to the window. The model counts in THOUSANDS; toDollars() is the
> one conversion and everything else calls it.

**Uses:** [Currency](Currency.md) (2)

**Used by (1):** [PolicyScreen](PolicyScreen.md)

## Sections

| line | section |
|---:|---|
| 22 | THE ONE PLACE MODEL MONEY BECOMES A STRING. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `Money.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | Thousands separators, Canadian style; every figure below goes through it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 217 | **type** `public final class Money` | Every figure the interface prints as money, in one place. |

### THE ONE PLACE MODEL MONEY BECOMES A STRING. (lines 22-236)

| line | len | member | says |
|---:|---:|---|---|
| 49 | 4 | `static { ... }` |  |
| 54 | 1 | `public static String pct2(double rate)` |  |
| 61 | 1 | `public static String pts(double points)` | TWO DECIMALS SINCE THE LADDER. |
| 73 | 3 | `public static String people(double count)` | A headcount, as a whole number of people. |
| 78 | 3 | `public static String cash(double thousands)` | A wage or a price the model holds in thousands, in the dollars it is. |
| 93 | 1 | `public static double toDollars(double thousands)` | Thousands into dollars, for the one screen that has to talk about a family. |
| 106 | 1 | `public static String tightMoney(double value)` | Money at a width that cannot overflow its column. |
| 119 | 8 | `public static String tightMoney(double value, boolean compact)` | Two thresholds, because the two views of the tier table hold numbers three orders apart. |
| 129 | 3 | `public static String money(double thousands)` | City money. |
| 134 | 3 | `public static String moneyFull(double thousands)` | The same, with every digit rather than an abbreviation. |
| 150 | 5 | `public static String marked(String prefix, String amount)` | Somebody else's money, marked as such: "US$" in place of the "$". |
| 163 | 5 | `public static String signedTight(double thousands, boolean negate)` | signed(), in the k/M column a city-scale statement wants. |
| 175 | 5 | `public static String signed(double thousands, boolean negate)` | A movement, signed - and a zero movement is written without one. |
| 182 | 3 | `public static String usd(double thousands)` | Foreign money, abbreviated. |
| 187 | 3 | `public static String usdFull(double thousands)` | ...and with every digit. |
| 199 | 8 | `public static String unitPrice(double thousands)` | A price small enough that the cents matter - a unit on the shelf, an hourly rate, anything a lopped currency has just made tiny. |
| 219 | 8 | `public static String fxRate(double rate)` | An exchange rate - local dollars per US dollar, a ratio and not money, so it never goes through toDollars(). |
| 229 | 6 | `public static String shortNumber(double value)` | 12.4k rather than 12,400 - the panel is narrow and these are two to a row. |

