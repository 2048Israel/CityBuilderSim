# FloatLinkCheck.java - 2,092 lines · 71 methods · 23 constants · harnesses

`ham/citybuildersim/FloatLinkCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md
> sections 2.4, 2.5, 5.3 and 5.4; D25): a floater pays the month's policy rate
> plus a margin fixed at the sale, never under nothing; a linker pays a real
> coupon on a principal that follows the city's price index and, at maturity,
> the indexed face - never under the original face. Both are sold at the auction
> at the margin or real coupon that prices them at 100 on the curve, and carried
> on the books by effective interest: the floater's rate resets with its coupon,
> the linker's carrying value catches up with each indexation.
> 
> What this has to prove:
> 
> 1. Fair prices it at 100: on a calm, a hiking and a flat curve, with a flat and
>    a moving dial, the floater's payments at DebtManager.fairMargin() are worth
>    100 (the harness sums the present value itself) and a basis point either way
>    moves the price the right way; the linker's at fairRealCoupon() likewise with
>    the index at 2%, at 0% (the par yield) and falling, where the floor binds and
>    the last payment is the face. The study's figures are printed beside the game's.
> 2. A sale, in a played city: the margin is the auction's fair margin plus its
>    tail and the real coupon the fair plus the tail rounded down to Auction
>    .COUPON_STEP, both caused by capping a sale far past the bids; the floater's
>    index is the dial, the linker's base the price index; a second sale opens a
>    second line; a 3-year floater, a 20-year linker and a dollar one are refused
>    and change nothing; a default city with its rollover on never sells one unasked.
> 3. The floater's coupon follows the dial: each month's coupon is the face over
>    twelve at the dial plus the margin, read from the piece and from what its
>    holders were paid; the effective rate resets with it and stays where it was
>    when the dial did not move; the month's books hold; a margin under nothing
>    pays nothing on a dial at nothing; its value stays near par through a move
>    that takes a fixed bond of the same term down.
> 4. The linker's uplift: under an index the harness drives (rising, flat,
>    falling), the ratio follows the index over the base, each month's uplift is
>    the carrying value's catch-up to the present value of the known schedule at
>    the unchanged real rate, the coupon is the face times the ratio times the real
>    coupon over twelve, the statements' uplift is the month's and is in the
>    expenses, the treasury bridge foots with its not-cash row and leaves only the
>    bank's coupon timing, and the walk from EARNED to the budget leaves nothing in
>    a city whose paper the bank holds (and, where others hold it, only what it
>    leaves for any paper: the coupons EARNED never counted).
> 5. To maturity: a linker under a rising index repays its face through the
>    principal and the uplift through the coupons, each holder by its share; under a
>    falling one the original face (the floor) and no uplift; both end carried at
>    nothing; over each life interest expense plus uplift booked is the cash coupons
>    plus the face less what was raised, to the cent. A floater played to maturity
>    with the dial moved twice closes the same way.
> 6. The money closes: the audit closes every month pressed in 3-5 (and 7-9), the
>    households, the bank, the world and the central bank holding parts; every
>    holder was paid its share of each coupon and the cash coupons are what the
>    treasury paid on its interest line, so nothing is counted twice.
> 7. The buyback: half of a floater and half of a linker bought back at or over
>    fair, fair being the market value on the dial and index it expects; the piece's
>    face, carrying value and holders fall by what each sold and its rate, margin,
>    real coupon and ratio stay; the audit closes; a round trip never nets the city
>    money.
> 8. The rollover: under SAME_STRUCTURE a floater falling due rolls into a floater
>    and a linker into a linker, each of the nearest of its own terms, and the
>    central bank's par with it; under TWELVE_MONTH_BILL into a bill.
> 9. The save: a floater and a linker mid-life, saved and loaded, keep their type,
>    margin, index, real coupon, base, ratio, carrying value, effective rate and
>    market value, and the government block its uplift; the month after the load
>    books what the same month played without the save does; a change of unit
> ... (25 more lines in the source)

**Uses:** [Debt](Debt.md) (72), [FloatingRateNote](FloatingRateNote.md) (64), [InflationLinkedBond](InflationLinkedBond.md) (63), [PriceLevel](PriceLevel.md) (53), [Game](Game.md) (39), [DebtManager](DebtManager.md) (29), [YieldCurve](YieldCurve.md) (16), [Rollover](Rollover.md) (13), [MoneyAudit](MoneyAudit.md) (12), [NationalAccounts](NationalAccounts.md) (9), [Buyback](Buyback.md) (8), [Auction](Auction.md) (7), [DebtQuote](DebtQuote.md) (6), [CallCheck](CallCheck.md) (5), [Founding](Founding.md) (4), [Household](Household.md) (4), [Bank](Bank.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [TreasuryJournal](TreasuryJournal.md) (2), [GameFiles](GameFiles.md) (2), [Sectors](Sectors.md) (2), [CapitalFlows](CapitalFlows.md) (2), [LongTermBond](LongTermBond.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [CentralBank](CentralBank.md) (1), [Sector](Sector.md) (1), [Landmasses](Landmasses.md) (1)

**Used by (4):** [CallCheck](CallCheck.md), [DeskCheck](DeskCheck.md), [DeterminismCheck](DeterminismCheck.md), [RestructureCheck](RestructureCheck.md)

## Sections

| line | section |
|---:|---|
| 218 | THE CROSS-CUTTING IDENTITIES, held at every press of 3-5 (section 6 reports them) |
| 457 | THE PURE FIXTURES: the study's calm city, a hiking one, and a flat one |
| 520 | MAIN |
| 556 | THE PLAYED CITY |
| 836 | 1. FAIR PRICES IT AT 100 |
| 934 | 2. A SALE |
| 1161 | 3. THE FLOATER'S COUPON FOLLOWS THE DIAL |
| 1274 | 4. THE LINKER'S UPLIFT |
| 1384 | 5. TO MATURITY |
| 1415 | · the bank-held fixtures: one city, run once, read by 3 (the margin under nothing) and 5 (the floor) |
| 1643 | 6. THE MONEY CLOSES |
| 1676 | 7. THE BUYBACK |
| 1846 | 8. THE ROLLOVER |
| 1987 | 9. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 204 | `FloatLinkCheck.audits` | `new Tally()` |  |
| 207 | `FloatLinkCheck.ran` | `new boolean [ 10 ]` | Which of sections 3-5 have run, for section 6 to know what it can ask of the months they pressed. |
| 223 | `FloatLinkCheck.X_HOLDERS` | `new Worst()` | Each holder was paid its share of every coupon: households, businesses, the world, the central bank. |
| 225 | `FloatLinkCheck.X_TREASURY` | `new Worst()` | The cash coupons the pieces paid are what the treasury paid on its interest line, the uplift paid beside it. |
| 227 | `FloatLinkCheck.X_BOOKS` | `new Worst()` | The debt manager's month and the statements' lines are the sums of the pieces' months. |
| 229 | `FloatLinkCheck.X_NOTCASH` | `new Worst()` | The bridge's not-cash row is the expense the cash never paid. |
| 231 | `FloatLinkCheck.X_FOOTS` | `new Worst()` | The bridge foots through its journal and its residual. |
| 233 | `FloatLinkCheck.X_BRIDGE` | `new Worst()` | The bridge's residual is the bank's coupon timing and nothing else: the uplift is not in it. |
| 235 | `FloatLinkCheck.X_EARNED` | `new Worst()` | The walk from EARNED to the budget leaves what EARNED never counted - the coupons paid beyond the bank - and nothing of the uplift. |
| 652 | `FloatLinkCheck.DECLINE_WAIT_MONTHS` | `36` | FS14: the most months the played city is given past its settling for no tier to be in decline. |
| 655 | `FloatLinkCheck.ROOM_FOR_SALES` | `8` | BA1: the bank's room for the sales of a section, as a multiple of the one section 2 makes (saleOf(city, 1): the households' whole spare, no more than a third of a year of tax): that section sells six of them in one ci... |
| 658 | `FloatLinkCheck.EFFECTIVE_OFF_COUPON` | `5e-4` | BA1: the most a floater's effective rate may stand off its coupon, the fixed fee of the sale's share of it included (5 basis points): SALE_EFF's and section 3's bound, and what readyCity() sizes the sale's fee against. |
| 707 | `FloatLinkCheck.SPARE_OVER_LEAST_SALE` | `4` | FS14: the spare for paper readyCity() hands the households when theirs is less, as a multiple of the least sale the fee needs (leastSale()): at exactly the least sale the fee's share is the allowance to the last place... |
| 710 | `FloatLinkCheck.ROOM_HANDED_OVER` | `1.001` | FS14: the room readyCity() hands the bank is this much over the sales' face, so the last place of a float never decides it. |
| 739 | `FloatLinkCheck.LIFT_OVER` | `1e-9` | FC2: how far over the target liftSpare() raises the spare (a billionth), so that the last place of a float in the sum over the cells never decides a fixture line. |
| 811 | `FloatLinkCheck.SALE_OF_REVENUE` | `1./ 3` | The most one sale of section 2 and 3's fixture is, of a year's tax revenue (EM1, 2026-10-29). |
| 940 | `FloatLinkCheck.floaters` | `new ArrayList<>()` |  |
| 941 | `FloatLinkCheck.linkers` | `new ArrayList<>()` |  |
| 948 | `FloatLinkCheck.CAP_TAIL` | `.0025` | A tail the harness caps the sale at, to cause one: a sale far past the bids stops at the cap. |
| 950 | `FloatLinkCheck.SALE_MARGIN` | `new Worst(), SALE_INDEX = new Worst(), SALE_CASH = new Worst(), SALE_BOOKS = ...` |  |
| 1166 | `FloatLinkCheck.HELD_OVER_THE_WORLD` | `.0075, DIAL_MOVE =.015` | How far over the world's rate the dial is held in 3 (the old calendar's 3% over its 2.25%), and the move of it the section reads the bond's fall on. |
| 1412 | `FloatLinkCheck.M_SHARES` | `new Worst()` |  |
| 1413 | `FloatLinkCheck.FX_ZERO` | `new Worst()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 108 | `static int fails` |  |
| 109 | `static PrintStream out` |  |
| 110 | `static PrintStream quiet` |  |
| 159 | `int n` |  |
| 160 | `double worst` |  |
| 161 | `String at` |  |
| 183 | `final Map<String, int[]> byEvent` |  |
| 184 | `int months, open` |  |
| 185 | `double worst` |  |
| 186 | `String worstAt` |  |
| 208 | `static boolean ranRisingMaturity, ranFallingMaturity` |  |
| 239 | `final double ref` |  |
| 240 | `final double[] growth` |  |
| 241 | `int k` |  |
| 262 | `final Debt piece` |  |
| 263 | `final int remaining` |  |
| 264 | `final double carrying, eff, face, ratio, index` |  |
| 294 | `final Debt piece` |  |
| 295 | `final double raised` |  |
| 296 | `double expense, uplift, coupon` |  |
| 306 | `final Worst coupon` |  |
| 309 | `final Map<Debt, Life> lives` |  |
| 310 | `double prevBank` |  |
| 311 | `int moves` |  |
| 756 | `static boolean feeThresholdShown` | Whether the fee threshold has been shown from both sides in this run (it is the fee's, not a city's, so once). |
| 939 | `static Game city` | The city sections 2-4 share, and the pieces sold in it. |
| 942 | `static LongTermBond fixedTwoYear` |  |
| 945 | `static double sPrevBank` | The bank's share of the last month's coupons in the shared city, carried from section 3 to section 4 for the bridge's timing. |
| 1417 | `static Game fxCity` |  |
| 1418 | `static FloatingRateNote fxNeg, fxPos` |  |
| 1419 | `static InflationLinkedBond fxUp, fxDown` |  |
| 1420 | `static Books fxBooks` |  |
| 1421 | `static double fxNegAtNothing, fxNegAtHalf, fxNegAtTwo, fxRepaid, fxUpPaid, fxDownPaid, fxUpRatio, fxDownRat...` |  |
| 1422 | `static double fxOwed, fxOwedWant, fxPricedGap, fxDownMid` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 106 | 1987 | **type** `public class FloatLinkCheck` | The floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md sections 2.4, 2.5, 5.3 and 5.4; D25): a floater pays the month's policy rate plus a margin fixed at the sale, never under nothing; a linker ... |
| 112 | 4 | `static void check(String label, boolean ok)` |  |
| 117 | 9 | `static void near(String label, double actual, double expected, double tol)` |  |
| 127 | 4 | `static void quietly(Runnable r)` |  |
| 132 | 4 | `static<T> T quiet(Supplier<T> s)` |  |
| 137 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 144 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |
| 148 | 5 | `static boolean wanted(String[] args, int section)` |  |
| 158 | 22 | **type** `static final class Worst` | The worst gap between what the model did and what the harness worked out, over every case a section met: reported once, and failed if no case ran at all. |
| 163 | 9 | `void add(double actual, double expected, String where)` _(in FloatLinkCheck.Worst)_ |  |
| 173 | 6 | `void report(String label, double tol)` _(in FloatLinkCheck.Worst)_ |  |
| 182 | 21 | **type** `static final class Tally` | The audits of every month the harness pressed, by what happened in it. |
| 188 | 14 | `void add(String event, MoneyAudit.Result r)` _(in FloatLinkCheck.Tally)_ |  |
| 211 | 6 | `static MoneyAudit.Result press(Game g, String event)` | A month, held to the audit and told to the tally. |

