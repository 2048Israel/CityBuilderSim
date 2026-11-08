package ham.citybuildersim.tools;

import ham.citybuildersim.ui.IslandChip;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

/**
 * Renders the island chip for review (MT1, BK1-L): the chip as it was (one line, a 3 px dark stroke) and as it is (two
 * lines on a dark rounded plate, IslandChip.draw()) over the amber hatch and tint of a landmass no port links.
 *
 *     java -cp "target/classes;<gson.jar>;<javafx jars>" ham.citybuildersim.tools.ChipRender [outFile]
 *
 * outFile is docs/queue/MT1/render-chip.png unless named. The ground is a stand-in (a green with the hatch of the same
 * amber at the same strength); the chip is the game's own draw, in the game's face (Plex Mono, else Courier New).
 */
public final class ChipRender {

    private ChipRender() { }

    private static final String AMBER = "#e3b341";

    public static void main(String[] args) throws Exception {
        Path out = Path.of(args.length > 0 ? args[0] : "docs/queue/MT1/render-chip.png");
        Files.createDirectories(out.toAbsolutePath().getParent());
        CountDownLatch done = new CountDownLatch(1);
        Throwable[] failed = new Throwable[1];
        Platform.startup(() -> {
            try {
                render(out);
            } catch (Throwable t) {
                failed[0] = t;
            } finally {
                done.countDown();
            }
        });
        done.await();
        Platform.exit();
        if (failed[0] != null) throw new RuntimeException(failed[0]);
        System.out.println("wrote " + out);
    }

    private static void render(Path out) throws Exception {
        Font font;
        try (InputStream in = ChipRender.class.getResourceAsStream("/fonts/IBMPlexMono-Regular.ttf")) {
            Font loaded = in == null ? null : Font.loadFont(in, 9);
            font = loaded != null ? loaded : Font.font("Courier New", 9);
        }
        double w = 760, h = 330;
        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // The ground: grass and forest as a stand-in, then the island's tint and hatch over it, as islandHatch() lays them.
        gc.setFill(Color.web("#3f6b3c"));
        gc.fillRect(0, 0, w, h);
        gc.setFill(Color.web("#2f5a35"));
        for (int i = 0; i < 90; i++) gc.fillRect((i * 73) % (int) w, (i * 41) % (int) h, 40, 14);
        gc.setFill(Color.web(AMBER, 0.12));
        gc.fillRect(0, 0, w, h);
        gc.setStroke(Color.web(AMBER, 0.55));
        gc.setLineWidth(1);
        for (double d = -h; d < w; d += 8) gc.strokeLine(d, h, d + h, 0);
        Text measure = new Text();
        java.util.function.ToDoubleFunction<String> width = s -> {
            measure.setFont(font);
            measure.setText(s);
            return measure.getLayoutBounds().getWidth();
        };
        gc.setFont(font);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        // Before: one line, a 3 px stroke (the chip until MT1).
        String old = "Island 2 · 5.37 km² · not linked: needs a port here (none has one)";
        gc.setLineWidth(3);
        gc.setStroke(Color.rgb(11, 17, 24, 0.85));
        gc.strokeText(old, w / 2, 40);
        gc.setFill(Color.web(AMBER));
        gc.fillText(old, w / 2, 40);
        // After: the plate, the worst line of the batch's own wording, a short one, and one at the view's edge (kept inside).
        IslandChip.draw(gc, font, new String[] { "Island 2 · 5.37 km²", "not linked: needs a port here (none has one)" }, w / 2, 110, w, width);
        IslandChip.draw(gc, font, new String[] { "Greenland · 2.12 million km²", "not linked: needs a port here (home has one)" }, 200, 200, w, width);
        IslandChip.draw(gc, font, new String[] { "Island 3 · 41.0 km²", "not linked: needs a port here and one at home" }, 6, 290, w, width);
        IslandChip.draw(gc, font, new String[] { "North and South America · 36.8 million km²", "not linked: needs a port here (home has one)" }, 600, 260, w, width);
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        java.awt.image.BufferedImage png = new java.awt.image.BufferedImage((int) image.getWidth(), (int) image.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        javafx.scene.image.PixelReader pixels = image.getPixelReader();
        for (int y = 0; y < png.getHeight(); y++) for (int x = 0; x < png.getWidth(); x++) png.setRGB(x, y, pixels.getArgb(x, y));
        javax.imageio.ImageIO.write(png, "png", out.toFile());
    }
}
