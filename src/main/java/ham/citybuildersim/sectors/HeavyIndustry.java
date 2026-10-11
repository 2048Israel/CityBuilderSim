package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Good;

/**
 * The mills - Iron & Steel since 0.7.116, under the key it was saved by,
 * "Heavy Industry": the arc furnaces, which melt scrap into steel and sell it
 * to the city's fabricators or abroad, built by what pays.
 *
 * WHY (0.7.116, batch I3; runs/spec-materials.md 2.1 and 2.3, and Jerus's
 * Q2, Q4 and Q6 of 2026-10-08). Until then a mill bought IRON - "iron ore,
 * and the scrap that stands in for it" - local ore first and imported scrap
 * for the rest, sold steel at the US band (1.284 / .847), and was built only
 * into spare local ore: "no spare local ore to smelt" was its plan's gate,
 * and an order the sector could not make a case for was the borrowing
 * spiral the investment engine was written to end. Three of Jerus's answers
 * took that apart. Scrap splits from ore (Q4): the furnaces take SCRAP, and
 * IRON is ore alone, which nothing here uses until the integrated route
 * (batch I4). Steel goes to the world's price (Q6: .650 / .470), at which a
 * mill on imported scrap loses money exporting and earns a little replacing
 * imports. And the sector is renamed Iron & Steel (Q2), its saved key kept.
 *
 * SO IT IS A ProcessingSector (I2's base), and its plan is SpreadPlanner's
 * like every works of the materials chains (the study's 1.2): each furnace a
 * candidate earning its steel at what the city would otherwise pay or get
 * for it (outValue(): the import price for as much as the city imports, the
 * export price past it) less its scrap at what the city would otherwise get
 * or pay for that (inValue()), less its wages, power, water, repairs, tax
 * and consumables, asked the ground, the staff and the money on its whole
 * cost (decision A); the best on its cost ordered, one in flight; a kind
 * whose last furnace earns below nothing six months running the one the
 * spare-capacity rule may sell (idle, then shed). The study's four cases
 * for the two furnaces (its 2.5, at parity): the Electric Arc Furnace -0.5,
 * -0.1, 0.7 and 0.2% of its cost a month, the Steel Mini-Mill -0.5, 0.1, 1.0
 * and 0.4% - thin, and left as they are (its 10.2): built where the city's
 * fabricators import steel and its own scrap is cheap, shed where neither
 * holds. MaterialsCheck 9 holds the rows to the study.
 *
 * THE INTEGRATED ROUTE AND OLD STEEL (0.7.121, batch I4; runs/spec-materials.md
 * 2.2, 2.7, 2.8 and 2.9, the study's 2.4 to 2.7; Jerus's Q4, Q6, Q7, Q8, Q19
 * and Q20 of 2026-10-08). Seven more rows, each a candidate the same way:
 *   - the SCRAP YARD sorts the city's old steel (OldMetal) into scrap;
 *   - the PELLETISING PLANT fires ore into pellets, which ship (nothing here
 *     uses them until a DRI plant exists, the study's 2.8);
 *   - the COKE OVENS bake coking coal into coke;
 *   - the MINI BLAST FURNACE and the BLAST FURNACE smelt ore, coke and
 *     limestone into pig iron;
 *   - the OXYGEN STEEL SHOP blows pig iron, scrap and lime into steel;
 *   - the LARGE MINI-MILL melts scrap, the big arc-furnace plant (Q19: two
 *     sizes where real plants are big).
 * Coal, limestone and lime are imported until Coal Mining (batch I6) and
 * Quarrying and Cement & Concrete (I7) make them. The study's four cases for
 * each (its 2.5, at parity) MaterialsCheck 11 holds: what pays on imported
 * feed for export is under every hurdle (case A, at most 0.2%), and the route
 * pays on the city's own feed or replacing imports (cases B to D).
 *
 * CAPTIVE (ProcessingSector, the spec's 2.7): coke, pig iron and scrap go to
 * its own works' need first - the ovens' coke to the furnaces, the furnaces'
 * pig iron to the oxygen shop, the yards' scrap to the shop and the arc
 * furnaces - and only the rest is bought or sold.
 *
 * ITS OWN POWER (the spec's 2.8, Q20): the Coke Ovens make 8,000 kW against a
 * 2,200 kW draw, the Blast Furnace 60,000 against 51,400 and the Oxygen Steel
 * Shop 25,000 against 51,600, from their gas (BuildingsTemplate.getGen()): the
 * sector buys from the grid only its works' draw past what they make and
 * sells the grid what they make past it, at the tariff (I2's machinery).
 *
 * THE OLD STEEL (OldMetal): a yard takes old steel, which is not a good, so
 * its feed is not on a market. It is bought when the yards make their scrap
 * (produceFlow(SCRAP)), as much as they take at the rate they run and no more
 * than arose, at OldMetal.PAY_SHARE of scrap's export price, from each seller
 * pro rata (Jerus's Q8); scrap past what arose is not made (groundLimit()).
 * A yard is weighed against the pool (feedRefusal(): old steel for
 * FEED_GATE of one past what the yards standing take), on the share of its
 * nameplate the pool would fill (feedShare()) and paying for its old steel
 * (offMarketFeed()).
 */