### THE CROSS-CUTTING IDENTITIES, held at every press of 3-5 (section 6 reports them) (lines 218-456)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 21 | **type** `static final class Index implements DoubleSupplier` | A price index the harness drives: the level at step k is the reference times the growth of the first k steps, and flat beyond the last. |
| 243 | 4 | `Index(double ref, double...growth)` _(in FloatLinkCheck.Index)_ |  |
| 248 | 5 | `double levelAt(int steps)` _(in FloatLinkCheck.Index)_ |  |
| 255 | 3 | `public double getAsDouble()` _(in FloatLinkCheck.Index)_ |  |
| 261 | 15 | **type** `static final class Snap` | One piece as a press finds it: the figures the month's identities start from. |
| 266 | 9 | `Snap(Debt d)` _(in FloatLinkCheck.Snap)_ |  |
| 285 | 6 | `static double[] holdersOf(Debt d)` | Who held a piece when its coupon was paid, as shares of it: households, businesses, the world, the central bank. |
| 293 | 10 | **type** `static final class Life` | What a piece cost and paid over its life: the books' identity is read off it at the end. |
| 298 | 4 | `Life(Debt piece, double raised)` _(in FloatLinkCheck.Life)_ |  |
| 305 | 12 | **type** `static final class Books` | The piece-level identities of one stretch of presses, and the lives of the pieces followed. |
| 313 | 3 | `void follow(Debt piece, double raised)` _(in FloatLinkCheck.Books)_ |  |
| 323 | 133 | `static MoneyAudit.Result press(Game city, Books b, String event)` | A month pressed and held to everything the pieces owe the books: the coupon, the reset or the catch-up, the effective interest, the carrying value; then the statements' lines, the holders, the bridge and the walk from... |

