# FollowBench.java - 749 lines · 28 methods · 13 constants · interface

`ham/citybuildersim/ui/FollowBench.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

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

**Uses:** [InputLog](InputLog.md) (48), [UserInterface](UserInterface.md) (17), [Reenactor](Reenactor.md) (8), [FollowRules](FollowRules.md) (7), [ReplayMode](ReplayMode.md) (6), [ReplayCheck](ReplayCheck.md) (6), [Recording](Recording.md) (5), [Game](Game.md) (4), [GameFiles](GameFiles.md) (2), [LongPlaytest](LongPlaytest.md) (1), [TempDirs](TempDirs.md) (1), [InputTimeline](InputTimeline.md) (1)

**Used by (1):** [ReplayCheck](ReplayCheck.md)

## Sections

| line | section |
|---:|---|
| 367 | · a sitting followed in the window |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `FollowBench.WIDTH` | `1600` | The window's width the bench lays the scene out at. |
| 64 | `FollowBench.HEIGHT` | `900` | The window's height the bench lays the scene out at. |
| 370 | `FollowBench.RAIL_WORDS` | `{ "People", "Policy", "Services", "Bank", "Trade", "Finances", "Sectors" }` | The rail's words the sitting clicks, in the order tried: the first few the window has, whose clicks open different screens. |
| 373 | `FollowBench.LOGGED_MONTHS` | `4` | How many of the recording's months the sitting logs, one after the other from its fourth. |
| 376 | `FollowBench.MONTH_MS` | `2_400` | How long a month lasts in the sitting's own time, ms. |
| 379 | `FollowBench.MENU_MONTH` | `1` | The month of the sitting (counting from 0) in which the player also clicked the rail's Menu. |
| 382 | `FollowBench.REJOIN_MONTH` | `2` | The month of the sitting (counting from 0) in which Follow is stopped and joined again. |
| 385 | `FollowBench.REJOIN_STOP_FRAMES` | `30` | The frames of the rejoin's first stretch: 990 ms into the month, after the click (560 ms) and its SCREEN marker (760 ms). |
| 388 | `FollowBench.REJOIN_END_FRAMES` | `60` | The frames from the month's start to where the rejoin and the run without a stop are compared: 1,980 ms, after the month's last move (1,800 ms). |
| 391 | `FollowBench.FRAME_MS` | `33` | One Follow frame, in ms of the player's time (about 30 a second); the window check steps by it. |
| 394 | `FollowBench.MAX_FRAMES` | `3_000` | The most frames one stretch of stepping may take: a Follow that never gets going ends the check rather than hangs it. |
| 397 | `FollowBench.SITTING_AT` | `1_790_000_000_000L` | The epoch of the sitting's start in its file's name: any, but one no real sitting has. |
| 400 | `FollowBench.GHOST_PX` | `2` | How near, in pixels, the ghost's tip has to be to the logged pointer. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private static int failures` | The findings that failed: what a bench prints as the exit code. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 694 | **type** `public final class FollowBench` | The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an undecorated stage that is invisible and off every screen, laid out at 1600 x 900), and logged ev... |
| 58 | 1 | `private FollowBench()` |  |
| 73 | 22 | `public static void main(String[] args) throws Exception` | The bench on a small city of its own; {@code args[0]}, if given, is a recording's folder (the copy of one is followed in the window too: {@link #followCheck}). |
| 100 | 17 | `public static String startJavaFx()` | Why JavaFX cannot start here, in a few words, or null when it is up. |
| 119 | 5 | `public static<T> T fx(Callable<T> c) throws Exception` | Runs on the FX thread and waits for the answer; an exception in it comes back here. |
| 126 | 15 | `public static List<String[]> check(Game game)` | The findings, on the FX thread, for the city {@code game}: the window is built on it and hidden again at the end. |
| 143 | 8 | `private static Stage invisibleStage()` | A stage as the game's, undecorated, that nobody sees: invisible and off every screen. |
| 153 | 9 | `private static void startWindow(UserInterface ui, Stage stage)` | The interface started on the stage as the game starts it, the city opened and the scene laid out at the bench's size. |
| 164 | 6 | `private static void layout(UserInterface ui)` | The scene's CSS and layout at the bench's size, as a pulse would do them. |
| 171 | 3 | `private static void add(List<String[]> out, String label, boolean ok)` |  |
| 175 | 141 | `private static void run(UserInterface ui, List<String[]> out)` |  |
| 317 | 3 | `private static int code(KeyCode k)` |  |
| 321 | 3 | `private static InputLog.Event key(long ms, boolean down, KeyCode k, String text)` |  |
| 326 | 7 | `private static void sized(javafx.scene.layout.Region n, double w, double h, Pos at, Insets margin)` | A probe node of a fixed size, set in the window's stack at a corner or an edge. |
| 335 | 4 | `private static double[] centre(Node n)` | The scene point at the middle of a node. |
| 341 | 5 | `private static Node railCell(UserInterface ui, String word)` | The rail's button with this word under its icon. |
| 348 | 8 | `private static Node railCellOrNull(UserInterface ui, String word)` | The rail's button with this word under its icon, or null when the rail has none. |
| 358 | 8 | `private static Node clockButton(UserInterface ui)` | The header's play button, the first button in its clock block. |

