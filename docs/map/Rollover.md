# Rollover.java - 312 lines · 29 methods · 2 constants · model

`ham/citybuildersim/Rollover.java` - generated 2026-09-27 by CodeMap; line numbers are as of that run.

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
>      lacking. SAME_STRUCTURE rolls each piece into its own instrument, term
>      and currency (Jerus: "dollars into dollars"; with the window abroad
>      shut, dollar paper rolls at home, and the log says so);
>      TWELVE_MONTH_BILL rolls every piece into a local note of BILL_MONTHS.
>      Pieces rolling into the same paper are one issue, and an issue under
>      Game.minimumIssueSize() is not arranged: its share is paid out of cash.
> 
> WHAT ROLLING FOR CASH DOES. Paper sells under its face - a note by its
> discount, a term loan by its redemption premium (its coupon is a third of
> its yield), any issue by the underwriting spread and the fixed fee - so
> the face whose cash covers the maturity is more than the face falling
> due. Rolling for cash capitalises the interest of the paper it replaces
> into the new principal, at every roll, at whatever rate the city then
> pays. Nothing in the model caps what a city may sell of its own paper at
> home - the rate rises with the debt to its ceiling and the bank buys the
> rest (0.7.13's notes) - so in a city that runs deficits the principal
> compounds. Measured on a Lean founding (8 seeds, 4,005 months; 0.7.13's
> notes, round 2): in seed 0, a city in deficit, every twenty-year loan
> rolled into one of 1.7 to 2.9 times its face, the city's rate climbing to
> its ceiling (a twenty-year yield of up to 26.8%), and the city owed $476.6
> trillion of its own paper by month 3,948 - $760.6 trillion of face issued
> for $284.1 trillion of cash. The central bank advanced $652.6 trillion
> for the coupons, 3,633 months with its advances at their ceiling (a
> coupon is a promise, paid past it), and the city ended at
> 2,423 people (145,800 by hand). The other seven seeds rolled $1.68 billion
> of face for $1.26 billion, and repaid all of it. Jerus chose this sizing
> over rolling face for face (round 1), knowing it.
> 
> WHAT IT IS NOT. It does not cover a treasury short of cash for its
> ... (6 more lines in the source)

**Used by (10):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [DataSave](DataSave.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [SaveFileCheck](SaveFileCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 81 | `Rollover.Mode.MANUAL` | Nothing automatic: every maturity is paid out of cash, as before 0.7.13. |
| 83 | `Rollover.Mode.SAME_STRUCTURE` | Each piece rolls into its own instrument, term and currency - dollar paper at home while the window abroad is shut. |
| 85 | `Rollover.Mode.TWELVE_MONTH_BILL` | Every piece rolls into a local note of BILL_MONTHS. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `Rollover.NETTING_MONTHS` | `12` | How far back the surplus that nets a maturity is read, in months: Jerus's "net of last year's surplus" - a year. |
| 92 | `Rollover.BILL_MONTHS` | `12` | The note TWELVE_MONTH_BILL rolls into, in months: twelve, the longest note the treasury sells (the Finances tab's notes run 3 to 12 months). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 162 | `private Mode mode` |  |
| 165 | `private final List<double[]> ledger` | What each rollover netted, and since 0.7.14 what the city's fund took (noteFundTook()), and the month: {month, netted}, oldest first, only the last NETTING_MONTHS kept. |
| 168 | `private int lastMonth` | The last rollover, and the run's: what the Finances page and the playtest read, saved (recordToSave()). |
| 169 | `private double lastDue, lastNetted, lastIssued, lastRaised` |  |
| 170 | `private double issuedLifetime, raisedLifetime, nettedLifetime` |  |
| 171 | `private int issuesLifetime, atHomeForDollarsLifetime` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 76 | 237 | **type** `public final class Rollover` | What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13). |
| 79 | 8 | **type** `public enum Mode` | The three settings, saved by name. |
| 103 | 23 | **type** `public record Issue(String type, int term, boolean foreign, double cash, double face, int pieces, int atHom...` | One issue the rollover makes: the instrument by the quote functions' names ("Note", "Serial", "Term"), its term in their units (months for a note, years for the rest), whether it is sold abroad in dollars, the cash it... |
| 107 | 8 | `public String paper()` _(in Rollover.Issue)_ | The paper, in words: "a 20-year term loan in dollars", "a 12-month note". |
| 117 | 8 | `public String why(Mode mode)` _(in Rollover.Issue)_ | Why it is this paper, in words. |
| 135 | 17 | **type** `public record Plan(Mode mode, double due, double dueAbroad, double surplus, double used, double cash, doubl...` | What the rollover does this month, worked out before it does it: the setting; what falls due, and how much of it abroad; the surplus over the last year and what is used of it - what earlier rollovers netted, and since... |
| 138 | 1 | `public double toRoll()` _(in Rollover.Plan)_ | What falls due less what is netted: the cash the issues must bring between them, before any too small to be worth arranging (Game.minimumIssueSize()). |
| 140 | 5 | `public double toRaise()` _(in Rollover.Plan)_ | The cash the issues are sized to bring between them. |
| 146 | 5 | `public double issued()` _(in Rollover.Plan)_ | The face their quotes give for it, each quoted on the books as they stand: more than the cash, by the paper's discount, premium and costs. |
| 154 | 7 | `public static String words(Mode mode)` | The setting in words, for the log and the Finances tab: "by hand", "in the same structure", "into 12-month notes". |
| 173 | 1 | `public Mode getMode()` |  |
| 174 | 1 | `public void setMode(Mode mode)` |  |
| 182 | 4 | `public static double netting(double surplusYear, double usedInYear, double cash, double due)` | S: the surplus over the last year less what is already used of it - what earlier rollovers netted in it, and since 0.7.14 the city's fund's part (Game.rolloverPlan()) - never below nothing, never more than the treasur... |
| 188 | 7 | `public double usedInYear(int month)` | What the rollovers run in the NETTING_MONTHS ending with this month netted between them - and, since 0.7.14, what the city's fund took of the surplus in them (noteFundTook()). |
| 206 | 4 | `void noteFundTook(int month, double fromSurplus)` | THE CITY'S FUND TOOK PART OF THE YEAR'S SURPLUS (0.7.14): its year-end pay-in's share of the surplus, entered on this ledger in the month it was taken, so the rollover nets only what the fund did not take - the surplu... |
| 212 | 14 | `void record(int month, double due, double netted, double issued, double raised, int issues, int atHomeForDollars)` | A rollover ran: what fell due, what it netted, the face its issues came to and the cash they raised, in the month it ran in. |
| 228 | 1 | `public int getLastMonth()` | The month the last rollover ran in, or -1 if none has. |
| 230 | 1 | `public double getLastDue()` | ...what fell due then, in local money. |
| 232 | 1 | `public double getLastNetted()` | ...what it netted from the year's surplus. |
| 234 | 1 | `public double getLastIssued()` | ...the face its issues came to, in local money. |
| 236 | 1 | `public double getLastRaised()` | ...and the cash they brought: less than their face by their discount and costs. |
| 238 | 1 | `public double getIssuedLifetime()` | The face the rollover has issued since the setting was first on, in local money. |
| 240 | 1 | `public double getRaisedLifetime()` | ...and the cash it raised. |
| 242 | 1 | `public double getNettedLifetime()` | ...and netted from surplus. |
| 244 | 1 | `public int getIssuesLifetime()` | How many issues it has made. |
| 246 | 1 | `public int getAtHomeForDollarsLifetime()` | How many of them rolled dollar paper at home because the window abroad was shut. |
| 249 | 8 | `public double[] ledgerToSave()` | The ledger, for the save: {month, netted} pairs, oldest first. |
| 259 | 5 | `public void restoreLedger(double[] saved)` | ...and back. |
| 266 | 4 | `public double[] recordToSave()` | The last rollover and the run's, for the save. |
| 272 | 14 | `public void restoreRecord(double[] saved)` | ...and back. |
| 287 | 6 | `private void clearRecord()` |  |
| 295 | 5 | `public void reset()` | A city founded from nothing: nothing automatic, nothing netted. |
| 302 | 10 | `public void redenominate(double scale)` | Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM). |

