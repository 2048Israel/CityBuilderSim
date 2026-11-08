# ReplayMoveProbe.java - 876 lines · 32 methods · 12 constants · interface

`ham/citybuildersim/ui/ReplayMoveProbe.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> What a replay's camera moves show unpainted, measured: it opens the real window full screen on a recorded city in replay
> mode, runs the seven moves a trailer makes (the fly out and back, the glide from the street to the whole Earth and back, a
> glide within a month and one across months, the first again with the caches warm) and prints, for each, the tally MapView
> keeps of every frame (MapView.MoveTally): the cells drawn as nothing, from a stand-in or from an older month, the frames
> drawn at half resolution, the worker's asks, the pictures let go and the frame gaps. A main() run by hand on the PC; it
> is not in AllChecks, because it opens a window and needs the Earth's file, which the cloud has neither of.
> 
> WHY IT EXISTS (CM1, 2026-11-02): Jerus films his trailer from a recorded city in replay mode and "half the screen is
> black while you zoom out well". The batch loads a whole move before it runs it; this measures first, with the camera as the
> trailer uses it, so the batch can say which cause makes the black frames (tiles not drawn in time, the worker behind,
> pictures let go for want of room, a stand-in showing, a half-resolution draw) and so that its last check has numbers to
> hold itself to.
> 
> HOW IT IS RUN, from the clone's root on the PC (Git Bash), on the Earth's and the nations' files:
> 
>     java -Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin
>         -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin
>         -cp "target/classes;$(cat target/classpath.txt)"
>         ham.citybuildersim.ui.ReplayMoveProbe RECORDING_FOLDER [grow to month, default 360] [months recorded, default 120]
> 
> RECORDING_FOLDER is a recording as the game keeps it (recording.json and month-*.bin). If it holds one, the probe replays
> it; if not, it founds a city on the Earth at Andorra (42.507, 1.522), plays it to the month it is given, records the months
> after it (a dozen at a time, the writer given time to catch up, so none is left out) and copies the recording there - to be
> kept for the next run, and for a run on another build, whose moves are then the same city's.
> 
> WHAT IT PRINTS: the recording, the window's canvas, and for each move its heading, how long the view took to settle before
> it, how long the move took to load (CM1: the views it walked, the Ready line's words, the heap after it) and to run,
> and MoveTally.report() for the run (the load's frames are not counted: the map is veiled while it loads).
> 
> NOTHING OF JERUS'S IS TOUCHED. The cities are on GameFiles.scratch() folders, checked to lie under java.io.tmpdir and
> deleted when the JVM exits; the replay's copy is on its own scratch folder (ReplayCity); the window's preferences are
> read from the scratch folder, never from %APPDATA%; the only file written outside is the recording folder it is given.
> 
> THE WINDOW IS OFF EVERY SCREEN unless -Dcbs.probe.onscreen=true: the primary screen's size, undecorated, left of the
> leftmost screen, so a run does not cover the PC's screen while someone works on it (CM1, 2026-11-03). It still draws
> every frame; it does take the keyboard's focus when it opens, until another window is clicked.
> 
> Where JavaFX cannot start - no display, a headless JVM, the jars missing - it says so in one line and exits 0.
> 
> check() is the public entry ReplayCheck 5d runs (CM1): the same window off screen on a generated world's recording, a short glide
> loaded and run by a click, one across months run by a key, and Esc while one loads, returned as figures (Checked) for it to assert on.

**Uses:** [CameraPath](CameraPath.md) (12), [MapView](MapView.md) (11), [GameFiles](GameFiles.md) (8), [Recording](Recording.md) (7), [Game](Game.md) (6), [GlobeFrame](GlobeFrame.md) (6), [UserInterface](UserInterface.md) (5), [ReplayMode](ReplayMode.md) (5), [GamePrefs](GamePrefs.md) (4), [LongPlaytest](LongPlaytest.md) (4), [ReplayCamera](ReplayCamera.md) (1)

**Used by (1):** [ReplayCheck](ReplayCheck.md)

## Sections

| line | section |
|---:|---|
| 269 | · the recording |
| 341 | · views and waiting |
| 429 | · the moves |
| 511 | · the check ReplayCheck runs (5d) |
| 811 | · the toolkit and reflection |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `ReplayMoveProbe.report` | `System.out` | Where the probe's own lines go: the console, as it was when the class loaded; the game's narration is silenced once the probe starts (UiProbe's way). |
| 90 | `ReplayMoveProbe.SPOT` | `"42.507,1.522"` | The spot the city is founded at when there is no recording: Andorra, on the Earth. |
| 93 | `ReplayMoveProbe.GROW_TO` | `360, RECORDED = 120` | The months a city is grown to, and the months recorded after it, when the arguments say nothing. |
| 96 | `ReplayMoveProbe.RECORD_CHUNK` | `12` | The months played between two waits for the recording's writer. |
| 99 | `ReplayMoveProbe.SETTLE_MAX_S` | `20, MOVE_MAX_S = 120` | The most a view is waited for to settle, and a move to end, in seconds. |
| 102 | `ReplayMoveProbe.GLIDE_S` | `10` | The seconds a glide takes. |
| 105 | `ReplayMoveProbe.ON_SCREEN` | `Boolean.getBoolean("cbs.probe.onscreen")` | Whether the window is full screen on the PC's screen (-Dcbs.probe.onscreen=true) rather than the same size off every screen. |
| 445 | `ReplayMoveProbe.LOAD_MAX_S` | `900` | The most a move's load is waited for, in seconds, before the probe stops it and says so. |
| 514 | `ReplayMoveProbe.CHECK_LOAD_MAX_S` | `300` | How long a move's load is waited for in check(), in seconds, before it is stopped and said not to have reached Ready. |
| 517 | `ReplayMoveProbe.PLANE_STREET_PX` | `8` | The scale of the "street" on a plane in check(), in pixels a plot: a level-0 view with the big tiles (MapFrame.BIG_TILES_FROM is 6). |
| 520 | `ReplayMoveProbe.NORMAL_FRAMES` | `30` | The frames the normal game's map is asked to draw before the replay is entered, and the most the wait for them takes, in seconds. |
| 521 | `ReplayMoveProbe.NORMAL_MAX_S` | `30` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 107 | `private static UserInterface ui` |  |
| 108 | `private static Stage stage` |  |
| 109 | `private static ReplayMode mode` |  |
| 110 | `private static ReplayCamera cam` |  |
| 111 | `private static MapView map` |  |
| 112 | `private static Canvas canvas` |  |
| 114 | `private static double cityLat, cityLon` | The city's centre: latitude and longitude on the Earth; on a generated world (plane) the plot x and y. |
| 117 | `private static boolean plane` | Whether the window's map is a plane (a generated world) rather than the globe: views are then in plots and px a plot, not degrees and km a pixel. |
| 120 | `private static boolean silent` | Whether the probe's own waiting lines are said: not while the harness runs check() (it prints the figures itself). |
| 123 | `private static int firstMonth, lastMonth` | The recorded months' ends: what goTo() is given. |
| 126 | `private static Field panKeys, canvasField` | The private members the probe reaches, found by bind() once the toolkit is up (a build that moved one fails there, naming it). |
| 127 | `private static Method replayMode, enter, applyView, requestDraw` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 795 | **type** `public final class ReplayMoveProbe` | What a replay's camera moves show unpainted, measured: it opens the real window full screen on a recorded city in replay mode, runs the seven moves a trailer makes (the fly out and back, the glide from the street to t... |
| 84 | 1 | `private ReplayMoveProbe()` |  |
| 130 | 11 | `private static void offScreen(Stage s)` | The window moved off every screen at the primary screen's size, then made visible: drawn as full screen would be, seen by nobody. |
| 142 | 8 | `private static void bind()` |  |
| 151 | 21 | `public static void main(String[] args)` |  |
| 173 | 95 | `private static void run(String[] args) throws Exception` |  |

### the recording (lines 269-340)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 8 | `private static Game found(GameFiles files)` | The city founded on the Earth at SPOT, on a FX-thread call; a friendlier line when the Earth's file is not there. |
| 282 | 9 | `private static GameFiles scratch(String label)` | A scratch save folder, checked to lie under java.io.tmpdir (UiProbe's rule); null, after saying so, when it does not. |
| 293 | 33 | `private static Game record(Path folder, int growTo, int recorded) throws Exception` | Founds a city, grows it to growTo, records `recorded` months more and copies the recording to `folder`; the grown city, or null if the scratch folder was refused. |
| 327 | 13 | `private static void copyTree(Path from, Path to) throws IOException` |  |

### views and waiting (lines 341-428)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 3 | `private static CameraPath.View at(double kmPx)` | A view on the globe at the city's centre, `kmPx` km a pixel (its month is the camera's to put on: mark() takes the month on screen). |
| 349 | 5 | `private static CameraPath.View wholeNow() throws Exception` | The widest view as it stands now (its scale follows the canvas), the whole Earth facing the city. |
| 356 | 9 | `private static CameraPath.View waitForWhole() throws Exception` | Waits for the expanded map's globe to exist, and returns the whole-Earth view (its centre is the city's). |
| 367 | 12 | `private static void waitForCanvas() throws Exception` | Waits for the canvas to keep one size over three looks (the full screen takes a moment), and prints it. |
| 381 | 6 | `private static void goTo(int month) throws Exception` | Puts the recorded month nearest `month` on screen, as the camera does. |
| 389 | 7 | `private static void take(CameraPath.View v) throws Exception` | The expanded map's view taken whole and drawn (what a glide's or a fly's frame does). |
| 397 | 6 | `private static void mark(boolean a) throws Exception` |  |
| 405 | 3 | `private static void tell(String format, Object...arguments)` | One of the probe's waiting lines, unless check() runs it. |
| 410 | 18 | `private static void settle(String what) throws Exception` | Waits, at most SETTLE_MAX_S, until MapView.viewSettled() has held over two looks 100 ms apart, and prints how long it took. |

### the moves (lines 429-510)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 11 | `private static void zoomOutGlide(String heading) throws Exception` | M3 and M7: the street at the city marked A, the whole Earth B, the street taken again and settled, then the glide. |
| 452 | 58 | `private static void move(String heading, Runnable start, boolean glide) throws Exception` | Runs a move: started, its load waited for (at most LOAD_MAX_S) and printed, then run as a click runs it (the tally reset in the same FX task), polled until the camera is idle (at most MOVE_MAX_S), and printed under it... |

### the check ReplayCheck runs (5d) (lines 511-810)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 1 | **type** `public record Normal(long frames, boolean camera, boolean moveHeld, int cachesPinned)` | The normal game, before the replay is entered: the map's frames drawn, and whether a camera, a loaded move or a pinned cache exists. |
| 533 | 4 | **type** `public record Moved(String name, boolean ready, double loadSeconds, int planViews, int passed, boolean held...` | One glide, loaded and run: how long the load took and how many plan views it walked, whether it reached Ready and how many views it passed; while it waited, whether the map held the move and how many of the six caches... |
| 539 | 1 | **type** `public record Cancelled(boolean wasLoading, boolean taken, String phaseAfter, boolean heldAfter, int pinned...` | Escape pressed while a glide loads: whether it was loading, whether the key was taken, and where the camera and the map were left. |
| 545 | 1 | **type** `public record Checked(String cannot, boolean globe, Normal normal, Moved zoomOut, Moved months, Cancelled e...` | What check() measured. |
| 554 | 30 | `public static Checked check(Path recordingFolder, GameFiles windowFiles, double glideSeconds) throws Exception` | CM1's check, run by ReplayCheck 5d (2026-11-03): the window off screen on a game of its own (windowFiles), first as the normal game draws its map, then in the replay of the recording at recordingFolder with the expand... |
| 585 | 93 | `private static Checked measureAll(Path folder, GameFiles windowFiles, double glideSeconds) throws Exception` |  |
| 680 | 9 | `private static void markMonths(double first, double last) throws Exception` | M6's marks: the last month at `last` is B, the first month at `first` is A, and the view is settled on A. |
| 691 | 9 | `private static MapView waitForMap() throws Exception` | Waits for the expanded map to exist on the window (the normal game's) and returns its view. |
| 705 | 82 | `private static Moved measureGlide(String name, boolean byKey) throws Exception` | The glide set up from the marks: loaded (at most CHECK_LOAD_MAX_S), held while it waits, run by a click on the canvas's middle or by a plain key as the window sends it to ReplayMode.key(), and polled until the camera ... |
| 789 | 21 | `private static Cancelled cancelWhileLoading() throws Exception` | A glide set up from the marks and, once the map has begun to load it, Escape sent to ReplayMode.key(); then where it all stands. |

### the toolkit and reflection (lines 811-876)

| line | len | member | says |
|---:|---:|---|---|
| 814 | 17 | `private static String startJavaFx()` | Why JavaFX cannot start here, in a few words, or null when it can: the toolkit is started and the answer is whether it came up (UiProbe's). |
| 833 | 5 | `private static<T> T fx(Callable<T> c) throws Exception` | Runs on the FX thread and waits for the answer; an exception in it comes back here. |
| 839 | 9 | `private static Field field(Class<?> c, String name)` |  |
| 849 | 9 | `private static Method method(Class<?> c, String name, Class<?>...parameters)` |  |
| 859 | 7 | `private static Object get(Field f, Object on)` |  |
| 867 | 9 | `private static Object call(Method m, Object on, Object...arguments)` |  |

