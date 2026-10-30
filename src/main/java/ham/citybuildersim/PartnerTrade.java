package ham.citybuildersim;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The city's trade by partner: what it sold to each nation and bought of each this month and so far this year, by good, and every finished year's
 * table (batch WN3; docs/studies/spec-nations.md 6 and 8).
 *
 * <p>WHY THIS EXISTS. Until the world was a market the city traded with "the world": GoodsMarket recorded a fill with the world as its other side,
 * and no screen could say who the world was. The market (WorldMarket) now says who took the city's exports and who sold its imports
 * (allocate(), by the same fills the band is made of); the map draws the month's trade as lanes (TradeLanes), a nation's card says what it took and
 * sold this year, and City History reads the years behind. All of that is this one record, kept at the end of each month by Game (THE WORLD MARKET,
 * tallyThePartners()) from the month's own fills and draws: the values are what the trades came to (units times the price each was struck at, in the
 * city's money, thousands), split to the nations pro rata to the units the market allocated, so a nation's share of a good's value is its share of
 * the units and the nations add up to the month's trade, to the rounding. Trade with no partner - a good the world file does not model, or one with
 * no buyers or no sellers at the city's site - is kept apart (noPartnerExports(), noPartnerImports()) so the total still reconciles.
 *
 * <p>THE BAND IS STRUCK FROM IT TOO. The month's units by good - every fill and every draw with the world on the other side (monthExportUnits(),
 * monthImportUnits()) - are what the next month's band is made of (Markets.setWorldBand()). GoodsMarket's own tally would not do: the clearing wipes
 * it of the draws made before it, and the city's fuel for its fleets and its builders' materials are all draws (a playtest city's petrol and diesel
 * imports are millions of units a month in the trades and none in the tally).
 *
 * <p>THE YEAR TURNS HERE. The first tally of a calendar year (Nations.yearOf(month)) sends the finished year's table to the history (history(),
 * rounded to cents of the city's money and hundredths of a unit, as every series kept for ever is) and starts the year empty. The history is saved in
 * the HistorySave, a list a year; the month's and the year to date in the DataSave (State), because a flow cannot be reconstructed from the state a
 * month ended in: the lanes a loaded city draws are last month's trade.
 *
 * <p>TWO THREADS. The model's thread writes, and publishes an immutable Snapshot through a volatile at the end of every tally (close()); the
 * screen's thread reads the snapshot and never sees a half-tallied month. Everything else here is the model thread's alone.
 */
public final class PartnerTrade {

    /** A good and a nation, and what the city sold there and bought of it over a span: units, and thousands of the city's money. Immutable. */
    public record Cell(Good good, int nation, double unitsOut, double unitsIn, double valueOut, double valueIn) { }

    /** A finished year's table, sparse: the cells of the (good, nation) pairs that traded, in good order and then nation order. Immutable. */
    public record Table(int year, List<Cell> cells) {
        public Table {
            cells = cells == null ? List.of() : List.copyOf(cells);
        }
    }

    /** A good's trade with the world in a month, in its units: what the city sold abroad and what it bought. Immutable. */
    public record GoodUnits(Good good, double unitsOut, double unitsIn) { }

    /** The places a table is rounded to when it goes to the history: hundredths of a unit and of a thousand. */
    static final double HISTORY_ROUND = 100;

    /**
     * The state a save carries, plain fields (Gson matches them by name): the month last tallied by nation, with the trade that had no partner, and the
     * year to date by nation and by (good, nation). Nations are indexed from 0 (nation - 1), as the feed's are.
     */
    public static final class State {
        public int month = -1, year = -1;
        public double[] exportValue, importValue, exportTonnes, importTonnes;
        public double noPartnerExports, noPartnerImports;
        public double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes;
        public List<Cell> yearCells;
        /** The month's units by good, every good with trade: what the next band is struck from (GoodsMarket's own tally leaves out the draws made before the clearing). */
        public List<GoodUnits> goodUnits;
    }

