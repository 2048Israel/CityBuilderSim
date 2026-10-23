package ham.citybuildersim.ui;

import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.*;
import java.util.function.Consumer;

/**
 * Scratch, for the study UP4 (never in src/): one month's page, built off the scene, set onto the
 * page on screen node by node - only what differs - so the CSS and layout that follow are what a
 * kept page would pay. Not a design: the study says why a generic patch cannot be the pattern
 * (a handler built with the fresh page holds the fresh page's nodes).
 */
final class TreePatch {

    private TreePatch() { }

    /** What one patch did. */
    static final class Stats {
        long text, style, set, handlers, swapped, swappedNodes, added, addedNodes, removed, removedNodes, untouched, pictures;
        /** Fresh node to the kept node it was set onto, for a picture's fields (transplant()). */
        final IdentityHashMap<Node, Node> kept = new IdentityHashMap<>();
        final Map<String, Integer> swappedBy = new HashMap<>(), byCard = new HashMap<>(), swapDetail = new HashMap<>();
        /** The values set by name (the CSS property's name, or the setter's), for the record. */
        static final Map<String, Integer> SET_BY = new HashMap<>();

        void add(Stats o) {
            text += o.text; style += o.style; set += o.set; handlers += o.handlers;
            swapped += o.swapped; swappedNodes += o.swappedNodes; added += o.added; addedNodes += o.addedNodes;
            removed += o.removed; removedNodes += o.removedNodes; untouched += o.untouched; pictures += o.pictures;
            o.swappedBy.forEach((k, v) -> swappedBy.merge(k, v, Integer::sum));
            o.byCard.forEach((k, v) -> byCard.merge(k, v, Integer::sum));
            o.swapDetail.forEach((k, v) -> swapDetail.merge(k, v, Integer::sum));
        }
    }

    /** A page's nodes by kind: every node in the scene graph, and the code's own nodes by what they are. */
    static final class Census {
        int nodes, labeled, text, region, shape, canvas, control, custom;
    }

    static Census census(List<Node> roots) {
        Census c = new Census();
        for (Node n : roots) {
            c.nodes += countAll(n);
            censusOf(n, c);
        }
        return c;
    }

    private static void censusOf(Node n, Census c) {
        if (custom(n)) c.custom++;
        if (n instanceof Labeled) c.labeled++;
        else if (n instanceof Text) c.text++;
        else if (n instanceof Control) c.control++;
        else if (n instanceof Canvas) c.canvas++;
        else if (n instanceof Shape) c.shape++;
        else if (n instanceof Region) c.region++;
        for (Node k : codeChildren(n)) censusOf(k, c);
    }

    /** Every node under this one, the controls' skins included: what CSS walks. */
    static int countAll(Node n) {
        int k = 1;
        if (n instanceof Parent p) for (Node c : p.getChildrenUnmodifiable()) k += countAll(c);
        if (n instanceof Labeled l && l.getGraphic() != null && l.getGraphic().getParent() != n) k += countAll(l.getGraphic());
        if (n instanceof ScrollPane s && s.getContent() != null && s.getSkin() == null) k += countAll(s.getContent());
        return k;
    }

    /** The children the screen's code made: a pane's or group's list, a label's graphic, a scroller's content. */
    static List<Node> codeChildren(Node n) {
        if (n instanceof Pane p) return new ArrayList<>(p.getChildren());
        if (n instanceof Group g) return new ArrayList<>(g.getChildren());
        if (n instanceof Labeled l) return l.getGraphic() == null ? List.of() : List.of(l.getGraphic());
        if (n instanceof ScrollPane s) return s.getContent() == null ? List.of() : List.of(s.getContent());
        return List.of();
    }

    static boolean custom(Node n) {
        return n.getClass().getName().startsWith("ham.");
    }

