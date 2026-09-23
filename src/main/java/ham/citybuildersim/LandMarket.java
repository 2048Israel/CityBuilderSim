package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The land office's window: nine plots on offer, and what the next one costs.
 *
 * WHY A LISTING RATHER THAN A PRICE
 *
 * Buying land used to be a button with a number on it. There was no decision in
 * it - the price only went up, so the answer was always "buy now or buy later",
 * and the only thing the player could get wrong was timing.
 *
 * Nine plots at once is a decision. They are different sizes at different prices,
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
 *
 * WHAT THE CITY PAYS IS US DOLLARS (0.7.6)
 *
 * Jerus: "when you buy land, make it so that it costs USD not domestic
 * currency". The world sells the city its ground, as it always implicitly
 * did, and is paid in its own money: the base and the premiums are dollar
 * figures now, a parcel's DOLLAR price is what is frozen at listing, and
 * what the treasury pays is that times the rate on the day it buys
 * (LandParcel.localPrice(), Game.buyLandParcel()). At the founding rate of
 * 1.00 every number is what it was. What BUSINESSES pay the city stays local
 * money and is struck exactly as before (basePricePerSqFt), and a currency
 * reform no longer touches the listing (redenominate()).
 */
public class LandMarket {

    /**
     * Plots on offer at any one time.
     *
     * NINE, so the shelf is a square. Jerus: "make it so its only 9 cards".
     * Ten wrapped to 3-3-3-1 at every window width the game is ever played at,
     * and the orphan on the last row read as an afterthought rather than as the
     * tenth of ten equal choices it was.
     *
     * An older save holding ten is trimmed to nine on restore, from the END of
     * the listing - the most recently generated offer, and so the one least
     * likely to be the tract somebody has been saving up for.
     */
    public static final int LISTING_SIZE = 9;

    /* ------------------------- what the city pays ------------------------- */

    /**
     * Ground price per square foot before any premium, in thousands of US dollars.
     *
     * $0.70/sq ft, which is exactly what a block cost before parcels existed
     * ($70,000 for 100,000 sq ft). The opening of the game should feel the same.
     *
     * IN THE WORLD'S MONEY SINCE 0.7.6: the seller is outside the city and is
     * paid in dollars, so no reform reaches this - like every foreign price
     * (Denomination) - and at the founding rate of 1.00 it is the $0.70 it was.
     */
    private static final double BASE_PRICE_PER_SQ_FT = .0007;

    /**
     * The same base in LOCAL money, reformed with every other price - what
     * the inside price is struck from, and nothing else.
     *
     * WHAT BUSINESSES PAY STAYS LOCAL MONEY (0.7.6), struck exactly as it
     * was: this base, the premiums, the scarcity. The rate does not reach it -
     * a developer inside the city does not care what the city paid the world
     * for the ground (what businesses pay, below) - so a fallen currency makes
     * the city's purchases dear without making its sales dearer, and the
     * margin (LandManager.getMarginPerSqFt()) carries the difference.
     */
    private double basePricePerSqFt = BASE_PRICE_PER_SQ_FT;

