package ham.citybuildersim;

import ham.citybuildersim.sectors.Retail;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * What a played city WOULD buy, measured against what it spends today.
 *
 * THE GO/NO-GO FOR TURNING CONSUMPTION ON, and the reason the model was built
 * before anything ate it. Households still buy the single FOOD good; this asks
 * every cell in a real city what it would put in a thirteen-good basket at the
 * income it actually has, and prints that beside the food bill it actually
 * pays.
 *
 * WHAT THE ANSWER DECIDES. If the implied bill lands near today's, switching
 * the goods on is a change of detail and the city will not notice. If it lands
 * at three times today's, then switching them on is a BALANCE change wearing a
 * realism change's clothes - the same trap the senior-care denominator was
 * walked back from on 2026-09-15 - and the quantities want calibrating first,
 * here, where nobody is hungry and nobody dies of it.
 *
 * It also prints the weights the price index would take, because those come
 * from this model now rather than from a month's tills, and a weight nobody
 * has looked at is a weight nobody has checked.
 *
 *     java ham.citybuildersim.ShadowBasket [months]
 */
public class ShadowBasket {

    public static void main(String[] args) throws Exception {
        int months = args.length > 0 ? Integer.parseInt(args[0]) : 240;

        PrintStream real = System.out;
        PrintStream quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        Consumption model = new Consumption();
        if (!model.load()) {
            real.println("No consumption data; nothing to shadow.");
            System.exit(1);
        }

        Game g = new Game(GameFiles.scratch("shadow-basket"));
        System.setOut(quiet);
        try {
            g.run();
            g.getGovernmentInvestor().spend(-2_000_000);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            LongPlaytest.build(g, "House", 400);
            LongPlaytest.build(g, "Convenience Store", 20);
            LongPlaytest.build(g, "Construction Depot", 6);
            LongPlaytest.build(g, "Coal Power Plant", 2);
            LongPlaytest.build(g, "Water Treatment Plant", 2);
            LongPlaytest.build(g, "Industrial Bakery", 4);
            LongPlaytest.build(g, "Elementary School", 3);
            LongPlaytest.build(g, "Walk-in Clinic", 3);
            LongPlaytest.build(g, "Paved Road", 20);
            g.simulateMonths(months);
        } finally {
            System.setOut(real);
        }

        /*
         * THE RATE, AND WHY IT IS READ BEFORE ANYTHING IS DIVIDED.
         *
         * consumption.json is priced at the WORLD's prices. A cell's
         * disposable() is in the CITY's money, which is the world's money
         * times this rate. The first version of this probe divided one
         * straight into the other and reported the city at 117 times
         * subsistence; the rate was 2.083 and the true figure was 56. Both
         * currencies are now named wherever they appear, here and in
         * Consumption.
         */
        double fx = g.getForeignAccounts().getRate();
        double subsistenceWorld = model.subsistenceCostAtWorldPrices();
        double subsistence = model.subsistenceCost(fx);
        HouseholdBalance books = g.getHouseholdBalance();
        HouseholdAccounts accounts = g.getHouseholds();
        Retail shops = g.getSectors().retail();
        double shoppingPerHead = accounts.shoppingPerHead();
        /*
         * WHAT THE SHOPS PAY, NOT WHAT THEY CHARGE. The first draft of this
         * compared a basket priced at the WORLD's prices against
         * shoppingPerHead, which is the shelf price with Retail's whole markup
         * on it - and reported that switching the model on would cut the
         * city's food bill to a third. It would not; the two numbers were
         * never the same kind of number.
         *
         * THE GO/NO-GO IS ANSWERED AND THE GOOD IT COMPARED AGAINST IS GONE.
         * This probe existed to ask whether switching the thirteen on was a
         * realism change or a balance change; the answer was 1.03x, they were
         * switched on, and FOOD was retired behind them. So the denominator
         * changed rather than the probe: what the model says the city WOULD eat
         * is now measured against what the shops ACTUALLY BOUGHT this month, at
         * the same world prices.
         *
         * That is a live instrument rather than a one-off. Restocking is
         * recentUse times a cover ratio, capped by shelf room, so the two can
         * part company - and when they did, they did it silently and by a
         * factor of five: a shelf still sized in units of a good that had
         * become kilograms held the city at 20% of demand for ever. A ratio
         * far from one means the shelf, the cover or the basket has drifted
         * from the others.
         */

        real.printf("A city of %,d at month %d.%n", g.getPopulationManager().getPopulation(), g.getMonth());
        real.printf("The exchange rate is %.3f, so the world's prices are that much dearer here.%n", fx);
        real.printf("Subsistence costs $%.2f at the world's prices, $%.2f in this city's money.%n",
                subsistenceWorld * 1000, subsistence * 1000);
        real.printf("A month of food costs $%.2f landed here across %d goods;"
                + " the shops charge $%.2f a head, which is Retail's markup on it.%n%n",
                shops.getImportPrice() * 1000, Retail.SHELF.length, shoppingPerHead * 1000);

        real.printf("%-34s %8s %9s %8s %9s %8s %7s%n",
                "cell", "people", "income/hd", "x", "would buy", "kcal/day", "diet");

        Map<String, Double> cityBasket = new LinkedHashMap<>();
        double cityPeople = 0, wouldSpend = 0, doesSpend = 0;
        double[] lowX = { Double.MAX_VALUE }, highX = { 0 }, satiated = { 0 };

        for (Household cell : books.cells()) {
            double households = cell.households();
            if (households < .5) continue;
            double heads = cell.headcount();
            if (heads <= 0) continue;

            double incomePerHead = cell.disposable() / heads;
            double x = model.incomeMultiple(incomePerHead, fx);
            if (!(x > 0)) continue;

            double pressure = Consumption.timePressure(cell.shape());
            Map<String, Double> basket = model.basket(x, pressure);
            double cost = model.costOf(basket);
            double people = households * heads;

            for (Map.Entry<String, Double> e : basket.entrySet()) {
                cityBasket.merge(e.getKey(), e.getValue() * people, Double::sum);
            }
            lowX[0] = Math.min(lowX[0], x);
            highX[0] = Math.max(highX[0], x);
            if (model.caloriesOf(basket) >= Consumption.KCAL_A_MONTH * Consumption.SATIATION - 1e-6) {
                satiated[0] += people;
            }
            cityPeople += people;
            wouldSpend += cost * people;

            real.printf("%-34s %8.0f %9.2f %8.1f %9.2f %8.0f %7.2f%n",
                    cell.label().length() > 34 ? cell.label().substring(0, 34) : cell.label(),
                    people, incomePerHead * 1000, x, cost * 1000,
                    model.caloriesOf(basket) / 30, model.dietQuality(basket));
        }

        /* What the shops actually put on the shelf this month, at the same prices. */
        for (Good sg : Retail.SHELF) {
            Sector.Input in = shops.input(sg);
            doesSpend += (Math.max(0, in.boughtLocal) + Math.max(0, in.imported))
                    * sg.worldImportPrice();
        }

        real.printf("%n=== THE CITY ===%n");
        real.printf("  the model says  $%,.0f a month at world prices%n", wouldSpend * 1000);
        real.printf("  the shops bought $%,.0f a month at world prices%n", doesSpend * 1000);
        real.printf("  RATIO           %.2fx   <- one means the shelf keeps up with the model%n",
                doesSpend > 0 ? wouldSpend / doesSpend : 0);
        real.printf("  the shops' till $%,.0f, which is the same food with Retail's markup on it%n",
                shoppingPerHead * cityPeople * 1000);
        real.printf("  eating          %,.0f kcal a person a day, diet quality %.2f%n",
                cityPeople > 0 ? model.caloriesOf(cityBasket) / cityPeople / 30 : 0,
                model.dietQuality(cityBasket));

        /*
         * AND WHERE ON THE CURVE THE CITY ACTUALLY LIVES, which decides how
         * much of this model a player will ever see. The interesting range is
         * one to about fifty times subsistence; a city whose poorest household
         * is at three hundred has no poor households by this yardstick and
         * every one of them is satiated, so Engel never bites and Bennett has
         * nothing to vary over.
         */
        real.printf("%n=== WHERE THE CITY SITS ON THE CURVE ===%n");
        real.printf("  poorest cell %,.0f x subsistence, richest %,.0f x%n", lowX[0], highX[0]);
        real.printf("  satiated cells: %,.0f of %,.0f people%n", satiated[0], cityPeople);

        real.printf("%n=== THE WEIGHTS THE INDEX WOULD TAKE ===%n");
        Map<String, Double> shares = model.valueShares(cityBasket);
        for (Consumption.Item i : model.items()) {
            double w = shares.getOrDefault(i.key(), 0.0);
            real.printf("  %-18s %5.1f%%  %s%n", i.label(), w * 100,
                    "#".repeat(Math.max(0, (int) Math.round(w * 120))));
        }
        for (String cat : model.categories()) {
            double w = 0;
            for (Consumption.Item i : model.items()) {
                if (i.category().equals(cat)) w += shares.getOrDefault(i.key(), 0.0);
            }
            real.printf("     %-20s %5.1f%% of the food basket%n", cat.replace("FOOD_", ""), w * 100);
        }
    }
}
