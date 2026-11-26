package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static ham.citybuildersim.ConstructionTrapCheck.BUILDERS;
import static ham.citybuildersim.ConstructionTrapCheck.HOUSE;
import static ham.citybuildersim.ConstructionTrapCheck.RE;
import static ham.citybuildersim.ConstructionTrapCheck.audited;
import static ham.citybuildersim.ConstructionTrapCheck.auditLine;
import static ham.citybuildersim.ConstructionTrapCheck.calm;
import static ham.citybuildersim.ConstructionTrapCheck.filesOf;
import static ham.citybuildersim.ConstructionTrapCheck.month;
import static ham.citybuildersim.ConstructionTrapCheck.out;
import static ham.citybuildersim.ConstructionTrapCheck.quietly;
import static ham.citybuildersim.ConstructionTrapCheck.roads;
import static ham.citybuildersim.ConstructionTrapCheck.roadsFor;
import static ham.citybuildersim.ConstructionTrapCheck.template;

/**
 * The choices that use the foreign crews (FC2b): a payer - a business by its own sum, the city from its card - hands its
 * share of one site's remaining work to the foreign crews, who build it on that site at the pace and price struck then;
 * held on the calm city of ConstructionTrapCheck (its landlords' Houses on site at 0.8 of the knee), the queue at home made
 * long by standing Paved Roads, whose repairs take the sites' month.
 *
 * WHY. Jerus, 2026-11-22 (docs/queue/FC2.md, "## FC2b"): "allowing businesses to even decide to pay more to finish sooner
 * (for example, something finished in 20 months is better than something finished in 100 months, cause thats 80 more
 * months of paying interest...)", and "also let the city itself do it, like per building, aka for example if roads are
 * causing the issues then you could pay more to get just roads fixed". So the money spent already is sunk and only the work
 * still to do moves; a business moves when the extra is under what the months saved are worth to it, and stays when it is
 * over; the city pays from its treasury what its card shows and its site opens sooner; one order leaves a shared site and
 * the others' progress is not touched; and every dollar is booked once. The model is BuildersShort (the arithmetic, THE
 * CHOICE TO FINISH SOONER), BuildingsStacks (the home and abroad contracts of one stack), ConstructionControl (G. ABROAD),
 * BuildingManager (abroadTermsOf(), handSiteAbroad(), advanceConstruction()), Game (investorsHandAbroad(), abroadPreview(),
 * handSiteAbroad(), settleCrewsOnSites()) and sectors.Construction (payCrewsOnSites()). ForeignCrewsCheck holds FC2's own
 * crews, the threshold and the pooled book; this holds what a payer chooses on top of them.
 *
 * What this has to prove:
 *   1. THE ARITHMETIC: sitePace() floors at the depot, caps at the points and at a finite build-time cap, and is the points
 *      over the threshold's months otherwise; monthsAbroad() rounds up; broughtForward() is linear in its earnings, tends to
 *      perMonth x (Wh - Wa) as the rate goes to nothing, is P / i x (1 + i)^-Wa for a home start endless, and is the sum of
 *      the discounted months; and interestSaved(C) + broughtForward(P - C x i) is broughtForward(P): the two parts, no
 *      double count.
 *   2. A BUSINESS MOVES WHEN THE EXTRA IS UNDER ITS SAVING AND STAYS WHEN IT IS OVER: twin cities built the same, the
 *      landlords' Houses on a long queue, differing in the exchange rate alone; the foreign crews would finish it sooner in
 *      both, and the extra net of the tax recovered is under the saving at broughtForward() in the one that hands the site
 *      to them and over it in the one that does not.
 *   3. A HALF-BUILT SITE MOVES ON ITS REMAINING WORK ALONE: with months of work on it, a business's site and the city's
 *      each hand over their payer's share of the points owed, not the points on site; the extra is struck on them; the
 *      stack's progress and its finished buildings are what they were, and the record owes those points. A site FC2 holds
 *      a claim on hands its share less that pooled claim (the terms alone: with his book busy the card is not open).
 *   4. ONE ORDER'S BUILDINGS LEAVE A SHARED STACK AS SETTLED: the city's contract on a stack the landlords share is the same
 *      at the handover, and then earned at the home part's fraction while the landlords' abroad one is earned at the abroad
 *      part's, the earned dues summing to the stack's revenue due.
 *   5. THE CITY'S SITE HANDED ABROAD FINISHES SOONER AT THE CARD'S PRICE: twins on a long queue, in one the card is open and
 *      the handover is made; the treasury pays the card's extra, the site's last building opens sooner than in the twin,
 *      the crews billed on it are its points at the card's price, and the card's months and interest saved are its getters'.
 *   6. THE MONEY IS BOOKED ONCE: the payer's payment is in the builders' order book (unearned revenue up by the gross
 *      extra) and is earned as the abroad part is built; the crews' bill is an import of the builders, the month after, in
 *      the audit's TRADE scope and the national and foreign accounts; every month of it balanced.
 *   7. THE SAVED CHOICE: a city with a record half done, saved and loaded, has the record and the abroad contracts it had
 *      and plays on month for month as its live twin.
 *   8. A WAIT AT HOME THAT NEVER ENDS CROSSES A SAVE (REV-FC2b-1): the terms of a site read at a wait output of nothing have an endless
 *      wait at home; the record made of them keeps it as the greatest finite wait and every figure it keeps finite, so that DataSave's Gson,
 *      which writes no infinity, saves the city; loaded, the record is the same field for field and plays on as its live twin. In the city's
 *      own read no wait is endless: the contractor's month keeps orderWaitOutput() over nothing when the repairs take every point.
 *   9. A PAVING HANDED ABROAD (ConstructionControl, F): the city's Paved Road site that paves its gravel roads is handed by its card; the
 *      record owes the paving's whole work, nothing of it stays at home, and the gravel roads retire as the paved roads open, all of them in
 *      fewer months than in the twin that keeps the paving at home.
 *  10. THE PAYMENT'S TWO REFUSALS, BOTH SIDES EACH: a business that must borrow its extra moves when the bank lends it (its debt up by the
 *      shortfall, its cash what the loan and the extra leave) and stays, nothing booked, when the bank will not (its borrowing barred); the
 *      city moves when its treasury holds the card's extra and is refused, nothing booked, when it holds less (it cannot borrow).
 *
 * Every fixture causes its condition: the orders placed, the standing roads, the exchange rate, the payer's cash, the paving ordered, the
 * borrowing ban on the credit record, the treasury's cash.
 */
public class ForeignChoiceCheck {

    /** The first share of the foreign crews' excess at the strong D$ of section 2's mover (BuildersShort.capShare() over this many): the stayer keeps the city's own rate, the share the cap. */
    static final double STRONG_SHARE_OF_CAP = 0.125;

    /** The months queued at home at which the search for the twins' queue starts, and the step it takes: a month at a time, so the crossing is not stepped over. */
    static final double SEARCH_FROM = 12, SEARCH_STEP = 1;

    /** The months queued at home past which the search gives up: a premium past the crews' excess makes every move free, and there is no pair of rates to tell apart. */
    static final double SEARCH_TO = 60;

    /** The landlords hold this many times the cost of their Houses on site in cash, so the extra is never borrowed: the saving is then judged at the loan rate of nothing borrowed. */
    static final double PAYER_CASH_TIMES = 100;

    /**
     * The crowd the town takes in before the landlords' Houses are ordered (RES3): this many Convenience Stores stand, their posts open, and this many people arrive. The landlords
     * count what one more House earns as the rent of the households left unplaced that can pay the ask, after every door standing and every House on site
     * (HousingMarket.candidateIncome()); the calm town's 238 Houses on site take every household that can pay, and the ones left cannot, so a House earned nothing, its months
     * saved were worth nothing and no queue told the strong rate's extra from the city's own. With the posts and the people, households that can pay wait for doors beyond them.
     */
    static final int CROWD_STORES = 200;
    static final double CROWD_PEOPLE = 2500;

    /** Section 3's months worked on a site before it is handed: some of its buildings finished, one part built. */
    static final int HALF_BUILT_MONTHS = 3;

    /** ...and the city's Middle Schools ordered past the threshold in its second case, so that FC2 keeps a claim on the site (REV-FC2). */
    static final long CLAIM_SCHOOLS = 6;

    /** Section 4's Houses the city orders on the landlords' site: a share of it. */
    static final long CITY_HOUSES = 60;

    /** Section 5's site: Middle Schools the city orders when the landlords' Houses are built, and the months queued at home it stands at: long, and under the point where the home premium passes the crews' excess. */
    static final String SCHOOL = "Middle School";
    static final long SCHOOLS = 2;
    static final double CITY_QUEUE_MONTHS = 25;

    /** The most months a site is played for to see its last building opened, and the months a record is followed. */
    static final int MOST_MONTHS = 60;

    /** The months played on before a save (the record half done), and after it, in both twins. */
    static final int BEFORE_THE_SAVE = 2, AFTER_THE_SAVE = 4;

    /** Section 9's gravel roads the city orders paved: a pair, whose paving at 25 months queued at home still leaves the crews' excess over the home premium (the extra is something). */
    static final int PAVINGS = 2;

    /** Section 10(a)'s landlords hold this share of the gross extra they would pay, so that they must borrow the rest. */
    static final double SHORT_CASH_SHARE = 0.5;

    /** Section 10(b)'s treasury holds this many times the card's extra when it can pay and this many when it cannot (the city never borrows: Game's government). */
    static final double TREASURY_OVER = 2, TREASURY_UNDER = 0.5;

    /** The slot the saved city is written to, in its own scratch folder. */
    static final int SCRATCH_SLOT = ConstructionTrapCheck.SCRATCH_SLOT;

    /** The assertions made here. */
    static int checked = 0;

