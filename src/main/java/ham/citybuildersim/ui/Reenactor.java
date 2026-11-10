package ham.citybuildersim.ui;

import ham.citybuildersim.InputLog;
import ham.citybuildersim.ui.FollowRules.Verdict;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;

/**
 * Re-enacts one logged input event in the window (TR2, 2026-11-09): the mouse moved over the copy's real nodes, pressed,
 * dragged, released and clicked on them, the wheel turned over them, the keys pressed into the focused control - as events
 * fired into the scene, so the nodes' own handlers and the game's key filter answer as they did in the sitting. What not to
 * fire is {@link FollowRules}' to say; this class asks it for every event and keeps a press's verdict for its drags and its
 * release.
 *
 * <p>WHY (TR2): Follow plays a sitting back into the copy's window and Jerus chose "Re-enact it" over a ghost that only points:
 * a tab switches, a page scrolls, a hover card opens, a dial moves, because the ghost did it. JavaFX has no public pick and
 * no way to hand a scene a mouse position without a real pointer, so this picks by itself (the same rules as the scene's: the
 * top layer first, the last child first, invisible, disabled and mouse-transparent nodes and clipped-out points skipped) and
 * builds the events the scene's own handler would. Everything runs on the FX thread.
 */
final class Reenactor {

    /** How far, in pixels, a press may be dragged and still count as one that has not moved (the click's "still since press"). */
    private static final double CLICK_SLOP = 3;

    /** How far outside a parent's bounds a point may lie and still be looked for in its children: the bounds' own rounding. */
    private static final double BOUNDS_SLACK = 1;

    /** Node.setHover, which is protected: found by reflection, or null where the platform will not allow it. */
    private static final Method SET_HOVER = nodeSetter("setHover");

    /** Node.setPressed, which is protected too: found the same way, or null. */
    private static final Method SET_PRESSED = nodeSetter("setPressed");

    /** The toolkit's key for each log code (InputLog.keyCode of the key's name). */
    private static final Map<Integer, KeyCode> KEYS = new HashMap<>();

    static {
        for (KeyCode k : KeyCode.values()) KEYS.putIfAbsent(InputLog.keyCode(k.getName()), k);
    }

    private final UserInterface ui;
    /** The layers of the window's stack that are never picked (the time bar, the ghost, the graphs). */
    private final List<Node> excluded = new ArrayList<>();
    /** How many synthesized events are being dispatched now. */
    private int firing;

    /** The nodes under the pointer, outermost first. */
    private List<Node> hovered = new ArrayList<>();

    // The press being re-enacted.
    private boolean gesture;            // a press was seen and its release has not come
    private Verdict pressVerdict = Verdict.KEEP;
    private Node pressTarget;           // null for a dropped press
    private List<Node> pressChain = List.of();      // the press target and its ancestors, deepest first
    private int pressButton, pressClicks, pressMods;
    private double pressX, pressY;
    private boolean still;

    /** The keys whose KEY_PRESSED was fired and whose KEY_RELEASED has not been, with the modifiers of the press: reset() lets go of them, so a copy does not keep panning (TR2's review). */
    private final Map<KeyCode, Integer> keysDown = new LinkedHashMap<>();

    Reenactor(UserInterface ui) {
        this.ui = ui;
    }

    /** Whether a synthesized event is being dispatched now: the window's key handler then lets the replay's own keys alone. */
    boolean isFiring() {
        return firing > 0;
    }

    /** Whether Node.setHover could be called (the nodes under the pointer then answer isHover() and :hover). */
    static boolean setsHover() {
        return SET_HOVER != null;
    }

    /** Whether Node.setPressed could be called. */
    static boolean setsPressed() {
        return SET_PRESSED != null;
    }

    /**
     * The hover and the press forgotten, without a word to any node: after a month was loaded whole, the nodes they were on are
     * gone. The keys still down are let go of (a KEY_RELEASED each, to the control that has the focus), so a W pressed when Follow
     * stops does not keep the copy panning.
     */
    void reset() {
        for (Node n : hovered) setFlag(SET_HOVER, n, false);
        hovered = new ArrayList<>();
        forgetPress();
        Map<KeyCode, Integer> held = new LinkedHashMap<>(keysDown);
        keysDown.clear();
        Scene scene = ui.replayScene();
        if (scene == null) return;
        for (Map.Entry<KeyCode, Integer> k : held.entrySet()) {
            int m = k.getValue();
            fire(target(scene), new KeyEvent(scene, target(scene), KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED, "", k.getKey(),
                    (m & InputLog.SHIFT) != 0, (m & InputLog.CTRL) != 0, (m & InputLog.ALT) != 0, (m & InputLog.META) != 0));
        }
    }

