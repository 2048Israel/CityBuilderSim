# DenominationCheck.java - 1,085 lines · 36 methods · 3 constants · harnesses

`ham/citybuildersim/DenominationCheck.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> A currency reform is a change of units, and this is how we know.
> 
> WHAT IS ACTUALLY BEING ASKED. Jerus wanted a button that divides the money by
> ten or a hundred so that three centuries of inflation do not end with bread
> priced at 300M and the arithmetic losing digits at the bottom. That is a
> currency reform, and the entire risk in one is that it is not ONLY a change
> of units: miss one balance and the button quietly creates or destroys money;
> miss one compile-time constant and a House is a hundred times dearer the next
> morning.
> 
> So the test is not a list of fields. It is TWO CITIES:
> 
>   A runs normally.
>   B is the same city, reformed, and then run for exactly as long.
> 
> If the reform is only a change of units then B is A with a different label on
> the axis: every real quantity identical to the person, every money quantity
> identical after dividing by the factor, and the two trajectories still on top
> of each other years later. Any field left unscaled changes a relative price,
> and a changed relative price changes what gets built and who moves in - so
> the populations part company and the harness says so.
> 
> That is a much stronger assertion than "the balances add up", and it is the
> only one that can be trusted, because nobody can enumerate this codebase's
> money by reading it.
> 
> The twins have no transit, so a second pair with a bus network is reformed
> by a thousand in section 6 (B6, 0.7.47): the month's transit lines, the
> riders at the same real fare, and the working rows' fixed bills.
> 
> Section 7 (DZ1, 2026-11-02) is the trade by partner. Its first part is on
> bare PartnerTrade objects, which need no world file: its history's rounding
> grid follows the currency's unit, and a reform divides the month and the year
> held, the finished years and the save's copy of them. Its second part is two
> cities on the repository's twelve-nation world fixture, one reformed in
> mid-year: the proof that Game wires the grid to the currency's unit and that
> Game.reformCurrency() divides the record, which the bare objects cannot show
> and which the twins of section 2 show only where the gate names the real
> world file.

**Uses:** [PartnerTrade](PartnerTrade.md) (49), [Game](Game.md) (31), [LongPlaytest](LongPlaytest.md) (17), [Sector](Sector.md) (8), [CityCalendar](CityCalendar.md) (7), [Denomination](Denomination.md) (6), [Nations](Nations.md) (6), [Good](Good.md) (5), [WorldData](WorldData.md) (5), [GameFiles](GameFiles.md) (4), [Household](Household.md) (4), [Founding](Founding.md) (3), [PriceLevel](PriceLevel.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [Currency](Currency.md) (2), [Equity](Equity.md) (2), [WorldMarket](WorldMarket.md) (2), [HistorySave](HistorySave.md) (2), [Mining](Mining.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [TempDirs](TempDirs.md) (1), [Sectors](Sectors.md) (1), [TaxPolicy](TaxPolicy.md) (1), [WorkingHousehold](WorkingHousehold.md) (1), [WorldDataCheck](WorldDataCheck.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

## Sections

| line | section |
|---:|---|
| 143 | · (untitled) |
| 195 | · (untitled) |
| 203 | · 1. THE UNIT |
| 229 | · 2. IT IS THE SAME CITY |
| 271 | · ...AND THE SHARE DESK'S ROOM DOES NOT DIVIDE, WHICH IS THE POINT |
| 397 | · 3. AND IT STAYS THE SAME CITY |
| 509 | · 3a. AND NO CELL UNDER HALF A HOUSEHOLD HOLDS ANYTHING |
| 529 | · 4. AND NO MONEY WAS MADE OR LOST |
| 544 | · 5. AND IT SURVIVES A SAVE |
| 573 | · 6. AND THE TRAMS (B6, 0.7.47) |
| 635 | · 7. THE TRADE BY PARTNER (DZ1, 2026-11-02) |
| 675 | · section 7: the trade by partner |
| 821 | · section 7, second part: the Game's two lines, in a city |
| 1026 | · helpers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 678 | `DenominationCheck.TRADE_STEP` | `1 / PartnerTrade.HISTORY_ROUND_MONEY` | The founding grid's step: what the history's money is rounded to before a reform. |
| 824 | `DenominationCheck.TRADE_WORLD_SEED` | `2_026_102_8L` | The world seed of the second part's cities: ReadPathCheck's WN3 city's, so the fixture is one the repository already plays. |
| 827 | `DenominationCheck.REFORM_MONTH_OF_YEAR` | `7` | The month of the year the reform is put in: July, the middle of the calendar year, six tallies behind it and five or six in front. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `static int fails` |  |
| 55 | `static PrintStream out` |  |
| 56 | `static PrintStream quiet` |  |
| 1029 | `static int emptyHolding` | Cells under half a household holding something, counted over every scan. |
| 1031 | `static int emptyHeldAfterReform` | ...and the same, as it stood right after the reform. |
| 1032 | `static int looks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 1034 | **type** `public class DenominationCheck` | A currency reform is a change of units, and this is how we know. |
| 58 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 74 | 23 | `static void sameCity(Game plain, Game lopped, double factor, double band)` | Everything real to the person, everything nominal to the factor. |
| 99 | 11 | `static void relative(String label, double actual, double expected, double band)` | Equal to within a RELATIVE band, so one helper works at any scale. |
| 112 | 4 | `static double gap(double actual, double expected)` | How far apart two figures are, as a percentage of the expected one. |
| 117 | 9 | `static void within(String label, double actual, double expected, double band)` |  |
| 127 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 137 | 5 | `static void quietly(Runnable work)` |  |

