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
 * THIS IS THE ONLY PLACE IT IS WRITTEN (2026-09-14). "Build EXE.bat" reads the
 * line below with findstr and sets APPVER from it, so jpackage stamps the exe
 * with whatever is here and cannot disagree.
 *
 * It used to be typed in both, with a note here asking whoever changed one to
 * remember the other. That is a hope rather than a mechanism, and it went out
 * of step the first time it mattered - 0.5.1 in the source, 0.5.0 on the exe.
 * What the parse depends on: one line declaring the field, with a quoted
 * literal ending in a semicolon. findstr matches on the declaration keywords
 * through the equals sign rather than on the field name alone, which is why
 * prose here can mention VERSION without being taken for the declaration - an
 * earlier wording of this very comment was a second match, and survived only
 * because it happened to carry no equals sign.
 */
public final class GameVersion {

    /**
     * Bump on release. Build EXE.bat reads this line for APPVER.
     *
     * 0.6.0 (2026-09-15) - THE THIRTEEN GOODS. 0.5.15 stood while seven batches
     * shipped under it, which is what a version number is for and is not what
     * this one was doing: the sixth age band and save format 27, the children
     * who follow a parent out of work, the consumption model, the exchange-rate
     * units fix, the thirteen foods going live, FOOD's retirement, and a cost
     * model that can price a line making two things. The goods economy is a
     * bigger change than the clock was, and the clock took 0.4.4 to 0.5.0.
     *
     * 0.6.5 (2026-09-17) - TRANSPORT, AND THE THINGS THAT MOVE ON IT. Nine
     * batches under 0.6.0 and every one of them about the same subject: the
     * freight band decomposed into a world price and a cost of moving, the
     * road's one load split into commuters, goods and bulk, the three modes
     * and the fare, the railway as the twelfth sector, the automobile industry
     * as the thirteenth, households buying cars and the road getting its
     * teeth, and the sectors buying the lorries they move things with. Then
     * the screen that finally shows a player any of it.
     *
     * Two whole industries, a fifth operating ratio, the first durable a
     * household in this game has ever been able to own, and a tab. Five points
     * rather than one: 0.6.1 through 0.6.4 would each have been a fair release
     * and none of them shipped on its own.
     *
     * SAVE_FORMAT did not move for any of it. Every field added since 0.6.0 is
     * keyed by name or appended to the end of an array whose reader checks its
     * length, so a 0.6.0 city opens in 0.6.5 with no cars, no fleets and the
     * rolling stock its track implies - which is exactly what that city was.
     *
     * 0.6.6 - THE FOURTEENTH SECTOR, AND A RIDE STOPS BEING A MONTH.
     *
     * Luxury Retail: boutiques and department stores that import what they
     * sell, strike their margin against the queue at the door, and give the
     * household savings a second place to go besides the shelf. The city's
     * net worth stops diverging and starts oscillating, which is the whole
     * reason it was built.
     *
     * And the fare. It was charged ONCE per rider per month against a constant
     * that says "a single journey", so a monthly pass cost $2.50 and the buses
     * could not have paid for themselves at any fare a player would set. Forty
     * journeys a month now - out and back, twenty days - which is most of the
     * transit loss the todo list has been carrying as a balance problem.
     *
     * SAVE_FORMAT did not move for either. The sector is keyed by name and the
     * accounts' luxury baseline is appended to an array whose reader checks
     * its length, so a 0.6.5 city opens with no boutiques and an empty shelf -
     * which is exactly what that city was.
     *
     * 0.6.7 - THE FIFTEENTH SECTOR, AND A MEAL OUT IS FOOD.
     *
     * Restaurants, the other half of what Jerus asked for beside the luxury
     * shops, and the half with a rule attached: *a meal out REPLACES
     * groceries*. So they import nothing. A dinner is the same thirteen foods
     * off the same shelf at the same prices, which makes the sector a SECOND
     * DOOR to a supply the city is already short of rather than an addition to
     * it - and a second buyer in that market, which is what makes the basket
     * dearer. A meal is one ninetieth of a person-month, three a day and
     * thirty days, and the hunger measure counts it at what a person-month of
     * food costs rather than at what the kitchen charged for it.
     *
     * SAVE_FORMAT did not move. The sector is keyed by name and the dinners a
     * household ate are appended to a cell array whose reader checks its
     * length, so a 0.6.6 city opens with no kitchens and a city that ate in -
     * which is exactly what that city was.
     *
     * 0.6.8 (2026-09-19) - THE CLINIC HAS A PRICE, AND THE TREASURY OPENS ITS
     * LAST ROW.
     *
     * Jerus: "healthcare should be an adjustable price, all the way to even
     * make it a profitable business or the option to make it an obligatory
     * insurance payment system." Two dials on the policy, in the EI premium's
     * shape, and a Health page under Promises to set them: a scale on the
     * three care fees, 0 to 15 with the city's own break-even said on the
     * screen, and a premium off every wage into the treasury, employee side,
     * with nothing balancing it. And his rule for the household that cannot
     * pay the fee after its savings, its shares and its credit: it goes
     * without care, not without food - the share of its people the clinic
     * turns away is the share of its bill that would have come out of the
     * basket, and they are unserved the way people with no clinic are, in the
     * sick rate, the swings and the births. Beside it, two things the screens
     * owed him: the treasury bridge's "everything else" row opens into the
     * month's movements by name, from a journal kept where the cash moves,
     * and the bank's trading desk shows the re-mark of what it holds as a
     * line, so the desk's lines foot to its result and a loss says which
     * half of it was the marking.
     *
     * SAVE_FORMAT did not move. The two dials ride the end of the policy
     * array, the share of a household that paid for care rides the end of the
     * cell array, the full-service bill and the three coverages the end of
     * the service's, the premium the end of the accounts' and the statement's,
     * and the journal goes under its own key - every reader checks its
     * length, so a 0.6.7 city opens at the founding fee with no premium,
     * everybody paid, and a treasury row with nothing yet to open - which is
     * exactly what that city was.
     *
     * 0.6.9 (2026-09-21) - THE PRICE OF A PLACE.
     *
     * Jerus: "yes grants and government tuition are in the game, but what
     * about more granularity, so perhaps, grants its just a menu where you
     * can choose between a fixed amount, or a percentage of last month's
     * surplus, or a % as it is now of living costs, or a % of tuition. and
     * then another slider which is the interest rate for the student loans,
     * and idk if real life is like that but have it so that the money is
     * withdrawn from the treasury and then later when they pay it back it's
     * added back, and you get the interest if there is any. ... and also
     * make it so that you can tweak the price of tuition as well." Three
     * dials, on a Schools page under Promises where the Tuition page was,
     * beside the subsidy it already had: the price of a place, a scale on the
     * founding tuition table from free to five times it; the student grant,
     * now a basis and an amount - a share of the unskilled wage as it always
     * was, a fixed sum, a share of last month's surplus as one pool, or a
     * share of each student's own tuition - struck by one rule,
     * TaxPolicy.grantBill, that the treasury's bill, the save's re-strike and
     * the page all call; and a rate on the student loan. The rate is the
     * Canadian shape: nothing accrues while they study, a graduate's balance
     * is charged it during repayment, and it is paid with the instalment, so
     * the balance is only ever principal and falls exactly as it did. The
     * interest is the treasury's own revenue line, beside the premiums; the
     * principal keeps coming back through the bridge. And one the load path
     * had missed since the schools had books: a reloaded city with schools
     * read its education bill and its fees as nothing until its first month.
     *
     * SAVE_FORMAT did not move. The four dials ride the end of the policy
     * array, the interest line the end of the accounts' government block,
     * and its monthly series goes under its own key - every reader checks
     * its length, so a 0.6.8 city opens on the founding basis at its own
     * share of the wage, no interest and the founding price - which is
     * exactly what that city was.
     *
     * 0.6.10 (2026-09-21) - THE INSTRUMENTS.
     *
     * Jerus, reading his own city's year book - prices 399 times founding in
     * twenty-five years, the currency at its guard: "i think we should make
     * it so that of the 3.5B you start with, 1B is in usd in the reserve, so
     * you only see 2.5B start with... i think that greatly helps, since 99%
     * players wont add to reserves most probably cause they have no clue."
     * And: "perhaps also a number visible on the screen showing both the
     * price index and current inflation year on year ... and a proper
     * exchange rate which tells you how many your coins equals USD." Three
     * things. The top strip carries prices against founding with inflation
     * year on year under them, and the rate both ways - US$1 = D$x, D$1 =
     * US cents or dollars - each coloured by how far it has drifted from the
     * target and from parity. The founding endowment is the same $3.5B,
     * split: D$2.5B in the treasury and US$1B bought into the vault on day
     * one. And the monetary page names both figures when the rule would set a
     * rate the dial cannot reach.
     *
     * And the bug found checking the first: THE VAULT WAS HELD IN THE WRONG
     * CURRENCY. It was a local figure at the price paid, so when the currency
     * fell a hundredfold a US$1B vault read US$10M and its import cover fell
     * a hundredfold with it, exactly when it was needed - while the dollar
     * debt beside it was revalued every month. The vault is kept in dollars
     * now; its local value is those dollars at today's rate, and the move is
     * a revaluation line beside it, not cash and not an audit flow.
     *
     * And the rule the founding reserve needed. Jerus: "a reserve defends a
     * currency; it does not hold one down." The vault absorbed the pressure on
     * the rate both ways, so a deep one muted the surplus and the policy rate
     * - the two forces that pull a currency back out of an inflation spiral -
     * and with the founders' dollars in it, three of the ensemble's eight
     * seeds reproduced the year book that started this: two past 140x
     * founding with the currency at its guard, one to 11x. It damps a push
     * weaker only
     * now, and no seed's prices swung more than 2.81x, against 7.13x at the
     * worst before any of this went in.
     *
     * SAVE_FORMAT did not move. Slot 19 of the foreign accounts keeps its
     * meaning - the vault's local value when saved, so an older build reads
     * what it always read - and the dollars and the month's revaluation ride
     * the end of the array, whose reader checks its length. A 0.6.9 city
     * opens with its vault at the rate it was saved at, which is the only
     * rate it has, and without the founders' dollars it never had - which is
     * exactly what that city was.
     */
    public static final String VERSION = "0.6.10";

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
     * ---------------------------------------------------------------------
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
     * ---------------------------------------------------------------------
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
     * ---------------------------------------------------------------------
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
     * ---------------------------------------------------------------------
     * NOT 21: the sectors' savings abroad (2026-09-10, OutwardInvestment).
     *
     *     A new array under a new key (outwardInvestment), two figures
     *     appended to the foreign accounts' array (slots 20 and 21), and three
     *     fields added to SectorBooks' month record, which Gson matches by
     *     name. All absent-safe: an older save loads with nothing abroad, a
     *     financial account of zero that settles within a year, and sector
     *     books that show no foreign line - which is exactly what that city
     *     had, because the mechanic did not exist when it was saved.
     * ---------------------------------------------------------------------
     * NOT 21 EITHER: the households' cells and the share register
     * (2026-09-10, evening). Two new keys (householdCellKeys/householdCells,
     * equityKeys/equity), both named entry by entry so a shape or a company
     * added later cannot read one's figures into another's; the household
     * row array still written as the sum of the cells for an older build;
     * two fields appended to SectorBooks' month record (equityRaised,
     * dividendsPaid) which Gson matches by name. All absent-safe: an older
     * save loads with its rows seeded into the cells and nobody owning
     * anything, which is what that city had.
     * ---------------------------------------------------------------------
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
     * ---------------------------------------------------------------------
     * 21  THE SECTOR TEMPLATE (2026-09-11), and the first CLEAN BREAK.
     *
     *     Every sector is one class over one template now (see Sector and
     *     Sectors), every good it trades is a market (Good, GoodsMarket,
     *     Markets), and a save carries each sector whole, by name - its
     *     cash, its stocks, the month in progress, the month last struck,
     *     the three bills of the month, and whatever state is its own
     *     (SectorState) - plus every market's price and stock (Markets.State),
     *     the VAT ledger by sector name, the policy offsets by sector name,
     *     the protected sectors by name and what each was paid.
     *
     *     What went: five differently-shaped report arrays, three arrays
     *     indexed by BuildingType.ordinal(), the construction books' four
     *     loose fields, the retail flows, the ratio basis, the rent and
     *     shelf prices carried one by one. None of that has a reader any
     *     more, and there is nothing in a format-20 save this build can
     *     make a sector out of.
     *
     *     SO THIS IS THE ONE FORMAT OLDER SAVES DO NOT CROSS. Jerus, asked:
     *     "clean break." Every format before this walked forward - a missing
     *     field read as the blank that city already had. A format-20 city
     *     read here would load with seven empty businesses and a yard, which
     *     is not that city and not a blank either. So Game refuses it with a
     *     sentence that says why (isFromBeforeSectors), the same way it
     *     refuses a save from a newer build. Upward is as before: a
     *     format-20 build refuses a 21.
     *
     *     Also with this format: the construction-material unit settled at
     *     $18,000 for good (MATERIALS_UNIT_BEFORE_20 is gone with the
     *     format-19 reader), and the seventh sector, Materials.
     *
     * 22 - THE EIGHTH SECTOR: Business Services, and the three goods the world
     *     pays for. A format-21 city loads and owns none of it, which is true
     *     of that city.
     *
     *     The number is here because the eighth sector CHANGES THE SHAPE OF
     *     THE HOUSEHOLD SAVE. Equity.COMPANIES is the sectors plus the bank,
     *     the households' share block is as wide as that list, and every slot
     *     after it moves: eighteen a cell meant "the whole array" at seven
     *     sectors and means "the array before student debt" at eight.
     *
     *     A bump does NOT fix that on its own - older saves always load, so
     *     nothing would have been refused. HouseholdBalance.restoreCells()
     *     computes its widths from the company list the save was WRITTEN with
     *     (DataSave.getEquityKeys) and maps holdings BY NAME. This number is
     *     the record of why, not the mechanism.
     *
     * 23 - THE NINTH SECTOR: Manufacturing, the two goods it makes out of the
     *     city's steel, and the import ceiling steel got the day something
     *     here started buying it. A format-22 city loads and owns none of it,
     *     which is true of that city.
     *
     *     Here for the same reason 22 is: the ninth sector CHANGES THE SHAPE
     *     OF THE HOUSEHOLD SAVE. Equity.COMPANIES is the sectors plus the
     *     bank, the households' share block is as wide as that list, and every
     *     slot after it moves again - nine holdings a cell at eight sectors,
     *     ten at nine. The mechanism is unchanged and still does the work:
     *     restoreCells() reads its widths off the company list the save was
     *     written with and maps holdings BY NAME, so a format-22 city loads
     *     with its nine read into the right nine names and no Manufacturing
     *     shares, which is exactly what that city owned.
     *
     *     THE STEEL PRICE IS NOT A SAVE CONCERN, though it looks like one. A
     *     save carries the local price of every market it had and restores it
     *     rather than recomputing it (GoodsMarket.setLocalPrice); a ceiling
     *     changes what the NEXT strike can reach, not what the saved month
     *     traded at.
     *
     * 24 - THE TENTH SECTOR: Agriculture, the crop the mills have to buy now,
     *     and the farmland relief. A format-23 city loads and owns no fields,
     *     which is true of that city - and its mills start buying crops from
     *     the world the month it is loaded, because that is what a city with
     *     no farms does.
     *
     *     Here for the same reason 22 and 23 are: the tenth sector CHANGES THE
     *     SHAPE OF THE HOUSEHOLD SAVE, because Equity.COMPANIES is the sectors
     *     plus the bank and the households' share block is as wide as that
     *     list. Ten holdings a cell becomes eleven. The mechanism is unchanged
     *     and still does the work: restoreCells() reads its widths off the
     *     company list the save was written with and maps holdings BY NAME.
     *
     *     The farmland dial is NOT a reason for this number. It is an
     *     additive slot on the end of the policy array and a format-23 save
     *     simply keeps the default, which is full relief - the behaviour that
     *     city already had, since it had no fields to tax.
     *
     * 25 - THE BANK'S OWN COST OF FUNDS, struck once at the close of the month
     *     and carried, because the live path priced mid-month and the load
     *     path priced at the end - a city reloaded from disk quoted a
     *     different rate than the one it had just been running at. It is here
     *     because the bank's last-month array WIDENED from four slots to six,
     *     and an older build handed six would read past what it knows. A
     *     format-24 save is read the other way round and is fine: the tail is
     *     simply absent, the cost opens at zero, and for one month the floor
     *     under every rate in the city is the minimum margin alone. The first
     *     close of the month strikes the real figure and it never reads zero
     *     again.
     *
     * 26 - THE PRICE INDEX'S HIGH AND LOW WATER-MARKS, and the month each was
     *     set. Four doubles on the end of the index's array, so the same
     *     widening argument applies. A format-25 city opens with both marks
     *     sitting on today's level rather than on 1.0: its real high and low
     *     are unknowable from that save, and claiming it had never been
     *     anywhere else would be a made-up record rather than an empty one.
     *
     * 27 - THE SIXTH AGE BAND: the seniors split at 85, and the over-85s given
     *     their own two household shapes. Here because the pyramid's array
     *     grows from five bands to six and the household matrix from thirteen
     *     shapes to fifteen, so a format-26 build handed this save refuses
     *     both whole and comes back with a city of nobody living in no
     *     houses. That is the exact accident this number exists to prevent.
     *
     *     THE OTHER DIRECTION IS SAFE AND IS THE POINT. A format-26 save has
     *     no bandNames and no shapeNames, which is what tells this build to
     *     read it as the five bands and thirteen shapes it was written with -
     *     see PopulationCohorts.LEGACY_BANDS and FamilyModel.LEGACY_SHAPES.
     *     Every band and every shape is then found by name, so nothing is read
     *     at an offset it was not written at.
     *
     *     An old city therefore loads with ALL of its over-seventies in the
     *     70-85 band and none in the new one (Jerus's call: the save never
     *     recorded who was over 85, and dividing the headcount at load would
     *     be inventing a number nothing can check). They age across over the
     *     following years. The visible transient is that senior care reads as
     *     fully covered for a while, because the band it mostly serves is
     *     still filling.
     * --------------------------------------------------------------------- */
    public static final int SAVE_FORMAT = 27;

    /** The first format a sector can be read out of. Nothing older loads. */
    public static final int FIRST_SECTOR_FORMAT = 21;

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

    /**
     * True when a save predates the sector template and so carries nothing
     * this build can read a sector out of. The one older direction that is
     * refused - see format 21 above.
     */
    public static boolean isFromBeforeSectors(int saveFormat) {
        return saveFormat < FIRST_SECTOR_FORMAT;
    }
}