    /** One assertion: the label reads as a sentence; a failing one prints FAIL, and `fails` (ConstructionTrapCheck's) is the exit code. */
    static void check(String label, boolean ok) {
        checked++;
        if (!ok) ConstructionTrapCheck.fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** ...with what was read, printed whether or not it held. */
    static void check(String label, boolean ok, String shown) {
        checked++;
        if (!ok) ConstructionTrapCheck.fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", shown);
    }

    /** Two figures within `tol` of each other, absolutely or of the expected, whichever is the larger. */
    static void near(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        check(label, ok, String.format("%,.9f against %,.9f", actual, expected));
    }

    /** Two figures the same to the bit. */
    static void exact(String label, double actual, double expected) {
        check(label, Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected), actual + " against " + expected);
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        ConstructionTrapCheck.out = System.out;
        ConstructionTrapCheck.quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        DeathCareFirstCheck.out = ConstructionTrapCheck.out;
        DeathCareFirstCheck.quiet = ConstructionTrapCheck.quiet;
        Path root = TempDirs.create("foreignchoicecheck");

        arithmetic();
        theTwins(root);
        theHalfBuilt(root);
        theSharedStack(root);
        theCitysSite(root);
        theMoney(root);
        theSave(root);
        theEndlessWait(root);
        thePaving(root);
        thePayment(root);
        out.println("\n--- the audit ---");
        audited("in every month here");

        out.printf("%n%d assertions made here%n", checked);
        out.println(ConstructionTrapCheck.fails == 0 ? "\nAll checks passed." : "\n" + ConstructionTrapCheck.fails + " FAILED");
        System.exit(ConstructionTrapCheck.fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE ARITHMETIC ============================ */

    static final double INF = Double.POSITIVE_INFINITY;

    static void arithmetic() {
        out.println("--- 1. the arithmetic: the pace, the months, the earnings brought forward, the interest saved ---");
        double w = 1000, t = 8, depot = 300, cap = 100;
        // sitePace
        near("the pace is the points over the threshold's months when nothing floors or caps it", BuildersShort.sitePace(w, t, 0, INF), w / t, 1e-12);
        near("...a depot under that changes nothing", BuildersShort.sitePace(w, t, w / t / 2, INF), w / t, 1e-12);
        near("...a depot over it floors the pace at the depot", BuildersShort.sitePace(w, t, depot, INF), depot, 1e-12);
        near("...never over the points, whatever the depot (done in one month)", BuildersShort.sitePace(w, t, 10 * w, INF), w, 1e-12);
        near("...nor a threshold of a tenth of a month", BuildersShort.sitePace(w, 0.1, 0, INF), w, 1e-12);
        near("...a threshold of nothing, under nothing or endless reads as one month: the whole site", BuildersShort.sitePace(w, 0, 0, INF), w, 1e-12);
        near("...under nothing", BuildersShort.sitePace(w, -3, 0, INF), w, 1e-12);
        near("...endless", BuildersShort.sitePace(w, INF, 0, INF), w, 1e-12);
        near("...a finite build-time cap under it caps the pace", BuildersShort.sitePace(w, t, 0, cap), cap, 1e-12);
        near("...a cap under the depot wins over the depot", BuildersShort.sitePace(w, t, depot, cap), cap, 1e-12);
        near("...a cap over the pace changes nothing", BuildersShort.sitePace(w, t, 0, 10 * w), w / t, 1e-12);
        near("...a cap that is endless caps nothing", BuildersShort.sitePace(w, t, 0, INF), w / t, 1e-12);
        near("...nor one of nothing, which is no cap", BuildersShort.sitePace(w, t, 0, 0), w / t, 1e-12);
        near("...nor a NaN depot", BuildersShort.sitePace(w, t, Double.NaN, INF), w / t, 1e-12);
        near("no points, no pace", BuildersShort.sitePace(0, t, depot, INF), 0, 0);
        near("...nor under nothing", BuildersShort.sitePace(-5, t, depot, INF), 0, 0);
        boolean bounded = true, floored = true, capped = true, plain = true;
        int cells = 0;
        for (double points : new double[] { 10, 1000, 5e5 })
            for (double thr : new double[] { 1, 9, 21 })
                for (double floor : new double[] { 0, 400, 1e6 })
                    for (double c : new double[] { INF, 50, 3000 }) {
                        double p = BuildersShort.sitePace(points, thr, floor, c);
                        cells++;
                        bounded &= p > 0 && p <= points;
                        if (c == INF) floored &= p >= Math.min(points, floor) - 1e-9 && p >= Math.min(points, points / thr) - 1e-9;
                        else capped &= p <= c + 1e-9;
                        if (c == INF && floor == 0) plain &= Math.abs(p - Math.min(points, points / thr)) <= 1e-9 * points;
                    }
        check("over " + cells + " points, thresholds, depots and caps the pace is never over the points and never nothing", bounded);
        check("...with no cap never under the depot or the threshold's month (under the points)", floored);
        check("...with a finite cap never over it", capped);
        check("...with no depot and no cap the points over the threshold's months", plain);

        // monthsAbroad
        near("a part of 1,000 points at 300 a month takes 4 months, rounded up", BuildersShort.monthsAbroad(1000, 300), 4, 0);
        near("...900 at 300 takes 3, a whole number of months is not one more", BuildersShort.monthsAbroad(900, 300), 3, 0);
        near("...a ten-billionth over is not another month", BuildersShort.monthsAbroad(900 * (1 + 1e-11), 300), 3, 0);
        near("...a point over is", BuildersShort.monthsAbroad(901, 300), 4, 0);
        near("...a few points are a month", BuildersShort.monthsAbroad(10, 300), 1, 0);
        near("...no points, no months", BuildersShort.monthsAbroad(0, 300), 0, 0);
        check("...no pace, endless", BuildersShort.monthsAbroad(1000, 0) == INF);
        boolean whole = true, rounded = true;
        for (double points : new double[] { 1, 299, 300, 301, 12345.6, 5e5 })
            for (double pace : new double[] { 1, 7, 300, 400, 99999 }) {
                double m = BuildersShort.monthsAbroad(points, pace);
                whole &= m == Math.rint(m) && m >= 1;
                rounded &= m * pace >= points * (1 - 1e-9) && (m - 1) * pace < points;
            }
        check("over a grid the months are whole and at least one", whole);
        check("...enough months to build it and not one more than it needs", rounded);

        // broughtForward
        double perMonth = 12.5, i = 0.004, wa = 7, wh = 31;
        double bf = BuildersShort.broughtForward(perMonth, i, wa, wh);
        double explicit = 0;
        for (int m = (int) wa + 1; m <= (int) wh; m++) explicit += perMonth / Math.pow(1 + i, m);
        near("the earnings brought forward are the months between, discounted one by one (summed here)", bf, explicit, 1e-12);
        check("...less than the months themselves and over nothing", bf > 0 && bf < perMonth * (wh - wa));
        near("...linear in its earnings: twice the earnings, twice the figure", BuildersShort.broughtForward(2 * perMonth, i, wa, wh), 2 * bf, 1e-12);
        near("...and additive in them", BuildersShort.broughtForward(perMonth + 3.25, i, wa, wh), bf + BuildersShort.broughtForward(3.25, i, wa, wh), 1e-12);
        near("...a negative earning gives the negative", BuildersShort.broughtForward(-perMonth, i, wa, wh), -bf, 1e-12);
        near("...as the rate goes to nothing it tends to the earnings x the months between", BuildersShort.broughtForward(perMonth, 1e-10, wa, wh), perMonth * (wh - wa), 1e-6);
        near("...at no rate it is exactly that", BuildersShort.broughtForward(perMonth, 0, wa, wh), perMonth * (wh - wa), 1e-12);
        near("...under no rate the same", BuildersShort.broughtForward(perMonth, -0.01, wa, wh), perMonth * (wh - wa), 1e-12);
        near("...a home start endless: P / i x (1 + i)^-Wa", BuildersShort.broughtForward(perMonth, i, wa, INF), perMonth / i * Math.pow(1 + i, -wa), 1e-12);
        near("...the more months saved the more it is worth", BuildersShort.broughtForward(perMonth, i, wa, wh + 10) - bf,
                BuildersShort.broughtForward(perMonth, i, wh, wh + 10), 1e-12);
        near("...no start sooner, nothing (the same month)", BuildersShort.broughtForward(perMonth, i, wa, wa), 0, 0);
        near("...a start later abroad than at home, nothing", BuildersShort.broughtForward(perMonth, i, wh, wa), 0, 0);
        near("...a NaN month, nothing", BuildersShort.broughtForward(perMonth, i, Double.NaN, wh), 0, 0);
        check("...endless at no rate has the earnings' sign", BuildersShort.broughtForward(perMonth, 0, wa, INF) == INF
                && BuildersShort.broughtForward(-perMonth, 0, wa, INF) == -INF);

        // interestSaved: the two parts, no double count
        boolean parts = true, interestOnly = true;
        double worst = 0;
        int rows = 0;
        for (double c : new double[] { 0, 3.5, 4000, 1e6 })
            for (double p : new double[] { 0.5, 12.5, 300, 5e4 })
                for (double rate : new double[] { 0.0005, 0.004, 0.03 })
                    for (double[] months : new double[][] { { 1, 9 }, { 7, 31 }, { 13, 130 }, { 4, INF } }) {
                        double total = BuildersShort.broughtForward(p, rate, months[0], months[1]);
                        double interest = BuildersShort.interestSaved(c, rate, months[0], months[1]);
                        double over = BuildersShort.broughtForward(p - c * rate, rate, months[0], months[1]);
                        worst = Math.max(worst, Math.abs(interest + over - total) / Math.max(1, Math.abs(total)));
                        parts &= Math.abs(interest + over - total) <= 1e-9 * Math.max(1, Math.abs(total));
                        interestOnly &= Math.abs(interest - BuildersShort.broughtForward(c * rate, rate, months[0], months[1])) <= 1e-12 * Math.max(1, Math.abs(interest));
                        rows++;
                    }
        check("interestSaved(C) + broughtForward(P - C x i) is broughtForward(P) over " + rows + " costs, earnings, rates and months, an endless home start among them",
                parts, String.format("worst relative gap %.3g", worst));
        check("...and the interest alone is the interest on the cost, the months saved discounted", interestOnly);
        near("...with no rate it saves no interest", BuildersShort.interestSaved(4000, 0, wa, wh), 0, 0);
        near("...earnings that only pay the interest bring nothing over it", BuildersShort.broughtForward(4000 * i - 4000 * i, i, wa, wh), 0, 0);
    }

    /* ============================ THE DECIDING QUANTITIES ============================ */

    /**
     * What a business's test reads for its site (Game.investorsHandAbroad()), read the way it reads them and from the model's own getters: the terms
     * a handover would strike, the extra grossed up for the builders' tax and net of the share of it the payer gets back (cost), the payer's monthly
     * earnings on the buildings on site and the saving they bring forward at the real rate of a loan of nothing (the payer holds the cash), with the
     * interest saved among it.
     */
    record Verdict(BuildingManager.AbroadTerms terms, double gross, double cost, double perMonth, double rate, double saving, double interest,
                   double rB, double recovered, double buildingsCost) { }

    static BuildingsStacks.Contract contractOf(BuildingsStacks s, String payer) {
        for (BuildingsStacks.Contract c : s.getContracts()) if (c.payer.equals(payer)) return c;
        return null;
    }

    static Verdict verdict(Game g, BuildingsTemplate t, String payer) {
        BuildingManager bm = g.getBuildingManager();
        BuildingManager.AbroadTerms terms = bm.abroadTermsOf(t, payer, g.orderWaitOutput());
        BuildingsStacks.Contract c = contractOf(bm.getStack(t), payer);
        if (terms == null || c == null) return null;
        double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, g.getEconomyManager().getTaxPolicy().effectiveSalesRate(g.getSectors().construction())));
        double gross = terms.extra() / (1 - rB);
        double cost = gross * (1 - c.getRecovered() * rB);
        double annual = g.getEconomyManager().getBusinessDebtManager().projectRate(payer, 0);
        double rate = g.getBusinessInvestment().realTestRate(annual) / 12;
        double perMonth = g.getBusinessInvestment().estimatedMonthlyProfit(payer, t) * terms.buildings();
        double saving = BuildersShort.broughtForward(perMonth, rate, terms.monthsAbroad(), terms.monthsHome());
        double buildingsCost = g.getBusinessInvestment().getCostOf(t, 1) * terms.buildings();
        double interest = BuildersShort.interestSaved(buildingsCost, rate,
                terms.monthsAbroad(), terms.monthsHome());
        return new Verdict(terms, gross, cost, perMonth, rate, saving, interest, rB, c.getRecovered(), buildingsCost);
    }

    static boolean soonerAbroad(Verdict v) { return v != null && v.terms().monthsAbroad() < v.terms().monthsHome(); }