    /** A class of the game's own that lays its children out from data it holds: kept, it would lay out last month's. */
    static boolean laysOutItself(Class<?> c) {
        for (Class<?> k = c; k != null && k.getName().startsWith("ham."); k = k.getSuperclass()) {
            try {
                k.getDeclaredMethod("layoutChildren");
                return true;
            } catch (NoSuchMethodException e) {
                // keep looking
            }
        }
        return false;
    }

    /** Swapped whole rather than patched: a canvas (its pixels), a control with state of its own, a self-laying class. */
    static boolean opaque(Node n) {
        if (n instanceof Canvas) return true;
        if (n instanceof ToggleButton || n instanceof CheckBox) return true;
        if (n instanceof Control && !(n instanceof Labeled) && !(n instanceof ScrollPane)) return true;
        return custom(n) && laysOutItself(n.getClass());
    }

    /** The words on a page, in order: every label's and text's string, and every inline style. */
    static List<String> words(List<Node> roots) {
        List<String> out = new ArrayList<>();
        for (Node n : roots) wordsOf(n, out);
        return out;
    }

    private static void wordsOf(Node n, List<String> out) {
        out.add(n.getClass().getSimpleName() + (n.isVisible() ? "" : " hidden") + (n.isManaged() ? "" : " unmanaged"));
        if (n.getStyle() != null && !n.getStyle().isEmpty()) out.add("S " + n.getStyle());
        if (n instanceof Labeled l) out.add("L " + l.getText());
        if (n instanceof Text t) out.add("T " + t.getText());
        if (opaque(n)) return;
        for (Node k : codeChildren(n)) wordsOf(k, out);
    }

    static String firstText(Node n) {
        if (n instanceof Labeled l && l.getText() != null && !l.getText().isBlank()) return l.getText();
        if (n instanceof Text t && t.getText() != null && !t.getText().isBlank()) return t.getText();
        for (Node k : codeChildren(n)) {
            String s = firstText(k);
            if (s != null) return s;
        }
        return null;
    }

    static String firstWords(Node n) {
        String s = firstText(n);
        if (s == null) return "";
        s = s.replace('\n', ' ').strip();
        return (s.length() > 16 ? s.substring(0, 16) : s).replace(' ', '_');
    }

    static String cardName(int i, Node n) {
        String s = firstText(n);
        if (s == null) s = n.getClass().getSimpleName();
        s = s.replace('\n', ' ').strip();
        if (s.length() > 18) s = s.substring(0, 18);
        return i + ":" + s.replace(' ', '_');
    }

    /** The page on screen (the box's children) made equal to the fresh build, touching only what differs. */
    static Stats patch(Pane liveRoot, List<Node> fresh) {
        Stats st = new Stats();
        patchList(liveRoot.getChildren(), new ArrayList<>(liveRoot.getChildren()), fresh, st, null, 0);
        return st;
    }

    private static void patchList(ObservableList<Node> list, List<Node> a, List<Node> b, Stats st, String card, int depth) {
        int n = Math.min(a.size(), b.size());
        for (int i = 0; i < n; i++) {
            final int at = i;
            String c = card;
            if (depth == 1) c = cardName(i, b.get(i));
            patchNode(a.get(i), b.get(i), st, c == null ? "top" : c, depth + 1, x -> list.set(at, x));
        }
        if (b.size() > n) {
            List<Node> extra = new ArrayList<>(b.subList(n, b.size()));
            st.added += extra.size();
            for (Node x : extra) st.addedNodes += countAll(x);
            if (card != null) st.byCard.merge(card, extra.size(), Integer::sum);
            list.addAll(extra);
        }
        if (a.size() > n) {
            st.removed += a.size() - n;
            for (Node x : a.subList(n, a.size())) st.removedNodes += countAll(x);
            if (card != null) st.byCard.merge(card, a.size() - n, Integer::sum);
            list.remove(n, a.size());
        }
    }

