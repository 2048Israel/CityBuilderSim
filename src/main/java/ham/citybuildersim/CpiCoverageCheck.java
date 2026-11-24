package ham.citybuildersim;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Nine-component CPI coverage, comparable prices, and native saved observations.
 * WHY: closing fleets cannot recover purchases, and a pump/queue unit-value average
 * can move with unchanged prices. Exercise both actual transactions and index math.
 */
public final class CpiCoverageCheck {
    private static final double[] FEES = { 2, 1, .5, .1 }, FEE_SPEND = { 5, 3, 2, 0 };
    private static final Pattern SAVED_AT = Pattern.compile("(\"savedAt\"\\s*:\\s*)[0-9]+");

    private static void check(String label, boolean ok) { CpiReferenceFinding.check(label, ok); }
    private static void close(String label, double actual, double expected) { CpiReferenceFinding.close(label, actual, expected); }
    private static void quietly(Runnable action) { CpiReferenceFinding.quietly(action); }

    private static PriceIndex based(double[] prices, double[] spend) {
        PriceIndex p = new PriceIndex();
        for (int m = 1; m <= PriceIndex.SETTLING_MONTHS; m++) p.takeMonth(prices, spend, FEES, FEE_SPEND, m);
        return p;
    }

    private static void priceMath() {
        CpiReferenceFinding.OUT.println("--- comparable transport prices and fixed reference quantities ---");
        Motoring m = new Motoring();
        close("new cars have no invented price before their first paid sale", m.getNewCarPrice(), 0);
        close("petrol has no invented price before its first paid sale", m.getPetrolPrice(), 0);
        m.notePetrolPrices(10, 2, 2, 4, 28);
        double first = m.getPetrolPrice();
        close("first petrol observation anchors at the actual full bill per litre", first, 28.0 / 12);
        m.notePetrolPrices(2, 10, 2, 4, 44);
        close("different pump/queue mix at unchanged channel prices creates no inflation", m.getPetrolPrice(), first);
        var observed = m.petrolPricesForSave();
        m.notePetrolPrices(0, 0, Double.NaN, Double.NaN, 0);
        check("no petrol trade carries both the comparable price and the last observed quantities", observed.equals(m.petrolPricesForSave()));
        m.notePetrolPrices(2, 10, 4, 4, 48);
        close("a pump-only price shock compares the preceding actual channel quantities", m.getPetrolPrice(), first * 48 / 44);
        Motoring back = new Motoring();
        back.restorePetrolPrices(m.petrolPricesForSave());
        m.notePetrolPrices(5, 5, 8, 8, 80);
        back.notePetrolPrices(5, 5, 8, 8, 80);
        check("petrol observation restore prices the next sale identically", m.petrolPricesForSave().equals(back.petrolPricesForSave()));

        double[] prices = { .30, .12, .9, 2, 0, 12, m.getPetrolPrice(), .003, .2 };
        double[] spend = { 50, 30, 10, 40, 0, 20, 5, 10, 15 };
        PriceIndex index = based(prices, spend);
        double baseline = index.getIndex();
        double[] changedSpending = { 500, 3, 1, 4, 0, 200, 50, 100, 150 };
        index.takeMonth(prices, changedSpending, FEES, FEE_SPEND, PriceIndex.SETTLING_MONTHS + 1);
        close("same prices and changed purchases do not change the fixed basket", index.getIndex(), baseline);
        for (int k : new int[] { PriceIndex.NEW_CARS, PriceIndex.PETROL, PriceIndex.LAMP_OIL, PriceIndex.HOME_COAL }) {
            PriceIndex shock = based(prices, spend);
            double[] dearer = prices.clone();
            dearer[k] *= 1.1;
            shock.takeMonth(dearer, spend, FEES, FEE_SPEND, PriceIndex.SETTLING_MONTHS + 1);
            close("isolated " + PriceIndex.COMPONENT_NAMES[k] + " price shock has its reference basket effect",
                    shock.getIndex() - 1, .1 * spend[k] / 190);
            dearer[k] = 0;
            double before = shock.getIndex();
            double[] noTradeSpend = spend.clone();
            noTradeSpend[k] = 0;
            shock.takeMonth(dearer, noTradeSpend, FEES, FEE_SPEND, PriceIndex.SETTLING_MONTHS + 2);
            close("no-trade " + PriceIndex.COMPONENT_NAMES[k] + " carries its last paid price", shock.getIndex(), before);
        }

        double[] absent = prices.clone(), unspent = spend.clone();
        absent[PriceIndex.NEW_CARS] = absent[PriceIndex.PETROL] = absent[PriceIndex.LAMP_OIL] = absent[PriceIndex.HOME_COAL] = 0;
        unspent[PriceIndex.NEW_CARS] = unspent[PriceIndex.PETROL] = unspent[PriceIndex.LAMP_OIL] = unspent[PriceIndex.HOME_COAL] = 0;
        PriceIndex firstLater = based(absent, unspent);
        firstLater.takeMonth(prices, spend, FEES, FEE_SPEND, PriceIndex.SETTLING_MONTHS + 1);
        check("first transport observation after basing has zero weight until the existing link, without a jump",
                firstLater.getWeight(PriceIndex.NEW_CARS) == 0 && firstLater.getWeight(PriceIndex.PETROL) == 0
                        && firstLater.getWeight(PriceIndex.LAMP_OIL) == 0 && firstLater.getWeight(PriceIndex.HOME_COAL) == 0
                        && firstLater.getIndex() == 1 && Double.isFinite(firstLater.getComponentLevel(PriceIndex.PETROL)));
        int due = PriceIndex.SETTLING_MONTHS + PriceIndex.REBASE_MONTHS;
        for (int month = PriceIndex.SETTLING_MONTHS + 2; month <= due; month++)
            firstLater.takeMonth(prices, spend, FEES, FEE_SPEND, month);
        check("new transport expenditures enter only at the unchanged ten-year link",
                PriceIndex.REBASE_MONTHS == 120 && firstLater.getLinkedMonth() == due
                        && firstLater.getWeight(PriceIndex.NEW_CARS) > 0 && firstLater.getWeight(PriceIndex.PETROL) > 0
                        && firstLater.getWeight(PriceIndex.LAMP_OIL) > 0 && firstLater.getWeight(PriceIndex.HOME_COAL) > 0);
        close("the existing link adds coverage with no level jump", firstLater.getIndex(), 1);

        PriceIndex restored = new PriceIndex();
        restored.restore(index.toSaveArray());
        check("all nine component bases, weights, prices, rings and chained levels cross index save", Arrays.equals(index.toSaveArray(), restored.toSaveArray()));
        double scale = .01;
        double[] scaledPrices = prices.clone(), scaledSpending = spend.clone(), scaledFees = FEES.clone(), scaledFeeSpending = FEE_SPEND.clone();
        for (int k = 0; k < scaledPrices.length; k++) { scaledPrices[k] *= scale; scaledSpending[k] *= scale; }
        for (int j = 0; j < scaledFees.length; j++) { scaledFees[j] *= scale; scaledFeeSpending[j] *= scale; }
        restored.redenominate(scale);
        restored.takeMonth(scaledPrices, scaledSpending, scaledFees, scaledFeeSpending, PriceIndex.SETTLING_MONTHS + 2);
        index.takeMonth(prices, spend, FEES, FEE_SPEND, PriceIndex.SETTLING_MONTHS + 2);
        close("redenomination leaves the complete fixed basket index unchanged", restored.getIndex(), index.getIndex());
        var petrol = m.petrolPricesForSave();
        m.redenominateCpi(scale);
        close("petrol's comparable price has nominal money/litre units", m.getPetrolPrice(), petrol.price() * scale);
        check("redenomination preserves petrol's physical comparison quantities", m.petrolPricesForSave().pumpLitres() == petrol.pumpLitres()
                && m.petrolPricesForSave().queueLitres() == petrol.queueLitres());
        m.restorePetrolPrices(null);
        m.restoreNewTrade(null);
        check("absent fresh observations reset carried prices and flows", m.getPetrolPrice() == 0 && m.getNewCarPrice() == 0 && m.getHouseholdCarsBought() == 0);
    }