public final class HeavyIndustry extends ProcessingSector {

    /** The Electric Arc Furnace's permanent id in buildings.json, the Steel Foundry's until 0.7.116 (batch I3; spec-materials 4): what the harnesses and the test player find it by. */
    public static final int ARC_FURNACE = 11;

    /** The Steel Mini-Mill's permanent id in buildings.json (0.7.116). */
    public static final int MINI_MILL = 12;

    /** The Scrap Yard's permanent id in buildings.json (0.7.121, batch I4): the city's old steel into scrap. */
    public static final int SCRAP_YARD = 101;

    /** The Pelletising Plant's permanent id (0.7.121): ore into pellets. */
    public static final int PELLETISING_PLANT = 102;

    /** The Coke Ovens' permanent id (0.7.121): coking coal into coke, and power. */
    public static final int COKE_OVENS = 103;

    /** The Mini Blast Furnace's permanent id (0.7.121): ore, coke and limestone into pig iron. */
    public static final int MINI_BLAST_FURNACE = 104;

    /** The Blast Furnace's permanent id (0.7.121): the big furnace, its sinter plant and its top-gas turbine. */
    public static final int BLAST_FURNACE = 105;

    /** The Oxygen Steel Shop's permanent id (0.7.121): pig iron, scrap and lime into steel. */
    public static final int OXYGEN_STEEL_SHOP = 106;

    /** The Large Mini-Mill's permanent id (0.7.121): the big arc-furnace plant. */
    public static final int LARGE_MINI_MILL = 107;

    public HeavyIndustry() {
        super("Heavy Industry", "Iron & Steel", BuildingType.HEAVY_INDUSTRY);
        makes(Good.STEEL);
        uses(Good.SCRAP);
        // ...and the integrated route's (0.7.121, batch I4): what its seven new works make and take.
        makes(Good.SCRAP);
        makes(Good.PELLETS);
        makes(Good.PIG_IRON);
        makes(Good.MET_COKE);
        uses(Good.IRON);
        uses(Good.MET_COKE);
        uses(Good.PIG_IRON);
        uses(Good.COKING_COAL);
        uses(Good.THERMAL_COAL);
        uses(Good.LIMESTONE);
        uses(Good.LIME);
        blurb("Makes steel: arc furnaces melt scrap, and since the integrated route "
                + "blast furnaces smelt ore with coke into pig iron for an oxygen shop. "
                + "Sorts the city's old steel into scrap at its yards, and sells the grid "
                + "the power its ovens and furnaces make from their gas. Builds whichever "
                + "works pays most on its whole cost: where the city imports steel, and "
                + "where its own ore and scrap are cheap.");
    }

    /* =====================================================================
       THE OLD STEEL (0.7.121, batch I4; OldMetal, the spec's 2.9)
       ===================================================================== */

    /** The city's old steel, or null with no city (a fixture's bare sector). */
    private ham.citybuildersim.OldMetal pool() {
        return game == null ? null : game.getOldMetal();
    }

    /** Tonnes of old steel its standing yards take a month at nameplate. */
    public double oldSteelIntake() {
        return buildings == null ? 0 : buildings.totalBySector(key(), t -> t.oldMetal(Good.STEEL));
    }

    /** ...and the tonnes of old steel a tonne of their scrap takes: their intake over their scrap at nameplate (10,500 into 10,000), 0 with no yard standing. */
    public double oldSteelPerTonneOfScrap() {
        double scrap = getCapacity(Good.SCRAP);
        return scrap > 0 ? oldSteelIntake() / scrap : 0;
    }

    /** What its yards pay a tonne of old steel today: OldMetal.PAY_SHARE of scrap's export price (Jerus's Q8); 0 with no markets. */
    public double oldSteelPrice() {
        if (markets == null) return 0;
        double export = markets.get(Good.SCRAP).exportPrice();
        return Double.isFinite(export) && export > 0 ? ham.citybuildersim.OldMetal.PAY_SHARE * export : 0;
    }

    /** Old steel arisen in the pool's last month past what the yards standing take: what one more would run on. */
    private double spareOldSteel(ham.citybuildersim.OldMetal pool) {
        return Math.max(0, pool.lastArisen() - oldSteelIntake());
    }