    /** The press forgotten: no node keeps the pressed state it was given. */
    private void forgetPress() {
        if (pressTarget != null) for (Node n : pressChain) setFlag(SET_PRESSED, n, false);
        gesture = false;
        pressVerdict = Verdict.KEEP;
        pressTarget = null;
        pressChain = List.of();
    }

    /** A layer of the window's stack that is never picked: the time bar, the ghost, the graphs. */
    void exclude(Node layer) {
        excluded.removeIf(n -> n.getScene() == null);      // a layer of a replay left before (its bar, its ghost) is gone from the window
        if (layer != null && !excluded.contains(layer)) excluded.add(layer);
    }

    Verdict play(InputLog.Event e, String loggedScreen) {
        return play(e, loggedScreen, false);
    }

    /**
     * Re-enacts one event. {@code catchingUp}: many events come in one frame, so after each press and release the window's CSS
     * and layout are done at once, and the next pick sees the page the last click made.
     *
     * @return KEEP when it was re-enacted (or is a marker, or had nothing to act on), else why it was dropped
     */
    Verdict play(InputLog.Event e, String loggedScreen, boolean catchingUp) {
        Scene scene = ui.replayScene();
        if (scene == null || e == null) return Verdict.KEEP;
        switch (e.kind()) {
            case MOVE: return move(e, loggedScreen);
            case PRESS: return press(e, loggedScreen, catchingUp);
            case DRAG: return drag(e, loggedScreen);
            case RELEASE: return release(e, loggedScreen, catchingUp);
            case WHEEL: return wheel(e, loggedScreen);
            case KEY_DOWN: case KEY_UP: return key(e, loggedScreen);
            default: return Verdict.KEEP;       // the markers
        }
    }

    private Verdict judge(InputLog.Event e, String loggedScreen, boolean onDropNode) {
        boolean opens = false;
        if (e.kind() == InputLog.Kind.KEY_DOWN || e.kind() == InputLog.Kind.KEY_UP) {
            opens = e.code() == FollowRules.P ? ui.pOpensMenu() : ui.escapeOpensMenu();
        }
        return FollowRules.judge(e, loggedScreen, ui.currentScreen, ui.replayDialog() != null, ui.typingNow(), opens, onDropNode);
    }

    /* --------------------------------- the mouse --------------------------------- */

    private Verdict move(InputLog.Event e, String loggedScreen) {
        Verdict v = judge(e, loggedScreen, false);
        if (v != Verdict.KEEP) return v;
        Node hit = pick(e.x(), e.y());
        updateHover(hit, e);
        if (hit != null) fire(hit, mouse(MouseEvent.MOUSE_MOVED, hit, e.x(), e.y(), MouseButton.NONE, 0, 0, 0));
        return Verdict.KEEP;
    }

    private Verdict press(InputLog.Event e, String loggedScreen, boolean catchingUp) {
        if (gesture) abandonPress();
        Node hit = pick(e.x(), e.y());
        Verdict v = judge(e, loggedScreen, hit != null && onDropNode(hit));
        gesture = true;
        pressVerdict = v;
        pressTarget = null;
        if (v != Verdict.KEEP || hit == null) return v;
        updateHover(hit, e);
        pressTarget = hit;
        pressChain = chain(hit);
        pressButton = e.code();
        pressClicks = (int) e.a();
        pressMods = e.mods();
        pressX = e.x();
        pressY = e.y();
        still = true;
        if (pressButton == 1) for (Node n : pressChain) setFlag(SET_PRESSED, n, true);
        fire(hit, mouse(MouseEvent.MOUSE_PRESSED, hit, e.x(), e.y(), button(pressButton), pressClicks, pressMods, pressButton));
        if (catchingUp) relayout();
        return Verdict.KEEP;
    }

