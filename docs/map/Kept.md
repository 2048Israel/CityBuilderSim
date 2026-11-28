# Kept.java - 913 lines · 91 methods · 5 constants · interface

`ham/citybuildersim/ui/Kept.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a
> screen's code made at its last draw, handed back to the same code, in the order it asks for
> them, at the next - so a month that lands sets its figures on the page on show instead of
> throwing the page away and building another.
> 
> <p>WHY. Before 0.8.26 every screen was drawn by emptying the page and building it afresh
> (UserInterface.clearMenu()), on every month the clock redraws; since KP7 every rail screen is
> kept, and only the menu's own screens are still built that way. A node new to the scene is
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
> does not shift every label below it onto a neighbour's. A row the model writes in some months and
> not in others (a tax's slice of a ring, a step of a bridge) is a {@link #parkedScope}: its nodes
> wait, off the page, for the month it comes back, and are handed back as any kept node is.</li>
> <li>A node handed back loses, at the draw's end, what the code did not give it this time - its click and hover
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
> A draw gives optional state through {@link #give}, and a grid's constraints through {@link #columns}
> and {@link #rows}: reset at the draw's end only what that draw did not set. An unchanged assignment
> still counts as given, so a greyed control and an unchanged grid never reset and set back.

**Used by (33):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CapChips](CapChips.md), [CentralBankScreen](CentralBankScreen.md), [ConstructionScreen](ConstructionScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [HouseholdDebtPanel](HouseholdDebtPanel.md), [InfrastructureScreen](InfrastructureScreen.md), [KeptBench](KeptBench.md), [Ladder](Ladder.md), [LandScreen](LandScreen.md), [Levers](Levers.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PartnersPage](PartnersPage.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [RankingsScreen](RankingsScreen.md), [ResearchScreen](ResearchScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [Statement](Statement.md), [StatementView](StatementView.md), [SummaryScreen](SummaryScreen.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [VaultTable](VaultTable.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 107 | THE POOL, THE DRAW AND THE SCOPES |
| 428 | THE BOXES: their children are the draw's own list while a draw is on |
| 693 | THE NODES, AS THE CODE ASKS FOR THEM |
| 800 | WHAT A NODE IS GIVEN THAT WOULD PILE UP ON A KEPT ONE |
| 849 | THE PLAYER'S HAND (KP2): a control on a kept page is not set under it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 183 | `Kept.PARKED_MAX` | `48` | The most scopes a scope parks (parkedScope()): rows are keyed by what they say, and a few dozen is every row a page has had in a year. |
| 805 | `Kept.TOOLTIP` | `"javafx.scene.control.Tooltip"` | The key Tooltip.install() keeps a node's tooltip under in its properties: what tipOf() reads. |
| 807 | `Kept.ANIMATION` | `"kept.animation"` | The key play() keeps a node's last animation under in its properties, to stop it before the next. |
| 854 | `Kept.HELD` | `"kept.held"` | The key grip() keeps a node's "held" flag under, and the marker that it has been gripped. |
| 855 | `Kept.GRIP` | `"kept.grip"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 113 | `private Scope root` |  |
| 115 | `private int draws` | Draws served since the pool last started afresh: 0 means the next draw makes every node new. |
| 117 | `private int lastMade, lastKept` | At the last draw: the nodes it made new, and the nodes it handed back (what KeptCheck counts). |
| 143 | `private Map<Class<?>, List<Node>> made` |  |
| 144 | `private Map<Class<?>, List<Node>> making` |  |
| 145 | `private final Map<Class<?>, Integer> taken` |  |
| 146 | `private Map<Object, Scope> inner` |  |
| 147 | `private Map<Object, Scope> innerMaking` |  |
| 152 | `private final java.util.LinkedHashMap<Object, Scope> parkedKeys` | The inner scopes parkedScope() was asked for, the least recently asked first (an access-ordered map: asking again moves a key to the end), so a scope that has more than PARKED_MAX lets go the eldest and never an arbit... |
| 188 | `final Draw outer` | The draw this one was begun inside, or null (UP2: a draw of another pool within a draw). |
| 189 | `final ArrayDeque<Scope> scopes` |  |
| 190 | `final List<Scope> begun` |  |
| 191 | `final List<Staging> staged` |  |
| 192 | `final List<Node> handedBack` |  |
| 193 | `final Set<Node> made` |  |
| 194 | `final Set<Node> tipped` |  |
| 195 | `final Set<Property<?>> given` |  |
| 196 | `final Map<Node, Scene> detached` |  |
| 210 | `private static Draw drawing` | The draw on now, or null: then every call here makes a new node. |
| 212 | `private static Draw settling` | The draw whose staged lists are committing, when constructors must behave outside a draw. |
| 215 | `static Consumer<Node> layoutObserver` | Bench observation of JavaFX layout requests, null during ordinary play (FX thread only). |
| 217 | `static Consumer<Node> sizeObserver` | The preferred-size convenience setter bypasses Parent.requestLayout; observe its calls directly. |
| 460 | `private ObservableList<Node> stage` |  |
| 491 | `private ObservableList<Node> stage` |  |
| 522 | `private ObservableList<Node> stage` |  |
| 553 | `private ObservableList<Node> stage` |  |
| 584 | `private ObservableList<Node> stage` |  |
| 618 | `private ObservableList<Node> stage` |  |
| 619 | `private ObservableList<ColumnConstraints> columns` |  |
| 620 | `private ObservableList<RowConstraints> rows` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 103 | 811 | **type** `public final class Kept` | A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a screen's code made at its last draw, handed back to the same code, in the order it asks for them, at the next - so a month th... |
| 105 | 1 | `private Kept()` |  |

