# UserInterface.java - 4,316 lines · 76 methods · 18 constants · interface

`ham/citybuildersim/ui/UserInterface.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The window: the stage and its theme, the clock and the speed ladder, the two
> strips, the rail down the left and the inbox, the left panel and the
> construction panel, the save and settings dialogs, the time-skip dialog, and
> the scroller every screen draws into. Everything a tab SHOWS is a class of
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

**Uses:** [Palette](Palette.md) (37), [Icons](Icons.md) (13), [CityCalendar](CityCalendar.md) (11), [Currency](Currency.md) (10), [GameFiles](GameFiles.md) (10), [Debt](Debt.md) (5), [DemolitionLog](DemolitionLog.md) (5), [Notice](Notice.md) (5), [Game](Game.md) (4), [ServicesScreen](ServicesScreen.md) (4), [GamePrefs](GamePrefs.md) (4), [BuildScreen](BuildScreen.md) (3), [SectorScreen](SectorScreen.md) (3), [FinancesScreen](FinancesScreen.md) (3), [BankScreen](BankScreen.md) (3), [TradeScreen](TradeScreen.md) (3), [PolicyScreen](PolicyScreen.md) (3), [DebtManager](DebtManager.md) (3), [SaveHeader](SaveHeader.md) (3), [TimeSkipReport](TimeSkipReport.md) (3), [LandScreen](LandScreen.md) (2), [PeopleScreen](PeopleScreen.md) (2), [GovernmentScreen](GovernmentScreen.md) (2), [HistoryScreen](HistoryScreen.md) (2), [SummaryScreen](SummaryScreen.md) (2), [GameVersion](GameVersion.md) (2), [Health](Health.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [GameLog](GameLog.md) (2), [WorldEconomy](WorldEconomy.md) (2)... and 11 more

**Used by (14):** [BankScreen](BankScreen.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [CityBuilderSim](CityBuilderSim.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 54 | · THE SCREENS, one class each since 2026-09-18, in the order the rail |
| 74 | · WHERE THE SCREENS WENT. Each line is a run of this file's banner |
| 133 | THE RECEIPT INDICATOR |
| 164 | THE THEME |
| 286 | THE CLOCK |
| 435 | · THE MIDDLE COLUMN SCROLLS NOW, and it should have from the start. |
| 481 | · WHERE THE PAGE IS SCROLLED TO IS A FACT, NOT SOMETHING RECAPTURED. |
| 533 | · THE RAIL, AND WHY IT IS PART OF THE PANEL. |
| 565 | · THE STAGE IS A STACK NOW. |
| 595 | · AND THE TIME CONTROLS DO NOT FLOAT, WHICH IS A CORRECTION. |
| 630 | · THE WHEEL WORKS WHERE THE POINTER ALREADY IS. |
| 676 | · FULL SCREEN, AND THE TWO THINGS JAVAFX DOES ABOUT IT THAT WE DO NOT |
| 847 | · AND THE SCROLL STAYS WHERE YOU PUT IT. |
| 897 | · HOLD THE PAGE'S HEIGHT WHILE IT IS REBUILT, so the position is |
| 979 | A WHEEL NOTCH IS WORTH THE SAME EVERY TIME |
| 1411 | THE TWO STRIPS |
| 1981 | · WHAT "RESUME" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO RESUME |
| 2058 | THE SAVE SYSTEM |
| 2317 | SETTINGS. |
| 2367 | · THE WORLD THE NEXT CITY IS FOUNDED INTO |
| 2475 | THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. |
| 2497 | ECONOMY IS GONE, AND IT SPLIT IN TWO. |
| 2514 | THE SCROLLER |
| 2573 | · WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and |
| 2584 | SIMULATE MULTIPLE MONTHS |
| 2695 | · · headlines |
| 2711 | · · deltas |
| 2752 | · · land |
| 2759 | · · buildings |
| 2775 | · · demolitions |
| 2799 | · · health |
| 2843 | · · households |
| 3003 | CONSTRUCTION PANEL |
| 3218 | THE RAIL |
| 3408 | THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF |
| 3576 | THE INBOX |
| 3617 | · · the envelope |
| 3647 | · · the one line an urgent notice gets |
| 3672 | · · the list |
| 3837 | TIME, AND WHAT THE MONTH IS WORTH |
| 4062 | TWELVE PIPS, AND ONE OF THEM MOVES. |
| 4201 | · ...AND WHAT THE BALANCE ACTUALLY DID |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 192 | `UserInterface.STAGE` | `"#111a24"` | The middle of the window: the blackish blue everything else sits on. |
| 314 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 324 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed slider sticks to. |
| 325 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 346 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 1008 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1406 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1422 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads in the quiet grey. |
| 1425 | `UserInterface.STRIP_INFLATION_ALARM` | `.10` | Inflation past this, or deflation past its negative, reads red: prices are running away, or collapsing. |
| 1428 | `UserInterface.STRIP_RATE_QUIET` | `.05` | The currency within this of its parity (ForeignAccounts.deviationFromParity) reads grey, and so does one stronger than parity by any amount. |
| 1431 | `UserInterface.STRIP_RATE_ALARM` | `.25` | Weaker than parity by more than this reads red: a currency well below what its basket is worth abroad is the thing the player should notice. |
| 1754 | `UserInterface.STRIP_QUIET` | `"#78909c"` | The strip's quiet colour: the cash trend's muted grey. |
| 1757 | `UserInterface.STRIP_FIGURE` | `"-fx-font-family: 'Courier New'; -fx-font-size: 14px;" + " -fx-font-weight: b...` | A small figure on the strip: Courier, so the digits hold their columns, at the population's weight. |
| 1761 | `UserInterface.STRIP_CAPTION` | `"-fx-font-family: 'Courier New'; -fx-font-size: 11px;"` | ...and the caption under it, at the size of the anchors' own captions. |
| 2070 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3223 | `UserInterface.RAIL_WIDTH` | `46` | Wide enough for a glyph and its highlight, narrow enough to be an edge. |
| 3226 | `UserInterface.STRIP_HEIGHT` | `72` | The strip under the stage that holds the dome and the time controls. |
| 3598 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `Game game` | package-private since the split: the screens read it as ui.game |
| 61 | `final BuildScreen buildScreen` |  |
| 62 | `final LandScreen landScreen` |  |
| 63 | `final PeopleScreen peopleScreen` |  |
| 64 | `final ServicesScreen servicesScreen` |  |
| 65 | `final SectorScreen sectorScreen` |  |
| 66 | `final GovernmentScreen governmentScreen` |  |
| 67 | `final FinancesScreen financesScreen` |  |
| 68 | `final BankScreen bankScreen` |  |
| 69 | `final TradeScreen tradeScreen` |  |
| 70 | `final PolicyScreen policyScreen` |  |
| 71 | `final HistoryScreen historyScreen` |  |
| 72 | `final SummaryScreen summaryScreen` | the left panel's content, not a tab |
| 118 | `private Stage stage` |  |
| 119 | `VBox rootMenu` |  |
| 122 | `private VBox tabRail` | The navigation rail down the panel's right edge; see start(). |
| 124 | `private StackPane stagePane` | The middle of the window, with the three overlays on top of it. |
| 125 | `private VBox inboxCorner` |  |
| 126 | `private HBox timeControls` |  |
| 127 | `private StackPane incomeDome` |  |
| 129 | `javafx.scene.control.ScrollPane menuScroller` | The middle column's scroller. |
| 130 | `private VBox constructionPanel` |  |
| 131 | `VBox cityPanel` |  |
| 149 | `int receiptSeen` |  |
| 150 | `boolean receiptOpen` |  |
| 162 | `FadeTransition receiptPulse` | The pulse, held so it can be stopped. |
| 282 | `private HBox dateBar` | Always-visible strips on the two BorderPane edges nothing else uses. |
| 283 | `private HBox debtBar` |  |
| 284 | `private Scene scene` |  |
| 327 | `private int speedIndex` | 1x |
| 328 | `private boolean clockRunning` |  |
| 329 | `private double monthProgress` |  |
| 330 | `private long lastFrame` |  |
| 347 | `private double sinceRedraw` |  |
| 348 | `private boolean redrawPending` |  |
| 349 | `private javafx.animation.AnimationTimer clock` |  |
| 352 | `private Label dayLabel` | The date line, kept so a day can be repainted without a whole redraw. |
| 355 | `private String pausedBecause` | Why the clock stopped itself, shown until it is started again. |
| 358 | `private String pausedOnKey` | The notice that last stopped it, so one condition interrupts once. |
| 971 | `private double pageScrollAt` | Where the player has scrolled the page to. |
| 974 | `private boolean settlingScroll` | True while a rebuild is in flight, so its clamps are not mistaken for a hand. |
| 977 | `private boolean correcting` | Guards the re-entry when the listener corrects a clamp of its own. |
| 1011 | `String currentScreen` | Which show*Menu drew what is on screen; see clearMenu. |
| 1014 | `private Runnable redrawScreen` | How to draw it again after a month passes; see clearMenu. |
| 1025 | `private Runnable resumeTo` | The screen Esc was pressed on, so Resume can go back to it. |
| 1053 | `final java.util.Map<String, Double> innerScrollAt` | Where a scroller inside the CURRENT screen was left, by name. |
| 1063 | `private final java.util.Map<String, Double> panelScrollAt` | The same, for the two side panels - and this one is NEVER emptied. |
| 1409 | `private long lastWheelNanos` | When the last wheel event moved the page; see scrollPageBy. |
| 2298 | `GamePrefs prefs` | How the player likes the window. |
| 3503 | `private boolean railJump` | Set for exactly one clearMenu, by goHome(). |
| 3601 | `private boolean inboxOpen` | Whether the list is dropped down. |
| 3604 | `private String inboxExpanded` | Which notice's body is unfolded, by key. |
| 4060 | `private int dialAt` | The month the dial is currently showing, so it only pops when it moves. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 4267 | **type** `public class UserInterface extends Application` | The window: the stage and its theme, the clock and the speed ladder, the two strips, the rail down the left and the inbox, the left panel and the construction panel, the save and settings dialogs, the time-skip dialog... |

