package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Actual reference expenditure and purchase coverage, using the pre-correction API.
 * WHY: a component-count check cannot show the omitted household money or the
 * unsupported luxury truncation. This finding can run unchanged on old production.
 */
public final class CpiReferenceFinding {
    static int fails;
    static final PrintStream OUT = System.out;
    record Town(Game game, GameFiles files) {}

    static void check(String label, boolean ok) {
        OUT.println((ok ? "OK   " : "FAIL ") + label);
        if (!ok) fails++;
    }

    static void close(String label, double actual, double expected) {
        boolean ok = Double.isFinite(actual) && Double.isFinite(expected)
                && Math.abs(actual - expected) <= 1e-10 * Math.max(1, Math.abs(expected));
        check(label + " [" + actual + " / " + expected + "]", ok);
    }

    static void quietly(Runnable action) {
        PrintStream before = System.out;
        try (PrintStream sink = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(sink);
            action.run();
        } finally { System.setOut(before); }
    }

    static void referenceWeights() {
        OUT.println("--- reference expenditure, including high luxury spending ---");
        PriceIndex p = new PriceIndex();
        double[] prices = new double[PriceIndex.COMPONENTS], spent = new double[PriceIndex.COMPONENTS];
        Arrays.fill(prices, 1);
        spent[PriceIndex.GROCERIES] = 50;
        spent[PriceIndex.RENT] = 30;
        spent[PriceIndex.MEALS] = 10;
        spent[PriceIndex.LUXURY] = 40;
        double[] fees = { 1, 1, 1, 1 }, feeSpend = { 5, 3, 2, 0 };
        for (int m = 1; m <= PriceIndex.SETTLING_MONTHS; m++) p.takeMonth(prices, spent, fees, feeSpend, m);
        check("fixture: actual reference luxury spending exceeds the former fifteen percent truncation", 40.0 / 140 > .15 && p.isBased());
        close("luxury receives its actual reference expenditure share", p.getWeight(PriceIndex.LUXURY), 40.0 / 140);
        double before = p.getIndex();
        prices[PriceIndex.LUXURY] *= 1.1;
        p.takeMonth(prices, spent, fees, feeSpend, PriceIndex.SETTLING_MONTHS + 1);
        close("a luxury-only price rise has its full observed basket effect", p.getIndex() / before - 1, .1 * 40 / 140);
    }

    /** CarCheck's caused buyer premise, on real scratch files, with operating filling stations. */
    static Town actualTown() { return actualTown(Founding.defaults(), "cpi-reference"); }

    static Town actualTown(Founding founding, String label) {
        GameFiles files = GameFiles.scratch(label);
        Game g = new Game(files);
        quietly(() -> {
            g.newGame(founding);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = g.getBuildingManager();
            String[] names = { "House", "Convenience Store", "Small Grocery Store", "Paved Road",
                    "Coal Power Plant", "Water Treatment Plant", "Construction Depot", "Filling Station" };
            int[] counts = { 900, 20, 6, 60, 1, 1, 4, 2 };
            for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
            g.simulateMonths(24);
            for (Household c : g.getHouseholdBalance().cells()) {
                if (c.isEmpty()) continue;
                c.debt = 0;
                c.shutOut = 0;
                c.savings = HouseholdBalance.SHARE_CUSHION_MONTHS * Math.max(0, c.disposable)
                        + 2 * g.getMarkets().get(Good.CARS).importPrice();
            }
            g.simulateMonths(1); // Make the households' actual plans solvent before causing buyers.
        });
        return new Town(g, files);
    }

    static double covered(Game g, String name) {
        for (int k = 0; k < PriceIndex.COMPONENT_NAMES.length; k++)
            if (name.equals(PriceIndex.COMPONENT_NAMES[k])) return g.indexSpends()[k];
        return 0;
    }

    static double coveredPrice(Game g, String name) {
        for (int k = 0; k < PriceIndex.COMPONENT_NAMES.length; k++)
            if (name.equals(PriceIndex.COMPONENT_NAMES[k])) return g.indexPrices()[k];
        return 0;
    }

