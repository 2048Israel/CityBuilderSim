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
    public record Value(double capital, double land, double futureCapital, int futureUnits, double financing,
                        double running, double avoided, double payroll, double payrollReturned,
                        double jobs, double unfilled, double lead, double used) {
        public double cost() { return capital + land + futureCapital + financing + running; }
        public double net() { return cost() - avoided - payrollReturned; }
        /** Informational cost per physical unit relieved; reserve-only work has no such denominator. */
        public double perUsed() { return used > 0 ? net() / used : Double.POSITIVE_INFINITY; }
        public String words() {
            return String.format("%d-year maintained-service present value: order %s + land %s"
                            + " + %d later works and land %s + new financing %s + running %s"
                            + " - prevented output loss %s - payroll returned %s = %s."
                            + " Physical relief %.1f/month; initial %.0f posts (%.0f unfilled);"
                            + " initial payroll %s/month; opens %.1f months.",
                    BuildAdvice.LIFE_MONTHS / 12, DecisionLog.money(capital), DecisionLog.money(land),
                    futureUnits, DecisionLog.money(futureCapital), DecisionLog.money(financing),
                    DecisionLog.money(running), DecisionLog.money(avoided), DecisionLog.money(payrollReturned),
                    DecisionLog.money(net()), used, jobs, unfilled, DecisionLog.money(payroll), lead);
        }
    }

    public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                           BuildingsTemplate t, int count, double landLeft) {
        return of(g, m, site, t, count, landLeft, g.getCash());
    }

    /** The same comparison after earlier cards have used some of the treasury. */
    public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                           BuildingsTemplate t, int count, double landLeft, double cashLeft) {
        if (count <= 0) return new Value(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Game.BuildQuote q = g.quoteBuild(t, count);
        double lead = Double.isFinite(q.months) ? Math.max(0, q.months) : 0;
        double factor = BuildAdvice.lifeFactor(g)
                * Math.pow(1 + Math.max(0, BuildAdvice.lifeRate(g)) / 12, -lead);
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
        double footprint = t.getLandSqFt() * (double) count;
        GroundPlan ground = new GroundPlan(g, t, landLeft);
        double usableGround = ground.free;
        double groundCash = ground.take(g.landNeededFor(t, count));
        Maintenance maintenance = new Maintenance(t, count, usableGround, cashLeft, q.total + groundCash, ground);
        Relief relief = relief(g, m, site, order, lead, maintenance);
        return new Value(q.total, BuildAdvice.landValue(g, footprint, usableGround), relief.futureCapital(),
                relief.futureUnits(), relief.financing(), relief.running(),
                relief.avoided(), payroll, returned * factor + returned / count * relief.futureMonths(),
                jobs, count * BuildAdvice.unfilledPosts(t, fill), lead, relief.used());
    }

    /** A paving is the whole replacement plan, including the ground freed and running-cost difference. */
    public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, int count, double landLeft) {
        return paving(g, m, site, count, landLeft, g.getCash());
    }

    public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                               int count, double landLeft, double cashLeft) {
        if (count <= 0) return new Value(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Game.BuildQuote q = g.quotePave(count);
        BuildingsTemplate to = g.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_TO);
        double lead = Double.isFinite(q.months) ? Math.max(0, q.months) : 0;
        GroundPlan ground = new GroundPlan(g, to, landLeft);
        ground.take(-BuildAdvice.pavingFrees(g) * count);
        Maintenance maintenance = new Maintenance(to, count, landLeft, cashLeft, q.total, ground);
        Relief relief = relief(g, m, site, BuildAdvice.times(Map.of(), BuildAdvice.pavingStep(g), count), lead, maintenance);
        return new Value(q.total, -BuildAdvice.landValue(g, BuildAdvice.pavingFrees(g) * count, landLeft),
                relief.futureCapital(), relief.futureUnits(), relief.financing(), relief.running(),
                relief.avoided(), 0, 0, 0, 0, lead, relief.used());
    }

    /** Later works are priced in the comparison and ordered by later monthly passes. */
    private record Maintenance(BuildingsTemplate template, int count, double landLeft,
                               double cashLeft, double initialCashCost, GroundPlan ground) { }

    private record Relief(double used, double avoided, double futureCapital, int futureUnits,
                          double futureMonths, double financing, double running) { }

    private record Financing(double monthlyCoupon, double issueCost, int months) { }

    private static Financing financing(Game g, double gap) {
        if (!(gap > 0)) return new Financing(0, 0, 0);
        DebtQuote q = AutoBuilder.bondQuote(g, gap, g.getAutoBuilder().getCap());
        if (q == null || !(q.cashReceived() > 0)) {
            double rate = Math.max(0, g.getDebtManager().quoteRate(0, Game.BUILD_BOND_YEARS * 12)) / 12;
            return new Financing(gap * rate, 0, Game.BUILD_BOND_YEARS * 12);
        }
        int months = q.auction() == null ? q.duration() * 12 : q.auction().ask().months();
        double scale = Math.max(1, gap / q.cashReceived());
        return new Financing(q.monthlyInterest() * scale, (q.faceValue() - q.cashReceived()) * scale, months);
    }

    /** Whole parcels retain their unused ground, and the foreign vault pays only once across the sequence. */
    private static final class GroundPlan {
        private final Game game;
        private final java.util.Set<Integer> bought = new java.util.LinkedHashSet<>();
        private double free, paid;

        GroundPlan(Game game, BuildingsTemplate template, double available) {
            this.game = game;
            free = BuildAdvice.availableGround(game, template, available);
        }

        double take(double area) {
            double cash = 0, lack = area - free;
            if (lack > 0) {
                AutoBuilder.Ground ground = AutoBuilder.groundFor(game, lack, bought);
                if (ground == null) {
                    // No offered parcel can be bought now. Future unavailable ground uses the office's estimate.
                    cash = lack * game.getLandManager().getOfficePricePerSqFt();
                    free += lack;
                } else {
                    for (LandParcel parcel : ground.offers()) {
                        bought.add(parcel.getId());
                        free += parcel.getSizeSqFt() - parcel.getUnlinkedDryKm2() * LandManager.SQ_FT_PER_KM2;
                    }
                    java.util.List<Integer> ids = new java.util.ArrayList<>(bought);
                    double total = game.isLandPaidFromVault() ? game.landTopUpLocal(ids) : game.landPriceLocal(ids);
                    cash = total - paid;
                    paid = total;
                }
            }
            free -= area;
            return cash;
        }
    }

    private static double annuity(double monthlyRate, double months) {
        if (!(months > 0)) return 0;
        return Math.abs(monthlyRate) > 1e-12 ? (1 - Math.pow(1 + monthlyRate, -months)) / monthlyRate : months;
    }

    /** Power at monthly resolution and other services at yearly midpoints, with the same real rate and long city trend. */
    public static double avoided(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                                 Map<BuildingsTemplate, Integer> order, double lead) {
        return relief(g, m, site, order, lead, null).avoided();
    }

    /** Demand is physical: the reserve dial sizes orders but cannot create lost output. */
    private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site,
                                 Map<BuildingsTemplate, Integer> order, double lead, Maintenance maintenance) {
        Map<Sector, double[]> traffic = traffic(g, m);
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
        double horizon = lead + BuildAdvice.LIFE_MONTHS;
        double futureCapital = 0, futureMonths = 0, financingCost = 0, runningCost = 0;
        int futureUnits = 0;
        double futureLead = 0, initialFootprint = 0, cashForLater = 0;
        double expectedInflation = Math.max(-.99, g.getBusinessInvestment().getExpectedInflation());
        double couponDiscount = (1 + rate) * (1 + expectedInflation / 12) - 1;
        Map<Double, Financing> creditQuotes = new java.util.HashMap<>();
        if (maintenance != null) {
            double cash = g.getAutoBuilder().spendableForMeasure(g, m) - (g.getCash() - maintenance.cashLeft());
            double gap = Math.max(0, maintenance.initialCashCost() - cash);
            Financing funding = creditQuotes.computeIfAbsent(gap, amount -> financing(g, amount));
            cashForLater = Math.max(0, cash - maintenance.initialCashCost());
            financingCost = funding.issueCost() + funding.monthlyCoupon()
                    * annuity(couponDiscount, Math.min(horizon, funding.months()));
            futureLead = g.quoteBuild(maintenance.template(), 1).months;
            if (!Double.isFinite(futureLead)) futureLead = 0;
            for (Map.Entry<BuildingsTemplate, Integer> e : order.entrySet())
                initialFootprint += e.getValue() * (double) e.getKey().getLandSqFt();
        }
        Map<BuildingsTemplate, Integer> previousReady = null, previousWith = null;
        double[] powerBefore = null, powerAfter = null;
        double basePowerDemand = g.getServicesManager().getUtilitiesHandler().getConsumption();
        int interval = power ? 1 : 12;
        for (int start = 0; start < BuildAdvice.LIFE_MONTHS; start += interval) {
            double months = lead + start + interval / 2.0;
            BuildAdvice.Ahead at = new BuildAdvice.Ahead(months, BuildAdvice.lifeTrend(g, months), 0);
            Map<BuildingsTemplate, Integer> ready = power ? completedAt(g, schedule, months) : site;
            Map<BuildingsTemplate, Integer> with = BuildAdvice.plus(ready, order);
            if (maintenance != null) {
                BuildingsTemplate t = maintenance.template();
                with = BuildAdvice.plus(with, Map.of(t, futureUnits));
                BuildAdvice.Ahead target = new BuildAdvice.Ahead(months, at.k(), g.getAutoBuilder().getSlack());
                if (!BuildAdvice.maintained(g, m, with, target)) {
                    int most = t.isHydro() ? Math.max(0, g.hydroSlotsFree(t) - maintenance.count() - futureUnits)
                            : BuildAdvice.MOST;
                    int[] extra = BuildAdvice.count(g, m, with, Map.of(t, 1), target, most, true);
                    if (extra[0] > 0) {
                        double cashCost = g.quoteBuild(t, extra[0]).total;
                        double area = t.getLandSqFt() * (double) extra[0];
                        double landLeft = Math.max(0, maintenance.landLeft() - initialFootprint
                                - futureUnits * (double) t.getLandSqFt());
                        double land = BuildAdvice.landValue(g, area, landLeft);
                        double paidAt = Math.max(0, months - futureLead);
                        double discount = Math.pow(1 + rate, -paidAt);
                        futureCapital += (cashCost + land) * discount;
                        double cashNeeded = cashCost + maintenance.ground().take(g.landNeededFor(t, extra[0]));
                        double financed = Math.max(0, cashNeeded - cashForLater);
                        cashForLater = Math.max(0, cashForLater - cashNeeded);
                        Financing funding = creditQuotes.computeIfAbsent(financed, amount -> financing(g, amount));
                        financingCost += (funding.issueCost() + funding.monthlyCoupon()
                                * annuity(couponDiscount, Math.min(horizon - paidAt, funding.months()))) * discount;
                        futureUnits += extra[0];
                        with = BuildAdvice.plus(with, Map.of(t, extra[0]));
                    }
                }
            }
            double weight = 0;
            for (int j = 1; j <= interval; j++) weight += Math.pow(1 + rate, -(lead + start + j));
            futureMonths += weight * futureUnits;
            if (maintenance != null) runningCost += weight * BuildAdvice.runningChange(g, ready, with, at);
            double[] before, after;
            if (power) {
                // Reuse the physical components until a pending or planned unit changes them.
                if (!ready.equals(previousReady) || !with.equals(previousWith)) {
                    powerBefore = BuildAdvice.supplyDemand(g, m, ready, BuildAdvice.NOW);
                    powerAfter = BuildAdvice.supplyDemand(g, m, with, BuildAdvice.NOW);
                    previousReady = ready;
                    previousWith = with;
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
        return new Relief(weights > 0 ? used / weights : 0, Math.max(0, result), futureCapital,
                futureUnits, futureMonths, financingCost, runningCost);
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

    private static Map<Sector, double[]> traffic(Game g, BuildAdvice.Measure m) {
        Map<Sector, double[]> traffic = new LinkedHashMap<>();
        if (BuildAdvice.roadKind(m) || m.kind() == BuildAdvice.Kind.TRANSIT) for (Sector sector : g.getSectors().all()) {
            double[] mix = new double[Traffic.values().length];
            for (Traffic stream : Traffic.values()) mix[stream.ordinal()] = g.getBuildingManager()
                    .totalBySector(sector.key(), t -> t.loadOf(stream));
            traffic.put(sector, mix);
        }
        return traffic;
    }

    /** The current monthly output loss that a fully serving available work could relieve. */
    static double shortageLoss(Game g, BuildAdvice.Measure m) {
        double loss = 0;
        Map<Sector, double[]> traffic = traffic(g, m);
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (!m.serves(t) || !(BuildAdvice.built(g, m, t) > 0)) continue;
            int[] n = BuildAdvice.count(g, m, Map.of(), Map.of(t, 1), BuildAdvice.NOW, BuildAdvice.MOST, true);
            if (n[0] > 0)
                loss = Math.max(loss, outputRestored(g, m, Map.of(), Map.of(t, n[0]), BuildAdvice.NOW, traffic));
        }
        return loss;
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