    /* ------------- THE TWO PREMIUMS ADD, THEY DO NOT MULTIPLY -------------

       They used to multiply, and multiplying two terms that both grow with the
       same city is a compound curve wearing a linear one's clothes. Measured
       over 600 months of an ordinary game:

           month  96    pop  16k   blocks   47    ->     4.1x base
           slot 7       pop  49k   blocks  186    ->    17.6x base
           month 576    pop 214k   blocks  581    ->   823.0x base  ($576/sq ft)

       823x is $576 a square foot, which is not a land market, it is a wall. By
       month 576 the ground under a House was 99% of what the House cost to build, so every residential
       template had collapsed into the same purchase - a plot of land with a
       roof thrown in - and no amount of fixing the housing MIX could matter
       while the things being chosen between had stopped differing.

       For scale, real ground per square foot: US pasture $0.044, farm average
       $0.10, cropland $0.134, Halifax residential $15-50, Manhattan about
       $2,700, central Tokyo $29k-$40k at the peak. This game's base is $0.70 -
       BASE_PRICE_PER_SQ_FT reads .0007 because money here is in thousands - so
       the honest ceiling for a city of 200,000 is tens of dollars, not
       hundreds.

       ADDING leaves a city that still gets dearer as it grows, at a rate a
       player can feel rather than one that ends the game. MEASURED after the
       change, on two 600-month runs:

           founding                     $0.46/sq ft   (base x the scarcity floor)
           month 241, 69,255 people    $25.69/sq ft
           month 601, 111,757 people   $30.95/sq ft
           month 576, 160,351 people   $45.90/sq ft   (66x base)

       Which is a real mid-size North American city - Halifax residential land
       runs $15-50 - and 87x under Manhattan. The number this replaced, $576,
       was San Francisco core prices in a town the size of Waterloo.

       Jerus: "outside land is cheaper, like the blocks get bigger, thats fine,
       but the cost per sq ft barely rises, still rises but not too much."

       Note what this does NOT change: the INSIDE price is still the outside
       price times scarcity, so buying land still makes land cheaper by exactly
       as much as it did. Flattening the outside curve flattens both together.
       ------------------------------------------------------------------- */

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
     * What the seller charges for the ore, per tonne in the ground, in thousands of US dollars.
     *
     * $0.40 a tonne against an export price of $320 a tonne, so the ground is
     * changing hands at about a thousandth of what is under it. That sounds
     * generous until you price it against the mine: a three-million-tonne
     * deposit costs $1.2M, and a mine feeding local mills clears about $62k a
     * month, so the ground is roughly a year and a half of the mine's profit.
     * Expensive enough to be a decision, cheap enough to be a good one.
     *
     * A dollar price since 0.7.6, like the ground's, and for the same reason
     * no longer reformed: the per-reform copy that stood beside it went with it.
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

    /* ------------------------- what businesses pay -------------------------

       TWO PRICES, TWO DIFFERENT MARKETS, and they were only nominally different
       before.

       OUTSIDE the city, the price of ground the city can annex rises with the
       city: more blocks owned and more people mean the next tract costs more.
       That is unchanged and it is what marketPricePerSqFt does.

       INSIDE the city, none of that matters. A developer does not care what the
       city paid for the ground; they care whether there is anywhere left to
       build. So the inside price is set by SCARCITY and nothing else: plenty of
       free land makes it cheap, a built-out city makes it dear.

       THE OLD VERSION MEASURED SCARCITY WITH UTILISATION, WHICH DOES NOT MOVE.
       Measured over 2,400 months of an ordinary game, utilisation sat between
       86% and 100% the entire time - the city only ever annexes when it has run
       out - so `SCARCITY_MARKUP * utilisation` was very nearly a constant. The
       markup ranged 85% to 105% across the whole run while both prices rose 17x
       together. The inside price was the outside price doubled, and the word
       "scarcity" was decoration.

       PRESSURE IS BUILT AGAINST FREE, which does move, and sharply:

           pressure = allocated / available

       At half built that is 1; at 90% it is 9; at 99% it is 99. The same city
       before and after annexing two blocks reads very differently, which is the
       entire point - buying land is supposed to make land cheaper.

       Saturating rather than linear, because pressure is unbounded and a linear
       response to 99 would price a nearly-full city off the map.
       ------------------------------------------------------------------- */

    /** Multiplier on acquisition cost when land is abundant. */
    private static final double SCARCITY_FLOOR = .65;

    /** Multiplier when there is effectively nothing left. */
    private static final double SCARCITY_CEILING = 1.90;

    /**
     * Pressure at which the curve is half way up.
     *
     * Sets where the city stops making money on land: the multiplier passes 1.0
     * at a pressure of about 1.6, which is roughly 61% built. Above that - which
     * is where ordinary play sits - the city profits on every sale. Below it,
     * a city that has annexed far more than it can use is selling ground for
     * less than it paid, which is the whole point of letting the margin go
     * negative. Over-buying should cost something.
     */
    private static final double SCARCITY_MIDPOINT = 4.0;

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

    /**
     * Ground price per square foot right now, before any parcel's ore premium,
     * in LOCAL money at the founding rate - the anchor the inside price is
     * struck from (basePricePerSqFt), and since 0.7.6 nothing else.
     */
    private double marketPricePerSqFt = BASE_PRICE_PER_SQ_FT;

