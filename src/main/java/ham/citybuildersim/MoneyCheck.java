package ham.citybuildersim;

import java.util.Locale;

/**
 * Money is conserved: every dollar that leaves a pool arrives in another, or
 * crosses the city's boundary in a way the audit can name.
 *
 * The 29th harness, and the one the other twenty-eight were missing. Every
 * money bug this codebase has had was a flow with one side - a charge with no
 * payee, a tax with no payer - and each was found by hand, months after it
 * started. MoneyAudit strikes the identity every month; this plays a city with
 * all six sectors trading, borrowing, building and being taxed, and demands
 * the residual stay at rounding.
 *
 * Two cities, because a fixture that only ever grows can hide a leak that
 * only opens under stress: one is left to prosper, the other is bankrupted
 * and restructured, taxed hard, and made to import everything.
 */
public class MoneyCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-70s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate t(Game g, String name) {
        for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
            if (b.getName().equals(name)) return b;
        }
        throw new IllegalArgumentException("no template " + name);
    }

    /** Runs `months` and returns the worst relative residual seen, printing the worst month. */
    /** The worst post-audit drift any month of the last play() saw. See Game.getPostAuditDrift(). */
    static double worstDrift = 0;
    static String worstDriftPool = "";

    static MoneyAudit.Result play(String label, Game g, int months, boolean verbose) {
        MoneyAudit.Result worst = MoneyAudit.Result.NONE;
        double worstRel = -1;
        worstDrift = 0;
        worstDriftPool = "";
        for (int i = 0; i < months; i++) {
            final Game gg = g;
            quietly(() -> gg.simulateMonths(1));
            MoneyAudit.Result r = g.getLastMoneyAudit();
            if (verbose) System.out.println("   " + r);
            if (r.relative() > worstRel) { worstRel = r.relative(); worst = r; }
            if (Math.abs(g.getPostAuditDrift()) > Math.abs(worstDrift)) {
                worstDrift = g.getPostAuditDrift();
                worstDriftPool = g.getPostAuditDriftPool();
            }
        }
        System.out.printf("   %s: worst month %s%n", label, worst);
        if (worst.relative() >= 1e-4) System.out.print(worst.detail);
        return worst;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.CANADA);
        boolean verbose = args.length > 0 && args[0].equals("-v");

        /* ==================== 1. a city that prospers ==================== */
        System.out.println("--- every sector trading, nothing leaks ---");

        Game g = new Game(GameFiles.scratch("moneycheck"));
        g.run();
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
        g.buildStack(t(g, "House"), 400, false);
        g.buildStack(t(g, "Low-Rise Apartments"), 4, false);
        g.buildStack(t(g, "Small Grocery Store"), 4, false);
        g.buildStack(t(g, "Industrial Bakery"), 3, false);
        g.buildStack(t(g, "Construction Depot"), 3, false);
        g.buildStack(t(g, "Coal Power Plant"), 1, false);
        g.buildStack(t(g, "Water Treatment Plant"), 1, false);
        g.buildStack(t(g, "Paved Road"), 10, false);
        g.buildStack(t(g, "Walk-in Clinic"), 2, false);
        g.buildStack(t(g, "Elementary School"), 1, false);

        MoneyAudit.Result worst = play("founding, 120 months", g, 120, verbose);
        assertTrue("a founding city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);

        // Heavy industry and mining need a deposit, which the player buys.
        LandMarket market = g.getLandManager().getMarket();
        for (LandParcel parcel : market.getListing()) {
            if (parcel.getIronTonnes() > 0) {
                g.getGovernmentInvestor().spend(-parcel.getPrice());
                g.buyLandParcel(parcel.getId());
                break;
            }
        }
        g.getGovernmentInvestor().spend(-50_000_000);
        g.buildStack(t(g, "Iron Mine"), 2, false);
        g.buildStack(t(g, "Steel Foundry"), 1, false);
        g.buildStack(t(g, "Memorial Cemetery"), 1, false);
        g.buildStack(t(g, "Community College"), 1, false);

        worst = play("with mines, mills, a cemetery and a college, 240 months", g, 240, verbose);
        assertTrue("an industrial city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);

        /* ==================== 2. a city under stress ==================== */
        System.out.println("\n--- broke, banned, taxed and importing: still nothing leaks ---");

        Game s = new Game(GameFiles.scratch("moneycheck"));
        s.run();
        s.getLandManager().setOwnedSqFt(s.getLandManager().getOwnedSqFt() + 100_000_000L);
        s.buildStack(t(s, "House"), 600, false);
        s.buildStack(t(s, "Convenience Store"), 6, false);
        s.buildStack(t(s, "Construction Depot"), 2, false);
        s.buildStack(t(s, "Coal Power Plant"), 1, false);
        // No food industry at all: the shops import every unit they sell.
        // Every tax dial up, so the sectors are taxed on everything they do.
        TaxPolicy policy = s.getEconomyManager().getTaxPolicy();
        policy.setIncomeTaxRate(.35);
        for (String sector : Sectors.KEYS) {
            policy.setProfitOffset(sector, .25);
            policy.setSalesOffset(sector, .10);
        }
        // Take the treasury to the edge so emergency notes get issued and
        // repaid inside the window.
        s.setCashForTest(20_000);

        worst = play("a stressed city, 180 months", s, 180, verbose);
        assertTrue("a stressed city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);

        // Bankrupt retail by hand and let the restructure and its ban run.
        BusinessDebtManager credit = s.getEconomyManager().getBusinessDebtManager();
        credit.issueLoan(Sectors.RETAIL, 5_000_000, s.getMonth());
        s.getEconomyManager().setSectorCash(Sectors.RETAIL,
                s.getEconomyManager().getSectorCash(Sectors.RETAIL) + 5_000_000);
        credit.setAssets(Sectors.RETAIL, -1);

        worst = play("through a restructure, 60 months", s, 60, verbose);
        assertTrue("a restructure moves no cash the audit cannot see",
                worst.relative() < 1e-4);

        /* ==================================================================
           AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK.

           The residual every check above asserts on reconciles WITHIN a month.
           It is blind, by construction, to money that moves after the strike:
           such money is missing from the flows and already in the pools by the
           next month's opening read, the two errors cancel exactly, and the
           residual stays at $0.00. Hot money lived in that gap for the whole
           life of the mechanic and 4,002 audited months never said a word.

           THE FIXTURE HAS TO CAUSE THE FLOW. A city whose rates sit under the
           world's attracts no hot money at all - the playtest's does, and its
           stock is zero for 4,002 months - so asserting this on an ordinary
           city proves nothing whatever. The policy rate goes to the maximum
           here, which puts the city well over the world's 2% and makes the
           carry money actually turn up. Without that line this check passes
           against the very bug it exists for; it was tried.
           ================================================================== */
        System.out.println("\n--- a city paying over the world: the money that arrives is audited ---");
        Game hot = new Game(GameFiles.scratch("moneycheck"));
        quietly(hot::run);
        hot.getLandManager().setOwnedSqFt(hot.getLandManager().getOwnedSqFt() + 400_000_000L);
        hot.buildStack(t(hot, "House"), 400, false);
        hot.buildStack(t(hot, "Small Grocery Store"), 4, false);
        hot.buildStack(t(hot, "Industrial Bakery"), 3, false);
        hot.buildStack(t(hot, "Coal Power Plant"), 1, false);
        hot.buildStack(t(hot, "Water Treatment Plant"), 1, false);
        hot.buildStack(t(hot, "Paved Road"), 10, false);
        hot.getDebtManager().setPolicyRate(DebtManager.MAX_POLICY_RATE);
        worst = play("paying the maximum for fifteen years", hot, 180, verbose);
        CapitalFlows flows = hot.getCapitalFlows();
        System.out.printf("   hot money: $%,.0fk here, $%,.0fk arrived and $%,.0fk left over the run,"
                + " on a %.2f-point spread%n",
                flows.getStock() * 1000, flows.getLifetimeArrived() * 1000,
                flows.getLifetimeDeparted() * 1000, flows.getSpread() * 100);
        assertTrue("fixture: the rate actually brought money in",
                flows.getLifetimeArrived() > 0);
        assertTrue("...and the audit saw it cross the border",
                worst.financialIn > 0 || worst.financialOut > 0
                        || hot.getLastMoneyAudit().financialIn > 0);
        assertTrue("a city conserves money with hot money flowing", worst.relative() < 1e-4);
        System.out.printf("   worst drift after a strike: $%,.2f%s%n", worstDrift,
                worstDriftPool.isEmpty() ? "" : " (" + worstDriftPool + ")");
        assertTrue("...and nothing moved after the audit struck", Math.abs(worstDrift) < .01);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
