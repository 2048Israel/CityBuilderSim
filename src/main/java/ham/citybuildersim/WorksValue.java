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

    /** Twenty yearly midpoints, the same real rate and long city trend as the service comparison. */
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
        for (int year = 0; year < BuildAdvice.LIFE_YEARS; year++) {
            double months = lead + year * 12 + 6;
            BuildAdvice.Ahead at = new BuildAdvice.Ahead(months, BuildAdvice.lifeTrend(g, months), 0);
            Map<BuildingsTemplate, Integer> ready = m.kind() == BuildAdvice.Kind.POWER ? powerOnSiteAt(g, site, months) : site;
            Map<BuildingsTemplate, Integer> with = BuildAdvice.plus(ready, order);
            double weight = 0;
            for (int j = 1; j <= 12; j++) weight += Math.pow(1 + rate, -(lead + year * 12 + j));
            result += weight * outputRestored(g, m, ready, with, at, traffic);
            double[] before = BuildAdvice.supplyDemand(g, m, ready, at), after = BuildAdvice.supplyDemand(g, m, with, at);
            used += weight * Math.max(0, Math.max(0, before[1] - before[0]) - Math.max(0, after[1] - after[0]));
            weights += weight;
        }
        return new Relief(weights > 0 ? used / weights : 0, Math.max(0, result));
    }

    /** Pending power contributes only after its construction output can finish it, including preparation. */
    private static Map<BuildingsTemplate, Integer> powerOnSiteAt(Game g,
            Map<BuildingsTemplate, Integer> site, double months) {
        Map<BuildingsTemplate, Integer> ready = new LinkedHashMap<>();
        for (Map.Entry<BuildingsTemplate, Integer> e : site.entrySet()) {
            BuildingsTemplate t = e.getKey();
            BuildingsStacks stack = g.getBuildingManager().getStack(t);
            if (stack == null || stack.getUnderConstruction() <= 0) {
                ready.put(t, e.getValue());
                continue;
            }
            double wait = g.onSiteMonths(t);
            double owed = stack.getUnderConstruction() * (double) t.getConstructionPoints() - stack.getConstructionProgress();
            double progress = stack.getConstructionProgress() + (Double.isFinite(wait) && wait > 0 ? owed * months / wait : 0);
            int finished = (int) Math.max(0, Math.min(e.getValue(), Math.floor(progress / t.getConstructionPoints())));
            if (finished > 0) ready.put(t, finished);
        }
        return ready;
    }

    static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added, BuildAdvice.Ahead at) {
        double[] sd = BuildAdvice.supplyDemand(g, m, added, at);
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
