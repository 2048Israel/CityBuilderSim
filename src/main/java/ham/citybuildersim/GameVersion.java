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
    public static final String VERSION = "0.4.3";

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
     *
     * NOT 16: two new roads (ids 29 and 30).
     *
     *     Adding a building is deliberately NOT a format change, and it is
     *     worth writing down why, because it looks like one. The buildings array
     *     in a save is keyed by template id and sized to the highest id that
     *     existed when it was written, and loadBuildings() already reads a short
     *     array as zeros past its end. So a format-15 city written before the
     *     Gravel Road existed loads into this build with no gravel roads - which
     *     is exactly what that city had.
     *
     *     What WOULD move this number is retiring or renumbering an id, because
     *     that silently loads the wrong buildings into the wrong slots. Renaming
     *     one does not: "Road Network" became "Paved Road" and kept id 13, and
     *     every save that owns them still owns them.
     *
     * NOT 16 EITHER: the graph history went from 8 series to 23.
     *
     *     The history is its own file beside the save, and its format is the
     *     FIELD NAMES of HistorySave - Gson matches by name. So a history
     *     written before a series existed loads into this build with that list
     *     empty, and a history written by this build loads into an older one,
     *     which ignores what it does not recognise. Both directions work, so
     *     neither is a break.
     *
     *     The care needed is not in the version number but in the READING: a
     *     series shorter than the month axis was not being recorded yet, so it
     *     describes the END of the city's life and not the start.
     *     HistorySave.aligned() pads it at the front with NaN, and HistoryCheck
     *     strips a series out of a real history file to prove it - because
     *     getting that backwards draws last decade's data over the founding
     *     years and produces a graph that looks entirely correct.
     * 16 - the labour market: what the city pays, and who it has to pay.
     *
     *     TWO THINGS, AND BOTH ARE THE SAME RULE. A wage is now a damped price
     *     rather than a constant, so today's figure is the result of every
     *     month of scarcity the city has lived through - rebuild it from the
     *     posts and workers a month ended with and you get the TARGET the live
     *     city was still walking toward, not the wage it was actually paying.
     *     Measured as a $52 gap in next month's income before it was carried.
     *
     *     And the skilled workforce is a pure stock. Until schools exist a
     *     skill arrives in somebody's head and leaves the same way, so the
     *     count is the entire history of who has moved to this city, and
     *     nothing in a closing balance reproduces it.
     *
     *     Downward is handled, and is the interesting half: a format-15 city
     *     has a workforce and no record of what any of them can do. Resetting
     *     them all to unskilled would shut every hospital in the city on load
     *     for no reason the player could see, so the skills are INFERRED from
     *     the posts those workers are demonstrably filling - the only reading
     *     that leaves the city exactly as it was left. The wages fall back to
     *     PayTier, which is what every city before this was paying anyway.
     *
     * 17 - schools: who is licensed to practise what, and what the city has
     *     taught.
     *
     *     A MEDICAL LICENCE IS A STOCK and the most expensive one in the game -
     *     seven years of somebody's life - so nothing in a closing balance
     *     reproduces it. A save that forgot it would reload a city whose
     *     hospitals had no doctors and whose medical school had apparently
     *     never graduated anybody, all of whom were there the moment before.
     *
     *     Downward is the interesting half again, and it is gentler than 16
     *     was: a format-16 city has skilled workers and no licences, which is
     *     exactly what a city with no schools looked like before this - the
     *     gated posts empty and the doctors imported. So the licences restore
     *     as zero and the city carries on, except that the doctors it was
     *     staffing out of its general graduate pool now correctly cannot be.
     *     That is a real change to a loaded city and it is the right one: those
     *     doctors were the bug.
     */
    /* ---------------------------------------------------------------------
     * 18  Education carries its pipeline (2026-09-06). Until now `Education`
     *     handed a school's steady-state throughput out the month the school
     *     opened; there were no students in flight, so there was nothing to
     *     save. Now every adult course keeps a queue of cohorts by months
     *     remaining, and the students are out of the labour supply until they
     *     graduate. Both facts live in the education state array, which grew
     *     by the sum of the adult course lengths.
     *
     *     Also carried from this format: the dollars the stores paid the mills
     *     for local food (the mills book that cheque rather than units x
     *     today's price), and the sales-tax ledger's import charges.
     *
     *     Downward: a format-17 city loads with nobody in any course. Its
     *     schools fill from empty and graduate nobody for a course length,
     *     which is what they would have done had they been built the month
     *     the save was made. The old ledger and flow shapes are read with the
     *     new fields at zero.
     */
    /* ---------------------------------------------------------------------
     * NOT 20: the inbox, and thirty-one new graph series (2026-09-08).
     *
     *     Both are additions that fail SAFELY in both directions, which is the
     *     test this number exists for - and it is worth writing down, because
     *     they look like format changes and are not.
     *
     *     The inbox is a list of notices under a new key in the save. Gson
     *     leaves a field alone when the JSON has no key for it, so a format-19
     *     city loads here with an empty inbox - and an empty inbox is CORRECT
     *     for that city: nothing had ever been said to it. Whatever is still
     *     wrong with it says so again next month, because the notices are
     *     raised from the city's own conditions rather than restored from a
     *     record of them. Nothing is silently gone, which is the only thing
     *     this number guards against. Upward, an older build ignores the key
     *     and shows the four banners it already had.
     *
     *     What WOULD move this number is a notice carrying state the city
     *     cannot reproduce - a decision the player made inside a notice, say.
     *     Today the only unreproducible thing in there is whether a notice had
     *     been read, and a warning that reappears unread is a nuisance rather
     *     than a loss.
     *
     *     The graph series are the same argument as "NOT 16 EITHER" above, and
     *     for exactly the same reason: the history is its own file and its
     *     format is the field names of HistorySave.
     *
     * NOT 20: the four figures the government's books were struck with.
     *
     *     struckCapitalSpending, struckLandSales, struckLandPurchases and
     *     struckInterest joined the save so a reloaded city's Government screen
     *     shows the month it actually played. Gson leaves a missing field alone,
     *     so a format-19 city loads with four zeros - and four zeros is exactly
     *     what that city's Government screen showed anyway, because the load
     *     path used to rebuild the block from accumulators that were empty by
     *     then. Nothing is silently lost, which is the only thing this number
     *     guards against, and one press of Next Month fills all four in.
     *
     * NOT 20 EITHER: everything else finding #15 asked to be carried.
     *
     *     The residents' whole statement, the subsidy the dial paid, what savers
     *     were paid, the schools' month, the hunger inside the sick rate, and
     *     the two counters that say WHY a household is doubled up. Six new
     *     fields and three grown arrays, all on the same argument as above and
     *     all in the same shape:
     *
     *       - the new DataSave fields (householdStatement, subsidyPaid,
     *         bankDepositRate) are absent from an older save, and Gson leaves an
     *         absent field alone, so an older city loads with exactly the blank
     *         it always had
     *       - the three arrays that GREW - Health's, Education's and
     *         FamilyModel's - accept BOTH lengths on purpose, which is the one
     *         place this codebase relaxes "refuse a wrong length whole". Refusing
     *         a short array here would throw away a real outbreak, a real cohort
     *         in flight and a real household mix in order to gain a figure those
     *         saves never had, which is the opposite of what that rule is for.
     *         Anything that is NEITHER length is still refused whole.
     *
     *     So an old save loses nothing and one month fills all of it in. What
     *     WOULD move this number is any of those arrays changing shape in the
     *     middle rather than growing at the end, because that silently reads
     *     one figure into another's line.
     *
     * AND NOT 20 FOR THE 2026-09-10 AUDIT BATCH, on the same argument. Five
     * more things carried, every one of them a value an older save was already
     * reading blank or re-deriving wrongly on load, and every one absent-safe:
     *
     *       - the borrower's record (restructureCounts, blockedMonths): absent
     *         reads as a clean record, which is what every load read before
     *       - the rate the economy traded at (tradedExchangeRate): zero falls
     *         back to the product the month would have struck
     *       - the land office's prices (landMarketPrices): absent keeps the
     *         founding seeds, as before
     *       - the world's thirteen-month level ring and the labour market's
     *         diagnostics: both APPENDED to arrays that accept either length
     *       - the bank's lifetime resolution loss now lives in the slot the
     *         monthly one used to be saved in; an old save restores whatever
     *         its save month held, which is what it always did
     * --------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------
     * 20  The unit of construction material changed meaning (2026-09-10).
     *
     *     NOTHING WAS ADDED TO THE SAVE. This number moves because a field
     *     that was already there - the yard's stock of construction material,
     *     `constructionMaterials`, plus the two monthly counts beside it -
     *     is a COUNT OF UNITS, and a unit was $2,000 through format 19 and is
     *     $18,000 from here (BuildingManager.MATERIALS_WORLD_PRICE; every
     *     template's count was re-derived with it, totals held). A format-19
     *     yard read as it stands is therefore nine times the material it
     *     was. Not lost - the opposite: 2,000 units that were $4M of
     *     aggregate silently become $36M of it, and the city's next order is
     *     free. That is exactly the kind of quiet misreading this number
     *     exists to make loud, even though it is a windfall rather than a
     *     loss.
     *
     *     Downward is handled: Game reads a format-19 yard at what it was
     *     WORTH - the old count times MATERIALS_UNIT_BEFORE_20, divided by
     *     today's unit - and the same for the month's import and consumption
     *     counts. A format-19 city loads with the same value of material in
     *     its yard that it saved with. Upward, an older build refuses a
     *     format-20 save, which is right: it would read the count in its own
     *     unit and give the city a ninth of its yard.
     *
     *     The BUILDINGS in a save are unaffected. They are counts of
     *     templates, and a template's cost is read from today's catalogue
     *     when it is valued, as it was through the two rebalances before
     *     this - a city's power plant did not need a format change to become
     *     a $1.43B power plant.
     * --------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------
     * NOT 21: the sectors' savings abroad (2026-09-10, OutwardInvestment).
     *
     *     A new array under a new key (outwardInvestment), two figures
     *     appended to the foreign accounts' array (slots 20 and 21), and three
     *     fields added to SectorBooks' month record, which Gson matches by
     *     name. All absent-safe: an older save loads with nothing abroad, a
     *     financial account of zero that settles within a year, and sector
     *     books that show no foreign line - which is exactly what that city
     *     had, because the mechanic did not exist when it was saved.
     * --------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------
     * NOT 21 EITHER: the households' cells and the share register
     * (2026-09-10, evening). Two new keys (householdCellKeys/householdCells,
     * equityKeys/equity), both named entry by entry so a shape or a company
     * added later cannot read one's figures into another's; the household
     * row array still written as the sum of the cells for an older build;
     * two fields appended to SectorBooks' month record (equityRaised,
     * dividendsPaid) which Gson matches by name. All absent-safe: an older
     * save loads with its rows seeded into the cells and nobody owning
     * anything, which is what that city had.
     * --------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------
     * NOR THIS: the exchange and the households' dollars abroad (2026-09-11).
     * One new key (exchange: the quote, fair value and the unfilled demand
     * per company, by the register's names, then the lifetime volume), two
     * slots appended to the register's per-company block (the desk's
     * inventory and its dividend), one slot appended to each household
     * cell's block (dollars abroad), a field on SectorBooks' month record
     * (sharesBoughtBack). Every one of them is read by name or by a length
     * the reader recognises, and every older length restores what it
     * carries: the evening's save loads with the desk empty, the quote at
     * fair value and nobody holding a dollar abroad, which is what that
     * city had. Later the same day: a fourth slot per company in the
     * exchange's block (the split factor - three a company still restores,
     * with one share then being one share now), and two maps of series in
     * the graph history (a share price and a share value per company, by
     * name), which Gson leaves empty on a history written before them.
     * --------------------------------------------------------------------- */
    public static final int SAVE_FORMAT = 20;

    /** What a unit of construction material cost through save format 19, in thousands. */
    public static final double MATERIALS_UNIT_BEFORE_20 = 2;

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
