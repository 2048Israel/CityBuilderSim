# CentralBankCheck.java - 913 lines · 11 methods · 2 constants · harnesses

`ham/citybuildersim/CentralBankCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> Proves the central bank's books: that money is made and destroyed on them
> and nowhere else, every price 0.7.0 hangs off the policy rate, and its two
> dials - the holdings (0.7.1) and the advances ceiling (0.7.2). Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus: "the feds sheet would show how much debt it holds,
> like debt to itself aka money printing." Until 0.7.0 the city's central bank
> was the rest of the world - the bank placed its spare cash abroad and funded
> its shortfalls abroad - and the treasury's overdraft was an emergency note
> with no limit, which compounded a broke seed to $193 quadrillion once the
> bank really paid for it. The central bank brings all of it home, and a
> balance sheet that makes money is exactly the kind of thing that must be
> checked to the cent: a dollar it makes that the audit does not see is a
> dollar from nowhere with an official name on it.
> 
> What it has to prove, a section each, every one a fixture that causes the
> condition rather than waiting for it:
> 
>   1. Money made less money destroyed is the change in M0, every month, and
>      the audit's own MONEY lines say the same - on a city played through
>      every kind of flow the central bank has, and a month holding most of
>      them at once.
>   2. The bank earns exactly the policy rate on its reserves, and what it
>      pays savers rises with it.
>   3. The window prices at the policy rate plus the penalty, and its
>      interest is the central bank's - and a failed bank's window debt is
>      advanced and charged nothing (0.7.1: decided, see Bank.fundToCover()).
>   4. The city's note prices at policy + spreads (+ the bank's strain
>      premium, until 0.7.7), with no discount under the dial, and never
>      under what the money costs the bank.
>   5. A broke treasury draws advances at the policy rate, repays them from
>      cash first, and the remittance carries the interest back less what
>      reserves cost - and a buyback pays the bank that held the bond.
>   6. The ceiling binds, and the arrears rule pays the promises, refuses the
>      rest, books what it refused, and pays it down first when cash returns.
>   7. The autopilot: the rule moves the dial, the player's hand stops it, and
>      the toggle survives a save - as does a dial at 0%.
>   8. A reform scales the money figures and not the ratios.
>   9. Everything above survives a save.
>  10. A save from before the central bank founds one empty and runs, a save
>      carrying a note runs it off, and a new game after a load founds a
>      fresh bank.
> 
> And since 0.7.1, the holdings dial - Jerus: "the central bank would buy
> gbonds or sell gbonds from thin air ... like QE and QT" - on a city of its
> own whose bank holds a twenty-year bond:
> 
>  11. With the dial at 30% the central bank buys QE_SPEED of it a month from
>      the bank, at the curve's market value, in money it makes; the bank's
>      book falls by the face; the long end of the curve sits exactly
>      compression(240) under the table and the note does not move.
>  12. The coupon on its share is paid it and destroyed, is in the month's
>      profit, and reaches the treasury as the remittance the month after.
>  13. The dial to nothing sells the book back over 1/QE_SPEED months, the
>      bank pays what the central bank destroys, and the curve returns to
>      the table.
>  14. The holdings survive a save.
>  15. A hundred-to-one reform scales the holdings and not the dial.
> 
> And since 0.7.2, the ceiling as a dial - batch B found six months of
> ... (13 more lines in the source)

**Uses:** [Game](Game.md) (29), [CentralBank](CentralBank.md) (25), [DebtManager](DebtManager.md) (14), [Bank](Bank.md) (12), [TreasuryLine](TreasuryLine.md) (8), [Debt](Debt.md) (6), [GameFiles](GameFiles.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [MoneyAudit](MoneyAudit.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Founding](Founding.md) (1)

## Sections

| line | section |
|---:|---|
| 118 | ONE MONTH, AUDITED - the identity section 1 asserts, held on every |
| 171 | · 2. reserves earn the policy rate |
| 204 | · 3. the window |
| 255 | · 4. the city's paper |
| 284 | · the city |
| 313 | · 1 and 5. every kind of flow |
| 438 | · 6. the ceiling and the arrears |
| 504 | · 7. the autopilot |
| 540 | · 9. the save |
| 571 | · 8. a currency reform |
| 595 | · 10. an old save |
| 643 | 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. |
| 813 | 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 123 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the ...` |  |
| 126 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `static int fails` |  |
| 88 | `static PrintStream out` |  |
| 89 | `static PrintStream quiet` |  |
| 127 | `static int monthsPlayed, monthsBroken, mostKindsInAMonth` |  |
| 128 | `static double worstResidual` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 829 | **type** `public class CentralBankCheck` | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its two dials - the holdings (0.7.1) and the advances ceiling (0.7.2). |
| 91 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 96 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 106 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 113 | 4 | `static void quietly(Runnable r)` |  |

### ONE MONTH, AUDITED - the identity section 1 asserts, held on every (lines 118-642)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 10 | `static int kindsThisMonth(CentralBank cb)` |  |
| 142 | 23 | `static int play(Game g)` | Plays a month and holds it to the identities. |
| 166 | 476 | `public static void main(String[] args) throws Exception` |  |

### 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. (lines 643-812)

| line | len | member | says |
|---:|---:|---|---|
| 648 | 1 | `static double shape20(DebtManager m)` | The long end's shape over the note: the premium at twenty years less what the holdings compress. |
| 650 | 162 | `static void theHoldingsDial(GameFiles files) throws Exception` |  |

### 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road (lines 813-913)

| line | len | member | says |
|---:|---:|---|---|
| 819 | 86 | `static void theCeilingDial(GameFiles files) throws Exception` |  |
| 907 | 6 | `static double journalAmount(Game g, String label)` | The amount on last month's journal line with this label, or 0. |