    /**
     * The month and the year as the screen's thread reads them, and the map's feed over them: every array is this snapshot's own and is never
     * written once it is published, so a reader may keep them and compare them (TradeLanes does, every frame). Money is the city's, thousands.
     * Nations are numbered from 1 and the arrays indexed by the nation less one.
     */
    public record Snapshot(int month, int year,
                           double[] exportValue, double[] importValue, double[] exportTonnes, double[] importTonnes,
                           double noPartnerExports, double noPartnerImports,
                           double[] yearExportValue, double[] yearImportValue, double[] yearExportTonnes, double[] yearImportTonnes,
                           List<Cell> yearCells, List<Table> history,
                           double standInLat, double standInLon, String[] names) implements TradeLanes.Feed {

        /* month(), standInLat() and standInLon() are the record's own accessors, which are the Feed's. */

        @Override public double[] exportsByNation() { return exportValue; }

        @Override public double[] importsByNation() { return importValue; }

        @Override public TradeLanes.YearToDate yearToDate(int nation) {
            int i = nation - 1;
            if (i < 0 || i >= yearExportValue.length) return null;
            return new TradeLanes.YearToDate(yearExportValue[i], yearImportValue[i], yearExportTonnes[i], yearImportTonnes[i]);
        }

        @Override public String nameOf(int nation) {
            return names == null || nation < 1 || nation > names.length ? null : names[nation - 1];
        }

        /** How many nations the figures are of. */
        public int nations() { return exportValue.length; }

        /** The month's trade with the nations, both ways together, thousands of the city's money. */
        public double monthTotal() {
            double s = 0;
            for (double v : exportValue) s += v;
            for (double v : importValue) s += v;
            return s;
        }
    }

    private final int n;

    /** The month being tallied, or -1 before the first; its figures by nation. */
    private int month = -1;
    private final double[] exportValue, importValue, exportTonnes, importTonnes;
    private double noPartnerExports, noPartnerImports;

    /** The month's world trade by good, in units: {out, in}. Only goods with trade. The model's thread's, for the band. */
    private final Map<Good, double[]> monthUnits = new EnumMap<>(Good.class);

    /** The calendar year held, or -1; its figures by nation and by (good, nation): {units out, units in, value out, value in}. */
    private int year = -1;
    private final double[] yearExportValue, yearImportValue, yearExportTonnes, yearImportTonnes;
    private final Map<Good, TreeMap<Integer, double[]>> cells = new EnumMap<>(Good.class);

    /** The finished years, oldest first. */
    private final List<Table> history = new ArrayList<>();

    private double standInLat = Double.NaN, standInLon = Double.NaN;
    private String[] names;

    private volatile Snapshot published;

    /** A record for a world of `nations` nations: nothing tallied, no month. */
    public PartnerTrade(int nations) {
        if (nations < 1) throw new IllegalArgumentException("partner trade: " + nations + " nations");
        this.n = nations;
        exportValue = new double[n];
        importValue = new double[n];
        exportTonnes = new double[n];
        importTonnes = new double[n];
        yearExportValue = new double[n];
        yearImportValue = new double[n];
        yearExportTonnes = new double[n];
        yearImportTonnes = new double[n];
        publish();
    }

    /** How many nations. */
    public int nations() { return n; }

    /**
     * Where the city stands, for a generated world's arrows at the edge of its map (TradeLanes.Feed.standInLat()), NaN for a city on the real Earth,
     * and the nations' names (index nation - 1). Set once the market is built; published with the next snapshot.
     */
    public void setPlace(double standInLat, double standInLon, String[] names) {
        this.standInLat = standInLat;
        this.standInLon = standInLon;
        this.names = names == null ? null : names.clone();
        publish();
    }

    /* =====================================================================
       THE TALLY
       ===================================================================== */

    /**
     * One good's trade with the world in a month, split to the nations: the exports by the allocation's units to each nation and the imports by its
     * units from each, the value of each side pro rata to the units (so a nation's share of the value is its share of the units), the tonnes the units
     * over `unitsPerTonne`. An allocation that gives no units to a side (null, or a good the market has no buyers or sellers of) leaves that side's
     * value with no partner. A month earlier than the one held starts a new one; the first tally of a calendar year turns the year.
     *
     * @param month         the game's month the trade is of
     * @param allocation    who took the exports and who sold the imports (WorldMarket.allocate()), or null for a good the market does not model
     * @param exportUnits   the month's exports of the good, in its units: the sum the allocation was made of, and what monthExportUnits() says next month
     * @param exportValue   ...and what they came to, thousands of the city's money
     * @param importUnits   the month's imports of the good, in its units
     * @param importValue   ...and what they came to
     * @param unitsPerTonne the good's units to the tonne, for the tonnes beside the money; 0 for none
     */
    public void tally(int month, Good good, WorldMarket.Allocation allocation, double exportUnits, double exportValue, double importUnits, double importValue,
                      double unitsPerTonne) {
        open(month);
        if (exportUnits > 0 || importUnits > 0) {
            double[] u = monthUnits.computeIfAbsent(good, g -> new double[2]);
            u[0] += Math.max(0, exportUnits);
            u[1] += Math.max(0, importUnits);
        }
        double[] to = allocation == null ? null : allocation.exportUnitsTo();
        double[] from = allocation == null ? null : allocation.importUnitsFrom();
        if (!split(good, to, exportValue, unitsPerTonne, true)) noPartnerExports += Math.max(0, exportValue);
        if (!split(good, from, importValue, unitsPerTonne, false)) noPartnerImports += Math.max(0, importValue);
    }

