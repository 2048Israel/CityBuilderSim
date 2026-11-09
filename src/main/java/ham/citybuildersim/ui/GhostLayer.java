package ham.citybuildersim.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

/**
 * The ghost cursor (TR2, 2026-11-09): a see-through pointer arrow drawn at a scene point over the replay's window, in
 * {@link Palette#GHOST}, with a ripple at each click - a ring that grows and fades. One mouse-transparent pane of the window's
 * stack, kept just under the time bar; it never takes an event and the Reenactor never picks it. It knows where it stands
 * ({@link #x()}, {@link #y()}, {@link #isShown()}) without a pulse, so a harness can read it.
 *
 * <p>WHY (TR2): Jerus asked for "a ghost mouse if you go to a screen where the player was... along with their actions". The
 * player's pointer is not the viewer's, so it is drawn, not a real cursor; and it must not look like part of the game - the
 * accent, a verdict colour or an area's colour on it would read as the interface answering, so it has a colour of its own.
 * See-through, because it lies over the very controls the viewer is looking at. The ripples are advanced by the replay's frame
 * ({@link #frame}), not by an animation of their own, so the layer needs no toolkit pulse of its own to exist.
 */
final class GhostLayer {

    /** How opaque the arrow's body is, 0 to 1: 0.55, so the page shows through it and it still stands out on the stage's dark ground. */
    static final double FILL_ALPHA = 0.55;

    /** How opaque the arrow's outline is, 0 to 1: 0.9, so a see-through body still has an edge to find. */
    static final double EDGE_ALPHA = 0.9;

    /** How long a click's ripple lasts, in milliseconds: 400, long enough to be seen at 4x and short enough that two clicks never share a ring. */
    static final long RIPPLE_MS = 400;

    /** The radius a ripple starts at, in pixels: 4. */
    static final double RIPPLE_FROM_PX = 4;

    /** The radius a ripple has grown to when it fades out, in pixels: 30. */
    static final double RIPPLE_TO_PX = 30;

    /** How thick a ripple's ring is, in pixels: 2.5. */
    static final double RIPPLE_WIDTH_PX = 2.5;

    /** The most ripples shown at once, the oldest dropped: 12 - a burst of fast clicks at 4x. */
    static final int MAX_RIPPLES = 12;

    /** The pointer's outline with its tip at the origin, in pixels: the usual arrow, 19 px tall. */
    private static final String ARROW = "M0 0 L0 16 L4 12.5 L6.8 19 L9.2 18 L6.5 11.6 L11.5 11.6 Z";

    private static final class Ripple {
        final Circle ring;
        final long startNs;

        Ripple(Circle ring, long startNs) {
            this.ring = ring;
            this.startNs = startNs;
        }
    }

    private final Pane layer = new Pane();
    private final SVGPath arrow = new SVGPath();
    private final List<Ripple> ripples = new ArrayList<>();
    private boolean shown;
    private double x, y;

    GhostLayer() {
        layer.setMouseTransparent(true);
        layer.setPickOnBounds(false);
        arrow.setContent(ARROW);
        arrow.setFill(Color.web(Palette.GHOST, FILL_ALPHA));
        arrow.setStroke(Color.web(Palette.GHOST, EDGE_ALPHA));
        arrow.setStrokeWidth(1.2);
        arrow.setMouseTransparent(true);
        arrow.setManaged(false);
        arrow.setVisible(false);
        layer.getChildren().add(arrow);
    }

    /** The layer, to lay in the window's stack. */
    Node node() { return layer; }

    /** Whether the arrow is on view. */
    boolean isShown() { return shown; }

    /** Where the arrow stands, in scene pixels; meaningful while {@link #isShown()}. */
    double x() { return x; }

    double y() { return y; }

    /** The ripples on view now. */
    int ripples() { return ripples.size(); }

    /** The arrow's tip at the scene point (the window's stack lies at the scene's origin, so a scene point is the layer's own). */
    void show(double sceneX, double sceneY) {
        x = sceneX;
        y = sceneY;
        shown = true;
        arrow.setLayoutX(sceneX);
        arrow.setLayoutY(sceneY);
        arrow.setVisible(true);
    }

    /** A ring begun at the scene point: it grows and fades over RIPPLE_MS (frame()). */
    void ripple(double sceneX, double sceneY) {
        Circle ring = new Circle(RIPPLE_FROM_PX);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(Color.web(Palette.GHOST));
        ring.setStrokeWidth(RIPPLE_WIDTH_PX);
        ring.setMouseTransparent(true);
        ring.setManaged(false);
        ring.setLayoutX(sceneX);
        ring.setLayoutY(sceneY);
        layer.getChildren().add(0, ring);
        ripples.add(new Ripple(ring, System.nanoTime()));
        while (ripples.size() > MAX_RIPPLES) layer.getChildren().remove(ripples.remove(0).ring);
    }

    /** The arrow and every ripple gone from view. */
    void hide() {
        shown = false;
        arrow.setVisible(false);
        for (Ripple r : ripples) layer.getChildren().remove(r.ring);
        ripples.clear();
    }

    /** One frame: every ripple moved along its life, the ones that have run out taken off. */
    void frame(long nowNs) {
        for (int i = ripples.size() - 1; i >= 0; i--) {
            Ripple r = ripples.get(i);
            double t = (nowNs - r.startNs) / (RIPPLE_MS * 1_000_000.0);
            if (t >= 1) {
                layer.getChildren().remove(r.ring);
                ripples.remove(i);
                continue;
            }
            t = Math.max(0, t);
            double eased = 1 - (1 - t) * (1 - t);
            r.ring.setRadius(RIPPLE_FROM_PX + (RIPPLE_TO_PX - RIPPLE_FROM_PX) * eased);
            r.ring.setOpacity(1 - t);
        }
    }
}
