# DebtMoneysCheck.java - 504 lines · 17 methods · 5 constants · harnesses

`ham/citybuildersim/DebtMoneysCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The city's paper in any of the eight moneys (CD8, Jerus 2026-10-23): the city sells bills and bonds abroad in the euro, the yen, the pound,
> the yuan, the Canadian and Australian dollars and the franc as well as the US$, each on ITS money's curve - that bank's policy rate (the world's
> price of money in it) plus the city's country premium plus the term premium - the way it sold only US$ paper before. Each piece keeps its books
> in its own money and is read in D$ at the month's rate (the D$'s rate to the US$ times the money's US$ per unit); its buyback is CD4's
> reverse auction as a dollar piece's is; its revaluation is CD7's exchange line, summed over the pieces at their own rates.
> 
> WHY. WN4 gave the world eight moneys and the vault holds each, XR4 gave each money's bank a policy rate, and CD7 books what a move of the rate
> does to the paper carried in another money - but the city could only owe the US$, so the one thing a treasury does to manage exchange risk (borrow
> where it earns, or where the money is cheap, and hold what it borrowed in the money it owes) could not be done. The rule that matters most is the
> one this proves last: US$ paper behaves as it always did, to the bit, and every other path is new.
> 
> What this has to prove (the quiet city is the default founding on the goods' literals; the others have the world file):
> 
>   1. Without the world file only the US$ is offered: a euro quote is empty and a euro sale is refused with a sentence, and the US$ quote
>      through the new signature is the four-argument quote, to the last bit.
>   2. With it, each of the eight is offered, and its curve at a tenor is that bank's policy rate plus the country premium plus the term
>      premium (the US$'s is foreignCurveRate(m), exactly).
>   3. A ten-year euro bond and a twelve-month yen bill: each piece's money, its quote's rate on that money's curve (the price the bill fetched is
>      the curve's factor), its books in its own money (what it raised), the treasury's cash up by what it raised at the money's rate, and the
>      country premium afterwards reading both.
>   4. A month: each coupon is paid in D$ at the month's rate (its own coupon, the D$'s rate, its US$ per unit) and the money audit closes.
>   5. A rate move: the D$ pinned 10% weaker for a press, and then the crosses moving alone: the exchange line is the sum over the pieces of the
>      carrying value in its own money times its rate before less its rate after, each piece's rate after the D$'s times its US$ per unit.
>   6. Held as reserves: a euro sale held raises the vault's euro holding by what it raised in euros, and no other money's holding moves.
>   7. The buyback: the euro bond bought back whole at fair - its fair per 100 the value on its money's curve, all of it offered by the world, it
>      leaves the books, the US$ the city owes falls by its US$ value, and the audit closes the month after.
>   8. The rollover: a one-month euro bill falling due rolls into a euro bill - the plan's issue says euros and the new piece is written in euros.
>   9. Across a save and a load: each piece's money, its rate and its carrying value in its own money, and each money's quote, come back the
>      same, and the load books no exchange move.
>  10. US$ paper in the same city as before: its rate is the D$'s exactly, it saves with no money, the US$ the city owes counts it at its own-money
>      principal, and the US$'s every curve and quote is its old expression's.

**Uses:** [Debt](Debt.md) (27), [Game](Game.md) (17), [DebtManager](DebtManager.md) (11), [MoneyAudit](MoneyAudit.md) (7), [ForeignAccounts](ForeignAccounts.md) (7), [DebtQuote](DebtQuote.md) (6), [WorldMoneys](WorldMoneys.md) (5), [Rollover](Rollover.md) (4), [Buyback](Buyback.md) (2), [InterestAbroadCheck](InterestAbroadCheck.md) (1), [PolicyRates](PolicyRates.md) (1), [Auction](Auction.md) (1), [ShortTermTBill](ShortTermTBill.md) (1), [NationalAccounts](NationalAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 186 | · 1. without the world file |
| 209 | · 2. the curves |
| 239 | · 3. a sale in each of two moneys |
| 310 | · 4. a month |
| 325 | · 5. a rate move |
| 347 | · 6. held as reserves |
| 364 | · 7. the buyback |
| 385 | · 8. the rollover |
| 411 | · 9. across a save and a load |
| 454 | · 10. US$ paper as before |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `DebtMoneysCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a thousandth of a thousandth is plenty (DebtExchangeCheck.TOLERANCE). |
| 56 | `DebtMoneysCheck.EXPORTS` | `5_000` | The month's exports (thousands of D$) every sale and quote is made against: the town sells nothing abroad, and the window shuts on a city that owes and earns none. |
| 59 | `DebtMoneysCheck.USD_ASK` | `10_000` | What each piece is worth when it is sold, in thousands of US$ at the day's cross: the amount asked in each money is this over its US$ per unit. |
| 62 | `DebtMoneysCheck.KEEPS` | `0.90` | The share of its worth in US$ the D$ keeps in a fall: 0.90 is a D$ 10% weaker (DebtExchangeCheck.KEEPS). |
| 65 | `DebtMoneysCheck.TENORS` | `{ 1, 12, 60, 120, 360 }` | The tenors, in months, the curves are read at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static int asserted` |  |
| 49 | `static PrintStream out` |  |
| 50 | `static PrintStream quiet` |  |
| 157 | `double line, byRule, rateBefore, rateAfter` |  |
| 158 | `boolean eachPieceAtItsRate` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 460 | **type** `public class DebtMoneysCheck` | The city's paper in any of the eight moneys (CD8, Jerus 2026-10-23): the city sells bills and bonds abroad in the euro, the yen, the pound, the yuan, the Canadian and Australian dollars and the franc as well as the US... |
| 67 | 5 | `static void check(String label, boolean ok)` |  |
| 73 | 3 | `static void near(String label, double actual, double expected)` |  |
| 77 | 10 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 88 | 5 | `static void quietly(Runnable work)` |  |
| 95 | 4 | `static void press(Game g)` | One press, its printing thrown away, the window abroad held open for it (the rollover reads it at the top of the press). |
| 101 | 3 | `static void open(Game g)` | The month's exports handed to the debt, so the window abroad is open and the country premium is the same figure every read: the press hands the real (nil) ones again. |
| 106 | 5 | `static Game town(String label, boolean literals)` | A trading town on the default founding (InterestAbroadCheck.town), played three months so the world's moneys are handed to the debt. |
| 112 | 5 | `static List<Debt> foreignPieces(Game g)` |  |
| 118 | 5 | `static double[] ratesOf(List<Debt> pieces)` |  |
| 125 | 5 | `static Debt newest(Game g, Set<Debt> had)` | The one foreign piece on the books that was not there before. |
| 132 | 3 | `static double askIn(Game g, String code)` | What the asked amount is in this money's own units: USD_ASK at the day's cross. |
| 136 | 6 | `static String sell(Game g, String type, String money, double ask, int duration, boolean hold)` |  |
| 144 | 5 | `static double localPer(Game g, String code)` | D$ per unit of this money by the vault's own figures: the D$'s rate to the US$ times the money's US$ per unit. |
| 150 | 4 | `static boolean closes(Game g)` |  |
| 156 | 4 | **type** `static final class Move` | What a press left that the rate sections read afterwards. |
| 162 | 19 | `static Move move(Game g, double factorOfTheRate)` | Moves the D$ by this factor of its rate before the press (pinned, so the reprice leaves it where it is) and presses; the crosses move on their own path. |
| 182 | 316 | `public static void main(String[] args)` |  |
| 500 | 4 | `static Game playedMonth(Game g)` | A month played, the audit of it left for the caller to read. |

