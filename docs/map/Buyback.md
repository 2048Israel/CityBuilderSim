# Buyback.java - 408 lines · 27 methods · 1 constants · model

`ham/citybuildersim/Buyback.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The city's paper bought back in a reverse auction: every holder offers what
> it holds in slices, each at the price where its own buying rule would stop
> wanting it and never under fair, and the city takes the cheapest offers
> first, paying each its own price.
> 
> WHY: until CD4 a buyback was a button that cleared a whole piece at one
> price, fair, and paid every holder its share of it pro rata - the households
> who held it for the spread they asked, the world that held it for its excess
> return, were bought out at the same number as the bank. Nobody sells a
> bond they are glad to hold for what it is worth to somebody who does not
> care. Here the city names how much face of ONE piece it wants and the
> most it will pay per 100, each holder offers by the rule it already follows
> when it BUYS (Auction's bidders' rules, read the other way round), and what
> the city pays is the sum of what the offers it took asked: the stop is the
> dearest of them, and what it cost over fair is a figure the screen can show
> before the button (spec-city-debt.md, section 6.2-6.3; D12, D17, D18, D19,
> D26, D34).
> 
> This class is pure, as Auction is: no Game, handed a snapshot of the piece
> (a {@link Line}) and of the bidders (Auction.Bidders, the same record the
> auction reads, so the buyback and the sale see the same city). The caller
> works out what the snapshot holds - the piece's remaining payments, its
> fair price off the curve, who holds how much - and pays the result holder by
> holder; nothing here moves money.
> 
> DOLLAR PAPER (D26): its only holder is the world. It offers the whole piece,
> every slice at fair - the dollar curve's value, which
> DebtManager.marketValue() already returns for it - because the world's rule
> for the city's own paper (CapitalFlows' excess-return appetite) is not the
> rule that holds a dollar bond.

**Uses:** [Auction](Auction.md) (7), [OutwardInvestment](OutwardInvestment.md) (2), [CapitalFlows](CapitalFlows.md) (2), [Debt](Debt.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

**Used by (9):** [BuybackCheck](BuybackCheck.md), [CentralBankCheck](CentralBankCheck.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 42 | THE DIAL |
| 49 | WHAT IS ON OFFER, WHAT IS ASKED, WHAT COMES OUT |
| 240 | EACH HOLDER'S OFFERS: its buying rule, read the other way round |
| 365 | THE REVERSE AUCTION |

## Enum constants

| line | constant | says |
|---:|---|---|
| 54 | `Buyback.Holder.BANK` |  |
| 54 | `Buyback.Holder.HOUSEHOLDS` |  |
| 54 | `Buyback.Holder.FIRMS` |  |
| 54 | `Buyback.Holder.WORLD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `Buyback.SLICES` | `10` | How many equal slices each holder cuts its holding into: the more there are, the finer the ladder of prices the holder's rule draws (D12). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 371 | **type** `public final class Buyback` | The city's paper bought back in a reverse auction: every holder offers what it holds in slices, each at the price where its own buying rule would stop wanting it and never under fair, and the city takes the cheapest o... |
| 40 | 1 | `private Buyback()` |  |

### THE DIAL (lines 42-48)

### WHAT IS ON OFFER, WHAT IS ASKED, WHAT COMES OUT (lines 49-239)

