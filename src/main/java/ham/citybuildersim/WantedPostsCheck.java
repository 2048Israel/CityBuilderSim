package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * The posts the staffing gate refused bid their band's wage up (LD1; WantedPosts). Not part of the game.
 *
 * WHAT THIS HAS TO PROVE. Jerus, 2026-10-31: "my city was stuck... i had available mines but the mining industry wasnt
 * building cause it wouldnt have enough workers... and new people didnt want to come cause the wage was trash, perhaps a
 * fix would be that if the reason is not enough workers and the business can... then it raises its wage?" A building
 * is ordered only if Sector.MIN_STAFFABLE_TO_ORDER of its posts can be filled from spare workers, and a wage rises
 * only on posts that exist, so a refused building never moved its band's wage. So:
 *
 *   1. THE GATE'S HOLD CARRIES ITS BUILDING: who asked, which building, how many, its posts and the spare workers per
 *      band; and the planner's Decision carries the hold, with its reason in the hold's own words.
 *   2. ONLY THE BANDS IT IS SHORT IN ask for posts, and a building that would not carry its interest asks for none.
 *   3. THE MARKET PRICES IT: the band's multiple is struck on the posts that exist plus the refused ones, and the wage
 *      walks ADJUST_RATE of the way, while the gate's own test still reads only the posts that exist.
 *   4. NEVER MORE THAN THE BAND HAS WORKERS: the refused posts cannot double a band's wage.
 *   5. ONLY AS FAR AS THE BUSINESS CAN PAY: the best-paying building that wants the posts is how far they count.
 *   6. THE BUILDERS' LAID-OFF CREWS ARE NOT A SHORTAGE: a hold the gate would not have put on but for them asks nothing.
 *   7. IT DECAYS: no hold, no latent posts, and the wage walks back.
 *   8. IT BRINGS THE BAND'S MIGRANTS: the premium it makes is what Migration reaches for.
 *   9. THE SAVE: the latent posts are priced next month, so a reloaded city prices it as the live one does.
 *  10. THE WIRING: in a city played ten years every hold a planner returns is noted, sorted and priced within the rules above.
 *
 * Every fixture CAUSES its condition: a founded city given the hands and the posts of a stated shape, and every
 * expected figure comes from the model's own calls (LabourMarket.advanceMonth() on a twin of the market, tightnessAt(),
 * the template's posts), never a number written here.
 */
public class WantedPostsCheck {

    static int fails = 0;
    static PrintStream out, quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-96s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static BuildingsTemplate template(Game g, String name) {
        return g.getBuildingManager().getTemplateByName(name);
    }

    static GameFiles filesFor(Path root, String name) {
        return new GameFiles(root.resolve(name), root.resolve("no-legacy"));
    }

    static Game city(Path root, String name) {
        return city(filesFor(root, name));
    }

    /** A fresh city, currency and world pinned, land by fiat (InvestCheck's). */
    static Game city(GameFiles files) {
        Game g = new Game(files);
        System.setOut(quiet);
        try {
            g.run();
            g.getForeignAccounts().pinRate(1.0);
            g.getWorldEconomy().pin();
            g.getLandManager().setOwnedSqFt(50_000_000);
        } finally {
            System.setOut(out);
        }
        return g;
    }

    /** One month, quietly. */
    static void month(Game g) {
        System.setOut(quiet);
        try { g.simulateMonths(1); } finally { System.setOut(out); }
    }

    static final String MACHINE_WORKS = "Machine Works";
    // The hands the fixture's city has: its workforce, and how many of them have a diploma, a college degree and a
    // university degree. The university posts that exist are as many as the hands and the college posts all but the
    // few the Machine Works can do without (collegePosts()), so the gate sees almost no spare graduate.
    static final long WORKFORCE = 5_000;
    static final double DIPLOMA_HEADS = 2_000, COLLEGE_HEADS = 200, UNIVERSITY_HEADS = 20;

    /**
     * The college posts the fixture's city has: its graduates less the most spare ones the gate could still refuse the
     * building with, found from the gate's own dial (a building passes at MIN_STAFFABLE_TO_ORDER of its posts, the
     * unskilled and diploma ones filled), so the college band stands just under full and the building just under the
     * share it needs.
     */
    static long collegePosts(BuildingsTemplate t) {
        double total = 0;
        for (WageBand band : WageBand.values()) total += postsIn(t, band);
        double lower = postsIn(t, WageBand.NONE) + postsIn(t, WageBand.DIPLOMA);
        long slack = (long) Math.ceil(Sector.MIN_STAFFABLE_TO_ORDER * total - lower) - 1;
        return (long) COLLEGE_HEADS - Math.max(0, slack);
    }

    /** The fixture's hands for a city: see shortCollege(). */
    static void shortHands(Game g) {
        hands(g, WORKFORCE, new double[] { 0, DIPLOMA_HEADS, COLLEGE_HEADS, UNIVERSITY_HEADS },
                collegePosts(template(g, MACHINE_WORKS)), (long) UNIVERSITY_HEADS);
    }

    /** The posts a building has in a wage band. */
    static double postsIn(BuildingsTemplate t, WageBand band) {
        double n = 0;
        for (JobType job : JobType.values()) if (WageBand.of(job) == band) n += t.getJobs(job);
        return n;
    }

