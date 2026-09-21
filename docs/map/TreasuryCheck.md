# TreasuryCheck.java - 417 lines · 6 methods · 1 constants · harnesses

`ham/citybuildersim/TreasuryCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Plays a city and audits what the screens say the treasury did. Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus: "show how much was the actual month change, like in
> the next month button it shows 3k but sometimes cause of land buybacks or
> sales it was actually more or less." The dome and the Government Overview now
> both print a measured cash movement, and a measured figure that is measured
> wrongly is worse than the estimate it replaced - it looks authoritative.
> 
> The six things it will not let past:
> 
>   1. THE WINDOW CLOSES. Each month's opening balance is the previous month's
>      closing balance, with no gap. If it ever is not, a month of the player's
>      own land and building decisions has fallen down the crack between two
>      windows and the figure on the button is understated by exactly that.
> 
>   2. THE CLOSING BALANCE IS THE CASH. Whatever the recorder wrote down is
>      what getCash() says a moment later.
> 
>   3. THE BRIDGE FOOTS. Surplus, plus paper issued, less principal repaid,
>      plus the "everything else" row, equals the change. That is an identity
>      by construction - which is the point: if it ever breaks, the screen is
>      drawing four rows that do not add to the total printed under them.
> 
>   4. IT SURVIVES A SAVE. The reconciliation is on the first screen a
>      returning player opens, so it has to be there before a month is played.
> 
>   5. AND ON A CITY NOBODY TOUCHED, THE RESIDUAL ROW IS EMPTY. This one is
>      new, and it is the assertion this harness was built to earn the right
>      to make. The "everything else" row is real money in a played city -
>      reserves bought, bonds retired early, capital put into the bank - but
>      none of those happen on their own, so a hands-off run has nothing to put
>      in it and every dollar must be described by the budget or its borrowing.
> 
>      It did not used to be. Three things were wrong, all found by asking why
>      this row was not zero:
> 
>        - the government's books were struck from a stash taken half a tick
>          early, so 116 of 120 months reported the PREVIOUS month's land and
>          buildings, largest $118.42k
>        - the bank's profit tax reached the treasury's cash and appeared on no
>          budget, which was the whole of what was left: 110 months adrift by
>          exactly the bank's tax and by nothing else
>        - a subsidy left the treasury on the spot and was on no budget either,
>          which nothing caught because the dial is off by default. Hence the
>          second city below, which turns it on.
> 
>   6. AND THE ROW OPENS. Since 2026-09-18 the "everything else" row is a
>      TreasuryJournal - capital into the bank, reserves, a buyback, the
>      students' loans, and the two lines the budget balance omits - with a
>      "Not accounted for" line under it. The third city below does each of
>      those things between two presses and asserts the line it left, that
>      what IS on the budget (land, a building) is not named twice, that the
>      bridge still foots with the journal and the residual in it, that the
>      residual is smaller than what the journal explained - and what it is:
>      the first coupon, booked the month it is charged and paid the month
>      after - and that the whole journal comes back from a save line for
>      line. The "Raised by issuing paper" row is asserted here too, because
>      until this batch it read $0 on every month the city borrowed.

**Uses:** [Game](Game.md) (14), [TreasuryJournal](TreasuryJournal.md) (10), [GameFiles](GameFiles.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Debt](Debt.md) (2), [Sectors](Sectors.md) (1), [LandParcel](LandParcel.md) (1), [ShortTermTBill](ShortTermTBill.md) (1), [NationalAccounts](NationalAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 123 | · · 1. no gap between windows |
| 129 | · · 2. the closing IS the cash |
| 133 | · · 3. the bridge foots |
| 141 | · · 5. and on a hands-off city there is nothing in it |
| 152 | · AND AGAIN WITH THE SUBSIDY DIAL ON. |
| 199 | · AND IT HAS TO SURVIVE A SAVE. |
| 243 | · · and the first month back still has no gap |
| 248 | · AND THE ROW OPENS. |
| 400 | · THE REPORT. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 68 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 66 | 352 | **type** `public class TreasuryCheck` | Plays a city and audits what the screens say the treasury did. |
| 73 | 8 | `static void near(String what, int month, double actual, double expected)` |  |
| 83 | 4 | `static void check(String what, boolean ok)` | A fact that is either so or not, printed either way so the run reads as a list. |
| 89 | 4 | `static TreasuryJournal.Entry line(java.util.List<TreasuryJournal.Entry> journal, String label)` | The journal line with this label, or null when the month has none. |
| 95 | 4 | `static double amount(java.util.List<TreasuryJournal.Entry> journal, String label)` | The amount on the journal line with this label, or 0 when there is none. |
| 100 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 107 | 310 | `public static void main(String[] args)` |  |

