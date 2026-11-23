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
 * The town is ChildcareCheck.town() at x1 (played 36 months) with a bond and no debt
 * room, town() here; what a section reads off it is set there by hand. Since FS15
 * the premises that sat on the played town's own figures are caused: the highway
 * forecast's traffic scaled between its two edges (mostCovered()), the streets'
 * share set (SERVED_STREETS), spare care by the people scaled to it (scalePeople()),
 * and the staff-short plant by the workforce set to half the posts.
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
        // THE FIXTURE CAUSES IT (PW2): a 1900 city knows the wind farm from E16 (December 1980) since each plant is offered from its year
        // (Jerus's Q2), and these towns weigh wind against coal; so their research found its code at the founding (Research.knownFrom(), R3's lever).
        knowWind(g);
        quietly(() -> g.handleLongBondForCash(2 * AutoBuilder.annualGdp(g),
                Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        g.getAutoBuilder().setDebtLimit(0, null);
        g.getAutoBuilder().setCashAnyway(true, null);
        return g;
    }

    /** A city's research finds the Wind Farm's code (its since) at month 1, so a 1900 city knows it: the fixture's lever, since PW2 dates every plant. */
    static void knowWind(Game g) {
        g.getResearch().restore(Map.of(), Map.of(), Map.of(), Map.of(g.getBuildingManager().getTemplateByName("Wind Farm").getSinceText(), 1));
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

    /**
     * Sets the city's power draw so the advice's demand is `share` of its supply (BuildAdvice.supplyDemand(), read at two draws and solved, the
     * works pending in it): served with a margin under 1, short over it. PW1, 2026-11-20: the advice plans power for the calm peak, the draw
     * times PowerDispatch.PLAN_PEAK over the firm supply, so a draw set as a share of the utility's production no longer says served or short.
     */
    static void powerDrawAt(Game g, double share) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        double c1 = u.getProduction(), c2 = 2 * c1;
        u.setWattsConsumption(c1);
        double[] one = BuildAdvice.supplyDemand(g, power, Map.of());
        u.setWattsConsumption(c2);
        double[] two = BuildAdvice.supplyDemand(g, power, Map.of());
        double perKw = (two[1] - one[1]) / (c2 - c1);
        u.setWattsConsumption(Math.max(0, c1 + (share * one[0] - one[1]) / perKw));
    }

    /** These cases isolate another essential order; physical wires cover even its longest forecast. */
    static void serveGrid(Game g) {
        BuildAdvice.Measure grid = BuildAdvice.Measure.of(BuildAdvice.Kind.GRID);
        BuildAdvice.Ahead at = BuildAdvice.opening(g, BuildAdvice.LIFE_MONTHS, g.getAutoBuilder().getSlack());
        double site = Math.max(1, g.getBuildingOutputAtEveryPost());
        SubstationCheck.wiresFor(g, BuildAdvice.supplyDemand(g, grid, Map.of(), at)[1]);
        // Wires need repairs as well as posts. Preserve the builders this isolated ordering case already had.
        BuildingManager b = g.getBuildingManager();
        for (int n = 0; n < 64 && g.getBuildingOutputAtEveryPost() < site; n++)
            b.addStack(b.getTemplateByName("Construction Depot"), 1, true);
        check("fixture: standing substations carry the isolated order's entire forecast",
                BuildAdvice.maintained(g, grid, Map.of(), at));
        check("fixture: the wired town retains its construction output after the substations' repairs",
                g.getBuildingOutputAtEveryPost() >= site);
    }

    /** Give unrelated essential services their operating capacity before isolating an ahead-of-need order. */
    static boolean serveOtherEssentials(Game g, BuildAdvice.Measure except) {
        BuildingManager bm = g.getBuildingManager();
        for (int pass = 0; pass < 3; pass++) {
            for (BuildAdvice.Measure m : AutoBuilder.remit(g, CityNeeds.measure(g, CityNeeds.PLAIN))) {
                if (m.equals(except) || !AutoBuilder.essential(m)
                        || BuildAdvice.maintained(g, m, Map.of(), BuildAdvice.NOW)) continue;
                for (BuildingsTemplate t : bm.getTemplates()) {
                    if (!m.serves(t) || !(BuildAdvice.unit(g, m, t) > 0) || t.isHydro()) continue;
                    long count = BuildAdvice.count(g, m, Map.of(), Map.of(t, 1L),
                            BuildAdvice.NOW, BuildAdvice.MOST, true)[0];
                    if (count > 0) quietly(() -> g.buildStack(t, count, true));
                    break;
                }
            }
            g.getServicesManager().updateServices();
            g.getServicesManager().updateInfrastructure();
        }
        return AutoBuilder.remit(g, CityNeeds.measure(g, CityNeeds.PLAIN)).stream()
                .filter(m -> !m.equals(except) && AutoBuilder.essential(m))
                .allMatch(m -> BuildAdvice.maintained(g, m, Map.of(), BuildAdvice.NOW));
    }

    /** The town carrying its traffic as it stood times `f`, every stream alike, and streets built past it: the
     *  forecast order has no unrelated unserved street whose construction promise would take precedence. A town with
     *  modes reads its streams, not its total (InfrastructureManager.getEffectiveLoad), so both are scaled. */
    static void carry(Game g, BuildingsTemplate highway, double load, double[] streams, double f) {
        InfrastructureManager roads = g.getInfrastructureManager();
        double[] scaled = streams.clone();
        for (int i = 0; i < scaled.length; i++) scaled[i] *= f;
        roads.setLoad(load * f);
        roads.setBreakdown(scaled);
        roads.setBuiltCapacity(roads.getStreetLoad() * 1.05
                + highway.getCapacity() * 2 - InfrastructureManager.BASE_CAPACITY);
    }

    /** The most of its traffic, as a multiple, at which the town's highways are maintained under `p`, by bisection
     *  (FS15). */
    static double mostCovered(Game g, BuildingsTemplate highway, double load, double[] streams, BuildAdvice.Ahead p) {
        double lo = 0, hi = 1;
        for (carry(g, highway, load, streams, hi); BuildAdvice.maintained(g, HIGHWAYS, Map.of(), p) && hi < 1e12;
                carry(g, highway, load, streams, hi)) hi *= 2;
        for (int i = 0; i < 60; i++) {
            double mid = (lo + hi) / 2;
            carry(g, highway, load, streams, mid);
            if (BuildAdvice.maintained(g, HIGHWAYS, Map.of(), p)) lo = mid; else hi = mid;
        }
        return lo;
    }

    static void anticipateHighway(Path root) {
        out.println("--- a growing highway is ordered before its construction lead consumes the reserve ---");
        Game g = town(root, "highway-ahead");
        AutoBuilder ab = g.getAutoBuilder();
        BuildingsTemplate highway = g.getBuildingManager().getTemplateByName("Elevated Highway");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        check("fixture: unrelated essential services already have their operating capacity",
                serveOtherEssentials(g, HIGHWAYS));
        // Growing population history and homes for the people it predicts.
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("House"), 1000, true));
        var history = g.getHistorySave().getPopulation();
        long pop = g.getPopulationManager().getPopulation();
        history.clear();
        for (int i = 0; i <= 12; i++) history.add(Math.round(pop * (.7 + .3 * i / 12)));
        InfrastructureManager roads = g.getInfrastructureManager();
        roads.setThrough(highway.getCapacity() * 2, LandManager.SQ_FT_PER_KM2 * 100, 0);
        g.getBusinessInvestment().restorePopulationHistory(history);
        double lead = g.quoteBuild(highway, 1).months;
        double shortGrowth = BuildAdvice.opening(g, 0, ab.getSlack()).scale();
        double longGrowth = BuildAdvice.opening(g, lead, ab.getSlack()).scale();
        // Find traffic whose six-month reserve is covered, but its construction-lead reserve is short.
        // FS15: the traffic caused from both sides. Until FS15 a load of today's capacity over the two growths' mean was
        // set, but the town has modes and reads its streams, so the load moved nothing and the premise was the played
        // town's own traffic; on HS1's path it no longer fell between. Now the town's traffic, every stream alike, is
        // scaled to the midpoint of the most covered today and the most covered at the opening, each by bisection.
        double load = roads.getLoad();
        double[] streams = new double[Traffic.values().length];
        for (Traffic t : Traffic.values()) streams[t.ordinal()] = roads.getLoad(t);
        double coveredNow = mostCovered(g, highway, load, streams, BuildAdvice.opening(g, 0, ab.getSlack()));
        double coveredAtOpening = mostCovered(g, highway, load, streams, BuildAdvice.opening(g, lead, ab.getSlack()));
        carry(g, highway, load, streams, (coveredNow + coveredAtOpening) / 2);
        out.printf("      the town's traffic covered today to x%.4f, at the opening to x%.4f%n", coveredNow, coveredAtOpening);
        check("fixture: operating streets carry the calibrated highway forecast's traffic",
                BuildAdvice.maintained(g, BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS), Map.of(), BuildAdvice.NOW));
        out.printf("lead %.2f short growth %.3f long growth %.3f sd %s%n", lead, shortGrowth, longGrowth, java.util.Arrays.toString(BuildAdvice.supplyDemand(g, HIGHWAYS, Map.of())));
        boolean current = BuildAdvice.maintained(g, HIGHWAYS, Map.of(), BuildAdvice.opening(g, 0, ab.getSlack()));
        boolean atOpening = BuildAdvice.maintained(g, HIGHWAYS, Map.of(), BuildAdvice.opening(g, lead, ab.getSlack()));
        check("fixture: today's highway and six-month reserve are covered, but demand outgrows them before a new highway opens",
                current && !atOpening && BuildAdvice.warranted(g, HIGHWAYS) && lead > BuildAdvice.HORIZON
                        && Double.isFinite(longGrowth) && longGrowth > shortGrowth);
        BuildAdvice.Suggestion card = BuildAdvice.suggestFor(g, null, HIGHWAYS, g.getCash(),
                g.getLandManager().getAvailableSqFt());
        check("the highway card's projection uses the quoted opening of its entire final order",
                card != null && Math.abs(card.ahead().months()
                        - BuildAdvice.opening(g, g.quoteBuild(card.template(), card.count()).months, ab.getSlack()).months()) < 1e-9);
        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        AutoBuilder.Step order = step(g, HIGHWAYS);
        out.println("      highway order " + order + ", held " + (card == null ? "none" : ab.heldBackWords(card)));
        check("the card and automatic path both order a highway before the six-month reserve is used",
                card != null && order != null && order.ordered() > 0
                        && order.building().equals(card.template().getName()));
    }

    static void schoolsAndPolice(Path root) {
        out.println("--- schools and police serve people without a payback veto ---");
        Game g = town(root, "schools-police");
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getTeaches() == EducationType.NONE && t.getSafety() != SafetyType.POLICE) continue;
            long n = g.getBuildingManager().getQuantity(t.getId());
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
        g.setCashForTest(1e14);
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("Construction Depot"), 8, true));
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 2);
        for (Sector sector : g.getSectors().all()) sector.setEnergyRatio(.5);
        double[] sd = BuildAdvice.supplyDemand(g, power, Map.of());
        check("fixture: the grid is rationing and the city's books contain output to restore",
                sd[0] > 0 && sd[1] > sd[0]
                        && g.quoteBuild(coal, 1).months < BuildAdvice.LIFE_MONTHS && g.getSectors().all().stream()
                        .anyMatch(s -> s.statement().revenue > s.statement().inputs));
        WorksValue.Value value = WorksValue.of(g, power, Map.of(), coal, 1, g.getLandManager().getAvailableSqFt());
        check("a plant that relieves rationing prevents a positive output loss", value.avoided() > 0);
        double already = WorksValue.avoided(g, power, Map.of(coal, 1000L), Map.of(coal, 1L), 0);
        check("a plant beyond all the horizon's demand claims no prevented rationing loss", already == 0);
        check("the card exposes the same component sum, including land, prevented output and payroll",
                BuildCard.worksValue(g, coal, power).words().contains("prevented output loss")
                        && value.land() > 0 && value.payroll() > 0 && value.jobs() > 0);
        // A physical surplus below the reserve target caused the original false credit.
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        // ...the draw 5% inside the supply as the advice reads it (PW1: the calm peak over the firm supply).
        powerDrawAt(g, 1 / 1.05);
        for (Sector sector : g.getSectors().all()) sector.setEnergyRatio(1);
        double[] surplus = BuildAdvice.supplyDemand(g, power, Map.of());
        check("fixture: physical power is sufficient, fifteen-percent reserve is not, and the projection has no growth",
                surplus[0] >= surplus[1] && surplus[0] < surplus[1] * 1.15 && BuildAdvice.lifeTrend(g, BuildAdvice.LIFE_MONTHS) == 1);
        WorksValue.Value reserve = WorksValue.of(g, power, Map.of(), coal, 1, g.getLandManager().getAvailableSqFt());
        check("a reserve-only plant claims zero physical relief and zero prevented output", reserve.used() == 0 && reserve.avoided() == 0);

    }

    static void serveStreets(Game g) {
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        BuildingsTemplate paved = g.getBuildingManager().getTemplateByName("Paved Road");
        long[] n = BuildAdvice.count(g, streets, Map.of(), Map.of(paved, 1L),
                g.getAutoBuilder().target(), BuildAdvice.MOST, true);
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        if (n[0] > 0) quietly(() -> g.buildStack(paved, n[0], true));
        g.getServicesManager().updateInfrastructure();
        check("fixture: streets already serve their full demand before the power allocation",
                BuildAdvice.maintained(g, streets, Map.of(), BuildAdvice.NOW));
    }

    static void scarceCash(Path root) {
        out.println("--- an unaffordable full power order takes the cash-funded step that can raise service ---");
        Game g = town(root, "scarce-cash");
        serveStreets(g);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 4);
        serveGrid(g);
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
            long n = bm.getQuantity(t.getId());
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

    static void pendingIndustrialLoad(Path root) {
        out.println("--- a known industrial opening needs its power ahead ---");
        Game g = town(root, "pending-industrial-load");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        check("fixture: unrelated essential services already have their operating capacity",
                serveOtherEssentials(g, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)));
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate mill = bm.getTemplateByName("Large Mini-Mill");
        quietly(() -> g.buildStack(mill, 1, false));
        BuildingsStacks stack = bm.getStack(mill);
        stack.setConstructionProgress(Math.max(0, mill.getConstructionPoints()
                - g.getBuildingOutputAtEveryPost() * .25));
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        // ...the draw a fifth inside the supply as the advice reads it (PW1: the calm peak over the firm supply).
        powerDrawAt(g, .8);
        serveGrid(g);
        double first = bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost());
        check("fixture: a paid mill opens within the planning horizon while current power has its reserve",
                stack.getUnderConstruction() == 1 && first > 0 && first < BuildAdvice.HORIZON
                        && BuildAdvice.maintained(g, power, Map.of(), g.getAutoBuilder().target()));
        BuildAdvice.Ahead before = new BuildAdvice.Ahead(first * .5, 1, 0);
        BuildAdvice.Ahead after = new BuildAdvice.Ahead(first + .01, 1, 0);
        double early = BuildAdvice.supplyDemand(g, power, Map.of(), before)[1];
        double late = BuildAdvice.supplyDemand(g, power, Map.of(), after)[1];
        // ...at the calm peak (PW1: the advice's demand is the draw x PowerDispatch.PLAN_PEAK).
        check("the forecast adds the mill's physical electrical draw only once it opens",
                Math.abs(late - early - PowerDispatch.PLAN_PEAK * mill.getElectricityConsumption()) < 1e-8
                        && WorksValue.pendingPowerLoad(g, before.months()) == 0);
        BuildAdvice.Suggestion card = BuildAdvice.suggestFor(g, null, power, g.getCash(),
                g.getLandManager().getAvailableSqFt());
        check("the power card anticipates the ordered industrial load before it is operating",
                card != null && card.count() > 0);
        g.getAutoBuilder().setOn(true, null);
        quietly(() -> g.getAutoBuilder().pass(g));
        AutoBuilder.Step order = step(g, power);
        check("the automatic path orders power before that industrial completion",
                order != null && order.ordered() > 0 && stack.getUnderConstruction() == 1);
    }

    static void interimCare(Path root) {
        out.println("--- a slow hospital cannot hold faster care ---");
        Game g = town(root, "interim-care");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        BuildingManager bm = g.getBuildingManager();
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        for (BuildingsTemplate t : bm.getTemplates()) {
            long n = bm.getQuantity(t.getId());
            if (care.serves(t) && n > 0) quietly(() -> bm.retire(t, n));
        }
        BuildingsTemplate clinic = bm.getTemplateByName("Walk-in Clinic");
        double demand = BuildAdvice.supplyDemand(g, care, Map.of())[1];
        double staffed = clinic.getCapacity() * BuildAdvice.staffing(clinic,
                g.getPopulationManager().getJobFillRate());
        double base = BuildAdvice.supplyDemand(g, care, Map.of())[0];
        int standing = Math.max(0, (int) Math.ceil((demand - base) / staffed) - 1);
        if (standing > 0) quietly(() -> g.buildStack(clinic, standing, true));
        BuildingsTemplate hospital = bm.getTemplateByName("General Hospital");
        quietly(() -> g.buildStack(hospital, 1, false));
        Map<BuildingsTemplate, Long> raw = BuildAdvice.onSite(g, care);
        double first = BuildAdvice.firstCompletion(g, care, raw);
        check("fixture: operating care is short, the slow hospital covers it, and a clinic can open earlier",
                !BuildAdvice.maintained(g, care, Map.of(), BuildAdvice.NOW)
                        && BuildAdvice.maintained(g, care, raw, g.getAutoBuilder().target())
                        && Math.ceil(g.quoteBuild(clinic, 1).months) < first);
        BuildAdvice.Suggestion card = BuildAdvice.suggestFor(g, null, care, g.getCash(),
                g.getLandManager().getAvailableSqFt());
        check("the care card offers an earlier useful order while the hospital is pending",
                card != null && Math.ceil(card.lead()) < first);
        g.getAutoBuilder().setOn(true, null);
        quietly(() -> g.getAutoBuilder().pass(g));
        AutoBuilder.Step order = step(g, care);
        check("the automatic path places that service's interim order",
                order != null && order.ordered() > 0);
    }

    static void stagedCosts(Path root) {
        out.println("--- maintained service prices later works and their borrowing ---");
        Game g = town(root, "staged-costs");
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        var history = g.getHistorySave();
        long people = g.getPopulationManager().getPopulation();
        history.getPopulation().clear();
        history.getMonth().clear();
        history.getPopulation().add(people / 2);
        history.getPopulation().add(people);
        history.getMonth().add(0);
        history.getMonth().add(g.getMonth());
        BuildingsTemplate clinic = g.getBuildingManager().getTemplateByName("Walk-in Clinic");
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        g.setCashForTest(0);
        double debt = AutoBuilder.debt(g);
        double rate = g.getDebtManager().quoteRate(0, Game.BUILD_BOND_YEARS * 12);
        WorksValue.Value borrowed = WorksValue.of(g, care, Map.of(), clinic, 1,
                g.getLandManager().getAvailableSqFt());
        check("fixture: the funded service has a positive growth trend, paid posts and positive borrowing terms",
                BuildAdvice.lifeTrend(g, BuildAdvice.LIFE_MONTHS) > 1 && rate > 0 && borrowed.payroll() > 0);
        check("the comparison includes the future service additions and their capital",
                borrowed.futureUnits() > 0 && borrowed.futureCapital() > 0);
        check("the comparison includes the new debt's issue and coupon costs", borrowed.financing() > 0);
        g.setCashForTest(1e14);
        double cash = g.getCash();
        WorksValue.Value funded = WorksValue.of(g, care, Map.of(), clinic, 1,
                g.getLandManager().getAvailableSqFt());
        check("cash that covers the complete sequence has no new financing charge", funded.financing() == 0);
        check("valuation places no order and moves no cash or debt",
                g.getCash() == cash && AutoBuilder.debt(g) == debt && g.getAutoBuilder().getOrders() == 0);
    }

    static void completeRunning(Path root) {
        out.println("--- complete fleets charge their actual traffic wear and shared fares ---");
        Game g = town(root, "complete-running");
        BuildingsTemplate rail = g.getBuildingManager().getTemplateByName("Light Rail Line");
        double fare = g.getEconomyManager().getTaxPolicy().monthlyFare();
        double base = BuildAdvice.roads(g, Map.of()).getTransitRiders();
        double one = BuildAdvice.roads(g, Map.of(rail, 1L)).getTransitRiders() - base;
        double two = BuildAdvice.roads(g, Map.of(rail, 2L)).getTransitRiders() - base;
        check("fixture: two lines share the same riders and the fare is positive",
                fare > 0 && one > 0 && two < 2 * one);
        double gross = BuildAdvice.running(g, rail, Map.of()) + one * fare;
        double actual = BuildAdvice.runningChange(g, Map.of(), Map.of(rail, 2L), BuildAdvice.NOW);
        double expected = 2 * gross - two * fare + existingWear(g, Map.of(rail, 2L));
        check("two lines collect the full network's fares once",
                Math.abs(actual - expected) < 1e-9 * Math.max(1, Math.abs(expected))
                        && actual - existingWear(g, Map.of(rail, 2L))
                                > 2 * BuildAdvice.running(g, rail, Map.of()));
        BuildingsTemplate gravel = g.getBuildingManager().getTemplateByName("Gravel Road");
        double single = BuildAdvice.running(g, gravel, Map.of());
        double complete = BuildAdvice.runningChange(g, Map.of(), Map.of(gravel, 2L), BuildAdvice.NOW);
        check("the whole two-road order pays wear at its own relieved traffic",
                complete < 2 * single);
        BuildingsTemplate paved = g.getBuildingManager().getTemplateByName("Paved Road");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        quietly(() -> g.buildStack(gravel, 4, true));
        g.getServicesManager().updateInfrastructure();
        double load = g.getInfrastructureManager().getStreetCapacity()
                * InfrastructureManager.GRAVEL_QUIET_UTILISATION * 3;
        double[] streams = new double[Traffic.values().length];
        streams[Traffic.GOODS.ordinal()] = load;
        g.getInfrastructureManager().setLoad(load);
        g.getInfrastructureManager().setBreakdown(streams);
        double savedWear = existingWear(g, Map.of(paved, 1L));
        check("fixture: existing gravel roads cost less to repair with the new paved road",
                g.getBuildingManager().getQuantity(gravel.getId()) > 0 && savedWear < 0);
        double pavedCost = BuildAdvice.running(g, paved, Map.of());
        double changed = BuildAdvice.runningChange(g, Map.of(), Map.of(paved, 1L), BuildAdvice.NOW);
        check("the new work includes the changed repair bill of the existing gravel stock",
                Math.abs(changed - (pavedCost + savedWear)) < 1e-9 * Math.max(1, Math.abs(changed)));
    }

    static double existingWear(Game g, Map<BuildingsTemplate, Long> order) {
        BuildingsTemplate gravel = g.getBuildingManager().getTemplateByName("Gravel Road");
        double materials = Math.max(0, g.getBuildingManager().getConstructionMaterialPrice());
        double basis = g.getEconomyManager().withBuildersTax(g.getBuildingManager().nonMaterialCost(gravel)
                + gravel.getConstructionMaterials() * materials) / 12;
        double before = BuildAdvice.roads(g, Map.of()).getUtilisation();
        double after = BuildAdvice.roads(g, order).getUtilisation();
        return g.getBuildingManager().getQuantity(gravel.getId()) * basis
                * (gravel.gravelRepairsAt(after) - gravel.gravelRepairsAt(before));
    }

    /** The share of their traffic currentLossPriority()'s streets serve: under full, over power's quarter (FS15). */
    static final double SERVED_STREETS = .5;

    static void currentLossPriority(Path root) {
        out.println("--- the higher monetary service loss gets the scarce cash first ---");
        Game g = town(root, "current-loss-priority");
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        utilities.setWattsConsumption(utilities.getProduction() * 4);
        // FS15: the streets' share caused. The town played 36 months served .274 of its street traffic against power's
        // .25 by this fixture's hand, a hair the right side of it; on HS1's path it fell under. Its traffic is now
        // scaled, every stream alike, until the streets serve SERVED_STREETS of it.
        InfrastructureManager roads = g.getInfrastructureManager();
        double[] played = BuildAdvice.supplyDemand(g, streets, Map.of());
        double scale = played[0] / SERVED_STREETS / played[1];
        double[] streams = new double[Traffic.values().length];
        for (Traffic t : Traffic.values()) streams[t.ordinal()] = roads.getLoad(t) * scale;
        roads.setLoad(roads.getLoad() * scale);
        roads.setBreakdown(streams);
        double roadLoss = WorksValue.shortageLoss(g, streets), powerLoss = WorksValue.shortageLoss(g, power);
        double[] road = BuildAdvice.supplyDemand(g, streets, Map.of());
        double[] watts = BuildAdvice.supplyDemand(g, power, Map.of());
        BuildingsTemplate wind = g.getBuildingManager().getTemplateByName("Wind Farm");
        g.setCashForTest(AutoBuilder.reserve(g) + g.quoteBuild(wind, 1).total * 1.01);
        out.printf("      streets serve %.3f and lose %.3f; power serves %.3f and loses %.3f%n",
                road[0] / road[1], roadLoss, watts[0] / watts[1], powerLoss);
        check("fixture: streets lose more output although power has the greater missing share",
                roadLoss > powerLoss && powerLoss > 0 && road[0] / road[1] > watts[0] / watts[1]
                        && !g.getAutoBuilder().within(g));
        var remit = AutoBuilder.remit(g, CityNeeds.measure(g, CityNeeds.PLAIN));
        check("the monetary loss puts streets before power in the actual remit",
                remit.indexOf(streets) >= 0 && remit.indexOf(streets) < remit.indexOf(power));
        g.getAutoBuilder().setOn(true, null);
        quietly(() -> g.getAutoBuilder().pass(g));
        AutoBuilder.Step roadOrder = step(g, streets), powerOrder = step(g, power);
        check("the higher-loss street work uses the cash first and power waits for funding",
                roadOrder != null && roadOrder.ordered() > 0 && powerOrder != null
                        && powerOrder.ordered() == 0 && powerOrder.cut() == AutoBuilder.Cut.CASH);
    }

    /** Every band of the town's people, and its two adult stages, times `f`, its workforce held (FS15): a demand moved
     *  without moving the staffing of the works that serve it. */
    static void scalePeople(Game g, double f) {
        PopulationCohorts cohorts = g.getCohorts();
        double[] saved = cohorts.toSaveArray();
        int bands = AgeBand.values().length;
        double people = 0;
        for (int i = 0; i < bands; i++) people += saved[i] *= f;
        for (int i = bands + 7; i < saved.length; i++) saved[i] *= f;
        cohorts.restore(PopulationCohorts.saveBands(), saved);
        PopulationManager pm = g.getPopulationManager();
        long workforce = pm.getWorkforce();
        pm.setPopulation(Math.round(people));
        pm.restoreWorkforce(workforce);
    }

    static void fundingPriority(Path root, boolean spareCare) throws Exception {
        out.println(spareCare ? "--- an unfunded school keeps its funding before spare care ---"
                : "--- current service loss ranks simultaneous essential needs ---");
        Game g = town(root, spareCare ? "protected-school" : "ranked-school");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        long people = g.getPopulationManager().getPopulation();
        g.getBusinessInvestment().restorePopulationHistory(java.util.List.of(people, people));
        BuildingManager bm = g.getBuildingManager();
        BuildAdvice.Measure school = BuildAdvice.Measure.school(EducationType.ELEMENTARY);
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        for (BuildingsTemplate t : bm.getTemplates()) {
            long n = bm.getQuantity(t.getId());
            if (n > 0 && (school.serves(t) || care.serves(t))) quietly(() -> bm.retire(t, n));
        }
        BuildingsTemplate clinic = bm.getTemplateByName("Walk-in Clinic");
        if (spareCare) {
            double[] base = BuildAdvice.supplyDemand(g, care, Map.of());
            double unit = clinic.getCapacity() * BuildAdvice.staffing(clinic,
                    g.getPopulationManager().getJobFillRate());
            int n = Math.max(0, (int) Math.ceil((base[1] - base[0]) / unit));
            if (n > 0) quietly(() -> g.buildStack(clinic, n, true));
            // FS15: spare care caused. Whole clinics overshoot the lack by up to one clinic, which on BO1-S's path put
            // supply past demand and its slack (5,471 places for a demand of 4,052, its slack's 4,660), and the card was gone. The
            // town's people are scaled, its workforce and so the clinics' staffing held, until care's demand is its supply
            // over half the slack: served now, and short of the reserve the card is for.
            double[] built = BuildAdvice.supplyDemand(g, care, Map.of());
            scalePeople(g, built[0] / (built[1] * (1 + g.getAutoBuilder().getSlack() / 2)));
            long scaled = g.getPopulationManager().getPopulation();
            g.getBusinessInvestment().restorePopulationHistory(java.util.List.of(scaled, scaled));
            double[] spare = BuildAdvice.supplyDemand(g, care, Map.of());
            check(String.format("fixture: care's places are inside its reserve: %.0f for a demand of %.0f, under %.0f",
                    spare[0], spare[1], spare[1] * (1 + g.getAutoBuilder().getSlack())),
                    spare[0] >= spare[1] && spare[0] < spare[1] * (1 + g.getAutoBuilder().getSlack()));
        } else {
            double share = BuildAdvice.cover(g, care, Map.of());
            g.getHealth().advanceMonth(share, 1, 0, 1, 1, 1, 1);
            g.getEconomyManager().setHealthRatio(g.getHealth().getWorkRatio());
        }
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        utilities.setWattsConsumption(utilities.getProduction() * .5);
        serveGrid(g);
        g.getInfrastructureManager().setLoad(0);
        g.getInfrastructureManager().setBreakdown(new double[Traffic.values().length]);
        BuildingsTemplate elementary = bm.getTemplateByName("Elementary School");
        double schoolCost = g.quoteBuild(elementary, 1).total;
        BuildAdvice.Suggestion careCard = BuildAdvice.suggestFor(g, null, care, g.getCash(),
                g.getLandManager().getAvailableSqFt());
        double careCost = careCard == null ? Double.POSITIVE_INFINITY : careCard.price();
        double available = spareCare ? (schoolCost + careCost) / 2 : schoolCost + careCost * .25;
        g.setCashForTest(AutoBuilder.reserve(g) + available);
        AutoBuilder ab = g.getAutoBuilder();
        ab.setDebtLimit(0, null);
        ab.setCashAnyway(true, null);
        check("fixture: the school is useful and entirely unserved, and the care plan is cheaper",
                BuildAdvice.warranted(g, school) && BuildAdvice.supplyDemand(g, school, Map.of())[0] == 0
                        && careCard != null && careCost > 0 && careCost < schoolCost);
        check("fixture: current care is on the intended side of full service",
                BuildAdvice.maintained(g, care, Map.of(), BuildAdvice.NOW) == spareCare
                        && (spareCare || WorksValue.shortageLoss(g, care) == 0));
        check("fixture: the available cash funds care, with the intended school funding gap",
                ab.canPay(g, careCost) && ab.canPay(g, schoolCost) != spareCare);
        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        AutoBuilder.Step order = step(g, school);
        if (spareCare) {
            AutoBuilder.Step heldCare = step(g, care);
            check("the school remains a hard cash or debt hold and the spare-care order preserves its funds",
                    order != null && order.outcome() == AutoBuilder.Outcome.HELD
                            && heldCare != null && heldCare.ordered() == 0
                            && heldCare.outcome() == AutoBuilder.Outcome.HELD && heldCare.building() == null);
            check("the same care card labels the automatic funding hold", ab.heldBackWords(careCard) != null);
            String words = ab.heldBackWords(careCard);
            boolean[] saved = {false};
            quietly(() -> saved[0] = g.saveGame(10, "HW1 funding reason reload").ok);
            Game loaded = new Game(g.getGameFiles());
            quietly(() -> loaded.loadGameSave(10));
            BuildAdvice.Suggestion loadedCard = BuildAdvice.suggestFor(loaded, null, care, loaded.getCash(),
                    loaded.getLandManager().getAvailableSqFt());
            check("a real scratch save and load keeps the same funding reason on the care card",
                    saved[0] && loaded.getMonth() == g.getMonth() && loaded.getAutoBuilder().isOn()
                            && loadedCard != null && words != null
                            && words.equals(loaded.getAutoBuilder().heldBackWords(loadedCard)));
            loaded.getAutoBuilder().setOn(false, null);
            ab.setOn(false, null);
            check("switching automatic building off clears the funding hold on both cards",
                    ab.heldBackWords(careCard) == null && loaded.getAutoBuilder().heldBackWords(loadedCard) == null);
            ab.setOn(true, null);
            loaded.getAutoBuilder().setOn(true, null);
            check("switching it back on before a new pass restores no stale funding hold",
                    ab.heldBackWords(careCard) == null && loaded.getAutoBuilder().heldBackWords(loadedCard) == null);


        } else {
            check("the fully unserved school is ordered before the partly served capped-care work",
                    order != null && order.ordered() > 0);
        }
    }

    static void filledPostCosts(Path root) {
        out.println("--- staff-short works pay only the posts filled in the utility books ---");
        Game g = town(root, "filled-post-costs");
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        PopulationManager pm = g.getPopulationManager();
        // FS15: the short staff caused: the workforce set to half the town's posts, where it was the 36-month play's.
        pm.restoreWorkforce(pm.getTotalJobs() / 2);
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
        double rate = Math.max(0, BuildAdvice.lifeRate(g)) / 12;
        double months = Math.max(0, BuildAdvice.LIFE_MONTHS - v.lead());
        double factor = rate > 0 ? (1 - Math.pow(1 + rate, -months)) / rate : months;
        double expected = (nonPayroll + coal.getUpkeep() + paid) * factor * delay;
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
            long n = bm.getQuantity(t.getId());
            if (n > 0) quietly(() -> bm.retire(t, n));
        }
        BuildingsTemplate clinic = bm.getTemplateByName("Community Health Centre");
        double demand = BuildAdvice.supplyDemand(g, care, Map.of())[1];
        long count = 1 + (int) Math.ceil((demand + g.getHealthcare().getPricedOut(CareType.GENERAL))
                / BuildAdvice.unit(g, care, clinic));
        Map<BuildingsTemplate, Long> order = Map.of(clinic, count);
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
        serveStreets(g);
        AutoBuilder ab = g.getAutoBuilder();
        ab.setSlack(.35, null);
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 2);
        serveGrid(g);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        check("overview cards require the NEEDS YOU row their screen reads",
                BuildAdvice.suggest(g, java.util.List.of()).isEmpty());
        var all = CityNeeds.measure(g, CityNeeds.PLAIN);
        BuildAdvice.Suggestion card = BuildAdvice.suggest(g, all).stream()
                .filter(s -> s.measure().equals(power)).findFirst().orElse(null);
        check("fixture: the power card leads the automatic remit and its full order is fundable",
                card != null && AutoBuilder.remit(g, all).get(0).equals(power)
                        && g.quoteBuild(card.template(), card.count()).total < AutoBuilder.spendable(g)
                        && card.landSqFt() < g.getLandManager().getAvailableSqFt());
        if (card == null) return;
        check("the power plan is sized to its whole order's actual opening and planning horizon",
                Math.abs(card.ahead().months() - g.quoteBuild(card.template(), card.count()).months
                        - BusinessInvestment.horizonFor(g.quoteBuild(card.template(), card.count()).months)) < 1e-9);
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

    static void interimPower(Path root) {
        out.println("--- pending coal does not hide an earlier forecast power loss ---");
        Game g = town(root, "interim-power");
        g.setCashForTest(1e14);
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("Construction Depot"), 8, true));
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        BuildingsTemplate wind = g.getBuildingManager().getTemplateByName("Wind Farm");
        // THE FIXTURE CAUSES IT (PW1): the advice credits a Wind Farm with its firm part only (PowerDispatch.WIND_FIRM, the calm peak), so
        // wind built for the peak needs three times the farms, and over the twenty years coal is the cheaper plan even opening later
        // (coal $193M against wind $285M on this town, the prevented loss $1.5M against $1.8M). The section is the timing, not that price:
        // the fixture's farm is built 1 / WIND_FIRM larger, firm at the nameplate the section was written against.
        wind.setProduction1(wind.getProduction1() / PowerDispatch.WIND_FIRM);
        // THE FIXTURE CAUSES IT (PW2): the section is the wind's timing against pending coal; the Oil-Fired Plant opens between
        // them (its 48 months to the wind's 36 and the coal's 60), and the pass ordered it. Dated past this town's year it is no
        // candidate; the founding's own oil-fired plant stands and runs as before. And PW2's refigure (spec-electricity 2.3) made
        // the coal plant cheaper a unit of power than the section was written against (776,914 of cash for 280,086, was 1,071,816
        // for 280,000) and the wind farm dearer (25,023 for 6,854, was 23,240 for 8,100): firm at its nameplate the farm's plan was
        // still 3% dearer than a second coal plant's here. The section is the timing, not that price, so the fixture's farm is
        // also priced at half.
        g.getBuildingManager().getTemplateByName("Oil-Fired Plant").setSince(2100);
        wind.setCashCost(wind.getCashCost() / 2);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        AutoBuilder ab = g.getAutoBuilder();
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        check("fixture: unrelated essential services already have their operating capacity",
                serveOtherEssentials(g, power));
        quietly(() -> g.buildStack(coal, 1, false));
        var people = g.getHistorySave().getPopulation();
        var months = g.getHistorySave().getMonth();
        people.clear(); months.clear();
        g.getBusinessInvestment().restorePopulationHistory(java.util.List.of());
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        utilities.setWattsConsumption(utilities.getProduction() * 1.1);
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, power);
        double first = Math.ceil(g.onSiteMonths(coal));
        double windLead = g.quoteBuild(wind, 1).months;
        check("fixture: constant physical power is short and pending coal covers the entire horizon, opening after wind",
                !BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW)
                        && BuildAdvice.maintained(g, power, site, BuildAdvice.NOW)
                        && Double.isFinite(first) && first < BuildAdvice.LIFE_MONTHS && windLead < first);
        WorksValue.Value value = WorksValue.of(g, power, site, wind, 1, g.getLandManager().getAvailableSqFt());
        check("wind prevents positive physical output loss before the pending coal opens", value.used() > 0 && value.avoided() > 0);
        long pop = g.getPopulationManager().getPopulation();
        for (int i = 0; i <= 12; i++) {
            people.add(Math.round(pop * (.8 + .2 * i / 12)));
            months.add(g.getMonth() - 12 + i);
        }
        g.getBusinessInvestment().restorePopulationHistory(people);
        // ...the draw a fifth inside the supply as the advice reads it (PW1: the calm peak over the firm supply).
        powerDrawAt(g, .8);
        serveGrid(g);
        check("fixture: power is covered now, its forecast is short before coal opens, and wind can open sooner",
                BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW)
                        && !BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.opening(g, windLead, ab.getSlack()))
                        && BuildAdvice.maintained(g, power, site, BuildAdvice.NOW)
                        && Double.isFinite(first) && first < BuildAdvice.LIFE_MONTHS && windLead < first);

        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        AutoBuilder.Step order = step(g, power);
        check("the automatic path orders earlier wind before the forecast shortage becomes physical",
                order != null && order.ordered() > 0 && order.building().equals(wind.getName()));
        BuildingsStacks pendingWind = g.getBuildingManager().getStack(wind);
        if (pendingWind == null || pendingWind.getUnderConstruction() <= 0) return;
        double windFirst = Math.ceil(g.getBuildingManager().siteUnitMonths(wind, 1, g.getBuildingOutputAtEveryPost()));
        var state = new com.google.gson.Gson().toJsonTree(ab.toState()).getAsJsonObject();
        var dates = state.has("pendingUntil") ? state.getAsJsonObject("pendingUntil") : null;
        double due = dates != null && dates.has(power.label()) ? dates.get(power.label()).getAsDouble() : Double.POSITIVE_INFINITY;
        check("the faster wind brings the saved first-completion deadline forward from the coal quote",
                due <= g.getMonth() + windFirst && due < g.getMonth() + first);
    }

    static void waterAtCap(Path root) {
        out.println("--- pending fresh water at its cap cannot be called a closing plan ---");
        Game g = town(root, "water-at-cap");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate fresh = bm.getTemplateByName("Water Treatment Plant");
        BuildingsTemplate desal = bm.getTemplateByName("Desalination Plant");
        BuildAdvice.Measure water = BuildAdvice.Measure.of(BuildAdvice.Kind.WATER);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        quietly(() -> g.buildStack(bm.getTemplateByName("Wind Farm"), 20, true));
        g.getServicesManager().updateJobFillRate(g.getPopulationManager().getJobFillRate());
        g.getServicesManager().updateServiceWages(g.getPopulationManager().getWagesPerType());
        g.getServicesManager().updateServices();
        u.setFreshCap(u.getFreshNameplate() * u.getAverageUtilityFill() * 1.25);
        quietly(() -> g.buildStack(fresh, 2, false));
        u.setBuildingWaterDraw(u.getFreshCap() * 2 + u.getBaseWaterProduction());
        u.updateWaterRatio();
        var site = BuildAdvice.onSite(g, water);
        double[] before = BuildAdvice.supplyDemand(g, water, site);
        double[] after = BuildAdvice.supplyDemand(g, water, BuildAdvice.plus(site, fresh, 1));
        check("fixture: pending fresh plants exhaust the cap, water remains short, and one more cannot reduce its shortage",
                before[0] < before[1] && after[0] - after[1] <= before[0] - before[1]
                        && BuildAdvice.unit(g, water, fresh) > 0);
        var skip = new java.util.HashSet<>(bm.getTemplates());
        skip.remove(fresh);
        BuildAdvice.Suggestion useless = BuildAdvice.suggestFor(g, null, water, g.getCash(),
                g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack(), skip, true);
        check("the fresh-only path offers no zero-help order", useless == null);
        BuildAdvice.Suggestion useful = BuildAdvice.suggestFor(g, null, water, g.getCash(),
                g.getLandManager().getAvailableSqFt());
        check("the normal water card chooses desalination and its count closes the demand",
                useful != null && useful.template() == desal && useful.closes()
                        && BuildAdvice.maintained(g, water, BuildAdvice.plus(site, desal, useful.count()), useful.ahead()));
    }

    static void queuedPower(Path root) {
        out.println("--- priority work waits in front of the first pending plant ---");
        Game g = town(root, "queued-power");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate coal = bm.getTemplateByName("Coal Power Plant");
        BuildingsTemplate wind = bm.getTemplateByName("Wind Farm");
        BuildingsTemplate road = bm.getTemplateByName("Paved Road");
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        quietly(() -> g.buildStack(bm.getTemplateByName("Construction Depot"), 8, true));
        int roads = (int) Math.ceil(g.getBuildingOutputAtEveryPost() * (BuildAdvice.LIFE_MONTHS + 100)
                / road.getConstructionPoints());
        quietly(() -> {
            g.buildStack(wind, 1, false);
            g.buildStack(road, roads, false);
            g.buildStack(coal, 20, false);
        });
        bm.getControl().setOrder(java.util.List.of(ConstructionControl.keyOf(wind),
                ConstructionControl.keyOf(road), ConstructionControl.keyOf(coal)));
        g.getBusinessInvestment().restorePopulationHistory(java.util.List.of());
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 2);
        BuildingsStacks roadSite = bm.getStack(road);
        double ahead = (roadSite.getUnderConstruction() * (double) road.getConstructionPoints()
                - roadSite.getConstructionProgress()) / g.getBuildingOutputAtEveryPost();
        double lead = g.quoteBuild(wind, 1).months;
        check("fixture: even the whole yard needs more than the valued horizon to finish the priority roads before coal",
                Double.isFinite(ahead) && lead > 0 && lead + BuildAdvice.LIFE_MONTHS < ahead);
        double first = BuildAdvice.firstCompletion(g, power, Map.of(coal, 20L));
        check("the first coal quote retains the entire earlier priority delay", first >= ahead);
        double actual = WorksValue.avoided(g, power, BuildAdvice.onSite(g, power), Map.of(wind, 1L), lead);
        double withoutCoal = WorksValue.avoided(g, power, Map.of(wind, 1L), Map.of(wind, 1L), lead);
        check("coal that cannot open inside the horizon erases none of the earlier wind's physical benefit",
                withoutCoal > 0 && Math.abs(actual - withoutCoal) < 1e-9 * Math.max(1, withoutCoal));
    }

    static void shortPowerBenefit(Path root) {
        out.println("--- power delivered for fewer than six months still prevents lost work ---");
        Game g = town(root, "short-power-benefit");
        g.setCashForTest(1e14);
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("Construction Depot"), 8, true));
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate coal = bm.getTemplateByName("Coal Power Plant");
        BuildingsTemplate wind = bm.getTemplateByName("Wind Farm");
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        g.setCashForTest(1e14);
        quietly(() -> g.buildStack(coal, 1, false));
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        g.getBusinessInvestment().restorePopulationHistory(java.util.List.of());
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 1.1);
        BuildingsStacks site = bm.getStack(coal);
        double lo = 0, hi = coal.getConstructionPoints();
        for (int i = 0; i < 60; i++) {
            double mid = (lo + hi) / 2;
            site.setConstructionProgress(mid);
            double gap = g.onSiteMonths(coal) - g.quoteBuild(wind, 1).months;
            if (gap > 3) lo = mid; else hi = mid;
        }
        site.setConstructionProgress((lo + hi) / 2);
        double lead = g.quoteBuild(wind, 1).months;
        double gap = g.onSiteMonths(coal) - lead;
        check("fixture: constant demand is short, coal will cover it, and wind opens between one and six months earlier",
                gap > 1 && gap < 6 && lead > 0 && Double.isFinite(lead)
                        && lead + gap < BuildAdvice.LIFE_MONTHS
                        && !BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW)
                        && BuildAdvice.maintained(g, power, Map.of(coal, 1L), BuildAdvice.NOW));
        WorksValue.Value value = WorksValue.of(g, power, Map.of(coal, 1L), wind, 1,
                g.getLandManager().getAvailableSqFt());
        check("the sub-six-month service window has positive relief and avoided output loss",
                value.used() > 0 && value.avoided() > 0);
    }

    static void pavingLead(Path root) {
        out.println("--- paving is sized to the final order's own opening ---");
        Game g = town(root, "paving-lead");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate gravel = bm.getTemplateByName("Gravel Road");
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        quietly(() -> {
            g.buildStack(gravel, 20, true);
            g.buildStack(bm.getTemplateByName("House"), 1000, true);
            g.buildStack(bm.getTemplateByName("Construction Depot"), 30, true);
        });
        g.getServicesManager().updateInfrastructure();
        InfrastructureManager roads = g.getInfrastructureManager();
        double load = roads.getStreetCapacity() * .9;
        roads.setLoad(load);
        double[] traffic = new double[Traffic.values().length];
        traffic[Traffic.GOODS.ordinal()] = load;
        roads.setBreakdown(traffic);
        var history = new java.util.ArrayList<Long>();
        long pop = g.getPopulationManager().getPopulation();
        for (int i = 0; i < 12; i++) history.add(Math.round(pop * (.7 + .3 * i / 11)));
        g.getBusinessInvestment().restorePopulationHistory(history);
        var site = BuildAdvice.onSite(g, streets);
        var step = BuildAdvice.pavingStep(g);
        double slack = g.getAutoBuilder().getSlack();
        int today = 0;
        while (today < g.paveable() && !BuildAdvice.maintained(g, streets,
                BuildAdvice.times(site, step, today), new BuildAdvice.Ahead(0, 1, slack))) today++;
        var skip = new java.util.HashSet<>(bm.getTemplates());
        BuildAdvice.Suggestion plan = BuildAdvice.suggestFor(g, null, streets, g.getCash(), 0, slack, skip, false);
        check("fixture: paving is worth offering and the growing city needs more than its unprojected paving count",
                today > 0 && plan != null && plan.count() > today);
        if (plan == null) return;
        double finalLead = g.quotePave(plan.count()).months;
        check("the paving quote and projection both use the final count's complete opening",
                Math.abs(plan.lead() - finalLead) < 1e-9
                        && Math.abs(plan.ahead().months() - BuildAdvice.opening(g, finalLead, slack).months()) < 1e-9);
    }

    static void overduePending(Path root) throws Exception {
        out.println("--- pending capacity keeps its deadline through a real mid-construction save and load ---");
        Game g = town(root, "overdue-pending");
        AutoBuilder ab = g.getAutoBuilder();
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate wind = bm.getTemplateByName("Wind Farm");
        BuildingsTemplate later = bm.getTemplateByName("Container Terminal");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2 * 1_000);
        quietly(() -> g.buildStack(bm.getTemplateByName("Construction Depot"), 8, true));
        // ...a hundred Wind Farms' firm kW pending (PW1: a farm counts PowerDispatch.WIND_FIRM of its average at the calm peak).
        quietly(() -> g.buildStack(wind, (long) Math.ceil(100 / PowerDispatch.WIND_FIRM), false));
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        UtilitiesHandler utilities = g.getServicesManager().getUtilitiesHandler();
        utilities.setWattsConsumption(utilities.getProduction() * 1.2);
        double first = Math.ceil(bm.siteUnitMonths(wind, 1, g.getBuildingOutputAtEveryPost()));
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
        // FC2, 2026-11-23: the work that stands before the wind was ten thousand Paved Roads. On FC2's path the foreign crews took them
        // (505 roads finished in the first month), and the repairs of the roads that stood outran the builders: 347 points of repairs
        // at the order, 3,707 a month on, 12,893 at the pass past the deadline, against a site output of 726 a month that was 0 by the
        // next month, so that pass held the Coal Power Plant "the builders have no site output to build them" and ordered no power. It is one
        // Container Terminal now, the dearest single building (829,800 points): one unit, which neither the home builders nor the
        // crews abroad finish in the months compared, so nothing finishes to be repaired and the builders keep their site output.
        quietly(() -> g.buildStack(later, 1, false));
        bm.getControl().setOrder(java.util.List.of(ConstructionControl.keyOf(later), ConstructionControl.keyOf(wind)));
        int reloadAt = began + Math.max(1, (int) first / 2);
        quietly(() -> g.simulateMonths(reloadAt - g.getMonth()));
        check("the unbroken city reaches the mid-construction save month", g.getMonth() == reloadAt);
        if (g.getMonth() != reloadAt) return;
        boolean[] saved = {false};
        quietly(() -> saved[0] = g.saveGame(10, "HW1 pending deadline twin").ok);
        GameFiles copied = new GameFiles(root.resolve("overdue-loaded"), root.resolve("overdue-loaded-no-legacy"));
        try (var paths = java.nio.file.Files.walk(g.getGameFiles().getDirectory())) {
            for (Path from : paths.toList()) {
                Path to = copied.getDirectory().resolve(g.getGameFiles().getDirectory().relativize(from));
                if (java.nio.file.Files.isDirectory(from)) java.nio.file.Files.createDirectories(to);
                else java.nio.file.Files.copy(from, to, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Double savedDeadline = ab.toState().pendingUntil.get(power.label());
        var originalOrders = new java.util.ArrayList<String>();
        var loadedOrders = new java.util.ArrayList<String>();
        boolean[] early = {false}, late = {false}, stayedShort = {true};
        installPendingProbe(g, power, began, first, originalOrders, early, late, stayedShort);
        boolean advanced = true;
        int remaining = began + (int) first + 2 - g.getMonth();
        for (int i = 0; i < remaining; i++) {
            int month = g.getMonth();
            quietly(() -> g.simulateMonths(1));
            if (g.getMonth() != month + 1) { advanced = false; break; }
        }
        // Replay the frozen checkpoint in its own files after the unbroken run.
        // AutoBuildCheck.loaded() pins an external rate for its own fixture.
        // Both cities here keep the ordinary floating-rate path.
        Game loaded = new Game(copied);
        if (g.isOnLiterals()) loaded.playOnLiterals();
        quietly(() -> loaded.loadGameSave(10));
        // ChildcareCheck.town's sector holds are deliberately unsaved fixture controls.
        for (Sector sector : g.getSectors().all())
            if (g.getBusinessInvestment().isHeld(sector.key()))
                loaded.getBusinessInvestment().holdSector(sector.key());
        check("the mid-construction scratch save loaded through DataSave with its original pending date",
                saved[0] && loaded.getLoadFailure() == null && loaded.getMonth() == reloadAt
                        && savedDeadline != null
                        && savedDeadline.equals(loaded.getAutoBuilder().toState().pendingUntil.get(power.label())));
        installPendingProbe(loaded, power, began, first, loadedOrders, new boolean[1], new boolean[1], new boolean[] {true});
        for (int i = 0; i < remaining; i++) {
            int month = loaded.getMonth();
            quietly(() -> loaded.simulateMonths(1));
            if (loaded.getMonth() != month + 1) { advanced = false; break; }
        }
        check("both scratch timelines advance every compared month", advanced && loaded.getMonth() == g.getMonth());
        g.autoBuildProbeForTest = null;
        loaded.autoBuildProbeForTest = null;
        check("fixture: later priority work kept operating power short throughout the original deadline", stayedShort[0]);
        check("fixture: the later priority work finished nothing, so the repairs left the builders a site output at the last pass",
                bm.getStack(later).getQuantity() == 0 && bm.getStack(later).getUnderConstruction() == 1
                        && g.getBuildingOutputAtEveryPost() > 0);
        check("monthly passes respect the original pending deadline before it expires", !early[0]);
        check("past that deadline the automatic path orders more power despite the pending wind", late[0]);
        check("the real loaded city and its unbroken twin place the same later automatic orders, one by one",
                !originalOrders.isEmpty() && originalOrders.equals(loadedOrders));
    }

    static void installPendingProbe(Game g, BuildAdvice.Measure power, int began, double first,
            java.util.List<String> orders, boolean[] early, boolean[] late, boolean[] stayedShort) {
        g.autoBuildProbeForTest = after -> {
            if (!after) {
                UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
                u.setWattsConsumption(u.getProduction() * 1.2);
                long people = g.getPopulationManager().getPopulation();
                g.getBusinessInvestment().restorePopulationHistory(java.util.List.of(people, people));
                return;
            }
            stayedShort[0] &= !BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW);
            for (AutoBuilder.Step order : g.getAutoBuilder().steps()) if (order.ordered() > 0) {
                orders.add(g.getMonth() + "|" + order.measure().label() + "|" + order.building() + "|" + order.wanted()
                        + "|" + order.ordered() + "|" + order.cut());
                if (!order.measure().equals(power)) continue;
                if (g.getMonth() <= began + first) early[0] = true;
                else late[0] = true;
            }
        };
    }

    public static void main(String[] args) throws Exception {
        Path root = TempDirs.create("essential-works");
        highways(root, true);
        highways(root, false);
        anticipateHighway(root);
        schoolsAndPolice(root);
        lostOutput(root);
        scarceCash(root);
        professionalDemand(root);
        pendingIndustrialLoad(root);
        interimCare(root);
        stagedCosts(root);
        completeRunning(root);
        currentLossPriority(root);
        fundingPriority(root, false);
        fundingPriority(root, true);
        filledPostCosts(root);
        cappedCare(root);
        adviceParity(root);
        overduePending(root);
        interimPower(root);
        waterAtCap(root);
        queuedPower(root);
        shortPowerBenefit(root);
        pavingLead(root);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
