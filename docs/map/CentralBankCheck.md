# CentralBankCheck.java - 2,812 lines · 55 methods · 17 constants · harnesses

`ham/citybuildersim/CentralBankCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

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
>      the toggle survives a save - as does a dial at 0%. (FS10: on a year of
>      prices restored to the index from a save array, not played for.)
>   8. A reform scales the money figures and not the ratios.
>   9. Everything above survives a save.
>  10. A save from before the central bank founds one empty and runs, a save
>      carrying a note runs it off, and a new game after a load founds a
>      fresh bank. (FS10: the note is sold to the households of a town whose
>      cells shop, standTheTown().)
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
> ... (134 more lines in the source)

**Uses:** [DebtManager](DebtManager.md) (130), [Game](Game.md) (102), [Debt](Debt.md) (77), [CentralBank](CentralBank.md) (39), [LongPlaytest](LongPlaytest.md) (29), [PriceLevel](PriceLevel.md) (24), [GameFiles](GameFiles.md) (21), [Bank](Bank.md) (16), [Rollover](Rollover.md) (15), [TreasuryLine](TreasuryLine.md) (9), [MoneyAudit](MoneyAudit.md) (8), [Expectations](Expectations.md) (8), [PriceIndex](PriceIndex.md) (6), [HouseholdBalance](HouseholdBalance.md) (5), [MediumTermBond](MediumTermBond.md) (5), [TreasuryJournal](TreasuryJournal.md) (4), [SectorBooks](SectorBooks.md) (3), [SectorStatements](SectorStatements.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Household](Household.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [LongTermBond](LongTermBond.md) (2), [WorldEconomy](WorldEconomy.md) (2), [PolicyPreview](PolicyPreview.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Buyback](Buyback.md) (1), [Sector](Sector.md) (1), [Founding](Founding.md) (1), [Equity](Equity.md) (1), [Sectors](Sectors.md) (1)... and 1 more

## Sections

| line | section |
|---:|---|
| 293 | ONE MONTH, AUDITED - the identity section 1 asserts, held on every |
| 347 | · 2. reserves earn the policy rate |
| 380 | · 3. the window |
| 431 | · 4. the city's paper |
| 460 | · the city |
| 480 | · 1 and 5. every kind of flow |
| 614 | · 6. the ceiling and the arrears |
| 736 | · 7. the autopilot |
| 787 | · 9. the save |
| 818 | · 8. a currency reform |
| 842 | · 10. an old save |
| 896 | 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. |
| 1084 | 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road |
| 1185 | 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt |
| 1281 | 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a |
| 1619 | 22. THE CENTRAL BANK'S PAR ROLLED INTO A LINE THE HOUSEHOLDS HOLD PART |
| 1866 | 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a |
| 2021 | 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 212 | `CentralBankCheck.YEAR_RISE` | `.10` | How far a year of prices has risen in section 7's fixture: well over the rule's own target, so the rule answers it with something other than the dial it is put at. |
| 298 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the ...` |  |
| 301 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1289 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |
| 1292 | `CentralBankCheck.SERIAL_OF_SURPLUS` | `.10` | The face of section 20's serial against last year's unused surplus: a tenth of it, small against the surplus the town ran. |
| 1326 | `CentralBankCheck.ESTATE_OF_PAPER` | `.001` | What a month's dead leave of the city's paper, as a share of what the households hold of it: an estate the fixture hands to the treasury's custody. |
| 1630 | `CentralBankCheck.LINE_YEARS` | `2` | The term of the fixture's bonds, in years: the shortest term bond the lineup sells, so the city's own sales build the whole fixture in two years. |
| 1633 | `CentralBankCheck.TAKE_ALL_MONTHS` | `14` | The most months the dial at all of the paper is given to take the whole of the first bond into the central bank's book (HoldersCheck 11 reaches it in a year or so). |
| 1670 | `CentralBankCheck.RATES_UP` | `.04` | How far the policy rate is put up after the line is sold, so that the line stands at the coupon of lower rates and its reopening comes in under par (HoldersCheck 12 puts it up the same four points). |
| 2051 | `CentralBankCheck.STRICT_BRANCH` | `600` | The month the probe city leaves Standard at: a mature city, the inflation ensemble's month for its policies. |
| 2054 | `CentralBankCheck.STRICT_HORIZON` | `240` | How long each of its twins plays on from there: twenty years, the inflation ensemble's window after its month 600. |
| 2064 | `CentralBankCheck.STRICT_WORLD_FALL` | `.20` | How far the world's prices fall, in the first month of the fourth game's twenty years (worldPricesFall): the cause of the very loose twin's months under its target, which the probe city's own path gave it in some buil... |
| 2067 | `CentralBankCheck.STRICT_FALL_WINDOW` | `24` | The months after the fall in which the fixture needs the very loose twin under its target: the fall's own, before the currency has answered. |
| 2070 | `CentralBankCheck.PROBE_HOUSES` | `43` | The probe city's houses before the next twenty: seed 7's shape since FR7 (probeCity()). |
| 2073 | `CentralBankCheck.PROBE_MONTHS` | `4` | ...and the months it plays before them, seed 7's four. |
| 2081 | `CentralBankCheck.STRICT_SHAPES` | `{ { 40, 3, 20 }, { 41, 3, 20 }, { 42, 3, 20 }, { 43, 3, 20 }, { 40, 4, 20 }, ...` | The founding shapes very strict is measured against Standard in: the playtest's seeds 0 to 23, with 40 + seed % 4 houses, 3 + (seed / 4) % 3 months before the next 20 + (seed / 12) % 3 houses. |
| 2094 | `CentralBankCheck.STRICT_SHARE` | `.75` | The share of STRICT_SHAPES' cities in which very strict must hold prices lower than Standard: three in four, retained after CB2 measured all 24 shapes at month 600 for 240 months on RT2 main (0.9.113), with all 24 hol... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 209 | `static int fails` |  |
| 214 | `static PrintStream out` |  |
| 215 | `static PrintStream quiet` |  |
| 302 | `static int monthsPlayed, monthsBroken, mostKindsInAMonth` |  |
| 303 | `static double worstResidual` |  |
| 2119 | `int under, underSoon, underAsStandard, over, overCheaper, past` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 207 | 2606 | **type** `public class CentralBankCheck` | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its two dials - the holdings (0.7.1) and the advances ceiling (0.7.2). |
| 217 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 222 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 232 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 239 | 4 | `static void quietly(Runnable r)` |  |
| 251 | 12 | `static void standTheTown(Game city, int months)` | The town sections 1-10 and 22 stand on, built to have households in cells that shop: the roads, shops, bakery and bank all standing at once, as the roads always did (FS10). |
| 265 | 8 | `static double[] householdsInCells(Game city)` | The households of the cells that settle (at or over HouseholdBalance.EMPTY_CELL) and of all of them. |
| 279 | 13 | `static void assertTheCellsShop(String town, Game city)` | The fixture line of a town: its cells shop. |

