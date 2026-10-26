# UserInterface.java - 6,719 lines · 182 methods · 41 constants · interface

`ham/citybuildersim/ui/UserInterface.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (351), [Kept](Kept.md) (137), [Icons](Icons.md) (20), [GameFiles](GameFiles.md) (12), [Expectations](Expectations.md) (11), [SaveHeader](SaveHeader.md) (10), [PeopleScreen](PeopleScreen.md) (9), [Game](Game.md) (8), [CityCalendar](CityCalendar.md) (8), [Notice](Notice.md) (8), [BuildScreen](BuildScreen.md) (6), [FoundingScreen](FoundingScreen.md) (6), [Pieces](Pieces.md) (6), [Currency](Currency.md) (6), [DemolitionLog](DemolitionLog.md) (6), [GameVersion](GameVersion.md) (5), [DebtManager](DebtManager.md) (5), [CityNeeds](CityNeeds.md) (5), [BuildingsStacks](BuildingsStacks.md) (5), [ServicesScreen](ServicesScreen.md) (4), [GamePrefs](GamePrefs.md) (4), [PriceIndex](PriceIndex.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [BuildingManager](BuildingManager.md) (4), [ConstructionControl](ConstructionControl.md) (4), [LandmassView](LandmassView.md) (4), [SectorScreen](SectorScreen.md) (3), [FinancesScreen](FinancesScreen.md) (3), [BankScreen](BankScreen.md) (3), [TradeScreen](TradeScreen.md) (3)... and 31 more

**Used by (28):** [BankScreen](BankScreen.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [ChartFitBench](ChartFitBench.md), [CityBuilderSim](CityBuilderSim.md), [ClockCheck](ClockCheck.md), [ConstructionScreen](ConstructionScreen.md), [FinancesScreen](FinancesScreen.md), [FoundingScreen](FoundingScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [KeptBench](KeptBench.md), [LandScreen](LandScreen.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UiProbe](UiProbe.md), [ZoningScreen](ZoningScreen.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 57 | · THE SCREENS, one class each since 2026-09-18, in the order the rail |
| 80 | · WHERE THE SCREENS WENT. Each line is a run of this file's banner |
| 214 | THE RECEIPT INDICATOR |
| 247 | THE THEME |
| 434 | THE CLOCK |
| 599 | · THE MIDDLE COLUMN SCROLLS NOW, and it should have from the start. |
| 648 | · WHERE THE PAGE IS SCROLLED TO IS A FACT, NOT SOMETHING RECAPTURED. |
| 700 | · THE RAIL, AND WHY IT WAS PART OF THE PANEL. |
| 731 | · THE STAGE IS A STACK NOW. |
| 790 | · AND THE TIME CONTROLS DID NOT FLOAT, WHICH WAS A CORRECTION. |
| 806 | · THE WHEEL WORKS WHERE THE POINTER ALREADY IS. |
| 859 | · FULL SCREEN, AND THE TWO THINGS JAVAFX DOES ABOUT IT THAT WE DO NOT |
| 1135 | A KEPT SCREEN (study UP4, 0.8.26; docs/studies/spec-screens-in-place.md). |
| 1200 | · AND THE SCROLL STAYS WHERE YOU PUT IT. |
| 1270 | · HOLD THE PAGE'S HEIGHT WHILE IT IS REBUILT, so the position is |
| 1307 | · A PAGE TORN DOWN HEARS NOTHING (0.7.50). |
| 1386 | A WHEEL NOTCH IS WORTH THE SAME EVERY TIME |
| 1845 | THE HEADER (0.7.21) |
| 2124 | · · POPULATION, and the month's net |
| 2166 | · · GDP, annualised from month 1, and real growth |
| 2200 | · · INFLATION, against the player's target |
| 2247 | · · OUT OF WORK, with a shortage read as watch |
| 2271 | · · RATE and the currency: the price of money |
| 2334 | · THE MONEY BLOCK (0.7.24) |
| 2990 | · THE INFLATION TILE'S WORDS (0.7.45), pure for the probe |
| 3112 | THE MAIN MENU (0.7.21) |
| 3385 | · WHAT "CONTINUE" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO CONTINUE |
| 3614 | THE SAVE SYSTEM |
| 3945 | QUIT ASKS FIRST (0.7.20) |
| 4001 | A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) |
| 4071 | EVERY TOOLTIP, DRESSED ONCE (0.7.20) |
| 4114 | SETTINGS. |
| 4241 | THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. |
| 4264 | ECONOMY IS GONE, AND IT SPLIT IN TWO. |
| 4281 | THE SCROLLER |
| 4352 | · WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and |
| 4376 | SIMULATE MULTIPLE MONTHS |
| 4487 | · · headlines |
| 4503 | · · deltas |
| 4553 | · · land |
| 4561 | · · buildings |
| 4577 | · · demolitions |
| 4601 | · · health |
| 4645 | · · households |
| 4805 | THE FRAME FOLDS AWAY (0.7.24) |
| 5019 | CONSTRUCTION PANEL |
| 5406 | THE RAIL |
| 5706 | THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF |
| 5892 | THE INBOX |
| 5941 | · · what an urgent notice gets |
| 5959 | · · the list |
| 6009 | · the envelope |
| 6237 | TOASTS (0.7.20) |
| 6321 | TIME, AND WHAT THE MONTH IS WORTH |
| 6490 | THE CHART OVER THE WHOLE WINDOW (0.7.23) |
| 6685 | TWELVE PIPS, AND ONE OF THEM MOVED. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 281 | `UserInterface.STAGE` | `Palette.STAGE` | The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). |
| 462 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 474 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed steps along: a rung a click on the clock's two arrows since 0.7.21, and the stops the speed slider stuck to before. |
| 475 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 525 | `UserInterface.PRESS_HELD` | `"UserInterface.pressHeld"` | The scene property that says a button is down (0.7.40), for what waits on a timer outside this class (TimeChart's settle). |
| 1415 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1840 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1915 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. |
| 1918 | `UserInterface.STRIP_INFLATION_OVER_TARGET` | `.05` | Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. |
| 1921 | `UserInterface.STRIP_DEFLATION_ALARM` | `.10` | Deflation past this reads red, whatever the target: prices collapsing. |
| 1933 | `UserInterface.DATE_WIDTH` | `164` | How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over (0.7.24: at 17 px, so the money block and five tiles fit a 1,280 w... |
| 1936 | `UserInterface.DATE_SIZE` | `17` | The date's size in the clock (0.7.24; it was 19). |
| 1939 | `UserInterface.SPARK_MONTHS` | `120` | How many months a tile's sparkline draws: ten years, or everything recorded if less. |
| 1942 | `UserInterface.SPARK_WIDTH` | `72` | A tile's sparkline at full size: on the label's row since 0.7.24, as the mockups draw it (it was 84 by 30, beside the words). |
| 1944 | `UserInterface.SPARK_HEIGHT` | `16` | ...and its height, the label's row (0.7.24; it was 30). |
| 1947 | `UserInterface.SPARK_MIN` | `30` | Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. |
| 1950 | `UserInterface.TILE_FIGURE` | `15` | The size of a tile's figure (0.7.24: 15, so the money block and five tiles fit a 1,280 window whole; it was 17). |
| 1952 | `UserInterface.TILE_LABEL` | `10.5` | The size of a tile's label, and of the money block's. |
| 1954 | `UserInterface.TILE_CHANGE` | `10.5` | The size of a tile's change line (0.7.24; it was 11). |
| 2415 | `UserInterface.MONEY_FIGURE` | `28` | The cash in the money block: the mockups' 28 px. |
| 3148 | `UserInterface.BACKDROP_BLOCK` | `"#121c28"` | The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. |
| 3149 | `UserInterface.BACKDROP_EDGE` | `"#1d2b3c"` |  |
| 3150 | `UserInterface.BACKDROP_WINDOW` | `"#22344a"` |  |
| 3153 | `UserInterface.FOUNDING_DIM` | `.72` | How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. |
| 3156 | `UserInterface.MENU_CITIES` | `3` | Up to this many of the cities saved last, as cards at the menu's bottom right. |
| 3499 | `UserInterface.MENU_LEFT` | `96` | How far in from the window's left the menu's column and version sit. |
| 3502 | `UserInterface.MENU_BUTTON` | `360` | The menu's buttons' width, as the mockups draw them. |
| 3577 | `UserInterface.CITY_CARD` | `250` | A city card's width, as the mockups draw it. |
| 3626 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 4091 | `UserInterface.TIP_WIDTH` | `420` | The width a tooltip's text wraps at, unless it asked for its own. |
| 4291 | `UserInterface.PAGE_FOOT` | `24` | Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). |
| 4972 | `UserInterface.CONSTRUCTION_TAB` | `44` | How wide the construction panel's tab is. |
| 5269 | `UserInterface.PANEL_TEXT` | `256` | How wide the construction panel's lines wrap: the panel less its padding. |
| 5426 | `UserInterface.RAIL_WIDTH` | `Palette.RAIL` | The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). |
| 5429 | `UserInterface.RAIL_BUTTON` | `54` | A rail button's height, and the least it may shrink to on a short window. |
| 5430 | `UserInterface.RAIL_BUTTON_MIN` | `40` |  |
| 5433 | `UserInterface.RAIL_ICON` | `20` | How big a rail icon is drawn: its 24-unit grid at 20 pixels. |
| 5916 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |
| 6263 | `UserInterface.TOAST_SECONDS` | `8` | How long a toast stays before it fades, in seconds. |
| 6266 | `UserInterface.TOAST_MAX` | `3` | How many toasts at once. |
| 6269 | `UserInterface.TOAST_WIDTH` | `340` | How wide a toast's text wraps. |

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
| 180 | `VBox tabRail` | The navigation rail down the window's left edge (0.7.21; it was the panel's right edge); see start(). |
| 182 | `private MiniMap miniMap` | The minimap at the rail's foot (0.7.134); see THE RAIL. |
| 184 | `private StackPane stagePane` | The middle of the window, with the City overview's drawer and the construction panel (both since 0.7.24), the inbox's list and the toasts on top of it. |
| 186 | `VBox inboxCorner` | The inbox's list, dropped down at the stage's top right while it is open (its envelope is the header's since 0.7.21). |
| 188 | `private VBox toastStack` | The toasts, stacked at the stage's bottom right (0.7.20); see TOASTS. |
| 190 | `private StackPane windowStack` | The whole window, with a dialog laid over it when one is open (0.7.20). |
| 192 | `private StackPane quitDialog` | The dialog that is up - Quit's, or since 0.7.22 a confirmation (confirm()) - or null. |
| 200 | `private boolean cityOpen` | Whether a city has been founded or loaded this session (0.7.20; anotherCity()). |
| 202 | `javafx.scene.control.ScrollPane menuScroller` | The middle column's scroller. |
| 204 | `VBox constructionPanel` | The construction panel: over the stage's right edge while open, folded to constructionTab otherwise (0.7.24). |
| 206 | `private VBox constructionTab` | The slim tab on the stage's right edge the construction panel folds to (0.7.24). |
| 208 | `VBox cityPanel` | The City overview's content, drawn by SummaryScreen. |
| 210 | `private VBox drawer` | The drawer the City overview opens in, over the stage's left (0.7.24); see THE FRAME FOLDS AWAY. |
| 212 | `boolean drawerOpen` | Whether the drawer is open. |
| 232 | `int receiptSeen` |  |
| 233 | `boolean receiptOpen` |  |
| 245 | `FadeTransition receiptPulse` | The pulse, held so it can be stopped. |
| 400 | `HBox header` | The always-visible strip on the BorderPane's top edge: the header (0.7.21; the date bar until then). |
| 401 | `private Scene scene` |  |
| 411 | `Kept.Pool headerPool` | The chrome kept between redraws (UP2, study UP4): the header's nodes and the rail's, made once and handed back to the code that draws them at every redraw, so a month that lands sets their figures and does not throw t... |
| 412 | `Kept.Pool inboxPool` |  |
| 413 | `Kept.Pool railPool` |  |
| 415 | `Kept.Pool cityPool` | The city panel's nodes (SummaryScreen.refreshCityPanel()), the construction panel's, and the inbox's list's. |
| 416 | `Kept.Pool sitesPool` |  |
| 417 | `Kept.Pool listPool` |  |
| 477 | `int speedIndex` | 1x |
| 478 | `boolean clockRunning` |  |
| 479 | `private long lastFrame` |  |
| 486 | `private final ClockPace pace` | The clock's arithmetic (UP2): the share of the month run, the months a frame may take, how long a redraw has to wait and the speed achieved. |
| 501 | `private boolean redrawPending` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 502 | `private javafx.animation.AnimationTimer clock` |  |
| 518 | `private boolean pressHeld` | A PRESS IS NEVER REBUILT AWAY (0.7.40). |
| 520 | `private final java.util.EnumSet<javafx.scene.input.MouseButton> buttonsDown` | The buttons down, each counted from its press to its release, so one let go while another is held still holds. |
| 523 | `private boolean letGo` | Set when a release has been delivered, and spent by the next frame: the first frame after a release. |
| 528 | `private Label dayLabel` | The date line, kept so a day can be repainted without a whole redraw. |
| 531 | `String pausedBecause` | Why the clock stopped itself, shown until it is started again. |
| 534 | `private String pausedOnKey` | The notice that last stopped it, so one condition interrupts once. |
| 1378 | `private double pageScrollAt` | Where the player has scrolled the page to. |
| 1381 | `private boolean settlingScroll` | True while a rebuild is in flight, so its clamps are not mistaken for a hand. |
| 1384 | `private boolean correcting` | Guards the re-entry when the listener corrects a clamp of its own. |
| 1418 | `String currentScreen` | Which show*Menu drew what is on screen; see clearMenu. |
| 1421 | `private boolean tearingDown` | True while clearMenu empties the page: rootMenu swallows the pointer's exits from what it removes (0.7.50). |
| 1424 | `private Runnable redrawScreen` | How to draw it again after a month passes; see clearMenu. |
| 1435 | `private Runnable resumeTo` | The screen Esc was pressed on, so Continue (Resume until 0.7.21) can go back to it. |
| 1463 | `final java.util.Map<String, Double> innerScrollAt` | Where a scroller inside the CURRENT screen was left, by name. |
| 1473 | `private final java.util.Map<String, Double> panelScrollAt` | The same, for the two side panels - and this one is NEVER emptied. |
| 1843 | `private long lastWheelNanos` | When the last wheel event moved the page; see scrollPageBy. |
| 1957 | `private Label monthFigure` | The month's figure in the clock, kept so it pops when a month lands. |
| 1960 | `private Label speedReading` | The speed's reading between its two arrows, kept so a click changes it in place. |
| 1963 | `private int clockAt` | The month the clock last showed, so the month's figure pops only when it moves. |
| 1966 | `private StackPane inboxHolder` | Where the inbox's envelope is drawn in the header, so a notice read elsewhere can redraw its count. |
| 2512 | `private double gap` |  |
| 2583 | `private final Label label` |  |
| 2584 | `private final HBox labelRow` |  |
| 2585 | `private final Label figure` |  |
| 2586 | `private final List<Label> changes` |  |
| 2587 | `private final Sparkline spark` |  |
| 2589 | `private javafx.scene.Node info` | The (i) in the label's row, or null for a tile with none. |
| 2715 | `private double[] points` |  |
| 2716 | `private int[] at` |  |
| 2717 | `private javafx.scene.paint.Color colour` |  |
| 2718 | `private javafx.scene.canvas.Canvas canvas` |  |
| 2719 | `private double drawnW` |  |
| 2720 | `private int firstMonth, lastMonth` |  |
| 2722 | `private boolean told` | Whether set() has been told a series yet: the first one always draws. |
| 3142 | `private StackPane titleLayer` | The layer the main menu and the founding screen draw on - and, with no city open, Settings, Load and Save (0.7.22): the backdrop, a dimmer, and what the screen puts on it. |
| 3143 | `private javafx.scene.canvas.Canvas backdrop` |  |
| 3144 | `private Region titleDim` |  |
| 3145 | `private StackPane titleContent` |  |
| 3250 | `private Runnable placeMenu` | How the menu's column is placed for the window's height; run again when it changes. |
| 3335 | `private final java.util.Map<Integer, KnownSave> knownSaves` |  |
| 3926 | `GamePrefs prefs` | How the player likes the window. |
| 4837 | `private Region needsChipNode` | The chip, kept so a click outside the drawer can repaint it without rebuilding the header under the pointer. |
| 4838 | `private Runnable restyleNeedsChip` |  |
| 4846 | `private HBox drawerBarBox` | The drawer's own bar: the pin and the close, refilled as the pin changes. |
| 5810 | `private boolean railJump` | Set for exactly one clearMenu, by goHome(). |
| 5919 | `boolean inboxOpen` | Whether the list is dropped down. |
| 5922 | `String inboxExpanded` | Which notice's body is unfolded, by key. |
| 6272 | `private final java.util.Set<String> toasted` | The notices already toasted, as key@raised. |
| 6509 | `private javafx.scene.Node chartFull` | The chart's pane while it has the window, its clock line, and what closes it; null otherwise. |
| 6510 | `private Label chartFullClock` |  |
| 6511 | `private java.util.function.Consumer<Boolean> chartFullLeave` |  |
| 6514 | `private String chartFullOwner` | The screen that laid the pane (a clearMenu() name): its own redraws keep it, any other screen closes it (0.7.61: City History's chart, the land office's map). |
| 6517 | `private MapView panKeys` | The map W, A, S and D pan while it is over the window (0.7.135, batch E9): MapView's expand() sets it, its collapse() clears it. |
| 6563 | `private double chartFullTop` | The overlay's top margin while it lies under the header (MS1): the header's height, 0 when it covers the window. |
| 6566 | `private javafx.beans.value.ChangeListener<Number> chartFullHeaderHeight` | ...and the listener holding it there, or null. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 6667 | **type** `public class UserInterface extends Application` | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating and the inbox - the rail down the left, the City overview's drawer and ... |

### THE SCREENS, one class each since 2026-09-18, in the order the rail (lines 57-79)

### WHERE THE SCREENS WENT. Each line is a run of this file's banner (lines 80-213)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 1 | `public UserInterface()` | JavaFX needs the no-arg one; a harness needs one bound to a city. |
| 174 | 1 | `public UserInterface(Game game)` |  |

### THE RECEIPT INDICATOR (lines 214-246)

### THE THEME (lines 247-433)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 115 | `void applyTheme(Scene target)` |  |
| 420 | 3 | `javafx.scene.Node miniMapNode()` | The minimap's node in the rail, or null before the rail is first drawn (KeptBench keeps it off the picture). |
| 425 | 8 | `void forgetChrome()` | Every kept node of the chrome let go: the next draw of the header, the envelope and the rail makes them all new. |

### THE CLOCK (lines 434-1134)

| line | len | member | says |
|---:|---:|---|---|
| 537 | 588 | `public void start(Stage primaryStage)` |  |
| 1131 | 3 | `void clearMenu(String screen, Runnable again)` | Clears the menu area and refreshes the construction panel. |

### A KEPT SCREEN (study UP4, 0.8.26; docs/studies/spec-screens-in-place.md). (lines 1135-1385)

| line | len | member | says |
|---:|---:|---|---|
| 1154 | 22 | `void keepMenu(String screen, Runnable again, Kept.Pool pool, Runnable page)` | Draws a kept screen: its page code run as a Kept draw from the screen's pool - over last time's nodes when the same screen is redrawn, afresh on arrival (the rail's own tab, pressed, arrives afresh: it goes to the top). |
| 1178 | 6 | `void setPage(javafx.scene.Node...nodes)` | The page's top nodes put in the menu column: left alone when they are already the ones there (a kept redraw). |
| 1190 | 186 | `private void clearMenu(String screen, Runnable again, boolean keepPage)` | clearMenu, and for a kept screen's redraw (keepPage, the same screen again) everything but the page: the chrome is refreshed and the screen's bookkeeping kept, but nothing is emptied, held or settled, since the page i... |

### A WHEEL NOTCH IS WORTH THE SAME EVERY TIME (lines 1386-1844)

| line | len | member | says |
|---:|---:|---|---|
| 1432 | 1 | `void redraw()` | Draw the screen that is showing, again, in place - the same call the clock makes after a month. |
| 1446 | 10 | `private static boolean isGameMenu(String screen)` | The screens that are ABOUT the game rather than in it. |
| 1476 | 3 | `private javafx.scene.control.ScrollPane keptScroller(String key, javafx.scene.Node content)` | A scroller inside a screen, which remembers where it was while you stay. |
| 1493 | 3 | `private javafx.scene.control.ScrollPane keptScrollerFromBottom(String key, javafx.scene.Node content)` | The same, remembering the distance from the BOTTOM rather than the fraction - for a page whose top half changes height under the player. |
| 1498 | 4 | `javafx.scene.control.ScrollPane keptPanelScroller(String key, javafx.scene.Node content)` | A scroller in one of the side panels, which always remembers. |
| 1510 | 9 | `private javafx.scene.control.ScrollPane remembering(java.util.Map<String, Double> where, String key, javafx.scene.Node content,...` | A ScrollPane that files its own position under a name and comes back to it. |
| 1525 | 11 | `javafx.scene.control.ScrollPane keptScroller(java.util.Map<String, Double> where, String key, javafx.scene.Node content)` | A scroller in a panel that is drawn kept (UP2): the one the last draw made, given its content - which is the same node when its box is kept too, so it keeps its position by itself - and the position of the map put on ... |
| 1538 | 23 | `private void remember(javafx.scene.control.ScrollPane scroller, java.util.Map<String, Double> where, String key, boolean fromBo...` | The position under a name: written as the scroller moves, and put back on a new one. |
| 1563 | 6 | `private static double scrollSpan(javafx.scene.control.ScrollPane scroller)` | How far a scroller's content can travel, in pixels, as laid out right now. |
| 1599 | 3 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, double to)` | Put it back, and put it back BEFORE anything is painted. |
| 1608 | 113 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, java.util.function.DoubleSupplier target)` | The same, with the position asked for afresh at each of the three attempts - so a caller keeping pixels rather than a fraction can turn them into a fraction of the height the page actually has by then. |
| 1747 | 35 | `private double pageSpan(javafx.scene.Node page)` | How far the page can travel: what the content WANTS to be, less the viewport. |
| 1792 | 14 | `private void wheelToPage(javafx.scene.input.ScrollEvent wheel)` | A wheel turn nothing else wanted, spent on the page. |
| 1827 | 11 | `private void scrollPageBy(double deltaY, double span)` | Move the page by one wheel event's worth, with a floor under the FIRST event of a gesture and the rest taken as they come. |

### THE HEADER (0.7.21) (lines 1845-2333)

| line | len | member | says |
|---:|---:|---|---|
| 1975 | 10 | `static VBox constructionPanelBox()` | The construction panel's box, empty and styled. |
| 1995 | 8 | `static VBox cityPanelBox()` | The city panel's box, empty and styled. |
| 2005 | 5 | `static VBox inboxCornerBox()` | The box the inbox's list is dropped into, empty: start() puts it at the stage's top right. |
| 2012 | 10 | `static HBox headerBox()` | The header's box, empty and styled: start() puts it across the window's top and KeptBench in a scene of its own. |
| 2024 | 11 | `static VBox railBox()` | The rail's box, empty and styled: start() puts it at the window's left and KeptBench in a scene of its own. |
| 2041 | 10 | `static void dropFocusIn(javafx.scene.Node container)` | A node of the chrome that had the focus loses it, as it did when the chrome was rebuilt under it: a play button, a speed arrow or a tile clicked keeps no focus ring now that its node survives the redraw, and the keys ... |
| 2058 | 4 | `void refreshHeader()` | The header, drawn kept (UP2): the clock, the money block, the five tiles, the "Needs you" chip, the rating and the inbox. |
| 2063 | 21 | `private void drawHeader()` |  |
| 2093 | 3 | **type** `private record Headline(String label, String area, String value, String valueTone, String[] change, String ...` | One tile, as the header draws it. |
| 2102 | 1 | `private static String[] words(String...longestFirst)` | A change line, longest wording first: the tile shows the longest that fits its width (HeadlineTile), so a line is shortened and never cut (0.7.24). |
| 2114 | 3 | `public static double[] sparkSeries(HistorySave h, String key)` | The series a header tile's sparkline draws (UP2): the last SPARK_MONTHS months of the line City History draws for the tile's key, and no more of the history than that. |
| 2119 | 175 | `private List<Headline> headlines()` | The five, in the mockups' order: people, output, prices, work, the price of money - TREASURY is its own block since 0.7.24 (moneyBlock()). |
| 2307 | 26 | `private String treasuryWhy(double income, double moved)` | The money block's (i) - the TREASURY tile's until 0.7.24: the three figures, EARNED, SURPLUS and BANKED (0.7.31's names, the same on the Government tab), and why they differ. |

### THE MONEY BLOCK (0.7.24) (lines 2334-2989)

| line | len | member | says |
|---:|---:|---|---|
| 2362 | 51 | `private Region moneyBlock()` | The cash at its own size, what the month EARNED under it (not how far the month moved the cash: see above), and a door to Finances. |
| 2425 | 35 | `private String moneyWhy(DebtManager market, ForeignAccounts fx, String here)` | The rate tile's (i): the price of money three ways (0.7.4) - Jerus: "the bank rate, the central bank rate, both should be shown, as well as the rate you borrow in" - and the currency both ways, against its parity. |
| 2469 | 18 | `private Region headlineTile(Headline t, List<Integer> axis)` | One tile (0.7.24, the mockups' shape): its label in its area's colour and a small sparkline on the top row, the figure under them, and the change line across the tile's whole width - in the longest of its wordings tha... |
| 2497 | 4 | `private void openHistory(String[] keys)` | Opens City History with these lines picked: the tiles' click. |
| 2511 | 61 | **type** `private static final class HeadlineRow extends javafx.scene.layout.Pane` | The tiles, side by side: equal shares of the row, except that a tile never gets less than its least width - its figure until 0.7.24, the widest of its label, its figure and its shortest change line since (HeadlineTile... |
| 2513 | 1 | `HeadlineRow()` _(in UserInterface.HeadlineRow)_ |  |
| 2514 | 1 | `HeadlineRow(double gap)` _(in UserInterface.HeadlineRow)_ |  |
| 2517 | 5 | `void setGap(double gap)` _(in UserInterface.HeadlineRow)_ | The gap between the tiles, set at every draw: the layout is asked for only when it moved. |
| 2523 | 5 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 2529 | 5 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineRow)_ |  |
| 2535 | 5 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineRow)_ |  |
| 2541 | 30 | `protected void layoutChildren()` _(in UserInterface.HeadlineRow)_ |  |
| 2582 | 122 | **type** `private static final class HeadlineTile extends Region` | One tile's inside (0.7.24): the label at the top left and the sparkline at the top right in what the label leaves, up to SPARK_WIDTH - none at all under SPARK_MIN; the figure under them; and the change line across the... |
| 2595 | 8 | `HeadlineTile()` _(in UserInterface.HeadlineTile)_ | A tile's parts, made once (UP2): the picture is told its figures by set() at every draw, as the People page's Mirror is, and its parts are its own (made with new, never through Kept). |
| 2612 | 28 | `void set(Headline t, List<Integer> axis, javafx.scene.Node more)` _(in UserInterface.HeadlineTile)_ | The tile told what it shows: its label, figure and change wordings set (a label whose words are the same is not laid out again), the sparkline given the series, the (i) put in or taken out of the label's row. |
| 2641 | 8 | `private double shortest()` _(in UserInterface.HeadlineTile)_ |  |
| 2650 | 3 | `private double changeHeight()` _(in UserInterface.HeadlineTile)_ |  |
| 2654 | 4 | `protected double computeMinWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2659 | 5 | `protected double computePrefWidth(double height)` _(in UserInterface.HeadlineTile)_ |  |
| 2665 | 3 | `protected double computeMinHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2669 | 4 | `protected double computePrefHeight(double width)` _(in UserInterface.HeadlineTile)_ |  |
| 2674 | 29 | `protected void layoutChildren()` _(in UserInterface.HeadlineTile)_ |  |
| 2714 | 119 | **type** `static final class Sparkline extends Region` | A tile's sparkline: the last SPARK_MONTHS of its series, a month a point, the months recorded as nothing (NaN) left out; a faint fill under the line and a dot on the latest month, in its area's colour. |
| 2725 | 7 | `Sparkline()` _(in UserInterface.Sparkline)_ | A sparkline with no series yet, told its figures by set() (the header's tiles keep theirs, UP2). |
| 2733 | 4 | `Sparkline(double[] series, List<Integer> months, String colour)` _(in UserInterface.Sparkline)_ |  |
| 2743 | 29 | `void set(double[] series, List<Integer> months, String colour)` _(in UserInterface.Sparkline)_ | The line told its figures: the points, the months they sit at and the colour. |
| 2773 | 5 | `protected void layoutChildren()` _(in UserInterface.Sparkline)_ |  |
| 2779 | 53 | `private void paint(double w, double h)` _(in UserInterface.Sparkline)_ |  |
| 2840 | 93 | `private HBox clockBlock(boolean inCity)` | The clock, docked at the header's left (0.7.21): the play button, the date, the month's figure and the speed - and why the clock stopped itself, when it did. |
| 2935 | 16 | `private Label speedArrow(String glyph, int step)` | One of the speed's two arrows: a rung down or up the ladder, the reading changed in place. |
| 2953 | 27 | `private HBox ratingAndInbox(boolean inCity)` | The "Needs you" chip, the credit rating's chip and the inbox's envelope, at the header's right. |
| 2982 | 7 | `static String ratingColour(String rating)` | A rating's verdict: investment grade is good, BB and B a watch, below that bad. |

### THE INFLATION TILE'S WORDS (0.7.45), pure for the probe (lines 2990-3111)

| line | len | member | says |
|---:|---:|---|---|
| 2998 | 10 | `static String[] anchorLine(Game g)` | The INFLATION tile's line, longest first: what people expect inflation to be and how far they believe the bank - "expect 2.2% · trust 82%", "exp 2.2% · 82%", "exp 2.2%" - and before the basket is based, when everyone ... |
| 3010 | 4 | `static String anchorTone(Game g)` | ...its tone: NEEDS YOU's PRICES row - plain at rest, amber in a month trust fell, red when it fell under half. |
| 3016 | 17 | `static String inflationTip(Game g, String off)` | ...its tooltip: the year's rate, how far off the target (`off`, the old line, or null with no rate yet), what people expect and why, and the level. |
| 3035 | 27 | `static String inflationMore(Game g)` | ...and its (i): the figure's colours, the trust paragraph, and before the first rate when it comes. |
| 3064 | 8 | `static String ordinal(int n)` | "sixtieth", "twenty-fourth": a month's share of the way, as the (i) says it. |
| 3088 | 6 | `private static String inflationColour(double inflation, double target)` | Good near the player's target, amber off it, red once prices run past it or collapse. |
| 3096 | 5 | `private static String rateColour(ForeignAccounts fx)` | Secondary grey near parity, amber past ForeignAccounts.PARITY_WATCH either side, red past PARITY_FAR (0.7.35: the one parity rule; a pinned rate is grey). |

### THE MAIN MENU (0.7.21) (lines 3112-3613)

| line | len | member | says |
|---:|---:|---|---|
| 3162 | 39 | `private void showTitleLayer(boolean show, boolean dim)` | The title layer shown or taken away, and emptied for the screen about to draw on it. |
| 3203 | 1 | `StackPane titleContent()` | Where the main menu and the founding screen put what they draw, and menuPage() its panel (0.7.22). |
| 3216 | 9 | `private static boolean coldMenu(String screen)` | The menu's own screens, which draw over the backdrop while no city is open. |
| 3227 | 21 | `private VBox menuPage()` | Where one of those screens draws: the middle column in a city; a panel over the backdrop, centred and scrolling if it must, when none is open. |
| 3260 | 72 | `static void drawBackdrop(javafx.scene.canvas.GraphicsContext g, double w, double h)` | The backdrop: the city's line rising behind a skyline of flat blocks, a few of their windows lit in the four area colours. |
| 3334 | 1 | **type** `private record KnownSave(long modified, long size, SaveHeader header)` | A save's header, kept while its file is unchanged, so the menu can read eleven slots without parsing eleven files every time it opens. |
| 3338 | 15 | `private SaveHeader headerOf(int slot)` | A slot's header, read again only when its file has changed; null for an empty or unreadable slot. |
| 3355 | 7 | `private static String savedWhen(long savedAt)` | "saved 20:38" today, "saved 29 Sep 20:38" this year, with the year before that. |
| 3363 | 134 | `void showMainMenu()` |  |
| 3505 | 27 | `private Button menuButton(String text, String under, boolean primary)` | One of the menu's buttons: a word, and under the primary one what it goes back to. |
| 3544 | 31 | `private VBox yourCities(double wide)` | YOUR CITIES: the slots saved last, newest first, as cards - the city's name, its slot, its people and its month, and when it was saved. |
| 3580 | 33 | `private VBox cityCard(int slot, SaveHeader header)` | One saved city: its name and slot, its people and month, when it was saved; a click loads it. |

### THE SAVE SYSTEM (lines 3614-3944)

| line | len | member | says |
|---:|---:|---|---|
| 3630 | 34 | `private String slotSummary(int slot)` | One line describing what is in a slot, or that it is empty. |
| 3665 | 13 | `private String slotTitle(int slot)` |  |
| 3680 | 30 | `private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty)` | A slot row: the label on the button, the city underneath it. |
| 3711 | 23 | `private void showSavingMenu()` |  |
| 3741 | 42 | `private void showSaveSlotConfirm(int slot)` | Confirms one slot, and takes the optional name. |
| 3784 | 22 | `private void showLoadMenu()` |  |
| 3807 | 27 | `private void loadSlot(int slot)` |  |
| 3842 | 10 | `private void openCity()` | Where a city opens. |
| 3864 | 18 | `private void anotherCity()` | Another city has just replaced the one on screen - founded, or loaded (0.7.20): its toasts and its purchases are not this one's. |
| 3884 | 5 | `void foundCity(Founding choices)` | The founding screen's last step: found the city as chosen, and open it. |
| 3890 | 34 | `private void showSaveResult(GameFiles.Result result)` |  |
| 3935 | 9 | `private void toggleFullScreen()` | In and out of full screen, and remembered. |

### QUIT ASKS FIRST (0.7.20) (lines 3945-4000)

| line | len | member | says |
|---:|---:|---|---|
| 3961 | 32 | `private void showQuitDialog()` |  |
| 3995 | 5 | `private void closeQuitDialog()` | Close the dialog that is up, Quit's or a confirmation: Cancel, or Esc. |

### A CONFIRMATION, WITH THE MONEY IN IT (0.7.22) (lines 4001-4070)

| line | len | member | says |
|---:|---:|---|---|
| 4013 | 57 | `void confirm(String ask, String why, java.util.List<String[]> lines, String yesText, Runnable yes)` |  |

### EVERY TOOLTIP, DRESSED ONCE (0.7.20) (lines 4071-4113)

| line | len | member | says |
|---:|---:|---|---|
| 4093 | 13 | `private void dressTooltips()` |  |
| 4108 | 5 | `private static void hideTooltips()` | Every tooltip showing, put away - so one is not left hanging over the menu Esc opens. |

### SETTINGS. (lines 4114-4240)

| line | len | member | says |
|---:|---:|---|---|
| 4122 | 78 | `private void showSettingsMenu()` |  |
| 4211 | 29 | `private VBox toggleRow(String label, boolean on, String what, Runnable flip)` | A setting: what it is, what it does, and a switch that says which way it is set without having to read the word next to it. |

### THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. (lines 4241-4263)

### ECONOMY IS GONE, AND IT SPLIT IN TWO. (lines 4264-4280)

### THE SCROLLER (lines 4281-4351)

| line | len | member | says |
|---:|---:|---|---|
| 4294 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column)` | A left-aligned column inside a scroll pane, which these screens all want. |
| 4305 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome)` | this scroller - a title alone is about 150; a title with a vitals bar and two strips pinned over it is a good deal more, and getting it wrong is a scrollbar that appears when there is nothing to scroll. |
| 4314 | 37 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome, boolean fromBottom)` | The same, with the position kept from the bottom of the page instead of as a fraction (fromBottom) - for a page that grows and shrinks ABOVE the controls the player is using; see keptScrollerFromBottom. |

### WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and (lines 4352-4375)

| line | len | member | says |
|---:|---:|---|---|
| 4360 | 1 | `public List<String> whatItDoes(BuildingsTemplate t)` |  |
| 4361 | 1 | `public List<String> whatCareItGives(BuildingsTemplate t)` |  |
| 4362 | 1 | `public String jobLabel(JobType job)` |  |
| 4365 | 6 | `public List<String> buildPages()` | Build's pages as the window names them: where it opens (BuildScreen.BUILD_HOME), then the strip's categories in order. |
| 4373 | 1 | `public String buildOpensOn()` | The page Build opens on in this window now: the Overview, until the player opens a category (0.7.24). |

### SIMULATE MULTIPLE MONTHS (lines 4376-4804)

| line | len | member | says |
|---:|---:|---|---|
| 4384 | 53 | `private void showSimulateMonthsMenu()` |  |
| 4449 | 222 | `private void showSimulateResultMenu(int requested, int completed)` | What happened while the player was not watching. |
| 4673 | 10 | `private void addSkipLine(VBox section, String label, double start, double end, double change, boolean isMoney)` | "Population  192 -> 664  (+472)", coloured by direction. |
| 4692 | 22 | `private void addChangeLine(VBox section, String label, double change, boolean isMoney, boolean higherIsBetter)` | A signed change, coloured by whether it is good news. |
| 4716 | 3 | `void showSectorReport(String title, VBox column, Runnable back)` | Shared scaffolding for the sector report screens - of which none is left: Household money, the last, became a page of its own in 0.7.27, and nothing calls this now. |
| 4726 | 77 | `void showSectorReport(String title, VBox column, Runnable back, Button extra)` | somewhere else - e.g. the industrial report linking to its financial statements. |

