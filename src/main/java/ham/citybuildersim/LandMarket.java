package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's edge, or none while that side has no room - and what the ground costs.
 *
 * WHY A LISTING RATHER THAN A PRICE
 *
 * Buying land used to be a button with a number on it. There was no decision in
 * it - the price only went up, so the answer was always "buy now or buy later",
 * and the only thing the player could get wrong was timing.
 *
 * Many offers at once is a decision. They are different sizes at different
 * prices, some wet and some with ore under them, which is worth far more than
 * the ground but costs more up front. Buying the cheap one, buying the big one
 * and buying the one with the ore are three different plays.
 *
 * ON THE WORLD SINCE 0.7.57 (batch J1b; the project's spec-land.md 2.2). The
 * nine parcels the office drew from a generator seeded with their ids are
 * gone. The city stands on the world (CityLand). From 0.7.57 to 0.7.66 each of
 * forty lanes of wedges had one offer standing, the next band of its lane,
 * sized by a multiple drawn from the offer's id.
 *
 * ON THE BLOCK GRID SINCE 0.7.67 (batch M3; the project's spec-grid.md 2.2).
 * Jerus, 2026-10-07: six offers a side, never rerolled. Each side has six
 * places, left to right facing out, and each place one offer standing: a
 * rectangle of whole blocks of the city's level against its edge, listed by
 * GridOffers (the seed, the rectangle, the clip, the trim, 2:1), or none
 * while the side has no room for it, listed again after every purchase and
 * every month until it has (an empty place, null). Its ground is every plot of
 * the rectangle the city does not own yet, counted plot by plot when it is
 * listed (CityLand.groundOf(): exact at every size), and its fields whole
 * (CityLand.fieldsOn()). An offer is never rerolled; buying it lists its
 * place's next - the innermost free ground of its lane - and the other
 * twenty-three do not move, because standing rectangles never meet. Priced by
 * price(): the ground at the world's dollar price, water cheaper, and each
 * resource the city can use at a share of its world price (spec-land star 6).
 *
 * TWO PRICES, MOVED BY DIFFERENT THINGS
 *
 * What the CITY pays for a new parcel is a function of how CROWDED the city is
 * (since 0.7.55): its people per square kilometre of the land it owns. A city
 * packed onto its ground is quoted dear for the next tract, one spread over
 * plenty is quoted cheap - and two cities equally crowded are quoted the same,
 * whatever their size. Until 0.7.55 it was the city's SIZE, every block owned
 * and every thousand residents, which at ten billion people priced land at
 * about 19,500 times today's (the crowding premium, below). It does not care
 * whether the city is full.
 *
 * What BUSINESSES pay the city per square foot is the opposite: pure supply
 * against demand inside the city limits. A city with empty blocks sells cheap; a
 * city with nothing spare sells dear. The player no longer sets this by hand -
 * it is a market now, and the way to make land cheap is to go and buy some.
 *
 * WHAT IS LISTED STAYS LISTED
 *
 * An offer's price is fixed the moment it appears and never moves, so a player
 * can save up for the expensive one without it drifting away. Offers are saved
 * whole (SAVE AND RESTORE, below), so reloading a save cannot reroll them into
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
 *
 * ...AND IT FOLLOWS THE DOLLAR'S OWN INFLATION (0.7.55)
 *
 * Jerus: "tie it to inflation as well, usd inflation not domestic
 * inflation". The dollar price is the base times the world's price level
 * (WorldEconomy.getPriceLevel(), what US prices have done since the
 * founding) times the crowding premium; what the treasury pays is that times
 * the rate, as since 0.7.6, so the city's own inflation reaches it only
 * through the rate.
 */
public class LandMarket {

    /**
     * Offers standing on each side of the city: six, one a place
     * (GridOffers.PLACES; Jerus, 2026-10-07, "six offers a side"). From
     * 0.7.57 to 0.7.66 ten, one a lane; the nine-card shelf before them:
     * Jerus, "make it so its only 9 cards".
     */
    public static final int OFFERS_A_SIDE = GridOffers.PLACES;

    /** Offers standing in all: OFFERS_A_SIDE on each of the four sides, twenty-four. */
    public static final int OFFERS = CityLand.SIDES * OFFERS_A_SIDE;

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

    /** The ground's price a square foot at the founding, in thousands of US dollars: BASE_PRICE_PER_SQ_FT, before any premium - what an Insane city owes for its starting land (Founding.landBondUsd(), 0.7.14). */
    public static double openingUsdPerSqFt() { return BASE_PRICE_PER_SQ_FT; }

    /**
     * The same base in LOCAL money, reformed with every other price and
     * struck at the expected price level since 0.7.42 (seedConstants()) -
     * what the inside price is struck from, and nothing else.
     *
     * WHAT BUSINESSES PAY STAYS LOCAL MONEY (0.7.6), struck exactly as it
     * was: this base, the premiums, the scarcity. The rate does not reach it -
     * a developer inside the city does not care what the city paid the world
     * for the ground (what businesses pay, below) - so a fallen currency makes
     * the city's purchases dear without making its sales dearer, and the
     * margin (LandManager.getMarginPerSqFt()) carries the difference.
     *
     * STILL THE DOMESTIC ANCHOR SINCE 0.7.55, with the crowding premium in
     * place of the size premiums. The other way was one price: the dollar
     * price at the rate, times the scarcity. It was not taken. Ground inside
     * the city is a domestic thing bought with domestic money, so its price
     * follows the price level the city expects, as every other price struck
     * from a constant does; one price would put every swing of the rate into
     * every business's ground. And it would not have been continuous: Jerus's
     * currency stands about 1.9 times above parity (his expected price level
     * 16.81 against the world's 1.137, at a rate of 7.89), so one price would
     * have cut what his businesses pay for ground by 47% on the day (1.94 a
     * square foot to 1.03, in thousands).
     * The two prices are one at parity - this base at the expected price
     * level, the dollar's at the world's times the rate - so the margin is
     * the currency's distance from parity and the scarcity, nothing else.
     */
    private double basePricePerSqFt = BASE_PRICE_PER_SQ_FT;

    /* -------------------- THE CROWDING PREMIUM (0.7.55) --------------------

       Jerus: "Yes for land price do that" - the scale study's proposal to
       make the premium depend on density rather than size (the project's
       spec-scale.md, section 8, step 2).

       WHAT IT REPLACED. The premium was 1 + .008 per block owned + .05 per
       thousand residents: linear in the city's absolute size. A copy of
       Jerus's city ten times over (5.1M people) saw rents x3.9 and lost 18%
       of its people in 30 months, and at ten billion a square foot cost about
       19,500 times what it costs him today, while the same copies priced at
       his size tracked his city. A city's size is not what makes its ground
       dear; how hard it presses on it is.

       NOW: premium = 1 + (CROWDING_CEILING - 1) x s, where
       s = 1 / (1 + (CROWDING_MIDPOINT / d)^CROWDING_STEEPNESS) and d is the
       city's people per square kilometre of the land it owns. d is the
       people per square kilometre BUILT ON times the share built on, so both
       read through it; the share built on reads a second time inside the city
       only, in the scarcity multiplier below, which is a ratio and so
       already scale-free. A copy of a city K times over - K times the people
       on K times the land - is quoted exactly what the city is.

       THE CURVE IS FITTED TO THE THREE CITIES WE HOLD, so the premium barely
       moves on the day: at its density each pays about the premium its size
       used to give it (2026-10-06, the last saves of each, re-struck by
       this build; size premium -> crowding premium):

           city600     5,978 people   1,972 /km2      3.67  ->    3.74
           city2400  112,858 people   3,391 /km2     35.07  ->   35.99
           Jerus     509,455 people   5,684 /km2    103.39  ->  104.00

       What the world asks in dollars moves by more, because it now carries
       the world's price level as well (1.174, 1.166 and 1.137 in those saves):
       +19.5%, +19.6% and +14.4%. What businesses pay moves with the premium
       alone: +1.9%, +2.6% and +0.7%.

       Cheap until a town is crowded (1.65x at 1,500 /km2), steep through the
       densities the played cities reach, and SATURATING: past about 8,000 a
       square kilometre there is little left to add, and no city pays more
       than CROWDING_CEILING, however packed - where the size premium had no
       ceiling at all.

       THE OLD REASONING, KEPT BECAUSE ITS NUMBERS ARE THE SCALE THE CURVE IS
       STILL HELD TO: the size premiums used to multiply, and multiplying two
       terms that both grow with the same city is a compound curve wearing a
       linear one's clothes. Measured over 600 months of an ordinary game:

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
     * People per square kilometre of the city's land at which the crowding
     * premium is half way to its ceiling (0.7.55): about 60x the base.
     *
     * Fitted, with the two below, to the three cities above. city2400 and
     * the playtest's city in its last two thousand months sit at 3,000 to
     * 3,800; Jerus's at 5,684.
     */
    public static final double CROWDING_MIDPOINT = 4_000;

    /**
     * How sharply the premium climbs through the midpoint: the curve's power
     * (0.7.55). At 5.3 a city ten per cent more crowded than the midpoint is
     * quoted 24% more - so buying ground, which spreads the same people over
     * more of it, makes the next tract cheaper.
     */
    public static final double CROWDING_STEEPNESS = 5.3;

    /**
     * The most crowding can multiply the ground's price by, however crowded
     * the city (0.7.55). With the base at $0.70 that is $84 a square foot at
     * the founding's US prices - Halifax runs $15-50, Manhattan about $2,700;
     * see below.
     */
    public static final double CROWDING_CEILING = 120;

    /**
     * What a square kilometre of fresh water sells for, against dry ground:
     * 45% (the map mockup's figure, spec-land star 6). Water cannot be built
     * on, but a lake or a river is what a water plant draws from (batch J2).
     */
    public static final double FRESH_PRICE_SHARE = 0.45;

    /** ...and of sea: 8% (the mockup's), the reach a desalination plant needs and nothing else does. */
    public static final double SEA_PRICE_SHARE = 0.08;

    /*
     * THE ORE UNDER AN OFFER is priced at its resource's share of its good's
     * world export price, at the world's price level (Resource.inGroundShare()):
     * iron at 1/350 of the floor of US$140 a tonne, which is the $0.40 a
     * tonne in the ground the office charged since parcels carried ore - "a
     * three-million-tonne deposit costs $1.2M, and a mine feeding local mills
     * clears about $62k a month, so the ground is roughly a year and a half of
     * the mine's profit". A resource with no good yet is free. Until 0.7.57 the
     * office drew an offer's sites (one to four, each needing room for a
     * mine's 400,000 sq ft) and tonnes (1.5 to 6 Mt a site) itself; they are
     * the world's fields now (Resource, World).
     */

    /* ------------------------- what businesses pay -------------------------

       TWO PRICES, TWO DIFFERENT MARKETS, and they were only nominally different
       before.

       OUTSIDE the city, the price of ground the city can annex rises as the
       city crowds onto its land (THE CROWDING PREMIUM above, since 0.7.55;
       before it, more blocks owned and more people made the next tract
       dearer). That is what marketPricePerSqFt carries.

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

    /* ---------------------------- the offers ---------------------------- */

    /** The twenty-four standing, one a place, side by side and place by place; null where a place waits empty. */
    private final LandParcel[] offers = new LandParcel[OFFERS];
    private int nextId = 1;

    /** The city's land the offers stand round; none on a bare office, which lists nothing. */
    private CityLand land;

    /** The offers' rectangles on the land's grid, as GridOffers lists and clips them; null on a bare office. */
    private GridOffers places;

    /** The place an offer was last taken from, listed first when the shelf is filled again (GridOffers.relist()'s order); -1 for none. */
    private int takenFrom = -1;

    /** The month the office lists in, for each offer's record. */
    private int month;

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

    /**
     * THE PRICE'S PARTS, AS STRUCK (0.7.55): the city's crowding - people
     * per square kilometre of its land - the premium it came to, and the
     * world's price level, so groundUsdPerSqFt is exactly
     * BASE_PRICE_PER_SQ_FT x usPriceLevel x crowdingPremium and the land
     * office can say so. Saved with the prices (getPriceState()): the load
     * path does not re-strike them.
     */
    private double crowding;
    private double crowdingPremium = 1;
    private double usPriceLevel = 1;

    /** What businesses are charged. Derived, not set. */
    private double salePricePerSqFt = BASE_PRICE_PER_SQ_FT * SCARCITY_FLOOR;

    /* ===================================================================
       PRICING
       =================================================================== */

    /**
     * Re-prices the market and lists the next offer of any place without one.
     *
     * Called once a month and after every purchase. Existing offers are never
     * touched: a new market price only affects offers listed from now on.
     *
     * At the founding's US prices: a land office with no world behind it,
     * what the harnesses sample.
     *
     * @param ownedSqFt     everything the city has annexed
     * @param allocatedSqFt what is standing on or being built on
     * @param population    residents - over the land, how crowded the city is
     */
    public void update(double ownedSqFt, double allocatedSqFt, long population) {
        update(ownedSqFt, allocatedSqFt, population, 1);
    }

    /**
     * ...and at the world's price level (0.7.55): US prices against the
     * founding's, WorldEconomy.getPriceLevel() - the dollar's own inflation,
     * which the world's price follows and the city's does not.
     */
    public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices) {
        update(ownedSqFt, allocatedSqFt, population, usPrices, month);
    }

    /** ...in a month, which each offer listed now records (0.7.57). */
    public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices, int month) {

        this.month = month;

        // CROWDED, not big (0.7.55). See THE CROWDING PREMIUM above.
        crowding = peoplePerKm2(population, ownedSqFt);
        crowdingPremium = crowdingPremium(crowding);
        usPriceLevel = usPrices > 0 && Double.isFinite(usPrices) ? usPrices : 1;
        // The same premium on both bases: the world's price in dollars, which
        // the listing is priced from, at the world's price level; and the
        // local anchor the inside price is struck from, at the price level
        // the city expects (seedConstants()). Equal at the founding rate and
        // unit, and one at parity (basePricePerSqFt).
        groundUsdPerSqFt   = BASE_PRICE_PER_SQ_FT * usPriceLevel * crowdingPremium;
        marketPricePerSqFt = basePricePerSqFt * crowdingPremium;

        salePricePerSqFt = marketPricePerSqFt * scarcityMultiplier(ownedSqFt, allocatedSqFt);

        // The places that have no offer, at the city's level now (a place
        // with no room waits). Offers standing keep the ground and the price
        // they were listed at.
        listMissing();
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

    /** The local anchor itself, before the premiums: BASE_PRICE_PER_SQ_FT in today's unit at the expected price level (0.7.42, Game.restrikeMoneyConstants()) - what ExpectationsCheck reads. */
    public double getBasePricePerSqFt() { return basePricePerSqFt; }

    /** Ground price per square foot the world asks today, in thousands of US dollars (0.7.6). */
    public double getGroundUsdPerSqFt()   { return groundUsdPerSqFt; }

    /** What a business pays the city per square foot, in thousands. */
    public double getSalePricePerSqFt()   { return salePricePerSqFt; }

    /**
     * A city's crowding (0.7.55): its people per square kilometre of the land
     * it owns - the measure the premium reads. 0 with nobody; with people and
     * no land at all, infinitely crowded, which the premium reads as its
     * ceiling.
     */
    public static double peoplePerKm2(long population, double ownedSqFt) {
        if (population <= 0) return 0;
        double km2 = LandManager.km2(ownedSqFt);
        return km2 > 0 ? population / km2 : Double.POSITIVE_INFINITY;
    }

    /**
     * What crowding multiplies the ground's price by (0.7.55): 1 for an empty
     * city, CROWDING_CEILING at the limit, half way at CROWDING_MIDPOINT. See
     * THE CROWDING PREMIUM. Written as 1 / (1 + (midpoint / d)^power) so an
     * infinitely crowded city reads 1, not infinity over infinity.
     */
    public static double crowdingPremium(double peoplePerKm2) {
        if (!(peoplePerKm2 > 0)) return 1;
        double share = 1 / (1 + Math.pow(CROWDING_MIDPOINT / peoplePerKm2, CROWDING_STEEPNESS));
        return 1 + (CROWDING_CEILING - 1) * share;
    }

    /** The crowding the ground was last priced at: people per square kilometre of the city's land (0.7.55); 0 until a city's first month on 0.7.55. */
    public double getCrowding()           { return crowding; }

    /** ...the premium it came to: what crowding multiplies the ground's price by (0.7.55). */
    public double getCrowdingPremium()    { return crowdingPremium; }

    /** ...and the world's price level the dollar price was struck at: US prices against the founding's (0.7.55). */
    public double getUsPriceLevel()       { return usPriceLevel; }

    /* ===================================================================
       LISTING AN OFFER (0.7.57; on the block grid since 0.7.67)

       Each place's offer is the rectangle GridOffers lists for it against
       the offers standing (spec-grid star 4), its ground counted plot by
       plot on the world when it is listed (CityLand.groundOf()) and priced
       then (price()) - frozen into the offer, which is why what is listed
       stays listed. Ids are handed out in the order places are listed - the
       place just bought first, then every empty one North 1 to West 6 - so
       the same city listing the same places in the same order sees the same
       offers. In US dollars since 0.7.6: the rate the city will pay it at is
       the day's, not the listing's.
       =================================================================== */

    /** Puts the office on a city's land with nothing on its shelf: what LandManager does when a city is founded, converted, loaded or restated. */
    void attach(CityLand land) {
        this.land = land;
        this.places = land == null ? null : new GridOffers(land.grid(), land.siteX(), land.siteY());
        this.takenFrom = -1;
        java.util.Arrays.fill(offers, null);
    }

    /** The city's land the offers stand round, or null on a bare office. */
    public CityLand getLand() { return land; }

    /** Lists every place without an offer - the one an offer was just taken from first, then North 1 to West 6 - at the prices last struck; a place with no room waits empty. */
    void listMissing() {
        if (land == null) return;
        if (places == null || places.grid() != land.grid()) restand();
        if (takenFrom >= 0 && offers[takenFrom] == null) listIn(takenFrom);
        takenFrom = -1;
        for (int i = 0; i < OFFERS; i++) if (offers[i] == null) listIn(i);
    }

    /** Lists place i (side i / OFFERS_A_SIDE, place i % OFFERS_A_SIDE) and stands it, or leaves it waiting. */
    private void listIn(int i) {
        int side = i / OFFERS_A_SIDE, place = i % OFFERS_A_SIDE;
        GridOffers.Rect r = places.list(side, place);
        places.stand(side, place, r);
        offers[i] = r == null ? null : measure(r);
    }

    /** The offers standing, stood again on a grid of the land's: what a restore or a hand-made listing goes through. */
    private void restand() {
        places = new GridOffers(land.grid(), land.siteX(), land.siteY());
        for (int i = 0; i < OFFERS; i++) {
            LandParcel p = offers[i];
            if (p != null) places.stand(i / OFFERS_A_SIDE, i % OFFERS_A_SIDE, p.rect());
        }
    }

    /**
     * An offer of a rectangle: its ground the plots of it the city does not
     * own, counted on the world (CityLand.groundOf()), the fields on that
     * ground (CityLand.fieldsOn()) and its forest's timber, priced at the
     * ground's dollar price struck last (price()), under a new id. Not put
     * on the shelf - listMissing() does that.
     */
    LandParcel measure(GridOffers.Rect r) {
        int id = nextId++;
        double[] km2 = land.groundOf(r.x0(), r.y0(), r.x1(), r.y1());
        int[] sites = new int[CityLand.KINDS];
        double[] amounts = new double[CityLand.KINDS];
        land.fieldsOn(r.x0(), r.y0(), r.x1(), r.y1(), sites, amounts);
        amounts[Resource.FOREST.ordinal()] = Math.rint(km2[CityLand.FOREST] * World.FOREST_M3_PER_KM2);
        double usd = price(km2, amounts, groundUsdPerSqFt, usPriceLevel);
        return new LandParcel(id, r.side(), r.place(), r.level(), r.x0(), r.y0(), r.x1(), r.y1(), km2, sites, amounts, usd, month);
    }

    /** The city's block level now: LandGrid.levelFor() of every plot it owns - what a new offer's blocks are, or one finer (spec-grid star 2); 0 on a bare office. */
    public int getLevel() { return land == null ? 0 : land.level(); }

    /**
     * An offer's price, in thousands of US dollars (spec-land star 6): the
     * ground's dollar price a square foot on its dry ground, FRESH_PRICE_SHARE
     * of it on its fresh water and SEA_PRICE_SHARE on its sea; and each
     * resource the city can use at its share of its good's world export
     * price, at the world's price level (Resource.inGroundShare()) - a
     * resource with no good is free. Rounded to US$5k, as the parcels were:
     * nobody wants to compare US$103,847 against US$98,211.
     */
    public static double price(double[] km2, double[] amounts, double groundUsdPerSqFt, double usPriceLevel) {
        double ground = km2[CityLand.DRY] + FRESH_PRICE_SHARE * km2[CityLand.FRESH] + SEA_PRICE_SHARE * km2[CityLand.SEA];
        double usd = groundUsdPerSqFt * ground * LandManager.SQ_FT_PER_KM2;
        for (Resource r : Resource.values()) {
            if (r.good() == null || !(r.inGroundShare() > 0)) continue;
            usd += amounts[r.ordinal()] * r.good().worldExportPrice() * r.inGroundShare() * usPriceLevel;
        }
        return Math.round(usd / 5) * 5.0;
    }

    /* ===================================================================
       THE LISTING
       =================================================================== */

    /** The offers standing, side by side and place by place. */
    public List<LandParcel> getListing() {
        List<LandParcel> out = new ArrayList<>(OFFERS);
        for (LandParcel p : offers) if (p != null) out.add(p);
        return out;
    }

    /** One side's offers, place by place: 0 north, 1 east, 2 south, 3 west; a place waiting empty is left out. */
    public List<LandParcel> offersOn(int side) {
        List<LandParcel> out = new ArrayList<>(OFFERS_A_SIDE);
        for (int place = 0; place < OFFERS_A_SIDE; place++) {
            LandParcel p = offers[side * OFFERS_A_SIDE + place];
            if (p != null) out.add(p);
        }
        return out;
    }

    /** The offer standing in one place, or null while it waits empty: no room on that side yet. */
    public LandParcel offerIn(int side, int place) {
        return offers[side * OFFERS_A_SIDE + place];
    }

    /**
     * How many places wait empty though their side has room for them now: a
     * listing GridOffers would stand against the offers standing (list()
     * stands nothing). After every update() it is none - an empty place lists
     * once there is room (spec-grid 2.2) - which the playtest and LandCheck
     * hold.
     */
    public int emptyWithRoom() {
        if (land == null || places == null) return 0;
        int n = 0;
        for (int i = 0; i < OFFERS; i++) {
            if (offers[i] == null && places.list(i / OFFERS_A_SIDE, i % OFFERS_A_SIDE) != null) n++;
        }
        return n;
    }

    /** How many of a side's places wait empty. */
    public int emptyOn(int side) {
        int n = 0;
        for (int place = 0; place < OFFERS_A_SIDE; place++) if (offers[side * OFFERS_A_SIDE + place] == null) n++;
        return n;
    }

    public LandParcel find(int id) {
        for (LandParcel p : offers) if (p != null && p.getId() == id) return p;
        return null;
    }

    /**
     * Whether a is nearer the city than b (0.7.67): the nearer its nearest
     * plot to the founding site, L-infinity, then the lower id - a tie's
     * breaker for the best-offer rules. (Until 0.7.67, the smaller inner
     * radius of a lane's band.)
     */
    public boolean nearer(LandParcel a, LandParcel b) {
        long da = reach(a), db = reach(b);
        return da < db || (da == db && a.getId() < b.getId());
    }

    /** How far an offer's nearest plot lies from the founding site, L-infinity, in plots: 0 when it holds the site's plot; 0 on a bare office. */
    long reach(LandParcel p) {
        if (land == null) return 0;
        long sx = land.siteX(), sy = land.siteY();
        long dx = sx < p.getX0() ? p.getX0() - sx : sx >= p.getX1() ? sx - (p.getX1() - 1) : 0;
        long dy = sy < p.getY0() ? p.getY0() - sy : sy >= p.getY1() ? sy - (p.getY1() - 1) : 0;
        return Math.max(dx, dy);
    }

    /** Whether an offer is bare ground (0.7.58): it holds none of a resource the office prices - ore under it is in its price, and a buyer short of ground has not asked for it (Game.bestOffer(), A SHORTFALL IS MET WITH GROUND). */
    public static boolean bareGround(LandParcel p) {
        for (Resource r : Resource.values()) {
            if (r.good() != null && r.inGroundShare() > 0 && p.getAmount(r) > 0) return false;
        }
        return true;
    }

    /** The cheapest offer standing, for a caller that just wants some land; the nearer on a tie. */
    public LandParcel cheapest() {
        LandParcel best = null;
        for (LandParcel p : offers) {
            if (p == null) continue;
            if (best == null || p.getPriceUsd() < best.getPriceUsd()
                    || (p.getPriceUsd() == best.getPriceUsd() && nearer(p, best))) best = p;
        }
        return best;
    }

    /** The cheapest offer with any sea in it, the nearer on a tie - what a desalination plant needs (0.7.59, batch J2); null when none has any. */
    public LandParcel cheapestWithSea() {
        LandParcel best = null;
        for (LandParcel p : offers) {
            if (p == null || !(p.getKm2(CityLand.SEA) > 0)) continue;
            if (best == null || p.getPriceUsd() < best.getPriceUsd()
                    || (p.getPriceUsd() == best.getPriceUsd() && nearer(p, best))) best = p;
        }
        return best;
    }

    /**
     * The most fresh water a dollar (0.7.59, batch J2): the offer with the
     * most square kilometres of lake or river a US dollar, the nearer on a
     * tie; null when no offer holds any. What a city past its fresh water
     * limit buys to lift it (UtilitiesHandler, THE FRESH WATER LIMIT).
     */
    public LandParcel bestFresh() {
        LandParcel best = null;
        double bestRate = 0;
        for (LandParcel p : offers) {
            if (p == null || !(p.getKm2(CityLand.FRESH) > 0)) continue;
            double rate = p.getPriceUsd() > 0 ? p.getKm2(CityLand.FRESH) / p.getPriceUsd() : Double.POSITIVE_INFINITY;
            if (best == null || rate > bestRate || (rate == bestRate && nearer(p, best))) {
                best = p;
                bestRate = rate;
            }
        }
        return best;
    }

    /**
     * THE BEST VALUE (spec-land star 14): the most dry square kilometres a
     * dollar, the nearer on a tie, never an offer that is mostly sea. Ore is
     * in the price, so ground with ore under it is the best value only when
     * it is cheap enough to carry the ore. When every offer is mostly sea,
     * the most dry ground a dollar among them all, so a city on a spit still
     * has a best. Null only with nothing listed. (Until 0.7.57: the cheapest
     * a square foot of the parcels with no ore.)
     */
    public LandParcel bestValue() {
        LandParcel best = bestDry(true);
        return best != null ? best : bestDry(false);
    }

    private LandParcel bestDry(boolean notMostlySea) {
        LandParcel best = null;
        for (LandParcel p : offers) {
            if (p == null || (notMostlySea && p.isMostlySea())) continue;
            if (best == null || p.getDryKm2PerUsd() > best.getDryKm2PerUsd()
                    || (p.getDryKm2PerUsd() == best.getDryKm2PerUsd() && nearer(p, best))) best = p;
        }
        return best;
    }

    /**
     * The richest offer in a resource (spec-land star 14): the most of its
     * sites a dollar, then the most of it, then the nearer; null when no
     * offer holds any. (Until 0.7.57 the office's richest deposit was the
     * parcel with the most iron tonnes.)
     */
    public LandParcel richest(Resource r) {
        LandParcel best = null;
        double bestRate = 0;
        for (LandParcel p : offers) {
            if (p == null || p.getSites(r) <= 0 || !(p.getAmount(r) > 0)) continue;
            double rate = p.getPriceUsd() > 0 ? p.getSites(r) / p.getPriceUsd() : Double.POSITIVE_INFINITY;
            if (best == null || rate > bestRate
                    || (rate == bestRate && (p.getAmount(r) > best.getAmount(r)
                        || (p.getAmount(r) == best.getAmount(r) && nearer(p, best))))) {
                best = p;
                bestRate = rate;
            }
        }
        return best;
    }

    /**
     * The cheapest offer holding any of a resource, the nearer on a tie;
     * null when none does (0.7.64, batch L). An offer holds every field
     * centred on its ground whole (CityLand), so this is the least a whole
     * field of it costs today, ground and all - what the playtest's player
     * buys its iron with (LongPlaytest.ironWhenNeeded()).
     */
    public LandParcel cheapestWith(Resource r) {
        LandParcel best = null;
        for (LandParcel p : offers) {
            if (p == null || p.getSites(r) <= 0 || !(p.getAmount(r) > 0)) continue;
            if (best == null || p.getPriceUsd() < best.getPriceUsd()
                    || (p.getPriceUsd() == best.getPriceUsd() && nearer(p, best))) best = p;
        }
        return best;
    }

    /**
     * THE GOING RATE ON THIS LISTING (0.7.26): the median of the offers'
     * dollar prices a square foot of dry ground - what the land office
     * judges each offer against ("44% under the going rate"). Moved here
     * verbatim from the screen (LandScreen, until 0.7.26), which worked it
     * out itself against the rule that every figure a screen shows is the
     * model's, through a public getter. In thousands of US dollars; 0 with
     * nothing listed; with an even count, the upper of the two middle prices.
     *
     * COMPARED AGAINST THE LISTING, not against the office's quoted rate.
     * The obvious baseline was LandManager.getAcquisitionCostPerSqFt() -
     * what the market says ground costs outside the city. It does not work:
     * on a played save that figure read $0.70 a square foot while every plot
     * actually on the shelf was priced between $7 and $28, so every card
     * came out "3260% over the office", which is not a verdict, it is
     * noise. An offer's price is frozen at the moment it is listed and the
     * market rate has moved since; either way, comparing today's offers to a
     * number they were not priced from tells the player nothing.
     *
     * The going rate ON THIS LISTING does work, because the offers were all
     * priced the same way and the question a player actually has is "which
     * of these". The median rather than the mean, so one enormous
     * ore-bearing offer cannot drag the line it is being judged against.
     */
    public double goingUsdPerSqFt() {
        List<LandParcel> listed = getListing();
        double[] rates = new double[listed.size()];
        int at = 0;
        for (LandParcel p : listed) rates[at++] = p.getUsdPerSqFt();
        java.util.Arrays.sort(rates);
        return rates.length == 0 ? 0 : rates[rates.length / 2];
    }

    /**
     * Takes an offer off the shelf. The caller has bought it.
     *
     * Does NOT list the place's next - update() does that, first of all the
     * empty places, so the replacement is priced and drawn against the city
     * as it stands after the purchase rather than before it.
     */
    public LandParcel take(int id) {
        for (int i = 0; i < OFFERS; i++) {
            if (offers[i] != null && offers[i].getId() == id) {
                LandParcel p = offers[i];
                offers[i] = null;
                if (places != null) places.stand(i / OFFERS_A_SIDE, i % OFFERS_A_SIDE, null);
                takenFrom = i;
                return p;
            }
        }
        return null;
    }

    /* ===================================================================
       SAVE AND RESTORE

       The offers are written out in full, every field (DataSave's
       landOffers), rather than listed again from the world: listing again
       would tie every save to the exact sampling and pricing rules forever -
       change one and every player's offers silently move, the one they were
       saving up for included. An older save's listing (landListing, the nine
       parcels; landOffers of a format-31 save, forty bands of lanes) is not
       read at all: the conversion lists twenty-four offers in its place,
       once (LandConversion).
       =================================================================== */

    /** The offers standing, one record each (LandParcel.offerRow()), side by side and place by place. */
    public double[][] getOffersState() {
        List<LandParcel> listed = getListing();
        double[][] rows = new double[listed.size()][];
        for (int i = 0; i < rows.length; i++) rows[i] = listed.get(i).offerRow();
        return rows;
    }

    /** The id the next offer listed will take: DataSave's nextOfferId. Ids only grow. */
    public int getNextOfferId() { return nextId; }

    /**
     * Saved offers back on the shelf, each in its own place, and the next id;
     * a record of the wrong width, or a second one for a place, is dropped,
     * and update() lists that place's next.
     */
    public void restoreOffers(double[][] rows, int nextOfferId) {
        List<LandParcel> list = new ArrayList<>();
        if (rows != null) for (double[] row : rows) {
            LandParcel p = LandParcel.fromOfferRow(row);
            if (p != null) list.add(p);
        }
        putOffers(list);
        nextId = Math.max(nextId, nextOfferId);
    }

    /** These offers on the shelf in place of whatever stood there, each in its own place (a second for a place is dropped), their rectangles stood on the land's grid; the next id past the largest. What a harness's hand-made listing goes through. */
    public void putOffers(List<LandParcel> list) {
        java.util.Arrays.fill(offers, null);
        int top = 0;
        for (LandParcel p : list) {
            if (p == null || p.getSide() < 0 || p.getSide() >= CityLand.SIDES || p.getPlace() < 0
                    || p.getPlace() >= OFFERS_A_SIDE) continue;
            int at = p.getSide() * OFFERS_A_SIDE + p.getPlace();
            if (offers[at] == null) offers[at] = p;
            top = Math.max(top, p.getId());
        }
        nextId = top + 1;
        takenFrom = -1;
        if (land != null) restand();
    }

    /** True when the price state came back without a dollar ground price (an older save). */
    private boolean groundFromLocal;

    /**
     * AN OLDER SAVE'S QUOTE WAS LOCAL MONEY, and this reads it as US dollars
     * at the rate of the day the save is loaded (0.7.6), when the price state
     * came back without a dollar ground price - so the local quote the player
     * saw is exactly what it is on the day of loading. Called by the load path
     * once the foreign accounts are back, and by nothing else; a no-op on a
     * dollar quote. Until 0.7.57 an older listing's prices were read the same
     * way; an older listing is not read at all now.
     *
     * @return 1 when the quote was read as dollars, 0 when it was dollars already
     */
    public int settleLocalPrices(double rate) {
        if (!(rate > 0) || !Double.isFinite(rate)) rate = ForeignAccounts.OPENING_RATE;
        int converted = groundFromLocal ? 1 : 0;
        if (groundFromLocal) groundUsdPerSqFt = marketPricePerSqFt / rate;
        groundFromLocal = false;
        return converted;
    }

    /** The side of the city's blocks now, in plots: 2^getLevel() (four plots, 120 m, for a new city); 0 on a bare office. */
    public long getBlockPlots() { return land == null ? 0 : 1L << getLevel(); }

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
     * loading rate by settleLocalPrices(), as its listing was until 0.7.57.
     *
     * The price's parts ride after it since 0.7.55 (slots 4 to 6: the
     * crowding, its premium and the world's price level). A save from before
     * has four slots: its price was the base times its size premium, so the
     * premium is read back as that and the world's level as 1, and its
     * crowding as 0, unknown, until the first month re-strikes all three.
     *
     * Slot 2 is the city's block level since 0.7.67 (getLevel(); the unit
     * offers were listed at, in blocks, from 0.7.57, and the smallest
     * parcel's blocks before): written for the record and not read back,
     * since the level is the city's land's own figure.
     */
    public double[] getPriceState() {
        return new double[] { marketPricePerSqFt, salePricePerSqFt, getLevel(), groundUsdPerSqFt,
                crowding, crowdingPremium, usPriceLevel };
    }

    public void restorePriceState(double[] state) {
        if (state == null || state.length < 3) return;
        if (state[0] > 0) marketPricePerSqFt = state[0];
        if (state[1] > 0) salePricePerSqFt = state[1];
        if (state.length > 3 && state[3] > 0) {
            groundUsdPerSqFt = state[3];
            groundFromLocal = false;
        } else {
            groundFromLocal = state[0] > 0;
        }
        if (state.length > 6 && state[5] > 0 && state[6] > 0) {
            crowding = Math.max(0, state[4]);
            crowdingPremium = state[5];
            usPriceLevel = state[6];
        } else {
            crowding = 0;
            crowdingPremium = Math.max(1, groundUsdPerSqFt / BASE_PRICE_PER_SQ_FT);
            usPriceLevel = 1;
        }
    }

    public void reset() {
        java.util.Arrays.fill(offers, null);
        if (places != null) places = new GridOffers(land.grid(), land.siteX(), land.siteY());
        takenFrom = -1;
        groundFromLocal = false;
        nextId = 1;
        marketPricePerSqFt = basePricePerSqFt;
        groundUsdPerSqFt = BASE_PRICE_PER_SQ_FT;
        salePricePerSqFt = basePricePerSqFt * SCARCITY_FLOOR;
        crowding = 0;
        crowdingPremium = 1;
        usPriceLevel = 1;
    }

    /**
     * The office's LOCAL prices in the new unit - the inside price and the
     * anchor it is struck from - and nothing else.
     *
     * THE LISTING IS LEFT ALONE SINCE 0.7.6. It used to be cleared here:
     * LandParcel is immutable and its price was local money, so a reform could
     * not reprice it and threw the board away instead, the tract the player
     * was saving for with it. Offers are priced in US dollars now, which no
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
     * Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit
     * over the expected price level they are struck at, every month
     * (Game.restrikeMoneyConstants()). See Denomination. The
     * local anchor only: the dollar base and the ore's price are the world's.
     */
    public void seedConstants(double unit) {
        basePricePerSqFt  = BASE_PRICE_PER_SQ_FT / unit;
    }

}
