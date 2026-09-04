package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * The city's land: what it owns, what is built on, and what it sells.
 *
 * This is the piece that changes what the player's job is. Before it, buildings
 * appeared wherever they were wanted and the only limits were cash, materials
 * and construction capacity - all of which the private sector eventually
 * supplies for itself. Land is the one input only the city controls, so it is
 * the lever that makes a city government necessary rather than decorative.
 *
 * THREE NUMBERS
 *
 *   owned       every square foot the city has annexed
 *   allocated   what is standing on, or being built on
 *   available   the difference - what can still be built on
 *
 * Land is never freed, because buildings are never demolished. When demolition
 * exists, release() is where it hooks in.
 *
 * BUYING AND SELLING
 *
 * The city buys from outside in blocks, at a price that rises as it expands -
 * annexing further out costs more, and it stops "buy everything immediately"
 * from being the obvious move. It sells to businesses at a price the player
 * sets, and the spread between the two is the city's margin. Setting the price
 * too high prices businesses out of building at all, which is a real decision
 * rather than a free revenue dial.
 *
 * The city does not pay itself for land it builds on: it already owns it, and
 * charging its own budget would just move money from one pocket to the other
 * while inflating GDP.
 */
public class LandManager {

    /** One city block, in square feet. About 2.3 acres, near a real block. */
    public static final double BLOCK_SQ_FT = 100000;

    /**
     * Land the city starts with - thirty blocks, about 69 acres.
     *
     * Sized off the two buildings the player cannot avoid: a coal plant is
     * twenty blocks and a water plant eight, so putting up both leaves two.
     * That is deliberate. Start with less and the first power station is
     * unbuildable, which is a wall rather than a constraint; start with much
     * more and the player never notices land exists until hour six. Twenty-eight
     * of the thirty going to the two utilities teaches the mechanic on the first
     * turn, at the price of one cheap purchase rather than a stalled game.
     */
    public static final double STARTING_SQ_FT = 3000000;

    /**
     * Cost of the first block bought, in thousands. $70,000, i.e. $0.70/sq ft.
     *
     * Priced off the House, which is the one building whose lot is large
     * relative to what it costs to put up: $30k of building on 8,000 sq ft,
     * where every other building runs $15 to $150 of construction per square
     * foot of lot. So the House is what any land price hits first and hardest,
     * and it sets the ceiling.
     *
     * At $6/sq ft - which looked reasonable next to real land prices - a house
     * plot cost $72 against $30 to build the house, and housing a resident in a
     * house stopped being cheaper than housing them in an apartment. That is a
     * defensible end state but not a default; it rewrites what the residential
     * sector builds before the player has touched anything. At $0.70 land is
     * 27% of a house and 1-7% of everything else, which leaves the existing
     * balance intact.
     *
     * The treasury still feels it, through volume rather than unit price: a
     * city of 160,000 needs hundreds of millions of square feet, and at these
     * prices that is hundreds of millions of dollars of land.
     */
    private static final double BASE_BLOCK_COST = 70;

    /** Each block bought makes the next this much dearer - annexing outward. */
    private static final double COST_GROWTH_PER_BLOCK = .02;

    /**
     * Opening sale price, $1/sq ft - a 43% margin on what the city pays.
     *
     * The interesting thing about this number is what happens when the player
     * raises it. Houses are land-hungry and apartments are not, so dear land
     * makes building up cheaper per resident than building out: somewhere
     * around $9/sq ft the residential sector stops choosing houses. The price
     * is a density policy, not just a revenue dial, and that is the whole
     * reason the player sets it rather than the market.
     */
    private static final double DEFAULT_PRICE_PER_SQ_FT = .001;

    private double ownedSqFt = STARTING_SQ_FT;
    private double allocatedSqFt;

    private int blocksPurchased;

    /**
     * What businesses pay per square foot.
     *
     * NO LONGER SET BY THE PLAYER. It is the land market's clearing price now -
     * see LandMarket - and moves with how full the city is: empty city, cheap
     * land; full city, dear land. The way to make land cheap is to go and buy
     * some, which is a more interesting lever than a slider was.
     *
     * Kept as a field rather than read through every time because it is a
     * MONTH'S price: the sale price a business paid is a fact about the month it
     * built in, and re-deriving it later is the mistake this codebase has made
     * in four other places.
     */
    private double pricePerSqFt = DEFAULT_PRICE_PER_SQ_FT;

    /** Ten plots on offer, and what the next one costs. */
    private final LandMarket market = new LandMarket();

    /* ------------------------------ ore ------------------------------
     *
     * Deposits are POOLED rather than tracked per parcel, and that is a
     * deliberate simplification with one consequence worth knowing: the count
     * caps how many mines can stand, and the tonnage is drawn down by all of
     * them together. Nothing sits on a particular deposit, because nothing in
     * this game sits anywhere - buildings are stacks, not locations.
     * ---------------------------------------------------------------- */

    /** Deposit sites the city owns. Caps how many Iron Mines can stand. */
    private int ironDeposits;

    /** Ore still in the ground across all of them, in tonnes. */
    private double ironReserveTonnes;

