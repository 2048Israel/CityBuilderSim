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

    /** The worst post-audit drift any month of the last play() saw. See Game.getPostAuditDrift(). */
    static double worstDrift = 0;
    static String worstDriftPool = "";

    /** Runs `months` and returns the worst relative residual seen, printing the worst month. */
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
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        g.setCashForTest(Founding.WEALTHY_CASH);
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

        // Heavy industry and mining need a deposit, which the player buys -
        // in dollars since 0.7.6, converting cash at the day's rate, so the
        // treasury is handed exactly that much local money first.
        LandMarket market = g.getLandManager().getMarket();
        for (LandParcel parcel : market.getListing()) {
            if (parcel.getIronTonnes() > 0) {
                g.getGovernmentInvestor().spend(-parcel.localPrice(g.getForeignAccounts().getRate()));
                g.buyLandParcel(parcel.getId());
                break;
            }
        }
        g.getGovernmentInvestor().spend(-50_000_000);
        g.buildStack(t(g, "Iron Mine"), 2, false);
        g.buildStack(t(g, "Steel Foundry"), 1, false);
        g.buildStack(t(g, "Memorial Cemetery"), 1, false);
        g.buildStack(t(g, "Community College"), 1, false);
        /* =================================================================
           AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16).

           Transit had been in the game for as long as the modes had, and no
           harness in this suite had ever put a passenger on one - the playtest
           advisor answered every jam with tarmac, and every fixture that
           needed a road bought a road. So the fare, which the city collects
           and NOBODY WAS EVER DEBITED FOR, sat as money from nowhere through
           every run of the very harness that exists to catch money from
           nowhere. It was found the first time the advisor bought a Bus
           Network, and the residual was the fare to six decimals.

           This class's own header says it: "every money bug this codebase has
           had was a flow with one side". The twenty-ninth harness could not
           see this one because its city had no buses, which is the more
           general lesson - a mechanic no fixture exercises has no test,
           whatever the assertion count says.
           ================================================================= */
        g.buildStack(t(g, "Bus Network"), 3, false);

        worst = play("with mines, mills, a cemetery, a college and a bus, 240 months", g, 240, verbose);
        assertTrue("an industrial city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);
        assertTrue("...and it really did carry passengers, so the fare was really charged",
                g.getInfrastructureManager().getTransitRiders() > 0
                        && g.getEconomyManager().getTransitFares() > 0);

        /* ==================================================================
           AND LAND PAID FOR OUT OF THE VAULT (0.7.6).

           The deposit above was bought the default way - cash converted at
           the day's rate - and the 240 months after it closed. The other way
           moves no local money at all: the vault's dollars go to the seller.
           THE FIXTURE CAUSES IT: the treasury buys the dollars into the vault
           first, so the vault can pay whatever the parcel costs, and the
           parcel is bought with the toggle on.
           ================================================================== */
        System.out.println("\n--- land bought out of the vault: nothing moves the audit cannot see ---");
        LandParcel plot = market.bestValue();
        double plotUsd = plot.getPriceUsd();
        g.getGovernmentInvestor().spend(-plot.localPrice(g.getForeignAccounts().getRate()) * 2);
        g.buyForeignCurrency(plot.localPrice(g.getForeignAccounts().getRate()) * 1.5);
        double cashBefore = g.getCash();
        double vaultBefore = g.getForeignAccounts().getReservesUsd();
        g.setLandPaidFromVault(true);
        boolean bought = g.buyLandParcel(plot.getId());
        g.setLandPaidFromVault(false);
        assertTrue("fixture: the vault paid for a parcel and the treasury's cash did not move",
                bought && g.getCash() == cashBefore
                        && Math.abs(vaultBefore - g.getForeignAccounts().getReservesUsd() - plotUsd) < 1e-6);
        worst = play("after land out of the vault, 24 months", g, 24, verbose);
        assertTrue("a city that paid for land out of the vault conserves money",
                worst.relative() < 1e-4);
        assertTrue("...and nothing moved a pool after any month's audit",
                Math.abs(worstDrift) < .01);

        /* ==================== 2. a city under stress ==================== */
        System.out.println("\n--- broke, banned, taxed and importing: still nothing leaks ---");

        Game s = new Game(GameFiles.scratch("moneycheck"));
        s.run();
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        s.setCashForTest(Founding.WEALTHY_CASH);
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
        // Take the treasury to the edge so the central bank's advances are
        // drawn and repaid inside the window (emergency notes, before 0.7.0).
        s.setCashForTest(20_000);

        worst = play("a stressed city, 180 months", s, 180, verbose);
        assertTrue("a stressed city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);

        /*
         * Bankrupt retail by hand and let the restructure and its ban run.
         *
         * SINCE 0.7.8 THE WHOLE-SECTOR RESTRUCTURE IS THE BACKSTOP'S ALONE, a
         * sector with nothing left, so the fixture takes retail there: a
         * loan, and unpaid bills past everything else it owns, which the
         * restructure forgives - money in from outside the city, the case
         * the audit has to see. It used to hand retail the loan's cash and
         * set its assets to -1, which never stuck (the month's check reads
         * the assets off the balance sheet again before it judges); what
         * restructured it was its debt passing 1.5 times its assets later,
         * the second way in, which is a slice a month now - BankCheck (14)
         * audits those.
         */
        BusinessDebtManager credit = s.getEconomyManager().getBusinessDebtManager();
        credit.issueLoan(Sectors.RETAIL, 5_000_000, s.getMonth());
        double retailOwnsBesidesItsTill = credit.getAssets(Sectors.RETAIL) - credit.getCash(Sectors.RETAIL);
        s.getEconomyManager().setSectorCash(Sectors.RETAIL, -Math.max(0, retailOwnsBesidesItsTill) - 100_000);
        int wholeBefore = credit.getRestructureCount(Sectors.RETAIL);

        worst = play("through a restructure, 60 months", s, 60, verbose);
        assertTrue("fixture: retail went under whole, and was restructured",
                credit.getRestructureCount(Sectors.RETAIL) > wholeBefore);
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
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        hot.setCashForTest(Founding.WEALTHY_CASH);
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
