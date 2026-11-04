# ReplayCamera.java - 327 lines · 29 methods · 6 constants · interface

`ham/citybuildersim/ui/ReplayCamera.java` - generated 2026-11-04 by CodeMap; line numbers are as of that run.

> The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back,
> the warm-up before a glide, and the smooth hand with its drift - the state of every camera move, frame by frame. The arithmetic
> is {@link CameraPath}; ui/MapView is the toolkit's half and asks this for one {@link Shot} a frame (MapView.replayCamera()), a
> view to take whole or a move to ease on, and applies it; ReplayMode owns this, takes the keys and the bar's camera group, and
> is the month on screen (a glide of views with different months moves time with it, by quick steps).
> 
> WHY IT EXISTS (TR1): the trailer's camera moves - the glide while the city grows, the pull-out to the whole planet - cannot be
> made by a hand on the wheel on a screen recording. They are curves the game follows a frame at a time, and the first frame of a
> glide must not find the map half painted: before a glide starts the camera walks its path once so every tile on it is drawn.
> 
> <p>The warm-up walks {@link #WARM_VIEWS} views of the path a frame each, staying on one until MapView says its painting has
> nothing left to do and nothing asked of the worker is outstanding (or {@link #WARM_VIEW_S} has passed), then returns to A and
> starts. It warms the start month's map only: a glide across months swaps the map as it goes, each quick step keeping the
> pictures that did not change (MapView.staleAll()), so what the glide shows of a later month is painted as it comes.
> 
> <p>Everything runs on the FX thread, from MapView's frame. Nothing here touches a node.

**Uses:** [CameraPath](CameraPath.md) (31), [ReplayMode](ReplayMode.md) (2), [UserInterface](UserInterface.md) (2)

**Used by (3):** [MapView](MapView.md), [ReplayMode](ReplayMode.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 112 | · the keys and the buttons |
| 255 | · the frame |

## Enum constants

| line | constant | says |
|---:|---|---|
| 46 | `ReplayCamera.Phase.IDLE` |  |
| 46 | `ReplayCamera.Phase.WARM` |  |
| 46 | `ReplayCamera.Phase.GLIDE` |  |
| 46 | `ReplayCamera.Phase.FLY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 27 | `ReplayCamera.WARM_VIEWS` | `24` | How many steps of the glide's path the warm-up walks: 24, so 25 views from A to B. |
| 30 | `ReplayCamera.WARM_VIEW_S` | `1.0` | The most the warm-up stays on one view, in seconds: 1 - a view whose last tile never comes (a far node the worker cannot ground) does not hold the rest. |
| 33 | `ReplayCamera.WARM_MOST_S` | `8` | The most the whole warm-up takes, in seconds: 8 - then the glide starts with what is painted. |
| 36 | `ReplayCamera.MOST_DT_S` | `0.1` | The longest step of a glide's or fly's own clock, in seconds: 0.1 - a frame held up (a quick step of the map) is a pause in the move, not a jump ahead. |
| 39 | `ReplayCamera.GLIDE_S` | `10, GLIDE_S_LEAST = 1, GLIDE_S_MOST = 600` | The seconds a glide takes when the bar's field says nothing: 10; the field's least and most. |
| 43 | `ReplayCamera.Shot.IDLE` | `new Shot(null, null, false)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private final ReplayMode mode` |  |
| 49 | `private final UserInterface ui` |  |
| 50 | `private final CameraPath.Smooth smooth` |  |
| 52 | `private CameraPath.View markA, markB` |  |
| 53 | `private double glideSeconds` |  |
| 55 | `private Phase phase` |  |
| 56 | `private String status` |  |
| 58 | `private List<CameraPath.View> path` | The warm-up. |
| 59 | `private int warmAt` |  |
| 60 | `private boolean warmApplied` |  |
| 61 | `private double warmS, warmViewS` |  |
| 63 | `private CameraPath.View from, to` | A glide or a fly: from where, to where, how far along (t), over how long. |
| 64 | `private boolean flyingOut` |  |
| 65 | `private double t, seconds` |  |
| 67 | `private CameraPath.View flyBack` | Where the last fly out began, which the fly in goes back to. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 304 | **type** `final class ReplayCamera` | The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back, the warm-up before a glide, and the smooth hand with its drift - the state of every came... |
| 42 | 3 | **type** `record Shot(CameraPath.View view, CameraPath.Move move, boolean more)` | What a frame of the camera asks of the view: a view to take whole, or a move to ease on, or neither; and whether the frames must go on. |
| 46 | 1 | **type** `private enum Phase` |  |
| 69 | 4 | `ReplayCamera(ReplayMode mode, UserInterface ui)` |  |
| 74 | 1 | `CameraPath.Smooth smooth()` |  |
| 77 | 1 | `boolean busy()` | Whether a warm-up, a glide or a fly is in flight: the month does not rest, the hand's input stops it. |
| 80 | 1 | `String status()` | What the bar says of a move in flight ("warming up the glide 7 of 25"), or null. |
| 82 | 1 | `CameraPath.View markA()` |  |
| 84 | 1 | `CameraPath.View markB()` |  |
| 86 | 1 | `double glideSeconds()` |  |
| 88 | 3 | `void setGlideSeconds(double s)` |  |
| 93 | 6 | `void reset()` | Back to nothing marked, nothing in flight, no drift: a replay opened or left. |
| 101 | 6 | `static String words(CameraPath.View v)` | The marks as the bar's tooltip says them: "month 123, ..." or "not marked". |
| 108 | 3 | `private static String shortScale(double s)` |  |

### the keys and the buttons (lines 112-254)

| line | len | member | says |
|---:|---:|---|---|
| 115 | 8 | `private CameraPath.View viewOrSay()` | The expanded map's view with the month on screen, or null with the bar told the map is not open. |
| 125 | 6 | `void mark(boolean a)` | Marks the expanded map's view (and the month on screen) as A or B. |
| 133 | 18 | `void glide()` | Starts the warm-up and then the glide from A to B over the bar's seconds. |
| 153 | 16 | `void flyOut()` | Flies out from the view on show to the whole Earth (or, on a generated world, the whole world), over the time the doublings of its width take. |
| 171 | 18 | `void flyIn()` | Flies back in to where the last fly out began. |
| 190 | 8 | `private void begin(boolean out, double secs, String words)` |  |
| 200 | 6 | `void stop()` | A glide, fly or warm-up let go where it is (Esc, a button); the smooth hand's moves go on. |
| 208 | 3 | `void hand()` | A hand on the mouse, the wheel or W A S D takes the camera from a glide or fly: it stops where it is. |
| 213 | 6 | `void mapGone()` | The map closed (or another screen is up) while a move was in flight. |
| 221 | 5 | `void stillAll()` | X: every drift and move stopped, a glide or fly with them. |
| 227 | 5 | `private void stopMove()` |  |
| 234 | 5 | `void driftBy(double across, double down, double zoom)` | Adds to the drift: px a second across and down (the ground's, as W A S D move it) and ln of the zoom a second. |
| 240 | 1 | `private static double clean(double v)` |  |
| 243 | 11 | `String driftReading()` | The drift as the bar reads it ("drift up 24 · left 48 px a second · in 6% a second"), or null when there is none. |

### the frame (lines 255-327)

| line | len | member | says |
|---:|---:|---|---|
| 262 | 13 | `Shot frame(double dt, java.util.function.BooleanSupplier settled)` | One frame (MapView.replayCamera()): {@code dt} seconds since the last, {@code settled} asked (only by the warm-up) whether the view as it stands has nothing left to paint. |
| 276 | 17 | `private Shot warm(double dt, java.util.function.BooleanSupplier settled)` |  |
| 294 | 10 | `private Shot startGlide()` |  |
| 305 | 22 | `private Shot advance(double dt)` |  |

