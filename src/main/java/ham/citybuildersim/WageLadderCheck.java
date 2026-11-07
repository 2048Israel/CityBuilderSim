package ham.citybuildersim;

import ham.citybuildersim.sectors.RealEstate;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The wage ladder a city is founded on (R0d): today's PayTier ladder in every city but one founded the 1900 way, which pays 1901's, the
 * census's. Not part of the game.
 *
 * WHAT THIS HAS TO PROVE (R0d, 2026-11-06; docs/studies/research-1900.md 5.2 and 12.1; docs/queue/R0-notes.md call 2). A city founded in 1900
 * pays about a quarter of today's real wage with the 1901 census's ratios between the skills, and only that city: the default way is
 * today's ladder to the bit, so every default trace stays what it was. What is checkable:
 *
 *   1. THE TABLE: one 1901 rung per PayTier rung, in PayTier's order, each with a comparator, a positive year's earnings and a source key or an
 *      estimate flag (the tiers the census does not price are flagged), rising up the ladder; the 1901 anchor is its unskilled rung through
 *      the class's one conversion
 *   2. TODAY'S LADDER IS PayTier'S TO THE BIT: every ratio and every rung equal, no tolerance; a city founded the default way is on it, its
 *      minimum wage, its dial's bounds, its wages, its rents, its grant and its pension's base the ones it always opened on
 *   3. A CITY FOUNDED THE 1900 WAY STARTS ON 1901'S LADDER: the ladder, the minimum wage at the anchor, every wage the anchor times the
 *      ladder's ratio (the population manager's too) before any month, the bounds the anchor's multiples, the rents, the grant and the
 *      pension's base struck on the anchor, and an unskilled wage about a quarter of a default city's
 *   4. THE MARKET STILL MOVES A 1901 LADDER: on a bare market founded on it, posts above supply raise a band's wage above its base, posts
 *      equal to supply leave it walking to base times the curve's multiple at one at the damping rate, the cost of living lifts the base,
 *      and the dial is bounded by the 1901 anchor's multiples (a floor under today's lowest setting can be set)
 *   5. SAVED AND LOADED: a 1900 city keeps its ladder, its minimum wage (one under today's lowest setting, which only the ladder's bounds let
 *      back), its wages, its bounds and its rents; a default city loads today's, into a fresh Game and into one that played a 1900 city
 *   6. PLAYED: a 1900 village plays 24 months with every wage positive and finite and the allocator's band bases the 1901 ladder's
 *      (the lowest ungated ratio of the band), which differ from today's
 *
 * Every fixture CAUSES its condition: the 1900 city is founded through newGame(Founding.withUnfitted(true)) as the player's door is; the
 * floor set for 5 is below today's lowest setting, so a load that clamped it to today's bounds would show; the market's tight and even
 * months are posts and supply handed to advanceMonth(). Figures are asserted against the model's own constants and methods.
 */
