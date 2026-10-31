# DebtQuote.java - 308 lines · 12 methods · 1 constants · model

`ham/citybuildersim/DebtQuote.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> What a loan would cost, worked out BEFORE the player agrees to it.
> 
> WHY THIS EXISTS
> 
> The market prices a loan with the loan itself on the books - ask for more and
> you are quoted more, because you are a worse credit the moment the money
> lands. That is the right behaviour and it was invisible: the player picked an
> amount, hit Confirm, and only then found out what it cost. You cannot make a
> borrowing decision after you have borrowed.
> 
> THE RULE THIS TYPE ENFORCES
> 
> A quote that is computed separately from the deal is a second definition of
> the deal, and second definitions drift. So the quote is not a preview of the
> arithmetic - it IS the arithmetic. Game.quoteTBill()/quoteMediumBond()/
> quoteLongBond() - and quoteLongBondForCash(), the build screen's bond
> (0.7.10) - work the terms out and return one of these; the matching
> handle*() then books exactly the numbers in the record it was handed and
> computes nothing of its own. There is no path by which the screen can promise
> one thing and the ledger record another.
> 
> AND SINCE CD2 THE DEAL IS AN AUCTION. The city's own paper is sold at
> auction (Auction): the quote carries the auction's result - the stop, the
> tail over fair, the cover, who took it - and a sale whose bids did not reach
> the issue at the highest yield the city took quotes what it SOLD, not what
> it asked (isShort()). Dollar paper is not sold at auction, so its quote
> carries none.
> 
> Everything here is a term of the loan, not a description of it - the wording
> lives in summary(), so the console and the JavaFX screens say the same thing.
> 
>                        note) or "Linked" (an inflation-linked bond), the last two since CD5
>                        for the ForCash quotes, the face for a bond sold by face
>                        auction's stop, or the world's rate for dollar paper
>                        exactly what this borrowing costs the city's credit
>                        that came up short quotes what it sold
>                        discount or premium, plus all the coupons
>                        bond's, rounded down to an eighth of a point; the
>                        stop itself for an amortizing bond) or the world's
>                        for a dollar bond; 0 for a bill. For a "Float" the
>                        MARGIN over the policy rate, which may be nothing or
>                        below (couponRate() would back out the whole coupon
>                        instead, so read this); for a "Linked" the real coupon
>                        carries the city's right to call it whole at 100
>                        reopened keeps its own); -1 when not callable

**Uses:** [Auction](Auction.md) (2)

**Used by (21):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CallCheck](CallCheck.md), [CreditCheck](CreditCheck.md), [CurveCheck](CurveCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [NewGameCheck](NewGameCheck.md), [Pieces](Pieces.md), [RestructureCheck](RestructureCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 302 | `DebtQuote.FORMAT` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 66 | 243 | **type** `public record DebtQuote(String instrument, int duration, double requested, double marketRate, double rateBe...` | What a loan would cost, worked out BEFORE the player agrees to it. |
| 82 | 6 | `public DebtQuote(String instrument, int duration, double requested, double marketRate, double rateBefore, double faceValue, dou...` | A quote of paper that cannot be called: every sale before CD4, and every one that does not ask for the option. |
| 90 | 3 | `public String timeUnit()` | Years for a bond, months for a bill - the unit the duration is in. |
| 101 | 10 | `public String creditImpact()` | What the borrowing does to the city's credit, as a sentence. |
| 121 | 64 | `public String summary()` | The terms, in the vocabulary a bond is actually described in. |
| 192 | 19 | `private String saleLines()` | What the auction did, for a sale at home: the stop and the tail over fair, the cover, the coupon and the price per 100, and "sold X of Y" when the bids did not reach the issue at the highest yield the city took. |
| 213 | 3 | `public double pricePerPar()` | Where it was issued against par, the way bonds are quoted. |
| 218 | 3 | `public boolean isDiscount()` | True when the city receives less than it will repay. |
| 227 | 4 | `public double couponRate()` | The annual coupon the paper carries: the one the sale set, no longer backed out of the monthly bill (CD2). |
| 233 | 3 | `public boolean isShort()` | True when this was sold at auction and the bids did not reach the issue at the highest yield the city took: the quote is what it sold. |
| 238 | 3 | `public boolean isEmpty()` | True if the city is receiving nothing worth booking. |
| 256 | 32 | `public double[] schedule(double toLocal)` | Every payment this paper would ask, month by month, index i paid i + 1 months after it is issued, times `toLocal` (1 for the city's own paper; the exchange rate, for a dollar quote's figures into local money): a bill'... |
| 298 | 3 | `private static String f(double v)` | A figure on this quote, in dollars. |
| 304 | 4 | `static { ... }` |  |