### THE POOL, THE DRAW AND THE SCOPES (lines 107-427)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 28 | **type** `static final class Pool` | One screen's kept nodes: what each of its scopes made at its last draw. |
| 120 | 3 | `int lastMade()` _(in Kept.Pool)_ | The nodes the last draw made new. |
| 125 | 3 | `int lastKept()` _(in Kept.Pool)_ | The nodes the last draw handed back from the one before. |
| 130 | 4 | `void forget()` _(in Kept.Pool)_ | Every node is made new at the next draw: another screen was shown, or another city opened. |
| 136 | 3 | `boolean holds()` _(in Kept.Pool)_ | Whether the next draw has kept nodes to hand back. |
| 142 | 39 | **type** `private static final class Scope` | One scope's nodes: by class, in the order the code asked for them; and its keyed inner scopes. |
| 154 | 5 | `void begin()` _(in Kept.Scope)_ |  |
| 166 | 14 | `void end()` _(in Kept.Scope)_ | What was not asked for this draw is let go: the next draw hands back only this one's - except the parked scopes (FB2, KS1-Gov), which stay as their last draw left them, up to PARKED_MAX of them in a scope, for the dra... |
| 186 | 22 | **type** `private static final class Draw` | A draw in progress. |
| 198 | 3 | `Draw(Draw outer)` _(in Kept.Draw)_ |  |
| 203 | 4 | `boolean drawing(Pool pool)` _(in Kept.Draw)_ | Whether this draw, or any it was begun inside, is drawing this pool. |
| 220 | 3 | `static boolean drawing()` | Whether a kept draw is on. |
| 236 | 39 | `static void draw(Pool pool, Runnable code)` | Runs a page's code as a kept draw from the pool: every node it asks for here is handed back from the pool's last draw where it can be, and when the code returns each box's children are set from what the code put in them. |
| 281 | 19 | `static<T> T scope(Object key, Supplier<T> code)` | Runs part of a page in a scope of its own, keyed by what it shows - a card's name, a row's sector or building - so its nodes are handed back to it and to nothing else. |
| 310 | 20 | `static<T> T parkedScope(Object key, Supplier<T> code)` | A scope for a row that comes and goes (FB2, KS1-Gov): as scope(), but the row's nodes are kept for the draw that asks for it again, not let go at the first draw that does not. |
| 332 | 6 | `static void parkedScope(Object key, Runnable code)` | parkedScope() for code that makes nothing to return. |
| 340 | 6 | `static void scope(Object key, Runnable code)` | The same, for code that makes nothing to return. |
| 352 | 21 | `static<T extends Node> T of(Class<T> type, Supplier<? extends T> fresh)` | The next node of this class the current scope made last time, or a new one: the one way a kept page's code makes a node. |
| 378 | 4 | `static boolean isNew(Node n)` | Whether this node was made new by the draw on now (or no draw is on): a listener or an event filter added only when this is true is added once in the node's life. |
| 384 | 3 | `private static void handBack(Draw d, Node n)` | A node handed back: optional state is settled after the drawing code has had its turn. |
| 389 | 4 | `static<T> void give(Property<T> property, T value)` | An explicit optional value, including an unchanged one, belongs to this draw. |
| 394 | 3 | `private static<T> void omitted(Draw d, Property<T> p, T initial)` |  |
| 398 | 17 | `private static void resetOmitted(Draw d, Node n)` |  |
| 422 | 5 | `private static<T> void resetIfCodeSet(Property<? extends T> p, T initial)` | Back to a new node's value where it is not that already - but only a value code set: one an inline style set belongs to the style, outranks code, and would not come back until the style changed. |

