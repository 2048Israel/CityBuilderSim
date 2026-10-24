# FloatLinkCheck.java - 1,785 lines · 58 methods · 15 constants · harnesses

`ham/citybuildersim/FloatLinkCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

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
> ... (10 more lines in the source)

**Uses:** [Debt](Debt.md) (67), [InflationLinkedBond](InflationLinkedBond.md) (63), [FloatingRateNote](FloatingRateNote.md) (59), [Game](Game.md) (29), [DebtManager](DebtManager.md) (29), [YieldCurve](YieldCurve.md) (16), [MoneyAudit](MoneyAudit.md) (12), [Rollover](Rollover.md) (11), [NationalAccounts](NationalAccounts.md) (9), [Buyback](Buyback.md) (8), [DebtQuote](DebtQuote.md) (6), [Auction](Auction.md) (6), [CallCheck](CallCheck.md) (5), [Founding](Founding.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [GameFiles](GameFiles.md) (2), [Sectors](Sectors.md) (2), [CapitalFlows](CapitalFlows.md) (2), [LongTermBond](LongTermBond.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [CentralBank](CentralBank.md) (1), [Bank](Bank.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

**Used by (1):** [DeskCheck](DeskCheck.md)

## Sections

| line | section |
|---:|---|
| 202 | THE CROSS-CUTTING IDENTITIES, held at every press of 3-5 (section 6 reports them) |
| 426 | THE PURE FIXTURES: the study's calm city, a hiking one, and a flat one |
| 489 | MAIN |
| 526 | THE PLAYED CITY |
| 614 | 1. FAIR PRICES IT AT 100 |
| 712 | 2. A SALE |
| 924 | 3. THE FLOATER'S COUPON FOLLOWS THE DIAL |
| 1014 | 4. THE LINKER'S UPLIFT |
| 1118 | 5. TO MATURITY |
| 1149 | · the bank-held fixtures: one city, run once, read by 3 (the margin under nothing) and 5 (the floor) |
| 1359 | 6. THE MONEY CLOSES |
| 1391 | 7. THE BUYBACK |
| 1559 | 8. THE ROLLOVER |
| 1683 | 9. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 189 | `FloatLinkCheck.audits` | `new Tally()` |  |
| 192 | `FloatLinkCheck.ran` | `new boolean [ 10 ]` | Which of sections 3-5 have run, for section 6 to know what it can ask of the months they pressed. |
| 207 | `FloatLinkCheck.X_HOLDERS` | `new Worst()` | Each holder was paid its share of every coupon: households, businesses, the world, the central bank. |
| 209 | `FloatLinkCheck.X_TREASURY` | `new Worst()` | The cash coupons the pieces paid are what the treasury paid on its interest line, the uplift paid beside it. |
| 211 | `FloatLinkCheck.X_BOOKS` | `new Worst()` | The debt manager's month and the statements' lines are the sums of the pieces' months. |
| 213 | `FloatLinkCheck.X_NOTCASH` | `new Worst()` | The bridge's not-cash row is the expense the cash never paid. |
| 215 | `FloatLinkCheck.X_FOOTS` | `new Worst()` | The bridge foots through its journal and its residual. |
| 217 | `FloatLinkCheck.X_BRIDGE` | `new Worst()` | The bridge's residual is the bank's coupon timing and nothing else: the uplift is not in it. |
| 219 | `FloatLinkCheck.X_EARNED` | `new Worst()` | The walk from EARNED to the budget leaves what EARNED never counted - the coupons paid beyond the bank - and nothing of the uplift. |
| 718 | `FloatLinkCheck.floaters` | `new ArrayList<>()` |  |
| 719 | `FloatLinkCheck.linkers` | `new ArrayList<>()` |  |
| 726 | `FloatLinkCheck.CAP_TAIL` | `.0025` | A tail the harness caps the sale at, to cause one: a sale far past the bids stops at the cap. |
| 728 | `FloatLinkCheck.SALE_MARGIN` | `new Worst(), SALE_INDEX = new Worst(), SALE_CASH = new Worst(), SALE_BOOKS = ...` |  |
| 1146 | `FloatLinkCheck.M_SHARES` | `new Worst()` |  |
| 1147 | `FloatLinkCheck.FX_ZERO` | `new Worst()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `static int fails` |  |
| 94 | `static PrintStream out` |  |
| 95 | `static PrintStream quiet` |  |
| 144 | `int n` |  |
| 145 | `double worst` |  |
| 146 | `String at` |  |
| 168 | `final Map<String, int[]> byEvent` |  |
| 169 | `int months, open` |  |
| 170 | `double worst` |  |
| 171 | `String worstAt` |  |
| 223 | `final double ref` |  |
| 224 | `final double[] growth` |  |
| 225 | `int k` |  |
| 246 | `final Debt piece` |  |
| 247 | `final int remaining` |  |
| 248 | `final double carrying, eff, face, ratio, index, worldShare` |  |
| 249 | `final boolean settled` |  |
| 268 | `final Debt piece` |  |
| 269 | `final double raised` |  |
| 270 | `double expense, uplift, coupon` |  |
| 280 | `final Worst coupon` |  |
| 283 | `final Map<Debt, Life> lives` |  |
| 284 | `double prevBank` |  |
| 285 | `int moves` |  |
| 717 | `static Game city` | The city sections 2-4 share, and the pieces sold in it. |
| 720 | `static LongTermBond fixedTwoYear` |  |
| 723 | `static double sPrevBank` | The bank's share of the last month's coupons in the shared city, carried from section 3 to section 4 for the bridge's timing. |
| 1151 | `static Game fxCity` |  |
| 1152 | `static FloatingRateNote fxNeg, fxPos` |  |
| 1153 | `static InflationLinkedBond fxUp, fxDown` |  |
| 1154 | `static Books fxBooks` |  |
| 1155 | `static double fxNegAtNothing, fxNegAtHalf, fxNegAtTwo, fxRepaid, fxUpPaid, fxDownPaid, fxUpRatio, fxDownRat...` |  |
| 1156 | `static double fxOwed, fxOwedWant, fxPricedGap, fxDownMid` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 91 | 1695 | **type** `public class FloatLinkCheck` | The floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md sections 2.4, 2.5, 5.3 and 5.4; D25): a floater pays the month's policy rate plus a margin fixed at the sale, never under nothing; a linker ... |
| 97 | 4 | `static void check(String label, boolean ok)` |  |
| 102 | 9 | `static void near(String label, double actual, double expected, double tol)` |  |
| 112 | 4 | `static void quietly(Runnable r)` |  |
| 117 | 4 | `static<T> T quiet(Supplier<T> s)` |  |
| 122 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 129 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |
| 133 | 5 | `static boolean wanted(String[] args, int section)` |  |
| 143 | 22 | **type** `static final class Worst` | The worst gap between what the model did and what the harness worked out, over every case a section met: reported once, and failed if no case ran at all. |
| 148 | 9 | `void add(double actual, double expected, String where)` _(in FloatLinkCheck.Worst)_ |  |
| 158 | 6 | `void report(String label, double tol)` _(in FloatLinkCheck.Worst)_ |  |
| 167 | 21 | **type** `static final class Tally` | The audits of every month the harness pressed, by what happened in it. |
| 173 | 14 | `void add(String event, MoneyAudit.Result r)` _(in FloatLinkCheck.Tally)_ |  |
| 195 | 6 | `static MoneyAudit.Result press(Game g, String event)` | A month, held to the audit and told to the tally. |

