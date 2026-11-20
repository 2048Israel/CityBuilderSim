# GoldStandardCheck.java - 1,060 lines · 33 methods · 14 constants · harnesses

`ham/citybuildersim/GoldStandardCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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
>   4. THE COVER BREAKS IT: a sale that would leave the cover under COVER_RATIO (over M0 less the sale retired, since FB14) breaks the month (BreakCause.COVER, Float, where breakLanding() says, nothing sold) and one
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
>  13. THE GOLD'S SALES RETIRE M0 (FB14; GD3's review, item 6): in a town founded on gold, what a reprice sold of gold is due back (CentralBank.getGoldRedeemDue()), the
>      next month's top retires it from the reserves (getGoldRedeemed(), the audit's two lines), most of it retired and only what the reserves could not pay the bank's
>      equity spent (GD3 spent it all), the town on Gold all 120 months, the bank keeps nothing every month; and booked by hand, a sale lowers M0 by itself, and one
>      past the reserves retires them and spends the rest.
>  14. THE START-ON-GOLD BOX (FB14; GD3-F): through the founding model, not the screen - Standard, Lean and Wealthy put the whole vault into gold and found on Gold,
>      the cover held at month 1; a custom vault deeper than 40% of the treasury keeps the rest in dollars; the box off holds no gold; both doors (newGame() and a
>      new Game's run()); a 1900 founding leaves Canada's peg for it; and Insane, with no vault, founds on dollars. The box's line reads Founding's figures
>      (dollarsUsd(), goldShareOfTreasury(), goldCoversTheLaw(); REV-FB14): Standard's whole vault covers 25% of the treasury, not the law's 40%.
>  15. THE DATES PASS IT BY (FB14; REV-FB14): a 1900 box city on Standard stays on its own Gold through August 1914 (Canada's suspension) and July 1926 (its
>      return): neither date floats it nor pegs it, it is never on Canada's peg, and the cover each month reads M0 after the last sale's retirement.
> 
> Money is in thousands, as everywhere; gold is in thousands of ounces, as the vault keeps it.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (128), [DebtManager](DebtManager.md) (46), [Game](Game.md) (44), [Founding](Founding.md) (30), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (28), [GoldPrice](GoldPrice.md) (28), [PolicyDates](PolicyDates.md) (5), [Nations](Nations.md) (4), [DecisionLog](DecisionLog.md) (3), [Notice](Notice.md) (3), [CentralBank](CentralBank.md) (3), [WorldEconomy](WorldEconomy.md) (3), [PegAnchorCheck](PegAnchorCheck.md) (2), [WorldMoneys](WorldMoneys.md) (2), [GameFiles](GameFiles.md) (2), [MoneyAudit](MoneyAudit.md) (2)

**Used by (2):** [ExchangeRateCheck](ExchangeRateCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 121 | THE FIXTURES |
| 161 | 1. ENTERING GOLD KEEPS THE D$'S VALUE |
| 247 | 2. THE GOLD POINTS HOLD THE RATE; GOLD IS PAID FIRST AND BOUGHT |
| 338 | 3. THE CENTRE MOVES WITH GOLD; FOLLOWING HOLDS IT |
| 405 | 4. THE COVER BREAKS IT |
| 477 | 5. DOUBT IS SCALED BY THE COVER |
| 541 | 6. DEFEND THE GOLD SETS THE DIAL |
| 611 | THE TOWN (7-10): one town played to March 1933, saved once, loaded for each |
| 837 | 11 and 12 |
| 879 | 13-14. FB14: THE GOLD'S OWN DEFENCE, AND THE START-ON-GOLD BOX |
| 1029 | 15 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `GoldStandardCheck.out` | `System.out` |  |
| 59 | `GoldStandardCheck.LEAN` | `ForeignAccounts.Regime.LEAN, FLOAT = ForeignAccounts.Regime.FLOAT, BAND = For...` |  |
| 61 | `GoldStandardCheck.LEFT_1933` | `ForeignAccounts.GoldEvent.LEFT_1933, PAR_1934 = ForeignAccounts.GoldEvent.PAR...` |  |
| 65 | `GoldStandardCheck.GOLD` | `ForeignAccounts.GOLD` | Gold's index in the vault, and the trade a quiet currency settles on, and a vault no fixture of these can empty. |
| 66 | `GoldStandardCheck.TRADE` | `CurrencyRegimeCheck.TRADE, DEEP = CurrencyRegimeCheck.DEEP` |  |
| 69 | `GoldStandardCheck.PRICE` | `GoldPrice.PAR_1900` | Gold's price while the statute fixes it at the first par, US$ an ounce: the price the bare currencies are handed unless a section moves it. |
| 343 | `GoldStandardCheck.FIRST` | `GoldPrice.FLOAT_START - 1, LAST = GoldPrice.FLOAT_START + GoldPrice.FLOAT_STEPS` | The months of the US$'s leaving gold: March 1933, the last month at the first par, to February 1934, the first at the second. |
| 482 | `GoldStandardCheck.SEEDS` | `100, MONTHS = 12` | How many seeded currencies and months the doubt is counted over. |
| 485 | `GoldStandardCheck.LEGAL` | `ForeignAccounts.COVER_RATIO * 1.001` | The legal cover a sale does not break: just over COVER_RATIO, by more than a month's sale of this deep a vault takes off it. |
| 616 | `GoldStandardCheck.SLOT` | `2` | The slot the shared town is saved to, in the harness's own scratch folder (GameFiles.scratch): never one of the nine. |
| 619 | `GoldStandardCheck.GOLD_LOCAL` | `40_000` | The founders' money put into gold by name, D$ thousands: with less the cover (gold at the mint price over M0) falls under the floor on the way to 1933 and the city is off gold before the dates. |
| 622 | `GoldStandardCheck.MIDWAY` | `(ForeignAccounts.COVER_RATIO + DebtManager.GOLD_DEFENCE_FROM) / 2` | The cover section 9 sets the town's gold to, between the floor and the defence's own: where the autopilot's spread is neither nothing nor the whole. |
| 894 | `GoldStandardCheck.REDEEM_MONTHS` | `120` | The months section 13 plays its town on Gold, looking for the gold's sales. |
| 959 | `GoldStandardCheck.DEEP_VAULT_USD` | `Game.FOUNDING_CASH` | A custom founding whose vault is the treasury's worth: deeper than the law's 40%, so the box keeps the rest in dollars. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static int fails` |  |
| 624 | `static Game shared` |  |
| 625 | `static double entryRate, coverMarch1933, centreMarch1933` |  |
| 643 | `Game g` |  |
| 644 | `ForeignAccounts fx` |  |
| 645 | `boolean leftOpen, noticeStood, noticeResolved, parOpen, parNoticeStood, wordedAnswer, centreHeld` |  |
| 646 | `int raised, resolved, answeredIn, onGoldMonths` |  |
| 647 | `double centreFeb, rateFeb, worstOff` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 1007 | **type** `public class GoldStandardCheck` | The gold standard (batch GD3; spec-gold-money 4.3): the D$ on Gold, a mint price that keeps its value when it is entered, the rate held inside the gold points of the centre that moves with gold, the sale paid in gold ... |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 77 | 5 | `static void same(String label, double actual, double expected)` | Equal to the bit (and so a -0.0 is not a 0.0). |
| 84 | 5 | `static void close(String label, double actual, double expected, double tol)` | Equal within a share of the expected. |
| 91 | 1 | `static boolean within(double actual, double expected, double tol)` | Whether a figure is within a share of another: the loops' accumulator. |
| 94 | 1 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 96 | 24 | `public static void main(String[] args) throws Exception` |  |

