package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Borrowing in somebody else's money.
 *
 * ORIGINAL SIN, which is the name the literature gives the thing this file is
 * here to prove works: a city that cannot borrow abroad in its own currency owes
 * dollars and earns local money, so a devaluation makes the debt dearer without
 * anybody having borrowed another cent. Domestic debt does the opposite -
 * inflation and devaluation quietly shrink it.
 *
 * WHAT IS ACTUALLY BEING ASKED
 *
 *   1. Does the instrument speak two currencies HONESTLY? The contract is in
 *      dollars and does not move; the city's books are in local money and must.
 *      Both, at once, with neither leaking into the other.
 *
 *   2. Do the books still balance? Foreign paper is the first city borrowing
 *      that genuinely crosses the city's edge, so MoneyAudit has three new lines
 *      and one deliberate absence - the revaluation, which is not a cash flow
 *      and must not appear as one.
 *
 *   3. Is the circular-capital hole actually closed? The whole point of foreign
 *      paper is that it does not reach the bank's book. If it does, the city is
 *      still borrowing from the institution it is recapitalising and none of
 *      this was worth building.
 *
 *   4. Does the window shut when it should, does a default cost what it is
 *      supposed to cost, and does any of it survive a reload?
 */
public class ForeignDebtCheck {

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

    /** A small city that has been going long enough to have a credit record. */
    static Game tradingCity(Path dir) throws Exception {
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        g.run();
        g.getLandManager().setOwnedSqFt(30_000_000);
        g.buildStack(template(g, "House"), 400, false);
        g.buildStack(template(g, "Convenience Store"), 6, false);
        g.buildStack(template(g, "Construction Depot"), 4, false);
        g.buildStack(template(g, "Coal Power Plant"), 1, false);
        g.buildStack(template(g, "Water Treatment Plant"), 1, false);
        g.buildStack(template(g, "Textile Mill"), 2, false);
        g.buildStack(template(g, "Paved Road"), 30, false);
        g.simulateMonths(60);
        return g;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ============ 1. the instrument speaks two currencies ============ */
        out.println("--- a bond written in dollars ---");

        DebtManager books = new DebtManager();
        books.addLongTermBond(10_000, 120, 0, .05, true);    // USD
        books.addLongTermBond(10_000, 120, 0, .05, false);   // local

        close("at parity the two are worth the same",
                books.getForeignPrincipal(), books.getDomesticPrincipal(), 1e-9);

        books.setExchangeRate(1.40);

        close("the dollars owed do not move", books.getForeignPrincipalUsd(), 10_000, 1e-9);
        close("...but what they cost at home does",
                books.getForeignPrincipal(), 14_000, 1e-6);
        close("...and the local bond has not noticed a thing",
                books.getDomesticPrincipal(), 10_000, 1e-9);

        /*
         * AND THE COUPON WITH IT, which is the half that is easy to get wrong.
         *
         * A devaluation raises the monthly payment by exactly as much as it
         * raises the principal, because the coupon is a fraction of a face that
         * is written in dollars. An instrument that revalued its principal and
         * not its coupon would look dangerous on the debt screen and be
         * painless to service, which is precisely backwards.
         */
        double fxCoupon = 0, localCoupon = 0;
        for (Debt d : books.getDebt()) {
            if (d.isForeign()) fxCoupon += d.getMonthlyInterestExpense();
            else               localCoupon += d.getMonthlyInterestExpense();
        }
        close("the coupon revalues too", fxCoupon / localCoupon, 1.40, 1e-9);

        /* AND THE WHOLE SCHEDULE, because market value is priced off it. */
        double fxFlows = 0, localFlows = 0;
        for (Debt d : books.getDebt()) {
            for (double cf : d.remainingCashFlows()) {
                if (d.isForeign()) fxFlows += cf; else localFlows += cf;
            }
        }
        close("...and every payment still to come", fxFlows / localFlows, 1.40, 1e-9);

        /* A rate of zero would value the city's whole foreign debt at nothing. */
        books.setExchangeRate(0);
        close("a nonsense rate is refused rather than applied",
                books.getForeignPrincipal(), 14_000, 1e-6);

        /* ============ 2. and the world is cheaper, to begin with ============ */
        out.println("\n--- and the world quotes it a better price ---");

        Path root = Files.createTempDirectory("fxdebt");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));

        System.setOut(quiet);
        try {
            city.run();
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(template(city, "House"), 400, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(template(city, "Textile Mill"), 2, false);
            city.buildStack(template(city, "Paved Road"), 30, false);

            /*
             * AND THEN ENOUGH ROAD TO PUT IT IN DEBT, WHICH IS THE POINT.
             *
             * The comparison below is between the city's OWN cost of money and
             * the world's, and a city with no debt is priced at the floor - it
             * came out at 1.00% against the world's 2.00% and the assertion
             * inverted. That is not the model being wrong; a debt-free treasury
             * really is the best credit in the room.
             *
             * The endowment went from $500M to $3.5B the night rebalance stage
             * two put every building on its real capital cost, and this build
             * list stopped being able to spend it. So the order is SIZED FROM
             * THE CITY'S CASH rather than written down - the same fix
             * CreditCheck's road city needed on the same night, for the same
             * reason - and the city borrows because it has been made to.
             */
            city.simulateMonths(60);

            /*
             * ...AND THEN PUT IT IN DEBT, LAST, BECAUSE THE RATE IS READ NEXT.
             *
             * Ordered BEFORE the sixty months it simply paid the loan off again
             * and arrived at the read with a clean balance sheet, which is the
             * same 1.00% floor by a longer road.
             */
            /*
             * Issued directly rather than provoked through a build. Ordering
             * more road than the treasury holds was the first attempt and it
             * never moved the rate - the city pays for a queue as it builds it,
             * so the debt never existed to be priced. What this section needs is
             * simply A CITY WITH DEBT ON ITS BOOKS at the moment the rate is
             * read, and the debt market is the honest way to give it one.
             */
            city.issueEmergencyDebt(city.getCash() * 2, Game.EMERGENCY_NOTE_MONTHS);
            city.simulateMonths(1);
        } finally {
            System.setOut(out);
        }

        DebtManager market = city.getDebtManager();
        out.printf("   at home %.2f%%, abroad %.2f%% (premium %.2f%%)%n",
                market.getRate() * 100, market.foreignRate() * 100,
                market.countryPremium() * 100);

        assertTrue("a city with no foreign debt owes no risk premium",
                market.countryPremium() < 1e-9);
        assertTrue("...so the world's money starts cheaper than the city's own",
                market.foreignRate() < market.getRate());
        assertTrue("...and the window is open", market.foreignWindowOpen());

        /* ============ 3. the books balance with dollars on them ============ */
        out.println("\n--- and the books still balance ---");

        double bankBookBefore = city.getBank().getBook();

        System.setOut(quiet);
        String booked;
        try {
            booked = city.handleForeignLogic("Term", 20_000, 25, 100, false);
        } finally {
            System.setOut(out);
        }

        assertTrue("the bond was actually issued", booked.startsWith("Issued abroad"));
        assertTrue("...and the city now owes dollars", market.getForeignPrincipalUsd() > 0);

        /*
         * THE CIRCULAR-CAPITAL HOLE, and whether it is shut.
         *
         * The city's own bonds are bought by its own bank: cityDebtRaised goes
         * to bank.lend(), so every dollar the treasury borrows at home enlarges
         * the book of the institution it may shortly have to recapitalise. That
         * is the hole recorded in claude/bank-as-a-bank.md and the reason this
         * whole phase exists.
         *
         * If a foreign bond enlarges the bank's book, nothing has been fixed.
         */
        close("the bank did not buy a cent of it",
                city.getBank().getBook(), bankBookBefore, .005);

        double worst = 0;
        int worstMonth = 0;
        System.setOut(quiet);
        try {
            for (int m = 0; m < 120; m++) {
                city.simulateMonths(1);
                MoneyAudit.Result r = city.getLastMoneyAudit();
                if (Math.abs(r.relative()) > Math.abs(worst)) {
                    worst = r.relative();
                    worstMonth = m;
                }
            }
        } finally {
            System.setOut(out);
        }

        ForeignAccounts fx = city.getForeignAccounts();
        out.printf("   ten years of servicing it: worst residual %.2e (month %d)%n",
                worst, worstMonth);
        assertTrue("every month of servicing a dollar bond reconciles",
                Math.abs(worst) < 1e-9);
        assertTrue("...and the fixture really did service it",
                fx.getLifetimeInterest() > 0);

        /* ============ 4. original sin ============ */
        out.println("\n--- and a devaluation makes it dearer, with nobody paid ---");

        double usdBefore    = market.getForeignPrincipalUsd();
        double localBefore  = market.getForeignPrincipal();
        double premiumBefore = market.countryPremium();
        double rateBefore   = fx.getRate();

        assertTrue("the fixture has a debt worth revaluing", localBefore > 1);

        market.setExchangeRate(rateBefore * 1.5);
        fx.takeForeignDebt(market.getForeignPrincipalUsd(), rateBefore * 1.5);

        out.printf("   a 50%% devaluation: US$%,.0f owed, $%,.0f -> $%,.0f at home%n",
                usdBefore, localBefore, market.getForeignPrincipal());

        close("not one dollar more is owed", market.getForeignPrincipalUsd(), usdBefore, 1e-9);
        close("...and half again as much at home",
                market.getForeignPrincipal() / localBefore, 1.5, 1e-6);
        close("...which is exactly what the revaluation says it is",
                fx.getLastRevaluation(), market.getForeignPrincipal() - localBefore, .01);

        /*
         * AND THE LOOP CLOSES.
         *
         * The premium is measured on debt-to-exports in LOCAL money, so the
         * devaluation that raised the burden also raises what the next bond
         * costs, without a cent being borrowed. Devalue, the burden rises,
         * solvency looks worse, the currency falls further. Mexico 1994, Asia
         * 1997, Argentina 2001.
         *
         * MEASURED ON A SYNTHETIC BOOK rather than on the city above, and the
         * reason is the whole of why fixtures get checked for what else they
         * are measuring: that city sells about a dollar a month abroad, so ANY
         * foreign debt pins its premium at the 16-point cap and a devaluation
         * cannot move a number that is already against its ceiling. The
         * assertion passed as written on the first attempt only because the
         * cap held it - and would have gone on passing if the loop had never
         * been wired at all.
         */
        DebtManager loop = new DebtManager();
        loop.setTrade(5_000, 6);                     // $60,000 a year of exports
        loop.addLongTermBond(120_000, 300, 0, .05, true);
        double premiumAtParity = loop.countryPremium();
        loop.setExchangeRate(1.5);
        out.printf("   the premium on a city that still sells things: %.2f%% -> %.2f%%%n",
                premiumAtParity * 100, loop.countryPremium() * 100);
        assertTrue("the fixture is not already against its cap",
                premiumAtParity < DebtManager.MAX_COUNTRY_PREMIUM - 1e-9);
        assertTrue("a weaker currency makes the NEXT bond dearer too",
                loop.countryPremium() > premiumAtParity);
        out.printf("   (and the city above was already capped at %.2f%%)%n",
                premiumBefore * 100);

        /* ...and the revaluation is not a cash flow. */
        System.setOut(quiet);
        try {
            city.simulateMonths(1);
        } finally {
            System.setOut(out);
        }
        assertTrue("...and none of it appeared in the audit as money moving",
                Math.abs(city.getLastMoneyAudit().relative()) < 1e-9);

        /* ============ 4b. and where the dollars actually went ============ */
        out.println("\n--- and the two things a player can do with the proceeds ---");

        /*
         * THE QUESTION NOBODY HAD ASKED THE CODE.
         *
         * Jerus, having borrowed abroad and chosen to park the proceeds: "when
         * player clicks hold... where does it go?" Nothing in this file could
         * answer him, because nothing asserted it - the toggle was built, the
         * books balanced either way, and no test distinguished the two paths at
         * all. A branch with no assertion on it is a branch that works by
         * coincidence.
         *
         * Two cities, identical but for the toggle, measured the instant the
         * bond is signed.
         */
        Path uses = Files.createTempDirectory("fxdebt-uses");
        double spendCash, spendReserves, holdCash, holdReserves, holdNet, proceeds;

        System.setOut(quiet);
        try {
            Game spender = tradingCity(uses.resolve("spend"));
            double cashBefore = spender.getCash();
            double resBefore = spender.getForeignAccounts().getReserves();
            DebtQuote q = spender.quoteForeign("Term", 20_000, 25, 100);
            proceeds = q.cashReceived() * spender.getForeignAccounts().getRate();
            spender.handleForeignLogic("Term", 20_000, 25, 100, false);
            spendCash = spender.getCash() - cashBefore;
            spendReserves = spender.getForeignAccounts().getReserves() - resBefore;

            Game holder = tradingCity(uses.resolve("hold"));
            cashBefore = holder.getCash();
            resBefore = holder.getForeignAccounts().getReserves();
            holder.handleForeignLogic("Term", 20_000, 25, 100, true);
            holdCash = holder.getCash() - cashBefore;
            holdReserves = holder.getForeignAccounts().getReserves() - resBefore;
            holdNet = holder.getForeignAccounts().netForeignPosition();
        } finally {
            System.setOut(out);
        }

        out.printf("   proceeds $%,.0f  |  convert: cash %+,.0f reserves %+,.0f%n",
                proceeds, spendCash, spendReserves);
        out.printf("                        |  hold:    cash %+,.0f reserves %+,.0f%n",
                holdCash, holdReserves);

        close("converting puts the whole proceeds in the treasury", spendCash, proceeds, .5);
        close("...and leaves the reserve position alone", spendReserves, 0, .5);

        close("holding puts the whole proceeds in reserves", holdReserves, proceeds, .5);
        close("...and the treasury does not see a cent of it", holdCash, 0, .5);

        /*
         * AND THE DEBT IS ON THE SCREEN THE SAME INSTANT THE MONEY IS.
         *
         * The stock used to be restruck only at the end of the month, so a city
         * that parked $7,742 of proceeds showed a net position of +$7,251 -
         * reserves counted, the $20,000 of paper that bought them not yet. For
         * one whole month the trade screen said borrowing had made the city
         * richer.
         */
        out.printf("   net position the instant the bond is signed: %,.0f%n", holdNet);
        assertTrue("parking borrowed dollars does not read as getting richer",
                holdNet < 0);

        /* ============ 5. the window shuts ============ */
        out.println("\n--- and the world stops answering ---");

        DebtManager shut = new DebtManager();
        shut.setTrade(100, 6);                         // $1,200 a year of exports
        assertTrue("a city that owes nothing can always borrow", shut.foreignWindowOpen());

        shut.addLongTermBond(200_000, 600, 0, .05, true);
        out.printf("   owing %,.0f against %,.0f a year of exports: %s%n",
                shut.getForeignPrincipal(), shut.getMonthlyExports() * 12,
                shut.foreignWindowReason());
        assertTrue("...and one drowning in dollars cannot", !shut.foreignWindowOpen());
        assertTrue("...and is told why in words", shut.foreignWindowReason() != null);

        /* Selling more abroad reopens it, which is the only thing that should. */
        shut.setTrade(20_000, 6);
        assertTrue("earning its way out reopens the window", shut.foreignWindowOpen());

        DebtManager noExports = new DebtManager();
        noExports.setTrade(0, 0);
        noExports.addLongTermBond(1_000, 120, 0, .05, true);
        assertTrue("owing dollars and selling nothing shuts it too",
                !noExports.foreignWindowOpen());

        /* ============ 6. and the price of walking away ============ */
        out.println("\n--- and what a default costs ---");

        double owedAtDefault = city.getDebtManager().getForeignPrincipal();
        double positionBefore = fx.netForeignPosition();

        System.setOut(quiet);
        double written;
        try {
            written = city.defaultOnForeignDebt("the harness said so");
        } finally {
            System.setOut(out);
        }

        close("everything owed abroad is written off", written, owedAtDefault, .01);
        close("...so the city owes nothing abroad", city.getDebtManager().getForeignPrincipal(), 0, 1e-9);
        assertTrue("...and the position improves by the whole of it",
                fx.netForeignPosition() > positionBefore);
        close("...recorded as what it is: money nobody was paid",
                fx.getRepudiated(), owedAtDefault, .01);

        assertTrue("the window is shut", !city.getDebtManager().foreignWindowOpen());
        assertTrue("...for five years", city.getDebtManager()
                .foreignWindowReason().contains("defaulted"));
        assertTrue("...and the price carries a scar",
                city.getDebtManager().getDefaultScar() > 0);

        /*
         * THE TRAP IN THE OTHER DIRECTION, and it is the point of the section.
         *
         * A default flatters every ratio on the trade screen the morning after -
         * debt-to-exports collapses to nothing, and the premium built on it
         * would read as perfect. What stops the next bond being sold is not a
         * ratio, and a city reading only its ratios would conclude it had just
         * made itself creditworthy.
         */
        close("debt-to-exports now says the city is spotless",
                city.getDebtManager().solvencyStress(), 0, 1e-9);
        assertTrue("...and it still cannot borrow a dollar",
                !city.getDebtManager().foreignWindowOpen());

        /* And a refusal, rather than a silent no-op. */
        System.setOut(quiet);
        String refused;
        try {
            refused = city.handleForeignLogic("Term", 5_000, 25, 100, false);
        } finally {
            System.setOut(out);
        }
        assertTrue("...and says so when asked", refused.startsWith("No lender abroad"));

        /* ============ 7. across a reload ============ */
        out.println("\n--- and it all survives a reload ---");

        System.setOut(quiet);
        Game reloaded;
        try {
            /* Something to carry: a live bond, on a currency away from parity. */
            city.getDebtManager().restoreForeignStanding(new double[] { .04, 200 });
            city.handleForeignLogic("Serial", 8_000, 10, 100, true);
            city.simulateMonths(3);
            city.saveGame(4, "the indebted city");
            reloaded = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            reloaded.run();
            reloaded.loadGameSave(4);
        } finally {
            System.setOut(out);
        }

        DebtManager back = reloaded.getDebtManager();
        ForeignAccounts backFx = reloaded.getForeignAccounts();

        assertTrue("the fixture has foreign paper worth carrying",
                city.getDebtManager().getForeignPrincipalUsd() > 0);
        close("the dollars owed reload exactly",
                back.getForeignPrincipalUsd(),
                city.getDebtManager().getForeignPrincipalUsd(), .005);

        /*
         * AND AT THE RIGHT RATE, WITHOUT A MONTH HAVING TICKED.
         *
         * This is the load-path parity trap for this class, and it is a quiet
         * one: a restored bond is constructed at 1.00 and only learns the real
         * rate when DebtManager is told. If that only happened on the month
         * tick, a freshly loaded city would show its foreign debt, its
         * debt-to-GDP and its credit rating at parity for a whole month - long
         * enough for a player to make a decision on all three.
         */
        close("...valued at the rate the city actually has, immediately",
                back.getForeignPrincipal(),
                city.getDebtManager().getForeignPrincipal(), .005);
        close("...and the exchange rate reached the instruments",
                back.getExchangeRate(), backFx.getRate(), 1e-12);

        close("the scar reloads", back.getDefaultScar(),
                city.getDebtManager().getDefaultScar(), 1e-9);
        assertTrue("...and it is a scar worth reloading",
                city.getDebtManager().getDefaultScar() > 0);
        assertTrue("...so the window is still shut on the reloaded city",
                !back.foreignWindowOpen());

        close("the lifetime revaluation reloads",
                backFx.getLifetimeRevaluation(), fx.getLifetimeRevaluation(), .005);
        close("...and what the city walked away from",
                backFx.getRepudiated(), fx.getRepudiated(), .005);

        /*
         * ...AND NO PHANTOM REVALUATION ON THE FIRST TICK.
         *
         * lastDebtRate is saved for exactly this. Without it the reloaded city
         * compares today's rate against 1.00 and books the whole of its currency
         * history as one month's loss - a city that has done nothing at all
         * suddenly reporting the largest revaluation of its life.
         */
        System.setOut(quiet);
        try {
            reloaded.simulateMonths(1);
        } finally {
            System.setOut(out);
        }
        out.printf("   first month after a reload revalues by %,.2f%n",
                reloaded.getForeignAccounts().getLastRevaluation());
        assertTrue("a reloaded city does not book its whole history as one month",
                Math.abs(reloaded.getForeignAccounts().getLastRevaluation())
                        < Math.max(1, back.getForeignPrincipal() * .05));

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