    /**
     * A calm city with the landlords' Houses on site, rich in cash, the queue at home made long by standing roads until the landlords' sum can go
     * either way on the exchange rate alone: at the strong D$ the extra they would pay is something and under what the months saved are worth them
     * (a mover), and at the city's own rate it is over it (a stayer). {@code cityHouses} of the city's own are ordered on their site first (a shared
     * stack) and {@code played} months are worked before the roads. The rate is left at the city's own.
     */
    static Game choiceCity(Path root, String name, int played, long cityHouses) {
        Game g = calm(root, name);
        BuildingManager bm = g.getBuildingManager();
        EconomyManager em = g.getEconomyManager();
        BuildingsTemplate house = template(g, HOUSE);
        double own = bm.getExchangeRate();
        double strong = ForeignCrewsCheck.rateFor(bm, STRONG_SHARE_OF_CAP * BuildersShort.capShare());
        double cash = PAYER_CASH_TIMES * g.getBusinessInvestment().getCostOf(house, Math.max(1, bm.getStack(house).getUnderConstruction()));
        em.setSectorCash(RE, em.getSectorCash(RE) + cash);
        // ...the crowd first, and a month for the clearing to see it (the households that wait for doors are the last clearing's).
        quietly(() -> {
            g.buildStack(template(g, "Convenience Store"), CROWD_STORES, true);
            g.getCohorts().migrate(CROWD_PEOPLE);
            g.getPopulationManager().setPopulation(Math.round(g.getCohorts().total()));
        });
        month(g);
        if (cityHouses > 0 && !ForeignCrewsCheck.cityOrders(g, house, cityHouses)) throw new IllegalStateException("the city's Houses could not be ordered");
        for (int k = 0; k < played; k++) month(g);
        double months = Math.max(SEARCH_FROM, Math.ceil(bm.monthsQueued()));
        while (true) {
            roads(g, roadsFor(g, months));
            em.setExchangeRate(strong);
            Verdict lo = verdict(g, house, RE);
            em.setExchangeRate(own);
            Verdict hi = verdict(g, house, RE);
            if (soonerAbroad(lo) && soonerAbroad(hi) && lo.cost() > 0 && lo.cost() < lo.saving() && hi.cost() > hi.saving()) break;
            months += SEARCH_STEP;
            if (months > SEARCH_TO) throw new IllegalStateException("no queue to " + SEARCH_TO + " months tells the strong rate's extra from the city's own: "
                    + "strong " + lo + " own " + hi);
        }
        em.setExchangeRate(own);
        return g;
    }

    /** The rate a mover is struck at: the first share of the crews' excess the fixture's strong D$ gives. */
    static double strongRate(Game g) { return ForeignCrewsCheck.rateFor(g.getBuildingManager(), STRONG_SHARE_OF_CAP * BuildersShort.capShare()); }

    static String key(Game g, String building) { return ConstructionControl.keyOf(template(g, building)); }

    /* ============================ 2. MOVE OR STAY ============================ */

    static void theTwins(Path root) {
        out.println("\n--- 2. a business moves when its extra is under its saving and stays when it is over: twins that differ in the exchange rate alone ---");
        Game mover = choiceCity(root, "mover", 0, 0), stayer = choiceCity(root, "stayer", 0, 0);
        BuildingsTemplate house = template(mover, HOUSE);
        String key = ConstructionControl.keyOf(house);
        BuildingManager bmM = mover.getBuildingManager(), bmS = stayer.getBuildingManager();
        BuildingsTemplate houseS = template(stayer, HOUSE);
        BuildingsStacks sm = bmM.getStack(house), ss = bmS.getStack(houseS);
        check("fixture: the twins are the same city before the rate is set: the queue, the sites' month, the points owed and the landlords' cash",
                bmM.queueAtHome() == bmS.queueAtHome() && mover.getBuildingOutputAtEveryPost() == stayer.getBuildingOutputAtEveryPost()
                        && sm.owedPoints() == ss.owedPoints() && mover.getEconomyManager().getSectorCash(RE) == stayer.getEconomyManager().getSectorCash(RE));
        mover.getEconomyManager().setExchangeRate(strongRate(mover));
        check("the one input that differs: the mover's D$ is stronger than the stayer's", bmM.getExchangeRate() < bmS.getExchangeRate(),
                bmM.getExchangeRate() + " against " + bmS.getExchangeRate());
        Verdict vm = verdict(mover, house, RE), vs = verdict(stayer, houseS, RE);
        out.printf("  the queue at home %.2f months; the home premium %.4f; the Houses on site %d, %,.0f points owed%n",
                bmM.monthsQueued(), bmM.scarcityPremium(), sm.getUnderConstruction(), sm.owedPoints());
        printVerdict("mover", vm);
        printVerdict("stayer", vs);
        check("fixture: a new House earns in both towns (the households that can pay the ask wait for doors past those standing and on site), so the months saved are worth something",
                vm.perMonth() > 0 && vs.perMonth() > 0, String.format("D$%,.4fk a month on the %,.0f Houses on site", vm.perMonth(), vm.terms().buildings()));
        check("fixture: the landlords hold the cash for the extra in both, so neither borrows",
                mover.getSectorInvestor(RE).getCash() > vm.gross() && stayer.getSectorInvestor(RE).getCash() > vs.gross());
        check("in both the foreign crews would finish it sooner than the home crews (monthsAbroad under monthsHome)", soonerAbroad(vm) && soonerAbroad(vs),
                vm.terms().monthsAbroad() + " < " + vm.terms().monthsHome() + "; " + vs.terms().monthsAbroad() + " < " + vs.terms().monthsHome());
        check("the mover's extra, net of the tax it gets back, is something and under what the months saved are worth it (broughtForward of its earnings)",
                vm.cost() > 0 && vm.cost() < vm.saving(), String.format("%,.4f under %,.4f", vm.cost(), vm.saving()));
        check("...the stayer's is over it", vs.cost() > vs.saving(), String.format("%,.4f over %,.4f", vs.cost(), vs.saving()));
        check("...and over the earnings' months undiscounted too, the most the saving could be at any rate", vs.cost() > vs.perMonth() * (vs.terms().monthsHome() - vs.terms().monthsAbroad()));
        check("the crews' extra is the one thing that differs: the stayer's is the more", vs.terms().extra() > vm.terms().extra());

        double cashM = mover.getEconomyManager().getSectorCash(RE), cashS = stayer.getEconomyManager().getSectorCash(RE);
        quietly(mover::investorsHandAbroad);
        quietly(stayer::investorsHandAbroad);
        ConstructionControl.Abroad rec = bmM.getControl().abroadOf(key);
        check("the mover has a record of work abroad on the site and the stack has work abroad", rec != null && sm.hasAbroad());
        check("...the stayer has neither", bmS.getControl().abroadOf(key) == null && !ss.hasAbroad() && ss.getAbroadContracts().isEmpty());
        check("...its home contract still the landlords'", contractOf(ss, RE) != null && contractOf(ss, RE).getPoints() > 0);
        exact("...and its cash untouched", stayer.getEconomyManager().getSectorCash(RE), cashS);
        if (rec == null) return;
        check("the record is the landlords'", RE.equals(rec.payer) && rec.key.equals(key) && rec.templateId == house.getId());
        near("...it owes the points the terms struck", rec.owed, vm.terms().points(), 1e-9);
        near("...at the pace and the price the terms struck", rec.pace, vm.terms().pace(), 1e-12);
        near("...the price a point", rec.pricePerPoint, vm.terms().pricePerPoint(), 1e-12);
        near("...it paid the gross extra: the terms' extra grossed up for the builders' tax", rec.paid, vm.gross(), 1e-9);
        check("...which is something", rec.paid > 0);
        near("...the landlords' cash fell by it", cashM - mover.getEconomyManager().getSectorCash(RE), rec.paid, 1e-9);
        exact("...the months at home are the terms'", rec.monthsHome, vm.terms().monthsHome());
        exact("...and the months abroad", rec.monthsAbroad, vm.terms().monthsAbroad());
        near("...the interest saved is the interest on the cost for the months saved (BuildersShort.interestSaved)", rec.interestSaved, vm.interest(), 1e-9);
        near("...and the earnings over that interest brought forward are broughtForward(earnings - cost x i)", rec.earningsForward,
                BuildersShort.broughtForward(vm.perMonth() - vm.buildingsCost() * vm.rate(), vm.rate(), vm.terms().monthsAbroad(), vm.terms().monthsHome()), 1e-9);
        near("...the two together are the saving the extra was weighed against", rec.interestSaved + rec.earningsForward, vm.saving(), 1e-9);
        check("...which the net cost it paid was under", rec.paid * (1 - vm.recovered() * vm.rB()) < rec.interestSaved + rec.earningsForward);
        check("the landlords' contract moved to the foreign crews' part: no home contract of theirs, an abroad one with the points",
                contractOf(sm, RE) == null && sm.getAbroadContracts().size() == 1 && sm.getAbroadContracts().iterator().next().getPoints() > 0);
        check("a site handed abroad is not the city's own: no reorder, rush, stop or cancel of it", !sm.isCitysOwn() && !bmM.isCitySite(key));
        check("...and no second record on it while this one stands", bmM.abroadTermsOf(house, RE, mover.orderWaitOutput()) == null);
    }

    /** The cost of the buildings a verdict's terms stand for, as the landlords' sum read it. */
    static double costOnSite(Game g, BuildingsTemplate t, Verdict v) { return g.getBusinessInvestment().getCostOf(t, 1) * v.terms().buildings(); }

    static void printVerdict(String who, Verdict v) {
        BuildingManager.AbroadTerms t = v.terms();
        out.printf("  %s: %,.0f points at %,.1f a month, %,.0f months abroad against %,.2f at home; excess %.4f over the home premium %.4f; extra %,.4f, gross %,.4f,"
                        + " net %,.4f; earns %,.4f a month; at %.5f a month the months are worth %,.4f (interest %,.4f)%n",
                who, t.points(), t.pace(), t.monthsAbroad(), t.monthsHome(), t.excess(), t.premiumHome(), t.extra(), v.gross(), v.cost(), v.perMonth(), v.rate(), v.saving(), v.interest());
    }

    /* ============================ 3. A HALF-BUILT SITE ============================ */

