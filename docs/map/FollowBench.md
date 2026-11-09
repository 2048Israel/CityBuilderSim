# FollowBench.java - 313 lines · 13 methods · 2 constants · interface

`ham/citybuildersim/ui/FollowBench.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an
> undecorated stage that is invisible and off every screen, laid out at 1600 x 900), and logged events played into it, with
> what each one did read back. {@link #check} gives the findings as {label, "true"/"false"} pairs for a harness to print, and
> must be called on the FX thread; {@link #main} makes a small city of its own in a scratch folder and prints them.
> 
> <p>WHY (TR2): the Reenactor fires events into the scene by hand, and whether the scene answers them as it answers a pointer
> - a tab switches on a click, a handler sees the pointer enter, a wheel moves a pane, a key reaches the game's filter - can only
> be known by doing it to the real window. Run: {@code java -cp "target/classes;$(cat target/classpath.txt)"
> ham.citybuildersim.ui.FollowBench}; the exit code is the number of failures.

**Uses:** [InputLog](InputLog.md) (24), [UserInterface](UserInterface.md) (7), [FollowRules](FollowRules.md) (7), [Reenactor](Reenactor.md) (5), [GameFiles](GameFiles.md) (2), [Game](Game.md) (2), [LongPlaytest](LongPlaytest.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `FollowBench.WIDTH` | `1600` | The window's width the bench lays the scene out at. |
| 51 | `FollowBench.HEIGHT` | `900` | The window's height the bench lays the scene out at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `private static int failures` | The findings that failed: what a bench prints as the exit code. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 43 | 271 | **type** `public final class FollowBench` | The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an undecorated stage that is invisible and off every screen, laid out at 1600 x 900), and logged ev... |
| 45 | 1 | `private FollowBench()` |  |
| 56 | 27 | `public static void main(String[] args) throws Exception` |  |
| 84 | 5 | `private static<T> T fx(Callable<T> c) throws Exception` |  |
| 91 | 25 | `public static List<String[]> check(Game game)` | The findings, on the FX thread, for the city {@code game}: the window is built on it and hidden again at the end. |
| 118 | 6 | `private static void layout(UserInterface ui)` | The scene's CSS and layout at the bench's size, as a pulse would do them. |
| 125 | 3 | `private static void add(List<String[]> out, String label, boolean ok)` |  |
| 129 | 141 | `private static void run(UserInterface ui, List<String[]> out)` |  |
| 271 | 3 | `private static int code(KeyCode k)` |  |
| 275 | 3 | `private static InputLog.Event key(long ms, boolean down, KeyCode k, String text)` |  |
| 280 | 7 | `private static void sized(javafx.scene.layout.Region n, double w, double h, Pos at, Insets margin)` | A probe node of a fixed size, set in the window's stack at a corner or an edge. |
| 289 | 4 | `private static double[] centre(Node n)` | The scene point at the middle of a node. |
| 295 | 8 | `private static Node railCell(UserInterface ui, String word)` | The rail's button with this word under its icon. |
| 305 | 8 | `private static Node clockButton(UserInterface ui)` | The header's play button, the first button in its clock block. |

