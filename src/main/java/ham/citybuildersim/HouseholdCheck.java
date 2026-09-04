package ham.citybuildersim;

/**
 * Verifies the residents' books and the demolition log.
 *
 * The household statement is the last missing side of this economy's ledger, so
 * what matters most here is that it is the OTHER SIDE of figures that already
 * exist rather than a second, differently-computed version of them. If the
 * people can be shown paying a different rent from the one landlords are shown
 * receiving, the statement is worse than useless.
 */
public class HouseholdCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-54s %13.4f  expected %13.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-54s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) {

        /* ==================== 1. the statement ==================== */
        System.out.println("--- the month ---");

        HouseholdAccounts hh = new HouseholdAccounts();

        // 1,000 of wages, 15% tax, 300 of rent, 400 in the shops.
        hh.update(1000, 150, 300, 400, 500, 250, 240);

        check("wages", hh.getWages(), 1000);
        check("wage tax", hh.getWageTax(), 150);
        check("take-home", hh.getDisposableIncome(), 850);
        check("rent", hh.getRent(), 300);
        check("shopping", hh.getShopping(), 400);
        check("total spending", hh.getSpending(), 700);
        check("saved", hh.getNetSaving(), 150);

        // Take-home less spending, not gross less spending: the tax is gone
        // before the people ever see it.
        check("saving rate is on take-home", hh.getSavingRate(), 150.0 / 850);
        check("effective tax rate", hh.getEffectiveTaxRate(), .15);
        check("rent burden", hh.getRentBurden(), 300.0 / 850);

        /* ============ 2. spending more than they earn ============ */
        System.out.println("\n--- living beyond it ---");

        // The case worth catching. Retail spending is driven by how many people
        // there are, not by what they earn, so nothing in the model prevents
        // this - and if it happens, money is arriving from nowhere.
        HouseholdAccounts squeezed = new HouseholdAccounts();
        squeezed.update(1000, 150, 600, 400, 500, 250, 240);

        assertTrue("spending over take-home is flagged", squeezed.isLivingBeyondIncome());
        check("...as a negative", squeezed.getNetSaving(), -150);
        assertTrue("...and a negative saving rate", squeezed.getSavingRate() < 0);
        assertTrue("rent over a third of income", squeezed.getRentBurden() > .35);
        System.out.printf("   short $%.0f a month, rent at %.0f%% of take-home%n",
                -squeezed.getNetSaving(), squeezed.getRentBurden() * 100);

        // Exactly breaking even is not living beyond it.
        HouseholdAccounts breakeven = new HouseholdAccounts();
        breakeven.update(1000, 150, 450, 400, 500, 250, 240);
        check("nothing left", breakeven.getNetSaving(), 0);
        assertTrue("...but not a shortfall", !breakeven.isLivingBeyondIncome());

        /* ==================== 3. accumulating ==================== */
        System.out.println("\n--- over time ---");

        HouseholdAccounts saver = new HouseholdAccounts();
        for (int i = 0; i < 12; i++) {
            saver.update(1000, 150, 300, 400, 500, 250, 240);
        }
        check("a year of saving 150", saver.getCumulativeSaving(), 1800);
        check("the month itself is still just one month", saver.getNetSaving(), 150);

        // A bad year eats into it, and the total can go negative - which is the
        // honest reading of a city whose people have been underpaid for years.
        for (int i = 0; i < 24; i++) {
            saver.update(1000, 150, 700, 400, 500, 250, 240);
        }
        check("two years of losing 250", saver.getCumulativeSaving(), 1800 - 24 * 250);
        assertTrue("cumulative can go negative", saver.getCumulativeSaving() < 0);

        /* ==================== 4. per head ==================== */
        System.out.println("\n--- per head ---");

        check("income per resident", hh.getIncomePerResident(), 1000.0 / 500);
        check("spending per resident", hh.getSpendingPerResident(), 700.0 / 500);
        check("average filled job pays", hh.getAverageWage(), 1000.0 / 240);
        check("people per worker", hh.getDependencyRatio(), 500.0 / 250);

