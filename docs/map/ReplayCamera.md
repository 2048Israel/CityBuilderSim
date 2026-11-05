# ReplayCamera.java - 390 lines · 46 methods · 5 constants · interface

`ham/citybuildersim/ui/ReplayCamera.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back,
> the load of a move before it runs (CM1), and the smooth hand with its drift - the state of every camera move, frame by frame. The
> arithmetic is {@link CameraPath}; ui/MapView is the toolkit's half and asks this for one {@link Shot} a frame
> (MapView.replayCamera()), a view to take whole or a move to ease on, and applies it; ReplayMode owns this, takes the keys and the
> bar's camera group, and is the month on screen (a glide of views with different months moves time with it, by quick steps).
> 
> WHY IT EXISTS (TR1): the trailer's camera moves - the glide while the city grows, the pull-out to the whole planet - cannot be
> made by a hand on the wheel on a screen recording. They are curves the game follows a frame at a time, and the first frame of a
> glide must not find the map half painted: before a glide starts the camera walks its path once so every tile on it is drawn.
> 
> <p>WHY THE LOAD (CM1, 2026-11-03): TR1's warm-up walked 25 views of a glide's path, a second a view and eight in all, the start
> month's map alone, and a recording of a glide or a fly out still showed the screen half black where the move arrived before the
> worker had. Jerus (2026-11-02): "allow the game to load up so that when it zooms or glides there is no loading, basically it could
> just say loading 10 seconds, then let you click to run it ... not for the normal game, only while in replaying mode."
> 
> <p>A move (a glide, a fly out or a fly in) is set up here and then LOADS: it goes through {@link #PLAN_HZ} views a second of its
> own clock, {@link #planViews()} in all, which MapView paints one after another with no limit on the time, in every month the move
> crosses, keeping what it painted until the move has run (MapView.loadStep()). The run shows, frame by frame, the plan's view nearest
> its own clock, never one between two: a view between two loaded ones can need a tile or a node neither had, and a frame clock lands
> between them nearly every frame (CM1's gate, 2026-11-04: on a busy PC a glide asked the worker 7 things and drew one frame
> unpainted; ReplayCheck 5d counted 54 of 56 frames off the plan before this). The bar says "Loading the move... N s", then
> "Ready - click to run"; a click on the map or any key runs it, so a screen recording can be started first, and Esc cancels at
> either point. Only a replay has this camera, so the normal game loads nothing ahead.
> 
> <p>Everything runs on the FX thread, from MapView's frame. Nothing here touches a node.

**Uses:** [CameraPath](CameraPath.md) (35), [ReplayMode](ReplayMode.md) (2), [UserInterface](UserInterface.md) (2)

**Used by (4):** [MapView](MapView.md), [ReplayMode](ReplayMode.md), [ReplayMoveProbe](ReplayMoveProbe.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 164 | · the keys and the buttons |
| 348 | · the frame |

## Enum constants

| line | constant | says |
|---:|---|---|
| 48 | `ReplayCamera.Phase.IDLE` |  |
| 48 | `ReplayCamera.Phase.LOAD` |  |
| 48 | `ReplayCamera.Phase.READY` |  |
| 48 | `ReplayCamera.Phase.GLIDE` |  |
| 48 | `ReplayCamera.Phase.FLY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 35 | `ReplayCamera.PLAN_HZ` | `240` | The views a second of the move's own clock the load walks: 240 - finer than the 165 Hz this PC's screen draws at, so a frame of the run, which shows the plan's view nearest its clock (advance()), is at most half a ste... |
| 38 | `ReplayCamera.MOST_DT_S` | `0.1` | The longest step of a glide's or fly's own clock, in seconds: 0.1 - a frame held up (a quick step of the map) is a pause in the move, not a jump ahead. |
| 41 | `ReplayCamera.GLIDE_S` | `10, GLIDE_S_LEAST = 1, GLIDE_S_MOST = 600` | The seconds a glide takes when the bar's field says nothing: 10; the field's least and most. |
| 45 | `ReplayCamera.Shot.IDLE` | `new Shot(null, null, false)` |  |
| 50 | `ReplayCamera.LOADING` | `"Loading the move..."` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `private final ReplayMode mode` |  |
| 53 | `private final UserInterface ui` |  |
| 54 | `private final CameraPath.Smooth smooth` |  |
| 56 | `private CameraPath.View markA, markB` |  |
| 57 | `private double glideSeconds` |  |
| 59 | `private Phase phase` |  |
| 60 | `private String status` |  |
| 62 | `private boolean glideKind, flyingOut` | A move (loading, ready or running): a glide or a fly (out or in), from where, to where, how far along (t), over how long. |
| 63 | `private CameraPath.View from, to` |  |
| 64 | `private double t, seconds` |  |
| 66 | `private long loadStartNanos` | The load: when it began, and the seconds-left figure the bar showed last; and which move it is (one more each time a move is set up). |
| 67 | `private int shownLeft, serial` |  |
| 69 | `private CameraPath.View flyBack` | Where the last fly out began, which the fly in goes back to. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 359 | **type** `final class ReplayCamera` | The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back, the load of a move before it runs (CM1), and the smooth hand with its drift - the state ... |
| 44 | 3 | **type** `record Shot(CameraPath.View view, CameraPath.Move move, boolean more)` | What a frame of the camera asks of the view: a view to take whole, or a move to ease on, or neither; and whether the frames must go on. |
| 48 | 1 | **type** `private enum Phase` |  |
| 71 | 4 | `ReplayCamera(ReplayMode mode, UserInterface ui)` |  |
| 76 | 1 | `CameraPath.Smooth smooth()` |  |
| 79 | 1 | `boolean busy()` | Whether a move is loading, waits for its click or is in flight: the month does not rest, the hand's input stops it. |
| 82 | 1 | `boolean moving()` | Whether a glide or a fly is under way - not loading, not waiting for the click, not idle (CM1: the frames MapView tallies). |
| 85 | 1 | `boolean loading()` | Whether a move is loading (CM1: MapView walks its views). |
| 88 | 1 | `boolean ready()` | Whether a move is loaded and waits for the click (CM1). |
| 91 | 1 | `int serial()` | Which move is set up: it moves on each time one is (MapView drops what it loaded of the move before). |
| 94 | 1 | `boolean holdsLoad()` | Whether MapView keeps what it loaded: from the move's load until it has run or been let go (CM1). |
| 97 | 1 | `String phaseName()` | The phase's name - IDLE, LOAD, READY, GLIDE or FLY (ReplayMoveProbe reads it). |
| 100 | 1 | `String status()` | What the bar says of a move ("Loading the move... |
| 103 | 1 | `String overlay()` | What MapView writes over the map while a move loads or waits for its click, or null (CM1). |
| 106 | 1 | `int shownMonth()` | The recorded month on screen (CM1: the load's month store reads it). |
| 109 | 1 | `boolean movesTime()` | Whether the move being loaded or run takes time with it: a glide whose two ends are of different months (a fly keeps the month on screen). |
| 112 | 3 | `void showMonthOf(CameraPath.View v)` | The month of a plan view put on screen, as the glide does it frame by frame (by quick steps); a fly leaves the month alone (CM1: the load shows each view's month). |
| 117 | 5 | `private CameraPath.View viewAt(double at)` | The view at a share {@code at} of the move: the glide's, the fly out's or the fly in's curve - the run's and the load's. |
| 123 | 1 | `private int planSteps()` |  |
| 126 | 1 | `int planViews()` | How many views the load walks: PLAN_HZ of them a second of the move, and one more for its end (CM1). |
| 129 | 1 | `CameraPath.View planView(int k)` | The k-th view of the load, k from 0 to planViews() - 1: the move's curve at k of planSteps() of its length (CM1). |
| 132 | 1 | `CameraPath.View startView()` | The view the move begins at (CM1). |
| 134 | 1 | `CameraPath.View markA()` |  |
| 136 | 1 | `CameraPath.View markB()` |  |
| 138 | 1 | `double glideSeconds()` |  |
| 140 | 3 | `void setGlideSeconds(double s)` |  |
| 145 | 6 | `void reset()` | Back to nothing marked, nothing in flight, no drift: a replay opened or left. |
| 153 | 6 | `static String words(CameraPath.View v)` | The marks as the bar's tooltip says them: "month 123, ..." or "not marked". |
| 160 | 3 | `private static String shortScale(double s)` |  |

### the keys and the buttons (lines 164-347)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 8 | `private CameraPath.View viewOrSay()` | The expanded map's view with the month on screen, or null with the bar told the map is not open. |
| 177 | 6 | `void mark(boolean a)` | Marks the expanded map's view (and the month on screen) as A or B. |
| 185 | 14 | `void glide()` | Sets up the glide from A to B over the bar's seconds: it loads (MapView), then waits for a click. |
| 201 | 16 | `void flyOut()` | Flies out from the view on show to the whole Earth (or, on a generated world, the whole world), over the time the doublings of its width take. |
| 219 | 18 | `void flyIn()` | Flies back in to where the last fly out began. |
| 239 | 12 | `private void beginLoad(boolean glide, boolean out, double secs)` | The move set up (from, to): its load begins - MapView walks the plan's views and reports back (loading(), loadDone(), loadFailed()). |
| 257 | 12 | `void loading(int done, int of, int passed)` | MapView's report while it loads (CM1): {@code done} of {@code of} views walked, {@code passed} of them given up. |
| 271 | 6 | `void loadDone(int passed)` | MapView has loaded the whole move (CM1): it waits for the click. |
| 279 | 4 | `void loadFailed(String why)` | MapView could not load the move (CM1): it is let go and the bar says why. |
| 285 | 7 | `void run()` | The click (or a key) on a loaded move runs it (CM1); nothing from any other phase. |
| 294 | 6 | `void stop()` | A move let go where it is (Esc, a button) - while it loads or waits for its click as in flight; the smooth hand's moves go on. |
| 302 | 3 | `void hand()` | A hand on the mouse, the wheel or W A S D takes the camera from a move: it stops where it is. |
| 307 | 6 | `void mapGone()` | The map closed (or another screen is up) while a move was loading, waiting or in flight. |
| 315 | 5 | `void stillAll()` | X: every drift and move stopped, a loading or loaded move, a glide or fly with them. |
| 321 | 4 | `private void stopMove()` |  |
| 327 | 5 | `void driftBy(double across, double down, double zoom)` | Adds to the drift: px a second across and down (the ground's, as W A S D move it) and ln of the zoom a second. |
| 333 | 1 | `private static double clean(double v)` |  |
| 336 | 11 | `String driftReading()` | The drift as the bar reads it ("drift up 24 · left 48 px a second · in 6% a second"), or null when there is none. |

### the frame (lines 348-390)

| line | len | member | says |
|---:|---:|---|---|
| 355 | 15 | `Shot frame(double dt)` | One frame (MapView.replayCamera()): {@code dt} seconds since the last. |
| 371 | 19 | `private Shot advance(double dt)` |  |

