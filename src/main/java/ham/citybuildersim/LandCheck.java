package ham.citybuildersim;

/**
 * Verifies the land ledger: what the city owns, what it can allocate, what it
 * charges, and that the three numbers never drift apart.
 *
 * The one thing worth being paranoid about here is that allocated land can only
 * ever go up by exactly what was built. A leak in either direction is invisible
 * for a hundred months and then the city is either mysteriously full or
 * mysteriously infinite.
 *
 * Sections 1-11 are the ledger and the market. The land office has its own
 * at the end: land priced in dollars and the two ways to pay for it (0.7.6,
 * sections 12-13); and, since 0.7.13, the office in square kilometres (14),
 * the funding page a city short of the price is offered, converting and
 * from the vault, with the window abroad open and shut (15), and the next N
 * plots bought at once ending exactly as N bought one by one (16); and,
 * since 0.7.26, the three figures the redrawn office takes from the model:
 * the going rate, which it used to work out itself, the GROUND row's
 * verdict and the receipt in the screens' money (17); and, since 0.7.55,
 * the ground priced by how crowded the city is rather than how big, at the
 * world's price level and not the city's (18); and, since 0.7.57, the land
 * on the world (spec-land.md, batch J1b): offers priced from batch I's ground
 * and holding the world's fields (5 to 5f) - forty, ten a side, each the next
 * band of its lane, until 0.7.66; since 0.7.67 (batch M3, spec-grid.md) six
 * places a side, each a rectangle of whole blocks against the city or none
 * while its side has no room, listed once there is, the city's level setting
 * the blocks and the books its plots counted - the world's totals kept to the tonne, the ground worked
 * out in the order it was bought, ground set by hand, forest's regrowth and
 * the best offer for each need (19), a saved city's land field for field
 * (20), and an older save's land converted - the three research cities' (21);
 * and the playtest's player keeping its ground ahead, as the build advice
 * keeps slack (22, batch J1d; since 0.7.67, batch M3b, its room to grow
 * weighed from the same line). From 0.7.58 to 0.7.63 (batch J1c) a field was
 * shared site by site among the ground its sites lie under; since 0.7.64
 * (batch L) a field goes whole again to the one piece of ground holding its
 * centre, an offer priced by its fields' tonnes, a new city's iron a
 * significant investment the funding page sizes a bond to (5e, 19); and the
 * playtest's player buys its iron a whole field at a time, the cheapest
 * standing, with cash or that bond - since 0.7.67 (batch M3b) only once the
 * mines it would carry pay it back (23); and, since 0.7.68 (batch M4), the
 * units the player reads land in - square metres under a hundredth of a
 * square kilometre, square kilometres from it, ground prices a square
 * metre (24).
 */
