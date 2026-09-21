# TradeScreen.java - 1,764 lines · 26 methods · 5 constants · interface

`ham/citybuildersim/ui/TradeScreen.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The Trade & the world tab: the landing with its vitals, the month as a
> river, the reserves, the currency, what we trade, and the three quiet gauges.
> 
> Split out of UserInterface on 2026-09-18: the seven banners from TRADE & THE
> WORLD to THE THREE QUIET GAUGES exactly as they were, the shell's members
> reached through ui. The shell still reads which area and page are open
> (tradeArea, tradePage) for the rail, and the services screen borrows
> bandMeter() for two of its gauges.

**Uses:** [Palette](Palette.md) (266), [ForeignAccounts](ForeignAccounts.md) (25), [CapitalFlows](CapitalFlows.md) (8), [Currency](Currency.md) (5), [UserInterface](UserInterface.md) (2), [Equity](Equity.md) (2), [Game](Game.md) (2), [Good](Good.md) (2), [OutwardInvestment](OutwardInvestment.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Exchange](Exchange.md) (1), [EconomyManager](EconomyManager.md) (1), [WorldEconomy](WorldEconomy.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 38 | TRADE & THE WORLD |
| 137 | · THE QUIET GAUGES |
| 148 | · AND THE LOUD ONES |
| 285 | ONE SUBJECT, ITS OWN STRIP |
| 347 | THE MONTH, AS A RIVER |
| 417 | · · and the band that makes it balance |
| 429 | · · in words |
| 493 | · · the trunk |
| 503 | · · the incoming half |
| 537 | · · and the outgoing |
| 566 | · · the city |
| 574 | · · the ends |
| 662 | · (untitled) |
| 774 | · · and the treasury |
| 793 | THE RESERVES |
| 844 | · · the two claims are not alike |
| 958 | · (untitled) |
| 987 | · · the hot money |
| 1022 | · (untitled) |
| 1162 | · · what it does to the cover |
| 1223 | THE CURRENCY |
| 1277 | · · what it means |
| 1313 | · (untitled) |
| 1416 | · · what it comes to |
| 1469 | WHAT WE TRADE |
| 1499 | · · at what price |
| 1541 | · · what moved |
| 1562 | · (untitled) |
| 1634 | THE THREE QUIET GAUGES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `TradeScreen.TRADE_HOME` | `"The picture"` | null is the landing |
| 70 | `TradeScreen.TRADE_MONTH_PAGES` | `{ "The picture", "The two accounts" }` |  |
| 71 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` |  |
| 72 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` |  |
| 73 | `TradeScreen.TRADE_GOODS_PAGES` | `{ "In and out", "Since founding" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 66 | `String tradeArea` | null is the landing |
| 68 | `String tradePage` |  |
| 1220 | `boolean tradeBuying` |  |
| 1221 | `double tradeExchange` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1734 | **type** `final class TradeScreen` | The Trade & the world tab: the landing with its vitals, the month as a river, the reserves, the currency, what we trade, and the three quiet gauges. |
| 36 | 1 | `TradeScreen(UserInterface ui)` |  |

### TRADE & THE WORLD (lines 38-284)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 110 | `void showForeignMenu()` |  |
| 208 | 4 | `double ownReserves()` | What is left of the reserve once everything owed against it is taken off. |
| 214 | 42 | `HBox tradeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the trade landing. |
| 257 | 27 | `HBox tradeVitals()` |  |

### ONE SUBJECT, ITS OWN STRIP (lines 285-346)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 57 | `void drawTradeScreen()` |  |

### THE MONTH, AS A RIVER (lines 347-661)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 1 | **type** `record Flow(String name, String note, double amount, String colour)` | One band of the river. |
| 365 | 86 | `void bopPicturePage(VBox column)` |  |
| 466 | 148 | `VBox bopRiver(java.util.List<Flow> in, java.util.List<Flow> out)` | The river itself. |
| 622 | 19 | `javafx.scene.shape.Path ribbon(double x0, double y0, double x1, double y1, double h, String colour)` | One ribbon: a filled cubic band of constant height between two columns. |
| 643 | 18 | `VBox bandLabel(Flow f, double x, double y, double width, boolean rightAlign)` | A band's name and figure, beside its bar. |

### (untitled) (lines 662-792)

| line | len | member | says |
|---:|---:|---|---|
| 682 | 110 | `void bopLedgerPage(VBox column)` | THE BALANCE OF PAYMENTS. |

### THE RESERVES (lines 793-957)

| line | len | member | says |
|---:|---:|---|---|
| 797 | 81 | `void reserveOwnPage(VBox column)` |  |
| 888 | 69 | `VBox claimBar(double gross, double debt, double parked)` | One bar of reserves with the claims against it eaten out of the left. |

### (untitled) (lines 958-1021)

| line | len | member | says |
|---:|---:|---|---|
| 960 | 61 | `void reserveCoverPage(VBox column)` |  |

### (untitled) (lines 1022-1222)

| line | len | member | says |
|---:|---:|---|---|
| 1035 | 184 | `void exchangePage(VBox column)` | TURNING RESERVES INTO CASH, AND CASH INTO RESERVES. |

### THE CURRENCY (lines 1223-1312)

| line | len | member | says |
|---:|---:|---|---|
| 1227 | 85 | `void currencyRatePage(VBox column)` |  |

### (untitled) (lines 1313-1468)

| line | len | member | says |
|---:|---:|---|---|
| 1315 | 140 | `void currencyForcesPage(VBox column)` |  |
| 1463 | 5 | `void forceLine(VBox column, String label, String value, String tone, String what)` | A reading, and the sentence that says what it means, under it. |

### WHAT WE TRADE (lines 1469-1561)

| line | len | member | says |
|---:|---:|---|---|
| 1473 | 76 | `void tradeGoodsPage(VBox column)` |  |
| 1550 | 11 | `int priceRow(javafx.scene.layout.GridPane table, int line, String label, double worldPrice, double rate, boolean earned)` |  |

### (untitled) (lines 1562-1633)

| line | len | member | says |
|---:|---:|---|---|
| 1564 | 69 | `void tradeRecordPage(VBox column)` |  |

### THE THREE QUIET GAUGES (lines 1634-1764)

| line | len | member | says |
|---:|---:|---|---|
| 1651 | 4 | `String coverReading(double months)` | Import cover, in words a player can act on. |
| 1656 | 13 | `VBox coverMeter(ForeignAccounts fx)` |  |
| 1670 | 14 | `VBox backingMeter(ForeignAccounts fx, CapitalFlows hot)` |  |
| 1685 | 10 | `VBox parityMeter(ForeignAccounts fx)` |  |
| 1704 | 60 | `VBox bandMeter(String label, String reading, double at, double[] edges, String[] tones, String note, boolean muted)` | A meter with named bands and the city's mark on it. |