    private record Guards(Motoring.NewTrade cars, Motoring.PetrolPrices petrol, double fuel, double litres,
                          double savings, double debt, double fleet, double bank, double cash, double loanFees, List<Double> homeFuel) {}

    private static Guards guards(Game g) {
        Motoring m = g.getMotoring();
        return new Guards(m.newTradeForSave(), m.petrolPricesForSave(), m.getFuelBill(), m.getFuelLitres(),
                g.getHouseholdBalance().totalSavings(), g.getHouseholdBalance().totalDebt(), g.getHouseholdBalance().totalCars(),
                g.getBank().getCash(), g.getCash(), g.getBank().getLoanFeesOwed(), List.of(g.getHomeFuel().getHomes(),
                        g.getHomeFuel().getLitres(), g.getHomeFuel().getTonnes(), g.getHomeFuel().getBill(), g.getHomeFuel().getCost(), g.getHomeFuel().getImported()));
    }

    /** The changed current acquisition inputs, whose existing ledgers/observations must also survive a load. */
    private static List<Double> currentInputs(Game g) {
        double[] p = g.indexPrices(), s = g.indexSpends();
        return List.of(p[PriceIndex.GROCERIES], s[PriceIndex.GROCERIES], p[PriceIndex.RENT], s[PriceIndex.RENT],
                p[PriceIndex.NEW_CARS], s[PriceIndex.NEW_CARS], p[PriceIndex.PETROL], s[PriceIndex.PETROL],
                p[PriceIndex.LAMP_OIL], s[PriceIndex.LAMP_OIL], p[PriceIndex.HOME_COAL], s[PriceIndex.HOME_COAL]);
    }