### a sitting followed in the window (lines 367-749)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 1 | **type** `private record Tab(String word, double[] at, String screen)` | A rail tab the sitting clicks: its word, its middle in the scene, and the screen a click on it opens. |
| 406 | 2 | **type** `private record Plan(int month, long start, Tab tab, long pressMs, long releaseMs, long markerMs, long lastM...` | What one month of the sitting logged, in the sitting's own ms: the click on a tab, its SCREEN marker, the month's last move and the Menu click (-1: none). |
| 410 | 1 | **type** `private record Frame(double t, String screen, String logged, int month, int gameMonth, boolean following, b...` | One frame of Follow in the window: the player's time, what the copy shows, what the log has open and what the ghost does. |
| 419 | 16 | `public static List<String[]> followCheck(Game game, Path recording)` | A sitting followed in the real window, on the FX thread, for the city {@code game}: a copy of the recording's folder (its own input logs left out) gets one synthetic sitting written with the log writer, in which the p... |
| 436 | 218 | `private static void followSitting(Game game, UserInterface ui, Path recording, List<String[]> out) throws Exception` |  |
| 656 | 15 | `private static void copyWithoutLogs(Path from, Path to) throws IOException` | The recording's folder copied to {@code to}, its input logs left out. |
| 673 | 20 | `private static List<Tab> learnTabs(UserInterface ui, int want)` | The first rail tabs the window has, in {@link #RAIL_WORDS}' order, that a click opens a screen of its own from, found by clicking each through the Reenactor. |
| 698 | 11 | `private static double stepTo(UserInterface ui, ReplayMode mode, double from, double to, List<Frame> out)` | Follow stepped by frames from the player's time {@code from} while it is on and the next frame ends by {@code to}: the window stepped by the frame, then laid out as a pulse would, each frame kept. |
| 711 | 4 | `private static Frame firstAtOrAfter(List<Frame> frames, double t)` | The first frame at or after the time, or null. |
| 717 | 7 | `private static Frame lastBefore(List<Frame> frames, double t)` | The last frame before the time, or null. |
| 726 | 5 | `private static List<String> collapsed(List<String> words)` | The list with each run of the same word as one. |
| 733 | 4 | `private static boolean within(Node n, Node ancestor)` | Whether the node is the ancestor or a node inside it. |
| 738 | 3 | `private static boolean near(double[] at, double x, double y)` |  |
| 743 | 6 | `private static void fireMouse(UserInterface ui, Node target, javafx.event.EventType<MouseEvent> type, double x, double y)` | A real mouse event of the viewer's, not the Reenactor's (it is not counted as firing), sent to the node through the scene. |