### THE FIXTURES (lines 121-160)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 6 | `static double[] rates(double goldUsd)` | The eight's rates (VaultMoneysCheck's cross rates) with gold at this price an ounce as the ninth. |
| 137 | 9 | `static ForeignAccounts onGold(double exports, double imports, double usdK, double goldK, long seed, double cover)` | A settled currency on this trade whose vault holds usdK in dollars and goldK in gold (bought by name at PAR_1900, then forgotten as a set-up and not a deal, as the vault of every fixture is), put on Gold at the rate o... |
| 148 | 4 | `static void month(ForeignAccounts fx, double goldUsd, double exports, double imports)` | One month of this trade with gold at this price handed first. |
| 154 | 6 | `static ForeignAccounts bandOfThePoints()` | A band of the gold points' width centred on the rate of the day, on a deep vault of dollars: the twin whose rule Gold's hold is. |

### 1. ENTERING GOLD KEEPS THE D$'S VALUE (lines 161-246)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 81 | `static void enteringGoldKeepsTheValue()` |  |

### 2. THE GOLD POINTS HOLD THE RATE; GOLD IS PAID FIRST AND BOUGHT (lines 247-337)

| line | len | member | says |
|---:|---:|---|---|
| 251 | 86 | `static void theGoldPointsHoldTheRate()` |  |

