package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One sector, as a save carries it.
 *
 * KEYED BY NAME, EVERYWHERE. The sector by its key, the stocks by the good's
 * name, the purchases by the supplier's key, the extras by whatever the
 * sector called them. Gson matches by name, so a save from a build with a
 * sector or a good this one does not have loses that line and not the load,
 * and a build that adds a field reads zero from an older save - which is
 * what the older city had.
 *
 * This replaced five differently-shaped report arrays, three arrays indexed
 * by BuildingType.ordinal(), and a dozen loose fields (commercialCash,
 * industryFoodInventory, retailFillBasis...) that DataSave carried one by
 * one. Jerus: "clean break" - the save format moved, and nothing here reads
 * the old shape.
 */
public final class SectorState {

    public String key;
    public double cash;

    /** The three bills of the month and the rate it was taxed at, as set at the top of it. A flow, carried. */
    public double interest, propertyTax, maintenance, taxRate;

    /**
     * Whether this sector's van fleet is a fact about the sector.
     *
     * Absent - and so false - in every save written before 2026-09-17, which
     * is exactly what the reader needs to know: that city was moving steel
     * with lorries the game had not invented yet, and Sector.runFleet() gives
     * it the fleet its plant implies rather than stopping it dead. See
     * Sector.vansKnown.
     */
    public boolean vansKnown;

    public Map<String, Double> stock = new LinkedHashMap<>();
    public Map<String, Double> pantry = new LinkedHashMap<>();
    public Map<String, Double> pantryUsed = new LinkedHashMap<>();

    public LedgerState ledger;
    public StatementState statement;

    /**
     * A good's money in a save: what it sold or bought at home and abroad.
     *
     * A NEW FIELD IN AN OLD SAVE READS ZERO, which is what this class's header
     * promises and what an older city actually had - it kept no per-good money
     * at all. So the income statement's breakdown is blank for the one month a
     * pre-2026-09-16 save was taken in and correct from the next month on, and
     * SAVE_FORMAT did not have to move for it. See UserInterface.incomePage().
     */
    public static final class SplitState {
        public double atHome, abroad;
    }

    static Map<String, SplitState> splitsOf(Map<Good, Sector.Split> from) {
        Map<String, SplitState> out = new LinkedHashMap<>();
        if (from != null) for (Map.Entry<Good, Sector.Split> e : from.entrySet()) {
            SplitState s = new SplitState();
            s.atHome = e.getValue().atHome;
            s.abroad = e.getValue().abroad;
            out.put(e.getKey().name(), s);
        }
        return out;
    }

    static Map<Good, Sector.Split> splitsTo(Map<String, SplitState> from) {
        Map<Good, Sector.Split> out = new java.util.EnumMap<>(Good.class);
        if (from != null) for (Map.Entry<String, SplitState> e : from.entrySet()) {
            Good g = Good.byName(e.getKey());
            if (g == null || e.getValue() == null) continue;
            Sector.Split x = new Sector.Split();
            x.atHome = e.getValue().atHome;
            x.abroad = e.getValue().abroad;
            out.put(g, x);
        }
        return out;
    }

    /** A sector's own state - a price it walks, an order book - by name. */
    public Map<String, Double> extras = new LinkedHashMap<>();

    /** The month in progress, unstruck. See Sector.Ledger. */
    public static final class LedgerState {
        public double localSales, exports, otherRevenue, imports, salesToHouseholds;
        public Map<String, Double> purchasesBySupplier = new LinkedHashMap<>();
        public Map<String, Double> unitsSold = new LinkedHashMap<>();
        public Map<String, Double> unitsBought = new LinkedHashMap<>();
        public Map<String, SplitState> sold = new LinkedHashMap<>();
        public Map<String, SplitState> bought = new LinkedHashMap<>();
        /** The named non-goods part of the month's purchases. See Sector.Ledger.otherInputs. */
        public Map<String, Double> otherInputs = new LinkedHashMap<>();

        static LedgerState of(Sector.Ledger l) {
            LedgerState s = new LedgerState();
            s.localSales = l.localSales;
            s.exports = l.exports;
            s.otherRevenue = l.otherRevenue;
            s.imports = l.imports;
            s.salesToHouseholds = l.salesToHouseholds;
            s.purchasesBySupplier = new LinkedHashMap<>(l.purchasesBySupplier);
            for (Map.Entry<Good, Double> e : l.unitsSold.entrySet())   s.unitsSold.put(e.getKey().name(), e.getValue());
            for (Map.Entry<Good, Double> e : l.unitsBought.entrySet()) s.unitsBought.put(e.getKey().name(), e.getValue());
            s.sold = splitsOf(l.sold);
            s.bought = splitsOf(l.bought);
            s.otherInputs = new LinkedHashMap<>(l.otherInputs);
            return s;
        }

