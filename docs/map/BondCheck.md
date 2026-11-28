# BondCheck.java - 2,110 lines · 58 methods · 1 constants · harnesses

`ham/citybuildersim/BondCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it
> over the bank, who loses what in a default, what the bank charges for
> concentration, who buys and sells, and the save.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Cheapest, within the bank's
> limit", "No limit, just price it", "Bank notes rank first", "Order book for
> both"):
> 
>   1. THE BOND'S ARITHMETIC: priced at its own coupon it is worth its face;
>      its price falls as the yield rises; the coupons a month are a twelfth
>      of the coupon on the face each holder had, exactly; at maturity every
>      holder is paid its face.
>   2. BOOKBUILDING: the coupon is the lowest yield at which the bids fill the
>      issue; what the bids will not fill at a price no dearer than the loan
>      is a bank loan; and at a loan rate under every bid, all of it is.
>   3. THE CHOICE: a small amount goes to the bank, a large one to a bond,
>      and the crossover comes from the fixed cost, the underwriter's spread,
>      the loan's price and the book's yield alone - printed.
>   4. THE ORDER BOOK in a played month: after the market's step no book is
>      crossed, the sellers who waited are counted, and the audit closes
>      through months of trading. (The book's own rules are OrderBookCheck's.)
>   5. RECOVERIES BY INSTRUMENT (round 2, Jerus: "Real averages by type",
>      in place of round 1's "Bank notes rank first"): a slice takes a
>      loan's loss off the loans and a bond's off the bonds, whatever the
>      mix; the backstop on a sector with nothing left writes both off whole;
>      the loss reaches every holder class; the world's is declared across
>      the border; the allowance, a loan's price and a bond's value read the
>      same pair. And the recoveries against Moody's and S&P's ranges,
>      printed.
>   6. CONCENTRATION: Basel's IRB capital reproduces Basel's published risk
>      weights; a sector's charge rises with its share and falls as the book
>      diversifies; the Euler shares add back to the add-on; the charge is in
>      the loan's price through the capital charge, and the add-on in the
>      bank's requirement, its weight table still footing (BankCheck 13).
>   7. EACH PARTICIPANT on a fixture that causes it: a household bids when the
>      expected return beats the deposit rate, and a household short of money
>      sells into a resting bid - or waits; the bank bids only at or over its
>      loan-equivalent yield, never under its target, and asks to raise
>      capital; a company never bids for its own bonds; the world's purchase
>      is a financial inflow the currency reads and its coupons an income
>      outflow, and it does not bid while its money is running.
>   8. SAVE AND LOAD: bonds, holdings and resting orders round-trip and the
>      reloaded city plays the same month; a save from before 0.7.12 loads
>      with no bonds and plays. Each cell's own bonds round-trip by the
>      cell's name, and a round-1 save - one pool, a claim per cell - loads
>      into the cells by those claims.
>   9. EACH HOUSEHOLD TYPE TRADES (round 2, Jerus: "Each household type
>      trades"): a cell with money past its cushion bids and a cell holding
>      more than its money calls for asks, and they trade with each other -
>      a transfer inside the households that no pool line sees; and a cell
>      short of money sells into a rich cell's resting bid in the waterfall.
>  10. BUY ONLY WHAT IT CAN PAY FOR (round 6, Jerus: "Buy only what it can
>      pay for"; section 5d): a sector under its ceiling places its orders
>      whole; one over it and short of cash buys only what its cash and the
>      credit it can get cover, and does not default on the stock it did not
>      buy; the same sector still defaults on a bill it cannot avoid, and
>      buys nothing that month but the stock its suppliers will wait for,
>      all of it on their credit (0.7.44); the audit closes.
>  11. CAN'T PAY MEANS DEFAULT, IN PLAY (rounds 4 and 5; section 5c): a bank
>      short of capital still covers a healthy sector's short month - the
> ... (25 more lines in the source)

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (74), [BondMarket](BondMarket.md) (69), [CorporateBond](CorporateBond.md) (62), [Bank](Bank.md) (60), [OrderBook](OrderBook.md) (52), [Game](Game.md) (39), [Household](Household.md) (14), [PayTier](PayTier.md) (14), [Sectors](Sectors.md) (13), [HouseholdBalance](HouseholdBalance.md) (13), [MoneyAudit](MoneyAudit.md) (12), [Sector](Sector.md) (10), [EconomyManager](EconomyManager.md) (8), [FamilyStructure](FamilyStructure.md) (8), [Good](Good.md) (6), [PriceLevel](PriceLevel.md) (5), [Statement](Statement.md) (4), [OutwardInvestment](OutwardInvestment.md) (3), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (2), [CapitalFlows](CapitalFlows.md) (2), [Founding](Founding.md) (1), [BuildingManager](BuildingManager.md) (1), [LongPlaytest](LongPlaytest.md) (1), [TempDirs](TempDirs.md) (1), [PriceLevelCheck](PriceLevelCheck.md) (1), [BankCheck](BankCheck.md) (1), [Retail](Retail.md) (1), [BusinessLoan](BusinessLoan.md) (1), [SupplierCredit](SupplierCredit.md) (1)... and 2 more

**Used by (2):** [GroceryCheck](GroceryCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 243 | 1. THE ARITHMETIC |
| 283 | 2. BOOKBUILDING |
| 343 | 3. THE CHOICE |
| 391 | 7a. A BOND SOLD IN THE MONTH |
| 450 | 1b. THE COUPONS |
| 479 | 4. THE BOOK IN PLAY |
| 512 | 7. THE PARTICIPANTS |
| 745 | 5c. CAN'T PAY MEANS DEFAULT |
| 959 | 5d. BUY ONLY WHAT IT CAN PAY FOR |
| 1182 | 5. RECOVERIES BY INSTRUMENT |
| 1346 | 6. CONCENTRATION |
| 1453 | 8. SAVE AND LOAD |
| 1621 | 9. EACH HOUSEHOLD TYPE TRADES |
| 1739 | 9b. DEFER ONLY THE MONTH'S HOUSEHOLD FOLDS |
| 2006 | 1c. MATURITY |
| 2048 | 9c. THE KNOWN ORDER IN PLAY (AP8) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 144 | `BondCheck.ISSUER` | `Sectors.CONSTRUCTION` | The sector every played fixture's bond is issued by: sound, owing nothing, with plant to borrow against. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 105 | `static int fails` |  |
| 106 | `static PrintStream out` |  |
| 107 | `static PrintStream quiet` |  |
| 108 | `static int closedMonths, brokenMonths` |  |
| 957 | `static String interimSector` | The sector §5c lent an interim loan to, for the save's round trip (§8). |
| 1632 | `final HouseholdBalance hb` |  |
| 1633 | `final BondMarket bm` |  |
| 1634 | `final Household poor, rich` |  |
| 1635 | `final CorporateBond bond` |  |
| 1743 | `Household seller` |  |
| 1744 | `int id` |  |
| 1745 | `double before` |  |
| 1746 | `boolean armed, thrown` |  |
| 1747 | `final RuntimeException failure` |  |
| 1767 | `final RecountBalance hb` |  |
| 1768 | `final BondMarket bm` |  |
| 1769 | `final Household[] cells` |  |
| 1770 | `int month` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 103 | 2008 | **type** `public class BondCheck` | Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it over the bank, who loses what in a default, what the bank charges for concentration, who buys and sells, and the save. |
| 110 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 116 | 4 | `static boolean fixture(String label, boolean ok)` | A premise the fixture itself set up, asserted: a failure is counted, and the caller stops its section where it could not go on (FS14). |
| 121 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 131 | 5 | `static void cents(String label, double line, double figure)` | An audit line against the figure it declares, within the model's cent. |
| 137 | 5 | `static void quietly(Runnable r)` |  |
| 147 | 32 | `static Game city(Path root, String name, int months)` | A city with households who save, a bank, sectors that owe it and sectors that do not: founded, built, played two years. |
| 181 | 10 | `static MoneyAudit.Result play(Game g)` | One played month, and whether its audit closed. |
| 192 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |
| 197 | 3 | `static double line(MoneyAudit.Result r, String label)` | One line of a month's audit, by its label: what MoneyAudit declared under it, as it recorded it (EP2: Result.figure()), 0 when it declared nothing. |
| 201 | 41 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ARITHMETIC (lines 243-282)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 37 | `static void arithmetic()` |  |

### 2. BOOKBUILDING (lines 283-342)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 57 | `static void bookbuilding(Game g)` |  |

### 3. THE CHOICE (lines 343-390)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 45 | `static void theChoice(Game g)` |  |

### 7a. A BOND SOLD IN THE MONTH (lines 391-449)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 34 | `static CorporateBond issuedInTheMonth(Game g)` |  |
| 437 | 12 | `static void theBankHoldsSome(Game g)` | The bank holds some of every bond, caused. |

### 1b. THE COUPONS (lines 450-478)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 26 | `static void coupons(Game g)` |  |

### 4. THE BOOK IN PLAY (lines 479-511)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 30 | `static void theBookInPlay(Game g)` |  |

### 7. THE PARTICIPANTS (lines 512-744)

| line | len | member | says |
|---:|---:|---|---|
| 514 | 67 | `static void participants(Game g, CorporateBond bond)` |  |
| 586 | 41 | `static void companies(Game g)` | The companies, in a city of their own before anything borrows: the one that owes nothing bids for another's bonds and never its own. |
| 629 | 26 | `static void underItsTarget()` | A market of its own around a bank rebuilding its capital. |
| 657 | 6 | `static void worldRunning()` | The world's rule while its money is running: no bid. |
| 664 | 14 | `static BondMarket.Readings readings(boolean running)` |  |
| 680 | 64 | `static void shortOfMoney(Game g, CorporateBond bond)` | A household cell short of money asks at the price its own borrowing rate makes the buyer's yield: into a bid at or over it, it sells at the bid's price; under every bid, it waits. |

### 5c. CAN'T PAY MEANS DEFAULT (lines 745-958)

| line | len | member | says |
|---:|---:|---|---|
| 754 | 3 | `static boolean standsInPlant(EconomyManager em, String sector)` | Whether a sector's assets stand in plant (land and buildings), which no waterfall sells: the section's three sectors are each short past what the waterfall raises, so each must have something under its assets to stay ... |
| 759 | 8 | `static String heaviestHolder(BusinessDebtManager credit, BondMarket bm, EconomyManager em)` | The company holding the most of the other sectors' bonds, or null if none holds any (5c's Y, and what it is handed). |
| 769 | 9 | `static String widestUnderTheCeiling(BusinessDebtManager credit, String y, EconomyManager em)` | The largest sector, by its assets, that owes under the shortfall desk's ceiling and is not y, the issuer or barred (5c's W). |
| 789 | 166 | `static void cantPayInPlay(Game g)` | 0.7.12 round 4 (Jerus: "Can't pay means default"): a sector whose till the month leaves short first sells what it holds, by the households' waterfall rule - the other sectors' bonds, into the bids resting - then asks ... |

### 5d. BUY ONLY WHAT IT CAN PAY FOR (lines 959-1181)

| line | len | member | says |
|---:|---:|---|---|
| 970 | 190 | `static void buyOnlyWhatItCanPayFor(Path root)` | 0.7.12 round 6 (Jerus: "Buy only what it can pay for"; Kashyap, Lamont & Stein, QJE 109(3), 1994): a sector's orders for stock - the shops' shelves and every fleet - are limited to its cash plus the credit it can get ... |
| 1162 | 13 | `static double[] stockBought(Sector s)` | What a sector bought of its stock in the month just played: {its value, the units it asked for, the units it got}. |
| 1176 | 5 | `static void check0(String label, double actual)` |  |

### 5. RECOVERIES BY INSTRUMENT (lines 1182-1345)

| line | len | member | says |
|---:|---:|---|---|
| 1184 | 83 | `static void seniorityOnTheLender()` |  |
| 1268 | 77 | `static void seniorityInPlay(Game g)` |  |

### 6. CONCENTRATION (lines 1346-1452)

| line | len | member | says |
|---:|---:|---|---|
| 1348 | 94 | `static void concentration(Game g)` |  |
| 1443 | 9 | `static double concentrationAt(Bank b, double a, double b2, double c)` |  |

### 8. SAVE AND LOAD (lines 1453-1620)

| line | len | member | says |
|---:|---:|---|---|
| 1455 | 125 | `static void saveAndLoad(Game g, Path root) throws Exception` |  |
| 1581 | 5 | `static int resting(BondMarket bm)` |  |
| 1587 | 12 | `static boolean sameBonds(BondMarket a, BondMarket b)` |  |
| 1600 | 10 | `static boolean sameBooks(BondMarket a, BondMarket b)` |  |
| 1611 | 9 | `static boolean sameOrders(List<OrderBook.Order> a, List<OrderBook.Order> b)` |  |

### 9. EACH HOUSEHOLD TYPE TRADES (lines 1621-1738)

| line | len | member | says |
|---:|---:|---|---|
| 1631 | 43 | **type** `static final class TwoCells` | Two cells and one bond, on a market of their own: skilled couples with $88k each past their cushion and no bonds, and unskilled couples with nothing saved holding the households' face. |
| 1637 | 36 | `TwoCells(double face, double poorHolds)` _(in BondCheck.TwoCells)_ |  |
| 1675 | 21 | `static void priceTick()` |  |
| 1697 | 41 | `static void eachTypeTrades()` |  |

### 9b. DEFER ONLY THE MONTH'S HOUSEHOLD FOLDS (lines 1739-2005)

| line | len | member | says |
|---:|---:|---|---|
| 1742 | 16 | **type** `static final class RecountBalance extends HouseholdBalance` | A fixture-only failed buyer lookup, inside Settle after the seller has actually changed. |
| 1749 | 8 | `public Household cellByKey(String key)` _(in BondCheck.RecountBalance)_ |  |
| 1766 | 53 | **type** `static final class RecountMarket` | Two sellers precede two buyers on three nonconsecutive ids. |
| 1772 | 39 | `RecountMarket(boolean eager)` _(in BondCheck.RecountMarket)_ |  |
| 1812 | 6 | `void armFailure()` _(in BondCheck.RecountMarket)_ |  |
| 1820 | 5 | `static boolean rawSame(double...values)` |  |
| 1826 | 5 | `static boolean rawArray(double[] a, double[] b)` |  |
| 1832 | 9 | `static boolean rawOrders(List<OrderBook.Order> a, List<OrderBook.Order> b)` |  |
| 1842 | 13 | `static double[] bondAuditFlows(BondMarket b)` |  |
| 1857 | 34 | `static void recountTwins(String label, RecountMarket a, RecountMarket b)` | Compare the actual model reads, without tolerance or recounting either twin for the assertion. |
| 1892 | 29 | `static void eagerOutside(String label, RecountMarket a, RecountMarket b)` |  |
| 1922 | 9 | `static boolean rawFills(List<OrderBook.Fill> a, List<OrderBook.Fill> b)` |  |
| 1941 | 64 | `static void deferredBondRecounts()` | Mutation witnesses: remove finally's recountBonds and the raw aggregates fail; omit the seller's touched identity and the two seller totals fail; omit the buyer's identity and the two buyer totals fail. |

### 1c. MATURITY (lines 2006-2047)

| line | len | member | says |
|---:|---:|---|---|
| 2008 | 5 | `static CorporateBond largestTheFundHoldsNone(BondMarket bm)` |  |
| 2014 | 33 | `static void maturity(Game g)` |  |

### 9c. THE KNOWN ORDER IN PLAY (AP8) (lines 2048-2110)

| line | len | member | says |
|---:|---:|---|---|
| 2051 | 12 | `static String marketBits(Game g)` | The played city's bond market to the bit: every bond's holders and its book, and the households' paper and savings. |
| 2065 | 9 | `static Game reloaded(Game g, int slot, OrderBook.Probe probe)` | A copy of the played city from its scratch slot, every book of its market under `probe`. |
| 2075 | 35 | `static void knownOrderInPlay(Game g, OrderBook.Probe inPlay)` |  |