### 3. THE CENTRE MOVES WITH GOLD; FOLLOWING HOLDS IT (lines 338-404)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 59 | `static void theCentreMovesWithGold()` |  |

### 4. THE COVER BREAKS IT (lines 405-476)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 67 | `static void theCoverBreaksIt()` |  |

### 5. DOUBT IS SCALED BY THE COVER (lines 477-540)

| line | len | member | says |
|---:|---:|---|---|
| 488 | 23 | `static double[] doubtRun(double cover)` | The doubt over SEEDS seeded gold currencies held at a gap of about a quarter above the centre, each kept at this cover: breaks by doubt, by cover, the expected and its sd, the months' hazard range. |
| 512 | 28 | `static void doubtIsScaledByTheCover()` |  |

### 6. DEFEND THE GOLD SETS THE DIAL (lines 541-610)

| line | len | member | says |
|---:|---:|---|---|
| 545 | 65 | `static void defendTheGoldSetsTheDial()` |  |

### THE TOWN (7-10): one town played to March 1933, saved once, loaded for each (lines 611-836)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 5 | `static Game reload(Game from, int slot)` |  |
| 633 | 1 | `static int world(Game g)` |  |
| 635 | 5 | `static boolean said(Game g, int from, String text)` |  |
| 642 | 7 | **type** `static final class Run` | One answer's run from March 1933 to February 1934 (or the month the city came off gold). |
| 650 | 40 | `static Run through(Game from, ForeignAccounts.GoldChoice answer, String said)` |  |
| 691 | 55 | `static void aTownOnGoldThroughNineteenThirtyThreeAndFour()` |  |
| 747 | 48 | `static void theDialInTheTown()` |  |
| 796 | 34 | `static void savedMidGold()` |  |
| 831 | 1 | `static double[] figures(Game g)` |  |
| 833 | 1 | `static boolean sameBits(double[] a, double[] b)` |  |
| 835 | 1 | `static void play(Game g, int months)` |  |

### 11 and 12 (lines 837-878)

| line | len | member | says |
|---:|---:|---|---|
| 841 | 25 | `static void gdTwosTwoFindings()` |  |
| 867 | 11 | `static void leanIsTheDefault()` |  |

### 13-14. FB14: THE GOLD'S OWN DEFENCE, AND THE START-ON-GOLD BOX (lines 879-1028)

| line | len | member | says |
|---:|---:|---|---|
| 884 | 8 | `static Game founded(String label, Founding f)` | A city founded through newGame(), the founding screen's door (UserInterface.foundCity()), in the harness's own scratch folder. |
| 896 | 61 | `static void theGoldsSalesRetireMZero()` |  |
| 961 | 67 | `static void theStartOnGoldBox()` |  |

### 15 (lines 1029-1060)

| line | len | member | says |
|---:|---:|---|---|
| 1033 | 27 | `static void theDatesPassItBy()` |  |

