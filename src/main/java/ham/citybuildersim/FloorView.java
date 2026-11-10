package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleFunction;

/**
 * What the Policy tab's minimum-wage page draws (WS1): the floor against the going rate and the unskilled wage, against the average wage and the world's
 * nations, what a full-time floor job pays for, who is paid it or near it, the tie, the move a staged floor makes, every job's target at the two floors,
 * the joined market, and the words that go beside them. A read of the city at one moment and a staged floor; nothing kept, nothing booked, no JavaFX.
 *
 * WHY (WS1; docs/studies/spec-wages.md 3.6). The page's rule is the screens' rule - every figure through a public model getter, never recomputed in the
 * screen - and the page needs a dozen figures that no one getter holds: the floor's share of the going rate and of the average wage, the cover of a
 * floor job, the workers a staged floor lifts and what they are paid at the two targets, the nations the gauge is drawn against. They are made once, here,
 * where a harness (PolicyPreviewCheck) can hold each to a hand sum of the model's own getters, and the screen only paints them. The words take the
 * screen's money writer as an argument, so the model makes no "$" of its own and one writer serves the whole page.
 *
 * A NUMBER THAT HAS NO MEANING IS NaN, NOT 0: a share of an average nobody is paid, a share of a going rate of nothing, an average pay of no workers.
 * The screen writes NaN as a dash (Formats), and a 0 would be read as a fact.
 */
public final class FloorView {

    /** The right end of the gauge against the going rate: 1.5 times it, over the floor a player would ever mean to set. */
    public static final double GAUGE_TOP = 1.5;

    /** Under this share of the going rate the floor never binds: the market alone stops at it (LabourMarket.MIN_MULTIPLE). */
    public static final double NEVER_UNDER = LabourMarket.MIN_MULTIPLE;

    /** Over this share of the going rate the floor binds even when hands are short: the going rate is what the market pays at exactly enough workers. */
    public static final double ALWAYS_OVER = 1.0;

    /** The nation at this percentile (nearest rank, counted from the lowest ratio) is the bottom of the middle band of nations. */
    public static final double BAND_LOW_PERCENTILE = .10;

    /** The nation at this percentile (nearest rank) is the top of the middle band of nations. */
    public static final double BAND_HIGH_PERCENTILE = .90;

    /** The world file's id for Canada, the nation the page names beside the lowest and the highest. */
    public static final String CANADA = "CAN";

    /** What a job's row says when the city has no posts of it. */
    public static final String NO_POSTS_WORDS = "no posts here";

    /** Where a floor sits against the going rate: it never binds, binds only while hands are plentiful, or binds always. */
    public enum Zone { NEVER, GLUT, ALWAYS }

    /** A nation with a minimum-wage-to-average-wage figure in the world file (key "kaitz"). */
    public record Nation(String id, String name, double kaitz) { }

    /** One job type's row: its posts and fill, its wage, its target at the floor in force and at the staged floor, whether the staged floor moves it. */
    public record JobRow(JobType job, long posts, double filled, double wage, double targetNow, double targetStaged, boolean moved, boolean noPosts) { }

    /** The zone a share of the going rate falls in: under NEVER_UNDER never, over ALWAYS_OVER always, else GLUT; null when the share is not a number. */
    public static Zone zoneOf(double shareOfGoingRate) {
        if (Double.isNaN(shareOfGoingRate)) return null;
        if (shareOfGoingRate < NEVER_UNDER) return Zone.NEVER;
        if (shareOfGoingRate > ALWAYS_OVER) return Zone.ALWAYS;
        return Zone.GLUT;
    }

    private static final Formats FORMAT = Formats.INSTANCE;

    private final WageBand lowestBand = WageBand.values()[0];

    private final double stagedFloor;

    // Against the going rate.
    private final double goingRate, floor, staged, unskilledWage;
    private final double floorOverGoing, stagedOverGoing, unskilledOverGoing;

