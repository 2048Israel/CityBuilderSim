package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The land office's window: ten plots on offer, and what the next one costs.
 *
 * WHY A LISTING RATHER THAN A PRICE
 *
 * Buying land used to be a button with a number on it. There was no decision in
 * it - the price only went up, so the answer was always "buy now or buy later",
 * and the only thing the player could get wrong was timing.
 *
 * Ten plots at once is a decision. They are different sizes at different prices,
 * and roughly one in five has iron under it, which is worth far more than the
 * ground but costs more up front. Buying the cheap one, buying the big one and
 * buying the one with the ore are three different plays.
 *
 * TWO PRICES, MOVED BY DIFFERENT THINGS
 *
 * What the CITY pays for a new parcel is a function of the city's size - a
 * bigger city annexes further out and is negotiating from a weaker position, so
 * every block owned and every thousand residents makes the next offer slightly
 * dearer. It does not care whether the city is full.
 *
 * What BUSINESSES pay the city per square foot is the opposite: pure supply
 * against demand inside the city limits. A city with empty blocks sells cheap; a
 * city with nothing spare sells dear. The player no longer sets this by hand -
 * it is a market now, and the way to make land cheap is to go and buy some.
 *
 * WHAT IS LISTED STAYS LISTED
 *
 * A parcel's price is fixed the moment it appears and never moves, so a player
 * can save up for the expensive one without it drifting away. Each parcel is
 * generated from its own id, so reloading a save cannot reroll the offers into
 * something better either.
 */
public class LandMarket {

    /** Plots on offer at any one time. */
    public static final int LISTING_SIZE = 10;

    /* ------------------------- what the city pays ------------------------- */

    /**
     * Ground price per square foot before any premium, in thousands.
     *
     * $0.70/sq ft, which is exactly what a block cost before parcels existed
     * ($70,000 for 100,000 sq ft). The opening of the game should feel the same.
     */
    private static final double BASE_PRICE_PER_SQ_FT = .0007;

    /**
     * Each block already owned makes the next offer this much dearer.
     *
     * Lower than the 2% the old per-block button charged, and it has to be: that
     * 2% was per PURCHASE, and a purchase was always exactly one block. A parcel
     * averages about two and a half, so charging the old rate per block owned
     * escalates two and a half times faster than the game was balanced for - a
     * city 500 blocks in was being quoted eleven times the going rate.
     */
    private static final double PREMIUM_PER_BLOCK_OWNED = .008;

    /** ...and so does each thousand residents. Deliberately small. */
    private static final double PREMIUM_PER_1000_PEOPLE = .05;

    /**
     * What the seller charges for the ore, per tonne in the ground, in thousands.
     *
     * $0.40 a tonne against an export price of $320 a tonne, so the ground is
     * changing hands at about a thousandth of what is under it. That sounds
     * generous until you price it against the mine: a three-million-tonne
     * deposit costs $1.2M, and a mine feeding local mills clears about $62k a
     * month, so the ground is roughly a year and a half of the mine's profit.
     * Expensive enough to be a decision, cheap enough to be a good one.
     */
    private static final double IRON_PRICE_PER_TONNE = .0004;

    /**
     * Land a single mine occupies, and therefore the room one deposit needs.
     *
     * Matches the Iron Mine template's landSqFt. A parcel offering more deposits
     * than it can physically hold mines would be selling the player a number
     * rather than a capability.
     */
    private static final double SQ_FT_PER_DEPOSIT = 400_000;

    /** Chance that a parcel with ore has one MORE site, each time it is asked. */
    private static final double EXTRA_DEPOSIT_CHANCE = .28;

    /** However big the tract, this many sites is the most it will ever carry. */
    private static final int MAX_DEPOSITS = 4;

    /* ------------------------- what businesses pay ------------------------- */

    /** The city's margin on land nobody is competing for. */
    private static final double BASE_MARKUP = .15;

    /** How much more a full city can charge on top of that. */
    private static final double SCARCITY_MARKUP = .90;

    /* ------------------------- how big a plot is ------------------------- */

    /**
     * The smallest thing the land office will sell, ever: one city block.
     *
     * The listing used to open with a band of 30,000-80,000 sq ft "infill"
     * slivers, which is a third of a block. They were cheap and they were the
     * thing an advisor or a hurried player clicked, so a city that needed real
     * room bought the same tiny plot over and over. Reaching 581 blocks in the
     * playtest took roughly two hundred separate purchases, and Jerus named that
     * the single biggest time sink in actually playing the game.
     */
    private static final double MIN_BLOCKS = 1;

