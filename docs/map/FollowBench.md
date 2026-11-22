# FollowBench.java - 918 lines · 33 methods · 14 constants · interface

`ham/citybuildersim/ui/FollowBench.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an
> undecorated stage that is invisible and off every screen, laid out at 1600 x 900), and logged events played into it, with
> what each one did read back. {@link #check} gives the findings as {label, "true"/"false"} pairs for a harness to print, and
> must be called on the FX thread; {@link #main} makes a small city of its own in a scratch folder and prints them. {@link #followCheck}
> (ReplayCheck section 10) enters a copy of a recording in the window and follows a sitting logged over it, frame by frame.
> 
> <p>WHY (TR2): the Reenactor fires events into the scene by hand, and whether the scene answers them as it answers a pointer
> - a tab switches on a click, a handler sees the pointer enter, a wheel moves a pane, a key reaches the game's filter - can only
> be known by doing it to the real window. Run: {@code java -cp "target/classes;$(cat target/classpath.txt)"
> ham.citybuildersim.ui.FollowBench}; the exit code is the number of failures.

**Uses:** [InputLog](InputLog.md) (57), [UserInterface](UserInterface.md) (22), [ReplayCheck](ReplayCheck.md) (10), [Reenactor](Reenactor.md) (8), [FollowRules](FollowRules.md) (7), [ReplayMode](ReplayMode.md) (7), [GamePrefs](GamePrefs.md) (7), [Game](Game.md) (6), [Recording](Recording.md) (5), [GameFiles](GameFiles.md) (3), [LongPlaytest](LongPlaytest.md) (1), [TempDirs](TempDirs.md) (1), [InputTimeline](InputTimeline.md) (1)

**Used by (6):** [AllChecks](AllChecks.md), [KeptCheck](KeptCheck.md), [ReplayCheck](ReplayCheck.md), [ReplayMoveProbe](ReplayMoveProbe.md), [TimingCheck](TimingCheck.md), [UiProbe](UiProbe.md)

## Sections

| line | section |
|---:|---|
| 437 | · a sitting followed in the window |
| 812 | · the copy's clicks and the player's prefs (TR2-G) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `FollowBench.WIDTH` | `1600` | The window's width the bench lays the scene out at. |
| 68 | `FollowBench.HEIGHT` | `900` | The window's height the bench lays the scene out at. |
| 101 | `FollowBench.DISPLAY_PROPERTY` | `"cbs.display"` | GB5: set on the runs of a machine with no screen (the gate box's, tools/gate-box.sh), it names the display they draw on. |
| 440 | `FollowBench.RAIL_WORDS` | `{ "People", "Policy", "Services", "Bank", "Trade", "Finances", "Sectors" }` | The rail's words the sitting clicks, in the order tried: the first few the window has, whose clicks open different screens. |
| 443 | `FollowBench.LOGGED_MONTHS` | `4` | How many of the recording's months the sitting logs, one after the other from its fourth. |
| 446 | `FollowBench.MONTH_MS` | `2_400` | How long a month lasts in the sitting's own time, ms. |
| 449 | `FollowBench.MENU_MONTH` | `1` | The month of the sitting (counting from 0) in which the player also clicked the rail's Menu. |
| 452 | `FollowBench.REJOIN_MONTH` | `2` | The month of the sitting (counting from 0) in which Follow is stopped and joined again. |
| 455 | `FollowBench.REJOIN_STOP_FRAMES` | `30` | The frames of the rejoin's first stretch: 990 ms into the month, after the click (560 ms) and its SCREEN marker (760 ms). |
| 458 | `FollowBench.REJOIN_END_FRAMES` | `60` | The frames from the month's start to where the rejoin and the run without a stop are compared: 1,980 ms, after the month's last move (1,800 ms). |
| 461 | `FollowBench.FRAME_MS` | `33` | One Follow frame, in ms of the player's time (about 30 a second); the window check steps by it. |
| 464 | `FollowBench.MAX_FRAMES` | `3_000` | The most frames one stretch of stepping may take: a Follow that never gets going ends the check rather than hangs it. |
| 467 | `FollowBench.SITTING_AT` | `1_790_000_000_000L` | The epoch of the sitting's start in its file's name: any, but one no real sitting has. |
| 470 | `FollowBench.GHOST_PX` | `2` | How near, in pixels, the ghost's tip has to be to the logged pointer. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `private static int failures` | The findings that failed: what a bench prints as the exit code. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 859 | **type** `public final class FollowBench` | The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an undecorated stage that is invisible and off every screen, laid out at 1600 x 900), and logged ev... |
| 62 | 1 | `private FollowBench()` |  |
| 77 | 22 | `public static void main(String[] args) throws Exception` | The bench on a small city of its own; {@code args[0]}, if given, is a recording's folder (the copy of one is followed in the window too: {@link #followCheck}). |
| 109 | 9 | `public static String startJavaFx()` | Why JavaFX cannot start here, in a few words, or null when it is up; every display harness starts the toolkit here. |
| 119 | 23 | `private static String start(boolean askAwt)` |  |
| 144 | 5 | `public static<T> T fx(Callable<T> c) throws Exception` | Runs on the FX thread and waits for the answer; an exception in it comes back here. |
| 151 | 15 | `public static List<String[]> check(Game game)` | The findings, on the FX thread, for the city {@code game}: the window is built on it and hidden again at the end. |
| 168 | 8 | `private static Stage invisibleStage()` | A stage as the game's, undecorated, that nobody sees: invisible and off every screen. |
| 178 | 9 | `private static void startWindow(UserInterface ui, Stage stage)` | The interface started on the stage as the game starts it, the city opened and the scene laid out at the bench's size. |
| 189 | 6 | `private static void layout(UserInterface ui)` | The scene's CSS and layout at the bench's size, as a pulse would do them. |
| 196 | 3 | `private static void add(List<String[]> out, String label, boolean ok)` |  |
| 200 | 186 | `private static void run(UserInterface ui, List<String[]> out)` |  |
| 387 | 3 | `private static int code(KeyCode k)` |  |
| 391 | 3 | `private static InputLog.Event key(long ms, boolean down, KeyCode k, String text)` |  |
| 396 | 7 | `private static void sized(javafx.scene.layout.Region n, double w, double h, Pos at, Insets margin)` | A probe node of a fixed size, set in the window's stack at a corner or an edge. |
| 405 | 4 | `private static double[] centre(Node n)` | The scene point at the middle of a node. |
| 411 | 5 | `private static Node railCell(UserInterface ui, String word)` | The rail's button with this word under its icon. |
| 418 | 8 | `private static Node railCellOrNull(UserInterface ui, String word)` | The rail's button with this word under its icon, or null when the rail has none. |
| 428 | 8 | `private static Node clockButton(UserInterface ui)` | The header's play button, the first button in its clock block. |

### a sitting followed in the window (lines 437-811)

| line | len | member | says |
|---:|---:|---|---|
| 473 | 1 | **type** `private record Tab(String word, double[] at, String screen)` | A rail tab the sitting clicks: its word, its middle in the scene, and the screen a click on it opens. |
| 476 | 2 | **type** `private record Plan(int month, long start, Tab tab, long pressMs, long releaseMs, long markerMs, long lastM...` | What one month of the sitting logged, in the sitting's own ms: the click on a tab, its SCREEN marker, the month's last move and the Menu click (-1: none). |
| 480 | 1 | **type** `private record Frame(double t, String screen, String logged, int month, int gameMonth, boolean following, b...` | One frame of Follow in the window: the player's time, what the copy shows, what the log has open and what the ghost does. |
| 489 | 16 | `public static List<String[]> followCheck(Game game, Path recording)` | A sitting followed in the real window, on the FX thread, for the city {@code game}: a copy of the recording's folder (its own input logs left out) gets one synthetic sitting written with the log writer, in which the p... |
| 506 | 218 | `private static void followSitting(Game game, UserInterface ui, Path recording, List<String[]> out) throws Exception` |  |
| 726 | 15 | `private static void copyWithoutLogs(Path from, Path to) throws IOException` | The recording's folder copied to {@code to}, its input logs left out. |
| 743 | 20 | `private static List<Tab> learnTabs(UserInterface ui, int want)` | The first rail tabs the window has, in {@link #RAIL_WORDS}' order, that a click opens a screen of its own from, found by clicking each through the Reenactor. |
| 768 | 11 | `private static double stepTo(UserInterface ui, ReplayMode mode, double from, double to, List<Frame> out)` | Follow stepped by frames from the player's time {@code from} while it is on and the next frame ends by {@code to}: the window stepped by the frame, then laid out as a pulse would, each frame kept. |
| 781 | 4 | `private static Frame firstAtOrAfter(List<Frame> frames, double t)` | The first frame at or after the time, or null. |
| 787 | 7 | `private static Frame lastBefore(List<Frame> frames, double t)` | The last frame before the time, or null. |
| 796 | 5 | `private static List<String> collapsed(List<String> words)` | The list with each run of the same word as one. |
| 803 | 4 | `private static boolean within(Node n, Node ancestor)` | Whether the node is the ancestor or a node inside it. |
| 808 | 3 | `private static boolean near(double[] at, double x, double y)` |  |

### the copy's clicks and the player's prefs (TR2-G) (lines 812-918)

| line | len | member | says |
|---:|---:|---|---|
| 820 | 16 | `public static List<String[]> prefsCheck(Game game, Path recording)` | What the copy's clicks do to the player's prefs, in the real window, on the FX thread, for the city {@code game} (TR2-G, ReplayCheck section 11): the drawer's pin and the construction tab clicked outside the copy, in ... |
| 837 | 60 | `private static void prefsSitting(Game game, UserInterface ui, Path recording, List<String[]> out) throws Exception` |  |
| 899 | 3 | `private static Node pin(UserInterface ui)` | The drawer's pin: the first of the bar's two parts, made again at every draw (UserInterface.drawDrawerBar), so asked for each time. |
| 904 | 6 | `private static void clicked(UserInterface ui, Node target)` | A click the way the scene delivers one to the node's handler: the event the node's onMouseClicked is given. |
| 912 | 6 | `private static void fireMouse(UserInterface ui, Node target, javafx.event.EventType<MouseEvent> type, double x, double y)` | A real mouse event of the viewer's, not the Reenactor's (it is not counted as firing), sent to the node through the scene. |

