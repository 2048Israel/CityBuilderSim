# InputRecorder.java - 205 lines · 14 methods · 1 constants · interface

`ham/citybuildersim/ui/InputRecorder.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> The window's input kept for the replay's ghost (TR1, 2026-10-31): while "Record this city" is on, the mouse and the keys
> of the sitting - and the markers beside them: the window's size, the screen open, the expanded map's view, the clock and
> the month - go to an {@link InputLog.Writer} in the recording's folder, each with its time in milliseconds since the
> sitting's log was opened. The log's format, its writer and its reader are InputLog's; this is the screen's half: what to
> listen to and when.
> 
> <p>Active exactly while the city on screen is recording and no replay is on. The events come from filters on the scene
> (a filter runs before any control, so a control that consumes an event does not hide it from the log); the markers are
> polled once a frame from a timer of its own, cheap reads that are logged only when they change. Everything here runs on
> the FX thread; the writer's own thread is the only one that touches the file, and it is handed events and nothing else.
> 
> WHY IT EXISTS (TR1): the ghost cursor of the replay's second half (TR2) shows where the player was and re-enacts what he
> pressed, and it can only play back what was kept from the start - an input log started after the fact has nothing in it.
> Kept in the recording's folder beside the months, per sitting, and closed (flushed) before Quit's System.exit and when the
> window closes, so a sitting's end is on disk.

**Uses:** [InputLog](InputLog.md) (20), [Game](Game.md) (4), [UserInterface](UserInterface.md) (2)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 127 | · the markers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `InputRecorder.MARKER_EPSILON` | `1e-6` | The smallest change of a width, a height or a map's centre that is worth a marker: below it the polled value is the same view. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final UserInterface ui` |  |
| 35 | `private InputLog.Writer writer` |  |
| 37 | `private Game openFor` | The game and the recording the open writer is of: another city, or another id, closes it and opens the next. |
| 38 | `private String openId` |  |
| 40 | `private long openedNanos` | When the log was opened (System.nanoTime()): every event's ms is the time since. |
| 42 | `private long lastMoveMs` | The last move or drag kept, so the filters do not build the events the writer would drop (InputLog.MOVE_EVERY_MS). |
| 45 | `private double markedW` | The markers as last logged: a new value is logged, the same is not. |
| 46 | `private String markedScreen` |  |
| 47 | `private double[] markedView` |  |
| 48 | `private int markedSpeed` |  |
| 49 | `private int markedRunning` |  |
| 50 | `private int markedMonth` |  |
| 52 | `private final AnimationTimer poll` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 177 | **type** `final class InputRecorder` | The window's input kept for the replay's ghost (TR1, 2026-10-31): while "Record this city" is on, the mouse and the keys of the sitting - and the markers beside them: the window's size, the screen open, the expanded m... |
| 56 | 3 | `InputRecorder(UserInterface ui)` |  |
| 61 | 19 | `void install(Scene scene)` | Listens to the scene's mouse and keys, and starts the markers' timer. |
| 82 | 7 | `void close()` | Flushes and closes the sitting's log, if one is open: before Quit's System.exit, and when the window closes. |
| 91 | 1 | `boolean isOpen()` | Whether the log is being written now. |
| 93 | 3 | `private long ms()` |  |
| 97 | 8 | `private void move(MouseEvent e, boolean dragging)` |  |
| 106 | 6 | `private void key(KeyEvent e, boolean down)` |  |
| 114 | 8 | `private static int button(MouseButton b)` | The log's button numbers: 1 primary, 2 middle, 3 secondary, 0 none. |
| 123 | 3 | `private static int mods(boolean shift, boolean ctrl, boolean alt, boolean meta)` |  |

### the markers (lines 127-205)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 4 | `private boolean active()` | Whether the city on screen is being recorded and no replay is on. |
| 136 | 40 | `private void tick()` | One frame: the log opened, kept or closed for the city on screen, and the markers that changed since the last frame. |
| 178 | 14 | `private void open(Game g)` | Opens a log for the city's recording: the markers forgotten, so the new log starts with the window, the screen, the view, the clock and the month. |
| 193 | 3 | `private static boolean differs(double a, double b)` |  |
| 198 | 7 | `private static boolean viewMoved(double[] now, double[] was)` | Whether the map's view {globe, a, b, scale, roll} is another than the last one logged. |

