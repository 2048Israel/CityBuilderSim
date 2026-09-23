package ham.citybuildersim;

/**
 * Verifies the land ledger: what the city owns, what it can allocate, what it
 * charges, and that the three numbers never drift apart.
 *
 * The one thing worth being paranoid about here is that allocated land can only
 * ever go up by exactly what was built. A leak in either direction is invisible
 * for a hundred months and then the city is either mysteriously full or
 * mysteriously infinite.
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

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    public static void main(String[] args) throws Exception {

        /* ==================== 1. what the city starts with ==================== */
        System.out.println("--- opening position ---");

        LandManager lm = new LandManager();

        check("owned", lm.getOwnedSqFt(), LandManager.STARTING_SQ_FT);
        check("allocated", lm.getAllocatedSqFt(), 0);
        check("available", lm.getAvailableSqFt(), LandManager.STARTING_SQ_FT);
        check("thirty blocks", lm.getAvailableBlocks(), 30);
        check("nothing built on -> 0% used", lm.getUtilisation(), 0);
        check("no blocks bought yet", lm.getBlocksPurchased(), 0);

        /* ==================== 2. allocating ==================== */
        System.out.println("\n--- building on it ---");

        assertTrue("room for a 600,000 sq ft plant", lm.canAllocate(600000));
        assertTrue("allocation succeeds", lm.allocate(600000));
        check("allocated", lm.getAllocatedSqFt(), 600000);
        check("available", lm.getAvailableSqFt(), 2400000);
        check("20% used", lm.getUtilisation(), .20);

        // Exactly the remainder is allowed; a square foot more is not.
        assertTrue("exactly what is left fits", lm.canAllocate(2400000));
        assertTrue("one sq ft more does not", !lm.canAllocate(2400001));

        assertTrue("an oversized allocation is refused", !lm.allocate(2400001));
        check("...and took nothing when it refused", lm.getAllocatedSqFt(), 600000);

        // Refusal leaving the ledger untouched is the whole point: the caller
        // reads the boolean and must not build, and if the land had been taken
        // anyway the city would lose a plot to a building that never went up.
        check("available unchanged after a refusal", lm.getAvailableSqFt(), 2400000);

        /* ==================== 3. filling up ==================== */
        System.out.println("\n--- full ---");

        assertTrue("the last of it fits", lm.allocate(2400000));
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
        check("...and cannot invent land", lm.getOwnedSqFt(), LandManager.STARTING_SQ_FT);

        /* ==================== 5. the listing ==================== */
        System.out.println("\n--- nine plots on offer ---");

        LandManager buy = new LandManager();
        buy.updateMarket(0);

        java.util.List<LandParcel> listing = buy.getListing();
        check("nine plots are listed", listing.size(), LandMarket.LISTING_SIZE);
        check("the ground still costs $0.70/sq ft", buy.getAcquisitionCostPerSqFt(), .0007);
        check("...which is US$0.70 in the money it is asked in", buy.getGroundUsdPerSqFt(), .0007);

        // The point of a listing rather than a price: they have to differ, or
        // there is no decision in it.
        double smallest = Double.MAX_VALUE, largest = 0;
        int withIron = 0;
        for (LandParcel parcel : listing) {
            smallest = Math.min(smallest, parcel.getSizeSqFt());
            largest = Math.max(largest, parcel.getSizeSqFt());
            if (parcel.hasIron()) withIron++;
            assertTrue("every plot has a size and a price",
                    parcel.getSizeSqFt() > 0 && parcel.getPriceUsd() > 0);
        }
        assertTrue("the plots are different sizes", largest > smallest * 2);
        System.out.printf("   sizes from %,.0f to %,.0f sq ft, %d with iron%n",
                smallest, largest, withIron);

        // Deterministic in the id, which is what lets a save restore the window
        // without storing it and stops a reload being a reroll.
        LandManager twin = new LandManager();
        twin.updateMarket(0);
        boolean identical = true;
        for (int i = 0; i < listing.size(); i++) {
            LandParcel a = listing.get(i);
            LandParcel b = twin.getListing().get(i);
            if (a.getId() != b.getId() || a.getSizeSqFt() != b.getSizeSqFt()
                    || a.getIronTonnes() != b.getIronTonnes()) {
                identical = false;
            }
        }
        assertTrue("the same city always sees the same plots", identical);

        /* ==================== 5b. buying one ==================== */
        System.out.println("\n--- buying a plot ---");

        LandParcel wanted = buy.getListing().get(3);
        double before = buy.getOwnedSqFt();

        // A bare land office converts at the founding rate, 1.00 (0.7.6).
        double paid = buy.buyParcel(wanted.getId(), 1e9, 0);
        check("paid exactly what was listed", paid,
                wanted.localPrice(ForeignAccounts.OPENING_RATE));
        check("owned grew by the plot's size",
                buy.getOwnedSqFt(), before + wanted.getSizeSqFt());
        check("recorded as a purchase this month", buy.getLandPurchasesThisMonth(), paid);
        check("the window refilled", buy.getListing().size(), LandMarket.LISTING_SIZE);
        assertTrue("...and that plot is gone from it",
                buy.getMarket().find(wanted.getId()) == null);

        // What is listed stays listed: everything the player was weighing up is
        // still there at the same price after somebody buys something else.
        boolean pricesHeld = true;
        for (LandParcel parcel : listing) {
            if (parcel.getId() == wanted.getId()) continue;
            LandParcel still = buy.getMarket().find(parcel.getId());
            if (still == null || still.getPriceUsd() != parcel.getPriceUsd()) pricesHeld = false;
        }
        assertTrue("the other offers did not move", pricesHeld);

        assertTrue("buying an unlisted plot does nothing",
                buy.buyParcel(999999, 1e9, 0) == 0);

        /* ==================== 5c. a bigger city pays more ==================== */
        System.out.println("\n--- and a bigger city pays more ---");

        double smallCityRate = new LandManager() {{ updateMarket(0); }}
                .getAcquisitionCostPerSqFt();

        LandManager bigCity = new LandManager();
        bigCity.setOwnedSqFt(LandManager.STARTING_SQ_FT + 40 * LandManager.BLOCK_SQ_FT);
        bigCity.updateMarket(8000);

        assertTrue("more land and more people means dearer land",
                bigCity.getAcquisitionCostPerSqFt() > smallCityRate);
        System.out.printf("   $%.4f/sq ft for a new city, $%.4f for a big one%n",
                smallCityRate, bigCity.getAcquisitionCostPerSqFt());

        // "Slightly", per the design. A city forty blocks and eight thousand
        // people on should not be paying five times the going rate.
        assertTrue("...but only slightly",
                bigCity.getAcquisitionCostPerSqFt() < smallCityRate * 4);

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
        System.out.println("\n--- ore ---");

        LandManager ore = new LandManager();
        ore.updateMarket(0);

        LandParcel deposit = ore.getMarket().richestDeposit();
        assertTrue("some plot on offer has iron under it", deposit != null);

        if (deposit != null) {
            // A deposit costs more than bare ground of the same size, which is
            // the whole reason it is a decision rather than free money.
            double bareGround = deposit.getSizeSqFt() * ore.getGroundUsdPerSqFt();
            assertTrue("a deposit costs more than the ground it sits on",
                    deposit.getPriceUsd() > bareGround);
            System.out.printf("   %,.0fk tonnes: US$%,.0f against US$%,.0f for bare ground%n",
                    deposit.getIronTonnes() / 1000, deposit.getPriceUsd(), bareGround);

            check("no deposits to start with", ore.getIronDeposits(), 0);
            ore.buyParcel(deposit.getId(), 1e9, 0);

            // Against the PARCEL'S site count, not a hardcoded 1. A tract can
            // carry several now, and an assertion that says otherwise would
            // start failing the day the roll happens to hand this one two.
            int sites = deposit.getDeposits();
            check("buying it gives the city its sites", ore.getIronDeposits(), sites);
            check("...and its tonnage", ore.getIronReserveTonnes(), deposit.getIronTonnes());

            assertTrue("the sites support that many mines", ore.hasUnminedDeposit(sites - 1));
            assertTrue("...and not one more", !ore.hasUnminedDeposit(sites));

            double lifted = ore.extractIron(50000);
            check("mining takes ore out of the ground", lifted, 50000);
            check("...and the reserve falls",
                    ore.getIronReserveTonnes(), deposit.getIronTonnes() - 50000);

            // Finite means finite. A mine on an empty deposit lifts nothing and
            // still costs its payroll, which is the point of depletion.
            ore.extractIron(1e12);
            check("a deposit can be worked out", ore.getIronReserveTonnes(), 0);
            check("...and then yields nothing", ore.extractIron(1000), 0);
            assertTrue("...and supports no more mines", !ore.hasUnminedDeposit(0));
        }

        /* ============ 5f. parcels are blocks, and they grow ============ */
        /*
         * Jerus, after the hand-played run: buying ~200 parcels one click at a
         * time to reach 581 blocks was the single biggest time sink in playing
         * the game. Two rules fix that, and this section is what holds them.
         */
        System.out.println("\n--- no more slivers ---");

        LandManager young = new LandManager();
        young.updateMarket(0);

        double floorSqFt = LandManager.BLOCK_SQ_FT;
        boolean allWholeBlocks = true;
        double tiniest = Double.MAX_VALUE;
        for (LandParcel parcel : young.getListing()) {
            tiniest = Math.min(tiniest, parcel.getSizeSqFt());
            if (parcel.getSizeSqFt() < floorSqFt) allWholeBlocks = false;
        }
        assertTrue("nothing on offer is smaller than a block", allWholeBlocks);
        check("a new city is offered blocks of one", young.getMarket().getMinBlocks(), 1);
        System.out.printf("   smallest plot on a new city's window: %,.0f sq ft (%.1f blocks)%n",
                tiniest, tiniest / LandManager.BLOCK_SQ_FT);

        System.out.println("\n--- and the floor rises with the city ---");

        // 160 blocks in is where the design says the smallest on offer is five.
        LandManager grown = new LandManager();
        grown.setOwnedSqFt(LandManager.STARTING_SQ_FT + 160 * LandManager.BLOCK_SQ_FT);
        grown.updateMarket(20000);

        double grownFloor = grown.getMarket().getMinBlocks();
        System.out.printf("   a city 160 blocks in is offered nothing under %.0f blocks%n",
                grownFloor);
        assertTrue("a big city is not offered scraps", grownFloor >= 5);

        boolean allAboveFloor = true;
        double grownSmallest = Double.MAX_VALUE;
        for (LandParcel parcel : grown.getListing()) {
            grownSmallest = Math.min(grownSmallest, parcel.getSizeSqFt());
            if (parcel.getSizeSqFt() < grownFloor * LandManager.BLOCK_SQ_FT) {
                allAboveFloor = false;
            }
        }
        assertTrue("...and every plot it IS offered respects that floor", allAboveFloor);
        assertTrue("its smallest plot dwarfs a new city's", grownSmallest > tiniest * 3);

        // The floor is capped, because a listing whose cheapest entry is
        // unaffordable is a worse failure than being offered scraps.
        LandManager enormous = new LandManager();
        enormous.setOwnedSqFt(LandManager.STARTING_SQ_FT + 5000 * LandManager.BLOCK_SQ_FT);
        enormous.updateMarket(500000);
        assertTrue("the floor stops climbing eventually",
                enormous.getMarket().getMinBlocks() <= 15);
        System.out.printf("   a 5,000-block city: floor stops at %.0f blocks%n",
                enormous.getMarket().getMinBlocks());

        System.out.println("\n--- a tract can hold a mining district ---");

        // Sample a lot of parcels: multiple deposits are meant to be possible,
        // not usual, so one window is not enough to see the behaviour.
        LandMarket sampler = new LandMarket();
        int multi = 0, single = 0, mostSites = 0;
        boolean roomForEvery = true;
        for (int round = 0; round < 400; round++) {
            sampler.update(LandManager.STARTING_SQ_FT + 400_000_000, 0, 50000);
            for (LandParcel parcel : sampler.getListing()) {
                if (!parcel.hasIron()) continue;
                if (parcel.getDeposits() > 1) multi++; else single++;
                mostSites = Math.max(mostSites, parcel.getDeposits());

                // A mine is 400,000 sq ft. Selling more sites than the plot can
                // physically hold mines would be selling a number, not a mine.
                if (parcel.getSizeSqFt() < parcel.getDeposits() * 400_000.0) {
                    roomForEvery = false;
                }
            }
            for (LandParcel parcel : sampler.getListing()) {
                sampler.take(parcel.getId());
            }
        }
        System.out.printf("   of %d ore parcels seen, %d carried more than one site "
                + "(most: %d)%n", multi + single, multi, mostSites);
        assertTrue("some parcels carry more than one deposit", multi > 0);
        assertTrue("...but most still carry one", single > multi);
        assertTrue("every site has room for a mine", roomForEvery);

        System.out.println("\n--- the listing survives a save, old format included ---");

        LandManager saver = new LandManager();
        saver.setOwnedSqFt(LandManager.STARTING_SQ_FT + 90 * LandManager.BLOCK_SQ_FT);
        saver.updateMarket(12000);
        java.util.List<LandParcel> written = saver.getListing();

        LandMarket reloaded = new LandMarket();
        assertTrue("a listing restores", reloaded.restoreListingState(saver.getMarket().getListingState()));

        boolean survived = written.size() == reloaded.getListing().size();
        for (int i = 0; survived && i < written.size(); i++) {
            LandParcel a = written.get(i), b = reloaded.getListing().get(i);
            survived = a.getId() == b.getId()
                    && a.getSizeSqFt() == b.getSizeSqFt()
                    && a.getPriceUsd() == b.getPriceUsd()
                    && a.getIronTonnes() == b.getIronTonnes()
                    && a.getDeposits() == b.getDeposits();
        }
        assertTrue("...every field of every parcel, deposits included", survived);

        /*
         * A save written before deposits were counted. Four fields per parcel and
         * nextId in the first slot.
         *
         * TEN parcels of FOUR fields is forty values, which divides by five as
         * well - so a reader that decides the width by arithmetic reads this back
         * as eight parcels of shifted nonsense. That is why the current format
         * carries a marker, and it is why this case is tested at exactly ten.
         *
         * The shelf now holds nine, so the tenth is trimmed off the back after
         * the parse - which is why the size assertion reads LISTING_SIZE and
         * the ids are checked at BOTH ends. A width-five misread gives eight
         * parcels with ids taken out of the middle of the array, so the pair
         * still catches exactly the failure this case exists for.
         */
        double[] legacy = new double[1 + 10 * 4];
        legacy[0] = 77;
        for (int i = 0; i < 10; i++) {
            legacy[1 + i * 4]     = 100 + i;                     // id
            legacy[1 + i * 4 + 1] = 250_000 + i * 1000;          // sq ft
            legacy[1 + i * 4 + 2] = 400 + i;                     // price
            legacy[1 + i * 4 + 3] = (i == 3) ? 2_000_000 : 0;    // tonnes
        }

        LandMarket old = new LandMarket();
        assertTrue("a pre-deposit save still loads", old.restoreListingState(legacy));
        check("...read four fields wide, then trimmed to the shelf",
                old.getListing().size(), LandMarket.LISTING_SIZE);
        check("...their ids intact", old.getListing().get(0).getId(), 100);
        check("...to the last one kept",
                old.getListing().get(old.getListing().size() - 1).getId(),
                100 + LandMarket.LISTING_SIZE - 1);
        check("...their sizes not read as prices",
                old.getListing().get(0).getSizeSqFt(), 250_000);
        check("...and its one ore parcel counts as a single site",
                old.getListing().get(3).getDeposits(), 1);
        check("...while bare ground counts as none",
                old.getListing().get(0).getDeposits(), 0);

        /*
         * ...AND ITS PRICES WERE LOCAL MONEY (0.7.6). Read as dollars at the
         * rate of the day it is loaded - here 2.00, so a $400 plot is a
         * US$200 one and still costs $400 on the day; a dollar listing, the
         * one saved above, is left exactly as it was.
         */
        check("an older listing's prices wait as written for the loading rate",
                old.getListing().get(0).getPriceUsd(), 400);
        check("...and settle as dollars at it, every parcel of them",
                old.settleLocalPrices(2.0), LandMarket.LISTING_SIZE);
        check("...so a $400 plot is a US$200 one at 2.00",
                old.getListing().get(0).getPriceUsd(), 200);
        check("...and costs what the save said on the day it is loaded",
                old.getListing().get(4).localPrice(2.0), 404);
        check("...and settles once: a second call converts nothing",
                old.settleLocalPrices(2.0), 0);
        check("a dollar listing is not converted at all",
                reloaded.settleLocalPrices(2.0), 0);
        check("...its prices exactly as written",
                reloaded.getListing().get(0).getPriceUsd(), written.get(0).getPriceUsd());

        assertTrue("a length that is neither shape is refused",
                !new LandMarket().restoreListingState(new double[]{ 5, 1, 2 }));

        /* ==================== 6. not affording it ==================== */
        System.out.println("\n--- an empty treasury ---");

        LandManager broke = new LandManager();
        broke.updateMarket(0);
        LandParcel offer = broke.getListing().get(0);

        double offerLocal = offer.localPrice(ForeignAccounts.OPENING_RATE);
        check("cannot afford it -> pays nothing",
                broke.buyParcel(offer.getId(), offerLocal - 1, 0), 0);
        check("...and gets nothing", broke.getOwnedSqFt(), LandManager.STARTING_SQ_FT);
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

        boolean allHaveLand = true;
        double biggest = 0;
        String biggestName = "";

        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getLandSqFt() <= 0) {
                allHaveLand = false;
                System.out.println("  no footprint: " + t.getName());
            }
            if (t.getLandSqFt() > biggest) {
                biggest = t.getLandSqFt();
                biggestName = t.getName();
            }
        }

        assertTrue("every building has a footprint", allHaveLand);
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

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
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
         * AN OLDER SAVE, written by hand from this one: no toggle key, the
         * listing in the old marker with LOCAL prices that are not this
         * listing's dollars at any rate (so the reading cannot pass by
         * accident), and the office's prices three slots long.
         */
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                java.nio.file.Files.readString(files.saveFile(4))).getAsJsonObject();
        assertTrue("fixture: the save carried the toggle", json.has("landPaidFromVault"));
        json.remove("landPaidFromVault");
        com.google.gson.JsonArray listing = json.getAsJsonArray("landListing");
        listing.set(0, new com.google.gson.JsonPrimitive(-5.0));
        int parcels = (listing.size() - 2) / 5;
        for (int i = 0; i < parcels; i++) {
            listing.set(2 + i * 5 + 2, new com.google.gson.JsonPrimitive(1_000.0 + i * 10));
        }
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
        boolean read = old.getLandListing().size() == parcels;
        for (int i = 0; read && i < parcels; i++) {
            read = Math.abs(old.getLandListing().get(i).getPriceUsd() - (1_000.0 + i * 10) / loadRate) < 1e-9;
        }
        assertTrue("an older listing's local prices read as dollars at the loading rate", read);
        check("...so the first costs what the save said, on the day it is loaded",
                old.getLandListing().get(0).localPrice(loadRate), 1_000);
        check("...and the office's local quote reads the same way",
                old.getLandManager().getGroundUsdPerSqFt(), localQuote / loadRate);
    }
}
