# DenominationCheck.java - 1,356 lines · 41 methods · 5 constants · harnesses

`ham/citybuildersim/DenominationCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
> 
> Section 7's third part (TP1, 2026-11-08) is what the screens read of it: the
> nation card's year to date, the month's figures the map's lanes and arrows
> are made of, and the floor under which a partner has none (TradeLanes.minLane(),
> MIN_LANE in the price index and the currency's unit). A bare part holds the
> floor's arithmetic; the same two cities, on both sides of the reform and
> again with prices moved, show that the reformed city's card is the plain
> one's divided and that it draws the same partners. The partner it draws is
> given its month's trade (FS2, givePartner()): twice the plain city's floor,
> the reformed city's divided, so a lane is there to draw on any path.

**Uses:** [PartnerTrade](PartnerTrade.md) (52), [Game](Game.md) (42), [TradeLanes](TradeLanes.md) (32), [LongPlaytest](LongPlaytest.md) (17), [Equity](Equity.md) (12), [Sector](Sector.md) (8), [CityCalendar](CityCalendar.md) (8), [Good](Good.md) (7), [Denomination](Denomination.md) (6), [Nations](Nations.md) (6), [WorldData](WorldData.md) (5), [GameFiles](GameFiles.md) (4), [Household](Household.md) (4), [WorldMarket](WorldMarket.md) (4), [HousingChoice](HousingChoice.md) (4), [Founding](Founding.md) (3), [PriceLevel](PriceLevel.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Currency](Currency.md) (2), [HistorySave](HistorySave.md) (2), [Mining](Mining.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [TempDirs](TempDirs.md) (1), [Sectors](Sectors.md) (1), [TaxPolicy](TaxPolicy.md) (1), [WorkingHousehold](WorkingHousehold.md) (1), [WorldDataCheck](WorldDataCheck.md) (1), [HousingForms](HousingForms.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

## Sections

| line | section |
|---:|---|
| 153 | · (untitled) |
| 205 | · (untitled) |
| 213 | · 1. THE UNIT |
| 239 | · 2. IT IS THE SAME CITY |
| 281 | · ...AND THE SHARE DESK'S ROOM DOES NOT DIVIDE, WHICH IS THE POINT |
| 407 | · 3. AND IT STAYS THE SAME CITY |
| 519 | · 3a. AND NO CELL UNDER HALF A HOUSEHOLD HOLDS ANYTHING |
| 539 | · 4. AND NO MONEY WAS MADE OR LOST |
| 554 | · 5. AND IT SURVIVES A SAVE |
| 583 | · 6. AND THE TRAMS (B6, 0.7.47) |
| 645 | · 7. THE TRADE BY PARTNER (DZ1, 2026-11-02) |
| 686 | · section 7: the trade by partner |
| 832 | · section 7, third part: the lanes' floor (TP1, 2026-11-08) |
| 871 | · section 7, second part: the Game's two lines, in a city |
| 1199 | · helpers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 689 | `DenominationCheck.TRADE_STEP` | `1 / PartnerTrade.HISTORY_ROUND_MONEY` | The founding grid's step: what the history's money is rounded to before a reform. |
| 874 | `DenominationCheck.TRADE_WORLD_SEED` | `2_026_102_8L` | The world seed of the second part's cities: ReadPathCheck's WN3 city's, so the fixture is one the repository already plays. |
| 877 | `DenominationCheck.REFORM_MONTH_OF_YEAR` | `7` | The month of the year the reform is put in: July, the middle of the calendar year, six tallies behind it and five or six in front. |
| 1087 | `DenominationCheck.LANES_FROM_LAT` | `44.65, LANES_FROM_LON = - 63.57` | Where the lanes of cardAndLanes() are drawn from: Halifax, as GlobeCheck's city. |
| 1090 | `DenominationCheck.GIVEN_FLOORS` | `2` | How many times the plain city's floor a partner is given in the month the card and the lanes read (FS2, givePartner()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 64 | `static int fails` |  |
| 65 | `static PrintStream out` |  |
| 66 | `static PrintStream quiet` |  |
| 1093 | `static int givenMonth` | The month givePartner() last gave the partner its trade in; a reform in that month has already divided it in the reformed city. |
| 1202 | `static int emptyHolding` | Cells under half a household holding something, counted over every scan. |
| 1204 | `static int emptyHeldAfterReform` | ...and the same, as it stood right after the reform. |
| 1205 | `static int looks` |  |
| 1218 | `static boolean housingParted, housingAsksParted, exchangeParted, exchangeBookParted, exchangeQuoteParted` |  |
| 1219 | `static boolean offeringParted` |  |
| 1220 | `static String[] previousPlainInputs` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 62 | 1295 | **type** `public class DenominationCheck` | A currency reform is a change of units, and this is how we know. |
| 68 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 84 | 23 | `static void sameCity(Game plain, Game lopped, double factor, double band)` | Everything real to the person, everything nominal to the factor. |
| 109 | 11 | `static void relative(String label, double actual, double expected, double band)` | Equal to within a RELATIVE band, so one helper works at any scale. |
| 122 | 4 | `static double gap(double actual, double expected)` | How far apart two figures are, as a percentage of the expected one. |
| 127 | 9 | `static void within(String label, double actual, double expected, double band)` |  |
| 137 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 147 | 5 | `static void quietly(Runnable work)` |  |

### (untitled) (lines 153-204)

| line | len | member | says |
|---:|---:|---|---|
| 156 | 1 | `static Game city(String name)` | A city with a bit of everything in it, so the reform has work to do. |
| 159 | 45 | `static Game city(String name, boolean buses)` | ...with a bus network on its roads too (B6, 0.7.47): a fare, riders and the month's transit lines for a reform to carry. |

### (untitled) (lines 205-685)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 478 | `public static void main(String[] args) throws Exception` |  |

### section 7: the trade by partner (lines 686-831)

| line | len | member | says |
|---:|---:|---|---|
| 698 | 7 | `static void tallyMonth(PartnerTrade t, int month, double by)` | One month of the fixture's trade, every money figure divided by `by` (the reformed copies' unit): crops and grains exported and imported to two nations (the first takes three quarters of the exports, each half the imp... |
| 707 | 3 | `static double gapOf(double actual, double expected)` | How far apart two figures are, relative to the expected one; infinity where the expected is nothing and the actual is not. |
| 712 | 6 | `static double arrayGap(double[] actual, double[] expected, double scale)` | The worst gap between two arrays, the expected one times `scale`. |
| 720 | 10 | `static double cellGap(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected, double scale)` | The worst gap between the money of two lists of cells, the expected times `scale`; infinity where they are not the same cells. |
| 732 | 7 | `static boolean sameUnits(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected)` | Whether two lists of cells have the same units, to the bit. |
| 741 | 90 | `static void tradeByPartner()` | Section 7's first part, on bare PartnerTrade objects. |

### section 7, third part: the lanes' floor (TP1, 2026-11-08) (lines 832-870)

| line | len | member | says |
|---:|---:|---|---|
| 838 | 32 | `static void lanesFloor()` | The floor under which a partner has no lane, on bare TradeLanes: the board's D$0.3M at the founding's prices (times PriceLevel.FACTOR), moved by the price index and the currency's unit alone, and the lanes and arrows ... |

### section 7, second part: the Game's two lines, in a city (lines 871-1198)

| line | len | member | says |
|---:|---:|---|---|
| 880 | 17 | `static void tradeByPartnerInGame(Path root) throws Exception` | Section 7's second part: two cities on the twelve-nation world fixture, one reformed in mid-year (the file is named for the property while they play). |
| 899 | 14 | `static Game worldCity(Path root, String name)` | ReadPathCheck's WN3 city (120 houses, two convenience stores, a water treatment plant) on the fixture file, played until its trade by partner has finished a year. |
| 915 | 3 | `static void monthOn(Game a, Game b)` | One month on both cities, quietly. |
| 920 | 1 | `static PartnerTrade tradeOf(Game g)` | A city's live trade by partner, or null where it has none. |
| 922 | 5 | `static double sum(double[] a)` |  |
| 929 | 4 | `static boolean hasTrade(PartnerTrade.Table t)` | Whether a finished year's table holds any trade in money. |
| 935 | 5 | `static void gapWithin(String label, double gap, double band)` | A worst gap measured, held to a band, and said on its line: the margin is there for a reader to see. |
| 942 | 10 | `static double unitsGap(java.util.List<PartnerTrade.Cell> actual, java.util.List<PartnerTrade.Cell> expected)` | The worst gap between the units of two lists of cells, relative to the expected; infinity where they are not the same cells. |
| 954 | 8 | `static double stateMoneyGap(PartnerTrade.State actual, PartnerTrade.State expected, double scale)` | The worst gap between the money of two records' state, the expected times `scale`: the month and the year to date by nation, the trade with no partner, and the year's cells. |
| 964 | 8 | `static double stateRealGap(PartnerTrade.State actual, PartnerTrade.State expected)` | The worst gap between the real figures of two records' state, nothing scaled: the tonnes by nation (month and year, cargo too) and the year's units. |
| 974 | 9 | `static double historyMoneyGap(java.util.List<PartnerTrade.Table> actual, java.util.List<PartnerTrade.Table> expected, double sc...` | The worst gap between the money of two lists of finished years, the expected times `scale`; infinity where they are not the same years. |
| 985 | 6 | `static double historyUnitsGap(java.util.List<PartnerTrade.Table> actual, java.util.List<PartnerTrade.Table> expected)` | ...and the units of them. |
| 997 | 88 | `static void reformedInMidYear(Path root)` | Two cities on the fixture world, identical until the reform, side by side: the reform is put in July of their second year, with the January behind it (a year turned, so the history holds a year in the old money) and t... |
| 1102 | 25 | `static void givePartner(Game plain, Game lopped, double scale)` | The lanes' fixture caused, not found (FS2): in the month the card and the lanes read, the partner with the most trade this year that stands apart from Halifax (else the first that does) is given exports of GIVEN_FLOOR... |
| 1136 | 62 | `static void cardAndLanes(String when, Game plain, Game lopped, double scale, boolean reformed)` | What the screens read of two cities' trade by partner, the second the first reformed by 1 / scale (scale 1: the same city, before the reform): the nation card's year to date, the month's figures the lanes are made of,... |

### helpers (lines 1199-1356)

| line | len | member | says |
|---:|---:|---|---|
| 1208 | 9 | `static void stepBoth(Game a, Game b, int months)` | Both cities a month at a time, each scanned after every month. |
| 1221 | 8 | `static String offeringInputs(Game city, int company, double factor)` |  |
| 1230 | 84 | `static void housingTrace(Game a, Game b)` | First differing cell/class allocation, beside the existing money trace. |
| 1316 | 11 | `static void scanEmpty(Game g)` | One look at a city: whether any cell is under half a household, and any of them holding something. |
| 1329 | 5 | `static boolean force(Game g, double factor)` | Reforms regardless of the price-level gate, which is tested separately. |
| 1336 | 7 | `static boolean same(Game a, Game b)` | The real city: who lives there and how much of everything there is. |
| 1344 | 3 | `static double shelf(Game g)` |  |
| 1348 | 3 | `static double rent(Game g)` |  |
| 1352 | 4 | `static double cost(Game g, String name)` |  |

