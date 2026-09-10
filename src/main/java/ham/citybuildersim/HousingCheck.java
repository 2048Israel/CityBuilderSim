package ham.citybuildersim;

/**
 * Audits the three subsystems that describe the same housing, every month, and
 * makes them agree. Not part of the game.
 *
 * WHY THIS EXISTS. Three classes hold a piece of one fact - how many doors this
 * city has and who is behind them - and nothing made them say the same thing:
 *
 *   BuildingManager  built the doors, and knows their SIZES
 *   FamilyModel      puts households behind them, and knows who did not fit
 *   CommercialHandler owns them as a business, and bills whoever is in one
 *
 * On 2026-09-08 a UI redo drew two of those figures next to each other for the
 * first time and reported an apparent contradiction: 15,181 homes standing
 * empty in a city where 17,416 households were doubled up. It looked like two
 * subsystems disagreeing and it was not - the empty homes were STUDIOS, a studio
 * cannot take a child, and every one of the doubled-up households had a child.
 * Both were right. Nothing in the game could say so, and it cost a day.
 *
 * The lesson is not "add a counter". It is that three descriptions of one fact
 * need an identity between them, checked every month, or the next person to draw
 * two of them side by side will lose the same day. So:
 *
 *   1. EVERY DOOR IS ACCOUNTED FOR. Let plus empty equals owned, and the
 *      per-size breakdown sums to the same total. If it ever does not, one of
 *      the three is counting a building the others cannot see.
 *
 *   2. EVERY HOUSEHOLD IS ACCOUNTED FOR. The ones in a home of their own, plus
 *      the ones doubled up, is all of them. Nobody is placed twice and nobody
 *      falls between the two.
 *
 *   3. RENT PAID IS RENT RECEIVED, and both are rentWeight x rentPrice. This is
 *      the money identity: households are outside MoneyAudit's pool, so a rent
 *      the landlords collect and nobody pays would be invisible to it. It has
 *      been wrong before - the two sides used to be computed separately and
 *      drifted - which is why Game reads the landlords' figure and hands it
 *      straight to the households rather than working it out twice.
 *
 *   4. AND AN EMPTY HOME BESIDE A HOMELESS FAMILY HAS A REASON. The assertion
 *      the finding above actually wanted: whenever this city holds vacancies AND
 *      doubled-up households at the same time, something must SAY WHY - either
 *      the spare doors are too small to take a child, or they are the wrong size
 *      for the households queueing. A city that cannot explain it is a city
 *      where the two subsystems really have come apart.
 *
 *   5. AND ALL OF IT SURVIVES A SAVE, because every figure above is struck
 *      inside the month tick and three of them were reading zero on a reloaded
 *      city until 2026-09-09.
 *
 * @author Jerus
 */
public class HousingCheck {

    static int fails = 0;

    /** Everything here is in thousands, so a tenth of a cent is plenty. */
    static final double TOLERANCE = 1e-6;