    private Verdict drag(InputLog.Event e, String loggedScreen) {
        if (gesture && pressVerdict != Verdict.KEEP) return pressVerdict;
        Verdict v = judge(e, loggedScreen, false);
        if (v != Verdict.KEEP || !gesture || pressTarget == null) return v;
        if (Math.abs(e.x() - pressX) > CLICK_SLOP || Math.abs(e.y() - pressY) > CLICK_SLOP) still = false;
        int b = e.code() == 0 ? pressButton : e.code();
        // The scene keeps picking the node under the pointer while a button is down and tells it enter and exit (a Button pressed and dragged off disarms), but the drag itself goes to the press target.
        updateHover(pick(e.x(), e.y()), e);
        fire(pressTarget, mouse(MouseEvent.MOUSE_DRAGGED, pressTarget, e.x(), e.y(), button(b), 0, pressMods, b));
        return Verdict.KEEP;
    }

    private Verdict release(InputLog.Event e, String loggedScreen, boolean catchingUp) {
        if (!gesture) return judge(e, loggedScreen, false);     // a release whose press was not seen: nothing to let go of
        Verdict pv = pressVerdict;
        Node target = pressTarget;
        List<Node> chain = pressChain;
        int clicks = pressClicks, mods = pressMods;
        boolean wasStill = still && Math.abs(e.x() - pressX) <= CLICK_SLOP && Math.abs(e.y() - pressY) <= CLICK_SLOP;
        forgetPress();
        // A dropped press has its release dropped; a press that was fired is always let go, so no node stays armed.
        if (pv != Verdict.KEEP) return pv;
        if (target == null) return Verdict.KEEP;
        Verdict v = judge(e, loggedScreen, false);
        int b = e.code() == 0 ? 1 : e.code();
        fire(target, mouse(MouseEvent.MOUSE_RELEASED, target, e.x(), e.y(), button(b), 0, e.mods(), 0, wasStill));
        Node hit = pick(e.x(), e.y());
        if (v == Verdict.KEEP && hit != null) {
            Node common = commonAncestor(chain, hit);
            if (common != null) {
                fire(common, mouse(MouseEvent.MOUSE_CLICKED, common, e.x(), e.y(), button(b), Math.max(1, clicks), mods, 0, wasStill));
            }
        }
        if (catchingUp) {
            relayout();
            hit = pick(e.x(), e.y());
        }
        updateHover(hit, e);
        return v;
    }

    /** A press whose release never came: let go of it where it was, with no click. */
    private void abandonPress() {
        Node target = pressTarget;
        InputLog.Event at = InputLog.Event.release(0, pressX, pressY, pressButton, pressMods);
        forgetPress();
        if (target != null) fire(target, mouse(MouseEvent.MOUSE_RELEASED, target, at.x(), at.y(), button(at.code()), 0, at.mods(), 0));
    }

    private Verdict wheel(InputLog.Event e, String loggedScreen) {
        Verdict v = judge(e, loggedScreen, false);
        if (v != Verdict.KEEP) return v;
        Node hit = pick(e.x(), e.y());
        if (hit == null) return Verdict.KEEP;
        Scene scene = ui.replayScene();
        Point2D screen = screenOf(scene, e.x(), e.y());
        int m = e.mods();
        fire(hit, new ScrollEvent(scene, hit, ScrollEvent.SCROLL, e.x(), e.y(), screen.getX(), screen.getY(),
                (m & InputLog.SHIFT) != 0, (m & InputLog.CTRL) != 0, (m & InputLog.ALT) != 0, (m & InputLog.META) != 0,
                false, false, e.a(), e.b(), e.a(), e.b(),
                ScrollEvent.HorizontalTextScrollUnits.NONE, 0, ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                0, new PickResult(hit, e.x(), e.y())));
        return Verdict.KEEP;
    }

    /** A mouse event for the scene's dispatch, from the scene at the scene point, aimed at {@code target}; the buttons down as {@code down} (the log's number). */
    private MouseEvent mouse(EventType<MouseEvent> type, Node target, double x, double y, MouseButton button, int clicks, int mods, int down) {
        return mouse(type, target, x, y, button, clicks, mods, down, true);
    }

