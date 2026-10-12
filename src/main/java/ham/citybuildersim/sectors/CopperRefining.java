package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.OldMetal;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Sectors;
import ham.citybuildersim.Trade;

import java.util.List;

/**
 * Smelters, refineries and rod mills - Copper Refining (0.7.133, batch I8;
 * docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12 and 5's I8 row; the
 * study's - materials-chains-research.md - 4): copper's processing industry, on
 * the end of Sectors.KEYS after Copper Mining (Jerus's Q21).
 *
 * WHY. Copper Mining's concentrate is 30% copper and nobody uses it but a
 * smelter; the city's old copper (OldMetal) is copper nobody here could use
 * at all. One sector owns every building that turns copper into something
 * else and builds whichever earns most on its whole cost (the study's 1.1 and
 * 1.2, Jerus's A) - ProcessingSector's rule, so this class is a declaration,
 * and its one rule of its own is THE OLD COPPER.
 *
 * THE FOUR BUILDINGS (the study's 4.3; ids 124 to 127):
 *   - the COPPER SMELTER, 9,750 t of anode a month from 33,000 t of
 *     concentrate, and 29,700 t of sulphuric acid from its gas, a co-product
 *     it sells (Jerus's Q12) - to the leach plants first;
 *   - the ELECTROLYTIC REFINERY, 9,700 t of cathode from 9,750 t of anode;
 *   - the SCRAP COPPER REFINERY, 4,000 t of cathode from 4,400 t of scrap -
 *     the city's old copper first, the market's past it;
 *   - the ROD MILL, 8,000 t of wire rod from 8,016 t of cathode.
 * Manufacturing's Wire & Cable Works draws the rod into wire and cable, the
 * chain's third industry (Manufacturing).
 *
 * WHAT IT PAYS (the study's 4.4, at parity; A on imported feed for export, B
 * on the city's feed at its export price, C that feed replacing imports, D
 * both at import prices): the smelter -0.3, 1.2, 2.7 and 1.2% of its cost a
 * month - smelting is thin, as it really is, and pays only on the city's own
 * concentrate, which costs no freight; the electrolytic refinery -1.9, 0.8,
 * 3.5 and 0.8%; the scrap refinery 0.0, 2.2, 2.9 and 0.7% - imported scrap
 * just breaks even, so it is worth building on the city's old copper; the
 * rod mill -6.7, 2.2, 11.1 and 2.2%, a thin premium on a big tonnage.
 *
 * CAPTIVE (ProcessingSector, the spec's 2.7): anode and cathode go to its own
 * works' need first - the smelters' anode to the refineries, the refineries'
 * cathode to the rod mills - and only the rest is bought or sold.
 *
 * THE OLD COPPER (OldMetal.copper(); the spec's 2.9). Old copper is not a
 * good: it arises in a pool each month, and the scrap refineries take it
 * before they bid for COPPER_SCRAP - as much as their intake at the rate
 * (their rows' oldMetal, 4,400 t a refinery: all of their feed) and no more
 * than arose, bought from each seller pro rata at OldMetal.PAY_SHARE of
 * COPPER_SCRAP's export price (★ the spec's 2.9, as Jerus's Q8 rule for old
 * steel) when the month's flow goods are made (produceFlow(ANODE)): the
 * households paid into their savings, a sector by its other revenue, each on
 * its input line, OldMetal.LINE, as Iron & Steel's yards pay for old steel.
 * Its bid for scrap is the rest of its need (bid()); what the refineries do
 * not take goes to landfill. One more scrap refinery is weighed with the old
 * copper past what those standing take at that price, and the scrap past it
 * at the market's (values()) - the last one standing with what the others
 * leave it - so the rule builds one where the city's old copper is, and not
 * on the world's scrap alone (the study's 4.4: "imported scrap just breaks
 * even").
 *
 * Who buys: Manufacturing's Wire & Cable Works the rod; Copper Mining's leach
 * plants the acid; the world the rest - anode, cathode, rod and acid all
 * ship.
 */
public final class CopperRefining extends ProcessingSector {

    /** The Copper Smelter's permanent id in buildings.json (0.7.133): 9,750 t of anode and 29,700 t of acid a month from 33,000 t of concentrate. */
    public static final int SMELTER = 124;

