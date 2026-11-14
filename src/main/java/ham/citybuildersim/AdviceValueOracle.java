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
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, measure);
        Map<BuildingsTemplate, Integer> order = BuildAdvice.added(g, s);
        Game.BuildQuote quote = s.paving() ? g.quotePave(s.count()) : g.quoteBuild(t, s.count());
        double opening = Double.isFinite(quote.months) ? Math.max(0, quote.months) : 0;
        double monthly = Math.max(0, BuildAdvice.lifeRate(g)) / 12;
        double nominal = (1 + monthly)
                * (1 + Math.max(-.99, g.getBusinessInvestment().getExpectedInflation()) / 12) - 1;
        double footprint = s.landSqFt();
        double initialLand = s.paving() ? -BuildAdvice.landValue(g, -footprint, land)
                : BuildAdvice.landValue(g, footprint, land);
        Ground ground = new Ground(g, t, land);
        double invoice = quote.total + ground.use(s.paving() ? footprint : g.landNeededFor(t, s.count()));
        double available = g.getAutoBuilder().spendableForMeasure(g, measure) - (g.getCash() - treasury);
        double initialFinance = credit(g, Math.max(0, invoice - available), nominal,
                opening + BuildAdvice.LIFE_MONTHS);
        available = Math.max(0, available - invoice);
        double perUnitReturned = 0;
        double[] wages = g.getPopulationManager().getWagesPerType(), fill = g.getPopulationManager().getJobFillRate();
        if (!s.paving()) for (JobType job : JobType.values())
            perUnitReturned += s.count() * (double) t.getJobs(job) * wages[job.ordinal()] * fill[job.ordinal()]
                    * g.getEconomyManager().getTaxPolicy().payslipShare(WageBand.of(job));
        double returned = perUnitReturned * BuildAdvice.lifeFactor(g) * Math.pow(1 + monthly, -opening);
        perUnitReturned /= s.count();
        double capitalLater = 0, financing = initialFinance, operating = 0, saved = 0, units = 0, weights = 0, unitMonths = 0;
        int additions = 0;
        double nextLead = g.quoteBuild(t, 1).months;
        if (!Double.isFinite(nextLead)) nextLead = 0;
        double powerPotential = 0;
        for (Sector sector : g.getSectors().all()) if (sector.getEnergyRatio() > 0) {
            Sector.Statement book = sector.statement();
            powerPotential += Math.max(0, book.revenue - book.inputs - book.electricity - book.water) / sector.getEnergyRatio();
        }
        Map<BuildingsTemplate, Integer> loads = new LinkedHashMap<>();
        if (power) for (BuildingsStacks stack : g.getBuildingManager().getStacksUnderConstruction()) {
            BuildingsTemplate load = stack.getBuilding();
            if (load.getCategory() != BuildingType.ELECTRICITY && load.getElectricityConsumption() > 0)
                loads.put(load, stack.getUnderConstruction());
        }
        int block = power ? 1 : 12;
        for (int start = 0; start < BuildAdvice.LIFE_MONTHS; start += block) {
            double date = opening + start + block / 2.0;
            BuildAdvice.Ahead future = new BuildAdvice.Ahead(date, BuildAdvice.lifeTrend(g, date), 0);
            Map<BuildingsTemplate, Integer> base = power ? ready(g, site, date) : site;
            Map<BuildingsTemplate, Integer> planned = BuildAdvice.plus(BuildAdvice.plus(base, order), Map.of(t, additions));
            if (!BuildAdvice.maintained(g, measure, planned,
                    new BuildAdvice.Ahead(date, future.k(), g.getAutoBuilder().getSlack()))) {
                int limit = t.isHydro() ? Math.max(0, g.hydroSlotsFree(t) - s.count() - additions) : BuildAdvice.MOST;
                int extra = BuildAdvice.count(g, measure, planned, Map.of(t, 1),
                        new BuildAdvice.Ahead(date, future.k(), g.getAutoBuilder().getSlack()), limit, true)[0];
                if (extra > 0) {
                    double build = g.quoteBuild(t, extra).total;
                    double free = Math.max(0, land - footprint - additions * (double) t.getLandSqFt());
                    double paidDate = Math.max(0, date - nextLead);
                    double discount = Math.pow(1 + monthly, -paidDate);
                    capitalLater += (build + BuildAdvice.landValue(g, t.getLandSqFt() * (double) extra, free)) * discount;
                    double needed = build + ground.use(g.landNeededFor(t, extra));
                    financing += credit(g, Math.max(0, needed - available), nominal,
                            Math.min(opening + BuildAdvice.LIFE_MONTHS - paidDate, Game.BUILD_BOND_YEARS * 12)) * discount;
                    available = Math.max(0, available - needed);
                    additions += extra;
                    planned = BuildAdvice.plus(planned, Map.of(t, extra));
                }
            }
            double weight = 0;
            for (int month = 1; month <= block; month++)
                weight += Math.pow(1 + monthly, -(opening + start + month));
            unitMonths += weight * additions;
            operating += weight * running(g, base, planned, future);
            double[] before = BuildAdvice.supplyDemand(g, measure, base, power ? BuildAdvice.NOW : future);
            double[] after = BuildAdvice.supplyDemand(g, measure, planned, power ? BuildAdvice.NOW : future);
            if (power) {
                double present = g.getServicesManager().getUtilitiesHandler().getConsumption();
                double pendingLoad = 0;
                for (Map.Entry<BuildingsTemplate, Integer> load : ready(g, loads, date).entrySet())
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

    private static double credit(Game g, double gap, double discount, double months) {
        if (!(gap > 0)) return 0;
        DebtQuote q = g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE,
                g.getAutoBuilder().getCap());
        double coupon, spread;
        if (q.cashReceived() > 0) {
            double scale = Math.max(1, gap / q.cashReceived());
            coupon = q.monthlyInterest() * scale;
            spread = (q.faceValue() - q.cashReceived()) * scale;
            months = Math.min(months, q.auction() == null ? q.duration() * 12 : q.auction().ask().months());
        } else {
            coupon = gap * Math.max(0, g.getDebtManager().quoteRate(0, Game.BUILD_BOND_YEARS * 12)) / 12;
            spread = 0;
        }
        double annuity = Math.abs(discount) > 1e-12 ? (1 - Math.pow(1 + discount, -months)) / discount : months;
        return spread + coupon * annuity;
    }

    private static final class Ground {
        final Game game;
        final Set<Integer> parcels = new LinkedHashSet<>();
        double free, paid;
        Ground(Game g, BuildingsTemplate t, double free) {
            game = g;
            this.free = Math.min(Math.max(0, free), Math.max(0, g.getLandManager().freeForAnyone(Game.groundUse(t))));
        }
        double use(double area) {
            double shortage = area - free, cash = 0;
            if (shortage > 0) {
                AutoBuilder.Ground bought = AutoBuilder.groundFor(game, shortage, parcels);
                if (bought == null) {
                    cash = shortage * game.getLandManager().getOfficePricePerSqFt();
                    free += shortage;
                } else {
                    for (LandParcel parcel : bought.offers()) {
                        parcels.add(parcel.getId());
                        free += parcel.getSizeSqFt() - parcel.getUnlinkedDryKm2() * LandManager.SQ_FT_PER_KM2;
                    }
                    List<Integer> ids = new ArrayList<>(parcels);
                    double bill = game.isLandPaidFromVault() ? game.landTopUpLocal(ids) : game.landPriceLocal(ids);
                    cash = bill - paid;
                    paid = bill;
                }
            }
            free -= area;
            return cash;
        }
    }

    private static Map<BuildingsTemplate, Integer> ready(Game g, Map<BuildingsTemplate, Integer> orders, double month) {
        Map<BuildingsTemplate, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<BuildingsTemplate, Integer> entry : orders.entrySet()) {
            int count = 0;
            for (int unit = 1; unit <= entry.getValue(); unit++) {
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
        double utilities = t.getElectricityConsumption() * g.getEconomyManager().getPricePerWatt()
                + t.getWaterConsumption() * g.getEconomyManager().getPricePerWaterUnit();
        return repairs + utilities + BuildCard.runningCost(g, t);
    }

    private static double running(Game g, Map<BuildingsTemplate, Integer> before,
                                  Map<BuildingsTemplate, Integer> after, BuildAdvice.Ahead date) {
        InfrastructureManager old = BuildAdvice.roads(g, before, date.scale()), next = BuildAdvice.roads(g, after, date.scale());
        Map<BuildingsTemplate, Integer> delta = new LinkedHashMap<>(after);
        for (Map.Entry<BuildingsTemplate, Integer> entry : before.entrySet()) delta.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        double result = 0;
        for (Map.Entry<BuildingsTemplate, Integer> entry : delta.entrySet()) if (entry.getValue() != 0)
            result += entry.getValue() * gross(g, entry.getKey(), entry.getValue() > 0 ? next.getUtilisation() : old.getUtilisation());
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (ConstructionControl.paves(t)) {
            int retained = g.getBuildingManager().getQuantity(t.getId())
                    + Math.min(before.getOrDefault(t, 0), after.getOrDefault(t, 0));
            result += retained * (gross(g, t, next.getUtilisation()) - gross(g, t, old.getUtilisation()));
        }
        return result - (next.getTransitRiders() - old.getTransitRiders())
                * g.getEconomyManager().getTaxPolicy().monthlyFare();
    }

    private static double fraction(double[] service) {
        return service[1] > 0 ? Math.max(0, Math.min(1, service[0] / service[1])) : 1;
    }

    private static double restored(Game g, BuildAdvice.Measure measure, Map<BuildingsTemplate, Integer> before,
                                   Map<BuildingsTemplate, Integer> after, BuildAdvice.Ahead date) {
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
