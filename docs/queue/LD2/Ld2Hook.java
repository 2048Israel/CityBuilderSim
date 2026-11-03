package ham.citybuildersim;

import java.io.*;

/** LD2's probe of points 2 and 3: after each month a band carries latent posts, what the carry did to the others. */
public class Ld2Hook {
    static PrintWriter w;
    static final int UNTIL = Integer.getInteger("ld2.until", 2400);

    static void month(Game g) {
        try {
            if (w == null) w = new PrintWriter(new FileWriter(System.getProperty("ld2.file")), true);
        } catch (IOException e) { throw new UncheckedIOException(e); }
        WantedPosts wp = g.getWantedPosts();
        if (wp.any()) {
            PopulationManager pm = g.getPopulationManager();
            double[] posts = pm.staffablePostsByBand(), latent = wp.latent();
            double[] sum = posts.clone();
            for (int b = 0; b < sum.length; b++) sum[b] += latent[b];
            double[] with = pm.supplyByBand(sum), without = pm.supplyByBand(posts);
            StringBuilder sb = new StringBuilder("M " + g.getMonth() + " pop " + pm.getPopulation());
            for (int b = 0; b < posts.length; b++) {
                double base = posts[b] <= 0 ? LabourMarket.MIN_MULTIPLE : LabourMarket.multipleAt(posts[b] / Math.max(without[b], 1));
                double mWith = sum[b] <= 0 ? LabourMarket.MIN_MULTIPLE : LabourMarket.multipleAt(sum[b] / Math.max(with[b], 1));
                double mNoCarry = sum[b] <= 0 ? LabourMarket.MIN_MULTIPLE : LabourMarket.multipleAt(sum[b] / Math.max(without[b], 1));
                sb.append(String.format(" | b%d L %.1f P %.1f Sw %.1f So %.1f base %.4f with %.4f nocarry %.4f adm %b",
                        b, latent[b], posts[b], with[b], without[b], base, mWith, mNoCarry, g.getMigration().admits(WageBand.values()[b])));
            }
            w.println(sb);
        }
        if (g.getMonth() >= UNTIL) { w.close(); System.exit(0); }
    }
}
