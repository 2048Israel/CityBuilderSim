package ham.citybuildersim.ui;

import javafx.animation.Animation;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
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

/**
 * A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a
 * screen's code made at its last draw, handed back to the same code, in the order it asks for
 * them, at the next - so a month that lands sets its figures on the page on show instead of
 * throwing the page away and building another.
 *
 * <p>WHY. Until 0.8.25 every screen was drawn by emptying the page and building it afresh
 * (UserInterface.clearMenu()), on every month the clock redraws. A node new to the scene is
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
 * does not shift every label below it onto a neighbour's.</li>
 * <li>A node handed back first loses what the code gives it only sometimes - its click and hover
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

        void begin() {
            making = new HashMap<>();
            innerMaking = new HashMap<>();
            taken.clear();
        }

        /** What was not asked for this draw is let go: the next draw hands back only this one's. */
        void end() {
            made = making;
            inner = innerMaking;
        }
    }

    /** A draw in progress. */
    private static final class Draw {
        final ArrayDeque<Scope> scopes = new ArrayDeque<>();
        final List<Scope> begun = new ArrayList<>();
        final List<Staging> staged = new ArrayList<>();
        final List<Node> handedBack = new ArrayList<>();
        final Set<Node> made = Collections.newSetFromMap(new IdentityHashMap<>());
        final Set<Node> tipped = Collections.newSetFromMap(new IdentityHashMap<>());
    }

    /** The draw on now, or null: then every call here makes a new node. Only ever touched on the FX thread. */
    private static Draw drawing;

    /** Whether a kept draw is on. */
    static boolean drawing() {
        return drawing != null;
    }

    /**
     * Runs a page's code as a kept draw from the pool: every node it asks for here is handed back
     * from the pool's last draw where it can be, and when the code returns each box's children are
     * set from what the code put in them. A draw inside a draw is part of it.
     */
    static void draw(Pool pool, Runnable code) {
        if (drawing != null) {
            code.run();
            return;
        }
        Draw d = new Draw();
        drawing = d;
        pool.root.begin();
        d.begun.add(pool.root);
        d.scopes.push(pool.root);
        try {
            code.run();
        } finally {
            drawing = null;
            for (Scope s : d.begun) s.end();
            for (Staging s : d.staged) s.commit();
            // A tooltip the code gave a node last time and not this time goes.
            for (Node n : d.handedBack) {
                Tooltip t = tipOf(n);
                if (t != null && !d.tipped.contains(n)) Tooltip.uninstall(n, t);
            }
            pool.draws++;
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

    /** A node handed back, made what a new one is in what code gives a node only sometimes. */
    private static void handBack(Draw d, Node n) {
        d.handedBack.add(n);
        n.setOnMouseClicked(null);
        n.setOnMousePressed(null);
        n.setOnMouseReleased(null);
        n.setOnMouseEntered(null);
        n.setOnMouseExited(null);
        n.setOnMouseMoved(null);
        n.setOnMouseDragged(null);
        n.setOnScroll(null);
        if (n instanceof ButtonBase b) b.setOnAction(null);
        n.setManaged(true);
        n.setDisable(false);
        n.setMouseTransparent(false);
        resetIfCodeSet(n.cursorProperty(), null);
        resetIfCodeSet(n.visibleProperty(), true);
        resetIfCodeSet(n.opacityProperty(), 1.0);
    }

    /**
     * Back to a new node's value - but only a value code set: one an inline style set belongs to
     * the style, outranks code, and would not come back until the style changed.
     */
    @SuppressWarnings("unchecked")
    private static <T> void resetIfCodeSet(Property<? extends T> p, T initial) {
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
    private static void settle(ObservableList<Node> real, List<Node> want) {
        if (real.size() == want.size()) {
            boolean same = true;
            for (int i = 0; i < want.size() && same; i++) same = real.get(i) == want.get(i);
            if (same) return;
        }
        real.setAll(want);
    }

    /** A VBox, kept. */
    static final class Column extends VBox implements Staging {
        private ObservableList<Node> stage;

        Column() { }

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
     * A GridPane, kept. Its column and row constraints are a list on the grid, not children, so a
     * grid handed back starts with none, and the code that adds them each draw adds them once.
     */
    static final class Grid extends GridPane implements Staging {
        private ObservableList<Node> stage;

        Grid() { }

        @Override public ObservableList<Node> getChildren() {
            return stage != null ? stage : super.getChildren();
        }

        @Override public void stage() {
            stage = FXCollections.observableArrayList();
            if (!isNew(this)) {
                getColumnConstraints().clear();
                getRowConstraints().clear();
            }
        }

        @Override public void commit() {
            ObservableList<Node> want = stage;
            stage = null;
            if (want != null) settle(super.getChildren(), want);
        }
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
        return of(Region.class, Region::new);
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

    private static final String TOOLTIP = "javafx.scene.control.Tooltip";
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

    /**
     * An animation on a node, played: the one this node was last given stopped first, so a month
     * that lands while the last month's count-up still runs does not leave two counting one label.
     */
    static void play(Node owner, Animation a) {
        Object was = owner.getProperties().put(ANIMATION, a);
        if (was instanceof Animation old) old.stop();
        a.play();
    }
}