    private static void readsAndHistory(Game g) {
        Guards before = guards(g);
        double[] savedIndex = g.getPriceIndex().toSaveArray();
        HistorySave recorded = new HistorySave();
        for (int i = 0; i < 3; i++) {
            g.indexPrices(); g.indexSpends(); g.indexFees(); g.indexFeeSpends();
            g.getMotoring().newTradeForSave(); g.getMotoring().petrolPricesForSave();
            g.getSectors().realEstate().housingMarket().forecast(g).income(g.getBuildingManager().getTemplateByName("House"), 1);
            recorded.recordMonth(g);
        }
        check("getters, housing forecasts and repeated records preserve exact cash, claims, stocks and saved transport observations", before.equals(guards(g)));
        check("getters, forecasts and records never price another month", Arrays.equals(savedIndex, g.getPriceIndex().toSaveArray()));
        HistorySave copied = new HistorySave();
        copied.restoreFrom(recorded);
        for (int k : new int[] { PriceIndex.NEW_CARS, PriceIndex.PETROL, PriceIndex.LAMP_OIL, PriceIndex.HOME_COAL }) {
            double[] levels = recorded.aligned(HistorySave.indexKey(k)), weights = recorded.aligned(HistorySave.weightKey(k));
            check("history records and exports " + PriceIndex.COMPONENT_NAMES[k] + " levels and weights", levels.length == 3 && weights.length == 3
                    && Double.isFinite(levels[2]) && Double.isFinite(weights[2]));
            check("history copies " + PriceIndex.COMPONENT_NAMES[k] + " levels and weights", Arrays.equals(levels, copied.aligned(HistorySave.indexKey(k)))
                    && Arrays.equals(weights, copied.aligned(HistorySave.weightKey(k))));
        }
    }

