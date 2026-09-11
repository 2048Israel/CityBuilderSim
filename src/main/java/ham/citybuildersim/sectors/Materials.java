package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

/**
 * The materials plant. THE SEVENTH SECTOR (2026-09-11, Jerus's call).
 *
 * Until the sector template the Construction Materials Plant's 160 units a
 * month went into the city's yard for free, and the builders imported the
 * rest at a fixed world price - a building that was never paid for what it
 * made. It sells on the materials market now: every build order and every
 * repair draws on the yard first, then on this plant's stock at the local
 * price, then on the world. A city that builds hard makes materials dear,
 * and a plant near the builders is the first thing in the game that gets
 * cheaper for the buyer AND richer for the maker at once.
 *
 * Stockable, importable, exportable - the same three as food, so the same
 * template rules: it makes for the demand it can see, holds three months of
 * output, and ships spare nameplate abroad at the export wedge.
 */
public final class Materials extends Sector {

    /**
     * Half a plant's nameplate, a month, before the first one is sunk. A
     * plant is 160 units and 250 staff; the first probe city drew thirty a
     * month, most of them from the free works yard, and the plant it built
     * against that lost money every month it stood and took the bank with
     * it. Eighty units a month is a city building eight houses a month, or
     * one apartment block a year - a city that is actually building.
     */
    public static final double FIRST_PLANT_UTILISATION = .5;

    public Materials() {
        super("Materials", "Materials", BuildingType.CONSTRUCTION);
        makes(Good.MATERIALS);
        blurb("Makes the units of material every building is made of, and sells "
                + "them to the builders at the market price - or abroad, when nothing "
                + "here is being built.");
    }

    @Override
    public double firstPlantUtilisation() { return FIRST_PLANT_UTILISATION; }

    /**
     * ITS CUSTOMERS' ORDER BOOK IS PUBLIC. What the sites still owe in
     * material, spread over the months asked, plus the repair draw that
     * goes on whatever is built - that is every unit the sector will sell
     * at home in that time, and the trend cannot promise more. Measured
     * without it: a fixture city that ordered five hundred houses on its
     * first day drew a thousand units a month for eight years, the sector
     * read the trend and built seven plants against it, and the queue
     * emptied the month the seventh opened - seven plants, seventeen
     * hundred staff, exporting at the floor for the rest of the run in a
     * city of eight thousand. And the playtest's first city, asked over
     * the nineteen months to a plant's opening, saw a hundred units a
     * month on a book that was in fact a seventeen-month spurt ending
     * that month; asked over the three years the trend looks back, it
     * sees sixty, and sixty does not pay for a plant.
     */
    @Override
    public double visibleDemandOver(double months, ham.citybuildersim.EconomyManager economy) {
        if (buildings == null || economy == null) return Double.MAX_VALUE;
        double owed = buildings.getMaterialsOwed();
        double repairs = economy.getMaintenanceMaterialsTotal();
        return owed / Math.max(1, months) + repairs;
    }

    /**
     * THE SHED IS THE BUSINESS. A mill makes for the demand it can see and
     * holds two months of it (Sector.getPlannedOutput), which is right for
     * a good people eat every month and wrong for one that is drawn on
     * order: the builders take four hundred units the month a foundry is
     * ordered and nothing for a year, so a plant sized to last month's
     * draw idles most months and exports the rest at the floor. It fills
     * its shed instead, and the shed is what the orders draw on - the
     * plant's own works yard, at the market price. It idles only when the
     * shed is full.
     */
    @Override
    public double getPlannedOutput(Good g) {
        if (g != Good.MATERIALS) return super.getPlannedOutput(g);
        double nameplate = getCapacity(g) * getOperatingRate();
        double room = Math.max(0, getStockCapacity(g) - getStock(g));
        return Math.max(0, Math.min(nameplate, room));
    }
}