### ONE MONTH, AUDITED - the identity section 1 asserts, held on every (lines 293-895)

| line | len | member | says |
|---:|---:|---|---|
| 305 | 10 | `static int kindsThisMonth(CentralBank cb)` |  |
| 317 | 24 | `static int play(Game g)` | Plays a month and holds it to the identities. |
| 342 | 553 | `public static void main(String[] args) throws Exception` |  |

### 11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own. (lines 896-1083)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 3 | `static double shape20(DebtManager m)` | The long end's shape: the premium at twenty years less what the holdings compress - the twenty-year rate less the dial the market expects over those years and the credit spread (the curve taken apart, CD1). |
| 912 | 171 | `static void theHoldingsDial(GameFiles files) throws Exception` |  |

### 16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road (lines 1084-1184)

| line | len | member | says |
|---:|---:|---|---|
| 1090 | 86 | `static void theCeilingDial(GameFiles files) throws Exception` |  |
| 1178 | 6 | `static double journalAmount(Game g, String label)` | The amount on last month's journal line with this label, or 0. |

### 17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt (lines 1185-1280)

| line | len | member | says |
|---:|---:|---|---|
| 1191 | 27 | `static void theWholeBook()` |  |
| 1219 | 61 | `static void theSplitFloor()` |  |

### 19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a (lines 1281-1618)