    static void actualPurchases(Town town) {
        OUT.println("--- actual car and petrol purchases, cash and physical sources ---");
        Game g = town.game();
        HouseholdBalance homes = g.getHouseholdBalance();
        GoodsMarket cars = g.getMarkets().get(Good.CARS);
        // Half the adoption's bill in spare cash; ample actual credit room, with the model's deposit test intact.
        for (Household c : homes.cells()) {
            if (c.isEmpty()) continue;
            c.cars = 0;
            c.debt = 0;
            c.shutOut = 0;
            c.disposable = Math.max(c.disposable, cars.importPrice());
            c.savings = HouseholdBalance.SHARE_CUSHION_MONTHS * c.disposable
                    + .5 * HouseholdBalance.CAR_ADOPTION * Math.min(cars.getLocalPrice(), cars.importPrice());
        }
        double savings = homes.totalSavings(), debt = homes.totalDebt(), bankCash = g.getBank().getCash();
        double carStockBefore = homes.totalCars();
        int draws = cars.monthsDraws().size();
        quietly(() -> g.getMotoring().month(g));
        double units = 0, bill = 0;
        for (Trade t : cars.monthsDraws().subList(draws, cars.monthsDraws().size())) {
            if (!Trade.HOUSEHOLDS.equals(t.buyer())) continue;
            units += t.units();
            bill += t.value();
        }
        double paid = g.getHouseholdCarSpend(), credit = g.getHouseholdCarCredit();
        check("fixture: actual new cars are bought with positive seller value and financing", units > 0 && paid > 0 && credit > 0);
        close("new-car physical stock closes after existing fleet wear and actual draws", homes.totalCars(),
                carStockBefore - carStockBefore / HouseholdBalance.CAR_LIFE_MONTHS + units);
        close("new-car source bills equal the full seller value", paid, bill);
        close("cash savings plus bank advance fund the complete seller bill", savings - homes.totalSavings() + credit, paid);
        close("the bank pays the financed purchase once", bankCash - g.getBank().getCash(), credit);
        close("household claims add principal and separately booked loan fees", homes.totalDebt() - debt, credit + homes.getCarLoanFees());
        close("internal used-car transfers contribute no new consumption in this fixture", g.getUsedCarSpend(), 0);
        close("CPI covers actual new-car seller expenditure including financed value", covered(g, "new cars"), paid);

        var retail = g.getSectors().retail();
        g.getEconomyManager().getTaxPolicy().setFuelDuty(OilCheck.DUTY);
        double journeys = (2 * Math.max(1, retail.getPumpCapacity()) + 1) / Motoring.LITRES_PER_JOURNEY;
        var priorPending = retail.pending().sold.get(Good.PETROL);
        double pendingPetrol = priorPending == null ? 0 : priorPending.atHome;
        quietly(() -> g.getMotoring().drawFuel(g, journeys));
        double litres = retail.getPumpLitres() + retail.getQueueLitres();
        check("fixture: actual pump and queue purchases include positive fuel duty", retail.getPumpLitres() > 0 && retail.getQueueLitres() > 0 && retail.getFuelDuty() > 0);
        close("physical petrol litres equal the actual household draw", g.getMotoring().getFuelLitres(), litres);
        close("the full petrol bill is tax-inclusive channel prices times actual litres", retail.getFuelBill(),
                retail.getPumpLitres() * retail.getPumpPrice() + retail.getQueueLitres() * retail.getQueuePrice());
        close("CPI covers the actual full petrol bill once", covered(g, "petrol"), g.getMotoring().getFuelBill());
        close("the actual petrol sale adds its bill without duty to the pending source book",
                retail.pending().sold.get(Good.PETROL).atHome - pendingPetrol, retail.getFuelBill() - retail.getFuelDuty());
        var struckPetrol = retail.statement().sold.get(Good.PETROL);
        check("fixture: the struck retail book already contains actual petrol sales", struckPetrol != null && struckPetrol.atHome > 0);
        var currentFood = retail.pending().sold.get(Good.GROCERIES);
        close("CPI groceries cover current completed food receipts without any petrol", g.indexSpends()[PriceIndex.GROCERIES],
                currentFood == null ? 0 : currentFood.atHome);
        check("fixture: current completed food receipts are positive", currentFood != null && currentFood.atHome > 0);
        close("CPI food uses the completed sale's charged price", g.indexPrices()[PriceIndex.GROCERIES], retail.getChargedPrice());
        close("CPI rent covers current completed lease bills", g.indexSpends()[PriceIndex.RENT], g.getSectors().realEstate().getRentIncomeBilled());
        check("the actual non-petrol grocery remainder is nonnegative without clipping", g.indexSpends()[PriceIndex.GROCERIES] >= 0);
    }

