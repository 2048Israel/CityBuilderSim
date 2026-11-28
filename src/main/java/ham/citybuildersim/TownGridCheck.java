package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Power for a small town (batch ASH1).
 *
 * WHAT THIS HAS TO PROVE. A town that grows past its founding 10 MW grid used to read "204% served" in green on NEEDS YOU while the
 * wires shed 4.6% of its month, and was sold a 100 MW Substation for a 5 MW need; the grid units' crews were paid whole at a thousandth
 * of the load. Each of these fails on the model before ASH1:
 *
 *   1. THE POWER ROW READS THE LESSER OF THE PLANTS AND THE WIRES. Plants well over the peak and wires under it: the row is red, is
 *      NEEDS YOU's, says "by the wires" and is advised on the grid's units; the plants' share read alone is over 100%. Distribution
 *      Substations on site turn it amber, a plant on site does not; and the wires built past the plants' share give the row back to
 *      the plants, green.
 *
 *   2. A TOWN THAT GROWS PAST 10 MW IN SMALL STEPS IS GIVEN DISTRIBUTION SUBSTATIONS. The automatic builder's every grid order is the
 *      5 MW unit, never the 100 MW one; each order covers the demand the advice sized it for and one unit fewer would not; and once
 *      the orders have opened the carry covers the peak.
 *
 *   3. THE GRID UNITS' POSTS FOLLOW THE LOAD THE WIRES CARRY. On a fixed grid, as the draw rises the share used rises and the posts
 *      offered with it, each type at postsOffered(raw posts, share); past the carry every post is offered; the labour market is
 *      offered every post less exactly the grid's withheld; and the payroll is struck on the posts offered.
 *
 *   4. THE MONTHS ON THE WAY ARE THE SITE'S PACE. A 100 MW Substation among many small sites that the month's water-fill caps: the
 *      rule's wait reads a month or more longer than the pace, the POWER row reads the pace (owed points over the points the plan
 *      gives the site), and the month then advances the site by exactly those points.
 *
 *   5. THE MONEY AUDIT STAYS EXACT to the cent over section 2's months.
 *
 * Every fixture CAUSES its condition rather than waiting for it, and asserts against the model's own constants and getters.
 */