### THE BOXES: their children are the draw's own list while a draw is on (lines 428-692)

| line | len | member | says |
|---:|---:|---|---|
| 433 | 7 | **type** `interface Staging` | A box that stages its children while a kept draw is on and sets them when it ends. |
| 435 | 1 | `void stage()` _(in Kept.Staging)_ | A draw has handed this box out: its children are a list of the draw's own from now. |
| 438 | 1 | `void commit()` _(in Kept.Staging)_ | The draw has ended: the real children set from the staged list, in one call, where they differ. |
| 442 | 3 | `private static void settle(ObservableList<Node> real, List<? extends Node> want)` | The real list made equal to the staged one: untouched when it already is, else in one call. |
| 447 | 5 | `static boolean same(List<? extends Node> real, List<? extends Node> want)` | Whether a children list is already these nodes, in this order. |
| 454 | 3 | `static void set(ObservableList<Node> real, List<? extends Node> want)` | A pane's children made these nodes, in one call and only where they differ (the chrome's boxes are not Kept's own). |
| 459 | 29 | **type** `static final class Column extends VBox implements Staging` | A VBox, kept. |
| 462 | 1 | `Column()` _(in Kept.Column)_ |  |
| 464 | 4 | `public void setPrefSize(double width, double height)` _(in Kept.Column)_ |  |
| 469 | 4 | `public void requestLayout()` _(in Kept.Column)_ |  |
| 474 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Column)_ |  |
| 478 | 3 | `public void stage()` _(in Kept.Column)_ |  |
| 482 | 5 | `public void commit()` _(in Kept.Column)_ |  |
| 490 | 29 | **type** `static final class Row extends HBox implements Staging` | An HBox, kept. |
| 493 | 1 | `Row()` _(in Kept.Row)_ |  |
| 495 | 4 | `public void setPrefSize(double width, double height)` _(in Kept.Row)_ |  |
| 500 | 4 | `public void requestLayout()` _(in Kept.Row)_ |  |
| 505 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Row)_ |  |
| 509 | 3 | `public void stage()` _(in Kept.Row)_ |  |
| 513 | 5 | `public void commit()` _(in Kept.Row)_ |  |
| 521 | 29 | **type** `static final class Stack extends StackPane implements Staging` | A StackPane, kept. |
| 524 | 1 | `Stack()` _(in Kept.Stack)_ |  |
| 526 | 4 | `public void setPrefSize(double width, double height)` _(in Kept.Stack)_ |  |
| 531 | 4 | `public void requestLayout()` _(in Kept.Stack)_ |  |
| 536 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Stack)_ |  |
| 540 | 3 | `public void stage()` _(in Kept.Stack)_ |  |
| 544 | 5 | `public void commit()` _(in Kept.Stack)_ |  |
| 552 | 29 | **type** `static final class Flow extends FlowPane implements Staging` | A FlowPane, kept. |
| 555 | 1 | `Flow()` _(in Kept.Flow)_ |  |
| 557 | 4 | `public void setPrefSize(double width, double height)` _(in Kept.Flow)_ |  |
| 562 | 4 | `public void requestLayout()` _(in Kept.Flow)_ |  |
| 567 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Flow)_ |  |
| 571 | 3 | `public void stage()` _(in Kept.Flow)_ |  |
| 575 | 5 | `public void commit()` _(in Kept.Flow)_ |  |
| 583 | 29 | **type** `static final class Plain extends Pane implements Staging` | A plain Pane, kept: its children placed by the code (layoutX and layoutY), not by the pane. |
| 586 | 1 | `Plain()` _(in Kept.Plain)_ |  |
| 588 | 4 | `public void setPrefSize(double width, double height)` _(in Kept.Plain)_ |  |
| 593 | 4 | `public void requestLayout()` _(in Kept.Plain)_ |  |
| 598 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Plain)_ |  |
| 602 | 3 | `public void stage()` _(in Kept.Plain)_ |  |
| 606 | 5 | `public void commit()` _(in Kept.Plain)_ |  |
| 617 | 37 | **type** `static final class Grid extends GridPane implements Staging` | A GridPane, kept: constraints stage with children. |
| 622 | 1 | `Grid()` _(in Kept.Grid)_ |  |
| 624 | 4 | `public void setPrefSize(double width, double height)` _(in Kept.Grid)_ |  |
| 629 | 4 | `public void requestLayout()` _(in Kept.Grid)_ |  |
| 634 | 3 | `public ObservableList<Node> getChildren()` _(in Kept.Grid)_ |  |
| 638 | 5 | `public void stage()` _(in Kept.Grid)_ |  |
| 644 | 9 | `public void commit()` _(in Kept.Grid)_ |  |
| 655 | 3 | `static ObservableList<ColumnConstraints> columns(GridPane grid)` |  |
| 659 | 3 | `static ObservableList<RowConstraints> rows(GridPane grid)` |  |
| 663 | 10 | `private static<T> void settleConstraints(ObservableList<T> actual, List<T> want, java.util.function.BiPredicate<T, T> same)` |  |
| 674 | 6 | `private static boolean sameColumn(ColumnConstraints a, ColumnConstraints b)` |  |
| 681 | 6 | `private static boolean sameRow(RowConstraints a, RowConstraints b)` |  |
| 689 | 3 | `static Group group()` | A Group, kept: Group's getChildren() is final, so a kept group's children are set through {@link #children}. |

### THE NODES, AS THE CODE ASKS FOR THEM (lines 693-799)

| line | len | member | says |
|---:|---:|---|---|
| 698 | 5 | `static VBox column(double spacing)` | A VBox with this spacing. |
| 705 | 5 | `static VBox column(double spacing, Node...kids)` | A VBox with this spacing and these children. |
| 712 | 5 | `static HBox row(double spacing)` | An HBox with this spacing. |
| 719 | 5 | `static HBox row(double spacing, Node...kids)` | An HBox with this spacing and these children. |
| 726 | 5 | `static StackPane stack(Node...kids)` | A StackPane with these children. |
| 733 | 6 | `static FlowPane flow(double hgap, double vgap)` | A FlowPane with these gaps. |
| 741 | 3 | `static GridPane grid()` | A GridPane. |
| 746 | 5 | `static Pane plain(Node...kids)` | A plain Pane with these children. |
| 753 | 3 | `static Region region()` | A Region: a spacer, a bar, a swatch. |
| 758 | 6 | **type** `static final class SizedRegion extends Region` | A plain Region with the same passive setter observation as the kept boxes. |
| 759 | 4 | `public void setPrefSize(double width, double height)` _(in Kept.SizedRegion)_ |  |
| 766 | 12 | `private static Label newLabel()` | A native Label whose skin text disappears with empty words, including raw CountUp updates. |
| 780 | 6 | `static Label label(String text)` | A Label with this text and no graphic. |
| 788 | 6 | `static Label label(String text, Node graphic)` | A Label with this text and this graphic. |
| 796 | 3 | `static void children(Group g, Node...kids)` | A Group's children set where they differ, kept or not (Group's own list cannot be staged). |