    static void actualHomeFuel(Town town) {
        OUT.println("--- actual current home-fuel acquisitions, with separate physical units ---");
        Game g = town.game();
        var retail = g.getSectors().retail();
        var home = g.getHomeFuel();
        check("fixture: Retail is no industrial supplier of JET or thermal coal", !retail.isMaker(Good.JET) && !retail.isMaker(Good.THERMAL_COAL));
        var oil = retail.pending().sold.get(Good.JET);
        var coal = retail.pending().sold.get(Good.THERMAL_COAL);
        double oilBill = oil == null ? 0 : oil.atHome, coalBill = coal == null ? 0 : coal.atHome;
        check("fixture: actual historical homes acquire positive lamp oil and coal", home.getLitres() > 0 && home.getTonnes() > 0 && oilBill > 0 && coalBill > 0);
        close("home lamp-oil physical litres reconcile to the saved Retail source", retail.pending().unitsSold.getOrDefault(Good.JET, 0.0), home.getLitres());
        close("home coal physical tonnes reconcile to the saved Retail source", retail.pending().unitsSold.getOrDefault(Good.THERMAL_COAL, 0.0), home.getTonnes());
        close("the complete home-fuel bill is exactly its two actual seller bills", home.getBill(), oilBill + coalBill);
        close("CPI covers the actual lamp-oil seller bill separately", covered(g, "lamp oil"), oilBill);
        close("CPI covers the actual home-coal seller bill separately", covered(g, "home coal"), coalBill);
        close("lamp-oil CPI price is local money per actual litre", coveredPrice(g, "lamp oil"), oilBill / home.getLitres());
        close("home-coal CPI price is local money per actual tonne", coveredPrice(g, "home coal"), coalBill / home.getTonnes());
        double fees = 0;
        for (double fee : g.indexFeeSpends()) fees += fee;
        double sum = fees;
        double[] expenditures = g.indexSpends();
        for (int k = 0; k < expenditures.length; k++) if (k != PriceIndex.SERVICES) sum += expenditures[k];
        var food = retail.pending().sold.get(Good.GROCERIES);
        double actual = (food == null ? 0 : food.atHome) + g.getSectors().realEstate().getRentIncomeBilled()
                + g.getHouseholdBalance().getMealSpend() + g.getHouseholdBalance().getLuxurySpend()
                + g.getHouseholdCarSpend() + g.getMotoring().getFuelBill() + home.getBill() + fees;
        close("the completed reference expenditure contains both home-fuel bills exactly once", sum, actual);
        double heldBill = home.getBill();
        quietly(() -> g.simulateMonths(1));
        close("the existing next-month household home-fuel payment equals that separate acquisition bill", g.getHouseholds().getHomeFuel(), heldBill);
    }

    public static void main(String[] args) {
        referenceWeights();
        actualPurchases(actualTown());
        actualHomeFuel(actualTown(Founding.defaults().withUnfitted(true), "cpi-home-reference"));
        OUT.println(fails == 0 ? "All CPI reference findings passed." : fails + " CPI reference findings FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
