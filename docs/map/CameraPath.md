# CameraPath.java - 231 lines · 20 methods · 8 constants · model

`ham/citybuildersim/CameraPath.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The replay camera's arithmetic without the toolkit (TR1, 2026-10-31): the glide between two marked views, the fly out to the
> whole Earth and back, and the smooth hand that eases the wheel, W A S D and the drift - what ui/MapView applies to its frame
> and ReplayCheck 5 holds, as GlobeFrame and MapFrame are for the view they move.
> 
> WHY IT EXISTS (TR1): Jerus films the game's trailer from a recorded city with a screen recorder, and two of its shots are a
> camera move - the 0:54-1:35 glide while the city grows, the 2:40-3:12 pull-out from the streets to the whole Earth. A hand on
> the wheel cannot make either on a recording: a notch is an instant jump of a fifth of the scale, a drag steps with the pointer's
> jitter. A move there has to be a curve the game follows a frame at a time, eased at both ends, whose scale moves in proportion
> (a view twice as close covers half the ground, so the same share of the scale per second reads as the same speed) and whose
> centre crosses the screen at an even pace while it does.
> 
> <p>A {@link View} is the camera's state for a mark, a glide or a fly: on the globe (an Earth world) a and b are the centre's
> latitude and longitude and the scale is km a pixel ({@code GlobeFrame.kmPx()}, larger is farther); on the plane (a generated
> world) a and b are the centre's plot x and y and the scale is pixels a plot ({@code MapFrame.scale()}, larger is closer). The
> roll is not kept: {@code GlobeFrame.set()} derives it from the centre and the scale, so a glide's roll follows its path.

**Uses:** [MapFrame](MapFrame.md) (1)

**Used by (6):** [MapView](MapView.md), [ReplayCamera](ReplayCamera.md), [ReplayCheck](ReplayCheck.md), [ReplayMode](ReplayMode.md), [ReplayMoveProbe](ReplayMoveProbe.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 115 | · the great circle |
| 144 | · the smooth hand |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 28 | `CameraPath.FLY_DOUBLINGS_A_SECOND` | `1.0` | How many times the view's width doubles in a second of the fly out and back: 1 - so a fly from a street, many doublings from the whole planet, takes longer than one from the region. |
| 31 | `CameraPath.SMOOTH_S` | `0.2` | The time the smooth hand takes to cover most of a move, in seconds: 0.2 - each frame eases off the share 1 - exp(-dt / SMOOTH_S) of the zoom still to do and of the gap between the pan velocity and the keys', so a notc... |
| 34 | `CameraPath.MOST_DT_S` | `1.0 / 20` | The longest step the smooth hand takes, in seconds: 1/20 - a frame held up (a collection, a load) is a pause in the move, not a jump. |
| 37 | `CameraPath.ZOOM_STEP` | `MapFrame.ZOOM_STEP` | A notch of the wheel zooms by this much (the map's own, MapFrame.ZOOM_STEP). |
| 40 | `CameraPath.DRIFT_STEP_PX` | `24` | Each press of Shift+W, A, S or D adds this many pixels a second to the drift that way: 24 - a still shot's slow constant move, about two seconds to cross 50 px. |
| 43 | `CameraPath.DRIFT_ZOOM_STEP` | `0.06` | Each press of R or F adds this to the drift's zoom, the natural log of the magnification a second: 0.06 - a press of R closes in by 6% a second. |
| 46 | `CameraPath.REST` | `1e-7` | What is left of a zoom below this is let go, so the move ends: 1e-7 of a log, far under a pixel of any view. |
| 49 | `CameraPath.REST_V` | `0.01` | ...and of a velocity with no key asking for it: 0.01 px a second. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 158 | `private double pending` | the zoom still to do, ln of the factor |
| 159 | `private double anchorX, anchorY` | the zoom still to do, ln of the factor |
| 160 | `private double centreX, centreY` | the wheel's anchor, the last notch's point |
| 161 | `private double keyVx, keyVy` | where a zoom with no wheel behind it closes in: the view's middle |
| 162 | `private double vx, vy` | the velocity the keys ask for, px a second |
| 163 | `private double driftVx, driftVy, driftZoom` | the velocity now |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 23 | 209 | **type** `public final class CameraPath` | The replay camera's arithmetic without the toolkit (TR1, 2026-10-31): the glide between two marked views, the fly out to the whole Earth and back, and the smooth hand that eases the wheel, W A S D and the drift - what... |
| 25 | 1 | `private CameraPath()` |  |
| 55 | 1 | **type** `public record View(boolean globe, double a, double b, double scale, int month)` | A camera view: {@code globe} says which kind (see the class's account), {@code a} and {@code b} the centre, {@code scale} how close, {@code month} the recorded month on screen when it was marked or when the glide is a... |
| 58 | 4 | `public static double ease(double t)` | The ease: (1 - cos(pi t)) / 2 of t clamped to [0, 1] - 0 and 1 at the ends, still at both, and symmetric (ease(1 - t) = 1 - ease(t)). |
| 72 | 14 | `public static View glide(View from, View to, double t)` | The view a share t of the way from {@code from} to {@code to}, eased. |
| 88 | 4 | `public static double share(double r, double e)` | The centre's share of the way at the eased share e when the pixels a unit change by the ratio r: (1 - r^-e) / (1 - r^-1), e itself when r is 1. |
| 94 | 6 | `public static List<View> path(View from, View to, int n)` | n + 1 views from t = 0 to t = 1 along the glide, equally spaced in t (n at least 1): what the warm-up walks before a glide starts. |
| 106 | 3 | `public static View flyOut(View from, double widestScale, double cityA, double cityB, double t)` | The fly out: the glide from {@code from} to the whole planet facing the city, the widest view - {@code widestScale} (on the globe GlobeFrame.mostKmPx(), on a generated world MapFrame.leastScale()) at the city's lat/lo... |
| 111 | 3 | `public static double flySeconds(double fromScale, double toScale)` | How long a fly takes, in seconds: the doublings of the view's width between the two scales over FLY_DOUBLINGS_A_SECOND. |

### the great circle (lines 115-143)

| line | len | member | says |
|---:|---:|---|---|
| 117 | 4 | `private static double[] unit(double lat, double lon)` |  |
| 123 | 20 | `static double[] slerp(double lat0, double lon0, double lat1, double lon1, double u)` | The point a share u of the way along the great circle from (lat0, lon0) to (lat1, lon1), {lat, lon} in degrees; the first point for a path of no length. |

### the smooth hand (lines 144-231)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 3 | **type** `public record Move(double zoom, double sx, double sy, double dx, double dy)` | What a step of the smooth hand asks of the view: a zoom factor about the point (sx, sy) (above 1 closer) and pixels to pan; 1 and 0 when nothing moves. |
| 148 | 1 | `public boolean none()` _(in CameraPath.Move)_ |  |
| 157 | 74 | **type** `public static final class Smooth` | What the wheel, W A S D and the drift do in a replay, a step a frame (TR1): a notch adds its zoom to what is still to do about its anchor, and each step eases a share of it off ({@code 1 - exp(-dt / SMOOTH_S)}); the k... |
| 166 | 5 | `public void wheel(double sx, double sy, double notches)` _(in CameraPath.Smooth)_ | Adds {@code notches} (positive closer) of the wheel's zoom still to do about (sx, sy); the last anchor holds. |
| 173 | 4 | `public void centre(double sx, double sy)` _(in CameraPath.Smooth)_ | The point a drift's zoom closes in on (the view's middle): a zoom still to do from the wheel keeps its own anchor. |
| 179 | 4 | `public void keys(double vx, double vy)` _(in CameraPath.Smooth)_ | The pan velocity the keys ask for, {across, down} px a second the ground moves (MapFrame.keyVelocity()'s); {0, 0} lets go. |
| 185 | 5 | `public void drift(double vx, double vy, double zoomPerSecond)` _(in CameraPath.Smooth)_ | A constant move kept until still(): the ground moves (vx, vy) px a second and the zoom closes by exp(zoomPerSecond) a second (negative opens). |
| 192 | 5 | `public void still()` _(in CameraPath.Smooth)_ | Stops every move: the drift, the zoom still to do, the keys' velocity and the velocity the hand has. |
| 199 | 1 | `public double[] driftNow()` _(in CameraPath.Smooth)_ | The drift now: {px a second across, px a second down, ln of the zoom a second}. |
| 202 | 1 | `public boolean drifting()` _(in CameraPath.Smooth)_ | Whether a drift is set. |
| 205 | 1 | `public double[] velocity()` _(in CameraPath.Smooth)_ | The pan velocity the hand has now, {across, down} px a second (a harness's). |
| 208 | 4 | `public boolean moving()` _(in CameraPath.Smooth)_ | Whether anything is still moving or about to: zoom to do, a velocity, the keys' ask or a drift - the frames go on while so. |
| 214 | 16 | `public Move step(double dt)` _(in CameraPath.Smooth)_ | One step of dt seconds (cut to MOST_DT_S): the move to apply to the view. |

