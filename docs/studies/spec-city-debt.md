# The city's debt, priced and sold as a treasury does: a design study

*2026-10-12. A read-only design study: no game code was changed. Samuel asked for it in one message: "you wont be writing code, you will be making the finances better, right now, it should be better pricing, show actually what the coupon and ytm is as well as gain and loss if you close it now compared to market value and face value, and all that stuff like in real life, and a more better ui, and also you can redo how city loans work, do take note there is the central bank, so take that into account, talk to me also, you can ask questions, also basically design study and ui mockup then once we are done put it on github and add it to the end of the queue". He answered twelve questions the same morning (§9, marked **Jerus's call, 2026-10-12**), and on reading the study: "actually add it before research int he queue. and also i think for d20 increased yield if you allow for it to get fully filled? the rest is good" (D20 rewritten, D13 to D27 taken); then "and auto issue for building, and rollover? what happens then?", answered as D28 to D30 (§4.5).*

*What was read:*
- *The code at `4269198` (main, 0.7.135), read and not changed: `Debt`, `ShortTermTBill`, `MediumTermBond`, `LongTermBond`, `DebtManager` (the policy rate, the curve, the holders, the rate taken apart), `DebtQuote`, `Game`'s issuance (`quoteTBill`, `quoteMediumBond`, `quoteLongBond`, `quoteLongBondForCash`, `longBondCouponYield`) and buyback (`quoteRepurchase`, `repurchaseGain`, `repurchaseDebt`), `CentralBank`, `Expectations`, `CapitalFlows`, `HouseholdBalance`'s paper rule, `Bank`'s floor and room, `Exchange.SPREAD`, `Rollover`, and `ui/FinancesScreen`'s hub, The book and Borrow.*
- *The notes: `the-central-bank.md`, `the-curve-and-the-holders.md`, `debt-buyback-and-long-bond-repricing.md`, `bond-instruments-rebuilt.md`, `the-central-bank-as-backstop.md`, `the-debt-at-a-glance.md`, `the-bond-that-broke-the-bank.md`.*

*Every figure is the model in `city-debt/debt-model.py`, which copies the game's constants by name and marks the study's own as PROPOSED. The boards are `city-debt/city-debt-study.html` (the artifact "City Debt Study"), drawn from `city-debt/book-data.py`. [R#] are the sources at the end.*

---

## 0. In one page

**What is wrong today, read plainly.**
- **The coupon rate is never shown.** A piece's card shows its coupon as D$ a month; the borrow quote shows the *market* rate. For a term loan the two are far apart, because its coupon is set at a third of the market rate and the face is grossed up to make the difference: D$100M for 20 years at a quoted 4.02% books **D$149.5M of face at a 1.61% coupon** (§1.1).
- **The yield a piece shows is the rate it was handed.** Each piece is discounted at one flat rate, so the yield to maturity it prints is that rate again (§1.2).
- **The serial bond is overcharged** 7 to 24 bp, because the whole issue carries the coupon of its last slice (§1.1).
- **A buyback is a magic button.** Every holder sells at exactly market value, the gain is booked against face, and the discount amortises straight-line (§1.3).
- **The central bank moves the curve only in parallel.** The long end does not price where the dial is expected to go: hiking leaves long money dearer than it should be, and holding the dial low by hand buys cheap thirty-year money that no market would sell (§1.4).

**The proposal in one sentence.** The city sells five kinds of paper at auction into lines it reopens, prices every payment on a curve built from where the market expects the central bank to go, keeps its books by effective interest, and closes a line early by a reverse auction or a call.

**The pieces:**
- **The curve (§3).** The short rate the market expects each month is the dial drifting to what the rule would set at the inflation it expects; the long rate compounds that path, adds the term premium and the credit spread, and takes off what QE compresses. A calm city prices exactly as today. A hiking city's curve inverts. A dial held low by hand gets a steep curve.
- **The lineup (§2).** Bills at a discount (1, 3, 6, 12 months); bonds at 2, 5, 10, 20, 30 and 50 years with the market's coupon, near par; an amortizing bond with one coupon solved on the curve; floating-rate notes on the policy rate; inflation-linked bonds on the price index; any bond of ten years or more may be sold callable. The low-coupon term loan goes.
- **The auction (§4).** The bidders are the game's own rules: households non-competitive and first, the bank at fair while it has room, the world as hot money. One clearing yield; the cover and the tail say how it went. The player caps the yield, or tells it to fill: then the yield rises until the bidders' own rules cover the issue, and the card says what that costs. The central bank bids only to roll its own paper. The rollover and automatic building run the same auction on their own settings: a short rollover is paid from cash and then the central bank's advances, and a short building sale keeps its ask open until the money is there (§4.5).
- **The books (§5).** Amortised cost by effective interest: each line's carrying value, its interest expense, and a gain or loss on retirement against carrying value, on the statements.
- **Closing early (§6).** A reverse auction: holders offer, each at the price where its own buying rule stops wanting the bond, never under fair; the city takes the cheapest and pays each its own price. A callable line can be called at 100 from its first call date; the card says when calling is worth it.
- **The screens (§7).** The book as a desk, a line's own page, the buyback and the call, Borrow as an auction, and Your rate as the curve the central bank sets.

**Six batches (§8), CD1 to CD6,** in the queue before research (Jerus): the curve, the lineup and the auction, the books, closing early, the floating and linked notes, the screens.

---

## 1. What the game does today

### 1.1 Three instruments, each priced its own way

| kind | today | what it is in real life |
|---|---|---|
| **Note** (`ShortTermTBill`) | 3 to 12 months, no coupon, the face grossed up for the discount on its own term at the short end of the curve | a treasury bill; right as it is |
| **Serial bond** (`MediumTermBond`) | 1 to 10 years, issued at par, principal in equal yearly slices, coupon **at the curve's rate for its final maturity** (`quoteMediumBond()`) | an amortizing bond, but every slice is charged the longest slice's rate |
| **Term loan** (`LongTermBond`) | 10 to 50 years, a bullet; coupon `marketRate / 3 + 0.00667 x years / (years + 30)` (`longBondCouponYield()`), and the face solved so the payments are worth the cash at the market rate | a deep-discount bond; no treasury sells one, and the player sees only the market rate |

The serial's coupon, against the one coupon that prices its schedule at 100 on the curve, in a calm city (the dial at 3%):

| serial | coupon today | the coupon that prices it at par | overcharge |
|---|---|---|---|
| 5 years | 3.339% | 3.266% | 7 bp |
| 10 years | 3.591% | 3.434% | 16 bp |
| 20 years | 3.913% | 3.683% | 23 bp |
| 30 years | 4.083% | 3.846% | 24 bp |

The term loan, D$100M for 20 years in the same city: the quote reads **4.02% a year**. The coupon is **1.61%**, the face **D$149.5M**, and the bill D$200k a month, against D$335k for a par bond. The 0.7.1 note called that the instrument's character. It also means the coupon is not a number the player ever sees, and the debt-to-GDP measure carries D$49.5M more face than the city raised.

**Fees.** An underwriter's spread of 0.75% of face and a fixed cost, off the proceeds, on every issue (`Game.UNDERWRITING_SPREAD`).

**Who buys.** The households up to half of an issue by their spread over the deposit rate, the bank the rest by construction, and the central bank an add-on rolling its own maturing paper (`CentralBank.buyAtIssue()`).

### 1.2 One flat rate per piece, so the yield is circular

`DebtManager.marketValue()` discounts every remaining payment of a piece at one rate, the curve at its months left (`valuationRate()`), and The book prints `getCurrentYield(rate)`: the yield to maturity at that price. Solving a schedule for the yield that gives back the price it was priced at returns the rate it was priced at. The book's "yields" column is the curve, read at each piece's months; it says nothing about the piece.

Real pricing discounts each payment at its own month's rate (the zero curve), and the yield to maturity is then a result: two bonds of the same maturity with different coupons have different yields, and a bond's yield moves differently from the curve at its date.

### 1.3 A buyback is a button, and its gain is against face

`Game.repurchaseDebt()` retires a whole piece at `marketValue()`, paying every holder its share pro rata: the households, the bank and the central bank all sell at exactly market value, whatever they want. `repurchaseGain()` is principal less price, against face. A term loan's discount accretes straight-line over the months left (`Debt.accrete()`), and a dollar piece's not at all. The 0.7.1 money pump (issue, buy back, repeat) was closed by pricing the issue and the buyback on the same function, which is why the round trip is exactly neutral today.

### 1.4 The curve sits on the dial, in parallel

`DebtManager.priceAt(debt, months)`: the floor (the dial, or the bank's cost of funds if higher) plus two credit spreads, plus the term premium for the months, less QE's compression. Nothing in it reads where the dial is going. The four cities the study uses, zero rates by maturity:

| city | 1y | 2y | 5y | 10y | 20y | 30y | 50y |
|---|---|---|---|---|---|---|---|
| **calm**: on target, the dial at neutral (3%) | 3.12 | 3.18 | 3.35 | 3.62 | 4.02 | 4.27 | 4.62 |
| **hiking**: 6% inflation, the dial at 7%, today | 7.12 | 7.18 | 7.35 | 7.62 | 8.02 | 8.27 | 8.62 |
| **behind**: 5% inflation, the dial held at 1% by hand, today | 1.12 | 1.18 | 1.35 | 1.62 | 2.02 | 2.27 | 2.62 |

The behind city sells fifty-year money at 2.62% while prices rise 5% a year and the rule says 7.5%. A market lending at that rate is lending at minus 2.4% real for fifty years on a promise nobody made.

### 1.5 What The book and Borrow show

A piece's card (`FinancesScreen.pieceCard()`): its name and date, its face, a life bar, "coupon D$X a month", who holds it, "worth today D$X · Y of par · yields Z", "clears D$F for D$MV · D$U under face", and Buy back. The quote card shows the market rate as "X% a year". Nowhere: the coupon rate, the yield it was sold at, its carrying value, or what closing it would book.

---

## 2. The lineup

### 2.1 Bills

1, 3, 6 and 12 months, sold at a discount, no coupon. Each bill is its own line. The card shows three figures: the price per 100, the **discount rate** (what a bill is quoted at: 100 less the price, a year) and the **yield** (what the money does, compounded monthly). Calm city: a 3-month bill at 99.223, discount 3.108%, yield 3.124%. A bill is the dial's instrument, and the one the rollover sells by default.

### 2.2 Bonds, in lines

2, 5, 10, 20, 30 and 50 years (**Jerus's call**: "2, 5, 10, 20, 30, 50"; 40 goes). A bond pays a fixed coupon every month and its face at the end.

- **The coupon is the market's** (**Jerus's call**): the auction's clearing yield rounded **down** to an eighth of a point, as the US Treasury sets it [R1], so a new line sells just under 100. Calm city: a 10-year at 3½%, price 99.233, yield 3.591%; a 30-year at 4%, price 98.534, yield 4.083%.
- **Lines are reopened** (**Jerus's call**). A line is the benchmark for its term while it has at least nine tenths of the term left (D14: 22 months of a 2-year, 54 of a 5-year, 108 of a 10-year, 216, 324 and 540); a sale at that term goes into it at its coupon and date, at whatever price the clearing yield makes. Past that, a sale opens a new line. A 10-year sold every quarter makes one line a year, not four pieces.
- **A line is named as a treasury names it**: coupon and date, "3½% Jan 2211". A reopened line is one row; its page lists its sales.

### 2.3 The amortizing bond

5 to 30 years in fives. Principal in equal yearly slices as the serial today, the coupon monthly on what is left, but **one coupon solved so the whole schedule prices at 100 on the curve** (the table in §1.1). Each sale is its own line; an amortizing line is not reopened, because its slices have already started to fall.

### 2.4 Floating-rate notes

2 and 5 years (D25). The coupon each month is **the month's policy rate plus a margin** fixed at the sale; the margin is what prices the note at 100 on the curve: the term premium and the credit spread the note's life carries, and the gap between the expected dial and the floor. Calm city: policy + 18 bp at 2 years, + 34 bp at 5. The price stays near 100, moving only with the credit spread and the premium; the city carries the dial's risk instead of the lender. The US Treasury has sold floaters on its own bill rate since 2014 [R2]; the index here is the dial, which is what the player moves (**Jerus's call**: "floating-rate notes on the policy rate").

### 2.5 Inflation-linked bonds

10 and 30 years (D25). The principal rises each month with the city's price index (the one `Expectations` reads inflation from); the coupon is a **real** coupon on the indexed principal; the face at the end is the indexed principal. Calm city: a real 1.61% at 10 years, 2.17% at 30, against the 2% a year the market expects.

They are what a central bank that is believed less than it deserves sells. In the behind city (credibility 60%) a nominal 10-year yields 5.28% because the market expects 3.39% inflation a year; a linked 10-year pays a real 1.92% plus the inflation the city actually has. A city that brings inflation back to its 2% target pays about 1.4 points a year less on the linked line. A city that inflates pays more. Canada sold Real Return Bonds from 1991 to 2022, the US sells TIPS [R3].

**The deflation floor** (D25, as TIPS): the face at the end is never under the original 100.

### 2.6 Callable bonds

Any bond of 10 years or more may be sold callable (**Jerus's call**: "market + callable"). D22: first call at 5 years for a 10-year, 10 years for 20, 30 and 50; at 100, then any month. A call is a right the city buys at the sale, so its coupon carries it: priced on a Ho–Lee short-rate tree fitted to the curve [R4], monthly, at a volatility measured from the city's own history of its 1-year rate and never under 0.75 points a year (D23: `RATE_VOL_FLOOR`).

| calm city | non-call coupon | callable | the option |
|---|---|---|---|
| 10-year, call from year 5, vol 0.75 | 3.591% | 3.935% | +34 bp |
| 30-year, call from year 10, vol 0.75 | 4.083% | 4.666% | +58 bp |
| 10-year, vol 1.00 | 3.591% | 4.082% | +49 bp |
| 30-year, vol 1.00 | 4.083% | 4.902% | +82 bp |

The tree prices the non-call bond at 100.000 on the same curve, which is the check that the tree is the curve. US municipal bonds are typically callable at par after ten years [R5].

### 2.7 Dollar paper

Bills and bonds only (no floaters or linked bonds abroad), priced on the world's curve as today (`foreignCurveRate()`), each payment discounted on its month as at home. Sold to the world, not at an auction: the world's lending is already priced by country risk and closes by its window (`foreignWindowOpen()`). They get the book's figures, effective interest and the buyback (the world the only holder) like any line.

### 2.8 What goes

`LongTermBond`, the term loan, and `MediumTermBond`'s final-maturity coupon. Old saves are refused (the standing rule: `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` raised together).

---

## 3. The curve

### 3.1 The dial the market expects

Month t ahead (t = 0 today):

    expected inflation  π(t) = π∞ + (π now − π∞) · e^(−t / ADAPT_MONTHS)
                        π∞   = credibility · target + (1 − credibility) · π now
    the rule            R(t) = DebtManager.ruleRate(π(t))        (Standard, inside the dial's bounds)
    the dial expected   p(t) = R(t) + (floor now − R(0)) · e^(−t / DRIFT_MONTHS)

- **π(t)** is `Expectations`' own rule run forward: the inflation the market expects settles at the target weighted by credibility and recent experience for the rest, over `ADAPT_MONTHS` (12), the months it smooths over.
- **The dial drifts to the rule, whoever holds it** (**Jerus's call**). Today's gap between the floor (`floorRate()`: the dial, or the bank's money if dearer) and what the rule would set closes with a time constant of **`DRIFT_MONTHS` = 6** (D15). On autopilot the gap is nothing and the path is the rule's.
- **No new modifier.** Every input is a figure the game already has: the target, credibility, smoothed inflation, the rule and its weight.

### 3.2 From the path to a price

    forward f(t) = max(MIN_RATE, p(t) + credit spread)
    zero z(m)    = the compound of f over months 0..m−1, as a rate a year
                   + termPremium(m) − compression(m)
    discount d(m) = (1 + z(m) / 12)^(−m)

The credit spread, the term premium table and QE's compression are today's, unchanged. **Every payment is discounted at its own month's factor.** A line's price is the sum of its payments times the factors; its yield to maturity is solved from that price and is a result, not an input.

### 3.3 The four cities

| city | 1m | 1y | 2y | 5y | 10y | 20y | 30y | 50y |
|---|---|---|---|---|---|---|---|---|
| calm, the dial at neutral | 3.12 | 3.12 | 3.18 | 3.35 | 3.62 | 4.02 | 4.27 | 4.62 |
| hiking: 6% inflation, the dial at 7%, credibility 65% | 7.12 | 6.85 | 6.50 | 6.04 | 6.02 | 6.27 | 6.47 | 6.78 |
| behind: 5% inflation, the dial at 1% by hand, credibility 60% | 1.12 | 3.65 | 4.46 | 5.00 | 5.35 | 5.79 | 6.05 | 6.41 |
| calm, the central bank holding a quarter of the term paper | 3.12 | 3.12 | 3.15 | 3.24 | 3.37 | 3.57 | 3.70 | 3.87 |

- **Calm prices exactly as today.** On target with the dial at neutral, the path is flat and the curve is today's to the hundredth.
- **Hiking inverts.** The rule says 9%, so the market expects a little more, then cuts as inflation falls; it expects inflation to settle at 3.4%, not 2%, because credibility is 65%. The 10-year is 6.02%, 1.6 points under today's 7.62%.
- **Behind is steep.** The dial is expected back to the rule's 7.5% within a year or two. The 10-year is 5.35%, not 1.62%: holding the dial down by hand no longer buys cheap long money.
- **QE bends the long end** exactly as today.

### 3.4 What else reads the curve

`BondMarket` takes the city's curve as the benchmark under every corporate bond (`curve(months)`), so the businesses' long yields invert and steepen with it. `getRate()` stays the short end (the 1-month), which hot money and the advisor read. The rate taken apart on Your rate becomes: the dial expected, averaged over the term; the credit spread; the term premium; less QE.

---

## 4. The auction

### 4.1 What the player does

Borrow › At home: the kind of paper, the term (each column showing today's fair yield), callable or not, how much face, and **the highest yield the city will take**: fair + `MAX_TAIL` (one point) by default, any other cap, or **Fill it**, no cap at all (§4.3). The preview is the auction run on the city as it stands, with the issue priced into the credit spread, so what it shows is what pressing the button books: `DebtQuote`'s rule, kept.

### 4.2 The bidders, each by its own rule in the game

- **Households, non-competitive and first.** What `HouseholdBalance`'s rule takes at fair (½ of the issue at most, 20 × its spread over the deposit rate, out of savings past the cushion), filled in full at the clearing yield before anyone else. A real treasury fills retail's non-competitive bids first [R1]; it is "the households first" the game has now. (D13.)
- **The bank, competitive, at fair while it has room.** It bids its room (`Bank.capacity()` less its weighted book, over `RISK_CITY`) at fair, plus whatever its funding costs past the floor the curve already carries (its cost of funds plus `MIN_MARGIN` over the dial, fading as the dial's gap does). That extra is nothing in the usual case.
- **The world, as hot money** (**Jerus's call**). `CapitalFlows`' rule: its target stock at the auction's yield over the world's rate, less what is already here, at `ARRIVAL_SPEED`.
- **Firms with idle cash**, by `OutwardInvestment`'s rule, the city's paper beside the deposit and abroad (left out of the study's figures; CD2 adds it beside the others).
- **The central bank: never**, except its non-competitive add-on rolling its own maturing paper, as today (**Jerus's call**: arm's length). The Bank of Canada buys at Government of Canada auctions the same way and for the same reason [R6].

**Nobody bids under fair**, because the paper trades at fair the next month and any bidder could buy it there.

### 4.3 How it clears

Single price (**Jerus's call**: "one clearing yield"): the stop-out is the lowest yield at which the households' share and the competitive bids cover the issue, never under fair and never over the highest yield taken; the competitive bids at the stop are filled pro rata.
- **The tail** is the stop-out over fair.
- **The cover** is what was bid up to the highest yield taken, each bidder counted at most the issue, over the issue.
- **Capped:** when the bids do not reach the issue at the highest yield taken, it sells only what was bid. The rest is not raised; if the month needed it, it lands on the central bank's advances as any shortfall does today.
- **Fill it** (**Jerus's call, 2026-10-12**: "increased yield if you allow for it to get fully filled"): the stop-out rises until the bids cover the issue. Nothing new decides how far: every bidder's own rule already wants more at a higher yield (the world's target stock grows with the excess return, up to `CapitalFlows.MAX_SPREAD`), and the bank's room and the households' half do not. Past the world's ceiling no rule adds anything, so only an issue bigger than every rule takes at any yield still sells short, and the card says so before the button.

| 10-year, D$300M | fair | stop | tail | cover | coupon | price | raised | households | bank | world |
|---|---|---|---|---|---|---|---|---|---|---|
| calm | 3.602% | 3.602% | 0 | 1.49× | 3½% | 99.148 | D$297.4M | 126.1M | 173.6M | 0.3M |
| hiking | 6.049% | 6.049% | 0 | 1.49× | 6% | 99.630 | D$298.9M | 93.0M | 205.4M | 1.7M |
| calm, the bank's room nearly gone, capped at fair + 1 | 3.602% | 4.602% | 100 bp | 0.83× | 4½% | 99.187 | D$245.8M of D$300M | 126.1M | 100.0M | 21.7M |
| the same, **filled** | 3.602% | 8.632% | 503 bp | - | 8⅝% | 99.956 | D$299.9M | 126.1M | 100.0M | 73.9M |

Filling it in that city costs D$25.9M a year of interest at its effective rate, against D$10.8M at fair: D$15.1M a year for ten years, paid to the hot money that was pulled in to cover what the bank could not hold. On the boards' hiking city a filled reopening of the 6% line stops at 11.23% (a tail of 519 bp) and raises D$208.6M for D$300M of face.

### 4.4 Fees

An auction has no underwriter, so **the 0.75% spread goes**; the fixed cost and the minimum issue stay (D21). The cost of selling is now the tail, which the city can see and cap.

### 4.5 When nobody presses the button: the rollover, automatic building, the build screen

Three things sell paper on the city's behalf, and each runs the same auction on the city as it stands. Jerus, 2026-10-12, asked what happens to them; his answers are D28 to D30.

**Each has its own cap or Fill it** (D28, **Jerus's call**: "each its own"). The rollover's card on the Finances hub and automatic building's panel each carry the same chips as Borrow: fair + 1 point (the default), another cap, or Fill it. The time skip runs both on their own settings, month after month, and its report lists every automatic sale: the stop, the tail, anything short and where the rest came from.

**The rollover.**
- By hand as today. "Same structure" sells each maturing piece into the line that is now the benchmark for its term, a bill for a bill and an amortizing piece into a new amortizing line of its remaining years; "12-month bills" as today. The central bank still rolls its own par at issue, outside the auction, and last year's surplus still nets first (`Rollover`'s rule, unchanged).
- **Capped and short** (D29, **Jerus's call**): the maturity is a promise and is paid in full. What the sale did not raise comes out of the treasury's cash, and past the cash from the central bank's advances, past their ceiling if it must, as any promise is paid today (`TreasuryLine`). That is printing, and the inbox says so: "The rollover's 10-year sale covered D$X at its cap of Y%; D$Z of January's maturity was paid by the central bank's advances."
- **Filled:** it pays the tail instead, and the inbox names the tail when it passes the cap the player would otherwise have set.

**Automatic building** (D30, **Jerus's call**: "stays open the ask until money raised, and what already raised plus cash can be used").
- It borrows the gap on the 20-year line (`BUILD_BOND_YEARS`, today's 20-year term bond), at its own cap or filled.
- **Capped and short:** it sells what was bid, and **the rest of the ask stays open**. The order is placed as soon as what has been raised for it plus the cash over its reserve covers it, that month or a later one. Each month the open ask is offered again at the month's auction, at the builder's cap, until it is covered or no longer needed.
- **What is raised is the order's.** The automatic builder counts it against the order and spends it on nothing else; the treasury's other bills may still draw on it if the month needs the cash, as any cash. If the order drops out of the ranking or the debt limit closes, the ask is withdrawn and what was raised stays in the treasury: the paper is sold and cannot be unsold, so the log and the inbox say so.
- The panel lists the open asks: the order, raised so far, still to raise, the months open, and a Withdraw button.
- Nothing is ever borrowed for an order that is then refused: the order waits for the money, not the reverse.

**The build screen's funding card** becomes the auction's preview for a 6-month bill (`BUILD_NOTE_MONTHS`) or the 20-year line, sized so the cash that arrives covers the order. The player is there, so a short preview offers the three answers the automatic sales use: raise the cap or Fill it, keep the ask open as automatic building does, or not borrow.

---

## 5. The books

### 5.1 Amortised cost, by effective interest

**Jerus's call** ("effective interest"). At the sale a line is carried at what it raised, net of the fixed cost. Every month:

    interest expense  = carrying value × effective rate / 12
    coupon paid       = face × coupon / 12
    amortised         = expense − coupon          (the discount wearing off; negative for a premium)
    carrying value   += amortised

The effective rate is the yield that equates what was raised with the line's payments. At the end the carrying value is the face. This is IFRS 9's amortised cost [R7] and ASPE 3856's.

The calm 10-year, D$300M of 3½% sold at 99.148 for D$297.4M, effective 3.602%:

| month | expense | coupon | amortised | carrying |
|---|---|---|---|---|
| 1 | D$893k | D$875k | D$18k | D$297.4M |
| 24 | D$894k | D$875k | D$19k | D$297.9M |
| 60 | D$896k | D$875k | D$21k | D$298.6M |
| 119 | D$900k | D$875k | D$25k | D$300.0M, and the face repaid the next month |

### 5.2 A reopened line

One carrying value and one effective rate a line (D16): at a reopening, the line's carrying value plus the new sale's net proceeds, against the line's remaining payments on its whole face, gives the blended rate. Fungible paper is one liability; a ledger of tranches per line would add rows nobody reads. The line's page lists its sales.

### 5.3 A linked bond and a floater

- **Linked:** the month's indexation of the principal is an expense, "inflation uplift on linked bonds", beside the real coupon; the carrying value is the indexed principal less what is left of any discount.
- **Floating:** the expense is the month's coupon plus the fixed cost amortised; its effective rate resets with the coupon, as a floating liability's does.

### 5.4 On the statements

- **The budget:** "Interest on the city's paper" is the expense above, coupons and amortisation together; "Inflation uplift on linked bonds"; "Gain (loss) on debt retired", below the operating lines.
- **The balance:** the city's paper at face, less unamortised discount (plus premium) = carrying value; and, as a note, **its fair value**, which a government discloses and does not book [R7].

---

## 6. Closing early

### 6.1 Three ways out

Hold it to its date; buy it back in a **reverse auction** (any line, **Jerus's call**); or **call** it (a callable line, from its first call date).

### 6.2 The reverse auction

The city names how much face of a line it wants back and, if it likes, the most it will pay per 100. Every holder offers what it holds in slices, **each slice at the price where its own buying rule would stop wanting it, and never under fair**:
- **Households:** they hold the line because it out-yields a deposit by the spread their rule asked for. A slice's price is the line's payments discounted at the deposit rate plus the spread their remaining share of the line would still ask (share ÷ `HOUSEHOLD_PAPER_APPETITE`). The more they sell, the dearer the next slice.
- **The world:** likewise from `CapitalFlows`' rule, at the world's rate plus the excess its remaining stock still asks.
- **The bank:** it bought at fair; it offers at **fair plus half the desk's spread** (`Exchange.SPREAD` ÷ 2, 1%), as it quotes a share (D17).
- **The central bank does not offer** (D18): its holdings follow its own dial, and a buyback is not its decision.

The city takes the cheapest offers up to the face asked and **pays each its own price** (multiple price, as treasury buybacks are run [R8][R9]; D19). The stop is the dearest offer taken.

The 3½% 2211, D$100M, two years on:

| city two years on | fair | offered | stop | average | paid | taken |
|---|---|---|---|---|---|---|
| calm (yield 3.49%) | 100.054 | 2.85× | 101.055 | 101.055 | D$101.1M | the bank 100.0M |
| hiking (yield 6.00%) | 84.123 | 2.85× | 84.964 | 84.930 | D$84.9M | the bank 91.0M, households 9.0M |

In the hiking city the households' first slices come in under the bank's ask: their deposits now pay 4.5%, so the line's spread is worth less to them.

### 6.3 What the city books, and what it costs

Every buyback shows three comparisons and books one:

| hiking, D$100M of the 3½% 2211 | |
|---|---|
| **against carrying value: the statement's "Gain (loss) on debt retired"** | **+D$14.4M** |
| against face: what the debt-to-GDP measure sees | +D$15.1M |
| over fair: what the auction cost | D$0.8M |

The journal: Dr City paper payable D$100.0M; Cr Unamortised discount D$0.7M; Cr Cash D$84.9M; Cr Gain on debt retired D$14.4M.

**The honest line beside it.** The buyback stops D$3.5M a year of coupon; the D$84.9M it pays costs D$5.1M a year borrowed again at today's 6.04% 10-year. It books a gain and raises the city's interest by D$1.6M a year. A buyback at fair moves nothing in present value; it changes when the city pays and what its measures say. The screen says both.

**No money pump.** Sell a 10-year line and buy it all straight back: the calm city loses D$13.7M on D$300M, the hiking city D$8.3M. The holders' offers sit at or over fair and the sale's price at or under it, so the round trip is neutral or worse by construction, `RestructureCheck`'s premise kept.

### 6.4 The call

From its first call date, a callable line can be called whole at 100 (D22: whole lines, any month after the date). The gain or loss is carrying value less 100, small for a line sold near par. **Worth calling** appears on the line when the present value saved, the line's remaining payments at today's curve less 100 less a new sale's cost, passes **3% of face** (D24), the threshold US municipal refunding practice uses [R5].

---

## 7. The screens

The boards in `city-debt/city-debt-study.html`, one city in January 2203, eighteen months into a hike:

1. **Finances › The book, as a desk.** Five figures: owed at face (D$2.31B), carried (D$2.29B), worth today (D$2.05B), the coupon a month, and what a point on the curve moves (D$140.3M). The curve with every line on it as a dot. Face, carried and worth as three bars by kind. Then the desk: one row a line, by when it falls due: its name ("3½% Jan 2211"), face, coupon, issue yield, price, yield, worth, carried, **if closed now** (carried less worth) and **vs face**, duration and holders. No colour on a gain or a loss (the 0.7.32 spec's B6, kept).
2. **A line's page.** Its five figures (coupon, issue yield, price and yield, worth, carried); its price since the sale against its carrying value per 100; **if you closed it today**, with the honest line; the next six months of effective interest; who holds it; its sales, duration and the value of a point.
3. **Close it now.** The offers as a ladder, cheapest first, coloured by holder, the ask and the stop marked; the ticket (face, the most to pay, offered, taken from, stop and average, the price); the journal and the three comparisons; and the call card on a callable line.
4. **Borrow, as an auction.** The five kinds as tiles, the terms as columns of today's fair yield, the callable switch with what it would add to the coupon, the face, the highest yield; the bids against yield with the issue and the highest yield drawn; the result line by line (stop and tail, cover, coupon and price, each bidder's fill, the effective rate, what is raised); and the red card for an auction that would not cover.
5. **Your rate, as the curve.** Today's curve, a year ago, two years ago, the world's, and the dial the market expects; what the market expects the dial to do (today, the rule, a year, five years, where it settles); the 10-year rate taken apart; real rates and the inflation priced, from the linked bonds.

---

## 8. Batches

| Batch | What | Traces |
|---|---|---|
| **CD1 ★** | **The curve** (§3): the expected path from `Expectations` and the rule, discount factors month by month, every piece priced on its own schedule and its yield solved from the price; `BondMarket`'s benchmark reads it; the rate taken apart in its new parts. A `CurveCheck`: a fixture that causes each shape (calm equal to today's curve, hiking inverted, behind steep, QE), asserted against `DRIFT_MONTHS`, `ADAPT_MONTHS` and the rule's own constants; `RestructureCheck` neutral or worse | every trace that borrows moves; re-baseline, the ensemble |
| **CD2 ★** | **The lineup and the auction** (§2.1–2.3, 2.7, §4): bills, bonds at the six terms in lines that reopen, the amortizing bond on one solved coupon; the auction with the game's own bidders, non-competitive households, single price, cover, tail, the highest yield or Fill it, a capped sale that falls short; the fees; `DebtQuote` as the auction's preview; the rollover, automatic building, the build screen and the time skip on the new paper, each with its own cap or Fill it, the rollover's shortfall to the advances and automatic building's open ask (§4.5); the term loan and the old serial removed; `SAVE_FORMAT` and its floor raised. An `AuctionCheck` | moves |
| **CD3** | **The books** (§5): effective interest a line, blended at a reopening; carrying value; the budget's interest line, the gain or loss on retirement, the balance at carrying value and the fair-value note; dollar paper amortised too. `TreasuryCheck` and `MoneyCheck` extended | moves (the interest line) |
| **CD4** | **Closing early** (§6, §2.6): the reverse auction (offers by each holder's own rule, multiple price, the most to pay, the central bank out), callable lines on a Ho–Lee tree at the measured volatility, the call, "worth calling"; `repurchaseDebt()` replaced. `RestructureCheck`'s round trip on the new mechanism, its premise unchanged | identical unless a playtest buys back |
| **CD5** | **Floating and linked** (§2.4, 2.5, 5.3): the floating-rate note on the dial, the linked bond on the price index with its deflation floor and its uplift line | identical (nothing sells them unasked) |
| **CD6** | **The screens** (§7): the book as a desk, a line's page, Close it now, Borrow as an auction, Your rate; the manual | identical to CD5 |

In `QUEUE.md` before R0, the research batches (Jerus, 2026-10-12: "add it before research"). CD1 can go alone and changes how every price of money in the city reads the dial. CD2 is the heart and depends on CD1. CD3 and CD4 follow CD2 in either order; CD5 after CD3. CD6 last, or each screen with its batch if the implementer prefers.

---


### 8.1 Current-code handoff for CD1 (2026-10-13)

**Source checked:** `Expectations`, `DebtManager`, `CentralBank`, `BondMarket`, `Rollover`, `Debt`, `DebtQuote`, both bond classes, `Game`, `DataSave`, `HistorySave`, `MoneyAudit`; `docs/month-order.md` and the named checks. **Inferences/design calls are marked below.**

- **Expectations:** `takeMonth()` smooths inflation over `ADAPT_MONTHS`, updates credibility/lean, expected inflation and its compounded price level; `strikeLevel()` supplies the level struck at the *top* of the next month. Its 8-slot state is `toSaveArray()/restore()` via `DataSave.expectations`. Today the debt curve does **not** read an expected path from it.
- **Dial/rule:** `DebtManager.setPolicyRate()/getPolicyRate()` own the nominal dial; `takeTheDial()` returns control to the player; `advisedPolicyRate()` and `ruleRate()` implement the bounded rule. In `Game.nextMonth()`, autopilot sets the dial before pricing (month-order line 46); `Expectations.takeMonth()` runs near the end (line 135), so its new expectations are not an input to prices earlier in that same month.
- **Today's curve:** `floorRate()` blends the policy rate with the bank's cost-of-funds floor by the central bank's *actual* share; `priceAt(debt, months)` adds the two debt/tax stress spreads, clamps credit, then adds `termShape(months)`; `curveRate(months)` prices the current balance sheet. This is a parallel-shift curve, not the proposed expected-dial path. `quoteRate(..., months)` iterates a fixed point with the proposed borrowing included.
- **Prices and instruments:** `DebtManager.marketValue()/valuationRate()` choose the city's curve or `foreignCurveRate()` at months remaining. `Debt.getMarketValue()` discounts the whole `remainingCashFlows()` schedule at **one flat annual rate**; `getYieldToMaturity(price)` bisects the rate that reproduces that price. `LongTermBond` pays a low monthly coupon plus bullet principal; `MediumTermBond` pays on outstanding principal and amortizes in annual slices. `DebtQuote` carries the quote's `marketRate`, face, proceeds and payments; `Game.quoteTBill()`, `quoteMediumBond()`, `quoteLongBond()` and the `...ForCash()` variants build it, and booking is meant to use that same quote.
- **Other readers:** `BondMarket.Readings.curve(months)` takes `DebtManager.curveRate()` as the corporate-bond benchmark; `getRate()` remains the short end used by hot-money/advisor paths. The central bank's reserve, window and advances prices use the policy dial directly, not the treasury term curve. Do not silently replace every rate getter with a long-end or expected-path rate.
- **QE, rollover, retirement:** `CentralBank.setTargetShare()/stepFor()` set and pace holdings; `buyPaper()/sellPaper()` create/destroy money at the transaction price while holdings are carried at face. `Rollover` plans maturities/netting and saves its ledger/record; `Game.rollMaturities()` books quotes before the month advances. `quoteRepurchase()` uses `marketValue()`; `repurchaseGain()` is outstanding principal minus price; `repurchaseDebt()` pays holders and defers the central-bank share's redemption/gain to `settleRedemptions()`, saved in the central-bank state.
- **Traps:** repricing a curve is a valuation change, not cash; QE purchases/sales and buybacks *are* money flows and must keep payer/payee and the MoneyAudit declaration aligned. Issue pricing and retirement pricing must share the same discount-path convention or `RestructureCheck`'s no-money-pump round trip can fail. A new flow needed next month must be saved and restored, not reconstructed from end-state. Foreign debt uses a separate world curve; `Debt.accrete()` deliberately skips its discount today.
- **Save decision:** current policy/expectations, debt instruments, central-bank holdings and rollover state already have save/load paths. **Inference:** if CD1 derives the path entirely from those saved inputs, no format bump follows merely from changing the valuation formula. If the implementation adds required path state or makes old fields load with a different meaning, bump `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` together. CD2's removal/re-shaping of instruments is a separate, already-specified format change.
- **Harness plan (fixtures must cause each case):** **new `CurveCheck`:** calm/on-target autopilot; high inflation with a hiking dial; high inflation with the dial manually held low; QE at `FULL_COMPRESSION_SHARE`. Assert the path against `Expectations.ADAPT_MONTHS`, CD1's `DRIFT_MONTHS`, the rule and QE constants. **Pricing:** a bullet and a serial with different coupons/schedules, plus a foreign piece; assert each schedule's curve-discounted price and that solved YTM reproduces it within tolerance. **Issue/retire:** quote and book each domestic instrument, then run the existing round-trip fixture; assert no pricing-created money and audit closure. **Save/month boundary:** save-load a city with a manual dial, expectations, QE holdings and outstanding pieces; assert identical derived curve/prices, and that the end-of-month expectation update only affects the next pricing pass.
- **Existing coverage:** `ExpectationsCheck` covers expectation recurrences and save/load; `CentralBankCheck` covers QE/M0 flows and its save; `MoneyCheck` audits whole-city conservation; `MonetaryCheck` checks macro rate/inflation response. `BondCheck` is for **corporate** bonds, not the treasury's `Debt` instruments; `RestructureCheck` is the essential issue/repurchase regression. Extend these where appropriate rather than treating their names as proof of CD1 coverage.
- **Jerus's design calls:** preserve the short end for the policy dial and direct central-bank facilities; decide how the expected path is converted into a zero/discount curve and how credit/QE components are applied to it. The spec's `DRIFT_MONTHS = 6` is the proposed design, not current code; `ADAPT_MONTHS = 12` is `Expectations`' own, already in the code (correction by Claude's review).
- **Answered (Jerus, 2026-10-18: "if it is realistic then yes"):** §3.2 as written. Each month's forward is the expected dial plus today's credit spread, compounded into a zero rate; the term premium and QE's compression go on the zero rate; every payment is discounted at its own month's factor, and the yield is solved from the price. It is the standard construction (expected short rates plus a term premium, which QE presses down), so it is realistic. The policy dial and the central bank's facilities keep the short end.

## 9. ★ Decisions

**Answered, 2026-10-12**
1. **D1. The lineup:** bills, bonds, amortizing, floating-rate notes on the policy rate, inflation-linked bonds. **Jerus's call** ("Lineup + extras").
2. **D2. The coupon is the market's,** the clearing yield rounded down to an eighth, near par. **Jerus's call.**
3. **D3. Terms:** bonds 2, 5, 10, 20, 30, 50; bills 1, 3, 6, 12 months; amortizing 5 to 30 years. **Jerus's call.**
4. **D4. Coupons are paid monthly.** **Jerus's call:** "monthly, i know unrealistic but makes the game alot more coherent". So no accrued interest and one price.
5. **D5. Sold by auction,** one clearing yield. **Jerus's call.**
6. **D6. Lines are reopened.** **Jerus's call.**
7. **D7. The world bids as hot money.** **Jerus's call.**
8. **D8. The central bank at arm's length:** it never bids except to roll its own; QE in the market and the advances ceiling as today. **Jerus's call.**
9. **D9. The market expects the dial to drift to the rule, whoever holds it.** **Jerus's call.**
10. **D10. Effective interest.** **Jerus's call.**
11. **D11. Closing early: buy back or call.** **Jerus's call.**
12. **D12. The buyback is a reverse auction.** **Jerus's call.**

**Taken, 2026-10-12** (Jerus: "the rest is good"; D20 his own)
13. **D13. Households bid non-competitively,** filled first at the clearing yield. **Taken:** it is how retail bids and keeps "the households first".
14. **D14. A line is the benchmark while it has nine tenths of its term left.** **Taken.**
15. **D15. `DRIFT_MONTHS` = 6,** the time constant of the dial's expected return to the rule. **Taken at that value; still his to move.**
16. **D16. One carrying value and one blended effective rate a line.** **Taken** (§5.2).
17. **D17. In a buyback, holders offer where their own rule stops wanting the bond, never under fair; the bank at fair plus half `Exchange.SPREAD`.** **Taken.** The alternative is a fixed premium per holder, which is a coefficient laid on top.
18. **D18. The central bank does not offer in a buyback.** **Taken.**
19. **D19. Multiple price in a buyback** (each offer paid its own price). **Taken**, as the US Treasury and the Bank of Canada run them.
20. **D20. The player caps the auction's yield or fills it.** **Jerus's call:** "increased yield if you allow for it to get fully filled". Capped, a short auction sells what was bid; filled, the yield rises until the bidders' own rules cover it (§4.3). Not the bank taking the rest at any yield: its room is its capital.
21. **D21. The underwriter's 0.75% goes; the fixed cost and the minimum issue stay.** **Taken.**
22. **D22. Callable terms:** first call at 5 years for a 10-year and 10 for longer, at 100, whole lines. **Taken.**
23. **D23. The call's volatility** is measured from the city's own 1-year rate, never under 0.75 points. **Taken**; `RATE_VOL_FLOOR` a number to settle.
24. **D24. "Worth calling" at 3% of face.** **Taken.**
25. **D25. Floating notes at 2 and 5 years; linked bonds at 10 and 30, with the deflation floor.** **Taken.**
26. **D26. Dollar paper** keeps the world's pricing and its window, sold to the world, but gets the book's figures, effective interest and the buyback. **Taken.**
27. **D27. `MAX_TAIL` = one point,** the default highest yield over fair. **Taken at that value; still his to move.**

**The sales nobody presses, 2026-10-12** (§4.5)
28. **D28. The rollover and automatic building each have their own cap or Fill it.** **Jerus's call:** "each its own" (not one setting shared). Default fair + 1 point.
29. **D29. A capped rollover that falls short pays the rest of the maturity from cash, then the central bank's advances.** **Jerus's call.** The maturity is a promise; the inbox names what was printed for it.
30. **D30. A capped automatic-building sale that falls short keeps its ask open** until what was raised plus the spare cash covers the order, offered again each month. **Jerus's call:** "stays open the ask until money raised, and what already raised plus cash can be used". The build screen offers the same choice when the player is there.

---

## 10. Found on the way

- **QE at half the term paper erases the whole term premium** (`FULL_COMPRESSION_SHARE` .5, `QE_COMPRESSION` 1): a calm city whose central bank holds half its term paper has a flat curve to fifty years. Unchanged by this study; noted because Your rate will now show it plainly.
- **The credit spread is still close to nothing** for a city at 63% of GDP (about 0.1 points), because `FULL_STRESS_MULTIPLE` is 150 years (`the-bond-that-broke-the-bank.md`). It stays Jerus's earlier "gentler"; the taken-apart bar will show how small it is, and the auction's tails come from the bank's room instead.
- **The bank carries the city's paper at what it paid** (`bankBook()`). When rates rise its book hides the loss a market price would show. A study of the bank's own books would take it up; the city's side is this one.
- **The households' "the spread is gone" sale still never fires** (`the-curve-and-the-holders.md` §4). The reverse auction gives them a reason to sell that does fire.
- **A dollar piece's discount never accretes** (`Debt.accrete()` returns nothing for foreign paper). CD3 amortises it like any line.

---

## Sources

- **[R1]** US Treasury, *Uniform Offering Circular* (31 CFR Part 356): single-price auctions; a note's or bond's interest rate set at the high yield rounded down to the nearest one-eighth of one percent; non-competitive bids accepted in full first, at the high yield.
- **[R2]** US Treasury, Floating Rate Notes, first auctioned January 2014, indexed to the 13-week bill's high rate.
- **[R3]** Government of Canada, Real Return Bonds (1991; issuance ended November 2022); US Treasury Inflation-Protected Securities and their principal floor at maturity.
- **[R4]** T. S. Y. Ho and S.-B. Lee (1986), "Term structure movements and pricing interest rate contingent claims", *Journal of Finance* 41(5).
- **[R5]** Government Finance Officers Association, *Refunding Municipal Bonds* (best practice): typical ten-year par calls, and present-value savings thresholds of 3 to 5% of the refunded bonds.
- **[R6]** Bank of Canada, its purchases of Government of Canada bonds at auction on a non-competitive basis to replace maturing holdings.
- **[R7]** IFRS 9 *Financial Instruments*, §5.4.1 and B5.4 (the effective interest method), §3.3.3 (the gain or loss on extinguishing a liability); CPA Canada Handbook, ASPE Section 3856; IFRS 13 and IFRS 7 on fair-value disclosure.
- **[R8]** US Treasury, the buyback programme (from 2024): reverse auctions, multiple-price.
- **[R9]** Bank of Canada, Government of Canada bond buybacks (cash-management and switch buybacks) by reverse auction.
