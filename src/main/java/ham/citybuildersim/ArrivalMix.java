package ham.citybuildersim;

import java.util.List;

/**
 * What the world's movers hold by year: the skill mix a month's arrivals are drawn from, in a city founded the 1900 way.
 *
 * WHY A MIX THAT FOLLOWS THE YEAR (R0d slice 2, 2026-11-06; docs/studies/research-1900.md 5.4 item 6, "The world's mix of skills follows
 * the year ... arrivals' diplomas come from that mix", and 12.1, "Skill mix by year": keep how many arrive apart from what they hold).
 * Migration.composeArrivals() makes the diploma the ordinary arrival ("universal high school", WageBand.arrivalCeiling()): the diploma band's
 * weight is 1 whatever the city pays, no-diploma arrivals are bought on a premium up to .12 of it, and the graduate bands are bought up to .35
 * and .12. That is a developed economy's mix. A 1901 mover was a labourer or a farm hand far more often than a diploma-holder (the 1913
 * immigrant tables: 72.8 in a hundred farmers, labourers and miners to 23.1 in a trade), and a university graduate was rarer still (1.2
 * students per 1,000 in 1901 against 4.7 in 1950). So a city founded the 1900 way draws its arrivals from the year's mix, and one founded the
 * default way (Game.foundedUnfitted() false) never reads this class: its Migration holds no mix and runs the code it always ran.
 *
 * WHAT IS READ AND WHAT IS INTERPOLATED. A Knot is the shares of a year's working arrivals by skill, at January of its year, summing to one;
 * the shares are what is interpolated, linearly by month from one January to the next. A Point is what composeArrivals() reads, derived from
 * shares and stating them relative to the diploma band's 1, the way WageBand's ceilings do: ordinaryNone, the arrivals with no diploma who come
 * at the going rate, unconditionally, per diploma arrival (today's model has none); college and university, those bands' ceilings, bought
 * exactly as today, on the premium (Migration.reach()). The no-diploma band keeps today's bought part on top of ordinaryNone. Before the first
 * knot's January the first knot's mix applies; at and after the last knot's January the mix is TODAY, the constants themselves, so a 1900 city
 * grows into the model every other city is on. The licensed professions keep today's licence shares (EducationType.worldLicenceShare()) in every
 * year, on the year's university figure: the study gives no earlier share of graduates holding a licence (a call the batch session recorded).
 *
 * HOW MANY ARRIVE is not here: Migration's volume (the jobs, the homes, the pulls) is the same in every city. This class only says who comes.
 *
 * Sources (Statistics Canada and the Crown, all commercial use allowed):
 *   [CYB1913]   Canada Year Book 1913, Table 47 (immigrant arrivals by occupation, fiscal year 1912-13),
 *               https://publications.gc.ca/collections/collection_2017/statcan/11-202/CS11-202-1913-eng.pdf
 *               (Crown copyright expired; Statistics Canada Open Licence)
 *   [HSC-A351]  Historical Statistics of Canada, 11-516-X, series A351-368 (immigrant workers by intended occupation),
 *               https://www150.statcan.gc.ca/n1/pub/11-516-x/sectiona/A351_368-eng.csv (Statistics Canada Open Licence)
 *   [V1951]     Ninth Census of Canada 1951, Vol. I, Table 27 (years of schooling),
 *               https://publications.gc.ca/collections/collection_2017/statcan/CS98-1951-2.pdf (Crown copyright expired)
 *   [B1]        Census and Statistics Office, Bulletin I, "Wage-earners by occupations" (the 1901 census, 1907),
 *               https://publications.gc.ca/collections/collection_2017/statcan/CS98-1901P-1.pdf (Crown copyright expired; WageLadder's [B1])
 *   [V1911]     Census of Canada 1911, Vol. VI, occupations (1901 re-classified),
 *               https://publications.gc.ca/collections/collection_2016/statcan/CS98-1911-6.pdf (Crown copyright expired)
 */
public final class ArrivalMix {

    private ArrivalMix() { }

    /**
     * One year's mix of the working arrivals, at January of that year: each skill's share of them (none, diploma, college, university),
     * summing to one, with the key of where it was read, whether it is an estimate, and a note with the arithmetic.
     */
    public record Knot(int year, double none, double diploma, double college, double university, String source, boolean estimate, String note) { }

    /**
     * The three figures composeArrivals() reads, relative to the diploma band's 1: the no-diploma arrivals who come at the going rate per
     * diploma arrival, and the college and university bands' ceilings, bought on the premium.
     */
    public record Point(double ordinaryNone, double college, double university) {

        /** The no-diploma arrivals who come at the going rate for this band, per diploma arrival: ordinaryNone for the unskilled band, nothing for any other. */
        public double ordinary(WageBand band) {
            return band == WageBand.NONE ? ordinaryNone : 0;
        }

        /** The ceiling of this band's bought arrivals, relative to the diploma band's 1: the mix's own for the graduate bands, WageBand's for the others. */
        public double ceiling(WageBand band) {
            return switch (band) {
                case COLLEGE -> college;
                case UNIVERSITY -> university;
                default -> band.arrivalCeiling();
            };
        }
    }

    /* ===== TODAY'S MIX, FROM WageBand'S OWN CEILINGS ===== */