### THE CROSS-CUTTING IDENTITIES, held at every press of 3-5 (section 6 reports them) (lines 202-425)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 21 | **type** `static final class Index implements DoubleSupplier` | A price index the harness drives: the level at step k is the reference times the growth of the first k steps, and flat beyond the last. |
| 227 | 4 | `Index(double ref, double...growth)` _(in FloatLinkCheck.Index)_ |  |
| 232 | 5 | `double levelAt(int steps)` _(in FloatLinkCheck.Index)_ |  |
| 239 | 3 | `public double getAsDouble()` _(in FloatLinkCheck.Index)_ |  |
| 245 | 20 | **type** `static final class Snap` | One piece as a press finds it: the figures the month's identities start from. |
| 251 | 13 | `Snap(Debt d)` _(in FloatLinkCheck.Snap)_ |  |
| 267 | 10 | **type** `static final class Life` | What a piece cost and paid over its life: the books' identity is read off it at the end. |
| 272 | 4 | `Life(Debt piece, double raised)` _(in FloatLinkCheck.Life)_ |  |
| 279 | 12 | **type** `static final class Books` | The piece-level identities of one stretch of presses, and the lives of the pieces followed. |
| 287 | 3 | `void follow(Debt piece, double raised)` _(in FloatLinkCheck.Books)_ |  |
| 297 | 128 | `static MoneyAudit.Result press(Game city, Books b, String event)` | A month pressed and held to everything the pieces owe the books: the coupon, the reset or the catch-up, the effective interest, the carrying value; then the statements' lines, the holders, the bridge and the walk from... |