    /** Lifted this month, for the mining report. */
    private double ironMinedThisMonth;

    /* Monthly flows, for the government accounts. Cleared each month. */
    private double landSalesThisMonth;
    private double sqFtSoldThisMonth;
    private double landPurchasesThisMonth;
    private double sqFtBoughtBackThisMonth;

    //getters
    public double getOwnedSqFt()     { return ownedSqFt; }
    public double getAllocatedSqFt() { return allocatedSqFt; }

    public double getAvailableSqFt() {
        return Math.max(ownedSqFt - allocatedSqFt, 0);
    }

    /** How full the city is. 1.0 means every square foot is spoken for. */
    public double getUtilisation() {
        return (ownedSqFt > 0) ? Math.min(allocatedSqFt / ownedSqFt, 1) : 1;
    }

    public double getAvailableBlocks()  { return getAvailableSqFt() / BLOCK_SQ_FT; }
    public int    getBlocksPurchased()  { return blocksPurchased; }
    public double getPricePerSqFt()     { return pricePerSqFt; }

    public double getLandSalesThisMonth()     { return landSalesThisMonth; }
    public double getSqFtSoldThisMonth()      { return sqFtSoldThisMonth; }
    public double getLandPurchasesThisMonth() { return landPurchasesThisMonth; }
    public double getSqFtBoughtBackThisMonth(){ return sqFtBoughtBackThisMonth; }

    /** What a block's worth of land costs at today's market rate, in thousands. */
    public double getNextBlockCost() {
        return getAcquisitionCostPerSqFt() * BLOCK_SQ_FT;
    }

    /** Ground price per square foot the city would pay today. */
    public double getAcquisitionCostPerSqFt() {
        return market.getMarketPricePerSqFt();
    }

    /* --------------------------- the market --------------------------- */

    public LandMarket getMarket()                 { return market; }
    public java.util.List<LandParcel> getListing(){ return market.getListing(); }

    /**
     * Re-prices the market and refills the window. Once a month, and again
     * after any purchase so the replacement plot is priced against the city as
     * it stands afterwards.
     */
    public void updateMarket(int population) {
        market.update(ownedSqFt, allocatedSqFt, population);
        pricePerSqFt = market.getSalePricePerSqFt();
    }

    /**
     * Buys one listed parcel.
     *
     * @return what it cost, or 0 if it could not be afforded or is not listed -
     *         in which case nothing changed and the caller must not spend
     */
    public double buyParcel(int parcelId, double availableCash, int population) {

        LandParcel parcel = market.find(parcelId);
        if (parcel == null || parcel.getPrice() > availableCash) {
            return 0;
        }

        market.take(parcelId);

        ownedSqFt += parcel.getSizeSqFt();
        blocksPurchased++;
        landPurchasesThisMonth += parcel.getPrice();

        if (parcel.hasIron()) {
            // A parcel is worth as many mines as it has sites, which is not
            // always one: this was `ironDeposits++`, so a tract holding four
            // deposits bought the city exactly the same mining capacity as the
            // smallest strike on the listing.
            ironDeposits += parcel.getDeposits();
            ironReserveTonnes += parcel.getIronTonnes();
        }

        // Refill and re-price AFTER the purchase, so a city that just got
        // bigger sees the next offer at its new size.
        updateMarket(population);

        return parcel.getPrice();
    }

    /* ------------------------------ ore ------------------------------ */

    public int getIronDeposits()          { return ironDeposits; }
    public double getIronReserveTonnes()  { return ironReserveTonnes; }
    public double getIronMinedThisMonth() { return ironMinedThisMonth; }

    public boolean hasUnminedDeposit(int minesStanding) {
        return minesStanding < ironDeposits && ironReserveTonnes > 0;
    }

    /**
     * Lifts ore out of the ground.
     *
     * @return what was actually there, which is less than asked for once the
     *         reserves run low and zero once they are gone. A mine standing on
     *         an exhausted deposit still costs its payroll; that is what
     *         "finite" means and the player is expected to notice.
     */
    public double extractIron(double tonnes) {
        double lifted = Math.max(0, Math.min(tonnes, ironReserveTonnes));
        ironReserveTonnes -= lifted;
        ironMinedThisMonth = lifted;
        return lifted;
    }

    public void restoreIron(int deposits, double reserveTonnes) {
        this.ironDeposits = Math.max(0, deposits);
        this.ironReserveTonnes = Math.max(0, reserveTonnes);
    }

    /** Margin per square foot at the current sale price. Negative sells at a loss. */
    public double getMarginPerSqFt() {
        return pricePerSqFt - getAcquisitionCostPerSqFt();
    }

    //setters
    /**
     * @deprecated The sale price is the market's now, not the player's. Kept so
     *             the load path can put back the price a saved month traded at
     *             before the market recomputes it, and so older callers still
     *             compile. Anything that calls this to STEER the price is doing
     *             nothing useful - updateMarket() overwrites it next month.
     */
    @Deprecated
    public void setPricePerSqFt(double price) {
        this.pricePerSqFt = Math.max(price, 0);
    }

