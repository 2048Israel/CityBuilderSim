package ham.citybuildersim.ui;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.function.DoubleConsumer;

/**
 * The corner a toast stands in, given up by the page (TS1): while a toast shows, the page's foot takes a
 * bottom padding as tall as the toasts and the corner they are set in, its pixel offset kept, and gives it
 * back when the last toast goes.
 *
 * WHY THIS EXISTS. The toasts float over the page in the stage's bottom right (UserInterface, TOASTS), and a
 * control can pass under one: on the Build overview (1,067 px, scrolling) "Turn automatic building off" sits
 * mid-page and went under the 33 px toast for about 50 px of scrolling (PT1, item 10,
 * PT1/build-overview-toast-covers-button.png). A padding that is always there only adds scroll after the
 * page, so it is there only while a toast is; and a page that grows by a padding while the player is part
 * way down it must not jump, so the scroll is moved to the pixel it was at (a ScrollPane keeps the
 * FRACTION of the way down, and a longer page puts that fraction further on). Screens only: nothing in the
 * model reads this.
 *
 * <p>What it watches is the stack itself: its toasts coming and going, its being hidden (a game menu) and
 * its being taken out of the window (cinematic mode). Any of the three asks for a fit, and the fit is made
 * once, after what asked has finished - a toast arrives inside clearMenu(), while the page is emptied, and
 * measured then the page would be nothing. The fit asks the page what it WANTS to be, as the wheel does
 * (UserInterface.pageSpan: prefHeight needs no layout), before and after the padding changes, so it is right
 * on the first frame and does not wait for a layout pass.
 */
public final class ToastRoom {

    private final ScrollPane scroller;
    private final Region page;
    private final Pane stack;
    private final DoubleConsumer moveTo;
    private final Insets base;

    /** What the page's foot gives now, in pixels. */
    private double room;

    /** True from a toast coming or going until the fit it asked for has run. */
    private boolean asked;

    /**
     * @param scroller the page's scroller, which the toasts float over
     * @param page     the scroller's content; its padding when built is the page's own, and the room is added under it
     * @param stack    the toasts' stack, set in a StackPane over the scroller with a margin (the corner)
     * @param moveTo   how the window moves the page: it is given the fraction of the way down to put it at
     *                 (the window keeps that number as the player's place, so it must hear this one)
     */
    public ToastRoom(ScrollPane scroller, Region page, Pane stack, DoubleConsumer moveTo) {
        this.scroller = scroller;
        this.page = page;
        this.stack = stack;
        this.moveTo = moveTo;
        this.base = page.getPadding();
        stack.getChildren().addListener((ListChangeListener<javafx.scene.Node>) c -> ask());
        stack.visibleProperty().addListener((o, was, now) -> ask());
        stack.parentProperty().addListener((o, was, now) -> ask());
        ask();
    }

    /** What the page's foot gives now: the toasts' height and the corner's, or nothing while no toast is on show. */
    public double room() {
        return room;
    }

    /** What it should give: the stack's height (asked, not measured) and the margin under it, or nothing. */
    double wanted() {
        if (stack.getParent() == null || !stack.isVisible() || stack.getChildren().isEmpty()) return 0;
        Insets corner = StackPane.getMargin(stack);
        // A toast just added has not been styled yet (the pulse does it), and unstyled it has neither its padding nor its font.
        stack.applyCss();
        return Math.ceil(stack.prefHeight(-1)) + (corner == null ? 0 : corner.getBottom());
    }

    /** A toast came or went, or the stack was shown, hidden or moved: fit once, when what is in flight has finished. */
    private void ask() {
        if (asked) return;
        asked = true;
        Platform.runLater(this::fit);
    }

    /**
     * Give the page the room the toasts want, and put the scroll back to the pixel it was at. Public for the
     * harness, which runs it where the window runs it, on the FX thread; the window never calls it.
     */
    public void fit() {
        asked = false;
        double want = wanted();
        if (Math.abs(want - room) < 0.5) return;

        double view = scroller.getViewportBounds() == null ? 0 : scroller.getViewportBounds().getHeight();
        double width = page.getWidth() > 0 ? page.getWidth() : scroller.getViewportBounds() == null ? 0 : scroller.getViewportBounds().getWidth();
        double w = width > 0 ? width : -1;
        double offset = scroller.getVvalue() * Math.max(0, page.prefHeight(w) - view);

        room = want;
        page.setPadding(new Insets(base.getTop(), base.getRight(), base.getBottom() + room, base.getLeft()));

        if (view <= 0) return;   // nothing laid out yet: there is no place to keep
        double span = page.prefHeight(w) - view;
        double at = span > 1 ? Math.min(1, offset / span) : 0;
        if (Math.abs(at - scroller.getVvalue()) > 1e-9) moveTo.accept(at);
    }
}