    /** Puts the city's hands and the posts that exist in a stated shape (they stand until a month is played). */
    static void hands(Game g, long workforce, double[] heads, long collegePosts, long universityPosts) {
        PopulationManager pm = g.getPopulationManager();
        pm.restoreWorkforce(workforce);
        pm.restoreSkilledHeads(heads);
        long[] jobs = new long[JobType.values().length];
        jobs[JobType.COLLEGE_ENGINEERING.ordinal()] = collegePosts;
        jobs[JobType.UNIV_SCIENCE.ordinal()] = universityPosts;
        pm.updateJobs(jobs);
    }

    /**
     * A city whose Machine Works the gate refuses for want of college and university hands: the workforce and the
     * diploma holders it needs, every college and university post taken by a graduate (so none is spare), and the
     * labour market at its base so no band starts under or over the going rate.
     */
    static Game shortCollege(Path root, String name) {
        return shortCollege(city(root, name));
    }

    static Game shortCollege(Game g) {
        month(g);   // the wage schedule the sectors pay is the market's, not the founding's zeros
        g.getLabourMarket().resetToBase();
        g.getPopulationManager().takeWagesFrom(g.getLabourMarket());
        shortHands(g);
        return g;
    }

    /** shortCollege() with the Machine Works' refusal noted and settled, as the month's planning does. */
    static Game heldCity(Path root, String name) {
        return held(shortCollege(root, name));
    }

    static Game held(Game g) {
        g.getWantedPosts().beginMonth();
        g.noteStaffingHold(g.getSectors().manufacturing().staffing(template(g, MACHINE_WORKS)));
        g.settleWantedPosts();
        return g;
    }

    /** A copy of a market as it stands, to ask what a month would do to it without doing it. */
    static LabourMarket twin(LabourMarket lm) {
        LabourMarket t = new LabourMarket();
        t.restore(lm.state());
        t.setCostOfLiving(lm.getCostOfLiving());
        return t;
    }

    /** What the market does with a month against these posts, on a twin of it. */
    static LabourMarket monthOn(Game g, double[] posts) {
        PopulationManager pm = g.getPopulationManager();
        LabourMarket t = twin(g.getLabourMarket());
        t.advanceMonth(posts, pm.supplyByBand(posts), pm.getJobs(), pm.getLicensedHeads());
        return t;
    }

    static double[] plus(double[] a, double[] b) {
        double[] sum = a.clone();
        for (int i = 0; i < sum.length && i < b.length; i++) sum[i] += b[i];
        return sum;
    }

    static final int COLLEGE = WageBand.COLLEGE.ordinal(), UNIVERSITY = WageBand.UNIVERSITY.ordinal();

    /** The margin Game.noteStaffingHold() works out for a building: what it earns over the interest hurdle at prime and its record. */
    static double marginOf(Game g, Sector owner, BuildingsTemplate t) {
        BusinessInvestment bi = g.getBusinessInvestment();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        return bi.estimatedMonthlyProfit(owner.key(), t)
                - bi.interestHurdle(bi.getCostOf(t, 1), credit.getPrimeRate() + credit.getRecordSurcharge(owner.key()));
    }

    /** The prime rate at which the interest hurdle on a building's cost is {@code hurdle}, found with the model's own hurdle. */
    static double primeFor(Game g, BuildingsTemplate t, double hurdle) {
        BusinessInvestment bi = g.getBusinessInvestment();
        double cost = bi.getCostOf(t, 1), lo = 0, hi = 1;
        while (bi.interestHurdle(cost, hi) < hurdle) hi *= 2;
        for (int i = 0; i < 80; i++) {
            double mid = (lo + hi) / 2;
            if (bi.interestHurdle(cost, mid) < hurdle) lo = mid; else hi = mid;
        }
        return (lo + hi) / 2;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("wantedposts");

        /* ============ 1. the gate's hold carries its building ============ */
        out.println("--- the gate's hold carries its building ---");

        Game g1 = shortCollege(root, "hold");
        Sector man = g1.getSectors().manufacturing();
        BuildingsTemplate mw = template(g1, MACHINE_WORKS);
        Sector.Staffing held = man.staffing(mw);
        out.printf("   a Machine Works with no spare college or university hand: %.1f%% staffable, wanted %s, spare %s%n",
                held.share * 100, java.util.Arrays.toString(held.wanted()), java.util.Arrays.toString(held.spare()));
        assertTrue("fixture: the gate refuses it, under MIN_STAFFABLE_TO_ORDER, with no band nobody can fill",
                !held.passes() && held.share < Sector.MIN_STAFFABLE_TO_ORDER && held.unfillable == null);
        boolean onlyCollegeAndUniversity = true;
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            boolean short_ = held.wanted()[b] > Math.max(0, held.spare()[b]);
            onlyCollegeAndUniversity &= short_ == (b == COLLEGE || b == UNIVERSITY);
        }
        assertTrue("fixture: it is short in the college and university bands and in no other", onlyCollegeAndUniversity);
        assertTrue("the hold names the sector that asked", held.owner() == man);
        assertTrue("...the building", held.template() == mw);
        assertTrue("...and how many of it", held.count() == 1);
        boolean postsMatch = true;
        for (WageBand band : WageBand.values()) postsMatch &= held.wanted()[band.ordinal()] == postsIn(mw, band);
        assertTrue("...its posts are the building's, band by band", postsMatch);
        PopulationManager pm1 = g1.getPopulationManager();
        // ...the posts the gate counts: those that exist and the builders' laid-off crews', as Sector.staffing() adds them
        double[] posts1 = pm1.staffablePostsByBand();
        for (JobType job : JobType.values()) posts1[WageBand.of(job).ordinal()] += g1.getBuildingManager().getPostsWithheld(job);
        double[] supply1 = pm1.supplyByBand(posts1);
        boolean spareMatches = true;
        for (int b = 0; b < supply1.length; b++) spareMatches &= held.spare()[b] == supply1[b] - posts1[b];
        assertTrue("...and its spare workers are each band's supply less its posts", spareMatches);
        held.wanted()[COLLEGE] = -1;
        assertTrue("...handed out as copies: changing one does not change the hold", held.wanted()[COLLEGE] == postsIn(mw, WageBand.COLLEGE));
        assertTrue("an order of three is asked of as three buildings' posts",
                man.staffing(mw, 3).count() == 3 && man.staffing(mw, 3).wanted()[COLLEGE] == 3 * postsIn(mw, WageBand.COLLEGE));