### THE PURE FIXTURES: the study's calm city, a hiking one, and a flat one (lines 457-519)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 3 | `static YieldCurve flatCurve()` | A curve with the dial at 3% for ever and no shape: every zero rate 3%. |
| 467 | 4 | `static YieldCurve studyCalm()` | The study's calm city (spec section 3.3): the dial at 3.00%, the 1-year zero rate 3.12% - twelve points of credit spread - and the long end on CallCheck's row. |
| 473 | 5 | `static double[] dialPath(YieldCurve c)` | The dial a curve expects, month by month. |
| 480 | 6 | `static double[] floaterFlows(double[] path, double margin, int months)` | What a floater pays per 100 on a dial path, as the spec says it: each month's coupon reads the dial one tick on, never under nothing, and the face comes with the last. |
| 488 | 5 | `static double pv(YieldCurve c, double[] flows)` | The payments' present value on a curve, summed here from the curve's factors. |
| 495 | 5 | `static double[] growthAt(double annual)` | An index growing this much a year, month by month. |
| 502 | 10 | `static double[] linkerFlows(double real, double[] growth, int months, boolean floor)` | What a linker pays per 100 when its first payment is on a ratio of 1 and the index then grows as given; the last month adds the face, or the indexed face when that is more (the floor) or whatever it is (no floor). |
| 514 | 5 | `static double solveReal(YieldCurve c, double[] growth, int months, boolean floor)` | The real coupon that prices that linker at 100, found from two evaluations of the present value (it is linear in the coupon). |

