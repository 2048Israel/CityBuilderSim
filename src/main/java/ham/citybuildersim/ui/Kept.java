package ham.citybuildersim.ui;

import javafx.animation.Animation;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.Consumer;

/**
 * A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a
 * screen's code made at its last draw, handed back to the same code, in the order it asks for
 * them, at the next - so a month that lands sets its figures on the page on show instead of
 * throwing the page away and building another.
 *
 * <p>WHY. Before 0.8.26 every screen was drawn by emptying the page and building it afresh
 * (UserInterface.clearMenu()), on every month the clock redraws; since KP7 every rail screen is
 * kept, and only the menu's own screens are still built that way. A node new to the scene is
 * styled from nothing - its skin made, every CSS property looked up - then laid out with its
 * text measured, then given a peer for the renderer; a node kept in place pays none of that,
 * and a label whose text is unchanged is not even laid out again. At month 4,000 of the
 * playtest's city a redraw of People cost 57 ms on the FX thread, of which 36 ms were the new
 * nodes' CSS, layout and sync and most of the rest making them (the study's probe). The way
 * out the brief named - each screen written twice, once to build and once to update - would
 * have been two code paths for every figure, free to drift apart. Here there is one: the
 * screen's own drawing code runs every time, and only where its nodes come from changes.
 *
 * <p>HOW. A screen draws inside {@link #draw(Pool, Runnable)}. Every node its code makes comes
 * through here - {@code Kept.label("12,576")}, {@code Kept.column(8)}, {@code Kept.of(Mirror.class,
 * Mirror::new)} - and inside a draw each call hands back the next node of that class the same
 * scope made last time, or a new one if it made fewer. Outside a draw every call is a new node,
 * exactly as {@code new} was, so a helper shared with screens not yet kept is unchanged for them.
 * <ul>
 * <li>The boxes are this class's own (Column, Row, Stack, Grid, Flow, Plain). While a draw is
 * on, a box's getChildren() is a list of the draw's own; when the draw ends each box's real
 * children are set from it in ONE call, and only where it differs. A node left in place is never
 * taken off the scene, and that is what keeps its style: cleared and added back in two steps
 * the same nodes cost People 20 ms (the study's ReaddProbe).</li>
 * <li>Rows that come and go are a scope each, keyed by what they show
 * ({@code Kept.scope(sector, () -> row(sector))}): a row that goes takes its own nodes with it,
 * and the rows after it keep theirs. A card is a scope too, so a chip that appears in one card
 * does not shift every label below it onto a neighbour's. A row the model writes in some months and
 * not in others (a tax's slice of a ring, a step of a bridge) is a {@link #parkedScope}: its nodes
 * wait, off the page, for the month it comes back, and are handed back as any kept node is.</li>
 * <li>A node handed back loses, at the draw's end, what the code did not give it this time - its click and hover
 * handlers, its action, its cursor, visibility, opacity, disabling and tooltip - where the code
 * set them (a value an inline style set is the style's, and left alone); so a row clickable last
 * month and not this one does not keep last month's click.</li>
 * <li>A picture of the game's own (Mirror, SegmentBar...) is kept with its children and told its
 * new figures through a method of its own; its constructor makes its children with new, never
 * through here, since it runs once in the picture's life.</li>
 * </ul>
 *
 * <p>THE RULES for code drawn kept, which KeptCheck holds by comparing the kept page with a page
 * built new, month after month: a node is made through here, never with new; a value set on a
 * node under a condition is set on both sides of it; a list on a node (points, path elements,
 * column constraints, style classes) is set, never added to; a listener or event handler added
 * to a node is added once ({@link #isNew}) or through {@link #play}/{@link #tip}; and the page
 * reaches the window through UserInterface.setPage(), not rootMenu.getChildren().add().
 * Two more since KP2: a style a hover changes is chosen from {@code isHover()} where the draw sets
 * it, since a pointer that has not moved stays over a kept node but sends no mouse-entered, and a
 * style set back to rest at the draw would lose the hover look; and a control keeps the player's
 * hand ({@link #held}, {@link #text}): a slider's value is set only where it differs and never
 * while the player holds it, a field's text only where it differs and never while it has the focus.
 * A draw gives optional state through {@link #give}, and a grid's constraints through {@link #columns}
 * and {@link #rows}: reset at the draw's end only what that draw did not set. An unchanged assignment
 * still counts as given, so a greyed control and an unchanged grid never reset and set back.
 */
