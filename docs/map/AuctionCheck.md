# AuctionCheck.java - 1,848 lines · 45 methods · 5 constants · harnesses

`ham/citybuildersim/AuctionCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> The city's paper sold at auction: one clearing yield, and what it costs.
> 
> WHAT THIS HAS TO PROVE (spec-city-debt.md, section 4; CD2). Auction is pure -
> a snapshot of the bidders and the ask in, a result out - so sections 1 to 6
> start no Game:
> 
>   1. EACH BIDDER BIDS BY THE RULE IT ALREADY FOLLOWS, against that rule's own
>      constants: the households' (D32) out of savings and never past the
>      issue, the firms' (D33) as OutwardInvestment moves their money, the
>      world's as CapitalFlows.stockAt() sizes its stock, the bank's room at
>      fair and nothing under.
> 
>   2. ONE CLEARING YIELD (D5). Covered at fair, it stops at fair. Otherwise it
>      is the lowest yield the bids cover: covered at the stop, not a hair
>      under; everyone is filled at it, the households first and in full, the
>      other three pro rata to their bids at it.
> 
>   3. THE COVER AND THE TAIL are read off the bids at the ceiling and the stop
>      over fair, each worked out here from bidsAt() rather than taken from
>      the result.
> 
>   4. CAPPED AND FILLED (D20). Capped, a short auction sells what was bid at
>      the ceiling and says so; filled, the stop rises past the default tail
>      because the world's bid rises with the yield; an issue bigger than
>      every rule sells short even then; a named yield under fair is fair.
> 
>   5. THE COUPON AND THE PRICE (D2). A new bond line's coupon is the stop
>      rounded down to an eighth; a reopening keeps its line's; an amortizing
>      bond's is the stop and prices at 100; a bill is a discount. Every price
>      is checked by summing the schedule here, and proceeds are the price on
>      what sold, less the fee.
> 
>   6. PRICED ON THE CURVE (slice 3's finding). An ask that carries a pricer is
>      priced by it: with no tail the schedule is worth exactly the curve's value
>      of it, to the bit, and with a tail the curve's value at that spread over
>      every month's zero rate - lower. An ask with none prices at the stop, one
>      flat yield, as sections 1-5 do. On a curve that is not flat the two
>      prices differ, which is what a round trip would have netted.
> 
>   7. THE LINEUP IN A CITY (CD2 slice 7). A ten-year sale opens a line at the
>      stop rounded down to an eighth; the next month's ten-year sale reopens the
>      SAME piece (its face the sum, its coupon unchanged, priced at the second
>      auction's yield); a line with under nine tenths of its term left is not
>      reopened (the edge, on a two-year line: 22 months left is a benchmark, 21
>      is not); a 40-year ask is refused; a bill and an amortizing sale each
>      open a piece of their own, every time.
> 
>   8. THE QUOTE IS THE DEAL, AND THE SETTLE PAYS THE ALLOTMENT. A quote and its
>      booking agree to the cent (face, proceeds, coupon); at the next settle the
>      households pay exactly their allotment (their savings fall by its cash, their
>      paper rises by its face) and the bank the rest, the month's money audit
>      closes; and a short sale that sells under the minimum issue is no sale (the
>      quote is empty, with its reason in the summary, and books nothing).
> 
>   9. THE ROLLOVER SELLS AT ITS OWN CAP (D28) AND A SHORT SALE IS PAID IN FULL
>      (D29). The cap is a setting of the rollover's, not automatic building's,
>      and saved. A rollover capped and short sells what was bid; the maturity
>      is paid in full all the same - the part the sale did not raise from the
>      cash, and past the cash from the central bank's advances - and the log
>      and the inbox name which. Filled, the same sale covers at the higher
> ... (29 more lines in the source)

**Uses:** [Auction](Auction.md) (132), [AutoBuilder](AutoBuilder.md) (52), [Game](Game.md) (49), [PriceLevel](PriceLevel.md) (32), [LongPlaytest](LongPlaytest.md) (26), [DebtQuote](DebtQuote.md) (23), [CapSetting](CapSetting.md) (13), [BuildAdvice](BuildAdvice.md) (12), [OutwardInvestment](OutwardInvestment.md) (11), [Debt](Debt.md) (11), [LongTermBond](LongTermBond.md) (8), [TimeSkipReport](TimeSkipReport.md) (8), [HouseholdBalance](HouseholdBalance.md) (7), [CapitalFlows](CapitalFlows.md) (7), [ChildcareCheck](ChildcareCheck.md) (6), [DebtManager](DebtManager.md) (5), [GameFiles](GameFiles.md) (5), [MediumTermBond](MediumTermBond.md) (5), [Notice](Notice.md) (5), [CityNeeds](CityNeeds.md) (5), [YieldCurve](YieldCurve.md) (4), [Founding](Founding.md) (4), [ShortTermTBill](ShortTermTBill.md) (4), [Rollover](Rollover.md) (4), [Bank](Bank.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [MoneyAudit](MoneyAudit.md) (3), [AuctionTally](AuctionTally.md) (3), [Household](Household.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2)... and 5 more

**Used by (1):** [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 136 | THE FIXTURE: the study's calm city (debt-model.py CALM), in the game's terms |
| 248 | 1. each bidder's rule against its constants |
| 367 | 2. one clearing yield |
| 430 | 3. cover and tail |
| 465 | 4. capped and filled |
| 535 | 5. the coupon and the price |
| 642 | 6. PRICED ON THE CURVE |
| 725 | THE GAME'S OWN SALES (sections 7, 8 and 11): a city that sells its paper |
| 814 | 7. the lineup in a city |
| 957 | 8. the quote is the deal, and the settle pays the allotment |
| 1069 | 11. the real run (D33) |
| 1197 | SECTIONS 9 AND 10: THE SALES NOBODY PRESSES (CD2, slice 6; D28 to D30) |
| 1265 | 9. the rollover's own cap, and a short sale paid in full |
| 1439 | 10. automatic building: its own cap, and the ask a short sale keeps open |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 146 | `AuctionCheck.FEE` | `12_000` | The fixed cost of a sale: the study's AUCTION_FEE, the one Game charges. |
| 149 | `AuctionCheck.FAIR` | `.03602` | The 10-year's fair yield in the calm city (spec-city-debt.md, section 4.3's table: 3.602%). |
| 152 | `AuctionCheck.FACE` | `3e8` | D$300M of face, the table's issue. |
| 734 | `AuctionCheck.QUIET` | `new PrintStream(java.io.OutputStream.nullOutputStream())` | A stream that prints nothing: System.out and LongPlaytest's out while a fixture city runs. |
| 790 | `AuctionCheck.HANDED_SPARE` | `32 * 100 * PriceLevel.FACTOR` | The savings past their cushion sections 7 and 8 hand the lineup city's households at least (FS15): thirty-two of the smallest asks, about what the city played sixty months held on 0.9.166's path (3,178k at month 61). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 110 | `static int fails` |  |
| 111 | `static int asserts` |  |
| 112 | `static PrintStream out` |  |
| 155 | `double deposit` |  |
| 156 | `double spare` |  |
| 157 | `double monthlyGdp` |  |
| 158 | `double bankRoom` | risk-weighted |
| 159 | `double bankExtra` | risk-weighted |
| 160 | `boolean panic` |  |
| 1216 | `static PrintStream quiet` | The stream ChildcareCheck's town and the cities here are built behind. |
| 1447 | `static double deposit` | The deposit rate of section 10's fixture, found by tuneDeposit(): low enough that at a cap of fair the buyers bid a real share of an issue. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 108 | 1741 | **type** `public class AuctionCheck` | The city's paper sold at auction: one clearing yield, and what it costs. |
| 114 | 5 | `static void assertTrue(String label, boolean ok)` |  |
| 121 | 6 | `static void close(String label, double actual, double expected, double tol)` | Equal within tol, or within tol of the expected figure's size, whichever is larger. |
| 129 | 6 | `static void near(String label, double actual, double expected, double abs)` | Equal within an absolute tolerance, for prices per 100. |

### THE FIXTURE: the study's calm city (debt-model.py CALM), in the game's terms (lines 136-247)

| line | len | member | says |
|---:|---:|---|---|
| 154 | 19 | **type** `static final class Fx` |  |
| 162 | 4 | `Bidders b()` _(in AuctionCheck.Fx)_ |  |
| 168 | 4 | `Fx tight()` _(in AuctionCheck.Fx)_ | The bank's room nearly gone: D$20M risk-weighted, D$100M of face. |
| 174 | 3 | `static Fx calm()` |  |
| 178 | 3 | `static Ask bond(double face, double fair)` |  |
| 183 | 8 | `static double bulletPrice(double coupon, int months, double y)` | The paper's value per 100 at one flat yield, summed here with Math.pow and not Debt.presentValue. |
| 193 | 13 | `static double amortizingPrice(double coupon, int years, double y)` | The same for an amortizing bond: a yearly slice of principal, the coupon monthly on what is left. |
| 208 | 6 | `static double coverAt(double ceiling, Bidders b, Ask a)` | What the cover is: each bidder's bid at the ceiling, at most the whole issue, over the issue. |
| 215 | 32 | `public static void main(String[] args) throws Exception` |  |

### 1. each bidder's rule against its constants (lines 248-366)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 117 | `static void section1()` |  |

### 2. one clearing yield (lines 367-429)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 61 | `static void section2()` |  |

### 3. cover and tail (lines 430-464)

| line | len | member | says |
|---:|---:|---|---|
| 431 | 33 | `static void section3()` |  |

### 4. capped and filled (lines 465-534)

| line | len | member | says |
|---:|---:|---|---|
| 466 | 68 | `static void section4()` |  |

### 5. the coupon and the price (lines 535-641)

| line | len | member | says |
|---:|---:|---|---|
| 536 | 105 | `static void section5()` |  |

### 6. PRICED ON THE CURVE (lines 642-724)

| line | len | member | says |
|---:|---:|---|---|
| 652 | 72 | `static void section6()` |  |

### THE GAME'S OWN SALES (sections 7, 8 and 11): a city that sells its paper (lines 725-813)

| line | len | member | says |
|---:|---:|---|---|
| 737 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 742 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 749 | 1 | `static double savings(Game g)` |  |
| 752 | 4 | `static MoneyAudit.Result play(Game g)` | A month, played with the narration off; the month's money audit comes back. |
| 758 | 4 | `static double cents(double v)` | Money to the cent, as every proceeds figure is: the cent of today's money, at the founding's price level (Game.cents, EP1). |
| 764 | 18 | `static Game lineupCity()` | HoldersCheck's city: given the money and the ground, a bank and three hundred houses, sixty months played. |
| 784 | 3 | `static long pieces(DebtManager ledger, Class<? extends Debt> kind)` | How many pieces of this class the city's debt book holds. |
| 800 | 13 | `static void handSpare(Game g, double spare)` | The households' savings past their cushion, handed (FS15): every household of the market's cells given the same savings more until their spare for paper is at least `spare`. |

### 7. the lineup in a city (lines 814-956)

| line | len | member | says |
|---:|---:|---|---|
| 815 | 25 | `static void section7(Game city)` |  |
| 842 | 114 | `static void lineupInCity(Game city, double ask, int need2)` | The played lineup's dependent laws; the independent term refusal stays in section 7. |

### 8. the quote is the deal, and the settle pays the allotment (lines 957-1068)

| line | len | member | says |
|---:|---:|---|---|
| 958 | 110 | `static void section8(Game city)` |  |

### 11. the real run (D33) (lines 1069-1196)

| line | len | member | says |
|---:|---:|---|---|
| 1071 | 22 | `static void historicalAuction()` | R1f: a young 1900 city need not borrow on its own; the player's actual bill sale causes an auction. |
| 1094 | 102 | `static void section11()` |  |

### SECTIONS 9 AND 10: THE SALES NOBODY PRESSES (CD2, slice 6; D28 to D30) (lines 1197-1264)

| line | len | member | says |
|---:|---:|---|---|
| 1219 | 7 | `static String printed(Runnable r)` | What a piece of work printed: the log is where the rollover and the builder say what they did. |
| 1228 | 10 | `static void roomTo(Game g, double faceRoom)` | Leaves the bank room for this much face of the city's paper and no more: its weighted book raised until the room the auction reads is that. |
| 1240 | 4 | `static Game reload(Game g)` | A city saved to its own scratch folder's slot 10 and loaded into another Game, as a player's reload. |
| 1246 | 5 | `static Game load(Game g)` | ...the same save loaded again, into a city of its own. |
| 1253 | 5 | `static int salesBy(Game g, String by)` | The sales automatic building and the rollover made in the month just run, by whom. |
| 1259 | 5 | `static long siteCount(Game g)` |  |

### 9. the rollover's own cap, and a short sale paid in full (lines 1265-1438)

| line | len | member | says |
|---:|---:|---|---|
| 1268 | 9 | `static Game rolloverTown(Path root, String label)` | A town with the rollover on, a 3-month note of D$20M on its books that falls due next month. |
| 1279 | 1 | **type** `record Scene(Rollover.Issue issue, double askFace, double hhCapped, double hhFilled, double room, double cash)` | What the rollover is about to roll, and the room that makes it short at the default cap and covered at Fill it. |
| 1286 | 12 | `static Scene scene(Game g, double cash)` | The rollover's one issue as the city stands with `cash` in the treasury and the deposit rate at 3% (so the households' appetite has a slope), with the bank's room ample, and the room that leaves the default cap short ... |
| 1299 | 139 | `static void section9(Path root) throws Exception` |  |

### 10. automatic building: its own cap, and the ask a short sale keeps open (lines 1439-1848)

| line | len | member | says |
|---:|---:|---|---|
| 1442 | 3 | `static BuildAdvice.Measure streets()` | The measure Streets, whose card is a Paved Road: the first the pass walks to in the fixture's town. |
| 1456 | 13 | `static double tuneDeposit(Game g, double gap, Cap cap)` | The deposit rate at which a bond for `gap` sold at the builder's cap of fair sells short, but a share of the issue that is over the smallest deal worth arranging (Game.isTinySale(): under it a short sale is no sale), ... |
| 1475 | 4 | `static void useUpTheRoom(Game g)` | The room used up and the deposit rate tuneDeposit() found, so that at a cap of fair the buyers bid a real share of an issue and the bank bids D$100k of face: a bond for an order sells short, over the smallest deal wor... |
| 1485 | 6 | `static String month(Game g, boolean ample)` | One month of the fixture's, run and printed: the room used up as above (unless `ample`), and the treasury's cash set to the reserve plus what the asks hold - the month's other bills are met from elsewhere, so what an ... |
| 1493 | 5 | `static double placedIn(AutoBuilder ab, int month)` | What the builder's orders placed in a month cost, ground with them: the cash they took out of the treasury. |
| 1499 | 349 | `static void section10(Path root) throws Exception` |  |

