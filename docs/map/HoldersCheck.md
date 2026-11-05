# HoldersCheck.java - 2,215 lines · 39 methods · 3 constants · harnesses

`ham/citybuildersim/HoldersCheck.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

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
> ... (47 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (60), [Game](Game.md) (54), [Debt](Debt.md) (48), [Bank](Bank.md) (30), [DebtManager](DebtManager.md) (29), [PriceLevel](PriceLevel.md) (29), [CapitalFlows](CapitalFlows.md) (18), [Sectors](Sectors.md) (15), [GameFiles](GameFiles.md) (10), [HouseholdBalance](HouseholdBalance.md) (8), [OutwardInvestment](OutwardInvestment.md) (8), [LongTermBond](LongTermBond.md) (8), [CentralBank](CentralBank.md) (7), [Founding](Founding.md) (5), [Household](Household.md) (5), [Buyback](Buyback.md) (4), [Auction](Auction.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [WorkingHousehold](WorkingHousehold.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [BuildingManager](BuildingManager.md) (1), [WantedPosts](WantedPosts.md) (1), [FamilyStructure](FamilyStructure.md) (1), [PayTier](PayTier.md) (1), [SectorBooks](SectorBooks.md) (1), [EconomyManager](EconomyManager.md) (1), [DebtQuote](DebtQuote.md) (1)

## Sections

| line | section |
|---:|---|
| 243 | · 1. at the settle |
| 303 | · 2. the coupon |
| 317 | · 3. the waterfall |
| 350 | · 4. selling after the curve rose |
| 380 | · 5. a buyback |
| 426 | · 6. the save |
| 470 | · 7. an old save |
| 532 | · 8. a dollar bond bought back |
| 573 | 9. the whole of the paper (0.7.15) |
| 770 | 10. the businesses (CD2, slice 4, D33) |
| 1239 | 11. the world (CD2, slice 5, D7; star 2) |
| 1661 | 12. the pooled discount re-struck (BK1) |
| 1856 | 13. the discount released at an issue or a trade (BD1) |
| 2150 | 14. the rounding floor on a holder's face (RT1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1267 | `HoldersCheck.CENT` | `.0051` | The audit's tolerance for a figure read to the cent: auditLine() reads a figure back from the audit's printed detail, which prints thousands to two places (MoneyAudit, "%,14.2f"), a step of 0.01 whatever the price lev... |
| 1745 | `HoldersCheck.CITY_FIGURES` | `{ "net income", "tax", "dividends", "gains on paper", "the treasury's cash", ...` |  |
| 1886 | `HoldersCheck.PREMIUM_RISE` | `.04` | 13a's rise in the policy rate for the line's first sale, taken back off for its reopening. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 120 | `static int fails` |  |
| 121 | `static PrintStream out` |  |
| 122 | `static PrintStream quiet` |  |
| 160 | `static int closedMonths, brokenMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 118 | 2098 | **type** `public class HoldersCheck` | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is bought back - every crossing declared, and every holding exactly where the... |
| 124 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 129 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 139 | 4 | `static void quietly(Runnable r)` |  |
| 144 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 151 | 1 | `static double savings(Game g)` |  |
| 154 | 5 | `static boolean booksAgree(Game g)` | The two books of the households' paper agree: the cells, and the paper. |
| 163 | 10 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit. |
| 174 | 398 | `public static void main(String[] args) throws Exception` |  |

### 9. the whole of the paper (0.7.15) (lines 573-769)

| line | len | member | says |
|---:|---:|---|---|
| 576 | 5 | `static Debt termPiece(DebtManager ledger)` | The one piece of term paper a city holds, or null. |
| 582 | 187 | `static void theWholeBook() throws Exception` |  |

### 10. the businesses (CD2, slice 4, D33) (lines 770-1238)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 5 | `static boolean firmsAgree(Game g)` | The two books of the businesses' paper agree: the sectors', and the paper's. |
| 780 | 5 | `static Debt newPiece(DebtManager ledger, java.util.List<Debt> before)` | The domestic piece on the books that was not there before. |
| 787 | 5 | `static double[] tills(Game g)` | Every sector's till, in Sectors.KEYS order. |
| 794 | 5 | `static double[] holdings(Game g)` | Every sector's paper, in Sectors.KEYS order. |
| 801 | 8 | `static boolean booksExplain(Game g, java.util.List<String> sectors)` | True when these sectors' cash flow statements explain their tills to the cent this month (SectorBooks.SectorMonth.unexplained()). |
| 811 | 3 | `static void near(String label, double actual, double expected)` | One close() with the tolerance a part in a billion of the figure, or a cent. |
| 815 | 423 | `static void theBusinesses() throws Exception` |  |

### 11. the world (CD2, slice 5, D7; star 2) (lines 1239-1660)

| line | len | member | says |
|---:|---:|---|---|
| 1242 | 5 | `static boolean worldAgrees(Game g)` | The two books of the world's paper agree: CapitalFlows', and the paper's. |
| 1249 | 12 | `static double auditLine(MoneyAudit.Result r, String label)` | One line of the month's audit by its label, to the cent: 0 when the month has none. |
| 1269 | 391 | `static void theWorld() throws Exception` |  |

### 12. the pooled discount re-struck (BK1) (lines 1661-1855)

| line | len | member | says |
|---:|---:|---|---|
| 1664 | 1 | **type** `record Reopening(Game city, Debt line, double splitStart)` | One city of section 12 (BK1): the city, the line it sold once and settled, and its split going in. |
| 1671 | 3 | `static Reopening reopeningFixture(String label, boolean restrikeOff) throws Exception` | Section 12's fixture, made twice: a city founded, built and played as the other sections' cities were, then a twenty-year bond sold between the presses and settled. |
| 1676 | 49 | `static Reopening reopeningFixture(String label, boolean restrikeOff, double firstSaleRise) throws Exception` | ...its first sale made with the policy rate this much higher, so the line's coupon stands over the rates the city goes back to (13a, BD1). |
| 1727 | 2 | **type** `record Reopened(double faceBefore, double faceAfter, double perFaceBefore, double perFaceAfter, double held...` | What the reopening did to a line: its face and its discount per unit of face either side, whether it is the same line, and what the households held going in. |
| 1731 | 13 | `static Reopened reopen(Game city, Debt line)` | The reopening, done the same way to the city and to its twin: the rates up four points and the same line sold again, to be settled the next month. |
| 1748 | 5 | `static double[] cityFigures(Game g)` | The figures of a month that the re-strike must not move (BK1): the bank's profit, tax, dividends and gains on the city's paper, the treasury's cash and the bank's equity. |
| 1755 | 6 | `static String firstDifference(double[] city, double[] twin)` | The first figure of the two that is not the same, bit for bit, as a sentence; empty when every one is. |
| 1762 | 93 | `static void theReopening() throws Exception` |  |

### 13. the discount released at an issue or a trade (BD1) (lines 1856-2149)

| line | len | member | says |
|---:|---:|---|---|
| 1859 | 1 | **type** `record Standing(double book, double carried, double restruck)` | What the book says the bank's unearned discount is, what the bank carries and what its movement has booked as re-struck, at a moment of section 13. |
| 1861 | 3 | `static Standing standing(Game g)` |  |
| 1866 | 1 | `static double movementTolerance(Bank b)` | Section 12's tolerance for the movement's residual, and for the split's. |
| 1867 | 1 | `static double splitTolerance(Bank b)` |  |
| 1870 | 7 | `static String linesApart(Game city, Game twin)` | The first of the statement's net income, tax and gains on the city's paper that is not the twin's, bit for bit, as a sentence; empty when all three are. |
| 1879 | 1 | **type** `record Premium(boolean sameLine, double face, double proceeds, double discountBefore, double discountAfter)` | What the premium reopening did to a line: whether it is the same line, the face sold, what it raised, and the line's discount left either side. |
| 1889 | 13 | `static Premium reopenAtAPremium(Game city, Debt line)` | The reopening of 13a, done the same way to the city and to its twin: the policy rate taken back down by PREMIUM_RISE (to its floor, at most) and the same line sold again between the presses, at a premium. |
| 1903 | 7 | `static void theDiscountReleased() throws Exception` |  |
| 1912 | 71 | `static void thePremium() throws Exception` | 13a: the same line reopened at a premium between the presses releases the bank's share of its discount. |
| 1985 | 81 | `static void theDeskOnAnUnsettledLine() throws Exception` | 13b: the desk buying the households' paper of a line with a sale unsettled is handed more or less discount than the book moves by. |
| 2068 | 2 | **type** `record UnsettledBuyback(boolean fresh, double face, double discount, double book, double carried, double ta...` | What 13c's round trip did: whether the piece was new and all unsettled, its face and discount, the bank's book and carried discount going into the buyback, what the buyback took and the bank's share of it, and whether... |
| 2072 | 4 | `static double billDiscount(Game city, double cash)` | BA1: the discount a year's bill raising this cash would carry, as the quote has it: its face less the cash it brings, the fixed fee of the issue in it. |
| 2078 | 23 | `static UnsettledBuyback sellAndBuyBackBeforeTheSettle(Game city)` | 13c's round trip, done the same way to the city and to its twin: a year's bill of a fifth of the bank's book sold between the presses and bought straight back whole, before its settle. |
| 2108 | 41 | `static void theBuybackBeforeTheSettle() throws Exception` | 13c, the gate's finding on BD1: a piece bought back whole before its settle is re-struck by nothing. |

### 14. the rounding floor on a holder's face (RT1) (lines 2150-2215)

| line | len | member | says |
|---:|---:|---|---|
| 2153 | 1 | `static LongTermBond loosePiece()` | A piece of term paper by itself, sold to nobody: 1,000 of face, all of it the bank's, for the moves below to take from and give back to. |
| 2155 | 60 | `static void theHoldingFloor()` |  |

