package ham.citybuildersim;

import ham.citybuildersim.ui.UserInterface;

import java.util.List;

/**
 * Verifies that every building in the game can describe itself. Not part of the
 * game.
 *
 * WHY THIS EXISTS
 *
 * The build menu's info card carries no written text at all - every sentence on
 * it is derived from the building's own fields, so that no description can ever
 * describe a building as it was two rebalances ago. That buys accuracy at the
 * cost of a specific new failure: production1 is tonnes of ore in a mine,
 * construction points in a depot, kilowatts in a power plant and units of food
 * in a mill, and capacity is people in a house, shelf stock in a shop,
 * warehouse space in a mill and treatments in a clinic. A card that reads the
 * wrong one produces a confident sentence about the wrong number, which is
 * worse than no sentence at all - and it produces it silently, because the
 * string still formats.
 *
 * So this prints the description of every building, which is how a person
 * checks the mapping, and asserts the things a person would not notice: that
 * every building says SOMETHING, that no sentence carries a null or a NaN, and
 * that every JobType has a label. The last one is the trap with a fuse on it -
 * add a JobType and the card silently falls back to printing the enum constant
 * at a player.
 */
public class BuildMenuCheck {

    private static int failures = 0;

    private static void check(boolean condition, String what) {
        if (!condition) {
            System.out.println("  FAIL: " + what);
            failures++;
        }
    }