### THE SCREENS, one class each since 2026-09-18, in the order the rail (lines 54-73)

### WHERE THE SCREENS WENT. Each line is a run of this file's banner (lines 74-132)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 1 | `public UserInterface()` | JavaFX needs the no-arg one; a harness needs one bound to a city. |
| 116 | 1 | `public UserInterface(Game game)` |  |

### THE RECEIPT INDICATOR (lines 133-163)

### THE THEME (lines 164-285)

| line | len | member | says |
|---:|---:|---|---|
| 194 | 86 | `private void applyTheme(Scene target)` |  |

### THE CLOCK (lines 286-978)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 472 | `public void start(Stage primaryStage)` |  |
| 839 | 130 | `void clearMenu(String screen, Runnable again)` | Clears the menu area and refreshes the construction panel. |

### A WHEEL NOTCH IS WORTH THE SAME EVERY TIME (lines 979-1410)

| line | len | member | says |
|---:|---:|---|---|
| 1022 | 1 | `void redraw()` | Draw the screen that is showing, again, in place - the same call the clock makes after a month. |
| 1036 | 10 | `private static boolean isGameMenu(String screen)` | The screens that are ABOUT the game rather than in it. |
| 1066 | 3 | `private javafx.scene.control.ScrollPane keptScroller(String key, javafx.scene.Node content)` | A scroller inside a screen, which remembers where it was while you stay. |
| 1083 | 3 | `private javafx.scene.control.ScrollPane keptScrollerFromBottom(String key, javafx.scene.Node content)` | The same, remembering the distance from the BOTTOM rather than the fraction - for a page whose top half changes height under the player. |
| 1088 | 3 | `javafx.scene.control.ScrollPane keptPanelScroller(String key, javafx.scene.Node content)` | A scroller in one of the side panels, which always remembers. |
| 1099 | 28 | `private javafx.scene.control.ScrollPane remembering(java.util.Map<String, Double> where, String key, javafx.scene.Node content,...` | A ScrollPane that files its own position under a name and comes back to it. |
| 1129 | 6 | `private static double scrollSpan(javafx.scene.control.ScrollPane scroller)` | How far a scroller's content can travel, in pixels, as laid out right now. |
| 1165 | 3 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, double to)` | Put it back, and put it back BEFORE anything is painted. |
| 1174 | 113 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, java.util.function.DoubleSupplier target)` | The same, with the position asked for afresh at each of the three attempts - so a caller keeping pixels rather than a fraction can turn them into a fraction of the height the page actually has by then. |
| 1313 | 35 | `private double pageSpan(javafx.scene.Node page)` | How far the page can travel: what the content WANTS to be, less the viewport. |
| 1358 | 14 | `private void wheelToPage(javafx.scene.input.ScrollEvent wheel)` | A wheel turn nothing else wanted, spent on the page. |
| 1393 | 11 | `private void scrollPageBy(double deltaY, double span)` | Move the page by one wheel event's worth, with a floor under the FIRST event of a gesture and the rest taken as they come. |

