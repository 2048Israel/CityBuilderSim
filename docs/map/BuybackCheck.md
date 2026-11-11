# BuybackCheck.java - 1,190 lines · 38 methods · 5 constants · harnesses

`ham/citybuildersim/BuybackCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

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
> ... (2 more lines in the source)

**Uses:** [Buyback](Buyback.md) (190), [Game](Game.md) (20), [Debt](Debt.md) (18), [PriceLevel](PriceLevel.md) (14), [HouseholdBalance](HouseholdBalance.md) (12), [Founding](Founding.md) (12), [Auction](Auction.md) (11), [DebtManager](DebtManager.md) (11), [MoneyAudit](MoneyAudit.md) (6), [CapitalFlows](CapitalFlows.md) (4), [Sectors](Sectors.md) (4), [LongTermBond](LongTermBond.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [OutwardInvestment](OutwardInvestment.md) (2), [GameFiles](GameFiles.md) (2), [Bank](Bank.md) (2), [CallTree](CallTree.md) (2), [CentralBank](CentralBank.md) (1), [TempDirs](TempDirs.md) (1), [ForeignDebtCheck](ForeignDebtCheck.md) (1), [RestructureCheck](RestructureCheck.md) (1), [Rollover](Rollover.md) (1), [HistorySave](HistorySave.md) (1)

## Sections

| line | section |
|---:|---|
| 118 | THE PURE FIXTURE: a ten-year bullet at 4%, D$1,000, the market at 5% |
| 191 | 1. the pure auction |
| 361 | 2. a hiking city |
| 394 | the played city |
| 464 | 3. a played city, half of a line |
| 584 | 4. the central bank holds part |
| 627 | 5. no money pump |
| 698 | 6. dollar paper |
| 747 | 7. refused |
| 820 | 8. the year adds up |
| 894 | 9. a line callable now |
| 1082 | 10. what the auction books and what fair would |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 122 | `BuybackCheck.PRINCIPAL` | `1_000` |  |
| 123 | `BuybackCheck.COUPON` | `.04` |  |
| 124 | `BuybackCheck.MARKET_YIELD` | `.05` |  |
| 897 | `BuybackCheck.COUPON_OVER_ASKS` | `.045` | A coupon over what the households ask, and under the market's yield: the line is worth less than par and the households still ask over par. |
| 899 | `BuybackCheck.COUPON_OVER_FAIR` | `.08` | A coupon so far over the market's yield that the line's fair is itself over the call price. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static PrintStream out` |  |
| 79 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1116 | **type** `public class BuybackCheck` | The city's paper bought back in a reverse auction (CD4, spec-city-debt.md section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central bank offers its holding in slices, each at the price where its own buyin... |
| 81 | 4 | `static void check(String label, boolean ok)` |  |
| 86 | 9 | `static void near(String label, double actual, double expected, double tol)` |  |
| 96 | 4 | `static void quietly(Runnable r)` |  |
| 101 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 109 | 4 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit: the audit is returned. |
| 114 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |

### THE PURE FIXTURE: a ten-year bullet at 4%, D$1,000, the market at 5% (lines 118-190)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 6 | `static double[] flows()` | The line's remaining payments, local money. |
| 135 | 3 | `static double per100(double yield)` | What the line's payments are worth per 100 at one yield: the harness's own sum, not Buyback's. |
| 139 | 3 | `static double fair()` |  |
| 144 | 3 | `static Buyback.Line line()` | The bank 400, the households 200, the businesses 100, the world 150, the central bank 150. |
| 149 | 3 | `static Auction.Bidders calm()` | A calm city: deposits at 2%, the world at 3% and half a point of premium, the businesses with idle money to spare. |
| 153 | 3 | `static Auction.Bidders bidders(double deposit, double firmsIdle, double worldStock, double monthlyGdp)` |  |
| 158 | 5 | `static List<Buyback.Offer> of(List<Buyback.Offer> ladder, Buyback.Holder holder)` | One holder's offers, in the order they come on the ladder. |
| 164 | 5 | `static double faceOf(List<Buyback.Offer> offers)` |  |
| 170 | 20 | `public static void main(String[] args) throws Exception` |  |

### 1. the pure auction (lines 191-360)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 167 | `static void purely()` |  |

### 2. a hiking city (lines 361-393)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 30 | `static void hiking()` |  |

### the played city (lines 394-463)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 5 | `static Debt unsettledPiece(DebtManager ledger)` |  |
| 403 | 4 | `static Debt sell(Game g, double ask, int years)` | A line sold through the auction: the piece the sale went into, unsettled until the next month. |
| 409 | 38 | `static Game playedCity(String label)` | The city HoldersCheck's section 11 was written against, with the world given an appetite (a tenth of the deposits, as in an early boom). |
| 449 | 5 | `static double tills(Game g)` | Every sector's till, added up. |
| 456 | 7 | `static void worldWantsPaper(Game city)` | The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. |

### 3. a played city, half of a line (lines 464-583)

| line | len | member | says |
|---:|---:|---|---|
| 466 | 46 | `static void halfOfALine(Game city)` |  |
| 518 | 65 | `static Buyback.Result part(Game city, Debt bond, double face, String what)` | A part of a settled piece bought back, and everything a part has to hold: the piece scaled, its rate where it was, each holder's principal down by what it sold, each paid its own price, the books of the holders and th... |

### 4. the central bank holds part (lines 584-626)

| line | len | member | says |
|---:|---:|---|---|
| 586 | 40 | `static void centralBankHolds()` |  |

### 5. no money pump (lines 627-697)

| line | len | member | says |
|---:|---:|---|---|
| 629 | 68 | `static void noPump(Game city)` |  |

### 6. dollar paper (lines 698-746)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 46 | `static void dollars() throws Exception` |  |

### 7. refused (lines 747-819)

| line | len | member | says |
|---:|---:|---|---|
| 749 | 55 | `static void refused(Game city)` |  |
| 806 | 6 | `static double[] snapshot(Game g, Debt d)` | Cash, the piece's principal and holders, the bank's cash and book, the households' savings and paper, the world's paper, the carried buyback, the count of pieces. |
| 813 | 6 | `static boolean same(double[] a, double[] b)` |  |

### 8. the year adds up (lines 820-893)

| line | len | member | says |
|---:|---:|---|---|
| 822 | 71 | `static void theYearAddsUp(Game city)` |  |

### 9. a line callable now (lines 894-1081)

| line | len | member | says |
|---:|---:|---|---|
| 902 | 7 | `static Buyback.Line lineAt(double coupon, double callPer100)` | The pure fixture's line at another coupon: ten years of months, fair at the market's yield, the call price as given (NaN: not callable now). |
| 910 | 5 | `static void callableNow(Game city)` |  |
| 916 | 85 | `static void callableByHand()` |  |
| 1002 | 79 | `static void callableInPlay(Game city)` |  |

### 10. what the auction books and what fair would (lines 1082-1190)

| line | len | member | says |
|---:|---:|---|---|
| 1085 | 18 | `static void figuresAdd(String what, Buyback.Result r)` | What the three figures say of one quote: the identities that hold of every result, whoever the holders. |
| 1105 | 9 | `static Buyback.Result householdsHold(double between)` | The households hold the whole line, carried at a fraction of the way from its worth at fair to what the auction pays. |
| 1115 | 75 | `static void fairAndTheAuction(Game city)` |  |

