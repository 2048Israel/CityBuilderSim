# SteadyBench.java - 292 lines · 21 methods · 3 constants · interface

`ham/citybuildersim/ui/SteadyBench.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The real window on a city, page by page, measured for UiSteadyCheck (UX1): every node on the scene, where it
> sits, and where every scroller stands, before and after a redraw.
> 
> <p>WHY. Jerus, 2026-11-27, on the interface rework: "the ui work watches out for scrolling issues, and issues
> were the screen flickers or expands without stopping". KeptCheck holds each kept page against a page built new,
> on a scene of its own; nothing held the window itself - the chrome, the page and the panels together, as the
> player has them - to the plain law that a redraw with nothing changed changes nothing on screen. This bench
> walks the picture tool's pages ({@link PagePictures#pages}), so the harness and the pictures see the same window.
> 
> <p>The window is the game's own (UserInterface.start), at 1920x1080, on a stage drawn transparent and off the
> screen as FollowBench's is, so the suite shows nothing on the player's desktop.

**Uses:** [FollowBench](FollowBench.md) (12), [PagePictures](PagePictures.md) (11), [Game](Game.md) (2), [UserInterface](UserInterface.md) (2)

**Used by (1):** [UiSteadyCheck](UiSteadyCheck.md)

## Sections

| line | section |
|---:|---|
| 163 | · the measure |
| 256 | · the fixtures: each fault caused, and seen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `SteadyBench.STILL` | `0.5` | A node that moves less than this between two measures has not moved (px): a layout's rounding. |
| 40 | `SteadyBench.ARRIVE_MS` | `1500` | How long a page is left after it opens before it is measured: its arrival's animations (a dial's arc, a waterfall's bars; the longest, 1.1 s) run their course (ms). |
| 43 | `SteadyBench.SCROLLS` | `40` | A page scroller holds more than its window when its content is this much taller (px), as the pictures take it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `private final Game g` |  |
| 46 | `private UserInterface ui` |  |
| 47 | `private Stage stage` |  |
| 48 | `private List<PagePictures.Page> walk` |  |
| 167 | `final Map<Node, double[]> nodes` |  |
| 168 | `final Map<ScrollPane, Double> places` |  |
| 170 | `public double pageWidth` | The page scroller's content (width, height), or NaN with no page scroller. |
| 172 | `public double rootWidth, rootHeight, windowWidth, windowHeight` | The window's root, and the window (width, height). |
| 212 | `public final List<String> moved` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 259 | **type** `public final class SteadyBench` | The real window on a city, page by page, measured for UiSteadyCheck (UX1): every node on the scene, where it sits, and where every scroller stands, before and after a redraw. |
| 51 | 3 | `public static String startJavaFx()` | Why JavaFX cannot start here, or null when it is up (FollowBench's one start, GB5). |
| 56 | 21 | `public SteadyBench(Game g) throws Exception` | The game's window on this city, as it lands after a load. |
| 79 | 3 | `public String size() throws Exception` | The window's size as the stage has it (a headless screen may give less than asked). |
| 84 | 5 | `public List<String> pages()` | The pages of the walk, by name ("012-build-industry"). |
| 91 | 11 | `public void open(String page) throws Exception` | A page opened as its rail button and chip open it, and given its layout. |
| 104 | 4 | `public void redraw() throws Exception` | The open page drawn again, in place, with nothing changed: the call the clock makes after a month. |
| 110 | 4 | `public void month() throws Exception` | A month played and the page redrawn, as the clock does it (UserInterface's clock: toggleNextMonth, then the redraw). |
| 116 | 13 | `private void settle() throws Exception` | Two pulses and their layout: what a redraw, or a scroll set, has asked of the next frames, done. |
| 131 | 11 | `public double placePage(double fraction) throws Exception` | The page's own scroller set to a fraction of its run; its place then, in pixels from its top, or -1 when it does not scroll. |
| 144 | 6 | `public double[] pageHold() throws Exception` | The page scroller's place and how far it can move, in pixels from its top; {-1, 0} when there is no page scroller. |
| 152 | 4 | `static double run(ScrollPane sp)` | How far a scroller can move: its content's height over its window's (px). |
| 158 | 4 | `static double place(ScrollPane sp)` | A scroller's place, in pixels from its top. |

### the measure (lines 163-255)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 13 | **type** `public static final class Shape` | The window at one moment: every node on the scene with its bounds there, every scroller's place, the page's size. |
| 175 | 3 | `public int count()` _(in SteadyBench.Shape)_ | How many nodes are on the scene. |
| 181 | 3 | `public Shape shape() throws Exception` | The window as it is now. |
| 186 | 15 | `static Shape measure(Scene sc)` | Every node under a scene's root, and its scrollers (package-level, for the bench's own fixtures). |
| 202 | 7 | `private static void walk(Node n, Shape s)` |  |
| 211 | 8 | **type** `public static final class Diff` | What changed from one measure to the next: nodes moved or resized, made, let go, scrollers moved. |
| 215 | 3 | `public static String few(List<String> l)` _(in SteadyBench.Diff)_ | The first few of a list, for a FAIL line: "Label "1,888" +12.0,+0.0; VBox ..." |
| 221 | 26 | `public static Diff compare(Shape a, Shape b)` | From a to b: every node on both that moved or changed size, every node on one only, every scroller on both whose place moved. |
| 249 | 6 | `static String name(Node n)` | A node as a FAIL line names it: its class, its text if it has one, its style class. |

### the fixtures: each fault caused, and seen (lines 256-292)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 23 | `public static int[][] fixtures() throws Exception` | The measure's own proof, on a scene of its own: a page of labels in a scroller, measured, then each fault the harness looks for caused once - a label put in another's place (made and lost), a box made taller (moved: e... |
| 289 | 3 | `public void close() throws Exception` | The stage put away. |