### (untitled) (lines 143-194)

| line | len | member | says |
|---:|---:|---|---|
| 146 | 1 | `static Game city(String name)` | A city with a bit of everything in it, so the reform has work to do. |
| 149 | 45 | `static Game city(String name, boolean buses)` | ...with a bus network on its roads too (B6, 0.7.47): a fare, riders and the month's transit lines for a reform to carry. |

### (untitled) (lines 195-674)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 477 | `public static void main(String[] args) throws Exception` |  |

### section 7: the trade by partner (lines 675-820)

| line | len | member | says |
|---:|---:|---|---|
| 687 | 7 | `static void tallyMonth(PartnerTrade t, int month, double by)` | One month of the fixture's trade, every money figure divided by `by` (the reformed copies' unit): crops and grains exported and imported to two nations (the first takes three quarters of the exports, each half the imp... |
| 696 | 3 | `static double gapOf(double actual, double expected)` | How far apart two figures are, relative to the expected one; infinity where the expected is nothing and the actual is not. |
| 701 | 6 | `static double arrayGap(double[] actual, double[] expected, double scale)` | The worst gap between two arrays, the expected one times `scale`. |
| 709 | 10 | `static double cellGap(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected, double scale)` | The worst gap between the money of two lists of cells, the expected times `scale`; infinity where they are not the same cells. |
| 721 | 7 | `static boolean sameUnits(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected)` | Whether two lists of cells have the same units, to the bit. |
| 730 | 90 | `static void tradeByPartner()` | Section 7's first part, on bare PartnerTrade objects. |

### section 7, second part: the Game's two lines, in a city (lines 821-1025)

| line | len | member | says |
|---:|---:|---|---|
| 830 | 17 | `static void tradeByPartnerInGame(Path root) throws Exception` | Section 7's second part: two cities on the twelve-nation world fixture, one reformed in mid-year (the file is named for the property while they play). |
| 849 | 14 | `static Game worldCity(Path root, String name)` | ReadPathCheck's WN3 city (120 houses, two convenience stores, a water treatment plant) on the fixture file, played until its trade by partner has finished a year. |
| 865 | 3 | `static void monthOn(Game a, Game b)` | One month on both cities, quietly. |
| 870 | 1 | `static PartnerTrade tradeOf(Game g)` | A city's live trade by partner, or null where it has none. |
| 872 | 5 | `static double sum(double[] a)` |  |
| 879 | 4 | `static boolean hasTrade(PartnerTrade.Table t)` | Whether a finished year's table holds any trade in money. |
| 885 | 5 | `static void gapWithin(String label, double gap, double band)` | A worst gap measured, held to a band, and said on its line: the margin is there for a reader to see. |
| 892 | 10 | `static double unitsGap(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected)` | The worst gap between the units of two lists of cells, relative to the expected; infinity where they are not the same cells. |
| 904 | 8 | `static double stateMoneyGap(PartnerTrade.State actual, PartnerTrade.State expected, double scale)` | The worst gap between the money of two records' state, the expected times `scale`: the month and the year to date by nation, the trade with no partner, and the year's cells. |
| 914 | 8 | `static double stateRealGap(PartnerTrade.State actual, PartnerTrade.State expected)` | The worst gap between the real figures of two records' state, nothing scaled: the tonnes by nation (month and year, cargo too) and the year's units. |
| 924 | 9 | `static double historyMoneyGap(java.util.List<PartnerTrade.Table> actual, java.util.List<PartnerTrade.Table> expected, double sc...` | The worst gap between the money of two lists of finished years, the expected times `scale`; infinity where they are not the same years. |
| 935 | 6 | `static double historyUnitsGap(java.util.List<PartnerTrade.Table> actual, java.util.List<PartnerTrade.Table> expected)` | ...and the units of them. |
| 947 | 78 | `static void reformedInMidYear(Path root)` | Two cities on the fixture world, identical until the reform, side by side: the reform is put in July of their second year, with the January behind it (a year turned, so the history holds a year in the old money) and t... |

### helpers (lines 1026-1085)

| line | len | member | says |
|---:|---:|---|---|
| 1035 | 8 | `static void stepBoth(Game a, Game b, int months)` | Both cities a month at a time, each scanned after every month. |
| 1045 | 11 | `static void scanEmpty(Game g)` | One look at a city: whether any cell is under half a household, and any of them holding something. |
| 1058 | 5 | `static boolean force(Game g, double factor)` | Reforms regardless of the price-level gate, which is tested separately. |
| 1065 | 7 | `static boolean same(Game a, Game b)` | The real city: who lives there and how much of everything there is. |
| 1073 | 3 | `static double shelf(Game g)` |  |
| 1077 | 3 | `static double rent(Game g)` |  |
| 1081 | 4 | `static double cost(Game g, String name)` |  |

