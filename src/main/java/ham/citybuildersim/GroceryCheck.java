package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Groceries at a price, the shelf cleared stickily, and food assistance
 * (0.7.43): the households' demand curve, the clearing price, who is handed
 * the baskets a shortage leaves, the shelf's step toward its target, and the
 * treasury's voucher, paid to the cent.
 *
 * WHY. Until 0.7.43 the grocer was handed `want` in money - subsistence and
 * most of what was left, the wealth term with it - so the city asked for
 * fourteen to seventeen times the baskets it could eat, and the shelf's price
 * answered a head count capped by the shops' coverage: no price in the index
 * read anybody's money. The project's spec-inflation.md section 2.6-2.9 is
 * the design; HouseholdBalance (GROCERIES AT A PRICE, AND FOOD ASSISTANCE),
 * Retail.sellOwnPriced() and Retail.repriceShelf() are the mechanics.
 *
 * WHAT THIS HAS TO PROVE
 *
 *   1. A household asks for a basket a head at most, however rich.
 *   2. All of it up to the satiation price, and above it GROCERY_ELASTICITY
 *      less per unit of the price, in logs.
 *   3. Never more than its money buys - and the city's demand falls in the
 *      price, so there is a price that clears.
 *   4. The clearing price is the one at which no more is wanted than the shops
 *      can hand over, found inside its band.
 *   5. A shortage is shared out at the clearing price, so the poorest are
 *      priced out first, every basket paid for at the price charged, and the
 *      cells add up to what was sold.
 *   6. The shelf aims at the clearing price between its floor and CLEARING_CAP
 *      over it, and moves CLEAR_SPEED of the way there a month in logs,
 *      drifting with expected inflation.
 *   7. A shelf under its floor closes FLOOR_CATCH_UP of the gap to the floor
 *      grown a month.
 *   8. Food assistance: a voucher of the dial's share of the baskets at the
 *      price, for a household whose baskets would take more than
 *      FOOD_ASSISTANCE_MEANS_SHARE of its means, paid only on the baskets got,
 *      into its savings - and the poor are handed more of a shortage for it.
 *      The means count the year's investment income, smoothed (0.7.45), so
 *      a single month's coupon does not take the voucher away.
 *   9. In a city: what the vouchers paid is what the treasury paid, on the
 *      budget's line and on the audit's, to the cent, every month, with the
 *      audit closing - and the Food tab's reads (0.7.45): the dial's preview
 *      at rest is what the month paid, the rows add up to the sale, the
 *      priced out and the short of stock to the hunger, and THE BUDGET
 *      previews the dial by the vouchers it would have paid.
 */