### THE PURE FIXTURES: the study's calm city, a hiking one, and a flat one (lines 426-488)

| line | len | member | says |
|---:|---:|---|---|
| 431 | 3 | `static YieldCurve flatCurve()` | A curve with the dial at 3% for ever and no shape: every zero rate 3%. |
| 436 | 4 | `static YieldCurve studyCalm()` | The study's calm city (spec section 3.3): the dial at 3.00%, the 1-year zero rate 3.12% - twelve points of credit spread - and the long end on CallCheck's row. |
| 442 | 5 | `static double[] dialPath(YieldCurve c)` | The dial a curve expects, month by month. |
| 449 | 6 | `static double[] floaterFlows(double[] path, double margin, int months)` | What a floater pays per 100 on a dial path, as the spec says it: each month's coupon reads the dial one tick on, never under nothing, and the face comes with the last. |
| 457 | 5 | `static double pv(YieldCurve c, double[] flows)` | The payments' present value on a curve, summed here from the curve's factors. |
| 464 | 5 | `static double[] growthAt(double annual)` | An index growing this much a year, month by month. |
| 471 | 10 | `static double[] linkerFlows(double real, double[] growth, int months, boolean floor)` | What a linker pays per 100 when its first payment is on a ratio of 1 and the index then grows as given; the last month adds the face, or the indexed face when that is more (the floor) or whatever it is (no floor). |
| 483 | 5 | `static double solveReal(YieldCurve c, double[] growth, int months, boolean floor)` | The real coupon that prices that linker at 100, found from two evaluations of the present value (it is linear in the coupon). |

### MAIN (lines 489-525)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 32 | `public static void main(String[] args) throws Exception` |  |

### THE PLAYED CITY (lines 526-613)

| line | len | member | says |
|---:|---:|---|---|
| 531 | 25 | `static Game playedCity(String label)` | The city BuybackCheck was written against, with the rollover off, the dial held by hand at 3% and the world given an appetite. |
| 558 | 7 | `static void worldWantsPaper(Game city)` | The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. |
| 567 | 12 | `static Game readyCity(String label, boolean manualRollover, double centralBankShare)` | A played city ready to sell: the world wants paper, cash to spare, nothing rolled over by itself, the dial where the harness holds it. |
| 581 | 5 | `static Debt newest(DebtManager dm, Set<Debt> before)` | The piece the last call put on the books, or null. |
| 587 | 3 | `static Set<Debt> piecesOf(DebtManager dm)` |  |
| 592 | 3 | `static double askOf(Game city, double share)` | What the households could take, to size a sale that clears at fair. |
| 597 | 7 | `static FloatingRateNote fixtureFloater(Game city, double face, int months, double margin)` | A bank-held fixture floater, books opened at a sale-like price. |
| 606 | 7 | `static InflationLinkedBond fixtureLinker(Game city, double face, int months, double real, double base)` | A bank-held fixture linker, books opened at a sale-like price. |

