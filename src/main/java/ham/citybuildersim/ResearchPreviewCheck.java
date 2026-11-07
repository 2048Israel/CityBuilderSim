package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

/**
 * The preview each dial card on Policy > Research shows: what a set of dials would find here over ten years (R3b, slice A: ResearchPreview, and the
 * words and the cache in ResearchView; docs/studies/research-design.md 5.4, 6.2, 9).
 *
 * WHY. The preview is the one place the game says a number about the FUTURE of a random process, and its numbers are the player's reason to move a
 * dial. A step that differs from the game's own by one rule (the labs read the city's share of this month and not the last, a find not counting at once
 * for the fields after it) would give a card that promises what the month will not deliver, and no other assertion in the model would see it; and a
 * sampler that leaked the city's own draw would show the player a size they are not to see. So: the two cities of research-design 5.4 are held to the
 * study's figures, which research_model.py made with the study's own model, within their Monte Carlo error; the preview's step is held to
 * Research.month() itself, month for month, over given sizes; the answer is the same for the same inputs and is never run twice for them; and the
 * words are caused from both sides of each edge.
 *
 * What this has to prove:
 *
 *   1. THE POSTS' COST. The wages chosen for the study's two cities give the study's post costs.
 *   2. THE YOUNG CITY, June 1955. Each figure of 5.4's table and the study's run (research_model.py forward(), figures.py), game against study within
 *      three standard errors of the two, the chase of the prize and the labs it brings.
 *   3. THE BIG CITY, June 1979. The same, with the finds the city made, the grants staged in the four ways and the medical prize.
 *  3b. THE STUDY'S MODEL, WRITTEN AGAIN. research_model.py forward() ported line for line (dicts of months and piles, the study's own effort, caps and windows,
 *      its 40 discoveries), run for the as-is big city, the same with its dials staged, and the young city with a medical grant: the preview agrees with it to four times the Monte Carlo error of the
 *      two (four and not three, for twelve comparisons from one fixed seed: a 3-sigma bound is crossed by chance in one run of a few hundred). A tight test
 *      where the study's own 1,000 draws are a loose one: each of its figures is a single sample (its medicine ones run 2 or 3 errors under the limit they
 *      converge to, the draws being shared between its scenarios), and the 1,000,000-draw preview and a 400,000-draw port agree to 0.001 of a find.
 *   4. THE STEP. With the sizes the city's own draw gives (Research.hiddenSize()), the preview's single path finds what Research.month() finds, in the
 *      same months, from both cities' states, without a prize and with one (the labs given to month() as labsWanted() would want them).
 *   5. THE DRAW. A size is always above the pile on it; the tail sampler draws the distribution plain redrawing does; the mean size is the work.
 *   6. REPRODUCIBLE AND CACHED. The same inputs give the same Result; another seed another one; ResearchView's cache returns without running again, and
 *      a new month, a changed dial or a changed pile runs afresh.
 *   7. THE ANSWER'S SHAPE. The purse is the prize times the finds, the chance is a share, the fields add to the total, nothing to find gives nothing.
 *   8. THE WORDS. A chance when the larger of the two expected counts is under 1, else the count to two places; "none", "under 1%" and "under 0.01"
 *      on their edges, both sides.
 *   9. THE COST. What 400 draws take in the big city (printed).
 *
 * The cities are built as the study's state restores them: a Research restored to the piles and the finds, the city's share of each problem the water fill
 * of the as-is researchers over the open problems (the share the last month() would have left; Research.waterFill() is the study's), the wages chosen
 * so that a post costs what the study's does. The study's game months are the study's plus 600 (the game's epoch is 1900, the study's 1950).
 */
public class ResearchPreviewCheck {

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

    /** The draws the harness makes the game's preview from (the screen uses ResearchPreview.DRAWS): enough that the game's error is a quarter of the study's. */
    static final int DRAWS = 20_000;

    /** The draws the study's figures were made from, for the binomial error of a chance it quotes without one. */
    static final int STUDY_DRAWS = 1000;

    static final Field ENG = Field.ENGINEERING, MED = Field.MEDICINE, BUS = Field.BUSINESS, LAW = Field.LAW;

    /* =====================================================================
       THE FIXTURES: the study's two cities as a Research
       ===================================================================== */

    /** The wages (D$ a month) that make a post cost what the study's does (research_model.py WAGE: COLLEGE 5.5k, PROF 8.2k, SENIOR 9.6k, ELITE 15.6k, SKILLED 4.5k). */
    static double[] studyWages() {
        double[] w = new double[JobType.values().length];
        w[JobType.UNIV_HIGHTECH_ENG.ordinal()] = 9600;
        w[JobType.UNIV_SCIENCE.ordinal()] = 8200;
        w[JobType.UNIV_DOCTOR.ordinal()] = 15600;
        w[JobType.UNIV_FINANCE.ordinal()] = 9600;
        w[JobType.UNIV_POLICY.ordinal()] = 8200;
        w[JobType.UNIV_LAW.ordinal()] = 15600;
        w[JobType.COLLEGE_ENGINEERING.ordinal()] = 5500;
        w[JobType.COLLEGE_HEALTH.ordinal()] = 5500;
        w[JobType.DIPLOMA.ordinal()] = 4500;
        return w;
    }

    static EnumMap<Field, Double> byField(double eng, double med, double bus, double law) {
        EnumMap<Field, Double> out = new EnumMap<>(Field.class);
        out.put(ENG, eng);
        out.put(MED, med);
        out.put(BUS, bus);
        out.put(LAW, law);
        return out;
    }

    static EnumMap<Field, Double> noPrize() {
        return byField(0, 0, 0, 0);
    }

    /** A city's state: the month, what is piled and found, and the researchers it has as it is. */
    record City(String name, int month, Map<String, Double> piles, Map<String, Integer> found, EnumMap<Field, Double> people) {

