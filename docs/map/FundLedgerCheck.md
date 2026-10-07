# FundLedgerCheck.java - 933 lines · 23 methods · 1 constants · harnesses

`ham/citybuildersim/FundLedgerCheck.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, through a split, a reform and a save, and that the ledger and the fund's own counters tell one story over a played run. Not part of the game.
> 
> WHY THIS EXISTS. Until 0.7.39 the fund knew what it was worth and every
> flow in and out of it, but not what any holding had cost: Jerus, "the city
> fund should show pnl and acb and all that". The ledger is hooked where the
> holdings move (Exchange's and BondMarket's settles, a maturity, a
> write-down, a dividend, the rescue) and nowhere else, so a path that moves
> a holding without a hook is exactly what section 6's identity is for. The
> project's spec-fund-0739.md, 3.7.
> 
> What it has to prove, a section each:
> 
>   1. Average cost, on the book (a seller caused on it): 100 bought at 2 and
>      100 at 4 cost 600, 3 a share; 50 sold at 5 take 150 of cost out and
>      realize 100; the average stays 3.
>   2. The books: the rule's buys and the hand's pool into one lot; the
>      rescue takes the fund's market-book bank shares for nothing (their
>      cost a realized loss); a hand sale takes the market lot first and
>      then the rescue lot, each at its own cost, and the rescue lot's cost
>      is TreasuryFund.getRescueCost() after.
>   3. Bonds: a coupon is income, not cost; a default at
>      BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT realizes that share of the
>      cost; a write-down that keeps the whole face realizes nothing; a
>      maturity realizes the face less the cost.
>   4. A split at Exchange.SPLIT_AT: the cost stands and the average a share
>      falls by the factor. A reform: every money figure in the ledger by
>      the reform's scale, share counts not.
>   5. A save: the ledger round-trips exactly; an older save's (none) is
>      seeded at market value, flagged with the month, the rescue lot from
>      the counters - a save with a rescue book, loaded with its ledger taken
>      out, gets its rescue lot at TreasuryFund.getRescueCost() exactly and
>      untagged, the same lot a city that tracked it all along has, and its
>      market lots at market value, tagged.
>   6. The identity over a played run: realized + the change in unrealized =
>      the change in what the shares and bonds are worth + what sales and
>      maturities brought in - what purchases and rescues cost, every month,
>      and the ledger's purchases and proceeds the fund's counters'.
>   7. Since it began, by kind, adds up to the fund's gain.
>   8. The hand (0.7.39): a buy no further than the 10% cap, counting every
>      buy of the fund's on the company, the rule's bid making way for the
>      hand's at the step and between steps - the rule's bid and two orders,
>      every one of them filled, leave the fund at the cap and no further,
>      and the ticket's quote reckons the room the same way; its cash held
>      from the rule; its order a row from the step that posts it to the step
>      that withdraws it, with what lapsed.

**Uses:** [Exchange](Exchange.md) (68), [OrderBook](OrderBook.md) (65), [FundLedger](FundLedger.md) (64), [TreasuryFund](TreasuryFund.md) (40), [Game](Game.md) (28), [Equity](Equity.md) (26), [FundView](FundView.md) (16), [BondMarket](BondMarket.md) (10), [Household](Household.md) (7), [CorporateBond](CorporateBond.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [FundCheck](FundCheck.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [Bank](Bank.md) (1)

## Sections

| line | section |
|---:|---|
| 120 | · the city |
| 164 | 1. average cost |
| 214 | 2. the books |
| 311 | 3. bonds |
| 384 | 4. a split, and a reform |
| 435 | 5. the save |
| 603 | 6 and 7. the identity, and by kind |
| 680 | 8. the hand |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 314 | `FundLedgerCheck.ISSUER` | `"Mining"` | The issuer of section 3's one bond: a sector's name, which BondMarket.writeDown() finds an issuer's bonds by. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static int fails` |  |
| 59 | `static PrintStream out` |  |
| 60 | `static PrintStream quiet` |  |
| 91 | `static GameFiles files` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 878 | **type** `public class FundLedgerCheck` | Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, through a split, a reform and a save, and that the ledger and the fund's ... |
| 62 | 4 | `static void check(String label, boolean ok)` |  |
| 67 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 77 | 4 | `static void quietly(Runnable r)` |  |
| 82 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 89 | 1 | `static void play(Game g)` |  |
| 94 | 5 | `static Game copy()` | A copy of the fixture city, loaded from its save: every section starts from the same city. |
| 101 | 8 | `static Household holder(Game g, int c)` | The household cell holding the most of a company's shares: a seller the fixture can cause on the book. |
| 110 | 5 | `static double filled(List<OrderBook.Fill> fills)` |  |
| 116 | 35 | `public static void main(String[] args)` |  |
| 153 | 10 | `static int heldCompany(Game g)` | A company the households hold shares of, other than the bank. |

### 1. average cost (lines 164-213)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 47 | `static void averageCost()` |  |

### 2. the books (lines 214-310)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 94 | `static void theBooks()` |  |

### 3. bonds (lines 311-383)

| line | len | member | says |
|---:|---:|---|---|
| 316 | 14 | `static BondMarket.Readings readings(int month)` |  |
| 331 | 52 | `static void bonds()` |  |

### 4. a split, and a reform (lines 384-434)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 48 | `static void splitAndReform()` |  |

### 5. the save (lines 435-602)

| line | len | member | says |
|---:|---:|---|---|
| 437 | 58 | `static void theSave()` |  |
| 508 | 94 | `static void olderSaveWithARescueBook()` | AN OLDER SAVE WITH A RESCUE BOOK (0.7.39, after its docs pass): the path a city that resolved its bank before 0.7.39 takes at its first load - Jerus's long city, if it has. |

### 6 and 7. the identity, and by kind (lines 603-679)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 10 | **type** `record Snap(double mv, double acb, double realized, double bought, double sold, double principal, double re...` |  |
| 607 | 7 | `static Snap of(Game g)` _(in FundLedgerCheck.Snap)_ |  |
| 617 | 5 | `static double residual(Snap s0, Snap s)` | The identity's residual between two snapshots: realized + the change in unrealized, less the change in value + proceeds - purchases - rescues. |
| 623 | 56 | `static void theIdentity()` |  |

### 8. the hand (lines 680-933)

| line | len | member | says |
|---:|---:|---|---|
| 682 | 55 | `static void theHand()` |  |
| 749 | 162 | `static void theCapCountsEveryBuy(int c)` | THE CAP COUNTS EVERY BUY OF THE FUND'S ON THE COMPANY (0.7.39, closed after its docs pass): what it holds, its own bids on the book - the rule's and the hand's - and the hand's buys still waiting, as if every one fill... |
| 913 | 20 | `static boolean fillTheFundsBids(Game g, int c)` | Every bid of the fund's on a company's book filled, at its price: the others' bids withdrawn, households selling into the fund's. |