### 1. FAIR PRICES IT AT 100 (lines 614-711)

| line | len | member | says |
|---:|---:|---|---|
| 618 | 86 | `static void fairAtPar()` |  |
| 705 | 6 | `static double maxDiff(double[] a, double[] b)` |  |

### 2. A SALE (lines 712-923)

| line | len | member | says |
|---:|---:|---|---|
| 732 | 23 | `static FloatingRateNote sellFloater(int years, double ask)` | Sells a floater by its handler and holds the piece to its quote. |
| 757 | 26 | `static InflationLinkedBond sellLinker(int years, double ask)` | Sells a linker by its handler and holds the piece to its quote. |
| 784 | 104 | `static void theSale()` |  |
| 889 | 5 | `static int countOf(DebtManager dm, Class<? extends Debt> type)` |  |
| 896 | 27 | `static void neverUnasked()` | A default city, a bill and a bond sold and played for years: not one floater or linker, though the rollover has run. |

### 3. THE FLOATER'S COUPON FOLLOWS THE DIAL (lines 924-1013)

| line | len | member | says |
|---:|---:|---|---|
| 928 | 62 | `static void followsTheDial()` |  |
| 992 | 21 | `static void aMarginUnderNothing()` | The coupon is never under nothing: a floater with a margin below it, on a dial at nothing. |

### 4. THE LINKER'S UPLIFT (lines 1014-1117)

| line | len | member | says |
|---:|---:|---|---|
| 1018 | 80 | `static void theUplift()` |  |
| 1100 | 17 | `static void anIndexThatIsNoNumber()` | A linker on its own, no city: an index that is no number books nothing, the floor is the face, the ratio is the level over the base. |

### 5. TO MATURITY (lines 1118-1148)

| line | len | member | says |
|---:|---:|---|---|
| 1127 | 6 | `static double shorten(Game g, InflationLinkedBond l, int months)` | A linker sold for real and shortened to a year after it has settled, so the households, the bank and (once its dial has bought in) the central bank hold it: its books are reopened at the price it sold at on the shorte... |
| 1134 | 11 | `static void report(String name, Books b)` |  |

### the bank-held fixtures: one city, run once, read by 3 (the margin under nothing) and 5 (the floor) (lines 1149-1358)

| line | len | member | says |
|---:|---:|---|---|
| 1165 | 45 | `static void runFixtures()` | Four fixtures built through DebtManager.addFloatingRateNote() and addLinkedBond() and opened at a price a sale would have made, the bank holding all of them, in a played city with a price index the harness drives: a f... |
| 1211 | 147 | `static void toMaturity()` |  |

### 6. THE MONEY CLOSES (lines 1359-1390)

| line | len | member | says |
|---:|---:|---|---|
| 1363 | 27 | `static void theMoneyCloses()` |  |

### 7. THE BUYBACK (lines 1391-1558)

| line | len | member | says |
|---:|---:|---|---|
| 1396 | 19 | `static double[] handPricing(DebtManager dm, Debt piece)` | The piece's payments as the market prices it, worked out here from the dial and index the manager expects. |
| 1416 | 5 | `static double tills(Game g)` |  |
| 1423 | 56 | `static Buyback.Result part(Game g, Debt piece, double face, String what)` | A part of a settled piece bought back, held to everything a part has to hold. |
| 1480 | 78 | `static void theBuyback()` |  |

### 8. THE ROLLOVER (lines 1559-1682)

| line | len | member | says |
|---:|---:|---|---|
| 1563 | 113 | `static void theRollover()` |  |
| 1678 | 4 | `static Debt dueNextMonth(Debt piece)` | A fixture that falls due at the next press: its term stays what it was, its remaining months are one. |

### 9. THE SAVE (lines 1683-1785)

| line | len | member | says |
|---:|---:|---|---|
| 1687 | 98 | `static void theSave() throws Exception` |  |

