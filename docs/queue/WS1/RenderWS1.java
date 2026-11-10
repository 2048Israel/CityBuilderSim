package ham.citybuildersim;

import ham.citybuildersim.ui.RenderWS1Page;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Scratch, not in the build (like RenderBB1): WS1's Wages page (the minimum wage's cards and the ladder under them) as PNGs. Plays a
 * scratch city (TempDirs.create(), nothing of Jerus's is read or written) on PolicyPreviewCheck's fixture - 800 homes and the businesses
 * of ORDERS, the levers opened, sixty months played, some five thousand people - and writes the page with nothing staged
 * (floor-default.png), with the floor staged 20% over the going rate (floor-staged-over.png), with the tie staged on the average wage
 * (floor-tie-average.png) and the staged floor again 900 wide (floor-narrow.png). Says, for each, the figures the page was drawn from.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/WS1/RenderWS1Page.java docs/queue/WS1/RenderWS1.java
 *   java -cp "<scratch>;target/classes;<classpath.txt>" ham.citybuildersim.RenderWS1 docs/queue/WS1
 */
public class RenderWS1 {

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        File dir = new File(args.length > 0 ? args[0] : "docs/queue/WS1");
        PrintStream real = System.out;
        PolicyPreviewCheck.out = real;
        PolicyPreviewCheck.quiet = new PrintStream(OutputStream.nullOutputStream());
        javafx.application.Platform.startup(() -> { });

        Path root = TempDirs.create("ws1-render");
        Game g = PolicyPreviewCheck.city(root);
        LabourMarket market = g.getLabourMarket();
        say(g, "the city as played", market.getMinimumWage());

        RenderWS1Page.snapshotFloor(g, Double.NaN, null, 1200, new File(dir, "floor-default.png"));

        double over = market.floorForCash(1.2 * market.cashGoingRate());
        say(g, "the floor staged 20% over the going rate", over);
        RenderWS1Page.snapshotFloor(g, over, null, 1200, new File(dir, "floor-staged-over.png"));

        RenderWS1Page.snapshotFloor(g, Double.NaN, LabourMarket.FloorTie.AVERAGE_WAGE, 1200, new File(dir, "floor-tie-average.png"));

        RenderWS1Page.snapshotFloor(g, over, null, 900, new File(dir, "floor-narrow.png"));

        // The floor 5% over the going rate and two months on: the wages under it are held at it, so some are paid it and some are near it.
        double binds = market.floorForCash(1.05 * market.cashGoingRate());
        PolicyPreviewCheck.quietly(() -> {
            g.setMinimumWage(binds);
            g.simulateMonths(2);
        });
        say(g, "the floor 5% over the going rate, two months on", market.getMinimumWage());
        RenderWS1Page.snapshotFloor(g, Double.NaN, null, 1200, new File(dir, "floor-binds.png"));

        // ...and the tie in force on the average wage: the dial moves the share.
        PolicyPreviewCheck.quietly(() -> g.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, LabourMarket.MAX_TIE_SHARE));
        RenderWS1Page.snapshotFloor(g, over, null, 1200, new File(dir, "floor-tied.png"));
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /** The figures a page is drawn from, for a staged floor. */
    static void say(Game g, String what, double staged) {
        FloorView v = FloorView.of(g, staged);
        System.out.printf("%s: floor %.1f (%.0f%% of the going rate %.1f), staged %.1f (%.0f%%), unskilled paid %.1f, average wage %.1f (floor %.0f%% of it), "
                + "posts filled %.0f, paid the floor %.0f, near it %.0f, lifted %.0f (pay %.1f to %.1f), payroll move %.1f, cover %.0f%%, tie %s %.2f/%.2f, joined %s%n",
                what, v.floor(), v.floorOverGoing() * 100, v.goingRate(), v.staged(), v.stagedOverGoing() * 100, v.unskilledWage(), v.averageWage(),
                v.floorOverAverage() * 100, v.postsFilled(), v.paidTotal(), v.nearTotal(), v.lifted(), v.liftedPayNow(), v.liftedPayThen(), v.payrollMove(),
                v.cover() * 100, v.tie(), v.tieShare(), v.enteringShare(), v.joined());
    }
}