### MAIN (lines 520-555)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 31 | `public static void main(String[] args) throws Exception` |  |

### THE PLAYED CITY (lines 556-835)

| line | len | member | says |
|---:|---:|---|---|
| 561 | 77 | `static Game playedCity(String label)` | The city BuybackCheck was written against, with the rollover off, the dial held by hand at 3% and the world given an appetite. |
| 644 | 6 | `static boolean orderFunded(Game city, String name, long quantity)` | FC2, 2026-11-23: one order of the played town, placed funded: the cash handed, where the city's own is under the order's quote (Game.quoteBuildOn(), the total buildStack() itself weighs against the cash), by that quot... |
| 661 | 3 | `static double bankRoomOf(Game city)` | BA1: the bank's room for the city's paper, as the auction counts it (Game.auctionBidders(): equity over Bank.CAPITAL_RATIO less the weighted book, over Bank.RISK_CITY). |
| 666 | 7 | `static void worldWantsPaper(Game city)` | The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. |
| 675 | 12 | `static Game preparedCity(String label, boolean manualRollover, double centralBankShare)` | A played city ready to sell: the world wants paper, cash to spare, nothing rolled over by itself, the dial where the harness holds it. |
| 693 | 3 | `static double leastSale(Game city)` | FS14: the least sale, in face, at which the fixed fee of an issue (Game.costOfIssuance(), the same whatever the face) stands within half the rate allowance on a 2-year floater: the fee's share of a year's interest, fe... |
| 698 | 3 | `static double feeShareOf(Game city, double sale)` | FS14: the fixed fee's share of a year's interest on a 2-year floater sold at this face: fee over face over the term. |
| 718 | 19 | `static boolean liftSpare(HouseholdBalance hb, double target)` | FS14: the households' savings raised, never lowered, until what they could put into paper (HouseholdBalance.spareForPaper()) is this much: first every non-empty cell to its cushion, so that a dollar a household adds i... |
| 747 | 7 | `static double handBankRoom(Game city, double face)` | FS14: the bank handed its room for this face of the city's paper, as the auction counts it (Game.auctionBidders(): its equity over Bank.CAPITAL_RATIO less its weighted book, over Bank.RISK_CITY), by what its equity la... |
| 764 | 22 | `static Game readyCity(String label, boolean manualRollover, double centralBankShare)` | A prepared city that has what a played sale needs, caused (FS14): the households' spare lifted until a sale of section 2's size covers its fixed fee, and the bank handed its room for the sales of a section, both betwe... |
| 788 | 5 | `static Debt newest(DebtManager dm, Set<Debt> before)` | The piece the last call put on the books, or null. |
| 794 | 3 | `static Set<Debt> piecesOf(DebtManager dm)` |  |
| 799 | 3 | `static double askOf(Game city, double share)` | What the households could take, to size a sale that clears at fair. |
| 814 | 3 | `static double saleOf(Game city, double share)` | A sale of this share of what the households could take, and no more than SALE_OF_REVENUE of a year of the city's tax revenue. |
| 819 | 7 | `static FloatingRateNote fixtureFloater(Game city, double face, int months, double margin)` | A bank-held fixture floater, books opened at a sale-like price. |
| 828 | 7 | `static InflationLinkedBond fixtureLinker(Game city, double face, int months, double real, double base)` | A bank-held fixture linker, books opened at a sale-like price. |

