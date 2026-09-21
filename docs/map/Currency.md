# Currency.java - 58 lines · 5 methods · 8 constants · model

`ham/citybuildersim/Currency.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> What the city's money is called, and how it is written.
> 
> ONE PLACE THAT DECIDES, which is the whole reason this exists rather than a
> string literal per screen. Twenty screens each writing "$" is twenty screens
> that will disagree the first time one of them has to say WHICH dollar.
> 
> THE CITY'S OWN MONEY IS THE DANZIK DOLLAR - Jerus, after the exchange rate
> went in and every screen started having to say which currency it meant. The
> convention is his:
> 
>     USD 10,000  ->  D$14,560        when both are on the screen
>                 ->  $14,560         when it is the only money in sight
> 
> Which is how anybody writes about money: nobody says "CAD" in Toronto, and
> everybody does the moment a US price is beside it. The player spends their
> whole game in one currency and should not be made to read a currency code to
> buy a house; they should be made to read one the instant a foreign price is
> on the same line, because that is the instant it matters.

**Used by (7):** [Denomination](Denomination.md), [FinancesScreen](FinancesScreen.md), [HistoryScreen](HistoryScreen.md), [Money](Money.md), [PolicyScreen](PolicyScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `Currency.NAME` | `"Danzik dollar"` | The city's own money. |
| 31 | `Currency.PLURAL` | `"Danzik dollars"` |  |
| 32 | `Currency.CODE` | `"DZD"` |  |
| 35 | `Currency.SYMBOL` | `"$"` | Written alone, where nothing foreign is in sight. |
| 38 | `Currency.QUALIFIED` | `"D$"` | ...and written where a foreign figure is on the same screen. |
| 41 | `Currency.FOREIGN_NAME` | `"US dollar"` | The world's money, which the game holds exactly one of. |
| 42 | `Currency.FOREIGN_CODE` | `"USD"` |  |
| 43 | `Currency.FOREIGN_SYMBOL` | `"US$"` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 34 | **type** `public final class Currency` | What the city's money is called, and how it is written. |
| 27 | 1 | `private Currency()` |  |
| 46 | 1 | `public static String local(String amount)` | Local money, on a screen with no foreign figure on it. |
| 49 | 1 | `public static String qualified(String amount)` | Local money, on a screen that also shows dollars. |
| 52 | 1 | `public static String foreign(String amount)` | Foreign money, always marked, because it is never the default here. |
| 55 | 3 | `public static String rateUnit()` | "Danzik dollars per US dollar", as the exchange rate's unit. |