public final class Kept {

    private Kept() { }

    /* =====================================================================
       THE POOL, THE DRAW AND THE SCOPES
       ===================================================================== */

    /** One screen's kept nodes: what each of its scopes made at its last draw. A kept screen holds one. */
    static final class Pool {
        private Scope root = new Scope();
        /** Draws served since the pool last started afresh: 0 means the next draw makes every node new. */
        private int draws;
        /** At the last draw: the nodes it made new, and the nodes it handed back (what KeptCheck counts). */
        private int lastMade, lastKept;

        /** The nodes the last draw made new. */
        int lastMade() {
            return lastMade;
        }

        /** The nodes the last draw handed back from the one before. */
        int lastKept() {
            return lastKept;
        }

        /** Every node is made new at the next draw: another screen was shown, or another city opened. */
        void forget() {
            root = new Scope();
            draws = 0;
        }

        /** Whether the next draw has kept nodes to hand back. */
        boolean holds() {
            return draws > 0;
        }
    }

    /** One scope's nodes: by class, in the order the code asked for them; and its keyed inner scopes. */
    private static final class Scope {
        private Map<Class<?>, List<Node>> made = new HashMap<>();
        private Map<Class<?>, List<Node>> making = new HashMap<>();
        private final Map<Class<?>, Integer> taken = new HashMap<>();
        private Map<Object, Scope> inner = new HashMap<>();
        private Map<Object, Scope> innerMaking = new HashMap<>();
        /** Whether this scope's nodes outlive a draw that does not ask for it (parkedScope()). */
        private boolean parked;

        void begin() {
            making = new HashMap<>();
            innerMaking = new HashMap<>();
            taken.clear();
        }

        /**
         * What was not asked for this draw is let go: the next draw hands back only this one's - except the parked scopes (FB2, KS1-Gov),
         * which stay as their last draw left them, up to PARKED_MAX of them in a scope, for the draw that asks for them again.
         */
        void end() {
            made = making;
            int kept = 0;
            for (Map.Entry<Object, Scope> e : inner.entrySet()) {
                if (e.getValue().parked && !innerMaking.containsKey(e.getKey()) && kept++ < PARKED_MAX) innerMaking.put(e.getKey(), e.getValue());
            }
            inner = innerMaking;
        }
    }

    /** The most scopes a scope parks (parkedScope()): rows are keyed by what they say, and a few dozen is every row a page has had in a year. */
    static final int PARKED_MAX = 48;

    /** A draw in progress. */
    private static final class Draw {
        /** The draw this one was begun inside, or null (UP2: a draw of another pool within a draw). */
        final Draw outer;
        final ArrayDeque<Scope> scopes = new ArrayDeque<>();
        final List<Scope> begun = new ArrayList<>();
        final List<Staging> staged = new ArrayList<>();
        final List<Node> handedBack = new ArrayList<>();
        final Set<Node> made = Collections.newSetFromMap(new IdentityHashMap<>());
        final Set<Node> tipped = Collections.newSetFromMap(new IdentityHashMap<>());
        final Set<Property<?>> given = Collections.newSetFromMap(new IdentityHashMap<>());
        final Map<Node, Scene> detached = new IdentityHashMap<>();

        Draw(Draw outer) {
            this.outer = outer;
        }

        /** Whether this draw, or any it was begun inside, is drawing this pool. */
        boolean drawing(Pool pool) {
            for (Draw d = this; d != null; d = d.outer) if (d.begun.contains(pool.root)) return true;
            return false;
        }
    }

    /** The draw on now, or null: then every call here makes a new node. Only ever touched on the FX thread. */
    private static Draw drawing;
    /** The draw whose staged lists are committing, when constructors must behave outside a draw. */
    private static Draw settling;

    /** Bench observation of JavaFX layout requests, null during ordinary play (FX thread only). */
    static Consumer<Node> layoutObserver;
    /** The preferred-size convenience setter bypasses Parent.requestLayout; observe its calls directly. */
    static Consumer<Node> sizeObserver;

    /** Whether a kept draw is on. */
    static boolean drawing() {
        return drawing != null;
    }