    /** Spreads one side of a good over the nations by units; false (and nothing kept) where the units add to nothing. */
    private boolean split(Good good, double[] units, double value, double unitsPerTonne, boolean out) {
        if (units == null || units.length != n) return false;
        double sum = 0;
        for (double u : units) if (u > 0) sum += u;
        if (!(sum > 0)) return false;
        double v = Math.max(0, value);
        double[] monthValue = out ? exportValue : importValue, monthTonnes = out ? exportTonnes : importTonnes;
        double[] yearValue = out ? yearExportValue : yearImportValue, yearTonnes = out ? yearExportTonnes : yearImportTonnes;
        TreeMap<Integer, double[]> byNation = cells.computeIfAbsent(good, g -> new TreeMap<>());
        for (int i = 0; i < n; i++) {
            double u = units[i];
            if (!(u > 0)) continue;
            double share = v * u / sum;
            double tonnes = unitsPerTonne > 0 ? u / unitsPerTonne : 0;
            monthValue[i] += share;
            monthTonnes[i] += tonnes;
            yearValue[i] += share;
            yearTonnes[i] += tonnes;
            double[] cell = byNation.computeIfAbsent(i + 1, k -> new double[4]);
            cell[out ? 0 : 1] += u;
            cell[out ? 2 : 3] += share;
        }
        return true;
    }

    /**
     * The month's tally is done: publishes the snapshot the screens read. A month with no trade at all is a month all the same (the year turns in it
     * and the figures are zero).
     */
    public void close(int month) {
        open(month);
        publish();
    }

    /** Starts a month: clears the month's figures, and turns the year first if the month is the next calendar year's. */
    private void open(int m) {
        if (m == month) return;
        int y = Nations.yearOf(m);
        if (y != year) {
            if (year >= 0) finishYear();
            year = y;
        }
        month = m;
        java.util.Arrays.fill(exportValue, 0);
        java.util.Arrays.fill(importValue, 0);
        java.util.Arrays.fill(exportTonnes, 0);
        java.util.Arrays.fill(importTonnes, 0);
        noPartnerExports = noPartnerImports = 0;
        monthUnits.clear();
    }

    /**
     * What the city sold the world of a good in the month last tallied, in its units: every fill and every draw of the month, the draws made before the
     * clearing too (GoodsMarket's own tally wipes those at the clearing: the fleets' fuel and the builders' materials are all draws). The band of the month
     * after is struck from this (Game, THE WORLD MARKET). 0 for a good with no trade, and before the first month.
     */
    public double monthExportUnits(Good good) {
        double[] u = monthUnits.get(good);
        return u == null ? 0 : u[0];
    }

    /** ...and what it bought of the world. */
    public double monthImportUnits(Good good) {
        double[] u = monthUnits.get(good);
        return u == null ? 0 : u[1];
    }

    /** Sends the year held to the history, rounded, and starts the next empty. */
    private void finishYear() {
        history.add(new Table(year, cellList(true)));
        cells.clear();
        java.util.Arrays.fill(yearExportValue, 0);
        java.util.Arrays.fill(yearImportValue, 0);
        java.util.Arrays.fill(yearExportTonnes, 0);
        java.util.Arrays.fill(yearImportTonnes, 0);
    }

    /** The year's cells in good order and then nation order, rounded for the history or exact for the state. */
    private List<Cell> cellList(boolean rounded) {
        List<Cell> out = new ArrayList<>();
        for (Map.Entry<Good, TreeMap<Integer, double[]>> g : cells.entrySet()) {
            for (Map.Entry<Integer, double[]> e : g.getValue().entrySet()) {
                double[] c = e.getValue();
                out.add(rounded
                        ? new Cell(g.getKey(), e.getKey(), round(c[0]), round(c[1]), round(c[2]), round(c[3]))
                        : new Cell(g.getKey(), e.getKey(), c[0], c[1], c[2], c[3]));
            }
        }
        return out;
    }

    private static double round(double v) { return Math.round(v * HISTORY_ROUND) / HISTORY_ROUND; }

    /* =====================================================================
       READS - the snapshot, and the history
       ===================================================================== */

    /** The snapshot the screens read: the last month closed, whole, immutable. Safe from any thread. */
    public Snapshot snapshot() { return published; }

    /** The finished years, oldest first, as a copy; the model's thread. */
    public List<Table> history() { return List.copyOf(history); }

    /** How many years the history holds. */
    public int yearsFinished() { return history.size(); }

    /** The month last tallied, or -1. */
    public int month() { return month; }

