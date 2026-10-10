# CentralBankCheck.java - 1,860 lines · 26 methods · 5 constants · harnesses

`ham/citybuildersim/CentralBankCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> ... (95 more lines in the source)

**Uses:** [DebtManager](DebtManager.md) (84), [Game](Game.md) (71), [CentralBank](CentralBank.md) (36), [Debt](Debt.md) (36), [LongPlaytest](LongPlaytest.md) (28), [Bank](Bank.md) (14), [GameFiles](GameFiles.md) (12), [Rollover](Rollover.md) (11), [TreasuryLine](TreasuryLine.md) (9), [MediumTermBond](MediumTermBond.md) (5), [SectorBooks](SectorBooks.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [MoneyAudit](MoneyAudit.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [PolicyPreview](PolicyPreview.md) (2), [Expectations](Expectations.md) (2), [TreasuryFund](TreasuryFund.md) (2), [HouseholdBalance](HouseholdBalance.md) (1), [Sector](Sector.md) (1), [Founding](Founding.md) (1), [LongTermBond](LongTermBond.md) (1), [DecisionLog](DecisionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 200 | ONE MONTH, AUDITED - the identity section 1 asserts, held on every |
| 253 | · 2. reserves earn the policy rate |
| 286 | · 3. the window |
| 337 | · 4. the city's paper |
| 366 | · the city |
| 395 | · 1 and 5. every kind of flow |
| 526 | · 6. the ceiling and the arrears |
| 617 | · 7. the autopilot |
| 653 | · 9. the save |
| 684 | · 8. a currency reform |
| 708 | · 10. an old save |
| 761 | 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. |
| 931 | 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road |
| 1032 | 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt |
| 1128 | 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a |
| 1376 | 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a |
| 1500 | 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 205 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the ...` |  |
| 208 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1136 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |
| 1530 | `CentralBankCheck.STRICT_BRANCH` | `600` | The month the probe city leaves Standard at: a mature city, the inflation ensemble's month for its policies. |
| 1533 | `CentralBankCheck.STRICT_HORIZON` | `240` | How long each of its twins plays on from there: twenty years, the inflation ensemble's window after its month 600. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 169 | `static int fails` |  |
| 170 | `static PrintStream out` |  |
| 171 | `static PrintStream quiet` |  |
| 209 | `static int monthsPlayed, monthsBroken, mostKindsInAMonth` |  |
| 210 | `static double worstResidual` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 167 | 1694 | **type** `public class CentralBankCheck` | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its two dials - the holdings (0.7.1) and the advances ceiling (0.7.2). |
| 173 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 178 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 188 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 195 | 4 | `static void quietly(Runnable r)` |  |

### ONE MONTH, AUDITED - the identity section 1 asserts, held on every (lines 200-760)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 10 | `static int kindsThisMonth(CentralBank cb)` |  |
| 224 | 23 | `static int play(Game g)` | Plays a month and holds it to the identities. |
| 248 | 512 | `public static void main(String[] args) throws Exception` |  |

### 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. (lines 761-930)

| line | len | member | says |
|---:|---:|---|---|
| 766 | 1 | `static double shape20(DebtManager m)` | The long end's shape over the note: the premium at twenty years less what the holdings compress. |
| 768 | 162 | `static void theHoldingsDial(GameFiles files) throws Exception` |  |

### 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road (lines 931-1031)

| line | len | member | says |
|---:|---:|---|---|
| 937 | 86 | `static void theCeilingDial(GameFiles files) throws Exception` |  |
| 1025 | 6 | `static double journalAmount(Game g, String label)` | The amount on last month's journal line with this label, or 0. |

### 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt (lines 1032-1127)

| line | len | member | says |
|---:|---:|---|---|
| 1038 | 27 | `static void theWholeBook()` |  |
| 1066 | 61 | `static void theSplitFloor()` |  |

### 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a (lines 1128-1375)

| line | len | member | says |
|---:|---:|---|---|
| 1139 | 6 | `static Debt fixtureSerial(Game g, int soldIn)` | The serial the fixture sold: the one piece of the city's paper that is a serial and started before the month the fixture saved in. |
| 1147 | 7 | `static java.util.List<Debt> soldAtTheLastPress(Game g)` | The city's own paper sold at the last press or between it and the one before - started the month before this one. |
| 1156 | 3 | `static double issuePricePerFace(Debt d)` | What the central bank paid a unit of face for a piece at its issue: what the treasury was paid for it over its face, which the add-on does not move. |
| 1161 | 7 | `static Game toTheSlice(GameFiles files, int soldIn)` | The fixture as saved, loaded fresh, and played to the gap before its first slice. |
| 1169 | 206 | `static void theRolloverAtIssue() throws Exception` |  |

### 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a (lines 1376-1499)

| line | len | member | says |
|---:|---:|---|---|
| 1383 | 116 | `static void theSurplusPaysEveryone() throws Exception` |  |

### 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, (lines 1500-1860)

| line | len | member | says |
|---:|---:|---|---|
| 1508 | 4 | `static double oldRule(double inflation, double target)` | The rule as it was until 0.7.51, written out: the neutral rate, the target's distance from the default, and TAYLOR_WEIGHT on the gap. |
| 1518 | 7 | `static double rule052(double inflation, double target, DebtManager.Strictness s)` | The rule at a step as it was from 0.7.52 to 0.7.80, written out: struck at the aim, on the gap past the band either side of it, at the step's weight. |
| 1527 | 1 | `static boolean bits(double a, double b)` | Equal to the bit (a NaN as any NaN). |
| 1535 | 232 | `static void howStrict() throws Exception` |  |
| 1801 | 43 | `static Game probeCity(GameFiles files)` | The probe city: the playtest's founding (LongPlaytest.founding()) in its seed 11's shape - 43 houses, then five months before the next 20 - and its rhythm (LongPlaytest.main's skips, schools and advice) to STRICT_BRAN... |
| 1846 | 3 | `static void playTo(Game g, int months)` | Up to `months` of the playtest's months, never past STRICT_BRANCH. |
| 1851 | 9 | `static void strictMonth(Game g)` | One of a twin's months: the playtest's month, with nothing built. |

