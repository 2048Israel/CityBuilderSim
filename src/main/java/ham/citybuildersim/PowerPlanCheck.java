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
 * (spec-electricity 2.8): the reserve test first, its order the firm row the screening curve picks for the slice of the load the gap lies
 * in - unless a wind or solar farm, credited at its falling credit as planners credit them at their ELCC toward the reserve margin,
 * closes the gap for less in total net cost than that pick (the thermal capital it avoids; PJM's Net CONE) - then the spread.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE PLACE AND THE SEASON. A city south of the equator has its months six on; a fixed rack yields less than a tracker,
 *      and less far from the equator than near it; each place's months average its year; the tracker is research E25's.
 *   2. THE CREDIT FALLS WITH THE SHARE: a variable row counts all of its peak hours with no variable row standing, a straight
 *      line down to CREDIT_FLOOR at CREDIT_HALF of the energy, and the floor past it; a fuelled row counts all of it.
 *   3. THE FIRM SUPPLY counts the works' own power, rises with a wind farm added (its posts never take more than it adds),
 *      and counts a plant still going up only from its opening.
 *   4. THE RESERVE TEST ORDERS THE SLICE'S CHEAPEST FIRM ROW: short at the base, coal; short at the peak alone, by more than a
 *      coal plant, a peaker.
 *   5. WIND ALONE NEVER PASSES THE TEST: a city knowing only wind and sun may order farms, never more than keep their share of
 *      the energy at CREDIT_HALF, and with them added is still short; knowing a firm row too, the order closes it.
 *   6. THE SPREAD orders wind where the fuelled rows standing run dear (diesel), and none where they run cheap (coal).
 *      A plant still going up counts in it from its own opening: coal that opens after the farm does takes none of its saving.
 *      And the reserve test credits a farm: where the town runs dear and one Wind Farm's credited firm kW closes a gap under
 *      FIRST_WORK_SHARE of a Diesel Station's, no firm row is a candidate (the share, 2026-11-24) and the farm is the order (6a); where the gap
 *      is at or over the share, the farms that close it are weighed against the diesel and, their total net cost below the firm pick's,
 *      are the order (6a(ii)).
 *   7. THE GROUND: a coal plant's is planned past the one listing, and the automatic path assembles it over several offers;
 *      past ASSEMBLE_MOST offers none is planned. (7a) The quote is what the ground costs, offers past the listing at their own prices; (7b) an
 *      assembly that cannot cover the order buys nothing and borrows nothing; (7c) an order the builders would refuse moves no money (REV-PW3 #1, #2).
 *   8. A GAP IN THE RESERVE ALONE IS NOT A PLANT (2026-11-24): with the seasonal peak (PLAN_PEAK_SEASON: the peak block of the year's highest
 *      month) covered at a plant's opening, a gap to the peak hour (PLAN_PEAK_HOUR) orders it only when it fills FIRST_WORK_SHARE of the
 *      plant; a gap in the seasonal peak orders as ever (8a). And the month lacks power when its peak month's peak block goes unserved, which
 *      the city's own dispatch shows at three firm levels, not below the plan's peak hour (BuildAdvice.lacksNow(), 8b).
 *   9. HOW OFTEN EACH FIRES: each suggestion counts once, on its own counter (BuildAdvice.RESERVE_ORDERS, SPREAD_ORDERS);
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

    /** The city's draw set so the planner's peak-hour demand at `p` exceeds its firm supply by `gap` kW (demand is linear in the draw). */
    static void drawGap(Game g, BuildAdvice.Ahead p, double gap) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double c1 = Math.max(1, u.getProduction()), c2 = 2 * c1;
        u.setWattsConsumption(c1);
        double[] one = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        u.setWattsConsumption(c2);
        double[] two = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        double perKw = (two[1] - one[1]) / (c2 - c1);
        u.setWattsConsumption(Math.max(0, c1 + (one[0] + gap - one[1]) / perKw));
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

    /**
     * The reserve order as the planner ranks it, best first: each candidate's suggestion with the ones before it left out (suggestFor()'s
     * `skip`), its count, and its figures: a firm row's screening curve (BuildAdvice.screening()) and its total net cost
     * (BuildAdvice.reserveRank(), the farms' comparison with it), a farm's total net cost when it closes the gap and its net cost a firm kW
     * added when it does not. Counts as the suggestions it asks.
     */
    static String order(Game g) {
        Set<BuildingsTemplate> skip = new java.util.HashSet<>();
        StringBuilder b = new StringBuilder();
        double land = g.getLandManager().getAvailableSqFt();
        for (int i = 0; i < 8; i++) {
            BuildAdvice.Suggestion s = BuildAdvice.suggestFor(g, null, POWER, g.getCash(), land, g.getAutoBuilder().getSlack(), skip, false, true);
            if (s == null) break;
            BuildingsTemplate t = s.template();
            Map<BuildingsTemplate, Long> at = WorksValue.openBy(g, BuildAdvice.onSite(g, POWER), s.lead());
            boolean farm = t.isWind() || t.isSolar();
            String figure = farm
                    ? String.format(s.closes() ? "total net cost %.4g" : "net cost a firm kW %.4g",
                            BuildAdvice.reserveRank(g, t, s.count(), s.closes(), s.lead(), at, s.ahead(), land))
                    : String.format("screening %.4g, total net cost %.4g", BuildAdvice.screening(g, t, s.count(), at, s.ahead(), land),
                            BuildAdvice.reserveRank(g, t, s.count(), true, s.lead(), at, s.ahead(), land));
            b.append(i == 0 ? "" : ", then ").append(s.count()).append(' ').append(t.getName())
                    .append(" (").append(s.closes() ? "closes" : "does not close").append("; ").append(figure).append(')');
            skip.add(t);
        }
        return b.length() == 0 ? "no candidate" : b.toString();
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
        out.println("the order, short at the base: " + order(g));

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
        out.println("the order, short at the peak alone: " + order(g));
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
        long r0 = BuildAdvice.RESERVE_ORDERS.get();
        BuildAdvice.Suggestion s = suggest(g);
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, POWER);
        Map<BuildingsTemplate, Long> farms = s == null ? Map.of() : Map.of(s.template(), s.count());
        double share = BuildAdvice.variableShare(g, farms);
        check(String.format("a city knowing only wind and sun orders farms or nothing, within CREDIT_HALF of the energy (%s: the variable share %.3f, CREDIT_HALF %.2f)",
                        named(s), share, PowerDispatch.CREDIT_HALF),
                s == null ? BuildAdvice.RESERVE_ORDERS.get() == r0
                        : (s.template().isWind() || s.template().isSolar()) && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1
                                && share <= PowerDispatch.CREDIT_HALF * (1 + 1e-9));
        double[] after = BuildAdvice.supplyDemand(g, POWER, BuildAdvice.plus(site, farms), s == null ? now(g) : s.ahead());
        check(String.format("...and with those farms added the reserve test is still not passed (firm supply %.0f kW under the %.0f kW the test asks)", after[0], after[1]),
                after[0] < after[1] && (s == null || !s.closes()));
        know(g, WIND, SOLAR, DIESEL);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        BuildAdvice.Suggestion firm = suggest(g);
        Map<BuildingsTemplate, Long> closed = firm == null ? Map.of() : Map.of(firm.template(), firm.count());
        check("knowing a firm row as well, the same city has a reserve order that closes it, the firm row (" + named(firm) + ")",
                firm != null && firm.template() == plant(g, DIESEL) && firm.closes() && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1
                        && BuildAdvice.maintained(g, POWER, BuildAdvice.plus(site, closed), firm.ahead()));
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
        reserveCredit(root);
        reserveFarms(root);
        spreadPending(root);
    }

    /** What fraction of one credited Wind Farm's firm kW the dear town of 6a is short by at the reserve test. */
    static final double GAP_SHARE = 0.5;

    /** The firm kW one `t` adds at `p`'s opening to the site's, the rank's denominator (BuildAdvice.reserveRank()). */
    static double firmAdded(Game g, BuildingsTemplate t, Map<BuildingsTemplate, Long> at, BuildAdvice.Ahead p) {
        return BuildAdvice.supplyDemand(g, POWER, BuildAdvice.plus(at, t, 1), p)[0] - BuildAdvice.supplyDemand(g, POWER, at, p)[0];
    }

    /** (6a(ii)) The second dear town's gap at a farm's opening, as a multiple of FIRST_WORK_SHARE of one Diesel Station's firm kW: over the share, so the diesel is a candidate, and under what the farms that close it credit. */
    static final double OVER_SHARE = 1.1;

    /**
     * The dear town of the spread (diesels standing, which a farm displaces) made short at a farm's opening by `gap` kW, wired to carry the draw
     * that sets: a farm's credit and a diesel's fall with the draw, so `gap` is read again over four passes, each wiring the town for the draw.
     */
    static void settleGap(Game g, BuildAdvice.Ahead p, java.util.function.DoubleSupplier gap) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        for (int pass = 0; pass < 4; pass++) {
            drawGap(g, p, gap.getAsDouble());
            double draw = u.getConsumption();
            SubstationCheck.wiresFor(g, BuildAdvice.supplyDemand(g, BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), Map.of(), p)[1]);
            g.getServicesManager().updateServices();
            u.setWattsConsumption(draw);
        }
    }

    /**
     * PW3 (REV-PW3 #3): the reserve test credits a farm. The dear town of the spread is made short at a farm's opening by GAP_SHARE of one Wind
     * Farm's credited firm kW, less than the farm's credit and under FIRST_WORK_SHARE of a Diesel Station's firm kW: no firm row fills Jerus's HW1
     * share of its order (2026-11-24), so the screening curve's pick is none, and the reserve order is the one credited farm that closes the gap.
     */
    static void reserveCredit(Path root) {
        out.println("--- 6a. the reserve test credits a farm: no firm row fills FIRST_WORK_SHARE of its order, so the farm that closes the gap is the order ---");
        Game g = covered(root, "reserve-credit", DIESEL);
        BuildingsTemplate wind = plant(g, WIND), diesel = plant(g, DIESEL);
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, POWER);
        double lead = g.quoteBuild(wind, 1).months;
        BuildAdvice.Ahead p = BuildAdvice.opening(g, lead, g.getAutoBuilder().getSlack());
        // ...the gap is a share of the farm's credit, which itself falls with the draw: settle the two.
        settleGap(g, p, () -> GAP_SHARE * firmAdded(g, wind, site, p));
        double farm = firmAdded(g, wind, site, p), dsl = firmAdded(g, diesel, site, p), line = BuildAdvice.FIRST_WORK_SHARE * dsl;
        double[] sd = BuildAdvice.supplyDemand(g, POWER, site, p);
        check(String.format("fixture: the dear town is short at a farm's opening by %.0f kW, %.0f%% of one Wind Farm's %.0f credited firm kW and under FIRST_WORK_SHARE's %.0f kW of a Diesel Station's %.0f",
                        sd[1] - sd[0], 100 * (sd[1] - sd[0]) / farm, farm, line, dsl),
                sd[1] - sd[0] > 0 && sd[1] - sd[0] < farm && sd[1] - sd[0] < line);
        Map<BuildingsTemplate, Long> at = WorksValue.openBy(g, site, lead);
        long[] cw = BuildAdvice.count(g, POWER, at, Map.of(wind, 1L), p, BuildAdvice.reserveMost(g, wind, at), true);
        double land = g.getLandManager().getAvailableSqFt();
        // ...the screening curve's firm pick: the order with the farm left out.
        BuildAdvice.Suggestion pick = BuildAdvice.suggestFor(g, null, POWER, g.getCash(), land, g.getAutoBuilder().getSlack(), Set.of(wind), false, true);
        long r0 = BuildAdvice.RESERVE_ORDERS.get();
        BuildAdvice.Suggestion s = suggest(g);
        check(String.format("no firm row fills FIRST_WORK_SHARE of its order (the gap %.0f kW under the %.0f kW of a Diesel Station's %.0f), so the screening curve's pick is none (%s) "
                        + "and the reserve order is the one credited Wind Farm that closes it (%s)", sd[1] - sd[0], line, dsl, named(pick), named(s)),
                pick == null && cw[0] == 1 && cw[1] == 1 && s != null && s.template() == wind && s.count() == 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);
    }

    /**
     * 6a's original point, on a second town: the gap at a farm's opening is OVER_SHARE of FIRST_WORK_SHARE of a Diesel Station's firm kW, so a
     * diesel (the screening curve's firm pick) fills the share and closes it, and more than one farm's credit, so the farms that close it are
     * weighed against that pick: their total net cost (the year's capital and running less the diesel fuel they save) is the lower, and the order is the farms.
     */
    static void reserveFarms(Path root) {
        out.println("--- 6a(ii). the farms that close a gap a firm row fills the share of are weighed against the screening curve's firm pick ---");
        Game g = covered(root, "reserve-credit-farms", DIESEL);
        BuildingsTemplate wind = plant(g, WIND), diesel = plant(g, DIESEL);
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, POWER);
        double lead = g.quoteBuild(wind, 1).months;
        BuildAdvice.Ahead p = BuildAdvice.opening(g, lead, g.getAutoBuilder().getSlack());
        settleGap(g, p, () -> OVER_SHARE * BuildAdvice.FIRST_WORK_SHARE * firmAdded(g, diesel, site, p));
        Map<BuildingsTemplate, Long> at = WorksValue.openBy(g, site, lead);
        long[] cw = BuildAdvice.count(g, POWER, at, Map.of(wind, 1L), p, BuildAdvice.reserveMost(g, wind, at), true);
        double[] sd = BuildAdvice.supplyDemand(g, POWER, site, p);
        double gap = sd[1] - sd[0], one = firmAdded(g, wind, site, p), dsl = firmAdded(g, diesel, site, p), line = BuildAdvice.FIRST_WORK_SHARE * dsl;
        double farms = BuildAdvice.supplyDemand(g, POWER, BuildAdvice.plus(site, wind, cw[0]), p)[0] - sd[0];
        check(String.format("fixture: the dear town is short at a farm's opening by %.0f kW, at or over FIRST_WORK_SHARE's %.0f kW of a Diesel Station's %.0f, over one Wind Farm's %.0f credited firm kW "
                        + "and under the %.0f of the %d farms that close it", gap, line, dsl, one, farms, cw[0]),
                gap >= line && gap > one && cw[0] > 1 && cw[1] == 1 && gap < farms);
        double land = g.getLandManager().getAvailableSqFt();
        double rw = BuildAdvice.reserveRank(g, wind, cw[0], cw[1] == 1, lead, at, p, land);
        // ...the screening curve's firm pick: the order with the farms left out, and its total net cost by the same formula.
        BuildAdvice.Suggestion pick = BuildAdvice.suggestFor(g, null, POWER, g.getCash(), land, g.getAutoBuilder().getSlack(), Set.of(wind), false, true);
        double rd = pick == null ? Double.NaN : BuildAdvice.reserveRank(g, pick.template(), pick.count(), true, pick.lead(),
                WorksValue.openBy(g, site, pick.lead()), pick.ahead(), land);
        out.printf("total net cost of the order a year: %d Wind Farms %.4g (closes %b) against the screening curve's firm pick, %s %.4g (closes %b)%n",
                cw[0], rw, cw[1] == 1, named(pick), rd, pick != null && pick.closes());
        long r0 = BuildAdvice.RESERVE_ORDERS.get();
        BuildAdvice.Suggestion s = suggest(g);
        check(String.format("the %d Wind Farms close the gap a Diesel Station closes too, and by total net cost the farms are the lowest (%.4g against %.4g), so the reserve order is the farms, not a diesel (%s)",
                        cw[0], rw, rd, named(s)),
                cw[0] > 1 && cw[1] == 1 && pick != null && pick.closes() && pick.template() == diesel && rw < rd
                        && s != null && s.template() == wind && s.count() == cw[0] && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);
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

    /** Section 7's town (PW3, REV-PW3 #1, #2): owns no ground spare, `past` the fewest coal plants whose ground runs past its listing, and a reserve test asking for at least that many. */
    record GroundTown(Game g, BuildingsTemplate coal, long past, BuildAdvice.Suggestion asked) { }

    static GroundTown groundTown(Path root, String name) {
        Game g = town(root, name, COAL);
        LandManager land = g.getLandManager();
        land.setOwnedSqFt(land.getAllocatedSqFt());
        BuildingsTemplate coal = plant(g, COAL);
        long past = 1;
        for (AutoBuilder.Ground gr; past < 64 && (gr = AutoBuilder.groundFor(g, coal, past)) != null && !(gr.more() > 0); ) past++;
        BuildAdvice.Suggestion asked = null;
        for (double share = 4; share < 1e4; share *= 1.5) {
            drawAt(g, share);
            asked = suggest(g);
            if (asked != null && asked.template() == coal && asked.count() >= past) break;
        }
        return new GroundTown(g, coal, past, asked);
    }

    /** What these offers cost in cash as paying for them moves it (the land office's toggle): the listed ones' prices, as groundFor() prices the listing. */
    static double listedCash(Game g, java.util.List<LandParcel> offers) {
        java.util.List<Integer> ids = offers.stream().map(LandParcel::getId).toList();
        return g.isLandPaidFromVault() ? g.landTopUpLocal(ids) : g.landPriceLocal(ids);
    }

    /* ----- 7a. the quote is the cost ----- */
    static void quoteIsCost(Path root) {
        out.println("--- 7a. dearer replenishment: the ground past the listing is quoted at what it costs ---");
        GroundTown f = groundTown(root, "ground-quote");
        Game g = f.g();
        AutoBuilder ab = g.getAutoBuilder();
        BuildAdvice.Suggestion s = f.asked();
        check("fixture: the reserve test asks for at least " + f.past() + " coal plants (" + named(s) + ")",
                s != null && s.template() == f.coal() && s.count() >= f.past());
        if (s == null || s.template() != f.coal()) return;
        // ...each count's ground as the pass weighs it, and what the same ground would cost at the office's rate a square foot.
        double office = g.getLandManager().getOfficePricePerSqFt();
        java.util.List<Double> quote = new java.util.ArrayList<>(), atRate = new java.util.ArrayList<>();
        quote.add(0.0);
        atRate.add(0.0);
        for (long k = 1; k <= s.count(); k++) {
            AutoBuilder.Ground gr = AutoBuilder.groundFor(g, f.coal(), k);
            if (gr == null) break;
            quote.add(gr.cash());
            atRate.add(listedCash(g, gr.offers()) + gr.more() * office);
        }
        int n = quote.size() - 1;
        check("fixture: ground is quoted for " + n + " coal plants, at least " + f.past(), n >= f.past());
        if (n < f.past()) return;
        // ...the cash set to cover the order and exactly the quote's ground, no more: what the cash protects is the reserve (AutoBuilder.reserve()).
        double floor = AutoBuilder.reserve(g);
        g.setCashForTest(floor + (AutoBuilder.total(g, s, n) + quote.get(n)) * (1 + 1e-9));
        double spent = ab.getLandSpent();
        int offers = ab.getLandOffers(), past = ab.getAssembledOffers();
        boolean placed = ab.placeForTest(g, POWER, s, n);
        double paid = ab.getLandSpent() - spent;
        check("fixture: the order of " + n + " coal plants is placed on ground assembled past the listing (placed " + placed + "; "
                + (ab.getLandOffers() - offers) + " offers bought, " + (ab.getAssembledOffers() - past) + " of them past the listing)",
                ab.getAssembledOffers() > past);
        check(String.format("fixture: the offers past the listing cost more than the office's rate for their square feet (paid %.0f; %.0f at the rate)",
                paid, atRate.get(n)), paid > atRate.get(n) * (1 + 1e-3));
        check(String.format("the quote is what the ground costs: the assembly pays no more than groundFor() quoted, and the cash protected stays untouched "
                + "(placed %b; paid %.0f against %.0f quoted; cash %.0f against %.0f protected)", placed, paid, quote.get(n), g.getCash(), floor),
                placed && close(paid, quote.get(n), 1e-6) && g.getCash() >= floor - 1e-6 * Math.max(1, floor));
    }

    /**
     * A town whose world has no ground left to list (one purchase of all of it, on the books as any purchase is) and three offers standing:
     * the ground the order needs past them is none that can be had.
     */
    static GroundTown shortTown(Path root, String name) {
        GroundTown f = groundTown(root, name);
        Game g = f.g();
        LandManager land = g.getLandManager();
        LandMarket market = land.getMarket();
        market.putOffers(market.getListing().stream().filter(LandMarket::bareGround).limit(3).toList());
        land.land().extend(new LandParcel(-7, 0, 0, land.land().level(), 0, 0, World.SIDE, World.SIDE, null, null, null, 0, g.getMonth()),
                g.getMonth(), 0);
        return f;
    }

    /* ----- 7b. an assembly that cannot cover ----- */
    static void cannotCover(Path root) {
        out.println("--- 7b. an assembly that cannot cover the order ---");
        GroundTown f = shortTown(root, "ground-short");
        Game g = f.g();
        AutoBuilder ab = g.getAutoBuilder();
        LandMarket market = g.getLandManager().getMarket();
        double standing = 0;
        for (LandParcel p : market.getListing()) standing += p.getSizeSqFt();
        double lack = AutoBuilder.lacks(g, f.coal(), 1);
        check(String.format("fixture: the %d offers standing hold %.0f sq ft, less than the %.0f one coal plant lacks, and no place can list another",
                market.getListing().size(), standing, lack), standing < lack && lack > 0);
        BuildAdvice.Suggestion s = suggest(g);
        check("fixture: the reserve test asks for coal (" + named(s) + ")", s != null && s.template() == f.coal());
        if (s == null || s.template() != f.coal()) return;
        double[][] before = market.getOffersState();
        double owned = g.getLandManager().getOwnedSqFt(), cash = g.getCash(), borrowed = ab.getBorrowed();
        int offers = ab.getLandOffers(), bonds = ab.getBonds(), debts = g.getDebtManager().getDebt().size();
        boolean placed = ab.placeForTest(g, POWER, s, 1);
        check(String.format("an assembly that cannot cover the order buys nothing, borrows nothing and leaves the market as it was "
                + "(placed %b; %d offers bought, %d bonds sold, %d debts raised, offers state %s)", placed, ab.getLandOffers() - offers, ab.getBonds() - bonds,
                g.getDebtManager().getDebt().size() - debts, java.util.Arrays.deepEquals(before, market.getOffersState()) ? "unchanged" : "changed"),
                !placed && ab.getLandOffers() == offers && g.getLandManager().getOwnedSqFt() == owned && ab.getBonds() == bonds && ab.getBorrowed() == borrowed
                && g.getDebtManager().getDebt().size() == debts && g.getCash() == cash && java.util.Arrays.deepEquals(before, market.getOffersState()));
    }

    /* ----- 7c. an order the builders would refuse ----- */
    static void refused(Path root) {
        out.println("--- 7c. an order the builders would refuse ---");
        GroundTown f = groundTown(root, "ground-refused");
        Game g = f.g();
        AutoBuilder ab = g.getAutoBuilder();
        BuildAdvice.Suggestion s = f.asked();
        check("fixture: the reserve test asks for coal (" + named(s) + ")", s != null && s.template() == f.coal());
        if (s == null || s.template() != f.coal()) return;
        AutoBuilder.Ground ground = AutoBuilder.groundFor(g, f.coal(), 1);
        check("fixture: ground can be had for one coal plant, past what the town owns spare", ground != null && AutoBuilder.lacks(g, f.coal(), 1) > 0);
        // ...and the plant not known any more: the builders refuse it before land and money (Game.buildStack(): NOT_KNOWN).
        know(g);
        check("fixture: the builders refuse the plant for no ground and no money (" + g.buildStack(f.coal(), 1, false, Landmasses.RULE) + ")",
                g.buildStack(f.coal(), 1, false, Landmasses.RULE) == Game.BuildResult.NOT_KNOWN);
        double owned = g.getLandManager().getOwnedSqFt(), cash = g.getCash(), borrowed = ab.getBorrowed();
        int offers = ab.getLandOffers(), bonds = ab.getBonds();
        double[][] before = g.getLandManager().getMarket().getOffersState();
        boolean placed = ab.placeForTest(g, POWER, s, 1);
        check(String.format("an order the builders would refuse moves no money: no ground bought and no bond sold (placed %b; %d offers bought, "
                + "%.0f sq ft owned against %.0f, %d bonds sold, cash %.0f against %.0f)", placed, ab.getLandOffers() - offers,
                g.getLandManager().getOwnedSqFt(), owned, ab.getBonds() - bonds, g.getCash(), cash),
                !placed && ab.getLandOffers() == offers && g.getLandManager().getOwnedSqFt() == owned && ab.getBonds() == bonds
                && ab.getBorrowed() == borrowed && g.getCash() == cash
                && java.util.Arrays.deepEquals(before, g.getLandManager().getMarket().getOffersState()));
    }

    /* ----- 8. a gap in the reserve alone is not a plant; the month lacks power when its peak month's peak block goes unserved ----- */

    /** The year's highest month's peak block over the peak hour's draw: the firm supply the month's dispatch needs at its worst over the supply the plan does (PowerDispatch.PLAN_PEAK_SEASON over PLAN_PEAK_HOUR). */
    static final double SEASONAL = PowerDispatch.PLAN_PEAK_SEASON / PowerDispatch.PLAN_PEAK_HOUR;

    /** The city's draw set so the peak hour's demand at `p` is `ratio` of its firm supply (the gap that makes it so is drawGap()'s). */
    static void drawRatio(Game g, BuildAdvice.Ahead p, double ratio) {
        drawGap(g, p, BuildAdvice.supplyDemand(g, POWER, Map.of(), p)[0] * (ratio - 1));
    }

    /** The firm supply over the city's draw at `p`: the plan's peak hour is PLAN_PEAK_HOUR of the draw, the month's peak block PLAN_PEAK, in the year's highest month PLAN_PEAK_SEASON. */
    static double supplyOverDraw(Game g, BuildAdvice.Ahead p) {
        double[] sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        return sd[0] / (sd[1] / PowerDispatch.PLAN_PEAK_HOUR);
    }

    static void calmPeak(Path root) {
        out.println("--- 8a. a reserve gap with the seasonal peak covered at the plant's opening orders it only when it fills FIRST_WORK_SHARE of the plant ---");
        Game g = town(root, "calm-peak", COAL);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate coal = plant(g, COAL);
        double unit = BuildAdvice.unit(g, POWER, coal), share = BuildAdvice.FIRST_WORK_SHARE, works = u.getWorksPower();
        // ...the firm supply lifted by the works' own power (firm, PW3's first gap) so a gap of a share of one plant lies inside the seasonal peak's cover.
        u.setWorksPower(works + 4 * unit);
        double lead = g.quoteBuild(coal, 1).months;
        BuildAdvice.Ahead p = BuildAdvice.opening(g, lead, g.getAutoBuilder().getSlack());
        long r0, s0;
        BuildAdvice.Suggestion s;
        double[] sd;

        drawGap(g, p, .5 * share * unit);
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        out.println("the firm supply " + Math.round(sd[0]) + " kW, the peak hour's draw " + Math.round(sd[1]) + " kW, the seasonal peak's " + Math.round(sd[1] * SEASONAL)
                + " kW, one coal plant " + Math.round(unit) + " kW");
        check(String.format("fixture: coal is the only firm row known; at its opening (%.0f months) the firm supply %.0f kW covers the seasonal peak's %.0f and is short of the peak hour's %.0f "
                        + "by %.0f kW, %.0f%% of one coal plant's %.0f, under FIRST_WORK_SHARE's %.0f%%",
                        lead, sd[0], sd[1] * SEASONAL, sd[1], sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit, unit, 100 * share),
                sd[0] >= sd[1] * SEASONAL && sd[1] > sd[0] && sd[1] - sd[0] < share * unit && BuildAdvice.units(BuildAdvice.onSite(g, POWER)) == 0);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s0 = BuildAdvice.SPREAD_ORDERS.get();
        s = suggest(g);
        check("the peak-hour gap under the share, the seasonal peak covered: the power suggestion orders nothing (" + named(s) + ")",
                s == null && BuildAdvice.RESERVE_ORDERS.get() == r0 && BuildAdvice.SPREAD_ORDERS.get() == s0);

        // ...the same city, the gap at the share and a little over: it fills the share, so the plant is ordered.
        drawGap(g, p, 1.05 * share * unit);
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        check(String.format("fixture: the same city, short of the peak hour by %.0f kW, %.0f%% of one coal plant's, the seasonal peak still covered (%.0f of %.0f kW)",
                        sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit, sd[0], sd[1] * SEASONAL),
                sd[0] >= sd[1] * SEASONAL && sd[1] - sd[0] >= share * unit);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("a reserve gap that fills FIRST_WORK_SHARE of the plant orders it (" + named(s) + ")",
                s != null && s.template() == coal && s.count() == 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);

        // ...and the seasonal peak itself short at the opening: ordered as ever, whatever the share.
        drawRatio(g, p, 1 / SEASONAL * 1.2);
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        check(String.format("fixture: the same city with the seasonal peak short at the opening (the firm supply %.0f kW under the seasonal peak's %.0f)", sd[0], sd[1] * SEASONAL),
                sd[0] < sd[1] * SEASONAL);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("with the seasonal peak short at the plant's opening it orders (" + named(s) + ")",
                s != null && s.template() == coal && s.count() >= 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);

        // ...the first condition alone: the works' power taken off again, one coal plant and the founding grid stand, and the first fixture's gap
        // (under the share) leaves the seasonal peak short, as the month's dispatch would in its highest month: it orders.
        u.setWorksPower(works);
        drawGap(g, p, .5 * share * unit);
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        check(String.format("fixture: the supply %.0f kW, short of the peak hour by %.0f kW, %.0f%% of one coal plant's and under FIRST_WORK_SHARE's, and under the seasonal peak's %.0f",
                        sd[0], sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit, sd[1] * SEASONAL),
                sd[0] < sd[1] * SEASONAL && sd[1] - sd[0] < share * unit);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("a gap under the share with the seasonal peak short at the plant's opening orders it (" + named(s) + ")",
                s != null && s.template() == coal && s.count() == 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);
    }

    /** The calendar month of the year's highest load at this city's latitude, as its dispatch reads it (PowerDispatch.season(month, latitude)). */
    static int peakCalendarMonth(UtilitiesHandler u) {
        int best = 1;
        for (int m = 2; m <= 12; m++) if (PowerDispatch.season(m, u.latitude()) > PowerDispatch.season(best, u.latitude())) best = m;
        return best;
    }

    /**
     * The town's month dispatched by the city's own update (UtilitiesHandler.updateEnergyRatio()) in its peak month, the draw set so its firm
     * supply is `have` of it and the wires carry whatever the dispatch asks: the peak block's load asked and what it left unserved, kW.
     */
    static double[] peakBlock(Game g, double have) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        int month = peakCalendarMonth(u);
        u.setCalendarMonth(() -> month);
        drawRatio(g, BuildAdvice.NOW, PowerDispatch.PLAN_PEAK_HOUR / have);
        double draw = u.getConsumption();
        SubstationCheck.wiresFor(g, 1.05 * draw * PowerDispatch.PLAN_PEAK_SEASON);
        g.getServicesManager().updateServices();
        u.setWattsConsumption(draw);
        u.updateEnergyRatio();
        PowerDispatch.Month m = u.getLatestMonth();
        int s = 2 * PowerDispatch.PEAK;
        return new double[] {m.load[s] + m.load[s + 1], m.unserved[s] + m.unserved[s + 1]};
    }

    static void lacksNow(Path root) {
        out.println("--- 8b. the month lacks power when its peak month's peak block goes unserved, shown by the dispatch ---");
        Game g = town(root, "lacks-now", COAL);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        String peak = "month " + peakCalendarMonth(u) + ", season " + PowerDispatch.season(peakCalendarMonth(u), u.latitude());
        check(String.format("the dials: PLAN_PEAK_SEASON %.3f is PLAN_PEAK %.3f times the highest season, between PLAN_PEAK and PLAN_PEAK_HOUR %.3f",
                        PowerDispatch.PLAN_PEAK_SEASON, PowerDispatch.PLAN_PEAK, PowerDispatch.PLAN_PEAK_HOUR),
                close(PowerDispatch.PLAN_PEAK_SEASON, PowerDispatch.PLAN_PEAK * PowerDispatch.season(peakCalendarMonth(u), u.latitude()), 1e-12)
                        && PowerDispatch.PLAN_PEAK < PowerDispatch.PLAN_PEAK_SEASON && PowerDispatch.PLAN_PEAK_SEASON < PowerDispatch.PLAN_PEAK_HOUR);
        // The firm supply as a multiple of the draw (the mean the month is asked for), each level dispatched in the peak month by the city's own update.
        double[] levels = {1.1 * PowerDispatch.PLAN_PEAK_HOUR,
                (PowerDispatch.PLAN_PEAK_SEASON + PowerDispatch.PLAN_PEAK_HOUR) / 2,
                (PowerDispatch.PLAN_PEAK + PowerDispatch.PLAN_PEAK_SEASON) / 2,
                .9 * PowerDispatch.PLAN_PEAK};
        String[] where = {"above PLAN_PEAK_HOUR", "between PLAN_PEAK_SEASON and PLAN_PEAK_HOUR", "between PLAN_PEAK and PLAN_PEAK_SEASON", "below PLAN_PEAK"};
        for (int i = 0; i < levels.length; i++) {
            double[] block = peakBlock(g, levels[i]);
            double have = supplyOverDraw(g, BuildAdvice.NOW);
            boolean short_ = block[1] > 0, lacks = BuildAdvice.lacksNow(g, POWER), planned = BuildAdvice.maintained(g, POWER, Map.of(), BuildAdvice.NOW);
            check(String.format("firm supply %.3f of the draw, %s: in the peak month (%s) its peak block asks %.0f kW and %.0f is left unserved, so lacksNow() is %b "
                            + "(maintained(NOW) %b)", have, where[i], peak, block[0], block[1], lacks, planned),
                    close(have, levels[i], 1e-6) && lacks == short_ && short_ == (i >= 2) && planned == (i == 0));
        }
        u.setWattsConsumption(0);
        check("a city drawing nothing lacks no power", !BuildAdvice.lacksNow(g, POWER));
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
        quoteIsCost(root);
        cannotCover(root);
        refused(root);
        calmPeak(root);
        lacksNow(root);
        out.println("--- 9. how often each fires ---");
        out.printf("this harness's suggestions: reserve test %d, spread %d (the default run's: LongPlaytest, \"the power planner (PW3)\")%n",
                BuildAdvice.RESERVE_ORDERS.get(), BuildAdvice.SPREAD_ORDERS.get());
        check("the reserve test and the spread each fired here", BuildAdvice.RESERVE_ORDERS.get() > 0 && BuildAdvice.SPREAD_ORDERS.get() > 0);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
