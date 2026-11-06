# ReplayMode.java - 991 lines · 47 methods · 6 constants · interface

`ham/citybuildersim/ui/ReplayMode.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> Replay mode (TR1, 2026-10-31): a recorded city (Recording) shown on a throwaway copy of it (ReplayCity) in the game's own
> window, with a time bar across the bottom to move through the months - play either way, step, drag, at 1 to 16 months a
> second - and a cinematic mode that hides everything round the picture for the screen recorder. This is the screen's half:
> the list of recordings, putting the real game aside and giving it back, the replay's own menu, the keys, the bar, the
> window's size for the recorder. ReplayCity does the loading, UserInterface the little of itself this reaches (its
> replay... methods), and every figure on screen is the copy's. The camera's moves are ReplayCamera's (marks A and B, the
> glide, the fly, the drift; its arithmetic CameraPath) and the faint graphs behind the city ReplayGraphLayer's: this takes their
> keys, puts their buttons in the bar's second row, and is the month a glide moves (showMonth()).
> 
> <p>Nothing is written. The copy lives in a scratch folder of its own; the player's save slots, the autosave and the recording
> are never touched; the clock never runs (UserInterface treats a replay as paused, and the time skip does nothing); Save,
> Load, New city and Continue are not in the replay's menu. "Nothing sticks": any click may change the copy, and the next
> whole load of a month replaces it.
> 
> <p>Moving in time is {@link ReplayCity#stepQuick} - only the map - and, when that cannot be (the land changed between the
> months), a whole load. When the month on screen has stood still for {@link #REST_MS}, and it was not loaded whole, it is
> loaded whole ({@link ReplayCity#loadWhole}): the same reset a load does, the open screen drawn again, so every screen shows
> exactly that month, its charts up to it.
> 
> WHY IT EXISTS (TR1): Jerus films the game's trailer from a recorded city, as often as he likes, and clicks around it in
> navigation mode. A live take cannot be repeated (a peg breaks by chance, a collapse comes when it comes); a recording can.
> Everything here runs on the FX thread: the copy loads on the calling thread and the screens read it as it loads.

**Uses:** [Palette](Palette.md) (38), [ReplayCamera](ReplayCamera.md) (8), [Recording](Recording.md) (8), [UserInterface](UserInterface.md) (5), [CameraPath](CameraPath.md) (5), [Game](Game.md) (4), [ReplayCity](ReplayCity.md) (4), [GameVersion](GameVersion.md) (3), [CityCalendar](CityCalendar.md) (3), [ReplayGraphLayer](ReplayGraphLayer.md) (2), [Branding](Branding.md) (1)

**Used by (4):** [ReplayCamera](ReplayCamera.md), [ReplayGraphLayer](ReplayGraphLayer.md), [ReplayMoveProbe](ReplayMoveProbe.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 168 | · the menu's entries |
| 203 | · the list |
| 271 | · entering and leaving |
| 375 | · the replay's menu |
| 431 | · the keys |
| 542 | · the clock |
| 557 | · moving in time |
| 639 | · the frame |
| 675 | · the bar |
| 928 | · cinematic mode, the window |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `ReplayMode.LIST` | `"showReplayList"` | The list of recordings, as a screen's name (clearMenu()): a screen of the menu's own, like the Load list. |
| 68 | `ReplayMode.REST_MS` | `300` | How long the month on screen must have stood still before it is loaded whole, in milliseconds: long enough that a drag, a held key or a play never pays for a load it will not show, short enough that a bar that has sto... |
| 71 | `ReplayMode.SPEEDS` | `{ 1, 2, 4, 8, 16 }` | The bar's speeds, in recorded months a second. |
| 74 | `ReplayMode.START_SPEED` | `2` | The speed the bar starts at: an index into {@link #SPEEDS}, 4 months a second. |
| 77 | `ReplayMode.NOTE_MS` | `4_000` | How long a note stays on the bar, in milliseconds. |
| 80 | `ReplayMode.FILM_W` | `1920, FILM_H = 1080` | The scene the screen recorder is given, in pixels: the bar's "1920 x 1080" button sets the window to exactly it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 82 | `private final UserInterface ui` |  |
| 83 | `private boolean on` |  |
| 86 | `private final ReplayCamera camera` | The camera's moves (the marks, the glide, the fly, the smooth hand's drift) and the faint graphs behind the city: this mode's keys and bar drive them. |
| 87 | `private final ReplayGraphLayer graphs` |  |
| 88 | `private boolean graphsLoaded` |  |
| 91 | `private Game realGame` | The real game, put aside while the replay runs, and what the window held of it. |
| 92 | `private boolean realCityOpen` |  |
| 93 | `private String realTitle` |  |
| 94 | `private Runnable realResume` |  |
| 97 | `private ReplayCity city` | The replay. |
| 98 | `private int[] months` |  |
| 99 | `private int at` |  |
| 100 | `private boolean playing` |  |
| 101 | `private int dir` |  |
| 102 | `private int speed` |  |
| 103 | `private double acc` |  |
| 104 | `private long lastMoveNs` |  |
| 105 | `private long beganNs` |  |
| 106 | `private int restFailed` |  |
| 107 | `private String note` |  |
| 108 | `private long noteUntilNs` |  |
| 109 | `private long lastFrame` |  |
| 112 | `private VBox bar` | The bar. |
| 113 | `private Slider slider` |  |
| 114 | `private boolean syncing` |  |
| 115 | `private Button bStepBack, bPlayBack, bPlay, bPlayFwd, bStepFwd` |  |
| 117 | `private Button bMarkA, bMarkB, bGraphs` | The bar's camera group (TR1, camera tools). |
| 118 | `private TextField glideField` |  |
| 119 | `private Label driftLabel` |  |
| 120 | `private CameraPath.View shownA, shownB` |  |
| 121 | `private boolean shownGraphs` |  |
| 122 | `private String shownDrift` |  |
| 123 | `private SVGPath playGlyph` |  |
| 124 | `private final List<Label> speedChips` |  |
| 125 | `private Label readout, noteLabel` |  |
| 126 | `private String shownReadout` |  |
| 127 | `private int shownSpeed` |  |
| 128 | `private boolean shownPlaying` |  |
| 129 | `private double paddedBy` |  |
| 132 | `private AnimationTimer timer` | The replay's frame, made when the first replay opens: an AnimationTimer needs the toolkit, which a harness's UserInterface does not have. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 62 | 930 | **type** `final class ReplayMode` | Replay mode (TR1, 2026-10-31): a recorded city (Recording) shown on a throwaway copy of it (ReplayCity) in the game's own window, with a time bar across the bottom to move through the months - play either way, step, d... |
| 134 | 5 | `ReplayMode(UserInterface ui)` |  |
| 140 | 1 | `boolean isOn()` |  |
| 143 | 1 | `Game realGame()` | The real game while a replay is on - the one Quit and the window's close autosave (UP6) - or null when none is. |
| 146 | 1 | `ReplayCamera camera()` | The replay's camera: MapView asks for it each frame (UserInterface.replayCamera()). |
| 149 | 1 | `int shownMonth()` | The recorded month on screen. |
| 155 | 7 | `void showMonth(int month)` | The camera asks for a month (a glide of views with different months moves time with it): the recorded month nearest it, by quick steps; the bar's play stops. |
| 164 | 3 | `String title()` | The window's title in a replay: "Replay - Medellin - Civic Ledger 0.9.0". |

### the menu's entries (lines 168-202)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 24 | `List<Node> menuEntries()` | The main menu's two entries (TR1): "Record this city" - only while a city is open - which says under it what it keeps, and "Replay a recording", always. |
| 199 | 3 | `private static String megabytes(long bytes)` |  |

### the list (lines 203-270)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 32 | `void showList(String failure)` | The list of recordings, drawn as the Load list is; {@code failure} is shown at its head (a recording that would not open). |
| 240 | 30 | `private VBox row(Recording r)` | One recording: the city's name on the button, and under it its months, how many are kept, its size and when it was started. |

### entering and leaving (lines 271-374)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 54 | `private void enter(Recording rec)` | Opens a recording at its first month. |
| 339 | 22 | `void leave()` | Leaves the replay: cinematic mode off, the bar gone, the real game back on screen (the per-city reset done for it, whether a city was open as it was, the screen Continue goes back to and the window's title as they wer... |
| 363 | 11 | `private static String wholeLoad(ReplayCity rc, int month)` | A month loaded whole (ReplayCity.loadWhole), its time in the log; a load that threw is a failure with the reason, as one that returned it is. |

### the replay's menu (lines 375-430)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 49 | `boolean showMenu()` | In a replay the main menu is this one: Back to the replay, Leave the replay, Quit - no Save, Load, New city or Continue. |

### the keys (lines 431-541)

| line | len | member | says |
|---:|---:|---|---|
| 450 | 55 | `boolean key(KeyEvent e, boolean typing)` | The replay's keys, taken at the top of the window's key filter (UserInterface); true when one was taken, and the event is consumed there, so none reaches the clock's Space or the speed ladder's arrows. |
| 507 | 7 | `private void drift(double across, double down, double zoom)` | A step of drift, with the map over the window; with none, the bar says so. |
| 516 | 4 | `private void glide()` | The glide, from the key or the bar's button: the seconds the field says first. |
| 522 | 7 | `private void toggleGraphs()` | G or the bar's button: the faint GDP and population lines behind the city on or off. |
| 531 | 10 | `private void commitGlideSeconds()` | The bar's field of seconds read into the camera, and written back as it took it. |

### the clock (lines 542-556)

| line | len | member | says |
|---:|---:|---|---|
| 553 | 3 | `double clockMonths()` | The clock the map's boats, trains and trucks sail on (UserInterface.clockMonths()): the copy's month and the real seconds since the replay began, over the seconds a month takes - growing without wrapping, so they keep... |

### moving in time (lines 557-638)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 4 | `private void togglePlay()` |  |
| 564 | 5 | `private void play(int direction)` |  |
| 570 | 4 | `private void turn()` |  |
| 575 | 4 | `private void step(int by)` |  |
| 580 | 4 | `private void jump(int index)` |  |
| 585 | 3 | `private void setSpeed(int index)` |  |
| 594 | 21 | `private void go(int index)` | Shows the month at {@code index} of the recording's months (a gap in the recording is stepped over: the index is into the months that are there): only the map, by ReplayCity.stepQuick; when the land changed between th... |
| 617 | 10 | `private void rest()` | The month on screen has stood still: it is loaded whole, and every screen shows exactly that month. |
| 629 | 4 | `private void afterWhole()` | A month loaded whole: the per-city reset a load does (the expanded map and its view kept), the open screen drawn again. |
| 634 | 4 | `void say(String words)` |  |

### the frame (lines 639-674)

| line | len | member | says |
|---:|---:|---|---|
| 641 | 18 | `private void frame(long now)` |  |
| 661 | 13 | `private void advance(double dt)` | Play: the months due since the last frame, in the direction chosen; the end of the recording either way ends the play. |

### the bar (lines 675-927)

| line | len | member | says |
|---:|---:|---|---|
| 677 | 71 | `private void buildBar()` |  |
| 750 | 6 | `private Button barButton(String text, String tip, Runnable act)` | A bar button: it stops a glide or fly in flight where it is, and then does what it says. |
| 757 | 8 | `private Button camButton(String text, String tip, Runnable act)` |  |
| 771 | 37 | `private HBox cameraRow()` | The bar's camera group (TR1): the map button, A and B (lit when marked, their tooltips say the mark), Glide with its field of seconds, Fly out and Fly in, Graphs (lit when on), Stop - and, when a drift is set, its rea... |
| 809 | 6 | `private static SVGPath glyph(String content)` |  |
| 817 | 4 | `private void showBar()` | The bar laid over the bottom of the window, and the page above it given room so the bar covers no part of it. |
| 822 | 7 | `private void hideBar()` |  |
| 835 | 17 | `private void keepBarAbove()` | The bar's place in the window's stack, kept every frame: above the window and the expanded map's pane (which is added to the stack when it expands, after the bar), below the menu's layer and a dialog. |
| 854 | 43 | `private void refreshBar()` | The bar's figures and lit buttons, set only when they change; the window's room for the bar. |
| 899 | 24 | `private void refreshCamera()` | The camera group's lit buttons, their tooltips and the drift's reading, set only when they change. |
| 924 | 3 | `private static void litButton(Button b, boolean lit)` |  |

### cinematic mode, the window (lines 928-991)

| line | len | member | says |
|---:|---:|---|---|
| 934 | 6 | `private void setCinematic(boolean want)` | Cinematic mode on or off: the window's chrome (UserInterface.replayCinematic()) and the time bar. |
| 947 | 10 | `private void filmSize()` | The window set so its scene is exactly 1920 x 1080 for the screen recorder: out of full screen and not maximised, the stage sized to the picture plus the window's decoration (the stage's size less the scene's), then l... |
| 958 | 33 | `private void fitFilm(boolean wasFull, int pass)` |  |