        // ...the threshold itself from both sides: one spare graduate more than the fixture has, and the share reaches the dial.
        hands(g1, WORKFORCE, new double[] { 0, DIPLOMA_HEADS, COLLEGE_HEADS, UNIVERSITY_HEADS }, collegePosts(mw) - 1, (long) UNIVERSITY_HEADS);
        assertTrue("with one more spare graduate than that the same building passes: the fixture stands just under the gate's dial",
                man.staffing(mw).passes());
        // ...and from the other side: enough spare graduates, and the same building passes.
        hands(g1, WORKFORCE, new double[] { 0, DIPLOMA_HEADS, COLLEGE_HEADS + postsIn(mw, WageBand.COLLEGE),
                UNIVERSITY_HEADS + postsIn(mw, WageBand.UNIVERSITY) }, collegePosts(mw), (long) UNIVERSITY_HEADS);
        Sector.Staffing enough = man.staffing(mw);
        assertTrue("with the building's college and university posts spare it passes the gate", enough.passes());
        BusinessInvestment.Decision open = man.plan(g1.getBusinessInvestment(), g1);
        assertTrue("...and a planner that is not held carries no hold", open.heldForStaff == null);

        // The planner's Decision carries the hold. Every works of the sector is refused here (a city of unskilled hands
        // alone), and the planner names the one the city could staff most of.
        hands(g1, WORKFORCE, new double[] { 0, 0, 0, 0 }, 0, 0);
        Sector.Staffing mostStaffable = null;
        BuildingsTemplate mostStaffableWorks = null;
        boolean everyWorksRefused = true;
        for (BuildingsTemplate t : g1.getBuildingManager().getTemplatesBySector(man.key())) {
            Sector.Staffing s = man.staffing(t);
            everyWorksRefused &= !s.passes();
            if (mostStaffable == null || s.share > mostStaffable.share) { mostStaffable = s; mostStaffableWorks = t; }
        }
        assertTrue("fixture: with unskilled hands alone the gate refuses every works the sector has", everyWorksRefused);
        BusinessInvestment.Decision refused = man.plan(g1.getBusinessInvestment(), g1);
        out.printf("   the planner's answer: \"%s\"%n", refused.reason);
        assertTrue("the planner holds, building nothing", !refused.build && refused.template == null);
        assertTrue("...with the hold on the Decision, for the works the city could staff most of",
                refused.heldForStaff != null && refused.heldForStaff.template() == mostStaffableWorks
                        && refused.heldForStaff.owner() == man && refused.heldForStaff.share == mostStaffable.share);
        assertTrue("...and its reason is the hold's own words, as before",
                mostStaffableWorks != null && refused.reason.equals(refused.heldForStaff.why(mostStaffableWorks.getName())));
        assertTrue("a decision that is not a staffing hold carries none: refused",
                BusinessInvestment.Decision.no("x", "y").heldForStaff == null);
        assertTrue("...refused for ground", BusinessInvestment.Decision.noLand("x", "y").heldForStaff == null);
        assertTrue("...an order", new BusinessInvestment.Decision("x", mw, 1, "y", true).heldForStaff == null);
        assertTrue("unstaffed() is a hold: nothing built, no ground blamed, the hold carried",
                BusinessInvestment.Decision.unstaffed("x", held, "y").heldForStaff == held
                        && !BusinessInvestment.Decision.unstaffed("x", held, "y").build
                        && !BusinessInvestment.Decision.unstaffed("x", held, "y").landBlocked);

        /* ============ 2. only the bands it is short in ============ */
        out.println("\n--- only the bands it is short in ---");