    public void setOwnedSqFt(double sqFt)     { this.ownedSqFt = sqFt; }
    public void setAllocatedSqFt(double sqFt) { this.allocatedSqFt = sqFt; }
    public void setBlocksPurchased(int blocks){ this.blocksPurchased = blocks; }

    //land
    /** Is there room to put this up at all? */
    public boolean canAllocate(double sqFt) {
        return sqFt <= getAvailableSqFt();
    }

    /**
     * Takes land out of the available pool.
     *
     * @return false if there was not enough, in which case nothing was taken -
     *         the caller must not build.
     */
    public boolean allocate(double sqFt) {
        if (!canAllocate(sqFt)) {
            return false;
        }
        allocatedSqFt += sqFt;
        return true;
    }

    /** Frees land again. Unused until buildings can be demolished. */
    public void release(double sqFt) {
        allocatedSqFt = Math.max(allocatedSqFt - sqFt, 0);
    }

    /** What a business pays the city for a plot this size. */
    public double priceFor(double sqFt) {
        return sqFt * pricePerSqFt;
    }

    /** Records a sale to a business. The caller moves the cash. */
    public void recordSale(double sqFt) {
        landSalesThisMonth += priceFor(sqFt);
        sqFtSoldThisMonth += sqFt;
    }

    /**
     * Records the city buying a plot back from a business that scrapped what
     * stood on it.
     *
     * The land was already the city's to allocate - release() has handed it
     * back to the available pool - so nothing about the holdings changes here.
     * What changes is the treasury: a city that taxes its businesses into
     * folding buys their plots back at the price it set. The caller moves the
     * cash; this only records it.
     */
    public void recordBuyback(double sqFt) {
        landPurchasesThisMonth += priceFor(sqFt);
        sqFtBoughtBackThisMonth += sqFt;
    }

    /**
     * Annexes one block.
     *
     * @return the cost, or 0 if the city could not afford it
     */
    public double buyBlock(double availableCash) {
        return buyBlock(availableCash, 0);
    }

    /**
     * Buys the cheapest thing on offer.
     *
     * The old "annex one block" button, kept working. There are no blocks any
     * more - there are ten plots of assorted sizes - so the nearest honest
     * equivalent is the cheapest one, which is what a player pressing a button
     * labelled "buy some land" means.
     *
     * @return what it cost, or 0 if nothing on offer was affordable
     */
    public double buyBlock(double availableCash, int population) {

        LandParcel cheapest = market.cheapest();
        if (cheapest == null) {
            updateMarket(population);
            cheapest = market.cheapest();
        }
        if (cheapest == null) {
            return 0;
        }

        return buyParcel(cheapest.getId(), availableCash, population);
    }

    /** Called once a month, after the government accounts have read the flows. */
    public void clearMonth() {
        landSalesThisMonth = 0;
        sqFtSoldThisMonth = 0;
        landPurchasesThisMonth = 0;
        sqFtBoughtBackThisMonth = 0;
        ironMinedThisMonth = 0;
    }

    public void reset() {
        ownedSqFt = STARTING_SQ_FT;
        allocatedSqFt = 0;
        blocksPurchased = 0;
        pricePerSqFt = DEFAULT_PRICE_PER_SQ_FT;
        ironDeposits = 0;
        ironReserveTonnes = 0;
        market.reset();
        clearMonth();
    }

    //printers
    public void printLandInfo() {
        System.out.println("\n======================= CITY LAND =======================");
        System.out.printf("Owned:              %s sq ft (%.1f blocks)%n",
                formatter.format(ownedSqFt), ownedSqFt / BLOCK_SQ_FT);
        System.out.printf("Built on:           %s sq ft%n", formatter.format(allocatedSqFt));
        System.out.printf("Available:          %s sq ft (%.1f blocks)%n",
                formatter.format(getAvailableSqFt()), getAvailableBlocks());
        System.out.printf("Utilisation:        %.1f%%%n", getUtilisation() * 100);
        System.out.println();
        System.out.printf("Market rate:        $%s /sq ft (a block: $%s)%n",
                formatter.format(getAcquisitionCostPerSqFt()),
                formatter.format(getNextBlockCost()));
        if (ironDeposits > 0) {
            System.out.printf("Iron deposits:      %d, %s tonnes left%n",
                    ironDeposits, formatter.format(ironReserveTonnes));
        }
        System.out.println("\n--- on offer ---");
        for (LandParcel parcel : market.getListing()) {
            System.out.printf("  #%-4d %s%n", parcel.getId(), parcel.describe());
        }
        System.out.println();
        System.out.printf("Sale price:         $%s /sq ft%n", formatter.format(pricePerSqFt));
        System.out.printf("Margin:             $%s /sq ft%n", formatter.format(getMarginPerSqFt()));
        System.out.printf("Sold this month:    %s sq ft for $%s%n",
                formatter.format(sqFtSoldThisMonth), formatter.format(landSalesThisMonth));
        System.out.println("=========================================================\n");
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(4);
        formatter.setMinimumFractionDigits(0);
    }
}
