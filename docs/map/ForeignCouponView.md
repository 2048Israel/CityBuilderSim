# ForeignCouponView.java - 49 lines · 3 methods · 0 constants · model

`ham/citybuildersim/ForeignCouponView.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> What the Government screen says of the coupons the city pays abroad: the note on its "...of which paid abroad" row, from the moneys the
> city's foreign paper is written in (FB13, CD9-G).
> 
> WHY THIS EXISTS. The row's note said "in dollars", from before CD8, when the city could owe the US$ alone. Since CD8 a piece is written in any
> of the eight moneys and its coupon is paid in that money (Game.payForeignInterest(own, money)), and the figure on the row is the month's
> sum of them in D$ (Game.getForeignInterestPaidThisMonth(), "kept in LOCAL money", with no split by money kept): right, and in D$, which
> "in dollars" read as US$. The note says so, and which moneys the paper the city holds now is written in - held now, not the month's coupons,
> which the books do not keep by money. A pure
> class beside the model, as LandmassView is, so that the words are written once and a harness can rebuild them from getters; the screen only
> puts them on the row.

**Uses:** [DebtManager](DebtManager.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [WorldMoneys](WorldMoneys.md) (1)

**Used by (2):** [FB13Check](FB13Check.md), [GovernmentScreen](GovernmentScreen.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 30 | **type** `public final class ForeignCouponView` | What the Government screen says of the coupons the city pays abroad: the note on its "...of which paid abroad" row, from the moneys the city's foreign paper is written in (FB13, CD9-G). |
| 22 | 1 | `private ForeignCouponView()` |  |
| 28 | 8 | `public static List<String> moneys(DebtManager ledger)` | The moneys the city's foreign paper is written in, in WorldMoneys.EIGHT's order, as ForeignAccounts.MONEY_PLURAL names them ("US dollars", "euros", "Swiss francs"): the pieces on the books now (DebtManager.hasForeignD... |
| 42 | 7 | `public static String note(DebtManager ledger)` | The row's note: "in D$, at the month's rates · paper held now in US dollars, euros and yen" - the figure's own money first, then the moneys the paper on the books is written in now (not the month's coupons: no split b... |