    /** The Electrolytic Refinery's (0.7.133): 9,700 t of cathode a month from 9,750 t of anode. */
    public static final int ELECTROLYTIC_REFINERY = 125;

    /** The Scrap Copper Refinery's (0.7.133): 4,000 t of cathode a month from 4,400 t of the city's old copper and scrap. */
    public static final int SCRAP_REFINERY = 126;

    /** The Rod Mill's (0.7.133): 8,000 t of wire rod a month from 8,016 t of cathode. */
    public static final int ROD_MILL = 127;

    public CopperRefining() {
        super(Sectors.COPPER_REFINING, "Copper Refining", BuildingType.HEAVY_INDUSTRY);
        // Cathode first: its planning good, the one it keeps in a yard.
        makes(Good.COPPER);
        makes(Good.ANODE);
        makes(Good.ACID);
        makes(Good.COPPER_ROD);
        uses(Good.COPPER_CONC);
        uses(Good.ANODE);
        uses(Good.COPPER);
        uses(Good.COPPER_SCRAP);
        blurb("Smelts copper concentrate into anode - selling the sulphuric acid its gas makes - "
                + "refines anode and the city's old copper and scrap into cathode, and rolls cathode "
                + "into wire rod. Builds whichever works pays most on its whole cost: a smelter beside "
                + "the city's own mines, a scrap refinery where the city throws copper out.");
    }

    /* =====================================================================
       THE OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9)
       ===================================================================== */

    /** The city's old copper, or null with no city (a fixture's bare sector). */
    private OldMetal.Pool pool() {
        return game == null || game.getOldMetal() == null ? null : game.getOldMetal().copper();
    }

    /** Tonnes of old copper its standing scrap refineries take a month at nameplate (their rows' oldMetal). */
    public double oldCopperIntake() {
        return buildings == null ? 0 : buildings.totalBySector(key(), t -> t.oldMetal(Good.COPPER));
    }

    /** What its refineries pay a tonne of old copper today: OldMetal.PAY_SHARE of COPPER_SCRAP's export price; 0 with no markets. */
    public double oldCopperPrice() {
        if (markets == null) return 0;
        double export = markets.get(Good.COPPER_SCRAP).exportPrice();
        return Double.isFinite(export) && export > 0 ? OldMetal.PAY_SHARE * export : 0;
    }

    /** Old copper its refineries took this month, tonnes: the pool's last purchase (settled when the month's flow goods are made, before any market clears); nothing with no city. */
    public double oldCopperTaken() {
        OldMetal.Pool pool = pool();
        return pool == null ? 0 : pool.lastBought();
    }

    /**
     * Makes a flow good for the month; and with its anode - the first flow
     * good it makes, before any market clears - the month's old copper is
     * bought (THE OLD COPPER): its refineries' intake at the rate, no more
     * than arose, from each seller pro rata at oldCopperPrice() - the
     * households paid into their savings as the refineries buy
     * (HouseholdBalance.creditScrappedCars()), a sector by its other revenue
     * (Sector.sellOldMetal()), its own for nothing, and each on its input
     * line (OldMetal.LINE, billed by the seller - Trade.HOUSEHOLDS for the
     * households). The pool's month is settled whether or not a refinery
     * stands: what none takes goes to landfill.
     */
    @Override
    public void produceFlow(Good g) {
        super.produceFlow(g);
        if (g != Good.ANODE) return;
        OldMetal.Pool pool = pool();
        if (pool == null) return;
        double take = oldCopperIntake() * getOperatingRate();
        double price = oldCopperPrice();
        for (OldMetal.Sale sale : pool.settle(take, price)) {
            double paid = sale.paid();
            if (!(paid > 0) || key().equals(sale.seller())) continue;
            if (Trade.HOUSEHOLDS.equals(sale.seller())) {
                double credited = game.getHouseholdBalance().creditScrappedCars(paid);
                if (credited > 0) billForService(Trade.HOUSEHOLDS, OldMetal.LINE, credited);
                continue;
            }
            Sector seller = game.getSectors().byKey(sale.seller());
            if (seller == null) continue;
            seller.sellOldMetal(paid);
            billForService(seller.key(), OldMetal.LINE, paid);
        }
    }

