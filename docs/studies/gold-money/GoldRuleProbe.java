package ham.citybuildersim;

/**
 * The gold standard's "rules of the game" tried on today's peg, from outside the game (the gold-money study, 2026-11-02).
 *
 * WHY. Today a peg's dial is the US's rate plus a spread the player sets (DebtManager.tiedRate), and the vault pays the month's
 * whole balance (ForeignAccounts.repriceCurrency, PEG). Nothing raises the dial as the vault empties, so a young town pegged at
 * founding sells its dollars until the vault is under a month of imports and the peg breaks (CurrencyRegimeCheck 11: month 49
 * on 0.9.2). The classical gold standard's central banks raised their rate as the gold left. This probe asks whether that rule,
 * worked by hand through the public dials, keeps the same town's peg, and at what cost.
 *
 * HOW. CurrencyRegimeCheck.town(), the harness's own fixture town, pegged at founding at 1.00 D$ per US$, played 240 months in
 * each of five ways: as today; with a vault 40 times deeper (Founding.WEALTHY_RESERVE_USD); and with the autopilot off and each
 * month's spread set from the vault's months of imports, spread = K x clamp((T - m) / (T - 1), 0, 1), T = 6 months, for three K;
 * with the argument "early", four more with T = 24 and 60 months (the rule biting from two and five years of imports).
 * It writes nothing but its own lines: the towns are GameFiles.scratch() folders, never a save slot.
 *
 * Compile and run from the repository root after a build (Git Bash):
 *   javac -d target/probe -cp "target/classes;$(cat target/classpath.txt)" docs/studies/gold-money/GoldRuleProbe.java
 *   java -cp "target/probe;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.GoldRuleProbe [early]
 * Its output is kept beside it in probe.txt.
 */
public final class GoldRuleProbe {

    static final int MONTHS = 240;

    public static void main(String[] args) {
        CurrencyRegimeCheck.out = System.out;
        CurrencyRegimeCheck.quiet = new java.io.PrintStream(java.io.OutputStream.nullOutputStream());
        System.out.println("run              | breaks (month, cause)      | vault months of imports at 12 / 24 / 48 / 120 / 240     | mean dial pegged | price index 120 / 240 | people 120 / 240  | rate 240");
        boolean second = args.length > 0 && args[0].equals("early");
        if (!second) {
            run("today's peg", Founding.defaults(), -1, 6);
            run("vault x40", Founding.custom(Founding.DEFAULT_CITY_NAME, Founding.defaults().getCash(), Founding.WEALTHY_RESERVE_USD,
                    WorldEconomy.DEFAULT_MEAN_INFLATION), -1, 6);
            run("rule K=0.03", Founding.defaults(), .03, 6);
            run("rule K=0.06", Founding.defaults(), .06, 6);
            run("rule K=0.10", Founding.defaults(), .10, 6);
        } else {
            run("K=0.10 T=24", Founding.defaults(), .10, 24);
            run("K=0.25 T=24", Founding.defaults(), .25, 24);
            run("K=0.10 T=60", Founding.defaults(), .10, 60);
            run("K=0.25 T=60", Founding.defaults(), .25, 60);
        }
    }

    static void run(String label, Founding founding, double k, double targetMonths) {
        Game g = CurrencyRegimeCheck.town("gold-probe-" + label.replaceAll("[^a-z0-9]", ""), founding);
        CurrencyRegimeCheck.quietly(() -> g.setCurrencyRegime(ForeignAccounts.Regime.PEG));
        ForeignAccounts fx = g.getForeignAccounts();
        DebtManager dm = g.getDebtManager();
        if (k >= 0) CurrencyRegimeCheck.quietly(() -> dm.setAutopilot(false));
        StringBuilder vault = new StringBuilder();
        double dialSum = 0; int pegged = 0, broke = 0; String cause = "";
        double p120 = 0, p240 = 0; long n120 = 0, n240 = 0;
        double ca = 0, fa = 0;   // the balance's two halves, US$, over the months 25 to 48 (the young town's drain)
        for (int m = 1; m <= MONTHS; m++) {
            if (k >= 0 && fx.getRegime() == ForeignAccounts.Regime.PEG) {
                double months = monthsOfImports(fx);
                double share = Math.max(0, Math.min(1, (targetMonths - months) / (targetMonths - 1)));
                double spread = k * share;
                CurrencyRegimeCheck.quietly(() -> dm.setPegSpread(spread));
            }
            boolean was = fx.getRegime() == ForeignAccounts.Regime.PEG;
            CurrencyRegimeCheck.play(g, 1);
            if (was) { pegged++; dialSum += dm.getPolicyRate(); }
            if (m > 24 && m <= 48) { ca += fx.currentAccount() / fx.getRate(); fa += fx.financialAccount() / fx.getRate(); }
            if (fx.brokeThisMonth() && broke == 0) { broke = m; cause = fx.getLastBreakCause().words; }
            if (m == 12 || m == 24 || m == 48 || m == 120 || m == 240) vault.append(String.format("%6.2f ", monthsOfImports(fx)));
            if (m == 120) { p120 = g.getPriceIndex().getIndex(); n120 = g.getPopulationManager().getPopulation(); }
            if (m == 240) { p240 = g.getPriceIndex().getIndex(); n240 = g.getPopulationManager().getPopulation(); }
        }
        System.out.printf("%-16s | %-26s | %-55s | %6.2f%%          | %8.2f / %8.2f   | %,7d / %,7d | %.4f | months 25-48: current US$%,.0fk, financial US$%,.0fk%n", label,
                broke == 0 ? "held 240" : ("month " + broke + ", " + cause), vault, pegged == 0 ? 0 : 100 * dialSum / pegged,
                p120, p240, n120, n240, fx.getRate(), ca, fa);
    }

    /** The vault in months of the trailing month's imports: the measure the peg's floor (ForeignAccounts.VAULT_FLOOR) is in. */
    static double monthsOfImports(ForeignAccounts fx) {
        double importsUsd = fx.monthlyImports() / fx.getRate();
        return importsUsd > 0 ? fx.getReservesUsd() / importsUsd : Double.POSITIVE_INFINITY;
    }
}
