# TreasuryCheck.java - 227 lines · 2 methods · 1 constants · harnesses

`ham/citybuildersim/TreasuryCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Plays a city and audits what the screens say the treasury did. Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus: "show how much was the actual month change, like in
> the next month button it shows 3k but sometimes cause of land buybacks or
> sales it was actually more or less." The dome and the Government Overview now
> both print a measured cash movement, and a measured figure that is measured
> wrongly is worse than the estimate it replaced - it looks authoritative.
> 
> The four things it will not let past:
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

**Uses:** [Game](Game.md) (6), [GameFiles](GameFiles.md) (2), [Sectors](Sectors.md) (1)

## Sections

| line | section |
|---:|---|
| 85 | · · 1. no gap between windows |
| 91 | · · 2. the closing IS the cash |
| 95 | · · 3. the bridge foots |
| 103 | · · 5. and on a hands-off city there is nothing in it |
| 114 | · AND AGAIN WITH THE SUBSIDY DIAL ON. |
| 161 | · AND IT HAS TO SURVIVE A SAVE. |
| 205 | · · and the first month back still has no gap |
| 210 | · THE REPORT. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 53 | 175 | **type** `public class TreasuryCheck` | Plays a city and audits what the screens say the treasury did. |
| 60 | 8 | `static void near(String what, int month, double actual, double expected)` |  |
| 69 | 158 | `public static void main(String[] args)` |  |

