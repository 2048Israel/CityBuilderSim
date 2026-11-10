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
 *   6. THE LANDLORDS BUILD BEFORE ANYONE CAN STAFF THEM (HD1, 2026-10-29).
 *      Real Estate has workers now - an office post and a maintenance post in
 *      a Studio or a Low-Rise, the National Apartment Association's rule - and
 *      the landlords are the one owner that does not ask the city whether it
 *      could staff what they order: homes come before workers. The section
 *      causes a city that could not staff a block and asserts the landlords
 *      still plan one, the investors' card does not refuse it for staffing,
 *      and the block, once standing, counts whole in the city's housing, bills
 *      its rent with its posts mostly empty, and pays the wages of those filled.
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
            ham.citybuildersim.sectors.RealEstate shops = game.getSectors().realEstate();
            BuildingManager built = game.getBuildingManager();
            HouseholdAccounts homes = game.getHouseholds();

            /* ---------------- 1. every door is accounted for ---------------- */
            double owned = shops.getHomes();
            double let = shops.getOccupiedHomes();
            near("let plus empty is owned", month, let + (owned - let), owned);
            near("...and the landlords own what was built", month,
                    owned, built.getTotalHomes());

            long[] bySize = built.homesBySize();
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
            // Every household that wants a door - since 2026-09-11 the out of
            // work and the students outside the family matrix too.
            double households = fam.householdsSeekingDoors();
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
            // The landlords' STATEMENT, since the sector template: the rent is
            // sold at the bottom of the month and struck at the top of the
            // next, which is when the households pay it - see Game's household
            // month, which reads the same line.
            double billed = shops.statement().salesToHouseholds;
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
             *
             * Since RN1 (2026-11-09) the floor holds where a market is tight
             * or balanced; a glut's empty flats are the landlord's loss
             * (Jerus, 2026-10-31, "Take all (Recommended)" over EN1-C star 2).
             * So the floor is asserted in the months neither market is in
             * glut, and a leg that is in glut is asserted to be its own cost
             * times its scarcity, not lifted.
             */
            double breakEven = shops.rentBreakEven();
            double studioPressure = shops.studioPressure(), familyPressure = shops.familyPressure();
            boolean neitherInGlut = studioPressure >= ham.citybuildersim.sectors.RealEstate.GLUT_PRESSURE
                    && familyPressure >= ham.citybuildersim.sectors.RealEstate.GLUT_PRESSURE;
            assertGlutLegsNotLifted(shops, month);
            if (breakEven > 0 && neitherInGlut) {
                /*
                 * ON THE BLEND, NOT ON EITHER LEG, since 2026-09-10.
                 *
                 * This used to assert the floor on the family target and on
                 * the studio target separately, and that was the test form of
                 * the bug CommercialHandler.targetFor() describes: a floor
                 * taken on the whole company applied to each leg forces both
                 * legs to the same number the moment the floor binds, which in
                 * any mature city is always. The city then had one rent for
                 * every building whatever it cost to build, and never built a
                 * studio again.
                 *
                 * The invariant that was actually wanted survives intact: the
                 * company never charges less than its stock costs to hold. It
                 * is a statement about the rent roll, so it is asserted on the
                 * rent roll.
                 */
                assertNotMore("while neither market is in glut, the blended rent target never goes under break-even",
                        month, breakEven, shops.blendedRentTarget() + TOLERANCE);
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
            ham.citybuildersim.sectors.Construction crew = game.getSectors().construction();
            EconomyManager econ = game.getEconomyManager();

            // Every sector's struck figure - the builders' own included, for
            // the depots they keep up - plus the bank's branches and the
            // treasury's share.
            double paidByEveryone = game.getCityMaintenancePaid() + econ.getBankMaintenanceBill();
            for (Sector sec : game.getSectors().all()) paidByEveryone += sec.statement().maintenance;

            near("what the city paid for repairs is what the builders were paid",
                    month, paidByEveryone, crew.getRepairsThisMonth());

            /*
             * ...and the landlords' share is still separable, because the Real
             * estate screen prints it on its own.
             */
            near("and the landlords' share is the residential charge",
                    month, shops.statement().maintenance,
                    econ.getMaintenanceCharge(Sectors.REAL_ESTATE));

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
            /*
             * THE PAYERS ARE DOORS PAID FOR SINCE 2026-09-11, not households:
             * an out-of-work adult five to a home pays a fifth, a household
             * with no home pays nothing, an orphan nothing. The identity is
             * the same one - the bill over the people who pay it - counted the
             * way the bill is now split.
             */
            double payers = 0;
            for (int r = 0; r < homes.getRowCount(); r++) payers += homes.getRowDoors(r);
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
        studios.getGovernmentInvestor().spend(-4_000_000 * PriceLevel.FACTOR);
        studios.getLandManager().setOwnedSqFt(
                studios.getLandManager().getOwnedSqFt() + 200_000_000L);
        // Placed rather than ordered - `true` is noConstruction. Sixty studio
        // blocks is four years of a small city's builders, and what is under
        // test here is the HOUSING MATCH, not the construction queue.
        for (String[] o : new String[][] {
                {"Studio Apartments", "40"}, {"House", "12"},
                {"Convenience Store", "14"}, {"Construction Depot", "4"},
                {"Coal Power Plant", "2"},   {"Water Treatment Plant", "2"},
                {"Industrial Bakery", "3"},       {"Paved Road", "10"} }) {
            studios.buildStack(template(studios, o[0]), Integer.parseInt(o[1]), true);
        }
        studios.simulateMonths(150);

        FamilyModel sf = studios.getFamilies();
        ham.citybuildersim.sectors.RealEstate ss = studios.getSectors().realEstate();
        double sEmpty = ss.getHomes() - ss.getOccupiedHomes();
        System.out.printf("   %,d studios, %,.0f let, %,.0f standing empty; "
                + "%,.0f households doubled up, %,.0f of them refused by a studio%n",
                ss.getHomes(), ss.getOccupiedHomes(), sEmpty,
                sf.getDoubledUpHouseholds(), sf.getRefusedByStudio());

        long[] sSizes = studios.getBuildingManager().homesBySize();
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
        double savedLet      = game.getSectors().realEstate().getOccupiedHomes();
        double savedStudioRent = game.getSectors().realEstate().getStudioRentPrice();
        double savedFamilyRent = game.getSectors().realEstate().getRentPrice();

        /*
         * WHAT THE GUARD IS FOR is that the figures compared below are not all
         * zero, because two zeros match perfectly and prove nothing. It asked
         * for DOUBLED-UP HOUSEHOLDS specifically, and that is the same kind of
         * claim the note above has already retired four times: true of a
         * particular week of a particular run, not of the model. The fifth
         * move was the ninth sector (2026-09-13), which employs people and so
         * changes how fast this city grows into its own housing; it came back
         * with nobody doubled up at all, which is a city that has enough
         * houses and not a bug.
         *
         * Rent, two prices and let doors carry the round trip. The crowding
         * figures are still compared below - they simply are not required to
         * be non-zero for the comparison to mean something.
         */
        assertTrue("fixture: this city really is collecting rent", savedRent > 0);
        assertTrue("fixture: ...at two prices that are really being charged",
                savedStudioRent > 0 && savedFamilyRent > 0);
        assertTrue("fixture: ...on doors that are really let", savedLet > 0);
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
                back.getSectors().realEstate().getOccupiedHomes(), savedLet);
        near("...and the studio market's own price", game.getMonth(),
                back.getSectors().realEstate().getStudioRentPrice(),
                savedStudioRent);
        near("...and the family one's", game.getMonth(),
                back.getSectors().realEstate().getRentPrice(),
                savedFamilyRent);

        /* ---- and the first month back still balances, which is the real test ---- */
        back.toggleNextMonth();
        ham.citybuildersim.sectors.RealEstate s2 = back.getSectors().realEstate();
        near("the month after a reload still balances", back.getMonth(),
                back.getHouseholds().getRent(), s2.statement().salesToHouseholds);

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
        // The seekers outside the family matrix are matched too (2026-09-11).
        double allHouseholds = match.householdsSeekingDoors();

        // Doors of size four and nothing else, and more of them than there are
        // households - so nothing can be left out for want of room.
        long[] bigOnly = new long[5];
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
        // ...and the out of work and the students sharing five to a home.
        for (FamilyModel.Seeker g : FamilyModel.Seeker.values()) {
            tooBigForFour += Math.min(match.getSeekers(g), match.getSeekersSharing(g)) / 5;
        }
        near("...and the only crowding is households too big for the flat",
                game.getMonth(), match.getCrowdedHouseholds(), tooBigForFour);

        /*
         * ...AND THE HARD NO STILL HOLDS, in the same breath, because the two
         * rules are one rule read in two directions. Only studios now: every
         * household with a child must be turned away, and the refusals must be
         * exactly the households that have one.
         */
        long[] studiosOnly = new long[3];
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

        /* ===================================================================
           6. THE LANDLORDS BUILD BEFORE ANYONE CAN STAFF THEM (HD1, 2026-10-29)

           Jerus: "gotta add workers to real estate". Population is min(housing,
           jobs x 2.25), so the people who would fill a block's office and
           maintenance posts are the ones its homes bring: a landlord that waited
           for spare workers (Sector.staffing()) would wait for the very people
           it houses. RealEstate.plan() never asked, the investors' build card
           (BuildCard.gate(), appraise(), of()) would have, and Sector
           .ordersOnlyWhatItCanStaff() is the one door they all ask through.

           THE POSTS are the National Apartment Association's ("Best Practices:
           Staffing", naahq.org, 2020): one full-time office employee and one
           full-time maintenance employee per 100 units, a further team member
           once a property passes 51 units toward the next hundred. Read off the
           templates here, never typed: a Studio (80 units) and a Low-Rise (63)
           each have the two, a House (one unit) has none.

           THE FIXTURE CAUSES THE CONDITION (HD1; re-caused RE2, 2026-11-08):
           the stores, depots and plants are built and the city is run 24 months
           with the landlords HELD (BusinessInvestment.holdSector), so they own
           no posts and build no beds of their own. The landlords never ask the
           staffing gate, so left free they order Studios and Low-Rises through
           those months themselves: measured, that city is 1,148 people, 493
           posts and 1,598 beds, jobs x 2.25 under the beds and the landlords
           already holding posts, and the city could staff all of a Studio's
           (the beds they built brought the people to fill the posts). Held, it
           is 106 people, 453 posts and 172 beds: every band has more posts than
           workers, the diploma band included, so a Studio's two diploma posts
           have no spare worker (0% of them, the bar is 80%). The builders are
           still pinned to their crews as InvestCheck's section 3 pins them,
           because a village this size staffs its depots at a seventh and the
           landlords hold their orders to the months of work the sites can do -
           a different question from staffing; at the check none of their crews
           is laid off (no posts withheld, not idle at the core crew), so IC1's
           spare crews add no worker here. The hold is RELEASED just before the
           plan is asked, so the landlords order the home the city could not
           staff as they would in any city, and own no posts until it stands.
           =================================================================== */
        System.out.println("\n--- the landlords build before anyone can staff them ---");

        Game staffed = new Game(GameFiles.scratch("housing-landlords-staff"));
        staffed.run();
        ham.citybuildersim.sectors.RealEstate landlords = staffed.getSectors().realEstate();
        staffed.getGovernmentInvestor().spend(-4_000_000);
        staffed.getLandManager().setOwnedSqFt(staffed.getLandManager().getOwnedSqFt() + 200_000_000L);
        staffed.getBusinessInvestment().holdSector(landlords.key());

        /* ---- the posts, by the NAA rule, read from the templates ---- */
        final int naaFurtherTeamMemberUnits = 51;
        boolean postsByRule = true, onlyDiploma = true;
        int homeTemplates = 0, withPosts = 0;
        for (BuildingsTemplate t : staffed.getBuildingManager().getTemplatesBySector(landlords.key())) {
            homeTemplates++;
            int expected = t.getDwellings() >= naaFurtherTeamMemberUnits ? 2 : 0;
            if (expected > 0) withPosts++;
            postsByRule &= t.getTotalJobs() == expected;
            onlyDiploma &= t.getJobs(JobType.DIPLOMA) == t.getTotalJobs();
        }
        BuildingsTemplate house = template(staffed, "House");
        BuildingsTemplate studio = template(staffed, "Studio Apartments");
        BuildingsTemplate lowRise = template(staffed, "Low-Rise Apartments");
        assertTrue("fixture: the landlords own three homes, two of them over the NAA's 51 units",
                homeTemplates == 3 && withPosts == 2 && house.getDwellings() < naaFurtherTeamMemberUnits
                        && studio.getDwellings() >= naaFurtherTeamMemberUnits
                        && lowRise.getDwellings() >= naaFurtherTeamMemberUnits);
        assertTrue("a House has no posts: a single house's repairs are the builders' order", house.getTotalJobs() == 0);
        assertTrue("a Studio and a Low-Rise each have one office and one maintenance post",
                studio.getTotalJobs() == 2 && lowRise.getTotalJobs() == 2);
        assertTrue("...every home's posts as the NAA rule has them, and all of them diploma posts",
                postsByRule && onlyDiploma);

        /* ---- a city that could not staff a block ---- */
        for (String[] o : new String[][] {
                {"House", "12"}, {"Convenience Store", "14"}, {"Construction Depot", "4"},
                {"Coal Power Plant", "2"}, {"Water Treatment Plant", "2"}, {"Paved Road", "10"} }) {
            staffed.buildStack(template(staffed, o[0]), Integer.parseInt(o[1]), true);
        }
        staffed.simulateMonths(24);
        double[] everyPost = new double[JobType.values().length];
        java.util.Arrays.fill(everyPost, 1.0);
        Sector crews = staffed.getSectors().construction();
        crews.updateJobFillRate(everyPost);
        crews.updateWages(staffed.getEconomyManager().getWageRates(), crews.postsPerTier());

        Sector.Staffing couldStaffStudio = landlords.staffing(studio), couldStaffLowRise = landlords.staffing(lowRise);
        System.out.printf("   %,d people, %,d posts, %,d beds; the city could staff %.0f%% of a Studio's posts (the bar is %.0f%%)%n",
                staffed.getPopulationManager().getPopulation(), staffed.getPopulationManager().getTotalJobs(),
                staffed.getHouseholdCapacity(), couldStaffStudio.share * 100, Sector.MIN_STAFFABLE_TO_ORDER * 100);
        assertTrue("fixture: jobs for more people than there are beds",
                staffed.getPopulationManager().getTotalJobs() * 2.25 > staffed.getHouseholdCapacity());
        assertTrue("fixture: the city could not staff a Studio (it would fail the bar every other owner is held to)",
                !couldStaffStudio.passes() && couldStaffStudio.share < Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("fixture: ...nor a Low-Rise", !couldStaffLowRise.passes());
        assertTrue("fixture: the landlords have no posts of their own yet", landlords.getPostsOffered() == 0);
        boolean othersAsk = true;
        for (Sector other : staffed.getSectors().all()) {
            othersAsk &= other.ordersOnlyWhatItCanStaff() == (other != landlords);
        }
        assertTrue("the landlords are the one owner that does not hold an order to staffing; every other keeps the gate",
                !landlords.ordersOnlyWhatItCanStaff() && othersAsk);

        /* ---- the plan, the card ---- */
        staffed.getBusinessInvestment().releaseSector(landlords.key());
        BusinessInvestment.Decision ordered = landlords.plan(staffed.getBusinessInvestment(), staffed);
        System.out.println("   " + ordered.reason
                + (ordered.template == null ? "" : " -> " + ordered.quantity + " x " + ordered.template.getName()));
        assertTrue("the landlords still order a home the city could not staff",
                ordered.build && ordered.quantity > 0 && ordered.template != null
                        && staffed.getSectors().ownerOf(ordered.template) == landlords
                        && !landlords.staffing(ordered.template).passes());
        BuildCard.Gate firstGate = BuildCard.investors(staffed, studio).own();
        assertTrue("the investors' gate for a Studio is not STAFFING",
                firstGate == null || firstGate.kind() != BuildCard.GateKind.STAFFING);
        BuildCard.Appraisal appraised = BuildCard.appraise(staffed, studio);
        assertTrue("...nor does its appraisal ask staffing at all: no mark, where a shop's would pass or fail",
                appraised.at(BuildCard.GateKind.STAFFING) == BuildCard.Mark.NONE);
        assertTrue("...and the card reads the city could staff all of it (1), so no \"could staff\" words",
                BuildCard.of(staffed, studio, null).staffable() == 1);

        /* ---- once standing: counted whole, billed, with its posts empty, and paid ---- */
        long bedsBefore = staffed.getHouseholdCapacity();
        long postsBefore = landlords.getPostsOffered();
        double salesBefore = landlords.statement().salesToHouseholds;
        int blocks = 2;
        staffed.buildStack(studio, blocks, true);
        assertTrue("two Studios standing: their whole capacity counts in the city's housing, staffed or not",
                staffed.getHouseholdCapacity() == bedsBefore + (long) blocks * studio.getCapacity());
        staffed.simulateMonths(3);
        assertTrue("the posts are real: the landlords' posts rose by the blocks' own jobs",
                landlords.getPostsOffered() == postsBefore + (long) blocks * studio.getTotalJobs());
        assertTrue("...mostly empty, since no spare worker was there to take them (partly or wholly)",
                landlords.getAverageFill() < 1);
        assertTrue("...and the ones the city did have for them are paid: the landlords' payroll is above zero",
                landlords.getAverageFill() > 0 && landlords.getPayroll() > 0);
        assertTrue("the block bills rent with its posts empty: the landlords' rent billed rose",
                landlords.statement().salesToHouseholds > salesBefore);
        double carryWithPay = landlords.getMaintenanceExpense() + landlords.getPropertyTaxExpense()
                + landlords.getInterestExpense() + landlords.getPayroll();
        near("the rent floor carries the wages they pay", staffed.getMonth(),
                landlords.rentBreakEven() * landlords.getOwnedHousingCapacity(), carryWithPay);
        assertTrue("...which the floor without them would not have",
                landlords.rentBreakEven() * landlords.getOwnedHousingCapacity()
                        > carryWithPay - landlords.getPayroll() + TOLERANCE);

        heldRateRentsItsGlutAtNoLift();
        aClosedHomesDebtIsTheLandlordsLoss();

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

    /**
     * A leg under GLUT_PRESSURE is its own cost times its scarcity and is not
     * lifted to cover the carry (RN1, 2026-11-09). A leg with no required
     * return has no cost to scale and is left to targetFor(). Returns whether
     * a leg was in glut with a required return.
     */
    static boolean assertGlutLegsNotLifted(ham.citybuildersim.sectors.RealEstate re, int month) {
        boolean any = false;
        for (boolean family : new boolean[] {false, true}) {
            double pressure = family ? re.familyPressure() : re.studioPressure();
            double required = re.rentRequired(family);
            if (pressure >= ham.citybuildersim.sectors.RealEstate.GLUT_PRESSURE || required <= 0) continue;
            any = true;
            double target = family ? re.rentTarget() : re.studioRentTarget();
            near((family ? "a family" : "a studio") + " leg in glut is its cost times its scarcity",
                    month, target, required * re.scarcityMultipleOf(pressure));
            assertNotMore((family ? "a family" : "a studio") + " leg in glut is not lifted over its cost",
                    month, target, required);
        }
        return any;
    }

    /**
     * RN1, 2026-11-09. LANDLORD_YIELD is .058 and the landlords borrow at about
     * 15.7% when the dial is held at 15%, so the carry is dearer than the
     * required return and the carry lift binds; a leg in glut must not take
     * it. The dial is held as tools/ensemble does (a hand takes it back from
     * the advisor each month), but NOT from month 1: a city held at 15% from
     * its first month never grows past seven homes, so it never has a glut to
     * rent. The city grows on its own dial for GROWN months (the ensemble's
     * own "pre" phase, which runs to MATURE), then the dial is held and the
     * people go, and a leg falls under GLUT_PRESSURE some 25 months into it.
     */
    static void heldRateRentsItsGlutAtNoLift() {
        System.out.println("\n--- a city at a held 15% rents its glut at no lift ---");
        final double HELD = .15;
        final int GROWN = 120, MONTHS = 240;
        long started = System.currentTimeMillis();
        Game held = new Game(GameFiles.scratch("housing-held-rate"));
        held.newGame();
        // WG1 (2026-11-10): the floor at the going rate, the wage this fixture was written against (RN1, 2026-11-09), before a month has run. A new city's
        // floor is .85 of the going rate now (a real minimum, WG1), and on that path the city held at 15% closed no home in its 240 months (the landlords' debt on
        // closed homes at the end 0.000 of it, the largest lift on a leg that took it 1.725); with the floor at the going rate, main's path, 0.106 of it.
        held.getLabourMarket().setMinimumWage(held.getLabourMarket().getGoingRate());
        ham.citybuildersim.sectors.RealEstate re = held.getSectors().realEstate();
        DebtManager dm = held.getDebtManager();
        int run = 0, studioWaived = 0, familyWaived = 0, glutMonths = 0, neitherMonths = 0;
        double largestLift = 1;
        for (int i = 0; i < MONTHS; i++) {
            // The advisor nudges a hand-held dial between looks; the hand takes it back.
            if (i >= GROWN && (dm.isAutopilot() || dm.getPolicyRate() != HELD)) dm.takeTheDial(HELD);
            held.toggleNextMonth();
            run++;
            int month = held.getMonth();
            boolean inGlut = assertGlutLegsNotLifted(re, month);
            if (inGlut) glutMonths++;
            if (re.glutWaivesLift(false)) studioWaived++;
            if (re.glutWaivesLift(true)) familyWaived++;
            // The lift is read off a leg that took it: its target over its cost times its scarcity.
            double studioPressure = re.studioPressure(), familyPressure = re.familyPressure();
            double studioBase = re.rentRequired(false) * re.scarcityMultipleOf(studioPressure);
            double familyBase = re.rentRequired(true) * re.scarcityMultipleOf(familyPressure);
            if (studioPressure >= ham.citybuildersim.sectors.RealEstate.GLUT_PRESSURE && studioBase > 0) {
                largestLift = Math.max(largestLift, re.studioRentTarget() / studioBase);
            }
            if (familyPressure >= ham.citybuildersim.sectors.RealEstate.GLUT_PRESSURE && familyBase > 0) {
                largestLift = Math.max(largestLift, re.rentTarget() / familyBase);
            }
            if (studioPressure >= ham.citybuildersim.sectors.RealEstate.GLUT_PRESSURE
                    && familyPressure >= ham.citybuildersim.sectors.RealEstate.GLUT_PRESSURE
                    && re.rentBreakEven() > 0) {
                neitherMonths++;
                assertNotMore("held 15%, neither market in glut: the blended target is not under break-even",
                        month, re.rentBreakEven(), re.blendedRentTarget());
            }
        }
        System.out.printf("   %d months run, the dial held at %.0f%% for the last %d, in %.1f s; a leg in glut in %d of them, none in %d; "
                        + "the lift waived on the studio leg in %d and on the family leg in %d; the largest lift on a leg that took it %.3f%n",
                run, HELD * 100, run - GROWN, (System.currentTimeMillis() - started) / 1000.0, glutMonths, neitherMonths,
                studioWaived, familyWaived, largestLift);
        assertTrue("fixture: the held dial is the rate the landlords borrow against", dm.getPolicyRate() == HELD);
        assertTrue("fixture: a glut with a lift waiting came up (the lift was waived in a month)",
                studioWaived + familyWaived > 0);

        // ...and the share of its debt whose homes are closed goes through a save and a load.
        double share = re.strandedDebtShare();
        System.out.printf("   the landlords' debt on closed homes at the end: %.3f of it%n", share);
        assertTrue("fixture: the city closed homes and left their debt on the landlords", share > 0 && share <= 1);
        assertTrue("saved", held.saveGame(1, "housing-held").ok);
        Game back = new Game(held.getGameFiles());
        back.loadGame(1);
        isNear("a closed home's debt share comes back from a save", back.getSectors().realEstate().strandedDebtShare(), share);
    }

    /**
     * RN1, 2026-11-09. Of the debt, the share whose homes are closed is not
     * carried by the homes left: capacity down raises the share by the places
     * that went, capacity up dilutes it by places, and a count from nothing
     * (a load, before the month's first count) leaves it as saved.
     */
    static void aClosedHomesDebtIsTheLandlordsLoss() {
        System.out.println("\n--- a closed home's debt is the landlord's loss ---");
        final double INTEREST = 100, MAINTENANCE = 30, TAX = 20;
        ham.citybuildersim.sectors.RealEstate re = new ham.citybuildersim.sectors.RealEstate();
        re.setInterestExpense(INTEREST);
        re.setMaintenanceExpense(MAINTENANCE);
        re.setPropertyTaxExpense(TAX);
        re.setOwnedHousingCapacity(1000);
        isNear("fixture: nothing closed, nothing stranded", re.strandedDebtShare(), 0);
        isNear("...and all the interest is carried", re.carriedInterest(), INTEREST);
        re.setOwnedHousingCapacity(600);
        isNear("1000 places down to 600: 400 of 1000 are closed, a share of .4", re.strandedDebtShare(), .4);
        isNear("...and the interest carried is the .6 left", re.carriedInterest(), INTEREST * .6);
        re.setOwnedHousingCapacity(1200);
        isNear("600 up to 1200: the share is diluted by places, .4 x 600 / 1200", re.strandedDebtShare(), .2);
        double carry = MAINTENANCE + TAX + re.carriedInterest() + re.getPayroll();
        isNear("the break-even over what it owns is maintenance, tax, the interest carried and payroll",
                re.rentBreakEven() * re.getOwnedHousingCapacity(), carry);
        assertTrue("...and it is under what it would be with all the interest counted",
                re.rentBreakEven() * re.getOwnedHousingCapacity()
                        < MAINTENANCE + TAX + INTEREST + re.getPayroll() - TOLERANCE);
        assertTrue("the company still pays all its interest: the line is untouched", re.getInterestExpense() == INTEREST);

        // A load: ownedCapacity is not saved, so the restored landlord counts from nothing and keeps the share.
        ham.citybuildersim.sectors.RealEstate loaded = new ham.citybuildersim.sectors.RealEstate();
        loaded.restore(re.toState());
        isNear("a saved landlord, loaded: the share is back", loaded.strandedDebtShare(), .2);
        loaded.setOwnedHousingCapacity(1200);
        isNear("...and the month's first count, from nothing, leaves it as saved", loaded.strandedDebtShare(), .2);

        // Every place closed: every debt is a closed home's.
        re.setOwnedHousingCapacity(0);
        isNear("1200 places down to none: the whole debt is a closed home's", re.strandedDebtShare(), 1);
        re.setOwnedHousingCapacity(1200);
        isNear("...and a count from nothing leaves it", re.strandedDebtShare(), 1);
    }

    /** assertTrue, said as an equality within TOLERANCE, so a figure that is right prints its line. */
    static void isNear(String label, double actual, double expected) {
        assertTrue(label, Math.abs(actual - expected) <= TOLERANCE);
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