public class LandCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-52s %14.4f  expected %14.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-52s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** ...and to a part in `relative` of the expected figure (0.7.55): prices a ten-thousandth of a unit, where check()'s 1e-6 is no test. */
    static void near(String label, double actual, double expected, double relative) {
        boolean ok = Math.abs(actual - expected) <= relative * Math.abs(expected);
        if (!ok) fails++;
        System.out.printf("%-52s %14.8g  expected %14.8g  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    public static void main(String[] args) throws Exception {

        /* ==================== 1. what the city starts with ==================== */
        /*
         * THE CENTRE'S DRAWN DRY PLOTS (0.7.67, batch M3). Until 0.7.66 a new
         * city owned exactly STARTING_SQ_FT, its centre sized to hold it. On
         * the block grid the centre is whole blocks of 120 m round the site
         * until their dry plots reach it (CityLand.found()), and the books
         * follow the map: the city owns the dry plots drawn - 315 on the
         * default world for STARTING_SQ_FT's 309.7, 1.7% more.
         */
        System.out.println("--- opening position ---");

        LandManager lm = new LandManager();
        CityLand opening = lm.getCityLand();
        double founded = LandManager.sqFt(opening.totalKm2(CityLand.DRY));
        long dryPlots = 0;
        for (LandGrid.Fill f : opening.centreRects()) {
            byte[] t = new byte[World.TILE * World.TILE];
            for (long y = f.y0(); y < f.y1(); y++) for (long x = f.x0(); x < f.x1(); x++) {
                World.of(opening.seed()).tileTerrain(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE), t);
                byte c = t[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))];
                if (c != World.SALT && c != World.FRESH) dryPlots++;
            }
        }
        System.out.printf("   founded on %d blocks of %.0f m: %d dry plots, %,.0f sq ft (STARTING_SQ_FT %,.0f)%n",
                opening.centreRects().size(), (1L << opening.level()) * World.PLOT_M, dryPlots, founded, LandManager.STARTING_SQ_FT);
        check("owned: the centre's drawn dry plots", lm.getOwnedSqFt(), LandManager.sqFt(dryPlots * World.KM2_PER_PLOT));
        assertTrue("...whole blocks holding at least STARTING_SQ_FT, and less than a block more",
                founded >= LandManager.STARTING_SQ_FT && founded - LandManager.STARTING_SQ_FT
                        < LandManager.sqFt((1L << (2 * opening.level())) * World.KM2_PER_PLOT));
        check("allocated", lm.getAllocatedSqFt(), 0);
        check("available", lm.getAvailableSqFt(), founded);
        check("thirty blocks and a little", lm.getAvailableBlocks(), founded / LandManager.BLOCK_SQ_FT);
        check("nothing built on -> 0% used", lm.getUtilisation(), 0);
        check("no blocks bought yet", lm.getBlocksPurchased(), 0);

        /* ==================== 2. allocating ==================== */
        System.out.println("\n--- building on it ---");

        double rest = founded - 600000;
        assertTrue("room for a 600,000 sq ft plant", lm.canAllocate(600000));
        assertTrue("allocation succeeds", lm.allocate(600000));
        check("allocated", lm.getAllocatedSqFt(), 600000);
        check("available", lm.getAvailableSqFt(), rest);
        check("a fifth of it used, near enough", lm.getUtilisation(), 600000 / founded);

        // Exactly the remainder is allowed; a square foot more is not.
        assertTrue("exactly what is left fits", lm.canAllocate(rest));
        assertTrue("one sq ft more does not", !lm.canAllocate(rest + 1));

        assertTrue("an oversized allocation is refused", !lm.allocate(rest + 1));
        check("...and took nothing when it refused", lm.getAllocatedSqFt(), 600000);

        // Refusal leaving the ledger untouched is the whole point: the caller
        // reads the boolean and must not build, and if the land had been taken
        // anyway the city would lose a plot to a building that never went up.
        check("available unchanged after a refusal", lm.getAvailableSqFt(), rest);

        /* ==================== 3. filling up ==================== */
        System.out.println("\n--- full ---");

        assertTrue("the last of it fits", lm.allocate(rest));
        check("nothing left", lm.getAvailableSqFt(), 0);
        check("100% used", lm.getUtilisation(), 1);
        assertTrue("even one sq ft is refused now", !lm.canAllocate(1));

        // Zero is not a request for land, and must never be refused - a
        // building with no footprint set would otherwise become unbuildable.
        assertTrue("zero always fits", lm.canAllocate(0));

        /* ==================== 4. releasing ==================== */
        System.out.println("\n--- demolition, when it exists ---");

        lm.release(900000);
        check("freed", lm.getAvailableSqFt(), 900000);
        lm.release(1e9);
        check("over-releasing floors at zero", lm.getAllocatedSqFt(), 0);
        check("...and cannot invent land", lm.getOwnedSqFt(), founded);

        /* ==================== 5. the listing ==================== */
        /*
         * SIX OFFERS A SIDE (0.7.67, batch M3; Jerus, 2026-10-07). Until 0.7.57
         * "nine plots are listed": a shelf of parcels drawn from a generator;
         * from 0.7.57 to 0.7.66 forty, the next band of each of ten lanes a
         * side. On the block grid each side has six places, and each place
         * one offer standing - a rectangle of whole blocks against the city's
         * edge (GridOffers) - or none while the side has no room for it. A new
         * city of 120 m blocks lists 19, each 1 x 2 blocks (spec-grid 1.5).
         */
        System.out.println("\n--- six offers a side ---");

        LandManager buy = new LandManager();
        buy.updateMarket(0);
        CityLand ground = buy.getCityLand();
        LandGrid grid = ground.grid();

        java.util.List<LandParcel> listing = buy.getListing();
        int waiting = 0;
        for (int side = 0; side < CityLand.SIDES; side++) waiting += buy.getMarket().emptyOn(side);
        System.out.printf("   %d offers listed, %d places waiting for room (North %d, East %d, South %d, West %d)%n", listing.size(), waiting,
                buy.getMarket().emptyOn(0), buy.getMarket().emptyOn(1), buy.getMarket().emptyOn(2), buy.getMarket().emptyOn(3));
        check("a new city lists every place it has room for: nineteen of twenty-four (spec-grid 1.5)", listing.size(), 19);
        check("...the other places waiting", waiting, LandMarket.OFFERS - listing.size());
        boolean placeEach = true, against = true, whole = true, theirArea = true, apart = true;
        for (int side = 0; side < CityLand.SIDES; side++) {
            for (int place = 0; place < LandMarket.OFFERS_A_SIDE; place++) {
                LandParcel p = buy.getMarket().offerIn(side, place);
                if (p != null) placeEach &= p.getSide() == side && p.getPlace() == place;
            }
        }
        long b2 = 1L << LandGrid.MIN_LEVEL;
        for (LandParcel p : listing) {
            against &= touchesOwned(grid, p) && grid.owned(p.getX0(), p.getY0(), p.getX1(), p.getY1()) == 0;
            whole &= p.getLevel() == LandGrid.MIN_LEVEL && p.blocksAcross() == 1 && p.blocksDeep() == GridOffers.DEPTH_OVER_WIDTH
                    && p.getX0() % b2 == 0 && p.getY0() % b2 == 0 && p.getX1() % b2 == 0 && p.getY1() % b2 == 0;
            theirArea &= p.getKm2() == (p.getX1() - p.getX0()) * (p.getY1() - p.getY0()) * World.KM2_PER_PLOT;
            for (LandParcel q : listing) if (q != p) apart &= !(p.getX0() < q.getX1() && q.getX0() < p.getX1() && p.getY0() < q.getY1() && q.getY0() < p.getY1());
        }
        assertTrue("...one in each place, place by place", placeEach);
        assertTrue("a new city's offers lie against its centre, on ground it does not own", against);
        assertTrue("...each 1 x 2 blocks of 120 m, whole blocks of the grid", whole);
        assertTrue("...each its rectangle's area, 0.0288 km2, every plot of it unowned", theirArea);
        assertTrue("...and no two of them share a plot", apart);
        near("the ground costs batch I's price at the founding: the base, the founding's US prices, no crowding",
                buy.getGroundUsdPerSqFt(), LandMarket.openingUsdPerSqFt() * 1 * LandMarket.crowdingPremium(0), 0);
        check("...which is what it costs here at the founding rate", buy.getAcquisitionCostPerSqFt(),
                buy.getGroundUsdPerSqFt() * ForeignAccounts.OPENING_RATE);
        boolean priced = true;
        for (LandParcel p : listing) {
            double[] km2 = new double[CityLand.AREAS], amounts = new double[CityLand.KINDS];
            for (int a = 0; a < CityLand.AREAS; a++) km2[a] = p.getKm2(a);
            for (Resource r : Resource.values()) amounts[r.ordinal()] = p.getAmount(r);
            priced &= p.getPriceUsd() == LandMarket.price(km2, amounts, buy.getGroundUsdPerSqFt(), 1);
        }
        assertTrue("...and every offer is priced at it: dry ground, water cheaper, ore at its share", priced);

        // The point of a listing rather than a price: they have to differ, or
        // there is no decision in it. A new city's are one size (spec-grid
        // 1.5: 0.0288 km2 each), so they differ in what that ground holds -
        // its dry ground, its water - and so in price.
        double smallest = Double.MAX_VALUE, largest = 0, cheapestUsd = Double.MAX_VALUE, dearestUsd = 0;
        int withIron = 0;
        boolean sized = true;
        for (LandParcel parcel : listing) {
            smallest = Math.min(smallest, parcel.getDryKm2());
            largest = Math.max(largest, parcel.getDryKm2());
            cheapestUsd = Math.min(cheapestUsd, parcel.getPriceUsd());
            dearestUsd = Math.max(dearestUsd, parcel.getPriceUsd());
            if (parcel.hasIron()) withIron++;
            sized &= parcel.getKm2() > 0 && parcel.getPriceUsd() > 0;
        }
        assertTrue("every offer has an area and a price", sized);
        assertTrue("the offers differ: in their dry ground and so in price (0.7.67; a new city's are one size)",
                largest > smallest * 2 && dearestUsd > cheapestUsd);
        System.out.printf("   offers of %.4f to %.4f km2 dry, US$%,.0fk to US$%,.0fk, round a centre of %.4f km2 (%.4f dry), %d with iron%n",
                smallest, largest, cheapestUsd, dearestUsd, ground.centreKm2(CityLand.TOTAL), ground.centreKm2(CityLand.DRY), withIron);

        // The same world and the same city give the same offers, field for
        // field: what lets a reload never be a reroll.
        LandManager twin = new LandManager();
        twin.updateMarket(0);
        boolean identical = twin.getListing().size() == listing.size();
        for (int i = 0; identical && i < listing.size(); i++) identical = listing.get(i).same(twin.getListing().get(i));
        assertTrue("the same city always sees the same offers, field for field", identical);

        /* ==================== 5b. buying one ==================== */
        System.out.println("\n--- buying an offer ---");

        LandParcel wanted = buy.getListing().get(3);
        double before = buy.getOwnedSqFt();

        // A bare land office converts at the founding rate, 1.00 (0.7.6).
        double paid = buy.buyParcel(wanted.getId(), 1e9, 0);
        check("paid exactly what was listed", paid,
                wanted.localPrice(ForeignAccounts.OPENING_RATE));
        check("owned grew by the offer's dry ground",
                buy.getOwnedSqFt(), before + wanted.getSizeSqFt());
        check("recorded as a purchase this month", buy.getLandPurchasesThisMonth(), paid);
        assertTrue("every place with room stands again, as many as before or more", buy.getListing().size() >= listing.size());
        assertTrue("...and that offer is gone from them",
                buy.getMarket().find(wanted.getId()) == null);
        LandParcel next = buy.getMarket().offerIn(wanted.getSide(), wanted.getPlace());
        boolean claimed = true;
        for (long y = wanted.getY0(); y < wanted.getY1(); y++) {
            for (long x = wanted.getX0(); x < wanted.getX1(); x++) claimed &= ground.holdingOf(x, y) == ground.purchases().size();
        }
        assertTrue("its rectangle's plots are the city's, the new holding's (the grid's next)", claimed
                && ground.purchases().size() == 1 && ground.grid().ownedPlots() == ground.centreRects().size() * b2 * b2
                        + (wanted.getX1() - wanted.getX0()) * (wanted.getY1() - wanted.getY0()));
        assertTrue("its place lists its next: against the city, on ground it does not own, under a new id", next != null
                && touchesOwned(ground.grid(), next) && ground.grid().owned(next.getX0(), next.getY0(), next.getX1(), next.getY1()) == 0
                && next.getId() > wanted.getId());

        // What is listed stays listed: everything the player was weighing up is
        // still there, field for field, after somebody buys something else.
        boolean held = true;
        for (LandParcel parcel : listing) {
            if (parcel.getId() == wanted.getId()) continue;
            held &= parcel.same(buy.getMarket().find(parcel.getId()));
        }
        assertTrue("the other offers did not move, field for field", held);

        assertTrue("buying an unlisted offer does nothing",
                buy.buyParcel(999999, 1e9, 0) == 0);

        // AN EMPTY PLACE LISTS ONCE THERE IS ROOM (0.7.67): a new city's five
        // waiting places, the sides bought in turn - the cheapest of each -
        // each lists at the first update its side has room for it, and no
        // update leaves a place empty that has room.
        LandManager even = new LandManager();
        even.updateMarket(0);
        java.util.Set<Integer> waited = new java.util.HashSet<>();
        for (int i = 0; i < LandMarket.OFFERS; i++) {
            if (even.getMarket().offerIn(i / LandMarket.OFFERS_A_SIDE, i % LandMarket.OFFERS_A_SIDE) == null) waited.add(i);
        }
        boolean neverWithRoom = even.getMarket().emptyWithRoom() == 0;
        int allBy = -1;
        for (int k = 0; k < 24 && allBy < 0; k++) {
            int side = k % CityLand.SIDES;
            LandParcel pick = null;
            for (LandParcel p : even.getMarket().offersOn(side)) if (pick == null || p.getPriceUsd() < pick.getPriceUsd()) pick = p;
            if (pick == null) continue;
            even.buyParcel(pick.getId(), 1e12, 0);
            neverWithRoom &= even.getMarket().emptyWithRoom() == 0;
            if (even.getListing().size() == LandMarket.OFFERS) allBy = k + 1;
        }
        System.out.printf("   %d places waited at the founding; all %d stand after purchase %d, the sides bought in turn%n",
                waited.size(), LandMarket.OFFERS, allBy);
        assertTrue("fixture: a new city has places waiting for room", !waited.isEmpty());
        assertTrue("an empty place lists once there is room: no update leaves one empty with room for it", neverWithRoom);
        assertTrue("...and, the sides bought in turn, every place comes to stand (GridCheck 4 holds the design's 4th to 12th)",
                allBy > 0);

        /* ============ 5c. a more crowded city pays more ============ */
        /*
         * CROWDED, NOT BIG (0.7.55). Until then this was "a bigger city pays
         * more ... but only slightly": forty blocks and eight thousand people
         * on, under four times a new city's price. The premium reads the
         * city's crowding now (LandMarket, THE CROWDING PREMIUM; section 18),
         * and that city is 12,302 people a square kilometre, past anything
         * played, so it pays near the curve's ceiling: "only slightly" was a
         * premise of the size premium, and went with it. What stands: more
         * people on the ground makes it dearer, and nothing makes it dearer
         * without limit.
         */
        System.out.println("\n--- and a more crowded city pays more ---");

        double smallCityRate = new LandManager() {{ updateMarket(0); }}
                .getAcquisitionCostPerSqFt();

        LandManager bigCity = new LandManager();
        bigCity.setOwnedSqFt(LandManager.STARTING_SQ_FT + 40 * LandManager.BLOCK_SQ_FT);
        bigCity.updateMarket(8000);

        assertTrue("more people on the land means dearer land",
                bigCity.getAcquisitionCostPerSqFt() > smallCityRate);
        System.out.printf("   $%.4f/sq ft for a new city, $%.4f for a crowded one (%,.0f people a km2)%n",
                smallCityRate, bigCity.getAcquisitionCostPerSqFt(), bigCity.getCrowding());
        assertTrue("...but never past the ceiling, however crowded",
                bigCity.getAcquisitionCostPerSqFt() <= smallCityRate * LandMarket.CROWDING_CEILING);

        /* ==================== 5d. supply and demand inside the city ========= */
        System.out.println("\n--- what businesses pay ---");

        LandManager empty = new LandManager();
        empty.updateMarket(0);
        double emptyPrice = empty.getPricePerSqFt();

        LandManager full = new LandManager();
        full.allocate(LandManager.STARTING_SQ_FT * .95);
        full.updateMarket(0);

        assertTrue("a full city sells land dearer than an empty one",
                full.getPricePerSqFt() > emptyPrice);

        /*
         * THE OLD ASSERTION HERE WAS "and both are above what the city paid for
         * it", and it is now DELIBERATELY FALSE at the empty end.
         *
         * That invariant only held because the sale price was the acquisition
         * price times a markup that never dropped below 1.15 - which is also why
         * scarcity did nothing: measured over 2,400 months the markup sat
         * between 85% and 105% for the whole game while utilisation sat between
         * 86% and 100%. The word "scarcity" was decoration.
         *
         * Inside land is priced on how much is left now, so a city holding far
         * more ground than it can build on sells below what it paid, and eats
         * the difference. Over-annexing is supposed to cost something.
         */
        assertTrue("a city with nothing built sells BELOW what it paid",
                emptyPrice < empty.getAcquisitionCostPerSqFt());
        assertTrue("...and a built-out one sells well above",
                full.getPricePerSqFt() > full.getAcquisitionCostPerSqFt() * 1.4);

        System.out.printf("   $%.4f/sq ft empty (%.0f%% of cost), "
                        + "$%.4f/sq ft full (%.0f%% of cost)%n",
                emptyPrice, emptyPrice / empty.getAcquisitionCostPerSqFt() * 100,
                full.getPricePerSqFt(),
                full.getPricePerSqFt() / full.getAcquisitionCostPerSqFt() * 100);

        /*
         * The property Jerus actually asked for, stated outright: annexing more
         * ground makes the ground already inside CHEAPER. It is the reason the
         * old measure had to go - utilisation moved so little that buying land
         * barely registered.
         */
        LandManager tight = new LandManager();
        tight.allocate(LandManager.STARTING_SQ_FT * .92);
        tight.updateMarket(2000);
        double whenTight = tight.getPricePerSqFt();

        tight.setOwnedSqFt(tight.getOwnedSqFt() + LandManager.BLOCK_SQ_FT * 12);
        tight.updateMarket(2000);
        double afterBuying = tight.getPricePerSqFt();

        System.out.printf("   tight $%.4f -> after annexing twelve blocks $%.4f%n",
                whenTight, afterBuying);
        assertTrue("buying land makes land cheaper for investors",
                afterBuying < whenTight);

        // And the curve is monotone, which a saturating ratio could get wrong.
        LandMarket curve = new LandMarket();
        double previous = -1;
        boolean rising = true;
        for (double built = .05; built <= 1.0; built += .05) {
            double m = curve.scarcityMultiplier(1_000_000, 1_000_000 * built);
            if (m < previous) rising = false;
            previous = m;
        }
        assertTrue("the scarcity curve never goes backwards", rising);
        check("nothing built is the cheapest it gets",
                curve.scarcityMultiplier(1_000_000, 0), .65);
        check("completely full is the dearest",
                curve.scarcityMultiplier(1_000_000, 1_000_000), 1.90);
        check("...and bad data cannot price below that",
                curve.scarcityMultiplier(1_000_000, 1_200_000), 1.90);

        /* ==================== 5e. iron in the ground ==================== */
        /*
         * THE WORLD'S FIELDS (0.7.57). Until then "some plot on offer has iron
         * under it": the office drew ore at random onto a fifth of its
         * parcels. An offer holds the world's fields now, every one whose
         * centre lies on its ground, whole - so a new city's first offers, a
         * few hundred metres out, seldom hold any, and the city finds its ore
         * by growing toward it: until 0.7.98 the founding site had an iron
         * field within World.SITE_IRON_KM; since 0.7.99 (batch W1) a founding
         * asks none, and the nearest field lies where its cluster does, some
         * twelve kilometres out on the default world - and buying toward it
         * reaches it. (From 0.7.58 to
         * 0.7.63, batch J1c, an offer held each site whose own centre lay in
         * its band, with its share of its field; whole again since 0.7.64,
         * batch L - Jerus: "whole iron fields as one offer". On the block grid
         * since 0.7.67: the fields whose centre plots are on an offer's free
         * plots, and the city grown toward a field by the offer nearest it.)
         */
        System.out.println("\n--- ore: the world's fields ---");

        LandManager ore = new LandManager();
        ore.updateMarket(0);
        CityLand oreLand = ore.getCityLand();
        double[] inCentre = recountOn(oreLand, Resource.IRON, (x, y) -> oreLand.holdingOf(x, y) == CityLand.CENTRE);
        check("a new city holds the iron fields the world centred on its centre's plots, whole, and no more",
                ore.getIronDeposits(), inCentre[0]);
        check("...and their tonnes", ore.getIronReserveTonnes(), inCentre[1]);
        Deposit field = nearestUnowned(oreLand, Resource.IRON);
        assertTrue("fixture: an iron field lies near the founding site", field != null);
        LandParcel deposit = ore.getMarket().richest(Resource.IRON);
        for (int i = 0; i < 400 && deposit == null && field != null; i++) {
            LandParcel push = MiningCheck.nearestOffer(ore.getMarket(), field.x(), field.y());
            ore.buyParcel(push.getId(), 1e12, 0);
            deposit = ore.getMarket().richest(Resource.IRON);
        }
        assertTrue("buying toward it, the offer nearest it each time, an offer comes to hold iron", deposit != null);

        if (deposit != null) {
            double[] counted = offerFields(oreLand, deposit, Resource.IRON);
            check("its iron sites are the world's fields centred on its free plots, recounted, whole",
                    deposit.getDeposits(), counted[0]);
            check("...and its tonnes theirs, whole, to the tonne", deposit.getIronTonnes(), counted[1]);
            double groundPart = ore.getGroundUsdPerSqFt() * LandManager.SQ_FT_PER_KM2
                    * (deposit.getDryKm2() + LandMarket.FRESH_PRICE_SHARE * deposit.getKm2(CityLand.FRESH)
                    + LandMarket.SEA_PRICE_SHARE * deposit.getKm2(CityLand.SEA));
            double orePart = counted[1] * Good.IRON.worldExportPrice() * Resource.IRON.inGroundShare();
            assertTrue("a deposit costs more than the ground it sits on", deposit.getPriceUsd() > groundPart);
            assertTrue("...by its fields' tonnes at the in-ground price, 1/350 of the world's iron, to the US$5k it is rounded to",
                    Math.abs(deposit.getPriceUsd() - groundPart - orePart) <= 2.5);
            System.out.printf("   %s: %d sites, %,.0fk tonnes: US$%,.0fk against US$%,.0fk for its ground%n",
                    deposit.where(), deposit.getDeposits(), deposit.getIronTonnes() / 1000, deposit.getPriceUsd(), groundPart);

            int sitesBefore = ore.getIronDeposits();
            double tonnesBefore = ore.getIronReserveTonnes();
            ore.buyParcel(deposit.getId(), 1e12, 0);

            int sites = sitesBefore + deposit.getDeposits();
            check("buying it gives the city its sites", ore.getIronDeposits(), sites);
            check("...and its tonnage", ore.getIronReserveTonnes(), tonnesBefore + deposit.getIronTonnes());

            assertTrue("the sites support that many mines", ore.hasUnminedDeposit(sites - 1));
            assertTrue("...and not one more", !ore.hasUnminedDeposit(sites));

            double lifted = ore.extractIron(50000);
            check("mining takes ore out of the ground", lifted, 50000);
            check("...and the reserve falls",
                    ore.getIronReserveTonnes(), tonnesBefore + deposit.getIronTonnes() - 50000);

            // Finite means finite. A mine on an empty deposit lifts nothing and
            // still costs its payroll, which is the point of depletion.
            ore.extractIron(1e18);
            check("a deposit can be worked out", ore.getIronReserveTonnes(), 0);
            check("...and then yields nothing", ore.extractIron(1000), 0);
            assertTrue("...and supports no more mines", !ore.hasUnminedDeposit(0));
        }

        /* ============ 5f. the level rule ============ */
        /*
         * Jerus, after the hand-played run: buying ~200 parcels one click at a
         * time to reach 581 blocks was the single biggest time sink in playing
         * the game. Until 0.7.57 nothing smaller than a block and a floor that
         * rose a block for every forty bought answered it; from 0.7.57 to
         * 0.7.66 the size rule, a multiple of a block or of 1% of the city
         * drawn from the offer's id. On the block grid since 0.7.67 (spec-grid
         * star 2) the city's level does: its blocks are the largest of which
         * FACE_BLOCKS fit across a square of its area, an offer w x 2w of them
         * (or one level finer where its side has no room at its own), so an
         * offer is about 2 to 3% of the city at any size. Its ground is its
         * rectangle's free plots, counted plot by plot: the books to the plot.
         */
        System.out.println("\n--- the level rule ---");

        LandManager young = new LandManager();
        young.updateMarket(0);
        check("a new city's blocks are MIN_LEVEL's, 120 m", young.getMarket().getLevel(), LandGrid.MIN_LEVEL);
        check("...four plots a side", young.getMarket().getBlockPlots(), 1L << LandGrid.MIN_LEVEL);

        System.out.println("\n--- and the level grows with the city ---");

        LandManager grown = new LandManager();
        grown.updateMarket(0);
        grown.setOwnedSqFt(LandManager.STARTING_SQ_FT + 1_000 * LandManager.BLOCK_SQ_FT);
        grown.updateMarket(20000);
        CityLand grownLand = grown.getCityLand();
        int level = grownLand.level();
        check("a city of 9.6 km2 has the level FACE_BLOCKS blocks across it make", level,
                LandGrid.levelFor(grownLand.grid().ownedPlots()));
        assertTrue("...past a new city's", level > LandGrid.MIN_LEVEL);
        boolean levels = true, aspect = true, alignedBlocks = true, freeGround = true, booked = true;
        for (LandParcel p : grown.getListing()) {
            long bl = 1L << p.getLevel();
            levels &= p.getLevel() == level || p.getLevel() == Math.max(LandGrid.MIN_LEVEL, level - 1);
            aspect &= p.blocksDeep() <= 2 * p.blocksAcross() && p.blocksAcross() <= 2 * p.blocksDeep();
            alignedBlocks &= p.getX0() % bl == 0 && p.getY0() % bl == 0 && p.getX1() % bl == 0 && p.getY1() % bl == 0;
            freeGround &= grownLand.grid().unowned(p.getX0(), p.getY0(), p.getX1(), p.getY1()) > 0;
            double[] counted = freePlotsCounted(grownLand, p);
            for (int a = 0; a < CityLand.AREAS; a++) booked &= p.getKm2(a) == counted[a];
        }
        System.out.printf("   a city of %.2f km2 (%.2f dry), level %d (%.0f m blocks): %d offers, %d places waiting%n",
                grownLand.totalKm2(CityLand.TOTAL), LandManager.km2(grown.getOwnedSqFt()), level, (1L << level) * World.PLOT_M,
                grown.getListing().size(), LandMarket.OFFERS - grown.getListing().size());
        assertTrue("...every offer of blocks of its level, or one finer where its side has no room", levels);
        assertTrue("...whole blocks of the grid, never past 2:1", alignedBlocks && aspect);
        assertTrue("...each holding ground the city does not own yet", freeGround);
        assertTrue("...its books the plots of its rectangle the city does not own, counted plot by plot", booked);

        // EXACT AT EVERY SIZE (the orchestrator's decision, 2026-10-07): a rectangle of PARALLEL_TILES tiles or more is
        // counted over the machine's cores, from tiles' kept counts where the city owns none of a tile - and gives
        // what one core counting every plot gives, to the plot.
        long side = (long) Math.ceil(Math.sqrt(CityLand.PARALLEL_TILES)) * World.TILE;
        long bx0 = grownLand.grid().minX() - side / 2, by0 = grownLand.grid().minY() - side / 2;
        LandParcel big = new LandParcel(0, 0, 0, 5, bx0 + 3, by0 + 5, bx0 + side + 3, by0 + side + 5, null, null, null, 0, 0);
        long tBig = System.nanoTime();
        double[] fast = grownLand.groundOf(big.getX0(), big.getY0(), big.getX1(), big.getY1());
        double fastMs = (System.nanoTime() - tBig) / 1e6;
        tBig = System.nanoTime();
        double[] slow = freePlotsCounted(grownLand, big);
        double slowMs = (System.nanoTime() - tBig) / 1e6;
        boolean exactBig = true;
        for (int a = 0; a < CityLand.AREAS; a++) exactBig &= fast[a] == slow[a];
        System.out.printf("   a rectangle of %,d tiles round the city, not on tile lines: %.2f km2 free, %.2f dry; counted over the cores in %.0f ms,"
                + " plot by plot on one in %.0f ms%n", (side / World.TILE) * (side / World.TILE), fast[CityLand.TOTAL], fast[CityLand.DRY], fastMs, slowMs);
        assertTrue("a rectangle of PARALLEL_TILES tiles or more is counted over the cores to the plot, as one core counts it",
                exactBig && fast[CityLand.TOTAL] > 0 && fast[CityLand.TOTAL] < side * side * World.KM2_PER_PLOT);

        System.out.println("\n--- the offers survive a save, field for field ---");

        LandMarket reloaded = new LandMarket();
        reloaded.attach(grown.getCityLand());
        reloaded.restoreOffers(grown.getMarket().getOffersState(), grown.getMarket().getNextOfferId());
        boolean survived = reloaded.getListing().size() == grown.getListing().size();
        for (int i = 0; survived && i < grown.getListing().size(); i++) {
            survived = grown.getListing().get(i).same(reloaded.getListing().get(i));
        }
        assertTrue("...every field of every offer", survived);
        check("...and the next id", reloaded.getNextOfferId(), grown.getMarket().getNextOfferId());
        double[][] torn = grown.getMarket().getOffersState();
        int tornAt = 7 % torn.length;
        LandParcel tornOffer = grown.getListing().get(tornAt);
        torn[tornAt] = java.util.Arrays.copyOf(torn[tornAt], 5);
        LandMarket mended = new LandMarket();
        mended.attach(grown.getCityLand());
        mended.restoreOffers(torn, grown.getMarket().getNextOfferId());
        check("a record of the wrong width is dropped", mended.getListing().size(), grown.getListing().size() - 1);
        mended.update(grown.getOwnedSqFt(), grown.getAllocatedSqFt(), 20000);
        LandParcel relisted = mended.offerIn(tornOffer.getSide(), tornOffer.getPlace());
        boolean clear = relisted != null && touchesOwned(grown.getCityLand().grid(), relisted);
        for (LandParcel q : mended.getListing()) {
            if (relisted != null && q != relisted) clear &= !(q.getX0() < relisted.getX1() && relisted.getX0() < q.getX1()
                    && q.getY0() < relisted.getY1() && relisted.getY0() < q.getY1());
        }
        assertTrue("...and its place lists its next, against the city and apart from the others", clear);
        check("...under the next id", relisted == null ? -1 : relisted.getId(), grown.getMarket().getNextOfferId());

        /* ==================== 6. not affording it ==================== */
        System.out.println("\n--- an empty treasury ---");

        LandManager broke = new LandManager();
        broke.updateMarket(0);
        LandParcel offer = broke.getListing().get(0);

        double offerLocal = offer.localPrice(ForeignAccounts.OPENING_RATE);
        check("cannot afford it -> pays nothing",
                broke.buyParcel(offer.getId(), offerLocal - 1, 0), 0);
        check("...and gets nothing: the ground it was founded on still", broke.getOwnedSqFt(),
                LandManager.sqFt(broke.getCityLand().totalKm2(CityLand.DRY)));
        check("...and is not recorded", broke.getLandPurchasesThisMonth(), 0);
        assertTrue("...and it is still on offer",
                broke.getMarket().find(offer.getId()) != null);
        check("exactly enough does buy it",
                broke.buyParcel(offer.getId(), offerLocal, 0), offerLocal);

        /* ==================== 7. selling ==================== */
        System.out.println("\n--- selling to businesses ---");

        LandManager sell = new LandManager();

        check("opening price is $1/sq ft", sell.getPricePerSqFt(), .001);
        check("a 8,000 sq ft house plot", sell.priceFor(8000), 8);
        check("margin at the opening price", sell.getMarginPerSqFt(), .001 - .0007);

        sell.recordSale(8000);
        check("sale recorded", sell.getLandSalesThisMonth(), 8);
        check("sq ft recorded", sell.getSqFtSoldThisMonth(), 8000);

        sell.recordSale(8000);
        check("sales accumulate over the month", sell.getLandSalesThisMonth(), 16);

        sell.clearMonth();
        check("cleared for the next month", sell.getLandSalesThisMonth(), 0);
        check("...sq ft too", sell.getSqFtSoldThisMonth(), 0);
        check("...and purchases", sell.getLandPurchasesThisMonth(), 0);

        // Clearing the month's flows must not touch the stock figures.
        check("owned survives the clear", sell.getOwnedSqFt(), LandManager.STARTING_SQ_FT);

        /* ==================== 8. the player's price ==================== */
        System.out.println("\n--- the price is the player's lever ---");

        sell.setPricePerSqFt(.003);
        check("price set", sell.getPricePerSqFt(), .003);
        check("the same plot now costs more", sell.priceFor(8000), 24);
        check("fatter margin", sell.getMarginPerSqFt(), .003 - .0007);

        // Selling below cost is allowed - subsidising land to attract industry
        // is a real policy - but it must read as the loss it is.
        sell.setPricePerSqFt(.0004);
        assertTrue("below cost reads as a negative margin", sell.getMarginPerSqFt() < 0);

        sell.setPricePerSqFt(0);
        check("free land is allowed", sell.getPricePerSqFt(), 0);
        check("...and costs the buyer nothing", sell.priceFor(8000), 0);

        sell.setPricePerSqFt(-5);
        check("a negative price floors at zero", sell.getPricePerSqFt(), 0);

        /* ==================== 9. reset ==================== */
        System.out.println("\n--- new game ---");

        sell.allocate(1500000);
        sell.buyBlock(1e6);
        sell.setPricePerSqFt(.05);
        sell.reset();

        check("owned back to the start", sell.getOwnedSqFt(), LandManager.STARTING_SQ_FT);
        check("nothing allocated", sell.getAllocatedSqFt(), 0);
        check("no blocks bought", sell.getBlocksPurchased(), 0);
        check("price back to default", sell.getPricePerSqFt(), .001);
        check("block cost back to the first", sell.getNextBlockCost(), 70);
        check("no flows", sell.getLandSalesThisMonth(), 0);

        /* ============ 10. every building fits on a starting city ============ */
        System.out.println("\n--- the buildings themselves ---");

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();

        boolean allHaveLand = true, seaHasNone = true;
        int atSea = 0;
        double biggest = 0;
        String biggestName = "";

        for (BuildingsTemplate t : bm.getTemplates()) {
            // ...the oil at sea (0.7.91) stands on none of the city's dry ground: an offshore platform, its wells, a pipeline.
            if (t.standsAtSea()) {
                atSea++;
                seaHasNone &= t.getLandSqFt() == 0;
                continue;
            }
            if (t.getLandSqFt() <= 0) {
                allHaveLand = false;
                System.out.println("  no footprint: " + t.getName());
            }
            if (t.getLandSqFt() > biggest) {
                biggest = t.getLandSqFt();
                biggestName = t.getName();
            }
        }

        assertTrue("every building on the ground has a footprint", allHaveLand);
        assertTrue("...and the three at sea none (0.7.91: the offshore platform, its wells, the crude pipeline)",
                seaHasNone && atSea == 3);
        System.out.printf("   largest: %s at %,.0f sq ft (%.1f blocks)%n",
                biggestName, biggest, biggest / LandManager.BLOCK_SQ_FT);

        // The opening ten blocks have to be enough for the player's first moves,
        // or the mechanic is a wall rather than a constraint.
        LandManager fresh = new LandManager();
        assertTrue("a power plant fits on the starting land",
                fresh.canAllocate(bm.getTemplateByName("Coal Power Plant").getLandSqFt()));

        double house = bm.getTemplateByName("House").getLandSqFt();
        check("the starting land holds this many houses",
                Math.floor(LandManager.STARTING_SQ_FT / house), 375);

        // Both utilities fit, and between them they take 28 of the 30 blocks -
        // so a player who builds power and water first is out of land for
        // housing on the very next turn. That is the mechanic introducing itself.
        double bothUtilities = bm.getTemplateByName("Coal Power Plant").getLandSqFt()
                + bm.getTemplateByName("Water Treatment Plant").getLandSqFt();

        assertTrue("both utilities fit", fresh.canAllocate(bothUtilities));
        assertTrue("...with almost nothing to spare",
                LandManager.STARTING_SQ_FT - bothUtilities < 3 * LandManager.BLOCK_SQ_FT);

        fresh.allocate(bothUtilities);
        assertTrue("...and then a materials plant does not fit",
                !fresh.canAllocate(
                        bm.getTemplateByName("Construction Materials Plant").getLandSqFt()));
        System.out.printf("   after power + water: %,.0f sq ft left (%.1f blocks)%n",
                fresh.getAvailableSqFt(), fresh.getAvailableBlocks());

        /* ============ 11. the price is a density policy ============ */
        System.out.println("\n--- dear land should push developers upward ---");

        BuildingsTemplate detached = bm.getTemplateByName("House");
        BuildingsTemplate studio = bm.getTemplateByName("Studio Apartments");

        LandManager policy = new LandManager();

        // Cost of housing one resident, land included. Houses are cheap to
        // build and hungry for land; apartments are the reverse. Which one wins
        // is therefore a function of what land costs - which is the player's
        // lever, and the reason it is theirs to set.
        double cheapHouse = (detached.getCashCost()
                + policy.priceFor(detached.getLandSqFt())) / detached.getCapacity();
        double cheapFlat = (studio.getCashCost()
                + policy.priceFor(studio.getLandSqFt())) / studio.getCapacity();

        assertTrue("at the default price, sprawl is cheaper", cheapHouse < cheapFlat);
        System.out.printf("   $%.2f/resident in a house vs $%.2f in a studio%n",
                cheapHouse, cheapFlat);

        policy.setPricePerSqFt(.020);
        double dearHouse = (detached.getCashCost()
                + policy.priceFor(detached.getLandSqFt())) / detached.getCapacity();
        double dearFlat = (studio.getCashCost()
                + policy.priceFor(studio.getLandSqFt())) / studio.getCapacity();

        assertTrue("at $20/sq ft, density is cheaper", dearFlat < dearHouse);
        System.out.printf("   $%.2f/resident in a house vs $%.2f in a studio%n",
                dearHouse, dearFlat);

        inDollars();
        bothWays();
        inSquareKilometres();
        whenShort();
        severalAtOnce();
        theOfficesFigures();
        crowdedNotBig();
        onTheWorld();
        savedAndLoaded();
        converted();
        groundKeptAhead();
        ironAWholeFieldAtATime();
        inThePlayersUnits();

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ==================================================================
       18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55)

       Jerus: "Yes for land price do that, but tie it to inflation as well,
       usd inflation not domestic inflation." The premium on the ground is
       the city's crowding - its people per square kilometre of the land it
       owns - so a city a million times bigger and as crowded is quoted
       exactly what the small one is; and the dollar price is the base at
       the world's price level, which the city's own price level does not
       reach. A town of 6,000 people on 330 blocks (1,957 a square kilometre,
       city600's crowding) against the same a million times over: six billion
       people. Since 0.7.67 the copy's land is the town's records with its
       books a million times over (timesOver()), as ScaleCheck's copies are:
       its ground is counted plot by plot, and three million square
       kilometres drawn and counted is minutes, not a harness's seconds.
       ================================================================== */
    static void crowdedNotBig() {
        System.out.println("\n--- the premium is crowding, not size ---");

        LandManager town = new LandManager();
        town.setOwnedSqFt(LandManager.STARTING_SQ_FT + 300 * LandManager.BLOCK_SQ_FT);
        town.allocate(town.getOwnedSqFt() * .9);
        town.updateMarket(6_000);
        long k = 1_000_000;
        LandManager metropolis = timesOver(town, k);
        metropolis.allocate(town.getAllocatedSqFt() * k);
        metropolis.updateMarket(6_000L * k);
        System.out.printf("   %,d people on %.2f km2 against %,d on %,.0f km2: %,.1f and %,.1f a km2%n",
                6_000, LandManager.km2(town.getOwnedSqFt()), 6_000L * k, LandManager.km2(metropolis.getOwnedSqFt()),
                town.getCrowding(), metropolis.getCrowding());
        assertTrue("fixture: the town is crowded enough to pay a premium",
                town.getCrowdingPremium() > 2);
        near("two cities equally crowded have the same premium", metropolis.getCrowdingPremium(),
                town.getCrowdingPremium(), 1e-12);
        near("...the world asks them the same dollars a sq ft", metropolis.getGroundUsdPerSqFt(),
                town.getGroundUsdPerSqFt(), 1e-12);
        near("...and their businesses pay the same", metropolis.getPricePerSqFt(), town.getPricePerSqFt(), 1e-12);

        LandManager crowded = new LandManager();
        crowded.setOwnedSqFt(town.getOwnedSqFt());
        crowded.allocate(town.getAllocatedSqFt());
        crowded.updateMarket(12_000);
        assertTrue("twice the people on the same ground pays more", crowded.getCrowdingPremium() > town.getCrowdingPremium());
        LandManager spread = new LandManager();
        spread.setOwnedSqFt(town.getOwnedSqFt() * 2);
        spread.allocate(town.getAllocatedSqFt());
        spread.updateMarket(6_000);
        assertTrue("...and the same people on twice the ground pays less", spread.getCrowdingPremium() < town.getCrowdingPremium());
        near("the premium is the curve's at the city's crowding", town.getCrowdingPremium(),
                LandMarket.crowdingPremium(6_000 / LandManager.km2(town.getOwnedSqFt())), 1e-15);
        near("...half way to the ceiling at CROWDING_MIDPOINT", LandMarket.crowdingPremium(LandMarket.CROWDING_MIDPOINT),
                (1 + LandMarket.CROWDING_CEILING) / 2, 1e-15);
        check("...and nothing on an empty city", LandMarket.crowdingPremium(0), 1);
        near("...and the ceiling at the limit", LandMarket.crowdingPremium(Double.POSITIVE_INFINITY),
                LandMarket.CROWDING_CEILING, 1e-15);

        System.out.println("\n--- the dollar price follows the world's prices, not the city's ---");

        double[] usPrices = { 1.0 };
        LandManager office = new LandManager(() -> ForeignAccounts.OPENING_RATE, () -> usPrices[0]);
        office.setOwnedSqFt(town.getOwnedSqFt());
        office.allocate(town.getAllocatedSqFt());
        office.updateMarket(6_000);
        double usd = office.getGroundUsdPerSqFt(), anchor = office.getMarket().getMarketPricePerSqFt();
        near("the parts: the base x the world's level x the premium", usd,
                LandMarket.openingUsdPerSqFt() * office.getUsPriceLevel() * office.getCrowdingPremium(), 1e-15);

        usPrices[0] = 1.25;
        office.updateMarket(6_000);
        near("US prices up 25%: the dollar price up 25%", office.getGroundUsdPerSqFt(), usd * 1.25, 1e-12);
        near("...what the treasury pays with it, at the same rate", office.getAcquisitionCostPerSqFt(),
                usd * 1.25 * ForeignAccounts.OPENING_RATE, 1e-12);
        near("...and the businesses' anchor not at all", office.getMarket().getMarketPricePerSqFt(), anchor, 1e-12);

        // The city's own inflation: the money constants struck at three times
        // the price level (seedConstants() takes the unit over the expected
        // level, Game.restrikeMoneyConstants()).
        office.seedConstants(1 / 3.0);
        office.updateMarket(6_000);
        near("the city's prices tripled: the dollar price is where the world put it",
                office.getGroundUsdPerSqFt(), usd * 1.25, 1e-12);
        near("...while the businesses' anchor follows the city's own prices",
                office.getMarket().getMarketPricePerSqFt(), anchor * 3, 1e-12);

        // Saved with the prices, so the office can name them after a load.
        LandMarket back = new LandMarket();
        back.restorePriceState(office.getMarket().getPriceState());
        near("the parts survive a save: the crowding", back.getCrowding(), office.getCrowding(), 0);
        near("...the premium", back.getCrowdingPremium(), office.getCrowdingPremium(), 0);
        near("...and the world's level", back.getUsPriceLevel(), 1.25, 0);
        LandMarket older = new LandMarket();
        double[] four = java.util.Arrays.copyOf(office.getMarket().getPriceState(), 4);
        older.restorePriceState(four);
        near("a save from before reads its price as the premium, at US prices of 1",
                older.getCrowdingPremium() * older.getUsPriceLevel(), office.getGroundUsdPerSqFt() / LandMarket.openingUsdPerSqFt(), 1e-12);
    }

    /**
     * A land office holding a city's land k times over, by its records (0.7.67):
     * the town's blocks, holdings and offers, each holding's five areas and
     * its forest's timber times k, and the figure times k - a copy K times
     * over as ScaleCheck's scaler writes one, which no harness can draw.
     */
    static LandManager timesOver(LandManager town, long k) {
        CityLand t = town.getCityLand();
        double[] centre = t.centreState();
        int timber = CityLand.AREAS + CityLand.KINDS + Resource.FOREST.ordinal();
        for (int a = 0; a < CityLand.AREAS; a++) centre[a] *= k;
        centre[timber] *= k;
        double[][] holdings = t.holdingsState();
        int at = LandParcel.PURCHASE_FIELDS - 2 * CityLand.KINDS - CityLand.AREAS - 2;
        for (double[] row : holdings) {
            for (int a = 0; a < CityLand.AREAS; a++) row[at + a] *= k;
            row[at + timber] *= k;
        }
        LandManager copy = new LandManager();
        copy.install(CityLand.restore(t.seed(), centre, t.centreRectsState(), holdings, t.partFieldsState(), null),
                town.getDepletionState(), town.getWorldTotalsState(), town.getWorldSeaTheta());
        copy.getMarket().restoreOffers(town.getMarket().getOffersState(), town.getMarket().getNextOfferId());
        copy.restoreOwnedSqFt(town.getOwnedSqFt() * k);
        return copy;
    }

    /* ==================================================================
       12. LAND IS PRICED IN DOLLARS (0.7.6)

       Jerus: "when you buy land, make it so that it costs USD not domestic
       currency". A land office reading a rate that moves under it: the
       listing's dollar prices stay put, what they cost here is exactly
       usd x rate on the day, and what businesses pay does not read the
       rate at all. Against a twin at the founding rate, so "does not move"
       is measured against something.
       ================================================================== */
    static void inDollars() {
        System.out.println("\n--- land is priced in dollars; what it costs here is the day's rate ---");

        double[] rate = { ForeignAccounts.OPENING_RATE };
        LandManager dollars = new LandManager(() -> rate[0]);
        dollars.updateMarket(0);
        LandManager atPar = new LandManager();
        atPar.updateMarket(0);
        LandParcel held = dollars.getListing().get(2);
        double heldUsd = held.getPriceUsd();

        rate[0] = 1.6;
        dollars.updateMarket(0);
        check("a listed parcel's dollar price does not move when the rate does",
                dollars.getMarket().find(held.getId()).getPriceUsd(), heldUsd);
        check("...nor the office's ground price in dollars",
                dollars.getGroundUsdPerSqFt(), atPar.getGroundUsdPerSqFt());
        check("...while what it quotes here is that at today's rate",
                dollars.getAcquisitionCostPerSqFt(), dollars.getGroundUsdPerSqFt() * 1.6);
        check("...and what businesses pay does not read the rate at all",
                dollars.getPricePerSqFt(), atPar.getPricePerSqFt());
        check("...so the margin carries the currency",
                dollars.getMarginPerSqFt(),
                dollars.getPricePerSqFt() - dollars.getGroundUsdPerSqFt() * 1.6);

        double paid = dollars.buyParcel(held.getId(), 1e9, 0);
        check("what a parcel costs is exactly its dollars times the rate", paid, heldUsd * 1.6);
        check("...and that is what the month's land purchases carry",
                dollars.getLandPurchasesThisMonth(), heldUsd * 1.6);
        LandParcel next = dollars.getListing().get(0);
        check("...and a parcel it cannot pay that for is refused",
                dollars.buyParcel(next.getId(), next.getPriceUsd() * 1.6 - 1, 0), 0);

        // A reform divides the local money; the dollars are the world's.
        java.util.List<LandParcel> board = dollars.getListing();
        double saleBefore = dollars.getPricePerSqFt();
        double groundBefore = dollars.getGroundUsdPerSqFt();
        dollars.redenominate(.01);
        boolean sameBoard = board.size() == dollars.getListing().size();
        for (int i = 0; sameBoard && i < board.size(); i++) {
            sameBoard = board.get(i).getId() == dollars.getListing().get(i).getId()
                    && board.get(i).getPriceUsd() == dollars.getListing().get(i).getPriceUsd();
        }
        assertTrue("a currency reform leaves the listing's dollar prices alone", sameBoard);
        check("...and the office's dollar ground price",
                dollars.getGroundUsdPerSqFt(), groundBefore);
        check("...while what businesses pay is reformed with every local price",
                dollars.getPricePerSqFt(), saleBefore * .01);
    }

    /* ==================================================================
       13. THE TWO WAYS TO PAY (0.7.6)

       Jerus: "a little toggle at the top to choose, when you buy land, to
       use up your USD reserves or to convert cash into usd exactly to buy
       the land, and the default is that you convert." Two cities founded
       the same way and ticked once (so the purchase is in an ordinary
       window, not the founding one), the rate held at 1.60 so local money
       and dollars differ - and neither currency defends itself with its
       vault, so the only difference between them is the way they paid.
       ================================================================== */
    static Game dollarCity(String label) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> { g.newGame(); g.toggleNextMonth(); });
        g.getForeignAccounts().pinRate(1.6);
        return g;
    }

    static double[] moved(double[] before, double[] after) {
        double[] m = new double[before.length];
        for (int i = 0; i < m.length; i++) m[i] = after[i] - before[i];
        return m;
    }

    static void bothWays() throws Exception {
        System.out.println("\n--- converting: the treasury buys the dollars and pays them over ---");

        Game convert = dollarCity("landcheck-convert");
        Game vault = dollarCity("landcheck-vault");
        assertTrue("fixture: a new city converts by default", !convert.isLandPaidFromVault());
        vault.setLandPaidFromVault(true);

        LandParcel plot = convert.getLandManager().getMarket().bestValue();
        double usd = plot.getPriceUsd();
        double rate = convert.getForeignAccounts().getRate();
        assertTrue("fixture: both cities list the same plot at the same dollars",
                vault.getLandManager().getMarket().find(plot.getId()) != null
                        && vault.getLandManager().getMarket().find(plot.getId()).getPriceUsd() == usd);

        double cash = convert.getCash();
        double dollarsHeld = convert.getForeignAccounts().getReservesUsd();
        double bought = convert.getForeignAccounts().getBoughtThisMonth();
        double[] pools = MoneyAudit.pools(convert);
        assertTrue("fixture: the plot is bought", convert.buyLandParcel(plot.getId()));
        double[] shift = moved(pools, MoneyAudit.pools(convert));

        check("converting: the treasury pays usd x rate", convert.getCash(), cash - usd * rate);
        check("...the vault ends where it began",
                convert.getForeignAccounts().getReservesUsd(), dollarsHeld);
        check("...the seller is paid the parcel's dollars",
                convert.getForeignAccounts().getLandUsdPending(), usd);
        check("...and nothing was bought for the vault",
                convert.getForeignAccounts().getBoughtThisMonth(), bought);
        double others = 0;
        for (int i = 1; i < shift.length; i++) others += Math.abs(shift[i]);
        check("...the city's pool fell by exactly that - money across the edge", shift[0], -usd * rate);
        check("...and no other pool took it", others, 0);
        assertTrue("...and the receipt names the conversion",
                convert.getLastLandReceipt().contains("converting"));

        System.out.println("\n--- from the vault: the dollars leave it, and no money moves ---");

        cash = vault.getCash();
        dollarsHeld = vault.getForeignAccounts().getReservesUsd();
        double intervention = vault.getForeignAccounts().getLifetimeIntervention();
        pools = MoneyAudit.pools(vault);
        assertTrue("fixture: the vault can pay for the plot", dollarsHeld > usd);
        assertTrue("fixture: the plot is bought", vault.buyLandParcel(plot.getId()));
        shift = moved(pools, MoneyAudit.pools(vault));

        check("from the vault: the treasury's cash does not move", vault.getCash(), cash);
        check("...the vault falls by the parcel's dollars",
                vault.getForeignAccounts().getReservesUsd(), dollarsHeld - usd);
        check("...its record by their local price, as a sale's would",
                vault.getForeignAccounts().getLifetimeIntervention(), intervention - usd * rate);
        double any = 0;
        for (double d : shift) any += Math.abs(d);
        check("...and no pool moved at all", any, 0);
        TreasuryJournal.Entry entry = null;
        for (TreasuryJournal.Entry e : vault.getTreasuryJournalBook().thisMonth()) {
            if (e.label().startsWith("Bought land with US$")) entry = e;
        }
        assertTrue("...the journal names it", entry != null);
        check("...at usd x rate, the other way up from the budget's land line",
                entry == null ? 0 : entry.amount(), usd * rate);
        assertTrue("...and the receipt says it came out of the vault",
                vault.getLastLandReceipt().contains("out of the vault"));

        System.out.println("\n--- and each month closes ---");

        quietly(convert::toggleNextMonth);
        quietly(vault::toggleNextMonth);
        for (Game g : new Game[] { convert, vault }) {
            String way = g == convert ? "converting" : "from the vault";
            assertTrue("the month after land bought " + way + " closes its audit",
                    Math.abs(g.getLastMoneyAudit().relative()) < 1e-9);
            assertTrue("...nothing moved after it struck", Math.abs(g.getPostAuditDrift()) < 1e-6);
            check("...the budget carries the land at usd x rate",
                    g.getEconomyManager().getNationalAccounts().getLandPurchases(), usd * rate);
            check("...and the month's dollars paid for it",
                    g.getForeignAccounts().getLandUsdThisMonth(), usd);
            check("...which cost here what the budget's line carries",
                    g.getForeignAccounts().getLandLocalThisMonth(), usd * rate);
        }
        check("the vault's part of them, converting", convert.getForeignAccounts()
                .getLandUsdFromVaultThisMonth(), 0);
        check("...and from the vault", vault.getForeignAccounts()
                .getLandUsdFromVaultThisMonth(), usd);
        check("the bridge leaves the same over either way: the journal carries the vault's",
                vault.getTreasuryResidual(), convert.getTreasuryResidual());

        System.out.println("\n--- a short vault pays what it holds and converts the rest ---");

        LandParcel dear = vault.getLandManager().getMarket().bestValue();
        double dearUsd = dear.getPriceUsd();
        double half = dearUsd / 2;
        rate = vault.getForeignAccounts().getRate();
        quietly(() -> vault.sellForeignCurrency(
                (vault.getForeignAccounts().getReservesUsd() - dearUsd / 2) * vault.getForeignAccounts().getRate()));
        double held = vault.getForeignAccounts().getReservesUsd();
        assertTrue("fixture: the vault holds about half the parcel",
                Math.abs(held - half) < 1e-3 && held < dearUsd);
        cash = vault.getCash();
        assertTrue("a purchase never fails for the toggle's sake", vault.buyLandParcel(dear.getId()));
        check("the vault paid what it held", vault.getForeignAccounts().getReservesUsd(), 0);
        check("...and the rest was converted from cash", vault.getCash(), cash - (dearUsd - held) * rate);
        assertTrue("...and the receipt says so",
                vault.getLastLandReceipt().contains("held only"));
        System.out.println("   " + vault.getLastLandReceipt());

        System.out.println("\n--- the toggle survives a save, and an older listing reads as dollars ---");

        GameFiles files = GameFiles.scratch("landcheck-save");
        Game saved = new Game(files);
        quietly(() -> { saved.newGame(); saved.toggleNextMonth(); });
        saved.getForeignAccounts().pinRate(1.6);
        saved.setLandPaidFromVault(true);
        java.util.List<LandParcel> board = saved.getLandListing();
        quietly(() -> saved.saveGame(4, "land in dollars"));

        Game[] back = new Game[1];
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(4); });
        assertTrue("fixture: it loads", back[0].getLoadFailure() == null);
        assertTrue("the toggle survives a save", back[0].isLandPaidFromVault());
        boolean same = board.size() == back[0].getLandListing().size();
        for (int i = 0; same && i < board.size(); i++) {
            same = board.get(i).getPriceUsd() == back[0].getLandListing().get(i).getPriceUsd();
        }
        assertTrue("...and a dollar listing comes back to the cent", same);
        check("...and the office's dollar quote with it",
                back[0].getLandManager().getGroundUsdPerSqFt(),
                saved.getLandManager().getGroundUsdPerSqFt());

        /*
         * AN OLDER SAVE, written by hand from this one: format 30, no toggle
         * key, the parcels in the old marker with LOCAL prices that are not
         * this listing's dollars at any rate, and the office's prices three
         * slots long. Its quote reads as dollars at the loading rate (0.7.6);
         * since 0.7.57 its parcels are not read at all - the conversion lists
         * offers in their place, priced at that quote (forty until 0.7.66;
         * every place with room of twenty-four since).
         */
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                java.nio.file.Files.readString(files.saveFile(4))).getAsJsonObject();
        assertTrue("fixture: the save carried the toggle", json.has("landPaidFromVault"));
        json.remove("landPaidFromVault");
        for (String key : new String[] { "worldSeaTheta", "worldTotals", "landCentre", "landCentreRects", "landHoldings",
                "landPartFields", "landConverted", "landLanes", "landPurchases", "landOffers", "nextOfferId", "depletion" }) json.remove(key);
        json.addProperty("saveFormat", LandConversion.LAST_FORMAT_BEFORE);
        com.google.gson.JsonArray listing = new com.google.gson.JsonArray();
        listing.add(-5.0);
        listing.add(10.0);
        int parcels = 9;
        for (int i = 0; i < parcels; i++) {
            for (double v : new double[] { i + 1, 250_000 + i * 1000, 1_000.0 + i * 10, 0, 0 }) listing.add(v);
        }
        json.add("landListing", listing);
        com.google.gson.JsonArray prices = json.getAsJsonArray("landMarketPrices");
        double localQuote = prices.get(0).getAsDouble();
        while (prices.size() > 3) prices.remove(prices.size() - 1);
        java.nio.file.Files.writeString(files.saveFile(4), json.toString());

        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(4); });
        Game old = back[0];
        assertTrue("fixture: the older save loads", old.getLoadFailure() == null);
        double loadRate = old.getForeignAccounts().getRate();
        check("fixture: at the rate it was saved at", loadRate, 1.6);
        assertTrue("an older save converts", !old.isLandPaidFromVault());
        check("the office's local quote reads as dollars at the loading rate",
                old.getLandManager().getGroundUsdPerSqFt(), localQuote / loadRate);
        int waiting = 0;
        for (int side = 0; side < CityLand.SIDES; side++) waiting += old.getLandManager().getMarket().emptyOn(side);
        check("its nine parcels are not read: every place listed or waiting for room in their place",
                old.getLandListing().size() + waiting, LandMarket.OFFERS);
        boolean atTheQuote = true;
        for (LandParcel p : old.getLandListing()) {
            double[] km2 = new double[CityLand.AREAS], amounts = new double[CityLand.KINDS];
            for (int a = 0; a < CityLand.AREAS; a++) km2[a] = p.getKm2(a);
            for (Resource r : Resource.values()) amounts[r.ordinal()] = p.getAmount(r);
            atTheQuote &= p.getPriceUsd() == LandMarket.price(km2, amounts, old.getLandManager().getGroundUsdPerSqFt(),
                    old.getLandManager().getUsPriceLevel());
        }
        assertTrue("...each priced at that quote", atTheQuote);
    }

    /* ==================================================================
       14. THE OFFICE IN SQUARE KILOMETRES (0.7.13)

       Jerus: "purely for visual purposes, instead of blocks, say km^2". The
       model keeps square feet; the office shows the same figure converted
       exactly, to three significant figures so the smallest plot it sells
       does not read 0.00.
       ================================================================== */
    static void inSquareKilometres() {
        System.out.println("\n--- the office in square kilometres ---");

        check("a square foot is 0.3048 m squared, exactly", LandManager.SQ_M_PER_SQ_FT, .3048 * .3048);
        check("a block is its square feet in square kilometres",
                LandManager.km2(LandManager.BLOCK_SQ_FT),
                LandManager.BLOCK_SQ_FT * LandManager.SQ_M_PER_SQ_FT / LandManager.SQ_M_PER_KM2);
        String block = LandManager.km2Words(LandManager.BLOCK_SQ_FT);
        System.out.println("   one block: " + block);
        assertTrue("...which reads 0.00929, not 0.00", block.startsWith("0.00929 "));
        LandManager office = new LandManager();
        office.updateMarket(0);
        boolean allRead = true;
        for (LandParcel parcel : office.getListing()) {
            String words = LandManager.km2Words(parcel.getSizeSqFt());
            double read = Double.parseDouble(words.substring(0, words.indexOf(' ')));
            double exact = LandManager.km2(parcel.getSizeSqFt());
            if (!(read > 0) || Math.abs(read - exact) > exact * 5e-3) allRead = false;
        }
        assertTrue("every plot on offer reads within half a percent of its area, and none as nothing", allRead);
    }

    /* ==================================================================
       24. THE UNITS THE PLAYER READS (0.7.68, batch M4)

       The project's spec-grid.md 2.5 and star 10: every area the player
       reads is in square metres under a hundredth of a square kilometre and
       in square kilometres from it, three figures, grouped from a thousand,
       through LandManager.areaWords(); a part of an area in its whole's
       unit (partFigure()); a ground price a square metre (perM2()). The
       model keeps square feet and prices a square foot, so each is the same
       figure converted exactly, and the unit is picked after the rounding.
       ================================================================== */
    static void inThePlayersUnits() {
        System.out.println("\n--- the units the player reads ---");
        String m2 = " m\u00b2", km2 = " km\u00b2";
        double line = LandManager.M2_WORDS_BELOW / LandManager.SQ_M_PER_KM2;

        String block = LandManager.areaWords(LandManager.BLOCK_SQ_FT);
        System.out.println("   one of spec-land's blocks: " + block);
        assertTrue("an area under a hundredth of a km2 reads in m2", block.endsWith(m2));
        assertTrue("...within half a percent of it, to three figures",
                readsAs(block, LandManager.BLOCK_SQ_FT * LandManager.SQ_M_PER_SQ_FT));
        assertTrue("the line itself reads in km2",
                LandManager.areaWords(LandManager.sqFt(line)).equals("0.01" + km2));
        assertTrue("...and so does an area that rounds up to it, never \"10,000 m2\"",
                LandManager.areaWords(LandManager.sqFt(line * (1 - 1e-4))).equals("0.01" + km2));
        assertTrue("one that rounds under it reads in m2",
                LandManager.areaWords(LandManager.sqFt(line * (1 - 1e-3))).endsWith(m2));
        assertTrue("no ground reads 0 m2", LandManager.areaWords(0).equals("0" + m2));
        String huge = LandManager.areaWords(LandManager.sqFt(1_760_034.2));
        System.out.println("   ten billion people's dry ground (spec-grid 2.5): " + huge);
        assertTrue("km2 are grouped from a thousand", huge.equals(String.format("%,d", 1_760_000L) + km2));

        LandManager office = new LandManager();
        office.updateMarket(0);
        boolean allRead = true;
        for (LandParcel parcel : office.getListing()) {
            String words = LandManager.areaWords(parcel.getSizeSqFt());
            if (!readsAs(words, parcel.getSizeSqFt() * LandManager.SQ_M_PER_SQ_FT)) allRead = false;
        }
        assertTrue("every offer standing reads within half a percent of its area, in its unit", allRead);

        double whole = LandManager.sqFt(line / 2), part = whole * .9;
        assertTrue("a part reads bare, in its whole's m2",
                readsAs(LandManager.partFigure(part, whole) + m2, part * LandManager.SQ_M_PER_SQ_FT));
        whole = LandManager.sqFt(line * 10);
        assertTrue("...and in its whole's km2, however small",
                readsAs(LandManager.partFigure(whole / 100, whole) + km2, whole / 100 * LandManager.SQ_M_PER_SQ_FT));

        near("a price a square metre is a square foot's over SQ_M_PER_SQ_FT",
                LandManager.perM2(LandMarket.openingUsdPerSqFt()) * LandManager.SQ_M_PER_SQ_FT,
                LandMarket.openingUsdPerSqFt(), 1e-12);
        assertTrue("...so a square metre costs more than a square foot",
                LandManager.perM2(LandMarket.openingUsdPerSqFt()) > LandMarket.openingUsdPerSqFt());
    }

    /** Whether words like "7,430 m2" or "0.0301 km2" read `squareMetres` to half a percent, never as nothing. */
    static boolean readsAs(String words, double squareMetres) {
        int at = words.lastIndexOf(' ');
        double figure = Double.parseDouble(words.substring(0, at).replace(",", ""));
        double read = words.endsWith("km\u00b2") ? figure * LandManager.SQ_M_PER_KM2 : figure;
        return read > 0 && Math.abs(read - squareMetres) <= squareMetres * 5e-3;
    }

    /* ==================================================================
       15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13)

       Jerus: "if you are on buy by converting and you dont have enough, you
       can stilll click buy, just the popup to issue debt appears, but if you
       are in buy with reserves, and click buy, then pop up to issue foreign
       debt should appear (aka the short or the 20y, like with buildings)".
       The page prints the model's figures (Game, WHEN THE CITY IS SHORT, AND
       SEVERAL AT ONCE), so they are asserted here: each offer sized to the
       gap in the money the toggle pays in, the plot bought once it is
       taken, and the month after closing its audit. Each fixture causes its
       shortage: the treasury given 40% of the plot, or the vault sold down to
       30% of it; and the window abroad shut by its own rule, a city that
       borrows dollars while it sells nothing abroad.
       ================================================================== */
    static void whenShort() throws Exception {
        System.out.println("\n--- short while converting: the build screen's two offers, sized to the gap ---");

        for (String paper : new String[] { "bond", "note" }) {
            Game g = dollarCity("landcheck-short-" + paper);
            LandParcel plot = g.landShelf().get(0);
            java.util.List<Integer> just = java.util.List.of(plot.getId());
            double price = plot.localPrice(g.getForeignAccounts().getRate());
            g.setCashForTest(price * .4);    // the treasury holds 40% of the plot
            double gap = g.landCashGap(just);
            assertTrue("fixture (" + paper + "): the treasury is short of the plot",
                    gap > 0 && !g.canAffordParcel(plot));
            check("the gap is the plot's local price less the cash", gap, price - g.getCash());
            assertTrue("...so its button opens the funding page", g.landNeedsFunding(just));
            boolean bond = paper.equals("bond");
            double granule = bond ? Game.BUILD_BOND_GRANULE : Game.BUILD_NOTE_GRANULE;
            DebtQuote quote = bond
                    ? g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, granule)
                    : g.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, granule);
            assertTrue("the " + paper + "'s cash covers the gap", quote.cashReceived() >= gap - 1e-9);
            assertTrue("...by no more than a granule of face", quote.cashReceived() - gap <= granule);
            int debts = g.getDebtManager().getDebt().size();
            quietly(() -> {
                if (bond) g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, granule);
                else g.handleTBillLogic(gap, Game.BUILD_NOTE_MONTHS, granule);
            });
            assertTrue("...issued, it is on the books", g.getDebtManager().getDebt().size() == debts + 1);
            double owned = g.getLandManager().getOwnedSqFt();
            assertTrue("...and the plot is bought", g.buyLandParcels(just) == 1);
            check("...the city owns it", g.getLandManager().getOwnedSqFt(), owned + plot.getSizeSqFt());
            check("...and the treasury keeps what the solver left over, as the build screen's does",
                    g.getCash(), quote.cashReceived() - gap);
            quietly(g::toggleNextMonth);
            assertTrue("...and the month after closes its audit", Math.abs(g.getLastMoneyAudit().relative()) < 1e-9);
            assertTrue("...with nothing moved after it struck", Math.abs(g.getPostAuditDrift()) < 1e-6);
        }

        System.out.println("\n--- short from the vault: dollar paper, sized to the dollar gap, into the vault ---");

        for (String type : new String[] { "Term", "Note" }) {
            Game g = dollarCity("landcheck-vault-" + type);
            g.setLandPaidFromVault(true);
            LandParcel plot = g.landShelf().get(0);
            java.util.List<Integer> just = java.util.List.of(plot.getId());
            double usd = plot.getPriceUsd();
            quietly(() -> g.sellForeignCurrency(      // the vault sold down to 30% of the plot
                    (g.getForeignAccounts().getReservesUsd() - usd * .3) * g.getForeignAccounts().getRate()));
            double held = g.getForeignAccounts().getReservesUsd();
            double gapUsd = g.landVaultGapUsd(just);
            assertTrue("fixture (" + type + "): the vault holds 30% of the plot", Math.abs(held - usd * .3) < 1e-6);
            check("the gap is in dollars: the plot less the vault", gapUsd, usd - held);
            assertTrue("fixture: the cash would cover the rest", g.canAffordParcel(plot) && g.landTopUpCovers(just));
            assertTrue("...and still the button opens the funding page: the rest converted is a choice, not the default",
                    g.landNeedsFunding(just));
            assertTrue("fixture: the window abroad is open", g.foreignWindowOpen());
            boolean bond = type.equals("Term");
            int term = bond ? Game.BUILD_BOND_YEARS : Game.BUILD_NOTE_MONTHS;
            double granule = bond ? Game.BUILD_BOND_GRANULE : Game.BUILD_NOTE_GRANULE;
            DebtQuote quote = g.quoteForeignForCash(type, gapUsd, term, granule);
            assertTrue("the dollar " + (bond ? "bond" : "note") + "'s dollars cover the gap",
                    quote.cashReceived() >= gapUsd - 1e-9);
            assertTrue("...by no more than a granule of face", quote.cashReceived() - gapUsd <= granule);
            check("...at the world's curve: the existing dollar quote at that face",
                    quote.marketRate(), g.quoteForeign(type, quote.requested(), term, granule).marketRate());
            double cash = g.getCash();
            quietly(() -> g.handleForeignForCash(type, gapUsd, term, granule, true));
            assertTrue("...issued abroad, it is dollar paper on the books", g.getDebtManager().hasForeignDebt());
            check("...the dollars land in the vault", g.getForeignAccounts().getReservesUsd(),
                    held + quote.cashReceived());
            check("...and the treasury's cash is where it was", g.getCash(), cash);
            assertTrue("...and the plot is bought", g.buyLandParcels(just) == 1);
            check("...out of the vault", g.getForeignAccounts().getReservesUsd(),
                    held + quote.cashReceived() - usd);
            check("...and no cash converted for it", g.getCash(), cash);
            quietly(g::toggleNextMonth);
            assertTrue("...and the month after closes its audit", Math.abs(g.getLastMoneyAudit().relative()) < 1e-9);
            assertTrue("...with nothing moved after it struck", Math.abs(g.getPostAuditDrift()) < 1e-6);
            check("...its dollars, the whole plot, the vault's", g.getForeignAccounts().getLandUsdFromVaultThisMonth(), usd);
        }

        System.out.println("\n--- with the window abroad shut, no dollar offer ---");

        Game g = dollarCity("landcheck-shut");
        g.setLandPaidFromVault(true);
        quietly(() -> g.handleForeignLogic("Note", 1_000, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE, true));
        assertTrue("fixture: the window is shut - it owes dollars and sells nothing abroad", !g.foreignWindowOpen());
        System.out.println("   the window: " + g.foreignWindowReason());
        LandParcel plot = g.landShelf().get(0);
        java.util.List<Integer> just = java.util.List.of(plot.getId());
        double usd = plot.getPriceUsd();
        quietly(() -> g.sellForeignCurrency(
                (g.getForeignAccounts().getReservesUsd() - usd * .3) * g.getForeignAccounts().getRate()));
        double gapUsd = g.landVaultGapUsd(just);
        assertTrue("fixture: the vault is short of the plot", gapUsd > 0 && g.landNeedsFunding(just));
        assertTrue("no dollar bond is quoted",
                g.quoteForeignForCash("Term", gapUsd, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE).isEmpty());
        assertTrue("...nor a dollar note",
                g.quoteForeignForCash("Note", gapUsd, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE).isEmpty());
        int debts = g.getDebtManager().getDebt().size();
        String[] said = new String[1];
        quietly(() -> said[0] = g.handleForeignForCash("Term", gapUsd, Game.BUILD_BOND_YEARS,
                Game.BUILD_BOND_GRANULE, true));
        assertTrue("...and asked anyway, nothing is issued", g.getDebtManager().getDebt().size() == debts);
        assertTrue("...and it says why, in the window's own words", said[0].contains(g.foreignWindowReason()));
        assertTrue("what remains is offered: the vault's dollars, the rest converted", g.landTopUpCovers(just));
        double held = g.getForeignAccounts().getReservesUsd(), cash = g.getCash();
        assertTrue("...which buys the plot", g.buyLandParcels(just) == 1);
        check("...the vault emptied into it", g.getForeignAccounts().getReservesUsd(), 0);
        check("...and the rest converted from cash", g.getCash(),
                cash - (usd - held) * g.getForeignAccounts().getRate());
        quietly(g::toggleNextMonth);
        assertTrue("...and the month after closes its audit", Math.abs(g.getLastMoneyAudit().relative()) < 1e-9);
    }

    /* ==================================================================
       16. THE NEXT N PLOTS, AT ONCE (0.7.13)

       Jerus: "add a button to buy multiple, so for example buy the next 5
       land options, and then also debt popup appears if not enough". Two
       cities founded alike; one buys the first five plots on the office's
       shelf in one go (Game.buyLandParcels()), the other one at a time
       (buyLandParcel()), and they must end the same to the cent - the same
       plots, the same prices, the same trail - converting, and from a vault
       that runs dry half way through, where the batch crosses from the vault
       to converting as five clicks would. A purchase moves only the plots
       listed after it (LandMarket, WHAT IS LISTED STAYS LISTED): asserted
       too, since it is why the two are the same. Then a total the treasury
       cannot cover opens the funding page, sized to the whole gap.
       ================================================================== */
    static void severalAtOnce() throws Exception {
        for (boolean fromVault : new boolean[] { false, true }) {
            String way = fromVault ? "from the vault" : "converting";
            System.out.println("\n--- the next five plots, " + way + ": at once is one by one ---");

            Game atOnce = dollarCity("landcheck-five-" + fromVault);
            Game oneByOne = dollarCity("landcheck-each-" + fromVault);
            java.util.List<Integer> ids = atOnce.nextLandParcels(5);
            java.util.List<LandParcel> shelf = atOnce.landShelf();
            boolean first = ids.size() == 5;
            for (int i = 0; first && i < 5; i++) first = shelf.get(i).getId() == ids.get(i);
            boolean ascending = true;
            for (int i = 1; i < shelf.size(); i++) {
                if (shelf.get(i).getUsdPerSqFt() < shelf.get(i - 1).getUsdPerSqFt()) ascending = false;
            }
            assertTrue("the next five are the first five on the office's shelf", first);
            assertTrue("...which is cheapest ground first", ascending);
            assertTrue("fixture: both cities list the same five", ids.equals(oneByOne.nextLandParcels(5)));
            double listed = 0;
            for (int id : ids) listed += atOnce.getLandManager().getMarket().find(id).getPriceUsd();
            check("their price together is their listed prices added", atOnce.landPriceUsd(ids), listed);
            check("...and in local money at today's rate", atOnce.landPriceLocal(ids),
                    listed * atOnce.getForeignAccounts().getRate());

            if (fromVault) {
                for (Game g : new Game[] { atOnce, oneByOne }) {
                    g.setLandPaidFromVault(true);
                    double keep = listed * .5;    // the vault holds half of the five
                    quietly(() -> g.sellForeignCurrency(
                            (g.getForeignAccounts().getReservesUsd() - keep) * g.getForeignAccounts().getRate()));
                }
                assertTrue("fixture: the vault runs dry part way through the five",
                        atOnce.landVaultGapUsd(ids) > 0 && atOnce.canAffordLandParcels(ids));
            } else {
                assertTrue("fixture: the treasury covers the five", !atOnce.landNeedsFunding(ids));
            }

            int bought = atOnce.buyLandParcels(ids);
            java.util.Map<Integer, Double> before = new java.util.HashMap<>();
            for (int id : ids) before.put(id, oneByOne.getLandManager().getMarket().find(id).getPriceUsd());
            boolean stood = true;
            for (int i = 0; i < ids.size(); i++) {
                oneByOne.buyLandParcel(ids.get(i));
                for (int j = i + 1; j < ids.size(); j++) {
                    LandParcel still = oneByOne.getLandManager().getMarket().find(ids.get(j));
                    if (still == null || still.getPriceUsd() != before.get(ids.get(j))) stood = false;
                }
            }
            assertTrue("all five were bought at once", bought == 5);
            assertTrue("each purchase left the plots still to come at their listed price", stood);
            check("the same cash, " + way, atOnce.getCash(), oneByOne.getCash());
            check("...the same vault", atOnce.getForeignAccounts().getReservesUsd(),
                    oneByOne.getForeignAccounts().getReservesUsd());
            check("...the same land owned", atOnce.getLandManager().getOwnedSqFt(),
                    oneByOne.getLandManager().getOwnedSqFt());
            check("...the same land purchases on the month's budget",
                    atOnce.getLandManager().getLandPurchasesThisMonth(),
                    oneByOne.getLandManager().getLandPurchasesThisMonth());
            check("...the same deposits", atOnce.getLandManager().getIronDeposits(),
                    oneByOne.getLandManager().getIronDeposits());
            boolean sameShelf = atOnce.landShelf().size() == oneByOne.landShelf().size();
            for (int i = 0; sameShelf && i < atOnce.landShelf().size(); i++) {
                sameShelf = atOnce.landShelf().get(i).getId() == oneByOne.landShelf().get(i).getId()
                        && atOnce.landShelf().get(i).getPriceUsd() == oneByOne.landShelf().get(i).getPriceUsd();
            }
            assertTrue("...and the same plots on offer after, at the same prices", sameShelf);
            assertTrue("the receipt names the five", atOnce.getLastLandReceipt().startsWith("Bought 5 plots"));
            quietly(atOnce::toggleNextMonth);
            quietly(oneByOne::toggleNextMonth);
            assertTrue("the month after closes its audit, at once", Math.abs(atOnce.getLastMoneyAudit().relative()) < 1e-9);
            assertTrue("...and one by one", Math.abs(oneByOne.getLastMoneyAudit().relative()) < 1e-9);
            check("...and the two cities end it with the same cash", atOnce.getCash(), oneByOne.getCash());
            check("...the month's dollars for land the five's, at once", atOnce.getForeignAccounts().getLandUsdThisMonth(), listed);
            check("...and one by one", oneByOne.getForeignAccounts().getLandUsdThisMonth(), listed);
            check("...the vault's part of them the same either way",
                    atOnce.getForeignAccounts().getLandUsdFromVaultThisMonth(),
                    oneByOne.getForeignAccounts().getLandUsdFromVaultThisMonth());
            if (fromVault) {
                assertTrue("fixture: the vault paid part of them and not all",
                        atOnce.getForeignAccounts().getLandUsdFromVaultThisMonth() > 0
                                && atOnce.getForeignAccounts().getLandUsdFromVaultThisMonth() < listed);
            }
        }

        System.out.println("\n--- a total the treasury cannot cover opens the funding page, for the whole gap ---");

        Game g = dollarCity("landcheck-five-short");
        java.util.List<Integer> ids = g.nextLandParcels(5);
        double rate = g.getForeignAccounts().getRate();
        LandParcel firstPlot = g.getLandManager().getMarket().find(ids.get(0));
        LandParcel second = g.getLandManager().getMarket().find(ids.get(1));
        g.setCashForTest(firstPlot.localPrice(rate) + second.localPrice(rate) / 2);   // one and a half plots
        assertTrue("fixture: the first plot alone is covered", !g.landNeedsFunding(ids.subList(0, 1)));
        assertTrue("the five are not, and open the funding page", g.landNeedsFunding(ids));
        double gap = g.landCashGap(ids);
        check("...for the whole gap", gap, g.landPriceLocal(ids) - g.getCash());
        DebtQuote quote = g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        assertTrue("the bond's cash covers it", quote.cashReceived() >= gap - 1e-9);
        quietly(() -> g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        assertTrue("...and, issued, the five are bought", g.buyLandParcels(ids) == 5);
        check("...leaving what the solver left over", g.getCash(), quote.cashReceived() - gap);
        quietly(g::toggleNextMonth);
        assertTrue("...and the month after closes its audit", Math.abs(g.getLastMoneyAudit().relative()) < 1e-9);
    }

    /* ==================================================================
       17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26)

       Jerus, on the screens not yet redone: "the others are still full of
       text and the design could be more intuitive and fun". The land office
       redrawn takes three things from the model: the going rate each plot
       is judged against - the median the screen took of the listing,
       LandMarket.goingUsdPerSqFt() now; the GROUND row its ground-free cell
       is coloured by - CityNeeds.ground(), the row NEEDS YOU lists, which
       Build's LAND FREE and the left panel read too, so none of them colours
       the ground used; and the receipt, which the model always wrote but in
       its own thousands ("US$101,800k"), in the screens' money now
       (Formats.amount() and rate()). Hand-made listings and a city's free
       ground set by hand cause each case.
       ================================================================== */
    static void theOfficesFigures() {
        System.out.println("\n--- the going rate is the listing's median, a square foot ---");

        LandMarket market = new LandMarket();
        // Nine offers of different sizes, priced so the median a square foot
        // (US$5) is neither the mean (US$14) nor the median of the prices.
        double[] perSqFt = { .007, .001, .090, .003, .005, .002, .008, .004, .006 };
        double[] sizes   = { 200_000, 9_000_000, 400_000, 1_000_000, 300_000, 7_000_000, 500_000,
                             2_000_000, 600_000 };
        market.putOffers(handMade(perSqFt, sizes, 9));
        assertTrue("fixture: the nine hand-made offers are listed", market.getListing().size() == 9);
        check("the going rate is the median of the offers' dollars a square foot",
                market.goingUsdPerSqFt(), .005);
        double mean = 0;
        for (double r : perSqFt) mean += r / perSqFt.length;
        assertTrue("...not their mean, which the one dear offer drags", Math.abs(market.goingUsdPerSqFt() - mean) > 1e-3);
        double[] prices = new double[9];
        for (int i = 0; i < 9; i++) prices[i] = perSqFt[i] * sizes[i];
        java.util.Arrays.sort(prices);
        double plotAtMedianPrice = 0;
        for (LandParcel p : market.getListing()) if (p.getPriceUsd() == prices[4]) plotAtMedianPrice = p.getUsdPerSqFt();
        assertTrue("...nor the dollars a square foot of the offer at the median price",
                Math.abs(market.goingUsdPerSqFt() - plotAtMedianPrice) > 1e-6);

        market.putOffers(handMade(new double[] { .001, .002, .003, .004, .005, .006, .007, .090 },
                new double[] { 1e6, 1e6, 1e6, 1e6, 1e6, 1e6, 1e6, 1e6 }, 8));
        check("with an even count, the upper of the two middle prices", market.goingUsdPerSqFt(), .005);
        market.putOffers(handMade(new double[0], new double[0], 0));
        check("with nothing listed, none", market.goingUsdPerSqFt(), 0);

        System.out.println("\n--- the ground's verdict is NEEDS YOU's GROUND row, on free ground alone ---");

        Game g = dollarCity("landcheck-ground");
        LandManager land = g.getLandManager();
        double owned = land.getOwnedSqFt();
        double allocated = land.getAllocatedSqFt();
        double[] frees = { 0, 50_000, CityNeeds.GROUND_YELLOW, CityNeeds.GROUND_YELLOW + 1_000, owned * .05 };
        int[] levels = { 2, 1, 1, 0, 0 };
        for (int i = 0; i < frees.length; i++) {
            land.setAllocatedSqFt(owned - frees[i]);
            CityNeeds.Need alone = CityNeeds.ground(g, CityNeeds.PLAIN);
            CityNeeds.Need listed = null;
            for (CityNeeds.Need n : CityNeeds.measure(g, CityNeeds.PLAIN)) {
                if (n.kind() == CityNeeds.Kind.GROUND) listed = n;
            }
            System.out.printf("   %,.0f sq ft free, %.1f%% used: %s%n", frees[i], land.getUtilisation() * 100,
                    alone.reading());
            check("the GROUND row's level is its free ground's", alone.level(), levels[i]);
            assertTrue("...is the row measure() lists, word for word",
                    listed != null && listed.level() == alone.level() && listed.reading().equals(alone.reading()));
        }
        assertTrue("fixture: the last is over 90% used, and still fine", land.getUtilisation() > .9);
        land.setAllocatedSqFt(allocated);

        System.out.println("\n--- the receipt writes its money as the office does ---");

        Game buyer = dollarCity("landcheck-receipt");
        LandParcel plot = buyer.landShelf().get(0);
        double rate = buyer.getForeignAccounts().getRate();
        String here = buyer.getCurrency().qualifiedSymbol();
        assertTrue("fixture: the plot is bought", buyer.buyLandParcel(plot.getId()));
        String receipt = buyer.getLastLandReceipt();
        System.out.println("   " + receipt);
        assertTrue("the receipt prices the plot as the screens do",
                receipt.contains(" for " + Formats.INSTANCE.amount(plot.getPriceUsd()).replace("$", "US$") + ","));
        assertTrue("...and the cash it converted",
                receipt.contains(Formats.INSTANCE.amount(plot.localPrice(rate)).replace("$", here) + " of cash"));
        assertTrue("...at the rate as the screens write it",
                receipt.contains(here + Formats.INSTANCE.rate(rate) + " to the dollar"));
        assertTrue("...and no thousands with a k stuck on", !receipt.matches(".*[0-9]k[ ,.].*"));
    }

    /** A listing of `n` offers by hand: dry ground of each size at its dollars a square foot, ids from 1, each in its own place on no ground of the grid, no ore. */
    static java.util.List<LandParcel> handMade(double[] perSqFt, double[] sizes, int n) {
        java.util.List<LandParcel> list = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            double km2 = LandManager.km2(sizes[i]);
            list.add(new LandParcel(i + 1, i / LandMarket.OFFERS_A_SIDE, i % LandMarket.OFFERS_A_SIDE, 0, 0, 0, 0, 0,
                    new double[] { km2, km2, 0, 0, 0 }, new int[CityLand.KINDS], new double[CityLand.KINDS],
                    perSqFt[i] * sizes[i], 0));
        }
        return list;
    }

    /* ------------------------- the grid's own tests (0.7.67) ------------------------- */

    /** Whether an offer's rectangle holds or borders ground the city owns (GridCheck.touches()). */
    static boolean touchesOwned(LandGrid g, LandParcel p) {
        return GridCheck.touches(g, p.rect());
    }

    /** Every field of a resource whose centre plot passes `on`, within the nine cells round a city's site and its owned box's: its sites and amount, whole - read off the world's cells. */
    static double[] recountOn(CityLand land, Resource kind, java.util.function.BiPredicate<Long, Long> on) {
        World world = World.of(land.seed());
        LandGrid g = land.grid();
        double far = World.CELL;
        double x0 = Math.min(land.siteX() - far, g.minX()), y0 = Math.min(land.siteY() - far, g.minY());
        double x1 = Math.max(land.siteX() + far, g.maxX()), y1 = Math.max(land.siteY() + far, g.maxY());
        double sites = 0, amount = 0;
        for (int cell : CityLand.cellsUnder(x0, y0, x1, y1)) {
            for (Deposit d : world.fieldsInCell(cell, kind)) {
                if (on.test(d.x(), d.y())) {
                    sites += d.sites();
                    amount += d.amount();
                }
            }
        }
        return new double[] { sites, amount };
    }

    /** An offer's fields of one resource, recounted from the world: every field whose centre plot is one of its rectangle's free plots, whole (spec-land star 12 on the grid). */
    static double[] offerFields(CityLand land, LandParcel offer, Resource kind) {
        return recountOn(land, kind, (x, y) -> offer.contains(x, y) && !land.ownsPlot(x, y));
    }

    /** An offer's five areas counted again, plot by plot from the world's tiles: the plots of its rectangle the city does not own (0.7.67, the books to the plot). */
    static double[] freePlotsCounted(CityLand land, LandParcel p) {
        long[] cls = new long[5];
        byte[] t = new byte[World.TILE * World.TILE];
        World world = World.of(land.seed());
        for (long ty = Math.floorDiv(p.getY0(), World.TILE); ty <= Math.floorDiv(p.getY1() - 1, World.TILE); ty++) {
            for (long tx = Math.floorDiv(p.getX0(), World.TILE); tx <= Math.floorDiv(p.getX1() - 1, World.TILE); tx++) {
                world.tileTerrain(tx, ty, t);
                for (int i = 0; i < t.length; i++) {
                    long x = tx * World.TILE + i % World.TILE, y = ty * World.TILE + i / World.TILE;
                    if (p.contains(x, y) && !land.ownsPlot(x, y)) cls[t[i]]++;
                }
            }
        }
        return GridConversion.km2Of(cls);
    }

    /* ==================================================================
       19. THE LAND ON THE WORLD (0.7.57)

       The project's spec-land.md, batch J1b. The world's totals are kept to
       the tonne: what no city owns, what remains in the city's ground and
       what it has taken out add up to the world's, through a purchase and
       through extraction. The ground is worked out in the order it was
       bought, the centre first. A restated city keeps its iron and what it
       has taken out. Forest grows back. And the best offer for each need.
       ================================================================== */
    static void onTheWorld() {
        System.out.println("\n--- the world's totals are kept to the tonne ---");

        LandManager land = new LandManager();
        land.updateMarket(0);
        CityLand city = land.getCityLand();
        double[] stored = land.getWorldTotalsState();
        double[] again = new World(city.seed()).computeTotals();
        assertTrue("the city stores the world's totals, recomputed to the tonne", java.util.Arrays.equals(stored, again));
        near("...and its sea's level", land.getWorldSeaTheta(), World.of(city.seed()).seaTheta(), 0);

        // A purchase with iron in it, and one more resource it holds, if any.
        Deposit field = nearestUnowned(city, Resource.IRON);
        LandParcel rich = land.getMarket().richest(Resource.IRON);
        for (int i = 0; i < 400 && rich == null && field != null; i++) {
            land.buyParcel(MiningCheck.nearestOffer(land.getMarket(), field.x(), field.y()).getId(), 1e12, 0);
            rich = land.getMarket().richest(Resource.IRON);
        }
        assertTrue("fixture: an offer with iron in it", rich != null);
        double[] w = new double[CityLand.KINDS], u = new double[CityLand.KINDS], o = new double[CityLand.KINDS];
        boolean adds = true;
        for (Resource r : Resource.values()) {
            w[r.ordinal()] = land.getWorldTotal(r);
            u[r.ordinal()] = land.getUnowned(r);
            o[r.ordinal()] = land.getRemaining(r);
            adds &= land.getUnowned(r) + land.getRemaining(r) + land.getExtracted(r) == land.getWorldTotal(r);
        }
        assertTrue("unowned, remaining and extracted add up to the world's, every resource", adds);
        if (rich != null) {
            land.buyParcel(rich.getId(), 1e12, 0);
            boolean moved = true, still = true;
            for (Resource r : Resource.values()) {
                moved &= land.getUnowned(r) == u[r.ordinal()] - rich.getAmount(r)
                        && land.getRemaining(r) == o[r.ordinal()] + rich.getAmount(r);
                still &= land.getUnowned(r) + land.getRemaining(r) + land.getExtracted(r) == w[r.ordinal()];
            }
            assertTrue("a purchase moves exactly what it listed from the world's unowned to the city's ground", moved);
            assertTrue("...and the three still add up to the world's, to the tonne", still);
        }
        double lifted = land.extractIron(1_234_567.5);
        boolean taken = land.getExtracted(Resource.IRON) == lifted && lifted == 1_234_567.5;
        double sum = land.getUnowned(Resource.IRON) + land.getRemaining(Resource.IRON) + land.getExtracted(Resource.IRON);
        assertTrue("extraction moves what was lifted from the ground to the extracted", taken);
        near("...and the three still add up to the world's (to a part in 1e15)", sum,
                land.getWorldTotal(Resource.IRON), 1e-15);

        System.out.println("\n--- a field goes whole to the one piece of ground holding its centre ---");

        /*
         * Since 0.7.64 (batch L), as at 0.7.57: every site and every tonne of
         * a field belongs to the piece of ground holding the field's centre,
         * wherever its sites lie. From 0.7.58 to 0.7.63 (batch J1c) each site
         * went to the piece holding the site's own centre, with its share of
         * the field. Conservation needs every field held once whoever lists
         * what when. On the block grid (0.7.67) the pieces are the holdings and
         * the offers standing, which never share a plot: so the plane near the
         * site is cut into them and the rest, and every field's centre plot is
         * in exactly one - the fields whose sites lie under more than one are
         * the fixture, those the two rules part on.
         *
         * Since 0.7.99 (batch W1) the default site has no field within ten
         * kilometres - the deposits are fewer and bigger, in clusters, and a
         * founding asks no iron - so the city here is founded on the centre
         * plot of the iron field nearest that site on dry ground (foundedOn();
         * the nearest cluster lies in a lake): it holds that field, and the
         * rest of its cluster lies round it.
         */
        Deposit onIron = nearestOnDryGround(new LandManager().getCityLand(), Resource.IRON);
        LandManager tiled = foundedOn(onIron.x(), onIron.y());
        tiled.updateMarket(0);
        CityLand t = tiled.getCityLand();
        // The field its centre holds goes whole with that ground: no offer round the new city carries any of it, though its sites reach under them.
        int sitesUnderOffers = 0;
        boolean noneCarry = t.holdingOf(onIron.x(), onIron.y()) == CityLand.CENTRE;
        for (LandParcel p : tiled.getListing()) {
            int under = 0;
            for (int k = 0; k < onIron.sites(); k++) {
                long[] at = GridConversion.sitePlot(onIron, k);
                if (p.contains(at[0], at[1]) && !t.ownsPlot(at[0], at[1])) under++;
            }
            sitesUnderOffers += under;
            double[] own = offerFields(t, p, Resource.IRON);
            if (under > 0) noneCarry &= p.getSites(Resource.IRON) == own[0] && p.getAmount(Resource.IRON) == own[1];
        }
        System.out.printf("   a city founded on the iron field nearest the default site on dry ground (%d sites, %,.0f Mt, %.1f km out): %d of its sites under"
                        + " its first offers%n", onIron.sites(), onIron.amount() / 1e6,
                LegacyLand.radius(onIron.x() - World.of(t.seed()).foundingX(), onIron.y() - World.of(t.seed()).foundingY()) * World.PLOT_M / 1000,
                sitesUnderOffers);
        assertTrue("fixture: a new city's centre holds an iron field some of whose sites lie under its first offers", sitesUnderOffers > 0);
        assertTrue("no offer of a new city's carries any of the field its centre holds: it goes whole with the ground its centre is on", noneCarry);
        for (int i = 0; i < 24; i++) tiled.buyParcel(tiled.getMarket().cheapest().getId(), 1e12, 0);
        World world = World.of(t.seed());
        int fieldsCut = 0, sitesAcross = 0, sitesAll = 0;
        boolean onePiece = true, sitesWhole = true, offersHold = true;
        double far = 220;
        long[] offerSites = new long[CityLand.KINDS];
        for (LandParcel p : tiled.getListing()) for (Resource r : Resource.values()) offerSites[r.ordinal()] += p.getSites(r);
        long[] counted = new long[CityLand.KINDS];
        for (int cell : CityLand.cellsUnder(t.siteX() - far, t.siteY() - far, t.siteX() + far, t.siteY() + far)) {
            for (Resource r : Resource.values()) {
                if (!r.inFields()) continue;
                for (Deposit d : world.fieldsInCell(cell, r)) {
                    if (LegacyLand.radius(d.x() - t.siteX(), d.y() - t.siteY()) > far) continue;
                    double each = 0;
                    for (int k = 0; k < d.sites(); k++) each += d.siteAmount(k);
                    sitesWhole &= each == d.amount();
                    int pieces = t.ownsPlot(d.x(), d.y()) ? 1 : 0;
                    for (LandParcel p : tiled.getListing()) {
                        if (p.contains(d.x(), d.y()) && !t.ownsPlot(d.x(), d.y())) {
                            pieces++;
                            counted[r.ordinal()] += d.sites();
                        }
                    }
                    // Where its sites lie: under how many pieces (the old rule's test, site by site).
                    java.util.Set<Integer> under = new java.util.HashSet<>();
                    for (int k = 0; k < d.sites(); k++) {
                        long[] at = GridConversion.sitePlot(d, k);
                        int h = t.holdingOf(at[0], at[1]);
                        if (h < 0) for (LandParcel p : tiled.getListing()) if (p.contains(at[0], at[1])) h = -1000 - p.getId();
                        under.add(h);
                    }
                    fieldsCut++;
                    sitesAll += d.sites();
                    if (under.size() > 1) sitesAcross++;
                    onePiece &= pieces <= 1;
                }
            }
        }
        for (Resource r : Resource.values()) offersHold &= !r.inFields() || offerSites[r.ordinal()] >= counted[r.ordinal()];
        System.out.printf("   %d fields centred within %.0f plots of the site (%d sites), the sites of %d of them under more than one piece%n",
                fieldsCut, far, sitesAll, sitesAcross);
        assertTrue("fixture: fields near the site, some with their sites under more than one piece", fieldsCut > 10 && sitesAcross > 0);
        assertTrue("a field's sites' shares of its amount sum to it exactly", sitesWhole);
        assertTrue("every field is held by one piece of ground at most - a holding or an offer standing - whole, wherever its sites lie",
                onePiece && offersHold);

        // The founding field: the iron field nearest the site - until 0.7.98 the one the site's fourth test (an iron field within
        // World.SITE_IRON_KM) found; since 0.7.99 (batch W1) a founding asks no iron, and the nearest lies where its cluster does.
        LandManager fresh = new LandManager();
        fresh.updateMarket(0);
        CityLand t0 = fresh.getCityLand();
        Deposit founding = nearestUnowned(t0, Resource.IRON);
        assertTrue("fixture: an iron field in the nine cells round the founding site", founding != null);
        if (founding != null) {
            LandParcel first = MiningCheck.nearestOffer(fresh.getMarket(), founding.x(), founding.y());
            System.out.printf("   the founding field: %d sites, %,.0f Mt, its centre %.0f plots out; the offer nearest it, %s, runs [%d, %d) x [%d, %d)%n",
                    founding.sites(), founding.amount() / 1e6, LegacyLand.radius(founding.x() - t0.siteX(), founding.y() - t0.siteY()),
                    first.where(), first.getX0(), first.getX1(), first.getY0(), first.getY1());
        }

        System.out.println("\n--- a new city's iron is a significant investment, and the funding page sizes to it ---");

        /*
         * Whole fields (0.7.64): the default world puts no iron field in a new
         * city's first ring, and its founding field - 35 sites, 449 Mt to
         * 0.7.98 - comes whole in the one offer holding its centre, at its
         * ground and its tonnes at the in-ground price: about US$180M against
         * a founding treasury of D$100M at 1.00. (At 0.7.58-0.7.63 the
         * cheapest offer with iron was a single shared site in the first
         * ring, 12.8 Mt for about US$5.3M, out of the founding treasury.)
         * Since 0.7.99 (batch W1) the nearest field is ten times the old
         * world's on the mean and lies where its cluster does, some twelve
         * kilometres out on the default world. Buying toward its centre, the
         * offer nearest it each time (its lane pushed out until 0.7.66),
         * lists it; the land office's funding page then sizes its bond to the
         * gap (Game.landCashGap()), and the bond's cash buys it.
         */
        Game founded = new Game(GameFiles.scratch("landcheck-whole-iron"));
        quietly(() -> { founded.newGame(); founded.toggleNextMonth(); });
        boolean noneFirst = true;
        for (LandParcel p : founded.getLandListing()) noneFirst &= !p.hasIron();
        assertTrue("a new default city is offered no iron in its first ring", noneFirst);
        LandManager fm = founded.getLandManager();
        CityLand fl = founded.getCityLand();
        LandParcel whole = null;
        if (founding != null) {
            for (int i = 0; i < 400 && whole == null; i++) {
                LandParcel next = MiningCheck.nearestOffer(fm.getMarket(), founding.x(), founding.y());
                if (next == null) break;
                if (next.contains(founding.x(), founding.y()) && !fl.ownsPlot(founding.x(), founding.y())) whole = next;
                else fm.buyParcel(next.getId(), 1e12, 0);
            }
        }
        assertTrue("buying toward its centre, the offer nearest it each time, lists the founding field", whole != null);
        if (whole != null) {
            double[] onIt = offerFields(fl, whole, Resource.IRON);
            assertTrue("...whole, in one offer: every one of its sites and tonnes",
                    whole.getDeposits() >= founding.sites() && whole.getIronTonnes() >= founding.amount());
            check("...with every other iron field centred on its free plots, recounted", whole.getDeposits(), onIt[0]);
            check("...and their tonnes, to the tonne", whole.getIronTonnes(), onIt[1]);
            java.util.List<Integer> ids = java.util.List.of(whole.getId());
            double rate = founded.getForeignAccounts().getRate();
            System.out.printf("   %s: %d site(s), %,.1f Mt, for US$%,.1fM; the treasury holds %s%,.1fM at %.2f to the dollar%n",
                    whole.where(), whole.getDeposits(), whole.getIronTonnes() / 1e6, whole.getPriceUsd() / 1000,
                    founded.getCurrency().qualifiedSymbol(), founded.getCash() / 1000, rate);
            assertTrue("...past a new city's founding treasury", !founded.canAffordParcel(whole) && founded.landNeedsFunding(ids));
            // The map's hover says whose a field is, whole, wherever the site under the pointer lies.
            int outside = 0;
            for (int k = 0; k < founding.sites(); k++) {
                double[] at = founding.siteAt(k);
                long px = Math.round(founding.x() + at[0]), py = Math.round(founding.y() + at[1]);
                LandMap.Pick under = LandMap.pick(fl, fm.getMarket(), px, py);
                if (under.owner() != LandMap.OFFER || under.offer().getId() != whole.getId()) outside++;
            }
            assertTrue("fixture: some of its sites lie outside the offer's rectangle", outside > 0);
            assertTrue("the map's hover gives the field to the offer holding its centre, whole: \""
                    + LandMap.fieldOwnerWords(fl, fm.getMarket(), founding) + "\"",
                    LandMap.fieldOwnerWords(fl, fm.getMarket(), founding).equals("all of it with " + whole.where() + ", on offer"));
            check("the funding page's gap is the offer's price here less the cash", founded.landCashGap(ids),
                    whole.localPrice(rate) - founded.getCash());
            DebtQuote bond = founded.quoteLongBondForCash(founded.landCashGap(ids), Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            assertTrue("...its bond's cash covers the gap", bond.cashReceived() >= founded.landCashGap(ids));
            int sitesBefore = fm.getIronDeposits();
            double tonnesBefore = fm.getIronReserveTonnes();
            quietly(() -> founded.handleLongBondForCash(founded.landCashGap(ids), Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
            assertTrue("...and once it is issued the cash covers the offer", !founded.landNeedsFunding(ids));
            boolean[] bought = new boolean[1];
            int wholeId = whole.getId();
            quietly(() -> bought[0] = founded.buyLandParcel(wholeId));
            assertTrue("...which the Buy then buys", bought[0]);
            check("the city gains the whole field's sites", fm.getIronDeposits(), sitesBefore + whole.getDeposits());
            check("...and its tonnes", fm.getIronReserveTonnes(), tonnesBefore + whole.getIronTonnes());
            assertTrue("...and the hover gives the field to the city, whole",
                    LandMap.fieldOwnerWords(fl, fm.getMarket(), founding).equals("the city's, whole"));
        }

        System.out.println("\n--- the ground is worked out in the order it was bought ---");

        LandManager order = new LandManager();
        order.updateMarket(0);
        order.restoreIron(2, 3_000_000);           // the centre: two sites, 3 Mt
        CityLand ordered = order.getCityLand();
        field = nearestUnowned(ordered, Resource.IRON);
        LandParcel first = order.getMarket().richest(Resource.IRON);
        for (int i = 0; i < 400 && first == null && field != null; i++) {
            order.buyParcel(MiningCheck.nearestOffer(order.getMarket(), field.x(), field.y()).getId(), 1e12, 0);
            first = order.getMarket().richest(Resource.IRON);
        }
        assertTrue("fixture: an offer with iron, after the centre's", first != null);
        if (first != null) {
            order.buyParcel(first.getId(), 1e12, 0);
            double[] held = ordered.amountsInOrder(Resource.IRON);
            int last = held.length - 1;
            order.extractIron(1_000_000);
            double[] left = order.remainingByHolding(Resource.IRON);
            check("a million tonnes out comes out of the centre first", left[0], 2_000_000);
            check("...leaving the purchase whole", left[last], held[last]);
            order.extractIron(2_500_000);
            left = order.remainingByHolding(Resource.IRON);
            check("past the centre's 3 Mt, the centre is worked out", left[0], 0);
            check("...and the rest comes out of the next bought", left[last], held[last] - 500_000);
            double all = 0;
            for (double d : left) all += d;
            check("...the holdings' remainders add to what remains", all, order.getIronReserveTonnes());
        }

        System.out.println("\n--- ground set by hand: the land drawn again, the iron kept ---");

        /*
         * Since 0.7.67 (spec-grid 2.6: restate() draws as a format-30 save is
         * drawn) the centre is blocks of the figure's level round the site,
         * the last split down to the plot, so its dry ground is the figure to
         * within a plot and never less (a part in a billion until 0.7.66, when
         * a centre's half-side was sized to hold it exactly); the figure is the
         * one set.
         */
        LandManager hand = new LandManager();
        hand.updateMarket(0);
        hand.restoreIron(5, 40_000_000);
        hand.extractIron(1_000_000);
        double want = LandManager.STARTING_SQ_FT + 400 * LandManager.BLOCK_SQ_FT;
        hand.setOwnedSqFt(want);
        CityLand drawn = hand.getCityLand();
        double plotSqFt = LandManager.sqFt(World.KM2_PER_PLOT);
        check("the city owns exactly the figure", hand.getOwnedSqFt(), want);
        assertTrue("...and its land's dry ground is that figure to within a plot, and never less",
                hand.getLandDrySqFt() >= want - 1e-6 && hand.getLandDrySqFt() < want + plotSqFt && LandConversion.sameGround(want, hand.getLandDrySqFt()));
        check("...all of it in the centre, nothing bought", drawn.purchases().size(), 0);
        check("its iron sites are kept", hand.getIronDeposits(), 5);
        check("...and what remains of its tonnes", hand.getIronReserveTonnes(), 39_000_000);
        check("...and what it had taken out", hand.getExtracted(Resource.IRON), 1_000_000);
        boolean edge = !hand.getListing().isEmpty();
        for (LandParcel p : hand.getListing()) edge &= touchesOwned(drawn.grid(), p);
        assertTrue("offers stand round the new centre, each against it", edge);

        System.out.println("\n--- forest grows back: a twentieth left in sixty years ---");

        // World 2, whose founding coast is wooded: 32.9 of the first 37.4 km2
        // of dry ground round its site (measured; the default world's site
        // stands among lakes, with none).
        LandManager wood = new LandManager(() -> ForeignAccounts.OPENING_RATE, () -> 1.0, () -> 2L, () -> 0);
        wood.setOwnedSqFt(LandManager.STARTING_SQ_FT + 4_000 * LandManager.BLOCK_SQ_FT);
        wood.updateMarket(0);
        double timber = wood.getRemaining(Resource.FOREST);
        assertTrue("fixture: the city's ground has forest on it", timber > 0);
        double cut = wood.extract(Resource.FOREST, timber / 2);
        wood.clearMonth();
        check("clearing a month's flows grows nothing back (a load does that)", wood.getExtracted(Resource.FOREST), cut);
        for (int m = 0; m < 240; m++) wood.endMonth();
        near("each month's end takes FOREST_REGROWTH off what was cut: after twenty years",
                wood.getExtracted(Resource.FOREST), cut * Math.pow(1 - LandManager.FOREST_REGROWTH, 240), 1e-12);
        for (int m = 240; m < 720; m++) wood.endMonth();
        assertTrue("...and after sixty, 95% has grown back", wood.getExtracted(Resource.FOREST) <= cut * .05);

        System.out.println("\n--- the best offer for each need ---");

        Game g = dollarCity("landcheck-best");
        LandMarket market = g.getLandManager().getMarket();
        LandParcel best = g.bestOffer(Game.LandNeed.room());
        boolean bestRoom = best != null && !best.isMostlySea() && g.canAffordParcel(best);
        for (LandParcel p : market.getListing()) {
            if (p.isMostlySea() || !g.canAffordParcel(p)) continue;
            bestRoom &= p.getDryKm2PerUsd() <= best.getDryKm2PerUsd();
        }
        assertTrue("room: the most dry ground a dollar the city can afford, never mostly sea", bestRoom);
        double need = 0;
        for (LandParcel p : market.getListing()) need = Math.max(need, p.getSizeSqFt() / 2);
        LandParcel covers = g.bestOffer(Game.LandNeed.shortfall(need));
        boolean cheapestCovering = covers != null;
        for (LandParcel p : market.getListing()) {
            if (p.getSizeSqFt() >= need) cheapestCovering &= covers.getSizeSqFt() >= need && p.getPriceUsd() >= covers.getPriceUsd();
        }
        assertTrue("a shortfall: the cheapest offer whose dry ground covers it", cheapestCovering);
        // A deposit (0.7.64, batch L2; the most sites a dollar until then): the cheapest offer holding it, what
        // the test player buys - on a city grown toward its founding field, the offer nearest it each time, until an offer holds iron.
        Game listed = ironListed("landcheck-best-deposit");
        LandMarket ironMarket = listed.getLandManager().getMarket();
        LandParcel deposit = listed.bestOffer(Game.LandNeed.deposit(Resource.IRON));
        boolean cheapestHolding = deposit != null && deposit.getSites(Resource.IRON) > 0 && deposit == ironMarket.cheapestWith(Resource.IRON);
        for (LandParcel p : ironMarket.getListing()) {
            if (p.getSites(Resource.IRON) > 0 && p.getAmount(Resource.IRON) > 0) cheapestHolding &= p.getPriceUsd() >= deposit.getPriceUsd();
        }
        assertTrue("a deposit: the cheapest offer holding it (LandMarket.cheapestWith()), what the test player buys", cheapestHolding);
        assertTrue("...for oil the same, or none when no offer holds any",
                g.bestOffer(Game.LandNeed.deposit(Resource.OIL)) == market.cheapestWith(Resource.OIL));
        assertTrue("a coast: the cheapest offer with sea in it",
                g.bestOffer(Game.LandNeed.coast()) == market.cheapestWithSea());
    }

    /* ==================================================================
       20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57)

       Two cities founded alike buy the same offer; one is saved and loaded.
       Its land comes back field for field - the centre and its blocks, the
       purchase, the offers, what was taken out, the world's totals - the
       other offers as they were listed, and its next month plays to the same
       people, cash and offers as the one that was not. (Since 0.7.67 the
       grid is replayed from the rectangles, node for node.)
       ================================================================== */
    static void savedAndLoaded() throws Exception {
        System.out.println("\n--- a city saved and loaded is the same land ---");

        GameFiles files = GameFiles.scratch("landcheck-world-save");
        Game live = new Game(files);
        quietly(() -> { live.newGame(); live.toggleNextMonth(); });
        LandParcel bought = live.getLandManager().getMarket().bestValue();
        java.util.List<LandParcel> others = live.getLandListing();
        assertTrue("fixture: the city buys an offer", live.buyLandParcel(bought.getId()));
        quietly(() -> live.saveGame(4, "land on the world"));
        Game[] back = new Game[1];
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(4); });
        Game loaded = back[0];
        assertTrue("fixture: it loads", loaded.getLoadFailure() == null);
        CityLand a = live.getCityLand(), b = loaded.getCityLand();
        assertTrue("the centre, its blocks and the purchase come back field for field, the grid node for node", a.same(b));
        boolean offers = live.getLandListing().size() == loaded.getLandListing().size();
        for (int i = 0; offers && i < live.getLandListing().size(); i++) {
            offers = live.getLandListing().get(i).same(loaded.getLandListing().get(i));
        }
        assertTrue("...the offers", offers);
        boolean stood = true;
        for (LandParcel p : others) {
            if (p.getId() == bought.getId()) continue;
            stood &= p.same(loaded.getLandManager().getMarket().find(p.getId()));
        }
        assertTrue("...the others exactly as listed before the purchase", stood);
        check("...the next offer's id", loaded.getLandManager().getMarket().getNextOfferId(),
                live.getLandManager().getMarket().getNextOfferId());
        assertTrue("...what was taken out of the ground",
                java.util.Arrays.equals(live.getLandManager().getDepletionState(), loaded.getLandManager().getDepletionState()));
        assertTrue("...the world's totals and its sea's level",
                java.util.Arrays.equals(live.getLandManager().getWorldTotalsState(), loaded.getLandManager().getWorldTotalsState())
                        && live.getLandManager().getWorldSeaTheta() == loaded.getLandManager().getWorldSeaTheta());
        check("...the square feet owned", loaded.getLandManager().getOwnedSqFt(), live.getLandManager().getOwnedSqFt());
        check("...and the office's block level", loaded.getLandManager().getMarket().getLevel(),
                live.getLandManager().getMarket().getLevel());

        quietly(live::toggleNextMonth);
        quietly(loaded::toggleNextMonth);
        check("its next month plays to the same population", loaded.getPopulationManager().getPopulation(),
                live.getPopulationManager().getPopulation());
        check("...the same cash", loaded.getCash(), live.getCash());
        assertTrue("...and the same land and offers",
                live.getCityLand().same(loaded.getCityLand())
                        && java.util.Arrays.deepEquals(live.getLandManager().getMarket().getOffersState(),
                                loaded.getLandManager().getMarket().getOffersState()));
    }

    /* ==================================================================
       21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57)

       spec-land 2.9: a save of format 30 carries one figure of ground, a
       pool of iron and nine parcels. The three research cities' land, as
       their saves carry it - city600 at month 612, city2400 at 2412, Jerus's
       at 1851: each one's square feet, iron sites and tonnes, its mines and
       the world seed its 0.7.56 save derives (fixJ1a's notes) - written into
       a format-30 save by hand, and loaded: since 0.7.67 a centre of blocks
       round J1b's site holding its dry ground to within a plot and never
       less, the city's figure its dry plots (a part in a billion, and the
       save's figure, until 0.7.66); at least as many iron sites as mines, its
       tonnes exact, twenty-four places listed. And a city with more mines
       than sites, which are raised to them.
       ================================================================== */

    /** One research city's land as its save carries it: its name, its world's seed, its square feet, iron sites and tonnes, and its Iron Mines. */
    record Research(String name, long seed, double landOwned, int ironSites, double ironTonnes, int mines) { }

    /** The three research saves' land (the autosaves at months 612, 2412 and 1851), and a city whose mines outnumber its sites. */
    static final Research[] RESEARCH = {
        new Research("city600 m612", 906013741141069877L, 32_635_000, 10, 36200450.60153519, 1),
        new Research("city2400 m2412", -8480926900264452605L, 358_285_000, 64, 218643630.67146584, 0),
        new Research("Jerus m1851", -2365104814562977942L, 964_751_000, 155, 508088779.4794291, 21),
        new Research("more mines than sites", Founding.DEFAULT_WORLD_SEED, 30_000_000, 1, 2_000_000, 3),
    };

    static void converted() throws Exception {
        System.out.println("\n--- an older save's land is put on the world, once ---");

        for (Research r : RESEARCH) {
            GameFiles files = GameFiles.scratch("landcheck-convert-" + r.seed());
            Game city = new Game(files);
            quietly(() -> {
                city.newGame();
                BuildingsTemplate mine = city.getBuildingManager().getTemplateByName("Iron Mine");
                if (r.mines() > 0) city.getBuildingManager().addStack(mine, r.mines(), true);
                city.saveGame(4, "older land");
            });
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                    java.nio.file.Files.readString(files.saveFile(4))).getAsJsonObject();
            for (String key : new String[] { "worldSeaTheta", "worldTotals", "landCentre", "landCentreRects", "landHoldings",
                    "landPartFields", "landConverted", "landLanes", "landPurchases", "landOffers", "nextOfferId", "depletion" }) json.remove(key);
            json.addProperty("saveFormat", LandConversion.LAST_FORMAT_BEFORE);
            json.addProperty("worldSeed", r.seed());
            json.addProperty("landOwned", r.landOwned());
            json.addProperty("ironDeposits", r.ironSites());
            json.addProperty("ironReserveTonnes", r.ironTonnes());
            com.google.gson.JsonArray parcels = new com.google.gson.JsonArray();
            parcels.add(-105.0);
            parcels.add(10.0);
            for (int i = 0; i < 9; i++) for (double v : new double[] { i + 1, 250_000, 175, 0, 0 }) parcels.add(v);
            json.add("landListing", parcels);
            java.nio.file.Files.writeString(files.saveFile(4), json.toString());

            Game[] back = new Game[1];
            long t0 = System.nanoTime();
            quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(4); });
            double ms = (System.nanoTime() - t0) / 1e6;
            Game old = back[0];
            assertTrue("fixture (" + r.name() + "): the format-30 save loads", old.getLoadFailure() == null);
            LandManager land = old.getLandManager();
            CityLand put = old.getCityLand();
            double share = put.centreKm2(CityLand.DRY) / put.centreKm2(CityLand.TOTAL);
            System.out.printf("   %s: %.2f km2 dry in a centre of %.2f km2 (%.0f%% dry) at (%d, %d), %d sites on %d mines,"
                            + " %,.0f t; loaded in %,.0f ms%n", r.name(), put.centreKm2(CityLand.DRY), put.centreKm2(CityLand.TOTAL),
                    share * 100, put.siteX(), put.siteY(), land.getIronDeposits(), old.minesCommitted(),
                    land.getIronReserveTonnes(), ms);
            check("its world is its save's seed", old.getWorldSeed(), r.seed());
            double plotSqFt = LandManager.sqFt(World.KM2_PER_PLOT);
            assertTrue("...its dry ground the save's square feet to within a plot, and never less",
                    land.getLandDrySqFt() >= r.landOwned() - 1e-6 && land.getLandDrySqFt() < r.landOwned() + plotSqFt);
            check("...owned: its dry plots, the books following the map", land.getOwnedSqFt(), land.getLandDrySqFt());
            assertTrue("...at least as many iron sites as mines standing and on order",
                    land.getIronDeposits() >= old.minesCommitted());
            check("...its sites the save's, or its mines where they are more", land.getIronDeposits(),
                    Math.max(r.ironSites(), r.mines()));
            check("...its tonnes the save's, exactly", land.getIronReserveTonnes(), r.ironTonnes());
            check("...nothing taken out yet", land.getExtracted(Resource.IRON), 0);
            int waiting = 0;
            for (int side = 0; side < CityLand.SIDES; side++) waiting += land.getMarket().emptyOn(side);
            check("...the nine parcels gone and every place listed or waiting for room in their place",
                    land.getListing().size() + waiting, LandMarket.OFFERS);
            assertTrue("...all of them against its ground, nothing bought", put.purchases().isEmpty() && !land.getListing().isEmpty()
                    && land.getListing().stream().allMatch(p -> touchesOwned(put.grid(), p)));
            double lx = put.legacyX() - put.siteX(), ly = put.legacyY() - put.siteY();
            boolean legacy = put.legacySites() == 0
                    || Math.sqrt(lx * lx + ly * ly) >= LandConversion.LEGACY_FIELD_KM * 1000 / World.PLOT_M - 1;
            assertTrue("...and a legacy iron field, if the map needs one, a kilometre or more from the site", legacy);
            if (share >= LandConversion.CENTRE_DRY_MIN) {
                assertTrue("...its centre at least 80% dry (the fifth test)", share >= LandConversion.CENTRE_DRY_MIN);
            } else {
                System.out.printf("   (no site in %d cells passed the fifth test: the driest found, %.0f%%)%n",
                        LandConversion.SITE_CELLS, share * 100);
            }
            quietly(() -> old.saveGame(5, "converted"));
            Game[] again = new Game[1];
            quietly(() -> { again[0] = new Game(files); again[0].loadGameSave(5); });
            assertTrue("...converted once: saved again, it loads as it is", again[0].getCityLand().same(put)
                    && java.util.Arrays.deepEquals(again[0].getLandManager().getMarket().getOffersState(),
                            land.getMarket().getOffersState()));
        }
    }

    /** The iron field nearest a city's site, in the nine cells round it, whose centre the city does not own; null when none. */
    static Deposit nearestUnowned(CityLand land, Resource kind) {
        World world = World.of(land.seed());
        long cx = land.siteX() / World.CELL, cy = land.siteY() / World.CELL;
        Deposit nearest = null;
        double best = Double.MAX_VALUE;
        for (long y = cy - 1; y <= cy + 1; y++) {
            for (long x = cx - 1; x <= cx + 1; x++) {
                if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                for (Deposit d : world.fieldsInCell((int) (y * World.CELLS + x), kind)) {
                    double r = LegacyLand.radius(d.x() - land.siteX(), d.y() - land.siteY());
                    if (land.ownsPlot(d.x(), d.y()) || r >= best) continue;
                    best = r;
                    nearest = d;
                }
            }
        }
        return nearest;
    }

    /** The field of a resource nearest a city's site, in the nine cells round it, whose centre plot is dry ground and not the city's (0.7.99): where foundedOn() puts a fixture's city. */
    static Deposit nearestOnDryGround(CityLand land, Resource kind) {
        World world = World.of(land.seed());
        long cx = land.siteX() / World.CELL, cy = land.siteY() / World.CELL;
        Deposit nearest = null;
        double best = Double.MAX_VALUE;
        for (long y = cy - 1; y <= cy + 1; y++) {
            for (long x = cx - 1; x <= cx + 1; x++) {
                if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                for (Deposit d : world.fieldsInCell((int) (y * World.CELLS + x), kind)) {
                    byte c = world.terrainAt(d.x(), d.y());
                    double r = LegacyLand.radius(d.x() - land.siteX(), d.y() - land.siteY());
                    if (c == World.SALT || c == World.FRESH || land.ownsPlot(d.x(), d.y()) || r >= best) continue;
                    best = r;
                    nearest = d;
                }
            }
        }
        return nearest;
    }

    /**
     * A land office whose city is founded on the default world at plot (x, y)
     * rather than its founding site (0.7.99, batch W1): a centre of whole
     * blocks round it holding a new city's dry ground (CityLand.found()),
     * nothing taken out, its figure its dry plots - as LandManager.land()
     * founds one. What a fixture needs a deposit near the city for: since the
     * deposits are fewer and bigger, the default site has none within ten
     * kilometres.
     */
    static LandManager foundedOn(long x, long y) {
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        LandManager lm = new LandManager();
        lm.install(CityLand.found(w, x, y, LandManager.km2(lm.getOwnedSqFt())), new double[CityLand.KINDS], w.totals(), w.seaTheta());
        lm.restoreOwnedSqFt(lm.getLandDrySqFt());
        return lm;
    }

    /** Read by nothing since 0.7.67: the width of the bands section 19 tiled the plane with until then, 7 plots, which no offer was - the pieces are the holdings and the offers standing now. */
    static final double DEAL = 7;

    /* ==================================================================
       22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d)

       LongPlaytest.keepGroundAhead(), at each of the playtest's looks: the
       best value for room until no more of the dry ground is built on than
       the ground in use grown BuildAdvice.HORIZON months by the businesses'
       growthFactor(), with BuildAdvice.SLACK past it, spending at most
       GROUND_AHEAD_CASH_SHARE of the cash. The fixtures build a new city's
       ground to 99% and give it its founding treasury, then a tenth too
       little for the best offer, and leave a third city as it was founded.
       Since 0.7.67 (M3b) the room-to-grow move is priced from the same line
       (LongPlaytest.roomToGrow()): the 99% city past it, kept to it, and a
       fourth city built half-way between .85 and the line.
       ================================================================== */
    static void groundKeptAhead() {
        System.out.println("\n--- the test player keeps its ground ahead (0.7.58) ---");

        Game g = dollarCity("landcheck-ahead");
        LandManager land = g.getLandManager();
        double line = LongPlaytest.groundAheadUtilisation(g);
        check("the line is the ground in use HORIZON months out, SLACK past it", line,
                1 / (g.getBusinessInvestment().growthFactor(BuildAdvice.HORIZON) * (1 + BuildAdvice.SLACK)));
        assertTrue("...never more built on than 1 / (1 + SLACK)", line <= 1 / (1 + BuildAdvice.SLACK) + 1e-12);
        land.allocate(land.getAvailableSqFt() - .01 * land.getOwnedSqFt());
        assertTrue("fixture: 99% of the ground is built on, past the line",
                land.getAllocatedSqFt() / land.getOwnedSqFt() > line);
        // The room-to-grow move, priced from the same line (0.7.67, M3b).
        double used = land.getAllocatedSqFt() / land.getOwnedSqFt();
        near("past the line, room to grow is weighed at the output from the line to a city built full (0.7.67)",
                LongPlaytest.roomToGrow(g), Math.max(1, g.getEconomyManager().getMonthGdp()) * (used - line) / (1 - line), 1e-12);
        double cash = g.getCash(), owned = land.getOwnedSqFt();
        int bought = LongPlaytest.groundAheadBought, purchases = g.getCityLand().purchases().size();
        quietly(() -> LongPlaytest.keepGroundAhead(g));
        int made = g.getCityLand().purchases().size() - purchases;
        System.out.printf("   %d offer(s), %.4f km2 dry, D$%,.0fk of D$%,.0fk; built on %.4f against the line %.4f%n",
                made, LandManager.km2(land.getOwnedSqFt() - owned), cash - g.getCash(), cash,
                land.getAllocatedSqFt() / land.getOwnedSqFt(), line);
        assertTrue("it bought ground, each purchase counted", made > 0 && LongPlaytest.groundAheadBought - bought == made);
        assertTrue("...until no more is built on than the line",
                land.getAllocatedSqFt() / land.getOwnedSqFt() <= line);
        assertTrue("...for no more than the cash share",
                cash - g.getCash() <= LongPlaytest.GROUND_AHEAD_CASH_SHARE * cash * (1 + 1e-12));
        assertTrue("...and kept to the line, room to grow is weighed at nothing (0.7.67)", LongPlaytest.roomToGrow(g) == 0);

        /*
         * BETWEEN .85 AND THE LINE (0.7.67, M3b): the old price, gdp x (u - .85)
         * / .15, weighed a city kept at the line as 45 to 67% of its output
         * lost for want of ground, and outranked its power and roads (ensemble
         * seed 14, months 477-490). The fixture builds a new city's ground
         * to half-way between the two.
         */
        Game between = dollarCity("landcheck-room-between");
        LandManager bl = between.getLandManager();
        double betweenLine = LongPlaytest.groundAheadUtilisation(between);
        bl.allocate((.85 + betweenLine) / 2 * bl.getOwnedSqFt() - bl.getAllocatedSqFt());
        double betweenUsed = bl.getAllocatedSqFt() / bl.getOwnedSqFt();
        assertTrue("fixture: built on past .85, within the ground-ahead line", betweenUsed > .85 && betweenUsed <= betweenLine);
        assertTrue("...where room to grow is weighed at nothing: the projection's ground holds",
                LongPlaytest.roomToGrow(between) == 0);

        Game poor = dollarCity("landcheck-ahead-poor");
        LandManager pl = poor.getLandManager();
        pl.allocate(pl.getAvailableSqFt() - .01 * pl.getOwnedSqFt());
        LandParcel best = poor.bestOffer(Game.LandNeed.room());
        double price = best.localPrice(poor.getForeignAccounts().getRate());
        poor.setCashForTest(price / LongPlaytest.GROUND_AHEAD_CASH_SHARE * .9);
        double poorCash = poor.getCash(), poorOwned = pl.getOwnedSqFt();
        int poorShort = LongPlaytest.groundAheadShort;
        assertTrue("fixture: the best value is past the cash share, inside the cash",
                price > LongPlaytest.GROUND_AHEAD_CASH_SHARE * poorCash && poor.canAffordParcel(best));
        quietly(() -> LongPlaytest.keepGroundAhead(poor));
        assertTrue("a look the cash share cannot cover buys nothing", pl.getOwnedSqFt() == poorOwned && poor.getCash() == poorCash);
        assertTrue("...and is counted stopped short", LongPlaytest.groundAheadShort == poorShort + 1);

        Game founded = dollarCity("landcheck-ahead-founded");
        LandManager fl = founded.getLandManager();
        double foundedOwned = fl.getOwnedSqFt();
        assertTrue("fixture: a city as founded is under the line",
                fl.getAllocatedSqFt() / fl.getOwnedSqFt() <= LongPlaytest.groundAheadUtilisation(founded));
        quietly(() -> LongPlaytest.keepGroundAhead(founded));
        assertTrue("...and buys nothing", fl.getOwnedSqFt() == foundedOwned);
    }

    /* ==================================================================
       23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L)

       LongPlaytest.ironWhenNeeded(), at each of the playtest's looks: when an
       Iron Mine would pay (wouldPay()) and every iron site the city owns has
       a mine on it or ordered, the cheapest offer holding iron
       (LandMarket.cheapestWith()) - out of the cash when it covers it, else
       on the land office's funding page's bond (BUILD_BOND_YEARS, sized to
       Game.landCashGap()) when the player's own test for borrowing passes
       (canService()); neither, nothing, and the look counted. Jerus: "Yes
       whole iron fields as one offer, yes that means significant
       investment." The fixtures grow a new city toward the default world's
       founding field, the offer nearest it each time (its lane pushed out
       until 0.7.66), until it is listed (35 sites, 449 Mt, about
       US$180M at 1.6 to the dollar), then hand the treasury the price and
       more, the price less a few thousand, and nothing; a fourth city is
       left as founded, with no iron listed.

       Since 0.7.67 (M3b) the rule buys only a field that pays itself back:
       the mines it would carry (LongPlaytest.fieldEarnings(): staffable,
       each paying on the mining sector's screen) earn a month at least the
       level payment repaying its price over BUILD_BOND_YEARS
       (fieldPayment()). A new city of a month can staff none of them, so on
       these fixtures the founding field does not pay back and is not bought,
       whatever the cash; the land office's Buy and the funding page's bond,
       which the rule pays with once a field does, are held through
       LongPlaytest.buyWhole() on the same fixtures.
       ================================================================== */
    static void ironAWholeFieldAtATime() {
        System.out.println("\n--- the test player buys its iron a whole field at a time (0.7.64) ---");

        Game rich = ironListed("landcheck-iron-cash");
        LandManager rl = rich.getLandManager();
        LandParcel cheapest = rl.getMarket().cheapestWith(Resource.IRON);
        boolean isCheapest = cheapest != null && cheapest.hasIron();
        for (LandParcel p : rl.getListing()) {
            if (p.hasIron()) isCheapest &= p.getPriceUsd() >= cheapest.getPriceUsd();
        }
        assertTrue("the cheapest whole field: the offer holding iron at the least price, whatever its size", isCheapest);
        assertTrue("fixture: an Iron Mine would pay, and the city owns no unworked iron site",
                LongPlaytest.wouldPay(rich, "Iron Mine", Sectors.MINING) && rl.getIronDeposits() <= rich.minesCommitted());
        double rate = rich.getForeignAccounts().getRate();
        rich.setCashForTest(cheapest.localPrice(rate) * 1.25);
        double cash = rich.getCash(), owed = rich.getDebtManager().getAllPrincipal();
        int sites = rl.getIronDeposits(), bought = LongPlaytest.ironFieldsBought;

        /* --- the payback (0.7.67, M3b) --- */
        BuildingsTemplate mine = rich.getBuildingManager().getTemplateByName("Iron Mine");
        double[] earns = LongPlaytest.fieldEarnings(rich, cheapest.getDeposits());
        double payment = LongPlaytest.fieldPayment(rich, cheapest);
        int months = Game.BUILD_BOND_YEARS * 12;
        double monthly = rich.getDebtManager().quoteRate(cheapest.localPrice(rate), months) / 12, repaid = 0;
        for (int k = 1; k <= months; k++) repaid += payment / Math.pow(1 + monthly, k);
        near("a field's payment repays its price here over BUILD_BOND_YEARS at the market's rate", repaid,
                cheapest.localPrice(rate), 1e-9);
        int staffable = rich.getSectors().byKey(Sectors.MINING).staffableCount(mine, cheapest.getDeposits());
        assertTrue("its mines no more than its sites and than the mining sector could staff",
                earns[0] <= Math.min(cheapest.getDeposits(), staffable));
        assertTrue("fixture: a new city of a month staffs none of the field's mines, so it does not pay back",
                staffable == 0 && earns[0] == 0 && earns[1] < payment);
        int noPay = LongPlaytest.ironDoesNotPay;
        quietly(() -> LongPlaytest.ironWhenNeeded(rich));
        System.out.printf("   %s: %d site(s), %,.1f Mt, US$%,.1fM, D$%,.1fM here, D$%,.1fM of cash; %.0f mine(s) earning D$%,.0fk a"
                        + " month against a payment of D$%,.0fk%n", cheapest.where(), cheapest.getDeposits(),
                cheapest.getIronTonnes() / 1e6, cheapest.getPriceUsd() / 1000, cheapest.localPrice(rate) / 1000, cash / 1000,
                earns[0], earns[1], payment);
        assertTrue("with the cash to cover it, a field that does not pay back is not bought, nor anything borrowed",
                rl.getMarket().find(cheapest.getId()) != null && rl.getIronDeposits() == sites && rich.getCash() == cash
                        && rich.getDebtManager().getAllPrincipal() == owed && LongPlaytest.ironFieldsBought == bought);
        assertTrue("...and the look is counted, to ask again at the next", LongPlaytest.ironDoesNotPay == noPay + 1);

        /* --- the payment, once a field pays back: the land office's Buy and the funding page (0.7.64) --- */
        String[] paid = new String[1];
        quietly(() -> paid[0] = LongPlaytest.buyWhole(rich, cheapest, "an iron field"));
        assertTrue("with the cash to cover it, it buys that offer, whole, out of the cash",
                "cash".equals(paid[0]) && rl.getMarket().find(cheapest.getId()) == null
                        && rl.getIronDeposits() == sites + cheapest.getDeposits());
        near("...the cash down by its price here", cash - rich.getCash(), cheapest.localPrice(rate), 1e-9);
        assertTrue("...and nothing borrowed", rich.getDebtManager().getAllPrincipal() == owed);
        int again = rl.getIronDeposits();
        quietly(() -> LongPlaytest.ironWhenNeeded(rich));
        assertTrue("with a site unworked it buys no more", rl.getIronDeposits() == again && LongPlaytest.ironFieldsBought == bought);

        Game shortBy = ironListed("landcheck-iron-bond");
        LandManager sl = shortBy.getLandManager();
        LandParcel field = sl.getMarket().cheapestWith(Resource.IRON);
        double here = field.localPrice(shortBy.getForeignAccounts().getRate());
        shortBy.setCashForTest(here - 5);
        java.util.List<Integer> ids = java.util.List.of(field.getId());
        assertTrue("fixture: a few thousand short, which the player's test for borrowing carries",
                shortBy.landNeedsFunding(ids) && LongPlaytest.canService(shortBy, shortBy.landCashGap(ids)));
        double owedBefore = shortBy.getDebtManager().getAllPrincipal();
        String[] onBond = new String[1];
        quietly(() -> onBond[0] = LongPlaytest.buyWhole(shortBy, field, "an iron field"));
        assertTrue("short, it borrows the funding page's bond for the gap and buys the offer, whole",
                "bond".equals(onBond[0]) && sl.getMarket().find(field.getId()) == null && sl.getIronDeposits() == field.getDeposits()
                        && shortBy.getDebtManager().getAllPrincipal() > owedBefore);
        assertTrue("...the bond's cash covering it: the treasury not overdrawn", shortBy.getCash() >= 0);

        Game poor = ironListed("landcheck-iron-wait");
        LandManager pl = poor.getLandManager();
        LandParcel dear = pl.getMarket().cheapestWith(Resource.IRON);
        java.util.List<Integer> dearIds = java.util.List.of(dear.getId());
        assertTrue("fixture: a founding treasury, far short, which the player's test for borrowing refuses",
                poor.landNeedsFunding(dearIds) && !LongPlaytest.canService(poor, poor.landCashGap(dearIds)));
        double poorCash = poor.getCash(), poorOwed = poor.getDebtManager().getAllPrincipal();
        String[] neither = { "" };
        quietly(() -> neither[0] = LongPlaytest.buyWhole(poor, dear, "an iron field"));
        assertTrue("neither, it buys nothing and borrows nothing",
                neither[0] == null && pl.getMarket().find(dear.getId()) != null && pl.getIronDeposits() == 0
                        && poor.getCash() == poorCash && poor.getDebtManager().getAllPrincipal() == poorOwed);

        Game founded = dollarCity("landcheck-iron-none");
        int none = LongPlaytest.ironNoneListed;
        boolean listed = founded.getLandManager().getMarket().cheapestWith(Resource.IRON) != null;
        quietly(() -> LongPlaytest.ironWhenNeeded(founded));
        assertTrue("with no iron listed - a new city's first ring - it buys nothing, and counts the look",
                !listed && founded.getLandManager().getIronDeposits() == 0 && LongPlaytest.ironNoneListed == none + 1);
    }

    /** A new city at 1.6 to the dollar grown, by hand, toward the founding field - the offer nearest it each time - until an offer holds iron. */
    static Game ironListed(String label) {
        Game g = dollarCity(label);
        CityLand land = g.getCityLand();
        LandManager lm = g.getLandManager();
        Deposit field = nearestUnowned(land, Resource.IRON);
        for (int i = 0; i < 400 && lm.getMarket().cheapestWith(Resource.IRON) == null; i++) {
            lm.buyParcel(MiningCheck.nearestOffer(lm.getMarket(), field.x(), field.y()).getId(), 1e12, 0);
        }
        return g;
    }

    /** Every field of a resource centred within `out` plots of a city's site (L-infinity) whose centre, seen from the site, passes `in`: its sites and its amount, whole - read off the world's cells, with none of CityLand's short cuts. Called by nothing since 0.7.67: recountOn() is the grid's. */
    static double[] recount(CityLand land, Resource kind, double out, java.util.function.BiPredicate<Double, Double> in) {
        World world = World.of(land.seed());
        double sites = 0, amount = 0;
        for (int cell : CityLand.cellsUnder(land.siteX() - out, land.siteY() - out, land.siteX() + out, land.siteY() + out)) {
            for (Deposit d : world.fieldsInCell(cell, kind)) {
                if (in.test((double) (d.x() - land.siteX()), (double) (d.y() - land.siteY()))) {
                    sites += d.sites();
                    amount += d.amount();
                }
            }
        }
        return new double[] { sites, amount };
    }

}
