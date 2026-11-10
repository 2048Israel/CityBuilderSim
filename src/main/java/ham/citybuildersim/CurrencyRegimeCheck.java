package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Proves the D$'s four regimes - Float, Lean against falls (the default), Band
 * and Peg to the US$ - hold the currency where they say and cost what they
 * should (XR2, 2026-10-22; XR5, 2026-11-06): Lean is exactly the float the city
 * had before there were regimes, a peg holds to the digit while the vault pays
 * the pressure - the dollars that lift the rate to the peg by the interventions'
 * rule, not the month's balance - and gives way at the floor or by doubt at the
 * chance the gap makes it, landing past the shadow on whichever side it was, the
 * doubt takes the hot money home, a band's edges hold by the same dealing and its
 * inside is free, the dial is tied to the US's rate under a peg and the curve is
 * priced on that tied path, the autopilot's aim picks the rule, the currency aim
 * keeps the shadow inside a band, the strong edge's dollars are paid exactly by
 * the payer chosen, and all of it is saved. Not part of the game.
 *
 * WHY THIS EXISTS. Jerus, 2026-10-21 (docs/studies/spec-exchange-rates.md, 3.6;
 * his calls star 3 to 6 and 13): the city chooses what its D$ is held to. Star 3,
 * four regimes, Lean the default and the city's own history; star 4, a peg
 * breaks - when the vault cannot pay for the hold (Krugman's first generation)
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
 *   2. A PEG HOLDS TO THE DIGIT, AND THE VAULT PAYS THE PRESSURE (XR5). A
 *      settled currency pegged at its rate, then months of a deficit that
 *      weaken its shadow, then (a twin) of a surplus that strengthens it: every
 *      held month the rate is the peg exactly; the vault falls by exactly what
 *      interventionLocalFor() says the lift takes (the shadow, getHoldFelt() and
 *      the peg), and not the deficit; it rises by exactly the lift in the
 *      surplus (the edge's purchase), and what is owed is that purchase at the
 *      month's rate; the seven moves add to the month's log move and the held
 *      line holds it; while held the hold is the log of the peg over the shadow.
 *   3. A VAULT AT THE FLOOR BREAKS THAT MONTH, AT THE SHADOW PLUS OVERSHOOT.
 *      A peg whose hold would sell the vault under VAULT_FLOOR months of
 *      imports breaks (cause VAULT, nothing sold), its twin with a vault a
 *      little deeper holds; the rate lands at the shadow plus OVERSHOOT of its
 *      gap; in a city credibility falls by PEG_BREAK_DENT of its distance to
 *      KMIN. (The overshoot's two sides and its fade are section 16's.)
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
 *   6. A BAND'S EDGES HOLD AND ITS INSIDE IS FREE (XR5). A band narrower than
 *      the market's swing, a few hundred months: where the rate would stand with
 *      the hold's faded support (free) is past the weak edge the rate is the edge
 *      and the vault sold what the lift takes, past the strong edge it bought
 *      that, inside the rate is free and nothing is dealt; the centre crawls at
 *      the slope; a vault the sale would take under the floor lets the band
 *      give way.
 *   7. UNDER A PEG THE DIAL IS THE US'S RATE PLUS THE SPREAD; THE AIM. In a
 *      city: pegged, the policy rate is the US's rate (worldRate()) plus the spread, with
 *      the autopilot off or aiming at inflation; aiming at the currency it is
 *      the gap's; a band and the currency aim, a mix; Lean with any aim the
 *      inflation rule.
 *   8. THE CURRENCY AIM HOLDS A BAND, COUNTED OVER 1,000 SEEDED YEARS. A band
 *      with the dial neutral against the dial the currency aim sets: its share
 *      of months with the shadow inside is higher and its vault spends less.
 *   9. THE TOGGLE'S TWO PAYERS EACH PAY EXACTLY WHAT THE EDGE BOUGHT. Cities
 *      pegged with a large spread, the shadow under the peg and the vault
 *      buying the lift (XR5): the central bank's pays in new money and the treasury's does not, the treasury's pays out of
 *      its cash and the central bank's does not, the audit closes in both, and
 *      the central bank's equity is unmoved by the purchase once settled. FX8:
 *      the central bank keeps nothing, restated with what it paid in new money
 *      for the vault's dollars, in both cities every month, and the old
 *      statement misses by exactly that term; in the settlement month the
 *      vault's record (getLifetimeIntervention()) rises by the whole bill,
 *      whoever paid it.
 *  10. SAVED AND LOADED MID-PEG. A pegged city with a purchase owed and a doubt
 *      share struck, saved and loaded: the next 12 months equal the twin that
 *      never saved; a bare currency restores every regime field, the hold's
 *      support (slot 147, XR5) among them, and an array cut before it restores
 *      with none; an array cut before the regimes restores as Lean with nothing
 *      owed.
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
 *  14. A PEG PAYS THE PRESSURE, NOT THE MONTH'S BALANCE (XR5; Jerus,
 *      2026-10-23: "pay the pressure"). A bare currency with a GDP, pegged at
 *      its rate, in a SURPLUS month with a WEAK shadow (the city's prices 16%
 *      over the world's: setParity()): the vault falls - sold above 0, nothing
 *      bought - by exactly what the lift takes, where the month's-balance rule
 *      would have bought the surplus; the mirror, a deficit month with a strong
 *      shadow, the vault buys; a weak gap held a year of surplus months: the vault
 *      falls every month (the first the gap, each after it what the support's
 *      fading takes) and its total fall is the sum of the model's own figures; a
 *      gap past INTERVENTION_LINEAR of a year's output is sold on the bounded
 *      rule; a devaluation is a purchase and a revaluation a sale, and repeg()
 *      keeps the support; with no GDP the hold holds the rate and deals nothing.
 *  15. A BAND'S EDGES PAY THE PRESSURE (XR5). The weak edge in a surplus month
 *      sells and the strong edge in a deficit month buys, each exactly what the
 *      lift takes; inside the band nothing is dealt and the rate is the shadow
 *      times exp(getHoldFelt()), the hold fading by half in INTERVENTION_HALF_LIFE;
 *      under the floor the band gives way for the month and stays a band.
 *  16. THE OVERSHOOT BOTH WAYS (XR5). A peg broken by doubt with the shadow
 *      STRONGER than the peg lands at the shadow times exp(OVERSHOOT x the gap),
 *      past the shadow on the strong side, as a weak break lands past it on the
 *      weak side; the overshoot fades at MARKET_HALF_LIFE on either side, against a
 *      floating twin on the same market; nothing is dealt in the month of the
 *      break and the hold goes with the peg.
 *  17. THE TIED PATH PRICES THE CURVE (XR5). A DebtManager wired to a tied
 *      source, struck on inflation well off the target: the curve's expected dial is
 *      the floor's premium over the dial closing on the dial and not on the rule,
 *      so the long yields are the curve on the tied path; moving the US's rate and
 *      re-tying moves them; ruleNow() and settledDial() are the dial; untied (the
 *      source false, or never set) every path is the rule's to the bit; in a city
 *      the source is the peg.
 *  18. A THIN VAULT'S MONTHS ARE WHAT THE HOLD DOES (XR5; Jerus, "Warn on a thin
 *      vault"). A bare currency pegged with its shadow a steady gap over the peg,
 *      the vault deep: the first month's sale is the first month of
 *      monthsHoldingGap() and each month after it the each-month figure, to a
 *      millionth of a dollar, with the cover over THIN_COVER; a vault under the
 *      first month's cost is the share of it the headroom pays; played on to the
 *      break by the vault, the months it held are printed beside the projection
 *      struck at entry and held between a lower bound (the months before the
 *      cover fell under THIN_COVER, which the projection prices exactly) and the
 *      projection (it leaves the cover's premium out, which only adds pressure);
 *      no GDP gives infinity, a vault under its floor 0, and asking changes
 *      nothing.
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

    /** The four regimes, by their short names. */
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

    /** What a hold's support fades to by the next reprice: its log, times a half for each INTERVENTION_HALF_LIFE (the model's own constant). */
    static double faded(ForeignAccounts fx) {
        return fx.getHoldFelt() * Math.pow(.5, 1 / ForeignAccounts.INTERVENTION_HALF_LIFE);
    }

    /**
     * One month of a held currency (a peg or a band), played with the harness's own sums beside it (XR5, THE HOLD PAYS THE PRESSURE): what the
     * month stood on before the reprice, and what the hold should deal by the interventions' rule, read from the model's own getters AFTER the
     * reprice - the shadow, the support it faded to, the peg or the band's edges - and not from the model's step 7. The deal is dollars at the
     * rate before the reprice: negative a sale, positive a purchase. The deal of a peg that broke, or of a band that gave way, is what the
     * hold would have needed, not what was done.
     */
    static final class Held {
        double rateBefore, vaultBefore, faded, annual, balance, deal, free;

        /** The vault's change over the month: negative where it sold. */
        double vaultMoved(ForeignAccounts fx) { return fx.getReservesUsd() - vaultBefore; }
    }

    /** A month of this trade on a held currency, its parity this many logs over the world's if parityLog is a number (the lever that makes the shadow weak or strong). */
    static Held heldMonth(ForeignAccounts fx, double exports, double imports, double parityLog) {
        Held h = new Held();
        h.rateBefore = fx.getRate();
        h.vaultBefore = fx.getReservesUsd();
        h.faded = faded(fx);
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(exports, imports, 0, 0), GDP);
        h.annual = 12 * fx.getGdpTrailing();
        h.balance = balanceUsd(fx);
        if (!Double.isNaN(parityLog)) fx.setParity(Math.exp(parityLog), 1.0, 0, 0);
        fx.repriceCurrency();
        double shadow = fx.brokeThisMonth() ? fx.getLastBreakShadow() : fx.getShadowRate();
        h.free = Math.log(shadow) + h.faded;
        double target = fx.getPegRate() > 0 && (fx.getRegime() == PEG || fx.brokeThisMonth()) ? Math.log(fx.getPegRate())
                : Math.max(Math.log(fx.bandStrongEdge()), Math.min(Math.log(fx.bandWeakEdge()), h.free));
        h.deal = ForeignAccounts.interventionLocalFor(target - h.free, h.annual) / h.rateBefore;
        return h;
    }

    /** A trading town on the default founding, as ExchangeRateCheck builds it (its own copy: that one is tied to its harness's streams). */
    static Game town(String label, Founding founding) {
        return town(label, founding, 0);
    }

    /**
     * ...with that many more depots STANDING beside the four it orders (PT1-M4). The works department that built 400
     * points a month for a city nobody staffed is retired, and the four ordered depots are built by the founding
     * yard's 160 points at the crews it has: 196 of the 500 houses stood at month 61, 672 people, and the price index
     * (a basket based on a month the city shopped in, then thirteen readings) had two months to go - the year of
     * prices section 7's inflation rule reads. Two depots stood (addStack(t, n, true), as foundingDepot() once stood
     * its one) raise the houses: 2,193 people at month 61, and the index has its rate from month 55.
     */
    static Game town(String label, Founding founding, int depotsStanding) {
        return town(label, founding, depotsStanding, false);
    }

    /** ...with the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). */
    static Game town(String label, Founding founding, int depotsStanding, boolean causesHeldOff) {
        Game city = new Game(GameFiles.scratch(label), founding);
        if (causesHeldOff) city.holdCausesOfDeathOff();
        quietly(() -> {
            city.run();
            // THE LEVERS OPENED IN THE FIXTURE (BA1, 2026-11-05; R0 dates a 1900 city on Canada's policy): the peg fixtures stand on a town that taxes from month 1, whose
            // hot money, drawn by the dial, outruns its current account's deficit so that a month at the peg is a surplus (and, since XR5, strengthens the shadow under the peg: the vault buys the lift). A 1900 town has no income
            // tax until 1916-17 and no sales tax until 1920: with the merge the town's financial inflow at the peg is US$124k a month against a deficit on current account of
            // US$212k (the vault sells, 0 bought), and with every lever open it is US$326k (the vault buys US$115k the month after the peg). Opened, and the defaults put
            // back, before a month has run, as MonetaryCheck 5 and the cities of RailDepotCheck do.
            city.getEconomyManager().getTaxPolicy().reset();
            city.getEconomyManager().getTaxPolicy().openAll();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            if (depotsStanding > 0) city.buildStack(ForeignCheck.template(city, "Construction Depot"), depotsStanding, true);
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
        for (int m = 0; m < months; m++) {
            if (bandHeld) placeInBand(g);
            quietly(() -> g.simulateMonths(1));
        }
    }

    /**
     * GW1: whether play() puts the town's smoothed inflation on the target before each month (placeInBand()). Set by pegTown(climbing) once the town is pegged and
     * cleared by section 3 when its twin is found; the credibility fixture is the only user.
     */
    static boolean bandHeld;

    /**
     * ...the gain branch placed directly: the smoothed inflation, which Expectations.takeMonth() then moves a twelfth of the way to the month's yoy (ADAPT_MONTHS), so
     * the month's miss is a twelfth of the town's own and inside TOLERANCE while the index reads within 12 points of the target. On GW1's path the peg town is a young
     * town in a supply transient that does not stay in the band for any run of months: the index read -9% at month 41, -10% at 44, +10% at 108, and the smoothed
     * inflation (1% either side of the 2% target is the band) passed through it only at months 40, 108-110, 128-132, 208 and 262-266 of 296 played.
     */
    static void placeInBand(Game g) {
        Expectations e = g.getExpectations();
        double[] saved = e.toSaveArray();
        saved[1] = g.getDebtManager().getInflationTarget();
        e.restore(saved);
    }

    public static void main(String[] args) throws Exception {
        // LD2's wage off for the whole JVM (WantedPosts.carryKeptOut; LD2's notes 6): on LD2's path the pegged twins' month
        // after the peg brings no money in (the vault bought US$0k), so the settlement's fixtures and the seven lines on them
        // fail. The claim is the peg's payer, not the wage; queued (LD3) to cause the surplus at the peg on LD2's path.
        WantedPosts.carryKeptOut = false;
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
        aPegPaysThePressure();
        aBandsEdgesPayThePressure();
        theOvershootBothWays();
        theTiedPathPricesTheCurve();
        aThinVaultsMonthsAreWhatTheHoldDoes();

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

    /* ================= 2. a peg holds to the digit, and the vault pays the pressure ================= */

    static void aPegHolds() {
        out.println("\n--- 2. a peg holds to the digit, and the vault pays the pressure ---");

        /*
         * RESTATED FOR XR5 (Jerus, 2026-10-23: "pay the pressure"). Until it a peg met the month's whole balance - the vault sold a deficit and bought a
         * surplus at the peg - so a surplus month with a weak shadow BOUGHT dollars. Now the vault deals what holding the rate takes: the dollars that
         * lift the log rate from where it would stand with the hold's faded support (the shadow plus getHoldFelt() faded) to the peg, by the
         * interventions' rule (interventionLocalFor(), XI1), at the month's rate before the reprice. The month's balance reaches the vault only
         * through the shadow it weakens or strengthens. The sums below are the harness's own, from the model's getters after the reprice.
         */
        final double vault0 = 40_000;
        ForeignAccounts fx = book(TRADE, TRADE, vault0, 0, PEG);
        final double peg = fx.getPegRate();
        month(fx, TRADE, TRADE);   // the peg is the rate of the day and trade is balanced: no pressure
        same("fixture: a settled currency pegged at the rate of the day holds it", fx.getRate(), peg);
        assertTrue("fixture: balanced trade is no pressure and dealt nothing: the vault is where it was to a millionth of a dollar, nothing sold or bought",
                Math.abs(fx.getReservesUsd() - vault0) <= DOLLAR && fx.getDefenceUsd() <= DOLLAR && fx.getEdgeBoughtUsd() <= DOLLAR);

        // Twelve months of a deficit, which weakens the shadow.
        int heldMonths = 0, weakMonths = 0, soldMonths = 0, notTheDeficit = 0, holdMoved = 0, hazardRight = 0;
        double worstVault = 0, worstSold = 0, worstMoves = 0, worstHold = 0, worstGap = 0, worstFelt = 0, deficitSeen = 0;
        for (int i = 0; i < 12; i++) {
            Held h = heldMonth(fx, .8 * TRADE, TRADE, Double.NaN);
            double deficit = -h.balance;   // the model's own figure: the month's net outflow in dollars at the rate it is struck at
            if (fx.getRegime() == PEG && Double.compare(fx.getRate(), fx.getPegRate()) == 0) heldMonths++;
            if (fx.getLastPegGap() > 0) weakMonths++;
            if (fx.getDefenceUsd() > 0 && fx.getEdgeBoughtUsd() == 0) soldMonths++;
            deficitSeen = deficit;
            worstVault = Math.max(worstVault, Math.abs(h.vaultMoved(fx) - h.deal));
            worstSold = Math.max(worstSold, Math.abs(fx.getDefenceUsd() + h.deal));
            if (Math.abs(fx.getDefenceUsd() - deficit) > 1) notTheDeficit++;
            double six = fx.getMoveTrend() + fx.getMoveGap() + fx.getMoveBalance() + fx.getMoveCover() + fx.getMoveIntervention() + fx.getMoveMarket();
            worstMoves = Math.max(worstMoves, Math.abs(fx.getLastMove() - Math.log(fx.getRate() / h.rateBefore)));
            worstHold = Math.max(worstHold, Math.abs(six + fx.getMoveHeld()));   // the rate did not move: the hold is minus the shadow's move
            if (Math.abs(fx.getMoveHeld()) > 1e-6) holdMoved++;
            worstGap = Math.max(worstGap, Math.abs(fx.getLastPegGap() - Math.log(fx.getShadowRate() / fx.getPegRate())));
            worstFelt = Math.max(worstFelt, Math.abs(fx.getHoldFelt() - Math.log(fx.getPegRate() / fx.getShadowRate())));
            if (Double.compare(fx.getLastHazard(), ForeignAccounts.hazard(Math.max(0, fx.getLastPegGap()))) == 0) hazardRight++;
        }
        close("fixture: the month's deficit is 20% of imports at the pegged rate, in dollars", deficitSeen, .2 * TRADE / peg, DOLLAR);
        assertTrue("fixture: a deficit weakens the shadow: it stands over the peg in every one of the twelve months (" + weakMonths + ")", weakMonths == 12);
        assertTrue("a deficit, twelve months: every one the rate is getPegRate() exactly and the regime is the peg", heldMonths == 12);
        assertTrue("...the vault sold in every one and bought in none (" + soldMonths + " of 12)", soldMonths == 12);
        out.printf("   the vault: US$%,.2fk to US$%,.2fk; the worst gap from the lift's dollars %.2e; the month's deficit US$%,.2fk, the last month's sale US$%,.2fk%n", vault0,
                fx.getReservesUsd(), worstVault, deficitSeen, fx.getDefenceUsd());
        close("...the vault's dollars fell by exactly what the lift takes (interventionLocalFor() over the rate), each month, to a millionth of a dollar", worstVault, 0, DOLLAR);
        close("...and the defence sold exactly that", worstSold, 0, DOLLAR);
        assertTrue("...which is not the month's deficit: the vault paid the pressure, and the sale was never the deficit's US$" + String.format("%,.2fk", deficitSeen)
                + " (" + notTheDeficit + " of 12 months)", notTheDeficit == 12);
        close("...the hold is the lift: getHoldFelt() is the log of the peg over the shadow, each month (while held the rate is the shadow plus it)", worstFelt, 0, 1e-12);
        close("...the seven moves add to the month's log move in the rate, each month", worstMoves, 0, 1e-12);
        close("...the six forces and the hold add to nothing: the held line is what holds the rate where the six would take it", worstHold, 0, 1e-12);
        assertTrue("...and the hold did something: the held move was not nothing in " + holdMoved + " of 12 months", holdMoved > 6);
        close("...the gap from the peg is the log of the shadow over it", worstGap, 0, 1e-12);
        assertTrue("...and the month's chance of a break is hazard() of that gap, each month", hazardRight == 12);

        // Twelve months of a surplus, a twin: it strengthens the shadow, and the vault buys the lift. The owed is taken every second month, so it adds up between.
        ForeignAccounts su = book(TRADE, TRADE, vault0, 0, PEG);
        month(su, TRADE, TRADE);
        int strongMonths = 0, heldAgain = 0, boughtMonths = 0, payerRight = 0, addsUp = 0, notTheSurplus = 0;
        double worstRise = 0, worstBought = 0, worstOwed = 0, carried = 0, surplusSeen = 0;
        for (int i = 0; i < 12; i++) {
            Held h = heldMonth(su, 1.2 * TRADE, TRADE, Double.NaN);
            surplusSeen = h.balance;
            if (su.getRegime() == PEG && Double.compare(su.getRate(), su.getPegRate()) == 0) heldAgain++;
            if (su.getLastPegGap() < 0) strongMonths++;
            if (su.getEdgeBoughtUsd() > 0 && su.getDefenceUsd() == 0) boughtMonths++;
            if (Math.abs(su.getEdgeBoughtUsd() - h.balance) > 1) notTheSurplus++;
            worstRise = Math.max(worstRise, Math.abs(h.vaultMoved(su) - h.deal));
            worstBought = Math.max(worstBought, Math.abs(su.getEdgeBoughtUsd() - h.deal));
            worstOwed = Math.max(worstOwed, Math.abs(su.getEdgeBoughtLocal() - h.deal * h.rateBefore));
            if (su.getEdgeOwedPayer() == ForeignAccounts.EdgePayer.CENTRAL_BANK) payerRight++;
            double owed = su.getEdgeOwedLocal();
            if (Math.abs(owed - (carried + h.deal * h.rateBefore)) <= 1e-6) addsUp++;
            carried = 0;
            if (i % 2 == 1) {
                double taken = su.takeEdgeOwed();
                if (Math.abs(taken - owed) > 1e-9 || su.getEdgeOwedLocal() != 0) addsUp--;
            } else {
                carried = owed;
            }
        }
        close("fixture: the month's surplus is 20% of imports at the pegged rate, in dollars", surplusSeen, .2 * TRADE / peg, DOLLAR);
        assertTrue("fixture: a surplus strengthens the shadow: it stands under the peg in every one of the twelve months (" + strongMonths + ")", strongMonths == 12);
        assertTrue("a surplus, twelve months: the peg holds every one, and the vault bought in each and sold in none (" + boughtMonths + " of 12)", heldAgain == 12 && boughtMonths == 12);
        close("...the vault's dollars rose by exactly the lift (the edge's purchase), each month, to a millionth of a dollar", worstRise, 0, DOLLAR);
        close("...getEdgeBoughtUsd() is that purchase", worstBought, 0, DOLLAR);
        close("...and what is owed for it is the purchase at the month's rate", worstOwed, 0, 1e-6);
        assertTrue("...which is not the month's surplus (US$" + String.format("%,.2fk", surplusSeen) + "): the vault paid the pressure in every month (" + notTheSurplus + " of 12)",
                notTheSurplus == 12);
        assertTrue("...owed by the central bank, as it is unless the player says otherwise", payerRight == 12);
        assertTrue("...what is owed adds up until it is taken, and taking it clears it (" + addsUp + " of 12 months)", addsUp == 12);
        close("...a strong shadow is no pressure to break: the hazard is BREAK_BASE", su.getLastHazard(), ForeignAccounts.BREAK_BASE, 1e-15);
    }

    /* ================= 3. a vault at the floor breaks that month ================= */

    /** Two currencies, one on a peg and one floating, the same seed, trade in balance and a parity drifting weaker 5% a year: the pair a peg breaks beside. */
    static void driftMonth(ForeignAccounts fx, int m) {
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
        fx.setParity(Math.exp(.004 * (m + 1)), 1.0, .048, 0);
        fx.repriceCurrency();
    }

    /**
     * The founding the peg fixtures stand on: the default's, on the Historical path (EP1). These towns are read at months 36 to 60, the young town's deficit
     * against the world's prices, and on the Random path those prices are draws keyed by calendar year, so what the town sells abroad, and whether a
     * month at the peg is a surplus or a deficit, changed with the calendar's move to 1900 (the vault's dollars sold every month from the peg, the
     * basket not based before the first break) and not with anything the fixture is about. Historical's world in the stand-in century is the file's first
     * year held, with no draw in it: the same world whatever the key. A fixture causes its condition; the assertions' premises stand.
     */
    static Founding steady() {
        return Founding.defaults().withPath(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
    }

    /**
     * BA1, 2026-11-04: THE PEG TOWNS NO LONGER CAUSED THEIR CONDITION. Since BA1 took the caps off the businesses' orders (the price of the
     * builders' queue rations them, with the interest on the wait) the town of 500 houses behind four depots is built for years: its
     * sectors hold their profits instead of building and borrowing, and, owing nothing and finding the bank's deposit rate near nothing (the
     * bank lends little), send them abroad (OutwardInvestment): D$355k in the first month at the peg beside D$0.15k on main's path, D$12,600k
     * held abroad at month 37 beside D$121k. A month at the peg is then a deficit, not the surplus the fixtures say they stand on: with no
     * depots standing the vault of US$569k is spent in three months (cur -14, fin -90 a month) and the peg breaks by the VAULT, in every
     * one of 40 seeds. With builders enough the sectors build and borrow as on main's path: 9 to 15 depots standing do (6 and 30 do not), and
     * at 12 the first month at the peg buys US$136k. Even so the hot money that comes for the dial's carry (US$2,900k in eight months) outgrows
     * a vault of US$286k, and the peg breaks by the vault and not by doubt, so the vault is made PEG_VAULT_MONTHS of imports deeper before the
     * peg, bought by the treasury and forgotten by the rate (a set-up, not a deal, as PassThroughCheck's twins' is): twice COMFORTABLE_COVER.
     * The sections about doubt then read a break at the market's chance, and section 9 a surplus at the peg.
     */
    static final int PEG_DEPOTS = 12;
    static final double PEG_VAULT_MONTHS = 2 * ForeignAccounts.COMFORTABLE_COVER;

    /**
     * BA1, 2026-11-04, REDONE 2026-11-05: the months past its 36 that a town played for the credibility fixture is given at most to find prices in the
     * band where the bank's credibility climbs (a smoothed miss within TOLERANCE of the target, the gain branch of Expectations.takeMonth()).
     * The first mend (2026-11-04) waited out a dip: on the path of that day the first price readings fell (the index read -4% at month 39 and -8% at 45)
     * and the smoothed inflation sat under zero from month 42 to 80, so the town was played to month 81 and pegged there. THE BUILDERS-FIRST TEST THEN
     * COUNTED A DEPOT ON SITE TWICE (4f66f5fd: Sector.staffingWithOwnSites()); with it mended the young town builds its depots first again and the dip
     * is gone from the years the fixture reads: the smoothed inflation holds 2.0% to month 38, is 1.2% at 41 to 43, 2.1% at 49 and 2.8% at 53,
     * and the bank's credibility climbs from 0.83 at month 37 through the peg's eight months and the six after the repeg (the miss never
     * past TOLERANCE until the smoothed inflation passes 3% at month 54). The old rule (dip under target less two TOLERANCEs, then stand back
     * at it) played that town to month 107 and pegged it at a smoothed -0.05%, a credibility of 0.449 falling by 0.002 a month: no climb to
     * read, and none of the 40 seeds met section 3's twin. The town is now pegged as soon as it is in the band, which on this path is at once
     * (month 36), and the search over seeds finds the seed whose break is the doubt's.
     */
    static final int CLIMB_LOOK_MONTHS = 120;

    /** A trading town on the default founding as the peg fixtures need it (see PEG_DEPOTS), played its 36 months and its vault deepened: ready to be pegged. */
    static Game pegTown(String label) {
        return pegTown(label, false);
    }

    /** ...and, if `climbing`, played on until prices are in the band where the bank's credibility can climb (see CLIMB_LOOK_MONTHS). */
    static Game pegTown(String label, boolean climbing) {
        return pegTown(label, climbing, PEG_DEPOTS);
    }

    /** ...with this many depots standing (see PEG_DEPOTS, and surplusDepots() for a town that needs more). */
    static Game pegTown(String label, boolean climbing, int depots) {
        return pegTown(label, climbing, depots, false);
    }

    /** The month the climbing town was pegged and the smoothed inflation it read then (GW1: the log's figures). */
    static int pegMonth;
    static double pegSmoothed;

    /** ...with the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). */
    static Game pegTown(String label, boolean climbing, int depots, boolean causesHeldOff) {
        Game g = town(label, steady(), depots, causesHeldOff);
        play(g, 36);
        if (climbing) {
            bandHeld = false;
            double target = g.getDebtManager().getInflationTarget();
            // GW1: in the band means a price rate READ and the smoothed miss inside TOLERANCE. Until the basket gives a rate the smoothed inflation is the target
            // itself, so the wait was vacuous at month 36 and the town was pegged before its first readings: on GW1's path they fell (the index read -9% at month 41,
            // the smoothed inflation -4.8% by month 56), the credibility fell with them from 0.83 to 0.33, and none of 400 seeds met section 3's twin.
            for (int past = 0; past < CLIMB_LOOK_MONTHS && !(g.getPriceIndex().hasRate()
                    && Math.abs(g.getExpectations().getSmoothedInflation() - target) <= Expectations.TOLERANCE); past++) {
                play(g, 1);
            }
            pegMonth = g.getMonth();
            pegSmoothed = g.getExpectations().getSmoothedInflation();
            // ...and it does not stay there: pegged at month 40 (smoothed 1.15%), the credibility had fallen to 0.688 by the break two months after the revaluation (month 50),
            // the twin that held read 0.6697 against 0.6880 before, and the gain branch was not the month's. The band is held from here on (placeInBand()).
            bandHeld = true;
        }
        ForeignAccounts fx = g.getForeignAccounts();
        double bill = PEG_VAULT_MONTHS * fx.monthlyImports();
        quietly(() -> g.buyForeignCurrency(bill));
        fx.forgetDealing();
        return g;
    }

    /**
     * A city on the default founding pegged with the peg revalued a fifth under its shadow: the doubt fixture, a few months from its break at the peg's chance.
     * XR5 (THE HOLD PAYS THE PRESSURE): the eight months the hot money takes to gather were played at the peg of the day, a peg that held the rate a quarter over
     * a shadow the town's surplus and the dial's carry had made strong - and a hold pays for that by buying dollars every month, building a support (getHoldFelt())
     * the revaluation then has to sell back: the repeg a fifth under the shadow was a lift of 0.69 in logs, a sale of more than a year of the town's output, the vault
     * broke on it in the month, by the VAULT, and no doubt was ever struck. Now the peg follows the shadow month by month (a crawling peg, repegged each month to where the float's rule has
     * the D$), which builds no support and leaves the hot money its eight months of the dial's carry; the revaluation is then a sale of a tenth of a year's output
     * (INTERVENTION_LINEAR) and the support it builds is paid for out of a vault PEG_VAULT_MONTHS deep.
     */
    static Game doubtTown(String label, long reseed) {
        return doubtTown(label, reseed, false, false);
    }

    /** ...and, if `climbing`, pegged in the month prices are back near the target (pegTown(), CLIMB_LOOK_MONTHS); with the causes of 1900 held off from the first month where asked (R0b call 7: see savedMidPeg()). */
    static Game doubtTown(String label, long reseed, boolean climbing, boolean causesHeldOff) {
        Game g = pegTown(label, climbing, PEG_DEPOTS, causesHeldOff);
        quietly(() -> {
            g.setCurrencyRegime(PEG);
            g.getDebtManager().setPegSpread(.30);
        });
        for (int m = 0; m < 8; m++) {
            play(g, 1);
            quietly(() -> g.repeg(g.getForeignAccounts().getShadowRate()));   // the peg follows the shadow: the vault builds no support to sell back
        }
        if (reseed != 0) g.getForeignAccounts().setMarketSeed(reseed);
        quietly(() -> g.repeg(g.getForeignAccounts().getShadowRate() * Math.exp(-.20)));
        return g;
    }

    /**
     * The doubt town's twin: played to the month before `months`, its credibility (start[0]) read, its peg moved to a rate so weak the shadow sits far
     * under it, so that month nothing breaks by chance, and played the month.
     */
    static Game heldTwin(String label, long seed, int months, double[] start) {
        return heldTwin(label, seed, months, start, false);
    }

    static Game heldTwin(String label, long seed, int months, double[] start, boolean climbing) {
        Game b = doubtTown(label, seed, climbing, false);
        play(b, months - 1);
        start[0] = b.getExpectations().getCredibility();
        quietly(() -> b.repeg(b.getForeignAccounts().getShadowRate() * 1.5));
        play(b, 1);
        return b;
    }

    /**
     * A peg a few deficit months old, quiet, whose vault is then sold down to this many dollars by the treasury (a set-up, not a deal: forgotten by the
     * rate). XR5: a deficit holds a hold behind it - the shadow is weak, the vault has been selling - and a thin vault weakens the shadow further (the
     * cover's premium), so the sale its next month needs depends on the vault. (It is sold down only after the months that set the hold up: a peg with a
     * thin vault SELLS even in a balanced month, for the cover's premium alone, and would break in the setting-up. Section 3 before XR5 set a quiet peg up
     * with six balanced months at the vault it was to test, which dealt nothing then.)
     */
    static ForeignAccounts pegAtTheFloor(double vaultUsd) {
        ForeignAccounts fx = book(.8 * TRADE, TRADE, DEEP, 0, PEG);
        for (int i = 0; i < 3; i++) month(fx, .8 * TRADE, TRADE);
        fx.sellReserves((fx.getReservesUsd() - vaultUsd) * fx.getRate());
        fx.forgetDealing();
        return fx;
    }

    /** The dollars the hold needs to sell in the peg's next deficit month at this vault, from the harness's sums (Held): the sale that month, whether the peg then held or broke. */
    static double saleAt(double vaultUsd) {
        return -heldMonth(pegAtTheFloor(vaultUsd), .8 * TRADE, TRADE, Double.NaN).deal;
    }

    static void aVaultAtTheFloorBreaks() {
        out.println("\n--- 3. a vault at the floor breaks that month, at the shadow plus overshoot ---");

        // RESTATED FOR XR5: the peg breaks when the SALE holding it takes would leave the vault under VAULT_FLOOR months of imports, not when the month's deficit
        // would. The sale depends on the vault (the thin cover is the very pressure it pays), so the vault at which it leaves exactly the floor is found by
        // iterating v = floor + sale(v) from the model's own figures; a vault a dollar under it breaks, a dollar over it holds.
        double floor = pegAtTheFloor(1e6).vaultFloorUsd();
        double limit = floor + 1_000;
        for (int i = 0; i < 40; i++) limit = floor + saleAt(limit);
        double sale = saleAt(limit);
        out.printf("   the vault's floor (VAULT_FLOOR = %.0f months of imports) US$%,.2fk; the sale the hold needs of a vault right at the limit US$%,.2fk, so the limit is US$%,.2fk%n",
                ForeignAccounts.VAULT_FLOOR, floor, sale, limit);
        assertTrue("fixture: there is a floor and a sale, and the iteration settled: the limit less the sale is the floor to a hundredth of a dollar",
                floor > 0 && sale > 0 && Math.abs(limit - sale - floor) < .01);

        ForeignAccounts shortOfIt = pegAtTheFloor(limit - 1);
        ForeignAccounts deeper = pegAtTheFloor(limit + 1);
        assertTrue("fixture: both are pegged, quiet, with a deficit's hold behind them (the shadow over the rate): the first has a vault a dollar short of the limit's, the second a dollar over it",
                shortOfIt.getRegime() == PEG && deeper.getRegime() == PEG && !shortOfIt.isMarketSeeded() && !deeper.isMarketSeeded()
                        && Math.abs(shortOfIt.getReservesUsd() - (limit - 1)) <= DOLLAR && shortOfIt.isHeld() && shortOfIt.getShadowRate() > shortOfIt.getRate());

        double rateBefore = shortOfIt.getRate(), shadowBefore = shortOfIt.getShadowRate(), floorShort = shortOfIt.vaultFloorUsd(), floorDeep = deeper.vaultFloorUsd();
        Held hs = heldMonth(shortOfIt, .8 * TRADE, TRADE, Double.NaN);
        Held hd = heldMonth(deeper, .8 * TRADE, TRADE, Double.NaN);

        assertTrue("fixture: the sale the hold needs would leave the first (US$" + String.format("%,.2f", hs.vaultBefore + hs.deal) + ") under its floor and the second over it",
                hs.vaultBefore + hs.deal < floorShort && hd.vaultBefore + hd.deal > floorDeep);
        assertTrue("the peg whose vault the hold's sale would take under its floor breaks that month", shortOfIt.brokeThisMonth() && shortOfIt.getBreaks() == 1);
        assertTrue("...by the vault, not by doubt", shortOfIt.getLastBreakCause() == ForeignAccounts.BreakCause.VAULT);
        assertTrue("...the regime is Float after it", shortOfIt.getRegime() == FLOAT);
        assertTrue("...nothing was sold that month, and the vault is where it was",
                shortOfIt.getDefenceUsd() == 0 && Double.compare(shortOfIt.getReservesUsd(), hs.vaultBefore) == 0);
        assertTrue("...and the hold went with the peg: getHoldFelt() is 0", shortOfIt.getHoldFelt() == 0);
        double shadow = shortOfIt.getLastBreakShadow();
        double gap = Math.log(shadow / shortOfIt.getPegRate());
        assertTrue("fixture: the shadow was the weaker side of the peg (" + String.format("%.4f against %.4f", shadow, shortOfIt.getPegRate()) + "), so the overshoot lands over it",
                gap > .01);
        double landing = shadow * Math.exp(ForeignAccounts.OVERSHOOT * gap);
        close("...the rate it lands at is the shadow times exp(OVERSHOOT x the gap): " + String.format("%.6f", shortOfIt.getRate()),
                shortOfIt.getRate(), landing, 1e-12 * landing);
        close("...the jump it records is the log move of the rate that month", shortOfIt.getLastBreakJump(), Math.log(shortOfIt.getRate() / rateBefore), 1e-12);
        assertTrue("fixture: the hold had something to give way: the rate stood away from its shadow (" + String.format("%.4f against %.4f", rateBefore, shadowBefore) + ")",
                Math.abs(Math.log(shadowBefore / rateBefore)) > 1e-4);
        close("...and the hold gave way: the held move is last month's gap of the shadow over the rate", shortOfIt.getMoveHeld(),
                Math.log(shadowBefore / rateBefore), 1e-12);
        close("...and the seven moves add to the jump", shortOfIt.getLastMove(), shortOfIt.getLastBreakJump(), 1e-12);
        assertTrue("its twin, a vault two dollars deeper, holds: still a peg, the rate at the peg, nothing broken",
                deeper.getRegime() == PEG && !deeper.brokeThisMonth() && deeper.getBreaks() == 0 && Double.compare(deeper.getRate(), deeper.getPegRate()) == 0);
        close("...and its vault sold what the hold needed, to the dollar", deeper.getDefenceUsd(), -hd.deal, DOLLAR);
        double over = deeper.getReservesUsd() - deeper.vaultFloorUsd();
        assertTrue("...leaving it a dollar or so over its floor (US$" + String.format("%.2f", over) + ")", over > 0 && over < 3);

        // The other side of the peg: a gap the other way lands past the shadow too, on its strong side (XR5; the Swiss franc, 2015). A surplus and a parity falling by more than
        // half in 40 months put the shadow far under the peg for the rest of the run, where the chance of a break is BREAK_BASE a month.
        ForeignAccounts stronger = null;
        double strongerBefore = 0, strongerRate = 0;
        int seedsTried = 0, monthsPlayed = 0;
        for (long s = 1; s <= 600 && stronger == null; s++) {
            seedsTried++;
            ForeignAccounts fx = book(1.2 * TRADE, TRADE, DEEP, SEED + s, PEG);
            for (int m = 0; m < 300; m++) {
                double v = fx.getReservesUsd(), r = fx.getRate();
                fx.startMonth();
                fx.takeMonth(CurrencyCheck.month(1.2 * TRADE, TRADE, 0, 0), GDP);
                fx.setParity(Math.exp(-.8 * Math.min(1, (m + 1) / 40.0)), 1.0, -.2, 0);
                fx.repriceCurrency();
                monthsPlayed++;
                if (fx.brokeThisMonth()) { stronger = fx; strongerBefore = v; strongerRate = r; break; }
            }
        }
        out.printf("   a peg with a surplus and a parity falling away: the first chance break in %d seeds, %,d pegged months (BREAK_BASE a month: about one in %,.0f)%n",
                seedsTried, monthsPlayed, 1 / ForeignAccounts.BREAK_BASE);
        assertTrue("fixture: a peg broke by chance with the shadow stronger than the peg", stronger != null
                && stronger.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT && stronger.getLastBreakShadow() < stronger.getPegRate());
        if (stronger != null) {
            double gapS = Math.log(stronger.getLastBreakShadow() / stronger.getPegRate());
            double landingS = stronger.getLastBreakShadow() * Math.exp(ForeignAccounts.OVERSHOOT * gapS);
            close("the rate lands at the shadow times exp(OVERSHOOT x the gap), the gap negative: past the shadow on the strong side", stronger.getRate(), landingS,
                    1e-12 * stronger.getRate());
            assertTrue("...under the shadow (" + String.format("%.4f under %.4f"  , stronger.getRate(), stronger.getLastBreakShadow()) + "), and so under the peg", stronger.getRate() < stronger.getLastBreakShadow()
                    && stronger.getRate() < stronger.getPegRate());
            assertTrue("...Float after it, and nothing bought or sold that month (the vault is where it was): the hold went with the peg",
                    stronger.getRegime() == FLOAT && stronger.getDefenceUsd() == 0 && stronger.getEdgeBoughtUsd() == 0
                            && Double.compare(stronger.getReservesUsd(), strongerBefore) == 0 && stronger.getHoldFelt() == 0);
            close("...the D$ jumped stronger, by the log move recorded", stronger.getLastBreakJump(), Math.log(stronger.getRate() / strongerRate), 1e-12);
            assertTrue("...and it is a jump the right way: the break let the D$ go stronger", stronger.getLastBreakJump() < 0);
        }

        // In a city, credibility falls by PEG_BREAK_DENT of its distance to KMIN, against a twin where nothing broke.
        /*
         * THE BREAK MUST BE ONE OF DOUBT, WHERE CREDIBILITY IS CLIMBING (EM1, 2026-10-29). The twin below is held to the gain
         * branch of Expectations (a smoothed miss within TOLERANCE, the basket based and read), because the dent is taken out of
         * that month's climb; and the peg breaks at the market's chance, in a month that depends on the city's path. At 0.8.72
         * the default seed broke it by doubt in month 51, the year the climb began (month 50). On EM1's path the same seed
         * breaks it in month 47, three months before the basket gives a rate to read, and the twin's credibility cannot climb in
         * a month with nothing to be judged on. And the city's vault is drained faster (US$19M at month 46 beside main's US$28M,
         * on a current account of -1.9M a month), so most breaks past month 53 are the vault's (BreakCause.VAULT), which the
         * twin's repegged peg meets as well, and not the doubt this section is about.
         * The fixture is made to cause it: a seed whose break is by doubt and follows a month the city's credibility climbed in
         * (its step above nothing), the default seed first and then SEED + 1, + 2, ... - on EM1's path SEED + 11, in month 52.
         * AND WHOSE TWIN HOLDS (EP1): the search takes the twin's conditions as it takes the city's, because the first seed that met the city's
         * (SEED + 3, month 54, on the calendar of 1900) left the vault at US$48.3k against a floor of 38.7k and a month's deficit of 22.4k, so
         * the twin's repegged peg broke by the vault too and its credibility fell as the city's did - no difference to read the dent in.
         */
        Game a = null, b = null;
        double c0 = 0;
        long creditSeed = 0;
        int k = 0;
        final int seeds = 40;
        for (int attempt = 0; attempt < seeds && a == null; attempt++) {
            long tried = attempt == 0 ? 0 : SEED + attempt;
            Game candidate = doubtTown("xr2-credibility-a-" + attempt, tried, true, false);
            int months = 0;
            boolean climbing = false;
            while (months < 40 && !candidate.getForeignAccounts().brokeThisMonth()) {
                climbing = candidate.getExpectations().getCredibilityStep() > 0;   // the month before the one about to be played
                months++;
                play(candidate, 1);
            }
            boolean fits = candidate.getForeignAccounts().brokeThisMonth() && climbing
                    && candidate.getForeignAccounts().getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT;
            Game twin = null;
            double[] start = new double[1];
            if (fits || attempt == seeds - 1) {
                twin = heldTwin("xr2-credibility-b-" + attempt, tried, months, start, true);
                fits = fits && !twin.getForeignAccounts().brokeThisMonth()
                        && Math.abs(twin.getExpectations().getCredibility() - (start[0] + (Expectations.KMAX - start[0]) / Expectations.GAIN_MONTHS)) <= 1e-12;
            }
            if (attempt == seeds - 1 || fits) {
                a = candidate;
                b = twin;
                c0 = start[0];
                creditSeed = tried;
                k = months;
            }
        }
        bandHeld = false;
        out.printf("   credibility fixture: pegged at month %d (smoothed inflation %.4f, target band +-%.2f), seed %d, break %d months after the revaluation%n",
                pegMonth, pegSmoothed, Expectations.TOLERANCE, creditSeed, k);
        assertTrue("fixture: the doubt city's peg broke within " + k + " months of the revaluation", a.getForeignAccounts().brokeThisMonth());
        if (a.getForeignAccounts().brokeThisMonth()) {
            double afterA = a.getExpectations().getCredibility();
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
        assertTrue("the saved array is 24 slots long: XR2's 16, the month's three and XR4's five (what ran home in a risk-off)", saved.length == SAVE_SLOTS);
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
            // XR4: a risk-off in force sends home its share of what the doubt left, so the stock the doubt took its share of is what remains and what the risk-off took.
            assertTrue("the month's doubt departure is the stock times the share the last reprice struck (the share of the stock after its own move)",
                    departed > 0 && Math.abs(departed / (left + departed + hot.getRiskOffDeparted()) - shareStruck) <= 1e-9);
            assertTrue("...it is inside what departed that month, and the lifetime rose by it", hot.getDeparted() >= departed - 1e-9
                    && Math.abs(hot.getDoubtDepartedLifetime() - lifetimeBefore - departed) <= 1e-9);
            assertTrue("...and the audit closes (residual " + String.format("%.4f", g.getLastMoneyAudit().residual) + ")", ExchangeRateCheck.auditCloses(g));
        }
    }

    /** Where CapitalFlows.toSaveArray() puts the doubt's lifetime (XR2) and the month's three flows (FX8), and how long the array is (XR4 appended slots 19 to 23 after them). */
    static final int SLOT_DOUBT_LIFETIME = 15, SLOT_ARRIVED = 16, SLOT_DEPARTED = 17, SLOT_DOUBT_DEPARTED = 18, SAVE_SLOTS = 24;

    static CapitalFlows restored(CapitalFlows f) {
        CapitalFlows back = new CapitalFlows();
        back.restore(f.toSaveArray());
        return back;
    }

    /* ================= 6. a band's edges hold and its inside is free ================= */

    /** A band this wide centred on the rate of the day, quiet, whose vault is sold down to this many dollars by the treasury (a set-up, not a deal: forgotten by the rate). */
    static ForeignAccounts bandAtTheFloor(double vaultUsd, double width) {
        ForeignAccounts fx = book(.8 * TRADE, TRADE, DEEP, 0, BAND);
        fx.setBand(fx.getRate(), 0, width);
        fx.sellReserves((fx.getReservesUsd() - vaultUsd) * fx.getRate());
        fx.forgetDealing();
        return fx;
    }

    /** The dollars the band's hold needs to sell in its first deficit month at this vault, from the harness's sums (Held): what it would sell, whether it then sold or gave way. */
    static double bandSaleAt(double vaultUsd, double width) {
        return -heldMonth(bandAtTheFloor(vaultUsd, width), .8 * TRADE, TRADE, Double.NaN).deal;
    }

    static void aBandsEdgesHold() {
        out.println("\n--- 6. a band's edges hold and its inside is free ---");

        /*
         * RESTATED FOR XR5 (THE HOLD PAYS THE PRESSURE). The band no longer sells the month's deficit past its weak edge and buys the month's surplus past
         * the strong one: it deals what puts the rate back at the edge. FREE is where the rate would stand with the hold's faded support and nothing new
         * dealt (the shadow's log plus getHoldFelt() faded, by the harness's reckoning); past the weak edge the vault sells the dollars that lift it
         * back to the edge by the interventions' rule, past the strong edge it buys those, inside it deals nothing and the rate is free - the shadow
         * plus what the hold dealt before and has not yet faded. So a month's class is read from free and not from the shadow, and the dollars are the
         * rule's (interventionLocalFor() at the rate before the reprice), not the month's deficit or surplus.
         */
        final double width = 2 * ForeignAccounts.MARKET_SIGMA;
        ForeignAccounts band = book(TRADE, TRADE, DEEP, SEED, BAND);
        band.setBand(band.getRate(), 0, width);
        ForeignAccounts flt = book(TRADE, TRADE, DEEP, SEED, FLOAT);
        assertTrue("fixture: the band is " + String.format("%.1f%%", width * 100) + " either side of the rate of the day, narrower than the market's swing",
                band.getRegime() == BAND && Math.abs(band.getBandWidth() - width) <= 1e-15 && Double.compare(band.getBandCentre(), band.getRate()) == 0);

        int weak = 0, weakSold = 0, strong = 0, strongBought = 0, inside = 0, bad = 0, shadowApart = 0, insideApart = 0, fadeApart = 0;
        double worstDeal = 0;
        for (int m = 0; m < 400; m++) {
            double exports = TRADE * (1 + .3 * Math.sin(m / 7.0)), imports = TRADE * (1 + .3 * Math.cos(m / 11.0));
            Held h = heldMonth(band, exports, imports, Double.NaN);
            month(flt, exports, imports);
            double weakEdge = band.bandWeakEdge(), strongEdge = band.bandStrongEdge(), shadow = band.getShadowRate();
            if (Math.abs(shadow - flt.getRate()) > 1e-12 * shadow) shadowApart++;
            if (h.free > Math.log(weakEdge)) {
                weak++;
                if (band.getDefenceUsd() > 0) weakSold++;
                boolean ok = Double.compare(band.getRate(), weakEdge) == 0 && band.getEdgeBoughtUsd() == 0;
                worstDeal = Math.max(worstDeal, Math.abs(band.getDefenceUsd() + h.deal));
                worstDeal = Math.max(worstDeal, Math.abs(h.vaultMoved(band) - h.deal));
                if (!ok) bad++;
            } else if (h.free < Math.log(strongEdge)) {
                strong++;
                if (band.getEdgeBoughtUsd() > 0) strongBought++;
                boolean ok = Double.compare(band.getRate(), strongEdge) == 0 && band.getDefenceUsd() == 0;
                worstDeal = Math.max(worstDeal, Math.abs(band.getEdgeBoughtUsd() - h.deal));
                worstDeal = Math.max(worstDeal, Math.abs(h.vaultMoved(band) - h.deal));
                if (!ok) bad++;
            } else {
                inside++;
                boolean ok = Math.abs(band.getRate() - Math.exp(h.free)) <= 1e-12 * band.getRate()
                        && band.getDefenceUsd() == 0 && band.getEdgeBoughtUsd() == 0 && Double.compare(band.getReservesUsd(), h.vaultBefore) == 0;
                if (!ok) insideApart++;
                if (Double.compare(band.getHoldFelt(), h.faded) != 0) fadeApart++;
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
        close("...the vault sold exactly the lift's dollars at the weak edge and bought exactly the lift's at the strong, in dollars", worstDeal, 0, DOLLAR);
        assertTrue("inside the band the rate is free - the shadow times exp(the hold faded) - and nothing is dealt: the vault, sold and bought are where they were", insideApart == 0);
        assertTrue("...and the hold inside is exactly what it faded to: nothing new dealt, nothing added to it, to the bit", fadeApart == 0);

        // The centre crawls.
        ForeignAccounts crawl = book(TRADE, TRADE, DEEP, 0, BAND);
        double c0 = crawl.getBandCentre();
        crawl.setBand(c0, .06, .05);
        for (int i = 0; i < 12; i++) month(crawl, TRADE, TRADE);
        close("the centre crawls at the slope: after 12 reprices it is the centre times exp(slope)", crawl.getBandCentre(), c0 * Math.exp(.06), 1e-12);
        close("...and the edges are the centre's width either side of it", crawl.bandWeakEdge() / crawl.bandStrongEdge(), (1 + .05) / (1 - .05), 1e-12);

        // A vault the sale would take under the floor at the weak edge: the band gives way that month, and stays a band. The sale depends on the vault (a thin cover is
        // part of the pressure), so the vault at which it leaves exactly the floor is found by iterating v = floor + sale(v), as section 3 does.
        final double narrow = .01;
        double floor = bandAtTheFloor(1e6, narrow).vaultFloorUsd();
        double limit = floor + 1_000;
        for (int i = 0; i < 40; i++) limit = floor + bandSaleAt(limit, narrow);
        double need = bandSaleAt(limit, narrow);
        assertTrue("fixture: there is a floor and a sale, and the iteration settled: the limit less the sale is the floor to a hundredth of a dollar",
                floor > 0 && need > 0 && Math.abs(limit - need - floor) < .01);
        ForeignAccounts under = bandAtTheFloor(limit - 1, narrow), over = bandAtTheFloor(limit + 1, narrow);
        ForeignAccounts floating = bandAtTheFloor(limit - 1, narrow);
        floating.setRegime(FLOAT);
        double underVault = under.getReservesUsd(), floorUnder = under.vaultFloorUsd(), floorOver = over.vaultFloorUsd();   // the floor at the rate before the reprice, as the model reads it
        Held hu = heldMonth(under, .8 * TRADE, TRADE, Double.NaN);
        Held ho = heldMonth(over, .8 * TRADE, TRADE, Double.NaN);
        month(floating, .8 * TRADE, TRADE);
        assertTrue("fixture: the shadow is past the weak edge in both, and the sale would leave the first under its floor and the second over it",
                floating.getRate() > over.bandWeakEdge() && floating.getRate() > under.bandWeakEdge()
                        && hu.vaultBefore + hu.deal < floorUnder && ho.vaultBefore + ho.deal > floorOver);
        assertTrue("a vault the hold's sale would take under its floor: the band gives way that month, the regime still Band, nothing sold",
                under.getRegime() == BAND && !under.brokeThisMonth() && under.getDefenceUsd() == 0 && Double.compare(under.getReservesUsd(), underVault) == 0);
        same("...the rate is the shadow (the floating twin's rate), not the edge", under.getRate(), floating.getRate());
        assertTrue("...past the edge it was to hold", under.getRate() > under.bandWeakEdge());
        close("...and the hold has no support to show for a sale never made: getHoldFelt() is left as it faded, 0", under.getHoldFelt(), 0, 0);
        same("its twin two dollars deeper holds the weak edge", over.getRate(), over.bandWeakEdge());
        close("...selling the lift's dollars", over.getDefenceUsd(), -ho.deal, DOLLAR);
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

        // Two depots stand beside the four ordered (PT1-M4): see town(); without them the price index has no rate at month 60.
        Game g = town("xr2-dial", Founding.defaults(), 2);
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
        same("a peg, the autopilot off: the policy rate is the US's rate (worldRate()) plus the spread", dialAfter(g), dm.worldRate() + spread);

        // ...the autopilot on, aiming at inflation: the same, whatever the inflation rule would say.
        quietly(() -> { dm.setAutopilot(true); dm.setAim(DebtManager.Aim.INFLATION); });
        pegWithGap(g, .06);
        double rule = dm.advisedPolicyRate(g.getPriceIndex().inflation());
        assertTrue("fixture: the inflation rule would set a different rate (" + String.format("%.4f against %.4f", rule, dm.worldRate() + spread) + ")",
                Math.abs(rule - (dm.worldRate() + spread)) > 1e-4);
        double gapBefore = fx.currencyGap();
        same("a peg, the autopilot on, aiming at inflation: still the US's rate plus the spread", dialAfter(g), dm.worldRate() + spread);
        assertTrue("fixture: the gap the aim ignored was not nothing (" + String.format("%.4f", gapBefore) + ")", gapBefore > .05);

        // ...aiming at the currency: the gap's spread, the player's own set aside.
        quietly(() -> dm.setAim(DebtManager.Aim.CURRENCY));
        pegWithGap(g, .06);
        double gap = fx.currencyGap();
        double expected = dm.tiedRate(gap);
        double bare = Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.worldRate() + DebtManager.CURRENCY_WEIGHT * gap));
        assertTrue("fixture: the gap is " + String.format("%.4f", gap) + " and the dial it asks is inside its bounds, over the world's rate",
                gap > .05 && expected > DebtManager.MIN_POLICY_RATE && expected < DebtManager.MAX_POLICY_RATE && expected > dm.worldRate());
        same("a peg, the autopilot aiming at the currency: the policy rate is tiedRate(currencyGap())", dialAfter(g), expected);
        close("...and that is the US's rate plus CURRENCY_WEIGHT of the gap, inside the dial's bounds", expected, bare, 1e-15);

        // ...but with the autopilot off the aim means nothing: the spread is the player's.
        quietly(() -> dm.setAutopilot(false));
        pegWithGap(g, .06);
        same("a peg, the autopilot off whatever the aim: the player's spread still", dialAfter(g), dm.worldRate() + spread);
        quietly(() -> dm.setAutopilot(true));

        // A band: the aim picks the rule.
        quietly(() -> dm.setAim(DebtManager.Aim.CURRENCY));
        bandWithGap(g, .05);
        gap = fx.currencyGap();
        expected = dm.currencyRate(gap);
        assertTrue("fixture: a band, the shadow " + String.format("%.4f", gap) + " past its centre, the autopilot aiming at the currency",
                fx.getRegime() == BAND && gap > .04 && expected > dm.worldRate());
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

    /**
     * BA1, 2026-11-05: THE DEPOTS A SURPLUS TOWN STANDS ON. A month at the peg is a surplus only where the hot money the dial draws
     * (the financial account) outruns the current account's deficit, and the two move together with the builders: with 12 depots standing
     * the first month at the peg is US$210.8k out on current account and US$180.4k in on financial, so the vault bought nothing (0.0k) and the
     * section's bill was 0; since the builders-first test stopped counting a depot on site twice (4f66f5fd) the town of 500 houses needs more
     * builders than the 12 PEG_DEPOTS stood on 2026-11-04 (US$136k bought then): 18 buy US$6.8k, 19 13.2k, 20 17.1k, 22 40.0k, 24 19.7k, and
     * 12, 16, 17, 25 and 30 nothing - a surplus that comes and goes with the count, a hair either side of a deficit. The count is therefore
     * SEARCHED for: the first of these whose first month at the peg owes the edge's bill the section asserts on (more than 100 x
     * PriceLevel.FACTOR of local money), found once on a trial town and kept for the cities of the section.
     *
     * XR5, 2026-11-06 (THE HOLD PAYS THE PRESSURE): the vault no longer buys the month's surplus, it buys the dollars that bring the rate up to the peg
     * when the shadow stands UNDER it. The same towns still buy, for the pressure's reason: at the first month at the peg the dial is tied to the US's
     * rate plus the spread (30 points), the carry draws the hot money, and the shadow stands 0.7879 under a peg of 1.0292 at 12 depots - a lift of 0.267 in
     * logs, bought as US$952k. The search still asks for the bill; the fixture assertion below now asks for its cause, the shadow under the peg.
     */
    static final int[] SURPLUS_DEPOTS = { PEG_DEPOTS, 18, 20, 22, 24, 30, 36 };
    static int surplusDepotsFound = 0;

    static int surplusDepots() {
        if (surplusDepotsFound == 0) {
            for (int depots : SURPLUS_DEPOTS) {
                Game trial = pegTown("xr2-surplus-trial-" + depots, false, depots);
                quietly(() -> {
                    trial.setCurrencyRegime(PEG);
                    trial.getDebtManager().setPegSpread(.30);
                });
                play(trial, 1);
                if (trial.getForeignAccounts().getEdgeOwedLocal() > 100 * PriceLevel.FACTOR) {
                    surplusDepotsFound = depots;
                    break;
                }
            }
            if (surplusDepotsFound == 0) surplusDepotsFound = PEG_DEPOTS;   // none met it: section 9's own fixture assertion says so
        }
        return surplusDepotsFound;
    }

    /** A city pegged at 36 months with a large positive spread: the dial high, money arriving, the shadow under the peg and the vault buying (XR5: the lift, not the month's surplus). */
    static Game surplusTown(String label, ForeignAccounts.EdgePayer payer) {
        Game g = pegTown(label, false, surplusDepots());
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
        assertTrue("fixture: money arrived and the vault bought dollars at the peg, the same in both cities (the shadow under the peg, " + String.format("%.4f under %.4f", cbFx.getShadowRate(), cbFx.getPegRate())
                        + ": the hold bought the lift), and the payer is recorded with it",
                owed > 100 * PriceLevel.FACTOR && Double.compare(owed, trFx.getEdgeOwedLocal()) == 0 && cbFx.getEdgeOwedPayer() == ForeignAccounts.EdgePayer.CENTRAL_BANK
                        && trFx.getEdgeOwedPayer() == ForeignAccounts.EdgePayer.TREASURY && cbFx.getEdgeBoughtUsd() > 0 && cbFx.getLastPegGap() < 0);
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
        close("...M0 moved by what was made less what was taken back, to the cent", cb.getCentralBank().m0() - m0Before, issued, MoneyAudit.CENT);
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
                        && Math.abs(cb.getLastMoneyAudit().residual) <= MoneyAudit.CENT && Math.abs(tr.getLastMoneyAudit().residual) <= MoneyAudit.CENT);

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

    /** The regime's state as the getters say it: every figure XR2 added to the currency, and XR5's hold, as one row. */
    static double[] regimeFields(ForeignAccounts fx) {
        return new double[] { fx.getRegime().ordinal(), fx.getEdgePayer().ordinal(), fx.getPegRate(), fx.getBandCentre(), fx.getBandSlope(),
                fx.getBandWidth(), fx.getShadowRate(), fx.isHeld() ? 1 : 0, fx.getDoubtShare(), fx.getLastHazard(), fx.getLastPegGap(),
                fx.getEdgeOwedLocal(), fx.getEdgeOwedPayer().ordinal(), fx.getEdgeBoughtUsd(), fx.getEdgeBoughtLocal(),
                fx.getEdgeBoughtUsdLifetime(), fx.getBreaks(), fx.getLastBreakCause().ordinal(), fx.getLastBreakJump(),
                fx.getLastBreakShadow(), fx.brokeThisMonth() ? 1 : 0, fx.getMoveHeld(), fx.getHoldFelt() };
    }

    static void savedMidPeg() {
        out.println("\n--- 10. saved and loaded mid-peg ---");

        /*
         * ...AT THE FIRST MONTH IT HAS ALL FOUR (EM1, 2026-10-29). A purchase is owed only in a month the peg bought dollars at its
         * edge, and the peg's chance of breaking is the market's: at 0.8.72 the first month after the revaluation had a purchase owed
         * (month 46, D$423k), and on EM1's path it has none in the seeds tried - the first purchase owed is in month 55, and the
         * default seed and SEED + 1 and + 2 break before it. The fixture is made to cause it: each candidate is played up to a year
         * (the twelve months the section plays on after the save) until the month it is pegged, held, in doubt and owes a purchase;
         * a peg that breaks first is no candidate.
         */
        /*
         * ...WITH THE WORLD WANTING THE CITY'S MONEY (WN3, after EM1). The young town's month at the peg is a deficit (its companies'
         * buybacks bought from the world took D$549k out of the city in month 46, against imports of D$608k), so the vault of
         * the peg sells dollars every month, from US$18.8k at month 46 to US$3.9k at 55, and on the Random path every candidate that
         * has outlived the doubt gives out by the vault at month 56 - one month before credibility has climbed and hot money floods
         * in (month 57, D$1.3-1.5M owed), which is the first month with a purchase owed. The world is given an appetite for the
         * city, as FloatLinkCheck's played city is given one (a tenth of the hot money it had, as early in a boom): the dollars it
         * brings keep the vault above its floor to month 57, and the candidates that survive the doubt (SEED + 2 and + 3) are pegged,
         * held, in doubt and owe a purchase in the month the loop's year ends.
         */
        /*
         * ...ON TODAY'S DEATHS (R0b call 7). The candidates are played with the causes of 1900 held off, from their first month, and the
         * city loaded from the one that qualifies is held too (the hold is not saved). The year's search needs a month with all four, and
         * the first purchase owed is a month of this young town's trade, sized on today's deaths; on 1900's causes no candidate of the six
         * was pegged, held, in doubt and owing a purchase in the twelve months it was played.
         */
        Game live = null;
        for (int attempt = 0; attempt < 6 && live == null; attempt++) {
            Game candidate = doubtTown("xr2-save-" + attempt, attempt == 0 ? 0 : SEED + attempt, false, true);
            FloatLinkCheck.worldWantsPaper(candidate);
            ForeignAccounts fx = candidate.getForeignAccounts();
            for (int m = 0; m < 12 && live == null && !fx.brokeThisMonth(); m++) {
                play(candidate, 1);
                if (fx.getRegime() == PEG && fx.getEdgeOwedLocal() > 0 && fx.getDoubtShare() > 0 && fx.isHeld()) live = candidate;
            }
        }
        assertTrue("fixture: a pegged city with a purchase owed, a doubt share struck and the rate held away from its shadow", live != null);
        if (live != null) {
            final Game saved = live;
            ForeignAccounts fx = live.getForeignAccounts();
            out.printf("   saved at month %d: owed %,.1fk, doubt %.2f%%, the shadow %.4f over a peg of %.4f%n", live.getMonth(), fx.getEdgeOwedLocal(),
                    fx.getDoubtShare() * 100, fx.getShadowRate(), fx.getPegRate());
            assertTrue("saved to its scratch folder", live.saveGame(2, "mid-peg").ok);
            Game back = new Game(live.getGameFiles());
            back.holdCausesOfDeathOff();   // R0b call 7: as the candidates; the hold is not saved
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

        // The hold's support is slot 147, the last (XR5): a band whose edges dealt carries one, and an array cut before it restores a hold with none.
        assertTrue("fixture: the band's edges have dealt and it carries a support (getHoldFelt() " + String.format("%.4f", band.getHoldFelt()) + "), the array 152 slots long, gold's three (149-151, GD2) after the peg's anchor (slot 148, WN4b)",
                band.getHoldFelt() != 0 && full.length == 152);
        same("slot 147 is the hold's support, and restore() reads it back to the bit", full[147], band.getHoldFelt());
        ForeignAccounts cut = new ForeignAccounts();
        cut.restore(Arrays.copyOf(full, 147));
        assertTrue("an array cut at slot 146, as XR4 wrote it, restores with no support (a hold just entered) and the rest of the regime as saved",
                cut.getHoldFelt() == 0 && cut.getRegime() == BAND && Double.compare(cut.getBandCentre(), band.getBandCentre()) == 0
                        && Double.compare(cut.getEdgeOwedLocal(), band.getEdgeOwedLocal()) == 0);
        double[] garbled = full.clone();
        garbled[147] = Double.NaN;
        ForeignAccounts nan = new ForeignAccounts();
        nan.restore(garbled);
        assertTrue("a support that is not a number reads as none", nan.getHoldFelt() == 0);
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
        // The figure Policy draws the month after: "... of hot money left on doubt, which weakens the shadow", in US$ (PolicyScreen).
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

    /* ================= 14. a peg pays the pressure, not the month's balance (XR5) ================= */

    static void aPegPaysThePressure() {
        out.println("\n--- 14. a peg pays the pressure, not the month's balance ---");

        /*
         * XR5, 2026-11-06 (Jerus, 2026-10-23: "pay the pressure"). Until it a peg met the month's balance, so a surplus month with a WEAK shadow bought
         * dollars at the peg. The lever that makes the shadow weak in a month of surplus, and strong in a month of deficit: the city's prices over the
         * world's (setParity(), a bare currency's only way to a parity), here 16% over, so the trend takes a sixth of the jump at once and the shadow stands
         * a gap over the peg; the month's balance, a fifth of imports, moves it the other way by less. Every figure the vault is held to is the harness's
         * own sum from the model's getters (Held): the shadow, the support faded, the peg, a year's GDP and the rate before the reprice.
         */
        final double weakParity = .15, vault0 = 40_000;

        ForeignAccounts weak = book(1.2 * TRADE, TRADE, vault0, 0, PEG);
        Held h = heldMonth(weak, 1.2 * TRADE, TRADE, weakParity);
        double gap = weak.getLastPegGap();
        out.printf("   a surplus month (US$%,.2fk) with the shadow %.1f%% over the peg: the vault sold US$%,.2fk and bought US$%,.2fk, to US$%,.2fk of US$%,.2fk%n", h.balance,
                gap * 100, weak.getDefenceUsd(), weak.getEdgeBoughtUsd(), weak.getReservesUsd(), vault0);
        assertTrue("fixture: the month is a SURPLUS and the shadow is WEAK, over the peg (" + String.format("%.3f", gap) + "), in a currency with a GDP to weigh a deal against",
                h.balance > 0 && gap > 0 && h.annual > 0 && weak.getRegime() == PEG);
        assertTrue("the vault FELL: it sold and bought nothing, where a peg that met the month's balance would have bought the surplus",
                weak.getDefenceUsd() > 0 && weak.getEdgeBoughtUsd() == 0 && weak.getEdgeOwedLocal() == 0 && weak.getReservesUsd() < vault0);
        close("...exactly what the lift takes: interventionLocalFor(the log of the peg over the free rate, a year's GDP) over the rate", weak.getDefenceUsd(), -h.deal, DOLLAR);
        close("...and the vault is lower by exactly that", vault0 - weak.getReservesUsd(), -h.deal, DOLLAR);
        assertTrue("...which is not the surplus (US$" + String.format("%,.2fk", h.balance) + "): the sale is the pressure's", Math.abs(weak.getDefenceUsd() - h.balance) > 100);
        same("...the rate is the peg to the digit", weak.getRate(), weak.getPegRate());
        close("...the hold is the lift, a sale's, negative: getHoldFelt() is the log of the peg over the shadow", weak.getHoldFelt(), Math.log(weak.getPegRate() / weak.getShadowRate()), 1e-12);
        assertTrue("...and it is below 0", weak.getHoldFelt() < 0);

        // The mirror: a deficit month with a strong shadow. The vault buys.
        ForeignAccounts strong = book(.8 * TRADE, TRADE, vault0, 0, PEG);
        Held m = heldMonth(strong, .8 * TRADE, TRADE, -weakParity);
        double gapStrong = strong.getLastPegGap();
        assertTrue("fixture: the month is a DEFICIT and the shadow is STRONG, under the peg (" + String.format("%.3f", gapStrong) + ")", m.balance < 0 && gapStrong < 0 && strong.getRegime() == PEG);
        assertTrue("the vault BOUGHT: it bought and sold nothing, where a peg that met the month's balance would have sold the deficit",
                strong.getEdgeBoughtUsd() > 0 && strong.getDefenceUsd() == 0 && strong.getReservesUsd() > vault0);
        close("...exactly what the lift takes", strong.getEdgeBoughtUsd(), m.deal, DOLLAR);
        close("...the vault is higher by exactly that", strong.getReservesUsd() - vault0, m.deal, DOLLAR);
        close("...and what is owed for it is that purchase at the month's rate", strong.getEdgeOwedLocal(), m.deal * m.rateBefore, DOLLAR);
        assertTrue("...a purchase never breaks a peg: still a peg, the rate the peg to the digit, the hold above 0 (a purchase's)",
                strong.getRegime() == PEG && !strong.brokeThisMonth() && Double.compare(strong.getRate(), strong.getPegRate()) == 0 && strong.getHoldFelt() > 0);

        // A weak gap held a year of surplus months: the vault falls every month.
        ForeignAccounts year = book(1.2 * TRADE, TRADE, vault0, 0, PEG);
        int soldMonths = 0, boughtMonths = 0, heldMonths = 0, smaller = 0;
        double worst = 0, sum = 0, first = 0, largest = 0;
        for (int i = 0; i < 12; i++) {
            Held y = heldMonth(year, 1.2 * TRADE, TRADE, weakParity);
            if (year.getDefenceUsd() > 0) soldMonths++;
            if (year.getEdgeBoughtUsd() > 0) boughtMonths++;
            if (year.getRegime() == PEG && Double.compare(year.getRate(), year.getPegRate()) == 0) heldMonths++;
            worst = Math.max(worst, Math.abs(year.getDefenceUsd() + y.deal));
            sum += y.deal;
            if (i == 0) first = year.getDefenceUsd(); else if (year.getDefenceUsd() < first) smaller++;
            largest = Math.max(largest, year.getDefenceUsd());
        }
        out.printf("   a weak gap held a year of surplus months: the vault from US$%,.2fk to US$%,.2fk, the first month's sale US$%,.2fk and the largest US$%,.2fk%n", vault0,
                year.getReservesUsd(), first, largest);
        assertTrue("a weak gap held a year of surplus months: the peg holds every month and the vault falls every month - it sold in " + soldMonths + " of 12 and bought in " + boughtMonths,
                heldMonths == 12 && soldMonths == 12 && boughtMonths == 0);
        close("...each month's sale is the lift's (the first the gap, each after it what the support's fading takes, less a gap that is closing), to a millionth of a dollar", worst, 0, DOLLAR);
        close("...and the vault's total fall is the sum of the model's figures", vault0 - year.getReservesUsd(), -sum, 12 * DOLLAR);
        assertTrue("...the first month paid the whole gap, and every month after it paid less (" + smaller + " of 11)", smaller == 11 && first == largest);

        // A gap past INTERVENTION_LINEAR of a year's output is sold on the bounded rule (XI1): each further dollar moves the rate less, so the lift costs more than the linear rule would ask.
        ForeignAccounts big = book(1.2 * TRADE, TRADE, vault0, 0, PEG);
        Held b = heldMonth(big, 1.2 * TRADE, TRADE, 2 * weakParity);
        double lift = -big.getHoldFelt();
        double linear = lift * b.annual / ForeignAccounts.INTERVENTION_IMPACT / b.rateBefore;
        assertTrue("fixture: the lift (" + String.format("%.3f", lift) + ") is past INTERVENTION_IMPACT x INTERVENTION_LINEAR, the rule's bound", lift > ForeignAccounts.INTERVENTION_IMPACT * ForeignAccounts.INTERVENTION_LINEAR
                && big.getRegime() == PEG);
        close("a gap past INTERVENTION_LINEAR of a year's output: the vault sold interventionLocalFor()'s dollars, on the bounded (logarithmic) branch", big.getDefenceUsd(), -b.deal, DOLLAR);
        assertTrue("...which is more than the linear rule would ask for that lift (US$" + String.format("%,.2fk", big.getDefenceUsd()) + " against " + String.format("%,.2fk", linear)
                + "), each further dollar moving the rate less, and more than the sale of the bound itself", big.getDefenceUsd() > linear
                && big.getDefenceUsd() > ForeignAccounts.INTERVENTION_LINEAR * b.annual / b.rateBefore);

        // A devaluation is a purchase and a revaluation a sale, the same rule; repeg() keeps the hold's support.
        ForeignAccounts dev = book(TRADE, TRADE, vault0, 0, PEG);
        final double oldPeg = dev.getPegRate();
        dev.repeg(oldPeg * 1.25);
        Held d = heldMonth(dev, TRADE, TRADE, Double.NaN);
        assertTrue("a devaluation is a purchase: the peg moved 25% weaker on balanced trade, the vault bought the lift and sold nothing, the rate the new peg to the digit",
                dev.getEdgeBoughtUsd() > 0 && dev.getDefenceUsd() == 0 && Double.compare(dev.getRate(), oldPeg * 1.25) == 0 && dev.getHoldFelt() > 0);
        close("...exactly interventionLocalFor()'s dollars", dev.getEdgeBoughtUsd(), d.deal, DOLLAR);
        final double support = dev.getHoldFelt();
        dev.repeg(oldPeg);
        assertTrue("repeg() keeps the hold's support: the revaluation back to the old peg leaves getHoldFelt() as it was, to the bit", Double.compare(dev.getHoldFelt(), support) == 0);
        Held back = heldMonth(dev, TRADE, TRADE, Double.NaN);
        assertTrue("a revaluation is a sale: the vault sold the lift back to the old peg - the support's fading and the rest of the way - and bought nothing, the rate the old peg to the digit",
                dev.getDefenceUsd() > 0 && dev.getEdgeBoughtUsd() == 0 && Double.compare(dev.getRate(), oldPeg) == 0);
        close("...exactly interventionLocalFor()'s dollars", dev.getDefenceUsd(), -back.deal, DOLLAR);

        // With no GDP the rule gives 0: the hold holds the rate and deals nothing.
        ForeignAccounts noGdp = CurrencyCheck.settled(1.2 * TRADE, TRADE, 0);
        noGdp.buyReserves(vault0 * noGdp.getRate());
        noGdp.forgetDealing();
        noGdp.setRegime(PEG);
        noGdp.startMonth();
        noGdp.takeMonth(CurrencyCheck.month(1.2 * TRADE, TRADE, 0, 0), 0);
        noGdp.setParity(Math.exp(weakParity), 1.0, 0, 0);
        noGdp.repriceCurrency();
        assertTrue("fixture: a currency with no GDP, pegged, its shadow over the peg (" + String.format("%.3f", noGdp.getLastPegGap()) + ")", noGdp.getGdpTrailing() == 0 && noGdp.getRegime() == PEG
                && noGdp.getLastPegGap() > 0);
        assertTrue("with no GDP interventionLocalFor() is 0: the hold holds the rate and deals nothing - the peg to the digit, the vault where it was, nothing sold or bought, no break",
                Double.compare(noGdp.getRate(), noGdp.getPegRate()) == 0 && Math.abs(noGdp.getReservesUsd() - vault0) <= DOLLAR && noGdp.getDefenceUsd() == 0
                        && noGdp.getEdgeBoughtUsd() == 0 && !noGdp.brokeThisMonth() && noGdp.getRegime() == PEG);
    }

    /* ================= 15. a band's edges pay the pressure (XR5) ================= */

    static void aBandsEdgesPayThePressure() {
        out.println("\n--- 15. a band's edges pay the pressure ---");

        final double weakParity = .15, vault0 = 40_000;

        // The weak edge in a surplus month sells.
        ForeignAccounts weak = book(1.2 * TRADE, TRADE, vault0, 0, BAND);
        weak.setBand(weak.getRate(), 0, ForeignAccounts.BAND_WIDTH);
        Held w = heldMonth(weak, 1.2 * TRADE, TRADE, weakParity);
        assertTrue("fixture: the month is a SURPLUS (US$" + String.format("%,.2fk", w.balance) + ") and the shadow is past the weak edge (" + String.format("%.4f over %.4f", weak.getShadowRate(), weak.bandWeakEdge()) + ")",
                w.balance > 0 && weak.getShadowRate() > weak.bandWeakEdge() && weak.getRegime() == BAND);
        assertTrue("the weak edge sells in a surplus month - the vault sold and bought nothing, where a band that met the month's balance would have held the edge for nothing",
                weak.getDefenceUsd() > 0 && weak.getEdgeBoughtUsd() == 0 && weak.getReservesUsd() < vault0);
        close("...exactly what the lift to the edge takes", weak.getDefenceUsd(), -w.deal, DOLLAR);
        same("...and the rate is the weak edge exactly", weak.getRate(), weak.bandWeakEdge());
        close("...the hold is the lift, a sale's: the log of the edge over the shadow", weak.getHoldFelt(), Math.log(weak.bandWeakEdge() / weak.getShadowRate()), 1e-12);

        // The strong edge in a deficit month buys.
        ForeignAccounts strong = book(.8 * TRADE, TRADE, vault0, 0, BAND);
        strong.setBand(strong.getRate(), 0, ForeignAccounts.BAND_WIDTH);
        Held s = heldMonth(strong, .8 * TRADE, TRADE, -weakParity);
        assertTrue("fixture: the month is a DEFICIT (US$" + String.format("%,.2fk", -s.balance) + ") and the shadow is past the strong edge (" + String.format("%.4f under %.4f", strong.getShadowRate(), strong.bandStrongEdge()) + ")",
                s.balance < 0 && strong.getShadowRate() < strong.bandStrongEdge() && strong.getRegime() == BAND);
        assertTrue("the strong edge buys in a deficit month - the vault bought and sold nothing, where a band that met the month's balance would have bought nothing",
                strong.getEdgeBoughtUsd() > 0 && strong.getDefenceUsd() == 0 && strong.getReservesUsd() > vault0);
        close("...exactly what the lift to the edge takes", strong.getEdgeBoughtUsd(), s.deal, DOLLAR);
        close("...what is owed for it is that purchase at the month's rate", strong.getEdgeOwedLocal(), s.deal * s.rateBefore, DOLLAR);
        same("...and the rate is the strong edge exactly", strong.getRate(), strong.bandStrongEdge());

        // Inside: a pulse of weakness that carries the rate to the edge once, then withdrawn. The hold's support then fades by half in INTERVENTION_HALF_LIFE and the rate
        // stands inside the band at the shadow times exp(the support), nothing dealt.
        ForeignAccounts band = book(TRADE, TRADE, vault0, 0, BAND);
        band.setBand(band.getRate(), 0, ForeignAccounts.BAND_WIDTH);
        Held pulse = heldMonth(band, TRADE, TRADE, .06);
        assertTrue("fixture: the pulse took the rate to the weak edge and sold (US$" + String.format("%,.2fk", band.getDefenceUsd()) + "), leaving a support of " + String.format("%.4f", band.getHoldFelt()),
                Double.compare(band.getRate(), band.bandWeakEdge()) == 0 && band.getDefenceUsd() > 0 && band.getHoldFelt() < 0);
        final double support = band.getHoldFelt();
        final int half = (int) ForeignAccounts.INTERVENTION_HALF_LIFE;
        assertTrue("fixture: INTERVENTION_HALF_LIFE is a whole number of months", half == ForeignAccounts.INTERVENTION_HALF_LIFE);
        int inside = 0, rateRight = 0, dealtNothing = 0, fadedRight = 0;
        double vaultAfterPulse = band.getReservesUsd();
        double worstRate = 0;
        for (int i = 1; i <= half; i++) {
            Held in = heldMonth(band, TRADE, TRADE, 0);
            if (in.free < Math.log(band.bandWeakEdge()) && in.free > Math.log(band.bandStrongEdge())) inside++;
            worstRate = Math.max(worstRate, Math.abs(band.getRate() / (band.getShadowRate() * Math.exp(band.getHoldFelt())) - 1));
            if (band.getDefenceUsd() == 0 && band.getEdgeBoughtUsd() == 0 && Double.compare(band.getReservesUsd(), vaultAfterPulse) == 0) dealtNothing++;
            if (Double.compare(band.getHoldFelt(), in.faded) == 0) fadedRight++;
        }
        out.printf("   a pulse to the edge, then %d months inside: the support %.4f to %.4f; the worst miss of the rate against the shadow times exp(the support) %.2e%n", half, support,
                band.getHoldFelt(), worstRate);
        assertTrue("inside the band, " + half + " months on from the edge, the rate was inside the edges in every one (" + inside + " of " + half + ")", inside == half);
        close("...the rate is the shadow times exp(getHoldFelt()), every month", worstRate, 0, 1e-12);
        assertTrue("...nothing is dealt inside: sold, bought and the vault are where they were, every month (" + dealtNothing + " of " + half + ")", dealtNothing == half);
        assertTrue("...and the hold is exactly what the support faded to, every month, to the bit (" + fadedRight + " of " + half + ")", fadedRight == half);
        close("...INTERVENTION_HALF_LIFE months on, half the support is left", band.getHoldFelt(), support / 2, 1e-12);

        // Under the floor the band gives way for the month, and stays a band: section 6 finds the vault the sale would take just under its floor; here it is the weak
        // edge of a surplus month, with a vault only a third over its floor against a sale of more than the whole vault's cover.
        ForeignAccounts thin = book(1.2 * TRADE, TRADE, 4_000, 0, BAND);
        thin.setBand(thin.getRate(), 0, ForeignAccounts.BAND_WIDTH);
        ForeignAccounts floating = book(1.2 * TRADE, TRADE, 4_000, 0, BAND);
        floating.setBand(floating.getRate(), 0, ForeignAccounts.BAND_WIDTH);
        floating.setRegime(FLOAT);
        double thinVault = thin.getReservesUsd();
        Held t = heldMonth(thin, 1.2 * TRADE, TRADE, weakParity);
        heldMonth(floating, 1.2 * TRADE, TRADE, weakParity);
        assertTrue("fixture: the sale the weak edge needs (US$" + String.format("%,.2fk", -t.deal) + ") is more than the vault holds over its floor (US$" + String.format("%,.2f", thinVault - thin.vaultFloorUsd()) + ")",
                t.deal < 0 && thinVault + t.deal < thin.vaultFloorUsd());
        assertTrue("under its floor the band gives way for the month - nothing sold, the vault where it was, the regime still Band, no break",
                thin.getRegime() == BAND && !thin.brokeThisMonth() && thin.getDefenceUsd() == 0 && Double.compare(thin.getReservesUsd(), thinVault) == 0);
        same("...the rate stands where it would with nothing dealt: the shadow, the floating twin's rate", thin.getRate(), floating.getRate());
        assertTrue("...past the edge it was to hold, and the hold is 0: it dealt nothing to show for", thin.getRate() > thin.bandWeakEdge() && thin.getHoldFelt() == 0);
    }

    /* ================= 16. the overshoot both ways (XR5) ================= */

    /** A month of balanced trade and a parity falling away by 60% over 30 months and staying there: the shadow far under the peg, where the chance of a break is BREAK_BASE a month. */
    static void fallMonth(ForeignAccounts fx, int m) {
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
        fx.setParity(Math.exp(-.6 * Math.min(1, (m + 1) / 30.0)), 1.0, -.2, 0);
        fx.repriceCurrency();
    }

    static void theOvershootBothWays() {
        out.println("\n--- 16. the overshoot both ways ---");

        /*
         * XR5 (Jerus, 2026-10-23: "overshoot both ways"; the Swiss franc, 2015). A peg that breaks lands OVERSHOOT of its gap past the shadow, on whichever side
         * the shadow was: the weak break, as it always did, over the shadow; and now the strong one, where the shadow was the stronger, under it. Before, a gap
         * the other way was "no overshoot" and the rate landed at the shadow. Each side is found by searching seeds of a bare seeded currency for one whose peg
         * broke by doubt, beside a floating twin on the same market (the chance of a break is BREAK_BASE a month with the shadow on the strong side, about one in a thousand).
         */
        for (int side = 0; side < 2; side++) {
            final boolean strong = side == 1;
            final String name = strong ? "stronger" : "weaker";
            ForeignAccounts peg = null, flt = null;
            int breakMonth = 0, marketApart = 0, seedsTried = 0, monthsPlayed = 0;
            double shadowAtBreak = 0, vaultBefore = 0;
            for (long s = 1; s <= 600 && peg == null; s++) {
                seedsTried++;
                ForeignAccounts p = book(TRADE, TRADE, DEEP, SEED + s, PEG), f = book(TRADE, TRADE, DEEP, SEED + s, FLOAT);
                int apart = 0;
                for (int m = 0; m < 400; m++) {
                    double v = p.getReservesUsd();
                    if (strong) { fallMonth(p, m); fallMonth(f, m); } else { driftMonth(p, m); driftMonth(f, m); }
                    monthsPlayed++;
                    if (Double.compare(p.getMarket(), f.getMarket()) != 0) apart++;
                    if (p.brokeThisMonth()) {
                        peg = p;
                        flt = f;
                        breakMonth = m;
                        marketApart = apart;
                        shadowAtBreak = p.getLastBreakShadow();
                        vaultBefore = v;
                        break;
                    }
                }
            }
            out.printf("   a peg broke by doubt with the shadow %s beside a floating twin: the first in %d seeds, %,d pegged months%n", name, seedsTried, monthsPlayed);
            assertTrue("fixture: a peg broke by doubt with the shadow on the " + name + " side of the peg, beside its floating twin", peg != null
                    && peg.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT && (strong ? peg.getLastBreakShadow() < peg.getPegRate() : peg.getLastBreakShadow() > peg.getPegRate()));
            if (peg == null) continue;
            final double gapAtBreak = Math.log(shadowAtBreak / peg.getPegRate());
            final double excess0 = Math.log(peg.getRate() / flt.getRate());
            out.printf("   month %d, a gap of %+.1f%%: the rate landed %+.1f%% from its twin's%n", breakMonth + 1, gapAtBreak * 100, (Math.exp(excess0) - 1) * 100);
            close("the " + name + " break lands at the shadow times exp(OVERSHOOT x the gap): past the shadow", peg.getRate(), shadowAtBreak * Math.exp(ForeignAccounts.OVERSHOOT * gapAtBreak),
                    1e-12 * peg.getRate());
            assertTrue("...on the " + name + " side: " + (strong ? "under the shadow, so under the peg" : "over the shadow, so over the peg"),
                    strong ? peg.getRate() < shadowAtBreak && peg.getRate() < peg.getPegRate() : peg.getRate() > shadowAtBreak && peg.getRate() > peg.getPegRate());
            close("the floating twin's rate is the shadow the peg broke from", flt.getRate(), shadowAtBreak, 1e-12 * shadowAtBreak);
            close("...so the break left the peg's rate off it by OVERSHOOT of the gap, in logs", excess0, ForeignAccounts.OVERSHOOT * gapAtBreak, 1e-12);
            assertTrue("fixture: the gap was " + (strong ? "negative" : "past BREAK_GAP, as a doubt break must be"),
                    strong ? gapAtBreak < 0 : gapAtBreak > ForeignAccounts.BREAK_GAP);
            assertTrue("nothing is dealt in the month of the break and the hold goes with the peg: the vault is where it was, getHoldFelt() is 0, Float after it",
                    peg.getDefenceUsd() == 0 && peg.getEdgeBoughtUsd() == 0 && Double.compare(peg.getReservesUsd(), vaultBefore) == 0 && peg.getHoldFelt() == 0 && peg.getRegime() == FLOAT);
            final int half = (int) ForeignAccounts.MARKET_HALF_LIFE;
            assertTrue("fixture: MARKET_HALF_LIFE is a whole number of months", half == ForeignAccounts.MARKET_HALF_LIFE);
            for (int m = breakMonth + 1; m <= breakMonth + half; m++) {
                if (strong) { fallMonth(peg, m); fallMonth(flt, m); } else { driftMonth(peg, m); driftMonth(flt, m); }
            }
            close("MARKET_HALF_LIFE months on, half the overshoot is gone, on the " + name + " side as on the other", Math.log(peg.getRate() / flt.getRate()), excess0 / 2, 1e-9);
            for (int m = breakMonth + half + 1; m <= breakMonth + 2 * half; m++) {
                if (strong) { fallMonth(peg, m); fallMonth(flt, m); } else { driftMonth(peg, m); driftMonth(flt, m); }
            }
            close("...and another MARKET_HALF_LIFE on, a quarter of it is left", Math.log(peg.getRate() / flt.getRate()), excess0 / 4, 1e-9);
            assertTrue("...and the peg's market path is the floating twin's, every month of it (" + marketApart + " months apart before the break)", marketApart == 0
                    && Double.compare(peg.getMarket(), flt.getMarket()) == 0 && peg.getMarketDraws() == flt.getMarketDraws());
        }
    }

    /* ================= 17. the tied path prices the curve (XR5) ================= */

    /** A bare debt manager on a dial and a world rate, struck on inflation well off its target, its tie read from this switch (null: never handed one). */
    static DebtManager tiedManager(boolean[] tie, double world) {
        DebtManager dm = new DebtManager();
        if (tie != null) dm.setTiedSource(() -> tie[0]);
        dm.setWorldRate(world);
        dm.strikeExpectations(.10, .06);
        dm.setPolicyRate(dm.tiedRate(0));
        return dm;
    }

    /** How many of the manager's paths and rates differ from another's by a bit: the curve's expected dial 600 months out, the floater's path, the long yields, the rule and where it settles. */
    static int pathsApart(DebtManager a, DebtManager b) {
        int apart = 0;
        for (int t = 0; t < YieldCurve.HORIZON; t++) if (Double.compare(a.expectedDialIn(t), b.expectedDialIn(t)) != 0) apart++;
        if (!Arrays.equals(a.expectedPolicyPath(), b.expectedPolicyPath())) apart++;
        for (int months : new int[] { 120, 360 }) if (Double.compare(a.curveRate(months), b.curveRate(months)) != 0) apart++;
        if (Double.compare(a.ruleNow(), b.ruleNow()) != 0) apart++;
        if (Double.compare(a.settledDial(), b.settledDial()) != 0) apart++;
        return apart;
    }

    static void theTiedPathPricesTheCurve() {
        out.println("\n--- 17. the tied path prices the curve ---");

        /*
         * XR5 (Jerus, 2026-10-23: "price on the tied path"). Under a peg Game sets the dial to the US's rate plus the spread every month (tiedRate()), but
         * pathFor() still priced the curve's expected dial on the inflation rule, so the long end followed a rule the dial no longer followed. Now a
         * tied manager's path closes the floor's premium over the dial on the dial itself; the tie (the dial when tied, NaN when not) is in the caches'
         * keys, so an untied manager's paths are built exactly as before. The fixture is a bare manager struck on a 10% inflation with 6% expected, far
         * off its target, so the rule's path is visibly not the dial's.
         */
        final double world = .03;
        boolean[] tie = { false };
        DebtManager wired = tiedManager(tie, world), never = tiedManager(null, world), off = tiedManager(new boolean[] { false }, world);
        double dial = wired.getPolicyRate();
        assertTrue("fixture: three managers on the same dial (" + String.format("%.4f", dial) + "), one wired to a switch that is off, one to a source that is always off, one never handed any", dial > 0
                && Double.compare(never.getPolicyRate(), dial) == 0 && Double.compare(off.getPolicyRate(), dial) == 0 && wired.isStruck() && !wired.isTied() && !never.isTied() && !off.isTied());
        assertTrue("fixture: the inflation rule is not the dial (the rule at today's inflation " + String.format("%.4f", never.ruleNow()) + ", settling at " + String.format("%.4f", never.settledDial())
                + "), so its path and the tied one are visibly apart", Math.abs(never.ruleNow() - dial) > .01 && Math.abs(never.settledDial() - dial) > .01);
        assertTrue("untied - the source off, or none handed - every path is the rule's to the bit: the curve's expected dial 600 months out, the floater's path, the long yields, the rule and where it settles",
                pathsApart(wired, never) == 0 && pathsApart(off, never) == 0);
        double untied120 = wired.curveRate(120), untied360 = wired.curveRate(360);
        double dialUntied = Math.abs(wired.expectedDialOver(360) - dial);

        // Tied.
        tie[0] = true;
        assertTrue("tied (the switch on): isTied() says so", wired.isTied());
        same("ruleNow() is the dial when tied: the market expects the dial to stay where the US's rate keeps it", wired.ruleNow(), dial);
        same("settledDial() is the dial when tied", wired.settledDial(), dial);
        int flat = 0;
        for (double p : wired.expectedPolicyPath()) if (Double.compare(p, dial) == 0) flat++;
        assertTrue("the floater's expected path is flat at the dial, every month (" + flat + " of " + YieldCurve.HORIZON + ")", flat == YieldCurve.HORIZON);
        double worst = 0;
        for (int t = 0; t < YieldCurve.HORIZON; t++) {
            double expected = dial + (wired.floorRate() - dial) * Math.exp(-(double) t / YieldCurve.DRIFT_MONTHS);
            worst = Math.max(worst, Math.abs(wired.expectedDialIn(t) - expected));
        }
        close("the curve's expected dial is the floor's premium over the dial closing on the dial (DRIFT_MONTHS), every month for fifty years, not on the rule", worst, 0, 1e-12);
        same("...it starts at the floor, to the bit", wired.expectedDialIn(0), wired.floorRate());
        double tied120 = wired.curveRate(120), tied360 = wired.curveRate(360);
        double dialTied = Math.abs(wired.expectedDialOver(360) - dial);
        out.printf("   struck on 10%% inflation, 6%% expected, the dial %.2f%%: the 10-year yield %.2f%% untied and %.2f%% tied; the 30-year %.2f%% and %.2f%%%n", dial * 100, untied120 * 100,
                tied120 * 100, untied360 * 100, tied360 * 100);
        assertTrue("the long yields are the curve on the tied path, not the rule's: the 10-year and the 30-year differ from the untied manager's by more than a point",
                Math.abs(tied120 - untied120) > .01 && Math.abs(tied360 - untied360) > .01);
        assertTrue("...the dial expected over 30 years is nearer the dial than the rule's path put it (" + String.format("%.4f against %.4f", dialTied, dialUntied) + ")", dialTied < dialUntied);
        close("...and the long yield is still the curve taken apart: the dial expected, the credit spread, the term premium less the compression", tied360,
                wired.expectedDialOver(360) + wired.creditSpread() + DebtManager.termPremium(360) - wired.compression(360), 1e-9);

        // Moving the US's rate and re-tying moves the long yields with it; untied they hardly move.
        final double move = .03;
        DebtManager rule = tiedManager(null, world);
        wired.setWorldRate(world + move);
        wired.setPolicyRate(wired.tiedRate(0));
        rule.setWorldRate(world + move);
        rule.setPolicyRate(rule.tiedRate(0));
        double rose120 = wired.curveRate(120) - tied120, rose360 = wired.curveRate(360) - tied360;
        double ruleRose360 = rule.curveRate(360) - untied360;
        out.printf("   the US's rate up %.0f points and the dial re-tied: the tied 10-year yield up %.2f points, the 30-year %.2f; untied manager's 30-year up %.2f%n", move * 100, rose120 * 100,
                rose360 * 100, ruleRose360 * 100);
        assertTrue("the US's rate up " + String.format("%.0f", move * 100) + " points and the dial re-tied: the tied long yields rise with it (by " + String.format("%.2f and %.2f", rose120 * 100, rose360 * 100) + " points)",
                rose120 > .5 * move && rose360 > .5 * move && rose120 < 1.5 * move && rose360 < 1.5 * move);
        assertTrue("...where the untied manager's 30-year, the rule's path under a dial that moved the same, hardly moves (" + String.format("%.2f", ruleRose360 * 100) + " points)", ruleRose360 < .25 * move);
        same("...ruleNow() and settledDial() follow the dial", wired.ruleNow() + wired.settledDial(), 2 * wired.getPolicyRate());

        // Untied again: the key honours the tie, so a manager that was tied is the rule's to the bit once the switch is off.
        tie[0] = false;
        assertTrue("untied again, a manager that was tied prices the rule's path to the bit - the caches' keys carry the tie - exactly as the one never handed a source on the same dial",
                pathsApart(wired, rule) == 0 && !wired.isTied());

        // In a city the source is the peg.
        Game city = town("xr5-tied", steady());
        DebtManager dm = city.getDebtManager();
        ForeignAccounts fx = city.getForeignAccounts();
        assertTrue("in a city the manager is tied under a peg and under nothing else: Lean no, Peg yes, Band no, Float no",
                tiedIn(city, dm, LEAN) == false && tiedIn(city, dm, PEG) && !tiedIn(city, dm, BAND) && !tiedIn(city, dm, FLOAT));
        quietly(() -> city.setCurrencyRegime(PEG));
        play(city, 2);
        assertTrue("fixture: the city is pegged and has struck its expectations", fx.getRegime() == PEG && dm.isStruck() && dm.isTied());
        same("a pegged city: ruleNow() is the dial the peg ties", dm.ruleNow(), dm.getPolicyRate());
        same("...and settledDial() is", dm.settledDial(), dm.getPolicyRate());
        quietly(() -> city.setCurrencyRegime(LEAN));
        assertTrue("left for Lean, the same city's rule is the inflation rule again: ruleNow() is advisedPolicyRate() at the inflation the month struck",
                !dm.isTied() && Double.compare(dm.ruleNow(), dm.advisedPolicyRate(dm.getStruckInflation())) == 0);
    }

    /** Whether the city's debt manager is tied with the city in this regime. */
    static boolean tiedIn(Game city, DebtManager dm, ForeignAccounts.Regime regime) {
        quietly(() -> city.setCurrencyRegime(regime));
        return dm.isTied();
    }

    /* ================= 18. a thin vault's months are what the hold does (XR5) ================= */

    /**
     * A bare currency pegged at its rate with its shadow a steady gap (a log) over the peg, on balanced trade and a quiet market: the parity is set once, before the first
     * reprice, so the trend opens on it and stays (a level with no slope on a parity that does not move), and the gap, balance and cover terms are 0 while the vault is deep.
     * The shadow then stands at exactly the gap over the peg every month, which is the steady gap monthsHoldingGap() is struck on.
     */
    static ForeignAccounts steadyGap(double gap, double vaultUsd) {
        ForeignAccounts fx = book(TRADE, TRADE, vaultUsd, 0, PEG);
        fx.setParity(Math.exp(gap), 1.0, 0, 0);
        return fx;
    }

    /** The first month's cost of holding a steady gap, in dollars, from the model's own constants and getters: interventionLocalFor(gap, a year's GDP) over the rate. */
    static double firstMonthUsd(ForeignAccounts fx, double gap) {
        return ForeignAccounts.interventionLocalFor(gap, 12 * fx.getGdpTrailing()) / fx.getRate();
    }

    /** ...and each month after it: what the support's fading takes, (1 - 0.5^(1 / INTERVENTION_HALF_LIFE)) of the gap. */
    static double eachMonthUsd(ForeignAccounts fx, double gap) {
        return ForeignAccounts.interventionLocalFor((1 - Math.pow(.5, 1 / ForeignAccounts.INTERVENTION_HALF_LIFE)) * gap, 12 * fx.getGdpTrailing()) / fx.getRate();
    }

    static void aThinVaultsMonthsAreWhatTheHoldDoes() {
        out.println("\n--- 18. a thin vault's months are what the hold does ---");

        /*
         * XR5 (Jerus, "Warn on a thin vault"). PolicyScreen warns a city choosing a peg or a band when the vault would hold a steady gap of BREAK_GAP for fewer than
         * THIN_HOLD_MONTHS; the figure it reads is monthsHoldingGap(), a projection that costs the first month at the whole gap and each month after it at what the support's
         * fading takes. This section proves that projection is what the hold deals: the sums are the harness's own, from the model's constants, and the sales are read
         * from the model's getters after each month.
         */
        final double gap = ForeignAccounts.BREAK_GAP, deep = 40_000;

        // The measured months, with a vault deep enough to keep the cover over THIN_COVER throughout.
        ForeignAccounts fx = steadyGap(gap, deep);
        final double first = firstMonthUsd(fx, gap), each = eachMonthUsd(fx, gap);
        final double headroom = fx.getReservesUsd() - fx.vaultFloorUsd();
        double projected = fx.monthsHoldingGap(gap);
        out.printf("   a peg with its shadow a steady %.0f%% over it: the first month costs US$%,.4f, each month after it US$%,.4f; a vault of US$%,.0f (floor US$%,.0f) holds %.2f months%n",
                gap * 100, first, each, deep, fx.vaultFloorUsd(), projected);
        assertTrue("fixture: a pegged currency with a GDP and a floor, the vault deep (cover " + String.format("%.1f", fx.importCover()) + " months, over THIN_COVER), the first month dearer than each after it",
                fx.getRegime() == PEG && fx.getGdpTrailing() > 0 && fx.vaultFloorUsd() > 0 && fx.importCover() > 2 * ForeignAccounts.THIN_COVER && first > each && each > 0);
        close("the projection over a vault above its first month's cost is 1 + (headroom - first month) / each month after it, from the harness's own sums", projected, 1 + (headroom - first) / each, 1e-9);
        double reserves = fx.getReservesUsd(), rate = fx.getRate(), holdFelt = fx.getHoldFelt();
        fx.monthsHoldingGap(gap);
        fx.monthsHoldingGap(3 * gap);
        assertTrue("...asking is pure: the vault, the rate and the hold are where they were, to the bit",
                Double.compare(fx.getReservesUsd(), reserves) == 0 && Double.compare(fx.getRate(), rate) == 0 && Double.compare(fx.getHoldFelt(), holdFelt) == 0);

        final int measured = 6;
        double worstGap = 0, worstFirst = 0, worstEach = 0, lowestCover = Double.MAX_VALUE;
        for (int i = 0; i < measured; i++) {
            double before = fx.getReservesUsd();
            month(fx, TRADE, TRADE);
            double sold = before - fx.getReservesUsd();
            worstGap = Math.max(worstGap, Math.abs(fx.getLastPegGap() - gap));
            if (i == 0) worstFirst = Math.abs(sold - first); else worstEach = Math.max(worstEach, Math.abs(sold - each));
            lowestCover = Math.min(lowestCover, fx.importCover());
            if (fx.getRegime() != PEG || Double.compare(fx.getRate(), fx.getPegRate()) != 0) worstGap = 1;
        }
        out.printf("   %d months played: the first sold US$%,.4f, the next ones US$%,.4f each; the vault from US$%,.2f to US$%,.2f, the cover down to %.1f months%n", measured,
                first, each, deep, fx.getReservesUsd(), lowestCover);
        close("the shadow stood at the steady gap over the peg every month, and the peg held (the fixture)", worstGap, 0, 1e-9);
        assertTrue("...the cover stayed over THIN_COVER (" + String.format("%.1f", lowestCover) + " months), so the cover's premium added nothing", lowestCover > ForeignAccounts.THIN_COVER);
        close("the first month's sale is the projection's first month: interventionLocalFor(the gap) over the rate, to a millionth of a dollar", worstFirst, 0, DOLLAR);
        close("...and each month after it sold the projection's each-month figure, to a millionth of a dollar (" + (measured - 1) + " months)", worstEach, 0, DOLLAR);

        // A vault under the first month's cost: the share of it the headroom pays.
        double floorUsd = fx.vaultFloorUsd();
        ForeignAccounts half = steadyGap(gap, floorUsd + first / 2);
        close("a vault whose headroom is half the first month's cost holds half a month", half.monthsHoldingGap(gap), .5, 1e-9);
        ForeignAccounts exact = steadyGap(gap, floorUsd + first);
        close("...and one whose headroom is exactly the first month's cost holds exactly one", exact.monthsHoldingGap(gap), 1, 1e-9);
        ForeignAccounts onePlus = steadyGap(gap, floorUsd + first + each);
        close("...and one with the first month and one more month's cost holds two", onePlus.monthsHoldingGap(gap), 2, 1e-9);

        // A vault at its floor, or under it, holds nothing; no GDP or no gap, nothing to pay.
        ForeignAccounts atFloor = steadyGap(gap, floorUsd);
        ForeignAccounts under = steadyGap(gap, floorUsd / 2);
        assertTrue("fixture: the first vault is at its floor to the dollar, the second under it", Math.abs(atFloor.getReservesUsd() - atFloor.vaultFloorUsd()) <= DOLLAR
                && under.getReservesUsd() < under.vaultFloorUsd());
        close("a vault at its floor holds 0 months", atFloor.monthsHoldingGap(gap), 0, 1e-9);
        same("...and one already under it, 0", under.monthsHoldingGap(gap), 0);
        ForeignAccounts noGdp = CurrencyCheck.settled(TRADE, TRADE, 0);
        noGdp.buyReserves(deep * noGdp.getRate());
        noGdp.forgetDealing();
        noGdp.setRegime(PEG);
        assertTrue("with no GDP the interventions' rule has nothing to weigh a deal against: the months are infinite",
                noGdp.getGdpTrailing() == 0 && Double.isInfinite(noGdp.monthsHoldingGap(gap)) && noGdp.monthsHoldingGap(gap) > 0);
        assertTrue("...and with no gap there is nothing to hold: infinite, at a gap of 0 and at a gap the other way", Double.isInfinite(fx.monthsHoldingGap(0)) && fx.monthsHoldingGap(0) > 0
                && Double.isInfinite(fx.monthsHoldingGap(-gap)));

        // Played on to the break by the vault. The vault starts a few months over THIN_COVER (the first month's cost and ten and a half more over it, so that no month opens
        // with the cover exactly at THIN_COVER) and falls through it: the cover's premium comes in on the way down, which is what the projection leaves out.
        final double start = ForeignAccounts.THIN_COVER * TRADE + first + 10.5 * each;
        ForeignAccounts run = steadyGap(gap, start);
        final double floorAtEntry = run.vaultFloorUsd();
        final double thinUsd = ForeignAccounts.THIN_COVER * TRADE / run.getRate();
        double atEntry = run.monthsHoldingGap(gap);
        // The months in which the cover (the vault after the last sale, over the month's imports) is still at THIN_COVER or over when the month opens: the cover's premium is
        // 0 in each, so the month's sale is the projection's, exactly, and the vault one sale lower is still over THIN_COVER's vault, which is over the floor and over the first
        // month's cost. Those months are held whatever comes after, so they are the lower bound; the projection itself is the upper bound, the premium only ever adding to
        // the sale.
        int lower = 0;
        for (double vault = run.getReservesUsd(); vault >= thinUsd; lower++) vault -= lower == 0 ? first : each;
        assertTrue("fixture: THIN_COVER months of imports is more than the floor and the first month's cost together (so a month opened over THIN_COVER can always pay), and the projection is past its lower bound",
                thinUsd - run.vaultFloorUsd() > first && atEntry > lower);

        int held = 0, exactMonths = 0, firstPremium = 0;
        double lastSale = 0;
        boolean fixedUp = true;
        for (int m = 1; m <= 200 && !run.brokeThisMonth(); m++) {
            double before = run.getReservesUsd();
            month(run, TRADE, TRADE);
            if (run.brokeThisMonth()) break;
            held++;
            double sold = before - run.getReservesUsd();
            double expected = m == 1 ? first : each;
            if (Math.abs(sold - expected) <= DOLLAR) { if (firstPremium == 0) exactMonths++; }
            else if (firstPremium == 0) firstPremium = m;
            lastSale = sold;
            if (Double.compare(run.getRate(), run.getPegRate()) != 0 || run.getRegime() != PEG) fixedUp = false;
        }
        out.printf("   a vault of US$%,.0f (cover %.1f months, floor US$%,.0f) projected at entry to hold %.2f months; played on, it held %d and broke in the %s month (cause: %s); the lower bound is %d%n",
                start, start / TRADE, floorAtEntry, atEntry, held, ordinal(held + 1), run.getLastBreakCause().words, lower);
        out.printf("   the first %d months sold the projection's figures exactly; the last held month sold US$%,.4f against US$%,.4f projected%n", exactMonths, lastSale, each);
        assertTrue("fixture: the run held the rate every month until the break, which came by the vault (not by doubt: the market is quiet)",
                fixedUp && run.brokeThisMonth() && run.getLastBreakCause() == ForeignAccounts.BreakCause.VAULT && run.getRegime() == FLOAT);
        assertTrue("the months held are at most the projection struck at entry (" + held + " against " + String.format("%.2f", atEntry) + "): the projection leaves out the cover's premium, which only adds pressure",
                held <= atEntry);
        assertTrue("...and at least the months before the cover fell under THIN_COVER (" + held + " against " + lower + "), which the projection prices exactly", held >= lower);
        assertTrue("...the first " + exactMonths + " months sold the projection's figures to a millionth of a dollar, which is at least the lower bound (" + lower + "): the premium came after the cover fell under THIN_COVER",
                exactMonths >= lower);
        assertTrue("...and the projection is not the whole story for a vault that falls under THIN_COVER: it held fewer months than projected (" + held + " against " + String.format("%.2f", atEntry) + ")",
                held < Math.floor(atEntry));
    }

    /** 21 as "21st", for a printed line. */
    static String ordinal(int n) {
        return n + (n % 100 >= 11 && n % 100 <= 13 ? "th" : n % 10 == 1 ? "st" : n % 10 == 2 ? "nd" : n % 10 == 3 ? "rd" : "th");
    }
}