    /** Its scrap is limited by the old steel that arose (the yards make no scrap past it); every other good as Sector's. */
    @Override
    protected double groundLimit(Good g, double asked) {
        if (g != Good.SCRAP) return super.groundLimit(g, asked);
        ham.citybuildersim.OldMetal pool = pool();
        double per = oldSteelPerTonneOfScrap();
        if (pool == null || !(per > 0)) return 0;
        return Math.max(0, Math.min(asked, pool.available() / per));
    }

    /**
     * Makes a flow good for the month; its scrap, from the old steel its
     * yards buy now (THE OLD STEEL): what they made at the rate, at
     * oldSteelPerTonneOfScrap() a tonne, bought from the pool pro rata to
     * each seller at oldSteelPrice() - the households paid into their
     * savings as the yards buy (HouseholdBalance.creditScrappedCars()), a
     * sector by its other revenue (Sector.sellOldMetal()), its own old steel
     * for nothing, and each on its input line (OldMetal.LINE, billed by the
     * seller - Trade.HOUSEHOLDS for the households). The pool's month is
     * settled whether or not a yard stands: what no yard takes goes to
     * landfill.
     */
    @Override
    public void produceFlow(Good g) {
        super.produceFlow(g);
        if (g != Good.SCRAP) return;
        ham.citybuildersim.OldMetal pool = pool();
        if (pool == null) return;
        Output o = output(Good.SCRAP);
        double made = o.produced + o.captive;
        double take = made > 0 ? made * oldSteelPerTonneOfScrap() : 0;
        double price = oldSteelPrice();
        for (ham.citybuildersim.OldMetal.Sale sale : pool.settle(take, price)) {
            double paid = sale.paid();
            if (!(paid > 0) || key().equals(sale.seller())) continue;
            if (ham.citybuildersim.Trade.HOUSEHOLDS.equals(sale.seller())) {
                double credited = game.getHouseholdBalance().creditScrappedCars(paid);
                if (credited > 0) billForService(ham.citybuildersim.Trade.HOUSEHOLDS, ham.citybuildersim.OldMetal.LINE, credited);
                continue;
            }
            ham.citybuildersim.Sector seller = game.getSectors().byKey(sale.seller());
            if (seller == null) continue;
            seller.sellOldMetal(paid);
            billForService(seller.key(), ham.citybuildersim.OldMetal.LINE, paid);
        }
    }

    /** A yard is fed only while old steel for FEED_GATE of one arises past what the yards standing take; every other works buys its feed on the market. */
    @Override
    protected String feedRefusal(ham.citybuildersim.BuildingsTemplate t) {
        double intake = t == null ? 0 : t.oldMetal(Good.STEEL);
        if (!(intake > 0)) return null;
        ham.citybuildersim.OldMetal pool = pool();
        double spare = pool == null ? 0 : spareOldSteel(pool);
        if (spare >= SpreadPlanner.FEED_GATE * intake) return null;
        return String.format("too little old steel for one: %,.0f t arose last month, %,.0f t of it the yards standing take, and one takes %,.0f t",
                pool == null ? 0 : pool.lastArisen(), oldSteelIntake(), intake);
    }

    /** One more yard runs on the old steel past what the yards standing take, at most its nameplate; every other works on all of it. */
    @Override
    protected double feedShare(ham.citybuildersim.BuildingsTemplate t) {
        double intake = t == null ? 0 : t.oldMetal(Good.STEEL);
        if (!(intake > 0)) return 1;
        ham.citybuildersim.OldMetal pool = pool();
        return pool == null ? 0 : Math.min(1, spareOldSteel(pool) / intake);
    }

    /** The last yard standing runs on what the others leave of the old steel that arose, at most its nameplate. */
    @Override
    protected double feedShareStanding(ham.citybuildersim.BuildingsTemplate t) {
        double intake = t == null ? 0 : t.oldMetal(Good.STEEL);
        if (!(intake > 0)) return 1;
        ham.citybuildersim.OldMetal pool = pool();
        if (pool == null) return 0;
        double others = Math.max(0, oldSteelIntake() - intake);
        return Math.min(1, Math.max(0, pool.lastArisen() - others) / intake);
    }

    /** What a yard pays a month for the old steel it takes at `rate`: its intake at the rate, at oldSteelPrice(). */
    @Override
    protected double offMarketFeed(ham.citybuildersim.BuildingsTemplate t, double rate) {
        double intake = t == null ? 0 : t.oldMetal(Good.STEEL);
        return intake > 0 ? intake * rate * oldSteelPrice() : 0;
    }
}
