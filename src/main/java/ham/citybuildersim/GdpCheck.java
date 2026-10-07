package ham.citybuildersim;

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
           an interest expense of zero, and budgetPie() drops a zero slice.

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
        assertTrue("...so it survives budgetPie(), which drops a zero slice",
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
            railTown.buildStack(b.getTemplateByName("Steel Foundry"), 30, true);
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
}
