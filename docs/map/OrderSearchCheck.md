# OrderSearchCheck.java - 924 lines · 23 methods · 16 constants · harnesses

`ham/citybuildersim/OrderSearchCheck.java` - generated 2026-11-04 by CodeMap; line numbers are as of that run.

> The order searches against the countdowns they replaced (0.7.54; the order's size is a formula since BA1).
> 
> WHY. Three loops sized an order one building at a time: the investment
> desk's trim (Game.consider()), the landlords' mortgage (Mortgage.decide())
> and the order's size (BusinessInvestment.orderSize()). An order grows with
> the city, so the month did too: 4.1 s on average at 1.1 billion people and
> 14.8 s at worst (the project's spec-scale.md, section 4). Each is a search
> now. The size and the mortgage halve, because their tests are proved to
> hold on a run from one (BusinessInvestment, THE WAIT GROWS WITH THE ORDER;
> Mortgage, THE LANDLORD'S ORDER IS A RUN FROM ONE). The trim's test is not,
> so it asks the countdown's own first Game.COUNTDOWN_SLICES slices, then
> doubles down and halves (Game, THE LARGEST SLICE, WITHOUT COUNTING TO IT).
> Since BA1 the size is not a search at all: orderSize() is the gap and the
> plots, with no wait to read (section 6), and the other two tests are asked
> on the cost grown over the order's wait (THE INTEREST ON THE WAIT), a
> carry that rises with the order, so they are still runs from one.
> 
> THE COUNTDOWNS ARE KEPT HERE, as they stood in 0.7.53 (decidedByCount(),
> investedByCount(); sizedByCount() is the count of BA1's formula), and asked
> the same question at the same moment through
> BusinessInvestment.OrderWatch: every order the game decides, before
> anything is built. The trim's question is the game's: on its whole cost
> since 0.7.113 (investedByCount()), grown over the wait since BA1, by the
> carry the game reads (BusinessInvestment.carryOf()); the lender's, on the
> shortfall grown by it (decidedByCount()).
> 
> What this has to prove:
>   1. over a long run of the playtest's city, and a stretch of it at a dear
>      policy rate, every order is the countdown's: the same trim, and for a
>      refusal the same rate at the whole order, the same
>      "at prime" and the same facts for one building; and no order asks the
>      bond desk more than Game.deskCallsMost() times;
>   2. the same in that city a thousand times over, copied at COPY_MONTH:
>      its orders run past a hundred thousand, and decisions below the
>      counted slices are found by the search - and in a twin of it asked the
>      rule to 0.7.112 (0.7.113: on its whole cost an order is all but never
>      trimmed), trims deeper than the counted slices;
>   3. the trim's search on its own (Game.largestSlice()): the countdown's
>      answer at every boundary of a test that holds on a run from one, at
>      every order to 300 and at the int limit, within deskCallsMost() asks;
>      and on a test that does not, the countdown's answer whenever the
>      countdown would have stopped inside the counted slices, and otherwise
>      a slice that passes with the next one failing;
>   4. Mortgage.decide() against its count on a grid of tills, incomes,
>      rates and costs, the yard running out part-way included, and (BA1)
>      under three carries: none, and two that rise with the order;
>   5. a window a bond opens (OS1, EP1's gate): an order whose till pays for
>      a few slices that fail at the sector's own rate, whose first slices
>      past the till pass on a bond's cheaper one and whose later ones are
>      past the bank's room and fail, is trimmed to the window's last slice,
>      the countdown's, in the playtest's city with its room set to cause it
>      and on tests of its own;
>   6. BusinessInvestment.orderSize() (BA1): the gap and the plots - the
>      fewest buildings whose capacity covers the shortfall, at least one and
>      never more plots than the city can sell - against the count up from
>      one, on a grid of shortfalls, capacities and templates, and the int
>      limit saturating where a wrap would have made a negative order.

**Uses:** [Game](Game.md) (45), [LongPlaytest](LongPlaytest.md) (33), [Mortgage](Mortgage.md) (20), [BusinessInvestment](BusinessInvestment.md) (16), [BuildingsTemplate](BuildingsTemplate.md) (12), [GameFiles](GameFiles.md) (8), [SpreadPlanner](SpreadPlanner.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (3), [ScaleCheck](ScaleCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [WantedPosts](WantedPosts.md) (1), [TempDirs](TempDirs.md) (1)

**Used by (3):** [InvestCheck](InvestCheck.md), [ScaleCheck](ScaleCheck.md), [ShortSellingCheck](ShortSellingCheck.md)

## Sections

| line | section |
|---:|---|
| 194 | · 1. a long run, every order watched |
| 232 | · 2. the same city, a thousand times over |
| 403 | 3. the trim's search on its own |
| 470 | 4. Mortgage.decide() on a grid |
| 534 | 5. a window a bond opens, caused (OS1) |
| 663 | 6. the order's size is the gap and the plots (BA1) |
| 725 | THE WATCH: every order the game decides, asked of the countdown too |
| 839 | THE COUNTDOWNS, AS THEY STOOD IN 0.7.53 (and the size's, as BA1's formula counts it) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `OrderSearchCheck.LONG_MONTHS` | `1200` | Months the playtest's city plays with every order watched... |
| 79 | `OrderSearchCheck.DEAR_MONTHS` | `120` | ...then this many more with the policy rate held at DEAR_RATE, so the mortgage lender trims and refuses. |
| 81 | `OrderSearchCheck.DEAR_RATE` | `.15` | ...and the policy rate those months are held at. |
| 123 | `OrderSearchCheck.COPY_MONTH` | `500` | The copy of that city: the month it is copied at, how many times over, and how many months it plays. |
| 125 | `OrderSearchCheck.COPY_TIMES` | `1000` | ...how many times over it is copied... |
| 127 | `OrderSearchCheck.COPY_MONTHS` | `24` | ...and the months the copy plays. |
| 136 | `OrderSearchCheck.COPY_RATE` | `DEAR_RATE` | The policy rate the copy's months are held at (FR8, 2026-10-28): the long run's dear stretch's, DEAR_RATE. |
| 152 | `OrderSearchCheck.TWIN_RATE` | `.40` | The policy rate the twin's months are held at (FW9, 2026-10-25). |
| 300 | `OrderSearchCheck.TWIN_TILLS` | `{.25,.5 }` | The shares of its whole cost the twin's till is put at under its largest order (tillsAsTheLine()). |
| 309 | `OrderSearchCheck.TWIN_LEAST_ORDER` | `4 * Game.COUNTDOWN_SLICES` | ...AT LEAST THIS MANY OF ITS KIND (FC1, 2026-11-02). |
| 537 | `OrderSearchCheck.WINDOW_ORDER` | `94` | The order the window is caused in: Retail's Convenience Stores, more than the counted slices and past the doubling's steps (94, 78, 76, 72, 64, 48, 16, 1) of the window. |
| 539 | `OrderSearchCheck.WINDOW_TILLS` | `{ 2, 4 }` | The slices the till pays for, which fail at the sector's own rate: two and four, so the doubling's last step (slice 1) is not in the window... |
| 541 | `OrderSearchCheck.WINDOW_ROOMS` | `{ 3, 6, 9 }` | ...and how many slices past the till the bank's room reaches, which a bond raises whole and may pass: a window of that many at most. |
| 543 | `OrderSearchCheck.WINDOW_PROFIT_FROM` | `.005` | The profit of a building the sweep starts at, as a share of a twelfth of its cost (a building that earns its cost a year at 100%)... |
| 545 | `OrderSearchCheck.WINDOW_PROFIT_TO` | `1` | ...ends at... |
| 547 | `OrderSearchCheck.WINDOW_PROFIT_STEP` | `1.04` | ...and steps by. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `static int fails` |  |
| 72 | `static PrintStream out` |  |
| 73 | `static PrintStream quiet` |  |
| 730 | `final Game g` |  |
| 731 | `int invested, investedDiffer, trimmed, refused, searched, deepest, largestInvested` |  |
| 732 | `int mostDeskCalls, mostCountDeskCalls, overBound` |  |
| 733 | `int mortgaged, mortgagedDiffer, mortgagedTrimmed, mortgagedRefused, largestMortgage` |  |
| 735 | `int carried, mortgageCarried` | BA1: orders whose whole size carried an interest over its cost's own (the carry over 1), judged on their interest and on a mortgage; the most the carry grew a cost by. |
| 736 | `double mostCarry` |  |
| 738 | `int mortgageUnnamed` | BA1: landlords' orders whose building the hook's costs did not name to one template. |
| 739 | `String firstDiffer` |  |
| 741 | `BusinessInvestment.Decision biggest` | The largest order judged on its interest so far (FR8, tillsAsTheLine()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 69 | 856 | **type** `public class OrderSearchCheck` | The order searches against the countdowns they replaced (0.7.54; the order's size is a formula since BA1). |
| 154 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 159 | 5 | `static void same(String label, long actual, long expected)` |  |
| 165 | 5 | `static void quietly(Runnable r)` |  |
| 171 | 20 | `public static void main(String[] args) throws Exception` |  |
| 192 | 106 | `static void run(Path root) throws Exception` |  |
| 321 | 18 | `static void tillsAsTheLine(Game twin, Watch w)` | THE TWIN'S DEEP TRIMS, CAUSED (FR8, 2026-10-28). |
| 345 | 30 | `static void playtestCity(Game g, int months, PrintStream quiet)` | The default playtest's city (LongPlaytest.main's seed 0): its founding by hand, then its rhythm to `months` (playtestRhythm()), all of it told to `quiet`. |
| 377 | 25 | `static void playtestRhythm(Game g, int months, PrintStream quiet)` | ...and the playtest's rhythm to `months`: a skip of its lengths, the schools, the advisor's moves and two months. |

### 3. the trim's search on its own (lines 403-469)

| line | len | member | says |
|---:|---:|---|---|
| 405 | 64 | `static void search()` |  |

### 4. Mortgage.decide() on a grid (lines 470-533)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 61 | `static void grid()` |  |

### 5. a window a bond opens, caused (OS1) (lines 534-662)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 10 | `static ham.citybuildersim.sectors.SpreadPlanner.MoneyGate windowGate(double room)` | THE WINDOW'S GATE (OS1). |
| 581 | 81 | `static void window(Path root) throws Exception` | THE WINDOW, CAUSED (OS1, 2026-11-01). |

### 6. the order's size is the gap and the plots (BA1) (lines 663-724)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 51 | `static void orderSizes(Game g)` | ORDER SIZE, A FORMULA (BA1). |

### THE WATCH: every order the game decides, asked of the countdown too (lines 725-838)

| line | len | member | says |
|---:|---:|---|---|
| 729 | 109 | **type** `static final class Watch implements BusinessInvestment.OrderWatch` |  |
| 743 | 1 | `Watch(Game g)` _(in OrderSearchCheck.Watch)_ |  |
| 746 | 27 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in OrderSearchCheck.Watch)_ |  |
| 775 | 20 | `public void mortgaged(int asked, IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRate, Mortgage.Decisi...` _(in OrderSearchCheck.Watch)_ |  |
| 802 | 15 | `IntToDoubleFunction carryOfTheOrder(int asked, IntToDoubleFunction costOf)` _(in OrderSearchCheck.Watch)_ | The carry the game grew the landlord's shortfall by (Game.lenderReads(): BusinessInvestment.carryOf() of the building at the insured rate). |
| 818 | 1 | `void note(String s)` _(in OrderSearchCheck.Watch)_ |  |
| 820 | 11 | `void report(String what)` _(in OrderSearchCheck.Watch)_ |  |
| 832 | 5 | `void verdicts(String what)` _(in OrderSearchCheck.Watch)_ |  |

### THE COUNTDOWNS, AS THEY STOOD IN 0.7.53 (and the size's, as BA1's formula counts it) (lines 839-924)

| line | len | member | says |
|---:|---:|---|---|
| 848 | 7 | `static int sizedByCount(BusinessInvestment plans, BuildingsTemplate template, double shortfall, double capacityPerUnit)` | BusinessInvestment.orderSize()'s count (BA1): up from one, the first number of buildings whose capacity covers the shortfall, never more than the plots, and one for a building with no capacity to count. |
| 857 | 23 | `static Mortgage.Decision decidedByCount(int asked, IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRat...` | Mortgage.decide()'s count: down from what was asked, the lender's test on the shortfall grown by the order's carry (BA1; n -> 1 for none). |
| 889 | 35 | `static Game.Afford investedByCount(Game g, BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | Game.consider()'s countdown: down from the whole order, the bond desk asked for every slice that borrows - and since 0.7.113 each slice asked the test on what the game's rule tests (Game.orderGate()): its whole cost, ... |