### THE FRAME FOLDS AWAY (0.7.24) (lines 4805-5018)

| line | len | member | says |
|---:|---:|---|---|
| 4849 | 51 | `private Region needsChip()` | "Needs you" and the count, or "Nothing needs you": the drawer's door. |
| 4908 | 9 | `private void openOnNeeds()` | The drawer as the chip opens it: on NEEDS YOU, the list the chip counts - Summary whatever mode is stored (SummaryScreen.needsView, the stored mode untouched), at the top of its scroller, where NEEDS YOU is under the ... |
| 4919 | 6 | `private HBox drawerBar()` | The drawer's bar: pin it open across screens, or close it. |
| 4926 | 26 | `private void refreshDrawerBar()` |  |
| 4954 | 10 | `private void placeFrame()` | The drawer, the construction panel and its tab, shown as the player left them - and none of it on the menus. |
| 4966 | 4 | `private static boolean inside(javafx.scene.Node node, javafx.scene.Node in)` | Whether a node is this one or inside it. |
| 4975 | 43 | `private void refreshConstructionTab(boolean inCity, boolean open)` | The tab the construction panel folds to: a crane, how many sites, and the words down it. |

### CONSTRUCTION PANEL (lines 5019-5405)

| line | len | member | says |
|---:|---:|---|---|
| 5044 | 4 | `void refreshConstructionPanel()` | The construction panel, drawn kept (UP2): the nodes the last draw made, with this month's sites set on them (sitesPool). |
| 5049 | 7 | `private void drawConstructionPanel()` |  |
| 5057 | 21 | `private HBox constructionHead()` |  |
| 5080 | 67 | `private void constructionBody(List<javafx.scene.Node> into, BuildingManager buildingManager)` | ...and what is under the head: the output, the sites or the idle line, the logs. |
| 5148 | 36 | `private void constructionSites(List<javafx.scene.Node> into, BuildingManager buildingManager, List<BuildingsStacks> sites, int ...` |  |
| 5186 | 71 | `private VBox siteRow(BuildingsStacks site, BuildingManager.Plan sitePlan, int forSites)` | One site's row: its name, its bar, and its months. |
| 5259 | 8 | `static Region areaSwatch(String colour, double size)` | A small square in an area's colour, before a heading (0.7.21): the pages' titles' swatch, smaller. |
| 5281 | 11 | `private void addDemolitionLog(List<javafx.scene.Node> into)` | What the city has lost lately, under what it is building. |
| 5293 | 48 | `private javafx.scene.Node demolishedLog(List<javafx.scene.Node> into, java.util.List<DemolitionLog.Entry> lost)` |  |
| 5354 | 13 | `private void addBuildLog(List<javafx.scene.Node> into)` | What the city has GAINED lately, above what it has lost. |
| 5368 | 36 | `private javafx.scene.Node builtLog(List<javafx.scene.Node> into, java.util.List<BuildLog.Entry> built)` |  |