| line | len | member | says |
|---:|---:|---|---|
| 1295 | 6 | `static Debt fixtureSerial(Game g, int soldIn)` | The serial the fixture sold: the one piece of the city's paper that is a serial and started before the month the fixture saved in. |
| 1303 | 7 | `static java.util.List<Debt> soldAtTheLastPress(Game g)` | The city's own paper sold at the last press or between it and the one before - started the month before this one. |
| 1312 | 3 | `static double issuePricePerFace(Debt d)` | What the central bank paid a unit of face for a piece at its issue: what the treasury was paid for it over its face, which the add-on does not move. |
| 1317 | 7 | `static Game toTheSlice(GameFiles files, int soldIn)` | The fixture as saved, loaded fresh, and played to the gap before its first slice. |
| 1341 | 8 | `static void anEstateWithNoHeir(Game g)` | FC2, 2026-11-23: an estate of the city's own paper that no household inherits, handed to the treasury's custody between two presses, as HouseholdBalance's census does when a death leaves no working heir (passOn(); Cap... |
| 1350 | 259 | `static void theRolloverAtIssue() throws Exception` |  |
| 1611 | 7 | `static void atItsHoldingsDial(Game g)` | Cause the rollover's at-dial premise on the actual book, as section22's held-line fixture does. |

### 22. THE CENTRAL BANK'S PAR ROLLED INTO A LINE THE HOUSEHOLDS HOLD PART (lines 1619-1865)

| line | len | member | says |
|---:|---:|---|---|
| 1636 | 5 | `static Debt pieceSince(DebtManager ledger, java.util.List<Debt> before)` | The city's own piece that is on the book now and was not before: the one a sale made. |
| 1643 | 4 | **type** `record LineNow(double face, double[] discounts, double bankFace, double householdFace, double centralBankFa...` | A line as the section reads it at one moment: its face, each holder's own discount (Debt.Holder order), the bank's face of it, and the bank's book and unearned discount over all the paper. |
| 1645 | 1 | `double of(Debt.Holder h)` _(in CentralBankCheck.LineNow)_ |  |
| 1648 | 9 | `static LineNow lineNow(Game g, Debt line)` |  |
| 1659 | 1 | **type** `record RolledMonth(LineNow going, LineNow rolled)` | A month played, with the line read going in and read once the central bank's face has joined it: after the roll and the holdings step, before the households' settle and the month's accretion (Game.settleProbeForTest). |
| 1661 | 7 | `static RolledMonth playRolling(Game g, Debt line)` |  |
| 1676 | 10 | `static Game atTheRollPress(GameFiles files)` | The saved fixture, loaded fresh: the press at which the first bond's last month is paid, with the rates up and the curve struck on them, and the treasury's cash at nothing so that no surplus nets the central bank's par. |
| 1688 | 7 | `static Debt fallingDue(Game g)` | The first bond: the term bond of the fixture's years that falls due next month. |
| 1697 | 4 | `static boolean soldBetweenPresses(Game g)` | Whether any of the city's term paper was sold between the presses and has yet to settle: what the central bank's par is added on to. |
| 1702 | 104 | `static void theParRolledIntoAHeldLine() throws Exception` |  |
| 1808 | 57 | `static void aboutTheRoll(String how, Game g, Debt line, RolledMonth m, double par, String saleReads)` | What the central bank's par did to the line in the month it joined it, read at the probe, and what the month left of the bank's books. |

### 20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a (lines 1866-2020)

| line | len | member | says |
|---:|---:|---|---|
| 1873 | 147 | `static void theSurplusPaysEveryone() throws Exception` |  |

### 21. HOW STRICT (0.7.52) - the dial beside the target (DebtManager, (lines 2021-2812)

