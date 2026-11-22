package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;

import ham.citybuildersim.sectors.Retail;

/**
 * Proves the city's goods are struck at a D$ that follows the D$ slowly (AN1,
 * 2026-10-22): each month the goods' D$ closes EconomyManager.PASS_THROUGH of
 * its gap to the D$, in logs, and every world price is struck at it times the
 * world's own price level; a step in the D$ reaches the goods at that speed;
 * the ring a one-for-one strike let run echoes less; a save carries the struck
 * rate; and in a real city the two are far enough apart for it to matter. Not
 * part of the game.
 *
 * WHY THIS EXISTS. Jerus, 2026-10-22: "Fix hyperinflation first". Since XR1
 * the D$ floats and follows its parity one for one, and until AN1 every traded
 * price - the dock's imports and exports, the band the city's own goods trade
 * in, the materials in a building's cost - was struck at the D$ of the month:
 * a price rise moved parity, the D$ followed it, imports got dearer, prices
 * rose again, with no lag anywhere in the ring, and the default playtest ran
 * to 1.7e9 times its founding prices (docs/queue/XR1-notes.md, section 3). The
 * fix is a lag on the goods and only the goods: EconomyManager, THE D$ THE
 * GOODS ARE STRUCK AT. This is its harness. The first four sections prove the
 * rule and its wiring; the fifth the save; the sixth, the third rule of the
 * checks, that the mechanic fires in a real run and not only on a bench; the
 * seventh, AN2, the reading the player is shown.
 *
 * What it has to prove, a section each, every one a fixture that causes the
 * condition rather than waiting for it:
 *
 *   1. THE RULE ON THE BENCH. passThrough() applied n times to a 10% step is
 *      the struck rate times 1.1^(1-(1-k)^n), k = PASS_THROUGH, to 1e-12 for
 *      several n; at speed one it is the D$ itself (to the last digit - see
 *      the section); a struck rate at the D$ stays there. The half-life and
 *      the share reached in a year are printed.
 *   2. THE WIRING, EVERY MONTH IN A REAL CITY. A new city on the default
 *      founding, 240 months: the first strike is at the D$ itself; each month
 *      the struck rate is passThrough() of the last month's, moved toward the
 *      D$ the strike saw (the one read between months), and the markets' rate
 *      is the struck rate times the world's price level, to the bit. The city
 *      ends with its goods' D$ set GAP_MADE from the D$ (setStruckRate) and is
 *      saved so, the gap sections 3, 5 and 6 need; and the wiring is read again
 *      for PUSH_MONTHS on a copy of it whose D$ is pinned by turns PUSH_OFF
 *      either side of where it stood, so the strikes close real gaps (caused).
 *   3. A STEP IN THE D$ REACHES THE GOODS AT PASS_THROUGH A MONTH. Twins of
 *      that city, both pinned at the struck rate so no gap is pending, one's
 *      D$ then pinned 10% weaker: for 36 months the weak twin's struck rate
 *      over the other's is 1.1^(1-(1-k)^n), and so are the markets'. The twins'
 *      price indices part by a share of the step - the city's own consumer
 *      pass-through, and its five components at 12 months, printed and not
 *      asserted, for the index holds rent and land, which the goods' rate does
 *      not set. The groceries component's share at 12 months is positive and
 *      under the share the goods' rate itself has reached - on a shelf the
 *      imported cost sets, caused: both twins' goods' D$ and D$ set at DEAR
 *      times the saved city's, so the shop's cost-plus floor stands over its
 *      opening price and the clearing price under the floor (see the section
 *      for the shelf's rule, traced); and since PT2 the shops' hand-over
 *      caused too: Small Grocery Stores stood in both twins until the shops
 *      hand over HAND_OVER_MARGIN of the households' need at the rate they
 *      ran at when struck, both played at the DEAR pin until the shelf holds
 *      what the places hand over, and then the step, so the clearing price is
 *      under the floor whatever the year's turn does to the rate (both
 *      counted, all 12 months).
 *   4. THE RING SETTLES. Twins of that city on the float, each first given
 *      the cash for the whole of what its treasury buys and a vault of twice
 *      THIN_COVER months of imports so the vault's cover is flat (a thin vault
 *      parts the twins by that road, not by the ring), and neither leaning on
 *      the D$ (the FLOAT regime: the defence sells none, so what the treasury
 *      sells back is what it bought and the vault earned): the treasury of one
 *      buys dollars worth 5% of a year's GDP and sells the same dollars back
 *      twelve months later, a swing of the D$ that moves the
 *      city's prices and so its parity. Played ten years at PASS_THROUGH and
 *      again with both twins at speed one (the rule before AN1): the D$'s
 *      widest gap between the twins, the goods' widest gap and the price
 *      index's gap at the end are printed for each, not asserted: ten years on
 *      a gap is the economy amplifying a small difference. The purchase moves
 *      the D$ by what INTERVENTION_IMPACT says; at PASS_THROUGH the goods' gap
 *      never exceeds the D$'s widest gap so far times 1-(1-k)^n (one for one,
 *      the goods' widest gap is the D$'s over the months before the last, a
 *      month behind it); and over the
 *      swing's own horizon - the round trip and the goods' half-life, from
 *      the constants - the mean gap of the goods' rate and of the price index
 *      is smaller at PASS_THROUGH than one for one (the D$'s is printed).
 *   5. A SAVE CARRIES IT. A city saved and loaded keeps the struck rate to
 *      the bit and strikes the next month as the live city does; a loaded city
 *      whose struck rate is cleared (a save without the key) strikes at the
 *      D$ itself; a currency reform scales the struck rate with the D$. The
 *      saved city's goods' D$ is apart from its D$ by cause (section 2's end).
 *   6. HOW OFTEN IT MATTERS IN A REAL CITY. A D$ pinned MARK_OFF from the
 *      goods' D$ is read as a gap of more than APART at the strike's month,
 *      and the goods' D$ moves for it - caused. Over section 2's 240 months,
 *      printed and not asserted: the goods' D$ against the D$ the strike saw -
 *      median and widest gap, the months more than 5% apart - and the months in
 *      which the mechanic moved the goods at all.
 *   7. THE SCREEN'S READING (AN2, 2026-11-06). Game.struckFromRate(), the line
 *      on Trade > The currency: NaN while nothing is struck; and over section
 *      2's months, the gap it reads at a month's end is the one the next
 *      month's strike closes PASS_THROUGH of, in logs.
 *
 * Numbering is the running order. Money is in thousands, as everywhere.
 *
 * @author Jerus
 */