### THE RAIL (lines 5406-5705)

| line | len | member | says |
|---:|---:|---|---|
| 5445 | 1 | **type** `private record Tab(String key, String svg, String label, String name, Runnable go)` | One destination. |
| 5452 | 14 | `static String areaOf(String key)` | Which of the four areas a rail key is in (0.7.21): building and land, people and services, business and trade, money and policy. |
| 5473 | 15 | `Label pageTitle(String text)` | A page's title, with its area's swatch before it (0.7.21): a small square in the colour of the rail tab that owns the screen showing, as the mockups set the title. |
| 5519 | 54 | `private Tab[] tabs()` | The rail, in the order a city is actually run. |
| 5584 | 56 | `private String tabFor(String screen)` | Which tab owns the screen that is showing. |
| 5646 | 10 | `void refreshTabRail()` | The rail, drawn kept (UP2): the twelve destinations, the gap, the minimap and Menu. |
| 5657 | 37 | `private void drawTabRail()` |  |
| 5701 | 4 | `void openMapOn(MiniMapFrame.Box frame)` | The minimap's click (0.7.134): the land office, as its rail button opens it, and its map over the window on the minimap's frame - the city's built-up ground (MiniMapFrame.frame(); none yet, as Expand opens it). |

### THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF (lines 5706-5891)

