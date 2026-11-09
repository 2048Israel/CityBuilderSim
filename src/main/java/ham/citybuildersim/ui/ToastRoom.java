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
 * page, so it is there only while a toast is. A page that grows by a padding while the player is part way
 * down it does not jump: the ScrollPane keeps the PIXEL offset itself when its content changes height
 * (ToastCheck 3 holds it, so a toolkit that stopped would be heard), and this does not move the scroll - a
 * first version did, to the fraction the new height wanted, before the layout had given the page that height,
 * and put the page 15 px out. What it does is tell the window the fraction the page will be at once laid out
 * (placeAt): the window keeps the player's place as a fraction (UserInterface.pageScrollAt) and a rebuild
 * restores it from there, so a toast arriving with the rebuild would otherwise put the page back at the
 * fraction of the shorter page. Screens only: nothing in the model reads this.
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
    private final DoubleConsumer placeAt;
    private final Insets base;

    /** What the page's foot gives now, in pixels. */
    private double room;

    /** True from a toast coming or going until the fit it asked for has run. */
    private boolean asked;

    /**
     * @param scroller the page's scroller, which the toasts float over
     * @param page     the scroller's content; its padding when built is the page's own, and the room is added under it
     * @param stack    the toasts' stack, set in a StackPane over the scroller with a margin (the corner)
     * @param placeAt  told the fraction of the way down the page will be at once it is laid out with the room
     *                 (or without it): the window keeps that number as the player's place; it moves nothing
     */
    public ToastRoom(ScrollPane scroller, Region page, Pane stack, DoubleConsumer placeAt) {
        this.scroller = scroller;
        this.page = page;
        this.stack = stack;
        this.placeAt = placeAt;
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
     * Give the page the room the toasts want, and say where the page will be (the pixel it is at, as a fraction of
     * the new length). Public for the harness, which runs it where the window runs it, on the FX thread; the window never calls it.
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
        placeAt.accept(span > 1 ? Math.min(1, offset / span) : 0);
    }
}
