package ham.citybuildersim;

import ham.citybuildersim.sectors.Restaurants;
import ham.citybuildersim.sectors.Retail;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A meal out is food, and it is the same food.
 *
 * WHAT THIS HAS TO PROVE, and none of it is "the sector makes money" - whether
 * it does depends on the city, which is the point of it:
 *
 *   1. A MEAL IS A NINETIETH OF A PERSON-MONTH, in both directions, and the
 *      kitchens buy the reference basket in exactly that proportion. If the
 *      two halves of that conversion ever disagree, a Diner either feeds
 *      thirty times the people it can or starves them, and nothing else in the
 *      game would notice.
 *
 *   2. THE MARGIN IS STRUCK AGAINST THE TABLES, on GoodsMarket.strike()'s own
 *      rule: empty tables at the floor, a queue at the ceiling, monotone
 *      between them.
 *
 *   3. A MEAL EATEN COUNTS AS SUBSISTENCE AT THE GROCER'S PRICE, not at the
 *      restaurant's. Two and a half to five times the food cost is wages and
 *      washing up, and none of it is nourishment. Counting the ticket would
 *      let a city feed itself by putting its prices up, which is the most
 *      embarrassing bug this sector could have.
 *
 *   4. THE KITCHENS TAKE FOOD OFF THE SAME SHELF. They add nothing to the
 *      city's supply: what they add is a second door to it. A harness that did
 *      not check this would let the sector quietly conjure dinners.
 *
 *   5. AND THE BOOTSTRAP. Every new sector in this game has deadlocked on one:
 *      a kitchen with no service has used nothing, so it orders nothing, so it
 *      has nothing to cook. LuxuryRetail lost a whole run to that one.
 *
 * Every fixture CAUSES its condition rather than waiting for it.
 */
