package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Independent discounted arithmetic for BuildAdviceCheck's complete-plan law.
 * WHY. Reading the value object back through its own factory could never find
 * an omitted cost. This fixture helper reads quotes, stocks and physical model
 * inputs, and computes the cash flows without calling WorksValue.
 */
final class AdviceValueOracle {
    private AdviceValueOracle() { }

    static double[] value(Game g, BuildAdvice.Suggestion s, double land, double treasury) {
        BuildingsTemplate t = s.template();
        BuildAdvice.Measure measure = s.measure();
        boolean power = measure.kind() == BuildAdvice.Kind.POWER;
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, measure);
        Map<BuildingsTemplate, Long> order = BuildAdvice.added(g, s);
        Game.BuildQuote quote = s.paving() ? g.quotePave(s.count()) : g.quoteBuild(t, s.count());
        double opening = Double.isFinite(quote.months) ? Math.max(0, quote.months) : 0;
        double monthly = Math.max(0, BuildAdvice.lifeRate(g)) / 12;
        double nominal = (1 + monthly)
                * (1 + Math.max(-.99, g.getBusinessInvestment().getExpectedInflation()) / 12) - 1;
        double footprint = s.landSqFt();
        // ...and a Wind or Solar Farm's farmland reserve while one stands (RG1).
        String reserve = s.paving() ? null : g.getLandManager().reserveKeyOf(t);
        double usableLand = Math.max(0, land + (g.getLandManager().freeForAnyone(Game.groundUse(t))
                - g.getLandManager().getAvailableSqFt()) + (reserve == null ? 0 : g.getLandManager().heldBy(reserve)));
        double initialLand = s.paving() ? -BuildAdvice.landValue(g, -footprint, usableLand)
                : BuildAdvice.landValue(g, t, footprint, usableLand);
        Ground ground = new Ground(g, t, land);
        double invoice = quote.total + ground.use(s.paving() ? footprint : g.landNeededFor(t, s.count()));
        double available = g.getAutoBuilder().spendableForMeasure(g, measure) - (g.getCash() - treasury);
        double[] initialFinance = credit(g, Math.max(0, invoice - available), nominal,
                BuildAdvice.LIFE_MONTHS);
        available = Math.max(0, available + initialFinance[1] - invoice);
        double perUnitReturned = 0;
        double[] wages = g.getPopulationManager().getWagesPerType(), fill = g.getPopulationManager().getJobFillRate();
        if (!s.paving()) for (JobType job : JobType.values())
            perUnitReturned += s.count() * (double) t.getJobs(job) * wages[job.ordinal()] * fill[job.ordinal()]
                    * g.getEconomyManager().getTaxPolicy().payslipShare(WageBand.of(job));
        double openMonths = Math.max(0, BuildAdvice.LIFE_MONTHS - opening);
        double openFactor = Math.abs(monthly) > 1e-12 ? (1 - Math.pow(1 + monthly, -openMonths)) / monthly : openMonths;
        double returned = perUnitReturned * openFactor * Math.pow(1 + monthly, -opening);
        perUnitReturned /= s.count();
        double capitalLater = 0, financing = initialFinance[0], operating = 0, saved = 0, units = 0, weights = 0, unitMonths = 0;
        long additions = 0;
        double nextLead = g.quoteBuild(t, 1).months;
        if (!Double.isFinite(nextLead)) nextLead = 0;
        double powerPotential = 0;
        for (Sector sector : g.getSectors().all()) {
            Sector.Statement book = sector.statement();
            powerPotential += sector.perPointOfGrid(Math.max(0, book.revenue - book.inputs - book.electricity - book.water));   // steam needs no grid (R1d)
        }
        Map<BuildingsTemplate, Long> loads = new LinkedHashMap<>();
        if (power) for (BuildingsStacks stack : g.getBuildingManager().getStacksUnderConstruction()) {
            BuildingsTemplate load = stack.getBuilding();
            if (load.getCategory() != BuildingType.ELECTRICITY && load.getElectricityConsumption() > 0)
                loads.put(load, stack.getUnderConstruction());
        }
        int block = power ? 1 : 12;
        for (int start = 0; start < openMonths; start += block) {
            double duration = Math.min(block, openMonths - start);
            double date = opening + start + duration / 2;
            BuildAdvice.Ahead future = new BuildAdvice.Ahead(date, BuildAdvice.lifeTrend(g, date), 0);
            Map<BuildingsTemplate, Long> base = power ? ready(g, site, date) : site;
            Map<BuildingsTemplate, Long> planned = BuildAdvice.plus(BuildAdvice.plus(base, order), Map.of(t, additions));
            BuildAdvice.Ahead target = new BuildAdvice.Ahead(date, future.k(), g.getAutoBuilder().getSlack());
            if (!maintained(g, measure, planned, target, loads)) {
                long limit = t.isHydro() ? Math.max(0, g.hydroSlotsFree(t) - s.count() - additions) : BuildAdvice.MOST;
                long extra = power ? powerCount(g, planned, t, target, limit, loads)
                        : BuildAdvice.count(g, measure, planned, Map.of(t, 1L), target, limit, true)[0];
                if (extra > 0) {
                    double build = g.quoteBuild(t, extra).total;
                    double free = Math.max(0, usableLand - footprint - additions * (double) t.getLandSqFt());
                    double paidDate = Math.max(0, date - nextLead);
                    double discount = Math.pow(1 + monthly, -paidDate);
                    capitalLater += (build + BuildAdvice.landValue(g, t, t.getLandSqFt() * (double) extra, free)) * discount;
                    double needed = build + ground.use(g.landNeededFor(t, extra));
                    double[] loan = credit(g, Math.max(0, needed - available), nominal,
                            Math.min(BuildAdvice.LIFE_MONTHS - paidDate, Game.BUILD_BOND_YEARS * 12));
                    financing += loan[0] * discount;
                    available = Math.max(0, available + loan[1] - needed);
                    additions += extra;
                    planned = BuildAdvice.plus(planned, Map.of(t, extra));
                }
            }
            double flowFactor = Math.abs(monthly) > 1e-12
                    ? (1 - Math.pow(1 + monthly, -duration)) / monthly : duration;
            double weight = flowFactor * Math.pow(1 + monthly, -(opening + start));
            unitMonths += weight * additions;
            operating += weight * running(g, base, planned, future);
            double[] before = power ? powerService(g, base, BuildAdvice.NOW, loads)
                    : BuildAdvice.supplyDemand(g, measure, base, future);
            double[] after = power ? powerService(g, planned, BuildAdvice.NOW, loads)
                    : BuildAdvice.supplyDemand(g, measure, planned, future);
            if (power) {
                double present = g.getServicesManager().getUtilitiesHandler().getConsumption();
                double pendingLoad = 0;
                for (Map.Entry<BuildingsTemplate, Long> load : ready(g, loads, date).entrySet())
                    pendingLoad += load.getValue() * (double) load.getKey().getElectricityConsumption();
                double demand = present * future.scale() + pendingLoad;
                before = new double[] {before[0], demand + (before[1] - present)};
                after = new double[] {after[0], demand + (after[1] - present)};
                saved += weight * powerPotential * Math.max(0, fraction(after) - fraction(before));
            } else saved += weight * restored(g, measure, base, planned, future);
            units += weight * Math.max(0, Math.max(0, before[1] - before[0]) - Math.max(0, after[1] - after[0]));
            weights += weight;
        }
        double payrollReturned = returned + perUnitReturned * unitMonths;
        double cost = quote.total + initialLand + capitalLater + financing + operating;
        return new double[] {cost - Math.max(0, saved) - payrollReturned, weights > 0 ? units / weights : 0};
    }

    private static boolean maintained(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> stock,
                                      BuildAdvice.Ahead at, Map<BuildingsTemplate, Long> loads) {
        if (m.kind() != BuildAdvice.Kind.POWER) return BuildAdvice.maintained(g, m, stock, at);
        double smallest = Double.POSITIVE_INFINITY;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates())
            if (t.getCategory() == BuildingType.ELECTRICITY && t.getProduction1() > 0)
                smallest = Math.min(smallest, t.getProduction1());
        double current = g.getServicesManager().getUtilitiesHandler().getConsumption();
        if (!(current > 0) || Double.isFinite(smallest) && current < BuildAdvice.FIRST_WORK_SHARE * smallest) return true;
        double[] sd = powerService(g, stock, at, loads);
        return !(sd[1] > 0) || sd[0] >= sd[1];
    }

    private static long powerCount(Game g, Map<BuildingsTemplate, Long> stock, BuildingsTemplate t,
                                  BuildAdvice.Ahead at, long limit, Map<BuildingsTemplate, Long> loads) {
        double[] before = powerService(g, stock, at, loads);
        double previous = before[0] - before[1];
        long count = 0;
        while (count < limit) {
            Map<BuildingsTemplate, Long> next = BuildAdvice.plus(stock, Map.of(t, count + 1));
            if (maintained(g, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER), next, at, loads)) return count + 1;
            double[] sd = powerService(g, next, at, loads);
            double margin = sd[0] - sd[1];
            if (!(margin > previous + 1e-12)) break;
            count++;
            previous = margin;
        }
        return count;
    }

    /** Independent firm supply and pending draw: the founding grid has no posts; each plant has its own (REV-PW9). */
    static double[] powerService(Game g, Map<BuildingsTemplate, Long> stock,
                                         BuildAdvice.Ahead at, Map<BuildingsTemplate, Long> loads) {
        UtilitiesHandler utility = g.getServicesManager().getUtilitiesHandler();
        BuildingManager bm = g.getBuildingManager();
        double demand = utility.getConsumption() * at.scale();
        if (at.months() > 0) for (Map.Entry<BuildingsTemplate, Long> e : ready(g, loads, at.months()).entrySet())
            demand += e.getValue() * (double) NewUnit.of(g, e.getKey()).getElectricityConsumption();
        double variable = 0, sun = PowerDispatch.solarYear(utility.latitude(), utility.trackers());
        for (BuildingsStacks stack : bm.getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.isWind() || t.isSolar()) variable += stack.getQuantity() * t.getProduction1() * (t.isSolar() ? sun : 1);
        }
        for (Map.Entry<BuildingsTemplate, Long> e : stock.entrySet()) {
            BuildingsTemplate t = e.getKey();
            if (t.isWind() || t.isSolar()) variable += e.getValue() * NewUnit.of(g, t).getProduction1() * (t.isSolar() ? sun : 1);
        }
        double share = utility.getConsumption() > 0 ? Math.max(0, variable) / utility.getConsumption() : 0;
        double supply = Math.max(0, utility.getWorksPower());
        double[] fill = g.getPopulationManager().getJobFillRate();
        for (BuildingsStacks stack : bm.getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCategory() == BuildingType.ELECTRICITY && stack.getQuantity() > 0)
                supply += firm(utility, t, NewUnit.standing(t), stack.getQuantity(), fill, share);
        }
        for (Map.Entry<BuildingsTemplate, Long> e : stock.entrySet()) {
            BuildingsTemplate t = e.getKey();
            long count = e.getValue();
            NewUnit unit = count > 0 ? NewUnit.of(g, t) : NewUnit.standing(t);
            demand += count * unit.getElectricityConsumption();
            if (t.getCategory() == BuildingType.ELECTRICITY) supply += firm(utility, t, unit, count, fill, share);
        }
        supply += utility.getFoundingGridKw();
        return new double[] {Math.max(0, supply), demand * PowerDispatch.PLAN_PEAK_HOUR};
    }

    private static double firm(UtilitiesHandler utility, BuildingsTemplate t, NewUnit unit, long count, double[] fill, double share) {
        double posts = 0, filled = 0;
        for (JobType job : JobType.values()) {
            posts += unit.getJobs(job);
            filled += unit.getJobs(job) * fill[job.ordinal()];
        }
        double staffed = posts > 0 ? filled / posts : 1;
        if (t.isHydro()) staffed = staffed > 0 ? 1 : 0;
        double credit = t.isWind() ? PowerDispatch.windCredit(share)
                : t.isSolar() ? PowerDispatch.solarCredit(utility.latitude(), utility.trackers(), share) : 1;
        return count * unit.getProduction1() * credit * staffed;
    }

    /** Present cost and cash actually raised, including the quoted granule's excess. */
    private static double[] credit(Game g, double gap, double discount, double months) {
        if (!(gap > 0)) return new double[] {0, 0};
        DebtQuote q = g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, g.fundingLot("Term"),
                g.getAutoBuilder().getCap());
        double coupon, spread, proceeds = gap;
        if (q.cashReceived() > 0) {
            double scale = Math.max(1, gap / q.cashReceived());
            coupon = q.monthlyInterest() * scale;
            spread = (q.faceValue() - q.cashReceived()) * scale;
            proceeds = q.cashReceived() * scale;
            months = Math.min(months, q.auction() == null ? q.duration() * 12 : q.auction().ask().months());
        } else {
            coupon = gap * Math.max(0, g.getDebtManager().quoteRate(0, Game.BUILD_BOND_YEARS * 12)) / 12;
            spread = 0;
        }
        double annuity = Math.abs(discount) > 1e-12 ? (1 - Math.pow(1 + discount, -months)) / discount : months;
        return new double[] {spread + coupon * annuity, proceeds};
    }

    private static final class Ground {
        final Game game;
        final Set<Integer> parcels = new LinkedHashSet<>();
        final LandManager.Use use;
        double dry, steep, forest, taken, works, runs;
        double free, paid;
        Ground(Game g, BuildingsTemplate t, double free) {
            game = g;
            use = Game.groundUse(t);
            LandManager land = g.getLandManager();
            dry = land.getLinkedOwnedSqFt();
            steep = land.getSlopeSqFt() - land.getLandmasses().unlinkedReliefSqFt(CityLand.RELIEF_STEEP)
                    - land.getLandmasses().unlinkedReliefSqFt(CityLand.RELIEF_VERY_STEEP);
            forest = land.getLinkedForestSqFt();
            taken = land.getLinkedAllocatedSqFt();
            works = land.getWorksSqFt();
            runs = land.getRunsSqFt();
            this.free = Math.max(0, free + (g.getLandManager().freeForAnyone(Game.groundUse(t))
                    - g.getLandManager().getAvailableSqFt()));
        }
        private double available() {
            double wanted = 0;
            Zoning zones = game.getLandManager().getZoning();
            for (Zoning.Zone zone : zones.zones())
                wanted += Math.max(0, zone.targetSqFt(dry) - zone.builtSqFt());
            double open = Math.max(0, dry - taken - zones.keptShare() * forest - wanted);
            if (use == LandManager.Use.WORKS) return open;
            if (use == LandManager.Use.BUILDING) {
                double slopeAfterRuns = steep - (dry > 0 ? Math.min(steep, runs * steep / dry) : 0);
                return Math.max(0, open - Math.max(0, slopeAfterRuns - works));
            }
            double share = dry > 0 ? (dry - steep) / dry : 0;
            double runLimit = dry - taken;
            if (share > 0) runLimit = Math.min(runLimit, (dry - steep - taken + works + runs) / share - runs);
            return Math.max(0, Math.min(open, runLimit));
        }
        double use(double area) {
            double cash = 0, estimated = 0;
            while (area > free) {
                double shortage = area - free;
                AutoBuilder.Ground bought = AutoBuilder.groundFor(game, shortage, parcels);
                if (bought == null) {
                    cash += shortage * game.getLandManager().getOfficePricePerSqFt();
                    estimated += shortage;
                    free = area;
                } else {
                    double previous = available();
                    for (LandParcel parcel : bought.offers()) {
                        parcels.add(parcel.getId());
                        double linked = parcel.getSizeSqFt() - LandManager.sqFt(parcel.getUnlinkedDryKm2());
                        double usable = LandManager.sqFt(parcel.getReliefKm2(CityLand.RELIEF_FLAT)
                                + parcel.getReliefKm2(CityLand.RELIEF_FOOTHILL) - parcel.getUnlinkedBuildableKm2());
                        dry += linked;
                        steep += Math.max(0, linked - usable);
                        forest += LandManager.sqFt(parcel.getKm2(CityLand.FOREST) - parcel.getUnlinkedForestKm2());
                    }
                    free += available() - previous;
                    List<Integer> ids = new ArrayList<>(parcels);
                    double bill = game.isLandPaidFromVault() ? game.landTopUpLocal(ids) : game.landPriceLocal(ids);
                    cash += bill - paid;
                    paid = bill;
                }
            }
            free -= area;
            taken += area - estimated;
            if (use == LandManager.Use.WORKS) works += area - estimated;
            if (use == LandManager.Use.RUN) runs += area - estimated;
            return cash;
        }
    }

    private static Map<BuildingsTemplate, Long> ready(Game g, Map<BuildingsTemplate, Long> orders, double month) {
        Map<BuildingsTemplate, Long> result = new LinkedHashMap<>();
        for (Map.Entry<BuildingsTemplate, Long> entry : orders.entrySet()) {
            long count = 0;
            for (long unit = 1; unit <= entry.getValue(); unit++) {
                double date = g.getBuildingManager().siteUnitMonths(entry.getKey(), unit, g.getBuildingOutputAtEveryPost());
                if (Double.isFinite(date) && date <= month) count++;
            }
            if (count > 0) result.put(entry.getKey(), count);
        }
        return result;
    }

    private static double gross(Game g, BuildingsTemplate t, double use) {
        double price = Math.max(0, g.getBuildingManager().getConstructionMaterialPrice());
        double repairs = g.getEconomyManager().withBuildersTax(g.getBuildingManager().nonMaterialCost(t)
                + t.getConstructionMaterials() * price) * (ConstructionControl.paves(t)
                ? t.gravelRepairsAt(use) : t.repairsPerYear()) / 12;
        double utilities = t.getElectricityConsumption() * g.getEconomyManager().getPowerPriceFor(t)
                + t.getWaterConsumption() * g.getEconomyManager().getPricePerWaterUnit();
        return repairs + utilities + BuildCard.runningCost(g, t);
    }

    private static double running(Game g, Map<BuildingsTemplate, Long> before,
                                  Map<BuildingsTemplate, Long> after, BuildAdvice.Ahead date) {
        InfrastructureManager old = BuildAdvice.roads(g, before, date.scale()), next = BuildAdvice.roads(g, after, date.scale());
        Map<BuildingsTemplate, Long> delta = new LinkedHashMap<>(after);
        for (Map.Entry<BuildingsTemplate, Long> entry : before.entrySet()) delta.merge(entry.getKey(), -entry.getValue(), Long::sum);
        double result = 0;
        for (Map.Entry<BuildingsTemplate, Long> entry : delta.entrySet()) if (entry.getValue() != 0)
            result += entry.getValue() * gross(g, entry.getKey(), entry.getValue() > 0 ? next.getUtilisation() : old.getUtilisation());
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (ConstructionControl.paves(t)) {
            long retained = g.getBuildingManager().getQuantity(t.getId())
                    + Math.min(before.getOrDefault(t, 0L), after.getOrDefault(t, 0L));
            result += retained * (gross(g, t, next.getUtilisation()) - gross(g, t, old.getUtilisation()));
        }
        return result - (next.getTransitRiders() - old.getTransitRiders())
                * g.getEconomyManager().getTaxPolicy().monthlyFare();
    }

    private static double fraction(double[] service) {
        return service[1] > 0 ? Math.max(0, Math.min(1, service[0] / service[1])) : 1;
    }

    private static double restored(Game g, BuildAdvice.Measure measure, Map<BuildingsTemplate, Long> before,
                                   Map<BuildingsTemplate, Long> after, BuildAdvice.Ahead date) {
        double pre = fraction(BuildAdvice.supplyDemand(g, measure, before, date));
        double post = fraction(BuildAdvice.supplyDemand(g, measure, after, date));
        double loss = 0;
        Health health = g.getHealth();
        for (Sector sector : g.getSectors().all()) {
            double observed, a, b;
            switch (measure.kind()) {
                case WATER -> { observed = sector.getWaterRatio(); a = pre; b = post; }
                case ROADS, HIGHWAYS, TRANSIT -> {
                    double[] traffic = new double[Traffic.values().length];
                    for (Traffic stream : Traffic.values()) traffic[stream.ordinal()] =
                            g.getBuildingManager().totalBySector(sector.key(), t -> t.loadOf(stream));
                    observed = sector.getRoadRatio();
                    a = BuildAdvice.roads(g, before, date.scale()).throughputFor(traffic);
                    b = BuildAdvice.roads(g, after, date.scale()).throughputFor(traffic);
                }
                case CARE -> {
                    if (measure.care() != CareType.GENERAL) continue;
                    observed = sector.getHealthRatio();
                    a = health.workRatioAt(pre, health.getUnburiedRate(), health.getInjuryRate());
                    b = health.workRatioAt(post, health.getUnburiedRate(), health.getInjuryRate());
                }
                case POLICE -> {
                    double people = g.getCrime().getPopulation();
                    if (!(people > 0)) continue;
                    observed = sector.getHealthRatio();
                    a = health.workRatioAt(health.getCoverage(), health.getUnburiedRate(),
                            g.getCrime().crimesAt(pre) * Crime.VIOLENT_SHARE * Crime.MONTHS_OFF_PER_VIOLENT / people);
                    b = health.workRatioAt(health.getCoverage(), health.getUnburiedRate(),
                            g.getCrime().crimesAt(post) * Crime.VIOLENT_SHARE * Crime.MONTHS_OFF_PER_VIOLENT / people);
                }
                case DEATH -> {
                    double people = g.getPopulationManager().getPopulation();
                    if (!(people > 0)) continue;
                    double backlog = g.getHealthcare().getUnburied();
                    double handled = BuildAdvice.supplyDemand(g, measure, after, date)[0]
                            - BuildAdvice.supplyDemand(g, measure, before, date)[0];
                    observed = sector.getHealthRatio();
                    a = health.workRatioAt(health.getCoverage(),
                            Math.min(Health.MAX_UNBURIED_SICKNESS, backlog / people * Health.UNBURIED_WEIGHT), health.getInjuryRate());
                    b = health.workRatioAt(health.getCoverage(), Math.min(Health.MAX_UNBURIED_SICKNESS,
                            Math.max(0, backlog - handled) / people * Health.UNBURIED_WEIGHT), health.getInjuryRate());
                }
                default -> { continue; }
            }
            Sector.Statement book = sector.statement();
            if (observed > 0) loss += Math.max(0, book.revenue - book.inputs - book.electricity - book.water)
                    / observed * date.k() * Math.max(0, b - a);
        }
        return loss;
    }
}