        WantedPosts.Ask ask = WantedPosts.askOf(man.key(), MACHINE_WORKS, held.wanted(), held.spare(), 100, 400);
        assertTrue("a building that carries its interest asks", ask != null);
        boolean bandsRight = ask != null;
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            double want = b == COLLEGE || b == UNIVERSITY ? postsIn(mw, band) : 0;
            bandsRight &= ask != null && ask.posts()[b] == want;
        }
        assertTrue("...for the posts it has in the bands it is short in, and none in the others", bandsRight);
        close("...able to pay up to its margin over the bill of the posts it lacks, on top of today's wage",
                ask == null ? 0 : ask.payFactor(), 1 + 100.0 / 400.0, 1e-12);
        assertTrue("...naming the sector and the building", ask != null && ask.sector().equals(man.key()) && ask.building().equals(MACHINE_WORKS));
        assertTrue("one that would not carry its interest at today's wage (margin zero) asks nothing",
                WantedPosts.askOf(man.key(), MACHINE_WORKS, held.wanted(), held.spare(), 0, 400) == null);
        assertTrue("...nor one under it", WantedPosts.askOf(man.key(), MACHINE_WORKS, held.wanted(), held.spare(), -5, 400) == null);
        assertTrue("short bills nothing to raise (the posts it lacks cost nothing), the wage is no limit",
                WantedPosts.askOf(man.key(), MACHINE_WORKS, held.wanted(), held.spare(), 100, 0).payFactor() == Double.POSITIVE_INFINITY);
        WantedPosts.Ask overdrawn = WantedPosts.askOf("s", "b", new double[] { 10, 10, 10, 10 }, new double[] { -5, 10, 9, 0 }, 1, 1);
        assertTrue("a band overdrawn by posts that exist (negative spare) counts as short, one with exactly enough does not",
                overdrawn.posts()[0] == 10 && overdrawn.posts()[1] == 0 && overdrawn.posts()[2] == 10 && overdrawn.posts()[3] == 10);

        /* ============ 3. the market prices it ============ */
        out.println("\n--- the market prices it ---");

        Game g3 = heldCity(root, "market");
        Sector man3 = g3.getSectors().manufacturing();
        BuildingsTemplate mw3 = template(g3, MACHINE_WORKS);
        WantedPosts wanted3 = g3.getWantedPosts();
        PopulationManager pm3 = g3.getPopulationManager();
        LabourMarket lm3 = g3.getLabourMarket();
        assertTrue("fixture: the Machine Works carries its interest at prime, so it asks", marginOf(g3, man3, mw3) > 0);
        assertTrue("the hold was counted as an ask, not a crews' hold or an unpaid one",
                wanted3.getHolds() == 1 && wanted3.getAsked() == 1 && wanted3.getCrewsHolds() == 0 && wanted3.getUnpaidHolds() == 0);
        double[] latent3 = wanted3.latent();
        out.printf("   the posts the college and university bands are priced on beyond those that exist: %.2f and %.2f%n",
                latent3[COLLEGE], latent3[UNIVERSITY]);
        assertTrue("the refused posts are in the two bands it is short in and no other",
                latent3[COLLEGE] > 0 && latent3[UNIVERSITY] > 0 && latent3[0] == 0 && latent3[1] == 0 && wanted3.any());
        assertTrue("...no more than it asked for", latent3[COLLEGE] <= postsIn(mw3, WageBand.COLLEGE) && latent3[UNIVERSITY] <= postsIn(mw3, WageBand.UNIVERSITY));
        double[] existing = pm3.staffablePostsByBand();
        double[] seen = plus(existing, latent3);
        LabourMarket with = monthOn(g3, seen), without = monthOn(g3, existing);
        double wasPremium = lm3.bandPremium(WageBand.COLLEGE);
        double gateShare = man3.staffing(mw3).share;
        g3.repriceLabour();
        close("the college band's multiple is the market's own on the posts that exist plus the refused ones",
                lm3.getBandMultiple(WageBand.COLLEGE), with.getBandMultiple(WageBand.COLLEGE), 1e-12);
        close("...which is the curve's at the tightness it was struck on",
                lm3.getBandMultiple(WageBand.COLLEGE), LabourMarket.multipleAt(lm3.getTightness(WageBand.COLLEGE)), 1e-12);
        assertTrue("...above what the same month gives with none",
                lm3.getBandMultiple(WageBand.COLLEGE) > without.getBandMultiple(WageBand.COLLEGE));
        close("...and the university band's too", lm3.getBandMultiple(WageBand.UNIVERSITY), with.getBandMultiple(WageBand.UNIVERSITY), 1e-12);
        close("the wage moved ADJUST_RATE of the way toward the new target",
                lm3.bandPremium(WageBand.COLLEGE), wasPremium + (lm3.getBandMultiple(WageBand.COLLEGE) - wasPremium) * LabourMarket.ADJUST_RATE, 1e-12);
        assertTrue("...up from where it stood", lm3.bandPremium(WageBand.COLLEGE) > wasPremium);
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == WageBand.COLLEGE || WageBand.of(job) == WageBand.UNIVERSITY) {
                close("...every " + job + " wage is the twin market's", lm3.getWage(job), with.getWage(job), 1e-12);
            }
        }
        assertTrue("the unskilled and diploma bands are priced as they were",
                lm3.getBandMultiple(WageBand.NONE) == with.getBandMultiple(WageBand.NONE)
                        && lm3.getBandMultiple(WageBand.NONE) == without.getBandMultiple(WageBand.NONE)
                        && lm3.getBandMultiple(WageBand.DIPLOMA) == without.getBandMultiple(WageBand.DIPLOMA));
        double[] after = pm3.staffablePostsByBand();
        boolean postsUnmoved = true;
        for (int b = 0; b < after.length; b++) postsUnmoved &= after[b] == existing[b];
        assertTrue("the posts that exist are not moved by pricing the refused ones", postsUnmoved);
        close("the gate's own test still reads only the posts that exist: the same share as before", man3.staffing(mw3).share, gateShare, 0);

        /* ============ 4. never more than the band has workers ============ */
        out.println("\n--- never more than the band has workers ---");

        double supply = 1_000;
        boolean cappedEverywhere = true, underTwiceEverywhere = true;
        double floorTightness = LabourMarket.tightnessAt(LabourMarket.MIN_MULTIPLE);
        double[] tightnesses = { 0, floorTightness / 2, floorTightness, 1, 2, LabourMarket.tightnessAt(LabourMarket.MAX_MULTIPLE) / 2 };
        for (double tightness : tightnesses) {
            WantedPosts wp = new WantedPosts();
            wp.beginMonth();
            wp.ask(new WantedPosts.Ask("s", "b", new double[] { 50 * supply, 50 * supply, 50 * supply, 50 * supply }, Double.POSITIVE_INFINITY));
            double[] posts = { tightness * supply, tightness * supply, tightness * supply, tightness * supply };
            wp.settle(posts, new double[] { supply, supply, supply, supply }, new double[] { 1, 1, 1, 1 });
            cappedEverywhere &= wp.getLatent(WageBand.COLLEGE) == WantedPosts.MOST_OF_SUPPLY * supply;
            double without_ = LabourMarket.multipleAt(posts[COLLEGE] / supply);
            double with_ = LabourMarket.multipleAt((posts[COLLEGE] + wp.getLatent(WageBand.COLLEGE)) / supply);
            underTwiceEverywhere &= with_ < 2 * without_;
            out.printf("   posts at %.2f of the band's workers: multiple %.3f, %.3f with asks far past its workers (x%.2f)%n",
                    tightness, without_, with_, with_ / without_);
        }
        assertTrue("asks far larger than the band count as MOST_OF_SUPPLY of its workers, whatever its tightness", cappedEverywhere);
        assertTrue("...so the band's multiple with them is under twice its multiple without, from the floor's tightness up", underTwiceEverywhere);
        WantedPosts tiny = new WantedPosts();
        tiny.beginMonth();
        tiny.ask(new WantedPosts.Ask("s", "b", new double[] { 5, 5, 5, 5 }, Double.POSITIVE_INFINITY));
        tiny.settle(new double[] { 0, 0, 0, 0 }, new double[] { 0, 0, 0, 0 }, new double[] { 1, 1, 1, 1 });
        assertTrue("a band with no workers is counted as having one, as the market counts it: no more than that",
                tiny.getLatent(WageBand.NONE) == WantedPosts.MOST_OF_SUPPLY * 1);

        /* ============ 5. only as far as the business can pay ============ */
        out.println("\n--- only as far as the business can pay ---");

        double premium5 = 1.1;
        double[] heldPosts = { 0, 0, 800, 0 };
        double[] bandWorkers = { supply, supply, supply, supply };
        double[] bandPremiums = { premium5, premium5, premium5, premium5 };
        double[] existingPosts = { supply, supply, supply, supply };
        double smallFactor = 1.05;
        WantedPosts few = new WantedPosts();
        few.beginMonth();
        few.ask(new WantedPosts.Ask("s", "b", heldPosts, smallFactor));
        few.settle(existingPosts, bandWorkers, bandPremiums);
        double expectedRoom = LabourMarket.tightnessAt(premium5 * smallFactor) * supply - supply;
        close("an ask that could pay little stops where the tightness at its wage puts it",
                few.getLatent(WageBand.COLLEGE), expectedRoom, 1e-12);
        assertTrue("...short of what it asked for and of the band's workers",
                few.getLatent(WageBand.COLLEGE) < 800 && few.getLatent(WageBand.COLLEGE) < WantedPosts.MOST_OF_SUPPLY * supply);
        WantedPosts more = new WantedPosts();
        more.beginMonth();
        more.ask(new WantedPosts.Ask("s", "b", heldPosts, smallFactor + .10));
        more.settle(existingPosts, bandWorkers, bandPremiums);
        assertTrue("one that could pay more counts for more", more.getLatent(WageBand.COLLEGE) > few.getLatent(WageBand.COLLEGE));
        WantedPosts both = new WantedPosts();
        both.beginMonth();
        both.ask(new WantedPosts.Ask("s", "poor", new double[] { 0, 0, 100, 0 }, smallFactor));
        both.ask(new WantedPosts.Ask("s", "rich", new double[] { 0, 0, 100, 0 }, smallFactor + .10));
        both.settle(existingPosts, bandWorkers, bandPremiums);
        double richRoom = LabourMarket.tightnessAt(premium5 * (smallFactor + .10)) * supply - supply;
        close("two asks together count as far as the best-paying of them could pay (200 asked)",
                both.getLatent(WageBand.COLLEGE), Math.min(200, richRoom), 1e-12);
        WantedPosts plenty = new WantedPosts();
        plenty.beginMonth();
        plenty.ask(new WantedPosts.Ask("s", "b", new double[] { 0, 0, 100, 0 }, smallFactor + 5));
        plenty.settle(existingPosts, bandWorkers, bandPremiums);
        close("...and one that could pay anything counts for all it asked (100)", plenty.getLatent(WageBand.COLLEGE), 100, 1e-12);
        WantedPosts overpaid = new WantedPosts();
        overpaid.beginMonth();
        overpaid.ask(new WantedPosts.Ask("s", "b", heldPosts, 1.5));
        overpaid.settle(new double[] { supply, supply, 5 * supply, supply }, bandWorkers, bandPremiums);
        assertTrue("a band already priced past what the business could pay has no room: the ask counts for nothing",
                overpaid.getLatent(WageBand.COLLEGE) == 0 && !overpaid.any());

        // Through the game: the same hold, and the prime rate that takes its margin to a twentieth of the bill it lacks.
        Game g5 = shortCollege(root, "pay");
        Sector man5 = g5.getSectors().manufacturing();
        BuildingsTemplate mw5 = template(g5, MACHINE_WORKS);
        BusinessDebtManager credit5 = g5.getEconomyManager().getBusinessDebtManager();
        BusinessInvestment bi5 = g5.getBusinessInvestment();
        double earns5 = bi5.estimatedMonthlyProfit(man5.key(), mw5);
        double[] rates5 = g5.getEconomyManager().getWageRates();
        double shortBill = postsIn(mw5, WageBand.COLLEGE) * rates5[JobType.COLLEGE_ENGINEERING.ordinal()]
                + postsIn(mw5, WageBand.UNIVERSITY) * rates5[JobType.UNIV_SCIENCE.ordinal()];
        assertTrue("fixture: the posts the Machine Works lacks hands for have a bill", shortBill > 0 && earns5 > 0);
        credit5.setPrimeRate(primeFor(g5, mw5, earns5 - .05 * shortBill));
        out.printf("   the Machine Works earns %,.1f a month and lacks hands for a bill of %,.1f; at a prime of %.2f%% its margin is %,.2f%n",
                earns5, shortBill, credit5.getPrimeRate() * 100, marginOf(g5, man5, mw5));
        close("fixture: at that prime the margin is a twentieth of the bill it lacks", marginOf(g5, man5, mw5), .05 * shortBill, 1e-6);
        g5.getWantedPosts().beginMonth();
        g5.noteStaffingHold(man5.staffing(mw5));
        WantedPosts wp5 = g5.getWantedPosts();
        assertTrue("a hold that pays a little asks, with a pay factor just over one",
                wp5.getAsked() == 1 && wp5.asks().get(0).payFactor() > 1 && wp5.asks().get(0).payFactor() < 1.1);
        g5.settleWantedPosts();
        PopulationManager pm5 = g5.getPopulationManager();
        double premiumC = g5.getLabourMarket().bandPremium(WageBand.COLLEGE);
        double roomC = LabourMarket.tightnessAt(premiumC * wp5.asks().get(0).payFactor()) * Math.max(pm5.supplyByBand()[COLLEGE], 1)
                - pm5.staffablePostsByBand()[COLLEGE];
        out.printf("   the college band is priced on %.2f refused posts of the %.0f the Machine Works lacks%n",
                wp5.getLatent(WageBand.COLLEGE), postsIn(mw5, WageBand.COLLEGE));
        close("the college band counts the refused posts only as far as that wage lets the tightness go",
                wp5.getLatent(WageBand.COLLEGE), roomC, 1e-9);
        assertTrue("...which is less than the building lacks", wp5.getLatent(WageBand.COLLEGE) < postsIn(mw5, WageBand.COLLEGE));

        // A hold that cannot carry its interest at today's wage is counted and asks nothing.
        credit5.setPrimeRate(primeFor(g5, mw5, 2 * earns5));
        assertTrue("fixture: at a dearer prime the Machine Works no longer carries its interest", marginOf(g5, man5, mw5) < 0);
        g5.getWantedPosts().beginMonth();
        g5.noteStaffingHold(man5.staffing(mw5));
        assertTrue("a hold that would not carry its interest is counted unpaid and asks nothing",
                wp5.getUnpaidHolds() == 1 && wp5.getAsked() == 0 && wp5.getCrewsHolds() == 0 && wp5.asks().isEmpty());
        g5.settleWantedPosts();
        assertTrue("...so no band is priced on posts that were refused", !wp5.any());

        /* ============ 6. the builders' laid-off crews are not a shortage ============ */
        out.println("\n--- the builders' laid-off crews are not a shortage ---");

        Game g6 = city(root, "crews");
        month(g6);
        Sector man6 = g6.getSectors().manufacturing();
        BuildingsTemplate mw6 = template(g6, MACHINE_WORKS);
        // Hands for the Machine Works, every band, to spare: its own gate passes.
        double[] plenty6 = { 0, 600, postsIn(mw6, WageBand.COLLEGE) + 100, postsIn(mw6, WageBand.UNIVERSITY) + 20 };
        hands(g6, 1_500, plenty6, 0, 0);
        BuildingManager bm6 = g6.getBuildingManager();
        BuildingsTemplate depot = template(g6, "Construction Depot");
        // Every crew offered (no hook): nothing is withheld, and the Machine Works passes.
        bm6.setOfferedShare(null);
        assertTrue("fixture: with every post offered nothing is withheld and the Machine Works passes",
                bm6.getPostsWithheld() == 0 && man6.staffing(mw6).passes());
        double shareBefore = man6.staffing(mw6).share;
        // ...then the builders lay their crews off: the posts of the buildings they own are not offered
        // (BuildingManager.getPostsWithheld()), and depots are put up until the gate counts enough of them spoken for.
        bm6.setOfferedShare(sector -> sector.equals(depot.getSector()) ? 0 : 1);
        int depots = 0;
        while (man6.staffing(mw6).passes() && depots < 400) {
            bm6.addStack(depot, 1, true);
            depots++;
        }
        Sector.Staffing spokenFor = man6.staffing(mw6), free6 = man6.staffingWithCrewsSpare(mw6, 1);
        long withheld6 = bm6.getPostsWithheld();
        out.printf("   %d depots laid off (%,d posts withheld): the gate refuses the Machine Works at %.1f%%; with the crews spare, %.1f%%%n",
                depots, withheld6, spokenFor.share * 100, free6.share * 100);
        assertTrue("with the builders' crews counted spoken for, the gate refuses the Machine Works", !spokenFor.passes() && withheld6 > 0 && depots < 400);
        assertTrue("with the crews counted spare the same building passes: it was the crews", free6.passes());
        close("...and the crews-spare test is the one with none of the withheld posts in it: the share the city had before",
                free6.share, shareBefore, 0);
        bm6.setOfferedShare(null);
        assertTrue("(and with the crews called back, the plain test says the same)", man6.staffing(mw6).passes());
        bm6.setOfferedShare(sector -> sector.equals(depot.getSector()) ? 0 : 1);
        Sector.Staffing three = man6.staffingWithCrewsSpare(mw6, 3);
        assertTrue("it is asked of for any number of the building", three.count() == 3 && three.wanted()[0] == 3 * postsIn(mw6, WageBand.NONE));
        g6.getWantedPosts().beginMonth();
        g6.noteStaffingHold(spokenFor);
        WantedPosts wp6 = g6.getWantedPosts();
        assertTrue("a hold the crews alone caused is counted so", wp6.getCrewsHolds() == 1 && wp6.getHolds() == 1);
        assertTrue("...and asks nothing: no ask, no unpaid hold", wp6.getAsked() == 0 && wp6.getUnpaidHolds() == 0 && wp6.asks().isEmpty());
        g6.settleWantedPosts();
        assertTrue("...so no band is priced on it", !wp6.any());
        // And the same city with no crews laid off but a real shortage of the building's hands: it asks.
        bm6.setOfferedShare(sector -> 1);
        hands(g6, 1_500, new double[] { 0, 600, 0, 0 }, 0, 0);
        Sector.Staffing shortage = man6.staffing(mw6);
        assertTrue("fixture: with the crews back and no graduate it is the hands that are short",
                !shortage.passes() && !man6.staffingWithCrewsSpare(mw6, 1).passes());
        g6.getWantedPosts().beginMonth();
        g6.noteStaffingHold(shortage);
        assertTrue("a hold the crews did not cause is no crews' hold: it asks", wp6.getCrewsHolds() == 0 && wp6.getAsked() == 1);

        /* ============ 7. it decays ============ */
        out.println("\n--- it decays ---");

        Game g7 = heldCity(root, "decay");
        PopulationManager pm7 = g7.getPopulationManager();
        LabourMarket lm7 = g7.getLabourMarket();
        g7.repriceLabour();   // a month priced with the refused posts: the wage is up
        double raised = lm7.bandPremium(WageBand.COLLEGE);
        assertTrue("fixture: the wage is up on the refused posts", raised > 1 && g7.getWantedPosts().any());
        double[] existing7 = pm7.staffablePostsByBand();
        LabourMarket nextWith = monthOn(g7, existing7);
        // The next month's planning: the gate lets the building through, or the sector stops asking - no hold.
        g7.getWantedPosts().beginMonth();
        g7.settleWantedPosts();
        assertTrue("with no hold the month after, the latent posts are zero", !g7.getWantedPosts().any() && g7.getWantedPosts().getTotal() == 0);
        g7.repriceLabour();
        close("...the target falls back to the market's on the posts that exist",
                lm7.getBandMultiple(WageBand.COLLEGE), nextWith.getBandMultiple(WageBand.COLLEGE), 1e-12);
        close("...and the wage walks back ADJUST_RATE of the gap",
                lm7.bandPremium(WageBand.COLLEGE), raised + (lm7.getBandMultiple(WageBand.COLLEGE) - raised) * LabourMarket.ADJUST_RATE, 1e-12);
        assertTrue("...down from where the refused posts took it", lm7.bandPremium(WageBand.COLLEGE) < raised);
        // A hold the month after, but a smaller one: the latent is that month's asks, not a sum with last month's.
        g7.getWantedPosts().beginMonth();
        g7.noteStaffingHold(g7.getSectors().manufacturing().staffing(template(g7, MACHINE_WORKS)));
        g7.settleWantedPosts();
        double[] again = g7.getWantedPosts().latent();
        g7.getWantedPosts().beginMonth();
        g7.noteStaffingHold(g7.getSectors().manufacturing().staffing(template(g7, MACHINE_WORKS)));
        g7.settleWantedPosts();
        boolean notSummed = true;
        for (int b = 0; b < again.length; b++) notSummed &= g7.getWantedPosts().latent()[b] == again[b];
        assertTrue("the same hold two months running prices the same posts, not twice the posts", notSummed);

        /* ============ 8. it brings the band's migrants ============ */
        out.println("\n--- it brings the band's migrants ---");

        Game bid = heldCity(root, "migrants-bid"), quiet8 = shortCollege(root, "migrants-none");
        bid.repriceLabour();
        quiet8.repriceLabour();
        double bidPremium = bid.getLabourMarket().bandPremium(WageBand.COLLEGE);
        double nonePremium = quiet8.getLabourMarket().bandPremium(WageBand.COLLEGE);
        out.printf("   college premium after a month: %.4f with the refused posts, %.4f without%n", bidPremium, nonePremium);
        assertTrue("fixture: without the refused posts the college band pays the going rate or under it", nonePremium <= 1);
        assertTrue("with them it pays over it", bidPremium > 1);
        close("without the refused posts the college wage draws no migrant", Migration.reach(nonePremium), 0, 0);
        assertTrue("with them it does", Migration.reach(bidPremium) > 0);
        assertTrue("...and Migration reads the premium, not the posts: the band's migrants are the wage's", Migration.reach(bidPremium) > Migration.reach(nonePremium));

        /* ============ 9. the save ============ */
        out.println("\n--- the save ---");

        // The city lives in a folder of the harness's own (the TempDirs root), saved to its last slot: no slot of
        // the player's is touched.
        GameFiles files9 = filesFor(root, "save");
        Game g9 = held(shortCollege(city(files9)));
        double[] latent9 = g9.getWantedPosts().latent();
        assertTrue("fixture: the city has latent posts to save", g9.getWantedPosts().any());
        GameFiles.Result saved;
        Game back = new Game(files9);
        System.setOut(quiet);
        try {
            saved = g9.saveGame(GameFiles.SLOT_COUNT, "wantedposts");
            back.loadGameSave(GameFiles.SLOT_COUNT);
        } finally {
            System.setOut(out);
        }
        assertTrue("the save was written and read back", saved.ok && back.getLoadFailure() == null);
        boolean bitForBit = back.getWantedPosts().latent().length == latent9.length;
        for (int b = 0; b < latent9.length; b++) {
            bitForBit &= Double.doubleToLongBits(back.getWantedPosts().latent()[b]) == Double.doubleToLongBits(latent9[b]);
        }
        assertTrue("the latent posts come back from the save to the bit", bitForBit);
        // Both cities in the same shape, and the same month priced: the reloaded one prices it as the live one does.
        shortHands(back);
        // (the cost of living is the price index's, not the wanted posts' to carry: the two cities start from one)
        back.getLabourMarket().setCostOfLiving(g9.getLabourMarket().getCostOfLiving());
        g9.repriceLabour();
        back.repriceLabour();
        boolean alike = true;
        double[] liveState = g9.getLabourMarket().state(), backState = back.getLabourMarket().state();
        for (int i = 0; i < liveState.length; i++) alike &= Double.doubleToLongBits(liveState[i]) == Double.doubleToLongBits(backState[i]);
        assertTrue("...and both cities price the next month alike, to the bit", alike && liveState.length == backState.length);
        assertTrue("...which is a month priced on the refused posts: the wage is up on both",
                g9.getLabourMarket().bandPremium(WageBand.COLLEGE) > 1 && back.getLabourMarket().bandPremium(WageBand.COLLEGE) > 1);
        WantedPosts nothing = new WantedPosts();
        nothing.restore(null);
        assertTrue("a save with no wanted posts (an older one) reads as none", !nothing.any());
        nothing.restore(new double[] { 1, 2 });
        assertTrue("...and one of a wrong length too", !nothing.any());

        /* ============ 10. the month's planning notes its holds ============ */
        out.println("\n--- the month's planning notes its holds ---");

        // A city played ten years: Game.consider() hands every staffing hold the planners return to noteStaffingHold().
        Game played = city(root, "played");
        int holds = 0, asked = 0, crews = 0, unpaid = 0, months = 120;
        boolean counted = true, latentFromAsks = true, withinWorkers = true;
        for (int m = 0; m < months; m++) {
            month(played);
            WantedPosts w = played.getWantedPosts();
            holds += w.getHolds();
            asked += w.getAsked();
            crews += w.getCrewsHolds();
            unpaid += w.getUnpaidHolds();
            counted &= w.getHolds() == w.getCrewsHolds() + w.getUnpaidHolds() + w.getAsked();
            latentFromAsks &= !w.any() || w.getAsked() > 0;
            // The month's real asks, settled resettled against the state the month ends in (the live latent was settled a
            // moment earlier, against a population that has since moved a little): never past the band's workers.
            WantedPosts resettled = new WantedPosts();
            resettled.beginMonth();
            for (WantedPosts.Ask a : w.asks()) resettled.ask(a);
            PopulationManager people = played.getPopulationManager();
            double[] premiums = new double[WageBand.values().length];
            for (WageBand band : WageBand.values()) premiums[band.ordinal()] = played.getLabourMarket().bandPremium(band);
            resettled.settle(people.staffablePostsByBand(), people.supplyByBand(), premiums);
            for (int b = 0; b < premiums.length; b++) {
                withinWorkers &= resettled.latent()[b] <= WantedPosts.MOST_OF_SUPPLY * Math.max(people.supplyByBand()[b], 1);
            }
        }
        out.printf("   %d months: %,d staffing holds noted - %,d asked for their posts, %,d for the builders' crews, %,d unpaid%n",
                months, holds, asked, crews, unpaid);
        assertTrue("the planners' holds are noted as the months play, and some ask", holds > 0 && asked > 0);
        assertTrue("...each sorted one way: the month's holds are its crews', its unpaid and its asks", counted);
        assertTrue("...a band is priced on refused posts only in a month that had an ask", latentFromAsks);
        assertTrue("...and the month's real asks never settle to more refused posts than the band has workers", withinWorkers);

        out.println();
        if (fails == 0) {
            out.println("The posts the gate refused are priced into their band's wage, as far as the business can pay and never past the band's workers.");
        } else {
            out.println(fails + " check(s) failed.");
            System.exit(1);
        }
    }
}
