# KeptTimingCheck.java - 682 lines · 20 methods · 23 constants · harnesses

`ham/citybuildersim/KeptTimingCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> What the kept pages cost: KeptCheck's timed sections, each kept month on a page against the same month built new, within its
> budget through Timing.
> 
> WHY (FS4, KT1). KeptCheck timed its pages among the sections that prove them right, so the whole harness was in
> Timing.HARNESSES and the timing step ran all of it hard (408 s of the step's 941 s on 2026-11-06) to judge a few medians, and a
> page slow under a loaded PC was a red in the harness whose verdict is that the pages are right (KEPT_ALL_CHROME_MS, KS1-rest).
> Split, KeptCheck is correctness alone and this is the clocks alone. In a soft run it does nothing at all (HA1: no rounds whose only
> verdict is time); hard, it builds KeptCheck's city (KeptCheck.playedCity()), plays it on without drawing to the month each budget
> was measured at, and costs each page there with the premise its section in KeptCheck causes, the page first drawn kept and new for
> WARM_MONTHS months untimed, as KeptCheck's correctness sections drew it before its cost was taken. The budgets, the sentences
> above them and the lines' labels are KeptCheck's as they were, and the sections keep KeptCheck's numbers.
> 
> What it has to prove, each figure through Timing with the same month built new printed beside it:
>   5. the kept People month is within KEPT_MONTH_MS;
>  10. the kept chrome's month (the header and the rail) is within KEPT_CHROME_MS;
>  13. the whole chrome kept (the header, the rail and the three panels) is within KEPT_ALL_CHROME_MS;
>  20. the kept City History month is within KEPT_HISTORY_MS;
>  24, 28, 32. the kept month on Infrastructure (with transit on it, KeptCheck.ensureTransit()), on Services and on the land office
>      is within KEPT_INFRASTRUCTURE_MS, KEPT_SERVICES_MS and KEPT_LAND_MS;
>  39, 43. on Build, the Overview and a city category with two orders dialled up and a card pinned, within KEPT_BUILD_MS, and on the
>      construction page with KeptCheck.SITE_ROWS sites held on, within KEPT_CONSTRUCTION_MS;
>  52. on the Sectors list within KEPT_SECTORS_MS and on the three business pages that weigh most within KEPT_SECTOR_MS, the plants
>      held and a works standing (KeptCheck.ensureWorks());
>  68. at MONEY_MONTH: on the heaviest page of the Bank and of Finances, on Borrow with an ask, and on the fund's Search and Activity
>      (the fund filled as KeptCheck's section 61 fills it) within KEPT_BANK_MS, KEPT_FINANCES_MS and KEPT_FUND_MS; and the fund's
>      heaviest page is one of the two costed;
>  81. at SCREENS_MONTH: on Government's Overview within KEPT_GOVERNMENT_MS, on Trade's Overview and its heaviest page within
>      KEPT_TRADE_MS, and on Policy's hub and its dial-heavy pages within KEPT_POLICY_MS;
>  90. on Household money (as it opens and with a cell's books open) within KEPT_HOUSEHOLD_MS and on the Rankings tab (as it opens
>      and with every measure unfolded) within KEPT_RANKINGS_MS.
> 
> It needs the JavaFX toolkit but no window, as KeptCheck does; where the toolkit cannot start it says so in one line and exits 0.

**Uses:** [KeptBench](KeptBench.md) (48), [KeptCheck](KeptCheck.md) (25), [Game](Game.md) (15), [Timing](Timing.md) (3), [BuildAdvice](BuildAdvice.md) (2), [Sectors](Sectors.md) (2), [GameFiles](GameFiles.md) (1), [ChartModel](ChartModel.md) (1), [Sector](Sector.md) (1), [CorporateBond](CorporateBond.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `KeptTimingCheck.MONEY_MONTH` | `2_100` | The month the money budgets were measured at (KEPT_BANK_MS's sentence: "a city played to month 2,100 as KeptCheck's is when it comes to them"). |
| 61 | `KeptTimingCheck.SCREENS_MONTH` | `3_500` | The month the Government, Trade, Policy, Household money and Rankings budgets were measured at (KEPT_GOVERNMENT_MS's sentence: "at month 3,500 of a played city"). |
| 64 | `KeptTimingCheck.PLAY_CHUNK` | `100` | The months played in one call on the toolkit's thread while the city is played on to a budget's month: well inside fx()'s ten minutes. |
| 67 | `KeptTimingCheck.KEPT_MONTH_MS` | `12` | The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. |
| 70 | `KeptTimingCheck.KEPT_CHROME_MS` | `4` | The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. |
| 73 | `KeptTimingCheck.KEPT_ALL_CHROME_MS` | `8` | The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. |
| 80 | `KeptTimingCheck.KEPT_HISTORY_MS` | `13` | The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same mon... |
| 87 | `KeptTimingCheck.KEPT_INFRASTRUCTURE_MS` | `9` | The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 betwe... |
| 94 | `KeptTimingCheck.KEPT_SERVICES_MS` | `13` | The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 betwe... |
| 101 | `KeptTimingCheck.KEPT_LAND_MS` | `5` | The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 ... |
| 109 | `KeptTimingCheck.KEPT_BUILD_MS` | `12` | The budget for the kept Build month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, on the Overview and on a city category with two orders dialled up and a card's stats pinned (the heavier of ... |
| 116 | `KeptTimingCheck.KEPT_CONSTRUCTION_MS` | `4` | The budget for the kept construction page's month - its draw, then the pulse's CSS and layout, on the Sites tab with the city's sites on - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (1.4 to 1... |
| 122 | `KeptTimingCheck.KEPT_SECTORS_MS` | `18` | The budget for the kept Sectors list month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (8.3 to 9.2 ms in five runs, 8.8 between them; the same mon... |
| 131 | `KeptTimingCheck.KEPT_SECTOR_MS` | `15` | The budget for the kept month on a business's page - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, the heaviest of the three pages costed (the shops' Operations; a plain business's Income stat... |
| 138 | `KeptTimingCheck.KEPT_BANK_MS` | `12` | The budget for the kept Bank month - its draw, then the pulse's CSS and layout - on the Bank's heaviest page (the Overview), on the PC: twice the kept month's median, rounded up (5.2 to 8.3 ms in five runs of the mone... |
| 145 | `KeptTimingCheck.KEPT_FINANCES_MS` | `18` | The budget for the kept Finances month on the heaviest of the two pages costed (Borrow at home, the page the auction draws, and the page with the most nodes), on the PC: twice the kept month's median, rounded up (the ... |
| 152 | `KeptTimingCheck.KEPT_FUND_MS` | `92` | The PC's budget for the kept month on the fund's heaviest page is twice the median in five sequential money-section runs, rounded up (FP1's Activity 41.7 to 47.1 ms, 45.8 between them, built new 93.5 to 129.7, and Sea... |
| 159 | `KeptTimingCheck.KEPT_GOVERNMENT_MS` | `7` | The budget for the kept Government month - its draw, then the pulse's CSS and layout - on the Overview, at month 3,500 of a played city (about where KeptCheck is when it comes to the Government tab), on the PC: twice ... |
| 167 | `KeptTimingCheck.KEPT_TRADE_MS` | `10` | The budget for the kept Trade month - its draw, then the pulse's CSS and layout - on the Overview and on the heaviest of its pages (the reserves), the larger of the two being the figure, at month 3,500 of a played cit... |
| 175 | `KeptTimingCheck.KEPT_POLICY_MS` | `10` | The budget for the kept Policy month - its draw, then the pulse's CSS and layout - on the hub and on the pages heavy with dials (Taxes' Profit page and the policy rate's), the largest of them being the figure, at mont... |
| 183 | `KeptTimingCheck.KEPT_HOUSEHOLD_MS` | `8` | The budget for the kept Household money month - its draw, then the pulse's CSS and layout - on the page as it opens and with the first family cell's books open (the larger of the two being the figure), at month 3,400 ... |
| 191 | `KeptTimingCheck.KEPT_RANKINGS_MS` | `7` | The budget for the kept Rankings month - its draw, then the pulse's CSS and layout - on the tab as it opens and with every measure unfolded (Browse all: a line a measure; the larger of the two being the figure), at mo... |
| 242 | `KeptTimingCheck.WARM_MONTHS` | `KeptCheck.MONTHS` | The months a page is drawn, untimed, before its rounds are timed: KeptCheck's MONTHS, the months its correctness section drew the page across, kept and then built new to compare, before the cost was taken there. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 46 | 637 | **type** `public final class KeptTimingCheck` | What the kept pages cost: KeptCheck's timed sections, each kept month on a page against the same month built new, within its budget through Timing. |
| 48 | 1 | `private KeptTimingCheck()` |  |
| 52 | 4 | `static void check(String label, boolean ok)` |  |
| 193 | 29 | `public static void main(String[] args) throws Exception` |  |
| 224 | 11 | `static void playTo(Game g, int to) throws Exception` | The city played on to `to` with no page drawn, a chunk of months to a call on the toolkit's thread, as KeptCheck's sections play it (KeptCheck.month()). |
| 245 | 3 | **type** `interface Redraw` | A redraw and its passes, which may throw as the bench's draws do. |
| 246 | 1 | `void run() throws Exception` _(in KeptTimingCheck.Redraw)_ |  |
| 250 | 7 | `private static void warm(Game g, Redraw kept, Redraw fresh) throws Exception` | WARM_MONTHS months on the toolkit's thread, nothing timed: each the page's kept redraw, its CSS and layout passed, then the page built new and the kept one put back, as a correctness section compares them. |
| 259 | 5 | `private static void timed(double figure, double budget, String label)` | One timed line: the figure against its budget through Timing, printed with its verdict, counted when it fails. |
| 266 | 31 | `static void peopleCost(Game g, KeptBench bench) throws Exception` | Section 5: what the kept People month costs, against the same month built new. |
| 299 | 34 | `static void chromeCost(Game g, KeptBench bench) throws Exception` | Section 10: what the kept chrome's month costs (the header and the rail), with the clock running at the third rung on the People tab. |
| 335 | 45 | `static void wholeChromeCost(Game g, KeptBench bench) throws Exception` | Section 13: what the whole chrome costs kept, the header, the rail and the three panels, the city panel on its dashboard with its economy section open. |
| 382 | 34 | `static void historyCost(Game g, KeptBench bench) throws Exception` | Section 20: what the kept City History month costs, against the same month built new. |
| 418 | 6 | `static void kp2Cost(Game g, KeptBench bench) throws Exception` | Sections 24, 28 and 32: the kept month on Infrastructure (with transit on it, as KeptCheck's 21 causes it), on Services and on the land office. |
| 429 | 25 | `static void kp3Cost(Game g, KeptBench bench) throws Exception` | Sections 39 and 43: the kept month on Build's Overview and on a city category with two orders dialled up and a card pinned, and on the construction page's Sites tab, with SITE_ROWS sites held on through the months as ... |
| 459 | 38 | `static void sectorsCost(Game g, KeptBench bench) throws Exception` | Section 52: the kept month on the Sectors list and on the three business pages that weigh most, with the plants held and a works standing (KeptCheck's 47 to 54), the clock running at the ladder's top rung as section 5... |
| 499 | 20 | `static KeptBench.Page heaviest(KeptBench bench, String tab, List<KeptBench.Page> pages) throws Exception` | The page of a tab that weighs most (the nodes its first draw makes), by name: printed with the others. |
| 525 | 72 | `static void moneyCost(Game g, KeptBench bench) throws Exception` | Section 68: what the kept month costs on the heaviest page of the Bank, of Finances and of the fund (and the fund's Activity and Search), the clock paused so that every month counts up, as KeptCheck's 55 to 67 draw th... |
| 599 | 18 | `static void kp6Cost(Game g, KeptBench bench) throws Exception` | Section 81: what the kept month costs on the Government Overview, on Trade's Overview and its heaviest page, and on Policy's hub and its dial-heavy pages. |
| 622 | 21 | `static void kp7Cost(Game g, KeptBench bench) throws Exception` | Section 90: what the kept month costs on Household money - as it opens and with the first family cell's books open - and on the Rankings tab - as it opens and with every measure unfolded - against the same month built... |
| 645 | 3 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception` | Section n: what the kept month on the page's home costs, against the same month built new. |
| 650 | 32 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial, int samples) throws Excep...` | ...the median of `samples` months, also used to calibrate the fund's two list pages. |

