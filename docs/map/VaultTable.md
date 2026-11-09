# VaultTable.java - 235 lines · 13 methods · 4 constants · interface

`ham/citybuildersim/ui/VaultTable.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The vault in eight moneys (WN6; docs/studies/spec-nations.md 8, board 3): the first card of Trade &gt; The reserves where the vault holds, or can hold,
> more than the dollar - a table of the eight (what it holds of each in the money's own mark, what that is worth in US$, its share, how it moved against
> the US$ this month, its central bank's policy rate and, where anything is owed in it, what the city owes), the total, the month's revaluation across the
> moneys in words, and under a fold the seven against the US$ and the eight's policy rates over the years. Every figure is VaultView's.
> <p>
> WHY. The reserves page said the sum, the sum's revaluation by the rate and the biggest cross mover, and a line of what was held; the vault keeps a
> balance in each of the eight (ForeignAccounts, THE VAULT IN EIGHT) and the page showed none of them in a place a player could read across. The card's
> charts are History's series (the rates are HistorySave's fx&lt;code&gt;, the policy rates policy&lt;code&gt;), drawn in TimeChart as the currency page's are.

**Uses:** [Palette](Palette.md) (31), [TimeChart](TimeChart.md) (14), [Kept](Kept.md) (8), [ForeignAccounts](ForeignAccounts.md) (7), [VaultView](VaultView.md) (6), [HistorySave](HistorySave.md) (6), [TradeScreen](TradeScreen.md) (4), [ChartModel](ChartModel.md) (4), [UserInterface](UserInterface.md) (2), [Money](Money.md) (2), [Game](Game.md) (1), [Icons](Icons.md) (1), [Pieces](Pieces.md) (1), [WorldMoneys](WorldMoneys.md) (1), [HistoryScreen](HistoryScreen.md) (1)

**Used by (1):** [TradeScreen](TradeScreen.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `VaultTable.CHART_HEIGHT` | `200` | The plot's height in the two charts, pixels. |
| 58 | `VaultTable.EIGHT_INFO` | `"The vault keeps a balance in each of the eight moneys central banks keep: th...` |  |
| 63 | `VaultTable.YEARS_INFO` | `"The seven against the US dollar since the city was founded, as how many US d...` |  |
| 202 | `VaultTable.CHARTS` | `"vault.charts"` | The key in the fold's box's properties for the charts its latest draw put in it (years()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `private final UserInterface ui` |  |
| 45 | `private final TradeScreen trade` |  |
| 48 | `private final ChartModel rateWindow` | The two charts' windows and hidden lines, kept across the month's redraw. |
| 49 | `private final Set<String> rateHidden` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 39 | 197 | **type** `final class VaultTable` | The vault in eight moneys (WN6; docs/studies/spec-nations.md 8, board 3): the first card of Trade &gt; The reserves where the vault holds, or can hold, more than the dollar - a table of the eight (what it holds of eac... |
| 51 | 4 | `VaultTable(UserInterface ui, TradeScreen trade)` |  |
| 56 | 1 | `private Game game()` |  |
| 67 | 6 | `boolean applies()` | Whether the vault holds, or can hold, more than the dollar: a rate for another money, or a balance in one. |
| 75 | 13 | `VBox eightCard()` | IN THE VAULT, IN EIGHT MONEYS; null where only the dollar can be held. |
| 90 | 36 | `private GridPane table(VaultView v, boolean owed)` | The table: a row a money and a total row. |
| 127 | 4 | `private static String capital(String name)` |  |
| 133 | 4 | `static String signedPct(double move)` | A move against the US$ as a signed per cent to a place, with a true minus: "−0.9%", "+0.4%", "0.0%". |
| 142 | 8 | `private String revaluation(VaultView v)` | The month's cross revaluation under the table, as TradeScreen.moved() words it: what the vault gained or lost because the eight moved against each other, and the money that moved it most; null in a month nothing moved... |
| 152 | 48 | `private Node years()` | Under the fold: the seven against the US$, and the eight's policy rates. |
| 206 | 4 | `private static List<TimeChart> chartsOf(VBox box)` | The charts the box's latest draw put in it. |
| 212 | 13 | `private TimeChart chart(String title, String unit, List<TimeChart.Line> lines, ChartModel window, Set<String> hidden, TimeChart...` | A chart of the fold, `width` wide: kept (KP6) - made once in its scope, told the month's lines each draw. |
| 226 | 4 | `private static boolean anyNumber(double[] series)` |  |
| 231 | 4 | `private static double firstPositive(double[] series)` |  |