    /**
     * One half-built site handed over: the terms read before it (its payer's share of the points owed less FC2's pooled claim, not its points on
     * site; the extra struck on them), the handover made by {@code hand}, and the stack and the record after it. {@code claimed}: FC2 holds a claim on it.
     */
    static void halfBuilt(String who, Game g, BuildingsTemplate t, String payer, boolean claimed, Runnable hand) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsStacks s = bm.getStack(t);
        String key = ConstructionControl.keyOf(t);
        BuildingManager.AbroadTerms terms = bm.abroadTermsOf(t, payer, g.orderWaitOutput());
        check("fixture: " + who + "'s site has had work done on it: buildings finished or progress made, the points owed under the points on site",
                (s.getQuantity() > 0 || s.getConstructionProgress() > 0) && s.owedPoints() < s.pointsOnSite(),
                String.format("%,.1f owed of %,.1f on site, %d standing, progress %,.1f", s.owedPoints(), s.pointsOnSite(), s.getQuantity(), s.getConstructionProgress()));
        double claim = bm.abroadOwedOf(t);
        check("fixture: " + (claimed ? "FC2 holds a claim on the site, work ordered past the threshold" : "FC2 holds no claim on the site"),
                claimed ? claim > 0 && claim < s.owedPoints() : claim == 0, String.format("%,.1f of %,.1f owed", claim, s.owedPoints()));
        check("fixture: the terms are read", terms != null);
        if (terms != null) out.printf("  %s: %,.0f points at %,.1f a month, %,.0f months abroad against %,.2f at home; the queue at home %.2f months, the home premium %.4f, the crews' excess %.4f%n", who, terms.points(), terms.pace(), terms.monthsAbroad(), terms.monthsHome(), bm.monthsQueued(), terms.premiumHome(), terms.excess());
        if (terms == null) return;
        double share = s.homePointsShare(payer);
        near("the points handed are the payer's share of the points owed at home: what is owed less FC2's claim on his book", terms.points(), share * (s.owedPoints() - claim), 1e-12);
        check("...not its points on site", Math.abs(terms.points() - s.pointsOnSite()) > 1, String.format("%,.1f against %,.1f on site", terms.points(), s.pointsOnSite()));
        if (claimed) check("...nor the points owed with the claim in them", Math.abs(terms.points() - s.owedPoints()) > 1, String.format("%,.1f against %,.1f owed", terms.points(), s.owedPoints()));
        BuildingManager.ForeignTerms ft = bm.foreignTerms();
        double excess = BuildersShort.excessAt(ft.firstShare(), ft.month() + bm.getControl().abroadMonth() + terms.pace(), ft.output());
        near("the crews' excess is struck at their month with this work in it", terms.excess(), excess, 1e-12);
        near("the extra is struck on those points: points x base x (the crews' excess less the home premium, never under nothing)", terms.extra(),
                terms.points() * bm.basePricePerPoint() * Math.max(0, excess - bm.scarcityPremium()), 1e-12);
        near("the pace is those points over the threshold's months, the depot and the build-time cap (BuildersShort.sitePace)", terms.pace(),
                BuildersShort.sitePace(terms.points(), bm.thresholdMonths(), bm.largestDepotPoints(), BuildingManager.monthCap(t, s.pointsOnSite())), 1e-12);
        near("the units of material are the part's share of those still owed", terms.materials(), s.getMaterialsOwed() * terms.points() / s.owedPoints(), 1e-12);
        double under = s.getUnderConstruction(), progress = s.getConstructionProgress(), standing = s.getQuantity();
        if (hand == null) return;   // the terms alone: with FC2's book busy the home wait counts his crews, and the card is not open
        hand.run();
        ConstructionControl.Abroad rec = bm.getControl().abroadOf(key);
        check("handed over: the record stands and the stack has work abroad", rec != null && s.hasAbroad());
        if (rec == null) return;
        exact("the stack's progress is what it was", s.getConstructionProgress(), progress);
        exact("...its buildings under construction", s.getUnderConstruction(), under);
        exact("...and its buildings finished", s.getQuantity(), standing);
        near("the record owes those points", rec.owed, terms.points(), 1e-12);
        near("...and handed those", rec.points, terms.points(), 1e-12);
        near("...FC2's claim stays on his book, as it was", bm.abroadOwedOf(t), claim, 1e-12);
        check("...the part abroad is under the points the site owes, the home share and the claim left out of it", rec.owed < s.owedPoints() || (!claimed && share == 1 && rec.owed <= s.owedPoints()));
    }

    static void theHalfBuilt(Path root) {
        out.println("\n--- 3. a half-built site moves on its remaining work alone: its payer's share of the points owed less FC2's claim, not its points on site ---");
        Game g = choiceCity(root, "half", HALF_BUILT_MONTHS, 0);
        g.getEconomyManager().setExchangeRate(strongRate(g));
        BuildingsTemplate house = template(g, HOUSE);
        Verdict v = verdict(g, house, RE);
        check("fixture: the landlords' sum is for moving", v != null && v.cost() < v.saving());
        halfBuilt("the landlords", g, house, RE, false, () -> quietly(g::investorsHandAbroad));
        // The city's, by its card: two schools ordered with the queue under the threshold, some of their work done.
        Game c = citySite(root, "half-city", SCHOOLS, HALF_BUILT_MONTHS, CITY_QUEUE_MONTHS);
        BuildingsTemplate school = template(c, SCHOOL);
        halfBuilt("the city", c, school, "City", false, () -> {
            String refused = c.handSiteAbroad(ConstructionControl.keyOf(school));
            check("the city's card is open and it hands the site", refused == null, String.valueOf(refused));
        });
        // ...and with FC2's claim on the site (a site ordered past the threshold in part, and some of it built): the terms alone, the claim off the points.
        Game k = citySite(root, "claim-city", CLAIM_SCHOOLS, HALF_BUILT_MONTHS, CITY_QUEUE_MONTHS);
        halfBuilt("the city, a site FC2 holds a claim on", k, template(k, SCHOOL), "City", true, null);
    }

    /* ============================ 4. A SHARED STACK ============================ */

    /** One contract's figures. */
    record Snap(double value, double units, double allowance, double points) {
        static Snap of(BuildingsStacks.Contract c) { return c == null ? null : new Snap(c.getValue(), c.getUnits(), c.getAllowance(), c.getPoints()); }
        boolean same(Snap o) {
            return o != null && Double.doubleToLongBits(value) == Double.doubleToLongBits(o.value) && Double.doubleToLongBits(units) == Double.doubleToLongBits(o.units)
                    && Double.doubleToLongBits(allowance) == Double.doubleToLongBits(o.allowance) && Double.doubleToLongBits(points) == Double.doubleToLongBits(o.points);
        }
    }

    static BuildingsStacks.Contract abroadContractOf(BuildingsStacks s, String payer) {
        for (BuildingsStacks.Contract c : s.getAbroadContracts()) if (c.payer.equals(payer)) return c;
        return null;
    }

    static void theSharedStack(Path root) {
        out.println("\n--- 4. one order's buildings leave a shared stack as settled: the city's contract untouched, each earned at its own part's fraction ---");
        Game g = choiceCity(root, "shared", 0, CITY_HOUSES);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate house = template(g, HOUSE);
        String key = ConstructionControl.keyOf(house);
        BuildingsStacks s = bm.getStack(house);
        check("fixture: the stack is shared: the landlords' contract and the city's on it, the city's the later", contractOf(s, RE) != null && contractOf(s, "City") != null
                && contractOf(s, RE).getPoints() > 0 && contractOf(s, "City").getPoints() > 0 && s.getContracts().size() == 2);
        Snap cityBefore = Snap.of(contractOf(s, "City")), reBefore = Snap.of(contractOf(s, RE));
        double rePointsShare = reBefore.points() / (reBefore.points() + cityBefore.points());
        g.getEconomyManager().setExchangeRate(strongRate(g));
        BuildingManager.AbroadTerms terms = bm.abroadTermsOf(house, RE, g.orderWaitOutput());
        check("fixture: the landlords' share of the site by points is a part of it, under one", terms != null && terms.share() > 0 && terms.share() < 1,
                terms == null ? "no terms" : String.valueOf(terms.share()));
        if (terms == null) return;
        near("...the terms read it: the contract's points over the home contracts'", terms.share(), rePointsShare, 1e-12);
        near("...and the points moved are that share of the points owed", terms.points(), rePointsShare * s.owedPoints(), 1e-12);
        double progress = s.getConstructionProgress(), standing = s.getQuantity(), under = s.getUnderConstruction(), owed = s.owedPoints();
        Verdict v = verdict(g, house, RE);
        check("fixture: the landlords' sum is for moving", v.cost() < v.saving(), String.format("%,.4f under %,.4f", v.cost(), v.saving()));
        quietly(g::investorsHandAbroad);
        ConstructionControl.Abroad rec = bm.getControl().abroadOf(key);
        check("the landlords handed their share", rec != null && s.hasAbroad() && abroadContractOf(s, RE) != null && contractOf(s, RE) == null);
        if (rec == null) return;
        check("the city's home contract's value, units, allowance and points are unchanged at the handover (to the bit)", cityBefore.same(Snap.of(contractOf(s, "City"))));
        exact("...the stack's progress", s.getConstructionProgress(), progress);
        exact("...its buildings finished", s.getQuantity(), standing);
        exact("...and under construction", s.getUnderConstruction(), under);
        exact("...and the points it owes", s.owedPoints(), owed);
        BuildingsStacks.Contract moved = abroadContractOf(s, RE);
        near("the landlords' abroad contract is theirs whole, with what they paid over added to its value", moved.getValue(), reBefore.value() + rec.paid, 1e-9);
        exact("...its units", moved.getUnits(), reBefore.units());
        exact("...and its allowance", moved.getAllowance(), reBefore.allowance());
        exact("...its points", moved.getPoints(), reBefore.points());
        near("...which are the record's owed (the work the part still has)", moved.getPoints(), rec.owed, 1e-9);
        check("the stack is not the city's own and its site is no longer the city's to reorder", !s.isCitysOwn() && !bm.isCitySite(key));

        // The months after: each contract earned by its own part's fraction.
        boolean cityFraction = true, abroadFraction = true, duesSum = true, byPart = true, cityPoints = true;
        double worstCity = 0, worstAbroad = 0, worstSum = 0;
        int months = 0, bothMonths = 0;
        double earnedCity = 0, earnedAbroad = 0;
        for (int m = 0; m < MOST_MONTHS && s.getUnderConstruction() > 0 && bm.getControl().abroadOf(key) != null; m++) {
            Snap c0 = Snap.of(contractOf(s, "City")), a0 = Snap.of(abroadContractOf(s, RE));
            double owed0 = s.owedPoints(), abroadOwed0 = bm.getControl().abroadOf(key).owed;
            double abroadPart = Math.min(abroadOwed0, owed0), homePart = owed0 - abroadPart;
            month(g);
            months++;
            Snap c1 = Snap.of(contractOf(s, "City")), a1 = Snap.of(abroadContractOf(s, RE));
            if (s.getUnderConstruction() == 0) break;   // the month the site empties earns what is left of both whole
            double abroadApplied = s.getLastAbroadApplied(), homeApplied = s.getLastApplied() - abroadApplied;
            double fh = homePart > 0 ? homeApplied / homePart : 0, fa = abroadPart > 0 ? abroadApplied / abroadPart : 0;
            double dueCity = 0, dueAbroad = 0, dueSum = 0;
            for (BuildingsStacks.Due d : s.getDues()) { dueSum += d.revenue; if (d.abroad) dueAbroad += d.revenue; else dueCity += d.revenue; }
            if (c1 != null) {
                worstCity = Math.max(worstCity, Math.abs(c1.value() - c0.value() * (1 - fh)) / Math.max(1, c0.value()));
                cityFraction &= Math.abs(c1.value() - c0.value() * (1 - fh)) <= 1e-9 * Math.max(1, c0.value());
                cityPoints &= Math.abs(c1.points() - c0.points() * (1 - fh)) <= 1e-9 * Math.max(1, c0.points());
                byPart &= Math.abs(dueCity - (c0.value() - c1.value())) <= 1e-9 * Math.max(1, c0.value());
            }
            if (a1 != null) {
                worstAbroad = Math.max(worstAbroad, Math.abs(a1.value() - a0.value() * (1 - fa)) / Math.max(1, a0.value()));
                abroadFraction &= Math.abs(a1.value() - a0.value() * (1 - fa)) <= 1e-9 * Math.max(1, a0.value());
                byPart &= Math.abs(dueAbroad - (a0.value() - a1.value())) <= 1e-9 * Math.max(1, a0.value());
                bothMonths++;
            }
            worstSum = Math.max(worstSum, Math.abs(dueSum - s.getRevenueDue()));
            duesSum &= Math.abs(dueSum - s.getRevenueDue()) <= 1e-9 * Math.max(1, s.getRevenueDue());
            earnedCity += dueCity;
            earnedAbroad += dueAbroad;
        }
        out.printf("  %d months followed (%d with both parts building): the city's contract off its fraction by %.2g, the landlords' abroad one by %.2g; earned %,.4f and %,.4f; the dues off the revenue due by %.2g%n",
                months, bothMonths, worstCity, worstAbroad, earnedCity, earnedAbroad, worstSum);
        check("fixture: months were played with both parts building", bothMonths >= 2);
        check("each month the city's contract's value fell by the home part's fraction done", cityFraction);
        check("...and its points by the same", cityPoints);
        check("...the landlords' abroad contract's by the abroad part's fraction done", abroadFraction);
        check("...each contract's dues are what its value fell by, the two never mixed", byPart);
        check("...and the earned dues of both sum to the stack's revenue due", duesSum);
    }

    /* ============================ 5. THE CITY'S SITE ============================ */

    /**
     * A calm city whose landlords' Houses are built (nothing else on site), the city's Middle Schools ordered, and the queue at home made long by
     * standing roads to CITY_QUEUE_MONTHS; the city's cash given for the extra.
     */
    static Game citySite(Path root, String name, long schools, int worked, double queueMonths) {
        Game g = calm(root, name);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate house = template(g, HOUSE), school = template(g, SCHOOL);
        for (int k = 0; k < MOST_MONTHS && bm.getStack(house).getUnderConstruction() > 0; k++) month(g);
        if (bm.getStack(house).getUnderConstruction() > 0) throw new IllegalStateException("the landlords' Houses did not finish in " + MOST_MONTHS + " months");
        if (!ForeignCrewsCheck.cityOrders(g, school, schools)) throw new IllegalStateException("the city's schools could not be ordered");
        for (int k = 0; k < worked; k++) month(g);
        roads(g, roadsFor(g, queueMonths));
        g.setCashForTest(g.getCash() + 1e9);
        return g;
    }

    static void theCitysSite(Path root) {
        out.println("\n--- 5. the city's site handed abroad finishes sooner, at the card's price: twins on a long queue, in one the card taken ---");
        Game a = citySite(root, "city-a", SCHOOLS, 0, CITY_QUEUE_MONTHS), b = citySite(root, "city-b", SCHOOLS, 0, CITY_QUEUE_MONTHS);
        String key = key(a, SCHOOL);
        BuildingManager bmA = a.getBuildingManager(), bmB = b.getBuildingManager();
        BuildingsTemplate school = template(a, SCHOOL);
        BuildingsStacks sa = bmA.getStack(school), sb = bmB.getStack(template(b, SCHOOL));
        check("fixture: the twins are the same city: the queue at home, the sites' month, the points owed and the treasury",
                bmA.queueAtHome() == bmB.queueAtHome() && a.getBuildingOutputAtEveryPost() == b.getBuildingOutputAtEveryPost()
                        && sa.owedPoints() == sb.owedPoints() && a.getCash() == b.getCash());
        check("fixture: the queue at home is long, past the knee and the threshold", bmA.monthsQueued() > bmA.thresholdMonths() && bmA.thresholdMonths() >= BuildersShort.KNEE_MONTHS,
                String.format("%.2f months queued, the threshold %.2f", bmA.monthsQueued(), bmA.thresholdMonths()));
        Game.AbroadCard card = a.abroadPreview(key), cardB = b.abroadPreview(key);
        out.printf("  the card: %,.0f points at %,.1f a month, %,.4f a point; extra %,.4f; %,.0f months abroad against %,.2f at home, %,.2f saved; interest saved %,.4f%n",
                card.points(), card.pace(), card.pricePerPoint(), card.extra(), card.monthsAbroad(), card.monthsHome(), card.monthsSaved(), card.interestSaved());
        check("the card is open on the city's own site", card.open() && cardB.open() && card.why().isEmpty(), card.why());
        check("...with something to pay and something saved", card.extra() > 0 && card.monthsSaved() > 0 && card.interestSaved() > 0);
        check("...the card's twin is the same card", card.extra() == cardB.extra() && card.monthsAbroad() == cardB.monthsAbroad() && card.monthsHome() == cardB.monthsHome());
        BuildingManager.AbroadTerms terms = bmA.abroadTermsOf(school, "City", a.orderWaitOutput());
        near("the card's months saved are the months at home less the months abroad", card.monthsSaved(), card.monthsHome() - card.monthsAbroad(), 1e-12);
        near("...its interest saved is BuildersShort.interestSaved on the buildings' cost at the city's long real rate a month (BuildAdvice.lifeRate)", card.interestSaved(),
                BuildersShort.interestSaved(a.getBusinessInvestment().getCostOf(school, 1) * terms.buildings(), BuildAdvice.lifeRate(a) / 12, terms.monthsAbroad(), terms.monthsHome()), 1e-12);
        near("...its extra is the terms' grossed up for the builders' tax", card.extra(),
                terms.extra() / (1 - Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, a.getEconomyManager().getTaxPolicy().effectiveSalesRate(a.getSectors().construction())))), 1e-12);
        near("...its price a point is the terms'", card.pricePerPoint(), terms.pricePerPoint(), 1e-12);

        double cash0 = a.getCash(), unearned0 = a.getSectors().construction().getUnearnedRevenue();
        String refused = a.handSiteAbroad(key);
        check("the city hands the site over: handSiteAbroad returns null", refused == null, String.valueOf(refused));
        ConstructionControl.Abroad rec = bmA.getControl().abroadOf(key);
        check("...the twin's site is its own still: no record", bmB.getControl().abroadOf(key) == null && b.abroadPreview(key).open());
        if (rec == null) return;
        near("the treasury paid exactly the card's extra", cash0 - a.getCash(), card.extra(), 1e-9);
        near("...which is in the builders' order book: unearned revenue up by it", a.getSectors().construction().getUnearnedRevenue() - unearned0, card.extra(), 1e-9);
        near("the record paid is the card's extra", rec.paid, card.extra(), 1e-9);
        near("...at the card's pace and price a point", rec.pace * rec.pricePerPoint, card.pace() * card.pricePerPoint(), 1e-12);
        near("...its points the card's", rec.points, card.points(), 1e-12);
        exact("...its months at home and abroad the card's", rec.monthsHome + rec.monthsAbroad, card.monthsHome() + card.monthsAbroad());
        near("...its interest saved the card's", rec.interestSaved, card.interestSaved(), 1e-12);
        check("the site is handed and is no longer open on the card", !a.abroadPreview(key).open() && a.abroadPreview(key).why().contains("foreign crews"), a.abroadPreview(key).why());
        check("...handing it again is refused", a.handSiteAbroad(key) != null);

        // Months in step: the site's last building opens sooner abroad; the crews are billed its points at the card's price.
        double perPoint = card.pricePerPoint(), sumPoints = 0, sumBill = 0;
        int opensA = -1, opensB = -1;
        boolean billed = true, built = true, oneRecord = true, pacing = true;
        for (int m = 1; m <= MOST_MONTHS && opensA < 0; m++) {
            ConstructionControl.Abroad r0 = bmA.getControl().abroadOf(key);
            double owed0 = r0 == null ? 0 : r0.owed, pace0 = r0 == null ? 0 : r0.pace;
            month(a);
            month(b);
            double points = bmA.getAbroadPointsBuilt(), bill = bmA.getAbroadBill();
            double expected = Math.min(owed0, pace0);
            built &= Math.abs(points - expected) <= 1e-6 * Math.max(1, expected);
            billed &= Math.abs(bill - points * perPoint) <= 1e-9 * Math.max(1, bill);
            oneRecord &= bmA.getControl().abroadSites().size() <= 1;
            pacing &= points <= card.pace() * (1 + 1e-9);
            sumPoints += points;
            sumBill += bill;
            if (sa.getUnderConstruction() == 0 && opensA < 0) opensA = m;
            if (sb.getUnderConstruction() == 0 && opensB < 0) opensB = m;
        }
        out.printf("  the site's last building opens in month %d abroad against %s at home (the card said %,.0f against %,.2f); %,.1f points built abroad for %,.4f%n",
                opensA, opensB < 0 ? "not yet in month " + opensA : String.valueOf(opensB), card.monthsAbroad(), card.monthsHome(), sumPoints, sumBill);
        check("the site's last building opened", opensA > 0);
        check("...in no more months than the card said (the crews' pace over the points)", opensA <= card.monthsAbroad(), opensA + " against " + card.monthsAbroad());
        check("...and in fewer months than in the twin: the twin's site still has buildings on it that month", opensA > 0 && sb.getUnderConstruction() > 0, "the twin's " + sb.getUnderConstruction() + " buildings left");
        check("...fewer months than the card's months at home as well", opensA < card.monthsHome(), opensA + " against " + card.monthsHome());
        check("each month the crews built the least of what the part owed and their pace, and never over the pace", built && pacing);
        check("...and each month the bill on it is its abroad points x the card's price a point (getAbroadBill)", billed && oneRecord);
        near("...in all the part's points were built and billed at that price: the record's points x the card's price a point", sumBill, card.points() * perPoint, 1e-9);
        near("...every point the card said", sumPoints, card.points(), 1e-9);
        check("the record ended with the part built", bmA.getControl().abroadOf(key) == null && !sa.hasAbroad());
    }

    /* ============================ 6. THE MONEY ============================ */

    /**
     * The money of one handed site, followed from the handover to its end and two months more: the bills as the builders' imports the month after,
     * in the audit's TRADE scope and the accounts; the payment earned as the abroad part is built.
     */
    static void followTheMoney(String who, Game g, String key, double unearnedBefore) {
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        ConstructionControl.Abroad r0 = bm.getControl().abroadOf(key).copy();
        BuildingsStacks s = bm.stackOfKey(key);
        double abroadValue = 0;
        for (BuildingsStacks.Contract k : s.getAbroadContracts()) abroadValue += k.getValue();
        check(who + ": the payer's payment reached the builders: unearned revenue is up by the gross extra",
                near2(c.getUnearnedRevenue() - unearnedBefore, r0.paid, 1e-9), String.format("%,.6f against %,.6f", c.getUnearnedRevenue() - unearnedBefore, r0.paid));
        check("...and the abroad contract carries it: its value is the home contract's and the extra", abroadValue >= r0.paid && r0.paid > 0);
        double sumPoints = 0, sumBill = 0, carried = 0, earned = 0, previousBill = 0;
        boolean billsRight = true, next = true, onAudit = true, onAccounts = true, balanced = true;
        int months = 0, ended = -1;
        for (int m = 1; m <= MOST_MONTHS && (ended < 0 || m <= ended + 2); m++) {
            month(g);
            months++;
            MoneyAudit.Result r = g.getLastMoneyAudit();
            double points = c.getSitesAbroadPointsThisMonth(), bill = c.getSitesAbroadBillThisMonth();
            double line = c.statement().otherInputs.getOrDefault(Construction.FOREIGN_CREWS, 0.0);
            billsRight &= Math.abs(points - bm.getAbroadPointsBuilt()) <= 1e-9 * Math.max(1, points) && Math.abs(bill - bm.getAbroadBill()) <= 1e-9 * Math.max(1, bill);
            billsRight &= Math.abs(bill - points * r0.pricePerPoint) <= 1e-9 * Math.max(1, bill);
            next &= Math.abs(line - previousBill) <= 1e-9 * Math.max(1, previousBill);
            double audited = auditLine(r, "- " + BUILDERS + " Imports");
            onAudit &= r != null && audited >= line - MoneyAudit.CENT && r.tradeOut >= line - MoneyAudit.CENT
                    && Math.abs(audited - c.statement().imports) < MoneyAudit.CENT;
            onAccounts &= g.getForeignAccounts().tradeImports() >= line - MoneyAudit.CENT && g.getEconomyManager().getNationalAccounts().getTotalImports() >= line - MoneyAudit.CENT;
            balanced &= r != null && r.relative() < 1e-4;
            if (ended < 0) for (BuildingsStacks.Due d : s.getDues()) if (d.abroad) earned += d.revenue;
            sumPoints += points;
            sumBill += bill;
            carried += line;
            previousBill = bill;
            if (ended < 0 && bm.getControl().abroadOf(key) == null) ended = m;
        }
        out.printf("  %s: %d months; the crews built %,.1f points and billed %,.4f; the statements carried %,.4f of it (the last bill the month after); the abroad contract earned %,.4f of its %,.4f%n",
                who, months, sumPoints, sumBill, carried, earned, abroadValue);
        check("the part ended and was built in full: the record's points, billed at its price a point", ended > 0 && Math.abs(sumPoints - r0.points) <= 1e-6 * r0.points
                && Math.abs(sumBill - r0.points * r0.pricePerPoint) <= 1e-9 * sumBill);
        check("each month's bill is its points built here at the price struck at the handover, the builders' own figures agreeing", billsRight);
        check("...each month's statement carries the month before's bill as the builders' import from the crews (FOREIGN_CREWS), struck a month on", next);
        near("...the statements carried every bill: their sum is the sum of the bills", carried, sumBill, 1e-9);
        check("...in the audit's imports line for the builders and its TRADE scope: the line is the statement's, the trade out at least that", onAudit);
        check("...and in the national accounts' imports and the foreign accounts' trade imports", onAccounts);
        check("...every month closed the money audit", balanced);
        near("the payer's payment was earned as the part was built: the abroad contract's dues are all of its value, the extra among them", earned, abroadValue, 1e-9);
    }

    static boolean near2(double actual, double expected, double tol) { return Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol); }

    static void theMoney(Path root) {
        out.println("\n--- 6. the money is booked once: the payment in the builders' book and earned with the part, the crews' bill an import, every month balanced ---");
        // A business's handover.
        Game g = choiceCity(root, "money", 0, 0);
        BuildingsTemplate house = template(g, HOUSE);
        String key = ConstructionControl.keyOf(house);
        g.getEconomyManager().setExchangeRate(strongRate(g));
        Construction c = g.getSectors().construction();
        double unearned = c.getUnearnedRevenue(), cash = g.getEconomyManager().getSectorCash(RE), builders = g.getEconomyManager().getSectorCash(BUILDERS);
        check("fixture: nothing is on the foreign crews' pooled book, so the only crews billed are the site's", c.getForeignBook() == 0 && g.getBuildingManager().getControl().abroadSites().isEmpty());
        quietly(g::investorsHandAbroad);
        ConstructionControl.Abroad rec = g.getBuildingManager().getControl().abroadOf(key);
        check("the landlords handed the site", rec != null);
        if (rec == null) return;
        near("the landlords paid the gross extra out of their cash", cash - g.getEconomyManager().getSectorCash(RE), rec.paid, 1e-9);
        out.printf("  the landlords' cash fell by %,.4f, the builders' cash moved by %,.4f, unearned revenue by %,.4f%n", cash - g.getEconomyManager().getSectorCash(RE),
                g.getEconomyManager().getSectorCash(BUILDERS) - builders, c.getUnearnedRevenue() - unearned);
        followTheMoney("the landlords' site", g, key, unearned);
        // The city's.
        Game city = citySite(root, "money-city", SCHOOLS, 0, CITY_QUEUE_MONTHS);
        String ck = key(city, SCHOOL);
        Construction cc = city.getSectors().construction();
        double treasury = city.getCash(), unearnedCity = cc.getUnearnedRevenue();
        check("the city hands its site", city.handSiteAbroad(ck) == null);
        check("...the treasury paid its extra and it is in the builders' book",
                near2(treasury - city.getCash(), city.getBuildingManager().getControl().abroadOf(ck).paid, 1e-9)
                        && near2(cc.getUnearnedRevenue() - unearnedCity, city.getBuildingManager().getControl().abroadOf(ck).paid, 1e-9));
        followTheMoney("the city's site", city, ck, unearnedCity);
    }

    /* ============================ 7. THE SAVED CHOICE ============================ */

    /** The fields of a record that differ between two, by name; every one compared to the bit. */
    static List<String> differ(ConstructionControl.Abroad a, ConstructionControl.Abroad b) {
        List<String> d = new ArrayList<>();
        if (!a.key.equals(b.key)) d.add("key");
        if (a.templateId != b.templateId) d.add("templateId");
        if (!a.building.equals(b.building)) d.add("building");
        if (!a.payer.equals(b.payer)) d.add("payer");
        if (a.month != b.month) d.add("month");
        for (String f : new String[] { "points", "owed", "materials", "pace", "pricePerPoint", "paid", "monthsHome", "monthsAbroad", "interestSaved", "earningsForward" }) {
            try {
                double x = ConstructionControl.Abroad.class.getField(f).getDouble(a), y = ConstructionControl.Abroad.class.getField(f).getDouble(b);
                if (Double.doubleToLongBits(x) != Double.doubleToLongBits(y)) d.add(f);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
        return d;
    }

    static boolean sameContracts(java.util.Collection<BuildingsStacks.Contract> a, java.util.Collection<BuildingsStacks.Contract> b) {
        if (a.size() != b.size()) return false;
        java.util.Iterator<BuildingsStacks.Contract> i = a.iterator(), j = b.iterator();
        while (i.hasNext()) {
            BuildingsStacks.Contract x = i.next(), y = j.next();
            if (!x.payer.equals(y.payer) || x.creditable != y.creditable || !Snap.of(x).same(Snap.of(y))
                    || Double.doubleToLongBits(x.getRecovered()) != Double.doubleToLongBits(y.getRecovered())) return false;
        }
        return true;
    }

    static void theSave(Path root) {
        out.println("\n--- 7. the saved choice: a record half done, saved and loaded, is the same and plays on month for month as its live twin ---");
        Game live = choiceCity(root, "saved", 0, CITY_HOUSES);
        BuildingsTemplate house = template(live, HOUSE);
        String key = ConstructionControl.keyOf(house);
        live.getEconomyManager().setExchangeRate(strongRate(live));
        quietly(live::investorsHandAbroad);
        for (int k = 0; k < BEFORE_THE_SAVE; k++) month(live);
        BuildingManager bm = live.getBuildingManager();
        ConstructionControl.Abroad rec = bm.getControl().abroadOf(key);
        check("fixture: the record stands, half done: some of the part built, some owed", rec != null && rec.owed > 0 && rec.owed < rec.points,
                rec == null ? "none" : String.format("%,.1f of %,.1f owed", rec.owed, rec.points));
        if (rec == null) return;
        Game back = new Game(filesOf(root, "saved"));
        quietly(() -> {
            live.saveGame(SCRATCH_SLOT, "the choice");
            back.run();
            back.loadGameSave(SCRATCH_SLOT);
            // The fixture holds every sector so no planner builds but by this harness's hand; a hold is a harness lever a save does not carry (BusinessInvestment.holdSector()), so the loaded city is held as the live one is.
            for (Sector s : back.getSectors().all()) back.getBusinessInvestment().holdSector(s.key());
        });
        BuildingManager bb = back.getBuildingManager();
        ConstructionControl.Abroad got = bb.getControl().abroadOf(key);
        check("the loaded city has the record", got != null);
        if (got == null) return;
        List<String> d = differ(rec, got);
        check("...every field of it the same to the bit", d.isEmpty(), d.isEmpty() ? String.format("%s %,.1f owed, %,.2f a point", got.payer, got.owed, got.pricePerPoint) : "differ: " + d);
        BuildingsStacks sl = bm.getStack(house), sb = bb.getStack(template(back, HOUSE));
        check("...the abroad contracts, with their points", sameContracts(sl.getAbroadContracts(), sb.getAbroadContracts()) && !sb.getAbroadContracts().isEmpty()
                && sb.getAbroadContracts().iterator().next().getPoints() > 0);
        check("...the home contracts, the city's with its points", sameContracts(sl.getContracts(), sb.getContracts()) && contractOf(sb, "City") != null && contractOf(sb, "City").getPoints() > 0);
        check("...the stack has work abroad and is not the city's own", sb.hasAbroad() && !sb.isCitysOwn());
        exact("...its progress", sb.getConstructionProgress(), sl.getConstructionProgress());
        exact("...its buildings under construction", sb.getUnderConstruction(), sl.getUnderConstruction());
        exact("...and the crews' month last settled (the points they built on the sites)", back.getSectors().construction().getSitesAbroadPointsThisMonth(),
                live.getSectors().construction().getSitesAbroadPointsThisMonth());
        exact("...and its bill", back.getSectors().construction().getSitesAbroadBillThisMonth(), live.getSectors().construction().getSitesAbroadBillThisMonth());

        boolean same = true, recordSame = true, bills = true, cash = true;
        String first = "";
        for (int m = 1; m <= AFTER_THE_SAVE; m++) {
            month(live);
            month(back);
            ConstructionControl.Abroad l = bm.getControl().abroadOf(key), r = bb.getControl().abroadOf(key);
            boolean now = sl.getConstructionProgress() == sb.getConstructionProgress() && sl.getUnderConstruction() == sb.getUnderConstruction()
                    && sl.getQuantity() == sb.getQuantity() && sl.getContractValue() == sb.getContractValue();
            boolean rs = (l == null) == (r == null) && (l == null || differ(l, r).isEmpty());
            boolean bl = Double.doubleToLongBits(bm.getAbroadBill()) == Double.doubleToLongBits(bb.getAbroadBill())
                    && Double.doubleToLongBits(bm.getAbroadPointsBuilt()) == Double.doubleToLongBits(bb.getAbroadPointsBuilt())
                    && Double.doubleToLongBits(live.getSectors().construction().getSitesAbroadBillThisMonth())
                    == Double.doubleToLongBits(back.getSectors().construction().getSitesAbroadBillThisMonth());
            boolean ca = Math.abs(live.getCash() - back.getCash()) <= 1e-9 * Math.max(1, Math.abs(live.getCash()));
            if (first.isEmpty() && !(now && rs && bl && ca)) first = "month " + m + (now ? "" : String.format(" stack(progress %s/%s, contracts %s/%s, standing %d/%d)", sl.getConstructionProgress(), sb.getConstructionProgress(), sl.getContractValue(), sb.getContractValue(), sl.getQuantity(), sb.getQuantity()))
                    + (rs ? "" : " record") + (bl ? "" : String.format(" bill(%s/%s, points %s/%s)", bm.getAbroadBill(), bb.getAbroadBill(), bm.getAbroadPointsBuilt(), bb.getAbroadPointsBuilt()))
                    + (ca ? "" : String.format(" treasury(%s/%s)", live.getCash(), back.getCash()));
            same &= now; recordSame &= rs; bills &= bl; cash &= ca;
        }
        check("the next " + AFTER_THE_SAVE + " months the stack's progress, buildings and contracts' value are the live twin's, to the bit", same, first);
        check("...the record is the live twin's every month, to the bit (or ended in both)", recordSame);
        check("...the crews' points and bill on the sites, to the bit", bills);
        check("...and the treasury's cash", cash);
    }

    /* ============================ 8. A WAIT AT HOME THAT NEVER ENDS, SAVED ============================ */

    /** The figures a record keeps as numbers: the ones differ() compares. */
    static final String[] RECORD_FIGURES = { "points", "owed", "materials", "pace", "pricePerPoint", "paid", "monthsHome", "monthsAbroad", "interestSaved", "earningsForward" };

    /** The first of a record's figures that is not finite, or null when every one is. */
    static String notFinite(ConstructionControl.Abroad r) {
        for (String f : RECORD_FIGURES) {
            try {
                if (!Double.isFinite(ConstructionControl.Abroad.class.getField(f).getDouble(r))) return f;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    /**
     * The city saved to the scratch slot of its own folder and loaded into a fresh Game over that folder, held as the live one is (a hold is a
     * harness lever a save does not carry). Null when the save or the load threw or the save was not written; the reason is in {@code why}.
     */
    static Game savedAndLoaded(Path root, String name, Game live, String slotName, String[] why) {
        Game back = new Game(filesOf(root, name));
        try {
            GameFiles.Result[] result = new GameFiles.Result[1];
            quietly(() -> result[0] = live.saveGame(SCRATCH_SLOT, slotName));
            if (result[0] == null || !result[0].ok) {
                why[0] = result[0] == null ? "no result" : String.valueOf(result[0].error);
                return null;
            }
            quietly(() -> {
                back.run();
                back.loadGameSave(SCRATCH_SLOT);
                for (Sector s : back.getSectors().all()) back.getBusinessInvestment().holdSector(s.key());
            });
        } catch (RuntimeException e) {
            why[0] = e.toString();
            return null;
        }
        return back;
    }

    static void theEndlessWait(Path root) {
        out.println("\n--- 8. a record from a wait at home that never ends: kept at the greatest finite wait, every figure finite, so that the city saves; loaded, the same ---");
        Game live = choiceCity(root, "endless", 0, 0);
        BuildingManager bm = live.getBuildingManager();
        EconomyManager em = live.getEconomyManager();
        Construction c = live.getSectors().construction();
        BuildingsTemplate house = template(live, HOUSE);
        String key = ConstructionControl.keyOf(house);
        em.setExchangeRate(strongRate(live));
        Verdict v = verdict(live, house, RE);
        check("fixture: the landlords' Houses are on site with terms to hand over", v != null && contractOf(bm.getStack(house), RE) != null);
        if (v == null) return;

        // THE CAUSE. No input of the city reaches an endless wait (the last lines of this section show why); the wait output the terms are read at is the one
        // the method takes as its argument, and a city of no builders' month and no contractor would pass nothing: siteMonths() is then NaN, its never.
        check("the cause: with no output to build with, the site's wait at home is NaN (siteMonths(): its never)", Double.isNaN(bm.siteMonths(key, 0)));
        BuildingManager.AbroadTerms terms = bm.abroadTermsOf(house, RE, 0);
        check("...the terms read at it have a wait at home that is +Infinity, and a finite wait abroad",
                terms != null && terms.monthsHome() == INF && Double.isFinite(terms.monthsAbroad()), String.valueOf(terms == null ? null : terms.monthsHome() + " / " + terms.monthsAbroad()));
        if (terms == null) return;
        double cost = costOnSite(live, house, v);
        double perMonth = live.getBusinessInvestment().estimatedMonthlyProfit(RE, house) * terms.buildings();
        double rate = v.rate();
        double interest = BuildersShort.interestSaved(cost, rate, terms.monthsAbroad(), terms.monthsHome());
        double earnings = BuildersShort.broughtForward(perMonth, 0, terms.monthsAbroad(), terms.monthsHome());
        check("fixture: handed an endless wait, the earnings brought forward at a rate of nothing are endless (what the record must not keep)",
                perMonth > 0 && earnings == INF, perMonth + " a month, " + earnings);
        check("...and the interest saved at the real rate is a figure", Double.isFinite(interest) && interest >= 0, String.valueOf(interest));

        // The handover, as investorsHandAbroad() makes it: the payment out of the landlords' cash and billed to the builders, then the books' half.
        double gross = terms.extra() / (1 - v.rB());
        double unearned0 = c.getUnearnedRevenue();
        live.getSectorInvestor(RE).spend(gross);
        c.bill(gross, 0);
        ConstructionControl.Abroad rec = bm.handSiteAbroad(terms, live.getMonth(), gross, interest, earnings);
        check("the site is handed: the record stands and the stack has work abroad", rec != null && bm.getControl().abroadOf(key) != null && bm.getStack(house).hasAbroad());
        if (rec == null) return;
        check("the terms' wait at home is infinite and the record's is Double.MAX_VALUE, the greatest finite wait", terms.monthsHome() == INF && rec.monthsHome == Double.MAX_VALUE,
                terms.monthsHome() + " -> " + rec.monthsHome);
        String bad = notFinite(rec);
        check("every figure the record keeps is finite", bad == null, bad == null ? "all ten" : "not finite: " + bad);
        exact("...the endless earnings brought forward it was handed are kept as nothing", rec.earningsForward, 0);
        near("...its points are the terms'", rec.points, terms.points(), 1e-12);
        near("...at the pace and the price a point the terms struck", rec.pace * rec.pricePerPoint, terms.pace() * terms.pricePerPoint(), 1e-12);
        exact("...and the months abroad", rec.monthsAbroad, terms.monthsAbroad());
        near("...it paid the gross extra, which is in the builders' book", c.getUnearnedRevenue() - unearned0, rec.paid, 1e-9);

        // THE SAVE.
        String[] why = { "" };
        Game back = savedAndLoaded(root, "endless", live, "the endless wait", why);
        check("the city saves and loads with the record in it (without BuildingManager's guard the record's infinity is DataSave's Gson refusing it, and this line throws)",
                back != null, why[0]);
        if (back == null) return;
        BuildingManager bb = back.getBuildingManager();
        ConstructionControl.Abroad got = bb.getControl().abroadOf(key);
        check("the loaded city has the record", got != null);
        if (got == null) return;
        List<String> d = differ(rec, got);
        check("...every field of it the same to the bit", d.isEmpty(), d.isEmpty() ? "monthsHome " + got.monthsHome : "differ: " + d);
        exact("...its wait at home still the greatest finite wait", got.monthsHome, Double.MAX_VALUE);
        BuildingsStacks sl = bm.getStack(house), sb = bb.getStack(template(back, HOUSE));
        check("...the abroad contracts and the home contracts the live city's", sameContracts(sl.getAbroadContracts(), sb.getAbroadContracts()) && !sb.getAbroadContracts().isEmpty()
                && sameContracts(sl.getContracts(), sb.getContracts()));
        boolean recordSame = true, cash = true;
        for (int m = 1; m <= AFTER_THE_SAVE; m++) {
            month(live);
            month(back);
            ConstructionControl.Abroad l = bm.getControl().abroadOf(key), r = bb.getControl().abroadOf(key);
            recordSame &= (l == null) == (r == null) && (l == null || differ(l, r).isEmpty());
            cash &= Math.abs(live.getCash() - back.getCash()) <= 1e-9 * Math.max(1, Math.abs(live.getCash()));
        }
        check("the next " + AFTER_THE_SAVE + " months the record is the live twin's every month, to the bit (or ended in both)", recordSame);
        check("...and the treasury's cash", cash);

        // WHY NO INPUT OF THE CITY REACHES IT: the repairs of standing roads take every point of the builders' month and the sites' month is
        // nothing, so that read at it alone a wait is endless (the trap, ConstructionTrapCheck) - but orders and the card read at orderWaitOutput(),
        // which adds the contractor's month, and that keeps every wait finite.
        roads(live, (int) Math.ceil(live.getBuildingOutputAtEveryPost() / ConstructionTrapCheck.perRoad(live)) + 1);
        check("fixture: standing roads' repairs take every point of the builders' month: the sites' output at every post is nothing", live.getBuildingOutputAtEveryPost() == 0,
                String.valueOf(live.getBuildingOutputAtEveryPost()));
        check("...read at that output alone the Houses' wait is endless (NaN)", Double.isNaN(bm.siteMonths(key, live.getBuildingOutputAtEveryPost())));
        double wait = bm.siteMonths(key, live.orderWaitOutput());
        check("...but orderWaitOutput() adds the contractor's month, which is over nothing, and the wait read at it is a finite number of months",
                live.orderWaitOutput() > 0 && !Double.isNaN(wait) && wait < Double.MAX_VALUE, live.orderWaitOutput() + " -> " + wait);
    }

    /* ============================ 9. A PAVING HANDED ABROAD ============================ */

    /**
     * A calm city whose landlords' Houses are built (nothing else on site), PAVINGS Gravel Roads standing and their paving ordered on the Paved
     * Road's site, the queue at home made long by standing Paved Roads to CITY_QUEUE_MONTHS; the city's cash given for the extra.
     */
    static Game pavingCity(Path root, String name) {
        Game g = calm(root, name);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate house = template(g, HOUSE), gravel = template(g, ConstructionControl.PAVE_FROM);
        for (int k = 0; k < MOST_MONTHS && bm.getStack(house).getUnderConstruction() > 0; k++) month(g);
        if (bm.getStack(house).getUnderConstruction() > 0) throw new IllegalStateException("the landlords' Houses did not finish in " + MOST_MONTHS + " months");
        g.setCashForTest(g.getCash() + 1e9);
        Game.BuildResult[] built = new Game.BuildResult[1];
        quietly(() -> built[0] = g.buildStack(gravel, PAVINGS, true));
        if (built[0] != Game.BuildResult.SUCCESS) throw new IllegalStateException("the gravel roads could not be stood: " + built[0]);
        boolean[] ordered = new boolean[1];
        quietly(() -> ordered[0] = g.paveRoads(PAVINGS));
        if (!ordered[0]) throw new IllegalStateException("the paving could not be ordered");
        roads(g, roadsFor(g, CITY_QUEUE_MONTHS));
        g.setCashForTest(g.getCash() + 1e9);
        return g;
    }

    static void thePaving(Path root) {
        out.println("\n--- 9. a paving handed abroad: its whole work to the foreign crews, nothing of it at home, the gravel roads retired as the paved roads open, sooner than in the twin ---");
        Game a = pavingCity(root, "paving-a"), b = pavingCity(root, "paving-b");
        BuildingManager bmA = a.getBuildingManager(), bmB = b.getBuildingManager();
        BuildingsTemplate paved = template(a, ConstructionControl.PAVE_TO), gravel = template(a, ConstructionControl.PAVE_FROM);
        String key = ConstructionControl.keyOf(paved);
        BuildingsStacks sa = bmA.getStack(paved), sb = bmB.getStack(template(b, ConstructionControl.PAVE_TO));
        check("fixture: the twins are the same city: the queue at home, the sites' month, the points owed on the Paved Road site and the treasury",
                bmA.queueAtHome() == bmB.queueAtHome() && a.getBuildingOutputAtEveryPost() == b.getBuildingOutputAtEveryPost()
                        && sa.owedPoints() == sb.owedPoints() && a.getCash() == b.getCash());
        check("fixture: the Paved Road site is a paving site with PAVINGS gravel roads being paved and none left to pave, the paving the whole of the site",
                a.isPavingSite(key) && b.isPavingSite(key) && a.pavingNow() == PAVINGS && a.paveable() == 0 && sa.getUnderConstruction() == PAVINGS,
                a.pavingNow() + " being paved, " + a.paveable() + " to pave, " + sa.getUnderConstruction() + " on site");
        check("fixture: the queue at home is long, past the knee and the threshold", bmA.monthsQueued() > bmA.thresholdMonths() && bmA.thresholdMonths() >= BuildersShort.KNEE_MONTHS,
                String.format("%.2f months queued, the threshold %.2f", bmA.monthsQueued(), bmA.thresholdMonths()));
        Game.AbroadCard card = a.abroadPreview(key);
        out.printf("  the card on the Paved Road site: %,.0f points at %,.1f a month; extra %,.4f; %,.0f months abroad against %,.2f at home%n",
                card.points(), card.pace(), card.extra(), card.monthsAbroad(), card.monthsHome());
        check("the card is open on the paving site, with months saved and an extra to pay", card.open() && card.why().isEmpty() && card.monthsSaved() > 0 && card.extra() > 0, card.why());
        double owed0 = sa.owedPoints(), queue0 = bmA.queueAtHome(), cash0 = a.getCash();
        near("fixture: the site owes the paving's work: its pavings' points, nothing worked yet", owed0, PAVINGS * paved.getConstructionPoints(), 1e-9);
        String refused = a.handSiteAbroad(key);
        check("the city hands the paving over: handSiteAbroad returns null", refused == null, String.valueOf(refused));
        ConstructionControl.Abroad rec = bmA.getControl().abroadOf(key);
        check("...the twin keeps its paving at home: no record, the card still open", bmB.getControl().abroadOf(key) == null && b.abroadPreview(key).open());
        if (rec == null) return;
        near("the record's points are the paving's work: the site's owed points at the handover, the card's points", rec.points, owed0, 1e-9);
        near("...the card's", rec.points, card.points(), 1e-12);
        near("...and what it owes is all of them: nothing built yet", rec.owed, rec.points, 1e-12);
        near("the treasury paid the card's extra (to the rounding of a treasury of a billion: a few of its last digits)", cash0 - a.getCash(), card.extra(), 8 * Math.ulp(cash0));
        check("the site is still the paving site, and open on the card no longer", a.isPavingSite(key) && !a.abroadPreview(key).open());
        check("nothing of the paving stays at home: the city has no home contract on the site and no share of its home work",
                contractOf(sa, "City") == null && sa.homePointsShare("City") == 0 && sa.getAbroadContracts().size() == 1);
        check("...the home work on the site is nothing: its owed points less the part abroad", Math.abs(sa.owedPoints() - rec.owed) <= 1e-9 * Math.max(1, rec.owed),
                String.format("%,.6f owed, %,.6f abroad", sa.owedPoints(), rec.owed));
        near("...the queue at home fell by the paving's work", queue0 - bmA.queueAtHome(), rec.owed, 1e-9);
        check("...and nothing is left to hand: no terms for the city on the site", bmA.abroadTermsOf(paved, "City", a.orderWaitOutput()) == null);

        // Months in step: the gravel roads retire as the paved roads open, in step with the paved roads standing; the handed site's all of them sooner.
        long gravel0 = bmA.getQuantity(gravel.getId()), paved0 = bmA.getQuantity(paved.getId());
        LandManager land = a.getLandManager();
        boolean order = true, ledger = true;
        int doneA = -1, doneB = -1, firstRetired = -1;
        for (int m = 1; m <= MOST_MONTHS && (doneA < 0 || doneB < 0); m++) {
            if (doneA < 0) month(a);
            if (doneB < 0) month(b);
            long retired = gravel0 - bmA.getQuantity(gravel.getId()), opened = bmA.getQuantity(paved.getId()) - paved0;
            order &= retired == opened && a.pavingNow() == PAVINGS - retired;
            ledger &= Double.doubleToLongBits(land.getAllocatedSqFt()) == Double.doubleToLongBits(bmA.getTotalLandFootprint());
            if (firstRetired < 0 && retired > 0) firstRetired = m;
            if (doneA < 0 && retired == PAVINGS && a.pavingNow() == 0) doneA = m;
            if (doneB < 0 && gravel0 - bmB.getQuantity(gravel.getId()) == PAVINGS && b.pavingNow() == 0) doneB = m;
        }
        out.printf("  the last gravel road retires in month %d abroad against %s at home; the first in month %d%n", doneA, doneB < 0 ? "not by month " + MOST_MONTHS : String.valueOf(doneB), firstRetired);
        check("the gravel roads retired as the paved roads opened: each month as many retired as had opened, and the pavings left were the rest", order);
        check("...every one of them retired and the paving is done", doneA > 0 && a.pavingNow() == 0 && bmA.getControl().pavings().isEmpty()
                && bmA.getQuantity(gravel.getId()) == gravel0 - PAVINGS && bmA.getQuantity(paved.getId()) == paved0 + PAVINGS);
        check("...the land ledger was the buildings' footprint every month, to the bit", ledger);
        check("they all retired in fewer months than in the twin that keeps the paving at home", doneA > 0 && (doneB < 0 ? b.pavingNow() > 0 : doneA < doneB),
                doneA + " against " + (doneB < 0 ? "not by month " + MOST_MONTHS : String.valueOf(doneB)));
        check("...and sooner than the card's months at home", doneA > 0 && doneA < card.monthsHome(), doneA + " against " + card.monthsHome());
        check("the record ended with the paving built", bmA.getControl().abroadOf(key) == null && !sa.hasAbroad());
    }

    /* ============================ 10. THE PAYMENT'S TWO REFUSALS ============================ */

    /** The saving the landlords' months saved are worth at the rate of a loan for the shortfall (Game.judged(): the loan's rate; a bond's cheaper coupon can only raise it). */
    static double savingBorrowing(Game g, Verdict v, double borrowed) {
        double annual = g.getEconomyManager().getBusinessDebtManager().projectRate(RE, borrowed);
        return BuildersShort.broughtForward(v.perMonth(), g.getBusinessInvestment().realTestRate(annual) / 12, v.terms().monthsAbroad(), v.terms().monthsHome());
    }

    /** Section 2's mover at the strong D$, its landlords' cash cut to SHORT_CASH_SHARE of the gross extra they would pay: they must borrow the rest. */
    static Game shortMover(Path root, String name) {
        Game g = choiceCity(root, name, 0, 0);
        g.getEconomyManager().setExchangeRate(strongRate(g));
        Verdict v = verdict(g, template(g, HOUSE), RE);
        if (v == null) throw new IllegalStateException("the mover has no terms");
        g.getEconomyManager().setSectorCash(RE, SHORT_CASH_SHARE * v.gross());
        return g;
    }

    static void thePayment(Path root) {
        out.println("\n--- 10. the payment's two refusals, both sides each: a business that must borrow (the bank lends / will not), the city's treasury (holds the extra / does not) ---");
        // (a) A BUSINESS THAT MUST BORROW.
        Game lends = shortMover(root, "borrows"), barred = shortMover(root, "barred");
        BusinessDebtManager credit = barred.getEconomyManager().getBusinessDebtManager();
        // The ban a default writes on a sector's credit record (BusinessDebtManager.restoreCreditRecord(), as BuildCardCheck sets it): its months are a default's exclusion.
        Map<String, Integer> ban = new LinkedHashMap<>(credit.getBlockedMonthsAll());
        ban.put(RE, BusinessDebtManager.exclusionFor(1));
        credit.restoreCreditRecord(credit.getRestructureCounts(), ban);
        BuildingsTemplate house = template(lends, HOUSE);
        String key = ConstructionControl.keyOf(house);
        Verdict vl = verdict(lends, house, RE), vb = verdict(barred, template(barred, HOUSE), RE);
        double shortL = vl.gross() - lends.getEconomyManager().getSectorCash(RE), shortB = vb.gross() - barred.getEconomyManager().getSectorCash(RE);
        check("fixture: the landlords hold under the gross extra in both, so each is short and must borrow", shortL > 0 && shortB > 0 && shortL == shortB, String.format("short %,.4f of %,.4f", shortL, vl.gross()));
        check("fixture: in both the extra, net of the tax recovered, is under the saving at the loan's rate for the shortfall, and the crews would finish it sooner: the sum says move",
                soonerAbroad(vl) && soonerAbroad(vb) && vl.cost() > 0 && vl.cost() < savingBorrowing(lends, vl, shortL) && vb.cost() < savingBorrowing(barred, vb, shortB),
                String.format("%,.4f under %,.4f", vl.cost(), savingBorrowing(lends, vl, shortL)));
        check("fixture: the one input that differs: one is under a borrowing ban, and the bank lends the shortfall to the other and not to it",
                !lends.getEconomyManager().getBusinessDebtManager().isBorrowingBlocked(RE) && credit.isBorrowingBlocked(RE)
                        && lends.getSectorInvestor(RE).canBorrow(shortL) && !barred.getSectorInvestor(RE).canBorrow(shortB));
        BuildingManager bmL = lends.getBuildingManager(), bmB = barred.getBuildingManager();
        EconomyManager emL = lends.getEconomyManager(), emB = barred.getEconomyManager();
        Construction cL = lends.getSectors().construction(), cB = barred.getSectors().construction();
        BuildingsStacks slb = bmB.getStack(template(barred, HOUSE));
        double cashL = emL.getSectorCash(RE), debtL = emL.getBusinessDebtManager().getPrincipal(RE), unearnedL = cL.getUnearnedRevenue();
        double cashB = emB.getSectorCash(RE), debtB = credit.getPrincipal(RE), unearnedB = cB.getUnearnedRevenue(), buildersB = emB.getSectorCash(BUILDERS);
        Snap homeB = Snap.of(contractOf(slb, RE));
        quietly(lends::investorsHandAbroad);
        quietly(barred::investorsHandAbroad);
        ConstructionControl.Abroad rec = bmL.getControl().abroadOf(key);
        check("the landlords the bank lends to move: a record of work abroad on the site", rec != null && bmL.getStack(house).hasAbroad());
        if (rec != null) {
            double tol = 1e-6 * vl.gross();
            near("...they paid the gross extra, as a business that holds the cash does", rec.paid, vl.gross(), 1e-9);
            check("...their debt rose by the shortfall (the loan grossed for its fee: not under it)", emL.getBusinessDebtManager().getPrincipal(RE) - debtL >= shortL - tol,
                    String.format("%,.4f against the shortfall %,.4f", emL.getBusinessDebtManager().getPrincipal(RE) - debtL, shortL));
            near("...their cash after is the cash less the gross extra plus the loan: what the loan handed them", emL.getSectorCash(RE), cashL - rec.paid + shortL, tol);
            near("...and the extra is in the builders' book: unearned revenue up by it", cL.getUnearnedRevenue() - unearnedL, rec.paid, 1e-9);
        }
        check("the landlords the bank will not lend to stay: no record, no work abroad, the home contract theirs still",
                bmB.getControl().abroadOf(key) == null && !slb.hasAbroad() && Snap.of(contractOf(slb, RE)).same(homeB));
        exact("...their cash unchanged", emB.getSectorCash(RE), cashB);
        exact("...their debt unchanged: nothing borrowed", credit.getPrincipal(RE), debtB);
        exact("...the builders' unearned revenue unchanged", cB.getUnearnedRevenue(), unearnedB);
        exact("...and the builders' cash", emB.getSectorCash(BUILDERS), buildersB);

        // (b) THE CITY'S TREASURY.
        Game over = citySite(root, "treasury-over", SCHOOLS, 0, CITY_QUEUE_MONTHS), under = citySite(root, "treasury-under", SCHOOLS, 0, CITY_QUEUE_MONTHS);
        String ck = key(over, SCHOOL);
        BuildingManager bmO = over.getBuildingManager(), bmU = under.getBuildingManager();
        BuildingsStacks so = bmO.getStack(template(over, SCHOOL)), su = bmU.getStack(template(under, SCHOOL));
        Game.AbroadCard cardO = over.abroadPreview(ck), cardU = under.abroadPreview(ck);
        check("fixture: the card is open on the city's site in both, at the same extra", cardO.open() && cardU.open() && cardO.extra() > 0 && cardO.extra() == cardU.extra(), cardO.why());
        over.setCashForTest(TREASURY_OVER * cardO.extra());
        under.setCashForTest(TREASURY_UNDER * cardU.extra());
        check("fixture: the one input that differs: the treasury holds over the card's extra in one and under it in the other", over.getCash() > cardO.extra() && under.getCash() < cardU.extra(),
                String.format("%,.2f and %,.2f against the extra %,.2f", over.getCash(), under.getCash(), cardO.extra()));
        double cashO = over.getCash(), unearnedO = over.getSectors().construction().getUnearnedRevenue();
        double cashU = under.getCash(), unearnedU = under.getSectors().construction().getUnearnedRevenue(), valueU = su.getContractValue();
        Snap homeU = Snap.of(contractOf(su, "City"));
        String paid = over.handSiteAbroad(ck), refused = under.handSiteAbroad(ck);
        check("the treasury that holds the extra hands the site over: handSiteAbroad returns null and the record stands", paid == null && bmO.getControl().abroadOf(ck) != null && so.hasAbroad(),
                String.valueOf(paid));
        near("...it paid the card's extra out of the treasury", cashO - over.getCash(), cardO.extra(), 1e-9);
        near("...which is in the builders' book", over.getSectors().construction().getUnearnedRevenue() - unearnedO, cardO.extra(), 1e-9);
        check("the treasury that holds less is refused with the reason: \"the treasury holds ...\"", refused != null && refused.startsWith("the treasury holds"), String.valueOf(refused));
        check("...no record, no work abroad, no abroad contract: the stack is the city's own as it was", bmU.getControl().abroadOf(ck) == null && !su.hasAbroad()
                && su.getAbroadContracts().isEmpty() && su.isCitysOwn() && Snap.of(contractOf(su, "City")).same(homeU));
        exact("...the treasury unchanged", under.getCash(), cashU);
        exact("...the builders' unearned revenue unchanged", under.getSectors().construction().getUnearnedRevenue(), unearnedU);
        exact("...the stack's contract value unchanged", su.getContractValue(), valueU);
        check("...and the card is open still, at the same extra", under.abroadPreview(ck).open() && under.abroadPreview(ck).extra() == cardU.extra());
    }
}