    /**
     * Runs a page's code as a kept draw from the pool: every node it asks for here is handed back
     * from the pool's last draw where it can be, and when the code returns each box's children are
     * set from what the code put in them.
     *
     * <p>A draw may be called while another is on (UP2: the header's draw reaches the envelope's,
     * which is also drawn on its own when a notice is read; a page's code may reach a refresh of the
     * chrome). A draw of ANOTHER pool inside it saves the outer draw, runs and commits as a draw of
     * its own, and restores the outer, so neither takes the other's nodes: the inner code asks its
     * own pool, and the outer's scopes are as they were. A draw of the SAME pool inside its own is
     * part of the outer draw, as it was (a pool is begun once).
     */
    static void draw(Pool pool, Runnable code) {
        Draw outer = drawing;
        if (outer != null && outer.drawing(pool)) {
            code.run();
            return;
        }
        Draw d = new Draw(outer);
        drawing = d;
        pool.root.begin();
        d.begun.add(pool.root);
        d.scopes.push(pool.root);
        try {
            code.run();
        } finally {
            // Nothing is being drawn while the boxes are settled: what a children change calls is not this draw's.
            drawing = null;
            Draw previousSettling = settling;
            settling = d;
            try {
                for (Scope s : d.begun) s.end();
                for (Node n : d.handedBack) resetOmitted(d, n);
                for (Staging s : d.staged) s.commit();
                for (Map.Entry<Node, Scene> e : d.detached.entrySet()) {
                    if (e.getKey().getScene() != e.getValue()) letGo(e.getKey());
                }
                // A tooltip the code gave a node last time and not this time goes.
                for (Node n : d.handedBack) {
                    Tooltip t = tipOf(n);
                    if (t != null && !d.tipped.contains(n)) Tooltip.uninstall(n, t);
                }
                pool.draws++;
                pool.lastMade = d.made.size();
                pool.lastKept = d.handedBack.size();
            } finally {
                settling = previousSettling;
                drawing = outer;
            }
        }
    }

    /**
     * Runs part of a page in a scope of its own, keyed by what it shows - a card's name, a row's
     * sector or building - so its nodes are handed back to it and to nothing else. A key used twice
     * in one draw under the same scope carries on where the first left off.
     */
    static <T> T scope(Object key, Supplier<T> code) {
        Draw d = drawing;
        if (d == null) return code.get();
        Scope parent = d.scopes.peek();
        Scope s = parent.innerMaking.get(key);
        if (s == null) {
            s = parent.inner.get(key);
            if (s == null) s = new Scope();
            s.begin();
            d.begun.add(s);
            parent.innerMaking.put(key, s);
        }
        d.scopes.push(s);
        try {
            return code.get();
        } finally {
            d.scopes.pop();
        }
    }

    /**
     * A scope for a row that comes and goes (FB2, KS1-Gov): as scope(), but the row's nodes are kept for the draw that asks for it again, not
     * let go at the first draw that does not. WHY: a line the model writes in some months and not in others (a tax's slice of the ring, a
     * step of the bridge, the arrears' lines) was made new at every return, and the Government Overview's busiest month made 45 nodes of 400
     * (47 of 378, 40 of 384, 46 of 400 on other cities' paths) against the tenth KeptCheck allows. A parked row is handed back as any kept node
     * is, set again where the draw sets it and cleared where it does not, so it equals a row built new; it is off the page while it is absent
     * (the box's children are the draw's own). A row's first appearance still makes its nodes, once; at most PARKED_MAX rows are parked in a
     * scope, the rest let go as before.
     */
    static <T> T parkedScope(Object key, Supplier<T> code) {
        Draw d = drawing;
        if (d == null) return code.get();
        Scope parent = d.scopes.peek();
        Scope s = parent.innerMaking.get(key);
        if (s == null) {
            s = parent.inner.get(key);
            if (s == null) s = new Scope();
            s.parked = true;
            s.begin();
            d.begun.add(s);
            parent.innerMaking.put(key, s);
        }
        d.scopes.push(s);
        try {
            return code.get();
        } finally {
            d.scopes.pop();
        }
    }

    /** parkedScope() for code that makes nothing to return. */
    static void parkedScope(Object key, Runnable code) {
        parkedScope(key, () -> {
            code.run();
            return null;
        });
    }

