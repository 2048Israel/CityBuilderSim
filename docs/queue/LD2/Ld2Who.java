package ham.citybuildersim;

import java.util.*;

public class Ld2Who {
    public static void main(String[] a) {
        int months = Integer.parseInt(a[0]);
        for (int i = 1; i < a.length; i++) {
            long seed = Long.parseLong(a[i]);
            Game g = new Game(GameFiles.scratch("ld2-who-" + seed));
            g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, seed));
            g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
            Map<String, Double> off = new TreeMap<>();
            Map<String, Double> allowMax = new TreeMap<>();
            double provTot = 0, offTot = 0, trade = 0;
            int failed = -1;
            StringBuilder big = new StringBuilder();
            for (int m = 0; m < months; m++) {
                g.toggleNextMonth();
                Bank b = g.getBank();
                provTot += b.provisions();
                trade += b.afterTrading() - b.afterLosses();
                double monthOff = 0;
                for (String k : Sectors.KEYS) {
                    double w = b.getWrittenOff(k);
                    if (w != 0) { off.merge(k, w, Double::sum); offTot += w; monthOff += w; }
                    double al = b.getSectorAllowance(k);
                    if (al > allowMax.getOrDefault(k, 0.0)) allowMax.put(k, al);
                }
                double t = b.afterTrading() - b.afterLosses();
                if (b.provisions() > 40 || Math.abs(t) > 40)
                    big.append(String.format(" m%d prov %.0f off %.0f trade %.0f;", g.getMonth(), b.provisions(), monthOff, t));
                if (b.isInResolution()) { failed = g.getMonth(); break; }
            }
            Bank b = g.getBank();
            System.out.printf("seed %d failed %d prov %.0f sectorOff %.0f trade %.0f hhAllow %.0f%n", seed, failed, provTot, offTot, trade, b.getHouseholdAllowance());
            StringBuilder s = new StringBuilder("   off:");
            off.forEach((k, v) -> s.append(String.format(" %s %.0f", k, v)));
            s.append(" | allowMax:");
            allowMax.forEach((k, v) -> { if (v > 5) s.append(String.format(" %s %.0f", k, v)); });
            System.out.println(s);
            System.out.println("   big:" + big);
        }
    }
}
