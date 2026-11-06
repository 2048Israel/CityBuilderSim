package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The city's research (R3): the grants the player sets, the posts they buy, the researchers who fill them, the work piled on each open problem, the
 * discoveries found here and the month's pure step that moves the work on (docs/studies/research-design.md 1.8, 5.1, 5.2, 6.3).
 *
 * WHY. Before R3 a discovery came only on history's date (ResearchCatalog, R2): the tractor in June 1917 whatever a city did, and nothing a school
 * or a grant could change. Jerus's brief (2026-10-18, "rec all"): money buys research POSTS at the schools, posts are jobs the labour market
 * fills, and the researchers, the schools' own faculty at 40% of their time and whoever fills a post, spread evenly over the field's OPEN
 * problems, each with a fixed amount of work in it. Nothing multiplies a chance. This class is that mechanism with no screen and no Game in
 * it, as research_model.py (the study's model) is, so a harness can feed it figures: every rule is a static function of its arguments, and the
 * instance holds only what a city adds to the catalogue. The seam to the rest of the game is Recipe, Equipment and BuildingsTemplate
 * knownAt(month, Research): a discovery found here is known in this city from the month it was found, and the shelf of history's dates is
 * otherwise unchanged. In a city with no researcher and no grant nothing is ever found here, so every reading equals history's.
 *
 * The month (month()) mirrors the inner loop of research_model.py run(): the world's own dates first (a discovery history has dated this month,
 * or earlier, that the city has not found is the city's from abroad, and the work piled on it stops); then, field by field, the problems that
 * are OPEN (not known; its science exists, which is history's month less the field's lag; every code it needs known, here or abroad), the
 * field's researchers spread over them evenly with none past its team cap, each pile gaining its researchers over twelve researcher-years a month,
 * and a pile that reaches the problem's HIDDEN SIZE is found here that month. The size is drawn once from the city's seed and the code
 * (hiddenSize()), is a fact about the city and is never saved. Not here: calling the step from the month, striking posts into the labour market,
 * paying for them, the save, the inbox (the next slice; this class gives it the getters and restore() it saves through).
 */
public final class Research {

    /* =====================================================================
       THE DIALS (research-design 1.8). Each is the study's, and (est.) where the study says so.
       ===================================================================== */

    /** The share of their time a school's university-band staff spend on research that no grant pays for: 40 of the 40/40/20 teaching, research and service split of a collective agreement [UVIC]. */
    public static final double FACULTY_SHARE = 0.40;

    /** The posts a school has room for, per university-band staff member: NSERC's 2.7 trainees to each funded researcher [NSERC-DRR] (est.). */
    public static final double POSTS_PER_STAFF = 2.7;

    /** The spread of the hidden size, the sigma of its lognormal (mean 1): a problem's size is its work times a draw, and 0.6 makes the draw run from about a third to about three times it at two standard deviations (est.). */
    public static final double SIZE_SIGMA = 0.6;

    /** The support staff a laboratory post carries, college-band, for each researcher: 0.43, Canada's researchers being 0.70 of its R&D personnel [TOE], [MSTI]. */
    public static final double SUPPORT_LAB = 0.43;

    /** The support staff a post with no laboratory carries, diploma-band, for each researcher (est.). */
    public static final double SUPPORT_NO_LABS = 0.20;

    /** The share of an engineering lab's running cost that is pay, so a post costs its pay over this: 62%, labour being about 60 to 67% of R&D cost [TOE] (est.). */
    public static final double LABOUR_SHARE_ENGINEERING = 0.62;

    /** The share of a medical lab's running cost that is pay: 60% (est.). */
    public static final double LABOUR_SHARE_MEDICINE = 0.60;

    /** The share of business and law research that is pay, there being no labs: 80% (est.). */
    public static final double LABOUR_SHARE_NO_LABS = 0.80;

    /** How many years before history's date engineering science exists: 7, academic finding to product [MANS91]. */
    public static final int LAG_YEARS_ENGINEERING = 7;

    /** How many years before history's date medical science exists: 9, drugs 8.8 [MANS91]. */
    public static final int LAG_YEARS_MEDICINE = 9;

    /** How many years before history's date business science exists: 4 (est.; [MANS91] has none for it). */
    public static final int LAG_YEARS_BUSINESS = 4;

    /** How many years before history's date legal science exists: 4, and so many months as a law lever may be brought forward (PolicyDates.MAX_EARLY_MONTHS), which DiscoveryCheck holds equal. */
    public static final int LAG_YEARS_LAW = 4;

    /** The fewest years of work a building takes, however many work on it: 3 [DIMASI] (est.). */
    public static final int MIN_YEARS_BUILDING = 3;

    /** The fewest years of work a piece of equipment takes: 3, as a building (est.). */
    public static final int MIN_YEARS_EQUIPMENT = 3;

    /** The fewest years of work a recipe takes: 2 (est.). */
    public static final int MIN_YEARS_RECIPE = 2;

    /** The fewest years of work a drug takes: 8, its clinical phases being sequential [DIMASI], from the year of the rule. */
    public static final int MIN_YEARS_DRUG = 8;

    /** The year a drug first had to pass the efficacy trials, the Kefauver-Harris amendments: 1962 (research-1900 Q6). */
    public static final int DRUG_RULE_YEAR = 1962;

    /** The fewest years of work a drug dated before DRUG_RULE_YEAR takes: 2, there being no phased trials to wait on (research-1900 Q6, decided). */
    public static final int MIN_YEARS_DRUG_BEFORE_RULE = 2;

    /** Months in a year: a pile gains its researchers over this many each month. */
    static final int MONTHS_A_YEAR = 12;

    /** The smallest number of researchers left over that the water fill counts as idle (the study's 1e-9). */
    static final double IDLE_DUST = 1e-9;

    /* =====================================================================
       THE FIELD: who does it, what a post is, how far ahead of the world (pure)
       ===================================================================== */

    /** The first of a field's two researcher job types, the larger half of a post: the licence-holding band (the high-tech engineer, the doctor, the finance officer, the lawyer). */
    public static JobType researcherA(Field f) {
        return switch (f) {
            case ENGINEERING -> JobType.UNIV_HIGHTECH_ENG;
            case MEDICINE -> JobType.UNIV_DOCTOR;
            case BUSINESS -> JobType.UNIV_FINANCE;
            case LAW -> JobType.UNIV_LAW;
        };
    }

    /** The second researcher job type of a field, the other half of a post: the ungated university band (science for the labs, policy for business and law). */
    public static JobType researcherB(Field f) {
        return switch (f) {
            case ENGINEERING, MEDICINE -> JobType.UNIV_SCIENCE;
            case BUSINESS, LAW -> JobType.UNIV_POLICY;
        };
    }

    /** The job type of a field's support staff: college engineering and college health for the labs, a diploma for business and law. */
    public static JobType supportType(Field f) {
        return switch (f) {
            case ENGINEERING -> JobType.COLLEGE_ENGINEERING;
            case MEDICINE -> JobType.COLLEGE_HEALTH;
            case BUSINESS, LAW -> JobType.DIPLOMA;
        };
    }

    /** Support staff a post carries for each researcher: SUPPORT_LAB in the labs, SUPPORT_NO_LABS in business and law. */
    public static double supportPerPost(Field f) {
        return switch (f) {
            case ENGINEERING, MEDICINE -> SUPPORT_LAB;
            case BUSINESS, LAW -> SUPPORT_NO_LABS;
        };
    }

    /** The share of a post's all-in cost that is pay. */
    public static double labourShare(Field f) {
        return switch (f) {
            case ENGINEERING -> LABOUR_SHARE_ENGINEERING;
            case MEDICINE -> LABOUR_SHARE_MEDICINE;
            case BUSINESS, LAW -> LABOUR_SHARE_NO_LABS;
        };
    }

    /** Years before history's date the science of a field's discoveries exists. */
    public static int lagYears(Field f) {
        return switch (f) {
            case ENGINEERING -> LAG_YEARS_ENGINEERING;
            case MEDICINE -> LAG_YEARS_MEDICINE;
            case BUSINESS -> LAG_YEARS_BUSINESS;
            case LAW -> LAG_YEARS_LAW;
        };
    }

    /** Months before history's date the science of a field's discoveries exists: lagYears() of twelve. */
    public static int lagMonths(Field f) {
        return MONTHS_A_YEAR * lagYears(f);
    }

    /** The fewest years of work a discovery takes, however many work on it: its size's (a drug dated before DRUG_RULE_YEAR the shorter). */
    public static double minYears(Entry e) {
        return switch (e.size()) {
            case BUILDING -> MIN_YEARS_BUILDING;
            case EQUIPMENT -> MIN_YEARS_EQUIPMENT;
            case RECIPE -> MIN_YEARS_RECIPE;
            case DRUG -> e.year() < DRUG_RULE_YEAR ? MIN_YEARS_DRUG_BEFORE_RULE : MIN_YEARS_DRUG;
        };
    }

    /** The most researchers that can work on a discovery at once: its work over its fewest years (a drug cannot be found in less than 8 years, however many work on it). */
    public static double teamCap(Entry e) {
        return e.work() / minYears(e);
    }

    /**
     * A discovery's size for THIS city, in researcher-years: its work times a lognormal draw (sigma SIZE_SIGMA, mean 1) from the city's seed and the
     * discovery's code, by World.mix() (the SplitMix64 finaliser the world uses) and Box-Muller, exactly as research_model.py hidden_size() does.
     * A fact about the city, drawn each time from the same two numbers, so a reload draws the same size and nothing about it is saved.
     */
    public static double hiddenSize(long seed, Entry e) {
        long id = e.code().hashCode();
        long h1 = World.mix(seed ^ (id * 0x9E3779B97F4A7C15L));
        long h2 = World.mix(seed ^ (id * 0xC2B2AE3D27D4EB4FL) ^ 1);
        double u1 = ((h1 >>> 11) + 0.5) * 0x1.0p-53, u2 = ((h2 >>> 11) + 0.5) * 0x1.0p-53;
        double z = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        return e.work() * Math.exp(SIZE_SIGMA * z - SIZE_SIGMA * SIZE_SIGMA / 2);
    }

    /* =====================================================================
       THE POST: its cost, its jobs and its bill (pure)
       ===================================================================== */

    /**
     * What one post costs a month, all in: half the pay of each researcher job type and the support staff a post carries at theirs, over the labour
     * share. `wagePerType` is indexed by JobType.ordinal(), in the game's money unit (PopulationManager.getWagesPerType()). NaN for a wage that is
     * not there or not a number, and for a result that is not above nothing.
     */
    public static double postCost(Field f, double[] wagePerType) {
        if (wagePerType == null) return Double.NaN;
        int a = researcherA(f).ordinal(), b = researcherB(f).ordinal(), s = supportType(f).ordinal();
        if (a >= wagePerType.length || b >= wagePerType.length || s >= wagePerType.length) return Double.NaN;
        double cost = (0.5 * wagePerType[a] + 0.5 * wagePerType[b] + supportPerPost(f) * wagePerType[s]) / labourShare(f);
        return Double.isFinite(cost) && cost > 0 ? cost : Double.NaN;
    }

    /** Whole posts a grant buys: the grant over a post's cost, never more than the room there is; none for a grant that is not above nothing, a cost that is not a number above nothing, or no room. */
    public static int posts(double grant, double cost, double room) {
        if (!(grant > 0) || !(cost > 0) || !Double.isFinite(cost) || !(room > 0)) return 0;
        double n = Math.min(room, grant / cost);
        return n >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.floor(n);
    }

    /**
     * The jobs `posts` posts of a field are, by JobType ordinal: the first researcher type takes the larger half (posts less posts / 2), the
     * second the smaller (posts / 2, whole numbers), and the support staff are the support per post of them, rounded.
     */
    public static long[] jobs(Field f, int posts) {
        long[] out = new long[JobType.values().length];
        if (posts <= 0) return out;
        out[researcherA(f).ordinal()] += posts - posts / 2;
        out[researcherB(f).ordinal()] += posts / 2;
        out[supportType(f).ordinal()] += Math.round(posts * supportPerPost(f));
        return out;
    }

    /** The fill of a job type: its entry in the array, or 1 where there is none (BuildingManager.getCategoryPayroll() reads a missing fill as full). */
    private static double fillOf(double[] fillPerType, int type) {
        return fillPerType != null && type < fillPerType.length ? fillPerType[type] : 1;
    }

    /** The pay of the posts, as BuildingManager.getCategoryPayroll() sums it: each job type's jobs times its wage times its fill. Nothing for no wages. */
    public static double payroll(Field f, int posts, double[] wagePerType, double[] fillPerType) {
        if (wagePerType == null) return 0;
        long[] jobs = jobs(f, posts);
        double pay = 0;
        for (int i = 0; i < jobs.length && i < wagePerType.length; i++) pay += jobs[i] * wagePerType[i] * fillOf(fillPerType, i);
        return pay;
    }

    /** What the filled posts cost the city, all in: their pay over the labour share. A post nobody fills costs nothing. */
    public static double bill(Field f, int posts, double[] wagePerType, double[] fillPerType) {
        return payroll(f, posts, wagePerType, fillPerType) / labourShare(f);
    }

    /** The researchers the posts give: the researcher jobs (not the support staff) times their fill. */
    public static double filledResearchers(Field f, int posts, double[] fillPerType) {
        long[] jobs = jobs(f, posts);
        int a = researcherA(f).ordinal(), b = researcherB(f).ordinal();
        return jobs[a] * fillOf(fillPerType, a) + jobs[b] * fillOf(fillPerType, b);
    }

    /* =====================================================================
       THE SCHOOLS: whose staff research, and the room for posts (pure)
       ===================================================================== */

    /** The fields a university-band job type serves (science serves two, policy two); none for any other job type. */
    public static List<Field> servedBy(JobType type) {
        return switch (type) {
            case UNIV_SCIENCE -> List.of(Field.ENGINEERING, Field.MEDICINE);
            case UNIV_POLICY -> List.of(Field.BUSINESS, Field.LAW);
            case UNIV_HIGHTECH_ENG -> List.of(Field.ENGINEERING);
            case UNIV_DOCTOR -> List.of(Field.MEDICINE);
            case UNIV_FINANCE -> List.of(Field.BUSINESS);
            case UNIV_LAW -> List.of(Field.LAW);
            default -> List.of();
        };
    }

    /**
     * A school's university-band staff by field. A University shares each university job type's staff equally over the fields that job type
     * serves (22 science to engineering and 22 to medicine, 6 policy to business and 6 to law, for buildings.json's 44 and 12); the Institute of
     * technology, the Medical, Business and Law schools give all of theirs to their one field; every other school hosts none (an empty map).
     * `univStaffByJobType` is indexed by JobType.ordinal(); only the university-band types are read, and a missing array is no staff.
     */
    public static EnumMap<Field, Double> staffByField(EducationType teaches, double[] univStaffByJobType) {
        EnumMap<Field, Double> out = new EnumMap<>(Field.class);
        if (teaches == null) return out;
        Field only = switch (teaches) {
            case ENGINEERING -> Field.ENGINEERING;
            case MEDICAL -> Field.MEDICINE;
            case BUSINESS -> Field.BUSINESS;
            case LAW -> Field.LAW;
            default -> null;
        };
        if (only == null && teaches != EducationType.UNIVERSITY) return out;
        for (Field f : Field.values()) if (only == null || f == only) out.put(f, 0.0);
        for (JobType type : JobType.values()) {
            List<Field> served = servedBy(type);
            if (served.isEmpty()) continue;
            double staff = univStaffByJobType != null && type.ordinal() < univStaffByJobType.length ? univStaffByJobType[type.ordinal()] : 0;
            if (!Double.isFinite(staff) || staff <= 0) continue;
            if (only != null) {
                out.merge(only, staff, Double::sum);
            } else {
                for (Field f : served) out.merge(f, staff / served.size(), Double::sum);
            }
        }
        return out;
    }

    /** The researcher-FTE the faculty give for nothing: FACULTY_SHARE of the university-band staff a school has in a field. */
    public static double faculty(double staff) {
        return FACULTY_SHARE * staff;
    }

    /** The posts a school has room for in a field: POSTS_PER_STAFF of its university-band staff there. */
    public static double room(double staff) {
        return POSTS_PER_STAFF * staff;
    }

    /* =====================================================================
       THE WATER FILL: researchers spread over the open problems (pure)
       ===================================================================== */

    /** How a field's researchers spread: each problem's share of them, in the order of the caps given, and the researchers left idle when every problem is full. */
    public record Fill(double[] share, double idle) { }

    /**
     * Spreads `people` researchers evenly over the problems, none past its cap, the spare spilling to the rest (research_model.py water_fill()): an
     * even share to each problem still live, the problems that cannot take a whole share filled to their cap and out, and what they did not take
     * shared again, until all is placed or every problem is full, and the remainder is idle. A cap that is not above nothing takes none. The
     * shares add to the people less the idle.
     */
    public static Fill waterFill(double people, double[] caps) {
        int n = caps == null ? 0 : caps.length;
        double[] share = new double[n], cap = new double[n];
        boolean[] live = new boolean[n];
        for (int i = 0; i < n; i++) {
            cap[i] = Double.isNaN(caps[i]) || caps[i] < 0 ? 0 : caps[i];
            live[i] = true;
        }
        int alive = n;
        double left = Double.isFinite(people) && people > 0 ? people : 0;
        while (left > IDLE_DUST && alive > 0) {
            double each = left / alive;
            boolean[] done = new boolean[n];
            boolean any = false;
            for (int i = 0; i < n; i++) {
                if (live[i] && cap[i] - share[i] <= each) {
                    done[i] = true;
                    any = true;
                }
            }
            if (!any) {
                for (int i = 0; i < n; i++) if (live[i]) share[i] += each;
                left = 0;
                break;
            }
            for (int i = 0; i < n; i++) {
                if (!done[i]) continue;
                left -= cap[i] - share[i];
                share[i] = cap[i];
                live[i] = false;
                alive--;
            }
        }
        return new Fill(share, left > IDLE_DUST ? left : 0);
    }

    /* =====================================================================
       THE CITY'S RESEARCH: what a city adds to the catalogue
       ===================================================================== */

    /** The grant dials, a month's money for each field's posts, in the game's money unit; a field with none is 0. */
    private final EnumMap<Field, Double> grants = new EnumMap<>(Field.class);

    /** The posts struck this month for each field (strikePosts()): what the grants bought within the room. */
    private final EnumMap<Field, Integer> posts = new EnumMap<>(Field.class);

    /** The work piled on each problem worked on and not yet known, in researcher-years, by code. */
    private final Map<String, Double> piles = new TreeMap<>();

    /** The discoveries found here and the month each was found, in the order found. */
    private final Map<String, Integer> found = new LinkedHashMap<>();

    /** This month's events (month()): the codes found here, the codes that reached the city from abroad, and each field's researchers, idle researchers and open problems. */
    private final List<String> foundThisMonth = new ArrayList<>();
    private final List<String> arrivedThisMonth = new ArrayList<>();
    private final EnumMap<Field, Double> researchersNow = new EnumMap<>(Field.class);
    private final EnumMap<Field, Double> idleNow = new EnumMap<>(Field.class);
    private final EnumMap<Field, Integer> openNow = new EnumMap<>(Field.class);

    /** The hidden sizes drawn so far for one seed, so that a month draws each once. Never saved: hiddenSize() draws it again from the seed and the code. */
    private final Map<String, Double> sizes = new HashMap<>();
    private long sizesSeed;

    /* ----- the grants ----- */

    /** A field's grant, a month's money: 0 where none is set. */
    public double grant(Field f) {
        return grants.getOrDefault(f, 0.0);
    }

    /** Sets a field's grant. A negative amount or one that is not a number is refused: the grant stays as it was, and false is returned. */
    public boolean setGrant(Field f, double amount) {
        if (f == null || !Double.isFinite(amount) || amount < 0) return false;
        grants.put(f, amount);
        return true;
    }

    /* ----- the posts ----- */

    /** The posts struck this month for a field (strikePosts()); 0 before any. */
    public int posts(Field f) {
        return posts.getOrDefault(f, 0);
    }

    /**
     * Strikes the month's posts: for each field the whole posts its grant buys at the post's cost (postCost() at these wages) within the room it
     * has (posts()). `room` is the posts the field's schools have room for; a field missing from it has none.
     */
    public void strikePosts(EnumMap<Field, Double> room, double[] wagePerType) {
        for (Field f : Field.values()) {
            Double r = room == null ? null : room.get(f);
            posts.put(f, posts(grant(f), postCost(f, wagePerType), r == null ? 0 : r));
        }
    }

    /** The jobs the month's posts are, all fields together, by JobType ordinal (jobs() of each field's posts). */
    public long[] jobs() {
        long[] out = new long[JobType.values().length];
        for (Field f : Field.values()) {
            long[] one = jobs(f, posts(f));
            for (int i = 0; i < out.length; i++) out[i] += one[i];
        }
        return out;
    }

    /* ----- what is known ----- */

    /** The month a code was found here, or Integer.MAX_VALUE for one that was not (a year, a blank and a code the city has not found are all that). */
    public int foundMonth(String code) {
        Integer month = code == null ? null : found.get(code.trim());
        return month == null ? Integer.MAX_VALUE : month;
    }

    /** Whether the city found a discovery itself, before history's date. */
    public boolean foundHere(String code) {
        return foundMonth(code) != Integer.MAX_VALUE;
    }

    /**
     * The game month a "since" is known from in this city: history's (ResearchCatalog.knownFrom(): a year's January, a code's entry's month, never
     * for a blank or a code the catalogue lacks) lowered to the month the city found the code, where it did.
     */
    public int knownFrom(String since) {
        return Math.min(ResearchCatalog.knownFrom(since), foundMonth(since));
    }

    /** Whether a "since" is known in this city at a game month: from history's month, or from the month it was found here, whichever is first. */
    public boolean known(String code, int month) {
        return month >= knownFrom(code);
    }

    /** Whether a discovery is open to its field's researchers in a month: not known, its science exists (history's month less the field's lag) and every code it needs known (here or abroad). */
    public boolean isOpen(Entry e, int month) {
        if (known(e.code(), month) || month < e.worldMonth() - lagMonths(e.field())) return false;
        for (String need : e.needs()) if (!known(need, month)) return false;
        return true;
    }

    /** A field's open problems in a month, in the catalogue's order. */
    public List<Entry> openProblems(Field f, int month) {
        List<Entry> open = new ArrayList<>();
        for (Entry e : ResearchCatalog.get().entries()) if (e.field() == f && isOpen(e, month)) open.add(e);
        return open;
    }

    /* ----- the month ----- */

    /** The size of a discovery in this city, drawn once for a seed (hiddenSize()). */
    private double sizeOf(long seed, Entry e) {
        if (seed != sizesSeed) {
            sizes.clear();
            sizesSeed = seed;
        }
        return sizes.computeIfAbsent(e.code(), c -> hiddenSize(seed, e));
    }

    /** A researcher count made safe to add: what is not a number or is below nothing is none. */
    private static double sane(Double people) {
        return people != null && Double.isFinite(people) && people > 0 ? people : 0;
    }

    /**
     * The month's step (research_model.py run(), one month). First the world's own dates: a discovery history dates in this month or before that
     * the city has not found is the city's from abroad, the work on it stops (its pile is dropped), and one dated this month is listed as arrived.
     * Then each field in turn (the enum's order): its open problems, its researchers (`researchers`, a field missing from it has none) spread over
     * them (waterFill() over the team caps), each pile gaining its share over twelve researcher-years, and a pile that has reached the problem's
     * hiddenSize() found here this month. A find counts at once for the fields after it, as in the model, and for the same field the next month.
     * Idle researchers, open problems and the finds and arrivals are kept for the month (idle(), openProblems(), foundThisMonth(), arrivedThisMonth()).
     */
    public void month(int month, long seed, EnumMap<Field, Double> researchers) {
        foundThisMonth.clear();
        arrivedThisMonth.clear();
        researchersNow.clear();
        idleNow.clear();
        openNow.clear();

        for (Entry e : ResearchCatalog.get().entries()) {
            if (e.worldMonth() > month || found.containsKey(e.code())) continue;
            piles.remove(e.code());
            if (e.worldMonth() == month) arrivedThisMonth.add(e.code());
        }

        for (Field f : Field.values()) {
            double people = sane(researchers == null ? null : researchers.get(f));
            List<Entry> open = openProblems(f, month);
            researchersNow.put(f, people);
            openNow.put(f, open.size());
            if (open.isEmpty()) {
                idleNow.put(f, people);
                continue;
            }
            double[] caps = new double[open.size()];
            for (int i = 0; i < caps.length; i++) caps[i] = teamCap(open.get(i));
            Fill fill = waterFill(people, caps);
            idleNow.put(f, fill.idle());
            for (int i = 0; i < caps.length; i++) {
                double add = fill.share()[i] / MONTHS_A_YEAR;
                if (!(add > 0)) continue;
                Entry e = open.get(i);
                double pile = piles.merge(e.code(), add, Double::sum);
                if (pile >= sizeOf(seed, e)) {
                    found.put(e.code(), month);
                    piles.remove(e.code());
                    foundThisMonth.add(e.code());
                }
            }
        }
    }

    /** The codes found here this month (month()), in the order found. Unmodifiable. */
    public List<String> foundThisMonth() {
        return Collections.unmodifiableList(foundThisMonth);
    }

    /** The codes that reached the city from abroad this month: history dated them this month and the city had not found them. Unmodifiable. */
    public List<String> arrivedThisMonth() {
        return Collections.unmodifiableList(arrivedThisMonth);
    }

    /** A field's researchers this month, as month() was given them. */
    public double researchers(Field f) {
        return researchersNow.getOrDefault(f, 0.0);
    }

    /** A field's researchers this month that had no problem to work on: all of them when none was open, else the remainder of the water fill. */
    public double idle(Field f) {
        return idleNow.getOrDefault(f, 0.0);
    }

    /** How many problems were open to a field this month. */
    public int open(Field f) {
        return openNow.getOrDefault(f, 0);
    }

    /* ----- what is kept ----- */

    /** The work piled on a problem, in researcher-years: 0 where none is worked on or it is known. */
    public double pile(String code) {
        return piles.getOrDefault(code, 0.0);
    }

    /** The piles by code: a fresh map. */
    public Map<String, Double> piles() {
        return new TreeMap<>(piles);
    }

    /** The discoveries found here by code and the month each was found, in the order found: a fresh map. */
    public Map<String, Integer> foundHere() {
        return new LinkedHashMap<>(found);
    }

    /** Whether the city has nothing of its own: no grant, no post, no work piled and nothing found (a city founded the default way, always). */
    public boolean isEmpty() {
        return grants.values().stream().allMatch(g -> g == 0) && posts.values().stream().allMatch(p -> p == 0) && piles.isEmpty() && found.isEmpty();
    }

    /** The grants to save, by Field.name(); only those above nothing. A fresh map. */
    public Map<String, Double> grantsForSave() {
        Map<String, Double> out = new LinkedHashMap<>();
        for (Field f : Field.values()) if (grant(f) > 0) out.put(f.name(), grant(f));
        return out;
    }

    /** The posts to save, by Field.name(); only those above nothing. A fresh map. */
    public Map<String, Integer> postsForSave() {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (Field f : Field.values()) if (posts(f) > 0) out.put(f.name(), posts(f));
        return out;
    }

    /**
     * Puts the city's research back, replacing all (the load path), keyed by Field.name() and by code and never by an ordinal. An entry that is
     * malformed is skipped: a field that is not one, an amount not finite or below nothing, a pile or a find for a code the catalogue lacks, a month
     * before the first. This month's events are cleared; the sizes are drawn again from the seed.
     */
    public void restore(Map<String, Double> grants, Map<String, Integer> posts, Map<String, Double> piles, Map<String, Integer> found) {
        reset();
        if (grants != null) {
            for (Map.Entry<String, Double> e : grants.entrySet()) {
                Field f = fieldOf(e.getKey());
                if (f != null && e.getValue() != null) setGrant(f, e.getValue());
            }
        }
        if (posts != null) {
            for (Map.Entry<String, Integer> e : posts.entrySet()) {
                Field f = fieldOf(e.getKey());
                if (f != null && e.getValue() != null && e.getValue() > 0) this.posts.put(f, e.getValue());
            }
        }
        ResearchCatalog catalogue = ResearchCatalog.get();
        if (piles != null) {
            for (Map.Entry<String, Double> e : piles.entrySet()) {
                if (e.getKey() == null || e.getValue() == null || catalogue.entry(e.getKey()) == null) continue;
                if (Double.isFinite(e.getValue()) && e.getValue() > 0) this.piles.put(e.getKey(), e.getValue());
            }
        }
        if (found != null) {
            for (Map.Entry<String, Integer> e : found.entrySet()) {
                if (e.getKey() == null || e.getValue() == null || catalogue.entry(e.getKey()) == null || e.getValue() < 1) continue;
                this.found.put(e.getKey(), e.getValue());
            }
        }
    }

    private static Field fieldOf(String name) {
        try {
            return name == null ? null : Field.valueOf(name);
        } catch (IllegalArgumentException notAField) {
            return null;
        }
    }

    /** A new game: no grant, no post, no work piled, nothing found and no event (Game.buildWorld()). */
    public void reset() {
        grants.clear();
        posts.clear();
        piles.clear();
        found.clear();
        foundThisMonth.clear();
        arrivedThisMonth.clear();
        researchersNow.clear();
        idleNow.clear();
        openNow.clear();
        sizes.clear();
        sizesSeed = 0;
    }
}