    /** The same, for code that makes nothing to return. */
    static void scope(Object key, Runnable code) {
        scope(key, () -> {
            code.run();
            return null;
        });
    }

    /**
     * The next node of this class the current scope made last time, or a new one: the one way a
     * kept page's code makes a node. Outside a draw, always {@code fresh.get()}.
     */
    @SuppressWarnings("unchecked")
    static <T extends Node> T of(Class<T> type, Supplier<? extends T> fresh) {
        Draw d = drawing;
        if (d == null) return fresh.get();
        Scope s = d.scopes.peek();
        int i = s.taken.merge(type, 1, Integer::sum) - 1;
        List<Node> was = s.made.get(type);
        T n;
        if (was != null && i < was.size()) {
            n = (T) was.get(i);
            handBack(d, n);
        } else {
            n = fresh.get();
            d.made.add(n);
        }
        s.making.computeIfAbsent(type, k -> new ArrayList<>()).add(n);
        if (n instanceof Staging st) {
            st.stage();
            d.staged.add(st);
        }
        return n;
    }

    /**
     * Whether this node was made new by the draw on now (or no draw is on): a listener or an event
     * filter added only when this is true is added once in the node's life.
     */
    static boolean isNew(Node n) {
        Draw d = drawing;
        return d == null || d.made.contains(n);
    }

    /** A node handed back: optional state is settled after the drawing code has had its turn. */
    private static void handBack(Draw d, Node n) {
        d.handedBack.add(n);
    }

    /** An explicit optional value, including an unchanged one, belongs to this draw. */
    static <T> void give(Property<T> property, T value) {
        if (drawing != null) drawing.given.add(property);
        property.setValue(value);
    }

    private static <T> void omitted(Draw d, Property<T> p, T initial) {
        if (!d.given.contains(p)) resetIfCodeSet(p, initial);
    }

    private static void resetOmitted(Draw d, Node n) {
        omitted(d, n.onMouseClickedProperty(), null);
        omitted(d, n.onMousePressedProperty(), null);
        omitted(d, n.onMouseReleasedProperty(), null);
        omitted(d, n.onMouseEnteredProperty(), null);
        omitted(d, n.onMouseExitedProperty(), null);
        omitted(d, n.onMouseMovedProperty(), null);
        omitted(d, n.onMouseDraggedProperty(), null);
        omitted(d, n.onScrollProperty(), null);
        if (n instanceof ButtonBase b) omitted(d, b.onActionProperty(), null);
        omitted(d, n.managedProperty(), true);
        omitted(d, n.disableProperty(), false);
        omitted(d, n.mouseTransparentProperty(), false);
        omitted(d, n.cursorProperty(), null);
        omitted(d, n.visibleProperty(), true);
        omitted(d, n.opacityProperty(), 1.0);
    }

    /**
     * Back to a new node's value where it is not that already - but only a value code set: one an
     * inline style set belongs to the style, outranks code, and would not come back until the style
     * changed.
     */
    @SuppressWarnings("unchecked")
    private static <T> void resetIfCodeSet(Property<? extends T> p, T initial) {
        if (Objects.equals(p.getValue(), initial)) return;
        if (p instanceof StyleableProperty<?> sp && sp.getStyleOrigin() != StyleOrigin.USER) return;
        ((Property<T>) p).setValue(initial);
    }

    /* =====================================================================
       THE BOXES: their children are the draw's own list while a draw is on
       ===================================================================== */

    /** A box that stages its children while a kept draw is on and sets them when it ends. */
    interface Staging {
        /** A draw has handed this box out: its children are a list of the draw's own from now. */
        void stage();

        /** The draw has ended: the real children set from the staged list, in one call, where they differ. */
        void commit();
    }

    /** The real list made equal to the staged one: untouched when it already is, else in one call. */
    private static void settle(ObservableList<Node> real, List<? extends Node> want) {
        if (!same(real, want)) real.setAll(want);
    }

    /** Whether a children list is already these nodes, in this order. */
    static boolean same(List<? extends Node> real, List<? extends Node> want) {
        if (real.size() != want.size()) return false;
        for (int i = 0; i < want.size(); i++) if (real.get(i) != want.get(i)) return false;
        return true;
    }

