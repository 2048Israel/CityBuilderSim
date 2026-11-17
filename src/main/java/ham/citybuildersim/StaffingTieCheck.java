package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.DoublePredicate;

/**
 * The harness for TQ1's tie rule at the staffing gate: a planner whose buildings the gate refused hands WantedPosts one hold
 * (LD1), and where two of its refusals stand at the same share the hold keeps both, Game.noteStaffingHold() notes each, and
 * each asks for its part of its posts (Sector.Staffing.held(), WantedPosts.Ask.part()). Not part of the game.
 *
 * WHY. On main 0.9.129 the thirteen planners each wrote a strict {@code >} and so the first template they met kept every tie.
 * In a city with no spare, every template at or below the diploma band shares drawn/posts, snapped to DRAWN_SHARE_GRID (GW1,
 * docs/queue/GW1-notes.md, section 5), so the refused templates tie exactly, and the order a sector lists its buildings in
 * decided which one the wage was bid for: measured on that default playtest, a tie decided 638 of its 3,502 holds, 278 of
 * Construction's 305 (the Builders' Yard's 20 posts priced, never the Construction Depot's 50), 226 of Restaurants' 467 and 103
 * of Manufacturing's 522, mostly in the founding. Before the snap float noise broke the tie as it fell; shared is that noise's
 * expectation, and nothing a sector lists first decides it. Nothing here moves a premise to get green: the tie is found in a
 * city the game plays, and every figure asserted against comes from the model's own calls.
 *
 * What this has to prove:
 *   1. THE FIXTURE CAUSES THE TIE: in a fresh city played month by month the gate refuses two templates of one sector at the
 *      same share to the bit and they differ in posts: the founding's first tie (no spare, no draw, a share of 0), and the
 *      first at a share above 0 and under Sector.MIN_STAFFABLE_TO_ORDER (one the draw filled part of).
 *   2. THE ONE PLACE KEEPS BOTH, WHICHEVER IS MET FIRST: held(a, b) and held(b, a) both carry both templates, each once; none
 *      so far keeps the one; a refusal at a larger share is kept alone, whenever it is met; one at a smaller share leaves the two.
 *   3. WANTEDPOSTS PRICES THE TWO ALIKE, WHICHEVER IS FIRST: the same asks in the same order and the same latent posts to the
 *      bit; one tied hold counted once and each building counted besides; each building's outcome is the one it has alone and
 *      each ask is its ask alone divided by the number tied, so the posts asked are the mean of the two alone.
 *   4. THE MONTH'S OWN PLANNING HANDS THE TIE IN, AND HOW OFTEN: in a city played ten years the tie fires, every month's
 *      holds are its crews', its unpaid and its asks, a tie's buildings each count, and in some month one sector's asks name two
 *      of its buildings.
 */
public class StaffingTieCheck {

    static int fails = 0;
    static PrintStream out, quiet;

    /** The most months a fresh city is played to find a tie at the gate (section 1). */
    static final int MONTHS_TO_A_TIE = 60;

    /** The months of the city section 4 plays, ten years as WantedPostsCheck's. */
    static final int PLAYED_MONTHS = 120;

    /** How many buildings share a tie that section 3 finds: the two of the fixture. */
    static final int TIED_WAYS = 2;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-110s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static GameFiles filesFor(Path root, String name) {
        return new GameFiles(root.resolve(name), root.resolve("no-legacy"));
    }