### THE TWO STRIPS (lines 1411-2057)

| line | len | member | says |
|---:|---:|---|---|
| 1446 | 306 | `private void refreshDateBar()` | Date, month number, cash and population across the top - and, since 2026-09-21, prices and the exchange rate between the last two, and since 0.7.4 the three rates beside them. |
| 1767 | 8 | `private VBox stripPanel(Label...lines)` | One of the strip's inset panels, in the anchors' shape at a smaller size: figure over caption, or since 0.7.4 three rate lines stacked. |
| 1777 | 5 | `private static Label stripRateLine(String word, String figure, String colour)` | One line of the price-of-money panel: a word, then the figure, at the caption's size, so the three figures hold one column. |
| 1784 | 5 | `private static String inflationColour(double inflation, double target)` | Grey near the player's target, amber off it, red once prices run or collapse. |
| 1791 | 6 | `private static String rateColour(ForeignAccounts fx)` | Grey near parity or stronger, amber weaker, red well below it. |
| 1800 | 6 | `private Label flowChip(String text, String colour)` | One small coloured figure on the population strip. |
| 1822 | 94 | `private void refreshDebtBar()` | The next five city debts to come due, and what the city owes altogether. |
| 1918 | 25 | `private VBox maturityChip(Debt debt, int currentMonth)` | One maturity on the bottom strip: amount, type, date, how far off. |
| 1944 | 113 | `private void showMainMenu()` |  |

