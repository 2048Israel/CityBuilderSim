# PartnerTrade.java - 408 lines · 29 methods · 2 constants · model

`ham/citybuildersim/PartnerTrade.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

> The city's trade by partner: what it sold to each nation and bought of each this month and so far this year, by good, and every finished year's
> table (batch WN3; docs/studies/spec-nations.md 6 and 8).
> 
> <p>WHY THIS EXISTS. Until the world was a market the city traded with "the world": GoodsMarket recorded a fill with the world as its other side,
> and no screen could say who the world was. The market (WorldMarket) now says who took the city's exports and who sold its imports
> (allocate(), by the same fills the band is made of); the map draws the month's trade as lanes (TradeLanes), a nation's card says what it took and
> sold this year, and City History reads the years behind. All of that is this one record, kept at the end of each month by Game (THE WORLD MARKET,
> tallyThePartners()) from the month's own fills and draws: the values are what the trades came to (units times the price each was struck at, in the
> city's money, thousands), split to the nations pro rata to the units the market allocated, so a nation's share of a good's value is its share of
> the units and the nations add up to the month's trade, to the rounding. Trade with no partner - a good the world file does not model, or one with
> no buyers or no sellers at the city's site - is kept apart (noPartnerExports(), noPartnerImports()) so the total still reconciles.
> 
> <p>THE BAND IS STRUCK FROM IT TOO. The month's units by good - every fill and every draw with the world on the other side (monthExportUnits(),
> monthImportUnits()) - are what the next month's band is made of (Markets.setWorldBand()). GoodsMarket's own tally would not do: the clearing wipes
> it of the draws made before it, and the city's fuel for its fleets and its builders' materials are all draws (a playtest city's petrol and diesel
> imports are millions of units a month in the trades and none in the tally).
> 
> <p>THE YEAR TURNS HERE. The first tally of a calendar year (Nations.yearOf(month)) sends the finished year's table to the history (history(),
> rounded to cents of the city's money and hundredths of a unit, as every series kept for ever is) and starts the year empty. The history is saved in
> the HistorySave, a list a year; the month's and the year to date in the DataSave (State), because a flow cannot be reconstructed from the state a
> month ended in: the lanes a loaded city draws are last month's trade.
> 
> <p>TWO THREADS. The model's thread writes, and publishes an immutable Snapshot through a volatile at the end of every tally (close()); the
> screen's thread reads the snapshot and never sees a half-tallied month. Everything else here is the model thread's alone.

**Uses:** [Good](Good.md) (12), [TradeLanes](TradeLanes.md) (3), [PriceLevel](PriceLevel.md) (1), [WorldMarket](WorldMarket.md) (1), [Nations](Nations.md) (1)

**Used by (9):** [DataSave](DataSave.md), [Game](Game.md), [HistorySave](HistorySave.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [ReplayCheck](ReplayCheck.md), [SaveFileCheck](SaveFileCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 167 | THE TALLY |
| 295 | READS - the snapshot, and the history |
| 323 | THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `PartnerTrade.HISTORY_ROUND` | `100` | The places a table's units are rounded to when it goes to the history: hundredths of a unit. |
| 59 | `PartnerTrade.HISTORY_ROUND_MONEY` | `HISTORY_ROUND / PriceLevel.FACTOR` | ...and its money: hundredths of a thousand at today's prices, so 100 / PriceLevel.FACTOR places at the founding's (EP1's gate review). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `public int month` |  |
| 67 | `public double[] exportValue, importValue, exportTonnes, importTonnes` |  |
| 68 | `public double noPartnerExports, noPartnerImports` |  |
| 69 | `public double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes` |  |
| 70 | `public List<Cell> yearCells` |  |
| 72 | `public List<GoodUnits> goodUnits` | The month's units by good, every good with trade: what the next band is struck from (GoodsMarket's own tally leaves out the draws made before the clearing). |
| 115 | `private final int n` |  |
| 118 | `private int month` | The month being tallied, or -1 before the first; its figures by nation. |
| 119 | `private final double[] exportValue, importValue, exportTonnes, importTonnes` |  |
| 120 | `private double noPartnerExports, noPartnerImports` |  |
| 123 | `private final Map<Good, double[]> monthUnits` | The month's world trade by good, in units: {out, in}. |
| 126 | `private int year` | The calendar year held, or -1; its figures by nation and by (good, nation): {units out, units in, value out, value in}. |
| 127 | `private final double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes` |  |
| 128 | `private final Map<Good, TreeMap<Integer, double[]>> cells` |  |
| 131 | `private final List<Table> history` | The finished years, oldest first. |
| 133 | `private double standInLat` |  |
| 134 | `private String[] names` |  |
| 136 | `private volatile Snapshot published` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 374 | **type** `public final class PartnerTrade` | The city's trade by partner: what it sold to each nation and bought of each this month and so far this year, by good, and every finished year's table (batch WN3; docs/studies/spec-nations.md 6 and 8). |
| 38 | 1 | **type** `public record Cell(Good good, int nation, double unitsOut, double unitsIn, double valueOut, double valueIn)` | A good and a nation, and what the city sold there and bought of it over a span: units, and thousands of the city's money. |
| 41 | 5 | **type** `public record Table(int year, List<Cell> cells)` | A finished year's table, sparse: the cells of the (good, nation) pairs that traded, in good order and then nation order. |
| 42 | 3 | `{ ... }` _(in PartnerTrade.Table)_ |  |
| 48 | 1 | **type** `public record GoodUnits(Good good, double unitsOut, double unitsIn)` | A good's trade with the world in a month, in its units: what the city sold abroad and what it bought. |
| 65 | 9 | **type** `public static final class State` | The state a save carries, plain fields (Gson matches them by name): the month last tallied by nation, with the trade that had no partner, and the year to date by nation and by (good, nation). |
| 80 | 34 | **type** `public record Snapshot(int month, int year, double[] exportValue, double[] importValue, double[] exportTonn...` | The month and the year as the screen's thread reads them, and the map's feed over them: every array is this snapshot's own and is never written once it is published, so a reader may keep them and compare them (TradeLa... |
| 89 | 1 | `public double[] exportsByNation()` _(in PartnerTrade.Snapshot)_ | month(), standInLat() and standInLon() are the record's own accessors, which are the Feed's. |
| 91 | 1 | `public double[] importsByNation()` _(in PartnerTrade.Snapshot)_ |  |
| 93 | 5 | `public TradeLanes.YearToDate yearToDate(int nation)` _(in PartnerTrade.Snapshot)_ |  |
| 99 | 3 | `public String nameOf(int nation)` _(in PartnerTrade.Snapshot)_ |  |
| 104 | 1 | `public int nations()` _(in PartnerTrade.Snapshot)_ | How many nations the figures are of. |
| 107 | 6 | `public double monthTotal()` _(in PartnerTrade.Snapshot)_ | The month's trade with the nations, both ways together, thousands of the city's money. |
| 139 | 13 | `public PartnerTrade(int nations)` | A record for a world of `nations` nations: nothing tallied, no month. |
| 154 | 1 | `public int nations()` | How many nations. |
| 160 | 6 | `public void setPlace(double standInLat, double standInLon, String[] names)` | Where the city stands, for a generated world's arrows at the edge of its map (TradeLanes.Feed.standInLat()), NaN for a city on the real Earth, and the nations' names (index nation - 1). |

### THE TALLY (lines 167-294)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 13 | `public void tally(int month, Good good, WorldMarket.Allocation allocation, double exportUnits, double exportValue, double impor...` | One good's trade with the world in a month, split to the nations: the exports by the allocation's units to each nation and the imports by its units from each, the value of each side pro rata to the units (so a nation'... |
| 200 | 24 | `private boolean split(Good good, double[] units, double value, double unitsPerTonne, boolean out)` | Spreads one side of a good over the nations by units; false (and nothing kept) where the units add to nothing. |
| 229 | 4 | `public void close(int month)` | The month's tally is done: publishes the snapshot the screens read. |
| 235 | 15 | `private void open(int m)` | Starts a month: clears the month's figures, and turns the year first if the month is the next calendar year's. |
| 256 | 4 | `public double monthExportUnits(Good good)` | What the city sold the world of a good in the month last tallied, in its units: every fill and every draw of the month, the draws made before the clearing too (GoodsMarket's own tally wipes those at the clearing: the ... |
| 262 | 4 | `public double monthImportUnits(Good good)` | ...and what it bought of the world. |
| 268 | 8 | `private void finishYear()` | Sends the year held to the history, rounded, and starts the next empty. |
| 278 | 12 | `private List<Cell> cellList(boolean rounded)` | The year's cells in good order and then nation order, rounded for the history or exact for the state. |
| 291 | 1 | `private static double round(double v)` |  |
| 293 | 1 | `private static double roundMoney(double v)` |  |

### READS - the snapshot, and the history (lines 295-322)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 1 | `public Snapshot snapshot()` | The snapshot the screens read: the last month closed, whole, immutable. |
| 303 | 1 | `public List<Table> history()` | The finished years, oldest first, as a copy; the model's thread. |
| 306 | 1 | `public int yearsFinished()` | How many years the history holds. |
| 309 | 1 | `public int month()` | The month last tallied, or -1. |
| 312 | 1 | `public int year()` | The calendar year held, or -1. |
| 314 | 8 | `private void publish()` |  |

### THE SAVE (lines 323-408)

| line | len | member | says |
|---:|---:|---|---|
| 328 | 19 | `public State toState()` | What a save keeps, fresh arrays and a fresh list: nothing here is shared with the model. |
| 352 | 36 | `public boolean restore(State s)` | Puts a saved month and year back and publishes them. |
| 390 | 12 | `public void restoreHistory(List<Table> years)` | Puts the finished years back (HistorySave) and publishes. |
| 403 | 5 | `private boolean sized(double[] a)` |  |

