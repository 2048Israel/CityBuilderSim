# Auction.java - 460 lines · 28 methods · 9 constants · model

`ham/citybuildersim/Auction.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The city's paper sold at auction: the bidders, each by its own rule in the
> game, and the one yield that clears them.
> 
> WHY: until CD2 the households took a share at the quoted rate and the bank
> took the rest by construction, whatever its room; nobody could say what an
> issue cost to sell. Here the city names what it offers and the highest yield
> it will take, every bidder bids by the rule it already follows elsewhere in
> the game, and the sale clears at ONE yield (D5) - the lowest at which the
> bids cover the issue. The difference between that yield and fair is the cost
> of selling, and it is a figure the screen can show before the button.
> 
> This class is the game's version of the study's auction function in
> docs/studies/city-debt/debt-model.py (spec-city-debt.md, section 4): pure,
> no Game, handed a snapshot of the bidders and the ask. The caller works out
> what the snapshot holds (the bank's room over Bank.RISK_CITY, the curve's
> fair yield with the issue on the books); nothing else in the game computes a
> clearing yield, and the screen's chart of the bids reads bidsAt() on a grid
> of yields so it draws the arithmetic that clears, not a copy of it.

**Uses:** [CapitalFlows](CapitalFlows.md) (4), [OutwardInvestment](OutwardInvestment.md) (3), [HouseholdBalance](HouseholdBalance.md) (1), [Debt](Debt.md) (1)

**Used by (25):** [AuctionCheck](AuctionCheck.md), [AuctionTally](AuctionTally.md), [AutoBuilder](AutoBuilder.md), [BuildScreen](BuildScreen.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CallTree](CallTree.md), [CapChips](CapChips.md), [CapSetting](CapSetting.md), [DataSave](DataSave.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DebtQuote](DebtQuote.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [MediumTermBond](MediumTermBond.md), [NewGameCheck](NewGameCheck.md), [RestructureCheck](RestructureCheck.md), [Rollover](Rollover.md), [SaveFileCheck](SaveFileCheck.md), [ShortTermTBill](ShortTermTBill.md)

## Sections

| line | section |
|---:|---|
| 27 | THE DIALS (spec-city-debt.md, section 9) |
| 52 | WHAT IS SOLD, WHO BIDS, WHAT COMES OUT |
| 260 | THE BIDDERS' RULES, each the rule the game already follows |
| 320 | THE AUCTION |
| 430 | THE SCHEDULES, per 100 of face |

## Enum constants

| line | constant | says |
|---:|---|---|
| 62 | `Auction.Kind.BILL` |  |
| 62 | `Auction.Kind.BOND` |  |
| 62 | `Auction.Kind.AMORTIZING` |  |
| 62 | `Auction.Kind.FLOATING` |  |
| 62 | `Auction.Kind.LINKED` |  |
| 65 | `Auction.Mode.TAIL` |  |
| 65 | `Auction.Mode.YIELD` |  |
| 65 | `Auction.Mode.FILL` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `Auction.MAX_TAIL` | `.01` | The default highest yield the city takes over fair at its own auction: one point (D27, Jerus's to move). |
| 35 | `Auction.COUPON_STEP` | `.00125` | A new bond line's coupon is the clearing yield rounded DOWN to this, an eighth of a point, so the paper sells near par (D2). |
| 38 | `Auction.BENCHMARK_LIFE` | `.9` | A bond line takes reopenings while it has this share of its term left, nine tenths (D14). |
| 41 | `Auction.BILL_MONTHS` | `{ 1, 3, 6, 12 }` | The terms a bill is sold at, in months (D3). |
| 44 | `Auction.AMORTIZING_YEARS` | `{ 5, 10, 15, 20, 25, 30 }` | The terms an amortizing bond is sold at, in years (D3). |
| 47 | `Auction.STOP_TOLERANCE` | `1e-7` | How close the bisection finds the stop, in yield: a hundredth of a basis point is 1e-6, this is a tenth of that. |
| 50 | `Auction.MAX_STEPS` | `200` | The bisection halves a gap of under a unit of yield to STOP_TOLERANCE in a few dozen steps; this is the belt over the braces. |
| 78 | `Auction.Cap.DEFAULT` | `tail(MAX_TAIL)` | The default: fair plus MAX_TAIL. |
| 81 | `Auction.Cap.FILL` | `new Cap(Mode.FILL, Double.NaN)` | No highest yield: the stop rises until the bids cover the issue, or no rule bids more. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 23 | 438 | **type** `public final class Auction` | The city's paper sold at auction: the bidders, each by its own rule in the game, and the one yield that clears them. |
| 25 | 1 | `private Auction()` |  |

### THE DIALS (spec-city-debt.md, section 9) (lines 27-51)

### WHAT IS SOLD, WHO BIDS, WHAT COMES OUT (lines 52-259)

| line | len | member | says |
|---:|---:|---|---|
| 62 | 1 | **type** `public enum Kind` | The kind of paper: a discount bill, a bullet bond with a monthly coupon, or an amortizing bond - and, at the end since CD5, the floating-rate note (a margin over the policy rate) and the inflation-linked bond (a real ... |
| 65 | 1 | **type** `public enum Mode` | How a cap on the yield is read: as a tail over fair, as a yield, or not at all. |
| 75 | 23 | **type** `public record Cap(Mode mode, double value)` | The highest yield the city will take (D20, D27): a tail over fair (fair is known only when the sale runs, so the tail is stored and resolved then), a named yield, or no highest yield at all (Fill it, Jerus, 2026-10-12... |
| 84 | 3 | `public static Cap tail(double overFair)` _(in Auction.Cap)_ | Fair plus this much, whatever fair is when the sale runs. |
| 89 | 3 | `public static Cap at(double yield)` _(in Auction.Cap)_ | This yield, and never under fair. |
| 94 | 3 | `public boolean fill()` _(in Auction.Cap)_ | True for Fill it. |
| 115 | 10 | **type** `public record Bidders(double depositRate, double worldRate, double countryPremium, double householdSpare, d...` | The bidders as they stand when the sale runs. |
| 121 | 3 | `public static Bidders none()` _(in Auction.Bidders)_ | An empty city: nobody has anything to bid with. |
| 141 | 3 | **type** `public interface Pricer` | What a schedule is worth at the sale, per 100 of face, given the tail the sale cleared at - the stop over fair, in yield. |
| 142 | 1 | `double per100(double[] flowsPer100, double tail)` _(in Auction.Pricer)_ |  |
| 150 | 3 | **type** `public interface Schedule` | A schedule per 100 of face as a function of the coupon the sale sets (CD5): what a floating-rate note or an inflation-linked bond pays on the paths the market expects, which depends on the margin or the real coupon an... |
| 151 | 1 | `double[] per100(double coupon)` _(in Auction.Schedule)_ |  |
| 169 | 44 | **type** `public record Ask(Kind kind, int months, double face, double fair, double lineCoupon, double fee, Pricer pr...` | What is for sale. |
| 173 | 3 | `public Ask(Kind kind, int months, double face, double fair, double lineCoupon, double fee, Pricer pricer)` _(in Auction.Ask)_ | An ask of the older kinds, priced as the caller says: no fair coupon, no schedule of its own (a bullet or the amortizing shape). |
| 178 | 3 | `public Ask(Kind kind, int months, double face, double fair, double lineCoupon, double fee)` _(in Auction.Ask)_ | An ask priced at one flat yield, the stop: the fixture's. |
| 191 | 6 | `public boolean equals(Object o)` _(in Auction.Ask)_ | Two asks are the same ask when they offer the same paper at the same fair yield and fee. |
| 199 | 3 | `public int hashCode()` _(in Auction.Ask)_ |  |
| 204 | 3 | `public boolean isNewLine()` _(in Auction.Ask)_ | True when this opens a line rather than reopens one. |
| 209 | 3 | `public int years()` _(in Auction.Ask)_ | The term in whole years, an amortizing bond's slices. |
| 215 | 7 | **type** `public record Fill(double households, double firms, double world, double bank)` | What each bidder takes: face in every field. |
| 218 | 3 | `public double total()` _(in Auction.Fill)_ | The four together. |
| 239 | 20 | **type** `public record Result(Ask ask, Cap cap, double ceiling, double stop, double tail, double cover, double sold,...` | The sale: the one stop, what it cost, what was sold and who took it. |
| 244 | 3 | `public boolean isEmpty()` _(in Auction.Result)_ | True when nothing was offered. |
| 249 | 3 | `public double shortBy()` _(in Auction.Result)_ | The face the city asked for and did not get. |
| 254 | 4 | `public double discountRate()` _(in Auction.Result)_ | A bill's discount rate, (100 - price) / 100 over the year: NaN for paper that is not a bill. |

### THE BIDDERS' RULES, each the rule the game already follows (lines 260-319)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 5 | `public static double householdsAt(double y, Bidders b, double face)` | The households (D32, Jerus, 2026-10-20): HouseholdBalance's rule at the auction's own yield - HOUSEHOLD_PAPER_APPETITE of the issue for each unit of yield over the deposit rate - out of savings past the cushion and ne... |
| 287 | 6 | `public static double firmsAt(double y, Bidders b)` | The businesses with idle cash (D33): OutwardInvestment's rule with the city's paper beside the deposit and the world - a share of their idle wealth, APPETITE per unit of what the paper pays over the better of the two,... |
| 302 | 7 | `public static double worldAt(double y, Bidders b)` | The world as hot money (D7): CapitalFlows' rule with the auction's yield as the city's rate - the stock it would hold at this excess return, less what is here, at ARRIVAL_SPEED. |
| 311 | 3 | `public static double bankAt(double y, Bidders b, double fair)` | The bank (spec 4.2), competitive: its whole room at fair plus what its funding needs past the curve, and nothing under that - a step, not a slope. |
| 316 | 3 | `public static Fill bidsAt(double y, Bidders b, Ask a)` | The four bids at one yield, for the paper on offer. |

### THE AUCTION (lines 320-429)

| line | len | member | says |
|---:|---:|---|---|
| 330 | 10 | `public static double ceilingOf(Cap cap, Ask a, Bidders b)` | The highest yield the cap allows, as a yield. |
| 349 | 49 | `public static Result run(Ask a, Bidders b, Cap cap)` | Sell the ask to the bidders. |
| 400 | 3 | `private static boolean covered(double y, Bidders b, Ask a)` | Do the bids at this yield cover the issue? |
| 413 | 16 | `private static double couponFor(Ask a, double stop)` | The coupon the paper carries. |

### THE SCHEDULES, per 100 of face (lines 430-460)

| line | len | member | says |
|---:|---:|---|---|
| 439 | 6 | `public static double[] bulletPer100(double coupon, int months)` | A bill or a bullet bond: the coupon a month (a bill's is nothing) and 100 at the end. |
| 447 | 13 | `public static double[] amortizingPer100(double coupon, int years)` | An amortizing bond (MediumTermBond's shape): the principal in equal yearly slices, the coupon monthly on what is left. |