| line | len | member | says |
|---:|---:|---|---|
| 5754 | 5 | `private void goHome(Tab tab)` | Press a tab: forget where you were inside it - Build's category apart, since 0.7.20 - and land at the top. |
| 5781 | 20 | `private void resetSection(String key)` | A section's own idea of where you were, forgotten. |
| 5827 | 64 | `private VBox railButton(String svg, String label, String name, String area, boolean active, Runnable go)` | One button on the rail: its icon over its name (0.7.21). |

### THE INBOX (lines 5892-6008)

| line | len | member | says |
|---:|---:|---|---|
| 5929 | 5 | `void refreshInbox()` | The envelope, the toasts and the list under the envelope when it is open. |
| 5936 | 16 | `void refreshInboxList()` | ...the part of it under the envelope: the toasts and the list. |
| 5953 | 55 | `private void drawInboxList()` |  |

### the envelope (lines 6009-6236)

| line | len | member | says |
|---:|---:|---|---|
| 6016 | 5 | `void refreshInboxButton()` |  |
| 6022 | 51 | `private void drawInboxButton()` |  |
| 6087 | 66 | `private VBox noticeRow(Notice notice)` | One notice: its title, and its body when it is unfolded. |
| 6154 | 18 | `private String dealLabel(String key)` |  |
| 6182 | 54 | `private void deal(Notice notice)` | Take the player to the control that answers it. |

