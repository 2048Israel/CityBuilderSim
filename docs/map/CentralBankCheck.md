# CentralBankCheck.java - 1,756 lines · 25 methods · 5 constants · harnesses

`ham/citybuildersim/CentralBankCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
>      rest, books what it refused, and pays it down first when cash returns -
>      and since 0.7.55 a sector paid arrears shows them on its cash-flow
>      statement, which still reconciles.
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
> ... (83 more lines in the source)

**Uses:** [DebtManager](DebtManager.md) (77), [Game](Game.md) (71), [CentralBank](CentralBank.md) (36), [Debt](Debt.md) (36), [LongPlaytest](LongPlaytest.md) (28), [Bank](Bank.md) (14), [GameFiles](GameFiles.md) (12), [Rollover](Rollover.md) (11), [TreasuryLine](TreasuryLine.md) (9), [MediumTermBond](MediumTermBond.md) (5), [SectorBooks](SectorBooks.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [MoneyAudit](MoneyAudit.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [PolicyPreview](PolicyPreview.md) (2), [Expectations](Expectations.md) (2), [TreasuryFund](TreasuryFund.md) (2), [HouseholdBalance](HouseholdBalance.md) (1), [Sector](Sector.md) (1), [Founding](Founding.md) (1), [LongTermBond](LongTermBond.md) (1), [DecisionLog](DecisionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 188 | ONE MONTH, AUDITED - the identity section 1 asserts, held on every |
| 241 | · 2. reserves earn the policy rate |
| 274 | · 3. the window |
| 325 | · 4. the city's paper |
| 354 | · the city |
| 383 | · 1 and 5. every kind of flow |
| 514 | · 6. the ceiling and the arrears |
| 605 | · 7. the autopilot |
| 641 | · 9. the save |
| 672 | · 8. a currency reform |
| 696 | · 10. an old save |
| 749 | 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. |
| 919 | 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road |
| 1020 | 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt |
| 1116 | 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a |
| 1364 | 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a |
| 1488 | 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 193 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the ...` |  |
| 196 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1124 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |
| 1505 | `CentralBankCheck.STRICT_BRANCH` | `600` | The month the probe city leaves Standard at: a mature city, the inflation ensemble's month for its policies. |
| 1508 | `CentralBankCheck.STRICT_HORIZON` | `240` | How long each of its twins plays on from there: twenty years, the inflation ensemble's window after its month 600. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 157 | `static int fails` |  |
| 158 | `static PrintStream out` |  |
| 159 | `static PrintStream quiet` |  |
| 197 | `static int monthsPlayed, monthsBroken, mostKindsInAMonth` |  |
| 198 | `static double worstResidual` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 155 | 1602 | **type** `public class CentralBankCheck` | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its two dials - the holdings (0.7.1) and the advances ceiling (0.7.2). |
| 161 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 166 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 176 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 183 | 4 | `static void quietly(Runnable r)` |  |

### ONE MONTH, AUDITED - the identity section 1 asserts, held on every (lines 188-748)

| line | len | member | says |
|---:|---:|---|---|
| 200 | 10 | `static int kindsThisMonth(CentralBank cb)` |  |
| 212 | 23 | `static int play(Game g)` | Plays a month and holds it to the identities. |
| 236 | 512 | `public static void main(String[] args) throws Exception` |  |

### 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. (lines 749-918)

| line | len | member | says |
|---:|---:|---|---|
| 754 | 1 | `static double shape20(DebtManager m)` | The long end's shape over the note: the premium at twenty years less what the holdings compress. |
| 756 | 162 | `static void theHoldingsDial(GameFiles files) throws Exception` |  |

### 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road (lines 919-1019)

| line | len | member | says |
|---:|---:|---|---|
| 925 | 86 | `static void theCeilingDial(GameFiles files) throws Exception` |  |
| 1013 | 6 | `static double journalAmount(Game g, String label)` | The amount on last month's journal line with this label, or 0. |

### 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt (lines 1020-1115)

| line | len | member | says |
|---:|---:|---|---|
| 1026 | 27 | `static void theWholeBook()` |  |
| 1054 | 61 | `static void theSplitFloor()` |  |

### 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a (lines 1116-1363)

| line | len | member | says |
|---:|---:|---|---|
| 1127 | 6 | `static Debt fixtureSerial(Game g, int soldIn)` | The serial the fixture sold: the one piece of the city's paper that is a serial and started before the month the fixture saved in. |
| 1135 | 7 | `static java.util.List<Debt> soldAtTheLastPress(Game g)` | The city's own paper sold at the last press or between it and the one before - started the month before this one. |
| 1144 | 3 | `static double issuePricePerFace(Debt d)` | What the central bank paid a unit of face for a piece at its issue: what the treasury was paid for it over its face, which the add-on does not move. |
| 1149 | 7 | `static Game toTheSlice(GameFiles files, int soldIn)` | The fixture as saved, loaded fresh, and played to the gap before its first slice. |
| 1157 | 206 | `static void theRolloverAtIssue() throws Exception` |  |

### 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a (lines 1364-1487)

| line | len | member | says |
|---:|---:|---|---|
| 1371 | 116 | `static void theSurplusPaysEveryone() throws Exception` |  |

### 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, (lines 1488-1756)

| line | len | member | says |
|---:|---:|---|---|
| 1496 | 4 | `static double oldRule(double inflation, double target)` | The rule as it was until 0.7.51, written out: the neutral rate, the target's distance from the default, and TAYLOR_WEIGHT on the gap. |
| 1502 | 1 | `static boolean bits(double a, double b)` | Equal to the bit (a NaN as any NaN). |
| 1510 | 167 | `static void howStrict() throws Exception` |  |
| 1697 | 43 | `static Game probeCity(GameFiles files)` | The probe city: the playtest's founding (LongPlaytest.founding()) in its seed 2's shape - 42 houses, then three months before the next 20 - and its rhythm (LongPlaytest.main's skips, schools and advice) to STRICT_BRAN... |
| 1742 | 3 | `static void playTo(Game g, int months)` | Up to `months` of the playtest's months, never past STRICT_BRANCH. |
| 1747 | 9 | `static void strictMonth(Game g)` | One of a twin's months: the playtest's month, with nothing built. |

