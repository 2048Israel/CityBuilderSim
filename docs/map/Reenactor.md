# Reenactor.java - 463 lines · 33 methods · 5 constants · interface

`ham/citybuildersim/ui/Reenactor.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> Re-enacts one logged input event in the window (TR2, 2026-11-09): the mouse moved over the copy's real nodes, pressed,
> dragged, released and clicked on them, the wheel turned over them, the keys pressed into the focused control - as events
> fired into the scene, so the nodes' own handlers and the game's key filter answer as they did in the sitting. What not to
> fire is {@link FollowRules}' to say; this class asks it for every event and keeps a press's verdict for its drags and its
> release.
> 
> <p>WHY (TR2): Follow plays a sitting back into the copy's window and Jerus chose "Re-enact it" over a ghost that only points:
> a tab switches, a page scrolls, a hover card opens, a dial moves, because the ghost did it. JavaFX has no public pick and
> no way to hand a scene a mouse position without a real pointer, so this picks by itself (the same rules as the scene's: the
> top layer first, the last child first, invisible, disabled and mouse-transparent nodes and clipped-out points skipped) and
> builds the events the scene's own handler would. Everything runs on the FX thread.

**Uses:** [InputLog](InputLog.md) (32), [FollowRules](FollowRules.md) (3), [UserInterface](UserInterface.md) (2)

**Used by (2):** [FollowBench](FollowBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 169 | · the mouse |
| 296 | · the hover |
| 337 | · the pick |
| 402 | · the keys |
| 431 | · the firing |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `Reenactor.CLICK_SLOP` | `3` | How far, in pixels, a press may be dragged and still count as one that has not moved (the click's "still since press"). |
| 47 | `Reenactor.BOUNDS_SLACK` | `1` | How far outside a parent's bounds a point may lie and still be looked for in its children: the bounds' own rounding. |
| 50 | `Reenactor.SET_HOVER` | `nodeSetter("setHover")` | Node.setHover, which is protected: found by reflection, or null where the platform will not allow it. |
| 53 | `Reenactor.SET_PRESSED` | `nodeSetter("setPressed")` | Node.setPressed, which is protected too: found the same way, or null. |
| 56 | `Reenactor.KEYS` | `new HashMap<>()` | The toolkit's key for each log code (InputLog.keyCode of the key's name). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private final UserInterface ui` |  |
| 64 | `private final List<Node> excluded` | The layers of the window's stack that are never picked (the time bar, the ghost, the graphs). |
| 66 | `private int firing` | How many synthesized events are being dispatched now. |
| 69 | `private List<Node> hovered` | The nodes under the pointer, outermost first. |
| 72 | `private boolean gesture` | The press being re-enacted. |
| 73 | `private Verdict pressVerdict` | a press was seen and its release has not come |
| 74 | `private Node pressTarget` | null for a dropped press |
| 75 | `private List<Node> pressChain` | null for a dropped press |
| 76 | `private int pressButton, pressClicks, pressMods` | the press target and its ancestors, deepest first |
| 77 | `private double pressX, pressY` |  |
| 78 | `private boolean still` |  |
| 81 | `private final Map<KeyCode, Integer> keysDown` | The keys whose KEY_PRESSED was fired and whose KEY_RELEASED has not been, with the modifiers of the press: reset() lets go of them, so a copy does not keep panning (TR2's review). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 423 | **type** `final class Reenactor` | Re-enacts one logged input event in the window (TR2, 2026-11-09): the mouse moved over the copy's real nodes, pressed, dragged, released and clicked on them, the wheel turned over them, the keys pressed into the focus... |
| 58 | 3 | `static { ... }` |  |
| 83 | 3 | `Reenactor(UserInterface ui)` |  |
| 88 | 3 | `boolean isFiring()` | Whether a synthesized event is being dispatched now: the window's key handler then lets the replay's own keys alone. |
| 93 | 3 | `static boolean setsHover()` | Whether Node.setHover could be called (the nodes under the pointer then answer isHover() and :hover). |
| 98 | 3 | `static boolean setsPressed()` | Whether Node.setPressed could be called. |
| 107 | 14 | `void reset()` | The hover and the press forgotten, without a word to any node: after a month was loaded whole, the nodes they were on are gone. |
| 123 | 7 | `private void forgetPress()` | The press forgotten: no node keeps the pressed state it was given. |
| 132 | 4 | `void exclude(Node layer)` | A layer of the window's stack that is never picked: the time bar, the ghost, the graphs. |
| 137 | 3 | `Verdict play(InputLog.Event e, String loggedScreen)` |  |
| 147 | 13 | `Verdict play(InputLog.Event e, String loggedScreen, boolean catchingUp)` | Re-enacts one event. |
| 161 | 7 | `private Verdict judge(InputLog.Event e, String loggedScreen, boolean onDropNode)` |  |

### the mouse (lines 169-295)

| line | len | member | says |
|---:|---:|---|---|
| 171 | 8 | `private Verdict move(InputLog.Event e, String loggedScreen)` |  |
| 180 | 22 | `private Verdict press(InputLog.Event e, String loggedScreen, boolean catchingUp)` |  |
| 203 | 11 | `private Verdict drag(InputLog.Event e, String loggedScreen)` |  |
| 215 | 28 | `private Verdict release(InputLog.Event e, String loggedScreen, boolean catchingUp)` |  |
| 245 | 6 | `private void abandonPress()` | A press whose release never came: let go of it where it was, with no click. |
| 252 | 15 | `private Verdict wheel(InputLog.Event e, String loggedScreen)` |  |
| 269 | 3 | `private MouseEvent mouse(EventType<MouseEvent> type, Node target, double x, double y, MouseButton button, int clicks, int mods,...` | A mouse event for the scene's dispatch, from the scene at the scene point, aimed at {@code target}; the buttons down as {@code down} (the log's number). |
| 273 | 7 | `private MouseEvent mouse(EventType<MouseEvent> type, Node target, double x, double y, MouseButton button, int clicks, int mods,...` |  |
| 281 | 5 | `private static Point2D screenOf(Scene scene, double x, double y)` |  |
| 287 | 8 | `private static MouseButton button(int logged)` |  |

