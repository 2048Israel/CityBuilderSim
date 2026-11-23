# HoldersCheck.java - 3,046 lines · 66 methods · 9 constants · harnesses

`ham/citybuildersim/HoldersCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
> ... (81 more lines in the source)

**Uses:** [Debt](Debt.md) (158), [MoneyAudit](MoneyAudit.md) (70), [Game](Game.md) (66), [Sectors](Sectors.md) (41), [PriceLevel](PriceLevel.md) (35), [Bank](Bank.md) (32), [DebtManager](DebtManager.md) (31), [CapitalFlows](CapitalFlows.md) (22), [LongTermBond](LongTermBond.md) (17), [ShortSellingCheck](ShortSellingCheck.md) (15), [HouseholdBalance](HouseholdBalance.md) (14), [GameFiles](GameFiles.md) (14), [OutwardInvestment](OutwardInvestment.md) (11), [Household](Household.md) (9), [CentralBank](CentralBank.md) (7), [Founding](Founding.md) (6), [BusinessDebtManager](BusinessDebtManager.md) (6), [DebtQuote](DebtQuote.md) (5), [Buyback](Buyback.md) (4), [Auction](Auction.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [WorkingHousehold](WorkingHousehold.md) (2), [EconomyManager](EconomyManager.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [BusinessDebt](BusinessDebt.md) (2), [BuildingManager](BuildingManager.md) (1), [FamilyStructure](FamilyStructure.md) (1), [PayTier](PayTier.md) (1), [SectorBooks](SectorBooks.md) (1), [BondMarket](BondMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 302 | · 1. at the settle |
| 363 | · 2. the coupon |
| 388 | · 3. the waterfall |
| 423 | · 4. selling after the curve rose |
| 453 | · 5. a buyback |
| 499 | · 6. the save |
| 561 | · 7. an old save |
| 624 | · 8. a dollar bond bought back |
| 667 | 9. the whole of the paper (0.7.15) |
| 882 | 10. the businesses (CD2, slice 4, D33) |
| 1364 | 11. the world (CD2, slice 5, D7; star 2) |
| 1794 | 12. the pooled discount re-struck (BK1) |
| 2140 | 13. the discount released at an issue or a trade (BD1) |
| 2512 | 14. the rounding floor on a holder's face (RT1) |
| 2727 | 15. each holder's discount is its own (BK2) |
| 2911 | 16. what each sector owes (AP2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 209 | `HoldersCheck.SPARE_MONTHS` | `12` | How many months of its disposable income past its cushion every household is given to put into paper, so what they can spend on it is set, not found. |
| 1383 | `HoldersCheck.CENT` | `MoneyAudit.CENT` | The audit's tolerance for a figure read from it: the model's own cent, MoneyAudit.CENT (EP2). |
| 1797 | `HoldersCheck.MATERIAL_PART` | `.01` | A holder took part of a sale, and not the rounding left when the others paid in full, when it holds over this share of the face sold (GW1: the bank's 3.2e-14 of a face of 54 passed "took face" on noise). |
| 1803 | `HoldersCheck.HOUSEHOLDS_AT_FAIR` | `.75` | The households' share of an issue at the curve's fair yield, which splitTheSale() sets the deposit rate to give them: three quarters, so the bank, the step bidder at fair and the residual buyer, is left a quarter and ... |
| 1819 | `HoldersCheck.BILL_FLOORS_CARRIED` | `2` | How many times the least discount a bill can carry the bank is made to carry on 13c's first sale, so the bill it sells back is not bigger than what the bank carries (the accretion of the month in between takes a part ... |
| 2001 | `HoldersCheck.CITY_FIGURES` | `{ "net income", "tax", "dividends", "gains on paper", "the treasury's cash", ...` |  |
| 2170 | `HoldersCheck.PREMIUM_RISE` | `.04` | 13a's rise in the policy rate for the line's first sale, taken back off for its reopening. |
| 2914 | `HoldersCheck.MONTHS_FIRST` | `24, MONTHS_MOST = 120` | The months the section 16 city runs before the bank's book is looked at, and the most it may run on for the condition (two sectors in debt). |
| 2917 | `HoldersCheck.MONTHS_LOOKED` | `12` | The months after, one at a time, in each of which the figures are read again: a year, the book changing under them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 157 | `static int fails` |  |
| 158 | `static PrintStream out` |  |
| 159 | `static PrintStream quiet` |  |
| 225 | `static int closedMonths, brokenMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 155 | 2892 | **type** `public class HoldersCheck` | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is bought back - every crossing declared, and every holding exactly where the... |
| 161 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 166 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 172 | 4 | `static boolean fixture(String label, boolean ok)` | A fixture's assertion that says whether it held, so a section stops on a fixture that failed (counted) instead of reading past it. |
| 177 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 187 | 4 | `static void quietly(Runnable r)` |  |
| 192 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 199 | 1 | `static double savings(Game g)` |  |
| 202 | 5 | `static boolean booksAgree(Game g)` | The two books of the households' paper agree: the cells, and the paper. |
| 217 | 7 | `static void setSpare(HouseholdBalance hb)` | The households' savings set by hand, between two months, and never lowered: every cell holds at least its cushion (HouseholdBalance.SHARE_CUSHION_MONTHS of its disposable income) and SPARE_MONTHS more, so the househol... |
| 228 | 10 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit. |
| 239 | 427 | `public static void main(String[] args) throws Exception` |  |

### 9. the whole of the paper (0.7.15) (lines 667-881)

| line | len | member | says |
|---:|---:|---|---|
| 670 | 5 | `static Debt termPiece(DebtManager ledger)` | The one piece of term paper a city holds, or null. |
| 676 | 205 | `static void theWholeBook() throws Exception` |  |

### 10. the businesses (CD2, slice 4, D33) (lines 882-1363)

| line | len | member | says |
|---:|---:|---|---|
| 885 | 5 | `static boolean firmsAgree(Game g)` | The two books of the businesses' paper agree: the sectors', and the paper's. |
| 892 | 5 | `static Debt newPiece(DebtManager ledger, java.util.List<Debt> before)` | The domestic piece on the books that was not there before. |
| 899 | 5 | `static double[] tills(Game g)` | Every sector's till, in Sectors.KEYS order. |
| 906 | 5 | `static double[] holdings(Game g)` | Every sector's paper, in Sectors.KEYS order. |
| 913 | 8 | `static boolean booksExplain(Game g, java.util.List<String> sectors)` | True when these sectors' cash flow statements explain their tills to the cent this month (SectorBooks.SectorMonth.unexplained()). |
| 923 | 3 | `static void near(String label, double actual, double expected)` | One close() with the tolerance a part in a billion of the figure, or a cent. |
| 927 | 436 | `static void theBusinesses() throws Exception` |  |

### 11. the world (CD2, slice 5, D7; star 2) (lines 1364-1793)

| line | len | member | says |
|---:|---:|---|---|
| 1367 | 5 | `static boolean worldAgrees(Game g)` | The two books of the world's paper agree: CapitalFlows', and the paper's. |
| 1374 | 3 | `static double auditLine(MoneyAudit.Result r, String label)` | One line of the month's audit by its label, as the model recorded it (EP2: the figure itself, not the printed detail's two places): 0 when the month has none. |
| 1385 | 408 | `static void theWorld() throws Exception` |  |

