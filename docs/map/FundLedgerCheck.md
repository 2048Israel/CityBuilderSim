# FundLedgerCheck.java - 1,186 lines · 28 methods · 2 constants · harnesses

`ham/citybuildersim/FundLedgerCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
> 
> Every city here plays with the owner line off (BO1-S, ShortSellingCheck.lineOff): the fund buys
> the households' shares, and in this harness's town of nine adults, under OwnerHousehold.OWNER_ADULT_SHARE,
> no company lists and no household holds one. The line's own claims are OwnerCheck's.
> 
> THE FIXTURE'S TOWN (FS13, 2026-11-20; PK1): a town whose bank is built at its founding (a Commercial Bank
> in the founding's order, its capital sold as shares, the households first and the world for the rest),
> played FOUNDING_MONTHS and saved. It is a stage of the founding and no more: every premise a section
> needs of it is caused and not read off what the months left - the households' holding of the company the
> sections trade in and of the bank (copy(): FundCheck.handTheHouseholds(), HOUSEHOLD_HOLDING of each), the
> desk's inventory handed away before the sections that play a step on a rule's bid (handCopy()), the seller
> on every book, the split's trigger sized from the wage at the month it is traded.

**Uses:** [Exchange](Exchange.md) (68), [FundLedger](FundLedger.md) (66), [OrderBook](OrderBook.md) (65), [TreasuryFund](TreasuryFund.md) (44), [Equity](Equity.md) (41), [Game](Game.md) (39), [FundView](FundView.md) (16), [BondMarket](BondMarket.md) (12), [Household](Household.md) (8), [FundCheck](FundCheck.md) (5), [ShortSellingCheck](ShortSellingCheck.md) (4), [CorporateBond](CorporateBond.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [Bank](Bank.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [EconomyManager](EconomyManager.md) (1)

**Used by (2):** [CapitalGainsCheck](CapitalGainsCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md)

## Sections

| line | section |
|---:|---|
| 161 | · the city |
| 236 | 1. average cost |
| 288 | 2. the books |
| 387 | 3. bonds |
| 547 | 4. a split, and a reform |
| 603 | 5. the save |
| 771 | 6 and 7. the identity, and by kind |
| 854 | 8. the hand |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 106 | `FundLedgerCheck.FOUNDING_MONTHS` | `72` | The months the fixture town is played before it is saved: a stage of the founding, never a month picked because the town's path passes at it (PK1). |
| 112 | `FundLedgerCheck.HOUSEHOLD_HOLDING` | `2 * TreasuryFund.OWNERSHIP_LIMIT` | The share of the company the sections trade in, and of the bank, that the base hands the households: twice the fund's cap on a company, so the households can sell the fund the whole of the room under it (section 8's f... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `static int fails` |  |
| 71 | `static PrintStream out` |  |
| 72 | `static PrintStream quiet` |  |
| 103 | `static GameFiles files` |  |
| 109 | `static Game.BuildResult bankBuilt` | What the founding's order for the bank answered: the fixture fails if it was not a built bank. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1119 | **type** `public class FundLedgerCheck` | Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, through a split, a reform and a save, and that the ledger and the fund's ... |
| 74 | 4 | `static void check(String label, boolean ok)` |  |
| 79 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 89 | 4 | `static void quietly(Runnable r)` |  |
| 94 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 101 | 1 | `static void play(Game g)` |  |
| 120 | 9 | `static Game copy()` | A copy of the fixture city, loaded from its save: every section starts from the same city, and the holding the sections sell from is CAUSED (FS13, 2026-11-20; PK1) and not what the founding's months left: the househol... |
| 135 | 5 | `static Game handCopy()` | A copy whose desk holds nothing, for the sections that play a step on the rule's bid (section 8): a rule's bid at the desk's ask is met by the desk's inventory first, so a bid the section sizes rests only where the de... |
| 142 | 8 | `static Household holder(Game g, int c)` | The household cell holding the most of a company's shares: a seller the fixture can cause on the book (copy() hands the households their holding of the company and the bank). |
| 151 | 5 | `static double filled(List<OrderBook.Fill> fills)` |  |
| 157 | 61 | `public static void main(String[] args)` |  |
| 225 | 10 | `static int heldCompany(Game g)` | THE COMPANY THE SECTIONS TRADE IN (FS13, 2026-11-20; PK1): the listed company, the bank apart, with the largest market cap (Exchange.marketCap()) - the model's own measure of the town's companies and not what the hous... |

### 1. average cost (lines 236-287)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 49 | `static void averageCost()` |  |

### 2. the books (lines 288-386)

| line | len | member | says |
|---:|---:|---|---|
| 290 | 96 | `static void theBooks()` |  |

### 3. bonds (lines 387-546)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 14 | `static BondMarket.Readings readings(int month)` |  |
| 404 | 142 | `static void bonds()` |  |

### 4. a split, and a reform (lines 547-602)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 53 | `static void splitAndReform()` |  |

### 5. the save (lines 603-770)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 58 | `static void theSave()` |  |
| 676 | 94 | `static void olderSaveWithARescueBook()` | AN OLDER SAVE WITH A RESCUE BOOK (0.7.39, after its docs pass): the path a city that resolved its bank before 0.7.39 takes at its first load - Jerus's long city, if it has. |

### 6 and 7. the identity, and by kind (lines 771-853)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 10 | **type** `record Snap(double mv, double acb, double realized, double bought, double sold, double principal, double re...` |  |
| 775 | 7 | `static Snap of(Game g)` _(in FundLedgerCheck.Snap)_ |  |
| 785 | 5 | `static double residual(Snap s0, Snap s)` | The identity's residual between two snapshots: realized + the change in unrealized, less the change in value + proceeds - purchases - rescues. |
| 791 | 62 | `static void theIdentity()` |  |

### 8. the hand (lines 854-1186)

| line | len | member | says |
|---:|---:|---|---|
| 856 | 63 | `static void theHand()` |  |
| 921 | 8 | `static Game fundAtTheCap(int c)` | A city whose fund holds the cap of company c and has a buy of the hand's for it waiting for the next step. |
| 937 | 13 | `static int companyAtTheCap(int c0)` | The company a fund at the cap is still at the cap of when the step is over: section 8's, or the first other whose step leaves the fund's holding alone, or -1 where none does (FS13). |
| 952 | 7 | `static double capsOf(Game g)` | The market caps of every company with shares and a fair value, on a city. |
| 961 | 10 | `static double ruleAloneFor(int c)` | What the rule alone bids for company c at the step, given money for about a hundredth of the market and four orders' worth of 4% of c (theCapCountsEveryBuy()'s twin): its shares resting on c's book. |
| 983 | 181 | `static void theCapCountsEveryBuy(int c0)` | THE CAP COUNTS EVERY BUY OF THE FUND'S ON THE COMPANY (0.7.39, closed after its docs pass): what it holds, its own bids on the book - the rule's and the hand's - and the hand's buys still waiting, as if every one fill... |
| 1166 | 20 | `static boolean fillTheFundsBids(Game g, int c)` | Every bid of the fund's on a company's book filled, at its price: the others' bids withdrawn, households selling into the fund's. |