        // An empty city divides by nothing and must not produce infinity.
        HouseholdAccounts empty = new HouseholdAccounts();
        empty.update(0, 0, 0, 0, 0, 0, 0);
        check("no people -> no income per head", empty.getIncomePerResident(), 0);
        check("no workers -> no average wage", empty.getAverageWage(), 0);
        check("no dependency ratio either", empty.getDependencyRatio(), 0);
        check("no income -> no saving rate", empty.getSavingRate(), 0);
        check("...nor a rent burden", empty.getRentBurden(), 0);
        check("no wages -> no tax rate", empty.getEffectiveTaxRate(), 0);

        HouseholdAccounts reset = new HouseholdAccounts();
        reset.update(1000, 150, 300, 400, 500, 250, 240);
        reset.reset();
        check("reset clears the running total", reset.getCumulativeSaving(), 0);
        check("...and the month", reset.getWages(), 0);

        /* ============ 5. it is the other side of consumption ============ */
        System.out.println("\n--- the same money, from the other end ---");

        // What households pay out IS consumption in the national accounts. If
        // these two ever diverge, one of them is wrong.
        NationalAccounts na = new NationalAccounts();
        na.update(400, 300, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);   // 400 retail, 300 rent

        HouseholdAccounts paired = new HouseholdAccounts();
        paired.update(1000, 150,
                na.getConsumptionHousing(), na.getConsumptionGoods(),
                500, 250, 240);

        check("what the people paid in rent", paired.getRent(), na.getConsumptionHousing());
        check("what they spent in shops", paired.getShopping(), na.getConsumptionGoods());
        check("household spending IS consumption",
                paired.getSpending(), na.getConsumption());

        /* ==================== 6. the demolition log ==================== */
        System.out.println("\n--- what the city lost ---");

        DemolitionLog log = new DemolitionLog();
        check("nothing lost yet", log.size(), 0);
        assertTrue("...and nothing to show", log.recent(1).isEmpty());

        log.record("Construction Depot", 3, "Construction", 10, 180);
        check("recorded", log.size(), 1);

        DemolitionLog.Entry entry = log.recent(10).get(0);
        check("quantity", entry.quantity, 3);
        assertTrue("building", "Construction Depot".equals(entry.building));
        assertTrue("sector", "Construction".equals(entry.sector));
        assertTrue("the city paid for the plot", entry.wasPaidFor());

        assertTrue("this month", "this month".equals(entry.when(10)));
        assertTrue("last month", "last month".equals(entry.when(11)));
        assertTrue("three months ago", "3 months ago".equals(entry.when(13)));
        check("months ago", entry.monthsAgo(13), 3);

        // A clock that somehow went backwards must not produce negative ages.
        check("never negative", entry.monthsAgo(5), 0);

        // Abandoned plots read differently from ones the city bought back.
        log.record("Steel Foundry", 1, "Heavy Industry", 11, 0);
        assertTrue("abandoned plot", !log.recent(11).get(0).wasPaidFor());

        /* ==================== 7. it fades out ==================== */
        System.out.println("\n--- and stops showing eventually ---");

        assertTrue("still visible after a year", log.recent(10 + 12).size() == 2);
        assertTrue("still there at the limit",
                !log.recent(10 + DemolitionLog.KEEP_MONTHS).isEmpty());
        assertTrue("gone a month later",
                log.recent(11 + DemolitionLog.KEEP_MONTHS + 1).isEmpty());
        assertTrue("...but not forgotten by all()", log.all().size() == 2);

        // Newest first, so the eye lands on what just happened.
        assertTrue("newest first", "Steel Foundry".equals(log.recent(11).get(0).building));

        // Nonsense is refused rather than stored.
        DemolitionLog strict = new DemolitionLog();
        strict.record(null, 3, "Retail", 5, 0);
        strict.record("House", 0, "Real Estate", 5, 0);
        strict.record("House", -2, "Real Estate", 5, 0);
        check("nothing nonsensical stored", strict.size(), 0);

        // Bounded, so a city demolishing every month cannot grow this forever.
        DemolitionLog busy = new DemolitionLog();
        for (int m = 1; m <= 200; m++) {
            busy.record("House", 1, "Real Estate", m, 8);
        }
        assertTrue("the log is capped", busy.size() <= 40);
        assertTrue("and keeps the newest",
                busy.all().get(0).month == 200);

        log.clear();
        check("cleared", log.size(), 0);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