    private MouseEvent mouse(EventType<MouseEvent> type, Node target, double x, double y, MouseButton button, int clicks, int mods, int down, boolean stillSincePress) {
        Scene scene = ui.replayScene();
        Point2D screen = screenOf(scene, x, y);
        return new MouseEvent(scene, target, type, x, y, screen.getX(), screen.getY(), button, clicks,
                (mods & InputLog.SHIFT) != 0, (mods & InputLog.CTRL) != 0, (mods & InputLog.ALT) != 0, (mods & InputLog.META) != 0,
                down == 1, down == 2, down == 3, false, false, stillSincePress, new PickResult(target, x, y));
    }

    private static Point2D screenOf(Scene scene, double x, double y) {
        double ox = scene.getWindow() == null ? 0 : scene.getWindow().getX() + scene.getX();
        double oy = scene.getWindow() == null ? 0 : scene.getWindow().getY() + scene.getY();
        return new Point2D(ox + x, oy + y);
    }

    private static MouseButton button(int logged) {
        return switch (logged) {
            case 1 -> MouseButton.PRIMARY;
            case 2 -> MouseButton.MIDDLE;
            case 3 -> MouseButton.SECONDARY;
            default -> MouseButton.NONE;
        };
    }

    /* ---------------------------------- the hover ---------------------------------- */

    /**
     * The nodes under the pointer are now those of {@code hit}: each node left is told (deepest first) and each entered is told
     * (outermost first), as the scene's own mouse handler does it. A node is told by MOUSE_EXITED_TARGET or MOUSE_ENTERED_TARGET
     * aimed at it, which its own handlers see as MOUSE_EXITED and MOUSE_ENTERED.
     */
    private void updateHover(Node hit, InputLog.Event at) {
        List<Node> now = hit == null ? new ArrayList<>() : chain(hit);
        Collections.reverse(now);               // outermost first
        int common = 0;
        while (common < hovered.size() && common < now.size() && hovered.get(common) == now.get(common)) common++;
        List<Node> was = hovered;
        hovered = now;
        for (int i = was.size() - 1; i >= common; i--) {
            Node n = was.get(i);
            setFlag(SET_HOVER, n, false);
            if (n.getScene() == ui.replayScene()) fire(n, mouse(MouseEvent.MOUSE_EXITED_TARGET, n, at.x(), at.y(), MouseButton.NONE, 0, 0, 0));
        }
        for (int i = common; i < now.size(); i++) {
            Node n = now.get(i);
            setFlag(SET_HOVER, n, true);
            fire(n, mouse(MouseEvent.MOUSE_ENTERED_TARGET, n, at.x(), at.y(), MouseButton.NONE, 0, 0, 0));
        }
    }

    /** {@code n} and its ancestors, deepest first. */
    private static List<Node> chain(Node n) {
        List<Node> out = new ArrayList<>();
        for (Node p = n; p != null; p = p.getParent()) out.add(p);
        return out;
    }

