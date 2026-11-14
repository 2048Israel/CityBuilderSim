package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A work's discounted cost and the output its service prevents losing (HW1).
 * WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
 * are comparisons at today's prices, never entries in the city's money books.
 */
public final class WorksValue {
    private WorksValue() { }

    /** Present values in local thousands; jobs and payroll are shown separately from their tax return. */
    public record Value(double capital, double land, double running, double avoided, double payroll,
                        double payrollReturned, double jobs, double unfilled, double lead, double used) {
        public double cost() { return capital + land + running; }
        public double net() { return cost() - avoided - payrollReturned; }
        /** Informational cost per physical unit relieved; reserve-only work has no such denominator. */
        public double perUsed() { return used > 0 ? net() / used : Double.POSITIVE_INFINITY; }
        public String words() {
            return String.format("%d-year present value: order %s + land %s + running %s - prevented output loss %s"
                            + " - payroll returned %s = %s. Physical relief %.1f/month; %.0f posts (%.0f unfilled); payroll %s/month; opens %.1f months.",
                    BuildAdvice.LIFE_MONTHS / 12, DecisionLog.money(capital), DecisionLog.money(land),
                    DecisionLog.money(running), DecisionLog.money(avoided), DecisionLog.money(payrollReturned),
                    DecisionLog.money(net()), used, jobs, unfilled, DecisionLog.money(payroll), lead);
        }
    }