    public static void main(String[] args) {

        Game game = new Game(GameFiles.scratch("buildmenucheck"));
        UserInterface ui = new UserInterface(game);
        BuildingManager bm = game.getBuildingManager();
        // A raw Game holds no templates until something asks for them; the real
        // game does this on the new-game path.
        if (bm.getTemplates().isEmpty()) bm.initializeTemplates();

        System.out.println("=== WHAT THE CARD SAYS ===\n");

        int described = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {

            List<String> lines = ui.whatItDoes(t);

            System.out.println(t.getName() + "   [" + t.getCategory()
                    + (t.getCare() == CareType.NONE ? "" : " / " + t.getCare()) + "]");
            for (String line : lines) System.out.println("    " + line);
            System.out.println();

            /*
             * EVERY BUILDING, not most of them. A category with no branch in
             * whatItDoes() falls through the switch and produces an empty card -
             * which looks, on screen, exactly like a building that has nothing
             * to say about itself. That is the failure this catches, and it is
             * the one that arrives with the next new category.
             */
            check(!lines.isEmpty(),
                    t.getName() + " (" + t.getCategory() + ") describes itself as nothing");

            for (String line : lines) {
                check(!line.contains("null"), t.getName() + ": a null reached the card");
                check(!line.contains("NaN"),  t.getName() + ": a NaN reached the card");
                check(!line.contains("Infinity"),
                        t.getName() + ": an infinity reached the card");
                check(line.endsWith(".") || line.endsWith("\""),
                        t.getName() + ": a sentence that does not end");
            }
            if (!lines.isEmpty()) described++;

            // A healthcare building's card has to come from its CARE type, not
            // its category - a cemetery and a hospital are the same category and
            // could not be less alike.
            if (t.getCategory() == BuildingType.HEALTHCARE) {
                check(t.getCare() != CareType.NONE,
                        t.getName() + " is healthcare with no care type, so its card"
                        + " cannot say what it is for");
                check(!ui.whatCareItGives(t).isEmpty(),
                        t.getName() + " has care type " + t.getCare()
                        + " and no sentence for it");
            }
        }

        System.out.println("Buildings described: " + described
                + " of " + bm.getTemplates().size());

        /* -----------------------------------------------------------------
           EVERY JOB TYPE HAS A NAME

           jobLabel() falls back to the lowercased enum constant, which means a
           new JobType does not break the card - it just puts UNIV_HIGHTECH_ENG
           in front of a player. A fallback that produces something plausible is
           exactly the kind that never gets noticed, so it is asserted here
           instead.
           ----------------------------------------------------------------- */
        System.out.println("\n=== JOB LABELS ===");
        for (JobType job : JobType.values()) {
            String label = ui.jobLabel(job);
            System.out.printf("  %-22s %s%n", job.name(), label);
            /*
             * NOT "the label differs from the enum constant". DIPLOMA's correct
             * written label IS "diploma", so that assertion fails on a label
             * that is right - the same trap as asserting a measurement is not
             * equal to a constant it can legitimately sit on. What actually
             * distinguishes a written label from a fallen-through enum name is
             * the shape: an enum constant carries underscores and shouts.
             */
            check(!label.contains("_"),
                    job.name() + " kept its underscore - the card is showing the enum"
                    + " constant, not a label");
            check(label.equals(label.toLowerCase()),
                    job.name() + " label is not written in lower case like its neighbours");
            check(label.length() <= 18, job.name() + " label is too long for the row");
        }

        /* -----------------------------------------------------------------
           THE TWO PRICES AGREE WITH THE TILL

           The row quotes calculateTotalCost(), which is the method
           processBuildOrder() charges with - so the quote and the debit cannot
           drift. What CAN drift is the claim the row makes about the two being
           equal: it showed one figure when the yard covered the materials and
           two when it did not (the 0.7.21 tile's sticker, then its all-in;
           since 0.7.25 every card shows both, the all-in first), and if that
           test disagreed with the arithmetic the player would see "$1,400"
           and be charged $3,200.

           AND THE ARITHMETIC IS THE TAXED PRICE (0.7.21; Jerus: "Update to the
           taxed price"). Since 0.7.19 the quote carries the builders' sales
           tax, grossed up so the builders keep exactly the work (Game, THE
           BUILDERS' PRICE; the project's the-price-keeps-up.md, section 2):

               G = (N + imports + plant x (1 - rM)) / (1 - rB)

           N the work - the template's cash cost with its labour at today's
           builders' wages (BuildingManager.nonMaterialCost()); the material
           beyond the yard, split between the plant and the world as the
           market would sell it (Markets.quote()); rB and rM the builders' and
           the plant's effective sales rates, each held to [0,
           TaxPolicy.MAX_INCOME_TAX] as the quote holds them. Worked here from
           those inputs, never read back off the quote it tests - a check that
           reads the answer it tests checks nothing. Until 0.7.21 the
           expectation was the sticker plus the material at the import price,
           and every building failed it from 0.7.19 on (73 checks).
           ----------------------------------------------------------------- */
        System.out.println("\n=== THE PRICE COLUMN ===");
        double stock = bm.getConstructionMaterials();
        double price = bm.getConstructionMaterialPrice();
        System.out.printf("  yard holds %,.0f materials at $%.2f%n%n", stock, price);

        TaxPolicy tax = game.getEconomyManager().getTaxPolicy();
        double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX,
                tax.effectiveSalesRate(game.getSectors().construction())));
        double rM = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX,
                tax.effectiveSalesRate(game.getSectors().materials())));
        System.out.printf("  the builders' sales rate %.2f%%, the plant's %.2f%%%n%n", rB * 100, rM * 100);

        int twoPrices = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            double sticker = t.getCashCost();
            double allIn = game.calculateTotalCost(t, 1);
            double work = bm.nonMaterialCost(t);
            double beyond = Math.max(t.getConstructionMaterials() - stock, 0);
            Markets.Draw bought = game.getMarkets().quote(Good.MATERIALS, beyond, game.getSectors());
            double expected = (work + bought.importCost() + bought.localCost() * (1 - rM)) / (1 - rB);

            check(Math.abs(allIn - expected) < 1e-9,
                    t.getName() + ": quoted " + allIn + ", arithmetic says " + expected);
            check(allIn >= sticker - 1e-9,
                    t.getName() + ": the all-in price is BELOW the sticker price");

            if (allIn > sticker + .5) {
                twoPrices++;
                System.out.printf("  %-30s $%,.0f > $%,.0f%n", t.getName(), sticker, allIn);
            }
        }
        System.out.println("\n  " + twoPrices + " of " + bm.getTemplates().size()
                + " cost more than their sticker price on month one");

        /* -----------------------------------------------------------------
           THE RECEIPT SERIAL COUNTS

           The indicator flashes when the serial differs from the one the window
           last showed, which is the whole reason it is a serial and not a
           comparison of the receipt's contents: build the same thing twice and
           every field is identical. So the serial has to actually move on a
           second identical build, and NOT move on a refused one.
           ----------------------------------------------------------------- */
        System.out.println("\n=== THE RECEIPT SERIAL ===");

        BuildingsTemplate house = null;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getName().equals("House")) house = t;
        }
        check(house != null, "the House template is missing");

        int before = game.getReceiptSerial();
        game.buildStack(house, 1, false);
        int first = game.getReceiptSerial();
        game.buildStack(house, 1, false);
        int second = game.getReceiptSerial();

        System.out.println("  serial: " + before + " -> " + first + " -> " + second);
        check(first == before + 1, "a build did not move the receipt serial");
        check(second == first + 1,
                "an IDENTICAL second build did not move the serial - the indicator"
                + " would treat it as already seen");
        check(game.getBuildQuantity() == 1, "the receipt lost the quantity");

        // A refusal is not a receipt. Ordering a hundred thousand houses fails
        // on cash, and the indicator must not flash for something that did not
        // happen.
        int beforeRefusal = game.getReceiptSerial();
        game.buildStack(house, 1_000_000, false);
        System.out.println("  after a refused order: " + game.getReceiptSerial());
        check(game.getReceiptSerial() == beforeRefusal,
                "a REFUSED build moved the receipt serial - the dot would flash for"
                + " a purchase that never happened");
        check(!game.hasNewReceipt(), "a refused build left a receipt standing");

        /* -----------------------------------------------------------------
           THE BUILD TAB'S PAGES (0.7.24)

           Jerus: "when you start the game you start in residential so the
           player without reading thinks he needs to building houses", and
           "Yes, rename them". Build opens on an Overview now, and the strip
           after it is the model's list (BuildAdvice.categories(), which
           BuildAdviceCheck holds: fourteen, every building in one) under the
           new names. Nothing above this section named a category, so none of
           it moved; this holds the window to the model, labels and order.
           ----------------------------------------------------------------- */
        System.out.println("\n=== THE BUILD TAB'S PAGES ===");
        List<String> pages = ui.buildPages();
        System.out.println("  " + pages);
        check(!pages.isEmpty() && pages.get(0).equals(BuildAdvice.OVERVIEW),
                "Build's first page is " + (pages.isEmpty() ? "nothing" : pages.get(0)) + ", not the Overview");
        check(BuildAdvice.OVERVIEW.equals(ui.buildOpensOn()),
                "a fresh window opens Build on " + ui.buildOpensOn() + ", not the Overview");
        List<BuildAdvice.Category> model = BuildAdvice.categories();
        check(pages.size() == model.size() + 1,
                "the strip has " + (pages.size() - 1) + " categories and the model " + model.size());
        for (int i = 0; i < model.size() && i + 1 < pages.size(); i++) {
            check(pages.get(i + 1).equals(model.get(i).name()),
                    "the strip's place " + (i + 1) + " reads " + pages.get(i + 1) + ", the model's " + model.get(i).name());
        }
        for (String old : new String[] {"Residential", "Commercial", "Industrial", "Infrastructure", "Services"}) {
            check(!pages.contains(old), "the strip still reads " + old);
        }

        System.out.println();
        if (failures == 0) {
            System.out.println("All checks passed");
        } else {
            System.out.println(failures + " check(s) failed");
        }
    }
}
