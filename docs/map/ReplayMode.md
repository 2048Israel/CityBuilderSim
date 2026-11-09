# ReplayMode.java - 1,629 lines · 85 methods · 12 constants · interface

`ham/citybuildersim/ui/ReplayMode.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> Replay mode (TR1, 2026-10-31): a recorded city (Recording) shown on a throwaway copy of it (ReplayCity) in the game's own
> window, with a time bar across the bottom to move through the months - play either way, step, drag, at 1 to 16 months a
> second - and a cinematic mode that hides everything round the picture for the screen recorder. This is the screen's half:
> the list of recordings, putting the real game aside and giving it back, the replay's own menu, the keys, the bar, the
> window's size for the recorder. ReplayCity does the loading, UserInterface the little of itself this reaches (its
> replay... methods), and every figure on screen is the copy's. The camera's moves are ReplayCamera's (marks A and B, the
> glide, the fly, the drift; its arithmetic CameraPath) and the faint graphs behind the city ReplayGraphLayer's: this takes their
> keys, puts their buttons in the bar's second row, and is the month a glide moves (showMonth()).
> 
> <p>Follow (TR2, 2026-11-09) is the player's own sitting played back into the copy: the input log of the month on the bar
> (InputTimeline) runs in real time of play at a chosen speed (FollowPlayer), months turn when the log says by loading that
> month whole, the logged mouse and keys are re-enacted in the window (Reenactor, FollowRules decides what is dropped) and the
> ghost cursor (GhostLayer) shows where the player's pointer was. Any press, wheel turn, key or real mouse move of the viewer's
> ends it, and it can be joined again where it stopped. The ghost also stands, outside Follow, where the player's pointer was
> as the month on the bar began, when the screen on view is the one he had open.
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

**Uses:** [Palette](Palette.md) (53), [InputTimeline](InputTimeline.md) (21), [FollowPlayer](FollowPlayer.md) (11), [Recording](Recording.md) (10), [UserInterface](UserInterface.md) (8), [ReplayCamera](ReplayCamera.md) (8), [InputLog](InputLog.md) (7), [CameraPath](CameraPath.md) (6), [Game](Game.md) (4), [ReplayCity](ReplayCity.md) (4), [GameVersion](GameVersion.md) (3), [CityCalendar](CityCalendar.md) (3), [ReplayGraphLayer](ReplayGraphLayer.md) (2), [GhostLayer](GhostLayer.md) (2), [Branding](Branding.md) (1), [MapView](MapView.md) (1)

**Used by (5):** [FollowBench](FollowBench.md), [ReplayCamera](ReplayCamera.md), [ReplayGraphLayer](ReplayGraphLayer.md), [ReplayMoveProbe](ReplayMoveProbe.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 249 | · the menu's entries |
| 284 | · the list |
| 352 | · entering and leaving |
| 499 | · the replay's menu |
| 555 | · the keys |
| 676 | · the clock |
| 691 | · moving in time |
| 773 | · the frame |
| 814 | · Follow |
| 1129 | · Follow, the viewer's own input |
| 1203 | · the bar |
| 1544 | · cinematic mode, the window |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ReplayMode.LIST` | `"showReplayList"` | The list of recordings, as a screen's name (clearMenu()): a screen of the menu's own, like the Load list. |
| 86 | `ReplayMode.REST_MS` | `300` | How long the month on screen must have stood still before it is loaded whole, in milliseconds: long enough that a drag, a held key or a play never pays for a load it will not show, short enough that a bar that has sto... |
| 89 | `ReplayMode.SPEEDS` | `{ 1, 2, 4, 8, 16 }` | The bar's speeds, in recorded months a second. |
| 92 | `ReplayMode.START_SPEED` | `2` | The speed the bar starts at: an index into {@link #SPEEDS}, 4 months a second. |
| 95 | `ReplayMode.NOTE_MS` | `4_000` | How long a note stays on the bar, in milliseconds. |
| 98 | `ReplayMode.FILM_W` | `1920, FILM_H = 1080` | The scene the screen recorder is given, in pixels: the bar's "1920 x 1080" button sets the window to exactly it. |
| 101 | `ReplayMode.RESYNC_MS` | `300` | How long after a logged SCREEN marker the copy's screen is looked at against the logged one, in milliseconds of the viewer's time: 300, long enough for the re-enacted click that changes it to have done so. |
| 104 | `ReplayMode.VIEWER_MOVE_PX` | `8` | How far the viewer's pointer must move from where it first was after Follow began to end Follow, in pixels: 8, so a nudge of the desk while filming does not end the shot. |
| 107 | `ReplayMode.VIEWER_GRACE_MS` | `1_500` | How long after Follow begins, or after a press on its group, the viewer's pointer may move without ending it, in milliseconds: time to take the hand off the button and the pointer out of the shot. |
| 110 | `ReplayMode.FOLLOW_START_SPEED` | `1` | The speed Follow starts at: an index into FollowPlayer.SPEEDS, 1x. |
| 113 | `ReplayMode.WINDOW_SETTLE_MS` | `400` | How long the window is given to settle after Follow set it and the screen could not hold the size, in milliseconds: 400, the wait filmSize() gives the window's own restore. |
| 116 | `ReplayMode.STAMP` | `DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefa...` | How a sitting's moment is read on the bar: the player's date and time of day. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 118 | `private final UserInterface ui` |  |
| 119 | `private boolean on` |  |
| 122 | `private final ReplayCamera camera` | The camera's moves (the marks, the glide, the fly, the smooth hand's drift) and the faint graphs behind the city: this mode's keys and bar drive them. |
| 123 | `private final ReplayGraphLayer graphs` |  |
| 124 | `private boolean graphsLoaded` |  |
| 127 | `private Game realGame` | The real game, put aside while the replay runs, and what the window held of it. |
| 128 | `private boolean realCityOpen` |  |
| 129 | `private String realTitle` |  |
| 130 | `private Runnable realResume` |  |
| 133 | `private ReplayCity city` | The replay. |
| 134 | `private int[] months` |  |
| 135 | `private int at` |  |
| 136 | `private boolean playing` |  |
| 137 | `private int dir` |  |
| 138 | `private int speed` |  |
| 139 | `private double acc` |  |
| 140 | `private long lastMoveNs` |  |
| 141 | `private long beganNs` |  |
| 142 | `private int restFailed` |  |
| 143 | `private String note` |  |
| 144 | `private long noteUntilNs` |  |
| 145 | `private long lastFrame` |  |
| 148 | `private VBox bar` | The bar. |
| 149 | `private Slider slider` |  |
| 150 | `private boolean syncing` |  |
| 151 | `private Button bStepBack, bPlayBack, bPlay, bPlayFwd, bStepFwd` |  |
| 153 | `private Button bMarkA, bMarkB, bGraphs` | The bar's camera group (TR1, camera tools). |
| 154 | `private TextField glideField` |  |
| 155 | `private Label driftLabel` |  |
| 156 | `private CameraPath.View shownA, shownB` |  |
| 157 | `private boolean shownGraphs` |  |
| 158 | `private String shownDrift` |  |
| 159 | `private SVGPath playGlyph` |  |
| 160 | `private final List<Label> speedChips` |  |
| 161 | `private Label readout, noteLabel` |  |
| 162 | `private String shownReadout` |  |
| 163 | `private int shownSpeed` |  |
| 164 | `private boolean shownPlaying` |  |
| 165 | `private double paddedBy` |  |
| 168 | `private InputTimeline timeline` | Follow and the ghost (TR2). |
| 169 | `private List<InputTimeline.Segment> segments` |  |
| 170 | `private FollowPlayer player` |  |
| 171 | `private GhostLayer ghost` |  |
| 172 | `private boolean following` |  |
| 174 | `private boolean followLive` | Follow has set the window and the state and the player is started: the frame may advance it. |
| 176 | `private int followToken` | Bumped each time Follow starts or ends, so a window that settles late finds its Follow gone. |
| 177 | `private boolean ghostOn` |  |
| 178 | `private boolean skipIdle` |  |
| 179 | `private int followSpeed` |  |
| 181 | `private InputTimeline.Moment stoppedAt` | Where Follow last stopped and the month on the bar then: joined again from there if the bar has not moved. |
| 182 | `private int stoppedMonth` |  |
| 184 | `private String loggedScreen` | The screen the log has open now (the last SCREEN marker), and the viewer's milliseconds left before the copy's is looked at against it (negative: not waiting). |
| 185 | `private double resyncInMs` |  |
| 187 | `private double followX, followY` | The last pointer position the log gave, and whether it gave one. |
| 188 | `private boolean followMouse` |  |
| 190 | `private InputLog.Event pendingMap` | The last map marker of a catch-up, applied once at its end. |
| 192 | `private double[] windowWanted` | The window size the log wants (null: none waiting), whether a fit is under way, and the size the screen refused. |
| 193 | `private boolean fitting` |  |
| 194 | `private double refusedW, refusedH` |  |
| 196 | `private boolean anchorKnown` | Where the viewer's pointer first was after Follow began. |
| 197 | `private double anchorX, anchorY` |  |
| 199 | `private long anchorFreeUntilNs` | Until when (System.nanoTime()) a move of the viewer's only takes the pointer's place afresh, and ends nothing. |
| 201 | `private int ghostMonth` | The month whose start state the ghost stands at outside Follow, and that state. |
| 202 | `private InputTimeline.State ghostState` |  |
| 203 | `private Scene filteredScene` |  |
| 205 | `private HBox followRow` | Follow's group on the bar. |
| 206 | `private Button bFollow, bSkipIdle, bGhost` |  |
| 207 | `private final List<Label> followChips` |  |
| 208 | `private Label followReadout` |  |
| 209 | `private int shownFollowFlags` |  |
| 210 | `private String shownFollowText` |  |
| 213 | `private AnimationTimer timer` | The replay's frame, made when the first replay opens: an AnimationTimer needs the toolkit, which a harness's UserInterface does not have. |
| 817 | `private final FollowPlayer.Target followTarget` | What the player turns the log into: a month loaded whole, an event re-enacted. |
| 1131 | `private final EventHandler<MouseEvent> pressFilter` |  |
| 1132 | `private final EventHandler<ScrollEvent> wheelFilter` |  |
| 1133 | `private final EventHandler<KeyEvent> keyFilter` |  |
| 1134 | `private final EventHandler<MouseEvent> moveFilter` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 1550 | **type** `final class ReplayMode` | Replay mode (TR1, 2026-10-31): a recorded city (Recording) shown on a throwaway copy of it (ReplayCity) in the game's own window, with a time bar across the bottom to move through the months - play either way, step, d... |
| 215 | 5 | `ReplayMode(UserInterface ui)` |  |
| 221 | 1 | `boolean isOn()` |  |
| 224 | 1 | `Game realGame()` | The real game while a replay is on - the one Quit and the window's close autosave (UP6) - or null when none is. |
| 227 | 1 | `ReplayCamera camera()` | The replay's camera: MapView asks for it each frame (UserInterface.replayCamera()). |
| 230 | 1 | `int shownMonth()` | The recorded month on screen. |
| 236 | 7 | `void showMonth(int month)` | The camera asks for a month (a glide of views with different months moves time with it): the recorded month nearest it, by quick steps; the bar's play stops. |
| 245 | 3 | `String title()` | The window's title in a replay: "Replay - Medellin - Civic Ledger 0.9.0". |

