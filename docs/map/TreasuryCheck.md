# TreasuryCheck.java - 872 lines · 15 methods · 1 constants · harnesses

`ham/citybuildersim/TreasuryCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> Plays a city and audits what the screens say the treasury did. Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus: "show how much was the actual month change, like in
> the next month button it shows 3k but sometimes cause of land buybacks or
> sales it was actually more or less." The dome (the header's TREASURY tile
> since 0.7.21, its money block since 0.7.24) and the Government Overview
> now both print a measured cash movement, and a measured figure that is
> measured wrongly is worse than the estimate it replaced - it looks
> authoritative.
> 
> The eight things it will not let past:
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
> ... (26 more lines in the source)

**Uses:** [Game](Game.md) (49), [Rollover](Rollover.md) (24), [TreasuryJournal](TreasuryJournal.md) (12), [Debt](Debt.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (5), [GameFiles](GameFiles.md) (5), [Sectors](Sectors.md) (1), [LandParcel](LandParcel.md) (1), [ShortTermTBill](ShortTermTBill.md) (1), [NationalAccounts](NationalAccounts.md) (1), [TaxPolicy](TaxPolicy.md) (1)

## Sections

| line | section |
|---:|---|
| 182 | · · 1. no gap between windows |
| 188 | · · 2. the closing IS the cash |
| 192 | · · 3. the bridge foots |
| 200 | · · 5. and on a hands-off city there is nothing in it |
| 218 | · AND AGAIN WITH THE SUBSIDY DIAL ON. |
| 280 | · AND IT HAS TO SURVIVE A SAVE. |
| 324 | · · and the first month back still has no gap |
| 329 | · AND THE ROW OPENS. |
| 496 | · 7. ROLLING WHAT FALLS DUE (0.7.13) |
| 499 | · 8. FROM EARNED TO THE BUDGET (0.7.31) |
| 502 | · THE REPORT. |
| 521 | 7. ROLLING WHAT FALLS DUE (0.7.13). |
| 550 | 8. FROM EARNED TO THE BUDGET (0.7.31). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 95 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 780 | **type** `public class TreasuryCheck` | Plays a city and audits what the screens say the treasury did. |
| 100 | 8 | `static void near(String what, int month, double actual, double expected)` |  |
| 114 | 8 | `static void nearOf(String what, int month, double actual, double expected, double size)` | Two figures that must agree to a part in a billion of the figures they are made of (0.7.31): the walk from EARNED to the budget adds a dozen figures in the millions, so an absolute tenth of a cent is too fine. |
| 128 | 11 | `static double[] earnedWalk(Game g)` | The walk from EARNED to the budget, as the Government tab draws it: {EARNED, the steps summed, what they leave, the budget's balance, the size of the figures in it}. |
| 141 | 4 | `static void check(String what, boolean ok)` | A fact that is either so or not, printed either way so the run reads as a list. |
| 147 | 4 | `static TreasuryJournal.Entry line(java.util.List<TreasuryJournal.Entry> journal, String label)` | The journal line with this label, or null when the month has none. |
| 153 | 4 | `static double amount(java.util.List<TreasuryJournal.Entry> journal, String label)` | The amount on the journal line with this label, or 0 when there is none. |
| 158 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 165 | 355 | `public static void main(String[] args)` |  |

### 7. ROLLING WHAT FALLS DUE (0.7.13). (lines 521-549)

### 8. FROM EARNED TO THE BUDGET (0.7.31). (lines 550-872)

| line | len | member | says |
|---:|---:|---|---|
| 562 | 29 | `static void earned()` |  |
| 593 | 5 | `static Game founded(String label)` | A city founded as a player founds one: newGame(), so it rolls in the same structure. |
| 600 | 7 | `static String press(Game g)` | One press, its printing kept - the log is where a rollover says what it did. |
| 609 | 5 | `static void quietly(Runnable work)` | Some quiet work: an issue's receipt, a save. |
| 616 | 7 | `static Debt paper(Game g, String type, int months, int started, boolean foreign)` | The piece of paper of this type, term and currency issued in this month, or null. |
| 625 | 3 | `static boolean audited(Game g)` | The press closed its audit, and nothing moved after it struck. |
| 629 | 243 | `static void rolling()` |  |

