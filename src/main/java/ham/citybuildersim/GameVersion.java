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
     *
     * 0.6.11 (2026-09-21) - THE GROUND FOR THE CENTRAL BANK.
     *
     * Jerus: "i think how it works, is that we implement a central bank, and
     * basically the central bank would buy gbonds or sell gbonds from thin
     * air". The design for 7.0 opens on why: the game already has a central
     * bank, and it is the rest of the world. This version is what has to be
     * true before one is built, and the number that says what the rate does
     * without it.
     *
     * THE BANK PAYS FOR THE CITY'S PAPER, which it never had. Every bond and
     * bill is sold between two presses, and the month took the bank's
     * settlement after the top-of-month clear, so it was handed zero for
     * every issue - since 0.4.3 at least: the paper went onto its book at
     * face, the coupons and the principal came in, and its cash never went
     * out. Equity from nothing, and the treasury's money from nowhere. The
     * settlement is taken before the clear now; the bank hands over the cash
     * at the next settle and books the discount, as it does for a business
     * loan, and until it has, what it owes is carried against its pool in
     * the money audit. It moves none of the eight seeds, because none of them
     * ever owes a dollar at home - the advisor borrows only the cheaper
     * dollars. Sent home by a playtest flag, six seeds sell one term bond
     * each around month 1,092, their banks now pay $37M to $87M for it where
     * they paid nothing, and failures, strain and prices move within the
     * ensemble's weather.
     *
     * Wages against the index: the finding of 2026-09-15, wages a third above
     * the price level they chase, does not reproduce. On the eight seeds the
     * wage index is the level the two-year lag implies to the last digit at
     * every checkpoint, and a harness holds it inside the lag's window through
     * twenty years of steady inflation, a currency reform and a reload. And
     * the measurement 7.0 has to turn: one founding held at 3%, 10% and 20%
     * from month 25 to 60 inflates 0.53%, -0.06% and -0.34% a year, through
     * the currency alone. Beside them, the trade page previews the push on
     * the rate instead of rewriting the month's record of it, and what the
     * currency did to the debt survives a reload.
     *
     * SAVE_FORMAT did not move. The paper a bank has not yet paid for and its
     * discount go under two keys of their own, and the debt's revaluation
     * rides the end of the foreign accounts' array, whose reader checks its
     * length. A 0.6.10 city opens owing its bank nothing for paper sold
     * before the save, and with no revaluation until its first month - which
     * is exactly what that city was.
     *
     * 0.7.0 (2026-09-21) - THE CENTRAL BANK'S BOOKS, AND THE FLOOR.
     *
     * Jerus: "the feds sheet would show how much debt it holds, like debt to
     * itself aka money printing." The game had a central bank, and it was
     * the rest of the world: the bank placed its spare cash abroad at the
     * world's 2% and funded its shortfalls abroad at the city's own rate plus
     * two points plus a stretch, so the policy dial only floored the lending
     * rate on top of a price of money the player did not set. This version
     * brings the marginal role home, onto a balance sheet the money audit
     * can see.
     *
     * THE BOOKS. A central bank with advances to the bank and to the
     * treasury, the city's paper (empty until 0.7.1) and the vault on one
     * side, and reserves and currency - M0 - on the other. Money is made and
     * destroyed only there, every operation counted and declared to the
     * audit as money in or out, and the month's made less destroyed is the
     * change in M0 to the cent. Its profit is remitted to the treasury as a
     * budget line; a loss is carried and made good first.
     *
     * THE FLOOR AND THE WINDOW. The bank's spare cash earns the policy rate
     * at the central bank, so nothing lends below it, and its shortfalls are
     * borrowed at the window at the rate plus a point. The city's paper is
     * priced off the dial itself - the two-point discount under it, "the one
     * number in this file that does not describe anything real", is gone -
     * and the carry trade and the bank's deposit bid are priced off the dial
     * rather than the city's rate, which had counted the strain premium
     * twice. Savers are paid a share of what reserves earn, so the deposit
     * rate rises with the dial.
     *
     * THE ADVANCES AND THE ARREARS RULE. The emergency note is retired. A
     * broke treasury is advanced its shortfall at the policy rate, repays it
     * from cash above zero before anything else, and may owe up to six
     * months of its revenue; past that, Jerus's rule - "pay promises first,
     * cut the rest" - pays pensions, EI, health, the schools, its wages and
     * its debts whatever it takes, pays subsidies, grants and repairs only
     * from cash it has, owes what it refused as arrears, and does not buy.
     * Held at 10%, the six seeds that ran dry in 0.6.11 - to $193
     * quadrillion, and NaN - now end owing their central bank $0.1B to $2.0B
     * with the audit closed every month; one never runs dry at all. The
     * dial can be handed to the rule (the autopilot) and taken back, and a
     * page under Finances shows the books, M0 and M2 and a year of each.
     *
     * The eight seeds change by design and stay in the ensemble's weather:
     * median population 150,500 to 160,500, bank failures 24 to 8 across the
     * eight, every seed borrowing at the window and none drawing an advance,
     * the audit clean on every month of all eight.
     *
     * SAVE_FORMAT did not move. The central bank and the treasury's arrears
     * are under keys of their own, the autopilot beside the policy rate, the
     * government's two new lines on the end of its block, the five money
     * series appended to the history. A 0.6.11 city founds an empty central
     * bank - nothing lent, nothing made - runs off any note it still holds,
     * and reloads a 0% dial at 0% rather than the 3% it used to.
     *
     * 0.7.1 (2026-09-22) - THE CURVE AND THE HOLDERS.
     *
     * Jerus: "i think we should only be able to issue 10y 20y 30y 40y and
     * 50y, so less granulity there, serial and tbills are fine tho. and also,
     * i think that the short term rates, aka the one you choose, those should
     * be basically the tbill rate, the others change just as in real life".
     * 0.7.0 made the short end true - a note prices at the dial plus the
     * credit - and then priced a fifty-year bond at the note's rate, sold
     * every issue to one buyer, and left the central bank's holdings of the
     * city's paper empty. This version is the rest of his sentence: the
     * central bank that would "buy gbonds or sell gbonds from thin air ...
     * like QE and QT".
     *
     * THE CURVE. A term premium over the short end, five entries - 0.50
     * points at ten years, 0.90 at twenty, 1.15 at thirty, 1.35 at forty,
     * 1.50 at fifty - rising in a straight line from nothing at a year to the
     * first and between the entries after it. Every price of the city's own
     * paper is read off it at the paper's maturity: the quote, the issue, the
     * buyback, what the paper is worth, and the borrowing page, which lists
     * it maturity by maturity. Term loans are issued at the five maturities
     * only, at home and in dollars; serial bonds and notes are as they were.
     *
     * THE HOLDERS. Every bond knows who holds it: the households, the central
     * bank, and the bank for the rest. At the settle the households take a
     * share of each new issue - a fifth of it per point of spread over the
     * deposit rate, never more than half - from the savings they can spare,
     * and hold it in a cell slot of their own. Coupons and principal are paid to
     * whoever holds them, each declared to the money audit. A household
     * short of cash sells its paper to the bank's desk before its dollars
     * and its shares, and once the spread it bought for is gone it sells a
     * little a month, at the pace the dollars abroad come home; a buyback
     * pays every holder, and a dollar bond bought back is money that leaves
     * the city.
     *
     * THE HOLDINGS DIAL. Chips on the Policy tab set the share of the city's
     * term paper the central bank aims to hold, up to half, and the Money
     * page shows what it holds. It buys from the bank's book, or sells back
     * into it, at the top of the month, a quarter of the move a month, with
     * reserves it makes or destroys; the coupons it is paid come back to the
     * treasury with its profit, and what it holds takes its share of the
     * premium off the long end - the whole premium at the maximum.
     *
     * THE DISCOUNT ACCRETES. What a bank pays under face is interest, and it
     * is earned over the paper's life a month at a time, not in one lump the
     * month the paper settles; what is still unearned is carried against its
     * book. With these, the list 0.7.0 left: a harness holds one press to one
     * month, the construction subsidy that paid nothing is gone, a failed
     * bank's window is charged nothing while it is resolved - decided now,
     * and labelled - and the students' grant is paid in the month it is
     * credited.
     *
     * The eight default seeds move through one thing only: the advisor's
     * dollar loan asked for twenty-five years, which is refused now, and
     * takes twenty. Put back at twenty-five, every one of the eight
     * reproduces 0.7.0 line for line. At twenty they stay in the ensemble's
     * weather - median population 160,500 to 164,100, bank failures 8 across
     * the eight as before, the audit closed on every month of all eight. Sent
     * home by a playtest flag, the households take 38% to half of each issue
     * at the settle and are paid $6M to $92M of coupons over the run; with the
     * holdings dial held at 30%, the central bank buys its 30% in four months
     * on every seed that borrows, takes 0.90 points off the fifty-year - the
     * premium's thirty fiftieths, exactly - and never sells, because the
     * twenty-year paper matures on its book.
     *
     * SAVE_FORMAT did not move. Who holds a bond, the yield it was sold at
     * and the discount it has left are fields a 0.7.0 save does not have,
     * and they read as zero: every bond held by the bank, and nothing left to
     * accrete - which is right, because 0.7.0 booked the whole discount the
     * month the paper settled, and paper saved between its issue and its
     * settle books its discount whole at that settle, as that version would
     * have. The households' paper is a slot on the end of the cell array,
     * whose reader takes the length it finds; the dial, the households' book
     * ratio and a buyback's unsettled payments are keys of their own that
     * read as 0; the central bank's five new figures ride the end of its
     * array. A 0.7.0 city opens with its bank holding all its paper, its
     * households none and its dial at 0% - which is exactly what that city
     * was.
     *
     * 0.7.2 (2026-09-22) - THE CURRENCY UNDER A CENTRAL BANK.
     *
     * Jerus: "makes sense, also, i think we need to uncap the rate... but if
     * we do... what happens to everyone?" The dial stopped at 25% because in
     * this model a higher rate bought nothing: its support for the currency
     * saturated thirteen points over the world, the hot money's appetite at
     * six, while the currency fell by the whole of the city's inflation over
     * the world's every month, by rule. So the rule goes, the channel opens,
     * and the dial goes with it.
     *
     * THE DRIFT, THE THIRD TIME. Relative PPP stood here first as a level -
     * the rate pulled toward local prices over the world's, unbounded over
     * three centuries - then as a drift, the rate moved by the inflation
     * differential every month, which was the ring Jerus's 0.6.9 year book
     * closed at 399x. Neither survives as a term. The level is the parity
     * pull it has been since, and the month's move comes from the capital
     * account: money goes to the higher REAL rate, the dial less the city's
     * inflation against the world's rate less the world's. A spiral needs an
     * outflow to continue, and a credible real rate stops it. The channel's
     * cap becomes a numerical guard a hundred points out, the hot money's
     * appetite grows to twenty-five points, and its pull is four, not six -
     * the one retune the batch allowed itself: at six, a dial left still
     * while prices started to rise gave the loop a gain half as big again as
     * the drift it replaced, and one default seed went to the currency's
     * guard and 115x its founding prices before the player's rule turned it.
     *
     * A DEFENCE THAT SPENDS. The vault's absorption cost nothing; not a
     * dollar ever left it. Now, a month the currency is pushed down and the
     * city is short of dollars, the central bank sells: the vault's capacity
     * times the month's own deficit, at most the vault, and what the dollars
     * meet of the deficit is what damps the push - a vault running low damps
     * less, an empty one nothing, a rise is never met. It is a capital
     * transaction of the central bank against the world, booked at the
     * reprice: the vault falls by the dollars, the central bank's equity by
     * their local price, and M0 does not move. The local money the world
     * hands back was the pools' and had left them the month it was spent
     * abroad, and M0 is a ledger of money the central bank made, which this
     * never was - the first build retired it anyway and took M0 below
     * nothing, -$255M on seed 0. Nothing goes through the month's profit or
     * the audit, no pool moving; the Money page shows it under equity, spent
     * defending the currency since founding. On the eight default seeds the
     * founders' billion was more than half spent in all eight, between month
     * 628 and month 3,747, and six spent all of it.
     *
     * THE DIAL, THE CEILING, THE WORLD'S PAPER. The dial reaches 100% - a
     * guard against a typo, with chips from 0 to 100 beside the slider - and
     * the autopilot's rule is no longer clamped at 25%. The treasury's
     * advances ceiling is the player's, three to thirty-six months of
     * revenue. A dollar bond is valued on the world's curve - the foreign
     * rate plus the same term premium table - instead of the city's short
     * rate, and a dollar quote prices its own coupons into what the world
     * charges; a dollar round trip, which netted the city a fifth of what it
     * raised in 0.7.1, costs it the issuance and nothing else.
     *
     * AND TWO THINGS FOUND ON THE WAY. A household cell the census leaves
     * under half a household is emptied where its count is written - what it
     * held folded into its own row, then the city - and nothing pays it:
     * cells of 1e-15 households had been collecting savings since 0.7.1, and
     * a reformed city parted from its twin on which cell was one
     * (HouseholdBalance, A CELL UNDER HALF A HOUSEHOLD IS EMPTY). And the hot
     * money compares the city's rate without the bank's strain premium,
     * which is what a strained bank charges and not a return.
     *
     * WHAT IT MEASURED. The eight default seeds stay in the weather: median
     * price swing 1.50x to 1.46x, no month at the currency's guard, the
     * currency a little stronger (median 0.55 to 0.52), M0 never under
     * $1.06B, the audit closed on every month. Bank failures rise, 8 to 19
     * across the eight, and the whole rise is Manufacturing's: a Fabrication
     * Works borrowed for at two and a half times a young bank's equity,
     * losing money before its interest, written off - then the restructured
     * residue a year later, and in some seeds again when it rebuilds. On
     * 0.7.2's path it takes four seeds' banks down at months 165-167 and
     * again twelve months on; on 0.7.1's it failed a bank three times, from
     * month 237. Netting the strain premium out of the hot money's spread
     * changed none of the nineteen. Held at 30% or 50%, where 0.7.1's
     * stop at 25% sent the six broke seeds to 190-216x their founding prices
     * with the currency at its guard, they end at a median swing of 1.9x and
     * 2.0x, the currency strong - two seeds at 30% with an episode of 13x and
     * 17x. Held at 10%, none of the six runs dry, so a ceiling of twelve or
     * twenty-four months changes nothing there. With the rule on the dial
     * the rate goes past 25% in four seeds, for 24 months in all, to 30.5%
     * at most, and bank failures fall 27 to 17. One founding held at 3, 10,
     * 20 and 40% inflates 0.02%, 0.01%, -0.38% and -0.98% a year: the rate
     * moves inflation through the currency now, and modestly - the
     * households' saving, 7.0e, is the rest of it.
     *
     * SAVE_FORMAT did not move. The ceiling is under a key of its own that an
     * older save does not have, which reads as the six months every city had;
     * the defence's month, its lifetime and the real rate the reprice was
     * handed ride the end of the foreign accounts' array, and what the
     * central bank has spent defending the currency since founding the end
     * of its own, both read by length. A 0.7.1 city opens with a vault never
     * spent - which is exactly what that city was.
     *
     * 0.7.3 (2026-09-22) - THE DEMAND CHANNEL.
     *
     * The design's sentence, the-central-bank.md section 9: for the rate to
     * bite at home, a household's saving must answer the real return. Until
     * now the only household decision that read a rate was where to keep the
     * money; a hike moved credit and, since 0.7.2, the currency, and not a
     * dollar of what anybody spent.
     *
     * WHAT A HOUSEHOLD SPENDS ABOVE A BASKET ANSWERS THE REAL DEPOSIT RATE.
     * One dial, HouseholdBalance.SAVING_RESPONSE, provisional 1.0 and
     * Jerus's to settle: ten points of real return on deposits cut what a
     * household spends above subsistence by a tenth, ten points negative
     * raise it by a tenth, held between SPEND_FLOOR a half and SPEND_CEILING
     * one and a half. One factor a month, struck by Game on
     * realDepositRate() - the deposit rate less the year's inflation, the
     * same inflation the parity reads - and handed to every cell's plan: the
     * propensity's share of income above the basket, the wealth term, and
     * the surplus the luxury counter and the table spend out of, all by the
     * same factor, so a hike does not hand what the grocer lost to the
     * counter. The basket itself never moves. The deposit rate and not the
     * curve, because it is the return on the money a household is deciding
     * whether to spend. The monetary page says it: "savers earn X% real, so
     * households spend Y% of what they would at zero".
     *
     * THE ASSERTION. MonetaryCheck section 6 - one founding held at 3, 10,
     * 20 and 40% from month 25 to 60 - asserts now what it measured since
     * 0.6.11: inflation falls with the rate, each row no higher than the one
     * before within the noise a month's delay in the hand makes (0.019
     * points, against an allowance of 0.05), and the 3% row a point above
     * the 40% row. At 0.7.2 the rows read +0.02%, +0.01%, -0.38% and -0.98%
     * a year - 0.996 of a point, and the floor would have failed; at 0.7.3
     * +0.04%, -0.00%, -0.37% and -0.99%, 1.032. Green, and honestly: the
     * point is still mostly the currency's. The bank pays savers
     * Bank.DEPOSIT_PASS_THROUGH of what it earns, so the deposit rate carries
     * about a quarter of the dial and the factor runs only from 0.99 to 0.88
     * across the range; and the founding's shelf sits at its floor on every
     * row, so the food four fifths of the index cannot fall whatever demand
     * does. SAVING_RESPONSE at 2.0 was measured at 1.145 and not taken.
     *
     * THE CURRENCY'S GUARD GOES. Jerus: "Better to have it exceed otherwise
     * one can just ignore once at 100." ForeignAccounts.MAX_RATE 100 and
     * MIN_RATE .01 become a billion and a billionth - numerical guards, never
     * a price - and the reform still scales them. The strip turns its second
     * line round past a hundredth of a cent, to what a US cent costs - D$1,000
     * at a rate of 100,000 - and every page that prints a rate goes through
     * one formatter that reads at any size. None of the thirty-six 0.7.3 runs
     * measured took a dollar past 3.5 local, and none spent a month at the
     * guard.
     *
     * EI IS PAID WHERE IT IS CREDITED. The out of work were credited at the
     * top of a month the bill the treasury had paid at the bottom of the one
     * before - the grant's lag, fixed in 0.7.1 for the grant. Struck on the
     * pool the month opens with and paid at the top now: the treasury, the
     * ledger and the audit read one month's one figure. On the eight seeds
     * it moves the treasury's cash by a tenth of a billion at a checkpoint
     * and nothing else.
     *
     * AND THE BANK QUOTES NO MORE THAN IT CHARGES. The deposit rate is the
     * payout over the deposits, and the payout is a share of everything the
     * bank earns, its own capital's placement included - so its first month
     * quoted 7,567% on $5.7k of deposits, and with the households now
     * reading the figure every seed's spend factor sat at its floor in month
     * 3. The rate reported stops at the bank's own lending rate now; what is
     * paid does not move. It binds in 17 to 236 months of the eight default
     * runs, from month 2 to month 2,961 - a book lent at older, dearer rates
     * earns its savers more than the bank charges today.
     *
     * WHAT IT MEASURED. The default eight change by design - the factor
     * runs every month on every household - and every run closed the audit
     * every month: median population 154,207 to 149,028, the price swing
     * 1.46x to 1.50x, the index at the end 1.126 to 1.135, bank failures 19
     * both, and the spend factor at a median of 0.994, its lowest 0.815 to
     * 0.863 in a deflation between months 274 and 839 - the advisor keeps
     * the dial low, so savers earn little and the channel idles. Measured
     * before the cap (it moves the founding months and the months above),
     * held at 10, 30 and 50% the six broke seeds do not deflate through
     * demand: the factor's median is 0.99, 0.97 and 0.89, and the median
     * index at the end 0.960, 1.101 and 1.716 against 0.964, 1.032 and
     * 1.408. At a held 30 or 50% what is saved compounds at a rate paid in
     * new money, and the wealth term spends a fixed share of a stock growing
     * faster than the factor takes off it. Seed 0 held at 20% ends at an
     * index of 2.64 against 1.10 at 3%. The channel is in; what it can move
     * is the shelf a scarcity prices, and in this model the shelf is priced
     * by the shops' coverage or by its floor.
     *
     * SAVE_FORMAT did not move, and nothing new is saved: the factor is
     * struck again every month and on the load path from the deposit rate
     * and the price index a save already carries, and the month's EI comes
     * back from the government's month, whose slot it has had since
     * 2026-09-11. A save from 0.7.2 pays one month's EI twice, once: the bill
     * its last month paid at the bottom is paid again at the top of its first
     * month here, and the out of work are credited it once. Otherwise it is
     * the city it was.
     *
     * 0.7.4 (2026-09-23) - THE HOUSEKEEPING: A TARGET, THREE RATES ON THE
     * STRIP, THE SECTOR LIST, THE TAXES BY TYPE.
     *
     * Four small things Jerus asked for in one evening, all of them player-
     * facing and none of them a model redesign; every new dial opens where
     * the old constant was, so the default run is the run it was, to the
     * byte.
     *
     * THE INFLATION TARGET IS A DIAL. Jerus: "i want to have the dial not
     * target 0 inflation, set it so that you can choose what is your
     * inflation target." DebtManager.INFLATION_TARGET, 2%, becomes the
     * player's inflationTarget, DEFAULT_INFLATION_TARGET 2% and held
     * between MIN_ and MAX_INFLATION_TARGET, 0 and 10%; the rule, its
     * reasons, the strip's colour and its tooltip read it. On the monetary
     * page under the autopilot, applied at once: chips from 0 to 5% and a
     * half-point ladder to 10, with the rule struck at the target and at
     * another in a sentence. A target is the rule's intercept and nothing
     * else - five points of it are TAYLOR_WEIGHT times five points of rate,
     * which MonetaryCheck asserts. -Dplaytest.inflationTarget sets it for a
     * run.
     *
     * THREE RATES ON THE STRIP. Jerus: "on the top of the UI the bank rate,
     * the central bank rate, both should be shown, as well as the rate you
     * borrow in." A third panel beside prices and the currency: the central
     * bank's dial, what the bank lends a business at before its own premium
     * (Bank.lendingRate() on the dial), and what the treasury borrows at -
     * three short lines at the caption's weight, grey, and "failed" or "no
     * bank" in red. The bank page's "It charges", which printed the city's
     * rate, prints the bank's now - the strip's "bank" figure.
     *
     * THE SECTOR LIST. Jerus: "a little graph of its net income, perhaps how
     * much workers it employs total ... a button in which you can click to
     * expand to show some more info for all at once". Each card draws the
     * last two years of its net income and says how many work there
     * (Sector.getWorkers(), the posts at the "Staffed" share); "Show more"
     * opens every card to revenue, margin, cash, what it owes and its posts
     * filled. Two history series per sector feed it, netIncome:<sector> and
     * workers:<sector>, folded by the year book as a flow and a level.
     *
     * THE TAXES BY TYPE. Jerus: "what about just increasing sale tax for all
     * at the same time? currently that's a hassle, i want to be able to do
     * that for every type of tax, even wage tax". Profit, sales and wage each
     * have a base of their own now, and every offset moves off its own tax's
     * base; each tax page's top lever is that base. His one city rate is
     * "Every tax at once" on the Everything page - TaxPolicy
     * .setIncomeTaxRate(), which sets all three, and which the playtest's
     * advisor and an older save still go through. One trap on the way: the
     * load path read the old single income key after the policy array, and
     * with three bases that would have put a split back together on every
     * load; it reads the key now only for a save whose array was not read.
     *
     * SAVE_FORMAT did not move. The target is a key of its own,
     * inflationTarget, that an older save does not have and reads as the 2%
     * it was; the three bases ride the end of the tax policy array
     * (TaxPolicy.STATE_BEFORE_SPLIT), and an older, shorter array reads all
     * three as its one income rate; the two series per sector are new keys
     * in the history, and an older save has no months of them until it plays
     * one. A 0.7.3 city opens aiming at 2%, taxing profit, sales and wages
     * at one rate, and with no sparklines yet - which is exactly what that
     * city was.
     *
     * 0.7.5 (2026-09-23) - ENTER BUILDS, BACKSPACE CLEARS; THE REPORTS PAGE,
     * REDRAWN.
     *
     * One shortcut on the build page. Jerus: "in the building rail, when you
     * have lets say 3 ready to build, i want to be able to press enter to
     * build, instead of having to click the green button, you can still click
     * it, but just a short cut, and backspace/delete to reset it." Enter
     * places every pending order on the category page showing, in the page's
     * order, each through the card's own placeOrder - the first refusal puts
     * its screen up and the rest stay pending; Backspace or Delete takes every
     * quantity on the page back to none. Neither key is spent when there is
     * nothing to act on. The Build button's tooltip says both, and so does
     * one caption under the grid while anything is pending.
     *
     * SAVE_FORMAT did not move, and the model did not: the orders on the
     * cards were never saved, and this is the interface's alone.
     *
     * THE REPORTS PAGE, REDRAWN. Jerus: "have it be a collapsable list ...
     * two graphs always displayed ... the real gdp yearly figure, and the
     * other is the population ... the bigger one ... defaults to the
     * borrowing rate, price level and inflation year on year ... 'clear all'
     * should just be beside the graph", and "pinnable defaults, but not
     * fixed, and leave goods there, and event marks". Two small charts at the
     * top, real GDP and the population until the player pins others - a
     * preference in GamePrefs (pinnedLeft, pinnedRight), not in the save;
     * then the big chart, its presets, "clear all" and a log switch in a row
     * above it, seeded on a first visit with "What money costs" (the rate,
     * the price level, inflation), two units on two real axes rather than
     * both squashed onto 0-100, a crosshair that reads every line at the
     * month under the pointer, recessions shaded on all three charts, and
     * the named episodes ("Financial crisis of 2045") ticked under it. The
     * episodes are YearBook.episodes(), a pure function of the history that
     * the year book's WHAT HAPPENED section prints too, so the file and the
     * chart name the same years; YearBook also became the one place real GDP
     * and inflation are struck. The picker folds into its groups, closed
     * until wanted, with a box that finds a line by name; the goods and the
     * year book stay at the bottom. Nothing is saved that was not before.
     *
     * 0.7.6 (2026-09-23) - ONE LADDER FOR EVERY DIAL, THE PRICE OF EACH
     * SCHOOL, GDP IN LAYERS, AND LAND BOUGHT IN DOLLARS.
     *
     * Four things Jerus asked for together. The first three are none of
     * them a change to what the model does with a number it already had:
     * every new dial opens where the old one was, so with those three alone
     * the default run was 0.7.5's, to the byte. The fourth, land bought in
     * dollars, does change the model, and is at the end.
     *
     * ONE LADDER. Jerus: "all the dials in the policy in the promises
     * section, make them a slider with steps, and a + - on the ends,
     * basically i think it's better if you create an object or class of
     * slider, and then whenever you need it you just call that class and
     * plug in the specific sensitivity, max, min and steps type". The tax
     * pages had it (PolicyScreen's taxLadder: "-", a snapping slider, "+",
     * the reading and a line of ends); the floor, the rate, the promises and
     * the fare had the slider alone, and the target, the holdings and the
     * ceiling had chips. ui/Ladder is the one class now - Ladder.of(min, max,
     * step, reads), what the city charges, and either a stage callback (the
     * foot bar or the page's apply bar makes it real, as before) or an
     * apply-at-once one (the monetary page's three, by design); the step is
     * the sensitivity and there is no other knob. Every dial on the policy
     * tab and the fare on Services is one, each with its key, range, step and
     * formatter as they were; the chips stay beside the three that had them.
     *
     * THE PRICE OF EACH SCHOOL. Jerus: "not only can you raise prices but
     * also raise the price for a specific university, and beside the dial it
     * shows the current space, the current students, and the current cost
     * and revenue." The tuition scale is nine, one per kind of school -
     * TaxPolicy.tuitionScaleOf() and setTuitionScaleOf() - in the shape the
     * income taxes were given in 0.7.4: setTuitionScale() is every school at
     * once, getTuitionScale() the first kind's, tuitionScalesSplit() whether
     * they have parted; Education.feeFor() reads the kind's own. The Schools
     * page keeps its every-school dial at the top and has a row per kind
     * under it: its own dial, and the places its buildings seat, the students
     * in it, what its staff and buildings cost (Education.getCostOf(), handed
     * in by the month from BuildingManager's per-course payroll and upkeep)
     * and the tuition its students paid (getFeesOf()) - a kind with nothing
     * standing says "no school" and its dial is greyed. The trap on the way
     * was the one 0.7.4 found: the month and the load path told the schools
     * getTuitionScale(), which would have put nine prices back to one on
     * every load; they tell them each kind's now.
     *
     * GDP IN LAYERS. Jerus: "have it so the gdp graph can be a toggle, and if
     * toggled it switches from line to mountain graph ... showing how much is
     * made up of investments, net exports, government spending". Four history
     * series beside gdp - consumption, investment, government, netExports,
     * off NationalAccounts' own getters - and YearBook.real() and realYear()
     * beside realGdpYear(). A "layers" chip on the real-GDP small chart (and
     * on the big chart's reading when real GDP is picked alone) stacks
     * consumption, investment and government from zero with the real GDP
     * line over them, so the gap is net exports - above the stack when the
     * city exports more than it imports, below it when not, because a
     * stacked area cannot hold a negative layer.
     *
     * SAVE_FORMAT did not move. The nine scales ride the end of the tax
     * policy array (TaxPolicy.STATE_BEFORE_SCHOOLS) and an older, shorter
     * array reads all nine as the one scale its slot carried; each kind's
     * month - its cost and its fees - rides the end of the schools' array,
     * and an older one reads none until a month is played; the four parts
     * are new keys in the history, and an older save has no months of them
     * until it plays one. A 0.7.5 city opens charging every school the one
     * price it had, with no layers yet - which is exactly what that city was.
     *
     * LAND IS BOUGHT IN DOLLARS, the batch's second half, and the one part
     * of it that changes the model. Jerus: "when you buy land, make it so
     * that it costs USD not domestic currency, and basically how it would
     * work is a little toggle at the top to choose, when you buy land, to use
     * up your USD reserves or to convert cash into usd exactly to buy the
     * land, and the default is that you convert." LandMarket prices every
     * parcel in US dollars - the same base and premiums, now the world's
     * figures, so at the founding rate every number is what it was - and
     * the treasury pays usd x rate on the day it buys: a weak currency makes
     * land dear and a strong one cheap. What businesses pay the city stays
     * local money, struck exactly as before, so the margin carries the
     * currency. Game.buyLandParcel() pays one of two ways, the land office's
     * chip pair (Game.isLandPaidFromVault()): CONVERTING, the default, pays
     * the local money through TreasuryLine.LAND and has ForeignAccounts buy
     * exactly the dollars and hand them over in one movement, the vault
     * where it began; FROM THE VAULT spends reservesUsd and moves no cash,
     * the journal carrying "Bought land with US$... of reserves" back
     * against the budget's land line, and a short vault spends what it holds
     * and converts the rest, the receipt saying so. A treasury's dollars are
     * the financing item, so converting puts the push on the rate a reserve
     * purchase of the same dollars would - none (ForeignCheck 14). The
     * landPrice series and the year book's column are the world's dollar
     * price since this build, and a currency reform no longer clears the
     * land office's board: its dollar prices are out of the reform's reach.
     *
     * AND THE SAVE: a new key, landPaidFromVault (an older save converts);
     * the land listing behind a new marker (-105) whose prices are dollars -
     * an older listing, and an older price state without its fourth slot, are
     * read as dollars at the rate of the day the save is loaded, so the local
     * cost the player saw is what it costs that day
     * (LandMarket.settleLocalPrices()); the office's dollar ground price as
     * the price state's fourth slot; and the land's dollars - the month
     * struck, both ways and out of the vault, the same since founding, and
     * the month's local cost - as ForeignAccounts slots 31-36. Every older
     * shape reads correctly, so SAVE_FORMAT did not move.
     *
     * 0.7.7 (2026-09-23) - THE BANK PRICES LIKE A BUSINESS.
     *
     * Jerus: "lets make banks realistic, and remember, its a business, it
     * wants to make money." Five changes to one institution, and the first
     * is a removal.
     *
     * THE STRAIN PREMIUM IS GONE. Bank.ratePremium() added nothing to
     * eighteen points to every rate in the city - business loans, household
     * credit and the car loans on it, the carry trade, the deposit bid and
     * the city's own paper - once the bank's book passed 80% of capacity,
     * and all eighteen with no branch, no equity or a failed bank. A
     * strained bank raising every rate caused the defaults that strained it
     * (the-rate-that-stops-the-cranes). strain() stays, as the measure the
     * branch decision reads; the history's bankPremium series is no longer
     * recorded.
     *
     * A LOAN IS PRICED FROM WHAT IT COSTS (Bank, WHAT A LOAN COSTS). Prime
     * is four parts: the funds-transfer price - the dial, the window's
     * quarter point on the share of the bank's money that came from the
     * window, and DebtManager's term premium for the loan's thirty-six
     * months; running costs per dollar lent, a trailing year of payroll and
     * upkeep over the larger of the book and what the capital could carry;
     * the expected loss of a sound book through the cycle, 0.4% a year
     * (RBC's 2025 provision rate); and the capital the loan ties up, 11%
     * of it (the 8% minimum and a three-point buffer) at the 12.5% its
     * owners ask (Equity's required yield) less the funding it replaces. A business pays prime
     * plus its own leverage and restructure spread, re-based so that a sound
     * one pays prime; a household pays the four parts at its risk weight
     * plus RISK_SLOPE for each month it owes; the carry trade the three
     * parts without a loss, because it never defaults. The parts are struck
     * at the close and a loan keeps its rate for its term.
     *
     * THE BANK CHOOSES WHAT TO PAY SAVERS: a share of the dial by its
     * funding position, 35% for a bank flush with reserves and 90% for
     * one at the window, moved a sixth of the way a month, never under zero
     * or over the window's rate. And never past net zero: the payout never
     * takes the bank's interest margin under its payroll and upkeep, and
     * never exceeds the savers' share of that margin - the deposits are
     * counted without being held as its cash, so a rate on all of them is
     * paid on money it earns nothing on. With the margin as the only bound,
     * the gate's held-10% stress paid savers every dollar a one-branch bank
     * earned in most months and failed it 184 times over eight seeds
     * against 0.7.6's 78. 0.7.3's cap (the quote never above the lending
     * rate) and the bid for hot money went with the rule they belonged to.
     *
     * FEES: an account fee for every housed household, $12 a month at
     * founding prices (Canadian chequing, 2025) and real-indexed, and 1% of
     * every new business and household loan. The households' books have a
     * line for the account fee, and the bank's income statement one for
     * fees. The central bank's window is the dial plus a quarter point (the
     * Bank of Canada's Bank Rate), not plus one.
     *
     * TWO PROFIT BUGS. The bank's dividend was a share of its profit before
     * tax; it is after tax now. And what the bank booked after its month
     * closed - the desk's re-mark and dividends, paper bought from the
     * households - was never taxed or paid out; it is carried into the next
     * month's (Bank.lateProfit()). And one found on the way: a company's
     * dividend was divided by its register when the holders held a few
     * hundredths of a share more, so it overpaid by up to $0.31k a month
     * from month 3,471 of the default run (Equity.payDividend()).
     *
     * THE EXPECTED LOSS IS NOT THE BANK'S OWN RECORD. It was built first
     * from the bank's write-offs, over five years and then over twenty, and
     * measured: a whole sector is one borrower here, so the record is lumpy
     * by construction - a young city's first sector default put it at 11%,
     * and over the default run's eight seeds the city ended at 122,287
     * people on average against 148,668, one seed stalled at 32,000. The
     * orchestrator's call: the through-the-cycle base in prime, the
     * borrower's own risk in its spread.
     *
     * MEASURED over eight seeds as shipped. The default run: four bank
     * failures (nineteen before); the population at the end 135,993 on
     * average against 148,668, and 26,733 against 33,350 at month 1,300,
     * the lowest seed ending at 111,831. On the autopilot: one failure
     * (thirteen before) and 162,751 people against 156,094. Held at a 10%
     * dial: forty failures (seventy-eight before). The slower early growth
     * is not the investment hurdle biting harder - over months 0-1,300 of
     * four seeds, measured before the savers' second bound went in, the
     * sectors were quoted less at it than before (7.13% against 7.44%) and
     * refused fewer plans (3,291 against 4,000); the batch's notes have the
     * rest.
     *
     * SAVE_FORMAT did not move. The bank's pricing record (its trailing
     * year of costs) and the profit it booked after its close
     * are new keys, bankPricingHistory and bankLateProfit, that an older
     * save reads as a record refilling a month at a time and none carried;
     * the two struck parts and what the book kept ride the end of the bank's
     * last-month array, which an older, shorter one reads as the defaults
     * until its first close; the households' account fee rides the end of
     * their statement (HouseholdAccounts.SCALARS_BEFORE_ACCOUNT_FEES), read
     * as none from an older one; the four new history series - policyRate,
     * bankPrime, bankDepositRate, bankFees - fill from the month a city is
     * next played, and an older history's bankPremium key is skipped.
     *
     * 0.7.8 (2026-09-23) - THE BANK AS A BUSINESS WITH ITS CAPITAL.
     *
     * Jerus: "lets make banks realistic, and remember, its a business, it
     * wants to make money" - and of its capital, "the bank chooses". 0.7.7
     * made it price like one; this makes it keep its capital like one
     * (Bank, THE BANK AS A BUSINESS WITH ITS CAPITAL). The regulator's
     * 8% (Bank.CAPITAL_RATIO) is still the only number in it that is the
     * city's.
     *
     * A LOSS ALLOWANCE (simplified IFRS 9). Each sector's business debt and
     * the families' book hold an allowance: a sound book a year's expected
     * loss; a borrower in trouble - a sector past
     * BusinessDebtManager.MAX_LOAN_TO_ASSETS of its assets, a family owing
     * more than half its credit ceiling in months of income - its lifetime
     * loss. For a sector both are read off the curve its firms default on
     * (see A SECTOR DEFAULTS A SLICE AT A TIME below): a year of it, never
     * under BASE_LOSS_RATE, and past the watch line a loan's term of it; for
     * the families BASE_LOSS_RATE, and past the line what the discharge would
     * cost, scaled by how near it they are. A write-off draws the allowance first; the income
     * statement's provision is the allowance's move plus what it had not set
     * aside. Net loans are the book less the allowance. Measured over two
     * seeds before it went in, a sector past the watch line defaulted within
     * two years half the time and more; one under it, 1.5%. On the two
     * default seeds traced as shipped, 64-71% of what was written off had
     * been set aside before the month it was.
     *
     * ITS OWN TARGET: the minimum plus the worst year of provisions it has
     * lived through, never less than the Basel conservation buffer (2.5
     * points) nor more than the whole Basel stack (Bank.MAX_BUFFER, 8.5
     * points) - uncapped, seed 0's bank chose 48% and then 108% after two
     * sector restructures, priced prime at 10-19% through the capital
     * charge, and stalled the city at 8,348 people. The top of its
     * band is the target plus 2.5 points. Loans are priced on the target;
     * the fixed 11% (CAPITAL_BUFFER) is gone.
     *
     * WHAT IT DOES WITH ITS PROFIT: nothing paid under the target; 45%
     * inside the band (RBC paid 43% of 2025's earnings); over the top that
     * and a twelfth of the excess a month. No longer capped at its cash. Its
     * desk buys its own shares back only while it is at or over its target
     * and issues new ones only while it is under it - issuing at the target
     * too, the first reading sold the households $28-86bn of new bank shares
     * a seed and paid it straight back, 131-183% of the bank's profit - and
     * a share issued or bought back is capital now, not trading income:
     * 0.7.7's seed 0 booked $8.2bn of its own shares sold to the households
     * as $8.2bn of its $8.65bn trading profit.
     *
     * WHAT IT LENDS: freely at or over the target; between the minimum and
     * the target a borrower's debt may grow 1% x (how far up) / (how far to
     * go) a month; under the minimum only a business's interest reserve and
     * a family's month of interest. Game hands the rule to both lending
     * desks at the top of the month, and the carry trade reads it through
     * headroom(). The families' credit had never been shut by a failed bank
     * before; it is now. A standing bank is asked for capital only under
     * the minimum, and then for enough to reach its target: asked for the
     * bare minimum, a topped-up bank sat on the line lending nothing new.
     *
     * AND 0.7.7's LOOSE ENDS: the bank's month lines are saved, so a reloaded
     * Income page reads the month it was saved in; Bank.redenominate()
     * scales them and everything new; a failure caused by what is booked
     * after the close is resolved that month, not the next; four player
     * strings that still described the strain premium say what is true; and
     * CapitalFlows.arrivalsAt(), with no caller since 0.7.7, is deleted.
     * Six history series - bankCapitalRatio, bankCapitalTarget,
     * bankAllowance, bankProvisions, bankDividends, bankReturnOnEquity - with
     * a year-book rule each.
     *
     * MEASURED over eight seeds as first built, before the slices below,
     * against 0.7.7. The default run:
     * 151,979 people at the end on average against 135,993 (0.7.6: 148,668),
     * 32,761 at month 1,300 against 26,733, $121bn written off against
     * $119bn. The bank's growth years: a capital ratio of 21.3% (median of
     * the seeds' medians) against a target of 16.5%, a return on equity of
     * 24% where 0.7.7's read 1-2%, 83% of its profit paid out, and it ended
     * the runs with $0.1-1.9bn of equity where 0.7.7's sat on $24-151bn.
     * BUT IT FAILED 92 TIMES (0.7.7: 4; 0.7.6: 19). A whole sector is one
     * borrower, and each seed's worst year cost 47-159% of the weighted
     * book - through the run, 1.0% of the loans a year against a real
     * bank's 0.4% - which a bank holding 16.5-19% cannot take; 0.7.7's
     * survived on the capital it never paid out. On the autopilot: 160,676
     * against 162,751, 69 failures against 1. Held at a 10% dial: 2,443
     * against 7,211 and 266 failures against 40 - there the shares it used
     * to sell at its target had been a buffer, and a bank under its target
     * pays no dividend, so nobody buys the ones it may still issue.
     *
     * TWO WAYS TO SPEND LESS OF IT WERE MEASURED AND NOT SHIPPED. Keeping the
     * excess instead of returning it: two seeds failed 3 times each and
     * ended with 78,048 and 127,198 people against 150,518 and 141,302. A
     * target from the bank's stress test on its largest borrower, uncapped,
     * with that concentration priced to the borrower past a quarter of its
     * capital (Bank.MAX_BUFFER has the numbers): 68 failures, but 21.5%
     * unemployment and $233bn written off. The batch's notes have the rest.
     *
     * A SECTOR DEFAULTS A SLICE AT A TIME, the batch's fix (Jerus,
     * 2026-09-23: "go for option C"). A sector stands for many firms, so each
     * month the share of its debt whose firms fell through the default point
     * defaults - Merton's (1974) structural model, the basis of Moody's KMV:
     * PD(L) = N(ln(L / INSOLVENCY_TRIGGER) / sigma) a year at leverage L,
     * sigma the firms' asset volatility (BusinessDebtManager.ASSET_VOLATILITY,
     * 0.25, the literature's typical industrial - Jerus's number to settle),
     * 1 - (1 - PD)^(1/12) a month - and the bank writes off 60% of it (1 -
     * RESTRUCTURE_TARGET / INSOLVENCY_TRIGGER), every loan pro rata, the plant
     * untouched. Next to nothing under 0.6 times its assets, 2% a year at
     * 0.9, half at 1.5, 88% at 2.0. The whole-sector write-down is kept only
     * for a sector with nothing left (assets at or below zero), and only it
     * goes on the record, the surcharge and the ban. The allowance reads the
     * same curve, and the inbox says "Businesses are going bust" when a
     * sector goes under whole or past the default point.
     *
     * MEASURED over eight seeds, against the first build: IT DID NOT FIX THE
     * FAILURES. The default run failed 84 times (92), the autopilot 87 (69;
     * one seed 44 of them) and held at 10% 305 (266); $143bn written off
     * (121) and $190bn on the autopilot (143). The city grew - 160,437
     * people at the end on average (151,979), 167,644 on the autopilot
     * (160,676) - and provisions in growth years read 0.28% of the loans
     * (0.02%), a real bank's. Of the default run's 84 failures, 58 came the
     * month the bank set aside its main borrower's lifetime loss as it went
     * past the watch line - Manufacturing, Real Estate or Automotive, a
     * median 62% of its whole book, the month's write-offs a twelfth of the
     * provision - and 25 when the distress rule retired a sector's plant for
     * nothing and its leverage went from about 1.2 to 5-75 in one to three
     * months, where the curve takes 60% in a month and the backstop the
     * rest. A sector is still one leverage: its firms share it, so their
     * losses are one loss, recognised all at once. The batch's notes have
     * the trace and what would fix it.
     *
     * THREE FIXES ON TOP OF IT (Jerus, 2026-09-23: "Price risk from the
     * curve, Sell a failing sector's plant, Smooth the loss reserve"). A
     * business loan is priced at prime plus the borrower's own expected
     * loss off the same curve over the BASE_LOSS_RATE prime already carries,
     * LOSS_GIVEN_DEFAULT x PD(L) - BASE_LOSS_RATE, at the leverage the loan
     * leaves it at, a project's building counted, and the plan is judged at
     * that rate. The leverage spread (six points a unit, capped at seven) is
     * gone: nothing under 0.75 times assets, 0.8 points at 0.9, 11 at 1.2,
     * 30 at 1.5. A plant retired by either rule is sold to the builders for
     * the material it was built with, at the day's price, as far as their
     * cash goes, and the crews build from it before they buy. And the
     * allowance stages a sector's firms, not the sector: the share past the
     * watch line, N(ln(L / 0.9) / sigma), holds a loan's term of the curve,
     * the rest a year of it - no cliff at the line.
     *
     * MEASURED over eight seeds: IT DID NOT FIX THE FAILURES EITHER. The
     * default run failed 124 times (84), the autopilot 69 (87), held at 10%
     * 305 (305); $136bn written off (143). 63 of the 124 are two seeds' last
     * hundred months, with Luxury Retail 67-99% of the bank's book and
     * restocking every other month: its assets swung a sixth month to month,
     * its leverage 1.06 to 1.27 and back, and the allowance on it $227M to
     * $637M against $590M of equity - the bank failed on every high month.
     * Each fix alone, in probes: 81, 75 and 91 failures, against 81 for the
     * first build's rules re-rolled at a rounding difference. The price cut
     * the loans written past 1.0 times assets by a third, the money lent
     * there by three quarters, and the bank's return on equity in growth
     * years by half (10% against 21%): a sound borrower pays prime now. The
     * sale paid the sellers $10.2bn over the eight seeds, but a building's
     * material is a quarter to a third of what it cost and the builders had
     * cash for 30% of what was offered, so the 28 collapses stayed. The early
     * city came back: 32,463 people at month 1,300 (31,372), 49,479 on the
     * autopilot (29,892).
     *
     * And the load path's last households restore read the save as the
     * legacy layout and was refused whole on every modern save; it reads
     * the save's own bands and shapes now (SaveFileCheck found it under the
     * new pricing).
     *
     * SYNDICATION, TRIED AND TAKEN OUT (Jerus, 2026-09-23: "Build
     * syndication"; 2026-09-24: "Drop syndication, keep quarterly"). The
     * bank held at most a quarter of its equity of any one sector - Basel's
     * large-exposure limit, a sector's firms one connected borrower - and
     * sold the rest of each new loan abroad at its own rate. Over the eight
     * default seeds it sent 99% of the businesses' borrowing abroad, put the
     * interest paid abroad at 9-14% of GDP a year and prices at up to 3.9
     * times founding, and left the bank an arranger with $16-120M of equity
     * that failed 101 times, on its own-share buybacks (68) and its desk
     * (28) rather than its loans. It is deleted - the loans' share, the three
     * audit lines, the screens' column - because a sector is many firms, the
     * premise its partial defaults are built on, and Basel's rule is for a
     * firm or a group tied by control, not an industry. Bank keeps the
     * paragraph, where its lending rules are.
     *
     * THE QUARTER STAYS: the allowance, the stage-2 share and a new loan's
     * price read a sector over its last BusinessDebtManager.STATEMENT_MONTHS
     * month-ends, the loss struck on what it owes now; the defaults read the
     * month. A backstop restarts the sector's quarter, as a lender re-rates a
     * restructured borrower - measured, that moves no loan, because the ban
     * outlasts the quarter; it corrects the quote, the stage and the watch
     * list. The builders' material from scrapped plant is a cost when they
     * build with it, at what they paid.
     *
     * AND THE DESK IS HELD TO THE BANK'S CAPITAL (Jerus, 2026-09-24: "Buybacks
     * only from spare capital", "Trading desk held to its capital"). It buys
     * the bank's own shares back only with what the bank holds over its
     * target (Bank.buybackRoom()), and other companies' only while the bank
     * would still hold its target with them on its books, at the weighted
     * book's own RISK_EQUITY - a trading book held against capital, Basel's
     * market-risk requirement (Bank.deskCanCarry()). What it will not buy
     * stays with the seller: abroad with an emigrant, with the world, or a
     * household short of money borrows or goes without.
     *
     * MEASURED over eight seeds: the default run failed 39 times (round 3:
     * 101; the same tree without the desk's two limits, 45; round 3's quarter
     * without syndication, 40), the autopilot 49 (97; 41 without the limits)
     * and held at 10% 284 (136; 292). Not one failure in the default run or
     * on the autopilot was a buyback, the desk or running costs: all were
     * lending - 20 collapses (the distress rule retires a sector's plant and
     * the backstop writes it down), 14 stage-2 set-asides (a borrower holding
     * a median 55% of the book past the watch line) and 5 slices, 21 of them
     * before month 500. Held at 10% the desk's re-mark is 15 of the 284: a
     * bank far over its target has capital to spare, the charge on a share
     * is 15.75% of it, and the quote can fall by half. The limits bound: its
     * own shares in 82 months over the default seeds, turning $106M of them
     * away; the desk in 5,253, $144bn - 79% of it the world's sales, 21%
     * emigrants', next to none the households'. The city: 162,273 people at
     * the end on average, 39,676 at month 1,300; the bank's growth years a
     * ratio of 19.2% against a target of 16.5%, a return on equity of 13%,
     * 76% of its profit paid out, provisions 0.4% of its loans (1.1% through
     * the run); the currency 42-57% under parity; prices peaked at 1.1-1.6
     * times founding, one seed 3.9 in a boom at month 1,000-1,300.
     *
     * SAVE_FORMAT did not move. Three new keys - bankAllowance (each book's
     * allowance, what it opened the month with, its write-offs and whether
     * it is watched), bankCapitalRecord (the loss record the target reads,
     * and the year of dividends and buybacks) and bankMonthLines (the
     * month's statement) - that an older save reads as missing: the loss
     * record starts empty (a young bank's target), the month lines as a
     * month not yet played, and the allowance is set up on load from the
     * book the save carried, by the same rules, with the month's opening
     * allowance set equal to it so that it is no provision and moves
     * nothing in the audit. A sector's allowance entry carries its stage-two
     * share as a fifth figure since the fixes above; a four-figure entry
     * reads its watched flag as the whole book or none of it. The quarter's
     * readings (creditStatements), the builders' cost of their salvage
     * (salvageCost, an extra) and a statement's stock paid for earlier
     * (paidEarlier) are new keys an older save reads as none.
     *
     * 0.7.9 (2026-09-23) - THE BANK TAB, REDONE.
     *
     * Jerus: "a redesign of the bank UI info, cause when you click on bank
     * you dont even see all the relevant stuff, lets make banks realistic."
     * The tab opens on whether the bank is healthy and why: its state in a
     * sentence with the figure that decides it (Bank.status()); a scorecard
     * - its profit this month and over the year, its return on equity, its
     * capital ratio on a bar against the minimum, its own target and the top
     * of its band, its provisions as a share of its loans, its net interest
     * margin, its costs against what it earns, its loans and its deposits;
     * and the ladder of its rates, from the policy rate through what savers
     * get, what a loan's money costs it and prime in its four parts, to what
     * each borrower pays, every step in points (Bank.ladder()). Behind it
     * five pages: Profit (the income statement beside last month's, the
     * interest by who paid it, what it did with the profit, the last twelve
     * months), Lending, Funding, Capital & owners, and History.
     *
     * EVERY FIGURE IS A GETTER (Bank, WHAT THE BANK TAB READS). The old tab
     * worked out a dozen of its own, and some were wrong: a branch's reach
     * from the founding constants, a hundred times out after a reform; a
     * weight table with no term and no desk, which did not foot; a "relief"
     * that could read negative; "99900.0% capital" with nothing lent; a
     * leverage warning at 2.0 where the default point is 1.5; an
     * equity movement without the founding settlement. BankCheck (13)
     * asserts the ladder's parts are prime, the weight table foots, and the
     * equity's movement leaves nothing unexplained on a played city.
     * Opened lines stay open through the clock's redraw (Statement.opens()).
     *
     * NOTHING THE BANK DECIDES MOVED. Its interest is booked by who paid it
     * on the same sum, to the bit, and nothing in the month reads the new
     * figures.
     *
     * SAVE_FORMAT did not move. One new key, bankStatementYear (the months
     * before the one saved, each filed whole at the top of the month after),
     * which an older save reads as missing - its year starts with the month
     * it was saved in. The month's lines gain the interest by who paid it on
     * the end of their array, and the solvency record what the city has put
     * into the bank over its life, both read by their length.
     *
     * 0.7.10 (2026-09-24) - FOUNDING A CITY: A SMALLER ENDOWMENT, AND A
     * SCREEN TO FOUND IT ON.
     *
     * Jerus: "lets reduce the cash the city starts with, both the foreign usd
     * and the starting cash ... cause the city should borrow right". A city
     * founds on D$100M in the treasury and US$25M in the vault
     * (Game.FOUNDING_CASH, FOUNDING_RESERVE_USD), where it had D$2.5B and
     * US$1B. The endowment founds the village and pays for one of the first
     * big works, and the city borrows for the rest - the way long-lived
     * public works are financed, over the asset's life, so that the people
     * who use the plant pay for it. The vault is years of a young city's
     * imports against the IMF's three months. A trace of 0.7.9 found the old
     * treasury never drawn below D$2.467B in thirty years, and the old vault
     * effectively empty by month 1,140 with no harm done.
     *
     * And "a small thing at the start of the game where you choose the name
     * of the city and the currency ... (with option to just start at
     * default)". Start New Game opens a screen to found a city on
     * (FoundingScreen): its name; its money, named after it - Arden gives the
     * Arden dollar, A$, ARD - or by hand, a name and a three-letter code; what
     * the founders leave, on four presets - Lean, Standard, Wealthy (the old
     * start) and Custom - each with what it buys at a new city's invoices
     * (Game.whatItBuys()); and the world's inflation, moved there from
     * Settings, the only moment it ever acted. "Found with defaults" is the
     * old one click. The choices are a record on the city (Founding): set by
     * the one founding path, saved, never changed. The city's money is read
     * through the game rather than five static finals in Currency, so one
     * city's name cannot leak into another's; the window's title and the slot
     * list name the city.
     *
     * Two things the founding path had wrong, found on the way: a new game
     * after a load kept the loaded city's world, and the menu set the world's
     * mean after the world was reset, so the first year's realised inflation
     * was back-cast at the previous city's mean. Both are the founding's now,
     * set before the reset.
     *
     * Forty-two fixtures in twenty-five harnesses had been bought out of the
     * old treasury without saying so - a coal plant, a water plant and four
     * hundred houses on a D$100M city are refused, or a free-placed city's
     * running costs run the treasury dry and simulateMonths() stops short.
     * Only ten went red; an audit of every refused order and every refused
     * month found the rest. Each is handed the treasury it was written
     * against, the Wealthy preset's, explicitly, through setCashForTest().
     *
     * And the build screen offers a twenty-year bond beside its six-month
     * note (Game.BUILD_BOND_YEARS). On D$100M the INSUFFICIENT FUNDS page is
     * how a player pays for a first big work, and the note was its only
     * offer: the whole face back in six months, out of a treasury that was
     * short to begin with. A water plant bought on it left the treasury
     * D$16.2M overdrawn when the note matured, ten months on the central
     * bank's advances; on a twenty-year bond sized to the gap it never fell
     * below D$0.5M. Jerus chose to offer both - a long-lived asset paid for
     * with long-lived debt. The bond is sized so the cash it brings covers the
     * gap, the fees paid out of its face (Game.quoteLongBondForCash(), the
     * long bond's counterpart of the note's faceForNetProceeds()), rather
     * than guessed; the page shows each offer's rate, face, cash, monthly
     * cost and what happens at the end, the bond first, and each button
     * books exactly the quote above it. The founding screen says the rest is
     * borrowed there.
     *
     * SAVE_FORMAT did not move. Eight new keys - cityName, the currency's
     * five names, foundingCash and foundingReserveUsd - which a save from
     * before 0.7.10 reads as missing: it loads as Danzik, in the Danzik
     * dollar (DZD, D$), founded with D$2.5B and US$1B, which is what it was.
     * Its cash and its vault are its own either way.
     *
     * 0.7.11 (2026-09-24) - THE LANDLORDS BORROW ON INSURED MORTGAGES.
     *
     * A high rate cut the supply of homes much harder than it cut the demand
     * for them, and a trace of 0.7.10 found why: the landlords financed
     * housing on the same 36-month interest-only bullet as a mill, tested at
     * 1.25 times gross rent over the interest on the whole cost at their own
     * risk's rate. On autopilot their maturing loans went unrolled, the hole
     * in their till was added to every building's loan, and nothing was built
     * for two hundred months with the city 37% short of homes; held at a 10%
     * dial nothing was built for 333 years. Jerus chose what a landlord
     * borrows on, on Canada's terms: "Mortgages", "CMHC (Canada)", "Keep it"
     * (the rent floor), "Insured by the city".
     *
     * A residential building is bought with at least 15% of the landlord's
     * own funds - its till, what it holds abroad, and what its owners are
     * asked for when those fall short - and a Mortgage for the rest: at most
     * 85% of the cost, CMHC's 5.00% premium and the 0.75% forty-year
     * surcharge added to the loan and paid to the treasury, at a rate fixed
     * for ten years (the bank's ten-year funds-transfer price and its running
     * costs, no loss - Bank.insuredMortgageRate(); round 2 below adds the
     * capital the leverage ratio ties up), paid down
     * as a level annuity over forty and renewed at the day's rate at each
     * term's end. The lender's test replaces the old interest test for these
     * orders: the building's rent less its repairs and property tax must
     * cover the payment 1.20 times. When a landlord's debt is written down,
     * every instrument falls pro rata and the treasury pays the bank what
     * came off the insured mortgages - a promise, like a coupon - so the bank
     * weighs them at nothing (Basel III's sovereign-guaranteed 0%), sets
     * nothing aside against them, and its capital rule does not ration them
     * while the risk weights are what binds (round 2 below).
     * Everything else keeps its 36-month loan. The Bank tab has the mortgages,
     * their row in the weight table and their rung on the ladder; the
     * landlords' screen their mortgages beside their other debt; the budget
     * the premiums and the claims.
     *
     * AND ONE KNIFE-EDGE IT UNCOVERED: a bank that bought its shares back
     * down to its capital target exactly then issued new ones or not on the
     * last bit of a subtraction (Bank.OWN_ISSUE_DEAD_BAND).
     *
     * ROUND 2, THE BANK AROUND THE MORTGAGES. Measured, the first round's
     * bank lost the landlords' margin and their capital. Its equity at year
     * 100 was half the 0.7.10 bank's, and it failed five times as often.
     * Jerus chose four answers:
     *   - "Basel leverage ratio": equity of at least 3% of everything the
     *     bank has lent, whatever it weighs (Bank.LEVERAGE_RATIO_MIN). Every
     *     comparison of its capital with a requirement reads the larger of
     *     the two, and its own target and band scale in the proportion it
     *     chose on the risk side. The insured mortgage's rate carries the
     *     capital the requirement ties up (Bank.capitalPerDollar()). When
     *     the leverage requirement binds, the capital rule rations the
     *     mortgages with the rest.
     *   - "Close losing branches": the branch test in reverse. After two
     *     years of a book that does not keep its branches' staff, one closes
     *     a month, never the last, by a retired building's path
     *     (Bank.closesBranch()). A bank under its minimum no longer opens a
     *     branch for the capital the opening brings.
     *   - "Pay out after principal": a sector's dividend is Equity.PAYOUT
     *     of its income less the principal that fell due in the month, and
     *     its buyback cushion counts its interest and principal.
     *   - "Keep asking the owners": the down payment's raise stays, with
     *     its reasons written down (Game.consider()).
     * The count of months without cover is carried in the bank's last-month
     * record, which an older save reads without.
     *
     * SAVE_FORMAT did not move. A mortgage is saved in the business debts
     * typed "MORTGAGE", which an older save has none of, and its loans load
     * as they were; three new keys (mortgageRepaid, insurancePremiums,
     * insuranceClaims) an older save reads as none; and the government's
     * month gains its two lines on the end of an array whose reader checks
     * its length, so an older block reads the two as zero.
     *
     * 0.7.12 (2026-09-25) - THE FIRMS SELL BONDS.
     *
     * With the landlords on insured mortgages the bank failed 115 times over
     * eight seeds against 42 before, and the sector that broke it owed it
     * five to six times its equity: a whole industry could borrow only from
     * the one bank. Jerus: businesses borrow from the bank on "notes" - the
     * screens say "bank loans", since the city's own short paper is already
     * a note - and from investors on tradeable bonds. Eight rounds; what
     * shipped is below, and the project's the-firms-sell-bonds.md has the
     * rounds.
     *   - THE BOND (CorporateBond): ten years, bullet, coupons monthly, sold
     *     at par by bookbuilding - the coupon the lowest yield at which the
     *     bids fill it - with the bank underwriting at Game's own issue
     *     costs. Both desks ask BondMarket.plan(): the largest bond no dearer
     *     than the bank's loan, costs counted, and the bank for the rest;
     *     never more, together, than the bank would lend.
     *   - THE ORDER BOOK (OrderBook): price-time priority, a trade at the
     *     resting price, nobody obliged to trade, orders good for a month.
     *     The bonds trade on it and, since round 2, the shares: the dealer's
     *     quote is gone, the bank's desk is one participant within its
     *     capital and its two caps, offering what it holds over them at fair
     *     value (Exchange); a company's price is its last trade, fair value
     *     beside it, on the dividend it actually paid
     *     (Equity.dividendPerShareAnnual()), and that dividend is net income
     *     less NET repayment (Game.payDividends()). Every household cell holds and
     *     trades its own bonds and shares; the bank buys a bond only at an
     *     equal loan's yield, on capital over its target; companies with idle
     *     cash and the world by the rules that already sent money abroad and
     *     brought hot money in.
     *   - THE BANK PRICES CONCENTRATION: Basel's IRB capital with a
     *     correlation that rises with the book's sector Herfindahl, shared by
     *     the Euler rule, in each sector's rate and in the bank's requirement
     *     (Bank, THE BANK PRICES CONCENTRATION). No limit; the 1.25
     *     multiplier kept and flagged.
     *   - RECOVERIES BY INSTRUMENT (BusinessDebtManager): a loan recovers
     *     LOAN_RECOVERY (75%) and a bond BOND_RECOVERY (45%) wherever a
     *     default's loss is read. Round 1's loans-first split of the uniform
     *     60% is gone.
     *   - HOW A FIRM THAT CANNOT PAY ENDS (BusinessDebtManager): nothing is
     *     lent past the default point, read on the quarter; a sector short
     *     at the settle sells what it holds, then borrows, and what no lender
     *     covers defaults that month (CAN'T PAY MEANS DEFAULT); what is still
     *     unpaid is lent as an InterimLoan ranked ahead of its other debt, or
     *     the whole sector goes to the backstop if nobody will lend it
     *     (INTERIM FINANCING); a bank short of capital rations growth and
     *     keeps the working-capital line open (CREDIT LINES STAY OPEN). No
     *     sector runs an overdraft for months any more, no special dividend
     *     is paid, and a buyback's unspent money stays in the till.
     *   - A sector's orders for stock are limited to what it can pay for; a
     *     maker's inputs are bought whole (Sector, BUY ONLY WHAT IT CAN PAY
     *     FOR).
     *   - Found and fixed on the way: Luxury Retail costed its stock at a
     *     price nobody paid (LuxuryRetail.landedCost()); the distress rule may
     *     sell any plant making something the sector sells; a project loan
     *     and the shortfall desk's loan beside a bond are grossed up for
     *     their fees; the bond book's dust and a household's dust-sized debt
     *     scale with the currency; a month-end holding nothing is not a
     *     reading; the lender reads the sheets valued that month; and two
     *     fields a reload lost (Retail's household want, the price index's
     *     settling count).
     *   - SHIPS WITH HEALTHCHECK RED, on Jerus's word ("ship with it red,
     *     flagged"): read over a year, the city where care costs money is 8.4
     *     points hungrier than the free one, against a tolerance of 5.
     * The screens: the bond market on the Finances tab; a sector's bank
     * loans, bonds and interim financing on its Cash & debt page, and its
     * shares' book on Its owners; the households' bonds on the household
     * panel; the bank's bonds, concentration and desk orders on the Bank
     * tab; the world's bonds on the Trade tab.
     *
     * SAVE_FORMAT did not move. The bond market, each cell's own bonds, the
     * exchange's books and what defaults took off the bonds are new keys; an
     * older save loads with no bonds and its dealer's last quote as each
     * book's last price, and a round-1 save's households' pool is handed to
     * the cells by their claims. An interim loan is saved among the business
     * debts under its own type, "INTERIM-LOAN". Every array that grew keeps a
     * reader that knows its older widths.
     *
     * 0.7.13 (2026-09-26) - THE LAND OFFICE, THE DIAL'S DEFAULT, ROLLING WHAT
     * FALLS DUE, AND THE BANK'S BALANCE SHEET. Housekeeping, from Jerus's list.
     *   - THE LAND OFFICE (LandScreen): a plot's price is large in the money
     *     the toggle pays in - local money converting, US dollars from the
     *     vault - with the other beside it; its size reads in square
     *     kilometres (LandManager.km2Words(): the model's square feet
     *     converted exactly, to three significant figures); its button stays
     *     live, and short it opens the build screen's funding page sized to
     *     the gap - converting, the build screen's 20-year bond and 6-month
     *     note; from the vault, the same two terms in dollars on the world's
     *     curve, held in reserve (Game.quoteForeignForCash()), and the vault's
     *     dollars with the rest converted as a choice, never the default. A
     *     control above the plots buys the next N at once, each as its own
     *     button would (Game.buyLandParcels()).
     *   - A NEW GAME FOUNDS ON THE AUTOPILOT (Game.newGame()), by both of the
     *     founding screen's doors. A save keeps the hand it saved; a city
     *     built bare, as the harnesses and the playtest build theirs, keeps
     *     the hand on the dial for them to state their own.
     *   - ROLLING WHAT FALLS DUE (Rollover; Game, ROLLING WHAT FALLS DUE): by
     *     hand, in the same structure - a new game's default - or as 12-month
     *     notes. Between two presses the treasury nets last year's surplus
     *     from what falls due the next month and issues the rest a month
     *     ahead, sized so its cash covers it - Jerus's choice (round 2),
     *     which capitalises the old paper's discount or premium into the new
     *     principal at every roll (Rollover says what that did on a Lean
     *     city); a ledger of what it netted keeps a surplus from netting
     *     twice. On the Finances tab's borrow pages; -Dplaytest.rollover
     *     sets it for a playtest, SAME_STRUCTURE when unset.
     *   - THE BANK'S BALANCE SHEET: a sixth page on the Bank tab - what its
     *     totalAssets() and totalLiabilities() sum, line by line, this month
     *     against a year ago (Bank.Sheet, and a year of sheets filed at the
     *     top of every month), the loans by sector, the city's own deposits
     *     beside the sheet - and its equity in two parts (round 2), paid-in
     *     capital and retained earnings, each of equityMovement()'s causes
     *     routed to one of them (Bank, ITS EQUITY, IN TWO PARTS): a buyback
     *     comes off paid-in at all it cost, a dividend off retained.
     *
     * SAVE_FORMAT did not move. The rollover's setting, ledger and record,
     * the bank's year of sheets and its equity's two parts are new keys; an
     * older save rolls nothing, keeps its dial's hand, reads the year-ago
     * column as nothing until it has lived a year in this build, and shows
     * its bank's equity whole, without the split.
     */
    public static final String VERSION = "0.7.13";

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
