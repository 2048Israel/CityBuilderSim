package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * The skill mix of the arrivals by year (R0d slice 2): the year's mix in a city founded the 1900 way, and WageBand's ceilings in every
 * other city. Not part of the game.
 *
 * WHAT THIS HAS TO PROVE (R0d, 2026-11-06; docs/studies/research-1900.md 5.4 item 6 and 12.1, "Skill mix by year"). A 1901 arrival was a
 * labourer or a farm hand far more often than a diploma-holder, and a graduate was rare; the mix reaches today's by 2021, and only a city
 * founded the 1900 way is drawn from it. What is checkable:
 *
 *   1. THE KNOTS: in year order, each one's shares non-negative and summing to one, a source key or the estimate flag on each, the last
 *      one today's model, and the mix from its January on today's constants themselves
 *   2. THE LOOK-UP: a knot's own point at its January, the mean of two knots' shares half-way, the first knot's before the first, the
 *      university figure never falling and the no-diploma one never rising from month to month, the same month the same point
 *   3. AGAINST THE STUDY: a 1901 arrival at the going rate most likely holds no diploma, and 1953's university share is a good
 *      multiple of 1901's
 *   4. MIGRATION ON THE YEAR'S MIX: today's mix is today's model (no mix and TODAY draw the same arrivals, the same licences and the
 *      same volume, bit for bit or not, said below); at the going rate in 1913 no graduate arrives and the arrivals without a diploma
 *      outnumber the diploma-holders; with the graduate bands bid up, 1953 brings a larger graduate share than 1913
 *   5. PLAYED: a city founded the 1900 way plays 24 months drawing on the year's mix each month, its arrivals by band summing to the
 *      month's and more of them without a diploma than with one; a city founded the default way holds no mix in any of them
 *
 * Every fixture CAUSES its condition: the markets are priced by the labour market's own months (a market at base, and one whose posts
 * outrun its supply for 200 months, with every licence in short supply too), the draw is a fresh Migration into a city with room, jobs and
 * homes and no workforce (so every band's chance of work is one), and the cities are founded through newGame(Founding.withUnfitted(true))
 * as the player's door founds them. Figures are asserted against the model's own constants and methods.
 */
public class ArrivalMixCheck {

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

    static final int NONE = WageBand.NONE.ordinal();
    static final int DIPLOMA = WageBand.DIPLOMA.ordinal();
    static final int COLLEGE = WageBand.COLLEGE.ordinal();
    static final int UNIVERSITY = WageBand.UNIVERSITY.ordinal();

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

        out.println(fails == 0 ? "\nAll ArrivalMixCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /** The knot of a year. */
    static ArrivalMix.Knot knot(int year) {
        for (ArrivalMix.Knot k : ArrivalMix.KNOTS) if (k.year() == year) return k;
        throw new IllegalStateException("no knot for " + year);
    }

    static ArrivalMix.Knot first() { return ArrivalMix.KNOTS.get(0); }

    static ArrivalMix.Knot last() { return ArrivalMix.KNOTS.get(ArrivalMix.KNOTS.size() - 1); }

    /* ============================ 1. the knots ============================ */

    static void sectionOne() {
        out.println("\n--- 1. the knots: in year order, shares summing to one, a source or an estimate flag, the last one today's model ---");
        List<ArrivalMix.Knot> knots = ArrivalMix.KNOTS;
        check("the table has knots, from the 1900s on", knots.size() >= 2 && first().year() > CityCalendar.EPOCH_YEAR);
        for (int i = 0; i < knots.size(); i++) {
            ArrivalMix.Knot k = knots.get(i);
            String y = String.valueOf(k.year());
            if (i > 0) check(y + ": the knot follows the one before it in year order", k.year() > knots.get(i - 1).year());
            double sum = k.none() + k.diploma() + k.college() + k.university();
            check(y + ": no share is negative, and the diploma's is above zero (the others are stated against it)",
                    k.none() >= 0 && k.college() >= 0 && k.university() >= 0 && k.diploma() > 0);
            check(y + ": the shares sum to one", Math.abs(sum - 1) <= 1e-12, String.valueOf(sum));
            check(y + ": a source key, or flagged an estimate", !k.source().isBlank() && (k.estimate() || !k.source().equals("est.")),
                    k.source() + " estimate=" + k.estimate());
            check(y + ": a note with the arithmetic, which an estimate cannot do without", !k.note().isBlank());
            int january = ArrivalMix.januaryOf(k.year());
            check(y + ": its January is the calendar's January of that year, and the month before it is the year before's",
                    CityCalendar.yearOf(january) == k.year() && CityCalendar.monthOfYear(january) == 1 && CityCalendar.yearOf(january - 1) == k.year() - 1);
        }
        ArrivalMix.Point end = ArrivalMix.of(last());
        check("the last knot is today's model: no ordinary arrivals without a diploma (derived)",
                Math.abs(end.ordinaryNone() - ArrivalMix.TODAY.ordinaryNone()) <= 1e-12, String.valueOf(end.ordinaryNone()));
        near("...its college figure is WageBand's ceiling (derived)", end.college(), WageBand.COLLEGE.arrivalCeiling(), 1e-12);
        near("...its university figure is WageBand's ceiling (derived)", end.university(), WageBand.UNIVERSITY.arrivalCeiling(), 1e-12);
        check("today's mix is WageBand's own ceilings, with nothing arriving without a diploma at the going rate",
                ArrivalMix.TODAY.ordinaryNone() == 0 && ArrivalMix.TODAY.college() == WageBand.COLLEGE.arrivalCeiling()
                        && ArrivalMix.TODAY.university() == WageBand.UNIVERSITY.arrivalCeiling());

        int january = ArrivalMix.januaryOf(last().year());
        boolean today = true;
        int firstWrong = -1;
        for (int m = january; m <= january + 1200; m++) {
            if (ArrivalMix.at(m) != ArrivalMix.TODAY) { today = false; firstWrong = m; break; }
        }
        check("from the last knot's January and for 1,200 months after it the mix is TODAY itself, not a derived copy", today, "month " + firstWrong);
    }

    /* ============================ 2. the look-up ============================ */

    static void sectionTwo() {
        out.println("\n--- 2. the look-up: a knot's own point at its January, the mean of two knots half-way, the first before the first ---");
        List<ArrivalMix.Knot> knots = ArrivalMix.KNOTS;
        for (int i = 0; i < knots.size() - 1; i++) {
            ArrivalMix.Knot a = knots.get(i), b = knots.get(i + 1);
            int from = ArrivalMix.januaryOf(a.year()), to = ArrivalMix.januaryOf(b.year());
            check(a.year() + ": at its January the point is the knot's own, derived, to the bit", ArrivalMix.at(from).equals(ArrivalMix.of(a)),
                    ArrivalMix.at(from) + " vs " + ArrivalMix.of(a));
            check(a.year() + " to " + b.year() + ": fixture: an even number of months between the Januaries, so there is a month half-way", (to - from) % 2 == 0);
            ArrivalMix.Point middle = ArrivalMix.at((from + to) / 2);
            ArrivalMix.Point mean = ArrivalMix.derive((a.none() + b.none()) / 2, (a.diploma() + b.diploma()) / 2,
                    (a.college() + b.college()) / 2, (a.university() + b.university()) / 2);
            near(a.year() + " to " + b.year() + ": half-way the no-diploma figure is the mean of the shares', derived", middle.ordinaryNone(), mean.ordinaryNone(), 1e-12);
            near("...the college figure", middle.college(), mean.college(), 1e-12);
            near("...the university figure", middle.university(), mean.university(), 1e-12);
        }

        ArrivalMix.Point opening = ArrivalMix.of(first());
        boolean before = true;
        for (int m : new int[] {-5, 0, 1, 6, 12, ArrivalMix.januaryOf(first().year()) - 1}) before &= ArrivalMix.at(m).equals(opening);
        check("before the first knot's January the first knot's mix applies, down to a month the calendar floors", before);

        boolean steady = true, finite = true, university = true, college = true, none = true;
        String bad = null;
        ArrivalMix.Point previous = null;
        for (int m = ArrivalMix.januaryOf(first().year()); m <= ArrivalMix.januaryOf(last().year()); m++) {
            ArrivalMix.Point p = ArrivalMix.at(m);
            boolean ok = Double.isFinite(p.ordinaryNone()) && Double.isFinite(p.college()) && Double.isFinite(p.university())
                    && p.ordinaryNone() >= 0 && p.college() >= 0 && p.university() >= 0;
            if (!ok && bad == null) bad = "month " + m + " " + p;
            finite &= ok;
            if (previous != null) {
                if (p.university() < previous.university() - 1e-12) { university = false; if (bad == null) bad = "month " + m + " university " + p.university() + " after " + previous.university(); }
                if (p.college() < previous.college() - 1e-12) { college = false; if (bad == null) bad = "month " + m + " college " + p.college() + " after " + previous.college(); }
                if (p.ordinaryNone() > previous.ordinaryNone() + 1e-12) { none = false; if (bad == null) bad = "month " + m + " none " + p.ordinaryNone() + " after " + previous.ordinaryNone(); }
            }
            steady &= p.equals(ArrivalMix.at(m));
            previous = p;
        }
        check("every month from the first knot's January to the last's is a finite non-negative mix", finite, bad);
        check("the university figure never falls, month by month, from 1901 to 2021", university, bad);
        check("...nor does the college figure", college, bad);
        check("...and the arrivals without a diploma at the going rate (per diploma arrival) never rise", none, bad);
        check("two calls for the same month give the same point, the mix having no state", steady);
        check("...and the mix has moved: 1901 has more ordinary arrivals without a diploma than 2021, and fewer university graduates",
                ArrivalMix.at(ArrivalMix.januaryOf(first().year())).ordinaryNone() > ArrivalMix.at(ArrivalMix.januaryOf(last().year())).ordinaryNone()
                        && ArrivalMix.at(ArrivalMix.januaryOf(first().year())).university() < ArrivalMix.at(ArrivalMix.januaryOf(last().year())).university());
    }

    /* ============================ 3. against the study ============================ */

    static void sectionThree() {
        out.println("\n--- 3. against the study (research-1900 5.4): 1901's arrival most likely holds no diploma, and 1953 has several times the graduates ---");
        ArrivalMix.Knot y1901 = knot(1901), y1953 = knot(1953);
        check("a 1901 arrival is most likely without a diploma: the 1901 knot's no-diploma share is above a half", y1901.none() > .5, String.valueOf(y1901.none()));
        ArrivalMix.Point opening = ArrivalMix.at(ArrivalMix.januaryOf(1901));
        check("...so at the going rate the arrivals without a diploma outnumber the diploma-holders, where today's model has none of them", opening.ordinaryNone() > 1 && ArrivalMix.TODAY.ordinaryNone() == 0,
                String.valueOf(opening.ordinaryNone()));
        double ratio = y1953.university() / y1901.university();
        check("1953's university share is at least 2.5 times 1901's (the study: 1.2 students per 1,000 in 1901, 4.7 in 1950)", ratio >= 2.5, String.valueOf(ratio));
        check("...and 1953's working arrivals hold more diplomas, college degrees and university degrees than 1901's, and fewer of them none",
                y1953.diploma() > y1901.diploma() && y1953.college() > y1901.college() && y1953.university() > y1901.university() && y1953.none() < y1901.none());
    }

    /* ============================ 4. Migration on the year's mix ============================ */

    /** The adult share the fixtures run at (PopulationCheck's). */
    static final double ADULT_SHARE = PopulationCohorts.equilibriumShare(AgeBand.ADULT);

    /** A labour market at base: every wage the going rate, so every band's premium is one and nobody is bought. */
    static LabourMarket atBase() {
        return new LabourMarket();
    }

    /**
     * A labour market whose posts outrun its supply for 200 months, every band's and every licence's: the wages walk to the top of their
     * range, so the premium on the graduate and the no-diploma bands and the doctors' over their band are all well above one.
     */
    static LabourMarket bidUp() {
        LabourMarket market = new LabourMarket();
        int bands = WageBand.values().length, jobs = JobType.values().length;
        double[] posts = new double[bands], supply = new double[bands], heads = new double[jobs];
        long[] jobPosts = new long[jobs];
        Arrays.fill(posts, 1000);
        Arrays.fill(supply, 250);
        Arrays.fill(jobPosts, 1000);
        for (int month = 0; month < 200; month++) market.advanceMonth(posts, supply, jobPosts, heads);
        return market;
    }

    /**
     * One month's draw by a fresh Migration with this mix (null for none), priced by this market, into a city of 4,000 with room, jobs and homes
     * for more and no workforce behind it (so every band's chance of work is one): the draw is the arrivals and who they are.
     */
    static Migration draw(ArrivalMix.Point mix, LabourMarket market) {
        Migration mig = new Migration();
        mig.setYearMix(mix);
        FamilyModel homes = new FamilyModel();
        PopulationCohorts some = new PopulationCohorts();
        some.migrate(4000);
        homes.rebuild(some, new double[PayTier.values().length]);
        mig.monthlyNet(4000, 6000, 12000, 9000, homes, ADULT_SHARE, 0, market, null);
        return mig;
    }

    static double sum(double[] values) {
        double s = 0;
        for (double v : values) s += v;
        return s;
    }

    /** The college and university arrivals (the licensed professions included, which are counted in the university band) as a share of the month's. */
    static double graduateShare(Migration mig) {
        double[] mix = mig.getLastArrivalMix();
        return (mix[COLLEGE] + mix[UNIVERSITY]) / sum(mix);
    }

    static void sectionFour() {
        out.println("\n--- 4. Migration on the year's mix: today's mix is today's model; 1913 at the going rate is mostly undiplomaed; 1953 brings more graduates when they are bid for ---");
        ArrivalMix.Point y1913 = ArrivalMix.at(ArrivalMix.januaryOf(1913)), y1953 = ArrivalMix.at(ArrivalMix.januaryOf(1953));

        // (a) today's mix is today's model
        LabourMarket base = atBase(), bid = bidUp();
        boolean priced = true;
        for (WageBand band : WageBand.values()) priced &= Migration.reach(base.bandPremium(band)) == 0;
        check("fixture: at base no band is dear: the premium of every band is the going rate", priced);
        check("fixture: bid up, the college and university bands are bought for more than a third of their ceilings",
                Migration.reach(bid.bandPremium(WageBand.COLLEGE)) > .33 && Migration.reach(bid.bandPremium(WageBand.UNIVERSITY)) > .33,
                Migration.reach(bid.bandPremium(WageBand.COLLEGE)) + " / " + Migration.reach(bid.bandPremium(WageBand.UNIVERSITY)));

        boolean bitExact = true, same = true, volume = true;
        double worst = 0;
        for (LabourMarket market : new LabourMarket[] {base, bid}) {
            Migration today = draw(null, market), joined = draw(ArrivalMix.TODAY, market);
            check("fixture: people arrive into the city the draw is made in", today.getLastArrivals() > 0, String.valueOf(today.getLastArrivals()));
            volume &= Double.compare(today.getLastArrivals(), joined.getLastArrivals()) == 0;
            bitExact &= Arrays.equals(today.getLastArrivalMix(), joined.getLastArrivalMix())
                    && Arrays.equals(today.getLastArrivalLicences(), joined.getLastArrivalLicences());
            double[][] pairs = {today.getLastArrivalMix(), joined.getLastArrivalMix(), today.getLastArrivalLicences(), joined.getLastArrivalLicences()};
            for (int p = 0; p < pairs.length; p += 2) {
                for (int i = 0; i < pairs[p].length; i++) {
                    worst = Math.max(worst, Math.abs(pairs[p][i] - pairs[p + 1][i]) / Math.max(1, Math.abs(pairs[p][i])));
                }
            }
        }
        same = worst <= 1e-12;
        Migration bought = draw(null, bid);
        check("fixture: bid up, graduates and licence-holders arrive in today's model (so the join below compares something)",
                bought.getLastArrivalMix()[COLLEGE] > 0 && sum(bought.getLastArrivalLicences()) > 0
                        && bought.getLastArrivalMix()[UNIVERSITY] > sum(bought.getLastArrivalLicences()));
        check("the arrivals by band and the licences are the same with no mix as with TODAY, at the going rate and bid up, to 1e-12", same, "worst " + worst);
        out.println("   (the join is bit-exact: " + bitExact + ", worst relative difference " + worst + ")");
        check("...and how many arrive is the same: the volume is not the mix's", volume);

        // (b) 1913 at the going rate
        Migration atRate = draw(y1913, base), todaysAtRate = draw(null, base);
        double[] mix = atRate.getLastArrivalMix();
        check("1913 at the going rate: no college graduate arrives", mix[COLLEGE] == 0, String.valueOf(mix[COLLEGE]));
        check("...no university graduate, and no licence-holder", mix[UNIVERSITY] == 0 && sum(atRate.getLastArrivalLicences()) == 0,
                mix[UNIVERSITY] + " / " + sum(atRate.getLastArrivalLicences()));
        check("...and the arrivals without a diploma outnumber the diploma-holders", mix[NONE] > mix[DIPLOMA], mix[NONE] + " vs " + mix[DIPLOMA]);
        near("...by the year's figure: no-diploma arrivals per diploma arrival", mix[NONE] / mix[DIPLOMA], y1913.ordinaryNone(), 1e-9);
        near("...everyone who arrives is placed once: the bands sum to the month's arrivals", sum(mix), atRate.getLastArrivals(), 1e-9);
        check("...where today's model brings nobody without a diploma at the going rate, and 1913's brings the same number of people",
                todaysAtRate.getLastArrivalMix()[NONE] == 0 && todaysAtRate.getLastArrivalMix()[DIPLOMA] > 0
                        && Double.compare(todaysAtRate.getLastArrivals(), atRate.getLastArrivals()) == 0);

        // (c) the graduate bands bid up
        Migration in1913 = draw(y1913, bid), in1953 = draw(y1953, bid);
        System.out.printf("   graduates bid up: %.2f%% of 1913's arrivals, %.2f%% of 1953's, %.2f%% of today's%n",
                100 * graduateShare(in1913), 100 * graduateShare(in1953), 100 * graduateShare(bought));
        check("fixture: bid up, 1913 brings some graduates", graduateShare(in1913) > 0, String.valueOf(graduateShare(in1913)));
        check("with the graduate bands bid up, 1953 brings a larger share of graduates than 1913", graduateShare(in1953) > graduateShare(in1913),
                graduateShare(in1953) + " vs " + graduateShare(in1913));
        check("...and today's model, the last mix, a larger share still", graduateShare(bought) > graduateShare(in1953));
        check("...with the arrivals without a diploma a smaller share of 1953's than of 1913's, bid up",
                in1953.getLastArrivalMix()[NONE] / sum(in1953.getLastArrivalMix()) < in1913.getLastArrivalMix()[NONE] / sum(in1913.getLastArrivalMix()));
    }

    /* ============================ 5. played ============================ */

    static void sectionFive() {
        out.println("\n--- 5. played: a city founded the 1900 way draws on the year's mix every month for 24, a default city on none ---");
        Game g = village("arrival-mix-1900", THE_1900_WAY);
        Game plain = village("arrival-mix-default", DEFAULT);
        check("fixture: one village founded the 1900 way, the other the default way", g.foundedUnfitted() && !plain.foundedUnfitted());
        check("a new city holds no mix before its first month", g.getMigration().getYearMix() == null && plain.getMigration().getYearMix() == null);

        boolean advanced = true, mixed = true, sums = true, none = true;
        double noDiploma = 0, diploma = 0, arrived = 0, plainNone = 0, plainDiploma = 0;
        String bad = null;
        for (int i = 1; i <= 24; i++) {
            int before = g.getMonth();
            step(g, 1);
            int played = g.getMonth();
            advanced &= played == before + 1;
            Migration mig = g.getMigration();
            boolean month = ArrivalMix.at(played).equals(mig.getYearMix());
            mixed &= month;
            double[] who = mig.getLastArrivalMix();
            if (Math.abs(sum(who) - mig.getLastArrivals()) > 1e-9 * Math.max(1, mig.getLastArrivals())) {
                sums = false;
                if (bad == null) bad = "month " + played + " bands sum " + sum(who) + " vs arrivals " + mig.getLastArrivals();
            }
            if (!month && bad == null) bad = "month " + played + " mix " + mig.getYearMix() + " vs " + ArrivalMix.at(played);
            noDiploma += who[NONE];
            diploma += who[DIPLOMA];
            arrived += mig.getLastArrivals();

            step(plain, 1);
            none &= plain.getMigration().getYearMix() == null;
            plainNone += plain.getMigration().getLastArrivalMix()[NONE];
            plainDiploma += plain.getMigration().getLastArrivalMix()[DIPLOMA];
        }
        check("24 months played: the clock moved one month a press", advanced);
        check("...each month the 1900 city's Migration holds the year's mix, ArrivalMix.at() of the month played", mixed, bad);
        check("fixture: people arrived in the 1900 city over the 24 months", arrived > 0, String.valueOf(arrived));
        check("...its arrivals by band sum to the month's arrivals, every month", sums, bad);
        System.out.printf("   24 months: %.1f arrived without a diploma, %.1f with one (a default village: %.1f and %.1f)%n", noDiploma, diploma, plainNone, plainDiploma);
        check("...and over the 24 months more arrive without a diploma than with one", noDiploma > diploma, noDiploma + " vs " + diploma);
        check("a city founded the default way holds no mix in any of its 24 months", none);
        check("...and the same village founded the default way draws a smaller share of its arrivals without a diploma",
                plainNone / Math.max(plainNone + plainDiploma, 1e-12) < noDiploma / Math.max(noDiploma + diploma, 1e-12),
                plainNone + " / " + plainDiploma + " vs " + noDiploma + " / " + diploma);
    }
}
