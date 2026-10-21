package ham.citybuildersim.ui;

import ham.citybuildersim.Auction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The highest yield a sale will take, as three chips and a yield box: fair plus
 * a point (the default), a yield the player names, or Fill it (CD2, D28).
 *
 * WHY (CD2, slice 9): Borrow had these chips as a block inside FinancesScreen,
 * bound to the one field the page keeps. The rollover's card, automatic
 * building's panel and the build screen's funding card each carry the same
 * three chips for a cap of their own (Jerus, 2026-10-12: "each its own"), so
 * the block became a piece: it reads and sets the cap through two lambdas and
 * keeps its own typing, so each screen binds it to whichever cap it owns and
 * the four look and behave alike. Nothing here is the model's: the cap is
 * Auction.Cap and the words for it are CapSetting's and the card's.
 *
 * One instance for each cap on a screen. The yield box is the house's search
 * box, kept through a month's redraw: a screen calls noteFocus() before it
 * rebuilds, so a box that had the focus takes it back.
 */
final class CapChips {

    private final Supplier<Auction.Cap> cap;
    private final Consumer<Auction.Cap> setCap;
    private final Runnable redraw;
    private final Runnable redrawSoon;

    /** What is typed in the yield box, in per cent, and not yet set; null while the box shows the cap as it stands. */
    private String typed;
    /** What the last yield that could not be read says, under the chips until the cap is next set; null for none. */
    private String refused;
    /** The yield box, and whether it had the focus when the page was last redrawn - a month landing rebuilds it under the typing. */
    private TextField field;
    private boolean typing;

    /**
     * @param cap        the cap as it stands
     * @param setCap     how the player sets it
     * @param redraw     draws the page again at once (a chip, or Enter in the box)
     * @param redrawSoon draws it on the next frame no button is held (leaving the box)
     */
    CapChips(Supplier<Auction.Cap> cap, Consumer<Auction.Cap> setCap, Runnable redraw, Runnable redrawSoon) {
        this.cap = cap;
        this.setCap = setCap;
        this.redraw = redraw;
        this.redrawSoon = redrawSoon;
    }

    /** Called as a screen starts to rebuild: whether the yield box has the focus now, so the new box takes it back. */
    void noteFocus() {
        typing = field != null && field.isFocused();
    }

    /** The chips' names, in order: the default tail, a named yield, Fill it. The second says the yield once it is set. */
    String[] names() {
        double points = Auction.MAX_TAIL * 100;
        Auction.Cap c = cap.get();
        return new String[] {
            "fair + " + (points == Math.rint(points) ? String.valueOf((int) Math.rint(points)) : String.format("%.1f", points)) + " pt",
            c.mode() == Auction.Mode.YIELD ? "at " + FinancesScreen.pct3(c.value()) : "at …",
            "Fill it" };
    }

    /**
     * The block: a caption with its (i), the three chips, the box for a yield
     * in per cent, and what could not be read. `fair` is what the paper is
     * worth now: "at …" starts a point over it when picked; `exact` says it is
     * this sale's own fair, so a yield under it can be said to read as fair
     * (a rollover or a builder sells several papers, and has no one fair).
     */
    List<Node> controls(String title, String info, double fair, boolean exact) {
        final double seed = fair + Auction.MAX_TAIL;
        Auction.Cap now = cap.get();
        String[] names = names();
        String current = switch (now.mode()) {
            case TAIL  -> names[0];
            case YIELD -> names[1];
            case FILL  -> names[2];
        };
        FlowPane chips = chipStrip(names, current, Palette.SIZE_LABEL, pick -> {
            typed = null;
            refused = null;
            field = null;
            if (pick.equals(names[0])) setCap.accept(Auction.Cap.DEFAULT);
            else if (pick.equals(names[2])) setCap.accept(Auction.Cap.FILL);
            else if (cap.get().mode() != Auction.Mode.YIELD) setCap.accept(Auction.Cap.at(seed));
            redraw.run();
        });
        chips.setAlignment(Pos.CENTER_LEFT);

        String shown = typed != null ? typed : now.mode() == Auction.Mode.YIELD ? yieldText(now) : "";
        TextField box = searchBox(shown, "type a yield, 4.5", 150, t -> typed = t, null);
        box.setOnAction(e -> commit(box, true));
        box.focusedProperty().addListener((o, was, isNow) -> {
            if (isNow) return;
            javafx.application.Platform.runLater(() -> {
                if (box.getScene() != null && !box.isFocused()) commit(box, false);
            });
        });
        boolean again = typing;
        typing = false;
        field = box;
        if (again) {
            javafx.application.Platform.runLater(() -> {
                box.requestFocus();
                box.positionCaret(box.getText().length());
            });
        }
        HBox entry = new HBox(Palette.GAP, box,
                FinancesScreen.words("% a year · Enter sets it", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        entry.setAlignment(Pos.CENTER_LEFT);

        List<Node> out = new ArrayList<>();
        out.add(FinancesScreen.caption(title, info));
        out.add(chips);
        out.add(entry);
        if (refused != null) out.add(FinancesScreen.words(refused, Palette.SIZE_LABEL, Palette.BAD));
        if (exact && now.mode() == Auction.Mode.YIELD && now.value() < fair - 1e-9) {
            out.add(FinancesScreen.muted("Nobody bids under fair (" + FinancesScreen.pct3(fair) + "), so this reads as fair."));
        }
        return out;
    }

    /** A named yield as the box shows it, in per cent to three places: 0.04602 is "4.602". */
    static String yieldText(Auction.Cap cap) {
        return String.format("%.3f", cap.value() * 100);
    }

    /** A typed yield, in per cent ("4.5" or "4.5%"), as the yield it names; NaN when it is not one between nothing and a hundred per cent. Pure. */
    static double fromWords(String typed) {
        if (typed == null) return Double.NaN;
        String t = typed.trim();
        if (t.endsWith("%")) t = t.substring(0, t.length() - 1).trim();
        try {
            double percent = Double.parseDouble(t.replace(',', '.'));
            return percent > 0 && percent < 100 ? percent / 100 : Double.NaN;
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    /** What the box says under the chips when an entry cannot be read. Pure. */
    static String refusal(String typed) {
        return "“" + typed.trim() + "” is not a yield: a number of per cent, 4.5 or 4.5%. The highest yield stays as it was.";
    }

    /**
     * What is in the yield box, set as the cap - or, when it cannot be read,
     * said under the chips with the cap left as it was; an empty box, or the
     * cap's own figure left in it, changes nothing. Enter draws the page at
     * once; leaving the box asks for a redraw on the next free frame
     * (UserInterface.redrawSoon()).
     */
    private void commit(TextField box, boolean enter) {
        String text = box.getText() == null ? "" : box.getText().trim();
        Auction.Cap now = cap.get();
        boolean changed;
        if (text.isEmpty() || (now.mode() == Auction.Mode.YIELD && text.equals(yieldText(now)))) {
            changed = refused != null;
            typed = null;
            refused = null;
        } else {
            double y = fromWords(text);
            if (Double.isNaN(y)) {
                changed = !refusal(text).equals(refused);
                refused = refusal(text);
                typed = text;
            } else {
                changed = true;
                setCap.accept(Auction.Cap.at(y));
                typed = null;
                refused = null;
            }
        }
        field = null;
        if (enter) redraw.run();
        else if (changed) redrawSoon.run();
    }
}