    /**
     * Blocks the city must already own before the floor rises another block.
     *
     * A land office does not parcel out single blocks to a city that owns
     * hundreds; it sells the district. So the SMALLEST thing on offer grows with
     * the buyer - one block at the start, five once the city is around 160
     * blocks in, and up from there.
     *
     * Driven by blocks owned rather than population, deliberately. Population is
     * what a player thinks of as "how big is my city", but the problem this
     * solves is the number of times they have to click Buy, and that tracks how
     * much land the city gets through - which is this number.
     */
    private static final double BLOCKS_PER_FLOOR_STEP = 40;

    /**
     * A ceiling on the floor. Without one, a very large city eventually sees a
     * listing whose cheapest entry is a purchase it cannot make, which is a
     * worse failure than being offered scraps.
     */
    private static final double MAX_MIN_BLOCKS = 15;

    /** Smallest parcel currently on offer, in blocks. Recomputed each update(). */
    private double minBlocks = MIN_BLOCKS;

    /* ---------------------------- the parcels ---------------------------- */

    /**
     * Fixed seed. Every parcel is generated from SEED and its own id, so the
     * same id always produces the same plot - which is what lets the listing be
     * restored from a save without storing every field, and stops a reload from
     * being a reroll.
     */
    private static final long SEED = 705_398_211_733L;

    private final List<LandParcel> listing = new ArrayList<>();
    private int nextId = 1;

    /** Ground price per square foot right now, before any parcel's ore premium. */
    private double marketPricePerSqFt = BASE_PRICE_PER_SQ_FT;

    /** What businesses are charged. Derived, not set. */
    private double salePricePerSqFt = BASE_PRICE_PER_SQ_FT * (1 + BASE_MARKUP);

    /* ===================================================================
       PRICING
       =================================================================== */

    /**
     * Re-prices the market and tops the listing back up to ten.
     *
     * Called once a month and after every purchase. Existing parcels are never
     * touched: a new market price only affects plots listed from now on.
     *
     * @param ownedSqFt     everything the city has annexed
     * @param allocatedSqFt what is standing on or being built on
     * @param population    residents - the other half of "how big is this city"
     */
    public void update(double ownedSqFt, double allocatedSqFt, int population) {

        double blocksOwned = Math.max(0,
                (ownedSqFt - LandManager.STARTING_SQ_FT) / LandManager.BLOCK_SQ_FT);

        marketPricePerSqFt = BASE_PRICE_PER_SQ_FT
                * (1 + PREMIUM_PER_BLOCK_OWNED * blocksOwned)
                * (1 + PREMIUM_PER_1000_PEOPLE * population / 1000.0);

        // The floor under every plot listed from now on. Existing listings keep
        // the size they were listed at, exactly as they keep their price.
        minBlocks = Math.min(MAX_MIN_BLOCKS,
                MIN_BLOCKS + Math.floor(blocksOwned / BLOCKS_PER_FLOOR_STEP));

        /*
         * Supply against demand, inside the city.
         *
         * Utilisation is the honest measure of both at once: it is what share of
         * the city's land is already spoken for, so a city with room is a city
         * whose land nobody is fighting over. Priced off the market rate rather
         * than a constant so the sale price rises with acquisition costs on its
         * own - otherwise a mature city would eventually be selling at a loss.
         */
        double utilisation = (ownedSqFt > 0)
                ? Math.min(Math.max(allocatedSqFt / ownedSqFt, 0), 1)
                : 1;

        salePricePerSqFt = marketPricePerSqFt
                * (1 + BASE_MARKUP + SCARCITY_MARKUP * utilisation);

        while (listing.size() < LISTING_SIZE) {
            listing.add(generate(nextId++));
        }
    }

    /** Ground price per square foot the city would pay today, in thousands. */
    public double getMarketPricePerSqFt() { return marketPricePerSqFt; }

    /** What a business pays the city per square foot, in thousands. */
    public double getSalePricePerSqFt()   { return salePricePerSqFt; }

    /* ===================================================================
       GENERATING A PARCEL

       Deterministic in the id, so parcel 47 is the same plot in every session
       and in every reload of the same session. The market price is NOT part of
       that determinism - it is applied at listing time and then frozen into the
       parcel, which is why what is listed stays listed.
       =================================================================== */

