# UserInterface.java - 6,239 lines · 142 methods · 42 constants · interface

`ham/citybuildersim/ui/UserInterface.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (345), [Icons](Icons.md) (20), [GameFiles](GameFiles.md) (12), [Expectations](Expectations.md) (11), [SaveHeader](SaveHeader.md) (10), [PeopleScreen](PeopleScreen.md) (9), [Game](Game.md) (8), [CityCalendar](CityCalendar.md) (8), [Notice](Notice.md) (8), [BuildScreen](BuildScreen.md) (6), [FoundingScreen](FoundingScreen.md) (6), [Pieces](Pieces.md) (6), [Currency](Currency.md) (6), [GameVersion](GameVersion.md) (5), [DebtManager](DebtManager.md) (5), [CityNeeds](CityNeeds.md) (5), [DemolitionLog](DemolitionLog.md) (5), [ServicesScreen](ServicesScreen.md) (4), [GamePrefs](GamePrefs.md) (4), [PriceIndex](PriceIndex.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [SectorScreen](SectorScreen.md) (3), [FinancesScreen](FinancesScreen.md) (3), [BankScreen](BankScreen.md) (3), [TradeScreen](TradeScreen.md) (3), [PolicyScreen](PolicyScreen.md) (3), [SummaryScreen](SummaryScreen.md) (3), [ConstructionScreen](ConstructionScreen.md) (3), [TimeSkipReport](TimeSkipReport.md) (3), [BuildingsStacks](BuildingsStacks.md) (3)... and 28 more

**Used by (24):** [BankScreen](BankScreen.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [CityBuilderSim](CityBuilderSim.md), [ConstructionScreen](ConstructionScreen.md), [FinancesScreen](FinancesScreen.md), [FoundingScreen](FoundingScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UiProbe](UiProbe.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 57 | · THE SCREENS, one class each since 2026-09-18, in the order the rail |
| 80 | · WHERE THE SCREENS WENT. Each line is a run of this file's banner |
| 214 | THE RECEIPT INDICATOR |
| 247 | THE THEME |
| 403 | THE CLOCK |
| 606 | · THE MIDDLE COLUMN SCROLLS NOW, and it should have from the start. |
| 655 | · WHERE THE PAGE IS SCROLLED TO IS A FACT, NOT SOMETHING RECAPTURED. |
| 707 | · THE RAIL, AND WHY IT WAS PART OF THE PANEL. |
| 745 | · THE STAGE IS A STACK NOW. |
| 805 | · AND THE TIME CONTROLS DID NOT FLOAT, WHICH WAS A CORRECTION. |
| 821 | · THE WHEEL WORKS WHERE THE POINTER ALREADY IS. |
| 874 | · FULL SCREEN, AND THE TWO THINGS JAVAFX DOES ABOUT IT THAT WE DO NOT |
| 1155 | · AND THE SCROLL STAYS WHERE YOU PUT IT. |
| 1225 | · HOLD THE PAGE'S HEIGHT WHILE IT IS REBUILT, so the position is |
| 1262 | · A PAGE TORN DOWN HEARS NOTHING (0.7.50). |
| 1339 | A WHEEL NOTCH IS WORTH THE SAME EVERY TIME |
| 1774 | THE HEADER (0.7.21) |
| 1942 | · · POPULATION, and the month's net |
| 1984 | · · GDP, annualised from month 1, and real growth |
| 2017 | · · INFLATION, against the player's target |
| 2064 | · · OUT OF WORK, with a shortage read as watch |
| 2088 | · · RATE and the currency: the price of money |
| 2150 | · THE MONEY BLOCK (0.7.24) |
| 2726 | · THE INFLATION TILE'S WORDS (0.7.45), pure for the probe |
| 2848 | THE MAIN MENU (0.7.21) |
| 3121 | · WHAT "CONTINUE" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO CONTINUE |
| 3350 | THE SAVE SYSTEM |
| 3674 | QUIT ASKS FIRST (0.7.20) |
| 3730 | A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) |
| 3800 | EVERY TOOLTIP, DRESSED ONCE (0.7.20) |
| 3843 | SETTINGS. |
| 3970 | THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. |
| 3993 | ECONOMY IS GONE, AND IT SPLIT IN TWO. |
| 4010 | THE SCROLLER |
| 4081 | · WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and |
| 4105 | SIMULATE MULTIPLE MONTHS |
| 4216 | · · headlines |
| 4232 | · · deltas |
| 4282 | · · land |
| 4290 | · · buildings |
| 4306 | · · demolitions |
| 4330 | · · health |
| 4374 | · · households |
| 4534 | THE FRAME FOLDS AWAY (0.7.24) |
| 4743 | CONSTRUCTION PANEL |
| 5079 | THE RAIL |
| 5358 | THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF |
| 5542 | THE INBOX |
| 5582 | · · what an urgent notice gets |
| 5592 | · · the list |
| 5639 | · the envelope |
| 5840 | TOASTS (0.7.20) |
| 5923 | TIME, AND WHAT THE MONTH IS WORTH |
| 6075 | THE CHART OVER THE WHOLE WINDOW (0.7.23) |
| 6207 | TWELVE PIPS, AND ONE OF THEM MOVED. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 281 | `UserInterface.STAGE` | `Palette.STAGE` | The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). |
| 431 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 443 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed steps along: a rung a click on the clock's two arrows since 0.7.21, and the stops the speed slider stuck to before. |
| 444 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 465 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 491 | `UserInterface.PRESS_HELD` | `"UserInterface.pressHeld"` | The scene property that says a button is down (0.7.40), for what waits on a timer outside this class (TimeChart's settle). |
| 1368 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1769 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1844 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. |
| 1847 | `UserInterface.STRIP_INFLATION_OVER_TARGET` | `.05` | Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. |
| 1850 | `UserInterface.STRIP_DEFLATION_ALARM` | `.10` | Deflation past this reads red, whatever the target: prices collapsing. |
| 1862 | `UserInterface.DATE_WIDTH` | `164` | How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over (0.7.24: at 17 px, so the money block and five tiles fit a 1,280 w... |
| 1865 | `UserInterface.DATE_SIZE` | `17` | The date's size in the clock (0.7.24; it was 19). |
| 1868 | `UserInterface.SPARK_MONTHS` | `120` | How many months a tile's sparkline draws: ten years, or everything recorded if less. |
| 1871 | `UserInterface.SPARK_WIDTH` | `72` | A tile's sparkline at full size: on the label's row since 0.7.24, as the mockups draw it (it was 84 by 30, beside the words). |
| 1873 | `UserInterface.SPARK_HEIGHT` | `16` | ...and its height, the label's row (0.7.24; it was 30). |
| 1876 | `UserInterface.SPARK_MIN` | `30` | Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. |
| 1879 | `UserInterface.TILE_FIGURE` | `15` | The size of a tile's figure (0.7.24: 15, so the money block and five tiles fit a 1,280 window whole; it was 17). |
| 1881 | `UserInterface.TILE_LABEL` | `10.5` | The size of a tile's label, and of the money block's. |
| 1883 | `UserInterface.TILE_CHANGE` | `10.5` | The size of a tile's change line (0.7.24; it was 11). |
| 2232 | `UserInterface.MONEY_FIGURE` | `28` | The cash in the money block: the mockups' 28 px. |
| 2884 | `UserInterface.BACKDROP_BLOCK` | `"#121c28"` | The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. |
| 2885 | `UserInterface.BACKDROP_EDGE` | `"#1d2b3c"` |  |
| 2886 | `UserInterface.BACKDROP_WINDOW` | `"#22344a"` |  |
| 2889 | `UserInterface.FOUNDING_DIM` | `.72` | How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. |
| 2892 | `UserInterface.MENU_CITIES` | `3` | Up to this many of the cities saved last, as cards at the menu's bottom right. |
| 3235 | `UserInterface.MENU_LEFT` | `96` | How far in from the window's left the menu's column and version sit. |
| 3238 | `UserInterface.MENU_BUTTON` | `360` | The menu's buttons' width, as the mockups draw them. |
| 3313 | `UserInterface.CITY_CARD` | `250` | A city card's width, as the mockups draw it. |
| 3362 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3820 | `UserInterface.TIP_WIDTH` | `420` | The width a tooltip's text wraps at, unless it asked for its own. |
| 4020 | `UserInterface.PAGE_FOOT` | `24` | Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). |
| 4696 | `UserInterface.CONSTRUCTION_TAB` | `44` | How wide the construction panel's tab is. |
| 4954 | `UserInterface.PANEL_TEXT` | `256` | How wide the construction panel's lines wrap: the panel less its padding. |
| 5099 | `UserInterface.RAIL_WIDTH` | `Palette.RAIL` | The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). |
| 5102 | `UserInterface.RAIL_BUTTON` | `54` | A rail button's height, and the least it may shrink to on a short window. |
| 5103 | `UserInterface.RAIL_BUTTON_MIN` | `40` |  |
| 5106 | `UserInterface.RAIL_ICON` | `20` | How big a rail icon is drawn: its 24-unit grid at 20 pixels. |
| 5566 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |
| 5863 | `UserInterface.TOAST_SECONDS` | `8` | How long a toast stays before it fades, in seconds. |
| 5866 | `UserInterface.TOAST_MAX` | `3` | How many toasts at once. |
| 5869 | `UserInterface.TOAST_WIDTH` | `340` | How wide a toast's text wraps. |

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
| 182 | `private MiniMap miniMap` | The minimap at the rail's foot (0.7.134); see THE RAIL. |
| 184 | `private StackPane stagePane` | The middle of the window, with the City overview's drawer and the construction panel (both since 0.7.24), the inbox's list and the toasts on top of it. |
| 186 | `private VBox inboxCorner` | The inbox's list, dropped down at the stage's top right while it is open (its envelope is the header's since 0.7.21). |
| 188 | `private VBox toastStack` | The toasts, stacked at the stage's bottom right (0.7.20); see TOASTS. |
| 190 | `private StackPane windowStack` | The whole window, with a dialog laid over it when one is open (0.7.20). |
| 192 | `private StackPane quitDialog` | The dialog that is up - Quit's, or since 0.7.22 a confirmation (confirm()) - or null. |
| 200 | `private boolean cityOpen` | Whether a city has been founded or loaded this session (0.7.20; anotherCity()). |
| 202 | `javafx.scene.control.ScrollPane menuScroller` | The middle column's scroller. |
| 204 | `private VBox constructionPanel` | The construction panel: over the stage's right edge while open, folded to constructionTab otherwise (0.7.24). |
| 206 | `private VBox constructionTab` | The slim tab on the stage's right edge the construction panel folds to (0.7.24). |
| 208 | `VBox cityPanel` | The City overview's content, drawn by SummaryScreen. |
| 210 | `private VBox drawer` | The drawer the City overview opens in, over the stage's left (0.7.24); see THE FRAME FOLDS AWAY. |
| 212 | `private boolean drawerOpen` | Whether the drawer is open. |
| 232 | `int receiptSeen` |  |
| 233 | `boolean receiptOpen` |  |
| 245 | `FadeTransition receiptPulse` | The pulse, held so it can be stopped. |
| 400 | `private HBox header` | The always-visible strip on the BorderPane's top edge: the header (0.7.21; the date bar until then). |
| 401 | `private Scene scene` |  |
| 446 | `private int speedIndex` | 1x |
| 447 | `private boolean clockRunning` |  |
| 448 | `private double monthProgress` |  |
| 449 | `private long lastFrame` |  |
| 466 | `private double sinceRedraw` |  |
| 467 | `private boolean redrawPending` |  |
| 468 | `private javafx.animation.AnimationTimer clock` |  |
| 484 | `private boolean pressHeld` | A PRESS IS NEVER REBUILT AWAY (0.7.40). |
| 486 | `private final java.util.EnumSet<javafx.scene.input.MouseButton> buttonsDown` | The buttons down, each counted from its press to its release, so one let go while another is held still holds. |
| 489 | `private boolean letGo` | Set when a release has been delivered, and spent by the next frame: the first frame after a release. |
| 494 | `private Label dayLabel` | The date line, kept so a day can be repainted without a whole redraw. |
| 497 | `private String pausedBecause` | Why the clock stopped itself, shown until it is started again. |
| 500 | `private String pausedOnKey` | The notice that last stopped it, so one condition interrupts once. |
| 1331 | `private double pageScrollAt` | Where the player has scrolled the page to. |
| 1334 | `private boolean settlingScroll` | True while a rebuild is in flight, so its clamps are not mistaken for a hand. |
| 1337 | `private boolean correcting` | Guards the re-entry when the listener corrects a clamp of its own. |
| 1371 | `String currentScreen` | Which show*Menu drew what is on screen; see clearMenu. |
| 1374 | `private boolean tearingDown` | True while clearMenu empties the page: rootMenu swallows the pointer's exits from what it removes (0.7.50). |
| 1377 | `private Runnable redrawScreen` | How to draw it again after a month passes; see clearMenu. |
| 1388 | `private Runnable resumeTo` | The screen Esc was pressed on, so Continue (Resume until 0.7.21) can go back to it. |
| 1416 | `final java.util.Map<String, Double> innerScrollAt` | Where a scroller inside the CURRENT screen was left, by name. |
| 1426 | `private final java.util.Map<String, Double> panelScrollAt` | The same, for the two side panels - and this one is NEVER emptied. |
| 1772 | `private long lastWheelNanos` | When the last wheel event moved the page; see scrollPageBy. |
| 1886 | `private Label monthFigure` | The month's figure in the clock, kept so it pops when a month lands. |
| 1889 | `private Label speedReading` | The speed's reading between its two arrows, kept so a click changes it in place. |
| 1892 | `private int clockAt` | The month the clock last showed, so the month's figure pops only when it moves. |
| 1895 | `private StackPane inboxHolder` | Where the inbox's envelope is drawn in the header, so a notice read elsewhere can redraw its count. |
| 2346 | `private final double gap` |  |
| 2409 | `private final HBox labelRow` |  |
| 2410 | `private final Label figure` |  |
| 2411 | `private final List<Label> changes` |  |
| 2412 | `private final Sparkline spark` |  |
| 2497 | `private final double[] points` |  |
| 2498 | `private final int[] at` |  |
| 2499 | `private final javafx.scene.paint.Color colour` |  |
| 2500 | `private final javafx.scene.canvas.Canvas canvas` |  |
| 2501 | `private double drawnW` |  |
| 2502 | `private int firstMonth, lastMonth` |  |
| 2878 | `private StackPane titleLayer` | The layer the main menu and the founding screen draw on - and, with no city open, Settings, Load and Save (0.7.22): the backdrop, a dimmer, and what the screen puts on it. |
| 2879 | `private javafx.scene.canvas.Canvas backdrop` |  |
| 2880 | `private Region titleDim` |  |
| 2881 | `private StackPane titleContent` |  |
| 2986 | `private Runnable placeMenu` | How the menu's column is placed for the window's height; run again when it changes. |
| 3071 | `private final java.util.Map<Integer, KnownSave> knownSaves` |  |
| 3655 | `GamePrefs prefs` | How the player likes the window. |
| 4566 | `private Region needsChipNode` | The chip, kept so a click outside the drawer can repaint it without rebuilding the header under the pointer. |
| 4567 | `private Runnable restyleNeedsChip` |  |
| 4575 | `private HBox drawerBarBox` | The drawer's own bar: the pin and the close, refilled as the pin changes. |
| 5462 | `private boolean railJump` | Set for exactly one clearMenu, by goHome(). |
| 5569 | `private boolean inboxOpen` | Whether the list is dropped down. |
| 5572 | `private String inboxExpanded` | Which notice's body is unfolded, by key. |
| 5872 | `private final java.util.Set<String> toasted` | The notices already toasted, as key@raised. |
| 6091 | `private javafx.scene.Node chartFull` | The chart's pane while it has the window, its clock line, and what closes it; null otherwise. |
| 6092 | `private Label chartFullClock` |  |
| 6093 | `private java.util.function.Consumer<Boolean> chartFullLeave` |  |
| 6096 | `private String chartFullOwner` | The screen that laid the pane (a clearMenu() name): its own redraws keep it, any other screen closes it (0.7.61: City History's chart, the land office's map). |
| 6099 | `private MapView panKeys` | The map W, A, S and D pan while it is over the window (0.7.135, batch E9): MapView's expand() sets it, its collapse() clears it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 6187 | **type** `public class UserInterface extends Application` | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating and the inbox - the rail down the left, the City overview's drawer and ... |

### THE SCREENS, one class each since 2026-09-18, in the order the rail (lines 57-79)

### WHERE THE SCREENS WENT. Each line is a run of this file's banner (lines 80-213)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 1 | `public UserInterface()` | JavaFX needs the no-arg one; a harness needs one bound to a city. |
| 174 | 1 | `public UserInterface(Game game)` |  |

### THE RECEIPT INDICATOR (lines 214-246)

### THE THEME (lines 247-402)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 115 | `private void applyTheme(Scene target)` |  |

### THE CLOCK (lines 403-1338)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 636 | `public void start(Stage primaryStage)` |  |
| 1145 | 184 | `void clearMenu(String screen, Runnable again)` | Clears the menu area and refreshes the construction panel. |

### A WHEEL NOTCH IS WORTH THE SAME EVERY TIME (lines 1339-1773)

| line | len | member | says |
|---:|---:|---|---|
| 1385 | 1 | `void redraw()` | Draw the screen that is showing, again, in place - the same call the clock makes after a month. |
| 1399 | 10 | `private static boolean isGameMenu(String screen)` | The screens that are ABOUT the game rather than in it. |
| 1429 | 3 | `private javafx.scene.control.ScrollPane keptScroller(String key, javafx.scene.Node content)` | A scroller inside a screen, which remembers where it was while you stay. |
| 1446 | 3 | `private javafx.scene.control.ScrollPane keptScrollerFromBottom(String key, javafx.scene.Node content)` | The same, remembering the distance from the BOTTOM rather than the fraction - for a page whose top half changes height under the player. |
| 1451 | 3 | `javafx.scene.control.ScrollPane keptPanelScroller(String key, javafx.scene.Node content)` | A scroller in one of the side panels, which always remembers. |
| 1462 | 28 | `private javafx.scene.control.ScrollPane remembering(java.util.Map<String, Double> where, String key, javafx.scene.Node content,...` | A ScrollPane that files its own position under a name and comes back to it. |
| 1492 | 6 | `private static double scrollSpan(javafx.scene.control.ScrollPane scroller)` | How far a scroller's content can travel, in pixels, as laid out right now. |
| 1528 | 3 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, double to)` | Put it back, and put it back BEFORE anything is painted. |
| 1537 | 113 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, java.util.function.DoubleSupplier target)` | The same, with the position asked for afresh at each of the three attempts - so a caller keeping pixels rather than a fraction can turn them into a fraction of the height the page actually has by then. |
| 1676 | 35 | `private double pageSpan(javafx.scene.Node page)` | How far the page can travel: what the content WANTS to be, less the viewport. |
| 1721 | 14 | `private void wheelToPage(javafx.scene.input.ScrollEvent wheel)` | A wheel turn nothing else wanted, spent on the page. |
| 1756 | 11 | `private void scrollPageBy(double deltaY, double span)` | Move the page by one wheel event's worth, with a floor under the FIRST event of a gesture and the rest taken as they come. |

### THE HEADER (0.7.21) (lines 1774-2149)

| line | len | member | says |
|---:|---:|---|---|
| 1902 | 14 | `private void refreshHeader()` | The header, rebuilt: the clock, the money block, the five tiles, the "Needs you" chip, the rating and the inbox. |
| 1925 | 3 | **type** `private record Headline(String label, String area, String value, String valueTone, String[] change, String ...` | One tile, as the header draws it. |
| 1934 | 1 | `private static String[] words(String...longestFirst)` | A change line, longest wording first: the tile shows the longest that fits its width (HeadlineTile), so a line is shortened and never cut (0.7.24). |
| 1937 | 173 | `private List<Headline> headlines()` | The five, in the mockups' order: people, output, prices, work, the price of money - TREASURY is its own block since 0.7.24 (moneyBlock()). |
| 2123 | 26 | `private String treasuryWhy(double income, double moved)` | The money block's (i) - the TREASURY tile's until 0.7.24: the three figures, EARNED, SURPLUS and BANKED (0.7.31's names, the same on the Government tab), and why they differ. |

### THE MONEY BLOCK (0.7.24) (lines 2150-2725)

| line | len | member | says |
|---:|---:|---|---|
| 2178 | 52 | `private Region moneyBlock()` | The cash at its own size, what the month EARNED under it (not how far the month moved the cash: see above), and a door to Finances. |
| 2242 | 32 | `private String moneyWhy(DebtManager market, ForeignAccounts fx, String here)` | The rate tile's (i): the price of money three ways (0.7.4) - Jerus: "the bank rate, the central bank rate, both should be shown, as well as the rate you borrow in" - and the currency both ways, against its parity. |
| 2283 | 38 | `private Region headlineTile(Headline t)` | One tile (0.7.24, the mockups' shape): its label in its area's colour and a small sparkline on the top row, the figure under them, and the change line across the tile's whole width - in the longest of its wordings tha... |
| 2331 | 4 | `private void openHistory(String[] keys)` | Opens City History with these lines picked: the tiles' click. |
| 2345 | 53 | **type** `private static final class HeadlineRow extends javafx.scene.layout.Pane` | The tiles, side by side: equal shares of the row, except that a tile never gets less than its least width - its figure until 0.7.24, the widest of its label, its figure and its shortest change line since (HeadlineTile... |
| 2347 | 1 | `HeadlineRow(double gap)` _(in UserInterface.HeadlineRow)_ |  |
| 2349 | 5 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 2355 | 5 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 2361 | 5 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineRow)_ |  |
| 2367 | 30 | `protected void layoutChildren()` _(in UserInterface.HeadlineRow)_ |  |
| 2408 | 78 | **type** `private static final class HeadlineTile extends Region` | One tile's inside (0.7.24): the label at the top left and the sparkline at the top right in what the label leaves, up to SPARK_WIDTH - none at all under SPARK_MIN; the figure under them; and the change line across the... |
| 2414 | 8 | `HeadlineTile(HBox labelRow, Label figure, List<Label> changes, Sparkline spark)` _(in UserInterface.HeadlineTile)_ |  |
| 2423 | 8 | `private double shortest()` _(in UserInterface.HeadlineTile)_ |  |
| 2432 | 3 | `private double changeHeight()` _(in UserInterface.HeadlineTile)_ |  |
| 2436 | 4 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2441 | 5 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2447 | 3 | `protected double computeMinHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2451 | 4 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2456 | 29 | `protected void layoutChildren()` _(in UserInterface.HeadlineTile)_ |  |
| 2496 | 80 | **type** `static final class Sparkline extends Region` | A tile's sparkline: the last SPARK_MONTHS of its series, a month a point, the months recorded as nothing (NaN) left out; a faint fill under the line and a dot on the latest month, in its area's colour. |
| 2504 | 24 | `Sparkline(double[] series, List<Integer> months, String colour)` _(in UserInterface.Sparkline)_ |  |
| 2529 | 46 | `protected void layoutChildren()` _(in UserInterface.Sparkline)_ |  |
| 2583 | 88 | `private HBox clockBlock(boolean inCity)` | The clock, docked at the header's left (0.7.21): the play button, the date, the month's figure and the speed - and why the clock stopped itself, when it did. |
| 2673 | 14 | `private Label speedArrow(String glyph, int step)` | One of the speed's two arrows: a rung down or up the ladder, the reading changed in place. |
| 2689 | 27 | `private HBox ratingAndInbox(boolean inCity)` | The "Needs you" chip, the credit rating's chip and the inbox's envelope, at the header's right. |
| 2718 | 7 | `static String ratingColour(String rating)` | A rating's verdict: investment grade is good, BB and B a watch, below that bad. |

### THE INFLATION TILE'S WORDS (0.7.45), pure for the probe (lines 2726-2847)

| line | len | member | says |
|---:|---:|---|---|
| 2734 | 10 | `static String[] anchorLine(Game g)` | The INFLATION tile's line, longest first: what people expect inflation to be and how far they believe the bank - "expect 2.2% · trust 82%", "exp 2.2% · 82%", "exp 2.2%" - and before the basket is based, when everyone ... |
| 2746 | 4 | `static String anchorTone(Game g)` | ...its tone: NEEDS YOU's PRICES row - plain at rest, amber in a month trust fell, red when it fell under half. |
| 2752 | 17 | `static String inflationTip(Game g, String off)` | ...its tooltip: the year's rate, how far off the target (`off`, the old line, or null with no rate yet), what people expect and why, and the level. |
| 2771 | 27 | `static String inflationMore(Game g)` | ...and its (i): the figure's colours, the trust paragraph, and before the first rate when it comes. |
| 2800 | 8 | `static String ordinal(int n)` | "sixtieth", "twenty-fourth": a month's share of the way, as the (i) says it. |
| 2824 | 6 | `private static String inflationColour(double inflation, double target)` | Good near the player's target, amber off it, red once prices run past it or collapse. |
| 2832 | 5 | `private static String rateColour(ForeignAccounts fx)` | Secondary grey near parity, amber past ForeignAccounts.PARITY_WATCH either side, red past PARITY_FAR (0.7.35: the one parity rule; a pinned rate is grey). |

### THE MAIN MENU (0.7.21) (lines 2848-3349)

| line | len | member | says |
|---:|---:|---|---|
| 2898 | 39 | `private void showTitleLayer(boolean show, boolean dim)` | The title layer shown or taken away, and emptied for the screen about to draw on it. |
| 2939 | 1 | `StackPane titleContent()` | Where the main menu and the founding screen put what they draw, and menuPage() its panel (0.7.22). |
| 2952 | 9 | `private static boolean coldMenu(String screen)` | The menu's own screens, which draw over the backdrop while no city is open. |
| 2963 | 21 | `private VBox menuPage()` | Where one of those screens draws: the middle column in a city; a panel over the backdrop, centred and scrolling if it must, when none is open. |
| 2996 | 72 | `static void drawBackdrop(javafx.scene.canvas.GraphicsContext g, double w, double h)` | The backdrop: the city's line rising behind a skyline of flat blocks, a few of their windows lit in the four area colours. |
| 3070 | 1 | **type** `private record KnownSave(long modified, long size, SaveHeader header)` | A save's header, kept while its file is unchanged, so the menu can read eleven slots without parsing eleven files every time it opens. |
| 3074 | 15 | `private SaveHeader headerOf(int slot)` | A slot's header, read again only when its file has changed; null for an empty or unreadable slot. |
| 3091 | 7 | `private static String savedWhen(long savedAt)` | "saved 20:38" today, "saved 29 Sep 20:38" this year, with the year before that. |
| 3099 | 134 | `void showMainMenu()` |  |
| 3241 | 27 | `private Button menuButton(String text, String under, boolean primary)` | One of the menu's buttons: a word, and under the primary one what it goes back to. |
| 3280 | 31 | `private VBox yourCities(double wide)` | YOUR CITIES: the slots saved last, newest first, as cards - the city's name, its slot, its people and its month, and when it was saved. |
| 3316 | 33 | `private VBox cityCard(int slot, SaveHeader header)` | One saved city: its name and slot, its people and month, when it was saved; a click loads it. |

### THE SAVE SYSTEM (lines 3350-3673)

| line | len | member | says |
|---:|---:|---|---|
| 3366 | 34 | `private String slotSummary(int slot)` | One line describing what is in a slot, or that it is empty. |
| 3401 | 13 | `private String slotTitle(int slot)` |  |
| 3416 | 30 | `private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty)` | A slot row: the label on the button, the city underneath it. |
| 3447 | 23 | `private void showSavingMenu()` |  |
| 3477 | 42 | `private void showSaveSlotConfirm(int slot)` | Confirms one slot, and takes the optional name. |
| 3520 | 22 | `private void showLoadMenu()` |  |
| 3543 | 25 | `private void loadSlot(int slot)` |  |
| 3576 | 10 | `private void openCity()` | Where a city opens. |
| 3596 | 15 | `private void anotherCity()` | Another city has just replaced the one on screen - founded, or loaded (0.7.20): its toasts and its purchases are not this one's. |
| 3613 | 5 | `void foundCity(Founding choices)` | The founding screen's last step: found the city as chosen, and open it. |
| 3619 | 34 | `private void showSaveResult(GameFiles.Result result)` |  |
| 3664 | 9 | `private void toggleFullScreen()` | In and out of full screen, and remembered. |

### QUIT ASKS FIRST (0.7.20) (lines 3674-3729)

| line | len | member | says |
|---:|---:|---|---|
| 3690 | 32 | `private void showQuitDialog()` |  |
| 3724 | 5 | `private void closeQuitDialog()` | Close the dialog that is up, Quit's or a confirmation: Cancel, or Esc. |

### A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) (lines 3730-3799)

| line | len | member | says |
|---:|---:|---|---|
| 3742 | 57 | `void confirm(String ask, String why, java.util.List<String[]> lines, String yesText, Runnable yes)` |  |

### EVERY TOOLTIP, DRESSED ONCE (0.7.20) (lines 3800-3842)

| line | len | member | says |
|---:|---:|---|---|
| 3822 | 13 | `private void dressTooltips()` |  |
| 3837 | 5 | `private static void hideTooltips()` | Every tooltip showing, put away - so one is not left hanging over the menu Esc opens. |

### SETTINGS. (lines 3843-3969)

| line | len | member | says |
|---:|---:|---|---|
| 3851 | 78 | `private void showSettingsMenu()` |  |
| 3940 | 29 | `private VBox toggleRow(String label, boolean on, String what, Runnable flip)` | A setting: what it is, what it does, and a switch that says which way it is set without having to read the word next to it. |

### THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. (lines 3970-3992)

### ECONOMY IS GONE, AND IT SPLIT IN TWO. (lines 3993-4009)

### THE SCROLLER (lines 4010-4080)

| line | len | member | says |
|---:|---:|---|---|
| 4023 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column)` | A left-aligned column inside a scroll pane, which these screens all want. |
| 4034 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome)` | this scroller - a title alone is about 150; a title with a vitals bar and two strips pinned over it is a good deal more, and getting it wrong is a scrollbar that appears when there is nothing to scroll. |
| 4043 | 37 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome, boolean fromBottom)` | The same, with the position kept from the bottom of the page instead of as a fraction (fromBottom) - for a page that grows and shrinks ABOVE the controls the player is using; see keptScrollerFromBottom. |

### WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and (lines 4081-4104)

| line | len | member | says |
|---:|---:|---|---|
| 4089 | 1 | `public List<String> whatItDoes(BuildingsTemplate t)` |  |
| 4090 | 1 | `public List<String> whatCareItGives(BuildingsTemplate t)` |  |
| 4091 | 1 | `public String jobLabel(JobType job)` |  |
| 4094 | 6 | `public List<String> buildPages()` | Build's pages as the window names them: where it opens (BuildScreen.BUILD_HOME), then the strip's categories in order. |
| 4102 | 1 | `public String buildOpensOn()` | The page Build opens on in this window now: the Overview, until the player opens a category (0.7.24). |

### SIMULATE MULTIPLE MONTHS (lines 4105-4533)

| line | len | member | says |
|---:|---:|---|---|
| 4113 | 53 | `private void showSimulateMonthsMenu()` |  |
| 4178 | 222 | `private void showSimulateResultMenu(int requested, int completed)` | What happened while the player was not watching. |
| 4402 | 10 | `private void addSkipLine(VBox section, String label, double start, double end, double change, boolean isMoney)` | "Population  192 -> 664  (+472)", coloured by direction. |
| 4421 | 22 | `private void addChangeLine(VBox section, String label, double change, boolean isMoney, boolean higherIsBetter)` | A signed change, coloured by whether it is good news. |
| 4445 | 3 | `void showSectorReport(String title, VBox column, Runnable back)` | Shared scaffolding for the sector report screens - of which none is left: Household money, the last, became a page of its own in 0.7.27, and nothing calls this now. |
| 4455 | 77 | `void showSectorReport(String title, VBox column, Runnable back, Button extra)` | somewhere else - e.g. the industrial report linking to its financial statements. |

### THE FRAME FOLDS AWAY (0.7.24) (lines 4534-4742)

| line | len | member | says |
|---:|---:|---|---|
| 4578 | 49 | `private Region needsChip()` | "Needs you" and the count, or "Nothing needs you": the drawer's door. |
| 4635 | 6 | `private void openOnNeeds()` | The drawer as the chip opens it: on NEEDS YOU, the list the chip counts - Summary whatever mode is stored (SummaryScreen.needsView, the stored mode untouched), at the top of its scroller, where NEEDS YOU is under the ... |
| 4643 | 6 | `private HBox drawerBar()` | The drawer's bar: pin it open across screens, or close it. |
| 4650 | 26 | `private void refreshDrawerBar()` |  |
| 4678 | 10 | `private void placeFrame()` | The drawer, the construction panel and its tab, shown as the player left them - and none of it on the menus. |
| 4690 | 4 | `private static boolean inside(javafx.scene.Node node, javafx.scene.Node in)` | Whether a node is this one or inside it. |
| 4699 | 43 | `private void refreshConstructionTab(boolean inCity, boolean open)` | The tab the construction panel folds to: a crane, how many sites, and the words down it. |

### CONSTRUCTION PANEL (lines 4743-5078)

| line | len | member | says |
|---:|---:|---|---|
| 4763 | 179 | `private void refreshConstructionPanel()` |  |
| 4944 | 8 | `static Region areaSwatch(String colour, double size)` | A small square in an area's colour, before a heading (0.7.21): the pages' titles' swatch, smaller. |
| 4966 | 54 | `private void addDemolitionLog()` | What the city has lost lately, under what it is building. |
| 5033 | 44 | `private void addBuildLog()` | What the city has GAINED lately, above what it has lost. |

### THE RAIL (lines 5079-5357)

| line | len | member | says |
|---:|---:|---|---|
| 5118 | 1 | **type** `private record Tab(String key, String svg, String label, String name, Runnable go)` | One destination. |
| 5125 | 14 | `static String areaOf(String key)` | Which of the four areas a rail key is in (0.7.21): building and land, people and services, business and trade, money and policy. |
| 5146 | 15 | `Label pageTitle(String text)` | A page's title, with its area's swatch before it (0.7.21): a small square in the colour of the rail tab that owns the screen showing, as the mockups set the title. |
| 5192 | 54 | `private Tab[] tabs()` | The rail, in the order a city is actually run. |
| 5257 | 56 | `private String tabFor(String screen)` | Which tab owns the screen that is showing. |
| 5314 | 32 | `private void refreshTabRail()` |  |
| 5353 | 4 | `void openMapOn(MiniMapFrame.Box frame)` | The minimap's click (0.7.134): the land office, as its rail button opens it, and its map over the window on the minimap's frame - the city's built-up ground (MiniMapFrame.frame(); none yet, as Expand opens it). |

### THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF (lines 5358-5541)

| line | len | member | says |
|---:|---:|---|---|
| 5406 | 5 | `private void goHome(Tab tab)` | Press a tab: forget where you were inside it - Build's category apart, since 0.7.20 - and land at the top. |
| 5433 | 20 | `private void resetSection(String key)` | A section's own idea of where you were, forgotten. |
| 5479 | 62 | `private VBox railButton(String svg, String label, String name, String area, boolean active, Runnable go)` | One button on the rail: its icon over its name (0.7.21). |

### THE INBOX (lines 5542-5638)

| line | len | member | says |
|---:|---:|---|---|
| 5574 | 64 | `private void refreshInbox()` |  |

### the envelope (lines 5639-5839)

| line | len | member | says |
|---:|---:|---|---|
| 5646 | 48 | `private void refreshInboxButton()` |  |
| 5708 | 58 | `private VBox noticeRow(Notice notice)` | One notice: its title, and its body when it is unfolded. |
| 5767 | 16 | `private String dealLabel(String key)` |  |
| 5793 | 46 | `private void deal(Notice notice)` | Take the player to the control that answers it. |

### TOASTS (0.7.20) (lines 5840-5922)

| line | len | member | says |
|---:|---:|---|---|
| 5875 | 13 | `private void refreshToasts()` | New urgent notices become toasts; read or settled ones leave the stack. |
| 5890 | 32 | `private Label toast(Notice notice)` | One toast: the notice's title, a timer, and a door to the inbox. |

### TIME, AND WHAT THE MONTH IS WORTH (lines 5923-6074)

| line | len | member | says |
|---:|---:|---|---|
| 5942 | 64 | `private void startClock()` | Starts the frame loop. |
| 6016 | 15 | `private void holdPresses()` | A PRESS IS NEVER REBUILT AWAY (0.7.40; see the clock's fields): a press anywhere in the window holds the clock's redraw, and the release lets it go once the release - and the click the scene makes of it - has been del... |
| 6033 | 4 | `private void letAllGo()` | No button is down after all: a move without one, or the window gone from under the pointer. |
| 6039 | 5 | `private void setPressHeld(boolean held)` | Holds the redraw, or lets it go - and says so on the scene, for TimeChart. |
| 6051 | 4 | `void redrawSoon()` | The open screen redrawn on the next frame no button is held on (0.7.40): for a change made as a press takes the focus - Finances' ask, set as its box is left - which a redraw at once would take from under the release. |
| 6057 | 4 | `static boolean pressHeld(javafx.scene.Node node)` | Whether a mouse button is down in the scene a node is in (0.7.40): what a redraw on a timer waits for. |
| 6069 | 5 | `private void paintDay()` | The day, repainted in place. |

### THE CHART OVER THE WHOLE WINDOW (0.7.23) (lines 6075-6206)

| line | len | member | says |
|---:|---:|---|---|
| 6102 | 1 | `void panKeysTo(MapView map)` | ...set, or cleared with null. |
| 6110 | 10 | `void showChartFullScreen(Region pane, Label clockLine, java.util.function.Consumer<Boolean> leave)` | Lays the chart's pane over the whole window. |
| 6122 | 7 | `void closeChartFullScreen()` | Takes the pane off the window; the screen redraws its page. |
| 6131 | 4 | `private void leaveChartFullScreen(boolean redraw)` | Esc, P or another screen: the screen's own way back, which calls closeChartFullScreen(). |
| 6137 | 1 | `boolean isChartFullScreen()` | Whether a chart has the whole window. |
| 6140 | 1 | `boolean isShowing(String screen)` | Whether this screen - a clearMenu() name, "showHistoryMenu" - is the one on show: a late redraw asks before it draws. |
| 6147 | 3 | `double clockMonths()` | The game's clock in months (0.7.97, batch O13): the month and the share of the next the clock has run - what the map's boats sail on (MapView.BOAT_GAME_MONTHS). |
| 6152 | 4 | `String clockWords()` | The date and what the clock is doing, for a line that stands in for the header: "14 February 2151 · paused". |
| 6158 | 8 | `private static boolean overAChart(Object target)` | Whether a wheel event's target is inside a chart that takes the wheel (TimeChart.WHEEL_OWNER). |
| 6180 | 11 | `private boolean stopIfSomethingHappened()` | Stops the clock when the city has something to say, if the player wants that. |
| 6193 | 7 | `private void setClockRunning(boolean run)` | Play, or pause. |
| 6202 | 4 | `private static String speedLabel(int index)` | "0.25×", "1×", "10×" - no trailing zeros on the round ones; the header's speed reads it (0.7.21: "×", the mockups', for "x"). |

### TWELVE PIPS, AND ONE OF THEM MOVED. (lines 6207-6239)

| line | len | member | says |
|---:|---:|---|---|
| 6228 | 11 | `static void popPip(Region pip)` | A quarter second of "that landed", on the figure the month just changed - and since 0.7.26 on the land office's figures the month moved. |

