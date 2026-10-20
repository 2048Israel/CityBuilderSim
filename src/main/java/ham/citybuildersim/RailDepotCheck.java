package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Mining;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The railway's stations: the Rail Depot, and the rules that put a station
 * first and track only where it is needed (FR7, 2026-10-18; docs/queue/FR7.md
 * 1.1; sectors.Freight, THE STATIONS).
 *
 * WHY THIS HARNESS EXISTS. Before FR7 the Rail Spur, the Freight Line and the
 * Rail Terminal were one ladder of tonnes, capacity was the least of the
 * track and the wagon sets, and plan() bought the best profit per cost whose
 * tonnes the unserved freight filled to MIN_LINE_UTILISATION: a young city
 * bought spurs and lines, a terminal late, and track ran out into the
 * country to no yard. Jerus: "needs one terminal and then spurs". RailCheck
 * holds the railway's money; this holds who may be ordered, in what order, and
 * what the stations let through. What it has to prove:
 *
 *   1. THE RAIL DEPOT IS IN THE CATALOGUE on the ladder's per-tonne rule, a
 *      tenth of a terminal in everything else, and the new template fields
 *      (stationTonnes, siding) are on the buildings they belong to and no
 *      others.
 *   2. A YOUNG CITY'S FIRST RAIL ORDER IS A RAIL DEPOT, at its threshold and
 *      not below it, however many tonnes there are: never a terminal first.
 *   3. NO SPUR OR LINE BEFORE A STATION, whatever track the city already has.
 *   4. THE STATIONS CAP WHAT IS RAILED: goods only through a station, bulk
 *      past the stations on the spurs' sidings, and nothing at all with
 *      neither - first as the dealing's arithmetic, then in a played city's
 *      months.
 *   5. A SPUR ONLY FOR A WORKS WITHOUT A SIDING: the bulk-works predicate
 *      (counted and named), a spur ordered while a bulk works has none and
 *      the track and the sidings are short, none when every works has one or
 *      the city has no works at all.
 *   6. THE OVERBUILD: while the wagon sets limit the capacity, no track is
 *      ordered against track that already stands. The old plan() ordered a
 *      Rail Spur in this very fixture (measured before the rule changed; the
 *      old measure is recomputed here from the model's own figures).
 *   7. A TERMINAL FOLLOWS when the tonnes outgrow the depots, at its own
 *      threshold.
 *   8. THE LAST STATION IS NOT RETIRED while a spur or a line stands.
 *
 * The fixtures are bare cities (a catalogue, a planner, a Freight with nothing
 * else), because plan() asks for nothing a city adds except the staffing,
 * which a bare city passes, and because the month's tonnes must be what the
 * fixture says (Freight.setMonthForTest()). An assertion whose subject is a
 * played city's trade is an assertion about the trade; section 4 plays one
 * only for what the stations let through. Every threshold is caused from both
 * sides, and every figure comes from the templates and the model's constants.
 */
public class RailDepotCheck {

    static int fails = 0;

    /** What a lorry charges for a tonne in the model's money: BuildingManager's RAIL banner measures $319 a tonne in the played city, and the fixtures' freight is the planner's to value, not this harness's. */
    static final double LORRY_A_TONNE = .319;

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            @Override public void write(int b) { }
        }));
        try { r.run(); } finally { System.setOut(real); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-96s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static boolean near(double a, double b, double tol) {
        return Math.abs(a - b) <= tol * Math.max(1, Math.abs(b));
    }

    /** The tonnes the last haul dealt the railway: this month's bulk and goods offered at the shares it set for the month to come. */
    static double dealtNow(Freight fr) {
        double[] share = fr.getCarried();
        return share[Traffic.BULK.ordinal()] * fr.getBulkOffered()
                + share[Traffic.GOODS.ordinal()] * (fr.getOfferedTonnes() - fr.getBulkOffered());
    }

    /** What a decision says, in a few words. */
    static String say(BusinessInvestment.Decision d) {
        return d.build ? "orders a " + d.template.getName() : d.reason;
    }

    /* ================================================================
       THE FIXTURE: a bare city
       ================================================================ */

    /** A catalogue, a planner with all the ground it wants, and a Freight attached to them: no city, so the staffing test passes. */
    static final class Fx {
        final BuildingManager bm = new BuildingManager();
        final BusinessInvestment plans;
        final Freight fr = new Freight();

        Fx() {
            quietly(bm::initializeTemplates);
            plans = new BusinessInvestment(bm, new EconomyManager(bm));
            plans.setLandAvailable(1e12, 0);
            ((Sector) fr).attach(bm, new Markets());
        }

        BuildingsTemplate t(String name) { return bm.getTemplateByName(name); }

        /** n of the named building standing. */
        Fx stand(String name, int n) {
            if (n > 0) bm.addStack(t(name), n, true);
            return this;
        }

        /** n bulk works standing: iron mines. */
        Fx works(int n) {
            if (n > 0) bm.addStack(bm.getTemplate(Mining.IRON_MINE), n, true);
            return this;
        }

        /** A month's offer to the railway, the wagon sets enough for the track standing. */
        Fx offer(double bulk, double goods) {
            return offer(bulk, goods, fr.setsNeeded());
        }

        /** ...with this many wagon sets, however many the track needs. */
        Fx offer(double bulk, double goods, double sets) {
            fr.setPantry(Good.ROLLING_STOCK, sets);
            fr.setMonthForTest(bulk, goods, LORRY_A_TONNE);
            return this;
        }

        BusinessInvestment.Decision plan() { return fr.plan(plans, null); }
    }

    public static void main(String[] args) {

        Fx cat = new Fx();
        BuildingsTemplate depot = cat.t("Rail Depot"), spur = cat.t("Rail Spur"),
                line = cat.t("Freight Line"), terminal = cat.t("Rail Terminal");
        if (depot == null) {
            System.out.println("no Rail Depot in the catalogue");
            System.exit(1);
        }
        final double U = Freight.MIN_LINE_UTILISATION;
        final double depotNeeds = depot.getStationTonnes() * U;       // a Depot's threshold
        final double terminalNeeds = terminal.getStationTonnes() * U; // a Terminal's
        final double spurNeeds = spur.getRailCapacity() * U;          // a Spur's, track and siding alike
        final double lineNeeds = line.getRailCapacity() * U;          // a Line's

        /* ================================================================
           1. THE RAIL DEPOT IN THE CATALOGUE
           ================================================================ */

        System.out.println("--- the Rail Depot is in the catalogue, on the ladder, a tenth of a terminal ---");

        assertTrue("it is id 141, Freight's, a RAIL building",
                depot.getId() == 141 && Sectors.FREIGHT.equals(depot.getSector()) && depot.getCategory() == BuildingType.RAIL);
        report("...its own track is its connection to the main line: railCapacity equals what it sorts",
                depot.getRailCapacity() > 0 && depot.getRailCapacity() == depot.getStationTonnes(),
                String.format("%,.0f t of track, %,.0f t sorted", depot.getRailCapacity(), depot.getStationTonnes()));

        double perSpur = spur.getCashCost() / spur.getRailCapacity(), perLine = line.getCashCost() / line.getRailCapacity(),
                perTerminal = terminal.getCashCost() / terminal.getRailCapacity(), perDepot = depot.getCashCost() / depot.getRailCapacity();
        report("...its cash is on the ladder: a tonne no dearer than the spur's and no cheaper than the line's",
                perDepot <= perSpur + 1e-12 && perDepot >= perLine - 1e-12 && perDepot > perTerminal,
                String.format("%.3f a tonne against the spur's %.3f, the line's %.3f, the terminal's %.3f", perDepot, perSpur, perLine, perTerminal));

        double tenth = depot.getStationTonnes() / terminal.getStationTonnes();
        boolean postsOk = true;
        long depotPosts = 0, terminalPosts = 0;
        StringBuilder posts = new StringBuilder();
        for (JobType j : JobType.values()) {
            depotPosts += depot.getJobs(j);
            terminalPosts += terminal.getJobs(j);
            boolean ok = depot.getJobs(j) == Math.round(terminal.getJobs(j) * tenth);
            postsOk &= ok;
            if (terminal.getJobs(j) > 0) posts.append(String.format("%s %d of %d; ", j.name(), depot.getJobs(j), terminal.getJobs(j)));
        }
        report("...its posts, by type, are the terminal's x its share of the tonnes, rounded", postsOk && depotPosts > 0, posts.toString());
        boolean figures = depot.getConstructionPoints() == Math.round(terminal.getConstructionPoints() * tenth)
                && depot.getConstructionMaterials() == Math.round(terminal.getConstructionMaterials() * tenth)
                && depot.getElectricityConsumption() == Math.round(terminal.getElectricityConsumption() * tenth)
                && depot.getWaterConsumption() == Math.round(terminal.getWaterConsumption() * tenth)
                && depot.getLandSqFt() == Math.round(terminal.getLandSqFt() * tenth)
                && depot.getRoadLoad() == Math.round(terminal.getRoadLoad() * tenth);
        report("...and so are its construction points and materials, power, water, ground and road load", figures,
                String.format("%d points, %d materials, %d kW, %.0f water, %,.0f sq ft, %.0f road load", depot.getConstructionPoints(),
                        depot.getConstructionMaterials(), depot.getElectricityConsumption(), depot.getWaterConsumption(),
                        depot.getLandSqFt(), depot.getRoadLoad()));
        double road = depot.loadOf(Traffic.COMMUTERS) + depot.loadOf(Traffic.GOODS) + depot.loadOf(Traffic.BULK);
        assertTrue("...its three road loads still add to its one, to the bit", Double.compare(road, depot.getRoadLoad()) == 0);

        assertTrue("the Rail Terminal is a station of all it carries: stationTonnes equals its railCapacity",
                terminal.getStationTonnes() > 0 && terminal.getStationTonnes() == terminal.getRailCapacity());
        assertTrue("...and the Rail Spur and the Freight Line sort nothing",
                spur.getStationTonnes() == 0 && line.getStationTonnes() == 0);
        Set<String> stations = new TreeSet<>(), sidings = new TreeSet<>();
        int rails = 0;
        for (BuildingsTemplate t : cat.bm.getTemplates()) {
            if (t.isStation()) stations.add(t.getName());
            if (t.isSiding()) sidings.add(t.getName());
            if (t.getRailCapacity() > 0) rails++;
        }
        report("the stations are the Rail Depot and the Rail Terminal and no other building",
                stations.equals(new TreeSet<>(List.of(depot.getName(), terminal.getName()))), stations.toString());
        report("...the one siding is the Rail Spur's",
                sidings.equals(new TreeSet<>(List.of(spur.getName()))), sidings.toString());
        report("...and the catalogue has four rail buildings: the spur, the line, the terminal and the depot",
                rails == 4, rails + " templates with track");

        /* ================================================================
           2. A YOUNG CITY'S FIRST RAIL ORDER IS A RAIL DEPOT
           ================================================================ */

        System.out.println("\n--- a young city's first rail order is a Rail Depot, at its threshold and never a terminal ---");

        // Bulk works stand, so a spur would be wanted if a station did; no track and no station yet.
        Fx young = new Fx().works(3).offer(depotNeeds - 1, 0);
        BusinessInvestment.Decision below = young.plan();
        report("tonnes a month just below the Depot's threshold: nothing is ordered, and the reason names the station",
                !below.build && below.reason.contains("no station yet") && below.reason.contains(depot.getName()),
                String.format("%,.0f of %,.0f needed: %s", depotNeeds - 1, depotNeeds, say(below)));

        for (double tonnes : new double[] { depotNeeds, depotNeeds + 1, terminalNeeds, terminalNeeds + 1, 10 * terminal.getStationTonnes() }) {
            Fx city = new Fx().works(3).offer(tonnes, 0);
            BusinessInvestment.Decision d = city.plan();
            report(String.format("%,.0f t a month, no station: the order is a Rail Depot", tonnes),
                    d.build && d.template == city.t("Rail Depot") && d.quantity == 1, say(d));
        }
        Fx goodsOnly = new Fx().works(3).offer(0, depotNeeds + 1);
        BusinessInvestment.Decision g = goodsOnly.plan();
        report("goods past the Depot's threshold and no bulk: the same, a Rail Depot", g.build && g.template == goodsOnly.t("Rail Depot"), say(g));

        /* ================================================================
           3. NO SPUR OR LINE BEFORE A STATION
           ================================================================ */

        System.out.println("\n--- no spur or line before a station, whatever track the city has and whatever works stand ---");

        boolean never = true;
        StringBuilder seen = new StringBuilder();
        for (int standingLines : new int[] { 0, 1 }) {
            for (double tonnes : new double[] { depotNeeds - 1, depotNeeds + 1, lineNeeds + 1, terminalNeeds + 1, 4 * terminal.getStationTonnes() }) {
                Fx city = new Fx().works(5).stand("Freight Line", standingLines).offer(tonnes + line.getRailCapacity() * standingLines, 0);
                BusinessInvestment.Decision d = city.plan();
                boolean ok = !d.build || (d.template.isStation() && d.template == city.t("Rail Depot"));
                never &= ok;
                seen.append(String.format("%d line(s), %,.0f t: %s; ", standingLines, tonnes, say(d)));
            }
        }
        report("with five bulk works and a line standing or not, at any tonnes: a Rail Depot or nothing, never a spur or a line", never, seen.toString());

        Fx legacy = new Fx().works(5).stand("Freight Line", 2).stand("Rail Spur", 2);
        legacy.offer(10 * terminal.getStationTonnes(), 0);
        BusinessInvestment.Decision viaLegacy = legacy.plan();
        report("...and spurs and lines already standing (a city from before the stations) do not count as one: still the Rail Depot first",
                viaLegacy.build && viaLegacy.template == legacy.t("Rail Depot"), say(viaLegacy));

        /* ================================================================
           4. THE STATIONS CAP WHAT IS RAILED
           ================================================================ */

        System.out.println("\n--- the stations cap what is railed: goods through a station only, bulk past the stations on the sidings ---");

        final double S = depot.getStationTonnes(), P = spur.getRailCapacity(), AMPLE = 100 * S;
        double[] a = Freight.deal(AMPLE, S, 0, 3 * S, 3 * S);
        report("a depot, ample track and sets, bulk and goods past it: the bulk takes the station and the goods get none",
                a[0] == S && a[1] == 0, String.format("bulk %,.0f, goods %,.0f of the depot's %,.0f", a[0], a[1], S));
        double[] b = Freight.deal(AMPLE, S, 0, 0, 3 * S);
        report("...goods alone never rail more than the depot handles", b[1] == S && b[0] == 0, String.format("goods %,.0f", b[1]));
        double[] c = Freight.deal(AMPLE, S, 0, S / 2, 3 * S);
        report("...a bulk that leaves room shares the station: goods rail what it leaves, and together never past the depot",
                c[0] == S / 2 && c[1] == S / 2 && c[0] + c[1] <= S, String.format("bulk %,.0f + goods %,.0f", c[0], c[1]));
        double[] d2 = Freight.deal(AMPLE, S, 2 * P, 100 * S, 3 * S);
        report("...bulk past the stations rides the spurs' sidings: a depot and two spurs rail the stations' tonnes plus the sidings'",
                d2[0] == S + 2 * P && d2[1] == 0, String.format("bulk %,.0f = %,.0f + 2 x %,.0f", d2[0], S, P));
        double[] e = Freight.deal(AMPLE, S, P, P / 2, 3 * S);
        report("...bulk that fits on a siding leaves the station whole for the goods",
                e[0] == P / 2 && e[1] == S, String.format("bulk %,.0f on the siding, goods %,.0f at the depot", e[0], e[1]));
        double[] f = Freight.deal(AMPLE, 0, P, 3 * P, 3 * S);
        report("a spur and no station: bulk rails to the siding's tonnes and goods, which go only through a station, not at all",
                f[0] == P && f[1] == 0, String.format("bulk %,.0f, goods %,.0f", f[0], f[1]));
        double[] h = Freight.deal(S / 4, S, 0, 3 * S, 0);
        report("the sets or the track still limit it: a quarter of a depot's capacity rails a quarter of its tonnes",
                h[0] == S / 4, String.format("bulk %,.0f", h[0]));
        double[] none = Freight.deal(AMPLE, 0, 0, 3 * S, 3 * S);
        report("neither a station nor a spur: nothing rails, exactly zero (the band stays the lorries', to the bit)",
                Double.compare(none[0], 0.0) == 0 && Double.compare(none[1], 0.0) == 0 && Double.compare(none[0] / (3 * S), 0.0) == 0,
                String.format("%s, %s", none[0], none[1]));
        assertTrue("its capacity is the least of the track, the sets and the stations and sidings together",
                Freight.capacityOf(AMPLE, AMPLE / Freight.TONNES_PER_SET, S, P) == S + P
                        && Freight.capacityOf(S / 2, AMPLE / Freight.TONNES_PER_SET, S, P) == S / 2
                        && Freight.capacityOf(AMPLE, 1, S, P) == Freight.TONNES_PER_SET
                        && Freight.capacityOf(AMPLE, AMPLE / Freight.TONNES_PER_SET, 0, 0) == 0);

        // ---- and in a played city's months ----
        System.out.println("\n--- ...and in a played city: lines alone rail nothing, spurs rail bulk, a depot lets the rest through ---");
        Game city = HaulageCheck.played("raildepotcheck-city", 14);
        Freight fr = city.getSectors().freight();
        GoodsMarket ore = city.getMarkets().get(Good.IRON);
        quietly(() -> { HaulageCheck.lay(city, "Freight Line", 4); for (int i = 0; i < 3; i++) city.toggleNextMonth(); });
        double[] carried0 = fr.getCarried();
        report("four freight lines and no station or spur: capacity none, nothing hauled, the band the lorries' to the bit",
                fr.trackTonnes() > 0 && fr.getCapacityTonnes() == 0 && fr.getHauledTonnes() == 0
                        && carried0[Traffic.BULK.ordinal()] == 0 && carried0[Traffic.GOODS.ordinal()] == 0 && ore.getFreightFactor() == 1.0,
                String.format("%,.0f t of track, %,.0f t offered, %,.0f hauled, ore's freight factor %s", fr.trackTonnes(),
                        fr.getOfferedTonnes(), fr.getHauledTonnes(), ore.getFreightFactor()));

        /*
         * THE TONNES THE LAST HAUL DEALT THE RAILWAY, not the tonnes it hauled: what is hauled is the month's tonnes at last
         * month's shares, which can pass a capacity a rising trade has just outgrown. The shares in force are the dealing's own,
         * made of this month's offer, so offered x share is exactly what the capacity let through.
         */
        quietly(() -> { HaulageCheck.lay(city, "Rail Spur", 2); for (int i = 0; i < 3; i++) city.toggleNextMonth(); });
        double goodsOffered = fr.getOfferedTonnes() - fr.getBulkOffered();
        double dealtSpurs = dealtNow(fr);
        report("then two spurs, no station: the capacity is the sidings', and the bulk, which is more, rides them up to it",
                fr.getCapacityTonnes() == fr.sidingTonnes() && fr.sidingTonnes() == 2 * P && fr.getHauledTonnes() > 0
                        && fr.getBulkOffered() > fr.sidingTonnes() && near(dealtSpurs, fr.sidingTonnes(), 1e-9)
                        && fr.getCarried()[Traffic.BULK.ordinal()] > 0,
                String.format("%,.0f t offered (%,.0f of it bulk), %,.0f dealt of the sidings' %,.0f", fr.getOfferedTonnes(), fr.getBulkOffered(),
                        dealtSpurs, fr.getCapacityTonnes()));
        report("...and the goods, which go only through a station, are not railed at all (the fixture offers some)",
                goodsOffered > 0 && fr.getCarried()[Traffic.GOODS.ordinal()] == 0,
                String.format("%,.0f t of goods offered, goods share %s", goodsOffered, fr.getCarried()[Traffic.GOODS.ordinal()]));

        quietly(() -> { HaulageCheck.lay(city, "Rail Depot", 1); for (int i = 0; i < 3; i++) city.toggleNextMonth(); });
        double wagons = fr.fleet() * Freight.TONNES_PER_SET;
        double dealtDepot = dealtNow(fr);
        report("then a depot: the capacity is the stations' and the sidings' together, and the goods ride the station",
                wagons >= fr.stationTonnes() + fr.sidingTonnes() && fr.getCapacityTonnes() == fr.stationTonnes() + fr.sidingTonnes()
                        && fr.stationTonnes() == depot.getStationTonnes()
                        && near(dealtDepot, Math.min(fr.getCapacityTonnes(), fr.getOfferedTonnes()), 1e-9)
                        && fr.getCarried()[Traffic.GOODS.ordinal()] > 0,
                String.format("%,.0f t dealt of %,.0f offered and %,.0f capacity (%,.0f stations + %,.0f sidings), goods share %.3f", dealtDepot,
                        fr.getOfferedTonnes(), fr.getCapacityTonnes(), fr.stationTonnes(), fr.sidingTonnes(), fr.getCarried()[Traffic.GOODS.ordinal()]));

        /* ================================================================
           5. A SPUR ONLY FOR A WORKS WITHOUT A SIDING
           ================================================================ */

        System.out.println("\n--- the bulk works: which templates are one ---");

        List<String> in = new ArrayList<>(), shopsOut = new ArrayList<>();
        int worksTemplates = 0;
        for (BuildingsTemplate t : cat.bm.getTemplates()) {
            if (Freight.isBulkWorks(t)) {
                worksTemplates++;
                if (in.size() < 8) in.add(t.getName());
            }
        }
        BuildingsTemplate mine = cat.bm.getTemplate(Mining.IRON_MINE);
        BuildingsTemplate quarry = cat.t("Quarry"), pelletiser = cat.t("Pelletising Plant");
        report("mines, quarries and the plants that work bulk are in: " + worksTemplates + " templates",
                Freight.isBulkWorks(mine) && Freight.isBulkWorks(quarry) && Freight.isBulkWorks(pelletiser) && worksTemplates > 0,
                "e.g. " + in + " ... " + Freight.bulkTonnesOf(mine) + " t a month at an iron mine");
        for (String shop : new String[] { "Convenience Store", "Small Grocery Store", "Restaurant", "Diner", "House" }) {
            BuildingsTemplate t = cat.t(shop);
            if (t != null && !Freight.isBulkWorks(t)) shopsOut.add(shop);
        }
        report("...shops, restaurants and homes are out, though a shop 'uses' grain (a few tonnes, under a set's month of it)",
                shopsOut.size() == 5 && Freight.bulkTonnesOf(cat.t("Convenience Store")) > 0
                        && Freight.bulkTonnesOf(cat.t("Convenience Store")) < Freight.BULK_WORKS_MIN_TONNES, shopsOut.toString());
        assertTrue("...and Freight's own buildings are out: the railway is not a works to lay a siding into",
                !Freight.isBulkWorks(spur) && !Freight.isBulkWorks(terminal) && !Freight.isBulkWorks(cat.t("Truck Depot")));
        Fx counted = new Fx().works(3).stand("Convenience Store", 4);
        assertTrue("the city counts the bulk works standing: three mines, and the shops beside them add none",
                counted.fr.bulkWorks() == 3);

        System.out.println("\n--- a spur is ordered for a works without a siding, when the track and the sidings are short ---");

        // A terminal stands: it is the city's station, and 500,000 t of track. The tonnes past it are the spur's to win.
        final double track = terminal.getRailCapacity();
        Fx at = new Fx().stand("Rail Terminal", 1).works(3).offer(track + spurNeeds, 0);
        BusinessInvestment.Decision spurDecision = at.plan();
        Freight.Shortfalls gap = at.fr.shortfalls();
        report("a terminal stands, three works have no siding, the track is short by the spur's threshold: a Rail Spur is ordered",
                spurDecision.build && spurDecision.template == at.t("Rail Spur"), say(spurDecision));
        report("...though a Rail Depot is also wanted there (the stations are as short), and a depot beats a spur on profit per cost: the siding goes first, by kind",
                gap.station() >= depotNeeds && gap.track() >= spurNeeds && gap.siding() >= spurNeeds,
                String.format("short: track %,.0f, stations %,.0f, sidings %,.0f", gap.track(), gap.station(), gap.siding()));
        Fx under = new Fx().stand("Rail Terminal", 1).works(3).offer(track + spurNeeds - 1, 0);
        BusinessInvestment.Decision spurBelow = under.plan();
        report("the track short by a tonne under the spur's threshold: no spur (nor anything: the stations are short by as little)",
                !spurBelow.build, say(spurBelow));

        Fx each = new Fx().stand("Rail Terminal", 1).stand("Rail Spur", 3).works(3);
        each.offer(3 * track, 0);
        BusinessInvestment.Decision eachDecision = each.plan();
        report("every bulk works already has a spur (three and three), the tonnes far past what stands: no spur, though a station may be",
                !eachDecision.build || eachDecision.template != each.t("Rail Spur"), say(eachDecision));
        Fx fourth = new Fx().stand("Rail Terminal", 1).stand("Rail Spur", 3).works(4);
        fourth.offer(3 * track, 0);
        BusinessInvestment.Decision fourthDecision = fourth.plan();
        report("...and the fourth works, with the same tonnes: a Rail Spur",
                fourthDecision.build && fourthDecision.template == fourth.t("Rail Spur"), say(fourthDecision));

        Fx noWorks = new Fx().stand("Rail Terminal", 1);
        noWorks.offer(3 * track, 0);
        BusinessInvestment.Decision noWorksDecision = noWorks.plan();
        report("a city with no bulk works: no spur, with the same tonnes",
                !noWorksDecision.build || noWorksDecision.template != noWorks.t("Rail Spur"), say(noWorksDecision));
        Fx oneWorks = new Fx().stand("Rail Terminal", 1).works(1);
        oneWorks.offer(3 * track, 0);
        BusinessInvestment.Decision oneWorksDecision = oneWorks.plan();
        report("...and with one works: a Rail Spur",
                oneWorksDecision.build && oneWorksDecision.template == oneWorks.t("Rail Spur"), say(oneWorksDecision));

        // The reason, when the spur is the only thing the numbers allow and nothing else qualifies: every rung's own threshold unmet.
        Fx covered = new Fx().stand("Rail Terminal", 1).stand("Rail Spur", 1).works(1);
        double haveTrack = covered.fr.trackTonnes(), haveStations = covered.fr.stationTonnes();
        covered.offer(haveTrack + spurNeeds, 0);
        BusinessInvestment.Decision coveredDecision = covered.plan();
        report("a works with its siding and a terminal, tonnes past the track by a spur's threshold: no spur is laid for it (a second siding into one works)",
                !coveredDecision.build || coveredDecision.template != covered.t("Rail Spur"), say(coveredDecision) + String.format(" [stations %,.0f]", haveStations));

        /* ================================================================
           6. THE OVERBUILD
           ================================================================ */

        System.out.println("\n--- the overbuild: no track is ordered against track that already stands, while the sets limit the capacity ---");

        // A depot and four lines stand (850,000 t of track), three works; the railway has ONE wagon set, so the capacity is the set's 2,500 t.
        Fx lag = new Fx().stand("Rail Depot", 1).stand("Freight Line", 4).works(3);
        lag.offer(depot.getStationTonnes(), 0, 1);
        double oldUnserved = lag.fr.getOfferedTonnes() - lag.fr.getCapacityTonnes();
        report("the fixture: the track stands at many times the tonnes, the sets are the limit, and the old measure (tonnes less capacity) was past a spur's threshold",
                lag.fr.trackTonnes() >= 10 * lag.fr.getOfferedTonnes() && lag.fr.getCapacityTonnes() == Freight.TONNES_PER_SET
                        && oldUnserved >= spurNeeds,
                String.format("%,.0f t of track, %,.0f t offered, %,.0f t of capacity (one set): the old rule saw %,.0f t unserved against a spur's %,.0f",
                        lag.fr.trackTonnes(), lag.fr.getOfferedTonnes(), lag.fr.getCapacityTonnes(), oldUnserved, spurNeeds));
        BusinessInvestment.Decision lagged = lag.plan();
        report("...and the plan orders nothing: the track and the station already carry the tonnes, the sets are what lag",
                !lagged.build && lagged.reason.contains("wagon sets"), say(lagged));
        boolean neverTrack = true;
        StringBuilder sweep = new StringBuilder();
        for (double times : new double[] { .5, .9, 1, 1.2, 2, 5, 10, 16 }) {
            double tonnes = times * depot.getStationTonnes();
            if (tonnes > lag.fr.trackTonnes()) continue;
            Fx f2 = new Fx().stand("Rail Depot", 1).stand("Freight Line", 4).works(3);
            f2.offer(tonnes, 0, 1);
            BusinessInvestment.Decision d = f2.plan();
            boolean ok = !d.build || d.template.isStation();
            neverTrack &= ok && f2.fr.trackTonnes() >= tonnes;
            sweep.append(String.format("%,.0f t: %s; ", tonnes, say(d)));
        }
        report("from half the depot's tonnes to sixteen times it, all under the track: never a spur or a line, a station at most", neverTrack, sweep.toString());
        Fx past = new Fx().stand("Rail Depot", 1).stand("Freight Line", 4).works(3);
        past.offer(past.fr.trackTonnes() + spurNeeds, 0, 1);
        BusinessInvestment.Decision pastDecision = past.plan();
        report("...but past the track by a spur's threshold, the sets still lagging, track IS ordered (a spur): the guard is the track's, not the sets'",
                pastDecision.build && pastDecision.template == past.t("Rail Spur"), say(pastDecision));
        Fx justShort = new Fx().stand("Rail Depot", 1).stand("Freight Line", 4).works(3);
        justShort.offer(justShort.fr.trackTonnes() + spurNeeds - 1, 0, 1);
        BusinessInvestment.Decision justDecision = justShort.plan();
        report("...and a tonne under it: no track (the stations are what is short)",
                !justDecision.build || justDecision.template.isStation(), say(justDecision));
        Fx lineCity = new Fx().stand("Rail Depot", 1);
        lineCity.offer(depot.getStationTonnes() + line.getRailCapacity(), 0);
        BusinessInvestment.Decision lineDecision = lineCity.plan();
        report("a depot stands and the track is short by a whole line, the stations by too little for a terminal: a Freight Line",
                lineDecision.build && lineDecision.template == lineCity.t("Freight Line")
                        && lineCity.fr.shortfalls().station() < terminalNeeds, say(lineDecision));

        /* ================================================================
           7. A TERMINAL FOLLOWS WHEN THE TONNES OUTGROW THE DEPOTS
           ================================================================ */

        System.out.println("\n--- a terminal follows when the tonnes outgrow the depots, at its own threshold ---");

        Fx twoDepots = new Fx().stand("Rail Depot", 2);
        double held = twoDepots.fr.stationTonnes();
        twoDepots.offer(held + terminalNeeds - 1, 0);
        BusinessInvestment.Decision shortOfTerminal = twoDepots.plan();
        report("two depots stand, the stations short a tonne under the terminal's threshold: not a terminal",
                !shortOfTerminal.build || shortOfTerminal.template != twoDepots.t("Rail Terminal"), say(shortOfTerminal));
        double outgrown = held + 3 * terminal.getStationTonnes();
        Fx grown = new Fx().stand("Rail Depot", 2);
        grown.offer(outgrown, 0);
        BusinessInvestment.Decision terminalDecision = grown.plan();
        report("...and when the tonnes are three terminals past the depots: the order is a Rail Terminal",
                terminalDecision.build && terminalDecision.template == grown.t("Rail Terminal"), say(terminalDecision));

        /* ================================================================
           8. THE LAST STATION IS NOT RETIRED WHILE TRACK STANDS
           ================================================================ */

        System.out.println("\n--- the last station is not retired while a spur or a line stands ---");

        Fx lone = new Fx().stand("Rail Depot", 1);
        assertTrue("a lone depot with no other track may be sold", lone.fr.mayRetire(lone.t("Rail Depot")));
        Fx withSpur = new Fx().stand("Rail Depot", 1).stand("Rail Spur", 1);
        assertTrue("...with a spur standing it may not", !withSpur.fr.mayRetire(withSpur.t("Rail Depot")));
        Fx withLine = new Fx().stand("Rail Terminal", 1).stand("Freight Line", 1);
        assertTrue("...nor the only terminal with a line standing", !withLine.fr.mayRetire(withLine.t("Rail Terminal")));
        Fx twoStations = new Fx().stand("Rail Depot", 2).stand("Rail Spur", 1).stand("Freight Line", 1);
        assertTrue("...but with a second depot standing one of them may go", twoStations.fr.mayRetire(twoStations.t("Rail Depot")));
        Fx mixed = new Fx().stand("Rail Depot", 1).stand("Rail Terminal", 1).stand("Rail Spur", 1);
        assertTrue("...and a depot beside a terminal may go, though the terminal is the one left", mixed.fr.mayRetire(mixed.t("Rail Depot")));
        assertTrue("the track itself may still be sold, the station standing",
                withSpur.fr.mayRetire(withSpur.t("Rail Spur")) && withLine.fr.mayRetire(withLine.t("Freight Line")));
        assertTrue("a Truck Depot is still never retired by the railway's measure",
                !withSpur.fr.mayRetire(withSpur.t("Truck Depot")));

        if (fails > 0) {
            System.out.printf("%n%d check(s) failed%n", fails);
            System.exit(1);
        }
        System.out.println("\nAll checks passed.");
    }
}
