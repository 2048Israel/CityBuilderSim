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
 *   2. Is the STOCK the sum of the FLOWS? The cumulative balance is an
 *      accumulation, and an accumulation that has drifted from what it
 *      accumulated is two sets of books wearing one name. (It was "the
 *      reserve" when this was written. The vault, split from it since, is
 *      bought and sold rather than accumulated: sections 5b and 9 to 13.)
 *
 *   3. Does it survive a reload?
 *
 *   4. And - the whole promise of phase one - does the city behave EXACTLY as it
 *      did before any of this went in?
 *
 * And since 2026-09-21, sections 9 to 12: is the vault kept in the money it
 * actually is? Dollars that stay dollars when the currency moves, a local
 * value that moves with it, a revaluation that is not a flow, a reform that
 * cannot reach them, and an older save whose vault comes back at the rate it
 * was saved at. Section 13: does the vault defend the currency without
 * holding it down - and can a screen show the push without rewriting the
 * month's record of it? Section 14 (0.7.6): does converting cash for land
 * push the rate exactly as buying the same dollars for the vault would?
 */
public class ForeignCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** What the last devaluationCity() built from its own scrapped plant, in dollars at the landed price. */
    static double scrappedPlantBuiltWith;

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
        double yearDomesticOut = 0, yearTrade = 0;
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
            city.buildStack(template(city, "Industrial Bakery"), 2, false);

            for (int m = 0; m < 180; m++) {
                city.simulateMonths(1);
                MoneyAudit.Result r = city.getLastMoneyAudit();
                // The last year's households' flows and trade balance, for
                // the swamp assertion below (0.7.7).
                if (m >= 168) { yearDomesticOut += r.domesticOut(); yearTrade += r.tradeBalance(); }

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

                // ...and the cumulative balance is the sum of what built it.
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
         *
         * SINCE 2026-09-21 THE FOUNDERS INTERVENE ONCE, on day one: a new city
         * opens with US$1B bought at the opening rate (Game's THE FOUNDING
         * RESERVE), booked as the purchase it is. So this city has intervened,
         * exactly once, and the property is asserted in the form that still
         * means something: the vault holds precisely the dollars the treasury
         * bought and not one cent of fifteen years of trade. Had the founding
         * set the stock without a purchase, a city that never bought anything
         * would hold a billion abroad and this would be false in both forms.
         *
         * AND SINCE 0.7.2 THE CENTRAL BANK SPENDS IT, which is what it is for
         * (ForeignAccounts, A DEFENCE THAT SPENDS): a month the currency is
         * pushed weaker on a deficit, the vault's dollars are sold against it.
         * This fixture's fifteen years spend most of the founders' billion -
         * the measurement batch D was asked for, printed here - so the two
         * lines were restated, not loosened: the vault is the founders'
         * dollars less exactly what the defence sold, and the intervention
         * record is the day-one purchase less exactly what those sales
         * fetched. Still not one cent of fifteen years of trade in either.
         */
        CentralBank books = city.getCentralBank();
        out.printf("   the defence sold US$%,.0fk of the founders' US$%,.0fk over the fifteen years%n",
                fx.getDefenceUsdLifetime(), Game.FOUNDING_RESERVE_USD);
        close("a city that never intervened holds its founders' dollars, less the defence's",
                fx.getReservesUsd(), Game.FOUNDING_RESERVE_USD - fx.getDefenceUsdLifetime(), 1e-6);
        close("...and its record is the day-one purchase, less what the defence fetched",
                fx.getLifetimeIntervention(),
                Game.FOUNDING_RESERVE_USD * ForeignAccounts.OPENING_RATE - books.vaultSpent(), 1e-6);
        assertTrue("...however much it has traded",
                Math.abs(fx.getCumulativeBalance()) > 1);
        assertTrue("...so it has cover from the start, as they meant",
                fx.importCover() > 0);

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
        /*
         * ...AND LARGE ENOUGH THAT COUNTING THEM AS TRADE WOULD SWAMP THE
         * BALANCE, which is the claim the paragraph above actually makes and
         * the only form of it that is a law.
         *
         * This asserted "wages are bigger than the import bill", and that has
         * now been overtaken twice by things that are not wrong. First the
         * sectors' own money going abroad for the world's rate
         * (OutwardInvestment, 2026-09-10), which a small city with a large
         * treasury can move faster than it pays people, and is right to. Then
         * the ninth sector (2026-09-13), which buys steel - a city whose
         * largest industry imports its raw material can perfectly well import
         * more than it pays in wages, and half the industrial world does.
         *
         * What is a law is the thing the split exists for: the payroll is big
         * against the TRADE BALANCE, so a balance of payments drawn on the old
         * boundary would have been a number about wages wearing a trade
         * balance's name. Measured here it is two orders of magnitude bigger.
         */
        /*
         * ...OVER THE LAST YEAR, not the last month, since 0.7.7: the 0.7.7
         * city's final month carried a one-off shipment - $14,711k of
         * imports against $1,500-2,900k in each of the thirty months before
         * it - and a law about the size of a payroll against a trade balance
         * is not a claim about one delivery. Every one of those thirty months
         * held it on its own; the year holds it whole.
         */
        out.printf("   the last year: $%,.0fk left the pools for households against a trade balance of $%,.0fk%n",
                yearDomesticOut, yearTrade);
        assertTrue("...and large enough that counting them as trade would swamp the balance",
                yearDomesticOut > Math.abs(yearTrade));

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
        close("...in the dollars it is held in, exactly",
                back.getReservesUsd(), fx.getReservesUsd(), 1e-9);
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

        double soldUnits = 0, paidUnits = 0, boughtUnits = 0, soldKg = 0;
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

            ham.citybuildersim.sectors.Retail shops = jammed.getSectors().retail();
            for (int m = 0; m < 120; m++) {
                jammed.simulateMonths(1);
                double sold = shops.getProductsSold();
                /*
                 * THE UNITS THE LEDGER HOLDS, not the takings divided by
                 * today's price. Since scarcity started lifting the shelf
                 * price, the live price is the one set AFTER this month's
                 * sale was booked, and dividing by it reported a hole in an
                 * identity that was still perfectly true. Since the sector
                 * template the sale is a Trade in the month's ledger, and
                 * the ledger carries the units it was paid for.
                 */
                double paid = shops.pending().unitsSold.getOrDefault(Good.GROCERIES, 0.0);
                soldUnits += sold;
                paidUnits += paid;
                /*
                  * TWO UNITS, AND THE RATIO BELOW NEEDS THEM TO BE ONE.
                  *
                  * What the shops BUY is kilograms of thirteen goods; what
                  * they SELL is person-months of groceries, and one of those
                  * is fifty of the other. Comparing them straight reported a
                  * bought/sold ratio of 60 and read as the shops importing
                  * wildly more than they sold - which was the shelf being
                  * weighed in kilos against a till counting customers, not a
                  * balance-of-payments problem. So the sale is converted to
                  * kilograms at the basket the shops were actually stocking
                  * that month.
                  */
                 double basketKg = 0;
                 for (Good fg : ham.citybuildersim.sectors.Retail.SHELF) {
                     Sector.Input food = shops.input(fg);
                     boughtUnits += food.boughtLocal + food.imported;
                     basketKg += shops.kgPerHead(fg);
                 }
                 soldKg += sold * basketKg;
                worstRatio = Math.min(worstRatio, shops.getRoadRatio());
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
        out.printf("   bought/sold ratio %.3f, both in kilograms%n",
                boughtUnits / Math.max(1, soldKg));
        assertTrue("...so the shops are not importing to replace goods nobody bought",
                boughtUnits <= soldKg * 1.15);

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
        double salvagePar, salvageWeak;
        double[] foodPar, foodWeak;
        System.setOut(quiet);
        try {
            foodPar = new double[2];
            ForeignAccounts par = devaluationCity(ml.resolve("par"), 1.00, foodPar).getForeignAccounts();
            lifeImpPar = par.getLifetimeImports();
            salvagePar = scrappedPlantBuiltWith;
            lifeExpPar = par.getLifetimeExports();
            lifeCaPar = lifeExpPar - lifeImpPar - par.getLifetimeInterest();

            foodWeak = new double[2];
            ForeignAccounts dev = devaluationCity(ml.resolve("dev"), 1.40, foodWeak).getForeignAccounts();
            lifeImpWeak = dev.getLifetimeImports();
            salvageWeak = scrappedPlantBuiltWith;
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
        /*
         * AND IN THE WORLD'S MONEY, NOT THE CITY'S (2026-09-10).
         *
         * This city is a price-taker at both ends - ore, steel, food and,
         * since the material unit was repriced, its building materials are
         * all priced abroad in dollars - so the only thing a devaluation can
         * change is VOLUMES, and volumes are what the balance in dollars
         * measures. The balance in local money measures volumes times the
         * rate, and this fixture's import side is a fixed public-works
         * programme: a power plant, a water plant and forty roads, about
         * $900M of material that gets bought whatever it costs. Forty percent
         * dearer, in local money, is forty percent more import bill, and no
         * elasticity in the world moves an order the fixture placed on
         * month one. Measured: 1,047,035k out at parity against 1,483,908k
         * weaker (x1.42), against 25,926k and 166,424k of exports.
         *
         * In dollars the same figures are $1,047M against $1,060M out - the
         * weaker city built a little more - and $26M against $119M in. The
         * balance improves by $80M, and it is the exports doing it, which is
         * the claim.
         */
        double usdBalancePar  = (lifeExpPar - lifeImpPar) / 1.00;
        double usdBalanceWeak = (lifeExpWeak - lifeImpWeak) / 1.40;
        out.printf("   in dollars: balance $%,.0fk at parity, $%,.0fk 40%% weaker%n",
                usdBalancePar, usdBalanceWeak);
        /*
         * MEASURED, NOT ASSERTED, since 2026-09-11 - the outcome this comment
         * always said the model was allowed to produce, it now produces.
         *
         * Since the crews draw material as they build, this fixture's order
         * - five hundred houses and the rest, nine years of work for its
         * builders - is a nine-year boom in building material, and the
         * seventh sector does what a materials industry does in a boom: it
         * builds plants against the order book, six of them, and when the
         * queue empties they export at the floor for the rest of the run.
         * That is nine tenths of both cities' exports now. The weaker city
         * pays forty percent more local money for the same programme, has
         * that much less for everything else, grows slower - 7,100 people
         * against 7,500 at the end - gets its plants up a year later, and
         * exports LESS in dollars: $630M against $663M, on the same $1,030M
         * of imports. Given twice the land it builds nine plants to the
         * parity city's ten and the gap is wider. A devaluation that makes
         * a fixed import programme dearer in a city whose export industry is
         * built by that city's own slower growth is the case the theorem
         * warns about, and the model finds it on its own.
         *
         * What the fixture can still assert: that the programme costs the
         * same in the world's money whatever the currency does, which is
         * the premise of the whole comparison, and that the mechanical half
         * below holds. The elasticity itself is printed, food separately -
         * the good the rate can move without a boom behind it - for whoever
         * next changes what this city grows.
         */
        /*
         * ...WHEREVER ITS MATERIAL CAME FROM (0.7.8). A failing sector's plant
         * is sold to the builders for its material now, and the crews build
         * from that stock before they buy - so part of the programme can come
         * out of the city's own scrapped shops and plants instead of from
         * abroad, and how much depends on how much each city happened to
         * scrap. That is the rule change reaching this premise, one step along
         * the chain from the three sectors held out below. Measured on the
         * build that introduced it: the parity city built 3,743 units from
         * scrapped plant and the weaker one 524, Construction imported
         * $815,886k against $848,928k, and the import bill alone read 1.0607.
         * The programme is the same programme; counted whole - its imports,
         * plus what it built from scrapped plant at what that material would
         * have cost to land - it reads 0.9945 (with the sale switched off in
         * a probe, 1.0034).
         */
        double usdImpPar = lifeImpPar / 1.00 + salvagePar, usdImpWeak = lifeImpWeak / 1.40 + salvageWeak;
        out.printf("   food, in dollars: out $%,.0fk in $%,.0fk at parity; out $%,.0fk in $%,.0fk weaker%n",
                foodPar[0], foodPar[1], foodWeak[0] / 1.40, foodWeak[1] / 1.40);
        assertTrue("the fixture sells food abroad at all", foodPar[0] > 0);
        out.printf("   the programme, in dollars: $%,.0fk at parity, $%,.0fk weaker (x%.4f); of it built from scrapped plant $%,.0fk and $%,.0fk%n",
                usdImpPar, usdImpWeak, usdImpPar > 0 ? usdImpWeak / usdImpPar : 0, salvagePar, salvageWeak);
        close("the same programme costs the same in the world's money",
                usdImpWeak / usdImpPar, 1.0, .05);

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

        vaultInDollars();
        reserveDefends();
        landByConversion();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * Sections 9 to 12: the vault kept in dollars (2026-09-21).
     *
     * FOUND while checking Jerus's founding reserve, before building it. The
     * vault was a LOCAL figure at the price paid: buyReserves() added the local
     * cash spent and getReservesUsd() divided by today's rate, so when the
     * currency fell a hundredfold a US$1B vault read US$10M and import cover
     * shrank a hundredfold exactly when it was needed - while the dollar debt
     * on the same class was revalued every month. A reserve is the one thing
     * that is supposed to gain when your currency falls. In a method of its
     * own so its figures cannot collide with main's.
     */
    static void vaultInDollars() throws Exception {

        /* ============ 9. the vault is held in dollars ============ */
        out.println("\n--- and the vault is held in the money it is held in ---");

        /*
         * Buy US$X at a rate r for rX of local money, let the currency halve
         * to 2r, and the vault must still hold US$X - worth 2rX at home now,
         * with rX booked as what the currency did to it. Sell it all and it
         * raises 2rX: the treasury is rX ahead on its own currency's fall,
         * which is what a reserve is for.
         *
         * In the month's own order: the rate moves, then the vault is valued,
         * then the treasury trades. revalueVault() is struck right after the
         * reprice in Game.nextMonth, and nothing moves the rate in between.
         */
        final double r = 1.25;             // local per US dollar the day the treasury buys
        final double usd = 8_000;          // what it buys, in dollars
        final double usdImports = 2_000;   // a month's imports, in dollars, whatever the rate

        ForeignAccounts fx = new ForeignAccounts();
        fx.pinRate(r);
        fx.revalueVault();
        for (int m = 0; m < 24; m++) {
            fx.takeMonth(month(usdImports * 1.1 * r, usdImports * r), 50_000);
        }
        double balanceBefore = fx.getCumulativeBalance();
        double flowsBefore = fx.balanceFromFlows();

        fx.startMonth();
        fx.buyReserves(usd * r);
        close("rX of local money at rate r buys US$X, and US$X is held",
                fx.getReservesUsd(), usd, 1e-9);
        close("...worth rX at home the day it is bought", fx.getReserves(), usd * r, 1e-9);
        double coverBefore = fx.importCover();
        close("...X over the dollar import bill, in months of cover",
                coverBefore, usd / usdImports, 1e-9);

        fx.pinRate(2 * r);
        fx.revalueVault();
        close("the currency halves and the vault still holds US$X", fx.getReservesUsd(), usd, 1e-9);
        close("...now worth 2rX at home", fx.getReserves(), 2 * r * usd, 1e-9);
        close("...and sellable for 2rX", fx.sellableReserves(), 2 * r * usd, 1e-9);
        close("...and the move is booked as the vault's revaluation: rX",
                fx.getLastVaultRevaluation(), r * usd, 1e-9);
        assertTrue("...positive: a falling currency is a dollar vault's gain",
                fx.getLastVaultRevaluation() > 0);
        close("...not a flow: the cumulative balance did not move",
                fx.getCumulativeBalance(), balanceBefore, 1e-9);
        close("...nor the flows it is rebuilt from", fx.balanceFromFlows(), flowsBefore, 1e-9);

        /*
         * COVER DOES NOT MOVE WITH THE CURRENCY, once the import bill has been
         * struck at the new rate - the same dollar goods, twice the local
         * money. The old vault, at the price paid, would have read half.
         */
        for (int m = 0; m < 400; m++) {
            fx.takeMonth(month(usdImports * 1.1 * 2 * r, usdImports * 2 * r), 50_000);
        }
        out.printf("   cover %.4f months at r and %.4f at 2r; the vault at the price paid would read %.4f%n",
                coverBefore, fx.importCover(), usd * r / fx.monthlyImports());
        close("the bill struck at 2r, the cover is what it was at r",
                fx.importCover(), coverBefore, 1e-6);

        fx.startMonth();
        double raised = fx.sellReserves(Double.MAX_VALUE);
        close("sold at 2r, the whole vault raises 2rX of cash", raised, 2 * r * usd, 1e-6);
        close("...and leaves nothing, not even a division's rounding",
                fx.getReservesUsd(), 0, 0);
        close("...so the treasury is rX ahead: the revaluation it saw",
                raised - usd * r, fx.getLastVaultRevaluation(), 1e-6);
        close("...as the record says: bought for rX, sold for 2rX",
                fx.getLifetimeIntervention(), -usd * r, 1e-6);
        close("and the cumulative balance is still the sum of its flows",
                fx.getCumulativeBalance(), fx.balanceFromFlows(), 1e-6);

        /* ============ 10. and the move is not money anybody moved ============ */
        out.println("\n--- and what the currency does to the vault is not a flow ---");

        /*
         * The same, through a city's month. Nobody can spend a revaluation
         * until the dollars are sold, and a sale is the only thing that
         * crosses the audit's edge - so a month in which the currency falls
         * under a full vault must still reconcile to the cent, with the move
         * nowhere in it.
         */
        Path dir = Files.createTempDirectory("foreigncheck-vault");
        Game city = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        double bought, dollarsBefore, rateBefore, rateAfter;
        System.setOut(quiet);
        try {
            city.run();
            city.buildStack(template(city, "House"), 40, false);
            city.buildStack(template(city, "Convenience Store"), 3, false);
            city.simulateMonths(3);
            bought = city.buyForeignCurrency(50_000);
            dollarsBefore = city.getForeignAccounts().getReservesUsd();
            rateBefore = city.getForeignAccounts().getRate();
            city.getForeignAccounts().pinRate(rateBefore * 1.6);
            rateAfter = city.getForeignAccounts().getRate();
            city.simulateMonths(1);
        } finally {
            System.setOut(out);
        }
        ForeignAccounts cfx = city.getForeignAccounts();
        assertTrue("fixture: the treasury bought dollars and the currency fell",
                bought > 0 && rateAfter > rateBefore);
        close("a month the currency falls in leaves the dollars alone",
                cfx.getReservesUsd(), dollarsBefore, 1e-9);
        close("...and books dollars times the move as its revaluation",
                cfx.getLastVaultRevaluation(), dollarsBefore * (rateAfter - rateBefore), 1e-6);
        assertTrue("...and none of it appeared in the audit as money moving",
                Math.abs(city.getLastMoneyAudit().relative()) < 1e-9);

        /* ============ 11. a reform does not reach the dollars ============ */
        out.println("\n--- and a currency reform leaves the dollars alone ---");

        /*
         * Lopping two zeros off the local dollar divides the rate by a
         * hundred, and every local figure with it. The vault's local value is
         * its dollars at the rate, so it follows by itself; the dollars are
         * somebody else's money and no domestic reform can reach them. Scaling
         * them too would divide the vault twice.
         */
        ForeignAccounts reformed = new ForeignAccounts();
        reformed.pinRate(80);
        reformed.revalueVault();
        for (int m = 0; m < 24; m++) {
            reformed.takeMonth(month(usdImports * 80, usdImports * 80), 5_000_000);
        }
        reformed.buyReserves(usd * 80);
        double localBefore = reformed.getReserves();
        double coverPre = reformed.importCover();
        reformed.redenominate(.01);
        close("lopping two zeros leaves the vault's dollars alone",
                reformed.getReservesUsd(), usd, 1e-9);
        close("...and divides their worth at home by the same hundred",
                reformed.getReserves(), localBefore * .01, 1e-6);
        close("...so the cover does not move", reformed.importCover(), coverPre, 1e-9);
        reformed.revalueVault();
        close("...and the next valuation finds no move to book",
                reformed.getLastVaultRevaluation(), 0, 1e-9);

        /* ============ 12. an older save ============ */
        out.println("\n--- and an older save's vault comes back at the rate it was saved at ---");

        /*
         * Slot 19 keeps its meaning - the vault's local value at the moment of
         * saving - so an older build reading a new save sees what it always
         * saw. The dollars ride the end, slot 22. A 0.6.9 save (slots 0 to 21)
         * has no dollars, and its vault comes back from slot 19 at the rate in
         * slot 3: the only rate it has, and the one those dollars were worth
         * that day.
         */
        ForeignAccounts saver = new ForeignAccounts();
        saver.pinRate(1.6);
        saver.revalueVault();
        saver.buyReserves(usd * 1.6);
        saver.pinRate(2.4);
        saver.revalueVault();
        double[] now = saver.toSaveArray();
        close("slot 19 holds the local value, as an older build reads it",
                now[19], saver.getReserves(), 1e-9);
        close("...and slot 22 its dollars", now[22], usd, 1e-9);

        ForeignAccounts fromNew = new ForeignAccounts();
        fromNew.restore(now);
        close("a save of this shape reloads the dollars exactly",
                fromNew.getReservesUsd(), usd, 1e-12);
        close("...and what the currency did to them that month",
                fromNew.getLastVaultRevaluation(), saver.getLastVaultRevaluation(), 1e-9);
        fromNew.revalueVault();
        close("...and the first valuation after it books no phantom move",
                fromNew.getLastVaultRevaluation(), 0, 1e-9);

        double[] older = java.util.Arrays.copyOf(now, 22);
        ForeignAccounts fromOld = new ForeignAccounts();
        fromOld.restore(older);
        close("a 0.6.9 save's vault comes back at its saved rate",
                fromOld.getReservesUsd(), older[19] / older[3], 1e-12);
        close("...which is the same dollars it held that day", fromOld.getReservesUsd(), usd, 1e-9);
        close("...worth what the old build said they were worth",
                fromOld.getReserves(), older[19], 1e-9);

        /*
         * AND NOTHING THE CITY HELD BEFORE THE LOAD. The load path runs
         * buildWorld() first, which founds a new city's vault; a save that
         * never had one must not come back with the founders' dollars in it.
         */
        ForeignAccounts fromAncient = new ForeignAccounts();
        fromAncient.buyReserves(250_000);
        fromAncient.restore(java.util.Arrays.copyOf(now, 19));
        close("a save older than the vault's slot loads it empty",
                fromAncient.getReservesUsd(), 0, 0);
        ForeignAccounts fromNothing = new ForeignAccounts();
        fromNothing.buyReserves(250_000);
        fromNothing.restore(null);
        close("...and so does a save with no foreign accounts at all",
                fromNothing.getReservesUsd(), 0, 0);
        close("...with no purchase on its record either",
                fromNothing.getLifetimeIntervention(), 0, 0);
    }

    /**
     * Section 13: the vault damps a fall and nothing else (2026-09-21).
     *
     * Jerus: "a reserve defends a currency; it does not hold one down." Damped
     * both ways, a deep vault muted the surplus and the policy rate - the two
     * forces that pull a currency back out of an inflation spiral - and the
     * founding reserve reproduced the year book in three seeds of eight. So:
     * a deep vault and a month that would weaken the currency, damped by the
     * cover's absorption; the same vault and a month that would strengthen
     * it, passed through in full; and the rate's support inside a weakening
     * month, which shortens the push before the vault damps what is left.
     * Last, the trade page's preview: previewPressure() leaves the month's
     * pressure and absorption as recorded, and previews exactly what
     * effectivePressure() then applies.
     *
     * Since 0.7.2 the damping is what the central bank SELLS, as a share of
     * the month's own deficit (ForeignAccounts, A DEFENCE THAT SPENDS). This
     * fixture's months are in deficit and its vault is twelve months deep, so
     * every sale is met in full and the realised absorption is the capacity -
     * which is why these assertions hold unchanged. What a vault running
     * short does is CurrencyCheck's section 3.
     */
    static void reserveDefends() {

        /* ============ 13. the vault defends, it does not hold down ============ */
        out.println("\n--- and the vault defends the currency, it does not hold it down ---");

        final double trade = 6_000;          // a month's goods, both ways together
        final double gdp = 40_000;           // so openness is a real fraction, not 1

        ForeignAccounts deficit = new ForeignAccounts();
        ForeignAccounts surplus = new ForeignAccounts();
        for (int m = 0; m < ForeignAccounts.SETTLING_MONTHS + 12; m++) {
            deficit.takeMonth(month(trade / 3, trade * 2 / 3), gdp);
            surplus.takeMonth(month(trade * 2 / 3, trade / 3), gdp);
        }
        // A deep vault in both: twice the cover that earns the most damping.
        deficit.buyReserves(deficit.monthlyImports() * ForeignAccounts.COMFORTABLE_COVER * 2);
        surplus.buyReserves(surplus.monthlyImports() * ForeignAccounts.COMFORTABLE_COVER * 2);

        assertTrue("fixture: the deficit city is pushed weaker", deficit.pressure() > 0);
        assertTrue("fixture: the surplus city is pushed stronger", surplus.pressure() < 0);
        assertTrue("fixture: both trade a fraction of their output",
                deficit.getOpenness() > 0 && deficit.getOpenness() < 1);
        close("fixture: both vaults are deep enough for the most damping",
                Math.min(deficit.absorption(), surplus.absorption()),
                ForeignAccounts.MAX_ABSORPTION, 1e-12);

        double down = deficit.effectivePressure();
        out.printf("   weaker: pressure %+.4f, absorbed %.0f%%, reaches the rate %+.4f%n",
                deficit.getLastPressure(), deficit.getLastAbsorption() * 100, down);
        close("a push weaker is damped by the vault's cover",
                down, deficit.pressure() * (1 - ForeignAccounts.MAX_ABSORPTION)
                        * deficit.getOpenness(), 1e-12);
        close("...and the absorption it records is what was applied",
                deficit.getLastAbsorption(), ForeignAccounts.MAX_ABSORPTION, 1e-12);

        double up = surplus.effectivePressure();
        out.printf("   stronger: pressure %+.4f, absorbed %.0f%%, reaches the rate %+.4f%n",
                surplus.getLastPressure(), surplus.getLastAbsorption() * 100, up);
        close("the same vault passes a push stronger in full",
                up, surplus.pressure() * surplus.getOpenness(), 1e-12);
        close("...and records that it absorbed nothing", surplus.getLastAbsorption(), 0, 0);
        close("...though its cover could have absorbed the most there is",
                surplus.absorption(), ForeignAccounts.MAX_ABSORPTION, 1e-12);

        /*
         * THE RATE'S SUPPORT INSIDE A WEAKENING MONTH. A real rate half the
         * trade term's worth over the world's (the real differential since
         * 0.7.2): the total is still a push weaker, so the vault damps it -
         * but only what is left after the rate has done its part.
         */
        double tradeTerm = deficit.pressure();
        deficit.setRealRateDifferential(tradeTerm / 2 / ForeignAccounts.RATE_PULL);
        assertTrue("fixture: the support is half the trade term, unclipped",
                Math.abs(deficit.ratePressure() + tradeTerm / 2) < 1e-12);
        double supported = deficit.effectivePressure();
        close("the rate's support comes off before the vault sees it",
                deficit.getLastPressure(), tradeTerm / 2, 1e-12);
        close("...and the vault damps what is left",
                supported, tradeTerm / 2 * (1 - ForeignAccounts.MAX_ABSORPTION)
                        * deficit.getOpenness(), 1e-12);

        /* ...and support that outweighs the trade term is a push up: in full. */
        deficit.setRealRateDifferential(tradeTerm * 2 / ForeignAccounts.RATE_PULL);
        double overtaken = deficit.effectivePressure();
        assertTrue("fixture: support larger than the trade term turns the push",
                deficit.getLastPressure() < 0);
        close("support outweighing the deficit reaches the rate in full",
                overtaken, deficit.getLastPressure() * deficit.getOpenness(), 1e-12);

        /*
         * AND A SCREEN LOOKS WITHOUT RECORDING (2026-09-21). The trade page
         * printed the push by calling effectivePressure(), which writes the
         * month's two readings - so opening it after anything had moved the
         * inputs rewrote the month's record. It calls previewPressure() now.
         * CAUSED here: the month above recorded a push up with nothing
         * absorbed; the dial then comes off, so the push the page would print
         * is a push down that the vault damps - a different pressure AND a
         * different absorption from the ones recorded.
         */
        double recordedPush = deficit.getLastPressure();
        double recordedAbsorbed = deficit.getLastAbsorption();
        deficit.setRealRateDifferential(0);
        double previewed = deficit.previewPressure();
        assertTrue("fixture: the page's push is not the one the month recorded",
                Math.abs(deficit.pressure() + deficit.ratePressure() - recordedPush) > 1e-9
                        && deficit.pressure() + deficit.ratePressure() > 0);
        close("previewing the push leaves the month's pressure as recorded",
                deficit.getLastPressure(), recordedPush, 0);
        close("...and its absorption", deficit.getLastAbsorption(), recordedAbsorbed, 0);
        close("...and previews exactly what the month will apply",
                previewed, deficit.effectivePressure(), 0);
        close("...which is the month's own call, and records what it applied",
                deficit.getLastAbsorption(), ForeignAccounts.MAX_ABSORPTION, 1e-12);
    }

    /* ============ 14. land bought by conversion pushes as reserves would ============
     *
     * The brief (0.7.6): "whatever pressure the exchange puts on the rate when
     * the treasury buys dollars applies here too". Read first, and it is none:
     * a treasury's purchase of dollars is the financing item (Scope.RESERVE),
     * so neither pressure() nor monthDeficitUsd() reads it - boughtThisMonth
     * and lifetimeIntervention feed the audit's reserve line and the record,
     * not the push. The dollars bought for land are bought the same way and
     * spent at once, so the push is the reserve purchase's exactly, and the
     * only thing the reserve purchase does that this does not is leave the
     * dollars where absorption() can see them. CAUSED: three accounts with
     * one history on a deficit, so there is a push for anything to add to.
     */
    static void landByConversion() {
        out.println("\n--- and land bought by converting pushes the rate as reserves bought would ---");

        final double trade = 6_000, gdp = 40_000;
        ForeignAccounts control = new ForeignAccounts();
        ForeignAccounts reserves = new ForeignAccounts();
        ForeignAccounts land = new ForeignAccounts();
        for (ForeignAccounts fx : new ForeignAccounts[] { control, reserves, land }) {
            for (int m = 0; m < ForeignAccounts.SETTLING_MONTHS + 12; m++) {
                fx.takeMonth(month(trade / 3, trade * 2 / 3), gdp);
            }
            fx.buyReserves(fx.monthlyImports() * 2);   // a thin vault, so cover matters
            fx.pinRate(1.6);                           // any rate but the founding one
            fx.startMonth();
        }
        assertTrue("fixture: the city is pushed weaker", control.pressure() > 0);

        double usd = 1_500;
        double vaultBefore = control.getReservesUsd();
        double interventionBefore = control.getLifetimeIntervention();
        reserves.buyReserves(usd * reserves.getRate());
        double paid = land.buyAndSpendDollarsForLand(usd);

        close("converting pays the parcel's dollars at today's rate", paid, usd * 1.6, 1e-9);
        close("converting for land pushes the rate as buying the dollars for the vault does",
                land.previewRawPressure(), reserves.previewRawPressure(), 0);
        close("...which is what buying nothing pushes: a treasury's dollars are the financing item",
                land.previewRawPressure(), control.previewRawPressure(), 0);
        close("...and it leaves the vault where it began", land.getReservesUsd(), vaultBefore, 0);
        close("...where the reserve purchase leaves it the parcel's dollars fuller",
                reserves.getReservesUsd() - vaultBefore, usd, 1e-9);
        close("...and nets to nothing in the intervention record",
                land.getLifetimeIntervention(), interventionBefore, 0);
        close("...and books no reserve purchase for the month",
                land.getBoughtThisMonth(), 0, 0);
        close("...while the seller was paid the parcel's dollars",
                land.getLandUsdPending(), usd, 0);
        out.printf("   push: control %+.5f, reserves %+.5f, land %+.5f; absorbed %.1f%% / %.1f%% / %.1f%%%n",
                control.previewRawPressure(), reserves.previewRawPressure(), land.previewRawPressure(),
                control.previewAbsorption() * 100, reserves.previewAbsorption() * 100,
                land.previewAbsorption() * 100);
        close("...so what the vault would absorb is the control's, not the reserve buyer's",
                land.previewAbsorption(), control.previewAbsorption(), 0);

        /* From the vault: the dollars leave it, at the rate of the day, and nothing else. */
        double spent = land.spendReservesOnLand(usd);
        close("paid from the vault, the dollars leave it", land.getReservesUsd(), vaultBefore - usd, 1e-9);
        close("...and the record falls by their local price, as a sale's would",
                land.getLifetimeIntervention(), interventionBefore - usd * 1.6, 1e-9);
        close("...and a vault asked for more than it holds pays what it holds",
                land.spendReservesOnLand(1e12), vaultBefore - usd, 1e-9);
        close("...and is empty", land.getReservesUsd(), 0, 0);
        land.strikeLandMonth();
        close("the land's month, struck: every dollar paid for it",
                land.getLandUsdThisMonth(), usd + spent + (vaultBefore - spent), 1e-9);
        close("...of which out of the vault", land.getLandUsdFromVaultThisMonth(), vaultBefore, 1e-9);

        ForeignAccounts back = new ForeignAccounts();
        back.restore(land.toSaveArray());
        close("...and the struck month and the lifetime survive a save",
                back.getLandUsdThisMonth() + back.getLandUsdFromVaultThisMonth()
                        + back.getLandUsdLifetime() + back.getLandUsdFromVaultLifetime(),
                land.getLandUsdThisMonth() + land.getLandUsdFromVaultThisMonth()
                        + land.getLandUsdLifetime() + land.getLandUsdFromVaultLifetime(), 1e-9);
        land.redenominate(.01);
        close("...and a reform does not reach them: they are dollars",
                land.getLandUsdLifetime(), back.getLandUsdLifetime(), 0);
    }

    /** A month whose only foreign flow is the treasury working its own vault. */
    static MoneyAudit.Result intervention(double soldIn, double boughtOut) {
        double[] f = new double[10];
        f[8] = soldIn;
        f[9] = boughtOut;
        return new MoneyAudit.Result(0, 0, 0, soldIn, boughtOut, 0, "", f);
    }

    /**
     * A synthetic month, which is the only honest way to test the rate rule.
     *
     * Only the four trade and income fields matter to takeMonth; the rest of a
     * Result describes a city this fixture does not have.
     */
    static MoneyAudit.Result month(double exports, double imports) {
        double[] f = new double[8];
        f[0] = exports;
        f[1] = imports;
        return new MoneyAudit.Result(0, 0, 0, exports, imports, 0, "", f);
    }

    /**
     * The same city twice, differing only in what its currency is worth.
     *
     * @param food filled with the run's food trade in local money: [exports, imports]
     */
    static Game devaluationCity(Path dir, double rate, double[] food) throws Exception {
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        g.run();
        /*
         * THE FOUNDERS' DOLLARS BACK INTO THE TREASURY, at the opening rate
         * and before the pin (2026-09-21). A new city opens with D$2.5B and
         * US$1B in the vault now, where this fixture was written against
         * $3.5B of cash - and the fixed programme below is bought out of the
         * treasury. At 1.40 the weaker city could no longer pay for it: it
         * built two-thirds of the programme, Construction imported $591M
         * against the parity city's $896M, and the premise read 0.63. Nothing
         * about the currency had changed; the ruler had lost its endowment.
         * Sold here, both cities open exactly as they did before the split -
         * $3.5B in the treasury, an empty vault - and the question is the
         * rate again and nothing else.
         */
        g.sellForeignCurrency(g.getForeignAccounts().sellableReserves());
        g.getForeignAccounts().pinRate(rate);
        g.getEconomyManager().setExchangeRate(rate);
        g.getLandManager().setOwnedSqFt(30_000_000);
        /*
         * THE NINTH SECTOR SITS THIS ONE OUT (2026-09-13), because the whole
         * instrument below rests on the import side being a FIXED PROGRAMME -
         * a power plant, a water plant and forty roads, about $900M of
         * material that gets bought whatever it costs. That is what makes
         * "the same programme costs the same in the world's money" a premise
         * rather than a finding, and it is what lets the exports carry the
         * elasticity.
         *
         * Manufacturing buys steel from the world, in proportion to how big
         * its city got, and the two cities here deliberately grow at different
         * speeds. Left in, the import side stopped being a programme: the
         * weaker city bought 30% fewer dollars of imports than the parity one
         * and the premise read 0.69 against 1.00. Nothing was wrong with
         * either city; the ruler had grown a hinge. What a devaluation does to
         * a steel-importing exporter is a real question and it belongs in the
         * harness that owns that sector.
         *
         * AND THE TENTH SITS IT OUT TOO, the next morning, for exactly the same
         * reason one sentence further along the chain: the fields sell crops,
         * the mills buy them, and how much of either happens depends on how big
         * each city got. Measured with the fields in, the premise read 1.068
         * against a five percent band. Held out, the mills buy every tonne from
         * the world - which is a fixed programme per mill, and the two cities
         * run the same mills - and the ruler is straight again.
         */
        g.getBusinessInvestment().holdSector(Sectors.MANUFACTURING);
        g.getBusinessInvestment().holdSector(Sectors.AGRICULTURE);
        /*
         * AND THE LANDLORD SITS IT OUT, 2026-09-15, the third of these and the
         * one that was always the largest hole in the premise.
         *
         * Construction imports the material for what the city builds, and most
         * of what a city builds is HOUSES - which the landlord puts up against
         * the household count, not against the fixture's order. So the "fixed
         * programme" was never fixed: it was the fixture's plant and roads plus
         * however much private housing each city happened to want, and the two
         * cities deliberately grow at different speeds.
         *
         * It went unnoticed while the two counts happened to track each other.
         * The senior band splitting on 2026-09-15 pulled them apart - the
         * over-85s live alone far more than the under-85s, so the same people
         * are more households, so more doors - and the premise walked out in
         * two steps: 1.0063 on the shipped build, 1.0275 with the band split,
         * 1.0558 once the elders drew their pension and could keep paying for
         * a door. Measured at the third: the weaker city held 2,765 homes for
         * 2,765 households against the parity city's 2,068 for 2,436, having
         * built SEVEN HUNDRED more houses on two hundred fewer people, and
         * Construction imported $924,634k against $867,630k. The gap was the
         * whole discrepancy; no other sector moved.
         *
         * Held out, both cities keep the five hundred houses the fixture built
         * them, Construction imports the programme and nothing else, and the
         * premise reads 0.9805. The instrument is also SHARPER for it, which is
         * how you can tell it was the right hole to close: the elasticity had
         * been hiding inside the building, and with the building held still the
         * weaker city exports $46,791k of food against $14,418k and imports
         * $3,194k against $29,662k - a devaluation selling more and buying less,
         * stated plainly, where the same two cities used to differ by three per
         * cent on the export side and read as noise.
         *
         * The per-sector import line below is printed rather than asserted, so
         * that whoever next finds this premise drifting can see in one run which
         * sector grew the hinge instead of bisecting for it.
         */
        g.getBusinessInvestment().holdSector(Sectors.REAL_ESTATE);
        /* =================================================================
           ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN
           REASON (2026-09-18)

           The premise of this whole comparison is that the import side is a
           FIXED PROGRAMME - a power plant, a water plant and forty roads that
           get bought whatever they cost - so that what a devaluation changes
           is the rate and nothing else. Real Estate walked out of that premise
           by building houses, and was held; these two walked out of it by
           importing watches.

           AND THE HINGE IS EXACTLY WHERE THE NOTE ABOVE SAID TO LOOK. The
           per-sector import line is printed rather than asserted so that the
           next person to find this drifting can see which sector grew it. It
           read Luxury Retail $138,850k at parity against $65,881k weaker - a
           $73M swing on a $1,149M programme, which is the whole of the 7.4%
           the premise was out by. A boutique buys fewer watches when watches
           get dearer, which is correct behaviour and is not a public works
           programme.

           Restaurants are held with them. They import almost nothing - $80k
           against $82k, and a kitchen buys its food at home - but the rule is
           about what the fixture is FOR, not about how big a sector's number
           happened to be this year.
           ================================================================= */
        g.getBusinessInvestment().holdSector(Sectors.LUXURY_RETAIL);
        g.getBusinessInvestment().holdSector(Sectors.RESTAURANTS);
        g.buildStack(template(g, "House"), 500, false);
        g.buildStack(template(g, "Convenience Store"), 8, false);
        g.buildStack(template(g, "Small Grocery Store"), 3, false);
        g.buildStack(template(g, "Construction Depot"), 4, false);
        g.buildStack(template(g, "Coal Power Plant"), 1, false);
        g.buildStack(template(g, "Water Treatment Plant"), 1, false);
        g.buildStack(template(g, "Industrial Bakery"), 2, false);
        g.buildStack(template(g, "Paved Road"), 40, false);
        g.buildStack(template(g, "Walk-in Clinic"), 4, false);
        g.buildStack(template(g, "Municipal Cemetery"), 1, false);
        double[] bySector = new double[Sectors.KEYS.length];
        scrappedPlantBuiltWith = 0;
        for (int m = 0; m < 180; m++) {
            g.simulateMonths(1);
            /*
             * THIRTEEN LINES WHERE THERE WAS ONE. This used to read Good.FOOD,
             * and FOOD stopped being traded on 2026-09-15 - so the fixture
             * measured a market nobody was in and reported that the city sold
             * no food abroad, which was true of FOOD and false of the city.
             * What it exports now is the bakeries' surplus; what it imports is
             * the other eleven.
             */
            for (Good fg : ham.citybuildersim.sectors.Retail.SHELF) {
                GoodsMarket f = g.getMarkets().get(fg);
                food[0] += f.getExported() * f.exportPrice();
                food[1] += f.getImported() * f.importPrice();
            }
            for (int i = 0; i < Sectors.KEYS.length; i++) {
                bySector[i] += g.getSectors().byKey(Sectors.KEYS[i]).statement().imports;
            }
            // Material the crews took from scrapped plant rather than import:
            // valued at what it would have cost to land, in dollars.
            scrappedPlantBuiltWith += g.getSalvageUsedThisMonth()
                    * g.getMarkets().get(Good.MATERIALS).importPrice() / rate;
        }
        StringBuilder sb = new StringBuilder("   imports by sector, in dollars: ");
        for (int i = 0; i < Sectors.KEYS.length; i++) {
            if (bySector[i] > 1) sb.append(String.format("%s $%,.0fk  ", Sectors.KEYS[i], bySector[i] / rate));
        }
        out.printf("   at %.2f: pop %,d, %,d homes for %,.0f households%n", rate,
                g.getPopulationManager().getPopulation(),
                g.getBuildingManager().getTotalHomes(), g.getFamilies().totalHouseholds());
        out.println(sb);
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
        /*
         * What a person-month of food costs to land, at this rate: the shelf
         * priced at the reference household's quantities. One good's price
         * would have done the same job while FOOD existed; thirteen goods need
         * a basket, and the reference one is the only basket that does not
         * depend on the city that is being measured.
         */
        double bill = 0;
        for (Good fg : ham.citybuildersim.sectors.Retail.SHELF) {
            Consumption.Item it = g.getConsumption().byKey(fg.name());
            double p = g.getMarkets().get(fg).importPrice();
            if (it != null && p > 0) bill += it.referenceKg() * p;
        }
        return bill;
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
