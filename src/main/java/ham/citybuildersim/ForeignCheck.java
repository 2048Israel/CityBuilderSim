package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The balance of payments, and whether the boundary it is drawn on is honest.
 *
 * WHAT IS ACTUALLY BEING ASKED
 *
 * MoneyAudit has always tracked every flow across the city's edge. What it could
 * not do was tell a HOUSEHOLD from a FOREIGNER - both sat outside the audited
 * pools, for entirely different reasons - and the balance of payments is exactly
 * the foreign half of that list.
 *
 * So the question this file exists for is not "do the numbers look plausible".
 * It is:
 *
 *   1. Is the split EXHAUSTIVE? Every dollar the audit says crossed the edge has
 *      to be either domestic or foreign, and never both. A flow added to
 *      MoneyAudit and forgotten here would silently leave the balance of
 *      payments understating the city's trade, and nothing else in the game
 *      would notice.
 *
 *   2. Is the STOCK the sum of the FLOWS? The reserve is an accumulation, and an
 *      accumulation that has drifted from what it accumulated is two sets of
 *      books wearing one name.
 *
 *   3. Does it survive a reload?
 *
 *   4. And - the whole promise of phase one - does the city behave EXACTLY as it
 *      did before any of this went in?
 */
public class ForeignCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-58s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-58s OK%n", label);
        }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. the rate is pinned ================= */
        out.println("--- phase one: the rate exists and does nothing ---");

        ForeignAccounts fresh = new ForeignAccounts();
        close("the exchange rate opens at parity", fresh.getRate(), 1.0, 1e-12);
        close("...so converting to local money changes nothing", fresh.toLocal(1234.5), 1234.5, 1e-9);
        close("...and back again likewise", fresh.toUsd(1234.5), 1234.5, 1e-9);
        assertTrue("...and a city with no history owes the world nothing",
                fresh.getReserves() == 0 && fresh.getCumulativeBalance() == 0
                        && !fresh.inDeficit());

        /* ================= 2. a city that trades ================= */
        out.println("\n--- a city with something to sell ---");

        Path root = Files.createTempDirectory("foreigncheck");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));

        double worstSplit = 0, worstStock = 0;
        int worstMonth = 0;

        System.setOut(quiet);
        try {
            city.run();
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(template(city, "House"), 500, false);
            city.buildStack(template(city, "Convenience Store"), 8, false);
            city.buildStack(template(city, "Small Grocery Store"), 3, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(template(city, "Textile Mill"), 2, false);

            for (int m = 0; m < 180; m++) {
                city.simulateMonths(1);
                MoneyAudit.Result r = city.getLastMoneyAudit();

                /*
                 * THE SPLIT IS EXHAUSTIVE AND DOES NOT OVERLAP.
                 *
                 * Every dollar the audit counted crossing the edge is either
                 * domestic or foreign. Asserted in both directions, because a
                 * flow tagged into two buckets and a flow tagged into none fail
                 * this the same way and both are bugs.
                 */
                double inSplit = (r.domesticIn() + r.foreignIn()) - r.inflows;
                double outSplit = (r.domesticOut() + r.foreignOut()) - r.outflows;
                if (Math.abs(inSplit) > Math.abs(worstSplit)) worstSplit = inSplit;
                if (Math.abs(outSplit) > Math.abs(worstSplit)) worstSplit = outSplit;

                // ...and the reserve is the sum of what built it.
                ForeignAccounts f = city.getForeignAccounts();
                double drift = f.getCumulativeBalance() - f.balanceFromFlows();
                if (Math.abs(drift) > Math.abs(worstStock)) {
                    worstStock = drift;
                    worstMonth = city.getMonth();
                }
            }
        } finally {
            System.setOut(out);
        }

        ForeignAccounts fx = city.getForeignAccounts();
        out.printf("   over 15 years: sold $%,.0fk abroad, bought $%,.0fk,"
                + " paid $%,.0fk of foreign interest%n",
                fx.getLifetimeExports(), fx.getLifetimeImports(), fx.getLifetimeInterest());
        out.printf("   cumulative balance $%,.0fk, vault $%,.0fk (%s)%n",
                fx.getCumulativeBalance(), fx.getReserves(),
                fx.inDeficit() ? "a net liability" : "no net liability");

        /*
         * THE TWO ARE NOT THE SAME NUMBER, and this fixture is the proof.
         *
         * A city that has traded for fifteen years and never once intervened in
         * the currency market has a large cumulative balance and an EMPTY
         * vault, because export earnings land with the firms that earned them,
         * not in the treasury's foreign account. That is why a country running
         * a trade surplus can still have no reserves to defend itself with, and
         * it is the distinction the old single field could not express.
         */
        assertTrue("a city that has never intervened holds nothing abroad",
                fx.getReserves() == 0);
        assertTrue("...however much it has traded",
                Math.abs(fx.getCumulativeBalance()) > 1);
        close("...so its import cover is nil, honestly", fx.importCover(), 0, 1e-9);

        assertTrue("the fixture actually traded with anybody at all",
                fx.getLifetimeExports() > 0 || fx.getLifetimeImports() > 0);
        assertTrue("...and imported, which every city does",
                fx.getLifetimeImports() > 0);

        close("every dollar crossing the edge is domestic OR foreign, never both",
                worstSplit, 0, .005);
        close("...and the reserve is exactly the flows that built it",
                worstStock, 0, .005);
        out.printf("   worst split error $%.6fk; worst stock drift $%.6fk (month %d)%n",
                worstSplit, worstStock, worstMonth);

        /*
         * WAGES ARE NOT IMPORTS, which is the whole reason for the split.
         *
         * Before it, everything leaving the audited pools looked alike. A city's
         * payroll dwarfs its import bill, so a balance of payments drawn on the
         * old boundary would have reported every city on earth as catastrophically
         * in deficit, on the strength of paying its own people.
         */
        MoneyAudit.Result last = city.getLastMoneyAudit();
        assertTrue("household flows are counted, but not as trade",
                last.domesticOut() > 0);
        assertTrue("...and they are the larger half, as they must be",
                last.domesticOut() > last.foreignOut());

        /* ================= 3. across a reload ================= */
        out.println("\n--- and it survives a reload ---");

        /* Something in the vault too, so the reload check covers both halves. */
        fx.buyReserves(2_500);
        double balanceBefore = fx.getCumulativeBalance();
        double vaultBefore = fx.getReserves();
        double lifetimeBefore = fx.getLifetimeExports();
        double coverBefore = fx.monthlyImports();

        System.setOut(quiet);
        Game reloaded;
        try {
            city.saveGame(3, "the trading city");
            reloaded = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            reloaded.run();
            reloaded.loadGameSave(3);
        } finally {
            System.setOut(out);
        }

        ForeignAccounts back = reloaded.getForeignAccounts();
        assertTrue("the fixture's position is actually worth carrying",
                Math.abs(balanceBefore) > 1);
        close("the cumulative balance reloads exactly",
                back.getCumulativeBalance(), balanceBefore, .005);
        close("...and the vault, which is a different number",
                back.getReserves(), vaultBefore, .005);
        assertTrue("...and they really are different",
                Math.abs(balanceBefore - vaultBefore) > 1);
        close("...and the trade record with it", back.getLifetimeExports(), lifetimeBefore, .005);
        close("...and the import bill the cover is measured against",
                back.monthlyImports(), coverBefore, .005);
        close("...and the exchange rate", back.getRate(), fx.getRate(), 1e-12);

        /*
         * AND THE THREE THAT LOOK DERIVED.
         *
         * openness, the pressure and the absorption are all restruck at the end
         * of a month, so a city loaded on the FIRST of one has nothing to
         * restrike them from. Left out of the save, the trade panel opened at 0%
         * pressure on a city running a chronic deficit and said nothing at all
         * was pushing the rate that was about to move.
         *
         * Guarded by an assertion that the fixture's own figures are non-zero,
         * because three fields that reload as 0 from 0 prove nothing.
         */
        assertTrue("the fixture has an openness worth carrying", fx.getOpenness() > 0);
        close("openness survives the reload", back.getOpenness(), fx.getOpenness(), 1e-9);
        close("...and the pressure reading with it",
                back.getLastPressure(), fx.getLastPressure(), 1e-9);
        close("...and the absorption",
                back.getLastAbsorption(), fx.getLastAbsorption(), 1e-9);

        /* ================= 4. nothing behaves differently ================= */
        out.println("\n--- and phase one changed nothing about the city ---");

        /*
         * The promise of an accounting-only pass is that it is accounting only.
         * Nothing reads the rate, nothing spends the reserve, and no decision
         * anywhere consults either - so a city run twice from the same seed with
         * the accounts watching must come out identical to one where they are
         * simply never asked.
         *
         * Asserted as DETERMINISM here rather than against the old build, which
         * no longer exists to compare with: if the foreign accounts had reached
         * into the simulation, they would have to have reached through some
         * state, and two runs would diverge.
         */
        Path twin = Files.createTempDirectory("foreigncheck-twin");
        int popA, popB;
        double cashA, cashB;
        System.setOut(quiet);
        try {
            popA = run(twin.resolve("a"));
            cashA = lastCash;
            popB = run(twin.resolve("b"));
            cashB = lastCash;
        } finally {
            System.setOut(out);
        }
        out.printf("   two identical cities: %,d people and $%,.2fk against %,d and $%,.2fk%n",
                popA, cashA, popB, cashB);
        assertTrue("two runs of the same city agree on its population", popA == popB);
        close("...and on its treasury, to the cent", cashA, cashB, 1e-6);

        /* ============ 5. every unit that leaves the shelf is paid for ============ */
        out.println("\n--- and the shops are paid for what they hand over ---");

        /*
         * THE REGRESSION THIS FILE EXISTS FOR, after the balance of payments.
         *
         * The utilisation ratios - power, water, roads, sickness, staffing -
         * used to be applied to the shops' REVENUE and not to the UNITS they
         * sold:
         *
         *     productsSold  = min(demand, inventory);
         *     grossRevenue  = productsSold * price * energy * water * road
         *                     * health * fill;
         *
         * So a shop at 35% road throughput handed over every basket and was paid
         * for a third of them. The units were not lost to a leak - they were
         * bought, they left inventory, and the shops restocked to replace them.
         * The city imported food, gave most of it away, and imported more.
         *
         * Measured over 140 months before the fix: 419,779 units sold and
         * 117,706 paid for. SEVENTY-TWO PERCENT given away. It was invisible
         * while imports were cheap and constant, and became the largest item in
         * the balance of payments the moment the exchange rate started moving -
         * the reason a city of a quarter of a million could post a negative GDP.
         *
         * Run in a CONGESTED city on purpose. With every ratio at 1.0 the old
         * code and the new agree exactly, so a fixture that is not short of
         * something proves nothing at all.
         */
        Path shelfRoot = Files.createTempDirectory("foreigncheck-shelf");
        Game jammed = new Game(new GameFiles(shelfRoot.resolve("data"), shelfRoot.resolve("no-legacy")));

        double soldUnits = 0, paidUnits = 0, boughtUnits = 0;
        double worstRatio = 1;

        System.setOut(quiet);
        try {
            jammed.run();
            jammed.getForeignAccounts().pinRate(1.0);
            jammed.getLandManager().setOwnedSqFt(40_000_000);
            // Houses and shops, and deliberately NO roads - the whole point is a
            // city that cannot move its goods.
            jammed.buildStack(template(jammed, "House"), 600, false);
            jammed.buildStack(template(jammed, "Convenience Store"), 10, false);
            jammed.buildStack(template(jammed, "Construction Depot"), 4, false);
            jammed.buildStack(template(jammed, "Coal Power Plant"), 1, false);
            jammed.buildStack(template(jammed, "Water Treatment Plant"), 1, false);

            CommercialHandler shops = jammed.getEconomyManager().getCommercialHandler();
            for (int m = 0; m < 120; m++) {
                jammed.simulateMonths(1);
                double sold = shops.getReportProductsSold();
                /*
                 * DIVIDED BY THE PRICE THE REVENUE WAS STRUCK AT, not by
                 * today's. Since scarcity started lifting the shelf price, the
                 * live price is the one set AFTER this month's takings were
                 * booked, and dividing by it reported a hole in an identity
                 * that was still perfectly true.
                 */
                double paid = shops.getReportSellPrice() > 0
                        ? shops.getGrossRevenue() / shops.getReportSellPrice() : 0;
                soldUnits += sold;
                paidUnits += paid;
                boughtUnits += shops.getReportLocalImports() + shops.getReportGlobalImports();
                worstRatio = Math.min(worstRatio, shops.getReportRoadRatio());
            }
        } finally {
            System.setOut(out);
        }

        out.printf("   a city at %.0f%% road throughput over ten years:%n", worstRatio * 100);
        out.printf("   bought %,.0f units, sold %,.0f, was paid for %,.0f%n",
                boughtUnits, soldUnits, paidUnits);

        assertTrue("the fixture is genuinely short of something",
                worstRatio < .95);
        assertTrue("...and the shops actually traded", soldUnits > 0);

        close("every unit that leaves the shelf is paid for",
                soldUnits - paidUnits, 0, Math.max(1, soldUnits * 1e-6));

        /*
         * ...and the consequence that matters for the balance of payments: the
         * shops cannot be buying wildly more than they sell, month after month.
         * Some accumulation is legitimate - a shelf is stock - but it is bounded
         * by the restock target, not proportional to everything sold.
         */
        out.printf("   bought/sold ratio %.3f%n", boughtUnits / Math.max(1, soldUnits));
        assertTrue("...so the shops are not importing to replace goods nobody bought",
                boughtUnits <= soldUnits * 1.15);

        /* ============ 5b. reserves you sell are reserves you no longer have ============ */
        out.println("\n--- and a reserve sold is a reserve gone ---");

        /*
         * THE BUG JERUS FOUND BY PLAYING: "i can go to the tab and sell my
         * reserves... and it builds back up?"
         *
         * It did. An intervention moved the stock in TWO places for the same
         * transaction - directly in buyReserves()/sellReserves(), and again in
         * takeMonth(), which adds the whole financial account to the stock. The
         * two cancelled for a purchase, so buying reserves cost real cash and
         * built nothing; and they COMPOUNDED for a sale, so selling raised cash
         * and left the stock untouched. A player could sell the same reserves
         * every month for ever, which is a money printer.
         *
         * The fix is the textbook treatment rather than a patch. A balance of
         * payments reads
         *
         *     current + capital + financial account = change in reserve assets
         *
         * so reserve transactions are the FINANCING item that settles the
         * balance, not a component of it. Scope.RESERVE keeps them foreign -
         * the money really does cross the edge, and the domestic/foreign split
         * has to stay exhaustive - and out of financialAccount(), so takeMonth()
         * leaves the stock alone and the direct effect stands by itself.
         */
        ForeignAccounts vault = new ForeignAccounts();

        vault.startMonth();
        vault.buyReserves(10_000);
        vault.takeMonth(intervention(0, vault.getBoughtThisMonth()), 50_000);
        close("buying reserves actually buys reserves", vault.getReserves(), 10_000, 1e-9);

        vault.startMonth();
        double soldNow = vault.sellReserves(4_000);
        vault.takeMonth(intervention(vault.getSoldThisMonth(), 0), 50_000);
        close("...and selling them spends them", vault.getReserves(), 6_000, 1e-9);
        close("...for exactly what was asked", soldNow, 4_000, 1e-9);

        /*
         * AND THE STOCK RUNS OUT, which is the half that makes the screen a
         * decision. Sell a thousand a month out of six thousand and the seventh
         * month sells nothing - the treasury has to find the money somewhere
         * that has it: taxes, a domestic bond, or borrowing abroad.
         */
        double raised = 0;
        int monthsItLasted = 0;
        for (int m = 0; m < 24; m++) {
            vault.startMonth();
            double got = vault.sellReserves(1_000);
            vault.takeMonth(intervention(vault.getSoldThisMonth(), 0), 50_000);
            raised += got;
            if (got > 0) monthsItLasted++;
        }
        out.printf("   $6,000 of reserves, sold $1,000 a month: raised $%,.0f over %d months%n",
                raised, monthsItLasted);
        close("a reserve stock cannot be sold twice", raised, 6_000, 1e-9);
        close("...and what is left is nothing", vault.getReserves(), 0, 1e-9);
        assertTrue("...and it ran out when it should have", monthsItLasted == 6);

        /* Asking for more than there is sells what there is, and no more. */
        vault.buyReserves(2_500);
        vault.startMonth();
        double greedy = vault.sellReserves(999_999);
        close("asking for more than the city holds sells what it holds",
                greedy, 2_500, 1e-9);
        close("...and never lends the difference into existence",
                vault.getReserves(), 0, 1e-9);
        close("...and a city with nothing sells nothing",
                vault.sellReserves(1_000), 0, 1e-9);
        assertTrue("...and the stock never goes negative through selling",
                vault.getReserves() >= 0);

        /*
         * AND THE BUFFER IS NOW A THING YOU HAVE TO BUILD.
         *
         * absorption() damps the pressure reaching the exchange rate in
         * proportion to import cover, and cover is measured against the vault.
         * While the vault and the cumulative balance were one number, every
         * trading city had thousands of months of cover for free - the mainline
         * playtest read 43,892 - so absorption was permanently maximal and the
         * whole mechanism did nothing anybody could influence.
         *
         * Split, a treasury that has never bought foreign money has none, and
         * absorbs nothing. That is not a nerf; it is what a reserve buffer IS.
         * Building one now competes with building a school out of the same
         * dollar, which is the decision the mechanic was always meant to be.
         */
        ForeignAccounts bare = new ForeignAccounts();
        for (int m = 0; m < 30; m++) bare.takeMonth(month(2_000, 4_000), 20_000);
        close("a treasury that never bought reserves absorbs nothing",
                bare.absorption(), 0, 1e-9);
        assertTrue("...however long it has been trading",
                Math.abs(bare.getCumulativeBalance()) > 1_000);

        bare.buyReserves(bare.monthlyImports() * ForeignAccounts.COMFORTABLE_COVER);
        assertTrue("...and buying a comfortable buffer is what earns the damping",
                bare.absorption() > .8 * ForeignAccounts.MAX_ABSORPTION);

        /* ============ 6. the rate is bounded, and moves the right way ============ */
        out.println("\n--- and the currency moves on the balance of payments ---");

        /*
         * DIRECTION FIRST, because it is the half a sign error hides in.
         *
         * The rate is quoted local-per-USD, so a WEAKER currency is a BIGGER
         * number. A city that keeps buying more than it sells has to bid more of
         * its own money for each foreign dollar, so a sustained deficit must push
         * the rate UP. A surplus pushes it down. Anyone reading the field name
         * alone gets this backwards half the time, which is exactly why it is
         * asserted rather than reasoned about.
         *
         * Driven straight at ForeignAccounts with synthetic months rather than
         * through a city, because a city has a hundred other reasons for its
         * rate to move and none of them are the thing under test.
         */
        ForeignAccounts weak = new ForeignAccounts();
        for (int m = 0; m < 240; m++) {
            weak.takeMonth(month(400, 1_000), 6_000);   // sells 400, buys 1,000
            weak.repriceCurrency();
        }
        out.printf("   240 months of deficit: rate %.4f (pressure %+.2f)%n",
                weak.getRate(), weak.getLastPressure());
        assertTrue("a sustained deficit weakens the currency", weak.getRate() > 1.0);
        /*
         * AND THE SIGN, which is the half of this a reader gets wrong.
         *
         * pressure() is DEPRECIATION pressure - positive means the currency
         * should weaken - while the current account it is built from is
         * negative in a deficit. The minus sign that turns one into the other
         * lives inside pressure(), and this is the assertion that says it is
         * still there. Written the other way round first, and it failed.
         */
        assertTrue("...and the pressure that did it reads as depreciation pressure",
                weak.getLastPressure() > 0);

        ForeignAccounts strong = new ForeignAccounts();
        for (int m = 0; m < 240; m++) {
            strong.takeMonth(month(1_000, 400), 6_000);
            strong.repriceCurrency();
        }
        out.printf("   240 months of surplus: rate %.4f (pressure %+.2f)%n",
                strong.getRate(), strong.getLastPressure());
        assertTrue("a sustained surplus strengthens it", strong.getRate() < 1.0);
        assertTrue("...and reads as appreciation pressure", strong.getLastPressure() < 0);

        /*
         * AND IT CANNOT RUN AWAY. Two thousand months of the most lopsided trade
         * the model can express, in both directions, with the rate checked every
         * single month rather than only at the end - a rate that goes to
         * infinity and comes back would pass an end-state check.
         */
        ForeignAccounts runaway = new ForeignAccounts();
        boolean bounded = true, finite = true;
        for (int m = 0; m < 2_000; m++) {
            runaway.takeMonth(m % 400 < 200 ? month(10, 50_000) : month(50_000, 10), 60_000);
            runaway.repriceCurrency();
            double r = runaway.getRate();
            if (Double.isNaN(r) || Double.isInfinite(r)) finite = false;
            if (r < ForeignAccounts.MIN_RATE - 1e-9 || r > ForeignAccounts.MAX_RATE + 1e-9) {
                bounded = false;
            }
        }
        out.printf("   2,000 months of violent swings: rate %.4f%n", runaway.getRate());
        assertTrue("the rate stays a number", finite);
        assertTrue("...and inside its bounds, every month of the way", bounded);

        /* AND A PINNED RATE IS PINNED. */
        ForeignAccounts held = new ForeignAccounts();
        held.pinRate(1.00);
        for (int m = 0; m < 120; m++) {
            held.takeMonth(month(100, 9_000), 6_000);
            held.repriceCurrency();
        }
        assertTrue("a pinned rate says it is pinned", held.isPinned());
        close("...and does not move, however bad the deficit", held.getRate(), 1.00, 1e-12);

        /* ================= 7. and it comes home ================= */
        out.println("\n--- and a currency that has wandered comes back ---");

        /*
         * THE PARITY ANCHOR, and the bug it was written for.
         *
         * The pressure signal is a RATIO of two local-currency figures - the
         * current account over the trade volume - and both scale with the rate
         * identically. Moving the rate therefore does not move the number that
         * moves the rate. It only bites through volumes, and in a city whose
         * exports are mines selling whatever they dig at the world price, the
         * volumes barely answer. The first floating playtest ran the currency
         * 4x into its floor over 333 years and shut 8 of 34 mines on the way.
         *
         * So the same basket costing the same abroad pulls the rate home. Tested
         * by driving it away and then taking the pressure off: the deficit stops,
         * nothing else changes, and the rate has to return on its own.
         */
        ForeignAccounts wandered = new ForeignAccounts();
        for (int m = 0; m < 240; m++) {
            wandered.takeMonth(month(200, 4_000), 6_000);
            wandered.repriceCurrency();
        }
        double displaced = wandered.getRate();
        assertTrue("the fixture actually moved the rate somewhere", displaced > 1.05);

        for (int m = 0; m < 600; m++) {
            wandered.takeMonth(month(2_000, 2_000), 6_000);   // trade in balance
            wandered.repriceCurrency();
        }
        out.printf("   %.4f displaced, %.4f after 50 years of balanced trade%n",
                displaced, wandered.getRate());
        assertTrue("balanced trade brings the rate back toward parity",
                wandered.getRate() < displaced);
        assertTrue("...and most of the way home",
                Math.abs(wandered.deviationFromParity()) < Math.abs(displaced - 1) * .25);

        /* ========== 8. a devaluation improves the current account ========== */
        out.println("\n--- and a cheaper currency sells more than it buys ---");

        /*
         * MARSHALL-LERNER, which is the whole reason a floating rate is worth
         * having: a devaluation only fixes a deficit if the two elasticities
         * together beat one. If they do not, the weaker currency makes the same
         * imports dearer without buying fewer of them and the deficit gets
         * WORSE - which is a real outcome, and one this model is allowed to
         * produce, but it must be measured rather than assumed.
         *
         * Two identical cities, one pinned at parity and one pinned 40% weaker,
         * played the same way. Pinned rather than floating so the rate is the
         * only difference between them; a floating pair would each find their
         * own rate and the comparison would measure nothing.
         */
        Path ml = Files.createTempDirectory("foreigncheck-ml");
        double lifeCaPar, lifeCaWeak, lifeImpPar, lifeImpWeak, lifeExpPar, lifeExpWeak;
        System.setOut(quiet);
        try {
            ForeignAccounts par = devaluationCity(ml.resolve("par"), 1.00).getForeignAccounts();
            lifeImpPar = par.getLifetimeImports();
            lifeExpPar = par.getLifetimeExports();
            lifeCaPar = lifeExpPar - lifeImpPar - par.getLifetimeInterest();

            ForeignAccounts dev = devaluationCity(ml.resolve("dev"), 1.40).getForeignAccounts();
            lifeImpWeak = dev.getLifetimeImports();
            lifeExpWeak = dev.getLifetimeExports();
            lifeCaWeak = lifeExpWeak - lifeImpWeak - dev.getLifetimeInterest();
        } finally {
            System.setOut(out);
        }

        /*
         * MEASURED OVER THE RUN, NOT AT THE END OF IT.
         *
         * The trailing month is the wrong instrument here and it took a probe to
         * see why: this fixture peaks around 6,000 people and then declines, and
         * the two cities reach that peak in different years. Read at month 180
         * they are compared at different points on their own curves, and the
         * trailing import bill says more about which side of the peak each one
         * is on than about what its currency is worth.
         *
         * The cumulative current account has no such problem. It is every month
         * of the run added up, and it is the thing a devaluation is supposed to
         * fix.
         */
        out.printf("   at parity:  %,.0fk out, %,.0fk in, trade balance %,.0fk"
                + " (current account $%,.0fk)%n",
                lifeImpPar, lifeExpPar, lifeExpPar - lifeImpPar, lifeCaPar);
        out.printf("   40%% weaker: %,.0fk out, %,.0fk in, trade balance %,.0fk"
                + " (current account $%,.0fk)%n",
                lifeImpWeak, lifeExpWeak, lifeExpWeak - lifeImpWeak, lifeCaWeak);

        assertTrue("the fixture trades enough for the question to mean anything",
                lifeImpPar > 500);

        /*
         * ON THE TRADE BALANCE, AND NOT ON THE IMPORT BILL ALONE.
         *
         * Marshall-Lerner is a statement about the BALANCE: a devaluation helps
         * if the two elasticities together beat one, and the export side is
         * half of "together". This asserted the import half on its own, which
         * is a stronger claim than the condition makes and one this city stopped
         * satisfying on 2026-09-09.
         *
         * Measured, at 180 months: the weaker city imports 1,376k against
         * 1,344k - 2.4% MORE - and exports 5,545k against 3,813k, 45% more. The
         * balance goes from 2,469k to 4,169k, up 69%. The condition holds
         * comfortably; it just does not hold through the import line alone, and
         * it never had to.
         *
         * The import figure is also the wrong instrument in this fixture for a
         * second reason worth writing down: both cities stop importing entirely
         * around month 61, once import substitution takes hold, so the lifetime
         * import bill is a fact about the first five years of two cities that
         * grew at slightly different speeds rather than about what their money
         * is worth.
         *
         * AND NOT ON THE CURRENT ACCOUNT EITHER. That subtracts foreign
         * interest, and a foreign coupon is denominated abroad - so a 40%
         * weaker currency makes the same coupon 40% dearer in local money
         * whatever the trade does. On this pair the interest is roughly $80M
         * against a trade balance in the thousands, so the current account
         * measures the revaluation and nothing else. That is a real effect and
         * ForeignDebtCheck is where it belongs; it is not an elasticity.
         */
        assertTrue("a weaker currency sells more abroad than it buys",
                (lifeExpWeak - lifeImpWeak) > (lifeExpPar - lifeImpPar));
        assertTrue("...and it is the exports doing it", lifeExpWeak > lifeExpPar);

        /*
         * AND THE MECHANICAL HALF, tested straight rather than through a city.
         *
         * Whatever the volumes do, a devaluation must make a foreign good cost
         * more local money. If this ever stops being true the rate has been
         * wired past something.
         */
        double parPrice  = worldFoodPrice(1.00);
        double weakPrice = worldFoodPrice(1.40);
        out.printf("   imported food: $%.4f at parity, $%.4f at 1.40%n", parPrice, weakPrice);
        assertTrue("the fixture has a world price to move at all", parPrice > 0);
        close("a 40% devaluation is a 40% rise in what an import costs",
                weakPrice / parPrice, 1.40, .02);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * A synthetic month, which is the only honest way to test the rate rule.
     *
     * Only the four trade and income fields matter to takeMonth; the rest of a
     * Result describes a city this fixture does not have.
     */
    /** A month whose only foreign flow is the treasury working its own vault. */
    static MoneyAudit.Result intervention(double soldIn, double boughtOut) {
        double[] f = new double[10];
        f[8] = soldIn;
        f[9] = boughtOut;
        return new MoneyAudit.Result(0, 0, 0, soldIn, boughtOut, 0, "", f);
    }

    static MoneyAudit.Result month(double exports, double imports) {
        double[] f = new double[8];
        f[0] = exports;
        f[1] = imports;
        return new MoneyAudit.Result(0, 0, 0, exports, imports, 0, "", f);
    }

    /** The same city twice, differing only in what its currency is worth. */
    static Game devaluationCity(Path dir, double rate) throws Exception {
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        g.run();
        g.getForeignAccounts().pinRate(rate);
        g.getEconomyManager().setExchangeRate(rate);
        g.getLandManager().setOwnedSqFt(30_000_000);
        g.buildStack(template(g, "House"), 500, false);
        g.buildStack(template(g, "Convenience Store"), 8, false);
        g.buildStack(template(g, "Small Grocery Store"), 3, false);
        g.buildStack(template(g, "Construction Depot"), 4, false);
        g.buildStack(template(g, "Coal Power Plant"), 1, false);
        g.buildStack(template(g, "Water Treatment Plant"), 1, false);
        g.buildStack(template(g, "Textile Mill"), 2, false);
        g.buildStack(template(g, "Paved Road"), 40, false);
        g.buildStack(template(g, "Walk-in Clinic"), 4, false);
        g.buildStack(template(g, "Municipal Cemetery"), 1, false);
        g.simulateMonths(180);
        return g;
    }

    /** What a foreign basket costs in local money at a given rate. */
    static double worldFoodPrice(double rate) throws Exception {
        Path dir = Files.createTempDirectory("foreigncheck-px" + (int) (rate * 100));
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            g.run();
            g.getForeignAccounts().pinRate(rate);
            g.getEconomyManager().setExchangeRate(rate);
        } finally {
            System.setOut(out);
        }
        return g.getEconomyManager().getFoodMarket().getImportPrice();
    }

    static double lastCash;

    /** One deterministic city, played the same way twice. */
    static int run(Path dir) throws Exception {
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        g.run();
        g.getLandManager().setOwnedSqFt(20_000_000);
        g.buildStack(template(g, "House"), 300, false);
        g.buildStack(template(g, "Convenience Store"), 6, false);
        g.buildStack(template(g, "Construction Depot"), 4, false);
        g.buildStack(template(g, "Coal Power Plant"), 1, false);
        g.simulateMonths(90);
        lastCash = g.getCash();
        return g.getPopulationManager().getPopulation();
    }
}