    /** The deepest node of the press's chain (deepest first) that is {@code hit} or one of its ancestors, or null. */
    private static Node commonAncestor(List<Node> pressChain, Node hit) {
        Set<Node> up = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Node p = hit; p != null; p = p.getParent()) up.add(p);
        for (Node n : pressChain) if (up.contains(n)) return n;
        return null;
    }

    /* ----------------------------------- the pick ----------------------------------- */

    /**
     * The deepest node of the window under the scene point, as the scene would pick it, or null. The layers of the window's stack
     * from the top down, except the excluded; the scene's root itself where no layer has the point.
     */
    Node pick(double x, double y) {
        if (ui.windowStack == null) return null;
        javafx.scene.layout.StackPane root = ui.windowStack;
        Point2D p = root.sceneToLocal(x, y);
        if (p == null) return null;
        List<Node> layers = inFront(root.getChildren());
        for (int i = layers.size() - 1; i >= 0; i--) {
            Node layer = layers.get(i);
            if (excluded.contains(layer)) continue;
            Node hit = pickIn(layer, p);
            if (hit != null) return hit;
        }
        return root.contains(p) ? root : null;
    }

    /** {@code n}, if the point (in its parent's coordinates) is on it and no child of it is deeper there; else null. */
    private static Node pickIn(Node n, Point2D inParent) {
        if (!n.isVisible() || n.isDisable() || n.isMouseTransparent()) return null;
        Point2D p = n.parentToLocal(inParent);
        if (p == null) return null;
        Node clip = n.getClip();
        if (clip != null && !clip.contains(clip.parentToLocal(p))) return null;
        if (n instanceof Parent parent) {
            Bounds b = n.getBoundsInLocal();
            if (p.getX() < b.getMinX() - BOUNDS_SLACK || p.getX() > b.getMaxX() + BOUNDS_SLACK
                    || p.getY() < b.getMinY() - BOUNDS_SLACK || p.getY() > b.getMaxY() + BOUNDS_SLACK) return null;
            List<Node> kids = inFront(parent.getChildrenUnmodifiable());
            for (int i = kids.size() - 1; i >= 0; i--) {
                Node hit = pickIn(kids.get(i), p);
                if (hit != null) return hit;
            }
        }
        return n.contains(p) ? n : null;
    }

    /** The children back to front: the order they are drawn in, which is their order, but by viewOrder (the higher the further back) where any is set. */
    private static List<Node> inFront(List<Node> children) {
        List<Node> out = new ArrayList<>(children);
        boolean ordered = false;
        for (Node n : out) if (n.getViewOrder() != 0) { ordered = true; break; }
        if (ordered) out.sort((a, b) -> Double.compare(b.getViewOrder(), a.getViewOrder()));      // stable: ties keep their order
        return out;
    }

    /** Whether the node, or any ancestor of it, is marked {@link FollowRules#DROP}. */
    static boolean onDropNode(Node n) {
        for (Node p = n; p != null; p = p.getParent()) {
            if (Boolean.TRUE.equals(p.getProperties().get(FollowRules.DROP))) return true;
        }
        return false;
    }

    private void relayout() {
        Parent root = ui.replayScene().getRoot();
        if (root == null) return;
        root.applyCss();
        root.layout();
    }

    /* ----------------------------------- the keys ----------------------------------- */

    private Verdict key(InputLog.Event e, String loggedScreen) {
        Verdict v = judge(e, loggedScreen, false);
        if (v != Verdict.KEEP) return v;
        KeyCode code = KEYS.get(e.code());
        if (code == null) return Verdict.KEEP;      // a key this toolkit has no name for
        Scene scene = ui.replayScene();
        int m = e.mods();
        boolean shift = (m & InputLog.SHIFT) != 0, ctrl = (m & InputLog.CTRL) != 0, alt = (m & InputLog.ALT) != 0, meta = (m & InputLog.META) != 0;
        String text = e.words() == null ? "" : e.words();
        boolean down = e.kind() == InputLog.Kind.KEY_DOWN;
        fire(target(scene), new KeyEvent(scene, target(scene), down ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                KeyEvent.CHAR_UNDEFINED, text, code, shift, ctrl, alt, meta));
        if (down) keysDown.put(code, m);
        else keysDown.remove(code);
        // The typed character follows a press that carries one (a printable key with no shortcut modifier).
        if (down && !text.isEmpty() && !ctrl && !alt && !meta && !Character.isISOControl(text.charAt(0))) {
            fire(target(scene), new KeyEvent(scene, target(scene), KeyEvent.KEY_TYPED, text, "", KeyCode.UNDEFINED, shift, ctrl, alt, meta));
        }
        return Verdict.KEEP;
    }

    /** Where a key goes: the focused control, or the scene's root where nothing has the focus (or the owner is one a redraw took out of the scene and no pulse has cleared yet). */
    private static Node target(Scene scene) {
        Node focus = scene.getFocusOwner();
        return focus != null && focus.getScene() == scene ? focus : scene.getRoot();
    }

    /* ---------------------------------- the firing ---------------------------------- */

    /** Dispatches the event through the node's chain (the scene's filters and handlers included), counted as firing. */
    private void fire(Node target, Event ev) {
        firing++;
        try {
            Event.fireEvent(target, ev);
        } catch (RuntimeException ex) {
            System.out.println("Follow: a re-enacted " + ev.getEventType().getName() + " raised " + ex);
        } finally {
            firing--;
        }
    }

    private static Method nodeSetter(String name) {
        try {
            Method m = Node.class.getDeclaredMethod(name, boolean.class);
            m.setAccessible(true);
            return m;
        } catch (Throwable t) {
            return null;
        }
    }

    private static void setFlag(Method setter, Node n, boolean on) {
        if (setter == null) return;
        try {
            setter.invoke(n, on);
        } catch (Throwable t) {
            // the platform would not: the flag stays as the handlers left it
        }
    }
}