    /**
     * ...and what the world asks for the same ground, in thousands of US
     * dollars (0.7.6): every parcel listed from now on is priced off this, and
     * it is the figure the history keeps as landPrice.
     */
    private double groundUsdPerSqFt = BASE_PRICE_PER_SQ_FT;

    /** What businesses are charged. Derived, not set. */
    private double salePricePerSqFt = BASE_PRICE_PER_SQ_FT * SCARCITY_FLOOR;

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

        // ADDED, not multiplied. See THE TWO PREMIUMS ADD above.
        double premiums = 1 + PREMIUM_PER_BLOCK_OWNED * blocksOwned
                            + PREMIUM_PER_1000_PEOPLE * population / 1000.0;
        // The same premiums on both bases: the world's price in dollars, which
        // the listing is priced from, and the local anchor the inside price
        // is struck from. Equal at the founding rate and unit.
        groundUsdPerSqFt   = BASE_PRICE_PER_SQ_FT * premiums;
        marketPricePerSqFt = basePricePerSqFt * premiums;

        // The floor under every plot listed from now on. Existing listings keep
        // the size they were listed at, exactly as they keep their price.
        minBlocks = Math.min(MAX_MIN_BLOCKS,
                MIN_BLOCKS + Math.floor(blocksOwned / BLOCKS_PER_FLOOR_STEP));

        salePricePerSqFt = marketPricePerSqFt * scarcityMultiplier(ownedSqFt, allocatedSqFt);

