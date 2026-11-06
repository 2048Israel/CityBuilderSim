# Kept.java - 678 lines · 66 methods · 4 constants · interface

`ham/citybuildersim/ui/Kept.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a
> screen's code made at its last draw, handed back to the same code, in the order it asks for
> them, at the next - so a month that lands sets its figures on the page on show instead of
> throwing the page away and building another.
> 
> <p>WHY. Before 0.8.26 every screen was drawn by emptying the page and building it afresh
> (UserInterface.clearMenu()), on every month the clock redraws, and every screen but People
> still is. A node new to the scene is
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
> Two more since KP2: a style a hover changes is chosen from {@code isHover()} where the draw sets
> it, since a pointer that has not moved stays over a kept node but sends no mouse-entered, and a
> style set back to rest at the draw would lose the hover look; and a control keeps the player's
> hand ({@link #held}, {@link #text}): a slider's value is set only where it differs and never
> while the player holds it, a field's text only where it differs and never while it has the focus.

**Used by (23):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CapChips](CapChips.md), [ConstructionScreen](ConstructionScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [KeptBench](KeptBench.md), [Ladder](Ladder.md), [LandScreen](LandScreen.md), [Levers](Levers.md), [MapTiles](MapTiles.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [Statement](Statement.md), [StatementView](StatementView.md), [SummaryScreen](SummaryScreen.md), [TimeChart](TimeChart.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 96 | THE POOL, THE DRAW AND THE SCOPES |
| 328 | THE BOXES: their children are the draw's own list while a draw is on |
| 496 | THE NODES, AS THE CODE ASKS FOR THEM |
| 581 | WHAT A NODE IS GIVEN THAT WOULD PILE UP ON A KEPT ONE |
| 630 | THE PLAYER'S HAND (KP2): a control on a kept page is not set under it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 586 | `Kept.TOOLTIP` | `"javafx.scene.control.Tooltip"` | The key Tooltip.install() keeps a node's tooltip under in its properties: what tipOf() reads. |
| 588 | `Kept.ANIMATION` | `"kept.animation"` | The key play() keeps a node's last animation under in its properties, to stop it before the next. |
| 635 | `Kept.HELD` | `"kept.held"` | The key grip() keeps a node's "held" flag under, and the marker that it has been gripped. |
| 636 | `Kept.GRIP` | `"kept.grip"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 102 | `private Scope root` |  |
| 104 | `private int draws` | Draws served since the pool last started afresh: 0 means the next draw makes every node new. |
| 106 | `private int lastMade, lastKept` | At the last draw: the nodes it made new, and the nodes it handed back (what KeptCheck counts). |
| 132 | `private Map<Class<?>, List<Node>> made` |  |
| 133 | `private Map<Class<?>, List<Node>> making` |  |
| 134 | `private final Map<Class<?>, Integer> taken` |  |
| 135 | `private Map<Object, Scope> inner` |  |
| 136 | `private Map<Object, Scope> innerMaking` |  |
| 154 | `final Draw outer` | The draw this one was begun inside, or null (UP2: a draw of another pool within a draw). |
| 155 | `final ArrayDeque<Scope> scopes` |  |
| 156 | `final List<Scope> begun` |  |
| 157 | `final List<Staging> staged` |  |
| 158 | `final List<Node> handedBack` |  |
| 159 | `final Set<Node> made` |  |
| 160 | `final Set<Node> tipped` |  |
| 174 | `private static Draw drawing` | The draw on now, or null: then every call here makes a new node. |
| 360 | `private ObservableList<Node> stage` |  |
| 381 | `private ObservableList<Node> stage` |  |
| 402 | `private ObservableList<Node> stage` |  |
| 423 | `private ObservableList<Node> stage` |  |
| 444 | `private ObservableList<Node> stage` |  |
| 468 | `private ObservableList<Node> stage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 587 | **type** `public final class Kept` | A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a screen's code made at its last draw, handed back to the same code, in the order it asks for them, at the next - so a month th... |
| 94 | 1 | `private Kept()` |  |

### THE POOL, THE DRAW AND THE SCOPES (lines 96-327)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 28 | **type** `static final class Pool` | One screen's kept nodes: what each of its scopes made at its last draw. |
| 109 | 3 | `int lastMade()` _(in Kept.Pool)_ | The nodes the last draw made new. |
| 114 | 3 | `int lastKept()` _(in Kept.Pool)_ | The nodes the last draw handed back from the one before. |
| 119 | 4 | `void forget()` _(in Kept.Pool)_ | Every node is made new at the next draw: another screen was shown, or another city opened. |
| 125 | 3 | `boolean holds()` _(in Kept.Pool)_ | Whether the next draw has kept nodes to hand back. |
| 131 | 19 | **type** `private static final class Scope` | One scope's nodes: by class, in the order the code asked for them; and its keyed inner scopes. |
| 138 | 5 | `void begin()` _(in Kept.Scope)_ |  |
| 145 | 4 | `void end()` _(in Kept.Scope)_ | What was not asked for this draw is let go: the next draw hands back only this one's. |
| 152 | 20 | **type** `private static final class Draw` | A draw in progress. |
| 162 | 3 | `Draw(Draw outer)` _(in Kept.Draw)_ |  |
| 167 | 4 | `boolean drawing(Pool pool)` _(in Kept.Draw)_ | Whether this draw, or any it was begun inside, is drawing this pool. |
| 177 | 3 | `static boolean drawing()` | Whether a kept draw is on. |
| 193 | 32 | `static void draw(Pool pool, Runnable code)` | Runs a page's code as a kept draw from the pool: every node it asks for here is handed back from the pool's last draw where it can be, and when the code returns each box's children are set from what the code put in them. |
| 231 | 19 | `static<T> T scope(Object key, Supplier<T> code)` | Runs part of a page in a scope of its own, keyed by what it shows - a card's name, a row's sector or building - so its nodes are handed back to it and to nothing else. |
| 252 | 6 | `static void scope(Object key, Runnable code)` | The same, for code that makes nothing to return. |
| 264 | 21 | `static<T extends Node> T of(Class<T> type, Supplier<? extends T> fresh)` | The next node of this class the current scope made last time, or a new one: the one way a kept page's code makes a node. |
| 290 | 4 | `static boolean isNew(Node n)` | Whether this node was made new by the draw on now (or no draw is on): a listener or an event filter added only when this is true is added once in the node's life. |
| 296 | 19 | `private static void handBack(Draw d, Node n)` | A node handed back, made what a new one is in what code gives a node only sometimes. |
| 322 | 5 | `private static<T> void resetIfCodeSet(Property<? extends T> p, T initial)` | Back to a new node's value where it is not that already - but only a value code set: one an inline style set belongs to the style, outranks code, and would not come back until the style changed. |

### THE BOXES: their children are the draw's own list while a draw is on (lines 328-495)

| line | len | member | says |
|---:|---:|---|---|
| 333 | 7 | **type** `interface Staging` | A box that stages its children while a kept draw is on and sets them when it ends. |
| 335 | 1 | `void stage()` _(in Kept.Staging)_ | A draw has handed this box out: its children are a list of the draw's own from now. |
| 338 | 1 | `void commit()` _(in Kept.Staging)_ | The draw has ended: the real children set from the staged list, in one call, where they differ. |
| 342 | 3 | `private static void settle(ObservableList<Node> real, List<? extends Node> want)` | The real list made equal to the staged one: untouched when it already is, else in one call. |
| 347 | 5 | `static boolean same(List<? extends Node> real, List<? extends Node> want)` | Whether a children list is already these nodes, in this order. |
| 354 | 3 | `static void set(ObservableList<Node> real, List<? extends Node> want)` | A pane's children made these nodes, in one call and only where they differ (the chrome's boxes are not Kept's own). |
| 359 | 19 | **type** `static final class Column extends VBox implements Staging` | A VBox, kept. |
| 362 | 1 | `Column()` _(in Kept.Column)_ |  |
| 364 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Column)_ |  |
| 368 | 3 | `public void stage()` _(in Kept.Column)_ |  |
| 372 | 5 | `public void commit()` _(in Kept.Column)_ |  |
| 380 | 19 | **type** `static final class Row extends HBox implements Staging` | An HBox, kept. |
| 383 | 1 | `Row()` _(in Kept.Row)_ |  |
| 385 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Row)_ |  |
| 389 | 3 | `public void stage()` _(in Kept.Row)_ |  |
| 393 | 5 | `public void commit()` _(in Kept.Row)_ |  |
| 401 | 19 | **type** `static final class Stack extends StackPane implements Staging` | A StackPane, kept. |
| 404 | 1 | `Stack()` _(in Kept.Stack)_ |  |
| 406 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Stack)_ |  |
| 410 | 3 | `public void stage()` _(in Kept.Stack)_ |  |
| 414 | 5 | `public void commit()` _(in Kept.Stack)_ |  |
| 422 | 19 | **type** `static final class Flow extends FlowPane implements Staging` | A FlowPane, kept. |
| 425 | 1 | `Flow()` _(in Kept.Flow)_ |  |
| 427 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Flow)_ |  |
| 431 | 3 | `public void stage()` _(in Kept.Flow)_ |  |
| 435 | 5 | `public void commit()` _(in Kept.Flow)_ |  |
| 443 | 19 | **type** `static final class Plain extends Pane implements Staging` | A plain Pane, kept: its children placed by the code (layoutX and layoutY), not by the pane. |
| 446 | 1 | `Plain()` _(in Kept.Plain)_ |  |
| 448 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Plain)_ |  |
| 452 | 3 | `public void stage()` _(in Kept.Plain)_ |  |
| 456 | 5 | `public void commit()` _(in Kept.Plain)_ |  |
| 467 | 23 | **type** `static final class Grid extends GridPane implements Staging` | A GridPane, kept. |
| 470 | 1 | `Grid()` _(in Kept.Grid)_ |  |
| 472 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Grid)_ |  |
| 476 | 7 | `public void stage()` _(in Kept.Grid)_ |  |
| 484 | 5 | `public void commit()` _(in Kept.Grid)_ |  |
| 492 | 3 | `static Group group()` | A Group, kept: Group's getChildren() is final, so a kept group's children are set through {@link #children}. |

### THE NODES, AS THE CODE ASKS FOR THEM (lines 496-580)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 5 | `static VBox column(double spacing)` | A VBox with this spacing. |
| 508 | 5 | `static VBox column(double spacing, Node...kids)` | A VBox with this spacing and these children. |
| 515 | 5 | `static HBox row(double spacing)` | An HBox with this spacing. |
| 522 | 5 | `static HBox row(double spacing, Node...kids)` | An HBox with this spacing and these children. |
| 529 | 5 | `static StackPane stack(Node...kids)` | A StackPane with these children. |
| 536 | 6 | `static FlowPane flow(double hgap, double vgap)` | A FlowPane with these gaps. |
| 544 | 3 | `static GridPane grid()` | A GridPane. |
| 549 | 5 | `static Pane plain(Node...kids)` | A plain Pane with these children. |
| 556 | 3 | `static Region region()` | A Region: a spacer, a bar, a swatch. |
| 561 | 6 | `static Label label(String text)` | A Label with this text and no graphic. |
| 569 | 6 | `static Label label(String text, Node graphic)` | A Label with this text and this graphic. |
| 577 | 3 | `static void children(Group g, Node...kids)` | A Group's children set where they differ, kept or not (Group's own list cannot be staged). |

### WHAT A NODE IS GIVEN THAT WOULD PILE UP ON A KEPT ONE (lines 581-629)

| line | len | member | says |
|---:|---:|---|---|
| 591 | 3 | `static Tooltip tipOf(Node n)` | The tooltip installed on a node, or null. |
| 600 | 14 | `static Tooltip tip(Node n, String text, double delayMillis)` | A tooltip on a node with this text and delay: the node's own, its text set, when it has one and a draw is on; else a new one installed, as Tooltip.install did. |
| 616 | 3 | `static Animation animationOf(Node n)` | The animation a node was last given through {@link #play}, or null. |
| 624 | 5 | `static void play(Node owner, Animation a)` | An animation on a node, played: the one this node was last given stopped first, so a month that lands while the last month's count-up still runs does not leave two counting one label. |

### THE PLAYER'S HAND (KP2): a control on a kept page is not set under it (lines 630-678)

| line | len | member | says |
|---:|---:|---|---|
| 645 | 13 | `static void grip(Node n)` | Makes {@link #held} true for this node between a primary-button press on it and the release that ends the press. |
| 664 | 4 | `static boolean held(Node n)` | Whether the player holds this node now (see {@link #grip}); a slider also counts as held while its value is changing. |
| 673 | 5 | `static void text(TextInputControl f, String text)` | A field's text set only where it differs and never while the field has the focus: a set marks the field edited and moves its caret, and the player typing owns it. |

