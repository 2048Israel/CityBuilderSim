# BuybackCheck.java - 845 lines · 31 methods · 3 constants · harnesses

`ham/citybuildersim/BuybackCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

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
> 
> Each fixture causes its condition rather than finding a city in it.

**Uses:** [Buyback](Buyback.md) (112), [Game](Game.md) (17), [Debt](Debt.md) (15), [HouseholdBalance](HouseholdBalance.md) (9), [Founding](Founding.md) (9), [Auction](Auction.md) (8), [DebtManager](DebtManager.md) (8), [MoneyAudit](MoneyAudit.md) (4), [CapitalFlows](CapitalFlows.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [OutwardInvestment](OutwardInvestment.md) (2), [GameFiles](GameFiles.md) (2), [Sectors](Sectors.md) (2), [Bank](Bank.md) (2), [CentralBank](CentralBank.md) (1), [TempDirs](TempDirs.md) (1), [ForeignDebtCheck](ForeignDebtCheck.md) (1), [RestructureCheck](RestructureCheck.md) (1), [Rollover](Rollover.md) (1), [HistorySave](HistorySave.md) (1)

## Sections

| line | section |
|---:|---|
| 98 | THE PURE FIXTURE: a ten-year bullet at 4%, D$1,000, the market at 5% |
| 169 | 1. the pure auction |
| 339 | 2. a hiking city |
| 372 | the played city |
| 425 | 3. a played city, half of a line |
| 545 | 4. the central bank holds part |
| 588 | 5. no money pump |
| 659 | 6. dollar paper |
| 708 | 7. refused |
| 781 | 8. the year adds up |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 102 | `BuybackCheck.PRINCIPAL` | `1_000` |  |
| 103 | `BuybackCheck.COUPON` | `.04` |  |
| 104 | `BuybackCheck.MARKET_YIELD` | `.05` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 58 | `static PrintStream out` |  |
| 59 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 791 | **type** `public class BuybackCheck` | The city's paper bought back in a reverse auction (CD4, spec-city-debt.md section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central bank offers its holding in slices, each at the price where its own buyin... |
| 61 | 4 | `static void check(String label, boolean ok)` |  |
| 66 | 9 | `static void near(String label, double actual, double expected, double tol)` |  |
| 76 | 4 | `static void quietly(Runnable r)` |  |
| 81 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 89 | 4 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit: the audit is returned. |
| 94 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |

### THE PURE FIXTURE: a ten-year bullet at 4%, D$1,000, the market at 5% (lines 98-168)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 6 | `static double[] flows()` | The line's remaining payments, local money. |
| 115 | 3 | `static double per100(double yield)` | What the line's payments are worth per 100 at one yield: the harness's own sum, not Buyback's. |
| 119 | 3 | `static double fair()` |  |
| 124 | 3 | `static Buyback.Line line()` | The bank 400, the households 200, the businesses 100, the world 150, the central bank 150. |
| 129 | 3 | `static Auction.Bidders calm()` | A calm city: deposits at 2%, the world at 3% and half a point of premium, the businesses with idle money to spare. |
| 133 | 3 | `static Auction.Bidders bidders(double deposit, double firmsIdle, double worldStock, double monthlyGdp)` |  |
| 138 | 5 | `static List<Buyback.Offer> of(List<Buyback.Offer> ladder, Buyback.Holder holder)` | One holder's offers, in the order they come on the ladder. |
| 144 | 5 | `static double faceOf(List<Buyback.Offer> offers)` |  |
| 150 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. the pure auction (lines 169-338)

| line | len | member | says |
|---:|---:|---|---|
| 171 | 167 | `static void purely()` |  |

### 2. a hiking city (lines 339-371)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 30 | `static void hiking()` |  |

### the played city (lines 372-424)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 5 | `static Debt unsettledPiece(DebtManager ledger)` |  |
| 381 | 4 | `static Debt sell(Game g, double ask, int years)` | A line sold through the auction: the piece the sale went into, unsettled until the next month. |
| 387 | 21 | `static Game playedCity(String label)` | The city HoldersCheck's section 11 was written against, with the world given an appetite (a tenth of the deposits, as in an early boom). |
| 410 | 5 | `static double tills(Game g)` | Every sector's till, added up. |
| 417 | 7 | `static void worldWantsPaper(Game city)` | The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. |

### 3. a played city, half of a line (lines 425-544)

| line | len | member | says |
|---:|---:|---|---|
| 427 | 46 | `static void halfOfALine(Game city)` |  |
| 479 | 65 | `static Buyback.Result part(Game city, Debt bond, double face, String what)` | A part of a settled piece bought back, and everything a part has to hold: the piece scaled, its rate where it was, each holder's principal down by what it sold, each paid its own price, the books of the holders and th... |

### 4. the central bank holds part (lines 545-587)

| line | len | member | says |
|---:|---:|---|---|
| 547 | 40 | `static void centralBankHolds()` |  |

### 5. no money pump (lines 588-658)

| line | len | member | says |
|---:|---:|---|---|
| 590 | 68 | `static void noPump(Game city)` |  |

### 6. dollar paper (lines 659-707)

| line | len | member | says |
|---:|---:|---|---|
| 661 | 46 | `static void dollars() throws Exception` |  |

### 7. refused (lines 708-780)

| line | len | member | says |
|---:|---:|---|---|
| 710 | 55 | `static void refused(Game city)` |  |
| 767 | 6 | `static double[] snapshot(Game g, Debt d)` | Cash, the piece's principal and holders, the bank's cash and book, the households' savings and paper, the world's paper, the carried buyback, the count of pieces. |
| 774 | 6 | `static boolean same(double[] a, double[] b)` |  |

### 8. the year adds up (lines 781-845)

| line | len | member | says |
|---:|---:|---|---|
| 783 | 62 | `static void theYearAddsUp(Game city)` |  |

