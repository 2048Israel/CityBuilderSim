package ham.citybuildersim;

import ham.citybuildersim.sectors.Retail;

/**
 * The homes' lamp oil and coal: what the households that light with a lamp and heat with a stove ask of the shops each month, as two shares of the homes.
 *
 * ==================== THE HOME FORM (R0c, 2026-11-05; research-1900 section 4 and 5.3) ====================
 *
 * A 1900 home was lit by a lamp burning kerosene (JET, the kerosene cut) and heated by a coal stove, and the
 * family of five spent about $1.50 a week on both: coal oil at 24 cents a gallon, a fifth of the bill for light
 * (1.06% against 4.19% of a working family's spending, US 1901, [P1.15]; [CYB18]), the rest for coal and wood
 * (R0e reads the same budget's quantities for the two dials below; the game has no firewood good). Two shares,
 * 0..1, of the homes run the 1900 way: lampShare, the homes lit by lamp oil and not by the grid, and coalShare,
 * the homes heated by a coal stove. At 0, the default and every played city in R0c, nothing here runs, nothing
 * is drawn or told and the month is today's, to the bit.
 *
 * At a share above 0 the month asks litres = lampShare x homes x LAMP_OIL_LITRES_A_HOME_MONTH of JET and tonnes =
 * coalShare x homes x COAL_TONNES_A_HOME_MONTH of THERMAL_COAL (strike(), at the drivers' fuel, Game 6d); Retail
 * sells them to the households at the pump's rule, each on its own wholesale (Retail.sellHomeFuel()), the city's
 * refiners and mines first and the world for the rest; and the households pay the bill like the drivers' fuel,
 * split over the homes (HouseholdAccounts.setHomeFuel()). "The homes" are the doors the households pay rent
 * on (Game.homeFuelHomes(): the sum of the rows' doors, HouseholdAccounts.rowDoors), the figure the bill is
 * split over, so a flat two families share has one stove.
 *
 * WHY A SHARE: the electric light and the gas range spread when they paid, and nothing in the game needs to
 * schedule that; R0e (the household's basket by year) sets the shares from the year, in a city founded the 1900
 * way (Game.basketHearsTheYear()), and sizes the two goods; a city founded the default way leaves them at 0, and
 * a harness may set them. The shares are not saved: they follow the year, which the month and the founding give
 * back on a load. The held sale is (heldSale(), restoreSale(); DataSave.homeFuelSale, R0e), since it is struck at
 * 6d and charged to the households the month after, and a save between the two must carry it.
 *
 * WHY NO POWER BILL: "electrified homes buy power instead", and today's homes pay nothing for power
 * (EconomyManager.setElectricityConsumption(): a household utility bill "is on the list"), so the grid's draw
 * for the homes is not touched here and no power bill is added: a home that leaves the lamp for the grid
 * costs the grid what it did, and costs the household nothing more, until the utility bill exists.
 */
public final class HomeFuel {

    /**
     * Lamp oil a home lit by kerosene burned a month, in litres: the Department of Labour's family budget, December
     * 1900, buys a gallon of coal oil a week (at 24.0 cents; Canada Year Book 1918, p. 485, Crown copyright expired),
     * the imperial gallon, 4.54609 L x 52 weeks / 12 = 19.70 L (R0e; R0c's estimate was 25 L).
     */
    public static final double LAMP_OIL_LITRES_A_HOME_MONTH = 4.54609 * 52 / 12;

    /**
     * Coal a home heated by a coal stove burned a month, in tonnes: the same budget buys a sixteenth of a short ton of
     * anthracite and a sixteenth of bituminous a week, beside an eighth of a cord of wood (a wood-heated home, weighted
     * alike), so an eighth of a short ton, 0.125 x 52 / 12 x 0.90718 t = 0.491 t (est. reading; R0e, R0c's was 0.7 t).
     */
    public static final double COAL_TONNES_A_HOME_MONTH = 0.125 * 52 / 12 * 0.90718474;

    /** The share of the homes lit by lamp oil, 0 to 1: 0 until the year (R0e) or a harness sets it; not saved, it follows the year. */
    private double lampShare;

    /** The share of the homes heated by a coal stove, 0 to 1: 0 until the year (R0e) or a harness sets it; not saved, it follows the year. */
    private double coalShare;

    /** The month's sale as struck (strike()): the homes it was struck for, the litres of JET and tonnes of THERMAL_COAL sold, the bill the households are charged, the wholesale cost and the part of it paid to the world. Zeroed before each month's strike. */
    private double homes, litres, tonnes, bill, cost, imported;

    /** Sets the lamp share, clamped to [0, 1] (not a number reads 0). */
    public void setLampShare(double s) {
        lampShare = Double.isFinite(s) ? Math.min(1, Math.max(0, s)) : 0;
    }