### 12. the pooled discount re-struck (BK1) (lines 1794-2139)

| line | len | member | says |
|---:|---:|---|---|
| 1812 | 5 | `static void splitTheSale(Game city, double ask, int years)` | FS14: the split of the next sale between the households and the bank, caused by the dial that places it and not found in the played city. |
| 1829 | 11 | `static double firstSaleSize(Game city, double share, double billFloors)` | FS14: a first sale of this share of the households' purse, grown by a quarter at a time (so many times at most) until the model's own quote of it leaves the bank, the residual buyer, at least `billFloors` times the le... |
| 1842 | 1 | **type** `record Reopening(Game city, Debt line, double splitStart, Edge[] firstSettle)` | One city of section 12 (BK1): the city, the line it sold once and settled, its split going in, and the line at the two edges of that first settle (BK2: Edge, null if no line was sold). |
| 1849 | 3 | `static Reopening reopeningFixture(String label, boolean restrikeOff) throws Exception` | Section 12's fixture, made twice: a city founded, built and played as the other sections' cities were, then a twenty-year bond sold between the presses and settled. |
| 1854 | 3 | `static Reopening reopeningFixture(String label, boolean restrikeOff, double firstSaleRise) throws Exception` | ...its first sale made with the policy rate this much higher, so the line's coupon stands over the rates the city goes back to (13a, BD1). |
| 1859 | 3 | `static Reopening reopeningFixture(String label, boolean restrikeOff, double firstSaleRise, double firstSaleShare) throws Exception` | BK2b: a smaller first sale leaves less of the bank's own discount to cover the next sale's premium. |
| 1868 | 58 | `static Reopening reopeningFixture(String label, boolean restrikeOff, double firstSaleRise, double firstSaleShare, double billFl...` | FS14: ...and a first sale sized so that the bank, the residual buyer, carries on it at least this many times the least discount a bill can carry (firstSaleSize()); 0 for the share of the purse alone. |
| 1928 | 2 | **type** `record Reopened(double faceBefore, double faceAfter, double perFaceBefore, double perFaceAfter, double held...` | What the reopening did to a line: its face and its discount per unit of face either side, whether it is the same line, and what the households held going in. |
| 1932 | 17 | `static Reopened reopen(Game city, Debt line)` | The reopening, done the same way to the city and to its twin: the rates up four points and the same line sold again, to be settled the next month. |
| 1951 | 3 | `static void closeRel(String label, double actual, double expected, double rel)` | Two of the model's own figures agree to this much of the larger of them (BK2's relative tolerances: 1e-9 for sums of a line's pieces, 1e-12 for a rate per face). |
| 1956 | 4 | `static double perFace(Debt line, Debt.Holder h)` | This holder's own unaccreted discount per unit of the face it holds on the line (BK2); zero where it holds none. |
| 1967 | 26 | **type** `record Edge(double due, double unsettledFace, double unsettledDiscount, double discountLeft, double[] disco...` | A line at one edge of the settle (BK2), read through Game.settleProbeForTest: before the households take their share and after the bank has been given the rest, both ahead of the month's accretion, so each holder's ow... |
| 1969 | 10 | `static Edge of(Game city, Debt line)` _(in HoldersCheck.Edge)_ |  |
| 1979 | 1 | `double discountOf(Debt.Holder h)` _(in HoldersCheck.Edge)_ |  |
| 1980 | 1 | `double faceOf(Debt.Holder h)` _(in HoldersCheck.Edge)_ |  |
| 1982 | 1 | `double bankPaid(Edge after)` _(in HoldersCheck.Edge)_ | What the bank paid for its face at the settle: what the market owed for the sale, less what the three outside payers paid. |
| 1984 | 8 | `double paid(Debt.Holder h, Edge after)` _(in HoldersCheck.Edge)_ | What this outside holder paid at the settle. |
| 1995 | 5 | `static Edge[] watchSettle(Game city, Debt line)` | Watches the next month's settle of this line: the two edges are filled as the month plays, and the caller clears city.settleProbeForTest after it. |
| 2004 | 5 | `static double[] cityFigures(Game g)` | The figures of a month that the re-strike must not move (BK1): the bank's profit, tax, dividends and gains on the city's paper, the treasury's cash and the bank's equity. |
| 2011 | 6 | `static String firstDifference(double[] city, double[] twin)` | The first figure of the two that is not the same, bit for bit, as a sentence; empty when every one is. |
| 2018 | 121 | `static void theReopening() throws Exception` |  |