    /** Two ordinary complete months distinguish the struck and current acquisition books. */
    private static void normalFuelPeriods(Game g) {
        var retail = g.getSectors().retail();
        var rent = g.getSectors().realEstate();
        double previousNet = g.getMotoring().getFuelBill() - retail.getFuelDuty();
        var previousFood = retail.pending().sold.get(Good.GROCERIES);
        var previousRent = rent.pending().sold.get(Good.HOUSING);
        double foodBill = previousFood == null ? 0 : previousFood.atHome;
        double rentBill = previousRent == null ? 0 : previousRent.atHome;
        // The new fixture month has many more actual drivers; no second sale/strike is called.
        for (Household cell : g.getHouseholdBalance().cells()) if (!cell.isEmpty()) cell.cars = 1;
        g.getEconomyManager().getTaxPolicy().setFuelDuty(0);
        quietly(() -> g.simulateMonths(1));
        var struck = retail.statement().sold.get(Good.PETROL);
        var current = retail.pending().sold.get(Good.PETROL);
        double currentNet = g.getMotoring().getFuelBill() - retail.getFuelDuty();
        check("fixture: normal adjacent months have distinct positive actual fuel acquisitions", previousNet > 0 && currentNet > 0
                && Math.abs(currentNet - previousNet) > 1e-8 * Math.max(1, previousNet));
        close("normal struck petrol sales correspond exactly to the preceding full bill minus its duty", struck == null ? 0 : struck.atHome, previousNet);
        close("normal current pending petrol sales correspond exactly to the current full bill minus duty", current == null ? 0 : current.atHome, currentNet);
        var struckFood = retail.statement().sold.get(Good.GROCERIES);
        var struckRent = rent.statement().sold.get(Good.HOUSING);
        close("normal struck food is the preceding acquisition's food bill", struckFood == null ? 0 : struckFood.atHome, foodBill);
        close("normal struck rent is the preceding acquisition's lease bill", struckRent == null ? 0 : struckRent.atHome, rentBill);
        var currentFood = retail.pending().sold.get(Good.GROCERIES);
        var currentRent = rent.pending().sold.get(Good.HOUSING);
        close("normal CPI food covers only the completed current grocery acquisition", g.indexSpends()[PriceIndex.GROCERIES], currentFood == null ? 0 : currentFood.atHome);
        close("normal CPI rent covers the completed current lease acquisition", g.indexSpends()[PriceIndex.RENT], currentRent == null ? 0 : currentRent.atHome);
        close("normal billed rent getter reconciles to the current saved source book", rent.getRentIncomeBilled(), currentRent == null ? 0 : currentRent.atHome);
        check("fixture: the normal varying-fuel month has actual food and lease bills", currentFood != null && currentFood.atHome > 0 && currentRent != null && currentRent.atHome > 0);
        close("normal CPI food price is the completed charged price before the next shelf repricing", g.indexPrices()[PriceIndex.GROCERIES], retail.getChargedPrice());
        double wrongPeriod = retail.statement().salesToHouseholds - currentNet;
        double sameBook = retail.statement().salesToHouseholds - (struck == null ? 0 : struck.atHome);
        check("the normal varying-fuel fixture rejects subtracting current petrol from prior struck retail sales",
                Math.abs(wrongPeriod - sameBook) > 1e-8 * Math.max(1, previousNet));
    }

    /** Preserve all serialized text except the one wall-clock metadata timestamp. */
    private static String nativeText(GameFiles files, boolean history) throws Exception {
        String raw = Files.readString(history ? files.historyFile(10) : files.saveFile(10), StandardCharsets.UTF_8);
        if (history) return raw;
        var matcher = SAVED_AT.matcher(raw);
        check("native save has exactly one wall-clock savedAt metadata field", matcher.results().count() == 1);
        return SAVED_AT.matcher(raw).replaceFirst(match -> match.group(1) + "0");
    }

