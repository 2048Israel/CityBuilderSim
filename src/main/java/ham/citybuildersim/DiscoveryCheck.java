package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;
import ham.citybuildersim.ResearchCatalog.Size;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The city finds discoveries of its own, and a discovery found here is known here from the month it was found (R3, slice A: Research, and the
 * seam in Recipe, Equipment, BuildingsTemplate, Recipes and EquipmentFit; docs/studies/research-design.md 1.8, 5.1, 5.2, 6.3).
 *
 * WHY. Research is a mechanism and not a chance (5.2: "Nothing multiplies a chance"), so every claim about it is a statement about a month: a post
 * costs this, a problem is open in that month and not the one before, a pile reached its size in the first month it could. A wrong dial or a step
 * out of order moves a find by a year and no assertion elsewhere would see it, and the seam is the part a played city can get wrong silently
 * (a recipe known in a month it should not be). So the figures are held to the study's, the rules to the study's model, the step to its
 * invariants over whole runs, and the seam to "equal to history's in every city that found nothing".
 *
 * What this has to prove:
 *
 *   1. THE POST'S COST. Each field's post at today's wages is the study's D$ 18.2k, 23.8k, 12.2k and 16.0k, and at the game's wages it moves with
 *      them; a wage that is not there or not a number gives no cost.
 *   2. FACULTY AND ROOM. The five schools' university-band staff, read from buildings.json, split over the fields as the study says (University
 *      22, 22, 6, 6; Institute 46; Medical 50; Business 30; Law 34); faculty and room are the dials' share of it; a school that is not one of
 *      those hosts none.
 *   3. POSTS. Never more than the room or the money buys; a post is the jobs the study says; a post nobody fills costs nothing and has no researcher;
 *      the dials refuse what is not an amount.
 *   4. THE WATER FILL. An even spread under the caps, a small cap filled and the rest spilt evenly, every cap full leaves the remainder idle, the
 *      shares add to the people less the idle, and no problem has less than another unless it is full.
 *   5. OPEN PROBLEMS. Not before its science exists, open inside the window, not once known (here or by history's month), not while a need is
 *      unknown; the lags are the study's and the law's is the Policy gate's; the minimum years and the team caps are the study's.
 *   6. THE HIDDEN SIZE. The same for the same seed and code, equal to the study's model's draw, mean 1 and the dial's spread over 20,000 seeds,
 *      different for different codes.
 *   7. NO RESEARCHERS, HISTORY'S DATES. Nothing is found and every discovery arrives abroad in history's month, once.
 *   8. A FIND. With a great many researchers in a field, discoveries are found here before history's date, never before their science exists or
 *      their needs are known, never faster than the team cap allows, in the first month the pile reached the size; and only in that field.
 *   9. KNOWN IN THIS CITY. A recipe, a piece and a building whose code was found here is known from that month and history's reading is not; with no
 *      research, or an empty one, every reading is history's; the planners' pure cores (canRun, best, judge) and the game layer agree.
 *  10. REPRODUCIBLE. The same months, researchers and seed find the same codes in the same months; another seed does not; a run saved, restored
 *      and continued finds what the uninterrupted one does; a malformed save entry is skipped.
 *  11. A NEW GAME CLEARS IT. Grants, posts, piles and finds are gone after newGame().
 *
 * Slice B, the research in the city's month (Game.nextMonth() strikes the posts, getJobs() counts them, advanceDemographics() pays and works them,
 * finalUpdateEconomy() settles the treasury's line; DataSave, Inbox, TaxPolicy.bringForward()); each section plays a village with a University:
 *
 *  12. POSTS IN THE LABOUR MARKET. A grant buys the posts Research.posts() says at the strike's wages, never above the room; the city's jobs are
 *      the buildings' plus Research.jobs() exactly; with every grant at 0 they are the buildings' alone.
 *  13. THE BILL. The month's research payroll and costs are the sums of Research.payroll() and bill() at the month's wages and fill; the treasury's
 *      line is a promise and the last of its constants; getExpenses() and the national accounts carry it; the month's money audit closes.
 *  14. A CITY FOUNDED THE DEFAULT WAY. No grant, no change (no job, no bill, no notice); a grant buys posts that are paid, and every researcher is idle.
 *  15. THE INBOX. A find raises one "found:" notice in its month and an arrival one "abroad:" notice, neither twice, none in the first month.
 *  16. A LAW FIND OPENS ITS LEVER EARLY. The lever of a found PL entry opens the month after the find, never before its earliest, never later than
 *      it was; a code that is no lever's key moves nothing; the openings are saved.
 *  17. THE SAVE. A city with research writes it and a load restores grants, posts, piles, finds, jobs and the bill, and plays on as the city saved;
 *      a city with none writes no key.
 *  18. A REFORM OF THE MONEY scales the grants and the bill and nothing else of the research.
 */
public class DiscoveryCheck {

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

    static PrintStream quiet;

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /* ----- the specification, typed from the study ----- */

    /** research-design 1.8: a post's cost at today's wages, D$ thousands a month, as the study prints it to a tenth (business's 12.25 is printed 12.2). */
    static final Map<Field, Double> STUDY_POST = new EnumMap<>(Map.of(Field.ENGINEERING, 18.2, Field.MEDICINE, 23.8, Field.BUSINESS, 12.2, Field.LAW, 16.0));

    /** How far from the study's figure a post may be: the study's rounding to a tenth. */
    static final double STUDY_ROUNDING = 0.06;

    /** research-design 1.8: how many years before history's date the science exists. */
    static final Map<Field, Integer> STUDY_LAG_YEARS = new EnumMap<>(Map.of(Field.ENGINEERING, 7, Field.MEDICINE, 9, Field.BUSINESS, 4, Field.LAW, 4));

    static final ResearchCatalog CAT = ResearchCatalog.get();

    static Entry of(String code) { return CAT.entry(code); }

    static int lastWorldMonth() {
        int last = 0;
        for (Entry e : CAT.entries()) last = Math.max(last, e.worldMonth());
        return last;
    }

    static BuildingsTemplate byName(List<BuildingsTemplate> templates, String name) {
        for (BuildingsTemplate t : templates) if (t.getName().equals(name)) return t;
        return null;
    }

    static BuildingsTemplate byId(List<BuildingsTemplate> templates, int id) {
        for (BuildingsTemplate t : templates) if (t.getId() == id) return t;
        return null;
    }

    /** The wages by JobType ordinal: today's (before the founding's price level) or the game's (PayTier.getMonthlyWage()). */
    static double[] wages(boolean today) {
        double[] w = new double[JobType.values().length];
        for (JobType type : JobType.values()) w[type.ordinal()] = today ? PayTier.of(type).getTodaysWage() : PayTier.of(type).getMonthlyWage();
        return w;
    }

    /** A school's university-band staff by JobType ordinal, as buildings.json gives its jobs. */
    static double[] staffOf(BuildingsTemplate t) {
        double[] staff = new double[JobType.values().length];
        for (JobType type : JobType.values()) staff[type.ordinal()] = t.getJobs(type);
        return staff;
    }

    /** All of a school's university-band staff (the job types Research.servedBy() gives a field). */
    static double universityStaff(double[] staff) {
        double sum = 0;
        for (JobType type : JobType.values()) if (!Research.servedBy(type).isEmpty()) sum += staff[type.ordinal()];
        return sum;
    }

    public static void main(String[] args) throws Exception {
        quiet = new PrintStream(OutputStream.nullOutputStream());
        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();
        sectionSix();
        sectionSeven();
        sectionEightAndNine();
        sectionTen();
        sectionEleven();
        sectionTwelve();
        sectionThirteen();
        sectionFourteen();
        sectionFifteen();
        sectionSixteen();
        sectionSeventeen();
        sectionEighteen();
        System.out.println(fails == 0 ? "DiscoveryCheck: all " + passes + " ok" : "DiscoveryCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /* ==================== 1. the post's cost ==================== */

    static void sectionOne() {
        System.out.println("--- 1. a post costs the study's figure at today's wages, and moves with the wages ---");
        double[] today = wages(true), game = wages(false);
        for (Field f : Field.values()) {
            near("a " + f.name().toLowerCase() + " post at today's wages is the study's D$ " + STUDY_POST.get(f) + "k a month", Research.postCost(f, today), STUDY_POST.get(f), STUDY_ROUNDING);
        }
        double engineering = (PayTier.SENIOR_PROFESSIONAL.getTodaysWage() / 2 + PayTier.PROFESSIONAL.getTodaysWage() / 2 + 0.43 * PayTier.COLLEGE.getTodaysWage()) / 0.62;
        near("...the engineering post is the study's sum written out: half a senior professional, half a professional and 0.43 of a college wage, over 0.62", Research.postCost(Field.ENGINEERING, today), engineering, 1e-9);
        for (Field f : Field.values()) {
            double ratio = Research.postCost(f, game) / Research.postCost(f, today);
            near("the " + f.name().toLowerCase() + " post at the game's wages is today's times the founding's price level (PriceLevel.FACTOR)", ratio, PriceLevel.FACTOR, 1e-9 * PriceLevel.FACTOR);
        }

        double[] raised = today.clone();
        raised[JobType.UNIV_SCIENCE.ordinal()] *= 2;
        check("doubling the science band's wage raises the engineering and medical posts and moves neither the business nor the law post",
                Research.postCost(Field.ENGINEERING, raised) > Research.postCost(Field.ENGINEERING, today)
                        && Research.postCost(Field.MEDICINE, raised) > Research.postCost(Field.MEDICINE, today)
                        && Research.postCost(Field.BUSINESS, raised) == Research.postCost(Field.BUSINESS, today)
                        && Research.postCost(Field.LAW, raised) == Research.postCost(Field.LAW, today));
        double[] policy = today.clone();
        policy[JobType.UNIV_POLICY.ordinal()] *= 2;
        check("...and the policy band's wage, the other half of a business and a law post, moves those two and neither lab post",
                Research.postCost(Field.BUSINESS, policy) > Research.postCost(Field.BUSINESS, today) && Research.postCost(Field.LAW, policy) > Research.postCost(Field.LAW, today)
                        && Research.postCost(Field.ENGINEERING, policy) == Research.postCost(Field.ENGINEERING, today));

        double[] nan = today.clone();
        nan[JobType.UNIV_HIGHTECH_ENG.ordinal()] = Double.NaN;
        double[] free = new double[today.length];
        double[] negative = today.clone();
        for (int i = 0; i < negative.length; i++) negative[i] = -negative[i];
        check("no cost for a wage array that is missing, too short, all nothing, below nothing, or has the researcher's wage not a number",
                Double.isNaN(Research.postCost(Field.ENGINEERING, null)) && Double.isNaN(Research.postCost(Field.ENGINEERING, new double[3]))
                        && Double.isNaN(Research.postCost(Field.ENGINEERING, free)) && Double.isNaN(Research.postCost(Field.ENGINEERING, negative))
                        && Double.isNaN(Research.postCost(Field.ENGINEERING, nan)));
        check("...and a wage that is not a number in a band the field does not use leaves it a cost", !Double.isNaN(Research.postCost(Field.LAW, nan)));
    }

    /* ==================== 2. faculty and room ==================== */

    static void sectionTwo() {
        System.out.println("\n--- 2. the schools' staff split over the fields; faculty and room follow the dials ---");
        List<BuildingsTemplate> shipped = new BuildingCatalog().load();
        BuildingsTemplate university = byName(shipped, "University"), institute = byName(shipped, "Institute of Technology"),
                medical = byName(shipped, "Medical School"), business = byName(shipped, "Business School"), law = byName(shipped, "Law School");
        List<BuildingsTemplate> others = new ArrayList<>();
        for (String name : List.of("Elementary School", "Middle School", "High School", "Community College")) others.add(byName(shipped, name));
        check("fixture: the five schools and four that are not (elementary, middle, high school, community college) are in buildings.json, each teaching what its name says",
                university != null && institute != null && medical != null && business != null && law != null && !others.contains(null)
                        && university.getTeaches() == EducationType.UNIVERSITY && institute.getTeaches() == EducationType.ENGINEERING
                        && medical.getTeaches() == EducationType.MEDICAL && business.getTeaches() == EducationType.BUSINESS && law.getTeaches() == EducationType.LAW);
        if (university == null || institute == null || medical == null || business == null || law == null || others.contains(null)) return;

        near("the dials are the study's: faculty research 40% of the staff's time", Research.FACULTY_SHARE, 0.40, 1e-12);
        near("...and 2.7 posts of room for each university-band staff member", Research.POSTS_PER_STAFF, 2.7, 1e-12);

        EnumMap<Field, Double> u = Research.staffByField(university.getTeaches(), staffOf(university));
        check("the University's 44 science and 12 policy staff split 22 to engineering, 22 to medicine, 6 to business and 6 to law",
                u.size() == 4 && u.get(Field.ENGINEERING) == 22.0 && u.get(Field.MEDICINE) == 22.0 && u.get(Field.BUSINESS) == 6.0 && u.get(Field.LAW) == 6.0, u.toString());
        record School(BuildingsTemplate t, Field field, double staff, int room) { }
        List<School> schools = List.of(new School(institute, Field.ENGINEERING, 46, 124), new School(medical, Field.MEDICINE, 50, 135),
                new School(business, Field.BUSINESS, 30, 81), new School(law, Field.LAW, 34, 92));
        for (School s : schools) {
            EnumMap<Field, Double> split = Research.staffByField(s.t().getTeaches(), staffOf(s.t()));
            check("the " + s.t().getName() + " gives all of its " + (int) s.staff() + " university-band staff to " + s.field().name().toLowerCase() + " and to no other field",
                    split.size() == 1 && split.get(s.field()) == s.staff(), split.toString());
            near("...whose faculty is the faculty share of that staff", Research.faculty(split.get(s.field())), Research.FACULTY_SHARE * s.staff(), 1e-9);
            near("...and whose room is the study's " + s.room() + " posts (rounded to the nearest, as the study has them)", Math.round(Research.room(split.get(s.field()))), s.room(), 0);
        }
        near("the University's room is the study's 59 posts for a science field", Math.round(Research.room(u.get(Field.ENGINEERING))), 59, 0);
        near("...and 16 each for business and law", Math.round(Research.room(u.get(Field.BUSINESS))) + Math.round(Research.room(u.get(Field.LAW))), 32, 0);
        double faculty = 0, total = 0;
        for (double staff : u.values()) {
            faculty += Research.faculty(staff);
            total += staff;
        }
        near("the University alone gives about 22 researcher-FTE across its fields, for nothing", faculty, 22, 0.5);
        near("...and the split loses none of its staff", total, universityStaff(staffOf(university)), 1e-9);
        for (School s : schools) {
            near("...nor the " + s.t().getName() + "'s", Research.staffByField(s.t().getTeaches(), staffOf(s.t())).get(s.field()), universityStaff(staffOf(s.t())), 1e-9);
        }

        boolean none = true;
        for (BuildingsTemplate t : others) none &= Research.staffByField(t.getTeaches(), staffOf(t)).isEmpty();
        check("a high school, a college, an elementary and a middle school host no research, whatever staff they have", none);
        check("...nor does a building that is no school (EducationType.NONE), nor a missing one",
                Research.staffByField(EducationType.NONE, staffOf(university)).isEmpty() && Research.staffByField(null, staffOf(university)).isEmpty());
        check("a missing staff array is no staff, and a University with none gives its four fields nothing",
                Research.staffByField(EducationType.UNIVERSITY, null).values().stream().allMatch(v -> v == 0.0) && Research.staffByField(EducationType.UNIVERSITY, null).size() == 4);
        List<String> served = new ArrayList<>();
        for (JobType type : JobType.values()) {
            boolean univ = type.name().startsWith("UNIV_");
            if (univ == Research.servedBy(type).isEmpty()) served.add(type.name());
        }
        check("every university-band job type serves a field, and no other job type does", served.isEmpty(), served.toString());
    }

    /* ==================== 3. posts ==================== */

    static void sectionThree() {
        System.out.println("\n--- 3. posts never pass the room or the money; a post is the jobs the study says; a post nobody fills costs nothing ---");
        double[] today = wages(true);
        double cost = Research.postCost(Field.ENGINEERING, today);
        double room = 59.4;
        check("a grant far above the room buys the whole posts of the room", Research.posts(1e9, cost, room) == (int) Math.floor(room));
        check("a grant of seven posts and a third buys seven, which cost no more than the grant, and eight would cost more",
                Research.posts(7.3 * cost, cost, room) == 7 && 7 * cost <= 7.3 * cost && 8 * cost > 7.3 * cost);
        List<String> broken = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            double grant = i * 0.37 * cost, r = 3 + (i % 11) * 5.5;
            int n = Research.posts(grant, cost, r);
            boolean money = n * cost <= grant + 1e-9 && (n + 1) * cost > grant - 1e-9 || n == (int) Math.floor(r);
            if (n > r || n < 0 || !money) broken.add(i + ": grant " + grant + " room " + r + " posts " + n);
        }
        check("over 400 grants and rooms: posts never pass the room, and are the money's whole posts unless the room is the limit", broken.isEmpty(), broken.toString());
        check("a zero grant, a grant below nothing or not a number buys none",
                Research.posts(0, cost, room) == 0 && Research.posts(-5, cost, room) == 0 && Research.posts(Double.NaN, cost, room) == 0);
        check("no room (none, below nothing, not a number) buys none, however big the grant",
                Research.posts(1e9, cost, 0) == 0 && Research.posts(1e9, cost, -3) == 0 && Research.posts(1e9, cost, Double.NaN) == 0);
        check("a cost that is not a number, nothing, below nothing or infinite buys none",
                Research.posts(1e9, Double.NaN, room) == 0 && Research.posts(1e9, 0, room) == 0 && Research.posts(1e9, -1, room) == 0
                        && Research.posts(1e9, Double.POSITIVE_INFINITY, room) == 0);

        for (Field f : Field.values()) {
            long[] jobs = Research.jobs(f, 7);
            int a = Research.researcherA(f).ordinal(), b = Research.researcherB(f).ordinal(), s = Research.supportType(f).ordinal();
            long others = 0;
            for (int i = 0; i < jobs.length; i++) if (i != a && i != b && i != s) others += jobs[i];
            check("seven " + f.name().toLowerCase() + " posts are four of the first researcher type, three of the second, and the support staff of them, rounded; no other job",
                    jobs[a] == 4 && jobs[b] == 3 && jobs[s] == Math.round(7 * Research.supportPerPost(f)) && others == 0, java.util.Arrays.toString(jobs));
            long[] none = Research.jobs(f, 0), negative = Research.jobs(f, -4);
            check("...none are none, and posts below nothing are none", java.util.Arrays.stream(none).sum() == 0 && java.util.Arrays.stream(negative).sum() == 0);
            long[] hundred = Research.jobs(f, 100);
            check("a hundred posts give the study's support: " + Research.supportPerPost(f) + " a researcher", hundred[s] == Math.round(100 * Research.supportPerPost(f))
                    && hundred[a] == 50 && hundred[b] == 50);

            double[] full = new double[today.length];
            java.util.Arrays.fill(full, 1.0);
            double[] empty = new double[today.length];
            near("a hundred filled " + f.name().toLowerCase() + " posts cost a hundred times the post's cost: the jobs and the cost say the same",
                    Research.bill(f, 100, today, full), 100 * Research.postCost(f, today), 1e-9 * 100 * Research.postCost(f, today));
            check("...a post nobody fills costs nothing and has no researcher", Research.payroll(f, 100, today, empty) == 0 && Research.bill(f, 100, today, empty) == 0
                    && Research.filledResearchers(f, 100, empty) == 0);
            double[] half = new double[today.length];
            java.util.Arrays.fill(half, 0.5);
            near("...half of them filled cost half and give half the researchers", Research.bill(f, 100, today, half), 0.5 * Research.bill(f, 100, today, full), 1e-9);
            near("...the researchers a hundred filled posts give are a hundred, the support not among them", Research.filledResearchers(f, 100, full), 100, 1e-9);
            near("...the bill is the payroll over the labour share", Research.bill(f, 100, today, half), Research.payroll(f, 100, today, half) / Research.labourShare(f), 1e-9);
            check("...and no fill array is full (as the game's payroll reads a missing one)", Research.bill(f, 100, today, null) == Research.bill(f, 100, today, full)
                    && Research.filledResearchers(f, 100, null) == 100 && Research.payroll(f, 100, null, full) == 0);
        }

        Research r = new Research();
        check("a grant is none to begin with, and a city's research is empty", r.grant(Field.LAW) == 0 && r.isEmpty());
        check("a grant of a positive amount is set, and zero is an amount", r.setGrant(Field.LAW, 250) && r.grant(Field.LAW) == 250 && r.setGrant(Field.LAW, 0) && r.grant(Field.LAW) == 0);
        r.setGrant(Field.LAW, 250);
        check("a negative grant, one not a number and an infinite one are refused: the grant stays as it was",
                !r.setGrant(Field.LAW, -1) && !r.setGrant(Field.LAW, Double.NaN) && !r.setGrant(Field.LAW, Double.POSITIVE_INFINITY) && r.grant(Field.LAW) == 250);
        check("...and a grant on no field is refused", !r.setGrant(null, 5));
        for (Field f : Field.values()) r.setGrant(f, 1e9);
        EnumMap<Field, Double> roomByField = new EnumMap<>(Field.class);
        roomByField.put(Field.ENGINEERING, 59.4);
        roomByField.put(Field.MEDICINE, 20.0);
        roomByField.put(Field.BUSINESS, 16.2);
        r.strikePosts(roomByField, today);
        check("striking the posts buys each field the whole posts of its room, and a field with no room none",
                r.posts(Field.ENGINEERING) == 59 && r.posts(Field.MEDICINE) == 20 && r.posts(Field.BUSINESS) == 16 && r.posts(Field.LAW) == 0);
        long[] sum = new long[JobType.values().length];
        for (Field f : Field.values()) {
            long[] one = Research.jobs(f, r.posts(f));
            for (int i = 0; i < sum.length; i++) sum[i] += one[i];
        }
        check("the month's jobs are the sum of every field's posts' jobs", java.util.Arrays.equals(r.jobs(), sum));
        check("...and the science band is asked for by engineering and medicine together, the policy band by business and law",
                r.jobs()[JobType.UNIV_SCIENCE.ordinal()] == Research.jobs(Field.ENGINEERING, 59)[JobType.UNIV_SCIENCE.ordinal()] + Research.jobs(Field.MEDICINE, 20)[JobType.UNIV_SCIENCE.ordinal()]
                        && r.jobs()[JobType.UNIV_POLICY.ordinal()] == Research.jobs(Field.BUSINESS, 16)[JobType.UNIV_POLICY.ordinal()]);
        r.strikePosts(null, today);
        check("striking against no room at all, and against wages that are not there, buys none", r.posts(Field.ENGINEERING) == 0 && java.util.Arrays.stream(r.jobs()).sum() == 0);
        r.strikePosts(roomByField, null);
        check("...the same when the wages are missing", r.posts(Field.ENGINEERING) == 0);
        double cheap = 2.5 * Research.postCost(Field.ENGINEERING, today);
        r.setGrant(Field.ENGINEERING, cheap);
        r.strikePosts(roomByField, today);
        check("a grant of two and a half posts' cost buys two", r.posts(Field.ENGINEERING) == 2);
    }

    /* ==================== 4. the water fill ==================== */

    static boolean sameShares(Research.Fill fill, double... expected) {
        if (fill.share().length != expected.length) return false;
        for (int i = 0; i < expected.length; i++) if (Math.abs(fill.share()[i] - expected[i]) > 1e-9) return false;
        return true;
    }

    static void sectionFour() {
        System.out.println("\n--- 4. researchers spread evenly over the open problems, none past its cap ---");
        Research.Fill even = Research.waterFill(30, new double[] {100, 100, 100});
        check("thirty researchers over three problems that can each take a hundred: ten each, none idle", sameShares(even, 10, 10, 10) && even.idle() == 0);
        Research.Fill small = Research.waterFill(30, new double[] {4, 100, 100});
        check("...a problem that can take four is filled, and the rest spilt evenly over the others: 4, 13, 13", sameShares(small, 4, 13, 13) && small.idle() == 0);
        Research.Fill cascade = Research.waterFill(30, new double[] {2, 4, 100});
        check("...two small caps both filled at once, the spare going to the third: 2, 4, 24", sameShares(cascade, 2, 4, 24) && cascade.idle() == 0);
        Research.Fill chain = Research.waterFill(30, new double[] {9, 100, 10.2});
        check("a cap over the first even share (10) and under the second (10.5) fills in a second round: 9, 10.8 and 10.2", sameShares(chain, 9, 10.8, 10.2) && chain.idle() == 0);
        Research.Fill full = Research.waterFill(50, new double[] {5, 5, 5});
        check("every cap full leaves the remainder idle: 50 over three problems of 5 leaves 35", sameShares(full, 5, 5, 5) && Math.abs(full.idle() - 35) < 1e-9);
        Research.Fill exact = Research.waterFill(15, new double[] {5, 5, 5});
        check("researchers equal to the caps together fill them all and leave none idle", sameShares(exact, 5, 5, 5) && exact.idle() == 0);
        Research.Fill noProblem = Research.waterFill(12, new double[0]);
        check("no open problem leaves them all idle", noProblem.share().length == 0 && noProblem.idle() == 12 && Research.waterFill(12, null).idle() == 12);
        check("no researchers, or a number that is not one, place none and leave none idle",
                sameShares(Research.waterFill(0, new double[] {5, 5}), 0, 0) && Research.waterFill(0, new double[] {5, 5}).idle() == 0
                        && sameShares(Research.waterFill(Double.NaN, new double[] {5}), 0) && sameShares(Research.waterFill(-3, new double[] {5}), 0)
                        && Research.waterFill(Double.NaN, new double[] {5}).idle() == 0);
        check("a cap that is nothing, below nothing or not a number takes none, and the others take its share",
                sameShares(Research.waterFill(10, new double[] {0, -2, Double.NaN, 100}), 0, 0, 0, 10));
        check("the shares come back in the order the caps were given", sameShares(Research.waterFill(10, new double[] {100, 3}), 7, 3));

        long x = 12345;
        List<String> wrong = new ArrayList<>();
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + (int) ((x = x * 6364136223846793005L + 1442695040888963407L) >>> 61);
            double[] caps = new double[n];
            for (int i = 0; i < n; i++) caps[i] = ((x = x * 6364136223846793005L + 1442695040888963407L) >>> 40) % 500 / 10.0;
            double people = ((x = x * 6364136223846793005L + 1442695040888963407L) >>> 40) % 3000 / 10.0;
            Research.Fill f = Research.waterFill(people, caps);
            double placed = 0;
            for (int i = 0; i < n; i++) {
                placed += f.share()[i];
                if (f.share()[i] < -1e-12 || f.share()[i] > caps[i] + 1e-9) wrong.add(trial + ": share " + f.share()[i] + " against cap " + caps[i]);
                for (int j = 0; j < n; j++) {
                    if (f.share()[i] < f.share()[j] - 1e-9 && f.share()[i] < caps[i] - 1e-9) wrong.add(trial + ": problem " + i + " has less than " + j + " and is not full");
                }
            }
            if (Math.abs(placed + f.idle() - people) > 1e-7) wrong.add(trial + ": placed " + placed + " idle " + f.idle() + " of " + people);
            if (f.idle() > 0) {
                for (int i = 0; i < n; i++) if (f.share()[i] < caps[i] - 1e-9) wrong.add(trial + ": idle researchers beside a problem with room");
            }
        }
        check("over 300 random fills: every share within its cap, the shares and the idle add to the people, a problem has less than another only when full, and idle only when all are", wrong.isEmpty(), wrong.toString());
    }

    /* ==================== 5. open problems ==================== */

    static void sectionFive() {
        System.out.println("\n--- 5. a problem is open from its science to its date, once its needs are known ---");
        check("the lags are the study's: engineering 7 years, medicine 9, business 4, law 4",
                Research.lagYears(Field.ENGINEERING) == STUDY_LAG_YEARS.get(Field.ENGINEERING) && Research.lagYears(Field.MEDICINE) == STUDY_LAG_YEARS.get(Field.MEDICINE)
                        && Research.lagYears(Field.BUSINESS) == STUDY_LAG_YEARS.get(Field.BUSINESS) && Research.lagYears(Field.LAW) == STUDY_LAG_YEARS.get(Field.LAW));
        check("...a lag in months is twelve times it, and law's is the most months a law lever may be brought forward (PolicyDates.MAX_EARLY_MONTHS)",
                Research.lagMonths(Field.ENGINEERING) == 84 && Research.lagMonths(Field.MEDICINE) == 108 && Research.lagMonths(Field.LAW) == PolicyDates.MAX_EARLY_MONTHS);

        Entry pe14 = of("PE14");
        int opens = pe14.worldMonth() - Research.lagMonths(pe14.field());
        Research none = new Research();
        check("fixture: the tractor's PE14 needs nothing and its science opens after the first month",
                pe14.needs().isEmpty() && opens > 1 && pe14.field() == Field.ENGINEERING && opens == pe14.worldMonth() - 84);
        check("PE14 is not open the month before its science exists, is open in that month, and is open the month before history has it",
                !none.isOpen(pe14, opens - 1) && none.isOpen(pe14, opens) && none.isOpen(pe14, pe14.worldMonth() - 1));
        check("...and not open once history has it", !none.isOpen(pe14, pe14.worldMonth()) && !none.isOpen(pe14, pe14.worldMonth() + 50));
        Research found = new Research();
        found.restore(null, null, null, Map.of("PE14", opens + 10));
        check("...nor once the city has found it, from the month it found it; it was open the month before",
                found.isOpen(pe14, opens + 9) && !found.isOpen(pe14, opens + 10) && !found.isOpen(pe14, opens + 11) && found.known("PE14", opens + 10) && !found.known("PE14", opens + 9));
        check("the open problems of a field are its entries in the catalogue's order, and PE14 is in engineering's at its window and in no other field's",
                none.openProblems(Field.ENGINEERING, opens).contains(pe14) && !none.openProblems(Field.MEDICINE, opens).contains(pe14)
                        && !none.openProblems(Field.ENGINEERING, opens - 1).contains(pe14));

        // An entry whose need is not known when its own science opens: the need blocks it until the need is known, here or abroad.
        Entry blocked = null, need = null;
        for (Entry e : CAT.entries()) {
            for (String n : e.needs()) {
                Entry d = of(n);
                if (blocked == null && d != null && d.worldMonth() > Math.max(1, e.worldMonth() - Research.lagMonths(e.field())) && d.worldMonth() < e.worldMonth()) {
                    blocked = e;
                    need = d;
                }
            }
        }
        check("fixture: an entry exists whose need history dates after the entry's science opens (a need that blocks)", blocked != null);
        if (blocked != null) {
            int science = Math.max(1, blocked.worldMonth() - Research.lagMonths(blocked.field()));
            check("the entry (" + blocked.code() + ", needing " + need.code() + ") is not open while its need is unknown, though its science exists",
                    !none.isOpen(blocked, science) && !none.isOpen(blocked, need.worldMonth() - 1) && science < need.worldMonth());
            check("...and open the month its need arrives from abroad (when it needs only that)", blocked.needs().size() != 1 || none.isOpen(blocked, need.worldMonth()));
            Research early = new Research();
            early.restore(null, null, null, Map.of(need.code(), science + 1));
            check("...and open from the month the city found the need itself, a month before that arrives abroad",
                    blocked.needs().size() != 1 || science + 1 >= need.worldMonth() || (early.isOpen(blocked, science + 1) && !early.isOpen(blocked, science)));
        }

        List<String> wrong = new ArrayList<>();
        for (Entry e : CAT.entries()) {
            for (int m = 1; m <= e.worldMonth() + 2; m++) {
                boolean needsKnown = true;
                for (String n : e.needs()) needsKnown &= m >= CAT.worldMonth(n);
                boolean expected = m < e.worldMonth() && m >= e.worldMonth() - 12 * STUDY_LAG_YEARS.get(e.field()) && needsKnown;
                if (none.isOpen(e, m) != expected) wrong.add(e.code() + " at " + m);
            }
        }
        check("over every entry and month: open exactly when history has not dated it, its science exists (the study's lag) and history has dated each need", wrong.isEmpty(), wrong.toString());

        check("the minimum years are the study's: 3 for a building, 3 for equipment, 2 for a recipe",
                Research.minYears(of("E4")) == 3 && Research.minYears(of("E1")) == 3 && Research.minYears(of("E3")) == 2);
        Entry before = null, after = null;
        for (Entry e : CAT.entries()) {
            if (e.size() != Size.DRUG) continue;
            if (e.year() < 1962 && before == null) before = e;
            if (e.year() >= 1962 && after == null) after = e;
        }
        check("fixture: a drug dated before 1962 and one dated after it are in the catalogue", before != null && after != null);
        if (before != null && after != null) {
            check("a drug dated before 1962 (" + before.code() + ") takes 2 years at least (research-1900 Q6), one dated after (" + after.code() + ") takes 8",
                    Research.minYears(before) == 2 && Research.minYears(after) == 8);
            near("the team cap is the work over the minimum years: " + after.code() + " " + after.work() + " researcher-years over 8", Research.teamCap(after), after.work() / 8.0, 1e-9);
        }
        near("...and a building's: E4 " + of("E4").work() + " over 3", Research.teamCap(of("E4")), of("E4").work() / 3.0, 1e-9);
    }

    /* ==================== 6. the hidden size ==================== */

    /** hiddenSize()'s size over the work, from the study's model: research_model.py hidden_size(seed, id) with id the code's Java hash, computed there. */
    static void spot(String label, long seed, String code, double expected) {
        near(label, Research.hiddenSize(seed, of(code)) / of(code).work(), expected, 1e-12);
    }

    static void sectionSix() {
        System.out.println("\n--- 6. the hidden size is the model's draw from the seed and the code: mean 1, the dial's spread, reproducible ---");
        Entry pe14 = of("PE14");
        check("the same seed and code give the same size, every time", Research.hiddenSize(7, pe14) == Research.hiddenSize(7, pe14) && Research.hiddenSize(7, pe14) > 0);
        spot("PE14 at seed 20261010 is research_model.py's 1.7407918672011038 times its work", 20261010L, "PE14", 1.7407918672011038);
        spot("E1 at seed 20261010 is the model's 0.5452431449769025", 20261010L, "E1", 0.5452431449769025);
        spot("M3 at seed 1 is the model's 1.222707267469131", 1L, "M3", 1.222707267469131);
        spot("L4 at seed -7 (a seed below nothing) is the model's 1.3640662285097322", -7L, "L4", 1.3640662285097322);

        int n = 20000;
        double sum = 0, logSum = 0, logSq = 0;
        for (int seed = 1; seed <= n; seed++) {
            double ratio = Research.hiddenSize(seed, pe14) / pe14.work();
            sum += ratio;
            logSum += Math.log(ratio);
            logSq += Math.log(ratio) * Math.log(ratio);
        }
        double meanLog = logSum / n, sd = Math.sqrt(logSq / n - meanLog * meanLog);
        near("over " + n + " seeds the mean of size over work is 1", sum / n, 1, 0.02);
        near("...the standard deviation of its logarithm is the dial's spread", sd, Research.SIZE_SIGMA, 0.02);
        near("...and the mean of its logarithm is minus half the spread squared, which is what makes the mean 1", meanLog, -Research.SIZE_SIGMA * Research.SIZE_SIGMA / 2, 0.02);

        Set<Double> ratios = new HashSet<>();
        for (Entry e : CAT.entries()) ratios.add(Research.hiddenSize(99, e) / e.work());
        check("different codes on one seed draw different sizes: all " + CAT.entries().size() + " draws are different", ratios.size() == CAT.entries().size());
        check("...and the same code on different seeds does", Research.hiddenSize(1, pe14) != Research.hiddenSize(2, pe14));
    }

    /* ==================== 7. no researchers: history's dates ==================== */

    static void sectionSeven() {
        System.out.println("\n--- 7. with no researchers every discovery arrives abroad, in history's month, once ---");
        Research r = new Research();
        EnumMap<Field, Double> none = new EnumMap<>(Field.class);
        Map<String, List<Integer>> arrivals = new HashMap<>();
        boolean piled = false, idleOnly = true;
        int sawOpen = 0, last = lastWorldMonth() + 1;
        for (int m = 1; m <= last; m++) {
            r.month(m, 20261010L, none);
            for (String code : r.arrivedThisMonth()) arrivals.computeIfAbsent(code, c -> new ArrayList<>()).add(m);
            piled |= !r.piles().isEmpty() || !r.foundThisMonth().isEmpty();
            for (Field f : Field.values()) {
                idleOnly &= r.researchers(f) == 0 && r.idle(f) == 0;
                sawOpen += r.open(f);
            }
        }
        check("nothing is found here, and no work is ever piled, in " + last + " months", r.foundHere().isEmpty() && !piled && r.isEmpty());
        List<String> wrong = new ArrayList<>();
        for (Entry e : CAT.entries()) {
            if (!List.of(e.worldMonth()).equals(arrivals.get(e.code()))) wrong.add(e.code() + " arrived " + arrivals.get(e.code()) + " not " + e.worldMonth());
        }
        check("every one of the " + CAT.entries().size() + " discoveries arrives in exactly its worldMonth, and in no other", wrong.isEmpty() && arrivals.size() == CAT.entries().size(), wrong.toString());
        check("...and nothing arrives the month after the last", r.arrivedThisMonth().isEmpty());
        check("problems were open to the (absent) researchers in some months, and none of them was idle: nobody was there", sawOpen > 0 && idleOnly);
        List<String> known = new ArrayList<>();
        for (Entry e : CAT.entries()) {
            for (int m : new int[] {1, e.worldMonth() - 1, e.worldMonth(), e.worldMonth() + 1}) {
                if (r.known(e.code(), m) != (m >= e.worldMonth())) known.add(e.code() + " at " + m);
            }
        }
        check("so a city that found nothing knows every code from history's month and not before it", known.isEmpty(), known.toString());

        Research again = new Research();
        for (int m = 1; m <= last; m++) again.month(m, 1L, null);
        check("a missing researchers map is none too: the same, whatever the seed", again.foundHere().isEmpty() && again.isEmpty());
    }

    /* ==================== 8. a find, 9. known in this city ==================== */

    /** One field staffed with a great many researchers from month 1 to the month after history's last: the Research, and everything that went wrong. */
    record Run(Research research, List<String> faults) { }

    static Run run(long seed, Field field, double people) {
        Research r = new Research();
        EnumMap<Field, Double> staff = new EnumMap<>(Field.class);
        staff.put(field, people);
        List<String> faults = new ArrayList<>();
        for (int m = 1; m <= lastWorldMonth() + 2; m++) {
            Map<String, Double> before = r.piles();
            r.month(m, seed, staff);
            Map<String, Double> after = r.piles();
            for (Map.Entry<String, Double> e : after.entrySet()) {
                Entry en = of(e.getKey());
                double was = before.getOrDefault(e.getKey(), 0.0);
                if (en.field() != field) faults.add(en.code() + " has a pile in another field at " + m);
                if (e.getValue() - was > Research.teamCap(en) / 12 + 1e-9) faults.add(en.code() + " grew by " + (e.getValue() - was) + " at " + m + ", past its cap " + Research.teamCap(en) / 12);
                if (!r.isOpen(en, m)) faults.add(en.code() + " is worked on at " + m + " and is not open");
            }
            for (String code : r.foundThisMonth()) {
                Entry en = of(code);
                double size = Research.hiddenSize(seed, en), was = before.getOrDefault(code, 0.0), cap = Research.teamCap(en) / 12;
                int science = Math.max(1, en.worldMonth() - Research.lagMonths(en.field()));
                if (en.field() != field) faults.add(code + " was found in another field");
                if (m >= en.worldMonth()) faults.add(code + " was found at " + m + ", not before history's " + en.worldMonth());
                if (m < science) faults.add(code + " was found at " + m + ", before its science at " + science);
                for (String need : en.needs()) {
                    if (!(r.foundMonth(need) < m || CAT.worldMonth(need) <= m)) faults.add(code + " was found at " + m + " before its need " + need + " was known");
                }
                if (was >= size) faults.add(code + " had reached its size before " + m);
                if (was + cap < size - 1e-9) faults.add(code + " was found at " + m + " with a pile that could not have reached its size");
                if (size > cap * (m - science + 1) + 1e-9) faults.add(code + " was found at " + m + " faster than its team cap allows");
                if (after.containsKey(code)) faults.add(code + " keeps a pile after it was found");
                if (r.foundMonth(code) != m) faults.add(code + " is not recorded at " + m);
            }
        }
        return new Run(r, faults);
    }

    /** Whether a code is carried as a "since" by a recipe, a piece of equipment or a building of the shipped file. */
    static Set<String> carried(List<BuildingsTemplate> shipped, boolean recipes, boolean equipment, boolean buildings) {
        Set<String> codes = new HashSet<>();
        for (BuildingsTemplate t : shipped) {
            if (recipes) for (Recipe rec : t.getRecipes()) if (!rec.isYear() && CAT.entry(rec.getSince()) != null) codes.add(rec.getSince());
            if (equipment) for (Equipment piece : t.getEquipment()) if (!piece.isYear() && CAT.entry(piece.getSince()) != null) codes.add(piece.getSince());
            if (buildings && CAT.entry(t.getSinceText()) != null) codes.add(t.getSinceText());
        }
        return codes;
    }

    static void sectionEightAndNine() {
        System.out.println("\n--- 8. with a great many researchers a field finds discoveries here, before history, by the rules ---");
        long[] seeds = {20261010L, 1, 2, 3};
        for (Field f : Field.values()) {
            List<String> faults = new ArrayList<>();
            int found = 0, before = 0;
            for (long seed : seeds) {
                Run run = run(seed, f, 5000);
                faults.addAll(run.faults());
                for (Map.Entry<String, Integer> e : run.research().foundHere().entrySet()) {
                    found++;
                    if (e.getValue() < of(e.getKey()).worldMonth()) before++;
                    if (of(e.getKey()).field() != f) faults.add(e.getKey() + " found in " + f);
                }
            }
            check("5,000 " + f.name().toLowerCase() + " researchers on " + seeds.length + " seeds: " + found + " found here, all before history, and no rule broken: not before the science exists or the needs are known, "
                    + "never past the team cap, in the first month the pile reached the size, never in another field", found > 0 && found == before && faults.isEmpty(),
                    faults.size() > 5 ? faults.subList(0, 5) + " ..." : faults.toString());
        }

        // The minimum years, held to the study's numbers (a drug of 1962 or later 8, before it 2) and not to minYears(): months worked >= 12 x years x size / work.
        record Drug(String code, int years) { }
        for (Drug d : List.of(new Drug("M10", 8), new Drug("PM6", 2))) {
            Entry e = of(d.code());
            boolean tried = false, held = true;
            List<String> faults = new ArrayList<>();
            for (long seed = 1; seed <= 40 && !tried; seed++) {
                Run medicine = run(seed, Field.MEDICINE, 1e6);
                faults.addAll(medicine.faults());
                int at = medicine.research().foundMonth(d.code());
                if (at == Integer.MAX_VALUE) continue;
                tried = true;
                int science = Math.max(1, e.worldMonth() - Research.lagMonths(Field.MEDICINE));
                held = at - science + 1 >= 12.0 * d.years() * Research.hiddenSize(seed, e) / e.work() - 1e-9;
            }
            check("a million medical researchers do not find " + d.code() + " in less than " + d.years() + " years' work: found here on some seed, and no sooner than 12 x " + d.years()
                    + " x size / work months after its science opened", tried && held && faults.isEmpty(), faults.toString());
        }

        System.out.println("\n--- 9. a discovery found here is known here from that month; history's reading is not ---");
        List<BuildingsTemplate> shipped = new BuildingCatalog().load();
        Set<String> pieces = carried(shipped, true, true, false), houses = carried(shipped, false, false, true);
        long picked = -1;
        Research r = null;
        for (long seed = 1; seed <= 200 && picked < 0; seed++) {
            Run run = run(seed, Field.ENGINEERING, 5000);
            Research candidate = run.research();
            if (candidate.foundMonth("PE14") < of("PE14").worldMonth() && candidate.foundMonth("PE11") < of("PE11").worldMonth()
                    && candidate.foundMonth("E1") < of("E1").worldMonth() && run.faults().isEmpty()) {
                picked = seed;
                r = candidate;
            }
        }
        check("fixture: a seed exists on which the city finds the tractor's PE14, the cracking units' PE11 and the oxygen converter's E1 before history does (seed " + picked + ")", picked > 0);
        if (picked < 0 || r == null) return;
        Map<String, Integer> foundMap = r.foundHere();
        int pe14 = r.foundMonth("PE14"), pe11 = r.foundMonth("PE11");
        check("fixture: the shipped file carries PE14 as a piece and a recipe, E1 as a recipe, and PE11 as a building", pieces.contains("PE14") && pieces.contains("E1") && houses.contains("PE11"));

        BuildingsTemplate grain = byId(shipped, 53);
        Equipment tractor = grain == null ? null : grain.getEquipment("tractor");
        Recipe tractorRecipe = grain == null ? null : grain.getRecipe("tractor"), horses = grain == null ? null : grain.getRecipe("horses");
        check("fixture: the Grain Farm has the tractor, its recipe and the horses' recipe", tractor != null && tractorRecipe != null && horses != null);
        if (tractor == null || tractorRecipe == null || horses == null) return;
        check("the tractor, found here in month " + pe14 + ", is known then and not the month before; history's reading is not known until PE14's month " + of("PE14").worldMonth(),
                tractor.knownAt(pe14, r) && !tractor.knownAt(pe14 - 1, r) && !tractor.knownAt(pe14) && tractor.knownAt(of("PE14").worldMonth()) && tractor.knownAt(of("PE14").worldMonth(), r));
        check("...its recipe the same", tractorRecipe.knownAt(pe14, r) && !tractorRecipe.knownAt(pe14 - 1, r) && !tractorRecipe.knownAt(pe14));
        check("...and the horses' recipe, dated by a year, is untouched by the find", horses.knownAt(1, r) == horses.knownAt(1) && horses.knownAt(pe14, r) == horses.knownAt(pe14));

        List<String> disagree = new ArrayList<>();
        int asked = 0;
        List<Object> things = new ArrayList<>();
        for (BuildingsTemplate t : shipped) {
            things.add(t);
            things.addAll(t.getRecipes());
            things.addAll(t.getEquipment());
        }
        Research empty = new Research();
        int last = lastWorldMonth() + 2;
        for (Object thing : things) {
            String since = thing instanceof Recipe rec ? rec.getSince() : thing instanceof Equipment piece ? piece.getSince() : ((BuildingsTemplate) thing).getSinceText();
            Integer at = foundMap.get(since);
            for (int m = 0; m <= last; m += 3) {
                boolean history, withNull, withEmpty, withFound;
                if (thing instanceof Recipe rec) {
                    history = rec.knownAt(m); withNull = rec.knownAt(m, null); withEmpty = rec.knownAt(m, empty); withFound = rec.knownAt(m, r);
                } else if (thing instanceof Equipment piece) {
                    history = piece.knownAt(m); withNull = piece.knownAt(m, null); withEmpty = piece.knownAt(m, empty); withFound = piece.knownAt(m, r);
                } else {
                    BuildingsTemplate t = (BuildingsTemplate) thing;
                    history = t.knownAt(m); withNull = t.knownAt(m, null); withEmpty = t.knownAt(m, empty); withFound = t.knownAt(m, r);
                }
                asked++;
                boolean expected = history || (at != null && m >= at);
                if (withNull != history || withEmpty != history || withFound != expected) disagree.add(thing + " at " + m);
            }
        }
        check("over every recipe, piece and building of buildings.json (" + things.size() + ", " + asked + " readings): with no research or an empty one the reading is history's; with this city's it is "
                + "history's or the month the code was found, whichever is first, and a year or an undated row is never moved", disagree.isEmpty(), disagree.size() > 5 ? disagree.subList(0, 5).toString() : disagree.toString());

        check("a template dated by PE11 (the Small Cracking Unit and the Cracking Unit, ids 79 and 80) is known here from the month PE11 was found, and history's reading is not",
                byId(shipped, 79).knownAt(pe11, r) && !byId(shipped, 79).knownAt(pe11 - 1, r) && !byId(shipped, 79).knownAt(pe11) && byId(shipped, 80).knownAt(pe11, r)
                        && !byId(shipped, 80).knownAt(pe11));
        check("...and an undated row stays known from the first month", byId(shipped, 0).knownAt(1) && byId(shipped, 0).knownAt(1, r) && byId(shipped, 0).getSinceText().isEmpty());

        check("the pure cores agree: the tractor recipe may be run by the fitted part in month " + pe14 + " with the research and not without it, nor with an empty one",
                Recipes.canRun(grain, tractorRecipe, true, pe14, r) && !Recipes.canRun(grain, tractorRecipe, true, pe14) && !Recipes.canRun(grain, tractorRecipe, true, pe14, null)
                        && !Recipes.canRun(grain, tractorRecipe, true, pe14, empty) && !Recipes.canRun(grain, tractorRecipe, true, pe14 - 1, r));
        check("...and still not by the unfitted part (it needs the tractor), found or not", !Recipes.canRun(grain, tractorRecipe, false, pe14, r) && Recipes.canRun(grain, horses, false, pe14, r));
        Recipes.Prices prices = new Recipes.Prices(good -> 1.0, wages(false), 0.1, 0.1, 1);
        BuildingsTemplate shop = byId(shipped, 106);
        Recipe oxygen = shop == null ? null : shop.getRecipe("basic-oxygen"), bessemer = shop == null ? null : shop.getRecipe("bessemer"), hearth = shop == null ? null : shop.getRecipe("open-hearth");
        // Pig iron dear enough that the oxygen converter, which uses less of it, beats the Bessemer converter, so that the choice rests on what is known.
        Recipes.Prices dear = prices.withGoods(good -> good == Good.PIG_IRON ? 2.0 : 1.0);
        check("fixture: the Oxygen Steel Shop's oxygen recipe (E1) is its default, the Bessemer recipe (1900) is older and needs no piece, and at pig iron twice the price the oxygen recipe is worth more",
                oxygen != null && bessemer != null && hearth != null && shop.defaultRecipe() == oxygen && bessemer.getNeedsEquipment() == null && oxygen.getNeedsEquipment() == null
                        && Recipes.value(oxygen, dear) > Recipes.value(bessemer, dear));
        if (oxygen == null || bessemer == null || hearth == null) return;
        int e1 = r.foundMonth("E1");
        check("the part without the open hearth runs the oxygen recipe from the month E1 was found here, and the Bessemer one before and without it, nor does an empty research move it",
                Recipes.best(shop, false, e1, dear, r) == oxygen && Recipes.best(shop, false, e1 - 1, dear, r) == bessemer && Recipes.best(shop, false, e1, dear) == bessemer
                        && Recipes.best(shop, false, e1, dear, empty) == bessemer && Recipes.best(shop, false, e1, dear, null) == bessemer);
        check("...so the open hearth's gain over the unfitted part is that against the oxygen recipe with the find, and against the Bessemer without it",
                Recipes.recipeGain(shop, e1, dear, r) == Recipes.value(hearth, dear) - Recipes.value(oxygen, dear)
                        && Recipes.recipeGain(shop, e1, dear) == Recipes.value(hearth, dear) - Recipes.value(bessemer, dear)
                        && Recipes.recipeGain(shop, e1, dear, empty) == Recipes.recipeGain(shop, e1, dear));
        check("...and the oxygen recipe is never the fitted part's, found or not", !Recipes.canRun(shop, oxygen, true, e1, r) && Recipes.canRun(shop, oxygen, false, e1, r) && !Recipes.canRun(shop, oxygen, false, e1));
        List<String> bestMoved = new ArrayList<>();
        for (BuildingsTemplate t : shipped) {
            if (!t.hasRecipeChoice()) continue;
            for (int m : new int[] {1, 200, 600, 900, last}) {
                for (boolean fitted : new boolean[] {true, false}) {
                    if (!Recipes.best(t, fitted, m, prices).getId().equals(Recipes.best(t, fitted, m, prices, empty).getId())
                            || !Recipes.best(t, fitted, m, prices).getId().equals(Recipes.best(t, fitted, m, prices, null).getId())) bestMoved.add(t.getName() + " at " + m);
                }
                if (Recipes.recipeGain(t, m, prices) != Recipes.recipeGain(t, m, prices, empty)) bestMoved.add(t.getName() + " gain at " + m);
            }
        }
        check("in a city that found nothing, the best recipe and the gain of every template with a choice are history's, as before R3", bestMoved.isEmpty(), bestMoved.toString());

        double rate = 0.06, cost = 100, factor = EquipmentFit.annuityFactor(tractor.getLifeMonths(), rate), hurdle = cost * rate / 12 * BusinessInvestment.PROFIT_OVER_INTEREST;
        double line = Math.max(cost / factor, hurdle) * 1.001;
        check("the tractor with figures that pay is NOT_KNOWN in month " + pe14 + " by history's reading and PAYS with the find; FITTED when no building lacks it",
                EquipmentFit.judge(tractor, grain, pe14, 1, line, cost, rate, hurdle).kind() == EquipmentFit.Kind.NOT_KNOWN
                        && EquipmentFit.judge(tractor, grain, pe14, 1, line, cost, rate, hurdle, r).kind() == EquipmentFit.Kind.PAYS
                        && EquipmentFit.judge(tractor, grain, pe14, 1, line, cost, rate, hurdle, empty).kind() == EquipmentFit.Kind.NOT_KNOWN
                        && EquipmentFit.judge(tractor, grain, pe14 - 1, 1, line, cost, rate, hurdle, r).kind() == EquipmentFit.Kind.NOT_KNOWN
                        && EquipmentFit.judge(tractor, grain, pe14, 0, line, cost, rate, hurdle, r).kind() == EquipmentFit.Kind.FITTED);
    }

    /* ==================== 10. reproducible ==================== */

    static void sectionTen() {
        System.out.println("\n--- 10. the same months, researchers and seed find the same things; a saved run continues as the whole one ---");
        Run a = run(1, Field.ENGINEERING, 800), b = run(1, Field.ENGINEERING, 800);
        check("fixture: the run finds something here", !a.research().foundHere().isEmpty());
        check("two runs on the same seed find the same codes in the same months, in the same order",
                a.research().foundHere().equals(b.research().foundHere()) && new ArrayList<>(a.research().foundHere().keySet()).equals(new ArrayList<>(b.research().foundHere().keySet())));
        Set<Map<String, Integer>> distinct = new HashSet<>();
        for (long seed = 1; seed <= 6; seed++) distinct.add(run(seed, Field.ENGINEERING, 800).research().foundHere());
        check("six seeds do not all find the same things in the same months: " + distinct.size() + " different histories", distinct.size() > 1);

        long seed = 3;
        EnumMap<Field, Double> staff = new EnumMap<>(Field.class);
        staff.put(Field.ENGINEERING, 800.0);
        staff.put(Field.MEDICINE, 300.0);
        Research whole = new Research(), first = new Research();
        whole.setGrant(Field.LAW, 100);
        first.setGrant(Field.LAW, 100);
        EnumMap<Field, Double> room = new EnumMap<>(Field.class);
        room.put(Field.LAW, 50.0);
        first.strikePosts(room, wages(true));
        int cut = 450, last = lastWorldMonth() + 2;
        for (int m = 1; m <= cut; m++) first.month(m, seed, staff);
        check("fixture: at month " + cut + " there are discoveries found, work piled on others, a grant and posts to save",
                !first.foundHere().isEmpty() && !first.piles().isEmpty() && first.grant(Field.LAW) > 0 && first.posts(Field.LAW) > 0);
        Research second = new Research();
        second.restore(first.grantsForSave(), first.postsForSave(), first.piles(), first.foundHere());
        check("what is saved is put back whole: the grant, the posts, the piles and the finds in their order",
                second.grant(Field.LAW) == first.grant(Field.LAW) && second.posts(Field.LAW) == first.posts(Field.LAW) && second.piles().equals(first.piles())
                        && second.foundHere().equals(first.foundHere()) && new ArrayList<>(second.foundHere().keySet()).equals(new ArrayList<>(first.foundHere().keySet())));
        for (int m = cut + 1; m <= last; m++) {
            first.month(m, seed, staff);
            second.month(m, seed, staff);
        }
        for (int m = 1; m <= cut; m++) whole.month(m, seed, staff);
        for (int m = cut + 1; m <= last; m++) whole.month(m, seed, staff);
        check("a run saved at month " + cut + ", restored into another Research and carried on, finds what the unbroken run finds, in the same months",
                second.foundHere().equals(whole.foundHere()) && first.foundHere().equals(whole.foundHere()) && second.piles().equals(whole.piles()));

        Research bad = new Research();
        bad.restore(Map.of("NONSENSE", 5.0, "LAW", -1.0, "MEDICINE", 2.0), Map.of("LAW", -3, "BUSINESS", 4), Map.of("ZZ99", 3.0, "PE14", 2.5, "PE11", Double.NaN),
                new LinkedHashMap<>(Map.of("ZZ99", 5, "PE14", 0, "PE11", 7)));
        check("a malformed save is skipped entry by entry: a field that is not one, an amount below nothing, a code the catalogue lacks, a month before the first, a pile not a number",
                bad.grant(Field.MEDICINE) == 2 && bad.grant(Field.LAW) == 0 && bad.posts(Field.BUSINESS) == 4 && bad.posts(Field.LAW) == 0 && bad.piles().equals(Map.of("PE14", 2.5))
                        && bad.foundHere().equals(Map.of("PE11", 7)));
        bad.restore(null, null, null, null);
        check("...and restoring nothing empties it", bad.isEmpty());
        check("the saves are keyed by field name and by code, never by an ordinal",
                first.grantsForSave().keySet().stream().allMatch(k -> k.equals("LAW")) && first.postsForSave().keySet().stream().allMatch(k -> k.equals("LAW"))
                        && first.piles().keySet().stream().allMatch(k -> CAT.entry(k) != null) && first.foundHere().keySet().stream().allMatch(k -> CAT.entry(k) != null));
    }

    /* ==================== 11. a new game clears it ==================== */

    static void sectionEleven() {
        System.out.println("\n--- 11. a new game starts with no research ---");
        Game g = new Game(GameFiles.scratch("discovery-new-game"));
        quietly(g::newGame);
        Research research = g.getResearch();
        check("a city founded the default way has an empty research", research != null && research.isEmpty() && research.foundHere().isEmpty());

        BuildingsTemplate brick = g.getBuildingManager().getTemplate(79), grain = g.getBuildingManager().getTemplateByName("Grain Farm");
        Equipment tractor = grain == null ? null : grain.getEquipment("tractor");
        check("fixture: the city's templates have the Small Cracking Unit dated by PE11 and the Grain Farm with its tractor", brick != null && brick.getSinceText().equals("PE11") && tractor != null);
        if (brick == null || tractor == null) return;
        int month = g.getMonth();
        check("fixture: it is before PE11's and PE14's months", month < of("PE11").worldMonth() && month < of("PE14").worldMonth());
        check("history's reading holds in a city that found nothing: the Small Cracking Unit is not known and the tractor's verdict is NOT_KNOWN",
                !g.knows(brick) && EquipmentFit.verdictForNew(g, grain, tractor).kind() == EquipmentFit.Kind.NOT_KNOWN);

        research.setGrant(Field.ENGINEERING, 40);
        research.setGrant(Field.LAW, 7);
        research.restore(research.grantsForSave(), Map.of("ENGINEERING", 3), Map.of("E1", 4.5), Map.of("PE11", month, "PE14", month));
        check("fixture: the city's research has grants, posts, work piled and two finds", !research.isEmpty() && research.grant(Field.ENGINEERING) == 40 && research.posts(Field.ENGINEERING) == 3
                && research.piles().containsKey("E1") && research.foundHere().size() == 2);
        check("the game asks the city's research: the Small Cracking Unit is known this month, and the tractor's verdict is no longer NOT_KNOWN",
                g.knows(brick) && EquipmentFit.verdictForNew(g, grain, tractor).kind() != EquipmentFit.Kind.NOT_KNOWN);
        check("...and the template's own reading, history's, is unchanged", !brick.knownAt(month) && brick.knownAt(month, research));

        quietly(g::newGame);
        check("after a new game the same research object has no grant, no post, no work piled and nothing found",
                g.getResearch() == research && research.isEmpty() && research.grant(Field.ENGINEERING) == 0 && research.grant(Field.LAW) == 0
                        && research.posts(Field.ENGINEERING) == 0 && research.piles().isEmpty() && research.foundHere().isEmpty());
        BuildingsTemplate brickAgain = g.getBuildingManager().getTemplate(79), grainAgain = g.getBuildingManager().getTemplateByName("Grain Farm");
        check("...and the city is back to history's reading: the Small Cracking Unit is unknown and the tractor NOT_KNOWN again",
                !g.knows(brickAgain) && EquipmentFit.verdictForNew(g, grainAgain, grainAgain.getEquipment("tractor")).kind() == EquipmentFit.Kind.NOT_KNOWN);
    }

    /* ============================================================================================================================================
       R3 SLICE B: THE RESEARCH IN THE CITY'S MONTH (sections 12 to 18). A city founded the 1900 way, a University (and a Law School where a section
       needs one) standing, the staff handed it as MaterialsCheck does; every figure asserted against Research's and the model's own functions.
       ============================================================================================================================================ */

    /** One press of the clock, quietly (the player's button: the auto-builder's pass first). */
    static void play(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    /** A village (FittingCheck.village) with a University standing, and a Law School if asked, and the staff handed; founded the 1900 way or the default way. */
    static Game cityWithSchools(String label, boolean the1900Way, boolean lawSchool) {
        FittingCheck.quiet = quiet;
        EquipmentCheck.quiet = quiet;
        Game g = FittingCheck.village(label, the1900Way ? Founding.defaults().withUnfitted(true) : Founding.defaults(), false, false);
        quietly(() -> {
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("University"), 1, true);
            if (lawSchool) g.buildStack(b.getTemplateByName("Law School"), 1, true);
            MaterialsCheck.handWorkers(g);
        });
        return g;
    }

    /** The posts the standing schools have room for in each field, written from each stack's template and quantity (Research.staffByField(), Research.room()). */
    static EnumMap<Field, Double> roomOf(Game g) {
        EnumMap<Field, Double> room = new EnumMap<>(Field.class);
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            double[] staff = staffOf(s.getBuilding());
            for (int i = 0; i < staff.length; i++) staff[i] *= s.getQuantity();
            for (Map.Entry<Field, Double> share : Research.staffByField(s.getBuilding().getTeaches(), staff).entrySet()) room.merge(share.getKey(), Research.room(share.getValue()), Double::sum);
        }
        return room;
    }

    /** The notices in a city's inbox whose key starts as given, in the order raised. */
    static List<Notice> noticesKeyed(Game g, String prefix) {
        List<Notice> out = new ArrayList<>();
        for (Notice n : g.getInbox().all()) if (n.getKey().startsWith(prefix)) out.add(n);
        return out;
    }

    /** Whether two long arrays are equal, entry by entry. */
    static boolean same(long[] a, long[] b) {
        return java.util.Arrays.equals(a, b);
    }

    /** Whether a research holds no post in any field (it may hold work piled and finds: the faculty of a school research with no grant at all). */
    static boolean noPosts(Research r) {
        for (Field f : Field.values()) if (r.posts(f) != 0) return false;
        return true;
    }

    /** The buildings' jobs plus the research posts' of a field, by JobType ordinal: what the labour market is to see. */
    static long[] plus(long[] buildings, long[] posts) {
        long[] sum = buildings.clone();
        for (int i = 0; i < sum.length; i++) sum[i] += posts[i];
        return sum;
    }

    /** The money bill of a research the way the model prices it: the sums of Research.payroll() and Research.bill() over the fields at the wages and fill. */
    static double[] billOf(Research research, double[] wages, double[] fill) {
        double payroll = 0, bill = 0;
        for (Field f : Field.values()) {
            payroll += Research.payroll(f, research.posts(f), wages, fill);
            bill += Research.bill(f, research.posts(f), wages, fill);
        }
        return new double[] {payroll, bill - payroll};
    }

    /** Sets a pile just under the size of an entry in a city's research (and keeps the rest), so that one month of researchers in its field finds it. */
    static void pileJustUnder(Game g, Entry e) {
        Research r = g.getResearch();
        Map<String, Double> piles = r.piles();
        piles.put(e.code(), Research.hiddenSize(g.getWorldSeed(), e) - 1e-6);
        r.restore(r.grantsForSave(), r.postsForSave(), piles, r.foundHere());
    }

    /* ==================== 12. the posts are jobs the labour market sees ==================== */

    static void sectionTwelve() {
        System.out.println("\n--- 12. a grant buys posts at the top of the month, and they are among the city's jobs ---");
        Game g = cityWithSchools("discovery-posts", true, false);
        Research r = g.getResearch();
        check("fixture: a city founded the 1900 way with a University standing and no grant", g.foundedUnfitted() && r.isEmpty() && roomOf(g).get(Field.ENGINEERING) > 0);
        play(g, 2);
        long[] nothing = new long[JobType.values().length];
        check("with every grant at 0 a month adds no post: the labour market's jobs are the buildings' jobs, and the city's getJobs() too",
                noPosts(r) && same(r.jobs(), nothing) && same(g.getPopulationManager().getJobs(), g.getBuildingManager().getTotalJobs()) && same(g.getJobs(), g.getBuildingManager().getTotalJobs()));

        EnumMap<Field, Double> room = roomOf(g);
        double[] wages = g.getPopulationManager().getWagesPerType().clone();
        double cost = Research.postCost(Field.ENGINEERING, wages);
        check("fixture: the city's wages give an engineering post a cost, and the University gives room for more than five posts", cost > 0 && room.get(Field.ENGINEERING) > 5);
        double grant = 5.5 * cost;
        check("a grant is refused if it is not an amount, and taken if it is", !r.setGrant(Field.ENGINEERING, -1) && !r.setGrant(Field.ENGINEERING, Double.NaN) && r.setGrant(Field.ENGINEERING, grant));
        play(g, 1);
        int posts = r.posts(Field.ENGINEERING);
        check("one month on, the engineering grant has bought the posts Research.posts() says at the strike's wages: five whole posts (" + posts + "), within the room",
                posts == Research.posts(grant, cost, room.get(Field.ENGINEERING)) && posts == 5 && posts <= room.get(Field.ENGINEERING));
        check("...and no other field has any: no grant there", r.posts(Field.MEDICINE) == 0 && r.posts(Field.BUSINESS) == 0 && r.posts(Field.LAW) == 0);
        long[] expected = plus(g.getBuildingManager().getTotalJobs(), Research.jobs(Field.ENGINEERING, posts));
        check("the labour market's jobs this month are the buildings' jobs plus the posts' (Research.jobs()): " + posts + " posts add " + java.util.Arrays.stream(Research.jobs(Field.ENGINEERING, posts)).sum() + " jobs",
                same(g.getPopulationManager().getJobs(), expected) && same(g.getJobs(), expected) && !same(expected, g.getBuildingManager().getTotalJobs()));
        long[] added = Research.jobs(Field.ENGINEERING, posts);
        check("...the larger half of the researchers in the licence-holding band, the smaller in science, and the support in the college's engineers",
                added[JobType.UNIV_HIGHTECH_ENG.ordinal()] == posts - posts / 2 && added[JobType.UNIV_SCIENCE.ordinal()] == posts / 2
                        && added[JobType.COLLEGE_ENGINEERING.ordinal()] == Math.round(posts * Research.supportPerPost(Field.ENGINEERING)));
        long[] buildingsOnly = g.getBuildingManager().getTotalJobs();
        long[] withPosts = g.getJobs();
        check("the buildings' own figure is not written into: asking the city for its jobs leaves what the buildings count as it was",
                same(g.getBuildingManager().getTotalJobs(), buildingsOnly) && !same(withPosts, buildingsOnly) && same(g.getJobs(), withPosts));

        // A grant past the room buys only the room.
        r.setGrant(Field.ENGINEERING, 1000 * cost);
        double[] wages2 = g.getPopulationManager().getWagesPerType().clone();
        play(g, 1);
        int most = r.posts(Field.ENGINEERING);
        check("a grant far past the room buys the room and no more: " + most + " posts of room for " + String.format("%.1f", room.get(Field.ENGINEERING)),
                most == Research.posts(1000 * cost, Research.postCost(Field.ENGINEERING, wages2), room.get(Field.ENGINEERING)) && most == (int) Math.floor(room.get(Field.ENGINEERING)) && most <= room.get(Field.ENGINEERING));

        // The grant dialled back to nothing: the posts go at the next strike, and the jobs with them.
        r.setGrant(Field.ENGINEERING, 0);
        play(g, 1);
        check("the grant put back to 0 buys no post the next month, and the jobs are the buildings' again",
                noPosts(r) && r.grantsForSave().isEmpty() && same(g.getPopulationManager().getJobs(), g.getBuildingManager().getTotalJobs()));
    }

    /* ==================== 13. the bill ==================== */

    static void sectionThirteen() {
        System.out.println("\n--- 13. the posts' bill is the pay of the filled posts and the labs' other costs, a promise the treasury pays and the audit counts ---");
        Game g = cityWithSchools("discovery-bill", true, true);
        Research r = g.getResearch();
        EconomyManager e = g.getEconomyManager();
        play(g, 2);
        check("fixture: no grant, no post, no bill", noPosts(r) && r.grantsForSave().isEmpty() && e.getResearchPayroll() == 0 && e.getResearchCosts() == 0 && e.getResearchBill() == 0);
        double[] wages = g.getPopulationManager().getWagesPerType().clone();
        r.setGrant(Field.ENGINEERING, 8.5 * Research.postCost(Field.ENGINEERING, wages));
        r.setGrant(Field.LAW, 6.5 * Research.postCost(Field.LAW, wages));
        play(g, 1);
        double[] now = g.getPopulationManager().getWagesPerType(), fill = g.getResearchFill();
        check("fixture: the grants bought posts in two fields", r.posts(Field.ENGINEERING) == 8 && r.posts(Field.LAW) == 6 && fill != null, r.posts(Field.ENGINEERING) + "/" + r.posts(Field.LAW));
        double[] bill = billOf(r, now, fill);
        check("fixture: some of the posts are filled, so the bill is above nothing", bill[0] > 0 && bill[1] > 0, java.util.Arrays.toString(bill));
        near("the month's research payroll is the sum of Research.payroll() over the fields at the month's wages and fill", e.getResearchPayroll(), bill[0], 1e-9 * bill[0]);
        near("...its other costs the sum of Research.bill() less that pay", e.getResearchCosts(), bill[1], 1e-9 * bill[1]);
        near("...and the bill is the two", e.getResearchBill(), bill[0] + bill[1], 1e-9 * (bill[0] + bill[1]));
        double nominal = 0;
        for (Field f : Field.values()) nominal += Research.bill(f, r.posts(f), now, null);
        check("the bill is at most the bill of every post filled: a post nobody fills costs nothing", e.getResearchBill() <= nominal + 1e-9 * nominal);

        check("the treasury's line is a promise, not a purchase, and named for the grants", TreasuryLine.RESEARCH.promise && !TreasuryLine.RESEARCH.purchase && TreasuryLine.RESEARCH.label.equals("Research grants")
                && !TreasuryLine.RESEARCH.accruesArrears() && TreasuryLine.valueOf("RESEARCH") == TreasuryLine.RESEARCH);
        check("...and the last of the lines, so that no saved name moved: every other line is before it", TreasuryLine.values()[TreasuryLine.values().length - 1] == TreasuryLine.RESEARCH);

        MoneyAudit.Result audit = g.getLastMoneyAudit();
        check("the month's money audit lists both research lines and closes: the treasury paid what the audit counted as leaving",
                audit.detail.contains("- research Payroll") && audit.detail.contains("- research Costs") && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved()),
                "residual " + audit.residual + " of " + audit.moved());

        // getExpenses() carries it: take the bill away and the difference is the bill.
        double withBill = e.getExpenses(), payroll = e.getResearchPayroll(), costs = e.getResearchCosts();
        e.setResearch(0, 0);
        double without = e.getExpenses();
        e.setResearch(payroll, costs);
        near("getExpenses() includes the bill: without it the city pays out exactly that much less", withBill - without, payroll + costs, 1e-9 * (payroll + costs));
        NationalAccounts na = e.getNationalAccounts();
        near("the national accounts carry the bill as the government's spending (the month's books were struck from the bill)", na.getResearchSpending(), payroll + costs, 1e-6 * (payroll + costs));
        double totalWith = na.getTotalExpenses(), spending = na.getResearchSpending();
        na.setResearchSpending(0);
        double totalWithout = na.getTotalExpenses();
        na.setResearchSpending(spending);
        near("...and in the government's total expenses: without the line the total is that much less", totalWith - totalWithout, spending, 1e-9 * spending);

        // Setters refuse what is not an amount.
        e.setResearch(Double.NaN, -5);
        check("a bill that is not a number or below nothing is none", e.getResearchPayroll() == 0 && e.getResearchCosts() == 0);
        e.setResearch(payroll, costs);

        // A second month with the grants kept: the bill is struck afresh and the audit closes again.
        play(g, 1);
        MoneyAudit.Result second = g.getLastMoneyAudit();
        check("the next month's audit closes too, with the bill struck again at that month's wages", second.detail.contains("- research Payroll") && Math.abs(second.residual) <= MoneyAudit.tolerance(second.moved()),
                "residual " + second.residual + " of " + second.moved());
    }

    /* ==================== 14. a city founded the default way ==================== */

    static void sectionFourteen() {
        System.out.println("\n--- 14. a city founded the default way: no grant, no change; a grant buys posts that are paid and whose researchers have nothing to work on ---");
        Game g = cityWithSchools("discovery-default", false, true);
        Research r = g.getResearch();
        EconomyManager e = g.getEconomyManager();
        check("fixture: a city founded the default way with a University and a Law School standing", !g.foundedUnfitted() && roomOf(g).get(Field.ENGINEERING) > 0 && roomOf(g).get(Field.LAW) > 0);
        play(g, 1);
        check("with no grant a month adds no job, no bill and no research notice, and the research stays empty",
                r.isEmpty() && same(g.getPopulationManager().getJobs(), g.getBuildingManager().getTotalJobs()) && e.getResearchBill() == 0
                        && noticesKeyed(g, "found:").isEmpty() && noticesKeyed(g, "abroad:").isEmpty() && r.foundHere().isEmpty() && r.piles().isEmpty());
        check("...and its faculty are recorded as researchers who are all idle, with no problem open and nothing found or arrived",
                r.researchers(Field.ENGINEERING) > 0 && r.researchers(Field.LAW) > 0 && r.foundThisMonth().isEmpty() && r.arrivedThisMonth().isEmpty());
        boolean idle = true, none = true;
        for (Field f : Field.values()) {
            idle &= r.idle(f) == r.researchers(f);
            none &= r.open(f) == 0;
        }
        check("...for every field the idle researchers are all the researchers, and no problem is open", idle && none);

        // A month on, before the grants: the labour market's fill is not yet steady in a village's first two months, and the bill is struck off it.
        play(g, 1);
        double[] wages = g.getPopulationManager().getWagesPerType().clone();
        EnumMap<Field, Double> room = roomOf(g);
        double grantE = 6.5 * Research.postCost(Field.ENGINEERING, wages), grantL = 4.5 * Research.postCost(Field.LAW, wages);
        r.setGrant(Field.ENGINEERING, grantE);
        r.setGrant(Field.LAW, grantL);
        play(g, 1);
        double[] now = g.getPopulationManager().getWagesPerType(), fill = g.getResearchFill();
        check("with a grant the posts are hired in a city founded the default way, as the model says: " + r.posts(Field.ENGINEERING) + " engineering and " + r.posts(Field.LAW) + " law, among the city's jobs",
                r.posts(Field.ENGINEERING) == Research.posts(grantE, Research.postCost(Field.ENGINEERING, wages), room.get(Field.ENGINEERING)) && r.posts(Field.ENGINEERING) == 6
                        && r.posts(Field.LAW) == Research.posts(grantL, Research.postCost(Field.LAW, wages), room.get(Field.LAW)) && r.posts(Field.LAW) == 4
                        && same(g.getPopulationManager().getJobs(), plus(g.getBuildingManager().getTotalJobs(), r.jobs())));
        double[] bill = billOf(r, now, fill);
        check("...and they are paid: the bill is the model's, above nothing, and the audit closes",
                bill[0] > 0 && Math.abs(e.getResearchPayroll() - bill[0]) <= 1e-9 * bill[0] && Math.abs(e.getResearchCosts() - bill[1]) <= 1e-9 * bill[1]
                        && Math.abs(g.getLastMoneyAudit().residual) <= MoneyAudit.tolerance(g.getLastMoneyAudit().moved()), "residual " + g.getLastMoneyAudit().residual);
        boolean allIdle = true, noneOpen = true, counted = true;
        for (Field f : Field.values()) {
            allIdle &= r.idle(f) == r.researchers(f);
            noneOpen &= r.open(f) == 0;
            counted &= Math.abs(r.researchers(f) - (Research.faculty(g.researchStaff(fill).getOrDefault(f, 0.0)) + Research.filledResearchers(f, r.posts(f), fill))) <= 1e-9;
        }
        check("every researcher is idle and no problem is open; the researchers are the faculty's 40% of the staffed staff and the filled posts', and nothing is found",
                allIdle && noneOpen && counted && r.foundHere().isEmpty() && r.piles().isEmpty() && r.foundThisMonth().isEmpty() && noticesKeyed(g, "found:").isEmpty());
        play(g, 5);
        check("through to month " + g.getMonth() + ", past the day the world gets its first discovery (month " + CAT.entry("PE2").worldMonth() + "), no notice of one arriving from abroad is raised in a city founded the default way",
                g.getMonth() > CAT.entry("PE2").worldMonth() && noticesKeyed(g, "abroad:").isEmpty() && r.arrivedThisMonth().isEmpty() && r.foundHere().isEmpty());
    }

    /* ==================== 15. the inbox ==================== */

    static void sectionFifteen() {
        System.out.println("\n--- 15. the inbox tells what the city found and what reached it from abroad, once ---");
        Game g = cityWithSchools("discovery-inbox", true, false);
        Research r = g.getResearch();
        int next = g.getMonth() + 1;
        List<Entry> open = r.openProblems(Field.ENGINEERING, next);
        check("fixture: engineering has problems open in the first month a press makes, which the University's faculty work on", open.size() >= 2);
        Entry found = open.get(open.size() - 1);
        // The earliest world date after now that is not the one the city will find: what arrives from abroad.
        Entry arrives = null;
        for (Entry e : CAT.entries()) if (e.worldMonth() > next && !e.code().equals(found.code()) && (arrives == null || e.worldMonth() < arrives.worldMonth())) arrives = e;
        final Entry arrival = arrives;
        check("fixture: a discovery of the world's is dated within the next dozen months, and is not the one found", arrives != null && arrives.worldMonth() <= next + 12);
        check("fixture: neither is in the inbox yet", noticesKeyed(g, "found:").isEmpty() && noticesKeyed(g, "abroad:").isEmpty());
        pileJustUnder(g, found);
        play(g, 1);
        check("fixture: the University's faculty were researchers in engineering that month, and the discovery was found in it", r.researchers(Field.ENGINEERING) > 0 && r.foundThisMonth().contains(found.code()), String.valueOf(r.researchers(Field.ENGINEERING)));
        List<Notice> notices = noticesKeyed(g, "found:");
        check("a find raises one notice in its month, keyed by the code: " + found.code() + " found in month " + g.getMonth(),
                r.foundMonth(found.code()) == g.getMonth() && notices.size() == 1 && notices.get(0).getKey().equals("found:" + found.code()) && notices.get(0).getRaised() == g.getMonth());
        Notice n = notices.isEmpty() ? null : notices.get(0);
        String body = n == null ? "" : String.join(" ", n.getBody());
        double years = (found.worldMonth() - g.getMonth()) / 12.0;
        check("...titled for the discovery, and naming the school that researched it, how far ahead of the world to a tenth of a year, and what it touches",
                n != null && n.getTitle().equals("Found here: " + found.name()) && body.contains(Field.ENGINEERING.school().substring(1)) && body.contains(String.format("%.1f years ahead", years))
                        && body.contains(found.touches()), n == null ? "no notice" : n.getTitle() + " | " + body);
        check("...and the lines of its body are no wider than the inbox's measure", n != null && n.getBody().stream().allMatch(line -> line.length() <= Inbox.BODY_COLUMNS));
        check("nothing from abroad yet: the world's date has not come", noticesKeyed(g, "abroad:").isEmpty());
        play(g, 1);
        List<Notice> later = noticesKeyed(g, "found:");
        check("the month after, the notice is settled and not raised again", later.size() == 1 && later.get(0).isResolved() && r.foundThisMonth().isEmpty());

        // Play to the world's date.
        int to = arrives == null ? 0 : arrives.worldMonth() - g.getMonth();
        if (to > 0) play(g, to);
        List<Notice> abroad = noticesKeyed(g, "abroad:");
        boolean one = arrives != null && g.getMonth() == arrives.worldMonth() && r.foundMonth(arrives.code()) == Integer.MAX_VALUE;
        check("on history's date " + (arrives == null ? "" : arrives.code() + " (month " + arrives.worldMonth() + ")") + " the discovery reaches the city from abroad: one notice, in that month",
                one && abroad.size() == 1 && abroad.get(0).getKey().equals("abroad:" + arrives.code()) && abroad.get(0).getRaised() == g.getMonth() && r.arrivedThisMonth().contains(arrives.code()));
        Notice a = abroad.isEmpty() ? null : abroad.get(0);
        String aboard = a == null ? "" : String.join(" ", a.getBody());
        check("...titled \"From abroad\" with the world's own words and what it touches", a != null && a.getTitle().equals("From abroad: " + arrives.name()) && aboard.contains(arrives.touches())
                && aboard.replace("\n", " ").contains(arrives.world().split(" ")[0]), a == null ? "no notice" : a.getTitle() + " | " + aboard);
        play(g, 1);
        check("...settled the month after, and neither kind is raised twice: still one of each", noticesKeyed(g, "abroad:").stream().filter(x -> x.getKey().equals("abroad:" + arrival.code())).count() == 1
                && noticesKeyed(g, "found:").stream().filter(x -> x.getKey().equals("found:" + found.code())).count() == 1
                && noticesKeyed(g, "abroad:").stream().filter(x -> x.getKey().equals("abroad:" + arrival.code())).allMatch(Notice::isResolved));
        check("the month a city is founded raises none: no research notice dated month 1", g.getInbox().all().stream().filter(x -> x.getKey().startsWith("found:") || x.getKey().startsWith("abroad:")).allMatch(x -> x.getRaised() > 1));
    }

    /* ==================== 16. a law find brings its lever forward ==================== */

    static void sectionSixteen() throws Exception {
        System.out.println("\n--- 16. a law discovery found here brings its Policy lever forward, never before its earliest and never later than it was ---");
        TaxPolicy p = new TaxPolicy();
        PolicyDates.Lever tax = PolicyDates.Lever.WAGE_TAX;
        int history = PolicyDates.openingMonth(tax), earliest = PolicyDates.earliestOpening(tax);
        check("fixture: the income tax's entry is PL3, history opens it in month " + history + ", the earliest a find may bring it is " + earliest + ", and the lookup finds the lever by its key",
                tax.entry().equals("PL3") && PolicyDates.leverOf("PL3") == tax && PolicyDates.leverOf(" PL3 ") == tax && earliest == history - PolicyDates.MAX_EARLY_MONTHS && earliest > 2);
        check("a code that is no lever's key finds none: a blank, an unknown code, a lever with no key (the policy rate) and a discovery of another kind",
                PolicyDates.leverOf("") == null && PolicyDates.leverOf(null) == null && PolicyDates.leverOf("ZZ99") == null && PolicyDates.leverOf("PL1") == null && PolicyDates.leverOf("E1") == null
                        && PolicyDates.Lever.POLICY_RATE.entry().isEmpty());
        check("every lever that carries a key is found by it, and the key is a law discovery of the catalogue",
                java.util.Arrays.stream(PolicyDates.Lever.values()).filter(l -> !l.entry().isEmpty()).allMatch(l -> PolicyDates.leverOf(l.entry()) == l && CAT.entry(l.entry()) != null && CAT.entry(l.entry()).field() == Field.LAW));

        check("a find before the earliest month brings the lever to the earliest and no sooner", p.bringForward(tax, 3) && p.openingMonth(tax) == earliest);
        check("...a second find, in a later month, does not move it later", !p.bringForward(tax, earliest + 5) && p.openingMonth(tax) == earliest);
        TaxPolicy q = new TaxPolicy();
        check("a find in a month between the earliest and history brings the lever to that month", q.bringForward(tax, earliest + 10) && q.openingMonth(tax) == earliest + 10);
        check("...a find in a month after history's, or in one that leaves the lever open, moves nothing", !q.bringForward(tax, history + 30) && !q.bringForward(tax, history) && q.openingMonth(tax) == earliest + 10);
        check("a lever open from founding has nothing to bring forward, and nor does a null lever", !q.bringForward(PolicyDates.Lever.PROPERTY_TAX, 1) && !q.bringForward(null, 1) && q.openingMonth(PolicyDates.Lever.PROPERTY_TAX) == 1);
        TaxPolicy opened = new TaxPolicy();
        opened.openAll();
        check("a lever already open (openAll()) is untouched", !opened.bringForward(tax, 200) && opened.openingMonth(tax) == 1);
        TaxPolicy saved = new TaxPolicy();
        saved.bringForward(tax, earliest + 10);
        TaxPolicy back = new TaxPolicy();
        back.restoreOpenings(saved.openingsForSave());
        check("a lever brought forward is saved with the other openings and comes back", back.openingMonth(tax) == earliest + 10 && back.openingMonth(PolicyDates.Lever.PROFIT_TAX) == PolicyDates.openingMonth(PolicyDates.Lever.PROFIT_TAX));

        // The city: a Law School's faculty find PL3 and PL1 (no lever) in the same month; only the income tax moves. The save is carried to month 156 by its file.
        Game start = cityWithSchools("discovery-lever", true, true);
        play(start, 1);
        GameFiles.Result wrote = EquipmentCheck.save(start, "lever start");
        String json = Files.readString(start.getGameFiles().saveFile(1));
        int month = earliest - 1;
        String moved = json.replaceFirst("\"month\": *\\d+", "\"month\": " + month);
        check("fixture: the city's save carries its month and is carried to month " + month + ", the one before the income tax's earliest", wrote.ok && !moved.equals(json) && moved.contains("\"month\": " + month));
        Files.writeString(start.getGameFiles().saveFile(1), moved);
        Game g = new Game(start.getGameFiles());
        quietly(() -> g.loadGameSave(1));
        check("fixture: the carried city loads in month " + month + " with a Law School and a 1900 founding", g.getLoadFailure() == null && g.getMonth() == month && g.foundedUnfitted(), String.valueOf(g.getLoadFailure()));
        if (g.getLoadFailure() != null || g.getMonth() != month) return;
        // The staff handed again: a loaded village starts its first month with the workforce it had, as the village's own first month does with the staff handed.
        quietly(() -> MaterialsCheck.handWorkers(g));
        Research r = g.getResearch();
        TaxPolicy policy = g.getEconomyManager().getTaxPolicy();
        Entry pl3 = CAT.entry("PL3"), pl1 = CAT.entry("PL1");
        check("fixture: both laws are open to the law faculty next month, and the income tax opens in history's month in this city",
                r.isOpen(pl3, month + 1) && r.isOpen(pl1, month + 1) && g.leverOpeningMonth(tax) == history && !g.isLeverOpen(tax));
        Map<String, Integer> openings = policy.openingsForSave();
        pileJustUnder(g, pl3);
        pileJustUnder(g, pl1);
        play(g, 1);
        check("both are found in month " + g.getMonth() + ", the first month the law faculty could", r.foundMonth("PL3") == month + 1 && r.foundMonth("PL1") == month + 1 && r.foundThisMonth().containsAll(List.of("PL3", "PL1")));
        Map<String, Integer> after = policy.openingsForSave();
        int expected = Math.max(month + 2, earliest);
        check("the income tax now opens in month " + g.leverOpeningMonth(tax) + ", the month after the find (the first the policy hears), and no other lever's opening moved",
                g.leverOpeningMonth(tax) == expected && after.get(tax.name()) == expected && after.entrySet().stream().allMatch(en -> en.getKey().equals(tax.name()) || en.getValue().equals(openings.get(en.getKey()))),
                openings + " -> " + after);
        check("...so it is still shut in the month of the find, the policy having held it at the top of the month",
                !g.isLeverOpen(tax) && g.leverOpeningMonth(tax) == g.getMonth() + 1);
        play(g, 1);
        check("the month after the find the lever is open and its own notice is raised, as for a lever history opens",
                g.isLeverOpen(tax) && g.getMonth() == g.leverOpeningMonth(tax) && noticesKeyed(g, "lever:WAGE_TAX").size() == 1 && noticesKeyed(g, "found:PL3").size() == 1);
    }

    /* ==================== 17. the save ==================== */

    static void sectionSeventeen() throws Exception {
        System.out.println("\n--- 17. the save: a city with research writes it, a load restores it whole and plays on as the city saved; a city with none writes no key ---");
        Game none = cityWithSchools("discovery-save-none", false, false);
        play(none, 2);
        GameFiles.Result wroteNone = EquipmentCheck.save(none, "research none");
        String noneJson = Files.readString(none.getGameFiles().saveFile(1));
        check("a city founded the default way, with a University and no grant, writes no research key: its researchers are idle and pile nothing",
                wroteNone.ok && !noneJson.contains("\"research\"") && none.getResearch().isEmpty());
        Game noneBack = new Game(none.getGameFiles());
        quietly(() -> noneBack.loadGameSave(1));
        check("...a save with no key restores an empty research, even in a Game that held another city's", noneBack.getLoadFailure() == null && noneBack.getResearch().isEmpty());
        Game held = new Game(none.getGameFiles());
        held.getResearch().setGrant(Field.LAW, 10);
        held.getResearch().restore(held.getResearch().grantsForSave(), Map.of("LAW", 3), Map.of("PL1", 1.5), Map.of("PE2", 4));
        held.getEconomyManager().setResearch(7, 9);
        quietly(() -> held.loadGameSave(1));
        check("...and a Game that held a city's research and loads that save keeps none of it: no grant, no post, no pile, no find and no bill",
                held.getLoadFailure() == null && held.getResearch().isEmpty() && held.getEconomyManager().getResearchBill() == 0);

        Game g = cityWithSchools("discovery-save", true, true);
        Research r = g.getResearch();
        EconomyManager e = g.getEconomyManager();
        play(g, 2);
        double[] wages = g.getPopulationManager().getWagesPerType().clone();
        r.setGrant(Field.ENGINEERING, 7.5 * Research.postCost(Field.ENGINEERING, wages));
        r.setGrant(Field.LAW, 5.5 * Research.postCost(Field.LAW, wages));
        List<Entry> open = r.openProblems(Field.ENGINEERING, g.getMonth() + 1);
        Entry found = open.get(open.size() - 1);
        pileJustUnder(g, found);
        play(g, 2);
        check("fixture: the city has grants, posts, work piled and a find, and a bill",
                r.grant(Field.ENGINEERING) > 0 && r.posts(Field.ENGINEERING) > 0 && r.posts(Field.LAW) > 0 && !r.piles().isEmpty() && r.foundHere().containsKey(found.code()) && e.getResearchBill() > 0,
                r.postsForSave() + " " + r.piles().keySet() + " " + r.foundHere());
        GameFiles.Result wrote = EquipmentCheck.save(g, "research kept");
        check("the city is saved", wrote.ok, wrote.message());
        String json = Files.readString(g.getGameFiles().saveFile(1));
        check("...with a research key, keyed by field name and by code", json.contains("\"research\"") && json.contains("\"ENGINEERING\"") && json.contains("\"" + found.code() + "\""));
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(1));
        Research b = back.getResearch();
        check("loaded without a failure", back.getLoadFailure() == null, String.valueOf(back.getLoadFailure()));
        check("the grants, the posts, the piles and the finds (in their order) come back equal",
                b.grantsForSave().equals(r.grantsForSave()) && b.postsForSave().equals(r.postsForSave()) && b.piles().equals(r.piles()) && b.foundHere().equals(r.foundHere())
                        && new ArrayList<>(b.foundHere().keySet()).equals(new ArrayList<>(r.foundHere().keySet())));
        check("...the loaded city's jobs include its posts, and equal the saved city's", same(back.getJobs(), g.getJobs()) && same(back.getPopulationManager().getJobs(), g.getPopulationManager().getJobs())
                && same(back.getJobs(), plus(back.getBuildingManager().getTotalJobs(), b.jobs())));
        near("...and the month's bill comes back: payroll and costs", back.getEconomyManager().getResearchPayroll(), e.getResearchPayroll(), 1e-9 * e.getResearchPayroll());
        near("...the costs too", back.getEconomyManager().getResearchCosts(), e.getResearchCosts(), 1e-9 * e.getResearchCosts());
        near("...and the national accounts' spending on it", back.getEconomyManager().getNationalAccounts().getResearchSpending(), e.getNationalAccounts().getResearchSpending(), 1e-9 * e.getResearchBill());
        check("a find known before the save is known after it: the discovery's code is among the finds and is known from its month", back.getResearch().known(found.code(), back.getMonth()) && back.getResearch().foundMonth(found.code()) == r.foundMonth(found.code()));
        play(g, 1);
        play(back, 1);
        check("one month played on the saved city and on the loaded one gives the same piles and the same finds",
                b.piles().keySet().equals(r.piles().keySet()) && b.foundHere().equals(r.foundHere()), r.piles() + " vs " + b.piles());
        boolean closePiles = true;
        for (Map.Entry<String, Double> en : r.piles().entrySet()) closePiles &= Math.abs(en.getValue() - b.piles().getOrDefault(en.getKey(), -1.0)) <= 1e-9 * Math.max(1, en.getValue());
        check("...to the piles' last digits, and the same posts", closePiles && b.postsForSave().equals(r.postsForSave()), r.piles() + " vs " + b.piles());
        near("...and the same research bill", back.getEconomyManager().getResearchBill(), e.getResearchBill(), 1e-9 * e.getResearchBill());
        check("...and the same jobs", same(back.getJobs(), g.getJobs()));
    }

    /* ==================== 18. a reform of the money ==================== */

    static void sectionEighteen() {
        System.out.println("\n--- 18. a reform of the money scales the grants and the bill, and nothing else of the research ---");
        Game g = cityWithSchools("discovery-reform", true, false);
        Research r = g.getResearch();
        EconomyManager e = g.getEconomyManager();
        play(g, 2);
        double[] wages = g.getPopulationManager().getWagesPerType().clone();
        r.setGrant(Field.ENGINEERING, 5.5 * Research.postCost(Field.ENGINEERING, wages));
        r.setGrant(Field.MEDICINE, 3.5 * Research.postCost(Field.MEDICINE, wages));
        List<Entry> open = r.openProblems(Field.ENGINEERING, g.getMonth() + 1);
        pileJustUnder(g, open.get(open.size() - 1));
        play(g, 2);
        double grantE = r.grant(Field.ENGINEERING), grantM = r.grant(Field.MEDICINE), bill = e.getResearchBill(), payroll = e.getResearchPayroll(), spending = e.getNationalAccounts().getResearchSpending();
        Map<String, Integer> posts = r.postsForSave(), found = r.foundHere();
        Map<String, Double> piles = r.piles();
        check("fixture: grants in two fields, posts, work piled, a find and a bill", grantE > 0 && grantM > 0 && !posts.isEmpty() && !piles.isEmpty() && !found.isEmpty() && bill > 0 && spending > 0);
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = g.reformCurrencyForTest(100));
        double scale = e.getResearchBill() / bill;
        check("fixture: the reform went through and moved the money: the research bill is " + scale + " of what it was", ok[0] && scale != 1 && scale > 0);
        near("the grants are scaled as the bill is: the engineering grant", r.grant(Field.ENGINEERING), grantE * scale, 1e-9 * grantE * scale);
        near("...and the medical grant", r.grant(Field.MEDICINE), grantM * scale, 1e-9 * grantM * scale);
        near("...the pay of the posts too", e.getResearchPayroll(), payroll * scale, 1e-9 * payroll * scale);
        near("...and the national accounts' spending on them", e.getNationalAccounts().getResearchSpending(), spending * scale, 1e-9 * spending * scale);
        check("the posts, the piles and the finds are not money and are as they were", r.postsForSave().equals(posts) && r.piles().equals(piles) && r.foundHere().equals(found));
        check("a grant of 0 stays 0 and the other fields stay without one", r.grant(Field.LAW) == 0 && r.grant(Field.BUSINESS) == 0);
    }
}
