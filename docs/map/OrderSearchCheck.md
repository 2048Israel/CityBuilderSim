# OrderSearchCheck.java - 497 lines · 19 methods · 6 constants · harnesses

`ham/citybuildersim/OrderSearchCheck.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> The three order searches against the countdowns they replaced (0.7.54).
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
> 
> THE COUNTDOWNS ARE KEPT HERE, as they stood in 0.7.53 (sizedByCount(),
> decidedByCount(), investedByCount()), and asked the same question at the
> same moment through BusinessInvestment.OrderWatch: every order the game
> decides, before anything is built.
> 
> What this has to prove:
>   1. over a long run of the playtest's city, and a stretch of it at a dear
>      policy rate, every order is the countdown's: the same size, the same
>      trim, and for a refusal the same rate at the whole order, the same
>      "at prime" and the same facts for one building; and no order asks the
>      bond desk more than Game.deskCallsMost() times;
>   2. the same in that city a thousand times over, copied at COPY_MONTH:
>      its orders run past a hundred thousand, and trims deeper than the
>      counted slices are found by the search;
>   3. the trim's search on its own (Game.largestSlice()): the countdown's
>      answer at every boundary of a test that holds on a run from one, at
>      every order to 300 and at the int limit, within deskCallsMost() asks;
>      and on a test that does not, the countdown's answer whenever the
>      countdown would have stopped inside the counted slices, and otherwise
>      a slice that passes with the next one failing;
>   4. Mortgage.decide() against its count on a grid of tills, incomes,
>      rates and costs, the yard running out part-way included.

**Uses:** [LongPlaytest](LongPlaytest.md) (32), [Game](Game.md) (26), [Mortgage](Mortgage.md) (20), [BusinessInvestment](BusinessInvestment.md) (6), [GameFiles](GameFiles.md) (4), [ScaleCheck](ScaleCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2)

**Used by (1):** [ScaleCheck](ScaleCheck.md)

## Sections

| line | section |
|---:|---|
| 108 | · 1. a long run, every order watched |
| 139 | · 2. the same city, a thousand times over |
| 222 | 3. the trim's search on its own |
| 289 | 4. Mortgage.decide() on a grid |
| 341 | THE WATCH: every order the game decides, asked of the countdown too |
| 423 | THE COUNTDOWNS, AS THEY STOOD IN 0.7.53 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `OrderSearchCheck.LONG_MONTHS` | `1200` | Months the playtest's city plays with every order watched... |
| 57 | `OrderSearchCheck.DEAR_MONTHS` | `120` | ...then this many more with the policy rate held at DEAR_RATE, so the mortgage lender trims and refuses. |
| 59 | `OrderSearchCheck.DEAR_RATE` | `.15` | ...and the policy rate those months are held at. |
| 67 | `OrderSearchCheck.COPY_MONTH` | `400` | The copy of that city: the month it is copied at, how many times over, and how many months it plays. |
| 69 | `OrderSearchCheck.COPY_TIMES` | `1000` | ...how many times over it is copied... |
| 71 | `OrderSearchCheck.COPY_MONTHS` | `24` | ...and the months the copy plays. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |
| 50 | `static PrintStream out` |  |
| 51 | `static PrintStream quiet` |  |
| 346 | `final Game g` |  |
| 347 | `int sized, sizedDiffer, largestNeeded, mostWaits` |  |
| 348 | `int invested, investedDiffer, trimmed, refused, searched, deepest, largestInvested` |  |
| 349 | `int mostDeskCalls, mostCountDeskCalls, overBound` |  |
| 350 | `int mortgaged, mortgagedDiffer, mortgagedTrimmed, mortgagedRefused` |  |
| 351 | `String firstDiffer` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 451 | **type** `public class OrderSearchCheck` | The three order searches against the countdowns they replaced (0.7.54). |
| 73 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 5 | `static void same(String label, long actual, long expected)` |  |
| 84 | 5 | `static void quietly(Runnable r)` |  |
| 90 | 15 | `public static void main(String[] args) throws Exception` |  |
| 106 | 52 | `static void run(Path root) throws Exception` |  |
| 164 | 30 | `static void playtestCity(Game g, int months, PrintStream quiet)` | The default playtest's city (LongPlaytest.main's seed 0): its founding by hand, then its rhythm to `months` (playtestRhythm()), all of it told to `quiet`. |
| 196 | 25 | `static void playtestRhythm(Game g, int months, PrintStream quiet)` | ...and the playtest's rhythm to `months`: a skip of its lengths, the schools, the advisor's moves and two months. |

### 3. the trim's search on its own (lines 222-288)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 64 | `static void search()` |  |

### 4. Mortgage.decide() on a grid (lines 289-340)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 49 | `static void grid()` |  |

### THE WATCH: every order the game decides, asked of the countdown too (lines 341-422)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 77 | **type** `static final class Watch implements BusinessInvestment.OrderWatch` |  |
| 353 | 1 | `Watch(Game g)` _(in OrderSearchCheck.Watch)_ |  |
| 356 | 10 | `public void sized(BuildingsTemplate t, int needed, double siteOutput, int deliverable, int waitsRead)` _(in OrderSearchCheck.Watch)_ |  |
| 368 | 21 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in OrderSearchCheck.Watch)_ |  |
| 391 | 11 | `public void mortgaged(int asked, IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRate, Mortgage.Decisi...` _(in OrderSearchCheck.Watch)_ |  |
| 403 | 1 | `void note(String s)` _(in OrderSearchCheck.Watch)_ |  |
| 405 | 9 | `void report(String what)` _(in OrderSearchCheck.Watch)_ |  |
| 415 | 6 | `void verdicts(String what)` _(in OrderSearchCheck.Watch)_ |  |

### THE COUNTDOWNS, AS THEY STOOD IN 0.7.53 (lines 423-497)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 12 | `static int sizedByCount(BusinessInvestment plans, BuildingsTemplate template, int needed, double siteOutput)` | BusinessInvestment.orderSize()'s count: up from one until the wait passes MAX_ORDER_MONTHS. |
| 442 | 22 | `static Mortgage.Decision decidedByCount(int asked, IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRate)` | Mortgage.decide()'s count: down from what was asked. |
| 466 | 31 | `static Game.Afford investedByCount(Game g, BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | Game.consider()'s countdown: down from the whole order, the bond desk asked for every slice that borrows. |

