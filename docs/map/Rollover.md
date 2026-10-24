# Rollover.java - 378 lines · 34 methods · 2 constants · model

`ham/citybuildersim/Rollover.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13).
> 
> WHY. Until 0.7.13 every piece of the city's paper was paid off out of the
> treasury's cash the month it fell due, and a treasury that could not meet
> one went to the central bank's advances for it - a term bond's whole face
> in a month. Jerus: "you should have an option, where it defualt toggles
> on, so basically the game checks whats going to mature next month, and
> issues what the treasury is lacking ... you can modify the toggle, so its
> either manual, aka you do it yourself, or that it defualts to same
> structure, or that it defualts to 12 month tbill. this doesnt take care of
> the treasury short of cash, thats the central bank thing, this only takes
> care of upcoming maturities."
> 
> THE RULE, EACH MONTH (Game, ROLLING WHAT FALLS DUE, between two presses):
> 
>   1. WHAT FALLS DUE: the principal every piece of the city's own paper
>      repays in the month about to run (Debt.principalDueNextMonth()), at
>      home and abroad, piece by piece.
>   2. THE NETTING, Jerus's "net of last year's surplus": the budget surplus
>      the city ran over the last NETTING_MONTHS (NationalAccounts
>      .getBalance(), a month at a time, as HistorySave keeps it) less what
>      earlier rollovers netted in the same months - never below nothing,
>      never more than the treasury holds, never more than falls due. That is
>      S (netting()). A deficit year nets nothing. What each rollover netted
>      is kept here (the ledger), so two months in a row cannot net the same
>      surplus twice - and since 0.7.14 what the city's fund took of the
>      year's surplus (noteFundTook()), which goes first: through the year
>      the rollover also leaves the dial's share of the year so far alone
>      (Game.fundReservation(), nothing at a dial of 0).
>   3. THE ISSUE: what falls due less S, each piece its share pro rata,
>      sized so the cash it brings covers that share - what the treasury is
>      lacking. Since 0.7.15, what the central bank holds of it and rolls
>      itself is not the treasury's to raise: it takes that par of the new
>      paper at issue, on top (Game, THE CENTRAL BANK ROLLS ITS OWN, AT
>      ISSUE), so the issue is sized for the market's part less S. S is
>      still struck on all of what falls due, and (Jerus: "Surplus pays
>      everyone") it pays the market's part first and then the central
>      bank's par, which then rolls only what S left of it.
>      SAME_STRUCTURE rolls each piece into its own instrument, term
>      and currency (Jerus: "dollars into dollars"; with the window abroad
>      shut, dollar paper rolls at home, and the log says so);
>      TWELVE_MONTH_BILL rolls every piece into a local note of BILL_MONTHS.
>      Pieces rolling into the same paper are one issue, and an issue under
>      Game.minimumIssueSize() is not arranged: its share is paid out of cash.
>   4. THE CAP AND A SHORT SALE (CD2, D28 and D29): each issue is an auction
>      at the rollover's own cap (getCap()), fair plus a point unless the
>      player moved it. Capped and short, it sells what was bid; the maturity
>      is a promise and is paid in full all the same - the treasury's cash
>      pays it, and past the cash the central bank's advances, as every
>      promise is paid (TreasuryLine.PRINCIPAL) - and the inbox names what the
>      advances paid. Filled, it pays the tail instead, and the inbox names
>      the tail when it passes the default cap (Auction.MAX_TAIL).
> 
> WHAT ROLLING FOR CASH DOES. Paper sells under its face - a note by its
> discount, a bond by its coupon (since CD2 the clearing yield rounded down
> to an eighth, so a new line sells just under 100; until then a term loan's
> redemption premium, its coupon a third of its yield), any issue by the
> fixed fee (and until CD2 the underwriting spread, Game.costOfIssuance()) -
> so the face whose cash covers the maturity is more than the face falling
> due. Rolling for cash capitalises the interest of the paper it replaces
> ... (23 more lines in the source)

**Uses:** [Auction](Auction.md) (6)

**Used by (18):** [AuctionCheck](AuctionCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [BuybackCheck](BuybackCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityNeeds](CityNeeds.md), [DataSave](DataSave.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [SaveFileCheck](SaveFileCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 98 | `Rollover.Mode.MANUAL` | Nothing automatic: every maturity is paid out of cash, as before 0.7.13. |
| 100 | `Rollover.Mode.SAME_STRUCTURE` | Each piece rolls into its own instrument, term and currency - dollar paper at home while the window abroad is shut. |
| 102 | `Rollover.Mode.TWELVE_MONTH_BILL` | Every piece rolls into a local bill of BILL_MONTHS. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 106 | `Rollover.NETTING_MONTHS` | `12` | How far back the surplus that nets a maturity is read, in months: Jerus's "net of last year's surplus" - a year. |
| 109 | `Rollover.BILL_MONTHS` | `12` | The bill TWELVE_MONTH_BILL rolls into, in months: twelve, the longest bill the treasury sells (bills run 1 to 12 months, Auction.BILL_MONTHS). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 214 | `private Mode mode` |  |
| 217 | `private final List<double[]> ledger` | What each rollover netted, and since 0.7.14 what the city's fund took (noteFundTook()), and the month: {month, netted}, oldest first, only the last NETTING_MONTHS kept. |
| 220 | `private int lastMonth` | The last rollover, and the run's: what the Finances page and the playtest read, saved (recordToSave()). |
| 221 | `private double lastDue, lastNetted, lastIssued, lastRaised` |  |
| 222 | `private double issuedLifetime, raisedLifetime, nettedLifetime` |  |
| 223 | `private int issuesLifetime, atHomeForDollarsLifetime` |  |
| 236 | `private Auction.Cap cap` | THE CAP THE ROLLOVER SELLS AT (CD2, D28; Jerus, 2026-10-12: "each its own"): the highest yield its auctions take, fair plus a point by default, another tail or a yield, or Fill it. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 93 | 286 | **type** `public final class Rollover` | What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13). |
| 96 | 8 | **type** `public enum Mode` | The three settings, saved by name. |
| 122 | 25 | **type** `public record Issue(String type, int term, boolean foreign, double cash, double face, int pieces, int atHom...` | One issue the rollover makes: the instrument by the quote functions' names ("Note", "Serial", "Term": the bill, the amortizing bond and the bond since CD2; "Float" and "Linked": the floating-rate note and the inflatio... |
| 126 | 10 | `public String paper()` _(in Rollover.Issue)_ | The paper, in words: "a 20-year bond in dollars", "a 12-month bill". |
| 138 | 8 | `public String why(Mode mode)` _(in Rollover.Issue)_ | Why it is this paper, in words. |
| 168 | 36 | **type** `public record Plan(Mode mode, double due, double dueAbroad, double surplus, double used, double cash, doubl...` | What the rollover does this month, worked out before it does it: the setting; what falls due, and how much of it abroad; the surplus over the last year and what is used of it - what earlier rollovers netted, and since... |
| 172 | 4 | `public Plan(Mode mode, double due, double dueAbroad, double surplus, double used, double cash, double netted, List<Issue> issues)` _(in Rollover.Plan)_ |  |
| 178 | 1 | `public double toRoll()` _(in Rollover.Plan)_ | What falls due, less what the central bank rolls itself, less what is netted: the cash the issues must bring between them, before any too small to be worth arranging (Game.minimumIssueSize()). |
| 180 | 1 | `public double centralBankRunsOff()` _(in Rollover.Plan)_ | What falls due that the central bank holds past its dial: repaid it, and raised from the market or netted with the rest. |
| 182 | 5 | `public double toRaise()` _(in Rollover.Plan)_ | The cash the issues are sized to bring between them. |
| 188 | 5 | `public double issued()` _(in Rollover.Plan)_ | The face their quotes give for it, each quoted on the books as they stand: more than the cash, by the paper's discount, premium and costs. |
| 200 | 3 | `public double fromCash()` _(in Rollover.Plan)_ | What the treasury's cash pays of what falls due (0.7.32, the Finances tab's NEXT MONTH bar): by hand, all of the market's part; rolling, what no issue raises - a share under the smallest deal worth arranging, or all o... |
| 206 | 7 | `public static String words(Mode mode)` | The setting in words, for the log and the Finances tab: "by hand", "in the same structure", "into 12-month bills". |
| 225 | 1 | `public Mode getMode()` |  |
| 226 | 1 | `public void setMode(Mode mode)` |  |
| 238 | 1 | `public Auction.Cap getCap()` |  |
| 239 | 1 | `public void setCap(Auction.Cap cap)` |  |
| 247 | 4 | `public static double netting(double surplusYear, double usedInYear, double cash, double due)` | S: the surplus over the last year less what is already used of it - what earlier rollovers netted in it, and since 0.7.14 the city's fund's part (Game.rolloverPlan()) - never below nothing, never more than the treasur... |
| 253 | 7 | `public double usedInYear(int month)` | What the rollovers run in the NETTING_MONTHS ending with this month netted between them - and, since 0.7.14, what the city's fund took of the surplus in them (noteFundTook()). |
| 271 | 4 | `void noteFundTook(int month, double fromSurplus)` | THE CITY'S FUND TOOK PART OF THE YEAR'S SURPLUS (0.7.14): its year-end pay-in's share of the surplus, entered on this ledger in the month it was taken, so the rollover nets only what the fund did not take - the surplu... |
| 277 | 14 | `void record(int month, double due, double netted, double issued, double raised, int issues, int atHomeForDollars)` | A rollover ran: what fell due, what it netted, the face its issues came to and the cash they raised, in the month it ran in. |
| 293 | 1 | `public int getLastMonth()` | The month the last rollover ran in, or -1 if none has. |
| 295 | 1 | `public double getLastDue()` | ...what fell due then, in local money. |
| 297 | 1 | `public double getLastNetted()` | ...what it netted from the year's surplus. |
| 299 | 1 | `public double getLastIssued()` | ...the face its issues came to, in local money. |
| 301 | 1 | `public double getLastRaised()` | ...and the cash they brought: less than their face by their discount and costs. |
| 303 | 1 | `public double getIssuedLifetime()` | The face the rollover has issued since the setting was first on, in local money. |
| 305 | 1 | `public double getRaisedLifetime()` | ...and the cash it raised. |
| 307 | 1 | `public double getNettedLifetime()` | ...and netted from surplus. |
| 309 | 1 | `public int getIssuesLifetime()` | How many issues it has made. |
| 311 | 1 | `public int getAtHomeForDollarsLifetime()` | How many of them rolled dollar paper at home because the window abroad was shut. |
| 314 | 8 | `public double[] ledgerToSave()` | The ledger, for the save: {month, netted} pairs, oldest first. |
| 324 | 5 | `public void restoreLedger(double[] saved)` | ...and back. |
| 331 | 4 | `public double[] recordToSave()` | The last rollover and the run's, for the save. |
| 337 | 14 | `public void restoreRecord(double[] saved)` | ...and back. |
| 352 | 6 | `private void clearRecord()` |  |
| 360 | 6 | `public void reset()` | A city founded from nothing: nothing automatic, nothing netted. |
| 368 | 10 | `public void redenominate(double scale)` | Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM). |

