# BondCheck.java - 1,685 lines · 42 methods · 1 constants · harnesses

`ham/citybuildersim/BondCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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
> ... (7 more lines in the source)

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (73), [Bank](Bank.md) (59), [CorporateBond](CorporateBond.md) (53), [BondMarket](BondMarket.md) (50), [Game](Game.md) (32), [OrderBook](OrderBook.md) (18), [MoneyAudit](MoneyAudit.md) (12), [Sectors](Sectors.md) (12), [HouseholdBalance](HouseholdBalance.md) (10), [Sector](Sector.md) (9), [Household](Household.md) (8), [PayTier](PayTier.md) (7), [Good](Good.md) (6), [FamilyStructure](FamilyStructure.md) (5), [EconomyManager](EconomyManager.md) (4), [PriceLevel](PriceLevel.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [Statement](Statement.md) (3), [GameFiles](GameFiles.md) (2), [CapitalFlows](CapitalFlows.md) (2), [Founding](Founding.md) (1), [BuildingManager](BuildingManager.md) (1), [LongPlaytest](LongPlaytest.md) (1), [TempDirs](TempDirs.md) (1), [PriceLevelCheck](PriceLevelCheck.md) (1), [BankCheck](BankCheck.md) (1), [Retail](Retail.md) (1), [BusinessLoan](BusinessLoan.md) (1), [SupplierCredit](SupplierCredit.md) (1), [Haulage](Haulage.md) (1)

**Used by (2):** [GroceryCheck](GroceryCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 224 | 1. THE ARITHMETIC |
| 264 | 2. BOOKBUILDING |
| 323 | 3. THE CHOICE |
| 371 | 7a. A BOND SOLD IN THE MONTH |
| 430 | 1b. THE COUPONS |
| 459 | 4. THE BOOK IN PLAY |
| 492 | 7. THE PARTICIPANTS |
| 697 | 5c. CAN'T PAY MEANS DEFAULT |
| 897 | 5d. BUY ONLY WHAT IT CAN PAY FOR |
| 1111 | 5. RECOVERIES BY INSTRUMENT |
| 1273 | 6. CONCENTRATION |
| 1380 | 8. SAVE AND LOAD |
| 1548 | 9. EACH HOUSEHOLD TYPE TRADES |
| 1644 | 1c. MATURITY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 120 | `BondCheck.ISSUER` | `Sectors.CONSTRUCTION` | The sector every played fixture's bond is issued by: sound, owing nothing, with plant to borrow against. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `static int fails` |  |
| 88 | `static PrintStream out` |  |
| 89 | `static PrintStream quiet` |  |
| 90 | `static int closedMonths, brokenMonths` |  |
| 895 | `static String interimSector` | The sector §5c lent an interim loan to, for the save's round trip (§8). |
| 1559 | `final HouseholdBalance hb` |  |
| 1560 | `final BondMarket bm` |  |
| 1561 | `final Household poor, rich` |  |
| 1562 | `final CorporateBond bond` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 1601 | **type** `public class BondCheck` | Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it over the bank, who loses what in a default, what the bank charges for concentration, who buys and sells, and the save. |
| 92 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 97 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 107 | 5 | `static void cents(String label, double line, double figure)` | An audit line against the figure it declares, within the model's cent. |
| 113 | 5 | `static void quietly(Runnable r)` |  |
| 123 | 3 | `static Game city(Path root, String name, int months)` | A city with households who save, a bank, sectors that owe it and sectors that do not: founded, built, played two years. |
| 128 | 34 | `static Game city(Path root, String name, int months, boolean holdIssuer)` | The same city; holdIssuer keeps ISSUER from ordering a building in it, so it owes nothing (7c). |
| 164 | 10 | `static MoneyAudit.Result play(Game g)` | One played month, and whether its audit closed. |
| 175 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |
| 180 | 3 | `static double line(MoneyAudit.Result r, String label)` | One line of a month's audit, by its label: what MoneyAudit declared under it, as it recorded it (EP2: Result.figure()), 0 when it declared nothing. |
| 184 | 39 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ARITHMETIC (lines 224-263)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 37 | `static void arithmetic()` |  |

### 2. BOOKBUILDING (lines 264-322)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 56 | `static void bookbuilding(Game g)` |  |

### 3. THE CHOICE (lines 323-370)

| line | len | member | says |
|---:|---:|---|---|
| 325 | 45 | `static void theChoice(Game g)` |  |

### 7a. A BOND SOLD IN THE MONTH (lines 371-429)

| line | len | member | says |
|---:|---:|---|---|
| 373 | 34 | `static CorporateBond issuedInTheMonth(Game g)` |  |
| 417 | 12 | `static void theBankHoldsSome(Game g)` | The bank holds some of every bond, caused. |

### 1b. THE COUPONS (lines 430-458)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 26 | `static void coupons(Game g)` |  |

### 4. THE BOOK IN PLAY (lines 459-491)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 30 | `static void theBookInPlay(Game g)` |  |

### 7. THE PARTICIPANTS (lines 492-696)

| line | len | member | says |
|---:|---:|---|---|
| 494 | 58 | `static void participants(Game g, CorporateBond bond)` |  |
| 554 | 25 | `static void companies(Game g)` | The companies, on the fixture's month before anything borrows: the one that owes nothing bids for another's bonds and never its own. |
| 581 | 26 | `static void underItsTarget()` | A market of its own around a bank rebuilding its capital. |
| 609 | 6 | `static void worldRunning()` | The world's rule while its money is running: no bid. |
| 616 | 14 | `static BondMarket.Readings readings(boolean running)` |  |
| 632 | 64 | `static void shortOfMoney(Game g, CorporateBond bond)` | A household cell short of money asks at the price its own borrowing rate makes the buyer's yield: into a bid at or over it, it sells at the bid's price; under every bid, it waits. |

### 5c. CAN'T PAY MEANS DEFAULT (lines 697-896)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 8 | `static String heaviestHolder(BusinessDebtManager credit, BondMarket bm)` | The company holding the most of the other sectors' bonds, or null if none holds any (5c's Y, and what it is handed). |
| 710 | 9 | `static String widestUnderTheCeiling(BusinessDebtManager credit, String y)` | The largest sector, by its assets, that owes under the shortfall desk's ceiling and is not y, the issuer or barred (5c's W). |
| 730 | 163 | `static void cantPayInPlay(Game g)` | 0.7.12 round 4 (Jerus: "Can't pay means default"): a sector whose till the month leaves short first sells what it holds, by the households' waterfall rule - the other sectors' bonds, into the bids resting - then asks ... |

### 5d. BUY ONLY WHAT IT CAN PAY FOR (lines 897-1110)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 181 | `static void buyOnlyWhatItCanPayFor(Path root)` | 0.7.12 round 6 (Jerus: "Buy only what it can pay for"; Kashyap, Lamont & Stein, QJE 109(3), 1994): a sector's orders for stock - the shops' shelves and every fleet - are limited to its cash plus the credit it can get ... |
| 1091 | 13 | `static double[] stockBought(Sector s)` | What a sector bought of its stock in the month just played: {its value, the units it asked for, the units it got}. |
| 1105 | 5 | `static void check0(String label, double actual)` |  |

### 5. RECOVERIES BY INSTRUMENT (lines 1111-1272)

| line | len | member | says |
|---:|---:|---|---|
| 1113 | 83 | `static void seniorityOnTheLender()` |  |
| 1197 | 75 | `static void seniorityInPlay(Game g)` |  |

### 6. CONCENTRATION (lines 1273-1379)

| line | len | member | says |
|---:|---:|---|---|
| 1275 | 94 | `static void concentration(Game g)` |  |
| 1370 | 9 | `static double concentrationAt(Bank b, double a, double b2, double c)` |  |

### 8. SAVE AND LOAD (lines 1380-1547)

| line | len | member | says |
|---:|---:|---|---|
| 1382 | 125 | `static void saveAndLoad(Game g, Path root) throws Exception` |  |
| 1508 | 5 | `static int resting(BondMarket bm)` |  |
| 1514 | 12 | `static boolean sameBonds(BondMarket a, BondMarket b)` |  |
| 1527 | 10 | `static boolean sameBooks(BondMarket a, BondMarket b)` |  |
| 1538 | 9 | `static boolean sameOrders(List<OrderBook.Order> a, List<OrderBook.Order> b)` |  |

### 9. EACH HOUSEHOLD TYPE TRADES (lines 1548-1643)

| line | len | member | says |
|---:|---:|---|---|
| 1558 | 43 | **type** `static final class TwoCells` | Two cells and one bond, on a market of their own: skilled couples with $88k each past their cushion and no bonds, and unskilled couples with nothing saved holding the households' face. |
| 1564 | 36 | `TwoCells(double face, double poorHolds)` _(in BondCheck.TwoCells)_ |  |
| 1602 | 41 | `static void eachTypeTrades()` |  |

### 1c. MATURITY (lines 1644-1685)

| line | len | member | says |
|---:|---:|---|---|
| 1646 | 5 | `static CorporateBond largestTheFundHoldsNone(BondMarket bm)` |  |
| 1652 | 33 | `static void maturity(Game g)` |  |

