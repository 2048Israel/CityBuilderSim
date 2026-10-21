# AuctionCheck.java - 1,649 lines · 42 methods · 4 constants · harnesses

`ham/citybuildersim/AuctionCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
> ... (21 more lines in the source)

**Uses:** [Auction](Auction.md) (130), [Game](Game.md) (56), [AutoBuilder](AutoBuilder.md) (42), [LongPlaytest](LongPlaytest.md) (25), [DebtQuote](DebtQuote.md) (21), [CapSetting](CapSetting.md) (13), [BuildAdvice](BuildAdvice.md) (12), [OutwardInvestment](OutwardInvestment.md) (11), [Debt](Debt.md) (11), [LongTermBond](LongTermBond.md) (8), [CapitalFlows](CapitalFlows.md) (7), [HouseholdBalance](HouseholdBalance.md) (6), [MediumTermBond](MediumTermBond.md) (5), [Notice](Notice.md) (5), [CityNeeds](CityNeeds.md) (5), [ChildcareCheck](ChildcareCheck.md) (4), [YieldCurve](YieldCurve.md) (4), [DebtManager](DebtManager.md) (4), [GameFiles](GameFiles.md) (4), [ShortTermTBill](ShortTermTBill.md) (4), [TimeSkipReport](TimeSkipReport.md) (4), [Rollover](Rollover.md) (4), [Bank](Bank.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [MoneyAudit](MoneyAudit.md) (3), [Founding](Founding.md) (2), [AuctionTally](AuctionTally.md) (2), [TreasuryFund](TreasuryFund.md) (1), [CityCalendar](CityCalendar.md) (1)

## Sections

| line | section |
|---:|---|
| 129 | THE FIXTURE: the study's calm city (debt-model.py CALM), in the game's terms |
| 240 | 1. each bidder's rule against its constants |
| 359 | 2. one clearing yield |
| 422 | 3. cover and tail |
| 457 | 4. capped and filled |
| 527 | 5. the coupon and the price |
| 634 | 6. PRICED ON THE CURVE |
| 717 | THE GAME'S OWN SALES (sections 7, 8 and 11): a city that sells its paper |
| 778 | 7. the lineup in a city |
| 905 | 8. the quote is the deal, and the settle pays the allotment |
| 1001 | 11. the real run (D33) |
| 1083 | SECTIONS 9 AND 10: THE SALES NOBODY PRESSES (CD2, slice 6; D28 to D30) |
| 1151 | 9. the rollover's own cap, and a short sale paid in full |
| 1324 | 10. automatic building: its own cap, and the ask a short sale keeps open |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 139 | `AuctionCheck.FEE` | `12_000` | The fixed cost of a sale: the study's AUCTION_FEE, the one Game charges. |
| 142 | `AuctionCheck.FAIR` | `.03602` | The 10-year's fair yield in the calm city (spec-city-debt.md, section 4.3's table: 3.602%). |
| 145 | `AuctionCheck.FACE` | `3e8` | D$300M of face, the table's issue. |
| 725 | `AuctionCheck.QUIET` | `new PrintStream(java.io.OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `static int fails` |  |
| 104 | `static int asserts` |  |
| 105 | `static PrintStream out` |  |
| 148 | `double deposit` |  |
| 149 | `double spare` |  |
| 150 | `double monthlyGdp` |  |
| 151 | `double bankRoom` | risk-weighted |
| 152 | `double bankExtra` | risk-weighted |
| 153 | `boolean panic` |  |
| 1102 | `static PrintStream quiet` | The stream ChildcareCheck's town and the cities here are built behind. |
| 1332 | `static double deposit` | The deposit rate of section 10's fixture, found by tuneDeposit(): low enough that at a cap of fair the buyers bid a real share of an issue. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 101 | 1549 | **type** `public class AuctionCheck` | The city's paper sold at auction: one clearing yield, and what it costs. |
| 107 | 5 | `static void assertTrue(String label, boolean ok)` |  |
| 114 | 6 | `static void close(String label, double actual, double expected, double tol)` | Equal within tol, or within tol of the expected figure's size, whichever is larger. |
| 122 | 6 | `static void near(String label, double actual, double expected, double abs)` | Equal within an absolute tolerance, for prices per 100. |

### THE FIXTURE: the study's calm city (debt-model.py CALM), in the game's terms (lines 129-239)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 19 | **type** `static final class Fx` |  |
| 155 | 4 | `Bidders b()` _(in AuctionCheck.Fx)_ |  |
| 161 | 4 | `Fx tight()` _(in AuctionCheck.Fx)_ | The bank's room nearly gone: D$20M risk-weighted, D$100M of face. |
| 167 | 3 | `static Fx calm()` |  |
| 171 | 3 | `static Ask bond(double face, double fair)` |  |
| 176 | 8 | `static double bulletPrice(double coupon, int months, double y)` | The paper's value per 100 at one flat yield, summed here with Math.pow and not Debt.presentValue. |
| 186 | 13 | `static double amortizingPrice(double coupon, int years, double y)` | The same for an amortizing bond: a yearly slice of principal, the coupon monthly on what is left. |
| 201 | 6 | `static double coverAt(double ceiling, Bidders b, Ask a)` | What the cover is: each bidder's bid at the ceiling, at most the whole issue, over the issue. |
| 208 | 31 | `public static void main(String[] args) throws Exception` |  |

### 1. each bidder's rule against its constants (lines 240-358)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 117 | `static void section1()` |  |

### 2. one clearing yield (lines 359-421)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 61 | `static void section2()` |  |

### 3. cover and tail (lines 422-456)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 33 | `static void section3()` |  |

### 4. capped and filled (lines 457-526)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 68 | `static void section4()` |  |

### 5. the coupon and the price (lines 527-633)

| line | len | member | says |
|---:|---:|---|---|
| 528 | 105 | `static void section5()` |  |

### 6. PRICED ON THE CURVE (lines 634-716)

| line | len | member | says |
|---:|---:|---|---|
| 644 | 72 | `static void section6()` |  |

### THE GAME'S OWN SALES (sections 7, 8 and 11): a city that sells its paper (lines 717-777)

| line | len | member | says |
|---:|---:|---|---|
| 728 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 733 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 740 | 1 | `static double savings(Game g)` |  |
| 743 | 4 | `static MoneyAudit.Result play(Game g)` | A month, played with the narration off; the month's money audit comes back. |
| 749 | 3 | `static double cents(double v)` | Money to the cent, as every proceeds figure is. |
| 754 | 18 | `static Game lineupCity()` | HoldersCheck's city: given the money and the ground, a bank and three hundred houses, sixty months played. |
| 774 | 3 | `static long pieces(DebtManager ledger, Class<? extends Debt> kind)` | How many pieces of this class the city's debt book holds. |

### 7. the lineup in a city (lines 778-904)

| line | len | member | says |
|---:|---:|---|---|
| 779 | 125 | `static void section7(Game city)` |  |

### 8. the quote is the deal, and the settle pays the allotment (lines 905-1000)

| line | len | member | says |
|---:|---:|---|---|
| 906 | 94 | `static void section8(Game city)` |  |

### 11. the real run (D33) (lines 1001-1082)

| line | len | member | says |
|---:|---:|---|---|
| 1002 | 80 | `static void section11()` |  |

### SECTIONS 9 AND 10: THE SALES NOBODY PRESSES (CD2, slice 6; D28 to D30) (lines 1083-1150)

| line | len | member | says |
|---:|---:|---|---|
| 1105 | 7 | `static String printed(Runnable r)` | What a piece of work printed: the log is where the rollover and the builder say what they did. |
| 1114 | 10 | `static void roomTo(Game g, double faceRoom)` | Leaves the bank room for this much face of the city's paper and no more: its weighted book raised until the room the auction reads is that. |
| 1126 | 4 | `static Game reload(Game g)` | A city saved to its own scratch folder's slot 10 and loaded into another Game, as a player's reload. |
| 1132 | 5 | `static Game load(Game g)` | ...the same save loaded again, into a city of its own. |
| 1139 | 5 | `static int salesBy(Game g, String by)` | The sales automatic building and the rollover made in the month just run, by whom. |
| 1145 | 5 | `static int siteCount(Game g)` |  |

### 9. the rollover's own cap, and a short sale paid in full (lines 1151-1323)

| line | len | member | says |
|---:|---:|---|---|
| 1154 | 9 | `static Game rolloverTown(Path root, String label)` | A town with the rollover on, a 3-month note of D$20M on its books that falls due next month. |
| 1165 | 1 | **type** `record Scene(Rollover.Issue issue, double askFace, double hhCapped, double hhFilled, double room, double cash)` | What the rollover is about to roll, and the room that makes it short at the default cap and covered at Fill it. |
| 1172 | 12 | `static Scene scene(Game g, double cash)` | The rollover's one issue as the city stands with `cash` in the treasury and the deposit rate at 3% (so the households' appetite has a slope), with the bank's room ample, and the room that leaves the default cap short ... |
| 1185 | 138 | `static void section9(Path root) throws Exception` |  |