    /** The calendar year held, or -1. */
    public int year() { return year; }

    private void publish() {
        published = new Snapshot(month, year,
                exportValue.clone(), importValue.clone(), exportTonnes.clone(), importTonnes.clone(),
                noPartnerExports, noPartnerImports,
                yearExportValue.clone(), yearImportValue.clone(), yearExportTonnes.clone(), yearImportTonnes.clone(),
                List.copyOf(cellList(false)), List.copyOf(history),
                standInLat, standInLon, names);
    }

    /* =====================================================================
       THE SAVE
       ===================================================================== */

    /** What a save keeps, fresh arrays and a fresh list: nothing here is shared with the model. */
    public State toState() {
        State s = new State();
        s.month = month;
        s.year = year;
        s.exportValue = exportValue.clone();
        s.importValue = importValue.clone();
        s.exportTonnes = exportTonnes.clone();
        s.importTonnes = importTonnes.clone();
        s.noPartnerExports = noPartnerExports;
        s.noPartnerImports = noPartnerImports;
        s.yearExportValue = yearExportValue.clone();
        s.yearImportValue = yearImportValue.clone();
        s.yearExportTonnes = yearExportTonnes.clone();
        s.yearImportTonnes = yearImportTonnes.clone();
        s.yearCells = cellList(false);
        s.goodUnits = new ArrayList<>();
        for (Map.Entry<Good, double[]> e : monthUnits.entrySet()) s.goodUnits.add(new GoodUnits(e.getKey(), e.getValue()[0], e.getValue()[1]));
        return s;
    }

    /**
     * Puts a saved month and year back and publishes them. False, and nothing changed, where the state is not this world's (another file's nations)
     * or holds a figure that is not a number; the record then starts as a new city's.
     */
    public boolean restore(State s) {
        if (s == null || !sized(s.exportValue) || !sized(s.importValue) || !sized(s.exportTonnes) || !sized(s.importTonnes)
                || !sized(s.yearExportValue) || !sized(s.yearImportValue) || !sized(s.yearExportTonnes) || !sized(s.yearImportTonnes)
                || !(s.noPartnerExports >= 0) || !(s.noPartnerImports >= 0) || !Double.isFinite(s.noPartnerExports) || !Double.isFinite(s.noPartnerImports)) {
            return false;
        }
        month = s.month;
        year = s.year;
        System.arraycopy(s.exportValue, 0, exportValue, 0, n);
        System.arraycopy(s.importValue, 0, importValue, 0, n);
        System.arraycopy(s.exportTonnes, 0, exportTonnes, 0, n);
        System.arraycopy(s.importTonnes, 0, importTonnes, 0, n);
        noPartnerExports = s.noPartnerExports;
        noPartnerImports = s.noPartnerImports;
        System.arraycopy(s.yearExportValue, 0, yearExportValue, 0, n);
        System.arraycopy(s.yearImportValue, 0, yearImportValue, 0, n);
        System.arraycopy(s.yearExportTonnes, 0, yearExportTonnes, 0, n);
        System.arraycopy(s.yearImportTonnes, 0, yearImportTonnes, 0, n);
        cells.clear();
        if (s.yearCells != null) {
            for (Cell c : s.yearCells) {
                if (c == null || c.good() == null || c.nation() < 1 || c.nation() > n) continue;
                cells.computeIfAbsent(c.good(), g -> new TreeMap<>())
                        .put(c.nation(), new double[] { c.unitsOut(), c.unitsIn(), c.valueOut(), c.valueIn() });
            }
        }
        monthUnits.clear();
        if (s.goodUnits != null) {
            for (GoodUnits u : s.goodUnits) {
                if (u == null || u.good() == null || !(u.unitsOut() >= 0) || !(u.unitsIn() >= 0) || !Double.isFinite(u.unitsOut()) || !Double.isFinite(u.unitsIn())) continue;
                monthUnits.put(u.good(), new double[] { u.unitsOut(), u.unitsIn() });
            }
        }
        publish();
        return true;
    }

    /** Puts the finished years back (HistorySave) and publishes. Cells of a good this build does not know, or a nation this world lacks, are dropped. */
    public void restoreHistory(List<Table> years) {
        history.clear();
        if (years != null) {
            for (Table t : years) {
                if (t == null) continue;
                List<Cell> kept = new ArrayList<>();
                for (Cell c : t.cells()) if (c != null && c.good() != null && c.nation() >= 1 && c.nation() <= n) kept.add(c);
                history.add(new Table(t.year(), kept));
            }
        }
        publish();
    }

    private boolean sized(double[] a) {
        if (a == null || a.length != n) return false;
        for (double v : a) if (!Double.isFinite(v)) return false;
        return true;
    }
}
