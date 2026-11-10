package ham.citybuildersim.ui;

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
 * Scratch, not in the build: the half of GD2's render that has to live in the interface's package, because TradeScreen and
 * VaultTable are package-private. RenderGD2 (package ham.citybuildersim) plays the city and calls snapshot(); this builds one
 * of Trade &gt; The reserves' cards on a UserInterface bound to the city, themed as the window themes its scene
 * (UserInterface.applyTheme(), by reflection), lays it out without a window and writes it as a PNG.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/GD2/RenderGD2Page.java docs/queue/GD2/RenderGD2.java
 */
public final class RenderGD2Page {
    private RenderGD2Page() { }

    /**
     * Builds `what` for the city - "vault" (IN THE VAULT, IN EIGHT MONEYS AND GOLD, with its fold open when `years`) or "exchange"
     * (BUY OR SELL FOREIGN MONEY, with its money picker and the reserve mix) - `width` px wide, and writes it to `out`. For
     * "exchange" the card is made buying or selling, with `money` the code the picker has chosen (null: the first chip) and
     * `amount` the thousands staged (0: nothing). Blocks until the FX thread has drawn it.
     */
    public static void snapshot(Game g, String what, String money, boolean buying, double amount, boolean years, double width, File out) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        Throwable[] failed = new Throwable[1];
        Platform.runLater(() -> {
            try {
                UserInterface ui = new UserInterface(g);
                TradeScreen trade = ui.tradeScreen;
                VBox page = Pieces.widePage();
                switch (what) {
                    case "vault" -> {
                        if (years) trade.openLines.add("reserves:eight");
                        page.getChildren().add(new VaultTable(ui, trade).eightCard());
                    }
                    case "exchange" -> {
                        trade.tradeBuying = buying;
                        trade.tradeMoney = money;
                        trade.tradeExchange = amount;
                        page.getChildren().add(trade.exchangeCard());
                    }
                    default -> throw new IllegalArgumentException(what);
                }
                VBox root = new VBox(page);
                root.setStyle("-fx-background-color: " + Palette.STAGE + ";");
                Scene scene = new Scene(root, width, 4000);
                java.lang.reflect.Method theme = UserInterface.class.getDeclaredMethod("applyTheme", Scene.class);
                theme.setAccessible(true);
                theme.invoke(ui, scene);
                root.applyCss();
                root.layout();
                // The charts draw on the next pulse: let two go by, then lay out again.
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