    private static void patchNode(Node a, Node b, Stats st, String card, int depth, Consumer<Node> replace) {
        // A picture of the game's own that lays out its children from its data: kept when its children are the
        // same shape, the way a picture given new data would be - its children patched, its fields taken from
        // this month's (node references mapped onto the kept children), and laid out again.
        boolean picture = a.getClass() == b.getClass() && custom(b) && laysOutItself(b.getClass()) && sameShape(a, b);
        if (!picture && (a.getClass() != b.getClass() || opaque(b))) {
            st.swapped++;
            st.swappedNodes += countAll(b);
            st.swappedBy.merge(b.getClass().getSimpleName(), 1, Integer::sum);
            String why = a.getClass() != b.getClass() ? a.getClass().getSimpleName() + ">" + b.getClass().getSimpleName()
                    : b.getClass().getSimpleName() + (custom(b) ? "(shape)" : "(opaque)");
            String near = a.getParent() == null ? "" : a.getParent().getClass().getSimpleName();
            st.swapDetail.merge(why + "@" + near + "[" + firstWords(b) + "]", 1, Integer::sum);
            st.byCard.merge(card, 1, Integer::sum);
            replace.accept(b);
            return;
        }
        st.kept.put(b, a);
        long before = st.text + st.style + st.set;
        if (!Objects.equals(a.getStyle(), b.getStyle())) { a.setStyle(b.getStyle()); st.style++; }
        if (!a.getStyleClass().equals(b.getStyleClass())) { a.getStyleClass().setAll(b.getStyleClass()); st.style++; }
        st.set += copyCodeValues(a, b);
        if (a.isManaged() != b.isManaged()) { a.setManaged(b.isManaged()); st.set++; Stats.SET_BY.merge("setManaged", 1, Integer::sum); }
        boolean placedByCode = !b.isManaged() || a.getParent() == null
                || a.getParent().getClass() == Pane.class || a.getParent() instanceof Group;
        if (placedByCode) {
            if (a.getLayoutX() != b.getLayoutX()) { a.setLayoutX(b.getLayoutX()); st.set++; Stats.SET_BY.merge("setLayoutX", 1, Integer::sum); }
            if (a.getLayoutY() != b.getLayoutY()) { a.setLayoutY(b.getLayoutY()); st.set++; Stats.SET_BY.merge("setLayoutY", 1, Integer::sum); }
        }
        if (a.isDisable() != b.isDisable()) { a.setDisable(b.isDisable()); st.set++; Stats.SET_BY.merge("setDisable", 1, Integer::sum); }
        if (a.isMouseTransparent() != b.isMouseTransparent()) { a.setMouseTransparent(b.isMouseTransparent()); st.set++; Stats.SET_BY.merge("setMouseTransparent", 1, Integer::sum); }
        if (a.isPickOnBounds() != b.isPickOnBounds()) { a.setPickOnBounds(b.isPickOnBounds()); st.set++; Stats.SET_BY.merge("setPickOnBounds", 1, Integer::sum); }
        a.setUserData(b.getUserData());
        st.set += copyProperties(a, b);
        if (copyHandlers(a, b)) st.handlers++;

        if (a instanceof Labeled la && b instanceof Labeled lb) {
            if (!Objects.equals(la.getText(), lb.getText())) { la.setText(lb.getText()); st.text++; }
            la.setTooltip(lb.getTooltip());
            if (a instanceof ButtonBase ba && b instanceof ButtonBase bb) ba.setOnAction(bb.getOnAction());
            Node ga = la.getGraphic(), gb = lb.getGraphic();
            if (ga == null && gb != null) { la.setGraphic(gb); st.added++; st.addedNodes += countAll(gb); }
            else if (ga != null && gb == null) { la.setGraphic(null); st.removed++; st.removedNodes += countAll(ga); }
            else if (ga != null) patchNode(ga, gb, st, card, depth + 1, la::setGraphic);
        } else if (a instanceof Text ta && b instanceof Text tb) {
            if (!Objects.equals(ta.getText(), tb.getText())) { ta.setText(tb.getText()); st.text++; }
            if (ta.getX() != tb.getX()) { ta.setX(tb.getX()); st.set++; Stats.SET_BY.merge("setX", 1, Integer::sum); }
            if (ta.getY() != tb.getY()) { ta.setY(tb.getY()); st.set++; Stats.SET_BY.merge("setY", 1, Integer::sum); }
            if (ta.getWrappingWidth() != tb.getWrappingWidth()) { ta.setWrappingWidth(tb.getWrappingWidth()); st.set++; Stats.SET_BY.merge("setWrappingWidth", 1, Integer::sum); }
        } else if (a instanceof Shape) {
            st.set += copyGeometry(a, b);
        } else if (a instanceof ImageView ia && b instanceof ImageView ib) {
            if (ia.getImage() != ib.getImage()) { ia.setImage(ib.getImage()); st.set++; Stats.SET_BY.merge("setImage", 1, Integer::sum); }
            if (ia.getFitWidth() != ib.getFitWidth()) { ia.setFitWidth(ib.getFitWidth()); st.set++; Stats.SET_BY.merge("setFitWidth", 1, Integer::sum); }
            if (ia.getFitHeight() != ib.getFitHeight()) { ia.setFitHeight(ib.getFitHeight()); st.set++; Stats.SET_BY.merge("setFitHeight", 1, Integer::sum); }
        } else if (a instanceof ScrollPane sa && b instanceof ScrollPane sb) {
            Node ca = sa.getContent(), cb = sb.getContent();
            if (ca == null || cb == null) { sa.setContent(cb); st.swapped++; }
            else patchNode(ca, cb, st, card, depth + 1, sa::setContent);
        }
        if (a instanceof GridPane ga && b instanceof GridPane gb) {
            if (!sameColumns(ga, gb)) { ga.getColumnConstraints().setAll(gb.getColumnConstraints()); st.set++; Stats.SET_BY.merge("setAll", 1, Integer::sum); }
            if (ga.getRowConstraints().size() != gb.getRowConstraints().size()) {
                ga.getRowConstraints().setAll(gb.getRowConstraints());
                st.set++;
            }
        }
        if (a instanceof FlowPane fa && b instanceof FlowPane fb && fa.getPrefWrapLength() != fb.getPrefWrapLength()) {
            fa.setPrefWrapLength(fb.getPrefWrapLength());
            st.set++;
        }
        long changes = st.text + st.style + st.set - before;
        if (changes == 0) st.untouched++;
        else st.byCard.merge(card, (int) changes, Integer::sum);
        if (a instanceof Pane pa && b instanceof Pane pb) {
            patchList(pa.getChildren(), new ArrayList<>(pa.getChildren()), new ArrayList<>(pb.getChildren()), st, card, depth);
        } else if (a instanceof Group ga && b instanceof Group gb) {
            patchList(ga.getChildren(), new ArrayList<>(ga.getChildren()), new ArrayList<>(gb.getChildren()), st, card, depth);
        }
        if (picture) {
            st.pictures++;
            transplant(a, b, st.kept);
            if (a instanceof Parent p) p.requestLayout();
        }
    }

