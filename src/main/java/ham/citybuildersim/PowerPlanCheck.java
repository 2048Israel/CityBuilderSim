package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;

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
 *      plant; so does a gap in the seasonal peak that only the opening's slack and horizon project (REV-PW3d); a gap in the seasonal peak
 *      at the plant's own completion, its lead and the town's growth over it, orders as ever, the month short or not (REV-PW3e) (8a). And
 *      the month lacks power when its peak month's peak block goes unserved, which
 *      the city's own dispatch shows at three firm levels, not below the plan's peak hour (BuildAdvice.lacksNow(), 8b); and the auto-builder's holds read the same "short now", through a real pass, in both directions (AutoBuilder.crewLimit() and pendingDelay(), 8b (b)).
 *   9. A WORKS UNDER CONSTRUCTION IS IN THE FORECAST (PF1, 2026-11-25): a works on site counts at its rated draw (NewUnit) from the month its queue
 *      opens it (WorksValue.pendingPowerLoad()), and the plan orders the plant to be on line by then; the same town without it orders nothing. A utility
 *      plans a large load from its signed request on its ramp schedule (ERCOT's 2025 Long-Term Load Forecast adds a large load with a signed agreement
 *      and a financial commitment on its ramp schedule, as a 2023 Texas law requires); here a works on site is that request. REV-PW3g (item 6) read
 *      LongPlaytest's m116-m150 power served at 0.57-0.61 as a forecast blind to such a works; PF1's trace found it already carried, and this pins it.
 *   9a. A SMALL TOWN IN 1906 orders small steam stations, through the ordinary dated catalogue and automatic order path;
 *       with that row absent the same town orders the 86 MW Oil-Fired Plant (PW9). The station's lead closes the actual gap before diesel's year.
 *   9b. THE JOBLESS FOUNDING GRID does not lose power when the first small station hires: its endowment is fixed, the new
 *       plant's output is staffed. Preview and native dispatch agree at scarce staff; zero/full staff, water/substation posts and K endowments too.
 *   9c. PENDING POWER AND LOAD share their evaluation date: covered commissioning fronts order nothing, an interim reserve bridge
 *       remains, a real seasonal gap is served, and a plant beyond the horizon counts nothing early, whatever the first-unit promise.
 *  10. HOW OFTEN EACH FIRES: each suggestion counts once, on its own counter (BuildAdvice.RESERVE_ORDERS, SPREAD_ORDERS);
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
            Map<BuildingsTemplate, Long> at = WorksValue.openBy(g, BuildAdvice.onSite(g, POWER), s.ahead().months());
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

    /** The month's firm supply over the seasonal peak's draw, just over it (the month does not lack) and just under it (it does), in 8a's last two fixtures. */
    static final double NOW_OVER = 1.02, NOW_UNDER = .99;

    /** The town's growth over the coal plant's lead in 8a's growing town (BusinessInvestment.growthFactor() at the lead, less one), to a hundredth (the history is whole people). */
    static final double GROWTH_AT_LEAD = .30;

    /** A plant's own completion: its lead and the town's growth over it, no horizon past it and no slack (BuildAdvice.suggestFor()'s share rule). */
    static BuildAdvice.Ahead completion(Game g, double lead) {
        return new BuildAdvice.Ahead(lead, g.getBusinessInvestment().growthFactor(lead), 0);
    }

    /** The firm supply over the city's draw at `p`: the plan's peak hour is PLAN_PEAK_HOUR of the draw, the month's peak block PLAN_PEAK, in the year's highest month PLAN_PEAK_SEASON. */
    static double supplyOverDraw(Game g, BuildAdvice.Ahead p) {
        double[] sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        return sd[0] / (sd[1] / PowerDispatch.PLAN_PEAK_HOUR);
    }

    /** The town's history set so it grows GROWTH_AT_LEAD over `lead` months (BusinessInvestment.growthFactor(), the businesses' trend), from half its reachable people. */
    static void grow(Game g, double lead) {
        BusinessInvestment bi = g.getBusinessInvestment();
        double perMonth = GROWTH_AT_LEAD / lead;
        long last = Math.max(1, Math.round(bi.reachablePopulation() / 2));
        java.util.List<Long> history = new java.util.ArrayList<>();
        for (int month = 0; month <= 12; month++) history.add(Math.round(last * (1 - perMonth * (12 - month))));
        bi.restorePopulationHistory(history);
    }

    /*
     * 8a's table (REV-PW3f, 2026-11-24): every case the last four reviews found, the fixture that causes it, and the form of the rule
     * it is red under (each shown red, docs/queue/PW3-notes.md section 2, the need date).
     *
     *   the case                                         its fixture                                          red under
     *   a reserve gap under the share (★R)               (1) 15% of one plant, the seasonal peak covered      no share: any reserve gap orders
     *   the 86 MW plant for a 3 MW town (REV-PW3d)       (i) a seasonal gap the slack alone projects;         the share waived for a seasonal
     *                                                    (iv) one only the horizon past the completion does   gap at the opening
     *   the seasonal peak (REV-PW3c)                     (ii) short in the month and at the completion,       both lines at PLAN_PEAK
     *                                                    over the year's mean peak block
     *   the shortage waited out for a lead (REV-PW3e)    (iii) covered now, short at the completion           the share waived only in a month that lacks
     *   two firm rows of different leads (REV-PW3f)      (v) short at coal's completion, covered at the       each row tested at its own lead alone,
     *                                                    diesel's, the diesel the cheaper: nothing            no need date (BuildAdvice.timely())
     *                                                    (vii) the same with coal the cheaper: coal late,     the late row weighed on its cost alone
     *                                                    no candidate, nothing                                (timely()'s first clause)
     *                                                    (vi) short at the diesel's own completion: the       every due row held (a control: green
     *                                                    diesel, its last on-time month                       without the need date)
     *                                                    (v') short at coal's completion, covered a month     the late clause alone (timely()'s cost
     *                                                    before: coal on time, the diesel the cheaper:        clause gone)
     *                                                    nothing; (v'') coal the cheaper: coal (a control)
     *   a faster row the calendar brings (REV-PW3f,      (viii) (v) with the diesel off the menu, brought     only the rows known now weighed
     *   LongPlaytest's 1908 Oil-Fired Plant)             12 months on, inside coal's lead: nothing            (BuildingManager.isTemplateKnownIn())
     *                                                    (ix) brought past coal's lead: coal (a control)
     */
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

        // ...and the seasonal peak itself short at the opening, by a gap past the share: ordered as ever (it needs no waiver; the waiver's
        // fixtures are (ii) and (iii) below).
        drawRatio(g, p, 1 / SEASONAL * 1.2);
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        check(String.format("fixture: the same city with the seasonal peak short at the opening (the firm supply %.0f kW under the seasonal peak's %.0f), "
                                + "the peak hour short by %.0f kW, %.0f%% of one coal plant's, past FIRST_WORK_SHARE's", sd[0], sd[1] * SEASONAL, sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit),
                sd[0] < sd[1] * SEASONAL && sd[1] - sd[0] >= share * unit);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("a gap past the share with the seasonal peak short at the plant's opening orders (" + named(s) + ")",
                s != null && s.template() == coal && s.count() >= 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);

        // ...the works' power taken off again, one coal plant and the founding grid stand, the town's history flat. REV-PW3d's P1 4
        // (2026-11-24): a gap under the share in the seasonal peak that only the opening's slack projects, the peak covered now and at the
        // plant's own completion, orders nothing: it waits for a gap that fills the share. With the share waived for any seasonal gap at the
        // opening, all 16 of the ensemble's ZERO_HOT towns, drawing some 3,000 kW on a 10,000 kW founding grid, were sold an 86,181 kW
        // Oil-Fired Plant at m72-81 on credit.
        u.setWorksPower(works);
        drawRatio(g, BuildAdvice.NOW, 1 / (SEASONAL * NOW_OVER));
        BuildAdvice.Ahead done = completion(g, lead);
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        double[] sdNow = BuildAdvice.supplyDemand(g, POWER, Map.of(), BuildAdvice.NOW), sdDone = BuildAdvice.supplyDemand(g, POWER, Map.of(), done);
        check(String.format("fixture: the seasonal peak covered now (lacksNow() false: the firm supply %.0f kW against the seasonal peak's %.0f) and at the plant's completion "
                                + "(%.0f months, growth %.4f: %.0f), and short at the opening, the slack's (%.0f), the peak hour short by %.0f kW, %.0f%% of one coal plant's, under FIRST_WORK_SHARE's",
                        sdNow[0], sdNow[1] * SEASONAL, lead, done.k(), sdDone[1] * SEASONAL, sd[1] * SEASONAL, sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit),
                !BuildAdvice.lacksNow(g, POWER) && sdDone[0] >= sdDone[1] * SEASONAL && sd[0] < sd[1] * SEASONAL && sd[1] > sd[0]
                        && sd[1] - sd[0] < share * unit);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s0 = BuildAdvice.SPREAD_ORDERS.get();
        s = suggest(g);
        check("a seasonal gap under the share that only the opening's slack projects: the power suggestion orders nothing (" + named(s) + ")",
                s == null && BuildAdvice.RESERVE_ORDERS.get() == r0 && BuildAdvice.SPREAD_ORDERS.get() == s0);

        // ...and the month itself short of the seasonal peak, though covered at the year's mean peak block (PLAN_PEAK): with the history flat
        // it is short at the completion too, and orders whatever the share (red if the line were PLAN_PEAK).
        drawRatio(g, BuildAdvice.NOW, 1 / (SEASONAL * NOW_UNDER));
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
        sdNow = BuildAdvice.supplyDemand(g, POWER, Map.of(), BuildAdvice.NOW);
        sdDone = BuildAdvice.supplyDemand(g, POWER, Map.of(), done);
        double mean = PowerDispatch.PLAN_PEAK / PowerDispatch.PLAN_PEAK_HOUR;
        check(String.format("fixture: the month lacks (lacksNow() true: the firm supply %.0f kW under the seasonal peak's %.0f now, over the year's mean peak block's %.0f), "
                                + "and at the completion (%.0f against %.0f, over the mean block's %.0f); at the opening short of the peak hour by %.0f kW, %.0f%% of one coal plant's, "
                                + "under FIRST_WORK_SHARE's",
                        sdNow[0], sdNow[1] * SEASONAL, sdNow[1] * mean, sdDone[0], sdDone[1] * SEASONAL, sdDone[1] * mean, sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit),
                BuildAdvice.lacksNow(g, POWER) && sdNow[0] >= sdNow[1] * mean && sdDone[0] >= sdDone[1] * mean
                        && sdDone[0] < sdDone[1] * SEASONAL && sd[1] - sd[0] < share * unit);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("a gap under the share with the seasonal peak short in the month and at the plant's completion orders it (" + named(s) + ")",
                s != null && s.template() == coal && s.count() == 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);

        // REV-PW3e's P1 (2026-11-24): a utility orders for the peak projected at the unit's in-service date, not once the month is short. The
        // town grows (the businesses' trend, GROWTH_AT_LEAD over the coal plant's lead) and the slack is 0, so the opening's projection past
        // the completion is the horizon's growth alone. Waived only in a month already short, the share held every diesel a whole build lead
        // (30 months) after the shortage began: 529 ZERO_HOT seed-months under lacksNow()'s line against 120 before.
        double slack = g.getAutoBuilder().getSlack();
        grow(g, lead);
        g.getAutoBuilder().setSlack(0, null);
        done = completion(g, lead);
        BuildAdvice.Ahead p0 = BuildAdvice.opening(g, lead, 0);
        check(String.format("fixture: the town grows %.4f over the coal plant's lead (%.0f months) and %.4f to the opening (%.0f months), the slack 0",
                        done.k(), lead, p0.k(), p0.months()),
                Math.abs(done.k() - 1 - GROWTH_AT_LEAD) < .01 && p0.k() > done.k() && p0.scale() == p0.k() && g.getAutoBuilder().getSlack() == 0);

        // ...covered now, short at the completion: it orders.
        drawRatio(g, BuildAdvice.NOW, 1 / (SEASONAL * (1 + done.k()) / 2));
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p0);
        sdNow = BuildAdvice.supplyDemand(g, POWER, Map.of(), BuildAdvice.NOW);
        sdDone = BuildAdvice.supplyDemand(g, POWER, Map.of(), done);
        check(String.format("fixture: the seasonal peak covered now (lacksNow() false: %.0f kW against %.0f) and short at the plant's completion (%.0f against %.0f); "
                                + "at the opening short of the peak hour by %.0f kW, %.0f%% of one coal plant's, under FIRST_WORK_SHARE's",
                        sdNow[0], sdNow[1] * SEASONAL, sdDone[0], sdDone[1] * SEASONAL, sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit),
                !BuildAdvice.lacksNow(g, POWER) && sdDone[0] < sdDone[1] * SEASONAL && sd[1] > sd[0] && sd[1] - sd[0] < share * unit);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("the seasonal peak short at the plant's own completion, the month covered: a gap under the share orders it (" + named(s) + ")",
                s != null && s.template() == coal && s.count() == 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);

        // ...covered at the completion, short only at the horizon past it: it waits.
        drawRatio(g, BuildAdvice.NOW, 1 / (SEASONAL * (done.k() + p0.k()) / 2));
        sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p0);
        sdDone = BuildAdvice.supplyDemand(g, POWER, Map.of(), done);
        check(String.format("fixture: the seasonal peak covered at the plant's completion (%.0f kW against %.0f) and short at the opening, the horizon's growth (%.0f); "
                                + "the peak hour short by %.0f kW, %.0f%% of one coal plant's, under FIRST_WORK_SHARE's",
                        sdDone[0], sdDone[1] * SEASONAL, sd[1] * SEASONAL, sd[1] - sd[0], 100 * (sd[1] - sd[0]) / unit),
                sdDone[0] >= sdDone[1] * SEASONAL && sd[0] < sd[1] * SEASONAL && sd[1] > sd[0] && sd[1] - sd[0] < share * unit);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s0 = BuildAdvice.SPREAD_ORDERS.get();
        s = suggest(g);
        check("a seasonal gap under the share that only the horizon past the completion projects: the power suggestion orders nothing (" + named(s) + ")",
                s == null && BuildAdvice.RESERVE_ORDERS.get() == r0 && BuildAdvice.SPREAD_ORDERS.get() == s0);
        g.getAutoBuilder().setSlack(slack, null);
        needDate(root);
    }

    /** (viii)'s months until the calendar brings the Diesel Station: as 1910's to a town of 1908, well inside coal's lead with the diesel's. */
    static final int ARRIVES = 12;

    /**
     * 8a (v) to (ix), REV-PW3f's P1 (2026-11-24), and (v') and (v''), REV-PW3g's P1: the need date. A town knowing two firm rows, coal (the slow) and the Diesel Station
     * (the fast), growing GROWTH_AT_LEAD over coal's lead, the slack 0. Tested each at its own lead, the slowest and biggest row was the
     * one that passed: LongPlaytest's Oil-Fired Plant, 102.8 months, ordered at m102 ($13.7M borrowed) for the seasonal gap of m205 that
     * the 30-month diesel could still meet. One need date and the rows that can be on line by it: short at coal's completion and covered
     * at the diesel's, the gap is the diesel's to meet, later, so nothing orders; short at the diesel's own completion, the diesel orders.
     * The rows that can be on line by it include one the calendar brings in time ((viii), (ix)): at m102 the diesel was not yet known.
     * Coal on its last on-time month, not late ((v'), (v'')), is weighed against the diesels on its cost alone, the screening curve.
     */
    static void needDate(Path root) {
        out.println("--- 8a (v, vi). two firm rows of different leads: a need date the fast row can still meet orders nothing; at the fast row's own completion, the fast row ---");
        Game g = town(root, "need-date", COAL, DIESEL);
        BuildingsTemplate coal = plant(g, COAL), diesel = plant(g, DIESEL);
        double unit = BuildAdvice.unit(g, POWER, coal), share = BuildAdvice.FIRST_WORK_SHARE, slack = g.getAutoBuilder().getSlack();
        double slow = g.quoteBuild(coal, 1).months, fast = g.quoteBuild(diesel, 1).months;
        grow(g, slow);
        g.getAutoBuilder().setSlack(0, null);
        BuildAdvice.Ahead pFast = BuildAdvice.opening(g, fast, 0), pSlow = BuildAdvice.opening(g, slow, 0);
        BuildAdvice.Ahead doneFast = completion(g, fast), doneSlow = completion(g, slow);

        // (v) the reserve covered at the diesel's opening (the firm supply NOW_OVER of the peak hour's demand there), so the seasonal peak
        // at its completion; the town's growth puts coal's completion short.
        drawRatio(g, pFast, 1 / NOW_OVER);
        double[] sdFast = BuildAdvice.supplyDemand(g, POWER, Map.of(), pFast), sdDoneFast = BuildAdvice.supplyDemand(g, POWER, Map.of(), doneFast);
        double[] sdSlow = BuildAdvice.supplyDemand(g, POWER, Map.of(), pSlow), sdDoneSlow = BuildAdvice.supplyDemand(g, POWER, Map.of(), doneSlow);
        long n = BuildAdvice.count(g, POWER, BuildAdvice.onSite(g, POWER), Map.of(diesel, 1L), pSlow, BuildAdvice.MOST, true)[0];
        double nLead = g.quoteBuild(diesel, Math.max(1, n)).months;
        check(String.format("fixture: the diesel's lead %.1f months, coal's %.1f; the reserve covered at the diesel's opening (%.0f kW against %.0f) and the seasonal "
                                + "peak at its completion (against %.0f); short at coal's completion (against %.0f, growth %.4f), at its opening short of the peak hour by "
                                + "%.0f kW, %.0f%% of one coal plant's, under FIRST_WORK_SHARE's; %d diesels close that gap, built in %.1f months, inside coal's lead",
                        fast, slow, sdFast[0], sdFast[1], sdDoneFast[1] * SEASONAL, sdDoneSlow[1] * SEASONAL, doneSlow.k(), sdSlow[1] - sdSlow[0],
                        100 * (sdSlow[1] - sdSlow[0]) / unit, n, nLead),
                fast < slow && BuildAdvice.maintained(g, POWER, Map.of(), pFast) && sdDoneFast[0] >= sdDoneFast[1] * SEASONAL
                        && sdDoneSlow[0] < sdDoneSlow[1] * SEASONAL && sdSlow[1] - sdSlow[0] < share * unit && n >= 1 && nLead < slow);
        double cheap = BuildAdvice.screening(g, diesel, Math.max(1, n), BuildAdvice.onSite(g, POWER), pSlow, g.getLandManager().getAvailableSqFt());
        double dear = BuildAdvice.screening(g, coal, 1, BuildAdvice.onSite(g, POWER), pSlow, g.getLandManager().getAvailableSqFt());
        check(String.format("fixture: at coal's opening the diesels meet the gap for less by the screening curve (%.4g a kW against coal's %.4g)", cheap, dear),
                cheap < dear);
        long r0 = BuildAdvice.RESERVE_ORDERS.get(), s0 = BuildAdvice.SPREAD_ORDERS.get();
        BuildAdvice.Suggestion s = suggest(g);
        check("short at the slow row's completion and covered at the fast row's: the need is the fast row's to meet, later, and nothing orders (" + named(s) + ")",
                s == null && BuildAdvice.RESERVE_ORDERS.get() == r0 && BuildAdvice.SPREAD_ORDERS.get() == s0);

        // (vi) the seasonal peak short at the diesel's own completion (the firm supply NOW_UNDER of its draw there): its last on-time
        // month, and the diesel orders, not coal.
        drawRatio(g, doneFast, 1 / (SEASONAL * NOW_UNDER));
        sdDoneFast = BuildAdvice.supplyDemand(g, POWER, Map.of(), doneFast);
        check(String.format("fixture: the seasonal peak short at the diesel's completion (%.0f kW against %.0f)", sdDoneFast[0], sdDoneFast[1] * SEASONAL),
                sdDoneFast[0] < sdDoneFast[1] * SEASONAL);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("short at the fast row's own completion: the fast row orders, not the slow (" + named(s) + ")",
                s != null && s.template() == diesel && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);

        // (vii) (v)'s draw again, the diesel's fuel doubled from the world's price until coal is the cheaper at its own opening by the
        // screening curve: coal is due by the waiver alone and late (the seasonal peak short a month before its completion too), and the
        // diesel can still be on line by the need date, so coal is no candidate whatever it costs (the rule's (b)), and nothing orders.
        drawRatio(g, pFast, 1 / NOW_OVER);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        Good oil = diesel.getFuel();
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, POWER);
        double land = g.getLandManager().getAvailableSqFt(), dearer = 1;
        while (dearer < 4096) {
            dearer *= 2;
            double k = dearer;
            u.setFuelPrice(good -> (good == oil ? k : 1) * good.worldImportPrice());
            n = BuildAdvice.count(g, POWER, site, Map.of(diesel, 1L), pSlow, BuildAdvice.MOST, true)[0];
            if (BuildAdvice.screening(g, diesel, Math.max(1, n), site, pSlow, land) > BuildAdvice.screening(g, coal, 1, site, pSlow, land)) break;
        }
        cheap = BuildAdvice.screening(g, coal, 1, site, pSlow, land);
        dear = BuildAdvice.screening(g, diesel, Math.max(1, n), site, pSlow, land);
        sdSlow = BuildAdvice.supplyDemand(g, POWER, Map.of(), pSlow);
        boolean late = BuildAdvice.seasonalShort(g, POWER, WorksValue.openBy(g, site, slow - 1), slow - 1),
                fastCovered = !BuildAdvice.seasonalShort(g, POWER, WorksValue.openBy(g, site, fast), fast);
        check(String.format("fixture: the diesel's fuel %.0f times the world's price, and coal the cheaper at its own opening (%.4g a kW against %d diesels' %.4g); "
                                + "the gap there %.0f%% of one coal plant's, under FIRST_WORK_SHARE's; the seasonal peak short a month before coal's completion (%b) "
                                + "and covered at the diesel's (%b)", dearer, cheap, n, dear, 100 * (sdSlow[1] - sdSlow[0]) / unit, late, fastCovered),
                cheap < dear && sdSlow[1] - sdSlow[0] < share * unit && late && fastCovered);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s0 = BuildAdvice.SPREAD_ORDERS.get();
        s = suggest(g);
        check("the slow row the cheaper but past its last on-time month, the fast row still able to be on line by the need date: nothing orders (" + named(s) + ")",
                s == null && BuildAdvice.RESERVE_ORDERS.get() == r0 && BuildAdvice.SPREAD_ORDERS.get() == s0);

        out.println("--- 8a (v', v''). the slow row on its last on-time month: a faster row meeting the gap at its opening for less waits it; the slow row the cheaper orders ---");
        // (v') the seasonal peak at its line half a month before coal's completion, at the world's fuel prices: short at the completion
        // and covered a month before it, so coal is on time, not late (REV-PW3g's P1: (v) and (vii) are late, and (viii) is (v)'s draw).
        // The diesels close coal's gap at its opening for less by the screening curve, the rule's (c): they order at their own last
        // on-time month instead, and nothing orders now.
        u.setFuelPrice(Good::worldImportPrice);
        drawRatio(g, completion(g, slow - .5), 1 / SEASONAL);
        boolean shortAtSlow = BuildAdvice.seasonalShort(g, POWER, site, slow);
        late = BuildAdvice.seasonalShort(g, POWER, WorksValue.openBy(g, site, slow - 1), slow - 1);
        fastCovered = !BuildAdvice.seasonalShort(g, POWER, WorksValue.openBy(g, site, fast), fast);
        n = BuildAdvice.count(g, POWER, site, Map.of(diesel, 1L), pSlow, BuildAdvice.MOST, true)[0];
        nLead = g.quoteBuild(diesel, Math.max(1, n)).months;
        cheap = BuildAdvice.screening(g, diesel, Math.max(1, n), site, pSlow, land);
        dear = BuildAdvice.screening(g, coal, 1, site, pSlow, land);
        sdSlow = BuildAdvice.supplyDemand(g, POWER, Map.of(), pSlow);
        check(String.format("fixture: the seasonal peak short at coal's completion (%b), covered a month before it (%b) and at the diesel's (%b); the gap at coal's "
                                + "opening %.0f kW, %.0f%% of one coal plant's, under FIRST_WORK_SHARE's; %d diesels close it, built in %.1f months, inside coal's lead, "
                                + "for less (%.4g a kW against coal's %.4g)", shortAtSlow, !late, fastCovered, sdSlow[1] - sdSlow[0], 100 * (sdSlow[1] - sdSlow[0]) / unit,
                        n, nLead, cheap, dear),
                shortAtSlow && !late && fastCovered && sdSlow[1] > sdSlow[0] && sdSlow[1] - sdSlow[0] < share * unit && n >= 1 && nLead < slow && cheap < dear);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s0 = BuildAdvice.SPREAD_ORDERS.get();
        s = suggest(g);
        check("the slow row on its last on-time month, a faster row closing the gap at its opening for less: the faster row meets the date later, and nothing orders ("
                        + named(s) + ")",
                s == null && BuildAdvice.RESERVE_ORDERS.get() == r0 && BuildAdvice.SPREAD_ORDERS.get() == s0);

        // (v'') its twin: the same draw, the diesel's fuel doubled from the world's price until coal is the cheaper at its own opening, so
        // coal is the least-cost row that meets the date, on its last on-time month, and it orders.
        dearer = 1;
        while (dearer < 4096) {
            dearer *= 2;
            double k = dearer;
            u.setFuelPrice(good -> (good == oil ? k : 1) * good.worldImportPrice());
            n = BuildAdvice.count(g, POWER, site, Map.of(diesel, 1L), pSlow, BuildAdvice.MOST, true)[0];
            if (BuildAdvice.screening(g, diesel, Math.max(1, n), site, pSlow, land) > BuildAdvice.screening(g, coal, 1, site, pSlow, land)) break;
        }
        cheap = BuildAdvice.screening(g, coal, 1, site, pSlow, land);
        dear = BuildAdvice.screening(g, diesel, Math.max(1, n), site, pSlow, land);
        check(String.format("fixture: the diesel's fuel %.0f times the world's price, and coal the cheaper at its own opening (%.4g a kW against %d diesels' %.4g)",
                        dearer, cheap, n, dear), cheap < dear);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s = suggest(g);
        check("the slow row on its last on-time month and the cheaper: it orders (" + named(s) + ")",
                s != null && s.template() == coal && s.count() == 1 && BuildAdvice.RESERVE_ORDERS.get() == r0 + 1);

        out.println("--- 8a (viii, ix). a faster firm row the calendar brings onto the menu in time is weighed by its arrival and its lead ---");
        // (viii) (v)'s draw at the world's fuel prices, the menu coal alone now and the diesel from ARRIVES months on (as the 1910 Diesel
        // Station for a 1908 town): its arrival and its lead fall inside coal's, so it can still be on line by the need date, and nothing orders.
        u.setFuelPrice(Good::worldImportPrice);
        drawRatio(g, pFast, 1 / NOW_OVER);
        BuildingManager bm = g.getBuildingManager();
        know(g, COAL);
        bm.setTemplateKnownIn((t, months) -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || t == coal
                || t == diesel && months >= ARRIVES);
        n = BuildAdvice.count(g, POWER, site, Map.of(diesel, 1L), pSlow, BuildAdvice.MOST, true)[0];
        cheap = BuildAdvice.screening(g, diesel, Math.max(1, n), site, pSlow, land);
        dear = BuildAdvice.screening(g, coal, 1, site, pSlow, land);
        nLead = g.quoteBuild(diesel, Math.max(1, n)).months;
        sdSlow = BuildAdvice.supplyDemand(g, POWER, Map.of(), pSlow);
        boolean arrivedCovered = !BuildAdvice.seasonalShort(g, POWER, WorksValue.openBy(g, site, ARRIVES + fast), ARRIVES + fast);
        check(String.format("fixture: the diesel off the menu now and on it %d months on (%b, %b); %d diesels close the gap at coal's opening (%.0f%% of one coal plant's, "
                                + "under FIRST_WORK_SHARE's) for less (%.4g a kW against %.4g), on line %.1f months on, inside coal's %.1f; the seasonal peak covered at "
                                + "one diesel's (%b)", ARRIVES, bm.isTemplateKnown(diesel), bm.isTemplateKnownIn(diesel, ARRIVES), n, 100 * (sdSlow[1] - sdSlow[0]) / unit,
                        cheap, dear, ARRIVES + nLead, slow, arrivedCovered),
                !bm.isTemplateKnown(diesel) && bm.isTemplateKnownIn(diesel, ARRIVES) && !bm.isTemplateKnownIn(diesel, ARRIVES - 1) && n >= 1
                        && sdSlow[1] - sdSlow[0] < share * unit && cheap < dear && ARRIVES + nLead < slow && arrivedCovered);
        r0 = BuildAdvice.RESERVE_ORDERS.get();
        s0 = BuildAdvice.SPREAD_ORDERS.get();
        s = suggest(g);
        check("a faster row the calendar brings in time can still meet the need date: the slow row known now waits, and nothing orders (" + named(s) + ")",
                s == null && BuildAdvice.RESERVE_ORDERS.get() == r0 && BuildAdvice.SPREAD_ORDERS.get() == s0);

        // (ix) the diesel brought only after coal's lead: no faster row can be on line by the need date, and coal, the one candidate, orders.
        int after = (int) Math.ceil(slow) + 1;
        bm.setTemplateKnownIn((t, months) -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || t == coal
                || t == diesel && months >= after);
        s = suggest(g);
        check(String.format("the diesel brought %d months on, past coal's lead: coal orders (%s)", after, named(s)), s != null && s.template() == coal);
        g.getAutoBuilder().setSlack(slack, null);
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
    /**
     * 8b (b): WorksFinanceCheck.reserveTown()'s founded town, every other essential service given its operating capacity, with a second Coal Power Plant going up, nearly built
     * (Power's saved completion, AutoBuilder's pendingUntil, as rememberPending() quotes it: the month and the months to its first power), the builders' output so tight that any order slows it past
     * that quote, the streets carrying their load with 1% to spare (the plan orders Gravel Roads ahead of need: the other essential order), and the firm supply
     * `level` of the draw; the pass not yet run.
     */
    static Game holdTown(Path root, String name, double level) {
        Game g = WorksFinanceCheck.reserveTown(root, name);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate coal = plant(g, COAL), road = plant(g, "Paved Road");
        check("fixture: every essential service but Power has its operating capacity", EssentialWorksCheck.serveOtherEssentials(g, POWER));
        quietly(() -> g.buildStack(coal, 1, false));
        // ...its remaining work scaled to 4 months of the first quote, a few months short of finished; the quote it then gives (it reads the build time too) and its room are asserted below.
        double first = bm.siteUnitMonths(coal, 1, g.getBuildingOutputAtEveryPost());
        BuildingsStacks site = bm.getStack(coal);
        site.setConstructionProgress(coal.getConstructionPoints() - (coal.getConstructionPoints() - site.getConstructionProgress()) * 4 / first);
        InfrastructureManager roads = g.getInfrastructureManager();
        roads.setModes(0, 0);
        roads.setThrough(0, g.getLandManager().getAllocatedSqFt(), 0);
        roads.setBuiltCapacity(20.0 * road.getCapacity());
        roads.setLoad(roads.getStreetCapacity() / 1.01);
        double[] traffic = new double[Traffic.values().length];
        traffic[Traffic.COMMUTERS.ordinal()] = roads.getLoad();
        roads.setBreakdown(traffic);
        roads.setBuiltCapacity(roads.getStreetLoad() * 1.01 - InfrastructureManager.BASE_CAPACITY);
        // ...the draw set so the firm supply is `level` of it (the wires carry it, as peakBlock()'s do), and set again after the wires are stood.
        drawRatio(g, BuildAdvice.NOW, PowerDispatch.PLAN_PEAK_HOUR / level);
        SubstationCheck.wiresFor(g, 1.05 * g.getServicesManager().getUtilitiesHandler().getConsumption() * PowerDispatch.PLAN_PEAK_SEASON);
        drawRatio(g, BuildAdvice.NOW, PowerDispatch.PLAN_PEAK_HOUR / level);
        AutoBuilder ab = g.getAutoBuilder();
        AutoBuilder.State state = ab.toState();
        state.on = true;
        state.pendingUntil.clear();
        state.pendingUntil.put(POWER.label(), g.getMonth() + BuildAdvice.firstCompletion(g, POWER, BuildAdvice.onSite(g, POWER)));
        ab.restore(state);
        return g;
    }

    /**
     * 8b (b): the holds read the same "short now" as the month does, through a real pass (AutoBuilder.crewLimit() and pendingDelay()). Power's plant is nearly
     * built and the Gravel Roads the plan orders ahead of need would slow it past its saved quote. With the firm supply between PLAN_PEAK_SEASON and
     * PLAN_PEAK_HOUR of the draw the month's dispatch serves the peak block, lacksNow() is false and the roads are ordered; below PLAN_PEAK_SEASON the peak block goes
     * unserved, and the order waits to preserve Power's quoted first completion. (The plan's peak hour read as short would hold them in the first case: the mend's twin.)
     */
    static void holds(Path root) {
        out.println("--- 8b (b). the holds read the month's short, through a pass: another essential order waits to preserve Power's quoted first completion only when the peak block goes unserved ---");
        double[] levels = {(PowerDispatch.PLAN_PEAK_SEASON + PowerDispatch.PLAN_PEAK_HOUR) / 2, (PowerDispatch.PLAN_PEAK + PowerDispatch.PLAN_PEAK_SEASON) / 2};
        String[] where = {"between PLAN_PEAK_SEASON and PLAN_PEAK_HOUR", "between PLAN_PEAK and PLAN_PEAK_SEASON"};
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        for (int i = 0; i < levels.length; i++) {
            Game g = holdTown(root, "hold-" + i, levels[i]);
            AutoBuilder ab = g.getAutoBuilder();
            BuildingManager bm = g.getBuildingManager();
            BuildingsTemplate coal = plant(g, COAL);
            BuildAdvice.Suggestion order = BuildAdvice.suggestFor(g, null, streets, g.getCash(), g.getLandManager().getAvailableSqFt(), ab.getSlack(), Set.of(), false, true);
            double due = ab.toState().pendingUntil.getOrDefault(POWER.label(), Double.NaN), output = g.getBuildingOutputAtEveryPost();
            double alone = bm.siteUnitMonths(coal, 1, output);
            double with = order == null ? Double.NaN : bm.siteUnitMonthsAfter(coal, 1, output, order.template(), order.count(),
                    WorksFinanceCheck.addedPoints(g.quoteBuild(order.template(), order.count()), order.template(), order.count()));
            double one = order == null ? Double.NaN : bm.siteUnitMonthsAfter(coal, 1, output, order.template(), 1,
                    WorksFinanceCheck.addedPoints(g.quoteBuild(order.template(), 1), order.template(), 1));
            double firm = supplyOverDraw(g, BuildAdvice.NOW);
            check(String.format("fixture: firm supply %.3f of the draw, %s (Power lacks now: %b); Power's plant is due month %.2f (now %d) and opens in %.4f months, the order (%s) would open it in %.4f "
                            + "(month %.2f), past the saved quote, as would one alone (%.4f); the builders' output is %.0f",
                            firm, where[i], BuildAdvice.lacksNow(g, POWER), due, g.getMonth(), alone, named(order), with, g.getMonth() + with, one, output),
                    close(firm, levels[i], 1e-6) && BuildAdvice.lacksNow(g, POWER) == (i == 1) && !BuildAdvice.lacksNow(g, streets) && order != null && !order.paving()
                            && g.getMonth() + alone <= due + 1e-9 && g.getMonth() + Math.ceil(one - 1e-9) > due && g.getMonth() + Math.ceil(with - 1e-9) > due);
            if (order == null) continue;
            quietly(() -> ab.pass(g));
            AutoBuilder.Step step = EssentialWorksCheck.step(g, streets);
            String hold = ab.toState().crewHolds.get(streets.label());
            boolean words = ab.held().stream().anyMatch(h -> h.contains(streets.label() + " waits to preserve " + POWER.label() + "'s quoted first completion."));
            String said = step == null ? "no step" : step.outcome() + " " + step.ordered() + " of " + step.wanted() + " " + step.building() + ", cut " + step.cut();
            double slipped = bm.siteUnitMonths(coal, 1, g.getBuildingOutputAtEveryPost());
            if (i == 0)
                check("the peak block served, Power is not lacked and the roads are ordered, nothing waiting to preserve its completion (" + said + "; the plant now opens in " + String.format("%.4f", slipped) + " months)",
                        step != null && step.ordered() == step.wanted() && step.ordered() > 0 && hold == null && !words && g.getMonth() + Math.ceil(slipped - 1e-9) > due);
            else
                check("the peak block unserved, the roads wait to preserve Power's quoted first completion (" + said + ", held for " + hold + ", the step's words " + words + "; the plant still opens in " + String.format("%.4f", slipped) + " months)",
                        step != null && step.ordered() == 0 && step.cut() == AutoBuilder.Cut.BUILDERS && POWER.label().equals(hold) && words && close(slipped, alone, 1e-9));
        }
    }

    /**
     * 8b (b), the other way round: reserveTown()'s founded town with General care lacking now, its Community Health Centres going up and their first completion saved
     * (pendingUntil, as rememberPending() quotes it), and no plant on site: the plan's order ahead of need is Power's. The firm supply is `level` of the draw; the pass not yet run.
     */
    static Game careTown(Path root, String name, double level, BuildAdvice.Measure care) {
        Game g = WorksFinanceCheck.reserveTown(root, name);
        BuildingManager bm = g.getBuildingManager();
        check("fixture: every essential service but General care has its operating capacity", EssentialWorksCheck.serveOtherEssentials(g, care));
        for (BuildingsTemplate t : bm.getTemplates()) if (care.serves(t) && bm.getQuantity(t.getId()) > 0) quietly(() -> bm.retire(t, bm.getQuantity(t.getId())));
        BuildingsTemplate centre = plant(g, "Community Health Centre");
        long required = BuildAdvice.count(g, care, Map.of(), Map.of(centre, 1L), g.getAutoBuilder().target(), BuildAdvice.MOST, true)[0];
        quietly(() -> g.buildStack(centre, required, false));
        drawRatio(g, BuildAdvice.NOW, PowerDispatch.PLAN_PEAK_HOUR / level);
        SubstationCheck.wiresFor(g, 1.05 * g.getServicesManager().getUtilitiesHandler().getConsumption() * PowerDispatch.PLAN_PEAK_SEASON);
        drawRatio(g, BuildAdvice.NOW, PowerDispatch.PLAN_PEAK_HOUR / level);
        AutoBuilder ab = g.getAutoBuilder();
        AutoBuilder.State state = ab.toState();
        state.on = true;
        state.pendingUntil.clear();
        state.pendingUntil.put(care.label(), g.getMonth() + BuildAdvice.firstCompletion(g, care, BuildAdvice.onSite(g, care)));
        ab.restore(state);
        return g;
    }

    /**
     * 8b (b), the guard: the order of a measure the month lacks is never held (AutoBuilder.crewLimit()). Power's own order ahead of need would slow the saved completion of
     * General care's centres, which lacks now. With the firm supply between PLAN_PEAK_SEASON and PLAN_PEAK_HOUR of the draw the peak block is served, Power is not lacked,
     * and its order waits to preserve care's quoted first completion; below PLAN_PEAK_SEASON the peak block goes unserved and the order, a lacked measure's, is placed.
     */
    static void holdsPower(Path root) {
        out.println("--- 8b (b). the holds' guard reads the month's short, through a pass: Power's order waits to preserve a lacked service's completion only when its own peak block is served ---");
        double[] levels = {(PowerDispatch.PLAN_PEAK_SEASON + PowerDispatch.PLAN_PEAK_HOUR) / 2, (PowerDispatch.PLAN_PEAK + PowerDispatch.PLAN_PEAK_SEASON) / 2};
        String[] where = {"between PLAN_PEAK_SEASON and PLAN_PEAK_HOUR", "between PLAN_PEAK and PLAN_PEAK_SEASON"};
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        for (int i = 0; i < levels.length; i++) {
            Game g = careTown(root, "hold-care-" + i, levels[i], care);
            AutoBuilder ab = g.getAutoBuilder();
            BuildingManager bm = g.getBuildingManager();
            BuildingsTemplate centre = plant(g, "Community Health Centre");
            BuildAdvice.Suggestion order = suggest(g);
            double due = ab.toState().pendingUntil.getOrDefault(care.label(), Double.NaN), output = g.getBuildingOutputAtEveryPost();
            double alone = bm.siteUnitMonths(centre, 1, output);
            double with = order == null ? Double.NaN : bm.siteUnitMonthsAfter(centre, 1, output, order.template(), order.count(),
                    WorksFinanceCheck.addedPoints(g.quoteBuild(order.template(), order.count()), order.template(), order.count()));
            double firm = supplyOverDraw(g, BuildAdvice.NOW);
            check(String.format("fixture: firm supply %.3f of the draw, %s (Power lacks now: %b); care lacks now (%b), its centres due month %.2f (now %d) and open in %.4f months, "
                            + "Power's order (%s) would open them in %.4f (month %.2f), past the saved quote; the builders' output is %.0f",
                            firm, where[i], BuildAdvice.lacksNow(g, POWER), BuildAdvice.lacksNow(g, care), due, g.getMonth(), alone, named(order), with, g.getMonth() + with, output),
                    close(firm, levels[i], 1e-6) && BuildAdvice.lacksNow(g, POWER) == (i == 1) && BuildAdvice.lacksNow(g, care) && order != null
                            && g.getMonth() + alone <= due + 1e-9 && g.getMonth() + Math.ceil(with - 1e-9) > due);
            if (order == null) continue;
            quietly(() -> ab.pass(g));
            AutoBuilder.Step step = EssentialWorksCheck.step(g, POWER);
            String hold = ab.toState().crewHolds.get(POWER.label());
            boolean words = ab.held().stream().anyMatch(h -> h.contains(POWER.label() + " waits to preserve " + care.label() + "'s quoted first completion."));
            String said = step == null ? "no step" : step.outcome() + " " + step.ordered() + " of " + step.wanted() + " " + step.building() + ", cut " + step.cut();
            if (i == 0)
                check("the peak block served, Power is not lacked and its order waits to preserve " + care.label() + "'s quoted first completion (" + said + ", held for " + hold + ", the step's words " + words + ")",
                        step != null && step.ordered() == 0 && step.cut() == AutoBuilder.Cut.BUILDERS && care.label().equals(hold) && words);
            else
                check("the peak block unserved, Power is lacked and its order is placed whatever it does to " + care.label() + "'s completion (" + said + ", held for " + hold + ")",
                        step != null && step.ordered() > 0 && hold == null && !words);
        }
    }

    /* ----- 9. a works under construction ----- */

    /** The whole months the mill's quote to open starts past the diesel's lead and the opening's horizon, before it falls to them a month at a time. */
    static final double MILL_PAST = 6;

    /** The share of the mill's own seasonal draw the twin's seasonal peak leaves spare at the mill's opening, so the mill town is short by the rest. */
    static final double MILL_SPARE = .5;

    /**
     * 9, PF1 (2026-11-25): a works on site is in the plan's forecast from the month it is due to open, at its rated draw, and the plant it needs
     * is ordered to be on line by then; the same town without it orders nothing. A utility plans a large load from its signed request on its
     * ramp schedule (ERCOT's 2025 Long-Term Load Forecast adds a large load with a signed agreement and a financial commitment, its "Contracts",
     * on their ramp schedules, as a 2023 Texas law requires: ercot.com/files/docs/2025/04/08/ERCOT-2025-Long-Term-Load-Forecast-Report.pdf);
     * here a works on site is that request. REV-PW3g (2026-11-24, item 6) read LongPlaytest's m116-m150 power served at 0.57-0.61 as a forecast
     * blind to a works under construction; PF1's trace (0.9.208) found the forecast already carries each at its rated draw from its due month
     * (WorksValue.pendingPowerLoad()), so this pins it end to end, red under a trend-only forecast.
     *
     * Two towns with the Diesel Station the one plant known, slack 0, the flat history, the same builders (DEPOTS, so that a score of diesels are
     * quoted at their own lead and not at the founding's few crews), the same draw: the twin's, set so its reserve is covered at the diesel's
     * opening and its seasonal peak at its completion, with MILL_SPARE of the mill's own seasonal draw to spare. The mill town has a Large Mini-Mill
     * on site (the Steel Mini-Mill's 3.7 MW is under the 12% of the draw that lies between the plan's peak hour and the seasonal peak, so a twin
     * set to be short by half of it would itself order two diesels), its work done to leave its quote to open past the lead and the horizon
     * (BuildingsStacks.setConstructionProgress()), and the quote then falls a month at a time, a month being the builders' output at every post
     * put on the stack's progress. At each step both towns are asked for the power suggestion.
     */
    static void millUnderConstruction(Path root) {
        out.println("--- 9. a works under construction: its rated draw is in the forecast from the month it is due to open, and the power it needs is ordered to be on line then; the same town without it orders nothing ---");
        Game g = town(root, "pf1-mill", DIESEL), twin = town(root, "pf1-twin", DIESEL);
        for (Game q : new Game[] {g, twin}) {
            BuildingsTemplate depot = plant(q, BuildersFirstCheck.DEPOT);
            q.getAutoBuilder().setSlack(0, null);
            quietly(() -> q.buildStack(depot, DEPOTS, true));
            q.getServicesManager().updateServices();
        }
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate diesel = plant(g, DIESEL), mill = bm.getTemplate(HeavyIndustry.LARGE_MINI_MILL);
        double lead = twin.quoteBuild(plant(twin, DIESEL), 1).months, horizon = BusinessInvestment.horizonFor(lead);
        double growthLead = twin.getBusinessInvestment().growthFactor(lead), growthOpen = twin.getBusinessInvestment().growthFactor(lead + horizon);
        Game.BuildResult[] placed = new Game.BuildResult[1];
        quietly(() -> placed[0] = g.buildStack(mill, 1, false));
        BuildingsStacks stack = bm.getStack(mill);
        int output = g.getBuildingOutputAtEveryPost();
        if (stack != null) stack.setConstructionProgress(stack.pointsOnSite() - (Math.ceil(lead + horizon) + MILL_PAST) * output);
        double start = bm.siteUnitMonths(mill, 1, output), rated = NewUnit.of(g, mill).getElectricityConsumption();
        check(String.format("fixture: the Diesel Station's lead is %.1f months and the opening's horizon %.1f (the town's growth over the lead %.4f and over both %.4f, the flat history), the same %.1f in the mill town "
                        + "with the %s on site; the mill is quoted to open in %.1f months, past %.1f, and draws %.0f kW once open; the twin has none on site (%b)",
                        lead, horizon, growthLead, growthOpen, g.quoteBuild(diesel, 1).months, mill.getName(), start, lead + horizon, rated, twin.getBuildingManager().getStacksUnderConstruction().isEmpty()),
                placed[0] == Game.BuildResult.SUCCESS && stack != null && bm.getStacksUnderConstruction().contains(stack) && stack.getUnderConstruction() == 1
                        && twin.getBuildingManager().getStacksUnderConstruction().isEmpty() && close(growthLead, 1, 1e-9) && close(growthOpen, 1, 1e-9)
                        && close(g.quoteBuild(diesel, 1).months, lead, 1e-9) && start > lead + horizon && rated > 0);
        if (stack == null) return;

        // The twin's draw: the diesel's opening has the reserve covered, and the seasonal peak MILL_SPARE of the mill's own seasonal draw to spare.
        BuildAdvice.Ahead open = BuildAdvice.opening(twin, lead, 0), done = completion(twin, lead);
        double firm = BuildAdvice.supplyDemand(twin, POWER, Map.of(), open)[0];
        drawGap(twin, open, (firm / PowerDispatch.PLAN_PEAK_SEASON - MILL_SPARE * rated) * PowerDispatch.PLAN_PEAK_HOUR - firm);
        g.getServicesManager().getUtilitiesHandler().setWattsConsumption(twin.getServicesManager().getUtilitiesHandler().getConsumption());
        double[] sdDone = BuildAdvice.supplyDemand(twin, POWER, Map.of(), done), sdOpen = BuildAdvice.supplyDemand(twin, POWER, Map.of(), open);
        BuildAdvice.Suggestion bare = suggest(twin);
        check(String.format("fixture: the twin, drawing %.0f kW, has its seasonal peak covered at the diesel's completion (%.0f kW against %.0f) and its peak hour at the opening (%.0f against %.0f), "
                        + "and its power suggestion is %s", twin.getServicesManager().getUtilitiesHandler().getConsumption(), sdDone[0], sdDone[1] * SEASONAL, sdOpen[0], sdOpen[1], named(bare)),
                sdDone[0] >= sdDone[1] * SEASONAL && sdOpen[0] >= sdOpen[1] && bare == null);
        double[] sdMill = BuildAdvice.supplyDemand(g, POWER, Map.of(), completion(g, start));
        double short0 = sdMill[1] * SEASONAL - sdMill[0], own = rated * PowerDispatch.PLAN_PEAK_SEASON;
        check(String.format("fixture: at the mill's opening its rated draw puts the seasonal peak short by %.0f kW, %.0f%% of its own seasonal draw, where the twin's is covered",
                        short0, 100 * short0 / own),
                BuildAdvice.seasonalShort(g, POWER, Map.of(), start) && !BuildAdvice.seasonalShort(twin, POWER, Map.of(), start) && short0 > 0 && short0 < own);

        // The steps: the mill's quote to open falls a month at a time, nothing else changed; both towns are asked at each.
        BuildAdvice.Suggestion first = null;
        double firstM = Double.NaN, firstLead = Double.NaN;
        boolean closed = false, reads = true, twinNothing = true;
        int steps = 0, firstStep = -1;
        double before0 = Double.NaN, after0 = Double.NaN;
        for (double m = start; m >= 1; m = bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost())) {
            double[] dm = {BuildAdvice.supplyDemand(g, POWER, Map.of(), completion(g, m - .5))[1], BuildAdvice.supplyDemand(g, POWER, Map.of(), completion(g, m + .5))[1]};
            double[] dt = {BuildAdvice.supplyDemand(twin, POWER, Map.of(), completion(twin, m - .5))[1], BuildAdvice.supplyDemand(twin, POWER, Map.of(), completion(twin, m + .5))[1]};
            reads &= close(dm[0], dt[0], 1e-9) && close(dm[1], dt[1] + rated * PowerDispatch.PLAN_PEAK_HOUR, 1e-9);
            if (steps == 0) {
                before0 = dm[0] - dt[0];
                after0 = dm[1] - dt[1];
            }
            twinNothing &= suggest(twin) == null;
            if (first == null) {
                BuildAdvice.Suggestion s = suggest(g);
                if (s != null) {
                    first = s;
                    firstM = m;
                    firstStep = steps;
                    firstLead = g.quoteBuild(s.template(), s.count()).months;
                    closed = !BuildAdvice.seasonalShort(g, POWER, Map.of(s.template(), s.count()), m);
                }
            }
            steps++;
            stack.setConstructionProgress(stack.getConstructionProgress() + g.getBuildingOutputAtEveryPost());
        }
        check(String.format("the forecast reads the works from the month it is due to open, at its rated draw: at each of %d steps the mill town's peak-hour demand a half month before the opening is the twin's "
                        + "(%.0f kW over it at the first step) and a half month after it is over by the mill's rated %.0f kW at the plan's peak hour (%.0f kW at the first step)",
                        steps, before0, rated * PowerDispatch.PLAN_PEAK_HOUR, after0),
                steps > 0 && reads);
        boolean diesels = first != null && first.template() == diesel;
        check(String.format("the mill town's first order is %s, at step %d with the mill %.0f months from its opening, inside the %.1f months the order is planned over; it is on line in %.1f months, "
                        + "%.1f to spare before the mill opens, and with it counted the seasonal peak at the mill's opening is covered (%b)",
                        named(first), firstStep, firstM, first == null ? Double.NaN : first.ahead().months(), firstLead, firstM - firstLead, closed),
                diesels && firstM <= first.ahead().months() + 1e-9 && firstLead < firstM + 1 && closed);
        check(String.format("the same town without the mill orders nothing at any of the %d steps", steps), steps > 0 && twinNothing);
    }

    /** PW9: a town of three thousand, short at the seasonal peak, four years before diesel is known. */
    static void earlyTown(Path root) {
        out.println("--- 9a. a small firm plant for a 1906 town (PW9) ---");
        Game g = WorksFinanceCheck.founded(root, "small-steam-1906", new String[][] {
                {"House", "900"}, {"Construction Depot", "5"}, {"Water Treatment Plant", "1"}, {"Commercial Bank", "1"}});
        quietly(() -> g.simulateMonths(ResearchCatalog.knownFrom("1906") - g.getMonth()));
        WorksFinanceCheck.place(g, 3_000);
        g.getBusinessInvestment().restorePopulationHistory(java.util.List.of(3_000L, 3_000L));
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        g.setCashForTest(1e12);
        g.getAutoBuilder().setCashAnyway(true, null);
        g.getAutoBuilder().setSlack(0, null);
        BuildingManager bm = g.getBuildingManager();
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2);
        // Restore the real date gates: founded() lends other cases an early discovery of wind.
        bm.setTemplateKnown(t -> t.getCategory() != BuildingType.ELECTRICITY || t.knownAt(g.getMonth()));
        bm.setTemplateKnownIn((t, months) -> t.getCategory() != BuildingType.ELECTRICITY || t.knownAt(g.getMonth() + months));
        // A small but real seasonal shortfall, independent of whether the missing row exists.
        drawRatio(g, BuildAdvice.NOW, PowerDispatch.PLAN_PEAK_HOUR / PowerDispatch.PLAN_PEAK_SEASON * 1.01);
        double[] before = BuildAdvice.supplyDemand(g, POWER, Map.of(), BuildAdvice.NOW);
        BuildingsTemplate small = plant(g, "Small Steam Station"), oil = plant(g, "Oil-Fired Plant"), diesel = plant(g, DIESEL);
        check("fixture: 1906, three thousand people, seasonal power short now, oil known and diesel not yet known",
                CityCalendar.yearOf(g.getMonth()) == 1906 && g.getPopulationManager().getPopulation() == 3_000 && BuildAdvice.lacksNow(g, POWER)
                        && bm.isTemplateKnown(oil) && !bm.isTemplateKnown(diesel));
        bm.setTemplateKnown(t -> t != small && (t.getCategory() != BuildingType.ELECTRICITY || t.knownAt(g.getMonth())));
        bm.setTemplateKnownIn((t, months) -> t != small && (t.getCategory() != BuildingType.ELECTRICITY || t.knownAt(g.getMonth() + months)));
        BuildAdvice.Suggestion old = suggest(g);
        check("without the small station the same town orders the 86 MW Oil-Fired Plant (" + named(old) + ")",
                old != null && old.template() == oil);
        bm.setTemplateKnown(t -> t.getCategory() != BuildingType.ELECTRICITY || t.knownAt(g.getMonth()));
        bm.setTemplateKnownIn((t, months) -> t.getCategory() != BuildingType.ELECTRICITY || t.knownAt(g.getMonth() + months));
        BuildAdvice.Suggestion s = suggest(g);
        check("the 1906 town orders the Small Steam Station through the normal planner (" + named(s) + ")",
                small != null && s != null && s.template() == small && s.count() > 0 && s.closes());
        if (small == null || s == null || s.template() != small) return;
        double[] after = BuildAdvice.supplyDemand(g, POWER, Map.of(small, s.count()), s.ahead());
        check("the small order closes the peak-hour gap and is quoted before diesel's year",
                before[0] < before[1] && after[0] >= after[1]
                        && g.getMonth() + s.lead() < ResearchCatalog.knownFrom(diesel.getSinceText()));
        boolean[] placed = {false};
        quietly(() -> placed[0] = g.getAutoBuilder().placeForTest(g, POWER, s, s.count()));
        check("the automatic path actually places the small stations, with no Oil-Fired Plant on site",
                placed[0] && bm.getStack(small) != null && bm.getStack(small).getUnderConstruction() == s.count()
                        && (bm.getStack(oil) == null || bm.getStack(oil).getUnderConstruction() == 0));
    }

    /** PW9's played finding: the first 733 kW station fabricated a 1,545 kW loss from the jobless founding grid. */
    static void foundingGrid(Path root) {
        out.println("--- 9b. a small station cannot consume the jobless founding endowment (PW9) ---");
        Game g = WorksFinanceCheck.founded(root, "founding-grid", new String[][] {{"House", "900"}, {"Construction Depot", "5"}});
        WorksFinanceCheck.place(g, 3_000);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate small = plant(g, "Small Steam Station"), water = plant(g, "Water Treatment Plant"), sub = plant(g, "Substation");
        long[] wanted = new long[JobType.values().length];
        for (JobType j : JobType.values()) wanted[j.ordinal()] = small.getJobs(j);
        g.getPopulationManager().updateJobs(wanted);
        g.getPopulationManager().restoreWorkforce(small.getTotalJobs() - 1);
        double[] heads = new double[WageBand.values().length];
        heads[WageBand.of(JobType.DIPLOMA).ordinal()] = small.getJobs(JobType.DIPLOMA);
        g.getPopulationManager().restoreSkilledHeads(heads);
        double[] fill = g.getPopulationManager().getJobFillRate();
        NewUnit unit = NewUnit.of(g, small);
        double own = BuildAdvice.staffing(unit, fill), base = g.getServicesManager().getUtilitiesHandler().getFoundingGridKw();
        check("fixture: no power/water posts stand; the sourced station has five posts and a positive scarce fill below the old net-loss boundary",
                java.util.Arrays.stream(bm.getJobArrayPerCategory(BuildingType.ELECTRICITY)).sum() == 0
                        && java.util.Arrays.stream(bm.getJobArrayPerCategory(BuildingType.WATER)).sum() == 0
                        && small.getTotalJobs() == 5 && own > 0 && own < base / (base + unit.getProduction1()));
        double before = BuildAdvice.supplyDemand(g, POWER, Map.of(), BuildAdvice.NOW)[0];
        double preview = BuildAdvice.supplyDemand(g, POWER, Map.of(small, 1L), BuildAdvice.NOW)[0];
        check("the first small station's preview preserves the endowment and adds its resolved staffed power",
                close(before, base, 1e-12) && close(preview - before, unit.getProduction1() * own, 1e-12) && preview > before);
        g.setCashForTest(1e12);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt() + LandManager.SQ_FT_PER_KM2);
        Game.BuildResult[] placed = {null};
        quietly(() -> placed[0] = g.buildStack(small, 1, true));
        UtilitiesHandler actual = g.getServicesManager().getUtilitiesHandler();
        g.getServicesManager().updateJobFillRate(fill);
        g.getServicesManager().updateServiceWages(new double[fill.length]);
        g.getServicesManager().updateServices();
        check("the actual first station's staffed generation agrees with the preview, with the row and jobs intact",
                placed[0] == Game.BuildResult.SUCCESS && bm.getQuantity(small.getId()) == 1
                        && small.getTotalJobs() == 5 && close(actual.getProduction(), base + unit.getProduction1() * own, 1e-12)
                        && close(actual.getProduction(), preview, 1e-12));
        double standing = BuildAdvice.supplyDemand(g, POWER, Map.of(), BuildAdvice.NOW)[0];
        check("water and substation posts added to the planner cannot derate the founding grid or the existing plant",
                close(BuildAdvice.supplyDemand(g, POWER, Map.of(water, 1L), BuildAdvice.NOW)[0], standing, 1e-12)
                        && close(BuildAdvice.supplyDemand(g, POWER, Map.of(sub, 1L), BuildAdvice.NOW)[0], standing, 1e-12));
        for (double staffed : new double[] {0, .5, 1}) {
            double[] rates = new double[fill.length];
            java.util.Arrays.fill(rates, staffed);
            long[] posts = new long[fill.length];
            for (JobType j : JobType.values()) posts[j.ordinal()] = small.getJobs(j);
            for (long k : new long[] {1, 3}) {
                UtilitiesHandler nativePower = new UtilitiesHandler();
                nativePower.setFoundingEndowments(k);
                nativePower.setWattsProduction(small.getProduction1());
                nativePower.setWattsConsumption(nativePower.getFoundingGridKw() / PowerDispatch.PLAN_PEAK_HOUR * .9);
                nativePower.updateJobFillRate(rates);
                nativePower.updateUtilitiyWages(new double[fill.length], posts, new long[fill.length]);
                for (boolean catalogue : new boolean[] {false, true}) {
                    nativePower.setPlants(catalogue ? Map.of(small, 1L) : null);
                    nativePower.updateEnergyRatio();
                    double grid = nativePower.getLatestRows().stream().filter(r -> r.name.equals("grid")).mapToDouble(r -> r.kw).sum();
                    check("native " + (catalogue ? "catalogue" : "bare handler") + ", fill " + staffed + ", endowments " + k + ": fixed grid, staffed plant and the spread's same free grid",
                            close(grid, k * base, 1e-12)
                                    && close(nativePower.getProduction(), k * base + small.getProduction1() * staffed, 1e-12)
                                    && nativePower.yearRunning(Map.of()) == 0);
                }
                nativePower.setPlants(Map.of());
                nativePower.updateUtilitiyWages(new double[fill.length], new long[fill.length], posts);
                nativePower.updateEnergyRatio();
                check("water-only posts, fill " + staffed + ", endowments " + k + ": founding generation stays fixed",
                        close(nativePower.getProduction(), k * base, 1e-12));
            }
        }
    }

    /** Set only a site's progress to cause its native last-unit quote to be `months`; no construction predictor in the fixture. */
    static void dueIn(Game g, BuildingsTemplate t, double months) {
        BuildingsStacks s = g.getBuildingManager().getStack(t);
        double hi = s.pointsOnSite(), lo = -hi;
        while (true) {
            s.setConstructionProgress(lo);
            if (g.getBuildingManager().siteUnitMonths(t, s.getUnderConstruction(), g.getBuildingOutputAtEveryPost()) >= months) break;
            lo *= 2;
        }
        for (int i = 0; i < 48; i++) {
            double mid = (lo + hi) / 2;
            s.setConstructionProgress(mid);
            if (g.getBuildingManager().siteUnitMonths(t, s.getUnderConstruction(), g.getBuildingOutputAtEveryPost()) > months) lo = mid;
            else hi = mid;
        }
        s.setConstructionProgress((lo + hi) / 2);
    }

    /** 9c: isolate the small bridge and coal waiver menus; larger pending diesels still count physically while off the menu. */
    static void pendingDates(Path root) {
        out.println("--- 9c. pending generation and load are compared at the same dates, preserving interim reserve and seasonal needs ---");
        Game g = town(root, "pending-dates", "Small Steam Station", DIESEL);
        BuildingsTemplate small = plant(g, "Small Steam Station"), diesel = plant(g, DIESEL);
        BuildingsTemplate mill = g.getBuildingManager().getTemplate(HeavyIndustry.LARGE_MINI_MILL);
        quietly(() -> {
            g.buildStack(plant(g, BuildersFirstCheck.DEPOT), DEPOTS, true);
            g.buildStack(diesel, 3, false);
            g.buildStack(mill, 1, false);
        });
        know(g, small.getName());
        g.getBuildingManager().setTemplateKnownIn((t, months) -> t.getCategory() != BuildingType.ELECTRICITY
                || Substations.isSubstation(t) || t == small);
        g.getAutoBuilder().setSlack(0, null);
        g.getServicesManager().updateServices();
        double lead = g.quoteBuild(small, 1).months, horizon = BusinessInvestment.horizonFor(lead);
        double last = lead + .9 * horizon, after = lead + .95 * horizon, before = lead + .8 * horizon;
        dueIn(g, diesel, last);
        dueIn(g, mill, after);
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, POWER);
        BuildingManager bm = g.getBuildingManager();
        double first = bm.siteUnitMonths(diesel, 1, g.getBuildingOutputAtEveryPost());
        double actualLast = bm.siteUnitMonths(diesel, 3, g.getBuildingOutputAtEveryPost());
        double load = bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost());
        double unit = BuildAdvice.unit(g, POWER, small), gap = 1.2 * BuildAdvice.FIRST_WORK_SHARE * unit;
        // With two diesels open, the mill's load leaves a useful but sub-unit reserve gap, covered by the third diesel.
        BuildAdvice.Ahead p = new BuildAdvice.Ahead(after + .01, g.getBusinessInvestment().growthFactor(after + .01), 0);
        double have = BuildAdvice.supplyDemand(g, POWER, Map.of(diesel, 2L), p)[0];
        drawGap(g, p, have + gap - BuildAdvice.supplyDemand(g, POWER, Map.of(), p)[0]);
        check(String.format("9c fixture: only small steam offered; pending diesels first %.3f < lead %.3f < last %.3f < load %.3f < Ahead %.3f, flat growth and native load %.0f kW",
                        first, lead, actualLast, load, lead + horizon, WorksValue.pendingPowerLoad(g, after + .01)),
                first >= 0 && first < lead && lead < actualLast && actualLast < load && load < lead + horizon
                        && close(p.k(), 1, 1e-9) && WorksValue.pendingPowerLoad(g, after + .01) > 0
                        && !bm.isTemplateKnown(diesel) && bm.isTemplateKnown(small));
        double baseDraw = g.getServicesManager().getUtilitiesHandler().getConsumption();
        for (String promise : new String[] {"absent", "expired", "live"}) {
            AutoBuilder.State state = g.getAutoBuilder().toState();
            state.pendingUntil.clear();
            if (!promise.equals("absent")) state.pendingUntil.put(POWER.label(), g.getMonth() + (promise.equals("live") ? first : -1));
            g.getAutoBuilder().restore(state);
            check("9c fixture: " + promise + " first-unit promise keeps the physical three-diesel site",
                    BuildAdvice.units(g.getAutoBuilder().pendingCapacity(g, POWER)) == 3
                            && (promise.equals("live") ? Double.isFinite(g.getAutoBuilder().promisedMonths(g, POWER))
                            : Double.isNaN(g.getAutoBuilder().promisedMonths(g, POWER))));
            dueIn(g, diesel, last);
            dueIn(g, mill, after);
            g.getServicesManager().getUtilitiesHandler().setWattsConsumption(baseDraw);
            double[] early = BuildAdvice.supplyDemand(g, POWER, WorksValue.openBy(g, site, Math.nextDown(actualLast)),
                    new BuildAdvice.Ahead(Math.nextDown(actualLast), 1, 0));
            double[] start = BuildAdvice.supplyDemand(g, POWER, WorksValue.openBy(g, site, lead), new BuildAdvice.Ahead(lead, 1, 0));
            double[] loaded = BuildAdvice.supplyDemand(g, POWER, WorksValue.openBy(g, site, after + .01), new BuildAdvice.Ahead(after + .01, 1, 0));
            check("9c A premise (" + promise + "): reserve covered at completion, before the last generator and after the load",
                    start[0] >= start[1] && early[0] >= early[1] && loaded[0] >= loaded[1]);
            BuildAdvice.Suggestion covered = suggest(g);
            check("9c A (" + promise + "): matched covered dates add no reserve generators (" + named(covered) + ")", covered == null);

            dueIn(g, mill, before);
            double jump = bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost());
            BuildAdvice.Ahead at = new BuildAdvice.Ahead(jump + .01, 1, 0);
            Map<BuildingsTemplate, Long> opened = WorksValue.openBy(g, site, at.months());
            double[] sd = BuildAdvice.supplyDemand(g, POWER, opened, at);
            long bridge = BuildAdvice.count(g, POWER, opened, Map.of(small, 1L), at, BuildAdvice.MOST, true)[0];
            double[] frozen = BuildAdvice.supplyDemand(g, POWER, WorksValue.openBy(g, site, lead), new BuildAdvice.Ahead(lead + horizon, 1, 0));
            long falseOrder = BuildAdvice.count(g, POWER, WorksValue.openBy(g, site, lead), Map.of(small, 1L),
                    new BuildAdvice.Ahead(lead + horizon, 1, 0), BuildAdvice.MOST, true)[0];
            check(String.format("9c B premise (%s): move only load to %.3f, reserve gap %.1f >= share %.1f, seasonal covered, bridge %d < frozen %d (gap %.1f)",
                            promise, jump, sd[1] - sd[0], BuildAdvice.FIRST_WORK_SHARE * bridge * unit, bridge, falseOrder, frozen[1] - frozen[0]),
                    lead < jump && jump < actualLast && sd[0] >= sd[1] * SEASONAL
                            && sd[1] - sd[0] >= BuildAdvice.FIRST_WORK_SHARE * bridge * unit && bridge > 0 && bridge < falseOrder);
            BuildAdvice.Suggestion s = suggest(g);
            check("9c B (" + promise + "): quantity, actual quote and binding evaluation date are coherent",
                    s == null || close(s.lead(), g.quoteBuild(s.template(), s.count()).months, 1e-9)
                            && s.ahead().months() >= s.lead() && s.ahead().months() <= BuildAdvice.opening(g, s.lead(), 0).months());
            check("9c B (" + promise + "): sufficient smaller reserve-only bridge survives covered Ahead (" + named(s) + ")",
                    s != null && s.template() == small && s.count() == bridge && s.lead() < jump
                            && BuildAdvice.supplyDemand(g, POWER, BuildAdvice.plus(opened, small, s.count()), at)[0] >= sd[1]);

            // Isolate coal for the waiver: its oversized order's reserve gap is below FIRST_WORK_SHARE, but the seasonal gap is real.
            BuildingsTemplate coal = plant(g, COAL);
            know(g, COAL);
            bm.setTemplateKnownIn((t, months) -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || t == coal);
            double coalLead = g.quoteBuild(coal, 1).months;
            dueIn(g, diesel, coalLead + BusinessInvestment.horizonFor(coalLead) / 2);
            dueIn(g, mill, lead / 2);
            Map<BuildingsTemplate, Long> byLead = WorksValue.openBy(g, site, coalLead);
            double firm = BuildAdvice.supplyDemand(g, POWER, byLead, completion(g, coalLead))[0];
            drawGap(g, completion(g, coalLead), firm / SEASONAL * 1.01
                    - BuildAdvice.supplyDemand(g, POWER, Map.of(), completion(g, coalLead))[0]);
            double[] done = BuildAdvice.supplyDemand(g, POWER, byLead, completion(g, coalLead));
            check("9c C premise (" + promise + "): actual seasonal gap at completion, reserve gap below the isolated coal row's share",
                    done[0] < done[1] * SEASONAL && done[1] - done[0] < BuildAdvice.FIRST_WORK_SHARE * BuildAdvice.unit(g, POWER, coal));
            s = suggest(g);
            check("9c C (" + promise + "): real early seasonal gap remains served (" + named(s) + ")",
                    s != null && s.template() == coal && !BuildAdvice.seasonalShort(g, POWER, BuildAdvice.plus(byLead, coal, s.count()), s.lead()));

            know(g, small.getName());
            bm.setTemplateKnownIn((t, months) -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || t == small);
            g.getServicesManager().getUtilitiesHandler().setWattsConsumption(baseDraw);
            dueIn(g, mill, after);
            dueIn(g, diesel, lead + horizon + 1);
            double beyond = bm.siteUnitMonths(diesel, 3, g.getBuildingOutputAtEveryPost());
            double[] end = BuildAdvice.supplyDemand(g, POWER, WorksValue.openBy(g, site, lead + horizon), new BuildAdvice.Ahead(lead + horizon, 1, 0));
            check("9c D premise (" + promise + "): last plant after Ahead leaves a useful reserve gap", beyond > lead + horizon && end[1] > end[0]
                    && end[1] - end[0] >= BuildAdvice.FIRST_WORK_SHARE * unit
                    && BuildAdvice.supplyDemand(g, POWER, site, new BuildAdvice.Ahead(lead + horizon, 1, 0))[0] >= end[1]);
            s = suggest(g);
            check("9c D (" + promise + "): generator after Ahead cannot count early (" + named(s) + ")",
                    s != null && s.template() == small && s.count() == bridge);
        }
        dueIn(g, diesel, last);
        dueIn(g, mill, after);
        g.getServicesManager().getUtilitiesHandler().setWattsConsumption(baseDraw);
        BuildAdvice.Suggestion s = suggest(g);
        double cash = g.getCash();
        long units = BuildAdvice.units(BuildAdvice.onSite(g, POWER));
        if (s != null) quietly(() -> g.getAutoBuilder().placeForTest(g, POWER, s, s.count()));
        check("9c A: the covered power order path spends nothing and adds no units", g.getCash() == cash
                && BuildAdvice.units(BuildAdvice.onSite(g, POWER)) == units);
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
        holds(root);
        holdsPower(root);
        millUnderConstruction(root);
        earlyTown(root);
        foundingGrid(root);
        pendingDates(root);
        out.println("--- 10. how often each fires ---");
        out.printf("this harness's suggestions: reserve test %d, spread %d (the default run's: LongPlaytest, \"the power planner (PW3)\")%n",
                BuildAdvice.RESERVE_ORDERS.get(), BuildAdvice.SPREAD_ORDERS.get());
        check("the reserve test and the spread each fired here", BuildAdvice.RESERVE_ORDERS.get() > 0 && BuildAdvice.SPREAD_ORDERS.get() > 0);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
