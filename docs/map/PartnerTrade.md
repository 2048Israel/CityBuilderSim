# PartnerTrade.java - 573 lines · 42 methods · 2 constants · model

`ham/citybuildersim/PartnerTrade.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

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
> <p>THE LANES' FLOOR (TP1). The map draws a partner only over a floor in the city's money, and the money is the record's own: each snapshot carries
> minLane(), TradeLanes.minLane() of the price index (setPriceIndex()) and the currency's unit (setMoneyUnit()) at the tally or the reform that
> published it, so the floor and the figures it is held against are always in the same money. A floor in absolute money (MIN_LANE alone) drifted
> with inflation and, after a reform dividing every trade by a hundred, hid every lane.
> 
> <p>TWO THREADS. The model's thread writes, and publishes an immutable Snapshot through a volatile at the end of every tally (close()); the
> screen's thread reads the snapshot and never sees a half-tallied month. Everything else here is the model thread's alone.
> 
> <p>THE CARGO TABLE (WN7). The month's tonnes are also kept by kind of cargo (Good.cargo()) and nation, a table for exports and one for imports,
> cleared with the month and kept in the State: what CallPartners draws a ship's or a train's partner by (the map sends a boat to the nation its cargo
> is bound for). A good that ships in no kind of cargo is in the month's tonnes by nation and in no kind. Nothing in the model's month reads it.

**Uses:** [Good](Good.md) (15), [TradeLanes](TradeLanes.md) (4), [Ports](Ports.md) (4), [PriceLevel](PriceLevel.md) (1), [WorldMarket](WorldMarket.md) (1), [Nations](Nations.md) (1)

**Used by (16):** [CallPartners](CallPartners.md), [DataSave](DataSave.md), [DenominationCheck](DenominationCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [MapView](MapView.md), [PartnerCallsCheck](PartnerCallsCheck.md), [PartnersCheck](PartnersCheck.md), [PartnersPage](PartnersPage.md), [PartnersView](PartnersView.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [ReplayCheck](ReplayCheck.md), [SaveFileCheck](SaveFileCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 259 | THE TALLY |
| 396 | READS - the snapshot, and the history |
| 438 | THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `PartnerTrade.HISTORY_ROUND` | `100` | The places a table's units are rounded to when it goes to the history: hundredths of a unit. |
| 81 | `PartnerTrade.HISTORY_ROUND_MONEY` | `HISTORY_ROUND / PriceLevel.FACTOR` | ...and its money: hundredths of a thousand at today's prices, so 100 / PriceLevel.FACTOR places at the founding's (EP1's gate review). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `public int month` |  |
| 89 | `public double[] exportValue, importValue, exportTonnes, importTonnes` |  |
| 90 | `public double noPartnerExports, noPartnerImports` |  |
| 91 | `public double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes` |  |
| 96 | `public double[] cargoExportTonnes, cargoImportTonnes` | The month's tonnes by kind of cargo and nation (WN7), flat: Ports.Cargo's ordinal times the nations, plus the nation less one; exports, then imports. |
| 97 | `public List<Cell> yearCells` |  |
| 99 | `public List<GoodUnits> goodUnits` | The month's units by good, every good with trade: what the next band is struck from (GoodsMarket's own tally leaves out the draws made before the clearing). |
| 168 | `private final int n` |  |
| 171 | `private int month` | The month being tallied, or -1 before the first; its figures by nation. |
| 172 | `private final double[] exportValue, importValue, exportTonnes, importTonnes` |  |
| 173 | `private double noPartnerExports, noPartnerImports` |  |
| 176 | `private final double[][] cargoExportTonnes, cargoImportTonnes` | The month's tonnes by kind of cargo (Ports.Cargo's ordinal) and nation, exports and imports (WN7): each good's tonnes by nation, added to its cargo's row. |
| 179 | `private final Map<Good, double[]> monthUnits` | The month's world trade by good, in units: {out, in}. |
| 182 | `private int year` | The calendar year held, or -1; its figures by nation and by (good, nation): {units out, units in, value out, value in}. |
| 183 | `private final double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes` |  |
| 184 | `private final Map<Good, TreeMap<Integer, double[]>> cells` |  |
| 187 | `private final List<Table> history` | The finished years, oldest first. |
| 189 | `private double standInLat` |  |
| 190 | `private String[] names` |  |
| 192 | `private volatile Snapshot published` |  |
| 195 | `private DoubleSupplier moneyUnit` | The currency's unit as it stands now (Denomination.getUnit()): 1 until a reform, then the old money per new. |
| 198 | `private DoubleSupplier priceIndex` | The city's price index as it stands now (PriceIndex.getIndex(): the basket against the basket at founding, 1 until it is based). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 529 | **type** `public final class PartnerTrade` | The city's trade by partner: what it sold to each nation and bought of each this month and so far this year, by good, and every finished year's table (batch WN3; docs/studies/spec-nations.md 6 and 8). |
| 48 | 1 | **type** `public record Cell(Good good, int nation, double unitsOut, double unitsIn, double valueOut, double valueIn)` | A good and a nation, and what the city sold there and bought of it over a span: units, and thousands of the city's money. |
| 51 | 12 | **type** `public record Table(int year, List<Cell> cells)` | A finished year's table, sparse: the cells of the (good, nation) pairs that traded, in good order and then nation order. |
| 52 | 3 | `{ ... }` _(in PartnerTrade.Table)_ |  |
| 57 | 5 | `public Table scaled(double scale)` _(in PartnerTrade.Table)_ | The same year and cells with every cell's money times `scale` (a currency reform, DZ1); the units, the good and the nation as they are. |
| 65 | 1 | **type** `public record GoodUnits(Good good, double unitsOut, double unitsIn)` | A good's trade with the world in a month, in its units: what the city sold abroad and what it bought. |
| 87 | 14 | **type** `public static final class State` | The state a save carries, plain fields (Gson matches them by name): the month last tallied by nation, with the trade that had no partner, and the year to date by nation and by (good, nation). |
| 107 | 60 | **type** `public record Snapshot(int month, int year, double[] exportValue, double[] importValue, double[] exportTonn...` | The month and the year as the screen's thread reads them, and the map's feed over them: every array is this snapshot's own and is never written once it is published, so a reader may keep them and compare them (TradeLa... |
| 118 | 1 | `public double[] exportsByNation()` _(in PartnerTrade.Snapshot)_ | month(), standInLat(), standInLon() and minLane() are the record's own accessors, which are the Feed's (minLane: TP1, the floor of a lane in the money the figures are in, struck when the snapshot is published). |
| 120 | 1 | `public double[] importsByNation()` _(in PartnerTrade.Snapshot)_ |  |
| 122 | 5 | `public TradeLanes.YearToDate yearToDate(int nation)` _(in PartnerTrade.Snapshot)_ |  |
| 128 | 3 | `public String nameOf(int nation)` _(in PartnerTrade.Snapshot)_ |  |
| 133 | 1 | `public int nations()` _(in PartnerTrade.Snapshot)_ | How many nations the figures are of. |
| 139 | 4 | `public double monthExportUnits(Good good)` _(in PartnerTrade.Snapshot)_ | What the city sold the world of a good in the month last tallied, in the good's units (WN6: the Partners page marks where the city stands on a good's curve by it; the live record's monthExportUnits() is the model's th... |
| 145 | 4 | `public double monthImportUnits(Good good)` _(in PartnerTrade.Snapshot)_ | ...and what it bought of the world. |
| 155 | 3 | `public double[] cargoTonnes(Ports.Cargo c, boolean out)` _(in PartnerTrade.Snapshot)_ | The month's tonnes of one kind of cargo sold to each nation (`out`) or bought of each, indexed by the nation less one (WN7: CallPartners draws a ship's or a train's partner by them). |
| 160 | 6 | `public double monthTotal()` _(in PartnerTrade.Snapshot)_ | The month's trade with the nations, both ways together, thousands of the city's money. |
| 201 | 15 | `public PartnerTrade(int nations)` | A record for a world of `nations` nations: nothing tallied, no month. |
| 218 | 1 | `public int nations()` | How many nations. |
| 224 | 3 | `public void setMoneyUnit(DoubleSupplier unit)` | The currency's unit, read live (DZ1): the history's money is rounded to HISTORY_ROUND_MONEY places times it, so the grid is the same real size in the money as it stands after a reform. |
| 228 | 4 | `private double unit()` |  |
| 237 | 4 | `public void setPriceIndex(DoubleSupplier index)` | The city's price index, read live (TP1): with the money's unit it is what the map's lane floor is in real terms (TradeLanes.minLane()), struck into every snapshot published. |
| 243 | 4 | `private double minLaneNow()` | The floor of a lane now, in the city's money as it stands: TradeLanes.minLane() of the price index and the unit. |
| 252 | 6 | `public void setPlace(double standInLat, double standInLon, String[] names)` | Where the city stands, for a generated world's arrows at the edge of its map (TradeLanes.Feed.standInLat()), NaN for a city on the real Earth, and the nations' names (index nation - 1). |

### THE TALLY (lines 259-395)

| line | len | member | says |
|---:|---:|---|---|
| 277 | 13 | `public void tally(int month, Good good, WorldMarket.Allocation allocation, double exportUnits, double exportValue, double impor...` | One good's trade with the world in a month, split to the nations: the exports by the allocation's units to each nation and the imports by its units from each, the value of each side pro rata to the units (so a nation'... |
| 292 | 27 | `private boolean split(Good good, double[] units, double value, double unitsPerTonne, boolean out)` | Spreads one side of a good over the nations by units; false (and nothing kept) where the units add to nothing. |
| 324 | 4 | `public void close(int month)` | The month's tally is done: publishes the snapshot the screens read. |
| 330 | 17 | `private void open(int m)` | Starts a month: clears the month's figures, and turns the year first if the month is the next calendar year's. |
| 353 | 4 | `public double monthExportUnits(Good good)` | What the city sold the world of a good in the month last tallied, in its units: every fill and every draw of the month, the draws made before the clearing too (GoodsMarket's own tally wipes those at the clearing: the ... |
| 359 | 4 | `public double monthImportUnits(Good good)` | ...and what it bought of the world. |
| 365 | 8 | `private void finishYear()` | Sends the year held to the history, rounded, and starts the next empty. |
| 375 | 12 | `private List<Cell> cellList(boolean rounded)` | The year's cells in good order and then nation order, rounded for the history or exact for the state. |
| 388 | 1 | `private static double round(double v)` |  |
| 391 | 4 | `private double roundMoney(double v)` | Rounded to HISTORY_ROUND_MONEY places times the unit: hundredths of a thousand at today's prices, in the city's money as it stands now (DZ1). |

### READS - the snapshot, and the history (lines 396-437)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 1 | `public Snapshot snapshot()` | The snapshot the screens read: the last month closed, whole, immutable. |
| 404 | 1 | `public List<Table> history()` | The finished years, oldest first, as a copy; the model's thread. |
| 407 | 1 | `public int yearsFinished()` | How many years the history holds. |
| 410 | 1 | `public int month()` | The month last tallied, or -1. |
| 413 | 1 | `public int year()` | The calendar year held, or -1. |
| 415 | 9 | `private void publish()` |  |
| 426 | 5 | `private List<GoodUnits> unitsByGood()` | The month's units by good as the snapshot keeps them, in good order. |
| 432 | 5 | `private static double[][] copy(double[][] a)` |  |

### THE SAVE (lines 438-573)

| line | len | member | says |
|---:|---:|---|---|
| 443 | 21 | `public State toState()` | What a save keeps, fresh arrays and a fresh list: nothing here is shared with the model. |
| 465 | 6 | `private static double[] flat(double[][] a)` |  |
| 473 | 8 | `private void unflat(double[] flat, double[][] rows)` | The flat table back into its rows; zeros where the save has none (an older save: Gson leaves the key null), or has it the wrong length or not a number. |
| 486 | 38 | `public boolean restore(State s)` | Puts a saved month and year back and publishes them. |
| 526 | 12 | `public void restoreHistory(List<Table> years)` | Puts the finished years back (HistorySave) and publishes. |
| 548 | 19 | `public void redenominate(double scale)` | A currency reform (DZ1): every money figure of the record times `scale` - the month's and the year's by nation, the trade with no partner, each cell's value out and in, and every finished year's table - then publishes. |
| 568 | 5 | `private boolean sized(double[] a)` |  |