        while (listing.size() < LISTING_SIZE) {
            listing.add(generate(nextId++));
        }
    }

    /**
     * How dear inside land is, as a multiple of what the ground cost outside.
     *
     * ANCHORED TO ACQUISITION COST, and that is deliberate rather than lazy. The
     * alternative - a fixed base scaled only by scarcity - was measured and
     * rejected: it leaves the inside price roughly flat for the whole game while
     * the outside price rises 17x, so a mature city buys at $13 and sells at
     * $1.50 on every square foot forever. That is not a bet, it is a leak.
     *
     * Anchoring keeps the two prices in the same band so the MARGIN can go
     * either way, which is what makes annexation a judgement call. Scarcity
     * decides which way:
     *
     *     ~60% built and falling  ->  below cost, the city eats the difference
     *     ~90% built              ->  about 1.5x cost
     *     nearly full             ->  approaching 1.9x
     *
     * So "the more land available, the cheaper for investors" holds exactly, and
     * a city that over-annexes pays for the privilege.
     */
    public double scarcityMultiplier(double ownedSqFt, double allocatedSqFt) {

        double available = ownedSqFt - allocatedSqFt;

        // Nothing left at all. Not a divide-by-zero - it is the most expensive
        // the land can possibly be, which is what the ceiling is for.
        if (available <= 0) {
            return SCARCITY_CEILING;
        }
        if (allocatedSqFt <= 0) {
            return SCARCITY_FLOOR;
        }

        double pressure = allocatedSqFt / available;

        return SCARCITY_FLOOR + (SCARCITY_CEILING - SCARCITY_FLOOR)
                * (pressure / (pressure + SCARCITY_MIDPOINT));
    }

    /**
     * The inside price's anchor: the ground price per square foot in local
     * money at the founding rate, in thousands. Not what the city pays since
     * 0.7.6 - that is getGroundUsdPerSqFt() at today's rate.
     */
    public double getMarketPricePerSqFt() { return marketPricePerSqFt; }

    /** Ground price per square foot the world asks today, in thousands of US dollars (0.7.6). */
    public double getGroundUsdPerSqFt()   { return groundUsdPerSqFt; }

    /** What a business pays the city per square foot, in thousands. */
    public double getSalePricePerSqFt()   { return salePricePerSqFt; }

    /* ===================================================================
       GENERATING A PARCEL

       Deterministic in the id, so parcel 47 is the same plot in every session
       and in every reload of the same session. The market price is NOT part of
       that determinism - it is applied at listing time and then frozen into the
       parcel, which is why what is listed stays listed. In US dollars since
       0.7.6: the rate the city will pay it at is the day's, not the listing's.
       =================================================================== */

    private LandParcel generate(int id) {

        Random random = new Random(scramble(SEED + id));

        double sizeSqFt = rollSize(random);
        int deposits = rollDeposits(random, sizeSqFt);
        double ironTonnes = rollTonnes(random, deposits);

        double priceUsd = sizeSqFt * groundUsdPerSqFt
                + ironTonnes * IRON_PRICE_PER_TONNE;

        // Round to something a player can read. Nobody wants to compare
        // US$103,847 against US$98,211.
        priceUsd = Math.round(priceUsd / 5) * 5.0;

        return new LandParcel(id, sizeSqFt, priceUsd, ironTonnes, deposits);
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
            if (best == null || parcel.getPriceUsd() < best.getPriceUsd()) {
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
            if (best == null || parcel.getUsdPerSqFt() < best.getUsdPerSqFt()) {
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
     * Marks a listing written with deposit counts, and says how wide it is -
     * in LOCAL money, as every listing was until 0.7.6 (USD_LISTING_MARKER).
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

    /**
     * Marks a listing whose prices are US DOLLARS (0.7.6), as wide as the one
     * before it. A marker rather than a stamp elsewhere in the save, for the
     * reason the first one is: the array says what it is. Anything carrying
     * LISTING_FORMAT_MARKER, or no marker at all, was written in local money.
     */
    private static final double USD_LISTING_MARKER = -100 - FIELDS_PER_PARCEL;

    /**
     * Ids restored from an older listing whose prices are still LOCAL money,
     * waiting for settleLocalPrices() to read them as dollars at the loading
     * rate - which the load path has only once the foreign accounts are back.
     */
    private final java.util.Set<Integer> localIds = new java.util.HashSet<>();

    /** True when the price state came back without a dollar ground price (an older save). */
    private boolean groundFromLocal;

    public double[] getListingState() {

        double[] state = new double[2 + listing.size() * FIELDS_PER_PARCEL];
        state[0] = USD_LISTING_MARKER;
        state[1] = nextId;

        int i = 2;
        for (LandParcel parcel : listing) {
            state[i++] = parcel.getId();
            state[i++] = parcel.getSizeSqFt();
            state[i++] = parcel.getPriceUsd();
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
     * THREE SHAPES since 0.7.6: the dollar listing this build writes, and
     * the two older ones, whose prices are LOCAL money. Those are restored as
     * written and remembered (localIds); settleLocalPrices() reads them as
     * dollars at the rate of the day the save is loaded, once the load path
     * has that rate.
     *
     * @return false if the array is none of the shapes; nothing is changed
     */
    public boolean restoreListingState(double[] state) {

        if (state == null || state.length < 1) return false;

        boolean usd = state[0] == USD_LISTING_MARKER;
        boolean current = usd || state[0] == LISTING_FORMAT_MARKER;
        int width = current ? FIELDS_PER_PARCEL : 4;
        int header = current ? 2 : 1;

        int payload = state.length - header;
        if (payload < 0 || payload % width != 0) return false;

        listing.clear();
        localIds.clear();
        nextId = (int) state[header - 1];    // last header slot is nextId, either way

        for (int i = header; i + width - 1 < state.length; i += width) {
            listing.add(current
                    ? new LandParcel((int) state[i], state[i + 1], state[i + 2],
                                     state[i + 3], (int) state[i + 4])
                    : new LandParcel((int) state[i], state[i + 1], state[i + 2],
                                     state[i + 3]));
            if (!usd) localIds.add((int) state[i]);
        }
        // A save written when the shelf held ten. Trim from the back rather
        // than leaving a tenth card the screen has no room for - see
        // LISTING_SIZE. update() refills if this ever runs the other way.
        while (listing.size() > LISTING_SIZE) listing.remove(listing.size() - 1);
        return true;
    }

    /**
     * AN OLDER SAVE'S PRICES WERE LOCAL MONEY, and this reads them as US
     * dollars at the rate of the day the save is loaded (0.7.6): each listed
     * parcel's price over the rate, so the local cost the player saw is
     * exactly what it costs on the day of loading - and from then on it is a
     * dollar price like any other, dearer as the currency falls. The same for
     * the office's quote, when the price state came back without a dollar
     * ground price. Called by the load path once the foreign accounts are
     * back, and by nothing else; a no-op on a dollar listing.
     *
     * @return how many parcels were converted
     */
    public int settleLocalPrices(double rate) {
        if (!(rate > 0) || !Double.isFinite(rate)) rate = ForeignAccounts.OPENING_RATE;
        int converted = 0;
        for (int i = 0; i < listing.size(); i++) {
            LandParcel p = listing.get(i);
            if (!localIds.contains(p.getId())) continue;
            listing.set(i, new LandParcel(p.getId(), p.getSizeSqFt(), p.getPriceUsd() / rate,
                    p.getIronTonnes(), p.getDeposits()));
            converted++;
        }
        localIds.clear();
        if (groundFromLocal) groundUsdPerSqFt = marketPricePerSqFt / rate;
        groundFromLocal = false;
        return converted;
    }

    /** Smallest parcel the office is currently willing to sell, in blocks. */
    public double getMinBlocks() { return minBlocks; }

    /*
     * THE OFFICE'S PRICES ARE STATE, and the listing above did not carry them.
     * update() re-strikes them each month from the stock and the land the
     * city holds, and the load path deliberately does not run update() - so
     * until 2026-09-10 a reloaded city read the FOUNDING seeds for a month:
     * the ground price 0.11274 -> 0.00070 (the BASE_PRICE_PER_SQ_FT literal,
     * 160x too cheap) on the Land Office and in the build screen's "buying N
     * blocks costs roughly X", and minBlocks 15 -> 1. Worse, the margin the
     * screen reports is the city's own price LESS this one, and the city's
     * price IS restored - so the margin flipped sign on load, from selling
     * below cost to a comfortable profit. Carried now, beside the listing
     * rather than inside it, so the listing's own format need not move.
     *
     * The dollar ground price rides the end since 0.7.6 (slot 3); an older
     * save has three slots, and its local quote is read as dollars at the
     * loading rate by settleLocalPrices(), like its listing.
     */
    public double[] getPriceState() {
        return new double[] { marketPricePerSqFt, salePricePerSqFt, minBlocks, groundUsdPerSqFt };
    }

    public void restorePriceState(double[] state) {
        if (state == null || state.length < 3) return;
        if (state[0] > 0) marketPricePerSqFt = state[0];
        if (state[1] > 0) salePricePerSqFt = state[1];
        if (state[2] > 0) minBlocks = state[2];
        if (state.length > 3 && state[3] > 0) {
            groundUsdPerSqFt = state[3];
            groundFromLocal = false;
        } else {
            groundFromLocal = state[0] > 0;
        }
    }

    public void reset() {
        listing.clear();
        localIds.clear();
        groundFromLocal = false;
        nextId = 1;
        minBlocks = MIN_BLOCKS;
        marketPricePerSqFt = basePricePerSqFt;
        groundUsdPerSqFt = BASE_PRICE_PER_SQ_FT;
        salePricePerSqFt = basePricePerSqFt * SCARCITY_FLOOR;
    }

    /**
     * The office's LOCAL prices in the new unit - the inside price and the
     * anchor it is struck from - and nothing else.
     *
     * THE LISTING IS LEFT ALONE SINCE 0.7.6. It used to be cleared here:
     * LandParcel is immutable and its price was local money, so a reform could
     * not reprice it and threw the board away instead, the tract the player
     * was saving for with it. Parcels are priced in US dollars now, which no
     * act of this city's parliament can change (Denomination: foreign prices
     * are the exception), so the board survives the reform as listed, and
     * what it costs in local money moves because the RATE was divided. The
     * dollar ground price and the ore's dollar price stay put for the same
     * reason.
     */
    public void redenominate(double scale) {
        basePricePerSqFt   *= scale;
        marketPricePerSqFt *= scale;
        salePricePerSqFt   *= scale;
    }


    /**
     * Re-seeds the money CONSTANTS at a given unit. See Denomination. The
     * local anchor only: the dollar base and the ore's price are the world's.
     */
    public void seedConstants(double unit) {
        basePricePerSqFt  = BASE_PRICE_PER_SQ_FT / unit;
    }

}
