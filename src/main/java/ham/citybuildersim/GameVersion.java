package ham.citybuildersim;

/**
 * What build this is, and what shape its saves are.
 *
 * TWO NUMBERS, ON PURPOSE
 *
 * VERSION is for people: it goes in the window title and in every save, so a
 * bug report or a broken save says which build produced it. Bump it whenever
 * something ships.
 *
 * SAVE_FORMAT is for the loader, and it changes far more rarely - only when the
 * save's SHAPE changes in a way an older build could not read correctly. It
 * exists to catch one specific accident: opening a save from a newer build in
 * an older one. Without it the older build reads the fields it recognises,
 * silently ignores the rest, and hands back a city missing whatever the newer
 * build added - which looks like a working load right up until something is
 * quietly gone.
 *
 * Older saves are still read. That direction is safe: every field added since
 * has a sensible default, and the load path already handles the pre-slot,
 * pre-flow and pre-land formats.
 *
 * KEEPING IT IN SYNC
 *
 * VERSION also appears in "Build EXE.bat" as APPVER, because jpackage stamps it
 * into the exe and cannot read it from here. Two places, and this is the one
 * that matters - the other only affects the file properties dialog.
 */
public final class GameVersion {

    /** Bump on release. Matches APPVER in Build EXE.bat. */
    public static final String VERSION = "1.0.0";

    /**
     * The save shape.
     *
     * 1 - the original flat save
     * 2 - land, and both tax rates
     * 3 - construction keyed by template id; the month's flows carried
     * 4 - numbered slots, names, and this stamp
     * 5 - the month's income statements carried whole, plus the utilisation
     *     they were written against. Roads needed nothing of their own: road
     *     capacity and road load are both pure functions of the building stock.
     * 6 - the land office's listing, iron deposits and reserves, the ore price,
     *     the mining sector's books, and the construction subsidy
     * 7 - parcels carry a DEPOSIT COUNT, so the listing is five fields per plot
     *     behind a marker instead of four. Reading downward is unaffected - a
     *     format-6 listing still loads, with any ore counting as one site, which
     *     is exactly what it meant. Upward is what this number is for: a build
     *     that only knows four-wide rows sees a length that divides by neither
     *     shape, throws the whole listing away, and silently hands the player
     *     ten new plots in place of the tract they were saving up for.
     * 8 - policy: the two city rates plus every wage-band and per-sector offset,
     *     which sectors the city has undertaken to subsidise, and the month's
     *     VAT ledger. An older build reads none of these and would hand back a
     *     city with one flat rate everywhere and nothing protected - a load that
     *     looks perfectly successful right up until the sectors start shrinking.
     * 9 - the national accounts carry the inventory baseline in UNITS plus the
     *     construction backlog, because GDP measures the change in stock as a
     *     volume now rather than as a value. A format-8 save has neither, so it
     *     restores with the baseline marked unknown and skips one month's
     *     inventory term - which costs a month's accuracy instead of booking an
     *     entire existing warehouse as that month's production.
     * 10 - the build log, the other half of the demolition log. An older build
     *     reads none of it and hands back a city that appears never to have
     *     finished anything, which is harmless in itself - but the same save
     *     also carries everything 9 added, and THAT is what this number is
     *     guarding. Downward is fine as always: a format-9 save has no build
     *     log, Gson reads the field as null, and BuildLog.restore() takes null
     *     as an empty log rather than an error.
     * 11 - the twelve months of per-tier wage history behind migration's decline
     *     test, and with it the fact that the AGE PYRAMID IS NOW THE POPULATION
     *     rather than a display. A format-10 save carries the pyramid already,
     *     so it loads with its residents intact; what it lacks is the history,
     *     so Migration.restore() refuses the null and the city starts with a
     *     clean slate - meaning it cannot shed anybody for a year even if a tier
     *     was already dying when it was saved. That is a real difference and
     *     this number is what records it, rather than it being discovered.
     * 12 - the history a reload could not rebuild, found in a deliberate audit:
     *     each sector's consecutive-loss streak (retirement needs six in a row,
     *     so forgetting it across a load reset the clock and made scrapping
     *     capacity save-scummable), the twelve-month population trend the
     *     private planners forecast from, and the three accumulators that fill
     *     up during the PLAYER'S TURN - capital spending, material imports and
     *     materials consumed - which next month's national accounts read. Also
     *     appends lastMigration to the pyramid array.
     *
     *     Downward is fine and matters more than usual here. A format-11 save
     *     restores with empty streaks and an empty trend, which is what those
     *     saves already behaved as; and PopulationCohorts.restore() accepts BOTH
     *     the old and new array lengths rather than refusing, because that array
     *     is the population now and refusing it would hand back an empty city.
     * 13 - sickness. The city's health state - the outbreak currently decaying,
     *     the coverage and the sick rate the month's statements were throttled
     *     by - plus a fourth entry in the ratio basis.
     *
     *     This is exactly the direction the number exists for. The outbreak roll
     *     is a pure function of the month, so a build that does not read the
     *     health array does not re-roll the epidemic the save was taken in the
     *     middle of - it simply walks out of it, at full output, and looks
     *     entirely well while doing so. Nothing on screen would say anything was
     *     lost.
     *
     *     Downward is fine and needed no work at all: the health array is null
     *     in an older save and Health.restore() takes null as "start well",
     *     while the fourth ratio basis defaults to 1, which is precisely what
     *     every city before this ran at.
     * 14 - the health service's own state: plots consumed, the unburied
     *     backlog, and the month's bill and fees.
     *
     *     The two STOCKS are why this number moved. Plots used and the backlog
     *     of dead nobody could deal with are both permanent facts about a city
     *     that nothing can reconstruct by looking at it - a build that ignored
     *     them would empty the graveyards, hand the player a cemetery that
     *     never fills, and clear an epidemic the city had created for itself,
     *     all while looking like a clean load.
     *
     *     Downward is fine: Healthcare.restore() refuses a null array whole, so
     *     a format-13 city loads with empty graveyards and no backlog, which is
     *     precisely what those cities were.
     * 15 - the health service's array grew by two: the plots ever built and the
     *     crematoria's monthly throughput, so the death-care panel can say how
     *     tight the month actually was rather than how tight it looks now.
     *
     *     Small, and the number still moves, because Healthcare.restore()
     *     refuses a wrong-length array WHOLE. A format-14 save carries ten
     *     entries where this build wants twelve, so it is refused - and a
     *     refused Healthcare is a city with empty graveyards, which for a save
     *     that HAS filled graves is exactly the silent loss SAVE_FORMAT exists
     *     to make loud.
     */
    public static final int SAVE_FORMAT = 15;

    public static final String NAME = "CityBuilderSim";

    private GameVersion() { }

    /** For the window title. */
    public static String title() {
        return NAME + " " + VERSION;
    }

    /**
     * True when a save claims a format this build does not know how to read.
     *
     * Deliberately not "!=". A save older than this build is fine and common;
     * only the future direction is dangerous.
     */
    public static boolean isFromNewerBuild(int saveFormat) {
        return saveFormat > SAVE_FORMAT;
    }
}