    private LandParcel generate(int id) {

        Random random = new Random(scramble(SEED + id));

        double sizeSqFt = rollSize(random);
        int deposits = rollDeposits(random, sizeSqFt);
        double ironTonnes = rollTonnes(random, deposits);

        double price = sizeSqFt * marketPricePerSqFt
                + ironTonnes * IRON_PRICE_PER_TONNE;

        // Round to something a player can read. Nobody wants to compare
        // $103,847 against $98,211.
        price = Math.round(price / 5) * 5.0;

        return new LandParcel(id, sizeSqFt, price, ironTonnes, deposits);
    }

    /**
     * Plot sizes, as multiples of whatever the current floor is.
     *
     * The SHAPE is fixed - mostly modest, occasionally an enormous tract, because
     * the tract is what makes the listing worth reading. The SCALE moves with the
     * city, so the same listing that offers a young city one to eighteen blocks
     * offers a mature one five times that.
     *
     * NOTE ON DETERMINISM. Parcel size is now a function of the city at listing
     * time as well as of the parcel's id, so id alone no longer reproduces a
     * plot. That is already true of the price and for the same reason: both are
     * struck when the parcel is listed and then frozen into it. What is listed
     * still stays listed, and the listing is saved in full, so nothing a player
     * can see has become less stable.
     */
    private double rollSize(Random random) {

        double floorSqFt = minBlocks * LandManager.BLOCK_SQ_FT;

        int roll = random.nextInt(100);
        double multiple;
        if (roll < 55)      multiple = 1.0 + random.nextDouble() * 1.5;   // a plot
        else if (roll < 85) multiple = 2.5 + random.nextDouble() * 2.5;   // room to work
        else                multiple = 5.0 + random.nextDouble() * 13.0;  // a tract

        return round(floorSqFt * multiple);
    }

    /**
     * How many separate deposit sites are under this plot, if any.
     *
     * Big plots are likelier to hold ore - the deposits are out in open country,
     * not under the infill - which also means the parcels that cost the most to
     * buy are the ones worth the most to own.
     *
     * A parcel that has ore usually has one site. Beyond that, each further site
     * needs both luck and ROOM: a mine occupies 400,000 sq ft, so a plot that
     * cannot physically hold three mines has no business offering three deposits.
     * Capping by area is what keeps a rich strike from being an unusable one.
     */
    private int rollDeposits(Random random, double sizeSqFt) {

        double chance = .06 + .00000025 * sizeSqFt;   // small plots rarely, tracts often
        if (random.nextDouble() > Math.min(chance, .55)) {
            return 0;
        }

        int roomFor = (int) Math.max(1, Math.min(MAX_DEPOSITS, sizeSqFt / SQ_FT_PER_DEPOSIT));

        int deposits = 1;
        while (deposits < roomFor && random.nextDouble() < EXTRA_DEPOSIT_CHANCE) {
            deposits++;
        }
        return deposits;
    }

    /**
     * Ore in the ground, in tonnes, pooled across the parcel's deposits.
     *
     * Each site is sized in millions of tonnes against a mine that lifts about
     * 16,000 tonnes a year, so one site is a century or two of one mine and
     * rather less of four. Finite, but not something the player has to plan
     * around in their first hundred years.
     */
    private double rollTonnes(Random random, int deposits) {

        double tonnes = 0;
        for (int i = 0; i < deposits; i++) {
            tonnes += 1_500_000 + random.nextDouble() * 4_500_000;
        }
        return Math.round(tonnes / 50_000) * 50_000.0;
    }

    private double round(double sqFt) {
        return Math.round(sqFt / 1000) * 1000.0;
    }

