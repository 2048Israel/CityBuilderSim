package ham.citybuildersim.ui;

import ham.citybuildersim.Debt;
import ham.citybuildersim.Game;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;

/**
 * Scratch, not in the build: the half of CD6's render that has to live in the interface's package, because FinancesScreen is
 * package-private. RenderCD6 (package ham.citybuildersim) plays the city and calls snapshot(); this builds the book page or a
 * line's page on a UserInterface bound to the city, themed as the window themes its scene (UserInterface.applyTheme(), by
 * reflection), lays it out without a window and writes it as a PNG.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/CD6/RenderCD6Page.java docs/queue/CD6/RenderCD6.java
 */
public final class RenderCD6Page {
    private RenderCD6Page() { }

    /**
     * Builds the book (a null `line`) or that line's page for the city, `width` px wide, and writes it to `out`. Blocks until
     * the FX thread has drawn it.
     */
    public static void snapshot(Game g, Debt line, double width, File out) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        Throwable[] failed = new Throwable[1];
        Platform.runLater(() -> {
            try {
                UserInterface ui = new UserInterface(g);
                VBox page = Pieces.widePage();
                if (line == null) {
                    ui.financesScreen.bookPage(page);
                } else {
                    java.lang.reflect.Field open = FinancesScreen.class.getDeclaredField("lineOpen");
                    open.setAccessible(true);
                    open.set(ui.financesScreen, line);
                    ui.financesScreen.linePage(page);
                }
                VBox root = new VBox(page);
                root.setStyle("-fx-background-color: " + Palette.STAGE + ";");
                Scene scene = new Scene(root, width, 4000);
                java.lang.reflect.Method theme = UserInterface.class.getDeclaredMethod("applyTheme", Scene.class);
                theme.setAccessible(true);
                theme.invoke(ui, scene);
                root.applyCss();
                root.layout();
                // The pictures draw on the next pulse: let two go by, then lay out again.
                Platform.runLater(() -> Platform.runLater(() -> {
                    try {
                        root.applyCss();
                        root.layout();
                        double used = Math.ceil(page.prefHeight(width)) + 24;
                        WritableImage shot = scene.snapshot(null);
                        int w = (int) shot.getWidth(), h = (int) Math.min(shot.getHeight(), used);
                        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        PixelReader pixels = shot.getPixelReader();
                        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) image.setRGB(x, y, pixels.getArgb(x, y));
                        javax.imageio.ImageIO.write(image, "png", out);
                        System.out.println("wrote " + out + " (" + w + " x " + h + ")");
                    } catch (Throwable t) {
                        failed[0] = t;
                    } finally {
                        done.countDown();
                    }
                }));
            } catch (Throwable t) {
                failed[0] = t;
                done.countDown();
            }
        });
        done.await();
        if (failed[0] != null) throw new Exception(failed[0]);
    }
}
