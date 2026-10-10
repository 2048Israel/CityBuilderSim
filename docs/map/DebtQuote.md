# DebtQuote.java - 209 lines · 9 methods · 1 constants · model

`ham/citybuildersim/DebtQuote.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> Everything here is a term of the loan, not a description of it - the wording
> lives in summary(), so the console and the JavaFX screens say the same thing.
> 
>                        exactly what this borrowing costs the city's credit
>                        discount or premium, plus all the coupons

**Used by (14):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CreditCheck](CreditCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [Pieces](Pieces.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 203 | `DebtQuote.FORMAT` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 43 | 167 | **type** `public record DebtQuote(String instrument, int duration, double requested, double marketRate, double rateBe...` | What a loan would cost, worked out BEFORE the player agrees to it. |
| 55 | 3 | `public String timeUnit()` | Years for a bond, months for a bill - the unit the duration is in. |
| 66 | 10 | `public String creditImpact()` | What the borrowing does to the city's credit, as a sentence. |
| 86 | 33 | `public String summary()` | The terms, in the vocabulary a bond is actually described in. |
| 121 | 3 | `public double pricePerPar()` | Where it was issued against par, the way bonds are quoted. |
| 126 | 3 | `public boolean isDiscount()` | True when the city receives less than it will repay. |
| 137 | 3 | `public double couponRate()` | The coupon actually charged monthly, backed out of the monthly bill. |
| 142 | 3 | `public boolean isEmpty()` | True if the city is receiving nothing worth booking. |
| 158 | 31 | `public double[] schedule(double toLocal)` | Every payment this paper would ask, month by month, index i paid i + 1 months after it is issued, times `toLocal` (1 for the city's own paper; the exchange rate, for a dollar quote's figures into local money): a note'... |
| 199 | 3 | `private static String f(double v)` | A figure on this quote, in dollars. |
| 205 | 4 | `static { ... }` |  |

