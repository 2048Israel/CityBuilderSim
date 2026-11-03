package ham.citybuildersim;

public class Ld2Bank {
    public static void main(String[] a) {
        int months = Integer.parseInt(a[0]);
        for (int i = 1; i < a.length; i++) {
            long seed = Long.parseLong(a[i]);
            long t0 = System.currentTimeMillis();
            Game g = new Game(GameFiles.scratch("ld2-bank-" + seed));
            g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, seed));
            g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
            int failed = -1;
            double minEq = Double.POSITIVE_INFINITY;
            for (int m = 0; m < months; m++) {
                g.toggleNextMonth();
                Bank b = g.getBank();
                minEq = Math.min(minEq, b.equity());
                if (b.isInResolution()) { failed = g.getMonth(); break; }
            }
            Bank b = g.getBank();
            System.out.printf("seed %d failed %d minEq %.0f eq %.0f dep %.0f pop %d latent %.1f %ds%n",
                    seed, failed, minEq, b.equity(), b.getDeposits(), g.getPopulationManager().getPopulation(),
                    g.getWantedPosts() == null ? -1 : g.getWantedPosts().getTotal(),
                    (System.currentTimeMillis() - t0) / 1000);
        }
    }
}
