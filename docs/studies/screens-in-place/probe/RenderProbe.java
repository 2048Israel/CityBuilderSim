package ham.citybuildersim.ui;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Scratch probe for the study UP4, never in src/: the People page rendered whole (the page's box,
 * not the viewport, so every card is in the picture) at its first draw and after some redraws on
 * the clock, written as PNGs - the pilot's renders before and after, compared pixel for pixel.
 * Arguments: month, redraws, a label for the files; -Dprobe.out names the folder.
 */
public final class RenderProbe {

    public static void main(String[] args) throws Exception {
        int month = args.length > 0 ? Integer.parseInt(args[0]) : 1000;
        int redraws = args.length > 1 ? Integer.parseInt(args[1]) : 8;
        String label = args.length > 2 ? args[2] : "before";
        InPlaceProbe.open(month);
        UserInterface ui = InPlaceProbe.ui;
        List<String> written = new ArrayList<>();
        written.add(InPlaceProbe.fx(() -> {
            ui.showMainMenu();
            InPlaceProbe.scenePulse();
            ui.peopleScreen.showPopulationInfoMenu();
            InPlaceProbe.scenePulse();
            return shot(ui.rootMenu, "people-m" + month + "-first-" + label);
        }));
        written.add(InPlaceProbe.fx(() -> {
            for (int i = 0; i < redraws; i++) {
                InPlaceProbe.g.toggleNextMonth();
                ui.redraw();
                InPlaceProbe.scenePulse();
            }
            // The count-up and the bars' growth run 600 ms after a month lands: the picture is taken settled.
            return null;
        }));
        Thread.sleep(1500);
        written.add(InPlaceProbe.fx(() -> {
            InPlaceProbe.scenePulse();
            return shot(ui.rootMenu, "people-m" + month + "-after" + redraws + "-" + label);
        }));
        for (String w : written) if (w != null) InPlaceProbe.out.println(w);
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /** The node rendered at its own size, written to probe.out as a PNG; returns a line saying where and how big. */
    static String shot(Node n, String name) throws Exception {
        WritableImage img = n.snapshot(new SnapshotParameters(), null);
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        PixelReader px = img.getPixelReader();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] row = new int[w];
        for (int y = 0; y < h; y++) {
            px.getPixels(0, y, w, 1, javafx.scene.image.PixelFormat.getIntArgbInstance(), row, 0, w);
            out.setRGB(0, y, w, 1, row, 0, w);
        }
        Path p = InPlaceProbe.OUT.resolve(name + ".png");
        Files.createDirectories(p.getParent());
        javax.imageio.ImageIO.write(out, "png", p.toFile());
        return String.format("%s: %d x %d", p, w, h);
    }
}
