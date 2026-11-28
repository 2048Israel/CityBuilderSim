package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The wires are ordered with the power, and the grid is planned at its date and latitude (batch PW9-G).
 *
 * WHY. REV-PW9 P1-b: 0.9.218's modern-equipment city ordered a 21 MW Diesel and 9 Small Steam Stations at pre134-135 behind 15 MW of
 * wires, its Substation held 13 passes by the High school's protected completion; PW9's round 2 had 31 stations behind the founding 10 MW,
 * and 12.61 GWh of its months unserved. The dispatch caps every half-block at what the wires carry, so generation past them is capital
 * that never flows. Each of these fails on the model before PW9-G.
 *
 * WHAT THIS HAS TO PROVE.
 *
 *   1. THE CONDITION AND ITS REAL EFFECT. A town on the founding wires, its load's peak past them, plants standing with firm supply well
 *      past it: a native month serves no half-block more than the wires carry, leaves load unserved, generates no more than the wires let
 *      through, and serves what it asked less what it left. No law here: it is the physics the law answers to.
 *
 *   2. THE WIRES ORDERED WITH THE POWER. Ample builders, nothing protected, the automatic builder on: the plants short at the next
 *      plant's opening and the wires short of the load there though they carry today's. In one pass the wires are ordered before the plant,
 *      and carry the load at its date; the months that follow, until every order has opened, are never wire-bound. The same town built
 *      afresh and run the same months with the law off (AutoBuilder.wiresOffForTest, 0.9.218's builder) is wire-bound in some month, and
 *      the law serves of the load asked no less than it does.
 *
 *   3. THE WIRES HELD BY A PROTECTED SCHOOL'S DEADLINE. Where a service lacking now has its first completion quoted and the builders are
 *      tight, the wires are held by it (the protected law stands) and the plants are cut to the share the wires carry, or held whole where
 *      they already reach it; and while the service goes up the grid is ordered only in a month it is lacked now (the crew guard's exemption).
 *      Against the same town with the law off, the cut orders fewer plant units, leaves no more months unserved with the plants short, and
 *      serves of the load asked at least what the plants it cut would have (less a part in a thousand).
 *
 *   4. WHAT THE LAW LEAVES ALONE. (a) Wires that carry the load at the plant's date: the order is not cut and no wires are ordered. (b) An
 *      order the plants on site already serve the load for (the spread's wind, for the fuel it saves) behind wires short at its date: placed
 *      whole, not cut.
 *
 *   5. THE GRID'S PEAK AT ITS DATE AND LATITUDE. Now it is the native dispatch's own month at the city's latitude; ahead it is the highest
 *      month of that season over the months the order serves, from its opening to its horizon, at most a year of them.
 *
 *   6. THE MONEY AUDIT STAYS EXACT to the cent over every month the sections above ran.
 *
 * Every fixture CAUSES its condition rather than waiting for it, and asserts against the model's own constants and getters.
 */
public final class PowerWiresCheck {
    static int fails;
    static final PrintStream OUT = System.out, QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) { OUT.println((ok ? "  OK   " : "  FAIL ") + label); if (!ok) fails++; }
    static boolean near(double a, double b, double relative) { return Math.abs(a - b) <= relative * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }
    static void quietly(Runnable work) { System.setOut(QUIET); try { work.run(); } finally { System.setOut(OUT); } }

    static final BuildAdvice.Measure GRID = BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), POWER = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);

    /** The one plant the fixtures' city knows: 19 MW firm in one unit, small enough to be ordered a unit at a time. */
    static final String DIESEL = "Diesel Station";

    /** Section 1: the town's peak is this many times the founding wires'; its plants' firm supply this many times the peak. */
    static final double OVER_CARRY = 1.4, PLANTS_OVER = 2;

    /** Section 2: the town's peak today is this share of the founding wires' carry, so the wires carry it now and not the peak a plant's opening brings. */
    static final double UNDER_CARRY = .85;

    /** Section 3: the town's peak today as a share of the carry of the founding wires and a distribution unit. */
    static final double HELD_LOAD = .95;

    /** Section 3: the native months run after the pass: the school's promised thirteen and three more. */
    static final int HELD_MONTHS = 16;

    /** Section 2: the native months run at least this many (a year and a quarter, so that the highest season comes round), at most MOST_MONTHS. */
    static final int LEAST_MONTHS = 15, MOST_MONTHS = 70;

    /** Every month's money audit residual, sections 2 and 3's, for section 6. */
    static final List<Double> AUDIT = new ArrayList<>();

    // ------------------------------------------------------------------------------------------------------------------ fixtures

    static BuildingsTemplate named(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new AssertionError("the catalogue has no " + name);
        return t;
    }

    /**
     * A founded city with cash and ground to spare, the founding 10 MW its wires and nothing built of the grid or the power; the Diesel
     * Station the one plant known, and builders of `crews` output (Construction Depots stood until it is reached).
     */
    static Game city(String label, long crews) {
        Game g = new Game(GameFiles.scratch("powerwires-" + label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager bm = g.getBuildingManager();
            bm.setTemplateKnown(t -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || DIESEL.equals(t.getName()));
            for (int guard = 0; guard < 80 && g.getBuildingOutput() < crews; guard++) bm.addStack(named(g, "Construction Depot"), 25, true);
            g.getServicesManager().updateServices();
        });
        return g;
    }

    /**
     * A founded town (WorksFinanceCheck.reserveTown(): its people, its works, every essential service served, a flat history) with its
     * Coal Power Plant retired, so that its plants are the founding grid's 10 MW alone, and the Diesel Station the one plant known.
     */
    static Game town(String label, String... menu) {
        java.util.Set<String> known = java.util.Set.of(menu);
        java.nio.file.Path root;
        try { root = TempDirs.create("powerwires-" + label); } catch (java.io.IOException e) { throw new IllegalStateException(e); }
        Game g = WorksFinanceCheck.reserveTown(root, label);
        BuildingManager bm = g.getBuildingManager();
        quietly(() -> {
            BuildingsTemplate coal = named(g, "Coal Power Plant");
            bm.retire(coal, bm.getQuantity(coal.getId()));
            bm.setTemplateKnown(t -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || known.contains(t.getName()));
            g.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            g.getServicesManager().updateServices();
            EssentialWorksCheck.serveOtherEssentials(g, POWER);
        });
        return g;
    }

    /** What the fixture's town is, for the log: people, draw, wires, plants, the plants' staffing and the builders. */
    static String describe(Game g) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate diesel = named(g, DIESEL);
        return String.format("people %d, draw %.0f kW, peak %.0f of carry %.0f kW, firm %.0f of %.0f kW, diesel staffed %.3f, job fill %s, builders %.0f at every post, month %d",
                g.getPopulationManager().getPopulation(), u.getConsumption(), wires(g)[1], wires(g)[0], plants(g)[0], plants(g)[1], u.plantFill(diesel),
                java.util.Arrays.toString(java.util.Arrays.stream(g.getPopulationManager().getJobFillRate()).map(x -> Math.round(x * 100) / 100.0).toArray()),
                (double) g.getBuildingOutputAtEveryPost(), g.getMonth());
    }

    /** The grid's pair at today's demand: the wires' carry and the peak asked of them. */
    static double[] wires(Game g) { return BuildAdvice.supplyDemand(g, GRID, Map.of()); }

    /** The plants' pair at today's demand. */
    static double[] plants(Game g) { return BuildAdvice.supplyDemand(g, POWER, Map.of()); }

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

    /** The town's water works stood as far as the grid's peak stays at or under `kw` (drawPast()'s whole works, one fewer than it): at most that. */
    static void drawTo(Game g, double kw) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate works = named(g, "Water Treatment Plant");
        quietly(() -> {
            double before = wires(g)[1];
            bm.addStack(works, 1, true);
            g.getServicesManager().updateServices();
            double each = wires(g)[1] - before;
            if (!(each > 0)) throw new AssertionError("the water works draw nothing");
            long fit = (long) Math.floor((kw - wires(g)[1]) / each);
            if (fit > 0) bm.addStack(works, fit, true);
            g.getServicesManager().updateServices();
        });
    }

    /** Units of a plant stood until the plants' firm supply is `kw`. */
    static void plantsTo(Game g, double kw, String plant) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate diesel = named(g, plant);
        quietly(() -> {
            for (int guard = 0; guard < 80 && plants(g)[0] < kw; guard++) {
                bm.addStack(diesel, 1, true);
                g.getServicesManager().updateServices();
            }
        });
    }

    /** The calendar month (1-12) whose season is the year's lowest on the north's calendar: a plant ordered in it opens into a higher one. */
    static int lowestMonth() {
        int low = 1;
        for (int m = 2; m <= 12; m++) if (PowerDispatch.season(m) < PowerDispatch.season(low)) low = m;
        return low;
    }

    /** The months run, quietly, until the calendar stands at `monthOfYear`. */
    static void advanceTo(Game g, int monthOfYear) {
        quietly(() -> { for (int guard = 0; guard < 24 && CityCalendar.monthOfYear(g.getMonth()) != monthOfYear; guard++) g.simulateMonths(1); });
    }

    /** The power suggestion as the automatic builder's pass asks it: the city's cash and ground, its slack, every plant it knows. */
    static BuildAdvice.Suggestion powerCard(Game g) {
        return BuildAdvice.suggestFor(g, null, POWER, g.getCash(), g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack(), Set.of(), false, true);
    }

    /** What the wires are asked at a power order's own date and demand: its months, growth and slack, the grid reading no power factor. */
    static BuildAdvice.Ahead loadAt(BuildAdvice.Ahead p) { return new BuildAdvice.Ahead(p.months(), p.k(), p.slack()); }

    /** The wires' served share at a date, the grid's own units on site counted. */
    static double wiresShare(Game g, BuildAdvice.Ahead p) { return AutoBuilder.servedAt(g, GRID, g.getAutoBuilder().pendingCapacity(g, GRID), loadAt(p)); }

    /** The plants' served share at the order's own date, the plants on site counted from their own openings. */
    static double plantsShare(Game g, BuildAdvice.Ahead p) {
        return AutoBuilder.servedAt(g, POWER, WorksValue.openBy(g, g.getAutoBuilder().pendingCapacity(g, POWER), p.months()), p);
    }

    /** The highest season of the native dispatch over the months an order serves: from its opening to its horizon, at most a year of them; the harness's own oracle. */
    static double windowSeason(Game g, double months, double lat) {
        int now = g.getMonth();
        int from = (int) Math.ceil(BusinessInvestment.leadFor(months));
        int to = Math.min(from + 11, (int) Math.floor(months));
        double most = 0;
        for (int t = from; t <= to; t++) most = Math.max(most, PowerDispatch.season(CityCalendar.monthOfYear(now + t), lat));
        return most;
    }

    /** What the plants offered the month last struck, kW: every dispatch row's offer. */
    static double offered(Game g) {
        UtilitiesHandler.DispatchRecord d = g.getServicesManager().getUtilitiesHandler().getStruckBook().dispatch;
        double kw = 0;
        if (d != null) for (UtilitiesHandler.RowRecord r : d.rows()) kw += r.offeredKw();
        return kw;
    }

    /** Whether the month last struck left load unserved while the plants offered more than the wires carry. */
    static boolean wireBound(Game g) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        return u.getUnservedKwh() > 0 && offered(g) > Substations.costs(g).carryKw();
    }

    // ------------------------------------------------------------------------------------------------------------------ 1

    static void physics() {
        OUT.println("--- 1. the condition and its real effect ---");
        Game g = town("physics", DIESEL);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double founding = u.getFoundingGridKw();
        drawPast(g, OVER_CARRY * founding);
        plantsTo(g, PLANTS_OVER * wires(g)[1], DIESEL);
        OUT.println("   " + describe(g));
        quietly(() -> g.simulateMonths(1));
        UtilitiesHandler.Book b = u.getStruckBook();
        double carry = Substations.costs(g).carryKw();
        double peakLoad = 0, through = 0, generated = 0;
        boolean carried = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            peakLoad = Math.max(peakLoad, b.load[s]);
            carried &= b.load[s] - b.unserved[s] <= carry * (1 + 1e-9);
            through += Math.min(b.load[s], carry) * PowerDispatch.halfHours(s);
        }
        if (b.dispatch != null) for (UtilitiesHandler.RowRecord r : b.dispatch.rows()) generated += r.kwh();
        double unserved = u.getUnservedKwh();
        OUT.printf("   carry %.0f kW, founding %.0f kW; the month's peak load %.0f kW, offered %.0f kW; asked %.0f kWh, served %.0f, unserved %.0f; generated %.0f of the %.0f the wires let through%n",
                carry, founding, peakLoad, offered(g), b.askedKwh, b.servedKwh, unserved, generated, through);
        check("fixture: the wires are the founding grid's (" + carry + " kW) and the month's peak load is past them (" + peakLoad + " kW)", near(carry, founding, 1e-9) && peakLoad > carry);
        check("fixture: the plants offer well past the wires (" + offered(g) + " kW against " + carry + " kW) and the dispatch is recorded", b.dispatch != null && offered(g) > PLANTS_OVER * carry / 2);
        check("no half-block is served more than the wires carry", carried);
        check("load is left unserved (" + unserved + " kWh)", unserved > 0);
        check("the plants generate no more than the wires let through (" + generated + " kWh of " + through + ")", generated <= through * (1 + 1e-9));
        check("served is asked less unserved (" + b.servedKwh + " = " + b.askedKwh + " - " + unserved + ") and is what the plants generated", near(b.servedKwh, b.askedKwh - unserved, 1e-9) && near(b.servedKwh, generated, 1e-6));
    }

    // ------------------------------------------------------------------------------------------------------------------ 2

    /** The months a run goes on after every order has opened (a year and a month, so that the highest season comes round with them all standing). */
    static final int YEAR_AFTER = 13;

    /** What a run of native months found. */
    static final class Run {
        int months, wireBound, gridWhileHeld, firstGrid = -1, openedAt = -1;
    }

    /** What a run delivered, the law on or off: the plants the builder ordered and what they cost, the months the grid failed and how, the load asked and served. */
    static final class Delivery {
        final String law;
        long plantUnits;
        double plantSpend, plantPoints, asked, served;
        int months, wireBound, plantBound;
        final StringBuilder gridOpened = new StringBuilder();
        /** Month by month: plants standing, plants on site, offered kW, carry kW, peak kW, unserved kWh, and what bound the month. */
        final List<String> trace = new ArrayList<>();
        /** Month by month: the plants standing, the grid's units standing and the kWh served, for comparing one run with another. */
        final List<Long> standing = new ArrayList<>(), gridStanding = new ArrayList<>();
        final List<Double> servedBy = new ArrayList<>();
        /** Month by month: the care centres' construction points done (the protected site the plants share the crews with), whether the plants bound the month, and the plants' offered kW. */
        final List<Double> careDone = new ArrayList<>(), offeredBy = new ArrayList<>();
        final List<Boolean> plantBoundBy = new ArrayList<>();

        Delivery(String law) { this.law = law; }

        /** The plant units the builder's last pass ordered, and their cost. */
        void passed(AutoBuilder auto) {
            for (AutoBuilder.Step s : auto.steps()) if (s.measure().equals(POWER) && s.ordered() > 0) { plantUnits += s.ordered(); plantSpend += s.cost(); }
        }

        /** ...and the construction points those units put on the crews (the units' own: the preparation and works a site may owe are not counted). */
        void passed(Game g) {
            passed(g.getAutoBuilder());
            for (AutoBuilder.Step s : g.getAutoBuilder().steps())
                if (s.measure().equals(POWER) && s.ordered() > 0) plantPoints += s.ordered() * (double) named(g, s.building()).getConstructionPoints();
        }

        /** What the month last struck asked and served, and whether the wires or the plants bound it (unserved while the plants offered no more than the carry). */
        void struck(Game g) {
            UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
            months++;
            asked += u.getStruckBook().askedKwh;
            served += u.getStruckBook().servedKwh;
            if (wireBound(g)) wireBound++;
            else if (u.getUnservedKwh() > 0) plantBound++;
            long stand = 0;
            for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (POWER.serves(t)) stand += g.getBuildingManager().getQuantity(t.getId());
            standing.add(stand);
            gridStanding.add(gridUnits(g));
            servedBy.add(u.getStruckBook().servedKwh);
            BuildingsTemplate centre = named(g, "Community Health Centre");
            BuildingsStacks care = g.getBuildingManager().getStack(centre);
            careDone.add(care == null ? 0 : care.getQuantity() * (double) centre.getConstructionPoints() + care.getConstructionProgress());
            offeredBy.add(offered(g));
            plantBoundBy.add(!wireBound(g) && u.getUnservedKwh() > 0);
            trace.add(String.format("   (law %-3s) month %2d: plants %d standing, %d on site; offered %.0f kW, carry %.0f kW, peak %.0f kW; unserved %.0f kWh%s", law, g.getMonth(), stand,
                    BuildAdvice.units(BuildAdvice.onSite(g, POWER)), offered(g), Substations.costs(g).carryKw(), wires(g)[1], u.getUnservedKwh(),
                    wireBound(g) ? " WIRE-BOUND" : u.getUnservedKwh() > 0 ? " PLANT-BOUND" : "") + "; " + sites(g) + " grid ordered " + g.getAutoBuilder().steps().stream()
                    .filter(x -> x.measure().equals(GRID)).mapToLong(AutoBuilder.Step::ordered).sum());
        }

        /** The first month (counted from 1) in which this run's plants standing, grid standing or kWh served differ from `o`'s, or 0 where they are the same in every month of both. */
        int firstDifference(Delivery o) {
            if (standing.size() != o.standing.size()) return Math.min(standing.size(), o.standing.size()) + 1;
            for (int i = 0; i < standing.size(); i++)
                if (!standing.get(i).equals(o.standing.get(i)) || !gridStanding.get(i).equals(o.gridStanding.get(i)) || !near(servedBy.get(i), o.servedBy.get(i), 1e-9)) return i + 1;
            return 0;
        }

        /** Served over asked across the run: 1 where nothing was asked. */
        double share() { return asked > 0 ? served / asked : 1; }

        void print() {
            OUT.printf("   law %-3s: %d plant units ordered for Cr %.1fM; of %d months %d wire-bound, %d plant-bound; asked %.0f MWh, served %.0f (%.4f); grid units opened in month%s%n",
                    law, plantUnits, plantSpend / PriceLevel.FACTOR / 1e6, months, wireBound, plantBound, asked / 1000, served / 1000, share(), gridOpened.length() == 0 ? ": none" : gridOpened.toString());
        }
    }

    /** The grid units standing: Substations and distribution units. */
    static long gridUnits(Game g) {
        BuildingManager bm = g.getBuildingManager();
        return bm.getQuantity(Substations.ID) + bm.getQuantity(Substations.DISTRIBUTION_ID);
    }

    /**
     * Native months, one at a time and the automatic builder on, until every order of the grid, the power and `care` (null for none) has opened
     * and YEAR_AFTER months more have run, at most `most`; or, `exact`, `most` months whatever has opened. The months that were wire-bound, the
     * grid's orders while `care`'s site was still going up (a month it opens in not counted either way), and the month the grid's first order
     * came after it opened; `d` takes what each month ordered, asked, served and opened.
     */
    static Run run(Game g, BuildAdvice.Measure care, int most, Delivery d, boolean exact) {
        AutoBuilder auto = g.getAutoBuilder();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        Run r = new Run();
        long units = gridUnits(g);
        for (int i = 0; i < most; i++) {
            boolean lackedBefore = BuildAdvice.lacksNow(g, GRID);
            boolean heldBefore = care != null && BuildAdvice.units(BuildAdvice.onSite(g, care)) > 0;
            quietly(() -> g.simulateMonths(1));
            AUDIT.add(g.getLastMoneyAudit().residual);
            r.months++;
            d.passed(g);
            d.struck(g);
            if (gridUnits(g) > units) { d.gridOpened.append(' ').append(r.months).append(" (+").append(gridUnits(g) - units).append(')'); units = gridUnits(g); }
            boolean heldAfter = care != null && BuildAdvice.units(BuildAdvice.onSite(g, care)) > 0;
            boolean grid = auto.steps().stream().anyMatch(s -> s.measure().equals(GRID) && s.ordered() > 0);
            if (grid && heldBefore && heldAfter && !lackedBefore && !BuildAdvice.lacksNow(g, GRID)) r.gridWhileHeld++;
            if (grid && !(heldBefore && heldAfter) && r.firstGrid < 0) r.firstGrid = i;
            if (wireBound(g)) {
                r.wireBound++;
                OUT.printf("   (law %s) month %d is wire-bound: unserved %.0f kWh, offered %.0f kW, carry %.0f kW%n", d.law, g.getMonth(), u.getUnservedKwh(), offered(g), Substations.costs(g).carryKw());
            }
            long site = BuildAdvice.units(BuildAdvice.onSite(g, GRID)) + BuildAdvice.units(BuildAdvice.onSite(g, POWER)) + (care == null ? 0 : BuildAdvice.units(BuildAdvice.onSite(g, care)));
            if (site == 0 && r.openedAt < 0) r.openedAt = i;
            if (!exact && r.openedAt >= 0 && i - r.openedAt >= YEAR_AFTER) break;
        }
        return r;
    }

    /** Section 2's town, built afresh: the builders ample, nothing protected, the automatic builder on at slack 0, the draw UNDER_CARRY of the founding wires in the year's lowest month. */
    static Game orderTown(String label) {
        Game g = town(label, DIESEL);
        advanceTo(g, lowestMonth());
        drawPast(g, UNDER_CARRY * g.getServicesManager().getUtilitiesHandler().getFoundingGridKw());
        quietly(() -> { g.getAutoBuilder().setSlack(0, null); g.getAutoBuilder().setOn(true, null); });
        return g;
    }

    static void ordered() {
        OUT.println("--- 2. the wires are ordered with the power: the law on, and off as 0.9.218 had it ---");
        Game g = orderTown("order");
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        BuildingsTemplate diesel = named(g, DIESEL);
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        double[] now = wires(g);
        double seasonNow = PowerDispatch.season(CityCalendar.monthOfYear(g.getMonth()));
        double seasonThen = p == null ? 0 : windowSeason(g, p.months(), Double.NaN);
        double plantsAt = p == null ? Double.NaN : plantsShare(g, p), wiresAt = p == null ? Double.NaN : wiresShare(g, p);
        OUT.println("   " + describe(g));
        OUT.printf("   the card %s x %d opens in %.1f months, planned %.1f months ahead (season then %.3f, now %.3f); plants' share there %.3f, the wires' %.3f%n",
                card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), card == null ? 0 : card.lead(), p == null ? 0 : p.months(),
                seasonThen, seasonNow, plantsAt, wiresAt);
        check("fixture: no service's first completion is protected, and the wires carry today's peak (" + now[1] + " of " + now[0] + " kW)", auto.toState().pendingUntil.isEmpty() && now[1] <= now[0]);
        check("fixture: the power card is Diesel Stations (" + (card == null ? "none" : card.template().getName() + " x " + card.count()) + ")", card != null && card.count() >= 1 && diesel.equals(card.template()));
        check("fixture: the order's window season (" + seasonThen + ") is past today's (" + seasonNow + ")", seasonThen > seasonNow);
        check("fixture: the plants are short at the plant's opening (" + plantsAt + ") and the wires short of the load there (" + wiresAt + ")", plantsAt < 1 && wiresAt < 1);
        if (card == null) return;
        Delivery on = new Delivery("on");
        quietly(() -> auto.pass(g));
        on.passed(g);
        int grid = -1, power = -1, at = 0;
        StringBuilder said = new StringBuilder();
        for (AutoBuilder.Step s : auto.steps()) {
            if (s.measure().equals(GRID) || s.measure().equals(POWER)) said.append(' ').append(s.measure().label()).append(':').append(s.ordered()).append('/').append(s.wanted()).append(':').append(s.cut());
            if (grid < 0 && s.measure().equals(GRID) && s.ordered() > 0) grid = at;
            if (power < 0 && s.measure().equals(POWER) && s.ordered() > 0) power = at;
            at++;
        }
        double after = wiresShare(g, p);
        OUT.printf("   the pass:%s; wires' share at the card's date after it %.3f%n", said, after);
        check("one pass orders the wires (step " + grid + ") before the plant (step " + power + ")", grid >= 0 && power >= 0 && grid < power);
        check("...and the wires then carry the load at the plant's date (the wires' share " + after + ")", after >= 1);
        Run r = run(g, null, MOST_MONTHS, on, false);
        long open = bm.getQuantity(diesel.getId()), units = bm.getQuantity(Substations.DISTRIBUTION_ID);
        OUT.printf("   %d months run: %d diesels and %d units stand; %d wire-bound%n", r.months, open, units, r.wireBound);
        check("every order has opened (" + open + " Diesel Stations and " + units + " distribution units stand) and the plants stood a year (" + r.months + " months)", r.openedAt >= 0 && open >= 1 && units >= 1 && r.months > r.openedAt + YEAR_AFTER);
        check("no month of the " + r.months + " was wire-bound (load unserved while the plants offered more than the carry)", r.wireBound == 0);
        // The same town built afresh and the same months run with the law off (0.9.218's builder): the plants wait for no wires and none are ordered with them.
        Delivery off = new Delivery("off");
        Game h = orderTown("order-off");
        AutoBuilder.wiresOffForTest = true;
        try {
            quietly(() -> h.getAutoBuilder().pass(h));
            off.passed(h);
            StringBuilder offSaid = new StringBuilder();
            for (AutoBuilder.Step s : h.getAutoBuilder().steps())
                if (s.measure().equals(GRID) || s.measure().equals(POWER)) offSaid.append(' ').append(s.measure().label()).append(':').append(s.ordered()).append('/').append(s.wanted()).append(':').append(s.cut());
            OUT.println("   the pass, law off:" + offSaid);
            run(h, null, r.months, off, true);
        } finally { AutoBuilder.wiresOffForTest = false; }
        on.print();
        off.print();
        int differ = on.firstDifference(off);
        check("where nothing holds the wires the law changes nothing: law on and law off have the same plants standing, grid standing and kWh served in each of " + on.months + " months ("
                + (differ == 0 ? "no month differs" : "month " + differ + " differs: plants " + on.standing.get(differ - 1) + " against " + off.standing.get(differ - 1) + ", grid " + on.gridStanding.get(differ - 1) + " against "
                + off.gridStanding.get(differ - 1) + ", served " + on.servedBy.get(differ - 1) + " against " + off.servedBy.get(differ - 1)) + ")", on.months == off.months && differ == 0);
        check("law on serves of the load asked no less than law off (" + on.share() + " against " + off.share() + ")", on.share() >= off.share() - 1e-9);
    }

    // ------------------------------------------------------------------------------------------------------------------ 3

    /** The service whose first completion the fixtures protect: General care's Community Health Centres. */
    static final BuildAdvice.Measure CARE = BuildAdvice.Measure.care(CareType.GENERAL);

    /** The one plant section 3 knows: a unit a twentieth of a Diesel Station's, so that an order of several is cut to fewer. */
    static final String STEAM = "Small Steam Station";

    /**
     * 3's town: section 2's, the Small Steam Station the plant known, with General care lacking now, its Community Health Centres going up on the
     * builders the town has and their first completion saved (PowerPlanCheck's careTown(): pendingUntil, as rememberPending() quotes it).
     */
    static Game schoolTown(String label) {
        Game g = town(label, STEAM);
        BuildingManager bm = g.getBuildingManager();
        quietly(() -> {
            for (BuildingsTemplate t : bm.getTemplates()) if (CARE.serves(t) && bm.getQuantity(t.getId()) > 0) bm.retire(t, bm.getQuantity(t.getId()));
            BuildingsTemplate centre = named(g, "Community Health Centre");
            long required = BuildAdvice.count(g, CARE, Map.of(), Map.of(centre, 1L), g.getAutoBuilder().target(), BuildAdvice.MOST, true)[0];
            g.buildStack(centre, required, false);
            AutoBuilder ab = g.getAutoBuilder();
            AutoBuilder.State state = ab.toState();
            state.on = true;
            state.pendingUntil.clear();
            state.pendingUntil.put(CARE.label(), g.getMonth() + BuildAdvice.firstCompletion(g, CARE, BuildAdvice.onSite(g, CARE)));
            ab.restore(state);
        });
        return g;
    }

    /** Whether `count` of the order would put the protected centres' first completion past its quote (AutoBuilder.pendingDelay(), read from outside). */
    static boolean delays(Game g, BuildingsTemplate t, long count) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate centre = named(g, "Community Health Centre");
        double output = g.getBuildingOutputAtEveryPost(), due = g.getAutoBuilder().toState().pendingUntil.getOrDefault(CARE.label(), Double.NaN);
        double alone = bm.siteUnitMonths(centre, 1, output);
        double with = bm.siteUnitMonthsAfter(centre, 1, output, t, count, WorksFinanceCheck.addedPoints(g.quoteBuild(t, count), t, count));
        return Double.isFinite(alone) && with > alone + 1e-9 && g.getMonth() + Math.ceil(with - 1e-9) > due;
    }

    /**
     * 3's town, built afresh: the school's town in the year's lowest month with a distribution unit standing and the draw HELD_LOAD of the carry
     * (the wires carry today's peak, and not the highest season's), the automatic builder on at slack 0 and, for (b), the plants stood halfway
     * between the wires' share at the plant's date and the load there: past the wires' and short of 1.
     */
    static Game heldTown(boolean cut) {
        Game g = schoolTown(cut ? "held-short" : "held-at");
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        advanceTo(g, lowestMonth());
        quietly(() -> { bm.addStack(bm.getTemplate(Substations.DISTRIBUTION_ID), 1, true); g.getServicesManager().updateServices(); });
        drawTo(g, HELD_LOAD * wires(g)[0]);
        quietly(() -> { auto.setSlack(0, null); auto.setOn(true, null); });
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        if (!cut && p != null) {
            double[] sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
            plantsTo(g, sd[1] * (1 + wiresShare(g, p)) / 2, STEAM);
        }
        return g;
    }

    /** The two sites a plant order crowds, for the log: the care centres and the grid's units, each as standing, on site and points done of points on site. */
    static String sites(Game g) {
        BuildingManager bm = g.getBuildingManager();
        StringBuilder out = new StringBuilder();
        for (BuildingsTemplate t : new BuildingsTemplate[] {named(g, "Community Health Centre"), bm.getTemplate(Substations.ID), bm.getTemplate(Substations.DISTRIBUTION_ID)}) {
            BuildingsStacks s = bm.getStack(t);
            out.append(String.format("%s: %d standing, %d on site, %.0f of %.0f points done; ", t.getName(), s == null ? 0 : s.getQuantity(), s == null ? 0 : s.getUnderConstruction(),
                    s == null ? 0 : s.getConstructionProgress(), s == null ? 0 : s.pointsOnSite()));
        }
        return out.toString();
    }

    /**
     * 3 (a) and (b): the plants short of the wires' share (a) or already at it and short of the reserve (b), and the wires' units held by the
     * school's deadline; then the same town with the law off, and what each delivered.
     */
    static void held(boolean cut) {
        String which = cut ? "(a) the plants short of the wires' share" : "(b) the plants already at the wires' share";
        OUT.println("--- 3 " + which + ": the wires wait for the school's completion, the plants are cut or held; the law on, and off ---");
        Game g = heldTown(cut);
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        OUT.println("   " + describe(g));
        double wiresBefore = p == null ? Double.NaN : wiresShare(g, p), plantsBefore = p == null ? Double.NaN : plantsShare(g, p);
        BuildingsTemplate unit = bm.getTemplate(Substations.DISTRIBUTION_ID);
        BuildAdvice.Suggestion nested = p == null ? null : BuildAdvice.suggestFor(g, null, GRID, g.getCash(), g.getLandManager().getAvailableSqFt(), auto.getSlack(), Set.of(), false, true, loadAt(p));
        // The least count of the plant whose share at its own date reaches the wires', by the harness's own linear search.
        long least = 0;
        if (p != null && card != null) {
            Map<BuildingsTemplate, Long> at = WorksValue.openBy(g, auto.pendingCapacity(g, POWER), p.months());
            for (long n = 1; n <= card.count() && least == 0; n++) if (AutoBuilder.servedAt(g, POWER, BuildAdvice.plus(at, card.template(), n), p) >= wiresBefore) least = n;
        }
        double promised = auto.promisedMonths(g, CARE);
        OUT.printf("   the card %s x %d opening in %.1f months, least count to the wires' share %d; plants' share %.3f, wires' %.3f; care promised in %.2f months%n",
                card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), card == null ? 0 : card.lead(), least, plantsBefore, wiresBefore, promised);
        check("fixture: General care lacks now and its first completion is protected (" + promised + " months); the wires carry today's peak (" + wires(g)[1] + " of " + wires(g)[0] + " kW), so the grid is not lacked and the crew guard holds its order",
                BuildAdvice.lacksNow(g, CARE) && Double.isFinite(promised) && wires(g)[1] <= wires(g)[0] && !BuildAdvice.lacksNow(g, GRID));
        check("fixture: the wires' order for the plant's load (" + (nested == null ? "none" : nested.count() + " x " + nested.template().getName()) + ") would put the centres' completion past its quote",
                nested != null && unit.equals(nested.template()) && delays(g, nested.template(), nested.count()));
        if (cut)
            check("fixture: the plants are short of the wires' share (" + plantsBefore + " against " + wiresBefore + "), the power lacked (the crew guard leaves it), and the order is more units (" + (card == null ? 0 : card.count()) + ") than the share needs (" + least + ")",
                    card != null && plantsBefore < wiresBefore && wiresBefore < 1 && BuildAdvice.lacksNow(g, POWER) && least >= 1 && card.count() > least);
        else
            check("fixture: the plants are at the wires' share (" + plantsBefore + " against " + wiresBefore + ") and short of the reserve at the plant's date",
                    card != null && plantsBefore >= wiresBefore && plantsBefore < 1 && wiresBefore < 1);
        if (card == null || nested == null) return;
        Delivery on = new Delivery("on");
        quietly(() -> auto.pass(g));
        on.passed(g);
        AutoBuilder.Step wiresStep = null, powerStep = null;
        for (AutoBuilder.Step s : auto.steps()) {
            if (s.measure().equals(GRID) && s.cut() == AutoBuilder.Cut.BUILDERS && wiresStep == null) wiresStep = s;
            if (s.measure().equals(POWER) && powerStep == null) powerStep = s;
        }
        String heldGrid = GRID.label() + " waits to preserve " + CARE.label() + "'s quoted first completion.";
        boolean words = auto.held().stream().anyMatch(h -> h.contains(heldGrid));
        OUT.println("   the pass: the wires " + (wiresStep == null ? "no step" : wiresStep.outcome() + " " + wiresStep.ordered() + "/" + wiresStep.wanted() + " cut " + wiresStep.cut())
                + "; the plant " + (powerStep == null ? "no step" : powerStep.outcome() + " " + powerStep.ordered() + "/" + powerStep.wanted() + " " + powerStep.building() + " cut " + powerStep.cut()));
        for (String h : auto.held()) OUT.println("   held: " + (h.length() > 200 ? h.substring(0, 200) : h));
        check("the wires' step is held by the builders, ordering none, and says it waits to preserve " + CARE.label() + "'s quoted first completion",
                wiresStep != null && wiresStep.ordered() == 0 && words && BuildAdvice.units(BuildAdvice.onSite(g, GRID)) == 0);
        check("...and the school's promised months are what they were (" + promised + ", now " + auto.promisedMonths(g, CARE) + ")", auto.promisedMonths(g, CARE) == promised);
        if (cut)
            check("the plants' order is cut for the wires (" + (powerStep == null ? "none" : powerStep.ordered() + " of " + powerStep.wanted() + ", cut " + powerStep.cut()) + ") to the least count whose share reaches the wires' (" + least + ")",
                    powerStep != null && powerStep.cut() == AutoBuilder.Cut.WIRES && powerStep.ordered() == least);
        else {
            boolean line = auto.held().stream().anyMatch(h -> h.startsWith(POWER.label() + ":") && h.contains("wires"));
            check("the plants' order is held whole by the wires (" + (powerStep == null ? "none" : powerStep.ordered() + " of " + powerStep.wanted() + ", cut " + powerStep.cut()) + "), its line naming them",
                    powerStep != null && powerStep.ordered() == 0 && powerStep.cut() == AutoBuilder.Cut.WIRES && line);
        }
        // The months of the school's deadline run on: the protected law stands, so the grid is ordered in them only in a month it is lacked now
        // (the crew guard's exemption: the peak of the season past the carry). The wires ordered then, and the plants that are not cut, crowd the
        // same few builders: the months the plants open into are printed, and held to what the law is for below, against the same town with it off.
        Run r = run(g, CARE, HELD_MONTHS, on, false);
        OUT.printf("   %d months run; %d of them wire-bound; the grid ordered in %d months it was not lacked while the centres went up%n", r.months, r.wireBound, r.gridWhileHeld);
        check("the grid is ordered while the centres go up only in a month it is lacked now (" + r.gridWhileHeld + " months it was not)", r.months > 0 && r.gridWhileHeld == 0);
        // The same town built afresh, the same months, the law off (0.9.218's builder: the plants wait for no wires and none are ordered with them).
        Delivery off = new Delivery("off");
        Game other = heldTown(cut);
        AutoBuilder.wiresOffForTest = true;
        try {
            quietly(() -> other.getAutoBuilder().pass(other));
            off.passed(other);
            run(other, CARE, r.months, off, true);
        } finally { AutoBuilder.wiresOffForTest = false; }
        on.print();
        off.print();
        for (int i = 0; i < Math.max(on.trace.size(), off.trace.size()); i++) {
            if (i < on.trace.size()) OUT.println(on.trace.get(i));
            if (i < off.trace.size()) OUT.println(off.trace.get(i));
        }
        OUT.printf("   plant orders' construction points on the crews: on %.0f, off %.0f%n", on.plantPoints, off.plantPoints);
        OUT.println("   end, law on : " + sites(g));
        OUT.println("   end, law off: " + sites(other));
        BuildAdvice.Suggestion lastOn = powerCard(g), lastOff = powerCard(other);
        OUT.printf("   at the last month, the plants' share at the first card's date (%.1f months): on %.4f, off %.4f; the wires' share there: on %.4f, off %.4f; the last cards: on %s, off %s%n",
                p.months(), plantsShare(g, p), plantsShare(other, p), wiresShare(g, p), wiresShare(other, p),
                lastOn == null ? "none" : lastOn.template().getName() + " x " + lastOn.count(), lastOff == null ? "none" : lastOff.template().getName() + " x " + lastOff.count());
        check("law on orders fewer plant units than law off (" + on.plantUnits + " against " + off.plantUnits + "): the excess is held", on.plantUnits < off.plantUnits);
        // What the held units were (PW9-G's notes, "the crews"): law off's units past the wires take a larger share of two depots (the crews' weight is
        // units x points^0.7, ConstructionControl), so its first plants open sooner and it serves more in the months law on's are still being built;
        // law on puts those points on the protected care centre instead, and its plants reach the carry. Its delivery over the months is printed, not
        // held to law off's: the months it serves less are months its plants stand fewer than law off's, never months the cut kept them short.
        check(String.format("law on's plant orders put fewer construction points on the crews than law off's (%.0f against %.0f)", on.plantPoints, off.plantPoints),
                on.plantPoints < off.plantPoints);
        int careBehind = 0, cutShort = 0;
        for (int i = 0; i < Math.min(on.careDone.size(), off.careDone.size()); i++) {
            if (on.careDone.get(i) < off.careDone.get(i) - 1e-9) careBehind++;
            if (on.plantBoundBy.get(i) && !off.plantBoundBy.get(i) && on.standing.get(i) >= off.standing.get(i)) cutShort++;
        }
        check("...and the protected care centre is as far along under law on as under law off in every month (" + careBehind + " months behind)", careBehind == 0);
        check("each month law on's plants bound and law off's did not, law on had fewer plants standing: they were still being built, not cut short ("
                + cutShort + " months otherwise)", cutShort == 0);
        double lastOffered = on.offeredBy.isEmpty() ? 0 : on.offeredBy.get(on.offeredBy.size() - 1);
        check(String.format("by the last month the plants law on allowed offer the carry (%.0f of %.0f kW): nothing the wires carry was left unbuilt", lastOffered, Substations.costs(g).carryKw()),
                lastOffered >= Substations.costs(g).carryKw() - 1e-9);
        OUT.printf("   delivery over the %d months: law on serves %.4f of the load asked, law off %.4f%s%n", on.months, on.share(), off.share(),
                on.share() < off.share() ? " (law off's earlier plants, at the care centre's expense)" : "");
    }

    // ------------------------------------------------------------------------------------------------------------------ 4

    static void leftAlone() {
        OUT.println("--- 4 (a) wires that carry the load at the plant's date: the order is not cut and no wires are ordered ---");
        Game g = town("carries", DIESEL);
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        advanceTo(g, lowestMonth());
        drawPast(g, UNDER_CARRY * u.getFoundingGridKw());
        quietly(() -> { auto.setSlack(0, null); auto.setOn(true, null); });
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        if (p != null) {
            // Distribution units standing for the peak at the plant's date and a tenth more.
            double need = BuildAdvice.supplyDemand(g, GRID, Map.of(), loadAt(p))[1] * 1.1;
            long units = (long) Math.ceil((need - wires(g)[0]) / (Substations.DISTRIBUTION_MW * 1000));
            if (units > 0) quietly(() -> { bm.addStack(bm.getTemplate(Substations.DISTRIBUTION_ID), units, true); g.getServicesManager().updateServices(); });
            card = powerCard(g);
            p = card == null ? null : card.ahead();
        }
        double wiresAt = p == null ? Double.NaN : wiresShare(g, p), plantsAt = p == null ? Double.NaN : plantsShare(g, p);
        OUT.printf("   the card %s x %d; wires' share at its date %.3f, plants' %.3f%n", card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), wiresAt, plantsAt);
        check("fixture: the plants are short at the plant's date (" + plantsAt + ") and the wires carry the load there (" + wiresAt + ")", card != null && plantsAt < 1 && wiresAt >= 1);
        if (card == null) return;
        quietly(() -> auto.pass(g));
        AutoBuilder.Step power = null;
        boolean grid = false;
        for (AutoBuilder.Step s : auto.steps()) {
            if (s.measure().equals(POWER) && power == null) power = s;
            if (s.measure().equals(GRID) && s.ordered() > 0) grid = true;
        }
        OUT.println("   the pass: the plant " + (power == null ? "no step" : power.outcome() + " " + power.ordered() + "/" + power.wanted() + " cut " + power.cut()) + "; any grid order " + grid);
        check("the plants' order is placed whole, not cut for the wires", power != null && power.ordered() > 0 && power.ordered() == power.wanted() && power.cut() != AutoBuilder.Cut.WIRES);
        check("no grid unit is ordered in the pass", !grid);
    }

    /** The one plant section 4 (b)'s spread orders: a farm that displaces the diesels' fuel. */
    static final String WIND = "Wind Farm";

    /** Section 4 (b): the distribution units that stand, so that the wires carry more than the founding grid's plant and the diesels run past it. */
    static final long SPREAD_UNITS = 2;

    /**
     * 4 (b): the town of section 1 (the founding wires short of its peak, Diesel Stations standing well past it) knowing the Wind Farm too, so that
     * the plants on site serve the load at the farm's opening and the one order the planner has is the spread's, wind for the fuel it saves; its
     * builders kept as EssentialWorksCheck.serveGrid keeps them (depots stacked until the output at every post is what it was, and above 0).
     */
    static void spreadLeftAlone() {
        OUT.println("--- 4 (b) an order the plants on site already serve the load for, behind short wires: not cut ---");
        Game g = town("spread", DIESEL, WIND);
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double site = Math.max(1, g.getBuildingOutputAtEveryPost());
        advanceTo(g, lowestMonth());
        // Distribution units stand (SPREAD_UNITS of them) so that the diesels run on a good part of the load past the founding grid's own plant (a farm saves fuel only where they do);
        // the draw is HELD_LOAD of the carry today, and past it at the highest season; the diesels stand well past that.
        quietly(() -> { bm.addStack(bm.getTemplate(Substations.DISTRIBUTION_ID), SPREAD_UNITS, true); g.getServicesManager().updateServices(); });
        drawTo(g, HELD_LOAD * wires(g)[0]);
        plantsTo(g, PLANTS_OVER * OVER_CARRY * wires(g)[0], DIESEL);
        quietly(() -> {
            for (int guard = 0; guard < 64 && !(g.getBuildingOutputAtEveryPost() >= site && g.getBuildingOutputAtEveryPost() > 0); guard++)
                bm.addStack(named(g, "Construction Depot"), 1, true);
            auto.setSlack(0, null);
            auto.setOn(true, null);
        });
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        double wiresAt = p == null ? Double.NaN : wiresShare(g, p), plantsAt = p == null ? Double.NaN : plantsShare(g, p);
        OUT.println("   " + describe(g));
        for (BuildAdvice.Spread s : BuildAdvice.spreads(g, g.getLandManager().getAvailableSqFt(), Set.of()))
            OUT.printf("   the spread weighs %s: saves %.0f a year against %.0f (net %.0f)%n", s.template().getName(), s.saved(), s.cost(), s.net());
        OUT.printf("   the card %s x %d; wires' share at its date %.3f, plants' %.3f%n", card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), wiresAt, plantsAt);
        check("fixture: the card is the Wind Farm, the plants serve the load at its date (" + plantsAt + ") and the wires are short of it (" + wiresAt + ")",
                card != null && WIND.equals(card.template().getName()) && plantsAt >= 1 && wiresAt < 1);
        if (card == null) return;
        long count = card.count();
        quietly(() -> auto.pass(g));
        AutoBuilder.Step power = null;
        for (AutoBuilder.Step s : auto.steps()) if (s.measure().equals(POWER) && power == null) power = s;
        OUT.println("   the pass: the plant " + (power == null ? "no step" : power.outcome() + " " + power.ordered() + "/" + power.wanted() + " " + power.building() + " cut " + power.cut()));
        for (String h : auto.held()) OUT.println("   held: " + (h.length() > 200 ? h.substring(0, 200) : h));
        check("the spread's order is placed whole, not cut for the wires (" + (power == null ? "none" : power.ordered() + " of " + count + ", cut " + power.cut()) + ")",
                power != null && power.ordered() == count && power.cut() != AutoBuilder.Cut.WIRES);
    }

    // ------------------------------------------------------------------------------------------------------------------ 5

    /** Section 5: the town's draw as a share of the founding wires', the ahead windows' months, and their growth and slack. */
    static final double SEASON_DRAW = .5;
    static final double[] WINDOWS = {3, 8, 14, 20, 30, 45};
    static final double WINDOW_K = 1.07, WINDOW_SLACK = .1;

    static void season() {
        OUT.println("--- 5. the grid's peak at its date and latitude ---");
        Game g = city("season", 1);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double[] lat = {45};
        u.setPlace(() -> lat[0], () -> true);
        drawPast(g, SEASON_DRAW * u.getFoundingGridKw());
        double[] lats = {45, -35, Double.NaN};
        int cases = 0, nowRed = 0, windowRed = 0, aboveRed = 0, southDiffers = 0, southNow = 0, above = 0;
        BuildAdvice.Ahead far = new BuildAdvice.Ahead(24, WINDOW_K, WINDOW_SLACK);
        for (int i = 0; i < 12; i++) {
            for (double l : lats) {
                lat[0] = l;
                double c = u.getConsumption();
                int calendar = CityCalendar.monthOfYear(g.getMonth());
                double drawNow = c + WorksValue.pendingPowerLoad(g, 0);
                double read = BuildAdvice.supplyDemand(g, GRID, Map.of(), BuildAdvice.NOW)[1];
                double want = Substations.peak(drawNow, PowerDispatch.season(calendar, l));
                cases++;
                if (read != want) nowRed++;
                if (l < 0 && Substations.peak(drawNow, PowerDispatch.season(calendar)) != want) { southDiffers++; if (read == want) southNow++; }
                for (double months : WINDOWS) {
                    BuildAdvice.Ahead p = new BuildAdvice.Ahead(months, WINDOW_K, WINDOW_SLACK);
                    double draw = p.scale() * c + WorksValue.pendingPowerLoad(g, months);
                    if (!near(BuildAdvice.supplyDemand(g, GRID, Map.of(), p)[1], Substations.peak(draw, windowSeason(g, months, l)), 1e-12)) windowRed++;
                    cases++;
                }
                double drawFar = far.scale() * c + WorksValue.pendingPowerLoad(g, far.months());
                double present = Substations.peak(drawFar, PowerDispatch.season(calendar, l));
                if (PowerDispatch.season(calendar, l) < PowerDispatch.season(PowerDispatch.peakMonth())) {
                    above++;
                    if (!(BuildAdvice.supplyDemand(g, GRID, Map.of(), far)[1] > present)) aboveRed++;
                }
            }
            quietly(() -> g.simulateMonths(1));
        }
        OUT.printf("   %d readings over 12 months and 3 latitudes; NOW off %d, windows off %d; the south's month differs from the north's in %d of 12 (read right in %d); above the present month %d, not above %d%n",
                cases, nowRed, windowRed, southDiffers, southNow, above, aboveRed);
        check("fixture: the town draws (" + u.getConsumption() + " kW) and the south's month differs from the north's in some months (" + southDiffers + ")", u.getConsumption() > 0 && southDiffers >= 6);
        check("NOW reads the dispatch's own month at the latitude exactly, at 45, -35 and NaN, in every month of the year", nowRed == 0);
        check("...which for -35 is not the north's month in the " + southDiffers + " months they differ (" + southNow + " read right)", southNow == southDiffers);
        check("a future date reads the highest season over its opening to its horizon, for " + WINDOWS.length + " windows, 3 latitudes and 12 months", windowRed == 0);
        check("a window holding the year's highest month reads above the present month's season (" + above + " cases, " + aboveRed + " not)", above >= 24 && aboveRed == 0);
    }

    // ------------------------------------------------------------------------------------------------------------------ 6

    static void money() {
        OUT.println("--- 6. the money audit stays exact over the months run ---");
        double worst = AUDIT.stream().mapToDouble(Math::abs).max().orElse(Double.NaN);
        OUT.printf("   %d months; the worst residual %.3g (a cent is %.3g)%n", AUDIT.size(), worst, MoneyAudit.CENT);
        check("every one of the " + AUDIT.size() + " months' money audits closes to the cent", AUDIT.size() >= LEAST_MONTHS && worst <= MoneyAudit.CENT);
    }

    public static void main(String[] args) {
        physics();
        ordered();
        held(true);
        held(false);
        leftAlone();
        spreadLeftAlone();
        season();
        money();
        OUT.println("PowerWiresCheck: " + fails + " failures");
        if (fails > 0) System.exit(1);
    }
}
