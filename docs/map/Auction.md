# Auction.java - 425 lines · 26 methods · 9 constants · model

`ham/citybuildersim/Auction.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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

**Used by (19):** [AuctionCheck](AuctionCheck.md), [AuctionTally](AuctionTally.md), [AutoBuilder](AutoBuilder.md), [BuildScreen](BuildScreen.md), [CapChips](CapChips.md), [CapSetting](CapSetting.md), [DataSave](DataSave.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DebtQuote](DebtQuote.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [MediumTermBond](MediumTermBond.md), [NewGameCheck](NewGameCheck.md), [RestructureCheck](RestructureCheck.md), [Rollover](Rollover.md), [SaveFileCheck](SaveFileCheck.md), [ShortTermTBill](ShortTermTBill.md)

## Sections

| line | section |
|---:|---|
| 27 | THE DIALS (spec-city-debt.md, section 9) |
| 52 | WHAT IS SOLD, WHO BIDS, WHAT COMES OUT |
| 234 | THE BIDDERS' RULES, each the rule the game already follows |
| 294 | THE AUCTION |
| 395 | THE SCHEDULES, per 100 of face |

## Enum constants

| line | constant | says |
|---:|---|---|
| 57 | `Auction.Kind.BILL` |  |
| 57 | `Auction.Kind.BOND` |  |
| 57 | `Auction.Kind.AMORTIZING` |  |
| 60 | `Auction.Mode.TAIL` |  |
| 60 | `Auction.Mode.YIELD` |  |
| 60 | `Auction.Mode.FILL` |  |

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
| 73 | `Auction.Cap.DEFAULT` | `tail(MAX_TAIL)` | The default: fair plus MAX_TAIL. |
| 76 | `Auction.Cap.FILL` | `new Cap(Mode.FILL, Double.NaN)` | No highest yield: the stop rises until the bids cover the issue, or no rule bids more. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 23 | 403 | **type** `public final class Auction` | The city's paper sold at auction: the bidders, each by its own rule in the game, and the one yield that clears them. |
| 25 | 1 | `private Auction()` |  |

### THE DIALS (spec-city-debt.md, section 9) (lines 27-51)

### WHAT IS SOLD, WHO BIDS, WHAT COMES OUT (lines 52-233)

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1 | **type** `public enum Kind` | The kind of paper: a discount bill, a bullet bond with a monthly coupon, or an amortizing bond. |
| 60 | 1 | **type** `public enum Mode` | How a cap on the yield is read: as a tail over fair, as a yield, or not at all. |
| 70 | 23 | **type** `public record Cap(Mode mode, double value)` | The highest yield the city will take (D20, D27): a tail over fair (fair is known only when the sale runs, so the tail is stored and resolved then), a named yield, or no highest yield at all (Fill it, Jerus, 2026-10-12... |
| 79 | 3 | `public static Cap tail(double overFair)` _(in Auction.Cap)_ | Fair plus this much, whatever fair is when the sale runs. |
| 84 | 3 | `public static Cap at(double yield)` _(in Auction.Cap)_ | This yield, and never under fair. |
| 89 | 3 | `public boolean fill()` _(in Auction.Cap)_ | True for Fill it. |
| 110 | 10 | **type** `public record Bidders(double depositRate, double worldRate, double countryPremium, double householdSpare, d...` | The bidders as they stand when the sale runs. |
| 116 | 3 | `public static Bidders none()` _(in Auction.Bidders)_ | An empty city: nobody has anything to bid with. |
| 136 | 3 | **type** `public interface Pricer` | What a schedule is worth at the sale, per 100 of face, given the tail the sale cleared at - the stop over fair, in yield. |
| 137 | 1 | `double per100(double[] flowsPer100, double tail)` _(in Auction.Pricer)_ |  |
| 151 | 36 | **type** `public record Ask(Kind kind, int months, double face, double fair, double lineCoupon, double fee, Pricer pr...` | What is for sale. |
| 154 | 3 | `public Ask(Kind kind, int months, double face, double fair, double lineCoupon, double fee)` _(in Auction.Ask)_ | An ask priced at one flat yield, the stop: the fixture's. |
| 166 | 5 | `public boolean equals(Object o)` _(in Auction.Ask)_ | Two asks are the same ask when they offer the same paper at the same fair yield and fee. |
| 173 | 3 | `public int hashCode()` _(in Auction.Ask)_ |  |
| 178 | 3 | `public boolean isNewLine()` _(in Auction.Ask)_ | True when this opens a line rather than reopens one. |
| 183 | 3 | `public int years()` _(in Auction.Ask)_ | The term in whole years, an amortizing bond's slices. |
| 189 | 7 | **type** `public record Fill(double households, double firms, double world, double bank)` | What each bidder takes: face in every field. |
| 192 | 3 | `public double total()` _(in Auction.Fill)_ | The four together. |
| 213 | 20 | **type** `public record Result(Ask ask, Cap cap, double ceiling, double stop, double tail, double cover, double sold,...` | The sale: the one stop, what it cost, what was sold and who took it. |
| 218 | 3 | `public boolean isEmpty()` _(in Auction.Result)_ | True when nothing was offered. |
| 223 | 3 | `public double shortBy()` _(in Auction.Result)_ | The face the city asked for and did not get. |
| 228 | 4 | `public double discountRate()` _(in Auction.Result)_ | A bill's discount rate, (100 - price) / 100 over the year: NaN for paper that is not a bill. |

### THE BIDDERS' RULES, each the rule the game already follows (lines 234-293)

| line | len | member | says |
|---:|---:|---|---|
| 248 | 5 | `public static double householdsAt(double y, Bidders b, double face)` | The households (D32, Jerus, 2026-10-20): HouseholdBalance's rule at the auction's own yield - HOUSEHOLD_PAPER_APPETITE of the issue for each unit of yield over the deposit rate - out of savings past the cushion and ne... |
| 261 | 6 | `public static double firmsAt(double y, Bidders b)` | The businesses with idle cash (D33): OutwardInvestment's rule with the city's paper beside the deposit and the world - a share of their idle wealth, APPETITE per unit of what the paper pays over the better of the two,... |
| 276 | 7 | `public static double worldAt(double y, Bidders b)` | The world as hot money (D7): CapitalFlows' rule with the auction's yield as the city's rate - the stock it would hold at this excess return, less what is here, at ARRIVAL_SPEED. |
| 285 | 3 | `public static double bankAt(double y, Bidders b, double fair)` | The bank (spec 4.2), competitive: its whole room at fair plus what its funding needs past the curve, and nothing under that - a step, not a slope. |
| 290 | 3 | `public static Fill bidsAt(double y, Bidders b, Ask a)` | The four bids at one yield, for the paper on offer. |

### THE AUCTION (lines 294-394)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 10 | `public static double ceilingOf(Cap cap, Ask a, Bidders b)` | The highest yield the cap allows, as a yield. |
| 323 | 48 | `public static Result run(Ask a, Bidders b, Cap cap)` | Sell the ask to the bidders. |
| 373 | 3 | `private static boolean covered(double y, Bidders b, Ask a)` | Do the bids at this yield cover the issue? |
| 384 | 10 | `private static double couponFor(Ask a, double stop)` | The coupon the paper carries. |

### THE SCHEDULES, per 100 of face (lines 395-425)

| line | len | member | says |
|---:|---:|---|---|
| 404 | 6 | `public static double[] bulletPer100(double coupon, int months)` | A bill or a bullet bond: the coupon a month (a bill's is nothing) and 100 at the end. |
| 412 | 13 | `public static double[] amortizingPer100(double coupon, int years)` | An amortizing bond (MediumTermBond's shape): the principal in equal yearly slices, the coupon monthly on what is left. |

