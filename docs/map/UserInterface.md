# UserInterface.java - 5,505 lines · 118 methods · 39 constants · interface

`ham/citybuildersim/ui/UserInterface.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The window: the stage and its theme, the header - the clock and the speed,
> six headline tiles, the rating and the inbox - and the debt bar, the rail
> down the left, the left panel and the construction panel, the main menu over
> its backdrop, the save and settings dialogs, the time-skip dialog, and the
> scroller every screen draws into. Everything a tab SHOWS is a class of
> its own in this package since 2026-09-18 - one screen per class, listed
> below in rail order - and this is what is left once they are out: what the
> screens share, and the shell that holds them.
> 
> WHY. This file was 25,000 lines, every screen in one class, and a session
> working on the bank had to carry the policy tab's text in the same window.
> The split is mechanical and the screens' text is verbatim; see the
> project's splitting-the-interface.md for how it was done and what moved
> where. A screen reaches the window through the ui it is constructed with
> (ui.game, ui.rootMenu, ui.clearMenu()), and the window reaches a screen
> through its field (policyScreen.showPolicyMenu()). Nothing in the model
> imports this package.

**Uses:** [Palette](Palette.md) (324), [Icons](Icons.md) (14), [GameFiles](GameFiles.md) (12), [CityCalendar](CityCalendar.md) (10), [SaveHeader](SaveHeader.md) (10), [PeopleScreen](PeopleScreen.md) (9), [Notice](Notice.md) (8), [FoundingScreen](FoundingScreen.md) (6), [DebtManager](DebtManager.md) (6), [Currency](Currency.md) (5), [Debt](Debt.md) (5), [DemolitionLog](DemolitionLog.md) (5), [Game](Game.md) (4), [ServicesScreen](ServicesScreen.md) (4), [GameVersion](GameVersion.md) (4), [GamePrefs](GamePrefs.md) (4), [SectorScreen](SectorScreen.md) (3), [FinancesScreen](FinancesScreen.md) (3), [BankScreen](BankScreen.md) (3), [TradeScreen](TradeScreen.md) (3), [PolicyScreen](PolicyScreen.md) (3), [HistoryScreen](HistoryScreen.md) (3), [ConstructionScreen](ConstructionScreen.md) (3), [ForeignAccounts](ForeignAccounts.md) (3), [TimeSkipReport](TimeSkipReport.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [ConstructionControl](ConstructionControl.md) (3), [BuildScreen](BuildScreen.md) (2), [LandScreen](LandScreen.md) (2), [GovernmentScreen](GovernmentScreen.md) (2)... and 22 more

**Used by (16):** [BankScreen](BankScreen.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [CityBuilderSim](CityBuilderSim.md), [ConstructionScreen](ConstructionScreen.md), [FinancesScreen](FinancesScreen.md), [FoundingScreen](FoundingScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 56 | · THE SCREENS, one class each since 2026-09-18, in the order the rail |
| 78 | · WHERE THE SCREENS WENT. Each line is a run of this file's banner |
| 150 | THE RECEIPT INDICATOR |
| 183 | THE THEME |
| 340 | THE CLOCK |
| 506 | · THE MIDDLE COLUMN SCROLLS NOW, and it should have from the start. |
| 555 | · WHERE THE PAGE IS SCROLLED TO IS A FACT, NOT SOMETHING RECAPTURED. |
| 607 | · THE RAIL, AND WHY IT WAS PART OF THE PANEL. |
| 645 | · THE STAGE IS A STACK NOW. |
| 689 | · AND THE TIME CONTROLS DID NOT FLOAT, WHICH WAS A CORRECTION. |
| 704 | · THE WHEEL WORKS WHERE THE POINTER ALREADY IS. |
| 756 | · FULL SCREEN, AND THE TWO THINGS JAVAFX DOES ABOUT IT THAT WE DO NOT |
| 972 | · AND THE SCROLL STAYS WHERE YOU PUT IT. |
| 1029 | · HOLD THE PAGE'S HEIGHT WHILE IT IS REBUILT, so the position is |
| 1118 | A WHEEL NOTCH IS WORTH THE SAME EVERY TIME |
| 1550 | THE HEADER (0.7.21) |
| 1686 | · · POPULATION, and the month's net |
| 1725 | · · GDP, annualised from month 1, and real growth |
| 1754 | · · INFLATION, against the player's target |
| 1803 | · · OUT OF WORK, with a shortage read as watch |
| 1823 | · · TREASURY: cash, red only when overdrawn, and the month |
| 1839 | · · RATE and the currency: the price of money |
| 2491 | THE MAIN MENU (0.7.21) |
| 2764 | · WHAT "CONTINUE" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO CONTINUE |
| 2993 | THE SAVE SYSTEM |
| 3297 | QUIT ASKS FIRST (0.7.20) |
| 3353 | A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) |
| 3423 | EVERY TOOLTIP, DRESSED ONCE (0.7.20) |
| 3466 | SETTINGS. |
| 3580 | THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. |
| 3603 | ECONOMY IS GONE, AND IT SPLIT IN TWO. |
| 3620 | THE SCROLLER |
| 3691 | · WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and |
| 3702 | SIMULATE MULTIPLE MONTHS |
| 3813 | · · headlines |
| 3829 | · · deltas |
| 3879 | · · land |
| 3887 | · · buildings |
| 3903 | · · demolitions |
| 3927 | · · health |
| 3971 | · · households |
| 4131 | CONSTRUCTION PANEL |
| 4465 | THE RAIL |
| 4716 | THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF |
| 4899 | THE INBOX |
| 4939 | · · what an urgent notice gets |
| 4949 | · · the list |
| 4996 | · the envelope |
| 5187 | TOASTS (0.7.20) |
| 5270 | TIME, AND WHAT THE MONTH IS WORTH |
| 5364 | THE CHART OVER THE WHOLE WINDOW (0.7.23) |
| 5473 | TWELVE PIPS, AND ONE OF THEM MOVED. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 217 | `UserInterface.STAGE` | `Palette.STAGE` | The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). |
| 368 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 380 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed steps along: a rung a click on the clock's two arrows since 0.7.21, and the stops the speed slider stuck to before. |
| 381 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 402 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 1147 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1545 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1608 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. |
| 1611 | `UserInterface.STRIP_INFLATION_OVER_TARGET` | `.05` | Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. |
| 1614 | `UserInterface.STRIP_DEFLATION_ALARM` | `.10` | Deflation past this reads red, whatever the target: prices collapsing. |
| 1617 | `UserInterface.STRIP_RATE_QUIET` | `.05` | The currency within this of its parity (ForeignAccounts.deviationFromParity) reads quiet, and so does one stronger than parity by any amount. |
| 1620 | `UserInterface.STRIP_RATE_ALARM` | `.25` | Weaker than parity by more than this reads red: a currency well below what its basket is worth abroad is the thing the player should notice. |
| 1623 | `UserInterface.DATE_WIDTH` | `182` | How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over. |
| 1626 | `UserInterface.SPARK_MONTHS` | `120` | How many months a tile's sparkline draws: ten years, or everything recorded if less. |
| 1629 | `UserInterface.SPARK_WIDTH` | `84` | A tile's sparkline at full size, as the mockups draw it. |
| 1630 | `UserInterface.SPARK_HEIGHT` | `30` |  |
| 1633 | `UserInterface.SPARK_MIN` | `30` | Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. |
| 1636 | `UserInterface.TILE_FIGURE` | `17` | The size of a tile's figure, and of its label. |
| 1637 | `UserInterface.TILE_LABEL` | `10.5` |  |
| 2527 | `UserInterface.BACKDROP_BLOCK` | `"#121c28"` | The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. |
| 2528 | `UserInterface.BACKDROP_EDGE` | `"#1d2b3c"` |  |
| 2529 | `UserInterface.BACKDROP_WINDOW` | `"#22344a"` |  |
| 2532 | `UserInterface.FOUNDING_DIM` | `.72` | How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. |
| 2535 | `UserInterface.MENU_CITIES` | `3` | Up to this many of the cities saved last, as cards at the menu's bottom right. |
| 2878 | `UserInterface.MENU_LEFT` | `96` | How far in from the window's left the menu's column and version sit. |
| 2881 | `UserInterface.MENU_BUTTON` | `360` | The menu's buttons' width, as the mockups draw them. |
| 2956 | `UserInterface.CITY_CARD` | `250` | A city card's width, as the mockups draw it. |
| 3005 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3443 | `UserInterface.TIP_WIDTH` | `420` | The width a tooltip's text wraps at, unless it asked for its own. |
| 3630 | `UserInterface.PAGE_FOOT` | `24` | Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). |
| 4340 | `UserInterface.PANEL_TEXT` | `256` | How wide the construction panel's lines wrap: the panel less its padding. |
| 4483 | `UserInterface.RAIL_WIDTH` | `Palette.RAIL` | The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). |
| 4486 | `UserInterface.RAIL_BUTTON` | `54` | A rail button's height, and the least it may shrink to on a short window. |
| 4487 | `UserInterface.RAIL_BUTTON_MIN` | `40` |  |
| 4490 | `UserInterface.RAIL_ICON` | `20` | How big a rail icon is drawn: its 24-unit grid at 20 pixels. |
| 4923 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |
| 5210 | `UserInterface.TOAST_SECONDS` | `8` | How long a toast stays before it fades, in seconds. |
| 5213 | `UserInterface.TOAST_MAX` | `3` | How many toasts at once. |
| 5216 | `UserInterface.TOAST_WIDTH` | `340` | How wide a toast's text wraps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `Game game` | package-private since the split: the screens read it as ui.game |
| 63 | `final BuildScreen buildScreen` |  |
| 64 | `final LandScreen landScreen` |  |
| 65 | `final PeopleScreen peopleScreen` |  |
| 66 | `final ServicesScreen servicesScreen` |  |
| 67 | `final SectorScreen sectorScreen` |  |
| 68 | `final GovernmentScreen governmentScreen` |  |
| 69 | `final FinancesScreen financesScreen` |  |
| 70 | `final BankScreen bankScreen` |  |
| 71 | `final TradeScreen tradeScreen` |  |
| 72 | `final PolicyScreen policyScreen` |  |
| 73 | `final HistoryScreen historyScreen` |  |
| 74 | `final SummaryScreen summaryScreen` | the left panel's content, not a tab |
| 75 | `final FoundingScreen foundingScreen` | the left panel's content, not a tab |
| 76 | `final ConstructionScreen constructionScreen` | New city's screen (0.7.10; Start New Game's until 0.7.21) |
| 122 | `private Stage stage` |  |
| 123 | `VBox rootMenu` |  |
| 126 | `private VBox tabRail` | The navigation rail down the window's left edge (0.7.21; it was the panel's right edge); see start(). |
| 128 | `private StackPane stagePane` | The middle of the window, with the inbox's list and the toasts on top of it. |
| 130 | `private VBox inboxCorner` | The inbox's list, dropped down at the stage's top right while it is open (its envelope is the header's since 0.7.21). |
| 132 | `private VBox toastStack` | The toasts, stacked at the stage's bottom right (0.7.20); see TOASTS. |
| 134 | `private StackPane windowStack` | The whole window, with a dialog laid over it when one is open (0.7.20). |
| 136 | `private StackPane quitDialog` | The dialog that is up - Quit's, or since 0.7.22 a confirmation (confirm()) - or null. |
| 144 | `private boolean cityOpen` | Whether a city has been founded or loaded this session (0.7.20; anotherCity()). |
| 146 | `javafx.scene.control.ScrollPane menuScroller` | The middle column's scroller. |
| 147 | `private VBox constructionPanel` |  |
| 148 | `VBox cityPanel` |  |
| 168 | `int receiptSeen` |  |
| 169 | `boolean receiptOpen` |  |
| 181 | `FadeTransition receiptPulse` | The pulse, held so it can be stopped. |
| 336 | `private HBox header` | Always-visible strips on the two BorderPane edges nothing else uses: the header (0.7.21; the date bar until then) and the debt bar. |
| 337 | `private HBox debtBar` |  |
| 338 | `private Scene scene` |  |
| 383 | `private int speedIndex` | 1x |
| 384 | `private boolean clockRunning` |  |
| 385 | `private double monthProgress` |  |
| 386 | `private long lastFrame` |  |
| 403 | `private double sinceRedraw` |  |
| 404 | `private boolean redrawPending` |  |
| 405 | `private javafx.animation.AnimationTimer clock` |  |
| 408 | `private Label dayLabel` | The date line, kept so a day can be repainted without a whole redraw. |
| 411 | `private String pausedBecause` | Why the clock stopped itself, shown until it is started again. |
| 414 | `private String pausedOnKey` | The notice that last stopped it, so one condition interrupts once. |
| 1110 | `private double pageScrollAt` | Where the player has scrolled the page to. |
| 1113 | `private boolean settlingScroll` | True while a rebuild is in flight, so its clamps are not mistaken for a hand. |
| 1116 | `private boolean correcting` | Guards the re-entry when the listener corrects a clamp of its own. |
| 1150 | `String currentScreen` | Which show*Menu drew what is on screen; see clearMenu. |
| 1153 | `private Runnable redrawScreen` | How to draw it again after a month passes; see clearMenu. |
| 1164 | `private Runnable resumeTo` | The screen Esc was pressed on, so Continue (Resume until 0.7.21) can go back to it. |
| 1192 | `final java.util.Map<String, Double> innerScrollAt` | Where a scroller inside the CURRENT screen was left, by name. |
| 1202 | `private final java.util.Map<String, Double> panelScrollAt` | The same, for the two side panels - and this one is NEVER emptied. |
| 1548 | `private long lastWheelNanos` | When the last wheel event moved the page; see scrollPageBy. |
| 1640 | `private Label monthFigure` | The month's figure in the clock, kept so it pops when a month lands. |
| 1643 | `private Label speedReading` | The speed's reading between its two arrows, kept so a click changes it in place. |
| 1646 | `private int clockAt` | The month the clock last showed, so the month's figure pops only when it moves. |
| 1649 | `private StackPane inboxHolder` | Where the inbox's envelope is drawn in the header, so a notice read elsewhere can redraw its count. |
| 1988 | `private final double gap` |  |
| 2047 | `private final VBox words` |  |
| 2048 | `private final Sparkline spark` |  |
| 2098 | `private final double[] points` |  |
| 2099 | `private final int[] at` |  |
| 2100 | `private final javafx.scene.paint.Color colour` |  |
| 2101 | `private final javafx.scene.canvas.Canvas canvas` |  |
| 2102 | `private double drawnW` |  |
| 2103 | `private int firstMonth, lastMonth` |  |
| 2521 | `private StackPane titleLayer` | The layer the main menu and the founding screen draw on - and, with no city open, Settings, Load and Save (0.7.22): the backdrop, a dimmer, and what the screen puts on it. |
| 2522 | `private javafx.scene.canvas.Canvas backdrop` |  |
| 2523 | `private Region titleDim` |  |
| 2524 | `private StackPane titleContent` |  |
| 2629 | `private Runnable placeMenu` | How the menu's column is placed for the window's height; run again when it changes. |
| 2714 | `private final java.util.Map<Integer, KnownSave> knownSaves` |  |
| 3278 | `GamePrefs prefs` | How the player likes the window. |
| 4819 | `private boolean railJump` | Set for exactly one clearMenu, by goHome(). |
| 4926 | `private boolean inboxOpen` | Whether the list is dropped down. |
| 4929 | `private String inboxExpanded` | Which notice's body is unfolded, by key. |
| 5219 | `private final java.util.Set<String> toasted` | The notices already toasted, as key@raised. |
| 5377 | `private javafx.scene.Node chartFull` | The chart's pane while it has the window, its clock line, and what closes it; null otherwise. |
| 5378 | `private Label chartFullClock` |  |
| 5379 | `private java.util.function.Consumer<Boolean> chartFullLeave` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 5454 | **type** `public class UserInterface extends Application` | The window: the stage and its theme, the header - the clock and the speed, six headline tiles, the rating and the inbox - and the debt bar, the rail down the left, the left panel and the construction panel, the main m... |

### THE SCREENS, one class each since 2026-09-18, in the order the rail (lines 56-77)

### WHERE THE SCREENS WENT. Each line is a run of this file's banner (lines 78-149)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 1 | `public UserInterface()` | JavaFX needs the no-arg one; a harness needs one bound to a city. |
| 120 | 1 | `public UserInterface(Game game)` |  |

### THE RECEIPT INDICATOR (lines 150-182)

### THE THEME (lines 183-339)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 115 | `private void applyTheme(Scene target)` |  |

### THE CLOCK (lines 340-1117)

| line | len | member | says |
|---:|---:|---|---|
| 417 | 539 | `public void start(Stage primaryStage)` |  |
| 962 | 146 | `void clearMenu(String screen, Runnable again)` | Clears the menu area and refreshes the construction panel. |

### A WHEEL NOTCH IS WORTH THE SAME EVERY TIME (lines 1118-1549)

| line | len | member | says |
|---:|---:|---|---|
| 1161 | 1 | `void redraw()` | Draw the screen that is showing, again, in place - the same call the clock makes after a month. |
| 1175 | 10 | `private static boolean isGameMenu(String screen)` | The screens that are ABOUT the game rather than in it. |
| 1205 | 3 | `private javafx.scene.control.ScrollPane keptScroller(String key, javafx.scene.Node content)` | A scroller inside a screen, which remembers where it was while you stay. |
| 1222 | 3 | `private javafx.scene.control.ScrollPane keptScrollerFromBottom(String key, javafx.scene.Node content)` | The same, remembering the distance from the BOTTOM rather than the fraction - for a page whose top half changes height under the player. |
| 1227 | 3 | `javafx.scene.control.ScrollPane keptPanelScroller(String key, javafx.scene.Node content)` | A scroller in one of the side panels, which always remembers. |
| 1238 | 28 | `private javafx.scene.control.ScrollPane remembering(java.util.Map<String, Double> where, String key, javafx.scene.Node content,...` | A ScrollPane that files its own position under a name and comes back to it. |
| 1268 | 6 | `private static double scrollSpan(javafx.scene.control.ScrollPane scroller)` | How far a scroller's content can travel, in pixels, as laid out right now. |
| 1304 | 3 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, double to)` | Put it back, and put it back BEFORE anything is painted. |
| 1313 | 113 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, java.util.function.DoubleSupplier target)` | The same, with the position asked for afresh at each of the three attempts - so a caller keeping pixels rather than a fraction can turn them into a fraction of the height the page actually has by then. |
| 1452 | 35 | `private double pageSpan(javafx.scene.Node page)` | How far the page can travel: what the content WANTS to be, less the viewport. |
| 1497 | 14 | `private void wheelToPage(javafx.scene.input.ScrollEvent wheel)` | A wheel turn nothing else wanted, spent on the page. |
| 1532 | 11 | `private void scrollPageBy(double deltaY, double span)` | Move the page by one wheel event's worth, with a floor under the FIRST event of a gesture and the rest taken as they come. |

### THE HEADER (0.7.21) (lines 1550-2490)

| line | len | member | says |
|---:|---:|---|---|
| 1655 | 12 | `private void refreshHeader()` | The header, rebuilt: the clock, the six tiles, the rating and the inbox. |
| 1676 | 3 | **type** `private record Headline(String label, String area, String value, String valueTone, String change, String ch...` | One tile, as the header draws it. |
| 1681 | 179 | `private List<Headline> headlines()` | The six, in the mockups' order: people, output, prices, work, cash, the price of money. |
| 1870 | 17 | `private String treasuryWhy(double income, double moved)` | The treasury tile's (i): the two figures the net-income dome carried until 0.7.21, and why they differ. |
| 1894 | 32 | `private String moneyWhy(DebtManager market, ForeignAccounts fx, String here)` | The rate tile's (i): the price of money three ways (0.7.4) - Jerus: "the bank rate, the central bank rate, both should be shown, as well as the rate you borrow in" - and the currency both ways, against its parity. |
| 1928 | 34 | `private Region headlineTile(Headline t)` | One tile: its label in its area's colour, the figure, the change, and the sparkline. |
| 1970 | 9 | `private void openHistory(String[] keys)` | Opens City History with these lines picked: the tiles' click. |
| 1987 | 53 | **type** `private static final class HeadlineRow extends javafx.scene.layout.Pane` | The tiles, side by side: equal shares of the row, except that a tile never gets less than its figure needs, and the rest share what is left. |
| 1989 | 1 | `HeadlineRow(double gap)` _(in UserInterface.HeadlineRow)_ |  |
| 1991 | 5 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 1997 | 5 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 2003 | 5 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineRow)_ |  |
| 2009 | 30 | `protected void layoutChildren()` _(in UserInterface.HeadlineRow)_ |  |
| 2046 | 41 | **type** `private static final class HeadlineTile extends Region` | One tile's inside: the words at the left, never narrower than the figure, and the sparkline at the right in what is left, up to SPARK_WIDTH - and none at all under SPARK_MIN. |
| 2050 | 5 | `HeadlineTile(VBox words, Sparkline spark)` _(in UserInterface.HeadlineTile)_ |  |
| 2056 | 3 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2060 | 3 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2064 | 3 | `protected double computeMinHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2068 | 3 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2072 | 14 | `protected void layoutChildren()` _(in UserInterface.HeadlineTile)_ |  |
| 2097 | 80 | **type** `private static final class Sparkline extends Region` | A tile's sparkline: the last SPARK_MONTHS of its series, a month a point, the months recorded as nothing (NaN) left out; a faint fill under the line and a dot on the latest month, in its area's colour. |
| 2105 | 24 | `Sparkline(double[] series, List<Integer> months, String colour)` _(in UserInterface.Sparkline)_ |  |
| 2130 | 46 | `protected void layoutChildren()` _(in UserInterface.Sparkline)_ |  |
| 2184 | 88 | `private HBox clockBlock(boolean inCity)` | The clock, docked at the header's left (0.7.21): the play button, the date, the month's figure and the speed - and why the clock stopped itself, when it did. |
| 2274 | 14 | `private Label speedArrow(String glyph, int step)` | One of the speed's two arrows: a rung down or up the ladder, the reading changed in place. |
| 2290 | 23 | `private HBox ratingAndInbox(boolean inCity)` | The credit rating's chip and the inbox's envelope, at the header's right. |
| 2315 | 7 | `static String ratingColour(String rating)` | A rating's verdict: investment grade is good, BB and B a watch, below that bad. |
| 2338 | 6 | `private static String inflationColour(double inflation, double target)` | Good near the player's target, amber off it, red once prices run past it or collapse. |
| 2346 | 6 | `private static String rateColour(ForeignAccounts fx)` | Secondary grey near parity or stronger, amber weaker, red well below it. |
| 2368 | 95 | `private void refreshDebtBar()` | The next five city debts to come due, and what the city owes altogether. |
| 2465 | 25 | `private VBox maturityChip(Debt debt, int currentMonth)` | One maturity on the bottom strip: amount, type, date, how far off. |

### THE MAIN MENU (0.7.21) (lines 2491-2992)

| line | len | member | says |
|---:|---:|---|---|
| 2541 | 39 | `private void showTitleLayer(boolean show, boolean dim)` | The title layer shown or taken away, and emptied for the screen about to draw on it. |
| 2582 | 1 | `StackPane titleContent()` | Where the main menu and the founding screen put what they draw, and menuPage() its panel (0.7.22). |
| 2595 | 9 | `private static boolean coldMenu(String screen)` | The menu's own screens, which draw over the backdrop while no city is open. |
| 2606 | 21 | `private VBox menuPage()` | Where one of those screens draws: the middle column in a city; a panel over the backdrop, centred and scrolling if it must, when none is open. |
| 2639 | 72 | `static void drawBackdrop(javafx.scene.canvas.GraphicsContext g, double w, double h)` | The backdrop: the city's line rising behind a skyline of flat blocks, a few of their windows lit in the four area colours. |
| 2713 | 1 | **type** `private record KnownSave(long modified, long size, SaveHeader header)` | A save's header, kept while its file is unchanged, so the menu can read eleven slots without parsing eleven files every time it opens. |
| 2717 | 15 | `private SaveHeader headerOf(int slot)` | A slot's header, read again only when its file has changed; null for an empty or unreadable slot. |
| 2734 | 7 | `private static String savedWhen(long savedAt)` | "saved 20:38" today, "saved 29 Sep 20:38" this year, with the year before that. |
| 2742 | 134 | `void showMainMenu()` |  |
| 2884 | 27 | `private Button menuButton(String text, String under, boolean primary)` | One of the menu's buttons: a word, and under the primary one what it goes back to. |
| 2923 | 31 | `private VBox yourCities(double wide)` | YOUR CITIES: the slots saved last, newest first, as cards - the city's name, its slot, its people and its month, and when it was saved. |
| 2959 | 33 | `private VBox cityCard(int slot, SaveHeader header)` | One saved city: its name and slot, its people and month, when it was saved; a click loads it. |

### THE SAVE SYSTEM (lines 2993-3296)

| line | len | member | says |
|---:|---:|---|---|
| 3009 | 31 | `private String slotSummary(int slot)` | One line describing what is in a slot, or that it is empty. |
| 3041 | 13 | `private String slotTitle(int slot)` |  |
| 3056 | 25 | `private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty)` | A slot row: the label on the button, the city underneath it. |
| 3082 | 23 | `private void showSavingMenu()` |  |
| 3112 | 40 | `private void showSaveSlotConfirm(int slot)` | Confirms one slot, and takes the optional name. |
| 3153 | 21 | `private void showLoadMenu()` |  |
| 3175 | 25 | `private void loadSlot(int slot)` |  |
| 3208 | 10 | `private void openCity()` | Where a city opens. |
| 3228 | 6 | `private void anotherCity()` | Another city has just replaced the one on screen - founded, or loaded (0.7.20): its toasts and its purchases are not this one's. |
| 3236 | 5 | `void foundCity(Founding choices)` | The founding screen's last step: found the city as chosen, and open it. |
| 3242 | 34 | `private void showSaveResult(GameFiles.Result result)` |  |
| 3287 | 9 | `private void toggleFullScreen()` | In and out of full screen, and remembered. |

### QUIT ASKS FIRST (0.7.20) (lines 3297-3352)

| line | len | member | says |
|---:|---:|---|---|
| 3313 | 32 | `private void showQuitDialog()` |  |
| 3347 | 5 | `private void closeQuitDialog()` | Close the dialog that is up, Quit's or a confirmation: Cancel, or Esc. |

### A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) (lines 3353-3422)

| line | len | member | says |
|---:|---:|---|---|
| 3365 | 57 | `void confirm(String ask, String why, java.util.List<String[]> lines, String yesText, Runnable yes)` |  |

### EVERY TOOLTIP, DRESSED ONCE (0.7.20) (lines 3423-3465)

| line | len | member | says |
|---:|---:|---|---|
| 3445 | 13 | `private void dressTooltips()` |  |
| 3460 | 5 | `private static void hideTooltips()` | Every tooltip showing, put away - so one is not left hanging over the menu Esc opens. |

### SETTINGS. (lines 3466-3579)

| line | len | member | says |
|---:|---:|---|---|
| 3474 | 65 | `private void showSettingsMenu()` |  |
| 3550 | 29 | `private VBox toggleRow(String label, boolean on, String what, Runnable flip)` | A setting: what it is, what it does, and a switch that says which way it is set without having to read the word next to it. |

### THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. (lines 3580-3602)

### ECONOMY IS GONE, AND IT SPLIT IN TWO. (lines 3603-3619)

### THE SCROLLER (lines 3620-3690)

| line | len | member | says |
|---:|---:|---|---|
| 3633 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column)` | A left-aligned column inside a scroll pane, which these screens all want. |
| 3644 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome)` | this scroller - a title alone is about 150; a title with a vitals bar and two strips pinned over it is a good deal more, and getting it wrong is a scrollbar that appears when there is nothing to scroll. |
| 3653 | 37 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome, boolean fromBottom)` | The same, with the position kept from the bottom of the page instead of as a fraction (fromBottom) - for a page that grows and shrinks ABOVE the controls the player is using; see keptScrollerFromBottom. |

### WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and (lines 3691-3701)

| line | len | member | says |
|---:|---:|---|---|
| 3697 | 1 | `public List<String> whatItDoes(BuildingsTemplate t)` |  |
| 3698 | 1 | `public List<String> whatCareItGives(BuildingsTemplate t)` |  |
| 3699 | 1 | `public String jobLabel(JobType job)` |  |

### SIMULATE MULTIPLE MONTHS (lines 3702-4130)

| line | len | member | says |
|---:|---:|---|---|
| 3710 | 53 | `private void showSimulateMonthsMenu()` |  |
| 3775 | 222 | `private void showSimulateResultMenu(int requested, int completed)` | What happened while the player was not watching. |
| 3999 | 10 | `private void addSkipLine(VBox section, String label, double start, double end, double change, boolean isMoney)` | "Population  192 -> 664  (+472)", coloured by direction. |
| 4018 | 22 | `private void addChangeLine(VBox section, String label, double change, boolean isMoney, boolean higherIsBetter)` | A signed change, coloured by whether it is good news. |
| 4042 | 3 | `void showSectorReport(String title, VBox column, Runnable back)` | Shared scaffolding for the sector report screens. |
| 4052 | 77 | `void showSectorReport(String title, VBox column, Runnable back, Button extra)` | somewhere else - e.g. the industrial report linking to its financial statements. |

### CONSTRUCTION PANEL (lines 4131-4464)

| line | len | member | says |
|---:|---:|---|---|
| 4149 | 179 | `private void refreshConstructionPanel()` |  |
| 4330 | 8 | `static Region areaSwatch(String colour, double size)` | A small square in an area's colour, before a heading (0.7.21): the pages' titles' swatch, smaller. |
| 4352 | 54 | `private void addDemolitionLog()` | What the city has lost lately, under what it is building. |
| 4419 | 44 | `private void addBuildLog()` | What the city has GAINED lately, above what it has lost. |

### THE RAIL (lines 4465-4715)

| line | len | member | says |
|---:|---:|---|---|
| 4502 | 1 | **type** `private record Tab(String key, String svg, String label, String name, Runnable go)` | One destination. |
| 4509 | 14 | `static String areaOf(String key)` | Which of the four areas a rail key is in (0.7.21): building and land, people and services, business and trade, money and policy. |
| 4530 | 15 | `Label pageTitle(String text)` | A page's title, with its area's swatch before it (0.7.21): a small square in the colour of the rail tab that owns the screen showing, as the mockups set the title. |
| 4576 | 54 | `private Tab[] tabs()` | The rail, in the order a city is actually run. |
| 4641 | 49 | `private String tabFor(String screen)` | Which tab owns the screen that is showing. |
| 4691 | 24 | `private void refreshTabRail()` |  |

### THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF (lines 4716-4898)

| line | len | member | says |
|---:|---:|---|---|
| 4764 | 5 | `private void goHome(Tab tab)` | Press a tab: forget where you were inside it - Build's category apart, since 0.7.20 - and land at the top. |
| 4790 | 20 | `private void resetSection(String key)` | A section's own idea of where you were, forgotten. |
| 4836 | 62 | `private VBox railButton(String svg, String label, String name, String area, boolean active, Runnable go)` | One button on the rail: its icon over its name (0.7.21). |

### THE INBOX (lines 4899-4995)

| line | len | member | says |
|---:|---:|---|---|
| 4931 | 64 | `private void refreshInbox()` |  |

### the envelope (lines 4996-5186)

| line | len | member | says |
|---:|---:|---|---|
| 5003 | 48 | `private void refreshInboxButton()` |  |
| 5065 | 58 | `private VBox noticeRow(Notice notice)` | One notice: its title, and its body when it is unfolded. |
| 5124 | 14 | `private String dealLabel(String key)` |  |
| 5148 | 38 | `private void deal(Notice notice)` | Take the player to the control that answers it. |

### TOASTS (0.7.20) (lines 5187-5269)

| line | len | member | says |
|---:|---:|---|---|
| 5222 | 13 | `private void refreshToasts()` | New urgent notices become toasts; read or settled ones leave the stack. |
| 5237 | 32 | `private Label toast(Notice notice)` | One toast: the notice's title, a timer, and a door to the inbox. |

### TIME, AND WHAT THE MONTH IS WORTH (lines 5270-5363)

| line | len | member | says |
|---:|---:|---|---|
| 5289 | 61 | `private void startClock()` | Starts the frame loop. |
| 5358 | 5 | `private void paintDay()` | The day, repainted in place. |

### THE CHART OVER THE WHOLE WINDOW (0.7.23) (lines 5364-5472)

| line | len | member | says |
|---:|---:|---|---|
| 5387 | 9 | `void showChartFullScreen(Region pane, Label clockLine, java.util.function.Consumer<Boolean> leave)` | Lays the chart's pane over the whole window. |
| 5398 | 6 | `void closeChartFullScreen()` | Takes the pane off the window; the screen redraws its page. |
| 5406 | 4 | `private void leaveChartFullScreen(boolean redraw)` | Esc, P or another screen: the screen's own way back, which calls closeChartFullScreen(). |
| 5412 | 1 | `boolean isChartFullScreen()` | Whether a chart has the whole window. |
| 5415 | 1 | `boolean isShowing(String screen)` | Whether this screen - a clearMenu() name, "showHistoryMenu" - is the one on show: a late redraw asks before it draws. |
| 5418 | 4 | `String clockWords()` | The date and what the clock is doing, for a line that stands in for the header: "14 February 2151 · paused". |
| 5424 | 8 | `private static boolean overAChart(Object target)` | Whether a wheel event's target is inside a chart that takes the wheel (TimeChart.WHEEL_OWNER). |
| 5446 | 11 | `private boolean stopIfSomethingHappened()` | Stops the clock when the city has something to say, if the player wants that. |
| 5459 | 7 | `private void setClockRunning(boolean run)` | Play, or pause. |
| 5468 | 4 | `private static String speedLabel(int index)` | "0.25×", "1×", "10×" - no trailing zeros on the round ones; the header's speed reads it (0.7.21: "×", the mockups', for "x"). |

### TWELVE PIPS, AND ONE OF THEM MOVED. (lines 5473-5505)

| line | len | member | says |
|---:|---:|---|---|
| 5494 | 11 | `private static void popPip(Region pip)` | A quarter second of "that landed", on the figure the month just changed. |

