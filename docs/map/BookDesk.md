# BookDesk.java - 184 lines · 20 methods · 0 constants · model

`ham/citybuildersim/BookDesk.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The debt book as a desk: every figure the book page and a line's page draw (CD6,
> spec-city-debt.md section 7, boards 1 and 2), a pure model of what the screen shows,
> so the screen computes nothing.
> 
> WHY THIS EXISTS. The debt tab worked out its own figures from the pieces - the face,
> the worth, the carrying value, the premium or discount of each - and a figure the
> screen works out is one the model never checks (CLAUDE.md: every model figure through
> a public getter). The desk is those figures, one line a piece by when it falls due,
> the totals the ledger already gives, and the totals by kind, each read from the
> ledger's own getters.
> 
> A pure read: {@link #of(DebtManager)} strikes nothing. It prices the market as it was
> last struck, so the screen strikes it ONCE for the whole desk (Game.bookDesk()), not
> once a line, and the month's recorder (DebtManager.recordTheBook()) never does.

**Uses:** [DebtManager](DebtManager.md) (10), [Debt](Debt.md) (3), [InflationLinkedBond](InflationLinkedBond.md) (2), [LongTermBond](LongTermBond.md) (1), [MediumTermBond](MediumTermBond.md) (1), [FloatingRateNote](FloatingRateNote.md) (1)

**Used by (5):** [DebtExchangeCheck](DebtExchangeCheck.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [ReadPathCheck](ReadPathCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `private final List<Line> lines` |  |
| 79 | `private final double face, carried, worth, coupon, valueOfAPoint` |  |
| 81 | `private final double exchangeSinceSold, foreignCarried` | CD7: the lines' exchange since the sale added up, and the carrying value of the foreign ones, which the share is over. |
| 82 | `private final double[] faceByKind` |  |
| 83 | `private final double[] carriedByKind` |  |
| 84 | `private final double[] worthByKind` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 160 | **type** `public final class BookDesk` | The debt book as a desk: every figure the book page and a line's page draw (CD6, spec-city-debt.md section 7, boards 1 and 2), a pure model of what the screen shows, so the screen computes nothing. |
| 46 | 31 | **type** `public record Line(Debt debt, double face, double coupon, double couponRate, double bookedRate, double pric...` | One line of the desk: a piece of paper and the figures the screen prints, in local money. |
| 52 | 3 | `public double carriedPer100()` _(in BookDesk.Line)_ | What it is carried at for each 100 of what is still owed on it (CD6): the carrying value over the face, times 100; 0 for a line that owes nothing. |
| 62 | 3 | `public double ifClosedNowShare()` _(in BookDesk.Line)_ | What closing the line now would book as a share of what the books carry it at (CD6, Jerus 2026-10-23): the gain {@link #ifClosedNow()} over the carrying value, a fraction (0.338 is a gain of 33.8 per cent of what is c... |
| 73 | 3 | `public double exchangeShare()` _(in BookDesk.Line)_ | What the exchange has done to what is carried since the sale as a share of what the books carry it at (CD7): the {@link #exchangeSinceSold()} over the carrying value, a fraction (-0.10 is a D$ that has lost 10 per cen... |
| 86 | 25 | `private BookDesk(DebtManager ledger)` |  |
| 113 | 3 | `public static BookDesk of(DebtManager ledger)` | The desk on the ledger's market as it stands: every piece a line, sorted by when it falls due. |
| 118 | 24 | `public static Line line(DebtManager ledger, Debt debt)` | One piece's line, for its own page. |
| 144 | 1 | `public List<Line> lines()` | Every line, by when it falls due (the fewest months left first). |
| 147 | 1 | `public double face()` | What the city owes at face: DebtManager.getAllPrincipal(). |
| 150 | 1 | `public double carried()` | ...what the books carry it at: DebtManager.getCarryingValue(). |
| 153 | 1 | `public double worth()` | ...what it would cost to buy back today: DebtManager.getTotalMarketValue(). |
| 156 | 1 | `public double coupon()` | ...the cash it pays a month in coupon: DebtManager.getMonthlyCoupon(). |
| 159 | 1 | `public double valueOfAPoint()` | ...what a point on the curve takes off its worth: the lines' summed. |
| 162 | 1 | `public double ifClosedNow()` | ...the carrying value less the worth: what closing the whole book now would book as a gain on the statement (below nothing, a loss). |
| 165 | 1 | `public double ifClosedNowShare()` | ...that gain as a share of the carrying value, a fraction (CD6, Jerus 2026-10-23): the book's {@link #ifClosedNow()} over {@link #carried()}; 0 for a book carried at nothing. |
| 168 | 1 | `public double vsFace()` | ...the face less the worth: what the market's price saves against what is owed. |
| 171 | 1 | `public double exchangeSinceSold()` | ...what the D$'s move since the sales has done to what the dollar lines carry (CD7): the lines' {@link Line#exchangeSinceSold()} added up, a gain positive; 0 with no dollar paper. |
| 174 | 1 | `public double exchangeShare()` | ...that as a share of the dollar lines' carrying value, a fraction (CD7): {@link #exchangeSinceSold()} over what the foreign lines are carried at; 0 with none. |
| 177 | 1 | `public double faceOf(String kind)` | The face of one kind of paper (a DebtManager.BOOK_KINDS entry): the bars by kind. |
| 180 | 1 | `public double carriedOf(String kind)` | ...its carrying value. |
| 183 | 1 | `public double worthOf(String kind)` | ...and its worth. |

