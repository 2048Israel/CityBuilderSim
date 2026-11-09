# CentralBankCheck.java - 2,516 lines · 44 methods · 14 constants · harnesses

`ham/citybuildersim/CentralBankCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

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
> ... (115 more lines in the source)

**Uses:** [Game](Game.md) (96), [DebtManager](DebtManager.md) (91), [Debt](Debt.md) (75), [CentralBank](CentralBank.md) (38), [LongPlaytest](LongPlaytest.md) (29), [PriceLevel](PriceLevel.md) (25), [GameFiles](GameFiles.md) (20), [Bank](Bank.md) (16), [Rollover](Rollover.md) (15), [TreasuryLine](TreasuryLine.md) (9), [MoneyAudit](MoneyAudit.md) (8), [MediumTermBond](MediumTermBond.md) (5), [TreasuryJournal](TreasuryJournal.md) (4), [SectorBooks](SectorBooks.md) (3), [SectorStatements](SectorStatements.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [LongTermBond](LongTermBond.md) (2), [WorldEconomy](WorldEconomy.md) (2), [PolicyPreview](PolicyPreview.md) (2), [Expectations](Expectations.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Buyback](Buyback.md) (1), [Sector](Sector.md) (1), [Founding](Founding.md) (1), [DecisionLog](DecisionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 221 | ONE MONTH, AUDITED - the identity section 1 asserts, held on every |
| 275 | · 2. reserves earn the policy rate |
| 308 | · 3. the window |
| 359 | · 4. the city's paper |
| 388 | · the city |
| 417 | · 1 and 5. every kind of flow |
| 551 | · 6. the ceiling and the arrears |
| 673 | · 7. the autopilot |
| 709 | · 9. the save |
| 740 | · 8. a currency reform |
| 764 | · 10. an old save |
| 818 | 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. |
| 1006 | 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road |
| 1107 | 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt |
| 1203 | 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a |
| 1467 | 22. THE CENTRAL BANK'S PAR ROLLED INTO A LINE THE HOUSEHOLDS HOLD PART |
| 1717 | 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a |
| 1853 | 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 226 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the ...` |  |
| 229 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1211 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |
| 1478 | `CentralBankCheck.LINE_YEARS` | `2` | The term of the fixture's bonds, in years: the shortest term bond the lineup sells, so the city's own sales build the whole fixture in two years. |
| 1481 | `CentralBankCheck.TAKE_ALL_MONTHS` | `14` | The most months the dial at all of the paper is given to take the whole of the first bond into the central bank's book (HoldersCheck 11 reaches it in a year or so). |
| 1518 | `CentralBankCheck.RATES_UP` | `.04` | How far the policy rate is put up after the line is sold, so that the line stands at the coupon of lower rates and its reopening comes in under par (HoldersCheck 12 puts it up the same four points). |
| 1883 | `CentralBankCheck.STRICT_BRANCH` | `600` | The month the probe city leaves Standard at: a mature city, the inflation ensemble's month for its policies. |
| 1886 | `CentralBankCheck.STRICT_HORIZON` | `240` | How long each of its twins plays on from there: twenty years, the inflation ensemble's window after its month 600. |
| 1896 | `CentralBankCheck.STRICT_WORLD_FALL` | `.20` | How far the world's prices fall, in the first month of the fourth game's twenty years (worldPricesFall): the cause of the very loose twin's months under its target, which the probe city's own path gave it in some buil... |
| 1899 | `CentralBankCheck.STRICT_FALL_WINDOW` | `24` | The months after the fall in which the fixture needs the very loose twin under its target: the fall's own, before the currency has answered. |
| 1902 | `CentralBankCheck.PROBE_HOUSES` | `43` | The probe city's houses before the next twenty: seed 7's shape since FR7 (probeCity()). |
| 1905 | `CentralBankCheck.PROBE_MONTHS` | `4` | ...and the months it plays before them, seed 7's four. |
| 1913 | `CentralBankCheck.STRICT_SHAPES` | `{ { 40, 3 }, { 41, 3 }, { 42, 3 }, { 43, 3 }, { 40, 4 }, { 41, 4 }, { 42, 4 }...` | The founding shapes very strict is measured against Standard in (CB1): the houses before the next twenty and the months played before them, as the playtest's seeds 0 to 11 found (LongPlaytest, ENSEMBLE MODE: 40 + seed... |
| 1922 | `CentralBankCheck.STRICT_SHARE` | `.75` | The share of STRICT_SHAPES' cities in which very strict must hold prices lower than Standard (CB1): EP1 found it in 11 of 12 shapes and FR7 in 15 of 16, so a city's path reverses it in about one city in twelve; three ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 190 | `static int fails` |  |
| 191 | `static PrintStream out` |  |
| 192 | `static PrintStream quiet` |  |
| 230 | `static int monthsPlayed, monthsBroken, mostKindsInAMonth` |  |
| 231 | `static double worstResidual` |  |
| 1947 | `int under, underSoon, underAsStandard, over, overCheaper, past` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 188 | 2329 | **type** `public class CentralBankCheck` | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its two dials - the holdings (0.7.1) and the advances ceiling (0.7.2). |
| 194 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 199 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 209 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 216 | 4 | `static void quietly(Runnable r)` |  |

