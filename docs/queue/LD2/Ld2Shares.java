package ham.citybuildersim;

public class Ld2Shares {
    public static void main(String[] a) {
        long seed = Long.parseLong(a[0]);
        int from = Integer.parseInt(a[1]), to = Integer.parseInt(a[2]);
        String key = a[3];
        int c = java.util.Arrays.asList(Sectors.KEYS).indexOf(key);
        Game g = new Game(GameFiles.scratch("ld2-shares-" + seed));
        g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, seed));
        g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
        for (int m = 0; m < to; m++) {
            g.toggleNextMonth();
            if (g.getMonth() < from) continue;
            Equity r = g.getEquity(); Exchange x = g.getExchange();
            BalanceSheet bs = g.getSectors().byKey(key).getBalanceSheet();
            System.out.printf("m%d %s shares %.0f outstanding %.0f dealer %.0f foreign %.0f | price %.4f fair %.4f | sector equity %.1f assets %.1f | boughtBack %.0f paidIn %.1f boughtBackPaid %.1f city %.0f%n",
                    g.getMonth(), key, r.getShares(c), r.getOutstanding(c), r.getDealerShares(c), r.getForeignShares(c),
                    x.price(c), x.fair(c), bs.getEquity(), bs.getTotalAssets(), r.getBoughtBackThisMonth(c), r.getPaidIn(c), r.getBoughtBackPaid(c), r.getCityShares(c));
        }
    }
}