    public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                           BuildingsTemplate t, int count, double landLeft) {
        Game.BuildQuote q = g.quoteBuild(t, count);
        double lead = Double.isFinite(q.months) ? Math.max(0, q.months) : 0;
        double delay = Math.pow(1 + Math.max(0, BuildAdvice.lifeRate(g)) / 12, -lead);
        double factor = BuildAdvice.lifeFactor(g) * delay;
        double jobs = 0, payroll = 0, returned = 0;
        PopulationManager people = g.getPopulationManager();
        double[] wages = people.getWagesPerType(), fill = people.getJobFillRate();
        for (JobType job : JobType.values()) {
            double posts = count * (double) t.getJobs(job);
            jobs += posts;
            double paid = posts * wages[job.ordinal()] * fill[job.ordinal()];
            payroll += paid;
            returned += paid * g.getEconomyManager().getTaxPolicy().payslipShare(WageBand.of(job));
        }
        Map<BuildingsTemplate, Integer> order = Map.of(t, count);
        Relief relief = relief(g, m, site, order, lead);
        return new Value(q.total, BuildAdvice.landValue(g, t.getLandSqFt() * (double) count, landLeft),
                count * BuildAdvice.running(g, t, site) * factor, relief.avoided(), payroll, returned * factor,
                jobs, count * BuildAdvice.unfilledPosts(t, fill), lead, relief.used());
    }

    /** A paving is the whole replacement plan, including the ground freed and running-cost difference. */
    public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, int count, double landLeft) {
        Game.BuildQuote q = g.quotePave(count);
        BuildingsTemplate from = g.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM);
        BuildingsTemplate to = g.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        double lead = Double.isFinite(q.months) ? Math.max(0, q.months) : 0;
        double factor = BuildAdvice.lifeFactor(g) * Math.pow(1 + Math.max(0, BuildAdvice.lifeRate(g)) / 12, -lead);
        Relief relief = relief(g, m, site, BuildAdvice.times(Map.of(), BuildAdvice.pavingStep(g), count), lead);
        return new Value(q.total, -BuildAdvice.landValue(g, BuildAdvice.pavingFrees(g) * count, landLeft),
                count * (BuildAdvice.running(g, to, site) - BuildAdvice.running(g, from, site, false)) * factor,
                relief.avoided(), 0, 0, 0, 0, lead, relief.used());
    }

    private record Relief(double used, double avoided) { }

    /** Power at monthly resolution and other services at yearly midpoints, with the same real rate and long city trend. */
    public static double avoided(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                                 Map<BuildingsTemplate, Integer> order, double lead) {
        return relief(g, m, site, order, lead).avoided();
    }

    /** Demand is physical: the reserve dial sizes orders but cannot create lost output. */
    private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                                 Map<BuildingsTemplate, Integer> order, double lead) {
        Map<Sector, double[]> traffic = new LinkedHashMap<>();
        if (BuildAdvice.roadKind(m) || m.kind() == BuildAdvice.Kind.TRANSIT) for (Sector sector : g.getSectors().all()) {
            double[] mix = new double[Traffic.values().length];
            for (Traffic stream : Traffic.values()) mix[stream.ordinal()] = g.getBuildingManager()
                    .totalBySector(sector.key(), t -> t.loadOf(stream));
            traffic.put(sector, mix);
        }
        double rate = Math.max(0, BuildAdvice.lifeRate(g)) / 12, result = 0, used = 0, weights = 0;
        boolean power = m.kind() == BuildAdvice.Kind.POWER;
        Map<BuildingsTemplate, PendingWork> schedule = power ? completionSchedule(g, site) : Map.of();
        Map<BuildingsTemplate, PendingWork> loads = power ? completionSchedule(g, pendingLoads(g)) : Map.of();
        double powerPotential = 0;
        if (power) for (Sector sector : g.getSectors().all()) {
            Sector.Statement book = sector.statement();
            double valueAdded = Math.max(0, book.revenue - book.inputs - book.electricity - book.water);
            if (sector.getEnergyRatio() > 0) powerPotential += valueAdded / sector.getEnergyRatio();
        }
        Map<BuildingsTemplate, Integer> previousReady = null;
        double[] powerBefore = null, powerAfter = null;
        double basePowerDemand = g.getServicesManager().getUtilitiesHandler().getConsumption();
        int interval = power ? 1 : 12;
        for (int start = 0; start < BuildAdvice.LIFE_MONTHS; start += interval) {
            double months = lead + start + interval / 2.0;
            BuildAdvice.Ahead at = new BuildAdvice.Ahead(months, BuildAdvice.lifeTrend(g, months), 0);
            Map<BuildingsTemplate, Integer> ready = power ? completedAt(g, schedule, months) : site;
            Map<BuildingsTemplate, Integer> with = BuildAdvice.plus(ready, order);
            double weight = 0;
            for (int j = 1; j <= interval; j++) weight += Math.pow(1 + rate, -(lead + start + j));
            double[] before, after;
            if (power) {
                // Capacity and the works' own draw stay fixed until another unit opens.
                if (!ready.equals(previousReady)) {
                    powerBefore = BuildAdvice.supplyDemand(g, m, ready, BuildAdvice.NOW);
                    powerAfter = BuildAdvice.supplyDemand(g, m, with, BuildAdvice.NOW);
                    previousReady = ready;
                }
                double demand = basePowerDemand * at.scale() + pendingPowerLoad(g, loads, months);
                before = new double[] {powerBefore[0], demand + (powerBefore[1] - basePowerDemand)};
                after = new double[] {powerAfter[0], demand + (powerAfter[1] - basePowerDemand)};
                result += weight * powerPotential * Math.max(0, cover(after) - cover(before));
            } else {
                before = BuildAdvice.supplyDemand(g, m, ready, at);
                after = BuildAdvice.supplyDemand(g, m, with, at);
                result += weight * outputRestored(g, m, ready, with, at, traffic);
            }
            used += weight * Math.max(0, Math.max(0, before[1] - before[0]) - Math.max(0, after[1] - after[0]));
            weights += weight;
        }
        return new Relief(weights > 0 ? used / weights : 0, Math.max(0, result));
    }

    /** Electrical draw of already ordered non-power works that their present queue opens by this date. */
    public static double pendingPowerLoad(Game g, double months) {
        return months > 0 ? pendingPowerLoad(g, completionSchedule(g, pendingLoads(g)), months) : 0;
    }

    private static Map<BuildingsTemplate, Integer> pendingLoads(Game g) {
        Map<BuildingsTemplate, Integer> loads = new LinkedHashMap<>();
        for (BuildingsStacks stack : g.getBuildingManager().getStacksUnderConstruction()) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCategory() != BuildingType.ELECTRICITY && t.getElectricityConsumption() > 0)
                loads.put(t, stack.getUnderConstruction());
        }
        return loads;
    }

    private static double pendingPowerLoad(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months) {
        double load = 0;
        for (Map.Entry<BuildingsTemplate, Integer> e : completedAt(g, schedule, months).entrySet())
            load += e.getValue() * (double) e.getKey().getElectricityConsumption();
        return load;
    }

    private record PendingWork(int units, double first, double interval, boolean rushed) { }

    /** The same queue-aware unit quotes the automatic completion deadline reads. */
    private static Map<BuildingsTemplate, PendingWork> completionSchedule(Game g,
            Map<BuildingsTemplate, Integer> site) {
        Map<BuildingsTemplate, PendingWork> schedule = new LinkedHashMap<>();
        BuildingManager bm = g.getBuildingManager();
        double output = g.getBuildingOutputAtEveryPost();
        for (Map.Entry<BuildingsTemplate, Integer> e : site.entrySet()) {
            BuildingsTemplate t = e.getKey();
            double first = bm.siteUnitMonths(t, 1, output);
            double second = e.getValue() > 1 ? bm.siteUnitMonths(t, 2, output) : first;
            schedule.put(t, new PendingWork(e.getValue(), first, second - first,
                    bm.getControl().isRushed(ConstructionControl.keyOf(t))));
        }
        return schedule;
    }

    /** Quote-completed units at this date. Priority delay precedes the first unit in full. */
    private static Map<BuildingsTemplate, Integer> completedAt(Game g,
            Map<BuildingsTemplate, PendingWork> schedule, double months) {
        Map<BuildingsTemplate, Integer> ready = new LinkedHashMap<>();
        for (Map.Entry<BuildingsTemplate, PendingWork> e : schedule.entrySet()) {
            BuildingsTemplate t = e.getKey();
            PendingWork p = e.getValue();
            int finished = 0;
            if (p.units() <= 0) finished = p.units();
            else if (Double.isFinite(p.first()) && months >= p.first()) {
                if (p.units() == 1) finished = 1;
                else if (!p.rushed() && p.interval() > 0)
                    finished = (int) Math.min(p.units(), 1 + Math.floor((months - p.first()) / p.interval()));
                else {
                    int lo = 0, hi = p.units() + 1;
                    while (hi - lo > 1) {
                        int mid = lo + (hi - lo) / 2;
                        double quote = g.getBuildingManager().siteUnitMonths(t, mid, g.getBuildingOutputAtEveryPost());
                        if (Double.isFinite(quote) && quote <= months) lo = mid;
                        else hi = mid;
                    }
                    finished = lo;
                }
            }
            if (finished != 0) ready.put(t, finished);
        }
        return ready;
    }

    static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added, BuildAdvice.Ahead at) {
        return cover(BuildAdvice.supplyDemand(g, m, added, at));
    }

    private static double cover(double[] sd) {
        return sd[1] > 0 ? Math.max(0, Math.min(1, sd[0] / sd[1])) : 1;
    }

    /** Only effects that the simulation actually charges: utility rationing, traffic and absence. */
    static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> before,
                                 Map<BuildingsTemplate, Integer> after, BuildAdvice.Ahead at, Map<Sector, double[]> traffic) {
        double pre = cover(g, m, before, at), post = cover(g, m, after, at);
        InfrastructureManager r0 = null, r1 = null;
        if (BuildAdvice.roadKind(m) || m.kind() == BuildAdvice.Kind.TRANSIT) {
            r0 = BuildAdvice.roads(g, before, at.scale());
            r1 = BuildAdvice.roads(g, after, at.scale());
        }
        double result = 0;
        Health health = g.getHealth();
        for (Sector sector : g.getSectors().all()) {
            Sector.Statement book = sector.statement();
            double valueAdded = Math.max(0, book.revenue - book.inputs - book.electricity - book.water);
            double oldFactor, a, b;
            switch (m.kind()) {
                case POWER: oldFactor = sector.getEnergyRatio(); a = pre; b = post; break;
                case WATER: oldFactor = sector.getWaterRatio(); a = pre; b = post; break;
                case ROADS: case HIGHWAYS: case TRANSIT: {
                    double[] mix = traffic.get(sector);
                    oldFactor = sector.getRoadRatio(); a = r0.throughputFor(mix); b = r1.throughputFor(mix); break;
                }
                case CARE:
                    if (m.care() != CareType.GENERAL) continue;
                    oldFactor = sector.getHealthRatio();
                    a = health.workRatioAt(pre, health.getUnburiedRate(), health.getInjuryRate());
                    b = health.workRatioAt(post, health.getUnburiedRate(), health.getInjuryRate()); break;
                case POLICE: {
                    oldFactor = sector.getHealthRatio();
                    double pop = g.getCrime().getPopulation();
                    if (!(pop > 0)) continue;
                    double injury0 = g.getCrime().crimesAt(pre) * Crime.VIOLENT_SHARE * Crime.MONTHS_OFF_PER_VIOLENT / pop;
                    double injury1 = g.getCrime().crimesAt(post) * Crime.VIOLENT_SHARE * Crime.MONTHS_OFF_PER_VIOLENT / pop;
                    a = health.workRatioAt(health.getCoverage(), health.getUnburiedRate(), injury0);
                    b = health.workRatioAt(health.getCoverage(), health.getUnburiedRate(), injury1); break;
                }
                case DEATH: {
                    oldFactor = sector.getHealthRatio();
                    double pop = g.getPopulationManager().getPopulation();
                    if (!(pop > 0)) continue;
                    double backlog = g.getHealthcare().getUnburied();
                    double handled = BuildAdvice.supplyDemand(g, m, after, at)[0]
                            - BuildAdvice.supplyDemand(g, m, before, at)[0];
                    double beforeSickness = Math.min(Health.MAX_UNBURIED_SICKNESS, backlog / pop * Health.UNBURIED_WEIGHT);
                    double afterSickness = Math.min(Health.MAX_UNBURIED_SICKNESS,
                            Math.max(0, backlog - handled) / pop * Health.UNBURIED_WEIGHT);
                    a = health.workRatioAt(health.getCoverage(), beforeSickness, health.getInjuryRate());
                    b = health.workRatioAt(health.getCoverage(), afterSickness, health.getInjuryRate()); break;
                }
                default: continue;
            }
            // A zero observed factor gives no defensible book-based potential; no invented output or multiplier.
            if (oldFactor > 0) result += valueAdded / oldFactor * at.k() * Math.max(0, b - a);
        }
        return result;
    }
}