### ONE MONTH, AUDITED - the identity section 1 asserts, held on every (lines 221-817)

| line | len | member | says |
|---:|---:|---|---|
| 233 | 10 | `static int kindsThisMonth(CentralBank cb)` |  |
| 245 | 24 | `static int play(Game g)` | Plays a month and holds it to the identities. |
| 270 | 547 | `public static void main(String[] args) throws Exception` |  |

### 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. (lines 818-1005)

| line | len | member | says |
|---:|---:|---|---|
| 830 | 3 | `static double shape20(DebtManager m)` | The long end's shape: the premium at twenty years less what the holdings compress - the twenty-year rate less the dial the market expects over those years and the credit spread (the curve taken apart, CD1). |
| 834 | 171 | `static void theHoldingsDial(GameFiles files) throws Exception` |  |

### 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road (lines 1006-1106)

| line | len | member | says |
|---:|---:|---|---|
| 1012 | 86 | `static void theCeilingDial(GameFiles files) throws Exception` |  |
| 1100 | 6 | `static double journalAmount(Game g, String label)` | The amount on last month's journal line with this label, or 0. |

### 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt (lines 1107-1202)

| line | len | member | says |
|---:|---:|---|---|
| 1113 | 27 | `static void theWholeBook()` |  |
| 1141 | 61 | `static void theSplitFloor()` |  |

### 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a (lines 1203-1466)

| line | len | member | says |
|---:|---:|---|---|
| 1214 | 6 | `static Debt fixtureSerial(Game g, int soldIn)` | The serial the fixture sold: the one piece of the city's paper that is a serial and started before the month the fixture saved in. |
| 1222 | 7 | `static java.util.List<Debt> soldAtTheLastPress(Game g)` | The city's own paper sold at the last press or between it and the one before - started the month before this one. |
| 1231 | 3 | `static double issuePricePerFace(Debt d)` | What the central bank paid a unit of face for a piece at its issue: what the treasury was paid for it over its face, which the add-on does not move. |
| 1236 | 7 | `static Game toTheSlice(GameFiles files, int soldIn)` | The fixture as saved, loaded fresh, and played to the gap before its first slice. |
| 1244 | 222 | `static void theRolloverAtIssue() throws Exception` |  |

### 22. THE CENTRAL BANK'S PAR ROLLED INTO A LINE THE HOUSEHOLDS HOLD PART (lines 1467-1716)

| line | len | member | says |
|---:|---:|---|---|
| 1484 | 5 | `static Debt pieceSince(DebtManager ledger, java.util.List<Debt> before)` | The city's own piece that is on the book now and was not before: the one a sale made. |
| 1491 | 4 | **type** `record LineNow(double face, double[] discounts, double bankFace, double householdFace, double centralBankFa...` | A line as the section reads it at one moment: its face, each holder's own discount (Debt.Holder order), the bank's face of it, and the bank's book and unearned discount over all the paper. |
| 1493 | 1 | `double of(Debt.Holder h)` _(in CentralBankCheck.LineNow)_ |  |
| 1496 | 9 | `static LineNow lineNow(Game g, Debt line)` |  |
| 1507 | 1 | **type** `record RolledMonth(LineNow going, LineNow rolled)` | A month played, with the line read going in and read once the central bank's face has joined it: after the roll and the holdings step, before the households' settle and the month's accretion (Game.settleProbeForTest). |
| 1509 | 7 | `static RolledMonth playRolling(Game g, Debt line)` |  |
| 1524 | 10 | `static Game atTheRollPress(GameFiles files)` | The saved fixture, loaded fresh: the press at which the first bond's last month is paid, with the rates up and the curve struck on them, and the treasury's cash at nothing so that no surplus nets the central bank's par. |
| 1536 | 7 | `static Debt fallingDue(Game g)` | The first bond: the term bond of the fixture's years that falls due next month. |
| 1545 | 4 | `static boolean soldBetweenPresses(Game g)` | Whether any of the city's term paper was sold between the presses and has yet to settle: what the central bank's par is added on to. |
| 1550 | 107 | `static void theParRolledIntoAHeldLine() throws Exception` |  |
| 1659 | 57 | `static void aboutTheRoll(String how, Game g, Debt line, RolledMonth m, double par, String saleReads)` | What the central bank's par did to the line in the month it joined it, read at the probe, and what the month left of the bank's books. |

