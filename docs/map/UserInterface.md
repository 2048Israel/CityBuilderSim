# UserInterface.java - 6,020 lines · 134 methods · 42 constants · interface

`ham/citybuildersim/ui/UserInterface.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

> The window: the stage and its theme, the header - the clock and the speed,
> the money block, five headline tiles, the "Needs you" chip, the rating and
> the inbox - the rail down the left, the City overview's drawer and the
> construction panel with its tab (0.7.24), the main menu over its backdrop,
> the save and settings dialogs, the time-skip dialog, and the scroller every
> screen draws into. Everything a tab SHOWS is a class of
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

**Uses:** [Palette](Palette.md) (342), [Icons](Icons.md) (20), [GameFiles](GameFiles.md) (12), [SaveHeader](SaveHeader.md) (10), [PeopleScreen](PeopleScreen.md) (9), [CityCalendar](CityCalendar.md) (8), [Notice](Notice.md) (8), [BuildScreen](BuildScreen.md) (6), [FoundingScreen](FoundingScreen.md) (6), [Pieces](Pieces.md) (6), [Currency](Currency.md) (6), [Game](Game.md) (5), [DebtManager](DebtManager.md) (5), [DemolitionLog](DemolitionLog.md) (5), [ServicesScreen](ServicesScreen.md) (4), [GameVersion](GameVersion.md) (4), [GamePrefs](GamePrefs.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [SectorScreen](SectorScreen.md) (3), [FinancesScreen](FinancesScreen.md) (3), [BankScreen](BankScreen.md) (3), [TradeScreen](TradeScreen.md) (3), [PolicyScreen](PolicyScreen.md) (3), [SummaryScreen](SummaryScreen.md) (3), [ConstructionScreen](ConstructionScreen.md) (3), [TimeSkipReport](TimeSkipReport.md) (3), [CityNeeds](CityNeeds.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [ConstructionControl](ConstructionControl.md) (3), [LandScreen](LandScreen.md) (2)... and 24 more

**Used by (20):** [BankScreen](BankScreen.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [CityBuilderSim](CityBuilderSim.md), [ConstructionScreen](ConstructionScreen.md), [FinancesScreen](FinancesScreen.md), [FoundingScreen](FoundingScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 57 | · THE SCREENS, one class each since 2026-09-18, in the order the rail |
| 80 | · WHERE THE SCREENS WENT. Each line is a run of this file's banner |
| 212 | THE RECEIPT INDICATOR |
| 245 | THE THEME |
| 401 | THE CLOCK |
| 599 | · THE MIDDLE COLUMN SCROLLS NOW, and it should have from the start. |
| 648 | · WHERE THE PAGE IS SCROLLED TO IS A FACT, NOT SOMETHING RECAPTURED. |
| 700 | · THE RAIL, AND WHY IT WAS PART OF THE PANEL. |
| 738 | · THE STAGE IS A STACK NOW. |
| 798 | · AND THE TIME CONTROLS DID NOT FLOAT, WHICH WAS A CORRECTION. |
| 814 | · THE WHEEL WORKS WHERE THE POINTER ALREADY IS. |
| 867 | · FULL SCREEN, AND THE TWO THINGS JAVAFX DOES ABOUT IT THAT WE DO NOT |
| 1117 | · AND THE SCROLL STAYS WHERE YOU PUT IT. |
| 1186 | · HOLD THE PAGE'S HEIGHT WHILE IT IS REBUILT, so the position is |
| 1275 | A WHEEL NOTCH IS WORTH THE SAME EVERY TIME |
| 1707 | THE HEADER (0.7.21) |
| 1875 | · · POPULATION, and the month's net |
| 1917 | · · GDP, annualised from month 1, and real growth |
| 1950 | · · INFLATION, against the player's target |
| 2004 | · · OUT OF WORK, with a shortage read as watch |
| 2028 | · · RATE and the currency: the price of money |
| 2094 | · THE MONEY BLOCK (0.7.24) |
| 2709 | THE MAIN MENU (0.7.21) |
| 2982 | · WHAT "CONTINUE" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO CONTINUE |
| 3211 | THE SAVE SYSTEM |
| 3522 | QUIT ASKS FIRST (0.7.20) |
| 3578 | A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) |
| 3648 | EVERY TOOLTIP, DRESSED ONCE (0.7.20) |
| 3691 | SETTINGS. |
| 3805 | THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. |
| 3828 | ECONOMY IS GONE, AND IT SPLIT IN TWO. |
| 3845 | THE SCROLLER |
| 3916 | · WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and |
| 3940 | SIMULATE MULTIPLE MONTHS |
| 4051 | · · headlines |
| 4067 | · · deltas |
| 4117 | · · land |
| 4125 | · · buildings |
| 4141 | · · demolitions |
| 4165 | · · health |
| 4209 | · · households |
| 4369 | THE FRAME FOLDS AWAY (0.7.24) |
| 4578 | CONSTRUCTION PANEL |
| 4914 | THE RAIL |
| 5172 | THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF |
| 5356 | THE INBOX |
| 5396 | · · what an urgent notice gets |
| 5406 | · · the list |
| 5453 | · the envelope |
| 5644 | TOASTS (0.7.20) |
| 5727 | TIME, AND WHAT THE MONTH IS WORTH |
| 5879 | THE CHART OVER THE WHOLE WINDOW (0.7.23) |
| 5988 | TWELVE PIPS, AND ONE OF THEM MOVED. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 279 | `UserInterface.STAGE` | `Palette.STAGE` | The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). |
| 429 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 441 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed steps along: a rung a click on the clock's two arrows since 0.7.21, and the stops the speed slider stuck to before. |
| 442 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 463 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 489 | `UserInterface.PRESS_HELD` | `"UserInterface.pressHeld"` | The scene property that says a button is down (0.7.40), for what waits on a timer outside this class (TimeChart's settle). |
| 1304 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1702 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1777 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. |
| 1780 | `UserInterface.STRIP_INFLATION_OVER_TARGET` | `.05` | Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. |
| 1783 | `UserInterface.STRIP_DEFLATION_ALARM` | `.10` | Deflation past this reads red, whatever the target: prices collapsing. |
| 1795 | `UserInterface.DATE_WIDTH` | `164` | How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over (0.7.24: at 17 px, so the money block and five tiles fit a 1,280 w... |
| 1798 | `UserInterface.DATE_SIZE` | `17` | The date's size in the clock (0.7.24; it was 19). |
| 1801 | `UserInterface.SPARK_MONTHS` | `120` | How many months a tile's sparkline draws: ten years, or everything recorded if less. |
| 1804 | `UserInterface.SPARK_WIDTH` | `72` | A tile's sparkline at full size: on the label's row since 0.7.24, as the mockups draw it (it was 84 by 30, beside the words). |
| 1806 | `UserInterface.SPARK_HEIGHT` | `16` | ...and its height, the label's row (0.7.24; it was 30). |
| 1809 | `UserInterface.SPARK_MIN` | `30` | Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. |
| 1812 | `UserInterface.TILE_FIGURE` | `15` | The size of a tile's figure (0.7.24: 15, so the money block and five tiles fit a 1,280 window whole; it was 17). |
| 1814 | `UserInterface.TILE_LABEL` | `10.5` | The size of a tile's label, and of the money block's. |
| 1816 | `UserInterface.TILE_CHANGE` | `10.5` | The size of a tile's change line (0.7.24; it was 11). |
| 2176 | `UserInterface.MONEY_FIGURE` | `28` | The cash in the money block: the mockups' 28 px. |
| 2745 | `UserInterface.BACKDROP_BLOCK` | `"#121c28"` | The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. |
| 2746 | `UserInterface.BACKDROP_EDGE` | `"#1d2b3c"` |  |
| 2747 | `UserInterface.BACKDROP_WINDOW` | `"#22344a"` |  |
| 2750 | `UserInterface.FOUNDING_DIM` | `.72` | How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. |
| 2753 | `UserInterface.MENU_CITIES` | `3` | Up to this many of the cities saved last, as cards at the menu's bottom right. |
| 3096 | `UserInterface.MENU_LEFT` | `96` | How far in from the window's left the menu's column and version sit. |
| 3099 | `UserInterface.MENU_BUTTON` | `360` | The menu's buttons' width, as the mockups draw them. |
| 3174 | `UserInterface.CITY_CARD` | `250` | A city card's width, as the mockups draw it. |
| 3223 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3668 | `UserInterface.TIP_WIDTH` | `420` | The width a tooltip's text wraps at, unless it asked for its own. |
| 3855 | `UserInterface.PAGE_FOOT` | `24` | Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). |
| 4531 | `UserInterface.CONSTRUCTION_TAB` | `44` | How wide the construction panel's tab is. |
| 4789 | `UserInterface.PANEL_TEXT` | `256` | How wide the construction panel's lines wrap: the panel less its padding. |
| 4932 | `UserInterface.RAIL_WIDTH` | `Palette.RAIL` | The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). |
| 4935 | `UserInterface.RAIL_BUTTON` | `54` | A rail button's height, and the least it may shrink to on a short window. |
| 4936 | `UserInterface.RAIL_BUTTON_MIN` | `40` |  |
| 4939 | `UserInterface.RAIL_ICON` | `20` | How big a rail icon is drawn: its 24-unit grid at 20 pixels. |
| 5380 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |
| 5667 | `UserInterface.TOAST_SECONDS` | `8` | How long a toast stays before it fades, in seconds. |
| 5670 | `UserInterface.TOAST_MAX` | `3` | How many toasts at once. |
| 5673 | `UserInterface.TOAST_WIDTH` | `340` | How wide a toast's text wraps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `Game game` | package-private since the split: the screens read it as ui.game |
| 64 | `final BuildScreen buildScreen` |  |
| 65 | `final LandScreen landScreen` |  |
| 66 | `final PeopleScreen peopleScreen` |  |
| 67 | `final ServicesScreen servicesScreen` |  |
| 68 | `final InfrastructureScreen infrastructureScreen` | the Infrastructure tab (0.7.28; ServicesScreen's until then) |
| 69 | `final SectorScreen sectorScreen` | the Infrastructure tab (0.7.28; ServicesScreen's until then) |
| 70 | `final GovernmentScreen governmentScreen` |  |
| 71 | `final FinancesScreen financesScreen` |  |
| 72 | `final BankScreen bankScreen` |  |
| 73 | `final TradeScreen tradeScreen` |  |
| 74 | `final PolicyScreen policyScreen` |  |
| 75 | `final HistoryScreen historyScreen` |  |
| 76 | `final SummaryScreen summaryScreen` | the left panel's content, not a tab |
| 77 | `final FoundingScreen foundingScreen` | the left panel's content, not a tab |
| 78 | `final ConstructionScreen constructionScreen` | New city's screen (0.7.10; Start New Game's until 0.7.21) |
| 176 | `private Stage stage` |  |
| 177 | `VBox rootMenu` |  |
| 180 | `private VBox tabRail` | The navigation rail down the window's left edge (0.7.21; it was the panel's right edge); see start(). |
| 182 | `private StackPane stagePane` | The middle of the window, with the City overview's drawer and the construction panel (both since 0.7.24), the inbox's list and the toasts on top of it. |
| 184 | `private VBox inboxCorner` | The inbox's list, dropped down at the stage's top right while it is open (its envelope is the header's since 0.7.21). |
| 186 | `private VBox toastStack` | The toasts, stacked at the stage's bottom right (0.7.20); see TOASTS. |
| 188 | `private StackPane windowStack` | The whole window, with a dialog laid over it when one is open (0.7.20). |
| 190 | `private StackPane quitDialog` | The dialog that is up - Quit's, or since 0.7.22 a confirmation (confirm()) - or null. |
| 198 | `private boolean cityOpen` | Whether a city has been founded or loaded this session (0.7.20; anotherCity()). |
| 200 | `javafx.scene.control.ScrollPane menuScroller` | The middle column's scroller. |
| 202 | `private VBox constructionPanel` | The construction panel: over the stage's right edge while open, folded to constructionTab otherwise (0.7.24). |
| 204 | `private VBox constructionTab` | The slim tab on the stage's right edge the construction panel folds to (0.7.24). |
| 206 | `VBox cityPanel` | The City overview's content, drawn by SummaryScreen. |
| 208 | `private VBox drawer` | The drawer the City overview opens in, over the stage's left (0.7.24); see THE FRAME FOLDS AWAY. |
| 210 | `private boolean drawerOpen` | Whether the drawer is open. |
| 230 | `int receiptSeen` |  |
| 231 | `boolean receiptOpen` |  |
| 243 | `FadeTransition receiptPulse` | The pulse, held so it can be stopped. |
| 398 | `private HBox header` | The always-visible strip on the BorderPane's top edge: the header (0.7.21; the date bar until then). |
| 399 | `private Scene scene` |  |
| 444 | `private int speedIndex` | 1x |
| 445 | `private boolean clockRunning` |  |
| 446 | `private double monthProgress` |  |
| 447 | `private long lastFrame` |  |
| 464 | `private double sinceRedraw` |  |
| 465 | `private boolean redrawPending` |  |
| 466 | `private javafx.animation.AnimationTimer clock` |  |
| 482 | `private boolean pressHeld` | A PRESS IS NEVER REBUILT AWAY (0.7.40). |
| 484 | `private final java.util.EnumSet<javafx.scene.input.MouseButton> buttonsDown` | The buttons down, each counted from its press to its release, so one let go while another is held still holds. |
| 487 | `private boolean letGo` | Set when a release has been delivered, and spent by the next frame: the first frame after a release. |
| 492 | `private Label dayLabel` | The date line, kept so a day can be repainted without a whole redraw. |
| 495 | `private String pausedBecause` | Why the clock stopped itself, shown until it is started again. |
| 498 | `private String pausedOnKey` | The notice that last stopped it, so one condition interrupts once. |
| 1267 | `private double pageScrollAt` | Where the player has scrolled the page to. |
| 1270 | `private boolean settlingScroll` | True while a rebuild is in flight, so its clamps are not mistaken for a hand. |
| 1273 | `private boolean correcting` | Guards the re-entry when the listener corrects a clamp of its own. |
| 1307 | `String currentScreen` | Which show*Menu drew what is on screen; see clearMenu. |
| 1310 | `private Runnable redrawScreen` | How to draw it again after a month passes; see clearMenu. |
| 1321 | `private Runnable resumeTo` | The screen Esc was pressed on, so Continue (Resume until 0.7.21) can go back to it. |
| 1349 | `final java.util.Map<String, Double> innerScrollAt` | Where a scroller inside the CURRENT screen was left, by name. |
| 1359 | `private final java.util.Map<String, Double> panelScrollAt` | The same, for the two side panels - and this one is NEVER emptied. |
| 1705 | `private long lastWheelNanos` | When the last wheel event moved the page; see scrollPageBy. |
| 1819 | `private Label monthFigure` | The month's figure in the clock, kept so it pops when a month lands. |
| 1822 | `private Label speedReading` | The speed's reading between its two arrows, kept so a click changes it in place. |
| 1825 | `private int clockAt` | The month the clock last showed, so the month's figure pops only when it moves. |
| 1828 | `private StackPane inboxHolder` | Where the inbox's envelope is drawn in the header, so a notice read elsewhere can redraw its count. |
| 2290 | `private final double gap` |  |
| 2353 | `private final HBox labelRow` |  |
| 2354 | `private final Label figure` |  |
| 2355 | `private final List<Label> changes` |  |
| 2356 | `private final Sparkline spark` |  |
| 2441 | `private final double[] points` |  |
| 2442 | `private final int[] at` |  |
| 2443 | `private final javafx.scene.paint.Color colour` |  |
| 2444 | `private final javafx.scene.canvas.Canvas canvas` |  |
| 2445 | `private double drawnW` |  |
| 2446 | `private int firstMonth, lastMonth` |  |
| 2739 | `private StackPane titleLayer` | The layer the main menu and the founding screen draw on - and, with no city open, Settings, Load and Save (0.7.22): the backdrop, a dimmer, and what the screen puts on it. |
| 2740 | `private javafx.scene.canvas.Canvas backdrop` |  |
| 2741 | `private Region titleDim` |  |
| 2742 | `private StackPane titleContent` |  |
| 2847 | `private Runnable placeMenu` | How the menu's column is placed for the window's height; run again when it changes. |
| 2932 | `private final java.util.Map<Integer, KnownSave> knownSaves` |  |
| 3503 | `GamePrefs prefs` | How the player likes the window. |
| 4401 | `private Region needsChipNode` | The chip, kept so a click outside the drawer can repaint it without rebuilding the header under the pointer. |
| 4402 | `private Runnable restyleNeedsChip` |  |
| 4410 | `private HBox drawerBarBox` | The drawer's own bar: the pin and the close, refilled as the pin changes. |
| 5276 | `private boolean railJump` | Set for exactly one clearMenu, by goHome(). |
| 5383 | `private boolean inboxOpen` | Whether the list is dropped down. |
| 5386 | `private String inboxExpanded` | Which notice's body is unfolded, by key. |
| 5676 | `private final java.util.Set<String> toasted` | The notices already toasted, as key@raised. |
| 5892 | `private javafx.scene.Node chartFull` | The chart's pane while it has the window, its clock line, and what closes it; null otherwise. |
| 5893 | `private Label chartFullClock` |  |
| 5894 | `private java.util.function.Consumer<Boolean> chartFullLeave` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 5968 | **type** `public class UserInterface extends Application` | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating and the inbox - the rail down the left, the City overview's drawer and ... |

### THE SCREENS, one class each since 2026-09-18, in the order the rail (lines 57-79)

### WHERE THE SCREENS WENT. Each line is a run of this file's banner (lines 80-211)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 1 | `public UserInterface()` | JavaFX needs the no-arg one; a harness needs one bound to a city. |
| 174 | 1 | `public UserInterface(Game game)` |  |

### THE RECEIPT INDICATOR (lines 212-244)

### THE THEME (lines 245-400)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 115 | `private void applyTheme(Scene target)` |  |

### THE CLOCK (lines 401-1274)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 600 | `public void start(Stage primaryStage)` |  |
| 1107 | 158 | `void clearMenu(String screen, Runnable again)` | Clears the menu area and refreshes the construction panel. |

### A WHEEL NOTCH IS WORTH THE SAME EVERY TIME (lines 1275-1706)

| line | len | member | says |
|---:|---:|---|---|
| 1318 | 1 | `void redraw()` | Draw the screen that is showing, again, in place - the same call the clock makes after a month. |
| 1332 | 10 | `private static boolean isGameMenu(String screen)` | The screens that are ABOUT the game rather than in it. |
| 1362 | 3 | `private javafx.scene.control.ScrollPane keptScroller(String key, javafx.scene.Node content)` | A scroller inside a screen, which remembers where it was while you stay. |
| 1379 | 3 | `private javafx.scene.control.ScrollPane keptScrollerFromBottom(String key, javafx.scene.Node content)` | The same, remembering the distance from the BOTTOM rather than the fraction - for a page whose top half changes height under the player. |
| 1384 | 3 | `javafx.scene.control.ScrollPane keptPanelScroller(String key, javafx.scene.Node content)` | A scroller in one of the side panels, which always remembers. |
| 1395 | 28 | `private javafx.scene.control.ScrollPane remembering(java.util.Map<String, Double> where, String key, javafx.scene.Node content,...` | A ScrollPane that files its own position under a name and comes back to it. |
| 1425 | 6 | `private static double scrollSpan(javafx.scene.control.ScrollPane scroller)` | How far a scroller's content can travel, in pixels, as laid out right now. |
| 1461 | 3 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, double to)` | Put it back, and put it back BEFORE anything is painted. |
| 1470 | 113 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, java.util.function.DoubleSupplier target)` | The same, with the position asked for afresh at each of the three attempts - so a caller keeping pixels rather than a fraction can turn them into a fraction of the height the page actually has by then. |
| 1609 | 35 | `private double pageSpan(javafx.scene.Node page)` | How far the page can travel: what the content WANTS to be, less the viewport. |
| 1654 | 14 | `private void wheelToPage(javafx.scene.input.ScrollEvent wheel)` | A wheel turn nothing else wanted, spent on the page. |
| 1689 | 11 | `private void scrollPageBy(double deltaY, double span)` | Move the page by one wheel event's worth, with a floor under the FIRST event of a gesture and the rest taken as they come. |

### THE HEADER (0.7.21) (lines 1707-2093)

| line | len | member | says |
|---:|---:|---|---|
| 1835 | 14 | `private void refreshHeader()` | The header, rebuilt: the clock, the money block, the five tiles, the "Needs you" chip, the rating and the inbox. |
| 1858 | 3 | **type** `private record Headline(String label, String area, String value, String valueTone, String[] change, String ...` | One tile, as the header draws it. |
| 1867 | 1 | `private static String[] words(String...longestFirst)` | A change line, longest wording first: the tile shows the longest that fits its width (HeadlineTile), so a line is shortened and never cut (0.7.24). |
| 1870 | 180 | `private List<Headline> headlines()` | The five, in the mockups' order: people, output, prices, work, the price of money - TREASURY is its own block since 0.7.24 (moneyBlock()). |
| 2063 | 30 | `private String treasuryWhy(double income, double moved)` | The money block's (i) - the TREASURY tile's until 0.7.24: the three figures, EARNED, SURPLUS and BANKED (0.7.31's names, the same on the Government tab), and why they differ. |

### THE MONEY BLOCK (0.7.24) (lines 2094-2708)

| line | len | member | says |
|---:|---:|---|---|
| 2122 | 52 | `private Region moneyBlock()` | The cash at its own size, what the month EARNED under it (not how far the month moved the cash: see above), and a door to Finances. |
| 2186 | 32 | `private String moneyWhy(DebtManager market, ForeignAccounts fx, String here)` | The rate tile's (i): the price of money three ways (0.7.4) - Jerus: "the bank rate, the central bank rate, both should be shown, as well as the rate you borrow in" - and the currency both ways, against its parity. |
| 2227 | 38 | `private Region headlineTile(Headline t)` | One tile (0.7.24, the mockups' shape): its label in its area's colour and a small sparkline on the top row, the figure under them, and the change line across the tile's whole width - in the longest of its wordings tha... |
| 2275 | 4 | `private void openHistory(String[] keys)` | Opens City History with these lines picked: the tiles' click. |
| 2289 | 53 | **type** `private static final class HeadlineRow extends javafx.scene.layout.Pane` | The tiles, side by side: equal shares of the row, except that a tile never gets less than its least width - its figure until 0.7.24, the widest of its label, its figure and its shortest change line since (HeadlineTile... |
| 2291 | 1 | `HeadlineRow(double gap)` _(in UserInterface.HeadlineRow)_ |  |
| 2293 | 5 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 2299 | 5 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 2305 | 5 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineRow)_ |  |
| 2311 | 30 | `protected void layoutChildren()` _(in UserInterface.HeadlineRow)_ |  |
| 2352 | 78 | **type** `private static final class HeadlineTile extends Region` | One tile's inside (0.7.24): the label at the top left and the sparkline at the top right in what the label leaves, up to SPARK_WIDTH - none at all under SPARK_MIN; the figure under them; and the change line across the... |
| 2358 | 8 | `HeadlineTile(HBox labelRow, Label figure, List<Label> changes, Sparkline spark)` _(in UserInterface.HeadlineTile)_ |  |
| 2367 | 8 | `private double shortest()` _(in UserInterface.HeadlineTile)_ |  |
| 2376 | 3 | `private double changeHeight()` _(in UserInterface.HeadlineTile)_ |  |
| 2380 | 4 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2385 | 5 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2391 | 3 | `protected double computeMinHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2395 | 4 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2400 | 29 | `protected void layoutChildren()` _(in UserInterface.HeadlineTile)_ |  |
| 2440 | 80 | **type** `static final class Sparkline extends Region` | A tile's sparkline: the last SPARK_MONTHS of its series, a month a point, the months recorded as nothing (NaN) left out; a faint fill under the line and a dot on the latest month, in its area's colour. |
| 2448 | 24 | `Sparkline(double[] series, List<Integer> months, String colour)` _(in UserInterface.Sparkline)_ |  |
| 2473 | 46 | `protected void layoutChildren()` _(in UserInterface.Sparkline)_ |  |
| 2527 | 88 | `private HBox clockBlock(boolean inCity)` | The clock, docked at the header's left (0.7.21): the play button, the date, the month's figure and the speed - and why the clock stopped itself, when it did. |
| 2617 | 14 | `private Label speedArrow(String glyph, int step)` | One of the speed's two arrows: a rung down or up the ladder, the reading changed in place. |
| 2633 | 27 | `private HBox ratingAndInbox(boolean inCity)` | The "Needs you" chip, the credit rating's chip and the inbox's envelope, at the header's right. |
| 2662 | 7 | `static String ratingColour(String rating)` | A rating's verdict: investment grade is good, BB and B a watch, below that bad. |
| 2685 | 6 | `private static String inflationColour(double inflation, double target)` | Good near the player's target, amber off it, red once prices run past it or collapse. |
| 2693 | 5 | `private static String rateColour(ForeignAccounts fx)` | Secondary grey near parity, amber past ForeignAccounts.PARITY_WATCH either side, red past PARITY_FAR (0.7.35: the one parity rule; a pinned rate is grey). |

### THE MAIN MENU (0.7.21) (lines 2709-3210)

| line | len | member | says |
|---:|---:|---|---|
| 2759 | 39 | `private void showTitleLayer(boolean show, boolean dim)` | The title layer shown or taken away, and emptied for the screen about to draw on it. |
| 2800 | 1 | `StackPane titleContent()` | Where the main menu and the founding screen put what they draw, and menuPage() its panel (0.7.22). |
| 2813 | 9 | `private static boolean coldMenu(String screen)` | The menu's own screens, which draw over the backdrop while no city is open. |
| 2824 | 21 | `private VBox menuPage()` | Where one of those screens draws: the middle column in a city; a panel over the backdrop, centred and scrolling if it must, when none is open. |
| 2857 | 72 | `static void drawBackdrop(javafx.scene.canvas.GraphicsContext g, double w, double h)` | The backdrop: the city's line rising behind a skyline of flat blocks, a few of their windows lit in the four area colours. |
| 2931 | 1 | **type** `private record KnownSave(long modified, long size, SaveHeader header)` | A save's header, kept while its file is unchanged, so the menu can read eleven slots without parsing eleven files every time it opens. |
| 2935 | 15 | `private SaveHeader headerOf(int slot)` | A slot's header, read again only when its file has changed; null for an empty or unreadable slot. |
| 2952 | 7 | `private static String savedWhen(long savedAt)` | "saved 20:38" today, "saved 29 Sep 20:38" this year, with the year before that. |
| 2960 | 134 | `void showMainMenu()` |  |
| 3102 | 27 | `private Button menuButton(String text, String under, boolean primary)` | One of the menu's buttons: a word, and under the primary one what it goes back to. |
| 3141 | 31 | `private VBox yourCities(double wide)` | YOUR CITIES: the slots saved last, newest first, as cards - the city's name, its slot, its people and its month, and when it was saved. |
| 3177 | 33 | `private VBox cityCard(int slot, SaveHeader header)` | One saved city: its name and slot, its people and month, when it was saved; a click loads it. |

### THE SAVE SYSTEM (lines 3211-3521)

| line | len | member | says |
|---:|---:|---|---|
| 3227 | 31 | `private String slotSummary(int slot)` | One line describing what is in a slot, or that it is empty. |
| 3259 | 13 | `private String slotTitle(int slot)` |  |
| 3274 | 25 | `private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty)` | A slot row: the label on the button, the city underneath it. |
| 3300 | 23 | `private void showSavingMenu()` |  |
| 3330 | 40 | `private void showSaveSlotConfirm(int slot)` | Confirms one slot, and takes the optional name. |
| 3371 | 21 | `private void showLoadMenu()` |  |
| 3393 | 25 | `private void loadSlot(int slot)` |  |
| 3426 | 10 | `private void openCity()` | Where a city opens. |
| 3446 | 13 | `private void anotherCity()` | Another city has just replaced the one on screen - founded, or loaded (0.7.20): its toasts and its purchases are not this one's. |
| 3461 | 5 | `void foundCity(Founding choices)` | The founding screen's last step: found the city as chosen, and open it. |
| 3467 | 34 | `private void showSaveResult(GameFiles.Result result)` |  |
| 3512 | 9 | `private void toggleFullScreen()` | In and out of full screen, and remembered. |

### QUIT ASKS FIRST (0.7.20) (lines 3522-3577)

| line | len | member | says |
|---:|---:|---|---|
| 3538 | 32 | `private void showQuitDialog()` |  |
| 3572 | 5 | `private void closeQuitDialog()` | Close the dialog that is up, Quit's or a confirmation: Cancel, or Esc. |

### A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) (lines 3578-3647)

| line | len | member | says |
|---:|---:|---|---|
| 3590 | 57 | `void confirm(String ask, String why, java.util.List<String[]> lines, String yesText, Runnable yes)` |  |

### EVERY TOOLTIP, DRESSED ONCE (0.7.20) (lines 3648-3690)

| line | len | member | says |
|---:|---:|---|---|
| 3670 | 13 | `private void dressTooltips()` |  |
| 3685 | 5 | `private static void hideTooltips()` | Every tooltip showing, put away - so one is not left hanging over the menu Esc opens. |

### SETTINGS. (lines 3691-3804)

| line | len | member | says |
|---:|---:|---|---|
| 3699 | 65 | `private void showSettingsMenu()` |  |
| 3775 | 29 | `private VBox toggleRow(String label, boolean on, String what, Runnable flip)` | A setting: what it is, what it does, and a switch that says which way it is set without having to read the word next to it. |

### THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. (lines 3805-3827)

### ECONOMY IS GONE, AND IT SPLIT IN TWO. (lines 3828-3844)

### THE SCROLLER (lines 3845-3915)

| line | len | member | says |
|---:|---:|---|---|
| 3858 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column)` | A left-aligned column inside a scroll pane, which these screens all want. |
| 3869 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome)` | this scroller - a title alone is about 150; a title with a vitals bar and two strips pinned over it is a good deal more, and getting it wrong is a scrollbar that appears when there is nothing to scroll. |
| 3878 | 37 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome, boolean fromBottom)` | The same, with the position kept from the bottom of the page instead of as a fraction (fromBottom) - for a page that grows and shrinks ABOVE the controls the player is using; see keptScrollerFromBottom. |

### WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and (lines 3916-3939)

| line | len | member | says |
|---:|---:|---|---|
| 3924 | 1 | `public List<String> whatItDoes(BuildingsTemplate t)` |  |
| 3925 | 1 | `public List<String> whatCareItGives(BuildingsTemplate t)` |  |
| 3926 | 1 | `public String jobLabel(JobType job)` |  |
| 3929 | 6 | `public List<String> buildPages()` | Build's pages as the window names them: where it opens (BuildScreen.BUILD_HOME), then the strip's categories in order. |
| 3937 | 1 | `public String buildOpensOn()` | The page Build opens on in this window now: the Overview, until the player opens a category (0.7.24). |

### SIMULATE MULTIPLE MONTHS (lines 3940-4368)

| line | len | member | says |
|---:|---:|---|---|
| 3948 | 53 | `private void showSimulateMonthsMenu()` |  |
| 4013 | 222 | `private void showSimulateResultMenu(int requested, int completed)` | What happened while the player was not watching. |
| 4237 | 10 | `private void addSkipLine(VBox section, String label, double start, double end, double change, boolean isMoney)` | "Population  192 -> 664  (+472)", coloured by direction. |
| 4256 | 22 | `private void addChangeLine(VBox section, String label, double change, boolean isMoney, boolean higherIsBetter)` | A signed change, coloured by whether it is good news. |
| 4280 | 3 | `void showSectorReport(String title, VBox column, Runnable back)` | Shared scaffolding for the sector report screens - of which none is left: Household money, the last, became a page of its own in 0.7.27, and nothing calls this now. |
| 4290 | 77 | `void showSectorReport(String title, VBox column, Runnable back, Button extra)` | somewhere else - e.g. the industrial report linking to its financial statements. |

### THE FRAME FOLDS AWAY (0.7.24) (lines 4369-4577)

| line | len | member | says |
|---:|---:|---|---|
| 4413 | 49 | `private Region needsChip()` | "Needs you" and the count, or "Nothing needs you": the drawer's door. |
| 4470 | 6 | `private void openOnNeeds()` | The drawer as the chip opens it: on NEEDS YOU, the list the chip counts - Summary whatever mode is stored (SummaryScreen.needsView, the stored mode untouched), at the top of its scroller, where NEEDS YOU is under the ... |
| 4478 | 6 | `private HBox drawerBar()` | The drawer's bar: pin it open across screens, or close it. |
| 4485 | 26 | `private void refreshDrawerBar()` |  |
| 4513 | 10 | `private void placeFrame()` | The drawer, the construction panel and its tab, shown as the player left them - and none of it on the menus. |
| 4525 | 4 | `private static boolean inside(javafx.scene.Node node, javafx.scene.Node in)` | Whether a node is this one or inside it. |
| 4534 | 43 | `private void refreshConstructionTab(boolean inCity, boolean open)` | The tab the construction panel folds to: a crane, how many sites, and the words down it. |

### CONSTRUCTION PANEL (lines 4578-4913)

| line | len | member | says |
|---:|---:|---|---|
| 4598 | 179 | `private void refreshConstructionPanel()` |  |
| 4779 | 8 | `static Region areaSwatch(String colour, double size)` | A small square in an area's colour, before a heading (0.7.21): the pages' titles' swatch, smaller. |
| 4801 | 54 | `private void addDemolitionLog()` | What the city has lost lately, under what it is building. |
| 4868 | 44 | `private void addBuildLog()` | What the city has GAINED lately, above what it has lost. |

### THE RAIL (lines 4914-5171)

| line | len | member | says |
|---:|---:|---|---|
| 4951 | 1 | **type** `private record Tab(String key, String svg, String label, String name, Runnable go)` | One destination. |
| 4958 | 14 | `static String areaOf(String key)` | Which of the four areas a rail key is in (0.7.21): building and land, people and services, business and trade, money and policy. |
| 4979 | 15 | `Label pageTitle(String text)` | A page's title, with its area's swatch before it (0.7.21): a small square in the colour of the rail tab that owns the screen showing, as the mockups set the title. |
| 5025 | 54 | `private Tab[] tabs()` | The rail, in the order a city is actually run. |
| 5090 | 56 | `private String tabFor(String screen)` | Which tab owns the screen that is showing. |
| 5147 | 24 | `private void refreshTabRail()` |  |

### THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF (lines 5172-5355)

| line | len | member | says |
|---:|---:|---|---|
| 5220 | 5 | `private void goHome(Tab tab)` | Press a tab: forget where you were inside it - Build's category apart, since 0.7.20 - and land at the top. |
| 5247 | 20 | `private void resetSection(String key)` | A section's own idea of where you were, forgotten. |
| 5293 | 62 | `private VBox railButton(String svg, String label, String name, String area, boolean active, Runnable go)` | One button on the rail: its icon over its name (0.7.21). |

### THE INBOX (lines 5356-5452)

| line | len | member | says |
|---:|---:|---|---|
| 5388 | 64 | `private void refreshInbox()` |  |

### the envelope (lines 5453-5643)

| line | len | member | says |
|---:|---:|---|---|
| 5460 | 48 | `private void refreshInboxButton()` |  |
| 5522 | 58 | `private VBox noticeRow(Notice notice)` | One notice: its title, and its body when it is unfolded. |
| 5581 | 14 | `private String dealLabel(String key)` |  |
| 5605 | 38 | `private void deal(Notice notice)` | Take the player to the control that answers it. |

### TOASTS (0.7.20) (lines 5644-5726)

| line | len | member | says |
|---:|---:|---|---|
| 5679 | 13 | `private void refreshToasts()` | New urgent notices become toasts; read or settled ones leave the stack. |
| 5694 | 32 | `private Label toast(Notice notice)` | One toast: the notice's title, a timer, and a door to the inbox. |

### TIME, AND WHAT THE MONTH IS WORTH (lines 5727-5878)

| line | len | member | says |
|---:|---:|---|---|
| 5746 | 64 | `private void startClock()` | Starts the frame loop. |
| 5820 | 15 | `private void holdPresses()` | A PRESS IS NEVER REBUILT AWAY (0.7.40; see the clock's fields): a press anywhere in the window holds the clock's redraw, and the release lets it go once the release - and the click the scene makes of it - has been del... |
| 5837 | 4 | `private void letAllGo()` | No button is down after all: a move without one, or the window gone from under the pointer. |
| 5843 | 5 | `private void setPressHeld(boolean held)` | Holds the redraw, or lets it go - and says so on the scene, for TimeChart. |
| 5855 | 4 | `void redrawSoon()` | The open screen redrawn on the next frame no button is held on (0.7.40): for a change made as a press takes the focus - Finances' ask, set as its box is left - which a redraw at once would take from under the release. |
| 5861 | 4 | `static boolean pressHeld(javafx.scene.Node node)` | Whether a mouse button is down in the scene a node is in (0.7.40): what a redraw on a timer waits for. |
| 5873 | 5 | `private void paintDay()` | The day, repainted in place. |

### THE CHART OVER THE WHOLE WINDOW (0.7.23) (lines 5879-5987)

| line | len | member | says |
|---:|---:|---|---|
| 5902 | 9 | `void showChartFullScreen(Region pane, Label clockLine, java.util.function.Consumer<Boolean> leave)` | Lays the chart's pane over the whole window. |
| 5913 | 6 | `void closeChartFullScreen()` | Takes the pane off the window; the screen redraws its page. |
| 5921 | 4 | `private void leaveChartFullScreen(boolean redraw)` | Esc, P or another screen: the screen's own way back, which calls closeChartFullScreen(). |
| 5927 | 1 | `boolean isChartFullScreen()` | Whether a chart has the whole window. |
| 5930 | 1 | `boolean isShowing(String screen)` | Whether this screen - a clearMenu() name, "showHistoryMenu" - is the one on show: a late redraw asks before it draws. |
| 5933 | 4 | `String clockWords()` | The date and what the clock is doing, for a line that stands in for the header: "14 February 2151 · paused". |
| 5939 | 8 | `private static boolean overAChart(Object target)` | Whether a wheel event's target is inside a chart that takes the wheel (TimeChart.WHEEL_OWNER). |
| 5961 | 11 | `private boolean stopIfSomethingHappened()` | Stops the clock when the city has something to say, if the player wants that. |
| 5974 | 7 | `private void setClockRunning(boolean run)` | Play, or pause. |
| 5983 | 4 | `private static String speedLabel(int index)` | "0.25×", "1×", "10×" - no trailing zeros on the round ones; the header's speed reads it (0.7.21: "×", the mockups', for "x"). |

### TWELVE PIPS, AND ONE OF THEM MOVED. (lines 5988-6020)

| line | len | member | says |
|---:|---:|---|---|
| 6009 | 11 | `static void popPip(Region pip)` | A quarter second of "that landed", on the figure the month just changed - and since 0.7.26 on the land office's figures the month moved. |

