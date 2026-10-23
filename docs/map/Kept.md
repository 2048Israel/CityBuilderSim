# Kept.java - 617 lines · 63 methods · 2 constants · interface

`ham/citybuildersim/ui/Kept.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a
> screen's code made at its last draw, handed back to the same code, in the order it asks for
> them, at the next - so a month that lands sets its figures on the page on show instead of
> throwing the page away and building another.
> 
> <p>WHY. Until 0.8.25 every screen was drawn by emptying the page and building it afresh
> (UserInterface.clearMenu()), on every month the clock redraws. A node new to the scene is
> styled from nothing - its skin made, every CSS property looked up - then laid out with its
> text measured, then given a peer for the renderer; a node kept in place pays none of that,
> and a label whose text is unchanged is not even laid out again. At month 4,000 of the
> playtest's city a redraw of People cost 57 ms on the FX thread, of which 36 ms were the new
> nodes' CSS, layout and sync and most of the rest making them (the study's probe). The way
> out the brief named - each screen written twice, once to build and once to update - would
> have been two code paths for every figure, free to drift apart. Here there is one: the
> screen's own drawing code runs every time, and only where its nodes come from changes.
> 
> <p>HOW. A screen draws inside {@link #draw(Pool, Runnable)}. Every node its code makes comes
> through here - {@code Kept.label("12,576")}, {@code Kept.column(8)}, {@code Kept.of(Mirror.class,
> Mirror::new)} - and inside a draw each call hands back the next node of that class the same
> scope made last time, or a new one if it made fewer. Outside a draw every call is a new node,
> exactly as {@code new} was, so a helper shared with screens not yet kept is unchanged for them.
> <ul>
> <li>The boxes are this class's own (Column, Row, Stack, Grid, Flow, Plain). While a draw is
> on, a box's getChildren() is a list of the draw's own; when the draw ends each box's real
> children are set from it in ONE call, and only where it differs. A node left in place is never
> taken off the scene, and that is what keeps its style: cleared and added back in two steps
> the same nodes cost People 20 ms (the study's ReaddProbe).</li>
> <li>Rows that come and go are a scope each, keyed by what they show
> ({@code Kept.scope(sector, () -> row(sector))}): a row that goes takes its own nodes with it,
> and the rows after it keep theirs. A card is a scope too, so a chip that appears in one card
> does not shift every label below it onto a neighbour's.</li>
> <li>A node handed back first loses what the code gives it only sometimes - its click and hover
> handlers, its action, its cursor, visibility, opacity, disabling and tooltip - where the code
> set them (a value an inline style set is the style's, and left alone); so a row clickable last
> month and not this one does not keep last month's click.</li>
> <li>A picture of the game's own (Mirror, SegmentBar...) is kept with its children and told its
> new figures through a method of its own; its constructor makes its children with new, never
> through here, since it runs once in the picture's life.</li>
> </ul>
> 
> <p>THE RULES for code drawn kept, which KeptCheck holds by comparing the kept page with a page
> built new, month after month: a node is made through here, never with new; a value set on a
> node under a condition is set on both sides of it; a list on a node (points, path elements,
> column constraints, style classes) is set, never added to; a listener or event handler added
> to a node is added once ({@link #isNew}) or through {@link #play}/{@link #tip}; and the page
> reaches the window through UserInterface.setPage(), not rootMenu.getChildren().add().

**Used by (8):** [KeptBench](KeptBench.md), [LandScreen](LandScreen.md), [MapTiles](MapTiles.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [Statement](Statement.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 86 | THE POOL, THE DRAW AND THE SCOPES |
| 318 | THE BOXES: their children are the draw's own list while a draw is on |
| 486 | THE NODES, AS THE CODE ASKS FOR THEM |
| 571 | WHAT A NODE IS GIVEN THAT WOULD PILE UP ON A KEPT ONE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 575 | `Kept.TOOLTIP` | `"javafx.scene.control.Tooltip"` |  |
| 576 | `Kept.ANIMATION` | `"kept.animation"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 92 | `private Scope root` |  |
| 94 | `private int draws` | Draws served since the pool last started afresh: 0 means the next draw makes every node new. |
| 96 | `private int lastMade, lastKept` | At the last draw: the nodes it made new, and the nodes it handed back (what KeptCheck counts). |
| 122 | `private Map<Class<?>, List<Node>> made` |  |
| 123 | `private Map<Class<?>, List<Node>> making` |  |
| 124 | `private final Map<Class<?>, Integer> taken` |  |
| 125 | `private Map<Object, Scope> inner` |  |
| 126 | `private Map<Object, Scope> innerMaking` |  |
| 144 | `final Draw outer` | The draw this one was begun inside, or null (UP2: a draw of another pool within a draw). |
| 145 | `final ArrayDeque<Scope> scopes` |  |
| 146 | `final List<Scope> begun` |  |
| 147 | `final List<Staging> staged` |  |
| 148 | `final List<Node> handedBack` |  |
| 149 | `final Set<Node> made` |  |
| 150 | `final Set<Node> tipped` |  |
| 164 | `private static Draw drawing` | The draw on now, or null: then every call here makes a new node. |
| 350 | `private ObservableList<Node> stage` |  |
| 371 | `private ObservableList<Node> stage` |  |
| 392 | `private ObservableList<Node> stage` |  |
| 413 | `private ObservableList<Node> stage` |  |
| 434 | `private ObservableList<Node> stage` |  |
| 458 | `private ObservableList<Node> stage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 536 | **type** `public final class Kept` | A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a screen's code made at its last draw, handed back to the same code, in the order it asks for them, at the next - so a month th... |
| 84 | 1 | `private Kept()` |  |

### THE POOL, THE DRAW AND THE SCOPES (lines 86-317)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 28 | **type** `static final class Pool` | One screen's kept nodes: what each of its scopes made at its last draw. |
| 99 | 3 | `int lastMade()` _(in Kept.Pool)_ | The nodes the last draw made new. |
| 104 | 3 | `int lastKept()` _(in Kept.Pool)_ | The nodes the last draw handed back from the one before. |
| 109 | 4 | `void forget()` _(in Kept.Pool)_ | Every node is made new at the next draw: another screen was shown, or another city opened. |
| 115 | 3 | `boolean holds()` _(in Kept.Pool)_ | Whether the next draw has kept nodes to hand back. |
| 121 | 19 | **type** `private static final class Scope` | One scope's nodes: by class, in the order the code asked for them; and its keyed inner scopes. |
| 128 | 5 | `void begin()` _(in Kept.Scope)_ |  |
| 135 | 4 | `void end()` _(in Kept.Scope)_ | What was not asked for this draw is let go: the next draw hands back only this one's. |
| 142 | 20 | **type** `private static final class Draw` | A draw in progress. |
| 152 | 3 | `Draw(Draw outer)` _(in Kept.Draw)_ |  |
| 157 | 4 | `boolean drawing(Pool pool)` _(in Kept.Draw)_ | Whether this draw, or any it was begun inside, is drawing this pool. |
| 167 | 3 | `static boolean drawing()` | Whether a kept draw is on. |
| 183 | 32 | `static void draw(Pool pool, Runnable code)` | Runs a page's code as a kept draw from the pool: every node it asks for here is handed back from the pool's last draw where it can be, and when the code returns each box's children are set from what the code put in them. |
| 221 | 19 | `static<T> T scope(Object key, Supplier<T> code)` | Runs part of a page in a scope of its own, keyed by what it shows - a card's name, a row's sector or building - so its nodes are handed back to it and to nothing else. |
| 242 | 6 | `static void scope(Object key, Runnable code)` | The same, for code that makes nothing to return. |
| 254 | 21 | `static<T extends Node> T of(Class<T> type, Supplier<? extends T> fresh)` | The next node of this class the current scope made last time, or a new one: the one way a kept page's code makes a node. |
| 280 | 4 | `static boolean isNew(Node n)` | Whether this node was made new by the draw on now (or no draw is on): a listener or an event filter added only when this is true is added once in the node's life. |
| 286 | 19 | `private static void handBack(Draw d, Node n)` | A node handed back, made what a new one is in what code gives a node only sometimes. |
| 312 | 5 | `private static<T> void resetIfCodeSet(Property<? extends T> p, T initial)` | Back to a new node's value where it is not that already - but only a value code set: one an inline style set belongs to the style, outranks code, and would not come back until the style changed. |

### THE BOXES: their children are the draw's own list while a draw is on (lines 318-485)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 7 | **type** `interface Staging` | A box that stages its children while a kept draw is on and sets them when it ends. |
| 325 | 1 | `void stage()` _(in Kept.Staging)_ | A draw has handed this box out: its children are a list of the draw's own from now. |
| 328 | 1 | `void commit()` _(in Kept.Staging)_ | The draw has ended: the real children set from the staged list, in one call, where they differ. |
| 332 | 3 | `private static void settle(ObservableList<Node> real, List<? extends Node> want)` | The real list made equal to the staged one: untouched when it already is, else in one call. |
| 337 | 5 | `static boolean same(List<? extends Node> real, List<? extends Node> want)` | Whether a children list is already these nodes, in this order. |
| 344 | 3 | `static void set(ObservableList<Node> real, List<? extends Node> want)` | A pane's children made these nodes, in one call and only where they differ (the chrome's boxes are not Kept's own). |
| 349 | 19 | **type** `static final class Column extends VBox implements Staging` | A VBox, kept. |
| 352 | 1 | `Column()` _(in Kept.Column)_ |  |
| 354 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Column)_ |  |
| 358 | 3 | `public void stage()` _(in Kept.Column)_ |  |
| 362 | 5 | `public void commit()` _(in Kept.Column)_ |  |
| 370 | 19 | **type** `static final class Row extends HBox implements Staging` | An HBox, kept. |
| 373 | 1 | `Row()` _(in Kept.Row)_ |  |
| 375 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Row)_ |  |
| 379 | 3 | `public void stage()` _(in Kept.Row)_ |  |
| 383 | 5 | `public void commit()` _(in Kept.Row)_ |  |
| 391 | 19 | **type** `static final class Stack extends StackPane implements Staging` | A StackPane, kept. |
| 394 | 1 | `Stack()` _(in Kept.Stack)_ |  |
| 396 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Stack)_ |  |
| 400 | 3 | `public void stage()` _(in Kept.Stack)_ |  |
| 404 | 5 | `public void commit()` _(in Kept.Stack)_ |  |
| 412 | 19 | **type** `static final class Flow extends FlowPane implements Staging` | A FlowPane, kept. |
| 415 | 1 | `Flow()` _(in Kept.Flow)_ |  |
| 417 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Flow)_ |  |
| 421 | 3 | `public void stage()` _(in Kept.Flow)_ |  |
| 425 | 5 | `public void commit()` _(in Kept.Flow)_ |  |
| 433 | 19 | **type** `static final class Plain extends Pane implements Staging` | A plain Pane, kept: its children placed by the code (layoutX and layoutY), not by the pane. |
| 436 | 1 | `Plain()` _(in Kept.Plain)_ |  |
| 438 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Plain)_ |  |
| 442 | 3 | `public void stage()` _(in Kept.Plain)_ |  |
| 446 | 5 | `public void commit()` _(in Kept.Plain)_ |  |
| 457 | 23 | **type** `static final class Grid extends GridPane implements Staging` | A GridPane, kept. |
| 460 | 1 | `Grid()` _(in Kept.Grid)_ |  |
| 462 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Grid)_ |  |
| 466 | 7 | `public void stage()` _(in Kept.Grid)_ |  |
| 474 | 5 | `public void commit()` _(in Kept.Grid)_ |  |
| 482 | 3 | `static Group group()` | A Group, kept: Group's getChildren() is final, so a kept group's children are set through {@link #children}. |

### THE NODES, AS THE CODE ASKS FOR THEM (lines 486-570)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 5 | `static VBox column(double spacing)` | A VBox with this spacing. |
| 498 | 5 | `static VBox column(double spacing, Node...kids)` | A VBox with this spacing and these children. |
| 505 | 5 | `static HBox row(double spacing)` | An HBox with this spacing. |
| 512 | 5 | `static HBox row(double spacing, Node...kids)` | An HBox with this spacing and these children. |
| 519 | 5 | `static StackPane stack(Node...kids)` | A StackPane with these children. |
| 526 | 6 | `static FlowPane flow(double hgap, double vgap)` | A FlowPane with these gaps. |
| 534 | 3 | `static GridPane grid()` | A GridPane. |
| 539 | 5 | `static Pane plain(Node...kids)` | A plain Pane with these children. |
| 546 | 3 | `static Region region()` | A Region: a spacer, a bar, a swatch. |
| 551 | 6 | `static Label label(String text)` | A Label with this text and no graphic. |
| 559 | 6 | `static Label label(String text, Node graphic)` | A Label with this text and this graphic. |
| 567 | 3 | `static void children(Group g, Node...kids)` | A Group's children set where they differ, kept or not (Group's own list cannot be staged). |

### WHAT A NODE IS GIVEN THAT WOULD PILE UP ON A KEPT ONE (lines 571-617)

| line | len | member | says |
|---:|---:|---|---|
| 579 | 3 | `static Tooltip tipOf(Node n)` | The tooltip installed on a node, or null. |
| 588 | 14 | `static Tooltip tip(Node n, String text, double delayMillis)` | A tooltip on a node with this text and delay: the node's own, its text set, when it has one and a draw is on; else a new one installed, as Tooltip.install did. |
| 604 | 3 | `static Animation animationOf(Node n)` | The animation a node was last given through {@link #play}, or null. |
| 612 | 5 | `static void play(Node owner, Animation a)` | An animation on a node, played: the one this node was last given stopped first, so a month that lands while the last month's count-up still runs does not leave two counting one label. |

