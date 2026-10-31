# UiProbe.java - 729 lines · 23 methods · 6 constants · interface

`ham/citybuildersim/ui/UiProbe.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> What the window costs the JavaFX thread as a city ages, measured: it grows
> the playtest's city to the months it is given, opens the real window on it,
> and times every rail screen, the real clock at 50x and the save. A main()
> run by hand on the PC; it is not in AllChecks, because it opens a window for
> a minute or so and the cloud has no display.
> 
> WHY IT EXISTS (UP1, 2026-10-22; docs/queue/UP1.md)
> 
> Jerus: the game gets sluggish after long play. The plan that looked into it
> wrote three scratch sources that were never in the tree - UiCostProbe,
> UiGrowProbe and JfrCallers (docs/queue/UP1/probe/) - and found the autosave
> on the FX thread the largest cost, 31% of it at month 4,000 at 50x, growing
> with every month played. UP1 took the write off the clock; UP2 and UP3 go on
> from what this prints, so the probe is kept where a later batch finds it and
> measures against it, instead of being rewritten from a note each time.
> 
> HOW IT IS RUN, from the clone's root on the PC (Git Bash):
> 
>     java -XX:FlightRecorderOptions:stackdepth=512 -Dprobe.out=DIR
>         -cp "target/classes;$(cat target/classpath.txt)"
>         ham.citybuildersim.ui.UiProbe 1000,2000,3000,4000 3
> 
> The arguments are the months to grow the city to, comma separated (it stops
> at each, measures, and grows on), and the seconds the clock runs on each
> screen (3 if left out). The classpath is the build's (target/classpath.txt,
> which batch-check.sh writes from the pom, holds gson and the JavaFX base,
> graphics and controls jars with their -win natives; on another platform the
> pom's own jars are the ones to name); stackdepth=512 is what lets a sample's
> whole stack reach down to the callers a share is counted from (a truncated
> stack is counted and printed). Options:
> 
>   -Dprobe.out=DIR     where the JFR recordings (ui-MONTH.jfr) are kept; unset,
>                       a TempDirs folder, which is deleted when the JVM ends
>                       (the repository is never written)
>   -Dprobe.earth=SPOT  grow the city on the real Earth round a spot, as
>                       LongPlaytest --earth does; needs -Dcbs.earth=PATH-TO-earth-2.bin
> 
> and, without a city or a window, the callers of a method in a recording
> (JfrCallers): {@code UiProbe --callers file.jfr ham.citybuildersim.Game.nextMonth [rows]}
> prints the ham.* frames above each FX-thread sample that has the method in
> its stack, most common first.
> 
> WHAT IT PRINTS, for each month it stops at (the shapes of
> docs/queue/UP1/default-m1000-4000.txt, so the two read side by side):
> 
>   - the city (people, buildings, history months, heap);
>   - the save as UP1 made it: what g.autosave() costs the FX thread (the fill
>     and the snapshot - what the clock pays), how long the writer then takes
>     to have the files whole (g.awaitSaves(), on the save-writer thread), the
>     three files' sizes, and what a numbered save (g.saveGame(3), written on
>     the FX thread, as the player's Save still does) costs; then the load;
>   - 24 months as the clock runs them (toggleNextMonth): median and max;
>   - each rail screen's first build, and its redraw as the clock makes it:
>     the draw, then the pulse's own CSS and layout passes (Scene.doCSSPass
>     and doLayoutPass, which touch only what is dirty - UP2: until then this
>     forced applyCss() over every node, which no pulse does, and could not
>     see what a kept page saves; the study's section 9); and the chrome alone
>     (the header, rail, city panel, construction panel and inbox drawn again
>     after a month, then the same passes) - and, where the chrome is kept
>     (UP2), the same chrome with its pools forgotten first, turn and turn
> ... (28 more lines in the source)

**Uses:** [GameFiles](GameFiles.md) (8), [UserInterface](UserInterface.md) (5), [LongPlaytest](LongPlaytest.md) (4), [Game](Game.md) (3), [GamePrefs](GamePrefs.md) (2), [Timing](Timing.md) (2), [TempDirs](TempDirs.md) (1), [SaveWriter](SaveWriter.md) (1)

## Sections

| line | section |
|---:|---|
| 589 | reading the recording |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 139 | `UiProbe.report` | `System.out` | Where the probe's own lines go: the console, as it was when the class loaded. |
| 151 | `UiProbe.FRAME_P95_MS` | `34` | The 95th-percentile frame on People at 50x at month 4,000 the clock must keep under, ms (UP2: 49 in the brief's figures and 42 in this probe's before it; the redraw paced, the months budgeted and the chrome kept). |
| 160 | `UiProbe.FRAME_P95` | `Map.ofEntries(Map.entry("Build", 99.0), Map.entry("Land", 57.0), Map.entry("P...` | Every rail screen's 95th-percentile frame at 50x at month 4,000, ms: its figure before UP2, as this probe measured it on the PC on main f29b9938 (0.8.27) with nothing else running, the run the batch's own is set besid... |
| 169 | `UiProbe.FRAME_P95_FROM` | `4000` | The month from which FRAME_P95 is asserted: the city it was measured on. |
| 596 | `UiProbe.KEYS` | `{ "ham.citybuildersim.ui.UserInterface$", "ham.citybuildersim.Game.nextMonth"...` | The prefixes of "type.method" an FX-thread sample is counted under, if any frame of its stack starts with one: inclusive shares, so a sample counts once for each key it is under. |
| 633 | `UiProbe.FX_THREAD` | `"JavaFX Application Thread"` | The JavaFX thread's name, as the recording names it: the only thread whose samples analyse() reads. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 141 | `private static UserInterface ui` |  |
| 142 | `private static Game g` |  |
| 143 | `private static Stage stage` |  |
| 144 | `private static Path out` |  |
| 171 | `private static Method focusCleanup, doCSSPass, doLayoutPass` |  |
| 520 | `final List<Double> frames` |  |
| 521 | `private final Field clockRunning, pausedBecause, speedIndex` |  |
| 522 | `private long last` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 129 | 601 | **type** `public final class UiProbe` | What the window costs the JavaFX thread as a city ages, measured: it grows the playtest's city to the months it is given, opens the real window on it, and times every rail screen, the real clock at 50x and the save. |
| 131 | 1 | `private UiProbe()` |  |
| 177 | 17 | `static double[] pulse(javafx.scene.Scene sc) throws Exception` | The pulse's CSS and layout, as a frame runs them (Scene.focusCleanup and doCSSPass, then doLayoutPass; by reflection, since the toolkit is on the class path): {CSS ms, layout ms}. |
| 196 | 9 | `private static Method forgetChrome()` | UserInterface.forgetChrome() where the chrome is kept (UP2), or null on a build that rebuilds it. |
| 207 | 7 | `private static void chrome() throws Exception` | The chrome drawn again, as every redraw draws it: the header, the rail, the city panel, the construction panel and the inbox. |
| 216 | 5 | `private static<T> T fx(Callable<T> c) throws Exception` | The window's pump: runs on the FX thread and waits for the answer. |
| 222 | 1 | **type** `private record Screen(String name, Runnable show)` |  |
| 225 | 16 | `private static List<Screen> screens()` | The thirteen rail screens, in rail order. |
| 242 | 5 | `private static Field field(String name) throws Exception` |  |
| 248 | 5 | `private static void call(String name) throws Exception` |  |
| 255 | 3 | `private static void settle() throws Exception` | Waits, on the FX thread as the game does, for the save writer to have every file whole. |
| 259 | 1 | `private static double ms(long nanos)` |  |
| 261 | 6 | `private static double median(List<Double> v)` |  |
| 268 | 6 | `private static double pct(List<Double> v, double p)` |  |
| 275 | 3 | `private static double megabytes(Path p)` |  |
| 279 | 3 | `private static long modified(Path p)` |  |
| 284 | 13 | `private static String startJavaFx()` | Why JavaFX cannot start here, in a few words, or null when it can: the toolkit is started and the answer is whether it came up. |
| 298 | 27 | `public static void main(String[] args) throws Exception` |  |
| 326 | 184 | `private static void run(String[] args) throws Exception` |  |
| 518 | 24 | **type** `private static final class FrameTimer extends javafx.animation.AnimationTimer` | One frame's length at a time, and the clock kept running at 50x: the window's own AnimationTimer would stop for a menu or a dialog, so the probe sets its three fields on every frame. |
| 524 | 5 | `FrameTimer(Field clockRunning, Field pausedBecause, Field speedIndex)` _(in UiProbe.FrameTimer)_ |  |
| 530 | 11 | `public void handle(long now)` _(in UiProbe.FrameTimer)_ |  |
| 551 | 31 | `private static void saveAndLoad(GameFiles files) throws Exception` | The save as UP1 made it, then the load. |
| 583 | 5 | `private static int buildings()` |  |

### reading the recording (lines 589-729)

| line | len | member | says |
|---:|---:|---|---|
| 635 | 49 | `private static void analyse(Path jfr) throws Exception` |  |
| 691 | 38 | `private static void callers(String[] a) throws Exception` | {@code --callers file.jfr method-prefix [rows]}: for the FX-thread samples with a method starting with the prefix in their stack, the ham.* frames from there up (six at most), most common path first. |