### 1. FAIR PRICES IT AT 100 (lines 836-933)

| line | len | member | says |
|---:|---:|---|---|
| 840 | 86 | `static void fairAtPar()` |  |
| 927 | 6 | `static double maxDiff(double[] a, double[] b)` |  |

### 2. A SALE (lines 934-1160)

| line | len | member | says |
|---:|---:|---|---|
| 954 | 23 | `static FloatingRateNote sellFloater(int years, double ask)` | Sells a floater by its handler and holds the piece to its quote. |
| 979 | 26 | `static InflationLinkedBond sellLinker(int years, double ask)` | Sells a linker by its handler and holds the piece to its quote. |
| 1006 | 119 | `static void theSale()` |  |
| 1126 | 5 | `static int countOf(DebtManager dm, Class<? extends Debt> type)` |  |
| 1133 | 27 | `static void neverUnasked()` | A default city, a bill and a bond sold and played for years: not one floater or linker, though the rollover has run. |

### 3. THE FLOATER'S COUPON FOLLOWS THE DIAL (lines 1161-1273)

| line | len | member | says |
|---:|---:|---|---|
| 1168 | 81 | `static void followsTheDial()` |  |
| 1251 | 22 | `static void aMarginUnderNothing()` | The coupon is never under nothing: a floater with a margin below it, on a dial at nothing. |