    private static void nativeTwins(Game g, GameFiles files, String label) throws Exception {
        quietly(() -> g.saveGame(10, "CPI coverage"));
        String data = nativeText(files, false), history = nativeText(files, true);
        Guards expected = guards(g);
        List<Double> inputs = currentInputs(g);
        Game fresh = new Game(files);
        quietly(() -> fresh.loadGameSave(10));
        check(label + " fresh load preserves exact observed transport flows and money/claim/stock guards", expected.equals(guards(fresh)));
        check(label + " fresh load preserves current food/rent/transport acquisition inputs", inputs.equals(currentInputs(fresh)));
        quietly(() -> fresh.saveGame(10, "CPI coverage"));
        check(label + " fresh load preserves every native data value", data.equals(nativeText(files, false)));
        check(label + " fresh load preserves every native history value", history.equals(nativeText(files, true)));
        Game running = new Game(files);
        quietly(() -> { running.newGame(); running.simulateMonths(2); running.loadGameSave(10); });
        check(label + " load into a running city replaces its old observations and exact guards", expected.equals(guards(running)));
        check(label + " running load preserves current food/rent/transport acquisition inputs", inputs.equals(currentInputs(running)));
        quietly(() -> running.saveGame(10, "CPI coverage"));
        check(label + " running load preserves every native data value", data.equals(nativeText(files, false)));
        check(label + " running load preserves every native history value", history.equals(nativeText(files, true)));
        quietly(() -> { g.simulateMonths(1); fresh.simulateMonths(1); running.simulateMonths(1); });
        check(label + " next-month twins preserve exact transport, cash, claims and stocks", guards(g).equals(guards(fresh)) && guards(g).equals(guards(running)));
        check(label + " next-month twins preserve current food/rent/transport acquisition inputs", currentInputs(g).equals(currentInputs(fresh)) && currentInputs(g).equals(currentInputs(running)));
        quietly(() -> g.saveGame(10, "CPI coverage"));
        data = nativeText(files, false); history = nativeText(files, true);
        quietly(() -> fresh.saveGame(10, "CPI coverage"));
        check(label + " fresh twin next month preserves all native data and history", data.equals(nativeText(files, false)) && history.equals(nativeText(files, true)));
        quietly(() -> running.saveGame(10, "CPI coverage"));
        check(label + " running twin next month preserves all native data and history", data.equals(nativeText(files, false)) && history.equals(nativeText(files, true)));
        check(label + " all three actual next-month money audits close", g.getLastMoneyAudit().relative() < 1e-10
                && fresh.getLastMoneyAudit().relative() < 1e-10 && running.getLastMoneyAudit().relative() < 1e-10);
    }