### WHAT A NODE IS GIVEN THAT WOULD PILE UP ON A KEPT ONE (lines 800-848)

| line | len | member | says |
|---:|---:|---|---|
| 810 | 3 | `static Tooltip tipOf(Node n)` | The tooltip installed on a node, or null. |
| 819 | 14 | `static Tooltip tip(Node n, String text, double delayMillis)` | A tooltip on a node with this text and delay: the node's own, its text set, when it has one and a draw is on; else a new one installed, as Tooltip.install did. |
| 835 | 3 | `static Animation animationOf(Node n)` | The animation a node was last given through {@link #play}, or null. |
| 843 | 5 | `static void play(Node owner, Animation a)` | An animation on a node, played: the one this node was last given stopped first, so a month that lands while the last month's count-up still runs does not leave two counting one label. |

### THE PLAYER'S HAND (KP2): a control on a kept page is not set under it (lines 849-913)

| line | len | member | says |
|---:|---:|---|---|
| 866 | 22 | `static void grip(Node n)` | Makes {@link #held} true for this node between a primary-button press on it and the release that ends the press. |
| 889 | 4 | `private static void letGo(Node n)` |  |
| 899 | 4 | `static boolean held(Node n)` | Whether the player holds this node now (see {@link #grip}); a slider also counts as held while its value is changing. |
| 908 | 5 | `static void text(TextInputControl f, String text)` | A field's text set only where it differs and never while the field has the focus: a set marks the field edited and moves its caret, and the player typing owns it. |

