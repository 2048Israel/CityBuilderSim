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
 * Fixtures cause short highways with and without ground, a street on site,
 * a partial power order from scarce cash, and schools that cannot pay their upkeep.
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
        quietly(() -> g.handleLongBondForCash(2 * AutoBuilder.annualGdp(g),
                Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
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
                        && AutoBuilder.spendable(g) > g.quoteBuild(highway, 1).total && !ab.within(g));
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

    static void lostOutput(Path root) {
        out.println("--- prevented losses use the service the economy actually loses ---");
        Game g = town(root, "prevented-loss");
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 2);
        for (Sector sector : g.getSectors().all()) sector.setEnergyRatio(.5);
        double[] sd = BuildAdvice.supplyDemand(g, power, Map.of());
        check("fixture: the grid is rationing and the city's books contain output to restore",
                sd[0] > 0 && sd[1] > sd[0] && g.getSectors().all().stream()
                        .anyMatch(s -> s.statement().revenue > s.statement().inputs));
        WorksValue.Value value = WorksValue.of(g, power, Map.of(), coal, 1, g.getLandManager().getAvailableSqFt());
        check("a plant that relieves rationing prevents a positive output loss", value.avoided() > 0);
        double already = WorksValue.avoided(g, power, Map.of(coal, 1000), Map.of(coal, 1), 0);
        check("a plant beyond all the horizon's demand claims no prevented rationing loss", already == 0);
        check("the card exposes the same component sum, including land, prevented output and payroll",
                BuildCard.worksValue(g, coal, power).words().contains("prevented output loss")
                        && value.land() > 0 && value.payroll() > 0 && value.jobs() > 0);
        // A physical surplus below the reserve target caused the original false credit.
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        u.setWattsConsumption(u.getProduction() / 1.05);
        for (Sector sector : g.getSectors().all()) sector.setEnergyRatio(1);
        double[] surplus = BuildAdvice.supplyDemand(g, power, Map.of());
        check("fixture: physical power is sufficient, fifteen-percent reserve is not, and the projection has no growth",
                surplus[0] >= surplus[1] && surplus[0] < surplus[1] * 1.15 && BuildAdvice.lifeTrend(g, BuildAdvice.LIFE_MONTHS) == 1);
        WorksValue.Value reserve = WorksValue.of(g, power, Map.of(), coal, 1, g.getLandManager().getAvailableSqFt());
        check("a reserve-only plant claims zero physical relief and zero prevented output", reserve.used() == 0 && reserve.avoided() == 0);

    }

    static void scarceCash(Path root) {
        out.println("--- an unaffordable full power order takes the cash-funded step that can raise service ---");
        Game g = town(root, "scarce-cash");
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 4);
        BuildingsTemplate cheapest = null;
        double one = Double.POSITIVE_INFINITY, second = Double.POSITIVE_INFINITY;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (power.serves(t) && BuildAdvice.unit(g, power, t) > 0) {
            double price = g.quoteBuild(t, 1).total;
            if (price < one) { second = one; one = price; cheapest = t; }
            else second = Math.min(second, price);
        }
        if (cheapest == null) { check("fixture has a plant that can raise power service", false); return; }
        BuildingsTemplate plant = cheapest;
        g.setCashForTest(AutoBuilder.reserve(g) + one + Math.min(one * .01, (second - one) / 2));
        check(String.format("fixture: power is short; cash %.2f buys one %s at %.2f, below the next plant %.2f",
                        AutoBuilder.spendable(g), plant.getName(), one, second),
                BuildAdvice.warranted(g, power) && !g.getAutoBuilder().atTarget(g, power)
                        && g.quoteBuild(plant, 2).total > AutoBuilder.spendable(g) && !g.getAutoBuilder().within(g));
        double debt = AutoBuilder.debt(g);
        g.getAutoBuilder().setOn(true, null);
        quietly(() -> g.getAutoBuilder().pass(g));
        AutoBuilder.Step s = step(g, power);
        check("the affordable step orders one " + plant.getName() + " from cash without borrowing: " + s,
                s != null && s.ordered() == 1 && s.building().equals(plant.getName())
                        && s.cost() <= one * 1.01 && AutoBuilder.debt(g) == debt && s.borrowed() == 0);
    }

    static void professionalDemand(Path root) {
        out.println("--- professional eligibility on both sides of the useful-demand threshold ---");
        Game g = ChildcareCheck.town(root, "professional-threshold", 4);
        EducationType type = EducationType.MEDICAL;
        BuildAdvice.Measure m = BuildAdvice.Measure.school(type);
        BuildingManager bm = g.getBuildingManager();
        for (BuildingsTemplate t : bm.getTemplates()) if (t.getTeaches() == type) {
            int n = bm.getQuantity(t.getId());
            if (n > 0) quietly(() -> bm.retire(t, n));
        }
        g.setCashForTest(1e12);
        BuildingsTemplate clinic = bm.getTemplateByName("Community Health Centre");
        quietly(() -> g.buildStack(clinic, 400, true));
        PopulationManager pm = g.getPopulationManager();
        pm.restoreLicensed(new double[JobType.values().length]);
        pm.setStudying(new double[WageBand.values().length]);
        double least = bm.getTemplates().stream().filter(t -> t.getTeaches() == type)
                .mapToDouble(BuildingsTemplate::getCapacity).min().orElseThrow();
        double[] skilled = new double[WageBand.values().length];
        skilled[type.requires().ordinal()] = pm.getWorkforce() * .9;
        pm.restoreSkilledHeads(skilled);
        double willing = g.getEducation().willingShare(type, g.getLabourMarket())
                * Education.ENROLMENT_RATE * type.months();
        check("fixture: eligible graduates will study and the city's doctor vacancies exceed one school's threshold",
                willing > 0 && CityNeeds.hires(g, type, 1) > least * BuildAdvice.FIRST_WORK_SHARE * 1.1);
        skilled[type.requires().ordinal()] = least * BuildAdvice.FIRST_WORK_SHARE * .9 / willing;
        pm.restoreSkilledHeads(skilled);
        double below = BuildAdvice.supplyDemand(g, m, Map.of())[1];
        check("below thirty percent the professional school is unwarranted and omitted",
                below > 0 && below < least * BuildAdvice.FIRST_WORK_SHARE
                        && !BuildAdvice.warranted(g, m)
                        && !AutoBuilder.kept(g, m, CityNeeds.measure(g, CityNeeds.PLAIN)));
        skilled[type.requires().ordinal()] = least * BuildAdvice.FIRST_WORK_SHARE * 1.1 / willing;
        pm.restoreSkilledHeads(skilled);
        double above = BuildAdvice.supplyDemand(g, m, Map.of())[1];
        check("above thirty percent, with no school, the professional service is warranted and not maintained",
                above >= least * BuildAdvice.FIRST_WORK_SHARE && above < least * .5
                        && BuildAdvice.warranted(g, m) && AutoBuilder.kept(g, m, CityNeeds.measure(g, CityNeeds.PLAIN))
                        && !BuildAdvice.maintained(g, m, Map.of(), g.getAutoBuilder().target()));
    }

    static void filledPostCosts(Path root) {
        out.println("--- staff-short works pay only the posts filled in the utility books ---");
        Game g = town(root, "filled-post-costs");
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        PopulationManager pm = g.getPopulationManager();
        double[] wages = pm.getWagesPerType(), fill = pm.getJobFillRate();
        double paid = 0, full = 0;
        for (JobType j : JobType.values()) {
            full += coal.getJobs(j) * wages[j.ordinal()];
            paid += coal.getJobs(j) * wages[j.ordinal()] * fill[j.ordinal()];
        }
        check("fixture: the plant has paid staff and unfilled posts", paid > 0 && paid < full);
        double card = BuildCard.runningCost(g, coal);
        check("the card's running bill charges upkeep and filled-post payroll",
                Math.abs(card - coal.getUpkeep() - paid) < 1e-9 * Math.max(1, full));
        WorksValue.Value v = WorksValue.of(g, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER),
                Map.of(), coal, 1, g.getLandManager().getAvailableSqFt());
        double delay = Math.pow(1 + Math.max(0, BuildAdvice.lifeRate(g)) / 12, -v.lead());
        double nonPayroll = BuildAdvice.running(g, coal, Map.of()) - card;
        double expected = (nonPayroll + coal.getUpkeep() + paid) * BuildAdvice.lifeFactor(g) * delay;
        check("the plan's running cost and payroll use the same filled-post basis",
                Math.abs(v.payroll() - paid) < 1e-9 * full
                        && Math.abs(v.running() - expected) < 1e-9 * Math.max(1, Math.abs(expected)));
    }

    static void cappedCare(Path root) {
        out.println("--- care prevents only the sickness the model can reduce ---");
        Game g = town(root, "capped-care");
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        BuildingManager bm = g.getBuildingManager();
        for (BuildingsTemplate t : bm.getTemplates()) if (t.getCare() == CareType.GENERAL) {
            int n = bm.getQuantity(t.getId());
            if (n > 0) quietly(() -> bm.retire(t, n));
        }
        BuildingsTemplate clinic = bm.getTemplateByName("Community Health Centre");
        double demand = BuildAdvice.supplyDemand(g, care, Map.of())[1];
        int count = 1 + (int) Math.ceil((demand + g.getHealthcare().getPricedOut(CareType.GENERAL))
                / BuildAdvice.unit(g, care, clinic));
        Map<BuildingsTemplate, Integer> order = Map.of(clinic, count);
        double before = BuildAdvice.cover(g, care, Map.of()), after = BuildAdvice.cover(g, care, order);
        check("fixture: only founding care remains short of the city, and the order restores full coverage",
                bm.getCareCapacity(CareType.GENERAL) == Healthcare.foundingCapacity(CareType.GENERAL)
                        && before < 1 && after == 1);
        g.getHealth().advanceMonth(before, 1, 0, 1, 1, 1, 1);
        g.getEconomyManager().setHealthRatio(g.getHealth().getWorkRatio());
        Health covered = new Health();
        covered.advanceMonth(after, 1, 0, 1, 1, 1, 1);
        check("fixture: sickness is capped before and after the care improvement",
                g.getHealth().getSickRate() == Health.MAX_SICK_RATE && covered.getSickRate() == Health.MAX_SICK_RATE);
        double restored = WorksValue.outputRestored(g, care, Map.of(), order, BuildAdvice.NOW, Map.of());
        check("care that leaves sickness capped claims zero prevented output", restored == 0);
        Health untreated = new Health(), treated = new Health();
        untreated.advanceMonth(before, 1, 0, 0, 0, .2, .02);
        treated.advanceMonth(after, 1, 0, 0, 0, .2, .02);
        check("fixture: uncapped care comparison has the same other causes and no outbreak",
                untreated.getSickRate() < Health.MAX_SICK_RATE && untreated.getOutbreakSeverity() == 0
                        && treated.getOutbreakSeverity() == 0);
        g.getHealth().restore(untreated.getState());
        g.getEconomyManager().setHealthRatio(untreated.getWorkRatio());
        double potential = 0;
        for (Sector sector : g.getSectors().all()) {
            Sector.Statement b = sector.statement();
            potential += Math.max(0, b.revenue - b.inputs - b.electricity - b.water) / sector.getHealthRatio();
        }
        double expected = potential * (treated.getWorkRatio() - untreated.getWorkRatio());
        restored = WorksValue.outputRestored(g, care, Map.of(), order, BuildAdvice.NOW, Map.of());
        check("uncapped care's credit equals the model's sickness reduction, including its unhoused cause",
                expected > 0 && Math.abs(restored - expected) < 1e-9 * Math.max(1, expected));
    }

    static void adviceParity(Path root) {
        out.println("--- the Build advice and automatic order use the same plan ---");
        Game g = town(root, "advice-parity");
        AutoBuilder ab = g.getAutoBuilder();
        ab.setSlack(.35, null);
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 2);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        var all = CityNeeds.measure(g, CityNeeds.PLAIN);
        BuildAdvice.Suggestion card = BuildAdvice.suggestFor(g, BuildAdvice.needFor(all, power), power,
                g.getCash(), g.getLandManager().getAvailableSqFt());
        check("fixture: the power card leads the automatic remit and its full order is fundable",
                card != null && AutoBuilder.remit(g, all).get(0).equals(power)
                        && g.quoteBuild(card.template(), card.count()).total < AutoBuilder.spendable(g)
                        && card.landSqFt() < g.getLandManager().getAvailableSqFt());
        if (card == null) return;
        String cardValue = card.value().words();
        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        AutoBuilder.Step order = step(g, power);
        check("the card uses the player's automatic reserve and full-service target",
                card.ahead().slack() == ab.getSlack());
        check("the automatic path orders the card's exact building and count",
                order != null && order.ordered() == card.count() && order.building().equals(card.template().getName()));
        check("the automatic path records the exact whole-plan valuation displayed by the card",
                ab.powerValues().size() == 1
                        && ab.powerValues().get(0).equals(card.count() + " " + card.template().getName() + ": " + cardValue));
    }

    static void overduePending(Path root) {
        out.println("--- pending capacity has a fixed completion deadline across monthly passes and a reload ---");
        Game g = town(root, "overdue-pending");
        AutoBuilder ab = g.getAutoBuilder();
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate wind = bm.getTemplateByName("Wind Farm");
        BuildingsTemplate road = bm.getTemplateByName("Paved Road");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        quietly(() -> g.buildStack(bm.getTemplateByName("Construction Depot"), 8, true));
        quietly(() -> g.buildStack(wind, 100, false));
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        utilities.setWattsConsumption(utilities.getProduction() * 1.2);
        BuildingsStacks site = bm.getStack(wind);
        double owed = site.getUnderConstruction() * (double) wind.getConstructionPoints() - site.getConstructionProgress();
        double first = Math.ceil(g.onSiteMonths(wind)
                * Math.max(0, wind.getConstructionPoints() - site.getConstructionProgress()) / owed);
        int began = g.getMonth();
        check("fixture: operating power is short, pending wind covers it, and the first completion is quoted within three years",
                !BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW)
                        && BuildAdvice.maintained(g, power, BuildAdvice.onSite(g, power), BuildAdvice.opening(g, 0, ab.getSlack()))
                        && Double.isFinite(first) && first > 0 && first <= 36);
        if (!Double.isFinite(first) || first <= 0 || first > 36) return;
        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        check("pending capacity covers the shortage before its quoted deadline",
                step(g, power) != null && step(g, power).outcome() == AutoBuilder.Outcome.AHEAD);
        String saved = new com.google.gson.Gson().toJson(ab.toState());
        check("the fixed pending deadline is part of the saved automatic-builder state", saved.contains("pendingUntil"));
        // A real later order and the player's priority delay the already quoted wind site.
        quietly(() -> g.buildStack(road, 10_000, false));
        bm.getControl().setOrder(java.util.List.of(ConstructionControl.keyOf(road), ConstructionControl.keyOf(wind)));
        boolean[] early = {false}, late = {false}, stayedShort = {true};
        g.autoBuildProbeForTest = after -> {
            if (!after) utilities.setWattsConsumption(utilities.getProduction() * 1.2);
            else {
                AutoBuilder.Step order = step(g, power);
                stayedShort[0] &= !BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW);
                if (order != null && order.ordered() > 0) {
                    if (g.getMonth() <= began + first) early[0] = true;
                    else late[0] = true;
                }
            }
        };
        int reloadAt = began + Math.max(1, (int) first / 2);
        while (g.getMonth() <= began + first + 1) {
            quietly(() -> g.simulateMonths(1));
            if (g.getMonth() == reloadAt)
                ab.restore(new com.google.gson.Gson().fromJson(saved, AutoBuilder.State.class));
        }
        g.autoBuildProbeForTest = null;
        check("fixture: later priority work kept operating power short throughout the original deadline", stayedShort[0]);
        check("monthly passes respect the original pending deadline before it expires", !early[0]);
        check("past that deadline the automatic path orders more power despite the pending wind, including after reload", late[0]);
    }

    public static void main(String[] args) throws Exception {
        Path root = TempDirs.create("essential-works");
        highways(root, true);
        highways(root, false);
        schoolsAndPolice(root);
        lostOutput(root);
        scarceCash(root);
        professionalDemand(root);
        filledPostCosts(root);
        cappedCare(root);
        adviceParity(root);
        overduePending(root);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
