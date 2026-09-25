package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Proves the currency under a central bank (0.7.2): the rate answers to the
 * real rate, the vault is spent defending it, and the dial and the carry
 * appetite have lost their stops. Not part of the game.
 *
 * WHY THIS EXISTS. Jerus: "i think we need to uncap the rate... but if we
 * do... what happens to everyone?" Until 0.7.2 repriceCurrency() moved the
 * rate by the whole inflation differential every month, unbounded, while the
 * support a high rate gave it saturated at 13 points over the world (the
 * old MAX_RATE_PRESSURE .8 over the old ForeignAccounts.RATE_PULL 6), the
 * hot money's appetite at six (CapitalFlows.MAX_SPREAD) and the dial at 25%
 * (DebtManager.MAX_POLICY_RATE). So a city whose prices rose 40% a year saw
 * its currency fall 40% a year by rule, whatever its central bank did - the
 * 0.6.9 year book, a-reserve-defends-a-currency.md section 1 - and the
 * vault's absorption cost nothing: no dollar ever left it. The design is
 * the-central-bank.md section 8; the batch is its D.
 *
 * What it has to prove, a section each, every one a fixture that causes the
 * condition rather than waiting for it:
 *
 *   1. THE REAL RATE MOVES THE CURRENCY, NOT THE INFLATION DIFFERENTIAL. Past
 *      SETTLING_MONTHS, trade in balance (no pressure), the vault empty
 *      (nothing damped), the city inflating at 20% and the world at 2%: a
 *      dial that pays a real rate over the world's strengthens the rate by
 *      exactly the real gap x RATE_PULL x DRIFT_SPEED (times the openness,
 *      here one) plus the parity pull; a dial under it weakens it by exactly
 *      the mirror; and a dial at the world's real rate plus the city's
 *      inflation moves it by the parity pull alone.
 *   2. A SPIRAL NEEDS AN OUTFLOW: with the real gap at nothing and no
 *      deficit, ten months of 40% inflation move the rate by the parity pull
 *      and nothing else.
 *   3. THE DEFENCE SPENDS. A vault of US$10M at six months' cover against a
 *      month with a US$2M deficit and a push to weaken sells exactly
 *      absorption() x US$2M; the vault falls by it; the push that reaches the
 *      rate is damped by what was sold; cover falls. An empty vault sells and
 *      damps nothing. A surplus month with the currency rising sells nothing
 *      and buys nothing. A vault of US$500k against the same deficit sells
 *      all of it and damps by 500k / 2M, not by absorption().
 *   4. WHAT IT FETCHES IS THE CENTRAL BANK'S CAPITAL, NOT MONEY DESTROYED.
 *      On a bare balance sheet: the sale leaves M0, issued and retired
 *      exactly where they were, takes equity down by exactly what the dollars
 *      fetched at the rate they were sold at (the vault's fall), and leaves
 *      the remittance, the loss carried and the month's profit alone. In a
 *      real city, the month it is defended and every month of ten years
 *      after: M0 moves by the money made and nothing else, nothing is retired
 *      but what the named operations take back, the audit closes and
 *      declares nothing for the defence, the remittance is the month's
 *      profit, and equity less the vault is what the bank owes; a save keeps
 *      the sale and the equity line. And every month Game hands the currency
 *      the real rate differential, struck on the same two inflations parity
 *      is.
 *   5. THE STOPS ARE GONE: the dial takes 60%; the autopilot sets a rule's
 *      rate past the old 25% stop; and the hot money's appetite at twenty
 *      points is larger than at six and stops at MAX_SPREAD.
 *   6. AND SO IS THE CURRENCY'S (0.7.3). Jerus, on the guard of 100: "Better
 *      to have it exceed otherwise one can just ignore once at 100." A
 *      month's push that would take the rate past 100 takes it there - to
 *      150 after the month, where the old guard held it at 100 - and a rate
 *      under the old guard crosses it; the dollar debt's revaluation follows
 *      the rate that far. The same the other way past .01. And a reform still
 *      scales the guards, now a billion either way, with the unit - and the
 *      reformed currency is pushed exactly as the unreformed one would be.
 *
 * Numbering is the running order. Money is in thousands, as everywhere.
 *
 * @author Jerus
 */
public class CurrencyCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-66s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-66s FAIL  %,.9f != %,.9f%n", label, actual, expected);
        } else {
            out.printf("%-66s OK%n", label);
        }
    }

    /** The dial's stop until 0.7.2, for the assertion that it is gone. */
    static final double OLD_DIAL_STOP = .25;

    /** The hot money's old stop, likewise: six points. */
    static final double OLD_SPREAD_STOP = .06;

    /** The currency's guard above until 0.7.3, a hundred local dollars to one of theirs - for the assertions that a rate goes past it. */
    static final double OLD_MAX_RATE = 100;

    /** ...and below, a hundredth. */
    static final double OLD_MIN_RATE = .01;

    /** A synthetic month: the trade and the financial account are all takeMonth() reads. */
    static MoneyAudit.Result month(double exports, double imports, double capitalIn, double capitalOut) {
        double[] f = new double[12];
        f[0] = exports;
        f[1] = imports;
        f[4] = capitalIn;
        f[5] = capitalOut;
        return new MoneyAudit.Result(0, 0, 0, exports + capitalIn, imports + capitalOut, 0, "", f);
    }

    /** A currency past its settling months on the same month, over and over. */
    static ForeignAccounts settled(double exports, double imports, double gdp) {
        ForeignAccounts fx = new ForeignAccounts();
        for (int m = 0; m < ForeignAccounts.SETTLING_MONTHS + 12; m++) {
            fx.takeMonth(month(exports, imports, 0, 0), gdp);
        }
        return fx;
    }

    /** Where the parity pull alone would take the rate from here. */
    static double pulled(double rate, double parity) {
        return rate + (parity - rate) * ForeignAccounts.REVERSION;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        realRate();
        spiral();
        defence();
        capital();
        uncapped();
        unguarded();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ================= 1. the real rate, not the inflation differential ================= */
    static void realRate() {
        out.println("--- 1. the real rate moves the currency, not the inflation differential ---");

        final double worldInflation = .02, cityInflation = .20;
        // Trade in balance and equal to output: no pressure, openness one, so
        // the push is the brief's -(gap x RATE_PULL) x DRIFT_SPEED exactly.
        final double trade = 3_000, gdp = 6_000;
        // The city's prices a fifth over the world's: parity 1.2 against a
        // rate of 1, so the pull is a figure to be seen and not a zero.
        final double cityLevel = 1.2;
        final double worldReal = DebtManager.WORLD_BASE_RATE - worldInflation;

        double[] dials = { .25, .10, cityInflation + worldReal };
        String[] names = { "a dial of 25%", "a dial of 10%", "a dial at the world's real rate plus the city's inflation" };
        for (int i = 0; i < dials.length; i++) {
            ForeignAccounts fx = settled(trade, trade, gdp);
            fx.setParity(cityLevel, 1.0, cityInflation, worldInflation);
            double gap = (dials[i] - cityInflation) - worldReal;
            fx.setRealRateDifferential(gap);
            if (i == 0) {
                assertTrue("fixture: past settling, trade in balance, the vault empty",
                        fx.pressure() == 0 && fx.getReservesUsd() == 0);
                close("fixture: ...and the whole economy trades, so openness is one", fx.getOpenness(), 1, 0);
            }
            double r0 = fx.getRate();
            fx.repriceCurrency();
            double moved = r0 * (1 - gap * ForeignAccounts.RATE_PULL * ForeignAccounts.DRIFT_SPEED);
            double expected = pulled(moved, fx.getParity());
            double pullOnly = pulled(r0, fx.getParity());
            out.printf("   %s: real gap %+.2f points, the rate %.6f -> %.6f (the pull alone %.6f;"
                    + " the old drift would have made it %.6f)%n", names[i], gap * 100, r0, fx.getRate(),
                    pullOnly, pulled(r0 * (1 + (cityInflation - worldInflation) / 12), fx.getParity()));
            if (i == 0) {
                assertTrue("paying over the world in real terms strengthens the rate", fx.getRate() < pullOnly);
                close("...by exactly -(real gap x RATE_PULL) x DRIFT_SPEED, and the parity pull",
                        fx.getRate(), expected, 1e-12);
            } else if (i == 1) {
                assertTrue("paying under it weakens the rate", fx.getRate() > pullOnly);
                close("...by exactly the mirror", fx.getRate(), expected, 1e-12);
            } else {
                close("at the world's real rate the rate moves by the parity pull alone",
                        fx.getRate(), pullOnly, 1e-12);
                assertTrue("...so the inflation differential no longer moves the rate directly",
                        Math.abs(fx.getRate() - pullOnly) < 1e-12
                                && fx.inflationGap() > .1);
            }
        }
    }

    /* ================= 2. a spiral needs an outflow ================= */
    static void spiral() {
        out.println("\n--- 2. a spiral needs an outflow ---");

        final double worldInflation = .02, cityInflation = .40;
        final double worldReal = DebtManager.WORLD_BASE_RATE - worldInflation;
        final double dial = cityInflation + worldReal;
        final double trade = 3_000, gdp = 6_000;

        ForeignAccounts fx = settled(trade, trade, gdp);
        double level = 1.0, monthly = Math.pow(1 + cityInflation, 1 / 12.0);
        double expected = fx.getRate(), oldRule = fx.getRate();
        boolean quiet_ = true;
        for (int m = 0; m < 10; m++) {
            fx.takeMonth(month(trade, trade, 0, 0), gdp);
            level *= monthly;
            fx.setParity(level, 1.0, cityInflation, worldInflation);
            fx.setRealRateDifferential((dial - cityInflation) - worldReal);
            quiet_ &= fx.pressure() == 0 && fx.monthDeficitUsd() == 0;
            fx.repriceCurrency();
            expected = pulled(expected, fx.getParity());
            oldRule = pulled(oldRule * (1 + (cityInflation - worldInflation) / 12), fx.getParity());
        }
        out.printf("   ten months of 40%% inflation: the rate %.6f; the old drift would have made it %.6f"
                + " (%+.1f%%)%n", fx.getRate(), oldRule, (oldRule / fx.getRate() - 1) * 100);
        assertTrue("fixture: ten months with no push and no deficit", quiet_);
        close("with the real gap at nothing and no outflow, the rate moves by the parity pull alone",
                fx.getRate(), expected, 1e-12);
        assertTrue("...where the inflation drift would have carried it a third weaker",
                oldRule / fx.getRate() > 1.25);
    }

    /* ================= 3. the defence spends ================= */

    /**
     * A currency past settling on a trailing deficit, a vault of this many
     * dollars bought at 1.00, and then the month under test: the same trade
     * and this much capital leaving.
     */
    static ForeignAccounts defended(double exports, double imports, double gdp, double vaultUsd,
                                    double capitalOut) {
        ForeignAccounts fx = settled(exports, imports, gdp);
        fx.buyReserves(vaultUsd * fx.getRate());
        fx.startMonth();
        fx.takeMonth(month(exports, imports, 0, capitalOut), gdp);
        return fx;
    }

    /** What the reprice should leave the rate at, on this push and this much of it met. */
    static double repriced(double r0, double total, double met, double openness, double parity) {
        return pulled(r0 * (1 + total * (1 - met) * openness * ForeignAccounts.DRIFT_SPEED), parity);
    }

    static void defence() {
        out.println("\n--- 3. the defence sells the vault's dollars, and what it sells is what it damps ---");

        // A US$10M vault is six months of these imports: exactly the cover
        // that earns the most damping there is.
        final double vault = 10_000;
        final double imports = vault / ForeignAccounts.COMFORTABLE_COVER;
        final double exports = imports / 2;
        final double gdp = 20_000;
        // The month under test: the same trade, and capital leaving, so the
        // month's own accounts show a deficit of exactly US$2M.
        final double deficit = 2_000;
        final double capitalOut = deficit - (imports - exports);

        ForeignAccounts full = defended(exports, imports, gdp, vault, capitalOut);
        double total = full.pressure() + full.ratePressure();
        double capacity = full.absorption();
        double cover = full.importCover();
        double r0 = full.getRate();
        assertTrue("fixture: the trailing deficit pushes the currency weaker", total > 0);
        close("fixture: the month's own accounts show a US$2M deficit", full.monthDeficitUsd(), deficit, 1e-9);
        close("fixture: the vault is at six months' cover", cover, ForeignAccounts.COMFORTABLE_COVER, 1e-9);
        close("fixture: ...so its capacity is the most there is", capacity, ForeignAccounts.MAX_ABSORPTION, 1e-12);
        close("fixture: the treasury bought nothing this month", full.getBoughtThisMonth(), 0, 0);

        full.repriceCurrency();
        double sold = full.getDefenceUsd();
        out.printf("   a US$10,000k vault against a US$2,000k deficit: sold US$%,.2fk, damped %.0f%%,"
                + " the rate %.6f -> %.6f, cover %.2f -> %.2f months%n",
                sold, full.getLastAbsorption() * 100, r0, full.getRate(), cover, full.importCover());
        close("the central bank sells absorption() x the deficit, to the cent", sold, capacity * deficit, 1e-9);
        close("...and the vault falls by exactly that", full.getReservesUsd(), vault - sold, 1e-9);
        close("...at the month's rate, which is what it fetches", full.getDefenceLocal(), sold * r0, 1e-9);
        close("...and comes off the treasury's intervention record, as a sale does",
                full.getLifetimeIntervention(), vault * ForeignAccounts.OPENING_RATE - sold * r0, 1e-9);
        close("the realised absorption is what the sale met of the deficit", full.getLastAbsorption(),
                sold / deficit, 1e-12);
        close("the push that reached the rate is total x (1 - realised) x openness",
                full.getRate(), repriced(r0, total, sold / deficit, full.getOpenness(), full.getParity()), 1e-12);
        assertTrue("...and cover fell", full.importCover() < cover);
        close("...to what is left, at the new rate", full.importCover(),
                (vault - sold) * full.getRate() / imports, 1e-9);
        close("the dollars sold since founding count it", full.getDefenceUsdLifetime(), sold, 1e-12);

        /* ---- the same month with nothing in the vault ---- */
        ForeignAccounts empty = defended(exports, imports, gdp, 0, capitalOut);
        double emptyTotal = empty.pressure() + empty.ratePressure();
        double e0 = empty.getRate();
        empty.repriceCurrency();
        close("an empty vault sells nothing", empty.getDefenceUsd(), 0, 0);
        close("...damps nothing", empty.getLastAbsorption(), 0, 0);
        close("...and the whole push reaches the rate", empty.getRate(),
                repriced(e0, emptyTotal, 0, empty.getOpenness(), empty.getParity()), 1e-12);

        /* ---- a surplus, and the currency rising ---- */
        ForeignAccounts surplus = defended(imports * 2, imports, gdp, vault, 0);
        double upTotal = surplus.pressure() + surplus.ratePressure();
        double s0 = surplus.getRate();
        assertTrue("fixture: a month in surplus, pushed stronger",
                upTotal < 0 && surplus.monthDeficitUsd() == 0);
        surplus.repriceCurrency();
        close("a surplus month sells nothing", surplus.getDefenceUsd(), 0, 0);
        close("...and buys nothing: the vault is what it was", surplus.getReservesUsd(), vault, 0);
        close("...not by the treasury either", surplus.getBoughtThisMonth(), 0, 0);
        close("...and the rise reaches the rate in full", surplus.getRate(),
                repriced(s0, upTotal, 0, surplus.getOpenness(), surplus.getParity()), 1e-12);
        assertTrue("...which rose", surplus.getRate() < s0);

        /* ---- a vault that cannot meet what its cover would sell ---- */
        // Small imports, so US$500k is ten months of them and the capacity is
        // the most there is; the month's deficit is capital leaving.
        final double smallImports = 50, smallExports = 40, smallVault = 500;
        ForeignAccounts shallow = defended(smallExports, smallImports, 400, smallVault,
                deficit - (smallImports - smallExports));
        double shallowTotal = shallow.pressure() + shallow.ratePressure();
        double shallowCapacity = shallow.absorption();
        double h0 = shallow.getRate();
        assertTrue("fixture: the vault's capacity would sell more than it holds",
                shallowTotal > 0 && shallowCapacity * shallow.monthDeficitUsd() > smallVault);
        shallow.repriceCurrency();
        out.printf("   a US$500k vault against the same deficit: capacity %.0f%%, sold US$%,.2fk, damped %.0f%%%n",
                shallowCapacity * 100, shallow.getDefenceUsd(), shallow.getLastAbsorption() * 100);
        close("a US$500k vault against a US$2M deficit sells all of it", shallow.getDefenceUsd(), smallVault, 1e-9);
        close("...and is empty", shallow.getReservesUsd(), 0, 0);
        close("...and damps 500k / 2M", shallow.getLastAbsorption(), smallVault / deficit, 1e-12);
        assertTrue("...not absorption()", Math.abs(shallow.getLastAbsorption() - shallowCapacity) > .1);
        close("...and the push that reached the rate is the rest of it", shallow.getRate(),
                repriced(h0, shallowTotal, smallVault / deficit, shallow.getOpenness(), shallow.getParity()), 1e-12);
    }

    /* ================= 4. the dollars it sells are capital, not money destroyed ================= */
    static void capital() throws Exception {
        out.println("\n--- 4. and what the dollars fetch is the central bank's capital spent, not money destroyed ---");

        /*
         * ON A BARE BALANCE SHEET FIRST, where nothing else moves: a central
         * bank that has printed for the treasury and has a profit owed, reading
         * the vault of section 3's fixture. The sale may take the vault and
         * equity down together and must leave M0, the ledger of money made,
         * and the remittance exactly where they were.
         */
        final double vault = 10_000;
        final double imports = vault / ForeignAccounts.COMFORTABLE_COVER;
        final double exports = imports / 2;
        final double deficit = 2_000;
        ForeignAccounts books = defended(exports, imports, 20_000, vault, deficit - (imports - exports));
        CentralBank bare = new CentralBank(books::getReserves);
        bare.advanceToTreasury(500);
        bare.chargeAdvances(5);
        bare.closeMonth();
        double m0 = bare.m0(), eq = bare.equity(), vaultLocal = bare.getVault();
        double rem = bare.getRemittanceDue(), loss = bare.getLossCarried(), profit = bare.monthProfit();
        double issued = bare.getIssuedLifetime(), retired = bare.getRetiredLifetime();
        double r0 = books.getRate();
        assertTrue("fixture: the bank has made money and owes a remittance", m0 > 0 && rem > 0);

        books.repriceCurrency();
        bare.dollarsSold(books.getDefenceLocal());
        double fetched = books.getDefenceLocal();
        double left = books.getReservesUsd() * (books.getRate() - r0);   // what the move did to the rest
        out.printf("   sold US$%,.2fk for $%,.2fk: M0 %,.2f -> %,.2f, equity %,.2f -> %,.2f"
                + " (the rest of the vault revalued %+,.4f)%n",
                books.getDefenceUsd(), fetched, m0, bare.m0(), eq, bare.equity(), left);
        assertTrue("fixture: the defence sold", fetched > 0);
        close("M0 did not move", bare.m0(), m0, 0);
        close("...nothing issued", bare.getIssuedLifetime(), issued, 0);
        close("...and nothing retired", bare.getRetiredLifetime(), retired, 0);
        close("equity fell by exactly what the dollars fetched, at the rate they were sold at",
                (bare.equity() - left) - eq, -fetched, 1e-9);
        close("...which is exactly what the vault fell by, at that rate",
                (bare.getVault() - left) - vaultLocal, -fetched, 1e-9);
        close("the remittance is untouched", bare.getRemittanceDue(), rem, 0);
        close("...the loss carried", bare.getLossCarried(), loss, 0);
        close("...and the month's profit: it is capital spent, not a loss to recover",
                bare.monthProfit(), profit, 0);
        close("the balance sheet's line says why: spent defending the currency",
                bare.vaultSpent(), fetched, 1e-12);
        close("...this month", bare.getDefendedThisMonth(), fetched, 1e-12);

        /* ...AND IN A REAL CITY, month by month, with the audit watching. */
        Path root = Files.createTempDirectory("currencycheck");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            city.run();
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
            // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
            // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(ForeignCheck.template(city, "House"), 500, false);
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 8, false);
            city.buildStack(ForeignCheck.template(city, "Small Grocery Store"), 3, false);
            city.buildStack(ForeignCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(ForeignCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Industrial Bakery"), 2, false);
        } finally {
            System.setOut(out);
        }
        ForeignAccounts fx = city.getForeignAccounts();
        CentralBank cb = city.getCentralBank();
        DebtManager dial = city.getDebtManager();

        /*
         * THE REAL RATE DIFFERENTIAL, every month the fixture plays: the dial
         * less the city's inflation - the reading the month opened on, which
         * is the one the reprice sees, the index being struck after it - less
         * the world's base rate less the world's realised inflation.
         */
        double worstWiring = 0;
        int defendedAt = 0, played = 0;
        double vaultBefore = 0, rateBefore = 0;
        Before was = null;
        final int most = 240;
        for (; played < most; played++) {
            double cityInflation = city.getPriceIndex().inflation();
            vaultBefore = fx.getReservesUsd();
            rateBefore = fx.getRate();
            was = new Before(cb);
            System.setOut(quiet);
            try { city.simulateMonths(1); } finally { System.setOut(out); }
            double handed = (dial.getPolicyRate() - cityInflation)
                    - (DebtManager.WORLD_BASE_RATE - city.getWorldEconomy().realisedInflation());
            worstWiring = Math.max(worstWiring, Math.abs(fx.getRealRateDifferential() - handed));
            if (fx.getDefenceUsd() > 0) { defendedAt = city.getMonth(); played++; break; }
        }
        out.printf("   first defended at month %d: sold US$%,.2fk of US$%,.2fk, fetching $%,.2fk at %.6f%n",
                defendedAt, fx.getDefenceUsd(), vaultBefore, fx.getDefenceLocal(), rateBefore);
        close("every month, Game handed the currency the real rate differential",
                worstWiring, 0, 1e-12);
        assertTrue("fixture: the currency was defended in a real city", defendedAt > 0);
        if (defendedAt == 0) return;

        double sold = fx.getDefenceUsd();
        fetched = fx.getDefenceLocal();
        double deficitUsd = -fx.balance() / rateBefore;
        double capacity = ForeignAccounts.MAX_ABSORPTION * Math.max(0, Math.min(1,
                vaultBefore * rateBefore / fx.monthlyImports() / ForeignAccounts.COMFORTABLE_COVER));
        close("fixture: the treasury neither bought nor sold", fx.getBoughtThisMonth() + fx.getSoldThisMonth(), 0, 0);
        close("the sale is the vault's capacity times the month's own deficit, at most the vault",
                sold, Math.min(capacity * deficitUsd, vaultBefore), 1e-9 * Math.max(1, sold));
        close("...the vault fell by exactly it", vaultBefore - fx.getReservesUsd(), sold, 1e-9 * Math.max(1, sold));
        close("...and it fetched the dollars at the month's rate", fetched, sold * rateBefore, 1e-9 * Math.max(1, fetched));
        close("the central bank booked it the month it was sold", cb.getDefendedThisMonth(), fetched, 1e-9);
        close("...as spent defending the currency since founding", cb.vaultSpent(), fetched, 1e-9);
        close("equity fell by it: its move, less the rest of the sheet's, less the revaluation",
                (cb.equity() - was.equity) - ((cb.equity() - cb.getVault()) - was.notVault)
                        - fx.getLastVaultRevaluation(), -fetched, 1e-6 * Math.max(1, fetched));
        was.monthHeld(city, "the month it was sold");

        /*
         * ...AND IT SURVIVES A SAVE: the month's sale, what it fetched, the
         * dollars sold since founding, the central bank's line for it, and the
         * real rate the reprice was handed.
         */
        final Game[] back = new Game[1];
        System.setOut(quiet);
        try {
            city.saveGame(3, "the defended city");
            back[0] = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            back[0].run();
            back[0].loadGameSave(3);
        } finally {
            System.setOut(out);
        }
        ForeignAccounts backFx = back[0].getForeignAccounts();
        CentralBank backCb = back[0].getCentralBank();
        close("a save keeps the month's sale", backFx.getDefenceUsd(), sold, 1e-9);
        close("...what it fetched", backFx.getDefenceLocal(), fetched, 1e-9);
        close("...the dollars sold since founding", backFx.getDefenceUsdLifetime(), fx.getDefenceUsdLifetime(), 1e-9);
        close("...the vault", backFx.getReservesUsd(), fx.getReservesUsd(), 1e-9);
        close("...what the central bank spent on it", backCb.vaultSpent(), cb.vaultSpent(), 1e-9);
        close("...its equity", backCb.equity(), cb.equity(), 1e-6);
        close("...and the real rate the reprice was handed", backFx.getRealRateDifferential(),
                fx.getRealRateDifferential(), 0);

        /*
         * And how often the mechanic fires in a real city - the third rule -
         * with the same claims held every month it plays: M0 moved by the
         * money made and nothing else, the audit closed and silent about the
         * defence, the remittance the month's profit.
         */
        int monthsDefended = 1;
        double soldRun = sold, fetchedRun = fetched;
        int more = 0;
        for (; more < 120; more++) {
            Before month = new Before(cb);
            System.setOut(quiet);
            try { city.simulateMonths(1); } finally { System.setOut(out); }
            if (fx.getDefenceUsd() > 0) {
                monthsDefended++;
                soldRun += fx.getDefenceUsd();
                fetchedRun += fx.getDefenceLocal();
            }
            month.monthHeld(city, null);
        }
        out.printf("   over %d months the central bank defended the currency in %d, selling US$%,.0fk;"
                + " the vault holds US$%,.0fk; spent defending it since founding $%,.0fk; M0 $%,.0fk%n",
                played + more, monthsDefended, soldRun, fx.getReservesUsd(), cb.vaultSpent(), cb.m0());
        assertTrue("the defence fires in a real city more than once", monthsDefended > 1);
        close("...and the equity line is every month's sale at its own month's rate", cb.vaultSpent(),
                fetchedRun, 1e-6 * Math.max(1, fetchedRun));
        assertTrue("in every one of those months: M0 moved by the money made alone", Before.broken[0] == 0);
        assertTrue("...nothing retired but what the named operations took back", Before.broken[1] == 0);
        assertTrue("...the audit closed, and declared nothing for the defence", Before.broken[2] == 0);
        assertTrue("...the remittance was the month's profit, the sale not in it", Before.broken[3] == 0);
        assertTrue("...and the central bank kept nothing: its equity less the vault is what it owes",
                Before.broken[4] == 0);
    }

    /**
     * The central bank and the vault as a month opened, and the five claims
     * section 4 holds each month against them. A claim that fails is counted
     * in broken[] and, on the named month, asserted there and then.
     */
    static final class Before {
        static final int[] broken = new int[5];
        final double m0, equity, notVault, remittance, loss;

        Before(CentralBank cb) {
            m0 = cb.m0();
            equity = cb.equity();
            notVault = cb.equity() - cb.getVault();
            remittance = cb.getRemittanceDue();
            loss = cb.getLossCarried();
        }

        void monthHeld(Game city, String named) {
            CentralBank cb = city.getCentralBank();
            MoneyAudit.Result r = city.getLastMoneyAudit();
            double made = cb.getIssued() - cb.getRetired();
            double namedRetired = cb.getRepaidByBank() + cb.getRepaidByTreasury() + cb.getWindowInterest()
                    + cb.getAdvancesInterest() + cb.getSoldPaper() + cb.getPaperCoupons()
                    + cb.getPaperRedeemed() + cb.getBoughtBack();
            double tol = 1e-6 * Math.max(1, Math.abs(cb.m0()));
            boolean[] held = {
                Math.abs((cb.m0() - m0) - made) <= tol && Math.abs(r.moneyMade() - made) <= tol,
                Math.abs(cb.getRetired() - namedRetired) <= tol,
                (Math.abs(r.residual) <= .01 || r.relative() <= 1e-7) && !r.detail.contains("Defen"),
                Math.abs((cb.getRemittanceDue() - remittance) - (cb.getLossCarried() - loss)
                        + cb.getRemitted() - cb.monthProfit()) <= tol,
                Math.abs(cb.equity() - cb.getVault() - (cb.getRemittanceDue() - cb.getLossCarried())) <= .01,
            };
            for (int k = 0; k < held.length; k++) if (!held[k]) broken[k]++;
            if (named == null) return;
            out.printf("   %s: M0 %,.2f -> %,.2f, money made $%,.2f; the audit's residual $%.4f%n",
                    named, m0, cb.m0(), made, r.residual);
            assertTrue("M0 moved by the money made and nothing else, " + named, held[0]);
            assertTrue("...nothing retired but what the named operations took back", held[1]);
            assertTrue("...the audit closed, and declared nothing for the defence", held[2]);
            assertTrue("...the remittance is the month's profit, the sale not in it", held[3]);
            assertTrue("...and equity less the vault is what the bank owes: nothing kept", held[4]);
        }
    }

    /* ================= 5. the stops are gone ================= */
    static void uncapped() throws Exception {
        out.println("\n--- 5. the dial, the rule and the appetite are uncapped ---");

        DebtManager market = new DebtManager();
        market.setPolicyRate(.60);
        close("the dial takes 60%", market.getPolicyRate(), .60, 0);
        market.setPolicyRate(25);
        close("...and stops at MAX_POLICY_RATE, where a typo would have taken it",
                market.getPolicyRate(), DebtManager.MAX_POLICY_RATE, 0);
        close("the rule is advised unclamped past the old stop",
                market.advisedPolicyRate(.45), market.ruleRate(.45), 0);

        /*
         * THE AUTOPILOT ON A CITY WHOSE RULE ASKS MORE THAN 25%: a year of
         * prices read as 45% inflation - the index's own history, restored,
         * the way a save carries it - so the month opens on a rule of 67.5%.
         */
        Path root = Files.createTempDirectory("currencycheck-rule");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            city.run();
            city.buildStack(ForeignCheck.template(city, "House"), 200, false);
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 4, false);
            city.simulateMonths(40);
        } finally {
            System.setOut(out);
        }
        PriceIndex prices = city.getPriceIndex();
        double[] saved = prices.toSaveArray();
        int seen = (int) Math.round(saved[4]);
        double now = saved[5 + (seen - 1) % PriceIndex.WINDOW];
        saved[5 + seen % PriceIndex.WINDOW] = now / 1.45;
        prices.restore(saved);
        double read = prices.inflation();
        close("fixture: the city reads a year of 45% inflation", read, .45, 1e-9);
        city.getDebtManager().setAutopilot(true);
        System.setOut(quiet);
        try { city.simulateMonths(1); } finally { System.setOut(out); }
        out.printf("   the rule on %.1f%% inflation: %.2f%%; the dial: %.2f%%%n",
                read * 100, city.getDebtManager().ruleRate(read) * 100,
                city.getDebtManager().getPolicyRate() * 100);
        close("the autopilot sets the rule's rate", city.getDebtManager().getPolicyRate(),
                city.getDebtManager().ruleRate(read), 1e-12);
        assertTrue("...past the old stop of the dial", city.getDebtManager().getPolicyRate() > OLD_DIAL_STOP);

        /* ---- the hot money's appetite ---- */
        CapitalFlows flows = new CapitalFlows();
        final double world = DebtManager.WORLD_BASE_RATE, gdp = 1_000;
        double atSix = flows.stockAt(world + OLD_SPREAD_STOP, world + OLD_SPREAD_STOP, world, 0, gdp);
        double atTwenty = flows.stockAt(world + .20, world + .20, world, 0, gdp);
        double atFifty = flows.stockAt(world + .50, world + .50, world, 0, gdp);
        out.printf("   what would come: $%,.0fk at six points, $%,.0fk at twenty, $%,.0fk at fifty%n",
                atSix, atTwenty, atFifty);
        assertTrue("the appetite at twenty points is larger than at six: it no longer stops there",
                atTwenty > atSix);
        close("...it is twenty points' worth", atTwenty, .20 * CapitalFlows.APPETITE * gdp * 12, 1e-9);
        close("...and it stops at MAX_SPREAD", atFifty,
                CapitalFlows.MAX_SPREAD * CapitalFlows.APPETITE * gdp * 12, 1e-9);
        flows.takeMonth(world + .20, world + .20, world, 0, gdp, 1e9, 0, false, false, 1);
        close("a month at twenty points is pulled by all twenty", flows.getSpread(), .20, 1e-12);
        close("...toward the stock twenty points want", flows.getTarget(), atTwenty, 1e-9);
    }

    /* ================= 6. the currency's guards are a billion either way ================= */

    /** A settled currency, trade in balance and the vault empty, put at this rate the way a save would put it. */
    static ForeignAccounts at(double rate) {
        ForeignAccounts fx = settled(3_000, 3_000, 6_000);
        double[] saved = fx.toSaveArray();
        saved[3] = rate;
        fx.restore(saved);
        return fx;
    }

    /** One month of it: the same balanced trade, the city's price level at this (so parity at it, in founding money), and this real gap, repriced. */
    static void month(ForeignAccounts fx, double parityLevel, double realGap) {
        month(fx, parityLevel, realGap, 1);
    }

    /** ...in a unit this many of the founding's: the trade and the output are money, and a reform divides them. */
    static void month(ForeignAccounts fx, double parityLevel, double realGap, double unit) {
        fx.takeMonth(month(3_000 * unit, 3_000 * unit, 0, 0), 6_000 * unit);
        fx.setParity(parityLevel, 1.0, .02, .02);
        fx.setRealRateDifferential(realGap);
        fx.repriceCurrency();
    }

    static void unguarded() {
        out.println("\n--- 6. the rate goes where the push takes it: the guards are a billion either way ---");

        // A real gap of a hundred points either way: the whole of
        // MAX_RATE_PRESSURE, eight percent a month at an openness of one.
        final double weak = -1.0, strong = 1.0;
        final double push = ForeignAccounts.MAX_RATE_PRESSURE * ForeignAccounts.DRIFT_SPEED;
        final double usdOwed = 1_000;

        /*
         * PAST A HUNDRED. A city whose prices have run 150 times the world's
         * (parity 150), its currency at the rate from which one month of the
         * full push and the pull lands on 150 exactly: the old guard would
         * have ended the month at 100.
         */
        final double target = 150;
        double r0 = (target - target * ForeignAccounts.REVERSION)
                / ((1 + push) * (1 - ForeignAccounts.REVERSION));
        ForeignAccounts high = at(r0);
        high.takeForeignDebt(usdOwed, r0);
        assertTrue("fixture: past settling, trade in balance, the vault empty, the whole economy trading",
                high.pressure() == 0 && high.getReservesUsd() == 0 && high.getOpenness() == 1);
        month(high, target, weak);
        high.takeForeignDebt(usdOwed, high.getRate());
        out.printf("   from %.4f on a real gap of %.0f points: %.4f after the month (the old guard: %.0f)%n",
                r0, weak * 100, high.getRate(), OLD_MAX_RATE);
        close("a month's push past the old guard of 100 takes the rate to 150, not 100",
                high.getRate(), target, 1e-9);
        close("...and the dollar debt is worth the dollars at it", high.getForeignDebt(), usdOwed * target, 1e-6);
        close("...the month's revaluation the whole of the move", high.getLastRevaluation(),
                usdOwed * (target - r0), 1e-6);

        /* ...and a rate under the old guard crosses it, and keeps going. */
        ForeignAccounts climbing = at(98);
        double expected = 98, first = 0;
        boolean exact = true;
        int months = 0;
        while (climbing.getRate() < target && months < 24) {
            month(climbing, target, weak);
            expected = repriced(expected, ForeignAccounts.MAX_RATE_PRESSURE, 0, 1, climbing.getParity());
            exact &= Math.abs(climbing.getRate() - expected) <= 1e-9 * expected;
            if (++months == 1) first = climbing.getRate();
        }
        out.printf("   from 98: %.4f after one month, %.4f after %d%n", first, climbing.getRate(), months);
        assertTrue("a rate under the old guard crosses it in a month", first > OLD_MAX_RATE);
        assertTrue("...and every month is the push and the pull, never the guard", exact && climbing.getRate() >= target);

        /* UNDER A HUNDREDTH, the mirror: parity at 1/150, the push strengthening. */
        final double low = 1 / target;
        double s0 = (low - low * ForeignAccounts.REVERSION)
                / ((1 - push) * (1 - ForeignAccounts.REVERSION));
        ForeignAccounts dear = at(s0);
        dear.takeForeignDebt(usdOwed, s0);
        month(dear, low, strong);
        dear.takeForeignDebt(usdOwed, dear.getRate());
        out.printf("   from %.6f on a real gap of %+.0f points: %.6f after the month (the old guard: %.2f)%n",
                s0, strong * 100, dear.getRate(), OLD_MIN_RATE);
        close("a month's push past the old floor of .01 takes the rate to 1/150, not .01",
                dear.getRate(), low, 1e-12);
        close("...and the dollar debt is worth the dollars at it", dear.getForeignDebt(), usdOwed * low, 1e-9);
        close("...the month's revaluation the whole of the move, a gain", dear.getLastRevaluation(),
                usdOwed * (low - s0), 1e-9);
        ForeignAccounts falling = at(.0105);
        month(falling, low, strong);
        close("a rate over the old floor crosses it in a month, by the push and the pull alone",
                falling.getRate(), repriced(.0105, -ForeignAccounts.MAX_RATE_PRESSURE, 0, 1, falling.getParity()), 1e-15);
        assertTrue("...to under .01", falling.getRate() < OLD_MIN_RATE);

        /*
         * A REFORM SCALES THE GUARDS WITH THE UNIT. Three zeros off a currency
         * at 150: the rate is 0.15, the guards a thousandth of what they were,
         * and a month's push on the reformed currency lands on a thousandth of
         * where the same push takes the unreformed one.
         */
        ForeignAccounts reformed = at(target), twin = at(target);
        reformed.redenominate(.001);
        close("three zeros off: the rate a thousandth", reformed.getRate(), target * .001, 1e-12);
        close("...the guard above it a thousandth of MAX_RATE", reformed.getMaxRate(),
                ForeignAccounts.MAX_RATE * .001, 1e-3);
        close("...and the one below a thousandth of MIN_RATE", reformed.getMinRate(),
                ForeignAccounts.MIN_RATE * .001, 1e-24);
        // The price level is an index and a reform does not move it; the
        // parity it anchors is a rate, and the reform moved that.
        month(reformed, target, weak, .001);
        month(twin, target, weak);
        close("...and the push moves it as it moves the unreformed one, a thousandth the size",
                reformed.getRate(), twin.getRate() * .001, 1e-12);
        ForeignAccounts seeded = new ForeignAccounts();
        seeded.seedConstants(1_000);
        close("a city founded in the new unit carries the guards in it", seeded.getMaxRate(),
                ForeignAccounts.MAX_RATE / 1_000, 1e-3);
    }
}