        /** A Research restored to the state, with the prizes given, and the city's share of each problem the water fill of the as-is researchers over the open problems. */
        Research research(EnumMap<Field, Double> prize) {
            Research r = new Research();
            r.restore(null, null, piles, found);
            Map<String, Double> shares = new HashMap<>();
            for (Field f : Field.values()) {
                List<Entry> open = r.openProblems(f, month);
                double[] caps = new double[open.size()];
                for (int i = 0; i < caps.length; i++) caps[i] = Research.teamCap(open.get(i));
                Research.Fill fill = Research.waterFill(people.get(f), caps);
                for (int i = 0; i < caps.length; i++) if (fill.share()[i] > 0) shares.put(open.get(i).code(), fill.share()[i]);
            }
            Map<String, Double> prizes = new HashMap<>();
            for (Field f : Field.values()) if (prize.get(f) > 0) prizes.put(f.name(), prize.get(f));
            r.restorePrizes(prizes, null, null, shares, null);
            return r;
        }
    }

    /** The young city of research-design 5.4: June 1955 (game month 666), one University since 1953, nothing found here, the study's piles. */
    static City young() {
        Map<String, Double> piles = new HashMap<>();
        piles.put("E5", 3.202222);
        piles.put("E6", 3.202222);
        piles.put("E7", 3.202222);
        piles.put("E8", 1.882222);
        piles.put("E9", 0.122222);
        piles.put("E10", 0.122222);
        piles.put("M4", 5.744444);
        piles.put("M5", 5.744444);
        return new City("young city, June 1955", 666, piles, new HashMap<>(), byField(8.8, 8.8, 2.4, 2.4));
    }

    /** The big city of 5.4: June 1979 (game month 954), four finds here, the study's piles. */
    static City big() {
        Map<String, Double> piles = new HashMap<>();
        piles.put("E17", 463.663382);
        piles.put("M9", 207.968244);
        piles.put("M10", 94.53102);
        piles.put("B6", 97.791837);
        piles.put("L3", 1.533333);
        Map<String, Integer> found = new HashMap<>();
        found.put("M7", 878);
        found.put("E15", 883);
        found.put("M8", 928);
        found.put("E16", 954);
        return new City("big city, June 1979", 954, piles, found, byField(412.1452285841101, 226.87444794952683, 90.26938775510204, 18.4));
    }

    /** A copy of a research with everything the preview and the month read: grants, posts, piles, finds, prizes, pots, the labs' piles and the city's shares. */
    static Research copyOf(Research r) {
        Research c = new Research();
        c.restore(r.grantsForSave(), r.postsForSave(), r.piles(), r.foundHere());
        c.restorePrizes(r.prizesForSave(), r.potsForSave(), r.labPilesForSave(), r.citySharesForSave(), r.labIdleMonthsForSave());
        return c;
    }

    static ResearchPreview.Result run(City city, EnumMap<Field, Double> people, EnumMap<Field, Double> prize) {
        return ResearchPreview.preview(city.research(prize), city.month(), people, prize, studyWages(), 1.0, DRAWS, ResearchPreview.SEED);
    }

    /* =====================================================================
       main
       ===================================================================== */