### the hover (lines 296-336)

| line | len | member | says |
|---:|---:|---|---|
| 303 | 18 | `private void updateHover(Node hit, InputLog.Event at)` | The nodes under the pointer are now those of {@code hit}: each node left is told (deepest first) and each entered is told (outermost first), as the scene's own mouse handler does it. |
| 323 | 5 | `private static List<Node> chain(Node n)` | {@code n} and its ancestors, deepest first. |
| 330 | 6 | `private static Node commonAncestor(List<Node> pressChain, Node hit)` | The deepest node of the press's chain (deepest first) that is {@code hit} or one of its ancestors, or null. |

### the pick (lines 337-401)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 14 | `Node pick(double x, double y)` | The deepest node of the window under the scene point, as the scene would pick it, or null. |
| 359 | 18 | `private static Node pickIn(Node n, Point2D inParent)` | {@code n}, if the point (in its parent's coordinates) is on it and no child of it is deeper there; else null. |
| 379 | 7 | `private static List<Node> inFront(List<Node> children)` | The children back to front: the order they are drawn in, which is their order, but by viewOrder (the higher the further back) where any is set. |
| 388 | 6 | `static boolean onDropNode(Node n)` | Whether the node, or any ancestor of it, is marked {@link FollowRules#DROP}. |
| 395 | 6 | `private void relayout()` |  |

### the keys (lines 402-430)

| line | len | member | says |
|---:|---:|---|---|
| 404 | 20 | `private Verdict key(InputLog.Event e, String loggedScreen)` |  |
| 426 | 4 | `private static Node target(Scene scene)` | Where a key goes: the focused control, or the scene's root where nothing has the focus (or the owner is one a redraw took out of the scene and no pulse has cleared yet). |

### the firing (lines 431-463)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 10 | `private void fire(Node target, Event ev)` | Dispatches the event through the node's chain (the scene's filters and handlers included), counted as firing. |
| 445 | 9 | `private static Method nodeSetter(String name)` |  |
| 455 | 8 | `private static void setFlag(Method setter, Node n, boolean on)` |  |