    /** What it asks the market for of a good this month; of scrap, its need past the old copper its refineries took this month (THE OLD COPPER). */
    @Override
    public double bid(Good g) {
        double asked = super.bid(g);
        if (g != Good.COPPER_SCRAP) return asked;
        return Math.max(0, asked - oldCopperTaken());
    }

    /** The month's inputs at the rate, at today's prices: Sector's, its old copper at what it paid for it rather than the scrap market's price. */
    @Override
    protected double inputCostAtRate() {
        double total = super.inputCostAtRate();
        double old = Math.min(oldCopperTaken(), getInputAtCapacity(Good.COPPER_SCRAP) * getOperatingRate());
        if (old > 0) total -= old * (priceOf(Good.COPPER_SCRAP) - oldCopperPrice());
        return total;
    }

    /** Old copper arisen in the pool's last month past what the refineries standing take at nameplate: what one more would run on. */
    public double spareOldCopper() {
        OldMetal.Pool pool = pool();
        return pool == null ? 0 : Math.max(0, pool.lastArisen() - oldCopperIntake());
    }

    /** ...and what the others leave the last one standing: the pool's last month less the rest's intake at nameplate, at most its own. */
    public double leftForTheLast() {
        OldMetal.Pool pool = pool();
        if (pool == null || buildings == null) return 0;
        BuildingsTemplate refinery = buildings.getTemplate(SCRAP_REFINERY);
        double one = refinery == null ? 0 : refinery.oldMetal(Good.COPPER);
        double others = Math.max(0, oldCopperIntake() - one);
        return Math.min(one, Math.max(0, pool.lastArisen() - others));
    }

    /**
     * The city's values a works of its is weighed at (ProcessingSector's),
     * with the old copper in its scrap (THE OLD COPPER): one more refinery's
     * scrap is the old copper past what those standing take, at
     * oldCopperPrice(), and the rest at the market's value (in()); the last
     * one standing's, what the others leave it at that price, and the rest
     * at the market's (out(), the mirror IDLE, THEN SHED reads). Every other
     * good, and every month with no old copper, as ProcessingSector's, to the
     * bit.
     */
    @Override
    protected SpreadPlanner.Values values() {
        SpreadPlanner.Values v = super.values();
        double price = oldCopperPrice(), spare = spareOldCopper(), last = leftForTheLast();
        if (!(price > 0) || !(spare > 0 || last > 0)) return v;
        return new SpreadPlanner.Values() {
            @Override public double out(Good g, double u) {
                if (g != Good.COPPER_SCRAP || !(last > 0)) return v.out(g, u);
                double old = Math.min(u, last);
                return old * price + v.out(g, u - old);
            }
            @Override public double in(Good g, double u) {
                if (g != Good.COPPER_SCRAP || !(spare > 0)) return v.in(g, u);
                double old = Math.min(u, spare);
                return old * price + v.in(g, u - old);
            }
        };
    }

    /* ----- the page ----- */

    @Override
    public List<Line> ownLines(Game game) {
        List<Line> lines = new java.util.ArrayList<>();
        OldMetal.Pool pool = pool();
        if (game == null || pool == null) return lines;
        Formats f = Formats.INSTANCE;
        lines.add(Line.head("The city's old copper, last month"));
        lines.add(Line.of("Arose, from cars and demolitions", f.count(pool.lastArisen()) + " t"));
        lines.add(Line.of("Bought by the scrap refineries", f.count(pool.lastBought()) + " t"));
        lines.add(Line.of("To landfill", f.count(pool.lastLandfill()) + " t", Line.Tone.MUTED));
        if (oldCopperIntake() > 0) {
            GoodsMarket scrap = markets == null ? null : markets.get(Good.COPPER_SCRAP);
            lines.add(Line.note(String.format("The scrap refineries take the city's old copper first, at %s a tonne, and buy"
                    + " scrap on the market past it%s.", f.amount(oldCopperPrice()),
                    scrap == null ? "" : ", at " + f.amount(scrap.getLocalPrice()) + " a tonne")));
        }
        return lines;
    }
}