    // Against the average wage, the nations.
    private final double averageWage, floorOverAverage, stagedOverAverage, lowBandsShare;
    private final List<Nation> nations;
    private final Nation lowestNation, highestNation, canada, bandLow, bandHigh;
    private final int nationsInBand;

    // What a full-time floor job pays for.
    private final double payslipShare, takeHome, rent, food, fees, costSum, left, cover;

    // Who is near it.
    private final double[] paid, near;
    private final double paidTotal, nearTotal, postsFilled, everyoneElse;

    // The tie.
    private final LabourMarket.FloorTie tie;
    private final double tieShare, enteringShare;

    // The preview, the floor's own move only.
    private final double lifted, liftedShare, liftedPayNow, liftedPayThen, payrollNow, payrollThen, payrollMove;

    // Per job, and the joined market.
    private final List<JobRow> jobs;
    private final boolean joined;

    /** The page's figures for a city and a staged real floor, in founding money as the dial holds it (the floor in force, LabourMarket.getMinimumWage(), when nothing is staged). */
    public static FloorView of(Game g, double stagedFloor) {
        return new FloorView(g, stagedFloor);
    }

    private FloorView(Game g, double stagedFloor) {
        this.stagedFloor = stagedFloor;
        LabourMarket market = g.getLabourMarket();
        PopulationManager people = g.getPopulationManager();

        goingRate = market.cashGoingRate();
        floor = market.cashMinimumWage();
        staged = market.cashAt(stagedFloor);
        unskilledWage = market.getWage(JobType.NO_DIPLOMA);
        floorOverGoing = over(floor, goingRate);
        stagedOverGoing = over(staged, goingRate);
        unskilledOverGoing = over(unskilledWage, goingRate);

        // Every job: posts and the posts filled as getJobsFilled() counts them (posts x fill rate, unrounded).
        long[] posts = people.getJobs();
        double[] fill = people.getJobFillRate();
        List<JobRow> rows = new ArrayList<>();
        double filledTotal = 0, lowBandsFilled = 0;
        for (JobType job : JobType.values()) {
            int i = job.ordinal();
            long n = i < posts.length ? posts[i] : 0;
            double filled = i < posts.length && i < fill.length ? posts[i] * fill[i] : 0;
            double now = market.targetWageAt(job, market.getMinimumWage());
            double then = market.targetWageAt(job, stagedFloor);
            rows.add(new JobRow(job, n, filled, market.getWage(job), now, then, PolicyPreview.floorMoves(now, then), n == 0));
            filledTotal += filled;
            if (WageBand.of(job).ordinal() < 2) lowBandsFilled += filled;
        }
        jobs = Collections.unmodifiableList(rows);
        postsFilled = filledTotal;
        lowBandsShare = over(lowBandsFilled, filledTotal);

        // The average wage, and the floor's share of it.
        averageWage = people.getAverageWage();
        boolean employed = averageWage > 0 && Double.isFinite(averageWage);
        floorOverAverage = employed ? floor / averageWage : Double.NaN;
        stagedOverAverage = employed ? staged / averageWage : Double.NaN;

        // The nations: every one with a finite ratio, lowest first.
        List<Nation> found = new ArrayList<>();
        for (WorldMeasures.Place place : WorldMeasures.load().nations()) {
            double ratio = place.value("kaitz");
            if (Double.isFinite(ratio)) found.add(new Nation(place.id(), place.name(), ratio));
        }
        found.sort(Comparator.comparingDouble(Nation::kaitz).thenComparing(Nation::id));
        nations = Collections.unmodifiableList(found);
        lowestNation = found.isEmpty() ? null : found.get(0);
        highestNation = found.isEmpty() ? null : found.get(found.size() - 1);
        Nation can = null;
        for (Nation n : found) if (CANADA.equals(n.id())) can = n;
        canada = can;
        bandLow = found.isEmpty() ? null : found.get(nearestRank(BAND_LOW_PERCENTILE, found.size()) - 1);
        bandHigh = found.isEmpty() ? null : found.get(nearestRank(BAND_HIGH_PERCENTILE, found.size()) - 1);
        int inside = 0;
        if (bandLow != null) for (Nation n : found) if (n.kaitz() >= bandLow.kaitz() && n.kaitz() <= bandHigh.kaitz()) inside++;
        nationsInBand = inside;

        // What a full-time floor job pays for: the take-home at the lowest band's payslip, against a person living alone.
        HouseholdAccounts households = g.getHouseholds();
        payslipShare = g.getEconomyManager().getTaxPolicy().payslipShare(lowestBand);
        takeHome = floor * (1 - payslipShare);
        rent = households.rentPerHousehold();
        food = households.shoppingPerHead();
        fees = households.feesPerHead();
        costSum = rent + food + fees;
        left = takeHome - costSum;
        cover = PolicyPreview.floorJobCover(g);

        // Who is paid the floor, who is a little over it, and everyone else.
        paid = people.workersPaidTheFloor(market);
        near = people.workersNearTheFloor(market);
        double paidSum = 0, nearSum = 0;
        for (double v : paid) paidSum += v;
        for (double v : near) nearSum += v;
        paidTotal = paidSum;
        nearTotal = nearSum;
        everyoneElse = postsFilled - paidSum - nearSum;

        // The tie, and the share choosing the average wage now would hold.
        tie = market.getFloorTie();
        tieShare = market.getTieShare();
        enteringShare = employed
                ? Math.max(LabourMarket.MIN_TIE_SHARE, Math.min(LabourMarket.MAX_TIE_SHARE, floorOverAverage))
                : LabourMarket.DEFAULT_TIE_SHARE;

        // The staged floor's own move.
        PolicyPreview.FloorMove move = PolicyPreview.floorMoveAt(g, stagedFloor);
        lifted = move.workers();
        liftedShare = over(move.workers(), postsFilled);
        liftedPayNow = move.workers() > 0 ? move.payNow() / move.workers() : Double.NaN;
        liftedPayThen = move.workers() > 0 ? move.payThen() / move.workers() : Double.NaN;
        payrollNow = move.billNow();
        payrollThen = move.billThen();
        payrollMove = move.billThen() - move.billNow();

        joined = people.fillByBand().joinedAbove(lowestBand);
    }

