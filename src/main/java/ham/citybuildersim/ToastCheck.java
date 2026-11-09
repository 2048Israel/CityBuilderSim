package ham.citybuildersim;

import ham.citybuildersim.ui.ToastRoom;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A toast stays out of the page's way (TS1): while a toast shows, the page's foot takes a bottom padding as
 * tall as the toasts, the scroll's pixel offset is kept as the toast comes and goes, and the last control of a
 * page scrolled to its end is clear of the toast.
 *
 * WHY THIS EXISTS. The toasts float over the page in the stage's bottom-right corner, and on the Build
 * overview (1,067 px, scrolling) "Turn automatic building off" sits mid-page and went under the 33 px toast
 * for about 50 px of scrolling (PT1, item 10). A bottom inset that is always there only adds scroll after the
 * page, so the page gives the corner up only while a toast stands in it (ToastRoom); and a ScrollPane keeps
 * the FRACTION of the way down, so a page that grows by a padding while the player is part way down it would
 * jump unless the scroll is moved to the pixel it was at. The window cannot be used for it (it opens
 * maximised, and the suite runs beside the player's game), so this lays the same nodes out on a scene of its
 * own, as the window builds them (a scroller over a stack, the page's minimum height bound to the viewport's,
 * the toasts a VBox in the bottom right with the window's margin), and reads where they land. Nothing in the
 * model is involved. The FX toolkit must be up: it is skipped, with a note, where it cannot start.
 *
 * What it has to prove:
 *
 *   1. THE FIXTURE CAUSES THE CONDITION. With no room made (the page as it was), a page scrolled to its end
 *      with a toast up has its last control under the toast's bounds, at one toast and at three.
 *   2. THE LAST CONTROL IS CLEAR. With the room made, the same page scrolled to its end has its last control
 *      clear of the toasts' bounds and inside the viewport, at one toast, at three, and at three with the
 *      first one wrapped to three lines; the padding is the stack's height and its corner's margin.
 *   3. THE PIXEL OFFSET IS KEPT. A page scrolled to a third and to two thirds of the way down, and to the top,
 *      is at the same pixel offset (within a pixel) after a toast comes, after a second and a third come, and
 *      after each of them goes; and the window is told the fraction the page ends at, which is what it
 *      restores the player's place from after a rebuild.
 *   4. IT IS GIVEN BACK. When the last toast goes the page's padding is its own again and the page is as
 *      tall as it was; with two of three gone the padding is that of the one left.
 *   5. A STACK THAT IS NOT ON SHOW GIVES NOTHING. Hidden (a game menu) or taken out of the window (cinematic
 *      mode), the stack with its toasts in it takes no room; shown or put back, it does.
 *   6. A PAGE THAT FITS IS NOT MOVED. A page shorter than the viewport is at the top with a toast up, and does
 *      not grow past the viewport.
 *   7. A TOAST THAT ARRIVES WITH THE REBUILD is fitted to the rebuilt page: the page emptied and drawn again
 *      in one pulse with a toast added keeps the offset it was at, and its end is clear of the toast.
 *   8. THE WINDOW BUILDS IT. UserInterface makes a ToastRoom of its own scroller, its page and its stack of
 *      toasts, and keeps what it is told in the field the rebuild restores the player's place from.
 */
public class ToastCheck {

    private static int fails;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    static String px(double v) { return String.format("%.1f", v); }

    /** The window the rig is laid out in: a laptop's. */
    static final double WIDTH = 1280, HEIGHT = 720;

    /** The rows above the last control that make the page 1,067 px or so tall, like the Build overview. */
    static final int TALL_ROWS = 20;

    /** The window's page padding (UserInterface.rootMenu) and toast margin, which the rig copies. */
    static final Insets PAGE_PADDING = new Insets(8, 0, 14, 0);
    static final Insets CORNER = new Insets(0, 24, 16, 0);

    /**
     * How far the pixel offset may stray as toasts come and go. The scroller keeps the offset through a change
     * of height by way of a fraction and snaps it to whole pixels at every layout, so a run of five changes
     * drifts a pixel or two (measured: none at the top, one at three toasts, two at the last going); a page
     * that was put at the fraction of the longer page by hand missed by 15 px and more.
     */
    static final double WITHIN = 2.0;

    /** The window's page, scroller and toast stack on a scene of their own. */
    static final class Rig {
        final ScrollPane scroller = new ScrollPane();
        final VBox page = new VBox(10);
        final VBox stack = new VBox(6);
        final StackPane root;
        final Scene scene;
        final List<Button> rows = new ArrayList<>();
        final Button last = new Button("Turn automatic building off");
        final ToastRoom room;
        /** The fraction of the way down the window was last told the page would be at (the field a rebuild restores it from). */
        double wrote = Double.NaN;

        Rig(boolean withRoom, int rowCount) {
            page.setAlignment(Pos.TOP_CENTER);
            page.setPadding(PAGE_PADDING);
            fill(rowCount);
            scroller.setContent(page);
            scroller.setFitToWidth(true);
            scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            page.minHeightProperty().bind(scroller.heightProperty().subtract(4));
            stack.setAlignment(Pos.BOTTOM_RIGHT);
            stack.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            stack.setPickOnBounds(false);
            StackPane.setAlignment(stack, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(stack, CORNER);
            root = new StackPane(scroller, stack);
            scene = new Scene(root, WIDTH, HEIGHT);
            room = withRoom ? new ToastRoom(scroller, page, stack, at -> wrote = at) : null;
            relayout();
        }

        /** The page's rows, a full-width last control after them (the finding's button sat where a toast passed). */
        void fill(int rowCount) {
            rows.clear();
            page.getChildren().clear();
            for (int i = 0; i < rowCount; i++) {
                Button row = new Button("Row " + i);
                row.setPrefHeight(40);
                row.setMinHeight(40);
                row.setMaxWidth(300);
                rows.add(row);
                page.getChildren().add(row);
            }
            last.setPrefHeight(40);
            last.setMinHeight(40);
            last.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(last, Priority.NEVER);
            page.getChildren().add(last);
        }

        /** What the toolkit would do in a pulse: styles and layout, twice, since the scroll bar changes the width. */
        void relayout() {
            for (int i = 0; i < 2; i++) {
                root.applyCss();
                root.layout();
            }
        }

        /** A toast like the window's: bold 12.5 px text wrapped at the window's width, padded 7 10 7 10. */
        Label toast(String text) {
            Label t = new Label(text);
            t.setWrapText(true);
            t.setMaxWidth(340);
            t.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-padding: 7 10 7 10;");
            return t;
        }

        /** The pixels the page is scrolled down by: the first row's distance above the viewport's top. */
        double offset() {
            Node first = rows.isEmpty() ? last : rows.get(0);
            return scroller.localToScene(0, 0).getY() - first.localToScene(0, 0).getY();
        }

        /** Whether the last control and the toasts' stack overlap on the screen. */
        boolean covered() {
            Bounds c = last.localToScene(last.getBoundsInLocal()), s = stack.localToScene(stack.getBoundsInLocal());
            return stack.getChildren().size() > 0
                    && c.getMaxY() > s.getMinY() && c.getMinY() < s.getMaxY()
                    && c.getMaxX() > s.getMinX() && c.getMinX() < s.getMaxX();
        }

        double footOfLast() { return last.localToScene(last.getBoundsInLocal()).getMaxY(); }

        double topOfStack() { return stack.localToScene(stack.getBoundsInLocal()).getMinY(); }

        double padding() { return page.getPadding().getBottom(); }
    }

    /** Everything the queue of the FX thread holds has run, and the scene is laid out: what a pulse does after a toast. */
    static void settle(Rig rig) throws Exception {
        KeptCheck.fx(() -> {
            rig.relayout();
            return null;
        });
    }

    /** Scrolled to a fraction of the way down, laid out. */
    static void scrollTo(Rig rig, double v) throws Exception {
        KeptCheck.fx(() -> {
            rig.scroller.setVvalue(v);
            rig.relayout();
            return null;
        });
    }

    /** A toast arrives: added to the stack on the FX thread, as the window does, then settled. */
    static Label arrive(Rig rig, String text) throws Exception {
        Label t = KeptCheck.fx(() -> {
            Label made = rig.toast(text);
            rig.stack.getChildren().add(made);
            return made;
        });
        settle(rig);
        return t;
    }

    static void leave(Rig rig, Label t) throws Exception {
        KeptCheck.fx(() -> rig.stack.getChildren().remove(t));
        settle(rig);
    }

    static final String SHORT = "[!]  Crime is running above Canada's";
    static final String LONG = "[!]  The harbour's grain stores are running below a month of what the city eats, and the next ship is not due for some time";

    public static void main(String[] args) throws Exception {
        System.out.println("=== ToastCheck: a toast stays out of the page's way (TS1) ===");
        String down = KeptCheck.startJavaFx();
        if (down != null) {
            System.out.println("skipped: the JavaFX toolkit cannot start here (" + down + "); the page needs it to be laid out");
            System.exit(0);
        }

        System.out.println("--- 1. the fixture causes the condition: without room, the last control is under the toast ---");
        {
            Rig bare = KeptCheck.fx(() -> new Rig(false, TALL_ROWS));
            double tall = bare.page.getHeight();
            check("the page is taller than the window (it scrolls)", tall > HEIGHT + 100, px(tall) + " px in " + (int) HEIGHT);
            arrive(bare, SHORT);
            scrollTo(bare, 1);
            check("a toast up, the page at its end, the last control is under the toast's bounds", bare.covered(),
                    "control to " + px(bare.footOfLast()) + ", toast from " + px(bare.topOfStack()));
            arrive(bare, SHORT);
            arrive(bare, SHORT);
            scrollTo(bare, 1);
            check("...and with three toasts up", bare.covered(), "control to " + px(bare.footOfLast()) + ", toasts from " + px(bare.topOfStack()));
        }

        System.out.println("--- 2. with room made, the last control of a page at its end is clear ---");
        {
            Rig rig = KeptCheck.fx(() -> new Rig(true, TALL_ROWS));
            double basePad = rig.padding();
            check("no toast: the page's padding is its own and no room is made", Math.abs(basePad - PAGE_PADDING.getBottom()) < 0.01 && rig.room.room() == 0,
                    "padding " + px(basePad) + ", room " + px(rig.room.room()));
            Label one = arrive(rig, SHORT);
            scrollTo(rig, 1);
            check("one toast: the padding is the stack's height and the corner's margin", Math.abs(rig.room.room() - (Math.ceil(rig.stack.getHeight()) + CORNER.getBottom())) < 1.0,
                    "room " + px(rig.room.room()) + ", stack " + px(rig.stack.getHeight()) + " + " + px(CORNER.getBottom()));
            check("one toast: the page's padding is its own and the room", Math.abs(rig.padding() - (basePad + rig.room.room())) < 0.01, px(rig.padding()));
            check("one toast: the last control is clear of the toast", !rig.covered(),
                    "control to " + px(rig.footOfLast()) + ", toast from " + px(rig.topOfStack()));
            check("one toast: ...and inside the viewport", rig.footOfLast() <= HEIGHT, px(rig.footOfLast()) + " of " + (int) HEIGHT);
            arrive(rig, SHORT);
            arrive(rig, SHORT);
            scrollTo(rig, 1);
            check("three toasts: the last control is clear of them", !rig.covered(),
                    "control to " + px(rig.footOfLast()) + ", toasts from " + px(rig.topOfStack()));
            check("three toasts: the room grew with the stack", Math.abs(rig.room.room() - (Math.ceil(rig.stack.getHeight()) + CORNER.getBottom())) < 1.0
                    && rig.room.room() > 3 * 30, "room " + px(rig.room.room()) + ", stack " + px(rig.stack.getHeight()));

            Rig wrapped = KeptCheck.fx(() -> new Rig(true, TALL_ROWS));
            Label tall = arrive(wrapped, LONG);
            arrive(wrapped, SHORT);
            arrive(wrapped, SHORT);
            scrollTo(wrapped, 1);
            check("a wrapped toast (" + px(tall.getHeight()) + " px) is taller than a short one", tall.getHeight() > 33 * 1.5, px(tall.getHeight()));
            check("three toasts with the first wrapped: the last control is clear of them", !wrapped.covered(),
                    "control to " + px(wrapped.footOfLast()) + ", toasts from " + px(wrapped.topOfStack()));
        }

        System.out.println("--- 3. the scroll's pixel offset is kept as a toast comes and goes ---");
        for (double v : new double[]{0, 1.0 / 3, 2.0 / 3}) {
            Rig rig = KeptCheck.fx(() -> new Rig(true, TALL_ROWS));
            scrollTo(rig, v);
            double at = rig.offset();
            String where = "at " + px(at) + " px (" + px(v * 100) + "% of the way)";
            Label a = arrive(rig, SHORT);
            check("a toast comes " + where + ": the page has not moved", Math.abs(rig.offset() - at) <= WITHIN, "now " + px(rig.offset()));
            double span = rig.page.getHeight() - rig.scroller.getViewportBounds().getHeight();
            check("...and the window is told the fraction the page is at, to the pixel", Math.abs(rig.wrote - rig.scroller.getVvalue()) * span <= 1.0,
                    "told " + String.format("%.5f", rig.wrote) + ", at " + String.format("%.5f", rig.scroller.getVvalue()) + " of " + px(span) + " px");
            Label b = arrive(rig, SHORT);
            Label c = arrive(rig, LONG);
            check("...a second and a third come", Math.abs(rig.offset() - at) <= WITHIN, "now " + px(rig.offset()));
            leave(rig, c);
            check("...the third goes", Math.abs(rig.offset() - at) <= WITHIN, "now " + px(rig.offset()));
            leave(rig, a);
            check("...the first goes", Math.abs(rig.offset() - at) <= WITHIN, "now " + px(rig.offset()));
            leave(rig, b);
            check("...and the last goes, the page where it was", Math.abs(rig.offset() - at) <= WITHIN, "now " + px(rig.offset()));
        }
        {
            // At the very end the padding going leaves less to scroll: the page can only be where the shorter page ends.
            Rig rig = KeptCheck.fx(() -> new Rig(true, TALL_ROWS));
            Label a = arrive(rig, SHORT);
            scrollTo(rig, 1);
            double end = rig.offset();
            leave(rig, a);
            double span = rig.page.getHeight() - rig.scroller.getViewportBounds().getHeight();
            check("at the end of a page with a toast, the toast going leaves the page no further than its end", rig.offset() <= span + WITHIN && rig.offset() <= end + WITHIN,
                    "offset " + px(rig.offset()) + " of " + px(span));
        }

        System.out.println("--- 4. the room is given back when the last toast goes ---");
        {
            Rig rig = KeptCheck.fx(() -> new Rig(true, TALL_ROWS));
            double height = rig.page.getHeight();
            Label a = arrive(rig, SHORT), b = arrive(rig, SHORT), c = arrive(rig, SHORT);
            double three = rig.room.room();
            leave(rig, c);
            leave(rig, b);
            check("two of three gone: the room is that of the one left", rig.room.room() < three && rig.room.room() > 0, px(rig.room.room()) + " of " + px(three));
            leave(rig, a);
            check("the last gone: no room", rig.room.room() == 0, px(rig.room.room()));
            check("...the page's padding is its own again", Math.abs(rig.padding() - PAGE_PADDING.getBottom()) < 0.01, px(rig.padding()));
            check("...and the page is as tall as it was", Math.abs(rig.page.getHeight() - height) <= WITHIN, px(rig.page.getHeight()) + " of " + px(height));
        }

        System.out.println("--- 5. a stack that is not on show takes no room ---");
        {
            Rig rig = KeptCheck.fx(() -> new Rig(true, TALL_ROWS));
            arrive(rig, SHORT);
            double room = rig.room.room();
            check("shown, with a toast: there is room", room > 0, px(room));
            KeptCheck.fx(() -> { rig.stack.setVisible(false); rig.stack.setManaged(false); return null; });
            settle(rig);
            check("hidden (a game menu): the room is given back", rig.room.room() == 0, px(rig.room.room()));
            KeptCheck.fx(() -> { rig.stack.setVisible(true); rig.stack.setManaged(true); return null; });
            settle(rig);
            check("shown again: the room is made again", Math.abs(rig.room.room() - room) < 1.0, px(rig.room.room()));
            KeptCheck.fx(() -> rig.root.getChildren().remove(rig.stack));
            settle(rig);
            check("taken out of the window (cinematic mode): the room is given back", rig.room.room() == 0, px(rig.room.room()));
            KeptCheck.fx(() -> rig.root.getChildren().add(rig.stack));
            settle(rig);
            check("put back: the room is made again", Math.abs(rig.room.room() - room) < 1.0, px(rig.room.room()));
        }

        System.out.println("--- 6. a page that fits is not moved ---");
        {
            Rig rig = KeptCheck.fx(() -> new Rig(true, 3));
            double view = rig.scroller.getViewportBounds().getHeight();
            check("the page is shorter than the window without a toast", rig.page.getHeight() <= view + WITHIN, px(rig.page.getHeight()) + " of " + px(view));
            double at = rig.offset();
            arrive(rig, SHORT);
            arrive(rig, LONG);
            check("a toast up: the page is at the top and has not moved", rig.scroller.getVvalue() == 0 && Math.abs(rig.offset() - at) <= WITHIN, "was " + px(at) + ", now " + px(rig.offset()));
            check("...and has not grown past the viewport", rig.page.getHeight() <= view + WITHIN, px(rig.page.getHeight()) + " of " + px(view));
            check("...and the last control is clear of the toast", !rig.covered(), "control to " + px(rig.footOfLast()) + ", toasts from " + px(rig.topOfStack()));
        }

        System.out.println("--- 7. a toast that arrives with the page's rebuild is fitted to the rebuilt page ---");
        {
            Rig rig = KeptCheck.fx(() -> new Rig(true, TALL_ROWS));
            scrollTo(rig, 0.5);
            double at = rig.offset();
            // One pulse, as clearMenu() and a screen's draw are: the page emptied, a toast raised, the page drawn again.
            KeptCheck.fx(() -> {
                rig.page.getChildren().clear();
                rig.stack.getChildren().add(rig.toast(SHORT));
                rig.fill(TALL_ROWS);
                return null;
            });
            settle(rig);
            check("the rebuild with a toast: the page is where it was", Math.abs(rig.offset() - at) <= WITHIN, "was " + px(at) + ", now " + px(rig.offset()));
            check("...with the room made against the rebuilt page", rig.room.room() > 0 && rig.page.getHeight() > HEIGHT + 100, "room " + px(rig.room.room()));
            scrollTo(rig, 1);
            check("...and its end is clear of the toast", !rig.covered(), "control to " + px(rig.footOfLast()) + ", toast from " + px(rig.topOfStack()));
        }

        System.out.println("--- 8. the window builds it ---");
        {
            Path window = Path.of("src", "main", "java", "ham", "citybuildersim", "ui", "UserInterface.java");
            if (!Files.isRegularFile(window)) {
                System.out.println("  note: " + window + " is not here (the harness runs from the repository's root); not read");
            } else {
                String src = Files.readString(window);
                check("UserInterface makes a ToastRoom of its scroller, its page and its stack of toasts",
                        Pattern.compile("new ToastRoom\\(\\s*menuScroller,\\s*rootMenu,\\s*toastStack,").matcher(src).find(), null);
                check("...and keeps what it is told in pageScrollAt, the field the rebuild restores the player's place from",
                        Pattern.compile("new ToastRoom\\(\\s*menuScroller,\\s*rootMenu,\\s*toastStack,\\s*at\\s*->\\s*pageScrollAt\\s*=\\s*at\\)").matcher(src).find(), null);
            }
        }

        System.out.println(fails == 0 ? "\nToastCheck: all ok" : "\nToastCheck: " + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
