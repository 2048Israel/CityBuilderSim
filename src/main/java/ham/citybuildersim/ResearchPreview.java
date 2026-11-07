package ham.citybuildersim;

import ham.citybuildersim.ResearchCatalog.Entry;
import ham.citybuildersim.ResearchCatalog.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * What a set of research dials would find in this city over the next YEARS years, from the DISTRIBUTION of the hidden sizes and never from the
 * city's own draw: the number each dial card on Policy > Research shows beside the dial (research-design 5.4, 6.2; mockups-research/1-funding.html).
 * Pure: no Game, no screen, nothing kept and nothing booked; given a Research as it stands at a month and the dials (the researchers each field would
 * have, the prize it would pay), it plays the next ten years DRAWS times and says what the draws found.
 *
 * WHY (R3b). The card must say what money buys ("0.2% to 25%", 5.4), but "the player never sees the size" (5.2, 6.2), and the size is the one thing
 * the answer turns on: a problem with 3 researcher-years piled and 60 to go is found in a year by 40 people or never by 4. So the preview does not read
 * the city's draw (Research.hiddenSize(), which would give the secret away one find at a time as a dial was moved); it draws the sizes afresh, from
 * the same lognormal, each problem's draw conditioned on its being above the pile already on it (a problem not found yet cannot be smaller than what
 * has been put into it: research_model.py forward()), and plays the month the game plays (Research.month()) over those sizes, so that what it reports
 * is the mean of what the game would do over the cities that fit what the player can see. The seed is fixed (SEED) and never the city's, so the same
 * inputs give the same answer and a card does not shimmer as the screen is redrawn, and the draws are the same sizes for every set of dials (an
 * as-is and a staged run share them), so the DIFFERENCE between the two is not noise.
 *
 * It is Research.month() written again on arrays, because the screen runs it for every card at both the dials as they are and the dials staged, and
 * the model's own step, which keeps maps and strings, is too slow for that by a factor of a few hundred; ResearchPreviewCheck holds the two to the same
 * finds in the same months from the same sizes (path()). Where the step has a rule, this has it: the world's dates first, then each field in the
 * enum's order (a find counts at once for the fields after it), its open problems (Research.isOpen()), its researchers spread by Research.waterFill()
 * over the team caps, and, where its prize is above 0, the labs: the chase (the problems the purse covers that a team could finish before the world,
 * least remaining first), the labs investors want for the room on it (Research.labsFor(), the city's share read from the month before, as
 * Research.labsWanted() reads it), their researchers poured on it cheapest first, each problem taking what its team cap leaves of the city's share.
 * What it leaves out, because nothing the card says depends on it: the labs' part of a pile apart from the city's (a find splits the purse by it; the
 * preview only counts the whole purse as the city's money), a lab that takes months to build (the want is met the month it arises) and the pot.
 */
public final class ResearchPreview {

    /** The years a preview plays ahead: ten, the study's horizon (research-design 5.4). */
    public static final int YEARS = 10;

    /** The draws a card's preview is made from: 400, which puts the standard error of a field's expected finds near 0.04 where it finds about one (the study's runs used 1,000, the harness 20,000). */
    public static final int DRAWS = 400;

    /** The seed the draws are made from: fixed, so that the same dials always say the same, and never the city's own (Game.getWorldSeed()), which would show the player the hidden sizes. */
    public static final long SEED = 0x5EED_C0FFEE_1001L;

    /** The preview's runs since the program began, for a harness to see that a cached answer did not run again. */
    private static final AtomicInteger RUNS = new AtomicInteger();

    /** How many previews have been run (preview(), not path()). */
    static int runs() {
        return RUNS.get();
    }

    /** A month nothing was found in. */
    static final int NEVER = Integer.MAX_VALUE;

    /* =====================================================================
       THE ANSWER
       ===================================================================== */

    /**
     * What the draws found, field by field: the mean finds here over the years, its standard error, the chance of at least one, and the purse the
     * finds would pay (the prize times the finds: the whole purse, the labs' part and the schools' pot alike, being the city's money); the same for
     * all the fields together; and the labs investors would want in the first month of the preview.
     */
    public static final class Result {
        private final int draws;
        private final double[] finds, findsSe, chance, purse;
        private final int[] labs;
        private final double total, totalSe;

        private Result(int draws, double[] finds, double[] findsSe, double[] chance, double[] purse, int[] labs, double total, double totalSe) {
            this.draws = draws;
            this.finds = finds;
            this.findsSe = findsSe;
            this.chance = chance;
            this.purse = purse;
            this.labs = labs;
            this.total = total;
            this.totalSe = totalSe;
        }

        /** How many draws it was made from. */
        public int draws() { return draws; }

        /** The finds here in the field over the years, the mean of the draws. */
        public double finds(Field f) { return finds[f.ordinal()]; }

        /** The standard error of finds(): the draws' spread over the root of their number. */
        public double findsSe(Field f) { return findsSe[f.ordinal()]; }

        /** The share of the draws in which the field found at least one, 0 to 1. */
        public double chance(Field f) { return chance[f.ordinal()]; }

        /** The purse the field's finds would pay over the years: its prize times finds(), in the game's money unit. */
        public double purse(Field f) { return purse[f.ordinal()]; }

        /** The finds of all the fields together, the mean of the draws. */
        public double total() { return total; }

        /** The standard error of total(). */
        public double totalSe() { return totalSe; }

        /** The purse all the fields' finds would pay. */
        public double purseTotal() {
            double sum = 0;
            for (double p : purse) sum += p;
            return sum;
        }

        /**
         * The labs investors would want in the field in the first month of the preview (Research.labsWanted() as the month would ask it); 0 where the field has
         * no prize. Read from the first draw: the same in every draw unless a find in an earlier field that very month opens a problem for this one.
         */
        public int labsFirstMonth(Field f) { return labs[f.ordinal()]; }
    }

    /* =====================================================================
       THE PREVIEW
       ===================================================================== */

    /**
     * The preview of a research at a month: the finds here over the next YEARS years from `draws` draws of the hidden sizes, `researchers` held fixed in
     * each field (a field missing has none) and `prize` the D$ a find in each field pays (missing is none), `wagePerType` the wages by JobType ordinal
     * (a post's cost, and so what a prize covers), `labFill` the share of a lab's posts filled in each field (missing is full). The research is as it
     * stands at `month`: what is found, the work piled and the city's share of each problem in the last month (Research.cityShare()). The same inputs
     * and seed always give the same result.
     */
    public static Result preview(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prize, double[] wagePerType,
                                 EnumMap<Field, Double> labFill, int draws, long seed) {
        RUNS.incrementAndGet();
        ResearchPreview plan = plan(research, month, researchers, prize, wagePerType, labFill);
        return plan.draw(Math.max(1, draws), seed);
    }

    /** The same with every field's labs equally full. */
    public static Result preview(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prize, double[] wagePerType,
                                 double labFill, int draws, long seed) {
        EnumMap<Field, Double> fill = new EnumMap<>(Field.class);
        for (Field f : Field.values()) fill.put(f, labFill);
        return preview(research, month, researchers, prize, wagePerType, fill, draws, seed);
    }

    /* =====================================================================
       THE PLAN: what a research at a month has left to find, on arrays
       ===================================================================== */

    private static final int FIELDS = Field.values().length;

    /** The answer where there is nothing to find (a city founded the default way knows every discovery from the start): nothing anywhere, from DRAWS draws. */
    public static final Result NOTHING = new Result(DRAWS, new double[FIELDS], new double[FIELDS], new double[FIELDS], new double[FIELDS], new int[FIELDS], 0, 0);

    private final int month, end;
    private final Entry[] entry;
    /** The first index of each field's entries (entries are in the enum's field order, the catalogue's within it); a field's are [start[f], start[f + 1]). */
    private final int[] start = new int[FIELDS + 1];
    private final double[] cap, work, pile0, share0;
    private final int[] worldMonth, openFrom;
    /** The entries each one needs that are not known at the month, as indices; a need that cannot be known inside the window is `never`. */
    private final int[][] needs;
    private final boolean[] never;
    private final double[] people = new double[FIELDS], prize = new double[FIELDS], cost = new double[FIELDS], labFill = new double[FIELDS];

    // The state of one path, reused from path to path.
    private final int[] foundAt;
    private final double[] pile;
    private double[] share, prev;
    private final double[] lab;
    private final int[] openIdx, chase;
    private final int[] cachedOpenCount = new int[FIELDS];
    private final int[][] cachedOpen = new int[FIELDS][];
    private final double[][] cachedShare = new double[FIELDS][];
    private final int[] labsFirst = new int[FIELDS];

    private ResearchPreview(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prizes, double[] wagePerType,
                            EnumMap<Field, Double> labFills) {
        this.month = month;
        this.end = month + YEARS * Research.MONTHS_A_YEAR;
        List<Entry> keep = new ArrayList<>();
        for (Field f : Field.values()) {
            start[f.ordinal()] = keep.size();
            for (Entry e : ResearchCatalog.get().entries()) {
                // What can matter: not known here at the month, and its science exists before the window ends.
                if (e.field() == f && !research.known(e.code(), month) && e.worldMonth() - Research.lagMonths(f) <= end) keep.add(e);
            }
        }
        start[FIELDS] = keep.size();
        int n = keep.size();
        entry = keep.toArray(new Entry[0]);
        cap = new double[n];
        work = new double[n];
        pile0 = new double[n];
        share0 = new double[n];
        worldMonth = new int[n];
        openFrom = new int[n];
        needs = new int[n][];
        never = new boolean[n];
        foundAt = new int[n];
        pile = new double[n];
        share = new double[n];
        prev = new double[n];
        lab = new double[n];
        openIdx = new int[n];
        chase = new int[n];
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < n; i++) index.put(entry[i].code(), i);
        for (int i = 0; i < n; i++) {
            Entry e = entry[i];
            cap[i] = Research.teamCap(e);
            work[i] = e.work();
            pile0[i] = research.pile(e.code());
            share0[i] = research.cityShare(e.code());
            worldMonth[i] = e.worldMonth();
            openFrom[i] = e.worldMonth() - Research.lagMonths(e.field());
            List<Integer> mine = new ArrayList<>();
            for (String need : e.needs()) {
                if (research.known(need, month)) continue;
                Integer j = index.get(need);
                if (j == null) never[i] = true;
                else mine.add(j);
            }
            needs[i] = mine.stream().mapToInt(Integer::intValue).toArray();
        }
        for (Field f : Field.values()) {
            int k = f.ordinal();
            people[k] = sane(researchers == null ? null : researchers.get(f));
            prize[k] = sane(prizes == null ? null : prizes.get(f));
            cost[k] = Research.postCost(f, wagePerType);
            Double fill = labFills == null ? null : labFills.get(f);
            labFill[k] = fill == null ? 1 : sane(fill);
            cachedOpenCount[k] = -1;
        }
    }

    /** The plan of a research at a month (package-private for the harness, which holds path() to Research.month()). */
    static ResearchPreview plan(Research research, int month, EnumMap<Field, Double> researchers, EnumMap<Field, Double> prize, double[] wagePerType,
                                EnumMap<Field, Double> labFill) {
        return new ResearchPreview(research, month, researchers, prize, wagePerType, labFill);
    }

    private static double sane(Double x) {
        return x != null && Double.isFinite(x) && x > 0 ? x : 0;
    }

    /** The entries the plan plays, in the order path() takes sizes and gives months: field by field in the enum's order, the catalogue's within a field. */
    List<Entry> entries() {
        return List.of(entry);
    }

    /* =====================================================================
       THE DRAWS
       ===================================================================== */

    private Result draw(int draws, long seed) {
        SplittableRandom rng = new SplittableRandom(seed);
        int n = entry.length;
        double[] size = new double[n];
        double[] sum = new double[FIELDS], sumSq = new double[FIELDS];
        int[] any = new int[FIELDS];
        int[] count = new int[FIELDS];
        double totalSum = 0, totalSq = 0;
        for (int d = 0; d < draws; d++) {
            for (int i = 0; i < n; i++) size[i] = drawSize(rng, work[i], pile0[i]);
            run(size, d == 0);
            Arrays.fill(count, 0);
            for (int i = 0; i < n; i++) if (foundAt[i] != NEVER) count[entry[i].field().ordinal()]++;
            int all = 0;
            for (int f = 0; f < FIELDS; f++) {
                sum[f] += count[f];
                sumSq[f] += (double) count[f] * count[f];
                if (count[f] > 0) any[f]++;
                all += count[f];
            }
            totalSum += all;
            totalSq += (double) all * all;
        }
        double[] finds = new double[FIELDS], findsSe = new double[FIELDS], chance = new double[FIELDS], purse = new double[FIELDS];
        int[] labs = new int[FIELDS];
        for (int f = 0; f < FIELDS; f++) {
            finds[f] = sum[f] / draws;
            findsSe[f] = standardError(sum[f], sumSq[f], draws);
            chance[f] = (double) any[f] / draws;
            purse[f] = prize[f] * finds[f];
            labs[f] = labsFirst[f];
        }
        return new Result(draws, finds, findsSe, chance, purse, labs, totalSum / draws, standardError(totalSum, totalSq, draws));
    }

    /** The standard error of the mean of `draws` counts, from their sum and the sum of their squares. */
    private static double standardError(double sum, double sumSq, int draws) {
        if (draws < 2) return 0;
        double variance = Math.max(0, (sumSq - sum * sum / draws) / (draws - 1));
        return Math.sqrt(variance / draws);
    }

    /**
     * A problem's size in a draw: its typical work times a lognormal draw (sigma Research.SIZE_SIGMA, mean 1), redrawn until it is above the pile on
     * it (research_model.py forward(): a problem not found yet is not smaller than its pile). Where the pile is far above the typical work, so that
     * redrawing would take very many tries, the draw is made from the conditional tail directly (Robert 1995's exponential rejection sampler), which
     * is the same distribution.
     */
    static double drawSize(SplittableRandom rng, double work, double pile) {
        double sigma = Research.SIZE_SIGMA, drift = sigma * sigma / 2;
        if (!(pile > 0)) return work * Math.exp(sigma * rng.nextGaussian() - drift);
        double z0 = (Math.log(pile / work) + drift) / sigma;
        if (z0 <= 1) {
            while (true) {
                double size = work * Math.exp(sigma * rng.nextGaussian() - drift);
                if (size > pile) return size;
            }
        }
        double alpha = (z0 + Math.sqrt(z0 * z0 + 4)) / 2;
        while (true) {
            double z = z0 + rng.nextExponential() / alpha;
            double zMinus = z - alpha;
            if (rng.nextDouble() > Math.exp(-zMinus * zMinus / 2)) continue;
            double size = work * Math.exp(sigma * z - drift);
            if (size > pile) return size;
        }
    }

    /* =====================================================================
       THE PATH: one city's ten years, Research.month() on arrays
       ===================================================================== */

    /**
     * One path over given sizes (`size` by entries(), what Research.hiddenSize() is in the city): the month each entry is found here, or NEVER, by
     * entries(). The same steps as Research.month() from month + 1 to month + YEARS years.
     */
    int[] path(double[] size) {
        run(size, true);
        return foundAt.clone();
    }

    /** The labs the first month of the last path() wanted in a field (the same as Result.labsFirstMonth() for the draw it was recorded in). */
    int labsFirstMonth(Field f) {
        return labsFirst[f.ordinal()];
    }

    private boolean knownAt(int i, int m) {
        return worldMonth[i] <= m || foundAt[i] <= m;
    }

    private void run(double[] size, boolean recordLabs) {
        int n = entry.length;
        Arrays.fill(foundAt, NEVER);
        System.arraycopy(pile0, 0, pile, 0, n);
        System.arraycopy(share0, 0, prev, 0, n);
        Arrays.fill(share, 0);
        Arrays.fill(lab, 0);
        if (recordLabs) Arrays.fill(labsFirst, 0);
        for (int m = month + 1; m <= end; m++) {
            if (m > month + 1) {
                // ...this month's shares are the last month's: what the investors read.
                double[] t = prev;
                prev = share;
                share = t;
                Arrays.fill(share, 0);
            }
            for (int f = 0; f < FIELDS; f++) {
                int lo = start[f], hi = start[f + 1];
                if (lo == hi) continue;
                int k = 0;
                for (int i = lo; i < hi; i++) {
                    if (never[i] || knownAt(i, m) || m < openFrom[i]) continue;
                    boolean ready = true;
                    for (int j : needs[i]) {
                        if (!knownAt(j, m)) {
                            ready = false;
                            break;
                        }
                    }
                    if (ready) openIdx[k++] = i;
                }
                if (k == 0) continue;
                spread(f, k);
                int chased = 0;
                double labsLeft = 0;
                if (prize[f] > 0 && cost[f] > 0) {
                    chased = chaseOf(f, k, m);
                    double room = 0;
                    for (int c = 0; c < chased; c++) room += Math.max(0, cap[chase[c]] - prev[chase[c]]);
                    int labs = Research.labsFor(room);
                    if (recordLabs && m == month + 1) labsFirst[f] = labs;
                    labsLeft = labs * (double) Research.LAB_POSTS * labFill[f];
                    for (int c = 0; c < chased && labsLeft > 0; c++) {
                        int i = chase[c];
                        double take = Math.min(labsLeft, Math.max(0, cap[i] - share[i]));
                        if (!(take > 0)) continue;
                        lab[i] = take;
                        labsLeft -= take;
                    }
                }
                for (int t = 0; t < k; t++) {
                    int i = openIdx[t];
                    double add = (share[i] + lab[i]) / Research.MONTHS_A_YEAR;
                    if (!(add > 0)) continue;
                    pile[i] += add;
                    if (pile[i] >= size[i]) foundAt[i] = m;
                }
                for (int c = 0; c < chased; c++) lab[chase[c]] = 0;
            }
        }
    }

    /** The field's open problems (openIdx[0..k)) spread over by its researchers: share[] set for each, the water fill reused while the open set is the same. */
    private void spread(int f, int k) {
        int[] cached = cachedOpen[f];
        boolean same = cached != null && cachedOpenCount[f] == k;
        for (int t = 0; same && t < k; t++) same = cached[t] == openIdx[t];
        if (!same) {
            double[] caps = new double[k];
            for (int t = 0; t < k; t++) caps[t] = cap[openIdx[t]];
            cachedOpen[f] = Arrays.copyOf(openIdx, k);
            cachedOpenCount[f] = k;
            cachedShare[f] = Research.waterFill(people[f], caps).share();
        }
        double[] shares = cachedShare[f];
        for (int t = 0; t < k; t++) share[openIdx[t]] = shares[t];
    }

    /**
     * The field's chase into chase[]: its open problems (openIdx[0..k)) that the prize pays for and a team at its limit could finish before the world, in
     * this path's piles, least remaining work first and the catalogue's order among equals (Research.chase()). Returns how many.
     */
    private int chaseOf(int f, int k, int m) {
        int c = 0;
        for (int t = 0; t < k; t++) {
            int i = openIdx[t];
            if (Research.purseCovers(prize[f], cost[f], entry[i], pile[i]) && Research.beforeTheWorld(entry[i], pile[i], m)) chase[c++] = i;
        }
        // A stable insertion sort by what is left: the open list is in the catalogue's order, so equals keep it.
        for (int a = 1; a < c; a++) {
            int i = chase[a];
            double rem = Research.remaining(entry[i], pile[i]);
            int b = a - 1;
            while (b >= 0 && Research.remaining(entry[chase[b]], pile[chase[b]]) > rem) {
                chase[b + 1] = chase[b];
                b--;
            }
            chase[b + 1] = i;
        }
        return c;
    }
}
