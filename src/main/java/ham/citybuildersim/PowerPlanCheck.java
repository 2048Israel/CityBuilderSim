package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * The power planner (PW3): wind and solar by place and season, the reserve test and the spread, checked on caused cities.
 *
 * WHY. PW1's planner counted a wind farm at a fixed firm third (PowerDispatch.WIND_FIRM) wherever it stood, a plant still going
 * up as if it ran, the works' own power as nothing, and ranked plants by the plan's present value with no slice of the load in it
 * (PW1-P's five gaps). PW3 reads the wind's, the sun's and the river's months at the city's latitude, the south on its own
 * season, credits a variable row with its peak hours, falling with its share, and plans as the study's prototype does
 * (spec-electricity 2.8): the reserve test first, its order the cheapest firm row for the slice of the load the gap lies in,
 * then the spread.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE PLACE AND THE SEASON. A city south of the equator has its months six on; a fixed rack yields less than a tracker,
 *      and less far from the equator than near it; each place's months average its year; the tracker is research E25's.
 *   2. THE CREDIT FALLS WITH THE SHARE: a variable row counts all of its peak hours with no variable row standing, a straight
 *      line down to CREDIT_FLOOR at CREDIT_HALF of the energy, and the floor past it; a fuelled row counts all of it.
 *   3. THE FIRM SUPPLY counts the works' own power, rises with a wind farm added (its posts never take more than it adds),
 *      and counts a plant still going up only from its opening.
 *   4. THE RESERVE TEST ORDERS THE SLICE'S CHEAPEST ROW: short at the base, coal; short at the peak alone, by more than a coal
 *      plant, a peaker.
 *   5. WIND ALONE NEVER PASSES THE TEST: a city knowing only wind and sun has no reserve order, however short; knowing a firm
 *      row too, the same city has.
 *   6. THE SPREAD orders wind where the fuelled rows standing run dear (diesel), and none where they run cheap (coal).
 *      A plant still going up counts in it from its own opening: coal that opens after the farm does takes none of its saving.
 *   7. THE GROUND: a coal plant's is planned past the one listing, and the automatic path assembles it over several offers;
 *      past ASSEMBLE_MOST offers none is planned.
 *   8. HOW OFTEN EACH FIRES: each suggestion counts once, on its own counter (BuildAdvice.RESERVE_ORDERS, SPREAD_ORDERS);
 *      the default run's counts are LongPlaytest's line, "the power planner (PW3)".
 */
public final class PowerPlanCheck {
    static final PrintStream out = System.out;
    static int fails;

    static void check(String label, boolean ok) {
        out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static boolean close(double a, double b, double tol) {
        return Math.abs(a - b) <= tol * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static final BuildAdvice.Measure POWER = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);

    static final String COAL = "Coal Power Plant", TURBINE = "Oil-Fired Turbine", DIESEL = "Diesel Station",
            WIND = "Wind Farm", SOLAR = "Solar Farm";

    /**
     * WorksFinanceCheck.town()'s founded 1900 town (its one coal plant, people at three-quarters of its homes, a flat history, the
     * builder's debt limit 0 and cash anyway) knowing only `menu` of the power plants, with the cash and ground to buy any order:
     * the fixture's lever (a 1900 city knows coal and hydro alone, PW2), every other building as the founding knows it.
     */
    static Game town(Path root, String name, String... menu) {
        Game g = WorksFinanceCheck.town(root, name);
        know(g, menu);
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + 1_000 * LandManager.SQ_FT_PER_KM2);
        return g;
    }

    static void know(Game g, String... menu) {
        Set<String> names = Set.of(menu);
        g.getBuildingManager().setTemplateKnown(t -> t.getCategory() != BuildingType.ELECTRICITY
                || Substations.isSubstation(t) || names.contains(t.getName()));
    }

    static BuildingsTemplate plant(Game g, String name) { return g.getBuildingManager().getTemplateByName(name); }

    /** The automatic path's opening for an order placed now: its slack, the town's flat history. */
    static BuildAdvice.Ahead now(Game g) { return BuildAdvice.opening(g, 0, g.getAutoBuilder().getSlack()); }

    /** The city's draw set so the planner's peak-hour demand at now(g) is `share` of its firm supply (demand is linear in the draw). */
    static void drawAt(Game g, double share) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double c1 = Math.max(1, u.getProduction()), c2 = 2 * c1;
        u.setWattsConsumption(c1);
        double[] one = BuildAdvice.supplyDemand(g, POWER, Map.of(), now(g));
        u.setWattsConsumption(c2);
        double[] two = BuildAdvice.supplyDemand(g, POWER, Map.of(), now(g));
        double perKw = (two[1] - one[1]) / (c2 - c1);
        u.setWattsConsumption(Math.max(0, c1 + (share * one[0] - one[1]) / perKw));
    }

    /** The slice of the load the gap at now(g) lies in (BuildAdvice.slice()). */
    static int slice(Game g) {
        double[] sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), now(g));
        return BuildAdvice.slice(sd[0], sd[1] / PowerDispatch.PLAN_PEAK_HOUR);
    }

    /** The automatic path's power suggestion, as AutoBuilder.step() asks it: the city's cash and ground, its slack, every plant. */
    static BuildAdvice.Suggestion suggest(Game g) {
        return BuildAdvice.suggestFor(g, null, POWER, g.getCash(), g.getLandManager().getAvailableSqFt(),
                g.getAutoBuilder().getSlack(), Set.of(), false, true);
    }

    static String named(BuildAdvice.Suggestion s) { return s == null ? "nothing" : s.count() + " " + s.template().getName(); }

    static void quietly(Runnable r) { EssentialWorksCheck.quietly(r); }

    /* ----- 1. the place and the season ----- */
    static void placeAndSeason(Path root) {
        out.println("--- 1. the south has its own season; the sun's year and months by place and rack; the tracker is E25's ---");
        boolean six = true, mirror = true;
        for (int m = 1; m <= 12; m++) {
            int turned = (m + 5) % 12 + 1;
            six &= PowerDispatch.northern(m, 40) == m && PowerDispatch.northern(m, -40) == turned;
            mirror &= close(PowerDispatch.season(m, -35), PowerDispatch.season(turned, 35), 1e-12)
                    && close(PowerDispatch.windMonth(m, -35), PowerDispatch.windMonth(turned, 35), 1e-12)
                    && close(PowerDispatch.hydroMonth(m, -35), PowerDispatch.hydroMonth(turned, 35), 1e-12)
                    && close(PowerDispatch.solarMonth(m, -35, false), PowerDispatch.solarMonth(turned, 35, false), 1e-12)
                    && close(PowerDispatch.solarMonth(m, -35, true), PowerDispatch.solarMonth(turned, 35, true), 1e-12);
        }
        check("a city south of the equator reads each calendar month six on; the equator and the north as they are", six
                && PowerDispatch.northern(1, 0) == 1 && PowerDispatch.northern(1, Double.NaN) == 1);
        check("...so its load, wind, water and sun in January are the north's in July at the same latitude", mirror);
        boolean below = true, means = true;
        for (double lat = 0; lat <= 70; lat += 5) below &= PowerDispatch.solarYear(lat, false) < PowerDispatch.solarYear(lat, true);
        for (double lat : new double[] {0, 12.5, 25, 47.5, 65, -33}) for (boolean tracked : new boolean[] {false, true}) {
            double sum = 0;
            for (int m = 1; m <= 12; m++) sum += PowerDispatch.solarMonth(m, lat, tracked);
            means &= close(sum / 12, PowerDispatch.solarYear(lat, tracked), 5e-3);
        }
        check("a fixed rack yields less than a tracker in every band, 0 to 70 degrees", below);
        check("a farm far from the equator yields less than one near the tropics, fixed or tracked (65 against 25 degrees)",
                PowerDispatch.solarYear(65, false) < PowerDispatch.solarYear(25, false)
                        && PowerDispatch.solarYear(65, true) < PowerDispatch.solarYear(25, true));
        check("each place's twelve months average its year, fixed and tracked (the tables' rounding)", means);
        check(String.format("summer's sun beats winter's at 45 degrees, each side of the equator in its own summer (June %.3f, December %.3f)",
                        PowerDispatch.solarMonth(6, 45, false), PowerDispatch.solarMonth(12, 45, false)),
                PowerDispatch.solarMonth(6, 45, false) > PowerDispatch.solarMonth(12, 45, false)
                        && PowerDispatch.solarMonth(12, -45, false) > PowerDispatch.solarMonth(6, -45, false));
        check("a generated world's farm (no latitude) is the tracked US fleet's year, its fixed rack SOLAR_US_FIXED of it",
                PowerDispatch.solarYear(Double.NaN, true) == 1
                        && PowerDispatch.solarYear(Double.NaN, false) == PowerDispatch.SOLAR_US_FIXED);
        double wind = 0;
        for (int m = 1; m <= 12; m++) wind += PowerDispatch.windMonth(m, 40);
        check("the wind's months average its year", close(wind / 12, 1, 1e-12));

        Game g = town(root, "place", COAL, WIND, SOLAR);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        boolean before = u.trackers();
        g.getResearch().restore(Map.of(), Map.of(), Map.of(), Map.of("E25", 1));
        check("a 1900 town's solar farms stand on fixed racks; once research finds E25 they track (" + before + ", " + u.trackers() + ")",
                !before && u.trackers());
    }

    /* ----- 2. the credit falls with the share ----- */
    static void credits() {
        out.println("--- 2. a variable row's credit falls with its share, a straight line to the floor; a fuelled row's is all of it ---");
        double floor = PowerDispatch.CREDIT_FLOOR, half = PowerDispatch.CREDIT_HALF;
        check("no variable row standing keeps all of its peak hours", PowerDispatch.creditFall(0) == 1);
        check("at CREDIT_HALF of the energy it keeps CREDIT_FLOOR, and the floor past it",
                close(PowerDispatch.creditFall(half), floor, 1e-12) && close(PowerDispatch.creditFall(1), floor, 1e-12));
        check("halfway there it keeps halfway between (a straight line)",
                close(PowerDispatch.creditFall(half / 2), (1 + floor) / 2, 1e-12));
        check("a share it cannot read (NaN) keeps all of it", PowerDispatch.creditFall(Double.NaN) == 1);
        check("a wind farm's credit is its peak hours' output times the fall",
                PowerDispatch.windCredit(0) == PowerDispatch.WIND_PEAK_CREDIT
                        && close(PowerDispatch.windCredit(.3), PowerDispatch.WIND_PEAK_CREDIT * PowerDispatch.creditFall(.3), 1e-12));
        check("a solar farm's at the US fleet's place, tracked, is its peak hours' output",
                close(PowerDispatch.solarCredit(Double.NaN, true, 0), PowerDispatch.SOLAR_PEAK_CREDIT, 1e-12));
        check("...more firm near the tropics than far from them, and the same either side of the equator",
                PowerDispatch.solarCredit(30, true, 0) > PowerDispatch.solarCredit(60, true, 0)
                        && close(PowerDispatch.solarCredit(-35, false, .2), PowerDispatch.solarCredit(35, false, .2), 1e-12));
    }

    /* ----- 3. the firm supply ----- */
    static void firmSupply(Path root) {
        out.println("--- 3. the firm supply: the works' own power, a wind farm added, a plant from its opening ---");
        Game g = town(root, "firm", COAL, WIND);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate coal = plant(g, COAL), wind = plant(g, WIND);
        check("a fuelled row's template credit is all of it; a wind farm's the windCredit at no share",
                coal.credit() == 1 && wind.credit() == PowerDispatch.windCredit(0));
        double f0 = BuildAdvice.supplyDemand(g, POWER, Map.of())[0], works = u.getWorksPower(), more = 50_000;
        u.setWorksPower(works + more);
        double f1 = BuildAdvice.supplyDemand(g, POWER, Map.of())[0];
        u.setWorksPower(works);
        check(String.format("the works' own power is firm supply, kW for kW (%.0f more of it, %.0f more firm)", more, f1 - f0),
                close(f1 - f0, more, 1e-9));
        double w1 = BuildAdvice.supplyDemand(g, POWER, Map.of(wind, 1L))[0];
        double share = BuildAdvice.variableShare(g, Map.of(wind, 1L));
        check(String.format("one wind farm added raises the firm supply, by no more than its credit at its share (%.0f kW; at most %.0f)",
                        w1 - f0, wind.getProduction1() * PowerDispatch.windCredit(share)),
                w1 > f0 && w1 - f0 <= wind.getProduction1() * PowerDispatch.windCredit(share) * (1 + 1e-9));
        quietly(() -> g.buildStack(coal, 1, false));
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, POWER);
        double first = g.getBuildingManager().siteUnitMonths(coal, 1, g.getBuildingOutputAtEveryPost());
        check("fixture: a coal plant is going up, its first unit quoted to open", BuildAdvice.units(site) == 1
                && Double.isFinite(first) && first > 1);
        Map<BuildingsTemplate, Long> openNow = WorksValue.openBy(g, site, 0), openLater = WorksValue.openBy(g, site, Math.ceil(first) + 1);
        check("a plant still going up is open by its opening and not before",
                BuildAdvice.units(openNow) == 0 && BuildAdvice.units(openLater) == 1);
        check("...so it adds nothing to today's firm supply, and its firm kW from its opening",
                close(BuildAdvice.supplyDemand(g, POWER, openNow)[0], f0, 1e-12)
                        && BuildAdvice.supplyDemand(g, POWER, openLater)[0] > f0);
    }

    /* ----- 4. the reserve test orders the slice's cheapest row ----- */
    static void reserveTest(Path root) {
        out.println("--- 4. the reserve test: short at the base, coal; short at the peak alone, a peaker ---");
        Game g = town(root, "reserve", COAL, TURBINE, DIESEL, WIND, SOLAR);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate coal = plant(g, COAL), turbine = plant(g, TURBINE), diesel = plant(g, DIESEL);
        drawAt(g, 4);
        check("fixture: power is short at the base, its firm supply under the base block's draw (slice " + slice(g) + ")",
                slice(g) == 0 && !BuildAdvice.maintained(g, POWER, Map.of(), now(g)));
        long r0 = BuildAdvice.RESERVE_ORDERS.get(), s0 = BuildAdvice.SPREAD_ORDERS.get();
        BuildAdvice.Suggestion base = suggest(g);
        check("short at the base the reserve test orders coal (" + named(base) + ")", base != null && base.template() == coal);
        check("...counted once as a reserve order and never as a spread",
                BuildAdvice.RESERVE_ORDERS.get() == r0 + 1 && BuildAdvice.SPREAD_ORDERS.get() == s0);

        // The peak alone, short by more than a coal plant's firm kW, so coal's size does not decide it: the works' own power
        // (firm, PW3's first gap) lifts the supply, and the draw puts the gap above the shoulder.
        u.setWorksPower(u.getWorksPower() + 4 * coal.getProduction1());
        drawAt(g, 1.3);
        double[] sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), now(g));
        check(String.format("fixture: power is short at the peak alone (slice %d), by %.0f kW, more than one coal plant's %.0f",
                        slice(g), sd[1] - sd[0], BuildAdvice.unit(g, POWER, coal)),
                slice(g) == 2 && sd[1] - sd[0] > BuildAdvice.unit(g, POWER, coal));
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        BuildAdvice.Suggestion peak = suggest(g);
        check("short at the peak the reserve test orders a peaker, not coal (" + named(peak) + ")",
                peak != null && (peak.template() == turbine || peak.template() == diesel));
        check("...counted once as a reserve order", BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);
        check("the slices' hours: the base all of the year's, the peak its peak block's",
                close(BuildAdvice.sliceHours(0), BuildAdvice.YEAR_HOURS, 1e-12)
                        && close(BuildAdvice.sliceHours(2), PowerDispatch.BLOCK_HOURS[PowerDispatch.PEAK] * BuildAdvice.YEAR_HOURS, 1e-12));
    }

    /* ----- 5. wind alone never passes the test ----- */
    static void windAlone(Path root) {
        out.println("--- 5. wind alone never passes the reserve test ---");
        Game g = town(root, "wind-alone", WIND, SOLAR);
        drawAt(g, 4);
        check("fixture: power is short", !BuildAdvice.maintained(g, POWER, Map.of(), now(g)));
        long r0 = BuildAdvice.RESERVE_ORDERS.get(), s0 = BuildAdvice.SPREAD_ORDERS.get();
        BuildAdvice.Suggestion s = suggest(g);
        check("a city knowing only wind and sun has no reserve order (" + named(s) + ")", BuildAdvice.RESERVE_ORDERS.get() == r0);
        check("...what it is offered is the spread's, counted there, or nothing",
                s == null ? BuildAdvice.SPREAD_ORDERS.get() == s0
                        : BuildAdvice.SPREAD_ORDERS.get() == s0 + 1 && (s.template().isWind() || s.template().isSolar()));
        know(g, WIND, SOLAR, DIESEL);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        BuildAdvice.Suggestion firm = suggest(g);
        check("knowing a firm row as well, the same city has a reserve order, the firm row (" + named(firm) + ")",
                firm != null && firm.template() == plant(g, DIESEL) && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);
        check("at CREDIT_HALF of the energy or more a wind farm counts CREDIT_FLOOR of its peak hours",
                close(PowerDispatch.windCredit(PowerDispatch.CREDIT_HALF), PowerDispatch.WIND_PEAK_CREDIT * PowerDispatch.CREDIT_FLOOR, 1e-12)
                        && close(PowerDispatch.windCredit(1), PowerDispatch.WIND_PEAK_CREDIT * PowerDispatch.CREDIT_FLOOR, 1e-12));
    }

    /* ----- 6. the spread ----- */
    /**
     * A town whose fuelled row `fuelled` stands to cover the reserve test at a wind farm's opening, knowing it and the wind farm, and
     * wired to carry it: the dispatch runs no more than the wires carry (Substations.costs()), and a founded town's are the founding
     * grid's alone, so without them the diesels stand idle and the farm displaces a sliver of coal.
     */
    static Game covered(Path root, String name, String fuelled) {
        Game g = town(root, name, fuelled, WIND);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate t = plant(g, fuelled), wind = plant(g, WIND);
        double draw = 3 * u.getProduction();
        u.setWattsConsumption(draw);
        BuildAdvice.Ahead at = BuildAdvice.opening(g, g.quoteBuild(wind, 1).months, g.getAutoBuilder().getSlack());
        long n = BuildAdvice.count(g, POWER, Map.of(), Map.of(t, 1L), at, BuildAdvice.MOST, true)[0];
        quietly(() -> g.buildStack(t, Math.max(1, n), true));
        SubstationCheck.wiresFor(g, BuildAdvice.supplyDemand(g, BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), Map.of(), at)[1]);
        g.getServicesManager().updateServices();
        u.setWattsConsumption(draw);
        return g;
    }

    static void spread(Path root) {
        out.println("--- 6. the spread: wind where the fuelled rows run dear, none where they run cheap ---");
        for (boolean dear : new boolean[] {true, false}) {
            String fuelled = dear ? DIESEL : COAL;
            Game g = covered(root, dear ? "spread-dear" : "spread-cheap", fuelled);
            BuildingsTemplate wind = plant(g, WIND);
            double lead = g.quoteBuild(wind, 1).months;
            check("fixture: " + fuelled + "s standing pass the reserve test at a wind farm's opening",
                    BuildAdvice.maintained(g, POWER, Map.of(), BuildAdvice.opening(g, lead, g.getAutoBuilder().getSlack())));
            BuildAdvice.Spread top = null;
            for (BuildAdvice.Spread s : BuildAdvice.spreads(g, g.getLandManager().getAvailableSqFt(), Set.of()))
                if (s.template() == wind) top = s;
            long r0 = BuildAdvice.RESERVE_ORDERS.get(), s0 = BuildAdvice.SPREAD_ORDERS.get();
            BuildAdvice.Suggestion s = suggest(g);
            String weighed = top == null ? "no wind weighed" : String.format("a farm saves %.0f a year against %.0f", top.saved(), top.cost());
            if (dear) {
                check("with diesel running, one more wind farm saves the year more than it costs (" + weighed + ")",
                        top != null && top.net() > 0);
                check("...so the spread orders wind (" + named(s) + "), counted once there and never as a reserve order",
                        s != null && s.template() == wind && BuildAdvice.SPREAD_ORDERS.get() == s0 + 1
                                && BuildAdvice.RESERVE_ORDERS.get() == r0);
            } else {
                check("with coal running, no wind farm saves the year what it costs (" + weighed + ")", top != null && !(top.net() > 0));
                check("...so nothing is ordered (" + named(s) + ") and nothing counted",
                        s == null && BuildAdvice.SPREAD_ORDERS.get() == s0 && BuildAdvice.RESERVE_ORDERS.get() == r0);
            }
        }
        spreadPending(root);
    }

    /** Construction Depots standing in the pending-plant fixture's town: their 400 points each lift the builders' output over the diesels' upkeep. */
    static final long DEPOTS = 100;

    /** The months of the builders' whole output the coal's ground owes in preparation (a foothill pad, the works): its first plant opens after the farm's 36. */
    static final double PREP_MONTHS = 60;

    /** The Wind Farm's row of the spread at `g`, or null when it is not weighed. */
    static BuildAdvice.Spread windRow(Game g) {
        BuildingsTemplate wind = plant(g, WIND);
        for (BuildAdvice.Spread s : BuildAdvice.spreads(g, g.getLandManager().getAvailableSqFt(), Set.of()))
            if (s.template() == wind) return s;
        return null;
    }

    /**
     * PW3 (REV-PW3 #4): a plant still going up counts in the spread from its own opening, as the reserve test reads it. The dear town
     * of the spread (diesel standing, wind's net above 0) with coal on site that opens after the farm does: counted open at once, that
     * coal would carry the energy the farm displaces and take its saving; counted from its opening, the farm opens first and saves what
     * it saves with nothing pending. The coal is as many plants as it takes for the whole site to leave the farm under half its saving.
     */
    static void spreadPending(Path root) {
        out.println("--- 6b. the spread counts a plant on site from its opening ---");
        Game g = covered(root, "spread-pending", DIESEL);
        know(g, DIESEL, WIND, COAL);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate wind = plant(g, WIND), coal = plant(g, COAL), depot = plant(g, BuildersFirstCheck.DEPOT);
        // ...the diesels' upkeep takes more of the builders' output than the town has, so no order is quoted to open (siteUnitMonths() is
        // NaN at no output): depots enough that the farm and the coal each have an opening.
        double draw = u.getConsumption();
        quietly(() -> g.buildStack(depot, DEPOTS, true));
        g.getServicesManager().updateServices();
        u.setWattsConsumption(draw);
        check(String.format("fixture: the builders have output to quote an opening (%d at every post)", g.getBuildingOutputAtEveryPost()),
                g.getBuildingOutputAtEveryPost() > 0);
        BuildAdvice.Spread bare = windRow(g);
        check("fixture: with nothing on site the farm saves the year more than it costs ("
                        + (bare == null ? "no wind weighed" : String.format("saves %.0f against %.0f", bare.saved(), bare.cost())) + ")",
                bare != null && bare.net() > 0);
        double nothing = bare == null ? 0 : bare.saved();
        long k = 1;
        for (; k <= 4096; k *= 2) {
            Map<BuildingsTemplate, Long> asked = Map.of(coal, k);
            if (u.yearRunning(asked) - u.yearRunning(BuildAdvice.plus(asked, wind, 1)) < nothing / 2) break;
        }
        final long plants = k;
        quietly(() -> g.buildStack(coal, plants, false));
        g.getBuildingManager().addPreparation(coal, PREP_MONTHS * g.getBuildingOutputAtEveryPost());
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, POWER);
        double first = g.getBuildingManager().siteUnitMonths(coal, 1, g.getBuildingOutputAtEveryPost()), lead = g.quoteBuild(wind, 1).months;
        check(String.format("fixture: %d coal plants are going up, the first to open in %.1f months, after the farm's %.1f", plants, first, lead),
                BuildAdvice.units(site) == plants && Double.isFinite(first) && first > lead);
        double counted = u.yearRunning(site) - u.yearRunning(BuildAdvice.plus(site, wind, 1));
        check(String.format("fixture: counted open at once, that coal leaves the farm %.0f a year, well below the %.0f it saves with nothing pending",
                counted, nothing), counted < nothing / 2);
        BuildAdvice.Spread row = windRow(g);
        check(String.format("a coal plant still being built past the farm's opening does not take the farm's saving: the spread's wind net is above 0 "
                        + "and its saving within 1%% of the town's with nothing pending (saved %.0f, cost %.0f; %.0f with nothing pending, %.0f with the coal counted open)",
                        row == null ? Double.NaN : row.saved(), row == null ? Double.NaN : row.cost(), nothing, counted),
                row != null && row.net() > 0 && close(row.saved(), nothing, .01));
    }

    /* ----- 7. the ground ----- */
    static void ground(Path root) {
        out.println("--- 7. a coal plant's ground, assembled past the one listing ---");
        Game g = town(root, "ground", COAL);
        LandManager land = g.getLandManager();
        land.setOwnedSqFt(land.getAllocatedSqFt());
        BuildingsTemplate coal = plant(g, COAL);
        // ...the fewest coal plants whose ground runs past the listing standing (a founded town's offers are larger than a new game's).
        long past = 1;
        for (AutoBuilder.Ground gr; past < 64 && (gr = AutoBuilder.groundFor(g, coal, past)) != null && !(gr.more() > 0); ) past++;
        AutoBuilder.Ground one = AutoBuilder.groundFor(g, coal, past);
        check("fixture: the town owns no ground spare, and its listing holds less than " + past + " coal plants' ("
                        + (one == null ? "no plan" : one.offers().size() + " offers, " + String.format("%.0f", one.more()) + " sq ft past them") + ")",
                one != null && one.more() > 0 && !one.covers());
        long far = 1;
        while (AutoBuilder.groundFor(g, coal, far) != null && far < 1 << 20) far *= 2;
        AutoBuilder.Ground none = AutoBuilder.groundFor(g, coal, far);
        check("past ASSEMBLE_MOST offers no ground is planned (" + far + " plants' ground)", none == null && far < 1 << 20);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        // ...and a shortage the reserve test meets with at least that many.
        BuildAdvice.Suggestion asked = null;
        for (double share = 4; share < 1e4; share *= 1.5) {
            drawAt(g, share);
            asked = suggest(g);
            if (asked != null && asked.template() == coal && asked.count() >= past) break;
        }
        check("fixture: the reserve test asks for at least " + past + " coal plants (" + named(asked) + ")",
                asked != null && asked.template() == coal && asked.count() >= past);
        AutoBuilder ab = g.getAutoBuilder();
        int before = ab.getAssembledOffers();
        ab.setOn(true, null);
        quietly(() -> ab.pass(g));
        AutoBuilder.Step step = EssentialWorksCheck.step(g, POWER);
        BuildingsStacks pending = g.getBuildingManager().getStack(coal);
        check("the automatic path orders coal on ground it assembles past the listing (" + step + "; "
                        + (ab.getAssembledOffers() - before) + " offers past it)",
                step != null && step.ordered() > 0 && coal.getName().equals(step.building())
                        && pending != null && pending.getUnderConstruction() > 0
                        && ab.getAssembledOffers() > before && ab.getAssembledOffers() - before <= AutoBuilder.ASSEMBLE_MOST);
        u.setWattsConsumption(u.getConsumption());
    }

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        Path root = TempDirs.create("powerplancheck");
        placeAndSeason(root);
        credits();
        firmSupply(root);
        reserveTest(root);
        windAlone(root);
        spread(root);
        ground(root);
        out.println("--- 8. how often each fires ---");
        out.printf("this harness's suggestions: reserve test %d, spread %d (the default run's: LongPlaytest, \"the power planner (PW3)\")%n",
                BuildAdvice.RESERVE_ORDERS.get(), BuildAdvice.SPREAD_ORDERS.get());
        check("the reserve test and the spread each fired here", BuildAdvice.RESERVE_ORDERS.get() > 0 && BuildAdvice.SPREAD_ORDERS.get() > 0);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
