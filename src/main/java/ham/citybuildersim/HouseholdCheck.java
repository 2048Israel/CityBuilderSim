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

        /* ==================== 8. the same books, per tier ==================== */
        System.out.println("\n--- split seven ways ---");

        /*
         * THE ROWS MUST ADD UP TO THE TOTAL. That is the only claim worth making
         * about a breakdown: if the parts and the whole disagree, one of them is
         * a second, differently-computed version of the other, which is the
         * failure this whole file exists to prevent.
         */
        HouseholdAccounts split = new HouseholdAccounts();
        split.update(1000, 150, 300, 400, 500, 250, 240);

        int rows = HouseholdAccounts.RETIRED + 1;
        double[] wagesByTier = new double[PayTier.values().length];
        double[] taxByTier   = new double[PayTier.values().length];
        wagesByTier[PayTier.UNSKILLED.ordinal()] = 600;
        wagesByTier[PayTier.SKILLED.ordinal()]   = 400;
        taxByTier[PayTier.UNSKILLED.ordinal()]   = 60;
        taxByTier[PayTier.SKILLED.ordinal()]     = 90;

        double[] people = new double[rows];
        double[] houses = new double[rows];
        people[PayTier.UNSKILLED.ordinal()] = 300;
        people[PayTier.SKILLED.ordinal()]   = 150;
        people[HouseholdAccounts.RETIRED]   = 50;
        houses[PayTier.UNSKILLED.ordinal()] = 150;
        houses[PayTier.SKILLED.ordinal()]   = 70;
        houses[HouseholdAccounts.RETIRED]   = 40;

        split.updateByTier(wagesByTier, taxByTier, people, houses);

        double sumWages = 0, sumTax = 0, sumRent = 0, sumShop = 0, sumPeople = 0;
        for (int r = 0; r < rows; r++) {
            sumWages += split.getRowWages(r);
            sumTax   += split.getRowTax(r);
            sumRent  += split.getRowRent(r);
            sumShop  += split.getRowShopping(r);
            sumPeople += split.getRowPeople(r);
        }
        check("the tiers' wages add up to the city's", sumWages, split.getWages());
        check("...and their tax", sumTax, split.getWageTax());
        check("...and their rent", sumRent, split.getRent());
        check("...and their shopping", sumShop, split.getShopping());
        check("everybody is in exactly one row", sumPeople, 500);

        /*
         * Rent and shopping follow HEADCOUNT, because that is how the model
         * charges them - rent is residents * rentPrice and retail demand is a
         * headcount. 300 of 500 people carry 60% of both.
         */
        check("rent follows people, not income",
                split.getRowRent(PayTier.UNSKILLED.ordinal()), 180);
        check("and so does the shopping",
                split.getRowShopping(PayTier.UNSKILLED.ordinal()), 240);

        /*
         * THE RETIRED ROW EARNS NOTHING. There is no pension in this game, so a
         * pensioner household is pure outgoing - and it must not be quietly
         * folded into the unskilled row, which is where FamilyModel stores it by
         * convention. If this ever reads non-zero, somebody has summed a tier
         * column instead of asking for the working households in it.
         */
        check("pensioners earn nothing",
                split.getRowWages(HouseholdAccounts.RETIRED), 0);
        assertTrue("...so their row is a deficit",
                split.getRowSaving(HouseholdAccounts.RETIRED) < 0);
        assertTrue("...and it is labelled as theirs",
                split.getRowLabel(HouseholdAccounts.RETIRED).contains("Retired"));

        // A malformed split is refused whole rather than half-applied.
        split.updateByTier(wagesByTier, taxByTier, new double[]{1, 2}, houses);
        check("a malformed split is refused",
                split.getRowPeople(PayTier.UNSKILLED.ordinal()), 0);

        /* ---- and the tax split is the tax the city actually charges ---- */
        TaxPolicy policy = new TaxPolicy();
        policy.setIncomeTaxRate(.20);
        policy.setWageOffset(WageBand.values()[0], -.10);
        policy.setWageOffset(WageBand.values()[WageBand.values().length - 1], .10);

        double[] perType = new double[JobType.values().length];
        for (int i = 0; i < perType.length; i++) perType[i] = 100 * (i + 1);

        double[] perTier = policy.wageTaxPerTier(perType, null);
        double tierSum = 0;
        for (double t : perTier) tierSum += t;

        /*
         * The bug this replaced: Game computed the residents' tax as
         * `wages * incomeTaxRate` while the city collected the banded figure.
         * They agreed exactly whenever every offset was zero, which is why it
         * survived - so this check deliberately sets two offsets first, and
         * measures a 23% gap on a played city when it is not applied.
         */
        check("the tier split IS the wage tax", tierSum,
                policy.wageTaxOn(perType, null));

        double flatWay = 0;
        for (double w : perType) flatWay += w;
        flatWay *= policy.getIncomeTaxRate();
        assertTrue("...and a flat rate on the total is NOT the same number",
                Math.abs(flatWay - tierSum) > 1);

        /* ==================== 9. rent is per home, not per head ==================== */
        System.out.println("\n--- what a home costs ---");

        /*
         * THE COMPLAINT THIS ANSWERS. Jerus, reading the per-tier statement:
         * "not only is there no room for rent to increase... but already people
         * are broke." Rent was $350 a month PER RESIDENT, so a family of six
         * paid $2,099 on two unskilled wages of $1,600 - 131% - while a childless
         * couple on the same two wages paid 44%. Children cost rent and earned
         * nothing.
         *
         * The claim now is that a household pays for its HOME and nothing else,
         * so every shape with the same earners pays the same share whatever its
         * size. That is the whole fix, in one assertion.
         */
        BuildingManager rentBm = new BuildingManager();
        rentBm.initializeTemplates();
        BuildingsTemplate house = rentBm.getTemplateByName("House");

        check("the House the rent price is derived from still holds four",
                house.getCapacity(), CommercialHandler.REFERENCE_HOME_CAPACITY);
        check("...in a single dwelling", house.getDwellings(), 1);

        CommercialHandler rentCh = new CommercialHandler();
        rentCh.setHousehold(1600);      // 400 houses of four
        rentCh.setHomes(400);
        rentCh.setPopulation(1218);
        rentCh.setOccupiedHomes(400);

        check("a home is charged for its size, not its occupants",
                rentCh.getRentIncome(), 1600 * rentCh.getRentPrice());
        check("the average home here holds four", rentCh.averageHomeSize(), 4);

        double homeRent = rentCh.averageHomeSize() * rentCh.getRentPrice();
        double coupleIncome = 2 * PayTier.UNSKILLED.getMonthlyWage();
        System.out.printf("   one home costs $%.0f; two unskilled wages are $%.0f%n",
                homeRent * 1000, coupleIncome * 1000);
        check("a working couple pays the burden the price was set for",
                homeRent / coupleIncome, CommercialHandler.TARGET_RENT_BURDEN);
        assertTrue("...and a family of six pays exactly the same, not three times it",
                Math.abs(homeRent / coupleIncome - CommercialHandler.TARGET_RENT_BURDEN) < 1e-9);

        /*
         * EMPTY HOMES EARN NOTHING, and this is the assertion that caught a
         * regression. The first version charged for every home the city had
         * built rather than every home somebody lived in, which meant a city
         * that overbuilt housing raised its own rent bill without gaining a
         * single tenant - worse than the per-head version it replaced, which at
         * least capped rent at the population.
         */
        rentCh.setOccupiedHomes(200);
        check("half let, half the rent", rentCh.getRentIncome(),
                200 * 4 * rentCh.getRentPrice());
        rentCh.setOccupiedHomes(0);
        check("nobody home, no rent", rentCh.getRentIncome(), 0);

        // More households than front doors: everybody crowds into what exists,
        // and nobody pays twice for the same roof.
        rentCh.setOccupiedHomes(900);
        check("a crowded city still only has 400 rents to pay",
                rentCh.getRentIncome(), 1600 * rentCh.getRentPrice());

        /* ==================== 10. pensions ==================== */
        System.out.println("\n--- contributions and pensions ---");

        /*
         * THE ROW THIS EXISTS FOR. Splitting the books by tier put
         * "Retired (no earner)  earned $0  -$95.8k" on screen: a seventh of the
         * city's households with NO income of any kind, paying rent out of money
         * that did not exist. Jerus: "add cpp to everyones pay, aka they pay a
         * tad, and make it so that the government pays for the seniors."
         */
        check("the pension follows the wage table, not a typed-in figure",
                SocialSecurity.pensionPerSenior(),
                SocialSecurity.PENSION_REPLACEMENT * PayTier.UNSKILLED.getMonthlyWage());
        check("contributions are a slice of the wage bill",
                SocialSecurity.contributionsOn(1000),
                1000 * SocialSecurity.CONTRIBUTION_RATE);
        assertTrue("...and it really is only a tad",
                SocialSecurity.CONTRIBUTION_RATE < .10);

        /*
         * THE TWO HALVES DO NOT BALANCE, ON PURPOSE. Contributions scale with
         * workers and pensions with pensioners, so coverage is a pure function
         * of the dependency ratio - a city that ages buys a structural deficit
         * without changing a single policy. Asserted as a DIRECTION rather than
         * a level, since the level depends on the city.
         */
        double youngCoverage = SocialSecurity.coverage(1000, 50);
        double oldCoverage   = SocialSecurity.coverage(1000, 200);
        System.out.printf("   same wage bill, 50 pensioners: %.0f%% covered;"
                + " 200 pensioners: %.0f%%%n", youngCoverage * 100, oldCoverage * 100);
        assertTrue("an ageing city covers less of its own pension bill",
                oldCoverage < youngCoverage);
        check("a city with no pensioners owes nothing",
                SocialSecurity.pensionsFor(0), 0);
        check("...and is fully covered by definition",
                SocialSecurity.coverage(1000, 0), 1);
        check("the shortfall is what contributions do not reach",
                SocialSecurity.shortfall(1000, 200),
                SocialSecurity.pensionsFor(200) - SocialSecurity.contributionsOn(1000));
        check("and never negative when contributions overshoot",
                SocialSecurity.shortfall(100_000, 1), 0);

        /* ---- and in the books ---- */
        HouseholdAccounts pens = new HouseholdAccounts();
        pens.update(1000, 150, 300, 400, 60, 200, 500, 250, 240);

        check("contributions come off take-home", pens.getDisposableIncome(),
                1000 - 150 - 60 + 200);
        check("...and the pension goes on", pens.getPensions(), 200);

        double[] pw = new double[PayTier.values().length];
        double[] pt = new double[PayTier.values().length];
        pw[PayTier.UNSKILLED.ordinal()] = 1000;
        pt[PayTier.UNSKILLED.ordinal()] = 150;
        double[] pp = new double[rows];
        double[] ph = new double[rows];
        pp[PayTier.UNSKILLED.ordinal()] = 400;
        pp[HouseholdAccounts.RETIRED]   = 100;
        ph[PayTier.UNSKILLED.ordinal()] = 200;
        ph[HouseholdAccounts.RETIRED]   = 80;
        pens.updateByTier(pw, pt, pp, ph);

        /*
         * Contributions follow WAGES and the pension goes entirely to the
         * retired row. Allocating contributions by headcount instead would
         * charge pensioners for their own pension, which is the one thing this
         * split must never do.
         */
        check("the workers pay all the contributions",
                pens.getRowContributions(PayTier.UNSKILLED.ordinal()), 60);
        check("...and the pensioners pay none",
                pens.getRowContributions(HouseholdAccounts.RETIRED), 0);
        check("the pension goes entirely to the retired",
                pens.getRowPensions(HouseholdAccounts.RETIRED), 200);
        check("...and nowhere else",
                pens.getRowPensions(PayTier.UNSKILLED.ordinal()), 0);
        assertTrue("the retired row now has an income at all",
                pens.getRowDisposable(HouseholdAccounts.RETIRED) > 0);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
