package ham.citybuildersim;

/**
 * Crime, the police and the prisons: every claim in
 * claude/crime-has-reasons.md, each with its own cause.
 *
 * Jerus, 2026-09-11: "crime is a function of unemployment, and tight or under
 * households, we need police, and also prison... alot of police drastically
 * reduces it but never eliminates it... if there is a reason for crime there
 * is no way to actually remove it without changing the underlying reason."
 */
public class CrimeCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-84s %12.4f  expected %12.4f  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-84s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    /** People for this many adults at the game's equilibrium share. */
    static double peopleFor(double adults) {
        return adults / PopulationCohorts.equilibriumShare(AgeBand.ADULT);
    }

    /** Officers for this coverage of this many people. */
    static double officersFor(double coverage, double people) {
        return coverage * people * Crime.FULL_OFFICERS_PER_100K / 100_000.0;
    }

    /** One month of a fresh Crime with these reasons, returned. */
    static Crime month(Crime.Causes k, double people, double coverage, double cells) {
        Crime c = new Crime();
        c.advanceMonth(k, people, officersFor(coverage, people), cells, 0, 3.46);
        return c;
    }

    static Crime.Causes bust(double adults) {
        return new Crime.Causes()
                .add(Crime.Cause.ON_EI, adults * .05)
                .add(Crime.Cause.PAST_EI, adults * .15)
                .add(Crime.Cause.NO_HOME, adults * .05)
                .add(Crime.Cause.SHORT_OF_MONEY, adults * .20)
                .add(Crime.Cause.CROWDED, adults * .08)
                .add(Crime.Cause.NO_CAUSE, adults * .47);
    }

    public static void main(String[] args) throws Exception {

        double adults = 600_000;
        double people = peopleFor(adults);

        /* ============ 1. K is Canada ============ */
        System.out.println("--- K is struck from Canada ---");
        Crime canada = month(Crime.Causes.canadaLike(adults), people, .5, 0);
        check("a Canada-like city at Canada's half coverage makes Canada's 5,585 a year per 100,000",
                canada.getRatePer100k(), Crime.CANADA_CRIMES_PER_100K, 1e-6);
        check("K is about 0.0075", Crime.K, .0075, .0001);
        check("...and kills Canada's 1.61 a year per 100,000",
                canada.getKilled() * 12 * 100_000 / people, Crime.CANADA_HOMICIDES_PER_100K, 1e-9);
        check("a quarter of it is violent", canada.getViolent() / canada.getCrimes(), .25, 1e-12);
        check("the injured: half a month off for each violent crime, over the city",
                canada.getInjuredShare(), canada.getViolent() * .5 / people, 1e-15);
        check("a theft takes a quarter of a month's unskilled wage",
                canada.getTheftWanted(), canada.getProperty() * .25 * 3.46, 1e-9);

        /* ============ 2. the police ============ */
        System.out.println("\n--- what the police do ---");
        check("no police leave all of it", Crime.deterrence(0), 1, 0);
        check("Canada's half coverage takes about a third off", 1 - Crime.deterrence(.5), .318, .001);
        check("full coverage takes 90% off", 1 - Crime.deterrence(1), .90, 1e-12);
        check("full coverage is twice Canada's 180 officers per 100,000", Crime.FULL_OFFICERS_PER_100K, 360, 0);

        String[] names = {"Canada-like causes", "no causes at all", "a bust"};
        Crime.Causes[] cities = {Crime.Causes.canadaLike(adults),
                new Crime.Causes().add(Crime.Cause.NO_CAUSE, adults), bust(adults)};
        double[][] table = {{2.4, 1.0, .05}, {2.0, .72, .01}, {3.6, 1.8, .17}};
        // To the places the table is written to: a tenth, or a hundredth.
        double[][] tol = {{.05, .05, .005}, {.05, .005, .005}, {.05, .05, .005}};
        double[] cover = {0, .5, 1};
        for (int i = 0; i < cities.length; i++) {
            for (int j = 0; j < 3; j++) {
                double vs = month(cities[i], people, cover[j], 0).getRateVsCanada();
                double t = tol[i][j];
                check(String.format("the table: %s, %s", names[i],
                        j == 0 ? "no police" : j == 1 ? "Canada's police" : "full"), vs, table[i][j], t);
            }
        }

        boolean monotone = true, neverZero = true;
        double before = Double.MAX_VALUE;
        for (int s = 0; s <= 40; s++) {
            double c = s / 40.0;
            double crimes = month(bust(adults), people, c, 0).getCrimes();
            if (crimes > before + 1e-9) monotone = false;
            if (!(crimes > 0)) neverZero = false;
            before = crimes;
        }
        assertTrue("more police never means more crime", monotone);
        assertTrue("...and no amount of police makes it zero", neverZero);
        check("officers past full coverage buy nothing",
                month(bust(adults), people, 2, 0).getCrimes(), month(bust(adults), people, 1, 0).getCrimes(), 1e-9);

        Crime fixedReasons = month(new Crime.Causes().add(Crime.Cause.PAST_EI, adults * .1)
                .add(Crime.Cause.NO_CAUSE, adults * .9), people, 1, 0);
        Crime workFound = month(new Crime.Causes().add(Crime.Cause.NO_CAUSE, adults), people, 1, 0);
        assertTrue("at full coverage, work for the tenth past EI cuts crime by more than half",
                workFound.getCrimes() < fixedReasons.getCrimes() * .5);
        check("the crime of a reason is its share of the pressure",
                fixedReasons.getCrimes(Crime.Cause.PAST_EI),
                fixedReasons.getCrimes() * adults * .1 * 4 / fixedReasons.getPressure(), 1e-9);
        double sumByCause = 0;
        for (Crime.Cause cause : Crime.Cause.values()) sumByCause += canada.getCrimes(cause);
        check("...and the reasons add up to the crime", sumByCause, canada.getCrimes(), 1e-9);
        check("the what-if at this month's coverage is this month",
                canada.crimesAt(canada.getCoverage()), canada.getCrimes(), 1e-9);
        check("coverage is officers over full coverage", Crime.coverageOf(90, 50_000), 90 / 180.0, 1e-12);
        check("an empty city is covered", Crime.coverageOf(0, 0), 1, 0);

        /* ============ 3. who is caught, and who is held ============ */
        System.out.println("\n--- who is caught, and who is held ---");
        check("nobody is caught with no police", month(bust(adults), people, 0, 1e9).getCaught(), 0, 0);
        Crime full = month(bust(adults), people, 1, 1e9);
        check("9% of crimes at full coverage", full.getCaught() / full.getCrimes(), .09, 1e-12);
        check("4.5% at Canada's", canada.getCaught() / canada.getCrimes(), .045, 1e-12);
        check("...and with no cells, every one of them caught and not held", canada.getNotHeld(), canada.getCaught(), 1e-12);
        Crime held = new Crime();
        for (int m = 0; m < 36; m++) {
            held.advanceMonth(Crime.Causes.canadaLike(adults), people, officersFor(.5, people), 1e9, 0, 3.46);
        }
        check("Canada's police, six months each: about Canada's 127 inside per 100,000",
                held.getPrisonersPer100k(), 127, 2);
        check("...admitted, 4.5% of crimes", held.getAdmitted() / held.getCrimes(), .045, 1e-12);

        Crime ring = new Crime();
        ring.advanceMonth(bust(1000), peopleFor(1000), officersFor(1, peopleFor(1000)), 1000, 0, 3.46);
        double sent = ring.getAdmitted();
        assertTrue("fixture: somebody went in", sent > 0);
        Crime.Causes nobody = new Crime.Causes();
        boolean stillIn = true;
        for (int m = 2; m <= 6; m++) {
            ring.advanceMonth(nobody, peopleFor(1000), officersFor(1, peopleFor(1000)), 1000, 0, 3.46);
            if (Math.abs(ring.prisoners() - sent) > 1e-12 || ring.getReleased() > 0) stillIn = false;
        }
        assertTrue("they serve months two to six", stillIn);
        ring.advanceMonth(nobody, peopleFor(1000), officersFor(1, peopleFor(1000)), 1000, 0, 3.46);
        check("...and the seventh month they are out", ring.getReleased(), sent, 1e-12);
        check("...and nobody is inside", ring.prisoners(), 0, 1e-12);

        Crime cramped = month(bust(adults), people, 1, 10);
        check("prisons full: ten cells hold ten", cramped.getAdmitted(), 10, 1e-12);
        check("...and the rest are caught but not held", cramped.getNotHeld(), cramped.getCaught() - 10, 1e-9);
        cramped.advanceMonth(bust(adults), people, officersFor(1, people), 10, 0, 3.46);
        check("...next month the cells are still full, so nobody goes in", cramped.getAdmitted(), 0, 1e-12);
        cramped.advanceMonth(bust(adults), people, officersFor(1, people), 4, 0, 3.46);
        check("a prison that lost its guards lets out who it cannot hold", cramped.getReleasedEarly(), 6, 1e-12);
        check("...leaving what its staffed cells hold", cramped.prisoners(), 4, 1e-12);

        Crime thinning = new Crime();
        thinning.advanceMonth(bust(1000), peopleFor(1000), officersFor(1, peopleFor(1000)), 1000, 0, 3.46);
        double in = thinning.prisoners();
        thinning.advanceMonth(nobody, peopleFor(1000), officersFor(1, peopleFor(1000)), 1000, .01, 3.46);
        check("prisoners die and turn seventy at the adults' rate", thinning.prisoners(), in * .99, 1e-12);

        double[] state = held.getState();
        Crime back = new Crime();
        assertTrue("the state restores", back.restore(state));
        check("...every figure", java.util.Arrays.equals(back.getState(), state) ? 0 : 1, 0, 0);
        assertTrue("a malformed state is refused whole", !new Crime().restore(new double[] {1, 2, 3}));

        /* ============ 4. migration ============ */
        System.out.println("\n--- who comes and who goes ---");
        check("at or below Canada's rate nothing moves", Migration.crimePull(1), 1, 0);
        check("at twice Canada's, a tenth off the size the city supports", Migration.crimePull(2), 1 / 1.1, 1e-12);

        /* ============ 5. the buildings ============ */
        System.out.println("\n--- the buildings ---");
        BuildingManager bm = new BuildingManager();
        quietly(bm::initializeTemplates);
        String[][] anchors = {{"Police Station", "POLICE", "120", "45000"}, {"Police Headquarters", "POLICE", "500", "180000"},
                {"Jail", "PRISON", "300", "360000"}, {"Penitentiary", "PRISON", "600", "600000"}};
        for (String[] a : anchors) {
            BuildingsTemplate t = bm.getTemplateByName(a[0]);
            assertTrue(a[0] + " exists, filed under SAFETY", t != null && t.getCategory() == BuildingType.SAFETY);
            if (t == null) continue;
            assertTrue("...as " + a[1], t.getSafety() == SafetyType.valueOf(a[1]));
            check("...holding " + a[2], t.getCapacity(), Double.parseDouble(a[2]), 0);
            check("...for what the real thing costs", t.getCashCost() + 18 * t.getConstructionMaterials(),
                    Double.parseDouble(a[3]), 10);
        }
        check("the founding constabulary: full coverage for 1,200 people",
                SafetyType.POLICE.foundingCapacity(), 1200 * .0036, 1e-12);
        check("...and no founding cells", SafetyType.PRISON.foundingCapacity(), 0, 0);
        BuildingsTemplate station = bm.getTemplateByName("Police Station");
        bm.addStack(station, 2, true);
        double[] halfStaffed = new double[JobType.values().length];
        java.util.Arrays.fill(halfStaffed, .5);
        check("a police station with half its staff patrols with half its officers",
                bm.getStaffedSafetyCapacity(SafetyType.POLICE, halfStaffed),
                SafetyType.POLICE.foundingCapacity() + 2 * 120 * .5, 1e-9);
        check("...and the buildings hold what they hold", bm.getSafetyCapacity(SafetyType.POLICE),
                SafetyType.POLICE.foundingCapacity() + 240, 1e-9);

        /* ============ 6. a played city ============ */
        System.out.println("\n--- a played city: the reasons, the thefts, the prisons ---");
        java.nio.file.Path root = java.nio.file.Files.createTempDirectory("crimecheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game bare = new Game(files);
        Game policed = new Game(files);
        double[] worstAudit = new double[1];
        double[] worstBooks = new double[1];
        double[] killedEver = new double[1];
        double[] prisonersBefore = new double[1];
        for (Game g : new Game[] {bare, policed}) {
            quietly(() -> {
                g.run();
                BuildingManager b = g.getBuildingManager();
                g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 100_000_000L);
                // Big enough that the founding constabulary is a sliver of it,
                // and more homes than work, so there are reasons.
                b.addStack(b.getTemplateByName("House"), 3000, true);
                b.addStack(b.getTemplateByName("Convenience Store"), 60, true);
                b.addStack(b.getTemplateByName("Textile Mill"), 30, true);
                b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
                b.addStack(b.getTemplateByName("Water Treatment Plant"), 2, true);
                b.addStack(b.getTemplateByName("Paved Road"), 20, true);
                if (g == policed) {
                    b.addStack(b.getTemplateByName("Police Station"), 1, true);
                    b.addStack(b.getTemplateByName("Jail"), 1, true);
                }
                for (int m = 0; m < 72; m++) {
                    if (g == policed) prisonersBefore[0] = g.getCrime().prisoners();
                    g.simulateMonths(1);
                    worstAudit[0] = Math.max(worstAudit[0], g.getLastMoneyAudit().relative());
                    for (SectorBooks.SectorMonth s : g.getSectorBooks().thisMonth()) {
                        worstBooks[0] = Math.max(worstBooks[0], Math.abs(s.unexplained()));
                    }
                    killedEver[0] += g.getCohorts().getKilled(AgeBand.ADULT);
                }
            });
        }
        Crime cb = bare.getCrime(), cp = policed.getCrime();
        System.out.printf("   no police: %,.0f people, %.0f%% covered, %.2fx Canada; policed: %.0f%% covered, %.2fx,"
                        + " %.1f inside, $%,.0f stolen from businesses last month%n",
                cb.getPopulation(), cb.getCoverage() * 100, cb.getRateVsCanada(), cp.getCoverage() * 100,
                cp.getRateVsCanada(), cp.prisoners(), cb.getStolenFromBusinesses() * 1000);
        check("with only the founding constabulary, coverage is 4.32 officers over the city",
                cb.getCoverage(), Crime.coverageOf(SafetyType.POLICE.foundingCapacity(), cb.getPopulation()), 1e-12);
        assertTrue("...and crime is above Canada's", cb.getRateVsCanada() > 1);
        assertTrue("a police station brings it well under", cp.getRateVsCanada() < cb.getRateVsCanada() * .5);
        assertTrue("...but not to nothing", cp.getCrimes() > 0);
        assertTrue("every month passed the money audit, the thefts from the tills declared", worstAudit[0] < 1e-6);
        check("every business's cash flow explained its till, the thefts a line of their own", worstBooks[0], 0, 1e-6);
        assertTrue("fixture: something was stolen from the businesses", cb.getStolenFromBusinesses() > 0);
        double tills = 0;
        for (SectorBooks.SectorMonth s : bare.getSectorBooks().thisMonth()) tills += s.stolen();
        check("...and the tills' lines add up to it", tills, cb.getStolenFromBusinesses(), 1e-9);
        assertTrue("violence killed somebody", killedEver[0] > 0);
        check("the graph holds this month's killed", bare.getHistorySave().aligned("deathsKilled")[
                        bare.getHistorySave().aligned("deathsKilled").length - 1],
                Math.round(bare.getCohorts().getKilled(AgeBand.ADULT) * 100) / 100.0, 1e-9);
        check("injuries are on the sick rate", bare.getHealth().getInjuryRate(), cb.getInjuredShare(), 1e-15);
        assertTrue("the crime notice is up exactly when crime is at 1.5x Canada's or worse",
                bare.getInbox().all().stream().anyMatch(n -> n.getKey().equals("crime") && !n.isResolved())
                        == (cb.getRateVsCanada() >= 1.5 || cb.getNotHeld() >= 1));
        check("migration read last month's crime", bare.getMigration().getLastCrimePull(),
                Migration.crimePull(cb.getRateVsCanada()), .05);

        HouseholdBalance hb = bare.getHouseholdBalance();
        double saved = hb.totalSavings();
        double taken = hb.takeFromSavings(saved * .01);
        check("a theft takes from the households' savings what it says it took", hb.totalSavings(), saved - taken, 1e-6);
        double[] weight = new double[hb.cellCount()];
        weight[0] = 1;
        weight[hb.cellCount() - 2] = 3;
        hb.creditByWeight(taken, weight);
        check("...and handing it to the offenders puts every dollar back", hb.totalSavings(), saved, 1e-6);

        /* ---- the prisons ---- */
        PopulationManager pm = policed.getPopulationManager();
        assertTrue("fixture: somebody is in prison", cp.prisoners() > 0);
        check("the prisoners are out of the labour force",
                pm.getLabourForce(), pm.getWorkforce() - pm.getStudyingTotal() - cp.prisoners(), 1e-9);
        check("...the whole of them", pm.getImprisoned(), cp.prisoners(), 1e-12);
        check("the pool is still the labour market's", policed.getUnemployment().getPool(), pm.getUnemployed(), 1e-6);
        FamilyModel f = policed.getFamilies();
        double inFamilies = 0;
        for (FamilyStructure s : FamilyStructure.values()) inFamilies += f.totalOf(s) * s.membersOf(AgeBand.ADULT);
        check("the families hold the adults outside nothing: not the pool, the students or the prisoners",
                inFamilies + f.getOutsideAdults(), policed.getCohorts().get(AgeBand.ADULT), 1e-6);
        PrisonerHousehold ledger = policed.getHouseholdBalance().prisoners();
        // The households are struck at the top of the month, the prisons in the middle of it.
        check("the prisoners' ledger holds the prisoners, as the month opened", ledger.households(),
                prisonersBefore[0], 1e-9);
        check("...pays no rent", ledger.rentShare(), 0, 0);
        check("...buys no food", ledger.subsistence(), 0, 0);
        check("...and pays no interest", ledger.interest(), 0, 0);
        assertTrue("...and none of them is caught but not held with cells to spare",
                cp.getNotHeld() == 0 || cp.getCells() - cp.prisoners() < 1e-9);
        EconomyManager em = policed.getEconomyManager();
        check("the treasury pays the police and the prisons", em.getSafetyBill(), cp.getGrossCost(), 1e-9);
        check("...on the government's books", em.getNationalAccounts().getSafetySpending(), cp.getGrossCost(), 1e-9);
        assertTrue("...and it costs something", cp.getGrossCost() > 0);

        /* ---- a save ---- */
        System.out.println("\n--- a save ---");
        quietly(() -> policed.saveGame(1, "crime"));
        Game again = new Game(files);
        quietly(() -> again.loadGameSave(1));
        check("the crime comes back, the prisoners in their months", java.util.Arrays.equals(
                again.getCrime().getState(), cp.getState()) ? 0 : 1, 0, 0);
        check("...and out of the labour force on the load path", again.getPopulationManager().getLabourForce(),
                pm.getLabourForce(), 1e-9);
        check("...and the bill", again.getEconomyManager().getSafetyBill(), em.getSafetyBill(), 1e-9);
        check("...and the ledger", again.getHouseholdBalance().prisoners().totalSavings(), ledger.totalSavings(), 1e-6);

        System.out.println();
        System.out.println(fails == 0 ? "Crime has reasons, and the police cannot remove them." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