    /** A fresh city, currency and world pinned, land by fiat, no wait read for an order's interest (WantedPostsCheck's). */
    static Game city(Path root, String name) {
        Game g = new Game(filesFor(root, name));
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
     * No wait read for an order's interest (BA1), as WantedPostsCheck's cities: a building nobody can build carries no
     * interest at any wage, and what the posts are priced at is this harness's subject, not the wait.
     */
    static void noWait(Game g) {
        g.getBusinessInvestment().setOrderWaitOutput(null);
    }

    /** One month, quietly. */
    static void month(Game g) {
        System.setOut(quiet);
        try { g.simulateMonths(1); } finally { System.setOut(out); }
    }

    /** A building's posts, all bands. */
    static double total(Sector.Staffing s) {
        double n = 0;
        for (double d : s.wanted()) n += d;
        return n;
    }

    /** Two refusals of one sector at the same share to the bit, in the order the sector lists them, and the month they were found in. */
    record Tie(int month, Sector sector, Sector.Staffing first, Sector.Staffing second) {
        String describe() {
            return String.format("month %d, %s: %s (id %d, %.0f posts) and %s (id %d, %.0f posts), both at a share of %.9f",
                    month, sector.key(), first.template().getName(), first.template().getId(), total(first),
                    second.template().getName(), second.template().getId(), total(second), first.share);
        }
    }

    /** A city and the tie found in it. */
    record Found(Game g, Tie tie) { }

    /**
     * The first tie in this city's sectors, in the order the sectors and their templates are listed: two templates the
     * sector's gate refuses at the same share to the bit, whose posts differ in total, at a share this test takes.
     */
    static Tie tieIn(Game g, DoublePredicate shareTaken, int month) {
        for (Sector sector : g.getSectors().all()) {
            if (!sector.ordersOnlyWhatItCanStaff()) continue;
            List<Sector.Staffing> refused = new ArrayList<>();
            for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(sector.key())) {
                Sector.Staffing s = sector.staffing(t);
                if (!s.passes()) refused.add(s);
            }
            for (int i = 0; i < refused.size(); i++) {
                for (int j = i + 1; j < refused.size(); j++) {
                    Sector.Staffing a = refused.get(i), b = refused.get(j);
                    if (a.share == b.share && shareTaken.test(a.share) && total(a) != total(b)) return new Tie(month, sector, a, b);
                }
            }
        }
        return null;
    }

    /** A fresh city played, month by month, until it holds a tie this test takes: the tie, or null at the dial. */
    static Found play(Path root, String name, DoublePredicate shareTaken) {
        Game g = city(root, name);
        for (int m = 1; m <= MONTHS_TO_A_TIE; m++) {
            month(g);
            Tie tie = tieIn(g, shareTaken, m);
            if (tie != null) return new Found(g, tie);
        }
        return new Found(g, null);
    }

    /** The shares a tie may stand at: any (the founding's), or above 0 and under Sector.MIN_STAFFABLE_TO_ORDER (a share the draw filled part of). */
    static final DoublePredicate ANY_SHARE = share -> true;
    static final DoublePredicate A_SHARE_THE_DRAW_FILLED_PART_OF = share -> share > 0 && share < Sector.MIN_STAFFABLE_TO_ORDER;

