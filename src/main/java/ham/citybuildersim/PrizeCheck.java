package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;
import ham.citybuildersim.ResearchCatalog.Size;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A prize pays only for a result, a Research Lab goes for the cheapest problem the purse pays for, and the schools' share of a purse buys posts
 * (R4, slice A: Research; docs/studies/research-design.md 5.3, 1.5, 5.4).
 *
 * WHY. A grant pays for the work whether it finds anything or not; a prize pays only for the find, and the whole point of it (5.3) is WHO does the
 * work: labs do not spread as the city's researchers do, they go for the cheapest problem a team could finish before the world, and the
 * purse is split by who did the work. Every claim here is a statement about a month and a threshold ("the prize is at least the remaining work
 * times a researcher-year's cost": one hair under it a lab stays away), so each threshold is caused from both sides and held to Research's own
 * constants and functions and never to a number copied from the study's formula; and a wrong order in the month (the chase computed after the
 * work, the purse read before the find) moves a find by a year and no assertion elsewhere would see it. Sections 1 to 10 (slice A) are the
 * pure model, which has to be right before Game meets it; sections 11 to 17 (slice B) are Game meeting it.
 *
 * What this has to prove:
 *
 *   1. THE DIALS. A prize is an amount, never below nothing, none by default, set for its field alone; a lab's jobs are LAB_POSTS posts' jobs;
 *      a reform of the money scales the prizes and the pots and nothing else new.
 *   2. THE PURSE'S RULE. A purse pays for a problem when the prize is at least the remaining work times twelve times the post's cost: true at
 *      exactly that and false a hair under; the remaining work floors at REMAINING_DUST; a team at its limit finishes before the world on this
 *      side of history's month less its months to finish and not on that.
 *   3. THE CHASE. Only open problems that pass both rules, least remaining first, ties in the catalogue's order; empty for a prize of 0, for
 *      no wages; a known problem and one whose science does not exist yet or whose need is unknown never.
 *   4. LABS ON THE TARGET. With and without labs the city's shares are the same; labs fill the cheapest first up to the team cap less the city's
 *      share, then the next; the rest is idle; the piles gain (share + lab) / 12 and the labs' part is kept apart.
 *   5. THE PURSE. A find with both kinds of work splits the prize by the work piled; all of it goes to the schools when no lab worked on the
 *      problem; none with a prize of 0; the purse is listed in its month only; toLabs sums the field's; the pot gains the schools' share; a
 *      world arrival drops the labs' part of a pile.
 *   6. THE POT BUYS POSTS. The strike buys posts for the grant and the pot, remembers the grant's alone; the bill's draw is the difference at
 *      the month's wages and fill, never more than the pot; a post nobody fills draws nothing.
 *   7. INVESTORS. Labs are wanted while the n-th would keep half its researchers at work, from both sides of each threshold; the room is the
 *      chase list's, less the city's last shares; the target is the chase's first and the stake its months of a lab's pay; none without a chase.
 *   8. IDLE LABS. The months a field has a lab too many are counted and zeroed as the rule says, from both sides of the threshold; labs with
 *      nothing to chase are all idle; a field with no lab counts none; idleMonth() counts as month() does.
 *   9. THE SAVE. A run of hundreds of months saved midway and carried on finds the same codes in the same months, with the same piles, lab piles,
 *      pots, idle counts and purses, as the unbroken one; a malformed entry is skipped; reset() clears everything new; isEmpty() sees each.
 *  10. NO PRIZE, NO CHANGE. With no labs the work is the study's own R3 month (a reference written here), whatever the prize; with a prize of 0
 *      labs sit idle and change nothing; nobody wants a lab.
 *
 * R4, slice B: the labs in the city (Game, the templates, the treasury's prizes line, the inbox, the save), on villages founded the 1900 way with a
 * University (DiscoveryCheck's fixtures) played to the month the model first wants a lab:
 *
 *  11. THE LABS ARE BUILDINGS. Four templates in the built-in definitions and in buildings.json, one a field, named, ids 148 to 151, in Business
 *      Services, making nothing and needing no licence, with Research.labJobs() job type by job type and the Engineering Services Office's figures
 *      times the study's 12 / 10.9; a "researches" that is no field is reported and read as none; Business Services' plan is never a lab nor held for
 *      the staff of one, in a 1900 village, a default one and one with no hands.
 *  12. INVESTORS BUILD A LAB. At the prize that just makes the model want one a lab is on site within a month, the owners' raise recorded and no loan;
 *      a hair under, none; a default-way village with the same prize, none; at most one a field a month, with two wanted.
 *  13. LABS AT WORK. The month's lab researchers are the labs' posts' filled researchers at the month's fill, and their work lands on the target.
 *  14. THE PURSE PAID. In the month of a find the treasury's prizes line pays exactly the labs' share, Business Services' books receive the same
 *      figure and close, the audit closes, the national accounts carry it with the subsidies, the schools' share is in the pot and the inbox's notice
 *      tells it; past the treasury's room the rest is owed as arrears.
 *  15. IDLE LABS ARE SHED. A lab with nothing to chase stands for exactly LAB_IDLE_MONTHS months of play and is sold back the month after; not for a
 *      sector a harness holds.
 *  16. THE SAVE. The new keys are written and read back; a loaded city and the one saved play two years alike; a city with only a prize writes the key.
 *  17. A CITY FOUNDED THE DEFAULT WAY orders nothing, pays nothing and has no lab, whatever the prizes; a reform of the money scales the prize, the
 *      pot and the month's prize paid.
 */
public class PrizeCheck {

    static int fails;
    static int passes;

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    /** The same, with what was found printed under a failure. */
    static void check(String label, boolean ok, String found) {
        check(label, ok);
        if (!ok && found != null) System.out.println("       found: " + found);
    }

    static void near(String label, double actual, double expected, double tolerance) {
        check(label, Math.abs(actual - expected) <= tolerance, String.format("%.9g against %.9g (tolerance %g)", actual, expected, tolerance));
    }

    /* ----- the fixtures: the shipped catalogue, the game's wages ----- */

    static final ResearchCatalog CAT = ResearchCatalog.get();
    static final long SEED = 20261010L;

    /** A prize that covers any problem there is: the chase is then ruled by the world's date alone. */
    static final double BIG = 1e12;

    /** The wages by JobType ordinal, the game's (PayTier.getMonthlyWage()), times `times`. */
    static double[] wages(double times) {
        double[] w = new double[JobType.values().length];
        for (JobType type : JobType.values()) w[type.ordinal()] = times * PayTier.of(type).getMonthlyWage();
        return w;
    }

    static final double[] W = wages(1);

    /** Every job type filled to `share`. */
    static double[] fill(double share) {
        double[] f = new double[JobType.values().length];
        Arrays.fill(f, share);
        return f;
    }

    static final double[] FULL = fill(1);

    static Entry of(String code) { return CAT.entry(code); }

    static int lastWorldMonth() {
        int last = 0;
        for (Entry e : CAT.entries()) last = Math.max(last, e.worldMonth());
        return last;
    }

    static double cost(Field f) { return Research.postCost(f, W); }

    /** The prize that just covers a problem with that pile: its remaining work times twelve times the post's cost, by Research's own statics. */
    static double cover(Entry e, double pile) {
        return Research.remaining(e, pile) * Research.MONTHS_A_YEAR * cost(e.field());
    }

    /** The prize that covers a problem of that much typical work, untouched. */
    static double prizeFor(Field f, double work) {
        return work * Research.MONTHS_A_YEAR * cost(f);
    }

    static EnumMap<Field, Double> map(Field f, double v) {
        EnumMap<Field, Double> m = new EnumMap<>(Field.class);
        m.put(f, v);
        return m;
    }

    static EnumMap<Field, Integer> counts(Field f, int n) {
        EnumMap<Field, Integer> m = new EnumMap<>(Field.class);
        m.put(f, n);
        return m;
    }

    static int indexOf(Entry e) { return CAT.entries().indexOf(e); }

    static List<String> codes(List<Entry> list) {
        List<String> out = new ArrayList<>();
        for (Entry e : list) out.add(e.code());
        return out;
    }

    /** One city's research month by month, in the game's order: the posts are struck, the pots pay their bill, then the month's work is done. */
    static final class Sim {
        final Research r = new Research();
        final long seed;
        final EnumMap<Field, Double> city = new EnumMap<>(Field.class), room = new EnumMap<>(Field.class), labPeople = new EnumMap<>(Field.class);
        final EnumMap<Field, Integer> labs = new EnumMap<>(Field.class);
        double[] wages = W, fill = FULL;
        double drawn;
        // what stood before the month's work: the piles, the labs' part, the pots, the chase of each field
        Map<String, Double> pilesBefore = new TreeMap<>(), labPilesBefore = new TreeMap<>();
        final EnumMap<Field, Double> potBefore = new EnumMap<>(Field.class);
        final EnumMap<Field, List<Entry>> chaseBefore = new EnumMap<>(Field.class);

        Sim(long seed) { this.seed = seed; }

        Sim city(Field f, double n) { city.put(f, n); return this; }

        Sim labs(Field f, int n, double people) { labs.put(f, n); labPeople.put(f, people); return this; }

        Sim room(Field f, double n) { room.put(f, n); return this; }

        void step(int m) {
            r.strikePosts(room, wages);
            drawn = r.drawPots(wages, fill);
            pilesBefore = r.piles();
            labPilesBefore = r.labPilesForSave();
            for (Field f : Field.values()) {
                potBefore.put(f, r.pot(f));
                chaseBefore.put(f, r.chase(f, m, wages));
            }
            r.month(m, seed, city, labs, labPeople, wages);
        }

        void run(int from, int to) {
            for (int m = from; m <= to; m++) step(m);
        }
    }

    /** Engineering and medicine with labs, grants, prizes and posts to buy: the city the save and the invariants are run on. */
    static Sim standard(long seed) {
        Sim s = new Sim(seed);
        s.city(Field.ENGINEERING, 15).labs(Field.ENGINEERING, 1, 30).room(Field.ENGINEERING, 40);
        s.city(Field.MEDICINE, 8).labs(Field.MEDICINE, 1, 60).room(Field.MEDICINE, 40);
        s.r.setGrant(Field.ENGINEERING, 3.4 * cost(Field.ENGINEERING));
        s.r.setGrant(Field.MEDICINE, 2.2 * cost(Field.MEDICINE));
        s.r.setPrize(Field.ENGINEERING, prizeFor(Field.ENGINEERING, 150));
        s.r.setPrize(Field.MEDICINE, prizeFor(Field.MEDICINE, 400));
        return s;
    }

    /** A city that has made one find with a prize and no lab: its schools' pot is full. Engineering, 400 researchers. */
    static Sim potted() {
        Sim s = new Sim(SEED).city(Field.ENGINEERING, 400);
        s.r.setPrize(Field.ENGINEERING, prizeFor(Field.ENGINEERING, 20));
        for (int m = 1; m <= 200 && s.r.pot(Field.ENGINEERING) <= 0; m++) s.step(m);
        return s;
    }

    /** The invariants of a month's step, found broken; empty when none. Run after Sim.step(m). */
    static List<String> invariants(Sim s, int m) {
        List<String> faults = new ArrayList<>();
        Research r = s.r;
        for (Entry e : CAT.entries()) {
            String code = e.code();
            double city = r.cityShare(code), lab = r.labShare(code);
            boolean foundNow = r.foundThisMonth().contains(code);
            boolean dropped = e.worldMonth() <= m && !r.foundHere(code);
            double pile0 = s.pilesBefore.getOrDefault(code, 0.0), lab0 = s.labPilesBefore.getOrDefault(code, 0.0);
            double pile = dropped || foundNow ? 0 : pile0 + (city + lab) / Research.MONTHS_A_YEAR;
            double labPile = dropped || foundNow ? 0 : lab0 + lab / Research.MONTHS_A_YEAR;
            if (Math.abs(r.pile(code) - pile) > 1e-9 * Math.max(1, pile)) faults.add(m + " " + code + " pile " + r.pile(code) + " not " + pile);
            if (Math.abs(r.labPile(code) - labPile) > 1e-9 * Math.max(1, labPile)) faults.add(m + " " + code + " lab pile " + r.labPile(code) + " not " + labPile);
            if (r.labPile(code) > r.pile(code) + 1e-9) faults.add(m + " " + code + " lab pile above the pile");
            if (city + lab > Research.teamCap(e) + 1e-9) faults.add(m + " " + code + " has " + (city + lab) + " past its cap " + Research.teamCap(e));
            if (lab > 0 && !s.chaseBefore.get(e.field()).contains(e)) faults.add(m + " " + code + " has lab researchers and is not on the chase");
        }
        for (Field f : Field.values()) {
            double cityOn = 0, labOn = 0;
            for (Entry e : CAT.entries()) {
                if (e.field() != f) continue;
                cityOn += r.cityShare(e.code());
                labOn += r.labShare(e.code());
            }
            if (Math.abs(cityOn + r.idle(f) - r.researchers(f)) > 1e-6) faults.add(m + " " + f + " city researchers " + r.researchers(f) + " are not " + cityOn + " at work and " + r.idle(f) + " idle");
            if (Math.abs(labOn + r.labsIdle(f) - r.labResearchers(f)) > 1e-6) faults.add(m + " " + f + " lab researchers " + r.labResearchers(f) + " are not " + labOn + " at work and " + r.labsIdle(f) + " idle");
        }
        return faults;
    }

    public static void main(String[] args) throws Exception {
        DiscoveryCheck.quiet = new PrintStream(OutputStream.nullOutputStream());
        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();
        sectionSix();
        sectionSeven();
        sectionEight();
        sectionNine();
        sectionTen();
        sectionEleven();
        sectionTwelve();
        sectionThirteen();
        sectionFourteen();
        sectionFifteen();
        sectionSixteen();
        sectionSeventeen();
        System.out.println(fails == 0 ? "PrizeCheck: all " + passes + " ok" : "PrizeCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /* ==================== 1. the dials ==================== */

    static void sectionOne() {
        System.out.println("--- 1. a prize is an amount, never below nothing, none by default; a lab is a hundred posts; a reform of the money scales prizes and pots ---");
        check("the dials are the design's: a lab has 100 posts, investors build while half a lab's researchers would have work, and a pile past its work keeps the model's 1e-6",
                Research.LAB_POSTS == 100 && Research.LAB_ROOM_SHARE == 0.5 && Research.REMAINING_DUST == 1e-6);
        check("a lab is shed after as many idle months as a building takes to retire (BusinessInvestment.RETIREMENT_LOSS_MONTHS)",
                Research.LAB_IDLE_MONTHS == BusinessInvestment.RETIREMENT_LOSS_MONTHS && Research.LAB_IDLE_MONTHS > 0);
        List<String> wrong = new ArrayList<>();
        for (Field f : Field.values()) {
            if (!Arrays.equals(Research.labJobs(f), Research.jobs(f, Research.LAB_POSTS))) wrong.add(f + " lab jobs are not LAB_POSTS posts' jobs");
            if (Math.abs(Research.filledResearchers(f, Research.LAB_POSTS, FULL) - Research.LAB_POSTS) > 1e-9) wrong.add(f + " lab has not LAB_POSTS researchers");
            long[] jobs = Research.labJobs(f);
            if (jobs[Research.supportType(f).ordinal()] != Math.round(Research.LAB_POSTS * Research.supportPerPost(f))) wrong.add(f + " lab support");
        }
        check("a lab's jobs are the jobs of LAB_POSTS posts of its field, LAB_POSTS researchers and the support they carry (the lab templates are written to equal them)", wrong.isEmpty(), wrong.toString());

        Research r = new Research();
        boolean none = true;
        for (Field f : Field.values()) none &= r.prize(f) == 0 && r.pot(f) == 0 && r.grantPosts(f) == 0 && r.labIdleMonths(f) == 0 && !r.labTooMany(f);
        check("no field has a prize, a pot, posts bought by the grant or a lab too many to begin with, and the research is empty", none && r.isEmpty());
        check("a prize is set for its field alone, and 0 is an amount",
                r.setPrize(Field.LAW, 250) && r.prize(Field.LAW) == 250 && r.prize(Field.MEDICINE) == 0 && r.setPrize(Field.LAW, 0) && r.prize(Field.LAW) == 0);
        r.setPrize(Field.LAW, 250);
        check("a negative prize, one not a number, an infinite one and a missing field are refused: the prize stays as it was",
                !r.setPrize(Field.LAW, -1) && !r.setPrize(Field.LAW, Double.NaN) && !r.setPrize(Field.LAW, Double.POSITIVE_INFINITY)
                        && !r.setPrize(Field.LAW, Double.NEGATIVE_INFINITY) && !r.setPrize(null, 5) && r.prize(Field.LAW) == 250);
        r.setGrant(Field.LAW, 40);
        check("a prize is not a grant: neither moves the other", r.grant(Field.LAW) == 40 && r.prize(Field.LAW) == 250 && r.setPrize(Field.LAW, 9) && r.grant(Field.LAW) == 40);

        Sim s = standard(SEED);
        s.run(1, 150);
        Research a = s.r;
        Map<String, Double> piles = a.piles(), labPiles = a.labPilesForSave(), shares = a.citySharesForSave();
        Map<String, Integer> found = a.foundHere(), idle = a.labIdleMonthsForSave(), postsSaved = a.postsForSave();
        double grantE = a.grant(Field.ENGINEERING), prizeE = a.prize(Field.ENGINEERING), prizeM = a.prize(Field.MEDICINE), potE = a.pot(Field.ENGINEERING), potM = a.pot(Field.MEDICINE);
        int grantPosts = a.grantPosts(Field.ENGINEERING), postsE = a.posts(Field.ENGINEERING);
        check("fixture: after 150 months of the standard city there are prizes, a pot, work piled by labs, shares, finds and an idle count to scale or leave alone",
                prizeE > 0 && prizeM > 0 && potE + potM > 0 && !labPiles.isEmpty() && !shares.isEmpty() && !found.isEmpty() && !idle.isEmpty() && grantPosts > 0 && postsE > grantPosts);
        a.redenominate(4);
        check("a reform of the money (times 4) scales the grant, the prizes and the pots, which are money",
                a.grant(Field.ENGINEERING) == 4 * grantE && a.prize(Field.ENGINEERING) == 4 * prizeE && a.prize(Field.MEDICINE) == 4 * prizeM
                        && a.pot(Field.ENGINEERING) == 4 * potE && a.pot(Field.MEDICINE) == 4 * potM);
        check("...and nothing that is not money: the posts, the posts the grant bought, the piles, the labs' part of them, the city's shares, the finds and the idle counts",
                a.posts(Field.ENGINEERING) == postsE && a.postsForSave().equals(postsSaved) && a.grantPosts(Field.ENGINEERING) == grantPosts && a.piles().equals(piles)
                        && a.labPilesForSave().equals(labPiles) && a.citySharesForSave().equals(shares) && a.foundHere().equals(found) && a.labIdleMonthsForSave().equals(idle));
    }

    /* ==================== 2. the purse's rule ==================== */

    static Entry firstOfSize(Size size) {
        for (Entry e : CAT.entries()) if (e.size() == size && e.work() > 0) return e;
        return null;
    }

    static void sectionTwo() {
        System.out.println("\n--- 2. the purse pays for a problem when the prize is the remaining work times twelve researcher-years' cost, and a team could finish before the world ---");
        for (Size size : Size.values()) {
            Entry e = firstOfSize(size);
            check("fixture: the catalogue has a " + size.name().toLowerCase() + " discovery", e != null);
            if (e == null) continue;
            Field f = e.field();
            double cost = cost(f), work = e.work();
            String name = e.code() + " (" + size.name().toLowerCase() + ", work " + e.work() + ")";
            near(name + ": its remaining work is its typical work, less the pile", Research.remaining(e, work / 4), work - work / 4, 1e-9);
            double edge = Research.remaining(e, 0) * Research.MONTHS_A_YEAR * cost;
            check(name + ": a prize of exactly the remaining work times twelve times the post's cost pays for it, one hair under does not, one hair over does",
                    Research.purseCovers(edge, cost, e, 0) && !Research.purseCovers(Math.nextDown(edge), cost, e, 0) && Research.purseCovers(Math.nextUp(edge), cost, e, 0));
            double half = Research.remaining(e, work / 2) * Research.MONTHS_A_YEAR * cost;
            check("...a pile of half the work halves the price: the prize that paid for the untouched problem pays for it, and the half price pays for the half-done one and not a hair under",
                    half < edge && Research.purseCovers(half, cost, e, work / 2) && !Research.purseCovers(Math.nextDown(half), cost, e, work / 2) && Research.purseCovers(edge, cost, e, work / 2));
            double dust = Research.REMAINING_DUST * Research.MONTHS_A_YEAR * cost;
            check("...a pile of the whole work or more leaves REMAINING_DUST, so a prize of nearly nothing pays for it and nothing at all does not",
                    Research.remaining(e, work) == Research.REMAINING_DUST && Research.remaining(e, work + 5) == Research.REMAINING_DUST && Research.remaining(e, work * 3) == Research.REMAINING_DUST
                            && Research.purseCovers(dust, cost, e, work + 5) && !Research.purseCovers(Math.nextDown(dust), cost, e, work + 5) && !Research.purseCovers(0, cost, e, work + 5));
            check("...a prize that is not above nothing (0, below nothing, not a number) pays for nothing, however cheap the problem",
                    !Research.purseCovers(0, cost, e, work + 5) && !Research.purseCovers(-1, cost, e, work + 5) && !Research.purseCovers(Double.NaN, cost, e, work + 5));
            check("...and a cost that is not a finite number above nothing (0, below nothing, not a number, infinite) pays for nothing, however big the prize",
                    !Research.purseCovers(BIG, 0, e, 0) && !Research.purseCovers(BIG, -3, e, 0) && !Research.purseCovers(BIG, Double.NaN, e, 0) && !Research.purseCovers(BIG, Double.POSITIVE_INFINITY, e, 0));
            near(name + ": a team at its limit takes the fewest years (" + Research.minYears(e) + ") to finish it untouched", Research.monthsToFinish(e, 0), Research.MONTHS_A_YEAR * Research.minYears(e), 1e-9);
            near("...half that with half the work piled, and next to nothing with all of it", Research.monthsToFinish(e, work / 2), Research.MONTHS_A_YEAR * Research.minYears(e) / 2, 1e-9);
            check("...but never none, since a pile past its work still has REMAINING_DUST left to do", Research.monthsToFinish(e, work * 2) > 0 && Research.monthsToFinish(e, work * 2) < 1e-6);

            for (double pile : new double[] {0, work / 2, work + 5}) {
                double toFinish = Research.monthsToFinish(e, pile);
                int latest = (int) Math.ceil(e.worldMonth() - toFinish) - 1;
                check("...history's month " + e.worldMonth() + " less " + String.format("%.2f", toFinish) + " months to finish (pile " + (int) pile + "): month " + latest + " is before the world, month " + (latest + 1) + " is not",
                        Research.beforeTheWorld(e, pile, latest) && !Research.beforeTheWorld(e, pile, latest + 1) && Research.beforeTheWorld(e, pile, latest - 50)
                                && !Research.beforeTheWorld(e, pile, e.worldMonth()) && !Research.beforeTheWorld(e, pile, e.worldMonth() + 10));
            }
        }
        List<String> faults = new ArrayList<>();
        for (Entry e : CAT.entries()) {
            double previous = Double.POSITIVE_INFINITY;
            for (double pile = 0; pile <= e.work() * 1.2; pile += e.work() / 16) {
                double price = cover(e, pile);
                if (price > previous + 1e-9) faults.add(e.code() + " costs more at " + pile);
                if (!Research.purseCovers(price, cost(e.field()), e, pile)) faults.add(e.code() + " not covered by its own price at " + pile);
                previous = price;
            }
        }
        check("over every discovery of the catalogue a bigger pile never costs more to cover, and its own price covers it", faults.isEmpty(), faults.size() > 5 ? faults.subList(0, 5).toString() : faults.toString());
    }

    /* ==================== 3. the chase ==================== */

    /** Two problems of one field with the same remaining work on the chase together, and the month. */
    record Tie(Field field, int month, Entry a, Entry b, List<Entry> list) { }

    static Tie findTie() {
        for (Field f : Field.values()) {
            Research r = new Research();
            r.setPrize(f, BIG);
            for (int m = 1; m <= lastWorldMonth(); m++) {
                List<Entry> list = r.chase(f, m, W);
                for (int i = 0; i + 1 < list.size(); i++) {
                    if (Research.remaining(list.get(i), 0) == Research.remaining(list.get(i + 1), 0)) return new Tie(f, m, list.get(i), list.get(i + 1), list);
                }
            }
        }
        return null;
    }

    static void sectionThree() {
        System.out.println("\n--- 3. the chase: the open problems the purse pays for and a team could finish before the world, cheapest first ---");
        List<String> bad = new ArrayList<>();
        int lists = 0, several = 0, nonEmpty = 0;
        for (Field f : Field.values()) {
            double cost = cost(f);
            for (int pass = 0; pass < 3; pass++) {
                Map<String, Double> piles = new HashMap<>();
                int i = 0;
                for (Entry e : CAT.entries()) {
                    if (e.field() != f) continue;
                    double share = pass == 0 ? 0 : pass == 1 ? 0.5 : (i++ % 4) * 0.2;
                    if (share > 0) piles.put(e.code(), e.work() * share);
                }
                Research r = new Research();
                r.restore(null, null, piles, null);
                for (int m = 1; m <= lastWorldMonth() + 2; m += 5) {
                    for (double work : new double[] {20, 100, 400, 1e9}) {
                        double prize = prizeFor(f, work);
                        r.setPrize(f, prize);
                        List<Entry> expected = new ArrayList<>();
                        for (Entry e : CAT.entries()) {
                            if (e.field() != f || !r.isOpen(e, m)) continue;
                            double pile = r.pile(e.code());
                            if (!(Research.purseCovers(prize, cost, e, pile) && Research.beforeTheWorld(e, pile, m))) continue;
                            int at = expected.size();
                            while (at > 0 && Research.remaining(expected.get(at - 1), r.pile(expected.get(at - 1).code())) > Research.remaining(e, pile)) at--;
                            expected.add(at, e);
                        }
                        List<Entry> got = r.chase(f, m, W);
                        lists++;
                        if (got.size() > 1) several++;
                        if (!got.isEmpty()) nonEmpty++;
                        if (!got.equals(expected)) bad.add(f + " month " + m + " prize for " + work + " pass " + pass + ": " + codes(got) + " not " + codes(expected));
                        for (int k = 1; k < got.size(); k++) {
                            double before = Research.remaining(got.get(k - 1), r.pile(got.get(k - 1).code())), after = Research.remaining(got.get(k), r.pile(got.get(k).code()));
                            if (before > after || (before == after && indexOf(got.get(k - 1)) > indexOf(got.get(k)))) bad.add(f + " month " + m + " out of order at " + got.get(k).code());
                        }
                    }
                }
            }
        }
        check("fixture: " + lists + " chase lists (4 fields, 3 states of the piles, every fifth month, 4 prizes), " + nonEmpty + " not empty and " + several + " of two or more", nonEmpty > 100 && several > 50);
        check("every chase is exactly the open problems that pass purseCovers() and beforeTheWorld() with today's piles, least remaining work first, ties in the catalogue's order",
                bad.isEmpty(), bad.size() > 3 ? bad.subList(0, 3) + " ..." : bad.toString());

        Tie t = findTie();
        check("fixture: two problems of one field with the same remaining work are on the chase together" + (t == null ? "" : " (" + t.a().code() + " and " + t.b().code() + " in month " + t.month() + ")"), t != null);
        if (t != null) {
            check("a tie keeps the catalogue's order: " + t.a().code() + " is before " + t.b().code() + " in the file and in the chase", indexOf(t.a()) < indexOf(t.b()) && t.list().indexOf(t.a()) < t.list().indexOf(t.b()));
            Research r = new Research();
            r.restore(null, null, Map.of(t.b().code(), 1.0), null);
            r.setPrize(t.field(), BIG);
            List<Entry> after = r.chase(t.field(), t.month(), W);
            check("...and one researcher-year of work piled on " + t.b().code() + " puts it ahead of " + t.a().code() + ": it has the less left", after.contains(t.a()) && after.contains(t.b()) && after.indexOf(t.b()) < after.indexOf(t.a()));

            Research fresh = new Research();
            Entry cheapest = t.list().get(0);
            fresh.setPrize(t.field(), cover(cheapest, 0));
            List<Entry> at = fresh.chase(t.field(), t.month(), W);
            fresh.setPrize(t.field(), Math.nextDown(cover(cheapest, 0)));
            List<Entry> under = fresh.chase(t.field(), t.month(), W);
            check("a prize of exactly the cheapest problem's price (" + cheapest.code() + ") puts it first on the chase, and one hair under takes it off",
                    !at.isEmpty() && at.get(0).equals(cheapest) && !under.contains(cheapest) && under.size() < at.size());
            fresh.setPrize(t.field(), BIG);
            Research known = new Research();
            known.restore(null, null, null, Map.of(cheapest.code(), t.month()));
            known.setPrize(t.field(), BIG);
            check("a problem that is known (found here) is never on the chase, whatever the prize", !known.chase(t.field(), t.month(), W).contains(cheapest) && fresh.chase(t.field(), t.month(), W).contains(cheapest));

            Research none = new Research();
            check("a prize of 0, the default, gives an empty chase though problems are open and wages are there", none.chase(t.field(), t.month(), W).isEmpty() && !none.openProblems(t.field(), t.month()).isEmpty());
            none.setPrize(t.field(), BIG);
            double[] noPay = new double[W.length], nan = W.clone();
            nan[Research.researcherA(t.field()).ordinal()] = Double.NaN;
            check("...as does a prize with no wages (none, too few, all nothing, the researcher's not a number): a post then costs no number",
                    none.chase(t.field(), t.month(), null).isEmpty() && none.chase(t.field(), t.month(), new double[3]).isEmpty() && none.chase(t.field(), t.month(), noPay).isEmpty()
                            && none.chase(t.field(), t.month(), nan).isEmpty() && !none.chase(t.field(), t.month(), W).isEmpty());
            none.setPrize(t.field(), 0);
            check("...and a prize set back to 0, and a missing field", none.chase(t.field(), t.month(), W).isEmpty() && none.chase(null, t.month(), W).isEmpty());
            check("...and the chase does not touch the research: no pile, no find, still empty", none.isEmpty() && none.piles().isEmpty());
        }

        // A problem is on the chase from the month its science exists, and not before; and not while a need is unknown.
        Entry plain = null;
        for (Entry e : CAT.entries()) {
            if (e.field() == Field.ENGINEERING && e.needs().isEmpty() && e.worldMonth() - Research.lagMonths(e.field()) >= 2) { plain = e; break; }
        }
        check("fixture: an engineering discovery with no need whose science exists after the first month", plain != null);
        if (plain != null) {
            int science = plain.worldMonth() - Research.lagMonths(plain.field());
            Research r = new Research();
            r.setPrize(Field.ENGINEERING, BIG);
            check(plain.code() + " is on the chase in month " + science + ", when its science exists, and not the month before", r.chase(Field.ENGINEERING, science, W).contains(plain) && !r.chase(Field.ENGINEERING, science - 1, W).contains(plain));
        }
        Entry blocked = null;
        String need = null;
        for (Entry e : CAT.entries()) {
            int open = Math.max(1, e.worldMonth() - Research.lagMonths(e.field()));
            if (e.needs().size() != 1 || open + Research.MONTHS_A_YEAR * Research.minYears(e) >= e.worldMonth()) continue;
            for (String n : e.needs()) if (CAT.worldMonth(n) > open && blocked == null) { blocked = e; need = n; }
        }
        check("fixture: a discovery whose need history has not dated when its science exists", blocked != null);
        if (blocked != null) {
            int month = Math.max(1, blocked.worldMonth() - Research.lagMonths(blocked.field()));
            Research r = new Research();
            r.setPrize(blocked.field(), BIG);
            Research found = new Research();
            found.restore(null, null, null, Map.of(need, month));
            found.setPrize(blocked.field(), BIG);
            check(blocked.code() + " needs " + need + ", which is not known in month " + month + ": not on the chase; found here, " + need + " is known and it is",
                    !r.chase(blocked.field(), month, W).contains(blocked) && found.chase(blocked.field(), month, W).contains(blocked));
        }
    }

    /* ==================== 4. labs on the target ==================== */

    /** A field and a month where three or more problems are on the chase and one more is open and is not. */
    record Spot(Field field, int month, List<Entry> chase, List<Entry> open) { }

    /** The first such month in which the chase's team caps whole have room for at least `labs` labs (labsFor()). */
    static Spot findSpot(int labs) {
        for (Field f : Field.values()) {
            Research r = new Research();
            r.setPrize(f, BIG);
            for (int m = 1; m <= lastWorldMonth(); m++) {
                List<Entry> chase = r.chase(f, m, W), open = r.openProblems(f, m);
                double room = 0;
                for (Entry e : chase) room += Research.teamCap(e);
                if (chase.size() >= 3 && open.size() > chase.size() && Research.labsFor(room) >= labs) return new Spot(f, m, chase, open);
            }
        }
        return null;
    }

    /** A sim at a spot: a city of researchers spread over what is open, and `labPeople` lab researchers, one month. */
    static Sim spotSim(Spot s, double cityPeople, double labPeople) {
        Sim sim = new Sim(SEED).city(s.field(), cityPeople).labs(s.field(), 1, labPeople);
        sim.r.setPrize(s.field(), BIG);
        sim.step(s.month());
        return sim;
    }

    static double room(Research r, Entry e) { return Math.max(0, Research.teamCap(e) - r.cityShare(e.code())); }

    static void sectionFour() {
        System.out.println("\n--- 4. labs fill the cheapest problem on the chase up to its team cap less the city's share, then the next; the city's own spread is unchanged ---");
        Spot spot = findSpot(0);
        check("fixture: a month where three problems or more are on the chase and another is open and is not" + (spot == null ? "" : " (" + spot.field() + " month " + spot.month() + ": chase " + codes(spot.chase()) + ", open " + codes(spot.open()) + ")"), spot != null);
        if (spot == null) return;
        Field f = spot.field();
        double capSum = 0;
        for (Entry e : spot.open()) capSum += Research.teamCap(e);
        double cityPeople = 0.3 * capSum;

        Sim without = spotSim(spot, cityPeople, 0), with = spotSim(spot, cityPeople, 5000);
        boolean same = without.r.idle(f) == with.r.idle(f) && without.r.open(f) == with.r.open(f) && without.r.researchers(f) == with.r.researchers(f);
        List<String> moved = new ArrayList<>();
        for (Entry e : CAT.entries()) if (without.r.cityShare(e.code()) != with.r.cityShare(e.code())) moved.add(e.code());
        check("with 5,000 lab researchers or none, the city's own researchers spread exactly the same: every share, the idle, the open problems", same && moved.isEmpty(), moved.toString());
        check("fixture: the city's researchers are at work on the chase's first problem and none of them is idle", without.r.cityShare(spot.chase().get(0).code()) > 0 && without.r.idle(f) == 0);

        List<Entry> chase = spot.chase();
        double r1 = room(without.r, chase.get(0)), r2 = room(without.r, chase.get(1)), r3 = room(without.r, chase.get(2)), rooms = 0;
        for (Entry e : chase) rooms += room(without.r, e);
        check("fixture: each of the first three problems on the chase has room for lab researchers over the city's share (" + String.format("%.2f, %.2f, %.2f", r1, r2, r3) + ")", r1 > 0 && r2 > 0 && r3 > 0);

        Sim few = spotSim(spot, cityPeople, r1 / 2);
        near("a few lab researchers (half the first problem's room) all go to the cheapest problem: " + chase.get(0).code(), few.r.labShare(chase.get(0).code()), r1 / 2, 1e-9);
        check("...and none to the next, and none idle", few.r.labShare(chase.get(1).code()) == 0 && few.r.labShare(chase.get(2).code()) == 0 && few.r.labsIdle(f) == 0);
        Sim two = spotSim(spot, cityPeople, r1 + r2 / 2);
        check("more fill the cheapest to its team cap less the city's share, and the rest go to the next: " + chase.get(0).code() + " full, " + chase.get(1).code() + " half its room, the third none",
                Math.abs(two.r.labShare(chase.get(0).code()) - r1) < 1e-9 && Math.abs(two.r.labShare(chase.get(1).code()) - r2 / 2) < 1e-9 && two.r.labShare(chase.get(2).code()) == 0
                        && Math.abs(two.r.cityShare(chase.get(0).code()) + two.r.labShare(chase.get(0).code()) - Research.teamCap(chase.get(0))) < 1e-9);
        Sim all = spotSim(spot, cityPeople, rooms + 40);
        boolean full = true;
        for (Entry e : chase) full &= Math.abs(all.r.labShare(e.code()) - room(without.r, e)) < 1e-9;
        check("lab researchers beyond the room of the whole chase fill every problem on it and the rest are idle (forty over)", full && Math.abs(all.r.labsIdle(f) - 40) < 1e-6 && Math.abs(all.r.labResearchers(f) - (rooms + 40)) < 1e-9);
        boolean off = true;
        for (Entry e : spot.open()) if (!chase.contains(e)) off &= all.r.labShare(e.code()) == 0 && all.r.labPile(e.code()) == 0;
        check("a problem that is open and not on the chase gets no lab researcher, however many there are (" + (spot.open().size() - chase.size()) + " here)", off);
        Sim nobody = spotSim(spot, cityPeople, 0);
        check("no lab researchers pile no lab work", nobody.r.labPilesForSave().isEmpty() && nobody.r.labsIdle(f) == 0);

        List<String> piles = new ArrayList<>();
        for (int i = 0; i < chase.size(); i++) {
            Entry e = chase.get(i);
            double city = all.r.cityShare(e.code()), lab = all.r.labShare(e.code());
            if (Math.abs(all.r.pile(e.code()) - (city + lab) / 12) > 1e-12 || Math.abs(all.r.labPile(e.code()) - lab / 12) > 1e-12) piles.add(e.code());
        }
        for (Entry e : spot.open()) {
            if (chase.contains(e)) continue;
            double city = all.r.cityShare(e.code());
            if (Math.abs(all.r.pile(e.code()) - city / 12) > 1e-12 || all.r.labPile(e.code()) != 0) piles.add(e.code());
        }
        check("each pile gains the city's share and the lab's together over twelve, and the labs' part of it alone is kept apart: lab share over twelve", piles.isEmpty(), piles.toString());

        // The same, month after month, for a city with labs, a grant, a prize and posts to buy: every month's invariants.
        List<String> faults = new ArrayList<>();
        Sim run = standard(SEED);
        int months = 700, worked = 0, labbed = 0;
        for (int m = 1; m <= months; m++) {
            run.step(m);
            faults.addAll(invariants(run, m));
            for (Field g : Field.values()) if (run.r.labResearchers(g) > run.r.labsIdle(g) + 1e-9) { worked++; break; }
            if (!run.r.labPilesForSave().isEmpty()) labbed++;
        }
        check("fixture: over " + months + " months labs worked on a problem in " + worked + " and held work piled in " + labbed, worked > 50 && labbed > 50);
        check("every month of " + months + ": each pile is last month's plus (city + lab) / 12 or gone (found, or the world's); the lab part likewise and never above the pile; nobody past the team cap; "
                + "lab researchers only on the chase; city and lab researchers at work and idle add up", faults.isEmpty(), faults.size() > 3 ? faults.subList(0, 3) + " ..." : faults.toString());
    }

    /* ==================== 5. the purse ==================== */

    static void sectionFive() {
        System.out.println("\n--- 5. a find pays its purse, split by who did the work: the labs' share is owed to their owners, the schools' joins the pot ---");
        Sim s = standard(SEED);
        List<String> faults = new ArrayList<>(), listed = new ArrayList<>(), sums = new ArrayList<>(), pots = new ArrayList<>(), noLab = new ArrayList<>();
        int finds = 0, both = 0;
        for (int m = 1; m <= 700; m++) {
            s.step(m);
            Research r = s.r;
            for (Field f : Field.values()) {
                double owed = 0, toSchools = 0;
                for (String code : r.foundThisMonth()) {
                    Entry e = of(code);
                    if (e.field() != f) continue;
                    Research.Purse p = r.purses().get(code);
                    if (p == null) {
                        if (r.prize(f) > 0) listed.add(m + " " + code + " has no purse");
                        continue;
                    }
                    finds++;
                    double city = r.cityShare(code), lab = r.labShare(code);
                    double pile = s.pilesBefore.getOrDefault(code, 0.0) + (city + lab) / 12, labPile = s.labPilesBefore.getOrDefault(code, 0.0) + lab / 12;
                    double expected = r.prize(f) * labPile / pile;
                    if (Math.abs(p.toLabs() - expected) > 1e-6 * Math.max(1, r.prize(f))) faults.add(m + " " + code + " toLabs " + p.toLabs() + " not " + expected);
                    if (Math.abs(p.toLabs() + p.toSchools() - r.prize(f)) > 1e-9 * r.prize(f)) sums.add(m + " " + code);
                    if (labPile == 0 && (p.toLabs() != 0 || p.toSchools() != r.prize(f))) noLab.add(m + " " + code);
                    if (labPile > 0 && city > 0 && p.toLabs() > 0 && p.toSchools() > 0) both++;
                    owed += p.toLabs();
                    toSchools += p.toSchools();
                }
                if (Math.abs(r.toLabs(f) - owed) > 1e-9 * Math.max(1, owed)) sums.add(m + " " + f + " toLabs(f) " + r.toLabs(f) + " not " + owed);
                if (Math.abs(r.pot(f) - (s.potBefore.get(f) + toSchools)) > 1e-9 * Math.max(1, r.pot(f))) pots.add(m + " " + f + " pot " + r.pot(f) + " not " + (s.potBefore.get(f) + toSchools));
            }
            Set<String> expectedListed = new HashSet<>();
            for (String code : r.foundThisMonth()) if (r.prize(of(code).field()) > 0) expectedListed.add(code);
            if (!r.purses().keySet().equals(expectedListed)) listed.add(m + " purses " + r.purses().keySet() + " not " + expectedListed);
        }
        check("fixture: " + finds + " finds with a prize in 700 months, " + both + " with the labs and the city both at work on the problem", finds > 20 && both > 5);
        check("every find with both kinds of work pays the labs the prize times the lab's work piled over all the work piled, this month's included, and the schools the rest",
                faults.isEmpty() && sums.isEmpty(), (faults.size() > 3 ? faults.subList(0, 3) + " ..." : faults.toString()) + sums);
        check("a find nobody in a lab worked on pays all of the prize to the schools and nothing to the labs", noLab.isEmpty(), noLab.toString());
        check("the purses are listed in the month of the find only, one for each find of a field with a prize, and none for a field without", listed.isEmpty(), listed.size() > 3 ? listed.subList(0, 3).toString() : listed.toString());
        check("toLabs(f) is the sum of the labs' parts of the field's purses this month, and each field's pot gains the schools' parts", sums.isEmpty() && pots.isEmpty(), pots.size() > 3 ? pots.subList(0, 3).toString() : pots.toString());

        // A find nobody in a lab worked on: the city alone, and a city whose labs are on other problems.
        for (boolean labbed : new boolean[] {false, true}) {
            Sim alone = new Sim(SEED).city(Field.ENGINEERING, 400);
            if (labbed) alone.labs(Field.ENGINEERING, 1, 100);
            double prize = prizeFor(Field.ENGINEERING, 20);
            alone.r.setPrize(Field.ENGINEERING, prize);
            int unlabbed = 0, withLab = 0;
            List<String> wrongPurse = new ArrayList<>();
            for (int m = 1; m <= 200; m++) {
                alone.step(m);
                for (String code : alone.r.foundThisMonth()) {
                    Research.Purse p = alone.r.purses().get(code);
                    double lab = alone.labPilesBefore.getOrDefault(code, 0.0) + alone.r.labShare(code) / 12;
                    if (lab == 0) {
                        unlabbed++;
                        if (p == null || p.toLabs() != 0 || p.toSchools() != prize) wrongPurse.add(m + " " + code + " " + p);
                    } else {
                        withLab++;
                    }
                }
                if (!labbed && alone.r.toLabs(Field.ENGINEERING) != 0) wrongPurse.add(m + " owed to labs with none");
            }
            check((labbed ? "with one lab of 100 beside 400 city researchers (" + withLab + " finds it worked on, " + unlabbed + " it did not)" : "with 400 city researchers and no lab (" + unlabbed + " finds)")
                    + ": every find nobody in a lab worked on pays the whole prize to the schools and nothing to the labs", unlabbed > 3 && wrongPurse.isEmpty(), wrongPurse.toString());
        }

        // Two finds of one field in one month: toLabs sums, the pot gains both.
        boolean twoFound = false, twoOk = false;
        for (long seed = 1; seed <= 40 && !twoFound; seed++) {
            Sim heavy = new Sim(seed).city(Field.ENGINEERING, 4000).labs(Field.ENGINEERING, 2, 200);
            heavy.r.setPrize(Field.ENGINEERING, prizeFor(Field.ENGINEERING, 300));
            for (int m = 1; m <= 400 && !twoFound; m++) {
                heavy.step(m);
                List<String> now = new ArrayList<>();
                for (String c : heavy.r.foundThisMonth()) if (of(c).field() == Field.ENGINEERING) now.add(c);
                if (now.size() < 2) continue;
                twoFound = true;
                double owed = 0, schools = 0;
                for (String c : now) {
                    owed += heavy.r.purses().get(c).toLabs();
                    schools += heavy.r.purses().get(c).toSchools();
                }
                twoOk = Math.abs(heavy.r.toLabs(Field.ENGINEERING) - owed) < 1e-9 * Math.max(1, owed) && Math.abs(heavy.r.pot(Field.ENGINEERING) - heavy.potBefore.get(Field.ENGINEERING) - schools) < 1e-9 * Math.max(1, schools)
                        && heavy.r.purses().size() == now.size();
            }
        }
        check("two finds of one field in one month (4,000 city researchers, two labs): toLabs(f) is both parts and the pot gains both", twoFound && twoOk);

        // The prize is read at the find: no prize, no purse, no pot.
        Sim free = new Sim(SEED).city(Field.ENGINEERING, 400).labs(Field.ENGINEERING, 1, 100);
        boolean any = false, owedNothing = true;
        for (int m = 1; m <= 150; m++) {
            free.step(m);
            any |= !free.r.foundThisMonth().isEmpty();
            owedNothing &= free.r.purses().isEmpty() && free.r.toLabs(Field.ENGINEERING) == 0 && free.r.pot(Field.ENGINEERING) == 0 && free.r.labPilesForSave().isEmpty();
        }
        check("with a prize of 0 (labs there, 400 city researchers, finds made) nothing is a purse: none listed, nothing owed, no pot, and the labs piled no work", any && owedNothing);

        // A find drops the labs' part with the pile; a world arrival does too.
        Sim last = standard(SEED);
        boolean dropped = true;
        int sawFind = 0;
        for (int m = 1; m <= 400; m++) {
            last.step(m);
            for (String c : last.r.foundThisMonth()) {
                sawFind++;
                dropped &= last.r.pile(c) == 0 && last.r.labPile(c) == 0 && last.r.foundHere(c);
            }
        }
        check("a find takes the pile and the labs' part of it with it (" + sawFind + " finds)", sawFind > 5 && dropped);

        Entry e = of("PE14") != null ? of("PE14") : CAT.entries().get(0);
        Entry other = null;
        for (Entry x : CAT.entries()) if (x.field() == e.field() && x.worldMonth() > e.worldMonth() + 5) { other = x; break; }
        check("fixture: two discoveries of one field, history dating the second later than the first", other != null);
        if (other == null) return;
        Research r = new Research();
        r.restore(null, null, Map.of(e.code(), 6.0, other.code(), 5.0), null);
        r.restorePrizes(null, null, Map.of(e.code(), 4.0, other.code(), 3.0), null, null);
        EnumMap<Field, Double> none = new EnumMap<>(Field.class);
        r.month(e.worldMonth() - 1, SEED, none);
        check("the month before history dates a discovery its pile and the labs' part of it stand", r.pile(e.code()) == 6.0 && r.labPile(e.code()) == 4.0 && r.pile(other.code()) == 5.0);
        r.month(e.worldMonth(), SEED, none);
        check("in history's month it reaches the city from abroad: both its pile and the labs' part are dropped, the other discovery's stand",
                r.arrivedThisMonth().contains(e.code()) && r.pile(e.code()) == 0 && r.labPile(e.code()) == 0 && r.pile(other.code()) == 5.0 && r.labPile(other.code()) == 3.0);
    }

    /* ==================== 6. the pot buys posts ==================== */

    static void sectionSix() {
        System.out.println("\n--- 6. the schools' pot buys posts beyond the grant, and pays for them at the bill, never more than it has ---");
        Field f = Field.ENGINEERING;
        double c = cost(f);
        Sim p = potted();
        Research r = p.r;
        double pot = r.pot(f);
        check("fixture: 400 city researchers and a prize that covers a problem of 20 researcher-years made a find, and the schools' pot has the whole prize (" + String.format("%.2f", pot) + ")", pot > 0 && pot >= r.prize(f) - 1e-9 && r.pot(Field.MEDICINE) == 0);
        r.setGrant(f, 2.5 * c);
        EnumMap<Field, Double> big = map(f, 1e6);
        r.strikePosts(big, W);
        check("the strike buys posts for the grant and the pot together (" + r.posts(f) + "), and remembers the posts the grant alone buys (" + r.grantPosts(f) + ")",
                r.posts(f) == Research.posts(2.5 * c + pot, c, 1e6) && r.grantPosts(f) == Research.posts(2.5 * c, c, 1e6) && r.posts(f) > r.grantPosts(f));
        check("...and the other fields, which have neither grant nor pot, buy none", r.posts(Field.MEDICINE) == 0 && r.grantPosts(Field.MEDICINE) == 0);
        check("the month's jobs are the posts' jobs, the pot's posts among them", Arrays.equals(r.jobs(), Research.jobs(f, r.posts(f))));

        double owed = Research.bill(f, r.posts(f), W, FULL) - Research.bill(f, r.grantPosts(f), W, FULL);
        double potBefore = r.pot(f);
        double drawn = r.drawPots(W, FULL);
        near("at the bill the pot pays for its posts: the bill of all the posts less the bill of the grant's, at the month's wages and fill", drawn, owed, 1e-9);
        near("...which is about the pot's posts' cost (" + (r.posts(f) - r.grantPosts(f)) + " posts at the post's cost), a post's jobs being whole numbers", drawn, (r.posts(f) - r.grantPosts(f)) * c, c);
        near("...the pot is lowered by what it paid, and drawPots() says the total", r.pot(f), potBefore - drawn, 1e-9);
        check("...which is less than the pot it had, the posts having cost no more than the money", drawn > 0 && drawn <= potBefore);

        // Never more than the pot: the posts struck at cheap wages, billed at dear ones.
        Sim dear = potted();
        dear.r.setGrant(f, 2.5 * c);
        dear.r.strikePosts(big, W);
        double had = dear.r.pot(f), paid = dear.r.drawPots(wages(3), FULL);
        check("posts bought at the wages struck but billed at wages three times as high would cost more than the pot: the draw is the whole pot and no more, the pot is none", paid == had && dear.r.pot(f) == 0 && had > 0);

        Sim half = potted();
        half.r.setGrant(f, 2.5 * c);
        half.r.strikePosts(big, W);
        double fullDraw = half.r.drawPots(W, FULL);
        Sim halfFilled = potted();
        halfFilled.r.setGrant(f, 2.5 * c);
        halfFilled.r.strikePosts(big, W);
        double halfDraw = halfFilled.r.drawPots(W, fill(0.5));
        near("posts only half filled with staff cost half: the draw is half", halfDraw, fullDraw / 2, 1e-9);
        Sim empty = potted();
        empty.r.setGrant(f, 2.5 * c);
        empty.r.strikePosts(big, W);
        double kept = empty.r.pot(f);
        check("a post nobody fills draws nothing: the pot is as it was", empty.r.drawPots(W, fill(0)) == 0 && empty.r.pot(f) == kept);
        Sim noFill = potted();
        noFill.r.setGrant(f, 2.5 * c);
        noFill.r.strikePosts(big, W);
        check("...and no fill array reads as full (as the payroll does)", Math.abs(noFill.r.drawPots(W, null) - fullDraw) < 1e-9);

        // Room-limited: the grant already fills the room, so the pot buys none and pays nothing.
        Sim limited = potted();
        limited.r.setGrant(f, 5.5 * c);
        limited.r.strikePosts(map(f, 3), W);
        double stays = limited.r.pot(f);
        check("with the room at three posts and a grant of five, the pot buys none (the room is the limit) and pays nothing", limited.r.posts(f) == 3 && limited.r.grantPosts(f) == 3 && limited.r.drawPots(W, FULL) == 0 && limited.r.pot(f) == stays);

        Research plain = new Research();
        plain.setGrant(f, 12.3 * c);
        plain.strikePosts(map(f, 100), W);
        check("with no pot the strike is the grant's alone, and drawPots() draws nothing from nothing", plain.posts(f) == Research.posts(12.3 * c, c, 100) && plain.grantPosts(f) == plain.posts(f) && plain.drawPots(W, FULL) == 0);
        plain.strikePosts(map(f, 100), null);
        check("no wages buy no post, the pot's or the grant's", plain.posts(f) == 0 && plain.grantPosts(f) == 0 && plain.drawPots(null, null) == 0);
        Sim after = potted();
        after.r.setGrant(f, 2.5 * c);
        after.r.strikePosts(big, W);
        int before = after.r.posts(f);
        after.r.drawPots(W, FULL);
        after.r.strikePosts(big, W);
        check("the next strike, the pot lowered by its draw, buys fewer posts", after.r.posts(f) < before && after.r.posts(f) >= after.r.grantPosts(f));
    }

    /* ==================== 7. investors ==================== */

    static void sectionSeven() {
        System.out.println("\n--- 7. investors build a lab while the n-th would keep half its researchers at work; the target is the chase's cheapest ---");
        List<String> faults = new ArrayList<>();
        for (int n = 1; n <= 6; n++) {
            double room = (n - 1 + Research.LAB_ROOM_SHARE) * Research.LAB_POSTS;
            if (Research.labsFor(room) != n) faults.add("n=" + n + " at the threshold " + Research.labsFor(room));
            if (Research.labsFor(room - 1e-6) != n - 1) faults.add("n=" + n + " just under " + Research.labsFor(room - 1e-6));
            if (Research.labsFor(room + 1e-6) != n) faults.add("n=" + n + " just over " + Research.labsFor(room + 1e-6));
        }
        check("labsFor() steps up to n at (n - 1 + LAB_ROOM_SHARE) lab-fuls of room, from both sides, for n = 1 to 6", faults.isEmpty(), faults.toString());
        check("no room (0, below nothing, not a number, minus infinity) wants no lab, nor does room short of half a lab",
                Research.labsFor(0) == 0 && Research.labsFor(-50) == 0 && Research.labsFor(Double.NaN) == 0 && Research.labsFor(Double.NEGATIVE_INFINITY) == 0
                        && Research.labsFor(Research.LAB_ROOM_SHARE * Research.LAB_POSTS - 1e-6) == 0);
        int last = 0;
        boolean steady = true;
        for (double room = 0; room <= 2000; room += 0.5) {
            int n = Research.labsFor(room);
            steady &= n >= last && n <= last + 1;
            last = n;
        }
        check("as room grows labs wanted never fall and rise one at a time (0 to 2,000, by halves)", steady && last > 10);

        // The room is the chase list's, less the city's last shares.
        Spot spot = findSpot(3);
        check("fixture: a month with three problems on the chase and room for three labs" + (spot == null ? "" : " (" + spot.field() + " month " + spot.month() + ")"), spot != null);
        if (spot == null) return;
        Field f = spot.field();
        int m = spot.month();
        double capSum = 0;
        for (Entry e : spot.open()) capSum += Research.teamCap(e);
        List<String> wrong = new ArrayList<>();
        Map<Integer, Integer> seen = new TreeMap<>();
        for (double people = 0; people <= capSum * 1.4; people += capSum / 40) {
            Sim s = new Sim(SEED).city(f, people);
            s.r.setPrize(f, BIG);
            s.step(m - 1);
            double room = 0;
            for (Entry e : s.r.chase(f, m, W)) room += Math.max(0, Research.teamCap(e) - s.r.cityShare(e.code()));
            int wanted = s.r.labsWanted(f, m, W);
            seen.merge(wanted, 1, Integer::sum);
            if (wanted != Research.labsFor(room)) wrong.add(people + " city researchers: " + wanted + " not " + Research.labsFor(room));
        }
        check("labs wanted are labsFor() of the room: the sum over the chase list of what each problem's team cap leaves of the city's share in the last month (41 city sizes)", wrong.isEmpty(), wrong.toString());
        int most = seen.keySet().stream().max(Integer::compare).orElse(0), fewest = seen.keySet().stream().min(Integer::compare).orElse(0);
        check("fixture: the city's own researchers change the number of labs wanted, through " + seen.size() + " different numbers from " + most + " with none to " + fewest + " with the room taken", seen.size() >= 3 && most > fewest);
        Sim crowded = new Sim(SEED).city(f, capSum * 50);
        crowded.r.setPrize(f, BIG);
        crowded.step(m - 1);
        Sim empty = new Sim(SEED);
        empty.r.setPrize(f, BIG);
        double roomEmpty = 0;
        for (Entry e : empty.r.chase(f, m, W)) roomEmpty += Research.teamCap(e);
        check("with no researchers the room is the chase's team caps whole (" + Research.labsFor(roomEmpty) + " labs wanted); a city with every problem that was open last month at its cap leaves less (" + crowded.r.labsWanted(f, m, W) + ")",
                empty.r.labsWanted(f, m, W) == Research.labsFor(roomEmpty) && Research.labsFor(roomEmpty) > 0 && crowded.r.labsWanted(f, m, W) < empty.r.labsWanted(f, m, W));
        double roomCrowded = 0;
        for (Entry e : crowded.r.chase(f, m, W)) roomCrowded += Math.max(0, Research.teamCap(e) - crowded.r.cityShare(e.code()));
        int wantedCrowded = crowded.r.labsWanted(f, m, W);
        crowded.r.month(m, SEED, new EnumMap<>(Field.class));
        double roomAfter = 0;
        for (Entry e : crowded.r.chase(f, m, W)) roomAfter += Research.teamCap(e) - crowded.r.cityShare(e.code());
        check("it is the LAST month's shares it reads: after a month without researchers the shares are gone and the room (" + String.format("%.1f", roomCrowded) + " researchers) is whole again ("
                        + String.format("%.1f", roomAfter) + ")", crowded.r.cityShare(spot.chase().get(0).code()) == 0 && roomAfter > roomCrowded && wantedCrowded == Research.labsFor(roomCrowded)
                        && crowded.r.labsWanted(f, m, W) == Research.labsFor(roomAfter));

        // The target and the stake.
        Research r = new Research();
        r.setPrize(f, BIG);
        Entry target = r.labTarget(f, m, W);
        check("the target is the first of the chase, the cheapest", target != null && target.equals(r.chase(f, m, W).get(0)) && target.equals(spot.chase().get(0)));
        double cost = cost(f);
        near("the stake is a lab's posts at the post's cost for the months to finish: LAB_POSTS x cost x 12 x the fewest years of " + target.code(),
                r.labStake(f, m, W), Research.LAB_POSTS * cost * Research.MONTHS_A_YEAR * Research.minYears(target), 1e-6);
        check("...Research.stake() of it", r.labStake(f, m, W) == Research.stake(cost, target, 0));
        Research partway = new Research();
        partway.restore(null, null, Map.of(target.code(), target.work() / 2.0), null);
        partway.setPrize(f, BIG);
        check("with half its work piled the target is still the cheapest and the stake is half", partway.labTarget(f, m, W).equals(target) && Math.abs(partway.labStake(f, m, W) - r.labStake(f, m, W) / 2) < 1e-6);
        int labsSeen = 0;
        for (Field g : Field.values()) {
            Research one = new Research();
            one.setPrize(g, BIG);
            for (int month = 1; month <= lastWorldMonth(); month += 3) {
                if (one.chase(g, month, W).isEmpty()) continue;
                labsSeen++;
                if (!one.chase(g, month, W).get(0).equals(one.labTarget(g, month, W))) wrong.add(g + " " + month + " target");
                if (Double.isNaN(one.labStake(g, month, W)) || one.labsWanted(g, month, W) < 0) wrong.add(g + " " + month + " stake or wanted");
            }
        }
        check("in every field and every third month with a chase, the target is its first and the stake is a number (" + labsSeen + " months)", labsSeen > 100 && wrong.isEmpty(), wrong.toString());

        // Nothing to chase: nothing wanted.
        Research none = new Research();
        boolean noneWanted = none.labsWanted(f, m, W) == 0 && none.labTarget(f, m, W) == null && Double.isNaN(none.labStake(f, m, W));
        none.setPrize(f, BIG);
        boolean noWages = none.labsWanted(f, m, null) == 0 && none.labTarget(f, m, null) == null && Double.isNaN(none.labStake(f, m, null));
        none.setPrize(f, Math.nextDown(cover(spot.chase().get(0), 0)));
        boolean tooSmall = none.labsWanted(f, m, W) == 0 && none.labTarget(f, m, W) == null && Double.isNaN(none.labStake(f, m, W));
        none.setPrize(f, BIG);
        boolean late = none.labsWanted(f, lastWorldMonth() + 5, W) == 0 && none.labTarget(f, lastWorldMonth() + 5, W) == null && Double.isNaN(none.labStake(f, lastWorldMonth() + 5, W));
        check("with a prize of 0 no lab is wanted, there is no target and no stake (NaN)", noneWanted);
        check("...with no wages", noWages);
        check("...with a prize a hair under the cheapest problem's price (nothing then is on the chase)", tooSmall);
        check("...and after history's last month, when nothing is open", late);
    }

    /* ==================== 8. idle labs ==================== */

    /** The researchers the chase could take in a field at a month with no city researchers: the team caps of its problems, whole. */
    static double roomNow(Research r, Field f, int m) {
        double room = 0;
        for (Entry e : r.chase(f, m, W)) room += Math.max(0, Research.teamCap(e) - r.cityShare(e.code()));
        return room;
    }

    static void sectionEight() {
        System.out.println("\n--- 8. a field whose labs have half a lab's researchers idle has a lab too many; six such months in a row and one is shed ---");
        Spot spot = findSpot(0);
        check("fixture: a month with three problems on the chase", spot != null);
        if (spot == null) return;
        Field f = spot.field();
        int m = spot.month();
        int labs = 2;
        double factor = 1 / (1 - Research.LAB_ROOM_SHARE / labs);

        // Idle exactly at the threshold, from both sides: the room is the chase's, the lab researchers are set against it.
        Research above = new Research(), below = new Research();
        above.setPrize(f, BIG);
        below.setPrize(f, BIG);
        List<String> counts = new ArrayList<>();
        for (int month = 1; month <= Research.LAB_IDLE_MONTHS + 2; month++) {
            double room = roomNow(above, f, m), people = room * factor * (1 + 1e-6);
            above.month(m, SEED, null, counts(f, labs), map(f, people), W);
            if (above.labIdleMonths(f) != month) counts.add("above, month " + month + ": " + above.labIdleMonths(f));
            if (Math.abs(above.labsIdle(f) - (people - room)) > 1e-6) counts.add("above, month " + month + " idle " + above.labsIdle(f) + " not " + (people - room));
            if (above.labTooMany(f) != (month >= Research.LAB_IDLE_MONTHS)) counts.add("above, month " + month + " labTooMany " + above.labTooMany(f));
            double roomB = roomNow(below, f, m);
            below.month(m, SEED, null, counts(f, labs), map(f, roomB * factor * (1 - 1e-6)), W);
            if (below.labIdleMonths(f) != 0 || below.labTooMany(f)) counts.add("below, month " + month + ": " + below.labIdleMonths(f));
        }
        check("two labs whose idle researchers are a hair over half of one lab's: the count rises each month, and labTooMany() is false until the " + Research.LAB_IDLE_MONTHS + "th month and true from it; a hair under, never",
                counts.isEmpty(), counts.toString());
        above.month(m, SEED, null, counts(f, labs), map(f, roomNow(above, f, m) * factor * (1 - 1e-6)), W);
        check("a month with fewer idle than that zeroes the count at once, whatever it had reached", above.labIdleMonths(f) == 0 && !above.labTooMany(f));
        above.month(m, SEED, null, counts(f, labs), map(f, roomNow(above, f, m) * factor * (1 + 1e-6)), W);
        check("...and it starts again from one", above.labIdleMonths(f) == 1);

        // labShed
        Research shed = new Research();
        for (int month = 1; month <= Research.LAB_IDLE_MONTHS + 1; month++) shed.idleMonth(null, counts(f, 1), map(f, 100.0));
        Field other = f == Field.LAW ? Field.BUSINESS : Field.LAW;
        EnumMap<Field, Integer> twoFields = counts(f, 1);
        twoFields.put(other, 1);
        EnumMap<Field, Double> twoPeople = map(f, 100.0);
        twoPeople.put(other, 100.0);
        Research both = new Research();
        for (int month = 1; month <= Research.LAB_IDLE_MONTHS; month++) both.idleMonth(null, twoFields, twoPeople);
        check("fixture: a lab idle for " + (Research.LAB_IDLE_MONTHS + 1) + " months in a row counts " + (Research.LAB_IDLE_MONTHS + 1) + " and is too many", shed.labIdleMonths(f) == Research.LAB_IDLE_MONTHS + 1 && shed.labTooMany(f));
        check("labShed() zeroes the field's count and it is no longer too many; the other field's count, " + Research.LAB_IDLE_MONTHS + ", is its own and stays",
                both.labTooMany(f) && both.labTooMany(other) && sheddingOne(both, f, other));
        shed.labShed(f);
        shed.idleMonth(null, counts(f, 1), map(f, 100.0));
        check("...and the count starts again from one", shed.labIdleMonths(f) == 1);

        // Nothing to chase: all idle.
        Research noPrize = new Research();
        noPrize.month(m, SEED, null, counts(f, 2), map(f, 200.0), W);
        boolean idle = noPrize.labsIdle(f) == 200 && noPrize.labResearchers(f) == 200 && noPrize.piles().isEmpty() && noPrize.labPilesForSave().isEmpty() && noPrize.labIdleMonths(f) == 1;
        for (Entry e : CAT.entries()) idle &= noPrize.labShare(e.code()) == 0;
        check("labs with nothing to chase (a prize of 0) are all idle: no share, no pile, and the field has a lab too many", idle);
        Research late = new Research();
        late.setPrize(f, BIG);
        late.month(lastWorldMonth() + 5, SEED, null, counts(f, 1), map(f, 100.0), W);
        check("a field with no open problem at all has its lab researchers all idle too", late.open(f) == 0 && late.labsIdle(f) == 100 && late.labIdleMonths(f) == 1);
        Research unmanned = new Research();
        unmanned.setPrize(f, BIG);
        unmanned.month(m, SEED, null, counts(f, 1), map(f, 0.0), W);
        check("a lab nobody works in counts as a lab too many: no researcher is half a lab's idle at least", unmanned.labResearchers(f) == 0 && unmanned.labIdleMonths(f) == 1);
        Research noLab = new Research();
        noLab.setPrize(f, BIG);
        noLab.month(m, SEED, null, counts(f, 0), map(f, 100.0), W);
        Research missing = new Research();
        missing.month(m, SEED, null, null, map(f, 100.0), W);
        check("a field with no lab counts none, whatever lab researchers it is given (a lab count of 0, or none given)", noLab.labIdleMonths(f) == 0 && missing.labIdleMonths(f) == 0 && !noLab.labTooMany(f));

        // idleMonth counts as month() does.
        Research idleOnly = new Research();
        EnumMap<Field, Double> staff = map(f, 20.0);
        for (int month = 1; month <= 3; month++) idleOnly.idleMonth(staff, counts(f, 1), map(f, 100.0));
        check("idleMonth() with labs: every lab researcher idle, the count rising as month()'s does (three months: 3)",
                idleOnly.labsIdle(f) == 100 && idleOnly.labResearchers(f) == 100 && idleOnly.labIdleMonths(f) == 3 && idleOnly.idle(f) == 20 && idleOnly.open(f) == 0 && idleOnly.researchers(f) == 20);
        idleOnly.idleMonth(staff);
        check("...the old idleMonth(researchers) has no lab: no lab researcher, no count", idleOnly.labResearchers(f) == 0 && idleOnly.labsIdle(f) == 0 && idleOnly.labIdleMonths(f) == 0 && idleOnly.idle(f) == 20);
        Research nobodyIn = new Research();
        nobodyIn.idleMonth(null, counts(f, 1), null);
        check("...and an idleMonth() lab nobody works in counts too, as in month()", nobodyIn.labIdleMonths(f) == 1);
    }

    /** Sheds a lab of the field and says whether it alone was cleared. */
    static boolean sheddingOne(Research r, Field shed, Field kept) {
        int before = r.labIdleMonths(kept);
        r.labShed(shed);
        return r.labIdleMonths(shed) == 0 && !r.labTooMany(shed) && r.labIdleMonths(kept) == before && r.labTooMany(kept);
    }

    /* ==================== 9. the save ==================== */

    /** What a save keeps, as text: compared between the unbroken run and the restored one. */
    static String kept(Research r, int month) {
        StringBuilder sb = new StringBuilder();
        sb.append(r.foundHere()).append(r.piles()).append(r.labPilesForSave()).append(r.citySharesForSave()).append(r.prizesForSave()).append(r.potsForSave());
        sb.append(r.grantsForSave()).append(r.postsForSave()).append(r.labIdleMonthsForSave());
        for (Field f : Field.values()) {
            sb.append(f).append(r.labsWanted(f, month + 1, W)).append(r.labTarget(f, month + 1, W)).append(r.labStake(f, month + 1, W)).append(r.chase(f, month + 1, W).size());
        }
        return sb.toString();
    }

    /** What the month did, as text. */
    static String monthly(Research r) {
        StringBuilder sb = new StringBuilder();
        sb.append(r.foundThisMonth()).append(r.arrivedThisMonth()).append(r.purses());
        for (Field f : Field.values()) {
            sb.append(f).append(r.researchers(f)).append(r.idle(f)).append(r.open(f)).append(r.labResearchers(f)).append(r.labsIdle(f)).append(r.toLabs(f))
                    .append(r.posts(f)).append(r.grantPosts(f)).append(r.labTooMany(f));
        }
        return sb.toString();
    }

    static void sectionNine() {
        System.out.println("\n--- 9. a run saved midway and carried on finds what the unbroken run finds; a bad entry is skipped; a new game clears it ---");
        int end = 450;
        Sim whole = standard(SEED);
        String[] keptWhole = new String[end + 1], monthlyWhole = new String[end + 1];
        int cut = -1;
        for (int m = 1; m <= end; m++) {
            whole.step(m);
            keptWhole[m] = kept(whole.r, m);
            monthlyWhole[m] = monthly(whole.r);
            Research r = whole.r;
            if (cut < 0 && m >= 120 && r.pot(Field.ENGINEERING) > 0 && r.pot(Field.MEDICINE) > 0 && !r.labIdleMonthsForSave().isEmpty() && !r.labPilesForSave().isEmpty()
                    && !r.citySharesForSave().isEmpty() && r.posts(Field.ENGINEERING) > r.grantPosts(Field.ENGINEERING) && !r.purses().isEmpty()
                    && r.toLabs(Field.ENGINEERING) + r.toLabs(Field.MEDICINE) > 0) cut = m;
        }
        check("fixture: a month to cut at where a pot, posts bought by it, work piled by labs, shares, an idle count, a find and a purse owed to labs are all there to save (month " + cut + " of " + end + ")", cut > 0);
        if (cut < 0) return;

        Sim first = standard(SEED);
        first.run(1, cut);
        Research a = first.r;
        Sim second = standard(SEED);
        second.r.restore(a.grantsForSave(), a.postsForSave(), a.piles(), a.foundHere());
        second.r.restorePrizes(a.prizesForSave(), a.potsForSave(), a.labPilesForSave(), a.citySharesForSave(), a.labIdleMonthsForSave());
        check("what is saved is put back whole at month " + cut + ": the finds, the piles, the labs' part, the city's shares, the prizes, the pots, the grants, the posts, the idle counts, and what investors would say",
                kept(second.r, cut).equals(keptWhole[cut]) && kept(a, cut).equals(keptWhole[cut]));
        boolean strays = true;
        for (Field f : Field.values()) strays &= second.r.grantPosts(f) == second.r.posts(f);
        double potsBefore = second.r.pot(Field.ENGINEERING) + second.r.pot(Field.MEDICINE);
        check("...the grant's posts are the restored posts, so a draw before the next strike takes nothing", strays && second.r.drawPots(W, FULL) == 0 && second.r.pot(Field.ENGINEERING) + second.r.pot(Field.MEDICINE) == potsBefore);

        String firstDiff = null;
        for (int m = cut + 1; m <= end && firstDiff == null; m++) {
            second.step(m);
            if (!kept(second.r, m).equals(keptWhole[m])) firstDiff = "kept state differs at month " + m;
            else if (!monthly(second.r).equals(monthlyWhole[m])) firstDiff = "the month differs at " + m;
        }
        check("carried on from month " + cut + " to " + end + ", the restored city has the same finds, piles, lab piles, pots, idle counts, purses and posts as the unbroken run every month", firstDiff == null, firstDiff);
        check("...and the same finds in the same months in the end (" + whole.r.foundHere().size() + " of them)", second.r.foundHere().equals(whole.r.foundHere()) && !whole.r.foundHere().isEmpty());

        // Malformed entries are skipped, as restore() skips them.
        Research bad = new Research();
        bad.restore(null, Map.of("LAW", 3), new HashMap<>(Map.of("PE14", 2.0, "PE12", 1.0)), null);
        Map<String, Double> prizes = new HashMap<>(), pots = new HashMap<>(), labPiles = new HashMap<>(), shares = new HashMap<>();
        Map<String, Integer> idle = new HashMap<>();
        prizes.put("NONSENSE", 5.0); prizes.put("LAW", -1.0); prizes.put("BUSINESS", Double.NaN); prizes.put("MEDICINE", 2.0); prizes.put("ENGINEERING", null); prizes.put(null, 3.0);
        pots.put("NONSENSE", 5.0); pots.put("LAW", -1.0); pots.put("BUSINESS", Double.POSITIVE_INFINITY); pots.put("MEDICINE", 7.0); pots.put("ENGINEERING", null);
        labPiles.put("ZZ99", 1.0); labPiles.put("PE14", 5.0); labPiles.put("PE11", 1.0); labPiles.put("PE12", Double.NaN); labPiles.put("PE13", -1.0); labPiles.put(null, 1.0);
        shares.put("ZZ99", 1.0); shares.put("PE14", 3.0); shares.put("PE11", Double.NaN); shares.put("PE12", -1.0); shares.put("PE13", 0.0); shares.put("PE15", null);
        idle.put("NONSENSE", 3); idle.put("ENGINEERING", 0); idle.put("MEDICINE", 4); idle.put("LAW", -2); idle.put("BUSINESS", null);
        bad.restorePrizes(prizes, pots, labPiles, shares, idle);
        check("a malformed prize state is skipped entry by entry: a field that is not one, an amount below nothing, not a number or infinite, a code the catalogue lacks, a count below one, a null",
                bad.prize(Field.MEDICINE) == 2 && bad.prize(Field.LAW) == 0 && bad.prize(Field.BUSINESS) == 0 && bad.prize(Field.ENGINEERING) == 0
                        && bad.potsForSave().equals(Map.of("MEDICINE", 7.0)) && bad.labIdleMonthsForSave().equals(Map.of("MEDICINE", 4)));
        check("...a lab's pile is kept only where there is a pile and never above it (PE14's 5 is cut to its pile of 2; PE11 has no pile), the city's shares only above nothing",
                bad.labPilesForSave().equals(Map.of("PE14", 2.0)) && bad.citySharesForSave().equals(Map.of("PE14", 3.0)) && bad.piles().equals(Map.of("PE14", 2.0, "PE12", 1.0)));
        check("...the posts restored are the grant's posts", bad.grantPosts(Field.LAW) == 3 && bad.posts(Field.LAW) == 3);
        bad.restorePrizes(null, null, null, null, null);
        check("restoring no prize state empties it and leaves the grants, posts, piles and finds as restore() put them", bad.prizesForSave().isEmpty() && bad.potsForSave().isEmpty() && bad.labPilesForSave().isEmpty()
                && bad.citySharesForSave().isEmpty() && bad.labIdleMonthsForSave().isEmpty() && bad.posts(Field.LAW) == 3 && bad.piles().size() == 2);
        Research again = new Research();
        again.restore(a.grantsForSave(), a.postsForSave(), a.piles(), a.foundHere());
        check("restore() alone leaves the prize state as reset() does: no prize, pot, lab pile, share or idle count", again.prizesForSave().isEmpty() && again.potsForSave().isEmpty() && again.labPilesForSave().isEmpty()
                && again.citySharesForSave().isEmpty() && again.labIdleMonthsForSave().isEmpty() && again.pot(Field.ENGINEERING) == 0 && !again.isEmpty());
        check("the saves are keyed by field name and by code, never by an ordinal", a.prizesForSave().keySet().stream().allMatch(k -> Field.valueOf(k) != null) && a.potsForSave().keySet().stream().allMatch(k -> Field.valueOf(k) != null)
                && a.labIdleMonthsForSave().keySet().stream().allMatch(k -> Field.valueOf(k) != null) && a.labPilesForSave().keySet().stream().allMatch(k -> CAT.entry(k) != null)
                && a.citySharesForSave().keySet().stream().allMatch(k -> CAT.entry(k) != null));

        // A new game clears it.
        Research c = first.r;
        check("fixture: the research at the cut has every kind of state to clear: prize, pot, the grant's posts, lab piles, shares, idle counts, the month's labs, purses and the purse owed",
                !c.isEmpty() && c.prize(Field.ENGINEERING) > 0 && !c.potsForSave().isEmpty() && c.grantPosts(Field.ENGINEERING) > 0 && !c.labPilesForSave().isEmpty() && !c.citySharesForSave().isEmpty()
                        && !c.labIdleMonthsForSave().isEmpty() && c.labResearchers(Field.ENGINEERING) > 0 && !c.purses().isEmpty() && c.toLabs(Field.ENGINEERING) + c.toLabs(Field.MEDICINE) > 0);
        c.reset();
        boolean clear = c.isEmpty() && c.prizesForSave().isEmpty() && c.potsForSave().isEmpty() && c.labPilesForSave().isEmpty() && c.citySharesForSave().isEmpty() && c.labIdleMonthsForSave().isEmpty() && c.purses().isEmpty();
        for (Field f : Field.values()) clear &= c.prize(f) == 0 && c.pot(f) == 0 && c.grantPosts(f) == 0 && c.labsIdle(f) == 0 && c.labResearchers(f) == 0 && c.toLabs(f) == 0 && c.labIdleMonths(f) == 0 && !c.labTooMany(f) && c.chase(f, 100, W).isEmpty();
        for (Entry e : CAT.entries()) clear &= c.labPile(e.code()) == 0 && c.cityShare(e.code()) == 0 && c.labShare(e.code()) == 0;
        check("reset() (a new game) clears every new field: prize, pot, the grant's posts, lab piles, shares, idle counts, the month's labs and purses", clear);

        // isEmpty sees each.
        Research onlyPrize = new Research();
        onlyPrize.setPrize(Field.LAW, 5);
        Research onlyPot = new Research();
        onlyPot.restorePrizes(null, Map.of("LAW", 5.0), null, null, null);
        Research onlyCount = new Research();
        onlyCount.restorePrizes(null, null, null, null, Map.of("LAW", 2));
        Research onlyShares = new Research();
        onlyShares.restorePrizes(null, null, null, Map.of("PE14", 2.0), null);
        check("isEmpty() is false with only a prize set, only a pot, or only an idle count; the city's shares alone are not the city's (they are what a month saw)",
                !onlyPrize.isEmpty() && !onlyPot.isEmpty() && !onlyCount.isEmpty() && onlyShares.isEmpty());
        onlyPrize.setPrize(Field.LAW, 0);
        check("...and a prize set back to 0 is empty again", onlyPrize.isEmpty());
    }

    /* ==================== 10. no prize, no change ==================== */

    /** The study's R3 month, written again here from nothing but Research's statics: what the city's own researchers do whatever labs and prizes there are. */
    static final class Reference {
        final Map<String, Double> piles = new TreeMap<>();
        final Map<String, Integer> found = new LinkedHashMap<>();
        final List<String> foundNow = new ArrayList<>(), arrivedNow = new ArrayList<>();
        final EnumMap<Field, Double> idle = new EnumMap<>(Field.class);
        final EnumMap<Field, Integer> open = new EnumMap<>(Field.class);

        boolean known(String code, int m) { return m >= Math.min(CAT.worldMonth(code), found.getOrDefault(code, Integer.MAX_VALUE)); }

        boolean isOpen(Entry e, int m) {
            if (known(e.code(), m) || m < e.worldMonth() - Research.lagMonths(e.field())) return false;
            for (String need : e.needs()) if (!known(need, m)) return false;
            return true;
        }

        void month(int m, long seed, EnumMap<Field, Double> researchers) {
            foundNow.clear();
            arrivedNow.clear();
            for (Entry e : CAT.entries()) {
                if (e.worldMonth() > m || found.containsKey(e.code())) continue;
                piles.remove(e.code());
                if (e.worldMonth() == m) arrivedNow.add(e.code());
            }
            for (Field f : Field.values()) {
                List<Entry> list = new ArrayList<>();
                for (Entry e : CAT.entries()) if (e.field() == f && isOpen(e, m)) list.add(e);
                Double people = researchers.get(f);
                double n = people == null || !Double.isFinite(people) || people < 0 ? 0 : people;
                open.put(f, list.size());
                if (list.isEmpty()) { idle.put(f, n); continue; }
                double[] caps = new double[list.size()];
                for (int i = 0; i < caps.length; i++) caps[i] = Research.teamCap(list.get(i));
                Research.Fill fill = Research.waterFill(n, caps);
                idle.put(f, fill.idle());
                for (int i = 0; i < caps.length; i++) {
                    double add = fill.share()[i] / 12;
                    if (!(add > 0)) continue;
                    Entry e = list.get(i);
                    double pile = piles.merge(e.code(), add, Double::sum);
                    if (pile >= Research.hiddenSize(seed, e)) {
                        found.put(e.code(), m);
                        piles.remove(e.code());
                        foundNow.add(e.code());
                    }
                }
            }
        }
    }

    static EnumMap<Field, Double> everyone(double each) {
        EnumMap<Field, Double> m = new EnumMap<>(Field.class);
        for (Field f : Field.values()) m.put(f, each);
        return m;
    }

    static EnumMap<Field, Integer> everyLab(int each) {
        EnumMap<Field, Integer> m = new EnumMap<>(Field.class);
        for (Field f : Field.values()) m.put(f, each);
        return m;
    }

    /** Runs a Research against the reference over all history; the first disagreement, or null. */
    static String against(Research r, int variant, long seed, EnumMap<Field, Double> staff) {
        Reference ref = new Reference();
        EnumMap<Field, Double> labPeople = everyone(variant == 2 ? 300 : 0);
        for (int m = 1; m <= lastWorldMonth() + 2; m++) {
            ref.month(m, seed, staff);
            switch (variant) {
                case 0 -> r.month(m, seed, staff);
                default -> r.month(m, seed, staff, everyLab(variant == 2 ? 3 : 0), labPeople, W);
            }
            if (!r.foundThisMonth().equals(ref.foundNow)) return "finds at month " + m + ": " + r.foundThisMonth() + " not " + ref.foundNow;
            if (!r.arrivedThisMonth().equals(ref.arrivedNow)) return "arrivals at month " + m;
            if (!r.piles().equals(ref.piles)) return "piles at month " + m;
            for (Field f : Field.values()) {
                if (r.idle(f) != ref.idle.get(f) || r.open(f) != ref.open.get(f)) return f + " idle or open at month " + m;
            }
        }
        return r.foundHere().equals(ref.found) ? null : "the finds";
    }

    static void sectionTen() {
        System.out.println("\n--- 10. no prize, no change: without labs the city's work is the study's own, whatever the prize; with a prize of 0 labs sit idle ---");
        for (long seed : new long[] {SEED, 1, 2}) {
            for (boolean heavy : new boolean[] {false, true}) {
                EnumMap<Field, Double> staff = everyone(heavy ? 1500 : 30);
                String name = seed + ", " + (heavy ? "1,500" : "30") + " researchers in each field";
                Research old = new Research();
                String oldRun = against(old, 0, seed, staff);
                check(name + ": the old month(month, seed, researchers) is the study's R3 month: the same finds, arrivals, piles and idle, every month of history (" + old.foundHere().size() + " finds)", oldRun == null && (!heavy || old.foundHere().size() > 10), oldRun);
                Research empty = new Research();
                String emptyRun = against(empty, 1, seed, staff);
                check("...the new one with no labs, wages given and a prize of 0 is the same", emptyRun == null && empty.foundHere().equals(old.foundHere()), emptyRun);
                Research idle = new Research();
                String idleRun = against(idle, 2, seed, staff);
                boolean quiet = idle.potsForSave().isEmpty() && idle.labPilesForSave().isEmpty() && idle.purses().isEmpty();
                check("...with three labs of 300 researchers in every field and a prize of 0 it is the same: the labs find nothing, pile nothing and sit idle (" + idle.labsIdle(Field.ENGINEERING) + " idle)",
                        idleRun == null && quiet && idle.foundHere().equals(old.foundHere()) && idle.labsIdle(Field.LAW) == idle.labResearchers(Field.LAW), idleRun);
                Research prized = new Research();
                for (Field f : Field.values()) prized.setPrize(f, BIG);
                String prizedRun = against(prized, 1, seed, staff);
                double potted = 0;
                for (Field f : Field.values()) potted += prized.pot(f);
                check("...and a prize with no lab changes none of the work either: the schools take every purse (" + prized.foundHere().size() + " finds, a pot of the prize for each)",
                        prizedRun == null && Math.abs(potted - prized.foundHere().size() * BIG) < 1e-3 * Math.max(1, potted) && prized.labPilesForSave().isEmpty(), prizedRun);
            }
        }
        List<String> wanted = new ArrayList<>();
        Research none = new Research();
        for (Field f : Field.values()) {
            for (int m = 1; m <= lastWorldMonth() + 2; m++) {
                if (none.labsWanted(f, m, W) != 0 || none.labTarget(f, m, W) != null || !Double.isNaN(none.labStake(f, m, W)) || !none.chase(f, m, W).isEmpty()) wanted.add(f + " " + m);
            }
        }
        check("with a prize of 0 nobody wants a lab, in any field, in any month of history: no wanted, no target, no stake, no chase", wanted.isEmpty(), wanted.size() > 3 ? wanted.subList(0, 3).toString() : wanted.toString());
    }

    /* ==========================================================================================================================================
       R4 SLICE B: THE LABS IN THE CITY (sections 11 to 17). A village founded the 1900 way (or the default way) with a University standing, DiscoveryCheck's
       fixtures (cityWithSchools(), play(), pileJustUnder()), played with the hands handed afresh each month (the village's own workforce is nil and
       MaterialsCheck.handWorkers() lasts a month), to the month before the model first wants a lab: month 80 for engineering, whose first lab is the
       earliest of the four fields' (the model's own monthWantingALab()). Every figure from Research's and the model's own functions and constants.
       ========================================================================================================================================== */

    /** The field the city sections work in: the University hosts it, and the first lab any field wants is wanted in it. */
    static final Field F = Field.ENGINEERING;

    static final String BS = Sectors.BUSINESS_SERVICES;

    /** research-design 5.3: the lab's D$ 12M against the Engineering Services Office's D$ 10.9M for 120 staff, which the lab templates' figures are written from. */
    static final double STUDY_RATIO = 12 / 10.9;

    /** Hands handed to the village (MaterialsCheck.handWorkers()), which last one month. */
    static void hand(Game g) {
        DiscoveryCheck.quietly(() -> MaterialsCheck.handWorkers(g));
    }

    /** `months` presses of the clock, the hands handed afresh before each. */
    static void playHanded(Game g, int months) {
        for (int i = 0; i < months; i++) {
            hand(g);
            DiscoveryCheck.play(g, 1);
        }
    }

    /** The first month in which a fresh Research with a prize no problem escapes wants `labs` labs in a field (Research.labsWanted() at the wages), or -1 for none in all of history. */
    static int monthWantingALab(Field f, int labs) {
        Research fresh = new Research();
        fresh.setPrize(f, BIG);
        for (int m = 1; m <= lastWorldMonth() + 1; m++) if (fresh.labsWanted(f, m, W) >= labs) return m;
        return -1;
    }

    /** A village with a University and the hands, played to the month before `f` first wants `labs` labs. */
    static Game labCity(String label, boolean the1900Way, Field f, int labs) {
        Game g = DiscoveryCheck.cityWithSchools(label, the1900Way, false);
        playHanded(g, monthWantingALab(f, labs) - 1 - g.getMonth());
        return g;
    }

    /** What one more researcher-year of a problem costs the investors' reckoning in the city: the problem's remaining work times twelve times the post's cost at the city's wages now (Research's own statics). */
    static double coverNow(Game g, Entry e) {
        return Research.remaining(e, g.getResearch().pile(e.code())) * Research.MONTHS_A_YEAR * Research.postCost(e.field(), g.getPopulationManager().getWagesPerType());
    }

    /**
     * The least prize at which Research wants a lab in the field in the city's next month: the least of the open problems' covers (coverNow()) at which
     * Research.labsWanted() is one or more. NaN where none does. The field's prize is left as it was.
     */
    static double justWantsALab(Game g, Field f) {
        Research r = g.getResearch();
        double was = r.prize(f);
        int m = g.getMonth() + 1;
        double[] w = g.getPopulationManager().getWagesPerType().clone();
        List<Double> covers = new ArrayList<>();
        for (Entry e : r.openProblems(f, m)) covers.add(coverNow(g, e));
        java.util.Collections.sort(covers);
        double found = Double.NaN;
        for (double c : covers) {
            r.setPrize(f, c);
            if (r.labsWanted(f, m, w) >= 1) { found = c; break; }
        }
        r.setPrize(f, was);
        return found;
    }

    /** DiscoveryCheck.pileJustUnder() with the prizes, the pots, the labs' piles, the shares and the idle counts kept: it restores the research from its grants, posts, piles and finds alone, which clears them. */
    static void pileJustUnder(Game g, Entry e) {
        Research r = g.getResearch();
        Map<String, Double> prizes = r.prizesForSave(), pots = r.potsForSave(), labPiles = r.labPilesForSave(), shares = r.citySharesForSave();
        Map<String, Integer> idle = r.labIdleMonthsForSave();
        DiscoveryCheck.pileJustUnder(g, e);
        r.restorePrizes(prizes, pots, labPiles, shares, idle);
    }

    /** The labs of every field standing and on site, as one string: "ENGINEERING 1+0 MEDICINE 0+0 ...". */
    static String labsOf(Game g) {
        StringBuilder sb = new StringBuilder();
        for (Field f : Field.values()) sb.append(f).append(' ').append(g.labsStanding(f)).append('+').append(g.labsOnSite(f)).append(' ');
        return sb.toString().trim();
    }

    /** The labs the city has in all fields, standing and on site. */
    static int labsInAll(Game g) {
        int n = 0;
        for (Field f : Field.values()) n += g.labsStanding(f) + g.labsOnSite(f);
        return n;
    }

    /* ==================== 11. the labs are buildings ==================== */

    static void sectionEleven() {
        System.out.println("\n--- 11. the labs are buildings: four templates, one a field, with a lab's jobs; no planner of Business Services offers one ---");
        BuildingManager builtIn = new BuildingManager();
        builtIn.initializeBuiltInTemplates();
        List<BuildingsTemplate> data = new BuildingCatalog().load();
        check("fixture: both the built-in definitions and buildings.json are there", data != null && !builtIn.getTemplates().isEmpty());
        Map<Field, String> names = new EnumMap<>(Map.of(Field.ENGINEERING, "Engineering Research Lab", Field.MEDICINE, "Medical Research Lab", Field.BUSINESS, "Business Research Lab", Field.LAW, "Law Research Lab"));
        for (int source = 0; source < 2 && data != null; source++) {
            List<BuildingsTemplate> all = source == 0 ? builtIn.getTemplates() : data;
            String from = source == 0 ? "the built-in definitions" : "buildings.json";
            BuildingsTemplate office = null;
            Set<Integer> ids = new HashSet<>();
            List<String> faults = new ArrayList<>();
            int labs = 0;
            for (BuildingsTemplate t : all) {
                if (!ids.add(t.getId())) faults.add("id " + t.getId() + " twice");
                if (t.getId() == 48) office = t;
                if (t.getResearches() != null) labs++;
            }
            for (Field f : Field.values()) {
                BuildingsTemplate lab = null;
                int of = 0;
                for (BuildingsTemplate t : all) if (t.getResearches() == f) { lab = t; of++; }
                if (of != 1) { faults.add(f + " has " + of + " labs"); continue; }
                if (!lab.getName().equals(names.get(f))) faults.add(f + " named " + lab.getName());
                if (lab.getId() != 148 + f.ordinal()) faults.add(lab.getName() + " has id " + lab.getId());
                if (lab.getCategory() != BuildingType.BUSINESS_SERVICES || !BS.equals(lab.getSector())) faults.add(lab.getName() + " is " + lab.getCategory() + " / " + lab.getSector());
                if (!lab.goodsMade().isEmpty() || !lab.goodsUsed().isEmpty() || lab.getRequiresLicence() != null) faults.add(lab.getName() + " makes, uses or needs a licence");
                if (lab.getCapacity() != Research.LAB_POSTS || lab.getUpkeep() != 0) faults.add(lab.getName() + " capacity " + lab.getCapacity() + ", upkeep " + lab.getUpkeep());
                long[] jobs = Research.labJobs(f);
                for (JobType type : JobType.values()) if (lab.getJobs(type) != jobs[type.ordinal()]) faults.add(lab.getName() + " " + type + " " + lab.getJobs(type) + " against " + jobs[type.ordinal()]);
                if (office != null) {
                    double[][] pairs = { {lab.getCashCost(), office.getCashCost()}, {lab.getConstructionPoints(), office.getConstructionPoints()}, {lab.getConstructionMaterials(), office.getConstructionMaterials()},
                            {lab.getElectricityConsumption(), office.getElectricityConsumption()}, {lab.getWaterConsumption(), office.getWaterConsumption()}, {lab.getRoadLoad(), office.getRoadLoad()} };
                    for (int i = 0; i < pairs.length; i++) if (Math.abs(pairs[i][0] - pairs[i][1] * STUDY_RATIO) > 0.5) faults.add(lab.getName() + " figure " + i + ": " + pairs[i][0] + " against " + pairs[i][1] * STUDY_RATIO);
                    if (Math.abs(lab.getLandSqFt() - office.getLandSqFt() * STUDY_RATIO) > 500) faults.add(lab.getName() + " land " + lab.getLandSqFt());
                }
            }
            check(from + ": four templates research a field, one each (ids 148 to 151, none reused), named, in Business Services, making nothing and needing no licence, with the jobs of Research.labJobs() job type by job type"
                    + " and the Engineering Services Office's figures times the study's 12 / 10.9", faults.isEmpty() && labs == 4 && office != null, faults.toString());
        }

        // A name that is no field is reported and read as none, as a bad school or care type is.
        PrintStream real = System.out;
        ByteArrayOutputStream said = new ByteArrayOutputStream();
        List<BuildingsTemplate> odd;
        System.setOut(new PrintStream(said));
        try {
            odd = new BuildingCatalog().parse(new StringReader("{\"buildings\":[{\"id\":900,\"name\":\"Lab of Nothing\",\"category\":\"BUSINESS_SERVICES\",\"researches\":\"ASTROLOGY\"},"
                    + "{\"id\":901,\"name\":\"Lab of Law\",\"category\":\"BUSINESS_SERVICES\",\"researches\":\"LAW\"}]}"));
        } finally {
            System.setOut(real);
        }
        check("a \"researches\" that is no field is reported by name and read as none, and one that is a field is read: the key is a Field's name",
                odd != null && odd.size() == 2 && odd.get(0).getResearches() == null && odd.get(1).getResearches() == Field.LAW
                        && said.toString().contains("Lab of Nothing") && said.toString().contains("ASTROLOGY"), said.toString());

        // No planner of Business Services offers a lab: the plan's template and its staffing hold are never one, in a village of each founding and with and without hands.
        int[] asked = new int[3];
        List<String> offered = new ArrayList<>();
        for (int variant = 0; variant < 3; variant++) {
            Game g = DiscoveryCheck.cityWithSchools("prize-plan-" + variant, variant != 1, false);
            ham.citybuildersim.sectors.BusinessServices bs = g.getSectors().businessServices();
            for (int i = 0; i < 36; i++) {
                if (variant != 2) playHanded(g, 1); else DiscoveryCheck.play(g, 1);
                BusinessInvestment.Decision d = bs.plan(g.getBusinessInvestment(), g);
                if (d == null) continue;
                asked[0]++;
                if (d.build) asked[1]++;
                if (d.heldForStaff != null) asked[2]++;
                if (d.template != null && d.template.getResearches() != null) offered.add("variant " + variant + " month " + g.getMonth() + ": the plan is " + d.template.getName());
                if (d.heldForStaff != null && d.heldForStaff.template() != null && d.heldForStaff.template().getResearches() != null) offered.add("variant " + variant + " month " + g.getMonth() + ": the staffing hold is " + d.heldForStaff.template().getName());
            }
        }
        check("Business Services' plan is never a lab nor held for the staff of one, over 36 months of a 1900 village, a default one and a 1900 village with no hands (" + asked[0] + " plans asked, " + asked[1] + " builds, " + asked[2] + " held for staff)",
                offered.isEmpty() && asked[0] > 0, offered.toString());

        // ...and the hazard that skip answers, caused: a village where the labs are the only templates the sector has and nobody is there to staff them. Without the skip the first lab
        // would be the plan's staffing hold (Decision.unstaffed()), which the sector's want for posts and the labour market's price would read.
        Game bare = DiscoveryCheck.cityWithSchools("prize-plan-bare", true, false);
        DiscoveryCheck.play(bare, 2);
        bare.getBuildingManager().getTemplates().removeIf(t -> BS.equals(t.getSector()) && t.getResearches() == null);
        ham.citybuildersim.sectors.BusinessServices only = bare.getSectors().businessServices();
        BuildingsTemplate lab = bare.labTemplate(F);
        boolean labsOnly = bare.getBuildingManager().getTemplatesBySector(BS).size() == 4;
        Sector.Staffing staffing = only.staffing(lab);
        BusinessInvestment.Decision d = only.plan(bare.getBusinessInvestment(), bare);
        check("fixture: the sector's only templates are the four labs and the city cannot staff one (" + staffing.why(lab.getName()) + ")", labsOnly && !staffing.passes());
        check("...and the plan still offers none and holds for nobody's staff: no template, no hold, no build", d != null && !d.build && d.template == null && d.heldForStaff == null, d == null ? "null" : d.reason);
    }

    /* ==================== 12. investors build a lab ==================== */

    static void sectionTwelve() {
        System.out.println("\n--- 12. investors build a lab while the purse pays for it: owners first, never a loan, one a field a month, only in a city founded the 1900 way ---");
        Game a = labCity("prize-order-a", true, F, 1), b = labCity("prize-order-b", true, F, 1), c = labCity("prize-order-c", false, F, 1);
        Research ra = a.getResearch(), rb = b.getResearch(), rc = c.getResearch();
        int m = a.getMonth() + 1;
        double[] w = a.getPopulationManager().getWagesPerType().clone();
        BuildingsTemplate lab = a.labTemplate(F);
        check("fixture: three villages at month " + a.getMonth() + ", the first lab's month less one: two founded the 1900 way with the same history, one the default way, no lab and no prize",
                a.getMonth() == b.getMonth() && a.getMonth() == c.getMonth() && Arrays.equals(w, b.getPopulationManager().getWagesPerType()) && a.foundedUnfitted() && b.foundedUnfitted() && !c.foundedUnfitted()
                        && a.getSectors().businessServices().getCash() == b.getSectors().businessServices().getCash() && labsInAll(a) == 0 && labsInAll(b) == 0 && labsInAll(c) == 0 && ra.prizesForSave().isEmpty());
        double prize = justWantsALab(a, F);
        check("fixture: a prize exists at which the model wants a lab here next month: the least cover of the open problems that does (" + prize + ")", !Double.isNaN(prize));
        ra.setPrize(F, prize);
        boolean atPrize = ra.labsWanted(F, m, w) >= 1;
        ra.setPrize(F, Math.nextDown(prize));
        boolean hairUnder = ra.labsWanted(F, m, w) == 0;
        check("...and it wants one at that prize and none a hair under it", atPrize && hairUnder);
        ra.setPrize(F, prize);
        rb.setPrize(F, Math.nextDown(prize));
        rc.setPrize(F, prize);
        check("fixture: the default-way village's model would want a lab at the same prize (its research is empty: it has not run)", rc.labsWanted(F, m, c.getPopulationManager().getWagesPerType().clone()) >= 1);
        Entry target = ra.labTarget(F, m, w);
        double cost = a.getBusinessInvestment().getCostOf(lab, 1), stake = ra.labStake(F, m, w);
        check("fixture: the lab has a target, a cost and a stake above nothing", target != null && cost > 0 && stake > 0);

        playHanded(a, 1);
        playHanded(b, 1);
        playHanded(c, 1);
        check("within a month of play a lab is ordered: one is on site, none standing yet, and the month's word under " + Game.LABS_SLOT + " names it and the problem its purse pays for",
                a.labsOnSite(F) == 1 && a.labsStanding(F) == 0 && a.getLastInvestment(Game.LABS_SLOT).equals(String.format("Built %s - the purse pays for %s", lab.getName(), target.name())), labsOf(a) + " | " + a.getLastInvestment(Game.LABS_SLOT));
        double raised = a.getEconomyManager().getEquityRaised(BS) - b.getEconomyManager().getEquityRaised(BS);
        check("...the owners put up money for it: Business Services raised more than its twin that built no lab, and no more than the lab's cost and its stake (" + raised + " of " + (cost + stake) + ")",
                raised > 0 && raised <= cost + stake + 1e-6);
        near("...and no loan: Business Services borrowed this month what its twin did", a.getEconomyManager().getBusinessDebtManager().getLentThisMonth(BS), b.getEconomyManager().getBusinessDebtManager().getLentThisMonth(BS),
                1e-9 * Math.max(1, b.getEconomyManager().getBusinessDebtManager().getLentThisMonth(BS)));
        check("with the prize a hair under, none is ordered: nothing on site, and nothing said", labsInAll(b) == 0 && b.getLastInvestment(Game.LABS_SLOT).isEmpty(), labsOf(b) + " | " + b.getLastInvestment(Game.LABS_SLOT));
        check("a village founded the default way with the same prize orders none and says nothing", labsInAll(c) == 0 && c.getLastInvestment(Game.LABS_SLOT).isEmpty(), labsOf(c));
        check("...and the lab is in no other field, and no other village ordered one", labsInAll(a) == 1 && a.labsOnSite(Field.MEDICINE) + a.labsOnSite(Field.BUSINESS) + a.labsOnSite(Field.LAW) == 0);

        // At most one a field a month: medicine's second lab is wanted at once with the first.
        Game d = labCity("prize-order-med", true, Field.MEDICINE, 2);
        Research rd = d.getResearch();
        int md = d.getMonth() + 1;
        double[] wd = d.getPopulationManager().getWagesPerType().clone();
        double most = 0;
        for (Entry e : rd.openProblems(Field.MEDICINE, md)) if (Research.beforeTheWorld(e, rd.pile(e.code()), md)) most = Math.max(most, coverNow(d, e));
        rd.setPrize(Field.MEDICINE, most);
        int wanted = rd.labsWanted(Field.MEDICINE, md, wd);
        check("fixture: a prize that covers every problem the world leaves a team time for makes the model want " + wanted + " medical labs at once, with none yet", wanted >= 2 && labsInAll(d) == 0);
        playHanded(d, 1);
        int first = d.labsOnSite(Field.MEDICINE) + d.labsStanding(Field.MEDICINE);
        check("...yet the investors order one that month, never two: at most one lab a field a month", first == 1, labsOf(d));
        int wantedNext = rd.labsWanted(Field.MEDICINE, d.getMonth() + 1, d.getPopulationManager().getWagesPerType().clone());
        playHanded(d, 1);
        int second = d.labsOnSite(Field.MEDICINE) + d.labsStanding(Field.MEDICINE);
        check("...and the next month one more only if the model still wants more than the one it has (wanted " + wantedNext + "): " + second + " labs", second - first <= 1 && (wantedNext > first ? second == first + 1 : second == first), labsOf(d));
    }

    /* ==================== 13. labs at work ==================== */

    static void sectionThirteen() {
        System.out.println("\n--- 13. a lab at work: its researchers are the posts' filled ones, and go for the problem the purse pays for ---");
        Game g = labCity("prize-work", true, F, 1);
        Research r = g.getResearch();
        BuildingsTemplate lab = g.labTemplate(F);
        long[] before = g.getBuildingManager().getTotalJobs();
        DiscoveryCheck.quietly(() -> g.buildStack(lab, 2, true));
        long[] added = new long[before.length];
        long[] after = g.getBuildingManager().getTotalJobs();
        for (int i = 0; i < added.length; i++) added[i] = after[i] - before[i];
        long[] twoLabs = Research.labJobs(F);
        for (int i = 0; i < twoLabs.length; i++) twoLabs[i] *= 2;
        check("fixture: two labs stand, and the buildings' jobs grew by two labs' (Research.labJobs() twice), which the city's jobs count", g.labsStanding(F) == 2 && DiscoveryCheck.same(added, twoLabs) && DiscoveryCheck.same(g.getJobs(), DiscoveryCheck.plus(after, r.jobs())));
        double prize = justWantsALab(g, F);
        check("fixture: a prize covers a problem the world leaves time for", !Double.isNaN(prize));
        r.setPrize(F, prize);
        int m = g.getMonth() + 1;
        double[] w = g.getPopulationManager().getWagesPerType().clone();
        Entry target = r.labTarget(F, m, w);
        check("fixture: the labs have a target on the chase list", target != null && !r.chase(F, m, w).isEmpty());
        playHanded(g, 1);
        double[] fill = g.getResearchFill();
        double expected = Research.filledResearchers(F, 2 * Research.LAB_POSTS, fill);
        near("the month's lab researchers are the filled researchers of the labs' posts at the month's fill, two labs' hundred posts each (" + expected + ")", r.labResearchers(F), expected, 1e-9 * Math.max(1, expected));
        check("...and there are some: the posts are filled", expected > 0);
        double onProblems = 0;
        for (Entry e : r.openProblems(F, m)) onProblems += r.labShare(e.code());
        check("their work lands on the target: the lab share on it is above nothing", r.labShare(target.code()) > 0, "share " + r.labShare(target.code()));
        near("...and every lab researcher is on a problem or idle: the shares and the idle make the researchers (" + onProblems + " on problems, " + r.labsIdle(F) + " idle)", onProblems + r.labsIdle(F), r.labResearchers(F), 1e-9 * Math.max(1, expected));
        check("...the labs' part of the target's pile is above nothing and within the pile", r.labPile(target.code()) > 0 && r.labPile(target.code()) <= r.pile(target.code()) + 1e-12);
        check("a field with no lab has no lab researchers", r.labResearchers(Field.MEDICINE) == 0 && r.labResearchers(Field.BUSINESS) == 0 && r.labResearchers(Field.LAW) == 0);
    }

    /* ==================== 14. the purse paid ==================== */

    /** A city with a lab at work, a prize and a find just made: the city, the problem found, and what the pot held before. */
    static final class Found {
        Game g;
        Entry target;
        double prize, potBefore;
    }

    /** A village at month 80 with a lab standing and a prize (`prize`, or the one that just wants a lab if 0), a month played with the lab at work, then the target's pile put just under its size and the month of the find played. */
    static Found findCity(String label, double prize) {
        Found f = new Found();
        f.g = labCity(label, true, F, 1);
        Game g = f.g;
        Research r = g.getResearch();
        BuildingsTemplate lab = g.labTemplate(F);
        DiscoveryCheck.quietly(() -> g.buildStack(lab, 1, true));
        f.prize = prize > 0 ? prize : justWantsALab(g, F);
        r.setPrize(F, f.prize);
        playHanded(g, 1);
        f.target = r.labTarget(F, g.getMonth() + 1, g.getPopulationManager().getWagesPerType().clone());
        if (f.target == null) return f;
        pileJustUnder(g, f.target);
        f.potBefore = r.pot(F);
        playHanded(g, 1);
        return f;
    }

    static void sectionFourteen() {
        System.out.println("\n--- 14. the purse is paid: the labs' share to Business Services' till through the treasury's prizes line, the schools' to the pot, told in the inbox ---");
        Found f = findCity("prize-paid", 0);
        Game g = f.g;
        Research r = g.getResearch();
        if (f.target == null) { check("fixture: the lab has a target on the chase list after its first month at work", false); return; }
        EconomyManager e = g.getEconomyManager();
        Research.Purse purse = r.purses().get(f.target.code());
        check("fixture: the lab's month found " + f.target.code() + " with the prize " + f.prize + " set, and it paid a purse with both parts above nothing",
                r.foundThisMonth().contains(f.target.code()) && purse != null && purse.toLabs() > 0 && purse.toSchools() > 0, String.valueOf(r.purses()));
        near("the purse is the prize, split by the work piled", purse.toLabs() + purse.toSchools(), f.prize, 1e-9 * f.prize);
        near("the treasury's prizes line paid exactly the labs' share (" + g.getPrizePaid() + " of " + r.toLabs(F) + "), nothing owed", g.getPrizePaid(), r.toLabs(F), 1e-9 * r.toLabs(F));
        check("...none of it refused past the ceiling: no arrears", !g.hasArrears() && g.getArrearsByLine().isEmpty());
        SectorBooks.SectorMonth books = g.getSectorBooks().get(BS);
        near("Business Services' cash flow statement received the same figure from the city (its cash rose by what the treasury paid)", books.fromTheCity(), g.getPrizePaid(), 1e-9 * g.getPrizePaid());
        check("...and the statement explains the sector's till to the cent: nothing unexplained", Math.abs(books.unexplained()) <= MoneyAudit.tolerance(1e-6, books.unexplainedScale()), String.valueOf(books.unexplained()));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        check("the month's money audit closes with the transfer in it", Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved()), "residual " + audit.residual + " of " + audit.moved());
        near("the national accounts carry it where the business subsidies are (a transfer to a firm, not government consumption): the month's subsidies paid", e.getSubsidiesPaid(), g.getTotalSubsidyPaid() + g.getPrizePaid(), 1e-9 * g.getPrizePaid());
        near("the schools' share is in the field's pot", r.pot(F), f.potBefore + purse.toSchools(), 1e-9 * Math.max(1, purse.toSchools()));
        List<Notice> notices = DiscoveryCheck.noticesKeyed(g, "found:" + f.target.code());
        String body = notices.isEmpty() ? "" : String.join(" ", notices.get(0).getBody());
        check("the inbox's \"found\" notice carries the sentence: " + Inbox.prizeSentence(purse), notices.size() == 1 && body.contains(Inbox.prizeSentence(purse)), body);
        check("the sentence names the prize and both parts, and a part of nothing would be left out",
                Inbox.prizeSentence(purse).contains("Research Labs") && Inbox.prizeSentence(purse).contains("schools' grants")
                        && !Inbox.prizeSentence(new Research.Purse(0, 5)).contains("Research Labs") && !Inbox.prizeSentence(new Research.Purse(5, 0)).contains("schools' grants"));
        check("the treasury's prizes line is discretionary like the subsidies: not a promise, not a purchase, owed as arrears when refused, labelled for the prizes, and the last of the lines",
                !TreasuryLine.PRIZES.promise && !TreasuryLine.PRIZES.purchase && TreasuryLine.PRIZES.accruesArrears() && TreasuryLine.PRIZES.label.equals("Research prizes")
                        && TreasuryLine.values()[TreasuryLine.values().length - 1] == TreasuryLine.PRIZES);
        playHanded(g, 1);
        check("a month with no find pays no prize: the month's prize paid is zero again", g.getPrizePaid() == 0 && r.toLabs(F) == 0 && r.purses().isEmpty());

        // A purse past what the treasury may spend: paid what it can, the rest owed to Business Services.
        Found big = findCity("prize-paid-big", BIG);
        Game bg = big.g;
        if (big.target == null) { check("fixture: the lab has a target on the chase list after its first month at work, at the largest prize", false); return; }
        Research rb = bg.getResearch();
        double owed = rb.toLabs(F), paid = bg.getPrizePaid();
        Double arrears = bg.getArrearsByLine().get(TreasuryLine.PRIZES);
        check("a purse past the treasury's room is paid what the treasury may spend and the rest is owed, as arrears on the prizes' line (" + paid + " of " + owed + ")",
                owed > 0 && paid > 0 && paid < owed && arrears != null && Math.abs(arrears - (owed - paid)) <= 1e-6 * owed, String.valueOf(arrears));
        SectorBooks.SectorMonth bigBooks = bg.getSectorBooks().get(BS);
        check("...and the sector's books still close on what it was paid, and the audit with them", Math.abs(bigBooks.fromTheCity() - paid) <= 1e-9 * Math.max(1, paid)
                && Math.abs(bigBooks.unexplained()) <= MoneyAudit.tolerance(1e-6, bigBooks.unexplainedScale()) && Math.abs(bg.getLastMoneyAudit().residual) <= MoneyAudit.tolerance(bg.getLastMoneyAudit().moved()));
    }

    /* ==================== 15. idle labs are shed ==================== */

    static void sectionFifteen() {
        System.out.println("\n--- 15. a lab with nothing to chase idles for LAB_IDLE_MONTHS months and is then sold back, as any building is ---");
        Game g = DiscoveryCheck.cityWithSchools("prize-shed", true, false);
        Research r = g.getResearch();
        playHanded(g, 2);
        BuildingsTemplate lab = g.labTemplate(F);
        DiscoveryCheck.quietly(() -> g.buildStack(lab, 1, true));
        check("fixture: a lab stands in a village with no prize, nothing to chase and no count yet", g.labsStanding(F) == 1 && r.prize(F) == 0 && r.labIdleMonths(F) == 0 && !r.labTooMany(F));
        List<String> early = new ArrayList<>();
        for (int n = 1; n <= Research.LAB_IDLE_MONTHS; n++) {
            playHanded(g, 1);
            if (g.labsStanding(F) != 1 || r.labIdleMonths(F) != n || r.labTooMany(F) != (n == Research.LAB_IDLE_MONTHS)) early.add("month " + n + ": " + g.labsStanding(F) + " standing, " + r.labIdleMonths(F) + " idle months");
        }
        check("not one month before: through the first " + Research.LAB_IDLE_MONTHS + " months of play the lab stands, its idle months are counted one by one, and it is too many only at the last", early.isEmpty(), early.toString());
        int shedFrom = g.getMonth();
        playHanded(g, 1);
        check("the next month it is sold back: no lab stands, the count starts again, the sale is in the demolition log under Business Services",
                g.labsStanding(F) == 0 && r.labIdleMonths(F) == 0 && g.getDemolitionLog().recent(g.getMonth()).stream().anyMatch(en -> en.building.equals(lab.getName()) && en.quantity == 1 && en.sector.equals(BS) && en.month == g.getMonth()),
                labsOf(g) + " " + r.labIdleMonths(F));
        playHanded(g, 2);
        check("...and no other lab is ordered or sold in the months after: with no prize nobody wants one", labsInAll(g) == 0);

        // Held, as the rest of the sector's retirement is: a harness that holds Business Services keeps its labs.
        Game held = DiscoveryCheck.cityWithSchools("prize-shed-held", true, false);
        held.getBusinessInvestment().holdSector(BS);
        playHanded(held, 2);
        DiscoveryCheck.quietly(() -> held.buildStack(lab, 1, true));
        playHanded(held, Research.LAB_IDLE_MONTHS + 3);
        check("a lab of a sector a harness holds is not sold, though its idle months pass the threshold (" + held.getResearch().labIdleMonths(F) + ")",
                held.labsStanding(F) == 1 && held.getResearch().labTooMany(F));
    }

    /* ==================== 16. the save ==================== */

    static void sectionSixteen() throws Exception {
        System.out.println("\n--- 16. the save: prizes, pots, the labs' piles, the shares, the idle counts and the month's prize paid come back, and a loaded city plays on as the one saved ---");
        Game g = labCity("prize-save", true, F, 1);
        Research r = g.getResearch();
        BuildingsTemplate lab = g.labTemplate(F);
        DiscoveryCheck.quietly(() -> g.buildStack(lab, 2, true));
        r.setPrize(F, justWantsALab(g, F));
        playHanded(g, 3);
        Entry target = r.labTarget(F, g.getMonth() + 1, g.getPopulationManager().getWagesPerType().clone());
        pileJustUnder(g, target);
        playHanded(g, 1);
        check("fixture: a city with a prize, two labs, a find and its purse paid, work piled by the labs, last month's shares and a lab too many",
                !r.prizesForSave().isEmpty() && !r.potsForSave().isEmpty() && !r.labPilesForSave().isEmpty() && !r.citySharesForSave().isEmpty() && !r.labIdleMonthsForSave().isEmpty() && g.getPrizePaid() > 0,
                r.prizesForSave() + " " + r.potsForSave() + " " + r.labPilesForSave() + " " + r.citySharesForSave() + " " + r.labIdleMonthsForSave() + " " + g.getPrizePaid());
        GameFiles.Result wrote = EquipmentCheck.save(g, "prize kept");
        check("the city is saved", wrote.ok, wrote.message());
        String json = Files.readString(g.getGameFiles().saveFile(1));
        check("...with the new keys under its research", json.contains("\"prizes\"") && json.contains("\"pots\"") && json.contains("\"labPiles\"") && json.contains("\"cityShares\"") && json.contains("\"labIdleMonths\"") && json.contains("\"prizePaid\""));
        Game back = new Game(g.getGameFiles());
        DiscoveryCheck.quietly(() -> back.loadGameSave(1));
        Research b = back.getResearch();
        check("loaded without a failure", back.getLoadFailure() == null, String.valueOf(back.getLoadFailure()));
        check("the prizes, the pots, the labs' piles, the city's shares and the idle counts come back equal",
                b.prizesForSave().equals(r.prizesForSave()) && b.potsForSave().equals(r.potsForSave()) && b.labPilesForSave().equals(r.labPilesForSave())
                        && b.citySharesForSave().equals(r.citySharesForSave()) && b.labIdleMonthsForSave().equals(r.labIdleMonthsForSave()),
                b.prizesForSave() + " " + b.potsForSave() + " " + b.labPilesForSave() + " " + b.citySharesForSave() + " " + b.labIdleMonthsForSave());
        near("...and the month's prize paid", back.getPrizePaid(), g.getPrizePaid(), 1e-12 * g.getPrizePaid());
        check("...the labs stand as they did and the jobs are the same", back.labsStanding(F) == 2 && DiscoveryCheck.same(back.getJobs(), g.getJobs()));

        // Both play on, month by month, for two years: the same finds, labs, pots, piles and cash every month, the labs standing in some of them and a purse paid in some.
        List<String> apart = new ArrayList<>();
        int withLabs = 0, withPurse = 0;
        for (int month = 1; month <= 24; month++) {
            playHanded(g, 1);
            playHanded(back, 1);
            if (g.labsStanding(F) > 0) withLabs++;
            if (g.getPrizePaid() > 0) withPurse++;
            String why = null;
            if (!b.foundHere().equals(r.foundHere())) why = "finds " + b.foundHere().keySet() + " vs " + r.foundHere().keySet();
            else if (!labsOf(back).equals(labsOf(g))) why = "labs " + labsOf(back) + " vs " + labsOf(g);
            else if (Math.abs(back.getPrizePaid() - g.getPrizePaid()) > 1e-9 * Math.max(1, g.getPrizePaid())) why = "prize paid " + back.getPrizePaid() + " vs " + g.getPrizePaid();
            else if (Math.abs(back.getSectors().businessServices().getCash() - g.getSectors().businessServices().getCash()) > 1e-9 * Math.max(1, Math.abs(g.getSectors().businessServices().getCash()))) {
                why = "cash " + back.getSectors().businessServices().getCash() + " vs " + g.getSectors().businessServices().getCash();
            }
            if (why != null && apart.isEmpty()) apart.add("month " + g.getMonth() + ": " + why);
        }
        check("two years on, month by month, the saved city and the loaded one have the same finds (in order, " + r.foundHere().size() + " of them), the same labs, the same prize paid and the same cash in Business Services' till"
                + " (labs standing in " + withLabs + " of the 24 months, a purse paid in " + withPurse + ")", apart.isEmpty() && new ArrayList<>(b.foundHere().keySet()).equals(new ArrayList<>(r.foundHere().keySet())), apart.toString());
        boolean same = b.potsForSave().keySet().equals(r.potsForSave().keySet()) && b.piles().keySet().equals(r.piles().keySet()) && b.labPilesForSave().keySet().equals(r.labPilesForSave().keySet());
        for (Map.Entry<String, Double> en : r.potsForSave().entrySet()) same &= Math.abs(en.getValue() - b.potsForSave().getOrDefault(en.getKey(), -1.0)) <= 1e-9 * Math.max(1, en.getValue());
        for (Map.Entry<String, Double> en : r.piles().entrySet()) same &= Math.abs(en.getValue() - b.piles().getOrDefault(en.getKey(), -1.0)) <= 1e-9 * Math.max(1, en.getValue());
        for (Map.Entry<String, Double> en : r.labPilesForSave().entrySet()) same &= Math.abs(en.getValue() - b.labPilesForSave().getOrDefault(en.getKey(), -1.0)) <= 1e-9 * Math.max(1, en.getValue());
        check("...and at the end the same pots, piles and labs' piles", same, r.potsForSave() + " " + r.labPilesForSave() + " vs " + b.potsForSave() + " " + b.labPilesForSave());

        // A city with only a prize writes the key.
        Game only = DiscoveryCheck.cityWithSchools("prize-save-only", false, false);
        DiscoveryCheck.play(only, 2);
        Research ro = only.getResearch();
        check("fixture: a city founded the default way that has played two months holds no research at all", ro.isEmpty() && labsInAll(only) == 0);
        ro.setPrize(Field.LAW, 12.5);
        check("a prize alone makes it hold some", !ro.isEmpty());
        GameFiles.Result wroteOnly = EquipmentCheck.save(only, "prize only");
        String onlyJson = Files.readString(only.getGameFiles().saveFile(1));
        check("...so the save has the research key, with the prize under its field", wroteOnly.ok && onlyJson.contains("\"research\"") && onlyJson.contains("\"prizes\"") && onlyJson.contains("\"LAW\""), wroteOnly.message());
        Game onlyBack = new Game(only.getGameFiles());
        DiscoveryCheck.quietly(() -> onlyBack.loadGameSave(1));
        check("...and a load puts it back: the prize is 12.5 in law and nothing in the other fields",
                onlyBack.getLoadFailure() == null && onlyBack.getResearch().prize(Field.LAW) == 12.5 && onlyBack.getResearch().prize(Field.ENGINEERING) == 0 && onlyBack.getPrizePaid() == 0);
    }

    /* ==================== 17. a city founded the default way; a reform of the money ==================== */

    static void sectionSeventeen() {
        System.out.println("\n--- 17. a city founded the default way orders nothing, pays nothing and has no lab, whatever the prizes; a reform of the money scales prizes, pots and the prize paid ---");
        Game d = labCity("prize-default", false, F, 1);
        Research rd = d.getResearch();
        for (Field f : Field.values()) rd.setPrize(f, BIG);
        int m = d.getMonth() + 1;
        double[] w = d.getPopulationManager().getWagesPerType().clone();
        check("fixture: a village founded the default way at month " + d.getMonth() + ", every prize set, where the model would want a lab of engineering (" + rd.labsWanted(F, m, w) + ") and nothing is in its research",
                !d.foundedUnfitted() && rd.labsWanted(F, m, w) >= 1 && labsInAll(d) == 0 && rd.potsForSave().isEmpty() && rd.foundHere().isEmpty());
        boolean orderedOrPaid = false;
        for (int i = 0; i < 6; i++) {
            playHanded(d, 1);
            if (labsInAll(d) != 0 || !d.getLastInvestment(Game.LABS_SLOT).isEmpty() || d.getPrizePaid() != 0 || rd.toLabs(F) != 0 || !rd.purses().isEmpty() || d.hasArrears()) orderedOrPaid = true;
        }
        check("six months on nothing is ordered, said, paid or owed, and nothing is found, piled or potted: no lab, no prize paid, no purse",
                !orderedOrPaid && rd.foundHere().isEmpty() && rd.potsForSave().isEmpty() && rd.labPilesForSave().isEmpty() && rd.piles().isEmpty(), labsOf(d));

        // A reform of the money scales what is money.
        Found f = findCity("prize-reform", 0);
        Game g = f.g;
        Research r = g.getResearch();
        if (f.target == null) { check("fixture: the lab has a target on the chase list after its first month at work", false); return; }
        double cash = g.getCash(), prize = r.prize(F), pot = r.pot(F), paid = g.getPrizePaid();
        Map<String, Double> piles = r.piles();
        Map<String, Integer> found = r.foundHere();
        check("fixture: a prize, a pot, a prize paid this month and a find", prize > 0 && pot > 0 && paid > 0 && !found.isEmpty());
        boolean[] ok = new boolean[1];
        DiscoveryCheck.quietly(() -> ok[0] = g.reformCurrencyForTest(100));
        double scale = g.getCash() / cash;
        check("fixture: the reform went through and moved the money: the treasury's cash is " + scale + " of what it was", ok[0] && scale != 1 && scale > 0);
        near("the prize is scaled as the cash is", r.prize(F), prize * scale, 1e-9 * prize * scale);
        near("...the pot too", r.pot(F), pot * scale, 1e-9 * pot * scale);
        near("...and the month's prize paid", g.getPrizePaid(), paid * scale, 1e-9 * paid * scale);
        check("the work piled, the finds and the other fields' prizes are not money and are as they were", r.piles().equals(piles) && r.foundHere().equals(found) && r.prize(Field.LAW) == 0 && r.prize(Field.MEDICINE) == 0);
    }
}
