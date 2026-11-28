package ham.citybuildersim;

import ham.citybuildersim.sectors.Retail;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A town with no shop goes hungry, the screens say so, and the price index does not wait for the shop (HG1).
 *
 * Jerus's 1900 city (0.9.219) had no shop in 79 months and went 97.5% hungry: its posts outran its people from the founding,
 * the 1900 arrival mix holds few diplomas, and the diploma band was over-posted every month, so the spare-worker test refused
 * every shop format and nothing in the hunger moved it. The header said "Nothing needs you", and the price index never based
 * (no month had both rent and groceries), so the policy rule read no inflation for ever.
 *
 * WHAT THIS HAS TO PROVE, in the order of the sections; each fixture CAUSES its condition (a city founded the 1900 way through
 * newGame, a bare PriceIndex fed by hand) and asserts against the model's own constants:
 *
 *   1. THE TRAP, CAUSED. A city founded the 1900 way has no Retail building and an over-posted diploma band, so the ordinary
 *      gate (another sector's staffing() of the same template) refuses a Convenience Store; while the town goes short of baskets
 *      Retail's gate passes it on the share the city's fill would staff (Sector.shareAtTheFill()) and its planner orders one.
 *      BOTH SIDES: a Filling Station (coverage 0) is not lifted, and once the shops cover the households' need Retail's
 *      reading of the Convenience Store is the ordinary one again.
 *
 *   2. THE TRAP PLAYED. From such a city, within PLAYED_BOUND months a shop stands, its shelf holds stock, baskets are sold,
 *      and the households short of stock fall under CityNeeds.HUNGER_YELLOW.
 *
 *   3. THE SCREENS. At red hunger from an empty shelf CityNeeds.food() is level 2, FOOD, door SHOPS, and NEEDS YOU lists it
 *      (so the header cannot say "Nothing needs you"); with the people priced out the door is FOOD_PROMISE; a fed city reads
 *      level 0 and lists no FOOD row; and the row follows the thresholds in every month any city here played.
 *
 *   4. THE INDEX. A basket with rent and a good bought beside it (meals, luxury, cars, fuels) bases at the SETTLING_MONTHS-th
 *      month with no groceries in it; rent alone, the city's own fees and a half that is priced or bought but not both never
 *      base it (BOTH HALVES); groceries join a year after they appear, with no jump in the level; the rule then reads a
 *      real rate; and a basket with groceries from the start bases and never relinks, as before.
 *
 *   5. THE MONEY AUDIT is exact to the cent in every month section 2's city played (and in the months of the others).
 *
 * Every city lives in GameFiles.scratch(); none touches a save slot.
 */
public class HungerCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) { check(label, ok, null); }

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-112s %s%s%n", label, ok ? "OK" : "FAIL", detail == null ? "" : "  " + detail);
    }

    /** A figure against the harness's own, to an absolute tolerance. */
    static void near(String label, double actual, double expected, double tol) {
        check(label, Math.abs(actual - expected) <= tol, String.format("%.12g against %.12g", actual, expected));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /* ---------------------------------- the fixtures' own figures ---------------------------------- */

    static final Founding THE_1900_WAY = Founding.defaults().withUnfitted(true);
    static final String CONVENIENCE = "Convenience Store", STATION = "Filling Station";
    static final int DIPLOMA = WageBand.DIPLOMA.ordinal();

    /** The most months a city founded the 1900 way is played for the first month the city's fill would staff a shop. */
    static final int HUNT_BOUND = 36;
    /** The months section 2 gives the town to stand a shop, stock it, sell baskets and be fed. */
    static final int PLAYED_BOUND = 12;
    /** The most Convenience Stores section 1 stands to cover the households' need. */
    static final int COVER_BOUND = 50;
    /** The most months a default city is played for a month with a shop selling and nobody hungry. */
    static final int FED_BOUND = 40;
    /** The shelf price section 3 multiplies, and the months it plays for the people to be priced out. */
    static final double PRICE_UP = 20;
    static final int PRICED_MONTHS = 2;

    /** A new city founded as the player's door founds it (newGame(founding)), on a scratch folder, quietly. */
    static Game found(String label, Founding founding) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> g.newGame(founding));
        return g;
    }

    /** What the money audit said of every month played through play(): the worst residual over its tolerance, and the months that broke it. */
    static final class Audit {
        int months, missing, broken;
        double worst;
        String first;

        void take(Game g) {
            months++;
            MoneyAudit.Result a = g.getLastMoneyAudit();
            if (a == null) { missing++; return; }
            double tolerance = MoneyAudit.tolerance(MoneyAudit.CENT, a.moved());
            double ratio = Math.abs(a.residual) / tolerance;
            worst = Math.max(worst, ratio);
            if (!(Math.abs(a.residual) <= tolerance)) {
                broken++;
                if (first == null) first = "month " + g.getMonth() + " residual " + a.residual + " against " + tolerance;
            }
        }
    }

    /** Every month played through play(): CityNeeds.food() against the thresholds and the doors, and which levels and doors were met. */
    static final class FoodWatch {
        final int[] levels = new int[3];
        int shops, promises, months;
        boolean follows = true;
        String first;

        void look(Game g) {
            months++;
            HouseholdBalance h = g.getHouseholdBalance();
            double hungry = h.getHungerRate();
            CityNeeds.Need food = CityNeeds.food(g);
            int level = hungry >= CityNeeds.HUNGER_RED ? 2 : hungry >= CityNeeds.HUNGER_YELLOW ? 1 : 0;
            boolean shelves = h.getHungerShortOfStock() >= h.getHungerPricedOut();
            CityNeeds.Go door = shelves ? CityNeeds.Go.SHOPS : CityNeeds.Go.FOOD_PROMISE;
            boolean listed = hasFood(CityNeeds.needsYou(g));
            boolean ok = food.kind() == CityNeeds.Kind.FOOD && food.level() == level && food.go() == door && listed == (level > 0);
            levels[level]++;
            if (shelves) shops++; else promises++;
            if (!ok) {
                follows = false;
                if (first == null) first = "month " + g.getMonth() + " hunger " + hungry + " level " + food.level() + " door " + food.go() + " listed " + listed;
            }
        }
    }

    static boolean hasFood(List<CityNeeds.Need> needs) {
        for (CityNeeds.Need n : needs) if (n.kind() == CityNeeds.Kind.FOOD) return true;
        return false;
    }

    /** One month of the clock, as the player's button makes it, and what is watched of it. */
    static void play(Game g, Audit audit, FoodWatch watch) {
        quietly(g::toggleNextMonth);
        if (audit != null) audit.take(g);
        if (watch != null) watch.look(g);
    }

    /** The buildings Retail owns, standing and on site. */
    static long retailBuildings(Game g) {
        long n = 0;
        for (BuildingsStacks s : g.getBuildingManager().getSectorStacks(g.getSectors().retail().key())) n += s.getQuantity() + s.getUnderConstruction();
        return n;
    }

    /**
     * A city founded the 1900 way and played, month by month, up to the first month Retail's gate would pass a Convenience Store:
     * the founding has Retail no building and its posts outrun its people, so nothing but the clock is waited for, and a shop is
     * ordered in the month after (the planner is not asked until the caller does). months says how long it took; HUNT_BOUND stops it.
     */
    static final class Trap {
        final Game game;
        final Retail retail;
        final BuildingsTemplate convenience;
        int months;

        Trap(String label, Audit audit, FoodWatch watch) {
            game = found(label, THE_1900_WAY);
            retail = game.getSectors().retail();
            convenience = game.getBuildingManager().getTemplateByName(CONVENIENCE);
            while (months < HUNT_BOUND && !retail.staffing(convenience).passes()) {
                play(game, audit, watch);
                months++;
            }
        }
    }

    static final Audit playedAudit = new Audit(), otherAudit = new Audit();
    static final FoodWatch watch = new FoodWatch();
    /** Section 2's city, once fed: section 3 prices its people out. */
    static Trap played;

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());

        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();

        out.println(fails == 0 ? "\nAll HungerCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. the trap, caused ============================ */

    static void sectionOne() {
        out.println("--- 1. the trap, caused: a shop the town goes without is staffed at the fill, and no other building is ---");
        Trap trap = new Trap("hunger-trap", otherAudit, watch);
        Game g = trap.game;
        Retail retail = trap.retail;
        BuildingManager bm = g.getBuildingManager();
        PopulationManager people = g.getPopulationManager();
        Sector ordinary = g.getSectors().restaurants();   // a sector whose staffedAtTheFill() is false, asked of the same template
        Sector asked = retail;
        BuildingsTemplate conv = trap.convenience, station = bm.getTemplateByName(STATION);
        check("fixture: the city is founded the 1900 way and the catalogue has a Convenience Store and a Filling Station",
                g.foundedUnfitted() && conv != null && station != null);
        check("fixture: the Convenience Store is a shop (coverage) and the Filling Station is not", conv.getCoverage() > 0 && station.getCoverage() <= 0);
        check("fixture: Retail owns no building, standing or on site", retailBuildings(g) == 0, retailBuildings(g) + " of them");

        Sector.Staffing plain = ordinary.staffing(conv);
        Sector.Staffing lifted = retail.staffing(conv);
        double[] spare = lifted.spare();
        out.printf("   month %d (%d played); the diploma band's spare %.2f, its workers %.2f for %.2f posts%n", g.getMonth(), trap.months,
                spare[DIPLOMA], people.workforceByBand()[DIPLOMA], people.staffablePostsByBand()[DIPLOMA]);
        check("premise: the diploma band is over-posted (its spare is negative: the 1900 founding's own posts outrun its workers)", spare[DIPLOMA] < 0, String.valueOf(spare[DIPLOMA]));
        check("premise: the ordinary gate refuses the Convenience Store (no unfillable band, but under MIN_STAFFABLE_TO_ORDER of its posts)",
                !plain.passes() && plain.unfillable == null && plain.share < Sector.MIN_STAFFABLE_TO_ORDER,
                String.format("share %.3f against %.2f", plain.share, Sector.MIN_STAFFABLE_TO_ORDER));
        check("the first month the town's fill could staff it came within HUNT_BOUND months of the founding", trap.months < HUNT_BOUND, trap.months + " months");

        // the mend
        check("the town goes short of baskets: the shops it has hand over fewer than the households need", retail.townGoesShort());
        check("Retail's gate staffs a shop the town goes without at the fill", asked.staffedAtTheFill(conv));
        check("...so Retail's gate passes the Convenience Store, with no band nobody could fill", lifted.passes() && lifted.unfillable == null);
        check("...at a share of its posts that reaches MIN_STAFFABLE_TO_ORDER", lifted.share >= Sector.MIN_STAFFABLE_TO_ORDER, String.valueOf(lifted.share));
        check("...which is more than the ordinary reading of the same posts", lifted.share > plain.share, lifted.share + " against " + plain.share);
        double total = 0;
        for (double w : lifted.wanted()) total += w;
        double atTheFill = Sector.shareAtTheFill(people, people.staffablePostsByBand(), lifted.wanted(), total);
        near("...and is the larger of the ordinary share and the share the fill staffs, recomputed from the same posts",
                lifted.share, Math.max(plain.share, atTheFill), 1e-9);
        check("...and a planner's order of one passes the gate", retail.staffableCount(conv, 1) >= 1);
        BusinessInvestment.Decision order = retail.plan(g.getBusinessInvestment(), g);
        out.println("   plan: " + order.reason);
        check("Retail's planner orders a shop (a template with coverage), and the staffing gate holds nothing back",
                order.build && order.template != null && order.template.getCoverage() > 0 && order.heldForStaff == null,
                order.template == null ? "no template" : order.template.getName() + " x" + order.quantity);

        // both sides: nothing but a shop the town goes without is lifted
        check("BOTH SIDES: Retail does not stage a Filling Station (coverage 0) at the fill", !asked.staffedAtTheFill(station));
        Sector.Staffing stationPlain = ordinary.staffing(station), stationRetail = retail.staffing(station);
        near("...so its share is the ordinary reading's", stationRetail.share, stationPlain.share, 1e-12);
        check("...and so is its unfillable band", stationRetail.unfillable == stationPlain.unfillable);

        // once the shops cover the need
        long added = 0;
        while (retail.townGoesShort() && added < COVER_BOUND) {
            bm.addStack(conv, 1, true);
            added++;
        }
        people.updateJobs(g.getJobs());
        double needed = g.getHouseholdBalance().groceriesNeeded();
        out.printf("   %d Convenience Stores stand: coverage %d for %.1f baskets needed%n", added, retail.getStoreCoverage(), needed);
        check("fixture: standing Convenience Stores cover the households' need (the shops hand over what the households need)",
                added > 0 && added < COVER_BOUND && !retail.townGoesShort() && retail.getStoreCoverage() * retail.getOperatingRate() >= needed,
                added + " stores");
        check("BOTH SIDES: Retail then does not stage a Convenience Store at the fill", !asked.staffedAtTheFill(conv));
        Sector.Staffing covered = retail.staffing(conv), coveredPlain = ordinary.staffing(conv);
        near("...and its share of the Convenience Store is the ordinary reading again", covered.share, coveredPlain.share, 1e-12);
        check("...and the planner orders no more shops while they cover the town", !retail.plan(g.getBusinessInvestment(), g).build);
    }

    /* ============================ 2. the trap played ============================ */

    static void sectionTwo() {
        out.println("\n--- 2. the trap played: a shop stands, is stocked, sells, and the shelves stop leaving the town hungry ---");
        Trap trap = new Trap("hunger-played", playedAudit, watch);
        Game g = trap.game;
        Retail retail = trap.retail;
        HouseholdBalance h = g.getHouseholdBalance();
        check("fixture: the trapped city has no shop and its shelf is empty before the first month played",
                retailBuildings(g) == 0 && retail.getStoreInventory() == 0 && retail.getLastMonthSales() == 0);
        // the trap's own premise again, so that a city only the clock freed is no trapped city: the diploma band is over-posted and
        // the ordinary gate (another sector's staffing() of the same template) still refuses the shop, in the month the played run starts
        Sector.Staffing plain = g.getSectors().restaurants().staffing(trap.convenience);
        double diplomaSpare = retail.staffing(trap.convenience).spare()[DIPLOMA];
        check("premise: the diploma band is over-posted and the ordinary gate refuses the Convenience Store, the shops' gate alone passing it",
                diplomaSpare < 0 && !plain.passes() && retail.staffing(trap.convenience).passes() && trap.months < HUNT_BOUND,
                String.format("diploma spare %.2f, ordinary share %.3f, the %d-th month", diplomaSpare, plain.share, trap.months));
        check("premise: the shelves leave the town short (more than HUNGER_YELLOW of it)", h.getHungerShortOfStock() >= CityNeeds.HUNGER_YELLOW,
                String.valueOf(h.getHungerShortOfStock()));

        int ordered = -1, stands = -1, stocked = -1, sold = -1, fed = -1;
        for (int i = 1; i <= PLAYED_BOUND; i++) {
            play(g, playedAudit, watch);
            if (ordered < 0 && retail.storeCoverageOnSite() > 0) ordered = i;
            if (stands < 0 && retail.getStoreCoverage() > 0) stands = i;
            if (stocked < 0 && retail.getStoreInventory() > 0) stocked = i;
            if (sold < 0 && retail.getLastMonthSales() > 0) sold = i;
            if (fed < 0 && h.getHungerShortOfStock() < CityNeeds.HUNGER_YELLOW) fed = i;
        }
        out.printf("   from the trapped month %d (%d played to it): a shop ordered in month +%d, standing +%d, stocked +%d, sold +%d, shelves no longer short +%d%n",
                g.getMonth() - PLAYED_BOUND, trap.months, ordered, stands, stocked, sold, fed);
        check("Retail ordered a shop within " + PLAYED_BOUND + " months", ordered > 0);
        check("a Retail shop stands within " + PLAYED_BOUND + " months (coverage above nothing)", stands > 0 && retail.getStoreCoverage() > 0, "month +" + stands);
        check("the shelf holds stock within " + PLAYED_BOUND + " months", stocked > 0, "month +" + stocked);
        check("baskets are sold within " + PLAYED_BOUND + " months", sold > 0, "month +" + sold);
        check("the households short of stock fall under HUNGER_YELLOW within " + PLAYED_BOUND + " months", fed > 0, "month +" + fed);
        check("...and are still under it at the bound", h.getHungerShortOfStock() < CityNeeds.HUNGER_YELLOW, String.valueOf(h.getHungerShortOfStock()));
        check("once the shop stands the town no longer goes short of baskets", !retail.townGoesShort());
        played = trap;
    }

    /* ============================ 3. the screens ============================ */

    static void sectionThree() {
        out.println("\n--- 3. the screens: hunger reaches FOOD on NEEDS YOU, at the door that mends it ---");
        // the empty shelf
        Trap trap = new Trap("hunger-screen", otherAudit, watch);
        Game g = trap.game;
        Retail retail = trap.retail;
        HouseholdBalance h = g.getHouseholdBalance();
        check("fixture: no shop stands and the shelf is empty", retail.getStoreCoverage() == 0 && retail.getStoreInventory() == 0);
        check("premise: the town goes hungry at the red line, from the empty shelf (more of it short of stock than priced out)",
                h.getHungerRate() >= CityNeeds.HUNGER_RED && h.getHungerShortOfStock() >= h.getHungerPricedOut(),
                String.format("hunger %.3f, short of stock %.3f, priced out %.3f", h.getHungerRate(), h.getHungerShortOfStock(), h.getHungerPricedOut()));
        CityNeeds.Need food = CityNeeds.food(g);
        check("FOOD reads level 2 (red) at red hunger", food.level() == 2 && food.kind() == CityNeeds.Kind.FOOD, food.reading());
        check("...and its door is the shops (Build > Shops), the shelves being the larger half", food.go() == CityNeeds.Go.SHOPS);
        List<CityNeeds.Need> needs = CityNeeds.needsYou(g);
        check("NEEDS YOU lists a FOOD row, so the header cannot say \"Nothing needs you\"", hasFood(needs) && !needs.isEmpty(), needs.size() + " rows");
        CityNeeds.Need row = rowOf(CityNeeds.measure(g, CityNeeds.PLAIN));
        check("...and measure() lists it among every row, at level 2", row != null && row.level() == 2);

        // the other door: the people priced out of a basket
        Trap fedTown = played;
        Game p = fedTown.game;
        Retail shop = fedTown.retail;
        HouseholdBalance ph = p.getHouseholdBalance();
        double pricedBefore = ph.getHungerPricedOut();
        shop.setStoreSellPrice(shop.getStoreSellPrice() * PRICE_UP);
        for (int i = 0; i < PRICED_MONTHS; i++) play(p, otherAudit, watch);
        out.printf("   the shelf price multiplied by %.0f, %d months on: hunger %.3f, priced out %.3f (was %.3f), short of stock %.3f%n",
                PRICE_UP, PRICED_MONTHS, ph.getHungerRate(), ph.getHungerPricedOut(), pricedBefore, ph.getHungerShortOfStock());
        check("premise: the dearer shelf priced more of the town out of a basket than before", ph.getHungerPricedOut() > pricedBefore);
        check("premise: more of the town is priced out than short of stock, and it is at the red line",
                ph.getHungerPricedOut() > ph.getHungerShortOfStock() && ph.getHungerRate() >= CityNeeds.HUNGER_RED);
        CityNeeds.Need priced = CityNeeds.food(p);
        check("FOOD reads level 2 there too, and its door is the food promise (Policy > Promises > Food)",
                priced.level() == 2 && priced.kind() == CityNeeds.Kind.FOOD && priced.go() == CityNeeds.Go.FOOD_PROMISE, priced.reading());
        check("...and NEEDS YOU lists it", hasFood(CityNeeds.needsYou(p)));

        // the fed side
        Game d = found("hunger-fed", Founding.defaults());
        Retail dr = d.getSectors().retail();
        HouseholdBalance dh = d.getHouseholdBalance();
        int months = 0;
        while (months < FED_BOUND && !(dr.getLastMonthSales() > 0 && dh.getHungerRate() < CityNeeds.HUNGER_YELLOW)) {
            play(d, otherAudit, watch);
            months++;
        }
        out.printf("   a city founded the default way, month %d: a shop sells %d baskets, hunger %.3f%n", d.getMonth(), dr.getLastMonthSales(), dh.getHungerRate());
        check("fixture: a shop stands and sells, and under HUNGER_YELLOW of the town goes hungry within FED_BOUND months",
                dr.getStoreCoverage() > 0 && dr.getLastMonthSales() > 0 && dh.getHungerRate() < CityNeeds.HUNGER_YELLOW && months < FED_BOUND,
                months + " months");
        CityNeeds.Need fedFood = CityNeeds.food(d);
        check("FOOD reads level 0 in a fed city", fedFood.level() == 0 && fedFood.kind() == CityNeeds.Kind.FOOD, fedFood.reading());
        check("...and NEEDS YOU has no FOOD row, while measure() still lists the row", !hasFood(CityNeeds.needsYou(d)) && rowOf(CityNeeds.measure(d, CityNeeds.PLAIN)) != null);

        // and every month any of them played
        out.printf("   %d months watched: level 0 in %d, level 1 in %d, level 2 in %d; the shops' door in %d, the food promise's in %d%n",
                watch.months, watch.levels[0], watch.levels[1], watch.levels[2], watch.shops, watch.promises);
        check("in every month played the FOOD row followed the thresholds (level 1 at HUNGER_YELLOW, 2 at HUNGER_RED), the door and NEEDS YOU's list",
                watch.follows, watch.first);
        check("...and all three levels and both doors were met among them", watch.levels[0] > 0 && watch.levels[1] > 0 && watch.levels[2] > 0
                && watch.shops > 0 && watch.promises > 0);
    }

    static CityNeeds.Need rowOf(List<CityNeeds.Need> all) {
        for (CityNeeds.Need n : all) if (n.kind() == CityNeeds.Kind.FOOD) return n;
        return null;
    }

    /* ============================ 4. the index ============================ */

    static final int LEAD = 5;
    static final double RENT_PRICE = 100, RENT_SPEND = 1000, MEAL_PRICE = 20, MEAL_SPEND = 1000, SHELF_PRICE = 2, SHELF_SPEND = 1000;
    static final double GOOD_PRICE = 50, GOOD_SPEND = 1000, FEE_PRICE = 10, FEE_SPEND = 100;
    static final double RENT_DRIFT = .005, MEAL_DRIFT = .003, MEAL_RISE = .01, SHELF_STEP = .10;

    static double[] prices(double groceries, double rent, double meals) {
        double[] p = new double[PriceIndex.COMPONENTS];
        p[PriceIndex.GROCERIES] = groceries;
        p[PriceIndex.RENT] = rent;
        p[PriceIndex.MEALS] = meals;
        return p;
    }

    static double[] spends(double groceries, double rent, double meals) { return prices(groceries, rent, meals); }

    static void take(PriceIndex px, int month, double[] prices, double[] spends) {
        px.takeMonth(prices, spends, new double[PriceIndex.FEE_LINES], new double[PriceIndex.FEE_LINES], month);
    }

    static double[] filled(int n, double value) {
        double[] a = new double[n];
        Arrays.fill(a, value);
        return a;
    }

    static void sectionFour() {
        out.println("\n--- 4. the index: a basket with no groceries bases on what the city buys, and groceries join it ---");
        basesWithoutGroceries();
        neverBasesOnHalf();
        groceriesJoin();
        theRateIsReal();
        groceriesFromTheStart();
    }

    /** (a) rent and a good bought beside it base the basket at the SETTLING_MONTHS-th such month, groceries absent. */
    static void basesWithoutGroceries() {
        for (int opening = 0; opening < 2; opening++) {
            double shelf = opening == 0 ? 0 : SHELF_PRICE;
            String why = opening == 0 ? "no shelf price" : "the opening shelf price held, nothing bought";
            PriceIndex px = new PriceIndex();
            boolean idle = true;
            for (int m = 1; m <= LEAD; m++) {
                take(px, m, prices(shelf, RENT_PRICE, 0), spends(0, RENT_SPEND, 0));
                idle &= !px.isBased();
            }
            check("(a) " + LEAD + " months of rent alone base nothing, " + why, idle);
            int basedAt = -1;
            boolean counts = true;
            for (int c = 1; c <= PriceIndex.SETTLING_MONTHS + 3; c++) {
                take(px, LEAD + c, prices(shelf, RENT_PRICE, MEAL_PRICE), spends(0, RENT_SPEND, MEAL_SPEND));
                if (px.isBased() && basedAt < 0) basedAt = c;
                if (!px.isBased()) counts &= px.monthsUntilRate() == PriceIndex.SETTLING_MONTHS - c + PriceIndex.WINDOW;
            }
            check("(a) rent and meals bought base the index at exactly the SETTLING_MONTHS-th such month, never before, " + why,
                    basedAt == PriceIndex.SETTLING_MONTHS, "based at the " + basedAt + "-th of " + PriceIndex.SETTLING_MONTHS);
            check("(a) ...and the header's months until a rate count down from SETTLING_MONTHS + WINDOW instead of standing still", counts);
            check("(a) ...the basket holding rent and meals and no groceries, " + why,
                    px.getWeight(PriceIndex.RENT) > 0 && px.getWeight(PriceIndex.MEALS) > 0 && px.getWeight(PriceIndex.GROCERIES) == 0);
        }
        int[] goods = { PriceIndex.MEALS, PriceIndex.LUXURY, PriceIndex.NEW_CARS, PriceIndex.PETROL, PriceIndex.LAMP_OIL, PriceIndex.HOME_COAL };
        for (int k : goods) {
            PriceIndex px = new PriceIndex();
            double[] p = prices(0, RENT_PRICE, 0), s = spends(0, RENT_SPEND, 0);
            p[k] = GOOD_PRICE;
            s[k] = GOOD_SPEND;
            int basedAt = -1;
            for (int c = 1; c <= PriceIndex.SETTLING_MONTHS + 2; c++) {
                take(px, c, p, s);
                if (px.isBased() && basedAt < 0) basedAt = c;
            }
            check("(a) rent and " + PriceIndex.COMPONENT_NAMES[k] + " bought base the index at the SETTLING_MONTHS-th month",
                    basedAt == PriceIndex.SETTLING_MONTHS && px.getWeight(PriceIndex.GROCERIES) == 0, "based at " + basedAt);
        }
    }

    /** Whether the index never bases in three settlings of the same month fed to it. */
    static boolean neverBased(double[] prices, double[] spends, double[] fees, double[] feeSpends) {
        PriceIndex px = new PriceIndex();
        for (int m = 1; m <= 3 * PriceIndex.SETTLING_MONTHS; m++) {
            px.takeMonth(prices, spends, fees, feeSpends, m);
            if (px.isBased()) return false;
        }
        return !px.hasRate();
    }

    /** (b) BOTH HALVES: rent beside nothing from outside the loop never bases the basket. */
    static void neverBasesOnHalf() {
        double[] none = new double[PriceIndex.FEE_LINES];
        double[] fees = filled(PriceIndex.FEE_LINES, FEE_PRICE), feeSpends = filled(PriceIndex.FEE_LINES, FEE_SPEND);
        String months = "in " + 3 * PriceIndex.SETTLING_MONTHS + " months";
        check("(b) rent alone never bases the index " + months, neverBased(prices(0, RENT_PRICE, 0), spends(0, RENT_SPEND, 0), none, none));
        check("(b) rent and the city's own fees (SERVICES) never base it " + months, neverBased(prices(0, RENT_PRICE, 0), spends(0, RENT_SPEND, 0), fees, feeSpends));
        double[] p = prices(0, RENT_PRICE, 0), s = spends(0, RENT_SPEND, 0);
        p[PriceIndex.SERVICES] = FEE_PRICE;
        s[PriceIndex.SERVICES] = FEE_SPEND;
        check("(b) ...nor with SERVICES given in the price and spend arrays too", neverBased(p, s, fees, feeSpends));
        check("(b) rent and a shelf price nobody bought at never base it", neverBased(prices(SHELF_PRICE, RENT_PRICE, 0), spends(0, RENT_SPEND, 0), none, none));
        check("(b) rent and groceries bought at no price never base it", neverBased(prices(0, RENT_PRICE, 0), spends(SHELF_SPEND, RENT_SPEND, 0), none, none));
        check("(b) rent and a meal priced and not bought never base it", neverBased(prices(0, RENT_PRICE, MEAL_PRICE), spends(0, RENT_SPEND, 0), none, none));
        check("(b) rent and meals bought at no price never base it", neverBased(prices(0, RENT_PRICE, 0), spends(0, RENT_SPEND, MEAL_SPEND), none, none));
        check("(b) groceries and meals bought and priced, with no rent priced, never base it",
                neverBased(prices(SHELF_PRICE, 0, MEAL_PRICE), spends(SHELF_SPEND, RENT_SPEND, MEAL_SPEND), none, none));
        check("(b) ...nor with a rent priced and none paid", neverBased(prices(SHELF_PRICE, RENT_PRICE, MEAL_PRICE), spends(SHELF_SPEND, 0, MEAL_SPEND), none, none));
    }

    static double driftRent(int month) { return RENT_PRICE * Math.pow(1 + RENT_DRIFT, month); }

    static double driftMeal(int month) { return MEAL_PRICE * Math.pow(1 + MEAL_DRIFT, month); }

    /** (c) groceries appearing after a basing with none join it once a whole year of them is in the weights, with the level running on. */
    static void groceriesJoin() {
        PriceIndex joined = new PriceIndex(), held = new PriceIndex(), never = new PriceIndex(), flat = new PriceIndex();
        int month = 0;
        for (int c = 1; c <= PriceIndex.SETTLING_MONTHS; c++) {
            month++;
            double[] p = prices(0, driftRent(month), driftMeal(month)), s = spends(0, RENT_SPEND, MEAL_SPEND);
            take(joined, month, p, s);
            take(held, month, p, s);
            take(never, month, p, s);
            take(flat, month, prices(0, RENT_PRICE, MEAL_PRICE), s);
        }
        check("(c) after the basing with no groceries their weight is nothing", joined.isBased() && joined.getWeight(PriceIndex.GROCERIES) == 0);

        boolean zeroBefore = true;
        double worst = 0, flatBefore = 0, flatAtLink = 0;
        int linkedAt = -1;
        for (int k = 1; k <= PriceIndex.WEIGHT_MONTHS; k++) {
            month++;
            double rent = driftRent(month), meal = driftMeal(month);
            double[] p = prices(SHELF_PRICE, rent, meal), s = spends(SHELF_SPEND, RENT_SPEND, MEAL_SPEND);
            take(joined, month, p, s);
            take(held, month, p, s);
            take(never, month, prices(0, rent, meal), spends(0, RENT_SPEND, MEAL_SPEND));
            take(flat, month, prices(SHELF_PRICE, RENT_PRICE, MEAL_PRICE), s);
            if (k < PriceIndex.WEIGHT_MONTHS) zeroBefore &= joined.getWeight(PriceIndex.GROCERIES) == 0 && flat.getWeight(PriceIndex.GROCERIES) == 0;
            worst = Math.max(worst, Math.abs(joined.getIndex() - never.getIndex()));
            if (k == PriceIndex.WEIGHT_MONTHS - 1) flatBefore = flat.getIndex();
            if (k == PriceIndex.WEIGHT_MONTHS) { flatAtLink = flat.getIndex(); linkedAt = month; }
        }
        check("(c) groceries bought for WEIGHT_MONTHS-1 months leave their weight at nothing", zeroBefore);
        check("(c) ...and at the WEIGHT_MONTHS-th month they join the basket", joined.getWeight(PriceIndex.GROCERIES) > 0 && joined.getLinkedMonth() == linkedAt,
                "weight " + joined.getWeight(PriceIndex.GROCERIES) + ", linked in month " + joined.getLinkedMonth());
        near("(c) ...at the share of the trailing year's spending they were bought for", joined.getWeight(PriceIndex.GROCERIES),
                SHELF_SPEND / (SHELF_SPEND + RENT_SPEND + MEAL_SPEND), 1e-12);
        check("(c) ...with rent and meals still in it", joined.getWeight(PriceIndex.RENT) > 0 && joined.getWeight(PriceIndex.MEALS) > 0);
        near("(c) the level does not jump across the link: prices held flat, the month before and the month of the link", flatAtLink, flatBefore, 1e-12);
        check("(c) ...and with rent and meals drifting, the level stays that of a basket groceries never joined in every month to the link, the link's included",
                worst <= 1e-12, "worst gap " + worst);

        // from the next month groceries move the index at their weight
        month++;
        double rent = driftRent(month), meal = driftMeal(month);
        take(joined, month, prices(SHELF_PRICE * (1 + SHELF_STEP), rent, meal), spends(SHELF_SPEND, RENT_SPEND, MEAL_SPEND));
        take(held, month, prices(SHELF_PRICE, rent, meal), spends(SHELF_SPEND, RENT_SPEND, MEAL_SPEND));
        near("(c) once joined a dearer shelf moves the index by its weight times the rise, from the level the basket was linked at",
                joined.getIndex() - held.getIndex(), joined.getLink() * joined.getWeight(PriceIndex.GROCERIES) * SHELF_STEP, 1e-9);

        // a month with a price and no sale inside the year holds the join off
        PriceIndex gapped = new PriceIndex();
        int m = 0;
        for (int c = 1; c <= PriceIndex.SETTLING_MONTHS; c++) take(gapped, ++m, prices(0, RENT_PRICE, MEAL_PRICE), spends(0, RENT_SPEND, MEAL_SPEND));
        boolean held0 = true;
        for (int k = 1; k <= 2 * PriceIndex.WEIGHT_MONTHS; k++) {
            boolean sold = k != PriceIndex.WEIGHT_MONTHS;   // the WEIGHT_MONTHS-th month has the price and no sale
            take(gapped, ++m, prices(SHELF_PRICE, RENT_PRICE, MEAL_PRICE), spends(sold ? SHELF_SPEND : 0, RENT_SPEND, MEAL_SPEND));
            if (k < 2 * PriceIndex.WEIGHT_MONTHS) held0 &= gapped.getWeight(PriceIndex.GROCERIES) == 0;
        }
        check("(c) a month with no sale among the year's holds the join off until a whole year of sales (the 2 x WEIGHT_MONTHS-th month)",
                held0 && gapped.getWeight(PriceIndex.GROCERIES) > 0);
    }

    /** (d) with no groceries the rule still reads a real rate: the one the meals' price makes of the level, a year on. */
    static void theRateIsReal() {
        PriceIndex px = new PriceIndex();
        List<Double> readings = new ArrayList<>();
        int rateAt = -1;
        boolean until = true, flag = true, idle = true;
        int months = PriceIndex.SETTLING_MONTHS + PriceIndex.WINDOW + 3;
        for (int c = 1; c <= months; c++) {
            take(px, c, prices(0, RENT_PRICE, MEAL_PRICE * Math.pow(1 + MEAL_RISE, c)), spends(0, RENT_SPEND, MEAL_SPEND));
            if (px.isBased()) readings.add(px.getIndex());
            else idle &= !px.hasRate() && px.inflation() == 0;
            flag &= px.hasRate() == readings.size() > PriceIndex.WINDOW;
            if (px.isBased()) until &= px.monthsUntilRate() == Math.max(0, PriceIndex.WINDOW + 1 - readings.size());
            if (px.hasRate() && rateAt < 0) rateAt = c;
        }
        check("(d) the first rate comes at the SETTLING_MONTHS + WINDOW-th month, not never", rateAt == PriceIndex.SETTLING_MONTHS + PriceIndex.WINDOW,
                "at the " + rateAt + "-th");
        check("(d) ...hasRate() is true exactly once the ring holds more than WINDOW readings, and reads nothing before the basing", flag && idle);
        check("(d) ...and the months until a rate follow the readings to nothing", until && px.monthsUntilRate() == 0);
        int last = readings.size() - 1;
        double year = readings.get(last) / readings.get(last - (PriceIndex.WINDOW - 1)) - 1;
        check("(d) the rule reads a rate above nothing from a rising meal price", px.hasRate() && px.inflation() > 0, String.valueOf(px.inflation()));
        near("(d) ...equal to the year-on-year change of the level as the index's own readings give it", px.inflation(), year, 1e-12);
        double rel = Math.pow(1 + MEAL_RISE, PriceIndex.WINDOW - 1);   // the meal price now over its price a year before
        int sinceBasing = months - PriceIndex.SETTLING_MONTHS;
        double now = px.getWeight(PriceIndex.RENT) + px.getWeight(PriceIndex.MEALS) * Math.pow(1 + MEAL_RISE, sinceBasing);
        double then = px.getWeight(PriceIndex.RENT) + px.getWeight(PriceIndex.MEALS) * Math.pow(1 + MEAL_RISE, sinceBasing) / rel;
        near("(d) ...and is the meals' rise at the meals' weight in the basket", px.inflation(), now / then - 1, 1e-9);
    }

    /** (e) a basket with groceries from the start is as it was: both halves at SETTLING_MONTHS, groceries in it from the basing, no relink. */
    static void groceriesFromTheStart() {
        PriceIndex px = new PriceIndex();
        for (int m = 1; m <= LEAD; m++) take(px, m, prices(0, RENT_PRICE, 0), spends(0, RENT_SPEND, 0));
        int basedAt = -1, linked = -1;
        double weight = 0;
        boolean steady = true, never = true;
        int months = PriceIndex.SETTLING_MONTHS + PriceIndex.WEIGHT_MONTHS + 20;
        for (int c = 1; c <= months; c++) {
            take(px, LEAD + c, prices(SHELF_PRICE, RENT_PRICE, 0), spends(SHELF_SPEND, RENT_SPEND, 0));
            if (basedAt < 0 && px.isBased()) {
                basedAt = c;
                linked = px.getLinkedMonth();
                weight = px.getWeight(PriceIndex.GROCERIES);
            } else if (basedAt > 0) {
                steady &= px.getLinkedMonth() == linked;
                never &= px.getWeight(PriceIndex.GROCERIES) == weight;
            }
        }
        check("(e) rent and groceries bought base the index at the SETTLING_MONTHS-th such month, the rent-only months before not counted",
                basedAt == PriceIndex.SETTLING_MONTHS, "based at the " + basedAt + "-th");
        check("(e) ...with groceries in the basket from the basing", weight > 0, String.valueOf(weight));
        check("(e) ...and the basket is never relinked for them afterwards", steady && never && linked == LEAD + PriceIndex.SETTLING_MONTHS);
    }

    /* ============================ 5. the money audit ============================ */

    static void sectionFive() {
        out.println("\n--- 5. the money audit: every month played is exact to the cent ---");
        out.printf("   section 2's city: %d months audited, the worst residual %.2e of its tolerance; the others: %d months, %.2e%n",
                playedAudit.months, playedAudit.worst, otherAudit.months, otherAudit.worst);
        check("every month section 2's city played (the founding's months to the trap included) has a money audit", playedAudit.months > PLAYED_BOUND && playedAudit.missing == 0,
                playedAudit.months + " months, " + playedAudit.missing + " without");
        check("...and each one's residual is within MoneyAudit.tolerance(CENT, moved)", playedAudit.broken == 0, playedAudit.first);
        check("every month the other cities played has an audit, and each one's residual is within the tolerance too",
                otherAudit.months > 0 && otherAudit.missing == 0 && otherAudit.broken == 0, otherAudit.first);
    }
}
