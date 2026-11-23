# TreasuryCheck.java - 1,884 lines · 45 methods · 5 constants · harnesses

`ham/citybuildersim/TreasuryCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> Plays a city and audits what the screens say the treasury did. Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus: "show how much was the actual month change, like in
> the next month button it shows 3k but sometimes cause of land buybacks or
> sales it was actually more or less." The dome (the header's TREASURY tile
> since 0.7.21, its money block since 0.7.24) and the Government Overview
> now both print a measured cash movement, and a measured figure that is
> measured wrongly is worse than the estimate it replaced - it looks
> authoritative.
> 
> The nine things it will not let past:
> 
>   1. THE WINDOW CLOSES. Each month's opening balance is the previous month's
>      closing balance, with no gap. If it ever is not, a month of the player's
>      own land and building decisions has fallen down the crack between two
>      windows and the figure on the button is understated by exactly that.
> 
>   2. THE CLOSING BALANCE IS THE CASH. Whatever the recorder wrote down is
>      what getCash() says a moment later.
> 
>   3. THE BRIDGE FOOTS. Surplus, plus paper issued, less principal repaid,
>      plus the "everything else" row, equals the change. That is an identity
>      by construction - which is the point: if it ever breaks, the screen is
>      drawing four rows that do not add to the total printed under them.
> 
>   4. IT SURVIVES A SAVE. The reconciliation is on the first screen a
>      returning player opens, so it has to be there before a month is played.
> 
>   5. AND ON A CITY NOBODY TOUCHED, THE RESIDUAL ROW IS EMPTY. This one is
>      new, and it is the assertion this harness was built to earn the right
>      to make. The "everything else" row is real money in a played city -
>      reserves bought, bonds retired early, capital put into the bank - but
>      none of those happen on their own, so a hands-off run has nothing to put
>      in it and every dollar must be described by the budget or its borrowing.
> 
>      It did not used to be. Three things were wrong, all found by asking why
>      this row was not zero:
> 
>        - the government's books were struck from a stash taken half a tick
>          early, so 116 of 120 months reported the PREVIOUS month's land and
>          buildings, largest $118.42k
>        - the bank's profit tax reached the treasury's cash and appeared on no
>          budget, which was the whole of what was left: 110 months adrift by
>          exactly the bank's tax and by nothing else
>        - a subsidy left the treasury on the spot and was on no budget either,
>          which nothing caught because the dial is off by default. Hence the
>          second city below, which turns it on.
> 
>   6. AND THE ROW OPENS. Since 2026-09-18 the "everything else" row is a
>      TreasuryJournal - capital into the bank, reserves, a buyback, the
>      students' loans, and the two lines the budget balance omits - with a
>      "Not accounted for" line under it. The third city below does each of
>      those things between two presses and asserts the line it left, that
>      what IS on the budget (land, a building) is not named twice, that the
>      bridge still foots with the journal and the residual in it, that the
>      residual is smaller than what the journal explained - and what it is:
>      the first coupon, booked the month it is charged and paid the month
>      after - and that the whole journal comes back from a save line for
>      line. The "Raised by issuing paper" row is asserted here too, because
> ... (49 more lines in the source)

**Uses:** [Game](Game.md) (88), [PriceLevel](PriceLevel.md) (38), [Debt](Debt.md) (34), [Rollover](Rollover.md) (27), [DebtManager](DebtManager.md) (23), [TreasuryJournal](TreasuryJournal.md) (15), [LongTermBond](LongTermBond.md) (12), [GameFiles](GameFiles.md) (5), [NationalAccounts](NationalAccounts.md) (5), [MoneyAudit](MoneyAudit.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [ShortTermTBill](ShortTermTBill.md) (4), [TaxPolicy](TaxPolicy.md) (2), [MediumTermBond](MediumTermBond.md) (2), [TreasuryFund](TreasuryFund.md) (1), [Sectors](Sectors.md) (1), [LandParcel](LandParcel.md) (1), [PriceLevelCheck](PriceLevelCheck.md) (1), [Founding](Founding.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (2):** [CallCheck](CallCheck.md), [ScaleCheck](ScaleCheck.md)

## Sections

| line | section |
|---:|---|
| 244 | · · 1. no gap between windows |
| 250 | · · 2. the closing IS the cash |
| 254 | · · 3. the bridge foots |
| 262 | · · 5. and on a hands-off city there is nothing in it |
| 280 | · AND AGAIN WITH THE SUBSIDY DIAL ON. |
| 343 | · AND IT HAS TO SURVIVE A SAVE. |
| 387 | · · and the first month back still has no gap |
| 392 | · AND THE ROW OPENS. |
| 566 | · 7. ROLLING WHAT FALLS DUE (0.7.13) |
| 569 | · 8. FROM EARNED TO THE BUDGET (0.7.31) |
| 572 | · 9. THE CITY'S PAPER ON AMORTISED COST (CD3) |
| 575 | · THE REPORT. |
| 595 | 7. ROLLING WHAT FALLS DUE (0.7.13). |
| 626 | 8. FROM EARNED TO THE BUDGET (0.7.31). |
| 1293 | 9. THE CITY'S PAPER ON AMORTISED COST (CD3, spec-city-debt.md section 5). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 121 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |
| 1187 | `TreasuryCheck.OPEN_SHARE` | `.9` | The share of the share the window shuts at that the six-month dollar note's next year of service is, in the city whose window must be open: just under it (FS13: the exports are set by hand, so the share can be brought... |
| 1190 | `TreasuryCheck.SHUT_SHARE` | `1.1` | ...and in its twin, whose window must be shut by that share alone: just over it. |
| 1193 | `TreasuryCheck.DOLLAR_NOTE_IN_MINIMUMS` | `10` | The dollar note's size, in times the smallest issue the rollover makes (Game.minimumIssueSize()), so that what falls due abroad is an issue it can make. |
| 1196 | `TreasuryCheck.NOTE_IN_SURPLUSES` | `3` | Each of the two notes falling due in the city in surplus has a cash of this many of the year's surplus, so that a surplus netted against it is part of it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 118 | `static int fails` |  |
| 1335 | `static int booksAsserted` | How many things the books part asserted, for its report line. |
| 1366 | `final DebtManager manager` |  |
| 1367 | `final Game payer` |  |
| 1368 | `final Debt piece` |  |
| 1369 | `final java.util.List<double[]> rows` |  |
| 1370 | `int months` |  |
| 1371 | `double sumExpense, sumCoupon, sumAmortised` |  |
| 1372 | `double worstPvGap, worstTotalsGap` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1769 | **type** `public class TreasuryCheck` | Plays a city and audits what the screens say the treasury did. |
| 123 | 8 | `static void near(String what, int month, double actual, double expected)` |  |
| 142 | 8 | `static void near(String what, int month, double actual, double expected, double size)` | ...and the journal's reconciliation, which subtracts one cash balance from another (0.7.54): MoneyAudit.tolerance(TOLERANCE, the treasury's size) - TOLERANCE exactly below 2^27 units (1.3e8), from there 64 of a double... |
| 152 | 3 | `static double treasurySize(Game g)` | The treasury's size this month, for the journal's reconciliation: the larger of its opening and closing balances. |
| 161 | 8 | `static void nearOf(String what, int month, double actual, double expected, double size)` | Two figures that must agree to a part in a billion of the figures they are made of (0.7.31): the walk from EARNED to the budget adds a dozen figures in the millions, so an absolute tenth of a cent is too fine. |
| 175 | 11 | `static double[] earnedWalk(Game g)` | The walk from EARNED to the budget, as the Government tab draws it: {EARNED, the steps summed, what they leave, the budget's balance, the size of the figures in it}. |
| 188 | 4 | `static void check(String what, boolean ok)` | A fact that is either so or not, printed either way so the run reads as a list. |
| 194 | 4 | `static TreasuryJournal.Entry line(java.util.List<TreasuryJournal.Entry> journal, String label)` | The journal line with this label, or null when the month has none. |
| 200 | 4 | `static double amount(java.util.List<TreasuryJournal.Entry> journal, String label)` | The amount on the journal line with this label, or 0 when there is none. |
| 205 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 221 | 3 | `static void nobodyPressesTheBankButton(Game g)` | A hands-off city is one whose player presses nothing, and the bank's button is the one thing newGame() leaves pressed for him: it sets the treasury to resolve a failed bank the month it fails (TreasuryFund.RescueMode.... |
| 225 | 369 | `public static void main(String[] args)` |  |

### 7. ROLLING WHAT FALLS DUE (0.7.13). (lines 595-625)

### 8. FROM EARNED TO THE BUDGET (0.7.31). (lines 626-1292)

| line | len | member | says |
|---:|---:|---|---|
| 638 | 116 | `static void earned()` |  |
| 760 | 3 | `static void openTheLevers(Game g)` | THE DEFAULTS PUT BACK IN THE FIXTURE (R0, OP1): this harness audits the treasury's bridge with its taxes, premiums and grant flowing, and until OP1 on history's dates they were shut until 1916-1966 (held at zero, thei... |
| 765 | 6 | `static Game founded(String label)` | A city founded as a player founds one: newGame(), so it rolls in the same structure. |
| 773 | 7 | `static String press(Game g)` | One press, its printing kept - the log is where a rollover says what it did. |
| 782 | 5 | `static void quietly(Runnable work)` | Some quiet work: an issue's receipt, a save. |
| 789 | 7 | `static Debt paper(Game g, String type, int months, int started, boolean foreign)` | The piece of paper of this type, term and currency issued in this month, or null. |
| 798 | 3 | `static boolean audited(Game g)` | The press closed its audit, and nothing moved after it struck. |
| 802 | 380 | `static void rolling()` |  |
| 1202 | 4 | `static double serviceShare(Game g)` | What the window's own test reads (DebtManager.foreignWindowReason()): the next year's service on what the city owes abroad, as a share of a year's exports. |
| 1208 | 3 | `static void sellsNothingAbroad(Game g)` | The city sells nothing abroad (FS13): its exports set by hand to nothing (DebtManager.setTrade(), the import cover as it stands), so that owing dollars shuts its window by that alone, whatever the path's exports are. |
| 1213 | 3 | `static double dollarNoteUsd(Game g)` | The dollar note's face, in US$: DOLLAR_NOTE_IN_MINIMUMS of the smallest issue the rollover makes, at the month's D$. |
| 1222 | 5 | `static void sellsAbroad(Game g)` | The city sells abroad (FS13): its exports set by hand (DebtManager.setTrade(), the import cover as it stands) to the level at which a year of the dollar note's face would be OPEN_SHARE of the share the window shuts at... |
| 1233 | 5 | `static void tradeAtShare(Game g, double of)` | The exports set by hand so that the next year's service on what the city owes abroad is exactly {@code of} times WINDOW_SHUT_SERVICE_SHARE of a year of them (DebtManager.nextYearService(), the sum the window's own tes... |
| 1240 | 4 | `static String pressAtShare(Game g, double of)` | One press with the exports set to the share first, its printing kept. |
| 1251 | 7 | `static Debt dollarNoteAtShare(Game g, double of)` | A six-month dollar note on a city that sells abroad (sellsAbroad()), its size the fixture's (dollarNoteUsd()), and then its exports set so that the next year's service on it is {@code of} times WINDOW_SHUT_SERVICE_SHA... |
| 1265 | 9 | `static void useOfTheYearsSurplus(Game g, double leavingUnused)` | The year's surplus used in the ledger, all but {@code leavingUnused} of it, by construction (FS13): the ledger is given, in this month, the netting that brings what it holds (Rollover.usedInYear()) up to the surplus l... |
| 1280 | 5 | `static java.util.TreeSet<Integer> noteDueMonths(int term, int presses)` | The plan-months, counted from the first press, at which a note of {@code term} months falls due in a run of {@code presses}: when one month of it is left, and then again every term-1 months, as it rolls into a note of... |
| 1287 | 5 | `static java.util.TreeSet<Integer> serialDueMonths(int remaining, int presses)` | ...and a serial's: its instalments fall due when its remaining months are one over a whole year, the harness's own reading of the paper's schedule. |

### 9. THE CITY'S PAPER ON AMORTISED COST (CD3, spec-city-debt.md section 5). (lines 1293-1884)

| line | len | member | says |
|---:|---:|---|---|
| 1338 | 4 | `static void books(String what, boolean ok)` | One fact of the books, printed either way. |
| 1344 | 10 | `static void booksNear(String what, double actual, double expected, double tolerance)` | Two figures of the books that must agree to a tolerance, printed either way with the figures on a failure. |
| 1365 | 52 | **type** `static final class Life` | A piece's months on a manager's books, stepped by the manager's own processAllDebts() - the books, the accretion and the month's payments in the game's order - with what each month left: {expense, coupon, amortised, c... |
| 1374 | 5 | `Life(DebtManager manager, Game payer, Debt piece)` _(in TreasuryCheck.Life)_ |  |
| 1381 | 20 | `void step(int n)` _(in TreasuryCheck.Life)_ | Steps n months, or to the piece's end when n is negative. |
| 1403 | 1 | `double[] month(int m)` _(in TreasuryCheck.Life)_ | The month's row, counted from 1. |
| 1406 | 4 | `boolean falls(int from, int to)` _(in TreasuryCheck.Life)_ | True when the carrying value closed every month from..to lower than it opened. |
| 1412 | 4 | `boolean rises(int from, int to)` _(in TreasuryCheck.Life)_ | ...and higher. |
| 1418 | 13 | `static void books()` |  |
| 1440 | 54 | `static void balance()` | g. |
| 1504 | 61 | `static void buyback()` | f. |
| 1573 | 49 | `static void playedCity()` | e. |
| 1624 | 3 | `static Debt onlyPiece(Game g)` | The one piece of paper a city owes, or null when it owes none or several. |
| 1640 | 80 | `static void dollarPaper()` | d. |
| 1722 | 44 | `static void calmTenYear(Game payer)` | a. |
| 1768 | 71 | `static void premiumBillAmortizing(Game payer)` | b. |
| 1841 | 43 | `static void reopenedLine(Game payer)` | c. |