    /** a over b, NaN when b is not a positive finite number. */
    private static double over(double a, double b) {
        return b > 0 && Double.isFinite(b) ? a / b : Double.NaN;
    }

    /** The 1-based rank of the nearest-rank percentile p among n: ceil(p x n), at least 1 (a hair under it, so an exact product does not round up). */
    private static int nearestRank(double p, int n) {
        return Math.max(1, Math.min(n, (int) Math.ceil(p * n - 1e-9)));
    }

    /* =====================================================================
       AGAINST THE GOING RATE
       ===================================================================== */

    /** The staged floor as the dial holds it: real, in founding money. */
    public double stagedFloor() { return stagedFloor; }
    /** The going rate in today's money: LabourMarket.cashGoingRate(). */
    public double goingRate() { return goingRate; }
    /** The floor in force in today's money: LabourMarket.cashMinimumWage(). */
    public double floor() { return floor; }
    /** The staged floor in today's money: LabourMarket.cashAt(stagedFloor). */
    public double staged() { return staged; }
    /** What the unskilled job is paid now: the lowest band's job, LabourMarket.getWage(NO_DIPLOMA). */
    public double unskilledWage() { return unskilledWage; }
    /** The floor over the going rate; NaN when the going rate is nothing. */
    public double floorOverGoing() { return floorOverGoing; }
    /** The staged floor over the going rate. */
    public double stagedOverGoing() { return stagedOverGoing; }
    /** The unskilled wage over the going rate. */
    public double unskilledOverGoing() { return unskilledOverGoing; }
    /** The zone the floor in force falls in; null when its share is not a number. */
    public Zone floorZone() { return zoneOf(floorOverGoing); }
    /** The zone the staged floor falls in. */
    public Zone stagedZone() { return zoneOf(stagedOverGoing); }

