# HoldersCheck.java - 1,717 lines · 25 methods · 2 constants · harnesses

`ham/citybuildersim/HoldersCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> Proves who holds the city's own paper (0.7.1): that the households buy it at
> the settle, are paid on it, sell it back, and are paid when it is bought
> back - every crossing declared, and every holding exactly where the paper
> says it is. Not part of the game.
> 
> WHY THIS EXISTS. Jerus: "yes households should be able to hold." Until
> 0.7.1 the commercial bank held every dollar of the city's paper, so a bond
> was a loan from the city's own bank with extra steps. Now a piece of paper
> carries what the households and the central bank hold of it, and the
> households carry their paper as a fourth asset. Two books that must agree
> to the dollar, and a set of crossings - households are outside the money
> audit's pools - each of which is a dollar from nowhere if it is not
> declared. So each is caused and asserted:
> 
>   1. At the settle the households take the share the dials give them of an
>      issue yielding well above the deposit rate; their savings fall by
>      exactly what they paid, the bank pays exactly the rest, the paper's
>      household share is the cells' paper summed, and the audit closes.
>   2. The next month's coupon reaches them in their share, the bank's in
>      its, and the treasury's books carry the whole of it.
>   3. A household short of money sells its paper before its shares, and its
>      shares only once the paper is gone.
>   4. A household selling after the curve has risen gets less than face,
>      and the bank books the gain against what it carries the paper at.
>   5. A buyback pays every holder its share - the bank, the households, the
>      central bank - and the next month declares what left the pools.
>   6. Save and reload, and every holding is exactly where it was.
>   7. A save from before the holders (no cell slot, no fields on the paper)
>      loads with the bank holding everything, and runs.
>   8. A dollar bond bought back is money leaving the country: none of the
>      price reaches the bank or the households, and the next month declares
>      all of it abroad (until 0.7.1 it left the treasury for nowhere).
>   9. The holdings dial at the whole of the paper (0.7.15, Jerus: "central
>      bank bond holding can ve 100% if one wants"): the central bank buys
>      the bank's term paper first, and only once the bank has none left
>      the households', at the curve's market value, their face onto its
>      book and the price made for them - the audit closing and M0 moving by
>      exactly the money made - until it holds all of it; a save and load
>      in the middle keeps the dial, the holdings and the pace, and both
>      cities buy the same from the households the month after.
> 
> 10. The businesses are a fourth holder (CD2, slice 4, D33): the auction
>     allots them a share of the sale; at the settle each sector pays its
>     share out of its own till, in proportion to its own bid at the stop and
>     never past the till, and the bank takes what a till could not pay; the
>     coupon, the principal and a buyback's price arrive in the tills; they
>     sell to the bank's desk when the spread has gone, when a sector
>     borrows and when it wants the money; the central bank reaches them last
>     when its dial is the whole of the paper; and at every step the sectors'
>     paper is the paper's businesses' share, exactly, the audit closing
>     (a till and the bank are both pools, so nothing is declared), through a
>     save and load, and an old save loads with none.
> 
> 11. The world is a fifth holder (CD2, slice 5, D7; star 2): the auction
>     allots it a share of the sale by CapitalFlows' rule, and at the settle
>     it pays from abroad - a financial inflow the audit declares, the bank's
>     settle shrinking by it; its coupon leaves as income and its principal
>     (and a buyback's price) as a financial outflow, both declared; it
>     leaves with its money, a share of the paper sold to the bank's desk when
>     the hot money departs and PANIC_EXIT of it in a run, when it also bids
> ... (21 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (49), [Game](Game.md) (45), [Debt](Debt.md) (32), [DebtManager](DebtManager.md) (26), [CapitalFlows](CapitalFlows.md) (18), [Bank](Bank.md) (15), [GameFiles](GameFiles.md) (10), [Sectors](Sectors.md) (10), [OutwardInvestment](OutwardInvestment.md) (8), [HouseholdBalance](HouseholdBalance.md) (7), [CentralBank](CentralBank.md) (7), [Founding](Founding.md) (5), [Household](Household.md) (5), [Buyback](Buyback.md) (3), [Auction](Auction.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [WorkingHousehold](WorkingHousehold.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [FamilyStructure](FamilyStructure.md) (1), [PayTier](PayTier.md) (1), [SectorBooks](SectorBooks.md) (1), [EconomyManager](EconomyManager.md) (1)

## Sections

| line | section |
|---:|---|
| 190 | · 1. at the settle |
| 250 | · 2. the coupon |
| 264 | · 3. the waterfall |
| 297 | · 4. selling after the curve rose |
| 327 | · 5. a buyback |
| 373 | · 6. the save |
| 416 | · 7. an old save |
| 477 | · 8. a dollar bond bought back |
| 516 | 9. the whole of the paper (0.7.15) |
| 683 | 10. the businesses (CD2, slice 4, D33) |
| 1144 | 11. the world (CD2, slice 5, D7; star 2) |
| 1547 | 12. the pooled discount re-struck (BK1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1168 | `HoldersCheck.CENT` | `.0051` | The audit's tolerance for a figure read to the cent. |
| 1607 | `HoldersCheck.CITY_FIGURES` | `{ "net income", "tax", "dividends", "gains on paper", "the treasury's cash", ...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `static int fails` |  |
| 95 | `static PrintStream out` |  |
| 96 | `static PrintStream quiet` |  |
| 134 | `static int closedMonths, brokenMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1626 | **type** `public class HoldersCheck` | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is bought back - every crossing declared, and every holding exactly where the... |
| 98 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 103 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 113 | 4 | `static void quietly(Runnable r)` |  |
| 118 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 125 | 1 | `static double savings(Game g)` |  |
| 128 | 5 | `static boolean booksAgree(Game g)` | The two books of the households' paper agree: the cells, and the paper. |
| 137 | 10 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit. |
| 148 | 367 | `public static void main(String[] args) throws Exception` |  |

### 9. the whole of the paper (0.7.15) (lines 516-682)

| line | len | member | says |
|---:|---:|---|---|
| 519 | 5 | `static Debt termPiece(DebtManager ledger)` | The one piece of term paper a city holds, or null. |
| 525 | 157 | `static void theWholeBook() throws Exception` |  |

### 10. the businesses (CD2, slice 4, D33) (lines 683-1143)

| line | len | member | says |
|---:|---:|---|---|
| 686 | 5 | `static boolean firmsAgree(Game g)` | The two books of the businesses' paper agree: the sectors', and the paper's. |
| 693 | 5 | `static Debt newPiece(DebtManager ledger, java.util.List<Debt> before)` | The domestic piece on the books that was not there before. |
| 700 | 5 | `static double[] tills(Game g)` | Every sector's till, in Sectors.KEYS order. |
| 707 | 5 | `static double[] holdings(Game g)` | Every sector's paper, in Sectors.KEYS order. |
| 714 | 8 | `static boolean booksExplain(Game g, java.util.List<String> sectors)` | True when these sectors' cash flow statements explain their tills to the cent this month (SectorBooks.SectorMonth.unexplained()). |
| 724 | 3 | `static void near(String label, double actual, double expected)` | One close() with the tolerance a part in a billion of the figure, or a cent. |
| 728 | 415 | `static void theBusinesses() throws Exception` |  |

### 11. the world (CD2, slice 5, D7; star 2) (lines 1144-1546)

| line | len | member | says |
|---:|---:|---|---|
| 1147 | 5 | `static boolean worldAgrees(Game g)` | The two books of the world's paper agree: CapitalFlows', and the paper's. |
| 1154 | 12 | `static double auditLine(MoneyAudit.Result r, String label)` | One line of the month's audit by its label, to the cent: 0 when the month has none. |
| 1170 | 376 | `static void theWorld() throws Exception` |  |

### 12. the pooled discount re-struck (BK1) (lines 1547-1717)

| line | len | member | says |
|---:|---:|---|---|
| 1550 | 1 | **type** `record Reopening(Game city, Debt line, double splitStart)` | One city of section 12 (BK1): the city, the line it sold once and settled, and its split going in. |
| 1557 | 30 | `static Reopening reopeningFixture(String label, boolean restrikeOff) throws Exception` | Section 12's fixture, made twice: a city founded, built and played as the other sections' cities were, then a twenty-year bond sold between the presses and settled. |
| 1589 | 2 | **type** `record Reopened(double faceBefore, double faceAfter, double perFaceBefore, double perFaceAfter, double held...` | What the reopening did to a line: its face and its discount per unit of face either side, whether it is the same line, and what the households held going in. |
| 1593 | 13 | `static Reopened reopen(Game city, Debt line)` | The reopening, done the same way to the city and to its twin: the rates up four points and the same line sold again, to be settled the next month. |
| 1610 | 5 | `static double[] cityFigures(Game g)` | The figures of a month that the re-strike must not move (BK1): the bank's profit, tax, dividends and gains on the city's paper, the treasury's cash and the bank's equity. |
| 1617 | 6 | `static String firstDifference(double[] city, double[] twin)` | The first figure of the two that is not the same, bit for bit, as a sentence; empty when every one is. |
| 1624 | 93 | `static void theReopening() throws Exception` |  |

