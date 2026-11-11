# GoldStandardCheck.java - 857 lines · 29 methods · 12 constants · harnesses

`ham/citybuildersim/GoldStandardCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The gold standard (batch GD3; spec-gold-money 4.3): the D$ on Gold, a mint price that keeps its value when it is entered, the rate held inside the gold points of the
> centre that moves with gold, the sale paid in gold first and the purchase made into gold, the cover that breaks it, doubt that grows as the cover thins, the central bank
> defending the gold, the US$'s three dates that open a decision, the notice that stands while one is open, and the save that carries the mint.
> 
> WHY. Gold is the only regime whose centre is not a number the player typed: it is the mint price over a price the world moves every month, so the rate can be held
> where it was at a centre that is falling, and the vault that pays for it is the one thing the law counts. A mint struck a digit off would change the D$'s value on the
> day it is entered; a centre that did not move with gold would hold the rate to a price that is gone; a sale paid out of the dollars would leave the gold the law counts
> where it was; a cover floor read against the wrong side, or one that also stopped a purchase, would break a city for filling its vault; a doubt that did not thin with
> the cover would break a fully covered city or never break a bare one; and an answer to a date that did not reach the dial, the notice, the log and the save would be a
> button that did nothing. Each section says against which constant or getter it is judged, and a fixture CAUSES the condition it asserts: the trade that weakens or
> strengthens the shadow, the gold bought into the vault and forgotten as a set-up, the money base that puts the cover on the right side of the floor, and a town played
> to the month the dollar left gold.
> 
> What this has to prove:
>   1. ENTERING GOLD KEEPS THE D$'S VALUE: the mint price is the day's rate times gold's US$ price (D$20.6718 an ounce at a rate of 1.00), the mint rate and the centre
>      are the day's rate; refused where gold has no price; setMintPrice() moves the centre to the typed price over gold's; leaving Gold keeps the gold in the vault.
>   2. THE GOLD POINTS HOLD THE RATE; GOLD IS PAID FIRST AND BOUGHT: past the weak edge the rate is goldWeakEdge() and the sale comes out of the ounces, the US$ untouched;
>      past the strong edge it is goldStrongEdge() and the purchase goes into the ounces; inside the points nothing is dealt; a band of GOLD_POINTS deals the same dollars.
>   3. THE CENTRE MOVES WITH GOLD, FOLLOWING HOLDS IT: the centre each month is the mint price over that month's price, by February 1934 the March 1933 centre times
>      PAR_1900 / PAR_1934; following keeps it at the mint rate; staying leaves the mint price where it was.
>   4. THE COVER BREAKS IT: a sale that would leave the cover under COVER_RATIO breaks the month (BreakCause.COVER, Float, where breakLanding() says, nothing sold) and one
>      that leaves it just over holds; a purchase under the floor holds; the vault's VAULT_FLOOR does not break Gold.
>   5. DOUBT IS SCALED BY THE COVER: goldHazard() is hazard() at the legal cover, none at a full cover, half way between at the midpoint; counted over seeded twins.
>   6. DEFEND THE GOLD SETS THE DIAL (a bare DebtManager): goldSpread(), goldRate() under each aim and the autopilot off, dropGoldAim() writing nothing, tiedRate().
>   7. A TOWN ON GOLD THROUGH 1933 AND 1934 (the full file only): one town with gold in its vault played to March 1933, saved, loaded three times to answer the US$'s
>      April 1933 date (stay, follow, leave).
>   8. THE NOTICE: raised the month the date opens, standing while it is open, settled the month after it is answered; none in a city that is not on gold.
>   9. THE DIAL IN THE TOWN: goldRate() as Game struck it, the inflation rule under that aim, and a break that drops the aim without a decision line.
>  10. SAVED MID-GOLD: an event open and following the dollar, slots 152-155, the regime and the aim as they were, and the next twelve months to the bit.
>  11. GD2'S TWO FINDINGS: a price that is NaN is no throw in Game.priceOrNaN(); a money that is gold's code is none of the eight's, and gold gets no share of the trade.
>  12. LEAN IS THE DEFAULT: a new town has no mint, no event, no gold, and deals none.
> 
> Money is in thousands, as everywhere; gold is in thousands of ounces, as the vault keeps it.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (107), [DebtManager](DebtManager.md) (44), [GoldPrice](GoldPrice.md) (28), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (27), [Game](Game.md) (25), [Nations](Nations.md) (4), [DecisionLog](DecisionLog.md) (3), [Notice](Notice.md) (3), [PegAnchorCheck](PegAnchorCheck.md) (2), [WorldMoneys](WorldMoneys.md) (2), [Founding](Founding.md) (2)

**Used by (1):** [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 108 | THE FIXTURES |
| 148 | 1. ENTERING GOLD KEEPS THE D$'S VALUE |
| 234 | 2. THE GOLD POINTS HOLD THE RATE; GOLD IS PAID FIRST AND BOUGHT |
| 325 | 3. THE CENTRE MOVES WITH GOLD; FOLLOWING HOLDS IT |
| 392 | 4. THE COVER BREAKS IT |
| 461 | 5. DOUBT IS SCALED BY THE COVER |
| 525 | 6. DEFEND THE GOLD SETS THE DIAL |
| 595 | THE TOWN (7-10): one town played to March 1933, saved once, loaded for each |
| 816 | 11 and 12 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `GoldStandardCheck.out` | `System.out` |  |
| 49 | `GoldStandardCheck.LEAN` | `ForeignAccounts.Regime.LEAN, FLOAT = ForeignAccounts.Regime.FLOAT, BAND = For...` |  |
| 51 | `GoldStandardCheck.LEFT_1933` | `ForeignAccounts.GoldEvent.LEFT_1933, PAR_1934 = ForeignAccounts.GoldEvent.PAR...` |  |
| 55 | `GoldStandardCheck.GOLD` | `ForeignAccounts.GOLD` | Gold's index in the vault, and the trade a quiet currency settles on, and a vault no fixture of these can empty. |
| 56 | `GoldStandardCheck.TRADE` | `CurrencyRegimeCheck.TRADE, DEEP = CurrencyRegimeCheck.DEEP` |  |
| 59 | `GoldStandardCheck.PRICE` | `GoldPrice.PAR_1900` | Gold's price while the statute fixes it at the first par, US$ an ounce: the price the bare currencies are handed unless a section moves it. |
| 330 | `GoldStandardCheck.FIRST` | `GoldPrice.FLOAT_START - 1, LAST = GoldPrice.FLOAT_START + GoldPrice.FLOAT_STEPS` | The months of the US$'s leaving gold: March 1933, the last month at the first par, to February 1934, the first at the second. |
| 466 | `GoldStandardCheck.SEEDS` | `100, MONTHS = 12` | How many seeded currencies and months the doubt is counted over. |
| 469 | `GoldStandardCheck.LEGAL` | `ForeignAccounts.COVER_RATIO * 1.001` | The legal cover a sale does not break: just over COVER_RATIO, by more than a month's sale of this deep a vault takes off it. |
| 600 | `GoldStandardCheck.SLOT` | `2` | The slot the shared town is saved to, in the harness's own scratch folder (GameFiles.scratch): never one of the nine. |
| 603 | `GoldStandardCheck.GOLD_LOCAL` | `40_000` | The founders' money put into gold by name, D$ thousands: with less the cover (gold at the mint price over M0) falls under the floor on the way to 1933 and the city is off gold before the dates. |
| 606 | `GoldStandardCheck.MIDWAY` | `(ForeignAccounts.COVER_RATIO + DebtManager.GOLD_DEFENCE_FROM) / 2` | The cover section 9 sets the town's gold to, between the floor and the defence's own: where the autopilot's spread is neither nothing nor the whole. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |
| 608 | `static Game shared` |  |
| 609 | `static double entryRate, coverMarch1933, centreMarch1933` |  |
| 627 | `Game g` |  |
| 628 | `ForeignAccounts fx` |  |
| 629 | `boolean leftOpen, noticeStood, noticeResolved, parOpen, parNoticeStood, wordedAnswer, centreHeld` |  |
| 630 | `int raised, resolved, answeredIn, onGoldMonths` |  |
| 631 | `double centreFeb, rateFeb, worstOff` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 814 | **type** `public class GoldStandardCheck` | The gold standard (batch GD3; spec-gold-money 4.3): the D$ on Gold, a mint price that keeps its value when it is entered, the rate held inside the gold points of the centre that moves with gold, the sale paid in gold ... |
| 61 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 67 | 5 | `static void same(String label, double actual, double expected)` | Equal to the bit (and so a -0.0 is not a 0.0). |
| 74 | 5 | `static void close(String label, double actual, double expected, double tol)` | Equal within a share of the expected. |
| 81 | 1 | `static boolean within(double actual, double expected, double tol)` | Whether a figure is within a share of another: the loops' accumulator. |
| 84 | 1 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 86 | 21 | `public static void main(String[] args) throws Exception` |  |

### THE FIXTURES (lines 108-147)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 6 | `static double[] rates(double goldUsd)` | The eight's rates (VaultMoneysCheck's cross rates) with gold at this price an ounce as the ninth. |
| 124 | 9 | `static ForeignAccounts onGold(double exports, double imports, double usdK, double goldK, long seed, double cover)` | A settled currency on this trade whose vault holds usdK in dollars and goldK in gold (bought by name at PAR_1900, then forgotten as a set-up and not a deal, as the vault of every fixture is), put on Gold at the rate o... |
| 135 | 4 | `static void month(ForeignAccounts fx, double goldUsd, double exports, double imports)` | One month of this trade with gold at this price handed first. |
| 141 | 6 | `static ForeignAccounts bandOfThePoints()` | A band of the gold points' width centred on the rate of the day, on a deep vault of dollars: the twin whose rule Gold's hold is. |

### 1. ENTERING GOLD KEEPS THE D$'S VALUE (lines 148-233)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 81 | `static void enteringGoldKeepsTheValue()` |  |

### 2. THE GOLD POINTS HOLD THE RATE; GOLD IS PAID FIRST AND BOUGHT (lines 234-324)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 86 | `static void theGoldPointsHoldTheRate()` |  |

### 3. THE CENTRE MOVES WITH GOLD; FOLLOWING HOLDS IT (lines 325-391)

| line | len | member | says |
|---:|---:|---|---|
| 332 | 59 | `static void theCentreMovesWithGold()` |  |

### 4. THE COVER BREAKS IT (lines 392-460)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 64 | `static void theCoverBreaksIt()` |  |

### 5. DOUBT IS SCALED BY THE COVER (lines 461-524)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 23 | `static double[] doubtRun(double cover)` | The doubt over SEEDS seeded gold currencies held at a gap of about a quarter above the centre, each kept at this cover: breaks by doubt, by cover, the expected and its sd, the months' hazard range. |
| 496 | 28 | `static void doubtIsScaledByTheCover()` |  |

### 6. DEFEND THE GOLD SETS THE DIAL (lines 525-594)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 65 | `static void defendTheGoldSetsTheDial()` |  |

### THE TOWN (7-10): one town played to March 1933, saved once, loaded for each (lines 595-815)

| line | len | member | says |
|---:|---:|---|---|
| 611 | 5 | `static Game reload(Game from, int slot)` |  |
| 617 | 1 | `static int world(Game g)` |  |
| 619 | 5 | `static boolean said(Game g, int from, String text)` |  |
| 626 | 7 | **type** `static final class Run` | One answer's run from March 1933 to February 1934 (or the month the city came off gold). |
| 634 | 40 | `static Run through(Game from, ForeignAccounts.GoldChoice answer, String said)` |  |
| 675 | 55 | `static void aTownOnGoldThroughNineteenThirtyThreeAndFour()` |  |
| 731 | 43 | `static void theDialInTheTown()` |  |
| 775 | 34 | `static void savedMidGold()` |  |
| 810 | 1 | `static double[] figures(Game g)` |  |
| 812 | 1 | `static boolean sameBits(double[] a, double[] b)` |  |
| 814 | 1 | `static void play(Game g, int months)` |  |

### 11 and 12 (lines 816-857)

| line | len | member | says |
|---:|---:|---|---|
| 820 | 25 | `static void gdTwosTwoFindings()` |  |
| 846 | 11 | `static void leanIsTheDefault()` |  |

