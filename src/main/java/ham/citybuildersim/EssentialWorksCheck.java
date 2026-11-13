package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Essential works ordered for their unmet service, including their ground (HW1).
 * WHY. Highways waited behind street sites at the twelve-month cap; schools
 * and police waited for a payback that the service itself must never face.
 * Fixtures cause short highways with and without ground, a long queue, an
 * unaffordable preferred street, and schools that cannot pay their upkeep.
 */
public final class EssentialWorksCheck {
    static int fails;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
    static final BuildAdvice.Measure HIGHWAYS = BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS);

    static void check(String label, boolean ok) {
        out.printf("%s %s%n", label, ok ? "OK" : "FAIL");
        if (!ok) fails++;
    }

    static void quietly(Runnable run) {
        PrintStream before = System.out;
        System.setOut(quiet);
        try { run.run(); } finally { System.setOut(before); }
    }

    static Game town(Path root, String name) {
        ChildcareCheck.quiet = quiet;
        Game g = ChildcareCheck.town(root, name);
        g.getAutoBuilder().setDebtLimit(0, null);
        g.getAutoBuilder().setCashAnyway(true, null);
        return g;
    }

    static AutoBuilder.Step step(Game g, BuildAdvice.Measure m) {
        return g.getAutoBuilder().steps().stream().filter(s -> s.measure().equals(m)).findFirst().orElse(null);
    }

    static void highways(Path root, boolean ground) {
        out.println("--- highways from cash " + (ground ? "with free ground" : "buying their ground") + " ---");
        Game g = town(root, "highways-" + ground);
        BuildingsTemplate highway = g.getBuildingManager().getTemplateByName("Elevated Highway");
        InfrastructureManager roads = g.getInfrastructureManager();
        roads.setLoad(BuildAdvice.highwayFlow(g) * 10);
        roads.setThrough(0, LandManager.SQ_FT_PER_KM2 * 100, 0);
        g.setCashForTest(AutoBuilder.reserve(g) + 1_000_000_000 * g.getPriceIndex().getIndex());
        AutoBuilder ab = g.getAutoBuilder();
        double[] sd = BuildAdvice.supplyDemand(g, HIGHWAYS, Map.of());
        check("fixture: highways are under-served, cash can buy one and its ground, debt limit forbids borrowing",
                sd[1] > sd[0] && AutoBuilder.groundFor(g, highway, 1) != null
                        && AutoBuilder.spendable(g) > g.quoteBuild(highway, 1).total && ab.getDebtLimit() == 0);
        // A street on site used to make the highway wait too (roads share their onSite map).
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("Paved Road"), 1, false));
        if (!ground) g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt());
        double owned = g.getLandManager().getOwnedSqFt(), debt = AutoBuilder.debt(g);
        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        AutoBuilder.Step s = step(g, HIGHWAYS);
        check("the short highway is ordered despite the street on site", s != null && s.ordered() > 0);
        check("its own land is bought where needed, with no new debt",
                s != null && (ground || ab.getLandSpent() > 0 && ab.getLandOffers() > 0
                        && g.getLandManager().getOwnedSqFt() > owned) && AutoBuilder.debt(g) == debt && ab.getBonds() == 0);
    }

    static void schoolsAndPolice(Path root) {
        out.println("--- schools and police serve people without a payback veto ---");
        Game g = town(root, "schools-police");
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getTeaches() == EducationType.NONE && t.getSafety() != SafetyType.POLICE) continue;
            int n = g.getBuildingManager().getQuantity(t.getId());
            if (n > 0) quietly(() -> g.getBuildingManager().retire(t, n));
        }
        quietly(() -> g.simulateMonths(1));
        g.getCrime().advanceMonth(Crime.Causes.canadaLike(100_000), 100_000, 0, 0, 0,
                g.getPopulationManager().getWagesPerType()[0]);
        g.getEconomyManager().setHealthcare(g.getEconomyManager().getNationalAccounts().getTotalRevenue() * 2, 0);
        g.getEconomyManager().refreshGovernmentAccounts(0, 0, 0, 0);
        g.setCashForTest(1_000_000_000 * g.getPriceIndex().getIndex());
        AutoBuilder ab = g.getAutoBuilder();
        for (BuildAdvice.Measure m : AutoBuilder.remit(g, CityNeeds.measure(g, CityNeeds.PLAIN))) {
            if (m.kind() != BuildAdvice.Kind.SCHOOL && m.kind() != BuildAdvice.Kind.POLICE) continue;
            if (!BuildAdvice.warranted(g, m)) continue;
            BuildAdvice.Suggestion s = BuildAdvice.suggestFor(g, null, m, g.getCash(),
                    g.getLandManager().getAvailableSqFt(), ab.getSlack(), Set.of(), false, true);
            check(m.label() + ": unmet service is essential, never subject to payback", AutoBuilder.essential(m)
                    && s != null && !AutoBuilder.paysBackFirst(m, s.template()));
        }
        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        for (AutoBuilder.Step s : ab.steps()) {
            if (s.measure().kind() == BuildAdvice.Kind.SCHOOL || s.measure().kind() == BuildAdvice.Kind.POLICE) {
                if (!BuildAdvice.warranted(g, s.measure())) continue;
                check(s.measure().label() + ": ordered without a payback hold", s.ordered() > 0
                        && s.cut() != AutoBuilder.Cut.PAYBACK && s.payback() == null);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path root = TempDirs.create("essential-works");
        highways(root, true);
        highways(root, false);
        schoolsAndPolice(root);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
