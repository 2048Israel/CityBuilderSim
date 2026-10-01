# TradeScreen.java - 1,895 lines · 26 methods · 5 constants · interface

`ham/citybuildersim/ui/TradeScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The Trade & the world tab: the landing with its vitals, the month as a
> river, the reserves, the currency, what we trade, and the three quiet gauges.
> 
> Split out of UserInterface on 2026-09-18: the seven banners from TRADE & THE
> WORLD to THE THREE QUIET GAUGES exactly as they were, the shell's members
> reached through ui. The shell still reads which area and page are open
> (tradeArea, tradePage) for the rail, and the services screen borrows
> bandMeter() for two of its gauges.

**Uses:** [Palette](Palette.md) (279), [ForeignAccounts](ForeignAccounts.md) (27), [CapitalFlows](CapitalFlows.md) (8), [Currency](Currency.md) (3), [UserInterface](UserInterface.md) (2), [Equity](Equity.md) (2), [Good](Good.md) (2), [BondMarket](BondMarket.md) (1), [OutwardInvestment](OutwardInvestment.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Exchange](Exchange.md) (1), [Game](Game.md) (1), [DebtManager](DebtManager.md) (1), [EconomyManager](EconomyManager.md) (1), [WorldEconomy](WorldEconomy.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 38 | TRADE & THE WORLD |
| 138 | · THE QUIET GAUGES |
| 149 | · AND THE LOUD ONES |
| 286 | ONE SUBJECT, ITS OWN STRIP |
| 346 | THE MONTH, AS A RIVER |
| 416 | · · and the band that makes it balance |
| 428 | · · in words |
| 492 | · · the trunk |
| 502 | · · the incoming half |
| 536 | · · and the outgoing |
| 565 | · · the city |
| 573 | · · the ends |
| 661 | · (untitled) |
| 811 | · · and the treasury |
| 830 | THE RESERVES |
| 881 | · · the two claims are not alike |
| 995 | · (untitled) |
| 1024 | · · the hot money |
| 1059 | · (untitled) |
| 1249 | · · what it does to the cover |
| 1310 | THE CURRENCY |
| 1364 | · · what it means |
| 1400 | · (untitled) |
| 1547 | · · what it comes to |
| 1600 | WHAT WE TRADE |
| 1630 | · · at what price |
| 1672 | · · what moved |
| 1693 | · (untitled) |
| 1765 | THE THREE QUIET GAUGES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `TradeScreen.TRADE_HOME` | `"The picture"` | null is the landing |
| 73 | `TradeScreen.TRADE_MONTH_PAGES` | `{ "The picture", "The two accounts" }` |  |
| 74 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` |  |
| 75 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` |  |
| 76 | `TradeScreen.TRADE_GOODS_PAGES` | `{ "In and out", "Since founding" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 69 | `String tradeArea` | null is the landing |
| 71 | `String tradePage` |  |
| 1307 | `boolean tradeBuying` |  |
| 1308 | `double tradeExchange` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1865 | **type** `final class TradeScreen` | The Trade & the world tab: the landing with its vitals, the month as a river, the reserves, the currency, what we trade, and the three quiet gauges. |
| 36 | 1 | `TradeScreen(UserInterface ui)` |  |

### TRADE & THE WORLD (lines 38-285)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 108 | `void showForeignMenu()` |  |
| 209 | 4 | `double ownReserves()` | What is left of the reserve once everything owed against it is taken off. |
| 215 | 42 | `HBox tradeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the trade landing. |
| 258 | 27 | `HBox tradeVitals()` |  |

### ONE SUBJECT, ITS OWN STRIP (lines 286-345)

| line | len | member | says |
|---:|---:|---|---|
| 290 | 55 | `void drawTradeScreen()` |  |

### THE MONTH, AS A RIVER (lines 346-660)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 1 | **type** `record Flow(String name, String note, double amount, String colour)` | One band of the river. |
| 364 | 86 | `void bopPicturePage(VBox column)` |  |
| 465 | 148 | `VBox bopRiver(java.util.List<Flow> in, java.util.List<Flow> out)` | The river itself. |
| 621 | 19 | `javafx.scene.shape.Path ribbon(double x0, double y0, double x1, double y1, double h, String colour)` | One ribbon: a filled cubic band of constant height between two columns. |
| 642 | 18 | `VBox bandLabel(Flow f, double x, double y, double width, boolean rightAlign)` | A band's name and figure, beside its bar. |

### (untitled) (lines 661-829)

| line | len | member | says |
|---:|---:|---|---|
| 681 | 148 | `void bopLedgerPage(VBox column)` | THE BALANCE OF PAYMENTS. |

### THE RESERVES (lines 830-994)

| line | len | member | says |
|---:|---:|---|---|
| 834 | 81 | `void reserveOwnPage(VBox column)` |  |
| 925 | 69 | `VBox claimBar(double gross, double debt, double parked)` | One bar of reserves with the claims against it eaten out of the left. |

### (untitled) (lines 995-1058)

| line | len | member | says |
|---:|---:|---|---|
| 997 | 61 | `void reserveCoverPage(VBox column)` |  |

### (untitled) (lines 1059-1309)

| line | len | member | says |
|---:|---:|---|---|
| 1072 | 234 | `void exchangePage(VBox column)` | TURNING RESERVES INTO CASH, AND CASH INTO RESERVES. |

### THE CURRENCY (lines 1310-1399)

| line | len | member | says |
|---:|---:|---|---|
| 1314 | 85 | `void currencyRatePage(VBox column)` |  |

### (untitled) (lines 1400-1599)

| line | len | member | says |
|---:|---:|---|---|
| 1402 | 184 | `void currencyForcesPage(VBox column)` |  |
| 1594 | 5 | `void forceLine(VBox column, String label, String value, String tone, String what)` | A reading, and the sentence that says what it means, under it. |

### WHAT WE TRADE (lines 1600-1692)

| line | len | member | says |
|---:|---:|---|---|
| 1604 | 76 | `void tradeGoodsPage(VBox column)` |  |
| 1681 | 11 | `int priceRow(javafx.scene.layout.GridPane table, int line, String label, double worldPrice, double rate, boolean earned)` |  |

### (untitled) (lines 1693-1764)

| line | len | member | says |
|---:|---:|---|---|
| 1695 | 69 | `void tradeRecordPage(VBox column)` |  |

### THE THREE QUIET GAUGES (lines 1765-1895)

| line | len | member | says |
|---:|---:|---|---|
| 1782 | 4 | `String coverReading(double months)` | Import cover, in words a player can act on. |
| 1787 | 13 | `VBox coverMeter(ForeignAccounts fx)` |  |
| 1801 | 14 | `VBox backingMeter(ForeignAccounts fx, CapitalFlows hot)` |  |
| 1816 | 10 | `VBox parityMeter(ForeignAccounts fx)` |  |
| 1835 | 60 | `VBox bandMeter(String label, String reading, double at, double[] edges, String[] tones, String note, boolean muted)` | A meter with named bands and the city's mark on it. |

