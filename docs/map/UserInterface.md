# UserInterface.java - 4,297 lines · 75 methods · 18 constants · interface

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

**Uses:** [Palette](Palette.md) (28), [Icons](Icons.md) (13), [CityCalendar](CityCalendar.md) (11), [GameFiles](GameFiles.md) (10), [Debt](Debt.md) (5), [DemolitionLog](DemolitionLog.md) (5), [Notice](Notice.md) (5), [Game](Game.md) (4), [ServicesScreen](ServicesScreen.md) (4), [FoundingScreen](FoundingScreen.md) (4), [GamePrefs](GamePrefs.md) (4), [Currency](Currency.md) (4), [BuildScreen](BuildScreen.md) (3), [SectorScreen](SectorScreen.md) (3), [FinancesScreen](FinancesScreen.md) (3), [BankScreen](BankScreen.md) (3), [TradeScreen](TradeScreen.md) (3), [PolicyScreen](PolicyScreen.md) (3), [GameVersion](GameVersion.md) (3), [DebtManager](DebtManager.md) (3), [SaveHeader](SaveHeader.md) (3), [TimeSkipReport](TimeSkipReport.md) (3), [LandScreen](LandScreen.md) (2), [PeopleScreen](PeopleScreen.md) (2), [GovernmentScreen](GovernmentScreen.md) (2), [HistoryScreen](HistoryScreen.md) (2), [SummaryScreen](SummaryScreen.md) (2), [Health](Health.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [GameLog](GameLog.md) (2)... and 12 more

**Used by (15):** [BankScreen](BankScreen.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [CityBuilderSim](CityBuilderSim.md), [FinancesScreen](FinancesScreen.md), [FoundingScreen](FoundingScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 54 | · THE SCREENS, one class each since 2026-09-18, in the order the rail |
| 75 | · WHERE THE SCREENS WENT. Each line is a run of this file's banner |
| 134 | THE RECEIPT INDICATOR |
| 165 | THE THEME |
| 287 | THE CLOCK |
| 436 | · THE MIDDLE COLUMN SCROLLS NOW, and it should have from the start. |
| 482 | · WHERE THE PAGE IS SCROLLED TO IS A FACT, NOT SOMETHING RECAPTURED. |
| 534 | · THE RAIL, AND WHY IT IS PART OF THE PANEL. |
| 566 | · THE STAGE IS A STACK NOW. |
| 596 | · AND THE TIME CONTROLS DO NOT FLOAT, WHICH IS A CORRECTION. |
| 631 | · THE WHEEL WORKS WHERE THE POINTER ALREADY IS. |
| 677 | · FULL SCREEN, AND THE TWO THINGS JAVAFX DOES ABOUT IT THAT WE DO NOT |
| 859 | · AND THE SCROLL STAYS WHERE YOU PUT IT. |
| 909 | · HOLD THE PAGE'S HEIGHT WHILE IT IS REBUILT, so the position is |
| 991 | A WHEEL NOTCH IS WORTH THE SAME EVERY TIME |
| 1423 | THE TWO STRIPS |
| 1991 | · WHAT "RESUME" MEANS DEPENDS ON WHETHER THERE IS ANYTHING TO RESUME |
| 2068 | THE SAVE SYSTEM |
| 2342 | SETTINGS. |
| 2456 | THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. |
| 2478 | ECONOMY IS GONE, AND IT SPLIT IN TWO. |
| 2495 | THE SCROLLER |
| 2554 | · WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and |
| 2565 | SIMULATE MULTIPLE MONTHS |
| 2676 | · · headlines |
| 2692 | · · deltas |
| 2733 | · · land |
| 2740 | · · buildings |
| 2756 | · · demolitions |
| 2780 | · · health |
| 2824 | · · households |
| 2984 | CONSTRUCTION PANEL |
| 3199 | THE RAIL |
| 3389 | THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF |
| 3557 | THE INBOX |
| 3598 | · · the envelope |
| 3628 | · · the one line an urgent notice gets |
| 3653 | · · the list |
| 3818 | TIME, AND WHAT THE MONTH IS WORTH |
| 4043 | TWELVE PIPS, AND ONE OF THEM MOVES. |
| 4182 | · ...AND WHAT THE BALANCE ACTUALLY DID |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 193 | `UserInterface.STAGE` | `"#111a24"` | The middle of the window: the blackish blue everything else sits on. |
| 315 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 325 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed slider sticks to. |
| 326 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 347 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 1020 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1418 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1434 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads in the quiet grey. |
| 1437 | `UserInterface.STRIP_INFLATION_ALARM` | `.10` | Inflation past this, or deflation past its negative, reads red: prices are running away, or collapsing. |
| 1440 | `UserInterface.STRIP_RATE_QUIET` | `.05` | The currency within this of its parity (ForeignAccounts.deviationFromParity) reads grey, and so does one stronger than parity by any amount. |
| 1443 | `UserInterface.STRIP_RATE_ALARM` | `.25` | Weaker than parity by more than this reads red: a currency well below what its basket is worth abroad is the thing the player should notice. |
| 1768 | `UserInterface.STRIP_QUIET` | `"#78909c"` | The strip's quiet colour: the cash trend's muted grey. |
| 1771 | `UserInterface.STRIP_FIGURE` | `"-fx-font-family: 'Courier New'; -fx-font-size: 14px;" + " -fx-font-weight: b...` | A small figure on the strip: Courier, so the digits hold their columns, at the population's weight. |
| 1775 | `UserInterface.STRIP_CAPTION` | `"-fx-font-family: 'Courier New'; -fx-font-size: 11px;"` | ...and the caption under it, at the size of the anchors' own captions. |
| 2080 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3204 | `UserInterface.RAIL_WIDTH` | `46` | Wide enough for a glyph and its highlight, narrow enough to be an edge. |
| 3207 | `UserInterface.STRIP_HEIGHT` | `72` | The strip under the stage that holds the dome and the time controls. |
| 3579 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |

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
| 73 | `final FoundingScreen foundingScreen` | the left panel's content, not a tab |
| 119 | `private Stage stage` |  |
| 120 | `VBox rootMenu` |  |
| 123 | `private VBox tabRail` | The navigation rail down the panel's right edge; see start(). |
| 125 | `private StackPane stagePane` | The middle of the window, with the three overlays on top of it. |
| 126 | `private VBox inboxCorner` |  |
| 127 | `private HBox timeControls` |  |
| 128 | `private StackPane incomeDome` |  |
| 130 | `javafx.scene.control.ScrollPane menuScroller` | The middle column's scroller. |
| 131 | `private VBox constructionPanel` |  |
| 132 | `VBox cityPanel` |  |
| 150 | `int receiptSeen` |  |
| 151 | `boolean receiptOpen` |  |
| 163 | `FadeTransition receiptPulse` | The pulse, held so it can be stopped. |
| 283 | `private HBox dateBar` | Always-visible strips on the two BorderPane edges nothing else uses. |
| 284 | `private HBox debtBar` |  |
| 285 | `private Scene scene` |  |
| 328 | `private int speedIndex` | 1x |
| 329 | `private boolean clockRunning` |  |
| 330 | `private double monthProgress` |  |
| 331 | `private long lastFrame` |  |
| 348 | `private double sinceRedraw` |  |
| 349 | `private boolean redrawPending` |  |
| 350 | `private javafx.animation.AnimationTimer clock` |  |
| 353 | `private Label dayLabel` | The date line, kept so a day can be repainted without a whole redraw. |
| 356 | `private String pausedBecause` | Why the clock stopped itself, shown until it is started again. |
| 359 | `private String pausedOnKey` | The notice that last stopped it, so one condition interrupts once. |
| 983 | `private double pageScrollAt` | Where the player has scrolled the page to. |
| 986 | `private boolean settlingScroll` | True while a rebuild is in flight, so its clamps are not mistaken for a hand. |
| 989 | `private boolean correcting` | Guards the re-entry when the listener corrects a clamp of its own. |
| 1023 | `String currentScreen` | Which show*Menu drew what is on screen; see clearMenu. |
| 1026 | `private Runnable redrawScreen` | How to draw it again after a month passes; see clearMenu. |
| 1037 | `private Runnable resumeTo` | The screen Esc was pressed on, so Resume can go back to it. |
| 1065 | `final java.util.Map<String, Double> innerScrollAt` | Where a scroller inside the CURRENT screen was left, by name. |
| 1075 | `private final java.util.Map<String, Double> panelScrollAt` | The same, for the two side panels - and this one is NEVER emptied. |
| 1421 | `private long lastWheelNanos` | When the last wheel event moved the page; see scrollPageBy. |
| 2323 | `GamePrefs prefs` | How the player likes the window. |
| 3484 | `private boolean railJump` | Set for exactly one clearMenu, by goHome(). |
| 3582 | `private boolean inboxOpen` | Whether the list is dropped down. |
| 3585 | `private String inboxExpanded` | Which notice's body is unfolded, by key. |
| 4041 | `private int dialAt` | The month the dial is currently showing, so it only pops when it moves. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 4248 | **type** `public class UserInterface extends Application` | The window: the stage and its theme, the clock and the speed ladder, the two strips, the rail down the left and the inbox, the left panel and the construction panel, the save and settings dialogs, the time-skip dialog... |

### THE SCREENS, one class each since 2026-09-18, in the order the rail (lines 54-74)

### WHERE THE SCREENS WENT. Each line is a run of this file's banner (lines 75-133)

| line | len | member | says |
|---:|---:|---|---|
| 115 | 1 | `public UserInterface()` | JavaFX needs the no-arg one; a harness needs one bound to a city. |
| 117 | 1 | `public UserInterface(Game game)` |  |

### THE RECEIPT INDICATOR (lines 134-164)

### THE THEME (lines 165-286)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 86 | `private void applyTheme(Scene target)` |  |

### THE CLOCK (lines 287-990)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 483 | `public void start(Stage primaryStage)` |  |
| 851 | 130 | `void clearMenu(String screen, Runnable again)` | Clears the menu area and refreshes the construction panel. |

### A WHEEL NOTCH IS WORTH THE SAME EVERY TIME (lines 991-1422)

| line | len | member | says |
|---:|---:|---|---|
| 1034 | 1 | `void redraw()` | Draw the screen that is showing, again, in place - the same call the clock makes after a month. |
| 1048 | 10 | `private static boolean isGameMenu(String screen)` | The screens that are ABOUT the game rather than in it. |
| 1078 | 3 | `private javafx.scene.control.ScrollPane keptScroller(String key, javafx.scene.Node content)` | A scroller inside a screen, which remembers where it was while you stay. |
| 1095 | 3 | `private javafx.scene.control.ScrollPane keptScrollerFromBottom(String key, javafx.scene.Node content)` | The same, remembering the distance from the BOTTOM rather than the fraction - for a page whose top half changes height under the player. |
| 1100 | 3 | `javafx.scene.control.ScrollPane keptPanelScroller(String key, javafx.scene.Node content)` | A scroller in one of the side panels, which always remembers. |
| 1111 | 28 | `private javafx.scene.control.ScrollPane remembering(java.util.Map<String, Double> where, String key, javafx.scene.Node content,...` | A ScrollPane that files its own position under a name and comes back to it. |
| 1141 | 6 | `private static double scrollSpan(javafx.scene.control.ScrollPane scroller)` | How far a scroller's content can travel, in pixels, as laid out right now. |
| 1177 | 3 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, double to)` | Put it back, and put it back BEFORE anything is painted. |
| 1186 | 113 | `private static void restoreScroll(javafx.scene.control.ScrollPane scroller, java.util.function.DoubleSupplier target)` | The same, with the position asked for afresh at each of the three attempts - so a caller keeping pixels rather than a fraction can turn them into a fraction of the height the page actually has by then. |
| 1325 | 35 | `private double pageSpan(javafx.scene.Node page)` | How far the page can travel: what the content WANTS to be, less the viewport. |
| 1370 | 14 | `private void wheelToPage(javafx.scene.input.ScrollEvent wheel)` | A wheel turn nothing else wanted, spent on the page. |
| 1405 | 11 | `private void scrollPageBy(double deltaY, double span)` | Move the page by one wheel event's worth, with a floor under the FIRST event of a gesture and the rest taken as they come. |

### THE TWO STRIPS (lines 1423-2067)

| line | len | member | says |
|---:|---:|---|---|
| 1458 | 308 | `private void refreshDateBar()` | Date, month number, cash and population across the top - and, since 2026-09-21, prices and the exchange rate between the last two, and since 0.7.4 the three rates beside them. |
| 1781 | 8 | `private VBox stripPanel(Label...lines)` | One of the strip's inset panels, in the anchors' shape at a smaller size: figure over caption, or since 0.7.4 three rate lines stacked. |
| 1791 | 5 | `private static Label stripRateLine(String word, String figure, String colour)` | One line of the price-of-money panel: a word, then the figure, at the caption's size, so the three figures hold one column. |
| 1798 | 5 | `private static String inflationColour(double inflation, double target)` | Grey near the player's target, amber off it, red once prices run or collapse. |
| 1805 | 6 | `private static String rateColour(ForeignAccounts fx)` | Grey near parity or stronger, amber weaker, red well below it. |
| 1814 | 6 | `private Label flowChip(String text, String colour)` | One small coloured figure on the population strip. |
| 1836 | 94 | `private void refreshDebtBar()` | The next five city debts to come due, and what the city owes altogether. |
| 1932 | 25 | `private VBox maturityChip(Debt debt, int currentMonth)` | One maturity on the bottom strip: amount, type, date, how far off. |
| 1958 | 109 | `void showMainMenu()` |  |

### THE SAVE SYSTEM (lines 2068-2341)

| line | len | member | says |
|---:|---:|---|---|
| 2084 | 31 | `private String slotSummary(int slot)` | One line describing what is in a slot, or that it is empty. |
| 2116 | 7 | `private String slotTitle(int slot)` |  |
| 2125 | 25 | `private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty)` | A slot row: the label on the button, the city underneath it. |
| 2151 | 22 | `private void showSavingMenu()` |  |
| 2180 | 39 | `private void showSaveSlotConfirm(int slot)` | Confirms one slot, and takes the optional name. |
| 2220 | 20 | `private void showLoadMenu()` |  |
| 2241 | 22 | `private void loadSlot(int slot)` |  |
| 2271 | 10 | `private void openCity()` | Where a city opens. |
| 2283 | 4 | `void foundCity(Founding choices)` | The founding screen's last step: found the city as chosen, and open it. |
| 2288 | 33 | `private void showSaveResult(GameFiles.Result result)` |  |
| 2332 | 9 | `private void toggleFullScreen()` | In and out of full screen, and remembered. |

### SETTINGS. (lines 2342-2455)

| line | len | member | says |
|---:|---:|---|---|
| 2350 | 65 | `private void showSettingsMenu()` |  |
| 2426 | 29 | `private VBox toggleRow(String label, boolean on, String what, Runnable flip)` | A setting: what it is, what it does, and a switch that says which way it is set without having to read the word next to it. |

### THE MAIN SCREEN IS GONE, AND THAT IS THE POINT. (lines 2456-2477)

### ECONOMY IS GONE, AND IT SPLIT IN TWO. (lines 2478-2494)

### THE SCROLLER (lines 2495-2553)

| line | len | member | says |
|---:|---:|---|---|
| 2505 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column)` | A left-aligned column inside a scroll pane, which these screens all want. |
| 2516 | 3 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome)` | this scroller - a title alone is about 150; a title with a vitals bar and two strips pinned over it is a good deal more, and getting it wrong is a scrollbar that appears when there is nothing to scroll. |
| 2525 | 28 | `javafx.scene.control.ScrollPane scrolled(VBox column, double chrome, boolean fromBottom)` | The same, with the position kept from the bottom of the page instead of as a fraction (fromBottom) - for a page that grows and shrinks ABOVE the controls the player is using; see keptScrollerFromBottom. |

### WHAT THE HARNESS READS. BuildMenuCheck sits in the model package and (lines 2554-2564)

| line | len | member | says |
|---:|---:|---|---|
| 2560 | 1 | `public List<String> whatItDoes(BuildingsTemplate t)` |  |
| 2561 | 1 | `public List<String> whatCareItGives(BuildingsTemplate t)` |  |
| 2562 | 1 | `public String jobLabel(JobType job)` |  |

### SIMULATE MULTIPLE MONTHS (lines 2565-2983)

| line | len | member | says |
|---:|---:|---|---|
| 2573 | 53 | `private void showSimulateMonthsMenu()` |  |
| 2638 | 212 | `private void showSimulateResultMenu(int requested, int completed)` | What happened while the player was not watching. |
| 2852 | 10 | `private void addSkipLine(VBox section, String label, double start, double end, double change, boolean isMoney)` | "Population  192 -> 664  (+472)", coloured by direction. |
| 2871 | 22 | `private void addChangeLine(VBox section, String label, double change, boolean isMoney, boolean higherIsBetter)` | A signed change, coloured by whether it is good news. |
| 2895 | 3 | `void showSectorReport(String title, VBox column, Runnable back)` | Shared scaffolding for the sector report screens. |
| 2905 | 77 | `void showSectorReport(String title, VBox column, Runnable back, Button extra)` | somewhere else - e.g. the industrial report linking to its financial statements. |

### CONSTRUCTION PANEL (lines 2984-3198)

| line | len | member | says |
|---:|---:|---|---|
| 2997 | 87 | `private void refreshConstructionPanel()` |  |
| 3095 | 47 | `private void addDemolitionLog()` | What the city has lost lately, under what it is building. |
| 3155 | 42 | `private void addBuildLog()` | What the city has GAINED lately, above what it has lost. |

### THE RAIL (lines 3199-3388)

| line | len | member | says |
|---:|---:|---|---|
| 3219 | 1 | **type** `private record Tab(String key, String svg, String name, Runnable go)` | One destination. |
| 3251 | 54 | `private Tab[] tabs()` | The rail, in the order a city is actually run. |
| 3316 | 49 | `private String tabFor(String screen)` | Which tab owns the screen that is showing. |
| 3366 | 22 | `private void refreshTabRail()` |  |

### THE RAIL GOES TO THE TOP OF ITS SECTION, NOT TO WHERE YOU LEFT OFF (lines 3389-3556)

| line | len | member | says |
|---:|---:|---|---|
| 3432 | 5 | `private void goHome(Tab tab)` | Press a tab: forget where you were inside it, and land at the top. |
| 3457 | 18 | `private void resetSection(String key)` | A section's own idea of where you were, forgotten. |
| 3501 | 55 | `private StackPane railButton(String svg, String name, boolean active, Runnable go)` | One icon on the rail. |

### THE INBOX (lines 3557-3817)

| line | len | member | says |
|---:|---:|---|---|
| 3587 | 113 | `private void refreshInbox()` |  |
| 3714 | 58 | `private VBox noticeRow(Notice notice)` | One notice: its title, and its body when it is unfolded. |
| 3773 | 10 | `private String dealLabel(String key)` |  |
| 3793 | 24 | `private void deal(Notice notice)` | Take the player to the control that answers it. |

### TIME, AND WHAT THE MONTH IS WORTH (lines 3818-4042)

| line | len | member | says |
|---:|---:|---|---|
| 3836 | 59 | `private void startClock()` | Starts the frame loop. |
| 3903 | 4 | `private void paintDay()` | The day, repainted in place. |
| 3921 | 11 | `private boolean stopIfSomethingHappened()` | Stops the clock when the city has something to say, if the player wants that. |
| 3934 | 7 | `private void setClockRunning(boolean run)` | Play, or pause. |
| 3942 | 45 | `private void refreshTimeControls()` |  |
| 4000 | 33 | `private HBox speedSlider()` | The speed, as a slider that sticks to the ladder. |
| 4035 | 4 | `private static String speedLabel(int index)` | "0.25x", "1x", "10x" - no trailing zeros on the round ones. |

### TWELVE PIPS, AND ONE OF THEM MOVES. (lines 4043-4297)

| line | len | member | says |
|---:|---:|---|---|
| 4068 | 36 | `private VBox yearDial()` |  |
| 4106 | 11 | `private static void popPip(Region pip)` | A quarter second of "that landed", on the pip the month just filled. |
| 4119 | 26 | `private Button roundButton(String glyph, double size, String fill, String tip)` | A circle with a glyph in it. |
| 4163 | 134 | `private void refreshIncomeDome()` | The half circle at the foot of the stage. |

