package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The long sick: who stays sick, and who it kills.
 *
 * Jerus, 2026-09-11: sick people who stay sick start dying; the base death
 * rate halved for every band but babies and seniors; general care saves lives
 * by curing people, not by scaling a death rate. Every claim below sets its
 * own cause. See claude/the-long-sick.md.
 */
public class SicknessCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-78s %12.6f  expected %12.6f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-78s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    /*
     * A city three times the size its founding doctor was meant for, so that
     * without hospitals its general care is thin. HealthCheck's four hundred
     * houses were not enough: the founding endowment covered 97% of them, and
     * the "city with no clinics" was a well-served city.
     */
    static void stock(Game g) {
        BuildingManager b = g.getBuildingManager();
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
        b.addStack(b.getTemplateByName("House"), 2000, true);
        b.addStack(b.getTemplateByName("Convenience Store"), 48, true);
        b.addStack(b.getTemplateByName("Textile Mill"), 16, true);
        b.addStack(b.getTemplateByName("Construction Depot"), 10, true);
        b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
        b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
    }

    public static void main(String[] args) throws Exception {

        /* ============ 1. the base rates ============ */
        System.out.println("--- the base rates, halved where Jerus said ---");
        check("children: half the life table's 0.015%", AgeBand.CHILD.getAnnualMortality(), .00015 / 2, 1e-12);
        check("teens: half of 0.04%", AgeBand.TEEN.getAnnualMortality(), .0004 / 2, 1e-12);
        check("adults: half of 0.45%", AgeBand.ADULT.getAnnualMortality(), .0045 / 2, 1e-12);
        check("babies untouched", AgeBand.BABY.getAnnualMortality(), .001, 1e-12);
        check("seniors untouched", AgeBand.SENIOR.getAnnualMortality(), .045, 1e-12);
        for (AgeBand b : new AgeBand[]{AgeBand.TEEN, AgeBand.ADULT}) {
            check("general care no longer scales " + b.getLabel().toLowerCase() + "' deaths: none",
                    Healthcare.mortalityFactor(b, .5, 0, .5), 1, 1e-12);
            check("...or everybody covered", Healthcare.mortalityFactor(b, .5, 1, .5), 1, 1e-12);
        }

        /* ============ 2. how much of each band is sick ============ */
        System.out.println("\n--- who is sick ---");
        double rate = .1;
        for (AgeBand b : new AgeBand[]{AgeBand.CHILD, AgeBand.TEEN, AgeBand.ADULT}) {
            check(b.getLabel() + " are sick at the city rate, whatever the care",
                    Sickness.shareFor(b, rate, 0, 0), rate, 1e-12);
        }
        check("babies with no childcare: twice the city rate", Sickness.shareFor(AgeBand.BABY, rate, 0, 1), 2 * rate, 1e-12);
        check("...with half of it, one and a half times", Sickness.shareFor(AgeBand.BABY, rate, .5, 1), 1.5 * rate, 1e-12);
        check("...with childcare for everybody, the city rate", Sickness.shareFor(AgeBand.BABY, rate, 1, 0), rate, 1e-12);
        check("seniors with no senior care: twice", Sickness.shareFor(AgeBand.SENIOR, rate, 1, 0), 2 * rate, 1e-12);
        check("...with it, the city rate", Sickness.shareFor(AgeBand.SENIOR, rate, 0, 1), rate, 1e-12);
        check("no band is ever sicker than the ceiling", Sickness.shareFor(AgeBand.BABY, .4, 0, 0),
                Sickness.MAX_SHARE, 1e-12);

        /* ============ 3. the ring: recovery, and nobody dies at once ============ */
        System.out.println("\n--- the ring ---");
        Sickness none = new Sickness();
        none.seed(0, 0, 1, 1);                       // a city nobody has ever been ill in
        check("fixture: a well city's ring is empty", none.share(AgeBand.ADULT), 0, 0);
        none.advanceMonth(rate, 0, 1, 1);             // the month a tenth fall ill
        check("the month they fall ill, they are all in the first slot", none.slot(AgeBand.ADULT, 0), rate, 1e-12);
        check("...and nobody can die of it yet", none.deathRates()[AgeBand.ADULT.ordinal()], 0, 0);
        none.advanceMonth(rate, 0, 1, 1);
        check("with no general care, half of them are better a month later",
                none.slot(AgeBand.ADULT, 1), rate * (1 - Sickness.RECOVERY_UNTREATED), 1e-12);
        check("...the city rate is met by new cases", none.share(AgeBand.ADULT), rate, 1e-12);
        check("...and two months sick is still not enough to die of it",
                none.deathRates()[AgeBand.ADULT.ordinal()], 0, 0);
        none.advanceMonth(rate, 0, 1, 1);
        double third = rate * Math.pow(1 - Sickness.RECOVERY_UNTREATED, 2);
        check("past two months, what is left of them can die", none.pastTwoMonths(AgeBand.ADULT), third, 1e-12);
        check("...at the adults' 2% a month", none.deathRates()[AgeBand.ADULT.ordinal()],
                third * Sickness.deathChance(AgeBand.ADULT), 1e-15);

        Sickness served = new Sickness();
        served.seed(0, 1, 1, 1);
        served.advanceMonth(rate, 1, 1, 1);
        served.advanceMonth(rate, 1, 1, 1);
        check("with general care for everybody, nine in ten are better in a month",
                served.slot(AgeBand.ADULT, 1), rate * (1 - Sickness.RECOVERY_SERVED), 1e-12);

        // The dead come out of their slot before the rest recover.
        none.advanceMonth(rate, 0, 1, 1);
        check("the dead leave the ring: a slot three months on keeps (1-h)(1-r)",
                none.slot(AgeBand.ADULT, 3),
                third * (1 - Sickness.deathChance(AgeBand.ADULT)) * (1 - Sickness.RECOVERY_UNTREATED), 1e-12);

        /* ============ 4. each age's chance, and the steady state ============ */
        System.out.println("\n--- each age's chance, and a city that has been this sick for ever ---");
        check("babies 4% a month", Sickness.deathChance(AgeBand.BABY), .04, 0);
        check("children 1%", Sickness.deathChance(AgeBand.CHILD), .01, 0);
        check("teens 1%", Sickness.deathChance(AgeBand.TEEN), .01, 0);
        check("adults 2%", Sickness.deathChance(AgeBand.ADULT), .02, 0);
        check("seniors 4%", Sickness.deathChance(AgeBand.SENIOR), .04, 0);

        Sickness steady = new Sickness();
        double cover = .5, childcare = .25, senior = .75;
        steady.seed(rate, cover, childcare, senior);
        double r = Sickness.recovery(cover);
        for (AgeBand b : AgeBand.values()) {
            double share = Sickness.shareFor(b, rate, childcare, senior);
            double h = Sickness.deathChance(b);
            double q = (1 - h) * (1 - r);
            double n = share / (1 + (1 - r) + (1 - r) * (1 - r) / (1 - q));
            double expected = h * n * (1 - r) * (1 - r) / (1 - q);
            check("steady state, " + b.getLabel().toLowerCase() + ": the dead are the closed form",
                    steady.deathRates()[b.ordinal()], expected, 1e-15);
        }
        double[] before = steady.getState();
        steady.advanceMonth(rate, cover, childcare, senior);
        double[] after = steady.getState();
        double drift = 0;
        for (int i = 0; i < AgeBand.values().length * Sickness.RING; i++) drift = Math.max(drift, Math.abs(after[i] - before[i]));
        check("...and a month at the same rate leaves it where it was", drift, 0, 1e-14);

        // The rate decides how many: a sickness that lifts lifts every slot alike.
        double lower = rate / 2;
        double ratio = steady.slot(AgeBand.ADULT, 4) / steady.slot(AgeBand.ADULT, 1);
        steady.advanceMonth(lower, cover, childcare, senior);
        check("when the rate halves, the adults sick are the new rate", steady.share(AgeBand.ADULT), lower, 1e-12);
        // Slot 4 lost its dead on the way to 5 and slot 1 had none to lose on the
        // way to 2; the same recovery and the same scaling fell on both.
        check("...and nobody's slot is favoured", steady.slot(AgeBand.ADULT, 5) / steady.slot(AgeBand.ADULT, 2),
                ratio * (1 - Sickness.deathChance(AgeBand.ADULT)), 1e-9);

        // Clinics save lives: the same rate, no care against full care.
        Sickness bare = new Sickness();
        Sickness cared = new Sickness();
        bare.seed(Health.UNTREATED_RATE, 0, 0, 0);
        cared.seed(Health.WELL_SERVED_RATE, 1, 1, 1);
        double[] bareDead = bare.deathRates(), caredDead = cared.deathRates();
        for (AgeBand b : AgeBand.values()) {
            double yearBare = 1 - Math.pow(1 - bareDead[b.ordinal()], 12);
            double yearCared = 1 - Math.pow(1 - caredDead[b.ordinal()], 12);
            System.out.printf("   %-9s sickness kills %.3f%% a year with no care, %.4f%% with all of it%n",
                    b.getLabel().toLowerCase(), yearBare * 100, yearCared * 100);
            assertTrue("  a city with no care loses far more " + b.getLabel().toLowerCase() + " to it",
                    bareDead[b.ordinal()] > caredDead[b.ordinal()] * 50);
        }

        // Hunger is a cause of sickness, so it is a cause of death.
        Health fed = new Health(), starving = new Health();
        fed.advanceMonth(500, 1000, 7, 0, 0);
        starving.advanceMonth(500, 1000, 7, 0, 1);
        Sickness fedRing = new Sickness(), starvingRing = new Sickness();
        fedRing.seed(fed.getSickRate(), .5, .5, .5);
        starvingRing.seed(starving.getSickRate(), .5, .5, .5);
        assertTrue("fixture: the hungry city is sicker", starving.getSickRate() > fed.getSickRate());
        assertTrue("...so more of it dies of illness",
                starvingRing.deathRates()[AgeBand.ADULT.ordinal()] > fedRing.deathRates()[AgeBand.ADULT.ordinal()]);

        /* ============ 5. the pyramid ============ */
        System.out.println("\n--- the dead reach the pyramid ---");
        PopulationCohorts withIllness = new PopulationCohorts();
        PopulationCohorts without = new PopulationCohorts();
        withIllness.migrate(20000);
        without.migrate(20000);
        double[] ones = {1, 1, 1, 1, 1};
        double[] ill = bare.deathRates();
        double openingAdults = withIllness.get(AgeBand.ADULT);
        withIllness.advanceMonth(ones, ill, 1);
        without.advanceMonth(ones, null, 1);
        check("the adults who died of illness are the opening band times the ring's rate",
                withIllness.getIllnessDeaths(AgeBand.ADULT), openingAdults * ill[AgeBand.ADULT.ordinal()], 1e-9);
        check("...on top of everybody else's deaths",
                withIllness.getDeaths(AgeBand.ADULT) - without.getDeaths(AgeBand.ADULT),
                openingAdults * ill[AgeBand.ADULT.ordinal()], 1e-9);
        check("a pyramid with no ring has no illness deaths", without.getIllnessDeaths(AgeBand.ADULT), 0, 0);

        /* ============ 6. a city ============ */
        System.out.println("\n--- a city, with and without clinics ---");
        Path root = Files.createTempDirectory("sicknesscheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game bareCity = new Game(files);
        Game clinicCity = new Game(files);
        double[] worstAudit = new double[1];
        double[] illEver = new double[2];
        quietly(() -> {
            bareCity.run();
            stock(bareCity);
            clinicCity.run();
            stock(clinicCity);
            BuildingManager cb = clinicCity.getBuildingManager();
            cb.addStack(cb.getTemplateByName("General Hospital"), 2, true);
            for (int m = 0; m < 48; m++) {
                bareCity.simulateMonths(1);
                clinicCity.simulateMonths(1);
                worstAudit[0] = Math.max(worstAudit[0], Math.max(
                        bareCity.getLastMoneyAudit().relative(), clinicCity.getLastMoneyAudit().relative()));
                illEver[0] += bareCity.getSickness().getLastDeaths();
                illEver[1] += clinicCity.getSickness().getLastDeaths();
            }
        });
        Health bareHealth = bareCity.getHealth(), clinicHealth = clinicCity.getHealth();
        System.out.printf("   no hospitals: coverage %.0f%%, sick %.1f%%, %,.1f ill past two months, %,.1f died of it in four years%n",
                bareHealth.getCoverage() * 100, bareHealth.getSickRate() * 100,
                bareCity.getSickness().peoplePastTwoMonths(bareCity.getCohorts()), illEver[0]);
        System.out.printf("   two hospitals: coverage %.0f%%, sick %.1f%%, %,.1f ill past two months, %,.1f died of it%n",
                clinicHealth.getCoverage() * 100, clinicHealth.getSickRate() * 100,
                clinicCity.getSickness().peoplePastTwoMonths(clinicCity.getCohorts()), illEver[1]);
        assertTrue("fixture: the hospitals gave the second city more coverage",
                clinicHealth.getCoverage() > bareHealth.getCoverage() + .2);
        check("the adults' share of the ring is the city's sick rate - output is untouched",
                bareCity.getSickness().share(AgeBand.ADULT), bareHealth.getSickRate(), 1e-12);
        assertTrue("the city with no hospitals lost people to illness", illEver[0] > 0);
        double barePer = illEver[0] / bareCity.getCohorts().total();
        double clinicPer = illEver[1] / clinicCity.getCohorts().total();
        assertTrue("...many times more, per head, than the city with them", barePer > clinicPer * 3);
        double booked = 0;
        for (AgeBand b : AgeBand.values()) booked += bareCity.getCohorts().getIllnessDeaths(b);
        check("the ring's dead are the pyramid's dead", bareCity.getSickness().getLastDeaths(), booked, 1e-9);
        assertTrue("every month passed the money audit", worstAudit[0] < 1e-6);

        /* ============ 7. a save, and a save from before ============ */
        System.out.println("\n--- a save ---");
        double[] ring = bareCity.getSickness().getState();
        assertTrue("fixture: the ring has something in it", bareCity.getSickness().pastTwoMonths(AgeBand.ADULT) > 0);
        quietly(() -> bareCity.saveGame(1, "sick"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        double worst = 0;
        double[] ringBack = back.getSickness().getState();
        for (int i = 0; i < ring.length; i++) worst = Math.max(worst, Math.abs(ringBack[i] - ring[i]));
        check("the whole ring comes back", ringBack.length == ring.length ? worst : 1, 0, 0);
        assertTrue("a malformed ring is refused whole", !new Sickness().restore(new double[]{1, 2, 3}));

        // A save from before the ring: the first month back still kills the long sick.
        quietly(() -> back.getSickness().reset());
        assertTrue("fixture: the ring is gone", !back.getSickness().isSeeded());
        quietly(() -> back.simulateMonths(1));
        assertTrue("a city with no ring seeds one and loses its long sick the same month",
                back.getSickness().isSeeded() && back.getSickness().getLastDeaths() > 0);

        System.out.println();
        System.out.println(fails == 0 ? "The long sick die, and clinics save them." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
