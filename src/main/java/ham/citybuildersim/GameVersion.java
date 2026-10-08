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
     *
     * 0.7.14 (2026-09-27) - THE CITY'S FUND, A FAILED BANK RESOLVED FOR ITS
     * SHARES, THE BANK'S PREFERRED, AND THE INSANE START. From Jerus's
     * answers (TreasuryFund has them whole).
     *   - THE CITY'S FUND (TreasuryFund; Game, THE CITY'S FUND AND THE BANK'S
     *     RESCUE): a market book of company shares and bonds, a rescue book
     *     and cash. A dial, 0 to 300% of the year's surplus and 0 by default,
     *     pays in once a year at December's close, before the rollover nets
     *     it, never taking the treasury under a month of its spending; the
     *     rule holds 70/30 on the market (Exchange.postFund(),
     *     BondMarket.postFund()), never over 10% of a company, rebalancing
     *     past 74% or 4 points under, and never a new issue; 3% of it a year
     *     is paid to the treasury monthly from its cash, the revenue line
     *     "Transfer from the fund". The hand buys and sells at fair value and
     *     pays in and draws out, off the budget. A Finances tab area.
     *   - A FAILED BANK IS RESOLVED FOR ITS SHARES (Game.resolveBank()):
     *     automatic on a new game, the Bank tab's button on an older save or
     *     a bare city. The old owners are wiped out; the city's preferred and
     *     warrants are cancelled; the city pays the hole and the capital to
     *     reopen as a promise (TreasuryLine.BANK_RESOLUTION), the central
     *     bank advancing what the treasury lacks; the bank reopens that month
     *     with every share the city's. Nobody outside the city pays: the
     *     absorption into the bank's own cash is gone.
     *   - THE BANK'S PREFERRED (Bank, THE CITY'S CAPITAL): a standing bank
     *     under its minimum asks the city, through the inbox, to buy senior
     *     preferred on TARP's term sheet - 1-3% of its risk-weighted book,
     *     5% then 9% cumulative, three years of consent, each block repaid
     *     whole at its third anniversary at par with its unpaid dividends -
     *     from capital over the bank's target, then new shares sold to the
     *     public, which dilute the city's - and warrants on 15% bought back
     *     at Black-Scholes, the same way, once none is left (any still out
     *     at ten years are exercised if in the money); an equity line of its
     *     own on the balance sheet.
     *   - A RELOADED CITY PRICES ITS PAPER AS THE LIVE ONE DID: the debt
     *     market's last strike, its inputs and its rate, is carried whole
     *     (DebtManager.marketToSave()), where the load re-struck it off the
     *     cash the save holds.
     *   - INSANE (Founding.Preset.INSANE): D$0, US$0, and a 20-year dollar
     *     bond at 3% owed abroad for the ground. The clock will not run on an
     *     empty treasury nobody advances (Game.clockRefusal()) and says
     *     where to borrow.
     *
     * SAVE_FORMAT did not move. The fund, the preferred and its record, and
     * the debt market's strike are new keys, the register's city shares ride
     * three new slots a company and the bonds' city holding a new field; an
     * older save loads with an empty fund, the dial at 0, the rescue on the
     * button, no preferred and no city shares, and keeps the load's own
     * strike of the debt market.
     *
     * 0.7.15 (2026-09-27) - THE DIALS: INSANE RUNS FROM DAY ONE, THE SKIP
     * RUNS ON AN EMPTY TREASURY, THE TARGET TO 20%, THE HOLDINGS TO 100%.
     * From Jerus's answers.
     *   - PLAY RUNS ON ADVANCES ("Play works from day one. The central bank
     *     covers what the treasury must pay, which starts with just the land
     *     bond's coupon; optional spending is refused."): 0.7.14's stop on
     *     the play clock is gone. An Insane city's promises take its
     *     treasury under nothing and the next settle advances the shortfall;
     *     anything discretionary is refused while it has no cash and no
     *     revenue behind it. Borrowing is how it builds.
     *   - THE SKIP RUNS TOO ("Same rule as play"): Game.simulateMonths() no
     *     longer stops at an empty treasury, only at a month that throws;
     *     its report says what the central bank advanced over it and what
     *     is owed at the end (TimeSkipReport.getAdvancedDuringSkip()).
     *   - THE INFLATION TARGET goes to 20% (DebtManager.MAX_INFLATION_TARGET,
     *     Jerus's number), and the strip's colours read from the target:
     *     grey within 3 points of it either side, amber further off, red
     *     more than 5 points over it (Jerus: "Red at target + 5 points";
     *     UserInterface.STRIP_INFLATION_OVER_TARGET) - not above 10% flat -
     *     or with prices falling more than 10% a year, whatever the target
     *     (STRIP_DEFLATION_ALARM).
     *   - THE HOLDINGS DIAL goes to 100% (CentralBank.MAX_QE_SHARE), a
     *     backstop for when the bank and investors will not hold the city's
     *     paper or the bank is in trouble: the central bank buys the bank's
     *     term paper first and then the households' (Game
     *     .buyPaperFromHouseholds()), selling only to the bank. The
     *     compression stays whole from half the paper
     *     (CentralBank.FULL_COMPRESSION_SHARE), and the city's rate floor is
     *     split by who holds the paper: the central bank's share at the
     *     policy rate, the rest at the bank's floor (DebtManager.floorRate()).
     *   - THE CENTRAL BANK ROLLS ITS OWN (Jerus: "Build the rollover
     *     fix", the way the Fed does): what it holds of a piece
     *     falling due it takes again at issue, par for par - a
     *     non-competitive add-on on top of what the city sells between the
     *     presses, pro rata to their face, at the issue's price (Federal
     *     Reserve Bank of New York, "FAQs: Treasury Rollovers";
     *     Debt.addOnForCentralBank(), CentralBank.buyAtIssue()). The rollover
     *     sizes the market's part; with nothing sold for the market and the
     *     rollover on, its par is issued it alone; by hand with nothing
     *     sold, it runs off. Last year's surplus pays the market's part
     *     first and then the central bank's par, which rolls only what is
     *     left (Jerus: "Surplus pays everyone"), so a city in surplus
     *     clears its paper whoever holds it. Holding more than its dial
     *     (QT), what it holds past the dial runs off and the rest is rolled
     *     (the FOMC's QT caps, the distance to the dial for the cap). The
     *     holdings step reads the book net of the month's maturity, trades
     *     no piece paying principal that month, and sells settled paper
     *     only. A surplus that pays only part of the central bank's par
     *     can leave it over its dial, and the step then sells the excess
     *     to the bank that month - left as built (Jerus: "Leave it (as
     *     built)").
     *
     * SAVE_FORMAT did not move. What the central bank has paid the
     * households for their paper, and what it has paid at issue rolling
     * its own and that paper's par, are appended to its save array;
     * an older save has done neither. A dial past a half saved by this build
     * and opened in an older one is held at the older build's half.
     *
     * 0.7.16 (2026-09-28) - THE YEAR BOOK AS CSV BESIDE THE TEXT. Jerus: "csv
     * if its cheaper token wise to read than xlsx, also keep the txt". The
     * export (Game.writeBooks()) writes year-book.txt and decade-book.txt as
     * before, and beside each its YEARS or DECADES table as year-book.csv or
     * decade-book.csv and its WITHIN table as year-book-within.csv or
     * decade-book-within.csv (GameFiles.yearBookCsv() and the three beside
     * it). RFC 4180, CRLF, a field quoted only when it must be, the header
     * the text's own column names, every cell the string the text prints -
     * a blank empty, a point for the decimal whatever the locale, no BOM.
     * Each table is built once (YearBook.Table) and the text and the CSV are
     * both written from it, so they cannot disagree; the text itself is the
     * same bytes 0.7.15 wrote, but for this number in its first line.
     * YearBookCheck section 15 reads both files back and compares them cell
     * for cell.
     *
     * SAVE_FORMAT did not move. Nothing enters the save.
     *
     * 0.7.17 (2026-09-29) - ROUND 1 OF JERUS'S FIXES FROM HIS CITY: THE
     * BUILDERS, THE HOUSING, THE ARRIVALS AND THE PAYROLL. From his answers
     * after a tracer ran a copy of his 150-year, 1.05-million city forward.
     *   - EVERY BUILDING GETS THE CREW IT CAN USE ("Share building work
     *     fairly"; "a university gets a bigger crew than a house"): the
     *     sites' output after the repairs is shared by each stack's
     *     buildings on site times their construction points to
     *     CREW_SCALE_EXPONENT, 0.70 - one less Bromilow's B, the exponent of
     *     the time-cost law T = K x C^0.30 - water-filled, no stack taking
     *     more than it still owes; what nobody can use is idle, never banked
     *     (BuildingManager.siteShares()). A save's progress past what a stack
     *     owes is cleared on load; it moves no money
     *     (BuildingManager.clearBankedProgress()). Every planner's lead time
     *     and order size, and the build screen's months to finish, read the
     *     wait an order would have at those shares (BuildingManager
     *     .waitFor(), Game.quoteMonths()). Jerus's own hybrid - a crew for
     *     every order first, the spare by size - was built and measured and
     *     is not what shipped (the round's notes).
     *   - THE BUILDERS COUNT REPAIRS AND STAFFING ("Builders count repairs
     *     and staffing"): Construction's months of queued work are read
     *     against what the sites are left after the repairs, a depot is
     *     ordered only if the city could staff it (Sector.staffableShare()
     *     at MIN_STAFFABLE_TO_ORDER, as the four sectors that already ask
     *     use it), and the spare-capacity rule weighs the repairs and the
     *     queue against the depots' staffed output, not their nameplate
     *     (sectors.Construction.retirementDemandAndCapacity()).
     *   - THE LANDLORDS HOLD MONTHS OF WORK, NOT ONE ORDER ("Landlords hold
     *     work, not one order"): Real Estate may keep ordering while what its
     *     sites owe, the order included, is at most MAX_ORDER_MONTHS of the
     *     builders' site output after the repairs, each order sized to stay
     *     inside it (BusinessInvestment.withinMonthsOfWork()); it counts its
     *     own homes on site as supply; and when its best home would not fit
     *     it orders the next smaller one that does (sectors.RealEstate.plan()).
     *     Every other sector keeps one order at a time.
     *   - ARRIVALS LIMITED, NOT SWITCHED OFF ("Arrivals limited, not switched
     *     off"): the wall that stopped all arrivals while anybody was left
     *     with nowhere is gone. A month's arrivals are capped at the room the
     *     placement has left - free doors by size and the doubling valve's
     *     room, asked of the match against today's doors and converted at
     *     the arrivals' own household mix (FamilyModel.roomLeft()) - and the
     *     crowding damper reads the households with a door of their own, so
     *     it falls as households double up (Migration.crowdingFactor()).
     *   - PAYROLL BY JOB TYPE, AND IDLE CREWS LAID OFF ("Pay wages by job
     *     type"; "Lay off idle crews"): every sector's and the utilities'
     *     payroll is each job type's wage for the posts of that type filled,
     *     not the whole schedule times the average fill, and the households
     *     are paid what the employers paid. The builders keep the crews
     *     their work needs - the repairs and what the sites owe, less the
     *     city's own works, over their depots' full-staffing output, over
     *     their own fill - never fewer than IDLE_PAYROLL_FLOOR of their
     *     posts, and lay the rest off: unemployed, on EI, free to take any
     *     other work, hired back when the work returns
     *     (sectors.Construction.strikeCrews()). The city counts only the
     *     posts offered; the planners and the landlords count every post,
     *     because a laid-off post comes back with the work
     *     (BuildingManager.getPostsWithheld()). The sick are still paid:
     *     sickness cuts output, never payroll. LabourCheck asserts both.
     *   - FOUND ON THE WAY: a company's share price and its dividend read a
     *     cancellation residue - a book or a payout a few picodollars from
     *     zero - as a value, by its sign; both now read nothing under the
     *     market's floor (Equity.priceOf(), dividendPerShareAnnual()).
     *     DenominationCheck's twin cities parted on them.
     *
     * SAVE_FORMAT did not move. The builders' share, need and fill ride in
     * their sector's extras (an old save offers every post until its first
     * month strikes them); a save's parked construction points are dropped
     * on load, which moves no money.
     *
     * 0.7.18 (2026-09-29) - ROUND 2 OF JERUS'S FIXES FROM HIS CITY: LABOUR.
     * From his answers to "Labour: which of these should it build?".
     *   - EVERY PLANNER CHECKS STAFFING ("Every planner checks staffing"):
     *     Retail, Luxury Retail, Restaurants, the makers' rule
     *     (BusinessInvestment.planMaker(): Industry, Materials, Agriculture),
     *     Food Processing, Heavy Industry and Mining ask Sector.staffing()
     *     before they order, as Construction, Manufacturing, Automotive, Rail
     *     and Business Services already did, and an order of several is held
     *     to all of them (Sector.staffableCount()). A building passes at
     *     MIN_STAFFABLE_TO_ORDER of its posts fillable from spare workers, and
     *     "the 20% it allows can't be jobs nobody can fill" - Jerus, "Truly
     *     unfillable only": a band counts as nobody can fill "only when it has
     *     no spare workers, nobody above who can step down into it, and no
     *     migrants who come for it" (Sector.Staffing.nobodyCanFill(),
     *     Migration.admits()). Every band has migrants who come for it, so in
     *     play the rule never binds; it stays for any band that ever has no way
     *     in, and InvestCheck causes it with a harness-only hold on arrivals
     *     (Migration.holdArrivals()). The advisor says which: "the city could
     *     staff X% of a Y; it wants 80%" or "no one could staff a Y's Z posts".
     *     Real Estate's homes carry no posts.
     *   - WORKERS TAKE THE BEST-PAID JOB ("Workers take the best-paid job"):
     *     a worker may hold any post at or below their band, and no worker
     *     holds a lower-paid post while a better-paid one they qualify for is
     *     empty (PopulationManager, WORKERS TAKE THE BEST-PAID JOB THEY
     *     QUALIFY FOR; Beaudry, Green and Sand 2016). Bands are filled from the
     *     top and a band still hiring that would pay more than the band above
     *     it is joined to it - one market at one wage, its workers split so
     *     every post in it pays the same (fillByBand()); a full band is priced
     *     no lower than the best-paid band below it still hiring. The market's
     *     supply, Migration's chance of work and the planners' spare read the
     *     same fill, so the unskilled wage can no longer pass the diploma wage
     *     while diploma holders are free to take unskilled posts, and a
     *     shortage spreads up the ladder. Unemployment is unchanged: only
     *     which posts stand empty moves. LabourMarket.multipleAt() and
     *     tightnessAt() are the curve in one place.
     *   - SOME UNSKILLED MIGRANTS ("Some unskilled migrants"): WageBand.NONE's
     *     arrival ceiling is the world's share of working-age migrants with
     *     no diploma, 7.5% (2021 Census, 2016-2021 immigrants aged 25-54,
     *     Statistics Canada table 98-10-0309-01, Ontario), read the way the
     *     graduate ceilings are, and the band is bought through the same
     *     reach() of its premium and chance of work: none at the going rate.
     *   - THE PEOPLE SCREEN says which bands filled as one market and how
     *     many over-qualified workers hold unskilled posts; its arrivals note
     *     says nobody arrives without a diploma only in a month nobody did,
     *     and the summary's no-diploma note no longer says it.
     *
     * SAVE_FORMAT did not move. Nothing enters the save: the fill is a
     * function of the posts, the workers and the licences, all saved already.
     *
     * 0.7.19 (2026-09-29) - ROUND 3 OF JERUS'S FIXES FROM HIS CITY: PRICES,
     * THE GRANT, THE BANK'S BRANCHES, LUXURY. From his answers to "Money and
     * prices: which of these?" and "Luxury Retail's markup: what should it
     * answer to?".
     *   - BUILDERS' PRICES KEEP UP ("Builders' prices keep up"): the labour in
     *     every building and repair price is at today's builders' wages - a
     *     template's points at a Construction Depot's founding wage bill a
     *     point, its own labour content, scaled by that depot's wage bill
     *     today over at the founding ladder - and the rest of its cash cost
     *     stays at its founding value (BuildingManager, THE LABOUR IN A PRICE
     *     KEEPS UP WITH WAGES). The quote carries the sales tax the builders
     *     remit, passed on: (work + imports + plant x (1 - rM)) / (1 - rB), so
     *     the tax falls on the owner once (Game, THE BUILDERS' PRICE). The
     *     owner pays the quote up front, as before, and as the crews draw the
     *     material pays what it cost them that month less what the quote
     *     allowed for it, or is refunded (MATERIAL AT THE PRICE WHEN IT IS
     *     USED; FAR 52.216-4's economic price adjustment) - the city on its
     *     own line, TreasuryLine.BUILDING_ESCALATION, owed to the builders as
     *     arrears when the ceiling binds. A business that makes taxable
     *     supplies claims the tax on its buildings and repairs back
     *     ("Businesses claim it back"; CRA RC4022, capital real property). And
     *     ("Both rebates", 2026-09-30) a landlord's new apartment building -
     *     four homes or more - gets all the tax on it back as purpose-built
     *     rental housing (CRA RC4231; ETA s. 256.2), and any other new home
     *     the new residential rental property rebate where its value allows
     *     ($6,300 at most, gone at $450,000, in founding money at the price
     *     index: a House mostly bears its tax), claimed at the strike after
     *     the work is billed; no rebate reaches a repair. The city's own
     *     rebate (CRA RC4034, municipalities 100%) is the tax coming home: the
     *     builders remit it to the treasury as they bill the work, so no line
     *     is added. The bank's branches bear it
     *     (EconomyManager, THE REBATES ON A NEW HOME, AND THE CITY'S). A depot's profit estimate
     *     counts the posts it would fill at today's wages. Book values and
     *     property assessments are as they were.
     *   - THE GRANT FOLLOWS PRICES ("Grant follows prices"): the default basis
     *     is a fixed amount in founding money at the month's price index,
     *     equal at founding to the old default (TaxPolicy.DEFAULT_FIXED_GRANT).
     *     A save keeps the basis it was played on; an older save's fixed
     *     amount is read at the price index it loads at.
     *   - THE BANK'S BRANCHES BY THEIR CUSTOMERS (Jerus: "one branch maintence
     *     and operating costs should be less than the revenue it makes of fees
     *     for that specific branch, that should always be true"; "First branch
     *     exempt"): the founding branch is the city's charter and stays; every
     *     other opens only while there are more than CUSTOMERS_PER_BRANCH
     *     customers for each branch standing (16,000, TD's clients per branch)
     *     and the month's account fees would cover every branch with it, and
     *     closes - as many at once as it takes - when they do not. It decides
     *     on last month's cost ("Accept the lag", 2026-09-30): a month whose
     *     wages rise can run a branch short once, and the next month closes
     *     it. The charter pays its staff and repairs but not its template's
     *     operating cost ("Exempt first branch", 2026-09-30); every later
     *     branch pays all three, and the rule reads what a later branch
     *     carries. A later branch costs its payroll, its repairs and its
     *     template's operating cost, which the bank pays since this version,
     *     and the planner asks Sector.staffing() of it like every other
     *     planner. The deposit cap a branch is gone ("Drop it: online
     *     banking"): the bank lends against all of its customers' savings
     *     (Bank, THE DEPOSITS ARE NOT CAPPED BY BRANCHES).
     *   - LUXURY'S MARKUP READS THE CUSTOMERS AT ITS PRICE ("The customers
     *     actually served"): the margin is struck on the shoppers who would buy
     *     at the price it charges - a fixed point - not a queue counted at the
     *     floor (sectors.LuxuryRetail.strikeMargin()).
     *   - Founding.MIN_CASH is D$6M: ten houses and a shop at a new city's
     *     invoices, D$5.37M with the builders' tax in them.
     *
     * SAVE_FORMAT did not move: what enters the save is read as absent from an
     * older one - each payer's contract on site (DataSave.contractRecords; an
     * older save's contract is its template owner's - Game, OLD CONTRACTS),
     * the sectors' capital purchases in the month's ledger and statement, the
     * builders' escalation in their extras, the bank's customers and operating
     * cost in its month's lines, last month's operating cost and what a later
     * branch carried of it (two slots appended to the bank's last month; an
     * older save's last month paid none), the share of the tax each payer's
     * contract gets back (ContractRecord.recovered; an older record reads it
     * off creditable), and the grant's marker that its fixed amount is real
     * (one slot appended to TaxPolicy's state).
     *
     * 0.7.20 (2026-09-30) - THE INTERFACE'S BUGS AND ITS LAYOUT. From Jerus's
     * play-through of 0.7.19 for the interface alone (the project's
     * playing-0-7-19-ui-notes.md; "all you listed is good"). No figure in the
     * model moves: a default playtest writes the same traces as 0.7.19's.
     *   - CITY HISTORY OPENS AT THE TOP: "Write the year book", the page's
     *     only focusable control, at its foot, took the focus the last
     *     screen's button left behind and the scroller scrolled to show it.
     *   - THE KEYS WORK FROM ANYWHERE: a showing tooltip, a popup, hid itself
     *     on Esc and consumed the key before the scene's filter saw it - on
     *     the Build tab the pointer is nearly always on one. Every tooltip
     *     leaves Esc alone now; a text box keeps its typing, the founding
     *     screen's Esc still goes back, and an open dialog keeps Esc.
     *   - THE HEADER'S PRICES say when the first rate comes ("rate in ~37
     *     mo") rather than "no year yet": the basket is fixed after
     *     PriceIndex.SETTLING_MONTHS of shopping and the rate follows a year
     *     after (PriceIndex.monthsUntilRate(), which reads).
     *   - ON SITE: a build card says "N on site - ~M mo at today's queue" for
     *     anybody's order, and a Needs-you line with its fix on site says "1
     *     on the way, ~8 mo" and falls from red to amber - at the quote's own
     *     wait (BuildingManager.waitOnSite(), Game.onSiteMonths(), which read).
     *     The construction panel reads the same wait.
     *   - Every build time says "at today's queue". Tooltips have a panel.
     *     People flows are whole people ("under 1" below one) and no shared
     *     formatter prints a negative zero. Ratios to GDP say "annualised"
     *     before a year is recorded, and the Dashboard's GDP is annualised.
     *     Quit asks first. The centre column is top-aligned on every screen,
     *     the build hint keeps its line, the receipt is a popover with the
     *     last five purchases and their sales tax, and scrolled pages keep
     *     room under their end for the dome. Urgent notices are toasts,
     *     bottom right, three at most, fading after eight seconds. Lines
     *     wrap rather than cut. Build remembers its category for the session.
     *   - THE FOUNDING SCREEN no longer says what the money buys: each choice
     *     shows its treasury and its vault (Founding.whatItBuys() stays for
     *     the harnesses), and its page fills the window.
     *   - Trillions print as "$3.1T"; the Construction page's money is the
     *     screens' form (Formats.amount()); "kg" is its own plural; the load
     *     list says "Autosave" once; Construction with no depot reads
     *     "no depot yet"; covered services read "everything covered".
     *
     * SAVE_FORMAT did not move. Nothing enters the save: the remembered build
     * category, the receipts and the toasts are this window's, for the
     * session.
     *
     * 0.7.21 (2026-10-01) - COLOUR, THE HEADER, THE MENU AND THE FOUNDING
     * SCREEN. The second of the four interface batches Jerus agreed after
     * playing 0.7.19 for the interface (the project's
     * playing-0-7-19-ui-notes.md), built to the mockups he saw ("that is
     * damn pretty, go for it"). No figure in the model moves: a default
     * playtest writes the same traces as 0.7.20's.
     *   - ONE PALETTE AND IBM PLEX: the mockups' grounds, three greys, four
     *     area colours - people teal, money blue, business violet, building
     *     pink - and the three verdicts (ui.Palette). Plex Sans for words and
     *     Plex Mono for every figure, loaded from the jar at start-up, the
     *     platform's faces if they do not load (ui.Palette.Fonts; the SIL licence
     *     beside the files in resources/fonts).
     *   - COLOUR THAT MEANS SOMETHING: each page's title carries its area's
     *     swatch; a labour shortage reads amber, not red; a sector earning
     *     nothing draws a grey line; the Government's rings take colours that
     *     can be told apart; the build tabs carry a dot for who builds them,
     *     with a key, in place of amber words.
     *   - THE RAIL is at the window's edge, 76 wide, each button an icon over
     *     its name, in its area's colour when showing; Menu replaced the gear.
     *   - THE HEADER: the clock - play, the date, the month and the speed -
     *     and six tiles: population, GDP annualised from the first month,
     *     inflation against the target, out of work, the treasury, the rate
     *     and the currency; each with its change and a ten-year sparkline off
     *     the history, and a click opening City History on its line. The
     *     rating and the inbox at its right. The floating time controls and
     *     the net-income dome went; the toasts keep the stage's corner.
     *   - THE MAIN MENU is drawn over the whole window, over a skyline the
     *     game draws: Continue (saying which city it goes back to), Save
     *     while a city is open, New city, Load a city, Settings, Quit, the
     *     cities saved last, and the version.
     *   - THE FOUNDING SCREEN is one panel over the dimmed skyline: the name,
     *     its money in a line, four cards for what it starts with, five for
     *     the world, Found and Back. "Found with defaults" went.
     *   - TEXT IN THREE LAYERS: a short line, and an (i) whose popover holds
     *     the rest (ui.Pieces.infoButton()) - on the build tab, Construction's
     *     billing (Sector.Line.note(shown, whole)), the one tax dial, the
     *     Government's last row, the founding screen, the menu and the header.
     *   - Every sector page's money is the screens' form (Formats.amount()).
     *   - BuildMenuCheck checks the taxed price (Jerus: "Update to the taxed
     *     price"), from the price rule's own inputs, and runs in the cloud.
     *   - YearBook.realGrowth() is public, for the GDP tile; it reads.
     *
     * SAVE_FORMAT did not move. Nothing enters the save: the fonts are the
     * jar's, and the menu reads the slots' headers it already wrote.
     *
     * 0.7.22 (2026-10-01) - THE CONSTRUCTION PANEL, WITH PRIORITY, RUSH,
     * CANCEL AND DEMOLISH. The third of the four interface batches from
     * Jerus's play-through of 0.7.19 ("not just a blue loading screen"), and
     * the new play he asked for with it: "not only repirotize and cancel but
     * also destroy buildings, like you yourself destroy buildings". His
     * answers on the mechanics (2026-09-30) are ConstructionControl's five
     * rules, each with its source; nothing of them runs unless the player
     * uses it, and a default playtest writes the same traces as 0.7.21's.
     *   - PRIORITY ("Both"): the city's own sites take the city's share of
     *     the crews, as the rule gives it, in the order the player sets, top
     *     down; nobody else's site moves a point (BuildingManager.plan()).
     *   - RUSH: a city's site on a 50-hour week, at the Business Roundtable's
     *     overtime productivity (Report C-2, 1980) - 1.14 times a month's work
     *     in the first month, 1.02 in the second, 0.94 after - for 1.375 times
     *     its crews' wages, the premium paid by the treasury and paid out as
     *     wages (TreasuryLine.BUILDING_OVERTIME).
     *   - CANCEL ("Keep the half-built shell"): termination for convenience,
     *     FAR 52.249-2 - the month's work billed, the contract left refunded
     *     out of the builders' book, the shell stopped on its ground until it
     *     is restarted at today's quote or demolished.
     *   - DEMOLISH ("City's, plus buy-outs"): 5% of the building's work
     *     (SIGTARP's Detroit average over NAHB's 2015 home), the building
     *     closed as the next month starts, its material sold to the builders
     *     by the 0.7.8 rule and its ground freed when done; a business's or a
     *     landlord's building only after a compulsory purchase - market value
     *     and the business loss, by Ontario's Expropriations Act.
     *   - THE CONSTRUCTION PAGE: Sites, Timeline and Demolish, from the right
     *     panel's "Open" and from Build; every cancel, demolition, buy-out
     *     and restart confirmed in a dialog with its money in it; the inbox
     *     says the month before a rush enters its third month, and when a
     *     demolition is done.
     *   - ONE WAIT FOR A SITE (after the docs pass): the page, the right
     *     panel, a build card, the Needs-you line and the build quote all
     *     read one getter (Game.siteMonths(), onSiteMonths(),
     *     quoteCityMonths()) - the rule's to the bit with the player's hand
     *     off, the order's and the overtime's with it on; and an order lasts
     *     as long as its sites: a city site placed under it joins it at the
     *     bottom, and it clears itself when none of its sites is left.
     *   - Cold-start Settings, Load and Save over the menu's backdrop; the
     *     five longest paragraphs on screen a line and an (i).
     *
     * SAVE_FORMAT 28: see below.
     *
     * 0.7.23 (2026-10-01) - THE CHARTS. The last of the four interface
     * batches from Jerus's play-through of 0.7.19: "in teh graphs you should
     * be able to pan the chart just like yahoo finance does", "like also the
     * crisis labels and all", and "let the player click fullscreen on the
     * graph so the whole screen concentrates on the graph". A default
     * playtest writes the same traces as 0.7.22's.
     *   - THE DECISION LOG (DecisionLog): every change of a policy and every
     *     spend at scale, a month, a kind and a line - "Taxes to 17%",
     *     "Central bank rate to 0.00%", "Bank rescued for its shares",
     *     "Rushed University" - written where each is applied (TaxPolicy's
     *     setters, the wage floor, the central bank's dials, the share of
     *     tuition, and Game's methods for the standing subsidies, the bank,
     *     the fund, the paper, the money and the queue), held while a city is
     *     founded or loaded, and saved. Ordinary build orders and land
     *     purchases are not decisions; the rollover's issues and a default the
     *     city could not avoid are not either.
     *   - THE CHART, REBUILT (ui.TimeChart over ChartModel): drag to pan, the
     *     wheel to zoom about the pointer, double-click to reset; 1Y, 5Y,
     *     10Y, 50Y and All; an overview of the whole history with a window
     *     to drag and stretch; years on every axis and months when they fit;
     *     a crosshair card with every line's value in its unit; a legend
     *     that hides and shows lines; each axis its own nice scale, a per
     *     cent from zero; lines in their areas' colours, never a verdict's.
     *   - CRISES NAMED: each recession band carries its episode's name, and
     *     a hover or a click gives the rule that named it and its depth and
     *     length (YearBook.recessionBands(), trigger(), worstWords()); every
     *     episode on a lane under the chart; the player's decisions as
     *     flags under that, one a month with a count.
     *   - FULL SCREEN: the chart over the whole window, Esc back; the clock
     *     as it was; F11 still the window's own.
     *   - EVERY OTHER CHART: the small charts, the bank's, Finances' and the
     *     share price's have years under them; the sector cards' sparklines
     *     and the header's mark each January; none draws in red, amber or
     *     green.
     *   - ChartCheck, the sixty-sixth harness.
     *
     * SAVE_FORMAT 29: see below.
     *
     * 0.7.24 (2026-10-01) - THE BUILD SCREEN AND THE FRAME. The first of the
     * interface redone "slowly, one at a time, starting with buildings", after
     * Jerus's second look at 0.7.23: "the money one has is barely visible to
     * see as well as ones income", and "when you start the game you start in
     * residential so the player without reading thinks he needs to building
     * houses". His answers on the round-2 mockups: the money "Own block by the
     * clock", the frame "B: panels fold away" for every screen, the Build home
     * "Keep all three rows", the renames "Yes, rename them". The model is
     * untouched: a default playtest writes the same traces as 0.7.23's.
     *   - THE MONEY BLOCK: TREASURY left the tile row for a block by the
     *     clock - the cash at 28 px, the month's net income under it as
     *     "+$1.5B a month" (the tile's figure, Game.getIncome(), which is not
     *     the change in the cash, and the tooltip says so); a click opens
     *     Finances. The other five tiles put their label and a sparkline on
     *     one row, and no label, figure or change line is ever cut: a change
     *     line too long for its tile is shortened, never clipped.
     *   - THE FRAME FOLDS AWAY: the City overview is a drawer the header's
     *     "Needs you" chip opens, always on NEEDS YOU, whatever mode the
     *     panel was left in (pinned, it stays across screens); Under
     *     construction a 44 px tab that opens the panel over the stage; NEXT
     *     DUE a card at the top of the Finances hub, its red maturity a NEEDS
     *     YOU row, FALLS DUE. The inbox's list and the drawer close on a
     *     screen change, on the main menu and on Esc (the list stayed open
     *     before, a fix).
     *   - NEEDS YOU, IN THE MODEL (CityNeeds): the panel's list, its lines and
     *     its order, moved out of the interface whole, so the header's chip,
     *     the panel and the Build overview read one set of verdicts. An
     *     amber TREASURY row says why it is listed - the cash, under a
     *     month's tax - where it said "in hand".
     *   - BUILD OPENS ON AN OVERVIEW: the city's job (a tile and a ring for
     *     each of the five only the city builds, the worst need NEEDS YOU
     *     lists for it), what would help most (up to three orders by
     *     BuildAdvice's rule, each the real quote its Order button charges)
     *     and what the market builds. Residential is Homes, Commercial Shops,
     *     Industrial Industry, Infrastructure Roads & transit, Services
     *     Offices.
     *   - THE CITY'S FIVE OPEN ON THEIR NEEDS: a ring per measure, the cards
     *     of the picked one - each saying what it does in the measure's own
     *     verb ("seats", "puts N officers on the street") - with a bar for the
     *     cost per unit served and one for the posts the city likely cannot
     *     fill per 10,000 served, and an order bar.
     *   - BuildAdviceCheck, the sixty-seventh harness.
     *
     * SAVE_FORMAT did not move: the drawer's pin and the construction panel's
     * fold are GamePrefs (settings.json), and an older settings file opens
     * with the drawer unpinned and the panel folded.
     *
     * 0.7.25 (2026-10-01) - ONE BUILD CARD FOR ALL 73. Jerus, after seeing
     * 0.7.24: "also the build card for every building, i think the card
     * itself needs a redesign dont you think?" The market's nine had kept the
     * 0.7.21 card; every building has the city cards' skeleton now, its
     * figures in the model (BuildCard). A default playtest writes the same
     * traces as 0.7.24's.
     *   - THE CARD: an icon in who-builds-it's colour, the name, what stands
     *     and what is on site; the best of its group as tags; a hero that
     *     says what it gives the city - "houses 252 residents", "makes 1,200
     *     t of steel a month", "exports 300 seat-months of support work" -
     *     with a detail line; the price all in with the sticker; a money bar
     *     and a bar for the scarce resource, scaled within its group; what it
     *     needs and what it costs to run; the stepper with +100 and the
     *     quote, whose verdict now warns of a missing deposit or licence
     *     before the click, in buildStack()'s order.
     *   - THE MARKET'S NINE, IN THEIR GROUPS: under their owning sectors
     *     (Industry seven, Shops two), each heading with the sector's own
     *     figure; bar 1 the price per resident, customer, thousand meals,
     *     tonne or point a month - or for the makers, farms and vehicle
     *     plants the price in months of their value added at today's prices
     *     - and bar 2 the land per unit (an office's, the posts the city could
     *     not staff); and the order bar's left half and Build.
     *   - THE INVESTORS' LINE: on site if they are building it, otherwise the
     *     sector's word for the month and, in amber, "this one:" with the
     *     first gate this building fails for them now - the deposit, the
     *     licence, the staffing test, the land, or a loss in their estimate.
     *   - THE CITY'S FIVE got back what 0.7.24 dropped: the (i) and its
     *     cover, "runs $X/mo", +100, the share of the land free; a group of
     *     one draws its bars with no track.
     *   - A FIX ON THE LOAD PATH: the bank's planner had no bank for the
     *     first month after every load ("Holding: no bank"); the load hands it
     *     over as the month does (Game, beside setFamilies()).
     *   - BuildCardCheck, the sixty-eighth harness.
     *
     * SAVE_FORMAT did not move: nothing new is saved. The investors' words
     * are still not, so after a load the line says nothing was recorded
     * until a month runs.
     *
     * 0.7.26 (2026-10-01) - THE LAND OFFICE REDRAWN. The first of the rail's
     * other screens in Build's style, after Jerus's "the others are still
     * full of text and the design could be more intuitive and fun". The
     * model is untouched but for three reads the office used to work out or
     * write itself, and the receipt's words; a default playtest writes the
     * same traces as 0.7.25's.
     *   - THE GROUND: the ground free and the next plots as one bar - free
     *     now solid, the next N as numbered ghosts with a sand stripe where
     *     there is ore, on a scale of the two - under four cells: the ground
     *     free, who is waiting on it, what the world asks and what investors
     *     pay. The stepper moves the ghosts; a purchase widens the solid.
     *   - THE SHELF: nine wide cards three by three, cheapest ground a square
     *     foot first; the next N with their numbers and a pink edge; each
     *     with its value as a bar against the going rate and the world's
     *     price, and BEST VALUE, ORE, MOST ORE and NEW as tags of their own
     *     (one card can carry BEST VALUE and MOST ORE). Prices are neutral,
     *     red only when no way pays without debt; a Buy that needs a loan,
     *     or a vault that is short, says so in its label.
     *   - WHAT THE GROUND IS WORTH: the margin, the ground on top of the
     *     build, the ore and who is waiting, as four cards with their (i);
     *     the world's price of ground over the city's life behind "details";
     *     the funding page in the same frame, its offers as cards.
     *   - FIXES: "Who is waiting" reads the sectors the month found blocked
     *     (a sector refused at the last moment was missed); the ground's
     *     colour is NEEDS YOU's GROUND row here, on Build's LAND FREE and in
     *     the left panel, not the share used (red at 95% for centuries); the
     *     receipt and the journal's land line in the screens' money ("for
     *     US$101.8M", not "US$101,800k"); the funding pages light the rail;
     *     a funding page with nothing left to fund goes back to the office;
     *     the floor's wording; the going rate is the market's
     *     (LandMarket.goingUsdPerSqFt()); ore has a colour of its own
     *     (Palette.ORE) and an icon.
     *   - LandCheck asserts the going rate, the GROUND row's verdict and the
     *     receipt's money (its seventeenth section).
     *
     * SAVE_FORMAT did not move: nothing new is saved.
     *
     * 0.7.27 (2026-10-01) - THE PEOPLE PAGE REDRAWN. The second of the rail's
     * screens in Build's style. The model gained reads of what it already
     * struck, and keeps the month's migration, the dead by cause and the two
     * halves of the hunger across a save (format 30); a default playtest
     * writes the same traces as 0.7.26's.
     *   - PEOPLE, one scrolling page that leads with pictures: five vitals,
     *     each a door; the age pyramid with a settled city's shape as a
     *     ghost; the month as a waterfall - born, died by cause, moved in,
     *     moved out by why, the net - with a year view, the headcount
     *     counting up and the bars growing when a month lands; why people
     *     come as a bridge, jobs and homes to the draw and the city against
     *     it; care as Build's four rings, each a door to Build; the homes as
     *     a gauge with one verdict; the households as a mosaic of who lives
     *     in them, each tile its members drawn and a door to its books; the
     *     people outside the families as five tiles with their sparklines,
     *     the pool's month and EI; the skill ladder as bars with its chance
     *     and pay chips. Every table is behind "details", every paragraph
     *     behind an (i).
     *   - HOUSEHOLD MONEY, its own page: the grid with each tier's live wage
     *     at its head and the open cell's books beside it, held in view; a
     *     verdict that counts every row; the city's month as a waterfall;
     *     what the households have put by as four cards.
     *   - FIXES: the verdict that said every household covered its month over
     *     red rows; the rows below the rule measured against a basket, not a
     *     saver's plan; the city's month left out the bank's account fees and
     *     did not foot; the opened cell's fares under "healthcare" and its
     *     account fee under "interest"; the matrix's fractional households,
     *     its pay row at the founding wages and its retired in the unskilled
     *     column; licences in fractions of people; GOING SHORT says which
     *     hunger it is; PER WORKER became INCOME PER RESIDENT; the dashboard's
     *     HUNGRY reads the share of people, as the page it opens does; the
     *     Pensions page's "They can afford to eat" under a deficit, and its
     *     three lines that did not foot; four of the eight "things it fakes"
     *     were no longer true.
     *   - Pieces gained the page head, the chip, a ring as a card (Build's
     *     rings draw with it), a waterfall and a bullet bar.
     *
     * 0.7.28 (2026-10-01) - THE SERVICES SCREEN REDRAWN. The third of the
     * rail's screens in Build's style. The model gained reads of what it
     * already struck - the school leavers' diplomas gross (not saved: "not
     * recorded yet" after a load until a month runs), what the homes draw of
     * the power and the water, the long sick by months ill - and the sick
     * rate's lines moved into CityNeeds; a default playtest writes the same
     * traces as 0.7.27's.
     *   - EVERY SYSTEM OPENS ON AN OVERVIEW whose one picture answers its
     *     question: the sick rate as one bar of its causes, each a door to
     *     where it is fixed, over the four kinds of care as cards (the
     *     coverage the month applied, the one thing each buys, its places
     *     against the people it serves, "Build for it ›"); the schools as a
     *     pipeline, the basic ladder to the diplomas to adult study to the
     *     four professions, each school's gate named; power, water and the
     *     road as capacity rows - now, at full staff, asked - with who draws
     *     each; the crime as one bar of its reasons beside Canada's, over the
     *     police, the cells and what it did. The pages behind lead with a
     *     picture too: the long sick by months ill, what childcare and
     *     senior care buy as scales from nobody covered to everybody, the
     *     ground and the month's dead, a course's gates as a funnel with the
     *     binding one outlined, the police before and after, the cells and
     *     the sentences. The books open on cost against what came back.
     *     Every table is behind "details", every paragraph behind an (i).
     *   - THE FRAME: the systems as chips with a verdict dot and a "!" for
     *     the month's news (an outbreak begins, a brownout begins, the last
     *     plot is taken, the first unburied, the basic ladder passes its
     *     line); each figure a door, with ten years' sparkline and its change
     *     on last month where the history keeps the figure. Build's rings
     *     get a "why ›" back.
     *   - FIXES: power is kilowatts, scaled to MW and GW (it was printed as
     *     watts, and as "units a month" on Build); "plenty" and "not for
     *     centuries" on full ground; the road's "Spare 0" in green at 163%;
     *     the schools' books listed the forgiven tuition as a cost and an
     *     alert compared a figure with itself; "new diplomas" was a net band
     *     movement (-18); the utilities called a private business, and
     *     "four fifths" of the water unpaid for (it is measured); "resident
     *     draw" that is the homes and the city's own buildings; senior care's
     *     "people" that were places; the elders left out of the death
     *     chances and of senior care's effects; the Health books' nought
     *     patients after a load; every verdict colour is NEEDS YOU's lines;
     *     the left panel's OFF SICK opens Health; "Build water" opens Build
     *     on water; Canada's prisoners a named figure.
     *   - The Infrastructure tab moved out of ServicesScreen into its own
     *     class, unchanged. Pieces gained a cause bar, a supply bar, a
     *     funnel, an effect scale, cohort bars and a door.
     *
     * 0.7.29 (2026-10-01) - THE INFRASTRUCTURE SCREEN REDRAWN. The fourth of
     * the rail's screens in Build's style. The model gained pure reads of
     * what it already works out - the flow curve at any use, the walk from
     * the trips the city makes to the load on its road, the transit
     * funnel's steps, the month's lorry bill three ways, each good's wedge
     * taken apart - and the railway keeps what it was allowed to bill and
     * what went abroad across a save (in its extras; format still 30); a
     * default playtest writes the same traces as 0.7.28's.
     *   - ROADS leads with the flow curve and the city's dot on it - a
     *     hollow dot where the road sites on site would leave it - beside
     *     FROM TRIPS TO THE ROAD: the streams stacked, less transit, plus the
     *     cars, less rail and highways, the streams on the road, with the
     *     capacity and the free-flow line through every row. Then a card a
     *     stream and the network in four figures. TRANSIT is a funnel from
     *     the commuters through the three ceilings - the lowest tagged - the
     *     fare and the cars to the riders, over its books and the fare.
     *     THE RAILWAY is the month's lorry bill split three ways - billed at
     *     home, paid abroad, kept - with its quote on a gauge, its track and
     *     trains, what it hauls and its business. FREIGHT is a bar a good:
     *     the world's margin, the freight paid abroad and the railway's
     *     charge, with a mark at what it would be by lorry. Every table is
     *     behind "details", every paragraph behind an (i).
     *   - THE FRAME: five figures - FULL and FLOW in NEEDS YOU's colour for
     *     the road, the only verdict there - each a door; the pages with
     *     their icons; Build › Roads & transit and the road over the years.
     *   - ONE ROAD: "162% full · 56% flow" in whole per cents, here, in the
     *     drawer, on Services' road card, on Build and in NEEDS YOU, in NEEDS YOU's
     *     ROADS colour (the drawer was red where Build was amber). Services'
     *     road row is one card and a door; Build's road and transit headings
     *     open Infrastructure.
     *   - FIXES: the room before the road slows read the raw trips (585 on a
     *     road 161% full); "paid abroad" was the shippers' saving; Build's
     *     transit ring said "carries 62.5k" where 41.4k rode; a commuter
     *     "costs" 1.00 a trip whatever the cars; the drawer's three decimals
     *     and the meaning it changed at the congestion line; the railway's
     *     allowed bill read nought after a load; a lorry row of noughts;
     *     verdict colours used as categories.
     *
     * 0.7.30 (2026-10-01) - THE SECTORS SCREEN REDRAWN. The fifth of the
     * rail's screens in Build's style. The model gained pure reads: a
     * business's month as a flow (SectorFlow - each good in and out with its
     * units and its money, the plant's six throttles and the rate they
     * multiply to; SectorFlowCheck holds its money to the statement and its
     * cascade to the rate), the investors' word read into a kind and a
     * sector's investors as one record (BuildCard), the operations page in
     * its two halves (Sector.plantLines() and ownLines(), operations()
     * unchanged), and a sector's plain bank loans; a default playtest
     * writes the same traces as 0.7.29's.
     *   - THE LIST is fifteen cards in Build's market order under four
     *     figures - what they kept, how many lost, who works there, the
     *     range they run at with the city's throttles named and a door to
     *     the Build category that relieves the thinnest. Each card: its icon,
     *     its name and Build group, what it kept, two years of it, its
     *     workers, a running-at bar with what cuts it most, and its
     *     investors' word in a kind.
     *   - A BUSINESS: "Sectors ›" and its name, five figures, its
     *     investors' line on every page. OPERATIONS is inputs → the plant
     *     (a ring of its rate and the six throttles as a cascade) → outputs,
     *     its own lines as a grid under it. INCOME is a waterfall over the
     *     statement, its ratios as chips. THE BALANCE SHEET is two bars on
     *     one scale and an owners card - the Bank's Owners page draws the
     *     same card. CASH & DEBT is the month's cash as a bridge, with its
     *     rate in parts, its leverage to the default point and its debt by
     *     kind. INVESTORS is the decision as one line, each building's first
     *     gate, what stops it in three tiles, the rules as chips and the
     *     four things the player controls as doors.
     *   - FIXES: the balance sheet left out what a business holds abroad and
     *     other businesses' bonds; the cash statement left out what was
     *     stolen and what was bought back; the operating rate's note said the
     *     thinnest of five ratios when it is six multiplied; a business with
     *     nothing standing read "Staffed 100%" and a rate in red; the
     *     Investors page read "Sold" and "Could not build" in green; OWES
     *     quoted a rate on no debt; the scrapping alert over nothing to scrap;
     *     taxes, revenue, bids and asks in verdict colours; a negative zero.
     *
     * 0.7.31 (2026-10-01) - THE GOVERNMENT SCREEN REDRAWN. The sixth of the
     * rail's screens in Build's style, and the city's money named three ways
     * everywhere: EARNED (the header's figure - "+$1.5B earned a month" now,
     * and "Earned" in the drawer), the budget's SURPLUS and what the cash
     * BANKED. The model gained pure reads: the walk from EARNED to the budget
     * (Game.getEarnedToBudget() and getEarnedResidual(), held by
     * TreasuryCheck), and EARNED read without striking four of the month's
     * lines (EconomyManager.getTaxIncomeNow()); and the national accounts'
     * rolling year is put back from the graph history on every load
     * (NationalAccounts.seedHistory()), so no "of GDP" reads one month
     * scaled up for a year after a load. A default playtest writes the same
     * traces as 0.7.30's.
     *   - OVERVIEW: the two rings - shares of the arcs drawn, a slice or a
     *     key row opening its line - with THE BALANCE between them, two bars
     *     on one scale and what was kept or short outlined in NEEDS YOU's
     *     colour; then FROM EARNED TO BANKED, three tiles and every step
     *     between them by name, each a door to where it is decided; the
     *     central bank and the budget against the economy as cards.
     *   - REVENUE and SPENDING: a ranked bar a line, opening into who pays
     *     on one bar and their rows, with a door to the dial or the screen
     *     that decides it; repairs and the transit fares named under the
     *     totals as outside the budget's; the debt, the services that charge
     *     and the pensions as three cards.
     *   - OUTPUT: City History's GDP layers lead, the four parts as cards,
     *     this month as one bar, the growth as five readings.
     *   - FIXES: repairs in the spending ring and list but not in the total
     *     (111%); the net cost of care and schooling printed with its sign
     *     turned over; verdict colours drawn as categories, and OWED's own
     *     60%/120% thresholds here and on Finances; "Steel exported" and
     *     "Scrap imported" for every sector's trade; an "of the change"
     *     column that read 1,038%; the header's (i) naming three of what
     *     EARNED leaves out; the pension alert every month; "the city's own
     *     staff" that was care and schools; a key adding to 100.3%; a hyphen
     *     beside a minus.
     *
     * 0.7.32 (2026-10-01) - THE FINANCES SCREEN REDRAWN. The seventh of the
     * rail's screens in Build's style: a hub that is the debt's dashboard,
     * six areas as cards, and every page one picture first, its paragraphs
     * behind an (i) and its tables behind "details". The model gained pure
     * reads: the ladder by the calendar year each payment falls in, with or
     * without a proposed issue, the next twelve months, the coupon, each
     * kind's principal and the rate a piece is valued at (DebtManager,
     * held by ForeignDebtCheck), a quote's payment schedule (DebtQuote),
     * the rollover's cash (Rollover.Plan.fromCash()), the bond market's
     * sums by issuer, the city's net position, and the service bands and
     * the soon line as CityNeeds constants. A default playtest writes the
     * same traces as 0.7.31's.
     *   - THE HUB: TREASURY, OWED, THE RATE, COUPON and NEXT DUE, one
     *     verdict each; WHEN IT FALLS DUE, a column a calendar year stacked
     *     by instrument, the dollar part striped, "later" a ghost, with NEXT
     *     DUE beside it; the rollover and the bank's rescue said once each,
     *     with next month's bar; the six areas as cards.
     *   - THE POSITION: the balance and the credit band, the debt against
     *     the economy, the debt and the rate over the years with the
     *     borrowing decisions as flags; a gauge of the next twelve months'
     *     coupons and principal against the take; who holds the paper and
     *     what the dollars are; the rate built up and the curve.
     *   - THE BOOK: a card a piece, Buy back on each. BORROW: the ask
     *     beside the land office's offer card, the terms as columns of their
     *     rate, the ladder with the issue as ghosts. MONEY, THE BOND MARKET
     *     and THE CITY'S FUND as bars and cards; Issued as a receipt;
     *     Default abroad as two equal cards.
     *   - FIXES: the ladder's "later" weighed as "year 13" with a false red
     *     alert, and its bars labelled a year early; "debt service" two
     *     different figures; debt judged red at 60% and 120% of output while
     *     the market charged AAA; "$" and "D$" on one page and rates with no
     *     unit; a dollar piece priced at the city's short rate where a
     *     buyback pays the world's curve; the net position taking an
     *     overdraft off twice; NEEDS YOU's doors landing on the last page
     *     open; the default page lighting Trade; "->" on the offer cards.
     *
     * 0.7.33 (2026-10-01) - THE BANK SCREEN REDRAWN. The eighth of the
     * rail's screens in Build's style: an Overview of the bank's state, its
     * capital in its band beside eight figures, and the ladder of its rates
     * drawn in their parts; six pages behind it, each one picture first, its
     * paragraphs behind an (i) and its statements and tables behind
     * "details", verbatim. The model gained pure reads: each sector's quote
     * kept in its parts as it is priced (BusinessDebtManager.quoteParts()),
     * an insured mortgage's running part (Bank.Ladder.mortgageRunning()),
     * the losses' watch line (Bank.LOSS_WATCH) and the rates' flags of two
     * kinds (ChartModel.flagsOf()), held by BankCheck; and a city just loaded
     * prices its businesses' credit with their record and the book's
     * concentration in it. A default playtest writes the same traces as
     * 0.7.32's.
     *   - OVERVIEW: the state as a banner; THE CAPITAL GAUGE on the measure
     *     that binds; PROFIT, RETURN ON EQUITY, CREDIT LOSSES, HOW FULL
     *     (NEEDS YOU's THE BANK row as a figure), MARGIN, COSTS, LENT OUT and
     *     DEPOSITS, each a door; THE LADDER, prime as its four parts and each
     *     borrower as prime and its own risk, record and concentration, its
     *     bonds under it; the six pages as cards with a year of their line.
     *   - PROFIT: a waterfall from the interest, by who paid it, to what it
     *     kept, this month or the year; the ratios and the year as cards; the
     *     trading desk as a card. BALANCE SHEET: what it owns against what it
     *     owes and its owners' as two bars on one scale, every line in the
     *     key; the deposits beside the sheet and its equity in parts as cards.
     *   - LENDING: the book by borrower, the bonds among it; a card a
     *     borrower - what it owes the bank, its bonds, its leverage on its
     *     scale; set aside and written off; the next loan's price in its
     *     parts. FUNDING: the deposits and what funds the book; savers, the
     *     central bank and what it can carry; the branches. CAPITAL & OWNERS:
     *     both ratios on their bands, BINDS on one; the payout and the
     *     equity's walk; the owners' card; rescues and the preferred.
     *     HISTORY: City History's charts, the decisions as flags, how full it
     *     is over time.
     *   - FIXES: "-0.00% a year"; amber families and green interest, verdict
     *     colours as series; a book that left out $166M of bonds; a borrower's
     *     "owed" that was its bonds held by anybody; a ladder that never named
     *     the concentration charge; every sector quoted without its record
     *     after a load; a branch verdict of "Yes" where the investors could
     *     not staff one; "at face value" on bonds at cost; 0.008% and 0.014%
     *     both "0.01%"; lent against capacity read as 94 times over; the
     *     Back button a page's length from the strip.
     *
     * 0.7.34 (2026-10-01) - BUTTONS THAT ASK TO BE PRESSED. Jerus, playing
     * 0.7.31: "everywhere you have build, like the build button, it should
     * be more intuitive aka like an actual button that is basically asking
     * to be pressed, cause currently its a tiny text". One action button in
     * Pieces wherever the player commits to building or buying, and one
     * door pill wherever a link sends them to Build or the land office. No
     * order, price or door moved; a default playtest writes the same traces
     * as 0.7.33's.
     *   - BUILD: every card's Build is the card's width under the stepper,
     *     in the building pink, and says the order - "Build 3 · $37.5M", "on
     *     credit", or why it cannot go ahead and the way out; at 0 it reads
     *     "Build · choose how many" and a press chooses one, never orders.
     *     The order bar, the suggestions and "Build all three" say the same;
     *     the credit page's offers say what they build and on which paper.
     *   - THE LAND OFFICE: each offer card ends in a full-width "Buy · D$1.0B"
     *     (outlined "on credit", or "the vault is short"); "Buy the next 5"
     *     and the funding page's offers the same.
     *   - DOORS: Services' "Build for it", Infrastructure's "Build · Roads &
     *     transit", "Build transit" and "Rail on Build", a business's "Build ·
     *     Homes" and its "Land office", People's "Build homes", the Bank's
     *     "Build a Commercial Bank" and Build's own refusal pages: pills.
     *   - FINANCES: the ladder's "later", past twice the tallest year, is
     *     drawn broken a little above it, so the twelve years fill the height.
     *   - SECTORS: after a load, one line over the cards says nothing is
     *     recorded yet, where fourteen of fifteen said "no word yet".
     *   - A double-click on either piece is one press.
     *
     * 0.7.35 (2026-10-02) - THE TRADE SCREEN, REDRAWN. Jerus, on the screens
     * not yet redone: "the others are still full of text and the design
     * could be more intuitive and fun". Trade & the world in Build's style
     * (the project's spec-trade-0734.md): one strip of five pages - Overview,
     * The month, What we trade, The currency, The reserves - and the five
     * figures over every page, each a door. No model behaviour moved; a
     * default playtest writes the same traces as 0.7.34's. SAVE_FORMAT 30.
     *   - OVERVIEW: what the city trades as mirrored bars a good, bought to
     *     the left and sold to the right, off the businesses' own books;
     *     the rate per US$ beside parity on a ten-year chart; the three
     *     gauges as cards.
     *   - THE MONTH: a walk from exports to the month's balance through the
     *     two accounts; the river one toggle away, with the income from
     *     abroad it dropped; what is held abroad and what the world holds here.
     *   - WHAT WE TRADE: every good, or every business; the ten years; the
     *     record since founding; the world's prices off the markets, freight
     *     in them.
     *   - THE CURRENCY: the rate on City History's chart, parity beside it
     *     (recorded from this build on), the crises and the city's decisions;
     *     the forces next month as bars either side of a line.
     *   - THE RESERVES: whose the vault is, how long it would last, what can
     *     leave, what else moved it, and the exchange with 0.7.34's button.
     *   - ONE PARITY RULE: amber past 25% either side, red past 50% - the
     *     drawer's THE CURRENCY row, which opens The currency now, the
     *     header's rate line and the tab alike.
     *   - AFTER A LOAD the month's flows read "not counted yet", not zero.
     *
     * 0.7.36 (2026-10-02) - THE POLICY SCREEN, REDRAWN. The tenth rail screen
     * in Build's style (the project's spec-policy-0735.md): a hub of four
     * area cards - Taxes, Wages, Money, Promises - over the levers that are
     * biting and the decisions lately made; the areas as chips in the head
     * and their pages as tabs; every dial a card with what it would do beside
     * it, before and after, following the thumb. No model behaviour moved; a
     * default playtest writes the same traces as 0.7.35's. SAVE_FORMAT 30.
     *   - EVERY "AFTER" IS THE MODEL'S: a staged set goes through a detached
     *     copy of the policy (TaxPolicy.copy()) and the owners' own reads -
     *     the tax take and THE BUDGET (PolicyPreview), the payroll lines, the
     *     pensions, the EI pool, the bank's choice for its savers. A dial at
     *     zero previews something now, and the EI bill is the pool's.
     *   - TAXES: the take as a bar, a card a tax with its ten years, every
     *     tax at once with the three rates as chips and marks once they have
     *     parted (no more "three rates" over the profit rate); each tax's
     *     payers ranked, a row opening into its own move, the bank among the
     *     profit payers; the staged set in a tray at the foot of the stage.
     *   - WAGES: the wage ladder against the floor in today's money.
     *   - MONEY: every rate on one line; the dial, the rule, the central bank.
     *   - PROMISES: the pension's cover beside a pensioner's month; EI's cover;
     *     who pays for care; who can afford a school place; the subsidies.
     *   - PROMISES, the figure, no longer counts the tuition the city waives:
     *     forgone revenue, not money out of the treasury.
     *
     * 0.7.37 (2026-10-02) - CITY HISTORY, FINISHED. The last rail screen in
     * Build's style (the project's spec-history-0736.md): the page at the
     * stage's width, not the 0.7.5 column of 760 pixels; the chart itself is
     * 0.7.23's. No model behaviour moved; a default playtest writes the same
     * traces as 0.7.36's. SAVE_FORMAT 30.
     *   - THE HEAD: "Write the year book" there, 0.7.34's button, its result
     *     a card under it; the strip about the city - its age, its hard
     *     times, what is running now (the worst first, a chronic one last),
     *     and the decisions made.
     *   - THE PINS as cards over the big chart, each with its move; the big
     *     chart and both pins follow the page's width.
     *   - A CARD A LINE: its figure, its move in neutral ink with an arrow
     *     (a rise was green and a fall amber, whatever the line), and where
     *     it ended in its range over the view.
     *   - HARD TIMES AND YOUR DECISIONS in view, a click each moving the
     *     chart there; every one since founding by kind behind "details".
     *   - DECISIONS FROM THE FOUNDING MONTH are on the chart's lane, at its
     *     first month.
     *   - EVERY SERIES THE HISTORY KEEPS can be drawn: GDP's parts, the
     *     central bank's year, the bank's capital, each band's dead a month,
     *     each sector's net income and workers, each company's fair value.
     *   - PRICES THIS MONTH: one line of counts, and every good on its band
     *     between what the world pays and what it charges behind "details".
     *
     * 0.7.38 (2026-10-02) - THE LOOSE ENDS OF THE REDRAW. Eleven small things
     * the rail screens' batches left for one another, each a display fix that
     * makes a screen agree with the model or with the others. No model
     * behaviour moved; a default playtest writes the same traces as 0.7.37's.
     * SAVE_FORMAT 30.
     *   - FLAGS ON THE SMALL CHARTS: the Bank's rates and Finances' debt and
     *     rate draw the decisions they are handed, on a smaller lane; the
     *     founding month's sit on the first month there and on Trade's rate
     *     chart, as on City History's. The Bank's (i) no longer says its
     *     chart can be dragged.
     *   - IMPORT COVER reads "under 0.1 months", not "0.0 months", on Finances
     *     and in the drawer too, and the drawer colours it as Trade does.
     *   - THE DRAWER'S "vs parity" is on the one parity rule (amber past 25%
     *     either side, red past 50%), not amber past 15%.
     *   - THE FLOOR in today's money on People; City History's line and the
     *     year book's column say they are the floor in founding money.
     *   - THE FARE on a dial card as Policy draws its dials, every row the
     *     model's own read at the fare under the thumb.
     *   - THE BANK'S LADDER: a shut-out sector's name reads red.
     *   - SECTORS' RATE BAR in the parts the rate was struck from.
     *   - Build's "all three" added up by the model; no method name in the
     *     pension's (i); members with no caller left removed.
     *
     * 0.7.39 (2026-10-02) - THE CITY'S FUND AS A BROKERAGE. Jerus: "like
     * wealthsimple trade ... search the shares and bonds and see and all, and
     * also the city fund should show pnl and acb and all that" (the project's
     * spec-fund-0739.md). The fund is four pages and a page a security, its
     * own screen (ui/FundScreen.java). Behind them a cost basis, FundLedger:
     * each holding's adjusted cost base by the average-cost method, what it
     * realized and the income it paid, booked where the holdings already
     * move - bookkeeping only; a default playtest writes the same traces as
     * 0.7.38's. SAVE_FORMAT 30: the ledger is saved inside the fund's own
     * state, and an older save counts what it holds at market value in the
     * month it loads ("cost from").
     *   - PORTFOLIO: its worth, the return over the chart's window and since
     *     it began (exact, from the fund's own record of every flow); a chart
     *     of its worth against what was put in, kept from this version on;
     *     every holding with its average cost and P&L, green and red as a
     *     verdict and nowhere else; each kind since it began; the closed lots.
     *   - SEARCH every listed company and every bond outstanding, as you type.
     *   - A SECURITY'S PAGE: its price and chart (a bond's, the cash it still
     *     pays), its facts, its book, its record, YOUR POSITION, and the order
     *     ticket, which says what the order would take off today's book,
     *     what would wait, what it would cost and leave, and the cap.
     *   - ACTIVITY: every trade, payment and event, newest first.
     *   - RULES & CASH: the dial, the 3%, the rescue book with its terms, pay
     *     in and draw out, and your orders.
     *   - YOUR ORDERS can name their price and be cancelled before the step; a
     *     buy goes no further than the 10% cap, and holds its money from the
     *     rule from the moment it is placed. None of it reaches a playtest.
     *   - AFTER ITS DOCS PASS: a buy's room under the 10% cap counts every buy
     *     of the fund's on the company as filled - the rule's bid and your
     *     orders on the book or waiting - so the rule's bid and two orders can
     *     no longer fill past it together; the rule's bid makes way for your
     *     orders, as its cash does (Exchange.fundRoom(), which the ticket's
     *     quote reads too; the ticket says "No room under the 10% cap" when
     *     there is none); THE RULE's words no longer say an order of yours
     *     can lift it past. An older save's rescue lot, seeded at what the
     *     city paid, has its checks. Search says a city younger than a year
     *     has a record shorter than the year, not one recorded too coarsely.
     *     The default playtest places no order: its traces stand.
     *
     * 0.7.40 (2026-10-04) - FIXES FROM PLAYING 0.7.39. Five things Jerus
     * found playing the fund's version. No model behaviour moved; a default
     * playtest writes the same traces as 0.7.39's. SAVE_FORMAT 30.
     *   - CITY HISTORY'S CHARTS STAY PUT: they crept right for ever, a pixel
     *     a pulse, the page widening to the charts and the charts to the page.
     *     They follow the window now, not the page, and a chart is cut at its
     *     edge rather than pushing what holds it.
     *   - BUILD'S ORDER BAR STAYS PUT: its stacked bar - now, on site, this
     *     order - is the house's segment bar, laid out at the width it is
     *     given.
     *   - A CLICK AT 20x GOES THROUGH: while a mouse button is down the clock
     *     runs the months and holds the redraw, and draws it once the button
     *     is up - a click's press and release land on the same button, and a
     *     slider or a chart is not rebuilt under the hand.
     *   - BUILDING ON CREDIT IS ONE PAGE FOR THE WHOLE RUN, in the land
     *     office's shape: "Build › Funding", the run's price against the cash
     *     and what it is short by, the bond and the note as cards, each button
     *     saying the run. Build all three, the order bar and Enter borrow for
     *     every order and place them all; a run that would stop at an order
     *     short of ground, ore or licences borrows only for the orders before
     *     it, and says so. The invoice is the model's, each order priced on
     *     the yard the ones before it leave. An order not placed stays on its
     *     card.
     *   - BORROWING ANY AMOUNT: the ask is typed - "40B", "2.5T", "750M",
     *     "12,000,000" - and scaled by ÷10 and ×10 and by steps that follow
     *     its size, with presets off the city's own figures: the minimum, what
     *     falls due within a year, a month's spending, a year of tax, what the
     *     treasury is overdrawn by. The ask is written in full and short.
     *   - MONEY AT A UNIT'S EDGE: 999.97B reads "$1.0T", not "$1000.0B".
     *
     * 0.7.41 (2026-10-04) - ONE RULE FOR EVERY SERVICE GAUGE: SERVED. Jerus,
     * playing 0.7.39: "some of the build stuff shows check no issues yet if
     * you click general care is at 90%, additionally roads are at 180% bad,
     * while general care 90% is bad, so its confusing to the player". Power,
     * water and the road read as a load, care and the schools as a cover; the
     * rule he chose: "Served %, higher = better". No model behaviour moved; a
     * default playtest writes the same traces as 0.7.40's. SAVE_FORMAT 30.
     * Built beside 0.7.40, both from 0.7.39, and merged with it: the two
     * ship together.
     *   - SERVED IS SUPPLY OVER DEMAND, unclamped, from the owners' getters
     *     (UtilitiesHandler.getPowerServed() and getWaterServed(),
     *     InfrastructureManager.getServed() and getTransitServed(),
     *     CityNeeds.careServed(), BuildAdvice.served()): the road 162% full
     *     serves 62%; care and the schools read their figures, "served".
     *   - ONE VERDICT, CityNeeds.verdict(): green and "enough" at 100% or more
     *     and past NEEDS YOU's line; red at or under its red line; amber
     *     between, "short" under 100% and "tight" from it. A tick only at 100%
     *     or more: Build's Overview shows a served row NEEDS YOU does not list
     *     but is not enough - general care at 90% - before its tick.
     *   - WHEREVER IT IS WRITTEN: Build's rings, tiles, suggestions and order
     *     bar (its figures, and its key's first word, "covered" in 0.7.40's
     *     bar); Services' utilities, care and schools; Infrastructure's SERVED,
     *     its hero and its curve (the flow against served, better to the
     *     right); the drawer; NEEDS YOU's rows ("62% served · 56% flow").
     *   - WHAT NEEDS YOU LISTS IS UNCHANGED - its levels decide the list, its
     *     order, the chip and Build's suggestions - but a served row takes the
     *     one verdict's colour, so a gauge and its row read alike.
     *
     * 0.7.42 (2026-10-04) - THE ANCHOR: EXPECTED INFLATION AND CREDIBILITY.
     * Jerus asked why easy money barely inflates; the answer was that nothing
     * in the city read the target, and constants in founding money held the
     * level. Batch P2 of the project's spec-inflation.md. Model behaviour
     * moves; the default playtest's traces are re-baselined. SAVE_FORMAT 30:
     * an older save has no anchor and is seeded, which is correct.
     *   - EXPECTATIONS (new): credibility rises on target and falls on a miss
     *     the rate does not lean against; expected inflation is the target
     *     weighted by credibility plus recent inflation; the expected price
     *     level compounds at it from the base month.
     *   - THE MONEY CONSTANTS FOLLOW THE EXPECTED LEVEL, never the index
     *     (Game.restrikeMoneyConstants()): the shelf's opening price, ground,
     *     building costs and upkeep, fees, tuition, pensions, a share's par,
     *     the bank's constants and account fee, the issue fee, the FIXED grant
     *     and the new-home rebates. The transit fare dial is real.
     *   - WAGES take half their indexing from expected inflation and half from
     *     chasing the index: pass-through one, never both in full.
     *   - THE RULE aims at any target (the neutral real rate plus the target);
     *     the real rates are ex ante; the investors' hurdle is real.
     *   - THE CURRENCY drifts at the credible part of expected inflation, and
     *     investors expect it back toward parity (UIP).
     *   - History and the year book record expected inflation and credibility.
     *
     * 0.7.43 (2026-10-04) - GROCERIES AT A PRICE, A STICKY SHELF, THE WHOLE
     * BASKET, AND FOOD ASSISTANCE. Batch P3 of the project's
     * spec-inflation.md: a shortage is priced, the price rations and the
     * poorest are priced out, which Jerus chose, with a dial that buys them
     * back in at the treasury's cost. Model behaviour moves; the default
     * playtest's traces are re-baselined. SAVE_FORMAT 30: every new figure
     * rides the end of an array or a key of its own, and an older save seeds
     * it - its households' groceries from the plan they made, its index
     * linked on its first month - which is correct.
     *   - DEMAND AT A PRICE (HouseholdBalance.groceryDemandOf()): a household
     *     asks for its baskets at the satiation price, SATIATION_MULTIPLE over
     *     the opening price, fewer above it at an elasticity of
     *     GROCERY_ELASTICITY, and never more than its food money buys.
     *   - THE PRICE THAT CLEARS (Retail.clearingPriceOf()): what the shops
     *     can hand over against what is wanted, found by bisection; the
     *     baskets are shared out at it and paid for at the shelf's price, so
     *     the poorest are handed least.
     *   - THE SHELF IS STICKY (Retail.stickyPrice()): a sixth of the way to
     *     the clearing price a month, in logs, between the floor and
     *     CLEARING_CAP over it, drifting at expected inflation; the kitchens'
     *     and counters' margins a sixth of the way to theirs; rent a lease's
     *     way to its target, drifting too.
     *   - RETAIL'S PLANNER builds for the baskets wanted at the floor against
     *     what the shops can hand over, where it counted people against
     *     coverage.
     *   - THE INDEX IS THE WHOLE BASKET (PriceIndex): groceries, rent, meals,
     *     luxury and the services' fees, weighted by the trailing year's
     *     spending, luxury capped at LUXURY_WEIGHT_CAP, chained every
     *     REBASE_MONTHS and on an older save's first month.
     *   - FOOD ASSISTANCE (TaxPolicy.getFoodAssistance(), default 0): a share
     *     of the baskets of every household they would take more than
     *     FOOD_ASSISTANCE_MEANS_SHARE of the means of, bid at the sale and
     *     paid at the till by the treasury - a promise, TreasuryLine
     *     FOOD_ASSISTANCE, a line in the budget, the national accounts, the
     *     money audit, the history and the year book. Its dial is on Policy's
     *     out-of-work page, before -> after through PolicyPreview.
     *   - HUNGER is counted in baskets got against baskets needed, meals out
     *     included; the shops' takings are split over the households by the
     *     baskets each was handed.
     *   - GroceryCheck (new) holds the sale, the shelf and the assistance to
     *     the cent; MonetaryCheck holds the five components and the chain.
     *   - A reload builds its stacks in the order the city had them
     *     (DataSave stackOrder), where it built them in the templates' order.
     *
     * 0.7.44 (2026-10-05) - THE LANDLORDS' LENDER TESTS AT THE REAL RATE, AND
     * THE GROCERS' SUPPLIERS WAIT A MONTH. The fix round after 0.7.43's
     * ensemble (runs/diag-0743.md): households with no home in ten cities of
     * sixteen at the founding step, and shops that ran out of stock, not of
     * shops, in 11.9% of the autopilot's months. Model behaviour moves; the
     * default playtest's traces are re-baselined. SAVE_FORMAT 30: what the
     * shops owe their suppliers rides their extras by name, and an older save
     * owes nothing, which is correct.
     *   - THE LENDER'S TEST IS REAL (Game.considerOnMortgage()): the
     *     landlords' mortgage payment is read at the insured rate less
     *     expected inflation, never under REAL_HURDLE_FLOOR of it
     *     (BusinessInvestment.realTestRate()) - the real hurdle every other
     *     investor has had since 0.7.42 - and written at the insured rate. A
     *     refusal says the rate it read.
     *   - SUPPLIER CREDIT (SupplierCredit, new; Retail.supplierCreditLimit()):
     *     a grocer whose till and lender cannot pay for the month's stock
     *     buys it on its suppliers' credit, up to a month of the baskets it
     *     expects to sell at what they cost to bring in, and repays it at the
     *     next strike out of that sale. Booked both sides - a payable on its
     *     balance sheet, a receivable on each supplier's, a line in each
     *     cash flow - and the world's share a financial flow in the money
     *     audit. It buys the shelf's stock and nothing else, and is not
     *     netted against the bills a grocer still defaults on.
     *   - The Sectors screen's books show both, and its investor rules say the
     *     rate each test reads.
     *   - SupplierCreditCheck (new) holds the credit to the cent.
     *
     * 0.7.45 (2026-10-05) - THE SCREENS CATCH UP WITH THE PRICES. The UI pass
     * over 0.7.42-0.7.44 (the project's spec-ui-0745.md): of the new price
     * model only the food assistance dial and the hunger had reached a
     * screen. Nothing reads the new figures in the month, so the default
     * playtest's traces are unchanged; one model rule moves, behind a dial
     * at 0 by default. SAVE_FORMAT 30: every new figure is appended or keyed,
     * and an older save seeds it.
     *   - THE ANCHOR on Policy › Money (expected inflation, trust in the bank
     *     and its month's move, the lean, the level the constants are struck
     *     at) beside THE CURRENCY'S DRIFT; the rate line's expected and
     *     neutral ticks, its inflation judged on the smoothed rate; the dial's
     *     real rate. The header's INFLATION line is the anchor, and NEEDS YOU
     *     gains PRICES - amber the month trust falls, red under half
     *     (CityNeeds.prices(), Expectations.getCredibilityStep(), saved as the
     *     eighth slot).
     *   - THE BASKET on PRICES: each part's weight and its own inflation;
     *     PriceIndex keeps each component's level chained across every link
     *     (after the ring in its save), and City History draws them, with the
     *     links as marks over the plot (ChartModel.basketLinks()).
     *   - THE SHELF on Sectors › Retail (the floor, the cap, the price that
     *     clears - a price only above the floor - the baskets, what limited
     *     the sale, the suppliers' credit), HANDED OVER as its fifth figure;
     *     WHAT IT CHARGES on the kitchens and the counters.
     *   - WHO GOES WITHOUT on a new Policy › Promises › Food tab and on People:
     *     the hunger's priced-out and short-of-stock halves; the voucher's
     *     dial previews at the price the last sale charged, so at rest it is
     *     the month's to the bit; THE BUDGET previews it.
     *   - FOOD ASSISTANCE'S MEANS TEST reads the year's investment income,
     *     smoothed over MEANS_INCOME_MONTHS, not the month's: a coupon month
     *     took a whole row in and out of assistance. A cell slot is appended.
     *   - Fixed: EARNED's walk carries the vouchers; Government's list and
     *     ring; People's waterfall; Trade's next month carries the anchored
     *     drift; the fare preview and the fare, the FIXED grant, the pension's
     *     base and the bank's paid-in read in today's money; the rule's slope;
     *     the real rates said less the year's inflation; seven stored series
     *     History never offered; the clearing price under the floor printed as
     *     a price. Before an old save's first month, every read of the sale
     *     says it is not counted yet.
     *
     * 0.7.46 (2026-10-05) - WHAT A SAVE CARRIES OF THE MONTH. Batch A of the
     * model fixes (the project's spec-model-fixes.md): flows a reload read as
     * nothing or struck again, and three history records that said something
     * other than their names. Nothing the month reads moves, so the default
     * playtest's traces are unchanged. SAVE_FORMAT 30: every new figure is a
     * keyed map, an extra by name or an appended slot, and an older save
     * reads what it always read.
     *   - THE MONTH'S TRADE CROSSES A SAVE (A1): every sector's units shipped
     *     and landed, carried beside the rows until the first strike
     *     (Sector, THE MONTH'S TRADE ACROSS A SAVE). The railway billed
     *     almost nothing the month after any load and repriced on it; a
     *     reloaded city now plays its first month as its unsaved twin - its
     *     people, arrivals and price index exactly, with twelve of 229 series
     *     still parting by under a ten-thousandth, a residual left unchased.
     *   - THE TRADE TAB'S MONTH (A2): the balance of payments' month and the
     *     treasury's dollars bought and sold ride slots 37-44 of the foreign
     *     accounts, so a freshly loaded city counts its month.
     *   - CARE'S FEES BY KIND (A3): the people each kind treated, appended to
     *     the service's state (Healthcare.STATE_BEFORE_SERVED).
     *   - THE MEANS TEST'S INCOME (A4): each cell's income after its fixed
     *     bills, one appended cell slot (CELL_SLOTS_BEFORE_AFTER_FIXED); a
     *     reload struck it again from the moment of loading.
     *   - CONSTRUCTION'S MATERIALS (A5): the row as it stood at the strike,
     *     kept (Sector.beforeBank()), so the page shows the statement's month
     *     and the row as "since then, so far".
     *   - GRADUATES (A6) are the month's gains at every level
     *     (Education.gainedThisMonth()), not the movement, which nets to 0.
     *   - THE BANK'S LEVERAGE (A7): bankLeverageRatio and bankLeverageTarget
     *     recorded every month; the Bank tab's capital chart draws them while
     *     the leverage ratio binds.
     *   - EPIDEMICS (A8) are named on Health's outbreaks (History's outbreak
     *     series, YearBook.EPIDEMIC_OUTBREAK), not on a sick rate over a
     *     tenth, which a city short of care held for its whole life.
     *
     * 0.7.47 (2026-10-05) - WHAT THE PAGES SAY, AND A FLOOR IN TODAY'S MONEY.
     * Batch B of the model fixes (the project's spec-model-fixes.md), B1-B8;
     * B9, the transit bill and G, waited for Jerus's word and shipped as
     * 0.7.49's D2. B1-B7 leave the
     * default playtest's numbers alone (B4 groups the thousands in one word
     * of t-house's why); B8 moves the traces from month 32, re-baselined.
     * SAVE_FORMAT 30: nothing new is saved.
     *   - THE SAVERS' REAL RATE PREVIEWED (B1) less the inflation they expect,
     *     as the month strikes it (PolicyPreview.realDepositRateAt()).
     *   - THE SHELF'S FLOOR (B2) is said struck at the level this month's
     *     constants are struck at (Expectations.getStruckLevel()), not the
     *     one the next month will be.
     *   - THE CAR PLANTS (B3) read the month: made of the nameplate, the parts
     *     ordered, bought and imported at the month's rate; the full-rate
     *     order is the note's.
     *   - THE LANDLORDS' HOLD (B4) groups its thousands.
     *   - A PROPERTY OFFSET (B5) is held at MAX_PROPERTY_TAX, where the dial
     *     always stopped: no offset past it rates differently.
     *   - A CURRENCY REFORM AND THE BUSES (B6): the month's transit bill and
     *     fares are divided with everything else, and the ridership curve and
     *     the fare dial's cap read the dial in founding money at the unit
     *     (InfrastructureManager.setFareUnit(), TaxPolicy.setMoneyUnit()).
     *   - GOVERNMENT'S "OF GDP" (B7) reads each line's last twelve months where
     *     City History records it (GovernmentScreen.TRAILING_MONTHS), not the
     *     month x 12.
     *   - A BAND HELD AT THE FLOOR IN TODAY'S MONEY IS PINNED (B8):
     *     LabourMarket.isPinned() reads cashMinimumWage(), the floor wages are
     *     held at, not the founding figure - so a glut there leaves, as
     *     Migration says it should, once living costs more than at founding.
     *
     * 0.7.48 (2026-10-05) - THE FUND'S WITHDRAWAL DIAL, AND THE FUND'S MARKS.
     * Batch C of the model fixes (the project's spec-model-fixes.md), C1-C5.
     * Jerus: "the city fund, you should be able to click how much to
     * withdraw automatically, even 0 or 10% a month". C1 and C2 leave the
     * default playtest's traces alone (the default is Norway's rule, to the
     * bit); C3, C4 and C5 move them, re-baselined. SAVE_FORMAT 30: the dial
     * is keyed and the month's two new figures appended, so an older save
     * loads Norway's rule, owing nothing.
     *   - THE WITHDRAWAL (C1): what the fund pays the budget a month, in
     *     quarter-point steps from nothing to 10% of its value
     *     (TreasuryFund.WITHDRAWAL_STEP, MAX_WITHDRAWAL_STEPS); one step,
     *     the default, is the 3% a year it always paid. Over the default it
     *     spends the fund: what its cash cannot cover is sold from its
     *     market book at the step, pro rata, and paid at the next month's
     *     top, and the rule buys nothing meanwhile; never the rescue book.
     *   - ITS DIAL (C2) on Finances > The city's fund > Rules & cash, first
     *     and full width, with what it would do next month and a year on
     *     (PolicyPreview.fundWithdrawalAt()); the transfer's card, Government's
     *     line and the fund's tile say what the dial sells and pays late.
     *   - THE RULE'S BID (C3) stands at the desk's ask, fair value plus
     *     TreasuryFund.RULE_PREMIUM: at fair value it met nobody, and its cash
     *     sat idle.
     *   - A STALE MARK (C4): the city's holding of a share is marked at fair
     *     value once its last trade is Exchange.STALE_MARK_MONTHS old
     *     (Exchange.cityMark()); trading, the households and the world read
     *     the last trade as before.
     *   - SHARE PRICES IN HISTORY (C5) are kept to six significant figures
     *     (HistorySave.SHARE_PRICE_DIGITS): a consolidated company's price
     *     per founding share was rounded to nothing.
     *
     * 0.7.49 (2026-10-05) - TRANSIT: THE BILL PAID, AND WHO RIDES BY WHAT IT
     * COSTS THEM. Batch D (the project's spec-transit.md), D1-D5. Jerus: "for
     * transit, fix the leak, and also, households shouldnt check the transit
     * price alone to determine if they use it, they should determine the cost
     * of their own transportation". D1 and D5 leave the default playtest's
     * traces alone; D2, D3 and D4 move them, re-baselined. SAVE_FORMAT 30:
     * three keyed figures, -1 in an older save (derived as before), and the
     * households' statement takes one scalar and one row on its end.
     *   - THE MONTH'S TRANSIT BILL CROSSES A SAVE (D1): DataSave.transitBill,
     *     as advanceDemographics() 6d struck it; the rebuild struck it at the
     *     fill the month ended on.
     *   - THE BILL IS PAID (D2, B9): transit's wages and upkeep were struck
     *     every month and paid by nobody. The treasury pays them as a promise
     *     (TreasuryLine.TRANSIT); getExpenses() and the budget carry the bill,
     *     the budget the fares (no longer journalled, and no step on the walk
     *     from EARNED); the audit debits it; G counts the schools and transit
     *     at cost. Government lists "Transit fares" and "Transit".
     *   - THE TEST PLAYER COUNTS THE WAGES (D3): LongPlaytest buys a transit
     *     line only while the output it frees and its fares pay its crews
     *     (linesThatPay()).
     *   - WHO RIDES, BY WHAT THEY PAY (D4): workers with no car of their own
     *     (HouseholdBalance.captiveShare(); a household holds one car) ride at
     *     any fare if a line reaches them and there is a seat, and walk if
     *     not; owners weigh a ride against a journey's fuel
     *     (Motoring.CAR_FUEL_PER_JOURNEY at the exchange rate) and split
     *     (InfrastructureManager.transitChosen(), MODE_SPREAD);
     *     TRANSIT_MAX_SHARE is the reach of each group. Drivers pay for fuel,
     *     imported; the fares fall on the rows that ride and the fuel on the
     *     rows that drive; a household without a car weighs a pass against a
     *     car's full monthly cost before it buys.
     *   - THE SCREENS (D5): Infrastructure > Transit's riders by reason; the
     *     fare card shows its founding anchor, a drive's fuel and a pass
     *     against an unskilled household's take-home; People's fuel; Trade's
     *     "Households' fuel".
     *
     * 0.7.50 (2026-10-06) - THE CHART THAT FROZE THE GAME. Jerus played 0.7.49
     * and on one run the window froze, the same exception in the terminal on
     * every frame. A screen fix: nothing the month reads moves, so the default
     * playtest's traces are unchanged. SAVE_FORMAT 30: nothing new is saved.
     *   - THE CAUSE: a chart kept the history's month list itself - the list
     *     the history adds each month to - and the stack's layers as the
     *     arrays they were. A month landed under Government's layers chart
     *     with the pointer on it; as the page was torn down, JavaFX told the
     *     chart the pointer had left, and its redraw read a month past the
     *     layers from inside JavaFX's removal of the page, which stopped half
     *     way and left nodes in the page with no scene.
     *   - A CHART DRAWS FROM A SNAPSHOT: TimeChart.setData() copies the months
     *     and every line and layer, each as many months long (ChartModel, WHAT
     *     A CHART DRAWS FROM), and the stack's arithmetic, now ChartModel's
     *     stackRuns() and stackReach(), reads nothing past the months or the
     *     layer. ChartCheck's section 8 walks it across a month landing.
     *   - NOTHING A CHART THROWS REACHES JAVAFX: a frame that fails is skipped
     *     and its fault logged once for the chart, with the stack trace; every
     *     pointer handler on a chart is guarded the same way.
     *   - A PAGE TORN DOWN HEARS NOTHING: while clearMenu() empties the page,
     *     rootMenu swallows the pointer's exits from what it removes, so no
     *     handler on the old page runs inside the removal.
     *   - THE LOG PAST ITS CAP: failures still go in, each once by its first
     *     lines, up to GameLog.FAILURE_BYTES more (RobustnessCheck).
     *
     * 0.7.51 (2026-10-06) - THE BUILD ADVICE, PRICED WITH ITS LAND AND SIZED
     * AHEAD. Batch F (the project's spec-build-advice.md). Jerus, of "Build
     * all three": "the building ideas is flawed, it doesnt take into account
     * land price, and it doesnt build any slack, and universities ... tend to
     * be overstated and way too early sometimes ... make it so that alot of
     * its ideas also take into account the same as businesses do, aka a
     * projection". Advice, not the month: the default playtest's traces are
     * unchanged, and the two model lines it moved (planMaker()'s growth,
     * advanceMonth()'s diplomas) are the same arithmetic, extracted. SAVE_FORMAT
     * 30: nothing new is saved; every new read is of saved state.
     *   - LAND: each building is ranked by its quote and its ground at the
     *     land office's price (LandManager.getOfficePricePerSqFt(),
     *     BuildAdvice.landValue()), a unit; the cards count the ground the
     *     ones before take. A card's price is still the quote.
     *   - AHEAD AND SLACK: an order is sized to the demand when it opens plus
     *     BuildAdvice.HORIZON months, grown by the businesses' own
     *     BusinessInvestment.growthFactor(), with BuildAdvice.SLACK on top, and
     *     a served gauge must reach 100% of that (BuildAdvice.ahead()).
     *   - HIGHER EDUCATION: a row for a school above the ladder - a college,
     *     a university, a professional school - wants the students the city
     *     would both get and hire (CityNeeds.wanted(): the smaller of who
     *     would come, its feeder's graduates by then counted, and the posts
     *     their degree fills); a first school needs
     *     CityNeeds.FIRST_SCHOOL_SHARE of the smallest's seats.
     *   - NO CASH CAP: a card is the count that keeps its need ahead, on
     *     credit when the cash the ones before leave is short, and says by
     *     how much. "Build all three" reads the run the model places
     *     (Game.buildRunAhead(), buildRunStop(), buildFundingGap()) and its
     *     total is Game.buildRunInvoice().
     *   - THE CARDS: what the order does, when it opens and what it is sized
     *     for, and its ground, with the ranking behind the choice in a
     *     tooltip; a count above one says why so many. Services > Education's
     *     school not built names the posts for its graduates beside who
     *     would come.
     *
     * 0.7.52 (2026-10-06) - THE AUTOSAVE'S MONTH AND THE CENTRAL BANK'S
     * STRICTNESS. Batch G: two small changes, and the default playtest's
     * traces are unchanged (it plays at Standard, and its autosaves move
     * nothing). SAVE_FORMAT 30: the one new key, the strictness, reads
     * Standard on an older save - the rule it was played under.
     *   - THE AUTOSAVE HOLDS A WHOLE MONTH: the twelfth month's autosave is
     *     written at the bottom of Game.nextMonth(), once the month is
     *     recorded. It was written near the top, after the calendar turned:
     *     the file said month N on a city that had finished N - 1, and a city
     *     loaded from it never ran N (Jerus's autosave: history to 1850 at
     *     month 1851, then 1852). The autosaves before a skip and on quit
     *     were always written between the presses; SaveSlotCheck 7b loads
     *     all three and plays on. An old autosave loads as it did - its month
     *     is gone and its history keeps the gap - because its top-of-month
     *     steps had run and would run again.
     *   - HOW STRICT: Jerus, "beside the target inflation, how strict, very
     *     strict then it trys to have it below the target, very loose and the
     *     target is a suggestion". Five steps under the target on Policy >
     *     Money, set at once like it (DebtManager.Strictness): Standard is the
     *     rule as it was, to the bit; Strict aims under the target, Very
     *     strict DebtManager.STRICTEST_AIM under (never under 0%), answering
     *     at up to STRICTEST_WEIGHT; Loose holds the neutral rate inside a
     *     band either side of the target and answers only what is past it,
     *     Very loose LOOSEST_BAND each way at LOOSEST_WEIGHT. Trust is judged
     *     on the target as before, and the lean against what holding it takes
     *     (DebtManager.holdingRate()). The card says what the bank aims at and
     *     what the rule would set at the step under the thumb
     *     (PolicyPreview.ruleAt()). CentralBankCheck 21 holds it.
     *
     * 0.7.53 (2026-10-06) - COUNTS TO LONG: A CITY CAN PASS 2.1 BILLION
     * PEOPLE. Batch H1, the scale study's step 3 (the project's
     * spec-scale.md): Jerus plans cities of five to ten billion, and every
     * count of them was an int that wrapped silently at 2,147,483,647 - a
     * 5.09B copy of his city read back as 938,901,759, its workforce stuck
     * at the ceiling, and its save would not parse at all. Nothing moves at
     * today's sizes: the default playtest's traces are byte-identical, and
     * his autosave and both research cities load and play six months to the
     * same bytes as 0.7.52, bar the build and the time each save is stamped
     * with. SAVE_FORMAT 30: a JSON number has no width, so an old save loads
     * into the long fields as it did into the ints; a save past 2^31 is
     * unreadable to an older build, which says so.
     *   - THE MODEL: the population, the workforce, the posts by job type and
     *     in total, the posts filled and the people out of work
     *     (PopulationManager), the homes and the homes by size, the household
     *     places, the posts withheld, on site and by sector or category
     *     (BuildingManager, BuildingsStacks, Sector), the landlords' doors
     *     (sectors.RealEstate), the shops' coverage and baskets
     *     (sectors.Retail), the luxury shops' coverage and the kitchens' seats,
     *     and every int x int product of a building count and a per-building
     *     figure, are longs; each (int) Math.round on a count is Math.round.
     *   - THE SAVE: SaveHeader's and DataSave's population and workforce, the
     *     businesses' population trend, and HistorySave's ten count series
     *     (posts, workforce, out of work, population, births, deaths,
     *     arrivals, departures, homes, construction capacity) are List<Long>;
     *     the month axis stays an int.
     *   - THE SCREENS read them as longs and print them through
     *     Money.people(), Formats.count() and %,d, none cast back to an int.
     *   - ScaleCheck scales a founded city's save past 2^31 people by the
     *     study's method and asserts the counts whole, exactly K times the
     *     founded city's, adding up every way, and given back by a save.
     *
     * 0.7.54 (2026-10-06) - THE ORDER LOOPS SEARCH, AND MONEY READS AT ANY
     * SIZE. Batch H2, the scale study's steps 1, 4 and 5 (the project's
     * spec-scale.md). Three loops sized an order one building at a time, and
     * an order grows with the city: a 10 billion copy of the research city
     * city2400 took a median of 7.0 s a month, and up to 143 s. Each is a
     * search now, and in every run measured decided what the count decided:
     * the default playtest's traces are byte-identical, and the two research
     * cities - as they are, a thousand times over, and at 5 and 10 billion
     * people - played 15 months to the same figures as 0.7.53, city2400's 10
     * billion at a median of 46 ms a month. SAVE_FORMAT 30: nothing new is
     * saved.
     *   - THE TRIM (Game.consider()): the countdown's own first
     *     Game.COUNTDOWN_SLICES slices, then doubling down from the last that
     *     failed, then halving (largestSlice()), so an order trimmed by fewer
     *     is decided as it always was, and the bond desk is asked at most
     *     deskCallsMost() times an order - 58 where the count asked it 9.1
     *     million times. The refusal keeps the rate at the whole order and
     *     whether any slice carried it at prime.
     *   - THE SIZE (BusinessInvestment.orderSize()) and THE MORTGAGE
     *     (Mortgage.decide()) halve, because their tests hold on a run from
     *     one, proved where each is (THE WAIT GROWS WITH THE ORDER, THE
     *     LANDLORD'S ORDER IS A RUN FROM ONE); the mortgage keeps what
     *     trimmed it and the facts for one building.
     *   - MONEY: MoneyAudit.tolerance() - a cent, or a part in a trillion of
     *     the figures compared, whichever is more, and a line held tighter
     *     keeps its own floor up to ten billion units, where that part is
     *     still under a cent - for the harness lines a 10 billion city misses:
     *     the month's audit to the cent, a sector's cash-flow statement, the
     *     bank's equity movement, the history's money to the cent, and the
     *     treasury journal's reconciliation. No tolerance moves at today's
     *     sizes. Formats and the screens' Money print quadrillions, and
     *     Formats.cash() hands a sum past 2^53 dollars to amount() rather
     *     than print a saturated long.
     *   - OrderSearchCheck (new) holds the three searches to the countdowns
     *     they replaced; ScaleCheck copies a growing city to 5 and 10 billion
     *     with its sectors free and asserts it whole, adding up, conserved
     *     from the first month and quick; its scaler no longer leaves the
     *     builders' recognised revenue unscaled, the 2.7e9 residual of
     *     0.7.53's first free month.
     *
     * 0.7.55 (2026-10-06) - LAND PRICED BY CROWDING AND THE DOLLAR'S OWN
     * INFLATION, HISTORY A YEAR TO A POINT PAST FIVE HUNDRED YEARS, AND
     * ARREARS ON THE STATEMENTS. Batch I, Jerus's decisions of 2026-10-06
     * after the scale study (the project's spec-scale.md). The default
     * playtest's traces move, every move the land's - with 0.7.54's premium
     * put back in a scratch copy of this build they are byte-identical to
     * 0.7.54's - and are re-baselined. SAVE_FORMAT 30: the land office's price
     * state and the history each gain a field an older save reads as before.
     *   - THE LAND PREMIUM IS CROWDING, NOT SIZE (LandMarket, THE CROWDING
     *     PREMIUM): 1 + 119 x s, s = 1 / (1 + (4,000 / d)^5.3), d the city's
     *     people per square kilometre of the land it owns, never past
     *     LandMarket.CROWDING_CEILING. Fitted so the three cities held pay
     *     about what their size gave them - city600 3.67 to 3.74, city2400
     *     35.07 to 35.99, Jerus's 103.39 to 104.00 - where at ten billion the
     *     size premium priced a square foot at about 19,500 times his. A copy
     *     of a city K times over is quoted exactly what it is.
     *   - THE DOLLAR PRICE FOLLOWS THE WORLD'S PRICES (Jerus: "usd inflation
     *     not domestic inflation"): the base x WorldEconomy's price level x the
     *     premium, and what the treasury pays that x the rate, as since 0.7.6.
     *     What businesses pay keeps the domestic anchor x the premium x the
     *     scarcity (LandMarket.basePricePerSqFt says why); the land office
     *     names the price's parts.
     *   - HISTORY (HistorySave, PAST FIVE HUNDRED YEARS): the newest
     *     MONTHLY_KEPT months stay a month to an entry; a calendar year all of
     *     whose months are older folds into one entry at its last month, each
     *     series by the year book's rule for it - a flow added, a level's last,
     *     a rate averaged - and the history keeps how many months each holds.
     *     aligned() reads a folded flow a month at a time; the year book, City
     *     History's charts and the fund's return weigh each entry by its
     *     months. The default playtest (4,005 months) never folds.
     *   - ARREARS ON THE STATEMENTS: what the treasury pays a sector of what
     *     it owed it (Game.payDownArrears()) is a line of that sector's cash
     *     flow, "Arrears paid by the city" (SectorBooks, arrearsPaid), where
     *     until now it reached the till and no line.
     *   - Harnesses: LandCheck 18 (crowding and the world's prices) and 5c
     *     (a more crowded city pays more, where it was a bigger one); ScaleCheck
     *     6 holds its 5 and 10 billion copies to the land and rents of the
     *     city they copy and to building, and reads their statements every
     *     month; HistoryCheck 6 grows a history past MONTHLY_KEPT and holds the
     *     fold, the totals, the save and the readers; CentralBankCheck 6 the
     *     arrears line. Five fixtures re-made to cause what they test under the
     *     new land prices: BondCheck 5d's till, DeathRecordCheck's baseline,
     *     HealthCheck's paid city's ground, PolicyPreviewCheck's homes and
     *     SaveFileCheck's fund pay-in.
     *
     * 0.7.56 (2026-10-06) - THE WORLD. Batch J1a, the first of the land, water,
     * map and fuel batches (the project's spec-land.md). A city stands on a
     * world now - a flat square the size of the Earth, made from one seed - and
     * nothing in the model reads it yet: the default playtest's traces are
     * byte-identical to 0.7.55's. SAVE_FORMAT 30: the seed is one new key an
     * older save reads without.
     *   - THE WORLD (World): 368 x 368 cells of 61.44 km, 511.2 million km2,
     *     plots of 30 m nested in tiles, districts and cells by powers of two.
     *     Value noise in octaves, every value SplitMix64 of the seed and the
     *     place and every transcendental StrictMath's, so it is the same on
     *     every machine: the sea 71% of it at a level each world finds from
     *     four jittered samples a cell, lakes 3.7% and forest 31% of the land,
     *     beaches; the founding site on a coast with an iron field within 2 km,
     *     its lake and river. The terrain a plot, a tile (65 us) or a region
     *     at a stride at a time; built on first use, about half a second.
     *   - THE RESOURCES (Resource, Deposit): iron, oil, stone, coal, copper,
     *     uranium in fields - a Poisson count a cell, sites with a heavy tail,
     *     whole tonnes that sum exactly to the cell's total - and forest as
     *     terrain; the world's totals in one 30 ms pass.
     *   - THE SEED IS FOUNDED (Founding.worldSeed, DataSave.worldSeed,
     *     Game.getWorld()): DEFAULT_WORLD_SEED 4127 for the defaults, the
     *     presets, the harnesses and the playtest; the founding screen's World
     *     field, rolled when it opens and by its dice; an older save reads a
     *     seed made from its name, founding treasury, ground and month.
     *   - THE DESIGN'S LAKE AND FOREST LEVELS MOVED (0.695 to 0.7092, 0.595 to
     *     0.5676): they were percentiles at the prototype's samples, lattice
     *     points of every fine octave, and made 5.0% of the land lakes and 24%
     *     forest; the sea pass jitters its samples for the same reason.
     *   - Harnesses: WorldCheck (new) holds three worlds to the design - the
     *     same world twice, the shares, the site's four tests and the river,
     *     the fields' exact sums, the totals and their time, the tile and the
     *     region against the point function; NewGameCheck 13 the seed through
     *     founding, a save, an older save and Start New Game; SaveFileCheck the
     *     key and the derived seed.
     *
     * 0.7.57 (2026-10-06) - THE LAND ON THE WORLD. Batch J1b of the land,
     * water, map and fuel batches (the project's spec-land.md). The city's land
     * is a piece of the world now, and the land office sells the world's
     * ground. SAVE_FORMAT 31 (see above): an older save is converted once.
     *   - THE CITY'S LAND (CityLand): a centre round the founding site holding
     *     the dry ground the city was founded or converted with - a new city's
     *     3,000,000 sq ft, 0.29 km2 in all on the default world - sized ring by
     *     ring so it holds that ground exactly; four sides of ten lanes
     *     fanning out from the site, each pushed out band by band by its
     *     purchases; what each piece holds measured once - its area dry, fresh,
     *     sea and forest, every field of the seven resources centred in it,
     *     whole. What the city owns in square feet is its dry ground; its
     *     water is owned too.
     *   - THE OFFERS (LandMarket, LandParcel): forty, one a lane, each the next
     *     band of its lane, never rerolled; an offer is a multiple of a block,
     *     or of 1% of the city once that is more, the multiple drawn as the
     *     parcels' were; priced at batch I's ground on its dry ground, 45% of
     *     it on fresh water and 8% on sea, and the ore at its share of the
     *     world's price (iron at 1/350, today's $0.40 a tonne); the best value
     *     the most dry ground a dollar, never mostly sea; the richest in a
     *     resource, the cheapest, the cheapest with sea; Game.bestOffer() for
     *     each need the Build tab will ask (batch J4). The nine parcels and
     *     their generator are gone.
     *   - THE ORE AND THE WORLD'S LEDGER (LandManager): the iron sites and
     *     tonnes the city owns are the world's fields in its ground; what it
     *     takes out is one figure a resource, worked out in the order the
     *     ground was bought; what no city owns, what remains and what was taken
     *     out add up to the world's totals, stored with the city. Forest grows
     *     back a 240th a month.
     *   - THE CONVERSION (LandConversion): an older save's square feet become a
     *     centre on its own world at a site whose square is at least 80% dry
     *     with the sea near, its iron its own (at least its mines), forty
     *     offers at today's prices; a save whose square feet disagree with its
     *     land - a copy K times over - has its land drawn again to hold them,
     *     as does ground set by hand.
     *   - THE LAND OFFICE, FOR NOW: the best nine of the forty, each tagged
     *     with its side and lane, in km2 (the office round the map is J4).
     *   - Harnesses: LandCheck 5 to 5f (ten offers a side, batch I's price at
     *     the founding, the size rule, the world's fields, the offers across a
     *     save), 13 (an older save converts), 17 (offers by hand), and new 19
     *     to 21: the world's totals kept to the tonne through a purchase and
     *     extraction, the ground worked out in the order it was bought, ground
     *     by hand, the forest's regrowth, the best offer for each need, a
     *     city's land field for field across a save, and the three research
     *     cities' land converted; MiningCheck 2 (a deposit from a bought offer
     *     with iron; one site, one mine); BuildAdviceCheck (the offers'
     *     records); ScaleCheck (the copy's land drawn to hold K times the
     *     ground); ReadPathCheck, SaveFileCheck and NewGameCheck the new
     *     fields. The playtest moves: see the project's write-up.
     *
     * 0.7.58 (2026-10-06) - EARLY IRON, LAND BOUGHT FOR WHAT IS SHORT, AND A
     * FLEET IS NOT NEGATIVE OUTPUT. Batch J1c, between J1b and water.
     *   - A FIELD IS SHARED SITE BY SITE (Deposit, CityLand): a field's sites
     *     lie on a grid one site wide round its centre, nearest first, each
     *     with an equal share of its tonnes to the tonne; a site belongs to
     *     the centre or the band holding the site's centre. So the ground a
     *     field lies under shares it in proportion to the area each covers,
     *     and its shares sum to it exactly. Until now a field went whole to
     *     the band holding its centre: the default world's founding field, 35
     *     sites and 449 Mt, was one US$180M offer, and the playtest's first
     *     iron came in month 1,088. A new default city's first ring now offers
     *     single sites from about US$5.3M against its D$100M treasury.
     *   - A SHORTFALL IS MET WITH GROUND (Game.bestOffer(), LandMarket.
     *     bareGround()): the cheapest offer covering a shortfall of dry ground
     *     that holds no ore - with none, the best value. The playtest's build()
     *     buys this (what the Build shortcut will, J4) in place of the cheapest
     *     offer standing.
     *   - EVERY OTHER GOOD A SECTOR HOLDS IS STOCK (NationalAccounts.HELD): the
     *     farms' crops, every sector's vans and the railway's rolling stock,
     *     their change priced at what one costs to bring in. A railway buying
     *     its fleet abroad read GDP of -16,056 in an ensemble month; it reads
     *     its output now. Saved in the accounts' slots 15 on; an older save's
     *     first month books no change in them.
     *   - Harnesses: LandCheck 5e and 19 (a field's shares sum to it, an
     *     offer priced by its share's tonnes, a new city's iron affordable),
     *     GdpCheck (a fleet bought abroad, the production side, every good
     *     held in a term, a railway town's fleet month), RailCheck (the track
     *     its fixture lays takes its ground). The playtest moves.
     *   - THE TEST PLAYER KEEPS ITS GROUND AHEAD (batch J1d; LongPlaytest.
     *     keepGroundAhead()): at each look it buys the best value until no
     *     more of its dry ground is built on than the ground in use grown
     *     BuildAdvice.HORIZON months by the businesses' growthFactor(), with
     *     BuildAdvice.SLACK past it - the build advice's own sizing - spending
     *     at most a tenth of the cash. On the world's land the playtest's
     *     city had been half 0.7.55's size from month 1,000 to 3,000: one
     *     offer a move, each a multiple of 1% of the city and the cheapest
     *     the oldest, added 8.7% to its ground a look in months 300-1,000
     *     where 0.7.55's parcels added 17.1%. Nothing in the game changes;
     *     the playtest and the ensemble move. Harnesses: LandCheck 22 (the
     *     line, the cash share, a look stopped short, a city under the line),
     *     CentralBankCheck 21's probe city in seed 2's shape, the calm one
     *     under the new player (seed 5's twins now end 795 people apart).
     *
     * 0.7.59 (2026-10-06) - FRESH WATER HAS A LIMIT, AND THE SEA CAN BE
     * DRUNK. Batch J2 of the land (the project's spec-land.md 2.3).
     *   - THE FRESH WATER LIMIT (UtilitiesHandler; Game, THE FRESH WATER
     *     LIMIT AND THE COAST): the Water Treatment Plants together treat no
     *     more than the city's lakes and river yield, FRESH_UNITS_PER_KM2
     *     (121,600 units a month a km2 - the world's river runoff over its
     *     river area) times the owned fresh km2, plus the city's water
     *     rights; past it the rest of their nameplate idles and the water is
     *     rationed through waterRatio as any shortage is. The wells' 8,000
     *     are groundwater, outside it. Not binding, production is the old
     *     formula to the bit. The Services page says "fresh water limit: 62%
     *     of the plants' nameplate idle · buy lake or river, or desalinate"
     *     (CityNeeds.freshLimitLine()), and its "at full staff" is held to
     *     the limit.
     *   - THE DESALINATION PLANT (id 73, WATER, "source": "SEA" -
     *     BuildingsTemplate.Source): the water plant's output, crew and ground at
     *     twice its cost, drawing 10,880 kW (3.5 kWh a m3); the limit does
     *     not reach it, and an order with no owned sea is refused NO_COAST
     *     (Game.hasCoastFor(), BuildResult.NO_COAST, BuildCard's NO_COAST
     *     verdict, Build's no-coast page). BuildAdvice values a fresh plant
     *     at the water left under the limit and desalination at its output
     *     where the city owns sea, nothing where it does not.
     *   - THE RIGHTS (DataSave.freshRights, boxed, no format bump): a save
     *     from before them is given its fresh plants' nameplate standing
     *     less what its lakes yield, so loading idles nothing it had - the
     *     research saves: Jerus's city 300,000 units (no lakes in its
     *     centre), city2400 960,000, city600 none. A new city has none.
     *   - THE TEST PLAYER (LongPlaytest.addWater()): a water plant while
     *     the fresh water left under the limit covers the shortage or a
     *     plant, and no more plants than that water feeds; past it,
     *     desalination on owned sea, else the offer with the most lake or
     *     river a dollar (LandMarket.bestFresh()), else the cheapest with
     *     sea. The playtest moves (the project's write-up).
     *   - Harnesses: WaterCheck 9-12 (the limit, rationing, the rule to the
     *     bit unbound, desalination past the limit and its 10,880 kW, the
     *     limit on a city's land and rights, NO_COAST and the coast bought,
     *     a lake's share lifting the limit exactly, the rights saved,
     *     derived and converted), BuildingDataCheck (the source, 74
     *     buildings), SaveFileCheck and ReadPathCheck (the rights and the
     *     new reads).
     *
     * 0.7.60 (2026-10-07) - THE CITY MAP'S DATA AND PAINTER. Batch J3 of the
     * land (the project's spec-land.md 2.5 and 2.6). Model only: nothing in
     * the model reads the map, and the playtest is byte for byte 0.7.59's.
     *   - THE DISTRICTS (CityMap): the city's buildings counted by type in
     *     each 7.68 km district of its land, with its owned dry plots (32 x 32
     *     samples, recounted in every district a purchase touches), the
     *     ground its buildings use and its owned iron and oil sites. Each
     *     month, after the month's construction and demolitions, each type's
     *     change goes into the first district with room - nearest the
     *     founding site first; farms, utilities, mines, the railway and the
     *     car plants farthest first - removals in reverse, and nothing placed
     *     moves; mines stand on the first owned iron sites in the order the
     *     ground was bought, those not worked out first. Drawn canonically -
     *     each type in proportion, inner first, the remainders by largest
     *     remainder - the first time it is asked for, for a save without its
     *     sidecar, and for land drawn again. Dealt into each district's 64
     *     tiles by each building's own hash, so one more building changes
     *     one tile by one; summed two by two up a pyramid for the far view.
     *   - THE PAINTER (TilePainter, TileRaster, BuildingVisual): a tile
     *     painted from the seed, its ground, what is owned, its counts, its
     *     road budget, its neighbours' roads and the sites under it, by the
     *     mockup's rules made local to the tile - ports on shared edges,
     *     roads grown from them, bridges over fresh water (paved 6 plots, a
     *     highway 14, gravel none), buildings at their own hashed places
     *     u^1.5 from the road, the mockup's footprints and colours, flats the
     *     apartment types, home daycare and home care drawn as homes, mines
     *     grey on their sites with a gravel spur, worked-out sites grey - and
     *     rastered for PixelWriter at any whole number of pixels a plot.
     *   - THE SIDECAR (GameFiles.mapFile(), DataSave.mapStamp, no format
     *     bump): slot-NN-map.bin beside the save, deflated, its stamp in the
     *     save; a load reads it back when the stamp matches and draws the map
     *     again when it does not; a city never asked for its map has none and
     *     pays nothing for it. The view, the land office around it and the
     *     Build shortcut are batch J4.
     *   - Harnesses: MapCheck (new): a played city's districts sum to its
     *     counts every month; nothing placed moves; the painter's rules at
     *     Jerus's density; the sidecar byte for byte, a stale one drawn again;
     *     a 198-tile screen in no more than 80 ms at his city x 1, x 9,814
     *     (5B), x 10,000 and x 19,629 (10B), each within 1.5 times x 1's, and
     *     a month's change at 5B and 10B in no more than 5 ms. The playtest
     *     keeps its city's map and audits it every month and every reload;
     *     SaveFileCheck and ReadPathCheck the stamp and the new reads.
     *   - THE MAP LOOKS LIKE A CITY (batch J3b, still model only). J3 drew a
     *     dot for each model building, and a block of 252 people is one
     *     building, so Jerus's city of half a million came out as scattered
     *     dots on a field. A tile now draws its homes from the people its
     *     homes house - houses of 5 in a thin town, flats of up to about 75
     *     where it is dense, never more than 26% of its ground - its shops,
     *     offices and plants from their jobs (8, 38.75 and 150 a building),
     *     and its schools, clinics, utilities, farms and mines one for one
     *     (BuildingVisual's visual counts, CityMap's drawCounts; both gone
     *     since 0.7.64). A district
     *     now spreads over the tiles within half a district of its middle,
     *     so neighbouring districts blend, homes, shops and services lean
     *     toward the founding site and plants toward the two highways and
     *     away from it (CityMap's deal). Roads are the frontage the buildings need, 0.31
     *     of their plots (the mockup's) in steps of 12, paved where people
     *     and jobs are dense and gravel at the fringe, the model's own paved
     *     share the least, a larger budget only growing the same roads
     *     further; buildings prefer the two rows beside a road, placed kind
     *     by kind, homes first and the one-for-one types last, and one with
     *     no road beside it is laid out square (J3 laid it as a diamond).
     *     Drawn at the mockup's widths and sizes from 4 px a plot, and as
     *     its blocks for the middle and far views (TileRaster.blocks()).
     *     Jerus's city is built 34%, its middle half, its edges a quarter;
     *     17,634 homes where J3 drew 9,920 dots, one a model home or daycare.
     *     MapCheck 6 (new): the counts drawn, mature land built 20 to 35%,
     *     the middle denser, never a carpet, homes the most of it; MapCheck
     *     2 and 3 now grow the city by a model building.
     *
     * 0.7.61 (2026-10-07) - THE MAP ON SCREEN, THE LAND OFFICE ROUND IT, AND
     * BUILD'S SHORTCUT. Batch J4 of the land (the project's spec-land.md 2.6
     * and 2.8). The playtest is byte for byte 0.7.59's.
     *   - THE MAP VIEW (ui/MapView; its arithmetic MapFrame, LandMap and
     *     MapTiles): one Canvas, small in the land office (600 x 400) and,
     *     with Expand, over the whole window, City History's pane - drag pans,
     *     the wheel zooms by 1.25 a notch at the pointer from the whole world
     *     to 16 px a plot, + and - zoom, 0 fits, Esc closes; a legend and a
     *     hover card (the ground, whose it is, an offer's size and price, the
     *     fields and their tonnes, the building or road). The mockup's levels:
     *     painted tiles at 4 px a plot from 3.2 px (8 past 6), its blocks of
     *     four from 1.4, of eight below while the view holds no more than
     *     1,024 tiles, and past that far nodes from the world's ground and the
     *     districts' counts. Tiles paint a few each frame, at most 8 ms of it,
     *     the middle of the view first, another level's picture standing in;
     *     a month or a purchase stamps the tiles in view again and repaints
     *     only those whose stamp moved. Every cache within 48 MB at the
     *     design's size. The city's edge, its forty offers hatched, the
     *     deposits in the far views and a scale bar over it all.
     *   - OFF THE SCREEN'S THREAD, ARITHMETIC ONLY: a city's map is drawn the
     *     first time on a copy of its land and counts (Game.mapDraft()) on the
     *     view's worker and kept back on the screen's thread, caught up to the
     *     month (Game.adoptMap(), CityMap.rebind()); far nodes' ground is read
     *     on the worker. CityMap gains tileInput() with the ground given and
     *     changes(), what a view stamps against.
     *   - THE LAND OFFICE (ui/LandScreen): the map, and beside it THE CITY -
     *     its size in km2, "107.8 km² · 89.6 dry · 0 fresh · 18.2 sea", its
     *     share of the world's iron - the four sides as chips (each its
     *     cheapest dry ground a km2, BEST VALUE on the best's side) and the
     *     chosen side's ten offers as rows: lane, km2, dry, fresh and sea as a
     *     bar, deposits with sites and tonnes, price, price a dry km2, a tag,
     *     Buy or Fund. A click on the map picks a side or an offer, a row
     *     lights its band. "Buy the next N" is "Buy the best N"; the Ore card
     *     is Ore and oil. The 3 x 3 shelf of 0.7.57 goes.
     *   - BUILD'S SHORTCUT (ui/BuildScreen, Game.bestOffer()): LAND FREE in
     *     km2 with "Buy the best land · 3.4 km² · US$12.1M ›"; the no-land page
     *     "Buy the best: 3.4 km² for US$12.1M", bare ground that covers the
     *     shortfall (batch J1c's rule, said on the button); the no-deposit and
     *     no-coast pages their own; short, the land office's funding page sized
     *     to the offer. The build card's land in km2. Icons MAP and EXPAND.
     *   - Harnesses: MapCheck 7 (new): the transforms, a notch at the pointer,
     *     the clamps, the levels and the tiles a screen asks for; what a click
     *     picks on a played city against owns() and sideLane() and the bands
     *     outlined; the map drawn on another thread and kept; a tile restamped
     *     only when its counts move; the view's per-tile path within section
     *     5's bounds and no dearer than it; the caches within 48 MB.
     *
     * 0.7.62 (2026-10-07) - FUEL: OIL WELLS ON OWNED OIL, A REFINERY, AND
     * CRUDE IMPORTED WHEN THE CITY HAS NONE. Batch K of the land (the
     * project's spec-land.md 2.7). The playtest moves; re-baselined.
     *   - TWO GOODS (Good): CRUDE, tonnes, .60 in and .50 out, a flow good -
     *     a well ships what it lifts, as a mine does; FUEL, litres, a
     *     journey's fuel over its 1.2 litres in (Motoring
     *     .CAR_FUEL_PER_JOURNEY / LITRES_PER_JOURNEY), .0007 out, held in the
     *     refiners' tanks. Freight by the file's rule, three quarters of the
     *     half-wedge. Resource.OIL is CRUDE's, so the land office prices oil
     *     at 5% of its world price from now on; offers listed before keep
     *     their prices.
     *   - TWO SECTORS on the end of Sectors.KEYS: Oil (sectors.Oil, Mining's
     *     shape: the Oil Well, id 74, 415 t a month on an owned oil site, the
     *     ground its limit through groundLimit(), one well a month while a
     *     site is unworked, retired when the oil runs out) and Refining
     *     (sectors.Refining, HeavyIndustry's shape: the Oil Refinery, id 75,
     *     8,300 t of crude into 8,300,000 L, the wells' crude first and the
     *     world's for the rest; planned while the investors' estimate clears,
     *     with its crude at what it would cost to import past the wells'
     *     spare, and only for a whole plant's worth of the city's own fuel or
     *     its own crude - the playtest stood 120 export refineries without it).
     *   - THE DRIVERS' FUEL IS DRAWN (Motoring.drawFuel(), at 6d): their
     *     litres off the refiners' tanks at the market's price and the rest
     *     imported, so with no refinery the bill is 0.7.49's at the world's
     *     price level, as every good's and the railway's already were. A
     *     journey's fuel, which the owners weigh a ride against, is its litres
     *     at the landed price. THE RAILWAY DRAWS FUEL as a buyer (Rail.haul(),
     *     eighteen litres a tonne hauled); Sector's bookImportedService goes.
     *   - THE BOOKS: the money audit's FuelFunded and FuelImports carry only
     *     the imported part (Game.getHouseholdFuelImports()); the refiners'
     *     part is Refining's SalesToHouseholds, and in consumption with the
     *     shops'; the trade by good lists FUEL with the households among its
     *     buyers where HOUSEHOLDS_FUEL was; the refiners' tanks are the fifth
     *     inventory term's (NationalAccounts.HELD, on the end).
     *   - THE DEPOSIT BY ITS GOOD (Game.siteOf(), committedOn()):
     *     minesCommitted() counts Iron Mines only, wellsCommitted() the wells,
     *     hasDepositFor() and a run's checks the resource each building stands
     *     on. Build's no-deposit page, its press and verdict words, the card's
     *     deposit line and the investors' gate name iron or oil.
     *   - THE SAVE: DataSave.householdFuel (the bill, its imported part and
     *     the litres, as 6d drew them; a save without it imports every
     *     litre, as it struck); the railway's imported fuel in its extras.
     *     No SAVE_FORMAT bump.
     *   - THE TEST PLAYER buys the richest offer in oil, sites a dollar, when
     *     the fuel bought abroad passes 1% of a month's GDP and every oil site
     *     has a well.
     *   - Harnesses: OilCheck (new). Premises moved: BuildingDataCheck (76
     *     buildings), BuildCardCheck (Industry nine groups), ForeignCheck and
     *     RailCheck (the fuel a good's), TradeCostCheck (two goods priced),
     *     ScaleCheck's scaler (the saved fuel). Fixtures re-made to their
     *     premises: ScaleCheck's city copied at month 410, where its own rents
     *     price nobody out (asserted); GroceryCheck's previews read at a month
     *     half a dial reaches somebody under the voucher's cap.
     *
     * 0.7.63 (2026-10-07) - SAVES THAT RELOAD EXACTLY, AND THE AUDIT'S FLOOR
     * AT SCALE. Batch L, on what batch K found. The playtest is byte for
     * byte 0.7.62's.
     *   - A RELOADED CITY PLAYS ON AS THE ONE IT WAS SAVED FROM, to the bit:
     *     city600, city2400 and Jerus's city, each reloaded at twelve points
     *     two months apart and compared a month on, and once each played 36
     *     months past a reload - every pool the money audit reads, and the
     *     people. Two figures only the month sets were not saved, both
     *     since before 0.7.54 (0.7.54, 0.7.57 and 0.7.62 measured alike):
     *     the ratios every sector's month was run at (SectorState's
     *     energyRatio, waterRatio, roadRatio and healthRatio) - the month is
     *     struck at the top of the next on them, and the load set them from
     *     the services, which have struck next month's by then, so city2400,
     *     short of power, billed its saved month 0.02% dear and came back
     *     0.23 units adrift a month on; and the students who finished a
     *     course and wait for the next census to carry their loans
     *     (HouseholdBalance.graduatesToSave(), DataSave.householdGraduates) -
     *     in Jerus's city their loans stayed with the students the first
     *     month back, the families repaid 0.93% less and his treasury came
     *     back 7,447 units short.
     *   - A SAVE FROM BEFORE THE MONTH'S UNITS (0.7.46) has them derived on
     *     load: the money abroad over the boundary price it crossed at
     *     (Sector.deriveCarriedTrade()). city2400's railway hauled 2,515 t
     *     the month after its load, 152,904 now (153,161 the month it was
     *     saved in); Jerus's city without its units hauls as with them, to
     *     the bit. Every save writes the units now, empty or not, so the two
     *     are told apart.
     *   - THE AUDIT'S FLOOR: MoneyAudit.tolerance(floor, size) is never under
     *     MoneyAudit.ULP_FLOOR (8) of a double's steps at the size - today's
     *     floor to the bit below 2^30 units, which every harness but
     *     ScaleCheck's copies reads under.
     *   - Harnesses: SaveFileCheck (a town short of power whose students
     *     finish, its twin a month on to the bit; the railway town's save
     *     without its units, within MoneyAudit.tolerance()); ScaleCheck (the
     *     floor at size; the city copied at month 430, where its copies order
     *     on their own months - the sized order at 410 was the load's; the
     *     scaler scales the saved graduates); ReadPathCheck (their reads).
     *
     * 0.7.64 (2026-10-07) - WHOLE IRON FIELDS AS ONE OFFER, THE AUDIT'S FLOOR
     * AT 64 STEPS, AND A MAP OF WHAT THE CITY HAS. Batches L and L2, on
     * 0.7.63's saves - this batch L (whole fields, the floor at 64) made on
     * 0.7.62's tree; 0.7.63 is an earlier batch L's save fixes, which L2
     * put back. The playtest moves (batch L, from month 32); re-baselined.
     *   - A FIELD GOES WHOLE WITH ITS CENTRE (CityLand; spec-land star 12).
     *     Jerus, 2026-10-07: "Yes whole iron fields as one offer, yes that
     *     means significant investment." Batch J1c's sharing (0.7.58) is
     *     undone: every site and every tonne of a field belongs to the centre
     *     or the one band of one lane holding the field's centre, wherever
     *     its sites lie - oil's and every other resource's alike. The default
     *     world's founding field, 35 sites and 449 Mt, is one offer of about
     *     US$180M again, where a new city bought one shared site for about
     *     US$5.3M, and a new default city's first ring holds no iron. Priced
     *     as before: the ground, and the fields' tonnes at the in-ground
     *     price. The map gives a field's sites to the holding of its centre
     *     (CityMap), and its hover names whose a field is, whole
     *     (LandMap.fieldOwnerWords()).
     *   - THE WORDS SAY IT: a land office row's tooltip and the Ore card's (i)
     *     (an offer holds every field centred in it whole, its row its sites
     *     and tonnes), Build's no-deposit page and its Buy (the sites and the
     *     tonnes), and the press a card and the build advice's suggestion
     *     show for no deposit ("whole fields of ore come with land"). The
     *     land office's funding page already sizes its bond to the offer.
     *   - THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME
     *     (LongPlaytest.ironWhenNeeded(), a rule beside the ground kept
     *     ahead): when an Iron Mine would pay and every iron site the city
     *     owns has a mine, the cheapest offer holding iron
     *     (LandMarket.cheapestWith()) - out of the cash when it covers it,
     *     else on the funding page's 20-year bond when the player's test for
     *     borrowing passes; neither, it asks again at the next look. The
     *     "bought a deposit" move goes. The playtest's first iron: month 485,
     *     a one-site field 33 plots out (East 7, 12.8 Mt, US$6.2M, out of the
     *     cash), where 0.7.62's player bought a shared site in month 32.
     *   - THE AUDIT'S FLOOR RISES FROM 8 STEPS TO 64 (MoneyAudit.ULP_STEPS,
     *     0.7.63's ULP_FLOOR): a floor of its own is never under 64 of a
     *     double's steps at the size of the figures compared. 0.7.63's 8 was
     *     the next power of two past the most a cash-flow statement missed by
     *     in ScaleCheck's copies at months 410 and 430 (6 steps); at batch
     *     L's months 410 to 570 the bank's equity movement missed by 57.7
     *     steps and a statement by 10.75 on 9.9e9 units - rounding alone,
     *     each identity a signed sum of figures that carry their own - so
     *     the floor is the next power of two past the worst. The floors
     *     under a cent the suite reads, at most 2.5e6 units, are their
     *     floors exactly (64 steps pass 1e-6 only from 2^27 units), and a
     *     cent never moves.
     *   - Harnesses: LandCheck 5e and 19 back to whole fields, with the
     *     funding page sized to the founding field and the hover's words;
     *     LandCheck 23 (new section): the player's iron rule; MapCheck's
     *     played city handed a whole field and a mine on it, the map's sites
     *     the land's; ScaleCheck 8 (new section): the floor; ScaleCheck's
     *     city copied at month 550, where it is calm, growing and its copies
     *     order (its premise: 400 to 530 squeeze or order nothing).
     *   - 0.7.63'S SAVE FIXES AS THE PC HAS THEM (batch L2). This batch L was
     *     made on a tree without them; they are back as deployed: the
     *     ratios every sector's month was run at (SectorState), the
     *     graduates waiting for the census (HouseholdBalance,
     *     DataSave.householdGraduates), the units of a save from before
     *     0.7.46 derived on load (Sector.deriveCarriedTrade()), and their
     *     harness cases. Jerus's autosave (month 416) and slot 3 (month
     *     212), both 0.7.63's, load with every key of the file read, play
     *     12 months, and save and reload to the bit: every pool the money
     *     audit reads, the people.
     *   - BUILD'S NO-DEPOSIT SHORTCUT BUYS THE CHEAPEST FIELD (Game.bestOffer()
     *     for a deposit, LandMarket.cheapestWith(); batch L2): the cheapest
     *     offer holding the ore, as the test player buys its iron, where it
     *     was the most of its sites a dollar (LandMarket.richest()). Its Buy
     *     names the offer, its sites, its tonnes and its price; short of cash,
     *     the funding page as before; the land office still lists every offer.
     *   - THE MAP DRAWS WHAT THE CITY HAS, NO MORE, NO LESS (CityMap,
     *     TilePainter, BuildingVisual; batch L2). Jerus, 2026-10-07: "a brand
     *     new city shows that it has a few houses and a shop when it doesnt".
     *     A new default city holds one building, its Commercial Bank
     *     (Game.foundingBank()), and no road; J3b drew homes from people and
     *     workplaces from jobs - the bank as a shop - beside two highways of
     *     the world's. Now each building the model has is drawn once, as its
     *     own type, on whole plots sized to its type's land
     *     (BuildingVisual.footprint()), a mine on its own site; the road plots
     *     of the model's roads are laid by kind (a Gravel Road 46, a Paved
     *     Road 26, an Elevated Highway 7) and no others. The deal keeps each
     *     tile to its free plots, counted plot by plot (the sidecar's format
     *     2; an older sidecar is not read and the map is drawn again once). A
     *     building no free box of its tile holds is drawn smaller: Jerus's
     *     city at month 416, 7 of 2,340.
     *   - Harnesses (batch L2): MapCheck 1 (every 30th month, every tile
     *     painted, the map draws exactly what the city has), 3 (each tile's
     *     road plots of each kind exactly; a building added moves none placed
     *     before it), 5 (the copies held to the 5B copy's cost, the first
     *     whose screen is all city) and 6 (a new city as founded, a month and
     *     a year on; the dense screen drawn one for one) - J3b's premises of a
     *     city drawn from people and jobs go with it; LandCheck (the
     *     shortcut's deposit offer the cheapest holding it); SaveFileCheck,
     *     ReadPathCheck and ScaleCheck's scaler as 0.7.63 deployed them.
     *   - EVERY AREA OF LAND THE PLAYER READS IS IN KM2 (the docs pass;
     *     spec-land 2.2, "area in km2 everywhere"): NEEDS YOU's GROUND row,
     *     the city panel's LAND head ("0.000137 km² · D$1,940/sq ft", its
     *     colour the free ground's), the land office's GROUND FREE, its big
     *     figures and bar, Build's short-of-land press and verdict, the
     *     investors' gate on a card, the advice card's land, an order's
     *     needs, the no-land page, the demolition card's ground and the
     *     landlock notice - LandManager.km2Words(), three figures, where they
     *     said square feet. Prices stay a square foot, and so do a card's land
     *     a unit and the investors' own word ("no land - needs N sq ft",
     *     BusinessInvestment's, which the playtest's traces carry).
     *
     * 0.7.65 (2026-10-07) - THE LAND AS A BLOCK GRID, BUILT PURE. Batch M1 of
     * the project's spec-grid.md, Jerus's design of 2026-10-07: the grid and
     * the offers six a side, on their own; nothing the game plays reads them
     * yet (CityLand moves onto them in batch M3), and the playtest is byte
     * for byte 0.7.64's.
     *   - THE OWNED GROUND AS A QUADTREE (LandGrid): square blocks of 2^k
     *     plots a side lined up with the world, from 120 m (MIN_LEVEL) to
     *     983 km (MAX_LEVEL), nesting in its tiles, districts and cells; a
     *     node is unowned, owned whole by one holding, mixed, or owned whole
     *     by several. A fill claims only the plots no holding owns yet, so
     *     the tree is rebuilt by replaying the rectangles in order and is
     *     never saved. A city's level is the largest block of which
     *     FACE_BLOCKS (six) fit across a square of its area.
     *   - THE OFFERS, SIX A SIDE (GridOffers): each side's six places, left to
     *     right facing out, each one rectangle of whole blocks against the
     *     city's edge - one across and DEPTH_OVER_WIDTH deep, two by four
     *     where there is room - seeded on the innermost free ground of its
     *     lane, a notch first, clipped against the offers standing, trimmed
     *     of ground the city owns and held to 2:1. Buying one lists only its
     *     place's next; the other 23 never move.
     *   - Ported from the prototype the design was measured on, and matched
     *     to it offer for offer after every one of 4,533 purchases: three
     *     worlds, bought evenly, on one side and for best value, to ten
     *     billion people. Two guards it did not have: no offer reaches past
     *     the world's edge, and a listing whose clip does not settle leaves
     *     its place waiting rather than standing over another (none does).
     *   - Harnesses: GridCheck (new).
     *
     * 0.7.66 (2026-10-07) - SAVED CITIES PUT ON THE GRID, BUILT PURE. Batch M2
     * of the project's spec-grid.md: the conversion every older city will go
     * through once, at load, from batch M3 - on its own; nothing the game
     * plays calls it yet, and the playtest is byte for byte 0.7.65's.
     *   - THE LANES, READ-ONLY (LegacyLand): spec-land's geometry - sides,
     *     lanes, radius, a band's area, which plots a centre and forty
     *     frontiers own - moved out of CityLand whole, CityLand delegating to
     *     it, so nothing moved by a bit; and a format-31 save's centre, lanes
     *     and purchases read with its own reader, totals added up in the
     *     save's order, so batch M3 can change CityLand and LandParcel and
     *     still read every save.
     *   - A FORMAT-31 SAVE SNAPPED (GridConversion.fromLanes()): one level
     *     finer than the city's offers (240 m for Jerus's city), each block
     *     the city's by the half it owned unless a field decided it - a
     *     field's centre, or each site of a field it held in part (a save of
     *     0.7.58 to 0.7.63) - and split down to the plot where fields of both
     *     kinds met. No field changes hands; a field held in part keeps
     *     exactly its own sites. Its sites and tonnes are the save's to the
     *     bit, E as saved; its five areas are the plots drawn (the books
     *     follow the map: Jerus's live city +0.130 km2 dry, +1.81%); no money
     *     moves.
     *   - AN OLDER SAVE'S CENTRE (fromFigure()): rings of blocks round J1b's
     *     site, the last split down to the plot, holding its dry ground to
     *     within a plot; its iron as saved, at least its mines, with a legacy
     *     field on its own dry ground where the world laid no iron on it.
     *   - LandConversion.legacyPlot(): J1b's legacy field draw, the grid's
     *     centre accepting only its own plots; J1b's draws unchanged.
     *   - Harnesses: ConversionCheck (new), on copies of the five saves' land
     *     (the resource conversion-saves.json): Jerus's live city and slot 3,
     *     his older city, city600 and city2400.
     *
     * 0.7.67 (2026-10-07) - THE LAND ON THE BLOCK GRID. Batch M3 of the
     * project's spec-grid.md, Jerus's design of 2026-10-07 ("the generation
     * should only put what the city has, not more not less"): the game's land
     * moves off spec-land's forty lanes onto the grid. SAVE_FORMAT 32.
     *   - THE CITY'S LAND (CityLand): the centre's blocks and each purchase's
     *     rectangle on a LandGrid, replayed from the rectangles at load and
     *     never saved. A new city is founded on whole 120 m blocks round its
     *     site until they hold STARTING_SQ_FT - 21 blocks, 315 dry plots on
     *     the default world, 3,051,569 sq ft, 1.7% more - and owns what is
     *     drawn. A field goes whole with the holding whose ground holds its
     *     centre plot; a converted city's field held in part keeps its own
     *     sites, its others going a site at a time to the ground that holds
     *     each.
     *   - THE OFFERS, SIX PLACES A SIDE (LandMarket, LandParcel): each place's
     *     rectangle as GridOffers lists it, or none while its side has no
     *     room - listed again after every purchase and every month until it
     *     has, the place just bought first. A new city lists 19 offers of 1 x
     *     2 blocks (0.0288 km2). The size rule (a drawn multiple of a block or
     *     of 1% of the city) is gone: the city's level sets the blocks.
     *   - THE BOOKS ARE THE PLOTS, EXACT AT EVERY SIZE (the orchestrator's
     *     decision, 2026-10-07): an offer's ground is its rectangle's free
     *     plots counted by class when it is listed - a whole tile the city
     *     owns none of from a kept count, a rectangle of PARALLEL_TILES tiles
     *     or more over the machine's cores, whole-number sums the same on any
     *     machine - and never sampled. Its ground cannot change while it
     *     stands.
     *   - SAVES CONVERTED AT LOAD, ONCE (LandConversion): a format-31 save's
     *     lanes snapped one level finer than its offers (GridConversion, batch
     *     M2) - Jerus's live city +0.130 km2 dry (+1.81%), its 13 iron sites
     *     and 160.6 Mt to the bit, its two shared fields keeping their 6 of 8
     *     sites, E as saved, its 133 purchases kept as history, no money
     *     moving; an older save's figure drawn as a centre of blocks to the
     *     plot; either way the city's figure its dry plots, and 24 places
     *     listed afresh at the prices the save last struck. A restatement
     *     (setOwnedSqFt()) draws as an older save is drawn, the figure the one
     *     set, the ground holding it to within a plot.
     *   - The map (CityMap, LandMap, MapTiles): ownership from the grid's
     *     cover and leaves, a site's holding its field's or its own, a
     *     purchase recounting the districts under its rectangle, the city's
     *     edge as runs along block lines; the sidecar FORMAT 3, its land stamp
     *     the holdings' rectangles. The office and the view keep their shape
     *     for batch M5, reading places where they read lanes.
     *   - Harnesses: none new. LandCheck, ConversionCheck (section 6: the
     *     game's own conversion at load), MapCheck, ScaleCheck, SaveFileCheck,
     *     ReadPathCheck, NewGameCheck, MiningCheck, OilCheck and WaterCheck on
     *     the grid; the playtest re-baselined (pt0767).
     *   - THE TEST PLAYER, WHERE ENSEMBLE SEED 14 BROKE (batch M3b; the game
     *     itself unchanged). Seed 14, 9,800 people at month 570, was 70% out
     *     of work by month 600 and never recovered (17,801 at month 2,400;
     *     126,136 at 0.7.63), and its iron was not why: a six-site field out of
     *     the cash, as 0.7.63's. Its player spent all six moves of the looks at
     *     months 477 to 490 on ground, its power and roads short, and in the 120
     *     months to its next look its plants fell to 23% of nameplate; after
     *     the crash it ordered 16 Bus Networks whose crews' wages ran to all
     *     of its revenue (runs/fixM3b-notes.md). Three rules, each a sensible player's:
     *     ROOM TO GROW PRICED FROM THE GROUND-AHEAD LINE (LongPlaytest.
     *     roomToGrow()), not .85, so ground the projection already holds
     *     outranks no throttle; NO MORE TRANSIT LINES THAN THE RIDERS FILL (the
     *     cap was in lines against a share of output, 25 times too loose); and
     *     AN IRON FIELD BOUGHT WHEN ITS MINES PAY IT BACK (fieldEarnings(),
     *     fieldPayment()): the mines the city could staff, each paying on the
     *     mining sector's screen, earn a month the level payment repaying its
     *     price over BUILD_BOND_YEARS at the market's rate - not because the
     *     borrowing test passed. LandCheck 22 and 23 and CarCheck (a new
     *     section) hold them; CentralBankCheck 21's probe city is seed 11's
     *     shape and ScaleCheck's FREE_FIXTURE_MONTHS 545, each re-made to its
     *     own premise; the playtest re-baselined (pt0767b).
     *
     * 0.7.68 (2026-10-07) - LAND IN SQUARE METRES AND SQUARE KILOMETRES.
     * Batch M4 of the project's spec-grid.md (2.5, star 10): the units the
     * player reads land in. The model keeps square feet and prices a square
     * foot; nothing it decides reads the words, and the playtest moves in
     * its words only.
     *   - EVERY AREA THE PLAYER READS (LandManager.areaWords()): in square
     *     metres under a hundredth of a square kilometre (M2_WORDS_BELOW) -
     *     a House's plot "743 m2", where 0.7.64 wrote "0.000743 km2" - and
     *     in square kilometres from it, three figures either way, grouped
     *     from a thousand ("1,760,000 km2"), the unit picked after the
     *     rounding. Through it: NEEDS YOU's GROUND row, the city panel's
     *     LAND, the land office's strip, figures, bar, rows and map hover,
     *     Build's LAND FREE, card, press, verdict, gate, advice card, order
     *     and no-land page, the demolition card and dialogs, the inbox's
     *     demolitions and landlock notice, the skip report, the farms'
     *     ground under cultivation (it read acres), and the log. A part of
     *     an area reads in its whole's unit (partFigure()).
     *   - THE INVESTORS' OWN WORDS: "no land - needs 5,570 m2, 3,900 m2
     *     free" (BusinessInvestment.landReason()) and "Could not build ... -
     *     needs ..., ... free" (Game) - the playtest's t-house trace carries
     *     the first, so it moved, in its words only.
     *   - GROUND PRICES A SQUARE METRE (LandManager.perM2(), Money.
     *     groundPrice()): THE WORLD ASKS, INVESTORS PAY and the margin, the
     *     price's parts ("US$7.53 x 1.00 US prices"), the details chart and
     *     History's Land line, the city panel and Build's advice card. An
     *     offer keeps its price a dry km2.
     *   - Gone, unused: the land office's square-feet words and their
     *     compact figures (LandScreen), LandManager.COST_GROWTH_PER_BLOCK.
     *   - Harnesses: LandCheck 24 (new: the units); BuildCardCheck's word
     *     fixtures in the shape the model now files them. The playtest
     *     re-baselined (pt0768), every figure byte for byte pt0767b's.
     *
     * 0.7.69 (2026-10-07) - THE LAND OFFICE AND THE MAP ON THE BLOCK GRID.
     * Batch M5 of the project's spec-grid.md (2.4, 2.5): what the player
     * sees of the six places a side. Nothing the model decides moves; the
     * playtest is byte for byte pt0768's.
     *   - THE LAND OFFICE'S ROWS: the place, 1 to 6 ("#", where it said
     *     "lane"); the size's tooltip gives the offer's blocks and its ground
     *     ("1 x 2 blocks of 120 m · 0.0288 km2 · 0.0273 dry, 0.0015 fresh",
     *     each part in the whole's unit), and the row's tooltip opens with
     *     it; a place waiting for room shows one muted line, "no room on this
     *     edge yet: it lists when the city grows here"; a side listing fewer
     *     than six names its count on its chip ("North · 4"), whose tooltip
     *     no longer says "ten offers". The (i) gives the blocks' side in km
     *     from a kilometre ("245.76 km", where it read "245,760 m").
     *   - BUILD'S SHORTCUT names the offer it buys: "Buy the best: North 3 ·
     *     0.0288 km2 · US$215k ›" (it read "Buy the best land · ...").
     *   - THE MAP'S OVERLAY (ui/MapView; the arithmetic in MapFrame and
     *     LandMap, which MapCheck holds): every line on whole pixels; the
     *     city's edge as one shape of whole straight runs, crisp and
     *     stepped, a pixel inside its ground; the city's block lines, faint,
     *     where a block is 6 px or more; each offer hatched and edged on its
     *     free ground only - a coarse rectangle takes in the city's finer
     *     steps beside it, which were hatched as on offer - and numbered by
     *     its place on its freest square; an offer larger than the view now
     *     drawn over it (a corners' test dropped it); the outline and the
     *     cut-outs found once a purchase or a listing, not every frame.
     *   - Harnesses: MapCheck 7 (the overlay, new assertions). No new
     *     harness (81).
     *
     * 0.7.70 (2026-10-08) - THE ROAD BY ITS LIFE, AND A GRAVEL ROAD PAVED.
     * Batch N1 of Jerus's list after the grid: "the game still recommends
     * gravel roads, even when i think paved roads are better, also ... make
     * it an option to upgrade from gravel to paved, but not from paved to
     * highway, and that the build menu allows and recommends this if better,
     * total cost is higher than just building paved" (runs/fixN1-notes.md).
     *   - WHAT RECOMMENDED GRAVEL: not the advice, which has priced a road's
     *     ground since 0.7.51 and picks paved roads in Jerus's city, but
     *     Build's road cards - a road's price a trip, its ground left out, so
     *     "cheapest per trip" went to the gravel road in every city - and the
     *     test player, which ranked the roads by their founding cash cost a
     *     trip: every road it built in the 0.7.69 run was gravel (123 orders;
     *     a paved road and a highway were tried once each, when the cash
     *     would not pay for gravel either).
     *   - A ROAD OVER ITS LIFE (BuildAdvice.lifetime()): its quote, its ground
     *     at the land office's price, and a month of running it - repairs at
     *     the 1% a year every city building is charged, power and water at
     *     the utility's prices, a line's crews less its riders' fares - for
     *     LIFE_MONTHS (the funding page's 20-year bond) at the city's real
     *     rate for that term, over the trips it takes off the road (its
     *     freight grade in them). The advice ranks the road's candidates by
     *     it, the road cards draw it as their first bar and their ground a
     *     trip as their second, and the test player picks its road by it.
     *     Gravel still wins where ground is cheap: a paved road beats it
     *     where a gravel road's ground is worth about 1.4 to 1.7 times its
     *     price.
     *   - PAVING (ConstructionControl, F; Game.paveRoads(), quotePave()): a
     *     Gravel Road paved to a Paved Road, one or many - a Paved Road's work
     *     and material less the gravel road's material, which goes into its
     *     bed, plus the take-up of the old surface at the demolition rule, so
     *     a gravel road and its paving cost more than a Paved Road; queued on
     *     the Paved Road site as the city's order, standing on the gravel
     *     road's ground, which carries its traffic until its Paved Road opens
     *     and then retires, the 200,000 sq ft between the two freed. A Paved
     *     Road is not raised to a highway. The site is not stopped while it
     *     paves. Saved with the hand on the queue; an older save has none.
     *   - RECOMMENDED WHEN IT BEATS A NEW PAVED ROAD over its life a trip
     *     (BuildAdvice.pavingBeatsPaved()): the advice offers it as a card of
     *     its own (not in "Build all three"), the Gravel Road card tags it,
     *     and the test player paves where it is the least of the roads.
     *   - Harnesses: RoadCheck (new, 82); BuildAdviceCheck 7 and BuildCardCheck
     *     2 price a road over its life. The playtest re-baselined (pt0770).
     *
     * 0.7.71 (2026-10-08) - CHILDCARE RESIZED.
     * Batch N2 of Jerus's list after the grid: "one city had 5k daycares and
     * 2k residential buildings, hilarious, the numbers children and housing
     * wise make sense ... i think we need to resize those, daycares are
     * childcares and childcares are even bigger" (runs/fixN2-notes.md).
     *   - WHO BUILT THE DAYCARES: the build advice and Build's cards. A Home
     *     Daycare (8 places, $147k) was the cheapest a place to put up, so the
     *     advice ordered them by the hundred and the cards tagged it cheapest
     *     per child: Jerus's live city holds 629 of them (5,032 places) beside
     *     1,521 home buildings. The test player builds no childcare.
     *   - THREE CENTRES ON THE SAME IDS (BuildingManager, childcare resized):
     *     15 Small Childcare Centre, 80 places (was Home Daycare, 8); 16
     *     Childcare Centre, 220 (was Neighbourhood Daycare, 60); 17 Large
     *     Childcare Centre, 360 (was Childcare Centre, 220) - the sizes
     *     Quebec's cap on a childcare installation, the old centre's, and
     *     China's guide for a kindergarten; each figure on the model's own
     *     scale curve through its two old centres, so a place costs less to
     *     build, keep, stand on and staff the bigger the centre, at one adult
     *     to 4.0 / 4.15 / 4.3 children.
     *   - A CITY'S BUILDINGS KEEP THEIR TYPE (Jerus: "keep them as they
     *     are"): a save holds them by id, so a Home Daycare loads as a Small
     *     Childcare Centre with its places, posts and ground. No format bump.
     *   - THE SIZE THAT FITS THE NEED (BuildAdvice.perPlaceNeeded()): a
     *     living care building is ranked by its whole order - quote and
     *     ground - over the places the need lacks, so a town short of a few
     *     children gets one small centre and a city short of thousands the
     *     large ones; the card says "per child without a place, the whole
     *     order with its land".
     *   - The map draws id 15 as care, not as a home (BuildingVisual).
     *   - The test player's childcare rule, the advice's card, behind
     *     -Dplaytest.childcare (off: the default playtest is byte-identical).
     *   - Harnesses: ChildcareCheck (new, 83); BuildAdviceCheck 7 prices a care
     *     card by its order; HealthCheck and BuildAdviceCheck name the new
     *     centres; MapCheck's dense fixture (Jerus's city at m1,851) holds
     *     his 86,032 childcare places as 239 Large Childcare Centres, on the
     *     ground his buildings need at his density.
     *
     * 0.7.72 (2026-10-08) - THE MAP'S ROADS AND RAIL.
     * Batch N3 of Jerus's list after the grid, "to make the map generation
     * prettier" (runs/fixN3-notes.md). Drawing only: the model is untouched
     * and the playtest byte-identical to 0.7.71's.
     *   - ONE NETWORK (CityMap's THE NETWORK, TilePainter): each district's
     *     road tiles, grown from its core and its mines, joined by main
     *     streets from hub to hub through crossings both tiles read alike;
     *     inside a tile the grid's streets JUNCTION_APART (8 plots, eight
     *     houses) apart, lanes off them in T's, then the fill - all of it
     *     before the small buildings, and the large ones keep the network
     *     the room its roads need (keepsNetwork()). Jerus's city x 1: 19,995
     *     road plots in one piece, 135 + junctions, none nearer than 8.
     *   - HIGHWAYS one straight run a district from its hub, turning only
     *     at the sea, the city's edge or a mine, crossing once at the hub.
     *   - EVERY BUILDING NEAR A ROAD: within REACH (4 plots) first; on Jerus's
     *     city 99.3%, the rest where their tile had no room near its roads.
     *   - RAIL drawn as track (the Rail Spur and Freight Line, plot by
     *     plot) on its tile's edge away from the main, one line a district,
     *     grey dashed white (TileRaster, the legend, the hover); a Rail
     *     Terminal is a yard beside it on the track's tile nearest a mine.
     *   - PACKING (from N2): the dense screen drew 16.7% of its buildings
     *     smaller than their land; a tile is now dealt to 90% of its room
     *     while another has some (TILE_FULL_MOST), and draws 7.2%.
     *   - The map's sidecar FORMAT 4: older maps are drawn again.
     *   - MapCheck: borders and ports restated as crossings; its section 8
     *     holds the network, the floor, the highways, the reach, the track
     *     and the yards. No new harness (83).
     *
     * 0.7.73 (2026-10-08) - AUTOMATIC BUILDING.
     * Batch N4 of Jerus's list after the grid: "an automatic build and
     * acquire debt button for basically automatic building, with a required
     * slack button that you add, aka maintain say 15% surplus service of
     * everything ... right in the build menu, and on/off, so that one can
     * focus on other things" (runs/fixN4-notes.md). His decisions: services
     * and infrastructure only; a debt limit, 15%, on debt payments as a
     * share of revenue, borrowing on the funding page's bonds only when the
     * cash runs out; a spare margin, 15%, for every service; off by default.
     *   - AutoBuilder, the first thing Game.nextMonth() does while it is on:
     *     for every service the five city categories open on (and the burial
     *     plots), the build advice's own card at the player's margin
     *     (BuildAdvice.suggestFor(..., slack)) - the same ranking and count
     *     the Build overview shows - cut to what the builders open within a
     *     year, the ground the city owns (it buys none), what the budget can
     *     run (a staffed building; past one it cannot, the next in the
     *     ranking) and the money: the cash over a month's tax, then the
     *     funding page's 20-year bond while debt payments stay under the
     *     limit - against a year's revenue, land sales and the builders' tax
     *     left out. A first school, police station or prison waits for half
     *     its worth of need. Placed through Build's own path; its log says
     *     what and why; what held a service back is the inbox's notice.
     *   - Build's Overview: AUTOMATIC BUILDING - the spare margin and the
     *     debt limit as dials, what it has done, and the switch; a chip on
     *     every other Build page while it is on.
     *   - Saved under one key (DataSave.autoBuild); an older save loads with
     *     it off. No format bump.
     *   - The test player's -Dplaytest.autobuild leaves it the city's works
     *     (off: the playtest is byte-identical to 0.7.72's).
     *   - Harnesses: AutoBuildCheck (new, 84).
     *
     * 0.7.74 (2026-10-08) - THE SECTOR STATEMENTS, A SUMMARY AND A STATEMENT.
     * Batch S1 of the sector statements, presentation only (the project's
     * spec-sector-statements.md, its section 10 batch 1; runs/fixS1-notes.md).
     * Jerus: "both a summarized and a detailed actual statement ... the
     * income statement, balance sheet, cash and debt, and investor ... a
     * banks income statement is different, cause interest income". The model
     * is untouched and the playtest byte-identical to 0.7.73's.
     *   - SectorStatements (new, model, pure): each business's month as a
     *     statement of profit or loss classified through gross and operating
     *     profit (sales tax under revenue, property tax an operating cost:
     *     D4, D5) with the flows that reach no statement as "outside the
     *     trading result" (F1, D6); a classified sheet; the cash flow in
     *     three sections, unexplained() its residual; the statement of
     *     changes in equity, its remainder named - and the bank's own income
     *     statement and sheet. Every bottom line the model's own.
     *   - Sector.statementFormat(): makers, merchants, landlords, builders,
     *     carriers - the words and the middle line.
     *   - R1 and R2, read where the lines are set and frozen with the
     *     month's books (EconomyManager, BusinessDebtManager.interestByKind()
     *     and debtByKind(), BondBook.faceDueWithin()): the interest by
     *     instrument, and the debt by kind and by when it falls due. Kept by
     *     SectorBooks for this month and last with each company's share, in
     *     memory, NOT saved: not counted the month after a load.
     *   - The Sectors pages and the Bank's Profit and Balance sheet: one
     *     Summary | Statement switch at the strip's right (D1, D9). Summary:
     *     the pictures, their subtotals the statement's (D14), IN SHORT and
     *     the ratios; OF EVERY DOLLAR IT TOOK; the equity's month as a
     *     bridge; the cash in three steps, its free cash flow, whether it
     *     can carry what it owes and when its debt falls due; FOR ITS
     *     SHAREHOLDERS. Statement: the formal statements (ui/StatementView:
     *     in $ thousands, millions past seven digits; negatives in
     *     parentheses; notes opening in place) and the investor report.
     *   - Harnesses: SectorStatementCheck (new, 85).
     *
     * 0.7.75 (2026-10-08) - THE SECTOR STATEMENTS, THE SECOND BATCH.
     * Batch S2 (the project's spec-sector-statements.md, section 10 batch 2;
     * runs/fixS2-notes.md): what batch 1 could not show from fields that
     * existed. Nothing in the model reads any of it; the playtest is
     * byte-identical to 0.7.74's.
     *   - R3, SHARE CAPITAL (D11): the register keeps each company's paid-in
     *     in money - its founders' book, what it raised, less all its
     *     buybacks paid (Equity.getPaidIn()) - and the books carry it with
     *     the founders' shares issued in the month. The sheet's equity is
     *     share capital and what it kept; the statement of changes in
     *     equity has the two as columns, share capital closing on its own
     *     lines. SAVE_FORMAT 33; an older save's is derived at the load and
     *     said so.
     *   - R4, EVERY GATE (D8, F11): BuildCard.appraise() asks every building
     *     every gate - ore, licence, staff, land, pays - with what one would
     *     earn, cost and take to pay back, and how it would be paid for
     *     (Game.financingOf(), consider()'s split read). The investor report
     *     shows it, with what each means; the summary keeps the first gate.
     *   - F2 AND R5: the bank's fees, the mortgages' premiums and the bonds'
     *     issuing costs, and the bonds their holders wrote off, saved with
     *     the month and named outside the trading result.
     *   - R6: what the prices did to the stock, the land and the buildings it
     *     began the month with, and what it holds abroad revalued - named in
     *     the equity statement before "and the rest".
     *   - R7: what moved its debt, by kind, counted where each moves
     *     (BusinessDebtManager.debtMovedByKind()) - the Cash & debt
     *     statement's schedule from last month's sheet to this month's, with
     *     each kind's rate and when the last of it falls due.
     *   - The owners card is on Investors (D7); the Balance sheet keeps a door.
     *   - Harnesses: SectorStatementCheck, sections 6b, 8 and 9 (85).
     *
     * 0.7.76 (2026-10-08) - OIL: THE GOODS, AND FUEL SPLIT INTO THE REFINERY'S
     * PRODUCTS. Batch O1 (runs/spec-oil.md 5's first row, 2.1, 2.3 and 3;
     * runs/fixO1-notes.md).
     *   - THE GOODS: FUEL is retired with a note (Good), and nine are
     *     appended - petroleum gas, naphtha, petrol, jet fuel, diesel,
     *     lubricants, fuel oil (litres, each with its litres a tonne,
     *     Good.litresPerTonne(), which is now what a litre weighs), bitumen
     *     and petroleum coke (tonnes) - at the research's wholesale ladder,
     *     petrol and diesel keeping FUEL's band until O2.
     *   - THE SLATE: an Oil Refinery is a crude unit. Its template makes
     *     nothing (id 75 loses FUEL); its crude is cut by a medium crude's
     *     cuts and each cut becomes its product, the residue cut three to
     *     one with the diesel into fuel oil (Refining.slate()): a tonne makes
     *     70 L of petrol and 169 L of diesel, where it made 1,000 L of FUEL.
     *     Refining's capacity, pipeline and tank room are the slate's; the
     *     tanks are shared as the run is.
     *   - THE BUYERS: the drivers draw PETROL (Motoring), the railway DIESEL
     *     (Rail, its statement's "Diesel"); the trade by good, the audit's
     *     PetrolFunded and PetrolImports and the goods held (HELD, twelve)
     *     follow. Everything else the refinery makes is exported.
     *   - THE GATE IN PETROL AND DIESEL: a refinery is built for a whole
     *     plant's worth of the city's own petrol and diesel (1.98M L a
     *     month) or of its wells' spare crude, and its estimate is struck
     *     over its slate (BusinessInvestment.estimatedMakerProfit() with
     *     what it makes, Refining.madeBy()).
     *   - SAVE_FORMAT 34; an older save's FUEL is split on its load
     *     (FuelSplit), no money moving.
     *   - Harnesses: OilCheck (the slate; the split), and the FUEL premises
     *     of BuildingDataCheck, BuildCardCheck, ForeignCheck, RailCheck,
     *     TradeCostCheck, MoneyCheck and SectorStatementCheck (85).
     *
     * 0.7.77 (2026-10-08) - GRAVEL ROADS BRIDGE RIVERS, AND AUTOMATIC BUILDING
     * BUYS ITS GROUND. Batch N5 of Jerus's list after the grid, two of his
     * answers of 2026-10-08 (runs/fixN5-notes.md). The playtest is
     * byte-identical to 0.7.76's (automatic building is off by default; the
     * map is drawing only).
     *   - THE MAP: Jerus, "gravel road bridge rivers sure". A street crosses
     *     fresh water gravel as paved, up to TilePainter.STREET_BRIDGE (6
     *     plots, the mockup's paved bridge): every district's main streets
     *     may bridge a river (CityMap; only one with paved road before), and
     *     a main street's or the grid's bridge is gravel where the tile has
     *     no paved left. Drawn as every bridge is, the road in its own colour
     *     over the deck. The sea is still bridged by nothing. His city at
     *     month 416 draws its roads in 2 pieces where it drew 4 (the streets
     *     north of the river join the rest; a corner patch stays apart), the
     *     playtest's at month 1,000 in 1 (4), at 4,000 in 7 (8: sea).
     *   - AUTOMATIC BUILDING BUYS ITS GROUND: Jerus, asked whether it should
     *     buy the land its orders need: "no you do need to buy land", read as
     *     it must. An order short of ground, once the builders and the budget
     *     have cut it, buys what Build's land shortcut would buy for the
     *     shortfall - the cheapest bare offer that covers it, else the best
     *     value of bare ground offer by offer, never a field for its ore -
     *     paid with the order from the cash over a month's tax and then the
     *     funding page's bond within the debt limit, fewer where all of it is
     *     not; held for the ground only where no bare offer is to be had
     *     (AutoBuilder.groundFor()). Its log and totals carry the ground
     *     (saved, under the same key; an older save has none), and the inbox
     *     notes each purchase and why ("autobuild-land").
     *   - Harnesses: MapCheck (gravel bridges; a river parts gravel streets
     *     no more than paved), AutoBuildCheck (the ground it buys; a town
     *     buying its ground saved and loaded) (85).
     *
     * 0.7.78 (2026-10-08) - OIL: PETROL AND DIESEL AT WHOLESALE. Batch O2
     * (runs/spec-oil.md 5's second row, 2.1, 2.5 and 6 A; Jerus 2026-10-08,
     * "yes wholesale"; runs/fixO2-notes.md).
     *   - THE PRICES: petrol and diesel leave FUEL's band - a journey's $2.00
     *     over its 1.2 L in, .0007 out, a pump price - for the research's
     *     wholesale ladder, 1.20 and 1.35 of crude's world middle a litre:
     *     petrol .0006118 in, .0005212 out, freight .00003399; diesel
     *     .0006883, .0005864, .00003824 (Good). With no refinery the drivers'
     *     fuel costs 63% less and the railway's 59% (about half where a
     *     railway carries the goods stream, whose freight had left the band).
     *   - THE RAILWAY'S LITRES: Rail.FUEL_LITRES_PER_TONNE is the literal 18,
     *     where it was WORLD_FUEL_PER_TONNE over the import price: the litres
     *     stay, the bill falls.
     *   - WHAT IT DOES: the owners weigh a journey's fuel at wholesale against
     *     a ride, so at the default fare none take the bus on cost alone,
     *     where up to two fifths did at the pump price; the transit share of
     *     commuters falls only where the owners had seats the car-less did
     *     not fill. And no refinery pays on wholesale prices at the playtest's
     *     size: none is built (0.7.76 and 0.7.77 built and shed eleven).
     *   - Harnesses: OilCheck (1 all nine on the ladder; 6 a product nobody
     *     buys idles where shipping it does not pay; 7 the bill on the
     *     ladder), CarCheck (a journey's fuel at wholesale, fewer owners on
     *     the bus), TradeCostCheck's prices, RailCheck (its reload by the
     *     game's own load) (85).
     *
     * 0.7.79 (2026-10-08) - OIL: CRUDE GRADES AND THE SEA'S DEPTH. Batch O3
     * (runs/spec-oil.md 5's third row, 2.2 and 2.7; runs/fixO3-notes.md).
     *   - THE GRADES: every oil field is light, medium or heavy crude, a third
     *     of fields each (Deposit.grade(), drawn from the field's cell and
     *     index, so nothing is stored and no tonne moves). Each grade cuts by
     *     its own column (Refining.CUTS): light Brent's, heavy Maya's, medium
     *     the blend imports are. A tonne of light makes 97 L of petrol and
     *     252 L of diesel; of heavy, 59 L of petrol and no diesel, 101 L of
     *     residue burned for want of diesel to cut it.
     *   - THE MONTH'S MIX: the wells' lift is graded field by field in the
     *     order the ground was bought (LandManager's oil runs: oil a fixture
     *     holds by fiat is medium), and a refinery's crude is its local part
     *     at the lift's grades and its imports at medium; next month's slate
     *     is struck on that mix (Refining, saved as crudeMix.*; a save without
     *     it reads medium). The tanks' shares and the planner stay on medium
     *     crude until the spread planner (O5).
     *   - THE SEA'S DEPTH: the world's sea has a depth (World.depthAt()), the
     *     shallowest 8.86% of it the shelf down to 140 m and deeper below at
     *     the same slope; a city's sites of a resource are dry or the sea's by
     *     where each field's centre lies (LandManager.getSites(r, dry)), what
     *     land wells (O7) and platforms (O10) will stand on.
     *   - O2'S TWO GATES: CentralBankCheck 21's very loose step is asserted on
     *     the side of the target its city runs (under it, where Standard
     *     eases, very loose holds prices lower); the land office opens no
     *     farther out than its smallest offer drawn 12 px across
     *     (LandMap.open(), MapFrame.OPENING_OFFER_PX), so every offer it
     *     shows whole is numbered.
     *   - The playtest is byte-identical to 0.7.78's: it builds no refinery.
     *   - Harnesses: OilCheck 12 (crude by grade), WorldCheck 7 (the depth,
     *     the grades), CentralBankCheck 21, MapCheck 7 (the opening zoom),
     *     SaveFileCheck and ReadPathCheck (the mix) (85).
     */
    public static final String VERSION = "0.7.79";

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
     *
     * 28 - THE PLAYER'S HAND ON THE QUEUE (0.7.22): the city's order of its
     *     own sites, the rushes and their months on overtime, the cancels
     *     waiting for the month's end, the stopped shells, the demolitions on
     *     site and the buy-outs, under one key (DataSave.constructionControl;
     *     ConstructionControl). Here because a format-27 build handed this
     *     save would load it WRONGLY, not merely incompletely: a shell and a
     *     demolition on site hold ground the load strikes the allocation from,
     *     and their buildings are off the stacks - a shell's progress and the
     *     material it drew, a demolished building already closed and paid
     *     for - so an older build would come back with the ground free, the
     *     work gone and the money spent. That is the accident this number
     *     exists to prevent.
     *
     *     THE OTHER DIRECTION IS SAFE: a format-27 save has no key, and loads
     *     with no order set, nothing rushed or stopped and nothing being
     *     demolished - which is what that city had.
     *
     * 29 - THE DECISION LOG (0.7.23): the player's decisions, a month, a kind
     *     and a line each, under one key (DataSave.decisionLog; DecisionLog).
     *     Here because a format-28 build handed this save would lose them
     *     for good, not merely leave them unread: it loads the city, plays
     *     on, and its next save - every autosave - writes the city back
     *     without the key. A decision is a flow; nothing in the rest of the
     *     save could give one back, and the chart's flags would be gone
     *     from that city's whole past.
     *
     *     THE OTHER DIRECTION IS SAFE: a format-28 save has no key, and loads
     *     with an empty log - which is what that city had kept.
     *
     * 30 - THE MONTH THE PEOPLE PAGE DRAWS (0.7.27): Migration's last month
     *     (the draw and its two halves, the pulls, the arrivals and the
     *     departures and who made them up, the mixes and the licences) after
     *     its wage history; the pyramid's dead by cause after its flows; and
     *     the share the shops handed over and the hungry at full shelves after
     *     the households' row array. Here because a format-29 build handed
     *     this save would load it WRONGLY: each of the three arrays is now a
     *     length it refuses whole, so it would come back with no wage history
     *     - every tier's twelve-month streak gone, nobody able to leave for a
     *     year - and with a pyramid of nobody. That is the accident this
     *     number exists to prevent.
     *
     *     THE OTHER DIRECTION IS SAFE: a format-29 save carries the arrays at
     *     their old lengths, which still read, and loads with those figures at
     *     0 until a month runs - what a reloaded People page showed before.
     *
     * 31 - THE LAND ON THE WORLD (0.7.57): the city's centre, its forty lanes,
     *     every purchase, the forty offers standing, what has been taken out
     *     of its ground and the world's totals (DataSave's landCentre,
     *     landLanes, landPurchases, landOffers, nextOfferId, depletion,
     *     worldTotals, worldSeaTheta). Here because a format-30 build handed
     *     this save would load it WRONGLY: it carries no iron pool and no
     *     listing, so the older build would come back with no ore under a
     *     city whose mines stand on it, and nine fresh parcels.
     *
     *     THE OTHER DIRECTION IS CONVERTED, ONCE (LandConversion): a format-30
     *     save's square feet become a centre on its own world holding exactly
     *     that dry ground, its iron sites (at least its mines) and tonnes its
     *     own, forty offers at today's prices, and its nine parcels go.
     *
     *     0.7.59 ADDS freshRights WITHOUT A BUMP: a format-31 save without it
     *     loads rightly, given the rights its plants already pump (Game,
     *     THE FRESH WATER LIMIT AND THE COAST).
     *
     *     0.7.60 ADDS mapStamp WITHOUT A BUMP: the stamp of the city map's
     *     sidecar (slot-NN-map.bin, CityMap's format 2 since 0.7.64); a save
     *     without it had no map, and draws one when it is first asked for.
     *
     *     0.7.62 ADDS householdFuel WITHOUT A BUMP: a save without it struck
     *     its drivers' fuel as drivers x journeys x a journey's fuel, every
     *     litre imported, and loads with exactly that (Game's load path).
     *
     *     0.7.63 ADDS householdGraduates AND EVERY SECTOR'S FOUR RATIOS
     *     WITHOUT A BUMP: a save without them loads as every load did. It
     *     also writes every sector's month's units, empty or not, so a save
     *     without them is one from before 0.7.46 and has them derived; an
     *     older build reads the new keys as nothing, as before.
     *
     * 32 - THE LAND ON THE BLOCK GRID (0.7.67): the centre's record 24 wide,
     *     its half-side gone (landCentre), its blocks (landCentreRects), each
     *     purchase as a rectangle 31 wide (landHoldings), the fields held in
     *     part (landPartFields), a converted city's purchase history
     *     (landConverted) and the offers 29 wide (landOffers); no landLanes
     *     and no landPurchases. Here because a format-31 build handed this save
     *     would load it WRONGLY: its centre record is the wrong width there,
     *     so that build would draw a new centre from the figure alone and list
     *     forty fresh offers, dropping every purchase and the offers standing.
     *
     *     THE OTHER DIRECTION IS CONVERTED, ONCE (LandConversion.convertLanes(),
     *     spec-grid 2.6): a format-31 save's lanes snapped to whole blocks one
     *     level finer than its offers, its fields deciding, its books its
     *     drawn plots and its figure their dry ground, its sites, tonnes and
     *     E as saved, its purchases kept as history, no money moving, and
     *     twenty-four places listed afresh. A format-30 save is put on the
     *     blocks as well (LandConversion.convert()): a centre of blocks round
     *     J1b's site holding its dry ground to within a plot, where format
     *     31's conversion drew a centre holding it exactly.
     *
     * 33 - SHARE CAPITAL (0.7.75, the sector statements' R3): every company's
     *     register entry three numbers longer (Equity.SLOTS) - the book its
     *     founders' shares were issued against, what its buybacks paid, and
     *     whether the two were derived - and the books' months a dozen fields
     *     longer (SectorMonth's paidIn to stockCounted). Here because a
     *     format-32 build handed this save would load it WRONGLY: its
     *     register refuses an array of a width it does not know whole
     *     (Equity.restore()), so that build would come back with no
     *     shareholders at all - no shares, no record, no dividend ring.
     *
     *     THE OTHER DIRECTION IS DERIVED: a format-32 save's paid-in is what
     *     each company raised since founding, at home and abroad - its
     *     founders' book and what its buybacks paid were not kept - marked
     *     derived (Equity.isPaidInDerived()), and its books' two months put on
     *     it (SectorBooks.derivePaidIn()); the screens say so.
     *
     * 34 - FUEL SPLIT (0.7.76, batch O1; spec-oil 3): no FUEL anywhere - its
     *     market, its sectors' stock and books and its slot in the goods held
     *     are PETROL's and DIESEL's, and the goods held are twelve
     *     (NationalAccounts.HELD). Here because a format-33 build handed this
     *     save would load it WRONGLY: it knows no PETROL or DIESEL, so it
     *     would drop the refiners' tanks, the month's fuel sales and both
     *     markets' strike, and read the goods held at the wrong width.
     *
     *     THE OTHER DIRECTION IS CONVERTED, ONCE (FuelSplit, before anything
     *     is restored): each FUEL figure into PETROL and DIESEL by the share
     *     of the month's litres the drivers burned against the railway's,
     *     each pair summing to the figure to the bit, the railway's own
     *     purchases all diesel, the market's price kept for both; the other
     *     seven products start at a known zero. No money moves: at 0.7.76
     *     petrol and diesel carry FUEL's band.
     * --------------------------------------------------------------------- */
    public static final int SAVE_FORMAT = 34;

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