public class GroceryCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-92s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        out.printf("%-92s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    /** A bench of the household ledger: one cell of each kind the sections need, by hand. */
    static Household cell(HouseholdBalance hb, FamilyStructure shape, PayTier tier,
                          double households, double foodMoney) {
        Household c = hb.cell(shape, tier);
        c.households = households;
        c.need = c.baskets();
        c.foodMoney = foodMoney;
        return c;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        double floor = ham.citybuildersim.sectors.Retail.OPENING_SELL_PRICE;
        double pSat = ham.citybuildersim.sectors.Retail.SATIATION_MULTIPLE * floor;
        double eps = ham.citybuildersim.sectors.Retail.GROCERY_ELASTICITY;

        /* ================= 1-3. demand at a price ================= */
        out.println("--- a household asks for baskets at a price: a basket a head at most, less above satiation, never past its money ---");

        HouseholdBalance hb = new HouseholdBalance();
        hb.setSatiationPrice(pSat);
        Household rich = cell(hb, FamilyStructure.COUPLE, PayTier.SKILLED, 100, 1e9);
        close("fixture: a couple needs two baskets, one a head", rich.need, 2, 0);
        close("however rich, a household asks for its baskets and no more", hb.groceryDemandOf(rich, floor / 100), 2, 0);
        close("...all of them at the satiation price", hb.groceryDemandOf(rich, pSat), 2, 0);
        close("...and above it, the elasticity's share less: twice the price, 2^-GROCERY_ELASTICITY of them",
                hb.groceryDemandOf(rich, 2 * pSat), 2 * Math.pow(2, -eps), 1e-12);
        double lnSlope = Math.log(hb.groceryDemandOf(rich, 4 * pSat) / hb.groceryDemandOf(rich, 2 * pSat)) / Math.log(2);
        close("...an elasticity of GROCERY_ELASTICITY, in logs", lnSlope, -eps, 1e-12);

        Household poor = cell(hb, FamilyStructure.COUPLE, PayTier.UNSKILLED, 100, .5 * floor);
        close("a household with half a basket's money at the floor asks for half a basket", hb.groceryDemandOf(poor, floor), .5, 1e-12);
        close("...and a quarter at twice the floor: its money is the cap", hb.groceryDemandOf(poor, 2 * floor), .25, 1e-12);
        close("the city's demand is every cell's times its households",
                hb.groceriesWanted(floor), 100 * 2 + 100 * .5, 1e-9);
        boolean falls = true;
        double was = Double.POSITIVE_INFINITY;
        for (double p = floor / 50; p < floor * 50; p *= 1.1) {
            double d = hb.groceriesWanted(p);
            falls &= d <= was;
            was = d;
        }
        assertTrue("...and it never rises with the price: there is a price that clears", falls);
        close("the baskets the city needs are a basket a head", hb.groceriesNeeded(), 100 * 2 + 100 * 2, 0);

        /* ================= 4. the clearing price ================= */
        out.println("\n--- the clearing price: no more wanted than the shops can hand over ---");

        double supply = 230;
        double clearing = ham.citybuildersim.sectors.Retail.clearingPriceOf(hb::groceriesWanted, floor, supply);
        out.printf("   %,.0f baskets to hand over: the clearing price %.6f against a floor of %.4f%n", supply, clearing, floor);
        assertTrue("at the clearing price, no more is wanted than the supply", hb.groceriesWanted(clearing) <= supply);
        assertTrue("...and a hair under it, more is", hb.groceriesWanted(clearing / (1 + 1e-9)) > supply);
        close("a city that wants less than there is, even at the band's bottom, clears there",
                ham.citybuildersim.sectors.Retail.clearingPriceOf(hb::groceriesWanted, floor, 1e9),
                floor / ham.citybuildersim.sectors.Retail.CLEARING_BAND, 1e-15);
        close("...and one that wants more even at its top, or a shop with nothing, at the top",
                ham.citybuildersim.sectors.Retail.clearingPriceOf(hb::groceriesWanted, floor, 0),
                floor * ham.citybuildersim.sectors.Retail.CLEARING_BAND, 1e-12);

        /* ================= 5. the shortage, shared out ================= */
        out.println("\n--- a shortage is shared out at the clearing price: the poorest are priced out first ---");

        double sold = Math.floor(Math.min(hb.groceriesWanted(floor), supply));
        double pAlloc = Math.max(floor, clearing);
        hb.allocateGroceries(sold, pAlloc, floor);
        double handed = rich.groceriesGot() * rich.households() + poor.groceriesGot() * poor.households();
        close("the cells are handed exactly what was sold", handed, sold, 1e-9);
        out.printf("   the rich couple got %.4f of its 2 baskets, the poor couple %.4f; the poor asked %.4f at the price charged%n",
                rich.groceriesGot(), poor.groceriesGot(), poor.groceriesAsked());
        assertTrue("the poor are handed a smaller share of what they need than the rich",
                poor.groceriesGot() / poor.groceriesNeed() < rich.groceriesGot() / rich.groceriesNeed());
        assertTrue("...less than they asked for at the price the shelf charged: priced out by the clearing price",
                poor.groceriesGot() < poor.groceriesAsked());
        close("...each at its demand at the clearing price, scaled to what was sold",
                poor.groceriesGot(), hb.groceryDemandOf(poor, pAlloc) * sold / hb.groceriesWanted(pAlloc), 1e-12);
        close("the sale records what each needed", poor.groceriesNeed(), poor.need, 0);

        /* ================= 6-7. the shelf ================= */
        out.println("\n--- the shelf: the clearing price between the floor and the cap, a sixth of the way a month, drifting ---");

        double monthly = Math.pow(1.03, 1.0 / 12) - 1;
        double theta = ham.citybuildersim.sectors.Retail.CLEAR_SPEED;
        double cost = .10;   // a floor of the opening price: cost-plus is under it
        ham.citybuildersim.sectors.Retail shelf = new ham.citybuildersim.sectors.Retail();
        shelf.setExpected(1, monthly);
        shelf.setStoreSellPrice(1.2 * floor);
        shelf.repriceShelf(cost, 1.2 * floor);
        close("at its target the shelf grows by expected inflation, (1 - CLEAR_SPEED) of it, in logs",
                shelf.getStoreSellPrice(), 1.2 * floor * Math.pow(1 + monthly, 1 - theta), 1e-12);
        ham.citybuildersim.sectors.Retail dear = new ham.citybuildersim.sectors.Retail();
        dear.setExpected(1, 0);
        dear.setStoreSellPrice(floor);
        dear.repriceShelf(cost, 100 * floor);
        close("however short, the target is CLEARING_CAP over the floor", dear.getScarcityMultiple(),
                ham.citybuildersim.sectors.Retail.CLEARING_CAP, 1e-12);
        close("...and the shelf moves CLEAR_SPEED of the way there, in logs", dear.getStoreSellPrice(),
                floor * Math.pow(ham.citybuildersim.sectors.Retail.CLEARING_CAP, theta), 1e-12);
        close("...on a floor of the opening price or the stock's cost plus RETAIL_MARKUP, the greater",
                dear.getFloorPrice(), Math.max(floor, cost * ham.citybuildersim.sectors.Retail.RETAIL_MARKUP), 0);
        ham.citybuildersim.sectors.Retail under = new ham.citybuildersim.sectors.Retail();
        under.setExpected(1, monthly);
        under.setStoreSellPrice(.5 * floor);
        under.repriceShelf(cost, 0);
        close("a shelf under its floor closes FLOOR_CATCH_UP of the gap to the floor grown a month",
                under.getStoreSellPrice(),
                .5 * floor + (floor * (1 + monthly) - .5 * floor) * ham.citybuildersim.sectors.Retail.FLOOR_CATCH_UP, 1e-12);

        /* ================= 8. food assistance, on the bench ================= */
        out.println("\n--- food assistance: the dial's share of the baskets, for those they would take half the means of ---");

        HouseholdBalance aided = new HouseholdBalance();
        aided.setSatiationPrice(pSat);
        aided.setFoodAssistance(.5);
        Household broke = cell(aided, FamilyStructure.COUPLE, PayTier.UNSKILLED, 100, .5 * floor);
        broke.afterFixed = .5 * floor;          // two baskets would take four times half of it
        Household comfortable = cell(aided, FamilyStructure.COUPLE, PayTier.SKILLED, 100, 1e9);
        comfortable.afterFixed = 100 * floor;   // two baskets are a fiftieth of it
        close("a household whose baskets take more than FOOD_ASSISTANCE_MEANS_SHARE of its means holds the dial's share of them",
                aided.foodAssistanceFor(broke, floor), .5 * 2 * floor, 1e-15);
        close("...one whose baskets do not, nothing", aided.foodAssistanceFor(comfortable, floor), 0, 0);
        HouseholdBalance none = new HouseholdBalance();
        close("...and at a dial of 0 nobody does", none.foodAssistanceFor(broke, floor), 0, 0);
        close("...counting investment income in the means: the year's, smoothed", aidedWithIncome(pSat, floor), 0, 0);
        close("...so a single month's coupon - a twelfth of it in the year's - does not take the voucher away (0.7.45, B16)",
                aidedWithACoupon(pSat, floor), .5 * 2 * floor, 1e-15);

        // As plan() strikes it: the voucher added to the money the household takes to the grocer.
        broke.voucher = aided.foodAssistanceFor(broke, floor);
        broke.foodMoney = .5 * floor + broke.voucher;
        double wantedAided = aided.groceriesWanted(floor);
        double clearingAided = ham.citybuildersim.sectors.Retail.clearingPriceOf(aided::groceriesWanted, floor, supply);
        double soldAided = Math.floor(Math.min(wantedAided, supply));
        double savingsBefore = broke.savings;
        aided.allocateGroceries(soldAided, Math.max(floor, clearingAided), floor);
        out.printf("   the broke couple got %.4f baskets with the voucher, %.4f without; the clearing price %.4f with, %.4f without%n",
                broke.groceriesGot(), poor.groceriesGot(), clearingAided, clearing);
        assertTrue("the voucher bids: the poor are handed more of the same shortage", broke.groceriesGot() > poor.groceriesGot());
        assertTrue("...and the price that clears it rises", clearingAided > clearing);
        close("the aid is the voucher, up to the baskets got at the price charged",
                broke.assistance(), Math.min(broke.voucher, broke.groceriesGot() * floor), 1e-15);
        close("...into the household's savings, at the till", broke.savings - savingsBefore, broke.assistance(), 1e-15);
        close("...and what the treasury owes is every cell's, times its households",
                aided.getFoodAssistancePaid(), broke.assistance() * broke.households(), 1e-9);
        close("...the baskets it paid for, at the price charged",
                aided.getFedByAssistance(), broke.assistance() / floor * broke.households(), 1e-9);
        close("the household books split it by the cells paid", aided.foodAssistanceByRow()[broke.row()],
                broke.assistance() * broke.households(), 1e-9);

        inACity(Files.createTempDirectory("grocery"));

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * A household whose investment income lifts its means past twice its
     * baskets holds no voucher. Since 0.7.45 the test reads the income
     * smoothed over MEANS_INCOME_MONTHS (B16), so the fixture is a household
     * that has had it for good: its year's income is the month's.
     */
    static double aidedWithIncome(double pSat, double floor) {
        HouseholdBalance hb = new HouseholdBalance();
        hb.setSatiationPrice(pSat);
        hb.setFoodAssistance(.5);
        Household c = cell(hb, FamilyStructure.COUPLE, PayTier.UNSKILLED, 100, floor);
        c.afterFixed = 0;
        c.investmentIncome = 10 * floor;
        c.meansIncome = 10 * floor;
        return hb.foodAssistanceFor(c, floor);
    }

    /** ...and one that had the same income in one month only - a coupon - which the year's average holds a twelfth of: still a voucher. */
    static double aidedWithACoupon(double pSat, double floor) {
        HouseholdBalance hb = new HouseholdBalance();
        hb.setSatiationPrice(pSat);
        hb.setFoodAssistance(.5);
        Household c = cell(hb, FamilyStructure.COUPLE, PayTier.UNSKILLED, 100, floor);
        c.afterFixed = 0;
        c.investmentIncome = 10 * floor;
        c.meansIncome = 10 * floor / HouseholdBalance.MEANS_INCOME_MONTHS;
        return hb.foodAssistanceFor(c, floor);
    }

    /* =================================================================
       9. IN A CITY: THE TREASURY PAYS, TO THE CENT

       The same founding twice - deterministic, so the twins are one city
       until the dial - played to its basket, then one of them given food
       assistance at half. Every month after: the vouchers the sale paid on
       are what the treasury paid (TreasuryLine.FOOD_ASSISTANCE, a promise -
       nothing owed), what the budget's line carries and what the audit
       declares, to the cent; the audit closes. And over the year, the
       households who held a voucher were handed more of what they need than
       the same households in the twin without one.
       ================================================================= */

    static Game founding(Path root, String label) {
        Game g = new Game(new GameFiles(root.resolve(label), root.resolve("no-legacy")));
        PrintStream real = System.out;
        System.setOut(quiet);
        try {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 50_000_000L);
            for (String[] order : new String[][] {
                    {"House", "300"}, {"Convenience Store", "3"}, {"Bakery", "3"}, {"Construction Depot", "3"},
                    {"Coal Power Plant", "2"}, {"Water Treatment Plant", "1"}, {"Paved Road", "10"} }) {
                g.buildStack(LongPlaytest.template(g, order[0]), Integer.parseInt(order[1]), true);
            }
            g.simulateMonths(PriceIndex.SETTLING_MONTHS + 12);
        } finally {
            System.setOut(real);
        }
        return g;
    }

    /** Whether somebody's voucher at this dial is above nothing and under what their baskets cost at the last sale - the voucher's cap (0.7.62). */
    static boolean underTheCap(Game g, double share) {
        HouseholdBalance hb = g.getHouseholdBalance();
        double price = PolicyPreview.lastSalePrice(g);
        for (Household c : hb.cells()) {
            double v = hb.foodAssistanceFor(c, price, share);
            if (v > 0 && v < c.groceriesGot() * price && c.households() > 0) return true;
        }
        return false;
    }

    static void inACity(Path root) {
        out.println("\n--- in a city: the treasury pays the vouchers the sale took, to the cent, and the audit closes ---");

        Game without = founding(root, "without");
        Game with = founding(root, "with");
        assertTrue("fixture: the twins are one city until the dial",
                with.getCash() == without.getCash()
                        && with.getHouseholdBalance().totalSavings() == without.getHouseholdBalance().totalSavings());
        with.getEconomyManager().getTaxPolicy().setFoodAssistance(.5);

        double paidTotal = 0, worstBudget = 0, worstAudit = 0, worstCells = 0;
        double worstAtRest = 0, worstRowsGot = 0, worstRowsAided = 0, worstHunger = 0;
        int closed = 0, months = 12;
        double gotWith = 0, needWith = 0, gotWithout = 0, needWithout = 0;
        java.util.Set<String> held = new java.util.HashSet<>();
        for (int m = 0; m < months; m++) {
            PrintStream real = System.out;
            System.setOut(quiet);
            try {
                with.simulateMonths(1);
                without.simulateMonths(1);
            } finally {
                System.setOut(real);
            }
            HouseholdBalance hb = with.getHouseholdBalance();
            double paid = hb.getFoodAssistancePaid();
            double cells = 0;
            for (Household c : hb.cells()) cells += c.assistance() * c.households();
            paidTotal += paid;
            worstCells = Math.max(worstCells, Math.abs(cells - paid));
            worstBudget = Math.max(worstBudget, Math.abs(with.getEconomyManager().getFoodAssistance() - paid)
                    + Math.abs(with.getEconomyManager().getNationalAccounts().getFoodAssistance() - paid));
            MoneyAudit.Result r = with.getLastMoneyAudit();
            worstAudit = Math.max(worstAudit, Math.abs(BondCheck.line(r, "- treasury FoodAssistance") - paid));
            // The Food tab's reads (0.7.45): its dial at rest, its rows, its two hungers.
            worstAtRest = Math.max(worstAtRest, Math.abs(PolicyPreview.foodAssistanceAt(with, .5) - paid));
            double rowsGot = 0, rowsAided = 0;
            for (HouseholdBalance.GroceryRow row : hb.groceriesByRow(PolicyPreview.lastSalePrice(with))) {
                rowsGot += row.got();
                rowsAided += row.aided();
            }
            worstRowsGot = Math.max(worstRowsGot, Math.abs(rowsGot - with.getSectors().retail().getProductsSold()));
            worstRowsAided = Math.max(worstRowsAided, Math.abs(rowsAided - hb.getHouseholdsAssisted()));
            worstHunger = Math.max(worstHunger, Math.abs(hb.getHungerPricedOut() + hb.getHungerShortOfStock() - hb.getHungerRate())
                    + Math.max(0, -hb.getHungerShortOfStock()) + Math.max(0, -hb.getHungerPricedOut()));
            if (BondCheck.closes(r)) closed++;
            // Who held a voucher at this sale, and what they and their twins were handed of it.
            for (Household c : hb.cells()) {
                if (!(c.assistance() > 0)) continue;
                held.add(c.key());
                Household twin = byKey(without.getHouseholdBalance(), c.key());
                gotWith += c.groceriesGot() * c.households();
                needWith += c.groceriesNeed() * c.households();
                if (twin != null) {
                    gotWithout += twin.groceriesGot() * twin.households();
                    needWithout += twin.groceriesNeed() * twin.households();
                }
            }
        }
        out.printf("   a year at 50%%: the treasury paid %,.1fk; %d kinds of household held a voucher; they were handed"
                        + " %.1f%% of their baskets, their twins without one %.1f%%%n",
                paidTotal, held.size(), needWith > 0 ? 100 * gotWith / needWith : 0,
                needWithout > 0 ? 100 * gotWithout / needWithout : 0);
        assertTrue("fixture: somebody held a voucher, and it paid", paidTotal > 0 && !held.isEmpty());
        assertTrue("what the treasury owes is the cells' aid, every month, to the cent", worstCells <= .005);
        assertTrue("...what it paid, and the budget's line, every month, to the cent", worstBudget <= .005);
        assertTrue("...and the audit's declared line, every month, to the cent", worstAudit <= .005);
        assertTrue("...a promise: nothing owed for it", !with.getArrearsByLine().containsKey(TreasuryLine.FOOD_ASSISTANCE)
                || with.getArrearsByLine().get(TreasuryLine.FOOD_ASSISTANCE) == 0);
        assertTrue("every month's audit closes", closed == months);
        assertTrue("the households who held a voucher were handed more of what they need than their twins without",
                needWith > 0 && needWithout > 0 && gotWith / needWith > gotWithout / needWithout);
        out.printf("   the Food tab's reads: the dial at rest off the month by at most %.2e; the rows' baskets off the sale by %.2e;"
                + " the hungers' sum off the rate by %.2e%n", worstAtRest, worstRowsGot, worstHunger);
        assertTrue("the dial's preview at the city's own dial is what the month paid: at the price the sale charged (0.7.45)",
                worstAtRest <= 1e-9 * Math.max(1, paidTotal));
        assertTrue("the rows' baskets add up to what the shops sold, every month", worstRowsGot <= 1e-6);
        assertTrue("...and the households paid a voucher, row by row, to the city's count", worstRowsAided <= 1e-9);
        assertTrue("the priced out and the short of stock add up to the hunger, every month", worstHunger <= 1e-12);
        // THE BUDGET previews the dial (0.7.45; the UI spec's B19): the twin
        // without it, whose households the vouchers would reach, set to half.
        //
        // ...AT A MONTH SOMEBODY IS UNDER THE LINE IN BOTH, RE-CAUSED (0.7.62).
        // The year's last sale is one month's means test, and the previews
        // below read it: in batch K's town (the drivers' fuel at the world's
        // price level moved its path) nobody in either twin was under half a
        // dial's line at that sale - the preview read nothing, and "more at a
        // fuller dial" compared nothing with nothing. The twins play on
        // together, a month at a time, until half a dial reaches somebody in
        // the twin without it and, in the twin with it, somebody whose half
        // dial's voucher is under what their baskets cost (else the voucher is
        // its cap at any dial, and a fuller one can pay no more), as both
        // were at 0.7.61's last sale.
        int waited = 0;
        while ((!(PolicyPreview.foodAssistanceAt(without, .5) > 0) || !underTheCap(with, .5)) && waited < 12) {
            PrintStream real = System.out;
            System.setOut(quiet);
            try {
                with.simulateMonths(1);
                without.simulateMonths(1);
            } finally {
                System.setOut(real);
            }
            waited++;
        }
        out.printf("   the previews read %d month(s) after the year, at a sale half a dial reached somebody in both%n", waited);
        assertTrue("fixture: ...and in the twin with it somebody whose half dial's voucher is under what their baskets cost",
                underTheCap(with, .5));
        TaxPolicy dialAtHalf = without.getEconomyManager().getTaxPolicy().copy();
        dialAtHalf.setFoodAssistance(.5);
        double share = without.getEducation().getTuitionSubsidy();
        double vouchers = PolicyPreview.foodAssistanceAt(without, .5);
        assertTrue("fixture: the twin without the dial has households half a dial would pay vouchers to",
                vouchers > 0 && PolicyPreview.foodAssistanceHouseholdsAt(without, .5) > 0);
        close("THE BUDGET's preview of half a dial moves it down by the vouchers it would have paid at the last sale",
                PolicyPreview.change(without, without.getEconomyManager().getTaxPolicy(), dialAtHalf, share, share),
                -vouchers, 1e-9 * vouchers);
        close("...its baskets, those vouchers at the price the sale charged",
                PolicyPreview.foodAssistanceBasketsAt(without, .5), vouchers / PolicyPreview.lastSalePrice(without), 1e-12 * vouchers);
        close("the dial's preview at nothing is nothing", PolicyPreview.foodAssistanceAt(with, 0), 0, 0);
        close("...nobody holds a voucher at it", PolicyPreview.foodAssistanceHouseholdsAt(with, 0), 0, 0);
        assertTrue("...and more at a fuller dial", PolicyPreview.foodAssistanceAt(with, 1) > PolicyPreview.foodAssistanceAt(with, .5));

        /*
         * ...AND JUST LOADED (A4, 0.7.46). The means test reads each
         * household's income after its fixed bills, which the month struck and
         * the save did not carry: a reload struck it again from the rent and
         * the fees at the moment of loading, and the Food tab's count of who a
         * voucher would reach moved with nothing pressed. The count is a line
         * a household is under or over, so most months the two strikes land
         * on the same side of it; the twin without the dial is pressed a year
         * on and saved and loaded every month (on 0.7.45, two of the twelve
         * read households the live city did not), and must count the
         * households the live one does in each.
         */
        double worstReach = 0, leastReach = Double.MAX_VALUE;
        for (int m = 0; m < 12; m++) {
            Game reloaded;
            PrintStream real = System.out;
            System.setOut(quiet);
            try {
                without.simulateMonths(1);
                without.saveGame(1, "without");
                reloaded = new Game(without.getGameFiles());
                reloaded.loadGameSave(1);
            } finally {
                System.setOut(real);
            }
            double reach = PolicyPreview.foodAssistanceHouseholdsAt(without, .5);
            leastReach = Math.min(leastReach, reach);
            worstReach = Math.max(worstReach, Math.abs(PolicyPreview.foodAssistanceHouseholdsAt(reloaded, .5) - reach));
        }
        assertTrue("fixture: every month of the year, half a dial would reach somebody in the twin without it",
                leastReach > 0);
        close("just loaded, the households a voucher would reach are the live city's", worstReach, 0, 1e-9);
    }

    static Household byKey(HouseholdBalance hb, String key) {
        for (Household c : hb.cells()) if (c.key().equals(key)) return c;
        return null;
    }
}