    /**
     * Spreads consecutive ids into unrelated seeds.
     *
     * java.util.Random is a linear congruential generator: seed it with 1001,
     * 1002, 1003 and the first few values it hands back are strongly related.
     * Parcel ids ARE consecutive, so seeding it with them directly produced ten
     * plots in a row of almost the same size and never once any iron - the
     * listing looked broken because it was.
     *
     * This is the SplitMix64 finaliser, which is three multiplies and three
     * shifts and exists precisely to turn a counter into something that looks
     * random. Determinism is preserved, which is the whole reason for seeding
     * by id in the first place.
     */
    private static long scramble(long value) {
        long z = value + 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /* ===================================================================
       THE LISTING
       =================================================================== */

    /** The plots on offer, in the order they were listed. */
    public List<LandParcel> getListing() {
        return new ArrayList<>(listing);
    }

    public LandParcel find(int id) {
        for (LandParcel parcel : listing) {
            if (parcel.getId() == id) return parcel;
        }
        return null;
    }

    /** The cheapest thing on offer, for a caller that just wants some land. */
    public LandParcel cheapest() {
        LandParcel best = null;
        for (LandParcel parcel : listing) {
            if (best == null || parcel.getPrice() < best.getPrice()) {
                best = parcel;
            }
        }
        return best;
    }

    /** The best value per square foot that carries no ore premium. */
    public LandParcel bestValue() {
        LandParcel best = null;
        for (LandParcel parcel : listing) {
            if (parcel.hasIron()) continue;
            if (best == null || parcel.getPricePerSqFt() < best.getPricePerSqFt()) {
                best = parcel;
            }
        }
        return (best != null) ? best : cheapest();
    }

    /** The listed deposit with the most ore, or null if none is on offer. */
    public LandParcel richestDeposit() {
        LandParcel best = null;
        for (LandParcel parcel : listing) {
            if (!parcel.hasIron()) continue;
            if (best == null || parcel.getIronTonnes() > best.getIronTonnes()) {
                best = parcel;
            }
        }
        return best;
    }

    /**
     * Removes a parcel from the window. The caller has bought it.
     *
     * Does NOT refill - update() does that, so the replacement is priced against
     * the city as it stands after the purchase rather than before it.
     */
    public LandParcel take(int id) {
        LandParcel parcel = find(id);
        if (parcel != null) {
            listing.remove(parcel);
        }
        return parcel;
    }

    /* ===================================================================
       SAVE AND RESTORE

       The listing is written out in full rather than regenerated from the id
       counter. Regenerating would be smaller, but it would tie every existing
       save to the exact contents of rollSize() and rollIron() forever - change
       a weighting and every player's window silently reshuffles, including the
       parcel they were saving up for.
       =================================================================== */

    /** Fields written per parcel. Was 4 before deposits were counted. */
    private static final int FIELDS_PER_PARCEL = 5;

    /**
     * Marks a listing written with deposit counts, and says how wide it is.
     *
     * WHY A MARKER RATHER THAN ARITHMETIC. The obvious test is "does the payload
     * divide by five or by four", and it is wrong: a full ten-parcel listing in
     * the OLD format is forty values, which divides by both. It would have been
     * read back as eight parcels of nonsense - every field shifted, sizes read as
     * prices - and the shapes only disagree once the listing is short, so it
     * would have looked fine right up until it did not.
     *
     * Negative because the old format's first value is nextId, which is always at
     * least one. Nothing that was ever written can be mistaken for this.
     */
    private static final double LISTING_FORMAT_MARKER = -FIELDS_PER_PARCEL;

    public double[] getListingState() {

        double[] state = new double[2 + listing.size() * FIELDS_PER_PARCEL];
        state[0] = LISTING_FORMAT_MARKER;
        state[1] = nextId;

        int i = 2;
        for (LandParcel parcel : listing) {
            state[i++] = parcel.getId();
            state[i++] = parcel.getSizeSqFt();
            state[i++] = parcel.getPrice();
            state[i++] = parcel.getIronTonnes();
            state[i++] = parcel.getDeposits();
        }
        return state;
    }

    /**
     * Restores a listing written by this build OR by one before deposits existed.
     *
     * The old shape is still readable: any ore at all meant exactly one site back
     * then, which is precisely what the four-argument LandParcel constructor
     * assumes. Rejecting it instead would throw away the window a player was
     * saving up against, replacing their awaited tract with ten fresh strangers.
     *
     * @return false if the array is neither shape; nothing is changed
     */
    public boolean restoreListingState(double[] state) {

        if (state == null || state.length < 1) return false;

        boolean current = state[0] == LISTING_FORMAT_MARKER;
        int width = current ? FIELDS_PER_PARCEL : 4;
        int header = current ? 2 : 1;

        int payload = state.length - header;
        if (payload < 0 || payload % width != 0) return false;

        listing.clear();
        nextId = (int) state[header - 1];    // last header slot is nextId, either way

        for (int i = header; i + width - 1 < state.length; i += width) {
            listing.add(current
                    ? new LandParcel((int) state[i], state[i + 1], state[i + 2],
                                     state[i + 3], (int) state[i + 4])
                    : new LandParcel((int) state[i], state[i + 1], state[i + 2],
                                     state[i + 3]));
        }
        return true;
    }

    /** Smallest parcel the office is currently willing to sell, in blocks. */
    public double getMinBlocks() { return minBlocks; }

    public void reset() {
        listing.clear();
        nextId = 1;
        minBlocks = MIN_BLOCKS;
        marketPricePerSqFt = BASE_PRICE_PER_SQ_FT;
        salePricePerSqFt = BASE_PRICE_PER_SQ_FT * (1 + BASE_MARKUP);
    }
}