| line | len | member | says |
|---:|---:|---|---|
| 2029 | 4 | `static double oldRule(double inflation, double target)` | The rule as it was until 0.7.51, written out: the neutral rate, the target's distance from the default, and TAYLOR_WEIGHT on the gap. |
| 2039 | 7 | `static double rule052(double inflation, double target, DebtManager.Strictness s)` | The rule at a step as it was from 0.7.52 to 0.7.80, written out: struck at the aim, on the gap past the band either side of it, at the step's weight. |
| 2048 | 1 | `static boolean bits(double a, double b)` | Equal to the bit (a NaN as any NaN). |
| 2104 | 7 | `static void worldPricesFall(Game g, double fall)` | A fall in the world's price level by `fall` (a fraction of it), at once: the level and the newest of the thirteen months it keeps (WorldEconomy.toSaveArray(), the level first, the ring oldest first, then the mean, the... |
| 2118 | 17 | **type** `static final class LooseMonths` | The very loose twin's months by the side of its target the rule read (0.7.81): in a month under it the rate is Standard's, to the bit; in one over it, money cheaper than Standard's rule at that reading - inside the ba... |
| 2122 | 12 | `void take(Game g, double read, double target, int month)` _(in CentralBankCheck.LooseMonths)_ | One month taken: `read` is what the autopilot read at the top of it (Game.nextMonth(), THE AUTOPILOT), the dial what the rule set. |
| 2136 | 324 | `static void howStrict() throws Exception` |  |
| 2569 | 10 | `static Game probeCity(GameFiles files)` | The probe city: the playtest's founding (LongPlaytest.founding()) in its seed 7's shape since FR7 - 43 houses, then four months before the next 20 (seed 5's, 41 and four months, from 0.7.139; seed 8's, 40 and five mon... |
| 2584 | 3 | `static Game shapedCity(GameFiles files, int houses, int months)` | A city founded in the given shape - `houses` before the next twenty, `months` played before them - and played as the probe city is (probeCity()) to STRICT_BRANCH. |
| 2589 | 3 | `static Game shapedCity(GameFiles files, int houses, int months, boolean causesHeldOff)` | ...with the causes of 1900 held off from the first month where asked (probeCity()). |
| 2594 | 44 | `static Game shapedCity(GameFiles files, int houses, int months, boolean causesHeldOff, int laterHouses)` | ...with the second House tranche of the playtest's seed, preserving the first twelve cities at twenty. |
| 2640 | 3 | `static void playTo(Game g, int months)` | Up to `months` of the playtest's months, never past STRICT_BRANCH. |
| 2645 | 9 | `static void strictMonth(Game g)` | One of a twin's months: the playtest's month, with nothing built. |
| 2656 | 4 | **type** `record CityGap(int houses, int months, int laterHouses, long people, double strict, double standard)` | One city's very strict and Standard inflation a year over STRICT_HORIZON, and its people at the branch. |
| 2658 | 1 | `double gap()` _(in CentralBankCheck.CityGap)_ | How far under Standard very strict ran, in a year's inflation: negative when it held prices lower. |
| 2662 | 19 | `static double[] strictAgainstStandard(Game city, GameFiles files)` | The city saved at its branch, then its very strict and its Standard twin played STRICT_HORIZON months: their inflation a year, {strict, standard}. |
| 2683 | 1 | `static int strictNeed(int cities)` | How many of `cities` must hold the claim: STRICT_SHARE of them, rounded up. |
| 2686 | 6 | `static double median(double[] xs)` | The median of `xs`, of a sorted copy: the mean of the two middle values when there is an even number. |
| 2694 | 5 | `static boolean strictHolds(double[] gaps)` | Whether very strict held prices lower than Standard over the set: the median gap under zero and at least strictNeed() of the gaps under it. |
| 2708 | 28 | `static void strictRuleHalf()` | Very strict's half of "it holds prices lower than Standard" (CB2's median and share over STRICT_SHAPES, FS10): the rule's own, on ruleRate() at every target, from both sides of the reading where very strict's line and... |
| 2738 | 5 | `static double crossing(double target)` | The reading at which very strict's rule and Standard's set the same rate at `target`, from the constants (strictRuleHalf()). |
| 2750 | 13 | `static void noSpiralHalf(DebtManager.Strictness s)` | "No spiral" at an end of the dial (FS10): the rule's half of "its last year is within a full miss of the target". |
| 2769 | 21 | `static double[] trustOnAPath(DebtManager.Strictness s, double annual)` | A bank at `s` over STRICT_HORIZON months of a basket rising `annual` a year (FS10): a bare PriceIndex, Expectations and DebtManager, the bank setting its rule's advice each month as the autopilot does (Game.nextMonth(... |
| 2796 | 16 | `static void looseCostsTrust()` | "Very loose costs trust, through the lean measured against holding the target" (FS10): on a basket rising inside very loose's band and over the tolerance, Standard's rate is what holding the target takes (lean all the... |

