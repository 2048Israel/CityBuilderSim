package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;

/** Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them). */
public class GdpCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-50s %13.4f  expected %13.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-50s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Where a good stands in NationalAccounts.HELD. */
    static int heldAt(Good g) {
        for (int i = 0; i < NationalAccounts.HELD.length; i++) if (NationalAccounts.HELD[i] == g) return i;
        throw new IllegalStateException(g + " is not in NationalAccounts.HELD");
    }

    public static void main(String[] args) {

        NationalAccounts na = new NationalAccounts();

        /* ==================== 1. the identity ==================== */
        System.out.println("--- GDP = C + I + G + NX ---");

        // retail 400, rent 350, construction 200, stock 1000, government 50,
        // food imports 30, material imports 20
        na.update(400, 350, 200, 1000, 0, 1, 0, 0, 0, 0, 50, 30, 20, 0, 0);

        check("consumption", na.getConsumption(), 750);
        check("investment (200 built + 1000 stock built up)", na.getInvestment(), 1200);
        check("government", na.getGovernment(), 50);
        check("net exports", na.getNetExports(), -50);
        check("GDP", na.getGdp(), 750 + 1200 + 50 - 50);
        check("identity holds",
                na.getGdp(),
                na.getConsumption() + na.getInvestment()
                        + na.getGovernment() + na.getNetExports());

        /* ==================== 2. stock is a CHANGE ==================== */
        System.out.println("\n--- inventories count the change, not the level ---");

        // Same 1,000 of stock still sitting there: that is not this month's output.
        na.update(400, 350, 200, 1000, 0, 1, 0, 0, 0, 0, 50, 30, 20, 0, 0);
        check("unchanged stock adds nothing", na.getInvestmentInventories(), 0);
        check("GDP without the one-off stock build", na.getGdp(), 750 + 200 + 50 - 50);

        // Running the warehouse down is consumption of something made earlier.
        na.update(400, 350, 200, 600, 0, 1, 0, 0, 0, 0, 50, 30, 20, 0, 0);
        check("stock drawn down subtracts", na.getInvestmentInventories(), -400);

        /* ==================== 3. THE BUG THIS REPLACES ==================== */
        System.out.println("\n--- a loss-making city still produces ---");

        // Every business losing money, shops still full, builders still busy.
        // The old figure was wages + profits, so this read as negative output.
        NationalAccounts loss = new NationalAccounts();
        loss.update(400, 350, 200, 0, 0, 1, 0, 0, 0, 0, 50, 30, 20, 0, 0);
        assertTrue("GDP is positive despite sector losses", loss.getGdp() > 0);
        System.out.printf("   GDP $%.2f on a month where every firm lost money%n", loss.getGdp());

        // The only way down is importing more than you make.
        NationalAccounts importer = new NationalAccounts();
        importer.update(10, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 500, 0, 0, 0);
        assertTrue("a city living on imports does read negative", importer.getGdp() < 0);

        /* ======== 3c. the two ways it went negative anyway ======== */
        /*
         * A hand-played city reported monthly GDP of -$446,424 for a hundred
         * months while its shops were full and its builders busy. Both causes
         * are below, and both are arithmetic rather than balance.
         */
        System.out.println("\n--- a bulk order is not negative output ---");

        // update(retail, rent, construction, foodUnits, foodWrittenOff, foodPrice,
        //        matlUnits, matlPrice, govt,
        //        foodImports, matlImports, rawImports, exports)

        // A quiet city: some retail, some rent, a little building, no backlog.
        NationalAccounts bulk = new NationalAccounts();
        bulk.update(400, 350, 200, 0, 0, 1, 0, 2, 0, 0, 50, 0, 0, 0, 0);
        double calm = bulk.getGdp();
        assertTrue("the quiet month is positive", calm > 0);

        /*
         * Now the player orders eight thousand houses. NOTHING IS IMPORTED
         * ON THE ORDER DAY, since 2026-09-11: the crews draw the material as
         * they build (see BuildingsStacks.materialsOwed), so the order month
         * reads exactly like the quiet one. The -$446,424 a hand-played city
         * reported for a century was the old order-day import with nothing
         * against it; the work-in-hand term that used to cancel it is gone
         * with the import it cancelled.
         */
        bulk.update(400, 350, 200, 0, 0, 1, 0, 2, 0, 0, 50, 0, 0, 0, 0);
        System.out.printf("   quiet month $%.0f, bulk-order month $%.0f%n", calm, bulk.getGdp());
        check("ordering is not output, and not an import either", bulk.getGdp(), calm);

        // ...and each month of building imports the material the month's
        // work embodies and puts that work in place: the import is the
        // builders' input and the work is their output, so the month reads
        // what they ADDED. $96,000 of houses built with $60,000 of imported
        // brick is $36,000 of the city's own production, over the quiet month.
        bulk.update(400, 350, 200 + 96_000, 0, 0, 1, 0, 2, 0, 0, 50, 0, 60_000, 0, 0);
        check("a month of building reads the value the builders added",
                bulk.getGdp(), calm + 96_000 - 60_000);
        assertTrue("...which is positive", bulk.getGdp() > calm);

        System.out.println("\n--- the materials plant's shed is stock; the city's yard is not ---");

        /*
         * The city's yard is deliberately NOT an inventory term, and since
         * the sector template (2026-09-11) it is not even a parameter here:
         * a contract already embodies the material the job will consume, so
         * counting the yard as well subtracted the same brick twice - once
         * when the order capitalised it and again when the yard handed it
         * over, months later. Observed in the playtest as Imatl -1,340
         * against Iconstr +1,163 in a month with no trade at all.
         *
         * What IS the third term is the materials plant's own warehouse -
         * output made and not yet sold, measured exactly like a mill's food:
         * a change in VOLUME at today's price, and a price move on the same
         * stock is nothing at all.
         */
        NationalAccounts shed = new NationalAccounts();
        shed.update(400, 350, 200, 0, 0, 1, 0, 2, 0, 0, 50, 0, 0, 0, 0);
        double emptyShed = shed.getGdp();
        shed.update(400, 350, 200, 0, 0, 1, 5_000, 2, 0, 0, 50, 0, 0, 0, 0);
        check("a plant filling its shed is production", shed.getInventoryMaterials(), 10_000);
        check("...and GDP rises by exactly that", shed.getGdp(), emptyShed + 10_000);
        shed.update(400, 350, 200, 0, 0, 1, 5_000, 3, 0, 0, 50, 0, 0, 0, 0);
        check("...while a price move on the same stock is nothing",
                shed.getInventoryMaterials(), 0);
        shed.update(400, 350, 200, 0, 0, 1, 0, 3, 0, 0, 50, 0, 0, 0, 0);
        check("...and selling it into a contract runs the term the other way",
                shed.getInventoryMaterials(), -15_000);

        /*
         * AND THE BOUTIQUE'S STOCKROOM, which is the case that was missing
         * (2026-09-17) and the reason a fresh city read GDP of -$8,636k in
         * month 7. A shop that imports three months of luxuries in one month
         * pays the world for them - NX goes down by the landed cost - and the
         * city still HAS them, so the same number has to come back as
         * inventory or the month reads as a collapse in output.
         *
         * The numbers are one Boutique: 960 pieces at $9, which is the figure
         * the playtest actually reported.
         */
        NationalAccounts boutique = new NationalAccounts();
        boutique.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        boutique.update(0, 0, 0, 0, 0, 0, 0, 0, 960, 9, 0, 0, 0, 8_640, 0);
        check("a shop stocking its first stockroom books it as production",
                boutique.getInventoryLuxuries(), 8_640);
        check("...so a month that is nothing but the import of it is not negative output",
                boutique.getGdp(), 0);

        /*
         * ...and the month it SELLS is the month the margin scores, and only
         * the margin. 320 pieces off the shelf at $14, refilled at $9: the
         * shelf is level, so the inventory term is zero and the city's output
         * is what the shop added, which is 320 x (14 - 9).
         */
        boutique.update(4_480, 0, 0, 0, 0, 0, 0, 0, 960, 9, 0, 0, 0, 2_880, 0);
        check("...and a month of selling and restocking scores the margin alone",
                boutique.getInventoryLuxuries(), 0);
        check("...which is what a retailer adds", boutique.getGdp(), 1_600);

        /*
         * ...and running the stockroom down is consumption of something an
         * earlier month produced, exactly as the food and materials terms are.
         */
        boutique.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0);
        check("...and running it down runs the term the other way",
                boutique.getInventoryLuxuries(), -8_640);

        /*
         * AND EVERY OTHER GOOD A SECTOR HOLDS (0.7.58, batch J1c): the fifth
         * term. Seed 15 of the ensemble's import shock read GDP of -16,056 in
         * month 637 - raw imports of 127,175 against GDP of about 120,000 -
         * and the month's import was a new railway's fleet: twenty sets of
         * rolling stock, 90,460, paid to the world with nothing saying the
         * railway still had them. The fixJ1b notes blamed the mills stocking
         * ore; there is no ore stock (iron is not stockable). The numbers
         * below are that month's: twenty sets at 4,523.
         */
        System.out.println("\n--- a fleet bought abroad is stock held, not negative output ---");

        int rs = heldAt(Good.ROLLING_STOCK);
        double setPrice = 90_460.0 / 20;
        double[] heldPrice = new double[NationalAccounts.HELD.length];
        heldPrice[rs] = setPrice;
        NationalAccounts fleet = new NationalAccounts();
        fleet.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new double[NationalAccounts.HELD.length], heldPrice);
        double[] twenty = new double[NationalAccounts.HELD.length];
        twenty[rs] = 20;
        fleet.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 90_460, 0, twenty, heldPrice);
        check("a railway taking delivery of its fleet books it as stock held", fleet.getInventoryHeld(), 90_460);
        check("...so a month of stocking up on imported inputs is not negative output", fleet.getGdp(), 0);
        assertTrue("...not negative at all", fleet.getGdp() >= 0);
        check("...and the identity holds", fleet.getGdp(),
                fleet.getConsumption() + fleet.getInvestment() + fleet.getGovernment() + fleet.getNetExports());

        /*
         * THE PRODUCTION SIDE AGREES. Production is what was made less what
         * was used up: inputs bought, less what of them went into stock. The
         * delivery month made nothing and used nothing - 90,460 bought,
         * 90,460 of it held - so its value added is zero, the figure above.
         *
         * The next month the railway hauls a mill's steel out: the mill ships
         * 2,940 and pays the railway 300 for the haulage, and the railway's
         * fleet wears a 240th of itself (sectors.Rail.SET_LIFE_MONTHS). The
         * mill adds 2,940 - 300; the railway adds its 300 less the wear it
         * used; the expenditure side is the export and the fall in stock held.
         */
        double[] worn = twenty.clone();
        double wear = 20 / ham.citybuildersim.sectors.Rail.SET_LIFE_MONTHS;
        worn[rs] = 20 - wear;
        fleet.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2_940, worn, heldPrice);
        double haulage = 300;
        double production = (2_940 - haulage) + (haulage - wear * setPrice);
        check("a month of hauling: the fleet's wear is its use", fleet.getInventoryHeld(), -wear * setPrice);
        check("...and GDP by expenditure is what the mill and the railway added", fleet.getGdp(), production);

        // ...and a save from before the term sets the baseline rather than
        // booking every fleet in the city as one month's output.
        NationalAccounts older = new NationalAccounts();
        older.restoreHeld(null);
        older.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, twenty, heldPrice);
        check("an older save's first month books no change in the goods held", older.getInventoryHeld(), 0);
        older.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, worn, heldPrice);
        check("...and the month after measures against it", older.getInventoryHeld(), -wear * setPrice);

        /*
         * A YARD SOLD BACK FULL IS WRITTEN OFF, NOT UNPRODUCED (0.7.133, batch
         * I8). The ensemble's first run with copper read GDP of -305,164 in
         * month 899 of seed 5: Copper Refining had sold back a Scrap Copper
         * Refinery the month before with 9,600 t of cathode in its yard, the
         * yard went with the works (Sector.produceStock() reports the units it
         * cannot hold as Output.writtenOff), and the fifth term booked the
         * fall as negative investment. The units below are that yard's, at
         * cathode's landed price from abroad.
         */
        System.out.println("\n--- a works sold back with its yard full is a loss of assets, not negative output ---");
        int cu = heldAt(Good.COPPER);
        double cathode = 9_600, tonne = Good.COPPER.worldImportPrice();
        double[] cuPrice = new double[NationalAccounts.HELD.length];
        cuPrice[cu] = tonne;
        double[] none = new double[NationalAccounts.HELD.length], yardFull = new double[NationalAccounts.HELD.length];
        yardFull[cu] = cathode;
        NationalAccounts yard = new NationalAccounts();
        yard.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, none, cuPrice);
        yard.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, yardFull, cuPrice);
        check("fixture: the yard filled is output held", yard.getInventoryHeld(), cathode * tonne);
        yard.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, none, yardFull, cuPrice);
        check("the month the works goes with its yard, the write-off is added back: no change in stock held",
                yard.getInventoryHeld(), 0);
        assertTrue("...so the month is not negative output", yard.getGdp() >= 0);
        check("...and the accounts keep what was written off, in units", yard.getHeldWrittenOff()[cu], cathode);
        check("...and measure next month from what was left, none", yard.getLastHeldUnits()[cu], 0);
        yard.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, none, cuPrice);
        check("...so the month after books no change either", yard.getInventoryHeld(), 0);
        // ...and the same fact the other way up: a yard SOLD is a fall in stock, matched by the sale.
        NationalAccounts sold = new NationalAccounts();
        sold.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, yardFull, cuPrice);
        sold.update(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, cathode * tonne, none, cuPrice);
        check("a yard exported is still the fall in stock it is", sold.getInventoryHeld(), -cathode * tonne);
        check("...matched by the export, so the month adds nothing", sold.getGdp(), 0);

        /*
         * EVERY TERM ADDS ITS WRITE-OFF BACK (0.7.136, batch FX1). I8 gave
         * HELD's goods the rule; food's own add-back had read a row the strike
         * had already cleared, and the materials and the luxuries had none.
         * A mill's warehouse, a materials plant's shed and a stockroom, each
         * filled and then written off with the works that held it: the month
         * it goes books no change in that term, and the next measures from
         * what is left. The quantities are each building's own room
         * (BuildingsTemplate.stocks()) - a Bakery's 996,000 kg of bread, a
         * Construction Materials Plant's 480 units, a Boutique's 960 pieces -
         * at a price of one, so the term reads in units.
         */
        System.out.println("\n--- a mill's warehouse, a plant's shed and a stockroom written off are a loss of assets, not negative output (0.7.136) ---");
        BuildingManager rooms = new BuildingManager();
        rooms.initializeTemplates();
        double bread = rooms.getTemplateByName("Bakery").stocks(Good.BREAD);
        double brick = rooms.getTemplateByName("Construction Materials Plant").stocks(Good.MATERIALS);
        double pieces = rooms.getTemplateByName("Boutique").stocks(Good.LUXURIES);
        assertTrue("fixture: each of the three holds something", bread > 0 && brick > 0 && pieces > 0);
        double[] noHeld = new double[NationalAccounts.HELD.length];
        // update(retail, rent, construction, foodUnits, foodWrittenOff, foodPrice, matlUnits, matlWrittenOff, matlPrice,
        //        luxUnits, luxWrittenOff, luxPrice, govt, foodImports, matlImports, rawImports, exports, held, heldWrittenOff, heldPrices)
        NationalAccounts sheds = new NationalAccounts();
        sheds.update(0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, noHeld, noHeld, noHeld);
        sheds.update(0, 0, 0, bread, 0, 1, brick, 0, 1, pieces, 0, 1, 0, 0, 0, 0, 0, noHeld, noHeld, noHeld);
        check("fixture: the warehouse filled is food held", sheds.getInventoryFood(), bread);
        check("fixture: ...the shed filled, materials held", sheds.getInventoryMaterials(), brick);
        check("fixture: ...and the stockroom filled, luxuries held", sheds.getInventoryLuxuries(), pieces);
        sheds.update(0, 0, 0, 0, bread, 1, 0, brick, 1, 0, pieces, 1, 0, 0, 0, 0, 0, noHeld, noHeld, noHeld);
        check("the month the mill goes with its warehouse, food's write-off is added back: no change in food held",
                sheds.getInventoryFood(), 0);
        check("...the plant with its shed, materials' alike", sheds.getInventoryMaterials(), 0);
        check("...and the shop with its stockroom, luxuries' alike", sheds.getInventoryLuxuries(), 0);
        assertTrue("...so the month is not negative output", sheds.getGdp() >= 0);
        check("...and the accounts keep what each wrote off: the food", sheds.getFoodWrittenOff(), bread);
        check("...the materials", sheds.getMaterialsWrittenOff(), brick);
        check("...and the luxuries", sheds.getLuxuriesWrittenOff(), pieces);
        double[] addedBack = sheds.getWrittenOffAddedBack();
        check("...and what each term added back, at its price", addedBack[0] + addedBack[1] + addedBack[2], bread + brick + pieces);
        sheds.update(0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, noHeld, noHeld, noHeld);
        check("...and the month after books no change in any of them",
                sheds.getInventoryFood() + sheds.getInventoryMaterials() + sheds.getInventoryLuxuries(), 0);
        // ...and the same fact the other way up: a shed SOLD is a fall in stock, matched by the sale.
        NationalAccounts shipped = new NationalAccounts();
        shipped.update(0, 0, 0, 0, 0, 1, brick, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, noHeld, noHeld, noHeld);
        shipped.update(0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, brick, noHeld, noHeld, noHeld);
        check("a shed exported is still the fall in stock it is", shipped.getInventoryMaterials(), -brick);
        check("...matched by the export, so the month adds nothing", shipped.getGdp(), 0);
        // ...and the shorter overloads hand in none, as they always have.
        NationalAccounts older2 = new NationalAccounts();
        older2.update(0, 0, 0, 0, 0, 1, brick, 1, 0, 0, 0, 0, 0, 0, 0);
        older2.update(0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0);
        check("an update with no write-offs handed in reads the shed's fall as before", older2.getInventoryMaterials(), -brick);

        System.out.println("\n--- a price move is not production ---");

        /*
         * 804,000 units of food in the warehouse, unchanged, while the price
         * eases from $1.00 to $0.75.
         *
         * Measured by VALUE that is -$201,000 of output for a warehouse nobody
         * touched. It is a holding loss, and real accounts strip it out with an
         * inventory valuation adjustment for exactly this reason.
         */
        NationalAccounts holding = new NationalAccounts();
        holding.update(400, 350, 200, 804_000, 0, 1.00, 0, 0, 0, 0, 50, 0, 0, 0, 0);
        holding.update(400, 350, 200, 804_000, 0, 0.75, 0, 0, 0, 0, 50, 0, 0, 0, 0);

        check("an unchanged warehouse contributes nothing when the price moves",
                holding.getInvestmentInventories(), 0);
        assertTrue("...and output stays positive", holding.getGdp() > 0);

        // ...while a real change in VOLUME still counts, priced at today's price.
        holding.update(400, 350, 200, 904_000, 0, 0.75, 0, 0, 0, 0, 50, 0, 0, 0, 0);
        check("100,000 more units at $0.75 is $75,000 of production",
                holding.getInvestmentInventories(), 75_000);

        System.out.println("\n--- a legacy save sets the baseline, it does not spend it ---");

        /*
         * A save written before stock was tracked in units carries no baseline.
         * Comparing against zero would book the whole existing warehouse as that
         * month's production - the same class of error, from the load path.
         */
        NationalAccounts loaded = new NationalAccounts();
        loaded.restore(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false);
        loaded.update(400, 350, 200, 804_000, 0, 1.00, 0, 0, 0, 0, 50, 0, 0, 0, 0);
        check("the first month after a legacy load books no inventory swing",
                loaded.getInvestmentInventories(), 0);

        // ...and the month after that measures normally against it.
        loaded.update(400, 350, 200, 810_000, 0, 1.00, 0, 0, 0, 0, 50, 0, 0, 0, 0);
        check("...and the next month measures against it", 
                loaded.getInvestmentInventories(), 6_000);

        // A new city is the opposite case: an empty warehouse is a REAL baseline,
        // so its first month of stock is real production and must be counted.
        NationalAccounts fresh = new NationalAccounts();
        fresh.update(0, 0, 0, 1_000, 0, 1.00, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        check("a new city's first stock IS production",
                fresh.getInvestmentInventories(), 1_000);

        /* ============ 3b. exports, the first the city has ever had ============ */
        System.out.println("\n--- a city that sells something ---");

        NationalAccounts trader = new NationalAccounts();

        // A steel mill: 2,940 of steel shipped out, 2,640 of scrap bought in.
        // Only the 300 of difference is output this city produced.
        trader.update(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2640, 2940);
        check("exports", trader.getExports(), 2940);
        check("raw material imported", trader.getImportsRawMaterial(), 2640);
        check("net exports is the difference", trader.getNetExports(), 300);
        check("...and that is the whole of GDP here", trader.getGdp(), 300);
        assertTrue("a city that exports more than it imports reads positive",
                trader.getNetExports() > 0);

        // Shipping out below the cost of the input is a real thing and must
        // read as the value destruction it is.
        NationalAccounts dumping = new NationalAccounts();
        dumping.update(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3000, 2500);
        assertTrue("selling below the cost of the input subtracts",
                dumping.getNetExports() < 0);

        // All three import lines total.
        NationalAccounts allImports = new NationalAccounts();
        allImports.update(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 30, 20, 50, 0);
        check("every import counted", allImports.getTotalImports(), 100);
        check("...and none of them is output", allImports.getNetExports(), -100);

        /* ==================== 4. annual and per capita ==================== */
        System.out.println("\n--- annualising ---");

        NationalAccounts year = new NationalAccounts();
        for (int m = 0; m < 12; m++) {
            year.update(100, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        check("twelve months of 100", year.getAnnualGdp(), 1200);
        check("trend equals the month when flat", year.getTrendGdp(), 100);
        check("per capita over 100 people", year.getGdpPerCapita(100), 12);
        check("no population -> no divide by zero", year.getGdpPerCapita(0), 0);

        // Only ever the last twelve, even after two years.
        for (int m = 0; m < 12; m++) {
            year.update(200, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        check("annual uses the last twelve only", year.getAnnualGdp(), 2400);

        /* ==================== 5. growth ==================== */
        System.out.println("\n--- growth rates ---");

        NationalAccounts g = new NationalAccounts();
        check("no history -> no growth", g.getMonthlyGrowthAnnualised(), 0);

        g.update(100, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        check("one month -> still nothing to compare", g.getMonthlyGrowthAnnualised(), 0);

        g.update(102, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        // 2% a month compounds to 26.8% a year, not 24%.
        check("2%/mo annualised", g.getMonthlyGrowthAnnualised(), Math.pow(1.02, 12) - 1);
        assertTrue("...which is more than 12x the monthly rate",
                g.getMonthlyGrowthAnnualised() > .02 * 12);

        // Shrinking reads negative.
        g.update(51, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertTrue("a halved month is negative growth", g.getMonthlyGrowthAnnualised() < 0);

        NationalAccounts yoy = new NationalAccounts();
        for (int m = 0; m < 12; m++) yoy.update(100, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        check("year on year needs 13 months", yoy.getYearOnYearGrowth(), 0);
        yoy.update(110, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        check("13th month against the 1st", yoy.getYearOnYearGrowth(), .10);

        /* ==================== 6. government books ==================== */
        System.out.println("\n--- the government's own accounts ---");

        NationalAccounts gov = new NationalAccounts();
        for (int m = 0; m < 12; m++) gov.update(1000, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        // taxes 100/50/75/200, utilities 30, land sold 45, property tax 25;
        // interest 40, capital 500, land bought 60.
        gov.updateGovernment(100, 50, 75, 200, 30, 45, 25, 40, 500, 60);

        check("total revenue", gov.getTotalRevenue(), 100 + 50 + 75 + 200 + 30 + 45 + 25);
        check("land sales are revenue", gov.getLandSales(), 45);
        check("property tax is its own line", gov.getPropertyTax(), 25);
        check("total expenditure", gov.getTotalExpenses(), 40 + 500 + 60);
        check("land bought is an expense", gov.getLandPurchases(), 60);
        check("deficit", gov.getBalance(), 525 - 600);
        assertTrue("a deficit is negative", gov.getBalance() < 0);

        // Trading land does not produce anything, so GDP must not move.
        double before = gov.getGdp();
        gov.updateGovernment(100, 50, 75, 200, 30, 9999, 25, 40, 500, 9999);
        check("a land boom leaves GDP alone", gov.getGdp(), before);
        gov.updateGovernment(100, 50, 75, 200, 30, 45, 25, 40, 500, 60);

        // Annual GDP is 12,000; revenue annualised is 525 * 12 = 6,300.
        check("revenue to GDP", gov.getRevenueToGdp(), (525.0 * 12) / 12000);
        check("debt to GDP", gov.getDebtToGdp(6000), .5);
        check("no debt -> zero", gov.getDebtToGdp(0), 0);

        NationalAccounts empty = new NationalAccounts();
        check("no output -> no ratio blow-up", empty.getDebtToGdp(1000), 0);
        check("...nor for revenue", empty.getRevenueToGdp(), 0);

        /* ==================== 4. THE INTEREST LINE IS REAL ====================

           Jerus, 2026-09-07: "the pie chart works but it doesn't show the
           interest portion." It never could. refreshGovernmentAccounts() runs a
           few lines after finalEconUpdate(), which zeroes the interest field it
           was reading - so the government block was re-struck every month with
           an interest expense of zero, and Pieces.topSlices() drops a zero slice.

           Note which way the failure ran: the LOAD path was correct, because
           rebuildSimulationState() restores the accrual and never calls
           finalEconUpdate(). A reloaded city showed its interest and a played
           one did not - load-path parity wearing its coat inside out. So this
           asserts both halves, and would have failed on either.
           ==================================================================== */
        System.out.println("\n--- the city's interest reaches its own accounts ---");

        java.io.PrintStream out = System.out;
        java.io.PrintStream quiet = new java.io.PrintStream(new java.io.OutputStream() {
            @Override public void write(int b) { }
        });

        GameFiles files = GameFiles.scratch("gdpcheck");
        Game city = new Game(files);
        System.setOut(quiet);
        try {
            city.newGame();
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
            // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
            // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
            city.setCashForTest(Founding.WEALTHY_CASH);
            for (BuildingsTemplate t : city.getBuildingManager().getTemplates()) {
                if (t.getName().equals("Low-Rise Apartments")) city.buildStack(t, 40, true);
                if (t.getName().equals("Small Grocery Store"))  city.buildStack(t, 6, true);
                if (t.getName().equals("Coal Power Plant"))     city.buildStack(t, 1, true);
                if (t.getName().equals("Water Treatment Plant"))city.buildStack(t, 1, true);
                if (t.getName().equals("Paved Road"))           city.buildStack(t, 12, true);
            }
            city.simulateMonths(4);
            // A COUPON bond, not a note. A note is a discount instrument and
            // genuinely has no monthly interest - which is a different thing the
            // screen now says in words rather than showing as a silent zero.
            city.handleMediumBondLogic(2_000_000, 5, 1000.0);
            city.simulateMonths(3);
        } finally { System.setOut(out); }

        NationalAccounts played = city.getEconomyManager().getNationalAccounts();
        double coupon = 0;
        for (Debt d : city.getDebtManager().getDebt()) coupon += d.getMonthlyInterestExpense();

        System.out.printf("   coupon due %,.2f, national accounts say %,.2f%n",
                coupon, played.getInterestExpense());

        assertTrue("fixture: the city really did issue a coupon bond", coupon > 0);
        assertTrue("THE INTEREST REACHES THE ACCOUNTS AT ALL",
                played.getInterestExpense() > 0);
        check("...and it is the coupon, not some other number",
                played.getInterestExpense(), coupon);
        assertTrue("...so it survives Pieces.topSlices(), which drops a zero slice",
                played.getInterestExpense() > 0);

        System.setOut(quiet);
        boolean saved;
        Game back = new Game(files);
        try {
            saved = city.saveGame(1, "gdpcheck city").ok;
            back.loadGameSave(1);
        } finally { System.setOut(out); }

        assertTrue("fixture: saved", saved);
        check("and a reloaded city reports the same interest",
                back.getEconomyManager().getNationalAccounts().getInterestExpense(),
                played.getInterestExpense());

        /*
         * G COUNTS THE SCHOOLS AND TRANSIT (0.7.49, B9). Government
         * consumption is the city's services with no market price, valued at
         * what they cost: the utilities' staff, care and the police counted
         * from the start, the schools never, and transit's bill was paid by
         * nobody. A town with a school and a Bus Network, played until both
         * cost something; G is the five costs, each its service's own figure,
         * as the month before struck them: the accounts are struck at the top
         * of the month, before 6d strikes the month's own.
         */
        GameFiles servedFiles = GameFiles.scratch("gdpcheck-g");
        Game served = new Game(servedFiles);
        System.setOut(quiet);
        try {
            served.newGame();
            served.setCashForTest(Founding.WEALTHY_CASH);
            served.getLandManager().setOwnedSqFt(served.getLandManager().getOwnedSqFt() + 100_000_000L);
            for (BuildingsTemplate t : served.getBuildingManager().getTemplates()) {
                if (t.getName().equals("Low-Rise Apartments")) served.buildStack(t, 40, true);
                if (t.getName().equals("Small Grocery Store"))  served.buildStack(t, 6, true);
                if (t.getName().equals("Coal Power Plant"))     served.buildStack(t, 1, true);
                if (t.getName().equals("Water Treatment Plant"))served.buildStack(t, 1, true);
                if (t.getName().equals("Paved Road"))           served.buildStack(t, 12, true);
                if (t.getName().equals("Elementary School"))    served.buildStack(t, 1, true);
                if (t.getName().equals("Bus Network"))          served.buildStack(t, 1, true);
            }
            served.simulateMonths(6);
        } finally { System.setOut(out); }
        double schools = served.getEducation().getGrossCost(), transitBill = served.getEconomyManager().getTransitBill();
        double others = served.getServicesManager().getUtilitiesHandler().getUtilityPayroll()
                + served.getHealthcare().getGrossCost() + served.getCrime().getGrossCost();
        System.out.printf("   the schools cost %,.2f and transit %,.2f a month%n", schools, transitBill);
        assertTrue("fixture: the town's school and its buses both cost something", schools > 0 && transitBill > 0);
        System.setOut(quiet);
        try { served.simulateMonths(1); } finally { System.setOut(out); }
        check("G counts the schools' and transit's cost",
                served.getEconomyManager().getNationalAccounts().getGovernment(),
                others + schools + transitBill);
        /*
         * EVERY GOOD A SECTOR HOLDS IS IN A TERM (0.7.58). The rule the
         * luxury note in NationalAccounts states - any good a sector can hold
         * and does not consume within the month belongs in the inventory
         * block the day the good is written - held as a check rather than a
         * memory: every good a sector of this city keeps a warehouse of (a
         * stockable good it makes) or a pantry of is the shelf's, materials,
         * luxuries, one of HELD, or the one named and left out (NOT_HELD).
         */
        System.out.println("\n--- every good a sector holds is in the accounts ---");
        java.util.Set<Good> counted = java.util.EnumSet.of(Good.MATERIALS, Good.LUXURIES, NationalAccounts.NOT_HELD);
        counted.addAll(java.util.Arrays.asList(ham.citybuildersim.sectors.Retail.SHELF));
        counted.addAll(java.util.Arrays.asList(NationalAccounts.HELD));
        StringBuilder missed = new StringBuilder();
        int declared = 0;
        for (Sector s : served.getSectors().all()) {
            for (Good gd : Good.values()) {
                boolean holds = (gd.stockable() && s.isMaker(gd)) || s.hasPantry(gd);
                if (!holds) continue;
                declared++;
                if (!counted.contains(gd)) missed.append(' ').append(s.key()).append(':').append(gd);
            }
        }
        System.out.printf("   %d warehouses and pantries declared across the sectors%s%n", declared,
                missed.length() == 0 ? ", every one counted" : "; not counted:" + missed);
        assertTrue("every good a sector holds is in an inventory term, or named as left out", missed.length() == 0);
        // ...and the integrated route's three stockable goods (0.7.123, batch I4; spec-materials 2.3) on HELD's end, in Good's
        // order - the steelworks' coke in the Coke Ovens' yard and the two coals - after crude, so a save's held slots keep their places.
        // ...and since 0.7.131 (batch I7) cement after them, the cement plants' silos, and since 0.7.133 (batch I8) cathode, copper's yards;
        // and since 0.7.140 (batch I10) wood's five after cathode, the mills' yards.
        int n = NationalAccounts.HELD.length;
        assertTrue("HELD ends with the steelworks' coke and the two coals, appended after crude (0.7.123), cement after them (0.7.131),"
                        + " cathode after it (0.7.133) and wood's five after that (0.7.140) - lumber, panels, pulp, paper and pellets -"
                        + " each stockable",
                n >= 11 && NationalAccounts.HELD[n - 11] == Good.CRUDE && NationalAccounts.HELD[n - 10] == Good.MET_COKE
                        && NationalAccounts.HELD[n - 9] == Good.THERMAL_COAL && NationalAccounts.HELD[n - 8] == Good.COKING_COAL
                        && NationalAccounts.HELD[n - 7] == Good.CEMENT && NationalAccounts.HELD[n - 6] == Good.COPPER
                        && NationalAccounts.HELD[n - 5] == Good.LUMBER && NationalAccounts.HELD[n - 4] == Good.PANELS
                        && NationalAccounts.HELD[n - 3] == Good.PULP && NationalAccounts.HELD[n - 2] == Good.PAPER
                        && NationalAccounts.HELD[n - 1] == Good.WOOD_PELLETS
                        && Good.MET_COKE.stockable() && Good.THERMAL_COAL.stockable() && Good.COKING_COAL.stockable()
                        && Good.CEMENT.stockable() && Good.COPPER.stockable() && Good.LUMBER.stockable() && Good.PANELS.stockable()
                        && Good.PULP.stockable() && Good.PAPER.stockable() && Good.WOOD_PELLETS.stockable());
        boolean ovensHold = served.getSectors().heavyIndustry().isMaker(Good.MET_COKE) && Good.MET_COKE.stockable();
        assertTrue("...and Iron & Steel, which keeps a yard of coke, is counted by it", ovensHold && counted.contains(Good.MET_COKE));
        boolean silos = served.getSectors().cementConcrete().isMaker(Good.CEMENT) && Good.CEMENT.stockable();
        assertTrue("...and Cement & Concrete, which keeps a silo of cement, is counted by it (0.7.131)", silos && counted.contains(Good.CEMENT));
        boolean yards = served.getSectors().copperMining().isMaker(Good.COPPER) && served.getSectors().copperRefining().isMaker(Good.COPPER)
                && Good.COPPER.stockable();
        assertTrue("...and Copper Mining and Copper Refining, which keep yards of cathode, are counted by it (0.7.133)",
                yards && counted.contains(Good.COPPER));
        ham.citybuildersim.sectors.WoodProducts mills = served.getSectors().woodProducts();
        boolean woodYards = mills.isMaker(Good.LUMBER) && mills.isMaker(Good.PANELS) && mills.isMaker(Good.PULP) && mills.isMaker(Good.PAPER)
                && mills.isMaker(Good.WOOD_PELLETS);
        assertTrue("...and Wood Products, which keeps yards of lumber, panels, pulp, paper and pellets, is counted by it (0.7.140)",
                woodYards && counted.containsAll(java.util.List.of(Good.LUMBER, Good.PANELS, Good.PULP, Good.PAPER, Good.WOOD_PELLETS)));

        /*
         * ...AND IN A CITY: a railway that buys its fleet abroad. RailCheck's
         * trading town - nine hundred houses and thirty foundries, its railway
         * held so what stands is what the fixture laid - played three years;
         * two spurs laid on their ground, and the month the trains it bought
         * land: the fleet's import is more than the town makes in a month, so
         * with nothing against it the month would read negative. The fleet is
         * in the held term, matched against its import to within its first
         * month's wear and the move in the price a set lands at (the railway
         * carrying its own freight takes the haulage out of that price), and
         * the month is not negative.
         */
        System.out.println("\n--- a city whose railway buys its fleet abroad ---");
        GameFiles railFiles = GameFiles.scratch("gdpcheck-rail");
        Game railTown = new Game(railFiles);
        System.setOut(quiet);
        try {
            railTown.newGame();
            railTown.setCashForTest(Founding.WEALTHY_CASH);
            railTown.getBusinessInvestment().holdSector(Sectors.RAIL);
            railTown.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = railTown.getBuildingManager();
            railTown.buildStack(b.getTemplateByName("House"), 900, true);
            railTown.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            railTown.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            railTown.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            railTown.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            railTown.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            railTown.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            railTown.buildStack(b.getTemplate(HeavyIndustry.ARC_FURNACE), 30, true);
            railTown.simulateMonths(36);
            RailCheck.lay(railTown, "Rail Spur", 2);
            railTown.simulateMonths(1);
        } finally { System.setOut(out); }
        ham.citybuildersim.sectors.Rail rail = railTown.getSectors().rail();
        GoodsMarket sets = railTown.getMarkets().get(Good.ROLLING_STOCK);
        double bought = rail.fleet(), landedThen = EconomyManager.heldPrice(sets);
        System.setOut(quiet);
        try { railTown.simulateMonths(1); } finally { System.setOut(out); }
        NationalAccounts town = railTown.getEconomyManager().getNationalAccounts();
        Sector.Split paid = rail.statement().bought.get(Good.ROLLING_STOCK);
        double imported = paid == null ? 0 : paid.abroad;
        double landedNow = EconomyManager.heldPrice(sets);
        // The units the month's accounts read - the fleet less the month's
        // wear, before the month's clearing bought the wear back - against
        // none the month before, when the track stood with no trains.
        double held = town.getLastHeldUnits()[rs] * landedNow;
        double slack = (bought / ham.citybuildersim.sectors.Rail.SET_LIFE_MONTHS) * landedNow + bought * Math.abs(landedNow - landedThen);
        System.out.printf("   %.1f sets bought abroad for %,.2f; held %,.2f at %,.2f a set; GDP %,.2f (held stock %,.2f, NX %,.2f)%n",
                bought, imported, held, landedNow, town.getGdp(), town.getInventoryHeld(), town.getNetExports());
        assertTrue("fixture: the railway bought its fleet abroad", bought > 0 && imported > 0);
        double without = town.getGdp() - town.getInventoryHeld();
        System.out.printf("   without the held term the month would read %,.2f%n", without);
        assertTrue("fixture: more than the town makes in a month, so with nothing against it the month reads negative", without < 0);
        assertTrue("the fleet's import is matched by the stock it went into, to within its wear and the price's move",
                Math.abs(held - imported) <= slack + 1e-6 * imported);
        assertTrue("...so the month it lands is not negative output", town.getGdp() >= 0);
        check("...and the identity holds in it", town.getGdp(),
                town.getConsumption() + town.getInvestment() + town.getGovernment() + town.getNetExports());

        /*
         * ...AND IN A CITY: a copper refinery retired with its yard full
         * (0.7.133). The railway town's shape without its railway, played
         * three years; one Electrolytic Refinery stood by hand, Copper
         * Refining held so what stands is what the fixture laid, its yard
         * filled to the room it has and a month played so the accounts count
         * it as held; then the refinery retired. The month after, the yard
         * goes with it, written off on its row; the strike at the top of the
         * next keeps those units past the rows it clears, and the accounts
         * struck after it are handed them: the month is not negative, though
         * the yard is worth more than the town makes, so booked as a fall in
         * stock it would be.
         */
        System.out.println("\n--- a city whose copper refinery goes with its yard full ---");
        GameFiles cuFiles = GameFiles.scratch("gdpcheck-copper");
        Game cuTown = new Game(cuFiles);
        BuildingsTemplate refinery;
        ham.citybuildersim.sectors.CopperRefining refiners;
        double room, kept, lost = 0;
        boolean cuSaved;
        Game cuBack = new Game(cuFiles);
        System.setOut(quiet);
        try {
            cuTown.newGame();
            cuTown.setCashForTest(Founding.WEALTHY_CASH);
            cuTown.getBusinessInvestment().holdSector(Sectors.COPPER_REFINING);
            cuTown.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = cuTown.getBuildingManager();
            refinery = b.getTemplate(ham.citybuildersim.sectors.CopperRefining.ELECTROLYTIC_REFINERY);
            cuTown.buildStack(b.getTemplateByName("House"), 900, true);
            cuTown.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            cuTown.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            cuTown.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            cuTown.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            cuTown.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            cuTown.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            cuTown.simulateMonths(36);
            cuTown.buildStack(refinery, 1, true);
            refiners = cuTown.getSectors().copperRefining();
            room = refiners.getStockCapacity(Good.COPPER);
            refiners.setStock(Good.COPPER, room);
            cuTown.simulateMonths(1);
            kept = refiners.getStock(Good.COPPER);
            cuTown.getBuildingManager().retire(refinery, 1);
            cuTown.simulateMonths(1);
            for (Sector s : cuTown.getSectors().all()) {
                Sector.Output row = s.outputRow(Good.COPPER);
                if (row != null) lost += row.writtenOff;
            }
            cuSaved = cuTown.saveGame(1, "gdpcheck copper").ok;
            cuBack.loadGameSave(1);
            cuTown.simulateMonths(1);
            cuBack.simulateMonths(1);
        } finally { System.setOut(out); }
        NationalAccounts cuAcc = cuTown.getEconomyManager().getNationalAccounts();
        double cuLanded = EconomyManager.heldPrice(cuTown.getMarkets().get(Good.COPPER));
        double booked = cuAcc.getGdp() - lost * cuLanded;
        System.out.printf("   a yard of %,.0f t, %,.0f t of it kept a month; %,.0f t written off at %,.2f a tonne;"
                        + " GDP %,.2f (held stock %,.2f) - booked as a fall in stock it would read %,.2f%n",
                room, kept, lost, cuLanded, cuAcc.getGdp(), cuAcc.getInventoryHeld(), booked);
        assertTrue("fixture: the refinery went with cathode in its yard, and the yard was written off",
                room > 0 && kept > 0 && lost > 0 && refiners.getStock(Good.COPPER) == 0);
        check("the strike kept the units its rows wrote off", refiners.writtenOffStruck(Good.COPPER), lost);
        check("the accounts were handed the units written off", cuAcc.getHeldWrittenOff()[cu], lost);
        assertTrue("fixture: the yard is worth more than the town makes in a month, so booked as a fall in stock the month reads negative",
                booked < 0);
        assertTrue("...so the month the works goes is not negative output", cuAcc.getGdp() >= 0);
        check("...and the identity holds in it", cuAcc.getGdp(),
                cuAcc.getConsumption() + cuAcc.getInvestment() + cuAcc.getGovernment() + cuAcc.getNetExports());
        // ...and across a save taken in the month the yard went: the rows are not saved, the units written off are.
        NationalAccounts cuBackAcc = cuBack.getEconomyManager().getNationalAccounts();
        assertTrue("fixture: saved in the month the yard was written off", cuSaved && lost > 0);
        check("a city loaded from that save hands its accounts the same units", cuBackAcc.getHeldWrittenOff()[cu], lost);
        check("...and strikes the same GDP", cuBackAcc.getGdp(), cuAcc.getGdp());
        try (var walk = java.nio.file.Files.walk(cuFiles.getDirectory())) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { java.nio.file.Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }

        sheds(quiet, out);

        try (var walk = java.nio.file.Files.walk(railFiles.getDirectory())) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { java.nio.file.Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }

        try (var walk = java.nio.file.Files.walk(servedFiles.getDirectory())) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { java.nio.file.Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }

        try (var walk = java.nio.file.Files.walk(files.getDirectory())) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { java.nio.file.Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /*
     * ...AND IN A CITY (0.7.136, batch FX1): a town's bakeries and materials
     * plants retired with their sheds full. The copper town's shape - nine
     * hundred houses, the shops, roads, power, water and four depots - played
     * three years with the bakers (Industry) and the materials plants
     * (Materials) held, so what stands of theirs is what the fixture lays:
     * as many Industrial Bakeries, and as many Construction Materials Plants,
     * as hold twice the month's GDP in their sheds at the city's prices,
     * stood by hand, filled to their room and a month played so the accounts
     * count it held; then every one retired. The month after, the sheds go
     * with them, written off on their rows; the strike at the top of the
     * next keeps the units past the rows it clears, and the accounts struck
     * after it are handed them - food's at the fixed weights its volume is
     * struck at, the materials in units: the month is not negative, though
     * booked as a fall in stock it would be. NO SECTOR MAKES LUXURIES, so in
     * a city nothing writes them off (Sector.produceStock() is the only
     * write-off, and a boutique's shelf is a pantry): the boutiques' row is
     * given a Boutique's stockroom by hand in the same month, as a maker's
     * warehouse sold back would write it, to show the term is handed it.
     * Saved in that month, a city loaded from the save hands its accounts
     * the same.
     */
    static void sheds(java.io.PrintStream quiet, java.io.PrintStream out) {
        System.out.println("\n--- a city whose bakeries and materials plants go with their sheds full (0.7.136) ---");
        GameFiles files = GameFiles.scratch("gdpcheck-sheds");
        Game town = new Game(files);
        Game back = new Game(files);
        Good[] shelf = ham.citybuildersim.sectors.Retail.SHELF;
        double[] lostFood = new double[shelf.length];
        double lostBrick = 0, pieces = 0, gdpThen = 0, foodHeld = 0, brickHeld = 0;
        int bakeries = 0, plants = 0;
        boolean saved;
        Sector bakers, makers;
        System.setOut(quiet);
        try {
            town.newGame();
            town.setCashForTest(Founding.WEALTHY_CASH);
            town.getBusinessInvestment().holdSector(Sectors.INDUSTRY);
            town.getBusinessInvestment().holdSector(Sectors.MATERIALS);
            town.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = town.getBuildingManager();
            BuildingsTemplate bakery = b.getTemplateByName("Industrial Bakery");
            BuildingsTemplate plant = b.getTemplateByName("Construction Materials Plant");
            town.buildStack(b.getTemplateByName("House"), 900, true);
            town.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            town.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            town.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            town.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            town.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            town.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            town.simulateMonths(36);
            bakers = town.getSectors().industry();
            makers = town.getSectors().materials();
            gdpThen = town.getEconomyManager().getNationalAccounts().getGdp();
            // A shed's worth at the city's prices, and as many as hold twice the month's GDP.
            double bakeryWorth = 0;
            for (Good g : shelf) {
                if (bakery.makes(g) > 0) bakeryWorth += bakery.stocks(g) * town.getMarkets().get(g).getLocalPrice();
            }
            double plantWorth = plant.stocks(Good.MATERIALS) * town.getMarkets().get(Good.MATERIALS).getLocalPrice();
            bakeries = (int) Math.ceil(2 * Math.max(gdpThen, 0) / bakeryWorth);
            plants = (int) Math.ceil(2 * Math.max(gdpThen, 0) / plantWorth);
            town.buildStack(bakery, bakeries, true);
            town.buildStack(plant, plants, true);
            for (Good g : shelf) if (bakers.isMaker(g)) bakers.setStock(g, bakers.getStockCapacity(g));
            makers.setStock(Good.MATERIALS, makers.getStockCapacity(Good.MATERIALS));
            town.simulateMonths(1);
            for (Good g : shelf) foodHeld += bakers.getStock(g);
            brickHeld = makers.getStock(Good.MATERIALS);
            b.retire(bakery, bakeries);
            b.retire(plant, plants);
            town.simulateMonths(1);
            for (Sector s : town.getSectors().all()) {
                for (int i = 0; i < shelf.length; i++) {
                    Sector.Output row = s.outputRow(shelf[i]);
                    if (row != null) lostFood[i] += row.writtenOff;
                }
                Sector.Output row = s.outputRow(Good.MATERIALS);
                if (row != null) lostBrick += row.writtenOff;
            }
            // ...and a stockroom's write-off on the boutiques' row, by hand: what no sector's month writes today.
            pieces = b.getTemplateByName("Boutique").stocks(Good.LUXURIES);
            town.getSectors().luxuryRetail().output(Good.LUXURIES).writtenOff = pieces;
            saved = town.saveGame(1, "gdpcheck sheds").ok;
            back.loadGameSave(1);
            town.simulateMonths(1);
            back.simulateMonths(1);
        } finally { System.setOut(out); }
        NationalAccounts acc = town.getEconomyManager().getNationalAccounts();
        double lostFoodAll = 0;
        for (double v : lostFood) lostFoodAll += v;
        // What the strike kept, summed as EconomyManager sums it - every sector, the shelf's goods at their weights.
        double struckVolume = 0, struckBrick = 0, struckPieces = 0;
        double[] struckFood = new double[shelf.length];
        for (Sector s : town.getSectors().all()) {
            for (int i = 0; i < shelf.length; i++) {
                struckFood[i] += s.writtenOffStruck(shelf[i]);
                struckVolume += s.writtenOffStruck(shelf[i]) * shelf[i].worldImportPrice();
            }
            struckBrick += s.writtenOffStruck(Good.MATERIALS);
            struckPieces += s.writtenOffStruck(Good.LUXURIES);
        }
        double[] added = acc.getWrittenOffAddedBack();
        double booked = acc.getGdp() - added[0] - added[1] - added[2];
        System.out.printf("   %d Industrial Bakeries and %d Construction Materials Plants against GDP %,.2f a month; %,.0f kg of food"
                        + " and %,.0f units of material held a month, %,.0f kg and %,.0f units written off, and %,.0f pieces by hand;"
                        + " added back %,.2f, %,.2f and %,.2f; GDP %,.2f - booked as a fall in stock it would read %,.2f%n",
                bakeries, plants, gdpThen, foodHeld, brickHeld, lostFoodAll, lostBrick, pieces,
                added[0], added[1], added[2], acc.getGdp(), booked);
        assertTrue("fixture: the bakeries went with food in their warehouses, and it was written off",
                foodHeld > 0 && lostFoodAll > 0 && bakers.getStock(Good.BREAD) <= bakers.getStockCapacity(Good.BREAD));
        assertTrue("fixture: ...and the plants with materials in their sheds, written off alike",
                brickHeld > 0 && lostBrick > 0 && makers.getStock(Good.MATERIALS) <= makers.getStockCapacity(Good.MATERIALS));
        boolean keptEach = true;
        for (int i = 0; i < shelf.length; i++) keptEach &= struckFood[i] == lostFood[i];
        assertTrue("the strike kept the food its rows wrote off, good by good", keptEach);
        check("...and the materials", struckBrick, lostBrick);
        check("...and the pieces written off by hand", struckPieces, pieces);
        check("the accounts were handed the food written off, at its fixed weights", acc.getFoodWrittenOff(), struckVolume);
        check("...and the materials, in units", acc.getMaterialsWrittenOff(), lostBrick);
        check("...and the luxuries, in pieces", acc.getLuxuriesWrittenOff(), pieces);
        assertTrue("fixture: the sheds are worth more than the town makes in a month, so booked as a fall in stock the month reads negative",
                booked < 0);
        assertTrue("...so the month the works go is not negative output", acc.getGdp() >= 0);
        check("...and the identity holds in it", acc.getGdp(),
                acc.getConsumption() + acc.getInvestment() + acc.getGovernment() + acc.getNetExports());
        // ...and across a save taken in the month the sheds went: the rows are not saved, the units written off are.
        NationalAccounts backAcc = back.getEconomyManager().getNationalAccounts();
        assertTrue("fixture: saved in the month the sheds were written off", saved && lostFoodAll > 0 && lostBrick > 0);
        check("a city loaded from that save hands its accounts the same food", backAcc.getFoodWrittenOff(), acc.getFoodWrittenOff());
        check("...the same materials", backAcc.getMaterialsWrittenOff(), lostBrick);
        check("...the same luxuries", backAcc.getLuxuriesWrittenOff(), pieces);
        check("...and strikes the same GDP", backAcc.getGdp(), acc.getGdp());
        try (var walk = java.nio.file.Files.walk(files.getDirectory())) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { java.nio.file.Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
