# PartnersPage.java - 1,054 lines · 50 methods · 16 constants · interface

`ham/citybuildersim/ui/PartnersPage.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> Trade &gt; Partners (WN6; docs/studies/spec-nations.md 8, board 2) and a nation's page behind it: who the city trades with, nation by nation; for a
> chosen good, where the next unit goes and what it nets there; the good's band this month and what moves it; and, for one nation, its figures, our trade
> with it, its money, its land and the city beside it. Every figure is PartnersView's (the snapshot, the curve and the page are immutable and safe from the
> screen's thread); this only draws them, in TradeScreen's cards.
> <p>
> WHY. Jerus: "if canda only needs 10 tons of wood, more wood would have to be sold esle where say portugal, thus transportation costs are higher". The
> model answered that in WN3 (a curve a good); nothing showed it. The page answers it for each good the city trades: the line of what the next unit nets as
> the city sells more, the places on it where a farther buyer joins, and the buyers the city sells to now, all netting the same.
> <p>
> WHAT SURVIVES THE REDRAW. TradeScreen redraws every month; the nation shown, the span, the good and the goods' fold are fields here (and the folds
> TradeScreen's openLines), so a month landing leaves the page where the player left it.

**Uses:** [Palette](Palette.md) (111), [PartnersView](PartnersView.md) (56), [Good](Good.md) (16), [WorldMarket](WorldMarket.md) (14), [Icons](Icons.md) (13), [Pieces](Pieces.md) (9), [TradeScreen](TradeScreen.md) (8), [Nations](Nations.md) (7), [NationsMap](NationsMap.md) (6), [PartnerTrade](PartnerTrade.md) (5), [UserInterface](UserInterface.md) (2), [WorldData](WorldData.md) (2), [Game](Game.md) (1)

**Used by (1):** [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 95 | THE PAGE |
| 126 | WHO WE TRADE WITH |
| 278 | WHERE THE NEXT UNIT GOES |
| 405 | · AT WHAT WE SELL NOW |
| 536 | · THE BAND THIS MONTH |
| 573 | · WHAT MOVES IT |
| 611 | THE OVERVIEW'S STRIP |
| 646 | A NATION'S PAGE |
| 845 | THE CHART - drawn as shapes in a Pane |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `PartnersPage.FIRST_ROWS` | `12` | How many rows the table shows before "+ N more nations". |
| 86 | `PartnersPage.GOOD_CHIPS` | `5, LIST_ROWS = 6, GOODS_SHOWN = 10` | How many goods get a chip of their own before "every good", and how many buyers or sellers a list names. |
| 118 | `PartnersPage.NONE_INFO` | `"This city's world has no nations in it: it was founded without the world fil...` |  |
| 136 | `PartnersPage.WHO_INFO` | `"Every nation the city sold to or bought from, ranked by the two together.Mon...` |  |
| 282 | `PartnersPage.NEXT_INFO` | `"Sell more of a good and the best buyers abroad each net a little less for th...` |  |
| 287 | `PartnersPage.SELL_INFO` | `"The buyers that took what the city sold last month.The city sells where the ...` |  |
| 291 | `PartnersPage.BUY_INFO` | `"The nations that sold what the city bought last month, cheapest landed cost ...` |  |
| 294 | `PartnersPage.BAND_INFO` | `"The floor is what the world would net the city for the next unit at the volu...` |  |
| 298 | `PartnersPage.MOVES_INFO` | `"Three things take from what the next unit nets.The city's own selling pushes...` |  |
| 615 | `PartnersPage.STRIP_INFO` | `"The five nations the city traded most with in the month last counted, sold a...` |  |
| 650 | `PartnersPage.FIGURES_INFO` | `"The nation's own measures for this year, from the world file the game was bu...` |  |
| 654 | `PartnersPage.OURS_INFO` | `"What the city sold this nation and bought from it, in D$ and in tonnes, this...` |  |
| 657 | `PartnersPage.MONEY_INFO` | `"What this nation's money is worth against the US$ and in D$, its central ban...` |  |
| 660 | `PartnersPage.LAND_INFO` | `"Land is bought from the nation it lies in, at that nation's land price, in i...` |  |
| 663 | `PartnersPage.BESIDE_INFO` | `"For comparison only: the city's GDP converted to real 2015 US$, the unit the...` |  |
| 861 | `PartnersPage.CurveChart.HEIGHT` | `250, LEFT = 74, RIGHT = 18, TOP = 22, BOTTOM = 44` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private final UserInterface ui` |  |
| 73 | `private final TradeScreen trade` |  |
| 76 | `int nation` | The nation shown: its number from 1, or 0 for the table. |
| 79 | `private PartnersView.Span span` | The span the table is of, and the good the board is of (null: the largest sold). |
| 80 | `private Good good` |  |
| 863 | `private final double xLo, xHi, yHi` |  |
| 864 | `private final double[] xs, net, world` |  |
| 865 | `private final Dot dot` |  |
| 866 | `private final List<Join> joins` |  |
| 867 | `private final List<Line> yGrid` |  |
| 868 | `private final List<Label> yLabels` |  |
| 869 | `private final List<Line> joinLines` |  |
| 870 | `private final List<Polygon> joinMarks` |  |
| 871 | `private final List<Join> drawnJoins` |  |
| 872 | `private final List<Double> yTicks` |  |
| 873 | `private final Polyline netLine` |  |
| 874 | `private final Line axis` |  |
| 875 | `private final Circle mark` |  |
| 876 | `private final Line drop` |  |
| 877 | `private final Label dotLabel` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 70 | 985 | **type** `final class PartnersPage` | Trade &gt; Partners (WN6; docs/studies/spec-nations.md 8, board 2) and a nation's page behind it: who the city trades with, nation by nation; for a chosen good, where the next unit goes and what it nets there; the goo... |
| 88 | 4 | `PartnersPage(UserInterface ui, TradeScreen trade)` |  |
| 93 | 1 | `private Game game()` |  |

### THE PAGE (lines 95-125)

| line | len | member | says |
|---:|---:|---|---|
| 100 | 17 | `void page(VBox page)` | The page: a nation's, or the table with the good's board under it. |
| 122 | 3 | `void open(int nationNumber)` | A door to a nation's page from anywhere on the tab. |

### WHO WE TRADE WITH (lines 126-277)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 5 | `private void tablePage(VBox page)` |  |
| 141 | 36 | `private VBox whoCard(PartnersView.Table table)` | WHO WE TRADE WITH: the table, the month or the year so far, twelve rows and then the rest. |
| 179 | 37 | `private GridPane table(List<PartnersView.Row> rows, boolean land)` | The table: a row a nation, a column each for what the rows carry. |
| 218 | 13 | `private String totals(PartnersView.Table table, boolean month)` | The line under the table: how many nations, the sums, and the trade that no partner took. |
| 233 | 15 | `private Label nationLink(String name, int number)` | A nation's name as a door to its page. |
| 250 | 6 | `static String tonnes(double t)` | Tonnes as the haul column writes them: "840 t", "12.4 kt", "3.20 Mt". |
| 258 | 3 | `static String markWords(int mark)` | The mark a figure carries: a published figure none, a projection a half-moon, the game's estimate a ring (NationsMap's). |
| 263 | 4 | `static String tickWords(double share)` | A power of ten of the share of the world's use, as the chart's axis writes it: "10%", "1%", "0.1%", "0.001%". |
| 269 | 8 | `static String shareWords(double share)` | A share of the world's use, to the places it needs: "12%", "2.1%", "0.04%", "0.0013%". |

### WHERE THE NEXT UNIT GOES (lines 278-404)

| line | len | member | says |
|---:|---:|---|---|
| 303 | 11 | `private void nextSection(VBox page)` | The second card and the three under it, for the good chosen. |
| 316 | 33 | `private VBox nextCard(PartnersView.Goods goods, PartnersView.GoodBoard board)` | WHERE THE NEXT UNIT GOES: the good's chips, the chart, its key. |
| 351 | 18 | `private Node goodChips(PartnersView.Goods goods, Good chosen)` | The chips: the goods the city sold abroad (the first few), the good chosen, and "every good" to open the rest. |
| 370 | 10 | `private FlowPane goodStrip(List<Good> list, Good chosen, boolean leftAligned)` |  |
| 382 | 6 | `private Node chartKey()` | The chart's key: the month's line, the world's, the dot and the ticks. |
| 389 | 15 | `private static HBox lineKey(String colour, boolean dashed, String name)` |  |

### AT WHAT WE SELL NOW (lines 405-535)

| line | len | member | says |
|---:|---:|---|---|
| 408 | 43 | `private VBox sellNowCard(PartnersView.GoodBoard b)` | AT WHAT WE SELL NOW (or, for a good the city only buys, WHO SELLS IT TO US). |
| 453 | 10 | `private String joinedWords(WorldMarket.Curve curve, int from, int count)` | "the first three sinks after k": "Cuba at 2.1% of the world's use, ..." for the buyers that would take it first. |
| 465 | 4 | `static String volume(double units, Good g)` | A volume with its unit: "46 cubic metres", "1.5 tonnes", "0.40 vans". |
| 471 | 5 | `static String shareOfSale(double share)` | A share of what the city sold: "89%", "7%", "0.4%". |
| 478 | 15 | `private void addBought(VBox c, PartnersView.GoodBoard b)` | Under the buyers, what the city bought of the same good, when it did: who sold it and what it landed at. |
| 495 | 40 | `private VBox sellersCard(PartnersView.GoodBoard b)` | WHO SELLS IT TO US: for a good the city only buys, the sellers at what it buys now, and the next to sell. |

### THE BAND THIS MONTH (lines 536-572)

| line | len | member | says |
|---:|---:|---|---|
| 539 | 28 | `private VBox bandCard(PartnersView.GoodBoard b)` | THE BAND THIS MONTH: the floor, the price here, the ceiling, each with where it comes from. |
| 568 | 4 | `private static VBox bandRow(String name, String figure, String reason)` |  |

### WHAT MOVES IT (lines 573-610)

| line | len | member | says |
|---:|---:|---|---|
| 576 | 17 | `private VBox movesCard(PartnersView.GoodBoard b)` | WHAT MOVES IT: our own selling, the world's answer, the freight to the first buyer, each a small diverging line in D$ a unit. |
| 594 | 3 | `private String firstBuyer(PartnersView.GoodBoard b)` |  |
| 598 | 5 | `private static double add(List<Pieces.Force> rows, double scale, String name, double value, String sentence)` |  |
| 605 | 5 | `private String signedUnit(double thousands)` | A difference in D$ a unit, signed, with a true minus. |

### THE OVERVIEW'S STRIP (lines 611-645)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 18 | `Node strip()` | OUR PARTNERS THIS MONTH: the top five of the month's table as small door cards, and a door to the page; null where there is no table or no partner. |
| 639 | 6 | `String reachWords(boolean sold)` | "to 38 nations" / "from 24 nations" for the five figures' notes, or null where the month's table has none. |

### A NATION'S PAGE (lines 646-844)

| line | len | member | says |
|---:|---:|---|---|
| 666 | 16 | `private void nationPage(VBox page, PartnersView.Page p)` |  |
| 684 | 20 | `private Node nationHead(PartnersView.Page p)` | The head: a way back, the nation's name, and its money. |
| 706 | 32 | `private VBox figuresCard(PartnersView.Page p)` | ITS FIGURES: label · value · rank, the map card's rows. |
| 740 | 23 | `private VBox oursCard(PartnersView.Page p)` | OUR TRADE WITH IT: the month, the year so far, by good, and the finished years. |
| 764 | 9 | `private Node goodBars(List<PartnerTrade.Cell> cells, double scale, boolean heads)` |  |
| 774 | 11 | `private Node yearBars(List<PartnersView.YearRow> years)` |  |
| 787 | 25 | `private VBox moneyCard(PartnersView.Page p)` | ITS MONEY: the rate, what one is worth in D$, the policy rate, the vault's holding and what the city owes in it. |
| 814 | 9 | `private VBox landCard(PartnersView.Page p)` | ITS LAND: its factor and mark, the published price a hectare, and what the city bought from it. |
| 825 | 9 | `private VBox besideCard(PartnersView.Page p)` | THE CITY BESIDE IT: the city's GDP in real 2015 US$ a year beside its output, and the share. |
| 836 | 8 | `static String sharePercent(double share)` | A small share of an output: "0.04%", "12%", "<0.001%". |

### THE CHART - drawn as shapes in a Pane (lines 845-1054)

| line | len | member | says |
|---:|---:|---|---|
| 854 | 200 | **type** `static final class CurveChart extends Pane` | What the next unit nets (up, D$ a unit) against how much the city sells (across, the share of the world's use a year, on a log scale): the month's line solid in the export violet, the world's dashed, a dot where the c... |
| 856 | 1 | **type** `record Dot(double share, double value, String words)` _(in PartnersPage.CurveChart)_ | The dot: where the city sold, its share of the world's use, what the next one nets there, its words. |
| 859 | 1 | **type** `record Join(double share, String name)` _(in PartnersPage.CurveChart)_ | A tick: where a buyer joins (a share of the world's use) and its name. |
| 879 | 78 | `CurveChart(double[] xs, double[] net, double[] world, Dot dot, List<Join> joins, DoubleFunction<String> money, DoubleFunction<S...` _(in PartnersPage.CurveChart)_ |  |
| 958 | 5 | `private static Label small(String text, String tone)` _(in PartnersPage.CurveChart)_ |  |
| 965 | 5 | `static double niceStep(double v)` _(in PartnersPage.CurveChart)_ | A step of 1, 2, 2.5 or 5 times a power of ten that is at least v (nothing: 1). |
| 971 | 4 | `private double px(double share, double plotL, double plotW)` _(in PartnersPage.CurveChart)_ |  |
| 976 | 3 | `private double py(double v, double plotT, double plotH)` _(in PartnersPage.CurveChart)_ |  |
| 980 | 4 | `private static void place(Label l, double x, double y)` _(in PartnersPage.CurveChart)_ |  |
| 985 | 68 | `protected void layoutChildren()` _(in PartnersPage.CurveChart)_ |  |