    /** Sets the coal share, clamped to [0, 1] (not a number reads 0). */
    public void setCoalShare(double s) {
        coalShare = Double.isFinite(s) ? Math.min(1, Math.max(0, s)) : 0;
    }

    public double getLampShare() { return lampShare; }

    public double getCoalShare() { return coalShare; }

    /** Whether either share is above 0: false in every played city in R0c, and then nothing here is called. */
    public boolean isOn() { return lampShare > 0 || coalShare > 0; }

    /** Whether a sale is held from a month struck while a share was set (the households are yet to be told it, or a share was since set to 0 and strike() clears it). */
    public boolean hasSale() { return homes > 0 || litres > 0 || tonnes > 0 || bill > 0 || cost > 0 || imported > 0; }

    /** The litres of lamp oil (JET) the homes ask for a month: the lamp share of the homes at LAMP_OIL_LITRES_A_HOME_MONTH. 0 with no share, no homes or a count that is not a number. */
    public double lampOilLitres(double homes) {
        return lampShare > 0 && Double.isFinite(homes) && homes > 0 ? lampShare * homes * LAMP_OIL_LITRES_A_HOME_MONTH : 0;
    }

    /** The tonnes of coal (THERMAL_COAL) the homes ask for a month: the coal share of the homes at COAL_TONNES_A_HOME_MONTH. 0 with no share, no homes or a count that is not a number. */
    public double coalTonnes(double homes) {
        return coalShare > 0 && Double.isFinite(homes) && homes > 0 ? coalShare * homes * COAL_TONNES_A_HOME_MONTH : 0;
    }

    /**
     * The homes' month, struck at the drivers' fuel (Game 6d): the month's sale is zeroed, then what the shares ask
     * of `homes` homes is sold through Retail (Retail.sellHomeFuel()) and kept for the households' books. With both
     * shares at 0 nothing is drawn, booked or kept: the sale stays zero.
     */
    void strike(Game game, double homes) {
        this.homes = litres = tonnes = bill = cost = imported = 0;
        double wantLitres = lampOilLitres(homes), wantTonnes = coalTonnes(homes);
        if (!(wantLitres > 0) && !(wantTonnes > 0)) return;
        this.homes = homes;
        Retail.HomeFuelSale sale = game.getSectors().retail().sellHomeFuel(wantLitres, wantTonnes, game);
        litres = sale.litres();
        tonnes = sale.tonnes();
        bill = sale.bill();
        cost = sale.cost();
        imported = sale.imported();
    }

    /** The homes the month's sale was struck for (the doors the households pay rent on, at the strike): 0 when nothing was asked. */
    public double getHomes() { return homes; }

    /** The month's litres of lamp oil sold to the homes. */
    public double getLitres() { return litres; }

    /** ...and tonnes of coal. */
    public double getTonnes() { return tonnes; }

    /** ...what the households were charged for both: the shop's two sales' values. */
    public double getBill() { return bill; }

    /** ...what Retail paid for them at wholesale, the city's refiners and mines and the world together. */
    public double getCost() { return cost; }

    /** ...and of that cost what Retail paid the world: its import. */
    public double getImported() { return imported; }

    /**
     * Both shares and the held sale back to 0, as a new city has them (Game.buildWorld()): this object outlives the city, and a share the last city
     * (founded the 1900 way, R0e) set must not burn lamp oil in the next, nor its sale be charged to the households of one that struck none.
     */
    void reset() {
        lampShare = coalShare = 0;
        homes = litres = tonnes = bill = cost = imported = 0;
    }

    /**
     * The held sale as six numbers {homes, litres, tonnes, bill, cost, imported}, a fresh array, for the save: null when no sale is held
     * (hasSale() false), so a month that struck nothing writes nothing. WHY (R0e): the sale struck at 6d is charged to the households next
     * month, and a save between the two has to carry it or the load loses a month's bill.
     */
    double[] heldSale() {
        return hasSale() ? new double[]{homes, litres, tonnes, bill, cost, imported} : null;
    }

    /**
     * Puts back a sale from heldSale(), as a load does. Null or fewer than six numbers restores nothing (the sale stays as it is); an entry that
     * is not a number reads 0.
     */
    void restoreSale(double[] v) {
        if (v == null || v.length < 6) return;
        homes = finite(v[0]);
        litres = finite(v[1]);
        tonnes = finite(v[2]);
        bill = finite(v[3]);
        cost = finite(v[4]);
        imported = finite(v[5]);
    }

    private static double finite(double d) {
        return Double.isFinite(d) ? d : 0;
    }

    /** The held sale, divided into the new unit in a currency reform (Game.redenominate()), as the drivers' fuel is. */
    void redenominate(double scale) {
        bill *= scale;
        cost *= scale;
        imported *= scale;
    }
}