### TOASTS (0.7.20) (lines 6237-6320)

| line | len | member | says |
|---:|---:|---|---|
| 6275 | 11 | `private void refreshToasts()` | New urgent notices become toasts; read or settled ones leave the stack. |
| 6288 | 32 | `private Label toast(Notice notice)` | One toast: the notice's title, a timer, and a door to the inbox. |

### TIME, AND WHAT THE MONTH IS WORTH (lines 6321-6489)

| line | len | member | says |
|---:|---:|---|---|
| 6351 | 45 | `private void startClock()` | Starts the frame loop. |
| 6398 | 4 | `private void drawPaced()` | The clock's redraw of the open screen: begun for the pace, which takes its cost when the layout pass after it is done. |
| 6412 | 15 | `private void holdPresses()` | A PRESS IS NEVER REBUILT AWAY (0.7.40; see the clock's fields): a press anywhere in the window holds the clock's redraw, and the release lets it go once the release - and the click the scene makes of it - has been del... |
| 6429 | 4 | `private void letAllGo()` | No button is down after all: a move without one, or the window gone from under the pointer. |
| 6435 | 5 | `private void setPressHeld(boolean held)` | Holds the redraw, or lets it go - and says so on the scene, for TimeChart. |
| 6447 | 4 | `void redrawSoon()` | The open screen redrawn on the next frame no button is held on (0.7.40): for a change made as a press takes the focus - Finances' ask, set as its box is left - which a redraw at once would take from under the release. |
| 6453 | 4 | `static boolean pressHeld(javafx.scene.Node node)` | Whether a mouse button is down in the scene a node is in (0.7.40): what a redraw on a timer waits for. |
| 6465 | 6 | `private void paintDay()` | The day, repainted in place. |
| 6478 | 4 | `private String speedText()` | The speed's reading between the arrows (UP2): the rung, and after it the speed the months landed over the last two seconds add up to when that is more than a tenth under the rung - "50× · 31×". |
| 6484 | 5 | `private void paintSpeed()` | The reading, repainted in place and only when its words change: no rebuild, and no layout for a figure that is the same. |

### THE CHART OVER THE WHOLE WINDOW (0.7.23) (lines 6490-6684)

| line | len | member | says |
|---:|---:|---|---|
| 6520 | 1 | `void panKeysTo(MapView map)` | ...set, or cleared with null. |
| 6528 | 3 | `void showChartFullScreen(Region pane, Label clockLine, java.util.function.Consumer<Boolean> leave)` | Lays the chart's pane over the whole window. |
| 6540 | 21 | `void showChartFullScreen(Region pane, Label clockLine, java.util.function.Consumer<Boolean> leave, boolean underHeader)` | ...or, with `underHeader` (MS1, the expanded map's: Jerus, 2026-10-26, "i should be able to see the top part of the screen aka population, gdp, treasury, rate, exchange rate inflation all that i already see"), laid ov... |
| 6569 | 3 | `private double headerHeight()` | The header's height as laid out, never under its own minimum (it has not been laid out before its first pulse). |
| 6574 | 1 | `double chartFullTop()` | How far down the window the overlay starts: the header's height while it lies under it (MapView.fitPane() sizes the canvas to the room under), 0 over the whole window. |
| 6577 | 9 | `private void letGoOfHeader()` | The header's listener removed and the overlay's margin back to none: the pane may be laid again, over all of the window or under it. |
| 6593 | 3 | `private void mapAwayFromHeader()` | A header control that opens a panel under the pane (the Needs-you chip's drawer, the inbox's list) takes the map away first (MS1), as another screen's coming does (clearMenu()): the panel would open hidden under it. |
| 6598 | 8 | `void closeChartFullScreen()` | Takes the pane off the window; the screen redraws its page. |
| 6608 | 4 | `private void leaveChartFullScreen(boolean redraw)` | Esc, P or another screen: the screen's own way back, which calls closeChartFullScreen(). |
| 6614 | 1 | `boolean isChartFullScreen()` | Whether a chart has the whole window. |
| 6617 | 1 | `boolean isShowing(String screen)` | Whether this screen - a clearMenu() name, "showHistoryMenu" - is the one on show: a late redraw asks before it draws. |
| 6624 | 3 | `double clockMonths()` | The game's clock in months (0.7.97, batch O13): the month and the share of the next the clock has run - what the map's boats sail on (MapView.BOAT_GAME_MONTHS). |
| 6629 | 4 | `String clockWords()` | The date and what the clock is doing, for a line that stands in for the header: "14 February 2151 · paused". |
| 6635 | 8 | `private static boolean overAChart(Object target)` | Whether a wheel event's target is inside a chart that takes the wheel (TimeChart.WHEEL_OWNER). |
| 6657 | 11 | `private boolean stopIfSomethingHappened()` | Stops the clock when the city has something to say, if the player wants that. |
| 6670 | 8 | `private void setClockRunning(boolean run)` | Play, or pause. |
| 6680 | 4 | `private static String speedLabel(int index)` | "0.25×", "1×", "10×" - no trailing zeros on the round ones; the header's speed reads it (0.7.21: "×", the mockups', for "x"). |

### TWELVE PIPS, AND ONE OF THEM MOVED. (lines 6685-6719)

| line | len | member | says |
|---:|---:|---|---|
| 6706 | 13 | `static void popPip(Region pip)` | A quarter second of "that landed", on the figure the month just changed - and since 0.7.26 on the land office's figures the month moved. |

