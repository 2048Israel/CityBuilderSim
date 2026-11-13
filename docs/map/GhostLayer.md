# GhostLayer.java - 141 lines · 11 methods · 8 constants · interface

`ham/citybuildersim/ui/GhostLayer.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The ghost cursor (TR2, 2026-11-09): a see-through pointer arrow drawn at a scene point over the replay's window, in
> {@link Palette#GHOST}, with a ripple at each click - a ring that grows and fades. One mouse-transparent pane of the window's
> stack, kept just under the time bar; it never takes an event and the Reenactor never picks it. It knows where it stands
> ({@link #x()}, {@link #y()}, {@link #isShown()}) without a pulse, so a harness can read it.
> 
> <p>WHY (TR2): Jerus asked for "a ghost mouse if you go to a screen where the player was... along with their actions". The
> player's pointer is not the viewer's, so it is drawn, not a real cursor; and it must not look like part of the game - the
> accent, a verdict colour or an area's colour on it would read as the interface answering, so it has a colour of its own.
> See-through, because it lies over the very controls the viewer is looking at. The ripples are advanced by the replay's frame
> ({@link #frame}), not by an animation of their own, so the layer needs no toolkit pulse of its own to exist.

**Uses:** [Palette](Palette.md) (3)

**Used by (1):** [ReplayMode](ReplayMode.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `GhostLayer.FILL_ALPHA` | `0.55` | How opaque the arrow's body is, 0 to 1: 0.55, so the page shows through it and it still stands out on the stage's dark ground. |
| 29 | `GhostLayer.EDGE_ALPHA` | `0.9` | How opaque the arrow's outline is, 0 to 1: 0.9, so a see-through body still has an edge to find. |
| 32 | `GhostLayer.RIPPLE_MS` | `400` | How long a click's ripple lasts, in milliseconds: 400, long enough to be seen at 4x and short enough that two clicks never share a ring. |
| 35 | `GhostLayer.RIPPLE_FROM_PX` | `4` | The radius a ripple starts at, in pixels: 4. |
| 38 | `GhostLayer.RIPPLE_TO_PX` | `30` | The radius a ripple has grown to when it fades out, in pixels: 30. |
| 41 | `GhostLayer.RIPPLE_WIDTH_PX` | `2.5` | How thick a ripple's ring is, in pixels: 2.5. |
| 44 | `GhostLayer.MAX_RIPPLES` | `12` | The most ripples shown at once, the oldest dropped: 12 - a burst of fast clicks at 4x. |
| 47 | `GhostLayer.ARROW` | `"M0 0 L0 16 L4 12.5 L6.8 19 L9.2 18 L6.5 11.6 L11.5 11.6 Z"` | The pointer's outline with its tip at the origin, in pixels: the usual arrow, 19 px tall. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `final Circle ring` |  |
| 51 | `final long startNs` |  |
| 59 | `private final Pane layer` |  |
| 60 | `private final SVGPath arrow` |  |
| 61 | `private final List<Ripple> ripples` |  |
| 62 | `private boolean shown` |  |
| 63 | `private double x, y` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 119 | **type** `final class GhostLayer` | The ghost cursor (TR2, 2026-11-09): a see-through pointer arrow drawn at a scene point over the replay's window, in {@link Palette#GHOST}, with a ripple at each click - a ring that grows and fades. |
| 49 | 9 | **type** `private static final class Ripple` |  |
| 53 | 4 | `Ripple(Circle ring, long startNs)` _(in GhostLayer.Ripple)_ |  |
| 65 | 12 | `GhostLayer()` |  |
| 79 | 1 | `Node node()` | The layer, to lay in the window's stack. |
| 82 | 1 | `boolean isShown()` | Whether the arrow is on view. |
| 85 | 1 | `double x()` | Where the arrow stands, in scene pixels; meaningful while {@link #isShown()}. |
| 87 | 1 | `double y()` |  |
| 90 | 1 | `int ripples()` | The ripples on view now. |
| 93 | 8 | `void show(double sceneX, double sceneY)` | The arrow's tip at the scene point (the window's stack lies at the scene's origin, so a scene point is the layer's own). |
| 103 | 13 | `void ripple(double sceneX, double sceneY)` | A ring begun at the scene point: it grows and fades over RIPPLE_MS (frame()). |
| 118 | 6 | `void hide()` | The arrow and every ripple gone from view. |
| 126 | 15 | `void frame(long nowNs)` | One frame: every ripple moved along its life, the ones that have run out taken off. |

