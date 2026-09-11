package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Sector;

import java.util.List;

/**
 * Iron mines. Its own sector because the ore has a price.
 *
 * THE SMALLEST SECTOR CLASS THERE IS, and the shape a thousand of them
 * should have: it says what it makes, and the one thing that is unlike a
 * factory - the ground is the limit. Everything else - the books, the
 * credit, the market, the payroll, the export of what the mills do not
 * want - is the template's.
 *
 * A mine ships what it lifts: the ore is not stockable (see Good.IRON), so
 * what the mills will not take at the local price leaves at the export
 * price the same month, which is why a mine is worth building before the
 * mills exist. LandManager decides how much of what was asked for is
 * actually there, and nothing once the deposit is out.
 */
public final class Mining extends Sector {

    public Mining() {
        super("Mining", "Mining", BuildingType.MINING);
        makes(Good.IRON);
        blurb("Lifts ore off the ground and sells it to the mills, or ships it abroad "
                + "when the mills do not want it. The deposit is the limit, and the "
                + "city has to buy the ground it is under.");
    }

    /** The ground, not the mine, decides what comes up. */
    @Override
    protected double groundLimit(Good g, double asked) {
        if (game == null || g != Good.IRON) return asked;
        LandManager land = game.getLandManager();
        return land == null ? asked : land.extractIron(asked);
    }

    /** What the mines could lift this month if the ground allowed it. */
    public double getPotentialOutput() {
        return getCapacity(Good.IRON) * getOperatingRate();
    }

    /**
     * Whether to open another mine.
     *
     * Unlike every other sector, this one can be refused by geology. A mine
     * needs ground with ore under it, and that is the city's to buy - so the
     * private sector expanding into mining depends on the player having gone
     * and bought a deposit. That is the intended coupling: land is the one
     * input only the city controls, and now it gates an industry.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        int ordersInFlight = buildings.getUnderConstructionBySector(sector);
        if (ordersInFlight >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        LandManager land = game.getLandManager();
        int unminedDeposits = land.getIronDeposits() - game.minesCommitted();
        double reserveTonnes = land.getIronReserveTonnes();

        if (unminedDeposits <= 0) return BusinessInvestment.Decision.no(sector, "no deposit to dig");
        if (reserveTonnes <= 0)   return BusinessInvestment.Decision.no(sector, "the ore is worked out");

        BuildingsTemplate best = null;
        double bestScore = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.makes(Good.IRON) <= 0) continue;
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = estimatedMonthlyProfit(t, plans) / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) return BusinessInvestment.Decision.no(sector, "nothing worth sinking");
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%d deposit(s) unworked, %,.0fk tonnes in the ground",
                        unminedDeposits, reserveTonnes / 1000), true);
    }

    /**
     * A price-taking exporter always sells what it lifts: it shrinks on
     * distress only - WHILE THERE IS ORE. A mine over a worked-out deposit
     * lifts nothing, and plant that lifts nothing is spare by any measure.
     *
     * Measured before this (2026-09-11), on the playtest's first city: the
     * ore ran out, seven hundred miners stayed on the payroll of fifty-two
     * dead mines, the sector borrowed fourteen million a month for three
     * years to pay them - the shortfall desk lends against the mines' book
     * value, which was seven hundred million of holes - and was written
     * down by four hundred million when the book was finally called. The
     * distress rule never fired because the loans kept the till positive.
     * Fifty-two mines and eleven write-downs in the run before the sector
     * template, too; the bank was just fat enough then to eat it.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null) return null;
        double capacity = getCapacity(Good.IRON);
        if (capacity <= 0) return null;
        return game.getLandManager().getIronReserveTonnes() > 0
                ? null
                : new double[] { 0, capacity };
    }

    @Override
    public List<Line> operations(Game game) {
        List<Line> lines = super.operations(game);
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;
        LandManager land = game.getLandManager();
        lines.add(Line.head("What is under the ground"));
        lines.add(Line.of("Deposits found", f.count(land.getIronDeposits())));
        lines.add(Line.of("Being worked", f.count(game.minesCommitted()),
                game.minesCommitted() < land.getIronDeposits() ? Line.Tone.WARN : Line.Tone.GOOD));
        if (game.minesCommitted() < land.getIronDeposits()) {
            lines.add(Line.note("There is ore under this city that nothing is digging."));
        }
        return lines;
    }
}