    /** A refusal of this sector's at a share this test takes, other than the two tied, or null. */
    static Sector.Staffing otherRefusal(Game g, Tie tie, DoublePredicate taken) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(tie.sector().key())) {
            if (t == tie.first().template() || t == tie.second().template()) continue;
            Sector.Staffing s = tie.sector().staffing(t);
            if (!s.passes() && s.unfillable == null && taken.test(s.share)) return s;
        }
        return null;
    }

    /** A refusal of no template and no owner at this share: held() reads nothing else of it. */
    static Sector.Staffing refusalAt(double share) {
        int bands = WageBand.values().length;
        return new Sector.Staffing(share, new double[bands], new double[bands], new double[bands], new boolean[bands]);
    }

    static Set<Integer> idsOf(List<Sector.Staffing> alike) {
        Set<Integer> ids = new HashSet<>();
        for (Sector.Staffing s : alike) ids.add(s.template().getId());
        return ids;
    }

    /** The template ids of these two, as a set. */
    static Set<Integer> idsOf(Tie tie) {
        return Set.of(tie.first().template().getId(), tie.second().template().getId());
    }

    /** What a hold noted alone, or tied, came to in WantedPosts, and the latent posts a fresh WantedPosts settles its asks to. */
    record Noted(List<WantedPosts.Ask> asks, int holds, int crews, int unpaid, int asked, int tied, double[] latent) { }

    /**
     * Game.noteStaffingHold() of this hold, in a month begun, and what it came to: its asks, its counts, and the latent
     * posts of a fresh WantedPosts given those asks and settled against the city's own posts, supply and premiums
     * (WantedPostsCheck section 10's).
     */
    static Noted note(Game g, Sector.Staffing hold) {
        WantedPosts wp = g.getWantedPosts();
        wp.beginMonth();
        g.noteStaffingHold(hold);
        List<WantedPosts.Ask> asks = wp.asks();
        Noted noted = new Noted(asks, wp.getHolds(), wp.getCrewsHolds(), wp.getUnpaidHolds(), wp.getAsked(), wp.getTiedHolds(), settled(g, asks));
        wp.beginMonth();
        return noted;
    }

    /** The latent posts a fresh WantedPosts settles to, given these asks and the city's posts, supply and premiums. */
    static double[] settled(Game g, List<WantedPosts.Ask> asks) {
        WantedPosts fresh = new WantedPosts();
        fresh.beginMonth();
        for (WantedPosts.Ask a : asks) fresh.ask(a);
        PopulationManager people = g.getPopulationManager();
        double[] premiums = new double[WageBand.values().length];
        for (WageBand band : WageBand.values()) premiums[band.ordinal()] = g.getLabourMarket().bandPremium(band);
        fresh.settle(people.staffablePostsByBand(), people.supplyByBand(), premiums);
        return fresh.latent();
    }

    /** The asks alike: the same sector, building, posts per band and pay factor to the bit, in the same order. */
    static boolean sameAsks(List<WantedPosts.Ask> x, List<WantedPosts.Ask> y) {
        if (x.size() != y.size()) return false;
        for (int i = 0; i < x.size(); i++) {
            WantedPosts.Ask p = x.get(i), q = y.get(i);
            if (!p.sector().equals(q.sector()) || !p.building().equals(q.building())
                    || !Arrays.equals(p.posts(), q.posts()) || Double.compare(p.payFactor(), q.payFactor()) != 0) return false;
        }
        return true;
    }

    static String outcome(Noted n) {
        return n.crews() == 1 ? "held for the crews" : n.unpaid() == 1 ? "unpaid" : n.asked() == 1 ? "asks" : "nothing noted";
    }

    /** The posts these asks make in each band, summed. */
    static double[] sumByBand(List<WantedPosts.Ask> asks) {
        double[] sum = new double[WageBand.values().length];
        for (WantedPosts.Ask a : asks) for (int b = 0; b < sum.length && b < a.posts().length; b++) sum[b] += a.posts()[b];
        return sum;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());
        Path root = TempDirs.create("staffingtiecheck");

        Found founding = sectionOne(root, true);
        Found grown = sectionOne(root, false);
        sectionTwo(founding, grown);
        sectionThree(founding, grown);
        sectionFour(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails > 0 ? 1 : 0);
    }

    /* ============ 1. the fixture causes the tie ============ */

    /**
     * @param first the founding's first tie (any share) when true, else the first at a share the draw filled part of
     */
    static Found sectionOne(Path root, boolean first) {
        if (first) out.println("--- 1. the fixture causes the tie: a fresh city, played until the gate refuses two templates of a sector alike ---");
        Found f = play(root, first ? "founding" : "grown", first ? ANY_SHARE : A_SHARE_THE_DRAW_FILLED_PART_OF);
        String which = first ? "the founding's first tie (no spare, no draw)" : "the first tie at a share the draw filled part of";
        assertTrue("fixture: " + which + " is found within " + MONTHS_TO_A_TIE + " months", f.tie() != null);
        if (f.tie() == null) return f;
        Tie t = f.tie();
        out.println("   " + which + ": " + t.describe());
        assertTrue("...both templates are refused by their sector's gate", !t.first().passes() && !t.second().passes());
        assertTrue("...at the same share to the bit (" + t.first().share + ")", t.first().share == t.second().share);
        assertTrue("...which is under Sector.MIN_STAFFABLE_TO_ORDER (" + Sector.MIN_STAFFABLE_TO_ORDER + "), unless a band nobody can fill refused it",
                t.first().share < Sector.MIN_STAFFABLE_TO_ORDER || t.first().unfillable != null);
        assertTrue("...and they are two templates of the sector that differ in posts", t.first().template() != t.second().template()
                && t.sector().key().equals(t.first().template().getSector()) && total(t.first()) != total(t.second()));
        if (!first) assertTrue("...a share above 0 and the draw's, not the founding's", t.first().share > 0);
        return f;
    }

    /* ============ 2. the one place keeps both, whichever is met first ============ */

    static void sectionTwo(Found founding, Found grown) {
        out.println("\n--- 2. the one place keeps both, whichever is met first ---");
        if (founding.tie() == null || grown.tie() == null) {
            assertTrue("fixture: both ties were found in section 1", false);
            return;
        }
        keepsBoth("founding", founding);
        keepsBoth("grown", grown);
    }

    static void keepsBoth(String label, Found f) {
        Tie tie = f.tie();
        Sector.Staffing a = tie.first(), b = tie.second();
        Sector.Staffing ab = Sector.Staffing.held(a, b), ba = Sector.Staffing.held(b, a);
        assertTrue(label + ": held(a, b) carries both templates, each once, the one it kept first of them",
                ab.alike().size() == 2 && idsOf(ab.alike()).equals(idsOf(tie)) && ab.alike().get(0).template() == a.template());
        assertTrue(label + ": held(b, a) carries the same two, the one it kept first of them",
                ba.alike().size() == 2 && idsOf(ba.alike()).equals(idsOf(tie)) && ba.alike().get(0).template() == b.template());
        assertTrue(label + ": the shares of the hold are still the tie's", ab.share == a.share && ba.share == a.share);
        assertTrue(label + ": a refusal that tied with none lists itself alone", a.alike().size() == 1 && a.alike().get(0) == a);
        Sector.Staffing none = Sector.Staffing.held(null, a);
        assertTrue(label + ": with none kept so far the first refusal met is the hold, alone", none == a && none.alike().size() == 1);

        // A refusal at a larger share: one of the sector's own where there is one, else one made at a share between the tie and the gate's.
        Sector.Staffing larger = otherRefusal(f.g(), tie, share -> share > a.share);
        String largerFrom = "the sector's own";
        if (larger == null) {
            larger = refusalAt(a.share + (Sector.MIN_STAFFABLE_TO_ORDER - a.share) / 2);
            largerFrom = "made, a share between the tie's and MIN_STAFFABLE_TO_ORDER";
        }
        out.printf("   %s: a refusal at a larger share (%.6f), %s%n", label, larger.share, largerFrom);
        assertTrue(label + ": a larger share met before the tie is kept alone",
                alone(Sector.Staffing.held(Sector.Staffing.held(larger, a), b), larger));
        assertTrue(label + ": ...met between the two", alone(Sector.Staffing.held(Sector.Staffing.held(a, larger), b), larger));
        assertTrue(label + ": ...and met after the tie", alone(Sector.Staffing.held(ab, larger), larger)
                && alone(Sector.Staffing.held(ba, larger), larger));

        if (a.share > 0) {
            Sector.Staffing smaller = otherRefusal(f.g(), tie, share -> share < a.share);
            String smallerFrom = "the sector's own";
            if (smaller == null) {
                smaller = refusalAt(a.share / 2);
                smallerFrom = "made, half the tie's share";
            }
            out.printf("   %s: a refusal at a smaller share (%.6f), %s%n", label, smaller.share, smallerFrom);
            assertTrue(label + ": a smaller share met after the tie leaves the two",
                    sameTwo(Sector.Staffing.held(ab, smaller), tie) && sameTwo(Sector.Staffing.held(ba, smaller), tie));
            assertTrue(label + ": ...met before the tie, the first of the two then the other still tie",
                    sameTwo(Sector.Staffing.held(Sector.Staffing.held(smaller, a), b), tie)
                            && sameTwo(Sector.Staffing.held(Sector.Staffing.held(smaller, b), a), tie));
        } else {
            out.printf("   %s: a share of 0 has no smaller refusal to meet%n", label);
        }
    }

    static boolean alone(Sector.Staffing held, Sector.Staffing expected) {
        return held == expected && held.alike().size() == 1 && held.alike().get(0) == expected;
    }

    static boolean sameTwo(Sector.Staffing held, Tie tie) {
        return held.alike().size() == 2 && idsOf(held.alike()).equals(idsOf(tie));
    }

    /* ============ 3. WantedPosts prices the two alike, whichever is first ============ */

    static void sectionThree(Found founding, Found grown) {
        out.println("\n--- 3. WantedPosts prices the two alike, whichever is first ---");
        if (founding.tie() == null || grown.tie() == null) {
            assertTrue("fixture: both ties were found in section 1", false);
            return;
        }
        priced("founding", founding);
        priced("grown", grown);
    }

    static void priced(String label, Found f) {
        Game g = f.g();
        Tie tie = f.tie();
        Sector.Staffing a = tie.first(), b = tie.second();
        Noted ab = note(g, Sector.Staffing.held(a, b)), ba = note(g, Sector.Staffing.held(b, a));
        Noted onlyA = note(g, a), onlyB = note(g, b);
        out.printf("   %s: %s; alone, %s %s and %s %s; the two tied: %d asks, %d holds counted, %d tied%n", label, tie.describe(),
                a.template().getName(), outcome(onlyA), b.template().getName(), outcome(onlyB), ab.asked(), ab.holds(), ab.tied());

        assertTrue(label + ": fixture: each building alone is one hold, so the pair is two",
                onlyA.holds() == 1 && onlyB.holds() == 1 && onlyA.tied() == 0 && onlyB.tied() == 0);
        assertTrue(label + ": fixture: at least one of the two would pay for its posts, so there is an ask to share", onlyA.asked() + onlyB.asked() > 0);
        assertTrue(label + ": the same asks, in the same order, whichever refusal the planner met first",
                sameAsks(ab.asks(), ba.asks()));
        assertTrue(label + ": ...and the same latent posts to the bit", Arrays.equals(ab.latent(), ba.latent()));
        assertTrue(label + ": ...the same counts of crews' holds, unpaid and asks",
                ab.crews() == ba.crews() && ab.unpaid() == ba.unpaid() && ab.asked() == ba.asked());
        assertTrue(label + ": one hold the gate tied, and each of its " + TIED_WAYS + " buildings counted among the month's holds",
                ab.tied() == 1 && ba.tied() == 1 && ab.holds() == TIED_WAYS && ba.holds() == TIED_WAYS);
        assertTrue(label + ": each building's outcome in the tie is its outcome alone: the crews', the unpaid and the asks add up",
                ab.crews() == onlyA.crews() + onlyB.crews() && ab.unpaid() == onlyA.unpaid() + onlyB.unpaid()
                        && ab.asked() == onlyA.asked() + onlyB.asked());

        // Each tied ask is the ask of the same building alone, its posts one of the number tied of each band's, its pay factor its own.
        List<WantedPosts.Ask> alone = new ArrayList<>(onlyA.asks());
        alone.addAll(onlyB.asks());
        boolean eachOfItsOwn = true, eachPart = true;
        for (WantedPosts.Ask tied : ab.asks()) {
            WantedPosts.Ask own = null;
            for (WantedPosts.Ask one : alone) if (one.building().equals(tied.building())) own = one;
            eachOfItsOwn &= own != null && own.sector().equals(tied.sector()) && own.posts().length == tied.posts().length;
            if (own == null || own.posts().length != tied.posts().length) { eachPart = false; continue; }
            for (int band = 0; band < own.posts().length; band++) eachPart &= tied.posts()[band] == own.posts()[band] / TIED_WAYS;
            eachPart &= Double.compare(tied.payFactor(), own.payFactor()) == 0;
        }
        assertTrue(label + ": each tied ask is a building that asked alone, in the sector that asked", eachOfItsOwn && ab.asks().size() == alone.size());
        assertTrue(label + ": ...its posts exactly its ask alone divided by the " + TIED_WAYS + " tied, band by band, its pay factor its own", eachPart);
        double[] tiedSum = sumByBand(ab.asks()), aloneA = sumByBand(onlyA.asks()), aloneB = sumByBand(onlyB.asks());
        boolean mean = true;
        for (int band = 0; band < tiedSum.length; band++) {
            double expected = (aloneA[band] + aloneB[band]) / TIED_WAYS;
            mean &= Math.abs(tiedSum[band] - expected) <= 1e-12 * Math.max(1, Math.abs(expected));
        }
        assertTrue(label + ": the posts asked by band are the mean of the two alone", mean);
        out.printf("   %s: posts asked by band (none, diploma, college, university): tied %s; %s alone %s; %s alone %s%n", label,
                Arrays.toString(tiedSum), a.template().getName(), Arrays.toString(aloneA), b.template().getName(), Arrays.toString(aloneB));
        out.printf("   %s: latent posts the wage is priced on: tied %s; %s alone %s (what main's first-template rule priced); %s alone %s%n", label,
                Arrays.toString(ab.latent()), a.template().getName(), Arrays.toString(onlyA.latent()), b.template().getName(), Arrays.toString(onlyB.latent()));
        g.getWantedPosts().beginMonth();
    }

    /* ============ 4. the month's own planning hands the tie in, and how often ============ */

    static void sectionFour(Path root) {
        out.println("\n--- 4. the month's own planning hands the tie in, and how often ---");
        Game played = city(root, "played");
        int months = PLAYED_MONTHS, holds = 0, tied = 0, crews = 0, unpaid = 0, asked = 0, tieMonths = 0, monthsTwoInASector = 0;
        boolean counted = true, aTieCountsEachBuilding = true;
        Map<String, Integer> sectorMonths = new TreeMap<>(), askedBySector = new TreeMap<>();
        for (int m = 0; m < months; m++) {
            month(played);
            WantedPosts w = played.getWantedPosts();
            holds += w.getHolds();
            tied += w.getTiedHolds();
            crews += w.getCrewsHolds();
            unpaid += w.getUnpaidHolds();
            asked += w.getAsked();
            if (w.getTiedHolds() > 0) tieMonths++;
            counted &= w.getHolds() == w.getCrewsHolds() + w.getUnpaidHolds() + w.getAsked();
            aTieCountsEachBuilding &= w.getHolds() >= TIED_WAYS * w.getTiedHolds();
            // The month's asks by sector: the buildings each asked for.
            Map<String, Set<String>> buildings = new TreeMap<>();
            for (WantedPosts.Ask a : w.asks()) {
                buildings.computeIfAbsent(a.sector(), k -> new HashSet<>()).add(a.building());
                askedBySector.merge(a.sector(), 1, Integer::sum);
            }
            boolean two = false;
            for (Map.Entry<String, Set<String>> e : buildings.entrySet()) {
                if (e.getValue().size() >= 2) {
                    sectorMonths.merge(e.getKey(), 1, Integer::sum);
                    two = true;
                }
            }
            if (two) monthsTwoInASector++;
        }
        out.printf("   %d months: %,d staffing holds noted (a tied hold once for each of its buildings): %,d asked for their posts, %,d for the builders' crews, %,d unpaid; %,d holds the gate tied, in %d months%n",
                months, holds, asked, crews, unpaid, tied, tieMonths);
        out.printf("   %d months had a sector whose asks named two of its buildings; by sector (months with two, of asks all told):%n", monthsTwoInASector);
        for (Map.Entry<String, Integer> e : sectorMonths.entrySet()) {
            out.printf("      %-24s %4d months, %,d asks%n", e.getKey(), e.getValue(), askedBySector.getOrDefault(e.getKey(), 0));
        }
        assertTrue("the planners hand in holds the gate tied, in a city played " + months + " months", tied > 0 && tieMonths > 0);
        assertTrue("...each month's holds are its crews', its unpaid and its asks", counted);
        assertTrue("...a hold the gate tied counts each of its buildings, so a month's holds are at least " + TIED_WAYS + " for each tied", aTieCountsEachBuilding);
        assertTrue("...and in some month one sector's asks name two of its buildings", monthsTwoInASector > 0);
    }
}