### THE SAVE SYSTEM (lines 2058-2316)

| line | len | member | says |
|---:|---:|---|---|
| 2074 | 29 | `private String slotSummary(int slot)` | One line describing what is in a slot, or that it is empty. |
| 2104 | 7 | `private String slotTitle(int slot)` |  |
| 2113 | 25 | `private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty)` | A slot row: the label on the button, the city underneath it. |
| 2139 | 22 | `private void showSavingMenu()` |  |
| 2168 | 39 | `private void showSaveSlotConfirm(int slot)` | Confirms one slot, and takes the optional name. |
| 2208 | 20 | `private void showLoadMenu()` |  |
| 2229 | 22 | `private void loadSlot(int slot)` |  |
| 2259 | 3 | `private void openCity()` | Where a city opens. |
| 2263 | 33 | `private void showSaveResult(GameFiles.Result result)` |  |
| 2307 | 9 | `private void toggleFullScreen()` | In and out of full screen, and remembered. |

### SETTINGS. (lines 2317-2474)

| line | len | member | says |
|---:|---:|---|---|
| 2325 | 82 | `private void showSettingsMenu()` |  |
| 2417 | 5 | `private static double settledLevelAt(double mean)` | Where the world's price level settles at a given mean. |
| 2424 | 16 | `private Button worldChip(double mean)` | One choice of world, shown as what it is and what it settles at. |
| 2445 | 29 | `private VBox toggleRow(String label, boolean on, String what, Runnable flip)` | A setting: what it is, what it does, and a switch that says which way it is set without having to read the word next to it. |

### THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. (lines 2475-2496)

### ECONOMY IS GONE, AND IT SPLIT IN TWO. (lines 2497-2513)

### THE SCROLLER (lines 2514-2572)