public class WageLadderCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s%s%n", label, ok ? "OK" : "FAIL", ok || detail == null ? "" : "  " + detail);
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    /** A figure against the harness's own, to a relative tolerance. */
    static void near(String label, double actual, double expected, double rel) {
        boolean ok = Math.abs(actual - expected) <= rel * Math.max(Math.abs(expected), 1e-12);
        check(label, ok, String.format("%.12g != %.12g", actual, expected));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** A new city founded as the player's door founds it (newGame(founding)), on a scratch folder, quietly. */
    static Game found(String label, Founding founding) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> g.newGame(founding));
        return g;
    }

    /** One press of the clock, as the player's button makes it, quietly. */
    static void step(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    /** A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()). */
    static Game village(String label, Founding founding) {
        return FittingCheck.village(label, founding, false, false);
    }

    static final Founding DEFAULT = Founding.defaults();
    static final Founding THE_1900_WAY = Founding.defaults().withUnfitted(true);

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());
        if (FittingCheck.quiet == null) FittingCheck.quiet = quiet;
        if (EquipmentCheck.quiet == null) EquipmentCheck.quiet = quiet;

        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();
        sectionSix();

        out.println(fails == 0 ? "\nAll WageLadderCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* ============================ 1. the table ============================ */

    static void sectionOne() {
        out.println("\n--- 1. the table: one 1901 rung per PayTier rung, with its comparator and its source or its estimate flag ---");
        PayTier[] tiers = PayTier.values();
        check("one 1901 rung per PayTier rung", WageLadder.RUNGS_1901.size() == tiers.length, WageLadder.RUNGS_1901.size() + " rungs for " + tiers.length + " tiers");
        for (int i = 0; i < tiers.length && i < WageLadder.RUNGS_1901.size(); i++) {
            WageLadder.Rung rung = WageLadder.RUNGS_1901.get(i);
            check(tiers[i] + ": the rung is its tier's, in PayTier's order", rung.tier() == tiers[i]);
            check(tiers[i] + ": a comparator, and earnings that are a positive finite sum", !rung.comparator().isBlank()
                    && rung.annual1901() > 0 && Double.isFinite(rung.annual1901()), rung.comparator() + " " + rung.annual1901());
            check(tiers[i] + ": a source key, or flagged an estimate", !rung.source().isBlank() && (rung.estimate() || !rung.source().equals("est.")),
                    rung.source() + " estimate=" + rung.estimate());
            check(tiers[i] + ": a rung with no 1901 figure is flagged an estimate, never read as an observation",
                    !rung.comparator().startsWith("no 1901 figure") || rung.estimate());
            check(tiers[i] + ": the rung is the ladder's rung1901()", WageLadder.rung1901(tiers[i]) == rung);
        }
        boolean rises = true;
        for (int i = 1; i < tiers.length; i++) rises &= WageLadder.Y1901.monthlyWage(tiers[i]) > WageLadder.Y1901.monthlyWage(tiers[i - 1]);
        check("the 1901 ladder rises from rung to rung", rises);
        WageLadder.Rung bottom = WageLadder.rung1901(PayTier.UNSKILLED);
        check("the 1901 anchor is the unskilled rung's year over twelve, in thousands, through the class's one conversion",
                WageLadder.Y1901.monthlyWage(PayTier.UNSKILLED) == WageLadder.monthlyFromAnnual(bottom.annual1901())
                        && WageLadder.Y1901.anchor() == WageLadder.Y1901.monthlyWage(PayTier.UNSKILLED),
                WageLadder.Y1901.anchor() + " vs " + WageLadder.monthlyFromAnnual(bottom.annual1901()));
        near("...which is the year, times the dollar's worth in founding money, over the months and the thousand",
                WageLadder.Y1901.anchor(), bottom.annual1901() * WageLadder.FOUNDING_PER_CENSUS_DOLLAR / WageLadder.MONTHS_A_YEAR / WageLadder.THOUSAND, 1e-15);
        check("the founding picks the ladder: the default founding today's, the 1900 way 1901's, none today's",
                WageLadder.of(DEFAULT) == WageLadder.TODAY && WageLadder.of(THE_1900_WAY) == WageLadder.Y1901 && WageLadder.of(null) == WageLadder.TODAY
                        && !WageLadder.TODAY.isHistorical() && WageLadder.Y1901.isHistorical());
    }

    /* ============================ 2. today's ladder is PayTier's to the bit ============================ */

    static void sectionTwo() {
        out.println("\n--- 2. today's ladder is PayTier's to the bit, and a city founded the default way opens on it ---");
        boolean ratios = true, independent = true, rungs = true;
        for (JobType job : JobType.values()) {
            double floor = PayTier.UNSKILLED.getMonthlyWage();
            ratios &= Double.compare(WageLadder.TODAY.ratio(job), LabourMarket.ratioOf(job)) == 0;
            independent &= Double.compare(WageLadder.TODAY.ratio(job), floor > 0 ? PayTier.wageOf(job) / floor : 1) == 0;
        }
        for (PayTier tier : PayTier.values()) rungs &= Double.compare(WageLadder.TODAY.monthlyWage(tier), tier.getMonthlyWage()) == 0;
        check("every job's ratio on today's ladder is LabourMarket.ratioOf(), with no tolerance", ratios);
        check("...and the expression PayTier's figures gave before this class existed", independent);
        check("every rung on today's ladder is PayTier's own figure, with no tolerance", rungs);
        check("today's anchor is the default minimum wage", Double.compare(WageLadder.TODAY.anchor(), LabourMarket.DEFAULT_MINIMUM_WAGE) == 0);

        Game g = found("wage-ladder-default", DEFAULT);
        LabourMarket market = g.getLabourMarket();
        PopulationManager people = g.getPopulationManager();
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        RealEstate homes = g.getSectors().realEstate();
        check("a city founded the default way is on today's ladder, market and population manager", market.getLadder() == WageLadder.TODAY
                && people.getLadder() == WageLadder.TODAY && !g.foundedUnfitted());
        check("...its minimum wage the default and its dial's bounds the constants, to the bit", Double.compare(market.getMinimumWage(), LabourMarket.DEFAULT_MINIMUM_WAGE) == 0
                && Double.compare(market.getMinSettable(), LabourMarket.MIN_SETTABLE) == 0 && Double.compare(market.getMaxSettable(), LabourMarket.MAX_SETTABLE) == 0);
        boolean peopleWages = true, marketWages = true;
        double worst = 0;
        for (JobType job : JobType.values()) {
            peopleWages &= Double.compare(people.getWagesPerType()[job.ordinal()], PayTier.wageOf(job)) == 0;
            double wanted = PayTier.wageOf(job);
            worst = Math.max(worst, Math.abs(market.getWage(job) - wanted) / wanted);
            marketWages &= worst <= 1e-12;
        }
        check("...the population manager's wage per type PayTier's, to the bit, before its first month", peopleWages);
        check("...and every job's wage in the market PayTier's (the base is the floor times a ratio: to a part in 10^12)", marketWages, "worst " + worst);
        check("...its rents the rent for PayTier's unskilled wage", Double.compare(homes.getRentPrice(), RealEstate.rentFor(PayTier.UNSKILLED.getMonthlyWage())) == 0
                && Double.compare(homes.getStudioRentPrice(), RealEstate.rentFor(PayTier.UNSKILLED.getMonthlyWage())) == 0);
        double grant = tax.isOpen(PolicyDates.Lever.STUDENT_AID) ? TaxPolicy.DEFAULT_FIXED_GRANT : 0;
        check("...its grant the default fixed grant (held at zero while the student-aid lever is shut) and its pension's wage base PayTier's unskilled wage, to the bit",
                tax.getGrantBasis() == TaxPolicy.GrantBasis.FIXED && Double.compare(tax.getGrantAmount(), grant) == 0
                        && Double.compare(tax.foundingPensionWage(), PayTier.UNSKILLED.getMonthlyWage()) == 0,
                tax.getGrantAmount() + " vs " + grant + ", " + tax.foundingPensionWage());
        tax.reset();
        check("...and a policy reset puts the grant back at the default, as it always did", Double.compare(tax.getGrantAmount(), TaxPolicy.DEFAULT_FIXED_GRANT) == 0);
    }

    /* ============================ 3. a 1900 city starts on 1901's ladder ============================ */

    static void sectionThree() {
        out.println("\n--- 3. a city founded the 1900 way starts on 1901's ladder, at about a quarter of a default city's wage ---");
        Game old = found("wage-ladder-1900", THE_1900_WAY);
        Game plain = found("wage-ladder-plain", DEFAULT);
        WageLadder ladder = WageLadder.Y1901;
        LabourMarket market = old.getLabourMarket();
        PopulationManager people = old.getPopulationManager();
        TaxPolicy tax = old.getEconomyManager().getTaxPolicy();
        RealEstate homes = old.getSectors().realEstate();
        double anchor = ladder.anchor();

        check("the city is founded the 1900 way, and its market and population manager are on 1901's ladder", old.foundedUnfitted()
                && market.getLadder() == ladder && people.getLadder() == ladder);
        check("its minimum wage is the 1901 anchor, to the bit", Double.compare(market.getMinimumWage(), anchor) == 0, market.getMinimumWage() + " vs " + anchor);
        boolean marketWages = true, peopleWages = true;
        String firstWrong = null;
        for (JobType job : JobType.values()) {
            boolean m = Double.compare(market.getWage(job), anchor * ladder.ratio(job)) == 0;
            boolean p = Math.abs(people.getWagesPerType()[job.ordinal()] - anchor * ladder.ratio(job)) <= 1e-12 * anchor * ladder.ratio(job);
            if (!m && firstWrong == null) firstWrong = "market " + job + " " + market.getWage(job) + " vs " + anchor * ladder.ratio(job);
            if (!p && firstWrong == null) firstWrong = "people " + job + " " + people.getWagesPerType()[job.ordinal()] + " vs " + anchor * ladder.ratio(job);
            marketWages &= m;
            peopleWages &= p;
        }
        check("every job's wage in the market is the anchor times the 1901 ratio, before any month", marketWages, firstWrong);
        check("...and the population manager's wage per type is the same", peopleWages, firstWrong);
        check("the dial's bounds are the anchor's multiples (the share and the multiple the market states)",
                Double.compare(market.getMinSettable(), anchor * LabourMarket.MIN_SETTABLE_SHARE) == 0
                        && Double.compare(market.getMaxSettable(), anchor * LabourMarket.MAX_SETTABLE_MULTIPLE) == 0
                        && market.getMinimumWage() >= market.getMinSettable() && market.getMinimumWage() <= market.getMaxSettable());
        check("the rents are the rent for the 1901 anchor", Double.compare(homes.getRentPrice(), RealEstate.rentFor(anchor)) == 0
                && Double.compare(homes.getStudioRentPrice(), RealEstate.rentFor(anchor)) == 0, homes.getRentPrice() + " vs " + RealEstate.rentFor(anchor));
        double grant = tax.isOpen(PolicyDates.Lever.STUDENT_AID) ? TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE * anchor : 0;
        check("the grant is held at zero while the student-aid lever is shut, else the default share of the anchor; the pension's wage base is the anchor",
                tax.getGrantBasis() == TaxPolicy.GrantBasis.FIXED && Double.compare(tax.getGrantAmount(), grant) == 0
                        && Double.compare(tax.foundingPensionWage(), anchor) == 0,
                tax.getGrantAmount() + " vs " + grant + ", pension " + tax.foundingPensionWage());
        TaxPolicy open = new TaxPolicy();
        open.foundOn(WageLadder.Y1901);
        check("...and a policy with the lever open founded on 1901's has the default share of the anchor for its grant",
                open.isOpen(PolicyDates.Lever.STUDENT_AID) && Double.compare(open.getGrantAmount(), TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE * anchor) == 0
                        && Double.compare(open.foundingPensionWage(), anchor) == 0, open.getGrantAmount() + "");
        check("a pension is the replacement rate times the anchor", Double.compare(tax.pensionPerSenior(), tax.getPensionReplacement() * anchor) == 0,
                tax.pensionPerSenior() + " vs " + tax.getPensionReplacement() * anchor);
        tax.reset();
        check("...and a policy reset in the 1900 city puts the grant back on its own anchor, not today's", Double.compare(tax.getGrantAmount(), TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE * anchor) == 0);

        double share = market.getWage(JobType.NO_DIPLOMA) / plain.getLabourMarket().getWage(JobType.NO_DIPLOMA);
        check("its unskilled wage is about a quarter of a default city's (research-1900 5.2: est. .23): between .2 and .3", share > .2 && share < .3, String.valueOf(share));
        double engineer = market.getWage(JobType.UNIV_SCIENCE) / market.getWage(JobType.NO_DIPLOMA);
        double today = plain.getLabourMarket().getWage(JobType.UNIV_SCIENCE) / plain.getLabourMarket().getWage(JobType.NO_DIPLOMA);
        check("...with the census's ratios, not today's: a professional is paid a larger multiple of the labourer", engineer > today && Math.abs(engineer - today) > 1e-6,
                engineer + " against " + today);
    }

    /* ============================ 4. the market still moves a 1901 ladder ============================ */

    static void sectionFour() {
        out.println("\n--- 4. the market still moves a 1901 ladder: scarcity, the curve's damping, the cost of living, the dial ---");
        int bands = WageBand.values().length;
        double[] posts = new double[bands], few = new double[bands], even = new double[bands];
        for (int b = 0; b < bands; b++) { posts[b] = 1000; few[b] = 250; even[b] = 1000; }

        LabourMarket tight = new LabourMarket();
        tight.foundOn(WageLadder.Y1901);
        check("a bare market founded on 1901's ladder: its ratios, its floor at the anchor, every wage on its base", tight.getLadder() == WageLadder.Y1901
                && Double.compare(tight.getMinimumWage(), WageLadder.Y1901.anchor()) == 0);
        boolean onBase = true, ratiosHold = true;
        for (JobType job : JobType.values()) {
            onBase &= Double.compare(tight.getWage(job), tight.baseWage(job)) == 0;
            ratiosHold &= Double.compare(tight.ratio(job), WageLadder.Y1901.ratio(job)) == 0;
        }
        check("...every wage on its base, and the market's ratio the ladder's", onBase && ratiosHold);

        tight.advanceMonth(posts, few);
        boolean raised = true;
        String slack = null;
        for (JobType job : JobType.values()) {
            if (LabourMarket.isGated(job)) continue;
            boolean up = tight.getWage(job) > tight.baseWage(job);
            if (!up && slack == null) slack = job + " " + tight.getWage(job) + " vs base " + tight.baseWage(job);
            raised &= up;
        }
        check("posts above supply raise every ungated band's wage above its base", raised, slack);

        LabourMarket flat = new LabourMarket();
        flat.foundOn(WageLadder.Y1901);
        double[] was = new double[JobType.values().length];
        for (JobType job : JobType.values()) was[job.ordinal()] = flat.getWage(job);
        flat.advanceMonth(posts, even);
        boolean walks = true, aimsAtOne = true;
        double floor = flat.getMinimumWage();
        for (JobType job : JobType.values()) {
            double target = flat.targetWageAt(job, floor);
            double expected = was[job.ordinal()] + (target - was[job.ordinal()]) * LabourMarket.ADJUST_RATE;
            walks &= Math.abs(flat.getWage(job) - expected) <= 1e-12 * expected;
            aimsAtOne &= Math.abs(target - Math.max(flat.baseWage(job) * LabourMarket.multipleAt(1), flat.cashAt(floor))) <= 1e-12 * target;
        }
        check("posts equal to supply: a wage is aimed at its base times the curve's multiple at one", aimsAtOne);
        check("...and closes the damping rate of the way there from where it was", walks);

        double base = flat.baseWage(JobType.UNIV_SCIENCE), floorWas = flat.cashMinimumWage();
        flat.setCostOfLiving(1.25);
        near("the cost of living lifts the base: a quarter dearer, a quarter more", flat.baseWage(JobType.UNIV_SCIENCE), base * 1.25, 1e-12);
        check("...and the floor in today's money with it", flat.cashMinimumWage() > floorWas);
        flat.advanceMonth(posts, even);
        check("...and the wages follow it up, none under the floor in today's money", flat.getWage(JobType.NO_DIPLOMA) >= flat.cashMinimumWage() - 1e-12
                && flat.getWage(JobType.UNIV_SCIENCE) > was[JobType.UNIV_SCIENCE.ordinal()]);

        LabourMarket dial = new LabourMarket();
        dial.foundOn(WageLadder.Y1901);
        double below = WageLadder.Y1901.anchor() * .5;
        check("fixture: half the 1901 anchor is under today's lowest setting", below < LabourMarket.MIN_SETTABLE, below + " vs " + LabourMarket.MIN_SETTABLE);
        dial.setMinimumWage(below);
        check("the 1901 dial can be set to it: the bounds are the 1901 anchor's", Double.compare(dial.getMinimumWage(), below) == 0, dial.getMinimumWage() + "");
        dial.setMinimumWage(-5);
        check("...and is held at its lowest, the share of the anchor", Double.compare(dial.getMinimumWage(), dial.getMinSettable()) == 0
                && Double.compare(dial.getMinSettable(), WageLadder.Y1901.anchor() * LabourMarket.MIN_SETTABLE_SHARE) == 0);
        dial.setMinimumWage(1e9);
        check("...and at its highest, the multiple of the anchor", Double.compare(dial.getMinimumWage(), WageLadder.Y1901.anchor() * LabourMarket.MAX_SETTABLE_MULTIPLE) == 0);
        LabourMarket back = new LabourMarket();
        back.setLadder(WageLadder.Y1901);
        back.setLadder(WageLadder.TODAY);
        check("a market put back on today's ladder has the constants for its bounds", Double.compare(back.getMinSettable(), LabourMarket.MIN_SETTABLE) == 0
                && Double.compare(back.getMaxSettable(), LabourMarket.MAX_SETTABLE) == 0);
        back.seedConstants(10);
        check("...and a reform re-seeds the 1901 bounds over the unit like the constants", Double.compare(back.getMinSettable(), LabourMarket.MIN_SETTABLE / 10) == 0);
        back.setLadder(WageLadder.Y1901);
        back.seedConstants(10);
        check("...and a market on 1901's re-seeds its own, not today's", Double.compare(back.getMinSettable(), WageLadder.Y1901.anchor() * LabourMarket.MIN_SETTABLE_SHARE / 10) == 0);
    }

    /* ============================ 5. saved and loaded ============================ */

    static void sectionFive() {
        out.println("\n--- 5. saved and loaded: the ladder, the floor, the wages, the bounds and the rents come back; a default city loads today's ---");
        Game g = village("wage-ladder-saved", THE_1900_WAY);
        double anchor = WageLadder.Y1901.anchor();
        double lowFloor = anchor * .5;
        g.getLabourMarket().setMinimumWage(lowFloor);
        step(g, 6);
        LabourMarket live = g.getLabourMarket();
        check("fixture: a 1900 village at month 6 with a floor under today's lowest setting", g.foundedUnfitted() && live.getMinimumWage() < LabourMarket.MIN_SETTABLE
                && Double.compare(live.getMinimumWage(), lowFloor) == 0, String.valueOf(live.getMinimumWage()));
        GameFiles.Result wrote = EquipmentCheck.save(g, "wage ladder 1900");
        check("fixture: the city saved to its scratch slot", wrote.ok, wrote.error);

        Game fresh = new Game(g.getGameFiles());
        quietly(() -> fresh.loadGameSave(1));
        check("the 1900 city loaded into a fresh Game keeps its ladder, on the market and the population manager",
                fresh.getLoadFailure() == null && fresh.foundedUnfitted() && fresh.getLabourMarket().getLadder() == WageLadder.Y1901
                        && fresh.getPopulationManager().getLadder() == WageLadder.Y1901, fresh.getLoadFailure());
        LabourMarket loaded = fresh.getLabourMarket();
        check("...its minimum wage, to the bit (the floor under today's lowest setting is not clamped up to it)",
                Double.compare(loaded.getMinimumWage(), live.getMinimumWage()) == 0, loaded.getMinimumWage() + " vs " + live.getMinimumWage());
        boolean wages = true, people = true;
        for (JobType job : JobType.values()) {
            wages &= Double.compare(loaded.getWage(job), live.getWage(job)) == 0;
            people &= Double.compare(fresh.getPopulationManager().getWagesPerType()[job.ordinal()], g.getPopulationManager().getWagesPerType()[job.ordinal()]) == 0;
        }
        check("...every job's wage, to the bit, in the market and the population manager", wages && people);
        check("...and the dial's bounds, the 1901 anchor's multiples", Double.compare(loaded.getMinSettable(), live.getMinSettable()) == 0
                && Double.compare(loaded.getMaxSettable(), live.getMaxSettable()) == 0
                && Double.compare(loaded.getMinSettable(), anchor * LabourMarket.MIN_SETTABLE_SHARE) == 0);
        check("...and the rents the live city was paying", Double.compare(fresh.getSectors().realEstate().getRentPrice(), g.getSectors().realEstate().getRentPrice()) == 0
                && Double.compare(fresh.getSectors().realEstate().getStudioRentPrice(), g.getSectors().realEstate().getStudioRentPrice()) == 0);
        check("...and the pension struck on the anchor, as the live city strikes it", Math.abs(fresh.getEconomyManager().getTaxPolicy().pensionPerSenior()
                - g.getEconomyManager().getTaxPolicy().pensionPerSenior()) <= 1e-9 * g.getEconomyManager().getTaxPolicy().pensionPerSenior());

        // The 1900 Game loads the same save again: nothing of its own ladder leaks into, or out of, the reload.
        quietly(() -> g.loadGameSave(1));
        check("the Game that played the 1900 city loads it again with the same floor and ladder", g.getLoadFailure() == null
                && g.getLabourMarket().getLadder() == WageLadder.Y1901 && Double.compare(g.getLabourMarket().getMinimumWage(), lowFloor) == 0, g.getLoadFailure());

        Game plain = found("wage-ladder-saved-plain", DEFAULT);
        GameFiles.Result wrotePlain = EquipmentCheck.save(plain, "wage ladder default");
        check("fixture: a default city saved to its scratch slot", wrotePlain.ok, wrotePlain.error);
        Game plainFresh = new Game(plain.getGameFiles());
        quietly(() -> plainFresh.loadGameSave(1));
        check("a default city loaded into a fresh Game is on today's ladder, its floor and bounds the constants", plainFresh.getLoadFailure() == null && !plainFresh.foundedUnfitted()
                && plainFresh.getLabourMarket().getLadder() == WageLadder.TODAY && plainFresh.getPopulationManager().getLadder() == WageLadder.TODAY
                && Double.compare(plainFresh.getLabourMarket().getMinSettable(), LabourMarket.MIN_SETTABLE) == 0
                && Double.compare(plainFresh.getLabourMarket().getMaxSettable(), LabourMarket.MAX_SETTABLE) == 0
                && Double.compare(plainFresh.getLabourMarket().getMinimumWage(), LabourMarket.DEFAULT_MINIMUM_WAGE) == 0, plainFresh.getLoadFailure());
        // A default save into the Game that played the 1900 city (its saved slot is the default city's now).
        Game mixed = new Game(plain.getGameFiles());
        quietly(() -> mixed.newGame(THE_1900_WAY));
        check("fixture: a Game that has founded a 1900 city is on 1901's ladder", mixed.getLabourMarket().getLadder() == WageLadder.Y1901
                && mixed.getPopulationManager().getLadder() == WageLadder.Y1901);
        quietly(() -> mixed.loadGameSave(1));
        check("...and loading a default city into it puts it back on today's: the ladder is the save's founding's, not the Game's",
                mixed.getLoadFailure() == null && mixed.getLabourMarket().getLadder() == WageLadder.TODAY && mixed.getPopulationManager().getLadder() == WageLadder.TODAY
                        && Double.compare(mixed.getLabourMarket().getMinSettable(), LabourMarket.MIN_SETTABLE) == 0
                        && Double.compare(mixed.getEconomyManager().getTaxPolicy().foundingPensionWage(), PayTier.UNSKILLED.getMonthlyWage()) == 0, mixed.getLoadFailure());
    }

    /* ============================ 6. played ============================ */

    static void sectionSix() {
        out.println("\n--- 6. played: a 1900 village for 24 months, every wage positive and finite, the allocator on 1901's bases ---");
        Game g = village("wage-ladder-played", THE_1900_WAY);
        Game plain = village("wage-ladder-played-plain", DEFAULT);
        PopulationManager people = g.getPopulationManager();

        boolean basesOk = true, differ = false, plainBases = true;
        StringBuilder wrong = new StringBuilder();
        for (WageBand band : WageBand.values()) {
            double lowest = Double.MAX_VALUE, lowestToday = Double.MAX_VALUE;
            for (JobType job : JobType.values()) {
                if (WageBand.of(job) != band || PopulationManager.isGated(job)) continue;
                lowest = Math.min(lowest, WageLadder.Y1901.ratio(job));
                lowestToday = Math.min(lowestToday, WageLadder.TODAY.ratio(job));
            }
            if (lowest == Double.MAX_VALUE) { lowest = 1; lowestToday = 1; }
            if (Double.compare(people.getBandBase(band), lowest) != 0) { basesOk = false; wrong.append(band).append(' ').append(people.getBandBase(band)).append(" vs ").append(lowest).append("; "); }
            if (Double.compare(plain.getPopulationManager().getBandBase(band), lowestToday) != 0) plainBases = false;
            if (Math.abs(lowest - lowestToday) > 1e-9) differ = true;
        }
        check("the allocator's base for each band is the lowest ungated ratio on the 1901 ladder", basesOk, wrong.toString());
        check("...which differs from today's in some band (the fixture causes the condition), a default village's being today's", differ && plainBases);

        boolean finite = true, positive = true, floorHolds = true;
        String bad = null;
        for (int m = 1; m <= 24; m++) {
            step(g, 1);
            for (JobType job : JobType.values()) {
                double w = g.getLabourMarket().getWage(job), p = people.getWagesPerType()[job.ordinal()];
                if (!(w > 0) || !(p > 0)) { positive = false; if (bad == null) bad = "month " + m + " " + job + " " + w + " / " + p; }
                if (!Double.isFinite(w) || !Double.isFinite(p)) { finite = false; if (bad == null) bad = "month " + m + " " + job + " " + w + " / " + p; }
            }
            double rent = g.getSectors().realEstate().getRentPrice(), floor = g.getLabourMarket().getMinimumWage();
            if (!(rent > 0) || !Double.isFinite(rent) || !(floor > 0) || !Double.isFinite(floor)) { floorHolds = false; if (bad == null) bad = "month " + m + " rent " + rent + " floor " + floor; }
        }
        check("24 months played: every job's wage, in the market and the population manager, positive", positive, bad);
        check("...and finite", finite, bad);
        check("...with the rent and the floor positive and finite", floorHolds, bad);
        check("...the city still on 1901's ladder, its allocator's bases unchanged", g.getLabourMarket().getLadder() == WageLadder.Y1901 && people.getLadder() == WageLadder.Y1901
                && Double.compare(people.getBandBase(WageBand.DIPLOMA), WageLadder.Y1901.ratio(JobType.DIPLOMA)) == 0);
        double share = g.getLabourMarket().getWage(JobType.NO_DIPLOMA) / WageLadder.TODAY.monthlyWage(PayTier.UNSKILLED);
        check("...and its unskilled wage still under half of today's after two years' scarcity", share > 0 && share < .5, String.valueOf(share));
    }
}