    /* =====================================================================
       AGAINST THE AVERAGE WAGE, AND THE NATIONS
       ===================================================================== */

    /** The average wage: PopulationManager.getAverageWage(), the wage bill over the posts filled (the Rankings'); 0 with nobody employed. */
    public double averageWage() { return averageWage; }
    /** The floor's share of the average wage; NaN with nobody employed. */
    public double floorOverAverage() { return floorOverAverage; }
    /** The staged floor's share of the average wage; NaN with nobody employed. */
    public double stagedOverAverage() { return stagedOverAverage; }
    /** The share of the posts filled that are in the two lowest wage bands (why a city reads high against the average wage); NaN with none filled. */
    public double lowBandsShare() { return lowBandsShare; }
    /** The world file's nations with a minimum-wage-to-average-wage figure, lowest first. */
    public List<Nation> nations() { return nations; }
    /** The nation with the lowest ratio; null with none. */
    public Nation lowestNation() { return lowestNation; }
    /** The nation with the highest ratio; null with none. */
    public Nation highestNation() { return highestNation; }
    /** Canada (CANADA); null when the file has no ratio for it. */
    public Nation canada() { return canada; }
    /** The nation at BAND_LOW_PERCENTILE: the bottom of the middle band; null with no nations. */
    public Nation bandLow() { return bandLow; }
    /** The nation at BAND_HIGH_PERCENTILE: the top of the middle band; null with no nations. */
    public Nation bandHigh() { return bandHigh; }
    /** How many nations have a ratio inside the middle band, its two ends included. */
    public int nationsInBand() { return nationsInBand; }

    /* =====================================================================
       WHAT A FULL-TIME FLOOR JOB PAYS FOR
       ===================================================================== */

    /** The share of a lowest-band payslip that does not reach the worker (TaxPolicy.payslipShare()). */
    public double payslipShare() { return payslipShare; }
    /** The floor less the payslip's share. */
    public double takeHome() { return takeHome; }
    /** A home's rent: HouseholdAccounts.rentPerHousehold(). */
    public double rent() { return rent; }
    /** A person's basket: HouseholdAccounts.shoppingPerHead(). */
    public double food() { return food; }
    /** A person's fees: HouseholdAccounts.feesPerHead(). */
    public double fees() { return fees; }
    /** Rent, food and fees: what living alone costs (HouseholdAccounts.costOfLivingAlone()). */
    public double costSum() { return costSum; }
    /** What is left of the take-home after them; negative when the floor does not carry a person. */
    public double left() { return left; }
    /** The take-home over what living alone costs: PolicyPreview.floorJobCover(). */
    public double cover() { return cover; }

    /* =====================================================================
       WHO IS NEAR IT
       ===================================================================== */

    /** The posts filled that the floor is what pays, in this band (PopulationManager.workersPaidTheFloor()). */
    public double paid(WageBand band) { return paid[band.ordinal()]; }
    /** The posts filled that are paid a little over the floor, in this band (PopulationManager.workersNearTheFloor()). */
    public double near(WageBand band) { return near[band.ordinal()]; }
    /** The posts filled in this band that are paid the floor or a little over it: paid(band) and near(band) together. */
    public double atOrNear(WageBand band) { return paid[band.ordinal()] + near[band.ordinal()]; }
    /** The posts filled that the floor is what pays, in every band. */
    public double paidTotal() { return paidTotal; }
    /** The posts filled that are paid a little over it, in every band. */
    public double nearTotal() { return nearTotal; }
    /** The posts filled, unrounded: posts times fill rate over every job, as PopulationManager.getJobsFilled() counts them. */
    public double postsFilled() { return postsFilled; }
    /** The posts filled that are neither paid the floor nor near it. */
    public double everyoneElse() { return everyoneElse; }
    /** The posts paid the floor over the posts filled; NaN with none filled. */
    public double paidShare() { return over(paidTotal, postsFilled); }
    /** The posts paid a little over the floor, over the posts filled; NaN with none filled. */
    public double nearShare() { return over(nearTotal, postsFilled); }
    /** The posts neither paid the floor nor near it, over the posts filled; NaN with none filled. */
    public double everyoneElseShare() { return over(everyoneElse, postsFilled); }