    /** A pane's children made these nodes, in one call and only where they differ (the chrome's boxes are not Kept's own). */
    static void set(ObservableList<Node> real, List<? extends Node> want) {
        settle(real, want);
    }

    /** A VBox, kept. */
    static final class Column extends VBox implements Staging {
        private ObservableList<Node> stage;

        Column() { }

        @Override public void setPrefSize(double width, double height) {
            if (sizeObserver != null) sizeObserver.accept(this);
            super.setPrefSize(width, height);
        }

        @Override public void requestLayout() {
            if (layoutObserver != null) layoutObserver.accept(this);
            super.requestLayout();
        }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            if (want != null) settle(super.getChildren(), want);
        }
    }

    /** An HBox, kept. */
    static final class Row extends HBox implements Staging {
        private ObservableList<Node> stage;

        Row() { }

        @Override public void setPrefSize(double width, double height) {
            if (sizeObserver != null) sizeObserver.accept(this);
            super.setPrefSize(width, height);
        }

        @Override public void requestLayout() {
            if (layoutObserver != null) layoutObserver.accept(this);
            super.requestLayout();
        }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            if (want != null) settle(super.getChildren(), want);
        }
    }

    /** A StackPane, kept. */
    static final class Stack extends StackPane implements Staging {
        private ObservableList<Node> stage;

        Stack() { }

        @Override public void setPrefSize(double width, double height) {
            if (sizeObserver != null) sizeObserver.accept(this);
            super.setPrefSize(width, height);
        }

        @Override public void requestLayout() {
            if (layoutObserver != null) layoutObserver.accept(this);
            super.requestLayout();
        }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            if (want != null) settle(super.getChildren(), want);
        }
    }

    /** A FlowPane, kept. */
    static final class Flow extends FlowPane implements Staging {
        private ObservableList<Node> stage;

        Flow() { }

        @Override public void setPrefSize(double width, double height) {
            if (sizeObserver != null) sizeObserver.accept(this);
            super.setPrefSize(width, height);
        }

        @Override public void requestLayout() {
            if (layoutObserver != null) layoutObserver.accept(this);
            super.requestLayout();
        }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            if (want != null) settle(super.getChildren(), want);
        }
    }

    /** A plain Pane, kept: its children placed by the code (layoutX and layoutY), not by the pane. */
    static final class Plain extends Pane implements Staging {
        private ObservableList<Node> stage;

        Plain() { }

        @Override public void setPrefSize(double width, double height) {
            if (sizeObserver != null) sizeObserver.accept(this);
            super.setPrefSize(width, height);
        }

        @Override public void requestLayout() {
            if (layoutObserver != null) layoutObserver.accept(this);
            super.requestLayout();
        }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            if (want != null) settle(super.getChildren(), want);
        }
    }

    /**
     * A GridPane, kept: constraints stage with children. Equal specifications retain the grid's own
     * constraints; omitted constraints are cleared only when this draw commits.
     */
    static final class Grid extends GridPane implements Staging {
        private ObservableList<Node> stage;
        private ObservableList<ColumnConstraints> columns;
        private ObservableList<RowConstraints> rows;

        Grid() { }

        @Override public void setPrefSize(double width, double height) {
            if (sizeObserver != null) sizeObserver.accept(this);
            super.setPrefSize(width, height);
        }

        @Override public void requestLayout() {
            if (layoutObserver != null) layoutObserver.accept(this);
            super.requestLayout();
        }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
            columns = FXCollections.observableArrayList();
            rows = FXCollections.observableArrayList();
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            settleConstraints(getColumnConstraints(), columns, Kept::sameColumn);
            settleConstraints(getRowConstraints(), rows, Kept::sameRow);
            columns = null;
            rows = null;
            if (want != null) settle(super.getChildren(), want);
        }
    }

    static ObservableList<ColumnConstraints> columns(GridPane grid) {
        return grid instanceof Grid g && g.columns != null ? g.columns : grid.getColumnConstraints();
    }

    static ObservableList<RowConstraints> rows(GridPane grid) {
        return grid instanceof Grid g && g.rows != null ? g.rows : grid.getRowConstraints();
    }

    private static <T> void settleConstraints(ObservableList<T> actual, List<T> want,
                                              java.util.function.BiPredicate<T, T> same) {
        if (want == null) return;
        if (actual.size() == want.size()) {
            boolean equal = true;
            for (int i = 0; i < actual.size(); i++) equal &= same.test(actual.get(i), want.get(i));
            if (equal) return;
        }
        actual.setAll(want);
    }

    private static boolean sameColumn(ColumnConstraints a, ColumnConstraints b) {
        return a.getMinWidth() == b.getMinWidth() && a.getPrefWidth() == b.getPrefWidth()
                && a.getMaxWidth() == b.getMaxWidth() && a.getPercentWidth() == b.getPercentWidth()
                && a.getHgrow() == b.getHgrow() && a.getHalignment() == b.getHalignment()
                && a.isFillWidth() == b.isFillWidth();
    }

    private static boolean sameRow(RowConstraints a, RowConstraints b) {
        return a.getMinHeight() == b.getMinHeight() && a.getPrefHeight() == b.getPrefHeight()
                && a.getMaxHeight() == b.getMaxHeight() && a.getPercentHeight() == b.getPercentHeight()
                && a.getVgrow() == b.getVgrow() && a.getValignment() == b.getValignment()
                && a.isFillHeight() == b.isFillHeight();
    }

    /** A Group, kept: Group's getChildren() is final, so a kept group's children are set through {@link #children}. */
    static Group group() {
        return of(Group.class, Group::new);
    }

    /* =====================================================================
       THE NODES, AS THE CODE ASKS FOR THEM
       ===================================================================== */

    /** A VBox with this spacing. */
    static VBox column(double spacing) {
        Column c = of(Column.class, Column::new);
        c.setSpacing(spacing);
        return c;
    }

    /** A VBox with this spacing and these children. */
    static VBox column(double spacing, Node... kids) {
        VBox c = column(spacing);
        c.getChildren().setAll(kids);
        return c;
    }

    /** An HBox with this spacing. */
    static HBox row(double spacing) {
        Row r = of(Row.class, Row::new);
        r.setSpacing(spacing);
        return r;
    }

    /** An HBox with this spacing and these children. */
    static HBox row(double spacing, Node... kids) {
        HBox r = row(spacing);
        r.getChildren().setAll(kids);
        return r;
    }

    /** A StackPane with these children. */
    static StackPane stack(Node... kids) {
        Stack s = of(Stack.class, Stack::new);
        s.getChildren().setAll(kids);
        return s;
    }

    /** A FlowPane with these gaps. */
    static FlowPane flow(double hgap, double vgap) {
        Flow f = of(Flow.class, Flow::new);
        f.setHgap(hgap);
        f.setVgap(vgap);
        return f;
    }

    /** A GridPane. */
    static GridPane grid() {
        return of(Grid.class, Grid::new);
    }

    /** A plain Pane with these children. */
    static Pane plain(Node... kids) {
        Plain p = of(Plain.class, Plain::new);
        p.getChildren().setAll(kids);
        return p;
    }

    /** A Region: a spacer, a bar, a swatch. */
    static Region region() {
        return of(SizedRegion.class, SizedRegion::new);
    }

    /** A plain Region with the same passive setter observation as the kept boxes. */
    static final class SizedRegion extends Region {
        @Override public void setPrefSize(double width, double height) {
            if (sizeObserver != null) sizeObserver.accept(this);
            super.setPrefSize(width, height);
        }
    }

    /** A Label with this text and no graphic. */
    static Label label(String text) {
        Label l = of(Label.class, Label::new);
        l.setText(text);
        if (l.getGraphic() != null) l.setGraphic(null);
        return l;
    }

    /** A Label with this text and this graphic. */
    static Label label(String text, Node graphic) {
        Label l = of(Label.class, Label::new);
        l.setText(text);
        l.setGraphic(graphic);
        return l;
    }

    /** A Group's children set where they differ, kept or not (Group's own list cannot be staged). */
    static void children(Group g, Node... kids) {
        settle(g.getChildren(), List.of(kids));
    }

    /* =====================================================================
       WHAT A NODE IS GIVEN THAT WOULD PILE UP ON A KEPT ONE
       ===================================================================== */

    /** The key Tooltip.install() keeps a node's tooltip under in its properties: what tipOf() reads. */
    private static final String TOOLTIP = "javafx.scene.control.Tooltip";
    /** The key play() keeps a node's last animation under in its properties, to stop it before the next. */
    private static final String ANIMATION = "kept.animation";

    /** The tooltip installed on a node, or null. */
    static Tooltip tipOf(Node n) {
        return n.getProperties().get(TOOLTIP) instanceof Tooltip t ? t : null;
    }

    /**
     * A tooltip on a node with this text and delay: the node's own, its text set, when it has one
     * and a draw is on; else a new one installed, as Tooltip.install did. Returned for anything
     * more the code sets on it.
     */
    static Tooltip tip(Node n, String text, double delayMillis) {
        Draw d = drawing;
        Tooltip t = tipOf(n);
        if (d != null && t != null) {
            if (!Objects.equals(t.getText(), text)) t.setText(text);
            if (t.getShowDelay().toMillis() != delayMillis) t.setShowDelay(Duration.millis(delayMillis));
        } else {
            t = new Tooltip(text);
            t.setShowDelay(Duration.millis(delayMillis));
            Tooltip.install(n, t);
        }
        if (d != null) d.tipped.add(n);
        return t;
    }

    /** The animation a node was last given through {@link #play}, or null. */
    static Animation animationOf(Node n) {
        return n.getProperties().get(ANIMATION) instanceof Animation a ? a : null;
    }

    /**
     * An animation on a node, played: the one this node was last given stopped first, so a month
     * that lands while the last month's count-up still runs does not leave two counting one label.
     */
    static void play(Node owner, Animation a) {
        Object was = owner.getProperties().put(ANIMATION, a);
        if (was instanceof Animation old) old.stop();
        a.play();
    }

    /* =====================================================================
       THE PLAYER'S HAND (KP2): a control on a kept page is not set under it
       ===================================================================== */

    /** The key grip() keeps a node's "held" flag under, and the marker that it has been gripped. */
    private static final String HELD = "kept.held";
    private static final String GRIP = "kept.grip";

    /**
     * Makes {@link #held} true for this node between a primary-button press on it and the release
     * that ends the press. Two event filters, added once in the node's life (a marker in its
     * properties, not {@link #isNew}, so it works outside a draw too): filters run before the node's
     * own handlers, so the release clears the flag before the release handler commits the value
     * and redraws - and that redraw is free to set the value. The scene hears a release even when
     * this node is greyed: a disable/enable during one press keeps the thumb under the hand, and
     * releasing elsewhere while disabled lets it go. Leaving the scene also lets it go.
     */
    static void grip(Node n) {
        if (n.getProperties().putIfAbsent(GRIP, Boolean.TRUE) != null) return;
        n.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.PRIMARY) n.getProperties().put(HELD, Boolean.TRUE);
        });
        EventHandler<MouseEvent> release = e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            letGo(n);
        };
        n.addEventFilter(MouseEvent.MOUSE_RELEASED, release);
        WeakEventHandler<MouseEvent> sceneRelease = new WeakEventHandler<>(release);
        n.sceneProperty().addListener((o, was, now) -> {
            if (was != null) was.removeEventFilter(MouseEvent.MOUSE_RELEASED, sceneRelease);
            if (now != null) now.addEventFilter(MouseEvent.MOUSE_RELEASED, sceneRelease);
            else {
                Draw draw = drawing != null ? drawing : settling;
                if (draw != null && was != null) draw.detached.putIfAbsent(n, was);
                else letGo(n);
            }
        });
        if (n.getScene() != null) n.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, sceneRelease);
    }

    private static void letGo(Node n) {
        n.getProperties().remove(HELD);
        if (n instanceof Slider s && s.isValueChanging()) s.setValueChanging(false);
    }

    /**
     * Whether the player holds this node now (see {@link #grip}); a slider also counts as held while
     * its value is changing. A kept control's value is never set while this is true: a month that
     * lands mid-drag must not move the thumb under the player's hand.
     */
    static boolean held(Node n) {
        if (n.getProperties().get(HELD) == Boolean.TRUE) return true;
        return n instanceof Slider s && s.isValueChanging();
    }

    /**
     * A field's text set only where it differs and never while the field has the focus: a set marks
     * the field edited and moves its caret, and the player typing owns it.
     */
    static void text(TextInputControl f, String text) {
        if (f.isFocused()) return;
        String want = text == null ? "" : text;
        if (!want.equals(f.getText())) f.setText(want);
    }
}
