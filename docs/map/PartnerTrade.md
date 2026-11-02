# PartnerTrade.java - 487 lines · 36 methods · 2 constants · model

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
> 
> <p>THE CARGO TABLE (WN7). The month's tonnes are also kept by kind of cargo (Good.cargo()) and nation, a table for exports and one for imports,
> cleared with the month and kept in the State: what CallPartners draws a ship's or a train's partner by (the map sends a boat to the nation its cargo
> is bound for). A good that ships in no kind of cargo is in the month's tonnes by nation and in no kind. Nothing in the model's month reads it.

**Uses:** [Good](Good.md) (15), [Ports](Ports.md) (4), [TradeLanes](TradeLanes.md) (3), [PriceLevel](PriceLevel.md) (1), [WorldMarket](WorldMarket.md) (1), [Nations](Nations.md) (1)

**Used by (15):** [CallPartners](CallPartners.md), [DataSave](DataSave.md), [Game](Game.md), [HistorySave](HistorySave.md), [MapView](MapView.md), [PartnerCallsCheck](PartnerCallsCheck.md), [PartnersCheck](PartnersCheck.md), [PartnersPage](PartnersPage.md), [PartnersView](PartnersView.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [ReplayCheck](ReplayCheck.md), [SaveFileCheck](SaveFileCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 206 | THE TALLY |
| 339 | READS - the snapshot, and the history |
| 381 | THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `PartnerTrade.HISTORY_ROUND` | `100` | The places a table's units are rounded to when it goes to the history: hundredths of a unit. |
| 63 | `PartnerTrade.HISTORY_ROUND_MONEY` | `HISTORY_ROUND / PriceLevel.FACTOR` | ...and its money: hundredths of a thousand at today's prices, so 100 / PriceLevel.FACTOR places at the founding's (EP1's gate review). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `public int month` |  |
| 71 | `public double[] exportValue, importValue, exportTonnes, importTonnes` |  |
| 72 | `public double noPartnerExports, noPartnerImports` |  |
| 73 | `public double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes` |  |
| 78 | `public double[] cargoExportTonnes, cargoImportTonnes` | The month's tonnes by kind of cargo and nation (WN7), flat: Ports.Cargo's ordinal times the nations, plus the nation less one; exports, then imports. |
| 79 | `public List<Cell> yearCells` |  |
| 81 | `public List<GoodUnits> goodUnits` | The month's units by good, every good with trade: what the next band is struck from (GoodsMarket's own tally leaves out the draws made before the clearing). |
| 149 | `private final int n` |  |
| 152 | `private int month` | The month being tallied, or -1 before the first; its figures by nation. |
| 153 | `private final double[] exportValue, importValue, exportTonnes, importTonnes` |  |
| 154 | `private double noPartnerExports, noPartnerImports` |  |
| 157 | `private final double[][] cargoExportTonnes, cargoImportTonnes` | The month's tonnes by kind of cargo (Ports.Cargo's ordinal) and nation, exports and imports (WN7): each good's tonnes by nation, added to its cargo's row. |
| 160 | `private final Map<Good, double[]> monthUnits` | The month's world trade by good, in units: {out, in}. |
| 163 | `private int year` | The calendar year held, or -1; its figures by nation and by (good, nation): {units out, units in, value out, value in}. |
| 164 | `private final double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes` |  |
| 165 | `private final Map<Good, TreeMap<Integer, double[]>> cells` |  |
| 168 | `private final List<Table> history` | The finished years, oldest first. |
| 170 | `private double standInLat` |  |
| 171 | `private String[] names` |  |
| 173 | `private volatile Snapshot published` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 449 | **type** `public final class PartnerTrade` | The city's trade by partner: what it sold to each nation and bought of each this month and so far this year, by good, and every finished year's table (batch WN3; docs/studies/spec-nations.md 6 and 8). |
| 42 | 1 | **type** `public record Cell(Good good, int nation, double unitsOut, double unitsIn, double valueOut, double valueIn)` | A good and a nation, and what the city sold there and bought of it over a span: units, and thousands of the city's money. |
| 45 | 5 | **type** `public record Table(int year, List<Cell> cells)` | A finished year's table, sparse: the cells of the (good, nation) pairs that traded, in good order and then nation order. |
| 46 | 3 | `{ ... }` _(in PartnerTrade.Table)_ |  |
| 52 | 1 | **type** `public record GoodUnits(Good good, double unitsOut, double unitsIn)` | A good's trade with the world in a month, in its units: what the city sold abroad and what it bought. |
| 69 | 14 | **type** `public static final class State` | The state a save carries, plain fields (Gson matches them by name): the month last tallied by nation, with the trade that had no partner, and the year to date by nation and by (good, nation). |
| 89 | 59 | **type** `public record Snapshot(int month, int year, double[] exportValue, double[] importValue, double[] exportTonn...` | The month and the year as the screen's thread reads them, and the map's feed over them: every array is this snapshot's own and is never written once it is published, so a reader may keep them and compare them (TradeLa... |
| 99 | 1 | `public double[] exportsByNation()` _(in PartnerTrade.Snapshot)_ | month(), standInLat() and standInLon() are the record's own accessors, which are the Feed's. |
| 101 | 1 | `public double[] importsByNation()` _(in PartnerTrade.Snapshot)_ |  |
| 103 | 5 | `public TradeLanes.YearToDate yearToDate(int nation)` _(in PartnerTrade.Snapshot)_ |  |
| 109 | 3 | `public String nameOf(int nation)` _(in PartnerTrade.Snapshot)_ |  |
| 114 | 1 | `public int nations()` _(in PartnerTrade.Snapshot)_ | How many nations the figures are of. |
| 120 | 4 | `public double monthExportUnits(Good good)` _(in PartnerTrade.Snapshot)_ | What the city sold the world of a good in the month last tallied, in the good's units (WN6: the Partners page marks where the city stands on a good's curve by it; the live record's monthExportUnits() is the model's th... |
| 126 | 4 | `public double monthImportUnits(Good good)` _(in PartnerTrade.Snapshot)_ | ...and what it bought of the world. |
| 136 | 3 | `public double[] cargoTonnes(Ports.Cargo c, boolean out)` _(in PartnerTrade.Snapshot)_ | The month's tonnes of one kind of cargo sold to each nation (`out`) or bought of each, indexed by the nation less one (WN7: CallPartners draws a ship's or a train's partner by them). |
| 141 | 6 | `public double monthTotal()` _(in PartnerTrade.Snapshot)_ | The month's trade with the nations, both ways together, thousands of the city's money. |
| 176 | 15 | `public PartnerTrade(int nations)` | A record for a world of `nations` nations: nothing tallied, no month. |
| 193 | 1 | `public int nations()` | How many nations. |
| 199 | 6 | `public void setPlace(double standInLat, double standInLon, String[] names)` | Where the city stands, for a generated world's arrows at the edge of its map (TradeLanes.Feed.standInLat()), NaN for a city on the real Earth, and the nations' names (index nation - 1). |

### THE TALLY (lines 206-338)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 13 | `public void tally(int month, Good good, WorldMarket.Allocation allocation, double exportUnits, double exportValue, double impor...` | One good's trade with the world in a month, split to the nations: the exports by the allocation's units to each nation and the imports by its units from each, the value of each side pro rata to the units (so a nation'... |
| 239 | 27 | `private boolean split(Good good, double[] units, double value, double unitsPerTonne, boolean out)` | Spreads one side of a good over the nations by units; false (and nothing kept) where the units add to nothing. |
| 271 | 4 | `public void close(int month)` | The month's tally is done: publishes the snapshot the screens read. |
| 277 | 17 | `private void open(int m)` | Starts a month: clears the month's figures, and turns the year first if the month is the next calendar year's. |
| 300 | 4 | `public double monthExportUnits(Good good)` | What the city sold the world of a good in the month last tallied, in its units: every fill and every draw of the month, the draws made before the clearing too (GoodsMarket's own tally wipes those at the clearing: the ... |
| 306 | 4 | `public double monthImportUnits(Good good)` | ...and what it bought of the world. |
| 312 | 8 | `private void finishYear()` | Sends the year held to the history, rounded, and starts the next empty. |
| 322 | 12 | `private List<Cell> cellList(boolean rounded)` | The year's cells in good order and then nation order, rounded for the history or exact for the state. |
| 335 | 1 | `private static double round(double v)` |  |
| 337 | 1 | `private static double roundMoney(double v)` |  |

### READS - the snapshot, and the history (lines 339-380)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 1 | `public Snapshot snapshot()` | The snapshot the screens read: the last month closed, whole, immutable. |
| 347 | 1 | `public List<Table> history()` | The finished years, oldest first, as a copy; the model's thread. |
| 350 | 1 | `public int yearsFinished()` | How many years the history holds. |
| 353 | 1 | `public int month()` | The month last tallied, or -1. |
| 356 | 1 | `public int year()` | The calendar year held, or -1. |
| 358 | 9 | `private void publish()` |  |
| 369 | 5 | `private List<GoodUnits> unitsByGood()` | The month's units by good as the snapshot keeps them, in good order. |
| 375 | 5 | `private static double[][] copy(double[][] a)` |  |

### THE SAVE (lines 381-487)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 21 | `public State toState()` | What a save keeps, fresh arrays and a fresh list: nothing here is shared with the model. |
| 408 | 6 | `private static double[] flat(double[][] a)` |  |
| 416 | 8 | `private void unflat(double[] flat, double[][] rows)` | The flat table back into its rows; zeros where the save has none (an older save: Gson leaves the key null), or has it the wrong length or not a number. |
| 429 | 38 | `public boolean restore(State s)` | Puts a saved month and year back and publishes them. |
| 469 | 12 | `public void restoreHistory(List<Table> years)` | Puts the finished years back (HistorySave) and publishes. |
| 482 | 5 | `private boolean sized(double[] a)` |  |