### 4. THE LINKER'S UPLIFT (lines 1274-1383)

| line | len | member | says |
|---:|---:|---|---|
| 1278 | 86 | `static void theUplift()` |  |
| 1366 | 17 | `static void anIndexThatIsNoNumber()` | A linker on its own, no city: an index that is no number books nothing, the floor is the face, the ratio is the level over the base. |

### 5. TO MATURITY (lines 1384-1414)

| line | len | member | says |
|---:|---:|---|---|
| 1393 | 6 | `static double shorten(Game g, InflationLinkedBond l, int months)` | A linker sold for real and shortened to a year after it has settled, so the households, the bank and (once its dial has bought in) the central bank hold it: its books are reopened at the price it sold at on the shorte... |
| 1400 | 11 | `static void report(String name, Books b)` |  |

### the bank-held fixtures: one city, run once, read by 3 (the margin under nothing) and 5 (the floor) (lines 1415-1642)

| line | len | member | says |
|---:|---:|---|---|
| 1431 | 46 | `static void runFixtures()` | Four fixtures built through DebtManager.addFloatingRateNote() and addLinkedBond() and opened at a price a sale would have made, the bank holding all of them, in a played city with a price index the harness drives: a f... |
| 1478 | 7 | `static void toMaturity()` |  |
| 1486 | 28 | `static void fixtureMaturity()` |  |
| 1515 | 84 | `static boolean risingMaturity()` |  |
| 1600 | 42 | `static boolean fallingMaturity()` |  |

### 6. THE MONEY CLOSES (lines 1643-1675)

| line | len | member | says |
|---:|---:|---|---|
| 1647 | 28 | `static void theMoneyCloses()` |  |

### 7. THE BUYBACK (lines 1676-1845)

| line | len | member | says |
|---:|---:|---|---|
| 1681 | 19 | `static double[] handPricing(DebtManager dm, Debt piece)` | The piece's payments as the market prices it, worked out here from the dial and index the manager expects. |
| 1701 | 5 | `static double tills(Game g)` |  |
| 1708 | 56 | `static Buyback.Result part(Game g, Debt piece, double face, String what)` | A part of a settled piece bought back, held to everything a part has to hold. |
| 1765 | 80 | `static void theBuyback()` |  |

### 8. THE ROLLOVER (lines 1846-1986)

| line | len | member | says |
|---:|---:|---|---|
| 1850 | 118 | `static void theRollover()` |  |
| 1973 | 7 | `static void handRolloverRoom(Game g, Rollover.Plan plan)` | FS14: the bank handed its room for what the plan is about to issue (handBankRoom(): its equity by hand, between months), and the room asserted: the rollover sells the plan's issues at the auction, and the bank is what... |
| 1982 | 4 | `static Debt dueNextMonth(Debt piece)` | A fixture that falls due at the next press: its term stays what it was, its remaining months are one. |

### 9. THE SAVE (lines 1987-2092)

| line | len | member | says |
|---:|---:|---|---|
| 1991 | 101 | `static void theSave() throws Exception` |  |