### 13. the discount released at an issue or a trade (BD1) (lines 2140-2511)

| line | len | member | says |
|---:|---:|---|---|
| 2143 | 1 | **type** `record Standing(double book, double carried, double restruck)` | What the book says the bank's unearned discount is, what the bank carries and what its movement has booked as re-struck, at a moment of section 13. |
| 2145 | 3 | `static Standing standing(Game g)` |  |
| 2150 | 1 | `static double movementTolerance(Bank b)` | Section 12's tolerance for the movement's residual, and for the split's. |
| 2151 | 1 | `static double splitTolerance(Bank b)` |  |
| 2154 | 7 | `static String linesApart(Game city, Game twin)` | The first of the statement's net income, tax and gains on the city's paper that is not the twin's, bit for bit, as a sentence; empty when all three are. |
| 2163 | 1 | **type** `record Premium(boolean sameLine, double face, double proceeds, double discountBefore, double discountAfter,...` | What the premium reopening did to a line: whether it is the same line, the face sold, what it raised, and the line's discount left either side. |
| 2173 | 17 | `static Premium reopenAtAPremium(Game city, Debt line)` | The reopening of 13a, done the same way to the city and to its twin: the policy rate taken back down by PREMIUM_RISE (to its floor, at most) and the same line sold again between the presses, at a premium. |
| 2191 | 8 | `static void theDiscountReleased() throws Exception` |  |
| 2201 | 88 | `static void thePremium() throws Exception` | 13a: the same line reopened at a premium between the presses: the premium is the unsettled sale's until the settle, which takes it off the bank's own discount on the line (BK2). |
| 2291 | 47 | `static void thePremiumPastOwnDiscount() throws Exception` | BK2b: the bank's premium exceeds its own discount; the clamp's loss belongs to retained, outside income. |
| 2340 | 89 | `static void theDeskOnAnUnsettledLine() throws Exception` | 13b: the desk buying the households' paper of a line with a sale unsettled is handed the households' own discount per face, which is what the book moves by (BK2). |
| 2431 | 2 | **type** `record UnsettledBuyback(boolean fresh, double face, double discount, double book, double carried, double ta...` | What 13c's round trip did: whether the piece was new and all unsettled, its face and discount, the bank's book and carried discount going into the buyback, what the buyback took and the bank's share of it, and whether... |
| 2435 | 4 | `static double billDiscount(Game city, double cash)` | BA1: the discount a year's bill raising this cash would carry, as the quote has it: its face less the cash it brings, the fixed fee of the issue in it. |
| 2441 | 23 | `static UnsettledBuyback sellAndBuyBackBeforeTheSettle(Game city)` | 13c's round trip, done the same way to the city and to its twin: a year's bill of a fifth of the bank's book sold between the presses and bought straight back whole, before its settle. |
| 2470 | 41 | `static void theBuybackBeforeTheSettle() throws Exception` | 13c: a fresh piece closed before its settle cancels its unpaid sale at the original proceeds (BS1). |