        Sector.Ledger toLedger() {
            Sector.Ledger l = new Sector.Ledger();
            l.localSales = localSales;
            l.exports = exports;
            l.otherRevenue = otherRevenue;
            l.imports = imports;
            l.salesToHouseholds = salesToHouseholds;
            if (purchasesBySupplier != null) l.purchasesBySupplier.putAll(purchasesBySupplier);
            if (unitsSold != null) for (Map.Entry<String, Double> e : unitsSold.entrySet()) {
                Good g = Good.byName(e.getKey());
                if (g != null && e.getValue() != null) l.unitsSold.put(g, e.getValue());
            }
            if (unitsBought != null) for (Map.Entry<String, Double> e : unitsBought.entrySet()) {
                Good g = Good.byName(e.getKey());
                if (g != null && e.getValue() != null) l.unitsBought.put(g, e.getValue());
            }
            l.sold.putAll(splitsTo(sold));
            l.bought.putAll(splitsTo(bought));
            if (otherInputs != null) l.otherInputs.putAll(otherInputs);
            return l;
        }
    }

    /** The month last struck. See Sector.Statement. */
    public static final class StatementState {
        public double revenue, inputs, payroll, electricity, water, maintenance;
        public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome;
        public double localSales, exports, otherRevenue, salesToHouseholds, localPurchases, imports;
        public Map<String, Double> purchasesBySupplier = new LinkedHashMap<>();
        public Map<String, SplitState> sold = new LinkedHashMap<>();
        public Map<String, SplitState> bought = new LinkedHashMap<>();
        public Map<String, Double> otherParts = new LinkedHashMap<>();
        /** The named non-goods part of the input line. Absent in a save from before rail: empty is right. */
        public Map<String, Double> otherInputs = new LinkedHashMap<>();

        static StatementState of(Sector.Statement t) {
            StatementState s = new StatementState();
            s.revenue = t.revenue; s.inputs = t.inputs; s.payroll = t.payroll;
            s.electricity = t.electricity; s.water = t.water; s.maintenance = t.maintenance;
            s.operatingIncome = t.operatingIncome; s.interest = t.interest; s.propertyTax = t.propertyTax;
            s.salesTax = t.salesTax; s.preTaxIncome = t.preTaxIncome; s.profitTax = t.profitTax;
            s.netIncome = t.netIncome;
            s.localSales = t.localSales; s.exports = t.exports; s.otherRevenue = t.otherRevenue;
            s.salesToHouseholds = t.salesToHouseholds; s.localPurchases = t.localPurchases; s.imports = t.imports;
            s.purchasesBySupplier = new LinkedHashMap<>(t.purchasesBySupplier);
            s.sold = splitsOf(t.sold);
            s.bought = splitsOf(t.bought);
            s.otherParts = new LinkedHashMap<>(t.otherParts);
            s.otherInputs = new LinkedHashMap<>(t.otherInputs);
            return s;
        }

        Sector.Statement toStatement() {
            Sector.Statement t = new Sector.Statement();
            t.revenue = revenue; t.inputs = inputs; t.payroll = payroll;
            t.electricity = electricity; t.water = water; t.maintenance = maintenance;
            t.operatingIncome = operatingIncome; t.interest = interest; t.propertyTax = propertyTax;
            t.salesTax = salesTax; t.preTaxIncome = preTaxIncome; t.profitTax = profitTax;
            t.netIncome = netIncome;
            t.localSales = localSales; t.exports = exports; t.otherRevenue = otherRevenue;
            t.salesToHouseholds = salesToHouseholds; t.localPurchases = localPurchases; t.imports = imports;
            t.purchasesBySupplier = purchasesBySupplier == null
                    ? new LinkedHashMap<>() : new LinkedHashMap<>(purchasesBySupplier);
            t.sold = splitsTo(sold);
            t.bought = splitsTo(bought);
            t.otherParts = otherParts == null ? new LinkedHashMap<>() : new LinkedHashMap<>(otherParts);
            t.otherInputs = otherInputs == null ? new LinkedHashMap<>() : new LinkedHashMap<>(otherInputs);
            return t;
        }
    }
}
