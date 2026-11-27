package ham.citybuildersim;

/**
 * The city's wires: the district Substation (100 MW) and the small distribution substation (5 MW), and their own costs in the power bill.
 * WHY. PW1 charged a flat 5.94 cents a kWh for wires that nothing built or maintained.
 * PW5 (spec-electricity 2.7) uses real city buildings, paid through the existing construction,
 * repair and utility payroll books; the tariff recovers those costs for their owner, the treasury.
 * ASH1: the only step past the free 10 MW was a 100 MW unit at 153% of a 6,166-person town's GDP,
 * so a city distribution substation of a few MW (Portland and the Midwest, 1924) lets the wires grow with the town.
 */
public final class Substations {
    private Substations() { }

    /** Permanent catalogue id, appended after Hydrogen Steel Works (PW5). */
    public static final int ID = 155;
    /** MW carried by one district's lines, transformers and meters (spec 2.7's recommended size). */
    public static final double SUBSTATION_MW = 100;
    /** Permanent catalogue id of the distribution substation, appended after the Small Steam Station (ASH1). */
    public static final int DISTRIBUTION_ID = 173;
    /** MW carried by one city distribution substation, as the 1924 Portland (3,000 kW, 6,000 planned) and Midwest (3 x 1,000 kVA) stations. */
    public static final double DISTRIBUTION_MW = 5;
    /** Real annual cost of capital in the study's grid calculation (spec 2.7). */
    public static final double REAL_RATE = .04;
    /** Years of a district grid's capital life (spec 2.7). */
    public static final int LIFE_YEARS = 40;
    /** Transmission's share of the grid's costs, AEO2026 table 8, as in PW1's two billing classes. */
    public static final double TRANSMISSION_SHARE = PowerDispatch.TRANSMISSION_A_KWH
            / (PowerDispatch.TRANSMISSION_A_KWH + PowerDispatch.DISTRIBUTION_A_KWH);

    /** A grid unit, the district Substation or a distribution substation. */
    public static boolean isSubstation(BuildingsTemplate t) { return t != null && (t.getId() == ID || t.getId() == DISTRIBUTION_ID); }

    /** The kW one unit of this template carries; 0 for anything that is not a grid unit. */
    public static double unitKw(BuildingsTemplate t) {
        if (t == null) return 0;
        if (t.getId() == ID) return SUBSTATION_MW * 1000;
        return t.getId() == DISTRIBUTION_ID ? DISTRIBUTION_MW * 1000 : 0;
    }

    /**
     * The kW the wires carry. The district Substation replaces the founding network as PW5 had it;
     * each distribution substation extends whatever network stands (ASH1).
     */
    public static double carryKw(double foundingKw, long substations, long distribution) {
        return Math.max(foundingKw, substations * SUBSTATION_MW * 1000) + distribution * DISTRIBUTION_MW * 1000;
    }

    /** One dispatch's monthly costs, in city money; capital is recovery, never a second cash payment. */
    public record Costs(long count, double carryKw, double capital, double repairs, double payroll) {
        public double total() { return capital + repairs + payroll; }
        Costs scaled(double scale) { return new Costs(count, carryKw, capital * scale, repairs * scale, payroll * scale); }
    }

    /** Reads the same replacement cost, builders' tax, repairs rate and filled payroll as the city's books. */
    public static Costs costs(Game game) {
        BuildingManager bm = game.getBuildingManager();
        BuildingsTemplate[] units = {bm.getTemplate(ID), bm.getTemplate(DISTRIBUTION_ID)};
        UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
        long[] n = new long[units.length];
        double value = 0, repairs = 0, payroll = 0;
        double annuity = REAL_RATE / (1 - Math.pow(1 + REAL_RATE, -LIFE_YEARS));
        double[] wages = game.getPopulationManager().getWagesPerType(), fill = game.getPopulationManager().getJobFillRate();
        for (int i = 0; i < units.length; i++) {
            BuildingsTemplate t = units[i];
            if (t == null) continue;
            n[i] = bm.getQuantity(t.getId());
            double unitValue = n[i] * game.getEconomyManager().withBuildersTax(bm.structureCost(t));
            value += unitValue;
            repairs += unitValue * t.repairsPerYear() / 12;
            for (JobType j : JobType.values()) payroll += n[i] * t.getJobs(j) * wages[j.ordinal()] * fill[j.ordinal()];
        }
        // The founding grid's existing 10 MW includes its wires. The district Substations replace that free starter network.
        double carry = carryKw(u.getFoundingGridKw(), n[0], n[1]);
        return new Costs(n[0] + n[1], carry, value * annuity / 12, repairs, payroll);
    }

    /** Peak kW asked at the plants, including the season and the existing grid losses. */
    public static double peak(double drawKw, double season) { return drawKw * season * PowerDispatch.PLAN_PEAK; }
}
