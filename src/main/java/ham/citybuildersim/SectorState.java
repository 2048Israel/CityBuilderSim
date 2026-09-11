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

    public Map<String, Double> stock = new LinkedHashMap<>();
    public Map<String, Double> pantry = new LinkedHashMap<>();
    public Map<String, Double> pantryUsed = new LinkedHashMap<>();

    public LedgerState ledger;
    public StatementState statement;

    /** A sector's own state - a price it walks, an order book - by name. */
    public Map<String, Double> extras = new LinkedHashMap<>();

    /** The month in progress, unstruck. See Sector.Ledger. */
    public static final class LedgerState {
        public double localSales, exports, otherRevenue, imports, salesToHouseholds;
        public Map<String, Double> purchasesBySupplier = new LinkedHashMap<>();
        public Map<String, Double> unitsSold = new LinkedHashMap<>();
        public Map<String, Double> unitsBought = new LinkedHashMap<>();

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
            return l;
        }
    }

    /** The month last struck. See Sector.Statement. */
    public static final class StatementState {
        public double revenue, inputs, payroll, electricity, water, maintenance;
        public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome;
        public double localSales, exports, otherRevenue, salesToHouseholds, localPurchases, imports;
        public Map<String, Double> purchasesBySupplier = new LinkedHashMap<>();

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
            return t;
        }
    }
}
