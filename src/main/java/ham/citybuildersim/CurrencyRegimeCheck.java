package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Proves the D$'s four regimes - Float, Lean against falls (the default), Band
 * and Peg to the US$ - hold the currency where they say and cost what they
 * should (XR2, 2026-10-22): Lean is exactly the float the city had before there
 * were regimes, a peg holds to the digit while the vault meets the month's
 * balance and gives way at the floor or by doubt at the chance the gap makes
 * it, the doubt takes the hot money home, a band's edges hold and its inside is
 * free, the dial is tied to the US's rate under a peg and the autopilot's aim
 * picks the rule, the currency aim keeps the shadow inside a band, the strong
 * edge's dollars are paid exactly by the payer chosen, and all of it is saved.
 * Not part of the game.
 *
 * WHY THIS EXISTS. Jerus, 2026-10-21 (docs/studies/spec-exchange-rates.md, 3.6;
 * his calls star 3 to 6 and 13): the city chooses what its D$ is held to. Star 3,
 * four regimes, Lean the default and the city's own history; star 4, a peg
 * breaks - when the vault cannot pay the deficit (Krugman's first generation)
 * or by doubt (Obstfeld's second) - and the break costs the bank its credibility;
 * star 5, under a peg the dial is tied to the US's rate and only a spread is the
 * city's; star 6, the dollars the vault takes at the strong edge are paid for by
 * a toggle, the central bank in new money or the treasury out of its cash; star
 * 13, the autopilot aims at inflation, the currency, or a mix. The study's row
 * (section 5, XR2) asks for a harness that counts how often each of these fires
 * rather than waiting for a run to show it: this is that harness. The default
 * playtest never leaves Lean, so nothing else in the suite would ever notice a
 * regime that did nothing.
 *
 * What it has to prove, a section each, every one a fixture that causes the
 * condition rather than waiting for it:
 *
 *   0. LEAN IS TODAY'S FLOAT, TO THE CENT. Two cities on the default founding,
 *      one switched to Float and back to Lean before its first month: 60 months,
 *      every month's rate, vault, defence, moves, M0, cash and credibility equal
 *      to the bit; under Lean the shadow is the rate, the held move is exactly
 *      0, nothing is bought at an edge, no doubt, no hot money left on doubt,
 *      nothing owed. The months the lean sold are counted (the comparison means
 *      nothing if the defence never fired).
 *   1. FLOAT SELLS NOTHING. A vault and deficits that make Lean sell, and the
 *      Float twin on the same months: its defence is 0 every month, its vault
 *      unchanged.
 *   2. A PEG HOLDS TO THE DIGIT, AND THE VAULT PAYS THE MONTH'S BALANCE. A
 *      settled currency pegged away from its rate, then months of a deficit,
 *      then of a surplus: every held month the rate is the peg exactly; the
 *      vault falls by exactly the deficit, rises by exactly the surplus (the
 *      edge's purchase), and what is owed is that purchase at the month's rate;
 *      the seven moves add to the month's log move and the held line holds it.
 *   3. A VAULT AT THE FLOOR BREAKS THAT MONTH, AT THE SHADOW PLUS OVERSHOOT.
 *      A peg whose deficit would take the vault under VAULT_FLOOR months of
 *      imports breaks (cause VAULT, nothing sold), its twin with a vault a
 *      little deeper holds; the rate lands at the shadow plus OVERSHOOT of its
 *      gap, no overshoot when the shadow is the stronger; the overshoot fades at
 *      MARKET_HALF_LIFE; in a city credibility falls by PEG_BREAK_DENT of its
 *      distance to KMIN.
 *   4. THE CHANCE OF A BREAK, COUNTED OVER 1,000 SEEDED PEGS AGAINST hazard().
 *      The breaks counted are within three standard deviations of the hazards
 *      summed; the hazard's own shape; the market's path is the same pegged or
 *      not.
 *   5. THE DOUBT TAKES THE HOT MONEY HOME. A bare CapitalFlows: setDoubt(h)
 *      takes exactly h of the stock, setDoubt(0) changes nothing to the bit; in
 *      a city under a peg past BREAK_GAP the month's departure is the stock
 *      times the share the last reprice struck, and the audit closes. The saved
 *      array is 19 long (FX8): slot 15 the doubt's lifetime, 16 to 18 the month's
 *      arrived, departed and doubtDeparted, restored to the bit, and an array
 *      cut at 16 (as XR2 wrote it) restores with the month's three at 0.
 *   6. A BAND'S EDGES HOLD AND ITS INSIDE IS FREE. A band narrower than the
 *      market's swing, a few hundred months: past the weak edge the rate is the
 *      edge and the vault sold the deficit, past the strong edge it bought the
 *      surplus, inside the rate is the float's and nothing is dealt; the centre
 *      crawls at the slope; a vault under the floor lets the band give way.
 *   7. UNDER A PEG THE DIAL IS THE US'S RATE PLUS THE SPREAD; THE AIM. In a
 *      city: pegged, the policy rate is WORLD_BASE_RATE plus the spread, with
 *      the autopilot off or aiming at inflation; aiming at the currency it is
 *      the gap's; a band and the currency aim, a mix; Lean with any aim the
 *      inflation rule.
 *   8. THE CURRENCY AIM HOLDS A BAND, COUNTED OVER 1,000 SEEDED YEARS. A band
 *      with the dial neutral against the dial the currency aim sets: its share
 *      of months with the shadow inside is higher and its vault spends less.
 *   9. THE TOGGLE'S TWO PAYERS EACH PAY EXACTLY WHAT THE EDGE BOUGHT. Cities
 *      pegged with a large spread, a surplus at the peg: the central bank's
 *      pays in new money and the treasury's does not, the treasury's pays out of
 *      its cash and the central bank's does not, the audit closes in both, and
 *      the central bank's equity is unmoved by the purchase once settled. FX8:
 *      the central bank keeps nothing, restated with what it paid in new money
 *      for the vault's dollars, in both cities every month, and the old
 *      statement misses by exactly that term; in the settlement month the
 *      vault's record (getLifetimeIntervention()) rises by the whole bill,
 *      whoever paid it.
 *  10. SAVED AND LOADED MID-PEG. A pegged city with a purchase owed and a doubt
 *      share struck, saved and loaded: the next 12 months equal the twin that
 *      never saved; a bare currency restores every regime field; an array cut
 *      before the regimes restores as Lean with nothing owed.
 *  11. A REAL RUN, COUNTED. A city pegged at founding for 240 months: the months
 *      held, sold, bought, the doubt's departures and the break are counted and
 *      consistent, every figure finite and the audit closing every month; the
 *      central bank, the default payer, keeps nothing (restated, FX8) in every
 *      one of the 240 and has paid for dollars in new money.
 *  12. THE HOT MONEY'S MONTH CROSSES A SAVE (FX8). A pegged city in which hot
 *      money left on doubt this month, saved and loaded: arrived, departed and
 *      doubtDeparted, and the figure Policy draws from it, equal to the live
 *      city's to the bit, and one month on the two stay equal to the bit.
 *  13. LEAVING A PEG TAKES ITS DOUBT WITH IT (FX8). A bare peg with a doubt
 *      struck, left for Float, Lean or Band: no doubt; kept on a re-press of
 *      the peg; not revived by leaving and re-entering. In a city: twins from
 *      section 12's save, one leaving the peg between two presses, the month
 *      after the one that left loses no hot money on doubt and the one that
 *      stayed does, the audit closing in both.
 *
 * Numbering is the running order. Money is in thousands, as everywhere.
 *
 * @author Jerus
 */
public class CurrencyRegimeCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %,.12f != %,.12f%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
        }
    }

    /** Equal to the bit. */
    static void same(String label, double actual, double expected) {
        boolean ok = Double.compare(actual, expected) == 0;
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %.17g != %.17g%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
        }
    }

    /** Runs a piece of work with the game's narration sent nowhere. */
    static void quietly(Runnable work) {
        PrintStream was = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(was); }
    }

    static final ForeignAccounts.Regime FLOAT = ForeignAccounts.Regime.FLOAT, LEAN = ForeignAccounts.Regime.LEAN,
            BAND = ForeignAccounts.Regime.BAND, PEG = ForeignAccounts.Regime.PEG;

    /** The trade a quiet currency settles on, each way, and the output it is weighed against: openness one (ExchangeRateCheck's). */
    static final double TRADE = 3_000, GDP = 6_000;

    /** The market's seed in the sections that draw one. */
    static final long SEED = 20_261_022L;

    /** A vault no fixture of these can empty or come near the floor of, in dollars. */
    static final double DEEP = 1e9;

    /** What the harness's own sums are held to where they are exact arithmetic on dollars. */
    static final double DOLLAR = 1e-6;

    /**
     * A currency past settling on this trade, its vault at this many dollars (bought at the rate of the day, then forgotten as a
     * set-up and not a deal), its market seeded unless the seed is 0 (quiet), held in this regime (a peg is struck at the rate of
     * the day, a band centred on it).
     */
    static ForeignAccounts book(double exports, double imports, double vaultUsd, long seed, ForeignAccounts.Regime regime) {
        ForeignAccounts fx = CurrencyCheck.settled(exports, imports, GDP);
        if (vaultUsd > 0) fx.buyReserves(vaultUsd * fx.getRate());
        fx.forgetDealing();
        if (seed != 0) fx.setMarketSeed(seed);
        fx.setRegime(regime);
        return fx;
    }

    /** One month of this trade, repriced. */
    static void month(ForeignAccounts fx, double exports, double imports) {
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(exports, imports, 0, 0), GDP);
        fx.repriceCurrency();
    }

    /** The month's own balance in dollars at the rate it is struck at, a surplus positive: the model's two accounts over the rate. */
    static double balanceUsd(ForeignAccounts fx) {
        return (fx.currentAccount() + fx.financialAccount()) / fx.getRate();
    }

    /** A trading town on the default founding, as ExchangeRateCheck builds it (its own copy: that one is tied to its harness's streams). */
    static Game town(String label, Founding founding) {
        Game city = new Game(GameFiles.scratch(label), founding);
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(ForeignCheck.template(city, "House"), 500, false);
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 8, false);
            city.buildStack(ForeignCheck.template(city, "Small Grocery Store"), 3, false);
            city.buildStack(ForeignCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(ForeignCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Industrial Bakery"), 2, false);
        });
        return city;
    }

    /** Plays this many months of a town, narration sent nowhere. */
    static void play(Game g, int months) {
        for (int m = 0; m < months; m++) quietly(() -> g.simulateMonths(1));
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        long started = System.currentTimeMillis();

        leanIsTodaysFloat();
        floatSellsNothing();
        aPegHolds();
        aVaultAtTheFloorBreaks();
        theChanceOfABreak();
        theDoubtTakesTheHotMoneyHome();
        aBandsEdgesHold();
        underAPegTheDialIsTheUSes();
        theCurrencyAimHoldsABand();
        theTwoPayers();
        savedMidPeg();
        aRealRun();
        theHotMoneysMonthCrossesASave();
        leavingAPegTakesItsDoubt();

        out.printf("%n(%.0f s)%n", (System.currentTimeMillis() - started) / 1000.0);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ================= 0. Lean is today's float, to the cent ================= */

    /** One city month's figures: the rate, the vault, the defence, the seven moves, M0, the treasury's cash and the bank's credibility. */
    static double[] figures(Game g) {
        ForeignAccounts fx = g.getForeignAccounts();
        return new double[] { fx.getRate(), fx.getReservesUsd(), fx.getDefenceUsd(), fx.getMoveTrend(), fx.getMoveGap(),
                fx.getMoveBalance(), fx.getMoveCover(), fx.getMoveIntervention(), fx.getMoveMarket(), fx.getMoveHeld(),
                g.getCentralBank().m0(), g.getCash(), g.getExpectations().getCredibility() };
    }

    static boolean sameBits(double[] a, double[] b) {
        for (int k = 0; k < a.length; k++) if (Double.compare(a[k], b[k]) != 0) return false;
        return true;
    }

    /** Everything XR2 added reads nothing under Lean. */
    static boolean leanIsClean(Game g) {
        ForeignAccounts fx = g.getForeignAccounts();
        CapitalFlows hot = g.getCapitalFlows();
        CentralBank cb = g.getCentralBank();
        return fx.getRegime() == LEAN
                && Double.compare(fx.getShadowRate(), fx.getRate()) == 0
                && fx.getMoveHeld() == 0
                && !fx.isHeld() && !fx.brokeThisMonth() && fx.getBreaks() == 0
                && fx.getEdgeBoughtUsd() == 0 && fx.getEdgeBoughtLocal() == 0 && fx.getEdgeOwedLocal() == 0
                && fx.getEdgeBoughtUsdLifetime() == 0
                && fx.getDoubtShare() == 0 && fx.getLastHazard() == 0 && fx.getLastPegGap() == 0
                && hot.getDoubtDeparted() == 0 && hot.getDoubtDepartedLifetime() == 0
                && cb.getDollarsBought() == 0 && cb.getDollarsBoughtLifetime() == 0;
    }

    static void leanIsTodaysFloat() {
        out.println("--- 0. Lean is today's float, to the cent ---");

        Founding standard = Founding.defaults();
        Game plain = town("xr2-lean-plain", standard);
        Game twin = town("xr2-lean-twin", standard);
        quietly(() -> {
            twin.setCurrencyRegime(FLOAT);
            twin.setCurrencyRegime(LEAN);
        });
        assertTrue("fixture: both cities are on Lean, the twin having been Float and back before its first month",
                plain.getForeignAccounts().getRegime() == LEAN && twin.getForeignAccounts().getRegime() == LEAN
                        && plain.getMonth() == twin.getMonth());
        int apart = 0, unclean = 0, sold = 0, firstApart = 0;
        for (int m = 1; m <= 60; m++) {
            quietly(() -> {
                plain.simulateMonths(1);
                twin.simulateMonths(1);
            });
            if (!sameBits(figures(plain), figures(twin))) { apart++; if (firstApart == 0) firstApart = m; }
            if (!leanIsClean(plain)) unclean++;
            if (!leanIsClean(twin)) unclean++;
            if (plain.getForeignAccounts().getDefenceUsd() > 0) sold++;
        }
        out.printf("   60 months on the default founding: the lean sold in %d of them (US$%,.0fk in all); the rate ends %.6f%n", sold,
                plain.getForeignAccounts().getDefenceUsdLifetime(), plain.getForeignAccounts().getRate());
        assertTrue("fixture: the lean sold in some month, or the comparison means nothing (" + sold + " of 60)", sold > 0);
        assertTrue("the Float-and-back city plays the plain one's 60 months to the bit: rate, vault, defence, seven moves, M0, cash,"
                + " credibility" + (apart == 0 ? "" : " (first apart: month " + firstApart + ")"), apart == 0);
        assertTrue("under Lean every month: the shadow is the rate, the held move is exactly 0, nothing bought at an edge, no doubt,"
                + " no hot money left on doubt, nothing owed", unclean == 0);
    }

    /* ================= 1. Float sells nothing ================= */

    /** A month of a city that runs a deficit most months and whose parity drifts weaker: the weather that makes the lean sell. */
    static void deficitMonth(ForeignAccounts fx, int m) {
        double exports = TRADE * (.82 + .1 * Math.sin(m / 7.0)), imports = TRADE * (1 + .1 * Math.cos(m / 11.0));
        double level = Math.exp(.003 * m + .08 * Math.sin(m / 30.0));
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(exports, imports, 0, 0), GDP);
        fx.setParity(level, 1.0, .036, .02);
        fx.repriceCurrency();
    }

    static void floatSellsNothing() {
        out.println("\n--- 1. Float sells nothing ---");

        ForeignAccounts lean = book(.8 * TRADE, TRADE, 20 * TRADE, SEED, LEAN);
        ForeignAccounts flt = book(.8 * TRADE, TRADE, 20 * TRADE, SEED, FLOAT);
        double vault = flt.getReservesUsd();
        assertTrue("fixture: the two have the same vault, and are on Lean and on Float",
                Double.compare(lean.getReservesUsd(), vault) == 0 && lean.getRegime() == LEAN && flt.getRegime() == FLOAT);
        int leanSold = 0, floatSold = 0, vaultMoved = 0;
        for (int m = 0; m < 120; m++) {
            deficitMonth(lean, m);
            deficitMonth(flt, m);
            if (lean.getDefenceUsd() > 0) leanSold++;
            if (flt.getDefenceUsd() != 0) floatSold++;
            if (Double.compare(flt.getReservesUsd(), vault) != 0) vaultMoved++;
        }
        out.printf("   120 months of deficits and a parity drifting weaker: the Lean twin sold in %d months (US$%,.0fk of its US$%,.0fk vault);"
                + " the Float twin in %d%n", leanSold, lean.getDefenceUsdLifetime(), vault, floatSold);
        assertTrue("fixture: the Lean twin sells in many of the months (" + leanSold + ")", leanSold > 20);
        assertTrue("fixture: and its vault is the lower for it", lean.getReservesUsd() < vault);
        assertTrue("Float's getDefenceUsd() is 0 every month", floatSold == 0);
        assertTrue("...its vault is unchanged every month, to the bit", vaultMoved == 0);
        close("...and nothing sold since founding", flt.getDefenceUsdLifetime(), 0, 0);
        close("...nor leaned on: the share of the deficit the vault met", flt.getLastAbsorption(), 0, 0);
    }

    /* ================= 2. a peg holds to the digit, and the vault pays the month's balance ================= */

    static void aPegHolds() {
        out.println("\n--- 2. a peg holds to the digit, and the vault pays the month's balance ---");

        final double vault0 = 40_000;
        ForeignAccounts fx = book(TRADE, TRADE, vault0, 0, PEG);
        fx.repeg(1.6);
        month(fx, TRADE, TRADE);   // the rate arrives at the peg; trade balanced, so nothing is dealt
        same("fixture: a settled currency pegged away from its rate arrives at the peg", fx.getRate(), 1.6);
        assertTrue("fixture: balanced trade dealt nothing and the vault is where it was",
                Double.compare(fx.getReservesUsd(), vault0) == 0 && fx.getDefenceUsd() == 0 && fx.getEdgeBoughtUsd() == 0);

        // Twelve months of a deficit.
        int heldMonths = 0, deficitsMet = 0, holdMoved = 0, hazardRight = 0;
        double worstVault = 0, worstSold = 0, worstMoves = 0, worstHold = 0, worstGap = 0, deficitSeen = 0;
        for (int i = 0; i < 12; i++) {
            double before = fx.getReservesUsd(), rateBefore = fx.getRate();
            fx.startMonth();
            fx.takeMonth(CurrencyCheck.month(.8 * TRADE, TRADE, 0, 0), GDP);
            double deficit = fx.monthDeficitUsd();   // the model's own figure: the month's net outflow in dollars at the rate it is struck at
            fx.repriceCurrency();
            if (fx.getRegime() == PEG && Double.compare(fx.getRate(), fx.getPegRate()) == 0) heldMonths++;
            if (deficit > 0) deficitsMet++;
            deficitSeen = deficit;
            worstVault = Math.max(worstVault, Math.abs((before - fx.getReservesUsd()) - deficit));
            worstSold = Math.max(worstSold, Math.abs(fx.getDefenceUsd() - deficit));
            double six = fx.getMoveTrend() + fx.getMoveGap() + fx.getMoveBalance() + fx.getMoveCover() + fx.getMoveIntervention() + fx.getMoveMarket();
            worstMoves = Math.max(worstMoves, Math.abs(fx.getLastMove() - Math.log(fx.getRate() / rateBefore)));
            worstHold = Math.max(worstHold, Math.abs(six + fx.getMoveHeld()));   // the rate did not move: the hold is minus the shadow's move
            if (Math.abs(fx.getMoveHeld()) > 1e-6) holdMoved++;
            worstGap = Math.max(worstGap, Math.abs(fx.getLastPegGap() - Math.log(fx.getShadowRate() / fx.getPegRate())));
            if (Double.compare(fx.getLastHazard(), ForeignAccounts.hazard(Math.max(0, fx.getLastPegGap()))) == 0) hazardRight++;
        }
        close("fixture: the month's deficit is 20% of imports at the pegged rate, in dollars", deficitSeen, .2 * TRADE / 1.6, DOLLAR);
        assertTrue("a deficit, twelve months: every one the rate is getPegRate() exactly and the regime is the peg", heldMonths == 12);
        assertTrue("...the vault met the deficit every month (" + deficitsMet + " of 12)", deficitsMet == 12);
        out.printf("   the vault: US$%,.2fk to US$%,.2fk; the worst gap from the month's deficit %.2e%n", vault0, fx.getReservesUsd(), worstVault);
        close("...the vault's dollars fell by exactly the month's deficit, each month, to a millionth of a dollar", worstVault, 0, DOLLAR);
        close("...and the defence sold exactly that", worstSold, 0, DOLLAR);
        close("...the seven moves add to the month's log move in the rate, each month", worstMoves, 0, 1e-12);
        close("...the six forces and the hold add to nothing: the held line is what holds the rate where the six would take it", worstHold, 0, 1e-12);
        assertTrue("...and the hold did something: the held move was not nothing in " + holdMoved + " of 12 months", holdMoved > 6);
        close("...the gap from the peg is the log of the shadow over it", worstGap, 0, 1e-12);
        assertTrue("...and the month's chance of a break is hazard() of that gap, each month", hazardRight == 12);

        // Twelve months of a surplus. The owed is taken every second month, so it adds up between.
        int surplusMonths = 0, ownedRight = 0, heldAgain = 0, payerRight = 0, addsUp = 0;
        double worstRise = 0, worstBought = 0, worstOwed = 0, carried = 0, surplusSeen = 0;
        for (int i = 0; i < 12; i++) {
            double before = fx.getReservesUsd(), rateBefore = fx.getRate();
            fx.startMonth();
            fx.takeMonth(CurrencyCheck.month(1.2 * TRADE, TRADE, 0, 0), GDP);
            double surplus = balanceUsd(fx);
            fx.repriceCurrency();
            if (surplus > 0) surplusMonths++;
            surplusSeen = surplus;
            if (fx.getRegime() == PEG && Double.compare(fx.getRate(), fx.getPegRate()) == 0) heldAgain++;
            worstRise = Math.max(worstRise, Math.abs((fx.getReservesUsd() - before) - surplus));
            worstBought = Math.max(worstBought, Math.abs(fx.getEdgeBoughtUsd() - surplus));
            worstOwed = Math.max(worstOwed, Math.abs(fx.getEdgeBoughtLocal() - surplus * rateBefore));
            if (fx.getDefenceUsd() == 0) ownedRight++;
            if (fx.getEdgeOwedPayer() == ForeignAccounts.EdgePayer.CENTRAL_BANK) payerRight++;
            double owed = fx.getEdgeOwedLocal();
            if (Math.abs(owed - (carried + surplus * rateBefore)) <= 1e-6) addsUp++;
            carried = 0;
            if (i % 2 == 1) {
                double taken = fx.takeEdgeOwed();
                if (Math.abs(taken - owed) > 1e-9 || fx.getEdgeOwedLocal() != 0) addsUp--;
            } else {
                carried = owed;
            }
        }
        close("fixture: the month's surplus is 20% of imports at the pegged rate, in dollars", surplusSeen, .2 * TRADE / 1.6, DOLLAR);
        assertTrue("a surplus, twelve months: the peg holds every one and the vault sold nothing", heldAgain == 12 && ownedRight == 12 && surplusMonths == 12);
        close("...the vault's dollars rose by exactly the surplus (the edge's purchase), each month, to a millionth of a dollar", worstRise, 0, DOLLAR);
        close("...getEdgeBoughtUsd() is that purchase", worstBought, 0, DOLLAR);
        close("...and what is owed for it is the purchase at the month's rate", worstOwed, 0, 1e-6);
        assertTrue("...owed by the central bank, as it is unless the player says otherwise", payerRight == 12);
        assertTrue("...what is owed adds up until it is taken, and taking it clears it (" + addsUp + " of 12 months)", addsUp == 12);
        close("...a surplus is no pressure to break: the hazard is BREAK_BASE", fx.getLastHazard(), ForeignAccounts.BREAK_BASE, 1e-15);
    }

    /* ================= 3. a vault at the floor breaks that month ================= */

    /** A quiet peg at the rate of the day, a few balanced months old, its vault at this many dollars. */
    static ForeignAccounts quietPeg(double vaultUsd) {
        ForeignAccounts fx = book(.8 * TRADE, TRADE, vaultUsd, 0, PEG);
        for (int i = 0; i < 6; i++) month(fx, TRADE, TRADE);
        return fx;
    }

    /** Two currencies, one on a peg and one floating, the same seed, trade in balance and a parity drifting weaker 5% a year: the pair a peg breaks beside. */
    static void driftMonth(ForeignAccounts fx, int m) {
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
        fx.setParity(Math.exp(.004 * (m + 1)), 1.0, .048, 0);
        fx.repriceCurrency();
    }

    /** A city on the default founding pegged with a surplus at the peg and the D$ a fifth over it: the doubt fixture, five months from its break at the peg's chance. */
    static Game doubtTown(String label, long reseed) {
        Game g = town(label, Founding.defaults());
        play(g, 36);
        quietly(() -> {
            g.setCurrencyRegime(PEG);
            g.getDebtManager().setPegSpread(.30);
        });
        play(g, 8);
        if (reseed != 0) g.getForeignAccounts().setMarketSeed(reseed);
        quietly(() -> g.repeg(g.getForeignAccounts().getShadowRate() * Math.exp(-.20)));
        return g;
    }

    static void aVaultAtTheFloorBreaks() {
        out.println("\n--- 3. a vault at the floor breaks that month, at the shadow plus overshoot ---");

        // The month's deficit and the floor, as the model reads them, from a probe with a vault too deep to matter.
        ForeignAccounts probe = quietPeg(DEEP);
        probe.startMonth();
        probe.takeMonth(CurrencyCheck.month(.8 * TRADE, TRADE, 0, 0), GDP);
        double deficit = probe.monthDeficitUsd(), floor = probe.vaultFloorUsd();
        out.printf("   the month's deficit US$%,.2fk, the vault's floor (VAULT_FLOOR = %.0f months of imports) US$%,.2fk%n", deficit, ForeignAccounts.VAULT_FLOOR, floor);
        assertTrue("fixture: there is a deficit and a floor", deficit > 0 && floor > 0);

        ForeignAccounts shortOfIt = quietPeg(floor + deficit - 1);
        ForeignAccounts deeper = quietPeg(floor + deficit + 1);
        assertTrue("fixture: both are pegged, quiet, and the first would be a dollar under its floor after the deficit, the second a dollar over",
                shortOfIt.getRegime() == PEG && deeper.getRegime() == PEG && !shortOfIt.isMarketSeeded() && !deeper.isMarketSeeded()
                        && Math.abs(shortOfIt.getReservesUsd() - (floor + deficit - 1)) <= DOLLAR);

        double vaultBefore = shortOfIt.getReservesUsd(), rateBefore = shortOfIt.getRate(), shadowBefore = shortOfIt.getShadowRate();
        month(shortOfIt, .8 * TRADE, TRADE);
        month(deeper, .8 * TRADE, TRADE);

        assertTrue("the peg whose vault the deficit would take under its floor breaks that month", shortOfIt.brokeThisMonth() && shortOfIt.getBreaks() == 1);
        assertTrue("...by the vault, not by doubt", shortOfIt.getLastBreakCause() == ForeignAccounts.BreakCause.VAULT);
        assertTrue("...the regime is Float after it", shortOfIt.getRegime() == FLOAT);
        assertTrue("...nothing was sold that month, and the vault is where it was",
                shortOfIt.getDefenceUsd() == 0 && Double.compare(shortOfIt.getReservesUsd(), vaultBefore) == 0);
        double shadow = shortOfIt.getLastBreakShadow();
        double gap = Math.log(shadow / shortOfIt.getPegRate());
        assertTrue("fixture: the shadow was the weaker side of the peg (" + String.format("%.4f against %.4f", shadow, shortOfIt.getPegRate()) + "), so the overshoot has something to be",
                gap > .01);
        double landing = shadow * Math.exp(ForeignAccounts.OVERSHOOT * Math.max(0, gap));
        close("...the rate it lands at is the shadow times exp(OVERSHOOT x the gap): " + String.format("%.6f", shortOfIt.getRate()),
                shortOfIt.getRate(), landing, 1e-12 * landing);
        close("...the jump it records is the log move of the rate that month", shortOfIt.getLastBreakJump(), Math.log(shortOfIt.getRate() / rateBefore), 1e-12);
        close("...and the hold gave way: the held move is last month's gap of the shadow over the rate", shortOfIt.getMoveHeld(),
                Math.log(shadowBefore / rateBefore), 1e-12);
        close("...and the seven moves add to the jump", shortOfIt.getLastMove(), shortOfIt.getLastBreakJump(), 1e-12);
        assertTrue("its twin, a vault a dollar deeper, holds: still a peg, the rate at the peg, nothing broken",
                deeper.getRegime() == PEG && !deeper.brokeThisMonth() && deeper.getBreaks() == 0 && Double.compare(deeper.getRate(), deeper.getPegRate()) == 0);
        close("...and its vault met the deficit, to the dollar", deeper.getDefenceUsd(), deficit, DOLLAR);
        close("...leaving it a dollar over its floor", deeper.getReservesUsd() - deeper.vaultFloorUsd(), 1, 1e-6);

        // The other side of the peg: a gap the other way lands at the shadow, no overshoot. A surplus and a parity falling by more than
        // half in 40 months put the shadow far under the peg for the rest of the run, where the chance of a break is BREAK_BASE a month.
        ForeignAccounts stronger = null;
        double strongerBefore = 0, strongerRate = 0, strongerSurplus = 0;
        int seedsTried = 0, monthsPlayed = 0;
        for (long s = 1; s <= 600 && stronger == null; s++) {
            seedsTried++;
            ForeignAccounts fx = book(1.2 * TRADE, TRADE, DEEP, SEED + s, PEG);
            for (int m = 0; m < 300; m++) {
                double v = fx.getReservesUsd(), r = fx.getRate();
                fx.startMonth();
                fx.takeMonth(CurrencyCheck.month(1.2 * TRADE, TRADE, 0, 0), GDP);
                double surplus = balanceUsd(fx);
                fx.setParity(Math.exp(-.8 * Math.min(1, (m + 1) / 40.0)), 1.0, -.2, 0);
                fx.repriceCurrency();
                monthsPlayed++;
                if (fx.brokeThisMonth()) { stronger = fx; strongerBefore = v; strongerRate = r; strongerSurplus = surplus; break; }
            }
        }
        out.printf("   a peg with a surplus and a parity falling away: the first chance break in %d seeds, %,d pegged months (BREAK_BASE a month: about one in %,.0f)%n",
                seedsTried, monthsPlayed, 1 / ForeignAccounts.BREAK_BASE);
        assertTrue("fixture: a peg broke by chance with the shadow stronger than the peg", stronger != null
                && stronger.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT && stronger.getLastBreakShadow() < stronger.getPegRate());
        if (stronger != null) {
            close("the rate lands at the shadow itself: no overshoot on a gap the other way", stronger.getRate(), stronger.getLastBreakShadow(),
                    1e-12 * stronger.getRate());
            assertTrue("fixture: the month had a surplus, which a held peg would have bought (US$" + String.format("%,.0fk", strongerSurplus) + ")", strongerSurplus > 0);
            assertTrue("...Float after it, and nothing bought or sold that month (the vault is where it was)",
                    stronger.getRegime() == FLOAT && stronger.getDefenceUsd() == 0 && stronger.getEdgeBoughtUsd() == 0
                            && Double.compare(stronger.getReservesUsd(), strongerBefore) == 0);
            close("...the D$ jumped stronger, by the log move recorded", stronger.getLastBreakJump(), Math.log(stronger.getRate() / strongerRate), 1e-12);
            assertTrue("...and it is a jump the right way: the break let the D$ go stronger", stronger.getLastBreakJump() < 0);
        }

        // The overshoot fades as the market's swings do: against a floating twin on the same months, by half in MARKET_HALF_LIFE.
        ForeignAccounts peg = null, flt = null;
        int breakMonth = 0, marketApart = 0;
        double shadowAtBreak = 0;
        for (long s = 1; s <= 200 && peg == null; s++) {
            ForeignAccounts p = book(TRADE, TRADE, DEEP, SEED + s, PEG), f = book(TRADE, TRADE, DEEP, SEED + s, FLOAT);
            int apart = 0;
            for (int m = 0; m < 400; m++) {
                driftMonth(p, m);
                driftMonth(f, m);
                if (Double.compare(p.getMarket(), f.getMarket()) != 0) apart++;
                if (p.brokeThisMonth()) {
                    peg = p;
                    flt = f;
                    breakMonth = m;
                    marketApart = apart;
                    shadowAtBreak = p.getLastBreakShadow();
                    break;
                }
            }
        }
        assertTrue("fixture: a peg broke by doubt on the weaker side beside its floating twin", peg != null
                && peg.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT && peg.getLastBreakShadow() > peg.getPegRate());
        if (peg != null) {
            double gapAtBreak = Math.log(shadowAtBreak / peg.getPegRate());
            double excess0 = Math.log(peg.getRate() / flt.getRate());
            out.printf("   a peg beside a floating twin broke in month %d at a gap of %.1f%%: the rate landed %.1f%% over its twin's%n", breakMonth + 1,
                    gapAtBreak * 100, (Math.exp(excess0) - 1) * 100);
            close("the floating twin's rate is the shadow the peg broke from", flt.getRate(), shadowAtBreak, 1e-12 * shadowAtBreak);
            close("...so the break left the peg's rate over it by OVERSHOOT of the gap", excess0, ForeignAccounts.OVERSHOOT * gapAtBreak, 1e-12);
            assertTrue("fixture: the gap was past BREAK_GAP, as a doubt break must be", gapAtBreak > ForeignAccounts.BREAK_GAP);
            int half = (int) ForeignAccounts.MARKET_HALF_LIFE;
            assertTrue("fixture: MARKET_HALF_LIFE is a whole number of months", half == ForeignAccounts.MARKET_HALF_LIFE);
            for (int m = breakMonth + 1; m <= breakMonth + half; m++) { driftMonth(peg, m); driftMonth(flt, m); }
            close("MARKET_HALF_LIFE months on, half the overshoot is gone", Math.log(peg.getRate() / flt.getRate()), excess0 / 2, 1e-9);
            for (int m = breakMonth + half + 1; m <= breakMonth + 2 * half; m++) { driftMonth(peg, m); driftMonth(flt, m); }
            close("...and another MARKET_HALF_LIFE on, a quarter of it is left", Math.log(peg.getRate() / flt.getRate()), excess0 / 4, 1e-9);
            assertTrue("...and the peg's market path is the floating twin's, every month of it (" + marketApart + " months apart before the break)", marketApart == 0
                    && Double.compare(peg.getMarket(), flt.getMarket()) == 0 && peg.getMarketDraws() == flt.getMarketDraws());
        }

        // In a city, credibility falls by PEG_BREAK_DENT of its distance to KMIN, against a twin where nothing broke.
        Game a = doubtTown("xr2-credibility-a", 0);
        int k = 0;
        while (k < 40 && !a.getForeignAccounts().brokeThisMonth()) {
            k++;
            quietly(() -> a.simulateMonths(1));
        }
        assertTrue("fixture: the doubt city's peg broke within " + k + " months of the revaluation", a.getForeignAccounts().brokeThisMonth());
        if (a.getForeignAccounts().brokeThisMonth()) {
            double afterA = a.getExpectations().getCredibility();
            Game b = doubtTown("xr2-credibility-b", 0);
            play(b, k - 1);
            double c0 = b.getExpectations().getCredibility();
            // The twin's peg is moved to a rate so weak the shadow sits far under it, so that month nothing breaks by chance.
            quietly(() -> b.repeg(b.getForeignAccounts().getShadowRate() * 1.5));
            play(b, 1);
            double afterB = b.getExpectations().getCredibility();
            assertTrue("fixture: the twin's peg did not break that month, and the city's did", !b.getForeignAccounts().brokeThisMonth());
            assertTrue("fixture: the twin's credibility climbed that month (the bank's gain branch, miss within tolerance)",
                    Math.abs(afterB - (c0 + (Expectations.KMAX - c0) / Expectations.GAIN_MONTHS)) <= 1e-12);
            // The month's gain is linear in credibility, so the dent shows in the difference once the gain's pull is divided out.
            double dent = (afterB - afterA) / (1 - 1.0 / Expectations.GAIN_MONTHS);
            out.printf("   credibility %.4f before the break month: %.4f in the twin that held, %.4f in the city that broke%n", c0, afterB, afterA);
            close("the month a peg broke, credibility fell by PEG_BREAK_DENT of its distance to KMIN", dent,
                    Expectations.PEG_BREAK_DENT * (c0 - Expectations.KMIN), 1e-9);
        }
        Expectations bare = new Expectations();
        double was = bare.getCredibility();
        bare.pegBroke();
        close("a bare Expectations: pegBroke() takes PEG_BREAK_DENT of the distance to KMIN", was - bare.getCredibility(),
                Expectations.PEG_BREAK_DENT * (was - Expectations.KMIN), 1e-12);
        for (int i = 0; i < 80; i++) bare.pegBroke();
        assertTrue("...and never takes it under KMIN, however many pegs break", bare.getCredibility() >= Expectations.KMIN
                && bare.getCredibility() <= Expectations.KMIN + 1e-9);
    }

    /* ================= 4. the chance of a break, counted over 1,000 seeded pegs ================= */

    static void theChanceOfABreak() {
        out.println("\n--- 4. the chance of a break, counted over 1,000 seeded pegs against hazard() ---");

        close("hazard(0) is BREAK_BASE", ForeignAccounts.hazard(0), ForeignAccounts.BREAK_BASE, 1e-15);
        close("hazard(BREAK_GAP) is BREAK_BASE: doubt adds nothing under the first BREAK_GAP", ForeignAccounts.hazard(ForeignAccounts.BREAK_GAP),
                ForeignAccounts.BREAK_BASE, 1e-15);
        close("hazard(2 x BREAK_GAP) is BREAK_BASE + BREAK_SLOPE", ForeignAccounts.hazard(2 * ForeignAccounts.BREAK_GAP),
                ForeignAccounts.BREAK_BASE + ForeignAccounts.BREAK_SLOPE, 1e-12);

        final int pegs = 1_000, horizon = 600;
        final double drift = .02;   // the study's prototype: the D$ two points over the world's inflation
        // ...counted twice over the same pegs: to the end of the run, where nearly every peg has broken and the count can hardly be anything
        // else, and in the first eight years, where about half have, which is the window that can tell a hazard that is out by a third.
        final int window = 96;
        double sumH = 0, sumVar = 0, winH = 0, winVar = 0;
        int breaks = 0, atRisk = 0, byDoubt = 0, byVault = 0, winBreaks = 0;
        int[] lives = new int[pegs];
        List<Double> jumps = new ArrayList<>();
        for (int k = 0; k < pegs; k++) {
            ForeignAccounts fx = book(TRADE, TRADE, DEEP, SEED + 7L * k + 1, PEG);
            lives[k] = Integer.MAX_VALUE;
            for (int m = 0; m < horizon; m++) {
                fx.startMonth();
                fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
                fx.setParity(Math.exp(drift / 12 * (m + 1)), 1.0, drift, 0);
                fx.repriceCurrency();
                double h = fx.getLastHazard();
                sumH += h;
                sumVar += h * (1 - h);
                atRisk++;
                if (m < window) {
                    winH += h;
                    winVar += h * (1 - h);
                    if (fx.brokeThisMonth()) winBreaks++;
                }
                if (fx.brokeThisMonth()) {
                    breaks++;
                    lives[k] = m + 1;
                    jumps.add(fx.getLastBreakJump());
                    if (fx.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT) byDoubt++; else byVault++;
                    break;
                }
            }
        }
        double sd = Math.sqrt(sumVar), z = sd > 0 ? (breaks - sumH) / sd : 0;
        Arrays.sort(lives);
        java.util.Collections.sort(jumps);
        double medianLife = lives[pegs / 2] == Integer.MAX_VALUE ? Double.NaN : lives[pegs / 2] / 12.0;
        double medianJump = jumps.isEmpty() ? Double.NaN : Math.exp(jumps.get(jumps.size() / 2)) - 1;
        out.printf("   %,d seeded pegs, the D$'s parity %.0f points over the world's, a vault too deep to reach its floor, up to %d months each (%,d pegged months):%n",
                pegs, drift * 100, horizon, atRisk);
        out.printf("   %d broke (%d by doubt, %d by the vault); the hazards summed to %.1f, a standard deviation of %.2f: %+.2f deviations%n", breaks, byDoubt, byVault,
                sumH, sd, z);
        out.printf("   median life %s years, median break %s, a log jump of %s (the study's prototype, section 3.6, at 2 points over: 7.4 years and 31%%)%n",
                Double.isNaN(medianLife) ? "over " + horizon / 12 : String.format("%.1f", medianLife),
                Double.isNaN(medianJump) ? "none" : String.format("%.0f%%", medianJump * 100),
                jumps.isEmpty() ? "none" : String.format("%.3f", jumps.get(jumps.size() / 2)));
        double winSd = Math.sqrt(winVar);
        out.printf("   in the first %d months: %d broke, the hazards summed to %.1f, a standard deviation of %.2f: %+.2f deviations%n", window, winBreaks, winH, winSd,
                winSd > 0 ? (winBreaks - winH) / winSd : 0);
        assertTrue("fixture: enough pegs broke for the count to mean something (" + breaks + " of " + pegs + ")", breaks >= 100 && sumH >= 100);
        assertTrue("fixture: in the first " + window + " months neither nearly all nor nearly none had broken (" + winBreaks + " of " + pegs + ")",
                winBreaks > pegs / 5 && winBreaks < 4 * pegs / 5);
        assertTrue("no peg broke by the vault: the vault was too deep to reach its floor", byVault == 0);
        assertTrue("the breaks counted are within 3 standard deviations of the hazards summed (" + breaks + " against " + String.format("%.1f +- %.1f", sumH, sd) + ")",
                Math.abs(breaks - sumH) <= 3 * sd);
        assertTrue("...and in the first " + window + " months, where it can be told (" + winBreaks + " against " + String.format("%.1f +- %.1f", winH, winSd) + ")",
                Math.abs(winBreaks - winH) <= 3 * winSd);

        // The market's path is the same pegged or not: one seed, a pegged twin and a floating one (also checked in 3 beside the break).
        ForeignAccounts pegged = book(TRADE, TRADE, DEEP, SEED, PEG), floating = book(TRADE, TRADE, DEEP, SEED, FLOAT);
        int marketApart = 0, drawsApart = 0, pegMonths = 0, floatMonths = 0;
        for (int m = 0; m < 240; m++) {
            driftMonth(pegged, m);
            driftMonth(floating, m);
            if (Double.compare(pegged.getMarket(), floating.getMarket()) != 0) marketApart++;
            if (pegged.getMarketDraws() != floating.getMarketDraws()) drawsApart++;
            if (pegged.getRegime() == PEG) pegMonths++; else floatMonths++;
        }
        out.printf("   one seed, 240 months: the pegged twin was pegged for %d of them and floating for %d%n", pegMonths, floatMonths);
        assertTrue("fixture: the pegged twin was pegged for some months and then broke, so the comparison spans the break", pegMonths > 12 && floatMonths > 12);
        assertTrue("the market's path is the same pegged or not: getMarket() equal to the bit in every month", marketApart == 0);
        assertTrue("...and it has taken the same draws: the peg's chance is a stream of its own", drawsApart == 0);
    }

    /* ================= 5. the doubt takes the hot money home ================= */

    /** One month of hot money at a 9% rate against the world's 2% and a point of risk: the standing fixture of CapitalFlowCheck. */
    static void hotMonth(CapitalFlows f, int m) {
        f.setMonth(m);
        f.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, m);
    }

    static CapitalFlows stocked(int months) {
        CapitalFlows f = new CapitalFlows();
        for (int m = 1; m <= months; m++) hotMonth(f, m);
        return f;
    }

    static void theDoubtTakesTheHotMoneyHome() {
        out.println("\n--- 5. the doubt takes the hot money home ---");

        final double share = .07;
        CapitalFlows doubting = stocked(60), calm = stocked(60);
        assertTrue("fixture: the two have the same stock, and it is money (" + String.format("%,.0f", doubting.getStock()) + ")",
                Double.compare(doubting.getStock(), calm.getStock()) == 0 && doubting.getStock() > 1_000);
        doubting.setDoubt(share);
        hotMonth(doubting, 61);
        hotMonth(calm, 61);
        double afterItsOwnMove = calm.getStock();
        close("setDoubt(h) takes exactly h of the stock, after its own move: getDoubtDeparted()", doubting.getDoubtDeparted(), share * afterItsOwnMove,
                1e-9 * afterItsOwnMove);
        close("...what is left is the rest", doubting.getStock(), (1 - share) * afterItsOwnMove, 1e-9 * afterItsOwnMove);
        close("...it is inside what departed: the twin's departure and the doubt's", doubting.getDeparted(), calm.getDeparted() + doubting.getDoubtDeparted(), 1e-9);
        same("...what arrived is untouched", doubting.getArrived(), calm.getArrived());
        close("...and the twin that doubted nothing left nothing on doubt", calm.getDoubtDeparted(), 0, 0);
        close("...after the one month the lifetime of it is that month's, the same figure", doubting.getDoubtDepartedLifetime(), doubting.getDoubtDeparted(), 1e-12);
        assertTrue("...the saved array carries the lifetime at slot 15 (XR2's) and restores it", doubting.toSaveArray().length == calm.toSaveArray().length
                && doubting.toSaveArray()[SLOT_DOUBT_LIFETIME] == doubting.getDoubtDepartedLifetime()
                && restored(doubting).getDoubtDepartedLifetime() == doubting.getDoubtDepartedLifetime());
        // FX8: the hot money's month - what arrived, what departed, what of it left on doubt - rides at the end, slots 16 to 18.
        double[] saved = doubting.toSaveArray();
        assertTrue("fixture: the month had hot money arrive, depart and leave on doubt (" + String.format("%,.1fk, %,.1fk, %,.1fk",
                doubting.getArrived(), doubting.getDeparted(), doubting.getDoubtDeparted()) + ")",
                doubting.getArrived() > 0 && doubting.getDeparted() > 0 && doubting.getDoubtDeparted() > 0);
        assertTrue("the saved array is 19 slots long: XR2's 16 and the month's three", saved.length == SAVE_SLOTS);
        assertTrue("...slots 16, 17 and 18 are the month's arrived, departed and doubtDeparted", saved[SLOT_ARRIVED] == doubting.getArrived()
                && saved[SLOT_DEPARTED] == doubting.getDeparted() && saved[SLOT_DOUBT_DEPARTED] == doubting.getDoubtDeparted());
        CapitalFlows back = restored(doubting);
        same("a restored copy reads the month's arrived to the bit", back.getArrived(), doubting.getArrived());
        same("...the month's departed to the bit", back.getDeparted(), doubting.getDeparted());
        same("...the month's doubtDeparted to the bit", back.getDoubtDeparted(), doubting.getDoubtDeparted());
        same("...and the net flow, which is the two of them", back.netFlow(), doubting.netFlow());
        // ...and a month that sets the three apart: nothing arriving, money leaving of its own accord as well as on doubt (the world's rate
        // is all the city pays), so slots 17 and 18 are two different numbers and not one figure read twice.
        CapitalFlows leaving = stocked(60);
        leaving.setDoubt(share);
        leaving.setMonth(61);
        leaving.takeMonth(.02, DebtManager.WORLD_BASE_RATE, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, 61);
        double[] savedLeaving = leaving.toSaveArray();
        assertTrue("fixture: a month with nothing arriving, money leaving of its own accord and money leaving on doubt (" + String.format("%,.1fk, %,.1fk, %,.1fk",
                leaving.getArrived(), leaving.getDeparted(), leaving.getDoubtDeparted()) + ")",
                leaving.getArrived() == 0 && leaving.getDoubtDeparted() > 0 && leaving.getDeparted() > leaving.getDoubtDeparted());
        assertTrue("...slots 16, 17 and 18 are its arrived, its departed and its doubtDeparted, each not the others'", savedLeaving.length == SAVE_SLOTS
                && savedLeaving[SLOT_ARRIVED] == leaving.getArrived() && savedLeaving[SLOT_DEPARTED] == leaving.getDeparted()
                && savedLeaving[SLOT_DOUBT_DEPARTED] == leaving.getDoubtDeparted() && savedLeaving[SLOT_DEPARTED] != savedLeaving[SLOT_DOUBT_DEPARTED]);
        CapitalFlows leavingBack = restored(leaving);
        assertTrue("...and a restored copy reads that month's three to the bit, the organic departure and the doubt's not swapped",
                Double.compare(leavingBack.getArrived(), leaving.getArrived()) == 0 && Double.compare(leavingBack.getDeparted(), leaving.getDeparted()) == 0
                        && Double.compare(leavingBack.getDoubtDeparted(), leaving.getDoubtDeparted()) == 0);
        // An array cut at 16, as XR2 wrote it, restores with the lifetime kept and the month's flows 0, as a new one has them.
        CapitalFlows cut = new CapitalFlows();
        cut.restore(Arrays.copyOf(saved, SLOT_ARRIVED));
        same("an array cut at 16 (as XR2 wrote it) keeps the doubt's lifetime", cut.getDoubtDepartedLifetime(), doubting.getDoubtDepartedLifetime());
        assertTrue("...and the month's arrived, departed and doubtDeparted are 0, as a new one has them", cut.getArrived() == 0
                && cut.getDeparted() == 0 && cut.getDoubtDeparted() == 0 && new CapitalFlows().getArrived() == 0
                && new CapitalFlows().getDeparted() == 0 && new CapitalFlows().getDoubtDeparted() == 0);

        // setDoubt(0) is the same as never having said a word, to the bit, over two years.
        CapitalFlows told = stocked(30), untold = stocked(30);
        int apart = 0;
        for (int m = 31; m <= 54; m++) {
            told.setDoubt(0);
            hotMonth(told, m);
            hotMonth(untold, m);
            if (Double.compare(told.getStock(), untold.getStock()) != 0 || Double.compare(told.getArrived(), untold.getArrived()) != 0
                    || Double.compare(told.getDeparted(), untold.getDeparted()) != 0 || Double.compare(told.getTarget(), untold.getTarget()) != 0
                    || Double.compare(told.getSpread(), untold.getSpread()) != 0 || told.getDoubtDeparted() != 0
                    || Double.compare(told.getLifetimeDeparted(), untold.getLifetimeDeparted()) != 0) apart++;
        }
        assertTrue("setDoubt(0) changes nothing against a twin that never called it: 24 months, every figure equal to the bit", apart == 0
                && Arrays.equals(told.toSaveArray(), untold.toSaveArray()));

        CapitalFlows whole = stocked(60);
        whole.setDoubt(3.5);
        hotMonth(whole, 61);
        close("setDoubt(more than 1) is all of it: the stock is gone", whole.getStock(), 0, 0);
        CapitalFlows nan = stocked(60), none = stocked(60);
        nan.setDoubt(Double.NaN);
        hotMonth(nan, 61);
        hotMonth(none, 61);
        same("setDoubt(not a number) is none", nan.getStock(), none.getStock());
        // ...and a city paying the world's rate has no hot money to take home (CapitalFlowCheck's first fixture).
        CapitalFlows empty = new CapitalFlows();
        empty.setDoubt(.5);
        empty.setMonth(1);
        empty.takeMonth(.02, DebtManager.WORLD_BASE_RATE, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, 1);
        assertTrue("fixture: a city that pays the world rate attracts nothing", empty.getStock() == 0);
        close("a doubt with no stock to take leaves nothing on doubt", empty.getDoubtDeparted(), 0, 0);

        // In a city: a peg a fifth over the D$ (a revaluation the shadow is far from), the surplus keeping the vault, hot money at home.
        Game g = null;
        double shareStruck = 0, stockBefore = 0;
        for (int attempt = 0; attempt < 6 && g == null; attempt++) {
            Game candidate = doubtTown("xr2-doubt-" + attempt, attempt == 0 ? 0 : SEED + attempt);
            ForeignAccounts fx = candidate.getForeignAccounts();
            play(candidate, 1);
            if (fx.getRegime() == PEG && !fx.brokeThisMonth() && fx.getDoubtShare() > 0 && candidate.getCapitalFlows().getStock() > 0) {
                g = candidate;
                shareStruck = fx.getDoubtShare();
                stockBefore = candidate.getCapitalFlows().getStock();
            }
        }
        assertTrue("fixture: a city pegged a fifth under its shadow held the month, with hot money in the city and a doubt struck", g != null);
        if (g != null) {
            ForeignAccounts fx = g.getForeignAccounts();
            CapitalFlows hot = g.getCapitalFlows();
            assertTrue("fixture: the gap is past BREAK_GAP (" + String.format("%.3f", fx.getLastPegGap()) + ")", fx.getLastPegGap() > ForeignAccounts.BREAK_GAP);
            close("the doubt share struck is hazard() of the gap, as the last reprice left it", shareStruck, ForeignAccounts.hazard(fx.getLastPegGap()), 1e-12);
            double lifetimeBefore = hot.getDoubtDepartedLifetime();
            play(g, 1);
            double departed = hot.getDoubtDeparted(), left = hot.getStock();
            out.printf("   hot money US$%,.0fk at the month's top, the doubt %.2f%%: %,.1fk left on doubt, %,.0fk remain%n", stockBefore, shareStruck * 100, departed, left);
            assertTrue("the month's doubt departure is the stock times the share the last reprice struck (the share of the stock after its own move)",
                    departed > 0 && Math.abs(departed / (left + departed) - shareStruck) <= 1e-9);
            assertTrue("...it is inside what departed that month, and the lifetime rose by it", hot.getDeparted() >= departed - 1e-9
                    && Math.abs(hot.getDoubtDepartedLifetime() - lifetimeBefore - departed) <= 1e-9);
            assertTrue("...and the audit closes (residual " + String.format("%.4f", g.getLastMoneyAudit().residual) + ")", ExchangeRateCheck.auditCloses(g));
        }
    }

    /** Where CapitalFlows.toSaveArray() puts the doubt's lifetime (XR2) and the month's three flows (FX8), and how long the array is. */
    static final int SLOT_DOUBT_LIFETIME = 15, SLOT_ARRIVED = 16, SLOT_DEPARTED = 17, SLOT_DOUBT_DEPARTED = 18, SAVE_SLOTS = 19;

    static CapitalFlows restored(CapitalFlows f) {
        CapitalFlows back = new CapitalFlows();
        back.restore(f.toSaveArray());
        return back;
    }

    /* ================= 6. a band's edges hold and its inside is free ================= */

    static void aBandsEdgesHold() {
        out.println("\n--- 6. a band's edges hold and its inside is free ---");

        final double width = 2 * ForeignAccounts.MARKET_SIGMA;
        ForeignAccounts band = book(TRADE, TRADE, DEEP, SEED, BAND);
        band.setBand(band.getRate(), 0, width);
        ForeignAccounts flt = book(TRADE, TRADE, DEEP, SEED, FLOAT);
        assertTrue("fixture: the band is " + String.format("%.1f%%", width * 100) + " either side of the rate of the day, narrower than the market's swing",
                band.getRegime() == BAND && Math.abs(band.getBandWidth() - width) <= 1e-15 && Double.compare(band.getBandCentre(), band.getRate()) == 0);

        int weak = 0, weakSold = 0, strong = 0, strongBought = 0, inside = 0, bad = 0, shadowApart = 0, insideApart = 0;
        double worstDeal = 0;
        for (int m = 0; m < 400; m++) {
            double exports = TRADE * (1 + .3 * Math.sin(m / 7.0)), imports = TRADE * (1 + .3 * Math.cos(m / 11.0));
            double vault = band.getReservesUsd();
            band.startMonth();
            band.takeMonth(CurrencyCheck.month(exports, imports, 0, 0), GDP);
            double deficit = band.monthDeficitUsd(), surplus = Math.max(0, balanceUsd(band));
            band.repriceCurrency();
            month(flt, exports, imports);
            double weakEdge = band.bandWeakEdge(), strongEdge = band.bandStrongEdge(), shadow = band.getShadowRate();
            if (Math.abs(shadow - flt.getRate()) > 1e-12 * shadow) shadowApart++;
            if (shadow > weakEdge) {
                weak++;
                if (band.getDefenceUsd() > 0) weakSold++;
                boolean ok = Double.compare(band.getRate(), weakEdge) == 0 && band.getEdgeBoughtUsd() == 0;
                worstDeal = Math.max(worstDeal, Math.abs(band.getDefenceUsd() - deficit));
                worstDeal = Math.max(worstDeal, Math.abs((vault - band.getReservesUsd()) - deficit));
                if (!ok) bad++;
            } else if (shadow < strongEdge) {
                strong++;
                if (band.getEdgeBoughtUsd() > 0) strongBought++;
                boolean ok = Double.compare(band.getRate(), strongEdge) == 0 && band.getDefenceUsd() == 0;
                worstDeal = Math.max(worstDeal, Math.abs(band.getEdgeBoughtUsd() - surplus));
                worstDeal = Math.max(worstDeal, Math.abs((band.getReservesUsd() - vault) - surplus));
                if (!ok) bad++;
            } else {
                inside++;
                boolean ok = Double.compare(band.getRate(), shadow) == 0 && Double.compare(band.getRate(), flt.getRate()) == 0
                        && band.getDefenceUsd() == 0 && band.getEdgeBoughtUsd() == 0 && Double.compare(band.getReservesUsd(), vault) == 0;
                if (!ok) insideApart++;
            }
            band.takeEdgeOwed();
        }
        out.printf("   400 months, a %.1f%% band under a market that swings %.1f%% a month: %d past the weak edge (the vault sold in %d), %d past the strong edge"
                + " (it bought in %d), %d inside%n", width * 100, ForeignAccounts.MARKET_SIGMA * 100, weak, weakSold, strong, strongBought, inside);
        assertTrue("fixture: months fell past the weak edge, past the strong edge and inside it (" + weak + ", " + strong + ", " + inside + ")",
                weak > 20 && strong > 20 && inside > 20);
        assertTrue("fixture: the vault sold in months past the weak edge and bought in months past the strong one", weakSold > 10 && strongBought > 10);
        assertTrue("fixture: the band's shadow is the floating twin's rate, month by month (the hold changes nothing the float's rule says)", shadowApart == 0);
        assertTrue("past the weak edge the rate is the weak edge exactly and nothing is bought; past the strong edge the strong edge exactly and nothing is sold", bad == 0);
        close("...the vault sold exactly the month's deficit at the weak edge and bought exactly the surplus at the strong, in dollars", worstDeal, 0, DOLLAR);
        assertTrue("inside the band the rate is the shadow exactly (the floating twin's rate to the bit), and nothing is dealt", insideApart == 0);

        // The centre crawls.
        ForeignAccounts crawl = book(TRADE, TRADE, DEEP, 0, BAND);
        double c0 = crawl.getBandCentre();
        crawl.setBand(c0, .06, .05);
        for (int i = 0; i < 12; i++) month(crawl, TRADE, TRADE);
        close("the centre crawls at the slope: after 12 reprices it is the centre times exp(slope)", crawl.getBandCentre(), c0 * Math.exp(.06), 1e-12);
        close("...and the edges are the centre's width either side of it", crawl.bandWeakEdge() / crawl.bandStrongEdge(), (1 + .05) / (1 - .05), 1e-12);

        // A vault under the floor at the weak edge: the band gives way that month, and stays a band.
        ForeignAccounts probe = book(.8 * TRADE, TRADE, DEEP, 0, BAND);
        probe.setBand(probe.getRate(), 0, .01);
        probe.startMonth();
        probe.takeMonth(CurrencyCheck.month(.8 * TRADE, TRADE, 0, 0), GDP);
        double need = probe.monthDeficitUsd(), floor = probe.vaultFloorUsd();
        ForeignAccounts under = book(.8 * TRADE, TRADE, floor + need - 1, 0, BAND), over = book(.8 * TRADE, TRADE, floor + need + 1, 0, BAND);
        ForeignAccounts floating = book(.8 * TRADE, TRADE, floor + need - 1, 0, FLOAT);
        under.setBand(under.getRate(), 0, .01);
        over.setBand(over.getRate(), 0, .01);
        double underVault = under.getReservesUsd();
        month(under, .8 * TRADE, TRADE);
        month(over, .8 * TRADE, TRADE);
        month(floating, .8 * TRADE, TRADE);
        assertTrue("fixture: the shadow is past the weak edge in both", floating.getRate() > over.bandWeakEdge() && floating.getRate() > under.bandWeakEdge());
        assertTrue("a vault a dollar under its floor after the deficit: the band gives way that month, the regime still Band, nothing sold",
                under.getRegime() == BAND && !under.brokeThisMonth() && under.getDefenceUsd() == 0 && Double.compare(under.getReservesUsd(), underVault) == 0);
        same("...the rate is the shadow (the floating twin's rate), not the edge", under.getRate(), floating.getRate());
        assertTrue("...past the edge it was to hold", under.getRate() > under.bandWeakEdge());
        same("its twin a dollar over the floor holds the weak edge", over.getRate(), over.bandWeakEdge());
        close("...selling the deficit", over.getDefenceUsd(), need, DOLLAR);
    }

    /* ================= 7. under a peg the dial is the US's rate plus the spread; the aim ================= */

    /** Plays a month and returns the dial it set: nothing else moves the policy rate in a month. */
    static double dialAfter(Game g) {
        quietly(() -> g.simulateMonths(1));
        return g.getDebtManager().getPolicyRate();
    }

    /** Puts the city on a peg a share of the D$'s shadow under it, so the gap the aim reads is not nothing. */
    static void pegWithGap(Game g, double logGap) {
        quietly(() -> {
            if (g.getForeignAccounts().getRegime() != PEG) g.setCurrencyRegime(PEG);
            g.repeg(g.getForeignAccounts().getShadowRate() * Math.exp(-logGap));
        });
    }

    /** The same for a band, centred a share under the shadow. */
    static void bandWithGap(Game g, double logGap) {
        quietly(() -> {
            if (g.getForeignAccounts().getRegime() != BAND) g.setCurrencyRegime(BAND);
            g.setBand(g.getForeignAccounts().getShadowRate() * Math.exp(-logGap), 0, .03);
        });
    }

    static void underAPegTheDialIsTheUSes() {
        out.println("\n--- 7. under a peg the dial is the US's rate plus the spread; the aim ---");

        Game g = town("xr2-dial", Founding.defaults());
        play(g, 60);
        ForeignAccounts fx = g.getForeignAccounts();
        DebtManager dm = g.getDebtManager();
        assertTrue("fixture: the city has a year of prices, so the inflation rule has a rate to read", g.getPriceIndex().hasRate());

        final double spread = .03;
        dm.setPegSpread(spread);

        // A peg, the autopilot off: the world's rate plus the player's spread.
        quietly(() -> dm.setAutopilot(false));
        pegWithGap(g, .06);
        assertTrue("fixture: pegged, the autopilot off, a gap of the D$ past the peg", fx.getRegime() == PEG && !dm.isAutopilot() && fx.currencyGap() > .05);
        same("a peg, the autopilot off: the policy rate is WORLD_BASE_RATE plus the spread", dialAfter(g), DebtManager.WORLD_BASE_RATE + spread);

        // ...the autopilot on, aiming at inflation: the same, whatever the inflation rule would say.
        quietly(() -> { dm.setAutopilot(true); dm.setAim(DebtManager.Aim.INFLATION); });
        pegWithGap(g, .06);
        double rule = dm.advisedPolicyRate(g.getPriceIndex().inflation());
        assertTrue("fixture: the inflation rule would set a different rate (" + String.format("%.4f against %.4f", rule, DebtManager.WORLD_BASE_RATE + spread) + ")",
                Math.abs(rule - (DebtManager.WORLD_BASE_RATE + spread)) > 1e-4);
        double gapBefore = fx.currencyGap();
        same("a peg, the autopilot on, aiming at inflation: still WORLD_BASE_RATE plus the spread", dialAfter(g), DebtManager.WORLD_BASE_RATE + spread);
        assertTrue("fixture: the gap the aim ignored was not nothing (" + String.format("%.4f", gapBefore) + ")", gapBefore > .05);

        // ...aiming at the currency: the gap's spread, the player's own set aside.
        quietly(() -> dm.setAim(DebtManager.Aim.CURRENCY));
        pegWithGap(g, .06);
        double gap = fx.currencyGap();
        double expected = dm.tiedRate(gap);
        double bare = Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, DebtManager.WORLD_BASE_RATE + DebtManager.CURRENCY_WEIGHT * gap));
        assertTrue("fixture: the gap is " + String.format("%.4f", gap) + " and the dial it asks is inside its bounds, over the world's rate",
                gap > .05 && expected > DebtManager.MIN_POLICY_RATE && expected < DebtManager.MAX_POLICY_RATE && expected > DebtManager.WORLD_BASE_RATE);
        same("a peg, the autopilot aiming at the currency: the policy rate is tiedRate(currencyGap())", dialAfter(g), expected);
        close("...and that is WORLD_BASE_RATE plus CURRENCY_WEIGHT of the gap, inside the dial's bounds", expected, bare, 1e-15);

        // ...but with the autopilot off the aim means nothing: the spread is the player's.
        quietly(() -> dm.setAutopilot(false));
        pegWithGap(g, .06);
        same("a peg, the autopilot off whatever the aim: the player's spread still", dialAfter(g), DebtManager.WORLD_BASE_RATE + spread);
        quietly(() -> dm.setAutopilot(true));

        // A band: the aim picks the rule.
        quietly(() -> dm.setAim(DebtManager.Aim.CURRENCY));
        bandWithGap(g, .05);
        gap = fx.currencyGap();
        expected = dm.currencyRate(gap);
        assertTrue("fixture: a band, the shadow " + String.format("%.4f", gap) + " past its centre, the autopilot aiming at the currency",
                fx.getRegime() == BAND && gap > .04 && expected > DebtManager.WORLD_BASE_RATE);
        same("a band, the autopilot aiming at the currency: currencyRate(gap)", dialAfter(g), expected);

        quietly(() -> dm.setAim(DebtManager.Aim.MIX));
        bandWithGap(g, .05);
        gap = fx.currencyGap();
        double inflation = g.getPriceIndex().inflation();
        expected = dm.mixRate(inflation, gap);
        assertTrue("fixture: a mix of the rule and the gap is not the rule alone, nor the gap alone",
                Math.abs(expected - dm.advisedPolicyRate(inflation)) > 1e-4 && Math.abs(expected - dm.currencyRate(gap)) > 1e-4);
        same("a band, the autopilot aiming at a mix: mixRate(inflation, gap)", dialAfter(g), expected);

        quietly(() -> dm.setAim(DebtManager.Aim.INFLATION));
        bandWithGap(g, .05);
        expected = dm.advisedPolicyRate(g.getPriceIndex().inflation());
        same("a band, the autopilot aiming at inflation: the inflation rule", dialAfter(g), expected);

        // Lean (and Float) with any aim: the inflation rule, exactly as advisedPolicyRate().
        int leanRight = 0, cases = 0;
        for (ForeignAccounts.Regime regime : new ForeignAccounts.Regime[] { LEAN, FLOAT }) {
            for (DebtManager.Aim aim : DebtManager.Aim.values()) {
                final ForeignAccounts.Regime r = regime;
                final DebtManager.Aim a = aim;
                quietly(() -> { dm.setAim(a); g.setCurrencyRegime(r); });
                double rule2 = dm.advisedPolicyRate(g.getPriceIndex().inflation());
                cases++;
                if (fx.getRegime() == regime && Double.compare(dialAfter(g), rule2) == 0) leanRight++;
            }
        }
        assertTrue("under Lean or Float, whatever the aim: the policy rate is the inflation rule's advisedPolicyRate(), exactly (" + leanRight + " of " + cases + " cases)",
                leanRight == cases);

        // The autopilot off in a band leaves the dial where the player put it.
        quietly(() -> { dm.setAutopilot(false); dm.setAim(DebtManager.Aim.CURRENCY); dm.setPolicyRate(.047); });
        bandWithGap(g, .05);
        same("a band, the autopilot off: the dial stays where the player left it", dialAfter(g), .047);
    }

    /* ================= 8. the currency aim holds a band, counted over 1,000 seeded years ================= */

    /**
     * 12,000 months of a band narrower than the market's swing, the real rate gap fed each month from a stand-in dial: the neutral
     * dial, or the one the currency aim sets (DebtManager.currencyRate(), the model's own). The gap is formed as Game.realRateDifferential()
     * forms it, the dial less the inflation the city expects against WORLD_BASE_RATE less the world's, here both inflations fixed.
     * Returns the months with the shadow inside the band and the dollars the vault spent.
     */
    static double[] bandYears(boolean aimed) {
        final double expectedInflation = .02, worldInflation = .02;
        final double neutral = DebtManager.WORLD_BASE_RATE + expectedInflation - worldInflation;   // the dial at which the real gap is nothing
        DebtManager dm = new DebtManager();
        ForeignAccounts fx = book(TRADE, TRADE, DEEP, SEED, BAND);
        fx.setBand(fx.getRate(), 0, 2 * ForeignAccounts.MARKET_SIGMA);
        int inside = 0, months = 12_000;
        double spent = 0;
        for (int m = 0; m < months; m++) {
            double dial = aimed ? dm.currencyRate(fx.currencyGap()) : neutral;
            fx.setRealRateDifferential((dial - expectedInflation) - (DebtManager.WORLD_BASE_RATE - worldInflation));
            double exports = TRADE * (1 + .3 * Math.sin(m / 7.0)), imports = TRADE * (1 + .3 * Math.cos(m / 11.0));
            month(fx, exports, imports);
            double shadow = fx.getShadowRate();
            if (shadow >= fx.bandStrongEdge() && shadow <= fx.bandWeakEdge()) inside++;
            spent += fx.getDefenceUsd();
            fx.takeEdgeOwed();
        }
        return new double[] { inside, spent, months };
    }

    static void theCurrencyAimHoldsABand() {
        out.println("\n--- 8. the currency aim holds a band, counted over 1,000 seeded years ---");

        double[] neutral = bandYears(false), aimed = bandYears(true);
        double neutralShare = neutral[0] / neutral[2], aimedShare = aimed[0] / aimed[2];
        out.printf("   12,000 months in a %.1f%% band: the dial neutral, the shadow inside the band %.1f%% of months and the vault spent US$%,.0fk;"
                + " the currency aim, %.1f%% and US$%,.0fk%n", 2 * ForeignAccounts.MARKET_SIGMA * 100, neutralShare * 100, neutral[1],
                aimedShare * 100, aimed[1]);
        assertTrue("fixture: the neutral dial's band is a hard one: the shadow is outside it more than a third of the months, and the vault spent", neutralShare < .66 && neutral[1] > 0);
        assertTrue("the currency aim's share of months with the shadow inside the band is higher (" + String.format("%.1f%% against %.1f%%", aimedShare * 100, neutralShare * 100) + ")",
                aimedShare > neutralShare);
        assertTrue("...and its vault spends less defending the weak edge (US$" + String.format("%,.0fk against US$%,.0fk", aimed[1], neutral[1]) + ")",
                aimed[1] < neutral[1]);
    }

    /* ================= 9. the toggle's two payers each pay exactly what the edge bought ================= */

    /** The label the treasury's journal gives the edge's bill (Game.settleTheEdge()). */
    static final String EDGE_BILL = "The band's or the peg's dollars";

    /** A city pegged at 36 months with a large positive spread: the dial high, money arriving, a surplus at the peg every month. */
    static Game surplusTown(String label, ForeignAccounts.EdgePayer payer) {
        Game g = town(label, Founding.defaults());
        play(g, 36);
        quietly(() -> {
            g.setCurrencyRegime(PEG);
            g.getDebtManager().setPegSpread(.30);
            g.setEdgePayer(payer);
        });
        return g;
    }

    /** What the treasury paid for the edge's dollars last month, from its journal (0 when the journal has no such line). */
    static double billPaid(Game g) {
        double paid = 0;
        for (TreasuryJournal.Entry e : g.getTreasuryJournal()) if (EDGE_BILL.equals(e.label())) paid -= e.amount();
        return paid;
    }

    static void theTwoPayers() {
        out.println("\n--- 9. the toggle's two payers each pay exactly what the edge bought ---");

        // The central bank's equity once settled: a bare bank, a vault it reads and a purchase of dollars paid in new money.
        double[] vault = { 1_000 };
        CentralBank bare = new CentralBank(() -> vault[0]);
        double e0 = bare.equity(), m0 = bare.m0();
        vault[0] += 250;
        close("a bare central bank: the vault's new dollars lift its equity before the money is made", bare.equity(), e0 + 250, 1e-9);
        bare.startMonth();
        close("...paying for them in new money: it pays what it is asked", bare.buyDollarsForVault(250), 250, 0);
        close("...M0 is up by their price", bare.m0(), m0 + 250, 1e-9);
        close("...and equity is where it was: the assets grew by what the liabilities grew by", bare.equity(), e0, 1e-9);
        close("...the money made is the month's issue, and it is recorded as bought", bare.getIssued() - bare.getRetired(), 250, 1e-9);
        close("...getDollarsBought() and its lifetime", bare.getDollarsBought() + bare.getDollarsBoughtLifetime(), 500, 1e-9);
        close("...and it buys nothing for nothing", bare.buyDollarsForVault(0) + bare.buyDollarsForVault(-5) + bare.buyDollarsForVault(Double.NaN), 0, 0);

        Game cb = surplusTown("xr2-payer-cb", ForeignAccounts.EdgePayer.CENTRAL_BANK);
        Game tr = surplusTown("xr2-payer-tr", ForeignAccounts.EdgePayer.TREASURY);
        ForeignAccounts cbFx = cb.getForeignAccounts(), trFx = tr.getForeignAccounts();
        assertTrue("fixture: two cities, pegged with a spread of 30 points, and the payer on the toggle is each's",
                cbFx.getRegime() == PEG && trFx.getRegime() == PEG && cbFx.getEdgePayer() == ForeignAccounts.EdgePayer.CENTRAL_BANK
                        && trFx.getEdgePayer() == ForeignAccounts.EdgePayer.TREASURY);
        // FX8: the central bank keeps nothing, restated with what it paid in new money for the vault's dollars: asserted in both cities every
        // month from here, the identity's months and its misses tallied.
        int[] keptTally = { 0, 0 };
        play(cb, 1);
        play(tr, 1);
        tallyKept(keptTally, cb, tr);
        double owed = cbFx.getEdgeOwedLocal();
        out.printf("   the month after the peg: the vault bought US$%,.0fk, owed %,.0fk of local money%n", cbFx.getEdgeBoughtUsd(), owed);
        assertTrue("fixture: money arrived and the vault bought dollars at the peg, the same in both cities (a surplus at the peg), and the payer is recorded with it",
                owed > 100 && Double.compare(owed, trFx.getEdgeOwedLocal()) == 0 && cbFx.getEdgeOwedPayer() == ForeignAccounts.EdgePayer.CENTRAL_BANK
                        && trFx.getEdgeOwedPayer() == ForeignAccounts.EdgePayer.TREASURY && cbFx.getEdgeBoughtUsd() > 0);
        assertTrue("fixture: nothing was paid yet: the bill is settled the month after, and the cities are alike to the bit until it is",
                cb.getCentralBank().getDollarsBought() == 0 && billPaid(tr) == 0 && Double.compare(cb.getCentralBank().m0(), tr.getCentralBank().m0()) == 0
                        && Double.compare(cb.getCash(), tr.getCash()) == 0);
        double m0Before = cb.getCentralBank().m0(), cashCb = cb.getCash(), cashTr = tr.getCash();
        double room = tr.discretionaryRoom();
        double recordCb = cbFx.getLifetimeIntervention(), recordTr = trFx.getLifetimeIntervention();
        play(cb, 1);
        play(tr, 1);
        tallyKept(keptTally, cb, tr);

        // The vault's record (FX8): in the settlement month it rises by the whole bill, whoever paid it. The month's other terms are the
        // treasury's reserve purchases and sales, the defence's sales and the land office's dollars; none of them dealt in the vault.
        double riseCb = cbFx.getLifetimeIntervention() - recordCb, riseTr = trFx.getLifetimeIntervention() - recordTr;
        double paidTr = billPaid(tr);
        out.printf("   the settlement month: the vault's record rose %,.4fk in the central bank's city and %,.4fk in the treasury's, of the %,.4fk owed%n",
                riseCb, riseTr, owed);
        assertTrue("fixture: the settlement month's other terms are 0 in the central bank's city: no defence, no reserves bought or sold, no land paid out of the vault",
                cbFx.getDefenceUsd() == 0 && cbFx.getDefenceLocal() == 0 && cbFx.getBoughtThisMonth() == 0 && cbFx.getSoldThisMonth() == 0
                        && cbFx.getLandUsdFromVaultThisMonth() == 0 && cbFx.getLandUsdPending() == 0);
        assertTrue("fixture: ...and in the treasury's city the only purchase is the bill it paid itself (" + String.format("%,.2fk", paidTr)
                        + "), with no defence, no sales, no land",
                trFx.getDefenceUsd() == 0 && trFx.getDefenceLocal() == 0 && Math.abs(trFx.getBoughtThisMonth() - paidTr) <= 1e-6 * Math.max(1, paidTr)
                        && trFx.getSoldThisMonth() == 0 && trFx.getLandUsdFromVaultThisMonth() == 0 && trFx.getLandUsdPending() == 0);
        close("the vault's record (getLifetimeIntervention()) rose by the whole owed bill in the central bank's city, which paid it in new money", riseCb, owed,
                1e-6 * Math.max(1, owed));
        close("...and by the whole owed bill in the treasury's city, which paid what its room allowed and the central bank the rest", riseTr, owed,
                1e-6 * Math.max(1, owed));

        // The central bank pays.
        double issued = cb.getCentralBank().getIssued() - cb.getCentralBank().getRetired();
        close("the central bank's city: it paid the owed local price in new money: getDollarsBought() is exactly it", cb.getCentralBank().getDollarsBought(), owed, 1e-9);
        close("...and its lifetime", cb.getCentralBank().getDollarsBoughtLifetime(), owed, 1e-9);
        close("...M0 moved by what was made less what was taken back, to the cent", cb.getCentralBank().m0() - m0Before, issued, .01);
        assertTrue("...and the money made includes it (" + String.format("%,.1f made, %,.1f of it for the dollars", cb.getCentralBank().getIssued(), owed) + ")",
                cb.getCentralBank().getIssued() >= owed - 1e-9);
        assertTrue("...the treasury's cash did not pay it: no line in the treasury's journal for the bill", billPaid(cb) == 0);
        // The treasury pays.
        double paid = billPaid(tr), rest = tr.getCentralBank().getDollarsBought();
        out.printf("   the treasury's city: the treasury paid %,.2fk of the %,.2fk (its room %,.0fk), the central bank the rest, %,.2fk%n", paid, owed, room, rest);
        assertTrue("the treasury's city: its cash paid it, as much as its room allowed (" + String.format("%,.2f", paid) + "), the rest by the central bank, so it was paid once and in full",
                paid > 0 && paid <= owed + 1e-9 && Math.abs(paid + rest - owed) <= 1e-9 && (paid >= owed - 1e-9 || Math.abs(paid - room) <= 1e-6 * Math.max(1, room)));
        assertTrue("...getBoughtThisMonth() includes it: the treasury's city bought what the central bank's did, and the bill besides (" + String.format("%,.2fk against %,.2fk",
                        trFx.getBoughtThisMonth(), cbFx.getBoughtThisMonth()) + ")",
                Math.abs((trFx.getBoughtThisMonth() - cbFx.getBoughtThisMonth()) - paid) <= 1e-6 * Math.max(1, paid));
        close("...the treasury's cash is lower than the central bank's city's by what it paid", cb.getCash() - tr.getCash() - (cashCb - cashTr), paid,
                1e-3 * Math.max(1, paid));
        close("...and the central bank's city's M0 is higher by it (the treasury's money left the city, the central bank's was made)",
                cb.getCentralBank().m0() - tr.getCentralBank().m0(), paid, 1e-3 * Math.max(1, paid));
        assertTrue("the audit closes in both, the settlement month (residuals " + String.format("%.4f and %.4f", cb.getLastMoneyAudit().residual, tr.getLastMoneyAudit().residual) + ")",
                ExchangeRateCheck.auditCloses(cb) && ExchangeRateCheck.auditCloses(tr)
                        && Math.abs(cb.getLastMoneyAudit().residual) <= .01 && Math.abs(tr.getLastMoneyAudit().residual) <= .01);

        // The central bank's equity, once settled, is unmoved by the purchase: the treasury's city, which paid out of cash, keeps the
        // dollars in its equity by what it paid; the central bank's, which made the money, does not. Three more months of it.
        double paidSoFar = paid;
        int closes = 0, months = 3;
        double worstIdentity = 0, gap = 0;
        for (int i = 0; i < months; i++) {
            play(cb, 1);
            play(tr, 1);
            tallyKept(keptTally, cb, tr);
            paidSoFar += billPaid(tr);
            // Equity reads the vault, which already holds the month's purchase before M0 has caught up: the purchase not yet paid for is taken out.
            double settledCb = cb.getCentralBank().equity() - cbFx.getEdgeOwedLocal();
            double settledTr = tr.getCentralBank().equity() - trFx.getEdgeOwedLocal();
            gap = settledTr - settledCb;
            worstIdentity = Math.max(worstIdentity, Math.abs(gap - paidSoFar));
            if (ExchangeRateCheck.auditCloses(cb) && ExchangeRateCheck.auditCloses(tr)) closes++;
        }
        out.printf("   four months of purchases: the treasury paid %,.2fk in all; the equity gap between the cities, less what the central bank's still owes for, is %,.2fk"
                + " (the worst miss %.2e)%n", paidSoFar, gap, worstIdentity);
        assertTrue("equity after the settlement, less the purchase not yet paid for, differs between the cities by what the treasury paid, to the cent: the central bank's own"
                + " equity is unmoved by the purchases it paid for in new money", worstIdentity < .01);
        assertTrue("the audit closes in both, every month of it", closes == months);

        // The central bank keeps nothing (FX8): equity less the vault, plus what it paid in new money for the vault's dollars, is the
        // remittance not yet paid less the loss carried, in both cities, in every month this section played.
        CentralBank cbBank = cb.getCentralBank(), trBank = tr.getCentralBank();
        out.printf("   the central bank keeps nothing: %d city-months held to it, %d missed; paid in new money for the vault's dollars, lifetime: US$%,.2fk"
                + " in the central bank's city, %,.2fk in the treasury's%n", keptTally[0], keptTally[1], cbBank.getDollarsBoughtLifetime(), trBank.getDollarsBoughtLifetime());
        assertTrue("fixture: the central bank's city paid for dollars in new money (lifetime " + String.format("%,.2fk", cbBank.getDollarsBoughtLifetime()) + "), or the term proves nothing",
                cbBank.getDollarsBoughtLifetime() > 0);
        int monthsHeld = 2 + months; // the month after the peg, the settlement month, and the loop's
        assertTrue("fixture: the identity was held to in " + 2 * monthsHeld + " city-months, two cities in each of the " + monthsHeld + " months after the fixture",
                keptTally[0] == 2 * monthsHeld);
        assertTrue("equity less the vault plus what the bank paid for the vault's dollars is the remittance due less the loss carried, in both cities, every month ("
                + keptTally[1] + " missed)", keptTally[1] == 0);
        double without = cbBank.equity() - cbBank.getVault() - (cbBank.getRemittanceDue() - cbBank.getLossCarried());
        close("...and the old statement, without the term, misses in the central bank's city by exactly what it paid in new money: the term is what closes it", without,
                -cbBank.getDollarsBoughtLifetime(), Math.max(.01, 1e-9 * cbBank.m0()));
        assertTrue("...the miss without it is a real one, not rounding (" + String.format("%,.2fk", without) + ")", Math.abs(without) > .01);
    }

    /** The central bank keeps nothing, as FX8 states it: equity less the vault, plus what it paid in new money for the vault's dollars, less the remittance due plus the loss carried. 0 when it keeps nothing. */
    static double kept(CentralBank cb) {
        return cb.equity() - cb.getVault() + cb.getDollarsBoughtLifetime() - (cb.getRemittanceDue() - cb.getLossCarried());
    }

    /** A miss, as LongPlaytest counts one: past a cent and past a billionth of M0. */
    static boolean keepsSomething(CentralBank cb) {
        double kept = kept(cb);
        return Math.abs(kept) > .01 && Math.abs(kept) > 1e-9 * Math.max(1, cb.m0());
    }

    /** One more month held to the identity in each of these cities: tally[0] counts the city-months, tally[1] the misses. */
    static void tallyKept(int[] tally, Game... cities) {
        for (Game city : cities) {
            tally[0]++;
            if (keepsSomething(city.getCentralBank())) tally[1]++;
        }
    }

    /* ================= 10. saved and loaded mid-peg ================= */

    /** The regime's state as the getters say it: every figure XR2 added to the currency, as one row. */
    static double[] regimeFields(ForeignAccounts fx) {
        return new double[] { fx.getRegime().ordinal(), fx.getEdgePayer().ordinal(), fx.getPegRate(), fx.getBandCentre(), fx.getBandSlope(),
                fx.getBandWidth(), fx.getShadowRate(), fx.isHeld() ? 1 : 0, fx.getDoubtShare(), fx.getLastHazard(), fx.getLastPegGap(),
                fx.getEdgeOwedLocal(), fx.getEdgeOwedPayer().ordinal(), fx.getEdgeBoughtUsd(), fx.getEdgeBoughtLocal(),
                fx.getEdgeBoughtUsdLifetime(), fx.getBreaks(), fx.getLastBreakCause().ordinal(), fx.getLastBreakJump(),
                fx.getLastBreakShadow(), fx.brokeThisMonth() ? 1 : 0, fx.getMoveHeld() };
    }

    static void savedMidPeg() {
        out.println("\n--- 10. saved and loaded mid-peg ---");

        Game live = null;
        for (int attempt = 0; attempt < 6 && live == null; attempt++) {
            Game candidate = doubtTown("xr2-save-" + attempt, attempt == 0 ? 0 : SEED + attempt);
            play(candidate, 1);
            ForeignAccounts fx = candidate.getForeignAccounts();
            if (fx.getRegime() == PEG && fx.getEdgeOwedLocal() > 0 && fx.getDoubtShare() > 0 && fx.isHeld()) live = candidate;
        }
        assertTrue("fixture: a pegged city with a purchase owed, a doubt share struck and the rate held away from its shadow", live != null);
        if (live != null) {
            final Game saved = live;
            ForeignAccounts fx = live.getForeignAccounts();
            out.printf("   saved at month %d: owed %,.1fk, doubt %.2f%%, the shadow %.4f over a peg of %.4f%n", live.getMonth(), fx.getEdgeOwedLocal(),
                    fx.getDoubtShare() * 100, fx.getShadowRate(), fx.getPegRate());
            assertTrue("saved to its scratch folder", live.saveGame(2, "mid-peg").ok);
            Game back = new Game(live.getGameFiles());
            quietly(() -> back.loadGameSave(2));
            ForeignAccounts bfx = back.getForeignAccounts();
            assertTrue("the loaded city keeps the regime, the payer, the peg, the shadow, the doubt, what is owed and who owes it, to the bit",
                    sameBits(regimeFields(fx), regimeFields(bfx)));
            assertTrue("...and the hot money's doubt, the vault and the dial", Double.compare(back.getCapitalFlows().getDoubtDepartedLifetime(),
                    live.getCapitalFlows().getDoubtDepartedLifetime()) == 0 && Double.compare(bfx.getReservesUsd(), fx.getReservesUsd()) == 0
                    && Double.compare(back.getDebtManager().getPegSpread(), live.getDebtManager().getPegSpread()) == 0
                    && back.getDebtManager().getAim() == live.getDebtManager().getAim());
            int apart = 0, firstApart = 0, regimeApart = 0;
            double worst = 0;
            boolean broke = false;
            for (int m = 1; m <= 12; m++) {
                quietly(() -> {
                    saved.simulateMonths(1);
                    back.simulateMonths(1);
                });
                double[] a = figures(saved), b = figures(back);
                for (int k = 0; k < a.length; k++) worst = Math.max(worst, Math.abs(a[k] - b[k]) / Math.max(1, Math.abs(a[k])));
                if (!sameBits(a, b)) { apart++; if (firstApart == 0) firstApart = m; }
                if (fx.getRegime() != bfx.getRegime()) regimeApart++;
                broke |= fx.brokeThisMonth();
            }
            out.printf("   12 months on: %d differ in the rate, vault, M0, cash or credibility to the bit; the largest relative gap %.2e; the peg %s%n", apart, worst,
                    broke ? "broke in the run" : "held");
            assertTrue("the next 12 months of rate, vault, M0, cash and credibility equal the twin that never saved (within SaveFileCheck's replay tolerance, 1e-9)", worst < 1e-9);
            assertTrue("...and the regime is the same every month, the break (if any) in the same month", regimeApart == 0 && fx.getBreaks() == bfx.getBreaks());
            assertTrue("...to the bit" + (apart == 0 ? "" : " (first apart: month " + firstApart + ")"), apart == 0);
        }

        // A bare currency with every regime field set: a band mid-crawl with a purchase owed by the treasury, and a peg already broken.
        ForeignAccounts band = book(TRADE, TRADE, DEEP, SEED, BAND);
        band.setBand(band.getRate(), .06, .02);
        band.setEdgePayer(ForeignAccounts.EdgePayer.TREASURY);
        for (int m = 0; m < 50; m++) {
            double exports = TRADE * (1 + .3 * Math.sin(m / 7.0)), imports = TRADE * (1 + .3 * Math.cos(m / 11.0));
            month(band, exports, imports);
        }
        assertTrue("fixture: a band mid-crawl, the rate held away from the shadow, with a purchase owed by the treasury",
                band.getBandCentre() > 1.0 && band.isHeld() && band.getEdgeOwedLocal() > 0 && band.getEdgeOwedPayer() == ForeignAccounts.EdgePayer.TREASURY
                        && band.getEdgeBoughtUsdLifetime() > 0);
        ForeignAccounts peg = null;
        int pegNext = 0;   // the month the broken peg plays next
        for (long s = 1; s <= 200 && peg == null; s++) {
            ForeignAccounts p = book(TRADE, TRADE, DEEP, SEED + s, PEG);
            for (int m = 0; m < 400; m++) {
                driftMonth(p, m);
                if (p.brokeThisMonth()) { for (int more = m + 1; more <= m + 8; more++) driftMonth(p, more); peg = p; pegNext = m + 9; break; }
            }
        }
        assertTrue("fixture: a peg that broke by doubt eight months ago, its overshoot still fading", peg != null && peg.getBreaks() == 1
                && peg.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT && peg.getRegime() == FLOAT);
        ForeignAccounts[] sources = peg == null ? new ForeignAccounts[] { band } : new ForeignAccounts[] { band, peg };
        String[] names = { "the band", "the broken peg" };
        int continued = 0;
        for (int i = 0; i < sources.length; i++) {
            ForeignAccounts source = sources[i];
            ForeignAccounts back = new ForeignAccounts();
            back.restore(source.toSaveArray());
            assertTrue("restore(toSaveArray()) keeps every regime field of " + names[i] + ", to the bit", sameBits(regimeFields(source), regimeFields(back)));
            assertTrue("...and the regime's state it cannot be asked: the market, the draws, the seed", Double.compare(source.getMarket(), back.getMarket()) == 0
                    && source.getMarketDraws() == back.getMarketDraws() && source.getMarketSeed() == back.getMarketSeed());
            int apart = 0;
            for (int m = 50; m < 74; m++) {
                if (i == 0) {
                    double exports = TRADE * (1 + .3 * Math.sin(m / 7.0)), imports = TRADE * (1 + .3 * Math.cos(m / 11.0));
                    month(source, exports, imports);
                    month(back, exports, imports);
                } else {
                    driftMonth(source, pegNext + (m - 50));
                    driftMonth(back, pegNext + (m - 50));
                }
                if (Double.compare(source.getRate(), back.getRate()) != 0 || Double.compare(source.getReservesUsd(), back.getReservesUsd()) != 0
                        || !sameBits(regimeFields(source), regimeFields(back))) apart++;
            }
            if (apart == 0) continued++;
        }
        assertTrue("...and each plays 24 months on exactly as the one it was saved from: rate, vault and every regime field to the bit (the fade of a break, the crawl of a band)",
                continued == sources.length);

        // An array cut before the regimes is a Lean currency with nothing owed.
        double[] full = band.toSaveArray();
        assertTrue("fixture: the regimes are slots 71 on, the array longer than 94", full.length > 94);
        ForeignAccounts old = new ForeignAccounts();
        old.restore(Arrays.copyOf(full, 71));
        assertTrue("an array cut at slot 70, as an older save wrote it, restores as Lean with nothing owed, held or broken",
                old.getRegime() == LEAN && old.getEdgeOwedLocal() == 0 && !old.isHeld() && old.getBreaks() == 0 && old.getPegRate() == 0
                        && old.getEdgePayer() == ForeignAccounts.EdgePayer.CENTRAL_BANK && old.getLastBreakCause() == ForeignAccounts.BreakCause.NONE);
        same("...its band is the default width, unset", old.getBandWidth(), ForeignAccounts.BAND_WIDTH);
        same("...and the rate it had is the saved one", old.getRate(), band.getRate());
        close("...and the vault is the saved vault, in dollars", Math.abs(old.getReservesUsd() - band.getReservesUsd()), 0, 1e-6 * band.getReservesUsd());
    }

    /* ================= 11. a real run, counted ================= */

    static void aRealRun() {
        out.println("\n--- 11. a real run, counted ---");

        Game g = town("xr2-run", Founding.defaults());
        quietly(() -> g.setCurrencyRegime(PEG));
        ForeignAccounts fx = g.getForeignAccounts();
        CapitalFlows hot = g.getCapitalFlows();
        CentralBank bank = g.getCentralBank();
        double peg = fx.getPegRate();
        assertTrue("fixture: the city is pegged and its edge's dollars are paid by the default payer, the central bank, in new money",
                fx.getRegime() == PEG && fx.getEdgePayer() == ForeignAccounts.EdgePayer.CENTRAL_BANK);
        int keptSomething = 0;   // the months the central bank missed keeping nothing, FX8's statement
        int months = 240, pegMonths = 0, held = 0, sold = 0, bought = 0, doubted = 0, brokeMonth = 0, notNumbers = 0, unclosed = 0, pegAfterBreak = 0;
        int heldOff = 0, breakMonthsPegged = 0, brokeAndDealt = 0, brokeWhileFloating = 0, heldAndBroke = 0;
        double soldUsd = 0, boughtUsd = 0, doubtedLocal = 0, jump = 0;
        ForeignAccounts.BreakCause cause = ForeignAccounts.BreakCause.NONE;
        for (int m = 1; m <= months; m++) {
            boolean pegged = fx.getRegime() == PEG;
            quietly(() -> g.simulateMonths(1));
            if (pegged) pegMonths++;
            if (pegged && fx.getRegime() == PEG) {
                held++;
                if (Double.compare(fx.getRate(), peg) != 0) heldOff++;
                if (fx.brokeThisMonth()) heldAndBroke++;
            }
            if (pegged && fx.getDefenceUsd() > 0) { sold++; soldUsd += fx.getDefenceUsd(); }
            if (pegged && fx.getEdgeBoughtUsd() > 0) { bought++; boughtUsd += fx.getEdgeBoughtUsd(); }
            if (pegged && hot.getDoubtDeparted() > 0) { doubted++; doubtedLocal += hot.getDoubtDeparted(); }
            if (fx.brokeThisMonth()) {
                brokeMonth = m;
                cause = fx.getLastBreakCause();
                jump = fx.getLastBreakJump();
                if (pegged && fx.getRegime() == FLOAT) breakMonthsPegged++;
                if (!pegged) brokeWhileFloating++;
                if (fx.getDefenceUsd() > 0 || fx.getEdgeBoughtUsd() > 0) brokeAndDealt++;
            }
            if (!pegged && fx.getRegime() == PEG) pegAfterBreak++;
            if (ExchangeRateCheck.notANumber(g) != null) notNumbers++;
            if (!ExchangeRateCheck.auditCloses(g)) unclosed++;
            if (keepsSomething(bank)) keptSomething++;
        }
        out.printf("   a city pegged at founding (%.4f D$ per US$) for %d months: held %d, the vault sold in %d (US$%,.0fk), bought in %d (US$%,.0fk),"
                + " the hot money left on doubt in %d (%,.0fk)%n", peg, months, held, sold, soldUsd, bought, boughtUsd, doubted, doubtedLocal);
        out.println(brokeMonth == 0 ? "   the peg never broke"
                : String.format("   the peg broke in month %d, by %s, the D$ going %+.1f%% in the month; it floats after (the rate ends %.4f)", brokeMonth,
                        cause.words, (Math.exp(jump) - 1) * 100, fx.getRate()));
        assertTrue("every figure of the city is a number every month, and the audit closes every month (" + notNumbers + " not numbers, " + unclosed + " unclosed)",
                notNumbers == 0 && unclosed == 0);
        assertTrue("fixture: the city was pegged for some months (" + pegMonths + ")", pegMonths > 0);
        assertTrue("every month the peg held the rate was the peg to the digit, and a month that held did not break", heldOff == 0 && heldAndBroke == 0);
        assertTrue("the peg broke at most once, and the count agrees with the model's", fx.getBreaks() == (brokeMonth > 0 ? 1 : 0)
                && brokeWhileFloating == 0);
        assertTrue("a break month is the last pegged month: it began pegged and ended on Float, nothing was pegged after it, and the months pegged are the months to the break",
                pegAfterBreak == 0 && (brokeMonth == 0 ? fx.getRegime() == PEG && pegMonths == months
                        : breakMonthsPegged == 1 && fx.getRegime() == FLOAT && pegMonths == brokeMonth));
        assertTrue("what was counted is consistent: the months held and the month that broke are the months pegged, and the vault sold or bought in no more of them than held",
                held + breakMonthsPegged == pegMonths && sold + bought <= held && doubted <= held + breakMonthsPegged);
        assertTrue("...a month the peg broke sold and bought nothing (its break let the rate go)", brokeAndDealt == 0);
        out.printf("   the central bank paid US$%,.0fk in new money for the vault's dollars (getDollarsBoughtLifetime(), local), keeping nothing in %d of %d months%n",
                bank.getDollarsBoughtLifetime(), months - keptSomething, months);
        assertTrue("the central bank keeps nothing, restated, in every one of the " + months + " months: equity less the vault plus what it paid for the vault's dollars"
                + " is the remittance due less the loss carried (" + keptSomething + " missed)", keptSomething == 0);
        assertTrue("the vault bought dollars at the peg (in " + bought + " months), so the central bank paid for some in new money: its lifetime of it is above 0,"
                + " and the term in the statement mattered", bought == 0 || bank.getDollarsBoughtLifetime() > 0);
    }

    /* ================= 12. the hot money's month crosses a save (FX8) ================= */

    /** Section 12's city, kept for section 13: saved to slot 2 of its own scratch folder in the month the hot money left on doubt, then played on. */
    static Game doubtCitySaved;

    static void theHotMoneysMonthCrossesASave() {
        out.println("\n--- 12. the hot money's month crosses a save ---");

        // A pegged city a fifth under its shadow: a doubt struck by one reprice acts the next month, so two months from the repeg.
        Game live = null;
        int tried = 0;
        for (int attempt = 0; attempt < 6 && live == null; attempt++) {
            tried++;
            Game candidate = doubtTown("fx8-hot-" + attempt, attempt == 0 ? 0 : SEED + attempt);
            play(candidate, 2);
            ForeignAccounts fx = candidate.getForeignAccounts();
            CapitalFlows hot = candidate.getCapitalFlows();
            if (fx.getRegime() == PEG && !fx.brokeThisMonth() && fx.getDoubtShare() > 0 && hot.getDoubtDeparted() > 0) live = candidate;
        }
        assertTrue("fixture: a pegged city held two months past its repeg, hot money leaving on doubt this month and a doubt struck for the next (attempt " + tried + ")",
                live != null);
        if (live == null) return;
        doubtCitySaved = live;
        final Game saved = live;
        ForeignAccounts fx = live.getForeignAccounts();
        CapitalFlows hot = live.getCapitalFlows();
        out.printf("   saved at month %d: hot money arrived %,.1fk, departed %,.1fk of which %,.1fk on doubt (%.2f%% of the stock), the stock %,.0fk%n", live.getMonth(),
                hot.getArrived(), hot.getDeparted(), hot.getDoubtDeparted(), fx.getDoubtShare() * 100, hot.getStock());
        assertTrue("fixture: the month it is saved in had hot money arrive, depart, and leave on doubt, and the doubt left is part of what departed",
                hot.getArrived() > 0 && hot.getDeparted() > 0 && hot.getDoubtDeparted() > 0 && hot.getDeparted() >= hot.getDoubtDeparted());
        assertTrue("saved to its scratch folder", live.saveGame(2, "hot-money").ok);
        final Game back = new Game(live.getGameFiles());
        quietly(() -> back.loadGameSave(2));
        CapitalFlows loaded = back.getCapitalFlows();
        ForeignAccounts bfx = back.getForeignAccounts();
        same("the loaded city reads the month's arrived as the live one does, to the bit", loaded.getArrived(), hot.getArrived());
        same("...the month's departed", loaded.getDeparted(), hot.getDeparted());
        same("...the month's doubtDeparted", loaded.getDoubtDeparted(), hot.getDoubtDeparted());
        same("...the net flow, which is the first two", loaded.netFlow(), hot.netFlow());
        same("...the doubt's lifetime", loaded.getDoubtDepartedLifetime(), hot.getDoubtDepartedLifetime());
        same("...the stock", loaded.getStock(), hot.getStock());
        // The figure Policy draws the month after: "of which ... was hot money leaving on doubt", in US$ (PolicyScreen).
        double drawn = fx.toUsd(hot.getDoubtDeparted());
        assertTrue("fixture: the figure Policy draws is a sum of money (US$" + String.format("%,.2fk", drawn) + ")", drawn > 0);
        same("the figure Policy draws for the doubt's departure, in US$, is the loaded city's as the live one's", bfx.toUsd(loaded.getDoubtDeparted()), drawn);
        assertTrue("...and the regime's state is as the live city's, to the bit (the doubt that acts next month is saved with it)",
                sameBits(regimeFields(fx), regimeFields(bfx)));

        // The restore must not change the replay: one more month of each, equal to the bit.
        quietly(() -> {
            saved.simulateMonths(1);
            back.simulateMonths(1);
        });
        CapitalFlows hot2 = saved.getCapitalFlows(), loaded2 = back.getCapitalFlows();
        out.printf("   a month on: the live city's hot money arrived %,.1fk, departed %,.1fk (%,.1fk on doubt); the loaded city's %,.1fk, %,.1fk (%,.1fk)%n", hot2.getArrived(),
                hot2.getDeparted(), hot2.getDoubtDeparted(), loaded2.getArrived(), loaded2.getDeparted(), loaded2.getDoubtDeparted());
        assertTrue("a month on, rate, vault, defence, the seven moves, M0, cash and credibility are equal to the bit in both cities", sameBits(figures(saved), figures(back)));
        assertTrue("...and so are the hot money's arrived, departed, doubtDeparted and its stock", Double.compare(hot2.getArrived(), loaded2.getArrived()) == 0
                && Double.compare(hot2.getDeparted(), loaded2.getDeparted()) == 0 && Double.compare(hot2.getDoubtDeparted(), loaded2.getDoubtDeparted()) == 0
                && Double.compare(hot2.getStock(), loaded2.getStock()) == 0 && Double.compare(hot2.getDoubtDepartedLifetime(), loaded2.getDoubtDepartedLifetime()) == 0);
        assertTrue("...and the audit closes in both (residuals " + String.format("%.4f and %.4f", saved.getLastMoneyAudit().residual, back.getLastMoneyAudit().residual) + ")",
                ExchangeRateCheck.auditCloses(saved) && ExchangeRateCheck.auditCloses(back));
    }

    /* ================= 13. leaving a peg takes its doubt with it (FX8) ================= */

    static void leavingAPegTakesItsDoubt() {
        out.println("\n--- 13. leaving a peg takes its doubt with it ---");

        // Bare: a quiet currency pegged a third under its shadow; the held month strikes the doubt (a quiet market never draws a break).
        ForeignAccounts pegged = book(TRADE, TRADE, DEEP, 0, PEG);
        pegged.repeg(pegged.getRate() * Math.exp(-.30));
        month(pegged, TRADE, TRADE);
        out.printf("   a peg a third under its shadow, held a month: gap %.3f, the doubt struck %.2f%%%n", pegged.getLastPegGap(), pegged.getDoubtShare() * 100);
        assertTrue("fixture: the peg held the month and the reprice struck a doubt (BREAK_GAP is " + ForeignAccounts.BREAK_GAP + ")",
                pegged.getRegime() == PEG && !pegged.brokeThisMonth() && pegged.getLastPegGap() > ForeignAccounts.BREAK_GAP && pegged.getDoubtShare() > 0);
        double struck = pegged.getDoubtShare();
        ForeignAccounts.Regime[] left = { FLOAT, LEAN, BAND };
        for (ForeignAccounts.Regime r : left) {
            ForeignAccounts copy = new ForeignAccounts();
            copy.restore(pegged.toSaveArray());
            assertTrue("fixture: a copy restored from the save is pegged with the same doubt (" + r + ")", copy.getRegime() == PEG && Double.compare(copy.getDoubtShare(), struck) == 0);
            copy.setRegime(r);
            assertTrue("leaving the peg for " + r + " leaves the regime " + r + " and no doubt: getDoubtShare() is 0", copy.getRegime() == r && copy.getDoubtShare() == 0);
        }
        ForeignAccounts stays = new ForeignAccounts();
        stays.restore(pegged.toSaveArray());
        stays.setRegime(PEG);
        assertTrue("setRegime(PEG) on a peg is no change: the doubt is kept", stays.getRegime() == PEG && Double.compare(stays.getDoubtShare(), struck) == 0);
        ForeignAccounts back = new ForeignAccounts();
        back.restore(pegged.toSaveArray());
        back.setRegime(LEAN);
        back.setRegime(PEG);
        assertTrue("leaving the peg and entering it again before any month does not revive the doubt (the new peg is at the rate of the day)",
                back.getRegime() == PEG && back.getDoubtShare() == 0 && Double.compare(back.getPegRate(), back.getRate()) == 0);

        // In a city: two copies of section 12's save, the month the hot money left on doubt. One leaves the peg between two presses.
        Game source = doubtCitySaved;
        assertTrue("fixture: section 12 left a saved city to load twins from", source != null);
        if (source == null) return;
        final Game kept = new Game(source.getGameFiles()), gone = new Game(source.getGameFiles());
        quietly(() -> {
            kept.loadGameSave(2);
            gone.loadGameSave(2);
        });
        ForeignAccounts keptFx = kept.getForeignAccounts(), goneFx = gone.getForeignAccounts();
        assertTrue("fixture: the twins are loaded alike - both pegged, the same doubt struck (" + String.format("%.2f%%", keptFx.getDoubtShare() * 100)
                        + "), hot money in the city - and alike to the bit",
                keptFx.getRegime() == PEG && goneFx.getRegime() == PEG && keptFx.getDoubtShare() > 0 && kept.getCapitalFlows().getStock() > 0
                        && sameBits(figures(kept), figures(gone)) && sameBits(regimeFields(keptFx), regimeFields(goneFx))
                        && Double.compare(kept.getCapitalFlows().getStock(), gone.getCapitalFlows().getStock()) == 0);
        quietly(() -> gone.setCurrencyRegime(LEAN));
        assertTrue("the twin that left the peg between two presses has no doubt to hand the hot money; the one that stayed has it still",
                goneFx.getRegime() == LEAN && goneFx.getDoubtShare() == 0 && keptFx.getRegime() == PEG && keptFx.getDoubtShare() > 0);
        play(kept, 1);
        play(gone, 1);
        double leftOnDoubt = kept.getCapitalFlows().getDoubtDeparted(), goneOnDoubt = gone.getCapitalFlows().getDoubtDeparted();
        out.printf("   the month after: the twin that stayed pegged lost %,.1fk of hot money on doubt, the twin that left lost %,.1fk (it departed %,.1fk in all)%n",
                leftOnDoubt, goneOnDoubt, gone.getCapitalFlows().getDeparted());
        assertTrue("the twin that stayed pegged saw hot money leave on doubt that month (the condition is caused: " + String.format("%,.1fk", leftOnDoubt) + ")", leftOnDoubt > 0);
        assertTrue("the twin that left the peg saw none leave on doubt: getDoubtDeparted() is 0 that month", goneOnDoubt == 0);
        assertTrue("...and the audit closes in both (residuals " + String.format("%.4f and %.4f", kept.getLastMoneyAudit().residual, gone.getLastMoneyAudit().residual) + ")",
                ExchangeRateCheck.auditCloses(kept) && ExchangeRateCheck.auditCloses(gone));
    }
}
