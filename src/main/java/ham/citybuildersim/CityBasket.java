package ham.citybuildersim;

/**
 * What the city eats: the basket per head, struck from the households' own
 * statements, and the file's reference basket for a city that has none yet.
 *
 * ==================== WHAT THE CITY EATS ====================
 *
 * Loaded once. Consumption owns the thirteen goods, Engel's curve and
 * Bennett's, and knows nothing about this city; what makes the basket
 * THIS city's basket is the incomes handed to it below, cell by cell.
 *
 * LOADED, NOT REQUIRED. A city whose consumption.json is missing or
 * unreadable gets an empty model, and cityBasketPerHead() then hands the
 * shops an empty basket - which they read as "no shelf yet" rather than
 * as "nobody eats". A data file must not be able to stop a game.
 *
 * ==================== WHERE IT CAME FROM ====================
 *
 * This was Game's "WHAT THE CITY EATS" section until 2026-09-18, when it
 * moved out with its text intact: cityBasketPerHead() as perHead(), and
 * referenceBasket() under its own name. The Consumption model and its lazy
 * loader, Game.getConsumption(), stayed behind, because the field is Game's
 * and the loader is what every reader of it goes through. It holds no month
 * flows of its own - the basket is struck fresh each time the shops and the
 * kitchens ask for it - so Game keeps cityBasketPerHead() as a delegation
 * and nothing else changed.
 *
 * WHY IT LEFT. Game.java was 8,200 lines, and a session reading what the
 * city eats had to carry the month, the save and the treasury with it. A
 * mechanic that has the shape of a class - its own month, its own figures,
 * one place it is called from - is its own file since 2026-09-18, and Game
 * keeps what every reader goes through: the getters, and the order of the
 * month. The interface went the same way the same day. See the project's
 * splitting-game.md.
 */
public final class CityBasket {

    /**
     * Kilograms of each of the thirteen in ONE person-month, averaged over the
     * city by headcount.
     *
     * The shops buy one basket; the city contains many households and they do
     * not eat the same one. A labourer's month and an elite's month differ in
     * what they are made of - that is Bennett's law and it is the whole point
     * of the model - but the shops stock ONE shelf, so what they stock is the
     * city's average, weighted by the people who will come through the door.
     *
     * The rich are not weighted by their money here, deliberately. A shelf
     * stocked for spending would carry the elite's fish and the elite's fruit
     * for a city of labourers, and the households at the bottom of the ladder
     * would find nothing they could afford - which is a real failure mode of
     * real retail and is NOT what this models today. One person, one place in
     * the basket.
     *
     * Was Game.cityBasketPerHead(), which now delegates here; the body is that
     * one, through Game's getters.
     */
    java.util.Map<Good, Double> perHead(Game game) {
        java.util.Map<Good, Double> out = new java.util.EnumMap<>(Good.class);
        Consumption consumption = game.getConsumption();
        if (consumption.items().isEmpty()) return out;

        ForeignAccounts foreign = game.getForeignAccounts();
        HouseholdBalance householdBalance = game.getHouseholdBalance();

        double rate = foreign == null ? 1 : foreign.getRate();
        if (householdBalance == null) return referenceBasket(consumption);
        double heads = 0;
        for (Household cell : householdBalance.cells()) {
            double homes = cell.households();
            if (homes < .5) continue;
            double perHome = cell.headcount();
            if (perHome <= 0) continue;
            double people = homes * perHome;

            double x = consumption.incomeMultiple(cell.disposable() / perHome, rate);
            if (!(x > 0)) continue;

            java.util.Map<String, Double> theirs =
                    consumption.basket(x, Consumption.timePressure(cell.shape()));
            for (java.util.Map.Entry<String, Double> e : theirs.entrySet()) {
                Good g = Good.byName(e.getKey());
                if (g != null) out.merge(g, e.getValue() * people, Double::sum);
            }
            heads += people;
        }
        /*
         * A FOUNDING CITY HAS NO LEDGER TO READ, AND STILL HAS TO EAT.
         *
         * perHead() is struck from the household statements, and on
         * month one there are none - the shops reach their restock before any
         * household has been settled. The first version of this returned an
         * empty basket there, so recentUse() was zero for all thirteen goods,
         * the shops stocked nothing, basketsOnShelf() was nothing, and the
         * city could not buy a loaf. InfrastructureCheck caught it on the one
         * assertion that says a shop can be supplied at all.
         *
         * So an unmeasured city stocks the reference household's basket. It is
         * the same fallback the shops already had in another form - recentUse()
         * has always guessed at coverage before it had a month of sales to go
         * on - and it is replaced by the real thing the moment one month has
         * been lived.
         */
        if (heads <= 0) return referenceBasket(consumption);
        for (java.util.Map.Entry<Good, Double> e : out.entrySet()) e.setValue(e.getValue() / heads);
        return out;
    }

    /** What the file's reference household eats, for a city with no statements yet. */
    private static java.util.Map<Good, Double> referenceBasket(Consumption consumption) {
        java.util.Map<Good, Double> out = new java.util.EnumMap<>(Good.class);
        for (java.util.Map.Entry<String, Double> e
                : consumption.basket(consumption.referenceMultiple(), 0).entrySet()) {
            Good g = Good.byName(e.getKey());
            if (g != null && e.getValue() > 0) out.put(g, e.getValue());
        }
        return out;
    }
}