    /* =====================================================================
       THE TIE
       ===================================================================== */

    /** What the floor keeps pace with now. */
    public LabourMarket.FloorTie tie() { return tie; }
    /** The share of the average wage the tie asks (kept, unused, under another tie). */
    public double tieShare() { return tieShare; }
    /** The share choosing the average wage now would hold: the floor's share of it, held to the tie's range; the default share with nobody employed. */
    public double enteringShare() { return enteringShare; }

    /* =====================================================================
       THE PREVIEW: THE FLOOR'S OWN MOVE
       ===================================================================== */

    /** The posts filled in the jobs the staged floor moves (PolicyPreview.floorMoveAt()). */
    public double lifted() { return lifted; }
    /** Those workers over the posts filled; NaN with none filled. */
    public double liftedShare() { return liftedShare; }
    /** Their average monthly pay at the floor in force; NaN with none lifted. */
    public double liftedPayNow() { return liftedPayNow; }
    /** Their average monthly pay at the staged floor; NaN with none lifted. */
    public double liftedPayThen() { return liftedPayThen; }
    /** The city's wage bill a month at every job's target at the floor in force, the posts filled (FloorMove.billNow()). */
    public double payrollNow() { return payrollNow; }
    /** ...and at every job's target at the staged floor, once wages have walked there (FloorMove.billThen()). */
    public double payrollThen() { return payrollThen; }
    /** The city's wage bill a month once wages have walked to the staged floor, less what it is at the floor in force. */
    public double payrollMove() { return payrollMove; }

    /* =====================================================================
       PER JOB, AND THE JOINED MARKET
       ===================================================================== */

    /** Every job type's row, in JobType order. */
    public List<JobRow> jobs() { return jobs; }
    /** One job type's row. */
    public JobRow job(JobType job) { return jobs.get(job.ordinal()); }
    /** Whether diploma holders fill the unskilled posts: the lowest band filled as one market with the band above it (BandFill.joinedAbove()). */
    public boolean joined() { return joined; }
    /** The wage the two bands share when they are one market (the unskilled wage); NaN when they are not. */
    public double joinedWage() { return joined ? unskilledWage : Double.NaN; }

    /* =====================================================================
       THE WORDS
       ===================================================================== */

    /** Who the floor holds: how many workers are paid it, or that nobody is and what unskilled work goes for. `money` is the screen's money writer. */
    public String holdsWords(DoubleFunction<String> money) {
        long n = Math.round(paidTotal);
        if (n >= 1) return "Holds " + FORMAT.count(n) + " " + (n == 1 ? "worker" : "workers") + ": they are paid the floor itself.";
        return "Nobody here is paid it: unskilled work goes for " + money.apply(unskilledWage) + ".";
    }

    /** That diploma holders fill the unskilled posts, so both bands pay the one wage; null when the bands are not one market. */
    public String joinedWords(DoubleFunction<String> money) {
        if (!joined) return null;
        WageBand above = WageBand.values()[lowestBand.ordinal() + 1];
        return "One market: " + above.label().toLowerCase(Locale.ROOT) + " holders fill the unskilled posts, so both pay " + money.apply(unskilledWage) + ".";
    }

    /** Why the gauge against the average wage reads high, when it does (the floor's share of it is over the middle band's top); null otherwise. */
    public String readsHighWords() {
        if (bandHigh == null || Double.isNaN(floorOverAverage) || !(floorOverAverage > bandHigh.kaitz())) return null;
        return "This city reads high because " + FORMAT.pct(lowBandsShare) + " of its posts are unskilled or diploma posts, paid near the floor; "
                + "the gauge against unskilled work is the one that says whether the floor bites.";
    }
}
