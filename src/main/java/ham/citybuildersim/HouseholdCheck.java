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
         * RENT FOLLOWS FRONT DOORS, THE SHOP FOLLOWS HEADS, and the two must not
         * be the same rule - which they were until 2026-09-07.
         *
         * CommercialHandler.getRentIncome() charges every let home the same
         * figure whoever is in it, because that is what a landlord charges for:
         * the flat. Splitting it by headcount instead billed a family of five
         * five times what it billed a single adult in the identical flat, so
         * every large household read as unable to afford to live here. The shop
         * genuinely is a headcount - retail demand is min(coverage, population)
         * - so it keeps the old rule.
         *
         * 150 of 260 homes carry 57.7% of the rent; 300 of 500 people carry 60%
         * of the shopping. That the two figures now DIFFER is the point of the
         * assertion: identical numbers here is what the bug looked like.
         */
        check("rent follows front doors", split.getRowRent(PayTier.UNSKILLED.ordinal()),
                300 * (150 / 260.0));
        check("the shop follows heads",
                split.getRowShopping(PayTier.UNSKILLED.ordinal()), 240);
        assertTrue("...and the two are not the same split",
                Math.abs(split.getRowRent(PayTier.UNSKILLED.ordinal()) / split.getRent()
                        - split.getRowShopping(PayTier.UNSKILLED.ordinal()) / split.getShopping())
                        > .01);

        /*
         * ONE HOUSEHOLD, AND THE SHAPES INSIDE A TIER.
         *
         * The tier row averages across every shape in it, which is the one
         * household nobody lives in. statementFor() splits it: same rent per
         * door for all of them, shopping by size, wages by earners. These
         * assert the three rules rather than the numbers, because the numbers
         * are the fixture's and the rules are the model's.
         */
        assertTrue("rent per door is the city's rent over its households",
                Math.abs(split.rentPerHousehold() - 300 / 260.0) < 1e-9);
        assertTrue("the shop per head is its shopping over its people",
                Math.abs(split.shoppingPerHead() - 400 / 500.0) < 1e-9);

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

        /*
         * THE YARDSTICK AND THE HOUSE ARE DIFFERENT NUMBERS NOW.
         *
         * This asserted that the House held exactly REFERENCE_HOME_CAPACITY, on
         * the reasoning that the rent price was derived from it. It is not: the
         * reference is the four-person home the affordability target is struck
         * against, and the House grew to six so that a large family and a
         * five-adult flatshare have somewhere to live. What survives is the
         * relationship - a House is at least the reference home, in one
         * dwelling, and therefore costs at least the reference rent.
         */
        assertTrue("a House is at least the home the rent target is struck against",
                house.getCapacity() >= CommercialHandler.REFERENCE_HOME_CAPACITY);
        assertTrue("...and it is the biggest home in the game",
                house.homeSize() >= rentBm.getTemplateByName("Low-Rise Apartments").homeSize()
                        && house.homeSize() >= rentBm.getTemplateByName("Studio Apartments").homeSize());
        assertTrue("...so it costs more than the reference home does",
                house.getCapacity() * 1.0 / CommercialHandler.REFERENCE_HOME_CAPACITY > 1);
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

        /* ============ THE BUDGET CONSTRAINT ============

           Until 2026-09-07 the shops sold `min(coverage, population)` - one
           basket a person, at no price anybody had to be able to pay. So a
           household could be shown spending more than it earned every month for
           three hundred months and the money came from nowhere, which is the
           one failure this whole file exists to catch.

           HouseholdBalance is the constraint. These assert the waterfall Jerus
           specified - savings, then credit, then hunger - at the level of the
           rule rather than of any city's numbers.
           ========================================================= */
        System.out.println("\n--- a household short of money spends its savings, then borrows ---");

        HouseholdBalance bal = new HouseholdBalance();
        int R = HouseholdBalance.ROWS;
        int U = PayTier.UNSKILLED.ordinal();

        double[] homes = new double[R];
        double[] heads = new double[R];
        double[] income = new double[R];
        double[] fees = new double[R];
        double[] spent = new double[R];
        homes[U] = 100;
        heads[U] = 200;          // two people a household
        income[U] = 100 * 1.0;   // $1.00 a household, in the game's thousands
        double rentEach = .60;   // more than half of it, so the shop cannot be covered
        double basket = .25;     // per head, so a household of two needs .50

        /*
         * THE SPEND FOLLOWS THE PLAN, as it does in the game: the shops sell
         * what the households said they could buy. A fixture that charged a
         * fixed amount every month would be testing a household nobody has,
         * and its debt would grow without a ceiling because nothing was
         * telling it to stop.
         */
        bal.advanceMonth(homes, heads, income, rentEach, fees, spent, basket, .05, 1);
        double opening = bal.getSavings(U);
        assertTrue("a founding household is not destitute on day one", opening > 0);
        assertTrue("...by about the buffer the dial names",
                opening >= 1.0 * HouseholdBalance.OPENING_BUFFER_MONTHS - 1e-9);

        spent[U] = homes[U] * bal.getPlanned(U);
        double savingsBefore = bal.getSavings(U);
        bal.advanceMonth(homes, heads, income, rentEach, fees, spent, basket, .05, 1);
        assertTrue("SAVINGS GO FIRST", bal.getSavings(U) < savingsBefore);
        assertTrue("...and nothing is borrowed while there are savings",
                bal.getDebt(U) == 0);

        // Run it until the savings are gone.
        for (int m = 0; m < 40; m++) {
            spent[U] = homes[U] * bal.getPlanned(U);
            bal.advanceMonth(homes, heads, income, rentEach, fees, spent, basket, .05, 1);
        }
        assertTrue("the savings run out", bal.getSavings(U) < 1e-9);
        assertTrue("...THEN THEY BORROW", bal.getDebt(U) > 0);
        assertTrue("...at a rate over the risk-free one",
                bal.getRate(U) > .05 + 1e-9);
        assertTrue("...which climbs with what they already owe",
                bal.getRate(U) > .05 + HouseholdBalance.BASE_SPREAD);
        assertTrue("...and never past the cap",
                bal.getRate(U) <= HouseholdBalance.MAX_RATE + 1e-9);

        /*
         * And keep going until the credit runs out too.
         *
         * MEASURED OVER THE CYCLE, NOT IN THE MONTH THE LOOP STOPS ON, and
         * that is a correction rather than a flourish. A household that has
         * been discharged is locked out for a year and then gets its credit
         * line back, so it eats short for eleven months and buys a full basket
         * in the twelfth. The first version of this assertion read whichever
         * month the loop happened to end in, and once bankruptcy went in it
         * landed on that twelfth month and failed - a fixture reading the
         * phase of an oscillation rather than its level.
         */
        double shortMonths = 0, fullMonths = 0, plannedTotal = 0, subsistenceTotal = 0;
        for (int m = 0; m < 400; m++) {
            spent[U] = homes[U] * bal.getPlanned(U);
            bal.advanceMonth(homes, heads, income, rentEach, fees, spent, basket, .05, 1);
            if (m >= 100) {   // past the savings and the first slide into debt
                plannedTotal += bal.getPlanned(U);
                subsistenceTotal += bal.getSubsistence(U);
                if (bal.getPlanned(U) < bal.getSubsistence(U) - 1e-9) shortMonths++;
                else fullMonths++;
            }
        }
        assertTrue("the debt stops at the ceiling, it does not run away",
                bal.getDebt(U) <= HouseholdBalance.CREDIT_LIMIT_MONTHS * 1.0 + .5);
        System.out.printf("   short in %.0f months of %.0f; planned $%.3fk against $%.3fk of subsistence%n",
                shortMonths, shortMonths + fullMonths,
                plannedTotal / (shortMonths + fullMonths),
                subsistenceTotal / (shortMonths + fullMonths));
        assertTrue("...AND THEN THEY EAT LESS", plannedTotal < subsistenceTotal - 1e-9);
        assertTrue("...in most months, not just on average", shortMonths > fullMonths);
        assertTrue("...which the health service can see", bal.getHungerRate() > 0);

        System.out.println("\n--- and a household with money to spare pays it down, then banks it ---");

        HouseholdBalance rich = new HouseholdBalance();
        income[U] = 100 * 4.0;
        spent[U] = 0;
        for (int m = 0; m < 6; m++) {
            rich.advanceMonth(homes, heads, income, rentEach, fees, spent, basket, .05, 1);
            spent[U] = homes[U] * rich.getPlanned(U);
        }
        assertTrue("a household with a surplus banks it", rich.getSavings(U) > 0);
        assertTrue("...and owes nothing", rich.getDebt(U) == 0);
        assertTrue("...and is not hungry", rich.getHungerRate() == 0);

        /*
         * SPENDING FOLLOWS INCOME UPWARD TOO, which is the half that is easy to
         * forget. Without it the rich bank everything - measured at $715,012 a
         * household after three hundred months - and the constraint only ever
         * bites downward.
         */
        assertTrue("a rich household WANTS more than a basket",
                rich.getWant(U) > rich.getSubsistence(U));
        assertTrue("...but a poor one wants exactly a basket, not less",
                Math.abs(bal.getWant(U) - bal.getSubsistence(U)) < 1e-9);

        System.out.println("\n--- the supply side starves people who had the money ---");

        HouseholdBalance shelves = new HouseholdBalance();
        income[U] = 100 * 4.0;
        spent[U] = 0;
        shelves.advanceMonth(homes, heads, income, rentEach, fees, spent, basket, .05, 1);
        spent[U] = homes[U] * shelves.getPlanned(U);
        shelves.advanceMonth(homes, heads, income, rentEach, fees, spent, basket, .05, 0);
        assertTrue("empty shelves are hunger even in a rich city",
                shelves.getHungerRate() > 0);

        /* ============ AND WHEN THEY CANNOT AFFORD A HOME, THEY SHARE ============

           Jerus, 2026-09-07: "perhaps poor families start living together."
           FamilyModel already knew how - it only ever did it when the city ran
           out of HOMES. This is the other reason, and the commoner one.
           ==================================================================== */
        System.out.println("\n--- a tier priced out of living alone shares instead ---");

        FamilyModel homes2 = new FamilyModel();
        double[] noPressure = new double[PayTier.values().length];
        homes2.shareByAffordability(noPressure);
        check("nobody shares when everybody can afford a home",
                homes2.getPricedOutShares(), 0);

        double[] priced = new double[PayTier.values().length];
        priced[PayTier.UNSKILLED.ordinal()] = 1;   // cannot cover any of it
        homes2.shareByAffordability(priced);
        check("...and a model with no households still forms none",
                homes2.getPricedOutShares(), 0);

        assertTrue("somebody always holds out, however dear the rent",
                FamilyModel.MAX_SHARING < 1);

        /*
         * THE PRESSURE ITSELF, which is the number the whole mechanic turns on.
         * Asserted as a shape rather than a figure: zero when one wage covers a
         * home and a basket comfortably, one when it covers none of it, and in
         * between when it is in between.
         */
        int hhRows = HouseholdAccounts.RETIRED + 1;
        HouseholdAccounts priceTest = new HouseholdAccounts();
        double[] wagesRich = new double[PayTier.values().length];
        double[] taxNone   = new double[PayTier.values().length];
        double[] peopleRow = new double[hhRows];
        double[] homesRow  = new double[hhRows];
        peopleRow[PayTier.UNSKILLED.ordinal()] = 100;
        homesRow[PayTier.UNSKILLED.ordinal()]  = 100;

        // rent 100 over 100 homes, shopping 100 over 100 people: $2 to live alone.
        priceTest.refresh(1000, 0, 100, 100, 0, 0, 0, 0, 0, 100, 100, 100);
        priceTest.updateByTier(wagesRich, taxNone, peopleRow, homesRow);
        double[] flat = priceTest.livingAlonePressure(null);
        check("no household model, no pressure", flat[PayTier.UNSKILLED.ordinal()], 0);

        /*
         * AND THE WHOLE THING, IN A CITY. The unit checks above say the valve
         * opens; this says it is worth opening - a flatshare has to leave its
         * members BETTER OFF than living alone, or the mechanic is a costume.
         */
        java.io.PrintStream realOut = System.out;
        java.io.PrintStream hush = new java.io.PrintStream(new java.io.OutputStream() {
            @Override public void write(int b) { }
        });

        Game poor = new Game(GameFiles.scratch("householdcheck"));
        System.setOut(hush);
        try {
            poor.newGame();
            for (BuildingsTemplate t : poor.getBuildingManager().getTemplates()) {
                if (t.getName().equals("Low-Rise Apartments")) poor.buildStack(t, 60, true);
                if (t.getName().equals("Small Grocery Store"))  poor.buildStack(t, 8, true);
                if (t.getName().equals("Coal Power Plant"))     poor.buildStack(t, 2, true);
                if (t.getName().equals("Water Treatment Plant"))poor.buildStack(t, 2, true);
                if (t.getName().equals("Paved Road"))           poor.buildStack(t, 20, true);
                if (t.getName().equals("Textile Mill"))         poor.buildStack(t, 6, true);
            }
            poor.simulateMonths(120);
        } finally { System.setOut(realOut); }

        HouseholdAccounts books = poor.getHouseholds();
        FamilyModel mix = poor.getFamilies();
        double[] pressure = books.livingAlonePressure(mix);
        int un = PayTier.UNSKILLED.ordinal();

        HouseholdAccounts.Statement alone =
                books.statementFor(mix, FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        HouseholdAccounts.Statement sharing =
                books.statementFor(mix, FamilyStructure.SHARED_ADULTS, PayTier.UNSKILLED);

        System.out.printf("   unskilled pressure %.0f%%, %,.0f flatshares priced out;"
                + " alone leaves %.3f a month, sharing %.3f%n",
                pressure[un] * 100, mix.getPricedOutShares(), alone.left(), sharing.left());

        assertTrue("fixture: an unskilled single adult really cannot afford this city",
                pressure[un] > 0);
        /*
         * Sharing, or nobody left to share. The fixture cannot guarantee single
         * adults exist - a city where every one of them has ALREADY paired off
         * is the mechanic having worked, not having failed - so the assertion
         * is that flatshares exist, however they got there.
         */
        assertTrue("...so some of them are sharing",
                mix.totalOf(FamilyStructure.SHARED_ADULTS) > 0);
        assertTrue("A FLATSHARE IS BETTER OFF THAN LIVING ALONE",
                sharing.left() > alone.left());
        assertTrue("...because five of them pay one rent, not five",
                sharing.rent() < alone.rent() * 5 - 1e-9);
        assertTrue("...and still five baskets",
                Math.abs(sharing.shopping() - alone.shopping() * 5) < 1e-6);

        /* ============ A HOME IS A SIZE, AND A HOUSEHOLD HAS TO FIT ============

           Jerus, 2026-09-07: "studio apartments... only single adults can
           occupy them, or well two people, so any other structure is not
           allowed." Homes were a pooled count before this, so a family of six
           could live in a studio as long as the city had eighty spare doors
           somewhere - which is most of why Studio Apartments were a building
           with no niche and lost to a House on price at every land price
           (backlog I1).
           ==================================================================== */
        System.out.println("\n--- a home is a size, and a household has to fit ---");

        BuildingsTemplate studio = new BuildingsTemplate("s", BuildingType.RESIDENTIAL);
        studio.setCapacity(160);
        studio.setDwellings(80);
        BuildingsTemplate houseT = new BuildingsTemplate("h", BuildingType.RESIDENTIAL);
        houseT.setCapacity(4);
        houseT.setDwellings(1);
        BuildingsTemplate lowRise = new BuildingsTemplate("l", BuildingType.RESIDENTIAL);
        lowRise.setCapacity(250);
        lowRise.setDwellings(100);

        check("a studio flat takes two", studio.homeSize(), 2);
        check("a house takes four", houseT.homeSize(), 4);
        check("a low-rise flat takes three", lowRise.homeSize(), 3);
        assertTrue("...and only the studio refuses children", studio.adultsOnly()
                && !houseT.adultsOnly() && !lowRise.adultsOnly());

        /*
         * A CITY OF NOTHING BUT STUDIOS CANNOT HOUSE A FAMILY, which is the one
         * hard no in the model. Everything else crowds.
         */
        FamilyModel onlyStudios = new FamilyModel();
        int[] studiosOnly = new int[]{0, 0, 500};      // 500 two-person flats
        double nowhere = onlyStudios.house(studiosOnly);
        check("an empty city needs no homes", nowhere, 0);

        /*
         * THE FLAT SETS THE PRICE, and the household in it does not come into
         * it. Jerus: "the building earns its rent regardless - if a couple is
         * living in a mansion it is paying a mansion's price." The first
         * version weighted the two together, which is a market story rather
         * than a lease and put a household's own size back into its rent.
         */
        check("a two-person flat bills two", FamilyModel.rentWeightOf(2), 2);
        check("a six-person house bills six", FamilyModel.rentWeightOf(6), 6);
        assertTrue("...so a bigger home is a dearer one",
                FamilyModel.rentWeightOf(6) > FamilyModel.rentWeightOf(2));

        FamilyModel priceTestModel = new FamilyModel();
        check("and a flat nobody in the city could live in bills nothing",
                priceTestModel.marginalRentWeight(2), 0);

        System.out.println("\n--- and the two pension dials are the player's ---");
        TaxPolicy pol = new TaxPolicy();
        check("the contribution starts at the real CPP rate",
                pol.getContributionRate(), SocialSecurity.DEFAULT_CONTRIBUTION_RATE);
        pol.setContributionRate(.09);
        check("...and moves", pol.getContributionRate(), .09);
        pol.setContributionRate(9);
        check("...but not past the ceiling", pol.getContributionRate(),
                TaxPolicy.MAX_CONTRIBUTION);
        pol.setPensionReplacement(.60);
        check("a richer pension is a bigger cheque", pol.pensionPerSenior(),
                .60 * PayTier.UNSKILLED.getMonthlyWage());
        double[] state = pol.getPolicyState();
        TaxPolicy back2 = new TaxPolicy();
        assertTrue("the dials survive a save", back2.restorePolicyState(state));
        check("...the contribution", back2.getContributionRate(), TaxPolicy.MAX_CONTRIBUTION);
        check("...and the pension", back2.getPensionReplacement(), .60);
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