### 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a (lines 1717-1852)

| line | len | member | says |
|---:|---:|---|---|
| 1724 | 128 | `static void theSurplusPaysEveryone() throws Exception` |  |

### 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, (lines 1853-2516)

| line | len | member | says |
|---:|---:|---|---|
| 1861 | 4 | `static double oldRule(double inflation, double target)` | The rule as it was until 0.7.51, written out: the neutral rate, the target's distance from the default, and TAYLOR_WEIGHT on the gap. |
| 1871 | 7 | `static double rule052(double inflation, double target, DebtManager.Strictness s)` | The rule at a step as it was from 0.7.52 to 0.7.80, written out: struck at the aim, on the gap past the band either side of it, at the step's weight. |
| 1880 | 1 | `static boolean bits(double a, double b)` | Equal to the bit (a NaN as any NaN). |
| 1932 | 7 | `static void worldPricesFall(Game g, double fall)` | A fall in the world's price level by `fall` (a fraction of it), at once: the level and the newest of the thirteen months it keeps (WorldEconomy.toSaveArray(), the level first, the ring oldest first, then the mean, the... |
| 1946 | 17 | **type** `static final class LooseMonths` | The very loose twin's months by the side of its target the rule read (0.7.81): in a month under it the rate is Standard's, to the bit; in one over it, money cheaper than Standard's rule at that reading - inside the ba... |
| 1950 | 12 | `void take(Game g, double read, double target, int month)` _(in CentralBankCheck.LooseMonths)_ | One month taken: `read` is what the autopilot read at the top of it (Game.nextMonth(), THE AUTOPILOT), the dial what the rule set. |
| 1964 | 319 | `static void howStrict() throws Exception` |  |
| 2391 | 10 | `static Game probeCity(GameFiles files)` | The probe city: the playtest's founding (LongPlaytest.founding()) in its seed 7's shape since FR7 - 43 houses, then four months before the next 20 (seed 5's, 41 and four months, from 0.7.139; seed 8's, 40 and five mon... |
| 2406 | 3 | `static Game shapedCity(GameFiles files, int houses, int months)` | A city founded in the given shape - `houses` before the next twenty, `months` played before them - and played as the probe city is (probeCity()) to STRICT_BRANCH. |
| 2411 | 44 | `static Game shapedCity(GameFiles files, int houses, int months, boolean causesHeldOff)` | ...with the causes of 1900 held off from the first month where asked (probeCity()). |
| 2457 | 3 | `static void playTo(Game g, int months)` | Up to `months` of the playtest's months, never past STRICT_BRANCH. |
| 2462 | 9 | `static void strictMonth(Game g)` | One of a twin's months: the playtest's month, with nothing built. |
| 2473 | 4 | **type** `record CityGap(int houses, int months, long people, double strict, double standard)` | One city's very strict and Standard inflation a year over STRICT_HORIZON, and its people at the branch. |
| 2475 | 1 | `double gap()` _(in CentralBankCheck.CityGap)_ | How far under Standard very strict ran, in a year's inflation: negative when it held prices lower. |
| 2479 | 19 | `static double[] strictAgainstStandard(Game city, GameFiles files)` | The city saved at its branch, then its very strict and its Standard twin played STRICT_HORIZON months: their inflation a year, {strict, standard}. |
| 2500 | 1 | `static int strictNeed(int cities)` | How many of `cities` must hold the claim: STRICT_SHARE of them, rounded up. |
| 2503 | 6 | `static double median(double[] xs)` | The median of `xs`, of a sorted copy: the mean of the two middle values when there is an even number. |
| 2511 | 5 | `static boolean strictHolds(double[] gaps)` | Whether very strict held prices lower than Standard over the set: the median gap under zero and at least strictNeed() of the gaps under it. |

