package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import ham.citybuildersim.sectors.Construction;

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
 *   3. THE MARKET PRICES IT: the band's multiple is struck on the posts that exist plus the refused ones, against the
 *      supply the posts that exist give, and the wage walks ADJUST_RATE of the way, while the gate's own test still
 *      reads only the posts that exist.
 *   4. NEVER MORE THAN THE BAND'S OWN POSTS AGAIN: the refused posts lift a band's multiple at most sqrt(2) times,
 *      never twice, and a band with few posts - a village's - bids little.
 *   5. ONLY AS FAR AS THE BUSINESS CAN PAY: the best-paying building that wants the posts is how far they count.
 *   6. A WORKING BUILDER'S LAID-OFF CREWS ARE NOT A SHORTAGE: a hold the gate would not have put on but for them asks
 *      nothing.
 *   7. IT DECAYS: no hold, no latent posts, and the wage walks back.
 *   8. IT BRINGS THE BAND'S MIGRANTS: the premium it makes is what Migration reaches for.
 *   9. THE SAVE: the latent posts come back from a save to the bit (the People caption reads them between a load and
 *      the next settle), but they never reach the wage: a month's own planning settles its asks before the reprice
 *      prices them, so a reloaded city's month is the same with the saved posts or without.
 *  10. THE WIRING: in a city played ten years every hold a planner returns is noted, sorted and priced within the rules above.
 *  11. AN IDLE BUILDER'S CREWS ARE SPARE (IC1): the posts a builder at its core crew has laid off do not hold the gate,
 *      and a working builder's still do.
 *  12. THE CAP READS THE MONTH'S OWN POSTS (LD2): posts that fall between the settle and the reprice (completions, a
 *      strike, retirement) leave the latent posts held, at the reprice, to MOST_OF_POSTS of the posts it prices.
 *  13. A BAND THAT ASKED NOTHING IS PRICED AS IT WAS (LD2): a refused post is the numerator of its own band's
 *      tightness alone, so it keeps no worker from the band below it, which is priced on the fill against the posts
 *      that exist.
 *  14. A BAND CLOSED TO MIGRANTS ASKS NOTHING (LD2): the wage is meant to bring migrants, so a band none can come for
 *      has no latent posts, though the hold is still counted and the bands open to migrants still bid.
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
            noWait(g);
        } finally {
            System.setOut(out);
        }
        return g;
    }

    /**
     * No wait read for an order's interest (BA1: BusinessInvestment.carryOf(), THE INTEREST ON THE WAIT), as in a bare
     * fixture: these cities have no builders, so every wait would be endless and every hold unpaid - rightly, a building
     * nobody can build carries no interest at any wage - and the posts' price is this harness's subject, not the wait.
     * Game wires the wait again on a load, so a reloaded city is told too.
     */
    static void noWait(Game g) {
        g.getBusinessInvestment().setOrderWaitOutput(null);
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

    /** The game's own hook (Game's constructor): the builders offer the share they struck, every other sector all its posts. */
    static void builderHook(Game g) {
        Construction builders = g.getSectors().construction();
        g.getBuildingManager().setOfferedShareByType((key, type) -> key.equals(builders.key()) ? builders.getPostsOfferedShare() : 1);
    }

    /**
     * Strikes the builders WORKING: {@code share} of their depots' posts on offer (above the core crew, under all). Last
     * month's wages are struck first with every post of theirs filled, as strikeCrews() reads the sector's own fill (a
     * city's builders have none to read until its first staffed month, and offer every post for want of one).
     */
    static void strikeWorking(Construction builders, double share) {
        double[] full = new double[JobType.values().length];
        java.util.Arrays.fill(full, 1);
        builders.updateJobFillRate(full);
        builders.updateWages(new double[full.length], builders.postsPerTier());
        builders.strikeCrews(share * 1_000, 1_000);
    }

    /** Strikes the builders IDLE: no work ahead, so only the core crew (Construction.IDLE_PAYROLL_FLOOR) is on offer. */
    static void strikeIdle(Construction builders) {
        builders.strikeCrews(0, 1_000);
    }

    /** Puts the city's hands and the posts that exist in a stated shape (they stand until a month is played). */
    static void hands(Game g, long workforce, double[] heads, long collegePosts, long universityPosts) {
        long[] jobs = new long[JobType.values().length];
        jobs[JobType.COLLEGE_ENGINEERING.ordinal()] = collegePosts;
        jobs[JobType.UNIV_SCIENCE.ordinal()] = universityPosts;
        hands(g, workforce, heads, jobs);
    }

    /** hands() with the posts written job by job, for a fixture that needs a band the other one does not (the diploma's). */
    static void hands(Game g, long workforce, double[] heads, long[] jobs) {
        PopulationManager pm = g.getPopulationManager();
        pm.restoreWorkforce(workforce);
        pm.restoreSkilledHeads(heads);
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
        atBase(g);
        shortHands(g);
        return g;
    }

    /** A city a month old with its labour market at its base, so no band starts under or over the going rate. */
    static Game atBase(Game g) {
        month(g);   // the wage schedule the sectors pay is the market's, not the founding's zeros
        g.getLabourMarket().resetToBase();
        g.getPopulationManager().takeWagesFrom(g.getLabourMarket());
        return g;
    }

    /**
     * A city with no room, a month older (GW1: the gate counts a year's draw, Sector.GATE_DRAW_MONTHS, and migration brings
     * nobody to a city that has no room, Migration.getLastCrowding() 0): the founding's first households have no homes yet,
     * so the month after the first reads the ramp at its floor. Months are played, the market put back at its base each,
     * until it reads so (a handful at most: homes come later), and the caller asserts the fixture.
     */
    static Game noRoom(Game g) {
        for (int months = 0; months < 6 && g.getMigration().getLastCrowding() > 0; months++) atBase(g);
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
        return monthOn(g, posts, posts);
    }

    /**
     * What the market does with a month priced on {@code posts} against the supply the fill gives {@code supplyPosts}
     * (LD2: the posts that exist; LD1 filled against the posts it priced on), on a twin of it.
     */
    static LabourMarket monthOn(Game g, double[] posts, double[] supplyPosts) {
        PopulationManager pm = g.getPopulationManager();
        LabourMarket t = twin(g.getLabourMarket());
        t.advanceMonth(posts, pm.supplyByBand(supplyPosts), pm.getJobs(), pm.getLicensedHeads());
        return t;
    }

    /** A city loaded from the harness's own save slot (never a player's: the folder is the TempDirs root's). */
    static Game reload(GameFiles files) {
        Game g = new Game(files);
        System.setOut(quiet);
        try { g.loadGameSave(GameFiles.SLOT_COUNT); } finally { System.setOut(out); }
        noWait(g);
        return g;
    }

    /** Whether two arrays hold the same doubles to the bit. */
    static boolean sameBits(double[] a, double[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (Double.doubleToLongBits(a[i]) != Double.doubleToLongBits(b[i])) return false;
        return true;
    }

    static double[] plus(double[] a, double[] b) {
        double[] sum = a.clone();
        for (int i = 0; i < sum.length && i < b.length; i++) sum[i] += b[i];
        return sum;
    }

    static final int DIPLOMA = WageBand.DIPLOMA.ordinal(), COLLEGE = WageBand.COLLEGE.ordinal(), UNIVERSITY = WageBand.UNIVERSITY.ordinal();

    /** The margin Game.noteStaffingHold() works out for a building: what it earns over the interest hurdle at prime and its record. */
    static double marginOf(Game g, Sector owner, BuildingsTemplate t) {
        BusinessInvestment bi = g.getBusinessInvestment();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        double prime = credit.getPrimeRate() + credit.getRecordSurcharge(owner.key());
        // ...on the cost grown over its wait, as Game.noteStaffingHold() asks it (BA1; 1 with no wait read, noWait()).
        return bi.estimatedMonthlyProfit(owner.key(), t) - bi.interestHurdle(bi.getCostOf(t, 1) * bi.carryOf(t, 1, prime), prime);
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
        // ...the posts the gate counts: those that exist and the laid-off crews' that are spoken for, as Sector.staffing()
        // adds them (an idle builder's are spare, IC1: this city's builders have no work ahead)
        double[] posts1 = pm1.staffablePostsByBand();
        Construction builders1 = g1.getSectors().construction();
        for (JobType job : JobType.values()) {
            long spokenFor1 = g1.getBuildingManager().getPostsWithheld(job)
                    - (builders1.isIdleAtCoreCrew() ? g1.getBuildingManager().getPostsWithheld(builders1.key(), job) : 0);
            posts1[WageBand.of(job).ordinal()] += spokenFor1;
        }
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
        // GW1: the gate counts a year's draw (Sector.GATE_DRAW_MONTHS); a city with no room draws nobody, so its unskilled hands are all it has.
        noRoom(g1);
        hands(g1, WORKFORCE, new double[] { 0, 0, 0, 0 }, 0, 0);
        assertTrue("fixture: the city has no room (crowding 0), so migration brings nobody for the gate's year and the draw is nothing",
                g1.getMigration().getLastCrowding() == 0);
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

        // GW1: the gate counts a year's draw (Sector.GATE_DRAW_MONTHS), which reads no band premium; a city with no room draws nobody, so
        // the gate's share is the spare workers' alone here, and the claim below (pricing leaves the share as it was) is read on that.
        // The same claim in a city with room follows the section's last lines.
        Game g3 = held(shortCollege(noRoom(city(root, "market"))));
        Sector man3 = g3.getSectors().manufacturing();
        BuildingsTemplate mw3 = template(g3, MACHINE_WORKS);
        WantedPosts wanted3 = g3.getWantedPosts();
        PopulationManager pm3 = g3.getPopulationManager();
        LabourMarket lm3 = g3.getLabourMarket();
        assertTrue("fixture: the city has no room (crowding 0), so migration brings nobody for the gate's year and the draw is nothing",
                g3.getMigration().getLastCrowding() == 0 && java.util.Arrays.stream(man3.staffing(mw3).drawn()).sum() == 0);
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
        // ...struck against the supply the posts that exist give (LD2: the refused posts are only a numerator)
        LabourMarket with = monthOn(g3, seen, existing), without = monthOn(g3, existing);
        double wasPremium = lm3.bandPremium(WageBand.COLLEGE);
        double gateShare = man3.staffing(mw3).share;
        g3.repriceLabour();
        close("the college band's multiple is the market's own on the posts that exist plus the refused ones, against the supply the posts that exist give",
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

        // The same claim in a city WITH room, where the gate's draw is not nothing: the band premium the refused posts raise is not in the
        // draw (Migration.drawFor() reads no band premium), so the share the gate counts, the draw with it, is as before the reprice.
        Game g3r = heldCity(root, "market-room");
        Sector man3r = g3r.getSectors().manufacturing();
        BuildingsTemplate mw3r = template(g3r, MACHINE_WORKS);
        LabourMarket lm3r = g3r.getLabourMarket();
        Sector.Staffing before3r = man3r.staffing(mw3r);
        double wasPremium3r = lm3r.bandPremium(WageBand.COLLEGE);
        double drawnBefore3r = java.util.Arrays.stream(before3r.drawn()).sum();
        assertTrue("fixture: the city has room (crowding over 0), and the gate's draw for the Machine Works is workers, not nothing",
                g3r.getMigration().getLastCrowding() > 0 && drawnBefore3r > 0 && !before3r.passes());
        g3r.repriceLabour();
        Sector.Staffing after3r = man3r.staffing(mw3r);
        out.printf("   a city with room (crowding %.2f): the Machine Works' share %.4f before the reprice and %.4f after; its draw %.2f workers before and %.2f after;"
                        + " the college premium %.4f then %.4f%n", g3r.getMigration().getLastCrowding(), before3r.share, after3r.share,
                drawnBefore3r, java.util.Arrays.stream(after3r.drawn()).sum(), wasPremium3r, lm3r.bandPremium(WageBand.COLLEGE));
        assertTrue("fixture: the refused posts raised the college wage over where it stood", lm3r.bandPremium(WageBand.COLLEGE) > wasPremium3r);
        close("the gate's own test still reads only the posts that exist, with room: the same share as before", after3r.share, before3r.share, 0);
        assertTrue("...and the same draw, band by band, to the bit: the gate does not read the wage the refused posts raised",
                java.util.Arrays.equals(before3r.drawn(), after3r.drawn()));

        /* ============ 4. never more than the band's own posts again ============ */
        out.println("\n--- never more than the band's own posts again ---");

        double supply = 1_000;
        boolean cappedEverywhere = true, underRootTwoEverywhere = true;
        double rootCap = Math.sqrt(1 + WantedPosts.MOST_OF_POSTS);
        double floorTightness = LabourMarket.tightnessAt(LabourMarket.MIN_MULTIPLE);
        double[] tightnesses = { floorTightness / 2, floorTightness, 1, 2, LabourMarket.tightnessAt(LabourMarket.MAX_MULTIPLE) / 2 };
        for (double tightness : tightnesses) {
            WantedPosts wp = new WantedPosts();
            wp.beginMonth();
            wp.ask(new WantedPosts.Ask("s", "b", new double[] { 50 * supply, 50 * supply, 50 * supply, 50 * supply }, Double.POSITIVE_INFINITY));
            double[] posts = { tightness * supply, tightness * supply, tightness * supply, tightness * supply };
            wp.settle(posts, new double[] { supply, supply, supply, supply }, new double[] { 1, 1, 1, 1 });
            cappedEverywhere &= wp.getLatent(WageBand.COLLEGE) == WantedPosts.MOST_OF_POSTS * posts[COLLEGE];
            double without_ = LabourMarket.multipleAt(posts[COLLEGE] / supply);
            double with_ = LabourMarket.multipleAt((posts[COLLEGE] + wp.getLatent(WageBand.COLLEGE)) / supply);
            underRootTwoEverywhere &= with_ <= rootCap * without_ * (1 + 1e-12);
            out.printf("   posts at %.2f of the band's workers: multiple %.3f, %.3f with asks far past its posts (x%.3f)%n",
                    tightness, without_, with_, with_ / without_);
        }
        assertTrue("asks far larger than the band count as MOST_OF_POSTS of the posts it has, whatever its tightness", cappedEverywhere);
        assertTrue(String.format("...so the band's multiple with them is at most sqrt(1 + MOST_OF_POSTS) = %.3f times its multiple without:"
                + " never twice", rootCap), underRootTwoEverywhere && rootCap < 2);
        WantedPosts none = new WantedPosts();
        none.beginMonth();
        none.ask(new WantedPosts.Ask("s", "b", new double[] { 5, 5, 5, 5 }, Double.POSITIVE_INFINITY));
        none.settle(new double[] { 0, 0, 0, 0 }, new double[] { 70, 70, 70, 70 }, new double[] { 1, 1, 1, 1 });
        assertTrue("a band with no posts that exist bids nothing for refused ones", none.getTotal() == 0 && !none.any());
        // A village: seventy workers in a band holding twenty posts, and a plant of three hundred refused there (the gate's
        // reason for being, Sector.MIN_STAFFABLE_TO_ORDER). With the plant's posts priced in full the band would read
        // past full; held to its own posts it reads under, and the wage barely moves.
        double villageWorkers = 70, villagePosts = 20, plant = 300;
        WantedPosts village = new WantedPosts();
        village.beginMonth();
        village.ask(new WantedPosts.Ask("s", "b", new double[] { plant, 0, 0, 0 }, Double.POSITIVE_INFINITY));
        village.settle(new double[] { villagePosts, 0, 0, 0 }, new double[] { villageWorkers, 1, 1, 1 }, new double[] { 1, 1, 1, 1 });
        close("a village's refused plant counts for no more posts than the band has", village.getLatent(WageBand.NONE),
                WantedPosts.MOST_OF_POSTS * villagePosts, 0);
        assertTrue("...so the band it bids on still reads under full, where the plant's posts in full would read far past it",
                (villagePosts + village.getLatent(WageBand.NONE)) / villageWorkers < 1 && (villagePosts + plant) / villageWorkers > 1);

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
        assertTrue("...short of what it asked for and of the band's posts",
                few.getLatent(WageBand.COLLEGE) < 800 && few.getLatent(WageBand.COLLEGE) < WantedPosts.MOST_OF_POSTS * existingPosts[COLLEGE]);
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

        /* ============ 6. a working builder's laid-off crews are not a shortage ============ */
        out.println("\n--- a working builder's laid-off crews are not a shortage ---");

        Game g6 = city(root, "crews");
        month(g6);
        // GW1: the gate counts a year's draw (Sector.GATE_DRAW_MONTHS), which would staff the building whatever the crews hold; a city with no room draws nobody, so the crews are what refuses it.
        noRoom(g6);
        Sector man6 = g6.getSectors().manufacturing();
        BuildingsTemplate mw6 = template(g6, MACHINE_WORKS);
        // Hands for the Machine Works, every band, to spare: its own gate passes.
        double[] plenty6 = { 0, 600, postsIn(mw6, WageBand.COLLEGE) + 100, postsIn(mw6, WageBand.UNIVERSITY) + 20 };
        hands(g6, 1_500, plenty6, 0, 0);
        assertTrue("fixture: the city has no room (crowding 0), so migration brings nobody for the gate's year and the draw is nothing",
                g6.getMigration().getLastCrowding() == 0);
        BuildingManager bm6 = g6.getBuildingManager();
        BuildingsTemplate depot = template(g6, "Construction Depot");
        Construction builders6 = g6.getSectors().construction();
        // Every crew offered (no hook): nothing is withheld, and the Machine Works passes.
        bm6.setOfferedShare(null);
        assertTrue("fixture: with every post offered nothing is withheld and the Machine Works passes",
                bm6.getPostsWithheld() == 0 && man6.staffing(mw6).passes());
        double shareBefore = man6.staffing(mw6).share;
        // ...then a WORKING builder lays crews off (IC1: an idle builder's would be spare for the gate): struck at half
        // their posts, above the core crew and under all, under the game's own hook, so the posts of the buildings they
        // own are withheld at the share the model struck (BuildingManager.getPostsWithheld()), and depots are put up
        // until the gate counts enough of them spoken for.
        strikeWorking(builders6, .5);
        builderHook(g6);
        assertTrue("fixture: the builders are working, offering more than the core crew and not all of their posts",
                builders6.getPostsOfferedShare() > Construction.IDLE_PAYROLL_FLOOR && builders6.getPostsOfferedShare() < 1
                && !builders6.isIdleAtCoreCrew() && depot.getSector().equals(builders6.key()));
        int depots = 0;
        while (man6.staffing(mw6).passes() && depots < 400) {
            bm6.addStack(depot, 1, true);
            depots++;
        }
        Sector.Staffing spokenFor = man6.staffing(mw6), free6 = man6.staffingWithCrewsSpare(mw6, 1);
        long withheld6 = bm6.getPostsWithheld();
        out.printf("   %d depots, the working builders offering %.0f%% of their posts (%,d posts withheld): the gate refuses the Machine Works at %.1f%%; with the crews spare, %.1f%%%n",
                depots, builders6.getPostsOfferedShare() * 100, withheld6, spokenFor.share * 100, free6.share * 100);
        assertTrue("with a working builder's crews counted spoken for, the gate refuses the Machine Works", !spokenFor.passes() && withheld6 > 0 && depots < 400);
        assertTrue("with the crews counted spare the same building passes: it was the crews", free6.passes());
        close("...and the crews-spare test is the one with none of the withheld posts in it: the share the city had before",
                free6.share, shareBefore, 0);
        bm6.setOfferedShare(null);
        assertTrue("(and with the crews called back, the plain test says the same)", man6.staffing(mw6).passes());
        builderHook(g6);
        Sector.Staffing three = man6.staffingWithCrewsSpare(mw6, 3);
        assertTrue("it is asked of for any number of the building", three.count() == 3 && three.wanted()[0] == 3 * postsIn(mw6, WageBand.NONE));
        g6.getWantedPosts().beginMonth();
        g6.noteStaffingHold(spokenFor);
        WantedPosts wp6 = g6.getWantedPosts();
        assertTrue("a hold a working builder's crews alone caused is counted so", wp6.getCrewsHolds() == 1 && wp6.getHolds() == 1);
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
        // ONE save, read back into every city below. The latent posts are saved for what shows them between a load and
        // the next month's settle (the People caption); the wage never reads them, because a month's planning settles
        // its asks before the reprice prices them (Game.nextMonth(): runPrivateInvestment() ends in settleWantedPosts(),
        // SimulationEngine.updatePopulation() reprices later). LD1 described them as priced next month.
        GameFiles files9 = filesFor(root, "save");
        Game g9 = held(shortCollege(city(files9)));
        double[] latent9 = g9.getWantedPosts().latent();
        assertTrue("fixture: the city has latent posts to save", g9.getWantedPosts().any());
        GameFiles.Result saved;
        System.setOut(quiet);
        try {
            saved = g9.saveGame(GameFiles.SLOT_COUNT, "wantedposts");
        } finally {
            System.setOut(out);
        }
        Game back = reload(files9);
        assertTrue("the save was written and read back", saved.ok && back.getLoadFailure() == null);
        assertTrue("(a) the latent posts come back from the save to the bit: the People caption reads them between a load and the next settle",
                sameBits(back.getWantedPosts().latent(), latent9));

        // (b) Caused from the other side: were the wage to read the saved posts, they WOULD move it. Two copies of the
        // save in the same shape and the same cost of living (the price index's, not the wanted posts' to carry), one
        // with the saved posts forgotten, each priced straight with repriceLabour(): an order the game never runs.
        Game withSaved = reload(files9), forgotten = reload(files9);
        forgotten.getWantedPosts().restore(null);
        for (Game copy : new Game[] { withSaved, forgotten }) {
            shortHands(copy);
            copy.getLabourMarket().setCostOfLiving(g9.getLabourMarket().getCostOfLiving());
        }
        assertTrue("fixture: one copy has the saved posts and the other none",
                sameBits(withSaved.getWantedPosts().latent(), latent9) && !forgotten.getWantedPosts().any());
        withSaved.repriceLabour();
        forgotten.repriceLabour();
        out.printf("   a reprice straight after the load: college premium %.4f with the saved posts, %.4f without%n",
                withSaved.getLabourMarket().bandPremium(WageBand.COLLEGE), forgotten.getLabourMarket().bandPremium(WageBand.COLLEGE));
        assertTrue("(b) fixture: priced straight after the load the two copies differ, so the saved posts would move the wage if the reprice read them",
                !sameBits(withSaved.getLabourMarket().state(), forgotten.getLabourMarket().state())
                        && withSaved.getLabourMarket().bandPremium(WageBand.COLLEGE) > forgotten.getLabourMarket().bandPremium(WageBand.COLLEGE));

        // (c) The order the game runs: a real month. The month's own planning settles its asks before the reprice, so
        // the saved posts are gone before anything prices them.
        Game monthWith = reload(files9), monthWithout = reload(files9);
        monthWithout.getWantedPosts().restore(null);
        assertTrue("(c) fixture: one copy has the saved posts and the other none, before the month",
                sameBits(monthWith.getWantedPosts().latent(), latent9) && !monthWithout.getWantedPosts().any());
        month(monthWith);
        month(monthWithout);
        out.printf("   a real month after the load: college premium %.4f with the saved posts, %.4f without; latent posts %s and %s%n",
                monthWith.getLabourMarket().bandPremium(WageBand.COLLEGE), monthWithout.getLabourMarket().bandPremium(WageBand.COLLEGE),
                java.util.Arrays.toString(monthWith.getWantedPosts().latent()), java.util.Arrays.toString(monthWithout.getWantedPosts().latent()));
        assertTrue("(c) a real month after the load leaves the two labour markets alike to the bit: the saved posts never reach the wage",
                sameBits(monthWith.getLabourMarket().state(), monthWithout.getLabourMarket().state()));
        assertTrue("(c) ...and the latent posts alike to the bit: the month's own settle replaced the saved ones",
                sameBits(monthWith.getWantedPosts().latent(), monthWithout.getWantedPosts().latent()));
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
            // moment earlier, against a population that has since moved a little): never past the band's own posts again.
            WantedPosts resettled = new WantedPosts();
            resettled.beginMonth();
            for (WantedPosts.Ask a : w.asks()) resettled.ask(a);
            PopulationManager people = played.getPopulationManager();
            double[] premiums = new double[WageBand.values().length];
            for (WageBand band : WageBand.values()) premiums[band.ordinal()] = played.getLabourMarket().bandPremium(band);
            resettled.settle(people.staffablePostsByBand(), people.supplyByBand(), premiums);
            for (int b = 0; b < premiums.length; b++) {
                withinWorkers &= resettled.latent()[b] <= WantedPosts.MOST_OF_POSTS * Math.max(people.staffablePostsByBand()[b], 0);
            }
        }
        out.printf("   %d months: %,d staffing holds noted - %,d asked for their posts, %,d for the builders' crews, %,d unpaid%n",
                months, holds, asked, crews, unpaid);
        assertTrue("the planners' holds are noted as the months play, and some ask", holds > 0 && asked > 0);
        assertTrue("...each sorted one way: the month's holds are its crews', its unpaid and its asks", counted);
        assertTrue("...a band is priced on refused posts only in a month that had an ask", latentFromAsks);
        assertTrue("...and the month's real asks never settle to more refused posts than the band has posts", withinWorkers);

        /* ============ 11. an idle builder's laid-off crews are spare for the gate: XS1's trap, released (IC1) ============ */
        out.println("\n--- an idle builder's laid-off crews are spare for the gate (IC1) ---");

        // XS1's trap, caused the model's own way: depots stand, the builders have no work and offer the core crew
        // alone, and the posts they lay off are more than the city has unemployed to fill anything.
        Game g11 = city(root, "idle-crews");
        month(g11);
        // GW1: the gate counts a year's draw (Sector.GATE_DRAW_MONTHS), which would staff the building whatever the crews hold; a city with no room draws nobody, so the crews are what refuses it.
        noRoom(g11);
        Sector man11 = g11.getSectors().manufacturing();
        BuildingsTemplate mw11 = template(g11, MACHINE_WORKS);
        BuildingsTemplate depot11 = template(g11, "Construction Depot");
        BuildingManager bm11 = g11.getBuildingManager();
        PopulationManager pm11 = g11.getPopulationManager();
        Construction builders11 = g11.getSectors().construction();
        // Hands for the Machine Works, every band, to spare (section 6's): its own gate passes with every crew spare.
        double[] plenty11 = { 0, 600, postsIn(mw11, WageBand.COLLEGE) + 100, postsIn(mw11, WageBand.UNIVERSITY) + 20 };
        hands(g11, 1_500, plenty11, 0, 0);
        assertTrue("fixture: the city has no room (crowding 0), so migration brings nobody for the gate's year and the draw is nothing",
                g11.getMigration().getLastCrowding() == 0);
        double postsThatExist = 0;
        for (double band : pm11.staffablePostsByBand()) postsThatExist += band;
        long unemployed11 = Math.round(pm11.getLabourForce() - postsThatExist);
        // The builders working at a share just above the core crew lay off the most crews a working builder does:
        // depots are put up until what they withhold is more than the city's unemployed and the gate refuses the works.
        double workingShare = Construction.IDLE_PAYROLL_FLOOR + .05;
        strikeWorking(builders11, workingShare);
        builderHook(g11);
        int depots11 = 0;
        while ((bm11.getPostsWithheld() <= unemployed11 || man11.staffing(mw11).passes()) && depots11 < 400) {
            bm11.addStack(depot11, 1, true);
            depots11++;
        }
        long withheldWorking = bm11.getPostsWithheld();
        double shareWorking = man11.staffing(mw11).share;
        boolean refusedWorking = !man11.staffing(mw11).passes();
        int countWorking = man11.staffableCount(mw11, 1);

        // ...and now the same city's builders have no work at all: they stand idle at the core crew.
        strikeIdle(builders11);
        long withheldIdle = bm11.getPostsWithheld();
        long builders11Withheld = 0;
        for (JobType job : JobType.values()) builders11Withheld += bm11.getPostsWithheld(builders11.key(), job);
        Sector.Staffing idleGate = man11.staffing(mw11), idleFree = man11.staffingWithCrewsSpare(mw11, 1);
        out.printf("   %d depots, %,d unemployed: working at %.0f%% they withhold %,d posts (the gate reads %.1f%%), idle at %.0f%% %,d (the gate reads %.1f%%; with the crews spare, %.1f%%)%n",
                depots11, unemployed11, workingShare * 100, withheldWorking, shareWorking * 100,
                builders11.getPostsOfferedShare() * 100, withheldIdle, idleGate.share * 100, idleFree.share * 100);
        assertTrue("fixture: the builders stand idle at the core crew, IDLE_PAYROLL_FLOOR of their posts offered",
                builders11.isIdleAtCoreCrew() && builders11.getPostsOfferedShare() == Construction.IDLE_PAYROLL_FLOOR);
        assertTrue("fixture: posts are withheld, and every one of them is the builders'",
                withheldIdle > 0 && withheldIdle == builders11Withheld && depot11.getSector().equals(builders11.key()) && depots11 < 400);
        assertTrue("fixture: the unemployed are fewer than the builders' laid-off crews (" + unemployed11 + " against " + withheldIdle + ")",
                unemployed11 < withheldIdle);
        assertTrue("while the builders stand idle at the core crew the gate passes the Machine Works", idleGate.passes());
        close("...at the share the crews-spare test gives, exactly: no laid-off post of theirs is counted spoken for",
                idleGate.share, idleFree.share, 0);
        assertTrue("...and the Machine Works' planner may order one of them: Manufacturing's staffable count is 1",
                man11.staffableCount(mw11, 1) == 1);

        // The same crews laid off by a builder that is WORKING stay spoken for (0.7.17's rule, kept).
        strikeWorking(builders11, workingShare);
        assertTrue("fixture: the same builders, working, offer more than the core crew and not all of their posts",
                !builders11.isIdleAtCoreCrew() && builders11.getPostsOfferedShare() > Construction.IDLE_PAYROLL_FLOOR
                && builders11.getPostsOfferedShare() < 1);
        assertTrue("...and a working builder lays off fewer posts than an idle one (" + withheldWorking + " against " + withheldIdle + ")",
                bm11.getPostsWithheld() == withheldWorking && withheldWorking > 0 && withheldWorking < withheldIdle);
        assertTrue("the same crews laid off by a working builder are spoken for: the gate refuses the Machine Works",
                refusedWorking && !man11.staffing(mw11).passes() && man11.staffing(mw11).share == shareWorking);
        assertTrue("...though the crews-spare test passes it, as it does an idle builder's", man11.staffingWithCrewsSpare(mw11, 1).passes());
        assertTrue("...and Manufacturing's staffable count is 0 while the builders work, 1 while they stand idle",
                countWorking == 0 && man11.staffableCount(mw11, 1) == 0);

        /* ============ 12. the cap reads the month's own posts (LD2) ============ */
        out.println("\n--- the cap reads the month's own posts (LD2) ---");

        // settle() holds a band's latent posts to MOST_OF_POSTS of the posts at the end of the month's planning, and the
        // reprice comes a few steps later in the same month, after completions, strikeBuildersCrews() and retirement
        // have moved the posts. Here a building lacking more college posts than the band has asks, and the band's
        // posts then FALL (hands() sets fewer, as a strike or retirement would) before the reprice.
        Game g12 = shortCollege(root, "cap");
        PopulationManager pm12 = g12.getPopulationManager();
        LabourMarket lm12 = g12.getLabourMarket();
        WantedPosts wp12 = g12.getWantedPosts();
        double[] atSettle12 = pm12.staffablePostsByBand();
        wp12.beginMonth();
        wp12.ask(new WantedPosts.Ask(g12.getSectors().manufacturing().key(), MACHINE_WORKS,
                new double[] { 0, 0, 2 * atSettle12[COLLEGE], 0 }, Double.POSITIVE_INFINITY));
        g12.settleWantedPosts();
        double[] settled12 = wp12.latent();
        long fallen12 = Math.round(.6 * COLLEGE_HEADS);
        hands(g12, WORKFORCE, new double[] { 0, DIPLOMA_HEADS, COLLEGE_HEADS, UNIVERSITY_HEADS }, fallen12, (long) UNIVERSITY_HEADS);
        double[] now12 = pm12.staffablePostsByBand();
        double[] heldTo12 = new double[settled12.length];
        for (int b = 0; b < heldTo12.length; b++) heldTo12[b] = Math.min(settled12[b], WantedPosts.MOST_OF_POSTS * Math.max(now12[b], 0));
        double rootCap12 = Math.sqrt(1 + WantedPosts.MOST_OF_POSTS);
        LabourMarket noLatent12 = monthOn(g12, now12);
        LabourMarket heldLatent12 = monthOn(g12, plus(now12, heldTo12), now12);
        LabourMarket settledLatent12 = monthOn(g12, plus(now12, settled12), now12);
        out.printf("   college posts %.0f at the settle (latent %.1f), %.0f at the reprice; multiple %.4f on the posts alone, %.4f held to the cap, %.4f on the settled latent%n",
                atSettle12[COLLEGE], settled12[COLLEGE], now12[COLLEGE], noLatent12.getBandMultiple(WageBand.COLLEGE),
                heldLatent12.getBandMultiple(WageBand.COLLEGE), settledLatent12.getBandMultiple(WageBand.COLLEGE));
        assertTrue("fixture: the college posts fell between the settle and the reprice", now12[COLLEGE] == fallen12 && now12[COLLEGE] < atSettle12[COLLEGE]);
        assertTrue("fixture: the settled college latent posts now exceed MOST_OF_POSTS of the college posts the reprice will read",
                settled12[COLLEGE] > WantedPosts.MOST_OF_POSTS * now12[COLLEGE]);
        assertTrue("fixture: the college band stands on the live part of the curve, off its floor, so the cap is what holds it",
                noLatent12.getBandMultiple(WageBand.COLLEGE) > LabourMarket.MIN_MULTIPLE
                        && settledLatent12.getBandMultiple(WageBand.COLLEGE) < LabourMarket.MAX_MULTIPLE);
        assertTrue("fixture: priced on the settled latent posts unheld, the college multiple would pass sqrt(1 + MOST_OF_POSTS) times the posts' own",
                settledLatent12.getBandMultiple(WageBand.COLLEGE) > rootCap12 * noLatent12.getBandMultiple(WageBand.COLLEGE));
        g12.repriceLabour();
        close("at the reprice the college latent posts read MOST_OF_POSTS of the posts it prices",
                wp12.getLatent(WageBand.COLLEGE), WantedPosts.MOST_OF_POSTS * now12[COLLEGE], 0);
        assertTrue("...held in place, so the caption and the save show the posts that were priced", sameBits(wp12.latent(), heldTo12));
        close("the college multiple is the twin month's on the new posts plus the held latent, against the supply of the posts that exist",
                lm12.getBandMultiple(WageBand.COLLEGE), heldLatent12.getBandMultiple(WageBand.COLLEGE), 1e-12);
        assertTrue("...above what the posts alone set", lm12.getBandMultiple(WageBand.COLLEGE) > noLatent12.getBandMultiple(WageBand.COLLEGE));
        assertTrue(String.format("...and at most sqrt(1 + MOST_OF_POSTS) = %.3f times it", rootCap12),
                lm12.getBandMultiple(WageBand.COLLEGE) <= rootCap12 * noLatent12.getBandMultiple(WageBand.COLLEGE) * (1 + 1e-12));
        double[] given = { 10, 20, 30, 40 };
        double[] pricedBare = new WantedPosts().pricedOn(given);
        assertTrue("with no latent posts pricedOn() is the posts unchanged, a copy", sameBits(pricedBare, given) && pricedBare != given);
        WantedPosts overBand = new WantedPosts();
        overBand.restore(new double[] { 50, 5, 50, 50 });
        double[] pricedOver = overBand.pricedOn(new double[] { -3, 20, 30, 40 });
        assertTrue("a band with no posts at the reprice (here under none) is priced on none of its latent posts",
                overBand.getLatent(WageBand.NONE) == 0 && pricedOver[0] == -3);
        assertTrue("...a latent under the cap is left as it was, and one over it is held to MOST_OF_POSTS of the posts",
                overBand.getLatent(WageBand.DIPLOMA) == 5 && pricedOver[1] == 25
                        && overBand.getLatent(WageBand.COLLEGE) == WantedPosts.MOST_OF_POSTS * 30 && pricedOver[2] == 30 + overBand.getLatent(WageBand.COLLEGE)
                        && overBand.getLatent(WageBand.UNIVERSITY) == Math.min(50, WantedPosts.MOST_OF_POSTS * 40));

        /* ============ 13. a band that asked nothing is priced as it was (LD2) ============ */
        out.println("\n--- a band that asked nothing is priced as it was (LD2) ---");

        // The fill carries a band's surplus down to the bands below (PopulationManager.fillByBand()). LD1 filled against
        // the posts plus the refused ones, so the college band's refused posts took college workers the diploma band
        // would have been carried, and the diploma band, which asked nothing, was priced up. Here the college band has
        // 300 more workers than posts, and the diploma band, with few of its own, is supplied mostly by them.
        Game g13 = atBase(city(root, "carry"));
        PopulationManager pm13 = g13.getPopulationManager();
        LabourMarket lm13 = g13.getLabourMarket();
        WantedPosts wp13 = g13.getWantedPosts();
        long[] jobs13 = new long[JobType.values().length];
        jobs13[JobType.DIPLOMA.ordinal()] = 400;
        jobs13[JobType.COLLEGE_ENGINEERING.ordinal()] = 400;
        hands(g13, WORKFORCE, new double[] { 0, 200, 700, 0 }, jobs13);
        double[] own13 = pm13.workforceByBand(), existing13 = pm13.staffablePostsByBand(), supply13 = pm13.supplyByBand();
        assertTrue("fixture: the college band has more workers than posts, so its surplus is carried down",
                own13[COLLEGE] > existing13[COLLEGE]);
        assertTrue("fixture: the diploma band has posts, and its supply is mostly workers carried down to it",
                existing13[DIPLOMA] > 0 && supply13[DIPLOMA] - own13[DIPLOMA] > own13[DIPLOMA]);
        wp13.beginMonth();
        wp13.ask(new WantedPosts.Ask(g13.getSectors().manufacturing().key(), MACHINE_WORKS, new double[] { 0, 0, 100, 0 }, Double.POSITIVE_INFINITY));
        g13.settleWantedPosts();
        double[] latent13 = wp13.latent();
        assertTrue("fixture: the college band asked and got latent posts, the diploma band asked nothing and has none",
                latent13[COLLEGE] == 100 && latent13[DIPLOMA] == 0);
        double[] seen13 = plus(existing13, latent13);
        LabourMarket ld1 = monthOn(g13, seen13), alone13 = monthOn(g13, existing13), ld2 = monthOn(g13, seen13, existing13);
        out.printf("   diploma band: supply %.0f against %.0f posts (%.0f of it carried down); multiple %.4f on the posts that exist, %.4f priced the LD1 way%n",
                supply13[DIPLOMA], existing13[DIPLOMA], supply13[DIPLOMA] - own13[DIPLOMA],
                alone13.getBandMultiple(WageBand.DIPLOMA), ld1.getBandMultiple(WageBand.DIPLOMA));
        assertTrue("fixture: priced the LD1 way (posts plus latent against the fill on them) the refused college posts absorb the carry and lift the diploma multiple",
                ld1.getBandMultiple(WageBand.DIPLOMA) > alone13.getBandMultiple(WageBand.DIPLOMA));
        assertTrue("fixture: the diploma band stands on the live part of the curve, off its floor and cap",
                alone13.getBandMultiple(WageBand.DIPLOMA) > LabourMarket.MIN_MULTIPLE && ld1.getBandMultiple(WageBand.DIPLOMA) < LabourMarket.MAX_MULTIPLE);
        g13.repriceLabour();
        close("the diploma band, which asked nothing, is priced on the posts that exist alone, to the bit",
                lm13.getBandMultiple(WageBand.DIPLOMA), alone13.getBandMultiple(WageBand.DIPLOMA), 0);
        close("the college band is priced on its posts plus its refused ones against the supply of the posts that exist",
                lm13.getBandMultiple(WageBand.COLLEGE),
                LabourMarket.multipleAt((existing13[COLLEGE] + latent13[COLLEGE]) / Math.max(supply13[COLLEGE], 1)), 1e-12);
        close("...which is the twin month's", lm13.getBandMultiple(WageBand.COLLEGE), ld2.getBandMultiple(WageBand.COLLEGE), 1e-12);
        assertTrue(String.format("...above what its posts alone set, and at most sqrt(1 + MOST_OF_POSTS) = %.3f times it", Math.sqrt(1 + WantedPosts.MOST_OF_POSTS)),
                lm13.getBandMultiple(WageBand.COLLEGE) > alone13.getBandMultiple(WageBand.COLLEGE)
                        && lm13.getBandMultiple(WageBand.COLLEGE) <= Math.sqrt(1 + WantedPosts.MOST_OF_POSTS) * alone13.getBandMultiple(WageBand.COLLEGE) * (1 + 1e-12));

        /* ============ 14. a band closed to migrants asks nothing (LD2) ============ */
        out.println("\n--- a band closed to migrants asks nothing (LD2) ---");

        // The wage is meant to bring migrants (Migration.reach()): a band no migrant can come for (Migration.admits())
        // bids nothing, though the hold is still an ask and the bands open to migrants still bid.
        Game closed14 = shortCollege(root, "closed");
        closed14.getMigration().holdArrivals(WageBand.COLLEGE);
        assertTrue("fixture: the college band is closed to migrants and the university band is not",
                !closed14.getMigration().admits(WageBand.COLLEGE) && closed14.getMigration().admits(WageBand.UNIVERSITY));
        held(closed14);
        WantedPosts wp14 = closed14.getWantedPosts();
        out.printf("   college closed: latent posts %s%n", java.util.Arrays.toString(wp14.latent()));
        assertTrue("the hold is still counted as an ask, for the bands that are open", wp14.getAsked() == 1 && wp14.getHolds() == 1);
        assertTrue("the closed college band settles to no latent posts", wp14.getLatent(WageBand.COLLEGE) == 0);
        assertTrue("...while the university band, still open to migrants, bids", wp14.getLatent(WageBand.UNIVERSITY) > 0);
        Game open14 = heldCity(root, "open");
        assertTrue("fixture, from the other side: the same city with no hold admits migrants to the college band",
                open14.getMigration().admits(WageBand.COLLEGE));
        assertTrue("...and its college band settles to latent posts", open14.getWantedPosts().getLatent(WageBand.COLLEGE) > 0);
        double[] existing14 = closed14.getPopulationManager().staffablePostsByBand();
        LabourMarket noLatent14 = monthOn(closed14, existing14);
        closed14.repriceLabour();
        close("the closed college band is priced on the posts that exist alone",
                closed14.getLabourMarket().getBandMultiple(WageBand.COLLEGE), noLatent14.getBandMultiple(WageBand.COLLEGE), 1e-12);
        WantedPosts direct14 = new WantedPosts();
        direct14.beginMonth();
        direct14.ask(new WantedPosts.Ask("s", "b", new double[] { 5, 5, 5, 5 }, Double.POSITIVE_INFINITY));
        double[] every = { 100, 100, 100, 100 };
        direct14.settle(every, every, new double[] { 1, 1, 1, 1 }, new boolean[] { true, true, false, true });
        assertTrue("settle() given the bands open to migrants leaves a closed band no latent posts and the others theirs",
                direct14.getLatent(WageBand.COLLEGE) == 0 && direct14.getLatent(WageBand.NONE) > 0
                        && direct14.getLatent(WageBand.DIPLOMA) > 0 && direct14.getLatent(WageBand.UNIVERSITY) > 0);
        direct14.settle(every, every, new double[] { 1, 1, 1, 1 });
        double[] threeArguments = direct14.latent();
        assertTrue("...and the three-argument settle, for a direct call, leaves every band open", threeArguments[COLLEGE] > 0);
        direct14.settle(every, every, new double[] { 1, 1, 1, 1 }, null);
        assertTrue("...the same as a null for open", sameBits(direct14.latent(), threeArguments));

        out.println();
        if (fails == 0) {
            out.println("The posts the gate refused are priced into their band's wage, as far as the business can pay and never past the band's own posts again.");
        } else {
            out.println(fails + " check(s) failed.");
            System.exit(1);
        }
    }
}