### 10. automatic building: its own cap, and the ask a short sale keeps open (lines 1324-1649)

| line | len | member | says |
|---:|---:|---|---|
| 1327 | 3 | `static BuildAdvice.Measure streets()` | The measure Streets, whose card is a Paved Road: the first the pass walks to in the fixture's town. |
| 1341 | 13 | `static double tuneDeposit(Game g, double gap, Cap cap)` | The deposit rate at which a bond for `gap` sold at the builder's cap of fair sells short, but a share of the issue that is over the smallest deal worth arranging (Game.isTinySale(): under it a short sale is no sale), ... |
| 1360 | 4 | `static void useUpTheRoom(Game g)` | The room used up and the deposit rate tuneDeposit() found, so that at a cap of fair the buyers bid a real share of an issue and the bank bids D$100k of face: a bond for an order sells short, over the smallest deal wor... |
| 1370 | 6 | `static String month(Game g, boolean ample)` | One month of the fixture's, run and printed: the room used up as above (unless `ample`), and the treasury's cash set to the reserve plus what the asks hold - the month's other bills are met from elsewhere, so what an ... |
| 1378 | 5 | `static double placedIn(AutoBuilder ab, int month)` | What the builder's orders placed in a month cost, ground with them: the cash they took out of the treasury. |
| 1384 | 265 | `static void section10(Path root) throws Exception` |  |

