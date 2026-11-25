# BuybackCheck.java - 1,579 lines · 48 methods · 9 constants · harnesses

`ham/citybuildersim/BuybackCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The city's paper bought back in a reverse auction (CD4, spec-city-debt.md
> section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central bank
> offers its holding in slices, each at the price where its own buying rule
> would stop wanting it and never under fair, and the city takes the cheapest
> offers first, paying each its own price.
> 
> What this has to prove:
> 
> 1. The pure auction (Buyback): every offer is at or over fair; the bank's
>    are exactly fair (D34); each holder's rule, solved by the harness from
>    the model's own constants, draws the prices of its slices, dearer as it
>    sells; the central bank's face is never offered (D18); a highest price
>    takes only the cheaper offers; the price paid is each slice's own, so
>    paid is the sum of face x price, and stop >= average >= fair.
> 2. A deposit rate over the line's yield (a hiking city) makes the
>    households' and the businesses' rule price under fair, so they offer at
>    fair: caused, and asserted. A tie in price goes to the bank first.
> 3. A played city: a ten-year line sold through the auction and held, after
>    the settle, by the households, the bank and the world. Bought back in
>    HALF, the piece stays on the books with its principal, face, carrying
>    value and schedule halved and its effective rate unchanged; each holder's
>    principal fell by exactly what it sold and was paid exactly its own
>    price; the treasury's cash fell by what was paid; the gain on debt
>    retired moves by the carrying value of the face taken less the price; the
>    money audit closes that month and the next. Then the rest: the piece
>    leaves the books.
> 4. A central bank holding part of the line (its dial): asking for the whole
>    principal takes only the rest, the central bank is not paid and its
>    principal is unchanged.
> 5. No money pump (RestructureCheck's premise): sell a line, let it settle,
>    buy it all back: the city ends the round trip with less than it raised,
>    or the same, never more.
> 6. Dollar paper (D26) is the world's alone: every slice at fair, and a part
>    of it leaves the piece on the books in dollars.
> 7. Refused: a buyback the city cannot pay for, one under every price, and
>    one of a piece with a sale unsettled change nothing.
> 8. The year adds up (CD3's star 5): a line bought back at a gain, then a
>    year played - History's revenue less surplus plus the gain on debt
>    retired is the budget's expenses, month by month, and the treasury's
>    month of spending is their average; without the gain it is off by it.
> 9. A line callable now (BB1, CD4-C ★6): the city could call it at
>    CallTree.CALL_PRICE, so no holder asks more than that. By hand, a line
>    whose coupon is over what the households ask offers over the call price
>    without the cap and at most the call price with it, the offers already
>    under it unchanged, the ladder still ascending, a full buyback's stop and
>    cost within it; a line whose fair is itself over it offers at exactly the
>    call price (the cap wins over the floor at fair). In a played city, a
>    callable line before its first call month is quoted as the holders ask,
>    over the call price; at it, the same quote is the same ladder with each
>    offer at the least of what it was and the call price, and the buyback
>    booked is that quote, within what calling the line costs.
> 10. What the auction books and what fair would (BB1, CD6-C star 1): the
>    book's desk shows a line's gain at fair, the auction books it at the
>    holders' asks, and the two differ by exactly what the holders ask over
>    fair: gainAtFair less gainOnCarrying is overFair, the holders' overFair
>    add up to it, the bank's is nothing (D34), none is under nothing. The
>    case CD6 saw, caused by hand: a line the households hold, carried
>    between its worth at fair and what the auction pays, reads a gain at
>    fair and books a loss. In a played city, a line sold and settled quotes
>    the same sums.
> ... (9 more lines in the source)

**Uses:** [Buyback](Buyback.md) (199), [Game](Game.md) (31), [Debt](Debt.md) (28), [PriceLevel](PriceLevel.md) (22), [Auction](Auction.md) (19), [MoneyAudit](MoneyAudit.md) (15), [Founding](Founding.md) (13), [DebtManager](DebtManager.md) (11), [HouseholdBalance](HouseholdBalance.md) (9), [GameFiles](GameFiles.md) (6), [LongTermBond](LongTermBond.md) (5), [CapitalFlows](CapitalFlows.md) (4), [Sectors](Sectors.md) (4), [Bank](Bank.md) (4), [RestructureCheck](RestructureCheck.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [OutwardInvestment](OutwardInvestment.md) (2), [CallTree](CallTree.md) (2), [MediumTermBond](MediumTermBond.md) (2), [CentralBank](CentralBank.md) (1), [ForeignDebtCheck](ForeignDebtCheck.md) (1), [Rollover](Rollover.md) (1), [HistorySave](HistorySave.md) (1), [TempDirs](TempDirs.md) (1), [DebtQuote](DebtQuote.md) (1), [BuildingManager](BuildingManager.md) (1)

## Sections

| line | section |
|---:|---|
| 126 | THE PURE FIXTURE: a ten-year bullet at 4%, D$1,000, the market at 5% |
| 204 | 1. the pure auction |
| 374 | 2. a hiking city |
| 407 | the played city |
| 550 | 3. a played city, half of a line |
| 671 | 4. the central bank holds part |
| 716 | 5. no money pump |
| 887 | 6. dollar paper |
| 938 | 7. refused |
| 1019 | 8. the year adds up |
| 1093 | 9. a line callable now |
| 1280 | 10. what the auction books and what fair would |
| 1414 | · 5b. the serial bond, on its own terms |
| 1486 | · 5c. the bill knows its own term |
| 1523 | · 5d. yield to maturity |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 130 | `BuybackCheck.PRINCIPAL` | `1_000` |  |
| 131 | `BuybackCheck.COUPON` | `.04` |  |
| 132 | `BuybackCheck.MARKET_YIELD` | `.05` |  |
| 494 | `BuybackCheck.A_PART` | `.02` | A part of a line is a real one when it is this share of it: a fiftieth, against the world's 0.09k of 3,807k on main's path. |
| 497 | `BuybackCheck.HOUSEHOLDS_SHARE` | `.25` | The share of the sale the households take: the savers' rate is set so their bid at the stop is this much of the issue (HOUSEHOLD_PAPER_APPETITE of it for each unit over the rate). |
| 500 | `BuybackCheck.BANK_SHARE` | `.55` | The share of the sale the bank takes: its room is set to this much of the issue, the largest part, as the half of a line is the bank's at fair. |
| 1096 | `BuybackCheck.COUPON_OVER_ASKS` | `.045` | A coupon over what the households ask, and under the market's yield: the line is worth less than par and the households still ask over par. |
| 1098 | `BuybackCheck.COUPON_OVER_FAIR` | `.08` | A coupon so far over the market's yield that the line's fair is itself over the call price. |
| 1569 | `BuybackCheck.SerialTerms.DOLLAR_ASK` | `5_000 * PriceLevel.FACTOR` | What the dollar round trip borrows: small against what its city earns, so the world's premium stays low. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `static int fails` |  |
| 86 | `static PrintStream out` |  |
| 87 | `static PrintStream quiet` |  |
| 1392 | `static int fails` |  |
| 1393 | `static PrintStream out` |  |
| 1394 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 1497 | **type** `public class BuybackCheck` | The city's paper bought back in a reverse auction (CD4, spec-city-debt.md section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central bank offers its holding in slices, each at the price where its own buyin... |
| 89 | 4 | `static void check(String label, boolean ok)` |  |
| 94 | 9 | `static void near(String label, double actual, double expected, double tol)` |  |
| 104 | 4 | `static void quietly(Runnable r)` |  |
| 109 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 117 | 4 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit: the audit is returned. |
| 122 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |

### THE PURE FIXTURE: a ten-year bullet at 4%, D$1,000, the market at 5% (lines 126-203)

| line | len | member | says |
|---:|---:|---|---|
| 135 | 6 | `static double[] flows()` | The line's remaining payments, local money. |
| 143 | 3 | `static double per100(double yield)` | What the line's payments are worth per 100 at one yield: the harness's own sum, not Buyback's. |
| 147 | 3 | `static double fair()` |  |
| 152 | 3 | `static Buyback.Line line()` | The bank 400, the households 200, the businesses 100, the world 150, the central bank 150. |
| 157 | 3 | `static Auction.Bidders calm()` | A calm city: deposits at 2%, the world at 3% and half a point of premium, the businesses with idle money to spare. |
| 161 | 3 | `static Auction.Bidders bidders(double deposit, double firmsIdle, double worldStock, double monthlyGdp)` |  |
| 166 | 5 | `static List<Buyback.Offer> of(List<Buyback.Offer> ladder, Buyback.Holder holder)` | One holder's offers, in the order they come on the ladder. |
| 172 | 5 | `static double faceOf(List<Buyback.Offer> offers)` |  |
| 178 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. the pure auction (lines 204-373)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 167 | `static void purely()` |  |

### 2. a hiking city (lines 374-406)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 30 | `static void hiking()` |  |

### the played city (lines 407-549)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 5 | `static Debt unsettledPiece(DebtManager ledger)` |  |
| 416 | 4 | `static Debt sell(Game g, double ask, int years)` | A line sold through the auction: the piece the sale went into, unsettled until the next month. |
| 422 | 43 | `static Game playedCity(String label)` | The city HoldersCheck's section 11 was written against, with the world given an appetite (a tenth of the deposits, as in an early boom). |
| 467 | 5 | `static double tills(Game g)` | Every sector's till, added up. |
| 474 | 7 | `static void worldWantsPaper(Game city)` | The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. |
| 503 | 11 | `static double roomTo(Game g, int months, double faceRoom)` | Leaves the bank room for this much face of the city's paper and no more: its weighted book raised until the room the auction reads is that. |
| 522 | 21 | `static Debt sellToAllHolders(Game city, int years, boolean callable)` | A line of this many years sold so that the households, the bank and the world each take a real part, the treasury's cash as it stands. |
| 545 | 4 | `static boolean eachHoldsAPart(Debt bond)` | The households, the bank and the world each hold at least A_PART of the line. |

### 3. a played city, half of a line (lines 550-670)

| line | len | member | says |
|---:|---:|---|---|
| 552 | 47 | `static void halfOfALine(Game city)` |  |
| 605 | 65 | `static Buyback.Result part(Game city, Debt bond, double face, String what)` | A part of a settled piece bought back, and everything a part has to hold: the piece scaled, its rate where it was, each holder's principal down by what it sold, each paid its own price, the books of the holders and th... |

### 4. the central bank holds part (lines 671-715)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 42 | `static void centralBankHolds()` |  |

### 5. no money pump (lines 716-886)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 79 | `static void noPump(Game city)` |  |
| 799 | 46 | `static void typedCancellation()` | The first review's typed face: a whole-piece tolerance closes the entire unpaid sale. |
| 847 | 39 | `static void cancelReopening(Game city)` | BY1: paid holders keep their own sale proceeds while a reopening's unpaid allotment is cancelled. |

### 6. dollar paper (lines 887-937)

| line | len | member | says |
|---:|---:|---|---|
| 889 | 48 | `static void dollars() throws Exception` |  |

### 7. refused (lines 938-1018)

| line | len | member | says |
|---:|---:|---|---|
| 940 | 63 | `static void refused(Game city)` |  |
| 1005 | 6 | `static double[] snapshot(Game g, Debt d)` | Cash, the piece's principal and holders, the bank's cash and book, the households' savings and paper, the world's paper, the carried buyback, the count of pieces. |
| 1012 | 6 | `static boolean same(double[] a, double[] b)` |  |

### 8. the year adds up (lines 1019-1092)

| line | len | member | says |
|---:|---:|---|---|
| 1021 | 71 | `static void theYearAddsUp(Game city)` |  |

### 9. a line callable now (lines 1093-1279)

| line | len | member | says |
|---:|---:|---|---|
| 1101 | 7 | `static Buyback.Line lineAt(double coupon, double callPer100)` | The pure fixture's line at another coupon: ten years of months, fair at the market's yield, the call price as given (NaN: not callable now). |
| 1109 | 5 | `static void callableNow(Game city)` |  |
| 1115 | 85 | `static void callableByHand()` |  |
| 1201 | 78 | `static void callableInPlay(Game city)` |  |

### 10. what the auction books and what fair would (lines 1280-1579)

| line | len | member | says |
|---:|---:|---|---|
| 1283 | 18 | `static void figuresAdd(String what, Buyback.Result r)` | What the three figures say of one quote: the identities that hold of every result, whoever the holders. |
| 1303 | 9 | `static Buyback.Result householdsHold(double between)` | The households hold the whole line, carried at a fraction of the way from its worth at fair to what the auction pays. |
| 1313 | 76 | `static void fairAndTheAuction(Game city)` |  |
| 1391 | 188 | **type** `static final class SerialTerms` | Serial amortisation, bill terms and yields, moved intact from RestructureCheck (HA1). |
| 1399 | 6 | `static void check(String label, double actual, double expected, double tolerance)` _(in BuybackCheck.SerialTerms)_ |  |
| 1406 | 4 | `static void assertTrue(String label, boolean ok)` _(in BuybackCheck.SerialTerms)_ |  |
| 1411 | 142 | `static void run() throws Exception` _(in BuybackCheck.SerialTerms)_ |  |
| 1553 | 14 | `static Game founded(Path root, String name) throws Exception` _(in BuybackCheck.SerialTerms)_ |  |
| 1571 | 7 | `static void cleanUp(Path root)` _(in BuybackCheck.SerialTerms)_ | today's figure, priced at the founding's level (EP1) |

