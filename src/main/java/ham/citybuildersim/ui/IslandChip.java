package ham.citybuildersim.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.ToDoubleFunction;

/**
 * The chip the map draws on a landmass no port links: its lines of amber mono caption on a dark rounded plate the width of
 * the longer line (MT1, BK1-L).
 *
 * WHY THIS EXISTS. The chip was one line of some 70 characters ("Island 2 · 5.37 km² · not linked: needs a port here (none
 * has one)") whose only ground was a 3 px dark stroke, drawn over the landmass's own amber hatch and tint, so over busy
 * ground it was hard to read. It is a class of its own, public, so tools.ChipRender draws the very same plate over a
 * hatch for review; MapView.islandChips() decides where and what.
 */
public final class IslandChip {

    private IslandChip() { }

    /** The plate's padding either side and above and below, the height of a line, and its corner. */
    static final double PAD_X = 6, PAD_Y = 3, LINE_H = 12, ARC = 7;

    /** The plate (the stage's dark, nearly opaque) and its hairline edge in the warning amber the text is in. */
    static final Color PLATE = Color.rgb(11, 17, 24, 0.9), EDGE = Color.web(Palette.WARN, 0.45);

    /**
     * One chip's plate and lines centred on (x, y), kept inside a view `viewWidth` wide; `width` measures a line in `font`.
     * The caller sets the font, the centred text align and the centred baseline (gc state it restores itself).
     */
    public static void draw(GraphicsContext gc, Font font, String[] lines, double x, double y, double viewWidth, ToDoubleFunction<String> width) {
        double wide = 0;
        for (String line : lines) wide = Math.max(wide, width.applyAsDouble(line));
        double half = wide / 2 + PAD_X;
        x = half * 2 >= viewWidth ? viewWidth / 2 : Math.max(half, Math.min(viewWidth - half, x));
        x = Math.rint(x);
        double tall = lines.length * LINE_H + PAD_Y * 2;
        double top = Math.rint(y - tall / 2);
        gc.setFill(PLATE);
        gc.fillRoundRect(x - half, top, half * 2, tall, ARC, ARC);
        gc.setLineWidth(1);
        gc.setStroke(EDGE);
        gc.strokeRoundRect(x - half + 0.5, top + 0.5, half * 2 - 1, tall - 1, ARC, ARC);
        gc.setFill(Color.web(Palette.WARN));
        for (int i = 0; i < lines.length; i++) gc.fillText(lines[i], x, top + PAD_Y + LINE_H * (i + 0.5));
    }
}