    /** The same classes in the same places all the way down: a picture that can keep its nodes. */
    static boolean sameShape(Node a, Node b) {
        if (a.getClass() != b.getClass()) return false;
        if (a instanceof Canvas) return true;
        List<Node> ka = codeChildren(a), kb = codeChildren(b);
        if (ka.size() != kb.size()) return false;
        for (int i = 0; i < ka.size(); i++) if (!sameShape(ka.get(i), kb.get(i))) return false;
        return true;
    }

    /** A picture's own fields, this month's, set on the kept picture: its nodes mapped onto the kept ones, its properties' values copied. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void transplant(Object live, Object fresh, IdentityHashMap<Node, Node> kept) {
        for (Class<?> c = fresh.getClass(); c != null && c.getName().startsWith("ham."); c = c.getSuperclass()) {
            for (java.lang.reflect.Field f : c.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) || f.isSynthetic()) continue;
                try {
                    f.setAccessible(true);
                    Object v = f.get(fresh), old = f.get(live);
                    if (v instanceof javafx.beans.value.WritableValue w && old instanceof javafx.beans.value.WritableValue ow
                            && !(v instanceof Node)) {
                        if (old instanceof javafx.beans.property.Property p && p.isBound()) continue;
                        ow.setValue(w.getValue());
                        continue;
                    }
                    f.set(live, remap(v, kept));
                } catch (ReflectiveOperationException | RuntimeException e) {
                    Stats.SET_BY.merge("transplant failed " + c.getSimpleName() + "." + f.getName(), 1, Integer::sum);
                }
            }
        }
    }

    private static Object remap(Object v, IdentityHashMap<Node, Node> kept) {
        if (v instanceof Node n) return kept.getOrDefault(n, n);
        if (v instanceof javafx.collections.ObservableList) return v;
        if (v instanceof List<?> l) {
            List<Object> r = new ArrayList<>(l.size());
            for (Object x : l) r.add(remap(x, kept));
            return r;
        }
        if (v instanceof Node[] arr) {
            Node[] r = arr.clone();
            for (int i = 0; i < r.length; i++) r[i] = (Node) remap(r[i], kept);
            return r;
        }
        return v;
    }

    private static boolean sameColumns(GridPane a, GridPane b) {
        if (a.getColumnConstraints().size() != b.getColumnConstraints().size()) return false;
        for (int i = 0; i < a.getColumnConstraints().size(); i++) {
            var x = a.getColumnConstraints().get(i);
            var y = b.getColumnConstraints().get(i);
            if (x.getPercentWidth() != y.getPercentWidth() || x.getPrefWidth() != y.getPrefWidth()
                    || x.getMinWidth() != y.getMinWidth() || x.getMaxWidth() != y.getMaxWidth()
                    || x.getHgrow() != y.getHgrow() || x.getHalignment() != y.getHalignment()) return false;
        }
        return true;
    }

    /**
     * The values the code set (StyleOrigin.USER on the fresh node, which CSS has never touched),
     * set on the kept node where they differ - unless CSS owns that value on the kept node (an
     * author or inline style, which outranks code). A value code set last time and not this time
     * goes back to its initial value.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int copyCodeValues(Styleable a, Styleable b) {
        int k = 0;
        for (CssMetaData m : b.getCssMetaData()) {
            StyleableProperty pb = m.getStyleableProperty(b);
            StyleableProperty pa = m.getStyleableProperty(a);
            if (pa == null || pb == null) continue;
            // A control's skin is the toolkit's, made by its first CSS pass: never the page's to set.
            if ("-fx-skin".equals(m.getProperty())) continue;
            StyleOrigin oa = pa.getStyleOrigin(), ob = pb.getStyleOrigin();
            if (oa == StyleOrigin.AUTHOR || oa == StyleOrigin.INLINE) continue;
            if (ob == StyleOrigin.USER) {
                Object vb = pb.getValue();
                if (!Objects.equals(pa.getValue(), vb)) {
                    try { pa.setValue(vb); k++; Stats.SET_BY.merge(m.getProperty(), 1, Integer::sum); } catch (RuntimeException e) { /* bound: leave it */ }
                }
            } else if (oa == StyleOrigin.USER && !(a instanceof Node n && n.getParent() != null
                    && custom(n.getParent()) && laysOutItself(n.getParent().getClass()))) {
                try { pa.setValue(m.getInitialValue(a)); k++; Stats.SET_BY.merge("reset " + m.getProperty(), 1, Integer::sum); } catch (RuntimeException e) { /* bound */ }
            }
        }
        return k;
    }

    /** Layout constraints and the rest a node keeps in its properties (GridPane's cell, HBox's grow, a margin). */
    private static int copyProperties(Node a, Node b) {
        int k = 0;
        var pa = a.getProperties();
        var pb = b.getProperties();
        if (pa.equals(pb)) return 0;
        boolean layout = false;
        for (var e : pb.entrySet()) {
            Object was = pa.get(e.getKey());
            if (Objects.equals(was, e.getValue())) continue;
            if (was instanceof javafx.scene.control.Tooltip ta && e.getValue() instanceof javafx.scene.control.Tooltip tb) {
                if (!Objects.equals(ta.getText(), tb.getText())) { ta.setText(tb.getText()); k++; Stats.SET_BY.merge("tooltip text", 1, Integer::sum); }
                continue;
            }
            pa.put(e.getKey(), e.getValue());
            k++;
            layout = true;
            Stats.SET_BY.merge("properties " + e.getKey(), 1, Integer::sum);
        }
        for (Object key : new ArrayList<>(pa.keySet())) if (!pb.containsKey(key) && pagesOwn(key)) { pa.remove(key); k++; Stats.SET_BY.merge("properties remove", 1, Integer::sum); }
        if (layout && a.getParent() != null) a.getParent().requestLayout();
        return k;
    }

    /** A key the page's code puts in a node's properties - a pane's constraint, a tooltip - rather than the toolkit's own. */
    private static boolean pagesOwn(Object key) {
        if (!(key instanceof String k)) return false;
        return k.startsWith("gridpane-") || k.startsWith("hbox-") || k.startsWith("vbox-") || k.startsWith("stackpane-")
                || k.startsWith("flowpane-") || k.startsWith("borderpane-") || k.startsWith("anchorpane-")
                || k.startsWith("tilepane-") || k.equals("javafx.scene.control.Tooltip");
    }

    /** The handlers a node was given, from this month's build: whether it had any. */
    private static boolean copyHandlers(Node a, Node b) {
        a.setOnMouseClicked(b.getOnMouseClicked());
        a.setOnMousePressed(b.getOnMousePressed());
        a.setOnMouseReleased(b.getOnMouseReleased());
        a.setOnMouseEntered(b.getOnMouseEntered());
        a.setOnMouseExited(b.getOnMouseExited());
        a.setOnMouseMoved(b.getOnMouseMoved());
        a.setOnMouseDragged(b.getOnMouseDragged());
        a.setOnScroll(b.getOnScroll());
        a.setOnKeyPressed(b.getOnKeyPressed());
        a.setOnContextMenuRequested(b.getOnContextMenuRequested());
        return b.getOnMouseClicked() != null || b.getOnMousePressed() != null || b.getOnMouseEntered() != null
                || b.getOnMouseExited() != null || b.getOnMouseMoved() != null || b.getOnScroll() != null
                || (b instanceof ButtonBase bb && bb.getOnAction() != null);
    }

    private static int copyGeometry(Node a, Node b) {
        int k = 0;
        if (a instanceof Rectangle x && b instanceof Rectangle y) {
            if (x.getX() != y.getX()) { x.setX(y.getX()); k++; Stats.SET_BY.merge("setX", 1, Integer::sum); }
            if (x.getY() != y.getY()) { x.setY(y.getY()); k++; Stats.SET_BY.merge("setY", 1, Integer::sum); }
            if (x.getWidth() != y.getWidth()) { x.setWidth(y.getWidth()); k++; Stats.SET_BY.merge("setWidth", 1, Integer::sum); }
            if (x.getHeight() != y.getHeight()) { x.setHeight(y.getHeight()); k++; Stats.SET_BY.merge("setHeight", 1, Integer::sum); }
        } else if (a instanceof Line x && b instanceof Line y) {
            if (x.getStartX() != y.getStartX()) { x.setStartX(y.getStartX()); k++; Stats.SET_BY.merge("setStartX", 1, Integer::sum); }
            if (x.getStartY() != y.getStartY()) { x.setStartY(y.getStartY()); k++; Stats.SET_BY.merge("setStartY", 1, Integer::sum); }
            if (x.getEndX() != y.getEndX()) { x.setEndX(y.getEndX()); k++; Stats.SET_BY.merge("setEndX", 1, Integer::sum); }
            if (x.getEndY() != y.getEndY()) { x.setEndY(y.getEndY()); k++; Stats.SET_BY.merge("setEndY", 1, Integer::sum); }
        } else if (a instanceof Circle x && b instanceof Circle y) {
            if (x.getCenterX() != y.getCenterX()) { x.setCenterX(y.getCenterX()); k++; Stats.SET_BY.merge("setCenterX", 1, Integer::sum); }
            if (x.getCenterY() != y.getCenterY()) { x.setCenterY(y.getCenterY()); k++; Stats.SET_BY.merge("setCenterY", 1, Integer::sum); }
            if (x.getRadius() != y.getRadius()) { x.setRadius(y.getRadius()); k++; Stats.SET_BY.merge("setRadius", 1, Integer::sum); }
        } else if (a instanceof Ellipse x && b instanceof Ellipse y) {
            if (x.getCenterX() != y.getCenterX()) { x.setCenterX(y.getCenterX()); k++; Stats.SET_BY.merge("setCenterX", 1, Integer::sum); }
            if (x.getCenterY() != y.getCenterY()) { x.setCenterY(y.getCenterY()); k++; Stats.SET_BY.merge("setCenterY", 1, Integer::sum); }
            if (x.getRadiusX() != y.getRadiusX()) { x.setRadiusX(y.getRadiusX()); k++; Stats.SET_BY.merge("setRadiusX", 1, Integer::sum); }
            if (x.getRadiusY() != y.getRadiusY()) { x.setRadiusY(y.getRadiusY()); k++; Stats.SET_BY.merge("setRadiusY", 1, Integer::sum); }
        } else if (a instanceof Arc x && b instanceof Arc y) {
            if (x.getCenterX() != y.getCenterX()) { x.setCenterX(y.getCenterX()); k++; Stats.SET_BY.merge("setCenterX", 1, Integer::sum); }
            if (x.getCenterY() != y.getCenterY()) { x.setCenterY(y.getCenterY()); k++; Stats.SET_BY.merge("setCenterY", 1, Integer::sum); }
            if (x.getRadiusX() != y.getRadiusX()) { x.setRadiusX(y.getRadiusX()); k++; Stats.SET_BY.merge("setRadiusX", 1, Integer::sum); }
            if (x.getRadiusY() != y.getRadiusY()) { x.setRadiusY(y.getRadiusY()); k++; Stats.SET_BY.merge("setRadiusY", 1, Integer::sum); }
            if (x.getStartAngle() != y.getStartAngle()) { x.setStartAngle(y.getStartAngle()); k++; Stats.SET_BY.merge("setStartAngle", 1, Integer::sum); }
            if (x.getLength() != y.getLength()) { x.setLength(y.getLength()); k++; Stats.SET_BY.merge("setLength", 1, Integer::sum); }
            if (x.getType() != y.getType()) { x.setType(y.getType()); k++; Stats.SET_BY.merge("setType", 1, Integer::sum); }
        } else if (a instanceof Polygon x && b instanceof Polygon y) {
            if (!x.getPoints().equals(y.getPoints())) { x.getPoints().setAll(y.getPoints()); k++; Stats.SET_BY.merge("setAll", 1, Integer::sum); }
        } else if (a instanceof Polyline x && b instanceof Polyline y) {
            if (!x.getPoints().equals(y.getPoints())) { x.getPoints().setAll(y.getPoints()); k++; Stats.SET_BY.merge("setAll", 1, Integer::sum); }
        } else if (a instanceof Path x && b instanceof Path y) {
            if (!x.getElements().toString().equals(y.getElements().toString())) {
                x.getElements().setAll(new ArrayList<>(y.getElements()));
                k++;
            }
        } else if (a instanceof SVGPath x && b instanceof SVGPath y) {
            if (!Objects.equals(x.getContent(), y.getContent())) { x.setContent(y.getContent()); k++; Stats.SET_BY.merge("setContent", 1, Integer::sum); }
        }
        return k;
    }
}