| line | len | member | says |
|---:|---:|---|---|
| 2524 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column)` | A left-aligned column inside a scroll pane, which these screens all want. |
| 2535 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome)` | this scroller - a title alone is about 150; a title with a vitals bar and two strips pinned over it is a good deal more, and getting it wrong is a scrollbar that appears when there is nothing to scroll. |
| 2544 | 28 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome, boolean fromBottom)` | The same, with the position kept from the bottom of the page instead of as a fraction (fromBottom) - for a page that grows and shrinks ABOVE the controls the player is using; see keptScrollerFromBottom. |

### WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and (lines 2573-2583)

| line | len | member | says |
|---:|---:|---|---|
| 2579 | 1 | `public List<String> whatItDoes(BuildingsTemplate t)` |  |
| 2580 | 1 | `public List<String> whatCareItGives(BuildingsTemplate t)` |  |
| 2581 | 1 | `public String jobLabel(JobType job)` |  |

### SIMULATE MULTIPLE MONTHS (lines 2584-3002)

| line | len | member | says |
|---:|---:|---|---|
| 2592 | 53 | `private void showSimulateMonthsMenu()` |  |
| 2657 | 212 | `private void showSimulateResultMenu(int requested, int completed)` | What happened while the player was not watching. |
| 2871 | 10 | `private void addSkipLine(VBox section, String label, double start, double end, double change, boolean isMoney)` | "Population  192 -> 664  (+472)", coloured by direction. |
| 2890 | 22 | `private void addChangeLine(VBox section, String label, double change, boolean isMoney, boolean higherIsBetter)` | A signed change, coloured by whether it is good news. |
| 2914 | 3 | `void showSectorReport(String title, VBox column, Runnable back)` | Shared scaffolding for the sector report screens. |
| 2924 | 77 | `void showSectorReport(String title, VBox column, Runnable back, Button extra)` | somewhere else - e.g. the industrial report linking to its financial statements. |

### CONSTRUCTION PANEL (lines 3003-3217)

| line | len | member | says |
|---:|---:|---|---|
| 3016 | 87 | `private void refreshConstructionPanel()` |  |
| 3114 | 47 | `private void addDemolitionLog()` | What the city has lost lately, under what it is building. |
| 3174 | 42 | `private void addBuildLog()` | What the city has GAINED lately, above what it has lost. |

### THE RAIL (lines 3218-3407)

| line | len | member | says |
|---:|---:|---|---|
| 3238 | 1 | **type** `private record Tab(String key, String svg, String name, Runnable go)` | One destination. |
| 3270 | 54 | `private Tab[] tabs()` | The rail, in the order a city is actually run. |
| 3335 | 49 | `private String tabFor(String screen)` | Which tab owns the screen that is showing. |
| 3385 | 22 | `private void refreshTabRail()` |  |

### THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF (lines 3408-3575)

| line | len | member | says |
|---:|---:|---|---|
| 3451 | 5 | `private void goHome(Tab tab)` | Press a tab: forget where you were inside it, and land at the top. |
| 3476 | 18 | `private void resetSection(String key)` | A section's own idea of where you were, forgotten. |
| 3520 | 55 | `private StackPane railButton(String svg, String name, boolean active, Runnable go)` | One icon on the rail. |

### THE INBOX (lines 3576-3836)

| line | len | member | says |
|---:|---:|---|---|
| 3606 | 113 | `private void refreshInbox()` |  |
| 3733 | 58 | `private VBox noticeRow(Notice notice)` | One notice: its title, and its body when it is unfolded. |
| 3792 | 10 | `private String dealLabel(String key)` |  |
| 3812 | 24 | `private void deal(Notice notice)` | Take the player to the control that answers it. |

### TIME, AND WHAT THE MONTH IS WORTH (lines 3837-4061)

| line | len | member | says |
|---:|---:|---|---|
| 3855 | 59 | `private void startClock()` | Starts the frame loop. |
| 3922 | 4 | `private void paintDay()` | The day, repainted in place. |
| 3940 | 11 | `private boolean stopIfSomethingHappened()` | Stops the clock when the city has something to say, if the player wants that. |
| 3953 | 7 | `private void setClockRunning(boolean run)` | Play, or pause. |
| 3961 | 45 | `private void refreshTimeControls()` |  |
| 4019 | 33 | `private HBox speedSlider()` | The speed, as a slider that sticks to the ladder. |
| 4054 | 4 | `private static String speedLabel(int index)` | "0.25x", "1x", "10x" - no trailing zeros on the round ones. |

### TWELVE PIPS, AND ONE OF THEM MOVES. (lines 4062-4316)

| line | len | member | says |
|---:|---:|---|---|
| 4087 | 36 | `private VBox yearDial()` |  |
| 4125 | 11 | `private static void popPip(Region pip)` | A quarter second of "that landed", on the pip the month just filled. |
| 4138 | 26 | `private Button roundButton(String glyph, double size, String fill, String tip)` | A circle with a glyph in it. |
| 4182 | 134 | `private void refreshIncomeDome()` | The half circle at the foot of the stage. |

