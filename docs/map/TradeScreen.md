# TradeScreen.java - 1,899 lines · 26 methods · 5 constants · interface

`ham/citybuildersim/ui/TradeScreen.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The Trade & the world tab: the landing with its vitals, the month as a
> river, the reserves, the currency, what we trade, and the three quiet gauges.
> 
> Split out of UserInterface on 2026-09-18: the seven banners from TRADE & THE
> WORLD to THE THREE QUIET GAUGES exactly as they were, the shell's members
> reached through ui. The shell still reads which area and page are open
> (tradeArea, tradePage) for the rail, and the services screen borrows
> bandMeter() for two of its gauges.

**Uses:** [Palette](Palette.md) (284), [ForeignAccounts](ForeignAccounts.md) (27), [CapitalFlows](CapitalFlows.md) (8), [Currency](Currency.md) (3), [UserInterface](UserInterface.md) (2), [Equity](Equity.md) (2), [Good](Good.md) (2), [BondMarket](BondMarket.md) (1), [OutwardInvestment](OutwardInvestment.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Exchange](Exchange.md) (1), [Game](Game.md) (1), [DebtManager](DebtManager.md) (1), [EconomyManager](EconomyManager.md) (1), [WorldEconomy](WorldEconomy.md) (1), [GoodsMarket](GoodsMarket.md) (1)

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
| 815 | · · and the treasury |
| 834 | THE RESERVES |
| 885 | · · the two claims are not alike |
| 999 | · (untitled) |
| 1028 | · · the hot money |
| 1063 | · (untitled) |
| 1253 | · · what it does to the cover |
| 1314 | THE CURRENCY |
| 1368 | · · what it means |
| 1404 | · (untitled) |
| 1551 | · · what it comes to |
| 1604 | WHAT WE TRADE |
| 1634 | · · at what price |
| 1676 | · · what moved |
| 1697 | · (untitled) |
| 1769 | THE THREE QUIET GAUGES |

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
| 1311 | `boolean tradeBuying` |  |
| 1312 | `double tradeExchange` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1869 | **type** `final class TradeScreen` | The Trade & the world tab: the landing with its vitals, the month as a river, the reserves, the currency, what we trade, and the three quiet gauges. |
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

### (untitled) (lines 665-833)

| line | len | member | says |
|---:|---:|---|---|
| 685 | 148 | `void bopLedgerPage(VBox column)` | THE BALANCE OF PAYMENTS. |

### THE RESERVES (lines 834-998)

| line | len | member | says |
|---:|---:|---|---|
| 838 | 81 | `void reserveOwnPage(VBox column)` |  |
| 929 | 69 | `VBox claimBar(double gross, double debt, double parked)` | One bar of reserves with the claims against it eaten out of the left. |

### (untitled) (lines 999-1062)

| line | len | member | says |
|---:|---:|---|---|
| 1001 | 61 | `void reserveCoverPage(VBox column)` |  |

### (untitled) (lines 1063-1313)

| line | len | member | says |
|---:|---:|---|---|
| 1076 | 234 | `void exchangePage(VBox column)` | TURNING RESERVES INTO CASH, AND CASH INTO RESERVES. |

### THE CURRENCY (lines 1314-1403)

| line | len | member | says |
|---:|---:|---|---|
| 1318 | 85 | `void currencyRatePage(VBox column)` |  |

### (untitled) (lines 1404-1603)

| line | len | member | says |
|---:|---:|---|---|
| 1406 | 184 | `void currencyForcesPage(VBox column)` |  |
| 1598 | 5 | `void forceLine(VBox column, String label, String value, String tone, String what)` | A reading, and the sentence that says what it means, under it. |

### WHAT WE TRADE (lines 1604-1696)

| line | len | member | says |
|---:|---:|---|---|
| 1608 | 76 | `void tradeGoodsPage(VBox column)` |  |
| 1685 | 11 | `int priceRow(javafx.scene.layout.GridPane table, int line, String label, double worldPrice, double rate, boolean earned)` |  |

### (untitled) (lines 1697-1768)

| line | len | member | says |
|---:|---:|---|---|
| 1699 | 69 | `void tradeRecordPage(VBox column)` |  |

### THE THREE QUIET GAUGES (lines 1769-1899)

| line | len | member | says |
|---:|---:|---|---|
| 1786 | 4 | `String coverReading(double months)` | Import cover, in words a player can act on. |
| 1791 | 13 | `VBox coverMeter(ForeignAccounts fx)` |  |
| 1805 | 14 | `VBox backingMeter(ForeignAccounts fx, CapitalFlows hot)` |  |
| 1820 | 10 | `VBox parityMeter(ForeignAccounts fx)` |  |
| 1839 | 60 | `VBox bandMeter(String label, String reading, double at, double[] edges, String[] tones, String note, boolean muted)` | A meter with named bands and the city's mark on it. |