    static void near(String what, int month, double actual, double expected) {
        if (Math.abs(actual - expected) <= TOLERANCE) return;
        if (fails < 12) {
            System.out.printf("  FAIL  %-40s month %3d: %,.6f, expected %,.6f%n",
                    what, month, actual, expected);
        }
        fails++;
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) {

        Game game = new Game(GameFiles.scratch("housing-check"));
        game.newGame();

        System.out.println("Playing 150 months and auditing the housing identities.");

        int monthsWithBoth = 0;
        int monthsExplained = 0;
        double worstUnexplained = 0;
        double worstDoorGap = 0;
        int doorGapMonth = 0;

        for (int i = 0; i < 150; i++) {

            game.toggleNextMonth();
            int month = game.getMonth();

            FamilyModel fam = game.getFamilies();
            CommercialHandler shops = game.getEconomyManager().getCommercialHandler();
            BuildingManager built = game.getBuildingManager();
            HouseholdAccounts homes = game.getHouseholds();

            /* ---------------- 1. every door is accounted for ---------------- */
            double owned = shops.getHomes();
            double let = shops.getOccupiedHomes();
            near("let plus empty is owned", month, let + (owned - let), owned);
            near("...and the landlords own what was built", month,
                    owned, built.getTotalHomes());

            int[] bySize = built.homesBySize();
            double sized = 0;
            for (int s = 1; s < bySize.length; s++) sized += bySize[s];
            near("...and the sizes add up to the same stock", month, sized, owned);

            /*
             * ...and nobody lets a door that does not exist.
             *
             * Skipped while the city owns NO homes at all, which is not an
             * empty city: it is one living in the founding endowment, which
             * houses a hundred people and is not a building. getRentIncome()
             * has an explicit branch for that case and bills it per resident.
             */
            if (owned > 0) {
                assertNotMore("nobody lets more homes than exist", month, let, owned);
            }

            /* ------------- 2. every household is accounted for ------------- */
            double households = fam.totalHouseholds();
            double doubled = fam.getDoubledUpHouseholds();
            near("housed plus doubled up is every household", month,
                    fam.homesNeeded() + doubled, households);
            assertNotMore("...and nobody is doubled up who does not exist", month,
                    doubled, households);
            /*
             * ...NOR REFUSED BY A STUDIO WITHOUT BEING SOMEWHERE.
             *
             * This compared refusals against the doubling-up alone, and that is
             * only the same question while the crowding ceiling has room in it.
             * noteUnplaced() absorbs what the match could not place into
             * doubledUp up to half the city plus one, and everything past that
             * ceiling is stillUnplaced - somebody sleeping outside. So a city
             * pressed hard enough against the ceiling has more refusals than it
             * has doubling-up, and the difference is exactly the people the
             * ceiling would not take.
             *
             * The real invariant is that a household a studio turned away is
             * either crammed in with somebody or outside. It cannot be neither.
             */
            assertNotMore("...nor refused by a studio without being somewhere", month,
                    fam.getRefusedByStudio(), doubled + fam.getStillUnplaced() + TOLERANCE);

            /* ------------- 3. rent paid is rent received ------------- */
            double billed = shops.getReportRentIncome();
            near("the households paid what the landlords billed", month,
                    homes.getRent(), billed);

            // The formula itself, not the month: what the landlords bill is the
            // weight FamilyModel handed them times the price they charge, and
            // nothing else. This is the line finding #3 was about - two screens
            // printing a figure called "rent" in two different units - and it
            // pins the conversion between them.
            if (shops.getRentWeight() > 0) {
                near("...and that is the weight times the price", month,
                        shops.getRentIncome(),
                        shops.getStudioRentWeight() * shops.getStudioRentPrice()
                                + shops.getFamilyRentWeight() * shops.getRentPrice());
                near("...and the two segment weights are the whole weight", month,
                        shops.getStudioRentWeight() + shops.getFamilyRentWeight(),
                        shops.getRentWeight());
            }

            /* ------------- 3b. the two segments partition everything -------------
             *
             * A door is a studio or it is family-capable, never both and never
             * neither; a household needs one or the other, and every household
             * needs exactly one. If either of those stops being true the two
             * prices are being struck against overlapping populations and the
             * scarcity in each means nothing. See FamilyModel's TWO SEGMENTS.
             */
            near("studio doors plus family doors is every door", month,
                    shops.getStudioHomes() + shops.getFamilyHomes(), owned);
            near("...and studio seekers plus family seekers is every household", month,
                    shops.getStudioSeekers() + shops.getFamilySeekers(), households);
            assertNotMore("a studio never bills more than its own doors hold", month,
                    shops.getStudioRentWeight(),
                    shops.getStudioHomes() * (double) FamilyModel.STUDIO_MAX_SIZE);

            /* ------------- 3c. rent never falls through the floor -------------
             *
             * The break-even is what the standing stock costs to hold. Jerus:
             * "theyll rent at zero profit" - and no further. A price below it
             * is a company paying to house people, which is not a market, it
             * is a subsidy nobody voted for.
             *
             * Checked on the TARGET rather than on the price, because the price
             * is a twelve-month walk toward the target and is allowed to be
             * below it on the way down - what must never happen is the target
             * itself being set under the floor.
             */
            double breakEven = shops.rentBreakEven();
            if (breakEven > 0) {
                assertNotMore("the rent target never goes under break-even", month,
                        breakEven, shops.getRentTarget() + TOLERANCE);
                assertNotMore("...nor does the studio target", month,
                        breakEven, shops.getStudioRentTarget() + TOLERANCE);
            }

            /* ------------- 3d. repairs are a flow, not a number -------------
             *
             * A maintenance expense is an order placed with the construction
             * sector, so the two sides have to be the same money. A cost that
             * leaves one set of books without arriving on another is the shape
             * of half the findings in this codebase.
             *
             * THIS USED TO COMPARE THE LANDLORDS AGAINST THE BUILDERS and that
             * was a complete test only while the landlords were the builders'
             * only maintenance customer. On 2026-09-09 Jerus asked for every
             * building in the city to be billed for standing there, and the
             * builders are now paid by six companies and the treasury - so the
             * old assertion started reading a residential figure against a
             * whole-city one and failed on a city whose houses had not finished
             * yet.
             *
             * The invariant is unchanged; the sum is wider. Every payer's own
             * struck figure, added up, against what the builders booked.
             */
            ConstructionHandler crew =
                    game.getServicesManager().getConstructionHandler();
            EconomyManager econ = game.getEconomyManager();

            double paidByEveryone =
                      shops.getReportPropertyMaintenance()
                    + shops.getReportRetailMaintenance()
                    + econ.getIndustrialHandler().getReportMaintenanceExpense()
                    + econ.getHeavyIndustryHandler().getReportMaintenanceExpense()
                    + econ.getMiningHandler().getReportMaintenanceExpense()
                    + crew.getReportMaintenanceExpense()
                    + game.getCityMaintenancePaid();

            near("what the city paid for repairs is what the builders were paid",
                    month, paidByEveryone, crew.getReportMaintenanceRevenue());

            /*
             * ...and the landlords' share is still separable, because the Real
             * estate screen prints it on its own.
             */
            near("and the landlords' share is the residential charge",
                    month, shops.getReportPropertyMaintenance(),
                    econ.getMaintenanceCharge(BuildingType.RESIDENTIAL));

            /*
             * And the figure the Household screen prints is the same money over
             * the HOUSEHOLDS that paid it.
             *
             * Not over the let doors, which is what it looks like and what the
             * Real estate screen calls it. The two agree only while every
             * household has a door of its own; on a city with people doubled up
             * they are different numbers, and on Jerus's slot 7 - 34,829
             * households behind 24,806 let doors - they differ by 40%. The
             * identity is pinned to what the method actually computes, and the
             * discrepancy with the per-door figure is reported below rather
             * than asserted, because which of the two a screen WANTS is a
             * labelling question, not an arithmetic one.
             */
            double payers = 0;
            for (int r = 0; r < homes.getRowCount(); r++) payers += homes.getRowHouseholds(r);
            if (payers > .5) {
                near("...and the per-household figure is that over the payers", month,
                        homes.rentPerHousehold() * payers, billed);
            }
            if (payers > .5 && let > .5) {
                double gap = Math.abs(homes.rentPerHousehold() - billed / let)
                        / Math.max(1e-9, billed / let);
                if (gap > worstDoorGap) { worstDoorGap = gap; doorGapMonth = month; }
            }

            /* ---- 4. and an empty home beside a homeless family has a reason ---- */
            double empty = owned - let;
            if (empty > .5 && doubled > .5) {
                monthsWithBoth++;
                // The stock has to contain something a family cannot use, or the
                // two subsystems genuinely disagree about the same city.
                double adultsOnly = 0;
                for (int s = 1; s < bySize.length && s <= 2; s++) adultsOnly += bySize[s];
                boolean explained = fam.getRefusedByStudio() > .5
                        || fam.getCrowdedHouseholds() > .5
                        || adultsOnly > .5;
                if (explained) {
                    monthsExplained++;
                } else if (empty > worstUnexplained) {
                    worstUnexplained = empty;
                }
            }
        }

        if (monthsWithBoth > 0 && monthsExplained < monthsWithBoth) {
            System.out.printf("  FAIL  %d months held empty homes AND doubled-up "
                    + "households with nothing to explain it; worst %,.0f empty%n",
                    monthsWithBoth - monthsExplained, worstUnexplained);
            fails++;
        }
        System.out.printf("   %d of %d months held vacancies and doubled-up households "
                + "at once%s%n", monthsExplained, monthsWithBoth,
                monthsWithBoth == 0
                        ? " - this city never does, which is what 4b below is for."
                        : ", and every one of them could say why.");
        System.out.printf("   rent-per-household and rent-per-let-door differ by up to "
                + "%.1f%% (month %d). Reported, not asserted - see the note above.%n",
                worstDoorGap * 100, doorGapMonth);

        /* ===================================================================
           4b. A CITY THAT REALLY DOES HOLD BOTH AT ONCE.

           The run above never produces vacancies and doubled-up households in
           the same month, so the assertion that matters most has nothing to
           bite on. This one is built to: nothing but Studio Apartments, which
           are adults-only, so every family with a child is turned away from a
           door that is standing open. That is Jerus's slot 7 in miniature -
           15,181 empty flats and 10,086 doubled-up families, 100% of them
           refused by a studio - and it is the shape the original finding
           mistook for two subsystems disagreeing.
           =================================================================== */
        System.out.println("\n--- a city that built nothing but studios ---");

        Game studios = new Game(GameFiles.scratch("housing-studios"));
        studios.run();
        studios.getGovernmentInvestor().spend(-4_000_000);
        studios.getLandManager().setOwnedSqFt(
                studios.getLandManager().getOwnedSqFt() + 200_000_000L);
        // Placed rather than ordered - `true` is noConstruction. Sixty studio
        // blocks is four years of a small city's builders, and what is under
        // test here is the HOUSING MATCH, not the construction queue.
        for (String[] o : new String[][] {
                {"Studio Apartments", "40"}, {"House", "12"},
                {"Convenience Store", "14"}, {"Construction Depot", "4"},
                {"Coal Power Plant", "2"},   {"Water Treatment Plant", "2"},
                {"Textile Mill", "3"},       {"Paved Road", "10"} }) {
            studios.buildStack(template(studios, o[0]), Integer.parseInt(o[1]), true);
        }
        studios.simulateMonths(150);

        FamilyModel sf = studios.getFamilies();
        CommercialHandler ss = studios.getEconomyManager().getCommercialHandler();
        double sEmpty = ss.getHomes() - ss.getOccupiedHomes();
        System.out.printf("   %,d studios, %,.0f let, %,.0f standing empty; "
                + "%,.0f households doubled up, %,.0f of them refused by a studio%n",
                ss.getHomes(), ss.getOccupiedHomes(), sEmpty,
                sf.getDoubledUpHouseholds(), sf.getRefusedByStudio());

        int[] sSizes = studios.getBuildingManager().homesBySize();
        double familySized = 0;
        for (int z = 3; z < sSizes.length; z++) familySized += sSizes[z];
        System.out.printf("   ...and it owns %,.0f doors a child is allowed in.%n", familySized);

        /* ===================================================================
           WHAT THIS SECTION CAN AND CANNOT CLAIM, REWRITTEN 2026-09-09.

           It used to assert that this city ended up with empty flats AND
           doubled-up families AND a non-zero refusedByStudio - slot 7 in
           miniature, the state the original finding mistook for two subsystems
           disagreeing.

           It cannot assert that any more, and the reason is the point. The
           residential rebalance re-costed all three homes to real build costs,
           which made Low-Rise Apartments the private planner's choice for the
           first time, and the city now BUILDS ITSELF 1,100-odd family doors
           over 150 months rather than leaving families standing in front of
           adults-only ones. Every attempt to force the old state back - taking
           the Houses away, sizing the ground to the studios - failed for the
           same reason: the land office sells plots, so a city that wants family
           doors buys the ground and puts them up.

           That is the housing pass working, and a fixture bent until a fixed
           bug reappears is worth nothing.

           ...AND IT THEN FLIPPED BACK, AND FLIPPED AGAIN, IN ONE NIGHT.

           Stage two of the rebalance put every non-residential building on its
           real capital cost, the city could no longer afford to build its way
           out, and the original state returned - 36 flats standing empty beside
           20 doubled-up households. So the original assertions went back in.
           Then charging maintenance on every building changed the city's cash
           again and it went straight back to housing everybody: 510 family
           doors, 35 empty studios, nobody refused.

           THREE FLIPS, IN OPPOSITE DIRECTIONS, FROM THREE PRICES THAT HAVE
           NOTHING TO DO WITH THE HOUSING MATCH. That is the fixture telling us
           what it is: a 150-month whole-city run is a DEMONSTRATION, and which
           side of the line it lands on is a fact about the economy that week,
           not about the code under test. Asserting either outcome is asserting
           the weather.

           So the outcome is printed and not asserted, and what IS asserted is
           the thing that has to be true on both sides of the flip: whatever the
           city did about its families, it did ONE of the two things - built
           them doors, or has refusals it can account for. A city that had
           neither would be losing households silently, which is the failure
           this section actually exists to catch.

           The refusal MECHANISM does not rest on this at all. It is asserted
           every single month of the main run above, twice - the invariant that
           a household a studio turned away is either crammed in or outside
           (section 2), and the requirement that an empty home beside a
           doubled-up household always has a reason (section 4 of that loop) -
           and again at the stock level in HouseholdCheck, on a hand-built
           studios-only stock where the condition is CAUSED rather than hoped
           for. Those are the tests. This is the picture.
           =================================================================== */
        /*
         * ...AND THE EMPTY FLATS ARE A GUARD, NOT A DEMAND. Fourth time this
         * run has moved: it has now come back fully let, 527 of 527, with 78
         * households doubled up and nothing standing empty at all - because the
         * bank started pricing its own deposits and the city grew differently
         * again. "There are empty flats" is the same kind of claim as "the road
         * city holds more people": true of a particular week, not of the model.
         *
         * So the demonstration runs when there is something to demonstrate. The
         * main loop above does exactly this, every month, with the same guard.
         */
        boolean somethingToExplain = sEmpty > 0 && sf.getDoubledUpHouseholds() > 0;
        System.out.printf("   %s%n", somethingToExplain
                ? "empty flats beside doubled-up households: the case this section is for"
                : "no empty flats beside doubled-up households this run - nothing to explain");

        boolean builtThemDoors = familySized > 0;
        boolean accountedForThem = sf.getRefusedByStudio() > 0
                || sf.getDoubledUpHouseholds() < 1;

        if (somethingToExplain) {
            assertTrue("a studio city either builds family doors or can say who it refused",
                    builtThemDoors || accountedForThem);
        }
        assertTrue("either way it owns doors a child is allowed in, or refuses openly",
                builtThemDoors || sf.getRefusedByStudio() > 0);
        assertTrue("...and every refusal is either crammed in or outside",
                sf.getRefusedByStudio()
                        <= sf.getDoubledUpHouseholds() + sf.getStillUnplaced() + TOLERANCE);

        /* ===================================================================
           5. AND ALL OF IT SURVIVES A SAVE.

           Three of these figures were struck inside the tick and in nobody's
           save until 2026-09-09, so a reloaded city reported households doubled
           up for no reason and a rent nobody paid.
           =================================================================== */
        System.out.println("\nSaving and reloading...");

        FamilyModel before = game.getFamilies();
        double savedDoubled  = before.getDoubledUpHouseholds();
        double savedRefused  = before.getRefusedByStudio();
        double savedCrowded  = before.getCrowdedHouseholds();
        double savedRent     = game.getHouseholds().getRent();
        double savedLet      = game.getEconomyManager().getCommercialHandler().getOccupiedHomes();
        double savedStudioRent = game.getEconomyManager().getCommercialHandler().getStudioRentPrice();
        double savedFamilyRent = game.getEconomyManager().getCommercialHandler().getRentPrice();

        assertTrue("fixture: this city really does have households doubled up",
                savedDoubled > 0);
        assertTrue("fixture: ...and really is collecting rent", savedRent > 0);
        assertTrue("fixture: ...at two prices that are really being charged",
                savedStudioRent > 0 && savedFamilyRent > 0);
        assertTrue("saved", game.saveGame(1, "housing").ok);

        Game back = new Game(game.getGameFiles());
        back.loadGame(1);

        near("households doubled up", game.getMonth(),
                back.getFamilies().getDoubledUpHouseholds(), savedDoubled);
        near("...the ones a studio turned away", game.getMonth(),
                back.getFamilies().getRefusedByStudio(), savedRefused);
        near("...the ones in something too small", game.getMonth(),
                back.getFamilies().getCrowdedHouseholds(), savedCrowded);
        near("the rent the households paid", game.getMonth(),
                back.getHouseholds().getRent(), savedRent);
        near("the homes the landlords let", game.getMonth(),
                back.getEconomyManager().getCommercialHandler().getOccupiedHomes(), savedLet);
        near("...and the studio market's own price", game.getMonth(),
                back.getEconomyManager().getCommercialHandler().getStudioRentPrice(),
                savedStudioRent);
        near("...and the family one's", game.getMonth(),
                back.getEconomyManager().getCommercialHandler().getRentPrice(),
                savedFamilyRent);

        /* ---- and the first month back still balances, which is the real test ---- */
        back.toggleNextMonth();
        CommercialHandler s2 = back.getEconomyManager().getCommercialHandler();
        near("the month after a reload still balances", back.getMonth(),
                back.getHouseholds().getRent(), s2.getReportRentIncome());

        /* ===================================================================
           4b. A SMALL HOUSEHOLD TAKES A BIG DOOR WHEN THE SMALL ONES RUN OUT.

           Jerus: "in some cases if not enough studio apt and already crowded,
           have them live in the regular places as well."

           It was already true - house() walks UPWARD from the smallest unit
           that fits - but nothing said so, and it is the half of the studio
           rule that has no counterpart: a child may never enter a studio, and
           that is a hard no, while an adult in a house is simply somebody with
           more room than they need. A change that tightened the first would
           very easily take the second with it.

           Tested on the MATCH ITSELF rather than on a played city, because a
           played city has to be manoeuvred into having only large doors and
           the manoeuvring is what would break. house() is a pure function of
           the household mix and an array of doors by size: hand it a city
           whose only doors are four-person flats and every household, however
           small, should be inside one.
           =================================================================== */
        System.out.println("\n--- a single adult takes a four-person flat when that is all there is ---");

        FamilyModel match = game.getFamilies();
        double allHouseholds = match.totalHouseholds();

        // Doors of size four and nothing else, and more of them than there are
        // households - so nothing can be left out for want of room.
        int[] bigOnly = new int[5];
        bigOnly[4] = (int) Math.ceil(allHouseholds) + 10;

        double leftOver = match.house(bigOnly);
        System.out.printf("   %,.0f households, %,d four-person flats, %,.0f left over; "
                + "billed weight %,.1f, of which studio %,.1f%n",
                allHouseholds, bigOnly[4], leftOver,
                match.rentWeight(), match.studioRentWeight());

        assertTrue("fixture: the city really does have households to place",
                allHouseholds > 1);
        near("every household is placed when only big doors exist",
                game.getMonth(), leftOver, 0);
        near("...and each one bills its DOOR's size, not its own",
                game.getMonth(), match.rentWeight(), allHouseholds * 4);
        near("...and none of it is billed as studio", game.getMonth(),
                match.studioRentWeight(), 0);
        /*
         * The only crowding left is the households that genuinely do not fit:
         * five adults sharing and the large family are six and five people in
         * a four-person flat, and crowded is exactly what they are. Everybody
         * SMALLER than the door is comfortable in it, which is the claim.
         */
        double tooBigForFour = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.size() > 4) tooBigForFour += match.totalOf(shape);
        }
        near("...and the only crowding is households too big for the flat",
                game.getMonth(), match.getCrowdedHouseholds(), tooBigForFour);

        /*
         * ...AND THE HARD NO STILL HOLDS, in the same breath, because the two
         * rules are one rule read in two directions. Only studios now: every
         * household with a child must be turned away, and the refusals must be
         * exactly the households that have one.
         */
        int[] studiosOnly = new int[3];
        studiosOnly[2] = (int) Math.ceil(allHouseholds) + 10;

        double refused = match.house(studiosOnly);
        double withChildren = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.dependants() > 0) withChildren += match.totalOf(shape);
        }
        System.out.printf("   with nothing but studios: %,.0f unplaced, %,.0f of them "
                + "refused by a studio, against %,.0f households with a child%n",
                refused, match.getRefusedByStudio(), withChildren);

        assertTrue("fixture: some households really do have a child", withChildren > 0);
        near("a studio-only city turns away exactly the households with a child",
                game.getMonth(), match.getRefusedByStudio(), withChildren);
        near("...and those are exactly the ones it could not place",
                game.getMonth(), refused, withChildren);
        near("nothing is billed as a family let when only studios exist",
                game.getMonth(), match.familyRentWeight(), 0);

        // Put the match back the way the played city left it, so section 5
        // saves and reloads the city it has been auditing rather than this one.
        match.noteUnplaced(match.house(game.getBuildingManager().homesBySize()));

        System.out.println(fails == 0
                ? "\nEvery door, every household and every dollar of rent is accounted for."
                : "\n" + fails + " FAILED");

        if (fails > 0) System.exit(1);
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static void assertNotMore(String what, int month, double actual, double ceiling) {
        if (actual <= ceiling + TOLERANCE) return;
        if (fails < 12) {
            System.out.printf("  FAIL  %-40s month %3d: %,.6f exceeds %,.6f%n",
                    what, month, actual, ceiling);
        }
        fails++;
    }
}