    /**
     * The sum of the ceilings the diploma and graduate bands have today: 1 + .35 + .12 = 1.47. A working month's arrivals at the ceilings are
     * the diploma band's 1 over this, the college band's .35 over it and the university band's .12 over it (the no-diploma band is bought on top).
     */
    private static final double TODAY_SUM = WageBand.DIPLOMA.arrivalCeiling() + WageBand.COLLEGE.arrivalCeiling() + WageBand.UNIVERSITY.arrivalCeiling();

    /** Today's mix: no ordinary arrivals without a diploma, the graduate bands at WageBand's ceilings. The mix from the last knot's January on, and the one Migration runs on when it holds none. */
    public static final Point TODAY = new Point(0, WageBand.COLLEGE.arrivalCeiling(), WageBand.UNIVERSITY.arrivalCeiling());

    /* ===== THE KNOTS: each figure is one line to change ===== */

    /**
     * The year's mix, in year order. The first knot is used before its January, the last never (TODAY is, from its January on). Each share is
     * of the year's working arrivals, summing to one, and the arithmetic is in the note.
     */
    public static final List<Knot> KNOTS = List.of(
            new Knot(1901, .73066, .23184, .03, .0075, "CYB1913", true,
                    "1913's: the boom's arrivals, the nearest year read (1901-1911's tables exist, not read)"),
            new Knot(1913, .73066, .23184, .03, .0075, "CYB1913", true,
                    "FY1912-13's 238,779 adult men: farmers and farm labourers 31.5%, general labourers 39.3%, miners 2.0% no diploma (72.8); "
                    + "mechanics 16.5%, clerks and traders 6.6% a trade (23.1); unclassified 4.1% pro rata; the table has no professional class, "
                    + "so college 3% and university .75% are 1901's adults (est.: degrees .56-.93% of adults, the professions with and without "
                    + "the clergy, B1/V1911; college teachers, nurses and clerks 3-4%), the rest .9625 split 72.8:23.1"),
            new Knot(1953, .47453, .42857, .06056, .03634, "HSC-A351", true,
                    "91,133 working arrivals: service 15.1, agriculture 18.9, logging and fishing .5, mining .5, labourers 11.4, other 1.1 no diploma "
                    + "(47.5); managerial 1.3, clerical 7.0, transport 2.0, commercial 3.5, manufacturing, mechanical and construction 29.1 a trade "
                    + "(42.9); professional 9.7 split college:university as the 1951 census's adults, 5.5:3.3 (V1951); over 100.1"),
            new Knot(2021, 0, WageBand.DIPLOMA.arrivalCeiling() / TODAY_SUM, WageBand.COLLEGE.arrivalCeiling() / TODAY_SUM,
                    WageBand.UNIVERSITY.arrivalCeiling() / TODAY_SUM, "WageBand", false,
                    "today's model: the diploma band 1, the college band .35 and the university band .12 of a working month's arrivals, over 1.47 "
                    + "(1 + .35 + .12); the no-diploma band is bought on top of them, so its share here is none"));

    static {
        if (KNOTS.isEmpty()) throw new IllegalStateException("KNOTS is empty");
        for (int i = 1; i < KNOTS.size(); i++) {
            if (KNOTS.get(i).year() <= KNOTS.get(i - 1).year()) {
                throw new IllegalStateException("KNOTS year " + KNOTS.get(i).year() + " does not follow " + KNOTS.get(i - 1).year());
            }
        }
    }

    /* ===== THE LOOK-UP ===== */

    /**
     * The game month of January of a year (month 1 is January 1900, CityCalendar): CityCalendar has no reverse function, deliberately, and this
     * is the one place a year is turned back into a month; ArrivalMixCheck asserts it against yearOf() and monthOfYear().
     */
    public static int januaryOf(int year) {
        return (year - CityCalendar.EPOCH_YEAR) * 12 + 1;
    }

    /**
     * The three figures a year's shares give, relative to the diploma band's 1. The one place the shares become a Point.
     *
     * @throws IllegalArgumentException when the diploma share is not above zero, since the others are stated against it
     */
    public static Point derive(double none, double diploma, double college, double university) {
        if (!(diploma > 0)) throw new IllegalArgumentException("a mix with no diploma-holders cannot be stated against the diploma band: " + diploma);
        return new Point(none / diploma, college / diploma, university / diploma);
    }

    /** The point a knot's own shares give. */
    public static Point of(Knot knot) {
        return derive(knot.none(), knot.diploma(), knot.college(), knot.university());
    }

    /**
     * The mix of a game month's arrivals. The first knot's before its January; between two knots the SHARES interpolated linearly by month from
     * one January to the next, then derived; TODAY itself (the constants, not derived) from the last knot's January on. No state, and the same
     * month always gives the same point.
     */
    public static Point at(int month) {
        Knot first = KNOTS.get(0), last = KNOTS.get(KNOTS.size() - 1);
        if (month < januaryOf(first.year())) return of(first);
        if (month >= januaryOf(last.year())) return TODAY;
        for (int i = 1; i < KNOTS.size(); i++) {
            Knot to = KNOTS.get(i);
            int end = januaryOf(to.year());
            if (month >= end) continue;
            Knot from = KNOTS.get(i - 1);
            int start = januaryOf(from.year());
            double t = (month - start) / (double) (end - start);
            return derive(
                    lerp(from.none(), to.none(), t),
                    lerp(from.diploma(), to.diploma(), t),
                    lerp(from.college(), to.college(), t),
                    lerp(from.university(), to.university(), t));
        }
        return TODAY;   // unreachable: the month is under the last knot's January, so some knot ends after it
    }

    /** a at t = 0, b at t = 1, a straight line between. */
    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