| line | len | member | says |
|---:|---:|---|---|
| 54 | 1 | **type** `public enum Holder` | The holders who offer, in the order a tie in price goes to them (D19): the bank first, the world last. |
| 63 | 1 | **type** `public record Offer(Holder holder, double face, double per100)` | One slice on the ladder. |
| 71 | 12 | **type** `public record Ask(double face, double maxPer100)` | What the city asks for. |
| 74 | 3 | `public static Ask anyPrice(double face)` _(in Buyback.Ask)_ | This much face at any price. |
| 79 | 3 | `public boolean capped()` _(in Buyback.Ask)_ | True when the ask names a highest price. |
| 102 | 23 | **type** `public record Line(double principal, double[] flows, double fairPer100, double carryingValue, double bankFa...` | The piece as it stands when the buyback runs. |
| 107 | 6 | `public Line(double principal, double[] flows, double fairPer100, double carryingValue, double bankFace, double householdFace, d...` _(in Buyback.Line)_ | A piece the city cannot call now: the ten figures that came before the call price (BB1), callPer100 NaN. |
| 115 | 3 | `public boolean callableNow()` _(in Buyback.Line)_ | True when the city may call the piece now, at a price above nothing (BB1). |
| 120 | 4 | `public double offerable()` _(in Buyback.Line)_ | The face that can be offered: everything but the central bank's. |
| 142 | 80 | **type** `public record Result(List<Offer> ladder, double asked, double offeredFace, double takenFace, double paid, d...` | The buyback: the ladder, what was taken from whom at what, and the three comparisons the city books (spec 6.3). |
| 148 | 3 | `public static Result none(double asked)` _(in Buyback.Result)_ | A buyback that took nothing: no ladder, nothing asked of the books. |
| 153 | 3 | `public double cover()` _(in Buyback.Result)_ | What was offered over what was asked: 1 or more when the ask can be filled, under 1 when it cannot. |
| 158 | 3 | `public double gainOnCarrying()` _(in Buyback.Result)_ | The statement's "Gain (loss) on debt retired": the carrying value of the face taken less what was paid (CD3). |
| 163 | 3 | `public double gainOnCarryingShare()` _(in Buyback.Result)_ | That gain as a share of the carrying value of the face taken, a fraction (CD6, Jerus 2026-10-23: the book is green or red, in D$ and per cent); 0 when nothing carried was taken. |
| 172 | 3 | `public double unamortisedTaken()` _(in Buyback.Result)_ | The premium still on the books for the face taken (CD6): the carrying value of the face taken less the face - the journal's "Unamortised premium", which the buyback takes off with the paper; below nothing it is the di... |
| 177 | 3 | `public double gainOnFace()` _(in Buyback.Result)_ | What the debt-to-GDP measure sees: the face taken less what was paid. |
| 182 | 3 | `public double overFair()` _(in Buyback.Result)_ | What the auction cost: what was paid over what the same face is worth at fair. |
| 190 | 3 | `public double gainAtFair()` _(in Buyback.Result)_ | What the same face taken would book at fair (BB1, Jerus CD6-C ★1): its carrying value less its worth at fair, the book's desk figure; the auction books gainOnCarrying(), which is this less overFair(). |
| 195 | 3 | `public double gainAtFairShare()` _(in Buyback.Result)_ | That gain at fair as a share of the carrying value of the face taken, a fraction (BB1); 0 when nothing carried was taken. |
| 200 | 8 | `public double overFair(Holder h)` _(in Buyback.Result)_ | What one holder is paid over fair for the face taken from it: its paid less its face at fair (BB1; the bank's is nothing, D34). |
| 210 | 11 | `public List<Offer> taken()` _(in Buyback.Result)_ | The offers that were taken, in the order they were: the ladder up to the stop, the last one in part when the ask ran out. |
| 232 | 7 | **type** `public record HonestLine(int months, double rate, double couponStopped, double costAgain)` | THE HONEST LINE's four figures (CD6; spec-city-debt section 6.3): a buyback's gain is real on the statement, but the cash to pay for it costs today's rate. |
| 235 | 3 | `public double change()` _(in Buyback.HonestLine)_ | What the city's interest does a month if it borrows the price again: above nothing it rises, below it falls. |

### EACH HOLDER'S OFFERS: its buying rule, read the other way round (lines 240-364)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 4 | `static double per100At(Line line, double yield)` | The piece's remaining payments discounted at one flat yield, per 100 of principal: Debt.presentValue's convention, the one every price in the game uses. |
| 269 | 4 | `public static double householdsYield(double remaining, Line line, Auction.Bidders b)` | The households' yield for a holding h left: the deposit rate plus the spread their rule asks to hold that share of the line - Auction.householdsAt solved for the yield, HOUSEHOLD_PAPER_APPETITE of the line for each un... |
| 281 | 6 | `public static double firmsYield(double remaining, Auction.Bidders b)` | The businesses' yield for a holding f left: the better of the deposit and the world, plus the spread that holds f of their idle wealth - Auction.firmsAt solved for the yield. |
| 294 | 6 | `public static double worldYield(double stockLeft, Auction.Bidders b)` | The world's yield for a stock s left: its rate plus the country's premium plus the excess return that holds s - CapitalFlows' appetite solved for the yield, never past MAX_SPREAD. |
| 302 | 5 | `private static double priceAt(Line line, double yield)` | A slice's price: the piece's payments at the yield its holder would still hold at, never under fair. |
| 309 | 33 | `public static List<Offer> offers(Line line, Auction.Bidders bidders)` | The ladder: every holder's slices, the cheapest first, ties to the bank, the households, the businesses, the world (D19). |
| 344 | 3 | **type** `private interface SlicePrice` | How a holder prices a slice: given the holding that REMAINS once the slice is sold. |
| 345 | 1 | `double of(double remaining)` _(in Buyback.SlicePrice)_ |  |
| 353 | 11 | `private static void slices(List<Offer> ladder, Holder holder, double holding, SlicePrice price, double cap)` | Cuts one holder's holding into SLICES equal slices, each priced at the holding that remains once it is sold, never under the slice before it and never over cap (the call price of a piece the city can call now, infinit... |

### THE REVERSE AUCTION (lines 365-408)

| line | len | member | says |
|---:|---:|---|---|
| 375 | 33 | `public static Result run(Line line, Auction.Bidders bidders, Ask ask)` | Run the buyback: take the cheapest offers first up to the face asked, the last one in part, skipping any over the most per 100, and pay each its own price (D19). |