public final class TownGridCheck {
    static int fails;
    static final PrintStream OUT = System.out, QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) { OUT.println((ok ? "  OK   " : "  FAIL ") + label); if (!ok) fails++; }
    static boolean near(double a, double b, double relative) { return Math.abs(a - b) <= relative * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }
    static void quietly(Runnable work) { System.setOut(QUIET); try { work.run(); } finally { System.setOut(OUT); } }

    static final BuildAdvice.Measure GRID = BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), POWER = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);

    /** A founded city with cash and ground to spare, and nothing built of the grid: the founding 10 MW is its wires. */
    static Game city(String label) {
        Game g = new Game(GameFiles.scratch("towngrid-" + label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            g.getServicesManager().updateServices();
        });
        return g;
    }

    static BuildingsTemplate named(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new AssertionError("the catalogue has no " + name);
        return t;
    }

    /** The grid's pair at today's demand: the wires' carry and the peak asked of them. */
    static double[] wires(Game g) { return BuildAdvice.supplyDemand(g, GRID, Map.of()); }

    /** The plants' pair at today's demand. */
    static double[] plants(Game g) { return BuildAdvice.supplyDemand(g, POWER, Map.of()); }

    static CityNeeds.Need powerRow(Game g) {
        for (CityNeeds.Need n : CityNeeds.measure(g, CityNeeds.PLAIN)) if (n.kind() == CityNeeds.Kind.POWER) return n;
        return null;
    }

    /** The town's water works, a municipal building that stands whatever the businesses do and draws power by its template: stock added until the grid's peak is over `kw`. */
    static void drawPast(Game g, double kw) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate works = named(g, "Water Treatment Plant");
        quietly(() -> {
            double before = wires(g)[1];
            bm.addStack(works, 1, true);
            g.getServicesManager().updateServices();
            double each = wires(g)[1] - before;
            if (!(each > 0)) throw new AssertionError("the water works draw nothing");
            long more = (long) Math.ceil((kw - wires(g)[1]) / each);
            if (more > 0) bm.addStack(works, more, true);
            g.getServicesManager().updateServices();
        });
    }

    /** Plants over the peak: Coal Power Plants (the players' first plant) stood until the plants' served share is `times` the peak, however thinly the town staffs them. */
    static void plantsOver(Game g, double times) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate coal = named(g, "Coal Power Plant");
        quietly(() -> {
            for (int guard = 0; guard < 40 && CityNeeds.servedShare(plants(g)[0], plants(g)[1]) < times; guard++) {
                bm.addStack(coal, 1, true);
                g.getServicesManager().updateServices();
            }
        });
    }

    /** A town drawing 1.3 times the founding grid at its peak, with plants twice that: its plants serve it and its wires do not. */
    static Game shortTown(String label) {
        Game g = city(label);
        double founding = g.getServicesManager().getUtilitiesHandler().getFoundingGridKw();
        drawPast(g, 1.3 * founding);
        plantsOver(g, 2);
        return g;
    }

    /** A short town (as shortTown()) that has lived `months` months with crews raised, so that its labour, builders and population have settled to the figures a month then keeps. */
    static Game settledTown(String label, int months, int crews) {
        Game g = shortTown(label);
        BuildingManager bm = g.getBuildingManager();
        quietly(() -> {
            for (int guard = 0; guard < 40 && g.getBuildingOutput() < crews; guard++) bm.addStack(named(g, "Construction Depot"), 25, true);
            g.getServicesManager().updateServices();
            g.simulateMonths(months);
        });
        return g;
    }

    /* ======================================================================== 1 ======================================================================== */

    static void theRow() {
        OUT.println("--- 1. the POWER row reads the lesser of the plants and the wires ---");
        Game g = shortTown("row");
        BuildingManager bm = g.getBuildingManager();
        double[] w = wires(g), p = plants(g);
        double founding = g.getServicesManager().getUtilitiesHandler().getFoundingGridKw();
        OUT.printf("   peak %.0f kW; the wires carry %.0f of it (%.0f%%), the plants %.0f (%.0f%%); the founding grid %.0f%n", w[1], w[0],
                CityNeeds.servedShare(w[0], w[1]) * 100, p[0], CityNeeds.servedShare(p[0], p[1]) * 100, founding);
        check("fixture: the town draws past the founding grid (a peak of " + Math.round(w[1]) + " kW over " + Math.round(founding) + " kW) and the wires are the founding grid's",
                w[1] > founding && w[0] == founding);
        check("fixture: the plants' own served share, read alone, is over 100% (" + Math.round(CityNeeds.servedShare(p[0], p[1]) * 100) + "%)",
                CityNeeds.servedShare(p[0], p[1]) > 1);
        check("fixture: ...and the wires' is under it (" + Math.round(CityNeeds.servedShare(w[0], w[1]) * 100) + "%)", CityNeeds.servedShare(w[0], w[1]) < 1);

        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        CityNeeds.Need row = powerRow(g);
        check("the POWER row is red where the wires shed load although the plants serve the peak: \"" + (row == null ? null : row.reading()) + "\"", row != null && row.level() == 2);
        check("...NEEDS YOU counts it", row != null && CityNeeds.biting(all).contains(row));
        check("...its reading ends with " + CityNeeds.BY_THE_WIRES.trim() + " and it reads the wires' pair", row != null && row.wires() && row.reading().endsWith(CityNeeds.BY_THE_WIRES)
                && row.a() == w[0] && row.b() == w[1]);
        check("...and its advice is measured on the grid's units (BuildAdvice.measureOf)", row != null && BuildAdvice.measureOf(row).equals(GRID));

        // The wires built, a unit at a time: the row stays the wires' while they serve a lesser share than the plants, and is the plants' after.
        BuildingsTemplate unit = bm.getTemplate(Substations.DISTRIBUTION_ID);
        long units = 0, covered = 0;
        boolean wiresWhileCovered = true;
        for (int guard = 0; guard < 400; guard++) {
            bm.addStack(unit, 1, true);
            units++;
            row = powerRow(g);
            w = wires(g); p = plants(g);
            if (covered == 0 && w[0] >= w[1]) {
                covered = units;
                wiresWhileCovered = row.wires() && CityNeeds.servedShare(w[0], w[1]) < CityNeeds.servedShare(p[0], p[1]);
                OUT.printf("   %d units carry %.0f kW over the peak %.0f: the row reads \"%s\" (%s)%n", units, w[0], w[1], row.reading(), row.wires() ? "the wires" : "the plants");
            }
            if (!row.wires()) break;
        }
        OUT.printf("   %d Distribution Substations: the wires carry %.0f kW (%.0f%%) of the peak %.0f, the plants %.0f%%; the row reads \"%s\"%n", units, w[0],
                CityNeeds.servedShare(w[0], w[1]) * 100, w[1], CityNeeds.servedShare(p[0], p[1]) * 100, row.reading());
        check("the wires carrying the peak is not yet the plants' row while they serve the lesser share (the unit that first covers it, " + covered + ", still reads the wires)",
                covered > 0 && wiresWhileCovered);
        check("built until the wires serve the peak past the plants' share, the row is green, reads the plants' pair and no longer says it is the wires'",
                !row.wires() && row.level() == 0 && !row.reading().contains(CityNeeds.BY_THE_WIRES) && row.a() == p[0] && row.b() == p[1]
                        && CityNeeds.servedShare(w[0], w[1]) >= CityNeeds.servedShare(p[0], p[1]) && w[0] > w[1]);
        check("...and its advice is measured on the plants again (BuildAdvice.measureOf)", BuildAdvice.measureOf(row).equals(POWER) && !CityNeeds.biting(CityNeeds.measure(g, CityNeeds.PLAIN)).contains(row));

        // On the way: a twin in the same shortage.
        Game twin = shortTown("row-site");
        BuildingManager tb = twin.getBuildingManager();
        quietly(() -> tb.addStack(named(twin, "Coal Power Plant"), 1, false));
        CityNeeds.Need withPlant = powerRow(twin);
        check("a plant on site does not answer the wires: the row stays red with nothing on the way (level " + withPlant.level() + ", " + withPlant.onSite() + " on the way)",
                withPlant.wires() && withPlant.level() == 2 && withPlant.onSite() == 0);
        quietly(() -> tb.addStack(tb.getTemplate(Substations.DISTRIBUTION_ID), 1, false));
        CityNeeds.Need withUnit = powerRow(twin);
        check("a Distribution Substation on site turns the red row amber, \"" + withUnit.reading() + "\"",
                withUnit.wires() && withUnit.level() == 1 && withUnit.onSite() == 1 && withUnit.reading().contains("on the way"));
    }

    /* ======================================================================== 2 ======================================================================== */

    /** One month of the growing town: what the pass saw and ordered, and what the month's money audit left. */
    record Month(int month, double peak, double carry, long standing, long onSite, long ordered, double need, double coveredAfter, double residual, boolean substation) { }

    static final List<Month> GROWTH = new ArrayList<>();
    static long growthSubstations;
    static double growthSlack;

    /** Stock added a month: the town's water works. */
    static final int FIRST_WORKS = 4, WORKS_A_MONTH = 3, GROWING_MONTHS = 8, SETTLING_MONTHS = 24, MOST_MONTHS = 70, WARM_MONTHS = 10;

    static void growth() {
        OUT.println("--- 2. a town grows past 10 MW in small steps ---");
        Game g = city("growth");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate works = named(g, "Water Treatment Plant"), unit = bm.getTemplate(Substations.DISTRIBUTION_ID);
        AutoBuilder auto = g.getAutoBuilder();
        // The plants stand over the town's peak and the crews are ample, so that the wires are the one thing the automatic builder is asked for.
        quietly(() -> {
            bm.addStack(named(g, "Coal Power Plant"), 3, true);
            for (int guard = 0; guard < 40 && g.getBuildingOutput() < 3000; guard++) bm.addStack(named(g, "Construction Depot"), 25, true);
            bm.addStack(works, FIRST_WORKS, true);
            g.getServicesManager().updateServices();
            g.simulateMonths(WARM_MONTHS);
        });
        quietly(() -> { auto.setSlack(0, null); auto.setOn(true, null); });
        growthSlack = auto.getSlack();
        double[] seen = new double[3];
        long[] before = new long[1];
        BuildAdvice.Suggestion[] card = new BuildAdvice.Suggestion[1];
        g.autoBuildProbeForTest = after -> {
            if (!after) return;
            // Just after the month's pass: what it ordered, and whether that covers what the advice sized it for.
            long site = bm.getUnderConstructionById()[Substations.DISTRIBUTION_ID];
            double need = card[0] == null ? 0 : BuildAdvice.supplyDemand(g, GRID, Map.of(unit, before[0]), card[0].ahead())[1];
            double cover = card[0] == null ? 0 : BuildAdvice.supplyDemand(g, GRID, Map.of(unit, site), card[0].ahead())[0];
            seen[0] = site - before[0];
            seen[1] = need;
            seen[2] = cover;
        };
        OUT.printf("   %-5s %-10s %-10s %-9s %-7s %-8s %-10s %s%n", "month", "peak kW", "carry kW", "standing", "on site", "ordered", "wanted kW", "residual");
        // The draw stops rising after GROWING_MONTHS; the months run on until every order has opened (and at least SETTLING_MONTHS), within MOST_MONTHS.
        for (int i = 0; i < MOST_MONTHS && (i < GROWING_MONTHS + SETTLING_MONTHS || bm.getUnderConstructionById()[Substations.DISTRIBUTION_ID] > 0); i++) {
            if (i < GROWING_MONTHS) quietly(() -> { bm.addStack(works, WORKS_A_MONTH, true); g.getServicesManager().updateServices(); });
            card[0] = null;
            for (BuildAdvice.Suggestion s : BuildAdvice.suggest(g)) if (s.measure().equals(GRID)) card[0] = s;
            before[0] = bm.getUnderConstructionById()[Substations.DISTRIBUTION_ID];
            seen[0] = seen[1] = seen[2] = 0;
            double peak = wires(g)[1];
            quietly(() -> g.simulateMonths(1));
            MoneyAudit.Result audit = g.getLastMoneyAudit();
            List<AutoBuilder.Step> steps = auto.steps();
            boolean small = true, ordered = false;
            for (AutoBuilder.Step s : steps) {
                if (!s.measure().equals(GRID) || s.ordered() <= 0) continue;
                ordered = true;
                small &= unit.getName().equals(s.building());
            }
            long stray = bm.getUnderConstructionById()[Substations.ID] + bm.getQuantity(Substations.ID);
            GROWTH.add(new Month(g.getMonth(), peak, Substations.costs(g).carryKw(), bm.getQuantity(Substations.DISTRIBUTION_ID),
                    bm.getUnderConstructionById()[Substations.DISTRIBUTION_ID], (long) seen[0], seen[1], seen[2], audit.residual, ordered && !small || stray > 0));
            Month m = GROWTH.get(GROWTH.size() - 1);
            OUT.printf("   %-5d %-10.0f %-10.0f %-9d %-7d %-8d %-10.0f %-10.0f %.3g%n", m.month(), m.peak(), m.carry(), m.standing(), m.onSite(), m.ordered(), m.need(), m.coveredAfter(), m.residual());
        }
        g.autoBuildProbeForTest = null;
        growthSubstations = bm.getQuantity(Substations.ID) + bm.getUnderConstructionById()[Substations.ID];

        double founding = g.getServicesManager().getUtilitiesHandler().getFoundingGridKw();
        double first = GROWTH.get(0).peak, top = GROWTH.stream().mapToDouble(Month::peak).max().orElse(0);
        check(String.format("fixture: the town starts under the founding grid and grows to its peak of %.0f kW (from %.0f kW, founding grid %.0f kW)", top, first, founding),
                first < founding && top > 2.5 * founding);
        long grid = GROWTH.stream().filter(m -> m.ordered() > 0).count();
        check("fixture: the automatic builder ordered the grid in " + grid + " months", grid >= 2);
        check("every grid order is Distribution Substations, and no 100 MW Substation is ever ordered, on site or built",
                GROWTH.stream().noneMatch(Month::substation) && growthSubstations == 0);
        boolean covers = true, least = true;
        for (Month m : GROWTH) {
            if (m.ordered() <= 0) continue;
            covers &= m.coveredAfter() >= m.need();
            // One unit fewer would not have covered it, with the slack the fixture set on the advice's margin.
            least &= m.coveredAfter() - m.need() < Substations.DISTRIBUTION_MW * 1000 + growthSlack * m.need();
        }
        // Where this is red: AutoBuilder.pendingCapacity() drops a grid order's units from what the advice counts once the first quote's deadline has passed
        // (HW1; only power's is exempt), so a town whose builders open one unit a month is ordered the grid again while its units are still on site.
        check("each order covers the demand the advice sized it for (BuildAdvice.supplyDemand at the card's own horizon)", covers);
        check("...and the carry never runs more than a unit (" + Substations.DISTRIBUTION_MW + " MW) and the slack (" + growthSlack + ") over it", least);
        Month last = GROWTH.get(GROWTH.size() - 1);
        double highest = Substations.peak(g.getServicesManager().getUtilitiesHandler().getConsumption(), java.util.Arrays.stream(PowerDispatch.SEASON).max().orElseThrow());
        OUT.printf("   at the end %d units stand, %d on site: the carry %.0f kW against the year's highest peak %.0f kW%n", last.standing(), last.onSite(), last.carry(), highest);
        check("once the orders have opened (none on site) the carry covers the peak of the year's highest month", last.onSite() == 0 && last.carry() >= highest);
    }

    /* ======================================================================== 3 ======================================================================== */

    static void posts() {
        OUT.println("--- 3. the grid units' posts follow the load ---");
        Game g = city("posts");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate district = bm.getTemplate(Substations.ID), unit = bm.getTemplate(Substations.DISTRIBUTION_ID);
        quietly(() -> { bm.addStack(district, 1, true); bm.addStack(unit, 4, true); g.getServicesManager().updateServices(); });
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double carry = Substations.costs(g).carryKw();
        double[] asked = {.1, .25, .5, .75, .95, 1.2};
        double lastUsed = -1;
        long[] lastOffered = new long[JobType.values().length];
        boolean rises = true, each = true, labour = true, paid = true, first = true, strictly = true, moved = false;
        long[] every = new long[JobType.values().length];
        for (JobType j : JobType.values()) every[j.ordinal()] = bm.getQuantity(Substations.ID) * district.getJobs(j) + bm.getQuantity(Substations.DISTRIBUTION_ID) * unit.getJobs(j);
        for (double share : asked) {
            // Two dispatches: the first puts the load on the wires, the share is struck from it as a month's turn strikes it (Game.nextMonth(),
            // UtilitiesHandler.strikeWiresShare()), and the second captures the grid units' crew at that share.
            SubstationCheck.dispatch(g, share * carry).strikeWiresShare();
            SubstationCheck.dispatch(g, share * carry);
            double used = u.getWiresShare();
            double wage = 0, full = 0;
            double[] wages = g.getPopulationManager().getWagesPerType(), fill = g.getPopulationManager().getJobFillRate();
            boolean growing = true, past = true;
            for (JobType j : JobType.values()) {
                long offered = bm.getGridPostsOffered(j), expected = BuildingManager.postsOffered(every[j.ordinal()], used);
                each &= offered == expected;
                growing &= offered >= lastOffered[j.ordinal()];
                moved |= offered > lastOffered[j.ordinal()];
                // The labour market is offered every post less the grid's withheld, nobody else withholding any here.
                labour &= bm.getTotalJobsAtEveryPost(j) - bm.getTotalJobs(j) == every[j.ordinal()] - expected && bm.getPostsWithheld(j) == every[j.ordinal()] - expected;
                past &= offered == every[j.ordinal()];
                wage += expected * wages[j.ordinal()] * fill[j.ordinal()];
                full += every[j.ordinal()] * wages[j.ordinal()] * fill[j.ordinal()];
                lastOffered[j.ordinal()] = offered;
            }
            rises &= used >= lastUsed && growing;
            strictly &= used > lastUsed;
            paid &= near(Substations.costs(g).payroll(), wage, 1e-9) && near(u.getStruckBook().gridCosts.payroll(), wage, 1e-9);
            if (share >= 1) paid &= near(wage, full, 1e-9);
            lastUsed = used;
            OUT.printf("   asked %3.0f%% of the carry: the wires used %.6f; diploma posts offered %d of %d, withheld %d; payroll %.6f of %.6f%n", share * 100, used,
                    bm.getGridPostsOffered(JobType.DIPLOMA), every[JobType.DIPLOMA.ordinal()], bm.getPostsWithheld(JobType.DIPLOMA), wage, full);
            if (share < 1) first &= bm.getGridPostsOffered(JobType.DIPLOMA) < every[JobType.DIPLOMA.ordinal()];
            if (share >= 1) {
                check("a draw past the carry (" + Math.round(share * 100) + "% of it) uses the wires whole, 1, and every post is offered", used == 1 && past);
            }
        }
        check("as the draw rises the share of the wires used rises with it, every month, until the carry is all used", rises && strictly);
        check("the grid units' offered posts rise with it, each job type at postsOffered(its posts, the share used), and fewer than every post while the load is light", each && moved && first);
        check("the labour market's posts (getTotalJobs) differ from every post by exactly the grid's withheld, in every job type and every month", labour);
        check("Substations.costs().payroll() and the struck book's are paid on the posts offered, each at its wage and fill, and the whole crew's once the carry is used", paid);
    }

    /* ======================================================================== 4 ======================================================================== */

    /** The share of its points a small site has built when the big one is ordered: it owes the least of its month, however large its weight on the crews (BuildingManager.siteShares()). */
    static final double NEARLY_DONE = .99;

    /** The months the town lives before section 4's site is ordered, and the output its crews are raised to at founding: a town whose labour, builders and population hold the figures a month keeps. */
    static final int PACE_WARM_MONTHS = 36, PACE_CREWS = 30_000, SMALL_SITES = 36;

    static void pace() {
        OUT.println("--- 4. the months on the way are the site's pace ---");
        // The reading is the month's plan as the advance takes it (Game.sitesPlan()): the city's crews and the foreign contractor's month as last settled
        // (Construction.getForeignPointsThisMonth()), SimulationEngine adding his month (Game.foreignCrewsOffer()) to the sites' pool. Two towns, warmed alike with him on
        // (a town that never had him, BuildingManager.buildersShortOn off from the founding, has no builders' output at all): the one turned off and run a month, so his
        // last settled month is none and the reading is the advance to the bit; the other left on, his month changing between the reading and the advance, the reading
        // within that change.
        boolean contractor = BuildingManager.buildersShortOn;
        try {
            BuildingManager.buildersShortOn = true;
            Game on = settledTown("pace-on", PACE_WARM_MONTHS, PACE_CREWS), off = settledTown("pace-off", PACE_WARM_MONTHS, PACE_CREWS);
            BuildingManager.buildersShortOn = false;
            quietly(() -> off.simulateMonths(1));
            pace(off, false);
            BuildingManager.buildersShortOn = true;
            pace(on, true);
            noHomeOutput();
        } finally { BuildingManager.buildersShortOn = contractor; }
    }

    /**
     * REV-ASH1 3: a town whose own crews give the sites nothing, standing roads' repairs taking every point of the builders' month (the trap,
     * ConstructionTrapCheck), while the foreign contractor's crews build: the right panel and the construction page decided "stalled" or
     * "waiting" on the city's crews alone, beside the finite months the card and the overview read. Their decision is Game.siteLeft(), on the
     * month's plan as the advance takes it, and it says the site is built on, at the same months as every other screen.
     */
    static void noHomeOutput() {
        OUT.println("   -- no home output, the contractor's crews at work --");
        Game g = settledTown("pace-abroad", PACE_WARM_MONTHS, 0);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate big = bm.getTemplate(Substations.ID);
        // The Substation is ordered and a month run, his crews' month settled on it; then standing Paved Roads as ConstructionTrapCheck lays them, as the
        // player lays them between the presses, until their repairs take the builders' whole month: the roads raise the road ratio and with it the
        // builders' month (ServicesManager.updateServices()), so they are laid again until the month after the repairs stays at nothing.
        BuildingsTemplate road = named(g, DeathCareFirstCheck.ROAD);
        double perRoad = road.getConstructionPoints() * road.repairsPerYear() / 12;
        quietly(() -> {
            bm.addStack(big, 1, false);
            g.getServicesManager().updateServices();
            g.simulateMonths(1);
            for (int guard = 0; guard < 20 && g.getBuildingOutputAtEveryPost() > 0; guard++) {
                if (g.buildStack(road, (int) Math.ceil(g.getBuildingOutputAtEveryPost() / perRoad) + 1, true) != Game.BuildResult.SUCCESS)
                    throw new AssertionError("fixture roads did not stand");
                g.getServicesManager().updateServices();
            }
        });
        String key = ConstructionControl.keyOf(big);
        double settled = g.getSectors().construction().getForeignPointsThisMonth();
        BuildingManager.Plan home = bm.plan(g.getBuildingOutput()), plan = g.sitesPlan();
        BuildingsStacks site = bm.getStack(big);
        OUT.printf("   the city's crews give the sites %d points (at every post %d); his month as last settled %.0f points; the plan gives the %s %.0f of %.0f owed%n",
                g.getBuildingOutput(), g.getBuildingOutputAtEveryPost(), settled, big.getName(), plan.workOf(key), site.owedPoints());
        check("fixture: the city's own crews give the sites nothing (the city's plan gives the Substation no share and no rule's share), while his crews' month as last settled is "
                        + Math.round(settled) + " points and the plan as the advance takes it works the site",
                g.getBuildingOutput() == 0 && home.shareOf(key) <= 0 && home.ruleOf(key) <= 0 && settled > 0 && plan.workOf(key) > 0
                && site.getUnderConstruction() == 1 && bm.getControl().abroadOf(key) == null);
        Game.SiteLeft left = g.siteLeft(key, plan);
        CityNeeds.Need row = powerRow(g);
        BuildCard.Figures card = BuildCard.of(g, big, GRID);
        double page = g.sitePaceMonths(key, g.sitesPlan()), pace = site.owedPoints() / plan.workOf(key);
        OUT.printf("   the right panel's line: %s at %.6f months; the overview %.6f, the build card %.6f, the construction page %.6f; the pace %.6f%n", left.progress(), left.months(),
                row == null ? Double.NaN : row.onSiteMonths(), card.siteMonths(), page, pace);
        check("the right panel's line and the construction page's status (Game.siteLeft(), the getter both call) say the site is built on, at finite months, the site's pace on his crews' work",
                left.progress() == Game.SiteProgress.BUILDING && Double.isFinite(left.months()) && near(left.months(), pace, 1e-9));
        check("...the same months as the overview's, the build card's and the construction page's, to the bit",
                row != null && row.onSite() == 1 && Double.doubleToRawLongBits(left.months()) == Double.doubleToRawLongBits(row.onSiteMonths())
                && Double.doubleToRawLongBits(left.months()) == Double.doubleToRawLongBits(card.siteMonths()) && Double.doubleToRawLongBits(left.months()) == Double.doubleToRawLongBits(page));
    }

    static void pace(Game g, boolean contractorOn) {
        String case_ = contractorOn ? "contractor on" : "contractor off";
        OUT.println("   -- " + case_ + " --");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate big = bm.getTemplate(Substations.ID), small = bm.getTemplate(Substations.DISTRIBUTION_ID), depot = named(g, "Construction Depot");
        quietly(() -> {
            // The wires are short (the town draws past the founding grid) and a 100 MW Substation is ordered, among small sites that are nearly built.
            bm.addStack(big, 1, false);
            int sites = 0;
            for (BuildingsTemplate t : bm.getTemplates()) {
                if (sites >= SMALL_SITES) break;
                boolean cheap = t.getConstructionPoints() > 0 && t.getConstructionPoints() < small.getConstructionPoints();
                if (!cheap || t.getCategory() == BuildingType.ELECTRICITY || t.getId() == depot.getId() || t.getCare() == CareType.BURIAL) continue;
                if (bm.getStack(t) != null && bm.getStack(t).getUnderConstruction() > 0) continue;
                bm.addStack(t, 1, false);
                bm.getStack(t).setConstructionProgress(NEARLY_DONE * bm.getStack(t).pointsOnSite());
                sites++;
            }
            g.getServicesManager().updateServices();
        });
        ham.citybuildersim.sectors.Construction builders = g.getSectors().construction();
        double settled = builders.getForeignPointsThisMonth();
        OUT.printf("   his month as last settled: %.0f points%n", settled);
        check("fixture: his month as last settled is " + (contractorOn ? "some points (" + Math.round(settled) + ")" : "none") + ", the contractor " + (contractorOn ? "on" : "off"),
                contractorOn ? settled > 0 : settled == 0);
        String key = ConstructionControl.keyOf(big);
        BuildingsStacks site = bm.getStack(big);
        BuildingManager.Plan plan = g.sitesPlan();
        double owed = site.owedPoints(), work = plan.workOf(key), rule = g.onSiteMonths(big);
        double alone = bm.plan(g.getBuildingOutput()).workOf(key);
        double pace = owed / work;
        long sites = bm.getStacksUnderConstruction().size() - 1;
        OUT.printf("   %d small sites beside one %s (%.0f points): the plan gives it %.0f (the city's crews alone %.0f) of %.0f points owed a month, %.2f months; the rule's wait reads %.2f%n",
                sites, big.getName(), owed, work, alone, owed, pace, rule);
        check("fixture: the site's pace is under the rule's wait by a month or more (" + String.format("%.2f", pace) + " months against " + String.format("%.2f", rule) + "), among " + sites + " small sites",
                sites >= 24 && work > 0 && rule - pace >= 1);
        CityNeeds.Need row = powerRow(g);
        check("the POWER row reads the wires and has the Substation on the way: " + (row == null ? null : row.reading()), row != null && row.wires() && row.onSite() == 1);
        check("...its months equal the site's owed points over the points the month's plan gives it (" + String.format("%.2f", pace) + ")",
                row != null && near(row.onSiteMonths(), pace, 1e-9) && near(g.onSitePaceMonths(big, plan), pace, 1e-9));
        // REV-ASH1 2: one months-left on every screen. The build card's figure (BuildCard.Figures.siteMonths()), the construction page's site row and
        // chart (Game.sitePaceMonths(key, the page's struck plan)) and the right panel's line (Game.siteLeft(key, its struck plan)) read the one getter.
        BuildCard.Figures card = BuildCard.of(g, big, GRID);
        Game.SiteLeft left = g.siteLeft(key, g.sitesPlan());
        double page = g.sitePaceMonths(key, g.sitesPlan()), panel = left.months();
        OUT.printf("   the months left: the overview %.6f, the build card %.6f, the construction page %.6f, the right panel %.6f; the rule's wait %.6f%n",
                row == null ? Double.NaN : row.onSiteMonths(), card.siteMonths(), page, panel, rule);
        check("...and every screen reads that one figure, to the bit: the build card's \"1 on site\", the construction page's and the right panel's, the overview's, none of them the rule's wait",
                row != null && card.onSite() == 1 && Double.doubleToRawLongBits(card.siteMonths()) == Double.doubleToRawLongBits(row.onSiteMonths())
                && Double.doubleToRawLongBits(page) == Double.doubleToRawLongBits(row.onSiteMonths()) && Double.doubleToRawLongBits(panel) == Double.doubleToRawLongBits(page)
                && left.progress() == Game.SiteProgress.BUILDING && Math.abs(page - rule) >= 1);
        if (contractorOn) {
            check("the pace reading includes his points: the plan gives the site more with them (" + String.format("%.0f", work) + ") than the city's crews' alone (" + String.format("%.0f", alone)
                    + "), and the months on the way are fewer (" + String.format("%.2f", pace) + " against " + String.format("%.2f", owed / alone) + ")", work > alone && pace < owed / alone);
        } else {
            check("with him off the plan is the city's crews' alone, to the bit (" + String.format("%.6f", work) + ")", work == alone);
        }
        double progress = site.getConstructionProgress();
        quietly(() -> g.simulateMonths(1));
        double advanced = bm.getStack(big).getConstructionProgress() - progress;
        double settledNow = builders.getForeignPointsThisMonth();
        OUT.printf("   the month advanced the site by %.6f points; the plan read before it gave %.6f; his month went from %.0f settled to %.0f%n", advanced, work, settled, settledNow);
        if (contractorOn) {
            check("the month's advance is the reading's pace within the change of his month, " + String.format("%.0f", settled) + " last settled to " + String.format("%.0f", settledNow) + " now ("
                    + String.format("%.0f", Math.abs(advanced - work)) + " off)", Math.abs(advanced - work) <= Math.abs(settledNow - settled) + 1e-9);
            check("...and nearer the reading with his points (" + String.format("%.0f", Math.abs(advanced - work)) + " off) than the city's crews' alone (" + String.format("%.0f", Math.abs(advanced - alone)) + " off)",
                    Math.abs(advanced - work) < Math.abs(advanced - alone));
        } else {
            check("the month then advances the site by exactly the points the plan gave it, so the reading is the site's real pace", near(advanced, work, 1e-9));
        }
    }

    /* ======================================================================== 5 ======================================================================== */

    static void money() {
        OUT.println("--- 5. the money audit stays exact over section 2's months ---");
        double worst = GROWTH.stream().mapToDouble(m -> Math.abs(m.residual())).max().orElse(Double.NaN);
        OUT.printf("   %d months; the worst residual %.3g (a cent is %.3g)%n", GROWTH.size(), worst, MoneyAudit.CENT);
        check("every one of section 2's " + GROWTH.size() + " months' money audits closes to the cent", GROWTH.size() >= GROWING_MONTHS && worst <= MoneyAudit.CENT);
    }

    public static void main(String[] args) {
        theRow();
        growth();
        posts();
        pace();
        money();
        OUT.println("TownGridCheck: " + fails + " failures");
        if (fails > 0) System.exit(1);
    }
}
