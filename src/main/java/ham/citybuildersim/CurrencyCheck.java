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
 *   1. THE REAL RATE HOLDS THE CURRENCY AT A LEVEL, AND THE INFLATION
 *      DIFFERENTIAL REACHES IT ONLY THROUGH PARITY'S TREND (restated for XR1,
 *      docs/studies/spec-exchange-rates.md 3.2-3.3: the 0.7.2 premise that
 *      inflation moves no rate by rule is retired, parity's trend is followed
 *      one for one). A quiet-market currency, trade in balance, the vault
 *      empty (so the cover's premium is RISK_PREMIUM), parity held at 1.2
 *      with the city at 20% and the world at 2%: after 120 months under each
 *      of three dials the rate is the trend and the real gap's, the balance's
 *      and the cover's levels reached, to the model's own getters; a dial that
 *      pays over the world's real rate holds it under the neutral dial's, one
 *      that pays under it over it, and the neutral dial's is parity's trend
 *      times the cover's premium alone.
 *   2. INFLATION MOVES THE RATE WITH PARITY'S TREND AND NO FURTHER: with the
 *      real gap at nothing, no deficit, the vault at THIN_COVER or more and
 *      the market quiet, ten months of 40% inflation put each month's rate on
 *      getParityTrend(), and the rate ends above where it began.
 *   3. THE DEFENCE SPENDS. A vault of US$10M at six months' cover against a
 *      month with a US$2M deficit and a month the D$ would fall sells exactly
 *      absorption() x US$2M; the vault falls by it; the sale lifts the D$ by
 *      INTERVENTION_IMPACT x what it fetched over a year of GDP (the intervention's
 *      move, which replaces the old damping of a push); cover falls. An empty
 *      vault sells nothing and lifts nothing. A surplus month with the
 *      currency rising sells nothing and buys nothing. A vault of US$500k
 *      against the same deficit sells all of it and meets 500k / 2M of it, not
 *      absorption().
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
 *      to have it exceed otherwise one can just ignore once at 100." A parity
 *      of 150 takes the rate there as the trend arrives - past 100, where
 *      the old guard held it - and the dollar debt's revaluation follows the
 *      rate that far. The same the other way past .01, at a parity of .005.
 *      And a reform still scales the guards, now a billion either way, with
 *      the unit - and the reformed currency moves exactly as the unreformed
 *      one would.
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

    /** The months a fixture trades before its test month: 24, the currency's settling months until XR1, and a year more for the trailing figures to settle. */
    static final int TRADING_MONTHS = 24 + 12;

    /** A currency that has traded the same month, over and over, for TRADING_MONTHS: its market quiet (a bare ForeignAccounts is unseeded) and not yet repriced. */
    static ForeignAccounts settled(double exports, double imports, double gdp) {
        ForeignAccounts fx = new ForeignAccounts();
        for (int m = 0; m < TRADING_MONTHS; m++) {
            fx.takeMonth(month(exports, imports, 0, 0), gdp);
        }
        return fx;
    }

    /** What a sale of what the defence fetched lifts the D$ by, in logs (ForeignAccounts, THE FLOAT, step 7): INTERVENTION_IMPACT x the local money over a year of the GDP the model weighs it against. */
    static double lift(ForeignAccounts fx) {
        return ForeignAccounts.INTERVENTION_IMPACT * fx.getDefenceLocal() / (12 * fx.getGdpTrailing());
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

    /* ================= 1. the real rate holds a level; inflation arrives through parity's trend ================= */
    static void realRate() {
        out.println("--- 1. the real rate holds the currency at a level, and the inflation differential reaches it only through parity's trend ---");

        // RESTATED FOR XR1 (docs/studies/spec-exchange-rates.md 3.2 and 3.3). The
        // premise of 0.7.2 - that the inflation differential moves no rate by rule,
        // only the real gap's push does - is retired: parity's trend is followed one
        // for one (3.2), and the real gap is a level (3.3). What stays is the
        // direction (paying over the world's holds the D$ dearer, under it cheaper)
        // and that the differential alone, with the real gap at nothing, adds nothing
        // of its own beyond the parity it raises.
        final double worldInflation = .02, cityInflation = .20;
        // Trade in balance and equal to output: no balance level, openness one, so
        // the gate is fully open.
        final double trade = 3_000, gdp = 6_000;
        // The city's prices a fifth over the world's: parity 1.2 against a rate of 1,
        // so the trend has a distance to arrive from.
        final double cityLevel = 1.2;
        final double worldReal = DebtManager.WORLD_BASE_RATE - worldInflation;

        double[] dials = { .25, .10, cityInflation + worldReal };
        String[] names = { "a dial of 25%", "a dial of 10%", "a dial at the world's real rate plus the city's inflation" };
        ForeignAccounts[] fxs = new ForeignAccounts[dials.length];
        for (int i = 0; i < dials.length; i++) {
            ForeignAccounts fx = settled(trade, trade, gdp);
            fxs[i] = fx;
            double gap = (dials[i] - cityInflation) - worldReal;
            if (i == 0) {
                assertTrue("fixture: trade in balance, the vault empty, the market quiet",
                        fx.balanceTarget() == 0 && fx.getReservesUsd() == 0 && !fx.isMarketSeeded());
                close("fixture: ...and the whole economy trades, so openness is one and the gate open", fx.getOpenness(), 1, 0);
                close("fixture: ...and the empty vault costs the whole risk premium", fx.coverTarget(),
                        ForeignAccounts.RISK_PREMIUM, 1e-12);
            }
            double r0 = fx.getRate();
            for (int m = 0; m < 120; m++) {
                fx.takeMonth(month(trade, trade, 0, 0), gdp);
                fx.setParity(cityLevel, 1.0, cityInflation, worldInflation);
                fx.setRealRateDifferential(gap);
                fx.repriceCurrency();
            }
            double expected = fx.getParityTrend() * Math.exp(fx.gate()
                    * (fx.getGapReached() + fx.getBalanceReached() + fx.getCoverReached()));
            out.printf("   %s: real gap %+.2f points, the rate %.6f -> %.6f after 120 months"
                    + " (trend %.6f, the gap's level reached %+.4f, the cover's %+.4f)%n", names[i], gap * 100, r0,
                    fx.getRate(), fx.getParityTrend(), fx.getGapReached(), fx.getCoverReached());
            close("after 120 months, " + names[i] + ": the rate is the trend and the three levels reached",
                    fx.getRate(), expected, 1e-9);
        }
        ForeignAccounts dear = fxs[0], cheap = fxs[1], neutral = fxs[2];
        assertTrue("paying over the world in real terms holds the rate under the neutral dial's", dear.getRate() < neutral.getRate());
        assertTrue("...and paying under it holds the rate over the neutral dial's", cheap.getRate() > neutral.getRate());
        close("at the world's real rate the rate is parity's trend times the cover's premium alone",
                neutral.getRate(), neutral.getParityTrend() * Math.exp(neutral.gate() * neutral.getCoverReached()), 1e-9);
        close("...and parity's trend is the parity it was handed, held for 120 months",
                neutral.getParityTrend(), neutral.getParity(), 1e-9);
        assertTrue("...so the inflation differential reaches the rate only through the parity it raises",
                neutral.inflationGap() > .1 && Math.abs(neutral.getGapReached()) < 1e-12);
    }

    /* ================= 2. inflation moves the rate with parity's trend, and no further ================= */
    static void spiral() {
        out.println("\n--- 2. ten months of inflation move the rate with parity's trend and no further ---");

        // RESTATED FOR XR1 (spec 3.2). The 0.7.2 premise - a spiral needs an
        // outflow, so with the real gap at nothing and no deficit the rate moves by
        // the parity pull alone - is retired with the pull: the rate follows parity's
        // trend one for one. What stays: with no gap, no deficit and nothing from the
        // vault or the market, the only thing that moves the rate is the trend.
        final double worldInflation = .02, cityInflation = .40;
        final double worldReal = DebtManager.WORLD_BASE_RATE - worldInflation;
        final double dial = cityInflation + worldReal;
        final double trade = 3_000, gdp = 6_000;

        ForeignAccounts fx = settled(trade, trade, gdp);
        // A vault at THIN_COVER or more, so no premium; bought before the month starts,
        // which startMonth() clears from the month's books - a founding's is not an intervention.
        fx.buyReserves(fx.monthlyImports() * 2 * ForeignAccounts.THIN_COVER);
        double level = 1.0, monthly = Math.pow(1 + cityInflation, 1 / 12.0);
        double r0 = fx.getRate(), worst = 0;
        boolean quiet_ = fx.importCover() >= ForeignAccounts.THIN_COVER;
        for (int m = 0; m < 10; m++) {
            fx.startMonth();
            fx.takeMonth(month(trade, trade, 0, 0), gdp);
            level *= monthly;
            fx.setParity(level, 1.0, cityInflation, worldInflation);
            fx.setRealRateDifferential((dial - cityInflation) - worldReal);
            quiet_ &= fx.balanceTarget() == 0 && fx.coverTarget() == 0 && fx.monthDeficitUsd() == 0;
            fx.repriceCurrency();
            worst = Math.max(worst, Math.abs(fx.getRate() - fx.getParityTrend()));
        }
        out.printf("   ten months of 40%% inflation: the rate %.6f from %.6f, parity %.6f, its trend %.6f%n",
                fx.getRate(), r0, fx.getParity(), fx.getParityTrend());
        assertTrue("fixture: ten months with no gap, no deficit and no premium", quiet_);
        close("each month the rate is parity's trend, and nothing else moved it", worst, 0, 1e-12);
        assertTrue("...and after ten months it is above where it began: it followed the inflation", fx.getRate() > r0);
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

    static void defence() {
        out.println("\n--- 3. the defence sells the vault's dollars, and what it sells lifts the D$ ---");

        // RESTATED FOR XR1 (spec 3.6's last point). The sale no longer damps a push by
        // its share of the deficit: it moves the rate through the intervention channel
        // at its real size, INTERVENTION_IMPACT x what it fetched over a year's GDP. The
        // amounts - what is sold, the vault, the local price, the record, the realised
        // share - are the old section's, and each fixture now asserts that the month
        // would have weakened the D$ (getLastWouldBeMove()), which is what the defence
        // leans against.

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
        double capacity = full.absorption();
        double cover = full.importCover();
        double r0 = full.getRate();
        assertTrue("fixture: the trailing deficit's level weakens the currency", full.balanceTarget() > 0);
        close("fixture: the month's own accounts show a US$2M deficit", full.monthDeficitUsd(), deficit, 1e-9);
        close("fixture: the vault is at six months' cover", cover, ForeignAccounts.COMFORTABLE_COVER, 1e-9);
        close("fixture: ...so its capacity is the most there is", capacity, ForeignAccounts.MAX_ABSORPTION, 1e-12);
        close("fixture: the treasury bought nothing this month", full.getBoughtThisMonth(), 0, 0);

        full.repriceCurrency();
        assertTrue("fixture: the month's move before the defence weakens the D$", full.getLastWouldBeMove() > 0);
        double sold = full.getDefenceUsd();
        out.printf("   a US$10,000k vault against a US$2,000k deficit: sold US$%,.2fk, met %.0f%% of it,"
                + " the rate %.6f -> %.6f, cover %.2f -> %.2f months%n",
                sold, full.getLastAbsorption() * 100, r0, full.getRate(), cover, full.importCover());
        close("the central bank sells absorption() x the deficit, to the cent", sold, capacity * deficit, 1e-9);
        close("...and the vault falls by exactly that", full.getReservesUsd(), vault - sold, 1e-9);
        close("...at the month's rate, which is what it fetches", full.getDefenceLocal(), sold * r0, 1e-9);
        close("...and comes off the treasury's intervention record, as a sale does",
                full.getLifetimeIntervention(), vault * ForeignAccounts.OPENING_RATE - sold * r0, 1e-9);
        close("the realised absorption is what the sale met of the deficit", full.getLastAbsorption(),
                sold / deficit, 1e-12);
        close("the sale lifted the D$ by INTERVENTION_IMPACT x what it fetched over a year's GDP: nothing was felt before, so the intervention's move is minus the lift",
                full.getMoveIntervention(), -lift(full), 1e-12);
        close("...and the move that reached the rate is the would-be move less the lift",
                Math.log(full.getRate() / r0), full.getLastWouldBeMove() - lift(full), 1e-12);
        assertTrue("...and cover fell", full.importCover() < cover);
        close("...to what is left, at the new rate", full.importCover(),
                (vault - sold) * full.getRate() / imports, 1e-9);
        close("the dollars sold since founding count it", full.getDefenceUsdLifetime(), sold, 1e-12);

        /* ---- the same month with nothing in the vault ---- */
        ForeignAccounts empty = defended(exports, imports, gdp, 0, capitalOut);
        double e0 = empty.getRate();
        empty.repriceCurrency();
        assertTrue("fixture: the month's move before the defence weakens the D$, with nothing to sell",
                empty.getLastWouldBeMove() > 0);
        close("an empty vault sells nothing", empty.getDefenceUsd(), 0, 0);
        close("...meets nothing of the deficit", empty.getLastAbsorption(), 0, 0);
        close("...lifts nothing: the intervention's move is nothing", empty.getMoveIntervention(), 0, 0);
        close("...and the whole of the month's would-be move reaches the rate",
                Math.log(empty.getRate() / e0), empty.getLastWouldBeMove(), 1e-12);

        /* ---- a surplus, and the currency rising ---- */
        ForeignAccounts surplus = defended(imports * 2, imports, gdp, vault, 0);
        double s0 = surplus.getRate();
        assertTrue("fixture: a month in surplus, the balance's level strengthens the currency",
                surplus.balanceTarget() < 0 && surplus.monthDeficitUsd() == 0);
        surplus.repriceCurrency();
        assertTrue("...and the surplus month's would-be move strengthens it", surplus.getLastWouldBeMove() < 0);
        close("a surplus month sells nothing", surplus.getDefenceUsd(), 0, 0);
        close("...and buys nothing: the vault is what it was", surplus.getReservesUsd(), vault, 0);
        close("...not by the treasury either", surplus.getBoughtThisMonth(), 0, 0);
        close("...and the rise reaches the rate in full", Math.log(surplus.getRate() / s0),
                surplus.getLastWouldBeMove(), 1e-12);
        assertTrue("...which rose", surplus.getRate() < s0);

        /* ---- a vault that cannot meet what its cover would sell ---- */
        // Small imports, so US$500k is ten months of them and the capacity is
        // the most there is; the month's deficit is capital leaving.
        final double smallImports = 50, smallExports = 40, smallVault = 500;
        ForeignAccounts shallow = defended(smallExports, smallImports, 400, smallVault,
                deficit - (smallImports - smallExports));
        double shallowCapacity = shallow.absorption();
        double h0 = shallow.getRate();
        assertTrue("fixture: the vault's capacity would sell more than it holds",
                shallow.balanceTarget() > 0 && shallowCapacity * shallow.monthDeficitUsd() > smallVault);
        shallow.repriceCurrency();
        assertTrue("fixture: the month's move before the defence weakens the D$", shallow.getLastWouldBeMove() > 0);
        out.printf("   a US$500k vault against the same deficit: capacity %.0f%%, sold US$%,.2fk, met %.0f%% of it%n",
                shallowCapacity * 100, shallow.getDefenceUsd(), shallow.getLastAbsorption() * 100);
        close("a US$500k vault against a US$2M deficit sells all of it", shallow.getDefenceUsd(), smallVault, 1e-9);
        close("...and is empty", shallow.getReservesUsd(), 0, 0);
        close("...and meets 500k / 2M of it", shallow.getLastAbsorption(), smallVault / deficit, 1e-12);
        assertTrue("...not absorption()", Math.abs(shallow.getLastAbsorption() - shallowCapacity) > .1);
        close("...and it lifted the D$ by what it fetched, over a year of the GDP: minus the lift",
                shallow.getMoveIntervention(), -lift(shallow), 1e-12);
        close("...so the move that reached the rate is the would-be move less the lift",
                Math.log(shallow.getRate() / h0), shallow.getLastWouldBeMove() - lift(shallow), 1e-12);
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
         * less the city's EXPECTED inflation since 0.7.42 (the year's until
         * then) - the reading the month opened on, which
         * is the one the reprice sees, the index being struck after it - less
         * the world's base rate less the world's realised inflation.
         */
        double worstWiring = 0;
        int defendedAt = 0, played = 0;
        double vaultBefore = 0, rateBefore = 0;
        Before was = null;
        final int most = 240;
        for (; played < most; played++) {
            // EX ANTE SINCE 0.7.42: the city's side is the dial less the
            // inflation it EXPECTS, as the month opened (Expectations; the
            // anchor is taken after the reprice) - it was the year's inflation.
            double cityInflation = city.getExpectations().getExpectedInflation();
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
            /*
             * THE SHOPS STAND FROM THE START (0.7.17): the index is a year of
             * the city's own prices, and a city with no shop has none. Ordered
             * beside two hundred houses they shared the builders with them
             * (BuildingManager, EVERY BUILDING GETS THE CREW IT CAN USE) and
             * opened later, and in month forty-one the index did not hold the
             * year this section restores into (it held fifteen months before;
             * measured again on the rule as shipped: no year, no 45%).
             */
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 4, true);
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
        out.println("\n--- 6. the rate goes where parity's trend takes it: the guards are a billion either way ---");

        // RESTATED FOR XR1 (spec 3.2). The old section drove the rate past the guards
        // with a push of a hundred points of real gap, which no longer exists: now the
        // rate goes where parity's trend goes. The fixture holds parity at 150 (the
        // city's prices 150 times the world's) from a currency whose trend has started
        // at 1, with a quiet market, for as many months as it takes the trend to arrive.
        final double usdOwed = 1_000, target = 150;
        final int most = 240;

        /* PAST A HUNDRED: the trend climbs, and the old guard would have held the rate at 100. */
        ForeignAccounts high = at(1);
        high.takeForeignDebt(usdOwed, 1);
        month(high, 1.0, 0);
        assertTrue("fixture: past settling, trade in balance, the vault empty, the whole economy trading, the market quiet",
                high.balanceTarget() == 0 && high.getReservesUsd() == 0 && high.getOpenness() == 1 && !high.isMarketSeeded());
        double before = high.getRate(), first = 0, worst = 0;
        int months = 0;
        while (high.getRate() <= OLD_MAX_RATE && months < most) {
            before = high.getRate();
            month(high, target, 0);
            high.takeForeignDebt(usdOwed, high.getRate());
            worst = Math.max(worst, Math.abs(high.getRate() - high.getFairLevel()) / high.getRate());
            if (++months == 1) first = high.getRate();
        }
        out.printf("   parity at %.0f: %.4f after one month, %.4f after %d (the old guard: %.0f)%n",
                target, first, high.getRate(), months, OLD_MAX_RATE);
        assertTrue("fixture: the trend arrived - the rate is past the old guard of 100", high.getRate() > OLD_MAX_RATE);
        assertTrue("...a rate under the old guard crosses it, never held by it", first < OLD_MAX_RATE);
        close("...every month the rate is the fundamentals' level, never the guard", worst, 0, 1e-9);
        close("...and the dollar debt is worth the dollars at it", high.getForeignDebt(), usdOwed * high.getRate(), 1e-6);
        close("...the month's revaluation the whole of that month's move", high.getLastRevaluation(),
                usdOwed * (high.getRate() - before), 1e-6);

        /* UNDER A HUNDREDTH, the mirror: parity at 1/200, the trend falling past the old floor. */
        final double low = .005;
        ForeignAccounts dear = at(1);
        dear.takeForeignDebt(usdOwed, 1);
        month(dear, 1.0, 0);
        before = dear.getRate();
        double firstDown = 0;
        months = 0;
        while (dear.getRate() >= OLD_MIN_RATE && months < most) {
            before = dear.getRate();
            month(dear, low, 0);
            dear.takeForeignDebt(usdOwed, dear.getRate());
            if (++months == 1) firstDown = dear.getRate();
        }
        out.printf("   parity at %.3f: %.4f after one month, %.6f after %d (the old guard: %.2f)%n",
                low, firstDown, dear.getRate(), months, OLD_MIN_RATE);
        assertTrue("fixture: the trend arrived - the rate is under the old floor of .01", dear.getRate() < OLD_MIN_RATE);
        assertTrue("...a rate over the old floor crosses it, never held by it", firstDown > OLD_MIN_RATE);
        close("...and the dollar debt is worth the dollars at it", dear.getForeignDebt(), usdOwed * dear.getRate(), 1e-9);
        close("...the month's revaluation the whole of that month's move, a gain", dear.getLastRevaluation(),
                usdOwed * (dear.getRate() - before), 1e-9);

        /*
         * A REFORM SCALES THE GUARDS WITH THE UNIT. Three zeros off a currency
         * whose trend is under way: the rate a thousandth, the guards a thousandth
         * of what they were, and every month after the reformed currency lands on a
         * thousandth of where the unreformed one does. Both are warmed at parity 1
         * first, so the trend has started in each before the reform.
         */
        ForeignAccounts reformed = at(1), twin = at(1);
        for (int m = 0; m < 3; m++) { month(reformed, 1.0, 0); month(twin, 1.0, 0); }
        reformed.redenominate(.001);
        close("three zeros off: the rate a thousandth", reformed.getRate(), twin.getRate() * .001, 1e-12);
        close("...the guard above it a thousandth of MAX_RATE", reformed.getMaxRate(),
                ForeignAccounts.MAX_RATE * .001, 1e-3);
        close("...and the one below a thousandth of MIN_RATE", reformed.getMinRate(),
                ForeignAccounts.MIN_RATE * .001, 1e-24);
        // The price level is an index and a reform does not move it; the
        // parity it anchors is a rate, and the reform moved that.
        double worstGap = 0;
        for (int m = 0; m < 30; m++) {
            month(reformed, target, 0, .001);
            month(twin, target, 0);
            worstGap = Math.max(worstGap, Math.abs(reformed.getRate() - twin.getRate() * .001));
        }
        close("...and parity's trend moves it as it moves the unreformed one, a thousandth the size, every month for thirty",
                worstGap, 0, 1e-12);
        ForeignAccounts seeded = new ForeignAccounts();
        seeded.seedConstants(1_000);
        close("a city founded in the new unit carries the guards in it", seeded.getMaxRate(),
                ForeignAccounts.MAX_RATE / 1_000, 1e-3);
    }
}
