# TradeScreen.java - 1,859 lines · 26 methods · 5 constants · interface

`ham/citybuildersim/ui/TradeScreen.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The Trade & the world tab: the landing with its vitals, the month as a
> river, the reserves, the currency, what we trade, and the three quiet gauges.
> 
> Split out of UserInterface on 2026-09-18: the seven banners from TRADE & THE
> WORLD to THE THREE QUIET GAUGES exactly as they were, the shell's members
> reached through ui. The shell still reads which area and page are open
> (tradeArea, tradePage) for the rail, and the services screen borrows
> bandMeter() for two of its gauges.

**Uses:** [Palette](Palette.md) (275), [ForeignAccounts](ForeignAccounts.md) (27), [CapitalFlows](CapitalFlows.md) (8), [Currency](Currency.md) (5), [UserInterface](UserInterface.md) (2), [Equity](Equity.md) (2), [Game](Game.md) (2), [Good](Good.md) (2), [OutwardInvestment](OutwardInvestment.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Exchange](Exchange.md) (1), [DebtManager](DebtManager.md) (1), [EconomyManager](EconomyManager.md) (1), [WorldEconomy](WorldEconomy.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 38 | TRADE & THE WORLD |
| 140 | · THE QUIET GAUGES |
| 151 | · AND THE LOUD ONES |
| 288 | ONE SUBJECT, ITS OWN STRIP |
| 350 | THE MONTH, AS A RIVER |
| 420 | · · and the band that makes it balance |
| 432 | · · in words |
| 496 | · · the trunk |
| 506 | · · the incoming half |
| 540 | · · and the outgoing |
| 569 | · · the city |
| 577 | · · the ends |
| 665 | · (untitled) |
| 777 | · · and the treasury |
| 796 | THE RESERVES |
| 847 | · · the two claims are not alike |
| 961 | · (untitled) |
| 990 | · · the hot money |
| 1025 | · (untitled) |
| 1213 | · · what it does to the cover |
| 1274 | THE CURRENCY |
| 1328 | · · what it means |
| 1364 | · (untitled) |
| 1511 | · · what it comes to |
| 1564 | WHAT WE TRADE |
| 1594 | · · at what price |
| 1636 | · · what moved |
| 1657 | · (untitled) |
| 1729 | THE THREE QUIET GAUGES |

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
| 1271 | `boolean tradeBuying` |  |
| 1272 | `double tradeExchange` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1829 | **type** `final class TradeScreen` | The Trade & the world tab: the landing with its vitals, the month as a river, the reserves, the currency, what we trade, and the three quiet gauges. |
| 36 | 1 | `TradeScreen(UserInterface ui)` |  |

### TRADE & THE WORLD (lines 38-287)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 110 | `void showForeignMenu()` |  |
| 211 | 4 | `double ownReserves()` | What is left of the reserve once everything owed against it is taken off. |
| 217 | 42 | `HBox tradeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the trade landing. |
| 260 | 27 | `HBox tradeVitals()` |  |

### ONE SUBJECT, ITS OWN STRIP (lines 288-349)

| line | len | member | says |
|---:|---:|---|---|
| 292 | 57 | `void drawTradeScreen()` |  |

### THE MONTH, AS A RIVER (lines 350-664)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 1 | **type** `record Flow(String name, String note, double amount, String colour)` | One band of the river. |
| 368 | 86 | `void bopPicturePage(VBox column)` |  |
| 469 | 148 | `VBox bopRiver(java.util.List<Flow> in, java.util.List<Flow> out)` | The river itself. |
| 625 | 19 | `javafx.scene.shape.Path ribbon(double x0, double y0, double x1, double y1, double h, String colour)` | One ribbon: a filled cubic band of constant height between two columns. |
| 646 | 18 | `VBox bandLabel(Flow f, double x, double y, double width, boolean rightAlign)` | A band's name and figure, beside its bar. |

### (untitled) (lines 665-795)

| line | len | member | says |
|---:|---:|---|---|
| 685 | 110 | `void bopLedgerPage(VBox column)` | THE BALANCE OF PAYMENTS. |

### THE RESERVES (lines 796-960)

| line | len | member | says |
|---:|---:|---|---|
| 800 | 81 | `void reserveOwnPage(VBox column)` |  |
| 891 | 69 | `VBox claimBar(double gross, double debt, double parked)` | One bar of reserves with the claims against it eaten out of the left. |

### (untitled) (lines 961-1024)

| line | len | member | says |
|---:|---:|---|---|
| 963 | 61 | `void reserveCoverPage(VBox column)` |  |

### (untitled) (lines 1025-1273)

| line | len | member | says |
|---:|---:|---|---|
| 1038 | 232 | `void exchangePage(VBox column)` | TURNING RESERVES INTO CASH, AND CASH INTO RESERVES. |

### THE CURRENCY (lines 1274-1363)

| line | len | member | says |
|---:|---:|---|---|
| 1278 | 85 | `void currencyRatePage(VBox column)` |  |

### (untitled) (lines 1364-1563)

| line | len | member | says |
|---:|---:|---|---|
| 1366 | 184 | `void currencyForcesPage(VBox column)` |  |
| 1558 | 5 | `void forceLine(VBox column, String label, String value, String tone, String what)` | A reading, and the sentence that says what it means, under it. |

### WHAT WE TRADE (lines 1564-1656)

| line | len | member | says |
|---:|---:|---|---|
| 1568 | 76 | `void tradeGoodsPage(VBox column)` |  |
| 1645 | 11 | `int priceRow(javafx.scene.layout.GridPane table, int line, String label, double worldPrice, double rate, boolean earned)` |  |

### (untitled) (lines 1657-1728)

| line | len | member | says |
|---:|---:|---|---|
| 1659 | 69 | `void tradeRecordPage(VBox column)` |  |

### THE THREE QUIET GAUGES (lines 1729-1859)

| line | len | member | says |
|---:|---:|---|---|
| 1746 | 4 | `String coverReading(double months)` | Import cover, in words a player can act on. |
| 1751 | 13 | `VBox coverMeter(ForeignAccounts fx)` |  |
| 1765 | 14 | `VBox backingMeter(ForeignAccounts fx, CapitalFlows hot)` |  |
| 1780 | 10 | `VBox parityMeter(ForeignAccounts fx)` |  |
| 1799 | 60 | `VBox bandMeter(String label, String reading, double at, double[] edges, String[] tones, String note, boolean muted)` | A meter with named bands and the city's mark on it. |