    static int run() throws Exception {
        int before = CpiReferenceFinding.fails;
        CpiReferenceFinding.referenceWeights();
        priceMath();
        var town = CpiReferenceFinding.actualTown();
        Game g = town.game();
        CpiReferenceFinding.actualPurchases(town);
        double carPrice = g.getMotoring().getNewCarPrice();
        for (Household cell : g.getHouseholdBalance().cells()) cell.shutOut = 1;
        quietly(() -> g.getMotoring().month(g));
        check("an actual month with no eligible car buyer carries the last paid price", g.getHouseholdCarsBought() == 0 && g.getMotoring().getNewCarPrice() == carPrice);
        // Resume a normal month after the isolated transaction finding, with buyers caused as in CarCheck.
        for (Household cell : g.getHouseholdBalance().cells()) {
            if (cell.isEmpty()) continue;
            cell.shutOut = 0;
            cell.cars = 0;
            cell.savings = HouseholdBalance.SHARE_CUSHION_MONTHS * Math.max(0, cell.disposable) + 2 * g.getMarkets().get(Good.CARS).importPrice();
        }
        quietly(() -> g.simulateMonths(1));
        check("native fixture saves nonzero actual new-car and petrol observations", g.getHouseholdCarsBought() > 0
                && g.getMotoring().getFuelLitres() > 0 && g.getMotoring().getNewCarPrice() > 0 && g.getMotoring().getPetrolPrice() > 0);
        readsAndHistory(g);
        nativeTwins(g, town.files(), "nominal");
        normalFuelPeriods(g);
        var cars = g.getMotoring().newTradeForSave();
        var petrol = g.getMotoring().petrolPricesForSave();
        double index = g.getPriceIndex().getIndex();
        boolean[] lopped = { false };
        quietly(() -> lopped[0] = g.reformCurrencyForTest(1000));
        check("fixture: native currency reform succeeds", lopped[0]);
        close("actual new-car paid prices scale with the new denomination", g.getMotoring().getNewCarPrice(), cars.price() / 1000);
        close("actual new-car seller expenditure scales with denomination", g.getHouseholdCarSpend(), cars.spend() / 1000);
        close("actual petrol comparable price scales with denomination", g.getMotoring().getPetrolPrice(), petrol.price() / 1000);
        check("actual reform preserves new-car and petrol physical observations", g.getMotoring().newTradeForSave().units() == cars.units()
                && g.getMotoring().petrolPricesForSave().pumpLitres() == petrol.pumpLitres() && g.getMotoring().petrolPricesForSave().queueLitres() == petrol.queueLitres());
        close("actual reform preserves the index level", g.getPriceIndex().getIndex(), index);
        nativeTwins(g, town.files(), "redenominated");
        var homeTown = CpiReferenceFinding.actualTown(Founding.defaults().withUnfitted(true), "cpi-home-native");
        CpiReferenceFinding.actualHomeFuel(homeTown);
        readsAndHistory(homeTown.game());
        nativeTwins(homeTown.game(), homeTown.files(), "historical home fuels");
        double lampLitres = homeTown.game().getHomeFuel().getLitres(), coalTonnes = homeTown.game().getHomeFuel().getTonnes();
        double homeBill = homeTown.game().getHomeFuel().getBill();
        double lampPrice = homeTown.game().getPriceIndex().getComponentPrice(PriceIndex.LAMP_OIL);
        double coalPrice = homeTown.game().getPriceIndex().getComponentPrice(PriceIndex.HOME_COAL);
        boolean[] homeLopped = { false };
        quietly(() -> homeLopped[0] = homeTown.game().reformCurrencyForTest(1000));
        check("historical home-fuel reform succeeds and preserves physical quantities", homeLopped[0]
                && homeTown.game().getHomeFuel().getLitres() == lampLitres && homeTown.game().getHomeFuel().getTonnes() == coalTonnes);
        close("historical home-fuel acquisition money scales with denomination", homeTown.game().getHomeFuel().getBill(), homeBill / 1000);
        close("saved lamp-oil unit prices scale with denomination", homeTown.game().getPriceIndex().getComponentPrice(PriceIndex.LAMP_OIL), lampPrice / 1000);
        close("saved coal unit prices scale with denomination", homeTown.game().getPriceIndex().getComponentPrice(PriceIndex.HOME_COAL), coalPrice / 1000);
        nativeTwins(homeTown.game(), homeTown.files(), "redenominated historical home fuels");
        quietly(g::newGame);
        check("refounding a running city resets its saved transport observations", g.getMotoring().getNewCarPrice() == 0
                && g.getMotoring().getPetrolPrice() == 0 && g.getHouseholdCarSpend() == 0);
        check("a city with no completed food acquisition invents no first paid food price", g.getSectors().retail().getCurrentGrocerySales() == 0
                && g.indexPrices()[PriceIndex.GROCERIES] == 0);
        PriceIndex lastPaidFood = new PriceIndex();
        double paidFood = 1.7 * g.getSectors().retail().getChargedPrice();
        for (int month = 1; month <= PriceIndex.SETTLING_MONTHS; month++) lastPaidFood.takeMonth(paidFood, .12, 60, 40, month);
        g.getPriceIndex().restore(lastPaidFood.toSaveArray());
        close("with no completed food sale the saved last CPI paid price carries", g.indexPrices()[PriceIndex.GROCERIES], paidFood);
        return CpiReferenceFinding.fails - before;
    }

    public static void main(String[] args) throws Exception {
        int failures = run();
        CpiReferenceFinding.OUT.println(failures == 0 ? "All CPI coverage checks passed." : failures + " CPI coverage checks FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }
}
