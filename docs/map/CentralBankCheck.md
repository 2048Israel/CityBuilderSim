# CentralBankCheck.java - 2,208 lines · 34 methods · 11 constants · harnesses

`ham/citybuildersim/CentralBankCheck.java` - generated 2026-11-03 by CodeMap; line numbers are as of that run.

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
> ... (97 more lines in the source)

**Uses:** [DebtManager](DebtManager.md) (88), [Game](Game.md) (83), [CentralBank](CentralBank.md) (36), [Debt](Debt.md) (36), [LongPlaytest](LongPlaytest.md) (29), [PriceLevel](PriceLevel.md) (22), [GameFiles](GameFiles.md) (16), [Bank](Bank.md) (14), [Rollover](Rollover.md) (11), [TreasuryLine](TreasuryLine.md) (9), [MoneyAudit](MoneyAudit.md) (6), [MediumTermBond](MediumTermBond.md) (5), [TreasuryJournal](TreasuryJournal.md) (4), [SectorBooks](SectorBooks.md) (3), [SectorStatements](SectorStatements.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [PolicyPreview](PolicyPreview.md) (2), [Expectations](Expectations.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Buyback](Buyback.md) (1), [Sector](Sector.md) (1), [Founding](Founding.md) (1), [LongTermBond](LongTermBond.md) (1), [WorldEconomy](WorldEconomy.md) (1), [DecisionLog](DecisionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 203 | ONE MONTH, AUDITED - the identity section 1 asserts, held on every |
| 257 | · 2. reserves earn the policy rate |
| 290 | · 3. the window |
| 341 | · 4. the city's paper |
| 370 | · the city |
| 399 | · 1 and 5. every kind of flow |
| 533 | · 6. the ceiling and the arrears |
| 655 | · 7. the autopilot |
| 691 | · 9. the save |
| 722 | · 8. a currency reform |
| 746 | · 10. an old save |
| 799 | 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. |
| 987 | 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road |
| 1088 | 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt |
| 1184 | 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a |
| 1448 | 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a |
| 1578 | 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 208 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the ...` |  |
| 211 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1192 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |
| 1608 | `CentralBankCheck.STRICT_BRANCH` | `600` | The month the probe city leaves Standard at: a mature city, the inflation ensemble's month for its policies. |
| 1611 | `CentralBankCheck.STRICT_HORIZON` | `240` | How long each of its twins plays on from there: twenty years, the inflation ensemble's window after its month 600. |
| 1621 | `CentralBankCheck.STRICT_WORLD_FALL` | `.20` | How far the world's prices fall, in the first month of the fourth game's twenty years (worldPricesFall): the cause of the very loose twin's months under its target, which the probe city's own path gave it in some buil... |
| 1624 | `CentralBankCheck.STRICT_FALL_WINDOW` | `24` | The months after the fall in which the fixture needs the very loose twin under its target: the fall's own, before the currency has answered. |
| 1627 | `CentralBankCheck.PROBE_HOUSES` | `43` | The probe city's houses before the next twenty: seed 7's shape since FR7 (probeCity()). |
| 1630 | `CentralBankCheck.PROBE_MONTHS` | `4` | ...and the months it plays before them, seed 7's four. |
| 1638 | `CentralBankCheck.STRICT_SHAPES` | `{ { 40, 3 }, { 41, 3 }, { 42, 3 }, { 43, 3 }, { 40, 4 }, { 41, 4 }, { 42, 4 }...` | The founding shapes very strict is measured against Standard in (CB1): the houses before the next twenty and the months played before them, as the playtest's seeds 0 to 11 found (LongPlaytest, ENSEMBLE MODE: 40 + seed... |
| 1647 | `CentralBankCheck.STRICT_SHARE` | `.75` | The share of STRICT_SHAPES' cities in which very strict must hold prices lower than Standard (CB1): EP1 found it in 11 of 12 shapes and FR7 in 15 of 16, so a city's path reverses it in about one city in twelve; three ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 172 | `static int fails` |  |
| 173 | `static PrintStream out` |  |
| 174 | `static PrintStream quiet` |  |
| 212 | `static int monthsPlayed, monthsBroken, mostKindsInAMonth` |  |
| 213 | `static double worstResidual` |  |
| 1672 | `int under, underSoon, underAsStandard, over, overCheaper, past` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 170 | 2039 | **type** `public class CentralBankCheck` | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its two dials - the holdings (0.7.1) and the advances ceiling (0.7.2). |
| 176 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 181 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 191 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 198 | 4 | `static void quietly(Runnable r)` |  |

### ONE MONTH, AUDITED - the identity section 1 asserts, held on every (lines 203-798)

| line | len | member | says |
|---:|---:|---|---|
| 215 | 10 | `static int kindsThisMonth(CentralBank cb)` |  |
| 227 | 24 | `static int play(Game g)` | Plays a month and holds it to the identities. |
| 252 | 546 | `public static void main(String[] args) throws Exception` |  |

### 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. (lines 799-986)

| line | len | member | says |
|---:|---:|---|---|
| 811 | 3 | `static double shape20(DebtManager m)` | The long end's shape: the premium at twenty years less what the holdings compress - the twenty-year rate less the dial the market expects over those years and the credit spread (the curve taken apart, CD1). |
| 815 | 171 | `static void theHoldingsDial(GameFiles files) throws Exception` |  |

### 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road (lines 987-1087)

| line | len | member | says |
|---:|---:|---|---|
| 993 | 86 | `static void theCeilingDial(GameFiles files) throws Exception` |  |
| 1081 | 6 | `static double journalAmount(Game g, String label)` | The amount on last month's journal line with this label, or 0. |

### 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt (lines 1088-1183)

| line | len | member | says |
|---:|---:|---|---|
| 1094 | 27 | `static void theWholeBook()` |  |
| 1122 | 61 | `static void theSplitFloor()` |  |

### 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a (lines 1184-1447)

| line | len | member | says |
|---:|---:|---|---|
| 1195 | 6 | `static Debt fixtureSerial(Game g, int soldIn)` | The serial the fixture sold: the one piece of the city's paper that is a serial and started before the month the fixture saved in. |
| 1203 | 7 | `static java.util.List<Debt> soldAtTheLastPress(Game g)` | The city's own paper sold at the last press or between it and the one before - started the month before this one. |
| 1212 | 3 | `static double issuePricePerFace(Debt d)` | What the central bank paid a unit of face for a piece at its issue: what the treasury was paid for it over its face, which the add-on does not move. |
| 1217 | 7 | `static Game toTheSlice(GameFiles files, int soldIn)` | The fixture as saved, loaded fresh, and played to the gap before its first slice. |
| 1225 | 222 | `static void theRolloverAtIssue() throws Exception` |  |

### 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a (lines 1448-1577)

| line | len | member | says |
|---:|---:|---|---|
| 1455 | 122 | `static void theSurplusPaysEveryone() throws Exception` |  |

### 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, (lines 1578-2208)

| line | len | member | says |
|---:|---:|---|---|
| 1586 | 4 | `static double oldRule(double inflation, double target)` | The rule as it was until 0.7.51, written out: the neutral rate, the target's distance from the default, and TAYLOR_WEIGHT on the gap. |
| 1596 | 7 | `static double rule052(double inflation, double target, DebtManager.Strictness s)` | The rule at a step as it was from 0.7.52 to 0.7.80, written out: struck at the aim, on the gap past the band either side of it, at the step's weight. |
| 1605 | 1 | `static boolean bits(double a, double b)` | Equal to the bit (a NaN as any NaN). |
| 1657 | 7 | `static void worldPricesFall(Game g, double fall)` | A fall in the world's price level by `fall` (a fraction of it), at once: the level and the newest of the thirteen months it keeps (WorldEconomy.toSaveArray(), the level first, the ring oldest first, then the mean and ... |
| 1671 | 17 | **type** `static final class LooseMonths` | The very loose twin's months by the side of its target the rule read (0.7.81): in a month under it the rate is Standard's, to the bit; in one over it, money cheaper than Standard's rule at that reading - inside the ba... |
| 1675 | 12 | `void take(Game g, double read, double target, int month)` _(in CentralBankCheck.LooseMonths)_ | One month taken: `read` is what the autopilot read at the top of it (Game.nextMonth(), THE AUTOPILOT), the dial what the rule set. |
| 1689 | 317 | `static void howStrict() throws Exception` |  |
| 2096 | 3 | `static Game probeCity(GameFiles files)` | The probe city: the playtest's founding (LongPlaytest.founding()) in its seed 7's shape since FR7 - 43 houses, then four months before the next 20 (seed 5's, 41 and four months, from 0.7.139; seed 8's, 40 and five mon... |
| 2104 | 43 | `static Game shapedCity(GameFiles files, int houses, int months)` | A city founded in the given shape - `houses` before the next twenty, `months` played before them - and played as the probe city is (probeCity()) to STRICT_BRANCH. |
| 2149 | 3 | `static void playTo(Game g, int months)` | Up to `months` of the playtest's months, never past STRICT_BRANCH. |
| 2154 | 9 | `static void strictMonth(Game g)` | One of a twin's months: the playtest's month, with nothing built. |
| 2165 | 4 | **type** `record CityGap(int houses, int months, long people, double strict, double standard)` | One city's very strict and Standard inflation a year over STRICT_HORIZON, and its people at the branch. |
| 2167 | 1 | `double gap()` _(in CentralBankCheck.CityGap)_ | How far under Standard very strict ran, in a year's inflation: negative when it held prices lower. |
| 2171 | 19 | `static double[] strictAgainstStandard(Game city, GameFiles files)` | The city saved at its branch, then its very strict and its Standard twin played STRICT_HORIZON months: their inflation a year, {strict, standard}. |
| 2192 | 1 | `static int strictNeed(int cities)` | How many of `cities` must hold the claim: STRICT_SHARE of them, rounded up. |
| 2195 | 6 | `static double median(double[] xs)` | The median of `xs`, of a sorted copy: the mean of the two middle values when there is an even number. |
| 2203 | 5 | `static boolean strictHolds(double[] gaps)` | Whether very strict held prices lower than Standard over the set: the median gap under zero and at least strictNeed() of the gaps under it. |

