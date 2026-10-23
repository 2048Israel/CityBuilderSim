# KeptBench.java - 255 lines · 17 methods · 2 constants · interface

`ham/citybuildersim/ui/KeptBench.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The People page on a scene of its own - no stage, no window - drawn kept and built new, for
> KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui
> package's own.
> 
> <p>WHY. A kept page is right only if every month's redraw over last month's nodes ends where
> a page built new from the same city would; the one way to know is to build both and compare
> them, node by node and pixel by pixel. The window cannot be used for it: it opens maximised,
> and the suite runs on the PC beside Jerus's game. A Scene with no stage takes CSS, layout and
> a snapshot all the same, so the bench lays the page out there, at the menu column's width.
> The FX toolkit must be up and every call made on its thread; KeptCheck sees to both.

**Uses:** [UserInterface](UserInterface.md) (2), [Palette](Palette.md) (2), [Kept](Kept.md) (2), [Game](Game.md) (1)

**Used by (1):** [KeptCheck](KeptCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `KeptBench.WIDTH` | `1456` | The width the page is laid out at: the menu column's in a 1600-pixel window (the study's renders). |
| 201 | `KeptBench.PRECISE` | `Boolean.getBoolean("keptbench.precise")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private final UserInterface ui` |  |
| 52 | `private final VBox column` |  |
| 53 | `private final Scene scene` |  |
| 54 | `private List<Node> kept` |  |
| 148 | `private static Method doCSSPass, doLayoutPass` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 46 | 210 | **type** `public final class KeptBench` | The People page on a scene of its own - no stage, no window - drawn kept and built new, for KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui package's own. |
| 57 | 12 | `public KeptBench(Game game)` | The bench on a city: the window's object with no stage, its menu column in a scene of its own. |
| 71 | 5 | `public double drawKept()` | People's page drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. |
| 78 | 3 | `public int lastMade()` | The nodes the last kept draw made new. |
| 83 | 3 | `public int lastKept()` | The nodes the last kept draw handed back. |
| 88 | 3 | `public Node top()` | The page's top node, kept or not: the same object from one kept draw to the next. |
| 96 | 8 | `public double[] showFresh() throws Exception` | The page built new in place of the kept one (no pool, every node new) and laid out, as every redraw was before UP4: {its draw ms, CSS ms, layout ms}. |
| 106 | 4 | `public void restoreKept() throws Exception` | The kept page back in the column, as it was, and laid out. |
| 116 | 3 | `public String spoil()` | One figure on the page made wrong: the first label whose words hold a digit gets a "9" in front (KeptCheck's proof that its comparison sees one wrong figure, and that the next kept draw sets it right). |
| 120 | 16 | `private static String spoil(List<Node> nodes)` |  |
| 138 | 4 | `public void details(String name, boolean open)` | A "details" fold opened or closed, as its toggle would (the next draw shows it). |
| 144 | 3 | `public void yearView(boolean on)` | The month card's year view on or off. |
| 155 | 22 | `public double[] passes() throws Exception` | The scene's CSS and layout, as a frame's pulse runs them (Scene.doCSSPass and doLayoutPass, which touch only what is dirty): {CSS ms, layout ms}. |
| 179 | 9 | `public int[] pixels()` | The page as pixels, ARGB, row by row: the menu column's snapshot, with its width and height first. |
| 195 | 5 | `public List<String> signature()` | The page as it is now, a line a node in the order its code made them: the node's class; its words; its inline style; the values code set on it (the styleable ones whose origin is code); whether it has a click, a hover... |
| 203 | 22 | `private static void sign(Node n, String path, List<String> out)` |  |
| 232 | 12 | `private static String codeSet(Styleable n)` | Every styleable value a node has now, by property, where it is not the property's initial value: what code set and what its style made of it, both pages styled and laid out alike. |
| 246 | 9 | `private static String shape(Node n)` | A shape's own geometry, which no style sets. |

