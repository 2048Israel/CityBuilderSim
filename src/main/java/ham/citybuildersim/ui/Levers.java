package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import ham.citybuildersim.ui.Pieces.Effect;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36)
 * - the dial on a card with what it does beside it, which every dial on the
 * Policy tab and, since 0.7.38, the fare on Infrastructure are drawn with -
 * and the arithmetic of snapping a slider to its step. The dial itself is
 * Ladder (0.7.6), which knows nothing of the staged set; the wiring to it
 * and the apply bar stay with the policy screen, because they read and
 * write that set. The lever's head, the would-be rows and the preview's
 * caveat sentence, which the cards replaced, went in 0.7.38 with their
 * last caller.
 */
public final class Levers {

    public static double snapped(double value, double min, double step) {
        return step <= 0 ? value : min + Math.round((value - min) / step) * step;
    }

    public static boolean moved(double value, double from, double step) {
        return Math.abs(value - from) >= Math.max(step, 1e-9) / 2;
    }

    /* =====================================================================
       THE DIAL CARD (0.7.36)

       The Policy tab's every dial was four things down a 560 px column -
       its figure in 20 px type, a paragraph, the ladder, and once moved a
       statement of what it would do - and the paragraph under it was most
       of the screen. Jerus, on the screens not yet redone: "the others are
       still full of text and the design could be more intuitive and fun".
       A card now (the Policy spec's section 6.1): on the left the dial's
       icon, its name with the paragraph behind an (i), its reading, a line
       of status and the ladder; on the right what it does, a row an effect
       (Pieces.beforeAfter()) - before and after, the after following the
       thumb while it is dragged (Ladder.onTrack()); under them the one
       sentence every preview owes the player; and at its foot, for a dial
       that keeps its own, its Apply.
       ===================================================================== */

    /**
     * One dial card: its icon and the icon's colour; its name and the (i)
     * behind it (null: none); the reading in large type and a line of status
     * under it (either null: none); anything that sits over the ladder - its
     * chips, a switch (null: none); the ladder, unbuilt, and the value it
     * shows at rest; what it does at any value; the caveat's line and its
     * whole (null: none); and the nodes at its foot (null: none).
     */
    public record DialCard(String svg, String colour, String name, String info, String reading, String status,
                           java.util.List<? extends javafx.scene.Node> extras, Ladder ladder, double at,
                           java.util.function.DoubleFunction<java.util.List<Effect>> effectsAt,
                           String caveat, String caveatWhole, java.util.List<? extends javafx.scene.Node> foot) { }

    /** How wide the effects column is held beside the ladder. */
    static final double EFFECTS = 420;

    /**
     * The card: the dial at the left `ladderWidth` wide and its effects at
     * the right, or - `stacked`, in a narrow column - the effects under the
     * dial. The effects are drawn at `at`, and again at every value the
     * thumb passes through while it is dragged.
     */
    public static VBox dialCard(DialCard d, double ladderWidth, boolean stacked) {
        Label name = new Label(d.name());
        name.setWrapText(true);
        name.setMinWidth(0);
        name.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        HBox head = new HBox(Palette.GAP, iconSquare(d.svg(), d.colour(), 28, 15), name);
        if (d.info() != null) head.getChildren().add(infoButton(d.info(), true));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox left = new VBox(6, head);
        if (d.reading() != null) {
            Label big = new Label(d.reading());
            big.setWrapText(true);
            big.setStyle(Palette.figure(Palette.SIZE_LEAD, Palette.TEXT_HEAD));
            left.getChildren().add(big);
        }
        if (d.status() != null) {
            Label status = new Label(d.status());
            status.setWrapText(true);
            status.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            left.getChildren().add(status);
        }
        if (d.extras() != null) for (javafx.scene.Node n : d.extras()) if (n != null) left.getChildren().add(n);

        VBox effects = new VBox(8);
        double effectsWidth = stacked ? ladderWidth : EFFECTS;
        java.util.function.DoubleConsumer fill = v -> {
            effects.getChildren().clear();
            if (d.effectsAt() == null) return;
            for (Effect e : d.effectsAt().apply(v)) effects.getChildren().add(beforeAfter(e, effectsWidth));
        };
        fill.accept(d.at());
        if (d.ladder() != null) {
            left.getChildren().add(d.ladder().wide(ladderWidth).onTrack(fill).build());
        }
        left.setMinWidth(0);

        VBox right = new VBox(6);
        if (d.effectsAt() != null) {
            Label what = new Label("WHAT IT WOULD DO");
            what.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            right.getChildren().addAll(what, effects);
        }
        if (d.caveat() != null) {
            right.getChildren().add(infoLine(d.caveat(), d.caveatWhole() != null ? d.caveatWhole() : d.caveat(), true,
                    Palette.SIZE_LABEL, Palette.TEXT_MUTED, effectsWidth));
        }

        VBox card = new VBox(10);
        if (stacked) {
            card.getChildren().addAll(left, right);
        } else {
            left.setPrefWidth(ladderWidth);
            left.setMaxWidth(ladderWidth);
            right.setMinWidth(260);
            HBox.setHgrow(right, Priority.ALWAYS);
            right.setMaxWidth(Double.MAX_VALUE);
            HBox both = new HBox(24, left, right);
            card.getChildren().add(both);
        }
        if (d.foot() != null && !d.foot().isEmpty()) {
            HBox foot = new HBox(10);
            foot.setAlignment(Pos.CENTER_LEFT);
            for (javafx.scene.Node n : d.foot()) if (n != null) foot.getChildren().add(n);
            card.getChildren().add(foot);
        }
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }
}