    public static void main(String[] args) {
        sectionOne();
        sectionTwo();
        sectionThree();
        sectionThreeB();
        sectionFour();
        sectionFive();
        sectionSix();
        sectionSeven();
        sectionEight();
        sectionNine();
        System.out.println(fails == 0 ? "ResearchPreviewCheck: all " + passes + " ok" : "ResearchPreviewCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /** One figure of the study against the game's: within three standard errors of the two (and half a unit of the study's last printed place). */
    static void against(String label, double game, double gameSe, double study, double studySe, double printedUnit) {
        against(label, game, gameSe, study, studySe, printedUnit, 3);
    }

    /** The same within `sigmas` standard errors of the two. */
    static void against(String label, double game, double gameSe, double study, double studySe, double printedUnit, int sigmas) {
        double tolerance = sigmas * Math.hypot(gameSe, studySe) + printedUnit / 2;
        System.out.printf("         %-52s game %9.4f +/- %.4f   study %9.4f +/- %.4f%n", label, game, gameSe, study, studySe);
        check(label + ": the game's " + String.format("%.4f", game) + " is the study's " + String.format("%.4f", study) + " within " + sigmas + " standard errors",
                Math.abs(game - study) <= tolerance, String.format("%.6f from %.6f, tolerance %.6f", game, study, tolerance));
    }

    /** The binomial standard error of a share. */
    static double binomial(double p, int draws) {
        return Math.sqrt(p * (1 - p) / draws);
    }

    static void finds(String label, ResearchPreview.Result game, Field f, double study, double studySe) {
        against(label + " " + f.name().toLowerCase() + " finds", game.finds(f), game.findsSe(f), study, studySe, 1e-4);
    }

    static void chance(String label, ResearchPreview.Result game, Field f, double study) {
        against(label + " " + f.name().toLowerCase() + " chance", game.chance(f), binomial(game.chance(f), game.draws()), study, binomial(study, STUDY_DRAWS), 1e-3);
    }

    static void all(String label, ResearchPreview.Result game, double study, double studySe) {
        against(label + " all fields", game.total(), game.totalSe(), study, studySe, 1e-3);
    }

    /** The codes of a list of entries. */
    static List<String> codes(List<Entry> entries) {
        List<String> out = new ArrayList<>();
        for (Entry e : entries) out.add(e.code());
        return out;
    }

    /** The room a field's chase leaves the labs: what each problem on it can still take of its team cap, less the city's share (what Research.labsWanted() sums). */
    static double chaseRoom(Research r, Field f, int month, double[] wages) {
        double room = 0;
        for (Entry e : r.chase(f, month, wages)) room += Math.max(0, Research.teamCap(e) - r.cityShare(e.code()));
        return room;
    }

    /* ==================== 1. the posts' cost ==================== */

    static void sectionOne() {
        System.out.println("--- 1. the wages of the study's cities make a post cost what the study's does ---");
        double[] w = studyWages();
        near("an engineering post costs D$ 18,169.4 a month", Research.postCost(ENG, w), 18169.4, 0.05);
        near("a medical post costs D$ 23,775", Research.postCost(MED, w), 23775, 0.05);
        near("a business post costs D$ 12,250", Research.postCost(BUS, w), 12250, 0.05);
        near("a law post costs D$ 16,000", Research.postCost(LAW, w), 16000, 0.05);
    }

    /* ==================== 2. the young city ==================== */

    static void sectionTwo() {
        System.out.println("\n--- 2. the young city, June 1955: 5.4's previews against the study's run of them ---");
        City c = young();
        double[] w = studyWages();
        Research r = c.research(noPrize());
        ResearchPreview plan = ResearchPreview.plan(r, c.month(), c.people(), noPrize(), w, null);
        List<String> open = new ArrayList<>();
        for (Field f : Field.values()) for (Entry e : r.openProblems(f, c.month())) open.add(e.code());
        System.out.println("         open at month 666: " + open + "; the plan plays " + codes(plan.entries()));
        check("fixture: engineering has six open problems (E5 to E10), medicine two (M4, M5), business and law none (5.4)",
                codes(r.openProblems(ENG, c.month())).equals(List.of("E5", "E6", "E7", "E8", "E9", "E10")) && codes(r.openProblems(MED, c.month())).equals(List.of("M4", "M5"))
                        && r.openProblems(BUS, c.month()).isEmpty() && r.openProblems(LAW, c.month()).isEmpty(), open.toString());

        ResearchPreview.Result asIs = run(c, c.people(), noPrize());
        System.out.println("      as is");
        finds("as is", asIs, MED, 0.0020, 0.0014);
        finds("as is", asIs, ENG, 0, 0);

        ResearchPreview.Result engGrant = run(c, byField(8.8 + 550_000 / w2(ENG), 8.8, 2.4, 2.4), noPrize());
        System.out.println("      engineering grant D$ 0.55M (eng = 8.8 + 550 / 18.1694)");
        finds("engineering grant", engGrant, ENG, 0.0060, 0.0024);

        ResearchPreview.Result medGrant = run(c, byField(8.8, 8.8 + 700_000 / w2(MED), 2.4, 2.4), noPrize());
        System.out.println("      medical grant D$ 0.70M (med = 8.8 + 700 / 23.775)");
        finds("medical grant", medGrant, MED, 0.2580, 0.0141);
        chance("medical grant", medGrant, MED, 0.254);

        // The engineering prize: the study put 200 lab researchers on E7 alone (its 75%); the game's chase (R4) puts labs on every problem the purse covers that
        // a team could finish before the world, cheapest first, and the investors want the labs the room on it holds: 4 here, so 400 lab researchers on E10 and E7.
        EnumMap<Field, Double> engPrize = byField(140_000_000, 0, 0, 0);
        Research withPrize = c.research(engPrize);
        ResearchPreview.Result prize = ResearchPreview.preview(withPrize, c.month(), c.people(), engPrize, w, 1.0, DRAWS, ResearchPreview.SEED);
        System.out.println("      engineering prize D$ 140M (no grant): the game's chase, not the study's 200 researchers on E7");
        List<String> chase = codes(withPrize.chase(ENG, c.month() + 1, w));
        double room = chaseRoom(withPrize, ENG, c.month() + 1, w);
        System.out.println("         the chase in the first month: " + chase + ", room " + room + ", labs wanted " + withPrize.labsWanted(ENG, c.month() + 1, w));
        check("the chase is E10 and E7 (the cheapest the purse pays for and a team could finish before the world), cheapest first", chase.equals(List.of("E10", "E7")), chase.toString());
        near("...its room is the study's 395.13 researchers", room, 395.13, 0.5);
        check("...so the investors want 4 labs in the first month, in the model's own question and in the preview's",
                withPrize.labsWanted(ENG, c.month() + 1, w) == 4 && prize.labsFirstMonth(ENG) == 4, "model " + withPrize.labsWanted(ENG, c.month() + 1, w) + ", preview " + prize.labsFirstMonth(ENG));
        check("...and no other field wants one (no prize there)", prize.labsFirstMonth(MED) == 0 && prize.labsFirstMonth(BUS) == 0 && prize.labsFirstMonth(LAW) == 0);
        finds("engineering prize", prize, ENG, 2.7390, 0.0142);
        chance("engineering prize", prize, ENG, 1.000);
        against("engineering prize expected purse (D$ M)", prize.purse(ENG) / 1e6, 140 * prize.findsSe(ENG), 383.46, 140 * 0.0142, 0.005);
        near("...and the purse is the prize times the finds", prize.purse(ENG), 140_000_000 * prize.finds(ENG), 1e-6);
    }

    /** The cost of a post of the field at the study's wages. */
    static double w2(Field f) {
        return Research.postCost(f, studyWages());
    }

    /* ==================== 3. the big city ==================== */

    static void sectionThree() {
        System.out.println("\n--- 3. the big city, June 1979: 5.4's table against the study's run of it ---");
        City c = big();
        double[] w = studyWages();
        Research r = c.research(noPrize());
        List<String> open = new ArrayList<>();
        for (Field f : Field.values()) for (Entry e : r.openProblems(f, c.month())) open.add(e.code());
        System.out.println("         open at month 954: " + open);
        check("fixture: engineering has one open problem (E17, rail), medicine two (M9, M10), business one (B6) and law one (L3) (5.4)",
                codes(r.openProblems(ENG, c.month())).equals(List.of("E17")) && codes(r.openProblems(MED, c.month())).equals(List.of("M9", "M10"))
                        && codes(r.openProblems(BUS, c.month())).equals(List.of("B6")) && codes(r.openProblems(LAW, c.month())).equals(List.of("L3")), open.toString());
        check("fixture: the four finds are known here and nothing else of the piles is", r.foundHere().equals(c.found()) && r.pile("M9") == 207.968244, r.foundHere().toString());

        System.out.println("      as is");
        ResearchPreview.Result asIs = run(c, c.people(), noPrize());
        finds("as is", asIs, ENG, 1.1020, 0.0239);
        finds("as is", asIs, MED, 0.8030, 0.0245);
        finds("as is", asIs, BUS, 0.0550, 0.0072);
        finds("as is", asIs, LAW, 0, 0);
        all("as is", asIs, 1.960, 0.035);

        System.out.println("      medicine +63 and business to its room (bus = 16.8 + 113.4)");
        ResearchPreview.Result staged = run(c, byField(412.1452285841101, 226.87444794952683 + 63, 16.8 + 113.4, 18.4), noPrize());
        finds("medicine +63, business to room", staged, MED, 1.0530, 0.0245);
        finds("medicine +63, business to room", staged, BUS, 0.1760, 0.0132);
        all("medicine +63, business to room", staged, 2.331, 0.036);

        System.out.println("      law grant D$ 1.0M (law = 18.4 + 1000 / 16.0)");
        ResearchPreview.Result law = run(c, byField(412.1452285841101, 226.87444794952683, 90.26938775510204, 18.4 + 1_000_000 / w2(LAW)), noPrize());
        finds("law grant", law, LAW, 0.0290, 0.0053);

        System.out.println("      engineering cut to its faculty (eng = 54.4)");
        ResearchPreview.Result cut = run(c, byField(54.4, 226.87444794952683, 90.26938775510204, 18.4), noPrize());
        finds("engineering cut", cut, ENG, 0.0440, 0.0065);
        all("engineering cut", cut, 0.902, 0.026);

        // The medical prize: the study put 130 lab posts on M9; the game's chase puts labs on M9 alone here (the only problem the purse pays for and a team
        // could finish before the world) and the investors want the labs its room holds.
        System.out.println("      medical prize D$ 350M (medicine as is): the game's chase, not the study's 130 lab posts");
        EnumMap<Field, Double> medPrize = byField(0, 350_000_000, 0, 0);
        Research withPrize = c.research(medPrize);
        ResearchPreview.Result prize = ResearchPreview.preview(withPrize, c.month(), c.people(), medPrize, w, 1.0, DRAWS, ResearchPreview.SEED);
        List<String> chase = codes(withPrize.chase(MED, c.month() + 1, w));
        double room = chaseRoom(withPrize, MED, c.month() + 1, w);
        System.out.println("         the chase in the first month: " + chase + ", room " + room + ", labs wanted " + withPrize.labsWanted(MED, c.month() + 1, w));
        check("the chase is M9 alone (keyhole surgery)", chase.equals(List.of("M9")), chase.toString());
        near("...its room is the study's 362.7 researchers", room, 362.7, 0.5);
        check("...so the investors want 4 labs in the first month, in the model's own question and in the preview's",
                withPrize.labsWanted(MED, c.month() + 1, w) == 4 && prize.labsFirstMonth(MED) == 4, "model " + withPrize.labsWanted(MED, c.month() + 1, w) + ", preview " + prize.labsFirstMonth(MED));
        finds("medical prize", prize, MED, 1.5050, 0.0169);
        against("medical prize expected purse (D$ M)", prize.purse(MED) / 1e6, 350 * prize.findsSe(MED), 526.75, 350 * 0.0169, 0.005);
    }

    /* ==================== 3b. the study's model, written again ==================== */

    /**
     * research_model.py forward() as the study wrote it (no prize), on the study's 40 discoveries: the 1951-1995 catalogue, every other of the game's
     * (before 1950 and L5, the 1900 study's) being known, or out of the ten years, in the cities it was run for. The study's own effort (its base work times
     * the growth to the year, not rounded), team cap, window and month; its sampling (a draw redrawn until above the progress); its month (the world's
     * dates, then each field in order its open problems, the water fill, the progress). Returns {mean, standard error} of the finds by field over `draws` draws of java.util.Random.
     */
    static double[][] studyForward(City city, EnumMap<Field, Double> people, int draws, long seed) {
        List<Entry> cat = new ArrayList<>();
        for (Entry e : ResearchCatalog.get().entries()) if (e.year() >= 1951 && !e.code().equals("L5")) cat.add(e);
        int n = cat.size(), now = city.month(), end = now + 120;
        double[] base = { 400, 400, 200, 500 }, tmin = { 3, 3, 2, 8 };
        int[] window = { 7, 9, 4, 4 };
        double[] effort = new double[n], cap = new double[n], prog0 = new double[n];
        int[] world = new int[n], opens = new int[n], field = new int[n];
        boolean[] known0 = new boolean[n];
        int[][] needs = new int[n][];
        for (int i = 0; i < n; i++) {
            Entry e = cat.get(i);
            field[i] = e.field().ordinal();
            double growth = e.field() == MED ? 0.035 : 0.053;
            effort[i] = base[e.size().ordinal()] * Math.pow(1 + growth, e.year() - 1950);
            cap[i] = effort[i] / tmin[e.size().ordinal()];
            world[i] = PolicyDates.monthOf(e.year(), e.monthOfYear());
            opens[i] = PolicyDates.monthOf(e.year() - window[field[i]], e.monthOfYear());
            known0[i] = world[i] <= now || city.found().containsKey(e.code());
            prog0[i] = city.piles().getOrDefault(e.code(), 0.0);
            List<Integer> mine = new ArrayList<>();
            for (String need : e.needs()) {
                for (int j = 0; j < n; j++) if (cat.get(j).code().equals(need)) mine.add(j);
            }
            needs[i] = mine.stream().mapToInt(Integer::intValue).toArray();
        }
        java.util.Random rng = new java.util.Random(seed);
        int fields = Field.values().length;
        double[] total = new double[fields], totalSq = new double[fields];
        for (int d = 0; d < draws; d++) {
            boolean[] known = known0.clone();
            int[] found = new int[fields];
            double[] prog = prog0.clone(), size = new double[n];
            for (int i = 0; i < n; i++) {
                if (known[i]) continue;
                double s;
                do {
                    s = effort[i] * Math.exp(0.6 * rng.nextGaussian() - 0.18);
                } while (!(s > prog[i]));
                size[i] = s;
            }
            for (int m = now + 1; m <= end; m++) {
                for (int i = 0; i < n; i++) if (!known[i] && world[i] <= m) known[i] = true;
                for (Field f : Field.values()) {
                    List<Integer> open = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        if (field[i] != f.ordinal() || known[i] || opens[i] > m) continue;
                        boolean ready = true;
                        for (int j : needs[i]) ready &= known[j];
                        if (ready) open.add(i);
                    }
                    if (open.isEmpty()) continue;
                    double[] caps = new double[open.size()];
                    for (int k = 0; k < caps.length; k++) caps[k] = cap[open.get(k)];
                    Research.Fill fill = Research.waterFill(people.get(f), caps);
                    for (int k = 0; k < caps.length; k++) {
                        int i = open.get(k);
                        prog[i] += fill.share()[k] / 12;
                        if (prog[i] >= size[i]) {
                            known[i] = true;
                            found[f.ordinal()]++;
                        }
                    }
                }
            }
            for (int f = 0; f < fields; f++) {
                total[f] += found[f];
                totalSq[f] += (double) found[f] * found[f];
            }
        }
        double[] mean = new double[fields], se = new double[fields];
        for (int f = 0; f < fields; f++) {
            mean[f] = total[f] / draws;
            se[f] = Math.sqrt(Math.max(0, totalSq[f] / draws - mean[f] * mean[f]) / draws);
        }
        return new double[][] { mean, se };
    }

    static void sectionThreeB() {
        System.out.println("\n--- 3b. the preview and the study's model written again (research_model.py forward(), no prize), many draws of each ---");
        int studyDraws = 20_000;
        double[] w = studyWages();
        City big = big(), young = young();
        EnumMap<Field, Double> staged = byField(412.1452285841101, 226.87444794952683 + 63, 16.8 + 113.4, 18.4);
        EnumMap<Field, Double> medicalGrant = byField(8.8, 8.8 + 700_000 / w2(MED), 2.4, 2.4);
        Object[][] cases = { { "big city as is", big, big.people() }, { "big city, medicine +63 and business to its room", big, staged },
                { "young city, medical grant D$ 0.70M", young, medicalGrant } };
        for (Object[] one : cases) {
            City c = (City) one[1];
            @SuppressWarnings("unchecked") EnumMap<Field, Double> people = (EnumMap<Field, Double>) one[2];
            ResearchPreview.Result game = run(c, people, noPrize());
            double[][] study = studyForward(c, people, studyDraws, 7);
            for (Field f : Field.values()) {
                if (study[0][f.ordinal()] == 0 && game.finds(f) == 0) continue;
                against(one[0] + ", " + f.name().toLowerCase() + " finds", game.finds(f), game.findsSe(f), study[0][f.ordinal()], study[1][f.ordinal()], 0, 4);
            }
        }
    }

    /* ==================== 4. the step ==================== */

    /** The number of finds and of months compared, for the printout. */
    static int stepFinds, stepMonths;

    /**
     * Plays a research the way the game does for the preview's ten years, with the sizes of one city's seed: Research.month() each month with the researchers
     * held, the labs wanted as Research.labsWanted() would ask them at the month's start, and the preview's path over the same sizes; and says where they part.
     */
    static String partsAt(City city, EnumMap<Field, Double> people, EnumMap<Field, Double> prize, long seed) {
        double[] w = studyWages();
        Research base = city.research(prize);
        ResearchPreview plan = ResearchPreview.plan(base, city.month(), people, prize, w, null);
        double[] size = new double[plan.entries().size()];
        for (int i = 0; i < size.length; i++) size[i] = Research.hiddenSize(seed, plan.entries().get(i));
        int[] path = plan.path(size);

        Research copy = copyOf(base);
        int end = city.month() + ResearchPreview.YEARS * Research.MONTHS_A_YEAR;
        for (int m = city.month() + 1; m <= end; m++) {
            EnumMap<Field, Integer> labs = new EnumMap<>(Field.class);
            EnumMap<Field, Double> labPeople = new EnumMap<>(Field.class);
            for (Field f : Field.values()) {
                int n = copy.prize(f) > 0 ? copy.labsWanted(f, m, w) : 0;
                labs.put(f, n);
                labPeople.put(f, n * (double) Research.LAB_POSTS);
            }
            copy.month(m, seed, people, labs, labPeople, w);
            stepMonths++;
        }
        StringBuilder parts = new StringBuilder();
        int found = 0;
        for (int i = 0; i < path.length; i++) {
            String code = plan.entries().get(i).code();
            int expected = copy.foundMonth(code);
            if (path[i] != expected) parts.append(code).append(" path ").append(path[i] == ResearchPreview.NEVER ? "never" : path[i]).append(" month ")
                    .append(expected == Integer.MAX_VALUE ? "never" : expected).append("; ");
            if (path[i] != ResearchPreview.NEVER) found++;
        }
        int foundByMonth = copy.foundHere().size() - city.found().size();
        if (found != foundByMonth) parts.append("the month found ").append(foundByMonth).append(" and the plan holds ").append(found).append("; ");
        stepFinds += found;
        return parts.toString();
    }

    static void stepCase(String label, City city, EnumMap<Field, Double> people, EnumMap<Field, Double> prize, int seeds) {
        int before = stepFinds, parted = 0;
        String first = "";
        for (int s = 1; s <= seeds; s++) {
            String parts = partsAt(city, people, prize, 1000 + 37L * s);
            if (!parts.isEmpty()) {
                parted++;
                if (first.isEmpty()) first = "seed " + (1000 + 37L * s) + ": " + parts;
            }
        }
        System.out.println("         " + label + ": " + (stepFinds - before) + " finds in " + seeds + " cities");
        check(label + ": the preview's path finds what Research.month() finds, the same codes in the same months, in all " + seeds + " cities (" + (stepFinds - before) + " finds)",
                parted == 0 && stepFinds - before >= 5, parted + " cities part; " + first + (stepFinds - before < 5 ? " (too few finds to be a test)" : ""));
    }

    static void sectionFour() {
        System.out.println("\n--- 4. the step: the preview's path and Research.month(), over the same sizes ---");
        City young = young(), big = big();
        stepCase("young city, researchers in every field, no prize", young, byField(150, 90, 40, 20), noPrize(), 30);
        stepCase("young city, the same with an engineering prize and a medical one", young, byField(150, 90, 40, 20), byField(140_000_000, 60_000_000, 0, 0), 30);
        stepCase("young city as is with the engineering prize (the labs do the finding)", young, young.people(), byField(140_000_000, 0, 0, 0), 30);
        stepCase("big city, researchers four times as is, no prize", big, byField(1648, 908, 361, 74), noPrize(), 30);
        stepCase("big city, as is, with a medical prize and an engineering one", big, big.people(), byField(300_000_000, 350_000_000, 100_000_000, 0), 30);
        stepCase("big city, a prize in every field and half the researchers", big, byField(206, 113, 45, 9), byField(400_000_000, 400_000_000, 200_000_000, 300_000_000), 30);
        System.out.println("         " + stepMonths + " months of Research.month() held, " + stepFinds + " finds");
    }

    /* ==================== 5. the draw ==================== */

    static void sectionFive() {
        System.out.println("\n--- 5. a size is drawn above the pile on it, from the same distribution as plain redrawing ---");
        SplittableRandom rng = new SplittableRandom(99);
        double work = 500;
        boolean above = true;
        double lowest = Double.MAX_VALUE;
        for (double pile : new double[] { 0, 1, 250, 500, 800, 1500, 3000, 6000 }) {
            for (int i = 0; i < 2000; i++) {
                double size = ResearchPreview.drawSize(rng, work, pile);
                above &= size > pile;
                lowest = Math.min(lowest, size - pile);
            }
        }
        check("every size drawn is above its pile, from a pile of nothing to twelve times the work", above, "lowest margin " + lowest);

        // The mean of the unconditional size is the work (the lognormal has mean 1).
        double sum = 0, sumSq = 0;
        int n = 200_000;
        for (int i = 0; i < n; i++) {
            double s = ResearchPreview.drawSize(rng, work, 0);
            sum += s;
            sumSq += s * s;
        }
        double mean = sum / n, se = Math.sqrt((sumSq / n - mean * mean) / n);
        near("with no pile the mean size is the typical work (the draw has mean 1)", mean, work, 5 * se);

        // The conditional mean: plain redrawing (the study's, written here) against the sampler, below the tail's threshold and above it.
        for (double z0 : new double[] { 0.5, 1.0, 1.2, 2.0, 3.0 }) {
            double sigma = Research.SIZE_SIGMA, pile = work * Math.exp(sigma * z0 - sigma * sigma / 2);
            double a = 0, aSq = 0, b = 0, bSq = 0;
            int m = z0 > 2.5 ? 40_000 : 200_000;
            for (int i = 0; i < m; i++) {
                double s = ResearchPreview.drawSize(rng, work, pile);
                a += s;
                aSq += s * s;
                double t;
                do {
                    t = work * Math.exp(sigma * rng.nextGaussian() - sigma * sigma / 2);
                } while (!(t > pile));
                b += t;
                bSq += t * t;
            }
            double ma = a / m, mb = b / m, sa = Math.sqrt((aSq / m - ma * ma) / m), sb = Math.sqrt((bSq / m - mb * mb) / m);
            check(String.format("a pile at %.1f standard deviations (%.0f of a work of 500): the sampler's mean size %.2f is plain redrawing's %.2f within 4 errors", z0, pile, ma, mb),
                    Math.abs(ma - mb) <= 4 * Math.hypot(sa, sb), ma + " against " + mb);
        }
    }

    /* ==================== 6. reproducible and cached ==================== */

    static void sectionSix() {
        System.out.println("\n--- 6. the same inputs give the same answer, another seed another, and ResearchView does not run it twice ---");
        City c = young();
        double[] w = studyWages();
        EnumMap<Field, Double> people = byField(8.8 + 550_000 / w2(ENG), 8.8 + 700_000 / w2(MED), 2.4, 2.4);
        Research r = c.research(noPrize());
        ResearchPreview.Result a = ResearchPreview.preview(r, c.month(), people, noPrize(), w, 1.0, 600, 5), b = ResearchPreview.preview(r, c.month(), people, noPrize(), w, 1.0, 600, 5);
        boolean same = true;
        for (Field f : Field.values()) same &= a.finds(f) == b.finds(f) && a.findsSe(f) == b.findsSe(f) && a.chance(f) == b.chance(f) && a.purse(f) == b.purse(f);
        check("the same inputs and seed give exactly the same finds, errors, chances and purses", same && a.total() == b.total() && a.totalSe() == b.totalSe());
        ResearchPreview.Result other = ResearchPreview.preview(r, c.month(), people, noPrize(), w, 1.0, 600, 6);
        check("another seed gives other draws (the medical finds differ)", other.finds(MED) != a.finds(MED), a.finds(MED) + " and " + other.finds(MED));

        // The cache.
        ResearchView.Dials dials = new ResearchView.Dials(new double[] { 8.8, 8.8, 2.4, 2.4 }, new double[4], new double[] { 1, 1, 1, 1 }, w);
        int runs = ResearchPreview.runs();
        ResearchPreview.Result first = ResearchView.preview(r, c.month(), dials, 200, 11);
        check("the first read runs the preview once", ResearchPreview.runs() == runs + 1, "runs " + (ResearchPreview.runs() - runs));
        ResearchPreview.Result again = ResearchView.preview(r, c.month(), dials, 200, 11);
        check("the same month and inputs again return the very same Result without running", again == first && ResearchPreview.runs() == runs + 1, "runs " + (ResearchPreview.runs() - runs));
        ResearchView.preview(copyOf(r), c.month(), dials, 200, 11);
        check("...a copy of the research in the same state is the same inputs", ResearchPreview.runs() == runs + 1);
        ResearchView.preview(r, c.month() + 1, dials, 200, 11);
        check("a new month runs again", ResearchPreview.runs() == runs + 2);
        ResearchView.preview(r, c.month(), new ResearchView.Dials(new double[] { 8.8, 9.8, 2.4, 2.4 }, new double[4], new double[] { 1, 1, 1, 1 }, w), 200, 11);
        check("a researcher more runs again", ResearchPreview.runs() == runs + 3);
        ResearchView.preview(r, c.month(), new ResearchView.Dials(new double[] { 8.8, 8.8, 2.4, 2.4 }, new double[] { 0, 1e8, 0, 0 }, new double[] { 1, 1, 1, 1 }, w), 200, 11);
        check("a prize runs again", ResearchPreview.runs() == runs + 4);
        double[] dearer = w.clone();
        dearer[JobType.UNIV_DOCTOR.ordinal()] *= 1.1;
        ResearchView.preview(r, c.month(), new ResearchView.Dials(new double[] { 8.8, 8.8, 2.4, 2.4 }, new double[4], new double[] { 1, 1, 1, 1 }, dearer), 200, 11);
        check("a change of wage runs again", ResearchPreview.runs() == runs + 5);
        Map<String, Double> piles = r.piles();
        piles.put("E5", 50.0);
        Research moved = new Research();
        moved.restore(null, null, piles, r.foundHere());
        ResearchView.preview(moved, c.month(), dials, 200, 11);
        check("a pile that moved runs again", ResearchPreview.runs() == runs + 6);
        ResearchView.preview(r, c.month(), dials, 200, 12);
        check("another seed runs again", ResearchPreview.runs() == runs + 7);
        check("...and the first inputs are still kept: no run", ResearchView.preview(r, c.month(), dials, 200, 11) == first && ResearchPreview.runs() == runs + 7);
        // More than the cache holds, then the oldest is run again.
        for (int i = 0; i < 40; i++) ResearchView.preview(r, c.month(), dials, 200, 1000 + i);
        int before = ResearchPreview.runs();
        ResearchView.preview(r, c.month(), dials, 200, 11);
        check("a small cache: after forty others the first is run again", ResearchPreview.runs() == before + 1);
        check("...and the last of them is still kept", ResearchView.preview(r, c.month(), dials, 200, 1039) != null && ResearchPreview.runs() == before + 1);
    }

    /* ==================== 7. the answer's shape ==================== */

    static void sectionSeven() {
        System.out.println("\n--- 7. the answer: purse = prize x finds, a chance is a share, the fields add up, nothing to find gives nothing ---");
        City c = big();
        double[] w = studyWages();
        EnumMap<Field, Double> prize = byField(300_000_000, 350_000_000, 100_000_000, 0);
        ResearchPreview.Result r = ResearchPreview.preview(c.research(prize), c.month(), c.people(), prize, w, 1.0, 1000, 3);
        boolean shape = true, purse = true, sum = true;
        double total = 0;
        for (Field f : Field.values()) {
            shape &= r.chance(f) >= 0 && r.chance(f) <= 1 && r.finds(f) >= 0 && r.findsSe(f) >= 0 && r.chance(f) <= r.finds(f) + 1e-12;
            purse &= Math.abs(r.purse(f) - prize.get(f) * r.finds(f)) <= 1e-6 * Math.max(1, r.purse(f));
            total += r.finds(f);
        }
        sum = Math.abs(total - r.total()) <= 1e-9;
        check("a chance is between 0 and 1 and never above the expected finds (a find is a whole one)", shape);
        check("the purse of a field is its prize times its finds, and of the city the sum", purse && Math.abs(r.purseTotal() - (r.purse(ENG) + r.purse(MED) + r.purse(BUS) + r.purse(LAW))) < 1e-6);
        check("the fields' finds add to the city's", sum, total + " against " + r.total());
        check("the draws are the draws asked for", r.draws() == 1000);

        Research done = new Research();
        ResearchPreview.Result none = ResearchPreview.preview(done, 5000, byField(500, 500, 500, 500), prize, w, 1.0, 50, 1);
        check("a city that has every discovery to its name (month 5000) has nothing to find and nothing to ask of the investors",
                none.total() == 0 && none.totalSe() == 0 && none.purseTotal() == 0 && none.labsFirstMonth(ENG) == 0 && none.labsFirstMonth(MED) == 0);
        ResearchPreview.Result nobody = ResearchPreview.preview(c.research(noPrize()), c.month(), byField(0, 0, 0, 0), noPrize(), w, 1.0, 100, 1);
        check("no researchers and no prize find nothing in ten years", nobody.total() == 0);
        ResearchPreview.Result bad = ResearchPreview.preview(c.research(noPrize()), c.month(), byField(Double.NaN, -5, Double.POSITIVE_INFINITY, 18.4), noPrize(), w, 1.0, 100, 1);
        check("researchers that are not a number, below nothing or not finite count as none (as Research.month() reads them)", bad.finds(ENG) == 0 && bad.finds(MED) == 0 && bad.finds(BUS) == 0);
        ResearchPreview.Result noWages = ResearchPreview.preview(c.research(prize), c.month(), c.people(), prize, null, 1.0, 100, 1);
        check("no wages, no cost of a post, no chase: the prize brings no lab (as Research.month() with none)", noWages.labsFirstMonth(MED) == 0 && noWages.labsFirstMonth(ENG) == 0);
        ResearchPreview.Result empty = ResearchPreview.preview(c.research(prize), c.month(), c.people(), prize, w, 0.0, 100, 1);
        check("labs nobody works in still are wanted, and do no work", empty.labsFirstMonth(MED) == 4);
        // The first month's labs in the step's terms: the model's own question.
        Research asked = c.research(prize);
        ResearchPreview.Result seen = ResearchPreview.preview(asked, c.month(), c.people(), prize, w, 1.0, 50, 1);
        boolean labsAsked = true;
        for (Field f : Field.values()) labsAsked &= seen.labsFirstMonth(f) == asked.labsWanted(f, c.month() + 1, w);
        check("the labs wanted in the first month are what Research.labsWanted() answers for the month, field by field", labsAsked);
    }

    /* ==================== 8. the words ==================== */

    static void sectionEight() {
        System.out.println("\n--- 8. the words: a chance below one expected find, a count above ---");
        check("a chance when both expected counts are under 1", ResearchView.asChance(0.0, 0.258) && ResearchView.asChance(0.5, 0.2) && ResearchView.asChance(0.9999, 0.9999));
        check("the larger of the two decides: one at 1 makes it a count, whichever side it is on",
                !ResearchView.asChance(1.0, 0.1) && !ResearchView.asChance(0.1, 1.0) && !ResearchView.asChance(1.1020, 1.0530) && !ResearchView.asChance(2, 3));
        check("...and just under 1 on both sides is still a chance", ResearchView.asChance(0.9999999, 0.2) && ResearchView.asChance(0.2, 0.9999999));
        check("a chance of exactly nothing is \"none\", NaN too", ResearchView.previewWords(0, 0, true).equals("none") && ResearchView.previewWords(Double.NaN, Double.NaN, true).equals("none"));
        check("a chance above nothing that rounds to 0% is \"under 1%\", up to 0.4999%",
                ResearchView.previewWords(0.00001, 0.00001, true).equals("under 1%") && ResearchView.previewWords(0.002, 0.002, true).equals("under 1%")
                        && ResearchView.previewWords(0.00499, 0.00499, true).equals("under 1%"));
        check("...and one that rounds to 1% is \"1%\", from 0.5%", ResearchView.previewWords(0.005, 0.005, true).equals("1%") && ResearchView.previewWords(0.0149, 0.0149, true).equals("1%"));
        check("a chance is a whole percent: 25.4% is \"25%\", 25.5% \"26%\"", ResearchView.previewWords(0.258, 0.254, true).equals("25%") && ResearchView.previewWords(0.258, 0.255, true).equals("26%"));
        check("a certain chance is \"100%\"", ResearchView.previewWords(2.7, 1.0, true).equals("100%"));
        check("a count of exactly nothing is \"none\", NaN too", ResearchView.previewWords(0, 0, false).equals("none") && ResearchView.previewWords(Double.NaN, 0, false).equals("none"));
        check("a count above nothing that shows as 0.00 is \"under 0.01\", up to 0.00499", ResearchView.previewWords(0.0001, 0, false).equals("under 0.01")
                && ResearchView.previewWords(0.00499, 0, false).equals("under 0.01"));
        check("...and one that rounds to 0.01 is \"0.01\", from 0.005", ResearchView.previewWords(0.005, 0, false).equals("0.01") && ResearchView.previewWords(0.0149, 0, false).equals("0.01"));
        check("a count is to two places: 1.102 is \"1.10\", 1.053 \"1.05\", 2.739 \"2.74\", 1 \"1.00\"",
                ResearchView.previewWords(1.102, 0.9, false).equals("1.10") && ResearchView.previewWords(1.053, 0.9, false).equals("1.05")
                        && ResearchView.previewWords(2.739, 1, false).equals("2.74") && ResearchView.previewWords(1, 1, false).equals("1.00"));
        check("the count is used whatever the chance when the card says so (a count of 0.6 is \"0.60\", not a percent)", ResearchView.previewWords(0.6, 0.45, false).equals("0.60"));
    }

    /* ==================== 9. the cost ==================== */

    static void sectionNine() {
        System.out.println("\n--- 9. what 400 draws take ---");
        City c = big();
        double[] w = studyWages();
        EnumMap<Field, Double> prize = byField(0, 350_000_000, 0, 0);
        Research asIs = c.research(noPrize()), withPrize = c.research(prize);
        long[] asIsTimes = new long[7], prizeTimes = new long[7];
        for (int i = 0; i < 7; i++) {
            long t0 = System.nanoTime();
            ResearchPreview.preview(asIs, c.month(), c.people(), noPrize(), w, 1.0, ResearchPreview.DRAWS, ResearchPreview.SEED);
            asIsTimes[i] = System.nanoTime() - t0;
            t0 = System.nanoTime();
            ResearchPreview.preview(withPrize, c.month(), c.people(), prize, w, 1.0, ResearchPreview.DRAWS, ResearchPreview.SEED);
            prizeTimes[i] = System.nanoTime() - t0;
        }
        java.util.Arrays.sort(asIsTimes);
        java.util.Arrays.sort(prizeTimes);
        System.out.printf("         %d draws, the big city: as is %.1f ms, with a medical prize %.1f ms (the median of 7 runs, the first of them cold)%n",
                ResearchPreview.DRAWS, asIsTimes[3] / 1e6, prizeTimes[3] / 1e6);
        check("the preview of 400 draws is made, as is and with a prize, in well under a second (timing is printed, not judged)", asIsTimes[3] < 1_000_000_000L && prizeTimes[3] < 1_000_000_000L);
    }
}