public class RestaurantsCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-72s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-72s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static final String DINER = "Diner";
    static final String RESTAURANT = "Restaurant";

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ============== 1. a meal is a ninetieth of a person-month ============== */
        out.println("--- a meal is a ninetieth of a person-month ---");

        close("three a day, thirty days", Restaurants.MEALS_A_PERSON_MONTH, 90, 0);
        close("...and the same fact the other way up",
                Restaurants.PERSON_MONTHS_PER_MEAL * Restaurants.MEALS_A_PERSON_MONTH, 1, 1e-12);

        /*
         * THE CATALOGUE HAS TO AGREE WITH THE CONSTANT, to the kilogram. A
         * kitchen's `uses` is the reference basket scaled by the person-months
         * its coverage comes to, and if the two ever drift the sector is
         * either conjuring food or throwing it away - silently, because
         * nothing else in the game divides one by the other.
         */
        Path dir = Files.createTempDirectory("restaurantscheck");
        GameFiles files = new GameFiles(dir.resolve("data"), dir.resolve("no-legacy"));
        Game game = new Game(files);
        quietly(game::run);

        BuildingsTemplate diner = template(game, DINER);
        BuildingsTemplate restaurant = template(game, RESTAURANT);
        assertTrue("the catalogue has both kitchens", diner != null && restaurant != null);

        BuildingsTemplate shop = template(game, "Convenience Store");
        assertTrue("...and a shop to measure the basket against", shop != null);

        for (BuildingsTemplate t : new BuildingsTemplate[]{ diner, restaurant }) {
            double meals = t.makes(Good.MEALS);
            double personMonths = meals * Restaurants.PERSON_MONTHS_PER_MEAL;
            boolean allMatch = true;
            double kg = 0;
            for (Good g : Retail.SHELF) {
                double perHead = shop.uses(g) / shop.makes(Good.GROCERIES);
                double want = perHead * personMonths;
                kg += t.uses(g);
                if (Math.abs(t.uses(g) - want) > 1e-6) allMatch = false;
            }
            report(t.getName() + " buys the shop's own basket, to the kilogram", allMatch,
                    String.format("%,.0f meals = %,.1f person-months = %,.0f kg a month",
                            meals, personMonths, kg));
        }

        /* ============== 2. the margin, struck against the tables ============== */
        out.println("\n--- the margin is struck against the tables ---");

        Restaurants kitchens = game.getSectors().restaurants();
        assertTrue("the sector is registered under its saved name",
                game.getSectors().byKey(Sectors.RESTAURANTS) == kitchens);

        /*
         * A MONTH HAS TO HAVE HAPPENED. The basket is handed in by Game from
         * the household ledger it just settled, so a city that has not run one
         * has an empty basket and a meal that costs nothing - which is not a
         * bug, it is a fixture that asked too early.
         */
        quietly(() -> {
            /*
             * THE FIXTURE BUYS ITS OWN KITCHENS. A Diner is $1.1M and eight
             * staff, and the question here is what one DOES, not whether a
             * sixty-house town could afford one - so the city is given the
             * money and the land the way every other fixture in this suite is.
             */
            game.getGovernmentInvestor().spend(-4_000_000);
            game.getLandManager().setOwnedSqFt(game.getLandManager().getOwnedSqFt() + 20_000_000L);
            LongPlaytest.build(game, "House", 60);
            LongPlaytest.build(game, "Convenience Store", 4);
            LongPlaytest.build(game, "Mixed Farm", 3);
            LongPlaytest.build(game, "Coal Power Plant", 1);
            LongPlaytest.build(game, "Water Treatment Plant", 1);
            LongPlaytest.build(game, "Paved Road", 6);
            game.simulateMonths(3);
        });

        double food = kitchens.foodCostOfAMeal(game.getMarkets());
        report("the food in a meal costs something", food > 0,
                String.format("$%.4f a meal, $%.4f a person-month",
                        food, food * Restaurants.MEALS_A_PERSON_MONTH));

        /*
         * WITH NO KITCHENS AT ALL there is nothing to strike a margin over,
         * and a sector that quoted one anyway would have the investor scoring
         * a building against a price no diner ever paid.
         */
        report("a city with no kitchens has no tables", kitchens.seats() == 0, "");
        close("...so the margin sits at its floor",
                kitchens.strikeMargin(game.getMarkets(), 0) / food, Restaurants.MARGIN_FLOOR, 1e-9);

        quietly(() -> {
            LongPlaytest.build(game, DINER, 1);
            game.simulateMonths(9);
        });

        int seats = kitchens.seats();
        report("a Diner is a Diner's worth of tables",
                seats == diner.getCoverage(),
                String.format("%,d meals against %,d", seats, diner.getCoverage()));

        double empty = kitchens.strikeMargin(game.getMarkets(), 0);
        double half  = kitchens.strikeMargin(game.getMarkets(), seats);
        double mobbed = kitchens.strikeMargin(game.getMarkets(), seats * 1000.0);
        double cost = kitchens.getFoodCost();
        out.printf("   $%.4f empty, $%.4f full, $%.4f mobbed, on $%.4f of food%n",
                empty, half, mobbed, cost);
        close("empty tables charge the floor", empty / cost, Restaurants.MARGIN_FLOOR, 1e-9);
        close("...a kitchen as full as it is big charges the middle",
                half / cost, (Restaurants.MARGIN_FLOOR + Restaurants.MARGIN_CEILING) / 2, 1e-6);
        assertTrue("...and a queue round the block charges near the ceiling",
                mobbed / cost > Restaurants.MARGIN_CEILING * .99
                        && mobbed / cost <= Restaurants.MARGIN_CEILING);
        assertTrue("...monotone between them", empty < half && half < mobbed);

        /*
         * AND THE FLOOR CLEARS A WAGE, which is the whole reason it is twice
         * the boutiques'. A kitchen is the most labour-heavy business in the
         * catalogue; one that marked food up like a shop marks up a watch
         * would not make its payroll in any city.
         */
        for (BuildingsTemplate t : new BuildingsTemplate[]{ diner, restaurant }) {
            double meals = t.makes(Good.MEALS);
            double margin = meals * cost * (Restaurants.MARGIN_FLOOR - 1);
            double payroll = 0;
            for (JobType job : JobType.values()) payroll += t.getJobs(job) * PayTier.of(job).getMonthlyWage();
            report(t.getName() + ": the margin at the FLOOR still covers the payroll",
                    margin > payroll,
                    String.format("$%,.0fk of margin against $%,.0fk of wages (%.0f%%)",
                            margin, payroll, payroll / margin * 100));
            /*
             * ...AND IT CLEARS THEM AT THE RATE A CITY ACTUALLY RUNS AT, which
             * is the assertion that matters and the one two calibrations in a
             * row got wrong.
             *
             * A BUILDING PAYS ITS STAFF AT A HUNDRED PER CENT AND SERVES AT
             * THE OPERATING RATE. Every sector has that asymmetry; a kitchen
             * feels it worst, because its payroll is most of its cost. The
             * playtest's cities run their commerce at around half, so half
             * their tables at the middle of the band is the honest bar - and a
             * quarter of it goes straight back out in sales tax.
             *
             * MEASURED WITH THE FIRST TWO CALIBRATIONS: eight staff on 2,700
             * meals cleared 29% of its wages, eight on 13,500 cleared 62%, and
             * both died. Neither would have been caught by anything else -
             * LongPlaytest reported a green audit either way, and the sector
             * simply sat at the bottom of the credit table with its margin
             * pinned at the ceiling and millions of diners at the door.
             */
            double middle = (Restaurants.MARGIN_FLOOR + Restaurants.MARGIN_CEILING) / 2;
            double realistic = meals * .5 * cost * (middle - 1) * .75;
            report("...and clears them at half its tables, mid-band, after the sales tax",
                    realistic > payroll,
                    String.format("$%,.0fk against $%,.0fk of wages (%.2fx)",
                            realistic, payroll, realistic / payroll));
        }

        /* ============== 3. a meal eaten is food, at the grocer's price ============== */
        out.println("\n--- a meal eaten is food, and it is priced at the grocer's ---");

        /*
         * THE MOST IMPORTANT ASSERTION IN THIS FILE, and it is asked of a
         * BENCH rather than of a city, because a city has a hundred other
         * reasons for its hunger number to move. Four identical ledgers, one
         * cell each, differing only in how many dinners the household ate and
         * what it was charged for them.
         *
         * A COUPLE eats two person-months of food in a month, so a hundred and
         * eighty dinners - ninety a head - is exactly its subsistence. Half of
         * them is half of it. If the conversion in advanceMonth() ever drifts
         * from Restaurants.PERSON_MONTHS_PER_MEAL, these two numbers stop
         * being 0% and 50% and nothing else in the game notices.
         *
         * NOTHING IS DELIVERED FROM THE SHOPS - supplyRatio is zero - so the
         * only thing feeding anybody here is the restaurant.
         */
        double grocer = 1.25;                 // what a person-month of food costs
        double perCouple = 2 * grocer;        // ...and what a couple therefore needs

        double[] hunger = new double[4];
        double[] ticket = { 0, 1.0, 1.0, 100.0 };
        double[] mealsEach = { 0, 90, 180, 180 };
        for (int i = 0; i < 4; i++) {
            HouseholdBalance bench = new HouseholdBalance();
            Household cell = bench.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
            cell.households = 1000;
            cell.savings = 10_000_000;
            cell.disposable = 10;
            cell.mealWant = 1_000_000;
            cell.planned = 0;                 // nothing was bought at a shop
            cell.subsistence = perCouple;
            if (mealsEach[i] > 0) {
                // THE REAL PATH: the same call LuxuryCounter.dine() makes.
                bench.takeMeals(mealsEach[i] * cell.households, ticket[i]);
            }
            final HouseholdBalance b = bench;
            quietly(() -> b.advanceMonth(
                    (shape, tier) -> shape == FamilyStructure.COUPLE && tier == PayTier.SKILLED ? 1000 : 0,
                    new double[Household.ROWS], 0, new double[Household.ROWS],
                    new double[Household.ROWS], grocer, 0, 0));
            hunger[i] = bench.getHungerRate();
        }
        out.printf("   no dinners %.1f%%, ninety %.1f%%, a hundred and eighty %.1f%%,"
                + " and the same hundred and eighty at a hundred times the price %.1f%%%n",
                hunger[0] * 100, hunger[1] * 100, hunger[2] * 100, hunger[3] * 100);

        close("a household that ate nothing is wholly short", hunger[0], 1, 1e-9);
        close("...ninety dinners feeds one of the two of them", hunger[1], .5, 1e-9);
        close("...and a hundred and eighty feeds the household", hunger[2], 0, 1e-9);
        /*
         * AND THE TICKET IS NOT THE NOURISHMENT. A kitchen charges two and a
         * half to five times what the food in the plate cost; the rest is
         * wages, rent and somebody else's washing up. Count the ticket and a
         * city feeds itself by putting its prices up, which is the most
         * embarrassing bug this sector could have had.
         */
        close("...and a dinner at a hundred times the price feeds exactly the same",
                hunger[3], hunger[2], 1e-9);

        /*
         * ...AND THE DINNERS ARE SPENT ONCE. A field that crosses a month
         * boundary has to be cleared by whoever reads it, or a household eats
         * the same dinner every month for ever and the city is never hungry
         * again. Run the same bench a second month with nothing added.
         */
        HouseholdBalance twice = new HouseholdBalance();
        Household repeat = twice.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
        repeat.households = 1000;
        repeat.savings = 10_000_000;
        repeat.disposable = 10;
        repeat.mealWant = 1_000_000;
        repeat.planned = 0;
        repeat.subsistence = perCouple;
        twice.takeMeals(180 * repeat.households, 1.0);
        quietly(() -> {
            twice.advanceMonth((shape, tier) -> shape == FamilyStructure.COUPLE
                    && tier == PayTier.SKILLED ? 1000 : 0,
                    new double[Household.ROWS], 0, new double[Household.ROWS],
                    new double[Household.ROWS], grocer, 0, 0);
            repeat.planned = 0;
            repeat.subsistence = perCouple;
            twice.advanceMonth((shape, tier) -> shape == FamilyStructure.COUPLE
                    && tier == PayTier.SKILLED ? 1000 : 0,
                    new double[Household.ROWS], 0, new double[Household.ROWS],
                    new double[Household.ROWS], grocer, 0, 0);
        });
        close("a dinner is eaten once: the month after, the household is short again",
                twice.getHungerRate(), 1, 1e-9);

        /* ============== 3b. appetite, not money, is the ceiling ============== */
        out.println("\n--- and a fortune buys a dearer dinner, not a ninety-first one ---");

        /*
         * THE BOUND THAT MAKES THE WEALTH TERM SAFE. A household's money says
         * what it CAN spend eating out; its stomach says how many dinners it
         * can get through. Without the second, a city whose households hold
         * forty years of its output would ask for meals by the billion and the
         * sector would be a hole the hoard drains into rather than a business.
         *
         * A cell with a fortune and nothing else to do with it, asked at a
         * price so low that money could not possibly be the constraint.
         */
        HouseholdBalance rich = new HouseholdBalance();
        Household fortune = rich.cell(FamilyStructure.COUPLE, PayTier.ELITE);
        fortune.households = 1000;
        fortune.savings = 1e12;
        fortune.disposable = 10;
        fortune.mealWant = 1e9;
        double people = fortune.people();
        double ceiling = people * Restaurants.MEALS_A_PERSON_MONTH
                * HouseholdBalance.MOST_MEALS_EATEN_OUT;
        double asked = rich.mealsWanted(1e-6);
        report("a city that could buy anything still only eats a third of its meals out",
                Math.abs(asked - Math.floor(ceiling)) < 1e-9,
                String.format("%,.0f meals asked for against %,.0f people x %.0f x %.2f",
                        asked, people, Restaurants.MEALS_A_PERSON_MONTH,
                        HouseholdBalance.MOST_MEALS_EATEN_OUT));

        // ...and when money is the tighter of the two, money wins.
        fortune.mealWant = 1;
        report("...and a household that cannot afford a third of them eats fewer",
                rich.mealsWanted(1) < Math.floor(ceiling),
                String.format("%,.0f meals", rich.mealsWanted(1)));

        /* ============== 4. the kitchens eat the city's own food ============== */
        out.println("\n--- the kitchens take food off the same shelf ---");

        /*
         * NO NEW FOOD. The city's total holding of the thirteen goods is what
         * it is; a kitchen that served a thousand dinners has that much less
         * of it and the shops have to buy it back like anybody else. The test
         * is that serving MOVES the larder, in the basket's own proportions.
         */
        double[] heldBefore = new double[Retail.SHELF.length];
        for (int i = 0; i < Retail.SHELF.length; i++) heldBefore[i] = kitchens.getPantry(Retail.SHELF[i]);
        double larder = kitchens.mealsInTheLarder();
        report("the kitchens restocked themselves without ever having served", larder > 0,
                String.format("%,.0f meals in the walk-in against %,d of tables", larder, seats));

        kitchens.strikeMargin(game.getMarkets(), seats);
        double served = kitchens.serve(game.getMarkets(), seats);
        report("...and they serve what the tables and the larder allow", served > 0,
                String.format("%,.0f meals", served));

        boolean tookAll = true;
        for (int i = 0; i < Retail.SHELF.length; i++) {
            Good g = Retail.SHELF[i];
            double expected = heldBefore[i] - served * kitchens.kgPerMeal(g);
            if (Math.abs(kitchens.getPantry(g) - expected) > 1e-6) tookAll = false;
        }
        assertTrue("...off every one of the thirteen, in the basket's proportions", tookAll);

        double fed = served * Restaurants.PERSON_MONTHS_PER_MEAL;
        out.printf("   %,.0f meals is %,.1f person-months of food, from a larder of %,.0f meals%n",
                served, fed, larder);

        /* ============== 5. the bootstrap ============== */
        out.println("\n--- the bootstrap every new sector deadlocks on ---");

        /*
         * THE FIRST HALF IS ALREADY PROVED ABOVE: "the kitchens restocked
         * themselves without ever having served" is the deadlock LuxuryRetail
         * lost a run to - a pantry that orders against what was USED orders
         * nothing for a kitchen that has never cooked, so it has nothing to
         * cook, so it uses nothing. The larder above was full with rServed at
         * zero, which is the fix working.
         *
         * THE SECOND HALF IS THE PLANNER, and it is the one that cost three
         * centuries: planMaker() scores a sector on its sales record, so a
         * sector with no kitchens scores zero and never builds one, while the
         * margin sits pinned at its ceiling and the queue goes home. The
         * signal has to be the QUEUE, which exists whether or not there is
         * anywhere to eat.
         */
        Path dir2 = Files.createTempDirectory("restaurantscheck-plan");
        Game hungry = new Game(new GameFiles(dir2.resolve("data"), dir2.resolve("no-legacy")));
        quietly(() -> {
            hungry.run();
            hungry.getGovernmentInvestor().spend(-4_000_000);
            hungry.getLandManager().setOwnedSqFt(hungry.getLandManager().getOwnedSqFt() + 20_000_000L);
            LongPlaytest.build(hungry, "House", 60);
            LongPlaytest.build(hungry, "Convenience Store", 4);
            LongPlaytest.build(hungry, "Mixed Farm", 3);
            LongPlaytest.build(hungry, "Coal Power Plant", 1);
            LongPlaytest.build(hungry, "Water Treatment Plant", 1);
            hungry.simulateMonths(4);
        });
        Restaurants none = hungry.getSectors().restaurants();
        report("a city with a queue and no kitchens has sold nothing at all",
                none.seats() == 0 && none.getServed() == 0, "");

        BusinessInvestment.Decision quiet2 = none.plan(hungry.getBusinessInvestment(), hungry);
        report("...and with nobody at the door it does not want one either",
                !quiet2.build, quiet2.reason);

        // ...and now a queue, measured the way LuxuryCounter.dine() measures it:
        // at the margin's floor, before anybody has been told a price.
        none.strikeMargin(hungry.getMarkets(), 500_000);
        BusinessInvestment.Decision wanted = none.plan(hungry.getBusinessInvestment(), hungry);
        report("...but a queue at a door that is not there is a reason to build one",
                wanted.build, wanted.reason);

        /*
         * AND THE BUILDING HAS TO BE WORTH ITS DEBT. plan() only says what the
         * sector wants; consider() asks estimatedMonthlyProfit() whether it
         * services what it would borrow, and the generic estimate reads a
         * maker's sales - which are zero. Answer nothing here and a sector
         * with half a million diners at the door never borrows a penny.
         */
        double worth = none.estimatedMonthlyProfit(diner, hungry.getBusinessInvestment());
        report("...and the sector values its own kitchen, because nothing else can",
                worth > 0, String.format("$%,.0fk a month for a Diner", worth));

        /* ============== and the save ============== */
        out.println("\n--- and the dinners survive a save ---");

        /*
         * THE SLOT, AND SOMETHING IN IT. A field that crosses a month boundary
         * has to survive a save or a city reloaded between dinner and the
         * hunger measure loses the meal. Driven through takeMeals() rather
         * than written in, so what is checked is the field the real path uses.
         */
        HouseholdBalance saver = new HouseholdBalance();
        Household diner2 = saver.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
        diner2.households = 100;
        diner2.savings = 100_000;
        diner2.disposable = 10;
        diner2.mealWant = 10_000;
        saver.takeMeals(300 * diner2.households, 1.0);
        double eatenOut = 0;
        double[] cells = saver.toCellSaveArray();
        int keyCount = saver.cellKeys().length;
        assertTrue("the cell array carries a slot for them",
                (cells.length - 3) % keyCount == 0
                        && (cells.length - 3) / keyCount == HouseholdBalance.CELL_SLOTS);
        for (int i = 0; i < keyCount; i++) {
            eatenOut += cells[i * HouseholdBalance.CELL_SLOTS + HouseholdBalance.CELL_SLOTS - 1];
        }
        report("...and the meals are in it", Math.abs(eatenOut - 300) < 1e-9,
                String.format("%,.2f meals a household, summed over the cells", eatenOut));

        /*
         * ...AND A SAVE FROM BEFORE THE KITCHENS RELOADS AS A CITY THAT ATE IN,
         * which is exactly true of that city. The reader checks the width, so
         * SAVE_FORMAT did not have to move - the same tail-append the cars and
         * the investment income got, and the same test they were given.
         */
        double[] older = new double[keyCount * (HouseholdBalance.CELL_SLOTS - 1) + 3];
        System.arraycopy(cells, 0, older, 0, 0);
        for (int i = 0; i < keyCount; i++) {
            System.arraycopy(cells, i * HouseholdBalance.CELL_SLOTS,
                    older, i * (HouseholdBalance.CELL_SLOTS - 1), HouseholdBalance.CELL_SLOTS - 1);
        }
        for (int k = 0; k < 3; k++) {
            older[older.length - 3 + k] = cells[cells.length - 3 + k];
        }
        HouseholdBalance reloaded = new HouseholdBalance();
        boolean took = reloaded.restoreCells(saver.cellKeys(), older, Equity.COMPANIES);
        assertTrue("a save from before the kitchens is still read", took);
        double eatenThen = 0;
        for (Household c : reloaded.cells()) eatenThen += c.mealsEaten();
        close("...and that city ate in", eatenThen, 0, 1e-12);

        out.println();
        if (fails == 0) {
            out.println("A meal out is food, it is the same food, and it is priced at the grocer's.");
        } else {
            out.println(fails + " check(s) failed.");
            System.exit(1);
        }
    }
}