### 14. the rounding floor on a holder's face (RT1) (lines 2512-2726)

| line | len | member | says |
|---:|---:|---|---|
| 2515 | 1 | `static LongTermBond loosePiece()` | A piece of term paper by itself, sold to nobody: 1,000 of face, all of it the bank's, for the moves below to take from and give back to. |
| 2517 | 61 | `static void theHoldingFloor()` |  |
| 2580 | 35 | `static void theBuybackDiscountFloor(double scale)` | BK2b: every seller hands over all its discount when an ulp-short partial buyback snaps its kept face to none. |
| 2617 | 63 | `static void theHolderLedgers(double scale)` | Full pro-rata takes that leave positive rounding on main, with partial takes as a control (RT1b). |
| 2682 | 44 | `static void theFirmDeskSplit(double scale, double share)` | Run the actual desk through its wired recall, and compare two independently kept books, never a residual sum (RT1b). |

### 15. each holder's discount is its own (BK2) (lines 2727-2910)

| line | len | member | says |
|---:|---:|---|---|
| 2730 | 5 | `static double[] owned(Debt line)` | The five owners' unaccreted discount on a line, in Debt.Holder's order. |
| 2736 | 5 | `static double sum(double[] a)` |  |
| 2747 | 10 | `static LongTermBond ownedPiece()` | A loose piece of 1,000 sold for 900 and settled by hand, every holder at a price of its own: the households took 300 for 285 (their own discount 15), the businesses 100 for 96 (4), the world 50 for 47 (3), the bank is... |
| 2758 | 152 | `static void theEachHoldersOwn() throws Exception` |  |

### 16. what each sector owes (AP2) (lines 2911-3046)

| line | len | member | says |
|---:|---:|---|---|
| 2920 | 5 | `static int sectorsLent(BusinessDebtManager bank)` | How many sectors the bank's book holds a loan of. |
| 2927 | 6 | `static int mostLoansOfASector(BusinessDebtManager bank)` | The most loans the bank's book holds of any one sector: more than one, and the sum's order is a thing to keep. |
| 2934 | 97 | `static void theBook()` |  |
| 3033 | 13 | `static int sectorsApart(BusinessDebtManager bank, String[] first)` | How many sectors' getPrincipalsByKey() figures are not getPrincipal() to the bit; `first` takes a line on the first that is not. |