### the menu's entries (lines 249-283)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 24 | `List<Node> menuEntries()` | The main menu's two entries (TR1): "Record this city" - only while a city is open - which says under it what it keeps, and "Replay a recording", always. |
| 280 | 3 | `private static String megabytes(long bytes)` |  |

### the list (lines 284-351)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 32 | `void showList(String failure)` | The list of recordings, drawn as the Load list is; {@code failure} is shown at its head (a recording that would not open). |
| 321 | 30 | `private VBox row(Recording r)` | One recording: the city's name on the button, and under it its months, how many are kept, its size and when it was started. |

### entering and leaving (lines 352-498)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 70 | `private void enter(Recording rec)` | Opens a recording at its first month. |
| 436 | 28 | `void leave()` | Leaves the replay: cinematic mode off, the bar gone, the real game back on screen (the per-city reset done for it, whether a city was open as it was, the screen Continue goes back to and the window's title as they wer... |
| 466 | 3 | `void enterFolder(Path folder)` | Opens the recording in a folder at its first month (a harness's way in; the list's rows enter the recording they were drawn from). |
| 471 | 14 | `private static InputTimeline readTimeline(Recording rec, int[] ms)` | The recording's input log read once, as the timeline Follow and the ghost play; its time is the log's. |
| 487 | 11 | `private static String wholeLoad(ReplayCity rc, int month)` | A month loaded whole (ReplayCity.loadWhole), its time in the log; a load that threw is a failure with the reason, as one that returned it is. |

### the replay's menu (lines 499-554)

| line | len | member | says |
|---:|---:|---|---|
| 505 | 49 | `boolean showMenu()` | In a replay the main menu is this one: Back to the replay, Leave the replay, Quit - no Save, Load, New city or Continue. |

### the keys (lines 555-675)

| line | len | member | says |
|---:|---:|---|---|
| 577 | 62 | `boolean key(KeyEvent e, boolean typing)` | The replay's keys, taken at the top of the window's key filter (UserInterface); true when one was taken, and the event is consumed there, so none reaches the clock's Space or the speed ladder's arrows. |
| 641 | 7 | `private void drift(double across, double down, double zoom)` | A step of drift, with the map over the window; with none, the bar says so. |
| 650 | 4 | `private void glide()` | The glide, from the key or the bar's button: the seconds the field says first. |
| 656 | 7 | `private void toggleGraphs()` | G or the bar's button: the faint GDP and population lines behind the city on or off. |
| 665 | 10 | `private void commitGlideSeconds()` | The bar's field of seconds read into the camera, and written back as it took it. |

### the clock (lines 676-690)

| line | len | member | says |
|---:|---:|---|---|
| 687 | 3 | `double clockMonths()` | The clock the map's boats, trains and trucks sail on (UserInterface.clockMonths()): the copy's month and the real seconds since the replay began, over the seconds a month takes - growing without wrapping, so they keep... |

### moving in time (lines 691-772)

| line | len | member | says |
|---:|---:|---|---|
| 693 | 4 | `private void togglePlay()` |  |
| 698 | 5 | `private void play(int direction)` |  |
| 704 | 4 | `private void turn()` |  |
| 709 | 4 | `private void step(int by)` |  |
| 714 | 4 | `private void jump(int index)` |  |
| 719 | 3 | `private void setSpeed(int index)` |  |
| 728 | 21 | `private void go(int index)` | Shows the month at {@code index} of the recording's months (a gap in the recording is stepped over: the index is into the months that are there): only the map, by ReplayCity.stepQuick; when the land changed between th... |
| 751 | 10 | `private void rest()` | The month on screen has stood still: it is loaded whole, and every screen shows exactly that month. |
| 763 | 4 | `private void afterWhole()` | A month loaded whole: the per-city reset a load does (the expanded map and its view kept), the open screen drawn again. |
| 768 | 4 | `void say(String words)` |  |

### the frame (lines 773-813)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 23 | `private void frame(long now)` |  |
| 800 | 13 | `private void advance(double dt)` | Play: the months due since the last frame, in the direction chosen; the end of the recording either way ends the play. |

### Follow (lines 814-1128)

| line | len | member | says |
|---:|---:|---|---|
| 823 | 1 | `boolean following()` | Whether Follow is on. |
| 826 | 1 | `String loggedScreen()` | The screen the log has open now (the last SCREEN marker played), "" before any. |
| 829 | 1 | `boolean ghostShown()` | Whether the ghost cursor is on view, and where its tip is (scene pixels), or null when it is not. |
| 831 | 1 | `double[] ghostAt()` |  |
| 834 | 1 | `InputTimeline timeline()` | The recording's input timeline, read once when the replay was entered; null outside a replay. |
| 837 | 4 | `void setFollow(boolean want)` | Follow on or off, as the L key and the bar's button do it. |
| 842 | 3 | `private void toggleFollow()` |  |
| 847 | 5 | `private void toggleGhost()` | H or the bar's button: the ghost cursor on or off, in Follow and outside it. |
| 853 | 5 | `private void toggleSkipIdle()` |  |
| 859 | 4 | `private void setFollowSpeed(int index)` |  |
| 864 | 3 | `private static String speedWords(double speed)` |  |
| 873 | 47 | `private void startFollow()` | Follow on: from where it last stopped when the bar has not moved since, else from the start of the month on the bar. |
| 922 | 22 | `private void beginFollow(int token, InputTimeline.Moment moment, InputTimeline.State state)` | The window is at the player's size (or as near as the screen allows): his state applied, the player started. |
| 949 | 19 | `private void stopFollow(String why, boolean quiet)` | Follow off, and where it stopped kept for joining again (not when the log ended). |
| 970 | 16 | `private void followLoad(int month)` | The player's loadMonth: the recorded month nearest it loaded whole and the bar on it, the pointer's hover and press forgotten (their nodes are gone). |
| 988 | 26 | `private void followEvent(InputLog.Event e, boolean catchingUp)` | The player's event: the markers keep the logged screen, the window and the map; an input is re-enacted, and the ghost follows it. |
| 1016 | 3 | `private void applyMapMarker(InputLog.Event e)` | A MAP_VIEW marker: the expanded map closed, or opened and its view set. |
| 1021 | 9 | `private void showLoggedMap(boolean open, boolean globe, double a, double b, double scale)` | The expanded map closed, or opened if it is not and set to the view (the globe's roll is not set: the map has no hand for it). |
| 1036 | 11 | `private void resync(String when)` | The copy's screen looked at against the logged one: when the copy is on a game menu and the log is not, the copy goes back to the city; and when the logged screen is on another rail tab than the copy's, the copy goes ... |
| 1049 | 14 | `void followStep(double realMs)` | What a frame does for Follow, for the real milliseconds since the last: the window, the screen check, the player. |
| 1065 | 14 | `private void followWindowStep()` | The log's window size, set once it has stopped changing for a step: one fit at a time, and not again for a size the screen refused. |
| 1080 | 4 | `private boolean sameSize(double w, double h)` |  |
| 1086 | 5 | `private void placeGhost()` | The ghost where the player's pointer is, or hidden: Follow's own placing. |
| 1096 | 17 | `private void refreshGhost()` | Outside Follow the ghost stands where the player's pointer was as the month on the bar began, when that moment's screen is the one on view and the pointer is known; hidden when no log reaches the month, on a game menu... |
| 1115 | 4 | `private String stampOf(InputTimeline.Moment m)` | The sitting's date and time of day at a moment. |
| 1121 | 7 | `private String followReading()` | The bar's Follow readout: the player's date and time at the moment and the speed while following; else why Follow cannot start. |

### Follow, the viewer's own input (lines 1129-1202)

| line | len | member | says |
|---:|---:|---|---|
| 1136 | 10 | `private void installFilters()` |  |
| 1147 | 9 | `private void removeFilters()` |  |
| 1158 | 6 | `private boolean inFollowGroup(Object target)` | Whether the event's target is on the bar's Follow group, whose presses and moves are Follow's own. |
| 1166 | 8 | `private void viewerPressed(Object target)` | A press or a wheel turn of the viewer's: Follow ends, then the press does its own thing; not on the Follow group, and not one the Reenactor fired. |
| 1176 | 17 | `private void viewerMoved(MouseEvent e)` | A real mouse move of the viewer's: from where the pointer first was after Follow began, more than VIEWER_MOVE_PX ends Follow. |
| 1195 | 7 | `private void viewerKey(KeyEvent e)` | A key of the viewer's: Follow ends, then the key does its own thing; but not Follow's own keys (L, H, Up and Down, C). |

### the bar (lines 1203-1543)

| line | len | member | says |
|---:|---:|---|---|
| 1205 | 71 | `private void buildBar()` |  |
| 1278 | 6 | `private Button barButton(String text, String tip, Runnable act)` | A bar button: it stops a glide or fly in flight where it is, and then does what it says. |
| 1285 | 8 | `private Button camButton(String text, String tip, Runnable act)` |  |
| 1299 | 37 | `private HBox cameraRow()` | The bar's camera group (TR1): the map button, A and B (lit when marked, their tooltips say the mark), Glide with its field of seconds, Fly out and Fly in, Graphs (lit when on), Stop - and, when a drift is set, its rea... |
| 1341 | 32 | `private HBox followGroup()` | The bar's Follow group (TR2): the Follow toggle, its speed chips, the Skip idle and Ghost toggles and the readout of the player's own date and time. |
| 1374 | 6 | `private static SVGPath glyph(String content)` |  |
| 1382 | 9 | `private void showBar()` | The bar laid over the bottom of the window, and the page above it given room so the bar covers no part of it. |
| 1392 | 11 | `private void hideBar()` |  |
| 1409 | 29 | `private void keepBarAbove()` | The bar's place in the window's stack, kept every frame: above the window and the expanded map's pane (which is added to the stack when it expands, after the bar), below the menu's layer and a dialog. |
| 1440 | 38 | `private void refreshBar()` | The bar's figures and lit buttons, set only when they change; the window's room for the bar. |
| 1480 | 9 | `private void applyRoom()` | The page ends above the bar, not under it; none of it while the bar is hidden (cinematic mode) or while Follow plays: the page is then the player's layout and the bar lies over it. |
| 1491 | 22 | `private void refreshFollow()` | Follow's group: the lit toggles, the speed chip and the readout, set only when they change. |
| 1515 | 24 | `private void refreshCamera()` | The camera group's lit buttons, their tooltips and the drift's reading, set only when they change. |
| 1540 | 3 | `private static void litButton(Button b, boolean lit)` |  |

### cinematic mode, the window (lines 1544-1629)

| line | len | member | says |
|---:|---:|---|---|
| 1550 | 6 | `private void setCinematic(boolean want)` | Cinematic mode on or off: the window's chrome (UserInterface.replayCinematic()) and the time bar. |
| 1558 | 3 | `private void filmSize()` | The 1920 x 1080 button: the window set so its picture is that size, for the screen recorder. |
| 1569 | 15 | `private void fitWindow(double w, double h, Consumer<Boolean> done)` | The window set so its scene is exactly {@code w} x {@code h}: out of full screen and not maximised, the stage sized to the picture plus the window's decoration (the stage's size less the scene's), then looked at again... |
| 1585 | 44 | `private void fitStage(double w, double h, boolean wasFull, int pass, Consumer<Boolean> done)` |  |