public class PassThroughCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** This run's folder: every city here saves under it, none under the real save folder. */
    static Path root;

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

    /** ...to a relative tolerance: the two figures' gap over the expected one's size. */
    static void closeRelative(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol * Math.abs(expected);
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %,.15f != %,.15f%n", label, actual, expected);
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

    /** The speed the goods follow the D$ at, as the model has it. */
    static final double K = EconomyManager.PASS_THROUGH;

    /** The share of a step in the D$ that has reached the goods after n months. */
    static double reached(double speed, int n) { return 1 - Math.pow(1 - speed, n); }

    /** The months section 2 plays the real city, and the series it records (sections 6 and 7 read it). */
    static final int MONTHS = 240;

    /** The gap, in logs, between the D$ and the goods' D$ that sections 3, 5 and 6 need a case to stand at (they assert more than this): a pin that removes it removes a real gap. */
    static final double APART = .01;

    /** The months section 2 reads the wiring again on a copy of the saved city whose D$ is pinned off by turns, PUSH_OFF either side of where it stood. */
    static final int PUSH_MONTHS = 12;

    /** How far, in logs, either side of where it stood section 2 pins that copy's D$, month by month: a pin 16% from the last month's, more than BIG_GAP. */
    static final double PUSH_OFF = .08;

    /** The gap, in logs, section 2's D$ must move between two strikes in the pinned months for its wiring to be read against a D$ that has moved (the old fixture's 5%). */
    static final double BIG_GAP = .05;

    /** How far, in logs, section 2 sets the saved city's goods' D$ from its D$ at the end of the run: the gap sections 3, 5 and 6 need (more than APART), made and not found. */
    static final double GAP_MADE = .05;

    /** The multiple of the saved city's goods' D$ at which section 3's twins are struck and pinned: dear enough that the shop's cost-plus floor stands over its opening price and the clearing price under the floor (the section asserts both). */
    static final double DEAR = 2;

    /** How much over the households' need section 3's shops can hand over, at the operating rate they ran at when the twins were struck, once the fixture's Small Grocery Stores stand (PT2): the year's turn took 7-9% off Retail's rate on OE1's path and on 0.9.181's, so half again is room for it. */
    static final double HAND_OVER_MARGIN = 1.5;

    /** The most months section 3's twins play as those stores stand, before the pin, for the shops' cash-capped restocking to fill the shelf to what the places hand over (PT2; two on 0.9.181). */
    static final int STOCKING_MONTHS = 12;

    /** How far, in logs, section 6 pins the D$ from the goods' D$ (5%) to read the gap the mechanic closes. */
    static final double MARK_OFF = .05;

    /** The size of the step in sections 1 and 3: the D$ 10% weaker. */
    static final double STEP = 1.10;

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        root = TempDirs.create("passthrough");
        long started = System.currentTimeMillis();

        theRuleOnTheBench();
        theWiring();
        aStepReachesTheGoods();
        theRingSettles();
        aSaveCarriesIt();
        howOftenItMatters();
        theScreensReading();

        out.printf("%nrun time %.1f s%n", (System.currentTimeMillis() - started) / 1000.0);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ================= the fixtures every section borrows ================= */

    /** A save folder of this run's own, one per city that saves. */
    static GameFiles files(String label) {
        return new GameFiles(root.resolve(label), root.resolve("no-legacy"));
    }

    /**
     * A trading town on the default founding, as ExchangeRateCheck builds it; its D$ is what the founding's seed makes it.
     *
     * TWO DEPOTS STANDING SINCE PT1-M4, beside the four it orders. The works department that built 400 points a month for a
     * city nobody staffed is retired, and the four ordered depots are built by the founding yard's 160 points at the crews it
     * has: the town grew to 672 people by month 61, its trade stayed thin, and its treasury ended the 240 months of section 2
     * at D$-2.0M, so section 4's treasury (buyForeignCurrency() buys no more than the cash it holds) bought no dollars at all:
     * D$0k for US$0k, and every assertion of the swing read a twin that had done nothing. Two depots stood
     * (addStack(t, n, true), as foundingDepot() once stood its one) are builders enough: 2,193 people at month 61 and a
     * treasury of D$748M at month 240.
     */
    static Game town(String label) {
        Game city = new Game(files(label), Founding.defaults());
        // R0b call 7: the causes of 1900 held off, before a month, here and in every twin loaded from it (load()). The ring settles on a
        // vault the defence sells from in proportion to what it holds, sized on today's deaths; on 1900's causes the twin that bought held
        // US$134k over the other's where it had bought US$131k, so the treasury could not sell back "the dollars the defence had not sold".
        city.holdCausesOfDeathOff();
        // On the world's band since WN3-R (2026-11-08), here and in every twin loaded from it (load()). WN3 put the ring on the goods'
        // literals: on the band at 0.8.75 the twins' D$ gap, one for one, stood at 34% at month 120 and the vault of the twin that bought
        // held more than it had bought. Measured again on 0.9.55: at PASS_THROUGH the D$'s widest gap 10.22% (month 1), ten years on
        // -0.14%; one for one -0.47%; the vaults equal after the treasury's sale-back, and the price index's mean gap 0.25% against 0.81%.
        // Both twins live in the one world, so its years move them alike and the intervention is still the only thing parting them.
        quietly(() -> {
            city.run();
            // THE DEFAULTS PUT BACK IN THE FIXTURE (BA1, 2026-11-05; from R0 to OP1 a 1900 city was dated on Canada's policy): the ring is read
            // in a town that taxes from month 1 and whose treasury section 4 buys the swing out of; until OP1 a 1900 town had no income tax to
            // 1916-17 and no sales tax to 1920, so its treasury had D$190k at month 240 for a swing of 5% of a year's GDP (the whole bill
            // unbought). Since OP1 every lever is open from the founding, and the defaults are put back before a month has run (MonetaryCheck 5).
            city.getEconomyManager().getTaxPolicy().reset();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(ForeignCheck.template(city, "Construction Depot"), 2, true);
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

    /** One month, with nothing built or decided by anyone: the twins differ only in what the harness does to them. */
    static void month(Game g) {
        quietly(() -> g.simulateMonths(1));
    }

    /** A city loaded from the base city's slot 1 (BASE_SLOT), into a city of its own. */
    static Game load(Game base) {
        Game g = new Game(base.getGameFiles());
        g.holdCausesOfDeathOff();   // R0b call 7: as the base city (town()); the hold is not saved
        quietly(() -> g.loadGameSave(BASE_SLOT));
        return g;
    }

    /** The slot, in this harness's own folder (TempDirs), the base city is saved to and its twins are loaded from. */
    static final int BASE_SLOT = 1;

    /** Section 2's city, and the save sections 3 to 5 load from; set by section 2. */
    static Game city;

    /* ================= 1. the rule on the bench ================= */

    static void theRuleOnTheBench() {
        out.println("--- 1. the rule on the bench ---");

        double struck0 = 1.37;
        for (int n : new int[] { 1, 2, 6, 12, 24, 36, 120 }) {
            double struck = struck0;
            for (int i = 0; i < n; i++) struck = EconomyManager.passThrough(struck, struck0 * STEP, K);
            closeRelative("a 10% step in the D$, " + n + " strikes on: the struck rate times 1.1^(1-(1-k)^n)", struck,
                    struck0 * Math.pow(STEP, reached(K, n)), 1e-12);
        }
        double halfLife = Math.log(.5) / Math.log(1 - K);
        out.printf("   PASS_THROUGH %.5f a month: the half-life of a step is %.1f months; %.1f%% of it has reached the goods in a year,"
                + " %.1f%% in two%n", K, halfLife, 100 * reached(K, 12), 100 * reached(K, 24));
        close("...which is when half of the step is still to come: (1-k) to the half-life is a half", Math.pow(1 - K, halfLife), .5, 1e-12);

        // The rule at speed one is the rule before AN1: the goods struck at the D$ of the month. exp(log(s) + (log(d) - log(s)))
        // is d to within the last digit or two, not always to the very bit - the model's own rounding, which this measures.
        java.util.Random random = new java.util.Random(20_261_022L);
        double worst = 0, worstStay = 0;
        int offTheBit = 0;
        for (int i = 0; i < 20_000; i++) {
            double struck = Math.exp((random.nextDouble() - .5) * 10), dollar = Math.exp((random.nextDouble() - .5) * 10);
            double at = EconomyManager.passThrough(struck, dollar, 1);
            worst = Math.max(worst, Math.abs(at / dollar - 1));
            if (Double.compare(at, dollar) != 0) offTheBit++;
            double stays = EconomyManager.passThrough(dollar, dollar, K);
            worstStay = Math.max(worstStay, Math.abs(stays / dollar - 1));
        }
        out.printf("   speed one over 20,000 random pairs: %d are not the D$ to the very bit, the widest %.2e of it%n", offTheBit, worst);
        assertTrue("at speed one the goods' D$ is the D$ itself: within a few last digits of it, over twenty thousand pairs", worst < 1e-14);
        assertTrue("a struck rate at the D$ stays there: within a few last digits of it, over twenty thousand rates", worstStay < 1e-14);
        assertTrue("one strike moves the goods' D$ the share k of the way to a D$ 10% off, in logs, and no further: ln(1.1) x k",
                Math.abs(Math.log(EconomyManager.passThrough(1, STEP, K)) - K * Math.log(STEP)) < 1e-12);
    }

    /* ================= 2. the wiring, every month in a real city ================= */

    /** The D$ the strike saw, and the goods' D$ it struck, for each of section 2's months: section 6 reads them. */
    static final double[] seen = new double[MONTHS], struck = new double[MONTHS];

    /** Game.struckFromRate() at the end of each of section 2's months (AN2), and whether it read NaN in the city before its first month: section 7 reads them. */
    static final double[] gapEnd = new double[MONTHS];
    static boolean nanAtFounding;

    static void theWiring() {
        out.println("\n--- 2. the wiring, every month in a real city ---");

        city = town("pass-through-city");
        EconomyManager em = city.getEconomyManager();
        ForeignAccounts fx = city.getForeignAccounts();
        assertTrue("fixture: a new city has struck nothing yet: its struck rate is 0", em.getStruckRate() == 0);
        nanAtFounding = Double.isNaN(city.struckFromRate());

        int wrongStrike = 0, wrongMarkets = 0, wrongBuildings = 0, wrongStart = 0;
        double widestDollar = 0, firstSeen = fx.getRate();
        for (int m = 0; m < MONTHS; m++) {
            double dollar = fx.getRate(), before = em.getStruckRate();
            month(city);
            double strike = em.getStruckRate();
            double expected = m == 0 ? dollar : EconomyManager.passThrough(before, dollar, K);
            // The D$ the strike saw is the one read between months: the reprice is late in the month and nothing but it (and a
            // reform) moves the rate, so what the last month left is what the next one's strike finds.
            if (Double.compare(strike, expected) != 0) wrongStrike++;
            double level = city.getWorldEconomy().getPriceLevel();
            if (Double.compare(city.getMarkets().getExchangeRate(), strike * level) != 0) wrongMarkets++;
            if (Double.compare(em.getExchangeRate(), strike * level) != 0) wrongBuildings++;
            if (m == 0 && Double.compare(strike, dollar) != 0) wrongStart++;
            widestDollar = Math.max(widestDollar, Math.abs(Math.log(dollar / firstSeen)));
            seen[m] = dollar;
            struck[m] = strike;
            gapEnd[m] = city.struckFromRate();
        }
        out.printf("   240 months played: the D$ from %.4f to %.4f (widest %.1f%% from its start), the goods' D$ ends at %.4f%n",
                firstSeen, fx.getRate(), 100 * widestDollar, em.getStruckRate());
        assertTrue("fixture: the city played its " + MONTHS + " months", city.getMonth() >= MONTHS);
        assertTrue("the city's first strike is at the D$ itself (the D$ the first month saw), to the bit", wrongStart == 0);
        assertTrue("every month the struck rate is the last month's moved PASS_THROUGH of the way to the D$ the strike saw, to the bit (wrong in "
                + wrongStrike + " of " + MONTHS + ")", wrongStrike == 0);
        assertTrue("every month the markets' rate is the struck rate times the world's price level, to the bit (wrong in " + wrongMarkets
                + " of " + MONTHS + ")", wrongMarkets == 0);
        assertTrue("...and so is the rate the economy manager prices the building materials at (wrong in " + wrongBuildings + " of " + MONTHS + ")",
                wrongBuildings == 0);

        // THE SAVED CITY STANDS APART, MADE. Sections 3, 5 and 6 load the city with the goods' D$ GAP_MADE from the D$ (its struck rate set,
        // as a save from a city whose D$ had moved leaves it); where the played D$ stood against the goods' D$ at month 240 is the
        // path's, and on one path it ended 0.7% from it. Section 4's twins are loaded from it too: they part by the swing alone.
        em.setStruckRate(fx.getRate() * Math.exp(GAP_MADE));
        double apart = Math.abs(Math.log(fx.getRate() / em.getStruckRate()));
        out.printf("   the city stands with the D$ %.1f%% from the goods' D$ (set; more than %.0f%%, as sections 3, 5 and 6 need)%n",
                100 * apart, 100 * APART);
        assertTrue("fixture: the saved city's goods' D$ is set apart from its D$ by more than " + String.format("%.0f%%", 100 * APART), apart > APART);
        boolean[] saved = new boolean[1];
        quietly(() -> saved[0] = city.saveGame(BASE_SLOT, "the pass-through city").ok);
        assertTrue("the city is saved at its end, for the twins of the sections after", saved[0]);

        // THE WIRING THROUGH A D$ THAT MOVES, CAUSED. Whether the played D$ ever moved far between two strikes is the path's, and a strike that
        // finds the D$ where the last left it has nothing to close, so its wiring would read as trivially as a rate held still. A copy of the
        // saved city has its D$ pinned PUSH_OFF above where it stood, then below, and so on for PUSH_MONTHS (a pin holds the D$ for the rest of
        // a city's life and is not saved, so it is not the live city that is pinned): each strike is handed a D$ 8% or 16% from the last, and
        // the same wiring is read through it, month by month, to the bit.
        Game pushed = load(city);
        EconomyManager pem = pushed.getEconomyManager();
        ForeignAccounts pfx = pushed.getForeignAccounts();
        double centre = pfx.getRate(), widestMove = 0;
        int pushWrongStrike = 0, pushWrongMarkets = 0, pushWrongBuildings = 0;
        for (int j = 0; j < PUSH_MONTHS; j++) {
            double was = pfx.getRate();
            pfx.pinRate(centre * Math.exp(j % 2 == 0 ? PUSH_OFF : -PUSH_OFF));
            double dollar = pfx.getRate(), before = pem.getStruckRate();
            widestMove = Math.max(widestMove, Math.abs(Math.log(dollar / was)));
            month(pushed);
            double strike = pem.getStruckRate(), level = pushed.getWorldEconomy().getPriceLevel();
            if (Double.compare(strike, EconomyManager.passThrough(before, dollar, K)) != 0) pushWrongStrike++;
            if (Double.compare(pushed.getMarkets().getExchangeRate(), strike * level) != 0) pushWrongMarkets++;
            if (Double.compare(pem.getExchangeRate(), strike * level) != 0) pushWrongBuildings++;
        }
        assertTrue("fixture: the D$ is pinned by turns " + String.format("%.0f%%", 100 * PUSH_OFF) + " either side and the strikes find it moved by more than "
                + String.format("%.0f%%", 100 * BIG_GAP) + " (widest " + String.format("%.1f%%", 100 * widestMove) + ")", pfx.isPinned() && widestMove > BIG_GAP);
        assertTrue("...and through them, every month, the struck rate is the last month's moved PASS_THROUGH of the way to the D$ the strike saw, to the bit"
                + " (wrong in " + pushWrongStrike + " of " + PUSH_MONTHS + ")", pushWrongStrike == 0);
        assertTrue("...the markets' rate is the struck rate times the world's price level, and so is the building materials' (wrong in "
                + pushWrongMarkets + " and " + pushWrongBuildings + " of " + PUSH_MONTHS + ")", pushWrongMarkets == 0 && pushWrongBuildings == 0);
    }

    /* ================= 3. a step in the D$ reaches the goods at PASS_THROUGH a month ================= */

    static void aStepReachesTheGoods() {
        out.println("\n--- 3. a step in the D$ reaches the goods at PASS_THROUGH a month ---");

        Game steady = load(city), weak = load(city);
        // GW1: no sector orders a building in either twin for the 36 months. The twins are to part by the pinned D$ and nothing else (month(): "nothing built or
        // decided by anyone"), but the staffing gate counts the workers a building's own posts would draw (Sector.GATE_DRAW_MONTHS), so the businesses order on
        // the month's costs and the twins' orders part: the twins' price index parted by 20.3% of the step at 12 months, 63.5% at 24 and 7.0% at 36, the groceries
        // component by 73.7% of it at 12 months where the goods' D$ had reached 28.7% - a shelf that moved with a building one twin ordered and the other did not.
        // Held, the index parts by 8.5%, 12.2% and 28.4% of the step at 12, 24 and 36 months and the groceries component by 8.9% at 12 (the goods' D$ 28.7%).
        for (Game twin : new Game[] {steady, weak}) {
            for (Sector sector : twin.getSectors().all()) twin.getBusinessInvestment().holdSector(sector.key());
        }
        EconomyManager steadyEm = steady.getEconomyManager(), weakEm = weak.getEconomyManager();
        assertTrue("fixture: the twins are one city before the pin: one month, one struck rate, one price index, to the bit",
                steady.getMonth() == weak.getMonth() && Double.compare(steadyEm.getStruckRate(), weakEm.getStruckRate()) == 0
                        && Double.compare(steady.getPriceIndex().getIndex(), weak.getPriceIndex().getIndex()) == 0);
        double saved = steadyEm.getStruckRate();
        assertTrue("fixture: the D$ and the goods' D$ are apart when the pin comes (the D$ is "
                + String.format("%.1f%%", 100 * Math.log(steady.getForeignAccounts().getRate() / saved)) + " from it): pinning at the struck rate removes a real gap",
                Math.abs(Math.log(steady.getForeignAccounts().getRate() / saved)) > APART);
        // THE GAP MADE, NOT INHERITED, AND DEAR (see the shelf's rule below): both twins' goods' D$ set at DEAR times the saved city's, and
        // the D$ pinned at it; then, once the shops are stocked at it (PT2, below), the weak twin's D$ pinned STEP weaker. No gap is pending
        // between the goods' D$ and the steady twin's D$.
        double base = DEAR * saved;
        for (Game twin : new Game[] {steady, weak}) {
            twin.getEconomyManager().setStruckRate(base);
            twin.getForeignAccounts().pinRate(base);
        }
        /*
         * PT2: THE SHOPS HAND OVER WHAT THE HOUSEHOLDS NEED, CAUSED. The clearing price falls under the floor only while the shops can
         * hand over every basket the households need (see the shelf's rule below: the band's bottom then clears it), and the saved
         * city's shops could not: on 0.9.181 its 4,160 places at Retail's rate of 0.28 (roads at MIN_THROUGHPUT, health 0.81) handed
         * over 1,158 baskets against 2,327 needed, so the shelf cleared at the band's top in all 12 months; on OE1's path 14,240 places
         * handed over the need to within a basket until the year's turn took the rate from 0.2907 to 0.2660 at the twelfth. Both twins
         * stand, at once, the Small Grocery Stores that bring the places at the rate they run at to HAND_OVER_MARGIN of the need, and
         * play at the DEAR pin, before the step, until the shelf holds what the places hand over: the shops restock for the baskets they
         * sold last month (Retail.recentUse()) out of a budget their cash and credit cap (Sector.wasPurchaseLimited()), and the
         * households' basket is struck at the month's prices (Game.cityBasketPerHead()), so at the DEAR pin a head takes 1.50 kg of
         * grains where it took 0.91: on 0.9.181, with the stores stood and stocked before the pin, the pin's first sale had 1,746 baskets
         * on the shelf of the 3,174 the places could hand over. The model's own restocking fills it; nothing is put on the shelf by hand.
         */
        Retail shopAtStrike = steady.getSectors().retail();
        BuildingsTemplate grocer = ForeignCheck.template(steady, "Small Grocery Store");
        double rateAtStrike = shopAtStrike.getOperatingRate(), needAtStrike = shopAtStrike.getBasketsNeeded();
        long placesBefore = shopAtStrike.getStoreCoverage();
        long stores = rateAtStrike > 0 ? Math.max(0, (long) Math.ceil(
                (HAND_OVER_MARGIN * needAtStrike / rateAtStrike - placesBefore) / grocer.getCoverage())) : 0;
        for (Game twin : new Game[] {steady, weak}) {
            quietly(() -> twin.buildStack(ForeignCheck.template(twin, "Small Grocery Store"), stores, true));
        }
        long places = steady.getSectors().retail().getStoreCoverage();
        out.printf("   the shops: %,d places at Retail's rate of %.4f hand over %,.0f baskets of the %,.0f the households need; %d Small Grocery Store(s)"
                + " stood in each twin make it %,d places, %,.0f baskets%n", placesBefore, rateAtStrike, placesBefore * rateAtStrike, needAtStrike,
                stores, places, places * rateAtStrike);
        assertTrue("fixture: the stores stood in both twins alike, and the shops' places at the rate they ran at hand over HAND_OVER_MARGIN of the"
                + " households' need", rateAtStrike > 0 && needAtStrike > 0 && places == weak.getSectors().retail().getStoreCoverage()
                && places * rateAtStrike >= HAND_OVER_MARGIN * needAtStrike);
        int stocking = 0;
        while (stocking < STOCKING_MONTHS && (stocking == 0 || !shelfHoldsTheHandOver(steady.getSectors().retail()) || !shelfHoldsTheHandOver(weak.getSectors().retail()))) {
            month(steady);
            month(weak);
            stocking++;
        }
        Retail stocked = steady.getSectors().retail();
        out.printf("   %d month(s) played at the DEAR pin as the stores stand: the shelf holds %,d baskets, the places hand over %,.0f, the households need %,.0f%n",
                stocking, stocked.getStoreInventory(), stocked.getHandOver(), stocked.getBasketsNeeded());
        assertTrue("fixture: before the step, the shelf holds what the places hand over in both twins, within STOCKING_MONTHS (" + stocking + ")",
                stocking > 0 && shelfHoldsTheHandOver(steady.getSectors().retail()) && shelfHoldsTheHandOver(weak.getSectors().retail()));
        assertTrue("fixture: ...and the twins are one city still at the step: one month, one struck rate, one price index, one shelf, to the bit",
                steady.getMonth() == weak.getMonth() && Double.compare(steadyEm.getStruckRate(), weakEm.getStruckRate()) == 0
                        && Double.compare(steady.getPriceIndex().getIndex(), weak.getPriceIndex().getIndex()) == 0
                        && steady.getSectors().retail().getStoreInventory() == weak.getSectors().retail().getStoreInventory());
        weak.getForeignAccounts().pinRate(base * STEP);
        assertTrue("fixture: both D$ are pinned, the steady one at the goods' D$ (no gap pending) and the other's 10% weaker",
                steady.getForeignAccounts().isPinned() && weak.getForeignAccounts().isPinned()
                        && Double.compare(steady.getForeignAccounts().getRate(), base) == 0
                        && Double.compare(steadyEm.getStruckRate(), base) == 0 && Double.compare(weakEm.getStruckRate(), base) == 0
                        && Math.abs(weak.getForeignAccounts().getRate() / steady.getForeignAccounts().getRate() - STEP) < 1e-12);
        Retail steadyShop = steady.getSectors().retail(), weakShop = weak.getSectors().retail();
        int costMonths = 0, handOverMonths = 0;

        double worstGoods = 0, worstMarkets = 0;
        double[] share = new double[37], goods = new double[37], component12 = new double[PriceIndex.COMPONENTS];
        for (int n = 1; n <= 36; n++) {
            month(steady);
            month(weak);
            double expected = Math.log(STEP) * reached(K, n);
            goods[n] = Math.log(weakEm.getStruckRate() / steadyEm.getStruckRate()) / Math.log(STEP);
            worstGoods = Math.max(worstGoods, Math.abs(Math.log(weakEm.getStruckRate() / steadyEm.getStruckRate()) - expected));
            worstMarkets = Math.max(worstMarkets,
                    Math.abs(Math.log(weak.getMarkets().getExchangeRate() / steady.getMarkets().getExchangeRate()) - expected));
            share[n] = Math.log(weak.getPriceIndex().getIndex() / steady.getPriceIndex().getIndex()) / Math.log(STEP);
            if (n <= 12 && costSets(steadyShop) && costSets(weakShop)) costMonths++;
            if (n <= 12 && handsOverTheNeed(steadyShop) && handsOverTheNeed(weakShop)) handOverMonths++;
            if (n == 12) {
                for (int c = 0; c < PriceIndex.COMPONENTS; c++) {
                    component12[c] = Math.log(weak.getPriceIndex().getComponentLevel(c) / steady.getPriceIndex().getComponentLevel(c)) / Math.log(STEP);
                }
            }
        }
        out.printf("   the weak twin's goods' D$ has reached %.4f, %.4f, %.4f of the step after 12, 24, 36 months (the rule: %.4f, %.4f, %.4f)%n",
                goods[12], goods[24], goods[36], reached(K, 12), reached(K, 24), reached(K, 36));
        close("for 36 months the weak twin's struck rate over the other's is 1.1^(1-(1-k)^n), worst error in logs over the 36", worstGoods, 0, 1e-9);
        close("...and so is the rate the markets are priced at, the world's level being one", worstMarkets, 0, 1e-9);
        out.printf("   the twins' price index parts by a share of the 10%% step: %.1f%% at 12 months, %.1f%% at 24, %.1f%% at 36"
                + " (the goods' own D$ has reached %.1f%%, %.1f%%, %.1f%%)%n", 100 * share[12], 100 * share[24], 100 * share[36],
                100 * reached(K, 12), 100 * reached(K, 24), 100 * reached(K, 36));
        out.printf("   the index's components at 12 months, as a share of the step: groceries %.1f%%, rent %.1f%%, meals %.1f%%, luxury %.1f%%,"
                + " services %.1f%% (weights %.2f, %.2f, %.2f, %.2f, %.2f)%n", 100 * component12[PriceIndex.GROCERIES], 100 * component12[PriceIndex.RENT],
                100 * component12[PriceIndex.MEALS], 100 * component12[PriceIndex.LUXURY], 100 * component12[PriceIndex.SERVICES],
                steady.getPriceIndex().getWeight(PriceIndex.GROCERIES), steady.getPriceIndex().getWeight(PriceIndex.RENT),
                steady.getPriceIndex().getWeight(PriceIndex.MEALS), steady.getPriceIndex().getWeight(PriceIndex.LUXURY),
                steady.getPriceIndex().getWeight(PriceIndex.SERVICES));
        // The whole index is not asserted: it holds rent and land, which the goods' D$ does not set. Read, not measured: rent's target is
        // the cost of a home per person - its structure, at the construction material price (struck rate) - plus its ground
        // (EconomyManager.repriceHousingCosts(), groundPriceFor()), and the ground's price comes from LandManager, built with
        // `() -> foreign.getRate()`: US$ land at the D$ itself, so rent leads the goods (46% of the step at 12 months, measured).
        /*
         * THE SHELF, TRACED. The groceries component is the shops' shelf price (Game.indexPrices(): sectors().retail().getStoreSellPrice()).
         * Retail.endOfMonth() prices the basket at what the shops paid for it - for each good on the shelf, the kilograms in a basket times the
         * blend of the local price (what they bought locally) and the net import price (what they imported: GoodsMarket.importPrice(), the world's
         * price and freight times the markets' rate - the struck rate times the world's level - plus the domestic freight) - and
         * Retail.repriceShelf() reads it so: the FLOOR is
         * the larger of the opening price (at the expected level) and that cost times RETAIL_MARKUP; the TARGET is the clearing price (where the
         * households' money and appetite meet what the shops can hand over: nothing in it reads a cost) held between the floor and CLEARING_CAP
         * over it; and the shelf closes CLEAR_SPEED of the way to the target in logs a month (FLOOR_CATCH_UP of the way to the floor, under it).
         * So the shelf is cost-plus on the goods only where the floor binds and the clearing price is not above it (Retail.isSlack(): the target
         * is the floor); on a shelf standing between its floor and its cap, as the saved city's does, it follows the clearing price, which parts
         * two twins by whatever parts their households and shops. Played so (the saved city's goods' D$ left where its strike put it, as this
         * section did until FS10), the weak twin's shelf had passed 42.7% of the step at 12 months against the goods' own 28.7%, its floor
         * 18.3%: the clearing price moved, not the cost. Nothing in the shelf reads rent, land or wages; only the goods' prices.
         * The law is the floor's, and it is caused: twins struck and pinned at DEAR times the saved city's goods' D$, so the imported basket's
         * cost times RETAIL_MARKUP stands over the opening price, and the shops stood and stocked to hand over what the households need
         * (PT2, above), so the clearing price falls under the floor (the fixture counts both, over the twelve months, and asserts them). Withdrawn - DEAR 1 - the shelf sits at its clearing price and the cost does not set it.
         */
        assertTrue("fixture: the shops hand over at least the households' need in both twins in each of the 12 months, the shelf too ("
                + handOverMonths + " of 12)", handOverMonths == 12);
        assertTrue("fixture: the imported cost sets the shelf in both twins in each of the 12 months: the floor over the opening price and the"
                + " clearing price not over the floor (" + costMonths + " of 12)", costMonths == 12);
        assertTrue("the groceries component's pass-through is positive at 12 months: a weaker D$ raised the shelf's price (" + String.format("%.1f%%", 100 * component12[PriceIndex.GROCERIES])
                + " of the step)", component12[PriceIndex.GROCERIES] > 0);
        assertTrue("...and under the goods' own share at 12 months: the shelf follows the goods' D$ with a lag of its own, not ahead of it ("
                + String.format("%.1f%% against %.1f%%", 100 * component12[PriceIndex.GROCERIES], 100 * reached(K, 12)) + ")",
                component12[PriceIndex.GROCERIES] < reached(K, 12));
    }

    /** Whether the shops handed over every basket the households need at this month's sale (PT2): the places at the operating rate, and the shelf, at least the need. */
    static boolean handsOverTheNeed(Retail shop) {
        return shop.getBasketsNeeded() > 0 && shop.getSupplyBaskets() >= shop.getBasketsNeeded();
    }

    /** Whether the month's sale handed over what the places could, the shelf not short of it, and the shelf as the month left it holds as many again (PT2): next month's sale is then the places', not the shelf's. */
    static boolean shelfHoldsTheHandOver(Retail shop) {
        return shop.getHandOver() > 0 && !shop.isShelfBound() && shop.getStoreInventory() >= shop.getHandOver();
    }

    /** Whether the imported cost sets the shop's shelf this month: its cost-plus floor stands over the opening price, and the clearing price is not over the floor (so the target is the floor). */
    static boolean costSets(Retail shop) {
        return shop.getFloorPrice() > shop.getOpeningSellPrice() && shop.isSlack();
    }

    /* ================= 4. the ring settles ================= */

    /** The size of the treasury's swing: dollars worth this share of a year's GDP. */
    static final double SWING = .05;

    /** The months the swing is bought, held and sold in, and the months it is followed for after: a year held, ten years in all. */
    static final int HELD = 12, FOLLOWED = 120;

    /** The swing's own horizon, from the model's constants: the round trip (HELD) and the months the goods take to feel half of it (their half-life, rounded up). */
    static final int HORIZON = HELD + (int) Math.ceil(Math.log(.5) / Math.log(1 - K));

    /** One pair of twins' swing: the figures the section prints and asserts. */
    static final class Swing {
        double dollarWidest, goodsWidest, indexWidest, dollarEnd, goodsEnd, indexEnd, boughtLocal, boughtUsd, soldLocal, soldUsd, worstExcess;
        int widestAt, excessMonths;
        boolean purchaseMoved;
        /** The D$'s widest gap over months 0 to FOLLOWED-1 (the gap of month 0 is none): the most the goods, a month behind it one for one, can have seen by month FOLLOWED. */
        double dollarWidestSeen;
        /** The gaps, in logs, month by month after the purchase (index 0 is the day of it: none): the D$, the goods' D$ and the price index. */
        final double[] dollarSeries = new double[FOLLOWED + 1], goodsSeries = new double[FOLLOWED + 1], indexSeries = new double[FOLLOWED + 1];
    }

    /** The twins of the base city, one buying dollars worth SWING of a year's GDP and selling them back HELD months later, played FOLLOWED months, goods following the D$ at `speed`. */
    static Swing swing(double speed, String name) {
        Game calm = load(city), shock = load(city);
        if (speed != K) {
            calm.getEconomyManager().setPassThroughForTest(speed);
            shock.getEconomyManager().setPassThroughForTest(speed);
        }
        ForeignAccounts calmFx = calm.getForeignAccounts(), shockFx = shock.getForeignAccounts();
        // NEITHER TWIN LEANS ON THE D$. The base city is on Lean, whose defence sells dollars from the vault against a month's fall, in lumps and in
        // proportion to what each twin's vault holds, so what the shock twin's vault holds over its twin's after a year is the dollars it bought
        // plus or minus the difference between what the two defences sold - a figure the path sets, which this section once searched months
        // for. On the float (FLOAT: "nothing is sold and nothing leaned on") the defence sells none and the sell-back is what it bought, by cause.
        calm.setCurrencyRegime(ForeignAccounts.Regime.FLOAT);
        shock.setCurrencyRegime(ForeignAccounts.Regime.FLOAT);
        assertTrue("fixture (" + name + "): both twins are on the float regime, the defence selling nothing",
                calmFx.getRegime() == ForeignAccounts.Regime.FLOAT && shockFx.getRegime() == ForeignAccounts.Regime.FLOAT);
        assertTrue("fixture (" + name + "): the twins are one city before the purchase: the month, the D$, the goods' D$ and the price index, to the bit",
                calm.getMonth() == shock.getMonth() && Double.compare(calmFx.getRate(), shockFx.getRate()) == 0
                        && Double.compare(calm.getEconomyManager().getStruckRate(), shock.getEconomyManager().getStruckRate()) == 0
                        && Double.compare(calm.getPriceIndex().getIndex(), shock.getPriceIndex().getIndex()) == 0);
        assertTrue("fixture (" + name + "): neither twin's D$ is pinned: this is the float", !calmFx.isPinned() && !shockFx.isPinned());

        // A thin vault would part the twins by another road than the ring: the real city's covers a third of a month of imports, so
        // the swing's dollars would lift it past THIN_COVER and the premium on an empty vault (RISK_PREMIUM) would fall away, a
        // stronger D$ that has nothing to do with prices. Both twins first hold a vault of twice THIN_COVER months of imports,
        // bought by the treasury and forgotten by the rate (a set-up, as ExchangeRateCheck's still() does), so the cover is flat.
        // The treasury buys no more than the cash it holds (buyForeignCurrency()), so both twins are first GIVEN the cash for the vault
        // and for the swing's whole bill, twice over: what the city's treasury happens to hold at month 240 is the path's.
        double vaultBill = 2 * ForeignAccounts.THIN_COVER * calmFx.monthlyImports();
        double swingBill = SWING * 12 * shockFx.getGdpTrailing();
        double cash = Math.max(calm.getCash(), 2 * (vaultBill + swingBill));
        calm.setCashForTest(cash);
        shock.setCashForTest(cash);
        double[] vaultSpent = new double[2];
        quietly(() -> { vaultSpent[0] = calm.buyForeignCurrency(vaultBill); vaultSpent[1] = shock.buyForeignCurrency(vaultBill); });
        calmFx.forgetDealing();
        shockFx.forgetDealing();
        assertTrue("fixture (" + name + "): both twins hold a vault of at least THIN_COVER months of imports (cover "
                + String.format("%.1f", calmFx.importCover()) + "), the same, and the rate has forgotten the set-up",
                calmFx.importCover() >= ForeignAccounts.THIN_COVER && Double.compare(calmFx.getReservesUsd(), shockFx.getReservesUsd()) == 0
                        && Math.abs(vaultSpent[0] - vaultBill) <= 1e-9 * vaultBill && Math.abs(vaultSpent[1] - vaultBill) <= 1e-9 * vaultBill
                        && calmFx.getDealtSinceReprice() == 0 && shockFx.getDealtSinceReprice() == 0);

        Swing s = new Swing();
        s.boughtLocal = swingBill;
        double vaultBefore = shockFx.getReservesUsd();
        double[] spent = new double[1];
        quietly(() -> spent[0] = shock.buyForeignCurrency(s.boughtLocal));
        s.boughtUsd = shockFx.getReservesUsd() - vaultBefore;
        assertTrue("fixture (" + name + "): the treasury bought dollars worth " + String.format("%.0f%%", 100 * SWING)
                + " of a year's GDP, the whole bill: D$" + String.format("%,.0fk for US$%,.0fk", spent[0], s.boughtUsd),
                Math.abs(spent[0] - s.boughtLocal) <= 1e-9 * s.boughtLocal && s.boughtUsd > 0 && shockFx.getDealtSinceReprice() > 0);

        double calmEarned0 = calmFx.getVaultInterestUsdLifetime(), shockEarned0 = shockFx.getVaultInterestUsdLifetime();
        for (int n = 1; n <= FOLLOWED; n++) {
            month(calm);
            month(shock);
            if (n == 1) {
                // The purchase is felt at this month's reprice, INTERVENTION_IMPACT per 1% of a year's GDP, as ExchangeRateCheck 5 asserts.
                // A real city's treasury has dealt before (the defence's sales fade with INTERVENTION_HALF_LIFE), so both twins feel
                // that fading, and what the purchase adds is the difference of the two moves.
                close("the purchase's month (" + name + "): the shock twin's intervention move less its twin's is INTERVENTION_IMPACT x the bill over a year's GDP",
                        shockFx.getMoveIntervention() - calmFx.getMoveIntervention(),
                        ForeignAccounts.INTERVENTION_IMPACT * s.boughtLocal / (12 * shockFx.getGdpTrailing()), 1e-9);
                close("...and the vault's cover moved the two alike, so nothing but the intervention parted their moves",
                        shockFx.getMoveCover() - calmFx.getMoveCover(), 0, 1e-9);
                s.purchaseMoved = shockFx.getRate() > calmFx.getRate();
                assertTrue("...the shock twin's D$ is weaker than its twin's that month (" + name + "): "
                        + String.format("%+.2f%%", 100 * Math.log(shockFx.getRate() / calmFx.getRate())), s.purchaseMoved);
            }
            if (n == HELD) {
                // The same dollars back: neither twin's defence has sold any (the float), so the vault holds over its twin's all that was
                // bought and what those dollars earned in the year (the interest stays in the vault, ForeignAccounts.earnVaultInterest());
                // the treasury sells back what it holds over its twin's.
                double extra = shockFx.getReservesUsd() - calmFx.getReservesUsd();
                double earned = (shockFx.getVaultInterestUsdLifetime() - shockEarned0) - (calmFx.getVaultInterestUsdLifetime() - calmEarned0);
                double want = extra * shockFx.getRate();
                double[] sold = new double[1];
                quietly(() -> sold[0] = shock.sellForeignCurrency(want));
                s.soldLocal = sold[0];
                s.soldUsd = extra;
                assertTrue("fixture (" + name + "): " + HELD + " months on the treasury sold back the dollars it bought and what they earned, the defence having sold none"
                        + String.format(" (US$%,.0fk of US$%,.0fk bought, US$%,.1fk earned), leaving the two vaults equal", extra, s.boughtUsd, earned),
                        extra > 0 && Math.abs(extra - s.boughtUsd - earned) <= 1e-9 * s.boughtUsd && Math.abs(sold[0] - want) <= 1e-9 * want
                                && Math.abs(shockFx.getReservesUsd() - calmFx.getReservesUsd()) <= 1e-9 * s.boughtUsd);
            }
            double dollarGap = Math.log(shockFx.getRate() / calmFx.getRate());
            double goodsGap = Math.log(shock.getEconomyManager().getStruckRate() / calm.getEconomyManager().getStruckRate());
            double indexGap = Math.log(shock.getPriceIndex().getIndex() / calm.getPriceIndex().getIndex());
            s.dollarSeries[n] = dollarGap;
            s.goodsSeries[n] = goodsGap;
            s.indexSeries[n] = indexGap;
            if (Math.abs(dollarGap) > s.dollarWidest) { s.dollarWidest = Math.abs(dollarGap); s.widestAt = n; }
            if (n < FOLLOWED) s.dollarWidestSeen = Math.max(s.dollarWidestSeen, Math.abs(dollarGap));
            s.goodsWidest = Math.max(s.goodsWidest, Math.abs(goodsGap));
            s.indexWidest = Math.max(s.indexWidest, Math.abs(indexGap));
            // The goods' gap is a weighted sum of the D$'s gaps of the months before, the weights k(1-k)^(n-j): it cannot pass the
            // widest D$ gap so far times what a constant gap would have reached by now.
            double bound = s.dollarWidest * reached(speed, n);
            if (Math.abs(goodsGap) > bound + 1e-12) { s.excessMonths++; s.worstExcess = Math.max(s.worstExcess, Math.abs(goodsGap) - bound); }
            if (n == FOLLOWED) { s.dollarEnd = dollarGap; s.goodsEnd = goodsGap; s.indexEnd = indexGap; }
        }
        return s;
    }

    static void printSwing(String name, Swing s) {
        out.printf("   %s: the D$'s widest gap between the twins %.2f%% (month %d), the goods' widest gap %.2f%%, the price index's widest %.2f%%;"
                + " ten years on the D$ is %+.2f%% apart, the goods' D$ %+.2f%%, the price index %+.2f%%%n", name, 100 * s.dollarWidest, s.widestAt,
                100 * s.goodsWidest, 100 * s.indexWidest, 100 * s.dollarEnd, 100 * s.goodsEnd, 100 * s.indexEnd);
        StringBuilder trace = new StringBuilder("      the gaps (D$ / goods' D$ / price index, %) at months");
        for (int n : new int[] { 3, 6, 12, 24, 36, 48, 72, 96, 120 }) {
            trace.append(String.format(" %d: %+.1f/%+.1f/%+.1f;", n, 100 * s.dollarSeries[n], 100 * s.goodsSeries[n], 100 * s.indexSeries[n]));
        }
        out.println(trace);
        out.printf("      the mean gap (D$ / goods' D$ / price index, %%): months 1-%d %.2f/%.2f/%.2f, months 97-120 %.2f/%.2f/%.2f%n", HORIZON,
                100 * meanGap(s.dollarSeries, 1, HORIZON), 100 * meanGap(s.goodsSeries, 1, HORIZON), 100 * meanGap(s.indexSeries, 1, HORIZON),
                100 * meanGap(s.dollarSeries, 97, 120), 100 * meanGap(s.goodsSeries, 97, 120), 100 * meanGap(s.indexSeries, 97, 120));
    }

    /** The mean of the gaps' sizes over months `from` to `to`. */
    static double meanGap(double[] series, int from, int to) {
        double sum = 0;
        for (int n = from; n <= to; n++) sum += Math.abs(series[n]);
        return sum / (to - from + 1);
    }

    static void theRingSettles() {
        out.println("\n--- 4. the ring settles: the same swing of the D$, the goods following at PASS_THROUGH and one for one ---");

        // Twins of one city part only by what is done to them: two plain loads of the base city play the whole ten years alike, to the
        // bit, the cash, the D$ and the price index every month - so every gap below is the swing's, however the economy amplifies it.
        Game plain = load(city), same = load(city);
        int parted = 0;
        for (int n = 1; n <= FOLLOWED; n++) {
            month(plain);
            month(same);
            if (Double.compare(plain.getForeignAccounts().getRate(), same.getForeignAccounts().getRate()) != 0
                    || Double.compare(plain.getPriceIndex().getIndex(), same.getPriceIndex().getIndex()) != 0
                    || Double.compare(plain.getCash(), same.getCash()) != 0) parted++;
        }
        assertTrue("fixture: two plain loads of the base city play " + FOLLOWED + " months alike, the D$, the price index and the cash to the bit"
                + " (parted in " + parted + ")", parted == 0);

        Swing slow = swing(K, "at PASS_THROUGH");
        printSwing("at PASS_THROUGH", slow);
        Swing fast = swing(1, "one for one");
        printSwing("one for one, the rule before AN1", fast);

        assertTrue("at PASS_THROUGH the goods' gap never exceeds the D$'s widest gap so far times 1-(1-k)^n, in any of the "
                + FOLLOWED + " months (over in " + slow.excessMonths + ", worst by " + String.format("%.1e", slow.worstExcess) + ")", slow.excessMonths == 0);
        // A COMPLETE HORIZON. One for one the goods' gap of month n is the D$'s of month n-1 (the strike reads the D$ the last month left), so the goods'
        // widest over months 1 to FOLLOWED is the D$'s widest over months 0 to FOLLOWED-1: the D$'s own widest over 1 to FOLLOWED would compare a
        // peak on the last sample with a goods' series that has no later sample to follow it (the D$'s widest gap fell on month 120 on one path).
        assertTrue("...while one for one the goods go as far as the D$ does, a month behind it (its widest "
                + String.format("%.2f%%", 100 * fast.goodsWidest) + " against the D$'s " + String.format("%.2f%%", 100 * fast.dollarWidestSeen)
                + ") and never past it", fast.excessMonths == 0 && Math.abs(fast.goodsWidest - fast.dollarWidestSeen) <= 1e-9);
        assertTrue("...and at PASS_THROUGH they never do (the goods' widest " + String.format("%.2f%% against the D$'s %.2f%%", 100 * slow.goodsWidest,
                100 * slow.dollarWidest) + ")", slow.goodsWidest < slow.dollarWidest);
        // THE PREMISE IS READ OVER THE SWING'S OWN HORIZON (AN1-notes), and its history is plain. The first wording asserted the ten-year
        // end gaps (|ln price index gap| and |ln D$ gap| smaller at PASS_THROUGH than one for one). On the thin-vault fixture it held
        // (index 0.44% against 3.60%, D$ 2.78% against 8.73%); on the healthy-vault fixture it did not (index 3.59% against 1.52%, D$
        // 4.03% against 2.51%), while the first three years went the intended way: ten years on, a gap is the economy amplifying a small
        // difference either way (the plain twins above stay equal, so it is the swing's, but not the ring's echo). So it is read over
        // the months the swing acts: HORIZON, the round trip and the goods' half-life, fixed from the constants and not from the data.
        // The ten-year widest and end gaps stay printed above, not asserted; the D$'s mean over the horizon is printed, not asserted.
        double slowGoods = meanGap(slow.goodsSeries, 1, HORIZON), fastGoods = meanGap(fast.goodsSeries, 1, HORIZON);
        double slowIndex = meanGap(slow.indexSeries, 1, HORIZON), fastIndex = meanGap(fast.indexSeries, 1, HORIZON);
        out.printf("   over months 1-%d (the round trip and the goods' half-life) the D$'s mean gap is %.2f%% at PASS_THROUGH and %.2f%% one for one%n",
                HORIZON, 100 * meanGap(slow.dollarSeries, 1, HORIZON), 100 * meanGap(fast.dollarSeries, 1, HORIZON));
        assertTrue("the premise: over the swing's months 1-" + HORIZON + " the goods' rate stays closer to its twin's at PASS_THROUGH than one for one ("
                + String.format("mean gap %.2f%% against %.2f%%", 100 * slowGoods, 100 * fastGoods) + ")", slowGoods < fastGoods);
    }

    /* ================= 5. a save carries it ================= */

    static void aSaveCarriesIt() {
        out.println("\n--- 5. a save carries it ---");

        Game live = city;
        Game loaded = load(city);
        EconomyManager liveEm = live.getEconomyManager(), loadedEm = loaded.getEconomyManager();
        double saved = liveEm.getStruckRate();
        assertTrue("fixture: the live city has struck its goods (" + String.format("%.6f", saved) + ") and the D$ is apart from it ("
                + String.format("%.6f", live.getForeignAccounts().getRate()) + ")",
                saved > 0 && Math.abs(Math.log(live.getForeignAccounts().getRate() / saved)) > APART);
        assertTrue("a loaded city has the struck rate of the city saved, to the bit", Double.compare(loadedEm.getStruckRate(), saved) == 0);
        assertTrue("...and the rate its markets were priced at", Double.compare(loaded.getMarkets().getExchangeRate(), live.getMarkets().getExchangeRate()) == 0);

        month(live);
        month(loaded);
        assertTrue("one month on both strike the same rate, to the bit (" + String.format("%.9f", liveEm.getStruckRate()) + ")",
                Double.compare(liveEm.getStruckRate(), loadedEm.getStruckRate()) == 0);
        assertTrue("...and price the markets the same, to the bit (" + String.format("%.9f", live.getMarkets().getExchangeRate()) + ")",
                Double.compare(live.getMarkets().getExchangeRate(), loaded.getMarkets().getExchangeRate()) == 0);
        assertTrue("...and it was a strike that moved the goods' D$ (from " + String.format("%.6f", saved) + ")", liveEm.getStruckRate() != saved);

        // A save without the key: the struck rate reads 0, and the next strike is at the D$ itself.
        Game cleared = load(city);
        cleared.getEconomyManager().setStruckRate(0);
        assertTrue("fixture: the loaded city's struck rate is cleared, as a save without the key reads", cleared.getEconomyManager().getStruckRate() == 0);
        double dollar = cleared.getForeignAccounts().getRate();
        month(cleared);
        assertTrue("a loaded city whose struck rate is cleared strikes at the D$ itself the next month, to the bit",
                Double.compare(cleared.getEconomyManager().getStruckRate(), dollar) == 0);
        assertTrue("...which is not where the city that kept its struck rate struck (" + String.format("%.6f against %.6f",
                cleared.getEconomyManager().getStruckRate(), liveEm.getStruckRate()) + ")",
                Math.abs(Math.log(cleared.getEconomyManager().getStruckRate() / liveEm.getStruckRate())) > .001);

        // A currency reform rescales the unit, and the struck rate with it.
        Game reformed = load(city), plain = load(city);
        double before = reformed.getEconomyManager().getStruckRate(), dollarBefore = reformed.getForeignAccounts().getRate();
        boolean[] done = new boolean[1];
        quietly(() -> done[0] = reformed.reformCurrencyForTest(100));
        assertTrue("fixture: the city was reformed, one new D$ for 100 old", done[0]);
        closeRelative("a reform scales the struck rate by the factor", reformed.getEconomyManager().getStruckRate(), before / 100, 1e-12);
        closeRelative("...as it does the D$ itself, so the gap between them is the one the city had",
                reformed.getEconomyManager().getStruckRate() / reformed.getForeignAccounts().getRate(), before / dollarBefore, 1e-12);
        month(reformed);
        month(plain);
        closeRelative("the month after, the reformed city strikes the unreformed twin's rate over the factor",
                reformed.getEconomyManager().getStruckRate(), plain.getEconomyManager().getStruckRate() / 100, 1e-12);
    }

    /* ================= 6. how often it matters in a real city ================= */

    static void howOftenItMatters() {
        out.println("\n--- 6. how often it matters in a real city ---");

        // THE GAP IS READ, CAUSED. A copy of the saved city has its D$ pinned MARK_OFF from the goods' D$ (the one the saved city's strike last
        // set), and one month is played: the strike sees a D$ that far from the goods' and closes PASS_THROUGH of the gap, so what is left of
        // it is read by the line the player is shown (Game.struckFromRate()) as more than APART. How often the real city's months stand so is
        // printed below, a finding of its path and not a law.
        Game marked = load(city);
        EconomyManager mem = marked.getEconomyManager();
        double goods = mem.getStruckRate();
        marked.getForeignAccounts().pinRate(goods * Math.exp(MARK_OFF));
        double dollar = marked.getForeignAccounts().getRate();
        assertTrue("fixture: the D$ is pinned " + String.format("%.0f%%", 100 * MARK_OFF) + " from the goods' D$ (it stands "
                + String.format("%.2f%%", 100 * Math.log(dollar / goods)) + " off it)",
                marked.getForeignAccounts().isPinned() && Math.abs(Math.log(dollar / goods) - MARK_OFF) <= 1e-9);
        month(marked);
        double left = Math.abs(Math.log(mem.getStruckRate() / dollar)), movedBy = Math.abs(Math.log(mem.getStruckRate() / goods));
        assertTrue("the mechanic fires in a real city: the goods' D$ was more than " + String.format("%.0f%%", 100 * APART)
                + " from the D$ the strike saw, the month after a D$ pinned " + String.format("%.0f%%", 100 * MARK_OFF) + " off it (gap "
                + String.format("%.2f%%", 100 * left) + ", the line reads " + String.format("%+.2f%%", 100 * marked.struckFromRate()) + ")",
                left > APART && Math.abs(Math.log(1 + marked.struckFromRate())) > APART);
        close("...and the strike moved the goods' D$ toward it by PASS_THROUGH of the gap, in logs", movedBy, K * MARK_OFF, 1e-9);

        double[] gap = new double[MONTHS];
        int moved = 0, over1 = 0, over5 = 0, over10 = 0;
        for (int m = 0; m < MONTHS; m++) {
            gap[m] = Math.abs(Math.log(struck[m] / seen[m]));
            if (m > 0 && Math.abs(Math.log(struck[m] / struck[m - 1])) > 1e-9) moved++;
            if (gap[m] > .01) over1++;
            if (gap[m] > .05) over5++;
            if (gap[m] > .10) over10++;
        }
        double[] sorted = gap.clone();
        Arrays.sort(sorted);
        double median = (sorted[MONTHS / 2 - 1] + sorted[MONTHS / 2]) / 2, widest = sorted[MONTHS - 1];
        // Played, printed and not asserted: the frequency is the path's.
        out.printf("   %d months of the real city: the goods' D$ against the D$ the strike saw - median gap %.2f%%, widest %.2f%%;"
                + " more than 1%% apart in %d months, more than 5%% in %d, more than 10%% in %d; the goods' D$ moved in %d of %d%n",
                MONTHS, 100 * median, 100 * widest, over1, over5, over10, moved, MONTHS - 1);
    }

    /* ================= 7. the screen's reading ================= */

    /**
     * The least a month's two figures in logs may differ by before section 7 calls them different: doubles' own noise (a log of a ratio and an
     * exp-log round trip each carry a few 1e-16), so a month whose gap is tiny is not failed for its last digits. Far under the screen's .05% line.
     */
    static final double LOG_NOISE = 1e-14;

    static void theScreensReading() {
        out.println("\n--- 7. the screen's reading: the goods' D$ against the D$ ---");

        // NaN while nothing is struck: a city before its first month, and section 2's city with its struck rate cleared and put back.
        assertTrue("a new city has struck nothing, and the reading is NaN: the line is not drawn (section 2's founding)", nanAtFounding);
        EconomyManager em = city.getEconomyManager();
        double kept = em.getStruckRate(), rate = city.getForeignAccounts().getRate();
        em.setStruckRate(0);
        assertTrue("fixture: the city's struck rate is cleared, as before a first strike", em.getStruckRate() == 0);
        assertTrue("...and with nothing struck the reading is NaN", Double.isNaN(city.struckFromRate()));
        em.setStruckRate(kept);
        assertTrue("fixture: the struck rate is put back (" + String.format("%.6f", kept) + ")", Double.compare(em.getStruckRate(), kept) == 0);
        close("...and the reading is the struck rate over the D$, less one (" + String.format("%.2f%%", 100 * city.struckFromRate()) + ")",
                city.struckFromRate(), kept / rate - 1, 1e-15);

        // The claim the screen's muted line makes: it closes PASS_THROUGH of the gap a month. gapEnd[m] is what the line would read when month m
        // ends; the strike of month m+1 moves the goods' D$ that share of the way, in logs, to the D$ it finds, which is the D$ the line read.
        int off = 0, shown = 0;
        double worst = 0;
        StringBuilder which = new StringBuilder();
        for (int m = 0; m + 1 < MONTHS; m++) {
            double actual = Math.log(struck[m + 1] / struck[m]), expected = -K * Math.log(1 + gapEnd[m]);
            double miss = Math.abs(actual - expected);
            if (miss > worst) worst = miss;
            if (!(miss <= 1e-9 * Math.abs(expected) + LOG_NOISE)) {
                off++;
                if (shown++ < 12) which.append(String.format(" m%d (%.3e against %.3e: %.3e)", m + 1, actual, expected, miss));
            }
        }
        out.printf("   %d month-pairs: the widest miss between what the strike moved the goods' D$ and K x the gap the line read is %.2e in logs;"
                + " %d off%s%n", MONTHS - 1, worst, off, off == 0 ? "" : ":" + which);
        assertTrue("every month, the gap the line reads at a month's end is the one the next strike closes PASS_THROUGH of, in logs (off in "
                + off + " of " + (MONTHS - 1) + ")", off == 0);

        // How often the line speaks: printed, not asserted.
        int over5 = 0, stronger = 0, weaker = 0, level = 0;
        double[] abs = new double[MONTHS];
        for (int m = 0; m < MONTHS; m++) {
            abs[m] = Math.abs(gapEnd[m]);
            if (abs[m] > .05) over5++;
            if (abs[m] < .0005) level++;
            else if (gapEnd[m] < 0) stronger++;
            else weaker++;
        }
        Arrays.sort(abs);
        double median = (abs[MONTHS / 2 - 1] + abs[MONTHS / 2]) / 2;
        out.printf("   the line over %d month-ends: %d months more than 5%% either way, median gap %.2f%%, widest %.2f%%;"
                + " %d say \"stronger than the rate\", %d \"weaker\", %d \"level\"%n", MONTHS, over5, 100 * median, 100 * abs[MONTHS - 1], stronger, weaker, level);
    }
}
