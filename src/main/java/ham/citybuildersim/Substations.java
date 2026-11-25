package ham.citybuildersim;

/**
 * The city's wires: one substation for each 100 MW of peak, and their own costs in the power bill.
 * WHY. PW1 charged a flat 5.94 cents a kWh for wires that nothing built or maintained.
 * PW5 (spec-electricity 2.7) uses real city buildings, paid through the existing construction,
 * repair and utility payroll books; the tariff recovers those costs for their owner, the treasury.
 */
public final class Substations {
    private Substations() { }

    /** Permanent catalogue id, appended after Hydrogen Steel Works (PW5). */
    public static final int ID = 155;
    /** MW carried by one district's lines, transformers and meters (spec 2.7's recommended size). */
    public static final double SUBSTATION_MW = 100;
    /** Real annual cost of capital in the study's grid calculation (spec 2.7). */
    public static final double REAL_RATE = .04;
    /** Years of a district grid's capital life (spec 2.7). */
    public static final int LIFE_YEARS = 40;
    /** Transmission's share of the grid's costs, AEO2026 table 8, as in PW1's two billing classes. */
    public static final double TRANSMISSION_SHARE = PowerDispatch.TRANSMISSION_A_KWH
            / (PowerDispatch.TRANSMISSION_A_KWH + PowerDispatch.DISTRIBUTION_A_KWH);

    public static boolean isSubstation(BuildingsTemplate t) { return t != null && t.getId() == ID; }

    /** One dispatch's monthly costs, in city money; capital is recovery, never a second cash payment. */
    public record Costs(long count, double carryKw, double capital, double repairs, double payroll) {
        public double total() { return capital + repairs + payroll; }
        Costs scaled(double scale) { return new Costs(count, carryKw, capital * scale, repairs * scale, payroll * scale); }
    }

    /** Reads the same replacement cost, builders' tax, repairs rate and filled payroll as the city's books. */
    public static Costs costs(Game game) {
        BuildingManager bm = game.getBuildingManager();
        BuildingsTemplate t = bm.getTemplateByName("Substation");
        UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
        long n = t == null ? 0 : bm.getQuantity(ID);
        double value = t == null ? 0 : n * game.getEconomyManager().withBuildersTax(bm.structureCost(t));
        double annuity = REAL_RATE / (1 - Math.pow(1 + REAL_RATE, -LIFE_YEARS));
        // The founding grid's existing 10 MW includes its wires. Substations replace that free starter network.
        double carry = Math.max(u.getFoundingGridKw(), n * SUBSTATION_MW * 1000);
        double payroll = 0;
        double[] wages = game.getPopulationManager().getWagesPerType(), fill = game.getPopulationManager().getJobFillRate();
        if (t != null) for (JobType j : JobType.values()) payroll += n * t.getJobs(j) * wages[j.ordinal()] * fill[j.ordinal()];
        return new Costs(n, carry, value * annuity / 12,
                t == null ? 0 : value * t.repairsPerYear() / 12, payroll);
    }

    /** Peak kW asked at the plants, including the season and the existing grid losses. */
    public static double peak(double drawKw, double season) { return drawKw * season * PowerDispatch.PLAN_PEAK; }
}
