# DebtExchangeCheck.java - 657 lines · 22 methods · 6 constants · harnesses

`ham/citybuildersim/DebtExchangeCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The exchange gain (loss) on the city's foreign-currency debt (CD7, spec-city-debt.md section 5): each dollar
> piece carried in US$ and read in D$ at the rate it was last valued at, so a month in which the D$ moves moves
> the D$ value of the debt, and the books say so - a line below the operating lines, in the budget's result and
> not in its totals, not cash, taken back out by the treasury bridge, and left out of every rule that is about
> cash.
> 
> WHY. CD3 put the dollar paper on the books in dollars and CD6 printed what closing a line would book, but the
> D$'s own move on what is still owed was booked nowhere: the debt screen's figure moved, the budget's did not,
> and a currency gain would have netted a rollover and paid a fund in with D$ nobody held. The rule is the one
> ForeignAccounts.revalueVault() values the vault by - the carrying value in its own money times the change in
> the rate - applied to each piece at the rate it was last valued at.
> 
> What this has to prove, on a city that borrows in dollars and whose D$ is made to move by exactly the factor
> the section names (ForeignAccounts.pinRate() before the press, so the move is the reprice's to book):
> 
>   1. The fixture: dollar paper outstanding with its books open, domestic paper beside it, and a month in which
>      the D$ does not move books nothing.
>   2. The D$ falls 10%: the month's line is the sum over the dollar pieces of the carrying value in US$ times
>      the change in the rate, to the cent; below nothing; the domestic paper adds nothing; the result carries it
>      and the totals do not; the month after books nothing again.
>   3. The bridge foots: the not-cash row takes the line out, EARNED walks to the budget with it a step, and the
>      residuals are what they are in a month with no move.
>   4. MoneyAudit is untouched: the month closes as it does without a move.
>   5. The book after the fall: a dollar line sold before it reads -10% of what it is carried at, the domestic
>      line nothing, the desk its lines' sums.
>   6. The D$ rises back: a gain, each piece at its own last rate, positive; a piece sold in between is booked
>      from the rate it was sold at.
>   7. The book after the rise: two sale rates on two lines.
>   8. The cash rules leave it out: the rollover's netting, the fund's pay-in and the fund's floor read the
>      history's surplus less the line, and the history keeps the line so spending is recoverable.
>   9. Across a save and a load: the line, its history, each sale's rate come back, and the reloaded city books
>      what an unsaved twin books - nothing at the load, the same in the next move.
>  10. The rule alone, on a bare ledger: a rate of nothing is refused, each piece moves from its own rate, a
>      piece sold at two rates remembers their proceeds-weighted mean, and a change of unit scales it.
>  11. The student grant on a surplus share is a share of the CASH surplus (SG1), on a city with students, a cash
>      surplus and dollar paper: the month the D$ rises 10% its exchange gain is in the balance and not in the grant,
>      the month it falls 10% the loss that takes the balance below nothing does not take the grant to nothing, and
>      the grant is what a month in which the D$ did not move would pay.
>  12. The same for the gain on paper bought back below its book: the balance holds it, the cash does not (the
>      price paid was the cash), and the grant is the share of the cash.

**Uses:** [Debt](Debt.md) (27), [MoneyAudit](MoneyAudit.md) (20), [Game](Game.md) (20), [BookDesk](BookDesk.md) (10), [TaxPolicy](TaxPolicy.md) (9), [NationalAccounts](NationalAccounts.md) (8), [TreasuryJournal](TreasuryJournal.md) (6), [FamilyModel](FamilyModel.md) (5), [ForeignAccounts](ForeignAccounts.md) (3), [DebtManager](DebtManager.md) (3), [GameFiles](GameFiles.md) (2), [EducationCheck](EducationCheck.md) (2), [Rollover](Rollover.md) (2), [HistorySave](HistorySave.md) (1), [CityCalendar](CityCalendar.md) (1), [EconomyManager](EconomyManager.md) (1), [LongTermBond](LongTermBond.md) (1)

## Sections

| line | section |
|---:|---|
| 246 | · 1. the fixture |
| 285 | · 2. the D$ falls 10% |
| 309 | · 3. the bridge foots |
| 338 | · 4. MoneyAudit untouched |
| 348 | · 5. the book after the fall |
| 371 | · 6. the D$ rises back |
| 408 | · 7. the book after the rise |
| 422 | · 8. the cash rules leave it out |
| 466 | · 9. across a save and a load |
| 501 | · 10. the rule alone |
| 544 | · 11. the student grant on a surplus share reads the cash |
| 600 | · 12. ...and a buyback below book |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `DebtExchangeCheck.OPENING` | `1.25` | The D$'s rate the city's dollar paper is first sold at, D$ per US$ (the fixture pins it there). |
| 60 | `DebtExchangeCheck.KEEPS` | `0.90` | The share of its worth in US$ the D$ keeps in a fall: 0.90 is a D$ 10% weaker; the rise back is the same factor the other way. |
| 63 | `DebtExchangeCheck.SHARE` | `.5` | The share of the surplus the grant is set to in section 11. |
| 66 | `DebtExchangeCheck.GRANT_BOND` | `60_000` | The dollar bond section 11 sells, in US$ thousands: large beside the surplus, so its exchange line is. |
| 69 | `DebtExchangeCheck.BUYBACK_BOND` | `100_000` | The domestic bond section 12 sells and buys back, in thousands: large beside the surplus, so its gain is. |
| 72 | `DebtExchangeCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a thousandth of a thousandth is plenty (TreasuryCheck.TOLERANCE). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |
| 53 | `static int asserted` |  |
| 54 | `static PrintStream out` |  |
| 210 | `final double rateBefore, rateAfter, line, gainByRule, gainAsCarrying, domesticWould` |  |
| 211 | `final MoneyAudit.Result audit` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 50 | 608 | **type** `public class DebtExchangeCheck` | The exchange gain (loss) on the city's foreign-currency debt (CD7, spec-city-debt.md section 5): each dollar piece carried in US$ and read in D$ at the rate it was last valued at, so a month in which the D$ moves move... |
| 74 | 5 | `static void check(String label, boolean ok)` |  |
| 81 | 3 | `static void near(String label, double actual, double expected)` | Two figures of the books that must agree to the tolerance MoneyAudit gives at their size, the figures printed on a failure. |
| 85 | 10 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 97 | 5 | `static void press(Game g)` | One press, its printing thrown away. |
| 103 | 5 | `static void quietly(Runnable work)` |  |
| 109 | 5 | `static Game founded(String label)` |  |
| 115 | 5 | `static List<Debt> foreign(Game g)` |  |
| 121 | 5 | `static List<Debt> domestic(Game g)` |  |
| 127 | 3 | `static NationalAccounts accounts(Game g)` |  |
| 131 | 3 | `static double sizeOf(Game g)` |  |
| 136 | 5 | `static double[] ratesOf(List<Debt> pieces)` | The rates each piece was last valued at, before a press, in the ledger's order. |
| 146 | 5 | `static double ruleGain(List<Debt> pieces, double[] ratesBefore, double rateAfter)` | The month's exchange gain by the rule alone: for each piece, its carrying value in its own money (at the reprice, which nothing after changes) times the rate it was last valued at less the rate now. |
| 153 | 5 | `static double asCarryingMove(List<Debt> pieces, double[] ratesBefore)` | The same figure said as the D$ carrying value the pieces had at the old rate less the one they have at the new. |
| 165 | 15 | `static Game schooledCity(String label)` | A city with homes and the schools to fill, as the grant's own harness builds it (EducationCheck), so somebody is studying; every tax at its ceiling, because the schools' running costs leave it a deficit otherwise and ... |
| 182 | 9 | `static Month untilSharing(Game g)` | Presses, the D$ not moving, until somebody is studying and the month closed on a cash surplus to share; returns that month. |
| 197 | 4 | `static double cashByRule(Game g)` | The month's cash surplus by the rule alone: the balance less the lines it books that no cash moves on - the gain on paper retired and the exchange gain - and plus the discount amortised and the uplift, which it charge... |
| 203 | 4 | `static TreasuryJournal.Entry entry(List<TreasuryJournal.Entry> entries, String label)` | The journal's or the walk's entry with this label, or null. |
| 209 | 16 | **type** `static final class Month` | What a month's press left that the sections read afterwards. |
| 213 | 11 | `Month(Game g, double rateBefore, List<Debt> abroad, double[] ownBefore, List<Debt> home, double[] homeBefore)` _(in DebtExchangeCheck.Month)_ |  |
| 227 | 9 | `static Month move(Game g, double factorOfTheRate)` | Moves the D$ by this factor of its rate before the press (pinned, so the reprice leaves it where it is) and presses. |
| 237 | 401 | `public static void main(String[] args)` |  |
| 640 | 10 | `static void accountsAlone()` | The government account's setter alone: a figure that is no number reads nothing, a number is kept. |
| 652 | 5 | `static String quote(Game g, String type, double usd, int years)` | Sells dollar paper through the game's own door: returns what it said. |

