package ham.citybuildersim;

import ham.citybuildersim.ui.RenderFW8Page;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Scratch, not in the build (like RenderCD6): FW8's Policy hub as PNGs, before and after WHAT IS BITING's zoning flag.
 * Plays ZoningViewCheck's city (a TempDirs folder, nothing of Jerus's read or written: 80 km² of dry ground, ZoningCheck's
 * orders, 12 months) and draws the hub under three zonings, each set and then taken off:
 *   short-one   Agriculture zoned 90 km², more than the city's dry ground: one zone short
 *   short-two   Mining zoned 50% of the dry ground at 40% of the going price and Agriculture 60%: two zones short
 *   enough      Mining zoned 2% at 40%, ZoningViewCheck's own: a zone the free ground covers
 * Each is written as <prefix>-<name>.png, 1,200 px wide.
 *
 *   javac -cp "target/classes;<classpath.txt>" -d <scratch> docs/queue/FW8/RenderFW8Page.java docs/queue/FW8/RenderFW8.java
 *   java -cp "<scratch>;target/classes;<classpath.txt>" ham.citybuildersim.RenderFW8 docs/queue/FW8 after
 */
public class RenderFW8 {

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        File dir = new File(args.length > 0 ? args[0] : "docs/queue/FW8");
        String prefix = args.length > 1 ? args[1] : "after";
        PrintStream real = System.out;
        ZoningViewCheck.out = real;
        ZoningViewCheck.quiet = new PrintStream(OutputStream.nullOutputStream());
        javafx.application.Platform.startup(() -> { });

        Path root = TempDirs.create("fw8-render");
        Game g = ZoningViewCheck.city(root, "city", 12);
        LandManager land = g.getLandManager();
        real.printf("the city: month %d, %,d people; %s dry, %s built on, %s free%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), LandManager.areaWords(land.getOwnedSqFt()),
                LandManager.areaWords(land.getAllocatedSqFt()), LandManager.areaWords(land.getFreeSqFt()));

        draw(g, dir, prefix, "short-one", () -> g.setZone(Sectors.AGRICULTURE, Zoning.Size.KM2, 90, 1));
        draw(g, dir, prefix, "short-two", () -> {
            g.setZone(Sectors.MINING, Zoning.Size.SHARE, .5, .4);
            g.setZone(Sectors.AGRICULTURE, Zoning.Size.SHARE, .6, 1);
        });
        draw(g, dir, prefix, "enough", () -> g.setZone(Sectors.MINING, Zoning.Size.SHARE, .02, .4));
        System.exit(0);
    }

    /** The zoning set, its rows printed, the hub drawn, and every zone taken off again. */
    static void draw(Game g, File dir, String prefix, String name, Runnable zone) throws Exception {
        ZoningViewCheck.quietly(zone);
        for (ZoningView.Row r : ZoningView.zones(g)) {
            System.out.printf("  %s: %s holds %s of %s it wants%s%n", name, r.sector(), LandManager.areaWords(r.heldSqFt()),
                    LandManager.areaWords(r.wantSqFt()), r.isShort() ? " - short" : "");
        }
        RenderFW8Page.snapshot(g, 1200, new File(dir, prefix + "-" + name + ".png"));
        ZoningViewCheck.quietly(() -> {
            for (Zoning.Zone z : g.getZoning().zones()) g.removeZone(z.sector());
        });
    }
}
