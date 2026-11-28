# CentralBankPaceCheck.java - 1,086 lines · 37 methods · 21 constants · harnesses

`ham/citybuildersim/CentralBankPaceCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Proves the central bank's pace on its holdings and its auction share (CBK2): the pace dial both ways, the runoff's caps,
> sales at market with the gain or loss realised, the auction share at the auction's price and inside the dial, the money
> audit over all of it, and the law that bars direct purchases. Not part of the game.
> 
> WHY THIS EXISTS. Jerus, 2026-11-26: "setting it at 10% doesnt mean it goes to 10% right away, just means thats teh cap",
> and "when you issue debt the ability to just issue directly to the central bank? if real life is not like that then no".
> Until CBK2 the pace was QE_SPEED's, a quarter of the larger setting a month, never the player's; QT was whatever ran off
> past the dial plus sales at that pace; and the bank bought at issue only its own maturing par. A pace that is wrong by a
> tenth moves the book a tenth too fast every month and nothing else in the game would say so; a purchase at the wrong price
> is money made that the audit sees and the bank's books do not agree with.
> 
> WHAT THIS HAS TO PROVE, every one a fixture that CAUSES its condition on one town (a five-year serial the households, the
> bank and the central bank each hold part of, saved a month before its first slice) loaded fresh for each case:
> 
>   1. THE PACE BOTH WAYS. A set pace moves the holding up by exactly pace of the term paper in a month with nothing falling
>      due, and down by exactly the pace in sale mode; Auto's step is QE_SPEED of the larger of the dial, the setting before
>      it and the share held, to the bit, and a played month moves by it - or by what is left to the dial when that is nearer.
> 
>   2. THE RUNOFF'S CAPS HOLD. Under runoff only nothing is sold; what runs off of its maturing par is the pace when the pace
>      binds, all its par over the dial when the pace does not, and what it holds over its dial when that is the least.
> 
>   3. SALES AT MARKET, GAINS AND LOSSES REALISED. What a sale is paid is the face given up at each piece's market value per
>      face; the paper gains line is that price less the face - a loss with the rates up, a gain with them down.
> 
>   4. AUCTION PURCHASES AT THE AUCTION'S PRICE AND INSIDE THE CAP. Under the law, the share of the face the market bought, at
>      the issue's own price per face, pro rata over the pieces; with the dial near, no more than the dial's room; none at or
>      over the dial; and the money made is on the money statement's MADE lines, closing.
> 
>   5. WITH THE LAW OFF, NO NEW PAPER AT AUCTION beyond the rollover, whatever the share; and the law on with a share of
>      nothing takes nothing.
> 
>   6. CREDIBILITY. Lending the treasury at auction costs nothing up to the anchored share of a year's GDP and its full at
>      the unanchored, in a straight line between; the full cost takes a FINANCING_LOSS_MONTHS-th of the way to KMIN a month
>      and adds up to FINANCING_PREMIUM to expected inflation, bounded both ways over fifty years, as the cases measured. On
>      twin towns, the one whose bank takes the market's face at auction is believed less, expects more inflation and has the
>      weaker currency; when it stops, the stock is forgotten a twelfth a month and the trust comes back only at GAIN_MONTHS.
> 
>   7. DEFAULTS AND SAVES. A new city's rules are Auto, runoff and sales, no share, the law off - the rules before CBK2 - and
>      the fixture loads at them; every rule, the auction's lifetimes, the lending stock and the premium come back from a
>      save to the bit; and a save from before CBK2 (no rules key, no premium, the bank's array without its three slots)
>      reads the defaults and plays its next month to the bit as the same save with them.
> 
>   8. THE MONEY AUDIT TO THE CENT, over every month any section played.

**Uses:** [Expectations](Expectations.md) (71), [HoldingsRules](HoldingsRules.md) (68), [Game](Game.md) (50), [CentralBank](CentralBank.md) (48), [Debt](Debt.md) (22), [GameFiles](GameFiles.md) (8), [DebtManager](DebtManager.md) (5), [PriceLevel](PriceLevel.md) (5), [CentralBankCheck](CentralBankCheck.md) (5), [CentralBankStatements](CentralBankStatements.md) (5), [MoneyAudit](MoneyAudit.md) (4), [Rollover](Rollover.md) (3), [PriceIndex](PriceIndex.md) (2), [HouseholdBalance](HouseholdBalance.md) (1), [SectorStatements](SectorStatements.md) (1)

## Sections

| line | section |
|---:|---|
| 107 | the helpers |
| 261 | the fixture |
| 279 | · 8. the money audit, to the cent |
| 333 | 1. THE PACE BOTH WAYS |
| 408 | 2. THE RUNOFF'S CAPS HOLD |
| 490 | 3. SALES AT MARKET, GAINS AND LOSSES REALISED |
| 543 | 4. AUCTION PURCHASES AT THE AUCTION'S PRICE AND INSIDE THE CAP |
| 655 | 5. WITH THE LAW OFF, NO NEW PAPER AT AUCTION BEYOND THE ROLLOVER |
| 710 | 6. CREDIBILITY |
| 910 | 7. DEFAULTS AND SAVES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `CentralBankPaceCheck.SLOT` | `10` | The scratch slot the fixture is saved in: the assistant's slot, in a scratch folder (never the player's 1-9). |
| 63 | `CentralBankPaceCheck.PACE` | `.01` | The pace the set-pace cases use: a hundredth of the term paper a month, small beside the .4 the fixture holds so that neither the dial nor the paper there is to buy or sell stops it. |
| 66 | `CentralBankPaceCheck.DIAL_UP` | `.9` | The dial the buying cases raise it to: far above the holding, so that the pace and not the dial is what stops it. |
| 69 | `CentralBankPaceCheck.DIAL_DOWN` | `.1` | The dial the selling cases lower it to: far below the holding, so that the pace and not the dial is what stops it. |
| 72 | `CentralBankPaceCheck.DIAL_NEAR` | `.01` | How far above the holding the near-dial case puts the dial: nearer than the quarter of the larger setting Auto allows. |
| 75 | `CentralBankPaceCheck.SHARE` | `.25` | The auction share the cases give the central bank: a quarter of the face the market buys. |
| 78 | `CentralBankPaceCheck.ROOM_OF_BID` | `.5` | Of the share's bid, the part the dial's room allows in the capped case: half, so that the cap binds with the bid unmet. |
| 81 | `CentralBankPaceCheck.RATES_UP` | `.04` | How far the policy rate is put up at a time in the loss case, until the paper stands under face (CentralBankCheck puts it up four points for a bond sold at the new rate; this paper carries the old, higher coupon). |
| 84 | `CentralBankPaceCheck.MOST_RATE_STEPS` | `10` | The most steps the loss case puts the rate up before it gives up on the paper standing under face. |
| 87 | `CentralBankPaceCheck.UNDER_FACE` | `.01` | How far under its face the paper must stand for the loss case: more than the month's own re-strike of the curve moves its price (0.2% where nothing else changed), so the sign holds through the window. |
| 90 | `CentralBankPaceCheck.CURVE_DRIFT` | `3e-4` | How far the price of paper per unit of face can move between the window that prices the sale and the probe that reads it: the month's GDP and taxes reach the spread after the window (priceTheDebtMarket), and the sale ... |
| 93 | `CentralBankPaceCheck.SALE_IN_PURSES` | `3` | The face of the new paper the cases sell between the presses, in households' purses: the fixture's own size (CentralBankCheck 19), so the market takes it. |
| 96 | `CentralBankPaceCheck.SALE_FLOOR` | `20_000` | ...and the least it is, which the treasury's lineup sells. |
| 99 | `CentralBankPaceCheck.MOST_MONTHS` | `30` | The most months any loop here plays before it gives up on the condition it plays to. |
| 715 | `CentralBankPaceCheck.INDONESIA` | `.03, ARGENTINA =.074, GHANA =.072` | The share of a year's GDP each case lent its treasury (docs/queue/CBK2-notes.md, "Reality checked"): Indonesia 2020, anchored; Argentina 2020 and Ghana 2022, unanchored within a year. |
| 718 | `CentralBankPaceCheck.LONG_MONTHS` | `600` | Months the bounded case lends all of a year's GDP: fifty years. |
| 721 | `CentralBankPaceCheck.TWIN_MONTHS` | `10` | Months the twins' treasury sells new paper between the presses: ten, the months before the serial's next slice. |
| 724 | `CentralBankPaceCheck.MOST_STOP_MONTHS` | `120` | The most months the lending twin is played after it stops, waiting for its stock to fall under the anchored share. |
| 727 | `CentralBankPaceCheck.SALE_OF_GDP` | `.01` | The face the twins' treasury sells between the presses each month, a share of the year's GDP: a year of it at a share of all of it is past the unanchored share. |
| 1068 | `CentralBankPaceCheck.LINES_AT` | `13 + CentralBank.REVENUE_MONTHS + 14` | Where the bank's line count stands in its saved array: thirteen slots, the year of revenue and fourteen more before it (CentralBank.toSaveArray). |
| 1071 | `CentralBankPaceCheck.CBK2_LINES` | `2` | The lines CBK2 added to CentralBank.Line, the enum's last: BOUGHT_AT_AUCTION and PAR_AT_AUCTION. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 56 | `static PrintStream out` |  |
| 57 | `static PrintStream quiet` |  |
| 101 | `static GameFiles files` |  |
| 102 | `static int soldIn` |  |
| 104 | `static int monthsPlayed, monthsOff` |  |
| 105 | `static double worstResidual, worstRelative` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 1034 | **type** `public class CentralBankPaceCheck` | Proves the central bank's pace on its holdings and its auction share (CBK2): the pace dial both ways, the runoff's caps, sales at market with the gain or loss realised, the auction share at the auction's price and ins... |

### the helpers (lines 107-260)

| line | len | member | says |
|---:|---:|---|---|
| 109 | 4 | `static void check(String label, boolean ok)` |  |
| 115 | 3 | `static void near(String label, double actual, double expected, double tol)` | actual against expected, to tol of whichever is larger (or of 1, for figures near nothing). |
| 120 | 5 | `static void near(String label, double actual, double expected, double scale, double tol)` | actual against expected, to tol of a scale of the caller's: for a difference of two big figures. |
| 126 | 5 | `static void quietly(Runnable r)` |  |
| 133 | 12 | `static void play(Game g)` | A month played and held to the money audit: the identity of every month in every section (section 5). |
| 147 | 4 | `static void playProbed(Game g, Runnable atTheProbe)` | One month played with a reading taken after the central bank's roll and its holdings step, before the households settle (Game.settleProbeForTest). |
| 153 | 6 | `static Game copy()` | The fixture as saved, loaded fresh and set to roll as the treasury of section 19 does. |
| 161 | 6 | `static boolean nothingFallsDue(Game g)` | True when no piece of the city's term paper has principal falling due next month: the premise of every case of pure pace. |
| 169 | 1 | **type** `record Piece(Debt d, double cb, double face, double market, double settleDue, double unsettled)` | One piece of the city's term paper as a reading found it. |
| 172 | 11 | **type** `record Snap(double held, double term, double sold, double gains, double parIssue, double boughtIssue, doubl...` | The central bank's holding, the term paper and its lines of the month, and each piece, as one reading found them. |
| 174 | 4 | `Piece of(Debt d)` _(in CentralBankPaceCheck.Snap)_ |  |
| 178 | 4 | `double cbOf(Debt d)` _(in CentralBankPaceCheck.Snap)_ |  |
| 184 | 14 | `static Snap snap(Game g)` |  |
| 200 | 1 | **type** `record Month(Snap going, Snap probed)` | A month played with the holdings read going in and at the probe. |
| 202 | 6 | `static Month probedMonth(Game g)` |  |
| 210 | 7 | `static List<Debt> waiting(Game g)` | The paper the treasury sold between the presses and the market has yet to pay for. |
| 219 | 5 | `static List<Debt> sellBetweenPresses(Game g)` | The treasury sells a five-year serial between the presses, as the fixture did: the face the market bought waits to settle. |
| 226 | 1 | **type** `record Sale(double paid, double expectedPaid, double faceSold, double gains, double term)` | What a sale month was paid and gave up, read at the probe against the book before it. |
| 229 | 11 | `static Sale saleMonth(Game g)` | A month played, with each piece's face given up by the central bank priced at its market value per face at the window. |
| 242 | 8 | `static boolean paperUnderFace(Game g, double share)` | Whether the central bank's paper, at each piece's market value per face, is worth under its face by that share of it. |
| 252 | 4 | `static double autoStep(CentralBank cb, double term)` | The plain expression of Auto's step, in the model's own order of multiplication, so the two can be held to the bit. |
| 257 | 3 | `static boolean bits(double a, double b)` |  |

### the fixture (lines 261-332)

| line | len | member | says |
|---:|---:|---|---|
| 263 | 27 | `public static void main(String[] args) throws Exception` |  |
| 296 | 36 | `static void buildTheTown()` | The one town every case loads a copy of: CentralBankCheck 19's, a five-year serial sold to the households, the bank and (by the dial at .4) the central bank, played on until the month before its first slice falls due,... |

### 1. THE PACE BOTH WAYS (lines 333-407)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 70 | `static void thePaceBothWays()` |  |

### 2. THE RUNOFF'S CAPS HOLD (lines 408-489)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 2 | **type** `record Runoff(double par, double over, double term, double ranOff, double parAtIssue, double sold, double h...` | What a roll month's runoff came to, with what it was struck on, read against the book as the press found it. |
| 421 | 29 | `static Runoff runoffMonth(double overOfPar, double paceOfPar)` | The fixture played to the month the serial's first slice is rolled in, under runoff only, with the pace the caller sets as a share of the par that falls due and the dial at nothing (overOfPar of nothing or less) or le... |
| 451 | 38 | `static void theRunoffCapsHold()` |  |

### 3. SALES AT MARKET, GAINS AND LOSSES REALISED (lines 490-542)

| line | len | member | says |
|---:|---:|---|---|
| 494 | 48 | `static void salesAtMarket()` |  |

### 4. AUCTION PURCHASES AT THE AUCTION'S PRICE AND INSIDE THE CAP (lines 543-654)

| line | len | member | says |
|---:|---:|---|---|
| 548 | 7 | `static Game underTheLaw(double share)` | The central bank under the law, with the share it bids; the treasury has sold new paper between the presses. |
| 556 | 98 | `static void auctionPurchases()` |  |

### 5. WITH THE LAW OFF, NO NEW PAPER AT AUCTION BEYOND THE ROLLOVER (lines 655-709)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 50 | `static void withTheLawOff()` |  |

### 6. CREDIBILITY (lines 710-909)

| line | len | member | says |
|---:|---:|---|---|
| 730 | 6 | `static Expectations believing(double k)` | A bare Expectations, its credibility at k and its inflation at what Argentina's stood at when it began (restore()'s five slots). |
| 738 | 3 | `static double afterLending(double k, double x)` | One month of financed(): its credibility after, by the model's own arithmetic in its own order. |
| 743 | 3 | `static double premiumAt(double k, double x)` | ...and the premium it strikes at that credibility. |
| 747 | 161 | `static void credibility()` |  |

### 7. DEFAULTS AND SAVES (lines 910-1086)

| line | len | member | says |
|---:|---:|---|---|
| 915 | 8 | `static double[] figures(Game g)` | A month of a city as the figures the rules touch read it: the holding, the term paper, the bank's issue and lines, expectations, the currency. |
| 924 | 5 | `static boolean sameBits(double[] a, double[] b)` |  |
| 931 | 10 | `static GameFiles copiedTo(String label) throws java.io.IOException` | The slot's files copied to another scratch folder, so a case can rewrite them without touching the fixture. |
| 942 | 6 | `static Game loaded(GameFiles from)` |  |
| 949 | 117 | `static void defaultsAndSaves() throws Exception` |  |
| 1078 | 8 | `static void beforeCbk2(com.google.gson.JsonArray bank, int count)` | The bank's saved array as a build before CBK2 wrote it: CBK2's three trailing slots gone, and its two lines gone from this month's block and from last month's; the blocks led by `count` - the old count for a real old ... |

