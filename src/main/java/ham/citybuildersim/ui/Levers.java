package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The pieces a policy lever is drawn with: its head, the would-be rows that
 * show a staged change against today's figure, and the arithmetic of snapping
 * a slider to its step. The dial itself is Ladder (0.7.6), which knows
 * nothing of the staged set; the wiring to it and the apply bar stay with
 * the policy screen, because they read and write that set.
 */
public final class Levers {

    /** The current setting, big, with the sentence that says what it governs. */
    public static VBox leverHead(String figure, String what) {

        Label big = new Label(figure);
        big.setStyle(Palette.figure(Palette.SIZE_TITLE, Palette.TEXT_HEAD));

        Label says = new Label(what);
        says.setWrapText(true);
        says.setMaxWidth(STATEMENT);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox box = new VBox(0, big, says);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 2 0 10 0;");
        return box;
    }

    public static double snapped(double value, double min, double step) {
        return step <= 0 ? value : min + Math.round((value - min) / step) * step;
    }

    public static boolean moved(double value, double from, double step) {
        return Math.abs(value - from) >= Math.max(step, 1e-9) / 2;
    }

    public static VBox wouldHead() { return statementHead("What it would do"); }

    /** "now  \u2192  then", or just now when nothing staged reaches this row. */
    public static String arrow(boolean staged, String now, String then) {
        return staged ? now + "  \u2192  " + then : now;
    }

    /** One line of a preview: what it reads now, and what it would read. */
    public static HBox wouldBe(String label, String before, String after, String tone) {
        return statementLine(label, before + "  →  " + after, tone);
    }

    public static VBox wouldTotal(String label, String before, String after, String tone) {
        return statementTotal(label, before + "  →  " + after, tone);
    }

    /** The sentence every preview on this tab ends with, because it is always true. */
    public static Label previewCaveat(String basis) {
        return statementNote(basis + " Nothing here knows that the new rate changes what "
                + "anybody does next month - a business taxed harder earns less, and that "
                + "arrives in its own books rather than in this preview.");
    }
}
